package oem.edge.ed.odc.dropbox.server;

import java.lang.*;
import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;
import java.lang.reflect.*;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import javax.activation.DataHandler;
import javax.activation.DataSource;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import oem.edge.ed.odc.util.UserRegistryFactory;
import  com.ibm.as400.webaccess.common.*;

import  oem.edge.ed.odc.dropbox.common.*;
import  oem.edge.ed.odc.dropbox.service.DropboxAccess;
import  oem.edge.ed.odc.dropbox.service.DropboxAccessWithHandler;
import  oem.edge.ed.util.*;
import  oem.edge.ed.odc.util.*;
import  oem.edge.common.cipher.*;

import  oem.edge.ed.odc.dsmp.server.*;
import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.tunnel.common.*;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;

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

public class DropboxAccessSrv implements DropboxAccessWithHandler {

   final static long MaxDownloadSlotSize   = 1024*1024*2;    // 2 Meg max slot size
   
  // Default MAX component size. Used to be 50M, smaller now cause of DB2 alloc
  //  waste that can occur with sftp/web aborted uploads. Space no longer given
  //  back upon failure, as we don't have a TCP connected JVM per dropbox anymore 
   public final long MAX_COMPONENT_SIZE   = 1024*1024*12;
      
  // Default smallest file chunking we will do if doing variable sized components
   public final long MIN_COMPONENT_SIZE   = 1024*1024*1;
   
  // If this changes, it MUST be synced up with the helper download operation
   final static long MIN_TAIL_BLOCK_SIZE   = 10000;          // Size of final read

   final static byte NagMail           = (byte)1;
   final static byte ReturnReceiptMail = (byte)2;
   final static byte NotificationMail  = (byte)3;

   
   protected Logger  log             = null;
   
   
   DB2PackageManager    packageMgr      = null;
   
  // Will contain the sessionid
   private static java.lang.ThreadLocal sessionIDs = new ThreadLocal();
   
  // holds the singleton instance if we are using that method of sharing
   private static DropboxAccessSrv singleton = null;
   
  // This method is actually a singleton factory method.
   public static DropboxAccessSrv getSingleton() {
      if (singleton == null) {
         synchronized(sessionIDs) {
            if (singleton == null) {
               singleton = new DropboxAccessSrv();
            }
         }
      }
      return singleton;
   }
   
   
   protected AMTMailer amtMailer             = new AMTMailer();

  // For unwrapping tokens
   protected ODCipherRSA cipher = null;

  // Arguments, etc.
   protected String forcedoverrides = null;
   protected MyReloadingProperty properties  = new MyReloadingProperty();
   
   
   protected ReloadingProperty limitSendByProject = null;
   
  // Override class to apply overrides to prop file which has changed
   class MyReloadingProperty extends ReloadingProperty {
      public boolean tryReload() {
         boolean ret = super.tryReload();
         if (ret) {
            handleOverrides();
         }
         return ret;
      }
      
      public void handleOverrides() {
      
        // Get overrides from file itself
         String overrides = getPropertyNoReload("dropbox.overrides", null);
         handleOverrides(overrides);
         
        // Handle any forced overrides (like from main)
         handleOverrides(forcedoverrides);
      }
      
      public void handleOverrides(String overrides) {
         if (overrides != null) {
            
           // Overrides are token:token or token=token
           //  each token can be encased in '' or "". 
           
           // Parse the string into tokens. All consecutive chars are a token,
           //  as well, entire strings enclosed as qouted strings.
            String maindelims = " =:\n\"'";
            String curdelims  = maindelims;
            boolean qstring = false;
            StringTokenizer st = new StringTokenizer(overrides, maindelims, true);
            
            boolean first = true;
            String lasttoken = null;
            while(st.hasMoreTokens()) {
               
               String token = st.nextToken(curdelims);
               
              // If we have a delim
               if (token.length() == 1 && curdelims.indexOf(token) >= 0) {
                  if (qstring) {
                     qstring = false;
                     curdelims = maindelims;
                  } else if (token.equals("'") || token.equals("\"")) {
                     qstring = true;
                     curdelims = token;
                  }
                  continue;
               }
               
               if (first) { 
                  lasttoken = token; 
                  first = false;
               } else {
                  first = true;
                  String debug = 
                     "Overriding property [" + lasttoken + "] = [" + token + "]";
                  if (log != null) {
                     log.info(debug);
                  } else {
                     DebugPrint.printlnd(DebugPrint.INFO4, debug);
                  }
                  if (reloadProp != null) {
                     reloadProp.setProperty(lasttoken, token);
                  } else {
                     AppProp.setProperty(lasttoken, token);
                  }
               }
            }
         }
      }
   }
   
  // When all the data is download or bad completion ... we will get a callback here
  //  Add the fileaccess record
   class CompletionListener implements ActionListener {
   
      DboxPackageInfo pinfo;
      DboxFileInfo    info;
      User            user;
      boolean         done;
   
      public CompletionListener(User user,
                                DboxPackageInfo pinfo, 
                                DboxFileInfo info, 
                                boolean done) {
         this.user  = user;
         this.pinfo = pinfo;
         this.info  = info;
         this.done  = done;
      }
      
      public void actionPerformed(ActionEvent ev) {
         try {
            if (ev.getID() == DropboxGenerator.STATUS_COMPLETE && done) { 
               pinfo.addFileAccessRecord(user, info.getFileId(), 
                                         DropboxGenerator.STATUS_COMPLETE, 
                                         0 /*Xferrate*/);
                                         
              // JMC 8/7/06 - We send in 0 for XFERRATE ... the CLIENT sets
              //              the actual perceived rate
                                         
              // Get an updated view of the pinfo object. If the object is
              // reading complete and was not before, send RR email if needed
               try {
                  DboxPackageInfo newpinfo = 
                     packageMgr.lookupPackage(pinfo.getPackageId(), user);
                  
                  if (doamtmailing()                     &&
                      newpinfo.getPackageReturnReceipt() && 
                      newpinfo.getPackageCompleted()     &&
                      !pinfo.getPackageCompleted()) {
                     amtMailer.sendAMTMailRR(user, newpinfo, 
                                             newpinfo.getPackageOwner()); 
                  }
               } catch(DboxException dbe2) {
                  log.error("DBException sending RR email");
                  log.error(dbe2);
               }
                                         
            } else {
              //
              //  - We want to incr the byte count accessed via this session
              //  - Calc xfer rate based on start time, and bytes returned.  
              //    Xfer rate would NOT be correct, as it was calculated 
              //    prior to this send. Add a method (confirmFileDownload)
              //    to help "acuratize" the number.
              //
              // JMC 8/7/06
              //  ... Biiiinnggg! Client now tells us his version of the truth
              //
               pinfo.addFileAccessRecord(user, info.getFileId(), 
                                         DropboxGenerator.STATUS_FAIL, 
                                         0 /*Xferrate*/);
                                         
            }   
         } catch(Exception e) {
            log.warn("Error logging file access record:\n" +
               user.toString() + "\n\n" + pinfo.toString() + "\n\n" + 
                     info.toString());
         }
      }
   }
   
  // When all the data is download or bad completion ... we will get a callback here
  //  Add the fileaccess record
   class PackageCompletionListener implements ActionListener {
   
      DboxPackageInfo    pinfo;
      User               user;
      PackageInputStream pstrm;
   
      public PackageCompletionListener(User user, 
                                       DboxPackageInfo pinfo, 
                                       PackageInputStream pstrm) {
         this.user  = user;
         this.pinfo = pinfo;
         this.pstrm = pstrm;
      }
      
      public void actionPerformed(ActionEvent ev) {
         try {
            if (ev.getID() == DropboxGenerator.STATUS_COMPLETE) {
            
              // JMC 8/7/06 - xferrate is calculated from stats on pis
               long totbytes = pstrm.getTotalBytesWritten();
               long       ms = pstrm.getElapsedTime();
               int  xferrate = 0;
               if (ms > 0) {
                  xferrate = (int)((totbytes*1000)/ms);
               }
            
              // Mark all files as accessed
               Enumeration enum =  pinfo.getFiles().elements();
               while(enum.hasMoreElements()) {
                  FileInfo linfo = (FileInfo)enum.nextElement();
                  pinfo.addFileAccessRecord(user, linfo.getFileId(), 
                                            DropboxGenerator.STATUS_COMPLETE,
                                            xferrate);
               }
            
            
              // Get an updated view of the pinfo object. If the object is
              // reading complete and was not before, send RR email if needed
               try {
                  DboxPackageInfo newpinfo = 
                     packageMgr.lookupPackage(pinfo.getPackageId(), user);
                  
                  if (doamtmailing()                     &&
                      newpinfo.getPackageReturnReceipt() && 
                      newpinfo.getPackageCompleted()     &&
                      !pinfo.getPackageCompleted()) {
                     amtMailer.sendAMTMailRR(user, newpinfo, 
                                             newpinfo.getPackageOwner()); 
                  }
               } catch(DboxException dbe2) {
                  log.error("DBException sending RR email");
                  log.error(dbe2);
               }
                                         
            } else if (ev.getID() != DropboxGenerator.STATUS_NONE) {
              // TODOTODOTODO ... should we mark access to any files?
               log.warn("Error completing full package download:\n" +
                        user.toString() + "\n\n" + pinfo.toString());
            }   
         } catch(Exception e) {
            log.warn("Error logging file access records for package download:\n" +
                     user.toString() + "\n\n" + pinfo.toString());
         }
      }
   }
   
   
   class ByteArrayDataSource implements DataSource {
      byte arr[];
      int  len;
      int  ofs;
      
      public ByteArrayDataSource(byte b[], int o, int l) {
         arr = b;  ofs = o;  len = l;
      }
   
      public ByteArrayDataSource(byte b[]) {
         arr = b;  ofs = 0;  len = b.length;
      }
      
      public String getContentType() { 
         return "application/binary";
      }
      public InputStream getInputStream()  throws IOException  { 
         return new ByteArrayInputStream(arr, ofs, len); 
      }
      public String getName() {
         return "from byte array";
      }
      public OutputStream getOutputStream() throws IOException {
         throw new IOException("InputStream use only");
      }      
   }
   
   public DropboxAccessSrv(String overrides) {
      forcedoverrides = overrides;
      doConstructor();
   }
   
   public DropboxAccessSrv() {
      doConstructor();
   }
   
   protected void doConstructor() {
      try {
      
         properties.setReloadPropertyName("dropbox.reloadPropertyFile");
         properties.load("DropboxServer.properties");
         
         String logfile = getProperty("dropbox.log4jlog");
         if (logfile != null) {
            logfile = SearchEtc.findFileInClasspath(logfile);
         }
         if (logfile != null) {
            PropertyConfigurator.configure(logfile);
         } else {
            System.out.println("No logfile specified/found for DropboxAccess log4j");
         }
         log = Logger.getLogger(getClass().getName());
         
         properties.handleOverrides();
         
         boolean useds = getBoolProperty("dropbox.useDataSource", true);
         log.info("Using datasource = " + useds);
         
         if (useds) {
            
            DBConnection lconn = DBSource.getDBConnection("AMT");
            if (lconn == null) {
               lconn = new DBConnDataSource();
               lconn.setURL("java:comp/env/edodc");
               lconn.setURL(getProperty("dropbox.FrontpageDatasource",
                                        "java:comp/env/edodc"));
               DBSource.addDBConnection("AMT", lconn, true);
               DBSource.addDBConnection("EDODC", lconn, true);
               
               log.info("AMT/EDODC datasource = " + lconn.getURL());
               
              /* to help debug nested resultset bug in WAS Pooled connections
               Connection c = null;
               try {
                  c = lconn.getConnection();
                  PreparedStatement pstmt = 
                     c.prepareStatement("select * from edesign.frontpage");
                  ResultSet rs1 = pstmt.executeQuery();
                  while(rs1.next()) {
                     PreparedStatement pstmt2 = 
                        c.prepareStatement("select * from edesign.frontpage");
                     ResultSet rs2 = pstmt2.executeQuery();
                     if (rs2.next()) System.out.println("rs2 has next");
                     else            System.out.println("rs2 has NO next");
                     pstmt2.close();
                  }  
                  
               } catch(Exception ee) {
                  System.out.println("Error doing test");
                  ee.printStackTrace(System.out);
               } finally {
                  try { c.close(); } catch(Exception uu) {}
               }
              */
            }
            
            lconn = DBSource.getDBConnection("dropbox");
            if (lconn == null) {
               lconn = new DBConnDataSource();
               lconn.setURL(getProperty("dropbox.DropboxDatasource",
                                        "java:comp/env/dropbox"));
               DBSource.addDBConnection("dropbox", lconn, true);
               log.info("Dropbox/Groups datasource = " + lconn.getURL());
            }
            DBSource.addDBConnection("GROUPS", lconn, true);
            
         } else {
           // Only load it up if its not already initted
            DBConnection lconn = DBSource.getDBConnection("AMT");
            if (lconn == null) {
            
               DBConnection conn = new DBConnLocalPool();
               conn.setDriver     (getProperty("dropbox.DB2DboxDriver", null));
               conn.setURL        (getProperty("dropbox.DB2DboxURL", null));
               conn.setInstance   (getProperty("dropbox.DB2DboxInstance", null));
               conn.setPasswordDir(getProperty("dropbox.DB2DboxPWDIR", null));
               
              // let the DB2 connection pool grow as needed
               ((DBConnLocalPool)conn).maxPoolSize = 
                  getIntProperty("dropbox.maxPoolSize", 3);
                  
               log.info("Localpool maxsize = " + (((DBConnLocalPool)conn).maxPoolSize));
               
              // Refactored the code to share Groups code ... use a different
              //  search qualifier to find the same DBConnection object
               DBSource.addDBConnection("dropbox", conn, false);
               DBSource.addDBConnection("GROUPS", conn, false);
               
               log.info("Dropbox/Groups Localpool = " + conn.getURL());
               
               conn = new DBConnLocalPool();
               conn.setDriver     (getProperty("dropbox.DB2AMTDriver", null));
               conn.setURL        (getProperty("dropbox.DB2AMTURL", null));
               conn.setInstance   (getProperty("dropbox.DB2AMTInstance", null));
               conn.setPasswordDir(getProperty("dropbox.DB2AMTPWDIR", null));
               DBSource.addDBConnection("AMT", conn, false);
               DBSource.addDBConnection("EDODC", conn, false);
               
               log.info("AMT/EDODC Localpool = " + conn.getURL());
            }
         }
                  
        // Create appropriate file allocator
         DB2DboxFileAllocator fa = new DB2DboxFileAllocator();
         
        // Set max component size ... will be further mitigated by pool
         long compsize = getLongProperty("dropbox.ComponentSize", MAX_COMPONENT_SIZE);
         fa.setMaxComponentSize(compsize);
         log.info("Dropbox maxcomponentsize = " + compsize);
         
        // Set max component size ... will be further mitigated by pool
         compsize = getLongProperty("dropbox.MinComponentSize", MIN_COMPONENT_SIZE);
         DboxFileInfo.min_component_size = compsize;
         log.info("Dropbox mincomponentsize = " + DboxFileInfo.min_component_size);
         
         
        // Set the max active slots to limit upload channels
        // NOTE: AFS/GSA/NFS have 'issues' with simultaneous file access from
        //       different machines ... so, we limit to one, and will raise 
        //       to a larger number (5) when we move into SHARK storage
         DboxFileInfo.max_num_active_slots = getIntProperty("dropbox.MaxActiveSlots", 1);
         log.info("Dropbox maxactiveslotes = " + DboxFileInfo.max_num_active_slots);
         
        // Set the max size for a file slot
         DboxFileInfo.max_file_slot_size   = getLongProperty("dropbox.MaxFileSlotSize", 
                                                             2*1024*1024);
         log.info("Dropbox maxfileslotsize = " + DboxFileInfo.max_file_slot_size);
         
        // Set whether or not we use channel locking on the slots that we write.
        // NOTE: For single server setup, should not need locking. If multi server,
        //       STILL should not NEED it, but we found that the file may not be
        //       correctly syncronized between the two machine for simultaneous update.
        //       on GSA/NFS w/o it. For AFS, it seems hopeless, it does not correctly
        //       synchronize simultaneous update even with locking.
         DboxFileSlot.do_record_locking = getBoolProperty("dropbox.SlotRecordLocking", false);
         log.info("Dropbox dorecordlocking = " + DboxFileSlot.do_record_locking);
         
        // Set whether file slots and components should be equal size. We would only want
        //  this to prevent the locking issues mentioned above. This would allow multiple
        //  channels cause there would only ever be a single writer per component. For
        //  large files, this will result in a GLUT of components
         DboxFileInfo.slots_equal_components = getBoolProperty("dropbox.SlotsEqualComponents",
                                                               false);
         log.info("Dropbox slotsEqualComponents = " + DboxFileInfo.slots_equal_components);
         
        // Set whether we modify the component size when doing slotsEqualComponents
        //  to help achieve better multichannel uploading. This will select smaller 
        //  component sizes when the amount left to upload is smaller
         DboxFileInfo.variable_component_size = 
            getBoolProperty("dropbox.VariableComponentSize", true);
            
         log.info("Dropbox variableComponentSize = " + DboxFileInfo.variable_component_size);
         
         
        // Set the upload_lock_clause used to get a lock on a row for life of transaction
         FileManager.update_lock_clause = 
            getProperty("dropbox.UpdateLockClause", 
                        " FOR UPDATE WITH RS USE AND KEEP UPDATE LOCKS ");
         
         log.info("Dropbox UploadLockClause = " + FileManager.update_lock_clause);
         
        // Set desired allocation policy
         byte allocpolicy = DboxFileAllocator.ALLOCATION_BALANCED;
         String allocpolicyS = getProperty("dropbox.allocationPolicy", "balanced");
         
         log.info("Dropbox file allocation policy = " + allocpolicyS);
         
         if (allocpolicyS.equalsIgnoreCase("balanced")) {
            allocpolicy = DboxFileAllocator.ALLOCATION_BALANCED;
         } else if (allocpolicyS.equalsIgnoreCase("priority")) {
            allocpolicy = DboxFileAllocator.ALLOCATION_PRIORITY;
         } else {
            log.warn("Invalid allocation policy specified: " + allocpolicyS);
         }
         
         fa.setAllocationPolicy(allocpolicy);
         
        // Create package manager using fileallocator
         packageMgr = new DB2PackageManager(fa);
         
        // Set desired send policy on PackageMgr
         packageMgr.policy_supportIBM =
            getBoolProperty("dropbox.policy_supportIBM", true);
         packageMgr.policy_supportSameCompany = 
            getBoolProperty("dropbox.policy_supportSameCompany", false);
         packageMgr.policy_supportLookup = 
            getBoolProperty("dropbox.policy_supportLookup", true);
                                                        
         log.info("Dropbox support policy         IBM = " + packageMgr.policy_supportIBM);
         log.info("Dropbox support policy SAMECOMPANY = " + packageMgr.policy_supportSameCompany);
         log.info("Dropbox support policy LOOKUP      = " + packageMgr.policy_supportLookup);
                                                        
                                                        
        // Does a threaded close help sometimes
         ComponentOutputStream.makeThreaded = getBoolProperty("dropbox.threadedClose",
                                                              true);
                                                              
        // AMT query fine tuning
         AMTQuery.includeMasterFSE = getBoolProperty("dropbox.includeMasterFSE",
                                                     false);

         AMTQuery.includeFSEAdmin  = getBoolProperty("dropbox.includeFSEAdmin",
                                                     false);
         
         log.info("Dropbox AMTQuery includeMasterFSE  = " + AMTQuery.includeMasterFSE);
         log.info("Dropbox AMTQuery includeFSEAdmin   = " + AMTQuery.includeFSEAdmin);
         
        // Do we limit sendByProject? If so, load up the folks who can use it
         String lsbp = getProperty("dropbox.limitSendByProject", null);
         if (lsbp != null) {
            try {
              // Have it watch for changes
               limitSendByProject = new ReloadingProperty(lsbp);
               limitSendByProject.setReloadFile(lsbp);
               log.info("Dropbox limitSendByProject = " + lsbp);
               
            } catch(IOException ioe) {
               log.warn("limitsendbyproject IOException: " + lsbp);
               log.warn(ioe);
            }
         }
         
      } catch(Exception ee) {
         if (log != null) {
            log.warn("Error during startup");
            log.warn(ee);
         } else {
            System.out.println("Error during startup. LOG not even inited!");
            ee.printStackTrace(System.out);
         }
      }
   }
   
   boolean getBoolProperty(String k, boolean def) {
      try {
         return ((String)properties.getProperty(k.toUpperCase())).equalsIgnoreCase("true"); 
      } catch(Exception ee) {
         return def;
      }
   }
   
   int getIntProperty(String k, int def) {
      try {
         return Integer.parseInt(((String)properties.getProperty(k.toUpperCase())));
      } catch(Exception ee) {
         return def;
      }
   }
   
   long getLongProperty(String k, long def) {
      try {
         return Long.parseLong(((String)properties.getProperty(k.toUpperCase())));
      } catch(Exception ee) {
         return def;
      }
   }
   
