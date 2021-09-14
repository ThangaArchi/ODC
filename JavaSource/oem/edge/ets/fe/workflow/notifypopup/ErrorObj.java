/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2006                                     */
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

package oem.edge.ets.fe.workflow.notifypopup;

import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import org.apache.commons.logging.Log;


/**
 * Class       : ErrorObj
 * Package     : oem.edge.ets.fe.workflow.notifypopup
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class ErrorObj extends WorkflowObject {
	
	private static Log logger = WorkflowLogger.getLogger(ErrorObj.class);
	private String errorText;

	public void setErrorText(String e) {
		errorText = e;
	}

	public String getErrorText() {
		return errorText;
	}
}
