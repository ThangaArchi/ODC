package oem.edge.ed.odc.dropbox.server;

import  oem.edge.ed.odc.dropbox.common.*;
import  oem.edge.ed.odc.ftp.common.*;
import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.tunnel.common.*;
import  oem.edge.ed.util.*;
import  oem.edge.ed.odc.util.*;
import  oem.edge.ed.odc.dsmp.server.*;
import  oem.edge.common.cipher.*;

import java.lang.*;
import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;
import java.lang.reflect.*;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;
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

/*
** Note: We are using the HandlerID to be the LoginID for now
**       Also, the ParticipantID is also == to LoginID. This is enough as
**       long as the meetingID is always presented at the same time. It is.
**
** Note1: Add timeout to incoming connections, disconnect if not logged in for
**        5 minutes (some value). If lots of errors ... disconnect, etc.
*/

public class DropboxServer extends DropboxDispatchBase implements Runnable {
            
//   java.lang.reflect.Method dataFromToken = null;
   Object                   cipher        = null;
  //Object                   edgecipher    = null;
   String                   userpwFile    = null;
   long                userpwLastModified = 0;
   
   protected String forbiddenChars = ":";
   
   protected String forcedropboxpath = "/forcedropboxdebug/";
   
   protected ReloadingProperty limitSendByProject = null;
   
   private String  klogUserid   = null;
   private String  klogPassword = null;
   private String  klogCell     = null;
   private String  klogPWDir    = null;
   private boolean doKlog       = false;
   
   private long    klogTimeout  = 12*60*60*1000;
   
   protected String log4jpropfile = "dropboxlog4j.properties";
   protected String dropboxBanner = "dropbox.BANNER";
   protected Logger log           = null;
   
   
   protected boolean cleanPackages           = false;
   protected boolean cleanFiles              = false;
   protected boolean allocationInfo          = false;
   protected boolean keeprunning             = false;
   protected boolean doamtchecking           = false;
   protected boolean doamtmailing            = false;
   protected boolean doemailsend             = true;
   protected boolean fakeamt                 = false;
   protected boolean doamtprojects           = true;
   protected boolean doHtml                  = false;
   protected boolean doWebdropbox            = true;
   
   protected boolean doGroupAMT              = true;
   protected boolean allowByEmail            = false;
   protected boolean byEmailToIBMONLY        = true;
   protected boolean complainReceiverNoExist = true;
   protected AMTMailer amtMailer             = new AMTMailer();
   
   protected boolean tryUDSlush              = false;
   protected boolean allowUDSlush            = false;
   protected ReloadingProperty udslushProp   = null;
   
  // Securing the incoming connection 
   protected KeystoreInfo ksi = null;
   protected KeystoreInfo tsi = null;
   protected boolean secureMode = false;
   
   protected String UDNote                   = "";
   
   static boolean      usetriggers               = false;
   static boolean      policy_supportSameCompany = false;
   static boolean      policy_supportIBM         = true;
   static boolean      policy_supportLookup      = false;
   
  // User key Vector value (first elem pw, reset projects)
   Hashtable userpw      = new Hashtable();
   
  // String elements have vector of User objects
  // Integer elements have a direct User object
   Hashtable users       = new Hashtable();
   
  // Integer elements have direct Handler objects
   Hashtable handlers    = new Hashtable();
   
  // Integer elements have direct Operation objects
   Hashtable operations  = new Hashtable();
   
   int localport            = 0;
   
   PackageManager packageMgr = null;
   
   static protected boolean skipPartialCheck = false;
   
   
   protected boolean        daemonMode    = false;
   public boolean getDaemonMode() { return daemonMode; }
   
   static final        int MAX_PROTO_OUT  = 3;
   
   public final long MAX_COMPONENT_SIZE   = 1024*1024*50;
   
   
   String tunneldropbox = "edesign.chips.ibm.com";
   String sftpdropbox   = "dropbox.chips.ibm.com";
   String smtpserver    = "us.ibm.com";
   String replyAddr     = "econnect@us.ibm.com";
   String FEHostURL  = "https://www-309.ibm.com";
   String ESS        = "/technologyconnect/EdesignServicesServlet.wss";
   String launchURL                  = "?op=7";
   String  downloadFileURL           = "?op=7&sc=";
   String weblaunchURL               = "?op=7&sc=webox:op:i";
   
   String htmlDownloadInfo = 
      "Finally, the package files may be individually downloaded\n" +
      "via a standard web browser using the URL's listed with each\n" +
      "file. Note that this facility is only available for files\n" +
      "less than 2GB in size, and that the download is NOT restartable\n\n";
      
   String webDownloadInfo = 
      "To launch the Web based dropbox tool, select this URL:\n\n" +
      "    %weblaunchurl%\n\n";
   
   String mailSubject   = "IBM Customer Connect Dropbox - Sender: %ownerid% - \"%40%packagename%\"";
   String mailBody = 
      "Dear IBM Customer Connect User:\n\n" +
      "A package has been sent to the Customer Connect Dropbox for\n" +
      "ID %sendtoid% by ID %ownerid% (%ownercompany%).\n\n" +
      "This package will expire on %packexpires%, so please\n" +
      "access the package prior to this date.\n\n" +
      "To launch the Dropbox GUI download tool, select this URL:\n\n" +
      "    %launchurl%\n\n" +
      "%webdownloadinfo%" +
      "Alternatively, you may choose to access your Dropbox account using\n" +
      "the sftp or dropboxftp command line tools:\n\n" +
      "         sftp %sendtoid%@%sftpdropbox%\n" +
      "  - or -\n" +
      "         dropboxftp %sendtoid%@%tunneldropbox%\n\n" +
      "%htmldownloadinfo%" +
      "-----------------------------------------------------------------------\n" +
      "Package Name           : %packagename%\n" +
      "Package Commit Date    : %commitdate%\n" +
      "Total Size             : %smartsize%\n" +
      "Estimated download time:\n" +
      "          56 Kbps      : %56kdownload%\n" +
      "          T1 (1.5 Mbps): %t1download%\n" +
      "Package Contents       :\n%packagecontents%" +
      "-----------------------------------------------------------------------\n" +
      "Delivered by IBM Customer Connect\n" +
      "This is a system generated email. Please do not reply\n" +
      "Please visit http://www.ibm.com/technologyconnect for more\n" +
      "information and to change or reset your IBM Customer Connect password.\n" +
      "Or contact the Customer Connect Help desk for other Dropbox ID and\n" +
      "Log-in problems:\n\n" +
      "       Monday-Friday, 8:00am-9:00pm EST USA\n" +
      "             US/Canada: +1-888-220-3343\n" +
      "         International: +1-802-769-3353\n" +
      "                E-mail: eConnect@us.ibm.com";
   
   
   String mailSubjectRR   = "IBM Customer Connect Dropbox: Return Receipt: %ownerid% \"%20%packagename%\"";
   String mailBodyRR = 
      "Dear IBM Customer Connect User:\n\n" +
      "A package that you sent to the Customer Connect Dropbox for\n" +
      "ID %sendtoid% (%sendtocompany%) using ID %ownerid% (%ownercompany%)\n" +
      "has been fully downloaded. This email notification has been generated\n"+
      "because the package in question had the Return Receipt option enabled.\n\n" +
      "" +
      "-----------------------------------------------------------------------\n" +
      "Package Name          : %packagename%\n" +
      "Package Expiration    : %packexpires%\n" +
      "Package Commit Date   : %commitdate%\n" +
      "Download Complete Date: %currentdate%\n" +
      "Total Size            : %smartsize%\n" +
      "Package Contents      :\n  ---Download complete date--- --TransferRate-- --Filesize-- --Filename------->\n%packagecontentsRR%" +
      "-----------------------------------------------------------------------\n" +
      "Delivered by IBM Customer Connect\n" +
      "This is a system generated email. Please do not reply\n" +
      "Please visit http://www.ibm.com/technologyconnect for more\n" +
      "information and to change or reset your IBM Customer Connect password.\n" +
      "Or contact the Customer Connect Help desk for other Dropbox ID and\n" +
      "Log-in problems:\n\n" +
      "       Monday-Friday, 8:00am-9:00pm EST USA\n" +
      "             US/Canada: +1-888-220-3343\n" +
      "         International: +1-802-769-3353\n" +
      "                E-mail: eConnect@us.ibm.com";
   
   
   public void debugprint(String s) {
      System.out.println(s);
   }
   
   private boolean reauthenticate() {
      boolean ret = false;
      try {
      
         EDCMafsFile afsFile = EDCMFSFactory.createFileObject(klogCell);
         
        //System.err.println("Authenticating to " + klogCell + " as " +
        //                   klogUserid + " with " + klogPassword);
                            
        // Reread the password/userid
         String lklogUserid   = PasswordUtils.getPassword(klogPWDir+"/._afsd435");
         String lklogPassword = PasswordUtils.getPassword(klogPWDir+"/._afsde7e");
         
         if (lklogUserid != null) klogUserid = lklogUserid;
         if (lklogPassword != null) klogPassword = lklogPassword;
                            
         if (!afsFile.afsAuthenticate(klogCell, klogUserid, klogPassword)) {
            DboxAlert.alert(1, "Unable to klog to Dropbox", 0,
                            "Attempt to klog upon startup of Dropbox failed"
                            + "\ncell         = " +  klogCell   
                            + "\npwdir        = " +  klogPWDir  
                            + "\nklogUserid   = " +  klogUserid 
                            + "\npasswd null? = " + (klogPassword == null) 
                            + "\npasswd len   = " 
                            + ((klogPassword == null)?0:klogPassword.length()));
         } else {
            ret = true;
         }
      } catch(Exception ee) {
         DboxAlert.alert(1, "Unable to klog to Dropbox", 0,
                         "Attempt to klog upon startup of Dropbox failed"
                         + "\ncell       = "   +  klogCell   
                         + "\npwdir        = " +  klogPWDir  
                         + "\nklogUserid = "   +  klogUserid 
                         + "\npasswd null? = " + (klogPassword == null) 
                         + "\npasswd len   = " 
                         + ((klogPassword == null)?0:klogPassword.length()) 
                         + "\nException = " + ee.toString() 
                         + "\nExMsg     = " + ee.getMessage());
      }
      return ret;
   }
   
   
   class KlogTimeout extends Timeout {
      long interval;
      long errinterval;
      public KlogTimeout(long delta, long error, boolean err) {
         super(err?error:delta, "klogtimeout", null);
         interval    = delta;
         errinterval = error;
      }
      
      public void tl_process(Timeout to) {
         
        // Add timeout for specified delta to do status update
         TimeoutManager tmgr = TimeoutManager.getGlobalManager();
         tmgr.removeTimeout("klogtimeout");
         
         if (!reauthenticate()) {
            tmgr.addTimeout(new KlogTimeout(interval, errinterval, true));
         } else {
            tmgr.addTimeout(new KlogTimeout(interval, errinterval, false));
         }
      }
   }
   
   public boolean isValidPackageEncoding(String encoding) {
      if (encoding.equals("tar.gz")) return true;
      if (encoding.equals("tgz")) return true;
      if (encoding.equals("tar")) return true;
      if (encoding.equals("zip")) return true;
      return false;
   }
   
