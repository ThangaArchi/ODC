package oem.edge.ed.odc.dropbox.testcases;

import oem.edge.ed.odc.dropbox.service.*;
import oem.edge.ed.odc.dropbox.service.helper.*;
import oem.edge.ed.odc.dropbox.common.*;
import oem.edge.ed.odc.dsmp.common.DboxException;
import java.util.*;
import java.io.*;
import java.net.*;

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
 * Testcase showing how to use DropboxAccess service object to 
 * create a simple command line app which can upload/download files.
 *
 * Note: The point of this command line client is NOT to create a
 *        great feeling command line client, but instead its to 
 *        show some of the workings of the DropboxAccess interface
 *        and supporting helper classes (i.e. we know the client
 *        stinks ;-)
 */

public class TestDropbox implements SessionListener, OperationListener {

   SessionHelper sessionHelper;
   DropboxAccess dropbox;

   String proto = "http";
   String ctx  = "technologyconnect/odc";
   String mach = "edesign4.fishkill.ibm.com";
   String user = null;
   String pw   = null;
   String port = null;
   
   boolean done = false;
   
   InputStream is = System.in;
   
   int delay   = 10*60;   // 10 minute autoshutdown default

   public TestDropbox() {
   }
   
  /**
   * Logging routines.
   */
   public void log(String s) {
      System.out.println(s);
   }
   public void log(Throwable t) {
      t.printStackTrace(System.out);
   }


  /** This method will be called for the various events available on the
   *  Operation classes
   */
   public void operationUpdate(OperationEvent e) {
      Operation op = e.getOperation();
      if        (e.isMD5()) {
         log("Calculating local MD5");
      } else if (e.isEnded()) {
         log("Operation is ended");
      } else if (e.isData()) {
         System.out.print("\rDownload " + 
                          op.percentDone() + "%  " + 
                          op.getRemainingLength() + " bytes remaining                ");
      }
   }
   
  /** This method will be called for the various events available on the
   * SessionHelper. 
   */
   public void sessionUpdate(SessionEvent e) {
      if        (e.isInactivity()) {
         log("Idle session detected ...");
      } else if (e.isShutdown()) {
         log("Session shutdown complete");
         
        // Force us out of main while loop
         done = true;
         try {
           // Stop reading input
            is.close();
         } catch(Exception ee) {}
      } else if (e.isRefreshError()) {
         log("Refresh error occurred ... retry scheduled");
         if (e.getCause() != null) {
            log(e.getCause().getMessage());
         }
      }
   }
   
   
  /**   
   * Parse up cmdline args 
   */
   public void parseArgs(String args[]) throws Exception {
      for(int i=0; i < args.length; i++) {
         String opt = args[i];
         if        (opt.equalsIgnoreCase("-proto")) {
            proto = args[++i];
         } else if (opt.equalsIgnoreCase("-host")) {
            mach = args[++i];
         } else if (opt.equalsIgnoreCase("-port")) {
            port = args[++i];
         } else if (opt.equalsIgnoreCase("-user")) {
            user = args[++i];
         } else if (opt.equalsIgnoreCase("-pw")) {
            pw   = args[++i];
         } else if (opt.equalsIgnoreCase("-cmdfile")) {
            is   = new FileInputStream(args[++i]);
         } else if (opt.equalsIgnoreCase("-closedelay")) {
            delay= Integer.parseInt(args[++i]);
         }
      }
   }
   
  /**
   * Take a string and return the whitespace separated tokens in vector
   */
   public Vector parseCommand(String s) {
      Vector ret = new Vector();
      StringTokenizer stok = new StringTokenizer(s, " \t", false);
      while(stok.hasMoreTokens()) {
         ret.add(stok.nextToken());
      }
      return ret;
   }
   