   String getProperty(String k, String def) {
      try {
         return ((String)properties.getProperty(k.toUpperCase(), def));
      } catch(Exception ee) {
         return def;
      }
   }
   
   String getProperty(String k) {
      return getProperty(k, null);
   }
   
   boolean doamtchecking()   { return getBoolProperty("dropbox.useamt",        false); }
   boolean doamtmailing()    { return getBoolProperty("dropbox.sendmail",      false); }
   boolean doHtml()          { return getBoolProperty("dropbox.doHtml",        false); }
   boolean doWebdropbox()    { return getBoolProperty("dropbox.doWebdropbox",  true);  }
   boolean doAmtprojects()   { return getBoolProperty("dropbox.doAmtprojects", true);  }
   boolean doemailsend()     { return getBoolProperty("dropbox.doemailsend",   true);  }
   boolean doreplyTo()       { return getBoolProperty("dropbox.doreplyto",     true);  }
   boolean doGroupAMT()      { return getBoolProperty("dropbox.doGroupAMT",    true);  }
   boolean allowByEmail()    { return getBoolProperty("dropbox.allowByEmail",  false); }
   
   boolean failIfNotEntitled() { 
      return getBoolProperty("dropbox.failIfNotEntitled", true);
   }
   
   boolean requireLoginAMTCheck() { 
      return getBoolProperty("dropbox.requireLoginAMTCheck", true);
   }
   
   boolean byEmailToIBMONLY(){ 
      return getBoolProperty("dropbox.byEmailToIBMONLY", true);  
   }
   boolean complainReceiverNoExist() { 
      return getBoolProperty("dropbox.complainReceiverNoExists",  true); 
   }
   
   String getURLNoQuery() {
      return getProperty("dropbox.FEHostURL", 
                         "http://www-309.ibm.com")        + "/" +
             getProperty("dropbox.FEEdesignServlet", 
                         "technologyconnect/EdesignServicesServlet.wss");
   }
   
   ODCipherRSA getCipher() { 
      if (cipher == null) {
         String cipherfile = getProperty("dropbox.localcipher"); 
         cipher = SearchEtc.loadCipherFile(cipherfile);
      }
      return cipher;
   }
   
   String getBannerFilename() { 
      return getProperty("dropbox.bannerFile",  null);
   }
   
   String getITAREntitlement() { 
      return getProperty("dropbox.ITAREntitlement", "ITAR_CERTIFIED");
   }
   
   String tunneldropbox() { 
      return getProperty("dropbox.tunneldropbox", "edesign.chips.ibm.com"); 
   }
   
   String forcedropboxpath() { 
      return getProperty("dropbox.forcedebugpath", "/forcedropboxdebug"); 
   }
   
   String sftpdropbox() { 
      return getProperty("dropbox.sftpdropbox", "dropbox.chips.ibm.com"); 
   }
   
   String smtpserver() { 
      return getProperty("dropbox.smtpserver", "us.ibm.com"); 
   }
   
   String forbiddenCharacters() {
      return getProperty("dropbox.forbiddenChars", ":"); 
   }
   
   class ReloadingFileProperty {
   
      String  filePropName;
      String  defVal;
      
      String  lastFile;
      String  lastFileShort;
      long    lastModified = 0L;
      int     researchFile = 500;
      boolean lastInFileSystem = false;
      
      ReloadingFileProperty(String propname, String defval) {
         this.defVal = defVal;
         filePropName = propname;
      }
      
      synchronized String getProperty() {
         if (filePropName != null) {
            String filename = properties.getProperty(filePropName);
            
           // Continue on to other reload checks IIF we have a filename 
            if (filename != null) {
                 
               try {
               
                 // Search for file only if we need to OR its been awhile
                  String thisFile = lastFile;
                  if (lastFileShort == null           ||
                      !lastFileShort.equals(filename) ||
                      ++researchFile > 500) {
                     
                    // Find using properties search (will use servingDirectories as well)
                     thisFile  = properties.findFileWhereEver(filename);
                     
                    // Prepend with file:
                     if (thisFile != null) thisFile = "file:" + thisFile;
                     
                    // When searching using the resource search method, make sure
                    //  we search relative to the classpath entry, not the loaders
                    //  notion of 'curdir'
                     String lfilename = filename;
                     if (!filename.startsWith("/")) {
                        lfilename = "/" + filename;
                     }
                     
                    // Only take this method if its different than a regular file OR failed
                    //  with above method.
                     URL uf  = this.getClass().getResource(lfilename);
                     if (uf != null) {
                        String us = uf.toString();
                        if (!us.startsWith("file:") || thisFile == null) {
                           thisFile = us;
                        }
                     }
                  }
                  
                  if (thisFile != null) {
                     BufferedReader br = null;
                     long tlastModified = lastModified;
                     
                    // If its a plain file, use fast File method, otherwise
                    //  just skip the reload, as calling lastModified seems to
                    //  delay for a second or so. not cool.
                     if (thisFile.startsWith("file:")) {
                        File thisFILE = new File(thisFile.substring(5));
                        tlastModified = thisFILE.lastModified();
                        if (thisFILE.exists() &&
                            (lastFile == null ||
                             !thisFile.equals(lastFile) || 
                             tlastModified != lastModified)) {
                           br = new BufferedReader(new InputStreamReader(new FileInputStream(thisFILE)));
                        }
                     } else {
                        URL url = new URL(thisFile);
                        URLConnection uc = url.openConnection();
                        
                       // tlastModified = uc.getLastModified();
                        
                        if (lastFile == null ||
                            !thisFile.equals(lastFile) 
                           // || tlastModified != lastModified
                           ) {
                           br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                        }
                     }
                                          
                    // If we have a file ... load it
                     if (br != null) {
                        String l;
                        StringBuffer ans = new StringBuffer();
                        while((l=br.readLine()) != null) {
                           ans.append(l).append('\n');
                        }
                        
                        defVal = ans.toString();
                        
                        lastModified  = tlastModified;
                        lastFile      = thisFile;
                        lastFileShort = filename;
                        researchFile  = 0;
                     }
                  }
                  
               } catch(Exception ee) {
                  System.out.println("Error (re)loading file resource: " + filePropName);
                  ee.printStackTrace(System.out);
               }
            }
         }
         return defVal;
      }
   }         
   
   ReloadingFileProperty mailbodyRF;
   ReloadingFileProperty mailbodyRRRF;
   ReloadingFileProperty mailbodyNag;
   
   String mailBody() {
      if (mailbodyRF == null) {
         mailbodyRF = new ReloadingFileProperty("dropbox.mailbodyFile", null);
      }
      return mailbodyRF.getProperty();
   }
   
   String mailBodyRR() {
      if (mailbodyRRRF == null) {
         mailbodyRRRF = new ReloadingFileProperty("dropbox.mailbodyRRFile", null);
      }
      return mailbodyRRRF.getProperty();
   }
   
   String mailBodyNag() {
      if (mailbodyNag == null) {
         mailbodyNag = new ReloadingFileProperty("dropbox.mailbodyNagFile", null);
      }
      return mailbodyNag.getProperty();
   }
   
   String msubrr="IBM Customer Connect Dropbox: Return Receipt: %ownerid% \"%20%packagename%\"";
   String msub="IBM Customer Connect Dropbox - Sender: %ownerid% - \"%40%packagename%\"";
   String msubnag="IBM Customer Connect Dropbox - About to Expire - Sender: %ownerid% - \"%20%packagename%\"";
   
   String hdi = 
   "Finally, the package files may be individually downloaded\n" +
   "via a standard web browser using the URL's listed with each\n" +
   "file. Note that this facility is only available for files\n" +
   "less than 2GB in size, and that the download is NOT restartable\n\n";
   
   String wdi = 
      "To launch the Web based dropbox tool, select this URL:\n\n" +
      "    %weblaunchurl%\n\n";
   
   String webDownloadInfo() {
      return properties.getProperty("dropbox.webDownloadInfo", wdi);
   }
   
   String htmlDownloadInfo() {
      return properties.getProperty("dropbox.htmlDownloadInfo", hdi);
   }
   
   
   String senderAddr() { 
      return getProperty("dropbox.senderAddr", "econnect@us.ibm.com"); 
   }
   
   String getLaunchURL() { 
      return getURLNoQuery() + getProperty("dropbox.launchURL", "?op=7");
   }
   String getDownloadFileURL() { 
      return getURLNoQuery() + getProperty("dropbox.downloadFileURL", "?op=7&sc=");
   }
   String getWebLaunchURL() { 
      return getURLNoQuery() + getProperty("dropbox.webLaunchURL", "?op=7&sc=webox:op:i");
   }
   
   String mailSubjectNag() {
      return getProperty("dropbox.mailSubjectNag", msubnag);
   }
   String mailSubjectRR() {
      return getProperty("dropbox.mailSubjectRR", msubrr);
   }
   String mailSubject() {
      return getProperty("dropbox.mailSubject", msub);
   }
   
  // Default is 75% thru lifetime, then on the last 3 days
   String nagInterval() {
      return getProperty("dropbox.nagMailInterval", "75:0:-1:-2");
   }
   
   
  // Builds a multi-tiered error message.  
  //
  // Returns the errmsg as main message, and msg as the sub-message. 
  // If a prefix of *:>> is found in msg, it is stripped off
  //
  // The format of the returned message is  mainmsg<@-@>submsg
  //
   protected String buildReturnErrorMsg(String errmsg, String msg) {
      String ret = "";
      if (errmsg != null) ret = errmsg;
      if (msg != null && msg.length() > 0) {
         int idx = msg.indexOf(":>>");
         if (idx >= 0) {
            msg = msg.substring(idx+3).trim();
         }
         ret = ret + "<@-@>" + msg;
      }
      return ret;
   }

  // Returns 'principal' object
  //
   
   public void setThreadSessionID(String sessionid) {
   
      log.debug("Setting sessionid for thread to " + sessionid);
      
      sessionIDs.set(sessionid);
      
     // If we are forgetting the session, lets forget alert info as well
      if (sessionid == null) {
         DboxAlert.setAlertInfo(null);
         
        // Clear the entire NDC ... no horsing around
         MDC.remove("CMD");
         MDC.remove("USER");
         MDC.remove("COMPANY");
         MDC.remove("EMAIL");
         MDC.remove("SESSIONID");
      }
   }
   
   public String getThreadSessionID() {
      return (String)sessionIDs.get();
   }
   
   
  // Get the USER object using the implied thread associated sessionID
  // Thought here is the sessionid is set prior to other calls being made to
  // service object (so jax/caucho servlet would need to seed the value prior
  // to each call).
   protected User getUserEx() throws DboxException {
      return getUserEx(getThreadSessionID());
   }
   
  // Not only check that the sessionid token is valid by itself, check in
  //  the DB to make sure its a valid session
   protected User getUserEx(String sessionid) throws DboxException {
      if (sessionid == null) throw new DboxException("Missing sessionID for call");
      try {
         ODCipherData cd = getCipher().decode(sessionid);
         
         if (!cd.isCurrent()) {
            throw new DboxException("SessionID has expired");
         } else {
            String ldata = cd.getString();
            ConfigObject co = new ConfigObject();
            co.fromString(ldata);
            
            User user = new User();
            user.setName(co.getProperty("USER"));
            user.setCompany(co.getProperty("COMPANY"));
            user.setCountry(co.getProperty("COUNTRY"));
            user.setEmail(co.getProperty("EMAIL"));
            user.setIBMDept(co.getProperty("IBMDEPT"));
            user.setIBMDiv(co.getProperty("IBMDIV"));
            user.setSessionId(co.getLongProperty("SESSIONID", -1));
            
            try {
               packageMgr.validateSession(user);
            } catch(DboxException dbe) {
               throw new DboxException(
                  "Session ID token is valid, but session has been closed", dbe);
            }
            
            user.setTokenLogin(co.getBoolProperty("TOKENLOGIN", false));
            user.setDoProjectSend(co.getBoolProperty("DOPROJSEND", 
                                                     user.getDoProjectSend()));
            
           /* JMC 1/31/07 - This comes from validate now 
            Vector v = co.getSection("PROJECTS");
            if (v.size() > 0) { 
               ConfigSection cs = (ConfigSection)v.elementAt(0);
               Enumeration enum = cs.getPropertyNames();
               while(enum.hasMoreElements()) {
                  user.addProject((String)enum.nextElement());
               }
            }
           */
            
           // Setup some MDC info
            MDC.put("CMD",       "DROPBOX");
            MDC.put("USER",      user.getName());
            MDC.put("COMPANY",   user.getCompany());
            MDC.put("EMAIL",     user.getEmail());
            MDC.put("SESSIONID", ""+user.getSessionId());
            
           // Set some alert info ... Perhaps use NDC info
            String alertinfo = "userid  = " + user.getName() + "\n" + 
               "company = " + user.getCompany() + "\n" + 
               "email   = " + user.getEmail();
            DboxAlert.setAlertInfo(alertinfo);
            
            return user;
         }
      } catch(DecodeException de) {
         throw new DboxException("Sessionid decode exception");
      } catch(DboxException dbe) {
         throw dbe;
      } catch(Exception ee) {
         throw new DboxException("Unexpected exception", ee);
      }
   }
   
   public User getUserFromToken(String token) throws Exception {
      User ret = null;
      Vector projs = null;
      String error = null;
      String user  = null;
      String country = null;
      String company = null;
      String email   = null;
      try {
         
         if (token == null) {
            throw new DboxException("Invalid token for login ... null string");
         }
         
         Hashtable hash = SearchEtc.dataFromToken(getCipher(), token);
         if (hash != null) {
         
            error = (String)hash.get("ERROR");
            if (error == null) {
               projs = new Vector();
               user = (String)hash.get("EDGEID");
               if (user != null) user = user.toLowerCase();
               country = (String)hash.get("COUNTRY");
               company = (String)hash.get("COMPANY");
               email   = (String)hash.get("EMAIL");
               if (user == null) {
                  error = "userid not in token";
               } else {
                  
                  int cnt=1;
                  String v = null;
                  while((v=(String)hash.get("P"+cnt)) != null) {
                     projs.addElement(v);
                     cnt++;
                  }
                  ret = new User(user, projs, -1);
                  ret.setCompany(company);
                  ret.setCountry(country);
                  ret.setEmail(email);
               }
            } 
         } else {
            error = "Error parsing token - null hash";
         }
      } catch(DboxException dbe) {
         throw dbe;
      } catch(Exception ee) {
         error = "Error parsing token";
      }
      
      if (error != null) {
         throw new DboxException(error);
      }
      
      return ret;
   }
   
  // 20 minutes of validity ... that gives 10 min to half way (which is when
  //  clients are going to try to re-up).
   final static int SECONDS_OF_ID_VALIDITY = 60*20;
   protected HashMap makeSessionMap(User user) throws DboxException {
      try {
         ConfigObject co = new ConfigObject();
         
         String name     = user.getName();
         String company  = user.getCompany();
         String country  = user.getCountry();
         String email    = user.getEmail();
         String ibmdept  = user.getIBMDept();
         String ibmdiv   = user.getIBMDiv();
         
         co.setProperty("USER",    name != null ? name : "");
         co.setProperty("COMPANY", company != null ? company : "");
         co.setProperty("COUNTRY", country != null ? country : "");
         co.setProperty("EMAIL",   email != null ? email : "");
         co.setProperty("IBMDEPT", ibmdept != null ? ibmdept : "");
         co.setProperty("IBMDIV",  ibmdiv != null ? ibmdiv : "");
         co.setLongProperty("SESSIONID", user.getSessionId());
         co.setBoolProperty("TOKENLOGIN", user.getTokenLogin());
         co.setBoolProperty("DOPROJSEND", user.getDoProjectSend());
         
        /* JMC 1/31/07 - Let the validate suck this in)
         Vector v = user.getProjects();
         if (v.size() > 0) { 
            ConfigSection cs = new ConfigSection("PROJECTS");
            Enumeration enum = v.elements();
            while(enum.hasMoreElements()) {
               cs.setProperty((String)enum.nextElement(), "");
            }
            co.addSection(cs);
         }
        */
         
         ODCipherRSA cipher = getCipher();
         ODCipherData cd    = cipher.encode(SECONDS_OF_ID_VALIDITY, co.toString());
         HashMap map = new HashMap();
         
         String sessid = cd.getExportString();
         
         long msSince70 = ((long)cd.getSecondsSince70())*1000L;
         map.put(SessionID, sessid);
         map.put(Expiration, new Long(msSince70));
         map.put(SessionTTL, new Long(SECONDS_OF_ID_VALIDITY));
         map.put(User, name);
         map.put(Company, company);
         
        // Set the expiration time into session
         packageMgr.setSessionExpiration(user, new Date(msSince70));
         
         return map;
      } catch(Exception e) {
         log.warn(e);
         throw new DboxException("Error creating Session map");
      }
   }
   
      
   protected HashMap finishSessionCreate(User userObj) throws DboxException {
   
      try {
         
        // If we are doing AMT Project checking, then get projects and
        //  entitlements associated with this goon. This also gets the
        //  bluepages info into the User object
         try {
         
           // Add checking of entitlement to the service TODOTODOTODOTOD
         
            Vector amtvec = UserRegistryFactory.getInstance().lookup(
               userObj.getName(), false, true, doAmtprojects());
           //Vector amtvec = AMTQuery.getAMTByUser(userObj.getName(),
           //                                       doAmtprojects(), 
           //                                       doAmtprojects(), null);

            if (amtvec != null && amtvec.size() > 0) {
               if (amtvec.size() == 1) {
                  AMTUser amtuser = (AMTUser)amtvec.elementAt(0);
                  if (doAmtprojects()) {
                     userObj.addProjects(amtuser.getProjects());
                     userObj.setIBMDept(amtuser.getIBMDept());
                     userObj.setIBMDiv(amtuser.getIBMDiv());
                     
                    // If reloadable prop for limiting project participants has changed, 
                    //  fix up this User
                    //
                    // Note that the first time tryReload is called, it will indeed reload
                    //  the file, so will come into this section to init things the first time
                     if (limitSendByProject != null) {
                        
                       // To allow it to be used by some ... sigh
                        String name = userObj.getName();
                        String canhe = limitSendByProject.getPropertyNoReload(name, "false");
                       // If not limited, then he can use it
                        if (canhe.equalsIgnoreCase("true")) {
                           userObj.setDoProjectSend(true);
                        } else {
                           userObj.setDoProjectSend(false);
                        }
                     }                     
                  }
                  
                 // If token provided info is null, use AMT info
                  if (userObj.getEmail() == null) {
                     userObj.setEmail(amtuser.getEmail());
                  }
                  if (userObj.getCompany() == null) {
                     userObj.setCompany(amtuser.getCompany());
                  }
                  if (userObj.getCountry() == null) {
                     userObj.setCountry(amtuser.getCountry());
                  }
                  
                 // Set that he is certified
                  if (amtuser.isEntitled(getITAREntitlement())) {
                     userObj.setUserItarCertified(true);
                  }
                  
                 // Check for entitlement
                  if (failIfNotEntitled() && !amtuser.isEntitled("DSGN_CONF")) {
                     throw new DboxException("User is not entitled for dropbox");
                  }
                  
               } else {
                  throw new DBException("User has multiple entries in registry: " + 
                                        userObj.getName());
               }
            } else {
               throw new DBException("User not found in registry: " + 
                                     userObj.getName());
            }
         } catch(DBException dbe) {
            log.warn("Error getting AMTUser record for " +
                     userObj.getName() +
                     " and doing AMT!. No projectlist update");
            log.warn(dbe);
            if (requireLoginAMTCheck()) {
               throw dbe;
            }
         }
            
         log.info((userObj.getTokenLogin()?"Token ": "User/PW ") + "login for [" + 
                  userObj.getName() + 
                  "] Company[" + 
                  userObj.getCompany() +
                  "] Email[" + 
                  userObj.getEmail() + "]");      
      
        /*
        // Cheap way to get debug turned on      TODOTODOTODOTODO  
         File f = new File(forcedropboxpath() + "/" + userObj.getName());
         if (f.exists()) {
            log.warn("DEBUGforced on by entry in " + forcedropboxpath());
            Debug.setDebug(true);
            DebugPrint.setLevel(DebugPrint.DEBUG3);
         }
        */
      
         packageMgr.openSession(userObj);
         return makeSessionMap(userObj);
      
      } catch(DboxException dbe2) {
         log.error("Error doing openSession for " +
                   userObj.toString());
         log.error(dbe2);
         throw dbe2;
      } catch(Exception ee) {
         log.error("Error doing openSession for " +
                   userObj.toString());
         log.error(ee);
         throw new DboxException("Error doing openSession", ee);
      }
   }
   
