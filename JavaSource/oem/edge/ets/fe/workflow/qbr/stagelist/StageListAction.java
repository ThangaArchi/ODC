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


package oem.edge.ets.fe.workflow.qbr.stagelist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.documents.data.DocumentDAO;
import oem.edge.ets.fe.workflow.core.WorkflowAction;
import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.qbr.initialize.InitializeVO;
import oem.edge.ets.fe.workflow.setmet.SetMetBL;
import oem.edge.ets.fe.workflow.setmet.SetMetIdentifyStageObject;
import oem.edge.ets.fe.workflow.setmet.WorkflowStageForm;
import oem.edge.ets.fe.workflow.util.DetailsUtils;
import oem.edge.ets.fe.workflow.util.MiscUtils;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Class       : StageListAction
 * Package     : oem.edge.ets.fe.workflow.qbr.stagelist
 * Description : 
 * Date		   : Feb 7, 2007
 * 
 * @author     : Pradyumna Achar
 */
public class StageListAction extends WorkflowAction {

	private static Log logger = WorkflowLogger.getLogger(StageListAction.class);
	private DetailsUtils d = null;
	
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.WorkflowAction#executeWorkflow(org.apache.struts.action.ActionMapping, oem.edge.ets.fe.workflow.core.WorkflowForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward executeWorkflow(ActionMapping mapping,
			WorkflowForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {

		boolean canAccess = false;
		if(isSuperAdmin(request) || isWspaceOwner(request) ||isWorkflowAdmin(request) || isWspaceMgr(request))
			canAccess = true;
		System.out.println("QBR View canAccess is:" + canAccess + ":");
		
		if(canAccess)
			request.setAttribute("ACCESS_DENIED","false");
		else
			request.setAttribute("ACCESS_DENIED","true");
		
		if(gatekeeper(mapping, form, request, response)==false)
			return mapping.findForward("badURL");
		attachmentSection(mapping,form,request,response);
		return mapping.findForward("viewQBR");
	}
	private boolean gatekeeper(ActionMapping mapping, WorkflowForm form, HttpServletRequest request, HttpServletResponse response) {
		if(mapping==null || request==null || response==null)
			return false;
		
		if(projectID==null || !MiscUtils.isValidProject(projectID))
		{
			request.setAttribute("WFerr", "No project ID or bad project ID");
			return false;
		}
		String workflowID = MiscUtils.getPA(request,"workflowID");
		if(workflowID==null || !MiscUtils.isValidWorkflow(projectID,workflowID))
		{
			request.setAttribute("WFerr", "No workflow ID or bad workflow ID");
			return false;
		}
		d=new DetailsUtils();
		d.setProjectID(projectID);
		d.setWorkflowID(MiscUtils.getPA(request,"workflowID"));
		d.extractWorkflowDetails();
		/*if(!("QBR".equals(d.getWwf_type())))
		{
			request.setAttribute("WFerr", "QBR function invoked on non-QBR workflow");
			return false;
		}*/
		return true;
	}
	private void attachmentSection(ActionMapping mapping, WorkflowForm form, HttpServletRequest request, HttpServletResponse response) {
		
		HttpSession sessionobj = request.getSession(true); 
		WorkflowStageForm sForm = (WorkflowStageForm)form;
		SetMetIdentifyStageObject object = new SetMetIdentifyStageObject();
		SetMetBL businessLogic = new SetMetBL();
		object.setProjectID(projectID);
		String workflowID = request.getParameter("workflowID");
		Vector v = new Vector();
		ArrayList docID = null;
		DocumentDAO udDAO = null;
		Vector attachments = new Vector();
		SetMetBL bl = new SetMetBL();
		SetMetIdentifyStageObject setmetobj = (SetMetIdentifyStageObject) bl.getWorkflowObject(workflowID);
		try{
			udDAO = getDAO();
			v = udDAO.getAllDocs(projectID,"4","DESC",true,loggedUser);
			docID = oem.edge.ets.fe.workflow.setmet.validate.ValidateDocumentStageDAO.getAttachmentIDs(projectID,workflowID);
			for(int x=0 ; x< v.size();x++){
				ETSDoc doc = (ETSDoc)v.get(x);
				if(docID.contains(String.valueOf(doc.getId()))){
					attachments.add(doc);				 	     
				}
			 			
			}
		
			sForm.setDocuments(attachments);
		}catch(Exception ex){
			logger.error("The exception caught at StageListAction.java at getDAO()",ex);
		}
	}
}