   class DownloadOperation extends SendFileOperation 
                           implements ProtoSentListener{
      
     // numToSend is >= 0 if enabled ... then determines how many packets
     //  that can be sent. This allows us to synchronize things
      protected int numToSend         = -1;
      
     // numout is the number of outstanding packets (no fire yet)
      protected int numout            = 0;
      protected long totalconfirmed   = 0;
      protected DboxFileInfo info     = null;
      protected DboxPackageInfo pinfo = null;
      protected User user             = null;
      
      public synchronized void enableSyncFrames() {
         if (numToSend < 0) numToSend = 0;
      }
      
      public DownloadOperation(DSMPBaseHandler handler, int id, 
                               long totToXfer, long ofs, 
                               DboxFileInfo info, DboxPackageInfo pinfo, 
                               User user) {
         super(handler, id, totToXfer, ofs, (InputStream)null);
         if (info != null) {
            this.is = new ComponentInputStream(info);
         }
         this.info  = info;
         this.pinfo = pinfo;
         this.user  = user;
      }
      
      public synchronized void sendNewFrame(int num) {
         if (numToSend >= 0 && num > 0) {
            numToSend += num;
            notifyAll();
         }
        // log.info("SendNewFrame: " + num + " newvalue = " + numToSend);
      }
      
      public synchronized void  handleEndError(String reason) {
         try { 
            String cifo="Package{" + pinfo.getPackageId() + 
               "} [" + pinfo.getPackageName()+
               "] Filename{" + info.getFileId() +
               "} [" + info.getFileName() + 
               "]";
            if (reason != null) {
               DSMPBaseProto proto = null;
               proto=DropboxGenerator.abortDownloadEvent(getHandleToUse(), 
                                                         id, 0, reason);
               
              // Handle this as low since we did the data packets as well
               proto.setLowPriority(true);
               
               handler.sendProtocolPacket(proto);
               log.error("Aborting download opid=" + id + 
                         " reason[" + reason + "] " + cifo);
               pinfo.addFileAccessRecord(user, info.getFileId(), 
                                         DropboxGenerator.STATUS_FAIL, 
                                         (int)getXferRate());
            } else {
               log.info("Download operation complete opid=" + id + " " + cifo);
               pinfo.addFileAccessRecord(user, info.getFileId(), 
                                         DropboxGenerator.STATUS_COMPLETE,
                                         (int)getXferRate());
               
              // Get an updated view of the pinfo object. If the object is
              // reading complete and was not before, send RR email if needed
               try {
                  DboxPackageInfo newpinfo = 
                     packageMgr.lookupPackage(pinfo.getPackageId(), user);
                  
                  if (doamtmailing                       &&
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
            }
         } catch(DboxException ee) {
            log.error("Error adding file access " + 
                      "record after DownloadOp =>\n" + 
                      " Package " + pinfo + "\n" +
                      " File    " +info  + "\n" +
                      " User    " +user);
         }
      }
      
      public long getTotalConfirmed() { return totalconfirmed; }
      
      public synchronized void fireSentEvent(DSMPBaseProto p) {
         
         totalconfirmed += p.getNonHeaderSize() - 12;
         dataTransferred();
         
         if (--numout < 0) {
            log.error("Zoinks! numout in fireSentEvent < 0!!");
            numout = 0;
         }
         notifyAll();
      }
      
      public void sendData(long tofs, byte arr[], int bofs, int blen) {
         DSMPBaseProto proto = null;
         proto=DropboxGenerator.downloadFrameEvent(getHandleToUse(), id, 
                                                   tofs, arr, bofs, blen);
         proto.addSentListener(this);
         
        // This packet can be trumped by other less volumnous items
         proto.setLowPriority(true);
         
        // If this is the first packet, just return, don't block, otherwise,
        //  We want to keep from saturating the upload stream, and we get 
        //  callbacks
         synchronized(this) {
            while((numout >= MAX_PROTO_OUT || numToSend == 0) &&
                  getStatus() == STATUS_INPROGRESS) { 
               try {
                  wait(60000);
               } catch(InterruptedException ie) {}
            }
            if (getStatus() == STATUS_INPROGRESS) {
               numout++;
               if (numToSend > 0) numToSend--;
            } else {
               proto = null;
            }
         }
         
         if (proto != null) {
            handler.sendProtocolPacket(proto);
         }         
      }
   }
   
  // Operation for total package download
  //
  // We tag the operation with HUGE data size, then stream the encoded files
  //  down to client. The PackageInputStream will modify the expected total
  //  byte count to equal the actual byte count streamed (its not known up
  //  front).
  //
   class DownloadPackageOperation extends DownloadOperation {
      
      public DownloadPackageOperation(DSMPBaseHandler handler, int id, 
                                      DboxPackageInfo pinfo, 
                                      User user, String encoding) 
         throws DboxException {
         
        // We show that we are downloading something HUGE at 0 ofs!!
         super(handler, id, 0x7fffffffffffffffL, (long)0, null, pinfo, user);
         this.is = new PackageInputStream(pinfo, this, encoding);
         this.info  = null;
         this.pinfo = pinfo;
         this.user  = user;
      }
      
     // Hack ... The PackageInputStream will set the operation objects
     //          toXfer size to match the actual size using the setToXfer
     //          method. Capture that and generate an OperationCompleteEvent
     //          containing this size. Client will reciprocate by sending 
     //          either an Abort or OperationComplete, which will finish 
     //          things up here with  handleEndError invoke.
      public void setToXfer(long v)        { 
         super.setToXfer(v);
         
         DSMPBaseProto proto =
            DropboxGenerator.operationCompleteEvent(getHandleToUse(), 
                                                    id, "", v);
         
        // Handle this as low since we did the data packets as well
         proto.setLowPriority(true);
         
         handler.sendProtocolPacket(proto);
         
         String cinfo="Package{" + pinfo.getPackageId() + 
            "} [" + pinfo.getPackageName()+ "]";
         log.info("DownloadPackageOp: " + cinfo + ": setToXfer = " + v + 
                  ": generate opComplete for client");
      }
      
     // When we get the OperationComplete cmd back after we send OpCompleteEv
     //  we get here ... OR if an AbortOperation occurs, we get here, or 
     //  if some problem with the download occurs
      public synchronized void  handleEndError(String reason) {
         try { 
         
            String cifo="Package{" + pinfo.getPackageId() + 
               "} [" + pinfo.getPackageName()+ "]";
               
            if (reason != null) {
            
              // Ensure user knows
               DSMPBaseProto proto = null;
               proto=DropboxGenerator.abortDownloadEvent(getHandleToUse(), id,
                                                         0, reason);
               
              // Handle this as low since we did the data packets as well
               proto.setLowPriority(true);
               
               handler.sendProtocolPacket(proto);
               log.error("Aborting package download opid=" + id + 
                         " reason[" + reason + "] " + cifo);
            } else {
               log.info("Package Download operation complete opid=" + id +
                        " " + cifo);
                        
              // Mark all files as accessed
               Enumeration enum =  pinfo.getFiles().elements();
               while(enum.hasMoreElements()) {
                  FileInfo linfo = (FileInfo)enum.nextElement();
                  pinfo.addFileAccessRecord(user, linfo.getFileId(), 
                                            DropboxGenerator.STATUS_COMPLETE,
                                            (int)getXferRate());
               }
               
              // Get an updated view of the pinfo object. If the object is
              // reading complete and was not before, send RR email if needed
               try {
                  DboxPackageInfo newpinfo = 
                     packageMgr.lookupPackage(pinfo.getPackageId(), user);
                  
                  if (doamtmailing                       &&
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
            }
         } catch(DboxException ee) {
            log.error("Error adding file access " + 
                      "records after PackageDownloadOp =>\n" + 
                      " Package " + pinfo + "\n" +
                      " User    " +user);
         }
      }
   }
   
   class UploadOperation extends ReceiveFileOperation {
      
      DboxFileInfo info = null;
      DboxPackageInfo pinfo = null;
      
      public UploadOperation(DSMPBaseHandler handler, int id,
                             long totToXfer, long ofs, 
                             DboxFileInfo info, DboxPackageInfo pinfo) {
         super(handler, id, totToXfer, ofs, (OutputStream)null);
         this.info  = info;
         this.pinfo = pinfo;
        // Defer creation of the digest. No harm in this, it will just be
        //  created upon first write
         this.os    = new ComponentOutputStream(info, true);
      }
      
      public synchronized void  handleEndError(String reason) {
         String cifo="Package{" + pinfo.getPackageId() + 
            "} [" + pinfo.getPackageName() + 
            "] Filename{" + info.getFileId() + 
            "} [" + info.getFileName() + 
            "]";
         if (reason != null) {
            DSMPBaseProto proto = null;
            log.error("Aborting upload opid=" + id + 
                      " reason[" + reason + "] " + cifo);
            proto=DropboxGenerator.uploadDataError(getHandleToUse(), 0, 
                                                   reason, id);
            
           // Handle this as low since we did the data packets as well
            proto.setLowPriority(true);
            
            handler.sendProtocolPacket(proto);
            
           // Not checking for DboxException!!
            info.setFileStatus(DropboxGenerator.STATUS_INCOMPLETE);
         } else {
            DSMPBaseProto proto = null;
            log.info("Upload operation complete opid=" + id + " " + cifo);
            
           // The MD5 value on info should be updated. BIG ASSUMPTION! But a
           //  good one for now. The ComponentOutputStream has the same one, 
           //  and he forceSet it.
            if (DropboxGenerator.getProtocolVersion() >= 6) {
               proto=DropboxGenerator.operationCompleteEvent(getHandleToUse(),
                                                             id, 
                                                            info.getFileMD5());
            } else {
               proto=DropboxGenerator.operationCompleteEvent(getHandleToUse(),
                                                             id);
            }
            
           // Not checking for DboxException!!
            info.setFileStatus(DropboxGenerator.STATUS_COMPLETE);
            log.info("Filestatus updated in DB2");
            
           // Handle this as low since we did the data packets as well
            proto.setLowPriority(true);
            
            handler.sendProtocolPacket(proto);
         }
         
         try {
            pinfo.recalculatePackageSize();
         } catch(DboxException dbe) {
         }
      }
   }
   
   public class AMTData {
      public AMTData(User u, DboxPackageInfo p, String n, boolean rr) {
         user  = u;
         pinfo = p;
         name  = n;
         returnreceipt = rr;
      }
      public User            user;
      public DboxPackageInfo pinfo;
      public String          name;
      public boolean         returnreceipt;
   }
   
  // Sending mail to lots of IDs really slows things down ... thread it
   public class AMTMailer implements Runnable {
   
      Thread me = null;
      Vector todo = new Vector();
      boolean continueRunning = true;
   
      public void run() {
      
         while(true) {
            
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
                  sendAMTMailNoThread(d.user, d.pinfo, d.name, d.returnreceipt);
               }
            }
            
            try {
               synchronized (todo) {
                  if (todo.size() == 0) {
                     if (continueRunning) todo.wait();
                     else                 return;
                  }
               }
            } catch(Exception ee) {}
         }
      }
      
      synchronized public void startThread() {
         if (me == null) {
            me = new Thread(this);
            me.start();
         }
      }
      
      public void stopThread() {
         synchronized(todo) {
            if (me != null) {
               continueRunning = false;
               todo.notifyAll();
            }
         }
         if (me != null) {
            while(true) {
               try {
                  me.join();
                  break;
               } catch(InterruptedException ie) {}
            }
         }
      }
   
      public void sendAMTMail(User user, 
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
      
      public void sendAMTMail(User user, DboxPackageInfo pinfo, String name) {
         synchronized(todo) {
            todo.addElement(new AMTData(user, pinfo, name, false));
            startThread();
            todo.notifyAll();
         }
      }
      
      public void sendAMTMailRR(User user, DboxPackageInfo pinfo, String name) {
         synchronized(todo) {
            todo.addElement(new AMTData(user, pinfo, name, true));
            startThread();
            todo.notifyAll();
         }
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
                                 AMTUser amtuser) throws DboxException {      
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
         
         check = "%launchurl%";
         while(sub.find(body, check)) body = sub.substitute(FEHostURL+ESS+launchURL,
                                                            false);
         
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
         while(sub.find(body, check)) body = sub.substitute(sftpdropbox, false);
         
         check = "%tunneldropbox%";
         while(sub.find(body, check)) body = sub.substitute(tunneldropbox, false);
            
        // This is directed download
         check = "%htmldownloadinfo%";
         while(sub.find(body, check)) {
            body = sub.substitute(doHtml?htmlDownloadInfo:"", false);
         }
            
        // This is web dropbox
         check = "%webdownloadinfo%";
         while(sub.find(body, check)) {
            body = sub.substitute(doWebdropbox?webDownloadInfo:"", false);
         }
            
        // MUST be after webdownloadinfo
         check = "%weblaunchurl%";
         while(sub.find(body, check)) {
            body = sub.substitute(FEHostURL + ESS + weblaunchURL + ":p:" + 
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
                     if (doHtml && fsize <= 0x7fffffff) {
                        String scope = 
                           "webox:op:d:p:" +
                           pinfo.getPackageId() + ":f:" + finfo.getFileId();
                              
//                           str += "   " + szS + "\t " + 
//                              "<A href=" + ESS+downloadFileURL + scope + 
//                              ">" + fname + "</A>\n";

                        str += pad(szS, 15, false) + " " + fname + 
                           "\n" + pad(" ", 16) + "Web download: " + 
                           FEHostURL+ESS+downloadFileURL + scope + "\n\n\n";
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
      
      public void sendAMTMailNoThread(User user, 
                                      DboxPackageInfo pinfo, 
                                      String name, 
                                      boolean returnreceipt) {
         
         String emailaddr = "Not Found";
         try {
            
            String ucompany     = user.getCompany();
            String uuser        = user.getName();
            
            if (ucompany == null) ucompany = "";
            else                  ucompany = ucompany.trim();
            
            String body         = null;
            String lmailSubject = null;
            
           // This method does Notification emails as well as return receipt
            if (returnreceipt) {
            
              // Mail subject has User name/company, which is person
              //  who just finished download at this point
               lmailSubject = mailSubjectRR;
               
              // For RR, we are sending to Package Owner
               name = pinfo.getPackageOwner();
               
               body = mailBodyRR;
               
              // Set u-variable to be package owner now
               ucompany = pinfo.getPackageCompany();
               uuser    = pinfo.getPackageOwner();
               
            } else {
            
              // u-vars already set to package owner (thats who User is here)
               lmailSubject = mailSubject;
              
               body = mailBody;
            }
            
           // Get user record, no entitlements or projects needed
            AMTUser amtuser = null;
            
           // n vars will be sendto id (recipient of package)
            String ncompany = null;
            String nuser    = null;
            
            
           // This first part is for unit testing
            if (fakeamt) {
               amtuser = new AMTUser();
               amtuser.setEdgeUser("euser");
               amtuser.setUser(name);
               amtuser.setEmail(nuser="fakeamtuser@fakecompany.com");
               amtuser.setCompany(ncompany="fakecompany.com");
            } else {
               Vector amtusers = AMTQuery.getAMTByUser(name, false, false, null);
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
                           nuser = user.getName();
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
            
            emailaddr = amtuser.getEmail();
                        
           // Ok, if we make it here, u-vars should be package owner,
           //     and n-vars should be package recipient in question.
            body = substituteVariables(body, pinfo, 
                                       uuser, ucompany,
                                       nuser, ncompany,
                                       amtuser);
            
            lmailSubject = substituteVariables(lmailSubject, pinfo, 
                                               uuser, ucompany,
                                               nuser, ncompany,
                                               amtuser);
            
            
            if (!doemailsend) {
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
                  oem.edge.ed.sd.ordproc.Mailer.sendMail (smtpserver,
                                                          replyAddr, 
                                                          emailaddr, null,
                                                          null, null,
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
               log.error("Error sending Dropbox " +
                         (returnreceipt?"ReturnReceipt ":"Available ") + 
                         "mail to " + emailaddr + " from " + user.getName());
               log.error(body);
               DboxAlert.alert(2, "Error Sending Dbox Notification tries: " + tries,
                               0, body, (Exception)savedExceptions.firstElement());
            } else if (tries > 1) {
               log.error("Finally sent Dropbox (tries=" + tries + ") package" +
                         (returnreceipt?"ReturnReceipt ":"Available ") + 
                         "mail to " + emailaddr + " from " + user.getName());
               DboxAlert.alert(4, "Notification worked on subsequent tries: " + tries,
                               0, emailaddr + " from " + user.getName(),
                               (Exception)savedExceptions.firstElement());
            }
            
         } catch(Throwable tt) {
            log.error("Error sending Dropbox " +
                      (returnreceipt?"ReturnReceipt ":"Available ") + 
                      "mail to " + emailaddr + " from " + user.getName());
            tt.printStackTrace();
            DboxAlert.alert(tt);
         }
      }
   }
   
      
  // Server Socket support
   public void run() {
      
      ServerSocket socket = null;
      for (int i=0; i<100; i++) {
         try {
            socket = new ServerSocket(localport);
            localport = socket.getLocalPort();
            break;
         } catch (IOException ioe) {
            localport++;
         }
      }
   
      log.info("Accept thread started. Listening on port " + localport);
                         
     //httpServer.startServer(8080, localport);
                         
      while (true) {
         try {
            Socket sock = socket.accept();
            if (sock == null) {
               log.fatal("DSMPServer: Got a NULL Socket Done!"); 
               break;
            }	
            
            if (secureMode) {
              // SSLize it as server and needClientAuth
               log.info("Securing Socket");
               sock = SSLizer.sslizeSocket(sock, true, true, ksi, tsi);
            }
            DSMPBaseHandler handler = new DSMPSocketHandler(sock, this);
            handlers.put(new Integer(handler.getHandlerId()), handler);
                
         } catch (Exception e){			
            log.error("Error accepting/starting dropbox connection");
            log.error(e);
            e.printStackTrace(System.out);
         }
      }
      
      try {socket.close();} catch(Throwable tt) {}
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
         
         Hashtable hash = SearchEtc.dataFromToken((ODCipherRSA)cipher, token);
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
                  
                 // PW == "" means can't login with Userid/PW
                 // JMC 9/29/03 ... BUG ... caused all users to have "" proj
                 // projs.addElement(""); 
                  
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
      } catch(Exception ee) {
         error = "Error parsing token";
      }
      
      if (error != null) {
         throw new Exception(error);
      }
      
      return ret;
   }
   
   public boolean parseArgs(String args[]) {
   
     // All prints MUST happen via stderr in here as in Daemon mode we will 
     //  print something to the client that is NOT protocol.
     
     // Just ditch stdout. Have everything we print go to stderr. If this
     //  is a daemonstartup, the startDropbox script will turn off stderr
     //  ... the user should then use the -stderr switch to put it somewhere.
      OutputStream outAtStart = System.out;
      InputStream  inAtStart  = System.in;
      
      
      System.setOut(System.err);
      
      boolean testlog4j = false;
     
      long maxexpire   = -1;
      long maxcompsize = DboxFileAllocator.getMaxComponentSize();
      DboxFileAllocator alloct = null;
      int allocpolicy = DboxFileAllocator.ALLOCATION_PRIORITY;
                        
      for(int i=0; i < args.length; i++) {
         if (args[i].equalsIgnoreCase("-port")) {
            try {
               localport = Integer.parseInt(args[++i]);
            } catch(Throwable tt) {
               System.err.println("-port take an integer value");
               return false;
            }
         } else if (args[i].equalsIgnoreCase("-stderr")) {
            try {
               System.setErr(new PrintStream(new FileOutputStream(args[++i])));
               System.err.println("Stderr = " + args[i]);
            } catch(IOException ee) {}
         } else if (args[i].equalsIgnoreCase("-config")) {
            String configpath = args[++i];
         } else if (args[i].equalsIgnoreCase("-grid")) {
            String toplevel = args[++i];
            packageMgr = new GridPackageManager(alloct=
               new GridFileAllocator(toplevel, 0, 0, (byte)0), toplevel);
         } else if (args[i].equalsIgnoreCase("-testlog4j")) {
            testlog4j=true;
         } else if (args[i].equalsIgnoreCase("-runAfterCleanup")) {
            keeprunning = true;
         } else if (args[i].equalsIgnoreCase("-maxComponentSize")) {
            try {
               maxcompsize = Long.parseLong(args[++i]);
            } catch(Throwable tt) {
               System.err.println("-maxComponentSize take a Long value");
               return false;
            }                       
         } else if (args[i].equalsIgnoreCase("-forcedebugdir")) {
            forcedropboxpath = args[++i];
         } else if (args[i].equalsIgnoreCase("-tunneldropbox")) {
            tunneldropbox = args[++i];
         } else if (args[i].equalsIgnoreCase("-htmldownloadinfo")) {
            htmlDownloadInfo = args[++i];
         } else if (args[i].equalsIgnoreCase("-webdownloadinfo")) {
            webDownloadInfo = args[++i];
         } else if (args[i].equalsIgnoreCase("-sftpdropbox")) {
            sftpdropbox   = args[++i];
         } else if (args[i].equalsIgnoreCase("-smtpserver")) {
            smtpserver    = args[++i];
         } else if (args[i].equalsIgnoreCase("-replyaddr")) {
            replyAddr     = args[++i];
         } else if (args[i].equalsIgnoreCase("-mailsubject")) {
            mailSubject   = args[++i];
         } else if (args[i].equalsIgnoreCase("-mailsubjectRR")) {
            mailSubjectRR = args[++i];
         } else if (args[i].equalsIgnoreCase("-FEHostURL")) {
            FEHostURL     = args[++i];
         } else if (args[i].equalsIgnoreCase("-FEEdesignServlet")) {
            ESS           = args[++i];
         } else if (args[i].equalsIgnoreCase("-launchurl")) {
            launchURL     = args[++i];
         } else if (args[i].equalsIgnoreCase("-weblaunchurl")) {
            weblaunchURL  = args[++i];
         } else if (args[i].equalsIgnoreCase("-mailbody")) {
            try {
               BufferedReader bf = 
                  new BufferedReader(new FileReader(args[++i]));
               mailBody = "";
               String str;
               while((str=bf.readLine()) != null) {
                  mailBody += str + "\n";
               }
            } catch(IOException ioe) {
               System.err.println("-mailbody: Error opening/loading " + 
                                  args[i]);
               return false;
            }
         } else if (args[i].equalsIgnoreCase("-mailbodyRR")) {
            try {
               BufferedReader bf = 
                  new BufferedReader(new FileReader(args[++i]));
               mailBodyRR = "";
               String str;
               while((str=bf.readLine()) != null) {
                  mailBodyRR += str + "\n";
               }
            } catch(IOException ioe) {
               System.err.println("-mailbodyRR: Error opening/loading " + 
                                  args[i]);
               return false;
            }
            
         } else if (args[i].equalsIgnoreCase("-threadedClose")) {
            ComponentOutputStream.makeThreaded = true;
         } else if (args[i].equalsIgnoreCase("-nonthreadedClose")) {
            ComponentOutputStream.makeThreaded = false;
         } else if (args[i].equalsIgnoreCase("-forbiddenChars")) {
            forbiddenChars = args[++i];
         } else if (args[i].equalsIgnoreCase("-maxExpirationDays")) {
            try {
               maxexpire = Long.parseLong(args[++i]);
               if (maxexpire < 1) throw new Exception("");
            } catch(Throwable tt) {
               System.err.println("-maxExpirationDays take a positive value");
               return false;
            }
         } else if (args[i].equalsIgnoreCase("-setAllocationPolicy")) {
            String policy = args[++i];
            if (policy.equalsIgnoreCase("balanced")) {
               allocpolicy = DboxFileAllocator.ALLOCATION_BALANCED;
            } else if (policy.equalsIgnoreCase("priority")) {
               allocpolicy = DboxFileAllocator.ALLOCATION_PRIORITY;
            } else {
               System.err.println("DropboxServer: Invalid Allocation policy " + 
                                  policy);
               return false;
            }
         } else if (args[i].equalsIgnoreCase("-debug")) {
            setDebug(true);
            Debug.setDebug(true);
            DebugPrint.setLevel(DebugPrint.DEBUG3);
         } else if (args[i].equalsIgnoreCase("-nodebug")) {
            setDebug(false);
            Debug.setDebug(false);
            DebugPrint.setLevel(DebugPrint.INFO);
         } else if (args[i].equalsIgnoreCase("-setSendPolicy")) {
            try {
               int policy = Integer.parseInt(args[++i]);
               policy_supportSameCompany = false;
               policy_supportIBM         = false;
               policy_supportLookup      = false;
               switch(policy) {
                  case 0:
                     break;
                  case 1:
                     policy_supportIBM         = true;
                     break;
                  case 2:
                     policy_supportSameCompany = true;
                     break;
                  case 3:
                     policy_supportSameCompany = true;
                     policy_supportIBM         = true;
                     break;
                  case 4:
                     policy_supportLookup      = true;
                     break;
                  case 5:
                     policy_supportIBM         = true;
                     policy_supportLookup      = true;
                     break;
                  case 6:
                     policy_supportSameCompany = true;
                     policy_supportLookup      = true;
                     break;
                  case 7:
                     policy_supportSameCompany = true;
                     policy_supportIBM         = true;
                     policy_supportLookup      = true;
                     break;
                  default:
                     System.err.println(
                        "DropboxServer: -setSendPolicy takes a number 0-7");
                     return false;
               }
               if (policy < 0 || policy > 7) {
                  System.err.println(
                     "DropboxServer: -setSendPolicy takes a number 0-7");
                  return false;
               }
               
               
            } catch (NumberFormatException ne) {
               System.err.println(
                  "DropboxServer: -setSendPolicy takes a number 0-7");
               return false;
            }
         } else if (args[i].equalsIgnoreCase("-db2")) {
            DBConnection conn = new DBConnLocalPool();
            conn.setDriver     (args[++i]);
            conn.setURL        (args[++i]);
            conn.setInstance   (args[++i]);
            conn.setPasswordDir(args[++i]);
            
           // Refactored the code to share Groups code ... use a different
           //  search qualifier to find the same DBConnection object
            DBSource.addDBConnection("dropbox", conn, false);
            DBSource.addDBConnection("GROUPS", conn, false);
            alloct=new DB2DboxFileAllocator();
            packageMgr = new DB2PackageManager(alloct);
         } else if (args[i].equalsIgnoreCase("-amtdb")) {
            DBConnection conn = new DBConnLocalPool();
            conn.setDriver     (args[++i]);
            conn.setURL        (args[++i]);
            conn.setInstance   (args[++i]);
            conn.setPasswordDir(args[++i]);
            DBSource.addDBConnection("AMT", conn, false);
         } else if (args[i].equalsIgnoreCase("-trustSizes")) {
            DB2PackageManager.setTrustSizes(true);
         } else if (args[i].equalsIgnoreCase("-notrustSizes")) {
            DB2PackageManager.setTrustSizes(false);
         } else if (args[i].equalsIgnoreCase("-projectsusemasterfse")) {
            AMTQuery.includeMasterFSE = true;
         } else if (args[i].equalsIgnoreCase("-noprojectsusemasterfse")) {
            AMTQuery.includeMasterFSE = false;
         } else if (args[i].equalsIgnoreCase("-projectsusefseadmin")) {
            AMTQuery.includeFSEAdmin = true;
         } else if (args[i].equalsIgnoreCase("-noprojectsusefseadmin")) {
            AMTQuery.includeFSEAdmin = false;
         } else if (args[i].equalsIgnoreCase("-limitsendbyproject")) {
            try {
              // Have it watch for changes
               limitSendByProject = new ReloadingProperty(args[++i]);
               limitSendByProject.setReloadFile(args[i]);
            } catch(IOException ioe) {
               System.err.println("-limitsendbyproject IOException: " + 
                                  args[i]);
               return false;
            }
         } else if (args[i].equalsIgnoreCase("-ROURString")) {
            DB2PackageManager.rour = args[++i];
         } else if (args[i].equalsIgnoreCase("-ROURSubselectString")) {
            DB2PackageManager.rourss = args[++i];
         } else if (args[i].equalsIgnoreCase("-doROUR")) {
            DB2PackageManager.doROUR = true;
         } else if (args[i].equalsIgnoreCase("-doROURSubselect")) {
            DB2PackageManager.doROURSubselect = true;
         } else if (args[i].equalsIgnoreCase("-noROUR")) {
            DB2PackageManager.doROUR = false;
         } else if (args[i].equalsIgnoreCase("-noROURSubselect")) {
            DB2PackageManager.doROURSubselect = false;
         } else if (args[i].equalsIgnoreCase("-useHtml")) {
            doHtml = true;
         } else if (args[i].equalsIgnoreCase("-noHtml")) {
            doHtml = false;
         } else if (args[i].equalsIgnoreCase("-useWebdropbox")) {
            doWebdropbox = true;
         } else if (args[i].equalsIgnoreCase("-noWebdropbox")) {
            doWebdropbox = false;
         } else if (args[i].equalsIgnoreCase("-webDropboxURL")) {
            weblaunchURL = args[++i];
         } else if (args[i].equalsIgnoreCase("-downloadFileURL")) {
            downloadFileURL = args[++i];
         } else if (args[i].equalsIgnoreCase("-FEHostURL")) {
            FEHostURL    = args[++i];
         } else if (args[i].equalsIgnoreCase("-amtchecks")) {
            doamtchecking = true;
         } else if (args[i].equalsIgnoreCase("-noamtchecks")) {
            doamtchecking = false;
         } else if (args[i].equalsIgnoreCase("-amtgroupchecks")) {
            doGroupAMT = true;
         } else if (args[i].equalsIgnoreCase("-noamtgroupchecks")) {
            doGroupAMT = false;
         } else if (args[i].equalsIgnoreCase("-amtcomplainnoexist")) {
            complainReceiverNoExist = true;
         } else if (args[i].equalsIgnoreCase("-noamtcomplainnoexist")) {
            complainReceiverNoExist = false;
         } else if (args[i].equalsIgnoreCase("-tryudslush")) {
            tryUDSlush = true;
         } else if (args[i].equalsIgnoreCase("-notryudslush")) {
            tryUDSlush = false;
         } else if (args[i].equalsIgnoreCase("-allowudslush")) {
            allowUDSlush = true;
         } else if (args[i].equalsIgnoreCase("-noallowudslush")) {
            allowUDSlush = false;
         } else if (args[i].equalsIgnoreCase("-udslushfile")) {
            try {
              // Have it watch for changes
               udslushProp = new ReloadingProperty(args[++i]);
               udslushProp.setReloadFile(args[i]);
            } catch(IOException ioe) {
               System.err.println("-udslushfile: Error opening/loading " + 
                                  args[i]);
               return false;
            }
         } else if (args[i].equalsIgnoreCase("-udnote")) {
            UDNote = args[++i];
         } else if (args[i].equalsIgnoreCase("-amtallowbyemail")) {
            allowByEmail = true;
         } else if (args[i].equalsIgnoreCase("-noamtallowbyemail")) {
            allowByEmail = false;
         } else if (args[i].equalsIgnoreCase("-byEmailtoIBMONLY")) {
            byEmailToIBMONLY = true; 
         } else if (args[i].equalsIgnoreCase("-nobyEmailtoIBMONLY")) {
            byEmailToIBMONLY = false; 
         } else if (args[i].equalsIgnoreCase("-amtprojects")) {
            doamtprojects = true;
         } else if (args[i].equalsIgnoreCase("-noamtprojects")) {
            doamtprojects = false;
         } else if (args[i].equalsIgnoreCase("-sizechecks")) {
            ComponentOutputStream.doSizeCheck = true;
         } else if (args[i].equalsIgnoreCase("-nosizechecks")) {
            ComponentOutputStream.doSizeCheck = false;
         } else if (args[i].equalsIgnoreCase("-amtmailing")) {
            doamtmailing = true;
         } else if (args[i].equalsIgnoreCase("-emailsend")) {
            doemailsend = true;
         } else if (args[i].equalsIgnoreCase("-noemailsend")) {
            doemailsend = false;
         } else if (args[i].equalsIgnoreCase("-cleanPackages")) {
            cleanPackages = true;
         } else if (args[i].equalsIgnoreCase("-cleanFiles")) {
            cleanFiles = true;
         } else if (args[i].equalsIgnoreCase("-cleanAll")) {
            cleanPackages = true;
            cleanFiles = true;
         } else if (args[i].equalsIgnoreCase("-usetriggers")) {
            usetriggers = true;
         } else if (args[i].equalsIgnoreCase("-notriggers")) {
            usetriggers = false;
         } else if (args[i].equalsIgnoreCase("-testemail")) {         
            doemailsend = true;
            fakeamt     = true;
         } else if (args[i].equalsIgnoreCase("-klogTimeoutSec")) {
            
            try {
              // klogTimeout is number of secs
               klogTimeout = Long.parseLong(args[++i]) * 1000;
            } catch (NumberFormatException ne) {
               System.err.println("DropboxServer: Bad format for klogTimeout ... Exiting");
               exit(4);
            }
         } else if (args[i].equalsIgnoreCase("-klogTimeoutMin")) {
            
            try {
              // klogTimeout is number of Mins
               klogTimeout = Long.parseLong(args[++i]) * 60 * 1000;
            } catch (NumberFormatException ne) {
               System.err.println("DropboxServer: Bad format for klogTimeout ... Exiting");
               exit(4);
            }
         } else if (args[i].equalsIgnoreCase("-klogTimeout")) {
            
            try {
              // klogTimeout is number of Hours
               klogTimeout = Long.parseLong(args[++i]) * 60 * 60 *1000;
            } catch (NumberFormatException ne) {
               System.err.println("DropboxServer: Bad format for klogTimeout ... Exiting");
               exit(4);
            }
         } else if (args[i].equalsIgnoreCase("-kloginfo")) {
            
            klogCell = args[++i];
            klogPWDir = args[++i];
            
            klogUserid   = PasswordUtils.getPassword(klogPWDir+"/._afsd435");
            klogPassword = PasswordUtils.getPassword(klogPWDir+"/._afsde7e");
            if (!reauthenticate()) {
               System.err.println("DropboxServer: Cannot klog ... Exitting");
               return false;
            }
            
           // Each klogTimeout millisefcs, try to get new token, 
           //  if error, try every 1/10 of full wait
            TimeoutManager tmgr = TimeoutManager.getGlobalManager();
            tmgr.addTimeout(new KlogTimeout(klogTimeout, klogTimeout/10, false));
            
         } else if (args[i].equalsIgnoreCase("-log4jpropfile")) {
            log4jpropfile = args[++i];
         } else if (args[i].equalsIgnoreCase("-loginbanner")) {
            dropboxBanner = args[++i];
         } else if (args[i].equalsIgnoreCase("-allocationInfo")) {
            allocationInfo = true;
         } else if (args[i].equalsIgnoreCase("-daemonStartup")) {
            if (!daemonMode) {
               
               try {
                  String outname = "/dev/null";
                  System.setIn(new ByteArrayInputStream(new byte[1]));
//               System.setOut(new PrintStream(new FileOutputStream(outname)));
                  System.setOut(System.err);
               } catch(Exception eee) {
                  System.err.println("Error setting up stream info");
                  eee.printStackTrace(System.err);
               }
               
               daemonMode = true;
            }
            
         } else if (args[i].equalsIgnoreCase("-secureMode")) {
            secureMode = true;
         } else if (args[i].equalsIgnoreCase("-noSecureMode")) {
            secureMode = false;
         } else if (args[i].equalsIgnoreCase("-truststore")) {
            String storefile = args[++i];
            String storepw   = args[++i];
            System.out.println("Load trust store: " + storefile);
            tsi = new KeystoreInfo(storefile, storepw);
         } else if (args[i].equalsIgnoreCase("-keystore")) {
            String storefile = args[++i];
            String storepw   = args[++i];
            System.out.println("Load key store: " + storefile);
            ksi = new KeystoreInfo(storefile, storepw);
            
         } else if (args[i].equalsIgnoreCase("-tokencipher")) {
         
           // Supporting token login ... get reflection setup
            try {
            
              /*
               Class dsrvclass = Class.forName(
                  "oem.edge.ed.odc.cntl.DesktopServlet");
               Class hashclass   = Class.forName("java.util.Hashtable");
               Class stringclass = Class.forName("java.lang.String");
               Class cipherclass = Class.forName("oem.edge.common.cipher.ODCipherRSA");
               
               Class classparms[] = new Class[1];
               classparms[0] = stringclass;
               java.lang.reflect.Method loadmeth = 
                  dsrvclass.getMethod("loadCipherFile", classparms);
              */  
              
              // Only ever expect local cipher file
               String cipherFile     = args[++i];
              // String edgecipherFile = args[++i];
               
              /*
                String parmarr[] = new String[1];
                parmarr[0] = cipherFile;
                cipher = loadmeth.invoke(null, parmarr);
               
              //parmarr[0] = edgecipherFile;
              //edgecipher = loadmeth.invoke(null, parmarr);
              */
              
               cipher = SearchEtc.loadCipherFile(cipherFile);
              
               
               if (cipher != null/* && edgecipher != null*/) {
                 /*
                  classparms    = new Class[2];
                  classparms[0] = cipherclass;
                  classparms[1] = stringclass;
                  dataFromToken = dsrvclass.getMethod("dataFromToken", 
                                                      classparms);
                  if (dataFromToken == null) {
                     System.err.println(
                        "-tokencipher failed! Error finding dataFromToken");
                     cipher = null;
                  }
                 */
                  ;
               } else {
                  cipher = null;
                  System.err.println(
                     "-tokencipher failed! Error loading cipherfile: " + 
                     cipherFile/* + ", " + edgecipherFile*/);
               }
            } catch(Exception ee) {
               System.err.println("-tokencipher failed! " + ee.toString());
            }
         } else if (args[i].equalsIgnoreCase("-userpw")) {
            userpwFile = args[++i];
            if (!refreshUserPW()) {
            
               System.err.println("Error processing -userpw option");
               return false;
            }
         } else {
            System.err.println(args[i] + ": unknown option\n\nUsage:\n" +
                               "DropboxServer [-port portno]\n" + 
                               "              [-userpw file]\n" +
                               "              [-tokencipher cipherfile]\n" + 
                               "              [-setSendPolicy <0-15>]\n"   +
                               "                {bitmask ALLOWSEND=4|SAMECOMPANY=2|IBM=1}]\n" + 
                               "              [-setAllocationPolicy <balanced|priority>]\n" + 
                               "              [-db2 driver url instance pwdir]\n" +
                               "              [-stderr filename]\n" +
                               "              [-daemonStartup]\n" +
                               "              [-secureMode]\n" +
                               "              [-forcedebugdir <dir>]\n" +
                               "              [-keystore   <ksfile> <kspw>\n" +
                               "              [-truststore <tsfile> <tspw>\n" +
                               "              [-testlog4j]\n" +
                               "              [-maxComponentSize <size>]\n" +
                               "              [-maxExpirationDays <days>]\n" +
                               "              [-forbiddenChars \"chars\"]\n" +
                               "              [-debug]\n" +
                               "              [-nodebug]\n" +
                               "              [-trustsizes]\n" +
                               "              [-notrustsizes]\n" +
                               "             --- AMT/DECAF options ---\n" + 
                               "              [-amtdb driver url instance pwdir]\n" +
                               "              [-amtchecks]\n" +
                               "              [-amtprojects]\n" +
                               "              [-amtgroupchecks]\n" +
                               "              [-amtmailing]\n" +
                               "              [-noamtchecks]\n" +
                               "              [-noamtprojects]\n" +
                               "              [-noamtgroupchecks]\n" +
                               "              [-noamtmailing]\n" +
                               "              [-amtcomplainnoexist]\n" +
                               "              [-amtallowbyemail]\n" +
                               "              [-byEmailtoIBMONLY]\n" +
                               "              [-noamtcomplainnoexist]\n" +
                               "              [-noamtallowbyemail]\n" +
                               "              [-projectsusemasterfse]\n" +
                               "              [-noprojectsusemasterfse]\n" +
                               "              [-projectsusefseadmin]\n" +
                               "              [-noprojectsusefseadmin]\n" +
                               "              [-limitsendbyproject]\n" +
                               "              [-nobyEmailtoIBMONLY]\n" +
                               "              [-tryUDSlush   | -notryUDSlush]\n" +
                               "              [-allowUDSlush | -noallowUDSlush]\n" +
                               "              [-udslushConfig configfile]\n" +
                               "              [-udnote noteForAclFail]\n" +
                               "             --- Mail fmt options ---\n" + 
                               "              [-tunneldropbox val]\n" + 
                               "              [-sftpdropbox val]\n" +
                               "              [-smtpserver val]\n" +
                               "              [-replyaddr val]\n" +
                               "              [-launchurl val]\n" +
                               "              [-mailbody filename]\n\n" +
                               "              [-mailsubject val]\n" +
                               "              [-mailbodyRR filename]\n\n" +
                               "              [-mailsubjectRR val]\n" +
                               "              [-useHTML]\n" +
                               "              [-noHTML (default)\n" +
                               "              [-htmldownloadinfo val]\n" + 
                               "              [-FEEdesignServlet <URL>]\n" +
                               "              [-downloadFileURL <URL>]\n" +
                               "              [-usewebdropbox (default)]\n" +
                               "              [-noWebDropbox\n" +
                               "              [-webdownloadinfo val]\n" + 
                               "              [-webDropboxURL <URL>]\n" +
                               "              [-FEHostURL <URL>]\n" +
                               "             --- cleaning options ---\n" + 
                               "              [-cleanPackages]\n" +
                               "              [-cleanFiles]\n" +
                               "              [-cleanAll]\n" +
                               "              [-usetriggers | -notriggers]\n" +
                               "              [-sizechecks | -nosizechecks]\n"+
                               "              [-threadedClose | nonthreadedClose]\n"+
                               "             --- ReadOnly UR options ---\n" + 
                               "              [-doROUR]\n" +
                               "              [-doROURSubselect]\n" +
                               "              [-noROUR]\n" +
                               "              [-noROURSubselect]\n" +
                               "              [-ROURString <sqlstr>]\n" +
                               "              [-ROURSubselectString <sqlstr>]\n" +
                               "             --- Misc options ---\n" + 
                               "              [-runAfterCleanup]\n" +
                               "              [-log4jpropfile <propfile>]\n" +
                               "              [-loginbanner  <bannerfile>]\n" +
                               "              [-allocationInfo]\n" +
                               "              [-klogTimeout <hrs>]\n" +
                               "              [-kloginfo userpwDir]  (user=._afsd435/..., pw=._afsde7e/...)");
            return false;
         }
      }
      
     // Allocate the Logger
      String f = null;
      try { 
         f = SearchEtc.findFileInClasspath(log4jpropfile);
      } catch(IOException ioe) {
      }
      if (f == null) f = log4jpropfile;
      PropertyConfigurator.configure(f);
      log = Logger.getLogger(getClass().getName());
      
      if (packageMgr == null) {
         packageMgr = new LPackageManager(alloct=new LDboxFileAllocator()); 
      }
      
      if (maxexpire > 0) packageMgr.setMaxExpireDays(maxexpire);
      
      try {
         alloct.setAllocationPolicy(allocpolicy);
         alloct.setMaxComponentSize(maxcompsize);
      } catch(Exception ee) {
         ee.printStackTrace(System.err);
         return false;
      }
      
      
     // Unit test email substitution
      if (fakeamt) {
         User testuser = new User("sendername@sendercompany.com", 1);
         testuser.setCompany("sendercompany");
         testuser.setCountry("US");
         
         DboxPackageInfo pinfo = new LDboxPackageInfo((PackageManager)null);
         pinfo.setPackageName("this is the package name");
         pinfo.setPackageOwner("sendername@sendercompany.com");
         pinfo.setPackageCompany("sendercompany");
         DboxFileInfo finfo = new LDboxFileInfo(null);
         finfo.setFileStatus((byte)10);
         finfo.setFileCreation(new Date().getTime()-(84000*1000));
         finfo.setFileExpiration(new Date().getTime());
         finfo.setFileSize(45739487);
         finfo.setFileName("This_is_the_file.yep");
         finfo.setFileId(23);
         finfo.setFileMD5("234874623876287638762387674");
         try {
            pinfo.addFile(finfo);
         } catch(Exception ee) {}
         
         AMTMailer m = new AMTMailer();
         m.sendAMTMailNoThread(testuser, pinfo, "thisisthename@thecompany.com",
                               false);
         
         m.sendAMTMailNoThread(testuser, pinfo, "thisisthename@thecompany.com",
                               true);
         
         exit(4);
      }
         
      if ((doamtchecking || doamtmailing)) {
         if (DBSource.getDBConnection("AMT") == null) {
            String err = "Specified doamtchecks/doamtmailing, and not -amtdb";
            log.fatal(err);
            System.err.println(err);
            return false;
         }
      }
            
      if (testlog4j) {
         log.fatal("Fatal log test");
         log.warn("Warn log test");
         log.info("Info log test");
         log.debug("Debug log test");
         DboxAlert.alert(DboxAlert.SEV1, "Test Alert", 0, 
                         "This is a test of the national Dropbox Alert system");
      }
      
     // Have to defer this to here or the Handler gets legs too soon.
      if (daemonMode) {
         
         if (secureMode) {
            
            try {
               System.out.println("Doing Secure dropbox. Get socketpair");
               SocketPair sp = new SocketPair();
               System.out.println("Get Allocated Socket");
               Socket s1 = sp.getServerAllocatedSocket();
               System.out.println("Get Connected Socket");
               Socket s2 = sp.getConnectingSocket();
               System.out.println("Link them");
               new ShuttleService(inAtStart, s1).start();
               new ShuttleService(s1, outAtStart).start();
               
               sp.decouple();
               
              // SSLize it as server and needClientAuth
               Socket sock = SSLizer.sslizeSocket(s2, true, true, ksi, tsi);
               
               DSMPSocketHandler hand = new DSMPSocketHandler(sock, this);
            } catch(Exception ee) {
               log.error("Error starting dropbox connection");
               log.error(ee);
               ee.printStackTrace(System.out);
               return false;
            }
         } else {
            DSMPBaseHandler hand = new DSMPBaseHandler(this);
            hand.setInputOutput(inAtStart, outAtStart);
         }
      }
      
      return true;
   }
   
   synchronized boolean refreshUserPW() {
      
      if (userpwFile == null) {
         System.err.println("refreshUserPW: File is NULL!");
         return false;
      }
      
      try {
         
         File file = new File(userpwFile);
         if (file.lastModified() == userpwLastModified) {
            return false;
         }
         
         userpwLastModified = file.lastModified(); 
         System.err.println("Reloading userpw: " + (new Date()).toString());
         
         BufferedReader in = 
            new BufferedReader(new FileReader(userpwFile));
         String s;
         while((s=in.readLine()) != null) {
            s = s.trim();
            if (s.length() == 0 || s.startsWith("#")) {
               ;
            } else {
               StringTokenizer tokenizer = new StringTokenizer(s, ":");
               String user=tokenizer.nextToken();
               int idx = user.indexOf("!");
               String company = "IBM";
               if (idx > 0) {
                  company = user.substring(idx+1);
                  user    = user.substring(0, idx).toLowerCase();
               }
               
               Vector v    = new Vector();
               Vector oldv = (Vector)userpw.get("user");
               
               
               int ii=0;
               int cnt = 0;
               
               if (oldv == null) {
                  System.err.println("User = " + user + " Company = " + company);
                  cnt=1;
               }
               
               while(tokenizer.hasMoreTokens()) {
                  String tn=tokenizer.nextToken();
                  boolean doit = true;
                  if (ii > 0 && oldv != null) {
                     Enumeration e = oldv.elements();
                     e.nextElement(); // skip pw
                     e.nextElement(); // skip company
                     while(e.hasMoreElements()) {
                        String p = (String)e.nextElement();
                        if (p.equals(tn)) {
                           doit=false;
                           break;
                        }
                     }
                  }
                  v.addElement(tn);
                  if (doit) {
                     if (++cnt == 1) 
                        System.err.println("User = " + user + 
                                           " Company = " + company);
                     if (ii++ !=  0) System.err.println("   " + tn);
                  }
               }
               
               v.insertElementAt(company, 1);
               userpw.put(user, v);
            }
         }
      } catch( Exception ee) { return false; } 
      return true;
   }
   
      
   public static void main(String args[]) {
   
     // Protect ourselves ... we had a hang when a NullPointer Exception was
     //  thrown cause our klog thread was still running.
      DropboxServer srv = new DropboxServer();
      try {
         try {
            if (!srv.parseArgs(args)) {
               srv.exit(3);
            }
         } catch(Exception ee) {
            System.err.println("Exception occured during startup. Exit");
            ee.printStackTrace(System.err);
            srv.exit(3);
         }
         
         int exitval = 0;
         if (srv.cleanPackages) {
            try {
               srv.packageMgr.cleanExpiredPackages();
            } catch(DboxException ex) {
               srv.log.error("Error cleaning packages => " + ex.toString());
               ex.printStackTrace(System.err);
               exitval++;
            }
         }
         if (srv.cleanFiles) {
            try {
               srv.packageMgr.getFileManager().cleanUnreferencedFiles();
            } catch(DboxException ex) {
               srv.log.error("Error cleaning unreffedFiles => " + ex.toString());
               ex.printStackTrace(System.err);
               exitval++;
            }
         }
         if (srv.allocationInfo) {
            try {
               DboxFileAllocator falloc = 
                  srv.packageMgr.getFileManager().getFileAllocator();
               Vector v = falloc.getFileAreas();
               Enumeration enum = v.elements();
               System.out.println("\nFILEAREA!PRIORITY!FSTYPE!STATE!DIRECTORY!MAXSPACE!USEDSPACE");
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
                  System.out.println(sb.toString());
               }
            } catch(Exception ex) {
               srv.log.error("Error getting AlloctionInfo => " + ex.toString());
               ex.printStackTrace(System.err);
               exitval++;
            }
         }
         
         
         if ((srv.allocationInfo || 
              srv.cleanPackages  || 
              srv.cleanFiles) && !srv.keeprunning) {
            srv.log.info("Clean/AllocationInfo operation complete. Exit");
            srv.exit(exitval);
         }
         
         if (!srv.getDaemonMode()) {
            srv.log.info("DropboxServer Started: Not in DaemonMode, create Listening ear");
            Thread thread  = new Thread(srv, "DropboxServer");
            thread.start();
         } else {
            srv.log.info("DropboxServer Started in DaemonMode");
         }
      } catch(Exception bige) {
         try {
            System.err.println("Yow: DropboxServer got exception");
            bige.printStackTrace(System.err);
            srv.exit(4);
         } catch(Exception BIGE) {
            System.exit(4);
         }
      }
   }
   
   
  /* -------------------------------------------------------*\
  ** Work Routines
  \* -------------------------------------------------------*/
         
   private String buildReturnErrorMsg(String errmsg, String msg) {
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
  
  
   boolean sendProtoTo(DSMPBaseProto proto, int loginid) {
      DSMPBaseHandler handler = 
         (DSMPBaseHandler)handlers.get(new Integer(loginid));
      if (handler != null) {
         handler.sendProtocolPacket(proto);
         return true;
      }
      return false;
   }
  
   void addOperation(Operation op) {
      int id = op.getId();
      operations.put(new Integer(id), op);
   }
   
   Operation endOperation(int id) {
      Operation op   = (Operation)operations.remove(new Integer(id));
      if (op != null) {
         op.endOperation("Operation Aborted");
      }
      return op;
   }
   Operation getOperation(int id) {
      Operation op   = (Operation)operations.get(new Integer(id));
      return op;
   }
   
  // Called by Operation.endOperation
   public void operationComplete(Operation op) {
      operations.remove(new Integer(op.getId()));
      User user = getUser(op.getHandler().getHandlerId());
      if (user != null) {
         user.removeOperation(op.getId());
      }
   }

  /*
   Vector getUser(String u) {
      return (Vector)users.get(u);
   }
  */
  
   User getUser(int u) {
      User ret = (User)users.get(new Integer(u));
      
     // If reloadable prop for limiting project participants has changed, 
     //  fix up this User
     //
     // Note that the first time tryReload is called, it will indeed reload
     //  the file, so will come into this section to init things the first time
      if (ret != null && limitSendByProject != null && 
          limitSendByProject.tryReload()) {
      
        // To allow it to be used by some ... sigh
         String name = ret.getName();
         String canhe = limitSendByProject.getPropertyNoReload(name, "false");
        // If not limited, then he can use it
         if (canhe.equalsIgnoreCase("true")) {
            ret.setDoProjectSend(true);
         } else {
            ret.setDoProjectSend(false);
         }
      }
      
      return ret;
   }
   User getUserEx(int u) throws DboxException {
      User ret = getUser(u);
      if (ret == null) {
         throw new DboxException("The Specified user login id [" + u + 
                                 "] does not exist", 0);
      }
      return ret;
   }
   
   void addUser(User user) {
      synchronized(users) {
         users.put(new Integer(user.getLoginId()), user);
         Vector v = (Vector)users.get(user.getName());
         if (v == null) {
            v = new Vector();
            users.put(user.getName(), v);
         }
         v.addElement(user);
      }
   }
   
   boolean removeUser(int loginid) {
      boolean ret = false;
      
      synchronized (users) {
         User user = (User)users.remove(new Integer(loginid));
         if (user != null) {
            Vector v = user.getOperations();
            if (v != null) {
               Enumeration enum = ((Vector)v.clone()).elements();
               while(enum.hasMoreElements()) {
                  Operation op = (Operation)enum.nextElement();
                  endOperation(op.getId());
               }
            }
         
            v = (Vector)users.get(user.getName());
            if (v != null) {
               int idx = 0;
               Enumeration e = v.elements();
               while(e.hasMoreElements()) {
                  User tu = (User)e.nextElement();
                  if (tu.getLoginId() == loginid) {
                     v.removeElementAt(idx);
                  }
                  idx++;
               }
               if (v.size() == 0) {
                  users.remove(user.getName());
               }
            }
            
           // If we are in Token mode, remove userpw entry as well
            if (user.getTokenLogin()) {
               userpw.remove(user.getName());
            }
         }
      }
      
      return ret;
   }
   
   
   private void invalidProtocol(DSMPBaseHandler h, byte opcode) {
      
      log.fatal("Invalid or uncaught protocol for server: Opcode = " + opcode);
      log.fatal("Closing handler for ==> " + h.getHandlerId());
      h.shutdown();
   }
   
   private void validateLoggedIn(DSMPBaseHandler h, byte handle, 
                                 byte opcode) throws DboxException {
      if (!h.bitsSetInFlags(0x01)) {
         throw new DboxException("Connection not logged in", 0);
      }
   }
   
   private void exit(int v) {
      amtMailer.stopThread();
      if (log != null) {
         log.info("==== *EXITING* -- LOG DONE -- *EXITING* ====");
      }
      System.exit(v);
   }
   
   
  /* -------------------------------------------------------*\
  ** AMT variables and routines
  \* -------------------------------------------------------*/
   public Vector assertAMTChecks(User user, Vector acls) throws DboxException {
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
                  v = assertAMTCheck(user, ainfo.getAclName());
               }
            } else {
               if (!ret.contains(ainfo)) {
                  ret.addElement(ainfo);
               }
            }
         } else {
            v= assertAMTCheck(user, (String)obj);
         }
         
         if (v != null && v.size() > 0) {
            Enumeration enum2 = v.elements();
            while(enum2.hasMoreElements()) {
               String tob = (String)enum2.nextElement();
               AclInfo ainfo = new AclInfo(); 
               ainfo.setAclName(tob);
               
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
  //  non-null, just adding to that vec, otherwise, new vec
  // 
   public Vector flattenACLToUsers(Vector vecin, 
                                   AclInfo acl) throws DboxException {
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
            Vector newvec = AMTQuery.getUsersHavingProject(acl.getAclName());
            if (newvec != null) {
               Enumeration penum = newvec.elements();
               while(penum.hasMoreElements()) {
                  String member = (String)penum.nextElement();
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
   public Vector flattenACLsToUsers(Vector acls) throws DboxException {
      Vector ret = new Vector();
      Enumeration enum = acls.elements();
      while(enum.hasMoreElements()) {
         AclInfo acl = (AclInfo)enum.nextElement();
         flattenACLToUsers(ret, acl);
      }
      return ret;
   }
    
  // This assertion only takes affect if the AMT user being checked is 
  //  actually found 
   public Vector assertAMTCheck(User user, String name) throws DboxException {
      Vector ret = null;
      try {
         boolean didByEmail = false;
         
        // Get user record, no entitlements or projects needed
         Vector amtusers = AMTQuery.getAMTByUser(name);
         if ((amtusers == null || amtusers.size() == 0)) {
            
            if (allowByEmail && name.indexOf('@') >= 0) {
               amtusers = AMTQuery.getAMTByEmail(name);
               didByEmail = true;
            } 

            if ((amtusers == null || amtusers.size() == 0) && tryUDSlush) {
               if (udslushProp != null) { 
                  String newname = udslushProp.getProperty(name);
                  if (newname != null) {
                     amtusers = AMTQuery.getAMTByUser(newname);
                  }
               } else {
                  amtusers = AMTQuery.getAMTByUser(name + "@us.ibm.com");
               }
               if (amtusers != null && amtusers.size() > 0) {
                  AMTUser amtuser = (AMTUser)amtusers.elementAt(0);
                  StringBuffer sb = new StringBuffer("The following user:\n\n");
                  sb.append(Nester.nest(user.toString()));
                  sb.append("\n\nhas used the non-UD name '").append(name);
                  sb.append("' to address a package. There is a matching UD id:\n\n");
                  sb.append(Nester.nest(amtuser.toString()));
                  sb.append("\n\n");
                  
                  if (allowUDSlush) {
                     sb.append("The UD name will be used for the rest of the checks");
                  } else {
                     sb.append("The UD name will NOT be used. Please contact the\n");
                     sb.append(" user in question and let them know to use the UD id");
                     amtusers = null;
                  }
                  
                  String str = sb.toString();
                  DboxAlert.alert(3, "non-UD name used for Dropbox", 0, str);
               }
            }
         }
         
         if (amtusers == null || amtusers.size() == 0) {
            if (complainReceiverNoExist) {
               throw new DboxException("Not valid ACL: " + name, 0);
            }
            return null;
         }
         
         String ucompany = user.getCompany();
         String uuser    = user.getName();
         
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
               if (byEmailToIBMONLY && !ncompany.equals("IBM")) {
                  log.error("byEmail lookup for " + name + 
                            " mapped to non-IBM ID:\n" + amtuser.toString());
               } else {
                  log.info("byEmail lookup for " + name + 
                           " mapped to ID:\n" + amtuser.toString());
               }
               continue;
            }
            
            if (packageMgr.allowsPackageReceipt(uuser, ucompany, 
                                                nuser, ncompany)){
               ok = true;
               if (ret == null) ret = new Vector();
               if (!ret.contains(nuser)) ret.addElement(nuser);
            }
         }
         
         if (!ok) {
            throw new DboxException("Not valid ACL: " + name, 0);
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
   
   
  /* -------------------------------------------------------*\
  ** Shutdown, uncaughtProtocol callback
  \* -------------------------------------------------------*/
   public void fireShutdownEvent(DSMPBaseHandler hand) {
      log.info("GotFireShutdownEvent for connection = " + hand);
      
      try {
         User user = getUser(hand.getHandlerId());
         if (user != null) {
            packageMgr.closeSession(user, hand);
         }
      } catch(Exception ee) {
         log.error("Error doing closeSession");
         log.error(ee);
      }
      
      removeUser(hand.getHandlerId());
      handlers.remove(new Integer(hand.getHandlerId()));
      if (daemonMode) {
         exit(2);
      }
   }
   
   public void uncaughtProtocol(DSMPBaseHandler h, byte opcode) {
      invalidProtocol(h, opcode);
   }
   
  // Utility routine to find banner file, and read it in ... return string
   public String getBanner() {
      String banner = "";
      BufferedReader br = null;
      try { 
         String f = SearchEtc.findFileInClasspath(dropboxBanner);
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
            DebugPrint.printlnd(DebugPrint.WARN, "Banner file not found: " + 
                                dropboxBanner);
         }
      } catch(FileNotFoundException fnfe) {
         DebugPrint.printlnd(DebugPrint.WARN, 
                             "Attempt to access banner failed: Banner not found"
                             + ":\n" + dropboxBanner);
      } catch(Exception ee) {
         DebugPrint.printlnd(DebugPrint.WARN, 
                             "Exception while getting banner info");
         DebugPrint.printlnd(DebugPrint.WARN, ee);
      } finally {
         try { if (br != null) br.close(); } catch(Exception eee) {}
      }
      
      return banner;
   }
      
  /* -------------------------------------------------------*\
  ** Commands 
  \* -------------------------------------------------------*/
   public void fireLoginCommandToken(DSMPBaseHandler hand, byte flags, 
                                     byte handle, String token) {
                                     
      byte opcode = DropboxGenerator.OP_LOGIN_REPLY;
      
      refreshUserPW();
      
      DSMPBaseProto proto = null;
      if (hand.bitsSetInFlags(0x01)) {
         log.error("TokenLogin failed: already logged in on this connection");
         proto = DropboxGenerator.genericReplyError(opcode, handle, 0,
                                    "Already logged in on this connection=" + 
                                                hand.getHandlerId());
                                                
         if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
         hand.sendProtocolPacket(proto);
         return;
      }      
      
      if (token.trim().equals("")) {
         log.error("Token Login failed with error: Bad Token Format");
         proto = DropboxGenerator.genericReplyError(opcode, handle, 0,
                                                    "Token login failed: Bad token format");
      } else {
      
         User userObj = null;
         String error = null;
         if (cipher != null) {
            
            try {
               userObj = getUserFromToken(token);
                              
               userObj.setLoginId(hand.getHandlerId());
               hand.setIdentifier(userObj.getName());
               
              // If we are doing AMT Project checking, then get projects and
              //  entitlements associated with this goon. This also gets the
              //  bluepages info into the User object
               try {
                  Vector amtvec = AMTQuery.getAMTByUser(userObj.getName(),
                                                        doamtprojects, 
                                                        doamtprojects, null);
                  if (amtvec != null && amtvec.size() > 0) {
                     if (amtvec.size() == 1) {
                        AMTUser amtuser = (AMTUser)amtvec.elementAt(0);
                        if (doamtprojects) {
                           userObj.addProjects(amtuser.getProjects());
                           userObj.setIBMDept(amtuser.getIBMDept());
                           userObj.setIBMDiv(amtuser.getIBMDiv());
                        }
                     } else {
                        throw new DBException("More than 1 match!");
                     }
                  } else {
                     throw new DBException("No Matches found");
                  }
               } catch(DBException dbe) {
                  log.warn("Error getting AMTUser record for " +
                           userObj.getName() +
                           " and doing AMT!. No projectlist update");
                  log.warn(dbe);
               }
            } catch(Exception ee) {
               error = ee.getMessage();
               log.error(ee);
            }
            
         } else {
            error = "Not configured for token login";
         }
         
         if (error == null && userObj != null) {
            synchronized (users) {
              /* ... Uncomment this to go back to only one login per user
               User u = (User)users.get(user);
               if (u != null) {
                  proto = DropboxGenerator.genericReplyError(opcode, handle, 0,
                                                "Already logged in as ID = " + 
                                                          u.getLoginId());
               } else {
              */
               {
                  userObj.setTokenLogin(true);
                  
                  addUser(userObj);
                  proto = DropboxGenerator.loginReply(handle, 
                                                  userObj.getLoginId(),
                                                  getBanner(),
                                                  File.separator);
                  hand.addMaskToFlags(0x01);
                  
                 // Tell the client we are at least V2 of protocol
                 //  That way he can use protocol negotiation 
                  proto.addMaskToFlags(0x2);
                  
                  log.info("Token login for [" + 
                           userObj.getName() + 
                           "] Company[" + 
                           userObj.getCompany() +
                           "] Email[" + 
                           userObj.getEmail() + "]");
                           
                 // Set some alert info
                  DboxAlert.alertinfo = 
                     "userid  = " + userObj.getName() + "\n" + 
                     "company = " + userObj.getCompany() + "\n" + 
                     "email   = " + userObj.getEmail();
                                        
                 // Cheap way to get debug turned on        
                  File f = new File(forcedropboxpath + "/" + 
                                    userObj.getName());
                  if (f.exists()) {
                     log.warn("DEBUGforced on by entry in " + forcedropboxpath);
                     setDebug(true);
                     Debug.setDebug(true);
                     DebugPrint.setLevel(DebugPrint.DEBUG3);
                  }
                           
                  try {
                     packageMgr.openSession(userObj);                    
                  } catch(Exception ee) {
                     log.error("Error doing openSession for " +
                               userObj.toString());
                     log.error(ee);
                  }
               }
            }
         } else {
            log.error("Token Login failed with error: " + error);
            proto = DropboxGenerator.genericReplyError(opcode, handle, 0,
                                                   "Token Login Failed: " + 
                                                   error==null?"???":error);
         }
      }
      
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
   
   public void fireLoginCommandUserPW(DSMPBaseHandler hand, byte flags, 
                                      byte handle,
                                      String user, String pw) {
                                      
      byte opcode = DropboxGenerator.OP_LOGIN_REPLY;
      
      refreshUserPW();
      
      DSMPBaseProto proto=null;
      if (hand.bitsSetInFlags(0x01)) {
         proto = DropboxGenerator.genericReplyError(opcode,
                                                handle, 0,
                                                "Already logged in = " + 
                                                hand.getHandlerId());
         log.error("Login failed: already logged in on this connection");
         
         if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
         hand.sendProtocolPacket(proto);
         return;
      }
      
      User userObj = null;
      Vector v = (Vector)userpw.get(user);
      
      if (v != null) {
      
         if (((String)v.firstElement()).equals(pw) && 
             pw.trim().length() != 0) {
            Vector projv = new Vector();
            Enumeration e = v.elements();
            
            e.nextElement();
            String company = (String)e.nextElement();
            while(e.hasMoreElements()) {
               projv.addElement(e.nextElement());
            }
            
            synchronized (users) {
              /* ... Uncomment this to go back to only one login per user
               User u = (User)users.get(user);
               if (u != null) {
                  proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                          0, 
                                                       "Already logged in = " +
                                                          u.getLoginId());
               } else {
              */
               {
                  hand.setIdentifier(user);
                  userObj=new User(user, projv, hand.getHandlerId());
                  userObj.setCompany(company);
                  addUser(userObj);
                  
                 // Set some alert info
                  DboxAlert.alertinfo = 
                     "userid  = " + userObj.getName() + "\n" + 
                     "company = " + userObj.getCompany() + "\n" + 
                     "email   = " + userObj.getEmail();
                  
                  proto = DropboxGenerator.loginReply(handle, 
                                                      userObj.getLoginId(),
                                                      getBanner(),
                                                      File.separator);
                                                      
                 // Tell the client we are at least V2 of protocol
                 //  That way he can use protocol negotiation 
                  proto.addMaskToFlags(0x2);
                                                      
                  hand.addMaskToFlags(0x01);
                  log.info("User/PW login for [" + userObj.getName() + 
                           "] Company[" + userObj.getCompany() + "]");
                           
                  try {
                     packageMgr.openSession(userObj);
                  } catch(Exception ee) {
                     log.error("Error doing openSession for " +
                               userObj.toString());
                     log.error(ee);
                  }
               }
            }
         }
      }
      if (proto == null) {
         log.error("Userid/PW based login failed. Invalid Login - bad Userid/pw");
         proto = DropboxGenerator.genericReplyError(
            DropboxGenerator.OP_LOGIN_REPLY, handle, 0, "Login failed: Bad UserID/PW");
      }
      
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
   
   public void fireLogoutCommand(DSMPBaseHandler hand, byte flags, 
                                 byte handle) {
      byte opcode = DropboxGenerator.OP_LOGOUT_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DSMPBaseProto proto = null;
      try {
      
         log.info("fireLogoutCommand received");
         
         validateLoggedIn(hand, handle, opcode);
      
         try {
            User user = getUser(hand.getHandlerId());
            if (user != null) {
               packageMgr.closeSession(user, hand);
            }
         } catch(Exception ee) {
            log.error("Error doing closeSession");
            log.error(ee);
         }
         
         subtask = "removing as active user";
         whereFailed = 1;
         
         if (removeUser(hand.getHandlerId())) {
            log.error("Logout:Huh? no User record for id=" + 
                      hand.getHandlerId());
         }
         
         whereFailed = 2;
         proto=DropboxGenerator.logoutReply(handle);
         hand.removeMaskFromFlags(0x01);
         
         if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
         hand.sendProtocolPacket(proto);
         try {
            Thread.currentThread().sleep(2000);
         } catch(Throwable tt) {}
         hand.shutdown();         
      } catch(DboxException dbex) {
         String errmsg = "Logout command ";
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
         if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
         hand.sendProtocolPacket(proto);
      }
   }
      
   public void fireChangeAreaCommand(DSMPBaseHandler hand, byte flags, 
                                     byte handle, String area) {
      
      byte opcode = DropboxGenerator.OP_CHANGEAREA_REPLY;
      uncaughtProtocol(hand, opcode);
   }
   public void fireListAreaCommand(DSMPBaseHandler hand, byte flags, 
                                   byte handle) {
      byte opcode = DropboxGenerator.OP_LISTAREA_REPLY;
      uncaughtProtocol(hand, opcode);
   }
   
   public void fireDeleteFileCommand(DSMPBaseHandler hand, byte flags, 
                                     byte handle, String file) {
      byte opcode = DropboxGenerator.OP_DELETEFILE_REPLY;
      uncaughtProtocol(hand, opcode);
   }
   
   public void fireNewFolderCommand(DSMPBaseHandler hand, byte flags, 
                                    byte handle, String folder) {
      byte opcode = DropboxGenerator.OP_NEWFOLDER_REPLY;
      uncaughtProtocol(hand, opcode);
   }
   
   public void fireUploadCommand(DSMPBaseHandler hand, byte flags, 
                                 byte handle, boolean tryRestart, 
                                 int crc, long crcsz,
                                 long filelen, String file) {
      
      byte opcode = DropboxGenerator.OP_UPLOAD_REPLY;
      uncaughtProtocol(hand, opcode);
   }
   
   public void fireAbortUploadCommand(DSMPBaseHandler hand, byte flags, 
                                      byte handle, int id) {
      
      byte opcode = DropboxGenerator.OP_ABORTUPLOAD_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      DSMPBaseProto proto = null;
      
      try {
         validateLoggedIn(hand, handle, opcode);
         
         User user = getUserEx(hand.getHandlerId());
         
         subtask = " searching for operation object";
         whereFailed = 1;
         
         Operation op = user.getOperation(id);
         if (op == null) {
         
            throw new DboxException("Operation ID to abort not found: " +
                                    id , 0);
         }
         
         whereFailed = 1;
         subtask = " sending endOperation to operation object";
         op.endOperation("User Abort Action");
         
         log.info("Client Abort upload completed for id=" + id);
         whereFailed = 2;
         subtask = " Nada";
         
         proto = DropboxGenerator.abortUploadReply(handle, id);
      } catch (DboxException dbex) {
         String errmsg = "Abort upload for operationid[" + id + "] ";
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
      }
      
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
   
   public void fireUploadDataCommand(DSMPBaseHandler hand, byte flags, 
                                     byte handle, int id, long ofs, 
                                     CompressInfo ci) { 
                                     
      byte opcode = DropboxGenerator.OP_UPLOADDATA;
      User user = getUser(hand.getHandlerId());
      DSMPBaseProto proto = null;;
      if (!hand.bitsSetInFlags(0x01)) {
         proto = DropboxGenerator.uploadDataError(handle, 0, 
                                                 "Not Logged In", 
                                                  id);
         hand.sendProtocolPacket(proto);
      } else if (user == null) {
         proto = DropboxGenerator.uploadDataError(handle, 0, 
                                                 "User object not found", 
                                                  id);
         hand.sendProtocolPacket(proto);
      } else {
        // TODO CATCH THIS CAST ERROR
         UploadOperation op = (UploadOperation)user.getOperation(id);
         if (op != null) {
            op.frameData(ofs, ci.buf, ci.ofs, ci.len);
         } else {
            String err = "Operation ID for UploadData not found[" + id + "]";
            proto = DropboxGenerator.uploadDataError(handle, 0, err, id);
            hand.sendProtocolPacket(proto);
         }
      }
   }
   public void fireDownloadCommand(DSMPBaseHandler hand, byte flags, 
                                   byte handle, boolean tryRestart, 
                                   int crc, long filelen, String file) {
                                   
      byte opcode = DropboxGenerator.OP_DOWNLOAD_REPLY;
      uncaughtProtocol(hand, opcode);
   }
                         
   public void fireAbortDownloadCommand(DSMPBaseHandler hand, byte flags, 
                                        byte handle, int id) {
      byte opcode = DropboxGenerator.OP_ABORTDOWNLOAD_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      DSMPBaseProto proto = null;
      
      try {
         validateLoggedIn(hand, handle, opcode);
         
         subtask = "finding user object";
         
         User user = getUserEx(hand.getHandlerId());
         
         subtask = " searching for operation object";
         
         whereFailed = 1;
         
         Operation op = user.getOperation(id);
         if (op == null) {
         
            throw new DboxException("Operation ID to abort not found: " +
                                    id , 0);
         }
         
         whereFailed = 1;
         subtask = " sending endOperation to operation object";
         op.endOperation("User Abort Action");
         
         whereFailed = 2;
         subtask = " Nada";
         
         log.info("Client Abort Download completed for id=" + id);
         proto = DropboxGenerator.abortDownloadReply(handle, id);
      } catch (DboxException dbex) {
         String errmsg = "Abort download for operationid[" + id + "] ";
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
      }
      
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
   
   public void fireOperationCompleteCommand(DSMPBaseHandler hand, byte flags, 
                                            byte handle, int id, String md5) {
      byte opcode = DropboxGenerator.OP_OPERATION_COMPLETE;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DSMPBaseProto proto = null;
      try {
      
         validateLoggedIn(hand, handle, opcode);
         
         subtask = "finding user object";
         
         User user = getUserEx(hand.getHandlerId());
         
         whereFailed = 1;
         
         Operation op = user.getOperation(id);
         if (op == null) {
         
            throw new DboxException("Operation ID to END not found: " +
                                    id , 0);
         }
         
         log.warn("Download operation complete: " + id);
         op.endOperation(null);
         
      } catch(DboxException dbex) {
         String errmsg = "Complete command for operation ID [" + id + "] ";
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         
        /* No errors for this xmitted back to client
          errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
          proto = DropboxGenerator.genericReplyError(opcode, handle, 
          dbex.getErrorCode(), 
          errmsg);
          hand.sendProtocolPacket(proto);
        */
      }
   }
   
  /* -=-=-=-=-=-=-=-=-=- Start of DropBox commands -=-=-=-=-=-=-=-=-=-=-=-=- */
  
   
   public void fireCreatePackageCommand(DSMPBaseHandler hand, byte flags, 
                                        byte handle, String packname, 
                                        long poolid,
                                        long expire, Vector acls,
                                        int optmsk, int optvals) {
      
      byte opcode=DropboxGenerator.OP_CREATE_PACKAGE_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      
      DSMPBaseProto proto = null;
      try {
         
         validateLoggedIn(hand, handle, opcode);
         
         subtask = "finding the User object";
         User user = getUserEx(hand.getHandlerId());
         
         log.info("Create Package: u[" + user.getName() + "] p[" + packname + 
                  "] exp[" + (expire==0?""+0:new Date(expire).toString()));
         
        /*
          if (packname.indexOf(File.separator) >= 0) {
          throw new 
          DboxException("Package names CANNOT contain separator char [" +
          packname + "]", 0);
          }
        */        
         
         subtask = "asserting the validity of the recipient list";
         if (doamtchecking) {
            acls = assertAMTChecks(user, acls);
         }
         
         subtask = "creating the package";
         
         whereFailed = 1;
         DboxPackageInfo info=packageMgr.createPackage(user,
                                                       packname, 
                                                       poolid,
                                                       expire,
                                                       acls);
         whereFailed = 2;
         
         subtask = "setting package specific options";
         
         packageMgr.setPackageOption(user, info.getPackageId(),
                                     optmsk, optvals);
         
         proto = DropboxGenerator.createPackageReply(handle,
                                                     info.getPackageId()); 
      } catch(DboxException dbex) {
         String errmsg = "Creation of package [" + packname + "] ";
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + UDNote + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
      }
      
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
    
   public void fireDeletePackageCommand(DSMPBaseHandler hand, byte flags, 
                                        byte handle, long packid) {
      byte opcode=DropboxGenerator.OP_DELETE_PACKAGE_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DSMPBaseProto proto = null;
      try {
         validateLoggedIn(hand, handle, opcode);
      
         subtask = "finding the User object";
         User user = getUserEx(hand.getHandlerId());
         
         log.info("Delete Package u[" + user.getName() + 
                  "] p[" + packid + "]");
                  
         subtask = " deleting package";
         whereFailed = 1;
         packageMgr.deletePackage(user, packid);
         
         proto = DropboxGenerator.deletePackageReply(handle);
      } catch(DboxException dbex) {
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
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
      }
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
    
   public void fireCommitPackageCommand(DSMPBaseHandler hand, byte flags, 
                                        byte handle, long packid) {
      byte opcode=DropboxGenerator.OP_COMMIT_PACKAGE_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DSMPBaseProto proto = null;
      try {
      
         validateLoggedIn(hand, handle, opcode);
      
         subtask = "finding user object";
         User user = getUserEx(hand.getHandlerId());
         log.info("Commit Package u[" + user.getName() + 
                  "] p[" + packid + "]");
         
         DboxPackageInfo pinfo = null;
         Vector pacls = null;
         Vector todo  = null;
         
         if (doamtchecking || doamtmailing) {
            subtask = "creating complete list of recipients";
            DebugPrint.printlnd(DebugPrint.DEBUG, subtask);
            pinfo = packageMgr.lookupPackage(packid);
            pacls = pinfo.getPackageAcls(true);
            todo  = flattenACLsToUsers(pacls);
         }
         
         if (doamtchecking) {
            subtask = "checking for valid recipient list";
            DebugPrint.printlnd(DebugPrint.DEBUG, subtask);
            if (todo.size() == 0) {
               throw new DboxException("Cannot commit package: No valid recipients specified", 0);
            }
         }
         
         whereFailed = 1;
         subtask = "doing the commit";
         DebugPrint.printlnd(DebugPrint.DEBUG, subtask);
         packageMgr.commitPackage(user , packid);
         
         whereFailed = 2;
         subtask = "mailing notifications to recipients";
         DebugPrint.printlnd(DebugPrint.DEBUG, subtask);
         
        // Send mail to the flattened ACL list
         if (doamtmailing) {
            
            if (pinfo.getPackageSendNotification()) {
            
               DebugPrint.printlnd(DebugPrint.DEBUG, 
                                   "Sending notification for package");
               
              // Lookup the package again to get the commit date
               pinfo = packageMgr.lookupPackage(packid);
               
              // todo contains DISTINCT list of users
               Enumeration lenum = todo.elements();
               while(lenum.hasMoreElements()) {
                  String s = (String)lenum.nextElement();
                  
                  boolean doit = true;
                  try {
                     String ts = packageMgr.getUserOption(s,
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
            DebugPrint.printlnd(DebugPrint.DEBUG, "AMT Mailing is off"); 
         }
         proto = DropboxGenerator.commitPackageReply(handle);
         
      } catch(DboxException dbex) {
      
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
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
      }
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
    
   public void fireQueryPackagesCommand(DSMPBaseHandler hand, byte flags, 
                                        byte handle, 
                                        boolean regexpValid,
                                        boolean ownerOrAccessor,
                                        String regexp, 
                                        boolean filterMarked, 
                                        boolean filterCompleted) {
      byte opcode=DropboxGenerator.OP_QUERY_PACKAGES_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DSMPBaseProto proto = null;
      try {
      
         validateLoggedIn(hand, handle, opcode);
         
         subtask = "finding user object";
         User user = getUserEx(hand.getHandlerId());
            
        // HACK. We allow regexp to be passed w/o the regexp valid bit being
        //       set. If so, then its a straight name lookup.
        // if (!regexpValid) regexp = null;
         
         subtask = "doing the package search";
         whereFailed = 1;
         Vector ret = 
            packageMgr.packagesMatchingExprWithAccess(user, ownerOrAccessor,
                                                      regexp, regexpValid,
                                                      filterMarked, 
                                                      filterCompleted);
        // Weed out all hidden packages
         String bv = packageMgr.getUserOption(user.getName(),
                                               DropboxGenerator.ShowHidden) ;
                                               
        // Packages are hidden only in INBOX AND only if ShowHidden == false
         if (!ownerOrAccessor && bv.equalsIgnoreCase("false")) {
             
            int l = ret.size();
            for(int i=0; i < l; i++) {
               PackageInfo pi = (PackageInfo)ret.elementAt(i);
               if ((pi.getPackageFlags() & PackageInfo.HIDDEN) != 0) {
                  ret.removeElementAt(i);
                  l--; i--;
               }
            }
         }
         
         proto = DropboxGenerator.queryPackagesReply(handle, 
                                                     ownerOrAccessor,
                                                     (flags & 0x10) != 0,
                                                     ret);
      } catch(DboxException dbex) {
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
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
      }
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
    
   public void fireQueryPackageCommand(DSMPBaseHandler hand, byte flags, 
                                       byte handle, long packid) {
      byte opcode=DropboxGenerator.OP_QUERY_PACKAGE_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DboxPackageInfo info = null; 
      
      DSMPBaseProto proto = null;
      try {
      
         validateLoggedIn(hand, handle, opcode);
      
         subtask = "finding user object";
         User user = getUserEx(hand.getHandlerId());
         
         subtask = "searching for package";
         info = packageMgr.lookupPackage(packid, user);
         
         subtask = "checking package access";
         whereFailed = 1;
         if (!info.canAccessPackage(user, true)) {
            info = null;
            throw new DboxException("queryPackage:>> Can't access package " +
                                     + packid,
                                    0);
         }
         proto = DropboxGenerator.queryPackageReply(handle, info, 
                                                    (flags & 0x01) != 0);
      } catch(DboxException dbex) {
      
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
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
      }
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
    
   public void fireQueryPackageContentsCommand(DSMPBaseHandler hand, 
                                               byte flags, byte handle,
                                               long packid) {
      byte opcode=DropboxGenerator.OP_QUERY_PACKAGE_CONTENTS_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DboxPackageInfo info = null; 
      
      DSMPBaseProto proto = null;
      try {
      
         validateLoggedIn(hand, handle, opcode);
      
         subtask = "finding user object";
         User user = getUserEx(hand.getHandlerId());
         
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
         
         Vector vec = info.getFiles();
         proto = DropboxGenerator.queryPackageContentsReply(handle, packid,
                                                            vec);   
         
      } catch(DboxException dbex) {
      
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
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
      }
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
    
   public void fireQueryPackageAclsCommand(DSMPBaseHandler hand, byte flags, 
                                           byte handle, long packid,
                                           boolean staticOnly) {
      byte opcode=DropboxGenerator.OP_QUERY_PACKAGE_ACLS_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DboxPackageInfo info = null; 
      
      DSMPBaseProto proto = null;
      try {
         validateLoggedIn(hand, handle, opcode);
         
         subtask = "finding user object";
         User user = getUserEx(hand.getHandlerId());
         
         subtask = "searching for package";
         info = packageMgr.lookupPackage(packid);
         
         subtask = "checking package access rights";
         if (!info.canAccessPackage(user, true)) {
            info = null;
            throw new DboxException("queryPackageAcls:>> Can't access package " +
                                    packid, 0);
         }
            
         whereFailed = 1;
         subtask = "accessing package ACLS";
         Vector vec = info.getPackageAcls(staticOnly);
         
        // If not owner or superuser, remove all non-pertinent info
         String name = user.getName();
         
         subtask = "culling restricted ACLS";
         if (!name.equals(info.getPackageOwner()) &&
             packageMgr.getPrivilegeLevel(name) < 
             packageMgr.PRIVILEGE_SUPER_USER) {
            
            for(int i=0; i < vec.size(); i++) {
               AclInfo acl = (AclInfo)vec.elementAt(i);
               byte astat = acl.getAclStatus();
               if ((astat != DropboxGenerator.STATUS_NONE      &&
                    astat != DropboxGenerator.STATUS_PARTIAL   &&
                    astat != DropboxGenerator.STATUS_FAIL      &&
                    astat != DropboxGenerator.STATUS_COMPLETE) ||
                   !acl.getAclName().equals(name)) {
                  vec.removeElementAt(i);
                  i--;
               }
            }
         }
         
         proto      = DropboxGenerator.queryPackageAclsReply(handle, vec,
                                                             staticOnly); 
      } catch(DboxException dbex) {
      
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
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
      }
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
   
   public void fireQueryPackageFileAclsCommand(DSMPBaseHandler hand, 
                                               byte flags, 
                                               byte handle, long packid, 
                                               long fileid) {
      byte opcode=DropboxGenerator.OP_QUERY_PACKAGE_FILE_ACLS_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DboxPackageInfo info = null;
      
      DSMPBaseProto proto = null;
      try {
         validateLoggedIn(hand, handle, opcode);
         
         subtask = "finding user object";
         User user = getUserEx(hand.getHandlerId());
         
         subtask = "searching for package";
         info = packageMgr.lookupPackage(packid);
         
         subtask = "checking package access rights";
         whereFailed = 1;
         if (!info.canAccessPackage(user, true)) {
            info = null;
            throw new DboxException("Can't access package " + packid, 0);
         }
         
         subtask = "obtaining package access list";
         Vector vec = info.getFileAcls(fileid);
         
        // If not owner or superuser, remove all non-pertinent info
         subtask = "culling restricted ACLS";
         String name = user.getName();
         if (!name.equals(info.getPackageOwner()) &&
             packageMgr.getPrivilegeLevel(name) < 
             packageMgr.PRIVILEGE_SUPER_USER) {
            
            for(int i=0; i < vec.size(); i++) {
               AclInfo acl = (AclInfo)vec.elementAt(i);
               if (!acl.getAclName().equals(name)) {
                  vec.removeElementAt(i);
                  i--;
               }
            }
         }
         
         proto      = DropboxGenerator.queryPackageFileAclsReply(handle,
                                                                 vec); 
      } catch(DboxException dbex) {
      
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
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
      }
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
    
   public void fireQueryFilesCommand(DSMPBaseHandler hand, byte flags, 
                                     byte handle,
                                     boolean regexpValid,
                                     boolean ownerOrAccessor,
                                     String regexp) {
                                     
      byte opcode=DropboxGenerator.OP_QUERY_FILES_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DSMPBaseProto proto = null;
      try {
      
         validateLoggedIn(hand, handle, opcode);
         
         subtask = "finding user object";
         User user = getUserEx(hand.getHandlerId());
         
         if (!regexpValid) regexp = null;
         FileManager fmgr = packageMgr.getFileManager();
         
         whereFailed = 1;
         subtask = "searching for files";
         Vector ret = fmgr.filesMatchingExprWithAccess(user, ownerOrAccessor,
                                                       regexp, regexpValid);
         
         proto = DropboxGenerator.queryFilesReply(handle, 
                                                  ownerOrAccessor,
                                                  ret);
      } catch(DboxException dbex) {
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
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
      }
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
    
   public void fireQueryFileCommand(DSMPBaseHandler hand, byte flags, 
                                    byte handle, long fileid) {
      byte opcode=DropboxGenerator.OP_QUERY_FILE_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DboxFileInfo info = null;
      
      DSMPBaseProto proto = null;
      try {
      
         validateLoggedIn(hand, handle, opcode);
         
         subtask = "find user object";
         User user = getUserEx(hand.getHandlerId());
         
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
         
         Vector vec = packageMgr.getPackagesContainingFile(user, info);
         proto      = DropboxGenerator.queryFileReply(handle, info, vec); 
         
      } catch(DboxException dbex) {
      
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
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
      }
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
    
   public void fireAddItemToPackageCommand(DSMPBaseHandler hand, byte flags, 
                                           byte handle, long packid, 
                                           long itemid) {
      
      byte opcode=DropboxGenerator.OP_ADD_ITEM_TO_PACKAGE_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DSMPBaseProto proto = null;
      try {
      
         validateLoggedIn(hand, handle, opcode);
         
         subtask = "find user object";
         User user = getUserEx(hand.getHandlerId());
         
         log.info("Add item to Package u[" + user.getName() + 
                  "] p[" + packid + "] item[" + itemid + "]");
                  
         whereFailed = 1;
         subtask = "adding item to package";
         packageMgr.addItemToPackage(user, packid, itemid);
         
         proto = DropboxGenerator.addItemToPackageReply(handle);
      } catch(DboxException dbex) {
         
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
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
      }
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
    
   public void fireUploadFileToPackageCommand(DSMPBaseHandler hand,
                                              byte flags, 
                                              byte handle, boolean tryRestart, 
                                              long packid,
                                              String md5, long md5Size,
                                              long filelen, String file) {
                                              
      byte opcode=DropboxGenerator.OP_UPLOAD_FILE_TO_PACKAGE_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DboxPackageInfo pinfo = null; 
      DboxFileInfo     info = null;
      
      DSMPBaseProto proto = null;
      try {
      
         validateLoggedIn(hand, handle, opcode);
         
         subtask = "finding user object";
         User user = getUserEx(hand.getHandlerId());
         long ofs = 0;
         
         log.info("Upload file to Package u[" + user.getName() + 
                  "] p[" + packid + "] f[" + file + "]");
         
        // JMC 3/16/04 - Forbidden char replacement 
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
         
         if (pinfo.includesFile(file)) {
            
            subtask = "checking for same file already in package";
            info = pinfo.getFile(file);
            if (!(packageMgr instanceof GridPackageManager) &&
                info.getFileStatus() == DropboxGenerator.STATUS_COMPLETE) {
                
               throw new DboxException(
                  "File with same name already in package and complete [" +
                  file + "]", 0);
            }
            
            subtask = "checking for possible restart of file";
            if (tryRestart) {
               subtask = "checking for possible restart of file";
               if (info.getFileSize() == md5Size) {
                  String imd5 = info.getFileMD5();
                  if (imd5.equals("")) {
                     try {
                        log.info("Calculating MD5 for upload: " + md5Size);
                        imd5 = info.calculateMD5((long)0);
                        log.info("Finished calculating MD5 for upload");
                        info.forceSetFileMD5(imd5);
                     } catch(Exception io) {
                        log.warn("IOExcp when trying to calculate MD5 for " + info);
                     }
                  }
                  if (imd5.equals(md5)) {
                     ofs = info.getFileSize();
                  }
               }
            }
            
            if (ofs == 0) {
               subtask = "prepping for file upload";
            
               info.deleteComponents();
               pinfo.recalculatePackageSize();
            }
            subtask = "setting file object attributes";
            info.setFileIntendedSize(filelen);
            info.setServersideFileStatus(DropboxGenerator.STATUS_NONE);
         } else {
            info = packageMgr.getFileManager().createFile(file, filelen, 
                                                          pinfo.getPackagePoolId());
            pinfo.addFile(info);
         }
         
         debugprint("UploadfileToPackage : " + info.toString());
         
         subtask = "setting up upload Operation object";
         whereFailed = 1;
         Operation op = new UploadOperation(hand, IDGenerator.getId(), 
                                            filelen-ofs, ofs, info, pinfo);
                                            
         op.setHandleToUse(handle);
         
         addOperation(op);       // trace by Dispatcher
         user.addOperation(op);  // Track by user
         proto = DropboxGenerator.uploadFileToPackageReply(handle, 
                                                           info.getFileId(),
                                                           ofs != 0,
                                                           op.getId(),
                                                           ofs);
         
         if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
         hand.sendProtocolPacket(proto);
         
         if (filelen == ofs) op.endOperation(null);
         
      } catch(DboxException dbex) {
         
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
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
         if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
         hand.sendProtocolPacket(proto);
      }
   }
    
   public void fireRemoveItemFromPackageCommand(DSMPBaseHandler hand, 
                                                byte flags, 
                                                byte handle, long packid,
                                                long itemid) {
      byte opcode=DropboxGenerator.OP_REMOVE_ITEM_FROM_PACKAGE_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DSMPBaseProto proto = null;
      try {
         validateLoggedIn(hand, handle, opcode);
         
         subtask = "finding user object";
         User user = getUserEx(hand.getHandlerId());
         
         log.info("Remove item from Package u[" + user.getName() + 
                  "] p[" + packid + "] + item[" + itemid + "]");
         
         subtask = "removing file from package";
         whereFailed = 1;
         packageMgr.removeItemFromPackage(user, packid, 
                                          itemid);
         proto = DropboxGenerator.removeItemFromPackageReply(handle);
      } catch(DboxException dbex) {
         
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
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
      }
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
    
   public void fireDownloadPackageItemCommand(DSMPBaseHandler hand, 
                                              byte flags, 
                                              byte handle, boolean tryRestart,
                                              boolean trySync,
                                              long packid, String md5, 
                                              long filelen, long fileid) { 
      byte opcode=DropboxGenerator.OP_DOWNLOAD_PACKAGE_ITEM_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DboxPackageInfo pinfo = null;
      DboxFileInfo    info  = null;
      
      DSMPBaseProto proto = null;
      try {
      
         validateLoggedIn(hand, handle, opcode);
      
         subtask = "finding user object";
         User user = getUserEx(hand.getHandlerId());
         long ofs = 0;
         
         log.info("Initiate Download u[" + user.getName() + 
                  "] c[" + user.getCompany() +
                  "] p[" + packid + "] f[" + fileid + "]");
                  
         subtask = "searching for package";
         pinfo = packageMgr.lookupPackage(packid, user);
         
         subtask = "checking package access rights";
         if (!pinfo.canAccessPackage(user, true)) {
            pinfo = null;
            throw new DboxException("Can't access package " + packid, 0);
         }
         
         info = pinfo.getFile(fileid);
         if (info.getFileStatus() != DropboxGenerator.STATUS_COMPLETE) {
            throw new DboxException("File " + fileid + 
                                    " is not yet Complete", 0);
         }
         
         if (tryRestart) {
            subtask = "checking for possible restart of file";
            if (info.getFileSize() >= filelen) {
               try {
                  String lmd5 = info.getFileMD5();
                  if (info.getFileSize() == filelen) {
                     if (lmd5.equalsIgnoreCase(md5)) {
                        ofs = filelen;
                     }
                  } else {
                     lmd5 = info.calculateMD5(filelen);
                     if (lmd5.equalsIgnoreCase(md5)) {
                        ofs = filelen;
                     }
                  }
                  log.info("Restart[" + (ofs != 0) + 
                           "] Download MD5[" + lmd5 + 
                           "] userMD5[" + md5 + "] len[" + 
                           filelen + "] mylen[" + info.getFileSize() + "]");
               } catch(Exception io) {}
            }
         }
         
         whereFailed = 1;
         subtask = "creating Download Operation object";
         DownloadOperation op;
         op = new DownloadOperation(hand, IDGenerator.getId(), 
                                    info.getFileSize()-ofs, ofs, 
                                    info, pinfo, user);
         
         op.setHandleToUse(handle);
         
         if (trySync) op.enableSyncFrames();
         
         addOperation(op);       // trace by Dispatcher
         user.addOperation(op);  // Track by user
         proto = 
            DropboxGenerator.downloadPackageItemReply(handle, 
                                                      ofs != 0, 
                                                      trySync,
                                                      op.getId(), 
                                                      ofs, 
                                                      info.getFileSize());
         
         if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
         hand.sendProtocolPacket(proto);
         new Thread(op, "DownloadOp").start();
         
      } catch(DboxException dbex) {
         String fn = "" + fileid;
         String pn = "" + packid;
         if (info  != null) pn =  info.getFileName();
         if (pinfo != null) pn = pinfo.getPackageName();
         
         String errmsg = "Download file[" + fn + "] from package [" + pn + "] ";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
         if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
         hand.sendProtocolPacket(proto);
      }
      
   }    
    
   public void fireDownloadPackageCommand(DSMPBaseHandler hand, 
                                          byte flags, 
                                          byte handle, 
                                          boolean trySync,
                                          long packid, String encoding) {
                                          
      byte opcode=DropboxGenerator.OP_DOWNLOAD_PACKAGE_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DboxPackageInfo pinfo = null;
      DboxFileInfo    info  = null;
      
      DSMPBaseProto proto = null;
      try {
      
         validateLoggedIn(hand, handle, opcode);
      
         subtask = "finding user object";
         User user = getUserEx(hand.getHandlerId());
         long ofs = 0;
         
         log.info("Initiate Package Download u[" + user.getName() + 
                  "] c[" + user.getCompany() +
                  "] p[" + packid + "] encoding[" + encoding + "]");
                  
         subtask = "searching for package";
         pinfo = packageMgr.lookupPackage(packid, user);
         
         subtask = "checking package access rights";
         if (!pinfo.canAccessPackage(user, true)) {
            pinfo = null;
            throw new DboxException("Can't access package " + packid, 0);
         }
         
         subtask = "checking package status";
         if (pinfo.getPackageStatus() != DropboxGenerator.STATUS_COMPLETE) {
            throw new DboxException("Package " + packid + 
                                    " is not yet Complete", 0);
         }
         
         subtask = "validating encoding request";
         if (!isValidPackageEncoding(encoding)) {
            throw new DboxException("Invalid encoding specified", 0);
         }
         
         whereFailed = 1;
         subtask = "creating Package Download Operation object";
         DownloadOperation op;
         op = new DownloadPackageOperation(hand, IDGenerator.getId(), 
                                           pinfo, user, encoding);
         
         op.setHandleToUse(handle);
         
         if (trySync) op.enableSyncFrames();
         
         addOperation(op);       // trace by Dispatcher
         user.addOperation(op);  // Track by user
         proto = 
            DropboxGenerator.downloadPackageReply(handle, 
                                                  trySync,
                                                  op.getId(), 
                                                  encoding);
         
         if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
         hand.sendProtocolPacket(proto);
         new Thread(op, "PackageDownloadOp").start();
         
      } catch(DboxException dbex) {
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
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
         if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
         hand.sendProtocolPacket(proto);
      }
      
   }    
    
   public void fireAddPackageAclCommand(DSMPBaseHandler hand, byte flags, 
                                        byte handle, byte acltype, 
                                        long packid, String aclname) {
      
      byte opcode=DropboxGenerator.OP_ADD_PACKAGE_ACL_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DboxPackageInfo pinfo = null;
      
      DSMPBaseProto proto = null;
      try {
         validateLoggedIn(hand, handle, opcode);
         
         subtask = "finding user object";
         User user = getUserEx(hand.getHandlerId());
         log.info("Add package ACL u[" + user.getName() + 
                  "] c[" + user.getCompany() +
                  "] p[" + packid + "] acln[" + aclname + 
                  "] acltype[" + acltype + "]");
         
         subtask = "searching for package in question";
         pinfo = packageMgr.lookupPackage(packid);
         
        // Check that this guy CAN be added to ACL list
         Vector acls = null;
         if (doamtchecking && acltype == DropboxGenerator.STATUS_NONE &&
             !aclname.equals("*")) {
            subtask = "asserting the validity of sending to the recipient";
            acls = assertAMTCheck(user, aclname);
         }
         
        // If we are doing mail, get flattened list of USERS currently
        // added as ACLS, either a USER or GROUP
         Vector currentReceivers = null;
         boolean sendmail = false;
         if (doamtmailing && 
             pinfo.getPackageStatus() == 
             DropboxGenerator.STATUS_COMPLETE &&
             pinfo.getPackageSendNotification() &&
             
            // JMC 9/21/04
            // This is essentially now == TRUE cause we accept ALL acl types
            //  used to be that we only accepted NONE and GROUP
             (acls != null                               ||
              acltype == DropboxGenerator.STATUS_NONE    ||
              acltype == DropboxGenerator.STATUS_PROJECT ||
              acltype == DropboxGenerator.STATUS_GROUP)) {
              
            subtask = "creating complete list of recipients";
            Vector pacls = pinfo.getPackageAcls(true);
            currentReceivers = flattenACLsToUsers(pacls);
            sendmail = true;
         }
         
        // Should throw exception if already added as an acl
         if (acls == null) {
            
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
               Vector todo = flattenACLToUsers(null, acl);
               
               Enumeration lenum = todo.elements();
               while(lenum.hasMoreElements()) {
                  String s = (String)lenum.nextElement();
                  if (!currentReceivers.contains(s)) {
                     
                     boolean doit = true;
                     try {
                        String ts = packageMgr.getUserOption(s,
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
                     
                     currentReceivers.addElement(aclname);
                  }
               }
            }
         } else {
           // If ACL checking is on, we are adding all acls returned
            Enumeration enum = acls.elements();
            while(enum.hasMoreElements()) {
               String laclname = (String)enum.nextElement();
               
               subtask = "adding name to access list";
               whereFailed = 1;
               packageMgr.addPackageAcl(user, packid, acltype, laclname);
               
              // Send mail to this guy
               if (sendmail) {
                  whereFailed = 2;
                  subtask = "mailing notifications to recipients";
                  if (!currentReceivers.contains(laclname)) {
                     
                     currentReceivers.addElement(laclname);
                     
                     boolean doit = true;
                     try {
                        String ts = packageMgr.getUserOption(laclname,
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
         
         proto = DropboxGenerator.addPackageAclReply(handle);
      } catch(DboxException dbex) {
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
         log.error(errmsg + UDNote + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
      }
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }

   public void fireRemovePackageAclCommand(DSMPBaseHandler hand, byte flags, 
                                           byte handle, byte acltype,
                                           long packid, String aclname) {
      byte opcode=DropboxGenerator.OP_REMOVE_PACKAGE_ACL_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DSMPBaseProto proto = null;
      try {
      
         validateLoggedIn(hand, handle, opcode);
         
         subtask = "finding user object";
         User user = getUserEx(hand.getHandlerId());
         
         log.info("Remove package ACL u[" + user.getName() + 
                  "] c[" + user.getCompany() +
                  "] p[" + packid + "] acln[" + aclname + 
                  "] acltype: " + acltype);
                  
         subtask = "removing package ACL";
         whereFailed = 1;
         packageMgr.removePackageAcl(user, packid, acltype, aclname);
         whereFailed = 2;
         proto = DropboxGenerator.removePackageAclReply(handle);
      } catch(DboxException dbex) {
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
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
      }
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
   
   public void fireChangePackageExpirationCommand(DSMPBaseHandler hand, 
                                                  byte flags, byte handle, 
                                                  long packid, long expire) {
      byte opcode = DropboxGenerator.OP_CHANGE_PACKAGE_EXPIRATION_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DSMPBaseProto proto = null;
      try {
      
         validateLoggedIn(hand, handle, opcode);
         
         subtask = "finding user object";
         User user = getUserEx(hand.getHandlerId());
         log.info("Change Package Expiration: u[" + user + 
                  "] c[" + user.getCompany() +
                  "] p[" + packid + 
                  "] exp[" + (expire==0?""+0:new Date(expire).toString()));
                  
         whereFailed = 1;
         subtask = "doing expiration change";
         packageMgr.changePackageExpiration(user, packid, expire);
         
         proto = DropboxGenerator.changePackageExpirationReply(handle);
      } catch(DboxException dbex) {
      
         String errmsg = "Change of package expiration for packid[" +
            packid + "] to " +
            (new Date(expire)).toString() + " ";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
      }
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
   
   public void fireGetProjectList(DSMPBaseHandler hand, byte flags, 
                                  byte handle) {
                                     
      byte opcode=DropboxGenerator.OP_GET_PROJECTLIST_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DSMPBaseProto proto = null;
      try {
         validateLoggedIn(hand, handle, opcode);
      
         subtask = "finding user object";
         User user = getUserEx(hand.getHandlerId());
         
         whereFailed = 1;
         
         Vector projs = user.getDoProjectSend()?user.getProjects():new Vector(); 
         proto = DropboxGenerator.getProjectListReply(handle, 
                                                      user.getName(),
                                                      user.getCompany(),
                                                      projs);
      } catch(DboxException dbex) {
      
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
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
      }
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
   
   public void fireMarkPackageCommand(DSMPBaseHandler hand, byte flags, 
                                      byte handle, long packid, boolean mark) {
      byte opcode=DropboxGenerator.OP_MARK_PACKAGE_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DSMPBaseProto proto = null;
      try {
         validateLoggedIn(hand, handle, opcode);
      
         subtask = "finding user object";
         User user = getUserEx(hand.getHandlerId());
         
         log.info("Marking Package u[" + user.getName() + 
                  "] p[" + packid + "] mark=" + mark);
                  
         whereFailed = 1;
         subtask = "marking the package";
         packageMgr.markPackage(user, packid, mark);
         proto = DropboxGenerator.markPackageReply(handle);
      } catch(DboxException dbex) {
      
         String errmsg = "Marking the package[" + packid + "] ";
         if (!mark) errmsg = "Un" + errmsg;
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
      }
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
   
   public void fireSendNewFrameCommand(DSMPBaseHandler hand, byte flags, 
                                       byte handle, int opid, int num) {
      byte opcode=DropboxGenerator.OP_SEND_NEW_FRAME;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      try {
         validateLoggedIn(hand, handle, opcode);
         subtask = "finding user object";
         User user = getUserEx(hand.getHandlerId());
         Operation op = user.getOperation(opid);
         if (op != null) {
            if (op instanceof DownloadOperation) {
               ((DownloadOperation)op).sendNewFrame(num);
            }
         }
      } catch(DboxException dbex) {
         log.error("UGG! Got exception while processing fireSendNewFrame! subtask=" + subtask);
         log.error(dbex);
      }
   }
   
   public void fireNegotiateProtocolVersionCommand(DSMPBaseHandler hand, 
                                                   byte flags, byte handle, 
                                                   int ver) {
      byte opcode=DropboxGenerator.OP_PROTO_VERSION_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DSMPBaseProto proto = null;
      try {
         validateLoggedIn(hand, handle, opcode);
      
         int myver = DropboxGenerator.PROTOCOL_VERSION;
         log.info("Client protocol ver = " + ver + " my ver = " + myver);
         
         if (myver > ver) myver = ver;
         
         whereFailed = 1;
         subtask = "setting protocol version";
         DropboxGenerator.setProtocolVersion(myver);
         
         proto = DropboxGenerator.negotiateProtocolVersionReply(handle, 
                                                                myver);
      } catch(DboxException dbex) {
      
         String errmsg = "Negotiating protocol clientversion[" + 
            ver + "] server version[" + DropboxGenerator.PROTOCOL_VERSION +
            "] ";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
      }
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
   
   public void fireManageOptionsCommand(DSMPBaseHandler hand, 
                                        byte flags, byte handle, 
                                        boolean doGet, 
                                        Hashtable set,
                                        Vector get) {
     // Check for errors first
      byte opcode=DropboxGenerator.OP_MANAGE_OPTIONS_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DSMPBaseProto proto = null;
      try {
         validateLoggedIn(hand, handle, opcode);
      
         subtask = "finding user object";
         User user = getUserEx(hand.getHandlerId());
         log.info("ManageOptions called: doGet " + doGet +
                  " setSize=" + (set==null?0:set.size()) + 
                  " getSize=" + (get==null?0:get.size()));
         
         if (doGet && get != null && get.size() > 0) {
            subtask = "asserting valid user options for GET";
            packageMgr.assertUserOptionNames(user.getName(), get);
         }
         
         if (set != null && set.size() > 0) {
            String OS         = (String)set.get(DropboxGenerator.OS);
            String clienttype = (String)set.get(DropboxGenerator.ClientType);
            
            if (OS != null) {
               subtask = "setting special OS option value";
               user.setOS(OS);
               set.remove(DropboxGenerator.OS);
            }
            if (clienttype != null) {
               subtask = "setting special clienttype option value";
               user.setClientType(clienttype);
               set.remove(DropboxGenerator.ClientType);
               
               log.info("Clienttype = " + clienttype + " OS = " + OS);
               
              // HACK! SFTP/dropboxftp do not need/use the stat field of pack
              //       ... its either COMPLETE or not. So lets speed things
              //           up for users by ditching the SLOW updateStatus
              //           Used by DB2PackageManager.updatePackageStatus
              //
              // Only really has an effect when there are LOTS of packages in
              //  drafts, as we skip for Completed packs anyway
               if (clienttype.equalsIgnoreCase("sftp")  || 
                   clienttype.equalsIgnoreCase("web")   || 
                   clienttype.equalsIgnoreCase("dropboxftp")) {
                  skipPartialCheck = true;
                  log.info("Clienttype = " + clienttype + 
                           ": Skipping partial package update");
               }
            }
            
            subtask = "asserting valid user options for SET";
            packageMgr.assertUserOptionNames(user.getName(), set);
            
            whereFailed = 1;
            subtask = "setting user options";
            packageMgr.setUserOptions(user.getName(), set);
         }
         
         if (doGet) {
            whereFailed = 1;
            subtask = "getting user options";
            
            boolean fullGet = (get == null || get.size() == 0);
            Hashtable getRet = packageMgr.getUserOptions(user.getName(),
                                                         get);
            proto = DropboxGenerator.manageOptionsReply(handle, true,
                                                        fullGet, getRet);
         } else {
            proto = DropboxGenerator.manageOptionsReply(handle, false,
                                                        false, null);
         }
         
      } catch(DboxException dbex) {
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
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
      }
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
   
   public void fireCreateGroupCommand(DSMPBaseHandler hand, byte flags, 
                                      byte handle, String groupname,
                                      byte visibility, 
                                      byte listability) {
      byte opcode=DropboxGenerator.OP_CREATE_GROUP_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DSMPBaseProto proto = null;
      try {
         validateLoggedIn(hand, handle, opcode);
      
         subtask = "finding user object";
         User user = getUserEx(hand.getHandlerId());
         
         log.info("CreateGroup called: groupname=" + groupname);
         
         whereFailed = 1;
         subtask = "creating group";
         packageMgr.createGroup(user, groupname);
         
         whereFailed = 2;
         subtask = "Modifying attributes for vis/listability";
         
         packageMgr.modifyGroupAttributes(user, groupname, 
                                          visibility, listability);
         proto = DropboxGenerator.createGroupReply(handle);
      } catch(DboxException dbex) {
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
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
      }
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
    
   public void fireDeleteGroupCommand(DSMPBaseHandler hand, byte flags, 
                                      byte handle, String groupname) {
      byte opcode=DropboxGenerator.OP_DELETE_GROUP_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DSMPBaseProto proto = null;
      try {
         validateLoggedIn(hand, handle, opcode);
      
         subtask = "finding user object";
         User user = getUserEx(hand.getHandlerId());
         log.info("DeleteGroup called: groupname=" + groupname);
         
         whereFailed = 1;
         subtask = "deleting group";
         packageMgr.deleteGroup(user, groupname);
         proto = DropboxGenerator.deleteGroupReply(handle);
      } catch(DboxException dbex) {
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
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
      }
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
   
   public void fireModifyGroupAclCommand(DSMPBaseHandler hand, byte flags, 
                                         byte handle, 
                                         boolean memberOrAccess,
                                         boolean addOrRemove,
                                         String groupname, String username) {
      byte opcode=DropboxGenerator.OP_MODIFY_GROUP_ACL_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DSMPBaseProto proto = null;
      try {
         validateLoggedIn(hand, handle, opcode);
      
         subtask = "finding user object";
         User user = getUserEx(hand.getHandlerId());
         log.info("ModifyGroupAcl called:" + 
                  " group=" + groupname +
                  " user=" + username +
                  " memberOrAccess=" + memberOrAccess +
                  " addRemove=" + addOrRemove);
         
         if (addOrRemove) {                        
            
           // If AMT checking is on, we are adding all acls returned
            if (doamtchecking && doGroupAMT) {
               subtask = "asserting valid username";
               Vector acls = assertAMTCheck(user, username);
               if (acls != null) {
                  Enumeration enum = acls.elements();
                  subtask = "adding member to group";
                  whereFailed = 1;
                  while(enum.hasMoreElements()) {
                     String laclname = (String)enum.nextElement();
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
         proto = DropboxGenerator.modifyGroupAclReply(handle);
      } catch(DboxException dbex) {
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
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
      }
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
   
   public void fireModifyGroupAttributeCommand(DSMPBaseHandler hand,
                                               byte flags, 
                                               byte handle, String groupname,
                                               byte visibility, 
                                               byte listability) {
      byte opcode=DropboxGenerator.OP_MODIFY_GROUP_ATTRIBUTES_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DSMPBaseProto proto = null;
      try {
         validateLoggedIn(hand, handle, opcode);
         
         subtask = "finding user object";
         User user = getUserEx(hand.getHandlerId());
         log.info("ModifyGroupAttribute called: " +
                  " groupname="   + groupname +
                  " visibility="  + visibility +
                  " listability=" + listability);
         
         whereFailed = 1;
         subtask = "modifying visibility/listability attributes";
         packageMgr.modifyGroupAttributes(user, groupname, 
                                          visibility, listability);
         proto = DropboxGenerator.modifyGroupAttributesReply(handle);
      } catch(DboxException dbex) {
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
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
      }
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
   
   public void fireQueryGroupsCommand(DSMPBaseHandler hand, byte flags,
                                      byte handle, 
                                      boolean regexSearch,
                                      boolean wantMember,
                                      boolean wantAccess,
                                      String groupname) {
                                      
      byte opcode=DropboxGenerator.OP_QUERY_GROUPS_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DSMPBaseProto proto = null;
      try {
      
         validateLoggedIn(hand, handle, opcode);
         
         subtask = "finding user object";
         User user = getUserEx(hand.getHandlerId());
         log.info("QueryGroups called: " +
                  " groupname="   + groupname +
                  " wantMember="  + wantMember +
                  " wantAccess="  + wantAccess +
                  " regexSearch=" + regexSearch);
         
         whereFailed = 1;
         subtask = "searching for matching groups";
         Hashtable ret = packageMgr.getMatchingGroups(user, groupname, 
                                                      regexSearch,
                                                      false, false, 
                                                      false, true, false,
                                                      true, true,
                                                      true);
         
         subtask = "building reply protocol";
         whereFailed = 2;
         
         proto = DropboxGenerator.queryGroupsReply(handle);
         if (wantMember) proto.addMaskToFlags(2);
         if (wantAccess) proto.addMaskToFlags(4);
         
         proto.append3ByteInteger(ret.size());
         
        // We only scoped the search by visible (and groupname if provided)
        //  So, we know the user is allowed to see the group. Still have 
        //  to prune out info he may not see (like members and attributes)
         String username = user.getName();
         Enumeration enum = ret.elements();
         while(enum.hasMoreElements()) {
            
            GroupInfo gi = (GroupInfo)enum.nextElement();
            
            String ownername = gi.getGroupOwner();
            
            proto.appendString16(gi.getGroupName());
            proto.appendString16(ownername);
            proto.appendString16(gi.getGroupCompany());
            proto.appendLong(gi.getGroupCreated());
            
           // If the user has modify access, then he can see the attributes
            boolean modify = false;
            Vector access = gi.getGroupAccess();
            Vector member = gi.getGroupMembers();
            if (ownername.equals(username) ||
                access.contains(username)) {
               proto.appendByte(gi.getGroupVisibility());
               proto.appendByte(gi.getGroupListability());
               modify = true;
            } else {
               proto.appendByte(DropboxGenerator.GROUP_SCOPE_NONE);
               proto.appendByte(DropboxGenerator.GROUP_SCOPE_NONE);
            }
            
            byte listable = gi.getGroupListability();
            byte lflags = (byte)(modify && wantAccess?2:0);
            lflags |= 
               (byte)((wantMember && 
                       ((modify)                    ||
                        (listable == DropboxGenerator.GROUP_SCOPE_ALL)     ||
                        ((listable == DropboxGenerator.GROUP_SCOPE_MEMBER) &&
                         member.contains(username)))) ? 1: 0);
            
            proto.appendByte(lflags);
            
            if ((lflags & (byte)1) != (byte)0) {
               proto.append3ByteInteger(member.size());
               Enumeration enum2 = member.elements();
               while(enum2.hasMoreElements()) {
                  proto.appendString16((String)enum2.nextElement());
               }
            } else {
               proto.append3ByteInteger(0);
            }
            if ((lflags & (byte)2) != (byte)0) {
               proto.append3ByteInteger(access.size());
               Enumeration enum2 = access.elements();
               while(enum2.hasMoreElements()) {
                  proto.appendString16((String)enum2.nextElement());
               }
            } else {
               proto.append3ByteInteger(0);
            }
         }
      } catch(DboxException dbex) {
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
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
      }
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }                                      
   
   public void fireSetPackageOptionCommand(DSMPBaseHandler hand, 
                                           byte flags, byte handle, 
                                           long    pkgid,
                                           int     pkgmsk, int pkgvals) {
                                           
     // Check for errors first
      byte opcode=DropboxGenerator.OP_SET_PACKAGE_OPTION_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DSMPBaseProto proto = null;
      try {
      
         validateLoggedIn(hand, handle, opcode);
         
         subtask = "finding user object";
         User user = getUserEx(hand.getHandlerId());
         log.info("SetPackageOption called: pkgid=" + pkgid + 
                  " pkgmsk=" + pkgmsk + " pkgvals=" + pkgvals);
         
         subtask = "setting package option for user";
         whereFailed = 1;
         int newflags = 
            packageMgr.setPackageOption(user, pkgid, pkgmsk, pkgvals);
         
         proto = DropboxGenerator.setPackageOptionReply(handle,
                                                        (byte)newflags);
      } catch(DboxException dbex) {
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
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
      }
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
   
   public void fireGetStoragePoolInstanceCommand(DSMPBaseHandler hand, byte flags,
                                                 byte handle, 
                                                 long poolid) {
                                      
     // Check for errors first
      byte opcode=DropboxGenerator.OP_GET_STORAGEPOOL_INSTANCE_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DSMPBaseProto proto = null;
      try {
      
         validateLoggedIn(hand, handle, opcode);
         
         subtask = "finding user object";
         User user = getUserEx(hand.getHandlerId());
         log.info("GetStoragePoolInstance called: poolid=" + poolid);
         
         subtask = "getting storagepool instance";
         whereFailed = 1;
         PoolInfo pool = packageMgr.getStoragePoolInstance(user, poolid);
         
         proto = DropboxGenerator.getStoragePoolInstanceReply(handle, pool);
         
      } catch(DboxException dbex) {
         String errmsg = "Getting storage pool instance for poolid[" + poolid + "] ";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
      }
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
   
   public void fireQueryStoragePoolInfoCommand(DSMPBaseHandler hand, byte flags,
                                               byte handle) {
                                      
     // Check for errors first
      byte opcode=DropboxGenerator.OP_QUERY_STORAGEPOOL_INFO_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DSMPBaseProto proto = null;
      try {
      
         validateLoggedIn(hand, handle, opcode);
         
         subtask = "finding user object";
         User user = getUserEx(hand.getHandlerId());
         log.info("QueryStoragePoolInfo called");
         
         subtask = "getting storagepool instance";
         whereFailed = 1;
         Vector v = packageMgr.queryStoragePoolInformation(user);
         
         proto = DropboxGenerator.queryStoragePoolInformationReply(handle, v);
         
      } catch(DboxException dbex) {
         String errmsg = "Querying storage pool info ";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         log.error(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         proto = DropboxGenerator.genericReplyError(opcode, handle, 
                                                    dbex.getErrorCode(), 
                                                    errmsg);
      }
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }

   
  /* -------------------------------------------------------*\
  ** Replies
  \* -------------------------------------------------------*/
   
  /* -------------------------------------------------------*\
  ** Reply Errors
  \* -------------------------------------------------------*/
  
   
  /* -------------------------------------------------------*\
  ** Events
  \* -------------------------------------------------------*/
   
  /* -------------------------------------------------------*\
  ** Errors
  \* -------------------------------------------------------*/
}
