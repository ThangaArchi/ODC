package oem.edge.ed.odc.dropbox.client.soa;

import  oem.edge.ed.odc.dropbox.service.DropboxAccess;
import  oem.edge.ed.odc.dropbox.common.*;
import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.tunnel.common.*;
import  oem.edge.ed.odc.util.*;
import java.net.*;

import com.caucho.hessian.client.*;

import java.lang.reflect.*;

import java.util.*;
import java.io.*;
import java.util.zip.*;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004                                     */
/*                                                                          */
/*     All Rights Reserved                                                  */
/*     US Government Users Restricted Rigts                                 */
/*                                                                          */
/*     The source code for this program is not published or otherwise       */
/*     divested of its trade secrets, irrespective of what has been         */
/*     deposited with the US Copyright Office.                              */
/*                                                                          */
/*   --------------------------------------------------------------------   */
/*     Please do not remove any of these commented lines  20 lines          */
/*   --------------------------------------------------------------------   */
/*                       Copyright Footer Check                             */

/**
 ** This class provides a synchronous (call/return) API to communicate with the
 ** IBM Customer Connect Dropbox service. This API can be used to write a client
 ** to create and retrieve package data in the dropbox.
 **
 ** Note: This is a thin shell over DropboxAccess API, which is the CORRECT API
 **        to use. This exists soley to support apps written to this OLD API
 **        (sftp/webdropbox)
 */
public class sftpDropbox {

  // Set this to have sftpDropbox do a System.exit if fireShutdown called.
  // Intended for use by sftp code.
   protected boolean   sysexit    = false;
   
   protected KeystoreInfo ksi     = null;
   protected KeystoreInfo tsi     = null;
   
  /** 
   * Sets whether the API will call System.exit when the connection to the
   *  Dropbox server is severed. The default is false.
   *
   *@param v         Exit will be performed upon shutdown if True
   */
   public void    setDoSystemExit(boolean v) { sysexit = v;    }
   
  /**
   * Query whether System.exit is performed when dropbox connection is severed.
   */
   public boolean getDoSystemExit()          { return sysexit; }
   
   protected String    userV      = null;
   protected String    companyV   = null;
   protected Vector    projectVec = null;
   
  // Allow 100k megabytes of data to arrive before forcing wait by default
   protected int bufferedDownloadDataSize  =  100 * 1024;
   protected int bufferedUploadDataSize    =  100 * 1024;
   
  /**
   * Set the Maximum buffer size to use when uploading a file
   *
   *@param v   Maximum buffer size to use when uploading a file. Default is 100KB.
   */
   public void setMaxUploadBufferSize(int v)  { bufferedUploadDataSize   = v; }
   
  /**
   * Set the Maximum buffer size to use when downloading a file
   *
   *@param v   Maximum buffer size to use when downloading a file. Default is 100KB.
   */
   public void setMaxDownloadBufferSize(int v){ bufferedDownloadDataSize = v; }
   
   public int  getMaxUploadBufferSize()    { return bufferedUploadDataSize; }
   public int  getMaxDownloadBufferSize()  { return bufferedDownloadDataSize; }
   
  /*-=-=-=-=-=-=-=-=-=- Start Inner Classes -=-=-=-=-=-=-=-=-=-=-*/ 
    
   interface MyClientOperation {
      public ICAOutputStream getOutputStream();
      public ICAInputStream getInputStream();
      public boolean isUpload();
   }
   
   class MyClientDownloadOperation extends StreamingDownloadOperation 
      implements MyClientOperation {
      
      ICAOutputStream los = null;
      
      public MyClientDownloadOperation(DropboxAccess acc, ConnectionFactory fac, 
                                       ICAOutputStream oos, 
                                       long pid, long fid) {
         super(acc, fac, oos, pid, fid);
         los = oos;
         los.getInputStream().setMaxBuffered(bufferedDownloadDataSize); 
      }
      
      public ICAOutputStream getOutputStream() {
         return los;
      }
      public ICAInputStream getInputStream() {
         return los.getInputStream();
      }
      public boolean isUpload() { return false; }
   }
   
   class MyPackageDownloadOperation extends PackageDownloadOperation 
      implements MyClientOperation {
      
      ICAOutputStream los = null;
      
      public MyPackageDownloadOperation(DropboxAccess acc, ConnectionFactory fac, 
                                        ICAOutputStream oos, 
                                        long pid, String encoding) {
         super(acc, fac, oos, pid, encoding);
         los = oos;
         los.getInputStream().setMaxBuffered(bufferedDownloadDataSize); 
      }
      
      public ICAOutputStream getOutputStream() {
         return los;
      }
      public ICAInputStream getInputStream() {
         return los.getInputStream();
      }
      public boolean isUpload() { return false; }
   }
   
   class MyClientUploadOperation extends StreamingUploadOperation
      implements MyClientOperation {
      
      private ICAOutputStream los = null;
      
      public MyClientUploadOperation(DropboxAccess acc, ConnectionFactory fac, 
                                     ICAOutputStream oos, 
                                     long pid, long fid) {
         super(acc, fac, oos.getInputStream(), pid, fid);
         los = oos;
         los.getInputStream().setMaxBuffered(bufferedDownloadDataSize); 
      }
      
      public ICAOutputStream getOutputStream() {
         return los;
      }
      public ICAInputStream getInputStream() {
         return los != null?los.getInputStream():null;
      }
      public boolean isUpload() { return true; }
   }
   

