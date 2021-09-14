/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004                                     */
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

package oem.edge.ets.fe.aic.dyntab.actions;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorActionForm;
import org.apache.struts.validator.ValidatorForm;

/**
 * Form bean for a Struts application.
 * Users may access 4 fields on this form:
 * <ul>
 * <li>template_id - [your comment here]
 * <li>is_active - [your comment here]
 * <li>template_update_date - [your comment here]
 * <li>template_name - [your comment here]
 * </ul>
 * @version 	1.0
 * @author
 */
public class AICTemplateDataForm extends ValidatorForm {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";

	private String template_id = null;
	private String is_active = null;
	private String template_name = null;
	private String template_update_date = null;

	/**
	 * Get template_id
	 * @return String
	 */
	public String getTemplate_id() {
		return template_id;
	}

	/**
	 * Set template_id
	 * @param <code>String</code>
	 */
	public void setTemplate_id(String t) {
		this.template_id = t;
	}

	/**
	 * Get is_active
	 * @return String
	 */
	public String getIs_active() {
		return is_active;
	}

	/**
	 * Set is_active
	 * @param <code>String</code>
	 */
	public void setIs_active(String i) {
		this.is_active = i;
	}

	/**
	 * Get template_name
	 * @return String
	 */
	public String getTemplate_name() {
		return template_name;
	}

	/**
	 * Set template_name
	 * @param <code>String</code>
	 */
	public void setTemplate_name(String t) {
		this.template_name = t;
	}

	/**
	 * Get template_update_date
	 * @return String
	 */
	public String getTemplate_update_date() {
		return template_update_date;
	}

	/**
	 * Set template_update_date
	 * @param <code>String</code>
	 */
	public void setTemplate_update_date(String t) {
		this.template_update_date = t;
	}
	
/*
	public void reset(ActionMapping mapping, HttpServletRequest request) {

		

		template_id = null;
		is_active = null;
		//template_name = null;
		template_update_date = null;

	}

	public ActionErrors validate(
		ActionMapping mapping,
		HttpServletRequest request) {

		ActionErrors errors = new ActionErrors();
		// Validate the fields in your form, adding
		// adding each error to this.errors as found, e.g.

		// if ((field == null) || (field.length() == 0)) {
		//   errors.add("field", new org.apache.struts.action.ActionError("error.field.required"));
		// }
		return errors;

	}
	*/
	/*
	public ActionErrors validate(ActionMapping mapping,HttpServletRequest request) {
		
	  ActionErrors errors = super.validate(mapping, request);  
  
	  // Print out the error list in some way shape or form
	  // basic attempt:
	  System.out.println(errors.size() + " error(s) detected");
	  System.out.println(errors);
	  return errors;
	}
	*/

}
