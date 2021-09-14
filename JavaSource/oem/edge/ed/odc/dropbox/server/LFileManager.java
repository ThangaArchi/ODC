package oem.edge.ed.odc.dropbox.server;

import  oem.edge.ed.odc.dropbox.common.*;
import  oem.edge.ed.odc.ftp.common.*;
import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.dsmp.server.*;
import  oem.edge.ed.odc.util.*;

import java.lang.*;
import java.io.*;
import java.util.*;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 2003-2006                                    */ 
/*                                                                       */ 
/*     All Rights Reserved                                               */ 
/*     US Government Users Restricted Rights                             */ 
/*                                                                       */ 
/*   The source code for this program is not published or otherwise      */ 
/*   divested of its trade secrets, irrespective of what has been        */ 
/*   deposited with the US Copyright Office.                             */ 
/*   ------------------------------------------------------------------  */
/*     Please do not remove any of these commented lines  21 lines       */
/*   ------------------------------------------------------------------  */
/*                       Copyright Footer Check                          */
/*   ------------------------------------------------------------------  */ 

public class LFileManager extends FileManager {
   
   Hashtable   files        = new Hashtable();
   Hashtable   fileNames    = new Hashtable();
   
   public LFileManager(PackageManager pm,
                      DboxFileAllocator fa) { 
      super(pm, fa);
   }
         
   public int cleanUnreferencedFiles(int tot) throws DboxException {
      System.out.println("LFileManager: cleanUnreferencedFiles: Fill this in!");
      throw new DboxException(
         "LFileManager: cleanUnreferencedFiles: Fill this in!", 0);
   }
   
   public DboxFileInfo lookupFile(long fileid) throws DboxException {
                                      
      DboxFileInfo info = 
         (DboxFileInfo)files.get(new Long(fileid));
      if (info == null) {
         throw new DboxException("LookupFile: file " + fileid +
                                 " not found", 0);
      }
         
      return info;
   }
   
   public DboxFileInfo  createFile(String file, long len, 
                                   long poolid) throws DboxException {
   
      if (len == -1) {
         throw new DboxException("createFile: Bad length specified [" +
                                 file + "] len = " + len, 0);
      }
         
      DboxFileInfo info = new LDboxFileInfo(this, file, 0,
                                            DropboxGenerator.STATUS_NONE, len);
                                            
      info.setFileId(IDGenerator.getId());
      info.setPoolId(poolid);
      
      addFile(info);
      return info;
   }   
   
   public synchronized void addFile(DboxFileInfo info) throws DboxException {
      Long l = new Long(info.getFileId());
      if (files.get(l) != null) {
         throw new DboxException("addFileToFileMgr: File " + 
                                 info.getFileId() + " already exists",
                                 0);
      }
         
      files.put(new Long(info.getFileId()), info);
      Vector vec = (Vector)fileNames.get(info.getFileName());
      if (vec == null) {
         vec = new Vector();
         fileNames.put(info.getFileName(), vec);
      }
      vec.addElement(info);
   }
      
   public synchronized void removeFile(long itemid) throws DboxException {
      Long lid = new Long(itemid);
      DboxFileInfo info = (DboxFileInfo)files.remove(lid);
      if (info == null) {
         throw new DboxException("FileManager: removeFile: error: file " + 
                                 itemid + " not found", 0);
      }
         
      Vector vec = (Vector)fileNames.get(info.getFileName());
      if (vec != null) {
         for(int i=0; i < vec.size(); i++) {
            DboxFileInfo tinfo = (DboxFileInfo)vec.elementAt(i);
            if (tinfo.getFileId() == itemid) {
               vec.removeElementAt(i);
               if (vec.size() == 0) {
                  fileNames.remove(info.getFileName());
               }
               break;
            }
         }
      } else {
         System.out.println("removeFile: FileManager: Hmmm ... found id="
                            + itemid + " but filename [" + 
                            info.getFileName() + "] not found");
      }
   }
   
   public Vector filesMatchingExpr(String exp, 
                                   boolean isReg) throws DboxException {
         
      Vector ret = new Vector();
         
      org.apache.regexp.RE re = null;
         
      if (isReg && exp != null) {
         try {
            re = new org.apache.regexp.RE(exp);
         } catch(org.apache.regexp.RESyntaxException syne) {
            throw new DboxException("Invalid regexp: " + exp, 0);
         }
      }
         
      Enumeration enum = fileNames.keys();
      while(enum.hasMoreElements()) {
         String name = (String)enum.nextElement();
         if (exp == null                || 
             (isReg  && re.match(name)) || 
             (!isReg && name.equals(exp))) {
            Vector vec = ((Vector)fileNames.get(name));
            Enumeration enump = vec.elements();
            while(enump.hasMoreElements()) {
               DboxFileInfo info = 
                  (DboxFileInfo)enump.nextElement();
               ret.addElement(info);
            }
         }
      }
      return ret;
   }         
   
   public Vector filesMatchingExprWithAccess(User user, boolean ownerOrAccessor,
                                             String exp, 
                                             boolean isReg) throws DboxException {
         
System.out.println("1");
      Vector ret = new Vector();
         
System.out.println("1");
      org.apache.regexp.RE re = null;
         
System.out.println("1");
      if (isReg) {
         if (exp == null) {
            throw new DboxException("Invalid regexp: " + exp, 0);
         }
         
         try {
            re = new org.apache.regexp.RE(exp);
         } catch(org.apache.regexp.RESyntaxException syne) {
            throw new DboxException("Invalid regexp: " + exp, 0);
         }
      }
         
      Enumeration enum = fileNames.keys();
      while(enum.hasMoreElements()) {
         String name = (String)enum.nextElement();
         if (exp == null                || 
             (isReg  && re.match(name)) || 
             (!isReg && name.equals(exp))) {
            Vector vec = ((Vector)fileNames.get(name));
            Enumeration enump = vec.elements();
            while(enump.hasMoreElements()) {
               DboxFileInfo info = 
                  (DboxFileInfo)enump.nextElement();
                  
               if (ownerOrAccessor) {
                  if (packageManager.isFileOwner(user, info)) {
                     ret.addElement(info);
                  }
               } else if (packageManager.canAccessFile(user, info, false)) {
                  ret.addElement(info);
               }
            }
         }
      }
      return ret;
   }         
   
}   
