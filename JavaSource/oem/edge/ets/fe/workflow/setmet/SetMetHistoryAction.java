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
 * Created on Nov 10, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.setmet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.workflow.core.WorkflowAction;
import oem.edge.ets.fe.workflow.core.WorkflowForm;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author ryazuddin
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SetMetHistoryAction extends WorkflowAction {

	/*
	 * (non-Javadoc)
	 * 
	 * @see oem.edge.ets.fe.workflow.core.WorkflowAction#executeWorkflow(org.apache.struts.action.ActionMapping,
	 *      oem.edge.ets.fe.workflow.core.WorkflowForm,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward executeWorkflow(ActionMapping mapping,
			WorkflowForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {

		System.out.println("Inside History action:-");
		SetMetHistoryForm hlist = (SetMetHistoryForm) form;
		SetMetBL businessObj = new SetMetBL();

		boolean canAccess = canAccess(request, "SETMET", "CREATE");

		if (canAccess)
			request.setAttribute("ACCESS_DENIED", "false");
		else
			request.setAttribute("ACCESS_DENIED", "true");
 
		//String wfid = "1163144152641-9465";
		
		//String wfid = "1163405493516-2027";
		String wfid = (String)request.getParameter("workflowID");
		ArrayList getlist = businessObj.getHistoryList(wfid);
		
		/*Iterator it = getlist1.iterator();
		ArrayList getlist = (ArrayList) it.next();
		hlist.setHistorylist(getlist);*/
		System.out.println(loggedUser+"logger userrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr^&%&%&%&%&%&%%");
		request.setAttribute("historyList", getlist);
		return mapping.findForward("SetMetHistory");

	}

}
