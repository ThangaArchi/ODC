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


package oem.edge.ets.fe.workflow.issue.edit;

import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.util.SelectControl;

import org.apache.commons.logging.Log;

//
/**
 * Class       : Owner
 * Package     : oem.edge.ets.fe.workflow.issue.edit
 * Description : 
 * Date		   : Nov 6, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class Owner extends SelectControl{
	private static Log logger = WorkflowLogger.getLogger(Owner.class);
	private String status = null;
	
	/**
	 * @param string
	 * @param string2
	 */
	public Owner(String string, String string2) {
		
		super(string, string2);
		
	}
	public Owner(String string, String string2, String string3) {
		
		super(string, string2);
		status=string3;
		
	}
	/**
	 * @return Returns the status.
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status The status to set.
	 */
	public void setStatus(String status) {
		this.status = status;
	}
}

