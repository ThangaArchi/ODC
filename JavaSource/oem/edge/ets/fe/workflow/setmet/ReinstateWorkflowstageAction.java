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


package oem.edge.ets.fe.workflow.setmet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.brand.ETSUnbrandedProperties;
import oem.edge.ets.fe.documents.data.DocumentDAO;
import oem.edge.ets.fe.workflow.core.WorkflowAction;
import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;



/**
 * @author ryazuddin
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ReinstateWorkflowstageAction extends WorkflowAction {

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.WorkflowAction#executeWorkflow(org.apache.struts.action.ActionMapping, oem.edge.ets.fe.workflow.core.WorkflowForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward executeWorkflow(ActionMapping mapping,
			WorkflowForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		
		HttpSession sessionobj = request.getSession(true); 
		WorkflowStageForm sForm = (WorkflowStageForm)form;
		
		System.out.println("Reinstate action is:" + sForm.getAction() + ":");
		boolean canAccess = false;
		if(isSuperAdmin(request) || isWspaceOwner(request) ||isWorkflowAdmin(request) || isWspaceMgr(request))
			canAccess = true;
		System.out.println("Reinstate canAccess is:" + canAccess + ":");
		
		if(canAccess)
			request.setAttribute("ACCESS_DENIED","false");
		else
			request.setAttribute("ACCESS_DENIED","true");
		
		
		String workflowID = request.getParameter("workflowID");
		String opn = request.getParameter("opn");
		SetMetIdentifyStageObject object = new SetMetIdentifyStageObject();
		SetMetBL businessLogic = new SetMetBL();
		object.setProjectID(projectID);
		
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
			
			SetMetDAO smdao = new SetMetDAO();
			smdao.reinstateWorkflow(projectID, workflowID, loggedUser);
			
		}catch(Exception ex){
			logger.error("The exception caught at ShowWorkflowStageAction.java at getDAO()",ex);
		}
        
        
		//StageCollection stagecoll = new StageCollection();
		//stagecoll.wforderedmap = new OrderedMap();
		//stagecoll.wforderedmap.put("IDENTIFY",setmetobj);
        
        //String wid = setmetobj.getWorkflowID().trim();
		request.setAttribute("wid",workflowID);        
		HashMap hashmap = new HashMap(); 
		hashmap.put("IDENTIFY",setmetobj);
		request.setAttribute("SETMETSTAGE-OBJECT",hashmap);

		if(!canAccess)
		{
			ETSUnbrandedProperties udBranding =      new ETSUnbrandedProperties();
			response.sendRedirect(udBranding.getUnauthorizedURL());     
			return mapping.findForward("signOn"); 
		} else
			return mapping.findForward("viewsetmet");
	}
	private static Log logger	=		WorkflowLogger.getLogger(ReinstateWorkflowstageAction.class);
}