  /**
   * Runs the testcase
   */
   public void process() throws Exception {
   
   
     // Setup topURL
      String topURL = proto + "://" + mach;
      if (ctx != null && ctx.length() > 0 && !ctx.equals("/")) {
         if (!ctx.startsWith("/")) {
             topURL += "/";
         }
             
         topURL += ctx;
      }
      
     // Create factory, set topURL, create proxy
      ConnectionFactory fac = new HessianConnectFactory();
      fac.setTopURL(new URL(topURL));
      dropbox = fac.getProxy();
      
     // Create the Dropbox Session (login)
      HashMap sessionmap = dropbox.createSession(user, pw);
      
     // Create session helper to keep the session alive. Enable Auto close
      sessionHelper = new SessionHelper(dropbox, sessionmap);
      sessionHelper.setAutoCloseDelay(delay);
      sessionHelper.setAutoClose(true);
      
     // Set up to receive asynchronous session events
      sessionHelper.addSessionListener(this);
            
     // Set session info to ID my application usage
      sessionHelper.setSessionInformation("TestDropbox");
     
     
      try {
        // Get a Buffered Reader to make input reading easier
         BufferedReader input = new BufferedReader(new InputStreamReader(is));
         
        // Read commands and process while not done
         while(!done) {
         
           // Read command
            System.out.print("command> ");
            String cmd = input.readLine();
            if (cmd == null) {
              // Log cleanup action and break from loop. "Finally" will ensure
              // cleanup sessionHelper.
               log("EOF reached. Cleaning up");
               done = true;
               break;
            }
            
           // Parse up command line for consumption
            Vector cv = parseCommand(cmd);
            
           // If we have a blank line, just continue
            if (cv.size() == 0) continue;
            
           // Save the command and remove from argument vector
            String c = ((String)cv.elementAt(0)).toLowerCase();
            cv.removeElementAt(0);
            
            try {
               
              // If we are listing ...
               if        (c.equals("list")) {
                  
                 // List inbox by default
                  if (cv.size() == 0) cv.add("inbox");
                  
                 // Easy access to args as iterator
                  Iterator itv = cv.iterator();
                     
                 // Get area to list
                  String tolist = (String)itv.next();
                     
                 // Support inbox, sent and drafts  as well as packid
                  log("Listing '" + tolist + "' :\n");
                  try {
                        
                     boolean inbox  = tolist.equalsIgnoreCase("inbox");
                     boolean sent   = tolist.equalsIgnoreCase("sent");
                     boolean drafts = tolist.equalsIgnoreCase("drafts");
                                          
                    // If we are listing a 'tab'
                     if (inbox || sent || drafts) {
                     
                       // If listing a regex name ... get it
                        String name = null;
                        if (itv.hasNext()) name = (String)itv.next();
                        
                       // If there are any more arguments, error
                        if (itv.hasNext()) {
                           throw new 
                              Exception("Usage: list <inbox|sent|drafts> <regex>");
                        }
                     
                       // Query inbox with possible regex name,
                       //  no filters including full details
                        Vector v = dropbox.queryPackages(name, true, !inbox, 
                                                         false, false, true);
                                                         
                       // Loop over each match
                        Iterator it = v.iterator();
                        while(it.hasNext()) {
                           PackageInfo p = (PackageInfo)it.next();
                           
                          // Skip complete packages as they cannot be in drafts
                           if (drafts && 
                               p.getPackageStatus() == 
                               DropboxAccess.STATUS_COMPLETE) {
                              continue;
                           }
                           
                          // Print the result
                           log("=================\n" + p.toString());
                        }
                     } else {
                     
                       // Listing a specific package (by id)
                       
                       // If there are any more arguments, error
                        if (itv.hasNext()) {
                           throw new Exception("Usage: list packid");
                        }
                     
                       // Lookup package by id
                        PackageInfo p = dropbox.queryPackage(Long.parseLong(tolist),
                                                             true);
                                                             
                       // Show package details
                        log("==Package===========\n" + p.toString());
                        
                       // Query inbox with no filters including full details
                        Vector v = dropbox.queryPackageContents(p.getPackageId());
                        
                       // Show package contents
                        Iterator it = v.iterator();
                        while(it.hasNext()) {
                           FileInfo f = (FileInfo)it.next();
                           log("==File===========\n" + f.toString());
                        }
                     }
                  } catch(Exception e) {
                     log(e);
                  }
                  
               } else if (c.equals("get") || c.equals("getpack") || c.equals("put")) {
               
                 // Upload and download data
                 
                 // Get the 3 required parms for each command
                  String e1 = (String)cv.elementAt(0);
                  String e2 = (String)cv.elementAt(1);
                  String e3 = (String)cv.elementAt(2);
                  
                  Operation op = null;
                  
                 // Create correct type of Operation object
                  if (c.equals("get")) {
                  
                    // Downloading a file with restarts
                     op = new DownloadOperation(dropbox, 
                                                new File(e3),
                                                Long.parseLong(e1),
                                                Long.parseLong(e2));
                                                
                  } else if (c.equals("getpack")) {
                  
                    // Downloading a package with encoding
                     op = new 
                        PackageDownloadOperation(dropbox, 
                                                 new FileOutputStream(new File(e3)),
                                                 Long.parseLong(e1),
                                                 e2);
                  } else {
                  
                    // Must be uploading a file with restarts
                     long packid = Long.parseLong(e2);
                     FileInfo fileinfo = null;
                     long fileid = -1;
                     
                    // Look up the fileid by name
                     Vector v = dropbox.queryPackageContents(packid);
                     Iterator it = v.iterator();
                     
                    // Find the file by name if it exists
                     while(it.hasNext()) {
                        FileInfo f = (FileInfo)it.next();
                        if (f.getFileName().equals(e3)) {
                           fileinfo = f;
                           break;
                        }
                     }
                     
                    // Get the length of file to upload
                     long len = (new File(e1)).length();
                     
                     if (fileinfo == null) {
                       // If file did not exist in package, create it
                        fileid = dropbox.uploadFileToPackage(packid, e3, len);
                        
                     } else if (fileinfo.getFileStatus() == 
                                DropboxAccess.STATUS_COMPLETE ||
                                len != fileinfo.getFileSize())  {
                                
                       // If file exists in package but is different size, 
                       //  delete it and recreate
                        dropbox.removeItemFromPackage(packid, fileinfo.getFileId());
                        fileid = dropbox.uploadFileToPackage(packid, e3, len);
                     } else {
                     
                       // File exists and is correct size ... just get fileid
                        fileid = fileinfo.getFileId();
                     }
                     
                    // Uploading a file to a package
                     op = new UploadOperation(dropbox, 
                                              new File(e1),
                                              packid,
                                              fileid);
                  }
                  
                 // Sign up for status updates on operation
                  op.addOperationListener(this);
                  
                 // Start the operation
                  op.process();
                  
                 // Block till done
                  op.waitForCompletion();
                  
                  log("Operation " + (op.validate()?"worked":"failed"));
                  
               } else if (c.equalsIgnoreCase("debug")) {
               
                 // Toggle debugging
                  boolean debugval = sessionHelper.isDebugEnabled();
                  log("Toggle debug " + (debugval?"off":"on"));
                  sessionHelper.setProxyDebug(!debugval);
                  
               } else if (c.equalsIgnoreCase("closedelay")) {
               
                  String e1 = (String)cv.elementAt(0);
               
                 // Change the autoclose delay for inactivity
                  delay = Integer.parseInt(e1);
                  sessionHelper.setAutoCloseDelay(delay);
                  log("AutoCloseDelay set to [" + delay + "] seconds");
                  
               } else if (c.equalsIgnoreCase("makepack")) {
               
                  String e1 = (String)cv.elementAt(0);
                  
                 // Create a new package
                  dropbox.createPackage(e1);
                  log("Package [" + e1 + "] created");
                  
               } else if (c.equalsIgnoreCase("addacl")) {
               
                  String e1 = (String)cv.elementAt(0);
                  String e2 = (String)cv.elementAt(1);
                  
                 // Add user as new recipient
                  dropbox.addUserAcl(Long.parseLong(e1), e2);
                  log("User [" + e2 + "] added as recipient to Package [" + e1 + "]");
                  
               } else if (c.equalsIgnoreCase("rmacl")) {
               
                  String e1 = (String)cv.elementAt(0);
                  String e2 = (String)cv.elementAt(1);
                  
                 // Remove user from ACL list for package
                  dropbox.removeUserAcl(Long.parseLong(e1), e2);
                  log("User [" + e2 + "] removed as recipient from Package [" + 
                      e1 + "]");
                      
               } else if (c.equalsIgnoreCase("commit")) {
               
                  String e1 = (String)cv.elementAt(0);
               
                 // Commit a package in the drafts
               
                 // Query inbox with possible regex name,
                 //  no filters including full details
                  Vector v = dropbox.queryPackages(e1, true, true,
                                                   false, false, true);
                                                   
                  if (v.size() == 0) {
                     log("No matches when committing '" + e1 + "'");
                     continue;
                  }
                  
                 // Search for package we are to commit (could be more than
                 //  one as we are allowing regex)
                  Iterator it = v.iterator();
                  while(it.hasNext()) {
                  
                     PackageInfo p = (PackageInfo)it.next();
                     
                    // Skip complete packages as they cannot be in drafts
                     if (p.getPackageStatus() == DropboxAccess.STATUS_COMPLETE) {
                        continue;
                     }
                  
                    // Commit the package
                     dropbox.commitPackage(p.getPackageId());
                     log("Package [" + p.getPackageName() + "] committed");
                  }
                  
               } else {
                  log("Invalid command '" + c + "'. Usage:");
                  log("   list <inbox|sent|drafts> <regex>");
                  log("   list <packageid>");
                  log("   get packageid fileid localfile");
                  log("   getpack packageid encoding localfile");
                  log("   put localfile packageid remotefilename");
                  log("   makepack packname");
                  log("   commit packname");
                  log("   addacl packid userid");
                  log("   rmacl  packid userid");
                  log("   debug");
                  log("   closedelay secs");
               }
            } catch(DboxException dboxexp) {
              // DropboxExceptions may have general command level message and
              //  a more detailed message, separated by "<@-@>"
               String msg = dboxexp.getMessage();
               int idx = msg.indexOf("<@-@>");
               String detail = "none";
               if (idx >= 0) {
                  detail = msg.substring(idx + 5);
                  msg = msg.substring(0, idx);
               }
               
               log("Exception while processing command '" + c + "'");
               log(dboxexp);
               
               log("\n\nException message: " + msg);
               log("Level 2 detail   : "     + detail);
               
            } catch(Exception eee) {
               log("Generic caught Exception while processing command '" + c + "'");
               log(eee);
            }
         }
      } finally {
        // In a finally, call cleanup ... no way out without trying to cleanup.
         sessionHelper.cleanup();
      }
   }
   
   /**
   * Main routine simply created a TestDropbox instance and runs it
   */
   public static void main(String args[]) {
      
      
      TestDropbox tp = new TestDropbox();
      try {
         tp.parseArgs(args);
         tp.process();
      } catch(Exception e) {
         tp.log("Exception while running test: ");
         tp.log(e);
      }
   }
}
