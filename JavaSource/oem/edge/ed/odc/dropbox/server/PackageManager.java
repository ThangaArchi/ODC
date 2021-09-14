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
/*     (C)Copyright IBM Corp. 2003-2006                                     */
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

public abstract class PackageManager {

  // These policy bits are set up front from property file
  // should be set/get routines ... but for now ...
   public boolean policy_supportSameCompany = false;
   public boolean policy_supportIBM         = true;
   public boolean policy_supportLookup      = true;
   public boolean usetriggers               = false;
   
   protected FileManager fileMgr    = null;
   protected long maxExpireDays      = 15;
   
  // What privilege level is required for particular action/feature
   public static final int PRIVILEGE_CAN_USE_WILD       = 3;
   public static final int PRIVILEGE_CAN_SET_OTHER_ACLS = 5;
   public static final int PRIVILEGE_SUPER_USER         = 20;
   
         
   public PackageManager(DboxFileAllocator fa) {
      fa.setPackageManager(this);
   }
         
   public void lockpackage(long packid) throws DboxException {
      fileMgr.lockpackage(packid); 
   }
         
   public void setMaxExpireDays(long newv) {
      maxExpireDays = newv;
   }
   public long getMaxExpireDays(User user, long poolid) throws DboxException {
      return getStoragePoolInstance(user, poolid).getPoolMaxDays();
   }
   public long getDefaultExpireDays(User user, long poolid) throws DboxException {
      return getStoragePoolInstance(user, poolid).getPoolDefaultDays();
   }
   public long getMaxExpireMillis(User user, long poolid) throws DboxException {
      return getMaxExpireDays(user,poolid)*60*60*1000*24;
   }
   public long getDefaultExpireMillis(User user, long poolid) throws DboxException {
      return getDefaultExpireDays(user, poolid)*60*60*1000*24;
   }
   
  // Exception thrown if specified flag bits are not valid
   public void assertValidFlags(int msk, int vals) throws DboxException {
   
     // If more settable package options become avail, add to this check
      if ((msk & (((int)(PackageInfo.RETURNRECEIPT | 
                         PackageInfo.ITAR          |
                         PackageInfo.SENDNOTIFY    |
                         PackageInfo.HIDDEN))      & 0xff)) != msk) {
         throw new 
            DboxException("assertValidFlags: error:>> Flags(s) specified " + 
                          " mask = " + msk + " values = " + vals +
                          " are not valid", 0); 
      }
   }
   
  // Exception thrown if specified flag bits are not valid for read
   public void assertValidFlagsRead(int msk, int vals) throws DboxException {
      assertValidFlags(msk, vals);
   }
   
  // Exception thrown if specified flag bits are not valid at create
   public void assertValidFlagsCreate(int msk, int vals) throws DboxException {
      assertValidFlags(msk, vals);
   }
   
  // Exception thrown if specified flag bits are not valid after create
   public void assertValidFlagsModify(int msk, int vals) throws DboxException {
   
     // ITAR can only be set upon package create
      if ((msk & (((int)(PackageInfo.ITAR)))) != 0) {
         throw new
            DboxException("assertValidFlags: error:>> ITAR package flag " + 
                          " mask = " + msk + " values = " + vals +
                          " can only be set at package create time", 0); 
      }
      assertValidFlags(msk, vals);
   }
   
   public void assertExpireTime(String head, long expire, 
                                User user, long poolid) throws DboxException {
                                
      long curtime = System.currentTimeMillis();
      long maxexpire = curtime+getMaxExpireMillis(user, poolid);
      
     // Have a slush of 1 hour for clock issues
      if (expire < (curtime-(60*60*1000))) {
         throw new DboxException(head + 
                                 " Specified Expiration time is in the past!", 0);
      } 
      
      if ((expire-(60*60*1000)) > maxexpire) {
         throw new DboxException(head + 
                       " Specified Expiration time is too far in the future!", 0);
      }
   }
   
   public PoolInfo getStoragePoolInstance(User user, long poolid) throws DboxException {
      throw new DboxException("Storage Pool code not implemented!");
   }
         
