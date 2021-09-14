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

public class DB2DboxFileComponent extends DboxFileComponent implements Rollback {
      
   DB2DboxFileComponent(FileManager fm, long fid, 
                        long filesize, long intendedSize, 
                        long startingOfs, String filename) {
      super(fm, fid, filesize, 
            intendedSize, startingOfs, 
            filename);
   }
      
  // From Rollback interface ... if called, try to return CompIntendedSize to repos
   public void execute() {
      try {
         log.warn("Attempting to return component space ... Rollback.execture called");
         returnSpace();
         log.warn("Successfully returned component space: " + toString());
      } catch (Exception ee) {
         DboxAlert.alert(DboxAlert.SEV2, 
                         "Error returning Component space ", 
                         0, 
                         "Rollback interface on FileComp called, error:\n" + toString(), 
                         ee);
      }
   }
   
  // From Rollback interface ... Noop 
   public void clear() {
   }
      
   public OutputStream makeOutputStream() throws IOException {
      OutputStream os = null;
      if (filename == null) {
         os = super.makeOutputStream();
         try {
            setFileName(filename);
         } catch(DboxException dbe) {
            os.close();
            os = null;
            throw new IOException("Error setting filename for component");
         }
      } else {
         os = super.makeOutputStream();
      }
      return os;
   }
      
  // Non service routine ... Deprecated!
   public void complete() throws DboxException {
      super.complete();
      
      boolean maindone = false;
      
     //update the component size in DB2
 
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      StringBuffer sql = null;
      
      long curtime = new java.util.Date().getTime();
      long delttime = ((curtime - starttime));
      int xferate = -1;
      if (delttime > 0) {
         xferate = (int)(((size*1000)/delttime));
      }
      
      try {
      
         connection = DbConnect.makeSharedConn();
         
        // Do any locking we need for this component
         fileManager.lockfile(fileid);
         
         sql = new StringBuffer("UPDATE EDESIGN.FILECOMPONENT fc SET fc.COMPONENTSIZE=?,fc.XFERSPEED=?,fc.MD5BLOB=?,fc.COMPINTENDEDSIZE=? WHERE fc.DELETED is null AND fc.FILEID=? AND fc.STARTOFS=?");
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1, size);
         pstmt.setInt (2, xferate);
         
         byte dataAsByteArray[]=null;
         try {
            dataAsByteArray = compMD5.stateToBytes();
         } catch (Exception ee) {
            log.error("DB2DboxFileComp.complete: IO exception while setting blob");
            
           //need to put alert
            if (!maindone) {
               throw new 
                  DboxException("DB2DboxFileComp:>> IOException Completing component",
                                DboxException.GENERAL_ERROR);
            }
         }
        
         pstmt.setBytes(3,dataAsByteArray); 
         
        // Set the component intended size to equal the size. We already gave the
        //  extra space (if any) back to Allocator in our Super method. Super also
        //  set the intendedsize equal to the size in the instance object.
         pstmt.setLong(4, size);
         
         pstmt.setLong(5, fileid);
         pstmt.setLong(6, getStartingOffset());
         if (DbConnect.executeUpdate(pstmt) != 1) {
            throw new DboxException("FileComponent:>> DB Error during complete",
                                    0);
         }
         
         maindone = true;
         
        //
        // Now, keep Filesize/componententry up to date for reporting. We used 
        //  to do this work in the triggers, but had deadlocking problems. Now
        //  the values MAY not be correct, but good enough for reporting. For
        //  the most part, the data SHOULD always be correct, but there is a
        //  window between the query and the update (on purpose, to avoid the
        //  deadlock). Should only be one dropbox instance modifying the
        //  components, and only one thread completing things at a time.
        //
        // If we successfully update the filesize, move on to the package.
        //
         pstmt.close();

         sql = new StringBuffer("SELECT VALUE(SUM(fc.componentsize), 0) as csize, VALUE(COUNT(*), 0) as numcomps from EDESIGN.FILECOMPONENT fc where fc.FILEID = ? AND fc.DELETED is null");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1, fileid);
            
        // Should always get one next
         rs = DbConnect.executeQuery(pstmt);
         
