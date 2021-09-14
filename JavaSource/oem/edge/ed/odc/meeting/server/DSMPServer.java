
package oem.edge.ed.odc.meeting.server;

import oem.edge.ed.odc.meeting.common.*;
import oem.edge.ed.odc.dsmp.common.*;
import oem.edge.ed.odc.dsmp.server.*;
import oem.edge.common.cipher.*;
import oem.edge.ed.odc.tunnel.common.DebugPrint;
import java.lang.*;
import java.io.*;
import java.util.*;
import java.net.*;
import java.sql.*;
import javax.sql.*;
import oem.edge.ed.odc.cntl.metrics.*;
import oem.edge.ed.util.*;
import oem.edge.ed.odc.util.*;
import com.ibm.as400.webaccess.common.*;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 2000-2005,2006		                 */ 
/*                                                                       */ 
/*     All Rights Reserved					         */ 
/*     US Government Users Restricted Rights			         */ 
/*                                                                       */ 
/*   The source code for this program is not published or otherwise      */ 
/*   divested of its trade secrets, irrespective of what has been        */ 
/*   deposited with the US Copyright Office.                             */ 
/*   ------------------------------------------------------------------  */
/*     Please do not remove any of these commented lines  21 lines       */
/*   ------------------------------------------------------------------  */
/*                       Copyright Footer Check                          */
/*   ------------------------------------------------------------------  */ 

/*
** Note: We are using the HandlerID to be the LoginID for now
**       Also, the ParticipantID is also == to LoginID. This is enough as
**       long as the meetingID is always presented at the same time. It is.
**
** Note1: Add timeout to incoming connections, disconnect if not logged in for
**        5 minutes (some value). If lots of errors ... disconnect, etc.
*/

public class DSMPServer extends DSMPSTDispatchBase implements Runnable {

   public String  makehash(String s) {
      
      String ret = null;
      try {
         java.security.MessageDigest d;
         d = java.security.MessageDigest.getInstance("MD5");
         
         d.update(s.getBytes());
         byte arr[] = d.digest();
         StringBuffer ans = new StringBuffer();
         for(int i=0 ; i < arr.length; i++) {
            String v = Integer.toHexString(((int)arr[i]) & 0xff);
            if (v.length() == 1) ans.append("0");
            ans.append(v);
         }
         
         ret = ans.toString();
      } catch(Exception ee) {
         ee.printStackTrace(System.out);
      }
      return ret;
   }

   ConfigObject arguments = new ConfigObject();
   Hashtable    allargs   = new Hashtable();
   boolean doemailsend  = true;
   String smtpserver    = "us.ibm.com";
   String replyAddr     = "econnect@us.ibm.com";
   
   String mailSubject   = "IBM Customer Connect - Web Conference invitation";
   String mailBody = 
      "Dear IBM Customer Connect User:\n\n" +
      "You have been invited to join a web conference by\n" +
      "ID %ownerid% (%ownercompany%).\n\n" +
      "Click the following link to launch the meeting client and\n" +
      "attend the web conference:\n\n   %URL%\n\n" +
      "Delivered by IBM Customer Connect\n" +
      "This is a system generated email. Please do not reply\n" +
      "Please visit http://www.ibm.com/technologyconnect for more\n" +
      "information and to change or reset your IBM Customer Connect password.\n" +
      "Or contact the Customer Connect Help desk for other Web Conference\n" +
      "ID and/or Log-in problems:\n\n" +
      "       Monday-Friday, 8:00am-9:00pm EST USA\n" +
      "             US/Canada: +1-888-220-3343\n" +
      "         International: +1-802-769-3353\n" +
      "                E-mail: eConnect@us.ibm.com";
      

   long frameupdatetime = 0;
   long framesrecv      = 0;
   long resizetime      = 0;
   
   static final String CHATMAN = "@Operator@";
   User chatman         = null;
   ODCipherRSA              cipher        = null;
   String                   automeeting   = null;
   Vector                   automeetings  = new Vector();
   long           automeetingLastModified = 0;
   
   String                   userpwFile    = null;
   long                userpwLastModified = 0;
   
   boolean            enforceInvitePolicy = true;
   
   protected AMTMailer amtMailer             = new AMTMailer();
   
   MappingFiles mapper = null;
   
   FakeHandler fakehandler = null;
   
   boolean doamtprojects   = true;
   
   static final int MAX_HELD_PROTO = 15;
   
   public DSMPServer() {
      super();
      ReloadingProperty prop = new ReloadingProperty();
      try {
         prop.load("edesign_edodc_desktop.properties");
      } catch(IOException ioe) {
         DebugPrint.printlnd(DebugPrint.ERROR,
                             "Error loading desktop properties");
      }
      mapper = new MappingFiles(prop);
      
     // All valid parms in property file for arguments value is num expected parms
     // Excluded is propertyfile which can come in ONLY as cmdline arg. This will be used
     // to check for valid parms during parse args
      allargs.put("MEETING.PORT",                "");
      allargs.put("MEETING.DEBUG",               "");
      allargs.put("MEETING.MAILBODY",            "");
      allargs.put("MEETING.FEHOSTURL",           "");
      allargs.put("MEETING.FEEDESIGNSERVLET",    "");
      allargs.put("MEETING.MAILSUBJECT",         "");
      allargs.put("MEETING.SMTPSERVER",          "");
      allargs.put("MEETING.REPLYADDR",           "");
      allargs.put("MEETING.MEETINGPARMS",        "");
      allargs.put("MEETING.SENDEMAIL",           "");
      allargs.put("MEETING.AMTPROJECTS",         "");
      allargs.put("MEETING.ENFORCEINVITEPOLICY", "");
      allargs.put("MEETING.METRICDRIVER",        "");
      allargs.put("MEETING.METRICURL",           "");
      allargs.put("MEETING.METRICINSTANCE",      "");
      allargs.put("MEETING.METRICPWDIR",         "");
      allargs.put("MEETING.GRPSDRIVER",          "");
      allargs.put("MEETING.GRPSURL",             "");
      allargs.put("MEETING.GRPSINSTANCE",        "");
      allargs.put("MEETING.GRPSPWDIR",           "");
      allargs.put("MEETING.AMTDRIVER",           "");
      allargs.put("MEETING.AMTURL",              "");
      allargs.put("MEETING.AMTINSTANCE",         "");
      allargs.put("MEETING.AMTPWDIR",            "");
      allargs.put("MEETING.COPYGROUPSDB",        "");
      allargs.put("MEETING.COPYMETRICDB",        "");
      allargs.put("MEETING.COPYAMTDB",           "");
      allargs.put("MEETING.TOKENCIPHER",         "");
      allargs.put("MEETING.USERPW",              "");
      allargs.put("MEETING.AUTOMEETING",         "");
      allargs.put("MEETING.VERBOSE",             "");
   }
   
  // The FakeHandler is used to setup fake meetings for CHAT only use in 
  //  Education ... 5/26/04 Now CHAT only use includes scraping ;-)
   class FakeHandler extends DSMPHandler {
      public FakeHandler(DSMPDispatchBase db) {
         super(db);
         
//         db.setDebug(true);
//         DebugPrint.setLevel(DebugPrint.DEBUG);
         
      }
      public void sendProtocolPacket(DSMPBaseProto p) {
        // Only pass along the opcodes which are of interest to CHATMAN
//         System.out.println("Fake: addingProto");
         
         try {
            dispatch.checkProtocol(p);
         } catch(Exception ee) {
         }
         
         switch(p.getOpcode()) {
            case DSMPGenerator.OP_JOINEDMEETING_EVENT:
            case DSMPGenerator.OP_MODERATORCHANGE_EVENT:
            case DSMPGenerator.OP_ENDMEETING_EVENT:
               super.sendProtocolPacket(p);
               break;
            default:
               break;
         }
      }
      
      public void drawMeetingYuk(int meetid) throws InvalidProtocolException {
      
         DSMPProto pn = DSMPGenerator.imageResize((byte)0, meetid,
                                                  0, 0, 640, 640);
         dispatch.dispatchProtocol(pn, this);
         
         byte white[] = new byte[4*64*64];
         byte black[] = new byte[4*64*64];
         byte whiteU[] = new byte[4*64*64];
         byte blackU[] = new byte[4*64*64];
         
         int whiteul = 0;
         int blackul = 0;
         
         for(int i = 0; i < (4096*4); i+=4) {
            white[i]   = (byte)0xff;
            white[i+1] = (byte)0xff;
            white[i+2] = (byte)0xff;
            white[i+3] = (byte)0xff;
            black[i]   = (byte)0xff;
            black[i+1] = (byte)0x00;
            black[i+2] = (byte)0x00;
            black[i+3] = (byte)0x00;
         }
         
         whiteul = DSMPGenerator.runlengthEncode(whiteU, 0,
                                                 white,  0,
                                                 white.length);
         blackul = DSMPGenerator.runlengthEncode(blackU, 0,
                                                 black,  0,
                                                 black.length);
         
        /*
          int uud[] = DSMPGenerator.runlengthDecode(white, null,
          0, 
          whiteul, 
          white.length);
          
          
          String s = DebugPrint.showbytes(white, 0, white.length);
          System.out.println("White = \n" + s);
          s = DebugPrint.showbytes(whiteU, 0, whiteul);
          System.out.println("WhiteU = \n" + s);
          s = DebugPrint.showbytes(uud, 0, white.length/4);
          System.out.println("WhiteUU = \n" + s);
        */
         
         for(int x = 0; x < 10; x++) {
            for(int y = 0; y < 10; y++) {
               
               byte data[];
               int datal;
               if (((x ^ y) & 1) == 0) {
                  data  = blackU;
                  datal = blackul;
               } else {
                  data = whiteU;
                  datal = whiteul;
               }
               
               pn = DSMPGenerator.frameUpdate((byte)0, false,
                                              meetid, x*64, y*64,
                                              64, 64, data,
                                              0, datal);
               
               dispatch.dispatchProtocol(pn, this);
            }
         }
         pn = DSMPGenerator.frameEnd((byte)0, meetid);
         dispatch.dispatchProtocol(pn, this);
      }
            
      public void run() {
         Hashtable mymeetings = new Hashtable();
         while(!done) {
            try {
//               System.out.println("Top of fake loop");
               DSMPBaseProto p = getNextProtoPacket();
               
              // Note, resetting the cursor SHOULD be ok. The cursor is shared
              //  by ALL entities associated with the Protocol, BUT, we are the
              //  only entity which cares to walk the object in the server 
              //  (other than debug code and the dispatcher)
               
               if (p == null && queuedProtoByteCount <= 0) {
            
                 // We have nothing left to send, allow subclasses to generate
                 //  any protocol it feels is needed here
                              
                  synchronized(addLock) {
                     if (queuedProtoByteCount <= 0 && !done) {
                        try {
                           
//                           System.out.println("Fake: Sleeping");
                           addLock.wait();
//                           System.out.println("Fake: Waking");
                        } catch(InterruptedException ex) {}
                     }
                  }
               }
               
               if (p == null) continue;
               
//               System.out.println("Fake: Got Some Proto");
//               dispatch.checkProtocol(p);
               
               p.resetCursor();
               switch(p.getOpcode()) {
               
                 // If we are set as Moderator of a meeting, just note that
                 //     moderator has changed. If we are moderator, then add
                 //     entry to mymeetings table, if we are not, remove it
                  case DSMPGenerator.OP_MODERATORCHANGE_EVENT: {
                     int meetid = p.getInteger();
                     int fromid = p.getInteger();
                     int toid   = p.getInteger();
                     Integer meetI = new Integer(meetid);
                     if (toid != handlerid) {
                        mymeetings.remove(meetI);
                     } else {
                        mymeetings.put(meetI, meetI);
                        drawMeetingYuk(meetid);
                     }
                     break;
                  }
                  
                 // If we are the moderator, then send him our content
                  case DSMPGenerator.OP_JOINEDMEETING_EVENT: {
                    // Lookup the meeting
                    // If we are moderator ... Generate Proto and send
                     int meetid   = p.getInteger();
                     int inviteid = p.getInteger();
                     int partid   = p.getInteger();
                     Integer meetI = new Integer(meetid);
                     
                    // If its CHATMAN, check if he is moderator
                     if (partid == handlerid) {
                        if ((p.getFlags() & 0x04) != 0) {
                           mymeetings.put(meetI, meetI);
                        }
                        
                     } else if ((partid != handlerid && 
                                 (mymeetings.get(meetI) != null))) {
//                        System.out.println("Someone Joined MY Meeting HA hahaha");
                       // If this is the first owner (other then I, CHATMAN!),
                       //  then make him the moderator.
                        int meetingid = meetid;
                        Meeting lmeeting = 
                           (Meeting)meetings.get(new Integer(meetingid)); 
                        if (lmeeting != null && lmeeting.isOwner(partid) &&
                            lmeeting.numOwners() == 2) {
                            
                          // Copied from fireTransferModerator Should make a 
                          //  routine
                           byte handle = 0;
                           DSMPProto proto;
                           proto=DSMPGenerator.controlEvent(handle, meetingid, 
                                                    lmeeting.getModeratorId());
                           lmeeting.sendToParticipants(DSMPServer.this, 
                                                       proto, false, 0);
                           
                           proto=DSMPGenerator.frozenModeEvent(meetingid, 
                                                               false);
                           lmeeting.sendToParticipants(DSMPServer.this, 
                                                       proto, false, 0);
                           
                           proto = DSMPGenerator.stopShareEvent((byte)0,
                                                      lmeeting.getMeetingId());
                           lmeeting.sendToParticipants(DSMPServer.this, 
                                                       proto, false, 0);
                           
                           proto = 
                              DSMPGenerator.moderatorChangeEvent((byte)0, 
                                                    lmeeting.getMeetingId(),
                                                    lmeeting.getModeratorId(), 
                                                                 partid);
                           
                           lmeeting.sendToParticipants(DSMPServer.this,
                                                       proto, false, 0);
                           
                           lmeeting.setModeratorId(partid);
                           lmeeting.setController(partid);
                           
                           proto=DSMPGenerator.controlEvent(handle, meetingid, 
                                                     lmeeting.getController());
                           lmeeting.sendToParticipants(DSMPServer.this, 
                                                       proto, false, 0);
                        } else {
                           drawMeetingYuk(meetid);
                        }
                     }
                     break;
                  }
               
                 // If a meeting CHATMAN is attending is ended, then we simply
                 //     autoload it again
                  case DSMPGenerator.OP_ENDMEETING_EVENT: {
                     int meetid = p.getInteger();
                     Integer meetI = new Integer(meetid);
                     mymeetings.remove(meetI);
                     refreshAutoMeetingFile(true);
                     break;
                  }
                  
                  default:
                     break;
               }
            } catch(Throwable ee) {
               System.out.println("CHATMAN Handler: Exception thrown!");
               ee.printStackTrace(System.out);
            }
         }
         System.out.println("Exiting RUN!! Done = " + done);
      }
   }

   class User extends UserInfo {
   
      int    loginid  = 0; /* IDGenerator.getId();  Using handlerId for now */
      boolean tokenLogin = false;
      String desktopid = null;
      String salt      = null;
      
      int meetingid = -1;
      
      public User(String name, int lid) {
         super(name);
         loginid = lid;
      }
      public User(String name, Vector projs, int loginid) {
         super(name, projs);
         this.loginid = loginid;
      }
      
      public String  getSalt()         { return salt; }
      public void    setSalt(String v) { salt = v; }
      public String  generateSalt() {
         setSalt(makehash(new java.util.Date().toString()));
         return getSalt();
      }
      
      public String  getDesktopId()         { return desktopid; }
      public void    setDesktopId(String c) { desktopid = c;    }
      
      public boolean getTokenLogin()          { return tokenLogin; }
      public void    setTokenLogin(boolean v) { tokenLogin = v;    }
      
      public int     getLoginId()    { return loginid;  }
      
     // Set/get the meeting we think we are in
      public int     getMeetingId()    { return meetingid;  }
      public void    setMeetingId(int mid)    { meetingid = mid;  }
   }
   
   class Invite {
      String  name      = null;
      boolean isOwn     = false;
      int     inviteid  = IDGenerator.getId();
      int     meetingid = 0;
      byte    inviteType = (byte)0;
      
      long    addtime   = System.currentTimeMillis();
      
      public  long getAddTime() { return addtime; }
      
      public Invite(String name, byte inviteType, int meetingid) {
         this.name       = name;
         this.inviteType = inviteType;
         this.meetingid  = meetingid;
      }
      
      public byte    getInviteType()    { return inviteType; }
      
      public boolean isProject() { 
         return inviteType == DSMPGenerator.STATUS_PROJECT;
      }
      
      public boolean isGroup() { 
         return inviteType == DSMPGenerator.STATUS_GROUP;
      }

      public boolean isUser() { 
         return inviteType == DSMPGenerator.STATUS_NONE;
      }
      
      public boolean isOwner()          { return isOwn;  }
      public void    isOwner(boolean v) { isOwn = v;     }
      
      public String  getName()          { return name;      }
      public int     getInviteId()      { return inviteid;  }
      public int     getMeetingId()     { return meetingid; }
   }
   
   class Meeting {
   
     // ID lookup for invitations
      Hashtable idinvitations  = new Hashtable();
      
     // All user based invited go here
      Hashtable uinvitations  = new Hashtable();
      
     // All project based invites go here
      Hashtable pinvitations = new Hashtable();
      
     // All group based invites go here
      Hashtable ginvitations = new Hashtable();
   
     // Integer elements have direct Invite object
      Hashtable  participants = new Hashtable();
      
     // ID for the meeting
      int     meetingid      = IDGenerator.getId();
      
     // JMC 5/21/04 - Current MODERATOR id ... can be different than owner
      int     moderatorid    = 0;
      
     // JMC 5/24/04 - owners contains all id's tagged as owner IDs
      Vector owners = new Vector();
      
      String  title          = "";
      String  pw             = "";
      String  classification = "";
      
      int     controller     = -1;
      
      boolean frozen         = false;
      // MPZ 12/20/04 - Now have awareness that sharing is active.
      boolean sharing        = false;
      boolean remotedebug    = false;
      
      boolean acceptscoldcalls = false;
   
   
      public boolean acceptsColdCalls()             { return acceptscoldcalls;}
      public void    acceptsColdCalls(boolean v)    { acceptscoldcalls = v;   }
   
      public String  getPassword()                  { return pw;  }
      public void    setPassword(String password)   { pw=password;}
      public String  getHashedPassword()            { return makehash(pw);   }
      
      public boolean isCorrectPassword(String pass) {
         String meetingpw = getPassword();
         String hashpw    = getHashedPassword();
         boolean ret = false;
         if (meetingpw.length() > 0 && 
             (pass.equals(meetingpw) || pass.equals(hashpw))) {
            ret = true;
         }
         return ret;
      }
      
