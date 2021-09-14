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

public abstract class FileManager {
   
   protected static 
      String update_lock_clause = " FOR UPDATE WITH RS USE AND KEEP UPDATE LOCKS ";
   
   protected DboxFileAllocator fileAllocator = null;
   protected PackageManager packageManager   = null;
   
   public FileManager(PackageManager pm,
                      DboxFileAllocator fa) { 
      packageManager = pm;
      fileAllocator = fa; 
   }
         
   public boolean lockfile(long fileid) throws DboxException { 
      return lockfile(-1, fileid); 
   }
   
   public boolean lockfile(long packid, long fileid) throws DboxException { return true; }
   
   public boolean lockpackage(long packid) throws DboxException { 
      return lockfile(packid, -1); 
   }
         
   public DboxFileAllocator getFileAllocator() { return fileAllocator; }
         
   public abstract int cleanUnreferencedFiles(int tot) throws DboxException;
   
   public int cleanUnreferencedFiles() throws DboxException {
      return cleanUnreferencedFiles(-1);
   }
   
   public abstract DboxFileInfo lookupFile(long fileid) throws DboxException;
      
   public abstract DboxFileInfo createFile(String file, long len, long poolid)
      throws DboxException;
        
   public abstract void addFile(DboxFileInfo info) throws DboxException;
      
   public abstract void removeFile(long itemid) throws DboxException;
   
   public abstract Vector filesMatchingExpr(String exp,
                                            boolean isReg) throws DboxException;
   public abstract Vector filesMatchingExprWithAccess(User user, 
                                                      boolean ownerOrAccessor,
                                                      String exp, 
                                                      boolean isReg) 
      throws DboxException;
}   