         if (rs.next()) {
            long csize   = rs.getLong(1);
            int  numcomp = rs.getInt(2);
            
            pstmt.close();
            sql = new StringBuffer("UPDATE EDESIGN.FILE f SET f.FILESIZE=?,f.COMPONENTENTRY=? WHERE f.FILEID=? AND f.DELETED is null");
            pstmt=connection.prepareStatement(sql.toString());
            pstmt.setLong(1, csize);
            pstmt.setInt (2, numcomp);
            pstmt.setLong(3, fileid);
            
            if (DbConnect.executeUpdate(pstmt) == 1) {
         
               pstmt.close();
               
              // Get the one and only package that should be ref-ing this file
               sql = new StringBuffer("SELECT p.pkgid, VALUE(SUM(f.filesize), 0), VALUE(COUNT(distinct f.fileid), 0) from EDESIGN.FILETOPKG fp, EDESIGN.PACKAGE p, EDESIGN.FILE f where fp.FILEID=? AND p.PKGID = fp.PKGID AND f.FILEID = ? AND p.DELETED is null and fp.DELETED is NULL and f.DELETED is NULL GROUP BY p.pkgid ");
               
              // ISOLATION
               sql.append(DB2PackageManager.getROUR());
               
               pstmt=connection.prepareStatement(sql.toString());
               pstmt.setLong(1, fileid);
               pstmt.setLong(2, fileid);
               
              // Should always get one next
               rs = DbConnect.executeQuery(pstmt);
               if (rs.next()) {
                  long pkgid    = rs.getLong(1);
                  long pkgsize  = rs.getLong(2);
                  int  numfiles = rs.getInt(3);
                  
                  pstmt.close();
                  sql = new StringBuffer("UPDATE EDESIGN.PACKAGE SET PKGSIZE=?,FILEENTRY=? WHERE PKGID=? AND DELETED is null");
                  pstmt=connection.prepareStatement(sql.toString());
                  pstmt.setLong(1, pkgsize);
                  pstmt.setInt (2, numfiles);
                  pstmt.setLong(3, pkgid);
                  
                  if (DbConnect.executeUpdate(pstmt) != 1) {
                     DboxAlert.alert(DboxAlert.SEV3, 
                                     "executeUpdate != " + 1, 
                                     0, 
                                     "update package stats for pkg : " + 
                                     pkgid + " failed");
                  }
               }
            }
         }
         
