package oem.edge.ed.odc.dropbox.server;

import  oem.edge.ed.odc.dropbox.common.*;
import  oem.edge.ed.odc.ftp.common.*;
import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.tunnel.common.*;
import  oem.edge.ed.odc.dsmp.server.*;
import  oem.edge.ed.odc.util.*;

import java.lang.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;

import javax.activation.DataSource;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import org.apache.tools.tar.*;
import org.apache.log4j.Logger;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 2003-2006                                    */ 
/*                                                                       */ 
/*     All Rights Reserved                                               */ 
/*     US Government Users Restricted Rights                             */ 
/*                                                                       */ 
/*   The source code for this program is not published or otherwise      */ 
/*   divested of its trade secrets, irrespective of what has been        */ 
/*   deposited with the US Copyright Office.                             */ 
/*   ------------------------------------------------------------------  */
/*     Please do not remove any of these commented lines  21 lines       */
/*   ------------------------------------------------------------------  */
/*                       Copyright Footer Check                          */
/*   ------------------------------------------------------------------  */ 

// Can stream in a single file, or ALL files in a package encoded as requested
public class PackageInputStream extends InputStream implements DataSource {

   protected static Logger log = Logger.getLogger(PackageInputStream.class.getName());
   
   boolean                   listersHandled = false;
   Vector                    listeners = new Vector();
      
   DboxPackageInfo               pinfo = null;
   boolean                      closed = false;
   String                     encoding = null;
  //Operation                 operation = null;
   
   ICAInputStream                icais = null;
   ICAOutputStream               icaos = null;
   FilterOutputStream           filtos = null;
   GZIPOutputStream             gzipos = null;
   DataDriver               datadriver = null;
   
   boolean           gettingTotalBytes = false;
   
   int                    encodingType = ENCODING_INVALID;
   
   IOException                piserror = null;
   
   long                     totwritten = 0; 
   long                      starttime = System.currentTimeMillis();
   long                        endtime = 0;
   
  // Only valid if completed successfully
   public long getTotalBytesWritten() { return totwritten; }
   public long getElapsedTime()       { 
      if (endtime < starttime) return 0;
      return endtime-starttime; 
   }
   
   final static int ENCODING_INVALID   = 0;
   final static int ENCODING_ZIP       = 1;
   final static int ENCODING_TGZ       = 2;
   final static int ENCODING_TAR       = 3;
   
  // This class drives the data movement thru the tar/zip/gzip filters 
   class DataDriver extends Thread {
   
      boolean done = false;
      public void end() { 
         done = true;
      }
      