   public HashMap  createSession(String token) 
      throws DboxException, RemoteException {
         
     // Create the session
      User userObj = null;
      try {
      
         userObj = getUserFromToken(token);
         if (userObj == null) {
            throw new DboxException("Error creating User object from token");
         }
         
      } catch(DboxException dbe) {
         throw dbe;
      } catch(Exception ee) {
         log.error("Error doing openSession for " +
                   (userObj!= null?userObj.toString():token));
         log.error(ee);
         throw new DboxException("Error opening session", ee);
      }
         
      userObj.setTokenLogin(true);
      
      return finishSessionCreate(userObj);
   }
   
   public HashMap createSession(String userid, 
                                String password) 
      throws DboxException, RemoteException {
      
      try {
         
         String token = Misc.getConnectInfoGeneric(userid, password, "XFR", null, 
                                                   getProperty("dropbox.authenticationURL"));
         if (token == null) {
            throw new DboxException("Unsuccessful authentication. Userid/password incorrect/invalid");
         }
         
         return createSession(token);
         
      } catch(DboxException dbe) {
         throw dbe;
      } catch(Exception e) {
         log.warn(e);
         throw new DboxException("Error creating Session", e);
      }
   }
   
   public HashMap refreshSession() 
      throws DboxException, RemoteException {
      
      User user = getUserEx();
      log.info("refreshSession: " + user.toString());
      return makeSessionMap(user);
   }
   
   public void closeSession() 
      throws DboxException, RemoteException {
      
      User user = getUserEx();
      log.info("closeSession: " + user.toString());
      
      packageMgr.closeSession(user);
   }
   
   
  /* -------------------------------------------------------*\
  ** AMT variables and routines - Returns AclInfo objects
  \* -------------------------------------------------------*/
   protected Vector assertAMTChecks(User user, Vector acls, boolean needitar) 
      throws DboxException, RemoteException {
      
      Enumeration enum = acls.elements();
      Vector ret = new Vector();
      while(enum.hasMoreElements()) {
         Object obj = enum.nextElement();
         Vector v = null;
         boolean isaclinfo = obj instanceof AclInfo;
         if (isaclinfo) {
            AclInfo ainfo = (AclInfo)obj;
            if (ainfo.getAclStatus() == DropboxGenerator.STATUS_NONE) {
               if (ainfo.getAclName().equals("*")) {
                  if (!ret.contains(ainfo)) {
                     ret.addElement(ainfo);
                  }
               } else {
                 // This returns vector of AMTUser objects
                  v = assertAMTCheck(user, ainfo.getAclName(), needitar, false, false);
               }
            } else {
               if (!ret.contains(ainfo)) {
                  ret.addElement(ainfo);
               }
            }
         } else {
           // This returns vector of AMTUser objects
            v= assertAMTCheck(user, (String)obj, needitar, false, false);
         }
         
         if (v != null && v.size() > 0) {
            Enumeration enum2 = v.elements();
            while(enum2.hasMoreElements()) {
               AMTUser amtuser = (AMTUser)enum2.nextElement();
               AclInfo ainfo = new AclInfo(); 
               
               ainfo.setAclType(DropboxGenerator.STATUS_NONE);
               ainfo.setAclName(amtuser.getUser());
               ainfo.setAclCompany(amtuser.getCompany());
               
               if (!ret.contains(ainfo)) {
                  ret.addElement(ainfo);
               }
            }
         }
      }
      return ret;
   }
      
  // vecin: Incoming is current vector of user strings
  // acl  : Incoming Acl being flattened/added to return vector
  //
  // Return a vector of User strings (flattened from acls). If vecin was
  //  non-null, just adding to that vec, otherwise, new vec.  
  // 
   protected Vector flattenACLToUsers(Vector vecin, 
                                      AclInfo acl) 
      throws DboxException { 
      
      Vector ret = vecin;
      if (ret == null) ret = new Vector();
      if (acl.getAclStatus() == DropboxGenerator.STATUS_NONE) {
         if (!ret.contains(acl.getAclName())) {
            ret.addElement(acl.getAclName());
         }
      } else if (acl.getAclStatus() == DropboxGenerator.STATUS_GROUP) {
         GroupInfo ginfo = packageMgr.getGroup(acl.getAclName(), 
                                               true, false);
         Enumeration menum = ginfo.getGroupMembers().elements();
         while(menum.hasMoreElements()) {
            String member = (String)menum.nextElement();
            if (!ret.contains(member)) {
               ret.addElement(member);
            }
         }
      } else if (acl.getAclStatus() == DropboxGenerator.STATUS_PROJECT) {
         try {
           //Vector newvec = AMTQuery.getUsersHavingProject(acl.getAclName());
            Vector newvec = 
               UserRegistryFactory.getInstance().lookupUsersWithProject(
                  acl.getAclName());
            if (newvec != null) {
               Enumeration penum = newvec.elements();
               while(penum.hasMoreElements()) {
                  Object o = penum.nextElement();
                  String member = null;
                 // My testregistry is wrong ... just check AMTUser here as well as String
                  if (o instanceof String) {
                     member = (String)o;
                  } else {
                     member = ((AMTUser)o).getUser();
                  }
                  if (!ret.contains(member)) {
                     ret.addElement(member);
                  }
               }
            }
         } catch(DBException dbe) {
            DebugPrint.printlnd(DebugPrint.WARN, 
                                "Error flattening ACL to User: " + 
                                acl.toString());
            DebugPrint.printlnd(DebugPrint.WARN, dbe);
            throw new DboxException(dbe.getMessage(), 0);
         }
      }
      return ret;
   }
      
  // Return a vector of User strings
   protected Vector flattenACLsToUsers(Vector acls) 
      throws DboxException {
      
      Vector ret = new Vector();
      Enumeration enum = acls.elements();
      while(enum.hasMoreElements()) {
         AclInfo acl = (AclInfo)enum.nextElement();
         flattenACLToUsers(ret, acl);
      }
      return ret;
   }
    
  // Return a vector of AMTUser objects which are valid to receive a package
  //  from specified user. Groups and projects are assumed to be valid
   protected Vector flattenACLsToUsersAMT(User user, 
                                          Vector acls, 
                                          boolean needitar,
                                          boolean retstrings)
      throws DboxException {
      
      Vector users = flattenACLsToUsers(acls);
      Vector ret  = new Vector();
      Iterator it = users.iterator();
      while(it.hasNext()) {
         String name = (String)it.next();
         try {
            Vector lacls = assertAMTCheck(user, name, needitar, true, retstrings);
            if (lacls != null && lacls.size() > 0) {
               Object uors = lacls.firstElement();
               if (!ret.contains(uors)) {
                  ret.add(uors);
               }
            }
         } catch(Exception e) {
         }
      }
      return ret;
   }
    
    
  // Returns AMTUser object(s) represented by 'name' which pass the
  //  visibility standards of User
   protected Vector assertAMTCheck(User user, String name, 
                                   boolean needitar, 
                                   boolean quiet,
                                   boolean retstrings) 
      throws DboxException, RemoteException {
      
      Vector ret = null;
      try {
         boolean didByEmail = false;
         
        // Get user record, no entitlements or projects needed
        //Vector amtusers = AMTQuery.getAMTByUser(name);
         Vector amtusers  = 
            UserRegistryFactory.getInstance().lookup(name, false, true, false);
         if ((amtusers == null || amtusers.size() == 0)) {
            
            if (allowByEmail() && name.indexOf('@') >= 0) {
              //amtusers = AMTQuery.getAMTByEmail(name);
               amtusers = 
                  UserRegistryFactory.getInstance().lookupByEmail(name, false, 
                                                                  true, false);
               didByEmail = true;
            } 
         }
         
         if (amtusers == null || amtusers.size() == 0) {
            if (complainReceiverNoExist()) {
               throw new DboxException("Not valid ACL: " + name, 0);
            }
            return new Vector();
         }
         
         String ucompany = user.getCompany();
         String uuser    = user.getName();
         
         String errormsg = null;
         
         boolean ok = false;
         Enumeration enum = amtusers.elements();
         while(enum.hasMoreElements()) {
            AMTUser amtuser = (AMTUser)enum.nextElement();
            String ncompany = amtuser.getCompany();
            String nuser    = amtuser.getUser();
            
            if (ucompany == null) ucompany = "";
            else                  ucompany = ucompany.trim();
            if (ncompany == null) ncompany = "";
            else                  ncompany = ncompany.trim();
            
            if (didByEmail) {
               if (!quiet) {
                  if (byEmailToIBMONLY() && !ncompany.equals("IBM")) {
                     log.error("byEmail lookup for " + name + 
                               " mapped to non-IBM ID:\n" + amtuser.toString());
                  } else {
                     log.info("byEmail lookup for " + name + 
                              " mapped to ID:\n" + amtuser.toString());
                  }
               }
               continue;
            }
                        
            if (packageMgr.allowsPackageReceipt(uuser, ucompany, 
                                                nuser, ncompany)){
                                                
              // Check for entitlement
               if (!amtuser.isEntitled("DSGN_CONF")) {
                  errormsg ="User is not entitled for dropbox access: " + name;
                  continue;
               }
                                                
              // If no ITAR, or this guy IS ITAR enabled ... take it
               if (!needitar || amtuser.isEntitled(getITAREntitlement())) {
                  ok = true;
                  if (ret == null) ret = new Vector();
                  Object toret = amtuser;
                  if (retstrings) toret = amtuser.getUser();
                  if (!ret.contains(toret)) {
                     ret.addElement(toret);
                  }
               } else {
                  errormsg ="User is not entitled to receive ITAR data: " + name;
                  if (!quiet) {
                     log.info("User assertion check failed for ITAR reasons: User = " + user.getName() + " checkuser = " + nuser);
                  }
               }
            }
         }
         
         if (!ok) {
            if (errormsg == null) {
               errormsg = "Not a valid recipient: " + name;
            }
            throw new DboxException(errormsg, 0);
         }
         
      } catch(NoSuchElementException nsee) {
         log.warn("Error while asserting check as " + user.getName() + 
                  " for " + name);
         DboxAlert.alert(nsee);
         throw new DboxException("Unexpected IndexError while validating: " +
                                 name, 0);
      } catch(DboxException dboxe) {
         throw dboxe;
      } catch(DBException dbe) {
         DboxAlert.alert(dbe);
         throw new DboxException("Unexpected AMT error while validating: " +
                                 name, 0);
      } catch(Throwable tt) {
         log.warn("Error while asserting check as " + user.getName() + 
                  " for " + name);
         DboxAlert.alert(tt);
         throw new DboxException("Unexpected error while validating: " +
                                 name, 0);
      }
      return ret;
   }
   
   public class AMTData {
      public AMTData(AMTUser u, DboxPackageInfo p, String n, byte t) {
         user  = u;
         pinfo = p;
         name  = n;
         type  = t;
      }
      public AMTUser         user;
      public DboxPackageInfo pinfo;
      public String          name;
      public byte            type;
   }
   
  // Sending mail to lots of IDs really slows things down ... thread it
   public class AMTMailer implements Runnable {
   
      Thread me = null;
      Vector todo = new Vector();
   
      public void run() {
      
         try {
            while(me == Thread.currentThread()) {
               
               Enumeration enum = null;
               synchronized (todo) {
                  if (todo.size() > 0) {
                     enum = (Enumeration)((Vector)(todo.clone())).elements();
                     todo.removeAllElements();
                  }
               }
               
               if (enum != null) {
                  while(enum.hasMoreElements()) {
                     AMTData d = (AMTData)enum.nextElement();
                     sendAMTMailNoThread(d.user, d.pinfo, d.name, d.type);
                  }
               }
               
              // If we are done, just bag out
               synchronized (todo) {
                  if (todo.size() == 0) {
                     return;
                  }
               }
            }
         } finally {
            synchronized(todo) {
               me = null;
            }
         }
      }
      
      public void startThread() {
         synchronized(todo) {
            if (me == null) {
               me = new Thread(this);
               me.start();
            }
         }
      }
      
      public void stopThread() {
         Thread curthread = null;
         synchronized(todo) {
            curthread = me;
         }
         if (curthread != null) {
            while(true) {
               try {
                  curthread.join();
                  break;
               } catch(InterruptedException ie) {}
            }
         }
      }
   
     // Notification with ACL list AMTUser
      public void sendAMTMail(AMTUser user, 
                              DboxPackageInfo pinfo, 
                              Vector acls) {
                              
         Enumeration enum = acls.elements();
         while(enum.hasMoreElements()) {
            Object obj = enum.nextElement();
            if (obj instanceof AclInfo) {
               AclInfo ainfo = (AclInfo)obj;
               if (ainfo.getAclStatus() == DropboxGenerator.STATUS_NONE) {
                  sendAMTMail(user, pinfo, ainfo.getAclName());
               }
            } else {
               sendAMTMail(user, pinfo, (String)obj);
            }
         }
      }
      
     // Notification with name - AMTUser
      public void sendAMTMail(AMTUser user, DboxPackageInfo pinfo, String name) {
         synchronized(todo) {
            todo.addElement(new AMTData(user, pinfo, name, NotificationMail));
            startThread();
            todo.notifyAll();
         }
      }
      
     // RR name - AMTUser
      public void sendAMTMailRR(AMTUser user, DboxPackageInfo pinfo, String name) {
         synchronized(todo) {
            todo.addElement(new AMTData(user, pinfo, name, ReturnReceiptMail));
            startThread();
            todo.notifyAll();
         }
      }
            
     // Nag name - AMTUser
      public void sendAMTMailNag(AMTUser user, DboxPackageInfo pinfo, String name) {
         synchronized(todo) {
            todo.addElement(new AMTData(user, pinfo, name, NagMail));
            startThread();
            todo.notifyAll();
         }
      }
      
     // User versions ... call AMTUser versions. Only user/company needed
      public void sendAMTMail(User user, 
                              DboxPackageInfo pinfo, 
                              Vector acls) {
         AMTUser amtuser = new AMTUserInst();
         amtuser.setUser(user.getName());
         amtuser.setCompany(user.getCompany());
         amtuser.setEmail(user.getEmail());
         
         sendAMTMail(amtuser, pinfo, acls);
      }
      public void sendAMTMail(User user, 
                              DboxPackageInfo pinfo, 
                              String name) {
         AMTUser amtuser = new AMTUserInst();
         amtuser.setUser(user.getName());
         amtuser.setCompany(user.getCompany());
         amtuser.setEmail(user.getEmail());
         
         sendAMTMail(amtuser, pinfo, name);
      }
      public void sendAMTMailRR(User user, 
                                DboxPackageInfo pinfo, 
                                String name) {
         AMTUser amtuser = new AMTUserInst();
         amtuser.setUser(user.getName());
         amtuser.setCompany(user.getCompany());
         amtuser.setEmail(user.getEmail());
         
         sendAMTMailRR(amtuser, pinfo, name);
      }
      public void sendAMTMailNag(User user, 
                                 DboxPackageInfo pinfo, 
                                 String name) {
         AMTUser amtuser = new AMTUserInst();
         amtuser.setUser(user.getName());
         amtuser.setCompany(user.getCompany());
         amtuser.setEmail(user.getEmail());
         
         sendAMTMailNag(amtuser, pinfo, name);
      }
      
            
      public String getSmartSize(long sz) {
         String szS = null;
         DecimalFormat df = new DecimalFormat("0.00");
         if (sz > 1024*1024*1024) {
            szS = df.format(((float)sz)/(1024*1024*1024)) + " GB";
         } else if (sz > 1024*1024) {
            szS = df.format(((float)sz)/(1024*1024)) + " MB";
         } else if (sz > 1024) {
            szS = df.format(((float)sz)/(1024)) + " KB";
         } else {
            szS = sz + " bytes";
         }
         return szS;
      }
      
      public String getSmartTime(long secs) {
         long days = secs/(60*60*24);
         secs -= days*60*60*24;
         long hrs  = secs/(60*60);
         secs -= hrs*60*60;
         long mins = secs/60;
         secs -= mins*60;
         
         String elap = "";
         if (days > 0) elap += "" + days + " day ";
         if (hrs  > 0) elap += "" + hrs  + " hr ";
         if (mins > 0) elap += "" + mins + " min ";
         if (secs > 0 || (days == 0 && hrs == 0 && mins == 0))
            elap += "" + secs + " sec ";
         return elap;
      }
      
      String strpad = 
   "                                                                                                                                                                                                                                         ";
   
      public String pad(String s, int len) { return pad(s, len, true); }
      public String pad(String s, int len, boolean leftJust) {
         if (s == null || s.length() == 0) s = "-";
//      if (s == null) return strpad.substring(0, len);
         
         int lpad = len - s.length();
         if (lpad == 0) return s;
         if (lpad > 0) {
            String lpadS = strpad.substring(0, lpad);
            if (leftJust) return s + lpadS;
            else          return lpadS + s;
         }
         
         if (len > 0) {
            return s.substring(0, len-1) + ">";
         }
         
         return "";
      }
      
     /*
     ** Substitutions take the form of %varname%. The substitution can be modified
     **  by a prefix:  %20%varname%. This would limit the output field to 20 chars.
     **  The prefix is [-]maxsize[.minsize]  If minsize is specified, the - leader 
     **  specifies the justification (- is left, default is right)
     */
      class Substruct {
         public  int     sidx;
         public  int     len;
         public  int     maxsize;
         public  int     minsize;
         public  boolean leftjustified;
         public  String  body;
         
         public void reset() {
            body = null;
            sidx = -1;
            len  = -1;
            maxsize=0x7fffffff;
            minsize=0;
            leftjustified=false;
         }
         
         public boolean find(String inbody, String marker) {
            boolean ret = false;
            reset();
            body = inbody;
            sidx = body.indexOf(marker);
            if (sidx >= 0) {
               ret = true;
               len = marker.length();
               if (sidx > 0) {
                  int pidx = body.lastIndexOf('%', sidx-1);
                  if (pidx >= 0) {
                     int plen =  sidx-pidx;
                     if (plen > 1 && plen < 12) {
                     
                       //System.out.println("Doing subsize!!");
                       //System.out.println(body.substring(pidx+1, sidx));
                     
                        char arr[] = body.substring(pidx+1, sidx).toCharArray();
                        int ai = 0;
                        switch(arr[ai++]) {
                           case '-':  leftjustified = true;   break;
                           case '+':  leftjustified = false;  break;
                           default:   ai--;                   break;
                        }
                        
                        maxsize = minsize = 0;
                        
                        while(ai < arr.length) {
                           if (arr[ai] != '.') {
                              int dig = Character.digit(arr[ai], 10);
                             //     System.out.println("Got digit: " + dig);
                              if (dig != -1) {
                                 maxsize *= 10;
                                 maxsize += dig;
                              } else {
                                 maxsize = 0x7fffffff; 
                                 return ret;
                              }
                             //System.out.println("maxsize = " + maxsize);
                              ai++;
                           } else {
                              ai++;
                              break;
                           }
                        }
                        
                        while(ai < arr.length) {
                           if (arr[ai] != '.') {
                              int dig = Character.digit(arr[ai], 10);
                             //System.out.println("Got digit: " + dig);
                              if (dig != -1) {
                                 minsize *= 10;
                                 minsize += dig;
                              } else {
                                 maxsize = 0x7fffffff; 
                                 minsize = 0;
                                 return ret;
                              }
                             //System.out.println("minsize = " + minsize);
                              ai++;
                           } else {
                              ai++;
                              break;
                           }
                        }
                        
                       // Have a valid max/min specified, take it
                        sidx = pidx;
                        len += plen;
                     }
                  }
               }
            } else {
               body = null;
            }
            return ret;
         }
         
         public String substitute(String val, boolean elipse) {
            if (body != null) {
               String newbody = body.substring(0,sidx);
               
               if (val == null) val = "";
               int l = val.length();
               if (maxsize < 1) maxsize = 1;
               if (minsize > maxsize) minsize = maxsize;
               if (l > maxsize) {
                  if (!elipse || maxsize <= 3) {
                     val = val.substring(0, maxsize);
                  } else {
                     val = val.substring(0, maxsize-3) + "...";
                  }
               }
               
               l = val.length();
               if (l < minsize) {
                  val = pad(val, minsize, leftjustified);
               }
               
               newbody += val;
               
               if (body.length() > len) {
                  newbody += body.substring(sidx+len);
               }
               body=newbody;
            }
            return body;
         }
      }
      
