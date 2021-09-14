package oem.edge.ed.odc.cntl.metrics;
import java.sql.Connection;
import java.sql.Timestamp;
public class Invitationdata extends Commondata {
   protected String    partyEdgeId = null;
   protected String    partySesId  = null;
   protected String    partyCompany= null;
   protected String    partyCountry= null;
   protected String    state       = null;
   protected String    invId       = null;
   protected long      timeDropped = 0;
   protected String    meetingId   = null;
   
   public Invitationdata() {
      long ctm = System.currentTimeMillis();
      setStartTime(ctm);
      setInvitationId("Inv_" + ctm);
   }
   
   public void setMeetingId(String v)      { meetingId = v;     }
   public String getMeetingId()            { return meetingId;  }      
   
   public String getInvitationId()           { return invId;       }
   public String getPartySessionId()         { return partySesId;  }
   public String getPartyEdgeId()            { return partyEdgeId; }
   public String getPartyCompany()           { return partyCompany;}
   public String getPartyCountry()           { return partyCountry;}
   public String getState()                  { return state;       }
   public long   getTimeDropped()            { return timeDropped; }
   public void   setPartySessionId(String v) { partySesId  = v;    }
   public void   setPartyEdgeId(String v)    { partyEdgeId = v;    }
   public void   setPartyCompany(String v)   { partyCompany= v;    }
   public void   setPartyCountry(String v)   { partyCountry= v;    }
   public void   setState(String v)          { state       = v;    }
   public void   setTimeDropped(long v)      { timeDropped = v;    }
   public void   setInvitationId(String v)   { invId       = v;    }
   
   public void fixEndTime(long t) {
      super.fixEndTime(t);
      if (timeDropped == 0 && state != null && 
          state.equalsIgnoreCase("Accepted")) {
         timeDropped = t;
      } else if (timeDropped != 0 && 
                 (state == null || !state.equalsIgnoreCase("Accepted"))) {
         timeDropped = 0;
      }
                                         
   }
   
   
   public void dooutput(StringBuffer ret, Connection conn, String instance, 
                        String table, int nest) {
      super.dooutput(ret, conn, instance, table, nest);
      write(ret, conn, nest, "partyedgeid",  partyEdgeId);
      write(ret, conn, nest, "partycompany", partyCompany);
      write(ret, conn, nest, "partycountry", partyCountry);
      write(ret, conn, nest, "partysessid",  partySesId);
      write(ret, conn, nest, "invstate",     state);
      write(ret, conn, nest, "meetingid",    meetingId);      
   
      if (timeDropped != 0) {
         write(ret, conn, nest, "timedropped",  new Timestamp(timeDropped));
         write(ret, conn, nest, "meetingDelta", 
               ""+(timeDropped-endTime));
      }
      
      flushDB2(conn, instance, Desktopdata.InviteTable);
   }
}
