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

package oem.edge.ets.fe.documents.processor;

import org.apache.struts.action.ActionMapping;

/**
 * Extends the Struts ActionMapping Class to provide more functionality
 * @author v2srikau
 */
public class DocActionMapping extends ActionMapping {

	/** Stores the Action being performed */
	private String m_strAction;
	
	/**
	 * @return
	 */
	public String getAction() {
		return m_strAction;
	}

	/**
	 * @param strAction
	 */
	public void setAction(String strAction) {
		m_strAction = strAction;
	}

}
