package oem.edge.ed.odc.dropbox.server;

import  oem.edge.ed.odc.dropbox.common.*;
import  oem.edge.ed.odc.ftp.common.*;
import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.dsmp.server.*;
import  oem.edge.ed.odc.util.*;
import  oem.edge.ed.odc.util.db.SharedConnectionProxy;

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

public class DB2DboxFileAllocator extends DboxFileAllocator {
   static Logger log = Logger.getLogger(DB2DboxFileAllocator.class.getName());

   public DB2DboxFileAllocator() {}

   public SpaceAllocation allocateSpace(long intendedSize, long poolid)
      throws DboxException {
            
      int iteration = 0;
      DboxSpaceAllocation ret = null;
      
      while (true) {
      
         PreparedStatement pstmt1 = null;
         PreparedStatement pstmt2 = null;
         ResultSet rs1=null;
         SharedConnection connection=null;
         SharedConnectionProxy savedconnection=null;
         StringBuffer sql = null;
         try{
         
           // Don't use shared connections
            savedconnection = DbConnect.saveSharedConn();
            connection=DbConnect.makeSharedConn();
            
            sql = new StringBuffer("SELECT INPOOL,PRIORITY,DIRECTORY,FSTYPE,MAXSPACE,USEDSPACE FROM EDESIGN.FILEALLOCATION WHERE INPOOL=1 AND POOLID = ? order by PRIORITY FOR UPDATE WITH RS");
            pstmt1=connection.prepareStatement(sql.toString()); 
         
            pstmt1.setInt(1, (int)poolid);
            
            rs1=DbConnect.executeQuery(pstmt1);
         
            Vector fas = new Vector();
         
            while (rs1.next()) {
               boolean b = rs1.getInt(1) != 0;
               int     p = rs1.getInt(2);
               String  f = rs1.getString(4);
               DboxFileArea a = new DboxFileArea(rs1.getString(3), 
                                                 rs1.getLong(5), 
                                                 rs1.getLong(6));
               a.setPriority(p);
               a.setState(b?a.STATE_NORMAL:a.STATE_BROKEN);
               if        (f.equalsIgnoreCase("AFS")) {
                  a.setFSType(a.FSTYPE_AFS); 
                  fas.addElement(a);
               } else if (f.equalsIgnoreCase("JFS")) {
                  a.setFSType(a.FSTYPE_JFS);
                  fas.addElement(a);
               } else {
                  DboxAlert.alert(DboxAlert.SEV2, 
                                  "Unknown Filesystem type in DBox pool",
                                  0, 
                                  "Type in DB2 is [" + f + "]\n" + a.toString());
               }
            }   
         
            if (fas.size() == 0) {
               DboxAlert.alert(DboxAlert.SEV1, 
                               "No valid areas for Dropbox store",
                               0, 
                               "Query of FILEALLOCATION yields no valid\n" + 
                               "FileArea's for allocation poolid: " + poolid);
            
               throw new DboxException("No valid FileAreas for alloc in pool " + poolid, 0);
            }
         
            ret = selectByPolicy(fas, intendedSize);
         
            if (ret != null) {
               String dir  = ret.getFileArea().getTopLevelDirectory();
               sql = new StringBuffer("UPDATE EDESIGN.FILEALLOCATION SET USEDSPACE=USEDSPACE+? WHERE DIRECTORY=?");
               pstmt2=connection.prepareStatement(sql.toString()); 
               pstmt2.setLong(1, ret.getSize());
               pstmt2.setString(2, dir);
            
               if (DbConnect.executeUpdate(pstmt2) != 1) {
                  throw new
                     DboxException("FileAllocator:>> DB Error adjusting space",
                                   0);
               }
               
            } else {
            
               pstmt1.close(); pstmt1 = null;
               
               connection.rollback();
              
               DbConnect.returnConnection(connection);
               connection = null;
            
              // Try up to 3 times to get space via reclaim
               if (iteration < 3) {
                  iteration++;
                  log.warn("DB2DboxFileAllocation - Not enough space, try clean iter " + iteration);
                  
                  try {
                     FileManager fileManager = packageManager.getFileManager();
                    // free 100 or so packages and 500 or so files to give us a chance
                    //  but also prevent ME from being the 30GIG multi thousand file
                    //  reclaimer. Hey, fair is fair, I only need 50Meg or so.
                     int numpack=packageManager.cleanExpiredPackages(100);
                     int numfile=fileManager.cleanUnreferencedFiles(500);
                     continue;
                  } catch(DboxException dex) {}
               }
            
               DboxAlert.alert(DboxAlert.SEV1, 
                               "Not enough space for upload to Dropbox",
                               0, 
                               "Not enough space to allocate [" + intendedSize + 
                               "] bytes for Dropbox upload");
               throw new DboxException("Not enough space left to alloc" + 
                                       intendedSize + " bytes", 0);
            }
         
            pstmt1.close(); pstmt1 = null;
            pstmt2.close(); pstmt2 = null;
         
            connection.commit();
            break;
         
         } catch (SQLException e) {
            log.error("DB2DboxFileAllocator.allocateSpace: SQL except doing: "
                      + (sql==null?"":sql.toString()));
            DbConnect.destroyConnection(connection); connection=null; 
            DboxAlert.alert(e);  // Generic Alert
            throw new 
               DboxException("DB2DboxFileAllocator:>> SQL exception allocating space",
                             0);
         } finally {
            if (pstmt1 != null) try {pstmt1.close();} catch(SQLException e) {}
            if (pstmt2 != null) try {pstmt2.close();} catch(SQLException e) {}
            DbConnect.returnConnection(connection); // This will do rollback
            DbConnect.restoreSharedConn(savedconnection);
         }
      }
   
      return ret;
   }
   
