package oem.edge.ed.odc.dropbox.server;

import  oem.edge.ed.odc.dropbox.common.*;
import  oem.edge.ed.odc.ftp.common.*;
import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.dsmp.server.*;
import  oem.edge.ed.odc.util.*;

import java.lang.*;
import java.io.*;
import java.util.*;
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

public abstract class DboxPackageInfo extends PackageInfo {
   
   PackageManager packageManager = null;
   
   DboxPackageInfo(PackageManager pm) {
      packageManager = pm;
   }
   
   DboxPackageInfo(DboxPackageInfo in) {
      super(in);
      packageManager = in.packageManager;
      country = in.country;
   }
   
   public abstract DboxPackageInfo cloneit();
         
   String country = "";
   
   public String  getPackageCountry()      { return country==null?"":country; }
   public void    setPackageCountry  (String c) { country = c;      }
   
   
  // These are just aliases for the PackageInfo meths (which were added later)
   public void setCommitTime(long msec) { setPackageCommitted(msec) ;   }
   public long getCommitTime()          { return getPackageCommitted(); }
      
   abstract Vector getFiles() throws DboxException;
   abstract void addFile(DboxFileInfo info) throws DboxException;
   abstract void removeFile(long itemid) throws DboxException;
   abstract public void recalculatePackageSize() throws DboxException;
      
   synchronized boolean includesFile(long itemid) throws DboxException {
      try {
         getFile(itemid);
      } catch(DboxException dbex) {
         if (dbex.getErrorCode() == dbex.SQL_ERROR) throw dbex;
         return false;
      }
      return true;
   }
   
   synchronized boolean includesFile(String name) throws DboxException {
      try {
         getFile(name);
      } catch(DboxException dbex) {
         if (dbex.getErrorCode() == dbex.SQL_ERROR) throw dbex;
         return false;
      }
      return true;
   }
      
   abstract DboxFileInfo getFile(String name) throws DboxException;
   abstract DboxFileInfo getFile(long itemid) throws DboxException;
      
   abstract void addProjectAcl(String name) throws DboxException;
   abstract void removeProjectAcl(String name) throws DboxException;
   abstract void addGroupAcl(String name) throws DboxException;
   abstract void removeGroupAcl(String name) throws DboxException;
   abstract void addUserAcl(String name) throws DboxException;
   abstract void removeUserAcl(String name) throws DboxException;
   abstract Vector getFileAcls(long fileid) throws DboxException;
   abstract void addFileAccessRecord(User user, 
                                     long fileid, 
                                     byte status,
                                     int xferate) 
                            throws DboxException;
                            
   abstract void removeFileAccessRecord(User user, long fileid) 
      throws DboxException;
      
  // only filled out by DB2 subclass ... SHOULD be abstract, but we are
  //  abandoning the rest anyhow
   AclInfo getFileAccessRecord(String user, long fileid) 
      throws DboxException {
      return null;
   }
      
   abstract Vector getPackageAcls(boolean staticOnly) throws DboxException;
      
   public boolean canAccessPackage(User user, boolean includeOwner) {
      try {
         getUserAccess(user, includeOwner);
         return true;
      } catch(DboxException ee) {}
      return false;
   }
      
   public abstract AclInfo getUserAccess(User user, 
                                         boolean includeOwner) 
      throws DboxException;
}