   public Vector queryStoragePoolInformation(User user) throws DboxException {
      throw new DboxException("Storage Pool code not implemented!");
   }
         
   public abstract void changePackageExpiration(User owner, long packid, 
                                                long expire) throws DboxException;
      
   public abstract void setPackageDescription(User owner, 
                                              long packid, 
                                              String desc) throws DboxException;
                                                
   FileManager getFileManager() { return fileMgr; }
         
   public DboxFileInfo lookupFile(long fileid) throws DboxException {
      return fileMgr.lookupFile(fileid);
   }
      
   public abstract int cleanExpiredPackages(int tot) throws DboxException;
   
   public int cleanExpiredPackages() throws DboxException {
      return cleanExpiredPackages(-1);
   }
      
   public boolean canAccessFile(User user, 
                                long fileid, 
                                boolean includeOwner) throws DboxException {
                                      
      DboxFileInfo info = fileMgr.lookupFile(fileid);
      return canAccessFile(user, info, includeOwner);
   }
   
   
   public void markPackage(User user, long packid, 
                           boolean mark) throws DboxException {
      DboxPackageInfo pinfo = lookupPackage(packid);
      if (pinfo.canAccessPackage(user, true)) {
         if (mark) {
            pinfo.addFileAccessRecord(user, 0, 
                                      DropboxGenerator.STATUS_COMPLETE, -1);
         } else {
            pinfo.removeFileAccessRecord(user, 0);
         }
         pinfo.setPackageMarked(mark);
      }
   }
      
  // True if user is in ACL list of a package containing this file
   public boolean canAccessFile(User user, DboxFileInfo info, 
                                boolean includeOwner) throws DboxException {
      Vector vec = getPackagesContainingFile(user, info);
      Enumeration enum = vec.elements();
      while(enum.hasMoreElements()) {
         DboxPackageInfo pinfo = (DboxPackageInfo)enum.nextElement();
         if (pinfo.canAccessPackage(user, includeOwner)) return true;
      }
      return false;
   }
      
  // True if user has this file in a package she owns
   public boolean isFileOwner(User user, DboxFileInfo info) 
      throws DboxException {
      
      Vector vec = getPackagesContainingFile(user, info);
      Enumeration enum = vec.elements();
      String n = user.getName();
      while(enum.hasMoreElements()) {
         DboxPackageInfo pinfo = (DboxPackageInfo)enum.nextElement();
         if (pinfo.getPackageOwner().equals(n)) return true;
      }
      return false;
   }
      
   public boolean isFileOwner(User user, long itemid) throws DboxException {
      return isFileOwner(user, fileMgr.lookupFile(itemid));
   }
      
   public abstract DboxPackageInfo lookupPackage(long packid)
      throws DboxException;
      
   public DboxPackageInfo lookupPackage(long packid, User user)
      throws DboxException {
      DboxPackageInfo ret = lookupPackage(packid);
      updatePackageStatus(ret, user);
      return ret;
   }
      
   public boolean canAccessPackage(User user, 
                                   long packid, 
                                   boolean includeOwner) throws DboxException {
                                      
      DboxPackageInfo info = lookupPackage(packid);
      return info.canAccessPackage(user, includeOwner);
   }
            
   public abstract Vector packagesMatchingExpr(String exp, 
                                               boolean isReg, 
                                               boolean filterMarked,
                                               boolean filterComplete)
      throws DboxException;
      
   public abstract Vector packagesMatchingExprWithAccess(User user, 
                                                      boolean ownerOrAccessor, 
                                                      String exp, 
                                                      boolean isReg, 
                                                      boolean filterMarked,
                                                      boolean filterComplete)
                                                      
      throws DboxException;
      
   public abstract Vector getPackagesContainingFile(User user, 
                                                    DboxFileInfo info) 
      throws DboxException;
      
  // Called when a package is deleted or expired
   public abstract void cleanPackage(long packid) throws DboxException;
      
   public abstract DboxPackageInfo createPackage(User owner, String packname, 
                                                 String desc, long poolid, 
                                                 long expires, Vector acls) 
      throws DboxException;
      
