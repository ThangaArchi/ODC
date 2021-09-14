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
import java.sql.*;
import org.apache.log4j.Logger;

/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2006                                          */
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

public class DB2DboxFileSlot extends DboxFileSlot {
      
   public DB2DboxFileSlot() { 
      super(); 
   }
      
   public DB2DboxFileSlot(DB2DboxFileSlot fs) {
      super(fs);
   }
      
   public DB2DboxFileSlot(FileManager fm, 
                          long fid,
                          long slotid, 
                          long size, 
                          long intendedSize,
                          long startofs, 
                          long sessionid,
                          long compid, 
                          long locktime) {
      super(fm, fid, slotid, size, intendedSize, startofs, sessionid, 
            compid, locktime);
   }
   
   
   public boolean forceSetLength(long len) throws DboxException {
      try {
      
         boolean released = false;
         SharedConnection connection=null;
         PreparedStatement pstmt=null;
         ResultSet rs=null;
         StringBuffer sql = null;
         int num = 0;
         try {
            
            connection=DbConnect.makeSharedConn();
            
           // lock on the file to be sure
            fileManager.lockfile(getFileId());
            
           // Set size and keep owner if more room
            sql = new StringBuffer("UPDATE EDESIGN.FILESLOT SET SLOTSIZE=? ");
            sql.append("WHERE FILEID = ? AND STARTOFS=? AND INTENDEDSIZE > ?");
            pstmt=connection.prepareStatement(sql.toString());
            pstmt.setLong(1, len);
            pstmt.setLong(2, getFileId());
            pstmt.setLong(3, getSlotId());
            pstmt.setLong(4, len);
            
            num += DbConnect.executeUpdate(pstmt);
            
            pstmt.close();
            
            if (num == 0) {
            
              // Set size and release owner if full
               sql = new StringBuffer("UPDATE EDESIGN.FILESLOT SET SLOTSIZE=?,SESSIONID=0 ");
               sql.append("WHERE FILEID = ? AND STARTOFS=? AND INTENDEDSIZE = ?");
               pstmt=connection.prepareStatement(sql.toString());
               pstmt.setLong(1, len);
               pstmt.setLong(2, getFileId());
               pstmt.setLong(3, getSlotId());
               pstmt.setLong(4, len);
               
               num += DbConnect.executeUpdate(pstmt);
               if (num > 0) {
                  released = true;
                  log.debug("UPDATED SLOT, RELEASED!");
               }
            } else {
               log.debug("UPDATED SLOT, NO RELEASE");
            }
            
           // If we did NOT update things once only, then error
            if (num != 1) {
               throw new 
                  DboxException("DB problem updating fileslot length. num="+num, 0);
            }
            
            connection.commit();
            setLength(len);
            return released;
            
         } catch (SQLException e) {
            log.error("DB2DboxFileSlot:forceSetLength: SQL except doing: " + 
                      (sql==null?"":sql.toString()));         
            DbConnect.destroyConnection(connection); connection=null; pstmt=null;
            DboxAlert.alert(e);  // Generic Alert
            throw new 
               DboxException("DB2DboxFileSlot:forceSetLength:>> SQL exception",
                             DboxException.SQL_ERROR);
         } finally {
            if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
            DbConnect.returnConnection(connection);
         }
         
      } catch(Exception e) {      
         throw new DboxException("Error setting slot length: slot=" + 
                                 getSlotId() + " len=" + len, e);
      }
   }
   
   public void forceSetMD5Object(MessageDigestI v) throws DboxException {
   
      try {
      
         SharedConnection connection=null;
         PreparedStatement pstmt=null;
         ResultSet rs=null;
         StringBuffer sql = null;
         int num = 0;
         try {
            
            String blobattrib = (v == null)?"null":"?";
            
            connection=DbConnect.makeSharedConn();
            
           // lock on the file to be sure
            fileManager.lockfile(getFileId());
            
           // Set size and keep owner if more room
            sql = new StringBuffer("UPDATE EDESIGN.FILESLOT SET MD5BLOB=");
            
            sql.append(blobattrib);
            sql.append(" WHERE FILEID = ? AND STARTOFS=? ");
            pstmt=connection.prepareStatement(sql.toString());
            
            int idx = 1;
            if (v != null) pstmt.setBytes(idx++, v.stateToBytes());
            
            pstmt.setLong(idx++, getFileId());
            pstmt.setLong(idx++, getSlotId());
            
            num += DbConnect.executeUpdate(pstmt);
            
           // If we did NOT update things once only, then error
            if (num != 1) {
               throw new 
                  DboxException("DB problem updating slot md5 length. num="+num, 0);
            }
            
            connection.commit();
            
         } catch (SQLException e) {
            log.error("DB2DboxFileSlot:forceSetMD5: SQL except doing: " + 
                      (sql==null?"":sql.toString()));         
            DbConnect.destroyConnection(connection); connection=null; pstmt=null;
            DboxAlert.alert(e);  // Generic Alert
            throw new 
               DboxException("DB2DboxFileSlot:forceSetMD5:>> SQL exception",
                             DboxException.SQL_ERROR);
         } finally {
            if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
            DbConnect.returnConnection(connection);
         }
         
         setMD5Object(v);
         
      } catch(Exception e) {      
         throw new DboxException("Error setting slot MD5: slot=" + 
                                 getSlotId(), e);
      }
   }
   
