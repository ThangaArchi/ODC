package oem.edge.ed.odc.cntl;
import oem.edge.ed.odc.cntl.*;
import oem.edge.ed.odc.tunnel.common.*;
import oem.edge.ed.odc.cntl.metrics.*;
import oem.edge.ed.odc.view.*;
import java.util.*;
import java.sql.*;
import javax.sql.*;
import javax.naming.*;


public class twist {

   static Connection persistentConnection = null;
   static String DBurl="jdbc:db2:edodcdb";
   static String DBdriver = "COM.ibm.db2.jdbc.app.DB2Driver";
   static String Userid="db2inst1";
   static String Password="db2admin";
   static String dbOldInstance = "db2inst1";
   static String dbNewInstance = "db2inst1";

   static private Connection getConnection() throws java.sql.SQLException {
      Connection conn = null;
      synchronized(Userid) {
         if (persistentConnection == null || persistentConnection.isClosed()) {
            persistentConnection = null;
            conn = DriverManager.getConnection(DBurl, Userid, Password);
         } else {
            conn = persistentConnection;
            persistentConnection = null;
         }
      }
      if (conn == null) {
         System.out.println("getConnection: conn == null!");
      }
      return conn;
   }
   
   static private void returnConnection(Connection conn) {
      if (conn != null) {
         synchronized(Userid) {
            if (persistentConnection == null) {
               persistentConnection = conn;
            } else {
               try { conn.close(); } catch(Throwable tt) {}
            }
         }
      }
   }
   
   static private void destroyConnection(Connection conn) {
      if (conn != null) {
         try { conn.close(); } catch(Throwable tt) {}
      }
   }

   public static long largestend = 0;
   
   public static boolean parseCommon(Commondata dd, String n, String v) {
      boolean ret = true;
      
     // Ignore these
      try {
         if        (n.equals("key") || n.equals("deltaTime")) {
            ;
         } else if (n.equals("starttime")) {
            long l = Long.parseLong(v);
            dd.setStartTime(l);
         } else if (n.equals("endtime")) {
            long l = Long.parseLong(v);
            if (l > largestend) largestend = l;
            dd.setEndTime(l);
         } else if (n.equals("toclient")) {
            long l = Long.parseLong(v);
            dd.setProtoToClient(l);
         } else if (n.equals("fromclient")) {
            long l = Long.parseLong(v);
            dd.setProtoFromClient(l);
         } else {
            ret = false;
         }
      } catch (NumberFormatException ne) {
         System.out.println("Error parsing value: " + n + "=" + v);
         ret = false;
      }
      return ret;
   }

   public static boolean parseDT(Desktopdata dd, String n, String v) {
      boolean ret = true;
      
      DesktopView dt = dd.getDesktop();
      
      if        (n.equals("edgeid")) {
         dt.setEdgeId(v);
      } else if (n.equals("company")) {
         dt.setCompany(v);
      } else if (n.equals("country")) {
         dt.setCountry(v);
      } else if (n.equals("state")) {
         dt.setState(v);
      } else if (n.equals("rhost")) {
         dt.setRemoteHost(v);
      } else if (n.equals("email")) {
         dt.setEmailAddr(v);
      } else if (n.equals("fname")) {
         dt.setFirstName(v);
      } else if (n.equals("lname")) {
         dt.setLastName(v);
      } else if (n.equals("projects")) {
         dt.setProjects(v);
      } else {
         ret = parseCommon(dd, n, v);
      }
      return ret;
   }
   
   
   public static void dohelp() {
      System.out.println(
         "Program to twist from 'old' flat Metric format to 'new'\n" +
         "beautiful format. Options:\n\n" +
         "\t-debug               - Set debug level\n"      +
         "\t-dburl      url      - Override default DB URL\n"      +
         "\t-dbdriver   driver   - Override default DB Driver\n"   +
         "\t-dbuser     user     - Override default DB userid\n"   +
         "\t-dbpw       passwd   - Override default DB password\n" +
         "\t-dboldinst  instance - Override default DB old instance\n" + 
         "\t-dbnewinst  instance - Override default DB new instance\n");
   }
   
