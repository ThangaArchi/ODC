package oem.edge.ed.odc.dropbox.server;

import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.util.*;

import java.io.*;

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

/**
 * Insert the type's description here.
 * Creation date: (5/2/2003 1:44:47 PM)
 * @author: Mike Zarnick
 */
public class GridFileComponent extends DboxFileComponent {
/**
 * GridFileComponent constructor comment.
 * @param fm oem.edge.ed.odc.dropbox.server.FileManager
 * @param fid long
 * @param compnum int
 * @param size long
 * @param intendedSize long
 */
GridFileComponent(FileManager fm, long fid, long size, long intendedSize, long sofs) {
	super(fm, fid, size, intendedSize, 0);
}
/**
 * GridFileComponent constructor comment.
 * @param fm oem.edge.ed.odc.dropbox.server.FileManager
 * @param fid long
 * @param compnum int
 * @param size long
 * @param intendedSize long
 * @param fullpath java.lang.String
 */
GridFileComponent(FileManager fm, long fid, long size, 
                  long intendedSize, long sofs, String fullpath) {
	super(fm, fid, size, intendedSize, sofs, fullpath);
}

public long recalculateFileSize() {
   File f = new File(filename);
   if (f.exists()) {
      size = f.length();
      intendedSize = size;
   } else {
      size = 0;
      intendedSize = 0;
   }
   return size;
}

/**
 * Insert the method's description here.
 * Creation date: (5/2/2003 1:59:14 PM)
 */
public void acquireSpace(long poolid) {
}
}
