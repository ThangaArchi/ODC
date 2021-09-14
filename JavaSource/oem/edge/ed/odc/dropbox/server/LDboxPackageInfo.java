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

public class LDboxPackageInfo extends DboxPackageInfo {
   Hashtable files       = new Hashtable();
   Hashtable projacls    = new Hashtable();
   Hashtable groupacls   = new Hashtable();
   Hashtable useracls    = new Hashtable();
   LFileAccess fileAccess = new LFileAccess();
   
   
   LDboxPackageInfo(PackageManager pm) {
      super(pm);
   }
      
   LDboxPackageInfo(LDboxPackageInfo in) {
      super(in);
      files      = (Hashtable)in.files.clone();
      projacls   = (Hashtable)in.projacls.clone();
      groupacls  = (Hashtable)in.groupacls.clone();      
      useracls   = (Hashtable)in.useracls.clone();
      
     // Just share the fileAccess object for now ... TODO
      fileAccess = in.fileAccess;
   }
   
   public DboxPackageInfo cloneit() {
      return new LDboxPackageInfo(this);
   }
   
   synchronized Vector getFiles() {
      Vector ret = new Vector();
      Enumeration enum = files.elements();
      while(enum.hasMoreElements()) {
         ret.addElement(enum.nextElement());
      }
      return ret;
   }
   synchronized void addFile(DboxFileInfo info) throws DboxException {
      Long l = new Long(info.getFileId());
      if (files.get(l) != null) {
         throw new DboxException("addFileToPackage: Package " + 
                                 getPackageId() + " already contains item "
                                 + l.longValue(), 
                                 0);
      }
         
      files.put(l, info);
      packagesize += info.getFileSize();
      numelements++;
   }
   synchronized void removeFile(long itemid) throws DboxException {
      Long l = new Long(itemid);
      DboxFileInfo info = (DboxFileInfo)files.get(l);
      if (info == null) {
         throw new DboxException("removeFileFromPackage: Package " + 
                                 getPackageId() + " does not contain item "
                                 + l.longValue(),
                                 0);
      }
         
      fileAccess.remove(itemid);
         
      files.remove(l);
      packagesize -= info.getFileSize();
      numelements--;
   }
      
   synchronized public void recalculatePackageSize() throws DboxException { 
      Enumeration enum = files.elements();
      packagesize = 0;
      while(enum.hasMoreElements()) {
         DboxFileInfo info = (DboxFileInfo)enum.nextElement();
         packagesize += info.getFileSize();
      }
   }
      
   synchronized DboxFileInfo getFile(String name) throws DboxException {
      Enumeration enum = files.elements();
      while(enum.hasMoreElements()) {
         DboxFileInfo info = (DboxFileInfo)enum.nextElement();
         if (info.getFileName().equals(name)) {
            return info;
         }
      }
      throw new DboxException("PackageInfo: getFile: File " + name + 
                              " does not exist in package " + 
                              getPackageId(), 0);
   }
      
   synchronized DboxFileInfo getFile(long itemid) throws DboxException {
      Long l = new Long(itemid);
      DboxFileInfo info = (DboxFileInfo)files.get(l);
      if (info == null) {
         throw new DboxException("PackageInfo: getFile: File " + itemid + 
                                 " does not exist in package " + 
                                 getPackageId(), 0);
      }
         
      return info;
   }
      
   synchronized void addProjectAcl(String name) throws DboxException {
      if (projacls.get(name) != null) {
         throw new DboxException("addProjectAcl: Package " + 
                                 getPackageId() + " already contains proj "
                                 + name, 0);
      }
      projacls.put(name, this);
   }
   synchronized void removeProjectAcl(String name) throws DboxException {
      if (projacls.get(name) == null) {
         throw new DboxException("removeProjectAcl: Package " + 
                                 getPackageId() + " does not contain proj "
                                 + name, 0);
      }
      projacls.remove(name);
   }
            
   synchronized void addGroupAcl(String name) throws DboxException {
      if (groupacls.get(name) != null) {
         throw new DboxException("addGroupAcl: Package " + 
                                 getPackageId() + " already contains group "
                                 + name, 0);
      }
      groupacls.put(name, this);
   }
   synchronized void removeGroupAcl(String name) throws DboxException {
      if (groupacls.get(name) == null) {
         throw new DboxException("removeGroupAcl: Package " + 
                                 getPackageId() + " does not contain group "
                                 + name, 0);
      }
      groupacls.remove(name);
   }

   synchronized void addUserAcl(String name) throws DboxException {
      if (useracls.get(name) != null) {
         throw new DboxException("addUserAcl: Package " + 
                                 getPackageId() + " already contains user "
                                 + name, 0);
      }
      useracls.put(name, this);
   }
   synchronized void removeUserAcl(String name) throws DboxException {
      if (useracls.get(name) == null) {
         throw new DboxException("removeUserAcl: Package " + 
                                 getPackageId() + " does not contain user "
                                 + name, 0);
      }
      useracls.remove(name);
   }
      
   synchronized Vector getFileAcls(long fileid) throws DboxException {
      if (files.get(new Long(fileid)) == null) {
         throw new DboxException("getFileAcls: Specified file " + fileid +
                                 " does not exist in package", 0);
      }
         
      return fileAccess.accessAclsForFile(fileid);
   }
      