      String substituteVariables(String body, DboxPackageInfo pinfo,
                                 String uuser, String ucompany,
                                 String nuser, String ncompany,
                                 AMTUser amtuser) 
      throws DboxException, RemoteException {
      
         int idx;
         String check;
         Substruct sub = new Substruct();
         
         check = "%ownerid%";
         while(sub.find(body, check)) body = sub.substitute(uuser, false);
         
         check = "%ownercompany%";
         while(sub.find(body, check)) body = sub.substitute(ucompany, false);
         
         check = "%sendtoid%";
         while(sub.find(body, check)) body = sub.substitute(nuser, false);
         
         check = "%sendtocompany%";
         while(sub.find(body, check)) body = sub.substitute(ncompany, false);
         
         check = "%emailsenderaddr%";
         while(sub.find(body, check)) body = sub.substitute(senderAddr(), false);
         
         check = "%launchurl%";
         while(sub.find(body, check)) body = sub.substitute(getLaunchURL(), false);
         
         check = "%packagedesc%";
         while(sub.find(body, check)) 
            body = sub.substitute(pinfo.getPackageDescription(), false);
         
         check = "%packagename%";
         while(sub.find(body, check)) body = sub.substitute(pinfo.getPackageName(),
                                                            true);
         
         check = "%commitdate%";
         String  cdate = null;
         while(sub.find(body, check)) {
            if (cdate == null) cdate = new Date(pinfo.getCommitTime()).toString();
            body = sub.substitute(cdate, false);
         }
         
         check = "%currentdate%";
         while(sub.find(body, check)) {
            if (cdate == null) cdate = new Date().toString();
            body = sub.substitute(cdate, false);
         }
         
         check = "%smartsize%";
         String szS = null;
         while(sub.find(body, check)) {
            if (szS == null) szS = getSmartSize(pinfo.getPackageSize());
            body = sub.substitute(szS, false);
         }
         
         check = "%t1download%";
         String elap = null;
         while(sub.find(body, check)) {
            if (elap == null) {
               long secs = ((pinfo.getPackageSize()*8)/((1024/2)*1024*3));
               
              // 2 seconds overhead to startup each xfer
               secs += pinfo.getPackageNumElements()*2;
               
               elap = getSmartTime(secs);
            }
            body = sub.substitute(elap, false);
         }
         
         check = "%56kdownload%";
         elap = null;
         while(sub.find(body, check)) {
            if (elap == null) {
               long secs = (pinfo.getPackageSize()*8)/(56*1024);
               
              // 2 seconds overhead to startup each xfer
               secs += pinfo.getPackageNumElements()*2;
               
               elap = getSmartTime(secs);
            }
            body = sub.substitute(elap, false);
         }
         
         check = "%packexpires%";
         while(sub.find(body, check)) {
            body = sub.substitute((new Date(pinfo.getPackageExpiration())).toString(),
                                  false);
         }
         
         check = "%sftpdropbox%";
         while(sub.find(body, check)) body = sub.substitute(sftpdropbox(), false);
         
         check = "%tunneldropbox%";
         while(sub.find(body, check)) body = sub.substitute(tunneldropbox(), false);
            
        // This is directed download
         check = "%htmldownloadinfo%";
         while(sub.find(body, check)) {
            body = sub.substitute(doHtml()?htmlDownloadInfo():"", false);
         }
            
        // This is web dropbox
         check = "%webdownloadinfo%";
         while(sub.find(body, check)) {
            body = sub.substitute(doWebdropbox()?webDownloadInfo():"", false);
         }
            
        // MUST be after webdownloadinfo
         check = "%weblaunchurl%";
         while(sub.find(body, check)) {
            body = sub.substitute(getWebLaunchURL() + ":p:" + 
                                  pinfo.getPackageId(), false);
         }
            
         check = "%packagecontents%";
         String str = null;
         while(sub.find(body, check)) {
            if (str == null) {
               
               str = "\n";
                  
              // Add file contents
               Vector files = pinfo.getFiles();
               if (files != null) {
                  Enumeration lenum = files.elements();
                  while(lenum.hasMoreElements()) {
                     DboxFileInfo finfo = (DboxFileInfo)lenum.nextElement();
                     String fname = finfo.getFileName();
                     long   fsize = finfo.getFileSize();
                        
                     szS = getSmartSize(fsize);
                     if (doHtml() && fsize <= 0x7fffffff) {
                        String scope = 
                           "webox:op:d:p:" +
                           pinfo.getPackageId() + ":f:" + finfo.getFileId();
                              
//                           str += "   " + szS + "\t " + 
//                              "<A href=" + ESS+downloadFileURL + scope + 
//                              ">" + fname + "</A>\n";

                        str += pad(szS, 15, false) + " " + fname + 
                           "\n" + pad(" ", 16) + "Web download: " + 
                           getDownloadFileURL() + scope + "\n\n\n";
                     } else {
                        str += pad(szS, 15, false) + " " + fname + "\n";
                     }
                  }
               }
            }
                  
            body = sub.substitute(str, false);
         }
            
         check = "%packagecontentsRR%";
         str = null;
         while(sub.find(body, check)) {
            if (str == null) {
               
               str = "";
                  
              // Add file contents
               Vector files = pinfo.getFiles();
               if (files != null) {
                  Enumeration lenum = files.elements();
                  while(lenum.hasMoreElements()) {
                     DboxFileInfo finfo = (DboxFileInfo)lenum.nextElement();
                     String fname = finfo.getFileName();
                     long   fsize = finfo.getFileSize();
                     szS = getSmartSize(fsize);
                     String dates = null;
                     String xferS = null;
                     if (pinfo instanceof DB2DboxPackageInfo) {
                        DB2DboxPackageInfo pidb2 = (DB2DboxPackageInfo)pinfo;
                        AclInfo ainfo = 
                           pidb2.getFileAccessRecord(nuser,
                                                     finfo.getFileId());
                        log.info(ainfo.toString());
                        if (ainfo != null) {
                           Date ldate = new Date(ainfo.getAclCreateTime());
                           dates = ldate.toString();
                           xferS = getSmartSize(ainfo.getXferRate())+"/sec";
                              
                        }
                     }
                     str += "  " +
                        pad(dates, 28, false) + " " +
                        pad(xferS, 16, false) + " " +
                        pad(szS,   12, false) + " " + 
                        fname + "\n";
                  }
               }
            }
                  
            body = sub.substitute(str, false);
         }
         return body;
      }
      
