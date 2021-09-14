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

public class LPackageManager extends PackageManager {
   
   Hashtable packages     = new Hashtable();
   Hashtable groups       = new Hashtable();
   Hashtable packageNames = new Hashtable();
         
   public LPackageManager(DboxFileAllocator fa) {
      super(fa);
      fileMgr = new LFileManager(this, fa);
   }
         
   public int cleanExpiredPackages(int tot) throws DboxException {
      System.out.println(
         "LPackageManager: BZZZZ. CleanExpiredPackages ... fill it in!!");
      throw new DboxException(
         "LPackageManager: BZZZZ. CleanExpiredPackages ... fill it in!!", 0);
   }
   
      
   public DboxPackageInfo lookupPackage(long packid) throws DboxException {
                                      
      DboxPackageInfo info = 
         (DboxPackageInfo)packages.get(new Long(packid));
      if (info == null) {
         throw new DboxException("LookupPackage: package " + packid +
                                 " not found", 0);
      }
      updatePackageStatus(info);   
      
      return info;
   }
            
   public Vector packagesMatchingExpr(String exp, 
                                      boolean isReg,
                                      boolean filterMarked,
                                      boolean filterComplete)
                         throws DboxException {
      
      Vector ret = new Vector();
         
      org.apache.regexp.RE re = null;
         
      if (isReg && exp != null) {
         try {
            re = new org.apache.regexp.RE(exp);
         } catch(org.apache.regexp.RESyntaxException syne) {
            throw new DboxException("Invalid regexp: " + exp, 0);
         }
      }
         
      synchronized(packages) {
         Enumeration enum = packageNames.keys();
         while(enum.hasMoreElements()) {
            String name = (String)enum.nextElement();
            if (exp == null                || 
                (isReg  && re.match(name)) || 
                (!isReg && name.equals(exp))) {
               Vector vec = ((Vector)packageNames.get(name));
               Enumeration enump = vec.elements();
               while(enump.hasMoreElements()) {
                  DboxPackageInfo info = 
                     (DboxPackageInfo)enump.nextElement();
                  info = info.cloneit();
                  updatePackageStatus(info);   
                  ret.addElement(info);
               }
            }
         }
      }
      return ret;
   }
      
   public Vector packagesMatchingExprWithAccess(User user, 
                                                boolean ownerOrAccessor, 
                                                String exp, 
                                                boolean isReg,
                                                boolean filterMarked,
                                                boolean filterComplete)
      throws DboxException {
      
      Vector ret = new Vector();
         
      org.apache.regexp.RE re = null;
         
      if (isReg && exp != null) {
         try {
            re = new org.apache.regexp.RE(exp);
         } catch(org.apache.regexp.RESyntaxException syne) {
            throw new DboxException("Invalid regexp: " + exp, 0);
         }
      }
         
      synchronized(packages) {
         Enumeration enum = packageNames.keys();
         while(enum.hasMoreElements()) {
            String name = (String)enum.nextElement();
            
            if (exp == null                || 
                (isReg  && re.match(name)) || 
                (!isReg && name.equals(exp))) {
               Vector vec = ((Vector)packageNames.get(name));
               Enumeration enump = vec.elements();
               while(enump.hasMoreElements()) {
                  DboxPackageInfo info = 
                     (DboxPackageInfo)enump.nextElement();
                     
                  if (ownerOrAccessor) {
                     if (info.getPackageOwner().equalsIgnoreCase(user.getName())){
                        info = info.cloneit();
                        updatePackageStatus(info, user);
                        ret.addElement(info);
                     }
                  } else if (info.canAccessPackage(user, false) && 
                             info.getPackageStatus() == 
                             DropboxGenerator.STATUS_COMPLETE) {
                     info = info.cloneit();
                     updatePackageStatus(info, user);
                     ret.addElement(info);
                  }
               }
            }
         }
      }
      return ret;
   }
      