   synchronized void addFileAccessRecord(User user, long fileid, 
                                         byte status, int xferate) 
      throws DboxException {
         
      if (fileid != 0 && files.get(new Long(fileid)) == null) {
         throw new DboxException("addFileAcls: Specified file " + fileid +
                                 " does not exist in package", 0);
      }
         
      AclInfo aclinfo = getUserAccess(user, true);
      aclinfo.setAclStatus(status);
      aclinfo.setXferRate(xferate);
      fileAccess.add(fileid, aclinfo, user);
   }
      
   synchronized void removeFileAccessRecord(User user, long fileid) 
      throws DboxException {
         
      fileAccess.remove(user, fileid);
   }
      
   synchronized Vector getPackageAcls(boolean staticOnly) throws DboxException {
      Vector ret = new Vector();
         
     // All project acl info gets sent
      Enumeration enum = projacls.keys();
      while(enum.hasMoreElements()) {
         AclInfo aclinfo = new AclInfo();
         aclinfo.setAclName((String)enum.nextElement());
         aclinfo.setAclStatus(DropboxGenerator.STATUS_PROJECT);
         ret.addElement(aclinfo);
      }
      
     // All group acl info gets sent
      enum = groupacls.keys();
      while(enum.hasMoreElements()) {
         AclInfo aclinfo = new AclInfo();
         aclinfo.setAclName((String)enum.nextElement());
         aclinfo.setAclStatus(DropboxGenerator.STATUS_GROUP);
         ret.addElement(aclinfo);
      }
      
      if (staticOnly) {
        // Take all users 
         enum = useracls.keys();
         while(enum.hasMoreElements()) {
            
            String user = (String)enum.nextElement();
            AclInfo aclinfo = new AclInfo();
            aclinfo.setAclName(user);
            aclinfo.setAclStatus(DropboxGenerator.STATUS_NONE);
            ret.addElement(aclinfo);
         }
      } else {
      
        // Take any users which have NOT accessed files
         enum = useracls.keys();
         while(enum.hasMoreElements()) {
            
            String user = (String)enum.nextElement();
            if (fileAccess.filesAccessedByUser(user).size() == 0) {
               
               AclInfo aclinfo = new AclInfo();
               aclinfo.setAclName(user);
               aclinfo.setAclStatus(DropboxGenerator.STATUS_NONE);
               ret.addElement(aclinfo);
            }
         }
        
        // Add in all useracls which have a logged access record
         Vector vec = fileAccess.accessSummary(getPackageNumElements());
         enum = vec.elements();
         while(enum.hasMoreElements()) {
            AclInfo aclinfo = (AclInfo)enum.nextElement();
            aclinfo = new AclInfo(aclinfo);
            ret.addElement(aclinfo);
         }
      }
      return ret;
   }
      
   public AclInfo getUserAccess(User user, boolean includeOwner) 
      throws DboxException {
         
      AclInfo ret = null;
         
      String n = user.getName();
         
     /*
      * useracls.contains does NOT work!
      */
      if (useracls.get(n)   != null ||
          useracls.get("*") != null ||
          (includeOwner && n.equals(getPackageOwner()))) {
         ret = new AclInfo();
         ret.setAclName(n);
         return ret;
      }
         
     // Check projects
      Vector projs = user.getProjects();
      if (projs.size() != 0 && projacls.size() != 0) {
         Enumeration enum = projs.elements();
         while(enum.hasMoreElements()) {
            Object o = enum.nextElement();
            if (projacls.get(o) != null) {
               ret = new AclInfo();
               ret.setAclName(n);
               ret.setAclProjectName((String)o);
               ret.setAclStatus(DropboxGenerator.STATUS_NONE);
               return ret;
            }
         }
      }
      
     // Check groups
      if (groupacls.size() != 0) {
         Enumeration enum = groupacls.keys();
         while(enum.hasMoreElements()) {
            String gs = (String)enum.nextElement();
            try {
               GroupInfo gi = packageManager.getGroup(gs, true, true);
               if (gi.getGroupMembers().contains(n)) {
                  ret = new AclInfo();
                  ret.setAclName(n);
                  ret.setAclProjectName(gs);
                  ret.setAclStatus(DropboxGenerator.STATUS_NONE);
                  return ret;
               }
            } catch(DboxException dbe) {
               ;
            }
         }
      }
      
      throw new DboxException("PackageInfo: getUserAccess: User " + 
                              user.getName() + 
                              "does not have access to package " + 
                              getPackageId(), 0);
   }
      
   public String toString() {
      String ret = super.toString();
      ret += Nester.nest("\n-------- Files --------\n");
      Enumeration enum = files.elements();
      while(enum.hasMoreElements()) {
         ret += Nester.nest("\n" + enum.nextElement().toString(), 2);
      }
      ret += Nester.nest("\n-------- projacls --------");
      enum = projacls.keys();
      while(enum.hasMoreElements()) {
         ret += Nester.nest("\n" + enum.nextElement().toString(), 2);
      }
      ret += Nester.nest("\n-------- groupacls --------");
      enum = groupacls.keys();
      while(enum.hasMoreElements()) {
         ret += Nester.nest("\n" + enum.nextElement().toString(), 2);
      }
         
      ret += Nester.nest("\n-------- useracls --------");
      enum = useracls.keys();
      while(enum.hasMoreElements()) {
         ret += Nester.nest("\n" + enum.nextElement().toString(), 2);
      }
      ret += Nester.nest("\n-------- fileaccess --------\n") + 
         Nester.nest(fileAccess.toString(), 2);
         
      return ret;
   }
}
