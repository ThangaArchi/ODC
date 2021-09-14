package oem.edge.ed.odc.dropbox.testcases;

import oem.edge.ed.odc.dropbox.service.helper.*;
import oem.edge.ed.odc.dropbox.common.*;
import oem.edge.ed.odc.dsmp.common.*;
import oem.edge.ed.odc.dropbox.service.*;
import oem.edge.ed.util.SearchEtc;
import oem.edge.ed.odc.util.ProxyDebugInterface;
import java.util.*;
import java.io.*;

public class Stresstest implements Runnable, OperationListener {
   
   
   String topURL  = null;
   String machine = "edesign4.fishkill.ibm.com";
   String proto   = "http";
   String context = "technologyconnect/odc";
   
   Vector sendusers   = new Vector();
   Vector uploadfiles = new Vector();
   String loginuser   = null;
   String loginpw     = null;
   
   int numworkers     = 1;
   int numapps        = 10;
   
   int lev = 0;
   
   boolean dodirect   = false;
   boolean useSoap    = false;
   
   boolean clean      = false;

   
   public void log(String s) {
      System.out.println(new Date().toString() + " " + 
                         Thread.currentThread().getName() + ": " + s);
   }
   
   public void usage() {
      System.out.println("Stresstest: Usage\n" +
                         "\t-machine    machname<:port>\n" +
                         "\t-proto      protocol\n" +
                         "\t-context    context\n" +
                         "\t-direct\n" +
                         "\t-soap\n" +
                         "\t-hessian\n" +
                         "\t-context    context\n" +
                         "\t-numapps num\n" +
                         "\t-numworkers num\n" +
                         "\t-topurl     full URL including context\n" +
                         "\t-upload     fullpathtofile\n" +
                         "\t-clean      delete all previous Stress packages on startup\n" +
                         "\t-loginuser  userid password\n" +
                         "\t-senduser   userid\n\n");
                         
      System.exit(2);
   }
   
   public static void main(String args[]) {
      new Stresstest().domain(args);
   }
   
   public void operationUpdate(OperationEvent e) {
      if (e.isEnded()) {
         Operation op = e.getOperation();
         log("Transferred " + op.getTotalXfered() + 
             " (" + op.percentDone() + ") Rate: " + (op.getXferRate()/1024) + "KB/s");
      }
   }
   
   public void domain(String args[]) {
      for(int i=0; i < args.length; i++) {
         if        (args[i].equals("-machine")) {
            machine = args[++i];
         } else if (args[i].equals("-proto")) {
            proto   = args[++i];
         } else if (args[i].equals("-clean")) {
            clean   = true;
         } else if (args[i].equals("-context")) {
            context = args[++i];
         } else if (args[i].equals("-topurl")) {
            topURL  = args[++i];
         } else if (args[i].equals("-direct")) {
            dodirect = true;
            useSoap  = false;
         } else if (args[i].equals("-soap")) {
            dodirect = false;
            useSoap  = true;
         } else if (args[i].equals("-hessian")) {
            dodirect = false;
            useSoap  = false;
         } else if (args[i].equals("-numworkers")) {
            try {
               numworkers = Integer.parseInt(args[++i]);
            } catch(Exception ee) {
               System.out.println("Error parsing NumWorkers value. Must be Integer");
               System.exit(1);
            }
         } else if (args[i].equals("-numapps")) {
            try {
               numapps = Integer.parseInt(args[++i]);
            } catch(Exception ee) {
               System.out.println("Error parsing NumApps value. Must be Integer");
               System.exit(1);
            }
         } else if (args[i].equals("-loginuser")) {
            loginuser  = args[++i];
            loginpw    = args[++i];
         } else if (args[i].equals("-senduser")) {
            sendusers.add(args[++i]);
         } else if (args[i].equals("-upload")) {
            uploadfiles.add(args[++i]);
         } else if (args[i].equals("-verbose")) {
            lev = 1;
         } else if (args[i].equals("-debug")) {
            lev = 2;
         } else {
            usage();
         }
      }
      
      if (topURL == null) topURL = proto + "://" + machine + "/" + context;
      
      log("Spawning " + numapps + " worker threads");
      for(int i=0; i < numapps; i++) {
         new Thread(this).start();
      }
   }
   
