package oem.edge.ed.odc.meeting.common;

import  oem.edge.ed.odc.dsmp.common.*;

import java.util.*;
import java.util.zip.*;

public class DSMPSTDispatchBase extends DSMPDispatchBase {

   public void setDebug(boolean v) {
      super.setDebug(v);
      DSMPGenerator.setDebug(v);
   }
   
   protected boolean toneitdown = false;
   public void setToneItDown(boolean v) {
      toneitdown = v;
      if (pdbg != null) {
         ((DSMPSTDispatchBase)pdbg).setToneItDown(toneitdown);
      }
   }
   public boolean getToneItDown() {
      return toneitdown;
   }
   
  /* -------------------------------------------------------*\
  ** Shutdown
  \* -------------------------------------------------------*/
   public void fireShutdownEvent(DSMPHandler h) {
      System.out.println("-----fireShutdownEvent connectionid = " + 
                         h.getHandlerId());
   }
   public void fireShutdownEvent(DSMPBaseHandler h) {
      fireShutdownEvent((DSMPHandler)h);
   }

  /* -------------------------------------------------------*\
  ** Commands
  \* --------- ----------------------------------------------*/
   public void fireLoginCommandToken(DSMPHandler h, byte flags, 
                                     byte handle, String token) {
      if (printdebug && dodebug) {
         System.out.println("-----fireLoginCommandToken: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\ttoken [" + token + "]");
      }
   }
   public void fireLoginCommandUserPW(DSMPHandler h, byte flags, 
                                      byte handle, String user, String pw) {
      if (printdebug && dodebug) {
         System.out.println("-----fireLoginCommandUserPW: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tuser [" + user + "]");
         System.out.println("\tpw [" + pw + "]");
      }
   }
   public void fireLogoutCommand(DSMPHandler h, byte flags, byte handle) {
      if (printdebug && dodebug) {
         System.out.println("-----fireLogoutCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
   }
   public void fireStartMeetingCommand(DSMPHandler h, 
                                       byte flags, byte handle, 
                                       String title, String pw, 
                                       String classification) {
      if (printdebug && dodebug) {
         System.out.println("-----fireStartMeetingCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\ttitle [" + title + "]");
         System.out.println("\tpw [" + pw + "]");
         System.out.println("\tclassification [" + classification + "]");
      }
   }
   public void fireLeaveMeetingCommand(DSMPHandler h, byte flags, 
                                       byte handle, int meetingid) {
      if (printdebug && dodebug) {
         System.out.println("-----fireLeaveMeetingCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid [" + meetingid + "]");
      }
   }
   public void fireEndMeetingCommand(DSMPHandler h, byte flags, 
                                     byte handle, int meetingid) {
      if (printdebug && dodebug) {
         System.out.println("-----fireEndMeetingCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid [" + meetingid + "]");
      }
   }
   public void fireCreateInvitationCommand(DSMPHandler h, byte flags, 
                                           byte handle, int meetingid,
                                           byte inviteType, String name) {
      if (printdebug && dodebug) {
         System.out.println("-----fireCreateInvitationCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid [" + meetingid + "]");
         System.out.println("\tinviteType [" + inviteType + "]");
         System.out.println("\tname [" + name + "]");
      }
   }
   public void fireJoinMeetingCommand(DSMPHandler h, byte flags, 
                                      byte handle, int meetingid) {
      if (printdebug && dodebug) {
         System.out.println("-----fireJoinMeetingCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid [" + meetingid + "]");
      }
   }
   public void fireDropInviteeCommand(DSMPHandler h, 
                                      byte flags, byte handle, 
                                      int meetingid,
                                      boolean dropInvite, int inviteid,
                                      boolean dropPart,   int participantid) {
      if (printdebug && dodebug) {
         System.out.println("-----fireDropInviteeCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tdropInvite [" + dropInvite + "]");
         System.out.println("\tinviteid [" + inviteid + "]");
         System.out.println("\tdropPart [" + dropPart + "]");
         System.out.println("\tparticipantid [" + participantid + "]");
      }
   }
   public void fireImageResizeCommand(DSMPHandler hand, byte flags, 
                                      byte handle, int meetingid,
                                      short x, short y, short w, short h) {
      if (printdebug && dodebug) {
         System.out.println("-----fireImageResizeCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid [" + meetingid + "]");
         System.out.println("\tx [" + x + "]");
         System.out.println("\ty [" + y + "]");
         System.out.println("\tw [" + w + "]");
         System.out.println("\th [" + h + "]");
      }
   }
   public void fireChatMessageCommand(DSMPHandler h, 
                                      byte flags, byte handle, 
                                      int meetingid, int toid, String msg,
                                      boolean unicast) {
      if (printdebug && dodebug) {
         System.out.println("-----fireChatMessageCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid [" + meetingid + "]");
         System.out.println("\ttoid [" + toid + "]");
         System.out.println("\tmsg [" + msg + "]");
         System.out.println("\tunicast [" + unicast + "]");
      }
   }
   public void fireModifyControlCommand(DSMPHandler h, 
                                        byte flags, byte handle, 
                                        int meetingid,
                                        int participantid, 
                                        boolean addRemove, 
                                        boolean forceAdd) {
      if (printdebug && dodebug) {
         System.out.println("-----fireModifyControlCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid [" + meetingid + "]");
         System.out.println("\tparticipantid [" + participantid + "]");
         System.out.println("\taddRemove [" + addRemove + "]");
         System.out.println("\tforceAdd [" + forceAdd + "]");
      }
   }
   public void fireGetAllMeetingsCommand(DSMPHandler h, byte flags,
                                         byte handle) {
      if (printdebug && dodebug) {
         System.out.println("-----fireGetAllMeetingsCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
   }
   public void fireMouseUpdateCommand(DSMPHandler h, 
                                      byte flags, byte handle, 
                                      int meetingid,
                                      boolean buttonEvent, 
                                      boolean pressRelease,
                                      boolean realMotion,
                                      byte button, short x, short y) {
      if (printdebug && dodebug && !toneitdown) {
         System.out.println("-----fireMouseUpdateCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid [" + meetingid + "]");
         System.out.println("\tbuttonEvent [" + buttonEvent + "]");
         System.out.println("\tpressRelease [" + pressRelease + "]");
         System.out.println("\trealMotion [" + realMotion + "]");
         System.out.println("\tbutton [" + button + "]");
         System.out.println("\tx [" + x + "]");
         System.out.println("\ty [" + y + "]");
      }
   }
   public void fireKeyUpdateCommand(DSMPHandler h, 
                                    byte flags, byte handle, 
                                    int meetingid,
                                    boolean pressRelease,
                                    boolean keyCodeOrChar,
                                    short x, short y, 
                                    int javakeysym) {
      if (printdebug && dodebug) {
         System.out.println("-----fireKeyUpdateCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid [" + meetingid + "]");
         System.out.println("\tpressRelease [" + pressRelease + "]");
         System.out.println("\tkeyCodeOrKeyChar [" + keyCodeOrChar + "]");
         System.out.println("\tx [" + x + "]");
         System.out.println("\ty [" + y + "]");
         System.out.println("\tjavakeysym [" + javakeysym + "]");
      }
   }
   public void fireFrameUpdateCommand(DSMPHandler hand, byte flags, 
                                      byte handle, int meetingid,
                                      short x, short y, short w, short h,
                                      boolean compressed, byte buf[],
                                      int ofs, int len) {
      if (printdebug && dodebug && !toneitdown) {
         System.out.println("-----fireFrameUpdateCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid [" + meetingid + "]");
         System.out.println("\tx [" + x + "]");
         System.out.println("\ty [" + y + "]");
         System.out.println("\tw [" + w + "]");
         System.out.println("\th [" + h + "]");
         System.out.println("\tcompressed [" + compressed + "]");
         System.out.println("\tbuflen [" + buf.length + "]");
         System.out.println("\tlen [" + len + "]");
         System.out.println("\tofs [" + ofs + "]");
      }
   }
   public void fireMultiFrameUpdateCommand(DSMPHandler hand, byte flags, 
                                           byte handle, boolean compressed,
                                           int meetingid, 
                                           DSMPBaseProto proto) {
      if (printdebug && dodebug && !toneitdown) {
         System.out.println("-----fireMultiFrameUpdateCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid [" + meetingid + "]");
         System.out.println("\tcompressed [" + compressed + "]");
         System.out.println("\tprotolen [" + proto.getDataLength() + "]");
      }
   }
   public void fireMultiFrameCollectionCommand(DSMPHandler hand, 
                                               int meetingid, Vector v) 
                                              throws InvalidProtocolException {
      if (printdebug && dodebug && !toneitdown) {
         System.out.println("-----fireMultiFrameCollectionCommand:");
         System.out.println("\tmeetingid [" + meetingid + "]");
         System.out.println("\tv.size [" + v.size());
         System.out.println("\t----------->>>>>");
      }
      Enumeration enum = v.elements();
      while(enum.hasMoreElements()) {
         DSMPBaseProto p = (DSMPBaseProto)enum.nextElement();
         dispatchProtocolI(p, hand, true);
      }
   }
   
   public void fireProtocolRest(DSMPHandler hand, 
                                byte flags, byte handle) {
      if (printdebug && dodebug && !toneitdown) {
         System.out.println("-----fireProtocolRestCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
   }
   public void fireFrameEnd(DSMPHandler hand, byte flags, byte handle,
                            int meetingid) {
      if (printdebug && dodebug && !toneitdown) {
         System.out.println("-----fireFrameEndCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid [" + meetingid + "]");
      }
   }
   public void fireFrozenMode(DSMPHandler hand, byte flags, byte handle,
                              int meetingid, boolean frozen) {
      if (printdebug && dodebug) {
         System.out.println("-----fireFrozenModeCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid [" + meetingid + "]");
         System.out.println("\tfrozen [" + frozen + "]");
      }
   }
   
   public void fireTransferModeratorCommand(DSMPHandler h, 
                                            byte flags, byte handle, 
                                            int meetingid,
                                            int partid) {
      if (printdebug && dodebug) {
         System.out.println("-----fireTransferModeratorCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid [" + meetingid + "]");
         System.out.println("\tpartid    [" + partid + "]");
      }
   }
   public void fireAssignOwnershipCommand(DSMPHandler h, 
                                          byte flags, byte handle, 
                                          int meetingid, int partid, 
                                          boolean onOrOff) {
      if (printdebug && dodebug) {
         System.out.println("-----fireAssignOwnershipCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid   [" + meetingid + "]");
         System.out.println("\tpartid      [" + partid + "]");
         System.out.println("\taddOrRemove [" + onOrOff + "]");
      }
   }
   public void fireStartShareCommand(DSMPHandler h, 
                                     byte flags, byte handle, 
                                     int meetingid) {
      if (printdebug && dodebug) {
         System.out.println("-----fireStartShareCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid   [" + meetingid + "]");
      }
   }
   public void fireStopShareCommand(DSMPHandler h, 
                                    byte flags, byte handle, 
                                    int meetingid) {
      if (printdebug && dodebug) {
         System.out.println("-----fireStopShareCommand: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid   [" + meetingid + "]");
      }
   }
   
   public void fireCreateGroupCommand(DSMPHandler h, byte flags, 
                                      byte handle, String groupname,
                                      byte visibility, 
                                      byte listability) {
                                      
      if (printdebug && dodebug) {
         System.out.println("-----fireCreateGroupCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tgroupname  [" +groupname+"]");
         System.out.println("\tvisibility [" +visibility+"]");
         System.out.println("\tlistability[" +listability+"]");
      }
      uncaughtProtocol(h, DSMPGenerator.OP_CREATE_GROUP);
   }
    
   public void fireDeleteGroupCommand(DSMPHandler h, byte flags, 
                                      byte handle, String groupname) {
      if (printdebug && dodebug) {
         System.out.println("-----fireDeleteGroupCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tgroupname[" +groupname+"]");
      }
      uncaughtProtocol(h, DSMPGenerator.OP_DELETE_GROUP);
   }
   
   public void fireModifyGroupAclCommand(DSMPHandler h, byte flags, 
                                         byte handle, 
                                         boolean memberOrAccess,
                                         boolean addOrRemove,
                                         String groupname, String username) {
                                      
      if (printdebug && dodebug) {
         System.out.println("-----fireModifyGroupAclCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tgroupname     [" +groupname+"]");
         System.out.println("\tusername      [" +username+"]");
         System.out.println("\tmemberOrAccess[" +memberOrAccess+"]");
         System.out.println("\taddOrRemove   [" +addOrRemove+"]");
      }
      uncaughtProtocol(h, DSMPGenerator.OP_MODIFY_GROUP_ACL);
   }
   
   public void fireModifyGroupAttributeCommand(DSMPHandler h, byte flags, 
                                               byte handle, String groupname,
                                               byte visibility, 
                                               byte listability) {
                                      
      if (printdebug && dodebug) {
         System.out.println("-----fireModifyGroupAttributeCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tgroupname  [" +groupname+"]");
         System.out.println("\tvisibility [" +visibility+"]");
         System.out.println("\tlistability[" +listability+"]");
      }
      uncaughtProtocol(h, DSMPGenerator.OP_MODIFY_GROUP_ATTRIBUTES);
   }
   
   public void fireQueryGroupsCommand(DSMPHandler h, byte flags,
                                      byte handle, 
                                      boolean regexSearch,
                                      boolean wantMember,
                                      boolean wantAccess,
                                      String groupname) {
                                      
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryGroupsCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tgroupname  [" +groupname+"]");
         System.out.println("\tregexSearch[" +regexSearch+"]");
         System.out.println("\twantMember [" +wantMember+"]");
         System.out.println("\twantAccess [" +wantAccess+"]");
      }
      uncaughtProtocol(h, DSMPGenerator.OP_QUERY_GROUPS);
   }
   
   public void firePlaceCallCommand(DSMPHandler h, byte flags,
                                    byte handle, 
                                    boolean meetingIdOrUser,
                                    int meetingid,
                                    String userToCall,
                                    String password) {
                                      
      if (printdebug && dodebug) {
         System.out.println("-----firePlaceCallCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmid Or User[" +meetingIdOrUser+"]");
         System.out.println("\tmeetingid  [" +meetingid+"]");
         System.out.println("\tusername   [" +userToCall+"]");
         System.out.println("\tpassword   [xxxxxxxxx]");
      }
      uncaughtProtocol(h, DSMPGenerator.OP_PLACECALL);
   }
   
   public void fireAcceptCallCommand(DSMPHandler h, byte flags,
                                     byte handle, 
                                     int meetingid,
                                     int callerid,
                                     String salt) {
                                      
      if (printdebug && dodebug) {
         System.out.println("-----fireAcceptCallCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid  [" +meetingid+"]");
         System.out.println("\tcallerid  [" +callerid+"]");
         System.out.println("\tsalt       [" +salt+"]");
      }
      uncaughtProtocol(h, DSMPGenerator.OP_ACCEPTCALL);
   }
   
   public void fireQueryMeetingOptionsCommand(DSMPHandler h, byte flags,
                                              byte handle, 
                                              boolean querySpecificOption,
                                              int meetingid,
                                              String specificOpt) {
                                      
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryMeetingOptionsCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
                            
         System.out.println("\tqrySpecific[" +querySpecificOption+"]");
         System.out.println("\tmeetingid  [" +meetingid+"]");
         System.out.println("\tspecificOpt[" +specificOpt+"]");
      }
      uncaughtProtocol(h, DSMPGenerator.OP_QUERY_MEETING_OPTIONS);
   }
   public void fireSetMeetingOptionsCommand(DSMPHandler h, byte flags,
                                            byte handle, 
                                            int meetingid,
                                            Hashtable options) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireSetMeetingOptionsCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
                            
         System.out.println("\tmeetingid  [" +meetingid+"]");
         System.out.println("\tnumOpts    [" +options.size()+"]");
         Enumeration keys = options.keys();
         while(keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            String val = (String)options.get(key);
            System.out.println("\t   Option[" +key+"]");
            System.out.println("\t   Value [" +val+"]");
         }
      }
      uncaughtProtocol(h, DSMPGenerator.OP_SET_MEETING_OPTIONS);
   }
   public void fireGetMeetingURLCommand(DSMPHandler h, byte flags,
                                        byte handle, 
                                        boolean includepw,
                                        boolean includemeetid,
                                        boolean sendemails,
                                        int meetingid) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireGetMeetingURLCommand: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
                            
         System.out.println("\tincludepw  [" +includepw+"]");
         System.out.println("\tincludemid [" +includemeetid+"]");
         System.out.println("\tsendemails [" +sendemails+"]");
         System.out.println("\tmeetingid  [" +meetingid+"]");
      }
      uncaughtProtocol(h, DSMPGenerator.OP_GET_MEETING_URL);
   }
   
   
  /* -------------------------------------------------------*\
  ** Replies
  \* -------------------------------------------------------*/
   public void fireLoginReply(DSMPHandler h, byte flags, byte handle,
                              int  loginid, String user, String company,
                              Vector v) {
      if (printdebug && dodebug) {
         System.out.println("-----fireLoginReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tloginid [" + loginid + "]");
         System.out.println("\tusername [" + user + "]");
         System.out.println("\tcompanyname [" + company + "]");
         
         if (v != null) {
            Enumeration e = v.elements();
            while(e.hasMoreElements()) {
               String s = (String)e.nextElement();
               System.out.println("\t   " + s);
               
            }
         }
      }
   }
   