      public String  getTitle()           { return title;          }
      public int     getAnyOwnerId()      { 
         synchronized(owners) {
            if (numOwners() == 0) return 0;
            else                  return ((Integer)owners.elementAt(0)).intValue();
         }
      }
      public boolean isOwner(int v) {
         return owners.contains(new Integer(v));
      }
      public boolean isOwner(String v) {
         return getOwner(v) != null;
      }
      public int numOwners() {
         return owners.size();
      }
      public Vector getOwners()      { 
         return (Vector)owners.clone();
      }
      public User  getOwner(String name)      { 
         Vector lv = (Vector)owners.clone();
         Enumeration enum = lv.elements();
         while(enum.hasMoreElements()) {
            Integer id = (Integer)enum.nextElement();
            User u = getUser(id.intValue());
            if (u != null && u.getName().equals(name)) {
               return u;
            }
         }
         return null;
      }
      
      public void    addOwner(int ownerid)      { 
         Integer v = new Integer(ownerid);
         synchronized(owners) {
            int idx = owners.indexOf(v);
            if (idx == -1) {
               owners.addElement(v);
            }
         }
      }
      public void    removeOwner(int ownerid)   { 
         owners.remove(new Integer(ownerid));
      }
      
      
      public int     getMeetingId()       { return meetingid;      }
      public String  getClassification()  { return classification; }
      
      public void    setModeratorId(int v){ moderatorid = v;       }
      public int     getModeratorId()     { return moderatorid;    }
      
      public void    setFrozen(boolean v) { frozen = v;            }
      public boolean getFrozen()          { return frozen;         }
      
      public void    setSharing(boolean v){ sharing = v;           }
      public boolean getSharing()         { return sharing;        }
      
      public void    setRemoteDebug(boolean v) { remotedebug = v;       }
      public boolean getRemoteDebug()          { return remotedebug;    }
      
      public int               getController()         { return controller; }
      public void              setController(int cont) { controller = cont; }
      
     // Metrics
      Meetingdata thismeeting = null;
      public void setDesktopId(String s)   { 
         thismeeting.setKey(s); 
      }
      public String getDesktopId() { 
         return thismeeting.getKey();
      }
      
      public int canControl(DSMPServer server, 
                            int participantid, 
                            boolean forceAdd) {
                            
        // Default return is failure
         int ret = 1;
         
        // Only do this if token mode
         if (cipher == null) {
            return 0; // Works if NOT in token mode
         }
         
         
         try {
           // JMC 5/21/04 - Changed from ownerid to getModeratorId
           //               Should be the desktop being scraped who 
           //               determines who can be in control
            User ownerU = server.getUser(getModeratorId());
            User partU  = server.getUser(participantid);
            
            if (ownerU == null || partU == null) {
               ret = 1;
            } else {
               Object methparms[] = new Object[2];
               String owncomp  = ownerU.getCompany();
               String partcomp = partU.getCompany();
               
               ret = mapper.allowParticipateAccess(owncomp, partcomp) ? 0 : 1;
               
               if (ret == 0) {
                 /* 
                 ** Used to only do this for IBM ... now for everyone
                 **
                 if (methparms[0] != null && 
                 ownerU.getCompany().equalsIgnoreCase("IBM")) {
                 
                 if (methparms[1] == null ||
                 !partU.getCompany().equalsIgnoreCase("IBM")) {
                 if (!forceAdd) ret = 2;
                 }
                 }
                 */
                  if (owncomp == null || partcomp == null || 
                      !owncomp.equals(partcomp)) { 
                     if (!forceAdd) ret = 2;
                  }
               }
            }
         } catch(Exception ee) {
            ret = 1;
         }
         
         return ret;
      }
      
      public boolean canInvite(DSMPServer server, 
                               int participantid) {
                               
         boolean ret = false;
            
         if (isOwner(participantid)) return true;
         
         Object arr[] = owners.toArray();
         for(int i=0; !ret && i < arr.length; i++) {
            try {
         
               int ownerid = ((Integer)arr[i]).intValue();
            
            
              // JMC 5/21/04 - Remains ownerid (not getModeratorId) cause we 
              //               want invitations mitigated by the real owner
               User ownerU = server.getUser(ownerid);
               User partU  = server.getUser(participantid);
               
               if (enforceInvitePolicy) {
                  String ownerCompany = ownerU.getCompany();
                  String ownerCountry = ownerU.getCountry();
                  String partCompany  = partU.getCompany();
                  String partCountry  = partU.getCountry();
                  
                 // If we have the method, call it, otherwise, do the default
                  ret = mapper.allowCompanyInvite(ownerCompany, ownerU.getName(),
                                                  partCompany,  partU.getName());
               } else {
                  ret = true;
               }
               
            } catch(NullPointerException ee) {
               ee.printStackTrace(System.out);
               ret = false;
            }
         }
         return ret;
      }
      
      
      Vector getFlatInviteList() {
      
         Vector v = new Vector();
         Invite invite;
         Enumeration enum = ((Hashtable)uinvitations.clone()).keys();
         while(enum.hasMoreElements()) {
            String n = (String)enum.nextElement();
            if (!v.contains(n)) v.addElement(n);
         }
         
        /*
         if (pinvitations.size() > 0) synchronized(pinvitations) {
            Vector projs = user.getProjects();
            if (projs != null && projs.size() != 0) {
               Enumeration e = pinvitations.keys();
               while(e.hasMoreElements()) {
                  String p = (String)e.nextElement();
                  if (projs.contains(p)) {
                     invite = (Invite)pinvitations.get(p);
                     return invite;
                  }
               }
            }
         }
        */
        
         if (ginvitations.size() > 0) try {
            
            Vector lov = getOwners();
            Enumeration love = lov.elements();
            while(love.hasMoreElements()) {
               int uid = ((Integer)love.nextElement()).intValue();
               User user = getUser(uid);
               if (user != null) synchronized(ginvitations) {
                  Hashtable grps = Groups.getMatchingGroups(user, null, 
                                                            false, false, 
                                                            false,
                                                            true, false, 
                                                            false, 
                                                            true, true, 
                                                            false);
                  if (grps != null && grps.size() > 0) {
                     Enumeration e = ginvitations.keys();
                     while(e.hasMoreElements()) {
                        String g = (String)e.nextElement();
                        GroupInfo gi = (GroupInfo)grps.get(g);
                        if (gi != null) {
                           Vector gm = gi.getGroupMembers();
                           Enumeration eenum = gm.elements();
                           while(eenum.hasMoreElements()) {
                              String n = (String)eenum.nextElement();
                              if (!v.contains(n)) v.addElement(n);
                           }
                        }
                     }
                  }
               }
            }
         } catch(DboxException dbex) {
            System.out.println("Error looking up matching groups");
            dbex.printStackTrace(System.out);
         }
         
         return v;
      }
      
      boolean containsInvite(String name, byte inviteType) {
         return getInvite(name, inviteType) != null;
      }
      
      Invite getInvite(String name, byte inviteType) {
         if (inviteType == DSMPGenerator.STATUS_NONE)
            return (Invite)uinvitations.get(name);
         if (inviteType == DSMPGenerator.STATUS_PROJECT)
            return (Invite)pinvitations.get(name);
         if (inviteType == DSMPGenerator.STATUS_GROUP)
            return (Invite)ginvitations.get(name);
      
         return null;
      }
      
     // Want to know of the User with the same NAME is in this meeting
      boolean containsUser(DSMPServer server, String name) {
         boolean ret = false;
         Vector users = (Vector)server.getUser(name);
         if (users != null) {
            Object arr[] = users.toArray();
            for(int i=0; i < arr.length; i++) {
               User u = (User)arr[i];
               ret = containsParticipant(u.getLoginId());
               if (ret) break;
            }
         }
         return ret;
      }
      
     //  Providing loginid cause thats simpler for me where I'm calling this
      boolean containsUser(DSMPServer server, int loginid) {
         boolean ret = false;
         User user = server.getUser(loginid);
         if (user != null) {
            ret = containsUser(server, user.getName());
         }
         return ret;
      }
      
      boolean containsParticipant(int partid) {
         return participants.get(new Integer(partid)) != null;
      }
      
      public Invite getInviteForUser(DSMPServer server, int loginid) {
         Invite invite = null;
         User user      = server.getUser(loginid);
         if (user != null) {
         
            if (!canInvite(server, loginid)) {
               return null;
            }
         
            invite = (Invite)uinvitations.get(user.getName());
            
            if (invite != null) return invite;
            
            if (pinvitations.size() > 0) synchronized(pinvitations) {
               Vector projs = user.getProjects();
               if (projs != null && projs.size() != 0) {
                  Enumeration e = pinvitations.keys();
                  while(e.hasMoreElements()) {
                     String p = (String)e.nextElement();
                     if (projs.contains(p)) {
                        invite = (Invite)pinvitations.get(p);
                        return invite;
                     }
                  }
               }
            }
            
            if (ginvitations.size() > 0) try {
               synchronized(ginvitations) {
                  Hashtable grps = Groups.getMatchingGroups(user, null, false, 
                                                            false, false, true,
                                                            false, false, false,
                                                            false, false);
                  if (grps != null && grps.size() > 0) {
                     Enumeration e = ginvitations.keys();
                     while(e.hasMoreElements()) {
                        String g = (String)e.nextElement();
                        if (grps.containsKey(g)) {
                           invite = (Invite)ginvitations.get(g);
                           return invite;
                        }
                     }
                  }
               }
            } catch(DboxException dbex) {
               System.out.println("Error looking up matching groups");
               dbex.printStackTrace(System.out);
            }
         }
         return null;
      }
      
      boolean addParticipant(DSMPServer server, int loginid, 
                             DSMPProto inproto){
                             
         boolean ret = false;
         
         User user     = server.getUser(loginid);
         Invite invite = getInviteForUser(server, loginid);
         if (invite != null && user != null) {
         
           // Create ref between User and Meeting
            user.setMeetingId(getMeetingId());
         
           // Make him an owner based on Invite
            System.out.println("Adding loginid = " + loginid + " to meeting");
            if (invite.isOwner()) {
            
               System.out.println("He is an owner");
               addOwner(loginid);
            
              // If he is the first owner in ... make him Moderator
               
               System.out.println("numOwners now " + numOwners());
               if (numOwners() == 1) {
                  System.out.println("Make him Moderator");
                  setModeratorId(loginid);
                  setController(loginid);
               }
               
            } else {
               removeOwner(loginid);
            }
         
            ret = true;
            
           // Send the proto sent in if we made it this far
            if (inproto != null) {
               sendProtoTo(inproto, loginid);
            }
            
           // Tell participant about invitees (he is one also).
            Enumeration e = idinvitations.elements();
            while(e.hasMoreElements()) {
               Invite inv = (Invite)e.nextElement();
               DSMPProto proto = 
                  DSMPGenerator.newInvitationEvent((byte)0, meetingid, 
                                                   inv.getInviteType(),
                                                   inv.isOwner(),
                                                   inv.getInviteId(),
                                                   inv.getName());
               sendProtoTo(proto, loginid);
            }
            
           // Tell particpant about his own arrival.
            DSMPProto proto = 
                  DSMPGenerator.joinedMeetingEvent((byte)0, 
                                                   invite.getInviteId(),
                                                   meetingid, 
                                                   loginid, loginid,
                                                   user.getName(),
                                                   invite.getInviteType(),
                                                   isOwner(loginid),
                                                   loginid == getModeratorId());
            sendProtoTo(proto, loginid);
                                                   
            proto = DSMPGenerator.ownershipChangeEvent((byte)0, 
                                                       meetingid,
                                                       loginid,
                                                       isOwner(loginid));
            sendProtoTo(proto, loginid);
               
           // Tell participant about all other participants
            Enumeration enum = participants.keys();
            while(enum.hasMoreElements()) {
               Integer iv = (Integer)enum.nextElement();
               int v = iv.intValue();
               User u = server.getUser(v);
               Invite inv = (Invite)participants.get(iv);
               proto = 
                  DSMPGenerator.joinedMeetingEvent((byte)0, 
                                                   inv.getInviteId(),
                                                   meetingid, 
                                                   v, v,
                                                   u.getName(),
                                                   inv.getInviteType(),
                                                   isOwner(v),
                                                   v == getModeratorId());
               server.sendProtoTo(proto, loginid);
               
               proto = DSMPGenerator.ownershipChangeEvent((byte)0, 
                                                          meetingid,
                                                          v,
                                                          isOwner(v));
               sendProtoTo(proto, loginid);
            }
            
           // Send out a current states: moderator, sharing, control frozen.
            proto = DSMPGenerator.moderatorChangeEvent((byte)0, 
                                                       meetingid,
                                                       getModeratorId(),
                                                       getModeratorId());
            sendProtoTo(proto, loginid);
            
            if (sharing) {
               proto=DSMPGenerator.startShareEvent((byte) 0, meetingid);
               sendProtoTo(proto, loginid);
            }

            proto=DSMPGenerator.controlEvent((byte)0, meetingid, 
                                             getController());
            sendProtoTo(proto, loginid);
            
            proto=DSMPGenerator.frozenModeEvent(meetingid, frozen);
            sendProtoTo(proto, loginid);

           // Tell all participants about new guy
            if (participants.size() != 0) {
               proto = 
                  DSMPGenerator.joinedMeetingEvent((byte)0, 
                                                   invite.getInviteId(),
                                                   meetingid, 
                                                   loginid, loginid,
                                                   user.getName(),
                                                   invite.getInviteType(),
                                                   isOwner(loginid),
                                                   loginid == getModeratorId());
                                                   
               sendToParticipants(server, proto, false, 0); 
               
              // Send out a moderator event to all if this guy is the moderator
               if (loginid == getModeratorId()) { 
                  proto = DSMPGenerator.moderatorChangeEvent((byte)0, 
                                                             meetingid,
                                                             loginid, loginid);
                  sendToParticipants(server, proto, false, 0);
               }
               
              // Send out a ownership event to all for this guy
               proto = DSMPGenerator.ownershipChangeEvent((byte)0, 
                                                          meetingid,
                                                          loginid,
                                                          isOwner(loginid));
               sendToParticipants(server, proto, false, 0);
               
            }
            
           // Add new guy
            participants.put(new Integer(loginid), invite);
            
           // Metrics
            addInvitationData(server, 
                              invite!=null ? invite.getAddTime()
                                           : System.currentTimeMillis(), 
                              loginid);
         }
         
         return ret;
      }
         
      boolean removeParticipant(DSMPServer server, int loginid) {
         boolean ret  = false;
         Integer iv    = new Integer(loginid);
         Invite invite = (Invite)participants.get(iv);
         if (invite != null) {
            ret = true;
            
            DSMPProto proto = null;
            
            removeOwner(loginid);
            
           // If no Owners left ... just gak the meeting
            if (numOwners() == 0) {
               endMeeting(server);
               return true;
            }
            
            if (getModeratorId() == loginid) {
               
               proto=DSMPGenerator.controlEvent((byte)0, meetingid, 
                                                getModeratorId());
               sendToParticipants(server, proto, true, loginid);
               
               proto=DSMPGenerator.frozenModeEvent(meetingid, false);
               sendToParticipants(server, proto, true, loginid);
               
               proto = DSMPGenerator.stopShareEvent((byte)0, getMeetingId());
               sendToParticipants(server, proto, true, loginid);
               
               int newmod = getAnyOwnerId();
               setModeratorId(newmod);
               proto = DSMPGenerator.moderatorChangeEvent((byte)0, 
                                                          getMeetingId(),
                                                          loginid, newmod);
               sendToParticipants(server, proto, true, loginid);
               
               setController(getModeratorId());
               proto=DSMPGenerator.controlEvent((byte)0, meetingid, 
                                                getModeratorId());
               sendToParticipants(server, proto, true, loginid);
            } else if (loginid == getController()) {
              // JMC 5/21/04 - Changed from getOwnerId to getModeratorId 
               setController(getModeratorId());
               proto=DSMPGenerator.controlEvent((byte)0, meetingid, 
                                                getModeratorId());
               sendToParticipants(server, proto, true, loginid);
            }
            
            proto = DSMPGenerator.droppedEvent((byte)0, meetingid, 
                                               invite.getInviteId(),
                                               loginid, false, true);
            
            sendToParticipants(server, proto, false, 0);
            
           // Metrics
            terminateInvitationData(server, iv.intValue());
            
            participants.remove(iv);
            
         }
         return ret;
      }
      
      Invite addInvitation(DSMPServer server, String name, 
                           byte inviteType, boolean isOwn) {
         return addInvitation(server, null, name, inviteType, isOwn);
      }
                           
      Invite addInvitation(DSMPServer server, User user, String name, 
                           byte inviteType, boolean isOwn) {
        
         Invite invite = null;        
         
         synchronized(idinvitations) {
            
            Hashtable hash = uinvitations;
            if (inviteType == DSMPGenerator.STATUS_NONE) {
            
               hash = uinvitations;
               
              // TODO: If we are going to AMT check individual users, 
              //        add that code here
              
            } else if (inviteType == DSMPGenerator.STATUS_PROJECT) {
            
               hash = pinvitations;
               
              // No invite if adding user does not contain project
               if (user != null && !user.getProjects().contains(name)) {
                  return null;
               }
            } else if (inviteType == DSMPGenerator.STATUS_GROUP) {
            
               hash = ginvitations;
               
              // No invite if adding user does not have visibility to group
               if (user != null) {
                  Hashtable h = null;
                  try {
                     h = Groups.getMatchingGroups(user, name, 
                                                  false, false, false, 
                                                  false, true, false,
                                                  false, false, false);
                  } catch(DboxException ee) {
                  }
                  if (h == null || h.get(name) == null) {
                     return null;
                  }
               }
            }
               
            invite = new Invite(name, inviteType, meetingid);
            invite.isOwner(isOwn);
            hash.put(invite.getName(), invite);
            idinvitations.put(new Integer(invite.getInviteId()), invite);
         }
         
        // Tell all participants about invite
         if (participants.size() != 0) {
            DSMPProto proto = 
               DSMPGenerator.newInvitationEvent((byte)0, meetingid, 
                                                invite.getInviteType(),
                                                invite.isOwner(),
                                                invite.getInviteId(),
                                                invite.getName());
            sendToParticipants(server, proto, false, 0);
         }
         return invite;
      }
      
