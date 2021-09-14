package oem.edge.ed.odc.meeting.common;

import  oem.edge.ed.odc.dsmp.common.*;

import java.lang.*;
import java.util.*;

public class DSMPGenerator implements CommonGenerator {

   public static void setDebug(boolean v) {
      cpress.setDebug(v);
   }
   private static Compression cpress = new Compression();
   
   public final static String MEETINGPASSWORD_OPTION = "password";
   public final static String COLDCALL_OPTION        = "coldcall";
   
  // Copied from DropboxGenerators
   public final static byte STATUS_GROUP               = -1;
   public final static byte STATUS_NONE                = 1;
   public final static byte STATUS_PROJECT             = 2;

  // Using bit7 is offlimits! Used for proto-morphing
   public final static byte FLAGS_BIT0                 = (byte)0x01;
   public final static byte FLAGS_BIT1                 = (byte)0x02;
   public final static byte FLAGS_BIT2                 = (byte)0x04;
   public final static byte FLAGS_BIT3                 = (byte)0x08;
   public final static byte FLAGS_BIT4                 = (byte)0x10;
   public final static byte FLAGS_BIT5                 = (byte)0x20;
   public final static byte FLAGS_BIT6                 = (byte)0x40;
   public final static byte FLAGS_XTRAINT              = (byte)0x80;

   public final static byte OPCOMMAND                  = (byte)1;
   public final static byte OPREPLY                    = (byte)41;
   public final static byte OPEVENT                    = (byte)81;
   public final static byte OPERROR                    = (byte)115;

   public final static byte OP_LOGIN                   = (OPCOMMAND+1);
   public final static byte OP_LOGOUT                  = (OPCOMMAND+2);
   public final static byte OP_STARTMEETING            = (OPCOMMAND+3);
   public final static byte OP_LEAVEMEETING            = (OPCOMMAND+4);
   public final static byte OP_ENDMEETING              = (OPCOMMAND+5);
   public final static byte OP_CREATEINVITATION        = (OPCOMMAND+6);
   public final static byte OP_JOINMEETING             = (OPCOMMAND+7);
   public final static byte OP_DROPINVITEE             = (OPCOMMAND+8);
   public final static byte OP_FRAMEUPDATE             = (OPCOMMAND+9);
   public final static byte OP_KEYUPDATE               = (OPCOMMAND+10);
   public final static byte OP_MOUSEUPDATE             = (OPCOMMAND+11);
   public final static byte OP_IMAGERESIZE             = (OPCOMMAND+12);
   public final static byte OP_CHATMESSAGE             = (OPCOMMAND+13);
   public final static byte OP_MODIFYCONTROL           = (OPCOMMAND+14);
   public final static byte OP_GETALLMEETINGS          = (OPCOMMAND+15);
   public final static byte OP_MULTIFRAMEUPDATE        = (OPCOMMAND+16);
   public final static byte OP_PROTOCOLREST            = (OPCOMMAND+17);
   public final static byte OP_FRAMEEND                = (OPCOMMAND+18);
   public final static byte OP_FROZENMODE              = (OPCOMMAND+19);
   public final static byte OP_TRANSFERMODERATOR       = (OPCOMMAND+20);
   public final static byte OP_STARTSHARE              = (OPCOMMAND+21);
   public final static byte OP_STOPSHARE               = (OPCOMMAND+22);
   public final static byte OP_ASSIGNOWNERSHIP         = (OPCOMMAND+23);
   
   public final static byte OP_CREATE_GROUP            = (OPCOMMAND+24);
   public final static byte OP_DELETE_GROUP            = (OPCOMMAND+25);
   public final static byte OP_MODIFY_GROUP_ACL        = (OPCOMMAND+26);
   public final static byte OP_MODIFY_GROUP_ATTRIBUTES = (OPCOMMAND+27);
   public final static byte OP_QUERY_GROUPS            = (OPCOMMAND+28);
   
   public final static byte OP_GET_MEETING_URL         = (OPCOMMAND+29);
   public final static byte OP_PLACECALL               = (OPCOMMAND+30);
   public final static byte OP_ACCEPTCALL              = (OPCOMMAND+31);
   public final static byte OP_QUERY_MEETING_OPTIONS   = (OPCOMMAND+32);
   public final static byte OP_SET_MEETING_OPTIONS     = (OPCOMMAND+33);
   public final static byte OP_SEND_MEETING_EMAIL      = (OPCOMMAND+34);
   
   public final static byte OPLASTCOMMAND       = (OP_SEND_MEETING_EMAIL);
   
   public final static byte OP_LOGIN_REPLY             = (OPREPLY  +1);
   public final static byte OP_LOGOUT_REPLY            = (OPREPLY  +2);
   public final static byte OP_STARTMEETING_REPLY      = (OPREPLY  +3);
   public final static byte OP_LEAVEMEETING_REPLY      = (OPREPLY  +4);
   public final static byte OP_ENDMEETING_REPLY        = (OPREPLY  +5);
   public final static byte OP_CREATEINVITATION_REPLY  = (OPREPLY  +6) ;
   public final static byte OP_JOINMEETING_REPLY       = (OPREPLY  +7);
   public final static byte OP_DROPINVITEE_REPLY       = (OPREPLY  +8);
   public final static byte OP_IMAGERESIZE_REPLY       = (OPREPLY  +9);
   public final static byte OP_CHATMESSAGE_REPLY       = (OPREPLY  +10);
   public final static byte OP_MODIFYCONTROL_REPLY     = (OPREPLY  +11) ;
   public final static byte OP_GETALLMEETINGS_REPLY    = (OPREPLY  +12);
   public final static byte OP_TRANSFERMODERATOR_REPLY = (OPREPLY  +13);
   public final static byte OP_STARTSHARE_REPLY        = (OPREPLY  +14);
   public final static byte OP_STOPSHARE_REPLY         = (OPREPLY  +15);
   public final static byte OP_ASSIGNOWNERSHIP_REPLY   = (OPREPLY  +16);
       
   public final static byte OP_CREATE_GROUP_REPLY             = (OPREPLY  +17);
   public final static byte OP_DELETE_GROUP_REPLY             = (OPREPLY  +18);
   public final static byte OP_MODIFY_GROUP_ACL_REPLY         = (OPREPLY  +19);
   public final static byte OP_MODIFY_GROUP_ATTRIBUTES_REPLY  = (OPREPLY  +20);
   public final static byte OP_QUERY_GROUPS_REPLY             = (OPREPLY  +21);
   
   public final static byte OP_GET_MEETING_URL_REPLY          = (OPREPLY+22);
   public final static byte OP_PLACECALL_REPLY                = (OPREPLY+23);
   public final static byte OP_ACCEPTCALL_REPLY               = (OPREPLY+24);
   public final static byte OP_QUERY_MEETING_OPTIONS_REPLY    = (OPREPLY+26);
   public final static byte OP_SET_MEETING_OPTIONS_REPLY      = (OPREPLY+27);
   public final static byte OP_SEND_MEETING_EMAIL_REPLY       = (OPREPLY+28);
   
   public final static byte OPLASTREPLY                
                                            = (OP_SEND_MEETING_EMAIL_REPLY);
   
   public final static byte OP_ENDMEETING_EVENT        = (OPEVENT  +1);
   public final static byte OP_NEWINVITATION_EVENT     = (OPEVENT  +2);
   public final static byte OP_JOINEDMEETING_EVENT     = (OPEVENT  +3);
   public final static byte OP_DROPPED_EVENT           = (OPEVENT  +4);
   public final static byte OP_FRAMEUPDATE_EVENT       = (OPEVENT  +5);
   public final static byte OP_IMAGERESIZE_EVENT       = (OPEVENT  +6);
   public final static byte OP_CHATMESSAGE_EVENT       = (OPEVENT  +7);
   public final static byte OP_CONTROL_EVENT           = (OPEVENT  +8);
   public final static byte OP_MOUSEUPDATE_EVENT       = (OPEVENT  +9);
   public final static byte OP_KEYUPDATE_EVENT         = (OPEVENT  +10);
   public final static byte OP_MULTIFRAMEUPDATE_EVENT  = (OPEVENT  +11);
   public final static byte OP_FRAMEEND_EVENT          = (OPEVENT  +12);
   public final static byte OP_FROZENMODE_EVENT        = (OPEVENT  +13);
   public final static byte OP_MODERATORCHANGE_EVENT   = (OPEVENT  +14);   
   public final static byte OP_OWNERCHANGE_EVENT       = (OPEVENT  +15);   
   public final static byte OP_STARTSHARE_EVENT        = (OPEVENT  +16);   
   public final static byte OP_STOPSHARE_EVENT         = (OPEVENT  +17);   
   
   public final static byte OP_PLACECALL_EVENT         = (OPEVENT  +18);   
   public final static byte OP_ACCEPTCALL_EVENT        = (OPEVENT  +19);   
   public final static byte OP_MEETING_OPTION_EVENT    = (OPEVENT  +20);   
   
   public final static byte OPLASTEVENT           = (OP_MEETING_OPTION_EVENT);

   public final static byte OP_FRAMEUPDATE_ERROR       = (OPERROR  +1);
   public final static byte OP_KEYUPDATE_ERROR         = (OPERROR  +2);
   public final static byte OP_MOUSEUPDATE_ERROR       = (OPERROR  +3);
   public final static byte OP_FRAMEEND_ERROR          = (OPERROR  +4);
   public final static byte OP_FROZENMODE_ERROR        = (OPERROR  +5);
   public final static byte OPLASTERROR                = (OP_FROZENMODE_ERROR);
   
   static boolean isError(byte op) {
      return op >= OPERROR && op <= OPLASTERROR;
   }
   static boolean isReply(byte op) {
      return op >= OPREPLY && op <= OPLASTREPLY;
   }
   static boolean isEvent(byte op) {
      return op >= OPEVENT && op <= OPLASTEVENT;
   }
   static boolean isCommand(byte op) {
      return op >= OPCOMMAND && op <= OPLASTCOMMAND;
   }
   