  /*-=-=-=-=-=-=-=-=-=- Start Constructor -=-=-=-=-=-=-=-=-=-=-*/ 

  /**
   * Initializes sftpDropbox. System.out will be set to go to System.err!
   *
   *@param dothatdebug  If set to true, then be prepared for LOTS of debug messages
   */
   public sftpDropbox(boolean dothatdebug) {
      super();
      System.setOut(System.err);
      if (dothatdebug) DebugPrint.setLevel(DebugPrint.DEBUG);
   }
   
  /**
   * Initializes sftpDropbox. System.out redirection can be selectively enabled
   *
   *@param dothatdebug  If set to true, then be prepared for LOTS of debug messages
   *@param redirectStdOutToErr If true, then System.out will be redirected to System.err
   */
   public sftpDropbox(boolean dothatdebug, boolean redirectStdOutToErr) {
      super();
      if (redirectStdOutToErr) {
         System.setOut(System.err);
      }
      if (dothatdebug) DebugPrint.setLevel(DebugPrint.DEBUG);
   }

  /*-=-=-=-=-=-=-=-=-=- Start of sftpDropbox commands -=-=-=-=-=-=-=-=-=-=-*/ 
  
   boolean isConnectedV = false;
   boolean isLoggedInV  = false;
   
  // For used by others, like WebDropbox
   protected String clienttype    = "sftp";
   
  /**
   * Set the client type that will show up in the Dropbox Metrics. This should be
   * a short, uniq moniker (currently used are sftp, gui, web & dropboxftp)
   *
   *@param v    application client type
   */
   public void setClientType(String v) { clienttype = v; }
   
  // For DropboxAccess health
   protected HashMap             sessionmap;
   protected DropboxAccess       dropbox;
   protected ConnectionFactory   factory;
   
   protected static final int TRASH_N   = 1;
   protected static final int INBOX_N   = 2;
   protected static final int SENT_N    = 3;
   protected static final int DRAFTS_N  = 4;
   protected static final int GROUPS_N  = 5;
   protected static final int OPTIONS_N = 6;
   
   
  /**
   * Returns whether or not this object is connected to a Dropbox server
   *
   * @return true if connected
   */
   public boolean isConnected() { return isConnectedV; }
   
  /**
   * Returns whether or not this object is fully logged into a Dropbox server
   *
   * @return true if logged in
   */
   public boolean isLoggedIn()  { return isLoggedInV;  }
      
  /**
   * Connects this object to a Dropbox service
   *
   * @param v Keystore information to use when securing Dropbox connection
   */
   void setKeystoreInfo(KeystoreInfo v) {
      ksi = v;
   }
   
  /**
   * Connects this object to a Dropbox service
   *
   * @param v Truststore information to use when securing Dropbox connection
   */
   void setTruststoreInfo(KeystoreInfo v) {
      tsi = v;
   }
   
   public void refreshSession() {
      if (isConnectedV) {
         try {
            sessionmap = dropbox.refreshSession();
            setSessionId();
         } catch(Exception e) {
            System.out.println("Error refreshing Session!");
            e.printStackTrace(System.out);
         }
      }
   }
      
   protected void setSessionId() throws Exception {
     
      
     // Remove previous timeout (if registered)
      String sessid = factory.getSessionId(dropbox);
      TimeoutManager toMgr = TimeoutManager.getGlobalManager();
      if (sessid != null) {
         toMgr.removeTimeout("REFRESHSESSIONID_" + sessid);
      }
      
     // Set sessionid on factory
      factory.setSessionId(dropbox, sessionmap);
      
     // Register timeout to refresh in half token life
      Long expires = (Long)sessionmap.get(dropbox.Expiration);
      long curtime = System.currentTimeMillis();
      
      long totime = (expires.longValue() - curtime)/2;
      
     // ... but no less than once a minute
      if (totime < 60000) totime = 60000;
      
     // Use a WeakReference to the sftpDropbox object, so it will garbage collect
     //  away if other strong refs are dropped, and this object is not properly
     //  disconnected.
      toMgr.addTimeout(new Timeout(totime, "REFRESHSESSIONID_" + sessid, 
                                   new SFTPWeakTimeoutListener(this)));
   }
   
  /**
   * Connects this object to a Dropbox service
   *
   * @param topurl TOP URL w/context for DropboxService. If null, trys to connect direct
   * @return Returns true if connect worked
   */
   public boolean connect(String topurl) {
      
      try {
      
         String facClassS = null;
         if (topurl == null) {
            facClassS = "oem.edge.ed.odc.dropbox.client.soa.DirectConnectFactory";
//         } else if (useSoap) {
//            facClassS = "oem.edge.ed.odc.dropbox.client.soa.JAXRPCConnectFactory";
         } else {
            facClassS = "oem.edge.ed.odc.dropbox.client.soa.HessianConnectFactory";
         }
      
         Class facClass = Class.forName(facClassS);
         factory = (ConnectionFactory)facClass.newInstance();
         if (topurl != null) factory.setTopURL(new java.net.URL(topurl));
         dropbox = factory.getProxy();
         
         isConnectedV = true;
         
      } catch(Exception ee) {
         System.out.println("Error during connect to: " + (topurl == null? "Local" : topurl));
         ee.printStackTrace(System.out);
         return false;
      }
      
      return isConnectedV;
   }
   