   public void fireLogoutReply(DSMPHandler h, byte flags, byte handle) {
      if (printdebug && dodebug) {
         System.out.println("-----fireLogoutReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
   }
   public void fireStartMeetingReply(DSMPHandler h, 
                                     byte flags, byte handle,
                                     int  meetingid, int participantid) {
      if (printdebug && dodebug) {
         System.out.println("-----fireStartMeetingReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid [" + meetingid + "]");
         System.out.println("\tparticipantid [" + participantid + "]");
      }
   }
   public void fireLeaveMeetingReply(DSMPHandler h, 
                                     byte flags, byte handle) {
      if (printdebug && dodebug) {
         System.out.println("-----fireLeaveMeetingReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
   }
   public void fireEndMeetingReply(DSMPHandler h, 
                                   byte flags, byte handle) {
      if (printdebug && dodebug) {
         System.out.println("-----fireEndMeetingReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
   }
   public void fireCreateInvitationReply(DSMPHandler h, byte flags, 
                                         byte handle, 
                                         int  inviteid) {
      if (printdebug && dodebug) {
         System.out.println("-----fireCreateInvitationReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tinviteid [" + inviteid + "]");
      }
   }
   public void fireJoinMeetingReply(DSMPHandler h, 
                                    byte flags, byte handle, 
                                    int meetingid, int participantid) {
      if (printdebug && dodebug) {
         System.out.println("-----fireJoinMeetingReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid [" + meetingid + "]");
         System.out.println("\tparticipantid [" + participantid + "]");
      }
   }
   public void fireDropInviteeReply(DSMPHandler h, 
                                    byte flags, byte handle) {
      if (printdebug && dodebug) {
         System.out.println("-----fireDropInviteeReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
   }
   public void fireImageResizeReply(DSMPHandler h, 
                                    byte flags, byte handle) {
      if (printdebug && dodebug) {
         System.out.println("-----fireImageResizeReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
   }
   public void fireChatMessageReply(DSMPHandler h, 
                                    byte flags, byte handle) {
      if (printdebug && dodebug) {
         System.out.println("-----fireChatMessageReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
   }
   public void fireModifyControlReply(DSMPHandler h, 
                                      byte flags, byte handle) {
      if (printdebug && dodebug) {
         System.out.println("-----fireModifyControlReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
   }
   public void fireGetAllMeetingsReply(DSMPHandler h, 
                                       byte flags, byte handle, 
                                       Vector v) {
      if (printdebug && dodebug) {
         System.out.println("-----fireGetAllMeetingsReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         if (v != null) {
            Enumeration e = v.elements();
            while(e.hasMoreElements()) {
               DSMPMeeting m = (DSMPMeeting)e.nextElement();
               System.out.println("   " + m.toString());
               
            }
         }
      }
   }
   
   public void fireTransferModeratorReply(DSMPHandler h,
                                          byte flags, byte handle) {
      if (printdebug && dodebug) {
         System.out.println("-----fireTransferModeratorReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
   }
   public void fireAssignOwnershipReply(DSMPHandler h, byte flags, 
                                        byte handle) {
      if (printdebug && dodebug) {
         System.out.println("-----fireAssignOwnershipReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
   }
   public void fireStartShareReply(DSMPHandler h, byte flags, byte handle) {
      if (printdebug && dodebug) {
         System.out.println("-----fireStartShareReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
   }
   public void fireStopShareReply(DSMPHandler h, byte flags, byte handle) {
      if (printdebug && dodebug) {
         System.out.println("-----fireStopShareReply: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
   }
   
   public void fireCreateGroupReply(DSMPHandler h, byte flags,
                                    byte handle) {
                                      
      if (printdebug && dodebug) {
         System.out.println("-----fireCreateGroupReply: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
      uncaughtProtocol(h, DSMPGenerator.OP_CREATE_GROUP_REPLY);
   }
    
   public void fireDeleteGroupReply(DSMPHandler h, byte flags, 
                                    byte handle) {
      if (printdebug && dodebug) {
         System.out.println("-----fireDeleteGroupReply: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
      uncaughtProtocol(h, DSMPGenerator.OP_DELETE_GROUP_REPLY);
   }
   
   public void fireModifyGroupAclReply(DSMPHandler h, byte flags, 
                                       byte handle) {
                                      
      if (printdebug && dodebug) {
         System.out.println("-----fireModifyGroupAclReply: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
      uncaughtProtocol(h, DSMPGenerator.OP_MODIFY_GROUP_ACL_REPLY);
   }
   
   public void fireModifyGroupAttributeReply(DSMPHandler h, byte flags, 
                                             byte handle) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireModifyGroupAttributeReply: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
      uncaughtProtocol(h, DSMPGenerator.OP_MODIFY_GROUP_ATTRIBUTES_REPLY);
   }
   
   public void fireQueryGroupsReply(DSMPHandler h, byte flags,
                                    byte handle, 
                                    boolean includesMember,
                                    boolean includesAccess,
                                    Vector groups) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryGroupsReply: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tincludesMember[" +includesMember+"]");
         System.out.println("\tincludesAccess[" +includesAccess+"]");
         if (groups != null) {
            System.out.println("\tNumGroups[" +groups.size()+"]");
            Enumeration enum = groups.elements();
            while(enum.hasMoreElements()) {
               GroupInfo gi = (GroupInfo)enum.nextElement();
               System.out.println("\t --------------------------------");
               System.out.println("\t  groupname [" +gi.getGroupName()+"]");
               System.out.println("\t  groupowner[" +gi.getGroupOwner()+"]");
               System.out.println("\t  grpcompany[" +gi.getGroupCompany()+"]");
               System.out.println("\t  created   [" +gi.getGroupCreated()+"]");
               System.out.println("\t  visible   [" +
                                  gi.getGroupVisibility()+"]");
               System.out.println("\t  listable  ["+
                                  gi.getGroupListability()+"]");
               System.out.println("\t  membersValid["+
                                  gi.getGroupMembersValid()+"]");
               System.out.println("\t  accessValid["+
                                  gi.getGroupAccessValid()+"]");
               Vector members = gi.getGroupMembers();
               
               System.out.println("\t  members   [" +
                                  ((members!=null)?members.size():0)+"]");
               if (members != null) {
                  Enumeration venum = members.elements();
                  while(venum.hasMoreElements()) {
                     String v = (String)venum.nextElement();
                     System.out.println("\t     " + v);
                  }
               }
               Vector access = gi.getGroupAccess();
               System.out.println("\t  access    [" +
                                  ((access!=null)?access.size():0)+"]");
               if (access != null) {
                  Enumeration venum = access.elements();
                  while(venum.hasMoreElements()) {
                     String v = (String)venum.nextElement();
                     System.out.println("\t     " + v);
                  }
               }
            }
         }
      }
      uncaughtProtocol(h, DSMPGenerator.OP_QUERY_GROUPS_REPLY);
   }
   
   public void firePlaceCallReply(DSMPHandler h, byte flags,
                                  byte handle) {
                                      
      if (printdebug && dodebug) {
         System.out.println("-----firePlaceCallReply: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
      uncaughtProtocol(h, DSMPGenerator.OP_PLACECALL_REPLY);
   }
   
   public void fireAcceptCallReply(DSMPHandler h, byte flags,
                                   byte handle) {
                                      
      if (printdebug && dodebug) {
         System.out.println("-----fireAcceptCallReply: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
      uncaughtProtocol(h, DSMPGenerator.OP_ACCEPTCALL_REPLY);
   }
   
   public void fireQueryMeetingOptionsReply(DSMPHandler h, byte flags,
                                            byte handle, 
                                            int meetingid,
                                            Hashtable options) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryMeetingOptionsReply: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
                            
         System.out.println("\tnumOpts    [" +options.size()+"]");
         Enumeration keys = options.keys();
         while(keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            String val = (String)options.get(key);
            System.out.println("\t   Option[" +key+"]");
            System.out.println("\t   Value [" +val+"]");
         }
      }
      uncaughtProtocol(h, DSMPGenerator.OP_QUERY_MEETING_OPTIONS_REPLY);
   }
   public void fireSetMeetingOptionsReply(DSMPHandler h, byte flags,
                                          byte handle) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireSetMeetingOptionsReply: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
      }
      uncaughtProtocol(h, DSMPGenerator.OP_SET_MEETING_OPTIONS_REPLY);
   }
   public void fireGetMeetingURLReply(DSMPHandler h, byte flags,
                                      byte handle, 
                                      int meetingid,
                                      String URL) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireGetMeetingURLReply: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
                            
         System.out.println("\tmeetingid  [" +meetingid+"]");
         System.out.println("\tURL        [" +URL+"]");
      }
      uncaughtProtocol(h, DSMPGenerator.OP_GET_MEETING_URL_REPLY);
   }
   
   
   
  /* -------------------------------------------------------*\
  ** Reply Errors
  \* -------------------------------------------------------*/
  
   public void fireGenericReplyError(DSMPHandler h, 
                                     byte flags, byte handle, 
                                     byte opcode, 
                                     short errorcode, String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireGenericReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
   }
   
   public void fireLoginReplyError(DSMPHandler h, byte flags, byte handle, 
                                   short errorcode, 
                                   String errorStr) {
      if (printdebug && dodebug) {
         System.out.println("-----fireLoginReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
   }
                                      
   public void fireLogoutReplyError(DSMPHandler h, byte flags, byte handle,
                                    short errorcode, 
                                    String errorStr) {
      if (printdebug && dodebug) {
         System.out.println("-----fireLogoutReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
   }
   public void fireStartMeetingReplyError(DSMPHandler h, 
                                          byte flags, byte handle,
                                          short errorcode, 
                                          String errorStr) {
      if (printdebug && dodebug) {
         System.out.println("-----fireStartMeetingReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
   }
   public void fireLeaveMeetingReplyError(DSMPHandler h, 
                                          byte flags, byte handle,
                                          short errorcode, 
                                          String errorStr) {
      if (printdebug && dodebug) {
         System.out.println("-----fireLeaveMeetingReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
   }
   public void fireEndMeetingReplyError(DSMPHandler h, 
                                        byte flags, byte handle,
                                        short errorcode, 
                                        String errorStr) {
      if (printdebug && dodebug) {
         System.out.println("-----fireEndMeetingReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
   }
   public void fireCreateInvitationReplyError(DSMPHandler h, 
                                              byte flags, byte handle,
                                              short errorcode, 
                                              String errorStr) {
      if (printdebug && dodebug) {
         System.out.println("-----fireCreateInvitationReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
   }
   public void fireJoinMeetingReplyError(DSMPHandler h, 
                                         byte flags, byte handle,
                                         short errorcode, String errorStr) {
      if (printdebug && dodebug) {
         System.out.println("-----fireJoinMeetingReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");      
      }
   }
   public void fireDropInviteeReplyError(DSMPHandler h, 
                                         byte flags, byte handle,
                                         short errorcode, String errorStr) {
      if (printdebug && dodebug) {
         System.out.println("-----fireDropInviteeReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");      
      }
   }
   public void fireImageResizeReplyError(DSMPHandler h, 
                                         byte flags, byte handle,
                                         short errorcode, String errorStr) {
      if (printdebug && dodebug) {
         System.out.println("-----fireImageResizeReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");      
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");      
      }
   }
   public void fireChatMessageReplyError(DSMPHandler h, 
                                         byte flags, byte handle,
                                         short errorcode, String errorStr) {
      if (printdebug && dodebug) {
         System.out.println("-----fireChatMessageReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");      
      }
   }
   public void fireModifyControlReplyError(DSMPHandler h, 
                                           byte flags, byte handle,
                                           short errorcode, String errorStr) {
      if (printdebug && dodebug) {
         System.out.println("-----fireModifyControlReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");      
      }
   }
   public void fireGetAllMeetingsReplyError(DSMPHandler h, 
                                            byte flags, byte handle,
                                            short errorcode, String errorStr) {
      if (printdebug && dodebug) {
         System.out.println("-----fireGetAllMeetingsReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");      
      }
   }
   public void fireTransferModeratorReplyError(DSMPHandler h, 
                                            byte flags, byte handle,
                                            short errorcode, String errorStr) {
      if (printdebug && dodebug) {
         System.out.println("-----fireTransferModeratorReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");      
      }
   }
   public void fireAssignOwnershipReplyError(DSMPHandler h, 
                                            byte flags, byte handle,
                                            short errorcode, String errorStr) {
      if (printdebug && dodebug) {
         System.out.println("-----fireAssignOwnershipReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");      
      }
   }
   public void fireStartShareReplyError(DSMPHandler h, 
                                            byte flags, byte handle,
                                            short errorcode, String errorStr) {
      if (printdebug && dodebug) {
         System.out.println("-----fireStartShareReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");      
      }
   }
   public void fireStopShareReplyError(DSMPHandler h, 
                                            byte flags, byte handle,
                                            short errorcode, String errorStr) {
      if (printdebug && dodebug) {
         System.out.println("-----fireStopShareReplyError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");      
      }
   }
   public void fireCreateGroupReplyError(DSMPHandler h, 
                                         byte flags, byte handle,
                                         short errorcode,
                                         String errorStr) {
                                      
      if (printdebug && dodebug) {
         System.out.println("-----fireCreateGroupReplyError: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DSMPGenerator.OP_CREATE_GROUP_REPLY);
   }
   public void fireDeleteGroupReplyError(DSMPHandler h, 
                                         byte flags, byte handle,
                                         short errorcode,
                                         String errorStr) {
                                      
      if (printdebug && dodebug) {
         System.out.println("-----fireDeleteGroupReplyError: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DSMPGenerator.OP_CREATE_GROUP_REPLY);
   }
   public void fireModifyGroupAclReplyError(DSMPHandler h, 
                                            byte flags, byte handle,
                                            short errorcode,
                                            String errorStr) {
                                      
      if (printdebug && dodebug) {
         System.out.println("-----fireModifyGroupAclReplyError: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DSMPGenerator.OP_CREATE_GROUP_REPLY);
   }
   public void fireModifyGroupAttributeReplyError(DSMPHandler h, 
                                                  byte flags, byte handle,
                                                  short errorcode,
                                                  String errorStr) {
                                      
      if (printdebug && dodebug) {
         System.out.println("-----fireModifyGroupAttributeReplyError: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DSMPGenerator.OP_CREATE_GROUP_REPLY);
   }
   public void fireQueryGroupsReplyError(DSMPHandler h, 
                                         byte flags, byte handle,
                                         short errorcode,
                                         String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryGroupsReply: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DSMPGenerator.OP_CREATE_GROUP_REPLY);
   }
   
   public void firePlaceCallReplyError(DSMPHandler h, 
                                       byte flags, byte handle,
                                       short errorcode,
                                       String errorStr) {

      if (printdebug && dodebug) {
         System.out.println("-----firePlaceCallReply: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DSMPGenerator.OP_PLACECALL_REPLY);
   }
   
   public void fireAcceptCallReplyError(DSMPHandler h, 
                                        byte flags, byte handle,
                                        short errorcode,
                                        String errorStr) {
                                      
      if (printdebug && dodebug) {
         System.out.println("-----fireAcceptCallReply: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DSMPGenerator.OP_ACCEPTCALL_REPLY);
   }
   
   public void fireQueryMeetingOptionsReplyError(DSMPHandler h, 
                                                 byte flags, byte handle,
                                                 short errorcode,
                                                 String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireQueryMeetingOptionsReply: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DSMPGenerator.OP_QUERY_MEETING_OPTIONS_REPLY);
   }
   public void fireSetMeetingOptionsReplyError(DSMPHandler h, 
                                               byte flags, byte handle,
                                               short errorcode,
                                               String errorStr) {
                                          
      
      if (printdebug && dodebug) {
         System.out.println("-----fireSetMeetingOptionsReply: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DSMPGenerator.OP_SET_MEETING_OPTIONS_REPLY);
   }
   public void fireGetMeetingURLReplyError(DSMPHandler h, 
                                           byte flags, byte handle,
                                           short errorcode,
                                           String errorStr) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireGetMeetingURLReply: " +
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorStr + "]");
      }
      uncaughtProtocol(h, DSMPGenerator.OP_GET_MEETING_URL_REPLY);
   }
   
   
   
  /* -------------------------------------------------------*\
  ** Events
  \* -------------------------------------------------------*/
   public void fireEndMeetingEvent(DSMPHandler h, byte flags, byte handle, 
                                   int meetingid) {
      if (printdebug && dodebug) {
         System.out.println("-----fireEndMeetingEvent: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid [" + meetingid + "]");
      }
   }
   public void fireNewInvitationEvent(DSMPHandler h, 
                                      byte flags, byte handle, 
                                      int meetingid,
                                      int inviteid, byte inviteType,
                                      String name) {
      if (printdebug && dodebug) {
         System.out.println("-----fireNewInvitationEvent: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid [" + meetingid + "]");
         System.out.println("\tinviteid [" + inviteid + "]");
         System.out.println("\tinviteType [" + inviteType + "]");
         System.out.println("\tname [" + name + "]");
      }
   }
   public void fireJoinedMeetingEvent(DSMPHandler h, 
                                      byte flags, byte handle, 
                                      int meetingid, int inviteid,
                                      int participantid, int loginid,
                                      byte inviteType, String name) {
      if (printdebug && dodebug) {
         System.out.println("-----fireJoinedMeetingEvent: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid [" + meetingid + "]");
         System.out.println("\tinviteid [" + inviteid + "]");
         System.out.println("\tparticipantid [" + participantid + "]");
         System.out.println("\tloginid [" + loginid + "]");
         System.out.println("\tinviteType [" + inviteType + "]");
         System.out.println("\tname [" + name + "]");
         
      }
   }
                                      
   public void fireDroppedEvent(DSMPHandler h, byte flags, byte handle, 
                                int meetingid,
                                int inviteid, int participantid, 
                                boolean inviteDropped, 
                                boolean participantDropped) {
      if (printdebug && dodebug) {
         System.out.println("-----fireDroppedEvent: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid [" + meetingid + "]");
         System.out.println("\tinviteid [" + inviteid + "]");
         System.out.println("\tparticipantid [" + participantid + "]");
         System.out.println("\tinviteDropped [" + inviteDropped + "]");
         System.out.println("\tparticipantDropped [" + participantDropped + "]");
      }
   }
   public void fireImageResizeEvent(DSMPHandler hand, 
                                    byte flags, byte handle, 
                                    int meetingid,
                                    short x, short y, short w, short h) {
      if (printdebug && dodebug) {
         System.out.println("-----fireImageResizeEvent: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid [" + meetingid + "]");
         System.out.println("\tx [" + x + "]");
         System.out.println("\ty [" + y + "]");
         System.out.println("\tw [" + w + "]");
         System.out.println("\th [" + h + "]");
      }
   }
   public void fireFrameUpdateEvent(DSMPHandler hand, 
                                    byte flags, byte handle, 
                                    int meetingid,
                                    short x, short y, short w, short h,
                                    boolean compressed, byte buf[], 
                                    int ofs, int len) {
      if (printdebug && dodebug && !toneitdown) {
         System.out.println("-----fireFrameUpdateEvent: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid [" + meetingid + "]");
         System.out.println("\tx [" + x + "]");
         System.out.println("\ty [" + y + "]");
         System.out.println("\tw [" + w + "]");
         System.out.println("\th [" + h + "]");
         System.out.println("\tcompressed [" + compressed + "]");
         System.out.println("\tbuf.length [" + buf.length + "]");
         System.out.println("\tofs [" + ofs + "]");
         System.out.println("\tlen [" + len + "]");
      }
   }
   public void fireChatMessageEvent(DSMPHandler h, 
                                    byte flags, byte handle, 
                                    int meetingid,
                                    int fromid, int toid, String msg,
                                    boolean unicast) {
      if (printdebug && dodebug) {
         System.out.println("-----fireChatMessageEvent: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid [" + meetingid + "]");
         System.out.println("\tfromid [" + fromid + "]");
         System.out.println("\ttoid [" + toid + "]");
         System.out.println("\tmsg [" + msg + "]");
         System.out.println("\tunicast [" + unicast + "]");
      }
   }
   public void fireControlEvent(DSMPHandler h, byte flags, byte handle, 
                                int meetingid, 
                                int participantid) {
      if (printdebug && dodebug) {
         System.out.println("-----fireControlEvent: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid [" + meetingid + "]");
         System.out.println("\tparticipantid [" + participantid + "]");
      }
   }
   public void fireMouseUpdateEvent(DSMPHandler h, 
                                    byte flags, byte handle, 
                                    int meetingid,
                                    boolean buttonEvent, 
                                    boolean pressRelease,
                                    boolean realMotion,
                                    byte button, short x, short y, 
                                    int participantid) {
      if (printdebug && dodebug && !toneitdown) {
         System.out.println("-----fireMouseUpdateEvent: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid [" + meetingid + "]");
         System.out.println("\tbuttonEvent [" + buttonEvent + "]");
         System.out.println("\tpressRelease [" + pressRelease + "]");
         System.out.println("\trealMotion [" + realMotion + "]");
         System.out.println("\tbutton [" + button + "]");
         System.out.println("\tx [" + x + "]");
         System.out.println("\ty [" + y + "]");
         System.out.println("\tparticipantid [" + participantid + "]");
      }
   }
   public void fireKeyUpdateEvent(DSMPHandler h, byte flags, byte handle, 
                                  int meetingid,
                                  boolean pressRelease,
                                  boolean keyCodeOrChar,
                                  short x, short y, 
                                  int javakeysym,
                                  int participantid) {
      if (printdebug && dodebug && !toneitdown) {
         System.out.println("-----fireKeyUpdateEvent: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid [" + meetingid + "]");
         System.out.println("\tpressRelease [" + pressRelease + "]");
         System.out.println("\tkeyCodeOrChar [" + keyCodeOrChar + "]");
         System.out.println("\tx [" + x + "]");
         System.out.println("\ty [" + y + "]");
         System.out.println("\tjavakeysym [" + javakeysym + "]");
         System.out.println("\tparticipantid [" + participantid + "]");
      }
   }
   
   public void fireFrameEndEvent(DSMPHandler hand, byte flags, byte handle,
                                 int meetingid) {
      if (printdebug && dodebug && !toneitdown) {
         System.out.println("-----fireFrameEndEvent: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid [" + meetingid + "]");
      }
   }
   public void fireFrozenModeEvent(DSMPHandler hand, 
                                   byte flags, byte handle,
                                   int meetingid, boolean frozen) {
      if (printdebug && dodebug) {
         System.out.println("-----fireFrozenModeEvent: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid [" + meetingid + "]");
         System.out.println("\tfrozen [" + frozen + "]");
      }
   }
   
   public void fireModeratorChangeEvent(DSMPHandler h, 
                                        byte flags, byte handle, 
                                        int meetingid,
                                        int fromPartid,
                                        int toPartid) {
      if (printdebug && dodebug) {
         System.out.println("-----fireModeratorChangeEvent: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid  [" + meetingid + "]");
         System.out.println("\tfromPartid [" + fromPartid + "]");
         System.out.println("\ttoPartid   [" + toPartid + "]");
      }
   }
   public void fireOwnershipChangeEvent(DSMPHandler h, 
                                        byte flags, byte handle, 
                                        int meetingid, int partid, 
                                        boolean addOrRemove) {
      if (printdebug && dodebug) {
         System.out.println("-----fireOwnershipChangeEvent: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid   [" + meetingid + "]");
         System.out.println("\tpartid      [" + partid + "]");
         System.out.println("\taddOrRemove [" + addOrRemove + "]");
      }
   }
   public void fireStartShareEvent(DSMPHandler h, byte flags, byte handle, 
                                   int meetingid) {
      if (printdebug && dodebug) {
         System.out.println("-----fireStartShareEvent: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid   [" + meetingid + "]");
      }
   }
   public void fireStopShareEvent(DSMPHandler h, byte flags, byte handle, 
                                  int meetingid) {
      if (printdebug && dodebug) {
         System.out.println("-----fireStopShareEvent: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid   [" + meetingid + "]");
      }
   }
   
   public void firePlaceCallEvent(DSMPHandler h, 
                                  byte flags, byte handle, 
                                  int meetingid, int fromPartid, 
                                  String fromUserid, 
                                  String fromCompany,
                                  String salt) {
      if (printdebug && dodebug) {
         System.out.println("-----firePlaceCallEvent: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid   [" + meetingid + "]");
         System.out.println("\tfromParmid  [" + fromPartid + "]");
         System.out.println("\tfromUserid  [" + fromUserid + "]");
         System.out.println("\tfromCompany [" + fromCompany + "]");
         System.out.println("\tsalt        [" + salt + "]");
      }
   }
   
   public void fireAcceptCallEvent(DSMPHandler h, 
                                   byte flags, byte handle, 
                                   int meetingid, int acceptingLoginId, 
                                   String acceptingUserid, 
                                   String acceptingCompany) {
      if (printdebug && dodebug) {
         System.out.println("-----fireAcceptCallEvent: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid   [" + meetingid + "]");
         System.out.println("\tacceptLogId [" + acceptingLoginId + "]");
         System.out.println("\tacceptUserid[" + acceptingUserid  + "]");
         System.out.println("\tacceptCompny[" + acceptingCompany + "]");
      }
   }
   
   public void fireMeetingOptionEvent(DSMPHandler h, 
                                      byte flags, byte handle, 
                                      int meetingid, String key, String val) {
      
      if (printdebug && dodebug) {
         System.out.println("-----fireMeetingOptionEvent: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\tmeetingid   [" + meetingid + "]");
         System.out.println("\tkey         [" + key + "]");
         System.out.println("\tval         [" + val + "]");
      }
   }
   
   
  /* -------------------------------------------------------*\
  ** Errors
  \* -------------------------------------------------------*/
   
   public void fireFrameUpdateError(DSMPHandler h, 
                                    byte flags, byte handle, 
                                    int meetingid,
                                    short errorcode, String errorString) {
      if (printdebug && dodebug) {
         System.out.println("-----fireFrameUpdateError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorString + "]");
      }
   }
   
   public void fireKeyUpdateError(DSMPHandler h, byte flags, byte handle, 
                                  int meetingid,
                                  short errorcode, String errorString) {
      if (printdebug && dodebug) {
         System.out.println("-----fireKeyUpdateError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorString + "]");
      }
   }
   public void fireMouseUpdateError(DSMPHandler h, 
                                    byte flags, byte handle, 
                                    int meetingid,
                                    short errorcode, String errorString) {
      if (printdebug && dodebug) {
         System.out.println("-----fireMouseUpdateError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorString + "]");
      }
   }
   public void fireFrameEndError(DSMPHandler h, byte flags, byte handle, 
                                 int meetingid, short errorcode, 
                                 String errorString) {
      if (printdebug && dodebug) {
         System.out.println("-----fireFrameEndError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorString + "]");
      }
   }
   public void fireFrozenModeError(DSMPHandler h, byte flags, byte handle, 
                                   int meetingid, short errorcode, 
                                   String errorString) {
      if (printdebug && dodebug) {
         System.out.println("-----fireFrozenModeError: " + 
                            "\n\tflags[" +flags+"] hand["+handle+"]");
         System.out.println("\terrorcode [" + errorcode + "]");
         System.out.println("\terrorStr [" + errorString + "]");
      }
   }
   
   
  /* -------------------------------------------------------*\
  ** Checking and Dispatching
  \* -------------------------------------------------------*/
  
   public void dispatchProtocolI(DSMPBaseProto proto, 
                                 DSMPBaseHandler handlerIn, 
                                 boolean doDispatch) 
                                            throws InvalidProtocolException {
                                            
      int meetingid, participantid, inviteid, loginid;
      String name;
      
      byte opcode = proto.getOpcode();
      byte flags  = proto.getFlags();
      byte handle = proto.getHandle();
      proto.resetCursor();
      
      DSMPHandler handler = (DSMPHandler)handlerIn;
      
      if (printdebug && dodebug && redispatch) {
         try {
            if (pdbg == null) {
               pdbg = (DSMPDispatchBase)DSMPSTDispatchBase.class.newInstance();
            }
            pdbg.setRedispatch(false);
            pdbg.setDebug(true);
            ((DSMPSTDispatchBase)pdbg).setToneItDown(toneitdown);
            
            pdbg.dispatchProtocolI(proto, null, true);
            proto.resetCursor();
         } catch(Throwable tttt) {
            System.out.println("Error dispatching DebugProtocol =>");
            tttt.printStackTrace(System.out);
         }
         proto.resetCursor();
      }
      
      if (DSMPGenerator.isReply(opcode)) {
         boolean success = (flags & (byte)0x01) != 0;
         if (!success) {
            short    errorcode = proto.getShort();
            String errorString = proto.getString8();
            
            proto.verifyCursorDone();
            if (doDispatch) {
               switch(opcode) {
                  case DSMPGenerator.OP_LOGIN_REPLY:
                     fireLoginReplyError(handler, flags, handle,
                                         errorcode, errorString);
                     break;
                  case DSMPGenerator.OP_LOGOUT_REPLY:
                     fireLogoutReplyError(handler, flags, handle,
                                          errorcode, errorString);
                     break;
                  case DSMPGenerator.OP_STARTMEETING_REPLY:
                     fireStartMeetingReplyError(handler, flags, handle,
                                                errorcode, 
                                                errorString);
                     break;
                  case DSMPGenerator.OP_LEAVEMEETING_REPLY:
                     fireLeaveMeetingReplyError(handler, flags, handle,
                                                errorcode, 
                                                errorString);
                     break;
                  case DSMPGenerator.OP_ENDMEETING_REPLY:
                     fireEndMeetingReplyError(handler, flags, handle,
                                              errorcode, 
                                              errorString);
                     break;
                  case DSMPGenerator.OP_CREATEINVITATION_REPLY:
                     fireCreateInvitationReplyError(handler, flags, handle,
                                                    errorcode, 
                                                    errorString);
                     break;
                  case DSMPGenerator.OP_JOINMEETING_REPLY:
                     fireJoinMeetingReplyError(handler, flags, handle,
                                               errorcode, 
                                               errorString);
                     break;
                  case DSMPGenerator.OP_DROPINVITEE_REPLY:
                     fireDropInviteeReplyError(handler, flags, handle,
                                               errorcode, 
                                               errorString);
                     break;
                  case DSMPGenerator.OP_IMAGERESIZE_REPLY:
                     fireImageResizeReplyError(handler, flags, handle,
                                               errorcode, 
                                               errorString);
                     break;
                  case DSMPGenerator.OP_CHATMESSAGE_REPLY:
                     fireChatMessageReplyError(handler, flags, handle,
                                               errorcode, 
                                               errorString);
                     break;
                  case DSMPGenerator.OP_MODIFYCONTROL_REPLY:
                     fireModifyControlReplyError(handler, flags, handle,
                                                 errorcode, 
                                                 errorString);
                     break;
                  case DSMPGenerator.OP_GETALLMEETINGS_REPLY:
                     fireGetAllMeetingsReplyError(handler, flags, handle,
                                                  errorcode, 
                                                  errorString);
                     break;
                  case DSMPGenerator.OP_TRANSFERMODERATOR_REPLY:
                     fireTransferModeratorReplyError(handler, flags, handle,
                                                     errorcode, 
                                                     errorString);
                     break;
                  case DSMPGenerator.OP_ASSIGNOWNERSHIP_REPLY:
                     fireAssignOwnershipReplyError(handler, flags, handle,
                                                   errorcode, 
                                                   errorString);
                     break;
                  case DSMPGenerator.OP_STARTSHARE_REPLY:
                     fireStartShareReplyError(handler, flags, handle,
                                              errorcode, 
                                              errorString);
                     break;
                  case DSMPGenerator.OP_STOPSHARE_REPLY:
                     fireStopShareReplyError(handler, flags, handle,
                                             errorcode, 
                                             errorString);
                     break;
                     
                  case DSMPGenerator.OP_CREATE_GROUP_REPLY: {
                     fireCreateGroupReplyError(handler, flags, 
                                               handle, errorcode, 
                                               errorString);
                     break;
                  }
                  case DSMPGenerator.OP_DELETE_GROUP_REPLY: {
                     fireDeleteGroupReplyError(handler, flags, 
                                               handle, errorcode, 
                                               errorString);
                     break;
                  }
                  case DSMPGenerator.OP_MODIFY_GROUP_ACL_REPLY: {
                     fireModifyGroupAclReplyError(handler, flags, 
                                                  handle, errorcode, 
                                                  errorString);
                     break;
                  }
                  case DSMPGenerator.OP_MODIFY_GROUP_ATTRIBUTES_REPLY: {
                     fireModifyGroupAttributeReplyError(handler, flags, 
                                                        handle, errorcode, 
                                                        errorString);
                     break;
                  }
                  case DSMPGenerator.OP_QUERY_GROUPS_REPLY: {
                     fireQueryGroupsReplyError(handler, flags, 
                                               handle, errorcode, 
                                               errorString);
                     break;
                  }
                  case DSMPGenerator.OP_PLACECALL_REPLY: {
                     firePlaceCallReplyError(handler, flags, 
                                             handle, errorcode, 
                                             errorString);
                     break;
                  }
                  case DSMPGenerator.OP_ACCEPTCALL_REPLY: {
                     fireAcceptCallReplyError(handler, flags, 
                                              handle, errorcode, 
                                              errorString);
                     break;
                  }
                  case DSMPGenerator.OP_QUERY_MEETING_OPTIONS_REPLY: {
                     fireQueryMeetingOptionsReplyError(handler, flags, 
                                                       handle, errorcode, 
                                                       errorString);
                                                  
                     break;
                  }
                  case DSMPGenerator.OP_SET_MEETING_OPTIONS_REPLY: {
                     fireSetMeetingOptionsReplyError(handler, flags, 
                                                     handle, errorcode, 
                                                     errorString);
                     break;
                  }
                  case DSMPGenerator.OP_GET_MEETING_URL_REPLY: {
                     fireGetMeetingURLReplyError(handler, flags, 
                                                 handle, errorcode, 
                                                 errorString);
                                            
                     break;
                  }                                             
               }
            }
         } else {
            switch(opcode) {
               case DSMPGenerator.OP_LOGIN_REPLY: {
                  Vector v       = new Vector();
                  loginid        = proto.getInteger();
                  String user    = proto.getString16();
                  String company = proto.getString16();
                  int nump       = proto.getInteger();
                  for(int i=0; i < nump; i++) {
                     v.addElement(proto.getString16());
                  }
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     fireLoginReply(handler, flags, handle,
                                    loginid, user, company, v);
                  }
                  break;
               }
               case DSMPGenerator.OP_LOGOUT_REPLY:
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     fireLogoutReply(handler, flags, handle);
                  }
                  break;
               case DSMPGenerator.OP_STARTMEETING_REPLY:
                  meetingid     = proto.getInteger();
                  participantid = proto.getInteger();
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     fireStartMeetingReply(handler, flags, handle,
                                           meetingid,
                                           participantid);
                  }
                  break;
               case DSMPGenerator.OP_LEAVEMEETING_REPLY:
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     fireLeaveMeetingReply(handler, flags, handle);
                  }
                  break;
               case DSMPGenerator.OP_ENDMEETING_REPLY:
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     fireEndMeetingReply(handler, flags, handle);
                  }
                  break;
               case DSMPGenerator.OP_CREATEINVITATION_REPLY:
                  inviteid = proto.getInteger();
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     fireCreateInvitationReply(handler, flags, handle,
                                               inviteid);
                  }
                  break;
               case DSMPGenerator.OP_JOINMEETING_REPLY:
                  meetingid     = proto.getInteger();
                  participantid = proto.getInteger();
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     fireJoinMeetingReply(handler, flags, handle,
                                          meetingid,
                                          participantid);
                  }
                  break;
               case DSMPGenerator.OP_DROPINVITEE_REPLY:
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     fireDropInviteeReply(handler, flags, handle);
                  }
                  break;
               case DSMPGenerator.OP_IMAGERESIZE_REPLY:
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     fireImageResizeReply(handler, flags, handle);
                  }
                  break;
               case DSMPGenerator.OP_CHATMESSAGE_REPLY:
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     fireChatMessageReply(handler, flags, handle);
                  }
                  break;
               case DSMPGenerator.OP_MODIFYCONTROL_REPLY:
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     fireModifyControlReply(handler, flags, handle);
                  }
                  break;
               case DSMPGenerator.OP_GETALLMEETINGS_REPLY: {
                  int numm  = proto.getUnsignedShort();
                  Vector v  = new Vector();  
                  for(int i=0; i < numm; i++) {
                     inviteid              = proto.getInteger();
                     loginid               = proto.getInteger();
                     byte inviteType       = proto.getByte();
                     meetingid             = proto.getInteger();
                     String title          = proto.getString8(); 
                     String owner          = proto.getString8(); 
                     String classification = proto.getString8(); 
                     v.addElement(new DSMPMeeting(inviteid, 
                                                  loginid,
                                                  inviteType,
                                                  meetingid, 
                                                  title,
                                                  owner,
                                                  classification));
                  }
                  
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     fireGetAllMeetingsReply(handler, flags, handle, v);
                  }
                  break;
               }
               
               case DSMPGenerator.OP_TRANSFERMODERATOR_REPLY:
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     fireTransferModeratorReply(handler, flags, handle);
                  }
                  break;
               case DSMPGenerator.OP_ASSIGNOWNERSHIP_REPLY:
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     fireAssignOwnershipReply(handler, flags, handle);
                  }
                  break;
               case DSMPGenerator.OP_STARTSHARE_REPLY:
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     fireStartShareReply(handler, flags, handle);
                  }
                  break;
               case DSMPGenerator.OP_STOPSHARE_REPLY:
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     fireStopShareReply(handler, flags, handle);
                  }
                  break;
               case DSMPGenerator.OP_CREATE_GROUP_REPLY: {
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     fireCreateGroupReply(handler, flags, handle);
                  }
                  break;
               }
               case DSMPGenerator.OP_DELETE_GROUP_REPLY: {
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     fireDeleteGroupReply(handler, flags, handle);
                  }
                  break;
               }
               case DSMPGenerator.OP_MODIFY_GROUP_ACL_REPLY: {
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     fireModifyGroupAclReply(handler, flags, handle);
                  }
                  break;
               }
               case DSMPGenerator.OP_MODIFY_GROUP_ATTRIBUTES_REPLY: {
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     fireModifyGroupAttributeReply(handler, flags, handle);
                  }
                  break;
               }
               case DSMPGenerator.OP_QUERY_GROUPS_REPLY: {
                  int numgrps = proto.get3ByteInteger();
                  Vector vec = new Vector();
                  while(numgrps-- > 0) {
                     GroupInfo gi = new GroupInfo();
                     gi.setGroupName(proto.getString16());
                     gi.setGroupOwner(proto.getString16());
                     gi.setGroupCompany(proto.getString16());
                     gi.setGroupCreated(proto.getLong());
                     gi.setGroupVisibility(proto.getByte());
                     gi.setGroupListability(proto.getByte());
                     byte lflags = proto.getByte();
                     
                     int nummem = proto.get3ByteInteger();
                     Vector members = gi.getGroupMembers();
                     while(nummem-- > 0) {
                        members.addElement(proto.getString16());
                     }
                     gi.setGroupMembers(members);
                     gi.setGroupMembersValid((lflags & (byte)1) != (byte)0);
                     
                     int numacc = proto.get3ByteInteger();
                     Vector access = gi.getGroupAccess();
                     while(numacc-- > 0) {
                        access.addElement(proto.getString16());
                     }
                     gi.setGroupAccess(access);
                     gi.setGroupAccessValid((lflags & (byte)2) != (byte)0);
                     
                     vec.addElement(gi);
                  }
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     boolean memberIncluded = (flags & (byte)1) != (byte)0;
                     boolean accessIncluded = (flags & (byte)2) != (byte)0;
                     fireQueryGroupsReply(handler, flags, handle, 
                                          memberIncluded, accessIncluded,
                                          vec);
                  }
                  break;
                  
               }
               
               case DSMPGenerator.OP_PLACECALL_REPLY: {
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     firePlaceCallReply(handler, flags, handle);
                  }
                  break;
               }
               case DSMPGenerator.OP_ACCEPTCALL_REPLY: {
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     fireAcceptCallReply(handler, flags, handle);
                  }
                  break;
               }
               case DSMPGenerator.OP_QUERY_MEETING_OPTIONS_REPLY: {
                  meetingid = proto.getInteger();
                  short numopts = proto.getShort();
                  Hashtable options = new Hashtable();
                  
                  for(int i=0; i < numopts; i++) {
                     String key = proto.getString8();
                     String val = proto.getString16();
                     options.put(key, val);
                  }
                  
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     fireQueryMeetingOptionsReply(handler, flags, handle,
                                                  meetingid, options);
                  }
                  break;
               }
               case DSMPGenerator.OP_SET_MEETING_OPTIONS_REPLY: {
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     fireSetMeetingOptionsReply(handler, flags, handle);
                  }
                  break;
               }
               case DSMPGenerator.OP_GET_MEETING_URL_REPLY: {
                  meetingid = proto.getInteger();
                  String url = proto.getString16();
                  proto.verifyCursorDone();
                  if (doDispatch) {
                     fireGetMeetingURLReply(handler, flags, handle, 
                                            meetingid, url);
                  }
                  break;
               }                                             
            }
         }
      } else if (DSMPGenerator.isError(opcode)) {
         meetingid          = proto.getInteger();
         short errorcode    = proto.getShort();
         String errorString = proto.getString8();
         proto.verifyCursorDone();
         if (doDispatch) {
            switch(opcode) {
               case DSMPGenerator.OP_FRAMEUPDATE_ERROR:
                  fireFrameUpdateError(handler, flags, handle, meetingid,
                                       errorcode, errorString);
                  break;
               case DSMPGenerator.OP_KEYUPDATE_ERROR:
                  fireKeyUpdateError(handler, flags, handle, meetingid,
                                     errorcode, errorString);
                  break;
               case DSMPGenerator.OP_MOUSEUPDATE_ERROR:
                  fireMouseUpdateError(handler, flags, handle, meetingid,
                                       errorcode, errorString);
                  break;
               case DSMPGenerator.OP_FRAMEEND_ERROR:
                  fireFrameEndError(handler, flags, handle, meetingid,
                                    errorcode, errorString);
                  break;
               case DSMPGenerator.OP_FROZENMODE_ERROR:
                  fireFrozenModeError(handler, flags, handle, meetingid,
                                      errorcode, errorString);
                  break;
            }
         }
      } else if (DSMPGenerator.isEvent(opcode)) {
         byte inviteType;
         switch(opcode) {
            case DSMPGenerator.OP_ENDMEETING_EVENT:
               meetingid = proto.getInteger();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireEndMeetingEvent(handler, flags, handle, meetingid);
               }
               break;
            case DSMPGenerator.OP_NEWINVITATION_EVENT:
               meetingid       = proto.getInteger();
               inviteid        = proto.getInteger();
               inviteType      = proto.getByte();
               name            = proto.getString8();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireNewInvitationEvent(handler, flags, handle, meetingid,
                                         inviteid, inviteType, name);
               }
               break;
            case DSMPGenerator.OP_JOINEDMEETING_EVENT:
               meetingid       = proto.getInteger();
               inviteid        = proto.getInteger();
               participantid   = proto.getInteger();
               loginid         = proto.getInteger();
               inviteType      = proto.getByte();
               name            = proto.getString8();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireJoinedMeetingEvent(handler, flags, handle, meetingid,
                                         inviteid, participantid, loginid, 
                                         inviteType, name);
               }
               break;
            case DSMPGenerator.OP_DROPPED_EVENT:
               meetingid     = proto.getInteger();
               inviteid      = proto.getInteger();
               participantid = proto.getInteger();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireDroppedEvent(handler, flags, handle, meetingid,
                                   inviteid, participantid, 
                                   (flags & (byte)0x01) != 0,
                                   (flags & (byte)0x02) != 0);
               }
               break;
            case DSMPGenerator.OP_IMAGERESIZE_EVENT: {
               short x, y, w, h;
               meetingid = proto.getInteger();
               x         = proto.getShort();
               y         = proto.getShort();
               w         = proto.getShort();
               h         = proto.getShort();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireImageResizeEvent(handler, flags, handle, meetingid, 
                                       x, y, w, h);
               }
               break;
            }
            case DSMPGenerator.OP_FRAMEUPDATE_EVENT: {
               short x, y, w, h;
               meetingid  = proto.getExtraInt();
               x          = proto.getShort();
               y          = proto.getShort();
               w          = proto.getShort();
               h          = proto.getShort();
               CompressInfo ci = proto.getDataAtCursor();
               proto.verifyCursorDone();
               if (doDispatch) {
                  boolean compressed = (flags & (byte)0x01) != 0;
                  if (compressed) {
                     try {
                        cpress.setStartDeCompressionSize(20000);
                        ci = cpress.decompress(ci.buf, ci.ofs, ci.len);
                        compressed = false;
                     } catch(DataFormatException de) {
                        System.out.println("Error decompressing!!");
                     }
                  }
                  
//                  System.out.println("VQ = " + proto.getVirtualQuadrant());
                  
                  fireFrameUpdateEvent(handler, flags, handle, meetingid, 
                                       x, y, w, h, compressed, 
                                       ci.buf, ci.ofs, ci.len);
               }
               break;
            }
            case DSMPGenerator.OP_MULTIFRAMEUPDATE_EVENT: {
               short x, y, w, h;
               meetingid  = proto.getExtraInt();
               CompressInfo ci = proto.getDataAtCursor();
               proto.verifyCursorDone();
               if (doDispatch) {
               
                  boolean compressed = (flags & (byte)0x01) != 0;
                  
                  if (compressed) {
                     try {
                        cpress.setStartDeCompressionSize(164000);
                        ci = cpress.decompress(ci.buf, ci.ofs, ci.len);
                        
                        compressed = false;
                     } catch(DataFormatException de) {
                        System.out.println("Error decompressing!!");
                        throw new InvalidProtocolException("Decompress error");
                     }
                  }
                  
                 // 1. Verify that each item IS an OP_FRAME_UPDATE
                 // 2. Convert the COMMAND to EVENT (opcode change)
                 // 3. verify that the meetingid's match
                 // 4. dispatch the event
                  try {
                     int ofs = ci.ofs + 4;  // Skip that useless numFrames
                     int endofs = ci.ofs + ci.len;
                     while(ofs < endofs) {
                        DSMPProto nproto = new DSMPProto();
                        ofs = nproto.readAll(ci.buf, ofs, 9999999);
                        if (nproto.getOpcode() != 
                            DSMPGenerator.OP_FRAMEUPDATE) {
                           throw 
                              new InvalidProtocolException("MFUE != opcode");
                        }
                        
                        if (nproto.getInteger() != meetingid) {
                           throw 
                              new InvalidProtocolException("MFUE != meetid");
                        }
                        
                        nproto.addMaskToFlags(DSMPGenerator.FLAGS_BIT1);
                        nproto.setOpcode(DSMPGenerator.OP_FRAMEUPDATE_EVENT);
                        nproto.resetCursor();
                        dispatchProtocol(nproto, handler);
                     }
                  } catch(ArrayIndexOutOfBoundsException abe) {
                     throw new InvalidProtocolException("Bad MFE");
                  }
               }
               break;
            }
            case DSMPGenerator.OP_CHATMESSAGE_EVENT: {
               meetingid     = proto.getInteger();
               participantid = proto.getInteger();
               int toid      = proto.getInteger();
               String str    = proto.getString16();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireChatMessageEvent(handler, flags, handle, meetingid,
                                       participantid, toid, str,
                                       (flags & (byte)0x01) != 0);
               }
               break;
            }
            case DSMPGenerator.OP_CONTROL_EVENT:
               meetingid     = proto.getInteger();
               participantid = proto.getInteger();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireControlEvent(handler, flags, handle, meetingid, 
                                   participantid);
               }
               break;
            case DSMPGenerator.OP_MOUSEUPDATE_EVENT: {
               short x, y;
               byte button;
               meetingid               = proto.getInteger();
               button                  = proto.getByte();
               x                       = proto.getShort();
               y                       = proto.getShort();
               participantid           = proto.getInteger();
               boolean buttonEv        = (flags & (byte)0x01) != 0;
               boolean pressRelease    = (flags & (byte)0x02) != 0;
               boolean realMotion      = (flags & (byte)0x04) != 0;
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireMouseUpdateEvent(handler, flags, handle, meetingid, 
                                       buttonEv, pressRelease, realMotion,
                                       button, x, y, participantid);
               }
               break;
            }
            case DSMPGenerator.OP_KEYUPDATE_EVENT: {
               short x, y;
               int javakeysym;
               meetingid               = proto.getInteger();
               x                       = proto.getShort();
               y                       = proto.getShort();
               javakeysym              = proto.getInteger();
               participantid           = proto.getInteger();
               boolean pressRelease    = (flags & (byte)0x01) != 0;
               boolean keyCodeOrChar   = (flags & (byte)0x02) != 0;
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireKeyUpdateEvent(handler, flags, handle, meetingid, 
                                     pressRelease, keyCodeOrChar, 
                                     x, y, javakeysym, participantid);
               }
               break;
            }
            case DSMPGenerator.OP_FRAMEEND_EVENT:
               meetingid     = proto.getInteger();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireFrameEndEvent(handler, flags, handle, meetingid);
               }
               break;
            case DSMPGenerator.OP_FROZENMODE_EVENT:
               meetingid     = proto.getInteger();
               proto.verifyCursorDone();
               if (doDispatch) {
                  boolean v = (flags & (byte)0x01) != 0;  // frozen or not
                  fireFrozenModeEvent(handler, flags, handle, meetingid, v);
               }
               break;
               
            case DSMPGenerator.OP_MODERATORCHANGE_EVENT: {
               meetingid     = proto.getInteger();
               int fromid    = proto.getInteger();
               int toid      = proto.getInteger();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireModeratorChangeEvent(handler, flags, handle, 
                                           meetingid, fromid, toid);
               }
               break;
            }
            case DSMPGenerator.OP_OWNERCHANGE_EVENT: {
               meetingid     = proto.getInteger();
               participantid = proto.getInteger();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireOwnershipChangeEvent(handler, flags, handle,
                                           meetingid, participantid, 
                                           (((int)flags) & 1) != 0);
               }
               break;
            }
            case DSMPGenerator.OP_STARTSHARE_EVENT: {
               meetingid     = proto.getInteger();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireStartShareEvent(handler, flags, handle, meetingid);
               }
               break;
            }
            case DSMPGenerator.OP_STOPSHARE_EVENT: {
               meetingid     = proto.getInteger();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireStopShareEvent(handler, flags, handle, meetingid);
               }
               break;
            }
            
            case DSMPGenerator.OP_PLACECALL_EVENT: {
               meetingid = proto.getInteger();
               int partid       = proto.getInteger();
               String userid    = proto.getString8();
               String company   = proto.getString8();
               String salt      = proto.getString8();
               proto.verifyCursorDone();
               if (doDispatch) {
                  firePlaceCallEvent(handler, flags, handle, 
                                     meetingid, partid, userid, 
                                     company, salt);
               }
               break;
            }
            case DSMPGenerator.OP_ACCEPTCALL_EVENT: {
               meetingid = proto.getInteger();
               int partid       = proto.getInteger();
               String  userid   = proto.getString8();
               String company   = proto.getString8();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireAcceptCallEvent(handler, flags, handle, 
                                      meetingid, partid, userid, 
                                      company);
               }
               break;
            }
            case DSMPGenerator.OP_MEETING_OPTION_EVENT: {
               meetingid = proto.getInteger();
               String key = proto.getString8();
               String val = proto.getString16();
               
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireMeetingOptionEvent(handler, flags, handle,
                                         meetingid, key, val);
               }
               break;
            }
            
         }            
      } else if (DSMPGenerator.isCommand(opcode)) {
      
         switch(opcode) {
            case DSMPGenerator.OP_LOGIN: {
               String tokenUser = proto.getString16();
               String pw        = proto.getString8();
               proto.verifyCursorDone();
               if (doDispatch) {
                  if ((flags & (byte)0x01) != 0) {
                     fireLoginCommandToken(handler, flags, handle, tokenUser);
                  } else {
                     fireLoginCommandUserPW(handler, flags, handle, 
                                            tokenUser, pw);
                  }
               }
               break;
            }
            case DSMPGenerator.OP_LOGOUT: {
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireLogoutCommand(handler, flags, handle);
               }
               break;
            }
            case DSMPGenerator.OP_STARTMEETING: {
               String title = proto.getString8();
               String pw    = proto.getString8();
               String cls   = proto.getString8();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireStartMeetingCommand(handler, flags, handle, 
                                          title, pw, cls);
               }
               break;
            }
            case DSMPGenerator.OP_LEAVEMEETING: {
               meetingid     = proto.getInteger();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireLeaveMeetingCommand(handler, flags, handle, meetingid);
               }
               break;
            }
            case DSMPGenerator.OP_ENDMEETING: {
               meetingid = proto.getInteger();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireEndMeetingCommand(handler, flags, handle, meetingid);
               }
               break;
            }
               
            case DSMPGenerator.OP_CREATEINVITATION: {
               meetingid       = proto.getInteger();
               byte inviteType = proto.getByte();
               name            = proto.getString8();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireCreateInvitationCommand(handler, flags, handle, 
                                              meetingid, inviteType,
                                              name);
               }
               break;
            }
            case DSMPGenerator.OP_JOINMEETING: {
               meetingid      = proto.getInteger();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireJoinMeetingCommand(handler, flags, handle, meetingid);
               }
               break;
            }
            
