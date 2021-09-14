package oem.edge.ed.odc.webdropbox.server;

import javax.servlet.http.HttpServletRequest;

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
 * WebDboxInboxForm.java is the Action Form associated with important
 * elements on the WebDboxInbox.jsp.Also provides validations for form elements. * 
 **/
public class WebDboxInboxForm extends ActionForm {
	
	//	Hidden
	protected String highlitPkg;
	protected String inboxPkgToLit;
	
	protected String uploadFile;
	
	public static final String PKGID        = "PKGID";
	public static final String FILEID        = "FILEID";
	public static final String FILENAME        = "FILENAME";
	

	//setter,getters
	public String  getHighlitPkg()     { return highlitPkg; }
	public void    setHighlitPkg(String v) { highlitPkg = v;    }
	
	
	protected String packToTrash;
	protected String packTrashOper;
	
	protected String modifyFieldValue      = "NoPackageSelected";
        protected String modifyFieldName       = "Package Name";
	
	
	public String getInboxPkgToLit() { return inboxPkgToLit; }
	public void setInboxPkgToLit(String string) { inboxPkgToLit = string; }
	
	public String  getUploadFile()     { return uploadFile; }
	public void    setUploadFile(String v) { uploadFile = v;    }
	
	public String getPackToTrash() { return packToTrash; }
	public void setPackToTrash(String string) {	packToTrash = string; }
	
	public String getPackTrashOper() {	return packTrashOper;	}
	public void setPackTrashOper(String string) {	packTrashOper = string;	}
	
	
	public String  getModifyFieldName()     { return modifyFieldName; }
	public void    setModifyFieldName(String v) { modifyFieldName = v;    }
   
	public String  getModifyFieldValue()     { return modifyFieldValue; }
	public void    setModifyFieldValue(String v) { modifyFieldValue = v;    }
    

	public void reset(ActionMapping mapping, HttpServletRequest request) {

	// Reset field values here.			
	setHighlitPkg("NoPackageSelected");
	setInboxPkgToLit("NoPackageSelected");	
	setModifyFieldName("Package Name");
	setModifyFieldValue("NoPackageSelected");
	setUploadFile("NoFileSelected");
		
	}

	public ActionErrors validate(
		ActionMapping mapping,
		HttpServletRequest request) {
			
			
			
			ActionErrors errors = super.validate(mapping, request);
		    if (errors == null) errors = new ActionErrors();
		    
			String s = getHighlitPkg();
			
			if (s == null) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.badlitPkg"));
			}
				 
			s = getUploadFile();
			
			if (s == null) {
					errors.add(ActionErrors.GLOBAL_ERROR, 
			  	    new ActionError("error.badlitPkg"));
		    }		
		
		// Validate the fields in your form, adding
		// adding each error to this.errors as found, e.g.

		// if ((field == null) || (field.length() == 0)) {
		//   errors.add("field", new org.apache.struts.action.ActionError("error.field.required"));
		// }
		return errors;

	}
	
	
	
	

	


	


}
