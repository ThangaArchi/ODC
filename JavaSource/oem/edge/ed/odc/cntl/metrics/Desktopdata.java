package oem.edge.ed.odc.cntl.metrics;
import oem.edge.ed.odc.view.*;
import oem.edge.ed.odc.tunnel.common.*;
import oem.edge.ed.odc.tunnel.servlet.*;
import oem.edge.ed.odc.cntl.DesktopServlet;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.sql.Connection;
import java.lang.reflect.*;

public class Desktopdata extends Commondata implements ActionListener {
   protected Hashtable   odcSessions   = null;
   protected Hashtable   dshSessions   = null;
   protected Hashtable   eduSessions   = null;
   protected Hashtable   genSessions   = null;
   protected DesktopView desktop       = null;
   protected int         minping       = -1;
   protected int         maxping       = -1;
   protected int         avgping       = -1;
   public    static final String      DesktopTable  = "METRICDT";
   public    static final String      ServiceTable  = "METRICSERVICE";
   public    static final String      InviteTable   = "METRICINVITE";
   public    static final String      MeetingTable  = "METRICMEETING";
   
   public    static java.lang.reflect.Method tunnelShuttingDown = null;
         
   public Desktopdata() {
      long ctm = System.currentTimeMillis();
      setStartTime(ctm);
      setKey("Inv_" + ctm);
   }
   
   public void setMaxPing(int v) { maxping = v; }
   public void setMinPing(int v) { minping = v; }
   public void setAvgPing(int v) { avgping = v; }
   
   public int getMaxPing(int v)  { return maxping; }
   public int getMinPing(int v)  { return minping; }
   public int getAvgPing(int v)  { return avgping; }
   
  // We get this primarily because the tunnel is shutting down
   public void actionPerformed(ActionEvent ae) {
      String cmd = ae.getActionCommand();
      
     // This is thrown when SessionManager is asked to Shutdown.
      if (cmd.equalsIgnoreCase("TunnelShutdown")) {
      
        // Use reflection here to get rid of DesktopServlet dep ... we use
        //  this class in Meeting Server ...and don't want to ship the world
         try {
            if (tunnelShuttingDown == null) {
               Class stringclass = Class.forName("java.lang.String");
               Class dsrvclass   
                  = Class.forName("oem.edge.ed.odc.cntl.DesktopServlet");
               Class classparms[]    = new Class[2];
               classparms[0] = Class.forName(
                  "oem.edge.ed.odc.tunnel.servlet.HttpTunnelSession");
               classparms[1] = java.lang.String.class;
               tunnelShuttingDown = dsrvclass.getMethod("tunnelShuttingDown", 
                                                        classparms);
            }
            
            Object parmarr[] = new Object[2];
            parmarr[0] = ae.getSource();
            parmarr[1] = cmd;
            tunnelShuttingDown.invoke(null, parmarr);
         } catch(Exception ee) {
            DebugPrint.printlnd(DebugPrint.ERROR,
                                "Error while doing actionPerformed to notify DTServlet of tunnel death");
            DebugPrint.printlnd(DebugPrint.ERROR, ee);
         }
      }
   }
   
  // Should probably use Vectors ... 
   public Hashtable getODCSessions() { return odcSessions; }
   public Hashtable getDSHSessions() { return dshSessions; }
   public Hashtable getEDUSessions() { return eduSessions; }
   public Hashtable getGenericSessions() { return genSessions; }
   
   public void addODCSession(ODCdata v) {
      if (odcSessions == null) odcSessions = new Hashtable();
      v.setKey(getKey());
      odcSessions.put(""+(odcSessions.size()+1), v);
   }
   
   public void addDSHSession(Hostingdata v) {
      if (dshSessions == null) dshSessions = new Hashtable();
      v.setKey(getKey());
      dshSessions.put(""+(dshSessions.size()+1), v);
   }
   
   public void addEDUSession(Educationdata v) {
      if (eduSessions == null) eduSessions = new Hashtable();
      v.setKey(getKey());
      eduSessions.put(""+(eduSessions.size()+1), v);
   }
   public void addGenericSession(GenericService v) {
      if (genSessions == null) genSessions = new Hashtable();
      v.setKey(getKey());
      genSessions.put(""+(genSessions.size()+1), v);
   }
   
   public ODCdata getODCSession(String v) {
      ODCdata ret = null;
      if (odcSessions != null) ret = (ODCdata)odcSessions.get(v);
      return ret;
   }
   public ODCdata getLastODCSession() {
      ODCdata ret = null;
      if (odcSessions != null) 
         ret = (ODCdata)odcSessions.get(""+(odcSessions.size()));
      return ret;
   }
   
   public Hostingdata getDSHSession(String v) {
      Hostingdata ret = null;
      if (dshSessions != null) ret = (Hostingdata)dshSessions.get(v);
      return ret;
   }
   