   public boolean forceSetLengthAndMD5Object(long len, MessageDigestI v) throws DboxException {
   
      try {
         
         boolean released = false;
         SharedConnection connection=null;
         PreparedStatement pstmt=null;
         ResultSet rs=null;
         StringBuffer sql = null;
         int num = 0;
         try {
            
            String blobattrib = (v == null)?"null":"?";
            
            connection=DbConnect.makeSharedConn();
            
           // lock on the file to be sure
            fileManager.lockfile(getFileId());
            
           // Set size and keep owner if more room
            sql = new StringBuffer("UPDATE EDESIGN.FILESLOT SET SLOTSIZE=?,MD5BLOB=");
            
            sql.append(blobattrib);
            sql.append(" WHERE FILEID = ? AND STARTOFS=? AND INTENDEDSIZE > ?");
            pstmt=connection.prepareStatement(sql.toString());
            
            int idx = 1;
            pstmt.setLong(idx++, len);
            if (v != null) pstmt.setBytes(idx++, v.stateToBytes());
            
            pstmt.setLong(idx++, getFileId());
            pstmt.setLong(idx++, getSlotId());
            pstmt.setLong(idx++, len);
            
            num += DbConnect.executeUpdate(pstmt);
            if (num == 0) {
              // Set size and release owner if full
               sql = new StringBuffer("UPDATE EDESIGN.FILESLOT SET SLOTSIZE=?,SESSIONID=0,MD5BLOB=");
               sql.append(blobattrib);
               sql.append(" WHERE FILEID = ? AND STARTOFS=? AND INTENDEDSIZE = ?");
               pstmt=connection.prepareStatement(sql.toString());
               idx = 1;
               pstmt.setLong(idx++, len);
               if (v != null) pstmt.setBytes(idx++, v.stateToBytes());
               pstmt.setLong(idx++, getFileId());
               pstmt.setLong(idx++, getSlotId());
               pstmt.setLong(idx++, len);
               
               num += DbConnect.executeUpdate(pstmt);
               if (num > 0) {
                  released = true;
                  log.debug("UPDATED SLOT, RELEASED!");
               }
            } else {
               log.debug("UPDATED SLOT, NO RELEASE");
            }
               
           // If we did NOT update things once only, then error
            if (num != 1) {
               throw new 
                  DboxException("DB problem updating fileslot length&MD5 num="+num+"\n\n" 
                                + toString(), 0);
            }
            
            connection.commit();
            setLengthAndMD5Object(len, v);
            
            return released;
            
         } catch (SQLException e) {
            log.error("DB2DboxFileSlot:forceSetLengthAndMD5: SQL except doing: " + 
                      (sql==null?"":sql.toString()));         
            DbConnect.destroyConnection(connection); connection=null; pstmt=null;
            DboxAlert.alert(e);  // Generic Alert
            throw new 
               DboxException("DB2DboxFileSlot:forceSetLengthAndMD5:>> SQL exception",
                             DboxException.SQL_ERROR);
         } finally {
            if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
            DbConnect.returnConnection(connection);
         }
         
      } catch(Exception e) {      
         throw new DboxException("Error setting slot length and md5: slot=" + 
                                 getSlotId() + " len=" + len, e);
      }
   }
   
   public RandomAccessFile getUploader() throws DboxException {
      try {
         DboxFileInfo info = fileManager.lookupFile(getFileId());
         DboxFileComponent comp = info.getComponent(componentid);
         String fname = comp.getFullPath();
         RandomAccessFile rf = new RandomAccessFile(fname, "rw");
         long fofs = (getStartingOffset() - comp.getStartingOffset());
         rf.seek(fofs + getLength());
         
        // If we are supposed to do record locking, then do so
         if (do_record_locking) {
//            long st = System.currentTimeMillis();
            FileLock flock = rf.getChannel().lock(rf.getFilePointer(), 
                                                  getRemainingBytes(), false);
//            long et = System.currentTimeMillis();
//            log.debug("Lock obtained in ( " + (et-st) + ") [" + 
//                      rf.getFilePointer() + "] [" + 
//                      (rf.getFilePointer() + getRemainingBytes()) + "]");
         }
         return rf;
      } catch(Exception e) {      
         throw new DboxException("Error creating Uploader for slot", e);
      }
   }
   
}
