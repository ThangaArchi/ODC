/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2000-2004                                     */
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
 * Created on Sep 5, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.setmet.prepare;


import java.util.ArrayList;

import oem.edge.ets.fe.workflow.core.WorkflowException;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SetMetPrepareBL {
		
	public ArrayList getpreviousIssueList(String projectID){
		
		ArrayList list = null;
		try{
			SetMetPrepareDAO dao = new SetMetPrepareDAO();
			list = dao.getpreviousIssueList(projectID);
			if (list==null)
				 throw new WorkflowException("Value object is empty");
		}catch(WorkflowException we){
			
		}catch(Exception e){
			
		}
		return list;
	}

}