   public Hostingdata getLastDSHSession() {
      Hostingdata ret = null;
      if (dshSessions != null) 
         ret = (Hostingdata)dshSessions.get(""+(dshSessions.size()));
      return ret;
   }
   
   public Educationdata getEDUSession(String v) {
      Educationdata ret = null;
      if (eduSessions != null) ret = (Educationdata)eduSessions.get(v);
      return ret;
   }
   public Educationdata getLastEDUSession() {
      Educationdata ret = null;
      if (eduSessions != null) 
         ret = (Educationdata)eduSessions.get(""+(eduSessions.size()));
      return ret;
   }
   
   public GenericService getGenericSession(String v) {
      GenericService ret = null;
      if (genSessions != null) ret = (GenericService)genSessions.get(v);
      return ret;
   }
   public GenericService getLastGenericSession() {
      GenericService ret = null;
      if (genSessions != null) 
         ret = (GenericService)genSessions.get(""+(genSessions.size()));
      return ret;
   }
   
   public DesktopView getDesktop() { 
      return desktop;
   }
   
   public void setDesktop(DesktopView dt) { 
      desktop = dt; 
      if (dt != null) {
         java.sql.Timestamp ts = dt.getStartTime();
         if (ts != null) {
            setStartTime(ts.getTime());
         }
         setKey(dt.getKey());
      }
   }
      
   public void fixEndTime(long t) {
      super.fixEndTime(t);
      
      if (odcSessions != null) {
         Enumeration enum = odcSessions.elements();
         while(enum.hasMoreElements()) {
            Commondata cd = (Commondata)enum.nextElement();
            cd.fixEndTime(t);
         }
      }
      if (dshSessions != null) {
         Enumeration enum = dshSessions.elements();
         while(enum.hasMoreElements()) {
            Commondata cd = (Commondata)enum.nextElement();
            cd.fixEndTime(t);
         }
      }
      if (eduSessions != null) {
         Enumeration enum = eduSessions.elements();
         while(enum.hasMoreElements()) {
            Commondata cd = (Commondata)enum.nextElement();
            cd.fixEndTime(t);
         }
      }
      if (genSessions != null) {
         Enumeration enum = genSessions.elements();
         while(enum.hasMoreElements()) {
            Commondata cd = (Commondata)enum.nextElement();
            cd.fixEndTime(t);
         }
      }
   }
      