      public void run() {
         
         DboxFileInfo                   info = null;
         Vector                     allfiles = null;
         Vector                     allcomps = null;
         DboxFileComponent current_component = null;
         
         long    pack_tot = 0;
         long curfile_tot = 0;
         long curcomp_tot = 0;
         
         InputStream cis = null;
         
         try {
         
            allfiles = pinfo.getFiles();
            byte buf[] = new byte[16*1024];
            
            while(!done) {
               
              // If we have no input stream ... get one.
               if (current_component == null) {
               
                  curfile_tot += curcomp_tot;
                     pack_tot += curcomp_tot;
               
                 // If we have a component left ... get it
                  if (allcomps != null && allcomps.size() > 0) {
                  
                     current_component = 
                        (DboxFileComponent)allcomps.elementAt(0);
                     allcomps.removeElementAt(0);
                     curcomp_tot = 0;
                     
                     cis = current_component.makeInputStream();
                     
                     continue;
                     
                  } else {
                  
                    // Check that we sent the appropriate amnt for the file
                     if (info != null) {
                        if (curfile_tot != info.getFileSize()) {
                           piserror = new IOException(
                              "Filesize read != expected: " + curfile_tot + 
                              "\n" + info.toString());
                           throw piserror;
                        }
                        
                       // If there are per file settings to make
                        switch(encodingType) {
                           case ENCODING_ZIP:
                              ((ZipOutputStream)filtos).closeEntry();
                              break;
                           case ENCODING_TAR:
                           case ENCODING_TGZ:
                              ((TarOutputStream)filtos).closeEntry();
                              break;
                        }
                     }
                  }
                  
                 // k, we know we have no components in play, get a new file
                  info = null;
                  allcomps = null;
                  
                 // If we have no files left
                  if (allfiles.size() == 0) {
                  
                    // If we did NOT send the appropriate amount ... complain
                     if (pack_tot != pinfo.getPackageSize()) {
                        piserror = new IOException(
                           "Packagesize read != expected: " + pack_tot + 
                           "\n" + pinfo.toString());
                        throw piserror;
                     }
                     
                     
                    // We are Done!
                     filtos.flush();
                     
                    // We have to have a switch here so when the ICAInputStream
                    //  gets the close, the PIS does not prematurely complain.
                    // Was using the Finish meth on our Filtered Stream, but
                    // they don't do what we want (no flush!!! need to close
                    // to get full count)
                     gettingTotalBytes = true;
                     
                     filtos.close();
                     
                    // Tell operation the real resultant size 
                    //operation.setToXfer(icais.getTotalQueued());
                    
                    // Now operation will get his info from the listener chain
                     totwritten = icais.getTotalQueued();
                     
                    // gettingTotalBytes set to false again in finally clause
                     
                     break;
                  }
                  
                  info = (DboxFileInfo)allfiles.elementAt(0);
                  allfiles.removeElementAt(0);
                  allcomps = info.getComponents();
                  curfile_tot = 0;
                  curcomp_tot = 0;
                  
                 // Do any per-file setup required by encoding
                  switch(encodingType) {
                     case ENCODING_ZIP:
                        ZipEntry zipentry = new ZipEntry(info.getFileName());
                        zipentry.setTime(info.getFileCreation());
                        ((ZipOutputStream)filtos).putNextEntry(zipentry);
                        break;
                     case ENCODING_TAR:
                     case ENCODING_TGZ:
                        TarEntry tarentry = new TarEntry(info.getFileName());
                        tarentry.setModTime(info.getFileCreation());
                        tarentry.setIds(1,1);
                        tarentry.setSize(info.getFileSize());
                        ((TarOutputStream)filtos).putNextEntry(tarentry);
                        break;
                  }
                  
                  continue;
               }
               
               int r = cis.read(buf);
               if (r == -1) {
                  if (curcomp_tot != current_component.getFileSize()) {
                     piserror = new IOException(
                        "Component read != expected: " + curcomp_tot + 
                        "\n" + current_component.toString());
                     throw piserror;
                  }
                  
                 // Show we need a new component
                  current_component = null;
                  cis.close();
                  cis = null;
                  
               } else if (r > 0) {
                  curcomp_tot += r;
                  filtos.write(buf, 0, r);
               }
            }
         } catch(Exception ee) {
            if (!(ee instanceof IOException)) {
               piserror = new IOException("Error during FilterStream: " +
                                          ee.getMessage() + ": " + 
                                          ee.getClass().getName());
            } else {
               piserror = (IOException)ee;
            }
            
            log.error("Error while encoding/streaming data: " +
               pinfo.toString() + "\n" + 
                      ((info != null)?info.toString():"null") + "\n" +
                      ((current_component != null)?
                       current_component.toString():"null"));
            log.error(ee);
            
         } finally {
         
            endtime    = System.currentTimeMillis();
            gettingTotalBytes = false;
            
            if (cis    != null) try { cis.close();    } catch(Exception eee) {}
            if (filtos != null) try { filtos.close(); } catch(Exception eee) {}
         }
      }
   }
   
   public PackageInputStream(DboxPackageInfo pinfo, //Operation op,
                             String encoding) throws DboxException {
                             
      this.pinfo     = pinfo;
     //this.operation = op;
      this.encoding  = encoding;
      
      if (encoding != null) {
         if        (encoding.equals("tgz") || 
                    encoding.equals("tar.gz")) {
                    
            encodingType = ENCODING_TAR;
            icais        = new ICAInputStream();
            icaos        = new ICAOutputStream(icais);
            
            try {
               gzipos       = new GZIPOutputStream(icaos);
            } catch(IOException ioe) {
               log.error("Error instantiating GZIP filter");
               log.error(ioe);
               throw new DboxException("Error instantiating GZIP filter", 0);
            }
            
            filtos       = new TarOutputStream(gzipos);
            icaos.setName("PackageIS-tgz");
            datadriver   = new DataDriver();
         } else if (encoding.equals("tar")) {
            encodingType = ENCODING_TAR;
            icais        = new ICAInputStream();
            icaos        = new ICAOutputStream(icais);
            filtos       = new TarOutputStream(icaos);
            icaos.setName("PackageIS-tar");
            datadriver   = new DataDriver();
         } else if (encoding.equals("zip")) {
            encodingType = ENCODING_ZIP;
            icais        = new ICAInputStream();
            icaos        = new ICAOutputStream(icais);
            filtos       = new ZipOutputStream(icaos);
            icaos.setName("PackageIS-zip");
            datadriver   = new DataDriver();
         }
      }
      
      if (encodingType == ENCODING_INVALID) {
         log.error("Invalid encoding specified: " + encoding);
         piserror = new IOException("Invalid encoding specified: " + encoding);
         throw new DboxException("Invalid encoding: " + encoding, 0);
      }
      
      datadriver.start();
   }
   
