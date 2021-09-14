package oem.edge.ed.odc.dropbox.server;

import  oem.edge.ed.odc.dsmp.common.*;
import  oem.edge.ed.odc.util.*;

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


/**
 * Insert the type's description here.
 * Creation date: (4/28/2003 9:16:50 AM)
 * @author: Mike Zarnick
 */
public class GridFileAllocator extends DboxFileAllocator {
	private DboxFileArea area;
/**
 * Insert the method's description here.
 * Creation date: (5/1/2003 4:38:52 PM)
 */
public GridFileAllocator(String gridDirectory,long total,long used,byte fstype) {
	area = new DboxFileArea(gridDirectory,total,used);
	area.fstype = fstype;
}
/**
 * allocateSpace method comment.
 */
public SpaceAllocation allocateSpace(long intendedSize, 
                                     long poolid) throws DboxException {
	DboxSpaceAllocation sa = new DboxSpaceAllocation(area,intendedSize);
	return sa;
}
/**
 * getFileAreas method comment.
 */
public Vector getFileAreas() throws DboxException {
	Vector ret = new Vector();

	ret.addElement(area);

	return ret;
}
/**
 * returnSpace method comment.
 */
public void returnSpace(String dir, long space) throws DboxException {
	// Nothing to do here...
}
}
