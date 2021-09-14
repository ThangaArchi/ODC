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
import org.apache.struts.validator.ValidatorForm;

/**
 * Form bean for a Struts application.
 * Users may access 9 fields on this form:
 * <ul>
 * <li>column_name - [your comment here]
 * <li>column_id - [your comment here]
 * <li>template_name - [your comment here]
 * <li>column_update_date - [your comment here]
 * <li>is_active - [your comment here]
 * <li>is_required - [your comment here]
 * <li>column_type - [your comment here]
 * <li>template_id - [your comment here]
 * <li>column_order - [your comment here]
 * </ul>
 * @version 	1.0
 * @author
 */
public class AICColumnDataForm extends ValidatorForm {
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";

	private String column_name = null;
	private String column_id;
	private String template_name = null;
	private String column_update_date = null;
	private String is_active = null;
	private String is_required = null;
	private String column_type = null;
	private String template_id = null;
	private String column_order = null;
	private String action = null;
	/**
	 * Get column_name
	 * @return String
	 */
	public String getColumn_name() {
		return column_name;
	}

	/**
	 * Set column_name
	 * @param <code>String</code>
	 */
	public void setColumn_name(String c) {
		this.column_name = c;
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
	 * Get column_update_date
	 * @return String
	 */
	public String getColumn_update_date() {
		return column_update_date;
	}

	/**
	 * Set column_update_date
	 * @param <code>String</code>
	 */
	public void setColumn_update_date(String c) {
		this.column_update_date = c;
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
	 * Get is_required
	 * @return String
	 */
	public String getIs_required() {
		return is_required;
	}

	/**
	 * Set is_required
	 * @param <code>String</code>
	 */
	public void setIs_required(String i) {
		this.is_required = i;
	}

	/**
	 * Get column_type
	 * @return String
	 */
	public String getColumn_type() {
		return column_type;
	}

	/**
	 * Set column_type
	 * @param <code>String</code>
	 */
	public void setColumn_type(String c) {
		this.column_type = c;
	}

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
	 * Get column_order
	 * @return String
	 */
	public String getColumn_order() {
		return column_order;
	}

	/**
	 * Set column_order
	 * @param <code>String</code>
	 */
	public void setColumn_order(String c) {
		this.column_order = c;
	}

	public void reset(ActionMapping mapping, HttpServletRequest request) {

		// Reset values are provided as samples only. Change as appropriate.

		column_name = null;
		column_id = null;
		template_name = null;
		column_update_date = null;
		is_active = null;
		is_required = null;
		column_type = null;
		template_id = null;
		column_order = null;

	}
	
	public ActionErrors validate(
		ActionMapping mapping,
		HttpServletRequest request) {
			ActionErrors errors = new ActionErrors();
			if(this.action != null)
			{
			
			if(!this.action.equals("Edit"))
			{
				errors = super.validate(mapping, request);	
			}
			}
		// Validate the fields in your form, adding
		// adding each error to this.errors as found, e.g.

		// if ((field == null) || (field.length() == 0)) {
		//   errors.add("field", new org.apache.struts.action.ActionError("error.field.required"));
		// }
		return errors;

	}
	
	/**
	 * @return
	 */
	public String getColumn_id() {
		return column_id;
	}

	/**
	 * @param string
	 */
	public void setColumn_id(String string) {
		column_id = string;
	}

	/**
	 * @return
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param string
	 */
	public void setAction(String string) {
		action = string;
	}

}
