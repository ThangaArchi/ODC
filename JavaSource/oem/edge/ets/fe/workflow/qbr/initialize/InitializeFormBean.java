/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     © Copyright IBM Corp. 2001-2006                                     */
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


package oem.edge.ets.fe.workflow.qbr.initialize;

import oem.edge.ets.fe.workflow.core.WorkflowForm;

/**
 * Class       : InitializeFormBean
 * Package     : oem.edge.ets.fe.workflow.qbr.initialize
 * Description : 
 * Date		   : Feb 2, 2007
 * 
 * @author     : Pradyumna Achar
 */
public class InitializeFormBean extends WorkflowForm {
	public InitializeFormBean(){
		workflowObject=new InitializeVO();
	}
}