         connection.commit();
         
      } catch (SQLException e) {
         log.error("DB2DboxFileComp.complete: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null;
         pstmt=null;
         
         DboxAlert.alert(e);  // Generic Alert
         
         throw new 
            DboxException("DB2DboxFileComp:>> sql failure Completing component",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);   // will auto-rollback
      } 
   }
      
   public void setFileName(String name) throws DboxException {
		  
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      SharedConnection connection=null;
      StringBuffer sql = null;
      try{
         connection=DbConnect.makeSharedConn();
         fileManager.lockfile(fileid);
         sql = new StringBuffer ("UPDATE EDESIGN.FILECOMPONENT fc SET fc.FILENAME=? ");
         sql.append("WHERE fc.FILEID=? AND fc.STARTOFS=? AND fc.DELETED is null");
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setString(1, name);
         pstmt.setLong(2, fileid);
         pstmt.setLong(3, getStartingOffset());
            
         if (DbConnect.executeUpdate(pstmt) != 1) {
            throw new 
               DboxException("FileComponent:>> DB Error during setFileName",
                             0);
         }
         
         connection.commit();
			
      } catch (SQLException e) {
         log.error("DB2DboxFileComp.setFileName: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null;
         pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2DboxFileComp:>> sql failure setting Filename: " +
                          name,
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);   // will Auto rollback
      } 
   }
   
   public void forceSetIntendedSize(long sz) throws DboxException {
      super.setFileIntendedSize(sz);
		  
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      SharedConnection connection=null;
      StringBuffer sql = null;
      try{
         connection=DbConnect.makeSharedConn();
         fileManager.lockfile(fileid);
         sql = new StringBuffer ("UPDATE EDESIGN.FILECOMPONENT fc SET fc.COMPINTENDEDSIZE=? ");
         sql.append("WHERE fc.FILEID=? AND fc.STARTOFS=? AND fc.DELETED is null");
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1, sz);
         pstmt.setLong(2, fileid);
         pstmt.setLong(3, getStartingOffset());
            
         if (DbConnect.executeUpdate(pstmt) != 1) {
            throw new 
               DboxException("FileComponent:>> DB Error during setFileSize",
                             0);
         }
         connection.commit();
      } catch (SQLException e) {
         log.error("DB2DboxFileComp.setIntendedSize: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null;
         pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2DboxFileComp:>> sql failure setting IntendedFileSize: " +
                          sz,
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);  // Will auto rollback
      } 
   }
   
   public void forceSetFileSize(long sz) throws DboxException {
      super.setFileSize(sz);
		  
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      SharedConnection connection=null;
      StringBuffer sql = null;
      try{
         connection=DbConnect.makeSharedConn();
         fileManager.lockfile(fileid);
         sql = new StringBuffer ("UPDATE EDESIGN.FILECOMPONENT fc SET fc.COMPONENTSIZE=? ");
         sql.append("WHERE fc.FILEID=? AND fc.STARTOFS=? AND fc.DELETED is null");
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1, sz);
         pstmt.setLong(2, fileid);
         pstmt.setLong(3, getStartingOffset());
            
         if (DbConnect.executeUpdate(pstmt) != 1) {
            throw new 
               DboxException("FileComponent:>> DB Error during setFileSize",
                             0);
         }
         connection.commit();
      } catch (SQLException e) {
         log.error("DB2DboxFileComp.setFileSize: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null;
         pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2DboxFileComp:>> sql failure setting FileSize: " +
                          sz,
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);  // Will auto rollback
      } 
   }
   
   public void forceSetFileMD5Object(MessageDigestI v) throws DboxException {
   
      super.setFileMD5Object(v);
      
      PreparedStatement pstmt =null;
      ResultSet rs=null;
      StringBuffer sql = null;
      SharedConnection connection=null;
      try{
			
         byte blobbytes[] = null;
         if (v != null) {
            try {
               blobbytes = v.stateToBytes();
            } catch(Exception e) {
               throw new DboxException("Error getting state from bytes", e);
            }
         }
         connection=DbConnect.makeSharedConn();
         
         fileManager.lockfile(fileid);
         
         sql = new 
            StringBuffer("UPDATE EDESIGN.FILECOMPONENT fc SET (fc.MD5BLOB) = (?) WHERE fc.FILEID=? AND fc.STARTOFS = ? AND fc.DELETED is null");
         pstmt=connection.prepareStatement(sql.toString()); 
         pstmt.setBytes (1, blobbytes);
         pstmt.setLong  (2, getFileId());
         pstmt.setLong  (3, getStartingOffset());
         
         if (DbConnect.executeUpdate(pstmt) != 1) { 
            throw new 
               DboxException("DB problem setting MD5 on file. 0|2+ matches", 
                             0);
         }
         connection.commit();
      } catch (SQLException e){
         log.error("DB2DboxFileComp:forceSetFileMD5Object: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2DboxFileComp:forceSetFileMD5Object:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);   // will auto rollback
      }      
   }
   
   public void forceSetSizeAndMD5Object(long sz, 
                                        MessageDigestI v) throws DboxException {
      
      setFileSize(sz);
      setFileMD5Object(v);
      
      PreparedStatement pstmt =null;
      ResultSet rs=null;
      StringBuffer sql = null;
      SharedConnection connection=null;
      try{
			
         byte blobbytes[] = null;
         if (v != null) {
            try {
               blobbytes = v.stateToBytes();
            } catch(Exception e) {
               throw new DboxException("Error getting state from bytes", e);
            }
         }
         
         connection=DbConnect.makeSharedConn();
         
         fileManager.lockfile(fileid);
         
         sql = new 
            StringBuffer("UPDATE EDESIGN.FILECOMPONENT fc SET (COMPONENTSIZE,MD5BLOB) = (?,?) WHERE fc.FILEID=? AND fc.STARTOFS = ? AND fc.DELETED is null");
         pstmt=connection.prepareStatement(sql.toString()); 
         pstmt.setLong  (1, sz);
         pstmt.setBytes (2, blobbytes);
         pstmt.setLong  (3, getFileId());
         pstmt.setLong  (4, getStartingOffset());
        
         if (DbConnect.executeUpdate(pstmt) != 1) { 
            throw new 
               DboxException("DB problem setting MD5 on fileComp. 0|2+ matches", 
                             0);
         }
         
         connection.commit();
         
      } catch (SQLException e){
         log.error("DB2DboxFileComp:forceSetSizeMD5Object: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2DboxFileComp:forceSetSizeMD5Object:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
      
   }
}
