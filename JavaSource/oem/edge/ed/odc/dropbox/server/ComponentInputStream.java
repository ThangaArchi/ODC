package oem.edge.ed.odc.dropbox.server;

import  oem.edge.ed.odc.dropbox.common.*;
import  oem.edge.ed.odc.ftp.common.*;
import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.dsmp.server.*;
import  oem.edge.ed.odc.util.*;

import java.lang.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;

import javax.activation.DataSource;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

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

public class ComponentInputStream extends InputStream implements DataSource {
   DboxFileInfo                   info = null;
   RandomAccessFile                cis = null;
   DboxFileComponent current_component = null;
   boolean                      closed = false;
   long                            ofs = 0;
   long                      totremain = 0;
   
   boolean                   listersHandled = false;
   Vector                    listeners = new Vector();
   
   public ComponentInputStream(DboxFileInfo info) {
      this(info, 0);
   }
   
   public ComponentInputStream(DboxFileInfo info, long startofs) {
      this(info, startofs, info.getFileSize()-startofs);
   }
   
   public ComponentInputStream(DboxFileInfo info, long startofs, long len) {
      this.info = info;
      ofs       = startofs;
      totremain = len;
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
      return info.getFileName();
   }
   public OutputStream getOutputStream() throws IOException {
      throw new IOException("InputStream use only");
   }
   
  /* Find out how many bytes to go */
   public long getBytesRemaining() { return totremain; }
   public long getCurrentOffset () { return ofs; }
   
  /* Support listeners */
   public void addCompletionListener(ActionListener list) { 
      listeners.add(list);
   }
   
   public int available() throws IOException { 
      if (closed) throw new IOException("Ostream Closed");
      return 0;
   }
   public void close() throws IOException { 
      if (!closed) {
         closed = true;
         if (cis != null) {
            RandomAccessFile tcis = cis;
            cis = null;
            tcis.close();
         }
         
         sendEvent(new ActionEvent(this, DropboxGenerator.STATUS_NONE, "close"));
      }
   }
   
   protected void sendEvent(ActionEvent ev) {
     // If there are any registered listeners, call them now
      Iterator it = listeners.iterator();
      while(it.hasNext()) {
         ((ActionListener)it.next()).actionPerformed(ev);
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
      
     // If no more bytes, close up shop
      if (totremain <= 0) {
         if (cis != null) {
            try {
               cis.close();
            } catch(IOException ee) {}
            cis = null;
         }
         
         sendEvent(new ActionEvent(this, DropboxGenerator.STATUS_COMPLETE, "complete"));
         
         return -1;
      }
      
      if (cis == null) {
         try {
            
            Debug.debugprint("ComponentInputStream: read: loading component " + ofs);
            
            current_component = info.getComponentContainingOffset(ofs);
            if (current_component == null) {
               throw new IOException("No component attached to offset: " + ofs);
            }
            
            cis = current_component.getDownloader(ofs);
            Debug.debugprint("Loaded!\n" + current_component.toString());
            
         } catch(DboxException ex) {
            close();
            return -1;
         }
      }
      
      if (cis == null) {
         close();
         return -1;
      }
      
      if (len > totremain) len = (int)totremain;
      
      int r = cis.read(b, off, len);
      if (r == -1) {
         try {
            cis.close();
         } catch(IOException ee) {}
         cis = null;
         
        // We should NOT have had a read error here!
         if ((current_component.getStartingOffset() +
              current_component.getFileSize()) > ofs) {
            DboxAlert.alert(DboxAlert.SEV2, 
                            "Component filesize is wrong!",
                            0, 
                            "Component file size does not match expectation:\n"
                            + current_component.toString() + "\n\n" +
                            info.toString());
            throw new IOException("Component filesize is wrong!");
         }
         
         return read(b, off, len);
      } else {
         totremain -= r;
         ofs += r;
         if (totremain == 0) {
            sendEvent(new ActionEvent(this, 
                                      DropboxGenerator.STATUS_COMPLETE, "complete"));
         }
      }
      
      
      return r;
   }
   
   public void reset() throws IOException { 
      throw new IOException("Dude, I said mark was not supported!");
   }
   
   public long skip(long n) throws IOException { 
      if (closed) throw new IOException("Ostream Closed");
      
      if (n <= 0) return 0;
      
      if (n > totremain) {
         n = totremain;
      }
      
      ofs       += n;
      totremain -= n;
      
     // Close up current component, let read code get correct new offset
      if (cis != null) {
         try {
            cis.close();
         } catch(IOException ee) {}
         cis = null;
      }
      
      if (totremain == 0) {
         sendEvent(new ActionEvent(this, DropboxGenerator.STATUS_COMPLETE, "complete"));
      }
      
      return n;
   }
   
   protected void finalize() throws Throwable {
      try {
         close();
      } catch(Exception ee) {}
   }
}
