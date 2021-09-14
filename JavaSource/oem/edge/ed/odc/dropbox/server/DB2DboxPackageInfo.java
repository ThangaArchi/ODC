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
/*     (C)Copyright IBM Corp. 2003-2004,2005,2006                           */
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
public class DB2DboxPackageInfo extends DboxPackageInfo {

   static Logger log = Logger.getLogger(DB2DboxPackageInfo.class.getName());
   
   DB2DboxPackageInfo(PackageManager pm) {
      super(pm);
   }
   
   DB2DboxPackageInfo(DB2DboxPackageInfo in) {
      super(in);
   }
      
      
   public DboxPackageInfo cloneit() {
      return new DB2DboxPackageInfo(this);
   }
      
   Vector getFiles() throws DboxException {
		  
      Vector ret = new Vector();
     
      SharedConnection connection=null;
      DboxFileInfo info=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      StringBuffer sql = null;      
      try{
			
         connection=DbConnect.makeSharedConn();
         sql = new StringBuffer("SELECT f.FILEID, f.FILENAME, f.FILESTAT, ");
         
        // FSZCHANGE Used to have filesize, now calculate it
        // JMC 7/29/04 - Ok, now do it if we say we trust the encoded sizes. 
         if (DB2PackageManager.getTrustSizes()) {
            sql.append(" f.FILESIZE, ");
         } else {
            sql.append("VALUE((SELECT SUM(fc.COMPONENTSIZE) from EDESIGN.FILECOMPONENT fc where f.FILEID=fc.FILEID AND fc.DELETED is null AND f.DELETED is null ");
            
           // ISOLATION - SUBSELECT
            sql.append(DB2PackageManager.getROURSubselect());
            
            sql.append("),0),");
         }
         
         sql.append(" f.INTENDEDSIZE, f.MD5, p.EXPIRATION, f.CREATED, f.MD5BLOB, F.POOLID,F.XFERSPEED ");
         sql.append("FROM EDESIGN.FILE f, EDESIGN.PACKAGE p, EDESIGN.FILETOPKG fp WHERE f.FILEID = fp.FILEID AND p.PKGID=fp.PKGID AND p.PKGID=? ");
         sql.append("AND p.deleted is null AND fp.DELETED is null AND f.DELETED is null");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1, getPackageId());
         
         rs=DbConnect.executeQuery(pstmt);
         
         while(rs.next()) {
            int i=1;
            long fid      = rs.getLong(i++);
            String fname  = rs.getString(i++);
            byte fstat    = rs.getByte(i++);
            long fsz      = rs.getLong(i++);
            long fisz     = rs.getLong(i++);
            String md5    = rs.getString(i++);
            long fexp     = rs.getLong(i++);
            long fcreate  = rs.getTimestamp(i++).getTime();
            byte[] md5bytes = rs.getBytes(i++);
            int  poolid   = rs.getInt(i++);
            int  xferrate = rs.getInt(i++);
            
            info = new DB2DboxFileInfo(packageManager.getFileManager(),
                                       fname, fsz, fstat, fisz);
            info.setFileId(fid);
            info.setFileExpiration(fexp);
            info.setFileCreation(fcreate);
            info.setPoolId(poolid);
            info.setFileXferrate(xferrate);
            ret.addElement(info);
            
           // If we have md5bytes, setting will set both md5object and blob,
           //   otherwise, set md5 string. 'OLD' dbox code uses this path
            if (md5bytes == null) info.setFileMD5ObjectFromBytes(md5bytes);
            else                  info.setFileMD5(md5);
         }
         
         connection.commit();
         
      } catch (SQLException e) {
         log.error("DB2PI.getFiles: SQL except: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2DBoxPI:>> SQLException during getFiles", 
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt != null) try {pstmt.close();} catch(SQLException e) {}
         DbConnect.returnConnection(connection);
      }   
		 
