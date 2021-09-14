package oem.edge.ed.odc.model;

import java.io.*;
import oem.edge.ed.odc.util.*;
import oem.edge.ed.odc.cntl.*;
import oem.edge.ed.odc.view.*;
import oem.edge.ed.util.PasswordUtils;
import oem.edge.ed.odc.tunnel.common.DebugPrint;
import java.util.*;
import java.sql.*;

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
 * Creation date: (09/05/00 16:29:24)
 * @author: Administrator
 */
public class DBDataManager implements DataManager {
   private static String Instance = null;
   
   public String getInstance() { return Instance; }
   
/**
 * DBDataManager constructor comment.
 */
   public DBDataManager() {
      super();
   }
/**
 * addDesktop method comment.
 */
   public boolean addDesktop(DesktopView dv) {
      
      Connection conn = null;
      boolean ret = false;
      try {
         conn = getConnection();
         StringBuffer sql = new StringBuffer("insert into ");
         
        /* -- Entry order --
        ** id,
        ** l_host
        ** r_host
        ** starttime
        ** l_port
        ** edgeid
        ** company
        ** country
        ** firstname
        ** lastname
        ** emailaddr
        ** projects
        ** state
        */
         sql.append(this.Instance);
         sql.append(".desktop (id,l_host,r_host,starttime,l_port,edgeid,company,country");
         
         String first    = dv.getFirstName();
         String last     = dv.getLastName();
         String email    = dv.getEmailAddr();
         String projects = dv.getProjects();
         String state    = dv.getState();
         String bumphost = dv.getBumpHost();
         StringBuffer    post = new StringBuffer();

         if (first    != null) { sql.append(",firstname"); post.append(",?"); }
         if (last     != null) { sql.append(",lastname");  post.append(",?"); }
         if (email    != null) { sql.append(",emailaddr"); post.append(",?"); }
         if (projects != null) { sql.append(",projects");  post.append(",?"); }
         if (state    != null) { sql.append(",state");     post.append(",?"); }
         if (bumphost != null) { sql.append(",bumphost");  post.append(",?"); }
         
         sql.append(") values (?,?,?,current timestamp,?,?,?,?")
            .append(post.toString()).append(")");
         
         PreparedStatement stmt = conn.prepareStatement(sql.toString());
         
         int sti = 1;
         
         stmt.setString(sti++, dv.getKey());
         stmt.setString(sti++, dv.getLocalHost());
         stmt.setString(sti++, dv.getRemoteHost());
         stmt.setInt   (sti++, dv.getLocalPort());
         stmt.setString(sti++, dv.getEdgeId());
         stmt.setString(sti++, dv.getCompany());
         stmt.setString(sti++, dv.getCountry());
         
         if (first    != null) stmt.setString (sti++, first);
         if (last     != null) stmt.setString (sti++, last);
         if (email    != null) stmt.setString (sti++, email);
         
        // Truncate to 1024 so we don't get DB2 exception
         if (projects != null) {
            if (projects.length() > 1024) {
               projects = projects.substring(0,1024);
            }
            stmt.setString (sti++, projects);
         }
         if (state    != null) stmt.setString (sti++, state);
         if (bumphost != null) stmt.setString (sti++, bumphost);
         
         DebugPrint.println(DebugPrint.INFO4, 
                            "AddDesktop: Executing " + sql.toString());
         int changed = stmt.executeUpdate();
         
         DebugPrint.println(DebugPrint.INFO4, "Result: " + changed);
         
        // Release JDBC resources
         stmt.close();
         
        // may also want to check for stmt.getWarnings();
         ret = (changed == 1);
          
      } catch ( Exception e ) {
         DebugPrint.println(DebugPrint.ERROR, "addDesktop: Exception:>");
         DebugPrint.println(DebugPrint.ERROR, e);
         destroyConnection(conn); conn = null;
         ret = false;
      } finally {
         returnConnection(conn);
      }
      
      return ret;
   }
   
   
/**
 * bindDesktop method comment.
 */
   public DesktopView bindDesktop(String id, String owner, String xDisplay, 
                                  String xCookie, String alias,
                                  String bumphost, String bumpport) {
      DesktopView dv = getDesktop(id);
      if ( dv != null ) {
         Connection conn = null;
         Statement stmt = null;
         try {
            String sep = "";
            conn = getConnection();
            StringBuffer sql = new StringBuffer("update ");
            sql.append(this.Instance).append(".desktop set ");
            
            if (owner != null) {
               sql.append("owner='").append(owner).append("'");
               sep = ",";
            }
            
            if (xDisplay != null) {
               sql.append(sep);
               sql.append("xdisplay='").append(xDisplay).append("'");
               sep = ",";
            }
            
            if (xCookie != null) {
               sql.append(sep).append("xcookie='").append(xCookie).append("'");
               sep = ",";
            }
            if (alias != null) {
               sql.append(sep).append("xalias='").append(alias).append("'");
               sep = ",";
            }
            if (bumphost != null) {
               sql.append(sep);
               sql.append("bumphost='").append(bumphost).append("',");
               sql.append("bumpport=").append(bumpport);
            }
            
            sql.append(" where id='").append(id).append("'");
            stmt = conn.createStatement();
            
            DebugPrint.println(DebugPrint.INFO4, 
                               "bindDesktop: Executing: " + sql.toString());
            int changed = stmt.executeUpdate(sql.toString());
            
            DebugPrint.println(DebugPrint.INFO4, 
                               "Results: " + changed);
            
            if ( changed == 1 ) {
               dv.setOwner(owner);
               dv.setXDisplay(xDisplay);
               dv.setXCookie(xCookie);
               dv.setXAlias(alias);
            } else {
				// may need message here
               dv = null;
            }
            
           // Release JDBC resources
            stmt.close();
         } catch ( Exception e ) {
            dv = null;
            DebugPrint.println(DebugPrint.ERROR, "bindDesktop: Exception:>");
            DebugPrint.println(DebugPrint.ERROR, e);
            destroyConnection(conn); conn = null;
         } finally {
            returnConnection(conn);
         }
      } else {
        // need message here
      }
      
      return dv;
   }
   
   
/**
 * Lifted from tunnel.common:)
 * Since we have to pass byte[] as string in .ini format, we need
 * to convert it to byte[] when making db2 calls:(
 * Creation date: (9/18/00 10:49:54 AM)
 * @return byte[]
 * @param id java.lang.String
 */
   private byte[] getBytes(String id) {
      
      int len = 13; // char[13] for bit data from db2 unique_id() function 
      byte ans = 0;
      byte db2id[] = new byte[id.length() / 2 + 4];
      byte[] newID = null;
      for (int i = 0; i < id.length(); i++) {
         char ch = id.charAt(i);
         int v = Character.digit(ch, 16);
         ans <<= 4;
         ans |= v;
         if (v >= 0 && v <= 15) {
            if ((i & 1) != 0) {
               db2id[i / 2] = (byte) ans;
            }
         } else {
            break;
         }
      }
      
      if ( db2id.length >= len ) {
         newID = new byte [13];
         for ( int k=0; k<13; k++ ) {
            newID[k] = db2id[k];
         }
         
         DebugPrint.println(DebugPrint.DEBUG, 
                            "string id to byte[] conversion succeeded.");
      } else {
         DebugPrint.println(DebugPrint.WARN, 
                            "string id to byte[] conversion failed: " + id);
      }
      
      return newID;
   }
   
/**
 * Make it a separate method so that we can localize the changes
 * when using name services to locate data sources or IBM connection manager.
 * Creation date: (09/06/00 09:45:02)
 * @return java.sql.Connection
 * @exception java.sql.SQLException The exception description.
 */
 