   public void deletePackage(User owner, long id) throws DboxException {
         
      synchronized (this) {
         Long lid = new Long(id);
         DboxPackageInfo info = lookupPackage(id);
         if (owner == null || 
             (!info.getPackageOwner().equals(owner.getName()) &&
              getPrivilegeLevel(owner.getName()) < PRIVILEGE_SUPER_USER)) {
            throw new DboxException("Delete: error:>> package " + id + 
                                    " not owned by " + owner.getName(), 
                                    0);
         }
               
         cleanPackage(id);
      }
   }
      
   public abstract void commitPackage(User owner, long id) throws DboxException;
      
  // For LOCAL only??
  //
  // Possible updates: NONE, PARTIAL, FAILED
  //
   
   public void updatePackageStatus(DboxPackageInfo pinfo) 
      throws DboxException {
      
      updatePackageStatus(pinfo, null);
   }
   
   public void updatePackageStatus(DboxPackageInfo pinfo, User user) 
      throws DboxException {
         
      int complete = 0;
      int incomplete = 0;
      int none = 0;
      synchronized (this) {
         
         Debug.debugprint("\n\nupdatePackageStatus for " + pinfo.toString());
         if (pinfo.getPackageStatus() != 
             DropboxGenerator.STATUS_COMPLETE) {
                
            Vector lfiles = pinfo.getFiles();
            Enumeration enum = lfiles.elements();
            while(enum.hasMoreElements()) {
               FileInfo finfo = (FileInfo)enum.nextElement();
               Debug.debugprint(finfo.toString());
               switch(finfo.getFileStatus()) {
                  case DropboxGenerator.STATUS_COMPLETE:
                     complete++;
                     break;
                  case DropboxGenerator.STATUS_NONE:
                     none++;
                     break;
                  case DropboxGenerator.STATUS_INCOMPLETE:
                  default:
                     incomplete++;
                     break;
               }
            }
            
           // No marking unless we know who its for
            if (user == null) { pinfo.setPackageMarked(false); }
            
            Debug.debugprint("\nincomplete = " + incomplete);
            Debug.debugprint("\nnone = " + none);
            Debug.debugprint("\ncomplete = " + complete);
                
            if (incomplete != 0) {
               pinfo.setPackageStatus(DropboxGenerator.STATUS_FAIL);
            } else if (none != 0 || complete != 0) {
               pinfo.setPackageStatus(DropboxGenerator.STATUS_PARTIAL);
            } else {
               pinfo.setPackageStatus(DropboxGenerator.STATUS_NONE);
            }
         }
      }
   }
      
   public void addItemToPackage(User owner, long packid, long itemid) 
      throws DboxException {
         
      synchronized (this) {
         Long lid = new Long(packid);
            
         DboxPackageInfo info = lookupPackage(packid);
            
         int privlev = getPrivilegeLevel(owner.getName());
         
         if (owner == null ||
             (!info.getPackageOwner().equals(owner.getName()) && 
              privlev < PRIVILEGE_SUPER_USER)) {
              
            throw new DboxException("addItemToPackage: error:>> " +
                                    "package " + packid + 
                                    " not owned by " + owner.getName(),
                                    0);
         }
               
         if (info.getPackageStatus() == DropboxGenerator.STATUS_COMPLETE) {
            throw new DboxException("addItemToPackage: error:>> package " 
                                    + packid + " already complete", 0);
         }
                  
         DboxFileInfo finfo = lookupFile(itemid);
               
         if (!canAccessFile(owner, itemid, true)) { 
            throw new DboxException("addItemToPackage: error:>> file " 
                                    + itemid + " not accessible", 
                                    0);
         }
            
         if (info.includesFile(finfo.getFileName())) {
            throw new DboxException("addItemToPackage:>> file w/same name [" 
                                    + finfo.getFileName() + 
                                    "] in package " + packid,
                                    0);
         }
            
         info.addFile(finfo);
      }
   }
      
