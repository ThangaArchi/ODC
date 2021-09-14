package oem.edge.ed.odc.dropbox.server;

import  oem.edge.ed.odc.dropbox.common.*;
import  oem.edge.ed.odc.ftp.common.*;
import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.dsmp.server.*;
import java.lang.*;
import java.io.*;
import java.util.*;
import java.net.*;
import java.lang.reflect.*;
import java.util.zip.*;
import java.sql.*;

import oem.edge.ed.odc.util.*;
import oem.edge.ed.odc.util.db.SharedConnectionProxy;
import oem.edge.ed.util.SearchEtc;

import org.apache.log4j.Logger;

/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004                                     */
/*                                                                          */
/*     All Rights Reserved                                                  */
/*     US Government Users Restricted Rigts                                 */
/*                                                                          */
/*     The source code for this program is not published or otherwise       */
/*     divested of its trade secrets, irrespective of what has been         */
/*     deposited with the US Copyright Office.                              */
/*                                                                          */
/*   --------------------------------------------------------------------   */
/*     Please do not remove any of these commented lines  20 lines          */
/*   --------------------------------------------------------------------   */
/*                       Copyright Footer Check                             */

/*
 * Note, Care must be taken to avoid "NESTED result sets", where a connection
 *  rollback could occur. The SharedConnection code uses SavePoints to support
 *  light weight transactions, but WAS connection pooling code closes all 
 *  result sets when a savepoint rollback occurs. Totally bocus man!  So, 
 *  cases to avoid are calling a routine which uses SharedConnection. Having 
 *  locally nested result sets is fine if they live in the same savepoint
 *  region (which they should)
 */
public class DB2PackageManager extends PackageManager {	

   private static Logger log = Logger.getLogger(DB2PackageManager.class.getName());
   
  // DB2 bit in flags byte in session table ... sigh
  // All session flags have to be defaulted to FALSE
  //  Moved to User.java 
   
   static public boolean doROUR          = true;
   static public boolean doROURSubselect = false;
   
   static public String rour   = " FOR READ ONLY WITH UR ";
   static public String rourss = " FOR READ ONLY WITH UR ";
   
   static public String getROUR() {
      return doROUR?rour:" ";
   }
   static public String getROURSubselect() {
      return doROURSubselect?rourss:" ";
   }
   
