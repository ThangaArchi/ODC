package oem.edge.ed.odc.dropbox.client.soa;

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

public class PackageDownloadOperation extends Operation
   implements ActionListener {

   public static final long MAX_BUFFER_SIZE = 2 * 1024 * 1024;

   protected OutputStream outstrm = null;
   protected String encoding = null;
   
   protected boolean downloadComplete = false;
   
   public PackageDownloadOperation(DropboxAccess srv, ConnectionFactory fac, 
                                   OutputStream os, long pid, String encoding) {
      super(srv, fac, pid, -1);
      outstrm = os;
      this.encoding = encoding;
   }
      
  /**
   * Start up the thread to read the inpstrm. Only a single worker pls.
   */
   public boolean process() throws DboxException {
      synchronized(threads) {
         
        // If there is ANY culled data, can't do the upload
         try {
            PackageInfo pinfo = srv.queryPackage(packid, false);
            if (pinfo.getPackageStatus() != DropboxGenerator.STATUS_COMPLETE) {
               throw new DboxException("Can't download package until committed");
            }
         } catch(java.rmi.RemoteException re) {
            throw new DboxException("Remote Exception received", re);
         }
         
        // Set bytes to download to entire size registered
         setTotalSize(0x7FFFFFFFFFFFFFFFL);
         setToXfer(0x7FFFFFFFFFFFFFFFL);
            
        // Let super class start up thread
         setNumberOfWorkers(1);
         super.process();
      }
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
               System.out.println("Error while querying status of package " + packid);
               return false;
            } finally {
              //System.out.println("validate complete");
            }
         }
      }
      return getStatus() == STATUS_FINISHED;
   }
         
  // gets called with num bytes xfered as ID
   public void actionPerformed(ActionEvent ev) {
      transferUpdate(ev.getID());
   }
         
   public void run() {
      
     //System.out.println("In run for thread: " + toString());
         
      try {
      
         java.security.MessageDigest digest;
         digest = java.security.MessageDigest.getInstance("MD5");
            
         DataDownloadTransfer ddt = new DataDownloadTransfer(srv, factory, 
                                                             packid, encoding,
                                                             outstrm);
            
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
         
      } catch(Exception e) {
         System.out.println("Error while processing thread: " + toString());
         e.printStackTrace(System.out);
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