  /**
   * Disconnects this object from the dropbox service. If there was a connection, 
   * and setDoSystemExit(true) has been called, then System.exit will be called.
   *
   * @return Void
   */
   public void disconnect() {
      System.out.println("sftpDropbox:disconnect");
      
      if (isConnectedV) {
         try {
         
            isConnectedV = false;
            dropbox.closeSession();
            
           // Remove the timeout
            String sessid = factory.getSessionId(dropbox);
            TimeoutManager toMgr = TimeoutManager.getGlobalManager();
            if (sessid != null) {
               toMgr.removeTimeout("REFRESHSESSIONID_" + sessid);
            }
            
         } catch(Throwable ee) {
            ee.printStackTrace();
            
         }
         if (getDoSystemExit()) {
            DebugPrint.println(DebugPrint.ERROR, 
                               "sftpDropbox:  ... Ok, System.exit!");
            System.exit(0);
         }
      }
   }
   
  /**
   * After connection to Dropbox service is complete, the login method will 
   * establish the user's identity. 
   * <p>
   * The token should be obtained from a valid authentication service. Use
   * the oem.edge.ed.odc.tunnel.common.Misc.getConnectInfoGeneric static method
   * to obtain the token 
   * (getConnectInfoGeneric(user, pw, "XFR", "", "https://edesign.chips.ibm.com/cc")
   * where the latter parmameter is the toplevel WebSphere address for the Dropbox
   * environment.
   *
   * @param token Encrypted token describing the user credentials
   * @return Returns true if login worked
   * @throws
   */
   public boolean login(String token) throws Exception {
      if (!isConnectedV) {
         System.out.println("dropboxftp:login - Not connected!");
         throw new Exception("No Connected");
      }
      if (isLoggedInV) {
         System.out.println("dropboxftp:login - Already Logged In");
         throw new Exception("Already Logged in");
      }
      
      sessionmap = dropbox.createSession(token);
      
      setSessionId();
            
      userV      = (String)sessionmap.get(dropbox.User);
      companyV   = (String)sessionmap.get(dropbox.Company);
      projectVec = dropbox.getProjectList();
      
      HashMap h = new HashMap();
      h.put(DropboxAccess.OS, 
            System.getProperty("os.name") + " " +
            System.getProperty("os.arch") + " " +
            System.getProperty("os.version"));
      h.put(DropboxAccess.ClientType, clienttype);
      dropbox.setOptions(h);
      
      isLoggedInV = true;
      
      return isLoggedInV;      
   }
   
  /**
   * After connection to Dropbox service is complete, the login method will 
   * establish the user's identity. This version takes user and password string,
   * and is really intended for test. More than likely, the Dropbox service will
   * not allow login in this manner.
   *
   * @param user  userid for user loggin in
   * @param pw    password for user logging in
   * @return Returns true if login worked
   *@throws
   */
   public boolean login(String user, String pw) throws Exception {
      if (!isConnectedV) {
         System.out.println("sftpDropbox:login - Not connected!");
         throw new Exception("No Connected");
      }
      if (isLoggedInV) {
         System.out.println("sftpDropbox:login - Already Logged In");
         throw new Exception("Already Logged in");
      }
      
      sessionmap = dropbox.createSession(user, pw);
      
      setSessionId();
            
      userV      = (String)sessionmap.get(dropbox.User);
      companyV   = (String)sessionmap.get(dropbox.User);
      projectVec = dropbox.getProjectList();
      
      HashMap h = new HashMap();
      h.put(DropboxAccess.OS, 
            System.getProperty("os.name") + " " +
            System.getProperty("os.arch") + " " +
            System.getProperty("os.version"));
      h.put(DropboxAccess.ClientType, clienttype);
      dropbox.setOptions(h);
      
      isLoggedInV = true;
      
      return isLoggedInV;
   }
   
  /**
   * Queries the projects  to which the logged in user has access
   *
   *@return Vector of project names (String) 
   */
   public Vector getUserProjects() {
      if (projectVec != null) return (Vector)projectVec.clone();
      
      return new Vector();
   }
   
