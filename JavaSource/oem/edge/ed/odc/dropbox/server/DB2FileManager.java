package oem.edge.ed.odc.dropbox.server;

import  oem.edge.ed.odc.dropbox.common.*;
import  oem.edge.ed.odc.ftp.common.*;
import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.dsmp.server.*;
import  oem.edge.ed.odc.util.*;
import  oem.edge.ed.util.SearchEtc;
import oem.edge.ed.odc.util.db.SharedConnectionProxy;
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

/*
 * Note, Care must be taken to avoid "NESTED result sets", where a connection
 *  rollback could occur. The SharedConnection code uses SavePoints to support
 *  light weight transactions, but WAS connection pooling code closes all 
 *  result sets when a savepoint rollback occurs. Totally bocus man!  So, 
 *  cases to avoid are calling a routine which uses SharedConnection. Having 
 *  locally nested result sets is fine if they live in the same savepoint
 *  region (which they should)
 */
public class DB2FileManager extends FileManager {  
	   
   private static Logger log = Logger.getLogger(DB2FileManager.class.getName());
      
   public DB2FileManager(PackageManager pm,
                         DboxFileAllocator fa) { 
      super(pm, fa);
   }
   
   public boolean lockfile(long packid, long fileid) throws DboxException {
   
      if (packid == -1 && fileid == -1) return false;
   
      DboxFileInfo info=null;		 
      PreparedStatement pstmt =null;
      SharedConnection connection=null;
      StringBuffer sql = null;
      ResultSet rs = null;
      
      boolean ret = false;
      	
      try{
         connection=DbConnect.makeSharedConn();
         
        // The FOR UPDATE WITH RS will ensure that others coming thru here will
        //  be stopped with the update lock as well. WITH RS means locking the 
        //  row(s) in question for the entire transaction
         if (packid != -1) {
            sql = new StringBuffer ("SELECT pkgid, pkgsize from edesign.package where pkgid = ? and DELETED IS NULL ");
            sql.append(update_lock_clause);
            pstmt=connection.prepareStatement(sql.toString()); 
            pstmt.setLong(1,packid);
			
            rs = DbConnect.executeQuery(pstmt);
            
            ret=rs.next();
            
            log.debug("    Package lock " + (ret?"WORKED":"FAILED"));
            
            connection.addStatementToParentFrame(pstmt);
            pstmt = null;
            
           // If error getting this part of lock ... return with error
            if (!ret) return ret;
         }
         
         if (fileid != -1) {
            sql = new StringBuffer ("SELECT fileid, filesize from edesign.file where fileid = ? and DELETED IS NULL ");
            sql.append(update_lock_clause);
            
            pstmt=connection.prepareStatement(sql.toString()); 
            pstmt.setLong(1,fileid);
			
            rs = DbConnect.executeQuery(pstmt);
            
            ret=rs.next();
            
            log.debug("    File lock " + (ret?"WORKED":"FAILED"));
            
           // We don't close the statement, instead, we register it with our
           //  parent frame. When that parent frame is popped, the
           //  statement will be closed
            connection.addStatementToParentFrame(pstmt);
            pstmt = null;
            
           // If error getting this part of lock ... return with error
            if (!ret) return ret;
         }
         
        // This will NOT unlock the pack/file if our caller already allocated a
        //  sharedconnection (which she SHOULD)
         connection.commit();
         
         return true;
         
      } catch (SQLException e) {
         log.error("DB2FM.lockfile/package: SQL except doing: " + 
                   (sql==null?"":sql.toString()) + " " + fileid);
         DboxAlert.alert(e);  // Generic Alert
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         throw new 
            DboxException("DB2FM.lockfile/package:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
   
  // We were blowing past the size limits for SQL thru JDBC (complaining running 
  // out of heap)
  //
  // Rather than rework the real routine, just call it multiple times. It only 
  // processes at most, 50 Fileids at a time
   public int cleanUnreferencedFiles() throws DboxException {
      int ret = 0;
      int lint;
      
      while((lint=cleanUnreferencedFilesInt()) > 0) {
         ret += lint;
      } 

      return ret;
   }
   
  // We were blowing past the size limits for SQL thru JDBC (complaining running 
  // out of heap)
  //
  // Rather than rework the real routine, just call it multiple times. It only 
  // processes at most, 50 Fileids at a time
   public int cleanUnreferencedFiles(int tot) throws DboxException {
      int ret = 0;
      int lint;
      
      while((tot < 0 || ret < tot) && (lint=cleanUnreferencedFilesInt()) > 0) {
         ret += lint;
      } 

      return ret;
   }
   
  // Do collection of File info lockstock, but do the delete of the filetable 
  // entries one at a time. If it works (executeUpdate == 1) then go ahead with the rest,
  // otherwise, bag it (another delete instance is probably at work).  This parallels the
  // workings of DB2DboxFileInfo ... lock the file row, then touch all other pertinent rows.
  // That is, file lock is the gate keeper.  We run the risk of increased delete times
  // as we have many more transactions that we are running. And all this so we can have
  // the inline deletes work correctly ... that is where we are going now. If the 
  // performance stinks, then revisit. 
  //
  // Note we COULD deadlock if there is an inline (non-cron) clean going on where they
  //  have a non-referenced File row locked as the inline caller. Only so much we can do.
  
  // Flush returnspace every 100 Meg or so
   static final long RETURNSPACE_FLUSH_THRESHOLD = 100*1024*1024;
  
   protected int cleanUnreferencedFilesInt() throws DboxException {
      PreparedStatement pstmt1 = null;
      ResultSet rs1=null;
      SharedConnection connection=null;
      SharedConnectionProxy savedconnection=null;
      int num = 0;
      StringBuffer sql = null;
      StringBuffer exitalerts = null;
      
      long returnspaceTotal = 0;
      
      Hashtable returnSpace = new Hashtable();
      
      long st = System.currentTimeMillis();
      long frontime1 = st;
      long frontime2 = st;
      try {
      
        // We want this action to be disconnected from any current transaction
        //  so commit MEANS commit now!
         savedconnection=DbConnect.saveSharedConn();
         connection=DbConnect.makeSharedConn();
         
        // Only take those files which are not referenced AND which have been 
        // around at least 1 minute (to close hole where file is created, but not
        // yet in filetopkg table)
         sql = new StringBuffer("SELECT FILEID,FILENAME,FILESTAT,");
         
        // FSZCHANGE Usedto have filesize now calculate it
        // JMC 7/29/04 - Ok, now do it if we say we trust the encoded sizes. 
        //               This one REALLY does not matter, cause we don't use 
        //               the filesize value anyway
         if (DB2PackageManager.getTrustSizes()) {
            sql.append("FILESIZE,");
         } else {
            sql.append("VALUE((SELECT SUM(fc.COMPONENTSIZE) from EDESIGN.FILECOMPONENT fc where f.FILEID=fc.FILEID AND fc.DELETED is null),0),");
         }
         
         sql.append("INTENDEDSIZE,MD5,POOLID,XFERSPEED FROM EDESIGN.FILE f WHERE f.DELETED is null AND FILEID NOT IN (SELECT fp.FILEID FROM EDESIGN.FILETOPKG fp where fp.DELETED is null) AND TIMESTAMPDIFF(4,char(CURRENT TIMESTAMP - CREATED)) >= 1 FETCH FIRST 50 ROWS ONLY");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
            
         pstmt1=connection.prepareStatement(sql.toString()); 
         
         rs1=DbConnect.executeQuery(pstmt1);
         
         
        //
        // We now will iterate over all Files which are expired, and build a query
        // get all the fileids along with pertinent component info
        //
        
        //
        // FLASH JMC 6/19/06 - Changeto/addin COMPINTENDEDSIZE, as that is 
        //                     what is correct for WebServices when returning space
        //
         sql = new StringBuffer("SELECT DISTINCT f.FILEID, fc.STARTOFS, fc.FILENAME, fc.COMPONENTSIZE, fc.COMPINTENDEDSIZE FROM EDESIGN.FILE f, EDESIGN.FILECOMPONENT fc WHERE f.FILEID=fc.FILEID AND f.DELETED is null AND fc.DELETED is null AND F.FILEID in (");
            
         HashMap filemap = new HashMap();
         while (rs1.next() && num < 50) {
            int i=1;
            long   fileid    = rs1.getLong(i++);
            String filename  = rs1.getString(i++);
            byte   filestat  = rs1.getByte(i++);
            long   filesize  = rs1.getLong(i++);
            long   fileisize = rs1.getLong(i++);
            String md5       = rs1.getString(i++);
            int    poolid    = rs1.getInt(i++);
            int    xferrate  = rs1.getInt(i++);
            
            DB2DboxFileInfo info = new DB2DboxFileInfo(this, filename, filesize, 
                                                       filestat, fileisize);
            info.setFileId(fileid);
            info.setFileMD5(md5);
            info.setPoolId(poolid);
            info.setFileXferrate(xferrate);
            
            Long tl = new Long(fileid);
            filemap.put(tl, info);
            
            if (num++ > 0) {
               sql.append(",");
            } 
            sql.append(fileid);
         }   
         sql.append(") ORDER BY f.FILEID, fc.STARTOFS");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         frontime2 = frontime1 = System.currentTimeMillis();
         
        // Start from scratch again on how many we REALLY process
         num = 0;
         
        //
        // If no files to process ... just return
        //
         if (filemap.size() == 0) return 0;
         
         
        // Now collect all filecomponent info in one fell swoop
        
         pstmt1.close();
         connection.commit();
         
         pstmt1=connection.prepareStatement(sql.toString()); 
            
         rs1=DbConnect.executeQuery(pstmt1);
            
         
        // Collect all filecomponents which are targets for deletion. 
         HashMap compinfo = new HashMap();         
         
         while (rs1.next()) {
            int    i=1;
            long   fileid    = rs1.getLong(i++);
            long   compsofs  = rs1.getLong(i++);
            String filename  = rs1.getString(i++);
            long   filesize  = rs1.getLong(i++);
            long   intendsize  = rs1.getLong(i++);
            
            DboxFileComponent fc = new DboxFileComponent(this, fileid, filesize,
                                                         intendsize, compsofs);
            fc.setFullPath(filename);
            Long fid = new Long(fileid);
            Vector v = (Vector)compinfo.get(fid);
            if (v == null) {
               v = new Vector();
               compinfo.put(fid, v);
            }
            
            v.add(fc);
         }
         
         pstmt1.close();
         connection.commit();
               
         frontime2 = System.currentTimeMillis();
               
        // Now, individually, we lock the files in question, delete any file components
        //   (returning the space) and disolve all db2 schema yuk for the file
        //
        // Also, I use a randomized iterator to help support the case where we  
        //  have multiple reapers running at once. This will let them ALL be more 
        //  productive as they will most likely work on different files.
        //
         Iterator it = SearchEtc.randomize(filemap.values()).iterator();
         while(it.hasNext()) {
         
            connection.commit();   // for good measure
         
            DboxFileInfo finfo = (DboxFileInfo)it.next();
            
            long lst = System.currentTimeMillis();
            
            log.info("cleanUnrefdFiles: attempt to reclaim file (" + finfo.getFileId() 
                     + ") [" + finfo.getFileName() + "] size = " + finfo.getFileSize());
                     
            sql = new StringBuffer("UPDATE EDESIGN.FILE SET DELETED=CURRENT TIMESTAMP");
            sql.append(" WHERE FILEID = ").append(finfo.getFileId());
            sql.append(" AND DELETED is null");
            
            pstmt1=connection.prepareStatement(sql.toString()); 
            
            int gotthis = DbConnect.executeUpdate(pstmt1);
            
            pstmt1.close();
            
           // If it did not work, then another reaper is probably running. Just
           //  log it, but don't alert/complain
            if (gotthis != 1) {
               log.info("\nSetting 'deleted' for FILE [" + finfo.getFileId() +
                        "] failed. Could be another reclaimer running. Skip");
               connection.commit();
               continue;
            }
            
           // Now we have the File row locked/modified, so any other reclaimer will
           //  block waiting for us to finish as we still have the lock till commit time.
           
           // We now delete the file components from disk, and mark all db2 components
           //  as deleted,  delete all remaining file slot objects and then commit the
           //  transaction.  If there was an error deleting any of the components from
           //  disk, the transaction will be rolled back (perhaps there is a filesystem
           //  issue?) so it can be retried again later.
           //
           // If transaction commit worked, then we can safely say that space can be
           //  returned to the fileallocators, so register the space. If the total amount
           //  registered exceeds some threshold, then purge it, otherwise, we wait till
           //  the end.  Recognize here, that if we fail to release the space back to the
           //  allocators, that we have already committed the deletion of the File and 
           //  accompanying metadata (as well as files on disk), so DB2 usedspace will be 
           //  less than it should. At the end of the dropboxCron, there is a consistency
           //  check that should help hilight this type of issue. Should be VERY few and
           //  far between ... if ever.
           
            Vector comps = (Vector)compinfo.get(new Long(finfo.getFileId()));
            boolean skiprest = false;
            if (comps != null) {
               Iterator fcit = comps.iterator();
               while(fcit.hasNext()) {
                  DboxFileComponent fc = (DboxFileComponent)fcit.next();
                  
                  File f = new File(fc.getFullPath());
                  if (f.exists() && f.isFile()) {
                     if (!f.delete()) {
                        if (exitalerts == null) exitalerts = new StringBuffer();
                        
                        exitalerts.append("\nFile id[").append(fc.getFileId());
                        exitalerts.append(" component[").append(fc.getStartingOffset());
                        exitalerts.append("] filename[").append(fc.getFullPath());
                        exitalerts.append("] filesize[").append(fc.getFileSize());
                        exitalerts.append("] intendize[").append(fc.getFileIntendedSize());
                        exitalerts.append("]. Could not delete! Skip file");
                        skiprest = true;
                        connection.rollback();
                        break;
                        
                     } else {
                        log.info("cleanUnrefdFiles: deleted fileComp (" + 
                                 fc.getFileId() + "/" + fc.getStartingOffset() + ") [" + 
                                 fc.getFullPath() + "] size = " + fc.getFileSize() + 
                                 " intendsz = " + fc.getFileIntendedSize());
                     }
                  } else {
                  
                     File p = f.getParentFile();
                     if (!p.exists() || !p.isDirectory()) {
                        if (exitalerts == null) exitalerts = new StringBuffer();
                        
                        exitalerts.append("\nFile id[").append(fc.getFileId());
                        exitalerts.append(" component[").append(fc.getStartingOffset());
                        exitalerts.append("] filename[").append(fc.getFullPath());
                        exitalerts.append("] filesize[").append(fc.getFileSize());
                        exitalerts.append("] intendize[").append(fc.getFileIntendedSize());
                        exitalerts.append("] parent directory does not exists or not Dir! skip File.");
                        connection.rollback();
                        skiprest = true;
                        break;
                     }
                     
                     if (finfo.getFileSize() != 0) {
                        if (exitalerts == null) exitalerts = new StringBuffer();
                        
                        exitalerts.append("\nFile id[").append(fc.getFileId());
                        exitalerts.append(" component[").append(fc.getStartingOffset());
                        exitalerts.append("] filename[").append(fc.getFullPath());
                        exitalerts.append("] filesize[").append(fc.getFileSize());
                        exitalerts.append("] intendize[").append(fc.getFileIntendedSize());
                        exitalerts.append("] file does not exist or not File! Perhaps prior reclaim operation failed. Continue.");
                        
                     } else {
                        log.info("cleanUnrefdFiles: deleted fileComp (" + 
                                 fc.getFileId() + "/" + fc.getStartingOffset() + ") [" + 
                                 fc.getFullPath() + "] size = " + 
                                 fc.getFileSize() + " was 0 len, did not exist. Sok.");
                     }
                     
                  }
               }
            }   
            
           // Now delete the components
            if (!skiprest) {
                  
               sql = new StringBuffer("UPDATE EDESIGN.FILECOMPONENT SET DELETED=CURRENT TIMESTAMP");
               sql.append(" WHERE FILEID = ").append(finfo.getFileId());
               sql.append(" AND DELETED is NULL");
                  
               pstmt1=connection.prepareStatement(sql.toString()); 
                  
               gotthis = DbConnect.executeUpdate(pstmt1);
                  
               pstmt1.close();
                  
              // If we did NOT get the correct amount of rows updated, roll back
              //  and skip the file for later processing
               if ((comps == null && gotthis != 0) || 
                   (comps != null && gotthis != comps.size())) {
                  if (exitalerts == null) exitalerts = new StringBuffer();
                     
                  exitalerts.append("\nFile id[").append(finfo.getFileId());
                  exitalerts.append(" Error deleting components from DB2. Got");
                  exitalerts.append(gotthis).append(" should have gotten ");
                  exitalerts.append((comps == null?0:comps.size()));
                  exitalerts.append(". Skip file!");
                  connection.rollback();
                  continue;
               }
                  
              // Delete all fileslots
               sql = new 
                  StringBuffer("DELETE FROM EDESIGN.FILESLOT ");
               sql.append(" WHERE FILEID = ").append(finfo.getFileId());
                  
               pstmt1=connection.prepareStatement(sql.toString()); 
                  
               DbConnect.executeUpdate(pstmt1);
               pstmt1.close();
                  
              // Do the deed and commit the changes for this file
               connection.commit();
                  
               num++;  // Count this one
                  
               long let = System.currentTimeMillis();
               log.info("File [" + finfo.getFileId() + "] reclaimed in [" + 
                        (let-lst) + " ms]");
                  
               
              // Now we can safely register the space to be reclaimed
               if (comps != null) {
                  Iterator fcit = comps.iterator();
                  while(fcit.hasNext()) {
                     DboxFileComponent fc = (DboxFileComponent)fcit.next();
                     long intendsize = fc.getFileIntendedSize();
                     File f = new File(fc.getFullPath());
               
                     String pdir = f.getParent();
                     Long retspace = (Long)returnSpace.get(pdir);
                     if (retspace == null) {
                        retspace = new Long(intendsize);  // JMC0606
                     } else {
                        retspace = new Long(intendsize+retspace.longValue()); // JMC0606
                     }
                     returnSpace.put(pdir, retspace);
                     returnspaceTotal += intendsize;
                     log.info("New Registered return space:" + pdir + ":" + retspace);
                  }
               }
            }
            
            if (returnspaceTotal > RETURNSPACE_FLUSH_THRESHOLD) {
               Iterator rsit = returnSpace.keySet().iterator();
               while(rsit.hasNext()) {
                  String pdir = (String)rsit.next();
                  long v = ((Long)returnSpace.get(pdir)).longValue();
                  try {
                     long llst = System.currentTimeMillis();
                     getFileAllocator().returnSpace(pdir, v);
                     long llet = System.currentTimeMillis();
                     log.info("db2fm:cuf: returnSpace: " + pdir + ":" + v + " : [" + 
                              (llet-llst) + " ms]");
                     returnspaceTotal -= v;
                     rsit.remove();   // We processed it ... remove it.
                  } catch(DboxException dbex) {
                     String m = 
                        "db2fm:cuf In Progress Error returnSpace: " + pdir + ":" + v + 
                        " " + dbex.getMessage();
                     log.warn(m);
                     
                     if (exitalerts == null) exitalerts = new StringBuffer();
                     exitalerts.append("\nIn Progress Error returning space: ").append(m);
                  }
               }
            }
         }
         
         connection.commit();
         
      } catch (SQLException e) {
         log.error("DB2FM.cleanUnrefFilesInt: SQL except: " + 
                   (sql==null?"":sql.toString())); 
         DbConnect.destroyConnection(connection); connection=null;
         pstmt1=null;
         DboxAlert.alert(e);  // Generic Alert
         
         throw new 
            DboxException("DB2FileMgr:>> SQLException during cleanUnrefdFiles", 
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt1 != null) try {pstmt1.close();} catch(SQLException e) {}
                  
         DbConnect.returnConnection(connection);
         DbConnect.restoreSharedConn(savedconnection);
         
        // Return any space still being held
         Iterator it = returnSpace.keySet().iterator();
         while(it.hasNext()) {
            String pdir = (String)it.next();
            long v = ((Long)returnSpace.get(pdir)).longValue();
            try {
               long lst = System.currentTimeMillis();
               getFileAllocator().returnSpace(pdir, v);
               long let = System.currentTimeMillis();
               log.info("db2fm:cuf: returnSpace: " + pdir + ":" + v + " : [" + 
                        (let-lst) + " ms]");
            } catch(DboxException dbex) {
               String m = 
                  "db2fm:cuf FINAL Error returnSpace: " + pdir + ":" + v + 
                  " " + dbex.getMessage();
               log.warn(m);
               
               if (exitalerts == null) exitalerts = new StringBuffer();
               exitalerts.append("\nFINAL Error returning space: ").append(m);
            }
         }
         
         if (exitalerts != null) {
            DboxAlert.alert(DboxAlert.SEV2, 
                            "Error cleaning unreferenced files",
                            0, exitalerts.toString());
         }
         
         long et = System.currentTimeMillis();
         log.info("Reclaim " + num + " query1 time [" + (frontime1-st) +
                  " query2 time [" + (frontime2-frontime1) +
                  "] work time [" + 
                  (et-frontime2) + "]");

      }   
      
      return num;
   }
   
   public DboxFileInfo lookupFile(long fileid) throws DboxException{
		   
      DboxFileInfo info=null;		 
      PreparedStatement pstmt =null;
      ResultSet rs=null;
      SharedConnection connection=null;
      StringBuffer sql = null;
      	
      try{
         connection=DbConnect.makeSharedConn();
         sql = new StringBuffer ("SELECT FILENAME, FILESTAT, ");
         
        // FSZCHANGE Used to have filesize now calculate it
        // JMC 7/29/04 - Ok, now do it if we say we trust the encoded sizes. 
         if (DB2PackageManager.getTrustSizes()) {
            sql.append("FILESIZE,");
         } else {
            sql.append("VALUE((SELECT SUM(fc.COMPONENTSIZE) from EDESIGN.FILECOMPONENT fc where f.FILEID=fc.FILEID AND f.DELETED is null and fc.DELETED is null ");
            
           // ISOLATION - SUBSELECT
            sql.append(DB2PackageManager.getROURSubselect());
            
            sql.append("),0),");
         }         
         
         sql.append("INTENDEDSIZE, MD5, MD5BLOB, POOLID, XFERSPEED FROM EDESIGN.FILE f WHERE f.fileid=? AND f.DELETED is null");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
                  
         pstmt=connection.prepareStatement(sql.toString());         
         pstmt.setLong(1,fileid);
			
         rs=DbConnect.executeQuery(pstmt);
		 
         if (!rs.next()) {
            throw new DboxException("DB2FileMgr:>> File lookup on fileid = " +
                                    fileid + " yielded no match", 0);
         }
         
         info = new DB2DboxFileInfo(this, rs.getString(1), rs.getLong(3),
                                    rs.getByte(2), rs.getLong(4));
         info.setFileId(fileid);
         info.setFileMD5(rs.getString(5)); // TODOTODOTODO reset by below
         info.setFileMD5ObjectFromBytes(rs.getBytes(6));
         info.setPoolId(rs.getInt(7));
         info.setFileXferrate(rs.getInt(8));
         
         
        // Selecting FILEID along with MAX(EXPIRATION) ensures that we get
        //  no false match ... sigh
         sql = new StringBuffer ("SELECT f.FILEID,MAX(p.EXPIRATION),MAX(f.CREATED) FROM EDESIGN.FILE f, EDESIGN.PACKAGE p, EDESIGN.FILETOPKG fp WHERE f.FILEID=fp.FILEID AND f.FILEID=? AND fp.PKGID=p.PKGID AND p.DELETED is null and f.DELETED is null and fp.DELETED is null GROUP BY f.FILEID");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt.close(); pstmt = null;
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1,fileid);
         
         rs=DbConnect.executeQuery(pstmt);
         if (rs.next()) {
            info.setFileExpiration(rs.getLong(2)); 
            info.setFileCreation(rs.getTimestamp(3).getTime()); 
         }
         
         connection.commit();
         
      } catch (SQLException e) {
         log.error("DB2FM.lookupFile: SQL except doing: " + 
                   (sql==null?"":sql.toString()) + " " + fileid);
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2FM.lookupFile:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
         
      return info;
   }	  
	   
     
   public synchronized void addFile(DboxFileInfo info) throws DboxException {
				
      if (info.getFileName().length() > 1024) {
         throw new DboxException("Filename too long (> 1024): " + 
                                 info.getFileName(), 0);
      }
      
      PreparedStatement pstmt =null;
      ResultSet rs = null;
      
      StringBuffer sql = null;      
      SharedConnection connection=null;
      try{
			
         long fileid = -1;
         
         connection=DbConnect.makeSharedConn();
          
         sql = new StringBuffer("values nextval for edesign.dropboxseq");
            
         pstmt=connection.prepareStatement(sql.toString());
		 
         rs=DbConnect.executeQuery(pstmt);
         
         if (rs.next()) {
            fileid = rs.getLong(1);
         } else {
            throw new 
               DboxException("addFile:>> Error getting next seq for fileid", 
                             0);
         }
         
         info.setFileId(fileid);
         
         sql=new StringBuffer("INSERT INTO EDESIGN.FILE(FILEID,FILENAME,FILESIZE,INTENDEDSIZE,FILESTAT,MD5,POOLID ) VALUES(?, ?, ?, ?, ?, ?, ?)");
         pstmt=connection.prepareStatement(sql.toString());  
         pstmt.setLong(1,fileid);
         pstmt.setString(2,info.getFileName());
         pstmt.setLong(3,info.getFileSize());
         pstmt.setLong(4,info.getFileIntendedSize());
         pstmt.setByte(5,info.getFileStatus());
         pstmt.setString(6,info.getFileMD5());
         pstmt.setInt(7, (int)info.getPoolId());
            
         if (DbConnect.executeUpdate(pstmt) != 1) {
            throw new DboxException("FileManager:addFile:>> DB error", 0);
         }
       
         connection.commit();
       
      } catch (SQLException e) {
         log.error("DB2FM.addFile: SQL except doing: " + 
                   (sql==null?"":sql.toString()) + " " + info.getFileName());
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2FM.addFile:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }      
	  
   public synchronized void removeFile(long itemid) throws DboxException {
     // This file is removed from FILETOPKG table by trigger as well.
      PreparedStatement pstmt =null;
      ResultSet rs=null;
      StringBuffer sql = null;      
      SharedConnection connection=null;
      try{
			
         connection=DbConnect.makeSharedConn();
         sql = new StringBuffer("DELETE FROM EDESIGN.FILE f WHERE f.FILEID=? AND f.DELETED is null");
         pstmt=connection.prepareStatement(sql.toString()); 
         pstmt.setLong(1,itemid);
         
        // Just complain via alert
         if (DbConnect.executeUpdate(pstmt) == 0) {
            DboxAlert.alert(DboxAlert.SEV3, 
                            "executeUpdate = 0 for fileid " + 
                            itemid, 
                            0, 
                            "FileManager: removeFile: got 0 from update " +
                            itemid);
         }
         
         connection.commit();
         
      } catch (SQLException e) {
         log.error("DB2FM.removeFile: SQL except doing: " + 
                   (sql==null?"":sql.toString()) + " " + itemid);
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2FM.removeFile:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
	  
   public synchronized DboxFileInfo  createFile(String file, 
                                                long len,
                                                long poolid) 
      throws DboxException {
      
      if (len == -1) {
         throw new DboxException("createFile:>> Bad length specified [" +
                                 file + "] len = " + len, 0);
      }
         
      DboxFileInfo info = new DB2DboxFileInfo(this, file, 0, 
                                              DropboxGenerator.STATUS_NONE, 
                                              len);
      info.setPoolId(poolid);
      addFile(info);
      return info;
   }      
   
   public Vector filesMatchingExprWithAccess(User user, 
                                             boolean ownerOrAccessor,
                                             String exp, 
                                             boolean isReg)
      throws DboxException {
         
      Vector ret = new Vector();
      DboxFileInfo finfo = null;
      PreparedStatement pstmt =null;
      ResultSet rs=null;
      StringBuffer sql =null;
      SharedConnection connection=null;
      try {
         
         if(exp!=null && isReg){
            log.error("TODO :FilesMatchingExprWAccess ! Needs updating!!");
            DboxException e = new 
               DboxException("DB2FM.filesMatchingExpWAccess:>> Not Implemented",
                             0);
            DboxAlert.alert(e);
            throw e;
         }
         
         connection=DbConnect.makeSharedConn();
         if (ownerOrAccessor){
            sql = new StringBuffer("SELECT f.FILENAME, ");
            
           // FSZCHANGE Used to have filesize now calculate it
           // JMC 7/29/04 - Ok, now do it if we say we trust the encoded sizes.
            if (DB2PackageManager.getTrustSizes()) {
               sql.append("FILESIZE,");
            } else {
               sql.append("VALUE((SELECT SUM(fc.COMPONENTSIZE) from EDESIGN.FILECOMPONENT fc where f.FILEID=fc.FILEID AND f.DELETED is null AND fc.DELETED is null ");
               
              // ISOLATION - SUBSELECT
               sql.append(DB2PackageManager.getROURSubselect());
               sql.append("),0),");
            }
            
            sql.append(" f.FILESTAT, f.INTENDEDSIZE,p.EXPIRATION,f.CREATED,f.MD5,f.FILEID,f.MD5BLOB,f.POOLID,f.XFERSPEED ");
            sql.append ("FROM EDESIGN.FILE f, EDESIGN.PACKAGE p, EDESIGN.FILETOPKG fp ");
            sql.append ("WHERE f.FILEID=fp.FILEID AND fp.PKGID=p.PKGID AND p.DELETED is null AND fp.DELETED is null AND f.DELETED is null AND ");
            
            boolean superuser = false;
            if (packageManager.getPrivilegeLevel(user.getName()) >= 
                packageManager.PRIVILEGE_SUPER_USER) {
              // Don't know if I can use TRUE or constant = constant ...
               superuser = true;
               sql.append(" (p.ownerid='na' OR p.ownerid != 'na') ");
            } else {
               sql.append(" p.ownerid=? ");
            }
            
           // ISOLATION
            sql.append(DB2PackageManager.getROUR());                 
            
            pstmt=connection.prepareStatement(sql.toString()); 
            
            if (!superuser) {
               pstmt.setString(1,user.getName());
            }
         } else {
            sql = new StringBuffer("SELECT FILENAME,");
            
           // FSZCHANGE Used to have filesize now calculate it
           // JMC 7/29/04 - Ok, now do it if we say we trust the encoded sizes 
            if (DB2PackageManager.getTrustSizes()) {
               sql.append("FILESIZE,");
            } else {
               sql.append("VALUE((SELECT SUM(fc.COMPONENTSIZE) from EDESIGN.FILECOMPONENT fc where f.FILEID=fc.FILEID AND f.DELETED is null AND fc.DELETED is null ");
               
              // ISOLATION - SUBSELECT
               sql.append(DB2PackageManager.getROURSubselect());
               sql.append("),0), ");
            }
            
            sql.append(" FILESTAT, INTENDEDSIZE, ");
            sql.append ("p.EXPIRATION,f.CREATED,f.MD5,f.FILEID,f.MD5BLOB,f.POOLID FROM EDESIGN.FILE f, EDESIGN.PACKAGE p, EDESIGN.FILETOPKG fp ");
            
            if (packageManager.policy_supportLookup) {
               sql.append(",EDESIGN.ALLOWSEND a ");
            }
                        
            sql.append (",EDESIGN.PKGACL pacl WHERE f.FILEID=fp.FILEID AND fp.PKGID=p.PKGID AND p.PKGID=pacl.PKGID AND p.DELETED is null AND f.DELETED is null AND fp.DELETED is null AND ");
            
            
           // If not owner, can only access package if its complete
            sql.append("(p.STATUS=? ");
            
            boolean addSameCompany = false;
            boolean addLookup      = false;
            
           // If Policy is in force, do we have access
            if (packageManager.policy_supportSameCompany || 
                packageManager.policy_supportIBM         ||
                packageManager.policy_supportLookup) {
               
              // Only do it if we are NOT IBM OR not supportIBM policy
               if (!packageManager.policy_supportIBM ||
                   !user.getCompany().equalsIgnoreCase("IBM")) {
                  
                  boolean didit = false;
                  
                  sql.append(" AND (");
                  if (packageManager.policy_supportIBM) {
                     sql.append(" p.company='IBM' ");
                     didit = true;
                  }
                  
                  if (packageManager.policy_supportSameCompany) {
                     if (didit) {
                        sql.append(" OR ");
                     }
                     sql.append(" (p.company=? and p.company != '') ");
                     addSameCompany = true;
                     didit = true;
                  }
                  
                  if (packageManager.policy_supportLookup) {
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
            
            sql.append("))");
            
            
           // ISOLATION
            sql.append(DB2PackageManager.getROUR());
                               
            pstmt=connection.prepareStatement(sql.toString()); 
            
            int idx = 1;
            
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
         }
         
         rs=DbConnect.executeQuery(pstmt);	
         
        // FIXNESTED FIXNESTED FIXNESTED 
         while (rs.next()) {
            finfo = new DB2DboxFileInfo (this, rs.getString(1), rs.getLong(2), rs.getByte(3), rs.getLong(4));
            
            finfo.setFileExpiration(rs.getLong(5));
            finfo.setFileCreation(rs.getTimestamp(6).getTime());
            finfo.setFileMD5(rs.getString(7)); // TODOTODOTODO reset by below
            finfo.setFileId(rs.getLong(8));
            finfo.setFileMD5ObjectFromBytes(rs.getBytes(9));
            finfo.setPoolId(rs.getInt(10));
            finfo.setFileXferrate(rs.getInt(11));
            
            try {
              // This redundent access check is needed cause its too hard
              //  to check if the file is in an ITAR package here. WISH db2
              //  supported bitwise operators! Could solve by ditching 
              //  package flags for individual package flag bytes
               if (packageManager.canAccessFile(user, finfo, true)) {
                  ret.addElement(finfo);
               }
            } catch(DboxException ldbe) {}
         }
         
         connection.commit();
         
      } catch (SQLException e) {
         log.error("DB2FM.filesMatchExpwAcc: SQL except doing: " + 
                   (sql==null?"":sql.toString()) + " " + user.getName());
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2FM.filesMatchingExpWithAccess:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
      
      return ret;
   }
   
   public Vector filesMatchingExpr(String exp, 
                                   boolean isReg) throws DboxException {
         
      log.error("TODO :FilesMatchingExpr! Needs updating!!");
      
      DboxException e = new 
         DboxException("DB2FM.filesMatchingExp:>> Not Implemented", 0);
      DboxAlert.alert(e);
      throw e;
   }         
}   
