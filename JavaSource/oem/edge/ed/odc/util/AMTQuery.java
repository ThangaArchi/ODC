package oem.edge.ed.odc.util;

import java.lang.*;
import java.sql.*;
import java.util.*;
import oem.edge.ed.odc.tunnel.common.*;

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

public class AMTQuery implements UserRegistry {

   static public boolean includeMasterFSE = false;
   static public boolean includeFSEAdmin  = false;

   static public String doTrim(String s) {
      if (s == null) s = "";
      else           s = s.trim();
      return s;
   }

  // Function taken from source/oem/edge/amt/AccessCntrlFuncs.java
  // modified to NOT take hasproject and isdeploylead. Instead we just
  // do the union ... if its in there, then its there. Not adding the
  // RedCarpet and DeplyLead entitlements at end. We don't care anyway.
   static protected Vector getUserEntitlements(
      Connection conn,
      String userid)
//      boolean hasProject,
//      boolean isDeployLead)
      throws SQLException {
      
      Statement stmt = null;
      ResultSet rs = null;
      
      StringBuffer qry =
         new StringBuffer("Select entitlement from amt.s_user_ent_view where userid='")
         .append(userid)
         .append("' ");
         
      qry.append(" union Select entitlement from amt.s_user_project_view where userid='") .append(userid) .append("'");
         
      qry.append(" union Select entitlement from amt.deploylead where userid='").append(userid).append("'");
      
      qry.append(" for read only with ur");
      
      
     //System.err.println(qry.toString());
      
      Vector v = new Vector();
      try {
         stmt = conn.createStatement();
         if (DebugPrint.getLevel() >= DebugPrint.DEBUG2) {
            DebugPrint.printlnd(DebugPrint.DEBUG2,
                                "getUserEntitle: " + qry.toString());
         }
         rs = stmt.executeQuery(qry.toString());
         while (rs.next()) {
            String ent = rs.getString(1).trim();
            v.addElement(ent);
         }
      } catch (SQLException ex) {
         throw ex;
      } finally {
         if (rs != null)
            try {
               rs.close();
               rs = null;
            } catch (SQLException ex) {
            }
         if (stmt != null)
            try {
               stmt.close();
               stmt = null;
            } catch (SQLException ex) {
            }
      }
     /*
      if (isDeployLead) {
         v.addElement("RedCarpet");
         v.addElement("DeployLead");
      }
     */
      
      return v;
   }

  // Function taken from source/oem/edge/ed/fe/EdesignServicesServlet.java
   public static 
   Vector getCollabProjects(Connection con, 
                            String sUserId,
                            String sApplication) throws SQLException {
	
      StringBuffer sb = new StringBuffer();
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      Vector vDetails = new Vector();	
	
      try {
	
         sb.append("SELECT CUSTOMER_PROJNAME FROM EDESIGN.SERVICES_PROJECTS WHERE EDGE_USERID = ? AND APPLICATION=?");
         sb.append(" for read only with ur");
         
			
         pstmt = con.prepareStatement(sb.toString());
         pstmt.setString(1,sUserId.trim());
         pstmt.setString(2,sApplication);
         
         if (DebugPrint.getLevel() >= DebugPrint.DEBUG2) {
            DebugPrint.printlnd(DebugPrint.DEBUG2,
                                "getCollabProj: " + sb.toString());
         }
	
         rs = pstmt.executeQuery();
	
         while (rs.next()) {
            vDetails.addElement(rs.getString("CUSTOMER_PROJNAME"));
         }
         
         pstmt.close();
      } catch (SQLException e) {
      
         if (e.getSQLState().equals("42704")) {
         
           // If the table is not define ... just skip it
            DebugPrint.printlnd(DebugPrint.WARN, 
               "AMTQuery: Call to getcollab projects, and table not there. Just skip");
           
         } else {
            throw e;
         }
         
      } finally {
         try {
            if (pstmt != null) {
               pstmt.close();
            }
         } catch(Exception ee) {
         }
      }
	
      return vDetails;
   }
   