  /**
   * Queries the user name or company associated with this Dropbox login.
   *
   *@param userOrCompany If true, querying User, otherwise querying Company
   *@return String representing the requested information
   *@throws
   */
   public String getTokenUserCompany(boolean userOrCompany) throws Exception {
      if (!isConnectedV) {
         System.out.println("sftpDropbox:getTokenUserComp - Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("sftpDropbox:getTokenUser - Not Logged In");
         throw new Exception("Not Logged in");
      }
      
      return userOrCompany?userV:companyV;
   }
   
  /**
   * Returns a FileInfo object describing the file represented by <i>fileid</i>
   *
   *@param fileid unique file id of file in question
   *@return FileInfo object containing attributes for specified file
   *@throws
   */
   public  FileInfo queryFile(long fileid) throws Exception {
      if (!isConnectedV) {
         System.out.println("sftpDropbox:queryFile - Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("sftpDropbox:queryFile - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      return dropbox.queryFile(fileid);
   }
   
  /**
   * Returns a PackageInfo object describing the package represented by <i>packid</i>
   *
   *@param packid unique package id of package in question
   *@return PackageInfo object containing attributes for specified package
   *@throws
   */
   public  PackageInfo queryPackage(long packid) throws Exception {
      if (!isConnectedV) {
         System.out.println("sftpDropbox:queryPackage - Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("sftpDropbox:queryPackage - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      return dropbox.queryPackage(packid, true);
   }
   
  /**
   * Queries packages which the user has available in one of the various folders
   *  (TRASH_N, INBOX_N, SENT_N, DRAFTS_N).
   *
   *@param which specifies which folder to query (TRASH_N, INBOX_N, SENT_N, DRAFTS_N).
   *@return Enumeration of PackageInfo objects in the specified container
   *@throws
   */
   public  Enumeration listInOutSandBox(int which) throws Exception {
      return listInOutSandBox(which, null);
   }
   
  /**
   * Queries packages which the user has available in one of the various folders
   *  (TRASH_N, INBOX_N, SENT_N, DRAFTS_N), and will slim the list down to those
   *  packages whose names start with the specified string. This is NOT regular,
   *  just simple prefix checking.
   *
   *@param which specifies which folder to query (TRASH_N, INBOX_N, SENT_N, DRAFTS_N).
   *@return Enumeration of PackageInfo objects in the specified container
   *@throws
   */
   public  Enumeration listInOutSandBox(int which, String n)
      throws Exception {
      
      if (!isConnectedV) {
         System.out.println("sftpDropbox:listInOutSandBox - Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("sftpDropbox:listInOutSandOrInBox - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      if (which < TRASH_N || which > DRAFTS_N) {
         System.out.println("sftpDropbox:listInOutSandOrInBox - Bad Which");
         throw new Exception("Which value is invalid: " + which);
      }
      
      Vector invec = dropbox.queryPackages(n, false, 
                                           which > INBOX_N,  // OwnerOrAccessor
                                           false,            // Filter Completed
                                           which == INBOX_N, // Filter Marked
                                           true);            // Full Detail
      
      Vector vec = new Vector();
      int num = invec.size();
      for(int i=0 ; i < num; i++) {
         PackageInfo info = (PackageInfo)invec.elementAt(i);
         
        // Add the element if complete and NOT SANDBOX query
         if (info.getPackageStatus() == DropboxGenerator.STATUS_COMPLETE) {
            if (which != DRAFTS_N) {
            
              //
              // Also, don't add if its NOT marked and its trash
              //  Marked packages are already filtered out by server if we 
              // are looking at inbox
              //
               if (which != TRASH_N || info.getPackageMarked()) {
                  vec.addElement(info);
               }
            }
            
        // ... OR, not complete and IS SANDBOX
         } else if (which == DRAFTS_N) {
            vec.addElement(info);
         }
      }
      return vec.elements();
   }
   
  /**
   * Queries the file contents of a specific package (specified by packid)
   *
   *@param packid unique package id of package in question
   *@return Enumeration of FileInfo objects contained in the specified package
   *@throws
   */
   public  Enumeration listPackageContents(long packid) throws Exception {
      if (!isConnectedV) {
         System.out.println("sftpDropbox:listPackageContents -Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("sftpDropbox:listPackageContents - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      Vector vec = dropbox.queryPackageContents(packid);
         
      return vec.elements();
   }
   
  /**
   * Queries the Package Access list associated with a specific package
   *
   *@param packid unique package id of package in question
   *@return Enumeration of AclInfo objects associated with the specified package
   *@throws
   */
   public  Enumeration queryAcls(long packid) throws Exception {
      if (!isConnectedV) {
         System.out.println("sftpDropbox:queryAcls -Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("sftpDropbox:queryAcls - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      Vector invec = dropbox.queryPackageAcls(packid, true);
      
      int  num     = invec.size();
      Vector vec = new Vector();
      for(int i=0 ; i < num; i++) {
         AclInfo info = (AclInfo)invec.elementAt(i);
         if (info.getAclStatus() == DropboxGenerator.STATUS_PROJECT) {
            vec.addElement("_P_" + info.getAclName());
         } else if (info.getAclStatus() == DropboxGenerator.STATUS_GROUP) {
            vec.addElement("_G_" + info.getAclName());
         } else {
            vec.addElement(info.getAclName());
         }
      }
         
      return vec.elements();
   }
   
  /**
   * Deletes the specific package represented by <i>packid</i>
   *
   *@param packid unique package id of package in question
   *@return Returns true if operation worked, but not used, as if 
   *         unsuccessful, an exception is thrown
   *@throws
   */
   public  boolean deletePackage(long packid) throws Exception {
      if (!isConnectedV) {
         System.out.println("sftpDropbox:deletePackage -Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("sftpDropbox:deletePackage - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      dropbox.deletePackage(packid);
      
      return true;
   }
   
  /**
   * Marks or Unmarks the specific package represented by <i>packid</i>. If a package
   * is marked, it will show up in the Trash folder, and if unmarked, will show up in
   * the Inbox.
   *
   *@param packid unique package id of package in question
   *@param markOrUnmark  true if marking the package, false to unmark
   *@return Returns true if operation worked, but not used, as if 
   *         unsuccessful, an exception is thrown
   *@throws
   */
   public  boolean markPackage(long packid, boolean markOrUnmark)
      throws Exception {
       
      if (!isConnectedV) {
         System.out.println("sftpDropbox:markPackage -Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("sftpDropbox:markPackage - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      dropbox.markPackage(packid, markOrUnmark);
      
      return true;
   }
   
  /**
   * Commits the specific package represented by <i>packid</i>. A package in Drafts
   * with no file errors, and which has at least one valid ACL can be committed. The
   * act of committing a package can be thought of like sending an email. Once sent, 
   * the package can no longer be changed (though it can be deleted, and the ACL list
   * may be altered).
   *
   *@param packid unique package id of package in question
   *@return Returns true if operation worked, but not used, as if 
   *         unsuccessful, an exception is thrown
   *@throws
   */
   public  boolean commitPackage(long packid) throws Exception {
      if (!isConnectedV) {
         System.out.println("sftpDropbox:commitPackage -Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("sftpDropbox:commitPackage - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      dropbox.commitPackage(packid);
      
      return true;
   }
   
  /**
   * Creates a new package with the specified name <i>packname</i> in the Drafts
   * folder. Package names MUST be unique in Drafts, so a failure will occur if
   * a uncommitted package with the same name already exists. 
   *
   *@param packid unique package id of package in question
   *@return Returns true if operation worked, but not used, as if 
   *         unsuccessful, an exception is thrown
   *@throws
   */
   public  boolean createPackage(String packname) throws Exception {
      if (!isConnectedV) {
         System.out.println("sftpDropbox:createPackage -Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("sftpDropbox:createPackage - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      dropbox.createPackage(packname);
      
      return true;
   }
   
  /**
   * Sets the expiration date on the specified package to be <i>exp</i>
   * days into the future from the moment of the call.  The current maximum
   * number of days which many be specified is 14, though it is a configurable
   * option. A new method will be added allow client to query the maximum
   * value that can be specified.
   *
   *@param packid unique package id of package in question
   *@param exp number of days the package should live
   *@return Returns true if operation worked, but not used, as if 
   *         unsuccessful, an exception is thrown
   *@throws
   */
   public  boolean setPackageExpiration(long packid, 
                                        long exp) throws Exception {
      if (!isConnectedV) {
         System.out.println("sftpDropbox:changeExpiration -Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("sftpDropbox:changeExpiration - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      dropbox.changePackageExpiration(packid, (new Date()).getTime() + (exp*1000*60*60*24));
         
      return true;
   }
   
   
  /**
   * Sets the provided descriptive text as a property on the package.
   *
   *@param packid unique package id of package in question
   *@param desc   description text
   *@return Returns true if operation worked, but not used, as if 
   *         unsuccessful, an exception is thrown
   *@throws
   */
   public  boolean setPackageDescription(long packid, 
                                         String desc) throws Exception {
      if (!isConnectedV) {
         System.out.println("sftpDropbox:setPkgDesc -Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("sftpDropbox:setPkgDesc - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      dropbox.setPackageDescription(packid, desc);
         
      return true;
   }
   
  /**
   * Removes an Access list (acl) item from a specific package. The acl is specified
   * using a prefix of "_P_" if its a project, and "_G_" if its a group. If its a
   * simple user, no prefix is added.
   *
   *@param packid unique package id of package in question
   *@param acl prefixed name of acl
   *@return Returns true if operation worked, but not used, as if 
   *         unsuccessful, an exception is thrown
   *@throws
   */
   public  boolean removeAcl(long packid, String acl) throws Exception {
      if (!isConnectedV) {
         System.out.println("sftpDropbox:removeAcl - Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("sftpDropbox:removeAcl - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      byte type = DropboxGenerator.STATUS_NONE;
      if (acl.indexOf("_P_") == 0) {
         type = DropboxGenerator.STATUS_PROJECT;
         acl = acl.substring(3);
      } else if (acl.indexOf("_G_") == 0) {
         type = DropboxGenerator.STATUS_GROUP;
         acl = acl.substring(3);
      }
      
      dropbox.removePackageAcl(packid, acl, type);
      
      return true;
   }
   
  /**
   * Removes the access list (acl) item from a specific package. 
   *
   *@param packid unique package id of package in question
   *@param acl AclInfo describing the name and type (status) of acl to remove
   *@return Returns true if operation worked, but not used, as if 
   *         unsuccessful, an exception is thrown
   *@throws
   */
   public  boolean removeAcl(long packid, AclInfo acl) throws Exception {
      if (!isConnectedV) {
         System.out.println("sftpDropbox:removeAcl - Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("sftpDropbox:removeAcl - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      dropbox.removePackageAcl(packid, acl.getAclName(), acl.getAclStatus());
      
      return true;
   }
   
   
  /**
   * Adds the access list (acl) item to a specific package.  The acl is specified
   * using a prefix of "_P_" if its a project, and "_G_" if its a group. If its a
   * simple user, no prefix is added.
   *
   *@param packid unique package id of package in question
   *@param acl prefixed name of acl
   *@return Returns true if operation worked, but not used, as if 
   *         unsuccessful, an exception is thrown
   *@throws
   */
   public  boolean addAcl(long packid, String acl) throws Exception {
      if (!isConnectedV) {
         System.out.println("sftpDropbox:addAcl - Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("sftpDropbox:addAcl - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      byte type = DropboxGenerator.STATUS_NONE;
      if (acl.indexOf("_P_") == 0) {
         type = DropboxGenerator.STATUS_PROJECT;
         acl = acl.substring(3);
      } else if (acl.indexOf("_G_") == 0) {
         type = DropboxGenerator.STATUS_GROUP;
         acl = acl.substring(3);
      }
      
      dropbox.addPackageAcl(packid, acl, type);
      
      return true;
   }
   
  /**
   * Adds the access list (acl) item to a specific package. 
   *
   *@param packid unique package id of package in question
   *@param acl AclInfo describing the name and type (status) of acl to remove
   *@return Returns true if operation worked, but not used, as if 
   *         unsuccessful, an exception is thrown
   *@throws
   */
   public  boolean addAcl(long packid, AclInfo acl) throws Exception {
      if (!isConnectedV) {
         System.out.println("sftpDropbox:addAcl - Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("sftpDropbox:addAcl - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      dropbox.addPackageAcl(packid, acl.getAclName(), acl.getAclStatus());
      
      return true;
   }
   
  /**
   * Deletes a specific file (item) from a specific package
   *
   *@param packid unique package id of the package in question
   *@param fileid unique file id of the file in question
   *@return Returns true if operation worked, but not used, as if 
   *         unsuccessful, an exception is thrown
   *@throws
   */
   public  boolean deleteFileFromPackage(long packid, 
                                         long fileid) throws Exception {
      if (!isConnectedV) {
         System.out.println("sftpDropbox:deleteFileFromPackage - No connect!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("sftpDropbox:deleteFileFromPackage - No LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      dropbox.removeItemFromPackage(packid, fileid);
      
      return true;
   }
   
   public  Operation uploadFile(long packid, String file) throws Exception {
      if (!isConnectedV) {
         System.out.println("sftpDropbox:uploadFile -Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("sftpDropbox:uploadFile - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      long itemid = dropbox.uploadFileToPackage(packid, file, 
                                                StreamingUploadOperation.DEFAULT_SIZE);
      
      ICAOutputStream os = new ICAOutputStream(new ICAInputStream());
      
      MyClientUploadOperation cujo = new MyClientUploadOperation(dropbox, factory,
                                                                 os, packid, itemid);
      cujo.process();
      
      return cujo;
   }
   
   public  Operation downloadFile(long packid, long fileid) throws Exception {
      if (!isConnectedV) {
         System.out.println("sftpDropbox:downloadFile - Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("sftpDropbox:downloadFile - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      FileInfo finfo = dropbox.queryFile(fileid);
      
      ICAOutputStream os = new ICAOutputStream(new ICAInputStream());
      
      MyClientDownloadOperation cdo = new MyClientDownloadOperation(dropbox, factory, 
                                                                    os, packid, fileid);
      cdo.process();
      
      return cdo;
   }
   
  /**
   * Download all the files of a specific package using a supported encoding
   *
   * An Operation object is returned as a successful result of this method. Data
   * can then be transferred from the dropbox using the readFileData method. When
   * all of the data has been downloaded, the closeOperation method should be called
   * to complete the transfer. Its suggested this be done in a finally clause
   * <p>
<pre>   
      try {
         ...  dropbox.readFileData(...);
      } catch(Exception ex)
         ...
      } finally {
        // Make sure operation is closed
         try {
            if (operation != null) {
               boolean successOperation=dropbox.closeOperation(operation);
            }
         } catch(Exception ee) {}
      }
</pre>      
   *
   *@param packid Unique package id of the package in question
   *@param encoding The encoding to be applied to the files in the package. Current
   *                supported encodings are "tgz", "tar", and "zip"
   *@return Operation object which can be used to stream data from the dropbox
   *@throws
   */
   public  Operation downloadPackage(long packid, String encoding)
      throws Exception {
      
      if (!isConnectedV) {
         System.out.println("sftpDropbox:downloadFile - Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("sftpDropbox:downloadFile - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      ICAOutputStream os = new ICAOutputStream(new ICAInputStream());
      
      MyPackageDownloadOperation cdo = 
         new MyPackageDownloadOperation(dropbox, factory, os, packid, encoding);
      cdo.process();
      
      return cdo;      
   }
   
  /**
   * Attempts to close the specified operation object provided
   *
   *@param op   Operation created using downloadFile, downloadPackage, or uploadFile
   *@return Returns true if operation worked, false otherwise
   */
   public boolean closeOperation(Operation op) {
      boolean ret = false;
      
      DebugPrint.println(DebugPrint.DEBUG, "closeOperation: \n" + op.toString());
         
      MyClientOperation mop = (MyClientOperation)op;
      
      if        (mop == null) {
         DebugPrint.printlnd(DebugPrint.ERROR, 
                             "Zoinks! op === null!");
         
      } else if (mop.isUpload()) {
         if (op.getStatus() <= Operation.STATUS_INPROGRESS) {
            
            DebugPrint.println(DebugPrint.DEBUG, "MyUploadOp");
            
           // Delay till all the proto packets are sent 
           //   (numoutstanding packets == 0).
           //
           // For upload, we set the filesize to a HUGE value. Check that 
           //  WE think we sent that much
           //
            
           // Get the OutputStream and close it. This signals to the uploader
           //  that we are done. Since the uploader may be caching data, he
           //  will flush and 
            try {
               mop.getOutputStream().close();            
            } catch(IOException ioe) {}
            
           // Give it 1 minute 
            op.waitForCompletion(60000);
            op.abort();
            
            ret = op.validate();
            
         } else {
            DebugPrint.printlnd(DebugPrint.WARN, 
                                "Asked to close and not in progress");
         }
      } else {
         
         if (op.getStatus() <= Operation.STATUS_INPROGRESS) {
         
            try {
               mop.getInputStream().close();            
            } catch(IOException ioe) {}
            
            try {
               mop.getOutputStream().close();            
            } catch(IOException ioe) {}
            
            op.abort();
            op.waitForCompletion();
         }
         ret = op.validate();
      }
      
      if (!ret) {
         DebugPrint.println(DebugPrint.WARN,
                            "closeOperation: ret=" + ret + "\n" + op.toString());
      }
      return ret;
   }

  /**
   * Reads data from an operation object created using the downloadFile or
   *  downloadPackage methods
   *
   *@param op   Operation created using downloadFile, downloadPackage, or uploadFile
   *@param buf  byte array into which the data will be placed
   *@param ofs  offset into buf to start writing
   *@param len  total number of bytes for read attempt
   *@return Returns number of bytes read. If this value is <= 0, then data trasfer
   *        is complete. Check Operation status (op.getStatus() == 
   *        Operation.STATUS_FINISHED) to validate that transfer was successful.
   */
   
   public int readFileData(Operation op, byte buf[], 
                           int ofs, int len) {
      int ret = -1;
      
      MyClientOperation mop = (MyClientOperation)op;
      ICAInputStream is     = mop.getInputStream();
      int            avail  = is.available();
      int            status = op.getStatus();
      if (status <= Operation.STATUS_INPROGRESS ||
          is.available() > 0) {
          
         ret = is.read(buf, ofs, len);
         
      } else if (status == Operation.STATUS_FINISHED) {
         ret = 0;
      }
      if (ret == -1) {
         DebugPrint.println(DebugPrint.DEBUG,
                            "readFileData: -1: Operation Status = " + 
                            status);
      }
      return ret;
   }
   
  /**
   * Writes data to an operation object created using the uploadFile method.
   *
   *@param op   Operation created using downloadFile, downloadPackage, or uploadFile
   *@param buf  byte array from which the data will be read
   *@param ofs  offset into buf to start reading
   *@param len  total number of bytes for write attempt
   *@throws
   */
   public void writeFileData(Operation op, byte buf[], 
                             int ofs, int len) throws IOException {
      MyClientOperation mop = (MyClientOperation)op;
                                
      DebugPrint.println(DebugPrint.DEBUG,
                         "writeFileData: len=" + len
                         + " avail=" + mop.getInputStream().available() + 
         " status = " + op.getStatus());
                                
      if (op.getStatus() <= Operation.STATUS_INPROGRESS) {
         mop.getOutputStream().write(buf, ofs, len);
      } else {
         DebugPrint.println(DebugPrint.DEBUG, "Throwing!");
         throw new IOException("Bad Status for write");
      }
   }
   
  /**
   * Creates a Dropbox Group of the specified name. The group will have no
   * members, and will be setup as a private group.  The group names MUST be
   * unique for all users. Its suggested that user prepend their userid to all
   * their groups (or use some less verbose, semi-unique prefix).
   *
   *@param group Name of group to be created
   *@return Returns true if operation worked, but not used, as if 
   *         unsuccessful, an exception is thrown
   *@throws
   */
   public boolean createGroup(String group) throws Exception {
      if (!isConnectedV) {
         System.out.println("sftpDropbox:createGroup - Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("sftpDropbox:createGroup - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      byte vb = DropboxGenerator.GROUP_SCOPE_NONE;
      dropbox.createGroup(group, vb, vb);
      
      return true;
   }
   
  /**
   * Attempts to delete the specified Dropbox Group. Only the owner of the group
   * can remove the group.
   *
   *@param group Name of group to be created
   *@return Returns true if operation worked, but not used, as if 
   *         unsuccessful, an exception is thrown
   *@throws
   */
   public boolean deleteGroup(String group) throws Exception {
      if (!isConnectedV) {
         System.out.println("sftpDropbox:deleteGroup - Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("sftpDropbox:deleteGroup - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      dropbox.deleteGroup(group);
      
      return true;
   }
   
  /**
   * Adds either a member or access enabled user to the specified group. Anyone
   * who is in the access list of the group has the same authority to change the
   * group attributes as the group owner, except for group deletion. Also, the
   * group owner is implicitly part of the access list.
   * <p>
   * The Member list of the group specifies who is 'in' the group. When a group 
   * is added as an ACL to a package, all the members are potential recipients.
   *
   *@param group Name of group to be modified
   *@param user  Name of the user (ccid) being added to the member or access list
   *@param memberOrAccess Set to true if this user is being added as a member,
   *                      otherwise this is an Access list add.
   *@return Returns true if operation worked, but not used, as if 
   *         unsuccessful, an exception is thrown
   *@throws
   */
   public boolean addGroupMemberAccess(String group, String user, 
                                       boolean memberOrAccess) 
      throws Exception {
      
      if (!isConnectedV) {
         System.out.println("sftpDropbox:addGrpMemAcc - Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("sftpDropbox:addGrpMemAcc - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      dropbox.addGroupAcl(group, user, memberOrAccess);
      
      return true;
   }
   
  /**
   * Removes a user from the member or access list of a group
   *
   *@param group Name of group to be modified
   *@param user  Name of the user (ccid) being removed from the member or access list
   *@param memberOrAccess Set to true if this user is being removed as a member,
   *                      otherwise this is an Access list remove.
   *@return Returns true if operation worked, but not used, as if 
   *         unsuccessful, an exception is thrown
   *@throws
   */
   public boolean removeGroupMemberAccess(String group, String user, 
                                          boolean memberOrAccess) 
      throws Exception {
      
      if (!isConnectedV) {
         System.out.println("sftpDropbox:rmGrpMemAcc - Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("sftpDropbox:rmGrpMemAcc - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      dropbox.removeGroupAcl(group, user, memberOrAccess);
      
      return true;
   }
   
  /**
   * Modifies the Visibility or Listability attributes of a specified group
   * <p>
   * The Visibility attribute determines who can use the group as a Package ACL
   * while the Listability attribute determine who can list the members of the group.
   * A user must have Visibility to a group for the Listability attribute to be 
   * meaningful. There are three levels for both these attributes, MEMBER, OWNER and
   * ALL. Using ALL is discouraged, and may be turned off in certain implementations,
   * due to security concerns.
   *
   *@param group Name of group to be modified
   *@param v     Scope for the attribute (GROUP_SCOPE_MEMBER or GROUP_SCOPE_OWNER)
   *@param visOrList  Set to true if the attribute value being modified is Visibility,
   *                      otherwise Listability is modified.
   *@return Returns true if operation worked, but not used, as if 
   *         unsuccessful, an exception is thrown
   *@throws
   */
   public boolean setGroupAttributes(String group, byte v, boolean visOrList) 
      throws Exception {
      
      if (!isConnectedV) {
         System.out.println("sftpDropbox:setGrpAttr - Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("sftpDropbox:setGrpAttr - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      byte l = visOrList? DropboxGenerator.GROUP_SCOPE_NONE: v;
      v      = visOrList? v                                : DropboxGenerator.GROUP_SCOPE_NONE;
      
      dropbox.modifyGroupAttributes(group, v, l);
      
      return true;
   }
   
  /**
   * Returns a Hashtable containing all of the GroupInfo objects to which the 
   * user has visibility., as the value, with the group name as the key.
   *
   *@return Returns Hashtable of groupname to GroupInfo objects
   *@throws
   */
   public Hashtable listGroups() throws Exception {
      
      if (!isConnectedV) {
         System.out.println("sftpDropbox:listGroups - Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("sftpDropbox:listGroups - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      Hashtable ret = new Hashtable();
      
      ret.putAll(dropbox.queryGroups(null, false, true, true));
      
      return ret;
   }
   
  /**
   * Returns the GroupInfo object for a specific group name
   *
   *@return Returns GroupInfo object
   *@throws
   */
   public GroupInfo listGroup(String group) throws Exception {
      
      if (!isConnectedV) {
         System.out.println("sftpDropbox:listGroup - Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("sftpDropbox:listGroup - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      Map map = dropbox.queryGroups(group, false, true, true);
      
      GroupInfo gi = (GroupInfo)map.get(group);
      if (gi == null) throw new Exception("Group not listable: " + group);
      return gi;
   }
   
  /**
   * Returns a Hashtable filled with all global options in effect for the user.
   * <p>
   * Current set of pertinent options are:
   *   <ul>
   *   <li>NewPackageEmailNotification - true if the user wants to receive email 
   *                                     notification when a package is available in
   *                                     his/her inbox
   *   <li>ReturnReceiptDefault        - true if the default is for new packages to 
   *                                     have return receipt enabled when received
   *   <li>SendNotificationDefault     - true if the default is for new packages to
   *                                     have email notification enabled when sent
   *   <li>ShowHidden                  - true if the user wants to see packages which
   *                                     are marked hidden
   *   </ul>
   *<p>
   * All options have true/false values
   *
   *@return Returns Hashtable of OptionName to OptionValue mappings
   *@throws
   */
   public Hashtable getOptions() throws Exception {
      if (!isConnectedV) {
         System.out.println("sftpDropbox:getOptions - Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("sftpDropbox:getOptions - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      Hashtable ret = new Hashtable();
      ret.putAll(dropbox.getOptions());
      
      return ret;
   }
   
  /**
   *Returns the option value specified by <i>key</i>
   *
   *@return Value of specified option
   *@throws
   */
   public String getOption(String key) throws Exception {
      if (!isConnectedV) {
         System.out.println("sftpDropbox:getOption - Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("sftpDropbox:getOption - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      return dropbox.getOption(key);
   }
   
   public boolean setOption(String key, String val) throws Exception {
      if (!isConnectedV) {
         System.out.println("sftpDropbox:setOption - Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("sftpDropbox:setOption - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      dropbox.setOption(key, val);
      
      return true;
   }
   
   public boolean setPackageFlags(long pkgid, int msk, 
                                 int vals) throws Exception {
      if (!isConnectedV) {
         System.out.println("sftpDropbox:setPackageFlag - Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("sftpDropbox:setPackageFlag - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      dropbox.setPackageFlags(pkgid, msk, vals);
      
      return true;
   }
   
   
   protected void finalize() throws Throwable {
      try {
         disconnect();
      } catch(Exception ee) {}
   }
   
   public static void main(String args[]) {
   }
   
   class GetAThread extends Thread {
      sftpDropbox v = null;
      GetAThread(sftpDropbox v) { this.v = v; }
      public void run() {
         sftpDropbox.giveMeAJavaThread(v);
      }
   }
   public void getMeAJavaThread() {
      Thread thrd = new GetAThread(this);
      thrd.setName("StolenJavaThread");
      thrd.start();
      Byte b = new Byte((byte)0);
     // Wait forever
      while(true) {
         try {
            synchronized(b) { b.wait(); }
         } catch(Throwable tt) {}
      }
   }
   public static native void giveMeAJavaThread(sftpDropbox a);
}