   public static String opcodeToString(byte opcode) {
      String s = null;
      switch(opcode) {
         case OP_LOGIN:
            s = "LOGIN"; break;
         case OP_LOGOUT:
            s = "LOGOUT"; break;
         case OP_STARTMEETING:
            s = "STARTMEETING"; break;
         case OP_LEAVEMEETING:
            s = "LEAVEMEETING"; break;
         case OP_ENDMEETING:
            s = "ENDMEETING"; break;
         case OP_CREATEINVITATION:
            s = "CREATEINVITATION"; break;
         case OP_JOINMEETING:
            s = "JOINMEETING"; break;
         case OP_DROPINVITEE:
            s = "DROPINVITEE"; break;
         case OP_FRAMEUPDATE:
            s = "FRAMEUPDATE"; break;
         case OP_KEYUPDATE:
            s = "KEYUPDATE"; break;
         case OP_MOUSEUPDATE:
            s = "MOUSEUPDATE"; break;
         case OP_IMAGERESIZE:
            s = "IMAGERESIZE"; break;
         case OP_CHATMESSAGE:
            s = "CHATMESSAGE"; break;
         case OP_MODIFYCONTROL:
            s = "MODIFYCONTROL"; break;
         case OP_GETALLMEETINGS:
            s = "GETALLMEETINGS"; break;
         case OP_MULTIFRAMEUPDATE:
            s = "MULTIFRAMEUPDATE"; break;
         case OP_PROTOCOLREST:
            s = "PROTOCOLREST"; break;
         case OP_FRAMEEND:
            s = "FRAMEEND"; break;
         case OP_FROZENMODE:
            s = "FROZENMODE"; break;
         case OP_TRANSFERMODERATOR:
            s = "TRANSFERMODERATOR"; break;
         case OP_ASSIGNOWNERSHIP:
            s = "ASSIGNOWNERSHIP"; break;
         case OP_STARTSHARE:
            s = "OP_STARTSHARE"; break;
         case OP_STOPSHARE:
            s = "OP_STOPSHARE"; break;
         case OP_CREATE_GROUP:
            s = "OP_CREATE_GROUP"; break;
         case OP_DELETE_GROUP:
            s = "OP_DELETE_GROUP"; break;
         case OP_MODIFY_GROUP_ACL:
            s = "OP_MODIFY_GROUP_ACL"; break;
         case OP_MODIFY_GROUP_ATTRIBUTES:
            s = "OP_MODIFY_GROUP_ATTRIBUTES"; break;
         case OP_QUERY_GROUPS:
            s = "OP_QUERY_GROUPS"; break;
         case OP_GET_MEETING_URL:
            s = "OP_GET_MEETING_URL"; break;
         case OP_PLACECALL:
            s = "OP_PLACECALL"; break;
         case OP_ACCEPTCALL:
            s = "OP_ACCEPTCALL"; break;
         case OP_QUERY_MEETING_OPTIONS:
            s = "OP_QUERY_MEETING_OPTIONS"; break;
         case OP_SET_MEETING_OPTIONS:
            s = "OP_SET_MEETING_OPTIONS"; break;
         case OP_SEND_MEETING_EMAIL:
            s = "OP_SEND_MEETING_EMAIL"; break;
            
         case OP_LOGIN_REPLY:
            s = "LOGIN_REPLY"; break;
         case OP_LOGOUT_REPLY:
            s = "LOGOUT_REPLY"; break;
         case OP_STARTMEETING_REPLY:
            s = "STARTMEETING_REPLY"; break;
         case OP_LEAVEMEETING_REPLY:
            s = "LEAVEMEETING_REPLY"; break;
         case OP_ENDMEETING_REPLY:
            s = "ENDMEETING_REPLY"; break;
         case OP_CREATEINVITATION_REPLY:
            s = "CREATEINVITATION_REPLY"; break;
         case OP_JOINMEETING_REPLY:
            s = "JOINMEETING_REPLY"; break;
         case OP_DROPINVITEE_REPLY:
            s = "DROPINVITEE_REPLY"; break;
         case OP_IMAGERESIZE_REPLY:
            s = "IMAGERESIZE_REPLY"; break;
         case OP_CHATMESSAGE_REPLY:
            s = "CHATMESSAGE_REPLY"; break;
         case OP_MODIFYCONTROL_REPLY:
            s = "MODIFYCONTROL_REPLY"; break;
         case OP_GETALLMEETINGS_REPLY:
            s = "GETALLMEETINGS_REPLY"; break;
         case OP_TRANSFERMODERATOR_REPLY:
            s = "TRANSFERMODERATOR_REPLY"; break;
         case OP_ASSIGNOWNERSHIP_REPLY:
            s = "ASSIGNOWNERSHIP_REPLY"; break;
         case OP_STARTSHARE_REPLY:
            s = "STARTSHARE_REPLY"; break;
         case OP_STOPSHARE_REPLY:
            s = "STOPSHARE_REPLY"; break;
         case OP_CREATE_GROUP_REPLY :
            s = "OP_CREATE_GROUP_REPLY "; break;
         case OP_DELETE_GROUP_REPLY:
            s = "OP_DELETE_GROUP_REPLY"; break;
         case OP_MODIFY_GROUP_ACL_REPLY:
            s = "OP_MODIFY_GROUP_ACL_REPLY"; break;
         case OP_MODIFY_GROUP_ATTRIBUTES_REPLY:
            s = "OP_MODIFY_GROUP_ATTRIBUTES_REPLY"; break;
         case OP_QUERY_GROUPS_REPLY:
            s = "OP_QUERY_GROUPS_REPLY"; break;
         case OP_GET_MEETING_URL_REPLY:
            s = "OP_GET_MEETING_URL_REPLY"; break;
         case OP_PLACECALL_REPLY:
            s = "OP_PLACECALL_REPLY"; break;
         case OP_ACCEPTCALL_REPLY:
            s = "OP_ACCEPTCALL_REPLY"; break;
         case OP_QUERY_MEETING_OPTIONS_REPLY:
            s = "OP_QUERY_MEETING_OPTIONS_REPLY"; break;
         case OP_SET_MEETING_OPTIONS_REPLY:
            s = "OP_SET_MEETING_OPTIONS_REPLY"; break;
         case OP_SEND_MEETING_EMAIL_REPLY:
            s = "OP_SEND_MEETING_EMAIL_REPLY"; break;
            
         case OP_ENDMEETING_EVENT:
            s = "ENDMEETING_EVENT"; break;
         case OP_NEWINVITATION_EVENT:
            s = "NEWINVITATION_EVENT"; break;
         case OP_JOINEDMEETING_EVENT:
            s = "JOINEDMEETING_EVENT"; break;
         case OP_DROPPED_EVENT:
            s = "DROPPED_EVENT"; break;
         case OP_FRAMEUPDATE_EVENT:
            s = "FRAMEUPDATE_EVENT"; break;
         case OP_IMAGERESIZE_EVENT:
            s = "IMAGERESIZE_EVENT"; break;
         case OP_CHATMESSAGE_EVENT:
            s = "CHATMESSAGE_EVENT"; break;
         case OP_CONTROL_EVENT:
            s = "CONTROL_EVENT"; break;
         case OP_MOUSEUPDATE_EVENT:
            s = "MOUSEUPDATE_EVENT"; break;
         case OP_KEYUPDATE_EVENT:
            s = "KEYUPDATE_EVENT"; break;
         case OP_MULTIFRAMEUPDATE_EVENT:
            s = "MULTIFRAMEUPDATE_EVENT"; break;
         case OP_FRAMEEND_EVENT:
            s = "FRAMEEND_EVENT"; break;
         case OP_FROZENMODE_EVENT:
            s = "FROZENMODE_EVENT"; break;
         case OP_MODERATORCHANGE_EVENT:
            s = "MODERATORCHANGE_EVENT"; break;
         case OP_OWNERCHANGE_EVENT:
            s = "OWNERCHANGE_EVENT"; break;
         case OP_STARTSHARE_EVENT:
            s = "STARTSHARE_EVENT"; break;
         case OP_STOPSHARE_EVENT:
            s = "STOPSHARE_EVENT"; break;
         case OP_PLACECALL_EVENT:
            s = "OP_PLACECALL_EVENT"; break;
         case OP_ACCEPTCALL_EVENT:
            s = "OP_ACCEPTCALL_EVENT"; break;
         case OP_MEETING_OPTION_EVENT:
            s = "OP_MEETING_OPTION_EVENT"; break;
            
         case OP_FRAMEUPDATE_ERROR:
            s = "FRAMEUPDATE_ERROR"; break;
         case OP_KEYUPDATE_ERROR:
            s = "KEYUPDATE_ERROR"; break;
         case OP_MOUSEUPDATE_ERROR:
            s = "MOUSEUPDATE_ERROR"; break;
         case OP_FRAMEEND_ERROR:
            s = "FRAMEEND_ERROR"; break;
         case OP_FROZENMODE_ERROR:
            s = "FROZENMODE_ERROR"; break;
         default: 
            s = "Invalid Opcode: " + opcode; break;
      }
      return s;
   }
   
   
  /*--------------------------------------------------------*\
  ** Generic Reply Error
  \*--------------------------------------------------------*/
   public static DSMPProto genericReplyError(byte opcode, byte handle, 
                                             int errorcode, String msg) {
      DSMPProto ret = new DSMPProto(opcode, (byte)0x00, handle);
      ret.appendShort(errorcode);
      ret.appendString8(msg!=null?msg:"");
      return ret;
   }
   
   public static DSMPProto genericReplyError(byte opcode, byte handle, 
                                             byte flags,
                                             int errorcode, String msg) {
      DSMPProto ret = new DSMPProto(opcode, flags, handle);
      ret.appendShort(errorcode);
      ret.appendString8(msg!=null?msg:"");
      return ret;
   }
   
   public static DSMPProto genericError(byte handle, 
                                        byte opcode,
                                        int meetingid,
                                        int errorcode, String msg) {
      
      DSMPProto ret = new DSMPProto(opcode, (byte)0, handle);
      ret.appendInteger(meetingid);
      ret.appendShort(errorcode);
      ret.appendString8(msg!=null?msg:"");
      return ret;
   }
   
  /*--------------------------------------------------------*\
  ** Login/Logout
  \*--------------------------------------------------------*/
  
  /*
  **   ======================================================================
  **   Login                  -  Log into meeting system
  **      
  **      flags   : BIT0 set data area contains TOKEN describing user 
  **                properties otherwise data area contains userid & pw
  **                BIT1 should be set ... if not (old client), login will
  **                fail
  **
  **      String16: Token or Username
  **      String8 : PW
  **
  **   LoginReply             - 
  **
  **      Flags   : Bit0 set if success
  **
  **      Integer : login ID# if success
  **      String16: Username
  **      String16: Companyname
  **      Integer : num projects
  **
  **    x String16: projectname          Repeated for each Project name
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  **
  **   ======================================================================
  **   Logout                 -  Log out of system, leave all meetings, etc
  **
  **      None
  ** 
  **   LogoutReply
  **      
  **      Flags   : Bit0 set if success.
  **                Connection will be closed by server soon after
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  **   ======================================================================
  */
  