  // This is an ASIC thing
   public static String getCustomerType(AMTUserInst user) {
   
      // determine the customer type based on who has logged on...
      
      String customerType = "";
      
      if (includeMasterFSE && user.isEntitled("MASTER_FSE")) {
         customerType = "MASTER";
      } else if (includeFSEAdmin  && user.isEntitled("FSE_ADMIN")) {
         customerType = "MASTER";
      } else if (user.isEntitled("EDSGN_FSE")) {
         customerType = "FSE";
      } else if (user.isEntitled("TECH_PM")) {
        //customerType = "TECHPM";
        // Don't do TECHPM, as no data ever gets sent to users via TECHPM
         customerType = "EXTERNAL";
      } else {
         customerType = "EXTERNAL";
      }
      return customerType;
   }
   
  // Derived from source/oem/edge/ed/fe/EdesignToolkitOrderDelta.java
  //          and source/oem/edge/ed/fe/EdesignToolkitOrder.java
   public static 
   Vector getUsersHavingProject(String proj) throws DBException {
      return getUsersHavingProject(proj, true);
   }
   public static 
   Vector getUsersHavingProject(String proj, 
                                boolean includeMaster) throws DBException {
      
      
      StringBuffer sb = new StringBuffer();
      Connection connection=null;
      DBConnection dbconnect = null;
      PreparedStatement pstmt =null;
      ResultSet rs=null;
      String sqls = null;
      Vector ret = new Vector();
      Hashtable seenit = new Hashtable();
      
      try {
      
         try {
            dbconnect = DBSource.getDBConnection("AMT");
            connection = dbconnect.getConnection();
         } catch(NullPointerException npe) {
            throw new DBException("No connection found");
         }
      
        // If we do NOT have an ASIC project, return nobody
         if (!proj.startsWith("ASIC#")) {
            return ret;
         }
	
         int idx1 = proj.indexOf('#');
         int idx2 = proj.indexOf('#', idx1+1);
         int idx3 = proj.indexOf('#', idx2+1);                         
         
         
        // If its not of the form ASIC#COMPANY#PROJECTNAME, return with nada
         if (idx1 < 0 || idx2 <= idx1 || idx3 >= 0) {
            DebugPrint.printlnd(DebugPrint.WARN, 
                                "AMTQuery: getUsersHavingProject: " + 
                                proj + 
                                ": Not correct form for supposid ASIC proj!");
            return ret;
         }
         
         String company  = AMTUserInst.decode(proj.substring(idx1+1, idx2));
         String projname = AMTUserInst.decode(proj.substring(idx2+1));
         
        // Get list of IR_USERIDs who have legitimate access to
        //  the specified company-project
        //
        // 1) Validate that company project exists in asic_codename tab
        // 2) Validate that the project exists in user_edesign_profile
        //
         sb.append("select count(acn.CUSTOMER_PROJNAME) from EDESIGN.ASIC_CODENAME acn, EDESIGN.USER_EDESIGN_PROFILE uep1 where acn.CUSTOMER_PROJNAME = uep1.CUSTOMER_PROJNAME AND acn.CUSTOMER_PROJNAME=? and acn.USERS_COMPANY = ?"); 
         
        // And of course, no deadlocking please
         sb.append(" FOR READ ONLY WITH UR");
         
         pstmt = connection.prepareStatement(sb.toString());
         pstmt.setString(1, projname);
         pstmt.setString(2, company);
         
         if (DebugPrint.getLevel() >= DebugPrint.DEBUG2) {
            DebugPrint.printlnd(DebugPrint.DEBUG2,
                                "getUsersHavingProject: " + sb.toString() +
                                " : " + proj + " " + company + " " + projname);
         }
         
         rs = pstmt.executeQuery();
	
         if (!rs.next()) {
            DebugPrint.printlnd(DebugPrint.WARN,
                                "getUsersHavingProject: no resultset: " + sb.toString() +
                                " : " + proj + " " + company + " " + projname);
            throw new DBException("getUsersHavingProject: No result set. Error");
         }
         
        // If its not a valid project, just return
         if (rs.getInt(1) == 0) {
            DebugPrint.printlnd(DebugPrint.INFO5, 
                                "getUserHavingProject: invalid project: " +
                                proj);
            return ret;
         }
         
         pstmt.close();
         
        // 
        // Man do I like TOTALLY not get this SQL DB2 thing. If I join all of 
        //  the following steps (3, 4 & 5) with UNION ALL into the same query,
        //  it takes anywhere from 5-10 secs depending on its mood. If I separate
        //  into 3 separate queries, takes < 2 secs total. Guess what I am using.
        //
         

        // 3) Find all NORMAL external access IDs ... 
         
        // JMC 7/7/06 Got this modified query from Bhushan ... should be faster
        // JMC 4/3/07 More refactoring ... sigh
         sb = new StringBuffer("WITH x AS (select eu.edge_userid eu_edge_userid from EDESIGN.USER_EDESIGN_PROFILE eu where  eu.CUSTOMER_PROJNAME=?) select au.ir_userid from amt.users au, x where  au.edge_userid=x.eu_edge_userid and au.edge_userid in (select cau.EDGE_USERID from EDESIGN.CHIPS_AFS_USERID cau WHERE cau.users_company = ? AND cau.EDGE_USERID = x.eu_edge_userid)");
                  
        // And of course, no deadlocking please
         sb.append(" FOR READ ONLY WITH UR");

         pstmt = connection.prepareStatement(sb.toString());
         pstmt.setString(1, projname);
         pstmt.setString(2, company);
         
         if (DebugPrint.getLevel() >= DebugPrint.DEBUG2) {
            DebugPrint.printlnd(DebugPrint.DEBUG2,
                                "getUsersHavingProject: " + sb.toString() +
                                " : " + proj + " " + company + " " + projname);
         }
         rs = pstmt.executeQuery();
	
         while (rs.next()) {
            String t = rs.getString(1);
            if (!seenit.containsKey(t)) {
               ret.addElement(t);
               seenit.put(t, t);
            }
         }
         
         pstmt.close();
         
        // 4) Find all MASTER IDs
         
         if (includeMaster && (includeMasterFSE || includeFSEAdmin)) {
            String masteradmin = "('MASTER_FSE','FSE_ADMIN')";
            if (includeMasterFSE && !includeFSEAdmin) {
               masteradmin = "('MASTER_FSE')";
            } else if (!includeMasterFSE && includeFSEAdmin) {
               masteradmin = "('FSE_ADMIN')";
            }
            sb = new StringBuffer("select au.IR_USERID from AMT.USERS au where au.edge_userid in ("); 
            sb.append("select userid from amt.S_USER_ENT_VIEW where entitlement in ");
            sb.append(masteradmin);
            sb.append(" union select userid from amt.S_USER_PROJECT_VIEW where entitlement in ");
            sb.append(masteradmin);
            sb.append(" union select userid from amt.DEPLOYLEAD where entitlement in ");
            sb.append(masteradmin);
         
           // And of course, no deadlocking please
            sb.append(") FOR READ ONLY WITH UR");
            
            pstmt = connection.prepareStatement(sb.toString());
            
            if (DebugPrint.getLevel() >= DebugPrint.DEBUG2) {
               DebugPrint.printlnd(DebugPrint.DEBUG2,
                                   "getUsersHavingProject: " + sb.toString() +
                                   " : " + proj + " " + company + " " + projname);
            }
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
               String t = rs.getString(1);
               if (!seenit.containsKey(t)) {
                  ret.addElement(t);
                  seenit.put(t, t);
               }
            }
            pstmt.close();
         }
         
        // 5) Find all FSE ids for company project
         sb = new StringBuffer("with ent as (select userid from   amt.S_USER_ENT_VIEW where  entitlement = 'EDSGN_FSE' union select userid from   amt.S_USER_PROJECT_VIEW where  entitlement = 'EDSGN_FSE' union select userid from amt.DEPLOYLEAD), x as (select au.ir_userid from   ent, edesign.project_fse_detail pfd, amt.users au where  pfd.FSE_ID=ent.userid and    pfd.USERS_COMPANY=? and    pfd.CUSTOMER_PROJNAME = ? and au.edge_userid=ent.userid) select * from x");
         
        // And of course, no deadlocking please
         sb.append(" FOR READ ONLY WITH UR");
         
         pstmt = connection.prepareStatement(sb.toString());
         pstmt.setString(1, company);
         pstmt.setString(2, projname);
         
         if (DebugPrint.getLevel() >= DebugPrint.DEBUG2) {
            DebugPrint.printlnd(DebugPrint.DEBUG2,
                                "getUsersHavingProject: " + sb.toString() +
                                " : " + proj + " " + company + " " + projname);
         }
         rs = pstmt.executeQuery();
	
         while (rs.next()) {
            String t = rs.getString(1);
            if (!seenit.containsKey(t)) {
               ret.addElement(t);
               seenit.put(t, t);
            }
         }
         
      } catch (SQLException e) {
         System.out.println("Exception during getUsersHavingProject: " + sb.toString());
         e.printStackTrace (System.out);
         dbconnect.destroyConnection(connection);
         connection=null;
         throw new DBException("SQLError");
      } finally {
         if (pstmt!=null){
            try{
               pstmt.close();
            }catch(SQLException e){}
         }
         dbconnect.returnConnection(connection);
      }
      