            case DSMPGenerator.OP_DROPINVITEE: {
               meetingid               = proto.getInteger();
               inviteid                = proto.getInteger();
               participantid           = proto.getInteger();
               boolean dropInvite      = (flags & (byte)0x01) != 0;
               boolean dropParticipant = (flags & (byte)0x02) != 0;
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireDropInviteeCommand(handler, flags, handle, meetingid, 
                                         dropInvite,      inviteid,
                                         dropParticipant, participantid);
               }
               break;
            }
            
            case DSMPGenerator.OP_IMAGERESIZE: {
               short x, y, w, h;
               meetingid   = proto.getInteger();
               x           = proto.getShort();
               y           = proto.getShort();
               w           = proto.getShort();
               h           = proto.getShort();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireImageResizeCommand(handler, flags, handle, meetingid,
                                         x, y, w, h);
               }
               break;
            }
            case DSMPGenerator.OP_CHATMESSAGE: {
               meetingid               = proto.getInteger();
               int toid                = proto.getInteger();
               String msg              = proto.getString16();
               boolean unicast         = (flags & (byte)0x01) != 0;
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireChatMessageCommand(handler, flags, handle, meetingid, 
                                         toid, msg, unicast);
               }
               break;
            }
            case DSMPGenerator.OP_MODIFYCONTROL: {
               meetingid               = proto.getInteger();
               participantid           = proto.getInteger();
               boolean addRemove       = (flags & (byte)0x01) != 0;
               boolean forceAdd        = (flags & (byte)0x02) != 0;
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireModifyControlCommand(handler, flags, handle, meetingid, 
                                          participantid, addRemove, forceAdd);
               }
               break;
            }
            case DSMPGenerator.OP_GETALLMEETINGS: {
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireGetAllMeetingsCommand(handler, flags, handle);
               }
               break;
            }
            case DSMPGenerator.OP_MOUSEUPDATE: {
               short x, y;
               byte button;
               meetingid               = proto.getInteger();
               button                  = proto.getByte();
               x                       = proto.getShort();
               y                       = proto.getShort();
               boolean buttonEv        = (flags & (byte)0x01) != 0;
               boolean pressRelease    = (flags & (byte)0x02) != 0;
               boolean realMotion      = (flags & (byte)0x04) != 0;
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireMouseUpdateCommand(handler, flags, handle, meetingid, 
                                         buttonEv, pressRelease, realMotion,
                                         button, x, y);
               }
               break;
            }
            case DSMPGenerator.OP_KEYUPDATE: {
               short x, y;
               int javakeysym;
               meetingid               = proto.getInteger();
               x                       = proto.getShort();
               y                       = proto.getShort();
               javakeysym              = proto.getInteger();
               boolean pressRelease    = (flags & (byte)0x01) != 0;
               boolean keyCodeOrChar   = (flags & (byte)0x02) != 0;
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireKeyUpdateCommand(handler, flags, handle, meetingid, 
                                       pressRelease, keyCodeOrChar, x, y, 
                                       javakeysym);
               }
               break;
            }
            
            case DSMPGenerator.OP_FRAMEUPDATE: {
               short x, y, w, h;
               meetingid  = proto.getExtraInt();
               x          = proto.getShort();
               y          = proto.getShort();
               w          = proto.getShort();
               h          = proto.getShort();
               CompressInfo ci = proto.getDataAtCursor();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireFrameUpdateCommand(handler, flags, handle, 
                                         meetingid, x, y, w, h,
                                         (flags & (byte)0x01) != 0, ci.buf,
                                         ci.ofs, ci.len);
               }
               break;
            }
            case DSMPGenerator.OP_MULTIFRAMEUPDATE: {
               meetingid  = proto.getExtraInt();
               int numframes   = proto.getInteger();
               CompressInfo ci = proto.getDataAtCursor();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireMultiFrameUpdateCommand(handler, flags, handle, 
                                              (flags & (byte)0x01) != 0, 
                                              meetingid, proto);
               }
               break;
            }
            case DSMPGenerator.OP_FRAMEEND:
               meetingid     = proto.getInteger();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireFrameEnd(handler, flags, handle, meetingid);
               }
               break;
               
            case DSMPGenerator.OP_FROZENMODE:
               meetingid     = proto.getInteger();
               proto.verifyCursorDone();
               if (doDispatch) {
                  boolean frozen = (flags & (byte)0x01) != 0;
                  fireFrozenMode(handler, flags, handle, meetingid, frozen);
               }
               break;
            
            case DSMPGenerator.OP_PROTOCOLREST:
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireProtocolRest(handler, flags, handle);
               }
               break;
               
            case DSMPGenerator.OP_TRANSFERMODERATOR:
               meetingid     = proto.getInteger();
               participantid = proto.getInteger();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireTransferModeratorCommand(handler, flags, handle,
                                               meetingid, participantid);
               }
               break;
            case DSMPGenerator.OP_ASSIGNOWNERSHIP:
               meetingid     = proto.getInteger();
               participantid = proto.getInteger();
               proto.verifyCursorDone();
               if (doDispatch) {
                  boolean addOrRemove = (flags & (byte)0x01) != 0;
                  fireAssignOwnershipCommand(handler, flags, handle,
                                             meetingid, participantid, 
                                             addOrRemove);
               }
               break;
            case DSMPGenerator.OP_STARTSHARE:
               meetingid     = proto.getInteger();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireStartShareCommand(handler, flags, handle, meetingid);
               }
               break;
            case DSMPGenerator.OP_STOPSHARE:
               meetingid     = proto.getInteger();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireStopShareCommand(handler, flags, handle, meetingid);
               }
               break;
               
            case DSMPGenerator.OP_CREATE_GROUP: {
               String group = proto.getString16().toLowerCase();
               byte   visibility  = proto.getByte();
               byte   listability = proto.getByte();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireCreateGroupCommand(handler, flags, handle, 
                                         group, visibility, listability);
               }
               break;
            }
            case DSMPGenerator.OP_DELETE_GROUP: {
               String group = proto.getString16().toLowerCase();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireDeleteGroupCommand(handler, flags, handle, 
                                         group);
               }
               break;
            }
            case DSMPGenerator.OP_MODIFY_GROUP_ACL: {
               String group = proto.getString16().toLowerCase();
               String usern = proto.getString16().toLowerCase();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireModifyGroupAclCommand(handler, flags, handle, 
                                            (flags & (byte)1) != (byte)0,
                                            (flags & (byte)2) != (byte)0,
                                            group, usern);
               }
               break;
            }
            case DSMPGenerator.OP_MODIFY_GROUP_ATTRIBUTES: {
               String group = proto.getString16().toLowerCase();
               byte   visibility  = proto.getByte();
               byte   listability = proto.getByte();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireModifyGroupAttributeCommand(handler, flags, handle, 
                                                  group, visibility,
                                                  listability);
               }
               break;
            }
            case DSMPGenerator.OP_QUERY_GROUPS: {
               String group = proto.getString16().toLowerCase();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireQueryGroupsCommand(handler, flags, handle, 
                                         (flags & (byte)1) != (byte)0,
                                         (flags & (byte)2) != (byte)0,
                                         (flags & (byte)4) != (byte)0,
                                         group);
               }
               break;
            }
            
            case DSMPGenerator.OP_PLACECALL: {
               meetingid = proto.getInteger();
               String userid = proto.getString8();
               String password = proto.getString8();
               proto.verifyCursorDone();
               if (doDispatch) {
                  firePlaceCallCommand(handler, flags, handle, 
                                       (flags & (byte)1) != 0,
                                       meetingid, userid, password);
               }
               break;
            }
            case DSMPGenerator.OP_ACCEPTCALL: {
               meetingid    = proto.getInteger();
               int callerid = proto.getInteger();
               String salt  = proto.getString8();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireAcceptCallCommand(handler, flags, handle,
                                        meetingid, callerid, salt);
               }
               break;
            }
            case DSMPGenerator.OP_QUERY_MEETING_OPTIONS: {
               meetingid = proto.getInteger();
               String specificOpt = proto.getString8();
               
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireQueryMeetingOptionsCommand(handler, flags, handle,
                                                 flags != 0, 
                                                 meetingid, specificOpt);
               }
               break;
            }
            case DSMPGenerator.OP_SET_MEETING_OPTIONS: {
               meetingid = proto.getInteger();
               short numOpts = proto.getShort();
               
               Hashtable options = new Hashtable();
               for(int i=0; i < numOpts; i++) {
                  String key = proto.getString8();
                  String val = proto.getString16();
                  options.put(key, val);
               }
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireSetMeetingOptionsCommand(handler, flags, handle,
                                               meetingid, options);
               }
               break;
            }
            case DSMPGenerator.OP_GET_MEETING_URL: {
               meetingid = proto.getInteger();
               proto.verifyCursorDone();
               if (doDispatch) {
                  fireGetMeetingURLCommand(handler, flags, handle, 
                                           (flags & (byte)1) != 0,
                                           (flags & (byte)2) != 0,
                                           (flags & (byte)4) != 0,
                                           meetingid);
               }
               break;
            }
               
               
         }
         
      } else {
         throw new InvalidProtocolException(opcode + ": Bad Opcode");
      }
   }
}
