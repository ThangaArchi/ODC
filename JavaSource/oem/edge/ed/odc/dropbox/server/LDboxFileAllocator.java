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
/*     (C)Copyright IBM Corp. 2003-2004,2005,2006                           */
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

public class LDboxFileAllocator extends DboxFileAllocator {

   Hashtable boxes = new Hashtable();
   public LDboxFileAllocator() {
      DboxFileArea a = new DboxFileArea("/tmp/dropbox/DB1", 
                                        200*1024*1024, 
                                        0);
      DboxFileArea b = new DboxFileArea("/tmp/dropbox/DB2", 
                                        200*1024*1024, 
                                        0);
      DboxFileArea c = new DboxFileArea("/tmp/dropbox/DB3", 
                                        200*1024*1024, 
                                        0);
      DboxFileArea d = new DboxFileArea("/tmp/dropbox/DB4", 
                                        1000*1024*1024, 
                                        0);
      a.setState(a.STATE_NORMAL);
      b.setState(b.STATE_NORMAL);
      c.setState(c.STATE_NORMAL);
      d.setState(d.STATE_NORMAL);
      boxes.put(a.getTopLevelDirectory(), a);
      boxes.put(b.getTopLevelDirectory(), b);
      boxes.put(c.getTopLevelDirectory(), c);
      boxes.put(d.getTopLevelDirectory(), d);
   }

   public SpaceAllocation allocateSpace(long intendedSize, 
                                        long poolid) throws DboxException {
      DboxSpaceAllocation sa = selectByPolicy(boxes.elements(), intendedSize);
      if (sa == null) {
         throw new DboxException("No Space Available", 0);
      }
      
      sa.getFileArea().allocateSpace(sa.getSize());
      return sa;
   }
   
   public void returnSpace(String dir, long space) throws DboxException {
      DboxFileArea fa = (DboxFileArea)boxes.get(dir);
      if (fa == null) {
         String s = 
            "returnSpace: No DboxFileArea for [" + dir + "]." +
            " Space=" + space + " is not recorded!";
         System.out.println(s);
         throw new DboxException(s, 0);
      }
      fa.returnSpace(space);
   }
   
   public Vector getFileAreas() throws DboxException {
      Vector ret = new Vector();
      if (false) throw new DboxException("sok", 0);
      
      Enumeration enum = boxes.elements();
      while(enum.hasMoreElements()) {
         ret.addElement(enum.nextElement());
      }
      return ret;
   }
}
