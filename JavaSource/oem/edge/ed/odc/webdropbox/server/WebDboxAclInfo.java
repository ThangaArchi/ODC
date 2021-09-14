package oem.edge.ed.odc.webdropbox.server;

import org.apache.struts.action.*;
import javax.servlet.http.*;
import javax.servlet.*;

import java.util.*;
import java.text.*;
import java.lang.*;

/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005                                          */
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

/**
 * WebDboxAclInfo.java hosts ACL Info for processing the Web Based Dropbox.
 *  
 **/
public class WebDboxAclInfo {
	 String aclname = "";
	 String acltype = "";
	 String created = "";
   
	 public WebDboxAclInfo() {	 	
	 }
   
	 public String getAclName()         { return aclname; }
	 public void   setAclName(String v) { aclname = v;    }
   
	 public String getAclType()         { return acltype; }
	 public void   setAclType(String v) { acltype = v;    }
   
	 public String getAclCreated()         { return created; }
	 public void   setAclCreated(String v) { created = v;    }
	 

	public boolean equals(Object o) {
	   int num = 0;
	   if (o instanceof WebDboxAclInfo) {
		WebDboxAclInfo to = (WebDboxAclInfo)o;
		  if (acltype == to.acltype) {
         
			 if (aclname != null && to.aclname != null) {
				if (!aclname.equals(to.aclname)) return false;
			 } //else if (aclname != to.aclname)   return false;
			 
			 return true;
		  } 
	   } 
	   return false;
	}
	 
	 
}