   public Vector getPackagesContainingFile(User user, DboxFileInfo info) 
      throws DboxException {
           
     // Foreach package, look if file is in there. If so, and user has access
     //  return it
      Vector ret = new Vector();
      synchronized (packages) {
         Enumeration enum = packages.elements();
         long itemid = info.getFileId();
         while(enum.hasMoreElements()) {
            DboxPackageInfo pinfo = (DboxPackageInfo)enum.nextElement();
            if (pinfo.includesFile(itemid)) {
               if (user != null) {
                  if (pinfo.canAccessPackage(user, true)) {
                     pinfo = pinfo.cloneit();
                     updatePackageStatus(pinfo, user);
                     ret.addElement(pinfo);
                  }
               } else {
                  pinfo = pinfo.cloneit();
                  updatePackageStatus(pinfo, user);
                  ret.addElement(pinfo);
               }
            }
         }
      }
      return ret;
   }
      
  // Called when a package is deleted or expired
   public void cleanPackage(long packid) throws DboxException {
      
      Long lid = new Long(packid);
      DboxPackageInfo info = (DboxPackageInfo)packages.remove(lid);
      if (info == null) {
         throw new DboxException("cleanPackage: error: package " + 
                                 packid + " not found", 0);
      }
         
      System.out.println("!!! For each file, check if in other packages");
      System.out.println("!!! if not, they should be culled");
      System.out.println("!!! Keep in mind that we should be checking");
      System.out.println("!!! if someone is downloading a file at ");
      System.out.println("!!! this time and deal with it");
         
      Vector vec = (Vector)packageNames.get(info.getPackageName());
      if (vec != null) {
         for(int i=0; i < vec.size(); i++) {
            DboxPackageInfo tinfo = (DboxPackageInfo)vec.elementAt(i);
            if (tinfo.getPackageId() == packid) {
               vec.removeElementAt(i);
               if (vec.size() == 0) {
                  packageNames.remove(info.getPackageName());
               }
               break;
            }
         }
      } else {
         System.out.println("cleanPackage: PackageMgr: Hmmm ... found id="
                            + packid + " but packagename [" + 
                            info.getPackageName() + "] not found");
      }
   }
      
      
   public void changePackageExpiration(User owner, long packid, long expire)
      throws DboxException {
      
      synchronized (packages) {
         DboxPackageInfo info = lookupPackage(packid);
         if (owner == null || 
             !info.getPackageOwner().equals(owner.getName())) {
            throw new DboxException("changePackageExpiration: error: package " + 
                                    packid + " not owned by " + owner.getName(),
                                    0);
         }
            
         assertExpireTime("changePackageExpiration:", expire, owner, 
                          info.getPackagePoolId());
         info.setPackageExpiration(expire);
      }
   }
      
   public void setPackageDescription(User owner, long packid, String desc)
      throws DboxException {
      
      synchronized (packages) {
         DboxPackageInfo info = lookupPackage(packid);
         if (owner == null || 
             !info.getPackageOwner().equals(owner.getName())) {
            throw new DboxException("setPackageDescription: error: package " + 
                                    packid + " not owned by " + owner.getName(),
                                    0);
         }
            
         info.setPackageDescription(desc);
      }
   }
      
