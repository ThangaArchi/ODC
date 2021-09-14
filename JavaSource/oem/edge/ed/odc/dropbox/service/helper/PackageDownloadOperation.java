package oem.edge.ed.odc.dropbox.service.helper;

import java.util.*;
import java.io.*;
import java.net.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import oem.edge.ed.odc.dropbox.service.DropboxAccess;
import oem.edge.ed.odc.dropbox.common.*;
import oem.edge.ed.odc.dsmp.common.*;
import oem.edge.ed.odc.util.*;
import oem.edge.ed.util.*;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 2006                                         */ 
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

/**
 * Manages the downloading of a package from the Dropbox. This Operation is of
 *  the streaming type, and thus cannot provide restarts nor can it support 
 *  multi-channel transfers. The entire package is downloaded, and written to the
 *  provided stream.
 */
public class PackageDownloadOperation extends Operation {

   public static final long MAX_BUFFER_SIZE = 2 * 1024 * 1024;

   protected OutputStream outstrm = null;
   protected String encoding = null;
   
   protected boolean downloadComplete = false;
   
  /**
   * Create the operation to download the package using the specified encoding,
   * and write it to the provided output stream. The
   * encodings supported by the dropbox are 
   * <ul><li>tar</li><li>tgz</li><li>zip</li></ul>
   * @param srv      DropboxAccess proxy used to communicate with Dropbox session
   * @param os       OutputStream used to save downloaded package data
   * @param pid      Package id for target package to download
   * @param encoding Describes how the dropbox should encode the data prior to transfer
   */
   public PackageDownloadOperation(DropboxAccess srv, OutputStream os, long pid, 
                                   String encoding) {
      super(srv, pid, -1);
      outstrm = os;
      this.encoding = encoding;
   }
      
   public boolean process() throws DboxException {
      synchronized(threads) {
         
        // If there is ANY culled data, can't do the upload
         try {
            PackageInfo pinfo = srv.queryPackage(packid, false);
            if (pinfo.getPackageStatus() != DropboxGenerator.STATUS_COMPLETE) {
               throw new DboxException("Can't download package until committed");
            }
           // This is approximate size ... could be larger or much smaller.
           // We don't use these stats to determine download, so just make it
           //  approx right for outside observer
            setTotalSize(pinfo.getPackageSize());
            setToXfer(pinfo.getPackageSize());
         } catch(java.rmi.RemoteException re) {
            throw new DboxException("Remote Exception received", re);
         }
         
        // Let super class start up thread. Limit to one
         if (getNumberOfWorkers() > 1) setNumberOfWorkers(1);
      }
      
     // Process OUTSIDE of threads synchronization
      super.process();
      
      return true;
   }
      
   public boolean validate() {
      synchronized (threads) {
         if (threads.size() == 0) {
            try {
              //System.out.println("Doing validate");
               if (getStatus() < STATUS_TERMINATED) {
               
                 // TODOTODO - Think more about PackageDownload checking
                 // DownloadComplete flag will be set if we think it worked
                 // Unfort, we don't know for sure if the server REALLY gave
                 //  us all the data ... unless we get an IOException on the
                 //  read of the data ... and no real way to check in this
                 //  stateless world.   Best we can do for now.
                  if (downloadComplete) {
                     setStatus(STATUS_FINISHED);
                  } else {
                     setStatus(STATUS_TERMINATED);
                  }
                  transferEnded();
               }
            } catch(Exception e) {
               if (getVerbose()) {
                  System.out.println("Error while querying status of package " + packid);
               }
               return false;
            } finally {
              //System.out.println("validate complete");
            }
         }
      }
      return getStatus() == STATUS_FINISHED;
   }
         
   public void run() {
      
     //System.out.println("In run for thread: " + toString());
         
      try {
      
         java.security.MessageDigest digest;
         digest = java.security.MessageDigest.getInstance("MD5");
            
         DataDownloadTransfer ddt = new DataDownloadTransfer(srv, packid, 
                                                             encoding, outstrm);
         ddt.setMaxBufferSize(getMaxBufferSize());
            
         ddt.addProgressListener(this);
         
        // This is a one shot. Not good way to know if it works either
         long sent = ddt.doTransfer(0, 0x7FFFFFFFFFFFFFFFL);
         
        // Assuming it worked, we have everything, let the stats engine show
        //  the correct amount now
         setTotXfered(sent);
         setToXfer(sent);
         setTotalSize(sent);
         
         transferUpdate(0);
         
         downloadComplete = true;
         
      } catch(AbortTransferException e) {
        // Do nothing for Abort exception
      } catch(Exception e) {
         addError(e);
         if (getVerbose()) {
            System.out.println("Error while processing thread: " + toString());
            e.printStackTrace(System.out);
         }
      } finally {
         if (outstrm != null) try {outstrm.close();} catch(Exception ee) {}
         
        //System.out.println("In finally for thread: " + toString());
         synchronized(threads) {
            threads.remove(Thread.currentThread());
            if (threads.size() == 0) {
               endTimedOperation();
               validate();
            }
            threads.notifyAll();
         }
      }
   }   
}
