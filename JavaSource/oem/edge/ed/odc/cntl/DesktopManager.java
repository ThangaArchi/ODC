package oem.edge.ed.odc.cntl;

import java.io.*;
import java.net.*;
import java.util.*;
import java.beans.*;
import java.sql.Connection;
import com.ibm.as400.webaccess.common.*;
import oem.edge.ed.odc.model.*;
import oem.edge.ed.odc.util.*;
import oem.edge.ed.odc.view.*;
import oem.edge.ed.odc.cntl.metrics.*;
import oem.edge.ed.odc.tunnel.common.*;
import oem.edge.ed.odc.tunnel.servlet.*;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 1998,2004,2005,2006		                 */ 
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

/**
 * Insert the type's description here.
 * Creation date: (07/25/00 14:21:08)
 * @author: Administrator
 */
public class DesktopManager {

  // interface for accessing persistant data
   private static DBDataManager DataStore = null;

  // single instance of this
   private static DesktopManager Myself = null;

  // application properties
   ReloadingProperty deskprops = null;
   
   boolean doDB2Metrics = false;
   
   private String  usageLog     = null;
   
   
/**
 * Insert the method's description here.
 * Creation date: (08/02/00 08:31:15)
 */
   protected DesktopManager() {
      super();
   }
   
/**
 * Insert the method's description here.
 * Creation date: (08/24/00 17:32:58)
 * @return boolean
 * @param prop java.util.Properties
 */
   public DesktopView bindDesktop(Properties prop) {
      DesktopView dv = 
         DataStore.bindDesktop(prop.getProperty(DesktopCommon.DESKTOP_ID),
                               prop.getProperty(DesktopCommon.OWNER),
                               prop.getProperty(DesktopCommon.DISPLAY),
                               prop.getProperty(DesktopCommon.COOKIE),
                               prop.getProperty(DesktopCommon.ALIAS),
                               prop.getProperty(DesktopCommon.BUMPHOST),
                               prop.getProperty(DesktopCommon.BUMPPORT));
      return dv;
   }
   
   public boolean createDesktop(DesktopView dt) {
      return DataStore.addDesktop(dt);
   }
   
   public boolean destroyDesktop(Properties prop, HttpTunnelSession s) {

      return destroyDesktop(prop.getProperty(DesktopCommon.DESKTOP_ID), s);
   }
   
   public DesktopView getDesktop(String id) {
      return (DesktopView)DataStore.getDesktop(id);
   }
   
