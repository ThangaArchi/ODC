package oem.edge.ed.odc.dropbox.client;

import  oem.edge.ed.odc.applet.CodeUpdater;
import  oem.edge.ed.odc.dropbox.common.*;
import  oem.edge.ed.odc.dropbox.service.DropboxAccess;
import  oem.edge.ed.odc.dropbox.service.helper.*;
import  oem.edge.ed.util.SearchEtc;
import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.tunnel.common.DebugPrint;
import  oem.edge.ed.odc.tunnel.common.URLConnection2;
import  oem.edge.ed.odc.tunnel.common.Misc;
import  oem.edge.ed.odc.util.*;
import  com.ibm.as400.webaccess.common.*;
import  java.lang.reflect.*;


import java.awt.event.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import java.net.*;
import java.util.zip.*;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004,2005,2006                           */
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



public class DropboxCmdline implements Runnable, /*TimeoutListener,*/ SessionListener {
   
   static public final String parseableHelp = 
   "  parseable reply codes:\n\n" + 
   "    100 - informational message\n" + 
   "    101 - partial success for command\n" + 
   "    102 - partial failure for command\n" + 
   "    105 - ready for command\n" + 
   "    106 - Software out of date\n" + 
   "    107 - Software MAY be out of date\n" + 
   "    125 - transfer started\n" + 
   "    197 - prompting on mput/mget\n" + 
   "    198 - send ABORT to abort transfer, or just newline to continue\n" + 
   "    199 - up/download status\n" + 
   "           %complete:XfrRate:Duration:Finish:Xfered:Remain\n" + 
   "               0-100: KB/sec:    secs:  secs: bytes: bytes\n" + 
   "             (eg. 95:250:35:2:8960000:471579)\n" + 
   "    200 - command successful\n" + 
   "    201 - partial command success (at least on 101)\n" + 
   "    230 - User logged in, proceed.	After successful login\n" + 
   "    250 - requested file action OK. Completed\n" + 
   "    331 - User name okay, need password.	Prompting for password\n" + 
   "    333 - Using HTTP Proxy, need proxy user\n" + 
   "    334 - Using HTTP Proxy, need proxy password\n" + 
   "    421 - Connection shutdown or exiting\n" + 
   "    426 - Transfer aborted (or error).\n" + 
   "    431 - Authentication failed\n" + 
   "    500 - syntax error - command not recognized\n" + 
   "    501 - syntax error - bad parameters for command\n" + 
   "    503 - command failure (general failure)\n" + 
   "  Format:   $$cmd:command:xxx:lineN:text\n\n" + 
   "  The prefix of $$cmd: is an indicator that the line is intended for\n" + 
   "  parsing. If lineN=1, then its the start of a reply. Any other value\n" + 
   "  for line indicates that its just an additional line for a previous\n" + 
   "  command. The reply=xxx is the code indicating success or failure.\n";
   
   int maxParseableUsed  = 0;
   int lastParseableUsed = 0;
   int num101            = 0;
   int num102            = 0;
   boolean parseable     = false;
   String  currentCmd    = "NONE";
   int parseableLumpingLine = 0;
   
   public void clearParseable() {
      currentCmd           = "NONE";
      maxParseableUsed     = 0;
      lastParseableUsed    = 0;
      num101               = 0;
      num102               = 0;
      parseableLumpingLine = 0;
   }
   
   public void finishParseable() {
      if (parseable) {
         if (maxParseableUsed < 200) {
            if ((num101 == 0 && num102 == 0) || (num101 > 0 && num102 == 0)) {
               showmsg(200, true, "Ok");
            } else if (num101 == 0) {
               showmsg(503, true, "Failed");
            } else if (num101 == 0) {
               showmsg(201, true, "Partial");
            }
         }
      }         
      clearParseable();
   }
   
   protected void showmsg(int code, boolean allowLumping, String s) {
      showmsgnoNL(code, allowLumping, s + "\n");
   }
   protected void showmsgnoNL(int code, boolean allowLumping, String s) {
      if (parseable) {
         if      (code == 101) num101++;
         else if (code == 102) num102++;
         if (code > maxParseableUsed) {
            maxParseableUsed = code;
         }
         
         if (!allowLumping || code != lastParseableUsed) {
            parseableLumpingLine = 1;
         }
         
         lastParseableUsed = code;
         
         int cidx = 0;
         int nlidx;
         while((nlidx = s.indexOf('\n', cidx)) >= 0) {
            System.out.println("$$cmd:" + currentCmd + ":" + code + 
                               ":" + (parseableLumpingLine++) + ":" + 
                               s.substring(cidx, nlidx));
            cidx = nlidx+1;
         }
         
         if (cidx < s.length()) {
            System.out.println("$$cmd:" + currentCmd + ":" + code + 
                               ":" + (parseableLumpingLine++) + ":" + 
                               s.substring(cidx));
         }
      } else {
         System.out.print(s);
      }
   }
   
  // This method will return the entire string if there is no <@-@> marker,
  //  or, if there is one found, only what is to the right of the marker
   protected String massageError(String err) {
      int idx = err.indexOf("<@-@>");
      if (idx >= 0) {
         err = err.substring(idx+5);
      }
      return err;
   }
   
   protected void showerror(int code, boolean allowLumping, String s) {
      showmsg(code, allowLumping, s);
      if (rc < 6) rc = 6;
      if (stoponerr) {
         showmsg(421, true, "StopOnError specified ... shutting down");
         shutdownAndExit();
      }
   }
         
  // Sort Vector of Strings or Pathinfo objects
   public void sortVector(Vector v, boolean ascDesc, boolean uniqIt) {
      int i,j;
      int count = v.size();
      int end = count;
      boolean swapped = true;
      for (i=0; i<count-1 && swapped; i++) {
        //swapped = false;
         for (j=0; j<end-1; j++) {
            Object o1, o2;
            o1 = v.elementAt(j);
            o2 = v.elementAt(j+1);
            int compval = -1;
            if (o1 instanceof String) {
               compval = ((String)o2).compareTo((String)o1);
            } else if (o1 instanceof PathInfo) {
               compval = ((PathInfo)o2).path.compareTo(((PathInfo)o1).path);
            } else if (o1 instanceof FileInfo) {
               compval = ((FileInfo)o2).getFileName().compareTo(((FileInfo)o1).getFileName());
            } else if (o1 instanceof PackageInfo) {
               compval = ((PackageInfo)o2).getPackageName().compareTo(((PackageInfo)o1).getPackageName());
            } else if (o1 instanceof AclInfo) {
               compval = ((AclInfo)o2).getAclName().compareTo(((AclInfo)o1).getAclName());
            } else if (o1 instanceof GroupInfo) {
               compval = ((GroupInfo)o2).getGroupName().compareTo(((GroupInfo)o1).getGroupName());
            }
            
            if((ascDesc && compval < 0) || (!ascDesc && compval > 0)){
               v.setElementAt(o2, j);
               v.setElementAt(o1, j+1);
               swapped = true;
              //System.out.println("Swapping " + o1 + ", " + o2);
            } else if (uniqIt && o1.equals(o2)) {
              //System.out.println("EQUAL UNiqueIt " + o1 + "," + o2);
               v.removeElementAt(j+1);
               count--;
               end--;
               j--;
              //} else {
              //System.out.println("NOSWAP g " + o1 + ", " + o2);
            }
         }
         end--;
      }
   }
                            
   long statOverrideBytes = 0;
                            
   class AbortAllException extends Exception {
      public AbortAllException(String m) {
         super(m);
      }
      public AbortAllException() {
         super();
      }
   }
                            
   static boolean dotimer = false;
   class Timer {
      long start;
      long last;
      
      public Timer() {
         start();
      }
      
      public void start() {
         if (!dotimer) return;
         start = System.currentTimeMillis();
         last  = start;
      }
      
      public long delta(String s) {
         if (!dotimer) return 0L;
         
         long cur = System.currentTimeMillis();
      
         long delt = cur - last;
         last = cur;
         if (s != null && dotimer) {
            System.out.println("Timer delt: " + s + ": " + delt);
         }
         return delt;
      }
      
      public long elapsed(String s) {
         if (!dotimer) return 0L;
         
         last = System.currentTimeMillis();
      
         long delt = last - start;
         if (s != null && dotimer) {
            System.out.println("Timer elap: " + s + ": " + delt);
         }
         return delt;
      }
   }
   
   Timer timer = new Timer();
   
   boolean getDebug() { return DebugPrint.doDebug(); }
                            
   boolean isConnectedV = false;
   boolean isLoggedInV  = false;
   
  // HashMap sessionmap = null;
   SessionHelper sessionHelper;
   
   boolean   printexception = false;
   
   DropboxAccess dropbox = null;
   
   long    storagepool = DropboxAccess.PUBLIC_POOL_ID;
   boolean itarpackage = false;
   
   ConnectionFactory factory = null;
   
   
  /*
   protected void setSessionId() throws DboxException, java.rmi.RemoteException {
   
      factory.setSessionId(dropbox, sessionmap);
      
      Long expires = (Long)sessionmap.get(dropbox.Expiration);
      long curtime = System.currentTimeMillis();
      
      TimeoutManager toMgr = TimeoutManager.getGlobalManager();
      long totime = (expires.longValue() - curtime)/2;
      
     // No more than once a minute
      if (totime < 60000) totime = 60000;
      
      toMgr.removeTimeout("REFRESHSESSIONID");
      toMgr.addTimeout(new Timeout(totime, "REFRESHSESSIONID", this));
   }
   
   
  // We process the Timeout here for REFRESHSESSIONID
   public void tl_process(Timeout t) {
      try {
         sessionmap = dropbox.refreshSession();
         setSessionId();
      } catch(Exception dbe) {
         System.out.println("Error refreshing Session!");
         SearchEtc.printStackTrace(dbe, System.out);
      }
   }
  */
   
  // This method will be called for the various events available on the
  //  SessionHelper. 
   public void sessionUpdate(SessionEvent e) {
      if        (e.isInactivity()) {
         showmsg(100, false, "Idle session detected. Shutdown ...");
         shutdownAndExit();
      } else if (e.isShutdown()) {
         showmsg(100, false, "Session shutdown complete");
      } else if (e.isRefreshError()) {
      
         showmsg(100, false, "Refresh error occurred ... retry scheduled");
         if (e.getCause() != null) {
            showmsg(100, false, SearchEtc.getStackTrace(e.getCause()));
         }
      }
   }
   
   public boolean isConnected() { return isConnectedV; }
   public boolean isLoggedIn()  { return isLoggedInV;  }
   
   public void connect() throws Exception {
   
      String facClassS = null;
      if (dodirect) {
         facClassS = "oem.edge.ed.odc.dropbox.service.helper.DirectConnectFactory";
      } else if (useSoap) {
         facClassS = "oem.edge.ed.odc.dropbox.service.helper.JAXRPCConnectFactory";
      } else {
         facClassS = "oem.edge.ed.odc.dropbox.service.helper.HessianConnectFactory";
      }
      
      
      Class facClass = Class.forName(facClassS);
      factory = (ConnectionFactory)facClass.newInstance();
      if (topURL != null) factory.setTopURL(new java.net.URL(topURL));
     //if (useURI != null) factory.setURI(useURI);
      dropbox = factory.getProxy();
            
      isConnectedV = true;
   }
      
   public void login(String token) throws Exception {
      if (!isConnectedV) {
         System.out.println("dropboxftp:login - Not connected!");
         throw new Exception("No Connected");
      }
      if (isLoggedInV) {
         System.out.println("dropboxftp:login - Already Logged In");
         throw new Exception("Already Logged in");
      }
      
      HashMap sessionmap = dropbox.createSession(token);
      
      String lw = (String)sessionmap.get(dropbox.User);
      if (!lw.equals(who)) {
         throw new Exception("Token WHO != login name! [" + lw + 
                             "] [" + who + "]");
      }
      
      company = (String)sessionmap.get(dropbox.Company);
      
     //setSessionId();
     // Use session helper to keep session alive and time us out
      sessionHelper = new SessionHelper(dropbox, sessionmap);
      sessionHelper.setAutoCloseDelay(autoCloseDelay);
      sessionHelper.setAutoClose(true);
      sessionHelper.addSessionListener(this);
            
      sessionHelper.setSessionInformation("dropboxftp");
      
      isLoggedInV = true;
   }
   