   public static DSMPProto loginToken(byte handle, String token) {
      DSMPProto ret = new DSMPProto(OP_LOGIN, (byte)0x03, handle);
      ret.appendString16(token);
      ret.appendString8("");
      return ret;
   }
   
   public static DSMPProto loginUserPW(byte handle, String user, String pw) {
      DSMPProto ret = new DSMPProto(OP_LOGIN, (byte)0x02, handle);
      ret.appendString16(user);
      ret.appendString8(pw);
      return ret;
   }
   
   public static DSMPProto loginReply(byte handle, int loginid, String user,
                                      String company, Vector projects) {
      DSMPProto ret = new DSMPProto(OP_LOGIN_REPLY, (byte)0x01, handle);
      int num = (projects == null) ? (byte)0 : (int)projects.size();
      ret.appendInteger(loginid);
      ret.appendString16(user);
      ret.appendString16(company);
      ret.appendInteger(num);
      if (num > 0) {
         Enumeration enum = projects.elements();
         while(enum.hasMoreElements()) {
            String s = (String)enum.nextElement();
            ret.appendString16(s);
         }
      }
      return ret;
   }
   
   public static DSMPProto logout(byte handle) {
      DSMPProto ret = new DSMPProto(OP_LOGOUT, (byte)0x00, handle);
      return ret;
   }
   
   public static DSMPProto logoutReply(byte handle) {
      DSMPProto ret = new DSMPProto(OP_LOGOUT_REPLY, (byte)0x01, handle);
      return ret;
   }
   
   
  /*--------------------------------------------------------*\
  ** StartMeeting/LeaveMeeting/EndMeeting
  \*--------------------------------------------------------*/
  /*   
  **   ======================================================================
  **   StartMeeting           -  Start a new meeting
  **      
  **      String8 : Meeting Title
  **      String8 : Meeting Password
  **      String8 : Classification
  **      
  **   StartMeetingReply      -  Caller becomes new meeting's only participant
  **
  **      Flags   : Bit0 set if success.
  **
  **      Integer : meeting     ID# if success
  **      Integer : participant ID# if success
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  **
  **   ======================================================================
  **   LeaveMeeting           -  End user request to leave meeting
  **
  **      Integer : meeting     ID# 
  **      
  **   LeaveMeetingReply      - 
  **
  **      Flags   : Bit0 set if success.
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  **
  **  =======================================================================
  **   EndMeeting             -  Meeting Owner request to end meeting
  **      
  **      Integer : meeting     ID# 
  **
  **   EndMeetingReply        - 
  **
  **      Flags  : Bit0 set if success.
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  **      
  **   EndMeetingEvent        -  Sent to all participants when meeting ends
  **
  **      Integer : Meeting     ID#
  **   ======================================================================
  */   
   
   public static DSMPProto startMeeting(byte handle, String title, 
                                        String pw,   String classification) {
      DSMPProto ret = new DSMPProto(OP_STARTMEETING, (byte)0x00, handle);
      ret.appendString8(title);
      ret.appendString8(pw!=null?pw:"");
      ret.appendString8(classification!=null?classification:"");
      return ret;
   }
   
   public static DSMPProto startMeetingReply(byte handle, int meetingid, 
                                             int participantid) {
      DSMPProto ret = new DSMPProto(OP_STARTMEETING_REPLY, 
                                    (byte)0x01, handle);
      ret.appendInteger(meetingid);
      ret.appendInteger(participantid);
      return ret;
   }
   
   public static DSMPProto leaveMeeting(byte handle, int meetingid) {
      DSMPProto ret = new DSMPProto(OP_LEAVEMEETING, (byte)0x00, handle);
      ret.appendInteger(meetingid);
      return ret;
   }
   
   public static DSMPProto leaveMeetingReply(byte handle) {
      DSMPProto ret = new DSMPProto(OP_LEAVEMEETING_REPLY, 
                                    (byte)0x01, handle);
      return ret;
   }
   
   public static DSMPProto endMeeting(byte handle, int meetingid) {
      DSMPProto ret = new DSMPProto(OP_ENDMEETING, (byte)0x00, handle);
      ret.appendInteger(meetingid);
      return ret;
   }
   public static DSMPProto endMeetingEvent(byte handle, int meetingid) {
      DSMPProto ret = new DSMPProto(OP_ENDMEETING_EVENT, (byte)0x00, handle);
      ret.appendInteger(meetingid);
      return ret;
   }
   
   public static DSMPProto endMeetingReply(byte handle) {
      DSMPProto ret = new DSMPProto(OP_ENDMEETING_REPLY, (byte)0x01, handle);
      return ret;
   }
   
  /*--------------------------------------------------------*\
  ** GetAllMeetings
  \*--------------------------------------------------------*/
  /*
  **   ======================================================================
  **   GetAllMeetings         -  Get list of meetings available for LoginID
  **      
  **      None
  **
  **   GetAllMeetingsReply    -
  **
  **      Flags   : Bit0 set if success
  **
  **      Short   : nummeetings
  **
  **    x Integer : Invitation ID#
  **    x Integer : Login      ID# of meeting Owner
  **    x Byte    : invite type (user, project or group)
  **    x Integer : Meeting    ID# 
  **    x String8 : Title
  **    x String8 : Owner name
  **    x String8 : Classification
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  **
  **   ======================================================================
  */   
   public static DSMPProto getAllMeetings(byte handle) {
      DSMPProto ret = new DSMPProto(OP_GETALLMEETINGS, (byte)0x00, handle);
      return ret;
   }
   public static DSMPProto getAllMeetingsReply(byte handle, Vector v) {
      DSMPProto ret = new DSMPProto(OP_GETALLMEETINGS_REPLY, (byte)0x01, 
                                    handle);
      ret.appendShort(v != null?v.size():0);
      if (v != null) {
         Enumeration enum = v.elements();
         while(enum.hasMoreElements()) {
            DSMPMeeting m = (DSMPMeeting)enum.nextElement();
            ret.appendInteger(m.getInviteId());
            ret.appendInteger(m.getLoginId());
            ret.appendByte(m.getInviteType());
            ret.appendInteger(m.getMeetingId());
            ret.appendString8(m.getTitle()!=null?m.getTitle():"");
            ret.appendString8(m.getOwner()!=null?m.getOwner():"");
            ret.appendString8(m.getClassification()!=null?m.getClassification():"");
         }
      }
      return ret;
   }
   
  /*--------------------------------------------------------*\
  ** CreateInvitation/NewInvitationEvent
  \*--------------------------------------------------------*/
  /*
  **   ======================================================================
  **   CreateInvitation       -
  **
  **      Flags   : Bit0 set means Invitee name is PROJECT   (mutually ex w/bit2)
  **                Bit1 set means invitation is for Owner
  **                Bit2 set means invitation is a 'Group'   (mutually ex w/bit0)
  **
  **      Integer : Meeting ID#
  **      Byte    : invite type (STATUS_NONE, STATUS_PROJECT, STATUS_GROUP)
  **      String8 : Invitee Name
  **
  **   CreateInvitationReply  - 
  **
  **      Flags   : Bit0 set if success.
  **
  **      Integer : if success, contains Invitation ID#
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  **
  **   NewInvitationEvent     -  Sent to meeting Attendees and Direct invitees
  **
  **      Flags   : Bit0 set means invitation is a 'Project' (mutually ex w/bit2)
  **                Bit1 set means invitation is for Owner
  **                Bit2 set means invitation is a 'Group'   (mutually ex w/bit0)
  **
  **      Integer : Meeting     ID#
  **      Integer : invitite    ID# 
  **      Byte    : invite type (STATUS_NONE, STATUS_PROJECT, STATUS_GROUP)
  **      String8 : Invitee Name
  **
  **   ======================================================================
  */   
   public static DSMPProto createInvitation(byte handle, int meetingid, 
                                            byte inviteType, 
                                            boolean isOwner, 
                                            String  name) {
      byte flags = (byte)0;
      if (isOwner) flags |= (byte)2;
      if (inviteType == STATUS_PROJECT) flags |= (byte)1;
      if (inviteType == STATUS_GROUP)   flags |= (byte)4;
      DSMPProto ret = new DSMPProto(OP_CREATEINVITATION, flags, handle);
      ret.appendInteger(meetingid);
      ret.appendByte(inviteType);
      ret.appendString8(name);
      return ret;
   }
   
   public static DSMPProto createInvitationReply(byte handle, int inviteid) {
      DSMPProto ret = new DSMPProto(OP_CREATEINVITATION_REPLY, 
                                    (byte)0x01, handle);
      ret.appendInteger(inviteid);
      return ret;
   }
   
   public static DSMPProto newInvitationEvent(byte handle, int meetingid, 
                                              byte inviteType, 
                                              boolean isOwner,
                                              int inviteid, String  name) {
      byte flags = (byte)0;
      if (isOwner) flags |= (byte)2;
      if (inviteType == STATUS_PROJECT) flags |= (byte)1;
      if (inviteType == STATUS_GROUP)   flags |= (byte)4;
      DSMPProto ret = new DSMPProto(OP_NEWINVITATION_EVENT, flags, handle);
      ret.appendInteger(meetingid);
      ret.appendInteger(inviteid);
      ret.appendByte(inviteType);
      ret.appendString8(name);
      return ret;
   }

   
  /*--------------------------------------------------------*\
  ** JoinMeeting/JoinedMeetingEvent
  \*--------------------------------------------------------*/
   
  /*
  **   ======================================================================
  **   JoinMeeting            -
  ** 
  **      Integer : Meeting ID# to join
  **      
  **   JoinMeetingReply       - 
  **
  **      Flags   : Bit0 set if success
  **
  **      Integer : if success, contains Meeting     ID# 
  **      Integer : if success, contains participant ID#
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  **
  **   JoinedMeetingEvent     -
  **
  **      Flags   : Bit0 set means invitation via Project
  **                Bit1 set means participant is Meeting Owner
  **                Bit2 set means participant is Meeting Moderator
  **                Bit3 set means invitation via Group
  **
  **      Integer : Meeting     ID#
  **      Integer : invite      ID# 
  **      Integer : participant ID# 
  **      Integer : login       ID# 
  **      Byte    : invite type (STATUS_NONE, STATUS_PROJECT, STATUS_GROUP)
  **      String8 : Invitee Name
  **
  **   ======================================================================
  */
   public static DSMPProto joinMeeting(byte handle, int meetingid) {
      DSMPProto ret = new DSMPProto(OP_JOINMEETING, (byte)0, handle);
      ret.appendInteger(meetingid);
      return ret;
   }
   