   public Vector getDesktopByUser(String user) {
      return DataStore.getDesktopByUser(user);
   }
      
/**
 * Insert the method's description here.
 * Creation date: (08/24/00 17:32:58)
 * @return boolean
 * @param prop java.util.Properties
 */
   public boolean destroyDesktop(String id, HttpTunnelSession session) {

      DesktopView view = DataStore.getDesktop(id);
         
      if ( view != null ) {

        // remove the desktop
         DataStore.removeDesktop(view.getKey());
        // DebugPrint.println("At destroy desktop: " + view.getKey());
        
         DebugPrint.println(DebugPrint.INFO,
                            "Destroying Desktop. Print final Stats!");
                            
         String o = null;
         if (session == null) {
            DebugPrint.println(DebugPrint.WARN,
                               " ... no session for ID!");
         } else {
            Desktopdata desktopdata = (Desktopdata)session.getUserData();
            if (desktopdata == null) {
               DebugPrint.printlnd("DM: destroyDesktop: id= " + id + 
                                   ": No desktopdata!");
            } else {
            
               SMStats stats = session.getTotalStats();
               desktopdata.setMaxPing((int)stats.getPingMaxMS());
               desktopdata.setMinPing((int)stats.getPingMinMS());
               desktopdata.setAvgPing((int)stats.getPingAvgMS());

               desktopdata.setProtoFromClient(session.getTotIn());
               desktopdata.setEndTime(System.currentTimeMillis());
               desktopdata.setProtoToClient(session.getTotOut());
               o = desktopdata.toString();
               DebugPrint.println(DebugPrint.INFO, "Usage:\n" + o);
            }
            
           // If we have the DBDataManager as our datastore, AND we are
           //  collecting metrics in DB2
            if (doDB2Metrics) {
               Connection c = null;
               DBConnection dbconn = DBSource.getDBConnection("EDODC");
               try {
                  c = dbconn.getConnection();
                  desktopdata.toDB2(c, "edesign");
               } catch(Throwable t) {
                  DebugPrint.printlnd(DebugPrint.ERROR, 
                                     "Error writing metrics to DB2!");
                  DebugPrint.println(DebugPrint.ERROR, t);
               } finally {
                  if (dbconn != null && c != null) dbconn.returnConnection(c);
               }
            }
                        
            if (usageLog != null && o != null) {
               synchronized(this) {
                  try {
                     
                     PrintWriter fw = 
                        new PrintWriter(
                           new FileWriter(usageLog, true));
                     fw.println(o);
                     fw.close();
                  } catch(IOException e) {
                     DebugPrint.println(DebugPrint.WARN,
                                        "Error logging usage to " + usageLog);
                  }
               }
            }
            
         }
         
         if (o == null) {
            String owner  = view.getOwner();
            String edgeid = view.getEdgeId();
            String company = view.getCompany();
            String country = view.getCountry();
            Date ts = view.getStartTime();
            Date ed = new Date();
            o = "EdgeId[" + edgeid + 
               "] Company[" + company + 
               "] Country[" + country  + 
               "] User["    + owner + 
               "] FN["      + view.getFirstName() +
               "] LN["      + view.getLastName()  +
               "] Proj["    + view.getProjects()  +
               "] email["   + view.getFirstName() + "]";
            if (ts == null) {
               o = o + " Start[????] End[" + ed.toString() + "] Delta: [????]";
            } else {
               ts = new Date(ts.getTime());
               o = o + " Start[" + ts.toString() +
                  "] End[" + ed.toString() + 
                  "] Delta[" + SMStats.msToTime(ed.getTime()-ts.getTime()) +
                  "]";
            }
            
            DebugPrint.println(DebugPrint.INFO, "Usage:\n" + o);
         }
		
         return true;
      } else {
         return false;
      }
   }
   
/**
 * Insert the method's description here.
 * Creation date: (8/26/00 2:57:32 PM)
 * @return com.ibm.edesign.collaboration.cntl.DesktopManager
 */
   public static DesktopManager getDesktopManager() {
      if ( Myself == null ) {
         Myself = new DesktopManager();
      }
      return Myself;
   }
   
  // JMC 3/13/01 - Give tunnel access to props
   public String getDesktopProperty(String in) {
      String ret = null;
      ret = deskprops.getProperty(in);
      return ret;
   }
   
/**
 * Insert the method's description here.
 * Creation date: (09/05/00 15:58:09)
 */
   public void init(ReloadingProperty prop, String domainIP) {
      deskprops = prop;
      
      DataStore = new DBDataManager();
      

      try {
         doDB2Metrics = 
            getDesktopProperty("edodc.metricsInDB2").equalsIgnoreCase("true");
      } catch(Throwable t) {
         doDB2Metrics = false;
         DebugPrint.println(DebugPrint.WARN,
                            "edodc.metricsInDB2 not set, assume false");
      }
      
      try {
         String s = getDesktopProperty("edodc.usageLog");
         usageLog = s + "-" + InetAddress.getLocalHost().getHostName();
      } catch(Throwable t) {
         DebugPrint.println(DebugPrint.WARN,
                "edodc.usageLog property not found, No usage will be logged!");
         usageLog = null;
      }

      DataStore.init(deskprops);
   }
/**
 * Insert the method's description here.
 * Creation date: (08/30/00 17:05:52)
 * @return boolean
 * @param id java.lang.String
 */
   public boolean isValidDesktop(String id) {
      return DataStore.getDesktop(id) != null;
   }
   
   public boolean setRemotePort(String desktopId, String rhost, int port) {
      int index = 0;
	
      DesktopView dt = DataStore.setRemotePort(desktopId, rhost, port);
      if ( dt == null ) {
         DebugPrint.println(DebugPrint.WARN, 
                            "DM.setRemotePort: How did you get here? Desktop "
                            + desktopId + " does not exist!");
         return false;
      }
      return true;
   }
}
