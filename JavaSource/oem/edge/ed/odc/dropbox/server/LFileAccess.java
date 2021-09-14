package oem.edge.ed.odc.dropbox.server;

import  oem.edge.ed.odc.dropbox.common.*;
import  oem.edge.ed.odc.ftp.common.*;
import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.dsmp.server.*;
import  oem.edge.ed.odc.util.*;

import java.lang.*;
import java.io.*;
import java.util.*;

public class LFileAccess extends FileAccess {
   
   protected Hashtable fileAccess = new Hashtable();
      
   public synchronized void add(long fileid, AclInfo aclinfo, User user) 
      throws DboxException {
      
     // Ignore User object
      
      Long l = new Long(fileid);
      Hashtable hash = (Hashtable)fileAccess.get(l);
      if (hash == null) {
         hash = new Hashtable(); 
         fileAccess.put(l, hash);
      }
         
      DboxFileAclInfo tinfo = 
         (DboxFileAclInfo)hash.get(aclinfo.getAclName());
      if (tinfo != null) {
         if (aclinfo.getAclStatus() > tinfo.getAclStatus()) {
            hash.put(aclinfo.getAclName(), 
                     new DboxFileAclInfo(aclinfo, fileid));
         }
      } else {
         hash.put(aclinfo.getAclName(), 
                  new DboxFileAclInfo(aclinfo, fileid));
      }
   }
      
   public synchronized void remove(long fileid) 
      throws DboxException {

      Long l = new Long(fileid);
      Hashtable hash = (Hashtable)fileAccess.remove(l);
   }
   
   public synchronized void remove(User user, long fileid) 
      throws DboxException {

      Long l = new Long(fileid);
      Hashtable hash = (Hashtable)fileAccess.get(l);
      hash.remove(user.getName());
   }
      
  // Return vec contains DboxFileAclInfo
   public synchronized Vector filesAccessedByUser(String user) 
      throws DboxException {

      Vector ret = new Vector();
      Enumeration enum = fileAccess.keys();
      while(enum.hasMoreElements()) {
         Long fid = (Long)enum.nextElement();
         
        // Skip mark record
         if (fid.longValue() == 0) continue;
         
         Hashtable hash = (Hashtable)fileAccess.get(fid);
         DboxFileAclInfo aclinfo = (DboxFileAclInfo)hash.get(user);
         if (aclinfo != null) {
            ret.addElement(new DboxFileAclInfo(aclinfo));
         }
      }
      return ret;
   }
      
  // Returns number of files access by the specified user
   public synchronized int numberFilesAccessedBy(String user) 
      throws DboxException {

      int ret = 0;
      Enumeration enum = fileAccess.keys();
      while(enum.hasMoreElements()) {
         Long fid = (Long)enum.nextElement();
         
        // Skip mark record
         if (fid.longValue() == 0) continue;
         
         Hashtable hash = (Hashtable)fileAccess.get(fid);
         DboxFileAclInfo aclinfo = (DboxFileAclInfo)hash.get(user);
         if (aclinfo != null) {
            ret++;
         }
      }
      return ret;
   }
      
   public synchronized DboxFileAclInfo fileAccessedBy(long fileid, 
                                                      String user) 
      throws DboxException {

      DboxFileAclInfo ret = null;
      Hashtable uhash = (Hashtable)fileAccess.get(new Long(fileid));
      if (uhash != null) {
         ret = (DboxFileAclInfo)uhash.get(user);
      }
      return ret;
   }
      
  // returns the number of complete uniq file xfers
   public synchronized int statusCompleteFor(String user) 
      throws DboxException {

      int ret = 0;
      Enumeration enum = fileAccess.elements();
      while(enum.hasMoreElements()) {
         Hashtable hash = (Hashtable)enum.nextElement();
         DboxFileAclInfo aclinfo = (DboxFileAclInfo)hash.get(user);
         if (aclinfo != null && 
         
            // Skip mark record
             aclinfo.getFileId() != 0 &&
             
             aclinfo.getAclStatus() == DropboxGenerator.STATUS_COMPLETE) {
            ret++;
         }
      }
      return ret;
   }
      
  // Returns uniq userlist (strings) for anyone accessing any files
  //   in this file scope
   public synchronized Vector userList() 
      throws DboxException {

      Vector ret = new Vector();
      Hashtable lhash = new Hashtable();
      Enumeration enum = fileAccess.elements();
      while(enum.hasMoreElements()) {
         Hashtable h = (Hashtable)enum.nextElement();
         Enumeration enum2 = h.keys();
         while(enum2.hasMoreElements()) {
            String name = (String)enum2.nextElement();
            if (lhash.get(name) == null) {
               lhash.put(name, name);
               ret.addElement(name);
            }
         }
      }
      return ret;
   }
            
  // Return vec contains DboxFileAclInfo
   public synchronized Vector accessAclsForFile(long fileid) 
      throws DboxException {

      Vector ret = new Vector();
      Hashtable uhash = (Hashtable)fileAccess.get(new Long(fileid));
      if (uhash != null) {
         Enumeration enum = uhash.elements();
         while(enum.hasMoreElements()) {
            DboxFileAclInfo aclinfo = (DboxFileAclInfo)enum.nextElement();
            ret.addElement(aclinfo);
         }
      }
      return ret;
   }
      
   public String toString() {
      String ret = "File Access:";
      Enumeration enum = fileAccess.keys();
      while(enum.hasMoreElements()) {
         Long fid = (Long)enum.nextElement();
         ret += Nester.nest("\n---- File : " + fid.longValue() + 
                            " has ACLs ->");
         Hashtable hash = (Hashtable)fileAccess.get(fid);
         Enumeration enum2 = hash.elements();
         while(enum2.hasMoreElements()) {
            AclInfo acl = (AclInfo)enum2.nextElement();
            ret += Nester.nest("\n" + acl.toString(), 2);
         }
      }
         
      return ret;
   }
}