   public static DSMPProto joinMeetingReply(byte handle, int meetingid, 
                                            int participantid) {
      DSMPProto ret = new DSMPProto(OP_JOINMEETING_REPLY,
                                    (byte)0x01, handle);
      
      ret.appendInteger(meetingid);
      ret.appendInteger(participantid);
      return ret;
   }
   
   public static DSMPProto joinedMeetingEvent(byte handle, 
                                              int inviteid,
                                              int meetingid, 
                                              int participantid,
                                              int loginid, 
                                              String name,
                                              byte inviteType,
                                              boolean isOwner,
                                              boolean isModerator) {
      byte flags = (byte)0;
      if (inviteType == STATUS_PROJECT) flags |= (byte)0x01;
      if (isOwner)                      flags |= (byte)0x02;
      if (isModerator)                  flags |= (byte)0x04;
      if (inviteType == STATUS_GROUP)   flags |= (byte)0x08;
      
      DSMPProto ret = new DSMPProto(OP_JOINEDMEETING_EVENT, flags, handle);
      ret.appendInteger(meetingid);
      ret.appendInteger(inviteid);
      ret.appendInteger(participantid);
      ret.appendInteger(loginid);
      ret.appendByte(inviteType);
      ret.appendString8(name);
      return ret;
   }
   
  /*--------------------------------------------------------*\
  ** DropInvitee/DroppedEvent
  \*--------------------------------------------------------*/
  /*   
  **   ======================================================================
  **   DropInvitee            -  MeetOwner to drop an invite and/or part
  **      
  **      Flags   : Bit0 set means drop Invitation
  **                Bit1 set means drop participant
  **
  **      Integer : Meeting     ID# 
  **      Integer : invitee     ID#
  **      Integer : participant ID# 
  **      
  **   DropInviteeReply       - 
  **
  **      Flags   : Bit0 set if success
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  **      
  **   DroppedEvent           -
  **
  **      Flags   : Bit0 set invitation  ID is Dropped
  **                Bit1 set participant ID is Dropped (and is valid)
  **
  **      Integer : Meeting     ID#
  **      Integer : invitation  ID# (always valid)
  **      Integer : participant ID# (valid when being dropped)
  **   ======================================================================
  */
   public static DSMPProto dropInvitee(byte handle, 
                                       int meetingid, 
                                       int inviteid,
                                       int participantid,
                                       boolean doInvitation,
                                       boolean doParticipant) {
      byte flags = (byte)0;
      if (doInvitation)  flags |= (byte)0x01;
      if (doParticipant) flags |= (byte)0x02;
      
      DSMPProto ret = new DSMPProto(OP_DROPINVITEE, flags, handle);
      ret.appendInteger(meetingid);
      ret.appendInteger(inviteid);
      ret.appendInteger(participantid);
      return ret;
   }
   
   public static DSMPProto dropInviteeReply(byte handle) {
      DSMPProto ret = new DSMPProto(OP_DROPINVITEE_REPLY, 
                                    (byte)0x01, handle);
      return ret;
   }
   
   public static DSMPProto droppedEvent(byte handle, 
                                        int meetingid, 
                                        int inviteid,
                                        int participantid,
                                        boolean doInvitation,
                                        boolean doParticipant) {
      byte flags = (byte)0;
      if (doInvitation)  flags |= (byte)0x01;
      if (doParticipant) flags |= (byte)0x02;
      
      DSMPProto ret = new DSMPProto(OP_DROPPED_EVENT, flags, handle);
      ret.appendInteger(meetingid);
      ret.appendInteger(inviteid);
      ret.appendInteger(participantid);
      return ret;
   }
   
  /*--------------------------------------------------------*\
  ** FrameUpdate/FrameUpdateEvent
  ** MultiFrameUpdate/MultiFrameUpdateEvent
  ** FrameUpdateError
  \*--------------------------------------------------------*/
  /*
  **   ======================================================================
  **   FrameUpdate/FrameUpdateEvent
  **      
  **      Flags   : Bit0 indicates compressed frame data
  **                Bit1 indicates was packed in MFUpdateEvent
  **                Bit7 ALWAYS on for xtraInt
  **
  **      xtraInt : Meeting     ID# 
  **
  **      Short   : x
  **      Short   : y
  **      Short   : w
  **      Short   : h
  **    x Byte    : 'Frame Data' - 24bit TrueColor 0xFFRRGGBB per pixel
  **
  **   MultiFrameUpdate/MultiFrameUpdateEvent
  **      
  **      Flags   : Bit0 indicates compressed mfd 
  **                Bit7 ALWAYS on for xtraInt
  **
  **      xtraInt: Meeting     ID# 
  **
  **      Integer : numEncapsulated
  **    x Byte    : 'MultiFrameData' Protocol items
  **
  **   FrameUpdateError       -  Returned to FrameUpdate generator if error
  **      Flags   : Bit0 indicates if MultiFrameUpdate error
  **      Integer : Meeting     ID# 
  **      Short   : errorcode
  **      String8 : contains error indicator
  **      
  **   ======================================================================
  */  
   public static DSMPProto frameUpdate(byte handle, 
                                       boolean compressed,
                                       int meetingid, 
                                       int x, int y, int w, int h,
                                       byte data[], int ofs, int len) {
      
      byte flags = (byte)(compressed?0x01:0);
      
      DSMPProto ret = new DSMPProto(len+16, OP_FRAMEUPDATE, flags, handle);
      ret.setExtraInt(meetingid);
      ret.appendShort(x);
      ret.appendShort(y);
      ret.appendShort(w);
      ret.appendShort(h);
      ret.appendData(data, ofs, len);
      return ret;
   }
   public static DSMPProto frameUpdateEvent(byte handle, 
                                            boolean compressed,
                                            int meetingid, 
                                            int x, int y, int w, int h,
                                            byte data[], int ofs, int len) {
      
      byte flags = (byte)(compressed?0x01:0);
      DSMPProto ret = new DSMPProto(len+16, OP_FRAMEUPDATE_EVENT, flags, handle);
      ret.setExtraInt(meetingid);
      ret.appendShort(x);
      ret.appendShort(y);
      ret.appendShort(w);
      ret.appendShort(h);
      ret.appendData(data, ofs, len);
      return ret;
   }
   
   public static DSMPProto multiFrameUpdate(byte handle, 
                                            boolean compress,
                                            int meetingid, 
                                            Vector frames) {
      
     // If compress is set to true ... The handler will manage the compression
      byte flags = (byte)(compress?0x01:0);
      DSMPProto ret = new DSMPProto(OP_MULTIFRAMEUPDATE, flags, handle, 
                                    new byte[164840], 0, 0);
      
     // meetingid needs to be uncompressed
      ret.setExtraInt(meetingid);
      
      ret.appendInteger(frames.size());
      Enumeration e = frames.elements();
      int len = 0;
      while(e.hasMoreElements()) {
         DSMPProto proto = (DSMPProto)e.nextElement();
         ret.appendByte(proto.getOpcode());
         ret.appendByte(proto.getFlags());
         ret.append3ByteInteger(proto.getDataLength());
         ret.appendByte(proto.getHandle());
         ret.appendInteger(proto.getExtraInt());
         CompressInfo ci = proto.getData();
         ret.appendData(ci.buf, ci.ofs, ci.len);
      }
      
     // If compression is desired
      if (compress) {
         CompressInfo ci = ret.getData();
         ci = cpress.compress(ci.buf, ci.ofs, ci.len);
         ret.setData(ci.buf, ci.ofs, ci.len);
      }
      
      return ret;
   }
  /* This is Not needed ... and potentially wrong (compress flag 
     should compress the entire non-header ...
   public static DSMPProto multiFrameUpdateEvent(byte handle, 
                                                 boolean compressed,
                                                 int meetingid, 
                                                 int numEncap,
                                                 byte data[], int ofs, 
                                                 int len) {
      
      byte flags = (byte)(compressed?1:0);
      DSMPProto ret = new DSMPProto(OP_MULTIFRAMEUPDATE_EVENT, flags, handle);
      ret.appendInteger(meetingid);
      ret.appendInteger(numEncap);
      ret.appendInteger(len);
      ret.appendData(data, ofs, len);
      return ret;
   }
  */
   public static DSMPProto frameUpdateError(byte handle, 
                                            int meetingid,
                                            int errorcode, String msg) {
      
      DSMPProto ret = new DSMPProto(OP_FRAMEUPDATE_ERROR, (byte)0, handle);
      ret.appendInteger(meetingid);
      ret.appendShort(errorcode);
      ret.appendString8(msg!=null?msg:"");
      return ret;
   }
   
   public static DSMPProto multiFrameUpdateError(byte handle,
                                                 int meetingid,
                                                 int errorcode, String msg) {
      
      DSMPProto ret = new DSMPProto(OP_FRAMEUPDATE_ERROR, (byte)0x01, handle);
      ret.appendInteger(meetingid);
      ret.appendShort(errorcode);
      ret.appendString8(msg!=null?msg:"");
      return ret;
   }
   
