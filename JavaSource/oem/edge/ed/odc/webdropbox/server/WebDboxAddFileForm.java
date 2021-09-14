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
 * WebDboxAddFileForm.java is the Action Form associated with important
 * elements on the AddFiles.jsp.Also provides validations for form elements. 
 *  
 **/
public class WebDboxAddFileForm extends ActionForm {
	
	
	protected FormFile content;

	
	public FormFile getContent() {	return content; 	}
	public void setContent(FormFile file) {	content = file;	}
	
	

	public void reset(ActionMapping mapping, HttpServletRequest request) {

		//Reset field values here.		
		//setUploadFile("NoFileSelected");
		//setFile1name("TestValue");		
	}

	public ActionErrors validate(
		ActionMapping mapping,
		HttpServletRequest request) {
			
			
			
			ActionErrors errors = super.validate(mapping, request);
		    if (errors == null) errors = new ActionErrors();
		    
			//String filename = getContent().getFileName();
			//if (filename.equals("")||filename == null){
			//	errors.add("error.fileName.value", 
			//					new ActionError("error.fileName.value"));
			//}
		
//			String s = getUploadFile();
//
//						if (s == null) {
//								errors.add(ActionErrors.GLOBAL_ERROR, 
//								new ActionError("error.badlitPkg"));
//						}
		
		// Validate the fields in your form, adding
		// adding each error to this.errors as found, e.g.

		// if ((field == null) || (field.length() == 0)) {
		//   errors.add("field", new org.apache.struts.action.ActionError("error.field.required"));
		// }
		return errors;

	}
	
	
	

	

	

}
