package oem.edge.ed.odc.cntl.metrics;
import java.util.*;
import java.sql.Connection;

public class Meetingdata extends CommonProtodata {
   protected Hashtable invitees = null; 
   protected String meetingId   = null;
   
   public void fixEndTime(long t) {
      super.fixEndTime(t);
      
      if (invitees != null) {
         Enumeration enum = invitees.elements();
         int idx = 0;
         while(enum.hasMoreElements()) {
            Commondata cd = (Commondata)enum.nextElement();
            cd.fixEndTime(t);
         }
      }
   }
   
   public void setMeetingId(String v)      { meetingId = v;     }
   public String getMeetingId()            { return meetingId;  }      
   
   public Invitationdata removeInvitation(String v) {
      Invitationdata ret = null;
      if (invitees != null) {
         ret = (Invitationdata)invitees.get(v);
         invitees.remove(v);
      }
      return ret;
   }
   public void addInvitation(Invitationdata v) {
      if (invitees == null) invitees = new Hashtable();
      invitees.put(v.getInvitationId(), v);
      v.setMeetingId(getMeetingId());
      v.setKey(getKey());
   }
   public Invitationdata getInvitation(String v) {
      Invitationdata ret = null;
      if (invitees != null) ret = (Invitationdata)invitees.get(v);
      return ret;
   }
   public Invitationdata getInvitationByEdgeId(String v) {
      Invitationdata ret = null;
      try {
         Enumeration enum = invitees.elements();
         while(enum.hasMoreElements()) {
            Invitationdata iret = (Invitationdata)enum.nextElement();
            if (iret.getPartyEdgeId().equals(v)) {
               if (ret == null) ret = iret;
               else if (ret.getTimeDropped() == 0) {
                  if (ret.getState().equals("Accepted")) {
                     ret = iret;
                  }
               }
            }
         }
      } catch(Exception e) {
      }
      return ret;
   }
   
   public void dooutput(StringBuffer ret, Connection conn, String instance,
                        String table, int nest) {
      
      super.dooutput(ret, conn, instance, table, nest);
      
      write(ret, conn, nest, "meetingid", meetingId);      
      
      try {
         Enumeration enum = invitees.elements();
         while(enum.hasMoreElements()) {
            Invitationdata iret = (Invitationdata)enum.nextElement();
            if (iret.getTimeDropped() == 0 && 
                iret.getState().equals("Accepted")) {
               iret.setTimeDropped(System.currentTimeMillis());
            }
         }
      } catch(Exception e) {
      }
      
      showElements(ret, invitees, conn, instance, table,
                   nest, "invitees");
      flushDB2(conn, instance, Desktopdata.MeetingTable);
   }
}