      return ret;
   }
   
   
   static protected boolean updateIBMBluepagesData(Connection conn,
                                                   AMTUserInst user)
      throws SQLException {
      
      Statement stmt = null;
      ResultSet rs = null;
      
     // Only do this for IBMers
      if (!user.getCompany().equalsIgnoreCase("IBM")) return false;
      
     // req_status must be C or U to be valid (as per Sandeep Meheta). 
     // If mult records, should be ok, just take one 
     // (we order by serial ID and take the 'largest')
      StringBuffer qry = new StringBuffer("select dept, div from DECAF.USERS_BLUEPAGES_INFO where req_status in ('C', 'U') AND edge_user_id='");
      qry.append(user.getEdgeUser())
         .append("' order by REQ_SERIAL_ID DESC ")
         .append(" for read only with ur");
      
      try {
         stmt = conn.createStatement();
         if (DebugPrint.getLevel() >= DebugPrint.DEBUG2) {
            DebugPrint.printlnd(DebugPrint.DEBUG2,
                                "updateIBMBluepagesData: " + qry.toString());
         }
         rs = stmt.executeQuery(qry.toString());
         
         if (rs.next()) {
            String div  = rs.getString(1);
            String dept = rs.getString(2);
            if (div  != null) div  = div.trim();
            if (dept != null) dept = dept.trim();
            user.setIBMDiv (div);
            user.setIBMDept(dept);
            return true;
         }
      } catch (SQLException ex) {
        throw ex;
      } finally {
         if (rs != null)
            try {
               rs.close();
               rs = null;
            } catch (SQLException ex) {
            }
         if (stmt != null)
            try {
               stmt.close();
               stmt = null;
            } catch (SQLException ex) {
            }
      }
      
      return false;
   }
   
   
  // Derived from source/oem/edge/ed/fe/EdesignToolkitOrderDelta.java
  //          and source/oem/edge/ed/fe/EdesignToolkitOrder.java
   public static 
   Vector getAsicProjects(Connection con, AMTUserInst user) throws SQLException {
      return getAsicProjects(con, user, false);
   }
   
   public static 
   Vector getAsicProjects(AMTUserInst user, boolean asExternal) throws DBException {
     // If no connection passed in, create one
      Vector vec = null;
      DBConnection dbconnect = null;
      Connection         con = null;
      try {
         dbconnect = DBSource.getDBConnection("AMT");
               con = dbconnect.getConnection();
         vec = getAsicProjects(con, user, asExternal);
      } catch(SQLException e) {
         dbconnect.destroyConnection(con);
         con = null;
         throw new DBException("SQL Exception getting AsicProjects");
      } catch(Exception e) {
         throw new DBException("Exception getting AsicProjects");
      } finally {
         if (con != null) dbconnect.returnConnection(con);
      }
      return vec;
   }
   
   public static 
   Vector getAsicProjects(Connection con, AMTUserInst user,
                          boolean asExternal) throws SQLException {
      
      StringBuffer sb = new StringBuffer();
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      Vector vDetails = new Vector();	
      
      String customerType = getCustomerType(user);
      String userid = user.getEdgeUser();      
      DBConnection dbconnect = null;
	
      try {
         
         if (asExternal || customerType.equals("EXTERNAL")) {
           // EXTERNAL
           //select distinct a.CUSTOMER_PROJNAME,a.USERS_COMPANY from edesign.asic_codename a where a.technology=? and a.version_no=? and ucase(a.CUSTOMER_PROJNAME) in (select distinct ucase(CUSTOMER_PROJNAME) from EDESIGN.USER_EDESIGN_PROFILE  where EDGE_USERID=?) and ucase(a.users_company) = (select ucase(users_company) from edesign.chips_afs_userid where edge_userid=?) order by CUSTOMER_PROJNAME for READ ONLY        
            sb.append("select distinct a.CUSTOMER_PROJNAME,a.USERS_COMPANY from edesign.asic_codename a where ucase(a.CUSTOMER_PROJNAME) in (select distinct ucase(CUSTOMER_PROJNAME) from EDESIGN.USER_EDESIGN_PROFILE  where EDGE_USERID='");
            sb.append(userid);
            sb.append("') and ucase(a.users_company) = (select ucase(users_company) from edesign.chips_afs_userid where edge_userid='");
            sb.append(userid);
            sb.append("') order by CUSTOMER_PROJNAME");
            
         } else if (customerType.equals("MASTER")) {
           // MASTER
           //select distinct a.CUSTOMER_PROJNAME, a.USERS_COMPANY from edesign.asic_codename a where a.technology=? and a.version_no=? and ucase(a.CUSTOMER_PROJNAME) in (select distinct ucase(CUSTOMER_PROJNAME) from EDESIGN.USER_EDESIGN_PROFILE) order by USERS_COMPANY for READ ONLY
           
            sb.append("select distinct a.CUSTOMER_PROJNAME, a.USERS_COMPANY from edesign.asic_codename a where ucase(a.CUSTOMER_PROJNAME) in (select distinct ucase(CUSTOMER_PROJNAME) from EDESIGN.USER_EDESIGN_PROFILE) order by USERS_COMPANY");
            
         } else if (customerType.equals("FSE")) {
        
           // FSE
           //select distinct a.CUSTOMER_PROJNAME, a.USERS_COMPANY from edesign.asic_codename a where a.technology=? and a.version_no=? and ucase(a.CUSTOMER_PROJNAME) in (select distinct ucase(CUSTOMER_PROJNAME) from EDESIGN.USER_EDESIGN_PROFILE where EDGE_USERID='"+userid+"' union select distinct ucase(CUSTOMER_PROJNAME) from edesign.project_fse_detail where FSE_ID='"+userid+"' ) and ucase(a.users_company) in (select ucase(users_company) from edesign.chips_afs_userid where edge_userid='"+userid+"' union select ucase(users_company) from edesign.project_fse_detail where FSE_ID='"+userid+"' ) order by USERS_COMPANY for READ ONLY
            sb.append("select distinct a.CUSTOMER_PROJNAME, a.USERS_COMPANY from edesign.asic_codename a where ucase(a.CUSTOMER_PROJNAME) in (select distinct ucase(CUSTOMER_PROJNAME) from EDESIGN.USER_EDESIGN_PROFILE where EDGE_USERID='");
            sb.append(userid);
            sb.append("' union select distinct ucase(CUSTOMER_PROJNAME) from edesign.project_fse_detail where FSE_ID='");
            sb.append(userid);
            sb.append("' ) and ucase(a.users_company) in (select ucase(users_company) from edesign.chips_afs_userid where edge_userid='");
            sb.append(userid);
            sb.append("' union select ucase(users_company) from edesign.project_fse_detail where FSE_ID='");
            sb.append(userid);
            sb.append("' ) order by USERS_COMPANY");
         }
        
         sb.append(" FOR READ ONLY WITH UR");
         
         if (DebugPrint.getLevel() >= DebugPrint.DEBUG2) {
            DebugPrint.printlnd(DebugPrint.DEBUG2,
                                "getAsicProjects: " + sb.toString() +
                                " : " + user.toString());
         }
         
         pstmt = con.prepareStatement(sb.toString());
	
         rs = pstmt.executeQuery();
	
         while (rs.next()) {
            vDetails.addElement("ASIC#" + rs.getString("USERS_COMPANY") + "#" +
                                rs.getString("CUSTOMER_PROJNAME"));
         }
         
      } catch (SQLException e) {
         throw e;
      } finally {
         if (pstmt != null) try {
            pstmt.close();
         } catch(Exception ee) {
         }
      }
      
      return vDetails;
   }
   
   
   static public Vector getFSEProjects(String user) throws DBException {
      Vector ret = new Vector();
      
      Connection connection=null;
      DBConnection dbconnect = null;
      PreparedStatement pstmt =null;
      ResultSet rs=null;
      String sqls = null;
      
      try {
         try {
            dbconnect = DBSource.getDBConnection("AMT");
            connection = dbconnect.getConnection();
         } catch(NullPointerException npe) {
            throw new DBException("No connection found");
         }
         
        // Can't have FSE projects if your not an FSE
         Vector ent = getUserEntitlements(connection, user);
         if (!ent.contains("FSE")) {
            return ret;
         }
         
         StringBuffer sb = 
            new StringBuffer("select distinct a.CUSTOMER_PROJNAME, a.USERS_COMPANY from edesign.asic_codename a, edesign.projects_fse_detail fsd where ucase(a.CUSTOMER_PROJNAME) = ucase(fsd.CUSTOMER_PROJNAME) AND fsd.FSE_ID='");
         sb.append(user);
         sb.append("' ) and ucase(a.users_company) = fsd.ucase(users_company)");
         sb.append(" order by USERS_COMPANY");
        
         sb.append(" FOR READ ONLY WITH UR");
         
         if (DebugPrint.getLevel() >= DebugPrint.DEBUG2) {
            DebugPrint.printlnd(DebugPrint.DEBUG2,
                                "getFSEProjects: " + sqls.toString() +
                                " : " + user);
         }
         
         sqls = sb.toString();
         pstmt = connection.prepareStatement(sqls);
         
         rs = pstmt.executeQuery();
         
         while (rs.next()) {
            ret.addElement("ASIC#" + rs.getString("USERS_COMPANY") + "#" +
                           rs.getString("CUSTOMER_PROJNAME"));
         }
      } catch (SQLException e) {
         System.out.println("Exception during getFSEProjects: " + sqls);
         e.printStackTrace (System.out);
         dbconnect.destroyConnection(connection);
         connection=null;
         throw new DBException("SQLError");
      } finally {
         if (pstmt!=null){
            try{
               pstmt.close();
            }catch(SQLException e){}
         }
         dbconnect.returnConnection(connection);
      }
      
      return ret;
   }
   
   
  // Would not let me fully qualify anything but the collisions! How odd!
  // AND only when I choose decaf.users ... selecting amt.users causes
  // 
   static final String interesting_columns = 
   "a.IR_USERID,"                 +
   "a.EDGE_USERID,"               +
   "a.USER_FNAME,"                +
   "a.USER_LNAME,"                +
   "a.USER_COMPANY,"              +
   "d.ASSOC_COMPANY," +
   "a.USER_EMAIL,"                +
   "a.USER_CNTRY,"                +
   "a.COMPANY_STATECODE,"         +
   "d.USER_TYPE";
   
   
  // Still have to implement getprojects ... TODO
   static protected Vector getAMTInt(String v, 
                                     boolean regex,
                                     boolean userOrEmail,
                                     boolean getentitlements,
                                     boolean doAsicProjects,
                                     String  proj) throws DBException {
      Vector ret = new Vector();
      
      Connection connection=null;
      DBConnection dbconnect = null;
      PreparedStatement pstmt =null;
      ResultSet rs=null;
      String sqls = null;
      
      try {
         try {
            dbconnect = DBSource.getDBConnection("AMT");
            connection = dbconnect.getConnection();
         } catch(NullPointerException npe) {
            throw new DBException("No connection found");
         }
         StringBuffer sql = 
            new StringBuffer("SELECT " + interesting_columns + " from ");
            
         sql.append("amt.users a LEFT OUTER JOIN decaf.users d on ");
         
        // JMC 7/7/06 -- Added status = A check re; Larry Grant for taking
        //               unlocked users only
         sql.append("a.EDGE_USERID = d.USERID where a.status = 'A' AND ");
         
         if (userOrEmail) {
            sql.append("a.ir_userid ");
         } else {
            sql.append("a.user_email ");
         }
         
        // If regex, 
        //   - we will be using \ as escape (to match regex escape)
        //     Since they should already be escaped if they are to be included
        //     in search, we are good to go for any \., \* as well as \%, \_
        //   - escape any _ and %
        //   - tr '?*' '_%'
         if (regex) {
            boolean esc=false;
            char arr[] = v.toCharArray();
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
            v = sb.toString();
            
           // Only get a tiny bite with wildcards
            sql.append(" like ? ESCAPE '\\' FETCH FIRST 20 ROWS ONLY ");
         } else {
            sql.append(" = ? ");
         }
         
         
         
         sql.append(" FOR READ ONLY WITH UR");
         
         sqls = sql.toString();
         pstmt=connection.prepareStatement(sqls);
         
         pstmt.setString(1, v);
         
         DebugPrint.printlnd(DebugPrint.DEBUG2, 
                             "AMTQuery: getAMTBy" + 
                             (userOrEmail?"User":"Email") + 
                             ": " + v + ": " + sqls);
            
         rs=dbconnect.executeQuery(pstmt);
         while(rs.next()) {
            AMTUserInst amtuser = new AMTUserInst();
            
            amtuser.setUser(doTrim(rs.getString("IR_USERID")));
            if (amtuser.getUser().length() == 0) continue;
            
            String euser = doTrim(rs.getString("EDGE_USERID"));
            if (euser.length() > 8) {
               euser = euser.substring(0, 8);
            }
            amtuser.setEdgeUser(euser);
            amtuser.setFirstName(doTrim(rs.getString("USER_FNAME")));
            amtuser.setLastName(doTrim(rs.getString("USER_LNAME")));
            amtuser.setAMTCompany(doTrim(rs.getString("USER_COMPANY")));
            amtuser.setPOCCompany(doTrim(rs.getString("ASSOC_COMPANY")));
            amtuser.setEmail(doTrim(rs.getString("USER_EMAIL")));
            amtuser.setCountry(doTrim(rs.getString("USER_CNTRY")));
            amtuser.setState(doTrim(rs.getString("COMPANY_STATECODE")));
            
            String usertype = doTrim(rs.getString("USER_TYPE"));
            if (usertype.equalsIgnoreCase("I")) {
               amtuser.setCompany("IBM");
            } else if (usertype.equalsIgnoreCase("E")) {
               amtuser.setCompany(amtuser.getPOCCompany());
            } else {
               amtuser.setCompany("");
            }
            
            ret.addElement(amtuser);
         }
            
        // Took this out of above loop cause I get the following exception
        //   CLI0125E Function sequence error 
        //
        // Happens when running within WAS 4.x or 5.x and using certain 
        //  jdbc driver. Have to adjust some holdibility parm to have multiple
        //  result sets open at once http://dbforums.com/t410616.html
        // Just punt and unroll.
        
         if (doAsicProjects) getentitlements = true;
         
         if (ret.size() > 0) {
            Enumeration enum = ret.elements();
            while(enum.hasMoreElements()) {
               AMTUserInst amtuser = (AMTUserInst)enum.nextElement();
               
              // Update the bluepages info if this is an IBMer <check in routine>
               updateIBMBluepagesData(connection, amtuser);
               
               if (getentitlements) {
                  amtuser.setEntitlements(
                     getUserEntitlements(connection, 
                                         amtuser.getEdgeUser()));
                                         
                 // Silly special case code. Add DSGN_CONF entitlement if IBMer
                  if (amtuser.getCompany().equals("IBM")) {
                     if (!amtuser.isEntitled("DSGN_CONF")) {
                        amtuser.addEntitlement("DSGN_CONF");
                     }
                  }
               }
               
               if (doAsicProjects) {
                 // Need entitlements prior to calling this function
                  amtuser.addProjects(getAsicProjects(connection, amtuser));
               }
               
               if (proj != null) {
                  amtuser.addProjects(getCollabProjects(connection, 
                                                        amtuser.getEdgeUser(),
                                                        proj));
               }
            }
         }
      } catch (SQLException e) {
         System.out.println("Exception during getAMTByUser: " + sqls);
         e.printStackTrace (System.out);
         dbconnect.destroyConnection(connection);
         connection=null;
         throw new DBException("SQLError");
      } finally {
         if (pstmt!=null){
            try{
               pstmt.close();
            }catch(SQLException e){}
         }
         dbconnect.returnConnection(connection);
      }
      
      return ret;
   }
   
   static public Vector getAMTByUser(String user,
                                     boolean regex,
                                     boolean getent,
                                     boolean getasicproj,
                                     String proj) throws DBException {
      return getAMTInt(user, regex, true, getent, getasicproj, proj);
   }
   static public Vector getAMTByEmail(String email,
                                      boolean regex,
                                      boolean getent,
                                      boolean getasicproj,
                                      String proj) throws DBException {
      return getAMTInt(email, regex, false, getent, getasicproj, proj);
   }
   static public Vector getAMTByUser(String user) throws DBException {
      return getAMTInt(user, false, true, true, false, null);
   }
   static public Vector getAMTByEmail(String email) throws DBException {
      return getAMTInt(email, false, false, true, false, null);
   }
   
   
   static public void main(String args[]) {
      int i=0;
      DBConnection conn = null;
      DebugPrint.setLevel(DebugPrint.DEBUG3);
      if (args[0].equalsIgnoreCase("-amtdb")) {
         conn = new DBConnLocalPool();
         conn.setDriver     (args[++i]);
         conn.setURL        (args[++i]);
         conn.setInstance   (args[++i]);
         conn.setPasswordDir(args[++i]);
         i++;
         DBSource.addDBConnection("AMT", conn, false);
      } else {
         conn = new DBConnLocalPool();
         conn.setDriver     ("COM.ibm.db2.jdbc.app.DB2Driver");
         conn.setURL        ("jdbc:db2:edodcdb");
         conn.setInstance   ("edesign");
         conn.setPasswordDir(".");
         DBSource.addDBConnection("AMT", conn, false);
      }
      
      boolean usermode  = true;
      boolean emailmode = false;
      for(;i < args.length; i++) {
         
         try {
            
            if (args[i].equalsIgnoreCase("-user")) {
               usermode  = true;
               emailmode = false;
            } else if (args[i].equalsIgnoreCase("-email")) {
               usermode  = true;
               emailmode = true;
            } else if (args[i].equalsIgnoreCase("-bothadmin")) {
               includeMasterFSE  = true;
               includeFSEAdmin   = true;
            } else if (args[i].equalsIgnoreCase("-noadmin")) {
               includeMasterFSE  = false;
               includeFSEAdmin   = false;
            } else if (args[i].equalsIgnoreCase("-masterfse")) {
               includeMasterFSE  = true;
            } else if (args[i].equalsIgnoreCase("-fseadmin")) {
               includeFSEAdmin   = true;
            } else if (args[i].equalsIgnoreCase("-project")) {
               usermode  = false;
            } else if (args[i].equalsIgnoreCase("-debug")) {
               DebugPrint.setLevel(Integer.parseInt(args[++i]));
            } else if (usermode) {
               System.out.println("Looking for user " + args[i]);
               
               Vector users = null;
               if (!emailmode) {
                  users = AMTQuery.getAMTByUser(args[i], args[i].indexOf('*') >= 0,
                                                true, true, null);
               } else {
                  users = AMTQuery.getAMTByEmail(args[i], args[i].indexOf('*') >= 0,
                                                 true, true, null);
               }
               if (users != null) {
                  Enumeration enum = users.elements();
                  while(enum.hasMoreElements()) {
                     AMTUserInst user = (AMTUserInst) enum.nextElement();
                     System.out.println(user.toString());
                  }
               } else {
                  System.out.println("Not found: " + args[i]);
               }
            } else {
               System.out.println("Getting user list for project " + args[i]);
               Vector v = getUsersHavingProject(args[i]);
               if (v == null || v.size() == 0) {
                  System.out.println("No users for project found");
               } else {
                  Enumeration enum = v.elements();
                  while(enum.hasMoreElements()) {
                     String user = (String) enum.nextElement();
                     System.out.println(user);
                  }
               }
            }
         } catch(Exception e) {
            e.printStackTrace();
         }
      }
   }
   
  // Support a more abstract interface
   public Vector lookup(String name, 
                        boolean regex,
                        boolean getentitlements, 
                        boolean getprojects) throws DBException {
      return AMTQuery.getAMTByUser(name, regex, getentitlements, getprojects, null);
   }
                     
   public Vector lookupByEmail(String email, 
                               boolean regex,
                               boolean getentitlements, 
                               boolean getprojects) throws DBException {
      return AMTQuery.getAMTByEmail(email, regex, getentitlements, getprojects, null);
   }
                            
   public Vector lookupUsersWithProject(String projname) throws DBException { 
      return lookupUsersWithProject(projname, true);
   }
   
   public Vector lookupUsersWithProject(String projname, boolean retstring) 
      throws DBException { 
      
      Vector ret = null;
      Vector v1 = AMTQuery.getUsersHavingProject(projname);
      if (retstring) return v1;
      
     // Well, we should fix getUsersHavingProject to do a better job and
     //  return this info as a single query (if possible). For now, 
     //  brute force
      if (v1 != null) {
         ret = new Vector();
         Iterator it = v1.iterator();
         while(it.hasNext()) {
            String n = (String)it.next();
            Vector v = lookup(n, false, true, false);
            if (v != null && v.size() > 0) {
               AMTUser amtuser = (AMTUser)v.firstElement();
               ret.add(amtuser);
            }
         }
      }
      return ret;
   }
}