   private DesktopView parseDesktopView(ResultSet results) throws 
                                                     java.sql.SQLException {
     //StringBuffer sql = new StringBuffer("select owner,l_host,xdisplay,xcookie,xalias,r_host,r_port,starttime,l_port,bumphost,bumpport,edgeid,firstname,lastname,emailaddr,projects,company,country,id from ");
         
      DesktopView dv = null;
      String temp;
      if ( results.next() ) {
         dv = new DesktopView();
         temp = results.getString(1);
         if ( temp != null ) {
            dv.setOwner(temp.trim());
         }
         temp = results.getString(2);
         if ( temp != null ) {
            dv.setLocalHost(temp.trim());
         }
         temp = results.getString(3);
         if ( temp != null ) {
            dv.setXDisplay(temp.trim());
         }
         temp = results.getString(4);
         if ( temp != null ) {
            dv.setXCookie(temp.trim());
         }
         temp = results.getString(5);
         if ( temp != null ) {
            dv.setXAlias(temp.trim());
         }
         temp = results.getString(6);
         if ( temp != null ) {
            dv.setRemoteHost(temp.trim());
         }
         dv.setRemotePort(results.getInt(7));
         if (dv.getRemotePort() <= 0 ) {
            dv.setRemotePort(-1);
         }
         dv.setStartTime(results.getTimestamp(8));
         dv.setLocalPort(results.getInt(9));
         
         temp = results.getString(10);
         if ( temp != null ) {
            dv.setBumpHost(temp.trim());
         }
         dv.setBumpPort(results.getInt(11));
         temp = results.getString(12);
         if ( temp != null ) {
            dv.setEdgeId(temp);
         }
         
         temp = results.getString(13);
         if ( temp != null ) {
            dv.setFirstName(temp);
         }
         temp = results.getString(14);
         if ( temp != null ) {
            dv.setLastName(temp);
         }
         temp = results.getString(15);
         if ( temp != null ) {
            dv.setEmailAddr(temp);
         }
         temp = results.getString(16);
         if ( temp != null ) {
            dv.setProjects(temp);
         }
         
         temp = results.getString(17);
         if ( temp != null ) {
            dv.setCompany(temp);
         }
         temp = results.getString(18);
         if ( temp != null ) {
            dv.setCountry(temp);
         }
         temp = results.getString(19);
         if ( temp != null ) {
            dv.setKey(temp);
         }
         temp = results.getString(20);
         if ( temp != null ) {
            dv.setState(temp);
         }
      }
      return dv;
   }

  
   public Connection getConnection() throws java.sql.SQLException {
      Connection conn = null;
      
      try {
         DBConnection dbconn = DBSource.getDBConnection("EDODC");
         conn = dbconn.getConnection();
      } catch(Exception ee) {
         SQLException tt = 
            new SQLException(ee.getMessage() + 
                             ": Error accessing DBDataSource for EDODC");
         tt.fillInStackTrace();
         throw tt;
      }
      
      if (conn == null) {
         DebugPrint.println(DebugPrint.ERROR, 
                            "getConnection: conn == null!");
      }
      return conn;
   }
   