  /*--------------------------------------------------------*\
  ** KeyUpdate/KeyUpdateError/MouseUpdate/MouseUpdateError
  \*--------------------------------------------------------*/
  /*
  **  =======================================================================
  **   KeyUpdate              -  Key injection from controller - Sent to Owner
  **      
  **      Flags   : Bit0 set if PRESS
  **              : Bit1 set if java Virt KeyCode, otherwise KeyChar(unicode)
  **
  **      Integer : Meeting     ID# 
  **      Short   : x
  **      Short   : y
  **      Integer : Java based KeyCode or UniCode
  **
  **   KeyUpdateEvent         -  Key injection from controller - Sent to Owner
  **      
  **      Flags   : Bit0 set if PRESS
  **              : Bit1 set if java Virt KeyCode, otherwise KeyChar(unicode)
  **
  **      Integer : Meeting     ID# 
  **      Short   : x
  **      Short   : y
  **      Integer : Java based KeyCode or UniCode
  **      Integer : participant ID#  Who is injecting Key
  **
  **   KeyUpdateError         -  Error injecting Key
  **      
  **      Integer : Meeting     ID# 
  **      Short   : errorcode
  **      String8 : contains error indicator
  **
  **  =======================================================================
  **   MouseUpdate            -  
  **
  **      Flags   : Bit0 set if Button Action, 
  **                Bit1 set if PRESS
  **                Bit2 set if RealMouse (not telepointer)
  **
  **      Integer : Meeting     ID# 
  **      Byte    : button
  **      Short   : x
  **      Short   : y
  **
  **   MouseUpdateEvent       -  
  **
  **      Flags   : Bit0 set if Button Action, 
  **                Bit1 set if PRESS
  **                Bit2 set if RealMouse (not telepointer)
  **
  **      Integer : Meeting     ID# 
  **      Byte    : button
  **      Short   : x
  **      Short   : y
  **      Integer : participant ID#  Who is updating Mouse
  **
  **   MouseUpdateError       -  Returned to MouseUpdate generator if error
  **
  **      Integer : Meeting     ID# 
  **      Short   : errorcode
  **      String8 : contains error indicator
  **      
  **   ======================================================================
  */  
   public static DSMPProto keyUpdate(byte handle, 
                                     boolean isKeyPress,
                                     boolean isKeyCodeOrChar,
                                     int meetingid, 
                                     int x, int y,
                                     int keysym) {
      
      byte flags = (byte)((isKeyPress?1:0) | (isKeyCodeOrChar?2:0));
      DSMPProto ret = new DSMPProto(OP_KEYUPDATE, flags, handle);
      ret.appendInteger(meetingid);
      ret.appendShort(x);
      ret.appendShort(y);
      ret.appendInteger(keysym);
      return ret;
   }
   public static DSMPProto keyUpdateEvent(byte handle, 
                                          boolean isKeyPress,
                                          boolean isKeyCodeOrChar,
                                          int meetingid, 
                                          int participantid, 
                                          int x, int y,
                                          int keysym) {
      
      byte flags = (byte)((isKeyPress?1:0) | (isKeyCodeOrChar?2:0));
      DSMPProto ret = new DSMPProto(OP_KEYUPDATE_EVENT, flags, handle);
      ret.appendInteger(meetingid);
      ret.appendShort(x);
      ret.appendShort(y);
      ret.appendInteger(keysym);
      ret.appendInteger(participantid);
      return ret;
   }
   
   public static DSMPProto keyUpdateError(byte handle, 
                                          int meetingid,
                                          int errorcode, String msg) {
      
      DSMPProto ret = new DSMPProto(OP_KEYUPDATE_ERROR, (byte)0, handle);
      ret.appendInteger(meetingid);
      ret.appendShort(errorcode);
      ret.appendString8(msg!=null?msg:"");
      return ret;
   }
   
   public static DSMPProto mouseUpdate(byte handle, 
                                       boolean isButtonAction,
                                       boolean isButtonPress,
                                       boolean isREALmouse,
                                       int meetingid, 
                                       int x, int y,
                                       int button) {
      
      byte flags = (byte)0;
      if (isButtonAction)  flags |= (byte)0x01;
      if (isButtonPress)   flags |= (byte)0x02;
      if (isREALmouse)     flags |= (byte)0x04;
      DSMPProto ret = new DSMPProto(OP_MOUSEUPDATE, flags, handle);
      ret.appendInteger(meetingid);
      ret.appendByte((byte)button);
      ret.appendShort(x);
      ret.appendShort(y);
      return ret;
   }
   
   public static DSMPProto mouseUpdateEvent(byte handle, 
                                            boolean isButtonAction,
                                            boolean isButtonPress,
                                            boolean isREALmouse,
                                            int meetingid, 
                                            int participantid, 
                                            int x, int y,
                                            int button) {
      
      byte flags = (byte)0;
      if (isButtonAction)  flags |= (byte)0x01;
      if (isButtonPress)   flags |= (byte)0x02;
      if (isREALmouse)     flags |= (byte)0x04;
      DSMPProto ret = new DSMPProto(OP_MOUSEUPDATE_EVENT, flags, handle);
      ret.appendInteger(meetingid);
      ret.appendByte((byte)button);
      ret.appendShort(x);
      ret.appendShort(y);
      ret.appendInteger(participantid);
      return ret;
   }
   
   public static DSMPProto mouseUpdateError(byte handle, 
                                            int meetingid,
                                            int errorcode, String msg) {
      
      DSMPProto ret = new DSMPProto(OP_MOUSEUPDATE_ERROR, (byte)0, handle);
      ret.appendInteger(meetingid);
      ret.appendShort(errorcode);
      ret.appendString8(msg!=null?msg:"");
      return ret;
   }
   
  /*--------------------------------------------------------*\
  ** ImageResize/ImageResizeEvent
  \*--------------------------------------------------------*/
  /*
  **   ======================================================================
  **   ImageResize            -
  **      
  **      Integer : Meeting     ID# 
  **      Short   : x
  **      Short   : y
  **      Short   : w
  **      Short   : h
  **
  **   ImageResizeReply       -  
  **
  **      Flags   : Bit0 set if success
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  **      
  **   ImageResizeEvent       -
  **
  **      Integer : Meeting     ID# 
  **      Short   : x
  **      Short   : y
  **      Short   : w
  **      Short   : h
  **
  **   ======================================================================
  */  
   public static DSMPProto imageResize(byte handle, 
                                       int meetingid, 
                                       int x, int y, int w, int h) {
      
      DSMPProto ret = new DSMPProto(OP_IMAGERESIZE, (byte)0, handle);
      ret.appendInteger(meetingid);
      ret.appendShort(x);
      ret.appendShort(y);
      ret.appendShort(w);
      ret.appendShort(h);
      return ret;
   }
   
   public static DSMPProto imageResizeEvent(byte handle, 
                                            int meetingid, 
                                            int x, int y, int w, int h) {
      
      DSMPProto ret = new DSMPProto(OP_IMAGERESIZE_EVENT, (byte)0, handle);
      ret.appendInteger(meetingid);
      ret.appendShort(x);
      ret.appendShort(y);
      ret.appendShort(w);
      ret.appendShort(h);
      return ret;
   }
   
   public static DSMPProto imageResizeReply(byte handle) {
      
      DSMPProto ret = new DSMPProto(OP_IMAGERESIZE_REPLY, (byte)0x01, handle);
      return ret;
   }
   
  /*--------------------------------------------------------*\
  ** ChatMessage/ChatMessageEvent
  \*--------------------------------------------------------*/
  /*
  **   ======================================================================
  **   ChatMessage        -
  **      
  **      Flags   : Bit0 set indicates unicast
  **
  **      Integer : Meeting     ID# 
  **      Integer : To   participant ID#
  **      String16: message
  **
  **   ChatMessageEvent   -
  **      
  **      Flags   : Bit0 set indicates unicast
  **
  **      Integer : Meeting     ID# 
  **      Integer : from participant ID#
  **      Integer : To   participant ID#
  **      String16: message
  **
  **   ChatMessageReply       -  
  **
  **      Flags   : Bit0 set if success
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  **
  **   ======================================================================
  */  
   public static DSMPProto chatMessage(byte   handle, 
                                       int    meetingid, 
                                       int    toParticipant,
                                       String msg) {
      
      DSMPProto ret = new DSMPProto(OP_CHATMESSAGE, (byte)0x01, handle);
      ret.appendInteger(meetingid);
      ret.appendInteger(toParticipant);
      ret.appendString16(msg!=null?msg:"");
      return ret;
   }
  
   public static DSMPProto chatBroadcast(byte   handle, 
                                         int    meetingid, 
                                         String msg) {
      
      DSMPProto ret = new DSMPProto(OP_CHATMESSAGE, (byte)0x00, handle);
      ret.appendInteger(meetingid);
      ret.appendInteger(0);
      ret.appendString16(msg!=null?msg:"");
      return ret;
   }
  
   public static DSMPProto chatMessageReply(byte handle) {
      DSMPProto ret = new DSMPProto(OP_CHATMESSAGE_REPLY,
                                    (byte)0x01, handle);
      return ret;
   }
   
   public static DSMPProto chatMessageEvent(byte    handle, 
                                            boolean unicast,
                                            int     meetingid, 
                                            int     fromParticipant, 
                                            int     toParticipant,
                                            String  msg) {
      
      byte flags = (byte)(unicast?0x01:0);
      DSMPProto ret = new DSMPProto(OP_CHATMESSAGE_EVENT, flags, handle);
      ret.appendInteger(meetingid);
      ret.appendInteger(fromParticipant);
      ret.appendInteger(toParticipant);
      ret.appendString16(msg!=null?msg:"");
      return ret;
   }
   
  /*--------------------------------------------------------*\
  ** ModifyControl/ControlEvent
  \*--------------------------------------------------------*/
  /*
  **   ======================================================================
  **   ModifyControl          -
  **      
  **      Flags   : Bit0 set indicates give control
  **                Bit1 set indicates Force add if policy allows force
  **
  **      Integer : Meeting     ID# 
  **      Integer : participant ID#
  **
  **   ModifyControlReply     -
  **
  **      Flags   : Bit0 set if success
  **                Bit1 set if add failed because of Overridable policy
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  **
  **   ControlEvent           -  New Controller specified
  **
  **      Integer : Meeting     ID# 
  **      Integer : participant ID#
  **
  **   ======================================================================
  */  
   public static DSMPProto modifyControl(byte    handle, 
                                         boolean addOrRemove,
                                         int     meetingid, 
                                         int     participant) {
      
      byte flags = (byte)(addOrRemove?0x01:0);
      DSMPProto ret = new DSMPProto(OP_MODIFYCONTROL, flags, handle);
      ret.appendInteger(meetingid);
      ret.appendInteger(participant);
      return ret;
   }
  
   public static DSMPProto modifyControl(byte    handle, 
                                         boolean addOrRemove,
                                         boolean forceAdd,
                                         int     meetingid, 
                                         int     participant) {
      
      byte flags = (byte)((addOrRemove?0x01:0)|(forceAdd?0x02:0));
      DSMPProto ret = new DSMPProto(OP_MODIFYCONTROL, flags, handle);
      ret.appendInteger(meetingid);
      ret.appendInteger(participant);
      return ret;
   }
  
   public static DSMPProto modifyControlReply(byte handle) {
      DSMPProto ret = new DSMPProto(OP_MODIFYCONTROL_REPLY,
                                    (byte)0x01, handle);
      return ret;
   }
   
   public static DSMPProto controlEvent(byte    handle, 
                                        int     meetingid, 
                                        int     participant) {
      
      byte flags = (byte)0;
      DSMPProto ret = new DSMPProto(OP_CONTROL_EVENT, flags, handle);
      ret.appendInteger(meetingid);
      ret.appendInteger(participant);
      return ret;
   }
   
