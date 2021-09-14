

package oem.edge.ed.odc.webdropbox.server;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.*;
import javax.servlet.http.*;
import javax.servlet.*;

import java.util.*;
import java.text.*;

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
 * WebDboxDispGroupInfoForm.java is the Action Form associated with important
 * elements on the GroupInfo.jsp.Also provides validations for form elements. * 
 **/
public class WebDboxDispGroupInfoForm extends ActionForm {
	

	
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		
	}
 
	public ActionErrors validate(
		ActionMapping mapping,
		HttpServletRequest request) {
			
			
			ActionErrors errors = super.validate(mapping, request);
			if (errors == null) errors = new ActionErrors();
		    
		   
			
		return errors;

	}
	
	
	

	

	


	


}