   public void returnSpace(String dir, long space) throws DboxException {
   
      PreparedStatement pstmt = null;
      ResultSet rs=null;
      Connection connection=null;
      StringBuffer sql = null;
      try{
        // Don't use shared connections
         connection=DbConnect.makeConn();
         sql = new StringBuffer("UPDATE EDESIGN.FILEALLOCATION SET USEDSPACE=USEDSPACE-? WHERE DIRECTORY=?");
         pstmt=connection.prepareStatement(sql.toString()); 
         pstmt.setLong(1, space);
         pstmt.setString(2, dir);
         
         if (DbConnect.executeUpdate(pstmt) != 1) {
            DboxAlert.alert(DboxAlert.SEV1, 
                            "Error adding  bytes back to FILEALLOCATION tab",
                            0, 
                            "Error adding " + space + 
                            " bytes back to FILEALLOCATION tab for dir " + 
                            dir);
            throw new DboxException("Error returning space[" + space + 
                                    "] to " + dir, 0);
         }
         
        //connection.commit();
         
      } catch (SQLException e) {
         DboxAlert.alert(DboxAlert.SEV1, 
                         "Error adding  bytes back to FILEALLOCATION tab",
                         0, 
                         "Error adding " + space + 
                         " bytes back to FILEALLOCATION tab for dir " + dir +
                         ".\nSQL = " + (sql==null?"":sql.toString()),
                         e);
         DbConnect.destroyConnection(connection); connection=null; pstmt=null;
         
         throw new DboxException(
            "DB2DboxFileAllocator.returnSpace:>> SQLError returning space[" + 
            space + "] to " + dir, DboxException.SQL_ERROR);
            
      } finally {
         if (pstmt != null) try {pstmt.close();} catch(SQLException e) {}
         DbConnect.returnConnection(connection);
      }
   }
   
   public Vector getFileAreas() throws DboxException {
      Vector ret = new Vector();
      PreparedStatement pstmt = null;
      ResultSet rs=null;
      SharedConnection connection=null;
      StringBuffer sql = null;
      try{
         connection=DbConnect.makeSharedConn();
         sql = new StringBuffer("SELECT INPOOL,PRIORITY,DIRECTORY,FSTYPE,MAXSPACE,USEDSPACE FROM EDESIGN.FILEALLOCATION ORDER BY PRIORITY");
         
        // ISOLATION
         sql.append(DB2PackageManager.getROUR());
         
         pstmt=connection.prepareStatement(sql.toString()); 
         
         rs=DbConnect.executeQuery(pstmt);
         
         while (rs.next()) {
            
            boolean b = rs.getInt(1) != 0;
            int     p = rs.getInt(2);
            String  f = rs.getString(4);
            DboxFileArea a = new DboxFileArea(rs.getString(3), 
                                              rs.getLong(5), 
                                              rs.getLong(6));
            a.setPriority(p);
            a.setState(b?a.STATE_NORMAL:a.STATE_BROKEN);
            if        (f.equalsIgnoreCase("AFS")) {
               a.setFSType(a.FSTYPE_AFS); 
            } else if (f.equalsIgnoreCase("JFS")) {
               a.setFSType(a.FSTYPE_JFS);
            } else {
               DboxAlert.alert(DboxAlert.SEV2, 
                               "Unknown Filesystem type in DBox pool",
                               0, 
                               "Type in DB2 is [" + f + "]\n" + a.toString());
            }
            ret.addElement(a);
         }
         
         pstmt.close();
         pstmt = null;
         
        // Fill in the component intended size info
         Iterator it = ret.iterator();
         while(it.hasNext()) {
            DboxFileArea fa = (DboxFileArea)it.next();
            
            sql = new StringBuffer(
               "SELECT VALUE(SUM(COMPINTENDEDSIZE), 0) FROM EDESIGN.FILECOMPONENT WHERE DELETED is NULL AND FILENAME LIKE '"
               );
            sql.append(fa.getTopLevelDirectory());
            
           // indexOf on StringBuffer is 1.4 ... sigh
            if (sql.toString().indexOf("\\") < 0) sql.append("/%'");
            else                                  sql.append("\\%'");
            
           // ISOLATION
            sql.append(DB2PackageManager.getROUR());
            
            pstmt=connection.prepareStatement(sql.toString()); 
         
            rs=DbConnect.executeQuery(pstmt);
         
            if (rs.next()) {
               fa.setComponentAllocation(rs.getLong(1));
            }
            
            pstmt.close();
            pstmt = null;
            
            connection.commit();
         }
         
         connection.commit();
         
      } catch (SQLException e) {

         DboxAlert.alert(DboxAlert.SEV1, 
                         "Error getting file areas in fileAllocator", 
                         0, 
                         "DB2DboxFileAllocator.getFileAreas - Error " +
                         "\nSQL = " + (sql==null?"":sql.toString()),
                         e);
         DbConnect.destroyConnection(connection);
         connection=null; pstmt = null;
         
         throw new 
            DboxException("DB2DboxFileAllocator:getFileAreas:>> SQL error", 0);
      } finally {
         if (pstmt != null) try {pstmt.close();} catch(SQLException e) {}
         DbConnect.returnConnection(connection);
      }
      
      return ret;
   }
   
   public String toString() {
      return super.toString() + "\n\tDB2FileAllocator. TODO";
   }
}
