package oem.edge.ed.odc.cntl.metrics;
import java.util.*;
import java.sql.Connection;

public class ODCdata extends CommonProtodata {
   protected Hashtable meetings = null;
   protected String    svctype = "ODC";
   
   public ODCdata() {
      super();
   }
   public ODCdata(String svc) {
      super();
      svctype = svc;
   }
   
   public void fixEndTime(long t) {
      super.fixEndTime(t);
      
      if (meetings != null) {
         Enumeration enum = meetings.elements();
         int idx = 0;
         while(enum.hasMoreElements()) {
            Commondata cd = (Commondata)enum.nextElement();
            cd.fixEndTime(t);
         }
      }
   }
   
   public int getNumMeetings() {
      return (meetings!=null)?meetings.size():0;
   }
   
   public void addMeeting(Meetingdata v) {
      if (meetings == null) meetings = new Hashtable();
      v.setMeetingId(""+(meetings.size()+1));
      v.setKey(getKey());
      meetings.put(v.getMeetingId(), v);
   }
   public Meetingdata getLastMeeting() {
      Meetingdata ret = null;
      if (meetings != null) 
         ret = (Meetingdata)meetings.get(""+(meetings.size()));
      return ret;
   }
   public Meetingdata getMeeting(String v) {
      Meetingdata ret = null;
      if (meetings != null) ret = (Meetingdata)meetings.get(v);
      return ret;
   }
   
   public void dooutput(StringBuffer ret, Connection conn, String instance, 
                        String table, int nest) {
      
      super.dooutput(ret, conn, instance, Desktopdata.ServiceTable, nest);
      write(ret, conn, nest, "servicetype",      svctype);
      showElements(ret, meetings, conn, instance, Desktopdata.MeetingTable,
                   nest, "odcmeetings");
      flushDB2(conn, instance, Desktopdata.ServiceTable);
   }
}
