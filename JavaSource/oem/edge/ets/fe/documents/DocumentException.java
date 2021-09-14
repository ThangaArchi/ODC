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

package oem.edge.ets.fe.documents;

import oem.edge.ets.fe.documents.common.DocConstants;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;

/**
 * @author v2srikau
 */
public class DocumentException extends Exception {

	ActionErrors m_pdErrors = null;

	/**
	 * @param strMsgKey
	 */
	public DocumentException(String strMsgKey) {
		m_pdErrors = new ActionErrors();
		m_pdErrors.add(
			DocConstants.MSG_USER_ERROR,
			new ActionMessage(strMsgKey));
	}

	/**
	 * @param pdErrors
	 */
	public DocumentException(ActionErrors pdErrors) {
		m_pdErrors = pdErrors;
	}

	/**
	 * @return
	 */
	public ActionErrors getErrors() {
		return m_pdErrors;
	}

}
