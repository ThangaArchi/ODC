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

public class ComponentOutputStream extends OutputStream implements Runnable {
   
   static Logger log = Logger.getLogger(ComponentOutputStream.class.getName());
   
   DboxFileInfo                     info = null;
   OutputStream                      cos = null;
   DboxFileComponent   current_component = null;
   boolean                        closed = false;
   static public boolean     doSizeCheck = true;
   static public boolean    makeThreaded = true;
   
   boolean                     deferDigest = false;
   MessageDigestI md5digest = null;
   
   public ComponentOutputStream(DboxFileInfo info, boolean deferDigestV) {
      this.info = info;
      
      try {
         deferDigest = deferDigestV;
         if (!deferDigest) {
            md5digest = info.calculateAndReturnMD5(info.getFileSize());
         }
      } catch(Exception ee) {
         log.error("Error calculating MD5");
         log.error(ee);
      }
   }
   
   public ComponentOutputStream(DboxFileInfo info) {
      this.info = info;
      
      try {
         deferDigest = false;
         md5digest = info.calculateAndReturnMD5(info.getFileSize());
      } catch(Exception ee) {
         log.error("Error calculating MD5");
         log.error(ee);
      }
   }
   
   
  // Needed to protect/thread the close
   DropboxFileMD5      outstandingDigest = null;
   String                 outstandingMD5 = null;
   OutputStream           outstandingCos = null;
   DboxFileComponent     outstandingComp = null;
   Thread              outstandingThread = null;
   Exception        outstandingException = null;
   public void run() {
      outstandingException = doStreamClose(outstandingCos, outstandingComp);
   }
   
   protected Exception doStreamClose(OutputStream lcos, 
                                     DboxFileComponent lcomp) {
      if (lcos != null) {
         try {
            long start = System.currentTimeMillis();
            lcos.flush();
            lcos.close();
            long end = System.currentTimeMillis();
            long delt = end - start;
            if (delt <= 0) delt = 1;
            log.info("Flush/Close size = " + lcomp.getFileSize() + 
                     " time = " + delt + " KBPS = " + 
                     (((lcomp.getFileSize()*1000)/delt)/1024));
         } catch(Exception eee) {
            log.error("Error Flushing/closing FileComponent: " + 
                      lcomp);
            return eee;
         }
      }  
      return null;
   }
   
   public String calculateMD5() {
	
	  String ret = null;
	  if (deferDigest) {
		 try {
			deferDigest = false;            
			md5digest = info.calculateAndReturnMD5(info.getFileSize());
		 } catch(Exception ee) {
			log.error("Error calculating MD5");
			log.error(ee);
		 }
	  }
	  if (md5digest != null) {
		 try {
         	           
			ret = md5digest.hashAsString();

		 } catch(Exception ee) {
		 }
	  } 
	  return ret;
   }
   
   
   protected void finishCompleteOperation(DboxFileComponent lcomp, 
                                          String md5, MessageDigestI md5digL) 
      throws DboxException, IOException {
            
      
     // Had to do this conditionally, as when in AFS, it causes SYNC to
     //  server out of cache, makes us wait WAY too long
      String err = null;
      if (doSizeCheck) {
         long fsize   = lcomp.getFileSize();
         String cname = lcomp.getFullPath();
         String fname = info.getFileName();
         File f = new File(lcomp.getFullPath());
         if (f.exists()) {
            if (f.length() != fsize) {
               err = "File size SHOULD be [" + fsize + 
                  "] for component [" + cname + "] and file [" + 
                  fname + "] but its [" + f.length() + "]";
               log.error(err);
               lcomp.setFileSize(f.length());
               DboxAlert.alert(DboxAlert.SEV2, 
                               "Incorrect File Size",
                               0, 
                               err);
            }
         } else if (lcomp.getFileSize() != 0) {
            err = "File size SHOULD be [" + fsize + 
               "] for component [" + cname + "] and file [" + 
               fname + "] but it does not exist!";
            log.error(err);
            lcomp.setFileSize(0);
         }
      }
      
     // if this is a zero len file comp ... delete it
      if (lcomp.getFileSize() == 0) {
         info.deleteComponent(lcomp.getStartingOffset());
         return;
      }
      
     //
      lcomp.setCompMD5State(md5digL);
      
      lcomp.complete();
      info.recalculateFileSize();
      
      log.info("Completed FileComponent" +
               "[" + lcomp.getFullPath() + 
               "] sz[" + lcomp.getFileSize() + 
               "] isz[" + lcomp.getFileIntendedSize() + 
               "] for file{" + info.getFileId() + 
               "}[" + 
               info.getFileName() + 
               "] sz[" + info.getFileSize() + 
               "] isz[" + info.getFileIntendedSize() + "] MD5[" + md5 + "]");
      
     // set MD5Object directly ... that will set MD5 string as well. We SHOULD be able
     //  to gak the MD5 in the FILE table, as the MD5BLOB serves the same purpose.
      if (err != null) {
         info.forceSetFileMD5Object(null);
         md5digest =null;
         throw new DboxException(err, 0);
      }      
      
      if (md5digL != null) {
         info.forceSetFileMD5Object(md5digL);
      }      
   }
      