   public DboxPackageInfo createPackage(User owner, String packname, 
                                        String desc, long poolid, 
                                        long expires, Vector acls) 
      throws DboxException {
         
      if (packname.length() == 0) { 
         throw new DboxException("Null package name not valid", 0);
      }
         
      long curtime = System.currentTimeMillis();
      if (expires == 0) expires = curtime + getMaxExpireMillis(owner, poolid);
      else {
         assertExpireTime("createPackage:", expires, owner, poolid);
      }
      
      DboxPackageInfo info = new LDboxPackageInfo(this);
      info.setPackageName(packname);
      info.setPackageOwner(owner.getName());
      info.setPackageId(IDGenerator.getId());
      info.setPackagePoolId(poolid);
      info.setPackageStatus(DropboxGenerator.STATUS_NONE);
      info.setPackageDescription(desc);
      info.setPackageExpiration(System.currentTimeMillis() + 
                                60*60*1000*24*5);
      info.setPackageCreation(System.currentTimeMillis());
         
      Enumeration enum = acls.elements();
      while(enum.hasMoreElements()) {
         AclInfo aclinfo = (AclInfo)enum.nextElement();
         String aclname = aclinfo.getAclName();
         
         if (aclinfo.getAclStatus() == DropboxGenerator.STATUS_PROJECT) {
            Vector v = owner.getProjects();
            if (v == null || !v.contains(aclname)) {
               throw new DboxException("createPackage: addPackageAcl: error: " +
                                       "Can't add project acl " + 
                                       aclname + 
                                       " ... you don't have that project!",
                                       0);
            }
            info.addProjectAcl(aclname);
         } else  if (aclinfo.getAclStatus() == DropboxGenerator.STATUS_GROUP) {
            GroupInfo gi = getGroupWithAccess(owner, aclname,
                                              false, false);
            info.addGroupAcl(aclname);
         } else {
            info.addUserAcl(aclname);
         }
      }
         
      synchronized (packages) {
         packages.put(new Long(info.getPackageId()), info);
         Vector vec = (Vector)packageNames.get(packname);
         if (vec == null) {
            vec = new Vector();
            packageNames.put(packname, vec);
         }
         vec.addElement(info);
      }
      return info;
   }
      
   public void commitPackage(User owner, long id) throws DboxException {
         
      synchronized (packages) {
         DboxPackageInfo info = lookupPackage(id);
         if (owner == null || 
             !info.getPackageOwner().equals(owner.getName())) {
            throw new DboxException("Commit: error: package " + id + 
                                    " not owned by " + owner.getName(),
                                    0);
         }
            
         if (info.getPackageStatus() != 
             DropboxGenerator.STATUS_COMPLETE) {
                
            Vector lfiles = info.getFiles();
            Enumeration enum = lfiles.elements();
            while(enum.hasMoreElements()) {
               FileInfo finfo = (FileInfo)enum.nextElement();
               if (finfo.getFileStatus() != 
                   DropboxGenerator.STATUS_COMPLETE) {
                     
                  throw new DboxException("Commit: error: package " + id + 
                                          " has at least 1 incomplete file",
                                          0);
               }
            }
                
            info.setPackageStatus(DropboxGenerator.STATUS_COMPLETE);
         } else {
            throw new DboxException("Commit: error: package " + id + 
                                    " already complete", 0);
         }
      }
   }
      
   public GroupInfo getGroup(String group,
                             boolean returnMember,
                             boolean returnAccess) throws DboxException {
      GroupInfo gi = (GroupInfo)groups.get(group);
      if (gi == null) {
         throw new DboxException("getGroup: Group does not exist: " + group, 
                                 0);
      }
      return gi;
   }

   public GroupInfo getGroupWithAccess(User user, 
                                       String group,
                                       boolean returnMember,
                                       boolean returnAccess) 
      throws DboxException {
      
      GroupInfo gi = (GroupInfo)groups.get(group);
      if (gi == null) {
         throw new DboxException("getGroup: Group does not exist: " + group,
                                 0);
      }
      if (!gi.getGroupOwner().equals(user.getName()) && 
          !gi.getGroupAccess().contains(user.getName())) {
         throw new DboxException("getGroupWithAccess: User '" + 
                                 user.getName() + 
                                 "' does not have access to group: " + group,
                                 0);
      }
      return gi;
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
                                      boolean returnMember,
                                      boolean returnAccess)
                                      
