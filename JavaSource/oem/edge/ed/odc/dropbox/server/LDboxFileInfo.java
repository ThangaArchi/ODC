package oem.edge.ed.odc.dropbox.server;

import  oem.edge.ed.odc.dropbox.common.*;
import  oem.edge.ed.odc.ftp.common.*;
import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.dsmp.server.*;
import  oem.edge.ed.odc.util.*;

import java.lang.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;

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

public class LDboxFileInfo extends DboxFileInfo {
   
   Vector components = new Vector();
   Vector fileslots  = new Vector();
   
   public LDboxFileInfo(FileManager fm) { 
      super(fm);
   }
   
   public LDboxFileInfo(FileManager fm, String n, long sz, byte stat, 
                        long isz) { 
      super(fm, n, sz, stat, isz);
   }
   
   public Vector getComponents() throws DboxException { return components; }
   
   public DboxFileComponent getComponent(long ofs) throws DboxException { 
      Iterator it = components.iterator();
      while(it.hasNext()) {
         DboxFileComponent fc = (DboxFileComponent)it.next();
         if (fc.getStartingOffset() == ofs) return fc;
      }
      
      throw new DboxException("Component at ofs " + ofs + 
                              " does not exists for file " + fileid);
   }
   
   public Vector getFileSlots() throws DboxException {
      return fileslots;
   }
   
   public void truncate(long filelen)  throws DboxException {
      throw new DboxException("Truncate not yet implemented");
   }
   
   public void cullSlots()  throws DboxException {
      throw new DboxException("cullSlots");
   }
   
   public DboxFileSlot allocateFileSlot(User user)  throws DboxException {
      throw new DboxException("allocateFileSlot not yet implemented");
   }
   
   public void releaseFileSlot(User user, long slotid)  throws DboxException {
      throw new DboxException("releaseFileSlot not yet implemented");
   }
   
   public void removeFileSlot(User user, long slotid)  throws DboxException {
      throw new DboxException("removeFileSlot not yet implemented");
   }
   
   public DboxFileSlot getFileSlot(long slotid) throws DboxException {
      throw new DboxException("getFileSlot not yet implemented");
   }
   
   public void recalculateFileSize() { 
      Enumeration enum = components.elements();
      filesize = 0;
      while(enum.hasMoreElements()) {
         DboxFileComponent comp = (DboxFileComponent)enum.nextElement();
         filesize += comp.getFileSize();
      }
   }
      
   public DboxFileComponent getComponent(int idx) throws DboxException {
      if (idx < 1 || idx > components.size()) {
         throw new DboxException("Component index out of range", 0);
      }
         
      return (DboxFileComponent)components.elementAt(idx-1);
   }
      
   public DboxFileComponent createComponent() throws DboxException {
      DboxFileComponent ret = null;
      synchronized(components) {
            
         long csize = intendedSize-getFileSize();
         
        // This allocates the space from fileManager
         ret = new DboxFileComponent(fileManager, getFileId(), 0, csize, 0);
         ret.acquireSpace(getPoolId());
         components.addElement(ret);
      }
      return ret;
   }
}