  /*--------------------------------------------------------*\
  ** ProtocolRest/FrameEnd/FrameEndEvent/FrozenMode/FrozenModeEvent
  \*--------------------------------------------------------*/
  /*
  **   ======================================================================
  **   ProtocolRest           - Shows a lull in proto sending. Used to allow
  **                            Server to bundle like commands
  **      
  **      Flags   : NA
  **
  **
  **   FrameEnd               - Message intended for clients. Moderator sends
  **                            this message when last frame of a scrape is
  **                            sent
  **
  **      Flags   : NA
  **      Integer : Meeting     ID# 
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  ***
  **
  **   FrameEndEvent           - 
  **
  **      Integer : Meeting     ID# 
  **
  **   FrameEndError           - 
  **
  **      Integer : Meeting     ID# 
  **      Short   : errorcode
  **      String8 : contains error indicator
  **
  **
  **   FrozenMode              - Switches between LIVE and FROZEN updates
  **
  **      Flags   : BIT0 set if frozen
  **      Integer : Meeting     ID# 
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  ***
  **
  **   FrozenModeEvent         - 
  **
  **      Flags   : BIT0 set if frozen
  **      Integer : Meeting     ID# 
  **
  **   FrozenModeError         - 
  **
  **      Integer : Meeting     ID# 
  **      Short   : errorcode
  **      String8 : contains error indicator
  **
  **   ======================================================================
  */  
   public static DSMPProto protocolRest() {
      DSMPProto ret = new DSMPProto(OP_PROTOCOLREST, (byte)0, (byte)0);
      return ret;
   }
  
   public static DSMPProto frameEnd(byte handle, int meetingId) {
      DSMPProto ret = new DSMPProto(OP_FRAMEEND, (byte)0, handle);
      ret.appendInteger(meetingId);
      return ret;
   }
   
   public static DSMPProto frameEndEvent(byte handle, int meetingId) {
      DSMPProto ret = new DSMPProto(OP_FRAMEEND_EVENT, (byte)0, handle);
      ret.appendInteger(meetingId);
      return ret;
   }
   
   public static DSMPProto frameEndError(byte handle, int meetingid,
                                         int errorcode, String msg) {
      
      DSMPProto ret = new DSMPProto(OP_FRAMEEND_ERROR, (byte)0, handle);
      ret.appendInteger(meetingid);
      ret.appendShort(errorcode);
      ret.appendString8(msg!=null?msg:"");
      return ret;
   }
   
   public static DSMPProto frozenMode(int meetingId, boolean frozen) {
      byte flags = frozen?(byte)0x01:(byte)0;
      DSMPProto ret = new DSMPProto(OP_FROZENMODE, flags, (byte)0);
      ret.appendInteger(meetingId);
      return ret;
   }
   
   public static DSMPProto frozenModeEvent(int meetingId, boolean frozen) {
      byte flags = frozen?(byte)0x01:(byte)0;
      DSMPProto ret = new DSMPProto(OP_FROZENMODE_EVENT, flags, (byte)0);
      ret.appendInteger(meetingId);
      return ret;
   }
   
   public static DSMPProto frozenModeError(byte handle, int meetingid,
                                           int errorcode, String msg) {
      
      DSMPProto ret = new DSMPProto(OP_FROZENMODE_ERROR, (byte)0, handle);
      ret.appendInteger(meetingid);
      ret.appendShort(errorcode);
      ret.appendString8(msg!=null?msg:"");
      return ret;
   }
   
  /*--------------------------------------------------------*\
  ** TransferModerator/AlterOwnership/StartShare/StopShare
  ** (and associated replies/events)
  \*--------------------------------------------------------*/
  /*
  **   ======================================================================
  **   TransferModerator      - Allows an Owner to transfer Moderator abilities
  **                            to a participant (or take it away)
  **      
  **      Flags   : NA
  **
  **      Integer : Meeting     ID# 
  **      Integer : To   participant ID#
  **
  **   ModeratorChangeEvent   - Each participant gets this event when there is
  **                            a moderator change
  **      
  **      Flags   : NA
  **
  **      Integer : Meeting     ID# 
  **      Integer : from participant ID#
  **      Integer : To   participant ID#
  **
  **   TransferModeratorReply - 
  **
  **      Flags   : Bit0 set if success
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  **
  **   ======================================================================
  **   AssignOwnership       - Allows an Owner to Add/Remove Ownership status
  **                           to another participant
  **      
  **      Flags   : BIT0 set if Assigning ownership, otherwise, removing 
  **
  **      Integer : Meeting     ID# 
  **      Integer : participant ID#
  **
  **   OwnerChangeEvent      - Each participant gets this event when there is
  **                           a change in ownership status of a participant
  **      
  **      Flags   : BIT0 set if Assigning Ownership, otherwise, removing
  **
  **      Integer : Meeting     ID# 
  **      Integer : participant ID#
  **
  **   AssignOwnershipReply - 
  **
  **      Flags   : Bit0 set if success
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  **
  **   ======================================================================
  **   StartShare             - Allows a moderator to indicate that a shared
  **                            content stream is starting.
  **
  **      Flags   : NA
  **
  **      Integer : Meeting     ID# 
  **
  **   StartShareEvent        - Sent to all participants when content sharing
  **                            for a meeting is started. 
  **      Flags   : NA
  **
  **      Integer : Meeting     ID# 
  **
  **   StartShareReply - 
  **
  **      Flags   : Bit0 set if success
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  **
  **   ======================================================================
  **   StopShare              - Allows a moderator to indicate that the shared
  **                            content stream is stopping, and cached content
  **                            should be purged.
  **
  **      Flags   : NA
  **
  **      Integer : Meeting     ID# 
  **
  **   StopShareEvent         - Sent to all participants when content sharing
  **                            for a meeting is stopped. 
  **      Flags   : NA
  **
  **      Integer : Meeting     ID# 
  **
  **   StopShareReply - 
  **
  **      Flags   : Bit0 set if success
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  **
  **   ======================================================================
  */  
   public static DSMPProto transferModerator(byte handle, 
                                             int meetingId, int partId) {
      DSMPProto ret = new DSMPProto(OP_TRANSFERMODERATOR, (byte)0, handle);
      ret.appendInteger(meetingId);
      ret.appendInteger(partId);
      return ret;
   }
   public static DSMPProto moderatorChangeEvent(byte handle, 
                                                int meetingId, 
                                                int fromPartId,
                                                int toPartId) {
      DSMPProto ret = new DSMPProto(OP_MODERATORCHANGE_EVENT, 
                                    (byte)0, handle);
      ret.appendInteger(meetingId);
      ret.appendInteger(fromPartId);
      ret.appendInteger(toPartId);
      return ret;
   }
   public static DSMPProto transferModeratorReply(byte handle) {
      DSMPProto ret = new DSMPProto(OP_TRANSFERMODERATOR_REPLY, 
                                    (byte)1, handle);
      return ret;
   }
   public static DSMPProto assignOwnership(byte handle, int meetingId, 
                                           int partId, boolean onOrOff) {
      DSMPProto ret = new DSMPProto(OP_ASSIGNOWNERSHIP, 
                                    (byte)(onOrOff?1:0), handle);
      ret.appendInteger(meetingId);
      ret.appendInteger(partId);
      return ret;
   }
   public static DSMPProto ownershipChangeEvent(byte handle,
                                                int meetingId, 
                                                int partId,
                                                boolean onOrOff) {
      DSMPProto ret = new DSMPProto(OP_OWNERCHANGE_EVENT, 
                                    (byte)(onOrOff?1:0), handle);
      ret.appendInteger(meetingId);
      ret.appendInteger(partId);
      return ret;
   }
   public static DSMPProto assignOwnershipReply(byte handle) {
      DSMPProto ret = new DSMPProto(OP_ASSIGNOWNERSHIP_REPLY, 
                                    (byte)1, handle);
      return ret;
   }
   public static DSMPProto startSharing(byte handle, int meetingId) {
      DSMPProto ret = new DSMPProto(OP_STARTSHARE, (byte)0, handle);
      ret.appendInteger(meetingId);
      return ret;
   }
   public static DSMPProto startShareEvent(byte handle, int meetingId) {
      DSMPProto ret = new DSMPProto(OP_STARTSHARE_EVENT, 
                                    (byte)0, handle);
      ret.appendInteger(meetingId);
      return ret;
   }
   public static DSMPProto startSharingReply(byte handle) {
      DSMPProto ret = new DSMPProto(OP_STARTSHARE_REPLY, 
                                    (byte)1, handle);
      return ret;
   }
   public static DSMPProto stopSharing(byte handle, int meetingId) {
      DSMPProto ret = new DSMPProto(OP_STOPSHARE, (byte)0, handle);
      ret.appendInteger(meetingId);
      return ret;
   }
   public static DSMPProto stopShareEvent(byte handle, int meetingId) {
      DSMPProto ret = new DSMPProto(OP_STOPSHARE_EVENT, 
                                    (byte)0, handle);
      ret.appendInteger(meetingId);
      return ret;
   }
   public static DSMPProto stopSharingReply(byte handle) {
      DSMPProto ret = new DSMPProto(OP_STOPSHARE_REPLY, 
                                    (byte)1, handle);
      return ret;
   }
   
   
  // 
  // Misc routine to runlength Encode  Assume we can drop high order byte
  //
  // Take data 4 bytes at a time
   final static int BYTES_AT_A_TIME = 4;
   public static int runlengthEncode(byte outpb[], int oofs, 
                                     byte inpb[], int iofs, int len) {

      int ret = 0;
   
     // if len is NOT % 4, return error
      if ((len & 0x3) != 0) return -1;
      if (len == 0)  return 0;
      
      int startp      = 0;
      
      int         inp = iofs;
      int        outp = oofs;
      int     stopinp = inp+len;
      int        comp = 0;    // Pointer to compare int
      int          lp = 0;    // Pointer to start of inp for copy
      int    sameDiff = 0;    // Processing DIFF < 0 < SAME
      int         num = 0;
      boolean flushit = false;
      
      while(ret < len && inp < stopinp) {
         
         if (num == 0) {
            lp   = inp;
            comp = inp;
            inp += BYTES_AT_A_TIME;
            num++;
         } else {
            
            boolean compvalue = true;
            for(int i=0; i < BYTES_AT_A_TIME; i++) {
               if (inpb[inp+i] != inpb[comp+i]) {
                  compvalue = false;
                  break;
               }
            }
            
            if        (sameDiff > 0) {
               if (compvalue) {
                  num++;
                  inp += BYTES_AT_A_TIME;
               } else {
                  flushit = true;
               }
            } else if (sameDiff < 0) {
               if (compvalue) {
                  flushit=true;   // Flush it out, 
                  num--;       // Take AWAY the last one
                  inp -= BYTES_AT_A_TIME;
               } else {
                  num++;
                  comp = inp;
                  inp += BYTES_AT_A_TIME;
               }
            } else {
               comp = inp;
               num++;
               inp += BYTES_AT_A_TIME;
               sameDiff = (!compvalue)?-1:1;
            }
         }
         
        // Flush
         if (flushit || num >= 128 || inp == stopinp) {
            flushit = false;
            if (num != 0) {
               int cnt = 0;
               if (sameDiff >= 0) {
                  cnt  = 1-num;
                  num = 1;
               } else {
                  cnt = num-1;
               }
               
               ret += 1 + (3*num);
               if (ret >= len) break;
               outpb[outp++] = (byte)cnt;
               
//            fprintf(stderr, "op[%d] [%d]", cnt, num);
               
               while(num-- != 0) {
                  
//               fprintf(stderr, " 0x%06.6x", t & 0xffffff);
                  
                  
                 // Skip high order byte ... its always 0xff
                 // AND we are already assumed Big endian
                 //outpb[outp++] = inpb[lp+0];
                  outpb[outp++] = inpb[lp+1];
                  outpb[outp++] = inpb[lp+2];
                  outpb[outp++] = inpb[lp+3];
                  
                  lp += 4;
               }
//            fprintf(stderr, "\n");
               
               num = 0;
               sameDiff = 0;
            }         
         }      
      }
      if (inp < stopinp || ret > len) ret = -1;
      
//   if (ret > 0) {
//      fprintf(stderr, "RLE: %d -> %d\n", len, ret);
//   }
      
      return ret;
   }
   
