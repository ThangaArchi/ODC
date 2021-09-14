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

public abstract class DboxFileAllocator {

   static protected   long MAX_COMPONENT_SIZE     = 50 * 1024 * 1024;
   
   static public final int ALLOCATION_BALANCED     = 1;
   static public final int ALLOCATION_PRIORITY     = 2;
   
   static public final int ALLOCATION_START        = ALLOCATION_BALANCED;
   static public final int ALLOCATION_END          = ALLOCATION_PRIORITY;
   
   protected int allocation_policy = ALLOCATION_PRIORITY;
   
   protected PackageManager packageManager = null;
   public void setPackageManager(PackageManager pm) { packageManager = pm;   }
   public PackageManager getPackageManager()        { return packageManager; }
   
   
  // Default allocation policy is PRIORITY. This means the FileAreas are
  //  used in priority order. 
   public int getAllocationPolicy() { return allocation_policy; }
   public void setAllocationPolicy(int p) throws DboxException {
      if (p < ALLOCATION_START || p > ALLOCATION_END) {
         throw new DboxException(
            "DboxFileAllocator: setAllocationPolicy: Invalid # " + p, 0);
      }
      allocation_policy = p;
   }

   
  // All policies are ignored for now. More thought needed. Just do PRIORITY
  // Assume DB tables are locked across this call
  // FileArea's come in sorted by Priority
   protected DboxSpaceAllocation selectByPolicy(Vector v, long sz) {
      return selectByPolicy(v.elements(), sz);
   }
   protected DboxSpaceAllocation selectByPolicy(Enumeration enum, long sz) {
      DboxSpaceAllocation ret = null;
      
      if (sz <= 0) return null;
      
      if (sz > getMaxComponentSize()) sz = getMaxComponentSize();
      
      while(enum.hasMoreElements()) {
         DboxFileArea fa = (DboxFileArea)enum.nextElement();
         long sleft = fa.spaceLeft();
         if (fa.getState() == fa.STATE_NORMAL && sleft > 0) {
         
            long available = sleft > sz?sz:sleft;
            if (allocation_policy == ALLOCATION_PRIORITY) {
               return new DboxSpaceAllocation(fa, available);
            }
            
            if (ret == null) {
               ret = new DboxSpaceAllocation(fa, available);
               
            } else if (allocation_policy == ALLOCATION_BALANCED &&
                       fa.percentUsed() < ret.getFileArea().percentUsed()) {
               ret = new DboxSpaceAllocation(fa, available);
            }
         }
      }
      
      return ret;
   }
   
   static public void setMaxComponentSize(long v) { MAX_COMPONENT_SIZE = v; }
   static public long getMaxComponentSize()       { return MAX_COMPONENT_SIZE; }
   
   public abstract SpaceAllocation allocateSpace(long intendedSize, long poolid) 
      throws DboxException;
      
   public abstract void returnSpace(String dir, long space) throws DboxException;
   public void returnSpace(SpaceAllocation sa) throws DboxException {
      returnSpace(sa.getDirectory(), sa.getSize());
   }
   
   public abstract Vector getFileAreas() throws DboxException;
}