   public void returnConnection(Connection conn) {
      if (conn != null) {
         try {
            DBConnection dbconn = DBSource.getDBConnection("EDODC");
            dbconn.returnConnection(conn);
         } catch(Exception ee) {
         }
      }
   }
   
   public void destroyConnection(Connection conn) {
      if (conn != null) {
         try {
            DBConnection dbconn = DBSource.getDBConnection("EDODC");
            dbconn.destroyConnection(conn);
         } catch(Exception ee) {
         }
      }
   }
  
  
/**
 * getDesktop method comment.
 */
   public DesktopView getDesktop(String id) {
      DesktopView dv = null;
      Connection conn = null;
      try {
         conn = getConnection();
         StringBuffer sql = new StringBuffer("select owner,l_host,xdisplay,xcookie,xalias,r_host,r_port,starttime,l_port,bumphost,bumpport,edgeid,firstname,lastname,emailaddr,projects,company,country,id,state from ");
         sql.append(this.Instance).append(".desktop where id='").append(id).append("'");
         Statement stmt = conn.createStatement();
         
         DebugPrint.println(DebugPrint.INFO4, 
                            "getDesktop: Executing: " + sql.toString());
         
         ResultSet results = stmt.executeQuery(sql.toString());
         
         dv = parseDesktopView(results);
         
         results.close();
         stmt.close();
         
      } catch ( Exception e ) {
         DebugPrint.println(DebugPrint.ERROR, "getDesktop: Exception:>");
         DebugPrint.println(DebugPrint.ERROR, e);
         destroyConnection(conn); conn = null;
      } finally {
         returnConnection(conn);
      }
      
      return dv;
   }
   
/**
 * getDesktopByUser method comment.
 */
   public java.util.Vector getDesktopByUser(String uid) {
      Vector vec = null;
      Connection conn = null;
      try {
         conn = getConnection();
         StringBuffer sql = new StringBuffer("select owner,l_host,xdisplay,xcookie,xalias,r_host,r_port,starttime,l_port,bumphost,bumpport,edgeid,firstname,lastname,emailaddr,projects,company,country,id,state from ");
         sql.append(this.Instance).append(".desktop where edgeid='").append(uid).append("'");
         Statement stmt = conn.createStatement();
         
         DebugPrint.println(DebugPrint.INFO4, 
                            "getDesktopByUser: Executing: " + sql.toString());
         
         ResultSet results = stmt.executeQuery(sql.toString());
         DesktopView dv = null;
         do {
            dv = parseDesktopView(results);
            if (dv != null) {
               try {
                  DebugPrint.println(DebugPrint.INFO4, 
                                     "Get By User:\n\n"+dv.toString());
               } catch (Throwable t) {
                  DebugPrint.println(DebugPrint.ERROR, 
                                     "Error doing fv.toString??!?");
                  DebugPrint.println(DebugPrint.ERROR, t);
               }
               if (vec == null) {
                  vec = new Vector();
               }
               vec.addElement(dv);
            }
         } while(dv != null);
         
         results.close();
         stmt.close();
         
      } catch ( Exception e ) {
         DebugPrint.println(DebugPrint.ERROR, "getDesktopByUser: Exception:>");
         DebugPrint.println(DebugPrint.ERROR, e);
         destroyConnection(conn); conn = null;
      } finally {
         returnConnection(conn);
      }
      return vec;
   }
   
/**
 * load method comment.
 */
public boolean init(ReloadingProperty props) {
   Instance = props.getProperty("edodc.dbInstance", "edesign");
   return DBSource.getDBConnection("EDODC") != null;
}
   
/**
 * removeDesktop method comment.
 */
public DesktopView removeDesktop(String id) {
   DesktopView dv = getDesktop(id);
   if ( dv != null ) {
      Connection conn = null;
      try {
         conn = getConnection();
         StringBuffer sql = new StringBuffer("delete from ");
         sql.append(this.Instance).append(".desktop where id='").append(id).append("'");
         Statement stmt = conn.createStatement();
         
         DebugPrint.println(DebugPrint.INFO4, 
                            "removeDesktop: Execute: " + sql.toString());
         
         int changed = stmt.executeUpdate(sql.toString());
         
         DebugPrint.println(DebugPrint.INFO4, 
                            "Results: " + changed);
         
         if ( changed != 1 ) {
           // what if someone removed the record befor I get here?
           // guess we should make the 2 jdbc calls as a "transacion" so that no one
           // can add or delete the record with the id before I remove it
            dv = null;
         }
         
         stmt.close();
         
      } catch ( Exception e ) {
         DebugPrint.println(DebugPrint.ERROR, "removeDesktop: Exception:>");
         DebugPrint.println(DebugPrint.ERROR, e);
         destroyConnection(conn); conn = null;
      } finally {
         returnConnection(conn);
      }
   }
   
   return dv;
}
   
   
/**
 * getDesktop method comment.
 */
   public DesktopView setRemotePort(String id, String rhost, int port) {
      
      DesktopView dv = getDesktop(id);
      dv.setRemotePort(port);
      dv.setRemoteHost(rhost);
      if ( dv != null ) {
         Connection conn = null;
         try {
            conn = getConnection();
            StringBuffer sql = new StringBuffer("update ");
            sql.append(this.Instance).append(".desktop set r_port=").append(port);
            sql.append(",r_host='").append(rhost);
            sql.append("' where id='").append(id).append("'");
            Statement stmt = conn.createStatement();
            DebugPrint.println(DebugPrint.INFO4, 
                               "setRemotePort: Executing: " + sql.toString());
            int changed = stmt.executeUpdate(sql.toString());
            DebugPrint.println(DebugPrint.INFO4, 
                               "Results: " + changed);
            stmt.close();
         } catch ( Exception e ) {
            DebugPrint.println(DebugPrint.ERROR, "setRemotePort: Exception:>");
            DebugPrint.println(DebugPrint.ERROR, e);
            destroyConnection(conn); conn = null;
         } finally {
            returnConnection(conn);
         }
      }
      
      return dv;
   }
}