   public void removeItemFromPackage(User owner, long packid, long itemid) 
      throws DboxException {
         
      synchronized (this) {
            
         DboxPackageInfo info = lookupPackage(packid);
            
         int privlev = getPrivilegeLevel(owner.getName());
         
         if (owner == null ||
             (!info.getPackageOwner().equals(owner.getName()) && 
              privlev < PRIVILEGE_SUPER_USER)) {
              
            throw new DboxException("removeItemFromPackage:>> package " 
                                    + packid + 
                                    " not owned by " + owner.getName(),
                                    0);
         }
               
         if (info.getPackageStatus() == DropboxGenerator.STATUS_COMPLETE) {
            throw new DboxException("removeItemToPackage:>> package " 
                                    + packid + " already complete", 0);
         }
            
         info.removeFile(itemid);
      }
   }
      
   public void addPackageAcl(User owner, long packid, 
                             byte acltype, String aclName)
      throws DboxException {
         
      synchronized (this) {
         DboxPackageInfo info = lookupPackage(packid);
            
         String ownerS = (owner == null)? "" : owner.getName();
         int privlev = getPrivilegeLevel(ownerS);
         
         if (owner == null || 
             ((!info.getPackageOwner().equals(ownerS)) && 
              privlev < PRIVILEGE_CAN_SET_OTHER_ACLS)) {
            throw new DboxException("addPackageAcl: error:>> " +
                                    "package " + packid + 
                                    " not owned by " + owner.getName(),
                                    0);
         }
               
         if (acltype == DropboxGenerator.STATUS_PROJECT) {
            Vector v = owner.getProjects();
            if (!owner.getDoProjectSend() || v == null || 
                !v.contains(aclName)) {
               throw new DboxException("addPackageAcl: error:>> " +
                                       "Can't add project acl " + aclName + 
                                       " ... you don't have that project!",
                                       0);
            }
            info.addProjectAcl(aclName);
         } else if (acltype == DropboxGenerator.STATUS_GROUP) {
            Hashtable h = getMatchingGroups(owner, aclName, false, 
                                            false, false, false, true, 
                                            false, false, false, false);
            if (h.size() == 0) {
               throw new DboxException("addPackageAcl: error:>> " +
                                       "Specified Group does not exist or " +
                                       "not visibile: " + aclName,
                                       0);
            }
            info.addGroupAcl(aclName);
         } else {
            if (aclName.equals("*") && privlev < PRIVILEGE_CAN_USE_WILD) {
               throw new DboxException("addPackageAcl: error:>> " +
                                       "You don't have the privilege level " +
                                       "to add ACL of '*'/wild-card",
                                       0);
            }
            info.addUserAcl(aclName);
         }
      }
   }
      
      
   public void removePackageAcl(User owner, long packid, 
                                byte acltype, String aclName)
      throws DboxException {
         
      synchronized (this) {
         Long lid = new Long(packid);
         DboxPackageInfo info = lookupPackage(packid);
            
         String ownerS = (owner == null)? "" : owner.getName();
         int privlev = getPrivilegeLevel(ownerS);
         
         if (owner == null || 
             ((!info.getPackageOwner().equals(ownerS)) && 
              privlev < PRIVILEGE_CAN_SET_OTHER_ACLS)) {
            throw new DboxException("removePackageAcl: error:>> " +
                                    "package " + packid + 
                                    " not owned by " + owner.getName(), 
                                    0);
         }
               
         if        (acltype == DropboxGenerator.STATUS_PROJECT) {
            info.removeProjectAcl(aclName);
         } else if (acltype == DropboxGenerator.STATUS_GROUP) {
            info.removeGroupAcl(aclName);
         } else {
            info.removeUserAcl(aclName);
         }
      }
   }
      
   public int getPrivilegeLevel(String user) throws DboxException {
      return 0;
   }
   