      boolean removeInvitation(DSMPServer server, int inviteid) {
         boolean ret  = false;
         Invite invite = null;
         synchronized(idinvitations) {
            invite = (Invite)idinvitations.remove(new Integer(inviteid));
            if (invite != null) {
               ret = true;
               Hashtable hash = uinvitations;
               byte inviteType = invite.getInviteType();
               if (inviteType == DSMPGenerator.STATUS_NONE)
                  hash = uinvitations;
               if (inviteType == DSMPGenerator.STATUS_PROJECT)
                  hash = pinvitations;
               if (inviteType == DSMPGenerator.STATUS_GROUP)
                  hash = ginvitations;
               hash.remove(invite.getName());
            }
         }
         
         if (ret && participants.size() != 0) {
            DSMPProto proto = DSMPGenerator.droppedEvent((byte)0, meetingid, 
                                                         invite.getInviteId(),
                                                         0, true, false);
            sendToParticipants(server, proto, false, 0);
         }
         return ret;
      }
      
      
     // Metrics
      public void addInvitationData(DSMPServer server, long inviteTime, 
                                    int i) {
         User u = server.getUser(i);
         if (u != null) {
            Invitationdata inv = new Invitationdata();
            inv.setInvitationId(""+i);
            inv.setPartySessionId(u.getDesktopId());
            inv.setPartyEdgeId(u.getName());
            inv.setPartyCompany(u.getCompany());
            inv.setPartyCountry(u.getCountry());
            inv.setStartTime(inviteTime);
            inv.setState("Accepted");
            inv.setEndTime(System.currentTimeMillis());
            thismeeting.addInvitation(inv);
         }
      }
      public void terminateInvitationData(DSMPServer server, int i) {
         Invitationdata inv = thismeeting.getInvitation(""+i);
         if (inv != null) {
            inv.fixEndTime(System.currentTimeMillis());
            thismeeting.removeInvitation(inv.getInvitationId());
            inv.setInvitationId((""+inv.getEndTime())+i);
            thismeeting.addInvitation(inv);
         }
      }
      protected void writeMeetingData(DSMPServer server) {
         thismeeting.fixEndTime(System.currentTimeMillis());
         StringBuffer ret = new StringBuffer();
         Connection conn = null;
         DBConnection dbconn = null;
         try {
            dbconn = DBSource.getDBConnection("EDODC");
            conn = dbconn.getConnection();
            thismeeting.dooutput(ret, conn, dbconn.getInstance(), 
                                 Desktopdata.MeetingTable, 0);
         } catch(Exception ee) {                  
            System.out.println("Error writing to DB2: " + ee.toString());
            ret = new StringBuffer();
            thismeeting.dooutput(ret, null, dbconn.getInstance(), 
                                 Desktopdata.MeetingTable, 0);
            System.out.println(ret.toString());
            if (dbconn != null) {
               dbconn.destroyConnection(conn);
            }
         } finally {
            if (dbconn != null) {
               dbconn.returnConnection(conn);
            }
         }
      }
      
      public void endMeeting(DSMPServer server) {
         long time = System.currentTimeMillis();
         
        // Make sure the automeeting table is updated PRIOR to 
        //  event generation, as CHATMAN will be doing his business
         automeetings.remove(new Integer(getMeetingId())); 
         
         Object arr[] = participants.keySet().toArray();
         
         if (arr.length > 0) {
            DSMPProto proto = DSMPGenerator.endMeetingEvent((byte)0, meetingid);
            sendToParticipants(server, proto, false, 0);
            
            for(int i=0; i < arr.length; i++) {
               int v  = ((Integer)arr[i]).intValue();
               terminateInvitationData(server, v);
            }
            participants.clear();
         }
         
         writeMeetingData(server);
      }
      
      public void sendToParticipants(DSMPServer server, Vector vec,
                                     boolean excludeAny, int anyid) {
                                     
        // Keep the synchronizations from propagating out of routine 
         Object arr[] = participants.keySet().toArray();
         for(int i=0; i < arr.length; i++) {
            int v = ((Integer)arr[i]).intValue();
            if (!excludeAny || v != anyid) {
               Enumeration enum2 = vec.elements();
               while(enum2.hasMoreElements()) {
                  DSMPProto p = (DSMPProto)enum2.nextElement();
                  server.sendProtoTo(p, v);
               }
            }
         }
      }
      
      public void sendToOwners(DSMPProto proto, boolean excludeAny,
                               int anyid) {
                               
        // Remove SYNC
         Object arr[] = owners.toArray();
         
         for(int i=0; i < arr.length; i++) {
            int v = ((Integer)arr[i]).intValue();
            if (!excludeAny || v != anyid) {
               sendProtoTo(proto, v);
            }
         }
      }
      
      public void sendToParticipants(DSMPServer server, DSMPProto proto,
                                     boolean excludeAny, int anyid) {
                                     
        // Remove SYNC
         Object arr[] = participants.keySet().toArray();
         
         for(int i=0; i < arr.length; i++) {
            int v = ((Integer)arr[i]).intValue();
            if (!excludeAny || v != anyid) {
               server.sendProtoTo(proto, v);
            }
         }
      }
      
      public Meeting(int loginid, String title, String pw, String cls) {
         this.title          = title;
         this.pw             = pw;
         this.classification = cls;
         this.controller     = loginid;
         this.owners.addElement(new Integer(loginid));
         
        // Metrics
         thismeeting = new Meetingdata();
         thismeeting.setStartTime(System.currentTimeMillis());
         thismeeting.setMeetingId(""+getMeetingId());
      }
   }
   
  // User key Vector value (first elem pw, reset projects)
   Hashtable userpw      = new Hashtable();
   
  // String elements have vector of User objects
  // Integer elements have a direct User object
   Hashtable users       = new Hashtable();
   
  // Integer elements have direct Meeting objects
   Hashtable meetings    = new Hashtable();

  // Integer elements have direct Handler objects
   Hashtable handlers    = new Hashtable();
   
   String FEHostURL  = "https://www-309.ibm.com";
   String ESS        = "technologyconnect/EdesignServicesServlet.wss";
   String MeetingParms = "?op=5&sc=launch";

   
   int localport            = 0;
   
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
   
      System.out.println("Accept thread started. Listening on port " + 
                         localport);
                         
     //httpServer.startServer(8080, localport);
                         
      while (true) {
         try {
            Socket sock = socket.accept();
            if (sock == null) {
               System.out.println("DSMPServer: Got a NULL Socket Done!"); 
               break;
            }	
            
            DSMPHandler handler = new DSMPSTSocketHandler(sock, this);
            handlers.put(new Integer(handler.getHandlerId()), handler);
                
         } catch (IOException e){			
            e.printStackTrace(System.out);
         }
      }
      