  // JMC 7/27/04 - The default is once again to trust the encoded sizes
   static protected boolean trustSizes = true;
   static public boolean getTrustSizes()          { return trustSizes; }
   static public void    setTrustSizes(boolean v) { trustSizes = v;    }
   
   
  // Override open/close/setExpiration to do something
   public void openSession(User user) throws DboxException {
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;	
      StringBuffer sql = null;
      try{	
         connection=DbConnect.makeSharedConn();
         
         sql = new StringBuffer("values nextval for edesign.sessionseq");
         pstmt=connection.prepareStatement(sql.toString());
		 
         rs=DbConnect.executeQuery(pstmt);
         
         if (rs.next()) {
            user.setSessionId(rs.getLong(1));
         } else {
            throw new 
               DboxException("openSession:>> Error getting next seq for sesid", 
                             0);
         }
         
         pstmt.close(); pstmt = null;
         
         sql = new StringBuffer
            ("INSERT INTO EDESIGN.DBOXSESS (OWNER,COMPANY,SESSID,FLAGS,EXPIRES) VALUES(?,?,?,?,?)");
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setString(1, user.getName());
         pstmt.setString(2, user.getCompany());
         pstmt.setLong  (3, user.getSessionId());
         pstmt.setInt   (4, user.getSessionFlags());
         
        // Give 1 min till expire. Should be a followup call right behind this one
        //  to set actual expiration
         pstmt.setTimestamp(5, new java.sql.Timestamp((new java.util.Date()).getTime() + 
                                                      1000*60*1));
         
         if (DbConnect.executeUpdate(pstmt) != 1) {
            throw new DboxException("openSession:>> Error opening Session?? "
                                    + user.toString(), 0);
         }
         
         log.info("DB2PkgManager: openSession: " + user.toString());
         
        // Put projects (if more than 5) into table so can be queried
         Vector v = user.getProjects();
         if (v != null && v.size() > 0) {
            log.info("Saving projects into table, as we have " + v.size());
            Enumeration enum = v.elements();
            while(enum.hasMoreElements()) {
               sql = new StringBuffer("insert into edesign.userprojects ");
               sql.append("(userid, sessid, project) values(?,");
               sql.append(""+user.getSessionId()).append(",?)");
               pstmt.close();
               pstmt=connection.prepareStatement(sql.toString());
               pstmt.setString(1, user.getName());
               pstmt.setString(2, (String)enum.nextElement());
               if (DbConnect.executeUpdate(pstmt) != 1) {
                  log.error("Yuk! Could open session, but NOT add projects");
                  DboxAlert.alert(2, "Error adding session projects", 0, 
                                  "Update did NOT return 1 when trying to add" +
                                  " projects for " + user.toString());
               }
            }
         }
         
         connection.commit();
         
      } catch (SQLException e) {
         log.error("DB2PM.openSession: SQL except doing: " + 
                   (sql==null?"":sql.toString()));
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("openSession:>> SQL exception opening Session: " 
                          + user.toString(), DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
   
   public void closeSession(User user) throws DboxException {
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;	
      StringBuffer sql = null;
      try{	
         connection=DbConnect.makeSharedConn();
         sql = new StringBuffer("UPDATE EDESIGN.DBOXSESS SET END=? where SESSID=? and END is null");
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setTimestamp(1, new java.sql.Timestamp((new java.util.Date().getTime())));
         pstmt.setLong  (2, user.getSessionId());
         
         log.info(user.toString());
                           
         DboxException dbe = null;
         if (DbConnect.executeUpdate(pstmt) != 1) {
            dbe = new DboxException("closeSession:>> Error closing Session?? "
                                    + user.toString(), 0);
         }
         
        // Delete any projects stored in DB for this session
         pstmt.close();
         sql = new StringBuffer("delete from edesign.userprojects where sessid=?");
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1, user.getSessionId());
         DbConnect.executeUpdate(pstmt);
         
         if (dbe != null) throw dbe;
         
         connection.commit();
         
      } catch (SQLException e){
         log.error("DB2PM.closeSession: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("closeSession:>> SQL exception closing Session: " 
                          + user.toString(), DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
   
   public void validateSession(User user) throws DboxException {
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;	
      StringBuffer sql = null;
      try{	
         connection=DbConnect.makeSharedConn();
         sql = new StringBuffer("SELECT SESSID, FLAGS FROM EDESIGN.DBOXSESS WHERE SESSID=? AND END IS NULL AND EXPIRES > CURRENT TIMESTAMP ");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         long curtime = System.currentTimeMillis();
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1, user.getSessionId());
         
         if (log.isDebugEnabled()) {
            log.debug(user.toString());
         }
            
         rs = DbConnect.executeQuery(pstmt);
         if (!rs.next()) {
            throw new DboxException("validateSession:>> Session not valid ", 0);
         }
         
        // Set the session flags into user object
         user.setSessionFlags(rs.getInt(2));
         
         pstmt.close();
         
        // JMC 1/31/07 - Get project info from session
         sql = new StringBuffer("SELECT PROJECT FROM EDESIGN.USERPROJECTS WHERE SESSID=");
         sql.append(""+user.getSessionId());
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         rs = DbConnect.executeQuery(pstmt);
         while (rs.next()) {
            user.addProject(rs.getString(1));
         }
         
         connection.commit();
         
      } catch (SQLException e){
         log.error("DB2PM.isSessionValid: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("isSessionValid:>> SQL exception checking Session: " 
                          + user.toString(), DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
   
   public void setSessionExpiration(User user, java.util.Date d) throws DboxException {
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;	
      StringBuffer sql = null;
      try{	
         connection=DbConnect.makeSharedConn();
         sql = new StringBuffer("UPDATE EDESIGN.DBOXSESS SET EXPIRES=? where SESSID=? and END is null AND (EXPIRES is NULL OR EXPIRES < ?)");
         
         java.sql.Timestamp ts = new java.sql.Timestamp(d.getTime());
         pstmt=connection.prepareStatement(sql.toString());
         
         pstmt.setTimestamp(1, ts);
         pstmt.setLong     (2, user.getSessionId());
         pstmt.setTimestamp(3, ts);
         
         DboxException dbe = null;
         if (DbConnect.executeUpdate(pstmt) != 1) {
            throw new DboxException("setSessionExpiration:>> Error setting session expiration."
                                    + user.toString(), 0);
         }
         
         connection.commit();
         
      } catch (SQLException e){
         log.error("DB2PM.setSessionExpiration: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("setSessionExpiration:>> SQL exception setting expiration: " 
                          + user.toString(), DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
   
   public void setClientInfo(User user, String OS, 
                             String clientType) throws DboxException {
                             
      if (OS == null && clientType == null) return;
      
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;	
      StringBuffer sql = null;
      try{	
         connection=DbConnect.makeSharedConn();
         sql = new StringBuffer("UPDATE EDESIGN.DBOXSESS SET ");
         if (OS         != null) {
            sql.append("OS=?");
         }
         if (clientType != null) {
            if (OS != null) sql.append(", ");
            sql.append("CLIENTTYPE=?");
         }
         
         sql.append(" where SESSID=? and END is null");
         
         int idx = 1;
         pstmt=connection.prepareStatement(sql.toString());
         if (OS         != null) pstmt.setString(idx++, OS);
         if (clientType != null) pstmt.setString(idx++, clientType);
         
         pstmt.setLong(idx++, user.getSessionId());
         
         DboxException dbe = null;
         if (DbConnect.executeUpdate(pstmt) != 1) {
            throw new DboxException("setClientInfo:>> Error setting client info."
                                    + user.toString(), 0);
         }
         
         connection.commit();
         
      } catch (SQLException e){
         log.error("DB2PM.setClientInfo: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("setClientInfo:>> SQL exception setting client info: " 
                          + user.toString(), DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
   
   
   public int getSessionFlags(User user) throws DboxException {
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;	
      StringBuffer sql = null;
      try{	
         connection=DbConnect.makeSharedConn();
         sql = new StringBuffer("SELECT flags FROM EDESIGN.DBOXSESS WHERE SESSID=? AND END IS NULL");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1, user.getSessionId());
         
         rs = DbConnect.executeQuery(pstmt);
         if (!rs.next()) {
            throw new DboxException("getSessionFlag:>> Session not valid ", 0);
         }
         
         int ret = rs.getInt(1);
         connection.commit();
         
         return ret;
         
      } catch (SQLException e){
         log.error("DB2PM.getSessionFlags: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("getSessionFlag:>> SQL exception checking Session: " 
                          + user.toString(), DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
   
   public void setSessionFlags(User user, int flags) throws DboxException {
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;	
      StringBuffer sql = null;
      try{	
         connection=DbConnect.makeSharedConn();
         sql = new StringBuffer("UPDATE EDESIGN.DBOXSESS SET FLAGS=? WHERE SESSID=? AND END IS NULL");
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setInt (1, flags);
         pstmt.setLong(2, user.getSessionId());
         
         if (DbConnect.executeUpdate(pstmt) != 1) {
            throw new DboxException("setSessionFlag:>> Session not valid ", 0);
         }
         
        // Set the result into the user object
         user.setSessionFlags(flags);
         
         connection.commit();
         
      } catch (SQLException e){
         log.error("DB2PM.setSessionFlags: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("setSessionFlags:>> SQL exception checking Session: " 
                          + user.toString(), DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
   
   public void setSessionFlag(User user, int flag, boolean val) throws DboxException {
      int curflags = getSessionFlags(user);
      int newflags = curflags;
      
      if (val) newflags |= flag;
      else     newflags &= ~flag;
      
     // If the value is changed ... set it
      if (newflags != curflags) {
         setSessionFlags(user, newflags);
      }
   }
   
   public boolean getSessionFlag(User user, int flag) throws DboxException {
      int curflags = getSessionFlags(user);
      return (curflags & flag) == flag;
   }
   
   public DB2PackageManager(DboxFileAllocator fa) {
      super(fa);
      fileMgr = new DB2FileManager(this, fa);
   }
   
  // Clean out 'old' session data (projects)
   public void cleanSessionData() throws DboxException {
      PreparedStatement pstmt = null;
      Connection connection=null;
      StringBuffer sql = null;
      try {
      
        // Use non-shared connection to limit locking consequences
         connection=DbConnect.makeConn();
         
        // Close all sessions which have expired
         sql = new StringBuffer("UPDATE EDESIGN.DBOXSESS SET END=EXPIRES where END is null AND EXPIRES <= CURRENT TIMESTAMP");
         
         pstmt=connection.prepareStatement(sql.toString());
         int num = DbConnect.executeUpdate(pstmt);
         
         pstmt.close();
         
         log.info("Clean session data resulted in Expiring  " + 
                  num + " sessions");
                  
        // Delete all userproject entries for sessions which are closed
         sql = new StringBuffer("delete from edesign.userprojects up where sessid in (select ds.sessid from edesign.dboxsess ds where ds.sessid = up.sessid and ds.end is not null)");
         
         pstmt=connection.prepareStatement(sql.toString());
         num = DbConnect.executeUpdate(pstmt);
         
         log.info("Cleaning session data resulted in deletion of " + 
                  num + " userproject rows");
         
        
        //connection.commit();
         
      } catch (SQLException e){
         log.error("DB2PM.cleanSessionData: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("cleanSessionData:>> SQL exception cleaning sessdata " 
                          , DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
         

  // We were blowing past the size limits for SQL thru JDBC (complaining 
  // running out of heap)
  //
  // Rather than rework the real routine, just call it multiple times. 
  // It only processes at most, 50 packages at a time
   public int cleanExpiredPackages(int tot) throws DboxException {
      int ret = 0;
      int lint;
      
      cleanSessionData();
      
      while((tot < 0 || ret < tot) && (lint=cleanExpiredPackagesInt()) > 0) {
         ret += lint;
      }
      return ret;
   }
         
  // Note, if this is called to help free memory outside of CRON, there could be deadlocks
  //  as it will be on an existing shared connection with LOCKS AND ... another app WS call
  //  may need to do the clean at the same time ... [they will bump heads ...
  //
  // Flash ... Added saveSharedConnection code and put this on its own transaction and commit
  //  after each clean package to minimize the window. Also unrolled nested access. If two
  //  apps DO bump heads here, should not kill them now. One will win and continue on one
  //  will close up shop and try again. Should not be deadlocks, as they will collide on
  //  package lock then package will be gone.
   public int cleanExpiredPackagesInt() throws DboxException {
      PreparedStatement pstmt1 = null;
      ResultSet rs1=null;
      SharedConnection connection=null;
      SharedConnectionProxy savedconnection=null;
      StringBuffer sql = null;
      int num = 0;
      try {
      
        // We isolate this method from the callers transation so commit means commit NOW!
         savedconnection=DbConnect.saveSharedConn();
         connection=DbConnect.makeSharedConn();
         
         long curtime = System.currentTimeMillis();
         
         sql = 
            new StringBuffer("SELECT PKGID,EXPIRATION,OWNERID,PKGNAME,COMPANY,PKGSTAT FROM EDESIGN.PACKAGE WHERE EXPIRATION <= ? AND DELETED is null FETCH FIRST 50 ROWS ONLY");
            
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
            
         pstmt1=connection.prepareStatement(sql.toString()); 
         
         pstmt1.setLong(1, curtime);
         
         rs1=DbConnect.executeQuery(pstmt1);
         
        // put up to 50 pkgid values into vector along with logging info
         Vector v = new Vector();
         while (rs1.next() && num < 50) {
            int i=1;
            long pkgid     = rs1.getLong(i++);
            long expires   = rs1.getLong(i++);
            String ownerid = rs1.getString(i++);
            String pkgname = rs1.getString(i++);
            String company = rs1.getString(i++);
            byte   stat    = rs1.getByte(i++);
            
            PackageInfo pinfo = new PackageInfo();
            pinfo.setPackageId(pkgid);
            pinfo.setPackageExpiration(expires);
            pinfo.setPackageOwner(ownerid);
            pinfo.setPackageName(pkgname);
            pinfo.setPackageCompany(company);
            pinfo.setPackageStatus(stat);
            
            v.add(pinfo);
            
            num++;  // count number successfully cleaned
         }
         
         pstmt1.close();
         
        // Iterate over each package, and commit as soon as its handled
        //
        // Also, I use a randomized iterator to help support the case where we  
        //  have multiple reapers running at once. This will let them ALL be more 
        //  productive as they will most likely work on different packages.
        //
         Iterator it = SearchEtc.randomize(v).iterator();
         while(it.hasNext()) {
         
            PackageInfo pinfo = (PackageInfo)it.next();
            long pkgid = pinfo.getPackageId();
         
            StringBuffer buf = new StringBuffer("Deleting expired package [");
            buf.append((new java.util.Date(pinfo.getPackageExpiration())).toString());
            buf.append("] Name[").append(pinfo.getPackageName()).append("] or ").append(pkgid);
            buf.append(" Owner[").append(pinfo.getPackageOwner()).append(" from ");
            buf.append(pinfo.getPackageCompany()).append("] status was ").append(pinfo.getPackageStatus());
         
            log.info(buf.toString());
            boolean worked = cleanPackageInt(pkgid, false);
            if (!worked) {
               log.warn("ERROR deleting package " + pkgid + 
                        " ... perhaps another reaper is running");
            }
            
            connection.commit();
         }   
         
         connection.commit();
         
      } catch (DboxException dbe) {
         log.error("DB2PM.cleanExpPackInt: DBoxError: ");
         DboxAlert.alert(dbe);  // Generic Alert
         throw new 
            DboxException("DB2PackMgr:>> Exception during cleanExpiredPackages", 
                          0);
         
      } catch (SQLException e) {
         log.error("DB2PM.cleanExpPackInt: SQL except: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null;
         pstmt1=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2PackMgr:>> Exception during cleanExpiredPackages", 
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt1 != null) try {pstmt1.close();} catch(SQLException e) {}
         DbConnect.returnConnection(connection);
         DbConnect.restoreSharedConn(savedconnection);
      }   
      return num;
   }
         
         
  // True if user is in ACL list of a package containing this file
   public boolean canAccessFile(User user, DboxFileInfo info, 
                                boolean includeOwner) throws DboxException {
      PreparedStatement pstmt =null;
      ResultSet rs = null;
      StringBuffer sql = null;
      SharedConnection connection=null;
      boolean ret=false;
      boolean superuser = false;
    
      try{
		
         connection=DbConnect.makeSharedConn(); 
         sql = new StringBuffer("SELECT DISTINCT P.PKGID, P.FLAGS FROM ");
         sql.append("EDESIGN.PKGACL PACL, EDESIGN.PACKAGE P ");
         
         if (policy_supportLookup) {
            sql.append(",EDESIGN.ALLOWSEND a ");
         }         
         
         sql.append("WHERE p.DELETED is null and pacl.DELETED is null AND (");
         
         if (includeOwner) {
            if (getPrivilegeLevel(user.getName()) >= PRIVILEGE_SUPER_USER) {
              // Don't know if I can use TRUE or constant = constant ...
               superuser = true;
               sql.append(" p.ownerid='na' OR p.ownerid != 'na' OR ");
            } else {
               sql.append(" p.ownerid=? OR ");
            }
         }
         
        // If not owner, can only access package if its complete
         sql.append("(p.PKGSTAT=? ");
            
         boolean addSameCompany = false;
         boolean addLookup      = false;
            
        // If Policy is in force, do we have access
         if (policy_supportSameCompany || 
             policy_supportIBM         ||
             policy_supportLookup) {
            
           // Only do it if we are NOT IBM OR not supportIBM policy
            if (!policy_supportIBM ||
                !user.getCompany().equalsIgnoreCase("IBM")) {
                
               boolean didit = false;
               
               sql.append(" AND (");
               if (policy_supportIBM) {
                  sql.append(" p.company='IBM' ");
                  didit = true;
               }
               
               if (policy_supportSameCompany) {
                  if (didit) {
                     sql.append(" OR ");
                  }
                  sql.append(" (p.company=? and p.company != '') ");
                  addSameCompany = true;
                  didit = true;
               }
               
               if (policy_supportLookup) {
                  if (didit) {
                     sql.append(" OR ");
                  }
                  
                  sql.append(
                     "(((a.FROMTYPE='U' AND a.FROMNAME in (p.OWNERID,'*')) OR ");
                  sql.append(
                     "(a.FROMTYPE='C' AND a.FROMNAME in (p.COMPANY,'*')) ) AND");
                  sql.append(
                     "((a.TOTYPE ='U' AND a.TONAME   in (?, '*'))          OR ");
                  sql.append(
                     " (a.TOTYPE ='C' AND a.TONAME   in (?, '*'))))");
                  
                  addLookup = true;
                  didit = true;
               }
               
               sql.append(")");
            }
         }
         
        // Check direct ACL, Project ACL
         sql.append(" AND (P.PKGID=PACL.PKGID AND ");
         sql.append(" (USERTYPE=? AND (pacl.USERID=? OR pacl.USERID='*')) ");
         
        // If this guy has has projects ...
         Vector projects = user.getProjects();
         Enumeration enum = projects.elements();
         if (enum.hasMoreElements()) {
         
           // 5 or fewer
            if (projects.size() <= 5) {
               sql.append(" OR (USERTYPE=? AND pacl.USERID in ( ");
            
               int num=0;
               while(enum.hasMoreElements()) {
                  String p = (String)enum.nextElement();
                  if (num++ > 0) sql.append(",?");
                  else           sql.append("?");
               }
               sql.append("))");
            } else {
               sql.append(" OR (USERTYPE=? AND pacl.USERID in ");
               sql.append(" (select project from edesign.userprojects where");
               sql.append(" sessid=").append(user.getSessionId()).append(")) ");
               log.info("More than 5 projects ... use projects table!");
            }
         }
         
        // Check Group ACLs
         sql.append(" OR (USERTYPE=? AND ");
         sql.append("pacl.USERID in (select gm.GROUPNAME from edesign.GROUPMEMBERS gm where gm.GROUPNAME=pacl.USERID AND gm.USERID=?)) ");
         
         sql.append(")))");
         
         
         sql.append(" AND P.PKGID IN (SELECT DISTINCT PKGID ");
         sql.append("FROM EDESIGN.FILETOPKG FP WHERE FP.FILEID=? AND fp.DELETED is null");
         
        // ISOLATION - SUBSELECT
         sql.append(DB2PackageManager.getROURSubselect());
         sql.append(")");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString()); 
         
         int idx = 1;
         
         if (includeOwner && !superuser) {
            pstmt.setString(idx++, user.getName());
         }
         
         pstmt.setByte(idx++, DropboxGenerator.STATUS_COMPLETE);
         if (addSameCompany) {
            pstmt.setString(idx++, user.getCompany());
         }
         if (addLookup) {
            pstmt.setString(idx++, user.getName());
            pstmt.setString(idx++, user.getCompany());
         }
         
         pstmt.setByte  (idx++, DropboxGenerator.STATUS_NONE);
         pstmt.setString(idx++, user.getName());
         
         enum = projects.elements();
         if (enum.hasMoreElements()) {
           // We have to add this regardless
            pstmt.setByte(idx++, DropboxGenerator.STATUS_PROJECT );
            if (projects.size() <= 5) {
               while(enum.hasMoreElements()) {
                  String p = (String)enum.nextElement();
                  pstmt.setString(idx++, p);
               }
            }
         }
         
         pstmt.setByte  (idx++, DropboxGenerator.STATUS_GROUP);
         pstmt.setString(idx++, user.getName());
         
         pstmt.setLong(idx++, info.getFileId());

         rs=DbConnect.executeQuery(pstmt);
		
        // FIXNESTED FIXNESTED FIXNESTED 
         while (rs.next()) {
           // if package is NOT ITAR or IS ITAR and user is certified ... good
            long pkgid = rs.getLong(1);
            if ((rs.getInt(2) & PackageInfo.ITAR) != 0) {
               if (!user.isUserItarCertified()) {
                  log.info("User denied access to file/package based on ITAR: User " + user.getName() + " pkgid = " + pkgid + " fileid = " + info.getFileId());
               } else {
                  ret = true;
                  break;
               }
            } else {
               ret = true;
               break;
            }
         }         
        	 
         connection.commit();
                 
      } catch (Exception e){
         log.error("DB2PM.canAccessFile: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2PackageManager: canAccessFile:>> SQL failure", 
                          DboxException.SQL_ERROR);
      
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
      return ret;
   }
	   
 
      
  // True if user has this file in a package she owns
   public boolean isFileOwner(User user, DboxFileInfo info) 
      throws DboxException{
      
      return super.isFileOwner(user, info);
   }
      
      
   public DboxPackageInfo lookupPackage(long packid) throws DboxException {
      return lookupPackage(packid, (User)null);
   }
   public DboxPackageInfo lookupPackage(long packid, 
                                        User user) throws DboxException {
            
      DboxPackageInfo info =new DB2DboxPackageInfo (this); 	
      PreparedStatement pstmt =null;
      ResultSet rs = null;
      
      SharedConnection connection=null;
      StringBuffer sql = null;		 
      
      try{
			
         connection=DbConnect.makeSharedConn();
         sql = new StringBuffer("SELECT PKGNAME, DESC, EXPIRATION, CREATED, ");
         
        // PKGCHANGE Used to have pkgsize and fileentry, now calculate it
        // 7/27/04 JMC -  now allow selection of trust
         if (getTrustSizes()) {
            sql.append(" PKGSIZE, FILEENTRY, ");
         } else {
            sql.append(" VALUE((SELECT SUM(fc.COMPONENTSIZE) from EDESIGN.FILECOMPONENT fc,EDESIGN.FILETOPKG fp WHERE fp.pkgid=? AND fc.FILEID=fp.FILEID AND fc.DELETED is null AND fp.DELETED is null ");

           // ISOLATION - SUBSELECT
            sql.append(DB2PackageManager.getROURSubselect());
            sql.append("), 0),");
         
            sql.append(" VALUE((SELECT COUNT(fp.FILEID) from EDESIGN.FILETOPKG fp WHERE fp.pkgid=? and fp.DELETED is null ");
            
           // ISOLATION - SUBSELECT
            sql.append(DB2PackageManager.getROURSubselect());
            sql.append("), 0),");
         }
         
         sql.append(" PKGSTAT, OWNERID, COUNTRY, COMPANY, COMMITTED, FLAGS, POOLID ");
         sql.append("FROM EDESIGN.PACKAGE p WHERE p.PKGID=? AND p.DELETED is null ");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString() );
         
         int idx = 1;
         
         if (!getTrustSizes()) {
            pstmt.setLong(idx++,packid);
            pstmt.setLong(idx++,packid);
         }
         
         pstmt.setLong(idx++,packid);
         
         rs=DbConnect.executeQuery(pstmt);
			
		
         if (rs.next())
         {
            info.setPackageId(packid);
            info.setPackageName(rs.getString(1));
            info.setPackageDescription(rs.getString(2));
            info.setPackageExpiration(rs.getLong(3));
            info.setPackageCreation(rs.getTimestamp(4).getTime());
            info.setPackageSize(rs.getLong(5));
            info.setPackageNumElements(rs.getInt(6)); 
            info.setPackageStatus(rs.getByte(7));
            info.setPackageOwner(rs.getString(8));
            info.setPackageCountry(rs.getString(9));
            info.setPackageCompany(rs.getString(10));
            java.sql.Timestamp tstamp = rs.getTimestamp(11);
            if (tstamp != null) {
               info.setCommitTime(tstamp.getTime());
            }
            info.setPackageFlags((byte)rs.getInt(12));
            info.setPackagePoolId(rs.getInt(13));
         }
         else
            throw new DboxException("LookupPackage:>> package " + packid +
                                    " not found", 
                                    DropboxGenerator.ERROR_PACKAGE_DOES_NOT_EXIST); 
		
         connection.commit();
                
      } catch (SQLException e) {
         log.error("DB2PM.lookupPackage: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2PackageManager: lookupPackage:>> SQL failure", 
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
     
      if (info != null) updatePackageStatus(info, user);   
     
      return info; 		  
   }
      
   public boolean canAccessPackage(User user, 
                                   long packid, 
                                   boolean includeOwner) throws DboxException {
                                      
      boolean result=false;
      String ownerid=null;
      PreparedStatement pstmt =null;
      ResultSet rs = null;
      boolean superuser = false;
      
      int pkgflags = 0;
		 
      StringBuffer sql = null;      
      SharedConnection connection=null;
      try{
			
         connection=DbConnect.makeSharedConn();
         sql = new StringBuffer ("SELECT DISTINCT p.PKGID, p.FLAGS FROM ");
            
         sql.append("EDESIGN.PACKAGE p,EDESIGN.PKGACL pacl ");
         
         if (policy_supportLookup) {
            sql.append(",EDESIGN.ALLOWSEND a ");
         }
         
         sql.append("WHERE p.PKGID=? AND p.DELETED is null AND pacl.DELETED is null AND (");
         
         if (includeOwner) {
            if (getPrivilegeLevel(user.getName()) >= PRIVILEGE_SUPER_USER) {
              // Don't know if I can use TRUE or constant = constant ...
               sql.append(" p.ownerid='na' OR p.ownerid != 'na' OR ");
               superuser = true;
            } else {
               sql.append(" p.ownerid=? OR ");
            }
         }
         
        // If not owner, can only access package if its complete
         sql.append("(p.PKGSTAT=? ");
            
         boolean addSameCompany = false;
         boolean addLookup      = false;
            
        // If Policy is in force, do we have access
         if (policy_supportSameCompany || 
             policy_supportIBM         ||
             policy_supportLookup) {
            
           // Only do it if we are NOT IBM OR not supportIBM policy
            if (!policy_supportIBM ||
                !user.getCompany().equalsIgnoreCase("IBM")) {
                
               boolean didit = false;
               
               sql.append(" AND (");
               if (policy_supportIBM) {
                  sql.append(" p.company='IBM' ");
                  didit = true;
               }
               
               if (policy_supportSameCompany) {
                  if (didit) {
                     sql.append(" OR ");
                  }
                  sql.append(" (p.company=? and p.company != '') ");
                  addSameCompany = true;
                  didit = true;
               }
               
               if (policy_supportLookup) {
                  if (didit) {
                     sql.append(" OR ");
                  }
                  
                  sql.append(
                     "(((a.FROMTYPE='U' AND a.FROMNAME in (p.OWNERID,'*')) OR ");
                  sql.append(
                     "(a.FROMTYPE='C' AND a.FROMNAME in (p.COMPANY,'*')) ) AND");
                  sql.append(
                     "((a.TOTYPE ='U' AND a.TONAME   in (?, '*'))          OR ");
                  sql.append(
                     " (a.TOTYPE ='C' AND a.TONAME   in (?, '*'))))");
                  
                  addLookup = true;
                  didit = true;
               }
               
               sql.append(")");
            }
         }
         
        // Check direct ACL, Project ACL
         
         sql.append(" AND (p.PKGID=pacl.PKGID AND ");
         sql.append("(USERTYPE=? AND (pacl.USERID=? OR pacl.USERID='*')) ");
         
         
        // If this guy has has projects ...
         Vector projects = user.getProjects();
         Enumeration enum = projects.elements();
         if (enum.hasMoreElements()) {
         
           // 5 or fewer
            if (projects.size() <= 5) {
               sql.append(" OR (USERTYPE=? AND pacl.USERID in ( ");
            
               int num=0;
               while(enum.hasMoreElements()) {
                  String p = (String)enum.nextElement();
                  if (num++ > 0) sql.append(",?");
                  else           sql.append("?");
               }
               sql.append("))");
            } else {
               sql.append(" OR (USERTYPE=? AND pacl.USERID in ");
               sql.append(" (select project from edesign.userprojects where");
               sql.append(" sessid=").append(user.getSessionId()).append(")) ");
               log.info("More than 5 projects ... use projects table!");
            }
         }
         
        // Check Group ACLs
         sql.append(" OR (USERTYPE=? AND ");
         sql.append("pacl.USERID in (select gm.GROUPNAME from edesign.GROUPMEMBERS gm where gm.GROUPNAME=pacl.USERID AND gm.USERID=?)) ");
         
         sql.append(")))");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString()); 
         
         int idx = 1;
         pstmt.setLong(idx++, packid);
         
         if (includeOwner && !superuser) {
            pstmt.setString(idx++, user.getName());
         }
         
         pstmt.setByte(idx++, DropboxGenerator.STATUS_COMPLETE);
         if (addSameCompany) {
            pstmt.setString(idx++, user.getCompany());
         }
         if (addLookup) {
            pstmt.setString(idx++, user.getName());
            pstmt.setString(idx++, user.getCompany());
         }
         
         pstmt.setByte  (idx++, DropboxGenerator.STATUS_NONE);
         pstmt.setString(idx++, user.getName());
         
         enum = projects.elements();
         if (enum.hasMoreElements()) {
           // We have to add this regardless
            pstmt.setByte(idx++, DropboxGenerator.STATUS_PROJECT );
            if (projects.size() <= 5) {
               while(enum.hasMoreElements()) {
                  String p = (String)enum.nextElement();
                  pstmt.setString(idx++, p);
               }
            }
         }
         
         pstmt.setByte  (idx++, DropboxGenerator.STATUS_GROUP);
         pstmt.setString(idx++, user.getName());
         
         rs=DbConnect.executeQuery(pstmt);
         
         if (rs.next()) {
            pkgflags = rs.getInt(2);
            result=true;
         }
         
         connection.commit();
         
      } catch (Exception e) {
         log.error("DB2PM.canAccessPackage: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2PackageManager: canAccessPackage:>> SQL failure", 
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
      
     // Deny access if ITAR package and user not ITAR certified. This could
     //  result in a package owner being cut off from his package!
      if (result) {
         if ((pkgflags & PackageInfo.ITAR) != 0) {
            if (!user.isUserItarCertified()) {
               result = false;
               log.info("User denied access to package based on ITAR: User " + user.getName() + " pkgid = " + packid);
            }
         }
      }
      
      if (log.isDebugEnabled()) {
         log.debug("Returning = " + result);
      }
      
      return result;	
   }
                        
   public Vector filesMatchingExpr(String exp, 
                                   boolean isReg) throws DboxException {
      return fileMgr.filesMatchingExpr(exp, isReg);
   }
      
   public Vector packagesMatchingExpr(String exp, boolean isReg,
                                      boolean filterMark, 
                                      boolean filterComplete) 
      throws DboxException {
      throw new DboxException("packagesMatchingExpr(exp, bool) NOT COMPLETED!! TODO");
   }                                      
   
   public Vector packagesMatchingExprWithAccess(User user, 
                                                boolean ownerOrAccessor,
                                                String exp, boolean isReg,
                                                boolean filterMark, 
                                                boolean filterComplete) 
      throws DboxException {
      
      Vector ret = new Vector();
      
      boolean packageByName = false;
         
      if (isReg && exp != null) {
         exp = SearchEtc.sqlEscape(exp, true);
         packageByName = true;
      } else if (!isReg && exp != null && exp.length() > 0) {
         packageByName = true;
         
        // We do a like, and take anything LIKE what is passed (startsWith)
        // Flash ... removed the startwith ... now that regex works, they should
        //  use that to do a startswith
         exp = SearchEtc.sqlEscape(exp, false); // + "%";
      }
         
      PreparedStatement pstmt =null;
      ResultSet rs =null;		 
      
     // Filter yuk
      StringBuffer filt = new StringBuffer();
      
      if (filterMark) {
         filt.append(" AND NOT EXISTS (SELECT facl1.PKGID FROM EDESIGN.FILEACL facl1 WHERE facl1.PKGID=p.PKGID AND facl1.USERID=? AND facl1.FILEID=0 AND facl1.DELETED is null ");
         
        // ISOLATION - SUBSELECT
         filt.append(DB2PackageManager.getROURSubselect());
         filt.append(") ");
      }
      
      if (filterComplete) {
         filt.append(" AND (SELECT COUNT(*) FROM EDESIGN.FILEACL facl2 WHERE facl2.PKGID=p.PKGID AND facl2.FILEID != 0 AND facl2.USERID=? AND facl2.DLOADSTAT=? AND facl2.DELETED is null ");
         
        // ISOLATION - SUBSELECT
         filt.append(DB2PackageManager.getROURSubselect());
         
        // 
        // PKGCHANGE Used to have fileentry, now calculate it
        // 7/27/04 JMC -  now allow selection of trust
         if (getTrustSizes()) {
            filt.append(") < p.FILEENTRY ");         
         } else {
            filt.append(") < VALUE((SELECT COUNT(DISTINCT fp.FILEID) from EDESIGN.FILETOPKG fp where fp.PKGID=p.PKGID AND fp.DELETED is null ");
         
           // ISOLATION - SUBSELECT
            filt.append(DB2PackageManager.getROURSubselect());
            
            filt.append("),0) ");
         }
      }
      
      StringBuffer sql = null;		 
      
      SharedConnection connection=null;
      DboxPackageInfo info = null;
      try{
         connection=DbConnect.makeSharedConn();
         if (ownerOrAccessor){
         
            boolean superuser = false;
            
            sql = new StringBuffer(
              "SELECT DISTINCT p.PKGID, p.FLAGS FROM EDESIGN.PACKAGE p ");
            
           // If not a super user, WHERE with ownerid
            if (getPrivilegeLevel(user.getName()) < PRIVILEGE_SUPER_USER) {
               sql.append(" WHERE p.OWNERID=? ");
            } else {
               sql.append(" WHERE p.OWNERID='na' OR p.OWNERID != 'na' ");
               superuser = true;
            }
            
            if (filterMark || filterComplete) {
               sql.append(filt.toString());
            }
            
            if (packageByName) {
               sql.append(" AND p.pkgname like ? escape '\\' ");
            }
            
            sql.append(" AND p.DELETED is null ");
               
           // ISOLATION
            sql.append(DB2PackageManager.getROUR());
               
            pstmt=connection.prepareStatement(sql.toString());
            int idx = 1;
            
            if (!superuser) {
               pstmt.setString(idx++,user.getName());
            }
            
            if (filterMark) {
               pstmt.setString(idx++,user.getName());
            }
            if (filterComplete) {
               pstmt.setString(idx++,user.getName());
               pstmt.setByte(idx++, DropboxGenerator.STATUS_COMPLETE);
            }
            
            if (packageByName) {
               pstmt.setString(idx++,exp);
            }
                        
            rs=DbConnect.executeQuery(pstmt);
            
           // FIXNESTED FIXNESTED FIXNESTED 
            while (rs.next()){
               boolean doit = true;
               long pkgid = rs.getLong(1);
               long pkgflags = rs.getInt(2);
               if ((pkgflags & PackageInfo.ITAR) != 0) {
                  if (!user.isUserItarCertified()) {
                     doit = false;
                     log.info("User denied access to package based on ITAR: User " + user.getName() + " pkgid = " + pkgid);
                  }
               }
               
               if (doit) {
                  try {
                     ret.addElement(lookupPackage(rs.getLong(1), user));
                  } catch(DboxException dbeInner) {
                    // If we get an error, an ALLOWABLE error is that the
                    // package in question does not exist. Could have been in the
                    // process of being deleted 
                     if (dbeInner.getErrorCode() != 
                         DropboxGenerator.ERROR_PACKAGE_DOES_NOT_EXIST) {
                        throw dbeInner;
                     }
                  }
               }
            }
         } else {	
            sql = new StringBuffer(
               "SELECT DISTINCT p.PKGID, p.FLAGS FROM ");
                  
            sql.append("EDESIGN.PACKAGE p,EDESIGN.PKGACL pacl ");
            if (policy_supportLookup) {
               sql.append(",EDESIGN.ALLOWSEND a ");
            }
                  
           // If the package is complete
            sql.append("WHERE p.PKGID=pacl.PKGID AND p.PKGSTAT=? ");
            
            if (filterMark || filterComplete) {
               sql.append(filt.toString());
            }
            
            if (packageByName) {
               sql.append(" AND p.pkgname like ? escape '\\' ");
            }
            
            sql.append(" AND p.DELETED is null AND pacl.DELETED is null ");
            
            boolean addSameCompany = false;
            boolean addLookup      = false;
            
           // If Policy is in force, do we have access
            if (policy_supportSameCompany || 
                policy_supportIBM         ||
                policy_supportLookup) {
               
              // Only do it if we are NOT IBM OR not supportIBM policy
               if (!policy_supportIBM ||
                   !user.getCompany().equalsIgnoreCase("IBM")) {
                  
                  boolean didit = false;
                  
                  sql.append(" AND (");
                  if (policy_supportIBM) {
                     sql.append(" p.company='IBM' ");
                     didit = true;
                  }
                  
                  if (policy_supportSameCompany) {
                     if (didit) {
                        sql.append(" OR ");
                     }
                     sql.append(" (p.company=? and p.company != '') ");
                     addSameCompany = true;
                     didit = true;
                  }
                  
                  if (policy_supportLookup) {
                     if (didit) {
                        sql.append(" OR ");
                     }
                     
                     sql.append(
                        "(((a.FROMTYPE='U' AND a.FROMNAME in (p.OWNERID,'*')) OR ");
                     sql.append(
                        "(a.FROMTYPE='C' AND a.FROMNAME in (p.COMPANY,'*')) ) AND");
                     sql.append(
                        "((a.TOTYPE ='U' AND a.TONAME   in (?, '*'))          OR ");
                     sql.append(
                        " (a.TOTYPE ='C' AND a.TONAME   in (?, '*'))))");
                     
                     addLookup = true;
                     didit = true;
                  }
                  
                  sql.append(")");
               }
            }
            
           // Check direct ACL, Project ACL
            sql.append(" AND ((USERTYPE=? AND (pacl.USERID=? OR pacl.USERID='*')) ");
            
           // If this guy has has projects ...
            Vector projects = user.getProjects();
            Enumeration enum = projects.elements();
            if (enum.hasMoreElements()) {
               
              // 5 or fewer
               if (projects.size() <= 5) {
                  sql.append(" OR (USERTYPE=? AND pacl.USERID in ( ");
                  
                  int num=0;
                  while(enum.hasMoreElements()) {
                     String p = (String)enum.nextElement();
                     if (num++ > 0) sql.append(",?");
                     else           sql.append("?");
                  }
                  sql.append("))");
               } else {
                  sql.append(" OR (USERTYPE=? AND pacl.USERID in ");
                  sql.append(" (select project from edesign.userprojects where");
                  sql.append(" sessid=").append(user.getSessionId()).append(")) ");
                  log.info("More than 5 projects ... use projects table!");
               }
            }
            
           // Check Group ACLs
            sql.append(" OR (USERTYPE=? AND ");
            sql.append("pacl.USERID in (select gm.GROUPNAME from edesign.GROUPMEMBERS gm where gm.GROUPNAME=pacl.USERID AND gm.USERID=?)) ");
            
            sql.append(")");
            
           // ISOLATION
            sql.append(DB2PackageManager.getROUR());
            
            pstmt=connection.prepareStatement(sql.toString()); 
            
            int idx = 1;
            pstmt.setByte(idx++, DropboxGenerator.STATUS_COMPLETE);
            
            if (filterMark) {
               pstmt.setString(idx++,user.getName());
            }
            if (filterComplete) {
               pstmt.setString(idx++,user.getName());
               pstmt.setByte(idx++, DropboxGenerator.STATUS_COMPLETE);
            }
            
            if (packageByName) {
               pstmt.setString(idx++,exp);
            }
            
            if (addSameCompany) {
               pstmt.setString(idx++, user.getCompany());
            }
            
            if (addLookup) {
               pstmt.setString(idx++, user.getName());
               pstmt.setString(idx++, user.getCompany());
            }
            
            pstmt.setByte  (idx++, DropboxGenerator.STATUS_NONE);
            pstmt.setString(idx++, user.getName());
            
            enum = projects.elements();
            if (enum.hasMoreElements()) {
              // We have to add this regardless
               pstmt.setByte(idx++, DropboxGenerator.STATUS_PROJECT );
               if (projects.size() <= 5) {
                  while(enum.hasMoreElements()) {
                     String p = (String)enum.nextElement();
                     pstmt.setString(idx++, p);
                  }
               }
            }
            
            pstmt.setByte  (idx++, DropboxGenerator.STATUS_GROUP);
            pstmt.setString(idx++, user.getName());
            
            rs=DbConnect.executeQuery(pstmt);
            
           // FIXNESTED FIXNESTED FIXNESTED 
            while(rs.next()){
            
               boolean doit = true;
               long pkgid = rs.getLong(1);
               long pkgflags = rs.getInt(2);
               if ((pkgflags & PackageInfo.ITAR) != 0) {
                  if (!user.isUserItarCertified()) {
                     doit = false;
                     log.info("User denied access to package based on ITAR: User " + user.getName() + " pkgid = " + pkgid);
                  }
               }
            
               if (doit) {
                  try {
                     ret.addElement(lookupPackage(pkgid, user));
                  } catch(DboxException dbeInner) {
                    // If we get an error, an ALLOWABLE error is that the
                    // package in question does not exist. Could have been
                    // in the process of being deleted 
                     if (dbeInner.getErrorCode() != 
                         DropboxGenerator.ERROR_PACKAGE_DOES_NOT_EXIST) {
                        throw dbeInner;
                     }
                  }
               }
            }
         }
		
         connection.commit();
                
      } catch(SQLException e) {
         log.error("DB2PM.packagesMatching: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2PackageManager: packagesMatching:>> SQL failure", 
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
      return ret;
   }
      
      
   public Vector getPackagesContainingFile(User user, DboxFileInfo info) 
      throws DboxException {
           
           
      Vector ret = new Vector();	
      PreparedStatement pstmt =null;
      ResultSet rs =null;		 
      
      StringBuffer sql = null;
      SharedConnection connection=null;
      try{
         connection=DbConnect.makeSharedConn();
         
         sql = new StringBuffer(
            "SELECT DISTINCT p.PKGID, PKGNAME, DESC, EXPIRATION, p.CREATED, ");
            
        // PKGCHANGE Used to have pkgsize and fileentry, now calculate it
        // 7/27/04 JMC -  now allow selection of trust
         if (getTrustSizes()) {
            sql.append(" PKGSIZE, FILEENTRY, ");
         } else {
            sql.append(" VALUE((SELECT SUM(fc.COMPONENTSIZE) from EDESIGN.FILECOMPONENT fc,EDESIGN.FILETOPKG fp WHERE fp.pkgid=p.pkgid AND fc.FILEID=fp.FILEID AND fc.DELETED is null AND fp.DELETED is null ");
            
           // ISOLATION - SUBSELECT
            sql.append(DB2PackageManager.getROURSubselect());
            sql.append("), 0),");
         
            sql.append(" VALUE((SELECT COUNT(DISTINCT fp.FILEID) from EDESIGN.FILETOPKG fp WHERE fp.pkgid=p.pkgid AND fp.DELETED is null ");
            
           // ISOLATION - SUBSELECT
            sql.append(DB2PackageManager.getROURSubselect());
            sql.append("), 0),");
         }
         
         sql.append(" PKGSTAT,OWNERID,COUNTRY,COMPANY,COMMITTED,FLAGS,POOLID ");
         sql.append("FROM EDESIGN.PACKAGE p, EDESIGN.FILETOPKG fp ");
         sql.append(" WHERE fp.FILEID=? AND fp.PKGID = p.PKGID AND fp.DELETED is null AND p.DELETED is null ");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString()); 
         
         pstmt.setLong(1, info.getFileId());
         
         rs=DbConnect.executeQuery(pstmt);	
               
        // FIXNESTED FIXNESTED FIXNESTED 
         while (rs.next()){					 
            try {
               if (user == null || canAccessPackage(user, rs.getLong(1), true)) {
                  DboxPackageInfo pinfo =new DB2DboxPackageInfo(this);
                  pinfo.setPackageId(rs.getLong(1));
                  pinfo.setPackageName(rs.getString(2));
                  pinfo.setPackageDescription(rs.getString(3));
                  pinfo.setPackageExpiration(rs.getLong(4));
                  pinfo.setPackageCreation(rs.getTimestamp(5).getTime());
                  pinfo.setPackageSize(rs.getLong(6));
                  pinfo.setPackageNumElements(rs.getInt(7)); 
                  pinfo.setPackageStatus(rs.getByte(8));
                  pinfo.setPackageOwner(rs.getString(9));
                  pinfo.setPackageCountry(rs.getString(10));
                  pinfo.setPackageCompany(rs.getString(11));
                  
                  java.sql.Timestamp tstamp = rs.getTimestamp(12);
                  if (tstamp != null) {
                     pinfo.setCommitTime(tstamp.getTime());
                  }
                  pinfo.setPackageFlags((byte)rs.getInt(13));
                  pinfo.setPackagePoolId(rs.getInt(14));
                  
                  ret.addElement(pinfo);
                  updatePackageStatus(pinfo, user);   
               }
            } catch(DboxException ee) {}
         }
         
         connection.commit();
         
      } catch(SQLException e) {
         log.error("DB2PM.getPackagesContainingFile: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2PackageManager: getPackagesContainingFile:>> SQL failure", 
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
      return ret;
   }
      
  // Called when a package is deleted or expired	 
  // Delete pkg from PACKAGE table, trigger delete from PKGACL table.
   public void cleanPackage(long packid) throws DboxException {
     // JMC 5/6/05 - now no archtive tables
      if (true || !usetriggers) {
         cleanPackageInt(packid, true);
      } /*else {
          cleanPackageIntOld(packid);
          }
        */
   }
      
  // Called when a package is deleted or expired	 
  // Delete pkg from PACKAGE table, and do all archiving ... NO TRIGGERS!! as 
  // DB2 stinks (or I stink at DB2).
   protected boolean cleanPackageInt(long packid, boolean except) throws DboxException {
		  
      PreparedStatement pstmt =null;
      ResultSet rs =null;		 
      
      SharedConnection connection=null;
      StringBuffer sql = null;		 
      
      int retry = 3;
      while(retry-- > 0) {
         try {
            connection=DbConnect.makeSharedConn();
            
           // Tables affected. Package table MUST go last, cause if there are problems
           //  its the pkgid in the package table that will kick the process off again
            String tables[]     = { "edesign.pkgacl", "edesign.filetopkg",
                                    "edesign.fileacl", "edesign.package"    };
            
           // Lock the package so we know we can mod safely
            if (!fileMgr.lockpackage(packid)) {
               if (except) {
                  throw new DboxException("cleanPackage: error:>> package " + 
                                          packid + " not found", 0); 
               } else {
                  return false;
               }
            }
            
           // Set deleted stamps
            for (int i=0; i < tables.length; i++) {
               sql = new StringBuffer("update ").append(tables[i]);
               sql.append(" set deleted = current timestamp where pkgid = ").append(packid);
               sql.append(" AND DELETED is null ");
               
               pstmt=connection.prepareStatement(sql.toString());
            
               if (DbConnect.executeUpdate(pstmt) < 1 && i == 3) {
               
                 // If this is the table update ... and it did NOT work
                 // commit anything we did get done, and throw exception if 
                 // we should ... return false otherwise 
                  connection.commit();
                  
                 // We should actually NEVER see this case since its locked!
                  if (except) {
                     throw new DboxException("cleanPackage: error:>> package " + 
                                             packid + " not found", 0); 
                  } else {
                     return false;
                  }
               }
               
               pstmt.close(); pstmt = null;
            }
            
            connection.commit();
            return true;
            
         } catch(SQLException e) {
            log.error("SQL except doing: " + 
                      (sql==null?"":sql.toString()));         
            DbConnect.destroyConnection(connection); connection=null; pstmt=null;
            DboxAlert.alert(e);  // Generic Alert
            
           // Loop around if this was a deadlock message and we have some 
           //  retries left
            int code     = e.getErrorCode();
            if ((code == -911 || code == -913) && retry > 0) {
               continue;
            }
            throw new 
               DboxException("DB2PackageManager: cleanPackage:>> SQL failure", 
                             DboxException.SQL_ERROR);
                             
         } finally {
            if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
            DbConnect.returnConnection(connection); connection = null;
         }
      }
      return false;
   }
      
   public PoolInfo getStoragePoolInstance(User user, 
                                          long poolid) throws DboxException {
   
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      
      SharedConnection connection=null;
      StringBuffer sql = null;
      boolean superuser = false;
      
      try {
			
         connection=DbConnect.makeSharedConn();
         
         sql = new StringBuffer("SELECT pp.POOLID, pp.GROUPNAME, pp.MAXDAYS, pp.DEFAULTDAYS, pp.DESCRIPTION ");
         
         if (getPrivilegeLevel(user.getName()) < PRIVILEGE_SUPER_USER) {
           // Only checking if != the default pool id
            if (poolid != 0) {
               sql.append(", USERID FROM EDESIGN.PERSISTENTPOOL pp JOIN EDESIGN.GROUPMEMBERS gm ON pp.groupname = gm.groupname AND gm.USERID = ? ");
            } else {
               sql.append("FROM EDESIGN.PERSISTENTPOOL pp ");
            }
         } else {
            sql.append("FROM EDESIGN.PERSISTENTPOOL pp ");
            superuser = true;
         }
         
         sql.append(" WHERE POOLID = ?");
         
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString ());
         int idx=1;
         if (!superuser && poolid != 0) {
            pstmt.setString(idx++,user.getName());
         }
         pstmt.setInt(idx++, (int)poolid);
         
         rs = DbConnect.executeQuery(pstmt);
         
         if (!rs.next()) {
            throw new DboxException("getStoragePoolInstance: error:>> poolid " + 
                                    poolid + " not accessible by " + user.getName() +
                                    " or does not exist",
                                    0);
         }
         
         PoolInfo ret = new PoolInfo();
         ret.setPoolId(rs.getInt(1));
         ret.setPoolName(rs.getString(2));
         ret.setPoolMaxDays(rs.getInt(3));
         ret.setPoolDefaultDays(rs.getInt(4));
         ret.setPoolDescription(rs.getString(5));
         
         connection.commit();
         
         return ret;
         
      } catch(SQLException e) {
         log.error("DB2PM.getStoragePoolInstance: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2PackageManager: getStoragePoolInstance:>> SQL failure", 
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
      
   }
         
   public Vector queryStoragePoolInformation(User user) throws DboxException {
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      
      SharedConnection connection=null;
      StringBuffer sql = null;
      boolean superuser = false;
      
      try {
			
         connection=DbConnect.makeSharedConn();
         
         sql = new StringBuffer("SELECT pp.POOLID, pp.GROUPNAME, pp.MAXDAYS, pp.DEFAULTDAYS, pp.DESCRIPTION FROM EDESIGN.PERSISTENTPOOL pp ");
         
         if (getPrivilegeLevel(user.getName()) < PRIVILEGE_SUPER_USER) {
            sql.append(" WHERE pp.POOLID = 0 OR pp.GROUPNAME in (SELECT gm.GROUPNAME FROM EDESIGN.GROUPMEMBERS gm WHERE USERID = ?) ORDER BY pp.POOLID ASC ");
         } else {
            superuser = true;
         }
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString ());
         if (!superuser) {
            pstmt.setString(1,user.getName());
         }
         
         rs = DbConnect.executeQuery(pstmt);
         
         Vector retv = new Vector();
         while (rs.next()) {
            PoolInfo ret = new PoolInfo();
            ret.setPoolId(rs.getInt(1));
            ret.setPoolName(rs.getString(2));
            ret.setPoolMaxDays(rs.getInt(3));
            ret.setPoolDefaultDays(rs.getInt(4));
            ret.setPoolDescription(rs.getString(5));
            retv.add(ret);
         }
         
         connection.commit();
         
         return retv;
         
      } catch(SQLException e) {
         log.error("DB2PM.getStoragePoolInformation: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2PackageManager: getStoragePoolInformation:>> SQL failure", 
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
      
   public void changePackageExpiration(User owner, long packid, long expire)
      throws DboxException {
               
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      
      SharedConnection connection=null;
      StringBuffer sql = null;
      boolean superuser = false;
      
      try {
			
         connection=DbConnect.makeSharedConn();
         
         fileMgr.lockpackage(packid);
         
         PackageInfo pinfo = lookupPackage(packid);
         
         long curtime = System.currentTimeMillis();
         if (expire == 0) {
            expire = curtime + getDefaultExpireMillis(owner, pinfo.getPackagePoolId());
         } else if (expire < 50000) {
            expire = curtime + (expire * 24 * 60 * 60 * 1000);
         }
         
         assertExpireTime("changePackageExpiration:", expire, owner, 
                          pinfo.getPackagePoolId());
         
         sql = new StringBuffer("UPDATE EDESIGN.PACKAGE SET EXPIRATION=? WHERE PKGID=? AND DELETED is null ");
         
         if (getPrivilegeLevel(owner.getName()) < PRIVILEGE_SUPER_USER) {
            sql.append(" AND OWNERID=? ");
         } else {
            superuser = true;
         }
         
         pstmt=connection.prepareStatement(sql.toString ());
         pstmt.setLong(1,expire);
         pstmt.setLong(2,packid);	
         
         if (!superuser) {
            pstmt.setString(3,owner.getName());
         }
         
         if (DbConnect.executeUpdate(pstmt) != 1) {
            throw new DboxException("changePackageExpiration: error:>> package " + 
                                    packid + " not owned by " + owner.getName() +
                                    " or does not exist",
                                    0);
         }
         
         connection.commit();
         
      } catch(SQLException e) {
         log.error("DB2PM.changePackageExpiration: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2PackageManager: changePackageExpiration:>> SQL failure", 
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
      
   public void setPackageDescription(User owner, long packid, String desc)
      throws DboxException {
      
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      
      SharedConnection connection=null;
      StringBuffer sql = null;
      boolean superuser = false;
      
      try{
			
         connection=DbConnect.makeSharedConn();
         
         fileMgr.lockpackage(packid);
         
         sql = new StringBuffer("UPDATE EDESIGN.PACKAGE SET desc=? WHERE PKGID=? AND DELETED is null ");
         
         if (getPrivilegeLevel(owner.getName()) < PRIVILEGE_SUPER_USER) {
            sql.append(" AND OWNERID=? ");
         } else {
            superuser = true;
         }
         
         pstmt=connection.prepareStatement(sql.toString ());
         pstmt.setString(1,desc);
         pstmt.setLong  (2,packid);	
         
         if (!superuser) {
            pstmt.setString(3,owner.getName());
         }
         
         if (DbConnect.executeUpdate(pstmt) != 1) {
            throw new DboxException("setPackageDescription: error:>> package " + 
                                    packid + " not owned by " + owner.getName() +
                                    " or does not exist",
                                    0);
         }
         
         connection.commit();
         
      } catch(SQLException e) {
         log.error("DB2PM.changePackageExpiration: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2PackageManager: changePackageExpiration:>> SQL failure", 
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
      
   public DboxPackageInfo createPackage(User owner, String packname,
                                        String desc, long poolid, long expires, 
                                        Vector acls) throws DboxException {
                                        
      String err = "";
      
      if (packname.length()           > 1024) {
         err += " : Package name too long (> 1024) - " + packname;
      }
      if (owner.getName().length()    > 128) {
         err += " : Userid too long (> 128) - " + owner.getName();
      }
      if (owner.getCompany().length() > 128) {
         err += " : Company name too long (> 128) - " + owner.getCompany();
      }
      if (owner.getCountry().length() > 64)  {
         err += " : Country too long (> 64) - " + owner.getCountry();
      }
      
      
      if (packname.indexOf("/") >= 0 || packname.indexOf("\\") >= 0) {
         err += " : Package name CANNOT contain separator chars [" +
                packname + "]";
      }
      
      if (acls != null) {
         Enumeration tenum = acls.elements();
         while(tenum.hasMoreElements()) {
            AclInfo ainfo = (AclInfo)tenum.nextElement();
            
            String aclname = ainfo.getAclName();
            if (aclname.length() > 128) {
               err += " : AclName too long (> 128) - " + aclname;
            }
            
            byte acltype = ainfo.getAclStatus();
            if (acltype == DropboxGenerator.STATUS_PROJECT) {
               Vector v = owner.getProjects();
               if (!owner.getDoProjectSend() || v == null || 
                   !v.contains(aclname)) {
                  err += " : addPackageAcl:error: " +
                     "Can't add project acl " + 
                     aclname + " ... you don't have that project!";
               }
            } else if (acltype == DropboxGenerator.STATUS_GROUP) {
               Hashtable h = getMatchingGroups(owner, aclname, false, false,
                                               false, false, true, false,
                                               false, false, false);
               if (h.size() == 0) {
                  err += " : addPackageAcl: error: " +
                     "Specified Group does not exist or not visibile: " + 
                     aclname;
               }
            } else {
               if (ainfo.getAclName().equals("*")) {
                  if (getPrivilegeLevel(owner.getName()) < 
                      PRIVILEGE_CAN_USE_WILD) {
                      
                     err += " : AclName of '*' (wildcard) not allowed at your privilege level"; 
                  }
               }
            }
            if (ainfo.getAclCompany().length() > 128) {
               err += ": AclCompany too long (> 128) - " + 
                  ainfo.getAclCompany();
               
            }
         }
      }
                                        
      if (err.length() > 0) {
         throw new DboxException("createPackage:>>" + err, 0);
      }
                                        
      long pkgid=-1;
      PreparedStatement pstmt=null;
      ResultSet rs=null;		 
      DboxPackageInfo info = new DB2DboxPackageInfo(this);
      if (packname.length() == 0) { 
         throw new DboxException("Null package name not valid", 0);
      }		 
      
      long curtime = System.currentTimeMillis();
      if (expires == 0)         expires = curtime + getDefaultExpireMillis(owner, poolid);
      else if (expires < 50000) expires = curtime + (expires * 24 * 60 * 60 * 1000);
      
      assertExpireTime("createPackage:", expires, owner, poolid);
      
      SharedConnection connection=null;
      StringBuffer sql = null;
      try{
			
         connection=DbConnect.makeSharedConn();
         
        // JMC 3/16/04 - Validate no other uncommitted packages w/ this name
        //               owned by this guy
         sql = new StringBuffer("select pkgid from edesign.package where ");
         sql.append("PKGNAME=? and pkgstat != ? and OWNERID = ? AND DELETED is null ");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         
         int i=1;
         pstmt.setString(i++, packname);
         pstmt.setByte  (i++, DropboxGenerator.STATUS_COMPLETE);
         pstmt.setString(i++, owner.getName());
         
         rs=DbConnect.executeQuery(pstmt);
         
         if (rs.next()) {
            throw new 
               DboxException("createPackage:>> uncommitted package with same name exists: " + 
                             packname, 0);
         }
         
         rs = null;
         pstmt.close(); pstmt = null;
         sql = new StringBuffer("values nextval for edesign.dropboxseq");
         pstmt=connection.prepareStatement(sql.toString());
		 
         rs=DbConnect.executeQuery(pstmt);
         
         if (rs.next()) {
            pkgid = rs.getLong(1);
         } else {
            throw new 
              DboxException("createPackage:>> Error getting next seq for pkgid", 
                            0);
         }
                        
         pstmt.close(); pstmt = null;
         
        // Setup flags value
         Hashtable options = getUserOptions(owner, null);
         
         int flags = 0;
         
         String rrd = (String)
            options.get(DropboxGenerator.ReturnReceiptDefault);
         String snd = (String)
            options.get(DropboxGenerator.SendNotificationDefault);
            
         if (rrd != null && rrd.equalsIgnoreCase("true")) {
            flags |= PackageInfo.RETURNRECEIPT;
         }
         if (snd.equalsIgnoreCase("true")) {
            flags |= PackageInfo.SENDNOTIFY;
         }
         
        // If dept or div is different than null, then we have some valid data.
        //  set it into the package.
         boolean dodeptdiv = owner.getIBMDept() != null;
            
         sql = new StringBuffer("INSERT INTO EDESIGN.PACKAGE (PKGID,PKGNAME,");
         sql.append("DESC,EXPIRATION,PKGSIZE,FILEENTRY,PKGSTAT,FLAGS,POOLID,");
         sql.append("OWNERID,COUNTRY,COMPANY");
         if (dodeptdiv) {
            sql.append(",DEPT,DIVISION");
         }
         sql.append(") VALUES (?,?,?,?,?,?,?,?,?,?,?,?");
         if (dodeptdiv) {
            sql.append(",?,?");
         }
            
         sql.append(")");
         pstmt=connection.prepareStatement(sql.toString());
         
         i=1;
         pstmt.setLong(i++,pkgid);
         pstmt.setString(i++,packname);
         pstmt.setString(i++,desc);
         pstmt.setLong (i++,expires);
         pstmt.setLong(i++, 0);
         pstmt.setInt(i++,0);
         pstmt.setByte(i++,DropboxGenerator.STATUS_NONE);
         pstmt.setInt(i++,flags);
         pstmt.setInt(i++,(int)poolid);
         pstmt.setString(i++,owner.getName());
         pstmt.setString(i++,owner.getCountry());
         pstmt.setString(i++,owner.getCompany());
         if (dodeptdiv) {
            pstmt.setString(i++, owner.getIBMDept());
            pstmt.setString(i++, owner.getIBMDiv());
         }
            
         if (DbConnect.executeUpdate(pstmt) != 1) {
            throw new 
               DboxException("PackageManager:>> Error creating package in DB", 
                             0);
         }
			
         info.setPackageId(pkgid);
         info.setPackageName(packname);	
         info.setPackageDescription(desc);
         info.setPackageExpiration(expires);
         info.setPackageCreation(System.currentTimeMillis());
         info.setPackageOwner(owner.getName());
         info.setPackageStatus(DropboxGenerator.STATUS_NONE);
         info.setPackageCountry(owner.getCountry());
         info.setPackageCompany(owner.getCompany());
         info.setPackageFlags((byte)flags);
         info.setPackagePoolId(poolid);
         
         connection.commit();
         
      } catch (SQLException e){
         log.error("DB2PM.createPackage: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2PackageManager: createPackage:>> SQL failure", 
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
		
      Enumeration enum = acls.elements();
      while(enum.hasMoreElements()) {
         AclInfo aclinfo = (AclInfo)enum.nextElement();
         String aclname = aclinfo.getAclName();
                  
         if (aclinfo.getAclStatus() == DropboxGenerator.STATUS_PROJECT) {
            info.addProjectAcl(aclname);
         } else if (aclinfo.getAclStatus() == DropboxGenerator.STATUS_GROUP) {
            info.addGroupAcl(aclname);
         } else {
            info.addUserAcl(aclname);
         }
      }
		 
      return info;
   }
      
	  
   public void deletePackage(User owner, long id) throws DboxException {
		  
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      
      SharedConnection connection=null;
      StringBuffer sql = null;
      try {
			
         connection=DbConnect.makeSharedConn();
         
         fileMgr.lockpackage(id);         
         
         sql = new StringBuffer ("SELECT OWNERID FROM EDESIGN.PACKAGE p WHERE p.PKGID=? AND p.DELETED is null ");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1,id);	
         
         rs=DbConnect.executeQuery(pstmt);		 
		 
        // FIXNESTED FIXNESTED FIXNESTED 
         while (rs.next()){			 
            String ownerid=rs.getString(1);			 
            if (owner==null ||
                (!ownerid.equals(owner.getName()) && 
                 getPrivilegeLevel(owner.getName()) < PRIVILEGE_SUPER_USER)) {
               throw new DboxException("Delete: error:>> package " + id + 
                                       " not owned by " + owner.getName(), 0); 
            }
         }
         cleanPackage(id);
         
         connection.commit();
         
      } catch(SQLException e) {
         log.error("DB2PM.deletePackage: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2PackageManager: deletePackage:>> SQL failure", 
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
      
   public void commitPackage(User owner, long id) throws DboxException {
      String ownerid;		 
      byte pkgstatus=-1;
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      
      SharedConnection connection=null;
      StringBuffer sql = null;
      try{
			
         connection=DbConnect.makeSharedConn();
         
         fileMgr.lockpackage(id);
                        
         sql = new StringBuffer("SELECT OWNERID, PKGSTAT FROM EDESIGN.PACKAGE p WHERE p.PKGID=? AND p.DELETED is null ");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString ());
         pstmt.setLong(1,id);
         
         rs=DbConnect.executeQuery(pstmt);		 
         
        // FIXNESTED FIXNESTED FIXNESTED 
         while (rs.next()){			 
            ownerid=rs.getString(1);
            pkgstatus=rs.getByte(2);			 
            if (owner==null ||
                (!ownerid.equals(owner.getName()) && 
                 getPrivilegeLevel(owner.getName()) < PRIVILEGE_SUPER_USER)) {
                 
               throw new DboxException("Commit: error:>> package " + id + 
                                       " not owned by " + owner.getName(), 0); 
            }
         }
		
         if (pkgstatus != DropboxGenerator.STATUS_COMPLETE) {
				
            sql = new StringBuffer("SELECT FILESTAT FROM EDESIGN.FILE f, EDESIGN.FILETOPKG fp ");
            sql.append("WHERE fp.PKGID=? AND fp.FILEID=f.FILEID");
            sql.append(" AND fp.DELETED is null AND f.DELETED is null ");
            
           // ISOLATION
            sql.append(DB2PackageManager.getROUR());
            
            try{ pstmt.close(); } catch (SQLException e) {} pstmt=null;
            
            pstmt=connection.prepareStatement(sql.toString());
            pstmt.setLong(1,id);
			
            rs=DbConnect.executeQuery(pstmt);
            while (rs.next()){			 
               if (rs.getByte(1) !=DropboxGenerator.STATUS_COMPLETE)
                  throw new DboxException("Commit: error:>> package " + id + 
                                          " has at least 1 incomplete file",0);
                  
            }
            sql = new StringBuffer("UPDATE EDESIGN.PACKAGE p SET p.PKGSTAT=?,COMMITTED=CURRENT TIMESTAMP WHERE p.PKGID=? AND p.DELETED is null ");
            
            try{ pstmt.close(); } catch (SQLException e) {} pstmt=null;
            pstmt=connection.prepareStatement(sql.toString ());
            pstmt.setByte(1,DropboxGenerator.STATUS_COMPLETE);
            pstmt.setLong(2,id);	
               
            if (DbConnect.executeUpdate(pstmt) != 1) {
               throw new 
                  DboxException("PackageManager:>> DB Error commiting package", 
                                0);
            }
            
           // Just to be sure
            recalculatePackageSize(id);
            
         } else 
            throw new DboxException("Commit: error:>> package " + id + 
                                    " already complete", 0); 
        
         connection.commit();
        
      } catch(SQLException e) {
         log.error("DB2PM.commitPackage: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2PackageManager: commitPackage:>> SQL failure", 
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
   
   public void recalculatePackageSize(long packageid) 
      throws DboxException {
      
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      StringBuffer sql = null;
      
      try {
         connection = DbConnect.makeSharedConn();
         
        // Lock the package
         fileMgr.lockpackage(packageid);
         
        // Calculate the package size from the filesizes. We used to use 
        // TRIGGERS to do this automatically ... had deadlock issues. So,
        // when the filesize is changed, this method should be called on the 
        // package. 
         sql = new StringBuffer("SELECT VALUE(SUM(f.filesize), 0), VALUE(COUNT(distinct f.fileid), 0) from EDESIGN.FILETOPKG fp, EDESIGN.PACKAGE p, EDESIGN.FILE f where  fp.PKGID=? AND p.PKGID = fp.PKGID AND f.FILEID = fp.FILEID AND p.DELETED is null AND fp.DELETED is null AND f.DELETED is null GROUP BY p.pkgid ");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1, packageid);
         
        // Should always get one next
         rs = DbConnect.executeQuery(pstmt);
         
         long pkgsize  = 0;
         int  numfiles = 0;
         
        // If we don't have any rows, then no files in package
         if (rs.next()) {
            pkgsize  = rs.getLong(1);
            numfiles = rs.getInt(2);
         }
            
         log.debug("recalc package: sz = " + pkgsize + " numfile = " + numfiles);
            
         pstmt.close();
         sql = new StringBuffer("UPDATE EDESIGN.PACKAGE SET PKGSIZE=?,FILEENTRY=? WHERE PKGID=? AND DELETED is null");
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1, pkgsize);
         pstmt.setInt (2, numfiles);
         pstmt.setLong(3, packageid);
            
         if (DbConnect.executeUpdate(pstmt) != 1) {
            DboxAlert.alert(DboxAlert.SEV3, 
                            "executeUpdate != " + 1, 
                            0, 
                            "update package stats for pkg : " + 
                            packageid + " failed");
         }
         
         connection.commit();
         
      } catch (SQLException e) {
         log.error("DB2PackMgr.recalcPkgSize: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null;
         pstmt=null;
         
         DboxAlert.alert(e);  // Generic Alert
         
         throw new 
            DboxException("DB2PackageMgr:>> sql failure recalculating packsz",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      } 
   }
   
      
   public void addItemToPackage(User owner, long packid, long itemid) 
      throws DboxException {
         
      String ownerid=null;		 
      byte pkgstatus=-1;
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      
      SharedConnection connection=null;
      StringBuffer sql = null;
      try {
         DboxPackageInfo info = lookupPackage(packid);
         connection=DbConnect.makeSharedConn();
         
         fileMgr.lockpackage(packid);
         
         sql = new StringBuffer("SELECT OWNERID, PKGSTAT FROM EDESIGN.PACKAGE p ");
         sql.append("WHERE p.PKGID=? AND p.DELETED is null ");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString ());
         pstmt.setLong(1,packid);		
		 
         rs=DbConnect.executeQuery(pstmt);		 
		
         if (rs.next()){			 
            ownerid=rs.getString(1);
            pkgstatus=rs.getByte(2);	
			
         }
		 
        // Consider letting super user do this ... 
        //   I don't see the need at this point
         if (owner==null || 
             (!ownerid.equals(owner.getName()) && 
              getPrivilegeLevel(owner.getName()) < PRIVILEGE_SUPER_USER)) { 
            
            throw new DboxException("addItemToPackage: error:>> " +
                                    "package " + packid + 
                                    " not owned by " + owner.getName(),0); 
         }
         
         if (pkgstatus == DropboxGenerator.STATUS_COMPLETE) 
            throw new DboxException("addItemToPackage: error:>> package " 
                                    + packid + " already complete", 0);
         
         if (!canAccessFile(owner, itemid, true))  
            throw new DboxException("addItemToPackage: error:>> file " 
                                    + itemid + " not accessible", 0);
         
         DboxFileInfo finfo = lookupFile(itemid);
         
         if (finfo.getFileStatus() != DropboxGenerator.STATUS_COMPLETE) {
            throw new DboxException("addItemToPackage:>> attempt to add file [" 
                                    + finfo.getFileName() + "] or fid=" + 
                                    itemid + " which is not complete", 0);
         } 
         
        // Throw an exception if adding a fileref from another storage pool
        // TODOTODOTODO ... copy data to package storage pool
         if (finfo.getPoolId() != info.getPackagePoolId() &&
            info.getPackagePoolId() != DropboxGenerator.PUBLIC_POOL_ID) {
            throw new 
               DboxException("addItemToPackage:>> Can't reference files from differing storage pool: [" + 
                             finfo.getFileName() + 
                             "] for package " + info.getPackageName(), 0);
         }	
         
         Vector packs = getPackagesContainingFile(owner, finfo);
         if (packs != null) {
            Iterator it = packs.iterator();
            while(it.hasNext()) {
               PackageInfo pinfo = (PackageInfo)it.next();
               if (pinfo.getPackageItar() ^ info.getPackageItar()) {
                  throw new 
                     DboxException("addItemToPackage:>> Can't reference a file between packages with differing ITAR settings [" + 
                                   finfo.getFileName() + 
                                   "] for package " + info.getPackageName(), 0);
               }
            }
         }
         
         
         sql = new StringBuffer ("SELECT COUNT(FILENAME) FROM EDESIGN.FILETOPKG fp, EDESIGN.FILE f ");
         sql.append("WHERE fp.FILEID=f.FILEID AND fp.PKGID=? AND f.FILENAME=? ");
         sql.append(" AND f.DELETED is null and fp.DELETED is null ");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         try{ pstmt.close(); } catch (SQLException e) {} pstmt=null;
         pstmt=connection.prepareStatement(sql.toString ());
         pstmt.setLong(1,packid);
         pstmt.setString(2, finfo.getFileName());
         
         rs=DbConnect.executeQuery(pstmt);
         
         if (rs.next() && rs.getInt(1) != 0) {
            throw new DboxException("addItemToPackage:>> file w/same name [" 
                                    + finfo.getFileName() + 
                                    "] already in package " + packid, 0);
         }	
         
         
        // when create file, there is no pkgid given, here is the place to 
        //  update File table with PKGID.		
         
         info.addFile(finfo);
         
         connection.commit();
         
      } catch (SQLException e) {
         log.error("DB2PM.addItemToPackage: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2PackageManager: addItemToPackage:>> SQL failure", 
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
      
   public void removeItemFromPackage(User owner, long packid, long itemid) 
      throws DboxException {
      
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      StringBuffer sql =null;
      try{
         connection=DbConnect.makeSharedConn();
         
         fileMgr.lockpackage(packid);
         
         DboxPackageInfo info = lookupPackage(packid);
         
         sql = new StringBuffer("SELECT OWNERID, PKGSTAT FROM EDESIGN.PACKAGE p WHERE p.PKGID=? AND p.DELETED is null ");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString ());
		 
         pstmt.setLong(1,packid);	
         
         rs=DbConnect.executeQuery(pstmt);		 
		 
         if (rs.next()){
            String lo = rs.getString(1);
            byte lstatus = rs.getByte(2);
            if (owner == null || 
                (!lo.equals(owner.getName()) && 
                 getPrivilegeLevel(owner.getName()) < PRIVILEGE_SUPER_USER)) { 
               throw new DboxException("removeItemFromPackage: error:>> " +
                                       "package " + packid + 
                                       " not owned by " + owner.getName(),0); 
            }
            if (lstatus == DropboxGenerator.STATUS_COMPLETE) {
               throw new DboxException("removeItemFromPackage: error:>> package "
                                       + packid + " already complete", 0);
            }
         }		
       
         info.removeFile(itemid);
			
         connection.commit();
                        
      }
      catch (SQLException e) {
         log.error("DB2PM.removeItemFromPackage: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2PackageManager: removeItemFromPackage:>> SQL failure", 
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
   
  // this is horrible !!! 
   public void updatePackageStatus(DboxPackageInfo pinfo, User user) 
      throws DboxException {
      
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      SharedConnection connection=null;
      StringBuffer sql = null;
	  
      int complete = 0;
      int incomplete = 0;
      int none = 0;
	  
	  
      try{
		 
         connection=DbConnect.makeSharedConn();
         
         byte status = pinfo.getPackageStatus ();
         if (status!=DropboxGenerator.STATUS_COMPLETE) { // && 
        // HACK ... if its SFTP or DropboxFTP, we don't update the status. 
        //   !skipPartialCheck){
            
            sql = new StringBuffer("SELECT ");
            
            sql.append(" sum(case when f.FILESTAT = ");
            sql.append(DropboxGenerator.STATUS_COMPLETE);
            sql.append(" then 1 else 0 END) as complete,  ");
            
            sql.append(" sum(case when f.FILESTAT = ");
            sql.append(DropboxGenerator.STATUS_INCOMPLETE);
            sql.append(" then 1 else 0 END) as incomplete,  ");
            
            sql.append(" sum(case when f.FILESTAT = ");
            sql.append(DropboxGenerator.STATUS_NONE);
            sql.append(" then 1 else 0 END) as none  ");
                        
            sql.append(" FROM EDESIGN.FILE f, EDESIGN.FILETOPKG fp WHERE ");
            sql.append(" fp.PKGID=? AND fp.FILEID=f.FILEID AND fp.DELETED is null AND f.DELETED is null ");
            
           // ISOLATION
            sql.append(DB2PackageManager.getROUR());
            
            pstmt=connection.prepareStatement(sql.toString ());
            pstmt.setLong(1,pinfo.getPackageId());
            
            rs=DbConnect.executeQuery(pstmt);		 
            
            if (rs.next()){
               complete=rs.getInt(1);
               incomplete=rs.getInt(2);
               none=rs.getInt(3);
            }
            
            pstmt.close();
            pstmt = null;
            
            if (log.isDebugEnabled()) {
               log.debug("incomplete,none,complete = " + incomplete + ", " + 
                         none + ", " + complete);
            }
            
            if (incomplete != 0) {
               pinfo.setPackageStatus(DropboxGenerator.STATUS_FAIL);
            } else if (none != 0 || complete != 0) {
               pinfo.setPackageStatus(DropboxGenerator.STATUS_PARTIAL);
            } else {
               pinfo.setPackageStatus(DropboxGenerator.STATUS_NONE);
            }
         }
		 
         if (user != null) {
            
            sql = new StringBuffer("SELECT ");
            sql.append(" sum(case when facl1.FILEID = 0 then 1 else 0 END), ");
            sql.append(" sum(case when facl1.FILEID != 0 AND ");
            sql.append(" facl1.DLOADSTAT = 10 then 1 else 0 END) ");
            sql.append(" FROM EDESIGN.FILEACL facl1 WHERE facl1.PKGID=? AND");
            sql.append(" facl1.USERID=? AND facl1.DELETED is null ");
            
           // ISOLATION
            sql.append(DB2PackageManager.getROUR());
            
            pstmt=connection.prepareStatement(sql.toString());
            pstmt.setLong(1,pinfo.getPackageId());
            pstmt.setString(2,user.getName());
            
            rs=DbConnect.executeQuery(pstmt);		 
            
            if (rs.next()){
               int num=rs.getInt(1);
               pinfo.setPackageMarked(num != 0);
               
               num=rs.getInt(2);
               pinfo.setPackageCompleted(num == pinfo.getPackageNumElements());
            }
         }
         		 
         connection.commit();
                         
      } catch (SQLException e) {
         log.error("DB2PM.updatePackageStatus: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2PackageManager: updatePackageStatus:>> SQL failure", 
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
   
   public int getPrivilegeLevel(String user) throws DboxException {
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;	
      StringBuffer sql = null;
      int ret = 0;
      try{	
         connection=DbConnect.makeSharedConn();
         sql = new StringBuffer ("SELECT LEVEL FROM EDESIGN.PRIVILEGES ");
         sql.append("WHERE USERID=?");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
                   
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setString(1, user);
         
         rs=DbConnect.executeQuery(pstmt);
				
         if (rs.next()) {
            ret = rs.getInt(1);
         }          
			
         connection.commit();
                        
      } catch (SQLException e) {
         log.error("DB2PM.getPrivilegeLevel: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2PackageManager: getPrivilegeLevel:>> SQL failure", 
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
      return ret;
   }
   
  // Applies Dropbox policies ... subclass may do more
   public boolean allowsPackageReceipt(String fromUser, String fromCompany,
                                       String toUser,   String toCompany) 
      throws DboxException {
      
      boolean ret = false;
      if (policy_supportLookup) {
         SharedConnection connection=null;
         PreparedStatement pstmt=null;
         ResultSet rs=null;	
         StringBuffer sql = null;
         try{	
            connection=DbConnect.makeSharedConn();
            sql = new StringBuffer ("SELECT count(*) FROM EDESIGN.ALLOWSEND ");
            sql.append("WHERE ");
            sql.append("((FROMTYPE='U' AND FROMNAME in (?, '*')  OR ");
            sql.append(" (FROMTYPE='C' AND FROMNAME in (?, '*'))) AND ");
            sql.append("((TOTYPE  ='U' AND TONAME in (?, '*'))    OR ");
            sql.append(" (TOTYPE  ='C' AND TONAME in (?, '*'))))");
            
           // ISOLATION
            sql.append(DB2PackageManager.getROUR());
            
            pstmt=connection.prepareStatement(sql.toString());
            pstmt.setString(1, fromUser);
            pstmt.setString(2, fromCompany);
            pstmt.setString(3, toUser);
            pstmt.setString(4, toCompany);
            
            rs=DbConnect.executeQuery(pstmt);
            
            if (rs.next()) {
               ret = (rs.getInt(1) > 0);
            }                        
            
            connection.commit();
            
         } catch (SQLException e) {
            log.error("DB2PM.allowPackageReceipt: SQL except doing: " + 
                      (sql==null?"":sql.toString()));         
            DbConnect.destroyConnection(connection); connection=null;
            pstmt=null;
            DboxAlert.alert(e);  // Generic Alert
            throw new 
               DboxException("DB2PackageManager: allowPackageReceipt:>> SQL failure", 
                             DboxException.SQL_ERROR);
         } finally {
            if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
            DbConnect.returnConnection(connection);
         }
      }
      
      if (!ret) ret = super.allowsPackageReceipt(fromUser, fromCompany,
                                                 toUser,   toCompany);
                                                 
      return ret;
   }
   
   public GroupInfo getGroup(String group,
                             boolean returnMembers, 
                             boolean returnAccess) throws DboxException {
      return Groups.getGroup(group, returnMembers, returnAccess);
   }

   public GroupInfo getGroupWithAccess(User user, 
                                       String group,
                                       boolean returnMembers,
                                       boolean returnAccess)
      throws DboxException {
      
      return Groups.getGroupWithAccess(user, group, returnMembers, returnAccess);
   }
   
   public Hashtable getMatchingGroups(User user,
                                      String  group,
                                      boolean regexSearch,
                                      boolean owner,
                                      boolean modify,
                                      boolean member,
                                      boolean visible,
                                      boolean listable,
                                      
                                      boolean returnGI,
                                      boolean returnMembers,
                                      boolean returnAccess)
      throws DboxException {

      return Groups.getMatchingGroups(user, group, 
                                      regexSearch, owner, modify, member,
                                      visible, listable, returnGI,
                                      returnMembers, returnAccess);
   }
   
   public void createGroup(User owner, String group) throws DboxException {
      Groups.createGroup(owner, group);
   }
   
   public void deleteGroup(User user, String group) throws DboxException {
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;	
      StringBuffer sql = null;
      try{	
         connection=DbConnect.makeSharedConn();
         sql = new StringBuffer ("SELECT POOLID FROM EDESIGN.PERSISTENTPOOL WHERE GROUPNAME = ?");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setString(1, group);
         
         rs=DbConnect.executeQuery(pstmt);
         
         if (rs.next()) {
            throw new DboxException("Persistant Pool group cannot be deleted");
         }                        
         
         Groups.deleteGroup(user, group);
         
         connection.commit();
         
      } catch (SQLException e) {
         log.error("DB2PM.deleteGroup: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null;
         pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2PackageManager: deleteGroup:>> SQL failure", 
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
   

   public void modifyGroupAttributes(User owner, String group,
                                     byte visibility, byte listability)
      throws DboxException {      
      
      Groups.modifyGroupAttributes(owner, group, visibility, listability);
   }
   public void addGroupAcl(User owner, 
                           String group,
                           String name,
                           boolean memberOrAccess)
      throws DboxException {

      Groups.addGroupAcl(owner, group, name, memberOrAccess);
   }
   public void removeGroupAcl(User owner, 
                              String group,
                              String name,
                              boolean memberOrAccess)
      throws DboxException {

      Groups.removeGroupAcl(owner, group, name, memberOrAccess);
   }
   
   public Hashtable getValidOptionNames(String username) {
      Hashtable h = new Hashtable();
      h.put(DropboxGenerator.NagNotification,             "true");
      h.put(DropboxGenerator.NewPackageEmailNotification, "true");
      h.put(DropboxGenerator.FilterComplete,              "false");
      h.put(DropboxGenerator.FilterMarked,                "false");
      h.put(DropboxGenerator.ReturnReceiptDefault,        "false");
      h.put(DropboxGenerator.SendNotificationDefault,     "true");
      h.put(DropboxGenerator.ShowHidden,                  "false");
      h.put(DropboxGenerator.ItarCertified,               "false");
      h.put(DropboxGenerator.ItarSessionCertified,        "false");
      return h;
   }
      
   public void setUserOptions(User user,
                              Hashtable h) throws DboxException {
   
   
      String username = user.getName();
      assertUserOptionNames(username, h);
   
      if (h != null && h.size() > 0) {
         SharedConnection connection=null;
         PreparedStatement pstmt=null;
         StringBuffer sql =null;
         try {
         
            connection=DbConnect.makeSharedConn();
      
            Enumeration enum = h.keys();
            while(enum.hasMoreElements()) {
               String k = (String)enum.nextElement();
               String v = (String)h.get(k);
               if (k.equals(DropboxGenerator.NewPackageEmailNotification) ||
                   k.equals(DropboxGenerator.NagNotification)             ||
                   k.equals(DropboxGenerator.FilterComplete)              ||
                   k.equals(DropboxGenerator.FilterMarked)                ||
                   k.equals(DropboxGenerator.ShowHidden)                  ||
                   k.equals(DropboxGenerator.ItarCertified)               ||
                   k.equals(DropboxGenerator.ItarSessionCertified)        ||
                   k.equals(DropboxGenerator.ReturnReceiptDefault)        ||
                   k.equals(DropboxGenerator.SendNotificationDefault)) {
                  
                  if (!v.equalsIgnoreCase("true") &&
                      !v.equalsIgnoreCase("false")) {
                     throw new DboxException("Value for option " + k + 
                                             " should be true or false: " +
                                             v, 0);
                  }
                  
                  v = v.toLowerCase();
                  
                 // ItarCertified is read only
                  if (k.equals(DropboxGenerator.ItarCertified)) {
                     throw new DboxException("Option is readonly: " + k, 0);
                  }
                  
                 // add as flag in dboxsess
                 // showHidden is 'special' as it is per session
                  if (k.equals(DropboxGenerator.ShowHidden)) {
                     setSessionFlag(user, User.SessionFlag_ShowHidden, 
                                    v.equalsIgnoreCase("true"));
                     continue;
                  }
                  
                 // add as flag in dboxsess
                 // showHidden is 'special' as it is per session
                  if (k.equals(DropboxGenerator.ItarSessionCertified)) {
                     if (v.equalsIgnoreCase("true") && 
                         !user.isUserItarCertified()) {
                        throw new DboxException("Session ITAR Certification denied, User not ITAR certified");
                     }
                     
                     setSessionFlag(user, 
                                    User.SessionFlag_ItarSessionCertified, 
                                    v.equalsIgnoreCase("true"));
                     continue;
                  }
                  
               } else {
                  throw new DboxException("setUserOptions:>> " + k +
                                          " is an invalid option", 0);
               }
               
               sql = new StringBuffer("DELETE FROM EDESIGN.USEROPTIONS where userid=? AND optname=?");
            
              // Delete the old options
               pstmt=connection.prepareStatement(sql.toString ());
               pstmt.setString(1, username);
               pstmt.setString(2, k);
               
               DbConnect.executeUpdate(pstmt);
               
               pstmt.close();
               
               sql = new StringBuffer("INSERT INTO EDESIGN.USEROPTIONS ");
               sql.append("(userid,optname,optval) VALUES(?,?,?)");
               
               pstmt=connection.prepareStatement(sql.toString ());
               pstmt.setString(1, username);
               pstmt.setString(2, k);
               pstmt.setString(3, v);
               
               if (DbConnect.executeUpdate(pstmt) != 1) {
                  throw new DboxException(
                     "DB2PackMgr: setUserOption: Error setting option after delete!: " + k + "=" + v, 0);
               }
            }
            
            connection.commit();
            
         } catch (SQLException e) {
            log.error("DB2PM.setUserOptions: SQL except doing: " + 
                      (sql==null?"sqlisnull":sql.toString()));         
            DbConnect.destroyConnection(connection); connection=null; pstmt=null;
            DboxAlert.alert(e);  // Generic Alert
            throw new 
               DboxException("DB2PackageMgr: setUserOptions:>> SQL failure", 
                             DboxException.SQL_ERROR);
         } finally {
            if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
            DbConnect.returnConnection(connection);
         } 
      }
   }
   
   public Hashtable getUserOptions(User user, 
                                   Vector v) throws DboxException {
   
      String username = user.getName();
   
      Hashtable defaultHash = getValidOptionNames(username);
      Enumeration enum = null;
      if (v == null || v.size() == 0) {
         enum = defaultHash.keys();
      } else {
         enum = v.elements();
      }
      
      Hashtable ret = new Hashtable();
      
      if (enum.hasMoreElements()) {
         SharedConnection connection=null;
         PreparedStatement pstmt=null;
         StringBuffer sql =null;
         ResultSet rs=null;	
         try {
         
            connection=DbConnect.makeSharedConn();
      
            sql = new StringBuffer("SELECT optname,optval from EDESIGN.USEROPTIONS WHERE userid=? AND optname in (");
            
            int cnt = 0;
            while(enum.hasMoreElements()) {
               String k = (String)enum.nextElement();
               
               String val = (String)defaultHash.get(k);
               
               if (val == null) {
                  throw new DboxException("getUserOptions:>> " + k +
                                          " is an invalid option", 0);
               }
               
              // fill in return hash with default value
               ret.put(k, val);
               
              // not first one
               if (cnt++ > 0) {
                  sql.append(",");
               }
               sql.append("'").append(k).append("'");
            }
            
            sql.append(") ");
            
           // ISOLATION
            sql.append(DB2PackageManager.getROUR());
            
            
            pstmt=connection.prepareStatement(sql.toString());
            pstmt.setString(1, username);
            
            rs = DbConnect.executeQuery(pstmt);
            
            while(rs.next()) {
               
               String optname = rs.getString(1);               
               String optval  = rs.getString(2);               
               
               ret.put(optname, optval);
            }
            
           // Use the session flag info in the User object. It should be
           //  uptodate from the point of gross level call on services
           //  API
            
           /*
           // Get hidden flag from session table IF we have a session set
            if (ret.get(DropboxGenerator.ShowHidden) != null &&
                user.getSessionId() != -1) {
               ret.put(DropboxGenerator.ShowHidden, 
                       ""+getSessionFlag(user, User.SessionFlag_ShowHidden));
            }
           */
            if (user.getSessionId() != -1) {
               if (ret.get(DropboxGenerator.ShowHidden) != null) {
                  ret.put(DropboxGenerator.ShowHidden, 
                          ""+getSessionFlag(user, 
                                            User.SessionFlag_ShowHidden));
               }
               if (ret.get(DropboxGenerator.ItarCertified) != null) {
                  ret.put(DropboxGenerator.ItarCertified,
                          ""+getSessionFlag(user, 
                                            User.SessionFlag_ItarCertified));
               }
               if (ret.get(DropboxGenerator.ItarSessionCertified) != null) {
                  ret.put(DropboxGenerator.ItarSessionCertified,
                          ""+getSessionFlag(user, 
                                            User.SessionFlag_ItarSessionCertified));
               }
            }
           
            connection.commit();
            
         } catch (SQLException e) {
            log.error("DB2PM.getUserOptions: SQL except doing: " + 
                      (sql==null?"sqlisnull":sql.toString()));         
            DbConnect.destroyConnection(connection); connection=null; pstmt=null;
            DboxAlert.alert(e);  // Generic Alert
            throw new 
               DboxException("DB2PackageMgr: getUserOptions:>> SQL failure", 
                             DboxException.SQL_ERROR);
         } finally {
            if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
            DbConnect.returnConnection(connection);
         } 
      }
      return ret;
   }
   
   public Hashtable getUserOptions(String u, Vector v) throws DboxException {
      User user = new User();
      user.setName(u);
      user.setSessionId(-1);
      return getUserOptions(user, v);
   }
   
   public String getUserOption(User user, String k) throws DboxException {
      Vector v = new Vector();
      v.addElement(k);
      Hashtable h = getUserOptions(user, v);
      return (String)h.get(k);
   }
   
   public String getUserOption(String u, String k) throws DboxException {
      User user = new User();
      user.setName(u);
      user.setSessionId(-1);
      return getUserOption(user, k);
   }
   
   
   public int  setPackageOption(User user, long pkgid, int msk, int vals) 
      throws DboxException {
      
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      StringBuffer sql =null;
      ResultSet rs=null;	
      int newflags = 0;
      
      try {
         
         DboxPackageInfo info = lookupPackage(pkgid);
         String ownerid = info.getPackageOwner();
         if (ownerid==null || user == null ||
             (!ownerid.equals(user.getName()) && 
              getPrivilegeLevel(user.getName()) < PRIVILEGE_SUPER_USER)) {
            throw new DboxException("setPackageOption: error:>> package " + 
                                    pkgid + " not owned by " + user.getName(),
                                    0); 
         }
         
         assertValidFlags(msk, vals);
                  
         if (msk == 0) {
            return info.getPackageFlags();
         }
         
        // Remove the values indicated in mask, then OR in the new values
         newflags  = (((int)info.getPackageFlags()) & ~msk) & 0xff;
         newflags |= (msk & vals);
         
         connection=DbConnect.makeSharedConn();
         
        // This is same as package lock
         sql = new
            StringBuffer("update edesign.package set flags=? where pkgid = ?");
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setInt(1, newflags);
         pstmt.setLong(2, pkgid);
         
         if (DbConnect.executeUpdate(pstmt) != 1) {
            throw new 
               DboxException("DB2PackageMgr: setPkgOption:>> Update failed!", 0);
         }
         
         connection.commit();
         
      } catch (SQLException e) {
         log.error("DB2PM.getUserOptions: SQL except doing: " + 
                   (sql==null?"sqlisnull":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2PackageMgr: setPkgOption:>> SQL failure", 
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      } 
      
      return newflags;
   }   
   
  //
  // Query to retrieve pkgid/userid pairs for those who should receive 
  //  Nag mail. Candidates are those individuals who are listed in 
  //  the access list either directly or via a group (projects are not 
  //  checked (sorry!). Hidden packages are excluded as well as packages
  //  which are not yet committed.  If the user has marked the package,
  //  then it will also be ommitted for that user. So really, a package 
  //  is only included for a user if that user has not fully downloaded
  //  it, and it is not marked, and its about to expire (which, hey
  //  is the whole point anyway).
  //
  //  Oh yeah, and if the package notification is turned off by the sender
  //   we STILL send the nag. Thought being losing the ability to download
  //   a package is different than wanting to know when someone sent a pack.
  //
  //   If the package receiver wants no nags, he can disable via a user
  //   option called NagNotifications. If so, then the receiver will be
  //   pulled from the list.
  //
  // nagdays parm:  format <%tillexpday><:0><:-day>
  //
  //                  eg. 80:95:0:-4    
  //
  //   This example shows that nags will be sent out when 80% and 95%
  //    of the lifetime of the package has passed, as well as on the 
  //    last day (0) and 4 days prior to the package expiring. Its 
  //    probably best to NOT mix the metaphores too much, as different
  //    expiration plans would result in nags going out on successive
  //    days (eg. 14 day liftime and 75:-3 would have a nag sent on the 
  //    10th and 11th day only. 
  //
  //   Here is a good starting plan
  //
  //                  eg. -3:-2:-1:0:75
  //
  //   This will nag the last 4 days of a package's life, as well as at
  //    75% done. Nagging will NOT be done for less than 50% of a packages
  //    life, so a package created with a 2 day lifetime, will get a nag
  //    only on the last day.
  //
  //  A major assumption here is that the nag process will run at the same 
  //   relative time each day, otherwise we will get skipped emails and/or
  //   possibly double emails.  We turn everything into 'days', and base all
  //   percentages on day values. 
  //                
  // Returns  Map key = Long(pkgid)  val Vector of String usernames
  //
  /* Debug db2 query. Dumps ALL non-deleted packs with halflife, curlife, daysleft
                      commited and expiration dates
  
    select CAST (pkgid as integer) as pkgid, CAST (((((p.expiration/(1000*60*60*24))+719163)-DAYS(p.committed))/2) AS INTEGER) as halflife, CAST(days(current timestamp)-DAYS(p.committed) as INTEGER) as curlife,CAST (((((p.expiration/(1000*60*60*24))+719163)-DAYS(current timestamp))) AS INTEGER) as daysleft,DATE(committed) as committed,date(((p.expiration/(1000*60*60*24)))+719163) as expiration from edesign.package p where committed is not null and deleted is null
    
   */
   public Map queryNagInfo(String nagdays) throws DboxException {
      
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      StringBuffer sql =null;
      ResultSet rs=null;	
      int newflags = 0;
      
      Map ret = new HashMap();
      
     // Convert expiration (currentTimeMillis()) to DAYS value
     // DAYS function returns days since 01/01/0001 + 1
     // so convert millis since 01/01/1970 to days format ... add in
     // the delta days from 01/01/0001 to 01/01/1970 ... 719163 (trust me)
      String eprS = "((p.expiration/(1000*60*60*24))+719163) ";
      
      long curdays = (System.currentTimeMillis()/(1000*60*60*24))+719163;
      
      try {
         
        // Hidden packs and packnotify ... need to remove any packages with flags
        //  containing PackageInfo.HIDDEN. No bitwise operators, so be clever and
        //  use mod function to isolate the bit position
         
         connection=DbConnect.makeSharedConn();
         sql = new StringBuffer("select p.pkgid, ta.userid from edesign.package p, table(select gm.userid from edesign.groupmembers gm, edesign.pkgacl pa where pa.pkgid = p.pkgid AND pa.usertype = ? AND pa.userid = gm.groupname UNION ALL select pa.userid from edesign.pkgacl pa where pa.pkgid = p.pkgid AND pa.usertype = ?) as ta where p.deleted is null AND p.committed IS NOT NULL AND p.pkgstat = ? AND MOD((p.flags / ");
         sql.append(PackageInfo.HIDDEN).append("), 2) = 0 AND p.fileentry > 0 AND p.fileentry > (select value(sum(case when fa.fileid = 0 then 99999999 else case when fa.dloadstat = ? then 1 else 0 END END), 0) from edesign.fileacl fa where fa.pkgid = p.pkgid and fa.userid = ta.userid)");
         
         sql.append(" AND ta.userid NOT IN (SELECT userid FROM edesign.useroptions WHERE optname = '");;
         sql.append(DropboxGenerator.NagNotification);
         sql.append("' AND LOWER(optval) = 'false') ");
         
        // Only match it if we are beyond 50% of package lifetime ... defined to be 
        //  from commit point to expiration
         sql.append(" AND (((").append(eprS).append("-DAYS(p.committed))/2) < (");
         sql.append(curdays).append("-DAYS(p.committed)))");
         
        // Only match it if its NOT already expired ... really don't need this
        //  if culling is done just before
         sql.append(" AND (((").append(curdays).append(" < ").append(eprS);
         sql.append("))) AND (");
         
        // Add in all expiration day ranges
         try {
            
            StringTokenizer stok = new StringTokenizer(nagdays, ":", false);
            int num=0;
            StringBuffer daysprior = new StringBuffer();
            StringBuffer pctOfLife = new StringBuffer(); 
            while(stok.hasMoreTokens()) {
               String pctS = stok.nextToken();
               int pct = Integer.parseInt(pctS);
               
               log.debug("nagging for " + pct);
               
               num++;
               
               if (pct <= 0) {
                 // This is days prior to end
                  if (daysprior.length() == 0) {
                     daysprior.append(eprS).append(" IN (");
                  } else {
                     daysprior.append(", ");
                  }
                  daysprior.append(curdays-pct);
               } else {
                 // This is percent of life
                  if (pctOfLife.length() == 0) {
                     pctOfLife.append("(").append(eprS).append("-DAYS(p.committed)) in (");
                  } else {
                     pctOfLife.append(", ");
                  }
                  pctOfLife.append("((").append(curdays);
                  pctOfLife.append("-DAYS(p.committed))*100)/").append(pct);
               }
            }
            
            if (num == 0) {
               throw new DboxException("Invalid interval specifier for NagMail: " +
                                       nagdays);
            }
            
            if (daysprior.length() > 0) {
               sql.append(daysprior).append(")");
            }
            
            if (pctOfLife.length() > 0) {
               if (daysprior.length() > 0) {
                  sql.append(" OR ");
               }
               sql.append(pctOfLife).append(")");
            }
            
            sql.append(")");
            
            
         } catch(Exception e) {
            throw new DboxException("Error parsing NagInfo string", e);
         }
         
        // 
         sql.append(" group by p.pkgid, ta.userid ");
         
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         
         int idx = 1;
         pstmt.setByte(idx++, DropboxGenerator.STATUS_GROUP);
         pstmt.setByte(idx++, DropboxGenerator.STATUS_NONE);
         pstmt.setByte(idx++, DropboxGenerator.STATUS_COMPLETE);
         pstmt.setByte(idx++, DropboxGenerator.STATUS_COMPLETE);
         
         rs = DbConnect.executeQuery(pstmt);
         
         while(rs.next()) {
            Long   pkgidL  = (Long)rs.getObject(1);
            String useridS = rs.getString(2);
            
            Vector v = (Vector)ret.get(pkgidL);
            if (v == null) {
               ret.put(pkgidL, v = new Vector());
            }
            
            if (!v.contains(useridS)) {
               v.add(useridS);
               log.debug("NagInfo for package: " + pkgidL + " and user " + useridS);
            }
         }
         
         connection.commit();
         
      } catch (SQLException e) {
         log.error("DB2PM.queryNagInfo: SQL except doing: " + 
                   (sql==null?"sqlisnull":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2PackageMgr: queryNagInfo:>> SQL failure", 
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      } 
      
      return ret;
   }
}
