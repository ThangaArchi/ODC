package oem.edge.ed.odc.dropbox.server;

import  oem.edge.ed.odc.dropbox.common.*;
import  oem.edge.ed.odc.ftp.common.*;
import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.dsmp.server.*;
import  oem.edge.ed.odc.util.*;

import java.lang.*;
import java.io.*;
import java.util.*;

public abstract class FileAccess {
   
   public abstract void add(long fileid, AclInfo aclinfo, User user) 
      throws DboxException;
   public void add(long fileid, AclInfo aclinfo) throws DboxException {
      throw new DboxException("Error ... don't use FileAccess.add w/o user!", 0);
   }
   public abstract void remove(long fileid) throws DboxException;
   public abstract void remove(User user, long fileid) throws DboxException;
      
  // Return vec contains DboxFileAclInfo
   public abstract Vector filesAccessedByUser(String user) throws DboxException;
      
  // Returns number of files access by the specified user
   public abstract int numberFilesAccessedBy(String user)
      throws DboxException;
   public abstract DboxFileAclInfo fileAccessedBy(long fileid, String user)
      throws DboxException;
      
  // returns the number of complete uniq file xfers
   public abstract int statusCompleteFor(String user)
      throws DboxException;
      
  // Returns uniq userlist (strings) for anyone accessing any files
  //   in this file scope
   public abstract Vector userList() throws DboxException;
      
  // Returns AclInfo objs, one per user which has accessed any files
  //  for this access scope
   public synchronized Vector accessSummary(int numexpected)
      throws DboxException {
      
      Vector ret = new Vector();
      Vector ulist = userList();
      Enumeration uenum = ulist.elements();
      while(uenum.hasMoreElements()) {
         String username = (String)uenum.nextElement();
         Vector vec = filesAccessedByUser(username);
            
         AclInfo taclinfo = null;
            
         int numcomplete = 0;
         Enumeration enum = vec.elements();
         while(enum.hasMoreElements()) {
            DboxFileAclInfo aclinfo = (DboxFileAclInfo)enum.nextElement();
            
           // skip fileid 0 ... mark indicator
            if (aclinfo.getFileId() == 0) continue;
               
            if (taclinfo == null) {
               taclinfo = new AclInfo(aclinfo);
            }
               
            if (aclinfo.getAclStatus() == DropboxGenerator.STATUS_COMPLETE){
               numcomplete++;
            }
         }
            
         if (taclinfo != null) {
            if (numcomplete >= numexpected) {
               taclinfo.setAclStatus(DropboxGenerator.STATUS_COMPLETE);
            } else if (numcomplete > 0) {
               taclinfo.setAclStatus(DropboxGenerator.STATUS_PARTIAL);
            } else {
               taclinfo.setAclStatus(DropboxGenerator.STATUS_FAIL);
            }
            ret.addElement(taclinfo);
         }
      }
      return ret;
   }
      
  // Return vec contains DboxFileAclInfo
   public abstract Vector accessAclsForFile(long fileid) throws DboxException;
   
   public String toString() {
      return "";
   }
}