   public void run() {
   
     // For DropboxAccess health
      DropboxAccess       dropbox;
      SessionHelper       sess_helper;
      ConnectionFactory   factory;

      String facClassS = null;
      if (dodirect) {
         facClassS = "oem.edge.ed.odc.dropbox.service.helper.DirectConnectFactory";
      } else if (useSoap) {
         facClassS = "oem.edge.ed.odc.dropbox.service.helper.JAXRPCConnectFactory";
      } else {
         facClassS = "oem.edge.ed.odc.dropbox.service.helper.HessianConnectFactory";
      }
            
      try {
      
         Class facClass = Class.forName(facClassS);
         factory = (ConnectionFactory)facClass.newInstance();
         if (topURL != null) factory.setTopURL(new java.net.URL(topURL));
        //if (useURI != null) factory.setURI(useURI);
         dropbox = factory.getProxy();
         
         String packname = "StressPackage_" + (new Random().nextInt()) + "_" + Thread.currentThread().getName();
         
         int iteration = 0;
         
         ((ProxyDebugInterface)dropbox).enableDebug(lev);
         
         
         HashMap sessionmap = dropbox.createSession(loginuser, loginpw);
      
        // Get session refreshing for free ... and autoclose if 1hr of inactivity
         sess_helper = new SessionHelper(dropbox, sessionmap);
         sess_helper.setAutoCloseDelay(60*60);
         sess_helper.setAutoClose(true);
         
         boolean myclean = false;
         synchronized (this) {
            myclean = clean;
            clean = false;
         }
         
         if (myclean) {
           // Delete all StressPackage_ packages from sent and drafts
           
            Vector v = 
               dropbox.queryPackages("StressPackage_*", true, true, false, false, false);
            if (v != null) {
               Iterator it = v.iterator();
               while(it.hasNext()) {
                  PackageInfo pinfo = (PackageInfo)it.next();
                  log("Deleting package for cleanup. Packid = " + pinfo.getPackageId());
                  dropbox.deletePackage(pinfo.getPackageId());
               }
            }
         }
         
         while(true) {
         
            log("Starting Iteration " + (++iteration));
                        
                        
           // Create a new package
            long pkgid = dropbox.createPackage(packname);
            
           // Set expiration to be tomorrow
            dropbox.changePackageExpiration(pkgid, 
                                            System.currentTimeMillis() + (24*60*60*1000));
            
           // Turn off notification
            dropbox.setPackageFlags(pkgid, DropboxAccess.SENDNOTIFY, (byte)0);
            
           // Set package description
            dropbox.setPackageDescription(pkgid, "This is a test description of stress test");
            
           // Add all recipients
            Iterator it = sendusers.iterator();
            while(it.hasNext()) {
               String recip = (String)it.next();
               dropbox.addUserAcl(pkgid, recip);
            }
            
            HashMap map = new HashMap();
            
           // Upload files
            it = uploadfiles.iterator();
            Vector operations = new Vector();
            while(it.hasNext()) {
               String filename = (String)it.next();
               File file = new File(filename);
               if (file.exists()) {
                  long fileid = dropbox.uploadFileToPackage(pkgid, filename, file.length());
                  log("Uploading file: " + file.length());
                  Operation op = new UploadOperation(dropbox, file, pkgid, fileid);
                  op.addOperationListener(this);
                  operations.add(op);
                  op.setNumberOfWorkers(numworkers);
                  op.process();
                  map.put(new Long(fileid), SearchEtc.calculateMD5(file));
                  
               } else {
                  log("Skipping file ... does not exist: " + filename);
               }
            }
            
            it = operations.iterator();
            while(it.hasNext()) {
               Operation op = (Operation)it.next();
               op.waitForCompletion();
               String m = op.getErrorMessages();
               if (m != null && m.length() > 0) {
                  log("Error uploading file: " + m);
               } else {
                  String md5 = (String) map.get(new Long(op.getFileId()));
                  if (!dropbox.queryFile(op.getFileId()).getFileMD5().equalsIgnoreCase(md5)) {
                     log("Uploaded MD5 differs from local MD5 for fileid " + op.getFileId());
                     log("localmd5 = " + md5);
                     log("remotemd5 = " + dropbox.queryFile(op.getFileId()).getFileMD5());
                  }
               }
            }
            
           // commit package
            dropbox.commitPackage(pkgid);
            Vector dofinally = new Vector();
            try {
               it = operations.iterator();
               Vector dopes = new Vector();
               int fidx = 1;
               while(it.hasNext()) {
                  UploadOperation op = (UploadOperation)it.next();
                  FileInfo finfo = op.getFileInfo();
                  File lf = new File("/tmp/stressdown_" + pkgid + "_" + op.getFileId());
                  dofinally.add(lf);
                  DownloadOperation dope = new DownloadOperation(dropbox, lf, pkgid, finfo);
                  dope.setNumberOfWorkers(numworkers);
                  dope.addOperationListener(this);
                  dope.process();
                  dopes.add(dope);
               }
               
               log("Begin download files");
               
               it = dopes.iterator();
               while(it.hasNext()) {
                  Operation op = (Operation)it.next();
                  op.waitForCompletion();
                  String m = op.getErrorMessages();
                  if (m != null && m.length() > 0) {
                     log("Error downloading file: " + m);
                  }
               }
            } finally {
               it = dofinally.iterator();
               while(it.hasNext()) {
                  File lf = (File)it.next();
                  lf.delete();
               }
            }
            
           // delete package
            dropbox.deletePackage(pkgid);
            
         }
      } catch(Exception ee) {
         log("Exception occurred ... stress thread bagging out!");
         ee.printStackTrace(System.out);
      }
   }
}