      try {socket.close();} catch(Throwable tt) {}
   }         
   
   public boolean convertArgs(String args[]) {
      String parm = "nada";
      try {
         for(int i=0; i < args.length; i++) {
            parm = args[i];
            if (args[i].equalsIgnoreCase("-port")) {
               arguments.setProperty("meeting.port", args[++i]);
            } else if (args[i].equalsIgnoreCase("-debug")) {
               arguments.setProperty("meeting.debug", "true");
            } else if (args[i].equalsIgnoreCase("-FEHostURL")) {
               arguments.setProperty("meeting.FEHostURL", args[++i]);
            } else if (args[i].equalsIgnoreCase("-FEEdesignServlet")) {
               arguments.setProperty("meeting.FEEdesignServlet", args[++i]);
            } else if (args[i].equalsIgnoreCase("-mailbody")) {
               arguments.setProperty("meeting.mailbody", args[++i]);
            } else if (args[i].equalsIgnoreCase("-propertyfile")) {
         
              // Copy incoming property file contents to incore property map
               try {
                  ConfigFile config = new ConfigFile();
                  
                  config.load(SearchEtc.findFileInClasspath(args[++i]));
                  Enumeration enum = config.getPropertyNames();
                  while(enum.hasMoreElements()) {
                     String n = (String)enum.nextElement();
                     arguments.setProperty(n, config.getProperty(n));
                  }
                  
               } catch(IOException ioe) {
                  System.err.println("-mailbody: Error opening/loading " + 
                                     args[i]);
                  return false;
               }
            } else if (args[i].equalsIgnoreCase("-mailSubject")) {
               arguments.setProperty("meeting.mailsubject", args[++i]);
            } else if (args[i].equalsIgnoreCase("-emailsend")) {
               arguments.setProperty("meeting.sendemail", "true");
            } else if (args[i].equalsIgnoreCase("-noemailsend")) {
               arguments.setProperty("meeting.sendemail", "false");
            } else if (args[i].equalsIgnoreCase("-smtpserver")) {
               arguments.setProperty("meeting.smtpserver", args[++i]);
            } else if (args[i].equalsIgnoreCase("-replyaddr")) {
               arguments.setProperty("meeting.replyaddr", args[++i]);
            } else if (args[i].equalsIgnoreCase("-meetingparms")) {
               arguments.setProperty("meeting.meetingparms", args[++i]);
            } else if (args[i].equalsIgnoreCase("-amtprojects")) {
               arguments.setProperty("meeting.amtprojects", "true");
            } else if (args[i].equalsIgnoreCase("-noamtprojects")) {
               arguments.setProperty("meeting.amtprojects", "false");
            } else if (args[i].equalsIgnoreCase("-noenforceinvitepolicy")) {
               arguments.setProperty("meeting.enforceinvitepolicy", "false");
            } else if (args[i].equalsIgnoreCase("-enforceinvitepolicy")) {
               arguments.setProperty("meeting.enforceinvitepolicy", "true");
            } else if (args[i].equalsIgnoreCase("-db2")) {
               arguments.setProperty("meeting.metricdriver",   args[++i]);
               arguments.setProperty("meeting.metricurl",      args[++i]);
               arguments.setProperty("meeting.metricinstance", args[++i]);
               arguments.setProperty("meeting.metricpwdir",    args[++i]);
            } else if (args[i].equalsIgnoreCase("-groupsdb")) {
               arguments.setProperty("meeting.grpsdriver",   args[++i]);
               arguments.setProperty("meeting.grpsurl",      args[++i]);
               arguments.setProperty("meeting.grpsinstance", args[++i]);
               arguments.setProperty("meeting.grpspwdir",    args[++i]);
            } else if (args[i].equalsIgnoreCase("-amtdb")) {
               arguments.setProperty("meeting.amtdriver",   args[++i]);
               arguments.setProperty("meeting.amturl",      args[++i]);
               arguments.setProperty("meeting.amtinstance", args[++i]);
               arguments.setProperty("meeting.amtpwdir",    args[++i]);
            } else if (args[i].equalsIgnoreCase("-copygroupsdb")) {
               arguments.setProperty("meeting.copygroupsdb", args[++i]);
            } else if (args[i].equalsIgnoreCase("-copyamtdb")) {
               arguments.setProperty("meeting.copyamtdb", args[++i]);
            } else if (args[i].equalsIgnoreCase("-copymetricsdb")) {
               arguments.setProperty("meeting.copymetricdb", args[++i]);
            } else if (args[i].equalsIgnoreCase("-tokencipher")) {
               arguments.setProperty("meeting.tokencipher", args[++i]);
            } else if (args[i].equalsIgnoreCase("-userpw")) {
               arguments.setProperty("meeting.userpw", args[++i]);
            } else if (args[i].equalsIgnoreCase("-automeeting")) {
               arguments.setProperty("meeting.automeeting", args[++i]);
            } else if (args[i].equalsIgnoreCase("-verbose")) {
               arguments.setProperty("meeting.verbose", "true");
            } else {
               throw new Exception("Unknown parameter: " + args[i]);
            }
         }
      } catch(Exception e) {
         System.out.println("Error while processing parameter: " + parm + "\n\n");
         e.printStackTrace(System.out);
         return false;
      }
      
      if (arguments.getBoolProperty("meeting.verbose", true)) {
         System.out.println("\nStartup arguments\n----------------\n" + 
                            arguments.toString());
      }
      
      return true;
   }
   
   public boolean parseArgs() {
      String k = null;
      String v = null;
              
      try {
         localport = arguments.getIntProperty(k="meeting.port", localport);
         
         if ((v=arguments.getProperty(k="meeting.debug")) != null) { 
            if (v.equalsIgnoreCase("true")) {
               setDebug(true);
               DebugPrint.setLevel(DebugPrint.DEBUG);
            }
         }
      
         if ((v=arguments.getProperty(k="meeting.mailbody")) != null) { 
            try {
               BufferedReader bf = new BufferedReader(new FileReader(v));
               mailBody = "";
               String str;
               while((str=bf.readLine()) != null) {
                  mailBody += str + "\n";
               }
            } catch(IOException ioe) {
               System.err.println("-mailbody: Error opening/loading " + v);
               return false;
            }
         }
      
         FEHostURL     = arguments.getProperty(k="meeting.FEHostURL",        FEHostURL);
         ESS           = arguments.getProperty(k="meeting.FEEdesignServlet", ESS);
         mailSubject   = arguments.getProperty(k="meeting.mailsubject",  mailSubject);
         smtpserver    = arguments.getProperty(k="meeting.smtpserver",   smtpserver);
         replyAddr     = arguments.getProperty(k="meeting.replyaddr",    replyAddr);
         MeetingParms  = arguments.getProperty(k="meeting.meetingparms", MeetingParms);
         doemailsend   = arguments.getBoolProperty(k="meeting.sendemail",  doemailsend);
         doamtprojects = arguments.getBoolProperty(k="meeting.amtprojects",doamtprojects);
         enforceInvitePolicy = arguments.getBoolProperty(k="meeting.enforceinvitepolicy", 
                                                         enforceInvitePolicy);
      
         if ((v=arguments.getProperty(k="meeting.metricdriver")) != null) { 
            DBConnection conn = new DBConnLocalPool();
            conn.setDriver     (v);
            conn.setURL        (arguments.getProperty(k="meeting.metricurl"));
            conn.setInstance   (arguments.getProperty(k="meeting.metricinstance"));
            conn.setPasswordDir(arguments.getProperty(k="meeting.metricpwdir"));
            DBSource.addDBConnection("EDODC", conn, false);
         }
      
         if ((v=arguments.getProperty(k="meeting.grpsdriver")) != null) { 
            DBConnection conn = new DBConnLocalPool();
            conn.setDriver     (v);
            conn.setURL        (arguments.getProperty(k="meeting.grpsurl"));
            conn.setInstance   (arguments.getProperty(k="meeting.grpsinstance"));
            conn.setPasswordDir(arguments.getProperty(k="meeting.grpspwdir"));
            DBSource.addDBConnection("GROUPS", conn, false);
         }
      
         if ((v=arguments.getProperty(k="meeting.amtdriver")) != null) { 
            DBConnection conn = new DBConnLocalPool();
            conn.setDriver     (v);
            conn.setURL        (arguments.getProperty(k="meeting.amturl"));
            conn.setInstance   (arguments.getProperty(k="meeting.amtinstance"));
            conn.setPasswordDir(arguments.getProperty(k="meeting.amtpwdir"));
            DBSource.addDBConnection("AMT", conn, false);
         }
      
         if ((v=arguments.getProperty(k="meeting.copygroupsdb")) != null) { 
            DBConnection conn = DBSource.getDBConnection(v);
            DBSource.addDBConnection("GROUPS", conn, false);
         }
      
         if ((v=arguments.getProperty(k="meeting.copymetricdb")) != null) { 
            DBConnection conn = DBSource.getDBConnection(v);
            DBSource.addDBConnection("EDODC", conn, false);
         }
      
         if ((v=arguments.getProperty(k="meeting.copyamtdb")) != null) { 
            DBConnection conn = DBSource.getDBConnection(v);
            DBSource.addDBConnection("AMT", conn, false);
         }
      
         if ((v=arguments.getProperty(k="meeting.tokencipher")) != null) { 
            String cipherFile = v;
            cipher = SearchEtc.loadCipherFile(cipherFile);
            
            if (cipher == null) {
               cipher = null;
               System.out.println(
                  "-tokencipher failed! Error loading cipherfile: " + 
                  cipherFile);
               return false;
            }
         } 

         if ((v=arguments.getProperty(k="meeting.userpw")) != null) { 
            userpwFile = v;
            if (!refreshUserPW()) {
               System.out.println("Error processing -userpw option");
               return false;
            }
         }
      
         if ((v=arguments.getProperty(k="meeting.automeeting")) != null) { 
            automeeting = v;
            
           // Get the fake handler going. He will consume protocol, 
           //  and generate the testpatterns for participants until another
           //  Moderator comes along.
            fakehandler = new FakeHandler(this);
            handlers.put(new Integer(fakehandler.getHandlerId()), 
                         fakehandler);
            new Thread(fakehandler).start();
            
           // Make a 40 char password
            byte pwarr[] = new byte[20];
            (new Random()).nextBytes(pwarr);
            StringBuffer ans = new StringBuffer();
            for(int i=0 ; i < pwarr.length; i++) {
               String vv = Integer.toHexString(((int)pwarr[i]) & 0xff);
               if (vv.length() == 1) ans.append("0");
               ans.append(vv);
            }
            
            String pw = ans.toString();
            
            Vector vec = new Vector();
            vec.addElement(pw);
            vec.addElement("IBM");
            userpw.put(CHATMAN, vec);
            fireLoginCommandUserPW(fakehandler, (byte)0, (byte)0, CHATMAN, pw);
            
            Vector chatmanV = getUser(CHATMAN);
            if (chatmanV != null) {
               chatman = (User)chatmanV.elementAt(0);
            }
            
            if (chatman == null) {
               System.out.println("Uhoh ... chatman null!\n");
            }
            
            if (!refreshAutoMeetingFile(false)) {
               throw new Exception("Perhaps error opening file " + 
                                   automeeting);
            }               
         }
      } catch (Exception tt) {
         System.out.println("Error processing option " + k);
         tt.printStackTrace(System.out);
         return false;
      }
      
     // Remove all known options ... if any left after, error
     // Note we are using UPPER case. Cause all getProperty and setProperty do an upcase
     //  on the key. removeProperty has a bug ... does NOT do the upcase
     
      Enumeration allkeys = allargs.keys();
      boolean foundsome = false;
      while(allkeys.hasMoreElements()) {
         String key = (String)allkeys.nextElement();
         if (arguments.getProperty(key, null) == null) {
            if (!foundsome) {
               foundsome = true;
               System.out.println("\nValid keys which were NOT used (informational)");
               System.out.println("----------------------------------------------");
            }
            System.out.println(key);
         } else {
            arguments.removeProperty(key);
         }
      }
            
      if (arguments.getPropertyNames().hasMoreElements()) {
         System.out.println("\nERROR! Unknown properties specified"   +
                            "\n-----------------------------------\n" +
                            arguments.toString());
         return false;
      }
      
      return true;
   }
   
   boolean refreshUserPW() {
      
      if (userpwFile == null) return false;
      
      synchronized(userpwFile) { try {
         
         File file = new File(userpwFile);
         if (file.lastModified() == userpwLastModified) {
            return false;
         }
         
         userpwLastModified = file.lastModified(); 
         System.out.println("Reloading userpw: " + (new java.util.Date()).toString());
         
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
               Vector oldv = (Vector)userpw.get(user);
               
               int ii=0;
               int cnt = 0;
               
               if (oldv == null) {
                  System.out.println("User = " + user + " Company = " + company);
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
                        System.out.println("User = " + user +
                                           " Company = " + company);
                     if (ii++ !=  0) System.out.println("   " + tn);
                  }
               }
               
               v.insertElementAt(company, 1);
               userpw.put(user, v);
            }
         }
      } catch( Exception ee) { return false; }}
      return true;
   }
   
   
   Meeting findAutoMeeting(String m) { 
      if (automeetings == null) return null;
      
     // Get rid of synchronize
      Object arr[] = automeetings.toArray();
      for (int j=0; j < arr.length; j++) {
         Integer i = (Integer)arr[j];
         Meeting meeting = (Meeting)meetings.get(i);
         if (meeting != null && meeting.getTitle().equals(m)) {
            return meeting;
         }
      }
      
     //  Well, hack. Look in normal meetings for CHATMAN owner. Used to 
     //   find meeting at meeting creation
      arr = meetings.values().toArray();
      
      for (int j=0; j < arr.length; j++) {
         Meeting meeting = (Meeting)arr[j];
         if (meeting.getTitle().equals(m) && 
             meeting.getAnyOwnerId() == chatman.getLoginId()) {
            return meeting;
         }
      }
      
      return null;
   }
   
  // Force means process file even though it may not have changed
   boolean refreshAutoMeetingFile(boolean force) {
   
      if (automeeting == null) return false;
      
     // Get rid of synchronize
     
      if (chatman == null) {
         return false;
      }
               
      synchronized (automeeting)  { try {
      
         File file = new File(automeeting);
         if (!force && file.lastModified() == automeetingLastModified) {
            return false;
         }
         
         System.out.println("Reloading automeeting: " + 
                            (new java.util.Date()).toString());
         
         automeetingLastModified = file.lastModified();
         
         BufferedReader in = 
            new BufferedReader(new FileReader(automeeting));
         
         String s;
         while((s=in.readLine()) != null) {
            s = s.trim();
            if (s.length() == 0 || s.startsWith("#")) {
               ;
            } else {
            
              /*
              ** Automeeting Syntax
              **
              **   meetingname : projectname : invite1 : invite2 : ... : inviteN
              **
              **   projectname and invitenames can be prefixed by !. If so, then
              **   the invite is tagged to be an 'OWNER' invite. When folks join
              **   using that invitation, then they will have owner privileges.
              **
              **   For backward compat reasons, the first invitation is a 
              **   project, whether its preceeded with a # or not. Any other
              **   invitations as user invites unless directly proceeded with
              **   a pound (#). So, a project declared as an OWNER would be 
              **   !#projname
              **
              */
              // Stupid tokenizer does not support empty tokens! Use it anyway
              //  and play games
               StringTokenizer tokenizer = new StringTokenizer(s + " ", ":", 
                                                               true);
               String meeting=tokenizer.nextToken().trim();
                              
               Meeting m = findAutoMeeting(meeting);
               int changecnt=0;
               
              // If this is a new automeeting ... create it
               if (m == null) {
                  changecnt++;
                  System.out.println("Automeeting: Meeting = " + meeting);
               
                 // Create Chat meeting
                  fireStartMeetingCommand(fakehandler, (byte)0, (byte)0, 
                                          meeting, "", "N/A");
                                          
                  if (automeetings == null) automeetings = new Vector();
                  
                  m = findAutoMeeting(meeting);
                  
                  automeetings.addElement(new Integer(m.getMeetingId()));
               }
                              
              // Only modify meetings that have 1 or fewer owners (assuming
              //  that the owner is the operator). This so an active meeting
              //  is not changed under the feet of the participants
               if (m.numOwners() > 1) {
                  continue;
               }
                              
              // Manage all user invites
               int idx = 0;
               boolean lastwascolon = false;
               while(tokenizer.hasMoreTokens()) {
               
                  idx++;
                  String tn=tokenizer.nextToken().trim();
                  
                 // If its a delimiter ... skip it. Incr idx if two colons in 
                 //   a row. Also make sure lastwascolon is set.
                  if (tn.equals(":")) {
                     if (lastwascolon) idx++;
                     else              lastwascolon = true;
                     continue;
                  } else {
                     lastwascolon = false;
                  }
                  
                  if (tn.length() == 0) continue;
                  
                  boolean isOwner = false;
                  if (tn.startsWith("!")) {
                     tn = tn.substring(1);
                     isOwner = true;
                  }
                  
                  boolean isProj = false;
                  boolean isGroup = false;
                  if (tn.startsWith("#")) {
                     tn = tn.substring(1);
                     isProj = true;
                  } else if (idx == 3) {
                     isProj = true;
                  } else if (tn.startsWith("%")) {
                     tn = tn.substring(1);
                     isGroup = true;
                  }
                  
                 // Manage project invite
                  if (isProj && tn.length() > 0) {
                     
                     Invite projInvite = 
                        m.getInvite(tn, DSMPGenerator.STATUS_PROJECT);
                        
                     if (projInvite == null) {
                        
                        if (changecnt++ == 0) {
                           System.out.println("@Automeeting: Meeting = " + 
                                              meeting);
                        }
                        
                        System.out.println("   Project = " + tn);
                        
                        chatman.addProject(tn);
                        fireCreateInvitationCommand(fakehandler, (byte)0, 
                                                    (byte)0, m.getMeetingId(),
                                                    DSMPGenerator.STATUS_PROJECT,
                                                    tn);
                        projInvite = m.getInvite(tn, 
                                                 DSMPGenerator.STATUS_PROJECT);
                     }
                     
                     if (projInvite != null) projInvite.isOwner(isOwner);
                  
                  } else if (isGroup && tn.length() > 0) {
                     
                     Invite groupInvite = 
                        m.getInvite(tn, DSMPGenerator.STATUS_GROUP);
                        
                     if (groupInvite == null) {
                        
                        if (changecnt++ == 0) {
                           System.out.println("@Automeeting: Meeting = " + 
                                              meeting);
                        }
                        
                        System.out.println("   Group = " + tn);
                        
                       //chatman.addProject(tn);
                        fireCreateInvitationCommand(fakehandler, (byte)0, 
                                                    (byte)0, m.getMeetingId(),
                                                    DSMPGenerator.STATUS_GROUP,
                                                    tn);
                        groupInvite = m.getInvite(tn, 
                                                  DSMPGenerator.STATUS_GROUP);
                     }
                     
                     if (groupInvite != null) groupInvite.isOwner(isOwner);
                  
                  } else if (tn.length() > 0) {
                  
                    // User invite
                     Invite userInvite = m.getInvite(tn, 
                                                     DSMPGenerator.STATUS_NONE);
                     if (userInvite == null) {
                        fireCreateInvitationCommand(fakehandler, (byte)0, 
                                                    (byte)0, m.getMeetingId(),
                                                    DSMPGenerator.STATUS_NONE,
                                                    tn);
                                                    
                        userInvite = m.getInvite(tn, DSMPGenerator.STATUS_NONE);
                        
                        if (changecnt++ == 0) {
                           System.out.println("@Automeeting: Meeting = " + 
                                              meeting);
                        }
                        System.out.println("   " + tn);
                     }
                     
                     if (userInvite != null) userInvite.isOwner(isOwner);
                  }
               }
            }
         }
      } catch( Exception ee) { ee.printStackTrace(); return false; } }
      return true;
   }
   
   public static void main(String args[]) {
   
      DSMPServer srv = new DSMPServer();
      if (!srv.convertArgs(args)) return;
      if (!srv.parseArgs()) {
         System.exit(3);
      }
      Thread thread  = new Thread(srv);
      thread.setName("DSMPServer");
      thread.start();
   }
   
   
   public class AMTData {
      public AMTData(User from, Vector users, String url) {
         this.from  = from;
         this.users = (Vector)users.clone();
         this.url   = url;
      }
      public User   from  = null;
      public Vector users = null;
      public String url   = null;
   }
   
  // Sending mail to lots of IDs really slows things down ... thread it
   public class AMTMailer implements Runnable {
   
      Thread me = null;
      Vector todo = new Vector();
      boolean continueRunning = true;
   
      public void run() {
         try {
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
                     sendAMTMailNoThread(d.from, d.users, d.url);
                  }
               }
               
               synchronized (todo) {
                  try {
                     if (todo.size() == 0) {
                        if (continueRunning) todo.wait(10000);
                        else                 return;
                     }
                  } catch(InterruptedException ee) {
                    // If I timed out, and there is nothing left to do, 
                    //  clean up here, as no window of vuln, and exit
                     if (todo.size() == 0) {
                        if (me == Thread.currentThread()) {
                           me = null;
                        }
                        break;
                     }
                        
                  } catch(Exception ee) {
                  }
               }
            }
         } finally {
           // null out me on my way out
            synchronized(todo) {
               if (me == Thread.currentThread()) {
                  me = null;
               }
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
         Thread lme = null;
         synchronized(todo) {
            if (me != null) {
               continueRunning = false;
               todo.notifyAll();
               lme = me;
            }
         }
         if (lme != null) {
            while(true) {
               try {
                  lme.join();
                  break;
               } catch(InterruptedException ie) {}
            }
         }
      }
   
      public void sendAMTMail(User from, Vector users, String url) {
                              
         synchronized(todo) {
            todo.addElement(new AMTData(from, users, url));
            startThread();
            todo.notifyAll();
         }
      }
      
      public void sendAMTMailNoThread(User from, 
                                      Vector users, 
                                      String url) {
         
         String emailaddrs = "";
         try {
         
            Enumeration enum = users.elements();
            while(enum.hasMoreElements()) {
               String name = (String)enum.nextElement();
               try {
                 //Vector amtvec = AMTQuery.getAMTByUser(name,
                 //                                       false,
                 //                                       false,
                 //                                       null);
                  Vector amtvec = 
                     UserRegistryFactory.getInstance().lookup(name,
                                                              false, 
                                                              false,
                                                              false);
                  if (amtvec != null && amtvec.size() > 0) {
                     if (amtvec.size() == 1) {
                        AMTUser amtuser = (AMTUser)amtvec.elementAt(0);
                        
                       //System.out.println("got match:\n" + 
                       //amtuser.toString());
                        
                        String email = amtuser.getEmail();
                        if (emailaddrs.length() > 0) {
                           emailaddrs += ", ";
                        } 
                        emailaddrs += email;
                        
                     } else {
                        System.out.println("More than one match found!");
                     }
                  } else {
                     System.out.println("User " + name + 
                                        " not found in AMT!!");
                  }
               } catch(DBException dbe) {
                  System.out.println("Error getting AMTUser record for " +
                                     name +
                                     " and doing AMT!");
                  dbe.printStackTrace(System.out);
               }
            }
         
            String ucompany     = from.getCompany();
            String uuser        = from.getName();
            
            if (ucompany == null) ucompany = "";
            else                  ucompany = ucompany.trim();
            
            String body         = null;
            String lmailSubject = null;
            
            
           // u-vars already set to package owner (thats who User is here)
            lmailSubject = mailSubject;
            
            body = mailBody;
               
           // If we did not pass the assertion check, don't send mail
            if (emailaddrs.length() == 0) {
               System.out.println("Nobody to notify for url: " + url);
               return;
            }
            
            if (!doemailsend) {
               DebugPrint.printlnd(DebugPrint.INFO,
                                   "Send Meeting notice to users " + 
                                   emailaddrs + 
                                   " NOT completed ... TESTING");
               return;
            }
            
            
            int idx;
            String check;
            check = "%URL%";
            while((idx=body.indexOf(check)) >=0) {
               String newbody = body.substring(0,idx) + url;
               if (body.length() > idx+check.length()) {
                  newbody += body.substring(idx+check.length());
               }
               body=newbody;
            }
            
            check = "%ownerid%";
            while((idx=body.indexOf(check)) >=0) {
               String newbody = body.substring(0,idx) + uuser;
               if (body.length() > idx+check.length()) {
                  newbody += body.substring(idx+check.length());
               }
               body=newbody;
            }
            
            check = "%ownercompany%";
            while((idx=body.indexOf(check)) >=0) {
               String newbody = body.substring(0,idx) + ucompany;
               if (body.length() > idx+check.length()) {
                  newbody += body.substring(idx+check.length());
               }
               body=newbody;
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
                                                          emailaddrs, null,
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
               DebugPrint.printlnd(DebugPrint.ERROR,
                                   "Error sending Meeting notice [" + 
                                   url + "] " +
                                   "email to " + emailaddrs + 
                                   " from " + from.getName());
            } else if (tries > 1) {
               DebugPrint.printlnd(DebugPrint.ERROR,
                                   "Finally sent Meeting notice [" + 
                                   url + "] (retries=" + tries + ") " +
                                   "email to " + emailaddrs + 
                                   " from " + from.getName());
            }
            
         } catch(Throwable tt) {
            DebugPrint.printlnd(DebugPrint.ERROR,
                                "Error sending Meeting notice [" + 
                                url + "] " +
                                "email to " + emailaddrs + 
                                " from " + from.getName());
            DebugPrint.printlnd(DebugPrint.ERROR, tt);
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
  
   boolean sendProtoTo(DSMPProto proto, int loginid) {
      DSMPHandler handler = (DSMPHandler)handlers.get(new Integer(loginid));
      if (handler != null) {
         handler.sendProtocolPacket(proto);
         return true;
      }
      return false;
   }
  
   void addMeeting(Meeting meeting) {
      int meetingid = meeting.getMeetingId();
      meetings.put(new Integer(meetingid), meeting);
   }
   
   Meeting endMeeting(int meetingid) {
      Meeting meeting   = (Meeting)meetings.remove(new Integer(meetingid));
      if (meeting != null) {
         meeting.endMeeting(this);
      }
      return meeting;
   }

   Vector getUser(String u) {
      return (Vector)users.get(u);
   }
   User getUser(int u) {
      return (User)users.get(new Integer(u));
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
   
   Vector getMeetingsOwnedBy(String u) {
      Vector ret = new Vector();
     // Find all meetings this guy is running and end them
     // This is where a unique participant ID would work well
     //  Also, remove him from all other meetings he is attending
     // This SYNC is ok
      synchronized(meetings) {
         Enumeration enum = meetings.elements();
         while(enum.hasMoreElements()) {
            Meeting meeting = (Meeting)enum.nextElement();
            if (meeting.isOwner(u)) {
               ret.addElement(meeting);
            }
         }
      }   
      return ret;
   }
   
   boolean removeUser(int loginid) {
      boolean ret = false;
      
     // Find all meetings this guy is running and end them
     // This is where a unique participant ID would work well
     //  Also, remove him from all other meetings he is attending
      Object arr[] = meetings.values().toArray();
      for(int i=0; i < arr.length; i++) {
         Meeting meeting = (Meeting)arr[i];
         if (meeting.isOwner(loginid) && meeting.numOwners() <= 1) {
            endMeeting(meeting.getMeetingId());
         } else if (meeting.containsParticipant(loginid)) {
            meeting.removeParticipant(this, loginid);
         }
      }
         
     // This SYNC is ok
      synchronized (users) {
         User user = (User)users.remove(new Integer(loginid));
         if (user != null) {
            Vector v = (Vector)users.get(user.getName());
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
   
   
   public User getUserFromToken(String token, int loginid) throws Exception {
      User ret = null;
      Vector projs = null;
      String error = null;
      String user  = null;
      String country = null;
      String company = null;
      String dtid    = null;
      String email   = null;
      try {
         
         Hashtable hash = SearchEtc.dataFromToken(cipher, token);
         if (hash != null) {
         
            error = (String)hash.get("ERROR");
            if (error == null) {
               projs = new Vector();
               user = (String)hash.get("EDGEID");
               if (user != null) user = user.toLowerCase();
               country = (String)hash.get("COUNTRY");
               company = (String)hash.get("COMPANY");
               email   = (String)hash.get("EMAIL");
               dtid    = (String)hash.get("DESKTOPID");
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
                  ret = new User(user, projs, loginid);
                  ret.setCompany(company);
                  ret.setCountry(country);
                  ret.setEmail(email);
                  ret.setTokenLogin(true);
                  ret.setCompany(company);
                  ret.setCountry(country);
                  ret.setDesktopId(dtid);
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
   
   private void invalidProtocol(DSMPHandler h, byte opcode) {
      System.out.println("Invalid protocol for server: Opcode = " + opcode);
      System.out.println("Closing handler for ==> " + h.getHandlerId());
      h.shutdown();
   }
   
  // Used by group code
   private void validateLoggedInEx(DSMPHandler h, byte handle, 
                                 byte opcode) throws DboxException {
      if (!h.bitsSetInFlags(0x01)) {
         throw new DboxException("Connection not logged in", 0);
      }
   }
   
   private boolean validateLoggedIn(DSMPHandler h, byte handle, byte opcode) {
      boolean ret = true;
      if (!h.bitsSetInFlags(0x01)) {
         DSMPProto proto;
         proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                                 "Not Logged In");
         h.sendProtocolPacket(proto);
         ret = false;
      }
      return ret;
   }
   
   
   public void fireShutdownEvent(DSMPBaseHandler hand) {
      System.out.println("GotFireShutdownEvent for connection = " + 
                         hand.getIdentifier() + " " + 
                         (new java.util.Date()).toString());
      removeUser(hand.getHandlerId());
      handlers.remove(new Integer(hand.getHandlerId()));
   }
   
   
  /* -------------------------------------------------------*\
  ** Commands 
  \* -------------------------------------------------------*/
   public void fireLoginCommandToken(DSMPHandler hand, byte flags, 
                                     byte handle, String token) {
                                     
      byte opcode = DSMPGenerator.OP_LOGIN_REPLY;
      
      refreshUserPW();
      refreshAutoMeetingFile(false);
      
      DSMPProto proto = null;
      String error = null;
      
      if (hand.bitsSetInFlags(0x01)) {
         System.out.println("TokenLogin failed: already logged in on this connection");
         error = "Already logged in on this connection=" + hand.getHandlerId();
      }      
      
      if (error == null && token.trim().equals("")) {
         System.out.println("Token Login failed with error: Bad Token Format");
         error = "Token login failed: Bad token format";
      }
      
      if (error == null) {
         
         if (cipher != null) {
         
            try {
            
               User userObj = getUserFromToken(token, hand.getHandlerId());
               
               if (userObj != null) {
                  
                  if (!userObj.getName().equalsIgnoreCase(CHATMAN)) {
                      
                     hand.setIdentifier(userObj.getName());
                     
                    // If we are doing AMT Project checking, then get projects 
                    // and entitlements associated with this goon. This also
                    // gets the bluepages info into the User object
                     if (doamtprojects) try {
                        System.out.println("Searching for amtuser projects: " +
                                           userObj.getName());
                       //Vector amtvec = AMTQuery.getAMTByUser(userObj.getName(),
                       //                                       true,
                       //                                       true,
                       //                                       null);
                        Vector amtvec = 
                           UserRegistryFactory.getInstance().lookup(
                              userObj.getName(), false, true, true);
                              
                        if (amtvec != null && amtvec.size() > 0) {
                           if (amtvec.size() == 1) {
                              AMTUser amtuser = (AMTUser)amtvec.elementAt(0);
                              
                             //System.out.println("got match:\n" + 
                             //amtuser.toString());
                                                 
                              userObj.addProjects(amtuser.getProjects());
                              userObj.setIBMDept(amtuser.getIBMDept());
                              userObj.setIBMDiv(amtuser.getIBMDiv());
                              
                           } else {
                              System.out.println("More than one match found!");
                           }
                        } else {
                           System.out.println("User not found in AMT!!");
                        }
                     } catch(DBException dbe) {
                        System.out.println("Error getting AMTUser record for " +
                                 userObj.getName() +
                                 " and doing AMT!. No projectlist update");
                        dbe.printStackTrace(System.out);
                     }
                     
                     addUser(userObj);
                     proto = DSMPGenerator.loginReply(handle, 
                                                      userObj.getLoginId(),
                                                      userObj.getName(),
                                                      userObj.getCompany(), 
                                                      userObj.getProjects());
                     hand.addMaskToFlags(0x01);
                     
                  } else {
                     error = "Invalid login ID for Conferencing: " +
                        userObj.getName();
                  }
               } else {
                  error = "User info not found in token";
               }
            } catch(Exception e) {
               System.out.println("Error during login");
               e.printStackTrace(System.out);
               error = e.getMessage();
            }
         } else {
            error = "Not configured for token login";
         }
      }      
      
      if (error != null || proto == null) {
         proto = DSMPGenerator.genericReplyError(opcode, handle, 0,
                                                 "LoginError = " + error);
      }
      
      hand.sendProtocolPacket(proto);
   }
   public void fireLoginCommandUserPW(DSMPHandler hand, byte flags, 
                                      byte handle,
                                      String user, String pw) {
                                      
      byte opcode = DSMPGenerator.OP_LOGIN_REPLY;
      
      int idx = 1;
      refreshUserPW();
      refreshAutoMeetingFile(false);
      
      DSMPProto proto=null;
      if (hand.bitsSetInFlags(0x01)) {
         proto = DSMPGenerator.genericReplyError(opcode,
                                                 handle, 0,
                                                 "Already logged in = " + 
                                                 hand.getHandlerId());
         hand.sendProtocolPacket(proto);
         return;
      }
      
      User userObj = null;
      Vector v = (Vector)userpw.get(user);
      if (v != null) {
      
//         String pws = (String)v.firstElement(); 
//         DebugPrint.printlnd(DebugPrint.WARN, "in PW = " + pw + 
//                             " pw of record = " + pws);

         if (((String)v.firstElement()).equals(pw) && 
             pw.trim().length() != 0) {
             
            Enumeration e = v.elements();
            e.nextElement();  // Skip pw
            String company = (String)e.nextElement();
            
            Vector projv = new Vector();
            if (v.size() > 2) {
               while(e.hasMoreElements()) {
                  projv.addElement(e.nextElement());
               }
            }
            
            {
            
              /* ... Uncomment this to go back to only one login per user
            synchronized (users) {
               User u = (User)users.get(user);
               if (u != null) {
                  proto = DSMPGenerator.genericReplyError(opcode, handle, 
                                                          0, 
                                                       "Already logged in = " +
                                                          u.getLoginId());
               } else {
              */
              
              // Only allow CHATMAN to come thru here once
               boolean doit = true;
               synchronized (users) {
                  Vector vv = (Vector)users.get(user);
                  if (vv != null && user.equals(CHATMAN)) {
                     doit = false;
                  }
               }
              
               if (doit) {
                  hand.setIdentifier(user);
                  userObj=new User(user, projv, hand.getHandlerId());
                  userObj.setCompany(company);
                  
                 // Copycode from above ... for testing
                 //
                 // If we are doing AMT Project checking, then get projects 
                 // and entitlements associated with this goon. This also
                 // gets the bluepages info into the User object
                  if (doamtprojects) try {
                  
                     System.out.println("Searching for amtuser projects: " +
                                        userObj.getName());
                                        
                    //Vector amtvec = AMTQuery.getAMTByUser(userObj.getName(),
                    //                                       true,
                    //                                       true,
                    //                                       null);
                     Vector amtvec = 
                        UserRegistryFactory.getInstance().lookup(
                           userObj.getName(), false, true, true);
                           
                     if (amtvec != null && amtvec.size() > 0) {
                        if (amtvec.size() == 1) {
                           AMTUser amtuser = (AMTUser)amtvec.elementAt(0);
                           
                          //System.out.println("got match:\n" + 
                          //                    amtuser.toString());
                           
                           userObj.addProjects(amtuser.getProjects());
                           userObj.setIBMDept(amtuser.getIBMDept());
                           userObj.setIBMDiv(amtuser.getIBMDiv());
                        } else {
                           System.out.println("More than 1 match for");
                        }
                     } else {
                        System.out.println("User not found in AMT!!");
                     }
                  } catch(DBException dbe) {
                     System.out.println("Error getting AMTUser record for " +
                                        userObj.getName() +
                                      " and doing AMT!. No projectlist update");
                     dbe.printStackTrace(System.out);
                  }
                  
                  addUser(userObj);
                  proto = DSMPGenerator.loginReply(handle, 
                                                   userObj.getLoginId(), 
                                                   userObj.getName(),
                                                   userObj.getCompany(),
                                                   userObj.getProjects());
                  hand.addMaskToFlags(0x01);
               }
            }
         }
      } else {
         DebugPrint.printlnd(DebugPrint.WARN, "Login attempt with user " + user
                             + " no such user");
      }
      if (proto == null) {
         proto = DSMPGenerator.genericReplyError(DSMPGenerator.OP_LOGIN_REPLY,
                                                 handle, 0, "Bad UserID/PW");
      }
      hand.sendProtocolPacket(proto);
   }
   public void fireLogoutCommand(DSMPHandler hand, byte flags, byte handle) {
      byte opcode = DSMPGenerator.OP_LOGOUT_REPLY;
      if (validateLoggedIn(hand, handle, opcode)) {
         
         if (removeUser(hand.getHandlerId())) {
            System.out.println("Logout:Huh? no User record for id=" + 
                               hand.getHandlerId());
         }
         DSMPProto proto;
         proto=DSMPGenerator.logoutReply(handle);
         hand.removeMaskFromFlags(0x01);
         hand.sendProtocolPacket(proto);
         try {
            Thread.currentThread().sleep(2000);
         } catch(Throwable tt) {}
         hand.shutdown();
      }
   }
   public void fireStartMeetingCommand(DSMPHandler hand, byte flags, 
                                       byte handle, 
                                       String title, String pw,
                                       String classification) {
      byte opcode = DSMPGenerator.OP_STARTMEETING_REPLY;
      if (validateLoggedIn(hand, handle, opcode)) {
                           
         
         DSMPProto proto=null;
         int loginid = hand.getHandlerId();
         
         User user = getUser(loginid);
         
         Meeting meeting = new Meeting(loginid, title, pw, classification);
         
        // Metric tracking
         meeting.setDesktopId(user.getDesktopId());
         if (meeting.getDesktopId() == null) {
            meeting.setDesktopId("No desktopID set");
         }
         
         int meetingid = meeting.getMeetingId();
         
         addMeeting(meeting);
         
        // Add Meeting owner invitation marked as Owner
         meeting.addInvitation(this, user.getName(), DSMPGenerator.STATUS_NONE,
                               true);
         
        // Need to Q up reply before calling ADD so Mike know that the events
        //  are about him.
         proto = DSMPGenerator.startMeetingReply(handle, meetingid,
                                                 loginid);
         if (!meeting.addParticipant(this, loginid, proto)) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                                    "Error setting up meeting");
            hand.sendProtocolPacket(proto);
         }
      }
   }
   
   public void fireLeaveMeetingCommand(DSMPHandler hand, byte flags, 
                                       byte handle, int meetingid) {
                                       
      byte opcode = DSMPGenerator.OP_LEAVEMEETING_REPLY;
      if (validateLoggedIn(hand, handle, opcode)) {
         DSMPProto proto = null;
         Meeting meeting = (Meeting)meetings.get(new Integer(meetingid));
         if (meeting == null) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                                    "Invalid meetingid");
         } else if (meeting.isOwner(hand.getHandlerId()) && 
                    meeting.numOwners() <= 1) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                             "Meeting owner must END meeting");
         } else {
            if (meeting.removeParticipant(this, hand.getHandlerId())) {
               proto = DSMPGenerator.leaveMeetingReply(handle);
            } else {
               proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                                  "You're not in the meeting");
               
            }
         }
         hand.sendProtocolPacket(proto);
      }
   }
   
   public void fireEndMeetingCommand(DSMPHandler hand, byte flags, 
                                     byte handle, 
                                     int meetingid) {
      byte opcode = DSMPGenerator.OP_ENDMEETING_REPLY;
      if (validateLoggedIn(hand, handle, opcode)) {
         DSMPProto proto = null;
         Meeting meeting = (Meeting)meetings.get(new Integer(meetingid));
         if (meeting == null) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                                    "Invalid meetingid");
         } else if (!meeting.isOwner(hand.getHandlerId())) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                          "Only the owner can end a meeting");
         } else {
            endMeeting(meeting.getMeetingId());
            proto = DSMPGenerator.endMeetingReply(handle);
         }
         hand.sendProtocolPacket(proto);
      }
   }
   public void fireCreateInvitationCommand(DSMPHandler hand, byte flags, 
                                           byte handle, int meetingid, 
                                           byte inviteType, String name) {
                                           
     // New flag being passed
      boolean isOwner = (flags & 0x2) != 0;
      
      byte opcode = DSMPGenerator.OP_CREATEINVITATION_REPLY;
      
      if (resizetime != 0 && framesrecv != 0 && frameupdatetime != 0) {
         System.out.println("Num Frames = " + framesrecv + 
                            " Deltatime (ms) = " + 
                            (frameupdatetime-resizetime));
      }
      framesrecv = 0;
      frameupdatetime = 0;
      resizetime = 0;
      
      if (validateLoggedIn(hand, handle, opcode)) {
      
         String inviteTypeName = "User";
         if (inviteType == DSMPGenerator.STATUS_PROJECT) {
            inviteTypeName = "Project";
         } else if (inviteType == DSMPGenerator.STATUS_GROUP) {
            inviteTypeName = "Group";
         }
         
         DSMPProto proto = null;
         Meeting meeting = (Meeting)meetings.get(new Integer(meetingid));
         
         if (meeting == null) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                                    "Invalid meetingid");
         } else if (!meeting.isOwner(hand.getHandlerId())) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                              "Only the meeting owner can send an invitation");
         } else if (meeting.containsInvite(name, inviteType)) {
               
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                                    inviteTypeName +
                                                    "Already invited");
         } else {
         
            User userObj = getUser(hand.getHandlerId());
            Invite invite = meeting.addInvitation(this, userObj, name, 
                                                  inviteType, isOwner);
            if (invite != null) {
               proto = DSMPGenerator.createInvitationReply(handle, 
                                                           invite.getInviteId());
            } else {
               proto = 
                  DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                                 inviteTypeName +
                                                 " is invalid OR access denied");
            }
         }
         hand.sendProtocolPacket(proto);
      }
   }
   
   public void fireJoinMeetingCommand(DSMPHandler hand, byte flags, 
                                      byte handle, int meetingid) {
      byte opcode = DSMPGenerator.OP_JOINMEETING_REPLY;
      if (validateLoggedIn(hand, handle, opcode)) {
         DSMPProto proto = null;
         int loginid = hand.getHandlerId();
         Meeting meeting = (Meeting)meetings.get(new Integer(meetingid));
         
         if (meeting == null) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                                    "Invalid meetingid");
         } else if (meeting.containsParticipant(loginid)) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                              "You are already IN the meeting");
         } else if (meeting.containsUser(this, loginid)){
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                              "Another Instance of your ID is IN the meeting");
         } else {
           // Pass in the proto which will be pre-sent IF the join is to succeed
            proto = DSMPGenerator.joinMeetingReply(handle, meetingid, 
                                                   loginid);
            if (!meeting.addParticipant(this, loginid, proto)) {
               proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                                 "Not invited to the meeting");
            } else {
               proto = null;
            }
         }
         if (proto != null) hand.sendProtocolPacket(proto);
      }
   }
   public void fireDropInviteeCommand(DSMPHandler hand, byte flags, 
                                      byte handle, 
                                      int meetingid,
                                      boolean dropInvite, int inviteid,
                                      boolean dropPart,   int participantid) {
      byte opcode = DSMPGenerator.OP_DROPINVITEE_REPLY;
      if (validateLoggedIn(hand, handle, opcode)) {
         DSMPProto proto = null;
         Meeting meeting = (Meeting)meetings.get(new Integer(meetingid));
         if (meeting == null) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                                    "Invalid meetingid");
         } else if (!meeting.isOwner(hand.getHandlerId())) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                              "Only the meeting owner can dropInvitee");
         } else {
            if (dropPart) {
               if (!meeting.removeParticipant(this, participantid)) {
                  proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                                     "Invalid participantid");
               }
            }
            
            if (proto == null && dropInvite) {
               if (!meeting.removeInvitation(this, inviteid)) {
                  proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                                          "Invalid inviteid");
               }
            }
            
            if (proto == null) {
               proto = DSMPGenerator.dropInviteeReply(handle);
            }
         }
         
         hand.sendProtocolPacket(proto);
      }      
   }
   public void fireImageResizeCommand(DSMPHandler hand, byte flags, 
                                      byte handle, 
                                      int meetingid,
                                      short x, short y, short w, short h) {
                                      
      byte opcode = DSMPGenerator.OP_IMAGERESIZE_REPLY;
            
      if (validateLoggedIn(hand, handle, opcode)) {
         DSMPProto proto = null;
         Meeting meeting = (Meeting)meetings.get(new Integer(meetingid));
         if (meeting == null) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                                    "Invalid meetingid");
         } else if (meeting.getModeratorId() != hand.getHandlerId()) {
           // JMC 5/21/04 - Changed from getOwnerId to getModeratorId 
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                              "Only the meeting Moderator can resizeimage");
         } else {
            proto = DSMPGenerator.imageResizeEvent(handle, meetingid, 
                                                   x, y, w, h);
           // JMC 5/21/04 - Changed from getOwnerId to getModeratorId 
            meeting.sendToParticipants(this, proto, true, 
                                       meeting.getModeratorId());
            proto = DSMPGenerator.imageResizeReply(handle);
         }
         hand.sendProtocolPacket(proto);
      }         
   }
   public void fireChatMessageCommand(DSMPHandler hand, byte flags, 
                                      byte handle, 
                                      int meetingid,
                                      int toid, String msg,
                                      boolean unicast) {
                                      
      byte opcode = DSMPGenerator.OP_CHATMESSAGE_REPLY;
      if (validateLoggedIn(hand, handle, opcode)) {
         DSMPProto proto = null;
         int loginid = hand.getHandlerId();
         Meeting meeting = (Meeting)meetings.get(new Integer(meetingid));
         if (meeting == null) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                                    "Invalid meetingid");
         } else if (!meeting.containsParticipant(hand.getHandlerId())) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                              "You are NOT in the meeting");
         } else if (unicast) {
            if (meeting.containsParticipant(toid)) {
               proto = DSMPGenerator.chatMessageEvent(handle, true, meetingid, 
                                                      hand.getHandlerId(),
                                                      toid, msg);
               sendProtoTo(proto, toid);
               proto = DSMPGenerator.chatMessageReply(handle);
            } else {
               proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                               "Invalid TOID");
            }
         } else {
            boolean telleveryone = true;
            if (meeting.isOwner(hand.getHandlerId())) {
               String inmsg = msg;
               boolean remotedebug = meeting.getRemoteDebug();
               if (msg.equals("uradufusandimnotDUFUSdebug")) {
                  remotedebug = true;
                  meeting.setRemoteDebug(remotedebug);
                  msg = "Debug mode is " + remotedebug;
                  telleveryone = false;
               } else if (remotedebug) {
                  StringTokenizer stok = new StringTokenizer(msg);
                  msg = "";
                  if (stok.hasMoreTokens()) {
                     msg = stok.nextToken();
                  }
                  telleveryone = false;
                  if        (msg.equalsIgnoreCase("minordebug")) {
                     setMinorDebug(!getMinorDebug());
                     msg = "New minor Debug = " + getMinorDebug();
                  } else if (msg.equalsIgnoreCase("debug")) {
                     setDebug(!getDebug());
                     msg = "New Debug = " + getDebug();
                  } else if (msg.equalsIgnoreCase("debuglevel")) {
                     if (stok.hasMoreTokens()) {
                        msg = stok.nextToken();
                        try {
                           int lev = Integer.parseInt(msg);
                           DebugPrint.setLevel(lev);
                           msg = "debuglevel = " + DebugPrint.getLevel();
                        } catch(Exception ee) {
                           msg = "Value for debuglevel must be integer!";
                        }
                     } else {
                        msg = "debuglevel = " + DebugPrint.getLevel();
                     }
                  } else if (msg.equalsIgnoreCase("toneitdown")) {
                     setToneItDown(!getToneItDown());
                     msg = "New toneitdown = " + getToneItDown();
                  } else if (msg.equalsIgnoreCase("exit")) {
                     remotedebug = false;
                     meeting.setRemoteDebug(remotedebug);
                     msg = "Debug mode is " + remotedebug;
                  } else if (msg.equalsIgnoreCase("handlers")) {
                     msg = "Status of Handlers[" + handlers.size() + "]\n";
                     Enumeration henum = handlers.elements();
                     while(henum.hasMoreElements()) {
                        DSMPBaseHandler dh = 
                           (DSMPBaseHandler)henum.nextElement();
                        msg += dh.toString() + "\n";
                     }
                  } else {
                     msg = "Invalid debug cmd [" + inmsg + 
                        "]\n Possible commands:\n" + 
                        "   minordebug\n   debug\n   debuglevel [lev]\n" +
                        "   toneitdown\n   exit\n   handlers\n";
                  }
               }
            } 
            
            proto = DSMPGenerator.chatMessageEvent(handle, false, meetingid, 
                                                   hand.getHandlerId(),
                                                   toid, msg);
           // If not debugging and 
            if (telleveryone) {
               meeting.sendToParticipants(this, proto, true, loginid);
            } else {
               hand.sendProtocolPacket(proto);
            }
            proto = DSMPGenerator.chatMessageReply(handle);
         }
         hand.sendProtocolPacket(proto);
      }      
   }
   public void fireModifyControlCommand(DSMPHandler hand, byte flags, 
                                        byte handle, 
                                        int meetingid,
                                        int participantid, 
                                        boolean addRemove, 
                                        boolean forceAdd) {
      byte opcode = DSMPGenerator.OP_MODIFYCONTROL_REPLY;
      if (validateLoggedIn(hand, handle, opcode)) {
         DSMPProto proto = null;
         Meeting meeting = (Meeting)meetings.get(new Integer(meetingid));
         if (meeting == null) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                                    "Invalid meetingid");
         } else if (meeting.getModeratorId() != hand.getHandlerId()) {
           // JMC 5/21/04 - Changed from getOwnerId to getModeratorId 
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                              "Only the meeting Moderator can modifyControl");
         } else if (!meeting.containsParticipant(participantid)) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                                    "Invalid participantid");
         } else if (addRemove) {
            int v = meeting.canControl(this, participantid, forceAdd);
            if (v == 0) {
               meeting.setController(participantid);
               proto=DSMPGenerator.controlEvent(handle, meetingid, 
                                                participantid);
               meeting.sendToParticipants(this, proto, false, 0);
               proto = null;
            } else {
               byte fl = (byte)(v == 2 ? 2 : 0);
               proto = DSMPGenerator.genericReplyError(opcode, handle, fl, 0, 
                                                       "mctl: Cannot give control to specified participant");
            }
         } else if (meeting.getController() != participantid){
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                                    "mctl:remote participant not IN control");
         } else {
           // JMC 5/21/04 - Changed from getOwnerId to getModeratorId 
            proto=DSMPGenerator.controlEvent(handle, meetingid,
                                             meeting.getModeratorId());
            meeting.sendToParticipants(this, proto, false, 0);
            proto = null;
            
           // JMC 5/21/04 - Changed from getOwnerId to getModeratorId 
            meeting.setController(meeting.getModeratorId());
         }
         if (proto == null) {
            proto = DSMPGenerator.modifyControlReply(handle);
         }
         
         hand.sendProtocolPacket(proto);
      }      
   }
   
   public void fireGetAllMeetingsCommand(DSMPHandler hand, byte flags, 
                                         byte handle) {
      byte opcode = DSMPGenerator.OP_GETALLMEETINGS_REPLY;
      if (validateLoggedIn(hand, handle, opcode)) {
         Vector ans = new Vector();
         
        // If someone is querying for their meetings, make sure we have the
        //  lastest automeeting info on file
         refreshAutoMeetingFile(false);
         
        // Remove SYNC
         Object arr[] = meetings.values().toArray();
         for(int i=0; i < arr.length; i++) {
            Meeting meeting = (Meeting)arr[i];
            Invite invite = meeting.getInviteForUser(this, 
                                                     hand.getHandlerId());
            if (invite != null) {
               User user = getUser(meeting.getAnyOwnerId());
               DSMPMeeting dm = new DSMPMeeting(invite.getInviteId(),
                                                user.getLoginId(),
                                                invite.getInviteType(),
                                                meeting.getMeetingId(),
                                                meeting.getTitle(),
                                                user.getName(),
                                                meeting.getClassification());
               ans.addElement(dm);
            }
         }
         DSMPProto proto = DSMPGenerator.getAllMeetingsReply(handle, ans);
         hand.sendProtocolPacket(proto);
      }
   }
   public void fireMouseUpdateCommand(DSMPHandler hand, byte flags, 
                                      byte handle, 
                                      int meetingid,
                                      boolean buttonEvent, 
                                      boolean pressRelease,
                                      boolean realMotion,
                                      byte button, short x, short y) {
      
      DSMPProto proto;
      if (!hand.bitsSetInFlags(0x01)) {
         proto = DSMPGenerator.mouseUpdateError(handle, meetingid, 
                                                0, "Not Logged In");
         hand.sendProtocolPacket(proto);
         return;
      }
      
      int handlerid = hand.getHandlerId();
      Meeting meeting = (Meeting)meetings.get(new Integer(meetingid));
      if (meeting == null) {
         proto = DSMPGenerator.mouseUpdateError(handle, meetingid, 
                                                0, "Invalid Meeting Id");
      } else if (!meeting.containsParticipant(handlerid)) {
         proto = DSMPGenerator.mouseUpdateError(handle, meetingid, 
                                                0, "Not In Meeting");
      } else {
      
        // JMC 5/21/04 - Changed from getOwnerId to getModeratorId 
         if (realMotion && (meeting.getController()     != handlerid &&
                            meeting.getModeratorId()    != handlerid)) {
            proto = DSMPGenerator.mouseUpdateError(handle, meetingid, 
                                                   0, "Not In Control");
         } else {
            proto = DSMPGenerator.mouseUpdateEvent(handle, 
                                                   buttonEvent, 
                                                   pressRelease,
                                                   realMotion, 
                                                   meetingid, handlerid,
                                                   x, y, button);
           // JMC 5/21/04 - Changed from getOwnerId to getModeratorId 
            if (meeting.getModeratorId() == handlerid || !realMotion) {
               meeting.sendToParticipants(this, proto, true, handlerid);
            } else {
               sendProtoTo(proto, meeting.getModeratorId());
            }
            proto = null;
         }
      }
      if (proto != null) {
         hand.sendProtocolPacket(proto);
      }
   } 
      
   public void fireKeyUpdateCommand(DSMPHandler hand, byte flags, 
                                    byte handle, 
                                    int meetingid,
                                    boolean pressRelease,
                                    boolean keyCodeOrChar,
                                    short x, short y, 
                                    int javakeysym) {
      DSMPProto proto;
      if (!hand.bitsSetInFlags(0x01)) {
         proto = DSMPGenerator.keyUpdateError(handle, meetingid, 
                                              0, "Not Logged In");
         hand.sendProtocolPacket(proto);
         return;
      }
      
      int handlerid = hand.getHandlerId();
      Meeting meeting = (Meeting)meetings.get(new Integer(meetingid));
      if (meeting == null) {
         proto = DSMPGenerator.keyUpdateError(handle, meetingid, 
                                              0, "Invalid Meeting Id");
      } else if (!meeting.containsParticipant(handlerid)) {
         proto = DSMPGenerator.keyUpdateError(handle, meetingid, 
                                              0, "Not In Meeting");
      } else {
        // JMC 5/21/04 - Changed from getOwnerId to getModeratorId 
         if (meeting.getController()   != handlerid &&
             meeting.getModeratorId()  != handlerid) {
            proto = DSMPGenerator.keyUpdateError(handle, meetingid, 
                                                 0, "Not In Control");
         } else {
            proto = DSMPGenerator.keyUpdateEvent(handle, pressRelease, 
                                                 keyCodeOrChar,
                                                 meetingid, handlerid,
                                                 x, y, javakeysym);
            meeting.sendToParticipants(this, proto, false, 0);
            proto = null;
         }
      }
      if (proto != null) {
         hand.sendProtocolPacket(proto);
      }
   }
   public void fireFrameUpdateCommand(DSMPHandler hand, byte flags, 
                                      byte handle, 
                                      int meetingid,
                                      short x, short y, short w, short h,
                                      boolean compressed, byte buf[],
                                      int ofs, int len) {
      DSMPProto proto;
      if (!hand.bitsSetInFlags(0x01)) {
         proto = DSMPGenerator.frameUpdateError(handle, meetingid, 
                                              0, "Not Logged In");
         hand.sendProtocolPacket(proto);
         return;
      }
      
      long tt = System.currentTimeMillis();
      if (frameupdatetime == 0) {
         resizetime = tt;
      }
      
      frameupdatetime = tt;
      framesrecv++;
      
      int handlerid = hand.getHandlerId();
      Meeting meeting = (Meeting)meetings.get(new Integer(meetingid));
      if (meeting == null) {
         proto = DSMPGenerator.frameUpdateError(handle, meetingid, 
                                                0, "Invalid Meeting Id");
      } else if (!meeting.containsParticipant(handlerid)) {
         proto = DSMPGenerator.frameUpdateError(handle, meetingid, 
                                                0, "Not In Meeting");
      } else {
        // JMC 5/21/04 - Changed from getOwnerId to getModeratorId 
         if (meeting.getModeratorId()    != handlerid) {
            proto = DSMPGenerator.frameUpdateError(handle, meetingid, 
                                                   0, "Not Moderator");
         } else {
            proto = DSMPGenerator.frameUpdateEvent(handle, compressed, 
                                                   meetingid, x, y, w, h, 
                                                   buf, ofs, len);
            meeting.sendToParticipants(this, proto, true, handlerid);
            proto = null;
         }
      }
      if (proto != null) {
         hand.sendProtocolPacket(proto);
      }
   }
   
   public void fireMultiFrameUpdateCommand(DSMPHandler hand, byte flags, 
                                           byte handle, boolean compressed, 
                                           int meetingid, DSMPProto proto) {
                                           
      if (!hand.bitsSetInFlags(0x01)) {
         proto = DSMPGenerator.frameUpdateError(handle, meetingid, 
                                              0, "Not Logged In");
         hand.sendProtocolPacket(proto);
         return;
      }
      
      long tt = System.currentTimeMillis();
      if (frameupdatetime == 0) {
         resizetime = tt;
      }
      
      frameupdatetime = tt;
      framesrecv++;
      
      int handlerid = hand.getHandlerId();
      Meeting meeting = (Meeting)meetings.get(new Integer(meetingid));
      if (meeting == null) {
         proto = DSMPGenerator.frameUpdateError(handle, meetingid, 
                                                0, "Invalid Meeting Id");
      } else if (!meeting.containsParticipant(handlerid)) {
         proto = DSMPGenerator.frameUpdateError(handle, meetingid, 
                                                0, "Not In Meeting");
      } else {
        // JMC 5/21/04 - Changed from getOwnerId to getModeratorId 
         if (meeting.getModeratorId()    != handlerid) {
            proto = DSMPGenerator.frameUpdateError(handle, meetingid, 
                                                   0, "Not Moderator");
         } else {
            proto.setOpcode(DSMPGenerator.OP_MULTIFRAMEUPDATE_EVENT);
            meeting.sendToParticipants(this, proto, true, handlerid);
            proto = null;
         }
      }
      if (proto != null) {
         hand.sendProtocolPacket(proto);
      }
   }
   
   public void fireMultiFrameCollectionCommand(DSMPHandler hand, 
                                               int meetingid, Vector v) {
      DSMPProto proto;
      byte      handle = (byte)0;
      
      if (!hand.bitsSetInFlags(0x01)) {
         proto = DSMPGenerator.frameUpdateError(handle, meetingid, 
                                                0, "Not Logged In");
         hand.sendProtocolPacket(proto);
         return;
      }
      
      long tt = System.currentTimeMillis();
      if (frameupdatetime == 0) {
         resizetime = tt;
      }
      
      frameupdatetime = tt;
      framesrecv++;
      
      int handlerid = hand.getHandlerId();
      Meeting meeting = (Meeting)meetings.get(new Integer(meetingid));
      if (meeting == null) {
         proto = DSMPGenerator.frameUpdateError(handle, meetingid, 
                                                0, "Invalid Meeting Id");
      } else if (!meeting.containsParticipant(handlerid)) {
         proto = DSMPGenerator.frameUpdateError(handle, meetingid, 
                                                0, "Not In Meeting");
      } else {
        // JMC 5/21/04 - Changed from getOwnerId to getModeratorId 
         if (meeting.getModeratorId()    != handlerid) {
            proto = DSMPGenerator.frameUpdateError(handle, meetingid, 
                                                   0, "Not Moderator");
         } else {
            meeting.sendToParticipants(this, v, true, handlerid);
            proto = null;
         }
      }
      if (proto != null) {
         hand.sendProtocolPacket(proto);
      }      
   }
   
   public void fireFrameEnd(DSMPHandler hand, byte flags, byte handle,
                            int meetingid) {
      DSMPProto proto;
      if (!hand.bitsSetInFlags(0x01)) {
         proto = DSMPGenerator.frameEndError(handle, meetingid, 
                                             0, "Not Logged In");
         hand.sendProtocolPacket(proto);
         return;
      }
      
      long tt = System.currentTimeMillis();
      if (frameupdatetime == 0) {
         resizetime = tt;
      }
      
      frameupdatetime = tt;
      framesrecv++;
      
      int handlerid = hand.getHandlerId();
      Meeting meeting = (Meeting)meetings.get(new Integer(meetingid));
      if (meeting == null) {
         proto = DSMPGenerator.frameEndError(handle, meetingid, 
                                             0, "Invalid Meeting Id");
      } else if (!meeting.containsParticipant(handlerid)) {
         proto = DSMPGenerator.frameEndError(handle, meetingid, 
                                             0, "Not In Meeting");
      } else {
        // JMC 5/21/04 - Changed from getOwnerId to getModeratorId 
         if (meeting.getModeratorId()    != handlerid) {
            proto = DSMPGenerator.frameEndError(handle, meetingid, 
                                                0, "Not Moderator");
         } else {
            proto = DSMPGenerator.frameEndEvent(handle, meetingid);
            meeting.sendToParticipants(this, proto, true, handlerid);
            proto = null;
         }
      }
      if (proto != null) {
         hand.sendProtocolPacket(proto);
      }
   }

   public void fireFrozenMode(DSMPHandler hand, byte flags, byte handle,
                              int meetingid, boolean frozen) {
      DSMPProto proto;
      if (!hand.bitsSetInFlags(0x01)) {
         proto = DSMPGenerator.frozenModeError(handle, meetingid, 
                                               0, "Not Logged In");
         hand.sendProtocolPacket(proto);
         return;
      }
      
      long tt = System.currentTimeMillis();
      if (frameupdatetime == 0) {
         resizetime = tt;
      }
      
      frameupdatetime = tt;
      framesrecv++;
      
      int handlerid = hand.getHandlerId();
      Meeting meeting = (Meeting)meetings.get(new Integer(meetingid));
      if (meeting == null) {
         proto = DSMPGenerator.frozenModeError(handle, meetingid, 
                                             0, "Invalid Meeting Id");
      } else if (!meeting.containsParticipant(handlerid)) {
         proto = DSMPGenerator.frozenModeError(handle, meetingid, 
                                             0, "Not In Meeting");
      } else {
        // JMC 5/21/04 - Changed from getOwnerId to getModeratorId 
         if (meeting.getModeratorId()    != handlerid) {
            proto = DSMPGenerator.frozenModeError(handle, meetingid, 
                                                0, "Not Moderator");
         } else {
            meeting.setFrozen(frozen);
            proto = DSMPGenerator.frozenModeEvent(meetingid, frozen);
            meeting.sendToParticipants(this, proto, false, 0);
            proto = null;
         }
      }
      if (proto != null) {
         hand.sendProtocolPacket(proto);
      }
   }
   
   public void fireTransferModeratorCommand(DSMPHandler hand, 
                                            byte flags, byte handle, 
                                            int meetingid,
                                            int partid) {
      byte opcode = DSMPGenerator.OP_TRANSFERMODERATOR_REPLY;
      if (validateLoggedIn(hand, handle, opcode)) {
         DSMPProto proto = null;
         Meeting meeting = (Meeting)meetings.get(new Integer(meetingid));
         if (meeting == null) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                                    "Invalid meetingid");
         } else if (!meeting.isOwner(hand.getHandlerId())) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                              "Only an Owner can change the Moderator");
         } else if (!meeting.containsParticipant(partid)) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                                    "Invalid participantid");
         } else {
         
           // JMC 5/21/04 - Stop Sharing, if sharing in progress
            proto=DSMPGenerator.controlEvent(handle, meetingid, 
                                             meeting.getModeratorId());
            meeting.sendToParticipants(this, proto, false, 0);
         
            proto=DSMPGenerator.frozenModeEvent(meetingid, false);
            meeting.sendToParticipants(this, proto, false, 0);
         
            proto = DSMPGenerator.stopShareEvent((byte)0,
                                                 meeting.getMeetingId());
            meeting.sendToParticipants(this, proto, false, 0);
                        
            proto = 
               DSMPGenerator.moderatorChangeEvent((byte)0, 
                                                  meeting.getMeetingId(),
                                                  meeting.getModeratorId(), 
                                                  partid);
                                                  
            meeting.sendToParticipants(this, proto, false, 0);
            
            meeting.setModeratorId(partid);
            meeting.setController(partid);
            
            proto=DSMPGenerator.controlEvent(handle, meetingid, 
                                             meeting.getController());
            meeting.sendToParticipants(this, proto, false, 0);
            proto = null;
            
            
         }
         if (proto == null) {
            proto = DSMPGenerator.transferModeratorReply(handle);
         }
         
         hand.sendProtocolPacket(proto);
      }      
   }
   public void fireAssignOwnershipCommand(DSMPHandler hand, 
                                          byte flags, byte handle, 
                                          int meetingid, int partid, 
                                          boolean onOrOff) {
      byte opcode = DSMPGenerator.OP_ASSIGNOWNERSHIP_REPLY;
      if (validateLoggedIn(hand, handle, opcode)) {
         DSMPProto proto = null;
         Meeting meeting = (Meeting)meetings.get(new Integer(meetingid));
         if (meeting == null) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                                    "Invalid meetingid");
         } else if (!meeting.isOwner(hand.getHandlerId())) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                              "Only an Owner can assign ownership");
         } else if (!onOrOff && meeting.numOwners() <= 1) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                              "Can't remove the only owner");
         } else if (!meeting.containsParticipant(partid)) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                                    "Invalid participantid");
         } else {
         
            proto = 
               DSMPGenerator.ownershipChangeEvent((byte)0, 
                                                  meeting.getMeetingId(),
                                                  partid, onOrOff);
                                                  
            meeting.sendToParticipants(this, proto, false, 0);
            
            if (onOrOff) meeting.addOwner(partid);
            else         meeting.removeOwner(partid);
            
            proto = null;
         }
         
         if (proto == null) {
            proto = DSMPGenerator.assignOwnershipReply(handle);
         }
         
         hand.sendProtocolPacket(proto);
      }      
   }
   public void fireStartShareCommand(DSMPHandler hand, 
                                     byte flags, byte handle, 
                                     int meetingid) {
      byte opcode = DSMPGenerator.OP_STARTSHARE_REPLY;
      if (validateLoggedIn(hand, handle, opcode)) {
         DSMPProto proto = null;
         Meeting meeting = (Meeting)meetings.get(new Integer(meetingid));
         if (meeting == null) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                                    "Invalid meetingid");
         } else if (meeting.getModeratorId() != hand.getHandlerId()) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                              "Only the Moderator can send Start Share");
         } else {
         
            proto = DSMPGenerator.startSharingReply(handle);
            hand.sendProtocolPacket(proto);
            
            proto = DSMPGenerator.startShareEvent((byte)0, 
                                                  meeting.getMeetingId());
                                                  
            meeting.setSharing(true);
            meeting.sendToParticipants(this, proto, true, 
	                                   meeting.getModeratorId());
            
         }
         
         if (proto != null) {
            hand.sendProtocolPacket(proto);
         }
         
      }      
   }
   public void fireStopShareCommand(DSMPHandler hand, 
                                    byte flags, byte handle, 
                                    int meetingid) {
      byte opcode = DSMPGenerator.OP_STOPSHARE_REPLY;
      if (validateLoggedIn(hand, handle, opcode)) {
         DSMPProto proto = null;
         Meeting meeting = (Meeting)meetings.get(new Integer(meetingid));
         if (meeting == null) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                                    "Invalid meetingid");
         } else if (meeting.getModeratorId() != hand.getHandlerId()) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                              "Only the Moderator can send Stop Share");
         } else {
            meeting.setSharing(false);
            
            proto = DSMPGenerator.stopSharingReply(handle);
            hand.sendProtocolPacket(proto);
            
            proto=DSMPGenerator.controlEvent(handle, meetingid, 
                                             meeting.getModeratorId());
            meeting.sendToParticipants(this, proto, false, 0);
         
            proto=DSMPGenerator.frozenModeEvent(meetingid, false);
            meeting.sendToParticipants(this, proto, false, 0);
            
            proto = DSMPGenerator.stopShareEvent((byte)0, 
                                                 meeting.getMeetingId());
                                                  
            meeting.sendToParticipants(this, proto, false, 0);
            
         }
         
         if (proto != null) {
            hand.sendProtocolPacket(proto);
         }
         
      }      
   }
   
  // JMC 11/15/04 - Pure copy code from DropboxServer with appropriate mods
   public void fireCreateGroupCommand(DSMPHandler hand, byte flags, 
                                      byte handle, String groupname,
                                      byte visibility, 
                                      byte listability) {
      byte opcode=DSMPGenerator.OP_CREATE_GROUP_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DSMPProto proto = null;
      try {
         validateLoggedIn(hand, handle, opcode);
      
         subtask = "finding user object";
         User user = getUserEx(hand.getHandlerId());
         
         System.out.println("CreateGroup called: groupname=" + groupname);
         
         whereFailed = 1;
         subtask = "creating group";
         Groups.createGroup(user, groupname);
         
         whereFailed = 2;
         subtask = "Modifying attributes for vis/listability";
         
         Groups.modifyGroupAttributes(user, groupname, 
                                      visibility, listability);
         proto = DSMPGenerator.createGroupReply(handle);
      } catch(DboxException dbex) {
         String errmsg = "Create group [" + groupname + "] ";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         System.out.println(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         proto = DSMPGenerator.genericReplyError(opcode, handle, 
                                                 dbex.getErrorCode(), 
                                                 errmsg);
      }
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
    
   public void fireDeleteGroupCommand(DSMPHandler hand, byte flags, 
                                      byte handle, String groupname) {
      byte opcode=DSMPGenerator.OP_DELETE_GROUP_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DSMPProto proto = null;
      try {
         validateLoggedIn(hand, handle, opcode);
      
         subtask = "finding user object";
         User user = getUserEx(hand.getHandlerId());
         System.out.println("DeleteGroup called: groupname=" + groupname);
         
         whereFailed = 1;
         subtask = "deleting group";
         Groups.deleteGroup(user, groupname);
         proto = DSMPGenerator.deleteGroupReply(handle);
      } catch(DboxException dbex) {
         String errmsg = "Delete group [" + groupname + "] ";
         
         if        (whereFailed == 0) {
            errmsg += "failed while " + subtask;
         } else if (whereFailed == 1) {
            errmsg += "failed";
         } else {
            errmsg += "succeeded, but failed while " + subtask;
         }
         System.out.println(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         proto = DSMPGenerator.genericReplyError(opcode, handle, 
                                                 dbex.getErrorCode(), 
                                                 errmsg);
      }
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
   
   public void fireModifyGroupAclCommand(DSMPHandler hand, byte flags, 
                                         byte handle, 
                                         boolean memberOrAccess,
                                         boolean addOrRemove,
                                         String groupname, String username) {
      byte opcode=DSMPGenerator.OP_MODIFY_GROUP_ACL_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DSMPProto proto = null;
      try {
         validateLoggedIn(hand, handle, opcode);
      
         subtask = "finding user object";
         User user = getUserEx(hand.getHandlerId());
         System.out.println("ModifyGroupAcl called:" + 
                            " group=" + groupname +
                            " user=" + username +
                            " memberOrAccess=" + memberOrAccess +
                            " addRemove=" + addOrRemove);
         
         if (addOrRemove) {                        
            
           /*
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
                     Groups.addGroupAcl(user, groupname, 
                                        laclname, memberOrAccess);
                  }
               }
             } else
           */
            {
               subtask = "adding member to group";
               whereFailed = 1;
               Groups.addGroupAcl(user, groupname, 
                                  username, memberOrAccess);
            }
            
         } else {
            subtask = "removing member from group";
            whereFailed = 1;
            Groups.removeGroupAcl(user, groupname, 
                                  username, memberOrAccess);
         }
         proto = DSMPGenerator.modifyGroupAclReply(handle);
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
         System.out.println(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         proto = DSMPGenerator.genericReplyError(opcode, handle, 
                                                 dbex.getErrorCode(), 
                                                 errmsg);
      }
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
   
   public void fireModifyGroupAttributeCommand(DSMPHandler hand,
                                               byte flags, 
                                               byte handle, String groupname,
                                               byte visibility, 
                                               byte listability) {
      byte opcode=DSMPGenerator.OP_MODIFY_GROUP_ATTRIBUTES_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DSMPProto proto = null;
      try {
         validateLoggedIn(hand, handle, opcode);
         
         subtask = "finding user object";
         User user = getUserEx(hand.getHandlerId());
         System.out.println("ModifyGroupAttribute called: " +
                            " groupname="   + groupname +
                            " visibility="  + visibility +
                            " listability=" + listability);
         
         whereFailed = 1;
         subtask = "modifying visibility/listability attributes";
         Groups.modifyGroupAttributes(user, groupname, 
                                      visibility, listability);
         proto = DSMPGenerator.modifyGroupAttributesReply(handle);
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
         System.out.println(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         proto = DSMPGenerator.genericReplyError(opcode, handle, 
                                                 dbex.getErrorCode(), 
                                                 errmsg);
      }
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }
   
   public void fireQueryGroupsCommand(DSMPHandler hand, byte flags,
                                      byte handle, 
                                      boolean regexSearch,
                                      boolean wantMember,
                                      boolean wantAccess,
                                      String groupname) {
                                      
      byte opcode=DSMPGenerator.OP_QUERY_GROUPS_REPLY;
      
      int whereFailed = 0;   // 0 = PRE, 1 = During, 2 = POST
      String subtask = "validating that user is logged in";
      
      DSMPProto proto = null;
      try {
      
         validateLoggedIn(hand, handle, opcode);
         
         subtask = "finding user object";
         User user = getUserEx(hand.getHandlerId());
         System.out.println("QueryGroups called: " +
                            " groupname="   + groupname +
                            " wantMember="  + wantMember +
                            " wantAccess="  + wantAccess +
                            " regexSearch=" + regexSearch);
         
         whereFailed = 1;
         subtask = "searching for matching groups";
         Hashtable ret = Groups.getMatchingGroups(user, groupname, 
                                                  regexSearch,
                                                  false, false, 
                                                  false, true, false,
                                                  true, true,
                                                  true);
         
         subtask = "building reply protocol";
         whereFailed = 2;
         
         proto = DSMPGenerator.queryGroupsReply(handle);
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
               proto.appendByte(DSMPGenerator.GROUP_SCOPE_NONE);
               proto.appendByte(DSMPGenerator.GROUP_SCOPE_NONE);
            }
            
            byte listable = gi.getGroupListability();
            byte lflags = (byte)(modify && wantAccess?2:0);
            lflags |= 
               (byte)((wantMember && 
                       ((modify)                    ||
                        (listable == DSMPGenerator.GROUP_SCOPE_ALL)     ||
                        ((listable == DSMPGenerator.GROUP_SCOPE_MEMBER) &&
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
         System.out.println(errmsg + "<@-@>" + dbex.getMessage());
         errmsg = buildReturnErrorMsg(errmsg, dbex.getMessage());
         proto = DSMPGenerator.genericReplyError(opcode, handle, 
                                                 dbex.getErrorCode(), 
                                                 errmsg);
      }
      if ((flags & 0x40) != 0) proto.addMaskToFlags(0x40); // Synch flag
      hand.sendProtocolPacket(proto);
   }                                      
   
   public void firePlaceCallCommand(DSMPHandler hand, byte flags,
                                    byte handle, 
                                    boolean meetingIdOrUser,
                                    int meetingid,
                                    String userToCall,
                                    String password) {
      byte opcode = DSMPGenerator.OP_PLACECALL_REPLY;
      if (validateLoggedIn(hand, handle, opcode)) {
         DSMPProto proto = null;
         int loginid = hand.getHandlerId();
         
         User user = getUser(loginid);
         
         if (meetingIdOrUser) {
         
            Meeting meeting = (Meeting)meetings.get(new Integer(meetingid));
         
            if (meeting == null) {
               proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                                       "Invalid meetingid");
            } else if (meeting.containsParticipant(loginid)) {
               proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                             "You are already IN the meeting");
            } else if (meeting.containsUser(this, loginid)){
               proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                             "Another Instance of your ID is IN the meeting");
            } else {
               
               if         (!meeting.canInvite(this, loginid)) {
                 // Check if we can be invited
                  proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                             "Specified meeting is not valid or available");
               
               } else {
                 // Are we already invited
                  boolean alreadyInvited = 
                     meeting.getInviteForUser(this, loginid) != null;
                     
                 // If we are already invited OR we have the correct 
                 //   meeting password
                  if (alreadyInvited || meeting.isCorrectPassword(password)) {
                       
                    // Send reply to 'PlaceCall'
                     proto = DSMPGenerator.placeCallReply(handle);
                     hand.sendProtocolPacket(proto);
                     
                    // Add the invitation if not there already
                     if (!alreadyInvited) {
                        meeting.addInvitation(this, user, 
                                              user.getName(), 
                                              DSMPGenerator.STATUS_NONE,
                                              false);
                     }
                     
                    // Send accept event on behalf of the owners
                     User owneruser = getUser(meeting.getAnyOwnerId());
                     proto = 
                        DSMPGenerator.acceptCallEvent((byte)0, true, meetingid,
                                                      owneruser.getLoginId(),
                                                      owneruser.getName(),
                                                      owneruser.getCompany());
                                                      
                  } else if (meeting.acceptsColdCalls()) {
                    // Send to each owner asking for entry
                     
                    // Send PlaceCall reply now
                     proto = DSMPGenerator.placeCallReply(handle);
                     hand.sendProtocolPacket(proto);
                     
                    // Generate unique salt for this call
                    //  and create proto for call event
                     user.generateSalt();
                     proto = DSMPGenerator.placeCallEvent((byte)0,
                                                          meetingid,
                                                          loginid,
                                                          user.getName(),
                                                          user.getCompany(),
                                                          user.getSalt());
                     
                    // Send call proto to each owner
                     Enumeration enum = meeting.getOwners().elements();
                     while(enum.hasMoreElements()) {
                        int ownerid = ((Integer)enum.nextElement()).intValue();
                        sendProtoTo(proto, ownerid);
                     }
                     
                     proto = null;
                     
                  } else {
                    // No cold calling available
                     proto = DSMPGenerator.genericReplyError(opcode, handle, 0,
                               "Specified meeting is not valid or available");
                  }
               }
            }
         } else {
            
           // For each meeting, check if userToCall is an owner,
           //  if found, 
           //    if already invited
           //      send accepted event
           //    else
           //      send PlaceCallEvent to owner
            Vector v = getMeetingsOwnedBy(userToCall);
            Enumeration enum = v.elements();
            
           // No cold calling OR no meetings found
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0,
                            "No valid meetings for " + userToCall + " found");
            
            Vector coldcalls = new Vector();
            
            while(enum.hasMoreElements()) {
               Meeting meeting = (Meeting)enum.nextElement();
               
               if (meeting.canInvite(this, loginid)) {
                 // Are we already invited
                  boolean alreadyInvited = 
                     meeting.getInviteForUser(this, loginid) != null;
                     
                 // If we are already invited OR we have the correct 
                 //   meeting password
                  if (alreadyInvited || meeting.isCorrectPassword(password)) {
                    // Send reply to 'PlaceCall'
                     proto = DSMPGenerator.placeCallReply(handle);
                     hand.sendProtocolPacket(proto);
                     
                    // Add the invitation if not there already
                     if (!alreadyInvited) {
                        meeting.addInvitation(this, user, 
                                              user.getName(), 
                                              DSMPGenerator.STATUS_NONE,
                                              false);
                     }
                     
                    // Send accept event on behalf of the owner
                     User owneruser = meeting.getOwner(userToCall);
                     proto = 
                        DSMPGenerator.acceptCallEvent((byte)0, true, 
                                                      meeting.getMeetingId(),
                                                      owneruser.getLoginId(),
                                                      owneruser.getName(),
                                                      owneruser.getCompany());
                     hand.sendProtocolPacket(proto);
                                                      
                    // Don't act on any coldcalls or errors ...
                     coldcalls.clear();
                     proto = null;
                     break;
                  } else if (meeting.acceptsColdCalls()) {                     
                     coldcalls.addElement(meeting);
                  }
               }
            }
            
           // If we found at least on meeting to coldcall into ...
            if (coldcalls.size() > 0) {
               enum = coldcalls.elements();
               user.generateSalt();
               
              // Send PlaceCall reply now
               proto = DSMPGenerator.placeCallReply(handle);
               hand.sendProtocolPacket(proto);
               
               while(enum.hasMoreElements()) {
               
                  Meeting meeting = (Meeting)enum.nextElement();
                  
                 // Generate unique salt for this call
                 //  and create proto for call event
                  proto = DSMPGenerator.placeCallEvent((byte)0,
                                                       meeting.getMeetingId(),
                                                       loginid,
                                                       user.getName(),
                                                       user.getCompany(),
                                                       user.getSalt());
                 // Send call proto to each owner
                  User owneruser = meeting.getOwner(userToCall);
                  sendProtoTo(proto, owneruser.getLoginId());
               }
               proto = null;
            }
         }
         
         if (proto != null) hand.sendProtocolPacket(proto);
      }
   }
   
   public void fireAcceptCallCommand(DSMPHandler hand, byte flags,
                                     byte handle, int meetingid, 
                                     int callerid, String salt) {
                                    
      byte opcode = DSMPGenerator.OP_ACCEPTCALL_REPLY;
      if (validateLoggedIn(hand, handle, opcode)) {
         DSMPProto proto = null;
         int loginid = hand.getHandlerId();
         
         User user = getUser(loginid);
         User callingUser = getUser(callerid);
         
         Meeting meeting = (Meeting)meetings.get(new Integer(meetingid));
         
         if (meeting == null) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                            "Invalid meetingid or not owner");
         } else if (!meeting.isOwner(loginid)) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                             "Invalid meetingid or not owner");
         } else {
            
            if (callingUser == null                || 
                !meeting.canInvite(this, callerid) ||
                callingUser.getSalt() == null      ||
                !callingUser.getSalt().equalsIgnoreCase(salt)) {
              // Check if we can be invited
               proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                          "User being accepted is not valid");
            } else {
            
              // Send reply to 'AcceptCall'
               proto = DSMPGenerator.acceptCallReply(handle);
               hand.sendProtocolPacket(proto);
               
              // Add the invitation if not there already
               if (meeting.getInviteForUser(this, 
                                            callingUser.getLoginId()) == null){
                  meeting.addInvitation(this, callingUser, 
                                        callingUser.getName(), 
                                        DSMPGenerator.STATUS_NONE,
                                        false);
               }
                  
              // Send accept event to calling user
               proto = 
                  DSMPGenerator.acceptCallEvent((byte)0, false, meetingid,
                                                user.getLoginId(),
                                                user.getName(),
                                                user.getCompany());
                                                
               sendProtoTo(proto, callingUser.getLoginId());
               proto = null;
            }
         }
         
         if (proto != null) hand.sendProtocolPacket(proto);
      }
   }
   
   public void fireGetMeetingURLCommand(DSMPHandler hand, byte flags,
                                        byte handle, 
                                        boolean includepw,
                                        boolean includemeetid,
                                        boolean sendemails,
                                        int meetingid) {
                                    
      byte opcode = DSMPGenerator.OP_GET_MEETING_URL_REPLY;
      if (validateLoggedIn(hand, handle, opcode)) {
         DSMPProto proto = null;
         int loginid = hand.getHandlerId();
         
         User user = getUser(loginid);
         
         Meeting meeting = (Meeting)meetings.get(new Integer(meetingid));
         
         if (meeting == null) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                            "Invalid meetingid or not owner");
         } else if (!meeting.isOwner(loginid)) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                             "Invalid meetingid or not owner");
         } else {
            
            String url = FEHostURL + "/" + ESS + MeetingParms;
            
            if (includepw && meeting.getPassword() != null && 
                meeting.getPassword().length() > 0) {
               url += ":pw:" + meeting.getHashedPassword();
            }
            
            if (includemeetid) {
               url += ":mid:" + meeting.getMeetingId();
            } else {
               url += ":user:" + user.getName();
            }
            
            proto = DSMPGenerator.getMeetingURLReply(handle, 
                                                     meeting.getMeetingId(),
                                                     url);
                                                     
            if (sendemails) {
               System.out.println("Searching for amt users for invites: ");
               
               Vector v = meeting.getFlatInviteList();
               Enumeration enum = v.elements();
               
               if (v.size() > 0) {
                  System.out.println("Sending email for meeting " + 
                                     meeting.getMeetingId() + ":" + url);
                  amtMailer.sendAMTMail(user, v, url);
               }
            }
         }
                  
         if (proto != null) hand.sendProtocolPacket(proto);
      }
   }
   
   public void fireSetMeetingOptionsCommand(DSMPHandler hand, byte flags,
                                            byte handle, 
                                            int meetingid,
                                            Hashtable options) {
                                    
      byte opcode = DSMPGenerator.OP_SET_MEETING_OPTIONS_REPLY;
      if (validateLoggedIn(hand, handle, opcode)) {
         DSMPProto proto = null;
         int loginid = hand.getHandlerId();
         
         User user = getUser(loginid);
         
         Meeting meeting = (Meeting)meetings.get(new Integer(meetingid));
         
         if (meeting == null) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                            "Invalid meetingid or not owner");
         } else if (!meeting.isOwner(loginid)) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                             "Invalid meetingid or not owner");
         } else {
            
            proto = DSMPGenerator.setMeetingOptionsReply(handle);
            
            Enumeration keys = options.keys();
            while(keys.hasMoreElements()) {
               String key = (String)keys.nextElement();
               String val = (String)options.get(key);
               boolean err = false;
               if (key.equalsIgnoreCase(DSMPGenerator.MEETINGPASSWORD_OPTION)){
                  meeting.setPassword(val);
               } else if (key.equalsIgnoreCase(DSMPGenerator.COLDCALL_OPTION)){
                  meeting.acceptsColdCalls(val.equalsIgnoreCase("true"));
               } else {
                  err = true;
                  proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                                          "Invalid option: "
                                                          + key);
               }
               
               if (!err) {
                  DSMPProto ep = DSMPGenerator.meetingOptionEvent((byte)0,
                                                                  meetingid,
                                                                  key,
                                                                  val);
                  meeting.sendToOwners(ep, false, 0);
               }
            }
         }
                  
         if (proto != null) hand.sendProtocolPacket(proto);
      }
   }
   
   public void fireQueryMeetingOptionsCommand(DSMPHandler hand, byte flags,
                                              byte handle, 
                                              boolean querySpecific,
                                              int meetingid,
                                              String opt) {
                                    
      byte opcode = DSMPGenerator.OP_QUERY_MEETING_OPTIONS_REPLY;
      if (validateLoggedIn(hand, handle, opcode)) {
         DSMPProto proto = null;
         int loginid = hand.getHandlerId();
         
         User user = getUser(loginid);
         
         Meeting meeting = (Meeting)meetings.get(new Integer(meetingid));
         
         if (meeting == null) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                            "Invalid meetingid or not owner");
         } else if (!meeting.isOwner(loginid)) {
            proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                             "Invalid meetingid or not owner");
         } else {
            
            Hashtable options = new Hashtable();
            if (querySpecific) {
               if (opt.equalsIgnoreCase(DSMPGenerator.MEETINGPASSWORD_OPTION)){
                  options.put(DSMPGenerator.MEETINGPASSWORD_OPTION,
                              meeting.getPassword());
               } else if (opt.equalsIgnoreCase(DSMPGenerator.COLDCALL_OPTION)){
                  options.put(DSMPGenerator.COLDCALL_OPTION,
                              meeting.acceptsColdCalls()?"true":"false");
               } else {
                  proto = DSMPGenerator.genericReplyError(opcode, handle, 0, 
                                             "Invalid option: " + opt);
               }
            } else {
               options.put(DSMPGenerator.MEETINGPASSWORD_OPTION,
                           meeting.getPassword());
               options.put(DSMPGenerator.COLDCALL_OPTION,
                           meeting.acceptsColdCalls()?"true":"false");
            }
            
            if (proto == null) {
               proto = DSMPGenerator.queryMeetingOptionsReply(handle, 
                                                              meetingid, 
                                                              options);
            }
         }
                  
         if (proto != null) hand.sendProtocolPacket(proto);
      }
   }
   
   
  /* -------------------------------------------------------*\
  ** Replies
  \* -------------------------------------------------------*/
   public void fireLoginReply(DSMPHandler hand, byte flags, byte handle,
                              int  inviteid, Vector v) {
      invalidProtocol(hand, DSMPGenerator.OP_LOGIN_REPLY);
   }
   
   public void fireLogoutReply(DSMPHandler hand, byte flags, byte handle) {
      invalidProtocol(hand, DSMPGenerator.OP_LOGOUT_REPLY);
   }
   public void fireStartMeetingReply(DSMPHandler hand, byte flags, byte handle,
                                     int  meetingid, int participantid) {
      invalidProtocol(hand, DSMPGenerator.OP_STARTMEETING_REPLY);
   }
   public void fireLeaveMeetingReply(DSMPHandler hand, byte flags, 
                                     byte handle) {
      invalidProtocol(hand, DSMPGenerator.OP_LEAVEMEETING_REPLY);
   }
   public void fireEndMeetingReply(DSMPHandler hand, byte flags, byte handle) {
      invalidProtocol(hand, DSMPGenerator.OP_ENDMEETING_REPLY);
   }
   public void fireCreateInvitationReply(DSMPHandler hand, byte flags, 
                                         byte handle, 
                                         int  inviteid) {
      invalidProtocol(hand, DSMPGenerator.OP_CREATEINVITATION_REPLY);
   }
   public void fireJoinMeetingReply(DSMPHandler hand, byte flags, byte handle, 
                                    int meetingid, int participantid) {
      invalidProtocol(hand, DSMPGenerator.OP_JOINMEETING_REPLY);
   }
   public void fireDropInviteeReply(DSMPHandler hand, byte flags, 
                                    byte handle) {
      invalidProtocol(hand, DSMPGenerator.OP_DROPINVITEE_REPLY);
   }
   public void fireImageResizeReply(DSMPHandler hand, byte flags, 
                                    byte handle) {
      invalidProtocol(hand, DSMPGenerator.OP_IMAGERESIZE_REPLY);
   }
   public void fireChatMessageReply(DSMPHandler hand, byte flags, 
                                    byte handle) {
      invalidProtocol(hand, DSMPGenerator.OP_CHATMESSAGE_REPLY);
   }
   public void fireModifyControlReply(DSMPHandler hand, byte flags, 
                                      byte handle) {
      invalidProtocol(hand, DSMPGenerator.OP_MODIFYCONTROL_REPLY);
   }
   public void fireGetAllMeetingsReply(DSMPHandler hand, byte flags, 
                                       byte handle, 
                                       Vector v) {
      invalidProtocol(hand, DSMPGenerator.OP_GETALLMEETINGS_REPLY);
   }
   public void fireTransferModeratorReply(DSMPHandler h,
                                          byte flags, byte handle) {
      invalidProtocol(h, DSMPGenerator.OP_TRANSFERMODERATOR_REPLY);
   }
   public void fireAssignOwnershipReply(DSMPHandler h, byte flags, 
                                        byte handle) {
      invalidProtocol(h, DSMPGenerator.OP_ASSIGNOWNERSHIP_REPLY);
   }
   public void fireStartShareReply(DSMPHandler h, byte flags, 
                                   byte handle) {
      invalidProtocol(h, DSMPGenerator.OP_STARTSHARE_REPLY);
   }
   public void fireStopShareReply(DSMPHandler h, byte flags, 
                                  byte handle) {
      invalidProtocol(h, DSMPGenerator.OP_STOPSHARE_REPLY);
   }
   
   
  /* -------------------------------------------------------*\
  ** Reply Errors
  \* -------------------------------------------------------*/
  
   public void fireGenericReplyError(DSMPHandler hand, byte flags, 
                                     byte handle, 
                                     byte opcode, 
                                     short errorcode, String errorStr) {
      invalidProtocol(hand, opcode);
   }
   
   public void fireLoginReplyError(DSMPHandler hand, byte flags, byte handle, 
                                   short errorcode, 
                                   String errorStr) {
      fireGenericReplyError(hand, flags, handle, 
                            DSMPGenerator.OP_LOGIN_REPLY,
                            errorcode, errorStr);
   }
                                      
   public void fireLogoutReplyError(DSMPHandler hand, byte flags, byte handle,
                                    short errorcode, 
                                    String errorStr) {
      fireGenericReplyError(hand, flags, handle, 
                            DSMPGenerator.OP_LOGOUT_REPLY,
                            errorcode, errorStr);
   }
   public void fireStartMeetingReplyError(DSMPHandler hand, byte flags, 
                                          byte handle,
                                          short errorcode, 
                                          String errorStr) {
      fireGenericReplyError(hand, flags, handle, 
                            DSMPGenerator.OP_STARTMEETING_REPLY,
                            errorcode, errorStr);
   }
   public void fireLeaveMeetingReplyError(DSMPHandler hand, byte flags, 
                                          byte handle,
                                          short errorcode, 
                                          String errorStr) {
      fireGenericReplyError(hand, flags, handle, 
                            DSMPGenerator.OP_LEAVEMEETING_REPLY,
                            errorcode, errorStr);
   }
   public void fireEndMeetingReplyError(DSMPHandler hand, byte flags, 
                                        byte handle,
                                        short errorcode, 
                                        String errorStr) {
      fireGenericReplyError(hand, flags, handle, 
                            DSMPGenerator.OP_ENDMEETING_REPLY,
                            errorcode, errorStr);
   }
   public void fireCreateInvitationReplyError(DSMPHandler hand, byte flags, 
                                              byte handle,
                                              short errorcode, 
                                              String errorStr) {
      fireGenericReplyError(hand, flags, handle, 
                            DSMPGenerator.OP_CREATEINVITATION_REPLY,
                            errorcode, errorStr);
   }
   public void fireJoinMeetingReplyError(DSMPHandler hand, byte flags, 
                                         byte handle,
                                         short errorcode, String errorStr) {
      fireGenericReplyError(hand, flags, handle, 
                            DSMPGenerator.OP_JOINMEETING_REPLY,
                            errorcode, errorStr);
   }
   public void fireDropInviteeReplyError(DSMPHandler hand, byte flags, 
                                         byte handle,
                                         short errorcode, String errorStr) {
      fireGenericReplyError(hand, flags, handle, 
                            DSMPGenerator.OP_DROPINVITEE_REPLY,
                            errorcode, errorStr);
   }
   public void fireImageResizeReplyError(DSMPHandler hand, byte flags, 
                                         byte handle,
                                         short errorcode, String errorStr) {
      fireGenericReplyError(hand, flags, handle, 
                            DSMPGenerator.OP_IMAGERESIZE_REPLY,
                            errorcode, errorStr);
   }
   public void fireChatMessageReplyError(DSMPHandler hand, byte flags, 
                                         byte handle,
                                         short errorcode, String errorStr) {
      fireGenericReplyError(hand, flags, handle, 
                            DSMPGenerator.OP_CHATMESSAGE_REPLY,
                            errorcode, errorStr);
   }
   public void fireModifyControlReplyError(DSMPHandler hand, byte flags, 
                                           byte handle,
                                           short errorcode, String errorStr) {
      fireGenericReplyError(hand, flags, handle, 
                            DSMPGenerator.OP_MODIFYCONTROL_REPLY,
                            errorcode, errorStr);
   }
   public void fireGetAllMeetingsReplyError(DSMPHandler hand, byte flags,
                                            byte handle,
                                            short errorcode, String errorStr) {
      fireGenericReplyError(hand, flags, handle, 
                            DSMPGenerator.OP_GETALLMEETINGS_REPLY,
                            errorcode, errorStr);
   }
   public void fireTransferModeratorReplyError(DSMPHandler hand, byte flags,
                                               byte handle,
                                               short errorcode, 
                                               String errorStr) {
      fireGenericReplyError(hand, flags, handle, 
                            DSMPGenerator.OP_TRANSFERMODERATOR_REPLY,
                            errorcode, errorStr);
   }
   public void fireAssignOwnershipReplyError(DSMPHandler hand, byte flags,
                                             byte handle,
                                             short errorcode, String errorStr) {
      fireGenericReplyError(hand, flags, handle, 
                            DSMPGenerator.OP_ASSIGNOWNERSHIP_REPLY,
                            errorcode, errorStr);
   }
   public void fireStartShareReplyError(DSMPHandler hand, byte flags,
                                        byte handle,
                                        short errorcode, String errorStr) {
      fireGenericReplyError(hand, flags, handle, 
                            DSMPGenerator.OP_STARTSHARE_REPLY,
                            errorcode, errorStr);
   }
   public void fireStopShareReplyError(DSMPHandler hand, byte flags,
                                       byte handle,
                                       short errorcode, String errorStr) {
      fireGenericReplyError(hand, flags, handle, 
                            DSMPGenerator.OP_STOPSHARE_REPLY,
                            errorcode, errorStr);
   }
   
   
  /* -------------------------------------------------------*\
  ** Events
  \* -------------------------------------------------------*/
   public void fireEndMeetingEvent(DSMPHandler hand, byte flags, byte handle, 
                                   int meetingid) {
      invalidProtocol(hand, DSMPGenerator.OP_ENDMEETING_EVENT);
   }
   public void fireNewInvitationEvent(DSMPHandler hand, byte flags, 
                                      byte handle, 
                                      int meetingid, int inviteid,
                                      byte inviteType, String name) {
      invalidProtocol(hand, DSMPGenerator.OP_NEWINVITATION_EVENT);
   }
   public void fireJoinedMeetingEvent(DSMPHandler hand, byte flags, 
                                      byte handle, 
                                      int meetingid, int inviteid,
                                      int participantid, int loginid,
                                      byte inviteType, String name) {
      invalidProtocol(hand, DSMPGenerator.OP_JOINEDMEETING_EVENT);
   }
                                      
   public void fireDroppedEvent(DSMPHandler hand, byte flags, 
                                byte handle, 
                                int meetingid,
                                int inviteid, int participant, 
                                boolean inviteDropped, 
                                boolean participantDropped) {
      invalidProtocol(hand, DSMPGenerator.OP_DROPPED_EVENT);
                                
   }
   public void fireImageResizeEvent(DSMPHandler hand, byte flags, byte handle, 
                                    int meetingid,
                                    short x, short y, short w, short h) {
      invalidProtocol(hand, DSMPGenerator.OP_IMAGERESIZE_EVENT);
                                    
   }
   public void fireFrameUpdateEvent(DSMPHandler hand, byte flags, byte handle, 
                                    int meetingid,
                                    short x, short y, short w, short h,
                                    boolean compressed, byte buf[],
                                    int ofs, int len) {
      invalidProtocol(hand, DSMPGenerator.OP_FRAMEUPDATE_EVENT);
   }
   public void fireChatMessageEvent(DSMPHandler hand, byte flags, byte handle,
                                    int meetingid,
                                    int participantid, int toid, String msg,
                                    boolean unicast) {
      invalidProtocol(hand, DSMPGenerator.OP_CHATMESSAGE_EVENT);
   }
   public void fireControlEvent(DSMPHandler hand, byte flags, byte handle, 
                                int meetingid, 
                                int participantid) {
      invalidProtocol(hand, DSMPGenerator.OP_CONTROL_EVENT);
   }
   public void fireMouseUpdateEvent(DSMPHandler hand, byte flags, byte handle, 
                                    int meetingid,
                                    boolean buttonEvent, 
                                    boolean pressRelease,
                                    boolean realMotion,
                                    byte button, short x, short y, 
                                    int participantid) {
      invalidProtocol(hand, DSMPGenerator.OP_MOUSEUPDATE_EVENT);
   }
   public void fireKeyUpdateEvent(DSMPHandler hand, byte flags, byte handle, 
                                  int meetingid,
                                  boolean pressRelease,
                                  short x, short y, 
                                  int javakeysym,
                                  int participantid) {
      invalidProtocol(hand, DSMPGenerator.OP_KEYUPDATE_EVENT);
   }
   
   public void fireModeratorChangeEvent(DSMPHandler h, 
                                        byte flags, byte handle, 
                                        int meetingid,
                                        int fromPartid,
                                        int toPartid) {
      invalidProtocol(h, DSMPGenerator.OP_MODERATORCHANGE_EVENT);
   }
   public void fireOwnershipChangeEvent(DSMPHandler h, 
                                        byte flags, byte handle, 
                                        int meetingid, int partid, 
                                        boolean addOrRemove) {
      invalidProtocol(h, DSMPGenerator.OP_OWNERCHANGE_EVENT);
   }
   public void fireStartShareEvent(DSMPHandler h, byte flags, byte handle, 
                                   int meetingid) {
      invalidProtocol(h, DSMPGenerator.OP_STARTSHARE_EVENT);
   }   
   public void fireStopShareEvent(DSMPHandler h, byte flags, byte handle, 
                                  int meetingid) {
      invalidProtocol(h, DSMPGenerator.OP_STOPSHARE_EVENT);
   }   
  /* -------------------------------------------------------*\
  ** Errors
  \* -------------------------------------------------------*/
   
   public void fireFrameUpdateError(DSMPHandler hand, byte flags, byte handle, 
                                    int meetingid,
                                    short errorcode, String errorString) {
      invalidProtocol(hand, DSMPGenerator.OP_FRAMEUPDATE_ERROR);
   }
   
   public void fireKeyUpdateError(DSMPHandler hand, byte flags, byte handle, 
                                  int meetingid,
                                  short errorcode, String errorString) {
      invalidProtocol(hand, DSMPGenerator.OP_KEYUPDATE_ERROR);
   }
   public void fireMouseUpdateError(DSMPHandler hand, byte flags, byte handle, 
                                    int meetingid, 
                                    short errorcode, String errorString) {
      invalidProtocol(hand, DSMPGenerator.OP_MOUSEUPDATE_ERROR);
   }
   
   
  /* 
  ** Override the Dispatcher's dispatchProtocol method. This is called from
  **  the handler ... we collect FRAMEUPDATE's for the same meeting until
  **  either we see a protocolRest or a non-FRAMEUPDATE or a FRAMEUPDATE for
  **  a different meeting.
  */
   public void dispatchProtocol(DSMPBaseProto proto, DSMPBaseHandler hIn) 
                                      throws InvalidProtocolException {
                                      
      DSMPHandler h = (DSMPHandler)hIn;
      byte op = proto.getOpcode();
      if (op == DSMPGenerator.OP_PROTOCOLREST) {
      
         flushHeldProto(h);
         
      } else if (op != DSMPGenerator.OP_FRAMEUPDATE) {
      
         flushHeldProto(h);
         dispatchProtocolI(proto, h, true);
         
      } else { 
//         System.out.println("VQIN = " + proto.getVirtualQuadrant());
         
         int xi = proto.getExtraInt();
         if (h.getHeldMeetingId() != xi) {
            flushHeldProto(h);
         }
         
         dispatchProtocolI(proto, h, false);
         proto.setOpcode(DSMPGenerator.OP_FRAMEUPDATE_EVENT);
         h.setHeldMeetingId(xi);
         Vector heldProto = h.getHeldProtoVector();
         heldProto.addElement(proto);
         
         if (heldProto.size() > MAX_HELD_PROTO) {
            flushHeldProto(h);
         }
      }
   }
   
   protected void flushHeldProto(DSMPHandler h) 
                                     throws InvalidProtocolException {
      Vector heldProto = h.getHeldProtoVector();
      if (heldProto.size() != 0) {
//         System.out.println("FHP = " + heldProto.size());
         fireMultiFrameCollectionCommand(h, h.getHeldMeetingId(), heldProto);
         heldProto.removeAllElements();
         h.setHeldMeetingId(-1);
      }
   }
}
