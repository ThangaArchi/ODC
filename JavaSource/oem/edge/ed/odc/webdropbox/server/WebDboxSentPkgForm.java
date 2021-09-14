package oem.edge.ed.odc.webdropbox.server;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.*;
import javax.servlet.http.*;
import javax.servlet.*;

import java.util.*;
import java.text.*;
import java.lang.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

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
 * WebDboxSentPkgForm.java is the Action Form associated with important
 * elements on the WebDboxSentPkg.jsp.Also provides validations for form elements. 
 *  
 **/
public class WebDboxSentPkgForm extends ActionForm {
	
	
//	Hidden
	protected String highlitSentPkg;
	protected String sentPkgToLit;
	protected String uploadFile;
	protected String litSentPkgStatus;
	protected String filesToDelete;
	protected String fileDelOper;
	
	
	
	public static final String PKGID        = "PKGID";
	public static final String FILEID        = "FILEID";
	public static final String FILENAME        = "FILENAME";
	

	//setter,getters
	public String  getHighlitSentPkg()     { return highlitSentPkg; }
	public void    setHighlitSentPkg(String v) { highlitSentPkg = v;    }
    
	public String  getUploadFile()     { return uploadFile; }
	public void    setUploadFile(String v) { uploadFile = v;    }
	
	protected String packToTrash;
	protected String packTrashOper;
	
	protected String modifyFieldValue      = "NoPackageSelected";
	protected String modifyFieldName       = "Package Name";
	
	
	
	public String getSentPkgToLit() { return sentPkgToLit;	}
	public void setSentPkgToLit(String string) { sentPkgToLit = string;	}
    
    public String getLitSentPkgStatus() { return litSentPkgStatus;	}
	public void setLitSentPkgStatus(String string) { litSentPkgStatus = string; }

	public String getFilesToDelete() {	return filesToDelete;	}
	public void setFilesToDelete(String string) { filesToDelete = string; }
    
	public String getFileDelOper() { return fileDelOper; }
	public void setFileDelOper(String string) {	fileDelOper = string; }

	public String getPackTrashOper() {	return packTrashOper;	}
	public void setPackTrashOper(String string) {	packTrashOper = string;	}
	
	
	public String  getModifyFieldName()     { return modifyFieldName; }
	public void    setModifyFieldName(String v) { modifyFieldName = v;    }
   
    public String  getModifyFieldValue()     { return modifyFieldValue; }
    public void    setModifyFieldValue(String v) { modifyFieldValue = v;    }
   	
	public String getPackToTrash() {  return packToTrash; }	
	public void setPackToTrash(String string) {	packToTrash = string; }
    

	public void reset(ActionMapping mapping, HttpServletRequest request) {
	//	Reset field values here.
		
		 setHighlitSentPkg("NoPackageSelected");
		 setSentPkgToLit("NoPackageSelected");
		 setUploadFile("NoFileSelected");
		 setFilesToDelete("NoFileSelected");
		 setFileDelOper("false");
	     
	     setModifyFieldName("Package Name");
	     setModifyFieldValue("NoPackageSelected");
	}

	public ActionErrors validate(
		ActionMapping mapping,
		HttpServletRequest request) {
			
			
			
			ActionErrors errors = super.validate(mapping, request);
			if (errors == null) errors = new ActionErrors();
			String s = getFilesToDelete();
			
						if (  s == null || s.trim().length() == 0 ) {
							errors.add("error.filedel.value", 
							new ActionError("error.filedel.value"));
						}
		
		return errors;

	}
	
	
	
	

	

		

}