      throws DboxException {
      
      Hashtable ret = new Hashtable();
      
      
      org.apache.regexp.RE re = null;
      
      if (group != null && group.trim().length() == 0) group = null;
      
      if (regexSearch && group != null) {
         try {
            re = new org.apache.regexp.RE(group);
         } catch(org.apache.regexp.RESyntaxException syne) {
            throw new DboxException("Invalid regexp: " + group, 0);
         }
      }
      
      String name = user.getName();
      Enumeration enum = groups.elements();
      while(enum.hasMoreElements()) {
         GroupInfo gi = (GroupInfo)enum.nextElement();
         
         String gname = gi.getGroupName();
         
        // If group name is supplied
         if (group == null ||
             !((regexSearch  && re.match(gname)) ||
               (!regexSearch && group.equals(gname)))) {
            continue;
         }
         
         boolean grpowner    = gi.getGroupOwner().equals(name);
         boolean grpaccess   = gi.getGroupAccess().contains(name);
         boolean grpmember   = gi.getGroupMembers().contains(name);
         boolean grpvisible  = 
            grpowner || grpaccess || 
            (grpmember && 
             gi.getGroupVisibility() == DropboxGenerator.GROUP_SCOPE_MEMBER) ||
            gi.getGroupVisibility() == DropboxGenerator.GROUP_SCOPE_ALL;
         boolean grplistable = 
            grpvisible && 
            (grpowner || grpaccess || 
            (grpmember && 
             gi.getGroupListability()==DropboxGenerator.GROUP_SCOPE_MEMBER) ||
            gi.getGroupListability() == DropboxGenerator.GROUP_SCOPE_ALL);
            
         
         if ((owner    && grpowner)                ||
             (modify   && grpaccess)               ||
             (member   && grpmember)               ||
             (visible  && grpvisible)              || 
             (listable && grplistable)) {
            if (returnGI) {
               ret.put(name, gi);
            } else {
               ret.put(name, name);
            }
         }
      }
      return ret;
   }
   
   
   public void createGroup(User owner, String group) throws DboxException {
      synchronized(groups) {
         try {
            getGroup(group, false, false);
            throw new DboxException("createGroup: Group already exists: " + 
                                    group, 0);
         } catch(DboxException dbe) {
            GroupInfo gi = new GroupInfo();
            gi.setGroupName(group);
            gi.setGroupOwner(owner.getName());
            gi.setGroupCompany(owner.getCompany());
            gi.setGroupCreated((new Date()).getTime());
            groups.put(group, gi);
         }
      }
   }
   public void deleteGroup(User user, String group) throws DboxException {
      synchronized(groups) {
         GroupInfo gi = null;
         try {
            gi = getGroup(group, false, false);
         } catch(DboxException dbe) {
            throw new DboxException("deleteGroup: Group does not exist: " + 
                                    group, 0);
         }
         
         if (!gi.getGroupOwner().equals(user.getName())) {
            throw new 
               DboxException("deleteGroup: Only owner can delete group: " 
                             + group, 0);
         }
         groups.remove(group);
      }
      
     // Remove group from all package acls
      synchronized(packages) {
         Enumeration enum = packageNames.elements();
         while(enum.hasMoreElements()) {
            Vector vec = (Vector)enum.nextElement();
            Enumeration enump = vec.elements();
            while(enump.hasMoreElements()) {
               DboxPackageInfo info = 
                  (DboxPackageInfo)enump.nextElement();
               try {
                  info.removeGroupAcl(group);
               } catch(Exception ee) {}
            }
         }
      }
   }
   
