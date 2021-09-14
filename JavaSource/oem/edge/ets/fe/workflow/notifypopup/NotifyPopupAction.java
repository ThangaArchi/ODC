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

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.workflow.core.WorkflowAction;
import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.util.CharUtils;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Class : NotifyPopupAction Package : oem.edge.ets.fe.workflow.notifypopup
 * Description :
 * 
 * @author Pradyumna Achar
 */
public class NotifyPopupAction extends WorkflowAction

{
	private static Log logger = WorkflowLogger.getLogger(NotifyPopupAction.class);

	public ActionForward executeWorkflow(ActionMapping mapping,	WorkflowForm form, HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException {
		
		ActionForward forward = null;
		NotifyPopupFormBean notifyPopupFormBean = null;
		NotifyPopupPreload preloadBean = null;
		NotifyPopupVO vo = null;
		
		if(form==null)
			System.out.println("PANIC: FORM NULL");

		notifyPopupFormBean = (NotifyPopupFormBean)form;
		
		if (form.getAction() == null) {
			notifyPopupFormBean.reset();
			preloadBean = new NotifyPopupPreload(projectID,loggedUser);
			request.setAttribute("preloadBean",preloadBean);
			vo = (NotifyPopupVO)form.getWorkflowObject();
			vo.setWorkflowID(request.getParameter("workflowID"));
			vo.setProjectID(projectID);
			forward = mapping.findForward("success");
		}
		else {
			vo = (NotifyPopupVO) notifyPopupFormBean.getWorkflowObject();
			if(vo==null)
				System.out.println("PANIC: VO NULL");
			ArrayList errs = getValidationErrors(vo);
			if (errs != null && errs.size() != 0) {
				forward = mapping.findForward("success");
				request.setAttribute("errorMessages", errs);
			}
			else {
				NotifyPopupBL bl = new NotifyPopupBL();
				vo.setProjectID(projectID);
				if (bl.sendNotification(vo,loggedUser) == true) {
					request.setAttribute("close_signal", " ");
					forward = mapping.findForward("success");
				} else {
					System.out.println("Failure not handled but BL returned false.");
					forward = mapping.findForward("failure");
				}
			}
		}
		return (forward);
	}

	/**
	 * @param vo
	 * @return
	 */
	private ArrayList getValidationErrors(NotifyPopupVO vo) {
		ArrayList errs = new ArrayList();
		if(vo.getComments()==null || vo.getComments().trim().length()==0)
			errs.add("You must specify comments");
		if(vo.getComments()!=null && vo.getComments().trim().length()>1000)
			errs.add("Comments must not exceed 1000 characters");
		if(vo.getPeopleToBeNotified()==null || vo.getPeopleToBeNotified().length==0 || vo.getPeopleToBeNotified()[0].trim().length()==0)
			errs.add("You must select atleast one person to be notified");
		if(vo.getComments()!=null && CharUtils.isAlNum(vo.getComments())==false)
			errs.add("Comments can only contain alphabets, digits and space characters");
		
		return errs;
	}
}
