package oem.edge.ed.odc.dropbox.server;

import  oem.edge.ed.odc.dropbox.common.*;
import  oem.edge.ed.odc.ftp.common.*;
import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.dsmp.server.*;
import  oem.edge.ed.odc.util.*;

import java.lang.*;
import java.io.*;
import java.nio.channels.*;
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
public class DB2DboxFileInfo extends DboxFileInfo {

  //
  // 12 min lock duration is based on 2Meg slot size / 3KB/s download rate
  //  We want this duration as short as possible, but as long as needed. 3KB/s
  //  is WAY bad, but sometimes, its just the truth
//   final static long FileSlotLockDuration = 1000 * 60 * 12; // 12 min lock duration

  // Ok, we raised the slot size to be 10Meg to equal component size, so base the 
  // timeout on that static fact. True is we should be doing this lock based on the
  // actual size of the data remaining to upload in the slot. The locktime field in
  // the slot SHOULD be an expire time rather than lock time.  Also, the fileslot lock
  // should become invalid when the sessionid doing the locking expires as well. TODO!!
   final static long FileSlotLockDuration = (10*1024*1024*1000)/(3*1024);
   
  /* used to help debug locking
   Boolean locker = new Boolean(true);
   class loctst extends Thread {
      public void run() {
         SharedConnection connection=null;
         try{
            
            connection=DbConnect.makeSharedConn();
         
            try {
               fileManager.lockfile(fileid);
            } catch(DboxException de) {
               log.warn("Dbox Exception");
               log.warn(de);
            }
            synchronized(locker) {
               try {
                  locker.wait();
               } catch(Exception ee) {}
            }
            
            connection.commit();
         } catch (SQLException e){
            log.error("loctst: SQL except!");
            DbConnect.destroyConnection(connection); connection=null;
            DboxAlert.alert(e);  // Generic Alert
         } finally {
            DbConnect.returnConnection(connection);
         }
      }
   }
   
   public void testlock() {
      log.info("\n\n\nDoing LockTest\n\n");
      loctst lt1 = new loctst();
      loctst lt2 = new loctst();
      lt1.setName("loctst1");
      lt2.setName("loctst2");
      
      log.info("Starting thread 1");
      lt1.start();
      
      try {
         log.info("Sleeping for 3 secs");
         Thread.sleep(3000);
      } catch(Exception e) {}
      
      log.info("Starting thread 2");
      lt2.start();
      
      try {
         log.info("Sleeping for 3 secs");
         Thread.sleep(3000);
      } catch(Exception e) {}
      
      log.info("opening locker");
      synchronized(locker) {
         locker.notifyAll();
      }
      
      try {
         log.info("Sleeping for 3 secs");
         Thread.sleep(3000);
      } catch(Exception e) {}
      
      log.info("opening locker again");
      synchronized(locker) {
         locker.notifyAll();
      }
   }
  */
   
   public DB2DboxFileInfo(DboxFileInfo i) {
      super(i);
   }
   
   public DB2DboxFileInfo(FileManager fm) { 
      super(fm);
   }
   
   public DB2DboxFileInfo(FileManager fm, String n, long sz, byte stat, 
                          long isz) { 
      super(fm, n, sz, stat, isz);
   }
   
