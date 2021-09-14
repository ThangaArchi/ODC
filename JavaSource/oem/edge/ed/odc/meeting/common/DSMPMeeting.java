package oem.edge.ed.odc.meeting.common;

import  oem.edge.ed.odc.dsmp.common.*;

import java.lang.*;
public class DSMPMeeting {
   protected int     inviteid;
   protected int     loginid;
   protected boolean isProj;
   protected byte    inviteType;
   protected int     meetingid;
   protected String  title;
   protected String  owner;
   protected String  classification;
   public DSMPMeeting(int invid,  int logid, byte invitetype,
                      int meetid, String t,  String o, String c) {
      inviteid        = invid;
      loginid         = logid;
      inviteType      = invitetype;
      meetingid       = meetid;
      title           = t;
      owner           = o;
      classification  = c;
   }
   
   public int     getInviteId()        { return inviteid;       }
   public int     getMeetingId()       { return meetingid;      }
   public int     getLoginId()         { return loginid;        }
   public boolean isUser()             { 
      return inviteType == DSMPGenerator.STATUS_NONE; 
   } 
   public boolean isProject()          { 
      return inviteType == DSMPGenerator.STATUS_PROJECT; 
   } 
   public boolean isGroup()            { 
      return inviteType == DSMPGenerator.STATUS_GROUP; 
   } 
   public byte    getInviteType()      { return inviteType;     }
   public String  getTitle()           { return title;          }
   public String  getOwner()           { return owner;          }
   public String  getClassification()  { return classification; }
   
   public String toString() {
      return "DSMPMeeting: Owner[" + owner + "] inviteType: " + inviteType + 
           "\n             Title[" + title + "]\n" +
           "\n             Classification[" + classification + "]\n" +
             "             Loginid[" + loginid + "] meetid[" + meetingid + 
         "] Inviteid[" + inviteid + "]";
   }
}