  // Misc routine to runlength Decode a buffer
   static public int[] runlengthDecode(byte arr[], int ret[], int ofs, 
                                       int len, int unencodedLen) {
      
      if (ret == null || ret.length < unencodedLen) 
         ret = new int[unencodedLen];
      
//      System.out.println("---Decode--- len=" + len + " ulen=" +unencodedLen);
      int i=ofs;
      int o=0;
      len += ofs;
      while(i < len) {
         byte l = arr[i++];
         if (l < 0) {
            int realL = ((int)(-l)) + 1;
            int v = 0xff000000 | ((((int)arr[i++]) & 0xff) << 16);
            v |= ((((int)arr[i++]) & 0xff) << 8);
            v |= ((((int)arr[i++]) & 0xff));
//            System.out.println("OP = Same[" + l + "] [" + realL + "] Val[" + v + "]");
            while(realL-- > 0) {
               ret[o++] = v;
            }
         } else {
            int realL = ((int)(l)) + 1;
//            System.out.println("OP = DIFF[" + l + "] [" + realL + "]");
            while(realL-- > 0) {
               int v = 0xff000000 | ((((int)arr[i++]) & 0xff) << 16);
               v |= ((((int)arr[i++]) & 0xff) << 8);
               v |= ((((int)arr[i++]) & 0xff));
//               System.out.println("\t" + o + ": [" + v + "]");
               ret[o++] = v;
            }
         }
      }
      return ret;
   }
   
  /* NOTE ... THIS IS COPY CODE FROM DropboxGenerator!!!!! */
  /*--------------------------------------------------------*\
  ** CreateGroup/DeleteGroup/QueryGroups/ModifyGroupAcl
  ** ModifyGroupAttribute
  \*--------------------------------------------------------*/
  /*
  **   ======================================================================
  **   CreateGroup            -  Create a new group
  **      
  **      flags   : None
  **               
  **      String16: groupname
  **      byte    : visibility
  **      byte    : listability
  **
  **   CreateGroupReply       - 
  **
  **      flags   : Bit0 set if success
  **
  **  -- errorcase
  **
  **      Short   : errorcode 
  **      String8 : contains error indicator
  **
  **   ======================================================================
  **   DeleteGroup            -  Delete the specified group
  **      
  **      flags   : None
  **               
  **      String16: groupname
  **
  **   DeletePackageReply     - 
  **
  **      flags   : Bit0 set if success
  **
  **  -- errorcase
  **
  **      Short   : errorcode
  **      String8 : contains error indicator
  **
  **   ======================================================================
  **   ModifyGroupACL         -  Add or remove id to a group or group access
  **      
  **      flags   : Bit0 set if modifying member list, clear if access list
  **                Bit1 set if adding, clear if removing
  **               
  **      String16: groupname
  **      string16: username
  **
  **   ModifyGroupACLReply    - 
  **
  **      flags   : Bit0 set if success
  **
  **  -- errorcase
  **
  **      Short   : errorcode
  **      String8 : contains error indicator
  **   ======================================================================
  **   ModifyGroupAttribute   -  Modify the visibility/listability attributes 
  **                             of a group
  **      
  **      flags   : None
  **                
  **      String16: groupname
  **      Byte    : visibility value   (non-zero if changing value) GROUP_SCOPE
  **      Byte    : listability value  (non-zero if changing value) GROUP_SCOPE
  **
  **   ModifyGroupAttributeReply -
  **
  **      flags   : Bit0 set if success
  **
  **  -- errorcase
  **
  **      Short   : errorcode
  **      String8 : contains error indicator
  **   ======================================================================
  **   QueryGroups            -  Query groups and their contents
  **      
  **      flags   : Bit0 set if regex search, otherwise direct match
  **                Bit1 set if want member info
  **                Bit2 set if want access info
  **                
  **      String16: search string. If 0 length string, then matches all
  **
  **   QueryGroupsReply       -
  **
  **      flags   : Bit0 set if success
  **                Bit1 set if member info included
  **                Bit2 set if access info included
  **
  **      Integer3: numgroups
  **
  **    x String16: groupname
  **    x String16: owner
  **    x String16: ownercompany
  **    x Long    : group created (MS since 70 GMT)
  **    x Byte    : visibility  (GROUP_SCOPE, NONE if not owner or access list)
  **    x Byte    : listability (GROUP_SCOPE, NONE if not owner or access list)
  **    x Byte    : flags       (BIT0 set if nummembers reflects reality)
  **                            (BIT1 set if numaccess is reflects reality)
  **    x Integer3: nummembers (y)
  ** 
  **    y String16: groupmember
  ** 
  **    x Integer3: numaccess  (z)
  **    z String16: accessmember
  **
  ** Note: if visibility is NONE, then you don't have access to 
  ** 
  **  -- errorcase
  **
  **      Short   : errorcode
  **      String8 : contains error indicator
  */
    
   public static DSMPProto createGroup(byte handle, 
                                       String groupname, 
                                       byte   visibility,
                                       byte   listability) {
                                             
      DSMPProto ret = new DSMPProto(OP_CREATE_GROUP, (byte)0, 
                                        handle);
      ret.appendString16(groupname);
      ret.appendByte(visibility);
      ret.appendByte(listability);
      
      return ret;
   }
   public static DSMPProto createGroupReply(byte handle) {
      DSMPProto ret = new DSMPProto(OP_CREATE_GROUP_REPLY, (byte)1, 
                                    handle);
      return ret;
   }
   public static DSMPProto deleteGroup(byte handle, String groupname) {
      DSMPProto ret = new DSMPProto(OP_DELETE_GROUP, (byte)0, 
                                    handle);
      ret.appendString16(groupname);
      return ret;
   }
   public static DSMPProto deleteGroupReply(byte handle) {
      DSMPProto ret = new DSMPProto(OP_DELETE_GROUP_REPLY, (byte)1, 
                                    handle);
      return ret;
   }
   public static DSMPProto addGroupMemberAcl(byte handle, 
                                                 String groupname, 
                                                 String user) {
      DSMPProto ret = new DSMPProto(OP_MODIFY_GROUP_ACL, (byte)0x3, 
                                    handle);
      ret.appendString16(groupname);
      ret.appendString16(user);
      return ret;
   }
   public static DSMPProto removeGroupMemberAcl(byte handle, 
                                                    String groupname,
                                                    String user) {
      DSMPProto ret = new DSMPProto(OP_MODIFY_GROUP_ACL, (byte)0x1, 
                                    handle);
      ret.appendString16(groupname);
      ret.appendString16(user);
      return ret;
   }
   public static DSMPProto addGroupAccessAcl(byte handle,
                                                 String groupname, 
                                                 String user) {
      DSMPProto ret = new DSMPProto(OP_MODIFY_GROUP_ACL, (byte)0x2, 
                                    handle);
      ret.appendString16(groupname);
      ret.appendString16(user);
      return ret;
   }
   public static DSMPProto removeGroupAccessAcl(byte handle,
                                                    String groupname,
                                                    String user) {
      DSMPProto ret = new DSMPProto(OP_MODIFY_GROUP_ACL, (byte)0x0, 
                                    handle);
      ret.appendString16(groupname);
      ret.appendString16(user);
      return ret;
   }
   public static DSMPProto modifyGroupAclReply(byte handle) {
      DSMPProto ret = new DSMPProto(OP_MODIFY_GROUP_ACL_REPLY, 
                                    (byte)1, handle);
      return ret;
   }
   
   public static DSMPProto modifyGroupAttributes(byte handle, 
                                                 String groupname,
                                                 byte visibility, 
                                                 byte listability) {
      DSMPProto ret = new DSMPProto(OP_MODIFY_GROUP_ATTRIBUTES, 
                                    (byte)0, handle);
      ret.appendString16(groupname);
      ret.appendByte(visibility);
      ret.appendByte(listability);
      return ret;
   }
   
   public static DSMPProto modifyGroupAttributesReply(byte handle) {
      DSMPProto ret = new DSMPProto(OP_MODIFY_GROUP_ATTRIBUTES_REPLY,
                                    (byte)1, handle);
      return ret;
   }
   
   public static DSMPProto queryGroups(byte handle, 
                                       boolean regex, 
                                       boolean wantMembers,
                                       boolean wantAccess,
                                       String groupname) {
      byte flags = (byte)((regex?1:0)|(wantMembers?2:0)|(wantAccess?4:0));
      DSMPProto ret = new DSMPProto(OP_QUERY_GROUPS, (byte)flags,
                                    handle);
      ret.appendString16(groupname);
      return ret;
   }
   
  // Note, this is just the starting point, caller must add all guts!
   public static DSMPProto queryGroupsReply(byte handle) {
      DSMPProto ret = new DSMPProto(OP_QUERY_GROUPS_REPLY, (byte)1, 
                                    handle);
      return ret;
   }
   
