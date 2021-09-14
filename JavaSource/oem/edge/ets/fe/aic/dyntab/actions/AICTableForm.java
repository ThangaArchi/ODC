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

import oem.edge.ets.fe.documents.BaseDocumentForm;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Form bean for a Struts application.
 * Users may access 6 fields on this form:
 * <ul>
 * <li>doc_id - [your comment here]
 * <li>template_id - [your comment here]
 * <li>table_name - [your comment here]
 * <li>is_active - [your comment here]
 * <li>docfile_update_date - [your comment here]
 * <li>table_id - [your comment here]
 * </ul>
 * @version 	1.0
 * @author
 */
public class AICTableForm extends BaseDocumentForm {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.4";
	
	private String doc_id;
	private String table_name = null;
	private String template_id;
	private String is_active = null;
	private String table_id;
	private String docfile_update_date = null;

	

	/**
	 * Get table_name
	 * @return String
	 */
	public String getTable_name() {
		return table_name;
	}

	/**
	 * Set table_name
	 * @param <code>String</code>
	 */
	public void setTable_name(String t) {
		this.table_name = t;
	}

	/**
	 * Get template_id
	 * @return int
	 */
	public String getTemplate_id() {
		return template_id;
	}

	/**
	 * Set template_id
	 * @param <code>int</code>
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
	 * Get docfile_update_date
	 * @return String
	 */
	public String getDocfile_update_date() {
		return docfile_update_date;
	}

	/**
	 * Set docfile_update_date
	 * @param <code>String</code>
	 */
	public void setDocfile_update_date(String d) {
		this.docfile_update_date = d;
	}

	public void reset(ActionMapping mapping, HttpServletRequest request) {

		// Reset values are provided as samples only. Change as appropriate.

		doc_id = null;
		table_name = null;
		template_id = null;
		is_active = null;
		table_id = null;
		docfile_update_date = null;

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
	/**
	 * @return
	 */
	public String getDoc_id() {
		return doc_id;
	}

	/**
	 * @return
	 */
	public String getTable_id() {
		return table_id;
	}

	/**
	 * @param string
	 */
	public void setDoc_id(String string) {
		doc_id = string;
	}

	/**
	 * @param string
	 */
	public void setTable_id(String string) {
		table_id = string;
	}

}
