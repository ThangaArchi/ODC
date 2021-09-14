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
 * Creation date: (4/28/2003 9:18:53 AM)
 * @author: Mike Zarnick
 */
public class GridFileAccess extends FileAccess {
/**
 * accessAclsForFile method comment.
 */
public java.util.Vector accessAclsForFile(long fileid)       
   throws DboxException {

   return new Vector();
}
/**
 * add method comment.
 */
public void add(long fileid, oem.edge.ed.odc.dropbox.common.AclInfo aclinfo,
                User user) 
   throws DboxException {

}
/**
 * fileAccessedBy method comment.
 */
public DboxFileAclInfo fileAccessedBy(long fileid, String user) 
   throws DboxException {

   return null;
}
/**
 * filesAccessedByUser method comment.
 */
public java.util.Vector filesAccessedByUser(String user) 
   throws DboxException {

   return new Vector();
}
/**
 * numberFilesAccessedBy method comment.
 */
public int numberFilesAccessedBy(String user) 
   throws DboxException {
   
   return 0;
}
/**
 * remove method comment.
 */
public void remove(long fileid) throws DboxException {
}
public void remove(User user, long fileid) throws DboxException {

}
/**
 * statusCompleteFor method comment.
 */
public int statusCompleteFor(String user) throws DboxException {
   return 0;
}
/**
 * userList method comment.
 */
public java.util.Vector userList() throws DboxException {

   return new Vector();
}
}