   public static void main(String[] args) {
   
      for(int i=0; i < args.length; i++) {
         if        (args[i].equalsIgnoreCase("-dburl")) {
            DBurl = args[++i];
         } else if (args[i].equalsIgnoreCase("-debug")) {
            DebugPrint.setLevel(DebugPrint.DEBUG3);
         } else if (args[i].equalsIgnoreCase("-dbdriver")) {
            DBdriver = args[++i];
         } else if (args[i].equalsIgnoreCase("-dbuser")) {
            Userid = args[++i];
         } else if (args[i].equalsIgnoreCase("-dbpw")) {
            Password = args[++i];
         } else if (args[i].equalsIgnoreCase("-dboldinst")) {
            dbOldInstance = args[++i];
         } else if (args[i].equalsIgnoreCase("-dbnewinst")) {
            dbNewInstance = args[++i];
         } else if (args[i].equalsIgnoreCase("-?") || 
                    args[i].equalsIgnoreCase("-help")) {
            dohelp();
            return;
         } else {
            System.out.println("Bad parm for twist " + args[i]);
            dohelp();
            return;
         }
      }
   
      System.out.println("Starting twist with the following parms:\n" +
                         "\t-dburl      " + DBurl      + "\n" +
                         "\t-dbdriver   " + DBdriver   + "\n" +
                         "\t-dbuser     " + Userid     + "\n" +
                         "\t-dbpw       " + Password   + "\n" +
                         "\t-dboldinst  " + dbOldInstance + "\n" + 
                         "\t-dbnewinst  " + dbNewInstance + "\n");
   
     // load jdbc driver
      Class drive = null;
      try {
         drive = Class.forName(DBdriver);
      } catch ( ClassNotFoundException e ) {
         DebugPrint.println(DebugPrint.ERROR, 
                            "JDBC Driver: " + DBdriver + " not found");
         DebugPrint.println(DebugPrint.ERROR, e); 
      }
      
      try {
         DriverManager.registerDriver((Driver)drive.newInstance());
      } catch(Throwable tt) {
         DebugPrint.println(DebugPrint.ERROR, 
                            "Error registering driver: " + DBdriver);
         DebugPrint.println(DebugPrint.ERROR, tt); 
      }
      
      Connection c = null;
      try {
         c = getConnection();
         StringBuffer sql = new StringBuffer();
         sql.append("select masterkey from ")
            .append(dbOldInstance).append(".dtmetrics")
            .append(" union ")
            .append("select masterkey from ")
            .append(dbOldInstance).append(".dtmetrics");
         
         Statement stmt = c.createStatement();
         
         System.out.println("Twist: Executing: " + sql.toString());
         
         ResultSet results = stmt.executeQuery(sql.toString());
         
         int i=1;
         while(results.next()) {
            String masterkey = results.getString(1);
            System.out.println("Processing key[" + i++ + "] = " + masterkey);
            
            sql.setLength(0);
            sql.append("select name,value from ")
               .append(dbOldInstance).append(".dtmetrics where masterkey='")
               .append(masterkey).append("'");
            
            Statement instmt = c.createStatement();
            ResultSet inres = instmt.executeQuery(sql.toString());
            
            Desktopdata dd = new Desktopdata();
            DesktopView dv = new DesktopView();
            dv.setKey(masterkey);
            dd.setDesktop(dv);
            largestend = 0;
            
            while(inres.next()) {
               String name = inres.getString(1);
               String val  = inres.getString(2);
               System.out.println(name + " \t" + val);
               
               StringTokenizer tokenizer = new StringTokenizer(name, "!");
               
               tokenizer.nextToken();  // get rid of leading dt
               String v1 = tokenizer.nextToken();
               if (tokenizer.hasMoreTokens()) {
                  String elem  = tokenizer.nextToken();
                  String v2    = tokenizer.nextToken();
                  long   lval  = 0;
                  
                  try { lval=Long.parseLong(val); } catch(Throwable tt) {}
                  
                  if (elem.indexOf("_Elm_") != 0) {
                     System.out.println("Bzzzz!: Expected _Elm_ got: " +
                                        elem);
                     continue;
                  }
                  String idxStr = elem.substring(5);
                  try {
                     int idx = Integer.parseInt(idxStr) + 1;
                     idxStr = "" + idx;
                  } catch (NumberFormatException nne){
                     System.out.println("Bzzzz!: Expected _Elm_N got: " +
                                        v1);
                     continue;
                  }
                  
                  if        (v1.equals("dshsessions")) {
                     Hostingdata dsh;
                     while((dsh=dd.getDSHSession(idxStr)) == null) {
                        dd.addDSHSession(new Hostingdata());
                     }
                     
                     if        (v2.equals("hostname")) {
                        dsh.setHostname(val);
                     } else if (v2.equals("project")) {
                        dsh.setProject(val);
                     } else if (v2.equals("numconns")) {
                        dsh.setNumConnections((int)lval);
                     } else if (!parseCommon(dsh, v2, val)) {
                        System.out.println("Bzzz! Bad!: " + name + " " + val);
                        continue;
                     }
                        
                  } else if (v1.equals("edusessions")) {
                     Educationdata edu;
                     while((edu=dd.getEDUSession(idxStr)) == null) {
                        dd.addEDUSession(new Educationdata());
                     }
                     
                     if        (v2.equals("hostname")) {
                        edu.setHostname(val);
                     } else if (v2.equals("project")) {
                        edu.setProject(val);
                     } else if (v2.equals("classname")) {
                        edu.setClassname(val);
                     } else if (v2.equals("numconns")) {
                        edu.setNumConnections((int)lval);
                     } else if (!parseCommon(edu, v2, val)) {
                        System.out.println("Bzzz! Bad!: " + name + " " + val);
                        continue;
                     }
                  } else if (v1.equals("odcsessions")) {
                     ODCdata odc;
                     while((odc=dd.getODCSession(idxStr)) == null) {
                        dd.addODCSession(new ODCdata());
                     }
                     
                     if (tokenizer.hasMoreTokens() && 
                         v2.equals("odcmeetings")) {
                        
                        elem  = tokenizer.nextToken();
                        String v3    = tokenizer.nextToken();
                        
                        if (elem.indexOf("_Elm_") != 0) {
                           System.out.println("Bzzzz!: Expect _Elm_ got: " +
                                              elem);
                           continue;
                        }
                        
                        idxStr = elem.substring(5);
                        try {
                           int idx = Integer.parseInt(idxStr) + 1;
                           idxStr = "" + idx;
                        } catch (NumberFormatException nne){
                           System.out.println("Bzzzz!: Want _Elm_N got: " +
                                              elem);
                           continue;
                        }
                        
                        Meetingdata meet;
                        while((meet=odc.getMeeting(idxStr)) == null) {
                           odc.addMeeting(new Meetingdata());
                        }
                        
                        if (tokenizer.hasMoreTokens() &&
                            v3.equals("invitees")) {
                            
                           elem  = tokenizer.nextToken();
                           String v4    = tokenizer.nextToken();
                           
                           if (elem.indexOf("_Elm_") != 0) {
                              System.out.println(
                                 "Bzzzz!: Expect _Elm_ got: " + elem);
                              continue;
                           }
                           
                           idxStr = elem.substring(5);
                           try {
                              int idx = Integer.parseInt(idxStr) + 1;
                              idxStr = "" + idx;
                           } catch (NumberFormatException nne){
                              System.out.println(
                                 "Bzzzz!: Want _Elm_N got: " + elem);
                              continue;
                           }
                           
                           Invitationdata inv;
                           while((inv=meet.getInvitation(idxStr)) == null) {
                              inv = new Invitationdata();
                              inv.setInvitationId(idxStr);
                              meet.addInvitation(inv);
                           }
                           
                           if        (v4.equalsIgnoreCase("timeDropped")) {
                              inv.setTimeDropped(lval);
                           } else if (v4.equals("state")) {
                              inv.setState(val);
                           } else if (v4.equals("partyedgeid")) {
                              inv.setPartyEdgeId(val);
                           } else if (v4.equals("partycompany")) {
                              inv.setPartyCompany(val);
                           } else if (v4.equals("partycountry")) {
                              inv.setPartyCountry(val);
                           } else if (v4.equals("partysessid")) {
                              inv.setPartySessionId(val);
                           } else if (!parseCommon(inv, v4, val)) {
                              System.out.println("Bzzz! Bad!: " + 
                                                 name + " " + val);
                              continue;
                           }
                           
                        } else if        (v3.equals("numconns")) {
                           meet.setNumConnections((int)lval);
                        } else if (!parseCommon(meet, v3, val)) {
                           System.out.println("Bzzz! Bad!: " + 
                                              name + " " + val);
                           continue;
                        }
                     } else if (v2.equals("numconns")) {
                        odc.setNumConnections((int)lval);
                     } else if (v2.equals("project")) {
                        odc.setProject(val);
                     } else if (!parseCommon(odc, v2, val)) {
                        System.out.println("Bzzz! Bad!: " + name + " " + val);
                        continue;
                     }
                  } else {
                     System.out.println("Bzzzz!: Expected xxxsessions got: " +
                                        v1);
                     continue;
                  }
               } else if (!parseDT(dd, v1, val)) {
                  System.out.println("Bzzz! ParseDT failed for: " + v1);
                  continue;
               } 
            }
            if (dd.getEndTime() != largestend) {
               System.out.println("LargestEndFix - was " + dd.getEndTime());
               dd.setEndTime(largestend);
            }
            System.out.println("----\n" + dd.toString() + "----\n");
            dd.toDB2(c, dbNewInstance);
            inres.close();
            instmt.close();
         }
         
         results.close();
         stmt.close();
         
      } catch(Throwable t) {
         System.out.println("Error Twisting metrics in DB2!");
         t.printStackTrace(System.out);
      } finally {
         destroyConnection(c);
      }
   }
}