   public Vector getProjectList() throws Exception {
      if (!isConnectedV) {
         System.out.println("dropboxftp:getProjectList - Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("dropboxftp:getProjectList - Not Logged In");
         throw new Exception("Not Logged in");
      }
      
      return dropbox.getProjectList();
   }
   
   boolean shuttingdown = false;
   public void shutdownAndExit() {
      if (!shuttingdown) { 
         shuttingdown = true;
         
         try {
            sessionHelper.cleanup();
           //dropbox.closeSession();
         } catch(Exception ee) {
         }
         System.exit(rc);
      }
   }
   
  /* -------------------------------------------------------*\
  ** Cmdline specific entrypoints
  \* -------------------------------------------------------*/
   public  Vector listInOutSandBox(int which, String name) throws Exception {
      if (!isConnectedV) {
         System.out.println("dropboxftp:listInOutSandBox - Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("dropboxftp:listInOutSandOrInBox - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      if (which < 1 || which > 3) {
         System.out.println("dropboxftp:listInOutSandOrInBox - Bad Which");
         throw new Exception("Which value is invalid: " + which);
      }
      
      Vector vec = (Vector)cacheWhich.get(new Integer(which));
      
      boolean dowild = false;
      
     // If no cache info, fix name if it was specified.
     // I think this name lookup thing will essentially turn off caching
      if (vec == null && name != null) {
      
        // We can only use up to any _id_  as duplicate names
        //  will come in here as name_id_123 and name_id_124 (etc). 
         int idx;
         
        // Check whether we should search wild or not
         idx = name.indexOf("*");
         if (idx >= 0) dowild = true;
         
        // Remove from any specific _id_ string found (duplicate) onward
         idx = name.indexOf("_id_");
         if (idx >= 0) name = name.substring(0,idx);
         
        // If it ends in _id, remove cause it could be duplicate id
         idx = name.indexOf("_id");
         if (idx >= 0 && (name.length()-idx)-3==0) name=name.substring(0,idx);
         
        // If it ends in _i, remove cause it could be duplicate id
         idx = name.indexOf("_i");
         if (idx >= 0 && (name.length()-idx)-2==0) name=name.substring(0,idx);
         
        // If it ends in _, remove cause it could be duplicate id
         idx = name.indexOf("_");
         if (idx >= 0 && (name.length()-idx)-1==0) name=name.substring(0,idx);
         
         if (name.length() == 0) {
            name = null;
         }
      }
      
      boolean cachesave  = true;
      boolean cullByName = false;
      if (vec != null) {
         vec = (Vector)vec.clone();
         cachesave  = false;
         cullByName = true;
      } else {
         
        // bool ownerOrAccessor, Vector of InfoObjs
         Vector vecin = dropbox.queryPackages(name, dowild, which > 1,
                                              which == 1 && filtercompleted,
                                              which == 1 && filtermarked,
                                              false);
                                           
         vec = new Vector();
         
         Map uniq = new Hashtable();
         
         int num = vecin.size();
         for(int i=0 ; i < num; i++) {
            PackageInfo info = (PackageInfo)vecin.elementAt(i);
            cachePackages.put(new Long(info.getPackageId()), info);
            
            byte status  = info.getPackageStatus();
            String pname = info.getPackageName();
            
           // Add the element if complete and NOT SANDBOX query
            boolean addit = false;
            if (status == DropboxGenerator.STATUS_COMPLETE) {
               if (which != 3) {
                  addit=true;
               }
           
              // ... OR, not complete and IS SANDBOX
            } else if (which == 3) {
               addit = true;
            }
            
           // If we are to add it, make sure the 'newest' pkg has real name,
           //  all others are suffixed with _id_packid. Newest means lastest
           //  commit date
            if (addit) {
               PackageInfo upinfo = (PackageInfo)uniq.get(pname);
               if (upinfo != null) {
               
                  if (upinfo.getPackageCommitted() > 
                      info.getPackageCommitted()) {
                     info.setPackageName(pname + "_id_" + 
                                         info.getPackageId());
                  } else {
                     upinfo.setPackageName(pname + "_id_" + 
                                           upinfo.getPackageId());
                                           
                    // Replace info as current in uniq hash
                     uniq.put(pname, info);
                  }
               } else {
                  uniq.put(pname, info);
               }
               
               vec.addElement(info);
            }
         }
         
         sortVector(vec, true, false);
         
         if (name != null) cachesave = false;
      }
   
      if (cullByName) {
         Iterator it = vec.iterator();
         while(it.hasNext()) {
            PackageInfo pi = (PackageInfo)it.next();
            if (!pi.getPackageName().startsWith(name)) {
               it.remove();
            }
         }
      }
      
      if (cachesave) {
         cacheWhich.put(new Integer(which), vec.clone());
      }
      
      return vec;
   }
      
   public  Vector listInOutSandBox(int which)  throws Exception {
      return listInOutSandBox(which, null);
   }
      
   
   public  Vector listPackageContents(long packid) throws Exception {
      if (!isConnectedV) {
         System.out.println("dropboxftp:listPackageContents -Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("dropboxftp:listPackageContents - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      
      Vector vec = (Vector)cacheFiles.get(new Long(packid));
      if (vec != null) {
         vec = (Vector)vec.clone();
      } else {
      
      
         vec = dropbox.queryPackageContents(packid);
            
         int num = vec.size();
         for(int i=0 ; i < num; i++) {
            FileInfo info = (FileInfo)vec.elementAt(i);
            cacheFilesByID.put(new Long(info.getFileId()), info);
         }
         sortVector(vec, true, true);
         cacheFiles.put(new Long(packid), vec.clone());
      }
         
      return vec;
   }
   
   public  FileInfo lookupFile(long fileid) throws Exception {
      if (!isConnectedV) {
         System.out.println("dropboxftp:lookupFile -Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("dropboxftp:lookupFile - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      FileInfo fi = (FileInfo)cacheFilesByID.get(new Long(fileid));
      if (fi == null) {
         
         fi = dropbox.queryFile(fileid);
         fi.setFileId(fileid);
         cacheFilesByID.put(new Long(fileid), fi);
      }
         
      return fi;
   }
   
   public  Map listGroups() throws Exception {
      if (!isConnectedV) {
         System.out.println("dropboxftp:listGroups - Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("dropboxftp:listGroups - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      Map groups = (Map)cacheWhich.get("groups");
      if (groups != null) {
         groups = new HashMap(groups);
      } else {
         groups = dropbox.queryGroups(true, true);
         cachePackages.put("groups", groups);
      }
      return groups;
   }
   
   
   public  void addNewGroup(String group) throws Exception {
      if (!isConnectedV) {
         System.out.println("dropboxftp:addNewGroup -Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("dropboxftp:addNewGroup - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      dropbox.createGroup(group);
   }
   
   public  void removeGroup(String group) throws Exception {
      if (!isConnectedV) {
         System.out.println("dropboxftp:removeGroup -Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("dropboxftp:removeGroup - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      dropbox.deleteGroup(group);
   }
   
   public  void addGroupMemberAccess(String group, 
                                     String v, 
                                     boolean memberOrAccess) throws Exception {
      if (!isConnectedV) {
         System.out.println("dropboxftp:addGroupMemberAccess -Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("dropboxftp:addGroupMemberAccess - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      dropbox.addGroupAcl(group, v, memberOrAccess);
   }
   
   public  void removeGroupMemberAccess(String group, 
                                        String v, 
                                        boolean memberOrAccess)
      throws Exception {
      
      if (!isConnectedV) {
         System.out.println("dropboxftp:remGroupMemberAccess -Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("dropboxftp:remGroupMemberAccess - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      dropbox.removeGroupAcl(group, v, memberOrAccess);
   }
   
   public  void setGroupAttrib(String group, 
                               String v, 
                               boolean visOrList) throws Exception {
      if (!isConnectedV) {
         System.out.println("dropboxftp:setGroupAttrib -Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("dropboxftp:setGroupAttrib - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      byte b = (byte)0;
      if        (v.equals(ALL)) {
         b = DropboxGenerator.GROUP_SCOPE_ALL;
      } else if (v.equals(MEMBERS)) {
         b = DropboxGenerator.GROUP_SCOPE_MEMBER;
      } else if (v.equals(OWNER)) {
         b = DropboxGenerator.GROUP_SCOPE_OWNER;
      } else {
         System.out.println("dropboxftp:setGroupAttrib - bad v val " + v);
         throw new Exception("bad v val: " + v);
      }
      
      if (visOrList) {
         dropbox.modifyGroupAttributes(group, b, dropbox.GROUP_SCOPE_NONE);
      } else {
         dropbox.modifyGroupAttributes(group, dropbox.GROUP_SCOPE_NONE, b);
      }
   }
   
   public  void markPackage(long packid, boolean mark) throws Exception {
      if (!isConnectedV) {
         System.out.println("dropboxftp:markPackage -Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("dropboxftp:markPackage - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      dropbox.markPackage(packid, mark);
   }
   
   
   public  Vector queryAcls(long packid) throws Exception {
      if (!isConnectedV) {
         System.out.println("dropboxftp:queryAcls -Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("dropboxftp:queryAcls - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      Vector vec = (Vector)cacheAcls.get(new Long(packid));
      if (vec != null) {
         vec = (Vector)vec.clone();
      } else {
         
         vec = dropbox.queryPackageAcls(packid, true);
         
         int  num   = vec.size();
         for(int i=0 ; i < num; i++) {
            AclInfo info = (AclInfo)vec.elementAt(i);
            
            if (info.getAclStatus() == DropboxGenerator.STATUS_PROJECT) {
               info.setAclName("_P_" + info.getAclName());
            } else if (info.getAclStatus() == DropboxGenerator.STATUS_GROUP) {
               info.setAclName("_G_" + info.getAclName());
            }
         }
         sortVector(vec, true, true);
         cacheAcls.put(new Long(packid), vec.clone());
      }
         
      return vec;
   }
   
   public  void deletePackage(long packid) throws Exception {
      if (!isConnectedV) {
         System.out.println("dropboxftp:deletePackage -Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("dropboxftp:deletePackage - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      dropbox.deletePackage(packid);
   }
   
   public  void commitPackage(long packid) throws Exception {
      if (!isConnectedV) {
         System.out.println("dropboxftp:commitPackage -Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("dropboxftp:commitPackage - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      Vector avec = queryAcls(packid);
      if (avec == null || avec.size() == 0) {
         throw new Exception("Cannot commit a package with no ACLs");
      }
      
      dropbox.commitPackage(packid);
   }
   
   public  void changeExpiration(long packid, long exp) throws Exception {
      if (!isConnectedV) {
         System.out.println("dropboxftp:changeExpiration -Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("dropboxftp:changeExpiration - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      dropbox.changePackageExpiration(packid, 
                                      (new Date()).getTime() + 
                                      (exp*1000*60*60*24));
   }
   
   public  void setPackageDescription(long packid, String desc) throws Exception {
      if (!isConnectedV) {
         System.out.println("dropboxftp:setPkgDesc -Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("dropboxftp:setPkgDesc - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      dropbox.setPackageDescription(packid, desc);
   }
   
   public long createPackage(String packname) throws Exception {
      if (!isConnectedV) {
         System.out.println("dropboxftp:createPackage -Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("dropboxftp:createPackage - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      return dropbox.createPackage(packname, null, storagepool, 
                                   itarpackage?PackageInfo.ITAR:0,
                                   itarpackage?PackageInfo.ITAR:0);
   }
   
   public  void removeAcl(long packid, String acl) throws Exception {
      if (!isConnectedV) {
         System.out.println("dropboxftp:removeAcl - Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("dropboxftp:removeAcl - Not LoggedIn");
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
   }
   
   public  void addAcl(long packid, String acl) throws Exception {
      if (!isConnectedV) {
         System.out.println("dropboxftp:addAcl - Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("dropboxftp:addAcl - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      byte type = DropboxGenerator.STATUS_NONE;
      if (acl.indexOf("_P_") == 0) {
         type = DropboxGenerator.STATUS_PROJECT;
         if (acl.length() > 3) {
            acl = acl.substring(3);
         } else {
            acl = "";
         }            
      } else if (acl.indexOf("_G_") == 0) {
         type = DropboxGenerator.STATUS_GROUP;
         if (acl.length() > 3) {
            acl = acl.substring(3);
         } else {
            acl = "";
         }
            
      }
      
      dropbox.addPackageAcl(packid, acl, type);
   }
   
   public void doAclCompanyCheck(long packid) throws Exception {
      Vector v = dropbox.queryPackageAclCompanies(packid);
      sortVector(v, true, false);
      Iterator it = v.iterator();
      String repc = "";
      int num = 0;
      int rnum = 0;
      while(it.hasNext()) {
         String lcomp = (String)it.next();
         
        // If this is NOT the first one add a comma. Used to do it by
        //  length, but if there is an empty string company <in test 
        //  there is> then it does not have any indication in the list
         if (rnum++ > 0)  repc += "," + lcomp;
         else             repc  = lcomp;
         if (!lcomp.equals(company)) num++;
      }
      
      if (num > 1) {
         showmsg(101, true, "Note: this package has " + num + " companies represented in the ACL list different than your own:");
         showmsg(102, true, "      Companies: (" + repc + ")");
      }
   }
   
   
   public  void deleteFileFromPackage(long packid, 
                                         long fileid) throws Exception {
      if (!isConnectedV) {
         System.out.println("dropboxftp:deleteFileFromPackage - No connect!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("dropboxftp:deleteFileFromPackage - No LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      dropbox.removeItemFromPackage(packid, fileid);
   }
   
   public  Operation uploadFile(long packid, 
                                String file, 
                                String rfname,
                                boolean restart) throws Exception {
      
      if (!isConnectedV) {
         System.out.println("dropboxftp:uploadFile - Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("dropboxftp:uploadFile - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      File f = makelocalfile(file);
      long ofs = 0;
      
      String lfmd5 = null;
      
      Vector cvec = listPackageContents(packid);
      Enumeration enumc = cvec.elements();
      boolean dorestart = false;
      while(enumc.hasMoreElements()) {
         FileInfo fi = (FileInfo)enumc.nextElement();
         if (fi.getFileName().equals(rfname)) {
            if (fi.getFileStatus() == DropboxGenerator.STATUS_COMPLETE) {
               showmsg(100, true, "Warning: Already complete on server: " + 
                       rfname + ". Removing and uploading again.");
               deleteFileFromPackage(packid, fi.getFileId());
            } else if (!restart) {
              // No restart, delete from package
               deleteFileFromPackage(packid, fi.getFileId());
            }
            break;
         }
      }
      
      long fileid = dropbox.uploadFileToPackage(packid, rfname, f.length());
      
      UploadOperation cujo = new UploadOperation(dropbox, f, packid, fileid);
      
      cujo.setNumberOfWorkers(numWorkers);
      
      boolean wasrestarted = cujo.process();
      
      if (!wasrestarted) {
         showmsg(100, false, "Restart not possible");
         showmsg(100, true, "Upload from beginning");
      }
      
      return cujo;
   }
   
   public  Operation downloadFile(File f,
                                  long packid, 
                                  FileInfo fi,
                                  boolean restart) throws Exception {
      if (!isConnectedV) {
         System.out.println("dropboxftp:downloadFile - Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("dropboxftp:downloadFile - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      Operation cujo = new DownloadOperation(dropbox, f, packid, fi);
      cujo.setNumberOfWorkers(numWorkers);
      
      boolean wasrestarted = cujo.process();
      
      if (!wasrestarted) {
         showmsg(100, false, "Restart not possible");
         showmsg(100, true,  "Download from beginning");
      }
      
      return cujo;
   }
   
   public  Operation downloadPackage(File f, long packid, 
                                     String encoding) throws Exception {
      if (!isConnectedV) {
         System.out.println("dropboxftp:downloadPackage - Not connected!");
         throw new Exception("No Connected");
      }
      if (!isLoggedInV) {
         System.out.println("dropboxftp:downloadPackage - Not LoggedIn");
         throw new Exception("Not Logged in");
      }
      
      long ofs = 0;
      
         
      if (f.exists()) {
         showmsg(100, false, "Warning: Replacing existing file: " + 
                 f.getAbsolutePath());
      }
      
      FileOutputStream ostream = new FileOutputStream(f.getAbsolutePath(), 
                                                      false);
         
      PackageDownloadOperation cdo = 
         new PackageDownloadOperation(dropbox, ostream, packid, encoding);
                                     
      
      cdo.process();
      
      return cdo;
   }
   
   
   public Map listOptions(Vector v) throws Exception {
      if (!isConnectedV) {
         System.out.println("dropboxftp:listoptions - Not connected!");
         throw new Exception("Not Connected");
      }
      if (!isLoggedInV) {
         System.out.println("dropboxftp:listoptions - Not Logged In");
         throw new Exception("Not Logged in");
      }
      
      if (v != null && v.size() == 0) v = null;
      
      Vector lv = null;
      if (v != null) {
         lv = new Vector(v);
         if (lv.contains("StoragePool")) {
            lv.remove("StoragePool");
         }
         if (lv.contains("ItarPackageCreate")) {
            lv.remove("ItarPackageCreate");
         }
      }
      
      Map opts = null;
      
      if (v == null || lv.size() > 0) {
         opts = dropbox.getOptions(lv);
      } else {
         opts = new HashMap();
      }
      
     // Add in storage pool session option
      if (v == null || v.contains("StoragePool")) {
         opts.put("StoragePool", 
                  dropbox.getStoragePoolInstance(storagepool).getPoolName());
      }
      
     // Add in storage pool session option
      if (v == null || v.contains("ItarPackageCreate")) {
         opts.put("ItarPackageCreate", ""+itarpackage);
      }
      
      return opts;
   }
   
   public Vector getStoragePools() throws Exception {
      if (!isConnectedV) {
         System.out.println("dropboxftp:getStoragePools - Not connected!");
         throw new Exception("Not Connected");
      }
      if (!isLoggedInV) {
         System.out.println("dropboxftp:getStoragePools - Not Logged In");
         throw new Exception("Not Logged in");
      }
      
      return dropbox.queryStoragePoolInformation();
   }
   
   public PoolInfo getStoragePoolInstance(String p) throws Exception {
      if (!isConnectedV) {
         System.out.println("dropboxftp:getStoragePoolInstance - Not connected!");
         throw new Exception("Not Connected");
      }
      if (!isLoggedInV) {
         System.out.println("dropboxftp:getStoragePoolInstance - Not Logged In");
         throw new Exception("Not Logged in");
      }
      
      Vector v = dropbox.queryStoragePoolInformation();
      Iterator it = v.iterator();
      while(it.hasNext()) {
         PoolInfo pi = (PoolInfo)it.next();
         String n = pi.getPoolName();
         if (n != null && n.equals(p)) {
            return pi;
         }
      }
      throw new DboxException("Storage Pool with name of " + p + " not found");
   }
   
   public PoolInfo getStoragePoolInstance(long p) throws Exception {
      if (!isConnectedV) {
         System.out.println("dropboxftp:getStoragePoolInstance - Not connected!");
         throw new Exception("Not Connected");
      }
      if (!isLoggedInV) {
         System.out.println("dropboxftp:getStoragePoolInstance - Not Logged In");
         throw new Exception("Not Logged in");
      }
      
      return dropbox.getStoragePoolInstance(p);
   }
  
   public void setOption(String k, String v) throws Exception {
      if (!isConnectedV) {
         System.out.println("dropboxftp:setoption - Not connected!");
         throw new Exception("Not Connected");
      }
      if (!isLoggedInV) {
         System.out.println("dropboxftp:setoption - Not Logged In");
         throw new Exception("Not Logged in");
      }
      
      dropbox.setOption(k, v);
   }
  
   public void setPackageOption(long pkgid, byte pkgmsk, byte pkgvals) 
                                throws Exception {
      if (!isConnectedV) {
         System.out.println("dropboxftp:setpkgoption - Not connected!");
         throw new Exception("Not Connected");
      }
      if (!isLoggedInV) {
         System.out.println("dropboxftp:setpkgoption - Not Logged In");
         throw new Exception("Not Logged in");
      }
      
      dropbox.setPackageFlags(pkgid, 0xff & (int)pkgmsk, pkgvals);
   }
  
   
  /* -------------------------------------------------------*\
  **                                                        ** 
  \* -------------------------------------------------------*/
   
   public String getDuration(long totdelt) {
      long tdays = totdelt/86400;
      totdelt   -= tdays *86400;
      long thr   = totdelt/3600;
      totdelt   -= thr * 3600;
      long tmin  = totdelt/60;
      totdelt   -= tmin * 60;
      long tsec  = totdelt;
      
      String tdaysS =               ("") + tdays;
      String thrS   = ((thr  < 10)?"0":"") + thr;
      String tminS  = ((tmin < 10)?"0":"") + tmin;
      String tsecS  = ((tsec < 10)?"0":"") + tsec;
      
      String tdelt = "";
      if (tdays > 0)                        tdelt += tdaysS + ":";
      if (thr   > 0 || tdelt.length() != 0) tdelt += thrS + ":";
      if (tmin  > 0 || tdelt.length() != 0) tdelt += tminS;
      tdelt += ":" + tsecS;
      return tdelt;
   }
   
   public void printstats(Operation op, boolean header) {
      if (dostats) {
         if (header && !parseable) {
            showmsg(100, false, 
              "Percent Complete Xfr Rate  Duration    Finish   Xfered Remain");
            showmsg(100, true,
              "---------------- -------- ---------- ---------- ------ ------");
         }
         
        //   "[##########]100% 99999KBs 1:23:59:59 9:23:59:59 999999 99999M
        
         long tottoxfr     = op.getToXfer();
         long totconfirmed = op.getTotalConfirmed();
         
         int percentdone = op.percentDone();
         
        // HACK If tottoxfr is MAX LONG, then we are doing pack download
         if (tottoxfr == 0x7FFFFFFFFFFFFFFFL) {
            tottoxfr = statOverrideBytes;
            if (tottoxfr == 0) {
               percentdone = 100;
            } else {
               percentdone = (int) ((totconfirmed * 100) / tottoxfr);
            }
         }
         
         long tottogo      = tottoxfr - totconfirmed;
        
         String percentpad = "";
         if      (percentdone < 10)  percentpad="  ";
         else if (percentdone < 100) percentpad=" ";
         
         String parseableS = ""+percentdone;
         String s = 
            pad("[#################".substring(0,(percentdone/10)+1), 11, true)
            + "]" + percentpad + percentdone + "% ";
            
         long xfrrate = cumulativeXferStats? op.getXferRate()
                                           : op.getInstantaneousXferRate();
         xfrrate /= 1024;
         s += pad((""+xfrrate) + "KBs", 8, false) + " ";
         parseableS += ":" + xfrrate;
         
         long curTime = System.currentTimeMillis();
         long totdelt = (int)((curTime - op.getStartTime())/1000);
         
         s += pad(getDuration(totdelt), 10, false) + " ";
         parseableS += ":" + totdelt;
         
         
         totdelt           = tottogo / ((xfrrate<=0)?1:(xfrrate*1024));
         s += pad(getDuration(totdelt), 10, false) + " ";
         parseableS += ":" + totdelt;
         
         String numS;
         if        (totconfirmed > 97*1024*1024) {
            numS = "" + (totconfirmed/(1024*1024)) + "M";
         } else if (totconfirmed > 99999) {
            numS = "" + (totconfirmed/(1024)) + "K";
         } else {
            numS = "" + totconfirmed;
         }
         s += pad(numS, 6, false) + " ";
         parseableS += ":" + totconfirmed;
         
         if        (tottogo > 97*1024*1024) {
            numS = "" + (tottogo/(1024*1024)) + "M";
         } else if (tottogo > 99999) {
            numS = "" + (tottogo/(1024)) + "K";
         } else {
            numS = "" + tottogo;
         }
         s += pad(numS, 6, false);
         parseableS += ":" + tottogo;
         
         if (parseable) {
            showmsg(199, false, parseableS);
         } else {
            if (emacs) {
               showmsg(199, false, s);
            } else {
               showmsgnoNL(199, false, "\r" + s);
            }
         }
      }
   }
   
                            
 
/**
 * MaskingThread from following URL. Full permissions given
 *
 * http://java.sun.com/features/2002/09/pword_mask.html  
 *
 * This class attempts to erase characters echoed to the console.
 */
   class MaskingThread extends Thread {
      private boolean stop = false;
      private int index;
      private String prompt;
      
      
     /**
      *@param prompt The prompt displayed to the user
      */
      public MaskingThread(String prompt) {
         this.prompt = prompt;
      }
      
      
     /**
      * Begin masking until asked to stop.
      */
      public void run() {
         Random rand = new Random();
         
         StringBuffer randStr = new StringBuffer();
         while(!stop) {
            try {
              // attempt masking at this rate
               this.sleep(50);
            }catch (InterruptedException iex) {
               SearchEtc.printStackTrace(iex);
            }
            String seedStr = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()?><,./][{}=+-_";
            if (!stop) {
               try {
                  randStr.setLength(0);
                  long l = rand.nextLong();
                  if (l < 0) l = -l;
                  int i = (int)(l % 10) + 8;
                  for(int j=0; j < i; j++) {
                     l = rand.nextLong();
                     if (l < 0) l = -l;
                     int idx =(int) (l % seedStr.length());
                     randStr.append(seedStr.charAt(idx));
                  }
               } catch(Exception ee) {}
               System.out.print("\r                                                    \r" + prompt + randStr.toString());
            }
            System.out.flush();
         }
      }
      
      
     /**
      * Instruct the thread to stop masking.
      */
      public void stopMasking() {
         this.stop = true;
      }
      
   }
   
   String getPassword(String lprompt) throws IOException {
      return getPassword(lprompt, 331);
   }
   String getPassword(String lprompt, int parsecode) throws IOException {
   
      if (parseable) {
         showmsg(parsecode, true, lprompt);
         String pw=input.readLine();
         return pw;
      }
      
      if (emacs) {
         
        /* Actually, looks like Emacs handles the password hiding Nice. */
         System.out.println("\n" + 
         "** POTENTIALLY NO PASSWORD HIDING!! *************************\n" +
         "*                                                           *\n" +
         "* The dropboxftp cmdline tool is written in JAVA, and so    *\n" +
         "* cannot directly control the local terminal to hide the    *\n" +
         "* password.                                                 *\n" +
         "*                                                           *\n" +
         "* It appears that you are using an emacs type TERM, so the  *\n" +
         "* standard JAVA password workaround to hide the password is *\n" +
         "* not available. EMACS typically will recognize that a      *\n" +
         "* password is being entered, and do the password hiding     *\n" +
         "* itself, so it MAY be hidden.  Take proper precautions     *\n" +
         "*                                                           *\n" +
         "*************************************************************\n");
         System.out.print(lprompt);
         String pw=input.readLine();
         return pw;
      } else {
         System.out.println("\n" + 
         "** LIMITED PASSWORD HIDING!! ********************************\n" +
         "*                                                           *\n" +
         "* The dropboxftp cmdline tool is written in JAVA, and so    *\n" +
         "* cannot directly control the local terminal to hide the    *\n" +
         "* password. A common scheme to attempt to hide the password *\n" +
         "* is being employed, however, it is possible that the       *\n" +
         "* password COULD be visible.    Take proper precautions     *\n" +
         "*                                                           *\n" +
         "*************************************************************\n");
      }
     
      String password = "";
      MaskingThread maskingthread = new MaskingThread(lprompt);
      Thread thread = new Thread(maskingthread, "MaskingThread");
      thread.start();
     // block until enter is pressed
      while (true) {
         char c = (char)System.in.read();
        // assume enter pressed, stop masking
         maskingthread.stopMasking();
         
         if (c == '\r') {
            c = (char)System.in.read();
            if (c == '\n') {
               break;
            } else {
               continue;
            }
         } else if (c == '\n') {
            break;
         } else {
           // store the password
            password += c;
         }
      }
      return password;
   }
   
   
   Map options        = new Hashtable();
   
   String  who        = null;
   String  company    = null;
   String  mach       = null;
   int     lev        = 6;
   String  ctx        = "technologyconnect/odc";
   String  topURL     = null;
   boolean dopipe     = false;
   
   boolean noteslaunch = false;
   boolean allowabort = true;
   String  cmdfile    = null;
   int     numWorkers = 3;
   int     autoCloseDelay = 60*60*2;  // 2 hour inactivity and out
   boolean stoponerr  = false;
   boolean dostats    = true;
   boolean oldext     = false;
   boolean doupdate   = true;
   boolean dodirect   = false;   // DEBUG!!
   boolean useSoap    = false;   // Hessian is default right now
   boolean emacs      = false;
   String  dscinstall = new File(".").getAbsolutePath();
   
   boolean restartXfer= true;
   
   int     rc         = 0;
   
   String  pwd        = File.separator;
   String  lpwd       = new File(".").getAbsolutePath();

   boolean cumulativeXferStats = true;
   
   BufferedReader   input    = null;
   Map              commands = new Hashtable();
   
   boolean          prompt          = true;
   boolean          filtermarked    = false;
   boolean          filtercompleted = false;
   
   class Command {
      String cmd, usage;
      int val;
      public Command(String cmd, int val, String usage) {
         this.cmd   = cmd;
         this.val   = val;
         this.usage = usage;
      }
      
      public String getUsage()   { return usage; }
      public int    getValue()   { return val;   }
      public String getCommand() { return cmd;   }
   }
   
   
   final static int CMD_ADDACL      = 0;
   final static int CMD_ADDGRP      = 1;
   final static int CMD_BYE         = 2;
   final static int CMD_CD          = 3;
   final static int CMD_CHECKMD5    = 4;
   final static int CMD_COMMIT      = 5;
   final static int CMD_COMPRESS    = 6;
   final static int CMD_DEBUG       = 7;
   final static int CMD_DIR         = 8;
   final static int CMD_EXIT        = 9;
   final static int CMD_EXPIRE      = 10;
   final static int CMD_FILTER      = 11;
   final static int CMD_GET         = 12;
   final static int CMD_GETPACK     = 13;
   final static int CMD_HELP        = 14;
   final static int CMD_LCD         = 15;
   final static int CMD_LISTACLS    = 16;
   final static int CMD_LISTOPTS    = 17;
   final static int CMD_LISTPKGOPTS = 18;
   final static int CMD_LISTPOOLS   = 19;
   final static int CMD_LLS         = 20;
   final static int CMD_LMKDIR      = 21;
   final static int CMD_LOOKUP      = 22;
   final static int CMD_LPWD        = 23;
   final static int CMD_LRMDIR      = 24;
   final static int CMD_LS          = 25;
   final static int CMD_LSP         = 26;
   final static int CMD_LSPKGDESC   = 27;
   final static int CMD_MARK        = 28;
   final static int CMD_MGET        = 29;
   final static int CMD_MGETPACK    = 30;
   final static int CMD_MKDIR       = 31;
   final static int CMD_MPUT        = 32;
   final static int CMD_PROMPT      = 33;
   final static int CMD_PUT         = 34;
   final static int CMD_PWD         = 35;
   final static int CMD_QUIT        = 36;
   final static int CMD_RESTART     = 37;
   final static int CMD_RM          = 38;
   final static int CMD_RMACL       = 39;
   final static int CMD_RMDIR       = 40;
   final static int CMD_RMGRP       = 41;
   final static int CMD_SETOPT      = 42;
   final static int CMD_SETPKGDESC  = 43;
   final static int CMD_SETPKGDESCF = 44;
   final static int CMD_SETPKGOPT   = 45;
   final static int CMD_TUNNEL      = 46;
   final static int CMD_UNMARK      = 47;
   final static int CMD_VERBOSE     = 48;
   final static int CMD_PARSEABLE   = 49;
   
   
   
   String commands_str[] = {
     // commands from sftp 
      "addacl",   "aclname [aclname]        - Add acls to pwd package",
      "addgroup", "grpname [grpname]        - Add group acls to pwd package",
      "bye",      "                         - quit this session",
      "cd",       "[remotepath]             - change to remote location",
      "checkmd5", "localpath [localpath]    - calculate local MD5's cmp to server",
      "commit",   "[remote package ...]     - commit specified packages",
      "compression","[on/off/stats]           - Set/toggle/query transfer compression",
      "debug",    null,
      "dir",      "[-lLdme] [remotepath]    - list remote files",
      "exit",     "                         - quit this session",
      "expires",  "[package ...] daysahead  - set package expiration ahead",
      "filter",   "completed|marked [true|false] - Toggle/set filtering value",
      "get",      "remotepath [localpath]   - download file to local location.",
      "getpack",  "encoding [rpath] [lpath] - download pack using encoding",
      "help",     "                         - present this help message",
      "lcd",      "[localpath]              - change to local location",
      "listacls", "[-c] [remote package ...]  - list package acls -c=show all companies",
      "listopts", "[optname ...]            - list all or specific option values",
      "listpkgopts","[package ...]            - list all package based options",
      "listpools","                         - list available storage pools",
      "lls",      "[-lLdm] [localpath]      - list local  files",
      "lmkdir",   "localpath                - create local directory",
      "lookup",   "username                 - Lookup userid",
      "lpwd",     "                         - show local working directory",
      "lrmdir",   "[-r] localpath           - remove local directory (-r=recursive)",
      "ls",       "[-lLdme] [remotepath]    - list remote files",
      "lsp",      "                         - list user's projects",
      "lspkgdesc","[remote package] [localpath] - Show package description",
      "mark",     "[remote package]         - Set package as marked read",
      "mget",     "remotepath [remotepath]  - download file to local location.",
      "mgetpack", "encoding [remotepath]*   - download package using encoding.",
      "mkdir",    "remotepath               - create remote directory/entity",
      "mput",     "localpath [localpath]    - upload file to remote location.",
      "prompt",   "                         - Toggle prompting for mput/mget",
      "put",      "localpath [remotepath]   - upload file to remote location.",
      "pwd",      "                         - show remote working directory",
      "quit",     "                         - quit this session",
      "restart",  "[true|false]             - Toggle/set restart on transfers",
      "rm",       "remotepath               - remove the remote file",
      "rmacl",    "aclname [aclname]        - Remove acls from pwd package",
      "rmdir",    "remotepath               - remove remote directory/entity",
      "rmgroup",  "aclname [aclname]        - Remove groups from pwd package",
      "setopt",   "optname optval           - Set an option's value",
      "setpkgdesc","[remote package] string - Set the package description from string",
      "setpkgdescfile","[remote package] localpath - Set package desc from file",
      "setpkgopt","[package] optname optval - Set a package option value",
      "tunnel",    null,
      "unmark",   "[remote package]         - Set package as marked unread",
      "verbose",   null,
      "parseable", null
   };
   
   
   public DropboxCmdline(String args[]) {
   
      dateformat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss z",
                                        java.util.Locale.US);
                                        
      if (!parse(args)) {
         System.exit(1);
      }
      
      for(int i=0; i < commands_str.length; i+=2) {
         commands.put(commands_str[i], new Command(commands_str[i], i/2, 
                                                   commands_str[i+1]));
      }
      
      new Thread(this, "DropboxCmdline").start();
   }
      
      
   private Map cacheFilesByID = new Hashtable();
   private Map cacheFiles     = new Hashtable();
   private Map cachePackages  = new Hashtable();
   private Map cacheAcls      = new Hashtable();
   private Map cacheWhich     = new Hashtable();
   
   public void clearCache() {
      cacheFiles.clear();
      cachePackages.clear();
      cacheAcls.clear();
      cacheWhich.clear();
      cacheFilesByID.clear();
   }
   
   public void run() {
         
      String pw = null;
      
      currentCmd = "STARTUP";
      
      input = new BufferedReader(new InputStreamReader(System.in));
      
     // Get the password and Authenticate
     
      String token = null;
      int attempts = 0;
      
      if (lpwd.length() > 1) {
         if (lpwd.endsWith(File.separator + ".")) {
            lpwd = lpwd.substring(0, lpwd.length()-2);
            if (lpwd.length() == 0) {
               lpwd = File.separator;
            } else if (lpwd.charAt(lpwd.length()-1) == ':') {
               lpwd += File.separator;
            }
         }
      }
      
      if (dscinstall.length() > 1) {
         if (dscinstall.endsWith(File.separator + ".")) {
            dscinstall = dscinstall.substring(0, dscinstall.length()-2);
            if (dscinstall.length() == 0) {
               dscinstall = File.separator;
            } else if (dscinstall.charAt(dscinstall.length()-1) == ':') {
               dscinstall += File.separator;
            }
         }
      }
      
      String term = System.getProperty("TERM");
      if (term != null &&
          (term.equalsIgnoreCase("emacs") || 
           term.equalsIgnoreCase("xemacs"))) {
           
         emacs = true;
      }
      
      String eini = dscinstall + File.separator + "edesign.ini";
      
      ConfigFile cfg = new ConfigFile();
      
      boolean versioncheckingOK = false;
      
      try {
      
         cfg.load(eini);
                  
         int connType           = cfg.getIntProperty("ODCCONNTYPE", -1);
         String socksServerHost = cfg.getProperty("ODCSOCKSSERVER",null);
         String socksServerPort = cfg.getProperty("ODCSOCKSPORT",null);
         String proxyServerHost = cfg.getProperty("ODCPROXYSERVER",null);
         String proxyServerPort = cfg.getProperty("ODCPROXYPORT",null);
         boolean proxyAuth      = cfg.getBoolProperty("ODCPROXYAUTH",false);
         String sproxyAuth      = cfg.getProperty("PROXYAUTH",null);
         String proxyId         = cfg.getProperty("ODCPROXYID",null);
         
         if (connType == -1) {
            connType = 0;
            if      (proxyServerHost != null) connType = 1;
            else if (socksServerHost != null) connType = 2;
         }
         
         Properties p = System.getProperties();
         switch(connType) {
            case 0:  // Direct
               p.remove("proxySet");
               p.remove("proxyPort");
               p.remove("proxyHost");
               p.remove("http.proxySet");
               p.remove("http.proxyPort");
               p.remove("http.proxyHost");
               p.remove("https.proxySet");
               p.remove("https.proxyPort");
               p.remove("https.proxyHost");
               
               p.remove("socks.proxySet");
               p.remove("socks.proxyHost");
               p.remove("socks.proxyPort");
               p.remove("socksProxySet");
               p.remove("socksProxyHost");
               p.remove("socksProxyPort");
               break;
            case 1:  // Proxy
               p.remove("socks.proxySet");
               p.remove("socks.proxyHost");
               p.remove("socks.proxyPort");
               p.remove("socksProxySet");
               p.remove("socksProxyHost");
               p.remove("socksProxyPort");
               p.put("proxySet","true");
               p.put("proxyHost", proxyServerHost);
               p.put("proxyPort", proxyServerPort);
               p.put("http.proxySet","true");
               p.put("http.proxyHost", proxyServerHost);
               p.put("http.proxyPort", proxyServerPort);
               p.put("https.proxySet","true");
               p.put("https.proxyHost", proxyServerHost);
               p.put("https.proxyPort", proxyServerPort);
               
               if (proxyAuth) {
                  if (sproxyAuth == null) {
                  
                     if (dopipe) {
                        showmsg(421, false, 
                           "Using Authenticated proxy and -pipe specified");
                        showmsg(421, true, "Exitting");
                        System.exit(2);
                     }
                     
                     if (proxyId == null || proxyId.trim().equals("")) {
                        showmsg(333, false, "Enter userid for HTTPProxy: ");
                        proxyId=input.readLine();
                     }
                     String proxyPW = 
                        getPassword("Enter HTTPProxy password for " + 
                                    proxyId + ": ",  334);
                                    
                     if (proxyPW == null) {
                        showmsg(421, false, "Error reading proxy password");
                        System.exit(2);
                     }
                                    
                     sproxyAuth = proxyId + ":" + proxyPW;
                     sproxyAuth = 
                        oem.edge.ed.util.Base64.encode(sproxyAuth.getBytes());
                  }
                  p.put("proxyAuth", sproxyAuth);
                  p.put("http.proxyAuth", sproxyAuth);
               }
               System.setProperties(p);
                        
               break;
            case 2:  // Socks
               p.remove("proxySet");
               p.remove("proxyHost");
               p.remove("proxyPort");
               p.remove("http.proxySet");
               p.remove("http.proxyHost");
               p.remove("http.proxyPort");
               p.remove("https.proxySet");
               p.remove("https.proxyHost");
               p.remove("https.proxyPort");
               p.put("socksProxySet","true");
               p.put("socksProxyHost", socksServerHost);
               p.put("socksProxyPort", socksServerPort);
               p.put("socks.proxySet","true");
               p.put("socks.proxyHost", socksServerHost);
               p.put("socks.proxyPort", socksServerPort);
               System.setProperties(p);
               break;
            default: // Unknown
               showmsg(100, false, "Unknown connection type in edesign.ini");
               break;
         }
      } catch (Exception e) {
         showmsg(100, false, "\n" + 
         "** NOTE - Proxy/Socks Users *********************************\n" +
         "*                                                           *\n" +
         "* The edesign.ini file was not found in the install point.  *\n" +
         "* If you have special connectivity requirements, such as    *\n" +
         "* using an HTTP Proxy or Socks Server, then you should      *\n" +
         "* ensure that the java -D options detailing the appropriate *\n" +
         "* server information is specified. In addition, if you are  *\n" +
         "* using an authenticated proxy (Basic only), then also set  *\n" +
         "* the Base64 encoded user:pw in the 'proxyAuth' system      *\n" +
         "* property.                                                 *\n" +
         "*                                                           *\n" +
         "*************************************************************\n");
      }
      
// MPZ replace version checking...

     //
     // Check for code updates, if update() returns true, updates were installed
     // and we need to exit with a non-zero to have the updates applied.
     //

     if(doupdate) {
        CodeUpdater codeUp = new CodeUpdater(topURL);
        int result = codeUp.update();
        if (result != CodeUpdater.GOOD) {
          System.exit(result == CodeUpdater.UPDATE ? 200 : 1);
        }
     }

     versioncheckingOK = true;

     //
     // Check version stamp
     //
      
      URLConnection2 conn = null;

     //
     // Get a BANNER if available
     //
      conn = null;
      try {
         URL url = new URL(topURL + "/servlet/oem/edge/ed/odc/HelperInstall/getFileAnyway/dropbox.BANNER");
            
         conn = new URLConnection2(url);
            
         conn.setDoInput(true);
         conn.setDoOutput(false);
         conn.setUseCaches(false);
         conn.setDefaultUseCaches(false);
         
        // Check connection status.
         InputStream in = conn.getInputStream();
         byte[] header = new byte[2];
         
        // If server didn't respond or sent "NO" as its first 2 bytes of content...
         if (in.read(header) != 2 || header[0] != 'O' || header[1] != 'K') {
            BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
            String error = rdr.readLine();
            
            throw new Exception("Servlet refused to download file: " + error);
         }
         
         StringBuffer banner = new StringBuffer();
         BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
         String str;
         while((str = rdr.readLine()) != null) {
            banner.append(str).append("\n");
         }
         
         showmsg(100, false, banner.toString());
         
      } catch(Exception dontcaree) {
        //showmsg(100, true, "Error receiving banner: "
        // + dontcaree.getMessage());
         
      } finally {
         try { 
            conn.disconnect();
         } catch(Exception gg) {
         }
      }
      
      currentCmd = "AUTHENTICATE";
      
     // Authenticate
      do try {
         if (dopipe) {
            pw=input.readLine();
         } else {
            pw = getPassword("Enter password for " + who + ": ");
         }
      
         if (pw == null) {
            showmsg(421, false, "Error reading password");
            System.exit(2);
         }
      
        // Authenticate the user
         if (emacs) showmsg(100, true, "");
         showmsgnoNL(100, true, "authenticating ...");
         System.out.flush();
         token = Misc.getConnectInfoGeneric(who, pw, "XFR", null, topURL);
         if (token == null) {
            showmsg(431, false, " failed");
         } else {
            showmsg(100, true, " done");
         }
      } catch(IOException ioe) {
         showmsg(431, false, " failed");
      } while(token == null && !dopipe && ++attempts < 3);
      
      if (token == null) {
         showmsg(421, true, "Error authenticating user '" + who + "'");
         System.exit(3);
      }
      
     // If we are reading from a file, hook that to input
      if (cmdfile != null) {
         try {
            input = new BufferedReader(new FileReader(makelocalfile(cmdfile)));
         } catch(FileNotFoundException e) {
            showmsg(421, true, 
                    "Specified command file '" + cmdfile + "' not found");
            System.exit(4);
         }
      }
      
      currentCmd = "TUNNELSTART";
      
      showmsgnoNL(100, true, "creating service proxy ...");
      System.out.flush();
      try {
         connect();
      } catch(Exception ee) {
         showmsg(421, true, "Error connecting to service: " + ee.getMessage());
         SearchEtc.printStackTrace(ee, System.out);
         System.exit(5);
      }
      
      showmsg(100, true, "(" + factory.getName() + ") done");
      
      currentCmd = "LOGGINGIN";
      
      showmsgnoNL(100, true, "Logging in ...");
      System.out.flush();
      
      try {
         login(token);
      } catch(Exception e) {
         SearchEtc.printStackTrace(e, System.out);
         showmsg(421, true, "Error logging in to Dropbox server: " + 
                 e.getMessage());
         shutdownAndExit();
      }
      showmsg(100, true, " done");
      
      showmsg(230, true, "Login complete");
      
      if (!versioncheckingOK) {
         showmsg(107, true, "Software MAY be out of date ... see previous banner");
      }
            
     // If we have options
      try {
         options = listOptions(null);
         String s = (String)options.get(DropboxGenerator.FilterComplete);
         if (s != null && s.equalsIgnoreCase("true")) {
            filtercompleted = true;
         }
         s = (String)options.get(DropboxGenerator.FilterMarked);
         if (s != null && s.equalsIgnoreCase("true")) {
            filtermarked = true;
         }
      } catch(Exception ee) {
         if (printexception) SearchEtc.printStackTrace(ee, System.out);
         showerror(503, true, "Error getting option values: " + 
                   ee.getMessage());
      }
            
     // Main loop
      boolean done = false;
      String arr[] = new String[128];
      while(!done) {
      
         clearParseable();
         
         if (cmdfile == null) {
            showmsgnoNL(105, true, "dropboxftp> ");
            System.out.flush();
         }
         
         clearParseable();
         
         String line = null;
         try {
            line = input.readLine();
            if (line == null) {
               showmsg(421, true, "EOF ... shutting down");
               shutdownAndExit();
            }
         } catch(IOException ioe) {
            showmsg(421, true, "Exception reading command stream");
            shutdownAndExit();
         }
         
        // Parse up the line into command components (cmd p1 p2 p3) in arr
         line = line.trim();
         
         int numused  = 0;
         try {
            int startidx = -1;
            char endchar = ' ';
            for(int i=0; i < line.length(); i++) {
               char ch = line.charAt(i);
               if (startidx != -1) {
                  if (ch == endchar) {
                     arr[numused++] = line.substring(startidx, i);
                     startidx = -1;
                  }
               } else if (ch == '\'' || ch == '"') {
                  endchar=ch;
                  startidx=i+1;
               } else if (ch != ' ') {
                  endchar=' ';
                  startidx=i;
               }
            }
            
            if (startidx != -1) {
               arr[numused++] = line.substring(startidx);
            }
         } catch(Exception ee) {
            showerror(500, true, "Error parsing line: '" + line + "'");
         }
         
         if (numused > 0) {
            
            int cmdi = -1;
            Command c = (Command)commands.get(arr[0]);
            if (c != null) {
               cmdi = c.getValue();
            }
            
            clearCache();
      
            currentCmd = arr[0];
            
            switch(cmdi) {
               case CMD_ADDACL:   cmd_addRmAcl(c, arr, numused, true);  break;
               case CMD_ADDGRP:   cmd_addRmGrp(c, arr, numused, true);  break;
               case CMD_BYE:      cmd_exit(c, arr, numused);        break;
               case CMD_CD:       cmd_cd(c, arr, numused);          break;
               case CMD_CHECKMD5: cmd_checkmd5(c, arr, numused);    break;
               case CMD_COMMIT:   cmd_commit(c, arr, numused);      break;
               case CMD_COMPRESS: cmd_compress(c, arr, numused);    break;
               case CMD_DEBUG:    cmd_debug(c, arr, numused);       break;
               case CMD_DIR:      cmd_ls(c, arr, numused);          break;
               case CMD_EXIT:     cmd_exit(c, arr, numused);        break;
               case CMD_EXPIRE:   cmd_expire(c, arr, numused);      break;
               case CMD_FILTER:   cmd_filter(c, arr, numused);      break;
               case CMD_GET:      cmd_get(c, arr, numused, false);  break;
               case CMD_GETPACK:  cmd_getpack(c, arr, numused, false);  break;
               case CMD_HELP:     cmd_help(c, arr, numused);        break;
               case CMD_LCD:      cmd_lcd(c, arr, numused);         break;
               case CMD_LISTACLS: cmd_listacls(c, arr, numused);    break;
               case CMD_LISTOPTS: cmd_listopts(c, arr, numused);    break;
               case CMD_LISTPKGOPTS: cmd_listpkgopts(c, arr, numused);  break;
               case CMD_LISTPOOLS:cmd_listpools(c, arr, numused);  break;
               case CMD_LLS:      cmd_lls(c, arr, numused);         break;
               case CMD_LMKDIR:   cmd_lmkdir(c, arr, numused);      break;
               case CMD_LOOKUP:   cmd_lookup(c, arr, numused);      break;
               case CMD_LPWD:     cmd_lpwd(c, arr, numused);        break;
               case CMD_LRMDIR:   cmd_lrmdir(c, arr, numused);      break;
               case CMD_LS:       cmd_ls(c, arr, numused);          break;
               case CMD_LSP:      cmd_lsp(c, arr, numused);         break;
               case CMD_LSPKGDESC: cmd_lspkgdesc(c, arr, numused);  break;
               case CMD_MARK:     cmd_mark(c, arr, numused, true);  break;
               case CMD_MGET:     cmd_get(c, arr, numused, true);   break;
               case CMD_MGETPACK: cmd_getpack(c, arr, numused, true);   break;
               case CMD_MKDIR:    cmd_mkdir(c, arr, numused);       break;
               case CMD_MPUT:     cmd_put(c, arr, numused, true);   break;
               case CMD_PROMPT:   cmd_prompt(c, arr, numused);      break;
               case CMD_PUT:      cmd_put(c, arr, numused, false);  break;
               case CMD_PWD:      cmd_pwd(c, arr, numused);         break;
               case CMD_QUIT:     cmd_exit(c, arr, numused);        break;
               case CMD_RESTART:  cmd_restart(c, arr, numused);     break;
               case CMD_RM:       cmd_rm(c, arr, numused);          break;
               case CMD_RMACL:    cmd_addRmAcl(c, arr, numused, false); break;
               case CMD_RMDIR:    cmd_rmdir(c, arr, numused);       break;
               case CMD_RMGRP:    cmd_addRmGrp(c, arr, numused, false); break;
               case CMD_SETOPT:   cmd_setopt(c, arr, numused);      break;
               case CMD_SETPKGDESC: cmd_setpkgdesc(c, arr, numused, false);   break;
               case CMD_SETPKGDESCF: cmd_setpkgdesc(c, arr, numused, true);   break;
               case CMD_SETPKGOPT:  cmd_setpkgopt(c, arr, numused);   break;
               case CMD_UNMARK:   cmd_mark(c, arr, numused, false); break;
               case CMD_VERBOSE:  cmd_verbose(c, arr, numused);     break;
               case CMD_PARSEABLE:cmd_parseable(c, arr, numused);   break;
               default: {
                  showerror(500, true, "Unknown Command: '" + arr[0] + 
                            "' Type help for command list");
                  break;
               }
            }
         }
         finishParseable();
      }
   }
   
   public void usage(String s) {

      if (s != null) {
         System.out.println("\ndropboxftp: Illegal option: " + s + "\n");
         System.out.println("Usage\n");
      } else {
         System.out.println("\ndropboxftp Usage\n");
      }
      
      System.out.println("\t-noupdate      - Skip the update phase");
      System.out.println("\t-numworkers    - number of channels to use for upload/download");
      System.out.println("\t-hessian       - Use the Hessian remoting method (default)");
      System.out.println("\t-soap          - Use the JAXRPC remoting method");
      System.out.println("\t-autotimeout # - Number of seconds of inactivity before shutdown");
      System.out.println("\t                 (default 120 minutes (7200 seconds)");
      System.out.println("\t-md5 f <len>   - Utility to calculate MD5 of specified file. If len");
      System.out.println("\t                 is provided, limit to len, otherwise entire file");
      System.out.println("\t-context ctx   - Context for Tunnel (default=technologyconnect/odc)");
      System.out.println("\t-verbose val   - turn up the verbosity (default=11)");
      System.out.println("\t-help or -?    - This usage message");
      System.out.println("\t-cmdfile file  - Read commands from file");
      System.out.println("\t-parseable     - All output is formated with parsing in mind");
      System.out.println("\t-pipe          - Read password from stdin (use with -cmdfile)");
      System.out.println("\t-stoponerror   - Stops on any error");
      System.out.println("\t-nostats       - don't show filexfer statistics");
      System.out.println("\t-fixfreeze     - Some JREs on Windows freeze when switching");
      System.out.println("\t                 to another window. Either switch to 1.3.1 or higher");
      System.out.println("\t                 or try this option");
      System.out.println("\tuser@machine   - dropbox ID and host machine");
      System.out.println("\n\nReturn codes:\n");
      System.out.println("\t0 = Successful");
      System.out.println("\t1 = parameter problems");
      System.out.println("\t2 = Error reading password");
      System.out.println("\t3 = Error Authenticating user");
      System.out.println("\t4 = Error opening Commandfile");
      System.out.println("\t5 = Error with connectivity (tunnel)");
      System.out.println("\t6 = Error with at least 1 command issued\n");
      System.out.println("\n\nWhen using -parseable option, message codes are as follows:\n\n");
      System.out.println(parseableHelp);
   }
   
   
   static String strpad = 
   "                                                                                                                                                                                                                                         ";
   
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
   
   DateFormat dateformat = null;
//   DateFormat.getDateTimeInstance(DateFormat.SHORT,
//                                  DateFormat.SHORT);
   
   public void lsl(String mode, String name, String own, 
                   String comp, long size, Date date, 
                   boolean extlay) {
      lsl(mode, name, own, comp, size, date, extlay, false, false, false);
   }
   
   public void lsl(String mode, String name, String own, 
                   String comp, long size, Date date, 
                   boolean extlay, boolean completed, 
                   boolean marked, boolean hidden) {
      
      String dateS;
      String szS = "" + size;
      if (size > 999999999) szS = "" + (size/(1024*1024)) + "M";
      
      mode  = pad(mode,             1, false);
      own   = pad(own,             13, false);
      comp  = pad(comp,            10, true);
      szS   = pad(szS,              9, false);
      dateS = pad(dateformat.format(date), 23, false);
      
      String mc = "";
      if (extlay) {
      
        // Change the extended options
         if (marked)    mc += "m";
         else           mc += "-";
         if (completed) mc += "c";
         else           mc += "-";
         if (hidden)    mc += "h";
         else           mc += "-";
         
         mc += " ";
         
        // If someone ABSOLUTELY needs it
         if (oldext) {
            mc = "";
            if      (marked && completed) mc = "@ ";
            else if (marked)              mc = "* ";
            else if (completed)           mc = "! ";
            else                          mc = "- ";
         }
      }
      
      showmsg(101, true, mode   + " " + 
                         own    + " " + 
                         comp   + " " + 
                         szS    + " " +
                         dateS  + " " +
                         mc     +  
                         name);
   }

   public void lsl(String mode, String name, String md5,
                   long size, Date date, boolean extlay) {
      
      String dateS;
      String szS = "" + size;
      if (size > 999999999) szS = "" + (size/(1024*1024)) + "M";
      
      if (md5.trim().length() == 0) {
         md5 = "-";
      }
      
      mode  = pad(mode,             1, false);
      md5   = pad(md5,             32, false);
      szS   = pad("" + size,        9, false);
      showmsg(101, true, mode    + " " + 
                          szS    + " " +
                          md5    + " " + 
                          (extlay?"- ":"") +  name);
   }
   
   
   static final String ACCESS  = "#ACCESS#";
   static final String INBOX   = "inbox";
   static final String OUTBOX  = "sent";
   static final String SANDBOX = "drafts";
   static final String GROUPS  = "groups";
   static final String DONE    = "#DONE#";
   
   static final String ALL         = "all";
   static final String MODIFIABLE  = "modifiable";
   static final String OWNED       = "owned";
   static final String MEMBERS     = "members";
   static final String PROPERTIES  = "properties";
   static final String GACCESS     = "access";
   static final String OWNER       = "owner";
   static final String USERID      = "userid";
   static final String COMPANY     = "company";
   static final String LISTABILITY = "listability";
   static final String VISIBILITY  = "visibility";
   
   static final int    INBOX_N     = 1;
   static final int    OUTBOX_N    = 2;
   static final int    SANDBOX_N   = 3;
   static final int    GROUPS_N    = 4;
   
  // Should be static in PathInfo
   public static String fs_dot       = File.separator + ".";
   public static String fs_dot_fs    = File.separator + "." + File.separator;
   public static String fs_dotdot    = File.separator + "..";
   public static String fs_dotdot_fs = File.separator + ".." + File.separator;
   
   class PathInfo {
      public boolean isValid  = false;
      public boolean isAccess = false;
      public String  topdir   = null;
      public String  pack     = null;
      public String  file     = null;
      public String  origpath = null;
      public String  path     = null;
      public String  pathnodrive = null;      
      public String  drive    = null;
      public long    miscLong = 0;
      public String  hier[]   = new String[10];
      public int     hidx     = 0;
      public int     hlimit   = 10;
      
      public int topdirnum    = -1;
      
      
      public PathInfo(PathInfo p) {
         this.isValid   = p.isValid;
         this.isAccess  = p.isAccess;
         this.topdir    = p.topdir;
         this.pack      = p.pack;
         this.file      = p.file;
         this.origpath  = p.origpath;
         this.path      = p.path;
         this.pathnodrive = p.pathnodrive;         
         this.topdirnum = p.topdirnum;
         this.drive     = p.drive;
         this.miscLong  = p.miscLong;
         this.hier      = (String[])p.hier.clone();
         this.hidx      = p.hidx;
         this.hlimit    = p.hlimit;
      }
      
      public PathInfo(String p) {
         parse(p, pwd);
      }
      public PathInfo(String p, String curdir) {
         parse(p, curdir);
      }
      
      public boolean equals(Object o) {
         if (o == null) return false;
         if (!(o instanceof PathInfo)) return false;
         
         PathInfo op = (PathInfo)o;
         if (path == null) {
            return op.path == null;
         } 
         
         if (op.path == null) return false;
         
         return path.equals(op.path);
      }      
      
      public void parse(String p, String curdir) {
      
        // Make sure we have a valid curdir
         if (curdir == null) curdir = File.separator;
         
        // Save the origpath
         origpath = p;
         
         
        // Convert all "\\" to File.separator
         if (!File.separator.equals("\\") && p.indexOf('\\') >= 0) { 
            p = p.replace('\\', File.separatorChar);
         }
         
        // Convert all "/"  to File.separator
         if (!File.separator.equals("/") && p.indexOf('/') >= 0) { 
            p = p.replace('/', File.separatorChar);
         }
         
        // If win32 device specification
         
         String pdrive   = null;
         String curdrive = null;
         
         // TODOTODOTODO add in support for \\host\share on Windows
         int colonidxp = p.indexOf(':');
         int sepidxp   = p.indexOf(File.separatorChar);
         if (colonidxp > 0 && (sepidxp < 0 || sepidxp > colonidxp)) {
            pdrive = p.substring(0, colonidxp) + ":";
            if (colonidxp+1 == p.length()) p = ".";
            else                           p = p.substring(colonidxp+1);
         }              
         
         int colonidxc = curdir.indexOf(':');
         int sepidxc   = curdir.indexOf(File.separatorChar);
         if (colonidxc > 0 && (sepidxc < 0 || sepidxc > colonidxc)) {
            curdrive = curdir.substring(0, colonidxc) + ":";
            if (colonidxc+1 == curdir.length()) {
               curdir = ".";
            } else {
               curdir = curdir.substring(colonidxc+1);
            }
         }
         
        // Set drive var
         if        (pdrive != null && curdrive == null) {
            drive = pdrive;
            curdir = File.separator;
         } else if (pdrive == null && curdrive != null) {
            drive = curdrive;
         } else if (pdrive != null && curdrive != null) {
            drive = pdrive;
            if (!pdrive.equalsIgnoreCase(curdrive)) {
               curdir = File.separator;
            }
         } else {
            drive = "";
         }
         
         
        // p is now driveless ... make a full path
         if (!p.startsWith(File.separator)) {
            if (curdir.endsWith(File.separator)) {
               p = curdir + p;
            } else {
               p = curdir + File.separator + p;
            }
         }
         
         path = p;
                  
        // Squash .. and . in path
         if (path.indexOf(fs_dot) >= 0) {
           // Handle . and ..
            int idx;
            while((idx=path.indexOf(fs_dot_fs)) >= 0) {
               path = path.substring(0,idx) + path.substring(idx+2);
            }
            if (path.endsWith(fs_dot)) {
               path = path.substring(0, path.length()-2);
            }
            
            if (path.length() == 0) {
               path = File.separator;
            }
            
            while((idx=path.indexOf(fs_dotdot_fs)) >= 0) {
               if (idx == 0) {
                  path = path.substring(idx+3);
               } else {
                  int tidx = path.lastIndexOf(File.separatorChar, idx-1);
                  path = path.substring(0,tidx) + path.substring(idx+3);
               }
            }
            if (path.endsWith(fs_dotdot)) {
               if (path.equals(fs_dotdot)) {
                  path = File.separator;
               } else {
                  idx = path.lastIndexOf(File.separatorChar, path.length()-4);
                  path = path.substring(0, idx);
               }
            }
            
            if (path.length() == 0) {
               path = File.separator;
            }
         }
         
        // No DRIVE for tokenization
         StringTokenizer stok = new StringTokenizer(path, File.separator);
         
         pathnodrive = path;
         
        // Add drive on resultant path
         path = drive + path;
         
         try {
            if (stok.hasMoreTokens()) {
               hier[hidx]=stok.nextToken();
               topdir = hier[hidx++];
            }
            if (stok.hasMoreTokens()) {
               hier[hidx]=stok.nextToken();
               pack = hier[hidx++];
            }
            if (stok.hasMoreTokens()) {
               hier[hidx]=stok.nextToken();
               file = hier[hidx++];
            }
            
            
            isValid = false;
            if (topdir != null) {
               if      (topdir.equals(INBOX))   topdirnum = INBOX_N;
               else if (topdir.equals(OUTBOX))  topdirnum = OUTBOX_N;
               else if (topdir.equals(SANDBOX)) topdirnum = SANDBOX_N;
               else if (topdir.equals(GROUPS))  topdirnum = GROUPS_N;
               
               if (topdirnum > 0) {
                  isValid = true;
                  if (topdirnum >= OUTBOX_N && topdirnum <= SANDBOX_N && 
                      file != null && file.equals(ACCESS)) {
                     file = null;
                     if (stok.hasMoreTokens()) {
                        hier[hidx]=stok.nextToken();
                        file = hier[hidx++];
                     }
                     isAccess = true;
                  }
                  
                 // 
                 // File names can contain hierarchy
                 //
                 // if (stok.hasMoreTokens()) isValid = false;
               }
               
              // Keep rest of tokens with file, use Unix separator char
               if (!isAccess) {
                  while(stok.hasMoreTokens()) {
                     if (hidx >= hlimit) {
                        String arr[] = new String[hlimit+10];
                        System.arraycopy(hier, 0, arr, 0, hidx);
                        hlimit += 10;
                        hier = arr;
                     }
                     hier[hidx] = stok.nextToken();
                     file += "/" + hier[hidx++];
                  }
               }
            } else {
               isValid = true;
            }
            
         } catch(NoSuchElementException nsee) {
            SearchEtc.printStackTrace(nsee);
         }                 
      }
      
      public String makePath() {
         String ret = File.separator;
         if (this.topdir != null) {
            ret += topdir;
            if (this.pack != null) {
               ret += File.separator + this.pack;
               if (this.isAccess) {
                  ret += File.separator + ACCESS;
               }
               if (this.file != null) {
                  ret += File.separator + this.file;
               }
            }
         }
         return ret;
      }
      
      public String toString() {
         return "PathInfo valid=" + isValid + 
                            " isaccess=" + isAccess + 
                            " Origpath=" + origpath +
                            " path=" + path +
                            " drive=" + drive + 
                            " topdir=" + topdir + 
                            " pack=" + pack + 
                            " file=" + file + 
                            " miscLong=" + miscLong + 
                            " hier=" + hier +
                            " hidx=" + hidx;
      }
   }
   
   
   public File makelocalfile(String s) { return new File(makelocalpath(s).path); }
   public PathInfo makelocalpath(String s) {
   
      s = s.trim();
      
     // Support ~ expansion
      if (s.startsWith("~")) {
      
         try {
         
           // Strip off ~
            String ts = s.substring(1).trim();
            String uname = null;
            
            if (getDebug()) {
               showmsg(100, true, "Searching for " + ts);
            }
                        
           // If its ~/... or ~, then get username from property
            if (ts.startsWith("/") || 
                ts.startsWith("\\") || 
                ts.length() == 0) {
               uname = System.getProperty("user.name");
            
               if (getDebug()) {
                  showmsg(100, true, "Got uname of " + uname);
               }
               
            } else {
            
              // Ok, we have ~name or ~name/path
               int idx = ts.indexOf("/");
               if (idx < 0) {
                  idx = ts.indexOf("\\");
               }
               
              // Must have ~name
               if (idx < 0) {
                  uname = ts;
                  ts = "";
               } else {
                 // ok, have ~name/path
                  uname = ts.substring(0,idx);
                  ts = ts.substring(idx);
               }
               if (getDebug()) {
                  showmsg(100, true, "Got uname of " + uname + " ts = " + ts);
               }
            }
            
           // ts has any path to append, uname has the username to lookup
           // Note that Creating the FileReader on Windows SHOULD cause 
           //  IOException, so skips the rest
            FileReader fr = new FileReader("/etc/passwd");
            BufferedReader br = new BufferedReader(fr);
            
           // Search for appropriate string
            String str;
            String unameColon = uname + ":";
            while((str=br.readLine()) != null) {
            
               if (str.startsWith(unameColon)) {
                  try {
                     StringTokenizer stok = new StringTokenizer(str, ":");
                     stok.nextToken(); stok.nextToken(); stok.nextToken();
                     stok.nextToken(); stok.nextToken(); 
                     s = stok.nextToken() + ts;
                  } catch(Exception tokex) {
                     if (getDebug()) {
                        showmsg(100, true, "Error doing nextToken");
                        DebugPrint.printlnd(DebugPrint.ERROR, tokex);
                     }
                  }
                  break;
               }
            }
            br.close();
         } catch(Exception ee) {
            showmsg(100, true, "Error doing ~ expansion for " + s + ": " +
                    ee.getMessage());
         }
      }
      
      PathInfo pinfo = new PathInfo(s, lpwd);
      return pinfo;
   }
   
   public boolean handleOperation(String cmd, File f, Operation op) 
      throws AbortAllException {
      
      char breakbuf[] = new char[1024];
      if (cmdfile == null && allowabort) {
         showmsg(100, false, "\n   - Hit Enter key to get prompt -\n");
      }
      
      long statdelt = emacs?5000:1000;
      long stattime = System.currentTimeMillis() + statdelt;
      printstats(op, true);
      boolean doheader = false;
      while(op.getStatus() < op.STATUS_TERMINATED) {
         long curtime = System.currentTimeMillis();
         long delt    = statdelt;
         if (curtime >= stattime) {
            printstats(op, doheader);
            doheader = false;
            stattime = curtime + statdelt;
            
            if (cmdfile == null) {
               boolean inputReady = false;
               
               String s = null;
               if (allowabort) {
                  try {
                    // Use System.in directly, the 'input' variable was
                    //  returning true for ready even when it was not!
                     while(System.in.available() > 0) {
                        int ch = System.in.read();
                        if (ch == '\n' || ch == -1) {
                           inputReady = true;
                        }
                     }
                  
                     if (inputReady) {
                        showmsg(100, false, 
                                "\nTransfer is still running ...");
                        showmsg(198, true, "\nEnter ABORT/ABORTALL<enter> or just enter to return to statistics: "); 
                        s = input.readLine();
                     }
                  } catch(IOException ioe) {}
               }
               
               if (inputReady) {
                  if (s == null || s.equalsIgnoreCase("abort") ||
                      s.equalsIgnoreCase("abortall")) {
                     op.abort();
                     showerror(426, true, 
                               "User aborted command " + cmd + " to file '" + 
                               f.getPath() + "'");
                     
                     if (s.equalsIgnoreCase("abortall")) {
                        throw new AbortAllException("Abort All");
                     }
                     
                     return false;
                  } else if (s.equalsIgnoreCase("stats")) {
                     cumulativeXferStats = !cumulativeXferStats;
                     showmsg(100, false, "Xfer stats are now " + 
                                        (cumulativeXferStats?
                                         "Cumulative":
                                         "Instantaneous"));
                  } else if (s.trim().length() != 0) {
                     showmsg(100, false, "Invalid abort command => " + s);
                  }
                  if (allowabort) {
                     showmsg(100, false, "\n   - Hit Enter key to get prompt -\n");
                     doheader = true;
                  }
               }
            } else {
               delt = stattime - curtime;
            }
            try {
               if (delt > 1000) {
                  synchronized(op) {
                     if(op.getStatus() < op.STATUS_TERMINATED) {
                        op.wait(delt);  
                     }
                  }
               } else {
                  if(op.getStatus() < op.STATUS_TERMINATED) {
                     Thread.sleep(delt);
                  }
               }
            } catch(InterruptedException ee) {}
         }
      }
      printstats(op, doheader);
      if (dostats && !emacs && !parseable) showmsg(100, true, "");
      if (op.getStatus() != op.STATUS_FINISHED) {
         String errmsg = op.getErrorMessages();
         if (errmsg != null && errmsg.length() > 0) {
            errmsg = ": " + errmsg;
         }
         showerror(426, true, "Error applying command " + cmd + " to file '" + 
                   f.getPath() + "'" + errmsg);
         return false;
                   
      } else {
         showmsg(101, true, "Operation succeeded"); 
      }
      
      return true;
   }
      
      
   public boolean matchrecurs(String s, int sidx, 
                              String splats[], int idx, int num) {
      
     // If we are past the end, then we are golden
      if (idx >= num && sidx >= s.length()) return true;
      
     // If we are past the end, but not finished ... Bzzz
      if (idx >= num) {
         return false;
      }
      
                         
                            
     // If this is a splat, work on next item
      if (splats[idx] == null) {
         if (++idx >= num) return true;
         while((sidx = s.indexOf(splats[idx],sidx)) >= 0) {
            if (matchrecurs(s, sidx, splats, idx, num)) {
               return true;
            }
            sidx++;
         }
      } else {
      
        // Must match in current position
         if (s.indexOf(splats[idx], sidx) == sidx) {
            return matchrecurs(s, sidx + splats[idx].length(), 
                               splats, idx+1, num);
         }
      }
      return false;
   }
      
   public int getMatchingWilds(String n, String prefix, Object inlist,
                               Vector matches) {
                               
      int ret = 0;
      String splats[] = new String[128];
      int preidx    = 0;
      int splatsidx = 0;
      
     // Build splats array (contains null for WILD, and string for FIXED
      while(preidx < n.length()) {
         try {
            int idx = n.indexOf("*", preidx);
            if (idx >= 0) {
               
               if (idx == preidx) {
                  if (splatsidx == 0 || splats[splatsidx-1] != null) {
                    //System.out.println("*");
                     splats[splatsidx++] = null;
                  }
               } else {
                  splats[splatsidx++] = n.substring(preidx, idx);
                  splats[splatsidx++] = null;
                 //System.out.println(n.substring(preidx, idx));
                 //System.out.println("*");
               }
               
               preidx = idx+1;
               
            } else {
               
              // Take rest of chars
               splats[splatsidx++] = n.substring(preidx);
              //System.out.println(n.substring(preidx));               
               break;
            }
            
         } catch(ArrayIndexOutOfBoundsException aioobe) {
           //aioobe.printStackTrace();
            break;
         } catch(IndexOutOfBoundsException ioobe) {
           //ioobe.printStackTrace();
            break;
         }
      }
      
      if (inlist instanceof Object[]) {
         Object list[] = (Object[])inlist;
         for(int j=0; j < list.length; j++) {
         
            if (list[j] != null) {
            
               Object obj = list[j];
               String s = null;
               if (obj instanceof PackageInfo) {
                  s = ((PackageInfo)obj).getPackageName();
               } else if (obj instanceof FileInfo) {
                  s = ((FileInfo)obj).getFileName();
               } else if (obj instanceof GroupInfo) {
                  s = ((GroupInfo)obj).getGroupName();
               } else if (obj instanceof AclInfo) {
                  s = ((AclInfo)obj).getAclName();
               } else if (obj instanceof String) {
                  s = (String)obj;
               }
               
               if (s != null) {
                  if (matchrecurs(s, 0, splats, 0, splatsidx)) {
                     ret++;
                     matches.addElement(prefix + s);
                  }
               }
            }
         }
      } else {
      
         Iterator enum =  null;
         if (inlist instanceof Vector) {
            enum = ((Vector)inlist).iterator();
         } else if (inlist instanceof Map) {
            enum = ((Map)inlist).values().iterator();
         }
         if (enum != null) {
            while (enum.hasNext()) {
               Object obj = enum.next();
               String s = null;
               if (obj instanceof PackageInfo) {
                  s = ((PackageInfo)obj).getPackageName();
               } else if (obj instanceof FileInfo) {
                  s = ((FileInfo)obj).getFileName();
               } else if (obj instanceof GroupInfo) {
                  s = ((GroupInfo)obj).getGroupName();
               } else if (obj instanceof AclInfo) {
                  s = ((AclInfo)obj).getAclName();
               } else if (obj instanceof String) {
                  s = (String)obj;
               }
               
               if (s != null) {
                  if (matchrecurs(s, 0, splats, 0, splatsidx)) {
                     ret++;
                     matches.addElement(prefix + s);
                  }
               }
            }
         }
      }
      return ret;
   }
   
  // Return vector of String filenames
   public Vector fileWild(String wild, Vector ret) {
   
      if (ret == null) ret = new Vector();
      PathInfo pinfo = makelocalpath(wild);
      File f;      
      
     // Return of wild is null or null len
      if (wild == null || wild.length() == 0) return ret;
      
      StringTokenizer stok = new StringTokenizer(pinfo.pathnodrive, File.separator, false);
                                                 
     // Starting candidates is just the tree root ... at the end, anything in 
     //  candidates is a match
      Vector newcandidates = new Vector();
      Vector candidates    = new Vector();
      
     // Get the correct starting candidate
      String initialcandidate = File.separator;
      if (pinfo.drive.length() > 0) {
         initialcandidate = pinfo.drive + File.separator;     	
      }
      
      candidates.add(initialcandidate);
      
     // For each token, if no wild, just add it on and loop around
     //  otherwise, for each candidate, list entries and do wild check
      while(stok.hasMoreTokens()) {
      
         String tok = stok.nextToken();
         
        // Consider adding trim here
         if (tok.length() == 0) continue;
         
         boolean iswild = tok.indexOf("*") >= 0;
         
         Iterator it = candidates.iterator();
         while(it.hasNext()) {
            
            String s = (String)it.next();
            
            String ns = s;
            if (!ns.endsWith(File.separator)) ns += File.separator;
            
            if (iswild) {
               f = new File(s);
               String list[] = f.list();
               if (list != null) {
                  getMatchingWilds(tok, ns, list, newcandidates); 
               }
            } else {
               ns += tok;
               newcandidates.add(ns);
            }
         }
         Vector swap = candidates;
         candidates = newcandidates;
         newcandidates = swap;
         newcandidates.clear();
      }
      
      ret.addAll(candidates);
      
      return ret;
   }
   
   
  // Strips off the last position of incoming path, and replaces it with
  //  the match
  
   protected String stripLastSegment(PathInfo pinfo) {
      String p = null;
      int idx = pinfo.path.lastIndexOf(File.separator);
      if (idx == -1) return "/";
      if (idx == 0) {
         p = File.separator;
      } else {
         p = pinfo.path.substring(0, idx+1);
      }
      return p;
   }
   protected void fillWithMatches(Vector ret, Vector matches, 
                                  PathInfo pinfo) {
      Enumeration menum = matches.elements();
      String p = null;
      int idx = pinfo.path.lastIndexOf(File.separator);
      if (idx == -1) return;
      if (idx == 0) {
         p = File.separator;
      } else {
         p = pinfo.path.substring(0, idx+1);
      }
      while(menum.hasMoreElements()) {
         String s = (String)menum.nextElement();
         PathInfo pp =  new PathInfo(p + s);
         ret.addElement(pp);
      }
   }
   
  // Return vector of PathInfo objs based on entire pathinfo
   public Vector packageWild(Vector inV) {
      return packageWild(inV, false);
   }
   
   
  // Return vector of PathInfo objs based on lastFieldStripped for checking
   public Vector packageWild(Vector inV, boolean strip) {
      Vector ret = new Vector();
      
      Enumeration pathenum = inV.elements();
      while(pathenum.hasMoreElements()) {
         PathInfo pinfo = (PathInfo)pathenum.nextElement();
         packageWild(pinfo, strip, ret);
      }      
      return ret;
   }
   
  // Return vector of PathInfo objs which are valid (wilds expanded)
  // If strip is specified, then the last logical element of inpinfo is
  //  ignored. For a file in a package, it may contain slashes, so handle
  //
  // Also, since a file can contain slashes, when NOT stripping, we need
  //  to match ALL trailing tokens. The 'stripped' tokens remain on the 
  //  returned match
   public Vector packageWild(PathInfo inpinfo, boolean strip, Vector ret) {
   
      if (ret == null) ret = new Vector();
      
      int retnum = ret.size();
         
      String tp = inpinfo.path;
      
      String filematch = null;
      
      StringTokenizer stok = new StringTokenizer(tp, File.separator, false);
      
     // Starting candidates is just the tree root ... at the end, anything in 
     //  candidates is a match
      Vector newcandidates = new Vector();
      Vector candidates    = new Vector();
      
     // This should ALWAYS be true
      if (stok.hasMoreTokens()) candidates.add(new PathInfo(File.separator));
      
     // For each token, if no wild, just add it on and loop around
     //  otherwise, for each candidate, list entries and do wild check
      int toknum = 0;
      while(stok.hasMoreTokens()) {
      
         String tok = stok.nextToken();
         toknum++;
         
        // Consider adding trim here
         if (tok.length() == 0) continue;
         
         Iterator it = candidates.iterator();
         while(it.hasNext()) {
            
            PathInfo lpath = (PathInfo)it.next();
            
            String ns = lpath.path;
            if (!ns.endsWith(File.separator)) ns += File.separator;
            
            ns += tok;
      
            PathInfo pinfo = new PathInfo(ns);
            
           // If this is the last token and we are stripping, just take the match and go
            if (strip && !stok.hasMoreTokens()) { 
               ret.add(pinfo); continue;
            }
            
            try {
         
               if (!pinfo.isValid) {
               
                 // Invalid cases which may STILL be valid (fixed named items
                 //  which may contain wilds) IN/OUT/SANDBOX
               
                  if (pinfo.pack == null && pinfo.topdir != null) {
                     String sarr[] = { INBOX, OUTBOX, SANDBOX, GROUPS };
                     Vector matches = new Vector();
                     getMatchingWilds(pinfo.topdir, "", sarr, 
                                      matches);
                     Enumeration menum = matches.elements();
                     while(menum.hasMoreElements()) {
                        String ls = (String)menum.nextElement();
                        pinfo.topdir = ls;
                        PathInfo pp =  new PathInfo(pinfo.makePath());
                        newcandidates.addElement(pp);
                     }
                  }
               
               } else if (pinfo.pack == null) {
               
                  newcandidates.addElement(pinfo);
                  
               } else {
               
                 // Valid cases which may contain wilds are packages, files, 
                 //  and acl dirs
              
                  if (pinfo.isAccess) {
                  
                     if (pinfo.file != null) {
                        Vector v = listInOutSandBox(pinfo.topdirnum, pinfo.pack);
                        Enumeration enum = v.elements();
                        while(enum.hasMoreElements()) {
                           PackageInfo pi = (PackageInfo)enum.nextElement();
                        
                           if (pi.getPackageName().equals(pinfo.pack)) {
                              Vector acls = queryAcls(pi.getPackageId());
                           
                              Vector matches = new Vector();
                              getMatchingWilds(pinfo.file, "", acls, matches);
                           
                              Enumeration menum = matches.elements();
                              while(menum.hasMoreElements()) {
                                 String ls = (String)menum.nextElement();
                                 pinfo.file = ls;
                                 PathInfo pp = 
                                    new PathInfo(pinfo.makePath());
                                 newcandidates.addElement(pp);
                              }
                           }
                        }
                     } else {
                       // not sure I can get here
                        newcandidates.addElement(pinfo);
                     }
                  
                  } else if (pinfo.topdirnum == GROUPS_N && pinfo.pack != null) {
               
                    /*
                    **  Each group found in one of the all, modifiable and
                    **  owned subdirectories also contain a directory hierarchy:
                    **
                    ** groups                                     hierIdx = 0
                    **     all                                    hierIdx = 1
                    **     modifiable                             hierIdx = 1
                    **     owned                                  hierIdx = 1
                    **        groupname                           hierIdx = 2
                    **            members                         hierIdx = 3
                    **                ccid1                       hierIdx = 4
                    **	           ccidN                       hierIdx = 4
                    **            properties                      hierIdx = 3
                    **	           owner                       hierIdx = 4 
                    **	               userid                  hierIdx = 5
                    **                       ccid                 hierIdx = 6
                    **	               company                 hierIdx = 5
                    **		          companyname          hierIdx = 6
                    **                access                      hierIdx = 4
                    **	               ccid1                   hierIdx = 5
                    **                    ccidN                   hierIdx = 5
                    **                visibility                  hierIdx = 4
                    **       	       owner | members | all   hierIdx = 5
                    **                listability                 hierIdx = 4  
                    **	               owner | members | all   hierIdx = 5
                    */

                    // Looking up groups with atleast 'pack' level specified
                 
                     if (true) {
                       // If we go no deeper than pack, just return matches
                       //  from all, modifiable, owned
                        if (pinfo.hidx == 2) {
                           String sarr[] = { ALL, MODIFIABLE, OWNED };
                           Vector matches = new Vector();
                           getMatchingWilds(pinfo.hier[2-1],
                                            "", sarr, matches);
                           fillWithMatches(newcandidates, matches, pinfo);
                        } else {
                           
                           boolean isAll = pinfo.hier[2-1].equals(ALL);
                           boolean isMod = pinfo.hier[2-1].equals(MODIFIABLE);
                           boolean isOwn = pinfo.hier[2-1].equals(OWNED); 
                           
                           String baseSeg = stripLastSegment(pinfo);
                           
                           if (!isAll && !isMod && !isOwn) {
                              continue;
                           }
                           
                           Map  groups = listGroups();
                              
                          // If querying at group level
                           if (pinfo.hidx == 3) {
                              if (groups.size() > 0) {
                                 Vector matches = new Vector();
                                 getMatchingWilds(pinfo.hier[3-1], "", 
                                                  groups, matches);
                                                     
                                 Enumeration menum = matches.elements();
                                 while(menum.hasMoreElements()) {
                                    String ls = (String)menum.nextElement();
                                    
                                    GroupInfo gi = (GroupInfo)groups.get(ls);
                                    
                                    boolean addit = false;
                                    
                                    if (isAll) {
                                       addit = true;
                                    } else if (isOwn) {
                                       if (gi.getGroupOwner().equals(who)) {
                                          addit=true;
                                       }
                                    } else if (isMod) {
                                       if (gi.getGroupOwner().equals(who) ||
                                           gi.getGroupAccess().contains(who)){
                                          addit=true;
                                       }
                                    }
                                    
                                    if (addit) {
                                       pinfo.file = ls;
                                       PathInfo pp = 
                                          new PathInfo(baseSeg + ls);
                                       newcandidates.addElement(pp);
                                    }
                                 }
                              }
                              continue;
                           } 
                           
                           GroupInfo gi = 
                              (GroupInfo)groups.get(pinfo.hier[3-1]);
                           
                           boolean addit  = false;
                           boolean canMod = 
                              gi.getGroupOwner().equals(who) ||
                              gi.getGroupAccess().contains(who);
                           
                           if (isAll) {
                              addit = true;
                           } else if (isOwn) {
                              if (gi.getGroupOwner().equals(who)) {
                                 addit=true;
                              }
                           } else if (isMod) {
                              if (canMod) {
                                 addit=true;
                              }
                           }
                           
                           if (!addit) continue;
                                    
                           if (pinfo.hidx == 4) {
                              
                              Vector v = new Vector();
                              if (gi.getGroupMembersValid()) {
                                 v.addElement(MEMBERS);
                              }
                              v.addElement(PROPERTIES);
                              
                              Vector matches = new Vector();
                              
                              getMatchingWilds(pinfo.hier[4-1], 
                                               "", v, matches);
                              fillWithMatches(newcandidates, matches, pinfo);
                              continue;
                           } 
                           
                           if (pinfo.hier[4-1].equals(MEMBERS)) {
                              
                              if (gi.getGroupMembersValid() &&
                                  pinfo.hidx == 5) {
                                 
                                 Vector matches = new Vector();
                                 Vector v = gi.getGroupMembers();
                                 if (v.size() > 0) {
                                    getMatchingWilds(pinfo.hier[5-1],
                                                     "", v, matches);
                                    fillWithMatches(newcandidates, matches, pinfo);
                                 }
                              }
                              continue;
                           }
                                    
                           if (!pinfo.hier[4-1].equals(PROPERTIES)) {
                              continue;
                           }
                           
                           
                           if (pinfo.hidx == 5) {
                              
                              Vector v = new Vector();
                              if (canMod) {
                                 v.addElement(GACCESS);
                                 v.addElement(LISTABILITY);
                                 v.addElement(VISIBILITY);
                              }
                              v.addElement(OWNER);
                              
                              Vector matches = new Vector();
                              
                              getMatchingWilds(pinfo.hier[5-1], 
                                               "", v, matches);
                              fillWithMatches(newcandidates, matches, pinfo);
                              continue;
                           } 
                           
                           if (pinfo.hidx == 6) {
                              if (canMod && 
                                  (pinfo.hier[5-1].equals(LISTABILITY) || 
                                   pinfo.hier[5-1].equals(VISIBILITY))) {
                                 byte lvb = 
                                    pinfo.hier[5-1].equals(LISTABILITY) 
                                    ? gi.getGroupListability()
                                    : gi.getGroupVisibility();
                                 
                                 String ls = null;
                                 switch(lvb) {
                                    case DropboxGenerator.GROUP_SCOPE_ALL:
                                       ls = ALL;
                                       break;
                                    case DropboxGenerator.GROUP_SCOPE_MEMBER:
                                       ls = MEMBERS;
                                       break;
                                    case DropboxGenerator.GROUP_SCOPE_OWNER:
                                       ls = OWNER;
                                       break;
                                    default:
                                       continue;
                                 }
                                 
                                 String v[] = new String[1]; 
                                 v[0] = ls;
                                 Vector matches = new Vector();
                                 getMatchingWilds(pinfo.hier[6-1], 
                                                  "", v, matches);
                                 fillWithMatches(newcandidates, matches, pinfo);
                                 continue;
                                 
                              } else if (canMod &&
                                         pinfo.hier[5-1].equals(GACCESS)) {
                                 Vector matches = new Vector();
                                 getMatchingWilds(pinfo.hier[6-1], 
                                                  "", gi.getGroupAccess(), 
                                                  matches);
                                 fillWithMatches(newcandidates, matches, pinfo);
                              } else if (pinfo.hier[5-1].equals(OWNER)) {
                                 String v[] = { USERID, COMPANY };
                                 Vector matches = new Vector();
                                 getMatchingWilds(pinfo.hier[6-1], 
                                                  "", v, matches);
                                 fillWithMatches(newcandidates, matches, pinfo);
                              }
                              continue;
                           }
                           
                           if (pinfo.hidx == 7) {
                              String v[] = new String[1];
                              if        (pinfo.hier[6-1].equals(USERID)) {
                                 v[0] = gi.getGroupOwner();
                              } else if (pinfo.hier[6-1].equals(COMPANY)) {
                                 v[0] = gi.getGroupCompany();
                              } else {
                                 v = null;
                              }
                              
                              if (v != null) {
                                 Vector matches = new Vector();
                                 getMatchingWilds(pinfo.hier[7-1], 
                                                  "", v, matches);
                                 fillWithMatches(newcandidates, matches, pinfo);
                              }
                           }
                           
                           continue;
                        }
                     }
                     
                  } else if (pinfo.pack != null) {
               
                     if (true) {
                      
                        Vector v = listInOutSandBox(pinfo.topdirnum, pinfo.pack);
                     
                       // Splatting package
                        Enumeration enum = v.elements();
                        if (pinfo.file == null) {
                     
                           if (enum.hasMoreElements()) {
                              Vector matches = new Vector();
                              getMatchingWilds(pinfo.pack, "", v, matches);
                              Enumeration menum = matches.elements();
                              while(menum.hasMoreElements()) {
                                 String ls = (String)menum.nextElement();
                                 pinfo.pack = ls;
                              
                                 PathInfo pp = new PathInfo(pinfo.makePath());
                                 newcandidates.addElement(pp);
                              }
                           }
                        } else {
                        
                          //
                          // Special case here is checking #ACCESS# as hierarchy still
                          //  applies to that and its children. If we were stripping,
                          //  and this element will match #ACCESS#, we are only here
                          //  if there is at least one more token. If there were not 
                          //  another token, it would have been stripped up top. So,
                          //  whether we are stripping or not, match #ACCESS# separate
                          //  from other files, and if match, add to newcandidates
                          //  for further processing
                          //
                           if (pinfo.topdirnum != INBOX_N) {
                              String sarr[] = new String[1];
                              sarr[0] = ACCESS;
                              
                              Vector matches = new Vector();
                              getMatchingWilds(tok, "", sarr, matches);
                              Enumeration menum = matches.elements();
                              while(menum.hasMoreElements()) {
                                 String ls = (String)menum.nextElement();
                                 pinfo.file = ls;
                                 PathInfo pp =  
                                    new PathInfo(pinfo.makePath());
                                 newcandidates.addElement(pp);
                              }
                           }
                              
                          // If we make it here and we are stripping, add this 
                          //  in and skip the rest ad we want to add the rest of 
                          //  the file tokens to the pathinfo
                           if (strip) {
                              newcandidates.addElement(pinfo);
                              continue;
                           }
                           
                          // Ok, we are not stripping, but we need to match
                          //  ALL the rest of the elements in one shot. If 
                          //  we match, add to ret, otherwise, ditch it
                          //
                          // So, set up pinfo.file to contain ALL file parts
                              
                           while(enum.hasMoreElements()) {
                              PackageInfo pi = (PackageInfo)enum.nextElement();
                             // Splatting file
                              if (pi.getPackageName().equals(pinfo.pack)) {
                                 Vector contents = 
                                    listPackageContents(pi.getPackageId());
                                 String sarr[] = new String[contents.size()+1];
                              
                                 int idx = 0;
                                 if (contents.size() > 0) {
                                    Enumeration cenum = contents.elements();
                                    while(cenum.hasMoreElements()) {
                                       FileInfo finfo = 
                                          (FileInfo)cenum.nextElement();
                                       sarr[idx++] = finfo.getFileName();
                                    }
                                 }
                                 
                                // Build the filematch string if not built already
                                 if (filematch == null) {
                                    
                                    StringTokenizer lstok = 
                                       new StringTokenizer(tp, File.separator, false);
                                    
                                    for (int i=0; i < toknum; i++) {
                                       lstok.nextToken();
                                    }
                                    
                                    StringBuffer ans = new StringBuffer(tok);
                                    while(lstok.hasMoreTokens()) {
                                       ans.append(File.separator);
                                       ans.append(lstok.nextToken());
                                    }
                                    filematch = ans.toString();
                                 }
                                 
                                 
                                 Vector matches = new Vector();
                                 getMatchingWilds(filematch, "", sarr, 
                                                  matches);
                                 Enumeration menum = matches.elements();
                                 while(menum.hasMoreElements()) {
                                    String ls = (String)menum.nextElement();
                                    pinfo.file = ls;
                                    PathInfo pp =  
                                       new PathInfo(pinfo.makePath());
                                    ret.addElement(pp);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            } catch(Exception e) {
               if (printexception) SearchEtc.printStackTrace(e, System.out);
               showerror(102, true, "Error doing package wild: " + e.getMessage());
            
            }
         }
         Vector swap = candidates;
         candidates = newcandidates;
         newcandidates = swap;
         newcandidates.clear();
      }
      
     // Add orig if no candidates
      ret.addAll(candidates);
      if (ret.size() == retnum) {
         ret.addElement(inpinfo);
      }
      
      return ret;
   }
   
   
   public void showfile(File f, boolean longlist, boolean md5, 
                        String indent, boolean extlay) {
      if (f.exists()) {
         String md5S = null;
         if (f.isFile() && md5) {
            try {
               md5S = SearchEtc.calculateMD5(f);
            } catch(Exception ee) {}
         }
         
         showmsgnoNL(101, true, indent);
         
         String ftype = (String)(f.isDirectory()?"D":(f.isFile()?"F":"U"));
         
         if (longlist) {
            if (md5S != null) {
               lsl("M", f.getName(), 
                   md5S, f.length(),
                   new Date(f.lastModified()), extlay);
            } else {
               lsl(ftype, f.getName(), 
                   "-",
                   "-",
                   f.length(),
                   new Date(f.lastModified()), extlay);
            }
         } else {
            if (md5S != null) {
               showmsg(101, true, pad(f.getName(), 30, true) + " " + md5S);
            } else {
               showmsg(101, true, f.getName());
            }
         }
      } else {
         showmsg(101, true, (f.getAbsolutePath() + ": permission denied"));
      }
   }
   
      
  // Specific commands from user
   void cmd_cd(Command c, String arr[], int numused) {
      if (numused != 2) {
         showerror(501, true, "Command '" + arr[0] + "' takes 1 parameter");
         return;
      }
      
      Vector todo = new Vector();
      todo.addElement(new PathInfo(arr[1]));
      
      todo = packageWild(todo);
      
      if (todo.size() != 1) {
         showerror(501, true, "Invalid number of paramaters");
         return;
      }
      
      PathInfo pinfo = (PathInfo)todo.elements().nextElement();
      
     // If not valid address, 
      if (!pinfo.isValid     || 
          (pinfo.file != null && pinfo.topdirnum != GROUPS_N)) {
         showerror(503, true, 
                   "Cannot cd to specified location: " + pinfo.origpath);
         return;
      }
      
      if (pinfo.pack == null) {
         pwd = pinfo.path;
      } else {
         try {
         
           // Group CD
            if (pinfo.topdirnum == GROUPS_N) {
               
              // We know we have at least hidx of 2 at this point
               boolean isAll = pinfo.hier[2-1].equals(ALL);
               boolean isMod = pinfo.hier[2-1].equals(MODIFIABLE);
               boolean isOwn = pinfo.hier[2-1].equals(OWNED); 
               
               if (!isAll && !isMod && !isOwn) {
                  showerror(503, true, 
                            "Cannot cd to specified location: " + 
                            pinfo.origpath);
                  return;
               }

               if (pinfo.hidx == 2) { 
                  pwd = pinfo.path;
                  return;
               }
               
               Map  groups = listGroups();
               Enumeration enum  = null;
               
              // If we have a particular group in mind here, simply look it
              //  up, and set the enum to contain just that group
               GroupInfo gi = (GroupInfo)groups.get(pinfo.hier[3-1]);
               if (gi == null) {
                  showerror(503, true, 
                            "Cannot cd to specified location: " + 
                            pinfo.origpath);
                  return;
               }
               
               boolean addit  = false;
               boolean canMod = 
                  gi.getGroupOwner().equals(who) ||
                  gi.getGroupAccess().contains(who);
                  
              // Check that this group should showup in specified hier[2-1]
               if (isAll) {
                 // We know its visible since it was returned from DboxSrv
                  addit = true;
               } else if (isOwn) {
                  if (gi.getGroupOwner().equals(who)) {
                     addit=true;
                  }
               } else if (isMod) {
                  if (canMod) {
                     addit=true;
                  }
               }
               
               if (!addit) {
                  showerror(503, true, 
                            "Cannot cd to specified location: " + 
                            pinfo.origpath);
                  return;
               }
                  
               if (pinfo.hidx == 3) { 
                  pwd = pinfo.path;
                  return;
               }
               
               
               boolean canList = gi.getGroupMembersValid();
               
               if (pinfo.hier[4-1].equals(MEMBERS)) {
                  if (canList && pinfo.hidx == 4) {
                     pwd = pinfo.path;
                     return;
                  }
                  
               } else if (pinfo.hier[4-1].equals(PROPERTIES)) {
               
                  if (pinfo.hidx == 4) { 
                     pwd = pinfo.path;
                     return;
                  }
                  
                  if (pinfo.hier[5-1].equals(GACCESS)) {
                     if (canMod && pinfo.hidx == 5) {
                        pwd = pinfo.path;
                        return;
                     }
                  } else if (pinfo.hier[5-1].equals(LISTABILITY)) {
                     if (canMod && pinfo.hidx == 5) {
                        pwd = pinfo.path;
                        return;
                     }
                  } else if (pinfo.hier[5-1].equals(VISIBILITY)) {
                     if (canMod && pinfo.hidx == 5) {
                        pwd = pinfo.path;
                        return;
                     }
                  } else if (pinfo.hier[5-1].equals(OWNER)) {
                     if (pinfo.hidx == 5) {
                        pwd = pinfo.path;
                        return;
                     }
                     
                     if (pinfo.hidx == 6) {
                        if (pinfo.hier[6-1].equals(USERID) ||
                            pinfo.hier[6-1].equals(COMPANY)) {
                           pwd = pinfo.path;
                           return;
                        }
                     }
                  }
               }
               
               showerror(503, true, 
                         "Cannot cd to specified location: " + 
                         pinfo.origpath);
               return;
               
              // End of GROUP CD
               
            } else {
               Vector v = listInOutSandBox(pinfo.topdirnum, pinfo.pack);
               Enumeration enum = v.elements();
               while(enum.hasMoreElements()) {
                  PackageInfo pi = (PackageInfo)enum.nextElement();
                  if (pi.getPackageName().equals(pinfo.pack)) {
                     pwd = pinfo.path;
                     return;
                  }
               }
            }
            showerror(503, true,
                      "Cannot cd to specified location: " + pinfo.origpath);
         } catch(Exception e) { 
            if (printexception) SearchEtc.printStackTrace(e, System.out);
            showerror(503, true, 
                      "Cannot cd to specified location: " + pinfo.origpath + 
                      ": " + e.getMessage());
         }
      }
   }
            
   void cmd_lcd(Command c, String arr[], int numused) {
      if (numused != 2) {
         showerror(501, true, "Command '" + arr[0] + "' takes 1 parameter");
         return;
      }
     
      Vector files = null;
      
      files = fileWild(arr[1], files);
            
      if (files.size() == 0) {
         showerror(503, true, arr[1] + ": No match found");
         return;
      }
      
      if (files.size() != 1) {
         showerror(503, true, arr[1] + ": too many matches found");
         return;
      }
      
      try {
         String lfile = (String)files.elementAt(0);
         File f = makelocalfile(lfile);
         
         if (!f.exists() || !f.isDirectory()) {
            showerror(503, true, lfile + ": does not exist or not directory");
         } else {
            lpwd = f.getAbsolutePath();
         }
      } catch(Exception ee) {}
   }
   void cmd_lsp(Command c, String arr[], int numused) {
   
      timer.start();
      
      if (numused != 1) {
         showerror(501, true, "Command '" + arr[0] + "' takes no parameters");
         return;
      }
      
      try {
         Vector v = getProjectList();
         Enumeration enum = v.elements();
         while(enum.hasMoreElements()) {
            showmsg(101, true, "_P_" + (String)enum.nextElement());
         }
      } catch(Exception e) {
         showerror(503, true, "Unable to list projects!?!: " + e.getMessage());
      }
   }
   
   void cmd_lspkgdesc(Command c, String arr[], int numused) {
   
      Vector todo = new Vector();
      
      
      
      PathInfo pinfo = null;
      if (numused < 2) {
         todo.addElement(new PathInfo(pwd));
      } else {
         todo.addElement(new PathInfo(arr[1]));
      }
      
      todo = packageWild(todo);
      
      if (todo.size() > 1) { 
         showerror(102, true, "Only one remote package may be specified");
         return;
      }
      
      Enumeration pathenum = todo.elements();
      while(pathenum.hasMoreElements()) {
      
         pinfo = (PathInfo)pathenum.nextElement();
         
         if (!pinfo.isValid                           ||
             pinfo.pack == null                       ||
             (pinfo.topdirnum != INBOX_N &&
              pinfo.topdirnum != OUTBOX_N &&
              pinfo.topdirnum != SANDBOX_N)) {
            
            showerror(102, true, 
                      "Cannot list package description for the specified entity: " + 
                      pinfo.path);
            return;
         }
         
         try {
            Vector v = listInOutSandBox(pinfo.topdirnum, pinfo.pack);
            Enumeration enum = v.elements();
            boolean found = false;
            while(enum.hasMoreElements()) {
               PackageInfo pi = (PackageInfo)enum.nextElement();
                  
               if (pinfo.pack.equals(pi.getPackageName())) {
                  String desc = pi.getPackageDescription();
                  if (desc == null) desc = "";
                  found = true;
                  
                  if (numused > 2) {
                     FileWriter fw = null;
                     try {
                        fw = new FileWriter(arr[2]);
                        fw.write(desc);
                        fw.close();
                        fw = null;
                        showmsg(101, true, 
                                "Package [" + pi.getPackageName() + "]" + 
                                " description saved to file " + arr[2]);
                     } catch(Exception ee) {
                        showmsg(102, true, 
                                "Package [" + pi.getPackageName() + "]" + 
                                " Error writing description to file " + arr[2]);
                     } finally {
                        try {
                           if (fw != null) fw.close();
                        } catch(Exception jj) {
                        }
                     }
                  } else {
                     showmsg(101, true, 
                             "Package [" + pi.getPackageName() + "]" + 
                             " description: " + desc);
                  }
               }
            }
            
            if (!found) {
               showerror(102, true, "Invalid Package : " + pinfo.pack);
            }
         } catch(Exception ee) {
            if (printexception) SearchEtc.printStackTrace(ee, System.out);
            showerror(102, true, "Cannot list package description for the specified entity: " + 
                      pinfo.path + ": " + ee.getMessage());
         }
      }
   }
   
   
   void cmd_ls(Command c, String arr[], int numused) {
      
      boolean longlist       = false;
      boolean extlay         = false;
      boolean directory      = false;
      boolean md5            = false;
      boolean opt            = false;
      boolean expiration     = false;
      
      if (numused > 1 && arr[1].startsWith("-")) {
      
         if (arr[1].length() == 1) {
            showerror(501, true, 
                      "Command '" + arr[0] + "' invalid option '-"+arr[1]+"'");
            return;
         }
         
         opt = true;
         for(int i=1; i < arr[1].length(); i++) {
            char ch = arr[1].charAt(i);
            if        (ch == 'd') {
               directory = true;
            } else if (ch == 'l') {
               longlist = true;
            } else if (ch == 'L') {
               longlist = true;
               extlay   = true;
            } else if (ch == 'm') {
               md5 = true;
            } else if (ch == 'e') {
               expiration = true;
            } else if (ch == '-') {
               ;
            } else {
               showerror(501, true, 
                         "Command '" + arr[0] + "' invalid option '-"+ch+"'");
               return;
            }
         }
      }
      
      timer.delta("Option parse");
      
      Vector todo = new Vector();
      for(int i=opt?2:1; i < numused; i++) {
         todo.addElement(new PathInfo(arr[i]));
      }
      
      if (numused == (opt?2:1)) {
         todo.addElement(new PathInfo(pwd));
      }
      
      todo = packageWild(todo);
      
      timer.delta("Wild calc");
      
      sortVector(todo, true, true);
      String lastp = null;
      
      Enumeration pathenum = todo.elements();
      while(pathenum.hasMoreElements()) {
         timer.start();
         PathInfo pinfo = (PathInfo)pathenum.nextElement();
         
        // If not valid address, 
         if (!pinfo.isValid) {
            showerror(102, true, 
                      "Cannot ls specified location: " + pinfo.origpath);
            continue;
         }
      
        // If At top (/)
         timer.delta("toploop");
         Date curdate = new Date();
         timer.delta("got date");
         
         if (pinfo.topdir == null) {
         
            if ((todo.size() > 1 || lastp != null) && 
                (lastp == null || !lastp.equals(pinfo.path))) {
               lastp = pinfo.path;
               showmsg(101, true, "\n" + lastp + ":\n");
            }
         
            if (longlist) {
               if (directory) {
                  lsl("D", File.separator,   who, company, 0L, curdate,extlay);
               } else {
                  lsl("D", GROUPS,  who, company, 0L, curdate, extlay);
                  lsl("D", INBOX,   who, company, 0L, curdate, extlay);
                  lsl("D", OUTBOX,  who, company, 0L, curdate, extlay);
                  lsl("D", SANDBOX, who, company, 0L, curdate, extlay);
               }
            } else {
               if (directory) {
                  showmsg(101, true, pinfo.origpath);
               } else {
                  showmsg(101, true, 
                          GROUPS+'\n'+INBOX+'\n'+OUTBOX+'\n'+SANDBOX);
               }
            }
         } 
         
        // In groups toplevel dir
         else if (pinfo.topdirnum == GROUPS_N) {
         
            try {
               
              // If ONLY have groups, then handle
               if (pinfo.hidx == 1) {
                  
                  String path = directory?File.separator:pinfo.path;
                  if ((todo.size() > 1 || lastp != null) && 
                      (lastp == null || !lastp.equals(path))) {
                     lastp = path;
                     showmsg(101, true, "\n" + lastp + ":\n");
                  }
                  
                  if (directory) {                    
                     if (longlist) {
                        lsl("D", GROUPS, who, company, 0L, curdate, extlay);
                     } else {
                        showmsg(101, true, GROUPS);
                     }
                  } else {
                     if (longlist) {
                        lsl("D", ALL, who, company, 0L, curdate, extlay);
                        lsl("D", MODIFIABLE, who, company, 0L, curdate,extlay);
                        lsl("D", OWNED, who, company, 0L, curdate, extlay);
                     } else {
                        showmsg(101, true, ALL);
                        showmsg(101, true, MODIFIABLE);
                        showmsg(101, true, OWNED);
                     }
                  }
                  continue;
               }
               
              // We know we have at least hidx of 2 at this point
               boolean isAll = pinfo.hier[2-1].equals(ALL);
               boolean isMod = pinfo.hier[2-1].equals(MODIFIABLE);
               boolean isOwn = pinfo.hier[2-1].equals(OWNED); 
               
//               showmsg(101, true, pinfo.toString());
               
               if (!isAll && !isMod && !isOwn) {
                  showerror(102, true, 
                            "Cannot ls specified location: " + pinfo.origpath);
                  continue;
               }

               if (pinfo.hidx == 2 && directory) {
                  String path = File.separator + GROUPS;
                  if ((todo.size() > 1 || lastp != null) && 
                      (lastp == null || !lastp.equals(path))) {
                     lastp = path;
                     showmsg(101, true, "\n" + lastp + ":\n");
                  }
                  
                  if (longlist) {
                     lsl("D", pinfo.hier[2-1], who, company, 0L, curdate, 
                         extlay);
                  } else {
                     showmsg(101, true, pinfo.hier[2-1]);
                  }
                  continue;
               }
               
               Map  groups = listGroups();
               Enumeration enum  = null;
               
              // If we have a particular group in mind here, simply look it
              //  up, and set the enum to contain just that group
               if (pinfo.hidx >= 3) {
                  GroupInfo gi = (GroupInfo)groups.get(pinfo.hier[3-1]);
                  if (gi == null) {
                     showerror(102, true, 
                               "Cannot ls specified location: " + 
                               pinfo.origpath);
                     continue;
                  }
                  Vector v = new Vector();
                  v.addElement(gi);
                  enum = v.elements();
               } 
               
              // Otherwise, lets visit each and every group
               else {
                  Vector v = new Vector(groups.values()); 
                  sortVector(v, true, true);
                  enum = v.elements();
               }
               
               boolean found = false;
               
              // If we are listing the groups, then if we made it this far,
              //  no complaints
               if (pinfo.hidx == 2) found = true;
               
              // For each GroupInfo ...
               while(enum.hasMoreElements()) {
                  GroupInfo gi = 
                     (GroupInfo)enum.nextElement();
                  
                  boolean addit  = false;
                  boolean canMod = 
                     gi.getGroupOwner().equals(who) ||
                     gi.getGroupAccess().contains(who);
                  
                 // Check that this group should showup in specified hier[2-1]
                  if (isAll) {
                    // We know its visible since it was returned from DboxSrv
                     addit = true;
                  } else if (isOwn) {
                     if (gi.getGroupOwner().equals(who)) {
                        addit=true;
                     }
                  } else if (isMod) {
                     if (canMod) {
                        addit=true;
                     }
                  }
                  
                  if (!addit) {
                     continue;
                  }
                  
                 // 
                  if (pinfo.hidx == 2 || (pinfo.hidx == 3 && directory)) {
                     String path = File.separator + GROUPS + 
                        File.separator + pinfo.hier[2-1];
                     if ((todo.size() > 1 || lastp != null) && 
                         (lastp == null || !lastp.equals(path))) {
                        lastp = path;
                        showmsg(101, true, "\n" + lastp + ":\n");
                     }
                     
                     if (longlist) {
                        lsl("D", gi.getGroupName(), gi.getGroupOwner(), 
                            gi.getGroupCompany(), 0L, curdate, 
                            extlay);
                     } else {
                        showmsg(101, true, gi.getGroupName());
                     }
                     found = true;
                     continue;
                  }
                  
                  boolean canList = gi.getGroupMembersValid();
                  
                  boolean doMembers    = false;
                  boolean doProperties = false;
                  
                 // If index goes to members/properties level, check now
                  if (pinfo.hidx >= 4) {
                     if (pinfo.hier[4-1].equals(MEMBERS)) {
                        if (!canList) {
                           continue;
                        }
                        doMembers = true;
                     } else if (pinfo.hier[4-1].equals(PROPERTIES)) {
                        doProperties = true;
                     } else {
                        break;  // This is an illegal value - no sense spinning
                     }
                  } else {
                     doMembers    = true;
                     doProperties = true;
                  }
                  
                  if (pinfo.hidx == 3 || (pinfo.hidx == 4 && directory)) {
                     String path = File.separator + GROUPS + 
                        File.separator + pinfo.hier[2-1] + 
                        File.separator + pinfo.hier[3-1];
                     if ((todo.size() > 1 || lastp != null) && 
                         (lastp == null || !lastp.equals(path))) {
                        lastp = path;
                        showmsg(101, true, "\n" + lastp + ":\n");
                     }
                     
                     if (longlist) {
                        if (canList && doMembers) {
                           lsl("D", MEMBERS, gi.getGroupOwner(), 
                               gi.getGroupCompany(), 0L, curdate, 
                               extlay);
                        }
                        if (doProperties) {
                           lsl("D", PROPERTIES, gi.getGroupOwner(), 
                               gi.getGroupCompany(), 0L, curdate, 
                               extlay);
                        }
                     } else {
                        if (canList && doMembers) {
                           showmsg(101, true, MEMBERS);
                        }
                        if (doProperties) {
                           showmsg(101, true, PROPERTIES);
                        }
                     }
                     found = true;
                     continue;
                  }
                  
                  if (doMembers) {
                     String member = pinfo.hier[5-1];
                     Vector members = gi.getGroupMembers();
                     if (pinfo.hidx > 5 || 
                         (pinfo.hidx == 5 && !members.contains(member))) {
                        continue;
                     }
                     
                     Enumeration memenum = null;
                     if (pinfo.hidx == 5) {
                        Vector v = new Vector();
                        v.addElement(member);
                        memenum = v.elements();
                     } else {
                        sortVector(members, true, true);
                        memenum = members.elements();
                     }
                     
                     String path = File.separator + GROUPS + 
                        File.separator + pinfo.hier[2-1] + 
                        File.separator + pinfo.hier[3-1] +
                        File.separator + MEMBERS;
                     if ((todo.size() > 1 || lastp != null) && 
                         (lastp == null || !lastp.equals(path))) {
                        lastp = path;
                        showmsg(101, true, "\n" + lastp + ":\n");
                     }
                        
                     while(memenum.hasMoreElements()) {
                        String mem = (String)memenum.nextElement();
                        
                        if (longlist) {
                           lsl("D", mem, "?", "?", 0L, curdate, 
                               extlay);
                        } else {
                           showmsg(101, true, mem);
                        }
                     }
                     
                     found = true;
                  }
                  
                  if (doProperties) {                  
                  
                     if (pinfo.hidx == 4) {
                        String path = File.separator + GROUPS + 
                           File.separator + pinfo.hier[2-1] + 
                           File.separator + pinfo.hier[3-1] +
                           File.separator + PROPERTIES;
                           
                        if ((todo.size() > 1 || lastp != null) && 
                            (lastp == null || !lastp.equals(path))) {
                           lastp = path;
                           showmsg(101, true, "\n" + lastp + ":\n");
                        }
                        
                        if (longlist) {
                           if (canMod) {
                              lsl("D", GACCESS, gi.getGroupOwner(), 
                                  gi.getGroupCompany(), 0L, curdate, 
                                  extlay);
                              lsl("D", LISTABILITY, gi.getGroupOwner(), 
                                  gi.getGroupCompany(), 0L, curdate, 
                                  extlay);
                           }
                           lsl("D", OWNER, gi.getGroupOwner(), 
                               gi.getGroupCompany(), 0L, curdate, 
                               extlay);
                           if (canMod) {
                              lsl("D", VISIBILITY, gi.getGroupOwner(), 
                                  gi.getGroupCompany(), 0L, curdate, 
                                  extlay);
                           }
                        } else {
                           if (canMod) {
                              showmsg(101, true, GACCESS);
                              showmsg(101, true, LISTABILITY);
                           }
                           showmsg(101, true, OWNER);
                           if (canMod) {
                              showmsg(101, true, VISIBILITY);
                           }
                        }
                        found = true;
                        continue;
                     }
                     
                    // Check validity level at hidx == 5
                     if (pinfo.hier[5-1].equals(GACCESS)) {
                        if (!canMod) continue;
                     } else if (pinfo.hier[5-1].equals(LISTABILITY)) {
                        if (!canMod) continue;
                     } else if (pinfo.hier[5-1].equals(VISIBILITY)) {
                        if (!canMod) continue;
                     } else if (!pinfo.hier[5-1].equals(OWNER)) {
                        continue;
                     }
                     
                     
                    // If we are doing directory, then doit
                     if (pinfo.hidx == 5 && directory) {
                        String path = File.separator + GROUPS + 
                           File.separator + pinfo.hier[2-1] + 
                           File.separator + pinfo.hier[3-1] +
                           File.separator + PROPERTIES;
                        
                        if ((todo.size() > 1 || lastp != null) && 
                            (lastp == null || !lastp.equals(path))) {
                           lastp = path;
                           showmsg(101, true, "\n" + lastp + ":\n");
                        }
                        
                        if (longlist) {
                           lsl("D", pinfo.hier[5-1], gi.getGroupOwner(), 
                               gi.getGroupCompany(), 0L, curdate, 
                               extlay);
                        } else {
                           showmsg(101, true, pinfo.hier[5-1]);
                        }
                        
                        found = true;
                        continue;
                     }
                     
                    // Ok, list the access member or all depending on hidx==6
                     if (pinfo.hier[5-1].equals(GACCESS)) {
                        String access = pinfo.hier[6-1];
                        Vector accessV = gi.getGroupAccess();
                        if (pinfo.hidx > 6 || 
                            (pinfo.hidx == 6 && !accessV.contains(access))) {
                           continue;
                        }
                        
                        Enumeration accenum = null;
                        if (pinfo.hidx == 6) {
                           Vector v = new Vector();
                           v.addElement(access);
                           accenum = v.elements();
                        } else {
                           sortVector(accessV, true, true);
                           accenum = accessV.elements();
                        }
                        
                        String path = File.separator + GROUPS + 
                           File.separator + pinfo.hier[2-1] + 
                           File.separator + pinfo.hier[3-1] +
                           File.separator + PROPERTIES + 
                           File.separator + GACCESS;
                        if ((todo.size() > 1 || lastp != null) && 
                            (lastp == null || !lastp.equals(path))) {
                           lastp = path;
                           showmsg(101, true, "\n" + lastp + ":\n");
                        }
                        
                        while(accenum.hasMoreElements()) {
                           String acc = (String)accenum.nextElement();
                           
                           if (longlist) {
                              lsl("D", acc, "?", "?", 0L, curdate, 
                                  extlay);
                           } else {
                              showmsg(101, true, acc);
                           }
                        }
                        
                        found = true;
                        continue;
                     } 
                     
                    // Listability 
                    // Visibility
                     else if (pinfo.hier[5-1].equals(LISTABILITY) ||
                              pinfo.hier[5-1].equals(VISIBILITY)) {
                              
                        String vlv = pinfo.hier[6-1];
                        if (pinfo.hidx > 6 || 
                            (pinfo.hidx == 6 && 
                             !(vlv.equals(MEMBERS) ||
                               vlv.equals(OWNER)   ||
                               vlv.equals(ALL)))) {
                           continue;
                        }
                        
                        if (pinfo.hidx == 5) vlv = null;
                        
                        byte vv = pinfo.hier[5-1].equals(VISIBILITY) 
                           ? gi.getGroupVisibility()
                           : gi.getGroupListability();
                           
                        switch(vv) {
                           case DropboxGenerator.GROUP_SCOPE_ALL:
                              if (vlv != null && !vlv.equals(ALL)) continue;
                              vlv = ALL;
                              break;
                           case DropboxGenerator.GROUP_SCOPE_MEMBER:
                              if (vlv != null && 
                                  !vlv.equals(MEMBERS)) continue;
                              vlv = MEMBERS;
                              break;
                           case DropboxGenerator.GROUP_SCOPE_OWNER:
                              if (vlv != null && !vlv.equals(OWNER)) continue;
                              vlv = OWNER;
                              break;
                           default:
                              continue;
                        }
                           
                        String path = File.separator + GROUPS + 
                           File.separator + pinfo.hier[2-1] + 
                           File.separator + pinfo.hier[3-1] +
                           File.separator + PROPERTIES + 
                           File.separator + pinfo.hier[5-1];
                        if ((todo.size() > 1 || lastp != null) && 
                            (lastp == null || !lastp.equals(path))) {
                           lastp = path;
                           showmsg(101, true, "\n" + lastp + ":\n");
                        }
                        if (longlist) {
                           lsl("D", vlv, gi.getGroupOwner(),
                               gi.getGroupCompany(), 0L, curdate, 
                               extlay);
                        } else {
                           showmsg(101, true, vlv);
                        }
                        
                        found = true;
                        continue;
                     }
                     
                    // Owner
                     else if (pinfo.hier[5-1].equals(OWNER)) {
                        
                        if (pinfo.hidx == 5) {
                           String path = File.separator + GROUPS + 
                              File.separator + pinfo.hier[2-1] + 
                              File.separator + pinfo.hier[3-1] +
                              File.separator + PROPERTIES + 
                              File.separator + pinfo.hier[5-1];
                           if ((todo.size() > 1 || lastp != null) && 
                               (lastp == null || !lastp.equals(path))) {
                              lastp = path;
                              showmsg(101, true, "\n" + lastp + ":\n");
                           }
                        
                           if (longlist) {
                              lsl("D", COMPANY, gi.getGroupOwner(),
                                  gi.getGroupCompany(), 0L, curdate, 
                                  extlay);
                              lsl("D", USERID, gi.getGroupOwner(),
                                  gi.getGroupCompany(), 0L, curdate, 
                                  extlay);
                           } else {
                              showmsg(101, true, COMPANY);
                              showmsg(101, true, USERID);
                           }
                           found = true;
                           continue;
                        }
                     }
                     
                     if (pinfo.hidx > 7) {
                        continue;
                     }
                     
                     if      (pinfo.hier[6-1].equals(COMPANY)) {
                     
                        if (pinfo.hidx == 6 && directory) {
                           String path = File.separator + GROUPS + 
                              File.separator + pinfo.hier[2-1] + 
                              File.separator + pinfo.hier[3-1] +
                              File.separator + PROPERTIES + 
                              File.separator + pinfo.hier[5-1];
                           if ((todo.size() > 1 || lastp != null) && 
                               (lastp == null || !lastp.equals(path))) {
                              lastp = path;
                              showmsg(101, true, "\n" + lastp + ":\n");
                           }
                        
                           if (longlist) {
                              lsl("D", COMPANY, gi.getGroupOwner(),
                                  gi.getGroupCompany(), 0L, curdate, 
                                  extlay);
                           } else {
                              showmsg(101, true, COMPANY);
                           }
                           found = true;
                           continue;
                        }
                           
                        if (pinfo.hidx == 6 || 
                            pinfo.hier[7-1].equals(gi.getGroupCompany())) {
                            
                           String path = File.separator + GROUPS + 
                              File.separator + pinfo.hier[2-1] + 
                              File.separator + pinfo.hier[3-1] +
                              File.separator + PROPERTIES + 
                              File.separator + pinfo.hier[5-1] +
                              File.separator + pinfo.hier[6-1];
                           if ((todo.size() > 1 || lastp != null) && 
                               (lastp == null || !lastp.equals(path))) {
                              lastp = path;
                              showmsg(101, true, "\n" + lastp + ":\n");
                           }
                            
                           if (longlist) {
                              lsl("D", gi.getGroupCompany(), 
                                  gi.getGroupOwner(), gi.getGroupCompany(), 
                                  0L, curdate, 
                                  extlay);
                           } else {
                              showmsg(101, true, gi.getGroupCompany());
                           }
                           found = true;
                        } 
                     }
                     else if (pinfo.hier[6-1].equals(USERID)) {
                        if (pinfo.hidx == 6 && directory) {
                           String path = File.separator + GROUPS + 
                              File.separator + pinfo.hier[2-1] + 
                              File.separator + pinfo.hier[3-1] +
                              File.separator + PROPERTIES + 
                              File.separator + pinfo.hier[5-1];
                           if ((todo.size() > 1 || lastp != null) && 
                               (lastp == null || !lastp.equals(path))) {
                              lastp = path;
                              showmsg(101, true, "\n" + lastp + ":\n");
                           }
                        
                           if (longlist) {
                              lsl("D", USERID, gi.getGroupOwner(),
                                  gi.getGroupCompany(), 0L, curdate, 
                                  extlay);
                           } else {
                              showmsg(101, true, USERID);
                           }
                           found = true;
                           continue;
                        }
                           
                        if (pinfo.hidx == 6 || 
                            pinfo.hier[7-1].equals(gi.getGroupOwner())) {
                            
                           String path = File.separator + GROUPS + 
                              File.separator + pinfo.hier[2-1] + 
                              File.separator + pinfo.hier[3-1] +
                              File.separator + PROPERTIES + 
                              File.separator + pinfo.hier[5-1] +
                              File.separator + pinfo.hier[6-1];
                           if ((todo.size() > 1 || lastp != null) && 
                               (lastp == null || !lastp.equals(path))) {
                              lastp = path;
                              showmsg(101, true, "\n" + lastp + ":\n");
                           }
                            
                           if (longlist) {
                              lsl("D", gi.getGroupOwner(), gi.getGroupOwner(),
                                  gi.getGroupCompany(), 0L, curdate, 
                                  extlay);
                           } else {
                              showmsg(101, true, gi.getGroupOwner());
                           }
                           found = true;
                        } 
                     }
                  }
               }
               
               if (!found) {
                  showerror(102, true, 
                            "Cannot ls specified location: " + pinfo.origpath);
               }
               
            } catch(Exception e) {
               if (printexception) SearchEtc.printStackTrace(e, System.out);
               showerror(102, true, 
                         "Cannot ls specified location: " + 
                         pinfo.origpath + ": " + e.getMessage());
            }
            
         }
        // In a toplevel (inbox, outbox, sandbox) dir
         else {
            try {
               
               boolean found = false;
               
              // If no package specified
               if (pinfo.pack == null) {
                  
                  String path = directory?File.separator:pinfo.path;
                  if ((todo.size() > 1 || lastp != null) && 
                      (lastp == null || !lastp.equals(path))) {
                     lastp = path;
                     showmsg(101, true, "\n" + lastp + ":\n");
                  }
                  
                  if (directory) {                   
                     if (longlist) {
                        lsl("D", pinfo.topdir, who, company, 0L, curdate, 
                            extlay);
                     } else {
                        showmsg(101, true, pinfo.topdir);
                     }
                     continue;
                  }
                  found = true;               
               }
               
               timer.delta("about to listInOutSand");
               Vector v = listInOutSandBox(pinfo.topdirnum, pinfo.pack);
               timer.delta("back");
               Enumeration enum = v.elements();
               while(enum.hasMoreElements()) {
                  PackageInfo pi = (PackageInfo)enum.nextElement();
                  
                 // If just listing the package
                  if (pinfo.pack == null) {
                     found = true;
                     if (longlist) {
                     
                        Date thedate = 
                           new Date(expiration ? pi.getPackageExpiration()
                                               : pi.getPackageCreation());
                                               
                        lsl("D", pi.getPackageName(), pi.getPackageOwner(), 
                            pi.getPackageCompany(), pi.getPackageSize(), 
                            thedate, extlay, pi.getPackageCompleted(),
                            pi.getPackageMarked(), pi.getPackageHidden());
                     } else {
                        showmsg(101, true, pi.getPackageName());
                     }
                  } 
                  
                 // Look for the a specific package we are listing
                  else if (pi.getPackageName().equals(pinfo.pack)) {
                     
                    // Print uniq containing directory
                     String path = pinfo.path;
                     if (directory ||
                         (pinfo.file != null && !pinfo.isAccess)) {
                        path = 
                           path.substring(0, path.lastIndexOf(File.separator));
                     }
                     
                     if ((todo.size() > 1 || lastp != null) && 
                         (lastp == null || !lastp.equals(path))) {
                        lastp = path;
                        showmsg(101, true, "\n" + lastp + ":\n");
                     }
                     
                    // If listing ACCESS dir
                     if (pinfo.isAccess) {
                        
                       // If listing Access dir
                        if (pinfo.file == null) {
                           found = true;
                           
                          // If -d, then we are done
                           if (directory) {
                              if (longlist) {
                                 lsl("D", ACCESS, pi.getPackageOwner(),
                                     pi.getPackageCompany(), 0L, curdate,
                                     extlay);
                              } else {
                                 showmsg(101, true, ACCESS);
                              }
                              break;  // done
                           }
                        }
                        
                        Vector avec = queryAcls(pi.getPackageId());
                        Enumeration enumacl = avec.elements();
                        while(enumacl.hasMoreElements()) {
                           AclInfo ai = (AclInfo)enumacl.nextElement();
                           
                          // If no acl or we have the specific one
                           if (pinfo.file == null ||
                               ai.getAclName().equals(pinfo.file)) {
                              found = true;
                              if (longlist) {
                                 lsl("A", ai.getAclName(), "-", 
                                     ai.getAclCompany(), 0L, curdate, extlay);
                              } else {
                                 showmsg(101, true, ai.getAclName());
                              }
                           }
                        }
                     } else {
                        
                       // If listing the package alone
                        if (pinfo.file == null && directory) {
                           found = true;
                           if (longlist) {
                              Date thedate = 
                                 new Date(expiration 
                                          ? pi.getPackageExpiration()
                                          : pi.getPackageCreation());
                           
                              lsl("D", pi.getPackageName(), 
                                  pi.getPackageOwner(), 
                                  pi.getPackageCompany(), pi.getPackageSize(), 
                                  thedate, extlay,
                                  pi.getPackageCompleted(),
                                  pi.getPackageMarked(), 
                                  pi.getPackageHidden());
                           } else {
                              showmsg(101, true, pi.getPackageName());
                           }
                           break;
                           
                        } 
                        
                       // Listing package contents
                        else {
                           
                           if (pinfo.file == null) {
                              
                             // Sandbox and Outbox get #ACCESS#
                              if (pinfo.topdirnum != INBOX_N) {
                                 if (longlist) {
                                    lsl("D", ACCESS, pi.getPackageOwner(), 
                                        pi.getPackageCompany(), 0L, 
                                        curdate, extlay);
                                 } else {
                                    showmsg(101, true, ACCESS);
                                 }
                              }
                              
                              found = true;
                           }
                           
                           Vector cvec = 
                              listPackageContents(pi.getPackageId());
                           Enumeration enumc = cvec.elements();
                           while(enumc.hasMoreElements()) {
                              FileInfo fi = (FileInfo)enumc.nextElement();
                              if (pinfo.file == null || 
                                  fi.getFileName().equals(pinfo.file)) {
                                  
                                 found = true;
                                 
                                 boolean fcomp = 
                                    fi.getFileStatus() == 
                                    DropboxGenerator.STATUS_COMPLETE;

                                 if (longlist) {
                                    long sss = fi.getFileCreation();
                                    if (sss <= 0 || expiration) {
                                       sss = pi.getPackageExpiration();
                                    }
                                    Date thedate = new Date(sss);
                                    if (md5) {
                                       lsl(fcomp?"M":"m", fi.getFileName(), 
                                           fi.getFileMD5(), 
                                           fi.getFileSize(), 
                                           thedate,
                                           extlay); 
                                    } else {
                                       lsl(fcomp?"F":"f", fi.getFileName(), 
                                           pi.getPackageOwner(), 
                                           pi.getPackageCompany(), 
                                           fi.getFileSize(), 
                                           thedate,
                                           extlay);
                                    }
                                 } else {
                                    if (md5) {
                                       showmsg(101, true, 
                                          pad(fi.getFileName(), 30, true) + 
                                          " " + 
                                          fi.getFileMD5());
                                    } else {
                                       showmsg(101, true, fi.getFileName());
                                    }
                                 }
                                 if (pinfo.file != null) { 
                                    break;
                                 }
                              }
                           }
                        }
                     }
                     break;
                  }
               }
               timer.delta("Finished with pass");
               if (!found) {
                  showerror(102, true, 
                            "Cannot ls specified location: " + pinfo.origpath);
               }
            } catch(Exception e) {
               if (printexception) SearchEtc.printStackTrace(e, System.out);
               showerror(102, true, 
                         "Cannot ls specified location: " + pinfo.origpath
                         + ": " + e.getMessage());
            }
         }
         timer.elapsed("interation of ls complete");
      }
   }
   
   void cmd_lls(Command c, String arr[], int numused) {
      
      
      boolean longlist   = false;
      boolean extlay     = false;
      boolean directory  = false;
      boolean md5        = false;
      boolean opt        = false;
      boolean expiration = false;
      
      if (numused > 1 && arr[1].startsWith("-")) {
      
         if (arr[1].length() == 1) {
            showerror(501, true, 
                      "Command '" + arr[0] + "' invalid option '-"+arr[1]+"'");
            return;
         }
         
         opt = true;
         for(int i=1; i < arr[1].length(); i++) {
            char ch = arr[1].charAt(i);
            if (ch == 'd') {
               directory = true;
            } else if (ch == 'l') {
               longlist = true;
            } else if (ch == 'l') {
               longlist = true;
               extlay   = true;
            } else if (ch == 'm') {
               md5 = true;
            } else if (ch == 'e') {
               expiration = true;
            } else if (ch == '-') {
               ;
            } else {
               showerror(501, true, 
                         "Command '" + arr[0] + "' invalid option '-"+ch+"'");
               return;
            }
         }
      }
      
      Vector files = new Vector();
               
      for(int i=opt?2:1; i < numused; i++) {
         fileWild(arr[i], files);
      }
      
      if (( opt && numused == 2) || (!opt && numused == 1)) { 
         files.addElement(lpwd);
      }
      
      if (files.size() == 0) {
         showerror(503, true, "No matching files to list");
         return;
      }
      
      sortVector(files, true, true);
      
      String lastp = null;
      Enumeration filenum = files.elements();
      while(filenum.hasMoreElements()) {
         String lfile = (String)filenum.nextElement();
         
         File f = makelocalfile(lfile);
         
         if (f.exists()) {
            if (f.isFile() || (f.isDirectory() && directory)) {
               if (files.size() > 1 && (lastp == null || 
                                        !lastp.equals(f.getParent()))) {
                  lastp = f.getParent();
                  if (lastp == null) lastp = File.separator;
                  showmsg(101, true, "\n" + lastp + ":\n");
               }
               showfile(f, longlist, md5, "", extlay);
            } else if (f.isDirectory()) {
            
              //
              // TODO!
              //  
              //   This is fine, except for order. The lastp: stuff bops
              //    between dirs and files. If doing /tmp/*, Files in tmp
              //    will be interspersed with the dirs based on sorted order.
              //    Need to defer the dirs that are to be expanded until after
              //    all files are listed for that parent dir.
              //
               lastp = f.getAbsolutePath();
               
               showmsg(101, true, "\n" + lastp + ":\n");
               
               String list[] = f.list();
               if (list != null) {
                  Vector v = new Vector();
                  for(int i=0; i < list.length; i++) {
                     v.addElement(list[i]);
                  }
                  sortVector(v, true, true);
                  Enumeration venum = v.elements();
                  while(venum.hasMoreElements()) {
                     String s = (String)venum.nextElement();
                     f = new File(lastp + File.separator + s);
                     showfile(f, longlist, md5, "", extlay); 
                  }
               }
            } else {
               if (files.size() > 1 && (lastp == null || 
                                        !lastp.equals(f.getParent()))) {
                  lastp = f.getParent();
                  if (lastp == null) lastp = File.separator;
                  showmsg(101, true, "\n" + lastp + ":\n");
               }
               showfile(f, longlist, md5, "", extlay);
            }
         } else {
            showerror(102, true, 
                      "lls: file/directory does not exist: '" + lfile + "'");
         }
      }
   }
   
   void cmd_mkdir(Command c, String arr[], int numused) {
   
      if (numused < 2) {
         showerror(501, true, 
                   "Command '" + arr[0] + "' takes at least 1 parameter");
         return;
      }
   
      Vector todo = new Vector();
      for(int i=1; i < numused; i++) {
         todo.addElement(new PathInfo(arr[i]));
      }
      
     // Remove last element for search, add back on at end
      todo = packageWild(todo, true);
      
      Enumeration pathenum = todo.elements();
      while(pathenum.hasMoreElements()) {
      
         PathInfo pinfo = (PathInfo)pathenum.nextElement();
         
         try {
           // If doing groups things
            if (pinfo.isValid && pinfo.topdirnum == GROUPS_N) {
               
               if (pinfo.hidx < 3 || pinfo.hidx == 4 || pinfo.hidx > 6) { 
                  showerror(102, true, 
                            "Cannot mkdir : " + pinfo.origpath);
                  continue;
               }
               
              // We know we have at least hidx of 2 at this point
               boolean isAll = pinfo.hier[2-1].equals(ALL);
               boolean isMod = pinfo.hier[2-1].equals(MODIFIABLE);
               boolean isOwn = pinfo.hier[2-1].equals(OWNED); 
               
               if (!isAll && !isMod && !isOwn) {
                  showerror(102, true, 
                            "Cannot mkdir : " + pinfo.origpath);
                  continue;
               }
               
               if (pinfo.hidx == 3) {
                  addNewGroup(pinfo.hier[3-1]);
                  continue;
               }
               
               Map  groups = listGroups();
               Enumeration enum  = null;
               
              // If we have a particular group in mind here, simply look it
              //  up, and set the enum to contain just that group
               GroupInfo gi = (GroupInfo)groups.get(pinfo.hier[3-1]);
               if (gi == null) {
                  showerror(102, true, 
                            "Cannot mkdir: " + pinfo.origpath);
                  continue;
               }
               
               boolean addit  = false;
               boolean canMod = 
                  gi.getGroupOwner().equals(who) ||
                  gi.getGroupAccess().contains(who);
               
              // Check that this group should showup in specified hier[2-1]
               if (isAll) {
                 // We know its visible since it was returned from DboxSrv
                  addit = true;
               } else if (isOwn) {
                  if (gi.getGroupOwner().equals(who)) {
                     addit=true;
                  }
               } else if (isMod) {
                  if (canMod) {
                     addit=true;
                  }
               }
               
               if (!addit) {
                  showerror(102, true, 
                            "Cannot mkdir: " + pinfo.origpath);
                  continue;
               }
               
              // Only allowed to if we have access
               boolean canList = gi.getGroupAccessValid();
               
               if (pinfo.hier[4-1].equals(MEMBERS)) {
                  if (canList && pinfo.hidx == 5) {
                     addGroupMemberAccess(pinfo.hier[3-1], pinfo.hier[5-1], true);
                     continue;
                  }
               } else if (pinfo.hier[4-1].equals(PROPERTIES)) {
                  
                  if (pinfo.hidx == 5) {
                     showerror(102, true, 
                               "Cannot mkdir: " + pinfo.origpath);
                     continue;
                  }
                  
                  if (pinfo.hier[5-1].equals(GACCESS)) {
                     if (canMod && pinfo.hidx == 6) {
                        addGroupMemberAccess(pinfo.hier[3-1], 
                                             pinfo.hier[6-1],
                                             false);
                        continue;
                     }
                  } else if (pinfo.hier[5-1].equals(LISTABILITY)) {
                     if (canMod && pinfo.hidx == 6) {
                        setGroupAttrib(pinfo.hier[3-1], pinfo.hier[6-1], false);
                        continue;
                     }
                  } else if (pinfo.hier[5-1].equals(VISIBILITY)) {
                     if (canMod && pinfo.hidx == 6) {
                        setGroupAttrib(pinfo.hier[3-1], pinfo.hier[6-1], true);
                        continue;
                     }
                  }
               }
               
               showerror(102, true, "Cannot mkdir: " + pinfo.path);
               
              // End of GROUP mkdir
               
               continue;
            }
         
           // Mkdir is valid to create a package, commit a package or add ACL
            if (!pinfo.isValid                           ||
                pinfo.pack == null                       ||
                (pinfo.isAccess  && pinfo.file == null)  ||
                (!pinfo.isAccess && (
                   pinfo.topdirnum != SANDBOX_N          ||
                   (pinfo.file != null            && 
                    !pinfo.file.equals(DONE))))          ||
                pinfo.topdirnum == INBOX_N) {
               
               showerror(102, true, "Cannot create the specified entity: " + 
                         pinfo.path);
               continue;
            }
         
           // If Access OR file (we know its DONE)
            if (pinfo.isAccess || pinfo.file != null) {
               Vector v = listInOutSandBox(pinfo.topdirnum, pinfo.pack);
               Enumeration enum = v.elements();
               boolean found = false;
               while(enum.hasMoreElements()) {
                  PackageInfo pi = (PackageInfo)enum.nextElement();
                  
                  if (pinfo.pack.equals(pi.getPackageName())) {
                  
                    // If its access, (and its not DONE)
                     if (pinfo.isAccess && 
                         !pinfo.hier[pinfo.hidx-1].equals(DONE)) {
                        String upg="user";
                        if (pinfo.file.indexOf("_P_") == 0) upg = "project";
                        else if (pinfo.file.indexOf("_G_") == 0) upg = "group";
                  
                        addAcl(pi.getPackageId(), pinfo.file);
                        showmsg(101, true, "Access added for " + upg + " " + 
                                           pinfo.file + " to Package " + 
                                           pi.getPackageName());
                                           
                        doAclCompanyCheck(pi.getPackageId());
                        
                     } else {
                     
                       // Asking for DONE
                        commitPackage(pi.getPackageId());
                        showmsg(101, true, "Package " + pi.getPackageName() + 
                                           " committed");
                     }
                     found = true;
                     break;
                  }
               }
               
               if (!found) {
                  showerror(102, true, "Invalid Package : " + pinfo.pack);
               }
               
            } else {
               if (pinfo.pack.equals(DONE) || pinfo.pack.equals(ACCESS)) {
                  throw new 
                     Exception("Can't create Done or Access as package");
               }
               createPackage(pinfo.pack);
               showmsg(101, true, 
                       "Package created[" + pinfo.pack + "]");
            }
         } catch(Exception ee) {
            if (printexception) SearchEtc.printStackTrace(ee, System.out);
            showerror(102, true, 
                      "Cannot create the specified entity: " + 
                      pinfo.path + ": " + ee.getMessage());
         }
      }
   }
   
   void cmd_addRmAcl(Command c, String arr[], int numused, boolean addRemove) {
   
      if (numused < 2) {
         showerror(501, true, 
                   "Command '" + arr[0] + "' takes at least 1 parameter");
         return;
      }
      
      Vector todo = new Vector();
      for(int i=1; i < numused; i++) {
         todo.addElement(new PathInfo(arr[i]));
      }
      
     // Remove last element for search, add back on at end
      todo = packageWild(todo, true);
      
      if (todo.size() < 1) {
         showerror(501, true, "Invalid number of paramaters");
         return;
      }
      
      Enumeration pathenum = todo.elements();
      while(pathenum.hasMoreElements()) {
      
         PathInfo pinfo = (PathInfo)pathenum.nextElement();
         
        // Valid to add/remove ACL
         if (!pinfo.isValid                           ||
             pinfo.pack == null                       ||
             pinfo.file == null                       ||
             pinfo.topdirnum == INBOX_N               ||
             pinfo.topdirnum == GROUPS_N) {
            
            showerror(102, true, 
                      "Cannot add/remove acl for the specified entity: " + 
                      pinfo.path);
            continue;
         }
         
         try {
            Vector v = listInOutSandBox(pinfo.topdirnum, pinfo.pack);
            Enumeration enum = v.elements();
            boolean found = false;
            while(enum.hasMoreElements()) {
               PackageInfo pi = (PackageInfo)enum.nextElement();
               
               if (pinfo.pack.equals(pi.getPackageName())) {
                  if (addRemove) {
                     addAcl(pi.getPackageId(), pinfo.file);
                  } else {
                     removeAcl(pi.getPackageId(), pinfo.file);
                  }
                  
                  String upg="user";
                  if (pinfo.file.indexOf("_P_") == 0) upg = "project";
                  else if (pinfo.file.indexOf("_G_") == 0) upg = "group";
                  
                  showmsg(101, true, "Access " + 
                                     (addRemove?"added":"removed") + 
                                     " for " + upg + " " +
                                     pinfo.file + ", Package " + 
                                     pi.getPackageName());
                                     
                  doAclCompanyCheck(pi.getPackageId());
                                     
                  found = true;
                  break;
               }
            }
            if (!found) {
               showerror(102, true, "Invalid Package : " + pinfo.pack);
            }
         } catch(Exception dbe) {
            if (printexception) SearchEtc.printStackTrace(dbe, System.out);
            if (addRemove) {
               showerror(102, true, 
                         "Error adding acl '" + pinfo.file + 
                         "' to package '" + pinfo.pack + "': " + 
                         dbe.getMessage());
            } else {
               showerror(102, true, 
                         "Error removing acl '" + pinfo.file + 
                         "' from package '" + pinfo.pack + "': " + 
                         dbe.getMessage());
            }
         }
      }
   }
   
   void cmd_addRmGrp(Command c, String arr[], int numused, boolean addRemove) {
   
      if (numused < 2) {
         showerror(501, true, 
                   "Command '" + arr[0] + "' takes at least 1 parameter");
         return;
      }
      
      Vector todo = new Vector();
      for(int i=1; i < numused; i++) {
         todo.addElement(new PathInfo(arr[i]));
      }
      
     // Remove last element for search, add back on at end
      todo = packageWild(todo, true);
      
      if (todo.size() < 1) {
         showerror(501, true, "Invalid number of paramaters");
         return;
      }
      
      Enumeration pathenum = todo.elements();
      while(pathenum.hasMoreElements()) {
      
         PathInfo pinfo = (PathInfo)pathenum.nextElement();
         
        // Valid to add/remove ACL
         if (!pinfo.isValid                           ||
             pinfo.pack == null                       ||
             pinfo.file == null                       ||
             pinfo.topdirnum == INBOX_N               ||
             pinfo.topdirnum == GROUPS_N) {
            
            showerror(102, true, 
                      "Cannot add/remove group for the specified entity: " + 
                      pinfo.path);
            continue;
         }
         
         try {
            Vector v = listInOutSandBox(pinfo.topdirnum, pinfo.pack);
            Enumeration enum = v.elements();
            boolean found = false;
            while(enum.hasMoreElements()) {
               PackageInfo pi = (PackageInfo)enum.nextElement();
               
               if (pinfo.pack.equals(pi.getPackageName())) {
               
                  String upg="group";
                  String s = pinfo.file;
                  if (s.indexOf("_P_") == 0) upg = "project";
                  else if (s.indexOf("_G_") == 0) upg = "group";
                  else s = "_G_" + s;
               
                  if (addRemove) {
                     addAcl(pi.getPackageId(), s);
                  } else {
                     removeAcl(pi.getPackageId(), s);
                  }
                  
                  showmsg(101, true, "Access " + 
                                     (addRemove?"added":"removed") + 
                                     " for " + upg + " " +
                                     pinfo.file + ", Package " + 
                                     pi.getPackageName());
                                     
                  doAclCompanyCheck(pi.getPackageId());
                                     
                  found = true;
                  break;
               }
            }
            if (!found) {
               showerror(102, true, "Invalid Package : " + pinfo.pack);
            }
         } catch(Exception dbe) {
            if (printexception) SearchEtc.printStackTrace(dbe, System.out);
            if (addRemove) {
               showerror(102, true, 
                         "Error adding acl '" + pinfo.file + 
                         "' to package '" + pinfo.pack + "': " + 
                         dbe.getMessage());
            } else {
               showerror(102, true, 
                         "Error removing acl '" + pinfo.file + 
                         "' from package '" + pinfo.pack + "': " + 
                         dbe.getMessage());
            }
         }
      }
   }
   
   void cmd_commit(Command c, String arr[], int numused) {
   
      Vector todo = new Vector();
      for(int i=1; i < numused; i++) {
         todo.addElement(new PathInfo(arr[i]));
      }
      
      if (todo.size() == 0) {
         todo.addElement(new PathInfo(pwd));
      }
      
      todo = packageWild(todo);
      
      Enumeration pathenum = todo.elements();
      while(pathenum.hasMoreElements()) {
      
         PathInfo pinfo = (PathInfo)pathenum.nextElement();
         
         if (!pinfo.isValid                           ||
             pinfo.pack == null                       ||
             pinfo.topdirnum != SANDBOX_N) {
            
            showerror(102, true, 
                      "Cannot commit the specified entity: " + 
                      pinfo.path);
            continue;
         }
         
         try {
            Vector v = listInOutSandBox(pinfo.topdirnum, pinfo.pack);
            Enumeration enum = v.elements();
            boolean found = false;
            while(enum.hasMoreElements()) {
               PackageInfo pi = (PackageInfo)enum.nextElement();
                  
               if (pinfo.pack.equals(pi.getPackageName())) {
                  commitPackage(pi.getPackageId());
                  showmsg(101, true, "Package commited: " + 
                                     pi.getPackageName());
                  found = true;
                  break;
               }
               
            }
            
            if (!found) {
               showerror(102, true, "Invalid Package : " + pinfo.pack);
            }
         } catch(Exception ee) {
            if (printexception) SearchEtc.printStackTrace(ee, System.out);
            showerror(102, true, "Cannot commit the specified entity: " + 
                      pinfo.path + ": " + ee.getMessage());
         }
      }
   }
   
   void cmd_compress(Command c, String arr[], int numused) {
   
      if (numused > 2) {
         showerror(501, true, "Command '" + arr[0] + "' takes 0-1 parameters");
         return;
      }
      
      DebugPrint.printlnd(DebugPrint.WARN, "Compression not currently supported");
      
     /*
      SessionManager sessionMgr = tunnel.getSessionManager();
      
      if (numused == 1) {
         boolean v = sessionMgr.getCompression();
         v = !v;
         sessionMgr.setCompression(v);
         sessionMgr.setRemoteCompression(v);
      } else if (arr[1].equalsIgnoreCase("true") ||
                 arr[1].equalsIgnoreCase("on")) {
         sessionMgr.setCompression(true);
         sessionMgr.setRemoteCompression(true);         
      } else if (arr[1].equalsIgnoreCase("false") ||
                 arr[1].equalsIgnoreCase("off")) {
         sessionMgr.setCompression(false);
         sessionMgr.setRemoteCompression(false);      
      } else if (arr[1].equalsIgnoreCase("stats") ||
                 arr[1].equalsIgnoreCase("statistics")  ||      
                 arr[1].equalsIgnoreCase("stat")) {
         
         SMStats lastStats = sessionMgr.getSinceLastStats();
         SMStats totStats  = sessionMgr.getTotalStats();
         
         long elap = (new Date()).getTime();
         elap     -= lastStats.getResetDate().getTime();
         if (elap <= 0) elap = 1;
         
         showmsg(101, true, "Time elapsed since last query: " + 
                 lastStats.msToTime(elap));
         
         long tot, utot, percnt;
         
         tot  = totStats.getTotOut();
         utot = totStats.getTotUncompressedOut();
         percnt = utot > 0?(100-(tot*100)/utot):0;
         
         showmsg(101, true, "Total tunnel bytes Uploaded " + 
                 tot + " (compression " + percnt + "%)");
                 
         tot  = totStats.getTotIn();
         utot = totStats.getTotUncompressedIn();
         percnt = utot > 0?(100-(tot*100)/utot):0;
         
         showmsg(101, true, "Total tunnel bytes Download " + 
                 tot + " (compression " + percnt + "%)");
         
         tot  = lastStats.getTotOut();
         utot = lastStats.getTotUncompressedOut();
         percnt = utot > 0?(100-(tot*100)/utot):0;
                 
         showmsg(101, true, "Since Last Query tunnel bytes Uploaded " + 
                 tot + " (compression " + percnt + "%)");
                 
         tot  = lastStats.getTotIn();
         utot = lastStats.getTotUncompressedIn();
         percnt = utot > 0?(100-(tot*100)/utot):0;
         
         showmsg(101, true, "Since Last Query tunnel bytes Download " + 
                 tot + " (compression " + percnt + "%)");
         
         sessionMgr.resetSinceLastStats();
         return;
      } else {
         showerror(501, true, "Command '" + arr[0] + 
                   "' takes a true/false, on/off, or stats parameter");
         return;
      }      
      
      showmsg(101, true, "Compression is now " + 
              (sessionMgr.getCompression()?"ON":"OFF"));
     */  
   }
   
   
  // Specific commands from user
   void cmd_expire(Command c, String arr[], int numused) {
      if (numused < 2) {
         showerror(501, true, 
                   "Command '" + arr[0] + "' takes 1-n parameters");
         return;
      }
      
      Vector todo = new Vector();
      if (numused == 2) {
         todo.addElement(new PathInfo(pwd));
      } else {
         for(int i=1; i < numused-1; i++) {
            todo.addElement(new PathInfo(arr[i]));
         }
         
         todo = packageWild(todo);
         if (todo.size() == 0) {
            showerror(503, true, "no matches");
            return;
         }
      }
      
      int days = 0;
      try {
         days = Integer.parseInt(arr[numused-1]);
         
      } catch(NumberFormatException nfe) {
         showerror(501, true, 
                   "Days ahead must be an integer: " + arr[numused-1]);
         return;
      }
      
      Enumeration pathenum = todo.elements();
      while(pathenum.hasMoreElements()) {
         PathInfo pinfo = (PathInfo)pathenum.nextElement();
      
        // If not valid address, 
         if (!pinfo.isValid     || 
             pinfo.pack == null ||
             pinfo.topdirnum == GROUPS_N) {
            showerror(102, true, 
                      "Cannot change expiration on specified location: " + 
                      pinfo.origpath);
            continue;
         }
      
         try {
            Vector v = listInOutSandBox(pinfo.topdirnum, pinfo.pack);
            Enumeration enum = v.elements();
            boolean found = false;
            while(enum.hasMoreElements()) {
               PackageInfo pi = (PackageInfo)enum.nextElement();
               if (pi.getPackageName().equals(pinfo.pack)) {
                  found = true;
                  changeExpiration(pi.getPackageId(), days);
                  showmsg(101, true, 
                          "Package expiration changed [" +
                          pi.getPackageName() + "]");
                  break;
               }
            }
            if (!found) {
               showerror(102, true, 
                         "Specified package not found!: " + pinfo.origpath);
            }
         } catch(Exception e) { 
            if (printexception) SearchEtc.printStackTrace(e, System.out);
            showerror(102, true, 
                      "Error changing expiration on specified location: " + 
                      pinfo.origpath + ": " + e.getMessage());
         }
      }
   }
      
   void cmd_listacls(Command c, String arr[], int numused) {
   
      boolean docompanyexpand = false;
      
      Vector todo = new Vector();
      for(int i=1; i < numused; i++) {
         if (arr[i].equals("-c")) {
            docompanyexpand = true;
            continue;
         }
         todo.addElement(new PathInfo(arr[i]));
      }
      
      if (todo.size() == 0) {
         todo.addElement(new PathInfo(pwd));
      }
      
      todo = packageWild(todo);
      
      Enumeration pathenum = todo.elements();
      while(pathenum.hasMoreElements()) {
      
         PathInfo pinfo = (PathInfo)pathenum.nextElement();
         
         if (!pinfo.isValid                           ||
             pinfo.pack == null                       ||
             pinfo.topdirnum == INBOX_N               ||
             pinfo.topdirnum == GROUPS_N) {
            
            showerror(102, true, 
                      "Cannot listacls for the specified entity: " + 
                      pinfo.path);
            continue;
         }
         
         try {
            Vector v = listInOutSandBox(pinfo.topdirnum, pinfo.pack);
            Enumeration enum = v.elements();
            boolean found = false;
            while(enum.hasMoreElements()) {
               PackageInfo pi = (PackageInfo)enum.nextElement();
                  
               if (pinfo.pack.equals(pi.getPackageName())) {
                  Vector avec = queryAcls(pi.getPackageId());
                  Enumeration enumacl = avec.elements();
                  Date curdate = new Date();
                  while(enumacl.hasMoreElements()) {
                     AclInfo ai = (AclInfo)enumacl.nextElement();
                     
                     if (docompanyexpand) {
                        String lcompany = ai.getAclCompany();
                        String aclname = ai.getAclName();
                       /*
                        if (ai.getAclType() != DropboxAccess.STATUS_NONE) {
                           Vector lvec = new Vector();
                           ai.setAclName(aclname.substring(3));
                           lvec.add(ai);
                           lvec = dropbox.queryRepresentedCompanies(lvec, 
                                                                    pi.getPackageItar());
                           ai.setAclName(aclname);
                           Iterator it = lvec.iterator();
                           lcompany = "";
                           while(it.hasNext()) {
                              String s = (String)it.next();
                              if (lcompany.length() > 0) lcompany += "," + s;
                              else                       lcompany = s;
                           }
                           }*/
                        showmsg(101, true, aclname + "   (" + lcompany + ")");
                     } else {
                        lsl("A", ai.getAclName(), "-", 
                            ai.getAclCompany(), 0L, curdate, false);
                     }
                  }
                  
                  found = true;
                  break;
               }
            }
            
            if (!found) {
               showerror(102, true, "Invalid Package : " + pinfo.pack);
            }
         } catch(Exception ee) {
            if (printexception) SearchEtc.printStackTrace(ee, System.out);
            showerror(102, true, 
                      "Cannot listacls on the specified entity: " + 
                      pinfo.path + ": " + ee.getMessage());
         }
      }
   }
   
   void cmd_listopts(Command c, String arr[], int numused) {
   
      try {
         Vector v = new Vector();
         for(int i=1; i < numused; i++) {
            v.addElement(arr[i]);
         }
      
         Map h = listOptions(v);
         Iterator it = h.entrySet().iterator();
         while(it.hasNext()) {
            Map.Entry ent = (Map.Entry)it.next();
            String k   = (String)ent.getKey();
            String val = (String)ent.getValue();
            showmsg(100, true, k + "=" + val);
         }
      } catch(Exception ee) {
         if (printexception) SearchEtc.printStackTrace(ee, System.out);
         showerror(102, true, 
                   "Error listing options: " + ee.getMessage());
      }
   }
   
   void cmd_setopt(Command c, String arr[], int numused) {
   
      if (numused != 3) {
         showerror(501, true, 
                   "Command '" + arr[0] + "' takes 2 parameters");
         return;
      }
   
      try {
        // Treat storagepool option special
         if (arr[1].equalsIgnoreCase("StoragePool")) {
            PoolInfo p = getStoragePoolInstance(arr[2]);
            storagepool = p.getPoolId();
         } else if (arr[1].equalsIgnoreCase("ItarPackageCreate")) {
            boolean v = arr[2].equalsIgnoreCase("true");
            if (!v && !arr[2].equalsIgnoreCase("false")) {
               throw new DboxException("Value for ItarPackageCreate must be true or false");
            }
            itarpackage = v;
         } else {
            setOption(arr[1], arr[2]);
         }
      } catch(Exception ee) {
         if (printexception) SearchEtc.printStackTrace(ee, System.out);
         showerror(102, true, 
                   "Error setting option: " + ee.getMessage());
      }
   }
   
   void cmd_listpools(Command c, String arr[], int numused) {
   
      if (numused > 1) {
         showerror(501, true, 
                   "Command '" + arr[0] + "' takes no parameters");
         return;
      }
      
      try {
         Vector v = getStoragePools();
         Iterator it = v.iterator();
         while(it.hasNext()) {
            PoolInfo p = (PoolInfo)it.next();
            showmsg(101, true, " ID " + p.getPoolId() + 
                    " Name '" + p.getPoolName() + "'" +
                    " DefaultDays " + p.getPoolDefaultDays() +
                    " MaxDays " + p.getPoolMaxDays());
         }
      } catch(Exception ee) {
         if (printexception) SearchEtc.printStackTrace(ee, System.out);
         showerror(102, true, 
                   "Error listing storage pools(s): " + ee.getMessage());
      }
   }

   void cmd_listpkgopts(Command c, String arr[], int numused) {
   
      Vector todo = new Vector();
      for(int i=1; i < numused; i++) {
         todo.addElement(new PathInfo(arr[i]));
      }
      
      if (todo.size() == 0) {
         todo.addElement(new PathInfo(pwd));
      }
      
      todo = packageWild(todo);
      
      Enumeration pathenum = todo.elements();
      while(pathenum.hasMoreElements()) {
      
         PathInfo pinfo = (PathInfo)pathenum.nextElement();
         
         if (!pinfo.isValid                           ||
             pinfo.pack == null                       ||
             pinfo.topdirnum == INBOX_N               ||
             pinfo.topdirnum == GROUPS_N) {
            
            showerror(102, true, 
                      "Cannot listpkgopts for the specified entity: " + 
                      pinfo.path);
            continue;
         }
         
         try {
            Vector v = listInOutSandBox(pinfo.topdirnum, pinfo.pack);
            Enumeration enum = v.elements();
            boolean found = false;
            while(enum.hasMoreElements()) {
               PackageInfo pi = (PackageInfo)enum.nextElement();
                  
               if (pinfo.pack.equals(pi.getPackageName())) {
                  byte pkgflags = pi.getPackageFlags();
                  
                  showmsg(101, true, "ReturnReceipt    = " + 
                          ((PackageInfo.RETURNRECEIPT & pkgflags) != 0));
                  showmsg(101, true, "SendNotification = " + 
                          ((PackageInfo.SENDNOTIFY & pkgflags) != 0));
                  showmsg(101, true, "Hidden           = " + 
                          ((PackageInfo.HIDDEN & pkgflags) != 0));
                  showmsg(101, true, "StoragePool      = " + 
                          getStoragePoolInstance(
                             pi.getPackagePoolId()).getPoolName());
                  showmsg(101, true, "Itar             = " + 
                          ((PackageInfo.ITAR & pkgflags) != 0));
                  found = true;
                  break;
               }
            }
            
            if (!found) {
               showerror(102, true, "Invalid Package : " + pinfo.pack);
            }
         } catch(Exception ee) {
            if (printexception) SearchEtc.printStackTrace(ee, System.out);
            showerror(102, true, 
                      "Error listing option(s) for " + pinfo.path + ": "
                      + ee.getMessage());
         }
      }
   }
   
   void cmd_setpkgopt(Command c, String arr[], int numused) {
   
      if (numused != 3 && numused != 4) {
         showerror(501, true, 
                   "Command '" + arr[0] + "' takes 2-3 parameters");
         return;
      }
      
//      if (DropboxGenerator.getProtocolVersion() < 5) {
//         showerror(501, true, "Server does not support perpackage options");
//         return;
//      }
      
      int idx = 1;
      
      Vector todo = new Vector();
      if (numused == 4) {
         todo.addElement(new PathInfo(arr[idx++]));
      } else {
         todo.addElement(new PathInfo(pwd));
      }
      
      String opt = arr[idx++].toLowerCase();
      byte flagdelt = (byte)0;
      if        (opt.equals("return") || 
                 opt.equals("returnreceipt") ||
                 opt.equals("rr")) {
         flagdelt = PackageInfo.RETURNRECEIPT;
      } else if (opt.equals("notify") || 
                 opt.equals("sendnotification") ||
                 opt.equals("sendnotify") ||
                 opt.equals("notification")) {
         flagdelt = PackageInfo.SENDNOTIFY;
      } else if (opt.equals("hide") || 
                 opt.equals("hidden")) {
         flagdelt = PackageInfo.HIDDEN;
      } else {
         showerror(501, true, "Option name must be [ReturnReceipt,Return,RR, SendNotification, SendNotify, Notification, Notify, Hide, Hidden]");
         return;
      }
      
      String val = arr[idx++].toLowerCase();
      boolean boolval = true;
      if        (val.equals("true")  || val.equals("on")) {
         boolval = true;
      } else if (val.equals("false") || val.equals("off")) {
         boolval = false;
      } else {
         showerror(501, true, "Option values must be [true, false, on, off]");
         return;
      }
      
      todo = packageWild(todo);
      
      Enumeration pathenum = todo.elements();
      while(pathenum.hasMoreElements()) {
      
         PathInfo pinfo = (PathInfo)pathenum.nextElement();
         
         if (!pinfo.isValid                           ||
             pinfo.pack == null                       ||
             pinfo.topdirnum == INBOX_N               ||
             pinfo.topdirnum == GROUPS_N) {
            
            showerror(102, true, 
                      "Cannot setpkgopts for the specified entity: " + 
                      pinfo.path);
            continue;
         }
      
         try {
            Vector v = listInOutSandBox(pinfo.topdirnum, pinfo.pack);
            Enumeration enum = v.elements();
            boolean found = false;
            while(enum.hasMoreElements()) {
               PackageInfo pi = (PackageInfo)enum.nextElement();
               
               if (pinfo.pack.equals(pi.getPackageName())) {
                  found = true;
                  
                  setPackageOption(pi.getPackageId(), 
                                   flagdelt, boolval?flagdelt:0);
                  
                  break;
               }      
            }
            
            if (!found) {
               showerror(102, true, "Invalid Package : " + pinfo.pack);
            }
         } catch(Exception ee) {
            if (printexception) SearchEtc.printStackTrace(ee, System.out);
            showerror(102, true, 
                      "Error setting option: " + ee.getMessage());
         }
      }
   }
   
   void cmd_setpkgdesc(Command c, String arr[], int numused, boolean fromfile) {
   
      if (numused != 2 && numused != 3) {
         showerror(501, true, 
                   "Command '" + arr[0] + "' takes 1-2 parameters");
         return;
      }
      
      int idx = 1;
      
      Vector todo = new Vector();
      if (numused > 2) {
         todo.addElement(new PathInfo(arr[idx++]));
      } else {
         todo.addElement(new PathInfo(pwd));
      }
      
      String desc = arr[idx++];
      
      todo = packageWild(todo);
      
      Enumeration pathenum = todo.elements();
      while(pathenum.hasMoreElements()) {
      
         PathInfo pinfo = (PathInfo)pathenum.nextElement();
         
         if (!pinfo.isValid                           ||
             pinfo.pack == null                       ||
             pinfo.topdirnum == INBOX_N               ||
             pinfo.topdirnum == GROUPS_N) {
            
            showerror(102, true, 
                      "Cannot setpkgdesc for the specified entity: " + 
                      pinfo.path);
            continue;
         }
      
         try {
            Vector v = listInOutSandBox(pinfo.topdirnum, pinfo.pack);
            Enumeration enum = v.elements();
            boolean found = false;
            while(enum.hasMoreElements()) {
               PackageInfo pi = (PackageInfo)enum.nextElement();
               
               if (pinfo.pack.equals(pi.getPackageName())) {
                  found = true;
                  
                  if (fromfile) {
                     BufferedReader fis = null;
                     try {
                        StringBuffer ans = new StringBuffer();
                        String l;
                        fis = new BufferedReader(new FileReader(desc));
                        while((l = fis.readLine()) != null) {
                           ans.append(l).append("\n");
                        }
                        desc = ans.toString();
                     } finally {
                        try { 
                           if (fis != null) fis.close();
                        } catch(IOException ioee) {}
                     }
                  }
                  
                  setPackageDescription(pi.getPackageId(), 
                                        desc);
                  
                  break;
               }      
            }
            
            if (!found) {
               showerror(102, true, "Invalid Package : " + pinfo.pack);
            }
         } catch(Exception ee) {
            if (printexception) SearchEtc.printStackTrace(ee, System.out);
            showerror(102, true, 
                      "Error setting option: " + ee.getMessage());
         }
      }
   }
   
   void cmd_lmkdir(Command c, String arr[], int numused) {
      if (numused < 2) {
         showerror(501, true, 
                   "Command '" + arr[0] + "' takes at least 1 parameter");
         return;
      }      
      
      for(int i=1; i < numused; i++) {
         String lfile = arr[i];
         
         File f = makelocalfile(lfile);
         
         if (f.exists()) {
            showerror(102, true, 
                      lfile + ": File or directory already exists!");
            continue;
         }
         String pdir = f.getParent();
         
         if (pdir == null) {
            showerror(102, true, 
                      lfile + ": Cannot create root directory!");
            continue;
         }
         
         File pfile = new File(pdir);
         if (!pfile.exists() || !pfile.isDirectory()) {
            showerror(102, true, 
                      lfile + ": Parent does not exist or not directory!");
            continue;
         }
         
         if (!f.mkdir()) {
            showerror(102, true, lfile + ": Error making directory!");
            continue;
         }
         showmsg(101, true, 
                 "Created local directory [" + f.getAbsolutePath() + "]");
         
      }
   }
   
   void cmd_lookup(Command c, String arr[], int numused) {
      if (numused < 2) {
         showerror(501, true, 
                   "Command '" + arr[0] + "' takes at least 1 parameter");
         return;
      }      
      
      try {
         for(int i=1; i < numused; i++) {
            boolean wild = arr[i].indexOf('*') >=0 || arr[i].indexOf('?') >=0;
            Vector v = dropbox.lookupUser(arr[i], wild);
            Iterator it = v.iterator();
            while(it.hasNext()) {
               AclInfo aclinfo = (AclInfo)it.next();
               showmsg(101, true, aclinfo.getAclName() + "   (" + 
                       aclinfo.getAclCompany() + ")");
            }
         }
      } catch(Exception ee) {
         if (printexception) SearchEtc.printStackTrace(ee, System.out);
         showerror(102, true, 
                   "Error looking up user: " + ee.getMessage());
      }
   }
   
   void cmd_lrmdir(Command c, String arr[], int numused) {
      int startidx = 1;
      
      boolean recursive = false;
      if (numused > 1 && arr[1].startsWith("-")) {
      
         startidx = 2;
      
         if (arr[1].length() == 1) {
            showerror(501, true, 
                      "Command '" + arr[0] + "' invalid option '-'");
            return;
         }
         
         for(int i=1; i < arr[1].length(); i++) {
            char ch = arr[1].charAt(i);
            if        (ch == 'r') {
               recursive = true;
            } else if (ch == '-') {
               ;
            } else {
               showerror(501, true, 
                         "Command '" + arr[0] + "' invalid option '-"+ch+"'");
               return;
            }
         }
      }
   
      if (startidx >= numused) {
         showerror(501, true, 
                   "Command '" + arr[0] + "' takes at least 1 parameter");
         return;
      }      
      
      for(int i=startidx; i < numused; i++) {
         String lfile = arr[i];
         
         File f = makelocalfile(lfile);
         
         if (!f.exists()) {
            showerror(102, true, 
                      lfile + ": Directory does not exist!");
            continue;
         }
         
         if (!f.isDirectory()) {
            showerror(102, true, 
                      lfile + ": is not a directory!");
            continue;
         }
         
         String pdir = f.getParent();
         
         if (pdir == null) {
            showerror(102, true, 
                      lfile + ": Cannot delete root directory!");
            continue;
         }
         
         Vector todo = new Vector();
         if (recursive) {
            todo = recursiveFindFile(f, true);
         } else {
            todo.addElement(f);
         }
            
         Enumeration todoenum = todo.elements();
         while(todoenum.hasMoreElements()) {
               
            File inf = (File)todoenum.nextElement();
            String dirOrFile = inf.isDirectory()?"directory":"file";
            if (!inf.delete()) {
               showerror(102, true, inf.getAbsolutePath() +
                         ": Error removing " + dirOrFile + "!");
            } else {
               showmsg(101, true, 
                       "Removed local " + dirOrFile + 
                       " [" + inf.getAbsolutePath() + "]");
            }
         }
      }
   }
   
   void cmd_rmdir(Command c, String arr[], int numused) {
   
      if (numused < 2) {
         showerror(501, true, 
                   "Command '" + arr[0] + "' takes at least 1 parameter");
         return;
      }
   
      Vector todo = new Vector();
      for(int i=1; i < numused; i++) {
         todo.addElement(new PathInfo(arr[i]));
      }
      
      todo = packageWild(todo);
      
      Enumeration pathenum = todo.elements();
      while(pathenum.hasMoreElements()) {
      
         PathInfo pinfo = (PathInfo)pathenum.nextElement();
         
         try {
         
            if (pinfo.isValid && pinfo.topdirnum == GROUPS_N) {
            
              // Can rmdir a group(3), or a member(5)/access(6)
              // all others error
               if (pinfo.hidx < 3 || pinfo.hidx == 4 || pinfo.hidx > 6) { 
                  showerror(102, true, 
                            "Cannot rmdir : " + pinfo.origpath);
                  continue;
               }
               
              // We know we have at least hidx of 3 at this point
               boolean isAll = pinfo.hier[2-1].equals(ALL);
               boolean isMod = pinfo.hier[2-1].equals(MODIFIABLE);
               boolean isOwn = pinfo.hier[2-1].equals(OWNED); 
               
               if (!isAll && !isMod && !isOwn) {
                  showerror(102, true, 
                            "Cannot rmdir : " + pinfo.origpath);
                  continue;
               }
               
               if (pinfo.hidx == 3) {
                  removeGroup(pinfo.hier[3-1]);
                  continue;
               }
               
               Map  groups = listGroups();
               Enumeration enum  = null;
               
              // If we have a particular group in mind here, simply look it
              //  up, and set the enum to contain just that group
               GroupInfo gi = (GroupInfo)groups.get(pinfo.hier[3-1]);
               if (gi == null) {
                  showerror(102, true, 
                            "Cannot rmdir: " + pinfo.origpath);
                  continue;
               }
               
               boolean addit  = false;
               boolean canMod = 
                  gi.getGroupOwner().equals(who) ||
                  gi.getGroupAccess().contains(who);
               
              // Check that this group should showup in specified hier[2-1]
               if (isAll) {
                 // We know its visible since it was returned from DboxSrv
                  addit = true;
               } else if (isOwn) {
                  if (gi.getGroupOwner().equals(who)) {
                     addit=true;
                  }
               } else if (isMod) {
                  if (canMod) {
                     addit=true;
                  }
               }
               
               if (!addit) {
                  showerror(102, true, 
                            "Cannot rmdir: " + pinfo.origpath);
                  continue;
               }
               
              // Can only rmdir if in Access list
               boolean canList = gi.getGroupAccessValid();
               
               if (pinfo.hier[4-1].equals(MEMBERS)) {
                  if (canList && pinfo.hidx == 5) {
                     removeGroupMemberAccess(pinfo.hier[3-1], 
                                             pinfo.hier[5-1],
                                             true);
                     continue;
                  }
               } else if (pinfo.hier[4-1].equals(PROPERTIES)) {
                  
                  if (pinfo.hidx == 5) {
                     showerror(102, true, 
                               "Cannot rmdir: " + pinfo.origpath);
                     continue;
                  }
                  
                  if (pinfo.hier[5-1].equals(GACCESS)) {
                     if (canMod && pinfo.hidx == 6) {
                        removeGroupMemberAccess(pinfo.hier[3-1], 
                                                pinfo.hier[6-1],
                                                false);
                        continue;
                     }
                  }
               }
               
               showerror(102, true, "Cannot rmdir: " + pinfo.origpath);
               
              // End of GROUP rmdir
               
               continue;
            }
            
           // Rmdir is valid to delete a package or remove an ACL
            if (!pinfo.isValid                           ||
                pinfo.pack == null                       ||
                (pinfo.isAccess  && pinfo.file == null)  ||
                (!pinfo.isAccess && pinfo.file != null)) {
               
               showerror(102, true, 
                         "Cannot remove the specified entity: " + 
                         pinfo.path);
               continue;
            }
      
            Vector v = listInOutSandBox(pinfo.topdirnum, pinfo.pack);
            Enumeration enum = v.elements();
            boolean found = false;
            while(enum.hasMoreElements()) {
               PackageInfo pi = (PackageInfo)enum.nextElement();
               
               if (pinfo.pack.equals(pi.getPackageName())) {
                  found = true;
                  if (pinfo.isAccess) {
                     removeAcl(pi.getPackageId(), pinfo.file);
                     
                     String upg="user";
                     if (pinfo.file.indexOf("_P_") == 0) upg = "project";
                     else if (pinfo.file.indexOf("_G_") == 0) upg = "group";
                     
                     showmsg(101, true, "Access removed for " + upg + " " + 
                             pinfo.file + " from Package " + 
                             pi.getPackageName());
                             
                     doAclCompanyCheck(pi.getPackageId());
                             
                  } else {
                     deletePackage(pi.getPackageId());
                     showmsg(101, true, 
                             "Package deleted [" + pi.getPackageName() + "]");
                  }
                  break;
               }
            }
            
            if (!found) {
               showerror(102, true, "Invalid Package : " + pinfo.pack);
            }
            
         } catch(Exception ee) {
            if (printexception) SearchEtc.printStackTrace(ee, System.out);
            showerror(102, true, 
                      "Cannot remove the specified entity: " + 
                      pinfo.path + ": " + ee.getMessage());
         }
      }
   }
   
   
   void cmd_restart(Command c, String arr[], int numused) {
      
      if (numused > 2) {
         showerror(501, true, 
                   "Command '" + arr[0] + "' takes 0-1 parameters");
         return;
      }
      
      if (numused == 2) {
         if (arr[1].equalsIgnoreCase("true")) {
            restartXfer = true;
         } else if (arr[1].equalsIgnoreCase("false")) {
            restartXfer = false;
         } else {
            showerror(501, true, 
                      "Command '" + arr[0] + "' takes a boolean parm. [" + 
                      arr[1] + "] is invalid");
            return;
         }
      } else {
         restartXfer = !restartXfer;
      }
      
      showmsg(101, true, "Upload/Download Restart: " + restartXfer);
   }
   
   void cmd_put(Command c, String arr[], int numused, boolean wild) {
   
      if (wild) {
         if (numused < 2) {
            showerror(501, true, 
                      "Command '" + arr[0] + "' takes 1-n parameters");
            return;
         }
      } else {
         if (numused < 2 || numused > 3) {
            showerror(501, true, 
                      "Command '" + arr[0] + "' takes 1-2 parameters");
            return;
         }
      }
   
      Vector files = new Vector();
      
     // Search correct number of spots
      for(int i=1; i < (wild?numused:2); i++) {
         int numpre = files.size();
         files = fileWild(arr[i], files);
         if (numpre == files.size()) {
            showerror(102, true, "Invalid file for upload '" + arr[i] + "'");
         }
      }
      
      if (files.size() == 0) {
         showerror(503, true, "No matching files for upload");
         return;
      }
      
      if (!wild) {
         if (files.size() != 1) {
            showerror(503, true, "Too many matching files for upload");
            return;
         }
      }
      
      PathInfo destinfo = null;
      
      Enumeration filenum = files.elements();
      while(filenum.hasMoreElements()) {
         String lfile = (String)filenum.nextElement();
         
         File f = new File(lfile);
         
         PathInfo pinfo = null;
         try {
            
            if (!f.exists()) {
               showerror(102, true, 
                         "File for upload is not valid: " + lfile);
               continue;
            }
            
            if (f.isDirectory()) {
               ;
            } else if (!f.isFile()) {
               showerror(102, true, 
                         "File for upload is not valid: " + lfile);
               continue;
            }
            
            if (numused == 2 || wild) {
            
              // Build a destintation object if not already built
               if (destinfo == null) {
                  destinfo = new PathInfo(".");
                  if (destinfo.isValid        && 
                      destinfo.topdirnum == SANDBOX_N && 
                      destinfo.pack != null) {
                     if (destinfo.isAccess || destinfo.file != null) {
                        destinfo = 
                           new PathInfo("/" + SANDBOX + "/" + destinfo.pack);
                     }
                     
                     destinfo = autoCreatePackage(destinfo);
                     
                     if (destinfo == null) {
                        showerror(102, true, 
                                  "Error autoCreating destination");
                        continue;
                     }
                  } else {
                     destinfo = autoSelectPackage(f.getName());
                     if (destinfo == null) {
                        showerror(102, true, 
                                  "Error autoSelecting destination package");
                        continue;
                     }
                  }
               }
               
              // Copy from destination object
               pinfo = new PathInfo(destinfo);
               
            } else {
            
              // We only get in here once, as its the 'put file pack' variant
               Vector destv = new Vector();
               
               destinfo = new PathInfo(arr[2]);
               
               destv.addElement(destinfo);
               destv = packageWild(destv, true);
               
               if (!destinfo.isValid     || 
                   destinfo.pack == null ||
                   destinfo.file == null ||
                   destinfo.isAccess     ||
                   destinfo.topdirnum != SANDBOX_N) {
                  destinfo = new PathInfo("/" + SANDBOX + "/" + arr[2]);
                  destv.clear();
                  destv.addElement(destinfo);
                  destv = packageWild(destv, true);
               }
               
               if (destv.size() == 0) {
                  showerror(102, true, 
                            "Destination for upload is not valid: " + arr[2]);
                  continue;
               } else if (destv.size() != 1) {
                  showerror(102, true, 
                            "Too many matches for destination : " + arr[2]);
                  continue;
               }
               
               pinfo = destinfo = (PathInfo)destv.firstElement();
               
               destinfo = autoCreatePackage(destinfo);
               if (destinfo == null) {
                  showerror(102, true, 
                            "Error autoCreating destination : " + pinfo.path);
                  continue;
               }
               
               pinfo = new PathInfo(destinfo);
            } 
         
           // Have to expand directory to file list
            
            Vector todo = new Vector();
            boolean isdir = f.isDirectory();
            String startingFile  = pinfo.file;
            String removePath  = f.getPath();
            int sepidx = removePath.lastIndexOf(File.separator);
            
            if (isdir) {
               todo = recursiveFindFile(f, false);
               
            } else {
               todo.addElement(f);
               
              // If file not being renamed, set the name
               if (pinfo.file == null) pinfo.file = f.getName();
            }
            
            Enumeration todoenum = todo.elements();
            while(todoenum.hasMoreElements()) {
               
               File inf = (File)todoenum.nextElement();
               try {
               
                 // If prompting
                  if (wild && prompt) {
                     showmsg(197, true, "Upload " + (isdir?"(recurse) ":"") + 
                             inf.getPath() + " to package " + pinfo.pack + 
                             " [yes/no]? <yes> ");
                     
                     try {
                        String ans = input.readLine();
                        if (ans == null) return;
                        ans = ans.trim();
                        if (!ans.equalsIgnoreCase("yes") &&
                            !ans.equalsIgnoreCase("y")   &&
                            !ans.equals("")) {
                           continue;
                        }
                     } catch(IOException ioe) {
                        return;
                     }
                  }
                  
                  String uploadAsName = pinfo.file;
                  if (isdir) {
                     uploadAsName = inf.getPath().substring(sepidx+1);
                  }

                  Operation op = uploadFile(pinfo.miscLong,
                                            inf.getPath(), uploadAsName,
                                            restartXfer);
                  showmsg(125, true, "Uploading to " + uploadAsName);
                  handleOperation(arr[0], inf, op);
               
               } catch(IOException ioe) {
                  if (printexception) SearchEtc.printStackTrace(ioe, System.out);
                  showerror(102, true, "File/Directory to upload not found '"
                            + inf.getPath());
               } catch(AbortAllException aall) {
                  showerror(102, true, "Aborting ALL uploads in queue '");
                  return;
               } catch(DboxException dbe) {
                  if (printexception) SearchEtc.printStackTrace(dbe, System.out);
                  showerror(102, true, "Error uploading file/directory '" + 
                            inf.getPath() + "' to '" + pinfo.path + "': " + 
                            dbe.getMessage());
               } catch(Exception ee) {
                  if (printexception) SearchEtc.printStackTrace(ee, System.out);
                  showerror(102, true, "Error uploading file/directory '" + 
                            inf.getPath() + "' to '" + pinfo.path + "'");
               }
            }
         } catch(Exception ee) {
            if (printexception) SearchEtc.printStackTrace(ee, System.out);
            showerror(102, true, "Error uploading file/directory '" + 
                      lfile + "' to '" + pinfo.path + "'" + 
                      ": " + ee.getMessage());
         }
      }
   }
   
   void cmd_get(Command c, String arr[], int numused, boolean wild) {
      
      if (wild) {
         if (numused < 2) {
            showerror(501, true, 
                      "Command '" + arr[0] + "' takes 1-n parameters");
            return;
         }
      } else {
         if (numused < 2 || numused > 3) {
            showerror(501, true, 
                      "Command '" + arr[0] + "' takes 1-2 parameters");
            return;
         }
      }
   
      Vector todo = new Vector();
      for(int i=1; i < (wild?numused:2); i++) {
         todo.addElement(new PathInfo(arr[i]));
      }
      
      todo = packageWild(todo);
      
      if (todo.size() == 0) {
         showerror(503, true, "No matching files for download");
         return;
      }
      
      if (!wild) {
         if (todo.size() != 1) {
            showerror(503, true, "Too many matching files for download");
            return;
         }
      }
      
      Enumeration pathenum = todo.elements();
      while(pathenum.hasMoreElements()) {
      
         PathInfo pinfo = (PathInfo)pathenum.nextElement();
                           
        // get is valid for any file
         if (!pinfo.isValid                           ||
             pinfo.pack == null                       ||
             pinfo.file == null                       ||
             pinfo.topdirnum == GROUPS_N              ||
             pinfo.isAccess) {
            showerror(102, true, "Invalid file for download: " + 
                      pinfo.path);
            continue;
         }
         
         if (wild && prompt) {
            showmsg(197, true, "Download " + pinfo.file + 
                               " from package " + pinfo.pack + 
                               " [yes/no]? <y> ");
            try {
               String ans = input.readLine();
               if (ans == null) return;
               ans = ans.trim();
               if (!ans.equalsIgnoreCase("yes") &&
                   !ans.equalsIgnoreCase("y")   &&
                   !ans.equals("")) {
                  continue;
               }
            } catch(IOException ioe) {
               return;
            }
         }
         
         try {
         
            File f = null;
            if (numused == 2 || wild) {
               f = makelocalfile(pinfo.file);
            } else {
            
               f = makelocalfile(arr[2]);
               String n = f.getName();
               if (n.indexOf("*") >= 0) {
                  Vector files = new Vector();
                  String p = f.getParent();
                  f = new File(p);
                  String list[] = f.list();
                  if (list != null) {
                     if (!p.endsWith(File.separator)) p += File.separator;
                     getMatchingWilds(n, p, list, files); 
                     if (files.size() != 1) {
                        showerror(102, true, 
                                  "Invalid number of matched for destination");
                        continue;
                     }
                     f = makelocalfile((String)files.firstElement());
                  } else {
                     showerror(102, true,
                               "Invalid destination directory '" + p + "'");
                     continue;
                  }
               }
               
              // If a a directory is named, then make it a file
               if (f.exists() && f.isDirectory()) {
                  f = new File(f.getAbsolutePath() + 
                               File.separator + pinfo.file);
               }
            }
            
            
           // make all directories
            String parentDir = f.getParent();
            if (parentDir != null) {
               File dirFile = new File(parentDir);
               if (!dirFile.exists()) {
                  if (dirFile.mkdirs()) {
                     showmsg(100, true, "Directories created: " + parentDir);
                  } else {
                     showerror(102, true, "Error creating directories: " + 
                               parentDir);
                     showerror(102, true, "Download not possible: " + 
                               pinfo.file);
                     continue;
                  }
               } else if (!dirFile.isDirectory()) {
                  showerror(102, true, 
                            "Target for download is not a directory: " +
                            parentDir);
                  showerror(102, true, "Download not possible: " + 
                            pinfo.file);
                  continue;
               }
            }
            
            Vector v = listInOutSandBox(pinfo.topdirnum, pinfo.pack);
            Enumeration enum = v.elements();
            boolean foundit = false;
            while(enum.hasMoreElements()) {
               PackageInfo pi = (PackageInfo)enum.nextElement();
               
               if (pinfo.pack.equals(pi.getPackageName())) {
                  
                  foundit = true;
                  
                  Vector cvec = listPackageContents(pi.getPackageId());
                  Enumeration enumc = cvec.elements();
                  boolean foundfile = false;
                  while(enumc.hasMoreElements()) {
                     FileInfo fi = (FileInfo)enumc.nextElement();
                     if (fi.getFileName().replace('\\', '/').equals(pinfo.file)) {
                        foundfile = true;
                        Operation op = downloadFile(f, pi.getPackageId(), 
                                                    fi, 
                                                    restartXfer);
                        showmsg(125, true, "Downloading from " + pinfo.file);
                        handleOperation(arr[0], f, op);
                        break;
                     }
                  }
                  
                  if (!foundfile) {
                     showerror(102, true, "Invalid file for download: " + 
                               pinfo.path);
                  }
                  
                  break;
               }
            }
            
            if (!foundit) {
               showerror(102, true, "Invalid package for download: " + 
                         pinfo.path);
            }
            
         } catch(AbortAllException aall) {
            showerror(102, true, "Aborting ALL downloads in queue '");
            return;
         } catch(Exception ee) {
            if (printexception) SearchEtc.printStackTrace(ee, System.out);
            showerror(102, true, "Error downloading file " + 
                      pinfo.path + ": " + ee.getMessage());
         }
      }
   }
   
   void cmd_getpack(Command c, String arr[], int numused, boolean wild) {
      
      if (wild) {
         if (numused < 3) {
            showerror(501, true, 
                      "Command '" + arr[0] + "' takes 2-n parameters");
            return;
         }
      } else {
         if (numused < 2 || numused > 4) {
            showerror(501, true, 
                      "Command '" + arr[0] + "' takes 1-3 parameters");
            return;
         }
      }
   
      Vector todo = new Vector();
      if (wild) {
         for(int i=2; i < numused; i++) {
            todo.addElement(new PathInfo(arr[i]));
         }
      }
      
      String encoding = arr[1];
      
     // If not doing wild, then current package is fine
      if (!wild) {
         todo.addElement(new PathInfo(numused > 2 ? arr[2] : "."));
      }
      
      todo = packageWild(todo);
      
      if (todo.size() == 0) {
         showerror(503, true, "No matching packages for download");
         return;
      }
      
      if (!wild) {
         if (todo.size() != 1) {
            showerror(503, true, "Too many matching packages for download");
            return;
         }
      }
      
      Enumeration pathenum = todo.elements();
      while(pathenum.hasMoreElements()) {
      
         PathInfo pinfo = (PathInfo)pathenum.nextElement();
                           
        // get is valid for any file
         if (!pinfo.isValid                           ||
             pinfo.pack == null                       ||
             pinfo.topdirnum == GROUPS_N) {
            showerror(102, true, "Invalid package for download: " + 
                      pinfo.path);
            continue;
         }
         
         if (wild && prompt) {
            showmsg(197, true, "Download package " + pinfo.pack + 
                               " with encoding " + encoding + 
                               " [yes/no]? <y> ");
            try {
               String ans = input.readLine();
               if (ans == null) return;
               ans = ans.trim();
               if (!ans.equalsIgnoreCase("yes") &&
                   !ans.equalsIgnoreCase("y")   &&
                   !ans.equals("")) {
                  continue;
               }
            } catch(IOException ioe) {
               return;
            }
         }
         
         try {
         
            File f = null;
            if (wild || numused < 4) {
               f = makelocalfile(pinfo.pack + "." + encoding);
            } else {
            
               f = makelocalfile(arr[3]);
               String n = f.getName();
               if (n.indexOf("*") >= 0) {
                  Vector files = new Vector();
                  String p = f.getParent();
                  f = new File(p);
                  String list[] = f.list();
                  if (list != null) {
                     if (!p.endsWith(File.separator)) p += File.separator;
                     getMatchingWilds(n, p, list, files); 
                     if (files.size() != 1) {
                        showerror(102, true, 
                                  "Invalid number of matched for destination");
                        continue;
                     }
                     f = makelocalfile((String)files.firstElement());
                  } else {
                     showerror(102, true,
                               "Invalid destination directory '" + p + "'");
                     continue;
                  }
               }
               
              // If a directory is named, then make it a file
               if (f.exists() && f.isDirectory()) {
                  f = new File(f.getAbsolutePath() + 
                               File.separator + pinfo.file + "." + encoding);
               }
            }
            
            
           // make all directories
            String parentDir = f.getParent();
            if (parentDir != null) {
               File dirFile = new File(parentDir);
               if (!dirFile.exists()) {
                  if (dirFile.mkdirs()) {
                     showmsg(100, true, "Directories created: " + parentDir);
                  } else {
                     showerror(102, true, "Error creating directories: " + 
                               parentDir);
                     showerror(102, true, "Download not possible: " + 
                               pinfo.file);
                     continue;
                  }
               } else if (!dirFile.isDirectory()) {
                  showerror(102, true, 
                            "Target for download is not a directory: " +
                            parentDir);
                  showerror(102, true, "Download not possible: " + 
                            pinfo.file);
                  continue;
               }
            }
            
            Vector v = listInOutSandBox(pinfo.topdirnum, pinfo.pack);
            Enumeration enum = v.elements();
            boolean foundit = false;
            while(enum.hasMoreElements()) {
               PackageInfo pi = (PackageInfo)enum.nextElement();
               
               if (pinfo.pack.equals(pi.getPackageName())) {
                  
                  foundit = true;
                  
                  statOverrideBytes = pi.getPackageSize();
                  Operation op = downloadPackage(f, pi.getPackageId(), 
                                                 encoding);
                  showmsg(125, true, "Downloading encoded package " + 
                          pinfo.pack);
                  handleOperation(arr[0], f, op);
                  break;
               }
            }
            
            if (!foundit) {
               showerror(102, true, "Invalid package for encoded download: " + 
                         pinfo.path);
            }
            
         } catch(AbortAllException aall) {
            showerror(102, true, "Aborting ALL downloads in queue '");
            return;
         } catch(Exception ee) {
            if (printexception) SearchEtc.printStackTrace(ee, System.out);
            showerror(102, true, "Error downloading package " + 
                      pinfo.path + ": " + ee.getMessage());
         }
      }
   }
   
   void cmd_rm(Command c, String arr[], int numused) {
   
      if (numused < 2) {
         showerror(501, true, 
                   "Command '" + arr[0] + "' takes at least 1 parameter");
         return;
      }
   
      Vector todo = new Vector();
      for(int i=1; i < numused; i++) {
         todo.addElement(new PathInfo(arr[i]));
      }
      
      todo = packageWild(todo);
      
      Enumeration pathenum = todo.elements();
      while(pathenum.hasMoreElements()) {
         PathInfo pinfo = (PathInfo)pathenum.nextElement();
         
        // If not valid address, 
         if (!pinfo.isValid) {
            showerror(102, true, 
                      "Cannot rmdir specified location: " + pinfo.origpath);
            continue;
         }
   
        // Rm is valid to remove a file only, and only in sandbox
         if (!pinfo.isValid                           ||
             pinfo.file == null                       ||
             pinfo.isAccess                           ||
             pinfo.topdirnum != SANDBOX_N) {
            
            showerror(102, true, "Cannot remove the specified entity: " + 
                      pinfo.path);
            continue;
         }
      
         try {
            Vector v = listInOutSandBox(pinfo.topdirnum, pinfo.pack);
            Enumeration enum = v.elements();
            boolean found = false;
            while(!found && enum.hasMoreElements()) {
               PackageInfo pi = (PackageInfo)enum.nextElement();
               
               if (pinfo.pack.equals(pi.getPackageName())) {
                  Vector cvec = listPackageContents(pi.getPackageId());
                  Enumeration enumc = cvec.elements();
                  while(enumc.hasMoreElements()) {
                     FileInfo fi = (FileInfo)enumc.nextElement();
                     if (pinfo.file.equals(fi.getFileName())) {
                        deleteFileFromPackage(pi.getPackageId(), 
                                              fi.getFileId());
                        found = true;
                        showmsg(101, true, 
                                "pack[" + pi.getPackageName() +
                                "] file[" + fi.getFileName() + "] deleted");
                        break;
                     }
                  }
                  break;
               }
            }
            
            if (!found) {
               showerror(102, true, "Invalid Package/File : " + pinfo.path);
            }
            
         } catch(Exception ee) {
            if (printexception) SearchEtc.printStackTrace(ee, System.out);
            showerror(102, true, 
                      "Cannot remove the specified file: " + 
                      pinfo.path + ": " + ee.getMessage());
         }
      }      
   }
   void cmd_help(Command c, String arr[], int numused) {
      if (numused != 1) {
         showerror(501, true, 
                   "Command '" + arr[0] + "' takes no parameters");
         return;
      }
      
      
      showmsg(101, true, " Command set (most commands accept wildcards) ");
      showmsg(101, true, "----------------------------------------------");
      for(int i=0; i < commands_str.length; i+=2) {
         if (commands_str[i+1] != null) {
            showmsg(101, true, 
                    pad(commands_str[i],15,true) + 
                    commands_str[i+1]);
         }
      }
   }
   void cmd_pwd(Command c, String arr[], int numused) {
      if (numused != 1) {
         showerror(501, true, "Command '" + arr[0] + "' takes no parameters");
         return;
      }
      showmsg(101, true, "Remote working directory: " + pwd);
   }
   void cmd_lpwd(Command c, String arr[], int numused) {
      if (numused != 1) {
         showerror(501, true, "Command '" + arr[0] + "' takes no parameters");
         return;
      }
      showmsg(101, true, "Local working directory: " + lpwd);
   }
   void cmd_exit(Command c, String arr[], int numused) {
      if (numused != 1) {
         showerror(501, true, "Command '" + arr[0] + "' takes no parameters");
         return;
      }
      showmsg(421, true, "Exit called ... shutting down");
      shutdownAndExit();
   }
   void cmd_checkmd5(Command c, String arr[], int numused) {
   
      if (numused < 2) {
         showerror(501, true, "Command '" + arr[0] + "' takes 1-n parameters");
         return;
      }
      
      boolean partial = false;
      Vector files = null;
      for(int i=1; i < numused; i++) {
         if (i == 1 && arr[i].equals("--partial")) {
            partial = true;
            continue;
         }
      
         files = fileWild(arr[i], files);
      }
      
      if (files.size() == 0) {
         showerror(503, true, "md5check: no matches for local file(s)");
         return;
      }
      
      Enumeration fenum = files.elements();
      while(fenum.hasMoreElements()) {
         String lfile = null;
         try {
            lfile = (String)fenum.nextElement();
            File f = makelocalfile(lfile);
            String remote = f.getName();
            
            if (!f.exists() || !f.isFile() || !f.canRead()) {
               showerror(102, true, lfile + ": not valid local file");
               continue;
            }
            
            PathInfo pinfo = new PathInfo(remote);
            if (!pinfo.isValid     || 
                pinfo.isAccess     || 
                pinfo.file == null ||
                pinfo.topdirnum == GROUPS_N) {
               showerror(102, true, remote + ": not valid package location");
               continue;
            }
            
            Vector v = listInOutSandBox(pinfo.topdirnum, pinfo.pack);
            boolean found = false;
            Enumeration enum = v.elements();
            while(enum.hasMoreElements()) {
               PackageInfo pi = (PackageInfo)enum.nextElement();
               if (pi.getPackageName().equals(pinfo.pack)) {
                  found = true;
                  Vector cvec = listPackageContents(pi.getPackageId());
                  Enumeration enumc = cvec.elements();
                  boolean ffound = false;
                  while(enumc.hasMoreElements()) {
                     FileInfo fi = (FileInfo)enumc.nextElement();
                     if (fi.getFileName().equals(f.getName())) {
                        ffound = true;
                        
                        if (f.length() == fi.getFileSize() ||
                           (partial && f.length() > fi.getFileSize())) {
                           
                           if (f.length() != fi.getFileSize()) {
                              String fimd5 = fi.getFileMD5().toLowerCase();
                              String lmd5 = SearchEtc.calculateMD5(f, 
                                                         fi.getFileSize());
                              if (!lmd5.equalsIgnoreCase(fimd5)) {
                                 showerror(102, true, 
                                           "checkmd5: partial files Differ: "
                                           + lfile + " " + lmd5.toLowerCase());
                              } else {
                                 showmsg(101, true, 
                                           "checkmd5: partial files Match: "
                                           + lfile + " " + lmd5.toLowerCase());
                              }
                           } else {
                              if (f.length() != 0) {
                                 String fimd5 = fi.getFileMD5().toLowerCase();
                                 if (!SearchEtc.calculateMD5(f).equalsIgnoreCase(fimd5)){
                                    showerror(102, true, 
                                              "checkmd5: files Differ: " + 
                                              lfile);
                                 }
                              } else {
                                 showmsg(101, true, 
                                         "checkmd5: 0 len files Match: "
                                         + lfile);
                              }
                           }
                        } else {
                           showerror(102, true, 
                                     "checkmd5: files Differ: " + lfile);
                        }
                        break;
                     }
                  }               
                  
                  if (!ffound) {
                     showerror(102, true, 
                               "Specified file to checkmd5 not found: " + 
                               remote);
                  }
                  
                  break;
               }
            }
            
            if (!found) {
               showerror(102, true, 
                         "Specified package to checkmd5 not found: " + remote);
            }
            
         } catch(Exception ee) {
            if (printexception) SearchEtc.printStackTrace(ee, System.out);
            showerror(102, true, "Error checking md5 for " + 
                      lfile + ": " + ee.getMessage());
         }
      }
   }
   void cmd_verbose(Command c, String arr[], int numused) {
      if (numused != 2) {
         showerror(501, true, "Command '" + arr[0] + "' takes 1 parameter");
         return;
      }
      
      int vlev = -1;
      try {
         vlev = Integer.parseInt(arr[1]);
      } catch(NumberFormatException nfe) {}
      if (vlev < 0) {
         showerror(501, true, "Error: verbose cmd takes a positive number");
      }
      dotimer = false;
      
      ((ProxyDebugInterface)dropbox).disableDebug();
     
      printexception = false;
      if (vlev > 0) dotimer = true;
      if (vlev > 1) printexception = true;
      if (vlev == 2) {
         ((ProxyDebugInterface)dropbox).enableDebug(ProxyDebugInterface.NAMES);
      } else if (vlev > 2) {
         ((ProxyDebugInterface)dropbox).enableDebug();
      }
   }
   void cmd_parseable(Command c, String arr[], int numused) {
      if (numused != 1) {
         showerror(501, true, "Command '" + arr[0] + "' takes no parms");
         return;
      }
      
      parseable = !parseable;
      showmsg(101, true, "Parseable output is now " + (parseable?"on":"off"));
   }
   
   void cmd_debug(Command c, String arr[], int numused) {
      if (numused != 2) {
         showerror(501, true, "Command '" + arr[0] + "' takes 1 parameter");
         return;
      }
      
      int dlev = -1;
      try {
         dlev = Integer.parseInt(arr[1]);
      } catch(NumberFormatException nfe) {}
      if (dlev < 0) {
         showerror(501, true, "Error: verbose cmd takes a positive number");
      }
      DebugPrint.setLevel(dlev);
   }
   
   void cmd_prompt(Command c, String arr[], int numused) {
   
      if (numused != 1) {
         showerror(501, true, "Command '" + arr[0] + "' takes no parameters");
         return;
      }
      prompt = !prompt;
      showmsg(101, true, "Prompt for mput/mget is now " + (prompt?"on":"off"));
   }
   
   void cmd_filter(Command c, String arr[], int numused) {
   
      if (numused == 1) {
         showmsg(101, true, "Filter for completed packages is " + 
                 (filtercompleted?"on":"off"));
         showmsg(101, true, "Filter for marked packages is " + 
                 (filtermarked?"on":"off"));
         return;
      }
      
      for(int i=1; i < numused; i++) {
         if (arr[i].equalsIgnoreCase("marked")) {
            if (i+1 < numused) {
               if        (arr[i+1].equalsIgnoreCase("true") ||
                          arr[i+1].equalsIgnoreCase("on")) {
                  filtermarked = true;
                  i++;
               } else if (arr[i+1].equalsIgnoreCase("false") ||
                          arr[i+1].equalsIgnoreCase("off")) {
                  filtermarked = false;
                  i++;
               } else {
                  filtermarked = !filtermarked;
               }
            } else {
               filtermarked = !filtermarked;
            }
            showmsg(101, true, "Filter for marked packages is now " + 
                               (filtermarked?"on":"off"));
                               
           /* Don't save the filter changes to server for now. To persistently
              change the filter options, use the setoption command
              
           // If we have options
            if (DropboxGenerator.getProtocolVersion() > 4) {
               try {
                  setOption(DropboxGenerator.FilterMarked, "" + 
                             filtermarked);
               } catch(Exception ee) {
                  if (printexception) SearchEtc.printStackTrace(ee, System.out);
                  showerror(503, true,
                            "Error saving option value to server");
               }
            }
           */
                               
         } else if (arr[i].equalsIgnoreCase("completed")) {
            if (i+1 < numused) {
               if        (arr[i+1].equalsIgnoreCase("true") ||
                          arr[i+1].equalsIgnoreCase("on")) {
                  filtercompleted = true;
                  i++;
               } else if (arr[i+1].equalsIgnoreCase("false") ||
                          arr[i+1].equalsIgnoreCase("off")) {
                  filtercompleted = false;
                  i++;
               } else {
                  filtercompleted = !filtercompleted;
               }
            } else {
               filtercompleted = !filtercompleted;
            }
            showmsg(101, true, "Filter for marked packages is now " + 
                               (filtercompleted?"on":"off"));
                               
           /* Don't save the filter changes to server for now. To persistently
              change the filter options, use the setoption command
              
           // If we have options
            if (DropboxGenerator.getProtocolVersion() > 4) {
               try {
                  setOption(DropboxGenerator.FilterComplete, "" + 
                             filtercompleted);
               } catch(Exception ee) {
                  if (printexception) SearchEtc.printStackTrace(ee, System.out);
                  showerror(503, true, 
                            "Error saving option value to server");
               }
            }
           */
                               
         } else {
            showerror(102, true, 
                      "Invalid parameter for filter command [" + arr[i] + "]");
         }
      }
   }
   
   void cmd_mark(Command c, String arr[], int numused, boolean mark) {
   
      String cmd = mark?"mark read":"mark unread";
   
      Vector todo = new Vector();
      for(int i=1; i < numused; i++) {
         todo.addElement(new PathInfo(arr[i]));
      }
      
      if (todo.size() == 0) {
         todo.addElement(new PathInfo(pwd));
      }
      
      todo = packageWild(todo);
      
      Enumeration pathenum = todo.elements();
      while(pathenum.hasMoreElements()) {
      
         PathInfo pinfo = (PathInfo)pathenum.nextElement();
         
         if (!pinfo.isValid                           ||
             pinfo.pack == null                       ||
             pinfo.topdirnum != INBOX_N) {
            
            showerror(102, true, 
                      "Cannot " + cmd + " the specified entity: " + 
                      pinfo.path);
            continue;
         }
         
         try {
            Vector v = listInOutSandBox(pinfo.topdirnum, pinfo.pack);
            Enumeration enum = v.elements();
            boolean found = false;
            while(enum.hasMoreElements()) {
               PackageInfo pi = (PackageInfo)enum.nextElement();
                  
               if (pinfo.pack.equals(pi.getPackageName())) {
                  markPackage(pi.getPackageId(), mark);
                  found = true;
                  showmsg(101, true, 
                          "Package [" + pi.getPackageName() + "]" + 
                          " is now " + (mark?"marked":"unmarked"));
                  
               }
            }
            
            if (!found) {
               showerror(102, true, "Invalid Package : " + pinfo.pack);
            }
         } catch(Exception ee) {
            if (printexception) SearchEtc.printStackTrace(ee, System.out);
            showerror(102, true, "Cannot " + cmd +" the specified entity: " + 
                      pinfo.path + ": " + ee.getMessage());
         }
      }
   }

   static public void main(String args[]) {
      new DropboxCmdline(args);
   }
   
   public boolean parse(String args[]) {
      
      int idx = 0;
      
      String proto = "https";
      
      if (args.length == 0) {
         usage(null);
         return false;
      }
      
      for(int i=0; i < args.length; i++) {
         currentCmd = args[i];
         if (args[i].startsWith("-")) {
            if (args[i].equals("-?") || args[i].equalsIgnoreCase("-help")) {
               usage(null);
               return false;
            } else if (args[i].equalsIgnoreCase("-noupdate")) {
               doupdate = false;
            } else if (args[i].equalsIgnoreCase("-numWorkers")) {
               numWorkers = Integer.parseInt(args[++i]);
            } else if (args[i].equalsIgnoreCase("-autotimeout")) {
               autoCloseDelay = Integer.parseInt(args[++i]);
            } else if (args[i].equalsIgnoreCase("-hessian")) {
               useSoap = false;
               dodirect = false;
            } else if (args[i].equalsIgnoreCase("-soap")) {
               useSoap = true;
               dodirect = false;
            } else if (args[i].equalsIgnoreCase("-direct")) {
               dodirect = true;
            } else if (args[i].equalsIgnoreCase("-oldext")) {
               oldext = true;
            } else if (args[i].equalsIgnoreCase("-nooldext")) {
               oldext = false;
            } else if (args[i].equalsIgnoreCase("-context")) {
               if (args.length <= i+1) {
                  showerror(500, true, "Error: -context takes a parameter");
                  return false;
               }
               ctx = args[++i];
            } else if (args[i].equalsIgnoreCase("-cmdfile")) {
               if (args.length <= i+1) {
                  showerror(500, true, "Error: -cmdfile takes a parameter");
                  return false;
               }
               cmdfile = args[++i];
            } else if (args[i].equalsIgnoreCase("-stopOnError")) {
               stoponerr = true;
            } else if (args[i].equalsIgnoreCase("-md5")) {
               
               if (args.length <= i+1) {
                  showerror(500, true, "Error: -md5 takes 1-2 parms");
                  return false;
               }
               
               String fn = args[++i];
               File f = new File(fn);
               
               try {
                  long flen = -1;
                  if (args.length > i+1) {
                     try {
                        flen = Long.parseLong(args[++i]);
                     } catch(NumberFormatException nfe) {}
                     
                     if (flen < 0) {
                        showerror(500, true, "Error: -md5 2nd parm should be a filesize!");
                        return false;
                     }
                  }
                  
                  String lmd5 = null;
                  if (flen > -1) {
                     if (flen > f.length()) {
                        showmsg(500, true, "-md5 2nd parm is > filesize");
                        return false;
                     }
                     lmd5 = SearchEtc.calculateMD5(f, flen);
                  } else {
                     flen = f.length();
                     lmd5 = SearchEtc.calculateMD5(f);
                  }
                  
                  showmsg(101, true, "MD5 for file[" + fn + "] len[" + flen + 
                                     "] = " + lmd5 + "\n\n");
               } catch(IOException ioe) {
                  showerror(503, true, 
                            "IOException while calculating MD5 for " + fn);
               }
               
               return false;
            } else if (args[i].equalsIgnoreCase("-verbose")) {
               if (args.length <= i+1) {
                  showerror(500, true, "Error: -verbose takes a parameter");
                  return false;
               }
               int vlev = -1;
               try {
                  vlev = Integer.parseInt(args[++i]);
               } catch(NumberFormatException nfe) {}
               if (vlev < 0) {
                  showerror(500, true, 
                            "Error: -verbose option takes a positive number");
                  return false;
               }
               if (vlev > 0) dotimer = true;
               if (vlev > 1) {
                  ((ProxyDebugInterface)dropbox).enableDebug();
               }
            } else if (args[i].equalsIgnoreCase("-debug")) {
               if (args.length <= i+1) {
                  showerror(500, true, "Error: -verbose takes a parameter");
                  return false;
               }
               lev = -1;
               try {
                  lev = Integer.parseInt(args[++i]);
               } catch(NumberFormatException nfe) {}
               if (lev < 0) {
                  showerror(500, true,
                          "Error: -verbose option takes a positive number");
                  return false;
               }
            } else if (args[i].equalsIgnoreCase("-pipe")) {
               dopipe = true;
            } else if (args[i].equalsIgnoreCase("-parseable")) {
               parseable = true;
            } else if (args[i].equalsIgnoreCase("-startdir")) {
               if (args.length <= i+1) {
                  showerror(500, true, "Error: -startdir takes a parameter");
                  return false;
               }
               lpwd = (new File(args[++i]).getAbsolutePath());
            } else if (args[i].equalsIgnoreCase("-nostats")) {
               dostats = false;
            } else if (args[i].equalsIgnoreCase("-stats")) {
               dostats = true;
            } else if (args[i].equalsIgnoreCase("-dscinstall")) {
               if (args.length <= i+1) {
                  showerror(500, true, "Error: -dscinstall takes a parameter");
                  return false;
               }
               dscinstall = args[++i];
            } else if (args[i].equalsIgnoreCase("-fixfreeze")) {
               allowabort = false;
            } else if (args[i].equalsIgnoreCase("-noteslaunch")) {
              // For noteslaunch, just read all parms from cmdline until 
              //  -THE_END is read
               showmsg(100, true, "Preparing for notes launch. Read parms from stdin until -THE_END");
               
               if (!noteslaunch) {
                  noteslaunch = true;
                  Vector parms = new Vector();
                  
                 // Add in the rest of the parms we have not yet parsed
                  while(++i < args.length) {
                     parms.addElement(args[i]);
                  }
                  
                  boolean foundTheEnd = false;
                  
                  char b;
                  StringBuffer sb = new StringBuffer();
                  
                 // Flash, seenFirst set to true, so we DON'T skip first parm
                  boolean seenFirst = true;
                  try {
                     while((b=(char)System.in.read()) != -1) {
                        if (b != '\n') {
                           
                          // Don't save CR
                           if (b != '\r') sb.append(b);
                        } else {
                           String thestring = sb.toString();
                           if (!thestring.equals("-THE_END")) {
                             // We skip the first parm when launching via NOTES
                             // Cause its actually startds that is calling, and
                             // is sending dropboxcmdline as first parm
                              if (seenFirst) {
                                 parms.addElement(thestring);
                              } else {
                                 seenFirst = true;
                              }
                              sb = new StringBuffer();
                           } else {
                              foundTheEnd = true;
                              break;
                           }
                        }
                     }
                  } catch(IOException ioe) {
                  }
                  
                  if (!foundTheEnd) {
                     showerror(503, true, 
                               "Notes launch failed. -THE_END not found!");
                     return false;
                  }
                  
                  i = 0 ;
                  args = new String[parms.size()];
                  Enumeration enum = parms.elements();
                  while(enum.hasMoreElements()) {
                     args[i++] = (String)enum.nextElement();
                  }
                  i = -1;
               }
            } else if (args[i].equalsIgnoreCase("-proto")) {
               if (args.length <= i+1) {
                  showerror(500, true, "Error: -proto takes a parameter");
                  return false;
               }
               proto = args[++i];
            } else if (args[i].equalsIgnoreCase("-allowabort")) {
               allowabort = true;
            } else {
               usage(args[i]);
               return false;
            }
         } else if ((idx=args[i].lastIndexOf('@')) > 0) {
            who  = args[i].substring(0,idx);
            try {
               mach = args[i].substring(idx+1);
            } catch(Exception e) {}
            if (mach == null || mach.length() == 0) {
               showerror(500, true, 
                       "For userid@host, the value following '@' must be non-null");
               return false;
            }
         } else {
            usage(args[i]);
            return false;
         }
      }
      
      DebugPrint.setLevel(lev);
      
      if (dopipe && cmdfile == null) {
         showerror(500, true, 
                   "If using -pipe, you must also specify -cmdfile");
         return false;
      }
      
      if ((dopipe || cmdfile != null) && parseable)  {
         showerror(500, true, 
                   "If using -pipe/-cmdfile and -parseable are mutually exclusive");
         return false;
      }
      
      if (who == null) {
         showerror(500, true, "userid@host MUST be specified!");
         return false;
      }
      
     // Setup topURL
      topURL = proto + "://" + mach;
      if (ctx != null && ctx.length() > 0 && !ctx.equals("/")) {
         topURL += "/" + ctx;
      }
      
      return true;
   }
   
   
   protected PathInfo autoCreatePackage(PathInfo pinfo) {
      try {
         Vector v = listInOutSandBox(3, pinfo.pack);
         Enumeration enum = v.elements();
         PackageInfo pi = null;
         while(enum.hasMoreElements()) {
            pi = (PackageInfo)enum.nextElement();
            
            if (pinfo.pack.equals(pi.getPackageName())) {
               pinfo = new PathInfo(pinfo);
               pinfo.miscLong = pi.getPackageId();
               return pinfo;
            }
         }
         
         try {
            long packid = createPackage(pinfo.pack);
            pinfo = new PathInfo(pinfo);
            pinfo.miscLong = packid;
            clearCache();
            return pinfo;
         } catch(Exception ee) {
            return null;
         }
      } catch(Exception ee) {
         return null;
      }
   }
   
   protected PathInfo autoSelectPackage(String createName) {
   
      try {
         Vector v = listInOutSandBox(3, createName);
         if (v == null || v.size() == 0) {
            return autoCreatePackage(new PathInfo("/" + SANDBOX + 
                                                  "/" + createName));
         }
         
         Enumeration enum = v.elements();
         boolean foundit = false;
         PackageInfo pi = null;
         PackageInfo pi_newest = null;
         while(enum.hasMoreElements()) {
            pi = (PackageInfo)enum.nextElement();
            
            if (pi_newest == null || 
                pi.getPackageCreation() >
                pi_newest.getPackageCreation()) {
               pi_newest = pi;
            }
         }
         
         PathInfo pinfo = new PathInfo("/" + SANDBOX + "/" + 
                                       pi_newest.getPackageName());
         pinfo.miscLong = pi_newest.getPackageId();
         return pinfo;
         
      } catch(Exception ee) {
         return null;
      }
   }
      
   public final int maxRecurseDepth = 100;
   
   protected Vector recursiveFindFile(File f, boolean includeDirs) {
      Vector ret = new Vector();
      if (f.exists()) {
         if (f.isDirectory()) {
            recursiveFindFile(ret, f, 1, includeDirs); 
           // If we are including the dir, add it after all of its
           //  recursively contained files
            if (includeDirs) {
               ret.addElement(f);
            }
         } else if (f.isFile()) {
            ret.addElement(f);
         }
      }
      return ret;
   }
   
  // Call only with directory
   protected void recursiveFindFile(Vector ret, File f, int depth, 
                                    boolean includeDirs) {
      if (depth > maxRecurseDepth) {
         showerror(102, true, "recursiveFindFile: search depth > " +
                   maxRecurseDepth + "! Stop traversal: " + 
                   f.getPath());
         return;
      }
      String files[] = f.list();
      if (files != null) {
         for(int i=0; i < files.length; i++) {
            File lf = new File(f, files[i]);
            if (lf.exists()) {
               if (lf.isDirectory()) {
                  recursiveFindFile(ret, lf, depth++, includeDirs);
                  
                 // If we are including the dir, add it after all of its
                 //  recursively contained files
                  if (includeDirs) {
                     ret.addElement(lf);
                  }
               } else if (lf.isFile()) {
                  ret.addElement(lf);
               }
            }
         }
      }
   }
   
  /*
   protected Vector flatFindFile(File f) {
      int depth = 1;
      
      Vector ret = new Vector();
      Vector todo = new Vector();
      todo.addElement(f);
      while(todo.size() > 0) {
         f = (File)todo.elementAt(0);
         todo.removeElementAt(0);
         
         if (f.exists()) {
            if (f.isFile()) {
               ret.addElement(f);
            } else if (f.isDirectory()) {
               if (files != null) {
                  for(int i=0; i < files.length; i++) {
                     todo.addElement(new File(f, files[i]));
                  }
               }
            }
         }
      }
      return ret;
   }
  */
}