   public void dooutput(StringBuffer ret, Connection conn, String instance, 
                        String table, int nest) {
      
      if (desktop == null) {
         DebugPrint.printlnd(DebugPrint.WARN, 
                             "Desktopdata: Desktop value NULL, can't toDB2!");
         return;
      }
      
      if (endTime == 0) {
         endTime = System.currentTimeMillis();
      }
      fixEndTime(endTime);
      
      if (conn == null) {
         String nstop  = getNestString(nest);
         ret.append(nstop).append("<desktop>\n");
      }
      
      nest++;
      super.dooutput(ret, conn, instance, table, nest);
      
      write(ret, conn, nest, 
            "edgeid",  desktop.getEdgeId());
      write(ret, conn, nest, 
            "company", desktop.getCompany());
      write(ret, conn, nest, 
            "country", desktop.getCountry());
      write(ret, conn, nest, 
            "state",   desktop.getState());
      write(ret, conn, nest, 
            "email",   desktop.getEmailAddr());
      write(ret, conn, nest, 
            "fname",   desktop.getFirstName());
      write(ret, conn, nest, 
            "lname",   desktop.getLastName());
      write(ret, conn, nest, 
            "projects",desktop.getProjects());
      write(ret, conn, nest, 
            "rhost",   desktop.getRemoteHost());
      write(ret, conn, nest, 
            "minping", new Integer(minping));
      write(ret, conn, nest, 
            "maxping", new Integer(maxping));
      write(ret, conn, nest, 
            "avgping", new Integer(avgping));
      
      if (odcSessions != null) {
         int  numODCMeetings = 0;
         long totBytes    = 0;
         int  totDuration = 0;
         
         Enumeration enum = odcSessions.elements();
         int idx = 0;
         while(enum.hasMoreElements()) {
            ODCdata dd      = (ODCdata)enum.nextElement();
            numODCMeetings += dd.getNumMeetings();
            totBytes       += dd.getTotalProto();
            totDuration    += dd.getTimeDelta();
         }
         
         write(ret, conn, nest,
               "numODCSess",   new Integer(odcSessions.size()));
         write(ret, conn, nest,
               "totODCDelta",  new Integer(totDuration));
         write(ret, conn, nest,
               "totODCBytes",  new Long(totBytes));
         write(ret, conn, nest,
               "numODCMeet",   new Integer(numODCMeetings));
      }
      
      int numdshsess  = 0;
      int totdshdelta = 0;
      int totdshbytes = 0;
      
      if (dshSessions != null) {
         long totBytes    = 0;
         int  totDuration = 0;
         
         Enumeration enum = dshSessions.elements();
         int idx = 0;
         while(enum.hasMoreElements()) {
            CommonProtodata dd  = (CommonProtodata)enum.nextElement();
            totdshbytes    += dd.getTotalProto();
            totdshdelta    += dd.getTimeDelta();
         }
         
         numdshsess += dshSessions.size();
      }
      if (eduSessions != null) {
         long totBytes    = 0;
         int  totDuration = 0;
         
         Enumeration enum = eduSessions.elements();
         int idx = 0;
         while(enum.hasMoreElements()) {
            CommonProtodata dd  = (CommonProtodata)enum.nextElement();
            totBytes       += dd.getTotalProto();
            totDuration    += dd.getTimeDelta();
         }
         
         write(ret, conn, nest,
               "numEDUSess",   new Integer(eduSessions.size()));
         write(ret, conn, nest,
               "totEDUDelta",  new Integer(totDuration));
         write(ret, conn, nest,
               "totEDUBytes",  new Long(totBytes));
      }
      if (genSessions != null) {
         long totBytes    = 0;
         int  totDuration = 0;
         
         Enumeration enum = genSessions.elements();
         int idx = 0;
         while(enum.hasMoreElements()) {
            CommonProtodata dd  = (CommonProtodata)enum.nextElement();
            totdshbytes    += dd.getTotalProto();
            totdshdelta    += dd.getTimeDelta();
         }
         
         numdshsess += genSessions.size();
      }
      
      if (numdshsess != 0) {
         write(ret, conn, nest,
               "numDSHSess",   new Integer(numdshsess));
         write(ret, conn, nest,
               "totDSHDelta",  new Integer(totdshdelta));
         write(ret, conn, nest,
               "totDSHBytes",  new Long(totdshbytes));
      }
      
      
      showElements(ret, odcSessions, conn, instance, table,
                   nest, "odcsessions");
      showElements(ret, dshSessions, conn, instance, table,
                   nest, "dshsessions");
      showElements(ret, eduSessions, conn, instance, table,
                   nest, "edusessions");
      showElements(ret, genSessions, conn, instance, table,
                   nest, "gensessions");
                   
                        
      flushDB2(conn, instance, DesktopTable);
      if (conn == null) {
         nest--;
         String nstop  = getNestString(nest);
         ret.append(nstop).append("</desktop>\n");
      }
   }
                        
   public void toDB2(Connection conn, String instance) {
      
      if (desktop == null) {
         DebugPrint.printlnd(DebugPrint.WARN, 
                             "Desktopdata: Desktop value NULL, can't toDB2!");
         return;
      }
      
      StringBuffer ret = new StringBuffer();
      
      dooutput(ret, conn, instance, "", 0);
   }
   
   
   public static void main(String args[]) {
      Desktopdata dtd = new Desktopdata();
      DesktopView dv  = new DesktopView();
      dv.setEdgeId("edgeidval");
      dv.setCompany("IBM");
      dv.setCountry("USA");
      dv.setState("NY");
      dv.setFirstName("Ackmed");
      dv.setLastName("Lavenstein");
      dv.setEmailAddr("al@aol.com");
      dv.setProjects("p1:p2:p3");
      dv.setKey("Unique Key");
      dv.setStartTime(new java.sql.Timestamp(System.currentTimeMillis()
                                             -65000));
      
      dtd.setDesktop(dv);
      
      ODCdata odcdata   = new ODCdata();
      odcdata.setKey("odckey1");
      dtd.addODCSession(odcdata);
      odcdata.setStartTime(System.currentTimeMillis()-60000);
      odcdata.setEndTime(System.currentTimeMillis());
      odcdata.setProject("p1");
      
      Meetingdata meet1 = new Meetingdata();
      
      meet1.setKey("meetingKey1");
      meet1.setNumConnections(3);
      meet1.setProtoFromClient(33874);
      meet1.setProtoToClient(4948022);
      meet1.setStartTime(System.currentTimeMillis()-60000);
      meet1.setEndTime(System.currentTimeMillis());
      odcdata.addMeeting(meet1);
      
      Meetingdata meet2 = new Meetingdata();
      
      meet2.setKey("meetingKey2");
      meet2.setNumConnections(1);
      meet2.setProtoFromClient(363874);
      meet2.setProtoToClient(498022);
      meet2.setStartTime(System.currentTimeMillis()-60000);
      meet2.setEndTime(System.currentTimeMillis());
      odcdata.addMeeting(meet2);
      
      System.out.println(dtd.toString());
   }
}