      return ret;
   }
   void addFile(DboxFileInfo info) throws DboxException {
		  
     
      SharedConnection connection=null;
      long fileid=info.getFileId();	
      StringBuffer sql = null; 
      PreparedStatement pstmt = null;      
      try{
			
         connection=DbConnect.makeSharedConn();
         pstmt=null;
         ResultSet rs=null;
        
         packageManager.lockpackage(getPackageId());
         
         sql = new StringBuffer("INSERT INTO EDESIGN.FILETOPKG (PKGID, FILEID) VALUES (?, ?)");
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1,getPackageId());
         pstmt.setLong(2,fileid);
         
         if (DbConnect.executeUpdate(pstmt) != 1) {
            throw new DboxException("DB2DboxPI:>> addfile != 1 matching",0);
         }
         
         connection.commit();
         
      } catch (SQLException e) {
         log.error("DB2PI.addFile: SQL except: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2DBoxPI:>> SQLException during addFile",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt != null) try {pstmt.close();} catch(SQLException e) {}
         DbConnect.returnConnection(connection);
      }   
      
     // Recalculate the package size now with the file added. Do it outside 
     //  of the above try block as we have the connection returned
      recalculatePackageSize();
   }
         
   void removeFile(long itemid) throws DboxException {
		  
     
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;	
      StringBuffer sql = null; 
      try{
      
			
         connection=DbConnect.makeSharedConn();
         
         packageManager.lockpackage(getPackageId());
         
         sql = new StringBuffer ("DELETE FROM EDESIGN.FILETOPKG fp WHERE fp.FILEID=? AND fp.PKGID=? AND fp.DELETED is null");
         
         pstmt=connection.prepareStatement(sql.toString ());
         pstmt.setLong(1,itemid);
         pstmt.setLong(2,getPackageId());
         
         if (DbConnect.executeUpdate(pstmt) == 1) {
            DB2FileAccess dbfileAccess = new DB2FileAccess(getPackageId());
            dbfileAccess.remove(itemid);
         } else {
            throw new DboxException("packageInfo:removeFile:>> package does not contain specified item: " + itemid, 0);
         }
         
         connection.commit();
			
      } catch (SQLException e) {
         log.error("DB2PI.removeFile: SQL except: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2DBoxPI:>> SQLException during removeFile",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt != null) try {pstmt.close();} catch(SQLException e) {}
         DbConnect.returnConnection(connection);
      }   
      
     // Recalculate the package size now with the file removed. Do it outside 
     //  of the above try block as we have the connection returned
      recalculatePackageSize();
   }
      
  // Impl of this lives in DB2PackageManager now ... sigh
   public void recalculatePackageSize() throws DboxException {
      ((DB2PackageManager)packageManager).recalculatePackageSize(packageid);
   }
      
   boolean includesFile(long itemid) throws DboxException {
         
     
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;	
      StringBuffer sql = null;
      boolean ret=false;
      try{
			
         connection=DbConnect.makeSharedConn();

         sql = new StringBuffer("SELECT FILEID FROM EDESIGN.FILETOPKG fp WHERE fp.PKGID=? AND fp.FILEID=? AND fp.DELETED is null");

        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1,getPackageId());
         pstmt.setLong(1,itemid);
         
         rs=DbConnect.executeQuery(pstmt);
			
         if (rs.next()){
            ret=true;
         }
         
         connection.commit();
         
      } catch (SQLException e) {
         log.error("DB2PI.includesFile: SQL except: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2DBoxPI:>> SQLException during includesFile",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt != null) try {pstmt.close();} catch(SQLException e) {}
         DbConnect.returnConnection(connection);
      }   
         
      return ret;
   }
      
   DboxFileInfo getFile(String name) throws DboxException {
		  
     
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      DboxFileInfo info = null;
      StringBuffer sql = null;
      try{
			
         connection=DbConnect.makeSharedConn();

         sql=new StringBuffer("SELECT F.FILEID, ");
         
        // FSZCHANGE Used to have filesize, now calculate it
        // JMC 7/29/04 - Ok, now do it if we say we trust the encoded sizes. 
         if (DB2PackageManager.getTrustSizes()) {
            sql.append(" f.FILESIZE, ");
         } else {
            sql.append("VALUE((SELECT SUM(fc.COMPONENTSIZE) from EDESIGN.FILECOMPONENT fc where f.FILEID=fc.FILEID AND fc.DELETED is null and f.DELETED is null ");
            
           // ISOLATION - SUBSELECT
            sql.append(DB2PackageManager.getROURSubselect());
            
            sql.append("),0),");
         }
         
         sql.append(" F.FILESTAT, F.INTENDEDSIZE, F.MD5, P.EXPIRATION, F.CREATED, F.MD5BLOB, F.POOLID, F.XFERSPEED FROM EDESIGN.FILE F, EDESIGN.PACKAGE P, EDESIGN.FILETOPKG FP WHERE F.FILEID=FP.FILEID AND FP.PKGID=P.PKGID AND P.PKGID=? AND F.FILENAME=? ");
         sql.append("AND p.DELETED is null and f.DELETED is null and fp.DELETED is null ");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());

         pstmt.setLong(1, getPackageId());
         pstmt.setString(2, name);

         rs=DbConnect.executeQuery(pstmt);

         if(!rs.next()) {

            throw new DboxException("PackageInfo: getFile:>> "+ getPackageId()+
                                    " does not include file " + name, 
                                    0);
         }

         long fid     = rs.getLong(1);
         long fsz     = rs.getLong(2);
         byte fstat   = rs.getByte(3);
         long fisz    = rs.getLong(4);
         String md5   = rs.getString(5);
         long fexp    = rs.getLong(6);
         long fcreate = rs.getTimestamp(7).getTime();
         
         byte md5bytes[] = rs.getBytes(8);
         int  poolid   = rs.getInt(9);
         int  xferrate = rs.getInt(10);
         
         
         info = new DB2DboxFileInfo(packageManager.getFileManager(),
                                    name, fsz, fstat, fisz);
         info.setFileId(fid);
         info.setFileExpiration(fexp);
         info.setFileCreation(fcreate);
         info.setPoolId(poolid);
         info.setFileXferrate(xferrate);
         
        // If we have md5bytes, setting will set both md5object and blob,
        //   otherwise, set md5 string. 'OLD' dbox code uses this path
         if (md5bytes == null) info.setFileMD5ObjectFromBytes(md5bytes);
         else                  info.setFileMD5(md5);
         			
         connection.commit();
                        
      } catch (SQLException e) {
         log.error("DB2PI.getFile: SQL except: " + 
                   (sql==null?"":sql.toString()) + " " + name);         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2DBoxPI:>> SQLException during getFile",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt != null) try {pstmt.close();} catch(SQLException e) {}
         DbConnect.returnConnection(connection);
      }   
         
      return info;
         
   }
      
   DboxFileInfo getFile(long itemid) throws DboxException {
		  
     
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;	
      StringBuffer sql = null;
      DboxFileInfo info = null;
      try {
			
         connection=DbConnect.makeSharedConn();
         sql = new StringBuffer("SELECT FILENAME, FILESTAT, ");

        // FSZCHANGE Used to have filesize, now calculate it
        // JMC 7/29/04 - Ok, now do it if we say we trust the encoded sizes. 
         if (DB2PackageManager.getTrustSizes()) {
            sql.append(" f.FILESIZE, ");
         } else {
            sql.append("VALUE((SELECT SUM(fc.COMPONENTSIZE) from EDESIGN.FILECOMPONENT fc where f.FILEID=fc.FILEID AND fc.DELETED is null ");
            
           // ISOLATION - SUBSELECT
            sql.append(DB2PackageManager.getROURSubselect());
            
            sql.append("),0),");
         }
         
         sql.append(" INTENDEDSIZE, MD5, EXPIRATION, f.CREATED, MD5BLOB, F.POOLID, F.XFERSPEED ");
         sql.append("FROM EDESIGN.PACKAGE p, EDESIGN.FILE f, EDESIGN.FILETOPKG fp ");
         sql.append ("WHERE p.PKGID=fp.PKGID AND fp.FILEID=f.FILEID AND p.PKGID=? AND f.FILEID=? ");
         sql.append("AND p.DELETED is null and f.DELETED is null and fp.DELETED is null ");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1,getPackageId());
         pstmt.setLong(2, itemid);
         
         rs=DbConnect.executeQuery(pstmt);
			
         if (rs.next()){
            String fname = rs.getString(1);
            byte fstat   = rs.getByte(2);
            long fsz     = rs.getLong(3);
            long fisz    = rs.getLong(4);
            String md5   = rs.getString(5);
            long fexp    = rs.getLong(6);
            long fcreate = rs.getTimestamp(7).getTime();
            byte md5bytes[] = rs.getBytes(8);
            int  poolid   = rs.getInt(9);
            int  xferrate = rs.getInt(10);
            
            info = new DB2DboxFileInfo(packageManager.getFileManager(),
                                       fname, fsz, fstat, fisz);
            info.setFileId(itemid);
            info.setFileExpiration(fexp);
            info.setFileCreation(fcreate);
            info.setPoolId(poolid);
            info.setFileXferrate(xferrate);
            
           // If we have md5bytes, setting will set both md5object and blob,
           //   otherwise, set md5 string. 'OLD' dbox code uses this path
            if (md5bytes == null) info.setFileMD5ObjectFromBytes(md5bytes);
            else                  info.setFileMD5(md5);
            
         } else {
            throw new DboxException("PackageInfo: getFile:>> File " + itemid + 
                                    " does not exist in package " + 
                                    getPackageId(), 0);
         }
			
         connection.commit();
                        
      } catch (SQLException e) {
         log.error("DB2PI.getFile: SQL except: " + 
                   (sql==null?"":sql.toString()) + " " + itemid);         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new DboxException("PackageInfo: getFile:>> SQLError getting File " + 
                                 itemid + " from package " + getPackageId(), 
                                 DboxException.SQL_ERROR); 
      } finally {
         if (pstmt != null) try {pstmt.close();} catch(SQLException e) {}
         DbConnect.returnConnection(connection);
      }   
         
      return info;
   }
        
   void addProjectAcl(String name) throws DboxException {
     
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;	
      StringBuffer sql =null;
      try{
			
         connection=DbConnect.makeSharedConn();
         
         packageManager.lockpackage(getPackageId());
         
         sql=new StringBuffer ("SELECT USERTYPE FROM EDESIGN.PKGACL pacl WHERE pacl.PKGID=? ");
         sql.append ("AND pacl.USERID=? AND pacl.USERTYPE=? AND pacl.DELETED is null");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1,getPackageId());
         pstmt.setString(2, name);
         pstmt.setByte(3, DropboxGenerator.STATUS_PROJECT);
         
         rs=DbConnect.executeQuery(pstmt);
			
         if (rs.next()){
           //throw new DboxException("addProjectAcl: Package " + 
           //                        getPackageId() + " already contains proj "
           //                        + name, 0);
            return;
         }
         
         rs.close();       rs = null;
         pstmt.close(); pstmt = null;
         
         sql = new StringBuffer("INSERT INTO EDESIGN.PKGACL (PKGID, USERID, USERTYPE) VALUES (?,?,?)");
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1, getPackageId());
         pstmt.setString(2, name);
         pstmt.setByte(3, DropboxGenerator.STATUS_PROJECT);
         
         if (DbConnect.executeUpdate(pstmt) < 1) {
            throw new DboxException("addProjectAcl:>> Package " + 
                                    getPackageId() + 
                                    ": error adding project acl "
                                    + name, 0);
         }
			
         connection.commit();
                        
      } catch (SQLException e) {
         log.error("DB2PI.addProjectAcl: SQL except: " + 
                   (sql==null?"":sql.toString()) + " " + name);         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new DboxException("PackageInfo: addProjAcl:>> SQLError setting acl "
                                 + name + " for package " + getPackageId(), 
                                 DboxException.SQL_ERROR); 
      } finally {
         if (pstmt != null) try {pstmt.close();} catch(SQLException e) {}
         DbConnect.returnConnection(connection);
      }   
   }
	  
   void removeProjectAcl(String name) throws DboxException {
		  
     
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;	
      StringBuffer sql =null;
      try{
			
         connection=DbConnect.makeSharedConn();
         
         packageManager.lockpackage(getPackageId());
         
         sql = new StringBuffer("DELETE FROM EDESIGN.PKGACL pacl WHERE pacl.PKGID=? AND pacl.USERID=? AND pacl.USERTYPE=? and pacl.DELETED is null");
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1,getPackageId());
         pstmt.setString(2, name);
         pstmt.setByte(3, DropboxGenerator.STATUS_PROJECT);
         
         if (DbConnect.executeUpdate(pstmt) < 1) {
            throw new DboxException("removeProjectAcl:>> Package " + 
                                    getPackageId() + " does not contain proj "
                                    + name, 0);
         }
				
         connection.commit();
                                
      } catch (SQLException e) {
         log.error("DB2PI.remProjectAcl: SQL except: " + 
                   (sql==null?"":sql.toString()) + " " + name);         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new DboxException("PackageInfo: remProjAcl:>> SQLError deleting acl "
                                 + name + " from package " + getPackageId(), 
                                 DboxException.SQL_ERROR); 
      } finally {
         if (pstmt != null) try {pstmt.close();} catch(SQLException e) {}
         DbConnect.returnConnection(connection);
      }   
   }
         
         
   void addGroupAcl(String name) throws DboxException {
		  
     
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;	
      StringBuffer sql =null;
      try{
			
         connection=DbConnect.makeSharedConn();
         
         packageManager.lockpackage(getPackageId());
         
         sql=new StringBuffer ("SELECT USERTYPE FROM EDESIGN.PKGACL pacl WHERE pacl.PKGID=? ");
         sql.append ("AND pacl.USERID=? AND pacl.USERTYPE=? AND pacl.DELETED is null");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1,getPackageId());
         pstmt.setString(2, name);
         pstmt.setByte(3, DropboxGenerator.STATUS_GROUP);
         
         rs=DbConnect.executeQuery(pstmt);
			
         if (rs.next()){
           //throw new DboxException("addGroupAcl: Package " + 
           //                       getPackageId() + " already contains group "
           //                        + name, 0);
            return;
         }
         
         rs.close();       rs = null;
         pstmt.close(); pstmt = null;
         
         sql = new StringBuffer("INSERT INTO EDESIGN.PKGACL (PKGID, USERID, USERTYPE) VALUES (?,?,?)");
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1, getPackageId());
         pstmt.setString(2, name);
         pstmt.setByte(3, DropboxGenerator.STATUS_GROUP);
         
         if (DbConnect.executeUpdate(pstmt) < 1) {
            throw new DboxException("addGroupAcl:>> Package " + 
                                    getPackageId() + 
                                    ": error adding group acl "
                                    + name, 0);
         }
         
         connection.commit();
         
      } catch (SQLException e) {
         log.error("DB2PI.addGroupAcl: SQL except: " + 
                   (sql==null?"":sql.toString()) + " : " + name);         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new DboxException("addGroupAcl: Error:>> SQL error adding ACL for Package " + 
                                 getPackageId() + " " + name + " => " + 
                                 e.toString(), DboxException.SQL_ERROR);
      } finally {
         if (pstmt != null) try {pstmt.close();} catch(SQLException e) {}
         DbConnect.returnConnection(connection);
      }   
   }
	  
   void removeGroupAcl(String name) throws DboxException {
		  
     
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;	
      StringBuffer sql =null;
      try{
			
         connection=DbConnect.makeSharedConn();
         
         packageManager.lockpackage(getPackageId());
         
         sql = new StringBuffer("DELETE FROM EDESIGN.PKGACL pacl WHERE pacl.PKGID=? AND pacl.USERID=? AND pacl.USERTYPE=? AND pacl.DELETED is null");
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1,getPackageId());
         pstmt.setString(2, name);
         pstmt.setByte(3, DropboxGenerator.STATUS_GROUP);
         
         if (DbConnect.executeUpdate(pstmt) < 1) {
            throw new DboxException("removeGroupAcl:>> Package " + 
                                    getPackageId() + " does not contain group "
                                    + name, 0);
         }
				
         connection.commit();
                                
      } catch (SQLException e) {
         log.error("DB2PI.remGroupAcl: SQL except: " + 
                   (sql==null?"":sql.toString()) + " : " + name);         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new DboxException("removeGroupAcl:>> SQL error removing group ACL: Package " + 
                                 getPackageId() + " " + name + " => " + 
                                 e.toString(), DboxException.SQL_ERROR);
      } finally {
         if (pstmt != null) try {pstmt.close();} catch(SQLException e) {}
         DbConnect.returnConnection(connection);
      }   
   }
   
   void addUserAcl(String name) throws DboxException {
		  
     
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;	
      StringBuffer sql =null;
      try{
			
         connection=DbConnect.makeSharedConn();
         
         packageManager.lockpackage(getPackageId());
         
         sql = new StringBuffer("SELECT USERID FROM EDESIGN.PKGACL pacl WHERE ");
         sql.append("pacl.PKGID=? AND pacl.USERID=? AND pacl.USERTYPE=? AND pacl.DELETED is null");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1,getPackageId());
         pstmt.setString(2, name);
         pstmt.setByte(3, DropboxGenerator.STATUS_NONE);
         
         rs=DbConnect.executeQuery(pstmt);
			
         if (rs.next()){
           //throw new DboxException("addUserAcl: Package " + 
           //                        getPackageId() + " already contains user "
           //                        + name, 0);
            rs.close();    rs    = null;
            pstmt.close(); pstmt = null;
            return;
         }
         else{
            sql = new StringBuffer("INSERT INTO EDESIGN.PKGACL (PKGID, USERID, USERTYPE) VALUES (?,?,?)");
            pstmt=connection.prepareStatement(sql.toString());
            pstmt.setLong(1, getPackageId());
            pstmt.setString(2, name);
            pstmt.setByte(3, DropboxGenerator.STATUS_NONE);
            
            if (DbConnect.executeUpdate(pstmt) < 1) {
               throw new DboxException("addUserAcl:>> Error adding user ACL: Package " + 
                                       getPackageId() + 
                                       ": error adding acl "
                                       + name, 0);
            }           
         }
			
         connection.commit();
                        
      } catch (SQLException e) {
         log.error("DB2PI.addUserAcl: SQL except: " + 
                   (sql==null?"":sql.toString()) + " : " + name);         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new DboxException("addUserAcl: Error:>> SQL error adding user ACL for Package " + 
                                 getPackageId() + " " + name + " => " + 
                                 e.toString(), DboxException.SQL_ERROR);
      } finally {
         if (pstmt != null) try {pstmt.close();} catch(SQLException e) {}
         DbConnect.returnConnection(connection);
      }   
   }
		  
   void removeUserAcl(String name) throws DboxException {
		  
     
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;	
      StringBuffer sql =null;
      try{
			
         connection=DbConnect.makeSharedConn();
         
         packageManager.lockpackage(getPackageId());
         
         sql=new StringBuffer("DELETE FROM EDESIGN.PKGACL pacl WHERE pacl.PKGID=? AND pacl.USERID=? AND pacl.USERTYPE=? AND pacl.DELETED is null");
         pstmt=connection.prepareStatement(sql.toString ());
         pstmt.setLong(1,getPackageId());
         pstmt.setString(2, name);
         pstmt.setByte(3, DropboxGenerator.STATUS_NONE);
            
         if (DbConnect.executeUpdate(pstmt) < 1) {
            throw new DboxException("removeUserAcl:>> Package " + 
                                    getPackageId() + " does not contain user "
                                    + name, 0);
         }
				
         connection.commit();
                                
      } catch (SQLException e) {
         log.error("DB2PI.remUserAcl: SQL except: " + 
                   (sql==null?"":sql.toString()) + " : " + name);         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new DboxException("remUserAcl: Error:>> SQL error remving acl from Package " + 
                                 getPackageId() + " " + name + " => " + 
                                 e.toString(), DboxException.SQL_ERROR);
      } finally {
         if (pstmt != null) try {pstmt.close();} catch(SQLException e) {}
         DbConnect.returnConnection(connection);
      }   
   }
      
   Vector getFileAcls(long fileid) throws DboxException {
     
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;	
      StringBuffer sql = null;
      try{	
         connection=DbConnect.makeSharedConn();
         sql = new StringBuffer ("SELECT FILEID FROM EDESIGN.FILETOPKG fp ");
         sql.append("WHERE fp.PKGID=? AND fp.FILEID=? AND fp.DELETED is null");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1,getPackageId());
         pstmt.setLong(2, fileid);
         
         rs=DbConnect.executeQuery(pstmt);
				
         if (!rs.next())
            throw new DboxException("getFileAcls:>> Specified file " + fileid +
                                    " does not exist in package", 0);
			
         connection.commit();
                        
      } catch (SQLException e) {
         log.error("DB2PI.getFileAcls: SQL except: " + 
                   (sql==null?"":sql.toString()) + " : " + fileid);         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new DboxException("getFileAcls: Error:>> SQL error getting fileacls for Package " + 
                                 getPackageId() + " " + fileid + " => " + 
                                 e.toString(), DboxException.SQL_ERROR);
      } finally {
         if (pstmt != null) try {pstmt.close();} catch(SQLException e) {}
         DbConnect.returnConnection(connection);
      }   
      
      DB2FileAccess dbfileAccess = new DB2FileAccess(getPackageId());
      return dbfileAccess.accessAclsForFile(fileid);
   }
      
   void removeFileAccessRecord(User user, long fileid) 
      throws DboxException {
      
      DB2FileAccess dbfileAccess = new DB2FileAccess(getPackageId());
      dbfileAccess.remove(user, fileid);
   }
   
   AclInfo getFileAccessRecord(String user, long fileid) 
      throws DboxException {
      
      DB2FileAccess dbfileAccess = new DB2FileAccess(getPackageId());
      return dbfileAccess.fileAccessedBy(fileid, user);
   }
   
   void addFileAccessRecord(User user, long fileid, 
                            byte status, int xferate) 
      throws DboxException {
         
     
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;	
      StringBuffer sql = null;
      try{	
         if (fileid != 0) {
            connection=DbConnect.makeSharedConn();
            
            sql=new StringBuffer("SELECT FILEID FROM EDESIGN.FILETOPKG fp ");
            sql.append("WHERE fp.PKGID=? AND fp.FILEID=? AND fp.DELETED is null");
            
           // ISOLATION
            sql.append(DB2PackageManager.getROUR());
            
            pstmt=connection.prepareStatement(sql.toString());
            pstmt.setLong(1,getPackageId());
            pstmt.setLong(2, fileid);
            
            rs=DbConnect.executeQuery(pstmt);
            
            if (!rs.next())
               throw new DboxException("getFileAcls:>> Specified file " +
                                       fileid + " does not exist in package", 0);
         }
         
         AclInfo aclinfo = getUserAccess(user, true);
         aclinfo.setAclStatus(status);
         aclinfo.setXferRate(xferate);
         
         DB2FileAccess dbfileAccess = new DB2FileAccess(getPackageId());
         dbfileAccess.add(fileid, aclinfo, user);
         
        // Only commit if we actually allocated a connection here
         if (fileid != 0) connection.commit();
         
      } catch (SQLException e) {
         log.error("DB2PI.addFileAccessRecs: SQL except: " + 
                   (sql==null?"":sql.toString()) + " : " + fileid);         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new DboxException("getFileAccessRecs: Error:>> SQL error adding file access record for Package " + 
                                 getPackageId() + " " + fileid + " => " + 
                                 e.toString(), DboxException.SQL_ERROR);
      } finally {
         if (pstmt != null) try {pstmt.close();} catch(SQLException e) {}
         DbConnect.returnConnection(connection);
      }   
   }
   
   Vector getPackageAcls(boolean staticOnly) 
      throws DboxException {
      
      Vector ret = new Vector();
         
      boolean flag=false;
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;	
      StringBuffer sql = null;
	  
      try{	
         Vector vec = null;  
         if (!staticOnly){
            DB2FileAccess dbfileAccess = new DB2FileAccess(getPackageId());
            vec = dbfileAccess.accessSummary(getPackageNumElements());
            Enumeration enum = vec.elements();
            while(enum.hasMoreElements()) {
               AclInfo aclinfo = (AclInfo)enum.nextElement();
               aclinfo = new AclInfo(aclinfo);
               ret.addElement(aclinfo);
            }
         }
         else
            vec = new Vector();
         connection=DbConnect.makeSharedConn();
         sql=new StringBuffer("SELECT USERTYPE, USERID, COMPANY FROM EDESIGN.PKGACL pacl WHERE pacl.PKGID=? AND pacl.DELETED is null");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString ());
         
         pstmt.setLong(1, getPackageId());
         
         rs=DbConnect.executeQuery(pstmt);
				
         while (rs.next()){
				
            AclInfo aclinfo = new AclInfo();
            
            flag = false;
            
           // Add all project and groups records
            byte acltype = rs.getByte(1);
            if (acltype == DropboxGenerator.STATUS_PROJECT ||
                acltype == DropboxGenerator.STATUS_GROUP) {
                
               aclinfo.setAclStatus(acltype);
               aclinfo.setAclProjectName(rs.getString(2));
               aclinfo.setAclName(rs.getString(2));
               ret.addElement (aclinfo);
            } else {//filter the useracls which are already in ret
               if (!staticOnly){
                  Enumeration tenum = ret.elements();
                  String name = rs.getString(2);
                  while(tenum.hasMoreElements()){
                     AclInfo taclinfo = (AclInfo)tenum.nextElement ();
                     if (taclinfo.getAclName().equals(name)) {
                        flag=true;
                        break;
                     }
                  }
               }
                  
               if(!flag){
                  aclinfo.setAclStatus(DropboxGenerator.STATUS_NONE );
                  aclinfo.setAclName(rs.getString(2));
                  aclinfo.setAclCompany(rs.getString(3));
                  ret.addElement(aclinfo);
               }
            }
         }
         
         connection.commit();
         
      } catch (SQLException e) {
         log.error("DB2PI.getPackageAcls: SQL except: " + 
                   (sql==null?"":sql.toString()) + " : ");         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new DboxException("getPackageAcls: Error:>> SQL error getting paclage acls for Package " + 
                                 getPackageId() + " => " + 
                                 e.toString(), DboxException.SQL_ERROR);
      } finally {
         if (pstmt != null) try {pstmt.close();} catch(SQLException e) {}
         DbConnect.returnConnection(connection);
      }   

      return ret;
   }
   
  // This would be a cycle, but DB2PackageManager actually implements the
  //  canAccessPackage method (does not call getUserAccess on PI)
   public boolean canAccessPackage(User user, boolean includeOwner) {
      boolean ret = false;
      try {
         ret =  packageManager.canAccessPackage(user, getPackageId(),
                                                        includeOwner);
      } catch(DboxException db2) {
      }
      return ret;
   }
      
   public AclInfo getUserAccess(User user, boolean includeOwner) 
      throws DboxException {
         
      AclInfo ret = null;
      String n = user.getName();
         
     
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;	
      StringBuffer sql=null;
		
      try {	
      
         if (includeOwner && 
             (n.equals(getPackageOwner()) ||
              (packageManager.getPrivilegeLevel(n) >= 
               packageManager.PRIVILEGE_SUPER_USER))) {
            ret=new AclInfo();
            ret.setAclName(n);
            ret.setAclProjectName("");
            ret.setAclStatus(DropboxGenerator.STATUS_NONE);
            return ret;
         }
      
         if (!packageManager.canAccessPackage(user, getPackageId(), false)) {
            throw new DboxException("PackageInfo: getUserAccess:>> User " + 
                                    user.getName() + 
                                    "does not have access to package " + 
                                    getPackageId(), 0);
         }
         
         connection=DbConnect.makeSharedConn();
         
         sql = 
            new StringBuffer("SELECT USERID, USERTYPE FROM EDESIGN.PKGACL pacl ");
         sql.append(" WHERE pacl.pkgid = ? AND pacl.DELETED is null ");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1,getPackageId());
         
         rs=DbConnect.executeQuery(pstmt);
         
         Hashtable groups = null;
         
        // FIXNESTED FIXNESTED FIXNESTED          
         while (rs.next()) {
            String userid   = rs.getString(1);
            byte   usertype = rs.getByte(2);
            if (ret == null || usertype == DropboxGenerator.STATUS_NONE) {
               boolean keepit = false;
               if (usertype == DropboxGenerator.STATUS_PROJECT) { 
                  Vector projs = user.getProjects();
                  if (projs.contains(userid)) {
                     keepit = true;
                  }
               } else if (usertype == DropboxGenerator.STATUS_GROUP) { 
               
                 // Get hash of all groups this guy is a member of
                  if (groups == null) {
                     groups = 
                        packageManager.getMatchingGroups(user, null,
                                                         false, false, false,
                                                         true, false, false,
                                                         false, false, false);
                  }
                  if (groups.get(userid) != null) {
                     keepit = true;
                  }
               } else if (userid.equals(user.getName()) ||
                          userid.equals("*")) {
                  keepit = true;
               }
               
               if (keepit) {
                  ret=new AclInfo();
                  ret.setAclName(user.getName());
                  ret.setAclStatus(usertype);
                  if (usertype != DropboxGenerator.STATUS_NONE) {
                     ret.setAclProjectName(userid);
                  } else {
                     ret.setAclProjectName("");
                     break;
                  }
               }
            }
         }
         
         if (ret == null) {
            throw new DboxException("PackageInfo: getUserAccess:>> User " + 
                                    user.getName() + 
                                    "does not have access to package " + 
                                    getPackageId(), 0);
         }
			
         connection.commit();
                        
      } catch (SQLException e) {
         log.error("DB2PI.getUserAccess: SQL except: " + 
                   (sql==null?"":sql.toString()) + " : " + user.getName()); 
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new DboxException("PackageInfo: getUserAccess:>> SQLEx " + 
                                 user.getName() + 
                                 " so not sure if has access to package " + 
                                 getPackageId() + " => " + e.toString(), 
                                 DboxException.SQL_ERROR);
      } finally {
         if (pstmt != null) try {pstmt.close();} catch(SQLException e) {}
         DbConnect.returnConnection(connection);
      }   
       
      return ret;
   }
      
   public String toString() {
      String ret = super.toString() + "FIX DB2TOSTING!";
	  
	 
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null, rs2=null;	
      StringBuffer sql=null;
      
/* FIX TODO		
      ret+=Nester.nest("\n---------Files---------\n");
      try{	
         connection=DbConnect.makeSharedConn();

         sql = new StringBuffer("SELECT * FROM EDESIGN.FILETOPKG fp ");
         sql.append("WHERE fp.PKGID=?");
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1,getPackageId());
         rs=DbConnect.executeQuery(pstmt);
         while (rs.next()){
            sql = new StringBuffer("SELECT FILEID, FILENAME, FILESTAT, xxxFILESIZE, INTENDEDSIZE, xxxCOMPONENTENTRY ");
            sql.append("FROM EDESIGN.FILE f WHERE f.FILEID=?");
            pstmt=connection.prepareStatement(sql.toString());
            pstmt.setLong(1,rs.getLong(1));
            rs2=DbConnect.executeQuery(pstmt);
            if (rs2.next()){
               StringBuffer out = new StringBuffer(rs2.getLong(1)+" "+rs2.getString(2)+" "+rs2.getByte(3));
               out.append(" "+rs2.getLong(4)+" "+rs2.getLong(5)+" "+rs2.getInt(6));
               ret+=Nester.nest("\n"+out.toString());
            }
         }
         ret += Nester.nest("\n------------PKGACLS------------------------\n");
         sql = new StringBuffer("SELECT USERID, USERTYPE FROM EDESIGN.PKGACL pacl ");
         sql.append("WHERE pacl.PKGID=?");
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1,getPackageId());
         rs=DbConnect.executeQuery(pstmt);
         while (rs.next()){
            StringBuffer out = new StringBuffer(rs.getString(1)+" "+rs.getString(2));
            ret+=Nester.nest("\n"+out.toString());
            
         }
		 
         connection.commit();
		
      }catch (SQLException e){
         DbConnect.destroyConnection(connection);
         connection=null;
         e.printStackTrace();
         DboxAlert.alert(e);  // Generic Alert
			
      }finally {
         DbConnect.returnConnection(c onnection);
      }
*/   
      return ret;
   }
}
