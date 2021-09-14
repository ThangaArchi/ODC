

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
 * WebDboxCrossCompInfoForm.java is the Action Form associated with important
 * elements on the CrossCompInfo.jsp & SentCrossCompInfo.jsp webpages . * 
 **/
public class WebDboxCrossCompInfoForm extends ActionForm {
	
	String crossCompWarning=null;
	String crossCompList=null;
    
    
    /**
	 * @return Returns the crossCompList.
	 */
	public String getCrossCompList() {
		return crossCompList;
	}
	/**
	 * @param crossCompList The crossCompList to set.
	 */
	public void setCrossCompList(String crossCompList) {
		this.crossCompList = crossCompList;
	}
	
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		setCrossCompList("");
	}
 
	public ActionErrors validate(
		ActionMapping mapping,
		HttpServletRequest request) {
			
			
			ActionErrors errors = super.validate(mapping, request);
			if (errors == null) errors = new ActionErrors();
		    
		   
			
		return errors;

	}
	
	
	

	

	


	


	
	/**
	 * @return Returns the crossCompWarning.
	 */
	public String getCrossCompWarning() {
		return crossCompWarning;
	}
	/**
	 * @param crossCompWarning The crossCompWarning to set.
	 */
	public void setCrossCompWarning(String crossCompWarning) {
		this.crossCompWarning = crossCompWarning;
	}
}