   public void modifyGroupAttributes(User owner, 
                                     String group,
                                     byte visibility, 
                                     byte listability) 
      throws DboxException {
      
      synchronized(groups) {
         GroupInfo gi = getGroupWithAccess(owner, group, false, false);
         switch(visibility) {
            case DropboxGenerator.GROUP_SCOPE_NONE:
               break;
            case DropboxGenerator.GROUP_SCOPE_OWNER:
            case DropboxGenerator.GROUP_SCOPE_MEMBER:
            case DropboxGenerator.GROUP_SCOPE_ALL:
               gi.setGroupVisibility(visibility);
               break;
            default:
               throw new DboxException("modifyGroupAttr: Group '" + group + 
                                       "' visibility value invalid: " +
                                       visibility,
                                       0);
               
         }
         switch(listability) {
            case DropboxGenerator.GROUP_SCOPE_NONE:
               break;
            case DropboxGenerator.GROUP_SCOPE_OWNER:
            case DropboxGenerator.GROUP_SCOPE_MEMBER:
            case DropboxGenerator.GROUP_SCOPE_ALL:
               gi.setGroupListability(listability);
               break;
            default:
               throw new DboxException("modifyGroupAttr: Group '" + group + 
                                       "' listability value invalid: " +
                                       listability,
                                       0);
               
         }
      }
   }
   public void addGroupAcl(User owner, 
                           String group,
                           String name,
                           boolean memberOrAccess)
      throws DboxException {
      synchronized(groups) {
         GroupInfo gi = getGroupWithAccess(owner, group, false, false);
         Vector vec = memberOrAccess ? gi.getGroupMembers() 
                                     : gi.getGroupAccess();
         if (vec.contains(name)) {
            throw new DboxException("addGroupAcl: Group '" + group + 
                                    "' already contains user '" + 
                                    owner.getName() + "'",
                                    0);
         }
         vec.addElement(name);
      }
   }
   public void removeGroupAcl(User owner, 
                              String group,
                              String name,
                              boolean memberOrAccess)
      throws DboxException {
      synchronized(groups) {
         GroupInfo gi = getGroupWithAccess(owner, group, false, false);
         Vector vec = memberOrAccess ? gi.getGroupMembers() 
                                     : gi.getGroupAccess();
         if (!vec.contains(name)) {
            throw new DboxException("remGroupAcl: Group '" + group + 
                                    "' does not contain user '" + 
                                    owner.getName() + "'",
                                    0);
         }
         vec.removeElement(name);
      }
   }
   
  // We have no options
   public Hashtable getValidOptionNames(String u) {
      Hashtable h = new Hashtable();
      return h;
   }
      
   public Hashtable getUserOptions(String u, Vector v) throws DboxException {
      assertUserOptionNames(u, v);
      return new Hashtable();
   }
   public String getUserOption(String u, String k) throws DboxException {
      Vector v = new Vector();
      v.addElement(k);
      assertUserOptionNames(u, v);
      return "";
   }
      
   public void setUserOptions(User user, Hashtable h) throws DboxException {
   
      assertUserOptionNames(user.getName(), h);
   }
   
   public Hashtable getUserOptions(User user, Vector v) throws DboxException {
      assertUserOptionNames(user.getName(), v);
      return new Hashtable();
   }
      
   public String getUserOption(User user, String k) throws DboxException {
      Vector v = new Vector();
      v.addElement(k);
      assertUserOptionNames(user.getName(), v);
      return "";
   }
      
   public int setPackageOption(User user, long pkgid, int msk, int vals)
      throws DboxException {
      
      DboxPackageInfo info = lookupPackage(pkgid);
      
      String ownerid = info.getPackageOwner();
      if (user==null || ownerid == null || (!ownerid.equals(user.getName()))) {
         throw new DboxException("setPackageOption: error: package " + pkgid + 
                                 " not owned by " + user.getName(), 0); 
      }
         
      if (msk == 0) {
         return ((int)info.getPackageFlags()) & 0xff;
      }
         
     // Remove the values indicated in mask, then OR in the new values
      int newflags  = (((int)info.getPackageFlags()) & ~msk) & 0xff;
      newflags |= (msk & vals);
         
      info.setPackageFlags((byte)newflags);
      
      return newflags;
   }   
      
   public String toString() {
      String ret = "\n\n------Start PackageManager Start -----\n";
         
      Enumeration enum = packages.elements();
      while(enum.hasMoreElements()) { 
         DboxPackageInfo pinfo = (DboxPackageInfo)enum.nextElement();
         ret += "\n" + Nester.nest(pinfo.toString());
      }
      ret += "\n\n------Start FileManager Start -----\n";
      ret += "\n" + Nester.nest(fileMgr.toString());
      ret += "\n\n------End   FileManager End   -----\n";
      ret += "\n------End   PackageManager End   -----\n";
      return ret;
   }
}
 
