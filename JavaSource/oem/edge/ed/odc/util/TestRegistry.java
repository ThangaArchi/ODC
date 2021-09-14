package oem.edge.ed.odc.util;

import oem.edge.ed.util.*;

import org.apache.log4j.Logger;

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Iterator;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 2006	               	                 */ 
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
* The TestRegistry is for local testing with Frontpage info as backing AMT
*/
public class TestRegistry implements UserRegistry {

   static protected Logger log  =
      Logger.getLogger("oem.edge.ed.odc.util.TestRegistry");

  /** 
   * Returns a Vector of AMTUser objects which match the specified name. 
   *  Normally there should be 0 or 1 matches, but systems which allow multiple
   *  records for the same user (for whatever reason) are accomodated.
   *
   * @param name             login name for AMTUser record retrieval
   * @param regex            if true, name is regex, otherwise, static string
   * @param getentitlements  if true, all the users entitlements will be 
   *                         included
   * @param getprojects      if true, all the users projects will be 
   *                         included
   *
   */
   public Vector lookup(String name, 
                        boolean regex,
                        boolean getentitlements, 
                        boolean getprojects) throws DBException {
      
      return getUserInfo(name, regex);
   }
                     
  /** 
   * Returns a Vector of AMTUser objects which have the specified email 
   *  address. There can be 0 - n matches, as a single email address (person)  
   *  might own multiple IDs.
   *
   * @param name             email address for AMTUser record retrieval
   * @param regex            if true, name is regex, otherwise, static string
   * @param getentitlements  if true, all the users entitlements will be 
   *                         included
   * @param getprojects      if true, all the users projects will be 
   *                         included
   *
   */
   public Vector lookupByEmail(String email, 
                               boolean regex,
                               boolean getentitlements, 
                               boolean getprojects) throws DBException {
      Vector v = getUserInfo("*", true);
      Vector ret = new Vector();
      Iterator it = v.iterator();
      
      org.apache.regexp.RE re = null;
      if (regex && email != null) {
         try {
            re = new org.apache.regexp.RE(email);
         } catch(org.apache.regexp.RESyntaxException syne) {
            throw new DBException("Invalid regexp: " + email);
         }
      }
      
      while(it.hasNext()) {
         AMTUser u = (AMTUser)it.next();
         String lemail = u.getEmail();
         
         if (lemail != null) {
            if ((regex  && re.match(email)) ||
                (!regex && email.equals(email))) {
               ret.add(u);
            }
         }
      }
      return ret;
   }
                            
  /** 
   * Returns a Vector of Strings ir AMTUser objects for users who have access
   *  to the specified project (based on retstring parm).
   *
   * @param projname         projname for access to specified project
   * @param retstring        true if this method should return strings 
   *                         else AMTUser objects
   *
   */
   public Vector lookupUsersWithProject(String projname, 
                                        boolean retstring) throws DBException {
      Vector v = getUserInfo("*", true);
      Vector ret = new Vector();
      Iterator it = v.iterator();
      while(it.hasNext()) {
         AMTUser u = (AMTUser)it.next();
         Vector p = u.getProjects();
         if (p != null && p.contains(projname)) {
            if (retstring) ret.add(u.getUser());
            else           ret.add(u);
         }
      }
      return ret;
   }
   
   
   public Vector lookupUsersWithProject(String projname) throws DBException { 
      return lookupUsersWithProject(projname, true);
   }
   