  /*--------------------------------------------------------*\
  ** PlaceCall/AcceptCall (and associated replies/events)
  \*--------------------------------------------------------*/
  /*
  **   ======================================================================
  **   PlaceCall              - Allows a user to request to join a meeting 
  **                            already in progress. This can be used w/ or 
  **                            w/o a meetingid. If no meeting ID, then its 
  **                            done by user. 
  **      
  **      Flags   : Bit0 is ON if meetingID is to be used, off for UserToCall
  **
  **      Integer : Meeting     ID# 
  **      String8 : UserToCall
  **      String8 : Meeting Password
  **
  **   PlaceCallEvent         - Event sent to Meeting owner who is the target
  **                            of a call. When call is to meetingid, will go
  **                            to all Owners of the meeting. When its to a
  **                            specific user name, will go to all instances
  **                            of that user existing with active meetings.
  **      
  **      Flags   : NA
  **
  **      Integer : Meeting     ID# 
  **      Integer : From participant ID#
  **      String8 : From Userid
  **      String8 : From Company
  **      String8 : salt
  **
  **   PlaceCallReply - 
  **
  **      Flags   : Bit0 set if success
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  **
  **   ======================================================================
  **   AcceptCall             - Owner of a meeting can accept a placed call
  **                            made by another user. This will cause caller
  **                            to be added to the invite list (if he is not
  **                            there already), and generate an AcceptCallEvent
  **                            for the caller.  The salt must match that used
  **                            for the original call.
  **      
  **      Flags   : NA
  **  
  **      Integer : Meeting         ID# 
  **      Integer : calling         ID# 
  **      String8 : salt     
  **
  **   AcceptCallEvent        - Each participant gets this event when there is
  **                            a change in ownership status of a participant
  **      
  **      Flags   : Bit0 set if call worked cause already invited or PW correct
  **
  **      Integer : Meeting         ID# 
  **      Integer : accepting owner ID#
  **      String8 : accepting owner userid
  **      String8 : accepting owner company
  **
  **   AcceptCallReply        - 
  **
  **      Flags   : Bit0 set if success
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  */  
   public static DSMPProto placeCall(byte handle, String userid,
                                     String password) {
      DSMPProto ret = new DSMPProto(OP_PLACECALL, (byte)0, handle);
      ret.appendInteger(0);
      ret.appendString8(userid);
      ret.appendString8(password);
      return ret;
   }
   public static DSMPProto placeCall(byte handle, int meetingId,
                                     String password) {
      DSMPProto ret = new DSMPProto(OP_PLACECALL, (byte)1, handle);
      ret.appendInteger(meetingId);
      ret.appendString8(null);
      ret.appendString8(password);
      return ret;
   }
   public static DSMPProto placeCallEvent(byte handle, 
                                          int meetingId, 
                                          int fromPartId,
                                          String fromPartUserid,
                                          String fromPartCompany,
                                          String salt) {
      DSMPProto ret = new DSMPProto(OP_PLACECALL_EVENT,
                                    (byte)0, handle);
      ret.appendInteger(meetingId);
      ret.appendInteger(fromPartId);
      ret.appendString8(fromPartUserid);
      ret.appendString8(fromPartCompany);
      ret.appendString8(salt);
      return ret;
   }
   public static DSMPProto placeCallReply(byte handle) {
      DSMPProto ret = new DSMPProto(OP_PLACECALL_REPLY, 
                                    (byte)1, handle);
      return ret;
   }
   
   public static DSMPProto acceptCall(byte handle, int meetingid,
                                      int callingid, String salt) {
      DSMPProto ret = new DSMPProto(OP_ACCEPTCALL, (byte)0, handle);
      ret.appendInteger(meetingid);
      ret.appendInteger(callingid);
      ret.appendString8(salt);
      return ret;
   }
   public static DSMPProto acceptCallEvent(byte handle, 
                                           boolean alreadyInvited,
                                           int meetingId, 
                                           int acceptingPartId,
                                           String acceptingPartUserid,
                                           String acceptingPartCompany) {
      DSMPProto ret = new DSMPProto(OP_ACCEPTCALL_EVENT,
                                    (byte)(byte)(alreadyInvited?1:0), handle);
      ret.appendInteger(meetingId);
      ret.appendInteger(acceptingPartId);
      ret.appendString8(acceptingPartUserid);
      ret.appendString8(acceptingPartCompany);
      return ret;
   }
   public static DSMPProto acceptCallReply(byte handle) {
      DSMPProto ret = new DSMPProto(OP_ACCEPTCALL_REPLY, 
                                    (byte)1, handle);
      return ret;
   }
   
  /*--------------------------------------------------------*\
  ** QueryMeetingOptions/SetMeetingOptions/MeetingOptionEvent
  \*--------------------------------------------------------*/
  
  /*
  **   ======================================================================
  **   QueryMeetingOptions    -  Query all options associated with a meeting.
  **                             The options returned are scoped to that 
  **                             which the caller is allowed to see (certain
  **                             options are for Meeting Owners only). If 
  **                             option_to_query is specified as non-empty 
  **                             string, return JUST that value
  **      
  **      Flags   : BIT0 set if querying a specific option, otherwise ALL
  **
  **      Integer : Meeting ID#
  **      String8 : option_to_query (can be null)
  **
  **
  **   QueryMeetingOptionsReply -
  **
  **      Flags   : Bit0 set if success
  **
  **      Integer : Meeting ID#
  **      Short   : numOptions
  **
  **    x String8 : optname
  **    x String16: optval
  **
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  **   
  **   ======================================================================
  **   SetMeetingOptions      -  Sets the specified meeting options
  **      
  **      Flags   : NA
  **
  **      Integer : Meeting ID#
  **      Short   : numOptions
  **
  **    x String8 : optname
  **    x String16: optval
  **
  **
  **   MeetingOptionEvent     - Event sent to all participants of a meeting
  **                            who have visibility to the option which is 
  **                            changing.
  **
  **      Flags   : NA
  **
  **      Integer : Meeting     ID# 
  **      String8 : OptionName
  **      String16: OptionValue
  **
  **
  **   SetMeetingOptionsReply -
  **
  **      Flags   : Bit0 set if success
  **
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  **   
  */
   public static DSMPProto queryMeetingOptions(byte handle, int meetingid) {
      DSMPProto ret = new DSMPProto(OP_QUERY_MEETING_OPTIONS, (byte)0, handle);
      ret.appendInteger(meetingid);
      ret.appendString8(null);
      return ret;
   }
   public static DSMPProto queryMeetingOptions(byte handle, int meetingid,
                                               String specificOpt) {
      DSMPProto ret = new DSMPProto(OP_QUERY_MEETING_OPTIONS, 
                                    (byte)(specificOpt != null?1:0), handle);
      ret.appendInteger(meetingid);
      ret.appendString8(specificOpt);
      return ret;
   }
   public static DSMPProto queryMeetingOptionsReply(byte handle, int meetingid,
                                                   Hashtable options) {
      DSMPProto ret = new DSMPProto(OP_QUERY_MEETING_OPTIONS_REPLY, 
                                    (byte)1, handle);
      
      ret.appendInteger(meetingid);
      
      if (options != null) {
         ret.appendShort((short)options.size());
         Enumeration keys = options.keys();
         while(keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            String val = (String)options.get(key);
            ret.appendString8(key);
            ret.appendString16(val);
         }
      } else {
         ret.appendShort((short)0);
      }
      return ret;
   }
   
   public static DSMPProto setMeetingOption(byte handle, int meetingid,
                                            String opt, String val) {
      DSMPProto ret = new DSMPProto(OP_SET_MEETING_OPTIONS, (byte)0, handle);
      ret.appendInteger(meetingid);
      ret.appendShort(1);
      ret.appendString8(opt);
      ret.appendString16(val);
      return ret;
   }
   public static DSMPProto setMeetingOptions(byte handle, int meetingid,
                                             Hashtable options) {
      DSMPProto ret = new DSMPProto(OP_SET_MEETING_OPTIONS, 
                                    (byte)0, handle);
      
      ret.appendInteger(meetingid);
      
      if (options != null) {
         ret.appendShort((short)options.size());
         Enumeration keys = options.keys();
         while(keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            String val = (String)options.get(key);
            ret.appendString8(key);
            ret.appendString16(val);
         }
      } else {
         ret.appendShort((short)0);
      }
      return ret;
   }
   
   public static DSMPProto meetingOptionEvent(byte handle, int meetingId, 
                                              String opt, String val) {
      DSMPProto ret = new DSMPProto(OP_MEETING_OPTION_EVENT, (byte)0, handle);
      ret.appendInteger(meetingId);
      ret.appendString8(opt);
      ret.appendString16(val);
      return ret;
   }
   public static DSMPProto setMeetingOptionsReply(byte handle) {
      DSMPProto ret = new DSMPProto(OP_SET_MEETING_OPTIONS_REPLY,
                                    (byte)1, handle);
      return ret;
   }
   
  /*--------------------------------------------------------*\
  ** GetMeetingURL
  \*--------------------------------------------------------*/
  
  /*
  **   ======================================================================
  **   GetMeetingURL          -  Returns a URL which can be used to launch
  **                             the web conference software, and to attend
  **                             the specified meeting 
  **      
  **      Flags   : Bit0 - Set if including hashed password
  **                Bit1 - Set if meetingID should be used, otherwise, call 
  **                        by user
  **                Bit2 - Set if should send EMAILS to all invitees NOT
  **                        already present in the meeting
  **
  **      Integer : Meeting ID#
  **
  **
  **   GetMeetingURLReply     -
  **
  **      Flags   : Bit0 set if success
  **
  **      Integer : Meeting ID#
  **      String16: meetingURL
  **
  **  -- errorcase
  **
  **      Short   : errorcode            
  **      String8 : contains error indicator
  **   
  */
   public static DSMPProto getMeetingURL(byte handle, 
                                         boolean includepw,
                                         boolean includemeetingid,
                                         boolean sendemails,
                                         int meetingid) {
      DSMPProto ret = new DSMPProto(OP_GET_MEETING_URL, 
                                    (byte)((includepw?1:0)|
                                           (includemeetingid?2:0)|
                                           (sendemails?4:0)), 
                                    handle);
      ret.appendInteger(meetingid);
      return ret;
   }
   
   public static DSMPProto getMeetingURLReply(byte handle, int meetingid,
                                              String URL) {
      DSMPProto ret = new DSMPProto(OP_GET_MEETING_URL_REPLY, 
                                    (byte)1, handle);
      
      ret.appendInteger(meetingid);
      ret.appendString16(URL);
      
      return ret;
   }
}
