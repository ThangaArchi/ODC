/*
 * Created on Apr 24, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
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
 * DboxFileInfo.java hosts dropbox file related information for the package in context
 *
 **/
public class DboxFileInfo {
	
	  String fileId = "";
	  String fileName = "";
	  String size = "";	  
	  String md5 = "";
	  
   
	  public DboxFileInfo() {
	  }
   
	 
	  public String getFileId()         { return fileId; }
	  public void   setFileId(String v) { fileId = v;    }
   
	  public String getFileName()         { return fileName; }
	  public void   setFileName(String v) { fileName = v;    }
   
	  public String getFileMD5()         { return md5; }
	  public void   setFileMD5(String v) { md5 = v;    }
   
	  public String getFileSize()         { return size; }
	  public void   setFileSize(String v) { size = v;    }
   
	
	
	

}