   public void forceSetFileXferrate(int xferrate) throws DboxException {
   
      super.forceSetFileXferrate(xferrate);
      
      PreparedStatement pstmt =null;
      ResultSet rs=null;
      StringBuffer sql = null;
      SharedConnection connection=null;
      try{
         connection=DbConnect.makeSharedConn();
         
        // This is just like a lock on the row
         sql = new 
            StringBuffer("UPDATE EDESIGN.FILE f SET f.XFERSPEED = ? WHERE f.FILEID=? AND f.DELETED is null");
         pstmt=connection.prepareStatement(sql.toString()); 
         pstmt.setInt   (1, xferrate);
         pstmt.setLong  (2, fileid);
         
         int v = DbConnect.executeUpdate(pstmt);
         
        // Silent fail
//         if (v != 1) { 
//            throw new 
//               DboxException("DB problem setting xferrate on file. 0|2+ matches", 
//                             0);
//         }
         
         connection.commit();
      } catch (SQLException e){
         log.error("DB2DboxFileInfo:forceSetFileXferrate: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2DboxFileInfo:forceSetFileXferrate:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
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
         String md5str = "";
         if (v != null) {
            try {
               blobbytes = v.stateToBytes();
            } catch(Exception ee) {
               throw new DboxException("Error getting MD5 state bytes");
            }
            md5str = v.hashAsString();
         }
         connection=DbConnect.makeSharedConn();
         
        // This is just like a lock on the row
         sql = new 
            StringBuffer("UPDATE EDESIGN.FILE f SET (f.MD5BLOB,f.MD5) = (?,?) WHERE f.FILEID=? AND f.DELETED is null");
         pstmt=connection.prepareStatement(sql.toString()); 
         pstmt.setBytes (1, blobbytes);
         pstmt.setString(2, md5str);
         pstmt.setLong  (3, fileid);
         
         if (DbConnect.executeUpdate(pstmt) != 1) { 
            throw new 
               DboxException("DB problem setting MD5 on file. 0|2+ matches", 
                             0);
         }
         
         connection.commit();
      } catch (SQLException e){
         log.error("DB2DboxFileInfo:forceSetFileMD5Object: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2DboxFileInfo:forceSetFileMD5Object:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
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
         String md5str = "";
         if (v != null) {
            try {
               blobbytes = v.stateToBytes();
            } catch(Exception ee) {
               throw new DboxException("Error getting MD5 state bytes");
            }
            md5str = v.hashAsString();
         }
         connection=DbConnect.makeSharedConn();
         
        // This is just like a lock on the row
         sql = new 
            StringBuffer("UPDATE EDESIGN.FILE f SET (FILESIZE,MD5BLOB,MD5,CHANGETIME) = (?,?,?,CURRENT TIMESTAMP) WHERE f.FILEID=? AND f.DELETED is null");
         pstmt=connection.prepareStatement(sql.toString()); 
         pstmt.setLong  (1, sz);
         pstmt.setBytes (2, blobbytes);
         pstmt.setString(3, md5str);
         pstmt.setLong  (4, fileid);
        
         if (DbConnect.executeUpdate(pstmt) != 1) { 
            throw new 
               DboxException("DB problem setting MD5 on file. 0|2+ matches", 
                             0);
         }
         
         connection.commit();
         
      } catch (SQLException e){
         log.error("DB2DboxFileInfo:forceSetSizeMD5Object: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2DboxFileInfo:forceSetSizeMD5Object:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
      
   }
   
   
   public void forceSetFileMD5(String md5) throws DboxException {
         
      super.setFileMD5(md5);
      
      PreparedStatement pstmt =null;
      ResultSet rs=null;
      StringBuffer sql = null;
      SharedConnection connection=null;
      try{
			
         connection=DbConnect.makeSharedConn();
         
        // This is just like a lock on the row
         sql = new 
            StringBuffer("UPDATE EDESIGN.FILE f SET f.MD5=? WHERE f.FILEID=? AND f.DELETED is null");
         pstmt=connection.prepareStatement(sql.toString()); 
         pstmt.setString(1, md5);
         pstmt.setLong(2,getFileId());
         
         if (DbConnect.executeUpdate(pstmt) != 1) { 
            throw new 
               DboxException("DB problem setting MD5 on file. 0|2+ matches", 
                             0);
         }
         
         connection.commit();
         
      } catch (SQLException e){
         log.error("DB2DboxFileInfo:forceSetFileMD5: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2DboxFileInfo:forceSetMD5:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
   
  /// TODOTODOTOD  Should be forceSetFileIntendedSize. Refactor to be clearer
   public void setFileIntendedSize(long sz) throws DboxException {
      super.setFileIntendedSize(sz);
      PreparedStatement pstmt =null;
      ResultSet rs=null;
      
      StringBuffer sql = null;
      SharedConnection connection=null;
      try{
			
         connection=DbConnect.makeSharedConn();
         
        // This is just like a lock on the row
         sql = new StringBuffer("UPDATE EDESIGN.FILE f SET f.INTENDEDSIZE=? WHERE f.FILEID=? AND f.DELETED is null");
         pstmt=connection.prepareStatement(sql.toString()); 
         pstmt.setLong(1,sz);
         pstmt.setLong(2, getFileId());
         
         if (DbConnect.executeUpdate(pstmt) != 1) { 
            throw new 
               DboxException("DB problem setting intendedSize. 0|2+ matches", 
                             0);
         }
         
         connection.commit();
            
      } catch (SQLException e) {
         log.error("DB2DboxFileInfo:setFileIntendedSize: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2DboxFileInfo:setFileIntendedSize:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
      
      
   public void forceSetFileSize(long sz) throws DboxException {
      super.setFileSize(sz);
      
      PreparedStatement pstmt =null;
      ResultSet rs=null;
      
      StringBuffer sql = null;
      SharedConnection connection=null;
      try{
			
         connection=DbConnect.makeSharedConn();
         
        // This is just like a lock on the row
         sql = new StringBuffer("UPDATE EDESIGN.FILE f SET (f.FILESIZE,f.CHANGETIME) = (?,CURRENT TIMESTAMP) WHERE f.FILEID=? AND f.DELETED is null");
         pstmt=connection.prepareStatement(sql.toString()); 
         pstmt.setLong(1,sz);
         pstmt.setLong(2, getFileId());
         
         if (DbConnect.executeUpdate(pstmt) != 1) { 
            throw new 
               DboxException("DB problem setting fileSize. 0|2+ matches", 
                             0);
         }
         
         connection.commit();
            
      } catch (SQLException e) {
         log.error("DB2DboxFileInfo:setFileSize: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2DboxFileInfo:setFileSize:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
      
  /* Nope
   public void setFileStatus(byte stat) {
      try {
         setServersideFileStatus(stat);
      } catch(DboxException dbe) {
      }
   }
  */
   
   public void setServersideFileStatus(byte stat) throws DboxException {
      PreparedStatement pstmt =null;
      ResultSet rs=null;
      
      StringBuffer sql = null;
      SharedConnection connection=null;
      try{
			
         connection=DbConnect.makeSharedConn();
         
        // This is just like a lock on the row
         sql = new StringBuffer("UPDATE EDESIGN.FILE f SET f.FILESTAT=? WHERE f.FILEID=? AND f.DELETED is null");
         pstmt=connection.prepareStatement(sql.toString()); 
         pstmt.setByte(1,stat);
         pstmt.setLong(2, getFileId());
         
         if (DbConnect.executeUpdate(pstmt) != 1) { 
            throw new 
               DboxException("DB problem setting file status. 0|2+ matches", 
                             0);
         }
         
         filestatus=stat;
         
         connection.commit();
            
      } catch (SQLException e) {
         log.error("DB2DboxFileInfo:setFileStatus: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2DboxFileInfo:setFileStatus:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
   
   public Vector getComponents() throws DboxException {
	   
	  
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      StringBuffer sql = null;
      long fileid=getFileId();
      Vector ret = new Vector();
      DboxFileComponent comp = null;
      try{
			
         connection=DbConnect.makeSharedConn();
         sql = new StringBuffer("SELECT COMPONENTSIZE, COMPINTENDEDSIZE, FILENAME, MD5BLOB, STARTOFS ");
         sql.append("FROM EDESIGN.FILECOMPONENT fc WHERE fc.FILEID=? AND fc.DELETED is NULL ORDER BY STARTOFS ASC");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1, fileid);
         
         rs=DbConnect.executeQuery (pstmt);
         while (rs.next()){
            comp = new DB2DboxFileComponent (fileManager, 
                                             fileid, 
                                             rs.getLong(1), 
                                             rs.getLong(2), 
                                             rs.getLong(5), 
                                             rs.getString(3));
            
            try{
               
               comp.setMD5blobBytes(rs.getBytes(4));
               
            }catch(Exception ex){
              //blob might be empty
            }				
            
            comp.threadedCloseWillHelp(comp.getFullPath().startsWith("/afs"));
            ret.addElement(comp); 
         }
         
         connection.commit();
         
      } catch (SQLException e) {
         log.error("DB2DboxFileInfo:getComponents: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2DboxFileInfo:getFileComponents:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
      
      return ret;
   }
   
   public Vector getFileSlots() throws DboxException {
	   
	  
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      StringBuffer sql = null;
      long fileid=getFileId();
      Vector ret = new Vector();
      DboxFileSlot slot = null;
      try{
			
         connection=DbConnect.makeSharedConn();
         sql = new StringBuffer("SELECT SLOTSIZE, INTENDEDSIZE, STARTOFS, SESSIONID, LOCKTIME, COMPONENTID, MD5BLOB ");
         sql.append("FROM EDESIGN.FILESLOT fs WHERE fs.FILEID=? ORDER BY STARTOFS ASC");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1, fileid);
         
         rs=DbConnect.executeQuery (pstmt);
         
         while (rs.next()){
            slot = new DB2DboxFileSlot(fileManager, 
                                       fileid,
                                       rs.getLong("STARTOFS"),
                                       rs.getLong("SLOTSIZE"),
                                       rs.getLong("INTENDEDSIZE"), 
                                       rs.getLong("STARTOFS"),
                                       rs.getLong("SESSIONID"),
                                       rs.getLong("COMPONENTID"),
                                       rs.getLong("LOCKTIME"));
                                       
            byte md5bytes[] = rs.getBytes("MD5BLOB");
            
            try {
               slot.setMD5ObjectFromBytes(md5bytes);
            } catch(Exception e) {}
         
            ret.addElement(slot); 
         }
         
         connection.commit();
         
      } catch (SQLException e) {
         log.error("DB2DboxFileInfo:getFileSlots: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2DboxFileInfo:getFileSlots:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
      
      return ret;
   }
   
   public DboxFileSlot getFileSlot(long slotid) throws DboxException {
	   
	  
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      StringBuffer sql = null;
      long fileid=getFileId();
      DboxFileSlot slot = null;
      try{
			
         connection=DbConnect.makeSharedConn();
         sql = new StringBuffer("SELECT SLOTSIZE, INTENDEDSIZE, STARTOFS, SESSIONID, LOCKTIME, COMPONENTID, MD5BLOB ");
         sql.append("FROM EDESIGN.FILESLOT fs WHERE fs.FILEID=? AND fs.STARTOFS = ?");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1, fileid);
         pstmt.setLong(2, slotid);
         
         rs=DbConnect.executeQuery (pstmt);
         
         if (!rs.next()) {
            throw new DboxException("Invalid file or slot id");
         }
         
         slot = new DB2DboxFileSlot(fileManager, 
                                    fileid,
                                    rs.getLong("STARTOFS"),
                                    rs.getLong("SLOTSIZE"),
                                    rs.getLong("INTENDEDSIZE"), 
                                    rs.getLong("STARTOFS"),
                                    rs.getLong("SESSIONID"),
                                    rs.getLong("COMPONENTID"),
                                    rs.getLong("LOCKTIME"));
                                    
         byte md5bytes[] = rs.getBytes("MD5BLOB");
         try {
            slot.setMD5ObjectFromBytes(md5bytes);
         } catch(Exception e) {}
                                    
         connection.commit();
                                    
         return slot;
      } catch (SQLException e) {
         log.error("DB2DboxFileInfo:getFileSlot: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2DboxFileInfo:getFileSlots:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
   
  /*
  ** This method will merge slots into slot0 as it can.
  */
   
   public void cullSlots() throws DboxException {
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      StringBuffer sql = null;
      long fileid=getFileId();
      
      try {
			
        // Get a connection to be used for autocommit = false.
        // This MAY be a shared connection, so I MAY be living in another transaction
        // Any changes made that need to be undone when rollback occurs need
        // to be registered
         connection=DbConnect.makeSharedConn();
         
         fileManager.lockfile(fileid);
         
        // Get the real file size as it exists NOW that its locked
         sql = new StringBuffer("SELECT FILESIZE FROM EDESIGN.FILE WHERE FILEID = ");
         sql.append(fileid);
         
         pstmt=connection.prepareStatement(sql.toString());
         
         rs=DbConnect.executeQuery (pstmt);
         
         if (!rs.next()) {
            throw new DboxException("File " + fileid + " no longers exists!");
         }
         
         long filesz = rs.getLong(1);
         pstmt.close();
         
         
        // Get all slots which are ready to be culled
         sql = new StringBuffer("SELECT SLOTSIZE, STARTOFS ");
         sql.append("FROM EDESIGN.FILESLOT WHERE FILEID=? AND SLOTSIZE = INTENDEDSIZE ORDER BY STARTOFS ASC");
         
        // NO ISOLATION ... selecting for update
        // sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1, fileid);
         
         rs=DbConnect.executeQuery (pstmt);
         
        // For each slot ready to be culled (sorted by STARTOFS, so in order)
        //   if the slot is ready to be merged, merge into File's size
        //   if merging the slot completes a component, set the md5blob on it
        //
        // We are going with a 'fully checked' MD5 here. We read in from disk
        //  what was written and calc MD5. Using the restartable MD5, we always
        //  get to start at the start of the first slot (by definition). We
        //  simply count up the number of slots we will be including in the 
        //  cull, and use the calcAndReturnMD5 to do all the real work, He even
        //  updates the MD5blobs on the components as appropriate.
        //
         long nextofs    = filesz;
         int  lastcompid = -1;
         Vector slots = new Vector();
         while (rs.next()) {
         
            long slotsize   = rs.getLong(1);
            long startofs   = rs.getLong(2);
            
            if (startofs == nextofs) {
               slots.add(new Long(startofs));
               nextofs += slotsize;
            }
         }
         
         pstmt.close();
         pstmt = null;
         
         
        // If we have something to cull OR 
        //  if we are done and its not marked complete (0 len file perhaps)
        //   calc MD5 up to that point 
        //   Save MD5 on File and save file size
        //   Delete the slots
         if (nextofs != filesz || 
         
             (nextofs == getFileIntendedSize() && 
              getFileStatus() != DropboxGenerator.STATUS_COMPLETE)) {
         
           // Set the filesize and MD5 object to null so things work properly
            setFileSize(nextofs);
            setFileMD5Object(null);
            
            MessageDigestI md5 = null;
            try {
               md5 = calculateAndReturnMD5(nextofs, true);
            } catch(DboxException dbe) {
               throw dbe;
            } catch(Exception e) {
               throw new DboxException("Error calculating MD5", e);
            }
            
           // Set the size and MD5 object/string on File.
            forceSetSizeAndMD5Object(nextofs, md5);
            
           // If we are done, then mark file as complete
            if (nextofs == getFileIntendedSize()) {
               forceSetFileStatus(DropboxGenerator.STATUS_COMPLETE);
            }
            
            sql = new StringBuffer("DELETE FROM EDESIGN.FILESLOT WHERE FILEID=? AND STARTOFS in (");
            Iterator it = slots.iterator();
            int i = 0;
            while(it.hasNext()) {
               long slotid = ((Long)it.next()).longValue();
               if (i++ > 0) {
                  sql.append(",");
               }
               sql.append(slotid);
            }
            
            sql.append(")");
            
            if (i > 0) {
               pstmt=connection.prepareStatement(sql.toString());
               pstmt.setLong(1, fileid);
               
               if (DbConnect.executeUpdate (pstmt) != i) {
                  log.error("Error cleaning up FileSlots after cull operation: " + 
                            toString());
                  throw new DboxException("Could not delete fileslots after all other updates during a cull!");
               }
            }
            
         }
         
        // If in a shared transaction, this is a NOOP
         connection.commit();
         
      } catch (SQLException e) {
         log.error("DB2DboxFileInfo:cullSlots: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
                   
         DbConnect.destroyConnection(connection); connection = null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2DboxFileInfo:cullSlots:>> SQL exception",
                          DboxException.SQL_ERROR);
                          
      } finally {
      
         if (pstmt!=null)   try {pstmt.close();}  catch (SQLException e) {}
         
         DbConnect.returnConnection(connection);
      }
   }
   
   
  /*
  ** Slot formation:
  **
  **   1) Either NO slots OR
  **   2) S1, S2, ..., Sx where 
  **        Sx->startOfs = (Sx-1)->startOfs + (Sx-1)->intendedSize
  **   
  **   When Slots are 'deleted' they will actually be kept and
  **    reused. Doing things this way will ensure there are no
  **    HOLES that we have to find/calculate.
  **
  **   Note, slotid is a fiction. startofs is really the slot id
  */
   public DboxFileSlot allocateFileSlot(User user) throws DboxException {
	   
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      PreparedStatement pstmt2=null;
      ResultSet rs=null;
      StringBuffer sql = null;
      long fileid=getFileId();
      DboxFileSlot slot = null;
      
      try {
			
        // Get an already allocated but disowned or expired slot which still has
        //  room in it
        
         connection=DbConnect.makeSharedConn();
         
         fileManager.lockfile(fileid);
         
        // Get the real file size as it exists NOW that its locked
         sql = new StringBuffer("SELECT FILESIZE FROM EDESIGN.FILE WHERE FILEID = ");
         sql.append(fileid);
         
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         
         rs=DbConnect.executeQuery (pstmt);
         
         if (!rs.next()) {
            throw new DboxException("File " + fileid + " no longers exists!");
         }
         
         long filesz = rs.getLong(1);
         pstmt.close();
         
        // If the filesize has changed ... update it
         if (filesz != getFileSize()) {
            setFileMD5Object(null);
            setFileSize(filesz);
         }
         
         sql = new StringBuffer("SELECT SLOTSIZE, INTENDEDSIZE, STARTOFS, SESSIONID, LOCKTIME, COMPONENTID, MD5BLOB ");
         sql.append("FROM EDESIGN.FILESLOT WHERE FILEID=? AND SLOTSIZE < INTENDEDSIZE AND (SESSIONID = 0 OR (LOCKTIME) < ?) ORDER BY STARTOFS ASC ");
         
         sql.append(DB2PackageManager.getROUR());

         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1, fileid);
         pstmt.setLong(2, System.currentTimeMillis() - FileSlotLockDuration);
         
         rs=DbConnect.executeQuery (pstmt);
         
        // If we get one, then just put in the locktime and sessionid
         if (rs.next()) {
            slot = new DB2DboxFileSlot(fileManager, 
                                       fileid,
                                       rs.getLong("STARTOFS"),
                                       rs.getLong("SLOTSIZE"),
                                       rs.getLong("INTENDEDSIZE"), 
                                       rs.getLong("STARTOFS"),
                                       rs.getLong("SESSIONID"),
                                       rs.getLong("COMPONENTID"),
                                       rs.getLong("LOCKTIME"));
            slot.setLockTime(System.currentTimeMillis());
            slot.setSessionId(user.getSessionId());
            
            byte md5bytes[] = rs.getBytes("MD5BLOB");
            try {
               slot.setMD5ObjectFromBytes(md5bytes);
            } catch(Exception e) {}
                        
            sql = new StringBuffer("UPDATE EDESIGN.FILESLOT set (LOCKTIME, SESSIONID) = (?,?) WHERE FILEID=? AND STARTOFS = ?");
         
            pstmt2=connection.prepareStatement(sql.toString());
            pstmt2.setLong(1, System.currentTimeMillis());
            pstmt2.setLong(2, user.getSessionId());
            pstmt2.setLong(3, fileid);
            pstmt2.setLong(4, slot.getSlotId());
               
            if (DbConnect.executeUpdate (pstmt2) != 1) {
               throw new DboxException("Error updating allocated slot in DB: " + 
                                       slot.toString());
            }
            
            connection.commit();
            
            return slot;
         }
         
         pstmt.close();
         pstmt = null;
         
         
        // Cull Slots that are ready to join the main body
        // This MAY change our filesize!
         cullSlots();
         
        // insert a new fileslot (if allowed)
         
        //
        // Get numslots, next slot number, and the startofs and len for the
        //  current LAST slot
        //
         sql = new StringBuffer("SELECT STARTOFS, INTENDEDSIZE, SLOTSIZE FROM EDESIGN.FILESLOT WHERE FILEID=? ORDER BY STARTOFS ASC ");
         
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1, fileid);
         
         rs=DbConnect.executeQuery (pstmt);
         
        // If we have some slots, use that info to move forward, otherwise, we
        //  have NO slots ... deal with that by creating a new slot. If there is 
        //  contention for the slot being created, then only 1 will win, as 
        //  there is a constraint which only allows startofs to be used a
        //  single time for a fileid
        //
        // Biiiing ... we lock on the File row for ALL updates to file/slots/components
        //             now
         long startofs             = getFileSize();
         long lastslotstartofs     = 0;
         long lastslotintendedsize = startofs;
         long lastslotsize         = startofs;
         int  numactiveslots = 0;
         while (rs.next()) {
            lastslotstartofs     = rs.getLong(1);
            lastslotintendedsize = rs.getLong(2);
            lastslotsize         = rs.getLong(3);
            
           // Count the slot as ACTIVE
            if (lastslotintendedsize > lastslotsize) numactiveslots++;
         }
         
         startofs  = lastslotstartofs + lastslotintendedsize;
         
        // only allow so many simultaneous slots
         if (numactiveslots >= max_num_active_slots) {
            connection.commit();  /// make sure any culling COUNTS            
            throw new DboxException("Max number of Upload Fileslots outstanding");
         }
         
         pstmt.close();
         pstmt = null;
         
         long intendedsize = getFileIntendedSize();
         
        // If we are done, there is nothing more we can insert
        //
         if (startofs >= intendedsize) {
         
           // If there is only 1 slot, and it is complete ... we are done
            if (startofs == intendedsize && 
                lastslotintendedsize == lastslotsize &&
                numactiveslots == 0) {
                
               connection.commit();  // make sure any culling COUNTS
               log.warn("Looks like its done. Nothing more to do. Just return null");
               return null;
              //throw new DboxException("File upload Complete");
            }
            
           // TODOTODOTODOTOD - Perhaps throw an exception rather than return null
           //throw new DboxException("");
            log.warn("Looks like its done. Nothing more to do. Just return null");
           
            connection.commit();  // make sure any culling COUNTS
            return null;
         }
         
        // Max amount it COULD be
         intendedsize  = intendedsize - startofs;
         
        // Limit it to what we want as max slot size
         if (intendedsize > max_file_slot_size) {
            intendedsize = max_file_slot_size;
         }
         
        // Now, map this onto how much is avail in associated component
        
        // Get the component with the largest starting ofs
         sql = new StringBuffer("SELECT STARTOFS, COMPINTENDEDSIZE FROM EDESIGN.FILECOMPONENT WHERE FILEID=? AND DELETED is NULL ORDER BY STARTOFS DESC FETCH FIRST 1 ROW ONLY ");
         
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1, fileid);
         
         rs=DbConnect.executeQuery (pstmt);
         
         
         long compid        = 0;
         long compstartofs  = 0;
         long compintendsz  = 0;
         long compsize      = 0;
         long compendofs    = 0;
         boolean createcomp = true;
         
        // If we HAVE a component, then try to fit it in there. If no room, then
        //  create a new component.
         if (rs.next()) {
         
            compstartofs = rs.getLong(1);
            compid       = compstartofs;
            compintendsz = rs.getLong(2);
            compendofs   = compstartofs + compintendsz;
            
            if (startofs < compstartofs) {
               throw new DboxException("Slot being created is NOT in last component!");
            }
            
            if (startofs > compendofs) {
               throw new DboxException("Slot being created is NOT lined up!");
            }
            
            if (startofs < compendofs) {
            
              // We have room in current component ... use up to intendsize amt
               if (compendofs-startofs < intendedsize) {
                  intendedsize = compendofs-startofs;
               } 
               
               createcomp = false;
            }
         }
         
         pstmt.close();
         pstmt = null;
         
        // If we need a new component ... get it
         if (createcomp) {
           // compid and compstartofs are correct. Manufacture the rest
            
            DboxFileComponent comp = createComponent(compendofs);
            compintendsz = comp.getIntendedFileSize();
            
            if (compintendsz < intendedsize) {
               intendedsize = compintendsz;
            } 
            
            if (compendofs != startofs) {
               throw new DboxException("Newly created component and slot differ in startofs!");
            }
            compid = compendofs;
         }
         
         slot = new DB2DboxFileSlot(fileManager, 
                                    fileid, 
                                    startofs,
                                    0L,
                                    intendedsize, 
                                    startofs,
                                    user.getSessionId(),
                                    compid,
                                    System.currentTimeMillis());
         
         sql = new StringBuffer("INSERT INTO EDESIGN.FILESLOT (FILEID, SLOTSIZE, INTENDEDSIZE, STARTOFS, SESSIONID, LOCKTIME, COMPONENTID) values(?,?,?,?,?,?,?)");
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1, fileid); 
         pstmt.setLong(2, 0);
         pstmt.setLong(3, intendedsize);
         pstmt.setLong(4, startofs);
         pstmt.setLong(5, user.getSessionId());
         pstmt.setLong(6, slot.getLockTime());
         pstmt.setLong(7, slot.getComponentId()); 
        
         if (DbConnect.executeUpdate (pstmt) != 1) {
            connection.commit();  /// make sure any culling COUNTS
         
            throw new DboxException("Error inserting DB record while allocating slot: " + 
                                    slot.toString());
         }
         
         connection.commit();
         
         return slot;
         
      } catch (SQLException e) {
      
        // If it failed because it already exists, its prob a sibliing helping with the
        //  upload TODOTODOTODO ... this could get REAL ugly. 
         String sqlstate = e.getSQLState();
         if (sqlstate.equals("23505")) {
         
           // Try again
            if (pstmt!=null)  try {pstmt.close();} catch (SQLException ee) {}
            if (pstmt2!=null) try {pstmt2.close();} catch (SQLException ee) {}
            pstmt  = null;
            pstmt2 = null;
            DbConnect.returnConnection(connection);
            
            connection = null;
            return allocateFileSlot(user);
         }
      
         log.error("DB2DboxFileInfo:allocateFileSlot: SQL except doing: " + 
                   (sql==null?"":sql.toString()) + " sqlstate=[" + sqlstate + "]");         
                   
         DbConnect.destroyConnection(connection); connection = null;
         
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2DboxFileInfo:allocateFileSlots:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null)  try {pstmt.close();} catch (SQLException e) {}
         if (pstmt2!=null) try {pstmt2.close();} catch (SQLException e) {}
         
         DbConnect.returnConnection(connection);
      }
   }
   
   public void removeFileSlot(User user, long slotid) throws DboxException {
	   
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      StringBuffer sql = null;
      long fileid=getFileId();
      DboxFileSlot slot = null;
      try {
			
        // Get an already allocated but disowned or expired slot which still has
        //  room in it
        
         connection=DbConnect.makeSharedConn();
            
         fileManager.lockfile(fileid);
            
         sql = new StringBuffer("UPDATE EDESIGN.FILESLOT set (LOCKTIME, SESSIONID, SLOTSIZE) = (0,0,0) WHERE FILEID=?");
         
         if (slotid > -2) sql.append(" AND STARTOFS=?");
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1, fileid);
         
         if (slotid > -2) pstmt.setLong(2, slotid);
         
         int num = DbConnect.executeUpdate (pstmt);
         if (slotid > -2 && num != 1) {
            throw new DboxException("Error removing specified slot from DB: fileid[" + 
                                    fileid + "] slotid[" + slotid + "]");
         }
         
         connection.commit();
         
      } catch (SQLException e) {
         log.error("DB2DboxFileInfo:removeFileSlot: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2DboxFileInfo:removeFileSlot:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null)  try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
   
   public void releaseFileSlot(User user, long slotid) throws DboxException {
	   
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      StringBuffer sql = null;
      long fileid=getFileId();
      DboxFileSlot slot = null;
      try {
			
        // Get an already allocated but disowned or expired slot which still has
        //  room in it
        
         connection=DbConnect.makeSharedConn();
         
         fileManager.lockfile(fileid);
            
         sql = new StringBuffer("UPDATE EDESIGN.FILESLOT set (LOCKTIME, SESSIONID) = (0,0) WHERE FILEID=?");
         
         if (slotid > -2) sql.append (" AND STARTOFS=?");
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1, fileid);
         
         if (slotid > -2) pstmt.setLong( 2, slotid);
         
         int num = DbConnect.executeUpdate (pstmt);
         if (slotid > -2 && num != 1) {
            throw new DboxException("Error releasing specified slot from DB: fileid[" + 
                                    fileid + "] slotid[" + slotid + "] num = " + num);
         }
         
         connection.commit();
         
      } catch (SQLException e) {
         log.error("DB2DboxFileInfo:releaseFileSlot: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2DboxFileInfo:allocateFileSlots:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null)  try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
   
   public void deleteComponents()  throws DboxException {
      
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      StringBuffer sql = null;
      long fileid=getFileId();
		
      try{
			
         connection=DbConnect.makeSharedConn();
         
         fileManager.lockfile(fileid);
         
         forceSetSizeAndMD5Object(0, null);
         
         sql = new StringBuffer("SELECT COMPONENTSIZE,COMPINTENDEDSIZE, FILENAME, STARTOFS FROM EDESIGN.FILECOMPONENT fc WHERE fc.FILEID=? AND fc.DELETED is null");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1, fileid);
         
         rs=DbConnect.executeQuery (pstmt);
         int num = 0;
         
        // FIXNESTED FIXNESTED FIXNESTED 
         while (rs.next()){
            DboxFileComponent comp = new DB2DboxFileComponent(fileManager,
                                                              fileid, 
                                                              rs.getLong(1),
                                                              rs.getLong(2), 
                                                              rs.getLong(4),
                                                              rs.getString(3));
            comp.deleteFile();
            num++;
         }
         
         pstmt.close();
         
         sql = new StringBuffer("DELETE FROM EDESIGN.FILECOMPONENT fc WHERE fc.FILEID=? AND fc.DELETED is NULL");
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1,getFileId());
            
         if (DbConnect.executeUpdate(pstmt) != num) {
            throw new 
               DboxException("DB problem deleting FileComps != " + num +
                             " matches", 0);
         }
         
         pstmt.close();
         
         sql = new StringBuffer("DELETE FROM EDESIGN.FILESLOT fs WHERE fs.FILEID=?");
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1,getFileId());
         
         DbConnect.executeUpdate(pstmt);
         
         connection.commit();
        
      } catch (SQLException e) {
         log.error("DB2DboxFileInfo:deleteComponents: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2DboxFileInfo:deleteComponents:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
		  
   public void deleteComponent(long compid)  throws DboxException {
      
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      StringBuffer sql = null;
      long fileid=getFileId();
		
      try {
      
         connection=DbConnect.makeSharedConn();
         
         fileManager.lockfile(fileid);
         
         sql = new StringBuffer("SELECT COMPONENTSIZE,COMPINTENDEDSIZE, FILENAME,STARTOFS FROM EDESIGN.FILECOMPONENT fc WHERE fc.FILEID=? AND fc.DELETED is null");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1, fileid);
         
         rs=DbConnect.executeQuery (pstmt);
         
         if (!rs.next()){
            throw new DboxException(
               "Specified component to delete not found: fileid = " + 
               getFileId() + " compid = " + compid);
         }
         
         DboxFileComponent comp = new DB2DboxFileComponent(fileManager,
                                                           fileid, 
                                                           rs.getLong(1),
                                                           rs.getLong(2), 
                                                           rs.getLong(4),
                                                           rs.getString(3));
         
         pstmt.close();
         
         comp.deleteFile();
         
         
         sql = new StringBuffer("DELETE FROM EDESIGN.FILECOMPONENT fc WHERE fc.FILEID=? AND fc.STARTOFS=? AND fc.DELETED is NULL");
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1,getFileId());
         pstmt.setLong (2,comp.getStartingOffset());
            
         if (DbConnect.executeUpdate(pstmt) != 1) {
            log.error("Error deleting component: " + comp.toString());
            throw new 
               DboxException("DB problem deleting FileComp compid = " + 
                             comp.getComponentId() +
                             " for fileid = " + fileid);
         }
         
         pstmt.close();
         
         sql = new StringBuffer("DELETE FROM EDESIGN.FILESLOT fs WHERE fs.FILEID=? AND fs.COMPONENTID=?");
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1,getFileId());
         pstmt.setLong (2,comp.getStartingOffset());
            
         DbConnect.executeUpdate(pstmt);
         
        // If this component was included in slot0, then negate that info
        //TODOTODOTODOTODO ... is this right? why not calc MD5
         long sofs = comp.getStartingOffset();
         
        // TODOTODOTODO This filesize MAY be wrong BUT, routine not used .... fix
         if (sofs < getFileSize()) {
            forceSetSizeAndMD5Object(sofs, null);
         }
         
         connection.commit();
         
      } catch (SQLException e) {
         log.error("DB2DboxFileInfo:deleteComponent: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2DboxFileInfo:deleteComponent:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
                  
                  
  // This method will expand as well as truncate a file
   public void truncate(long filelen)  throws DboxException {
      
      if (filelen < 0) {
         throw new DboxException("Bad file length for truncate");
      }
                        
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      PreparedStatement pstmt2=null;
      ResultSet rs=null;
      ResultSet rs2=null;
      StringBuffer sql = null;
      long fileid=getFileId();
		
      try {
			
         connection=DbConnect.makeSharedConn();
         
         fileManager.lockfile(fileid);
         
        // Get the real file size as it exists NOW that its locked
         long filesz     = getFileSize();
         
         sql = new StringBuffer("SELECT FILESIZE FROM EDESIGN.FILE WHERE FILEID = ");
         sql.append(fileid);
         
         pstmt=connection.prepareStatement(sql.toString());
         
         rs=DbConnect.executeQuery (pstmt);
         
         if (!rs.next()) {
            throw new DboxException("File " + fileid + " no longers exists!");
         }
         
         filesz = rs.getLong(1);
         pstmt.close();
         
         
        // Its smaller, than the currently set value ...
        //
        // If we have any components/slots which are > filelen, ditch/truncate
         Vector components = getComponents();
         Iterator it = components.iterator();
         while(it.hasNext()) {
            DboxFileComponent comp = (DboxFileComponent)it.next();
            long sofs   = comp.getStartingOffset();
            long elenck = sofs +  comp.getFileIntendedSize();
            
            if (sofs >= filelen) {
            
              // If this component is not in play in the new file ... delete it
            
               deleteComponent(comp.getComponentId());
               
            } else if (filelen < elenck) {
               
              // The Component is at least somewhat in play, 
               
              // Delete all BAAAD fileslots
               sql = new StringBuffer("DELETE FROM EDESIGN.FILESLOT fs WHERE fs.FILEID=? AND fs.COMPONENTID=? AND fs.STARTOFS >= ?");
               pstmt=connection.prepareStatement(sql.toString());
               pstmt.setLong(1,getFileId());
               pstmt.setLong(2,comp.getStartingOffset());
               pstmt.setLong(3,filelen);
               
               DbConnect.executeUpdate(pstmt);
               
               pstmt.close();               
               
              // Update any fileslot which is straddling (should be a max of 1)
               sql = new StringBuffer("SELECT fs.SLOTSIZE, fs.INTENDEDSIZE, fs.STARTOFS FROM  EDESIGN.FILESLOT fs WHERE fs.FILEID=? AND fs.COMPONENTID = ? AND fs.STARTOFS < ? AND (fs.STARTOFS+fs.INTENDEDSIZE) > ?");
               
              // ISOLATION
               sql.append(DB2PackageManager.getROUR());
               
               pstmt=connection.prepareStatement(sql.toString());
               pstmt.setLong(1, getFileId());
               pstmt.setLong(2, comp.getStartingOffset());
               pstmt.setLong(3, filelen);
               pstmt.setLong(4, filelen);
               
               rs=DbConnect.executeQuery (pstmt);
               
              // FIXNESTED FIXNESTED FIXNESTED 
               while (rs.next()){
                  long slotsize     = rs.getLong(1);
                  long intendedsize = rs.getLong(2);
                  long startingofs  = rs.getLong(3);
                  
                  boolean doblob = (startingofs+slotsize) > filelen;
                  
                  intendedsize = filelen-startingofs;
                  if (doblob) {
                     slotsize  = intendedsize;
                  }
                  
                  if (doblob) {
                     sql = new StringBuffer("UPDATE EDESIGN.FILESLOT set (SLOTSIZE, INTENDEDSIZE, MD5BLOB) = (?, ?, ?)");
                  } else {
                     sql = new StringBuffer("UPDATE EDESIGN.FILESLOT set (SLOTSIZE, INTENDEDSIZE) = (?, ?)");
                  }
               
                  sql.append(" WHERE FILEID = ? AND STARTOFS=?");
               
                  pstmt2=connection.prepareStatement(sql.toString());
                  int idx = 1;
                  pstmt2.setLong(idx++,slotsize);
                  pstmt2.setLong(idx++,intendedsize);
                  if (doblob) {
                    // Generate the correct md5 for the slot
                     MessageDigestI md5obj = calculateAndReturnMD5(comp.getFullPath(), 
                                                                   startingofs-sofs, 
                                                                   slotsize);
                     try {
                        pstmt2.setBytes(idx++, md5obj.stateToBytes());
                     } catch(Exception ee) {
                        throw 
                           new DboxException("Error getting bytes from MD5 object", ee);
                     }
                  }
                  
                  pstmt2.setLong(idx++,fileid);
                  pstmt2.setLong(idx++,startingofs);
                  
                  if (DbConnect.executeUpdate(pstmt2) != 1) {
                     throw new DboxException("Error while truncating file: " +
                                             "fileid = " + getFileId() +
                                             "compid = " + comp.getComponentId() +
                                             "slotid = " + startingofs); 
                  }
                  pstmt2.close();
               }
               
               pstmt.close();
               
               if (log.isDebugEnabled()) {
                  log.warn("Truncating file to len=" + filelen + ":\n" + toString());
               }
               long compintendedsize = filelen-sofs;
               long compsize         = comp.getFileSize();
               
              // Truncate file and free space. This sets size and intendedsize for the
              //  component
               if (log.isDebugEnabled()) {
                  log.debug("calling truncate component with compsize = " + compsize);
               }
               
               comp.truncateFile(compintendedsize);
               
              // Now fix COMP sizes and MD5 ... redundent on comp sizes done with 
              //  truncate above
              //
              // Null the MD5 value out for the component. This will be recalculated
              //  either below (if filesize had changed <not intendedFileSize>) or 
              //  upon next slot cull
               sql = new StringBuffer("UPDATE EDESIGN.FILECOMPONENT set (COMPONENTSIZE, COMPINTENDEDSIZE, MD5BLOB) = (?, ?, ?)");
               
               sql.append(" WHERE FILEID = ? AND STARTOFS = ?");
               
               pstmt=connection.prepareStatement(sql.toString());
               pstmt.setLong(1, compsize);
               pstmt.setLong(2, compintendedsize);
               pstmt.setBlob(3, null);
               pstmt.setLong(4, getFileId());
               pstmt.setLong(5, comp.getStartingOffset());
               
               if (DbConnect.executeUpdate(pstmt) != 1) {
                  throw new DboxException("Error while truncating comp for file: " +
                                          "fileid = " + getFileId() +
                                          "compid = " + comp.getComponentId());
               }
            }
         }
                        
        // Set the len to DB last, after other truncate operations occur
         setFileIntendedSize(filelen);
         
        // If filelen needs to change, set the filesize and file MD5
         if (filelen < filesz) {
            forceSetFileSize(filelen);
         }
         
        // cull any slots that are now cullable
         cullSlots();
         
        // Fixup MD5 values and filesizes on components and MD5 on File object
         try {
            calculateAndReturnMD5((filesz < filelen)?filesz:filelen, true);
         } catch(Exception ee) {
            throw new DboxException("Error calculating MD5", ee);
         }
         
         
         connection.commit();
      
      } catch (SQLException e) {
         log.error("DB2DboxFileInfo:truncate: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2DboxFileInfo:truncate:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         if (pstmt2!=null) try {pstmt2.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
                  
      
   public void recalculateFileSize() throws DboxException { 
      
      
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      StringBuffer sql = null;
      long fileid=getFileId();
      
      try{
			
         connection=DbConnect.makeSharedConn();
         
         fileManager.lockfile(fileid);
         
        // FSZCHANGE Used to have filesize now calculate it
        // sql = new StringBuffer("SELECT FILESIZE FROM EDESIGN.FILE WHERE FILEID=?");
         sql = new StringBuffer("SELECT VALUE(SUM(fc.COMPONENTSIZE),0), VALUE(COUNT(*), 0) from EDESIGN.FILECOMPONENT fc where fc.FILEID=? AND fc.DELETED is NULL");
         
        
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1,getFileId());
         
         rs=DbConnect.executeQuery(pstmt);
         if (rs.next()){
            filesize=rs.getLong(1);	
            int numentry=rs.getInt(2);	
            
            pstmt.close();
            sql = new StringBuffer("UPDATE EDESIGN.FILE SET FILESIZE=?,COMPONENTENTRY=? WHERE FILEID=? AND DELETED is null");
            pstmt=connection.prepareStatement(sql.toString());
            pstmt.setLong(1, filesize);
            pstmt.setInt (2, numentry);
            pstmt.setLong(3, fileid);
            
            if (DbConnect.executeUpdate(pstmt) != 1) {
               DboxAlert.alert(DboxAlert.SEV3, 
                               "executeUpdate != " + 1, 
                               0, 
                               "update file stats for file : " + 
                               fileid + " failed");
            }
         }
         
         connection.commit();
         
      } catch (SQLException e) {
         log.error("DB2DboxFileInfo:recalcFilesize: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2DboxFileInfo:recalcFilesize:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
      
      
   public DboxFileComponent getComponent(long idx) throws DboxException {
      
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      StringBuffer sql = null;
      long fileid=getFileId();
      long intendedsize=0;
      long filesize=0;
      long sofs = 0;
      String filename=null;
      byte[] md5blob = null;
      DboxFileComponent filecomponent = null;
      try{
			
         connection=DbConnect.makeSharedConn();
         
         sql = new StringBuffer("SELECT COMPINTENDEDSIZE, FILENAME, COMPONENTSIZE,  MD5BLOB, STARTOFS  FROM EDESIGN.FILECOMPONENT fc ");
         sql.append ("WHERE fc.FILEID=? AND STARTOFS=? AND fc.DELETED is null");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1,getFileId());
         pstmt.setLong(2,idx); 
		 
         rs=DbConnect.executeQuery(pstmt);
         if (!rs.next()){
            throw new DboxException("Nonexistant component");
         }
         intendedsize=rs.getLong(1);
         filename=rs.getString(2);
         filesize=rs.getLong(3);
         md5blob = rs.getBytes(4);  
         sofs = rs.getLong(5);  
         
         filecomponent=new DB2DboxFileComponent(fileManager, fileid, 
                                                filesize, intendedsize, 
                                                sofs,
                                                filename);
         try{
            
            filecomponent.setMD5blobBytes(md5blob);
            
         }catch(Exception ex){
           //Blob might be empty
         }                                                
         
         filecomponent.threadedCloseWillHelp(
         filecomponent.getFullPath().startsWith("/afs"));
			
         connection.commit();
         
      } catch (SQLException e) {
         log.error("DB2DboxFileInfo:getComponent: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2DboxFileInfo:getComponent:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
		 
      return filecomponent;
   }
	   
   public DboxFileComponent getComponentContainingOffset(long ofs) throws DboxException {
      
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      StringBuffer sql = null;
      long fileid=getFileId();
      long intendedsize=0;
      long filesize=0;
      long sofs = 0;
      String filename=null;
      byte[] md5blob = null;
      DboxFileComponent filecomponent = null;
      try{
			
         connection=DbConnect.makeSharedConn();
         
         sql = new StringBuffer("SELECT COMPINTENDEDSIZE, FILENAME, COMPONENTSIZE,  MD5BLOB, STARTOFS FROM EDESIGN.FILECOMPONENT fc ");
         sql.append ("WHERE fc.FILEID=? AND STARTOFS <= ? AND STARTOFS+COMPINTENDEDSIZE > ?  AND fc.DELETED is null");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1,getFileId());
         pstmt.setLong(2,ofs); 
         pstmt.setLong(3,ofs); 
		 
         rs=DbConnect.executeQuery(pstmt);
         if (!rs.next()){
            throw new DboxException("Nonexistant component");
         }
         intendedsize=rs.getLong(1);
         filename=rs.getString(2);
         filesize=rs.getLong(3);
         md5blob = rs.getBytes(4);  
         sofs = rs.getLong(5);  
         
         filecomponent=new DB2DboxFileComponent(fileManager, fileid, 
                                                filesize, intendedsize, 
                                                sofs,
                                                filename);
         try{
            
            filecomponent.setMD5blobBytes(md5blob);
            
         }catch(Exception ex){
           //Blob might be empty
         }                                                
         
         filecomponent.threadedCloseWillHelp(
         filecomponent.getFullPath().startsWith("/afs"));
         
         if (rs.next()) {
            DboxAlert.alert(DboxAlert.SEV2, 
                            "File has multiple comps contain offset!", 
                            0, 
                            "Multiple components contain same offset!: \n" + 
                            this.toString());
         }
			
         connection.commit();
                        
      } catch (SQLException e) {
         log.error("DB2DboxFileInfo:getComponentContainingOfs: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2DboxFileInfo:getComponentContainingOfs:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
		 
      return filecomponent;
   }
           
      
  // This routine is used only in OLD dropbox. The filesize COULD be wrong
  //  but that is single threaded, so SHOULD be right.
   public DboxFileComponent createComponent() throws DboxException  {
      return createComponent(getFileSize());
   }
   
   public DboxFileComponent createComponent(long startingOfs) throws DboxException  {
		  
      DB2DboxFileComponent ret = null;
      
      SharedConnection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;
      StringBuffer sql = null;
      long fileid=getFileId();
      long csize=0;
      try{
			
         connection=DbConnect.makeSharedConn();
         
         fileManager.lockfile(fileid);
			
         csize = intendedSize-startingOfs;
                  
        // If we are doing sftp/web ... anything special ... not right now 
        // if (intendedSize == 0x7fffffffffffffff) {
        // }
         
        // If we are making slots/components equal, then csize must be <= maxslotsize. 
        //  This would be true MOSTLY to allow multiple channels where simultaneous
        //  file access on multiple machines was slow or flakey (yes and yes with
        //  afs!).
         if (slots_equal_components) {
         
           // If we want to allow variable component sizes, make component
           //  smaller for smaller files. This is only interesting in the case
           //  where we have to force slots equal to components to support
           //  multichannel uploads.
            if (variable_component_size) {
               long vnum = csize / max_num_active_slots;
               if (vnum > min_component_size) {
                  csize = vnum;
               } else {
                  csize = min_component_size;
               }
            }
            
            if (csize > max_file_slot_size) csize = max_file_slot_size;
         }
         
         ret = new DB2DboxFileComponent(fileManager, getFileId(), 
                                        0, csize, 
                                        startingOfs, "none");
                                        
        // This method will apply the max component size check
         ret.acquireSpace(getPoolId());
         
        // Register the component on the shared connection. If we get rolled back, it
        //  SHOULD return the space we just allocated. This will survive till the 
        //  entire transaction is committed.
         connection.addRollback(ret);
         
         ret.threadedCloseWillHelp(ret.getFullPath().startsWith("/afs"));
         
         sql = new StringBuffer("INSERT INTO EDESIGN.FILECOMPONENT (FILEID, FILENAME,");
         sql.append("COMPONENTSIZE, COMPINTENDEDSIZE, STARTOFS ) VALUES (?,?,?,?,?)");
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1,getFileId());
         pstmt.setString(2,ret.getFullPath ());
         pstmt.setLong(3,0);
         pstmt.setLong(4,ret.getFileIntendedSize());
         pstmt.setLong(5, startingOfs);
            
         if (DbConnect.executeUpdate(pstmt) != 1) {
            log.error("\nError inserting new component:\n" + ret.toString());
            log.error("\nFor                          :\n" + ret.toString());
            throw new DboxException("DB problem createComponent insert failed", 0);
         }
         
         pstmt.close();
         
         sql = new StringBuffer("UPDATE EDESIGN.FILE SET COMPONENTENTRY=COMPONENTENTRY+1 WHERE FILEID = ?");
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setLong(1, getFileId());
         if (DbConnect.executeUpdate(pstmt) != 1) {
            log.error("Error updating component entry: \n" + toString());
            throw new DboxException("Error updating componententry for File", 0);
         }
         
         connection.commit();
         
      } catch (SQLException e) {
         
        // If it failed because it already exists, its prob a sibliing helping with the
        //  upload
         String sqlstate = e.getSQLState();
         if (sqlstate.equals("23505")) {
            ret.returnSpace();
            ret.setFileIntendedSize(0); // In case Rollback occurs ... 
            
            DboxFileComponent comp = getComponent(startingOfs);
            log.warn("\n\nComponent Conflict\n\n");
            log.warn(comp.toString());
            return comp;
         }
         
         log.error("DB2DboxFileInfo:createComponent: SQL except doing: " + 
                   (sql==null?"":sql.toString()));         
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("DB2DboxFileInfo:createComponent:>> SQL exception",
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         DbConnect.returnConnection(connection);
      }
      
      return ret;
   }
   
  // This routine calculates the MD5 for the specific segment of a file
   protected MessageDigestI calculateAndReturnMD5(String file, 
                                                  long ofs, 
                                                  long len) throws DboxException {
      
      MessageDigestI digest = new DropboxFileMD5();
      RandomAccessFile  fis = null;
      try {
         long left = len;
         byte buf[] = new byte[32768];
         
         fis = new RandomAccessFile(file, "r");
         
        // Lock shared 
         if (DboxFileSlot.do_record_locking) {
            FileLock flock = fis.getChannel().lock(ofs, len, true);
         }
         
         fis.seek(ofs);
         
        // Read all appropriate data and MD5 it
         while(left > 0) {
            
            int r = (int)(left > buf.length ? buf.length : left);
            
            r = fis.read(buf, 0, r);
            if (r == -1) {
               throw new DboxException("Ran out of bytes while calculating MD5");
            }
            
            digest.update(buf, 0, r);
            
            left -= r;
         }
         
        // Try to ensure that the data is written to disk. Perhaps this will ensure that
        //  NFS will be properly in sync for remote files.
         fis.getChannel().force(true);
         
      } catch(DboxException de) {
         throw de;
      } catch(Exception e) {
         throw new DboxException("Error generating MD5", e);
      } finally {
         try { if (fis != null) fis.close(); } catch(Exception fisce) {}
      }
      
      return digest;
   }
   
   public String toString(){
      String ret="DB2DboxFileInfo: " + super.toString();
      return ret;
   }
}
   