  // Applies Dropbox policies ... subclass may do more
   public boolean allowsPackageReceipt(String fromUser, String fromCompany,
                                       String toUser,   String toCompany) 
      throws DboxException {
      boolean ret = true;
      
     // If Policy is in force, do we have access
      if (policy_supportSameCompany || 
          policy_supportIBM) {
         
        // Only do it if we are NOT IBM
         if (policy_supportIBM && 
             !fromCompany.equalsIgnoreCase("IBM")) {
            
            if (!toCompany.equalsIgnoreCase("IBM")) {
               if (!policy_supportSameCompany ||
                   !toCompany.equalsIgnoreCase(fromCompany)) {
                  ret = false;
               }
            }
         }
      }
      return ret;
   }
   
  // Returns the specified group if the user has at least Access privileges
   public abstract GroupInfo getGroupWithAccess(User user, 
                                                String group,
                                                boolean returnMembers, 
                                                boolean returnAccess)
      throws DboxException;
      
  // Returns a hash filled with groupname as key, and GroupInfo values
  //   if returnGI is true, whocares otherwise). The GroupInfo objects in
  //   question are those matching the boolean parms for the specified user.
  //   These flags are logically OR'd to create the result set.
   public abstract Hashtable getMatchingGroups(User user,
                                               String  group,
                                               boolean regexSearch,
                                               boolean owner,
                                               boolean modify,
                                               boolean member,
                                               boolean visible,
                                               boolean list,
                                               boolean returnGI,
                                               boolean returnMembers, 
                                               boolean returnAccess)
      throws DboxException;
   public abstract GroupInfo getGroup(String group, 
                                      boolean returnMembers, 
                                      boolean returnAccess)
      throws DboxException;
   public abstract void createGroup(User owner, 
                                    String group)
      throws DboxException;
   public abstract void modifyGroupAttributes(User owner, 
                                              String group,
                                              byte listability, 
                                              byte visibility) 
      throws DboxException;
   public abstract void addGroupAcl(User owner, 
                                    String group,
                                    String name,
                                    boolean accessOrMember)
      throws DboxException;
   public abstract void removeGroupAcl(User owner, 
                                       String group,
                                       String name,
                                       boolean accessOrMember)
      throws DboxException;
   public abstract void deleteGroup(User user,
                                    String group) throws DboxException;
   
  // Default impl for open/close session is do nothin
   public void openSession(User user) throws DboxException  {}
   public void closeSession(User user) throws DboxException {}
   public void setSessionExpiration(User user, Date d) throws DboxException {}
   
   public void setClientInfo(User user, String OS, String clientType) throws DboxException {}
   
  // Deprecated!!
   public void closeSession(User user, 
                            DSMPBaseHandler h) throws DboxException {
      closeSession(user);
   }
   
   public void validateSession(User user) throws DboxException {}
      
  // Options code
   public void assertUserOptionNames(String username, Enumeration enum)
      throws DboxException {
      
      if (enum != null) {
         Hashtable validNames = getValidOptionNames(username);
         while(enum.hasMoreElements()) {
            String optn = (String)enum.nextElement();
            if (validNames == null || validNames.get(optn) == null) {
               throw new DboxException("assertOptionName: " + optn +
                                       " is an invalid option", 0);
            }
         }
      }
   }
   public void assertUserOptionNames(String u, 
                                     Hashtable h) throws DboxException {
                                     
      if (h != null) assertUserOptionNames(u, h.keys());
   }
   public void assertUserOptionNames(String u, 
                                     Vector v) throws DboxException {
                                     
      if (v != null) assertUserOptionNames(u, v.elements());
   }
   
   public abstract Hashtable getValidOptionNames(String user);
   public abstract void setUserOptions(User user, 
                                       Hashtable h) throws DboxException;
   public abstract Hashtable getUserOptions(User user, 
                                            Vector v) throws DboxException;
   public abstract String getUserOption(User user,
                                        String k) throws DboxException;
   public abstract Hashtable getUserOptions(String u,
                                            Vector v) throws DboxException;
   public abstract String getUserOption(String u,
                                        String k) throws DboxException;
   public abstract int setPackageOption(User user, long pkgid,
                                        int msk, int vals)
      throws DboxException;
                                        
                                        
   public String toString() {
      return "";
   }
}
