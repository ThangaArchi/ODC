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


/*
 * Created on Sep 11, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.stage;

import oem.edge.ets.fe.workflow.core.WorkflowStage;
import oem.edge.ets.fe.workflow.constants.WorkflowConstants;


/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class PrepareStageObject extends WorkflowStage {

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.WorkflowStage#getStageName()
	 */
	public String getStageName() {
		// TODO Auto-generated method stub
		return WorkflowConstants.PREPARE;
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.WorkflowStage#getNextStage()
	 */
	public String getNextStage() {
		// TODO Auto-generated method stub
		return WorkflowConstants.DOCUMENT;
	}

}