  // This should be called ONLY by main writer thread
   protected void completeCurrentComponent(boolean closeit) 
      throws DboxException, IOException {
      
      Exception outstandingException = null;
      
      Debug.debugprint("ComponentOutputStream: completeCurrComp called");
      
     // Handle any outstanding stuff before we finish current stuff
      if (outstandingThread != null) {
      
         log.info("completeCurrentComp: Have threaded component stuff");
      
        // If we have an outstanding threaded close, finish that up here
        // We only allow 1 outstanding threaded close at a time. Could do 
        // more, but that complicates things and is probably not needed.
        
        // Wait for last thread to be done
         if (outstandingThread != Thread.currentThread()) {
           // wait for last closer to die
            while(true) try {
               log.info("completeCurrentComp: Waiting to join");
               outstandingThread.join();
               break;
            } catch(InterruptedException ie) {
            }
            
           // Ignore errors from close for now ... 
            outstandingException = null;
         }
         
         outstandingThread = null;
            
         try {
            finishCompleteOperation(outstandingComp, outstandingMD5, outstandingDigest);
            log.info("completeCurrentComp: threaded component complete");
         } catch(Exception ee) {
            log.warn("completeCurrentComp: Eee threaded component had errors");
            outstandingException = ee;
         }
      }
      
     // If the current component is being closed ... mark it so
      if (closeit) closed = true;
      
      if (current_component != null) {
         outstandingCos    = cos;
         outstandingComp   = current_component;
         outstandingMD5    = calculateMD5();
         outstandingDigest = new DropboxFileMD5();
         outstandingDigest.setMD5State(md5digest.getMD5State());
         cos               = null;
         current_component = null;
         
        // If we can thread this close 
         if (makeThreaded && outstandingComp.threadedCloseWillHelp() && 
             !closed      && outstandingException == null) {
            outstandingThread = new Thread(this);
            log.info("completeCurrentComp: Starting threaded component");
            outstandingThread.start();
         } else {
           // Don't care about close exceptions for now ... fix
           //  Exception currentException = 
            doStreamClose(outstandingCos, outstandingComp);
            finishCompleteOperation(outstandingComp, outstandingMD5, outstandingDigest);
            outstandingCos    = null;
            outstandingComp   = null;
            outstandingMD5    = null;
            outstandingDigest = null;
            log.info("completeCurrentComp: Non-threaded component complete");
         }
      }
      
      if (outstandingException != null) {
         if (outstandingException instanceof DboxException)
            throw (DboxException)outstandingException;
         if (outstandingException instanceof IOException)
            throw (IOException)outstandingException;
      }
   }
   
   public void close() throws IOException {
      if (!closed) {
         try {
            completeCurrentComponent(true);
         } catch (DboxException de) {
            throw new IOException("DboxException: " + de.getMessage());
         }
      }
   }
   
   public void finalize() {
      try {
         close();
      } catch(Exception e) {}
   }
   public void flush() throws IOException {
      if (cos != null) cos.flush();
   }
   
   public void write(byte[] b, int off, int len) throws IOException {
      
      if (closed) throw new IOException("Ostream Closed");
      
      try {
         if (current_component == null) {
            current_component = info.createComponent();
            cos = current_component.makeOutputStream();
            
            Debug.debugprint(
               "ComponentOutputStream: write: Create new component\n" + 
               info.toString());
         }
      
         long left    = current_component.getSizeDelta();
         while(left < len) {
         
            if (left > 0) {
               cos.write(b, off, (int)left);
               current_component.incrementSize((int)left);
            } else {
               log.warn("Odd, left = " + left + "\n" + 
                        info + "\n" + current_component);
               left = 0;
            }
            
            if (deferDigest) {
               try {
                  deferDigest = false;
                  md5digest = info.calculateAndReturnMD5(info.getFileSize());
               } catch(Exception ee) {
                  log.error("Error calculating MD5");
                  log.error(ee);
               }
            }
            
            if (md5digest != null) {
               md5digest.update(b, off, (int)left);
            }
        
            completeCurrentComponent(false);
            len -= left;
            off += left;
            current_component = info.createComponent();
            cos               = current_component.makeOutputStream();
            left              = current_component.getSizeDelta();
            if (left <= 0) {
               String err = 
                  "CompOutStream: Comp deltasize <= 0 for new component";
               log.error(err + "\n" + info + "\n" + current_component);
               throw new IOException(err);
            }
         }
         
         if (len > 0) {
            cos.write(b, off, len);
            
            if (deferDigest) {
               try {
                  deferDigest = false;
                  md5digest = info.calculateAndReturnMD5(info.getFileSize());
               } catch(Exception ee) {
                  log.error("Error calculating MD5");
                  log.error(ee);
               }
            }
            
            if (md5digest != null) {
               md5digest.update(b, off, len);
            }
            
            current_component.incrementSize(len);
            if (current_component.getSizeDelta() == 0) {
               completeCurrentComponent(false);
            }
         }
      } catch(DboxException dbx) {
         throw new IOException("DboxException: " + dbx.getMessage());
      }
   }
      
      public void write(byte[] b) throws IOException {
      write(b, 0, b.length);
   }
   
  // YUK!! ... but hey, you shouldn't use this method!
   public void write(int b)  throws IOException {
      byte arr[] = new byte[1];
      arr[0] = (byte)(b&0xff);
      write(arr, 0, 1);
   }
}

