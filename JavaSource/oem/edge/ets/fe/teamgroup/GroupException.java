/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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

package oem.edge.ets.fe.teamgroup;

import oem.edge.ets.fe.teamgroup.GroupConstants;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;

/**
 * @author vishal
 */
public class GroupException extends Exception {

	ActionErrors m_pdErrors = null;

	/**
	 * @param strMsgKey
	 */
	public GroupException(String strMsgKey) {
		m_pdErrors = new ActionErrors();
		m_pdErrors.add(
			GroupConstants.MSG_USER_ERROR,
			new ActionMessage(strMsgKey));
	}

	/**
	 * @param pdErrors
	 */
	public GroupException(ActionErrors pdErrors) {
		m_pdErrors = pdErrors;
	}

	/**
	 * @return
	 */
	public ActionErrors getErrors() {
		return m_pdErrors;
	}

}
