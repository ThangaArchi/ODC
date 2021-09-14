package oem.edge.ed.odc.dropbox.server;

import  oem.edge.ed.odc.dropbox.common.*;
import  oem.edge.ed.odc.ftp.common.*;
import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.dsmp.server.*;
import  oem.edge.ed.odc.util.*;

import java.lang.*;
import java.io.*;
import java.util.*;
import java.net.*;
import java.lang.reflect.*;
import java.util.zip.*;
import java.sql.*;

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

public class DB2FileAccess extends FileAccess {

   static Logger log = Logger.getLogger(DB2FileAccess.class.getName());

   long pkgid;
   DB2FileAccess (long P) {
      super();
      pkgid=P;
   }
   
   public synchronized void add(long fileid, AclInfo aclinfo, User user) 
      throws DboxException {
   		  
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      
      SharedConnection connection=null;
      StringBuffer sql = null;
	  
      boolean exist = false;
      byte taclstat=(byte)0;
      try{
      
         String company = null;
         boolean dodeptdiv = false;
         String dept= null;
         String div = null;;
         
         if (user != null) {
            company = user.getCompany();
            dept = user.getIBMDept();
            div  = user.getIBMDiv();
            dodeptdiv = dept != null;
         } else {
            company = aclinfo.getAclCompany();
         }
		 	
         connection=DbConnect.makeSharedConn();
         
         int xferate = aclinfo.getXferRate();
         if (xferate < 0) xferate = 0;
         
         try {
            sql = new StringBuffer("INSERT INTO EDESIGN.FILEACL ");
            if (dodeptdiv) {
               sql.append("(FILEID, DLOADSTAT, USERID, PROJNAME, PKGID, COMPANY,XFERSPEED, DEPT, DIVISION) VALUES (?,?,?,?,?,?,?,?,?)");
            } else {
               sql.append("(FILEID, DLOADSTAT, USERID, PROJNAME, PKGID, COMPANY,XFERSPEED) VALUES (?,?,?,?,?,?,?)");
            }
            pstmt=connection.prepareStatement(sql.toString());
            pstmt.setLong(1, fileid);
            pstmt.setByte(2, aclinfo.getAclStatus());
            pstmt.setString(3, aclinfo.getAclName());
            pstmt.setString(4, aclinfo.getAclProjectName());
            pstmt.setLong(5, pkgid);
            pstmt.setString(6, company);
            pstmt.setInt   (7, xferate);
            if (dodeptdiv) {
               pstmt.setString(8, dept);
               pstmt.setString(9, div);
            }
            
            if (DbConnect.executeUpdate(pstmt) != 1) {
               log.error("DB2FileAccess: error adding fileaccess record [" +
                         sql.toString() + "]");
            }
         } catch(SQLException sqexinner) {
         
           // If its a complaint about constraint, do the update
            if (!sqexinner.getSQLState().equals("23505")) {
               throw sqexinner;
            }
            
            sql = new StringBuffer("UPDATE EDESIGN.FILEACL facl ");
            
            sql.append("SET facl.DLOADSTAT=?,CREATED=CURRENT TIMESTAMP");
            if (xferate > 0) {
               sql.append(",facl.XFERSPEED=?");
            }
            sql.append(" WHERE FILEID=? AND facl.PKGID=? AND lower(facl.userid)=? AND facl.DELETED is null AND DLOADSTAT != ?");
               
            pstmt.close();
            pstmt=connection.prepareStatement(sql.toString());
               
            int i=1;
            pstmt.setByte   (i++, aclinfo.getAclStatus());
            
            if (xferate > 0) {
               pstmt.setInt    (i++, xferate);
            }
            pstmt.setLong   (i++, fileid);
            pstmt.setLong   (i++, pkgid);
            pstmt.setString (i++, aclinfo.getAclName().toLowerCase());
            pstmt.setByte   (i++, DropboxGenerator.STATUS_COMPLETE);
            
           // If NO change AND we have an xfer rate AND we are complete ...
           //  just plunk the xfer rate in there
            if (DbConnect.executeUpdate(pstmt) == 0 && 
                xferate > 0                         && 
               aclinfo.getAclStatus() == DropboxGenerator.STATUS_COMPLETE) {
               
               pstmt.close();
               
               sql = new StringBuffer("UPDATE EDESIGN.FILEACL facl ");
            
               sql.append("SET facl.XFERSPEED=?");
               
               sql.append(" WHERE FILEID=? AND facl.PKGID=? AND lower(facl.userid)=? AND facl.DELETED is null");
               
               pstmt=connection.prepareStatement(sql.toString());
               
               i=1;
               
               pstmt.setInt    (i++, xferate);
               pstmt.setLong   (i++, fileid);
               pstmt.setLong   (i++, pkgid);
               pstmt.setString (i++, aclinfo.getAclName().toLowerCase());
               
               if (DbConnect.executeUpdate(pstmt) != 1) {
                  log.error("DB2FileAccess: error Modifying fileaccess record [" + sql.toString() + "]");
               }
            }
         }
			
         connection.commit();
         
      } catch (SQLException e){
         log.error("DB2FileAccess:add: SQL except doing: " + 
                   (sql==null?"":sql.toString()) + " " + aclinfo.toString()); 
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2FileAccess:add:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
      
   public synchronized void remove(long fileid) throws DboxException {
		  
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      
      SharedConnection connection=null;
      StringBuffer sql = null;
      try{
         connection=DbConnect.makeSharedConn();
         sql=new StringBuffer ("DELETE FROM EDESIGN.FILEACL WHERE FILEID=? AND PKGID=? AND DELETED is null");
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1,fileid);
         pstmt.setLong(2, pkgid);
            
         DbConnect.executeUpdate(pstmt);
         
         connection.commit();
         
      } catch (SQLException e) {
         log.error("DB2FileAccess:remove: SQL except doing: " + 
                   (sql==null?"":sql.toString()) + " " + fileid); 
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2FileAccess:remove:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
   
   public synchronized void remove(User user, long fileid) 
      throws DboxException {
		  
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      
      SharedConnection connection=null;
      StringBuffer sql = null;
      try{
         connection=DbConnect.makeSharedConn();
         sql=new StringBuffer ("DELETE FROM EDESIGN.FILEACL WHERE FILEID=? AND PKGID=? AND USERID=? AND DELETED is null");
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1,fileid);
         pstmt.setLong(2, pkgid);
         pstmt.setString(3, user.getName());
            
        // Can be 0 or 1
         if (DbConnect.executeUpdate(pstmt) > 1) {
            throw new DboxException("FileAccess:>> DB Error during accessremove",
                                    0);
         }
      
         connection.commit();
      
      } catch (SQLException e) {
         log.error("DB2FileAccess:remove: SQL except doing: " + 
                   (sql==null?"":sql.toString()) + " " + fileid); 
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2FileAccess:remove:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
         
      
  // Return vec contains DboxFileAclInfo
   public synchronized Vector filesAccessedByUser(String user)
      throws DboxException {

      Vector ret = null;
      AclInfo aclinfo=new AclInfo();
      DboxFileAclInfo daclinfo=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      
      SharedConnection connection=null;
      StringBuffer sql = null;
      try{
         connection=DbConnect.makeSharedConn();
         
        // Ignore the markread fileid=0
         sql = new StringBuffer ("SELECT FILEID, DLOADSTAT, PROJNAME, COMPANY, XFERSPEED, CREATED FROM EDESIGN.FILEACL facl ");
         sql.append("WHERE facl.USERID=? AND facl.PKGID=? AND facl.FILEID!=0");
         sql.append(" AND facl.DELETED is null ");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setString(1,user);
         pstmt.setLong(2, pkgid);
         
         rs=DbConnect.executeQuery(pstmt);
         
         while (rs.next()){
            aclinfo.setAclName(user);
            aclinfo.setAclStatus(rs.getByte(2));
            aclinfo.setAclProjectName(rs.getString(3));
            aclinfo.setAclCompany(rs.getString(4));
            aclinfo.setXferRate(rs.getInt(5));
            aclinfo.setAclCreateTime(rs.getTimestamp(6).getTime());
            daclinfo=new DboxFileAclInfo(aclinfo, rs.getLong(1));
            ret.addElement(daclinfo);
         }
         
         connection.commit();
         
      } catch (SQLException e) {
         log.error("DB2FileAccess:filesAccessedByUser: SQL except doing: " + 
                   (sql==null?"":sql.toString()) + " " + user); 
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2FileAccess:filesAccessedByUser:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
        
      return ret;
   }
      
  // Returns number of files access by the specified user
   public synchronized int numberFilesAccessedBy(String user)
      throws DboxException {
		  
      int ret = 0;
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      
      SharedConnection connection=null;
      StringBuffer sql =null;
      try{
         connection=DbConnect.makeSharedConn();
        // Ignore make as read fileid = 0
         sql=new StringBuffer("SELECT COUNT(*) FROM EDESIGN.FILEACL facl ");
         sql.append("WHERE facl.USERID=? AND facl.PKGID=? AND facl.FILEID!=0");
         sql.append(" AND facl.DELETED is null");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString ());
         pstmt.setString(1,user);
         pstmt.setLong(2,pkgid);
         
         rs=DbConnect.executeQuery(pstmt);
         
         if (rs.next())
            ret=rs.getInt(1);
        
         connection.commit();
        
      } catch (SQLException e) {
         log.error("DB2FileAccess:numFilesAccessedBy: SQL except doing: " + 
                   (sql==null?"":sql.toString()) + " " + user); 
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2FileAccess:numFilesAccessedBy:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
        
      return ret;
   }
      
   public synchronized DboxFileAclInfo fileAccessedBy(long fileid, 
                                                      String user)
      throws DboxException {

      DboxFileAclInfo ret = null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      
      SharedConnection connection=null;
      StringBuffer sql = null;
      try{
         connection=DbConnect.makeSharedConn();
         sql = new StringBuffer("SELECT DLOADSTAT, PROJNAME, COMPANY, XFERSPEED, CREATED FROM EDESIGN.FILEACL facl ");
         sql.append("WHERE facl.USERID=? AND facl.FILEID=? AND facl.PKGID=?");
         sql.append(" AND facl.DELETED is null");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setString(1,user);
         pstmt.setLong(2, fileid);
         pstmt.setLong(3, pkgid);
         
         rs=DbConnect.executeQuery(pstmt);
         
         if (rs.next()){
            AclInfo aclinfo=new AclInfo();
            aclinfo.setAclName(user);
            aclinfo.setAclStatus(rs.getByte(1));
            aclinfo.setAclProjectName(rs.getString(2));
            aclinfo.setAclCompany(rs.getString(3));
            aclinfo.setXferRate(rs.getInt(4));
            aclinfo.setAclCreateTime(rs.getTimestamp(5).getTime());            
            ret=new DboxFileAclInfo(aclinfo, fileid);
         }
      
         connection.commit();
      
      } catch (SQLException e) {
         log.error("DB2FileAccess:fileAccessedBy: SQL except doing: " + 
                   (sql==null?"":sql.toString()) + " " + user + " " + fileid); 
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2FileAccess:fileAccessedBy:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
        
      return ret;
   }
      
      
  // returns the number of complete uniq file xfers
   public synchronized int statusCompleteFor(String user)
      throws DboxException {

      int ret = 0;
		 
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      
      SharedConnection connection=null;
      StringBuffer sql =null;
      try{
         connection=DbConnect.makeSharedConn();
        // Ignore marked as read fileid = 0
         sql = new StringBuffer ("SELECT COUNT(*) FROM EDESIGN.FILEACL facl ");
         sql.append("WHERE facl.USERID=? AND facl.DLOADSTAT=? AND facl.PKGID=? AND facl.FILEID!=0 AND facl.DELETED is null");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setString(1,user);
         pstmt.setByte(2, DropboxGenerator.STATUS_COMPLETE);
         
         rs=DbConnect.executeQuery(pstmt);
         
         if (rs.next())
            ret=rs.getInt(1);
         
         connection.commit();
         
      } catch (SQLException e) {
         log.error("DB2FileAccess:statusCompleteFor: SQL except doing: " + 
                   (sql==null?"":sql.toString()) + " " + user); 
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2FileAccess:statusCompleteFor:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
        
      return ret;
   }
         
      
  // Returns uniq userlist (strings) for anyone accessing any files
  //   in this file scope
   public synchronized Vector userList()
      throws DboxException {
		 
      Vector ret = new Vector();
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      
      SharedConnection connection=null;
     // Hashtable lhash = new Hashtable();
      StringBuffer sql =null;
      try{
         connection=DbConnect.makeSharedConn();
         sql = new StringBuffer("SELECT USERID FROM EDESIGN.FILEACL facl WHERE facl.PKGID=? AND facl.DELETED is null GROUP BY USERID");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1, pkgid);
         
         rs=DbConnect.executeQuery(pstmt);
			
         while (rs.next()){
           //   String name=rs.getString(1);
           //    if(lhash.get(name) == null)
            ret.addElement(rs.getString(1));
         }
         
         connection.commit();
         
      } catch (SQLException e) {
         log.error("DB2FileAccess:userList: SQL except doing: " + 
                   (sql==null?"":sql.toString())); 
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2FileAccess:userList:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
        
      return ret;
   }
        
        
   public synchronized Vector accessSummary(int numexpected)
      throws DboxException {
    
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      
      SharedConnection connection=null;
      StringBuffer sql = null;
      AclInfo aclinfo=null;
      Vector ret = new Vector();
      try{
         connection=DbConnect.makeSharedConn();
        // Ignore fileid of 0, that is the mark as read record for the package
         sql = new StringBuffer("SELECT USERID, COMPANY, COUNT(*) FROM EDESIGN.FILEACL facl ");
         sql.append("WHERE facl.DLOADSTAT=? AND facl.PKGID=? AND facl.FILEID!=0 AND facl.DELETED is null GROUP BY USERID,COMPANY");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setByte(1, DropboxGenerator.STATUS_COMPLETE );
         pstmt.setLong(2, pkgid);
		 
         rs=DbConnect.executeQuery(pstmt);
			
         while (rs.next()){
            aclinfo=new AclInfo();
            aclinfo.setAclName(rs.getString(1));
            aclinfo.setAclCompany(rs.getString(2));
            if (rs.getInt(3)>=numexpected)
               aclinfo.setAclStatus(DropboxGenerator.STATUS_COMPLETE);
            else if (rs.getInt(3)>0)
               aclinfo.setAclStatus(DropboxGenerator.STATUS_PARTIAL);
            else
               aclinfo.setAclStatus (DropboxGenerator.STATUS_FAIL);
            
            ret.addElement(aclinfo);
         }
      
         connection.commit();
      
      } catch (SQLException e) {
         log.error("DB2FileAccess:numExpected: SQL except doing: " + 
                   (sql==null?"":sql.toString())); 
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2FileAccess:numExpected:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
        
      return ret;
   
   }
      
  // Return vec contains DboxFileAclInfo
   public synchronized Vector accessAclsForFile(long fileid)
      throws DboxException {
		  
      Vector ret = new Vector(); 
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      
      SharedConnection connection=null;
      AclInfo aclinfo=new AclInfo();
      DboxFileAclInfo daclinfo=null;
      StringBuffer sql = null;
      try{
         connection=DbConnect.makeSharedConn();
         sql = new StringBuffer("SELECT DLOADSTAT, USERID, PROJNAME, COMPANY, XFERSPEED, CREATED FROM EDESIGN.FILEACL facl ");
         sql.append("WHERE facl.FILEID=? AND facl.PKGID=? AND facl.FILEID!=0 AND facl.DELETED is null");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1, fileid);
         pstmt.setLong(2, pkgid);
         
         rs=DbConnect.executeQuery(pstmt);
			
         while (rs.next()){
				
            aclinfo.setAclStatus(rs.getByte(1));
            aclinfo.setAclName(rs.getString(2));
            aclinfo.setAclProjectName(rs.getString(3));
            aclinfo.setAclCompany(rs.getString(4));
            aclinfo.setXferRate(rs.getInt(5));
            aclinfo.setAclCreateTime(rs.getTimestamp(6).getTime());
            daclinfo=new DboxFileAclInfo(aclinfo, fileid);
            ret.addElement(daclinfo);
         }
        
         connection.commit();
        
      } catch (SQLException e) {
         log.error("DB2FileAccess:accessAclsForFile: SQL except doing: " + 
                   (sql==null?"":sql.toString()) + " " + fileid); 
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2FileAccess:accessAclsForFile:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
        
      return ret;
   }
         
      
   public String toString() {
      String ret = "File Access:";
		 
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      
      SharedConnection connection=null;
      Hashtable lhash = new Hashtable();
      StringBuffer sql = null;
      try{
         connection=DbConnect.makeSharedConn();
         
         sql = new StringBuffer("SELECT FILEID, USERID, DLOADSTAT, PROJNAME, COMPANY FROM EDESIGN.FILEACL facl WHERE facl.PKGID=? AND facl.DELETED is null");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
        
         rs=DbConnect.executeQuery(pstmt);
			
         while (rs.next()){
            ret += Nester.nest("\n---- File : " + rs.getLong(1) + " has ACLs ->");
            ret += Nester.nest("\naclname   = " + rs.getString(2) +
                               "\naclstatus = " +rs.getByte(3) +
                               "\naclprojname = " +rs.getString(4) + 
                               "\naclcompany = " +rs.getString(5));
         }
      
         connection.commit();
         
      } catch (SQLException e) {
         log.error("DB2FileAccess:toString: SQL except doing: " + 
                   (sql==null?"":sql.toString())); 
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
        
      return ret;
   }
}