  /* Data source methods. We really *SHOULD* support getInputStream returning
  **  a new stream each time ... but I'm the only user, so forget it.
  */
   public String getContentType() { 
      return "application/binary";
   }
   public InputStream getInputStream()  throws IOException  { 
      return this; 
   }
   public String getName() {
      return pinfo.getPackageName();
   }
   public OutputStream getOutputStream() throws IOException {
      throw new IOException("InputStream use only");
   }
   
  /* Support listeners */
   public void addCompletionListener(ActionListener list) { 
      listeners.add(list);
   }
   
   protected void sendEvent(ActionEvent ev) {
     // If there are any registered listeners, call them now
      Iterator it = listeners.iterator();
      while(it.hasNext()) {
         ((ActionListener)it.next()).actionPerformed(ev);
      }
   }
   
   public int available() throws IOException { 
      if (closed) throw new IOException("Ostream Closed");
      if (piserror != null) throw piserror;
      if (icais == null) {
         throw new IOException("PackageInputStream: icais NULL!!");
      }
      return icais.available();
   }
   public void close() throws IOException { 
      if (!closed) {
         closed = true;
         
        // Stop driving data in
         if (datadriver != null) datadriver.end();
         
        // CLOSE this BEFORE closing filtos ... otherwise, we could block on
        //  flush of the filtos to this is via the os write method 
        // (hey, its happened ;-).  
        //
        // The thought here is that if someone calls close on an input stream,
        //  its all over anyway, so we really don't need the data that is
        //  going to be flushed from the filtos anyway.
        
         try {
            if (icais != null) {icais.close();}
         } catch(Exception ee) {
         }
         
        // Close the output filter stream
         try {
            if (filtos != null) {filtos.close();}
         } catch(Exception ee) {
           //log.error("PackageInputStream.close: Exception closing filtstrm: "
           //           + DboxAlert.getStackTrace(ee));
         }         
         
         sendEvent(new ActionEvent(this, DropboxGenerator.STATUS_NONE, "close"));
      }
   }
   public void mark(int readlimit) {
      ;
   }
   public boolean markSupported() {
      return false;
   }
   public int read() throws IOException { 
      byte arr[] = new byte[1];
      int r = read(arr, 0, 1);
      if (r == 1) r = (int)arr[0];
      return r;
   }
   public int read(byte[] b) throws IOException { 
      return read(b, 0, b.length);
   }
   public int read(byte[] b, int off, int len) throws IOException { 
   
      if (closed) throw new IOException("Ostream Closed");
      
      if (piserror != null) throw piserror;
      
      if (icais == null) {
         close();
         return -1;
      }
      
      int r = icais.read(b, off, len);
      if (r == -1) {
      
        // Wait for the bool to get set to false ... Needed to ensure that
        //  the size is correctly set before we return (maybe ?)
         while(gettingTotalBytes) {
            log.info("Yielding ... wait for totbytes");
            Thread.yield();
         }
            
         if (piserror != null) {
            sendEvent(new ActionEvent(this, DropboxGenerator.STATUS_FAIL, "fail"));
            throw piserror;
         }
         
         sendEvent(new ActionEvent(this, DropboxGenerator.STATUS_COMPLETE, "complete"));
      }
      
      return r;
   }
   
   protected void finalize() {
      try { close(); } catch(Exception ee) {}
   }
   
   public void reset() throws IOException { 
      throw new IOException("Dude, I said mark was not supported!");
   }
   
   public long skip(long n) throws IOException { 
      if (closed) throw new IOException("Ostream Closed");
      
      if (piserror != null) throw piserror;
      
      if (n <= 0) return 0;
      
      byte lbuf[] = new byte[16*1024];
      long totr = 0;
      while(n > 0) {
         int r = lbuf.length;
         if (r > n) r = (int)n;
         r = read(lbuf, 0, r);
         if (r == -1) break;
         n -= r;
         totr += r;
      }
      return totr;
   }
}