      public void sendAMTMailNoThread(AMTUser user, 
                                      DboxPackageInfo pinfo, 
                                      String name, 
                                      byte type) {
         
         String emailaddr = "Not Found";
         
         boolean returnreceipt = type == ReturnReceiptMail;
         
         String notificationtype = returnreceipt ? "ReturnReceipt"
                                                 : ((type == NagMail) ? 
                                                    "NagMail" : "Notification");
         
         try {
            
            String ucompany     = user.getCompany();
            String uuser        = user.getUser();
            
            if (ucompany == null) ucompany = "";
            else                  ucompany = ucompany.trim();
            
            String body         = null;
            String lmailSubject = null;
            
           // This method does Notification emails as well as return receipt
            if (returnreceipt) {
            
              // Mail subject has User name/company, which is person
              //  who just finished download at this point
               lmailSubject = mailSubjectRR();
               
              // For RR, we are sending to Package Owner
               name = pinfo.getPackageOwner();
               
               body = mailBodyRR();
               
              // Set u-variable to be package owner now
               ucompany = pinfo.getPackageCompany();
               uuser    = pinfo.getPackageOwner();
               
            } else if (type == NagMail) {
               lmailSubject = mailSubjectNag();
               body = mailBodyNag();
            } else {
              // Assume its NotificationMail
              // u-vars already set to package owner (thats who User is here)
               lmailSubject = mailSubject();
               body = mailBody();
            }
            
           // Get user record, no entitlements or projects needed
            AMTUser amtuser = null;
            
           // n vars will be sendto id (recipient of package)
            String ncompany = null;
            String nuser    = null;
            
            
           //Vector amtusers = AMTQuery.getAMTByUser(name, false, false, null);
            Vector amtusers = 
               UserRegistryFactory.getInstance().lookup(name, false, true, false);
            
            if (amtusers != null) {
               Enumeration enum = amtusers.elements();
               while(enum.hasMoreElements()) {
                  AMTUser lamtuser = (AMTUser)enum.nextElement();
                  
                  ncompany = lamtuser.getCompany();
                  nuser    = lamtuser.getUser();
                  
                  if (ncompany == null) ncompany = "";
                  else                  ncompany = ncompany.trim();
                  
                  if (returnreceipt) {
                     if (ucompany.equals(ncompany) && uuser.equals(nuser)) {
                        amtuser = lamtuser;
                        
                       // K, change n-vars to be package recipient
                        nuser = user.getUser();
                        ncompany = user.getCompany();
                        if (ncompany == null) ncompany = "";
                        else                  ncompany = ncompany.trim();
                        
                        break;
                     }
                  } else {
                     if (packageMgr.allowsPackageReceipt(uuser, ucompany, 
                                                         nuser, ncompany)) {
                        amtuser = lamtuser;
                        break;
                     }
                  }
               }
            }
               
           // If we did not pass the assertion check, don't send mail
            if (amtuser == null) {
               if (returnreceipt) {
                  DboxAlert.alert(2, "ReturnReceipt Lookup failed", 0,
                                  "Generating a ReturnReceipt fails as no AMT data found for Package Owner\n" + 
                                  Nester.nest(pinfo.toString()));
                  
               }
               return;
            }
            
           // If user is not entitled ... just skip the send
            if (!amtuser.isEntitled("DSGN_CONF")) {
               return;
            }
            
            emailaddr = amtuser.getEmail();
                        
           // Ok, if we make it here, u-vars should be the email sender
           //     and n-vars should be email receiver
            body = substituteVariables(body, pinfo, 
                                       uuser, ucompany,
                                       nuser, ncompany,
                                       amtuser);
            
            lmailSubject = substituteVariables(lmailSubject, pinfo, 
                                               uuser, ucompany,
                                               nuser, ncompany,
                                               amtuser);
            
            
            if (!doemailsend()) {
               log.info("Send RR/Notification to user " + nuser + 
                        " NOT completed ... TESTING");
                        
               log.info("Subject: " + lmailSubject + "\n\n");
               log.info(body);
               return;
            }
            
           // Don't retry if we have waited more than 2 min
            long totICanStand = System.currentTimeMillis() + (1000*120);
            
            int tries = 0;
            boolean sent = false;
            Vector savedExceptions = null;
            while(++tries < 20 && System.currentTimeMillis() < totICanStand) {
               try {
                                        
                  oem.edge.ed.sd.ordproc.Mailer.sendMail 
                     (smtpserver(),
                      senderAddr(), 
                      emailaddr,
                      null,
                      null, 
                      doreplyTo()?user.getEmail():null,
                      lmailSubject,
                      body);
                      
                  sent = true;
                  break;
               } catch(Exception ee) {
               
                  if (savedExceptions == null) savedExceptions = new Vector();
                  
                  savedExceptions.addElement(ee);
                  
                 // Wait a random amt of time (so we don't keep colliding)
                  try {
                     Random rand = new Random();
                     int sleeptime = 1000 + (rand.nextInt() % 1000);
                     if (sleeptime < 1000 || sleeptime > 2000) sleeptime = 1000;
                     Thread.currentThread().sleep(sleeptime);
                  } catch(Throwable tt) {}
               }
            }
            
            if (!sent) {
               log.error("Error sending Dropbox " + notificationtype +
                         " mail to " + emailaddr + " from " + user.getUser());
               log.error(body);
               DboxAlert.alert(2, "Error Sending Dbox Notification tries: " + tries,
                               0, body, (Exception)savedExceptions.firstElement());
            } else if (tries > 1) {
               log.error("Finally sent Dropbox (tries=" + tries + ") package " +
                         notificationtype +
                         " mail to " + emailaddr + " from " + user.getUser());
               DboxAlert.alert(4, "Notification worked on subsequent tries: " + tries,
                               0, emailaddr + " from " + user.getUser(),
                               (Exception)savedExceptions.firstElement());
            } else {
               log.info("Email Notification: " + notificationtype + " sent to " +
                        emailaddr + " for package [" + pinfo.getPackageName() + 
                        "] (" + pinfo.getPackageId() + ") owned by " + 
                        pinfo.getPackageOwner());
            }
            
         } catch(Throwable tt) {
            log.error("Error sending Dropbox " +
                      (returnreceipt?"ReturnReceipt ":"Available ") + 
                      "mail to " + emailaddr + " from " + user.getUser());
            tt.printStackTrace();
            DboxAlert.alert(tt);
         }
      }
   }
   
      
  // Routine to find banner file, and read it in ... return string
   public String getLoginMessage() {
      String banner = "";
      String fn = null;
      BufferedReader br = null;
      try { 
         fn = getBannerFilename();
         if (fn != null) {
            String f = SearchEtc.findFileInClasspath(fn);
            if (f != null) {
               FileReader fr = new FileReader(f);
               br = new BufferedReader(fr);
               
               String str;
               StringBuffer sb = new StringBuffer();
               while((str=br.readLine()) != null) {
                  sb.append(str).append("\n");
                  if (sb.length() > 64000) {
                     DebugPrint.printlnd(DebugPrint.WARN, 
                                         "Banner in file " + f + 
                                         " is > 64000 ... truncating");
                     break;
                  }
               }
               banner = sb.toString();
            } else {
               log.warn("Banner file not found: " + fn);
            }
         }
      } catch(FileNotFoundException fnfe) {
         log.error("Attempt to access banner failed: Banner not found"
                   + ":\n" + fn);
      } catch(Exception ee) {
         log.error("Exception while getting banner info");
         log.error(ee);
      } finally {
         try { if (br != null) br.close(); } catch(Exception eee) {}
      }
      
      return banner != null ? banner : "";
   }
      
   
   public long   createPackage(String packageName, 
                               String desc,
                               long poolid,
                               long expiration, 
                               Vector acls,
                               int optmsk, int optvals) 
      throws DboxException, RemoteException {
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      try {
         
         subtask = "finding the User object";
         User user = getUserEx();
         
        // Truncate description to 1024 bytes
         if (desc == null) desc = "";
         if (desc.length() > 1024) desc = desc.substring(0,1024);
         
         log.info("Create Package: u[" + user.getName() + "] p[" + packageName + 
                  "] exp[" + (expiration==0?""+0:new Date(expiration).toString()));
         
        /*
          if (packname.indexOf(File.separator) >= 0) {
          throw new 
          DboxException("Package names CANNOT contain separator char [" +
          packname + "]", 0);
          }
        */        
        
         if (acls == null) acls = new Vector();
         
        // 1/29/07 IBMCC00011061 ... fold user and group names to lowercase
         subtask = "fold user/group names to lowercase";
         Iterator it = acls.iterator();
         while(it.hasNext()) {
            AclInfo aclinfo = (AclInfo)it.next();
            if (aclinfo.getAclType() == DropboxGenerator.STATUS_NONE   ||
                aclinfo.getAclType() == DropboxGenerator.STATUS_GROUP) {
               aclinfo.setAclName(aclinfo.getAclName().toLowerCase());
            }
         }
         
         subtask = "asserting the validity of the recipient list";
         if (doamtchecking()) {
           // Gets vector of aclinfo objects
            acls = assertAMTChecks(user, acls, 
                                   (optmsk & optvals & PackageInfo.ITAR) != 0);
         }
         
         subtask = "assert the validity of the package options";
         packageMgr.assertValidFlagsCreate(optmsk, optvals);
         
        // If the guy is specifying this is an ITAR package
         if ((optmsk & optvals & PackageInfo.ITAR) != 0) {
            subtask = "assert able to do ITAR";
            if (!user.isUserItarCertified()) {
               throw new 
                  DboxException("User not certified to create ITAR packages", 
                                0);
            }
            
            if (!user.isSessionItarCertified()) {
               throw new 
                  DboxException("User must validate ITAR certified", 
                                0);
            }
            
            packageName = getProperty("dropbox.itarPackagePrefix", 
                                      "ITAR_") + packageName;
         }
         
         subtask = "creating the package";
         
         whereFailed = 1;
         DboxPackageInfo info=packageMgr.createPackage(user,
                                                       packageName, 
                                                       desc,
                                                       poolid,
                                                       expiration,
                                                       acls);
         whereFailed = 2;
         
         subtask = "setting package specific options";
         
         packageMgr.setPackageOption(user, info.getPackageId(),
                                     optmsk, optvals);
         
         return info.getPackageId();
         
      } catch(Exception dbex) {
         String errmsg = "Creation of package [" + packageName + "] ";
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
                               
   public long   createPackage(String packageName, 
                               String desc,
                               long poolid,
                               Vector acls,
                               int optmsk, int optvals) 
      throws DboxException, RemoteException {
      
      return createPackage(packageName, desc, poolid, 0, acls, optmsk, optvals);
   }
                               
   public long   createPackage(String packageName, String desc, long poolid, 
                               int optmsk, int optvals) 
      throws DboxException, RemoteException {
      
      return createPackage(packageName, desc, poolid, 0, null, optmsk, optvals);
   }
                               
   public long   createPackage(String packageName) 
      throws DboxException, RemoteException {
      
      return createPackage(packageName, null, DropboxAccess.PUBLIC_POOL_ID, 0, 
                           null, 0, 0);
   }
   
   
   public void   setPackageFlags(long pkgid, 
                                 int pkgmsk, 
                                 int pkgvals) 
      throws DboxException, RemoteException {
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      try {
      
         subtask = "finding user object";
         User user = getUserEx();
         log.info("SetPackageOption called: pkgid=" + pkgid + 
                  " pkgmsk=" + pkgmsk + " pkgvals=" + pkgvals);
         
         subtask = "assert the validity of the package options for modify";
         packageMgr.assertValidFlagsModify(pkgmsk, pkgvals);
         
         subtask = "searching for package";
         DboxPackageInfo pinfo = packageMgr.lookupPackage(pkgid);
         
         subtask = "checking package access";
         whereFailed = 1;
         if (!pinfo.canAccessPackage(user, true)) {
            throw new DboxException("setPackageFlags:>> Can't access package " +
                                    + pkgid,
                                    0);
         }
         
        // If the guy is specifying this is an ITAR package
         if (pinfo.isPackageItar()) {
            subtask = "assert able to do ITAR";
            if (!user.isUserItarCertified()) {
               throw new 
                  DboxException("User not certified to access ITAR packages", 
                                0);
            }
            
            if (!user.isSessionItarCertified()) {
               throw new 
                  DboxException("User must validate ITAR certified", 
                                0);
            }
         }         
                  
         subtask = "setting package option for user";
         whereFailed = 1;
         int newflags = 
            packageMgr.setPackageOption(user, pkgid, pkgmsk, pkgvals);
         
      } catch(Exception dbex) {
         String errmsg = "Setting package options for package[" + pkgid + 
            "] mask[" + pkgmsk + "] pkgvals[" + pkgvals + "] ";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
   
   
   public void   deletePackage(long packid) 
      throws DboxException, RemoteException {
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      try {
         subtask = "finding the User object";
         User user = getUserEx();
         
         log.info("Delete Package u[" + user.getName() + 
                  "] p[" + packid + "]");
                  
         subtask = " deleting package";
         whereFailed = 1;
         packageMgr.deletePackage(user, packid);
         
      } catch(Exception dbex) {
         String packn = "" + packid;
        //if (info != null) packn = info.getPackageName();
         
         String errmsg = "Deletion of package [" + packn + "] ";
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
   
   
   public void   commitPackage(long packid) 
      throws DboxException, RemoteException {
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      try {
      
         subtask = "finding user object";
         User user = getUserEx();
         log.info("Commit Package u[" + user.getName() + 
                  "] p[" + packid + "]");
         
         DboxPackageInfo pinfo = null;
         Vector pacls = null;
         Vector todo  = null;
                  
         subtask = "searching for package";
         pinfo = packageMgr.lookupPackage(packid, user);
         
         subtask = "checking package access";
         whereFailed = 1;
         if (!pinfo.canAccessPackage(user, true)) {
            throw new DboxException("commitPackage:>> Can't access package " +
                                    + packid,
                                    0);
            
         }
         
        // JMC 04/06/07 - CSR 11320 Jamie Leblanc is using sendbyproject
        //                and it is taking too long to commit. Its becase 
        //                we flatten acls, then look them all up via AMT.
        //                Some of the projects he is using contain 400+
        //                users, and its just taking too long to look
        //                them all up ... need to fix that another time. 
        //                For now, just allow us to skip the AMT part
        //                for hidden package (which is how he sends).
         boolean skiphiddenamtcheck = 
            getBoolProperty("dropbox.skipHiddenAMT", true) &&
            pinfo.getPackageHidden();
         
         if (skiphiddenamtcheck) {
            log.info("Skipping hidden AMTCheck on commit");
         } else {
            if (doamtchecking() || doamtmailing()) {
               subtask = "creating complete list of recipients";
               log.debug(subtask);
               pacls = pinfo.getPackageAcls(true);
               
              // todo will be filled with only those ACLS (users) who can
              //  receive the package. Ask for them as strings (names)
               todo  = flattenACLsToUsersAMT(user, pacls, 
                                             (pinfo.getPackageFlags() & 
                                              PackageInfo.ITAR) != 0,
                                             true);
            }
            
            if (doamtchecking()) {
               subtask = "checking for valid recipient list";
               log.debug(subtask);
               if (todo.size() == 0) {
                  throw new DboxException("Cannot commit package: No valid recipients specified", 0);
               }
            }
         }
         
         whereFailed = 1;
         subtask = "doing the commit";
         log.debug(subtask);
         packageMgr.commitPackage(user , packid);
         
         whereFailed = 2;
         subtask = "mailing notifications to recipients";
         log.debug(subtask);
         
        // Send mail to the flattened ACL list
         if (doamtmailing() && !skiphiddenamtcheck) {
            
            if (pinfo.getPackageSendNotification()) {
            
               log.debug("Sending notification for package");
               
              // Lookup the package again to get the commit date
               pinfo = packageMgr.lookupPackage(packid);
               
              // todo contains DISTINCT list of users
               Enumeration lenum = todo.elements();
               while(lenum.hasMoreElements()) {
                  String s = (String)lenum.nextElement();
                  
                  boolean doit = true;
                  try {
                     String ts = 
                        packageMgr.getUserOption(s,
                                                 DropboxGenerator.NewPackageEmailNotification);
                     if (ts != null && !ts.equalsIgnoreCase("true")) {
                        doit=false;
                     }
                  } catch(DboxException dbe) {
                  }
                  
                 // Could fail if 's' aclname not allowed to see package
                  if (doit) {
                     log.info("Attempt email notification for: " + s);
                     amtMailer.sendAMTMail(user, pinfo, s);
                  } else {
                     log.info("Skipping email notification for " + s);
                  }
               }
            } else {
               log.info("Skipping send of email notification for package: " +
                        pinfo.toString());
            }
         } else {
            log.debug("AMT Mailing is off <or skiphiddenamtcheck> sjac=" + 
                      skiphiddenamtcheck); 
         }
         
      } catch(Exception dbex) {
      
         String packn = "" + packid;
        //if (info != null) packn = info.getPackageName();
         
         String errmsg = "Commit of package [" + packn + "] ";
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
   
   public void   markPackage(long packid, boolean v) 
      throws DboxException, RemoteException {
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      try {
      
         subtask = "finding user object";
         User user = getUserEx();
         
         log.info("Marking Package u[" + user.getName() + 
                  "] p[" + packid + "] mark=" + v);
                  
         whereFailed = 1;
         subtask = "marking the package";
         packageMgr.markPackage(user, packid, v);
         
      } catch(Exception dbex) {
      
         String errmsg = "Marking the package[" + packid + "] ";
         if (!v) errmsg = "Un" + errmsg;
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
   
   public void addPackageAcl(long packid, AclInfo acl)
      throws DboxException, RemoteException {
   
      addPackageAcl(packid, acl.getAclName(), acl.getAclStatus());
   }
   
   public void addPackageAcl(long packid, String aclname, byte acltype) 
      
      throws DboxException, RemoteException {
   
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DboxPackageInfo pinfo = null;
      
      try {
               
         subtask = "finding user object";
         User user = getUserEx();
         
        // 1/29/07 IBMCC00011061 ... fold user and group names to lowercase
         subtask = "fold user/group names to lowercase";
         if ((acltype == DropboxGenerator.STATUS_NONE   || 
             acltype == DropboxGenerator.STATUS_GROUP)  && 
             aclname != null) {
             
            aclname = aclname.toLowerCase();
         }
         
         log.info("Add package ACL u[" + user.getName() + 
                  "] c[" + user.getCompany() +
                  "] p[" + packid + "] acln[" + aclname + 
                  "] acltype[" + acltype + "]");
         
         
         subtask = "searching for package";
         pinfo = packageMgr.lookupPackage(packid);
         
         subtask = "checking package access";
         whereFailed = 1;
         if (!pinfo.canAccessPackage(user, true)) {
            throw new DboxException("addPackageAcl:>> Can't access package " +
                                    + packid,
                                    0);
            
         }
         
        // If the guy is specifying this is an ITAR package
         if (pinfo.isPackageItar()) {
            subtask = "assert able to do ITAR";
            if (!user.isUserItarCertified()) {
               throw new 
                  DboxException("User not certified to access ITAR packages", 
                                0);
            }
            
            if (!user.isSessionItarCertified()) {
               throw new 
                  DboxException("User must validate ITAR certified", 
                                0);
            }
         }         
         
         
        // Check that this guy CAN be added to ACL list. acls will contain
        //  AMTUser objs associated with the users name
         Vector acls = null;
         
         if (doamtchecking() && acltype == DropboxGenerator.STATUS_NONE &&
             !aclname.equals("*")) {
            subtask = "asserting the validity of sending to the recipient";
            
           // Get AMTUser objects
            acls = assertAMTCheck(user, aclname, 
                                  (pinfo.getPackageFlags() & PackageInfo.ITAR) != 0, 
                                  false, false);
         }
         
        // If we are doing mail, get flattened list of USERS currently
        // added as ACLS, either a USER or GROUP
         Vector currentReceivers = null;
         boolean sendmail = false;
         if (doamtmailing() && 
             pinfo.getPackageStatus() == 
             DropboxGenerator.STATUS_COMPLETE &&
             pinfo.getPackageSendNotification() &&
             
            // JMC 9/21/04
            // This is essentially now == TRUE cause we accept ALL acl types
            //  used to be that we only accepted NONE and GROUP
             (acls != null                                 ||
              acltype == DropboxGenerator.STATUS_NONE      ||
              acltype == DropboxGenerator.STATUS_PROJECT   ||
              acltype == DropboxGenerator.STATUS_GROUP)) {
              
            subtask = "creating complete list of recipients";
            Vector pacls = pinfo.getPackageAcls(true);
            
           // we don't have to do the AMT check here cause this is about
           //  building a list to limit who we send emails to. If the user
           //  is not allowed to send to those users, does not matter.
            currentReceivers = flattenACLsToUsers(pacls);
            sendmail = true;
         }
         
        // Should throw exception if already added as an acl
         boolean foundacls = true;
         if (acls == null) {
         
            foundacls = false;
            
            subtask = "adding name to access list";
            whereFailed = 1;
            
            packageMgr.addPackageAcl(user, packid, acltype, aclname);
            
            whereFailed = 2;
            
           // Send mail to this guy
            if (sendmail) {
               
               subtask = "mailing notifications to recipients";
               
              // Get expanded list for this Acl
               AclInfo acl = new AclInfo();
               acl.setAclName(aclname);
               acl.setAclStatus(acltype);
               acls = flattenACLToUsers(null, acl);
            }      
         }
         
         if (acls != null && (foundacls || sendmail)) {
           // If ACL checking is on, we are adding all acls returned
            Enumeration enum = acls.elements();
            while(enum.hasMoreElements()) {
               
               String laclname = null;
               Object uos = enum.nextElement();
               if (uos instanceof String) {
                  laclname = (String)uos;
               } else {
                  laclname = ((AMTUser)uos).getUser();
               }
               
              // If we are suppose to add this acl ... add it
               if (foundacls) {
                  subtask = "adding name to access list";
                  whereFailed = 1;
                  packageMgr.addPackageAcl(user, packid, acltype, laclname);
               }
               
              // Send mail to this guy
               if (sendmail) {
                  whereFailed = 2;
                  subtask = "mailing notifications to recipients";
                  if (!currentReceivers.contains(laclname)) {
                     
                     currentReceivers.addElement(laclname);
                     
                     boolean doit = true;
                     try {
                        String ts = 
                           packageMgr.getUserOption(laclname,
                                                    DropboxGenerator.NewPackageEmailNotification);
                        if (ts != null && !ts.equalsIgnoreCase("true")) {
                           doit=false;
                        }
                     } catch(DboxException dbe) {
                     }
                     
                    // We are now sure this is a user name
                     if (doit) {
                        log.info("Attempt email notification for: " + laclname);
                        amtMailer.sendAMTMail(user, pinfo, laclname);
                     } else {
                        log.info("Skipping email notification for " + 
                                 laclname);
                     }
                  }
               }
            }
         }
      } catch(Exception dbex) {
         String pn = "" + packid;
         if (pinfo != null) pn = pinfo.getPackageName();
         
         String errmsg = "Adding '" + aclname + 
            "' to the access list for package [" + pn + "] ";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
   
   public void   addUserAcl(long packid, String name) 
      throws DboxException, RemoteException {
      
      addPackageAcl(packid, name, DropboxGenerator.STATUS_NONE);
   }
   
   
   public void   addGroupAcl(long packid, String name) 
      throws DboxException, RemoteException {
      
      addPackageAcl(packid, name, DropboxGenerator.STATUS_GROUP);
   }
   
   
   public void   addProjectAcl(long packid, String name) 
      throws DboxException, RemoteException {
      
      addPackageAcl(packid, name, DropboxGenerator.STATUS_PROJECT);
   }
      
   
   public void removePackageAcl(long packid, String aclname, byte acltype) 
      
      throws DboxException, RemoteException {
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      try {
      
         
         subtask = "finding user object";
         User user = getUserEx();
         
        // 1/29/07 IBMCC00011061 ... fold user and group names to lowercase
         subtask = "fold user/group names to lowercase";
         if ((acltype == DropboxGenerator.STATUS_NONE   || 
             acltype == DropboxGenerator.STATUS_GROUP)  && 
             aclname != null) {
             
            aclname = aclname.toLowerCase();
         }
         
         log.info("Remove package ACL u[" + user.getName() + 
                  "] c[" + user.getCompany() +
                  "] p[" + packid + "] acln[" + aclname + 
                  "] acltype: " + acltype);
                  
                  
         subtask = "searching for package";
         DboxPackageInfo pinfo = packageMgr.lookupPackage(packid);
         
         subtask = "checking package access";
         whereFailed = 1;
         if (!pinfo.canAccessPackage(user, true)) {
            throw new DboxException("removePackageAcl:>> Can't access package " +
                                    + packid,
                                    0);
            
         }
         
        // If the guy is specifying this is an ITAR package
         if (pinfo.isPackageItar()) {
            subtask = "assert able to do ITAR";
            if (!user.isUserItarCertified()) {
               throw new 
                  DboxException("User not certified to access ITAR packages", 
                                0);
            }
            
            if (!user.isSessionItarCertified()) {
               throw new 
                  DboxException("User must validate ITAR certified", 
                                0);
            }
         }         
                  
         subtask = "removing package ACL";
         whereFailed = 1;
         packageMgr.removePackageAcl(user, packid, acltype, aclname);
         whereFailed = 2;
         
      } catch(Exception dbex) {
         String errmsg = "Remove acl[" + aclname + "] from package packid[" +
            packid + "] ";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
   
   public void   removeUserAcl(long packid, String name) 
      throws DboxException, RemoteException {
      
      removePackageAcl(packid, name, DropboxGenerator.STATUS_NONE);
   }
   
   
   public void   removeGroupAcl(long packid, String name) 
      throws DboxException, RemoteException {
      
      removePackageAcl(packid, name, DropboxGenerator.STATUS_GROUP);
   }
   
   
   public void   removeProjectAcl(long packid, String name) 
      throws DboxException, RemoteException {
      
      removePackageAcl(packid, name, DropboxGenerator.STATUS_PROJECT);
   }
   
   
   public PoolInfo getStoragePoolInstance(long poolid) 
      throws DboxException, RemoteException { 
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      try {
      
         subtask = "finding storage pool instance";
         User user = getUserEx();
         log.info("Get Storage Pool Instance: u[" + user + 
                  "] c[" + user.getCompany() +
                  "] p[" + poolid + 
                  "]");
                  
         whereFailed = 1;
         subtask = "getting storage pool instance";
         return packageMgr.getStoragePoolInstance(user, poolid);
         
      } catch(Exception dbex) {
      
         String errmsg = "Access storage pool instance for poolid[" +
            poolid + "] ";

         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
      
   public Vector queryStoragePoolInformation() 
      throws DboxException, RemoteException { 
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      try {
      
         subtask = "querying storage pool information";
         User user = getUserEx();
         log.info("Query storage pool information: u[" + user + "]");
                  
         whereFailed = 1;
         
         subtask = "getting Storage Pool info";
         return packageMgr.queryStoragePoolInformation(user);
         
      } catch(Exception dbex) {
      
         String errmsg = "Query storage pool information ";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
   
   
   public void   changePackageExpiration(long packid, 
                                         long expires) 
      throws DboxException, RemoteException {
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      try {
      
         subtask = "finding user object";
         User user = getUserEx();
         log.info("Change Package Expiration: u[" + user + 
                  "] c[" + user.getCompany() +
                  "] p[" + packid + 
                  "] exp[" + (expires==0?""+0:new Date(expires).toString()));
                  
                  
         subtask = "searching for package";
         DboxPackageInfo pinfo = packageMgr.lookupPackage(packid);
         
         subtask = "checking package access";
         whereFailed = 1;
         if (!pinfo.canAccessPackage(user, true)) {
            throw new DboxException("changePackageExpiration:>> Can't access package " +
                                    + packid,
                                    0);
         }
         
        // If the guy is specifying this is an ITAR package
         if (pinfo.isPackageItar()) {
            subtask = "assert able to do ITAR";
            if (!user.isUserItarCertified()) {
               throw new 
                  DboxException("User not certified to access ITAR packages", 
                                0);
            }
            
            if (!user.isSessionItarCertified()) {
               throw new 
                  DboxException("User must validate ITAR certified", 
                                0);
            }
         }
                                    
         whereFailed = 1;
         subtask = "doing expiration change";
         packageMgr.changePackageExpiration(user, packid, expires);
         
      } catch(Exception dbex) {
      
         String errmsg = "Change of package expiration for packid[" +
            packid + "] to " +
            (new Date(expires)).toString() + " ";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
   
   public void   setPackageDescription(long packid, 
                                       String desc) 
      throws DboxException, RemoteException {
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      try {
      
         subtask = "finding user object";
         User user = getUserEx();
         log.info("Set Package Description: u[" + user + 
                  "] c[" + user.getCompany() +
                  "] p[" + packid + 
                  "] desc[" + desc + "]");
                  
         whereFailed = 1;
         subtask = "doing expiration change";
         
        // Truncate description to 1024 bytes
         if (desc == null) desc = "";
         if (desc.length() > 1024) desc = desc.substring(0,1024);
         
         subtask = "searching for package";
         DboxPackageInfo pinfo = packageMgr.lookupPackage(packid);
         
         subtask = "checking package access";
         whereFailed = 1;
         if (!pinfo.canAccessPackage(user, true)) {
            throw new DboxException("setPkgDesc:>> Can't access package " 
                                    + packid,
                                    0);
         }
         
         if (pinfo.getPackageStatus() == DropboxAccess.STATUS_COMPLETE) {
            throw new DboxException("setPkgDesc:>> Completed package description cannot be modified " + packid, 0);
         }
         
        // If the guy is specifying this is an ITAR package
         if (pinfo.isPackageItar()) {
            subtask = "assert able to do ITAR";
            if (!user.isUserItarCertified()) {
               throw new 
                  DboxException("User not certified to access ITAR packages", 
                                0);
            }
            
            if (!user.isSessionItarCertified()) {
               throw new 
                  DboxException("User must validate ITAR certified", 
                                0);
            }
         }
         
         packageMgr.setPackageDescription(user, packid, desc);
         
      } catch(Exception dbex) {
      
         String errmsg = "Set package description for packid[" +
            packid + "] to " + desc + " ";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
   
   public HashMap getOptions() 
      throws DboxException, RemoteException {
      
      return getOptions(null);
   }
   
   
   public HashMap   getOptions(Vector optnames) 
      throws DboxException, RemoteException {
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      try {
      
         subtask = "finding user object";
         User user = getUserEx();
         
         log.info("GetOptions called: size = " + (optnames==null?0:optnames.size()));
         
         if (optnames != null && optnames.size() > 0) {
            subtask = "asserting valid user options for GET";
            packageMgr.assertUserOptionNames(user.getName(), optnames);
         }
         
         whereFailed = 1;
         subtask = "getting user options";
            
         return new HashMap(packageMgr.getUserOptions(user, optnames));
         
      } catch(Exception dbex) {
         String errmsg = "Manage user options ";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
   
   
   public String  getOption(String opt) 
      throws DboxException, RemoteException {
      
      return (String)getOptions().get(opt);
   }
   
   
   public void    setOptions(HashMap options) 
      throws DboxException, RemoteException {
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      try {
      
         subtask = "finding user object";
         User user = getUserEx();
         log.info("setOptions called: optionsize=" + 
                  (options==null?0:options.size()));

         if (options != null && options.size() > 0) {
            String OS         = (String)options.get(DropboxGenerator.OS);
            String clienttype = (String)options.get(DropboxGenerator.ClientType);
            
            if (OS != null) {
               subtask = "setting special OS option value";
               user.setOS(OS);
               options.remove(DropboxGenerator.OS);
            }
            if (clienttype != null) {
               subtask = "setting special clienttype option value";
               user.setClientType(clienttype);
               options.remove(DropboxGenerator.ClientType);
               
               log.info("Clienttype = " + clienttype + " OS = " + OS);
            }
            
            if (OS != null || clienttype != null) {
               packageMgr.setClientInfo(user, OS, clienttype);
            }
            
           // Hack for now to get to compile  TODOTODOTODOTOD
            Hashtable hack = new Hashtable();
            hack.putAll(options);
            
            subtask = "asserting valid user options for SET";
            packageMgr.assertUserOptionNames(user.getName(), hack);
            
            whereFailed = 1;
            subtask = "setting user options";
            packageMgr.setUserOptions(user, hack);
         }
      } catch(Exception dbex) {
         String errmsg = "Manage user options ";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
   
   
   public void    setOption(String opt, String val) 
      throws DboxException, RemoteException {
      
      HashMap map = new HashMap();
      map.put(opt, val);
      setOptions(map);
   }
   
   
   public Vector  getProjectList() 
      throws DboxException, RemoteException {
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      try {
      
         subtask = "finding user object";
         User user = getUserEx();
         
         whereFailed = 1;
         
         return user.getDoProjectSend()?user.getProjects():new Vector(); 
         
      } catch(Exception dbex) {
      
         String errmsg = "Query of project list ";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
   
   
   public Vector queryPackages(String name, boolean isRegExp, 
                               boolean ownerOrAccessor,
                               boolean filterCompleted, 
                               boolean filterMarked,
                               boolean fullDetail) 
      throws DboxException, RemoteException {
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      try {
      
         subtask = "finding user object";
         User user = getUserEx();
            
         subtask = "doing the package search";
         whereFailed = 1;
         Vector tret = 
            packageMgr.packagesMatchingExprWithAccess(user, ownerOrAccessor,
                                                      name, isRegExp,
                                                      filterMarked, 
                                                      filterCompleted);
                                                      
         log.info(" !!!! handle the fullDetail bool !!!! ");
         
        // Weed out all hidden packages
         boolean showhidden = 
            packageMgr.getUserOption(user, 
                                     DropboxGenerator.ShowHidden).equalsIgnoreCase("true");
                                               
        // Convert the returned objects to REAL PackageInfo and weed out hidden
         Vector ret = new Vector();
         
         int l = tret.size();
         for(int i=0; i < l; i++) {
            PackageInfo pi = (PackageInfo)tret.elementAt(i);
            
            
           // Packages are hidden only in INBOX AND only if ShowHidden == false
            if (ownerOrAccessor || showhidden ||
                (pi.getPackageFlags() & PackageInfo.HIDDEN) == 0) {
               pi = new PackageInfo(pi);
               ret.addElement(pi);
            }
         }
      
         return ret;
         
      } catch(Exception dbex) {
         String errmsg = "Query packages ";
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
                               
   public Vector queryPackages(boolean ownerOrAccessor,
                               boolean filterCompleted, 
                               boolean filterMarked,
                               boolean fullDetail) 
      throws DboxException, RemoteException {
      
      return queryPackages(null, false, ownerOrAccessor, filterCompleted, 
                           filterMarked, fullDetail);
   }
                        
   public PackageInfo queryPackage(long packid,
                                   boolean fullDetail) 
      throws DboxException, RemoteException {
                                   
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DboxPackageInfo info = null; 
      
      try {
      
         subtask = "finding user object";
         User user = getUserEx();
         
         subtask = "searching for package";
         info = packageMgr.lookupPackage(packid, user);
         
         subtask = "checking package access";
         whereFailed = 1;
         if (!info.canAccessPackage(user, true)) {
            info = null;
            throw new DboxException("queryPackage:>> Can't access package "
                                    + packid,
                                    0);
         }
         
         return new PackageInfo(info);
      } catch(Exception dbex) {
      
         String packn = "" + packid;
         if (info != null) packn = info.getPackageName();
         
         String errmsg = "Query package for [" + packn + "] ";
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
   
   public Vector queryPackageContents(long packid) 
      throws DboxException, RemoteException {
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DboxPackageInfo info = null; 
      
      try {
      
         subtask = "finding user object";
         User user = getUserEx();
         
         subtask = "searching for package";
         info = packageMgr.lookupPackage(packid);
         
         subtask = "checking package access";
         whereFailed = 1;
         if (!info.canAccessPackage(user, true)) {
            String pn = info.getPackageName();
            info = null;
            throw new DboxException("queryPackageContents:>> Can't access package " +
                                    pn + "(" + packid + ")",
                                    0);
         }
         
         Vector tret = info.getFiles();
         
        // Convert the returned objects to REAL FileInfo and weed out hidden
         Vector ret = new Vector();
         
         int l = tret.size();
         for(int i=0; i < l; i++) {
            FileInfo fi = (FileInfo)tret.elementAt(i);
            fi = new FileInfo(fi);
            ret.addElement(fi);
         }
      
         return ret;
         
      } catch(Exception dbex) {
      
         String packn = "" + packid;
         if (info != null) packn = info.getPackageName();
         
         String errmsg = "Query package contents for [" + packn + "] ";
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
   
                        
   public Vector queryFiles(String name, boolean isRegExp,
                            boolean ownerOrAccessor) 
      throws DboxException, RemoteException {
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      try {
      
         subtask = "finding user object";
         User user = getUserEx();
         
         FileManager fmgr = packageMgr.getFileManager();
         
         whereFailed = 1;
         subtask = "searching for files";
         Vector tret = fmgr.filesMatchingExprWithAccess(user, ownerOrAccessor,
                                                        name, isRegExp);
         Vector ret = new Vector();
         Enumeration enum = tret.elements();
         while(enum.hasMoreElements()) {
            FileInfo fi = (FileInfo)enum.nextElement();
            ret.addElement(new FileInfo(fi));
         }
         
         return ret;
         
      } catch(Exception dbex) {
         String errmsg = "Query files ";
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
                            
   public Vector queryFiles(boolean ownerOrAccessor) 
      throws DboxException, RemoteException {
      
      return queryFiles(null, false, ownerOrAccessor);
   }
                        
                        
   public FileInfo queryFile(long fileid) 
      throws DboxException, RemoteException {
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DboxFileInfo info = null;
      
      try {
      
         subtask = "find user object";
         User user = getUserEx();
         
         subtask = "searching for file in question";
         info = packageMgr.lookupFile(fileid);
         
         subtask = "validating access to file in question";
         if (!packageMgr.canAccessFile(user, info, true)) {
            info = null;   // Don't allow filename use
            throw new DboxException("queryFile:>> Can't access file " +
                                    fileid, 0);
         }
         
         whereFailed = 1;
         subtask = "searching for packages containing file";
         
//         Vector vec = packageMgr.getPackagesContainingFile(user, info);

         return new FileInfo(info);
         
         
      } catch(Exception dbex) {
      
         String errmsg = "Query file [" + 
            (info != null?info.getFileName():""+fileid) + "] ";
            
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
                        
                        
   public Vector queryPackageAcls(long packid, 
                                  boolean staticonly) 
      throws DboxException, RemoteException {
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DboxPackageInfo info = null; 
      
      try {
         
         subtask = "finding user object";
         User user = getUserEx();
         
         subtask = "searching for package";
         info = packageMgr.lookupPackage(packid);
         
         subtask = "checking package access rights";
         if (!info.canAccessPackage(user, true)) {
            info = null;
            throw new DboxException("queryPackageAcls:>> Can't access package " +
                                    packid, 0);
         }
            
            
         boolean setprojectaclcompany = getBoolProperty("dropbox.setProjectAclCompany",
                                                        true);
         boolean setaclcompany = getBoolProperty("dropbox.setAclCompany",
                                                        true);
            
         whereFailed = 1;
         subtask = "accessing package ACLS";
         
        // Acls returned here are real AclInfo objects, which is the contract
         Vector vec = info.getPackageAcls(staticonly);
         
        // If not owner or superuser, remove all non-pertinent info
         String name = user.getName();
         
         subtask = "culling restricted ACLS";
         boolean docull = (!name.equals(info.getPackageOwner()) &&
                           packageMgr.getPrivilegeLevel(name) < 
                           packageMgr.PRIVILEGE_SUPER_USER);
            
         for(int i=0; i < vec.size(); i++) {
            AclInfo acl = (AclInfo)vec.elementAt(i);
            byte astat = acl.getAclStatus();
            if (docull) {
               if ((astat != DropboxGenerator.STATUS_NONE      &&
                    astat != DropboxGenerator.STATUS_PARTIAL   &&
                    astat != DropboxGenerator.STATUS_FAIL      &&
                    astat != DropboxGenerator.STATUS_COMPLETE) ||
                   !acl.getAclName().equals(name)) {
                  vec.removeElementAt(i);
                  i--;
                  continue;
               }
            }
            
            if (setaclcompany) {
               try {
                  if (astat != DropboxGenerator.STATUS_PROJECT && 
                      astat != DropboxGenerator.STATUS_GROUP) {
                     Vector amtvec = UserRegistryFactory.getInstance().lookup(
                        acl.getAclName(), false, false, false);
                     if (amtvec != null && amtvec.size() > 0) {
                        acl.setAclCompany(((AMTUser)amtvec.firstElement()).getCompany());
                     }
                  } else if (astat == DropboxGenerator.STATUS_GROUP ||
                             setprojectaclcompany) {
                     Vector av = new Vector();
                     av.add(acl);
                     av = flattenAclsToCompanies(user, av, info.getPackageItar());
                     Iterator it = av.iterator();
                     String rets = "";
                     int numc=0;
                     while(it.hasNext()) {
                        String cs = (String)it.next();
                       // Was doing this check using length, but an empty
                       //  String company would be lost.
                        if (numc++ == 0) rets  = cs;
                        else             rets += "," + cs;
                     }
                     acl.setAclCompany(rets);
                  }
               } catch(Exception ee) {
                  log.debug("Error getting company info for acl: " + acl.toString());
               }
            }
         }
            
         return vec;
         
      } catch(Exception dbex) {
      
         String packn = "" + packid;
         if (info != null) packn = info.getPackageName();
         
         String errmsg = "Query package access list for [" + packn + "] ";
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
   
  // We assume any projects/groups specified are accessible by the user.
  //  Just cull out the list of group members and project folks, and get 
  //  the associated list of companies
   protected Vector flattenAclsToCompanies(User user, 
                                           Vector acls,
                                           boolean needitar) throws DboxException {
                                           
      Vector users = flattenACLsToUsersAMT(user, acls, needitar, false);
      Vector ret = new Vector();
      
      if (users != null && users.size() > 0) {
         Iterator it = users.iterator();
         while(it.hasNext()) {
            AMTUser amtuser = (AMTUser)it.next();
            if (!ret.contains(amtuser.getCompany())) {
               ret.add(amtuser.getCompany());
            }
         }
         Collections.sort(ret);
      }
      return ret;
   }
   
   public Vector queryPackageAclCompanies(long packid)
      throws DboxException, RemoteException {
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DboxPackageInfo info = null; 
      
      try {
         
         subtask = "finding user object";
         User user = getUserEx();
         
         subtask = "searching for package";
         info = packageMgr.lookupPackage(packid);
         
         subtask = "checking for package access";
         String ownerS = user.getName();
         int privlev = packageMgr.getPrivilegeLevel(ownerS);
         
        // Check that we are allowed to see the acls
         if (((!info.getPackageOwner().equals(ownerS)) && 
              privlev < PackageManager.PRIVILEGE_CAN_SET_OTHER_ACLS)) {
            throw new DboxException("queryPackageAclCompanies: error:>> " +
                                    "package " + packid + 
                                    " not owned by " + ownerS,
                                    0);
         }
         
         
        // Acls returned here 
         subtask = "accessing package ACLS";
         Vector vec = info.getPackageAcls(true);
         
         whereFailed = 1;
         subtask = "flattening acls to companies";
         return flattenAclsToCompanies(user, vec, info.getPackageItar());
         
      } catch(Exception dbex) {
      
         String packn = "" + packid;
         if (info != null) packn = info.getPackageName();
         
         String errmsg = "Query package Acl Companies [" + packn + "] ";
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
                        
   public Vector queryRepresentedCompanies(Vector acls, boolean needitar)
      throws DboxException, RemoteException {
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DboxPackageInfo info = null; 
      
      try {
         
         subtask = "finding user object";
         User user = getUserEx();
         
         if (needitar) {
            subtask = "validating user is ITAR";
            if (!user.isUserItarCertified()) {
               throw new DboxException("Caller is not ITAR certified");
            }
         }
         
         subtask = "Validating acls";
         Iterator it = acls.iterator();
         while(it.hasNext()) {
            AclInfo aclinfo = (AclInfo)it.next();
            
            switch(aclinfo.getAclType()) {
               case DropboxGenerator.STATUS_NONE:
               case DropboxGenerator.STATUS_PARTIAL:
               case DropboxGenerator.STATUS_FAIL:
               case DropboxGenerator.STATUS_COMPLETE:
                  aclinfo.setAclType(DropboxGenerator.STATUS_NONE);
                  assertAMTCheck(user, aclinfo.getAclName(), needitar, true, false); 
                  break;
               case DropboxGenerator.STATUS_GROUP:
                  packageMgr.getGroupWithAccess(user, aclinfo.getAclName(), 
                                                 false, false);
                  break;
               case DropboxGenerator.STATUS_PROJECT:
                  if (!user.getProjects().contains(aclinfo.getAclName())) {
                     throw new DboxException("Invalid project: " + aclinfo.getAclName());
                  }
                  break;
               default:
                  throw new DboxException("Invalid acl type: " + aclinfo.getAclType());
            }
         }
         
         whereFailed = 1;
         subtask = "flattening acls to companies";
         
         return flattenAclsToCompanies(user, acls, needitar);
         
      } catch(Exception dbex) {
      
         String errmsg = "Query companies represented in ACL list ";
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
   
   
   public Vector lookupUser(String username, boolean isregex) 
      throws DboxException, RemoteException {
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      try {
         
         subtask = "finding user object";
         User user = getUserEx();
         
        /*
         if (isregex) {
            subtask = "regex supported check";
            throw new DboxException("Regular expression lookupUser not supported");
         }
        */
         
         whereFailed = 1;
         subtask = "Searching for user";
         if (isregex) {
            Vector amtvec = 
               UserRegistryFactory.getInstance().lookup(username, isregex, 
                                                        false, false);
            if (amtvec == null || amtvec.size() == 0) {
               return new Vector();
            }
            Vector aclvec = new Vector();
            Iterator it = amtvec.iterator();
            
            Vector ret = new Vector();
            while(it.hasNext()) {
               AMTUser amtuser = (AMTUser)it.next();
               try {
                  assertAMTCheck(user, amtuser.getUser(), false, true, false);
                  
                  AclInfo aclinfo = new AclInfo();
                  aclinfo.setAclName(amtuser.getUser());
                  aclinfo.setAclType(DropboxGenerator.STATUS_NONE);
                  aclinfo.setAclCompany(amtuser.getCompany());
                  ret.add(aclinfo);
               } catch(Exception ee) {}
            }
            
            return ret;
            
         } else {
            Vector acls = assertAMTCheck(user, username, false, true, false);
            AMTUser amtuser = (AMTUser)acls.firstElement();
         
            Vector ret = new Vector();
            AclInfo aclinfo = new AclInfo();
            aclinfo.setAclName(amtuser.getUser());
            aclinfo.setAclType(DropboxGenerator.STATUS_NONE);
            aclinfo.setAclCompany(amtuser.getCompany());
            ret.add(aclinfo);
            
            return ret;
         }
         
      } catch(Exception dbex) {
      
         String errmsg = "Query companies represented in ACL list ";
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
   
                        
   public Vector queryPackageFileAcls(long packid, 
                                      long fileid) 
      throws DboxException, RemoteException {
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DboxPackageInfo info = null;
      
      try {
         
         subtask = "finding user object";
         User user = getUserEx();
         
         subtask = "searching for package";
         info = packageMgr.lookupPackage(packid);
         
         subtask = "checking package access rights";
         whereFailed = 1;
         if (!info.canAccessPackage(user, true)) {
            info = null;
            throw new DboxException("Can't access package " + packid, 0);
         }
         
         subtask = "obtaining package access list";
         
        // Acls returned here are real AclInfo objects, which is the contract
         Vector vec = info.getFileAcls(fileid);
         
        // If not owner or superuser, remove all non-pertinent info
        // Also, repackage to real AclInfo objs ... (they are DboxAclInfo now)
         subtask = "culling restricted ACLS";
         String name = user.getName();
         Vector retvec = new Vector();
         boolean cull =  (!name.equals(info.getPackageOwner()) &&
                          packageMgr.getPrivilegeLevel(name) < 
                          packageMgr.PRIVILEGE_SUPER_USER);
         
         Iterator it = vec.iterator();
         while(it.hasNext()) {
            AclInfo ainfo = (AclInfo)it.next();
            if (!cull || ainfo.getAclName().equals(name)) {
               retvec.add(new AclInfo(ainfo));
            }
         }
      
         return retvec;
         
      } catch(Exception dbex) {
      
         String packn = "" + packid;
         if (info != null) packn = info.getPackageName();
         
         String errmsg = "Query package file access list for [" + packn + "] ";
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
                        
                        
   public void addItemToPackage(long packid, long itemid) 
      throws DboxException, RemoteException {
   
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      try {
      
         subtask = "find user object";
         User user = getUserEx();
         
         log.info("Add item to Package u[" + user.getName() + 
                  "] p[" + packid + "] item[" + itemid + "]");
                  
         whereFailed = 1;
         subtask = "adding item to package";
         packageMgr.addItemToPackage(user, packid, itemid);
         
      } catch(Exception dbex) {
         
         String errmsg = "Add item to package packid[" + packid + 
            "] itemid[" + itemid + "] ";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
   
   public void removeItemFromPackage(long packid,
                                     long itemid)  
      throws DboxException, RemoteException {
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      try {
         
         subtask = "finding user object";
         User user = getUserEx();
         
         log.info("Remove item from Package u[" + user.getName() + 
                  "] p[" + packid + "] + item[" + itemid + "]");
         
         subtask = "removing file from package";
         whereFailed = 1;
         packageMgr.removeItemFromPackage(user, packid, itemid);
      } catch(Exception dbex) {
         
         String errmsg = "Remove item[" + itemid + "] from package packid[" +
            packid + "] ";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
   
   public long uploadFileToPackage(long packid, 
                                   String file,
                                   long totalIntendedSize) 
      throws DboxException, RemoteException {
                                   
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DboxPackageInfo pinfo = null; 
      DboxFileInfo     info = null;
      
      try {
      
         subtask = "finding user object";
         User user = getUserEx();
         
         log.info("Upload file to Package u[" + user.getName() + 
                  "] p[" + packid + "] f[" + file + "]");
         
        // JMC 3/16/04 - Forbidden char replacement 
         String forbiddenChars = forbiddenCharacters();
         if (forbiddenChars.length() > 0) {
            boolean reped = false;
            for(int ci=0; ci < forbiddenChars.length(); ci++) {
               char ch = forbiddenChars.charAt(ci);
               if (file.indexOf(ch) >= 0) {
                  file = file.replace(ch, '_');
                  reped = true;
               }
            }
            if (reped) {
               log.info("New name after forbiddenChar replacement [" + 
                        file + "]");
            }
         }
         
         subtask = "searching for package";
         pinfo = packageMgr.lookupPackage(packid);
         
         subtask = "checking package access rights";
         if (!pinfo.canAccessPackage(user, true)) {
            pinfo = null;
            throw new DboxException("Can't access package " + packid, 0);
         }
         
         subtask = "checking ITAR acceptance";
         if ((pinfo.getPackageFlags() & PackageInfo.ITAR) != 0) {
            if (!user.isSessionItarCertified()) {
               throw new DboxException("Can't access ITAR package data. Please certify your session", 0);
            }
         }
                  
         if (pinfo.getPackageStatus() == DropboxGenerator.STATUS_COMPLETE) {
            throw new DboxException("Package already complete");
         }
         
         if (pinfo.includesFile(file)) {
            
            subtask = "checking for same file already in package";
            info = pinfo.getFile(file);
            
            if ( /*!(packageMgr instanceof GridPackageManager) && */
                info.getFileStatus() == DropboxGenerator.STATUS_COMPLETE) {
                
               throw new DboxException(
                  "File with same name already in package and complete [" +
                  file + "]", 0);
            }
            
            subtask = "truncating the file";
            info.truncate(totalIntendedSize);
            pinfo.recalculatePackageSize();
            
            subtask = "setting file object attributes";
            info.setServersideFileStatus(DropboxGenerator.STATUS_NONE);
            
         } else {
            subtask = "doing the file create operation";
            info = packageMgr.getFileManager().createFile(file, 
                                                          totalIntendedSize, 
                                                          pinfo.getPackagePoolId());
            pinfo.addFile(info);
            
           /* was used to debug locking
            ((DB2DboxFileInfo)info).testlock();
           */
         }
         
         log.debug("UploadfileToPackage : " + info.toString());
         
         return info.getFileId();
         
      } catch(Exception dbex) {
         
         String pn = "" + packid;
         if (pinfo != null) pn = pinfo.getPackageName();
         
         String errmsg = "Upload file[" + file + "] to package [" + pn + "] ";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
   
   public FileSlot allocateUploadFileSlot(long packid, 
                                          long fileid,
                                          int  totalThreads)  
      throws DboxException, RemoteException {
                                          
     // ignore total threads for now
     
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DboxPackageInfo pinfo = null; 
      DboxFileInfo     info = null;
      
      try {
      
         subtask = "finding user object";
         User user = getUserEx();
         
         log.info("Allocating File Slot for file u[" + user.getName() + 
                  "] p[" + packid + "] f[" + fileid + "] tothreads [" +
                  totalThreads + "]");
         
         
         subtask = "searching for package";
         pinfo = packageMgr.lookupPackage(packid);
         
         subtask = "checking package access rights";
         if (!pinfo.canAccessPackage(user, true)) {
            pinfo = null;
            throw new DboxException("Can't access package " + packid, 0);
         }
         
         if ((pinfo.getPackageFlags() & PackageInfo.ITAR) != 0) {
            if (!user.isSessionItarCertified()) {
               throw new DboxException("Can't access ITAR package data. Please certify your session", 0);
            }
         }
         
         subtask = "checking for file in package";
         info = pinfo.getFile(fileid); 
         
         if (info.getFileStatus() == DropboxGenerator.STATUS_COMPLETE) {
            return null;
           //throw new DboxException("Cannot allocate slot, file complete");
         }
         
         subtask = "doing the allocate operation";
         FileSlot fs = info.allocateFileSlot(user);
         if (fs != null) {
            fs = new FileSlot(fs);
            log.info("Slot allocated:\n" + fs.toString());
            
           // Set file status to NONE 
            if (info.getFileStatus() != DropboxGenerator.STATUS_NONE) {
               subtask = "setting file object attributes";
               info.setServersideFileStatus(DropboxGenerator.STATUS_NONE);
            }
         }
         
         return fs;
         
      } catch(Exception dbex) {
         
         String pn = "" + packid;
         if (pinfo != null) pn = pinfo.getPackageName();
         
         String errmsg = "allocateUploadFileSlot for fileid[" + fileid + 
            "] to package [" + pn + "] ";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
     
   }
                                          
   public FileSlot uploadFileSlotToPackage(long packid, 
                                           long fileid,
                                           long slotid,
                                           boolean getNextSlot,
                                           byte[] is) 
      throws DboxException, RemoteException {
      
      DataHandler h = new DataHandler(new ByteArrayDataSource(is));
      return uploadFileSlotToPackageWithHandler(packid, fileid, slotid, 
                                                getNextSlot, h);
   }
   
                                          
   public FileSlot uploadFileSlotToPackageWithHandler(long packid, 
                                                      long fileid,
                                                      long slotid,
                                                      boolean getNextSlot,
                                                      DataHandler h)
      throws DboxException, RemoteException {
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DboxPackageInfo pinfo = null; 
      DboxFileInfo     info = null;
      User             user = null; 
      boolean      released = false;
      
      try {
      
         subtask = "finding user object";
         user = getUserEx();
         
         log.info("uploadFileSlotToPackageWHand for file u[" + user.getName() + 
                  "] p[" + packid + 
                  "] f[" + fileid +
                  "] s[" + slotid + "]");
         
         
         subtask = "searching for package";
         pinfo = packageMgr.lookupPackage(packid);
         
         subtask = "checking package access rights";
         if (!pinfo.canAccessPackage(user, true)) {
            pinfo = null;
            throw new DboxException("Can't access package " + packid, 0);
         }
         
         if ((pinfo.getPackageFlags() & PackageInfo.ITAR) != 0) {
            if (!user.isSessionItarCertified()) {
               throw new DboxException("Can't access ITAR package data. Please certify your session", 0);
            }
         }
         
         subtask = "checking for file in package";
         info = pinfo.getFile(fileid); 
         
         if (info.getFileStatus() == DropboxGenerator.STATUS_COMPLETE) {
            throw new DboxException("Cannot upload to file, file complete");
         }
         
        // Set file status to NONE 
         if (info.getFileStatus() != DropboxGenerator.STATUS_NONE) {
            subtask = "setting file object attributes";
            info.setServersideFileStatus(DropboxGenerator.STATUS_NONE);
         }
         
        // Get Slot object
         subtask = "Lookup file slot";
         DboxFileSlot slot = info.getFileSlot(slotid);
         log.info("Slot write offset = " + slot.getCurrentOffset());
         if (slot.getSessionId() != user.getSessionId()) {
            log.warn("SlotInfo:\n" + slot.toString());
            throw new DboxException("Slot is not owned by caller");
         }
         
        // Get an MD5 Digest. If there is NOT one, and we are at 0, build one
         MessageDigestI digest = slot.getMD5Object();
         if (digest == null && slot.getLength() == 0) {
            digest = new DropboxFileMD5();
         }
         
         long len  = slot.getRemainingBytes();
         long slen = len;
         
         
        // Write the data
         RandomAccessFile rf = null;        
         InputStream is = null;
         try {
         
            is = h.getInputStream();
         
            subtask = "Geting Output Stream for writing";
            rf = slot.getUploader();
            
           //
           // TODOTODOTODO WCCByteBuffer oddity
           //
           //  WCCByteBuffer E   Failed to get next body buffer
           // 
           //  I get this error when running webdropbox via Hessian to upload a file 
           //  when trying to read the first time. So, I just let it retry once if
           //  it fails on the first time to work around the problem         
           //        
            boolean hasfailed = false;            
            byte buf[] = new byte[32768];
            while(len > 0 && !released) {
            
               subtask = "Reading data";
               
               int l = buf.length;
               if (len < l) l = (int)len;
               int r = is.read(buf, 0, l);
               if (r < 0) {
                  if (hasfailed && slen == len) {
                     hasfailed = true;
                     continue;
                  }
                  break;
               } else if (r > 0) {
                  subtask = "Writing data";
                  rf.write(buf, 0, r);
                  if (digest != null) {
                     digest.update(buf, 0, r);
                  }
                  len -= r;
               }
            }
            
           // Close Random file here to release locks
            try {
              // Try to ensure that the data is written to disk. Perhaps this will ensure that
              //  NFS will be properly in sync for remote files.
               rf.getChannel().force(true);
               rf.close();
               rf = null;
            } catch(Exception ee) {
            }
            
            if (is.read(buf, 0, 1) != -1) {
               log.warn("Upload to:\n" + info.toString() + 
                        "\n has extra bytes left in stream! Ending slot info:\n"
                        + slot.toString());
            }
            
            
            if (digest != null) {
               released=slot.forceSetLengthAndMD5Object(slot.getLength()+(slen-len),
                                                        digest);
            } else {
               released=slot.forceSetLength(slot.getLength()+(slen-len));
            }
            
                        
            if (len > 0 && released) {
               log.warn("Upload to:\n" + info.toString() + 
                        "\n len > 0 and yet slot was released! Ending slot info:\n"
                        + slot.toString());
            }
                        
         } catch(IOException ioe) {
            throw new DboxException("IOException while writing slot", ioe);
         } catch(Exception e) {
            throw new DboxException("Exception while writing slot", e);
         } finally {
            try {
               if (rf != null) rf.close();
            } catch(Exception ee) {
            }
            try {
               if (is != null) is.close();
            } catch(Exception ee) {
            }
         }
         
        // If the current slot was not filled, just release it, and allow the
        //  allocate routine to give it (or another) back
         if (!released) {
            subtask = "doing the release operation";
            released = true;
            info.releaseFileSlot(user, slotid);
         }
            
        // If we should return a next slot, get it. This will also cause a
        //  culling of the slots, which will also take care of completing
        //  the file, if its done
         if (getNextSlot) {
         
            subtask = "Getting next slot";
            slot = info.allocateFileSlot(user);
            if (slot != null) {
               pinfo.recalculatePackageSize();
               return new FileSlot(slot);
            }
            
         } else {
           // Since we are NOT allocating a fileslot, do the cull directly
            info.cullSlots();
         }
         
         pinfo.recalculatePackageSize();
         
         return null;
         
      } catch(Exception dbex) {
         
         String pn = "" + packid;
         if (pinfo != null) pn = pinfo.getPackageName();
         
         String errmsg = "uploadFileSlotDatas for fileid[" + fileid + 
            "] to package [" + pn + "] slotid [" + slotid + "]";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      } finally {
        // we ALWAYS release it 
         if (!released && user != null && info != null) try {
            info.releaseFileSlot(user, slotid);
         } catch(Exception ee) {}
      }
   }
   
   public Vector queryFileSlots(long packid, long fileid) 
      throws DboxException, RemoteException {
                                
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DboxPackageInfo pinfo = null; 
      DboxFileInfo     info = null;
      
      try {
      
         subtask = "finding user object";
         User user = getUserEx();
         
         log.info("Query File Slots for file u[" + user.getName() + 
                  "] p[" + packid + "] f[" + fileid + "]");
         
         
         subtask = "searching for package";
         pinfo = packageMgr.lookupPackage(packid);
         
         subtask = "checking package access rights";
         if (!pinfo.canAccessPackage(user, true)) {
            pinfo = null;
            throw new DboxException("Can't access package " + packid, 0);
         }
         
         if ((pinfo.getPackageFlags() & PackageInfo.ITAR) != 0) {
            if (!user.isSessionItarCertified()) {
               throw new DboxException("Can't access ITAR package data. Please certify your session", 0);
            }
         }
         
         subtask = "checking for file in package";
         info = pinfo.getFile(fileid); 
         
         Vector ret = new Vector();
         if (info.getFileStatus() != DropboxGenerator.STATUS_COMPLETE) {
         
           // Get slots and clean them
            subtask = "doing the query operation";
            Vector slots = info.getFileSlots();
            Iterator it = slots.iterator();
            while(it.hasNext()) {
               FileSlot fs = (FileSlot)it.next();
               ret.addElement(new FileSlot(fs));
            }
         }
         
        // TODOTODOTODOTODO The MD5 value can be changing under our feet here!!
         FileSlot slot0 = new FileSlot(fileid, CULLED_SLOT, info.getFileSize(), 
                                       info.getFileSize(), 0L, 
                                       user.getSessionId());
         slot0.setMD5(info.getFileMD5());
      
         ret.add(0, slot0);
         
         return ret;
      } catch(Exception dbex) {
         
         String pn = "" + packid;
         if (pinfo != null) pn = pinfo.getPackageName();
         
         String errmsg = "queryFileSlots for fileid[" + fileid + 
            "] to package [" + pn + "] ";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
                                               
   public void removeFileSlot(long packid, 
                              long fileid,
                              long slotid)  
      throws DboxException, RemoteException {
      
      if (slotid == -1) { 
         throw new DboxException("Slotid -1 may NOT be removed");
      }
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DboxPackageInfo pinfo = null; 
      DboxFileInfo     info = null;
      
      try {
      
         subtask = "finding user object";
         User user = getUserEx();
         
         log.info("Remove File Slot for file u[" + user.getName() + 
                  "] p[" + packid + "] f[" + fileid + "] s[" + slotid + "]");
         
         
         subtask = "searching for package";
         pinfo = packageMgr.lookupPackage(packid);
         
         subtask = "checking package access rights";
         if (!pinfo.canAccessPackage(user, true)) {
            pinfo = null;
            throw new DboxException("Can't access package " + packid, 0);
         }
         
         if ((pinfo.getPackageFlags() & PackageInfo.ITAR) != 0) {
            if (!user.isSessionItarCertified()) {
               throw new DboxException("Can't access ITAR package data. Please certify your session", 0);
            }
         }
         
         subtask = "checking for file in package";
         info = pinfo.getFile(fileid); 
         
         if (info.getFileStatus() == DropboxGenerator.STATUS_COMPLETE) {
            throw new DboxException("Cannot modify slot, file complete");
         }
         
         subtask = "doing the remove operation";
         info.removeFileSlot(user, slotid);
         
      } catch(Exception dbex) {
         
         String pn = "" + packid;
         if (pinfo != null) pn = pinfo.getPackageName();
         
         String errmsg = "removeFileSlot for fileid[" + fileid + 
            "] package [" + pn + "] slotid[" + slotid + "]";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }              
   }
                                               
   public void releaseFileSlot(long packid, 
                               long fileid,
                               long slotid)  
      throws DboxException, RemoteException {
                                   
      if (slotid == -1) { 
         throw new DboxException("Slotid -1 may NOT be released/owned");
      }
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DboxPackageInfo pinfo = null; 
      DboxFileInfo     info = null;
      
      try {
      
         subtask = "finding user object";
         User user = getUserEx();
         
         log.info("Release File Slot for file u[" + user.getName() + 
                  "] p[" + packid + "] f[" + fileid + "] s[" + slotid + "]");
         
         
         subtask = "searching for package";
         pinfo = packageMgr.lookupPackage(packid);
         
         subtask = "checking package access rights";
         if (!pinfo.canAccessPackage(user, true)) {
            pinfo = null;
            throw new DboxException("Can't access package " + packid, 0);
         }
         
         if ((pinfo.getPackageFlags() & PackageInfo.ITAR) != 0) {
            if (!user.isSessionItarCertified()) {
               throw new DboxException("Can't access ITAR package data. Please certify your session", 0);
            }
         }
         
         subtask = "checking for file in package";
         info = pinfo.getFile(fileid); 
         
         if (info.getFileStatus() == DropboxGenerator.STATUS_COMPLETE) {
            throw new DboxException("Cannot modify slot, file complete");
         }
         
         subtask = "doing the release operation";
         info.releaseFileSlot(user, slotid);
         
      } catch(Exception dbex) {
         
         String pn = "" + packid;
         if (pinfo != null) pn = pinfo.getPackageName();
         
         String errmsg = "releaseFileSlot for fileid[" + fileid + 
            "] package [" + pn + "] slotid[" + slotid + "]";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
                                               
                                               
   public void registerAuditInformation(long packid, 
                                        long fileid,
                                        long length,
                                        long timems,
                                        boolean upOrDown)
      throws DboxException, RemoteException {
                                  
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DboxPackageInfo pinfo = null; 
      DboxFileInfo     info = null;
      
      try {
      
         subtask = "finding user object";
         User user = getUserEx();
         
         log.info("registerAuditInformation for Package/file u[" + user.getName() + 
                  "] p[" + packid + "] f[" + fileid + "] len[" + length + "] time[" + 
            timems + "] upOrDown[" + upOrDown + "]");
         
         
         int xferrate = 0;
         if (timems > 0) {
            xferrate = (int)((length*1000)/timems);
            if (xferrate < 0) xferrate = 0;
         }
         
         subtask = "searching for package";
         pinfo = packageMgr.lookupPackage(packid);
         
         subtask = "checking package access rights";
         if (!pinfo.canAccessPackage(user, true)) {
            pinfo = null;
            throw new DboxException("Can't access package " + packid, 0);
         }
         
         if (upOrDown && !pinfo.getPackageOwner().equals(user.getName())) {
            throw new DboxException("Only package owner can modify upload info", 0);
         }
         
         if ((pinfo.getPackageFlags() & PackageInfo.ITAR) != 0) {
            if (!user.isSessionItarCertified()) {
               throw new DboxException("Can't access ITAR package data. Please certify your session", 0);
            }
         }
         
         info = pinfo.getFile(fileid);
         
         if (upOrDown) {
            info.forceSetFileXferrate(xferrate);
            
           // Set file status to INCOMPLETE if not COMPLETE. This is a best can do situation
           //  The helper code registers audit info after upload is done, good or ill,
           //  so it SHOULD be a good time to update the status of the file. Problems
           //  in commo/client machine failure/etc will cause the state to remain NONE,
           //  and so the package status will be PARTIAL rather than FAIL. No biggy ...
            if (info.getFileStatus() != DropboxGenerator.STATUS_COMPLETE) {
               subtask = "setting file object attributes";
               info.setServersideFileStatus(DropboxGenerator.STATUS_INCOMPLETE);
            }
         } else {
           // Silently fail if record does not exist
            AclInfo ainfo =  pinfo.getFileAccessRecord(user.getName(), fileid);
            if (ainfo != null) {
               pinfo.addFileAccessRecord(user, fileid, ainfo.getAclStatus(), xferrate);
            } else {
               log.warn("Can't update audit info. No file access record found for \n\t" + pinfo.toString() + " \n\t" + user.toString() + "\n\tfileid = " + fileid);
            }
         }
         
      } catch(Exception dbex) {
         
         String pn = "" + packid;
         if (pinfo != null) pn = pinfo.getPackageName();
         
         String errmsg = "Commit Upload fileid[" + fileid + "] to package [" + pn + "] ";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
                                               
   public void commitUploadedFile(long packid, 
                                  long fileid,
                                  long length,
                                  String md5)  
      throws DboxException, RemoteException {
                                  
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DboxPackageInfo pinfo = null; 
      DboxFileInfo     info = null;
      
      try {
      
         subtask = "finding user object";
         User user = getUserEx();
         
         log.info("commitUpdatedFile for Package u[" + user.getName() + 
                  "] p[" + packid + "] f[" + fileid + "] len[" + length + "]");
         
         subtask = "searching for package";
         pinfo = packageMgr.lookupPackage(packid);
         
         subtask = "checking package access rights";
         if (!pinfo.canAccessPackage(user, true)) {
            pinfo = null;
            throw new DboxException("Can't access package " + packid, 0);
         }
         
         if ((pinfo.getPackageFlags() & PackageInfo.ITAR) != 0) {
            if (!user.isSessionItarCertified()) {
               throw new DboxException("Can't access ITAR package data. Please certify your session", 0);
            }
         }
         
         if (pinfo.getPackageStatus() == DropboxGenerator.STATUS_COMPLETE) {
            throw new DboxException("Package already complete");
         }
         
         info = pinfo.getFile(fileid);
         
         if (info.getFileStatus() == DropboxGenerator.STATUS_COMPLETE) {
            throw new DboxException("File already complete");
         }
         
         info.cullSlots();
         
        // If we don't have 'length' data for file w/o holes ... don't do it
         long flen = info.getFileSize();
         if (flen < length) {
            Vector slots = info.getFileSlots();
            if (slots.size() > 0) {
               FileSlot fs = (FileSlot)slots.elementAt(0);
               if (fs.getStartingOffset() != flen ||
                   flen + fs.getLength() < length) {
                  throw new DboxException("Cannot truncate file to be larger than actual size");
               }
            }
         }
         
         subtask = "truncating the file";
         
         info.truncate(length);
         
         pinfo.recalculatePackageSize();
         
         subtask = "checking MD5 values";
         
         if (md5 != null) {
            if (!info.getFileMD5().equals(md5)) {
               throw new DboxException("File MD5 does not match");
            }
         }
         
      } catch(Exception dbex) {
         
         String pn = "" + packid;
         if (pinfo != null) pn = pinfo.getPackageName();
         
         String errmsg = "Commit Upload fileid[" + fileid + "] to package [" + pn + "] ";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
   
   
   
   public byte[] downloadPackage(long packid, 
                                 String encoding)  
      throws DboxException, RemoteException {
      
      throw new DboxException("Not Yet Implemented", 0);
   }
   
   public byte[] downloadPackageItem(long packid, 
                                     long fileid)  
      throws DboxException, RemoteException {
      
      throw new DboxException("Not Yet Implemented", 0);
   }
   
   public DataHandler downloadPackageWithHandler(long packid, 
                                                 String encoding)
      throws DboxException, RemoteException {
      return new DataHandler(downloadPackageStream(packid, encoding));
   }
                                                 
   public PackageInputStream downloadPackageStream(long packid, 
                                                   String encoding)
      throws DboxException, RemoteException {
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DboxPackageInfo pinfo = null; 
      DboxFileInfo     info = null;
      
      try {
      
         subtask = "finding user object";
         User user = getUserEx();
         
         log.info("downloadPackage for Package u[" + user.getName() + 
                  "] p[" + packid + "] encoding[" + encoding + "]");
         
        // Add in User, as we want the Completed status to be correct in pinfo
         subtask = "searching for package";
         pinfo = packageMgr.lookupPackage(packid, user);
         
         subtask = "checking package access rights";
         if (!pinfo.canAccessPackage(user, true)) {
            pinfo = null;
            throw new DboxException("Can't access package " + packid, 0);
         }
         
         if ((pinfo.getPackageFlags() & PackageInfo.ITAR) != 0) {
            if (!user.isSessionItarCertified()) {
               throw new DboxException("Can't access ITAR package data. Please certify your session", 0);
            }
         }
         
         subtask = "Validate package is complete";
         if (pinfo.getPackageStatus() != DropboxGenerator.STATUS_COMPLETE) {
            throw new DboxException("Package not complete");
         }
         
        // Return a PackageInputStream
         PackageInputStream ret = new PackageInputStream(pinfo, encoding); 
         
        // Add a completion listener who will modify the file access records accoringly
         ret.addCompletionListener(new PackageCompletionListener(user, pinfo, ret));
         
         return ret;
            
      } catch(Exception dbex) {
         
         String pn = "" + packid;
         if (pinfo != null) pn = pinfo.getPackageName();
         
         String errmsg = "Download package [" + pn + 
            "] with encoding [" + encoding + "] ";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
   
   public DataHandler downloadPackageItemWithHandler(long packid, 
                                                     long fileid)
      throws DboxException, RemoteException {
      
      return new DataHandler(downloadPackageItemStream(packid, fileid, 0, -1));
   }
                                     
   public DataHandler downloadPackageItemWithHandler(long packid, 
                                                     long fileid,
                                                     long ofs,
                                                     long len)  
      throws DboxException, RemoteException {
      
      return new DataHandler(downloadPackageItemStream(packid, fileid, ofs, len));
   }
   
  // This routine is NOT part of the DropboxAccess interface. IMPL Specific
   public ComponentInputStream downloadPackageItemStream(long packid, 
                                                         long fileid,
                                                         long ofs,
                                                         long len)  
      throws DboxException, RemoteException {
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DboxPackageInfo pinfo = null; 
      DboxFileInfo     info = null;
      
      try {
      
         subtask = "finding user object";
         User user = getUserEx();
         
         log.info("downloadPackageItem for Package u[" + user.getName() + 
                  "] p[" + packid + "] f[" + fileid + 
                  "] ofs[" + ofs + "] len[" + len + "]");
         
         
        // Add in user parm cause we want completion info to be correct
         subtask = "searching for package";
         pinfo = packageMgr.lookupPackage(packid, user);
         
         subtask = "checking package access rights";
         if (!pinfo.canAccessPackage(user, true)) {
            pinfo = null;
            throw new DboxException("Can't access package " + packid, 0);
         }
         
         if ((pinfo.getPackageFlags() & PackageInfo.ITAR) != 0) {
            if (!user.isSessionItarCertified()) {
               throw new DboxException("Can't access ITAR package data. Please certify your session", 0);
            }
         }
         
         subtask = "Validate file is in package and good ofs";
         info = pinfo.getFile(fileid);
         
         if (info.getFileStatus() != DropboxGenerator.STATUS_COMPLETE) {
            throw new DboxException("File not complete");
         }
         
         long fsize = info.getFileSize();
         
        // If len is -1, we take it to mean the entire file
         if (len == -1) len = fsize;
         
         if (ofs >= fsize || ofs < 0) {
            throw new DboxException("Offset too large or neg: " + ofs);
         }
         
        // Calculate the offset of the start of the tail block
         long mtbofs = fsize - MIN_TAIL_BLOCK_SIZE;
         if (mtbofs < 0) mtbofs = 0;
         
        // If the offset is specified past the tail block, or exactly ON the
        //  offset but does not scoop the entire thing ... Exception
         if (ofs >  mtbofs || (ofs == mtbofs && ofs + len < fsize)) {
            throw new DboxException("Must download last " + MIN_TAIL_BLOCK_SIZE + 
                                    " bytes all at once");
         }
         
        // Truncate length to file size
         if (ofs + len > fsize) len = fsize - ofs;
                  
        // Truncate length to NOT eclipse mtbofs unless it can scoop entire thing
         long eofs = ofs + len;
         if (eofs > mtbofs && eofs != fsize) len = mtbofs - ofs;
         
         RandomAccessFile is = null;
         
         log.info("Downloading(ofs, len) = (" + ofs + ", " + len + ")");
         
        // Return a ComponentInputStream
         ComponentInputStream ret = new ComponentInputStream(info, ofs, len);
         
        // Add a completion listener who will modify the file access record accordingly
         ret.addCompletionListener(new CompletionListener(user, pinfo, 
                                                          info, eofs == fsize));
         
         return ret;
            
      } catch(Exception dbex) {
         
         String pn = "" + packid;
         if (pinfo != null) pn = pinfo.getPackageName();
         
         String errmsg = "Download file portion fileid[" + fileid + "] from package [" + pn + "] ";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
                                     
                                     
   public byte[] downloadPackageItem(long packid, 
                                     long fileid,
                                     long ofs,
                                     long len)  
      throws DboxException, RemoteException {
      
      
     // Truncate length to not be greater than max download slot size
      if (len > MaxDownloadSlotSize) len = MaxDownloadSlotSize;
      
      ComponentInputStream cis = downloadPackageItemStream(packid, fileid, ofs, len);
      
      try {
         long totr = cis.getBytesRemaining();
         
         if (totr > len || len < 0) {
            throw new DboxException("Download file portion fileid[" + 
                                    fileid + "] from package [" + packid + 
                                    "] failed initial len check");
         }
      
         byte ret[] = new byte[(int)totr];
         
         log.info("Downloading*(ofs, len) = (" + ofs + ", " + totr + ")");
         
         
        // Loop through the components and read in appropriate bytes
         int retofs = 0;
         while(retofs < totr) {
            int r = cis.read(ret, retofs, (int)(totr-retofs));
            if (r < 0) {
               throw new DboxException("Ran out of bytes prior to completion!");
            }
            retofs += r;   // Increment ret buffer insert ofs
         }            
         
         return ret;
      
      } catch(IOException ioe) {
         throw new DboxException("Error opening/reading filecomponent file!", ioe);
      } finally {
         if (cis != null) try {
            cis.close();
         } catch(Exception ee) {}
      }
   }

   public String[] getPackageItemMD5(long packid, 
                                     long fileid,
                                     long len)  
      throws DboxException, RemoteException {
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DboxPackageInfo pinfo = null; 
      DboxFileInfo     info = null;
      
      try {
      
         subtask = "finding user object";
         User user = getUserEx();
         
         log.info("getPackageItemMD5 for Package u[" + user.getName() + 
                  "] p[" + packid + "] f[" + fileid + 
                  "] len[" + len + "]");
         
         subtask = "searching for package";
         pinfo = packageMgr.lookupPackage(packid);
         
         subtask = "checking package access rights";
         if (!pinfo.canAccessPackage(user, true)) {
            pinfo = null;
            throw new DboxException("Can't access package " + packid, 0);
         }
         
         if ((pinfo.getPackageFlags() & PackageInfo.ITAR) != 0) {
            if (!user.isSessionItarCertified()) {
               throw new DboxException("Can't access ITAR package data. Please certify your session", 0);
            }
         }
         
         subtask = "Validate file is in package and good ofs";
         info = pinfo.getFile(fileid);
         
         if (info.getFileStatus() != DropboxGenerator.STATUS_COMPLETE) {
            throw new DboxException("File not complete");
         }
         
         long fsize = info.getFileSize();
         
         if (len > fsize || len < 0) {
            throw new DboxException("specified len too large or neg: " + len);
         }
         
         RandomAccessFile is = null;
         
        // Calculate the number of MD5's to return
         long numl = len / (1024*1024);
         
         if (numl > 5)       numl = 5;
         else if (numl == 0) numl = 1;
         
         int  num  = (int)numl;
         
        // Allocate string array for return. "num" has a value between 1 and 5
         String ret[] = new String[num];
         
        // Get the starting offset 
         long ofs = len - ((numl-1) * 1024 * 1024);
         
         log.debug("Calculating " + num + " MD5 values starting at ofs " + ofs);
         
         
        // If there are any 'num' entries left (ie. there was more than one)
        //   then we page the component in starting at ofs, and start
        //   digesting bytes till we have enough to make the MD5 ... etc.
         try {
         
           // Get the message digest for the starting offset
            MessageDigestI digest = info.calculateAndReturnMD5(ofs);
            ret[--num] = digest.hashAsString();
         
           // The number of bytes we have to read is num MB
            len = num * 1024 * 1024;
            
           // Loop through the components and read in appropriate bytes
            byte buf[] = null;
            if (len > 0) buf = new byte[32768];
            while(len > 0) {
               subtask = "accessing file component";
               DboxFileComponent fc = info.getComponentContainingOffset(ofs);
               long sofs = fc.getStartingOffset();
               long clen = fc.getFileSize();
               
               long seekofs = ofs - sofs;
               long possiblelen = clen - seekofs;
               
               if (len < possiblelen) possiblelen = len;
               
               log.info("getting MD5 from component(ofs, len) = (" + 
                        ofs + ", " + possiblelen + ") from fc: " + fc.toString());
               
               subtask = "Accessing filecomponent file";
               is = fc.getDownloader(ofs);
               
               subtask = "Reading data from file";
               int tot=0;
               while(tot < possiblelen) {
               
                 // toread is num bytes to read this time.
                  int toread = (int)(possiblelen-tot);
                  
                 // Make sure it fits in buffer
                  if (toread > buf.length) toread = buf.length;
                  
                 // Make sure we stop reading when we should to calc MD5
                  int left = (int)(len & ((1024*1024) - 1));
                  if (left > 0 && left < toread) toread = left;
                  
                  int r = is.read(buf, 0, toread);
                  
                  if (r < 0) {
                     throw new DboxException("Ran out of bytes prior to completion!");
                  }
                  
                  if (r > 0) {
                     tot += r;
                     digest.update(buf, 0, r);
                     
                     len -= r;
                  
                    // If the num bytes left is meg aligned, its a keeper
                     if ((len & ((1024*1024)-1)) == 0) {
                        ret[--num] = digest.hashAsString();
                     }
                  }
               }
               
               is.close();
               is = null;
               
               ofs    += tot;   // Increment absolute offset for next component read
            }
            
            return ret;
            
         } catch(IOException ioe) {
            throw new DboxException("Error opening/reading filecomponent file!", ioe);
         } catch(Exception eee) {
            throw new DboxException("Error calculating MD5", eee);
         } finally {
            if (is != null) try {
               is.close();
            } catch(Exception ee) {}
         }
         
      } catch(Exception dbex) {
         
         String pn = "" + packid;
         if (pinfo != null) pn = pinfo.getPackageName();
         
         String errmsg = "Download file portion fileid[" + fileid + "] from package [" + pn + "] ";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
   
   public void createGroup(String groupname, 
                           byte visibility, 
                           byte listability)  
      throws DboxException, RemoteException {
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      try {
      
         subtask = "finding user object";
         User user = getUserEx();
         
        // 1/29/07 IBMCC00011061 ... fold user and group names to lowercase
         if (groupname != null) {
            subtask = "fold user/group names to lowercase";
            groupname = groupname.toLowerCase();
         }
         
         log.info("CreateGroup called: groupname=" + groupname);
         
         whereFailed = 1;
         subtask = "creating group";
         packageMgr.createGroup(user, groupname);
         
         whereFailed = 2;
         subtask = "Modifying attributes for vis/listability";
         
         packageMgr.modifyGroupAttributes(user, groupname, 
                                          visibility, listability);
                                          
      } catch(Exception dbex) {
         String errmsg = "Create group [" + groupname + "] ";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
                                             
   public void createGroup(String groupname)  
      throws DboxException, RemoteException {
      
      createGroup(groupname, 
                  DropboxGenerator.GROUP_SCOPE_OWNER, 
                  DropboxGenerator.GROUP_SCOPE_OWNER);
   }
                                             
                                             
   public void deleteGroup(String groupname)  
      throws DboxException, RemoteException {
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      try {
      
         subtask = "finding user object";
         User user = getUserEx();
         
        // 1/29/07 IBMCC00011061 ... fold user and group names to lowercase
         if (groupname != null) {
            subtask = "fold user/group names to lowercase";
            groupname = groupname.toLowerCase();
         }
      
         log.info("DeleteGroup called: groupname=" + groupname);
         
         whereFailed = 1;
         subtask = "deleting group";
         packageMgr.deleteGroup(user, groupname);
         
      } catch(Exception dbex) {
         String errmsg = "Delete group [" + groupname + "] ";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
                                             
                                             
   protected void modifyGroupAcl(String  groupname, 
                                 String  username,
                                 boolean addOrRemove,
                                 boolean memberOrAccess)  
      throws DboxException, RemoteException {
                           
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      try {
      
         subtask = "finding user object";
         User user = getUserEx();
         
        // 1/29/07 IBMCC00011061 ... fold user and group names to lowercase
         subtask = "fold user/group names to lowercase";         
         if (groupname != null) groupname = groupname.toLowerCase();
         if (username != null)  username = username.toLowerCase();
         
         log.info("ModifyGroupAcl called:" + 
                  " group=" + groupname +
                  " user=" + username +
                  " memberOrAccess=" + memberOrAccess +
                  " addRemove=" + addOrRemove);
         
         if (addOrRemove) {                        
            
           // If AMT checking is on, we are adding all acls returned
            if (doamtchecking() && doGroupAMT()) {
               subtask = "asserting valid username";
               Vector acls = assertAMTCheck(user, username, false, false, false);
               if (acls != null) {
                  Enumeration enum = acls.elements();
                  subtask = "adding member to group";
                  whereFailed = 1;
                  while(enum.hasMoreElements()) {
                     String laclname = ((AMTUser)enum.nextElement()).getUser();
                     packageMgr.addGroupAcl(user, groupname, 
                                            laclname, memberOrAccess);
                  }
               }
            } else {
               subtask = "adding member to group";
               whereFailed = 1;
               packageMgr.addGroupAcl(user, groupname, 
                                      username, memberOrAccess);
            }
            
         } else {
            subtask = "removing member from group";
            whereFailed = 1;
            packageMgr.removeGroupAcl(user, groupname, 
                                      username, memberOrAccess);
         }
         
      } catch(Exception dbex) {
         String errmsg = (addOrRemove?"Add":"Remove") +
            " member[" + username+ "] " + (addOrRemove?"to":"from") +
            " group[" + groupname + "] ";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
   
   public void addGroupAcl(String  groupname, 
                           String  user,
                           boolean memberOrAccess)  
      throws DboxException, RemoteException {
      
      modifyGroupAcl(groupname, user, true, memberOrAccess);
   }                           
                                             
   public void removeGroupAcl(String  groupname, 
                              String  user,
                              boolean memberOrAccess)  
      throws DboxException, RemoteException {
      
      modifyGroupAcl(groupname, user, false, memberOrAccess);
   }
                                             
                                             
   public void modifyGroupAttributes(String groupname, 
                                     byte visibility, 
                                     byte listability)  
      throws DboxException, RemoteException {
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      try {
         
         subtask = "finding user object";
         User user = getUserEx();
         
        // 1/29/07 IBMCC00011061 ... fold user and group names to lowercase
         if (groupname != null) {
            subtask = "fold user/group names to lowercase";
            groupname = groupname.toLowerCase();
         }
         
         log.info("ModifyGroupAttribute called: " +
                  " groupname="   + groupname +
                  " visibility="  + visibility +
                  " listability=" + listability);
         
         whereFailed = 1;
         subtask = "modifying visibility/listability attributes";
         packageMgr.modifyGroupAttributes(user, groupname, 
                                          visibility, listability);
                                          
      } catch(Exception dbex) {
         String errmsg = "Modify visibility/listability attributes for [" + 
            groupname + "] ";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
                                             
   public GroupInfo queryGroup(String groupname) 
      throws DboxException, RemoteException {
      return (GroupInfo)queryGroups(groupname, false, true, true).get(groupname);
   }                                             
   
   public HashMap queryGroups(String groupname,
                              boolean isRegExp,
                              boolean includeMembers,
                              boolean includeAccess)  
      throws DboxException, RemoteException {
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      try {
      
         subtask = "finding user object";
         User user = getUserEx();
         
        // 1/29/07 IBMCC00011061 ... fold user and group names to lowercase
         if (groupname != null) {
            subtask = "fold user/group names to lowercase";
            groupname = groupname.toLowerCase();
         }
         
         log.info("QueryGroups called: " +
                  " groupname="   + groupname +
                  " wantMember="  + includeMembers +
                  " wantAccess="  + includeAccess +
                  " regexSearch=" + isRegExp);
         
         whereFailed = 1;
         subtask = "searching for matching groups";
         Hashtable ret = packageMgr.getMatchingGroups(user, groupname, 
                                                      isRegExp,
                                                      false, false, 
                                                      false, true, false,
                                                      true, true, true);
         
         subtask = "building reply protocol";
         whereFailed = 2;
         
        // We only scoped the search by visible (and groupname if provided)
        //  So, we know the user is allowed to see the group. Still have 
        //  to prune out info he may not see (like members and attributes)
         String username = user.getName();
         Enumeration enum = ret.elements();
         while(enum.hasMoreElements()) {
            
            GroupInfo gi = (GroupInfo)enum.nextElement();
            
            byte listable = gi.getGroupListability();
            
            String ownername = gi.getGroupOwner();
            
           // If the user has modify access, then he can see the attributes
            boolean modify = false;
            Vector access = gi.getGroupAccess();
            Vector member = gi.getGroupMembers();
            if (ownername.equals(username) || access.contains(username)) {
               modify = true;
            } else {
               gi.setGroupVisibility(DropboxGenerator.GROUP_SCOPE_NONE);
               gi.setGroupListability(DropboxGenerator.GROUP_SCOPE_NONE);
            }            
            
           // They don't get the access unless they are editor AND asked for it
            if (!includeAccess || !modify) {
               gi.setGroupAccess(new Vector());
               gi.setGroupAccessValid(false);
            }
            
           // They don't get members unless they ASKED for it AND they have a right
           //  to the member list
            if (!includeMembers ||
                !(modify || 
                  listable == DropboxGenerator.GROUP_SCOPE_ALL ||
                  (listable == DropboxGenerator.GROUP_SCOPE_MEMBER &&
                  member.contains(username)))) {
               gi.setGroupMembers(new Vector());
               gi.setGroupMembersValid(false);
            }
         }
         return new HashMap(ret);
         
      } catch(Exception dbex) {
         String errmsg = "Query groups [" + groupname + "] ";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         throw new DboxException(errmsg, dbex);
      }
   }
                                             
   public HashMap queryGroups(boolean includeMembers,
                              boolean includeAccess)  
      throws DboxException, RemoteException {
      
      return queryGroups(null, false, includeMembers, includeAccess);
   }
   
   
   public static void main(String args[]) {
   
     // Protect ourselves ... we had a hang when a NullPointer Exception was
     //  thrown cause our klog thread was still running.
      boolean cleanPackages  = false;
      boolean cleanFiles     = false;
      boolean allocationInfo = false;
      boolean nagMail        = false;
      String forcedoverrides = null;
      boolean doklog         = false;
      try {
         int exitval = 0;
      
         for(int i=0; i < args.length; i++) {
            if        (args[i].equalsIgnoreCase("-cleanPackages")) {
               cleanPackages = true;
            } else if (args[i].equalsIgnoreCase("-cleanFiles")) {
               cleanFiles = true;
            } else if (args[i].equalsIgnoreCase("-cleanAll")) {
               cleanPackages = true;
               cleanFiles = true;
            } else if (args[i].equalsIgnoreCase("-allocationInfo")) {
               allocationInfo = true;
            } else if (args[i].equalsIgnoreCase("-nagmail")) {
               nagMail = true;
            } else if (args[i].equalsIgnoreCase("-klog")) {
               doklog = true;
            } else if (args[i].equalsIgnoreCase("-gsa")) {
               doklog = true;
            } else if (args[i].equalsIgnoreCase("-overrides")) {
               if (forcedoverrides == null) {
                  forcedoverrides  = args[++i];
               } else {
                  forcedoverrides += "\n" + args[++i];
               }
            } else {
               System.out.println("Usage DropboxAccessSrv [-overrides k=v] [-cleanpackages][-cleanfiles][-cleanall][-allocationinfo][-nagmail]");
               exitval++;
            }
         }
         
         DropboxAccessSrv srv = new DropboxAccessSrv(forcedoverrides);
         
        // Setup klog schedule for GSA/AFS as desired
         if (doklog) {
           // If we have AFS/GSA authentication needs, get that thing running
            String wasauthcell  = srv.getProperty("wasProcessAuth_Cell");
            String wasauthpwdir = srv.getProperty("wasProcessAuth_PWDir");
            long   wasinterval  = srv.getLongProperty("wasProcessAuth_Timeout", 12*60);
            String klogpath     = srv.getProperty("klogPath");
            String gsapath      = srv.getProperty("gsaloginPath");
            
            srv.log.warn("Checking for AppServer based File System Authentication");
            if (wasauthcell != null && wasauthcell.length() > 0) {
               if (wasauthpwdir != null && wasauthpwdir.length() > 0) {
                  File pwdir = new File(wasauthpwdir);
                  if (!pwdir.exists() || !pwdir.isDirectory() || !pwdir.canRead()) {
                     srv.log.warn("wasProcessAuth: Error during setup for cell[" + 
                              wasauthcell + "]. PWDir does not exist/dir/readable [" + 
                              wasauthpwdir + "]");
                  } else {
            
                     FSAuthentication auth = new FSAuthentication(wasauthcell, wasauthpwdir);
               
                     if (klogpath != null && klogpath.length() > 0) auth.setKlog(klogpath);
                     if (gsapath  != null &&  gsapath.length() > 0) auth.setGSA(gsapath);
            
                     auth.setLoud(true);
            
                    // Set timeout time if valid number and > 1 minute
                     wasinterval *= 60*1000;
                     if (wasinterval > 60000) {
                        auth.setTimeoutMS(wasinterval);
                        srv.log.info("FSAuthenticator timeout set to " + 
                                 (wasinterval/60000) + " minutes");
                     }
                     
                    // Finally, authenticate, and schedule it to retry. If there is an error,
                    //  it will be verbose (generate alert), and will retry 1/10 of specified
                    //  interval
                     auth.schedule(!auth.reauthenticate());
               
                     srv.log.warn("AppServer based File System Authentication enabled: "
                              + wasauthcell);
                  }
            
               } else {
                  srv.log.warn("wasProcessAuth_Cell was set, but PWDir was NOT!");
               }
            } else {
               srv.log.warn("No File System Authentication as no CELL provided");
            }
         }         
         
         if (cleanPackages) {
            try {
               srv.packageMgr.cleanExpiredPackages();
            } catch(DboxException ex) {
               srv.log.error("Error cleaning packages => " + ex.toString());
               ex.printStackTrace(System.err);
               exitval++;
            }
         }
         if (cleanFiles) {
            try {
               srv.packageMgr.getFileManager().cleanUnreferencedFiles();
            } catch(DboxException ex) {
               srv.log.error("Error cleaning unreffedFiles => " + ex.toString());
               ex.printStackTrace(System.err);
               exitval++;
            }
         }
         if (nagMail) {
            try {
               srv.sendNagMail();
            } catch(DboxException ex) {
               srv.log.error("Error sending nagmail => " + ex.toString());
               ex.printStackTrace(System.err);
               exitval++;
            }
         }
         if (allocationInfo) {
            try {
               DboxFileAllocator falloc = 
                  srv.packageMgr.getFileManager().getFileAllocator();
               Vector v = falloc.getFileAreas();
               Enumeration enum = v.elements();
               System.out.println("\nFILEAREA!PRIORITY!FSTYPE!STATE!DIRECTORY!MAXSPACE!USEDSPACE!COMPINTENDED");
               StringBuffer sb = new StringBuffer();
               while(enum.hasMoreElements()) {
                  DboxFileArea fa = (DboxFileArea)enum.nextElement();
                  sb.setLength(0);
                  sb.append("@FILEAREA:");
                  sb.append("!").append(fa.getPriority());
                  sb.append("!").append(fa.getFSType());
                  sb.append("!").append(fa.getState());
                  sb.append("!").append(fa.getTopLevelDirectory());
                  sb.append("!").append(fa.getTotal());
                  sb.append("!").append(fa.getUsed());
                  sb.append("!").append(fa.getComponentAllocation());
                  System.out.println(sb.toString());
               }
            } catch(Exception ex) {
               srv.log.error("Error getting AlloctionInfo => " + ex.toString());
               ex.printStackTrace(System.err);
               exitval++;
            }
         }
         
         srv.amtMailer.stopThread();
         System.exit(exitval);
         
      } catch(Exception bige) {
         try {
            System.err.println("Yow: DropboxAccess got exception");
            bige.printStackTrace(System.err);
            System.exit(4);
         } catch(Exception BIGE) {
            System.exit(4);
         }
      }
   }
   
   public void sendNagMail() throws DboxException {
   
     // Get a map containing pkgid on Long as key, vector of userid strings as val
      Map map = packageMgr.queryNagInfo(nagInterval());
      Iterator it = map.keySet().iterator();
      while(it.hasNext()) {
         Long pkgid = (Long)it.next();
         DboxPackageInfo pinfo = null;
         try {
            pinfo = packageMgr.lookupPackage(pkgid.longValue());
         } catch(Exception e) {
            log.warn("Exception while looking up package for nagging: " + 
                     pkgid.toString());
            log.warn(e);
            continue;
         }
         
         AMTUser amtowner = null;
         try {
            String owner = pinfo.getPackageOwner();
            Vector amtvec = 
               UserRegistryFactory.getInstance().lookup(owner, false, false, false);
            
            if (amtvec == null || amtvec.size() == 0) {
               throw new DboxException("Error looking up Owner for package for nag");
            }
            
            amtowner = (AMTUser)amtvec.elementAt(0);
         } catch(Exception e) {
            log.warn("Exception while looking up package Owner for nagging: " + 
                     pinfo.toString());
            log.warn(e);
            continue;
         }
            
         Vector v = (Vector)map.get(pkgid);
         Iterator itn = v.iterator();
         while(itn.hasNext()) {
            String name = null;
            try {
               name = (String)itn.next();
               amtMailer.sendAMTMailNag(amtowner, pinfo, name);
            } catch(Exception e) {
               log.warn("Exception while sending nag to user: " + name);
               log.warn(e);
            }
         }
      }
   }
}