   protected Vector getUserInfo(String userid, boolean regex) throws DBException {
   
      Vector ret = new Vector();
      
      DBConnection dbconn = null;
      Connection conn = null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;	
      StringBuffer sql = null;
      
      try {
         dbconn = DBSource.getDBConnection("AMT");
         conn = dbconn.getConnection();
         
         sql = new StringBuffer("select ");
         sql.append("userid,email,company,country,state,last,first,projects");
         sql.append(" from edesign.frontpage ");
         
        // If regex, 
        //   - we will be using \ as escape (to match regex escape)
        //     Since they should already be escaped if they are to be included
        //     in search, we are good to go for any \., \* as well as \%, \_
        //   - escape any _ and %
        //   - tr '?*' '_%'
         if (regex) {
            boolean esc=false;
            char arr[] = userid.toCharArray();
            StringBuffer sb = new StringBuffer();
            for(int i=0; i < arr.length; i++) {
              // if this char is escaped ... leave it that way
               if (esc) {
                  sb.append('\\').append(arr[i]);
                  esc = false;
                  continue;
               } else if (arr[i] == '_' || arr[i] == '%') {
                  sb.append('\\').append(arr[i]);
               } else if (arr[i] == '?') {
                  sb.append('_');
                  arr[i] = '_';
               } else if (arr[i] == '*') {
                  sb.append('%');
               } else if (arr[i] == '\\') {
                  esc = true;
               } else {
                  sb.append(arr[i]);
               }
            }
            userid = sb.toString();
            
            sql.append("where ucase(userid) like ? ESCAPE '\\' ");
         } else {
            sql.append("where ucase(userid) = ? ");
         }
         
         sql.append(" FOR READ ONLY WITH UR");
         
         pstmt=conn.prepareStatement(sql.toString());
         
         pstmt.setString(1, userid.toUpperCase());
         
         rs = dbconn.executeQuery(pstmt);
         
         while (rs.next()) {
            
            int i=1;
            
            AMTUserInst u = new AMTUserInst();
            u.setUser(rs.getString(i++));
            u.setEdgeUser(u.getUser());
            u.setEmail(rs.getString(i++));
            u.setCompany(rs.getString(i++));
            u.setAMTCompany(u.getCompany());
            u.setPOCCompany(u.getCompany());
            u.setCountry(rs.getString(i++));
            u.setState(rs.getString(i++));
            u.setLastName(rs.getString(i++));
            u.setFirstName(rs.getString(i++));
            
            String projects = rs.getString(i++);
            if (projects != null) {
               StringTokenizer stok = new StringTokenizer(projects, ":", false);
               while(stok.hasMoreTokens()) {
                  String p = stok.nextToken();
                  
                 // We treat some projects like entitlements ;-) HACK
                  if (p.equals("DSGN_DBOX_SUPER")) {
                     u.addEntitlement(p);
                  } else if (p.equals("ITAR_CERTIFIED")) {
                     u.addEntitlement(p);
                  } else if (p.equals("DSGN_CONF")) {
                     u.addEntitlement(p);
                  } else {
                     Vector v = new Vector();
                     v.add(p);
                     u.addProjects(v);
                  }
               }
            }
            
           // Silly special case code. Add DSGN_CONF entitlement if IBMer
            if (u.getCompany().equals("IBM")) {
               if (!u.isEntitled("DSGN_CONF")) {
                  u.addEntitlement("DSGN_CONF");
               }
            }
            
           //rets.setProperty("expires",   ""+rs.getLong(i++));
           //rets.setProperty("creator",      rs.getString(i++));
           //rets.setProperty("loginallowed", rs.getString(i++));
           //rets.setProperty("admin",        rs.getString(i++));
           //rets.setProperty("status",       rs.getString(i++));
           //rets.setProperty("report",       rs.getString(i++));
           //rets.setProperty("reportdbox",   rs.getString(i++));
            ret.add(u);
         }
            
      } catch (Exception ee) {
         log.warn("Error with DB Processing");
         log.warn(ee);
         if (sql != null) log.warn(sql.toString());
         dbconn.destroyConnection(conn);
         conn=null;
         pstmt=null;
         throw new DBException("Error looking up user: " + userid + " was " + 
                               ee.getMessage());
      } finally {
         dbconn.returnConnection(conn);
         if (pstmt != null) try {
            pstmt.close();
         } catch(Exception ee) {}
      }      
      return ret;
   }
}

   
