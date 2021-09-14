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



package oem.edge.ets.fe.workflow.core;


import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.brand.ETSUnbrandedProperties;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.documents.data.DocumentDAO;
import oem.edge.ets.fe.workflow.constants.WorkflowConstants;
import oem.edge.ets.fe.workflow.eventdetailspopupwindow.WorkflowEventDetailsVO;
import oem.edge.ets.fe.workflow.issue.edit.EditIssueVO;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import oem.edge.ets.fe.workflow.setmet.WorkflowMatrixAction;
import oem.edge.ets.fe.workflow.setmet.document.ScorecardDAO;
import oem.edge.ets.fe.workflow.util.DetailsUtils;
import oem.edge.ets.fe.workflow.util.UserUtils;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * The parent for all the ActionForm classes
 * @author  S.Govindaraj
 */
public abstract class WorkflowAction extends Action{
	
	private static final String SESS_EDGEACCESS = "ETS.EDGEACCESSCNTRL";
	private static final String SESS_USERROLE = "ETS.USERROLE";
	private static final String SESS_SUPERADMIN = "ETS.ISSUPERADMIN";
	private static final String SESS_EXECUTIVE = "ETS.ISEXECUTIVE";
	
	
	
	protected ActionErrors errors  = null;
	protected String projectID	   = null;
	protected String tc			   = null;
	protected String action		   = null;
	protected String requestor	   = null;
	protected String sector		   = null;
	protected String loggedUser	   = null;
	protected String company	   = null;
	private   DocumentDAO udDAO    = null;
	private String currStage = "";
	
	/* Additional protected variables for 7.1.1, QBR */
	protected String wf_type = null;
	protected String wf_type_text = "Workflow";
	
	/*
	 * Validate the user whether logged in or not
	 */
	
	
	public ActionForward execute(ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws IOException, ServletException {
		
		ActionForward forward  = null;
		WorkflowForm  WForm  = (WorkflowForm)form;
		EdgeAccessCntrl udEdgeAccess = new EdgeAccessCntrl();
		EdgeAccessCntrl es = new EdgeAccessCntrl();
		ETSProj udProject = null;
		HttpSession pdSession = request.getSession();
		String strUserRole = null;//(String) pdSession.getAttribute(SESS_USERROLE);
		try{            
			projectID = (String)request.getParameter("proj");
			tc		  = (String)request.getParameter("tc");
			action    = (String)request.getParameter("action");
			
			
			
			String workflowName="";
			
			
			System.out.println("The value of action is &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&"+action);
			System.out.println("The value of action is &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&"+projectID);
			System.out.println("The value of action is &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&"+tc);
			
			
			udDAO = getDAO();
			System.out.println("after getDAO();");
			if (!(this instanceof WorkflowMatrixAction)) {
				udProject = udDAO.getProjectDetails(projectID);
				
				request.setAttribute(
						DocConstants.REQ_ATTR_PRIMARYCONTACT,
						udDAO.getProjContactInfo(projectID));
			
			udEdgeAccess.GetProfile(
					response,
					request,
					udDAO.getConnection());
			System.out.println("after GetProfile();");
			}
			else{
				UnbrandedProperties prop = PropertyFactory.getProperty(Defines.AIC_WORKSPACE_TYPE);
					if (!es.GetProfile(response, request, udDAO.getConnection())) {
						System.err.println("GetProfile returned false");
					}
			}
			if (!(this instanceof WorkflowMatrixAction)) {
				if (StringUtil.isNullorEmpty(strUserRole)) {
					strUserRole = ETSUtils.checkUserRole(
							udEdgeAccess,
							udProject.getProjectId());
					pdSession.setAttribute(SESS_USERROLE, strUserRole);
				}
				
				logger.error(
						"USER ROLE for "
						+ udEdgeAccess.gIR_USERN
						+ "IS : "
						+ strUserRole);
				
				logger.debug("The value of StrUserRole ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^"+strUserRole);
				
				
				
				request.setAttribute(
						DocConstants.REQ_ATTR_USERROLE,
						strUserRole);
				request.setAttribute(
						DocConstants.REQ_ATTR_EDGEACCESS,
						udEdgeAccess);
				
				request.setAttribute(
						DocConstants.REQ_ATTR_PRIMARYCONTACT,
						udDAO.getProjContactInfo(projectID));
				
				boolean bIsSuperAdmin = false;
				boolean bIsWorkspaceOwner = false;
				boolean bIsWorkspaceMgr= false;
				boolean bIsExecutive = false;
				boolean bIsViewable  = false;
				boolean bIsWorkflowAdmin = false;
				boolean bIsInvalidUser = false;
				boolean bIsVisitor      = false;
				
				if (Defines.INVALID_USER.equals(strUserRole)) {
					bIsInvalidUser = true;
				}
				
				if(bIsInvalidUser){
					ETSUnbrandedProperties udBranding =
						new ETSUnbrandedProperties();
					response.sendRedirect(udBranding.getUnauthorizedURL());
					return mapping.findForward("signOn");
				}
				
				if (strUserRole.equals(Defines.ETS_ADMIN)) {
					bIsSuperAdmin = true;
				}
				
				if (strUserRole.equals(Defines.ETS_EXECUTIVE)) {
					bIsExecutive = true;
				}
				
				if (strUserRole.equals(Defines.WORKFLOW_ADMIN)) {
					
					System.out.println("the value is coming inside ..######################################");
					bIsWorkflowAdmin =true;
				}
				
				
				if (strUserRole.equals(Defines.WORKSPACE_VISITOR) || strUserRole.equals(Defines.WORKSPACE_MEMBER)) {
					bIsViewable =true;
				}

				if(strUserRole.equals(Defines.WORKSPACE_VISITOR)){
				bIsVisitor = true;
			    }

				if (strUserRole.equals(Defines.WORKSPACE_OWNER)){
					bIsWorkspaceOwner = true;
				}
				
				
				if (strUserRole.equals(Defines.WORKSPACE_MANAGER)){
					bIsWorkspaceMgr = true;
				}
				
				
				pdSession.setAttribute(SESS_SUPERADMIN, Boolean.valueOf(bIsSuperAdmin));
				pdSession.setAttribute(SESS_EXECUTIVE, Boolean.valueOf(bIsExecutive));
				pdSession.setAttribute("SESS_WSOWNER", Boolean.valueOf(bIsWorkspaceOwner));
				pdSession.setAttribute("SESS_WSMGR", Boolean.valueOf(bIsWorkspaceMgr));
				pdSession.setAttribute("SESS_VIEWWORKFLOW",Boolean.valueOf(bIsViewable));		    
				pdSession.setAttribute("SESS_WORKFLOWADMIN",Boolean.valueOf(bIsWorkflowAdmin));
			    pdSession.setAttribute("SESS_WORKFLOWVISITOR",Boolean.valueOf(bIsVisitor));
				
				
				requestor = udEdgeAccess.gFIRST_NAME;
				loggedUser= udEdgeAccess.gIR_USERN;
				company   = udProject.getCompany();
				
				if(WForm!=null){
					
					WForm.setAction(action);
					WForm.setProj(projectID);
					WForm.setTc(tc);
					if(WForm.getWorkflowID()!=null){
						currStage = ScorecardDAO.getWorkflowCurrentStage(projectID,WForm.getWorkflowID());
						DetailsUtils dUtils = new DetailsUtils ();
						dUtils.setProjectID(WForm.getProj());
						dUtils.setWorkflowID(WForm.getWorkflowID());
						dUtils.extractWorkflowDetails();
						workflowName = dUtils.getWwf_name();
						/* Added in 7.1.1, for QBR */
						wf_type = dUtils.getWwf_type();
						if(wf_type.equalsIgnoreCase("SETMET")) wf_type_text = "Set/Met";
						if(wf_type.equalsIgnoreCase("QBR")) wf_type_text = "QBR";
						if(wf_type.equalsIgnoreCase("Self Assessment")) wf_type_text = "Self&nbsp;Assessment";
						request.setAttribute("WF_TYPE",wf_type);
						request.setAttribute("WF_TYPE_TEXT",wf_type_text);
					}
				}
				request.setAttribute("WORKFLOW_TITLE",workflowName);
				request.setAttribute("CURRENT_STAGE",currStage);
				//if(user == null){
				//   addError(request, WorkflowConstants.MSG_LOGIN_FAILED);
				
				//The if-else block immediately below this line is added by KP
				if(request.getParameter("extend")==null)
				{
					request.getSession().removeAttribute("errorMessages");
					request.getSession().removeAttribute("vo");
					System.out.println("Extend was null..Therefore, I removed errorMessages and vo");
				}
				else
				{
					try{
						WorkflowObject vo = (WorkflowObject)request.getSession().getAttribute("vo");
						
						if(WForm!=null && vo!=null && WForm.getWorkflowObject()!=null && WForm.getWorkflowObject().getClass() == vo.getClass())
						{
							WorkflowObject nVO = WForm.getWorkflowObject();
							if (false && nVO instanceof WorkflowEventDetailsVO) {
								WorkflowEventDetailsVO newVO = (WorkflowEventDetailsVO)WForm.getWorkflowObject();
								WorkflowEventDetailsVO oVO = (WorkflowEventDetailsVO)vo;
								PropertyUtils.copyProperties(newVO,oVO);
							}
							if (nVO instanceof EditIssueVO) {
								EditIssueVO newVO = (EditIssueVO)WForm.getWorkflowObject();
								EditIssueVO oVO = (EditIssueVO)vo;
								PropertyUtils.copyProperties(newVO,oVO);
							}
						}
					}catch(ClassCastException e)
					{
						System.out.println(e);
						System.out.println("This error hapenned because someone put an object that is not derived from WorkflowObject into session as \"vo\"");
					}catch(Exception e)
					{
						System.out.println(e);
					}
				}
				
				//Added by KP : 1 line
				if(WForm!=null)request.setAttribute("preloadBean",WForm.getPreloadBean());
				
				
			}
			else{
				boolean bAdmin = false;
				boolean bExecutive = false;
				boolean bEntitlement = false;
				boolean bOemSales = false;
				
				String edgeuserid = AccessCntrlFuncs.getEdgeUserId(udDAO.getConnection(),es.gIR_USERN);
				System.out.println("edgeuserid="+edgeuserid);
				Vector userents =  AccessCntrlFuncs.getUserEntitlements(udDAO.getConnection(),edgeuserid,true, true);
				System.out.println("after userents");
				for(int i = 0; i<userents.size(); i++)
					System.out.println(userents.get(i));
				
				if (!(userents.contains(Defines.COLLAB_CENTER_ADMIN_ENTITLEMENT) ||
						userents.contains(Defines.COLLAB_CENTER_EXECUTIVE_ENTITLEMENT) ||
						userents.contains(Defines.COLLAB_CENTER_NON_SALES_ENTITLEMENT) ||
						userents.contains(Defines.WORKFLOW_ADMIN) ||
						userents.contains(Defines.COLLAB_CENTER_SALES_ENTITLEMENT))) {
				ETSUnbrandedProperties udBranding =
					new ETSUnbrandedProperties();
				response.sendRedirect(udBranding.getUnauthorizedURL());
				if(udDAO!=null)udDAO.cleanup();
				return mapping.findForward("signOn");
				}
			}
			System.out.println("executeWorkflow");
			forward    = executeWorkflow(mapping, WForm, request, response);
			if (!(this instanceof WorkflowMatrixAction)) {
				
				
				//Added by KP : 2 lines
				Object preloadBean = request.getAttribute("preloadBean");
				if(WForm!=null)WForm.setPreloadBean(preloadBean);
				
				//Added by KP : IF block
				if(WForm!=null && WForm.getWorkflowObject()!=null && WForm.getWorkflowObject().getDB()!=null)
					try{WForm.getWorkflowObject().getDB().close();}catch(Exception e){System.out.println(e);}
					
					
					//    }
					
			}
			if(udDAO!=null)
				udDAO.cleanup();
		}catch(Exception e){             
			logger.error("Action Execution failed", e);
		}
		
		return (forward);
	}

    /**
  	 * @return
  	 */
  	protected boolean isVisitor(HttpServletRequest pdRequest) {
  		return ((Boolean) pdRequest.getSession().getAttribute("SESS_WORKFLOWVISITOR")).booleanValue();
  		
  	}

	/**
	 * @return
	 */
	protected boolean isSuperAdmin(HttpServletRequest pdRequest) {
		return ((Boolean) pdRequest.getSession().getAttribute(SESS_SUPERADMIN)).booleanValue();
		
	}
	/**
	 * @return
	 */
	protected boolean isWorkflowAdmin(HttpServletRequest pdRequest) {
		return ((Boolean) pdRequest.getSession().getAttribute("SESS_WORKFLOWADMIN")).booleanValue();
		
	}
	/**
	 * @return
	 */
	protected boolean isMemberOrVisitor(HttpServletRequest pdRequest) {
		return ((Boolean) pdRequest.getSession().getAttribute("SESS_VIEWWORKFLOW")).booleanValue();
	}
	/**
	 * @return
	 */
	protected boolean isExecutive(HttpServletRequest pdRequest) {
		return ((Boolean) pdRequest.getSession().getAttribute(SESS_EXECUTIVE)).booleanValue();
	}
	
	/**
	 * @return
	 */
	protected boolean isWspaceOwner(HttpServletRequest pdRequest) {
		return ((Boolean) pdRequest.getSession().getAttribute("SESS_WSOWNER")).booleanValue();
	}
	/**
	 * @return
	 */
	protected boolean isWspaceMgr(HttpServletRequest pdRequest) {
		return ((Boolean) pdRequest.getSession().getAttribute("SESS_WSMGR")).booleanValue();
	}
	
	protected boolean canAccess(HttpServletRequest pdRequest,String resource, String action){
		boolean canAccess = false;
		
		logger.debug(resource+"the resource being accessed @@@@@@@@@@@@@@@@@@@@@@@@");
		logger.debug(action+"The action being used is #########################33"); 
		logger.debug("the action beind done is &&&&&&&&&&&&&&"+action);
		logger.debug("the resource beind called is &&&&&&&&&&&&&&"+resource);
		
		
		
		if(WorkflowConstants.WORKFLOW_RESOURCE_SETMET.equalsIgnoreCase(resource)){
			logger.debug("Insicde setmet #########################################################");
			boolean checkAdmin = false;
			try{
				checkAdmin = UserUtils.doesUserHaveAIC_Workflow_AdminCollabEntitlement(udDAO.getConnection(),loggedUser);
				
			}catch(Exception adminChkException){
				logger.error("Error in canAccess while checking for workflow admin",adminChkException);
			}
			
			
			if(WorkflowConstants.WORKFLOW_SETMET_CREATE.equalsIgnoreCase(action)){
				logger.debug("Insicde setmet ######################################################### create");
				if((isWspaceOwner(pdRequest) || isWspaceMgr(pdRequest)) && !checkAdmin){
					logger.debug("Insicde setmet ######################################################### isWspaceOwner");
					canAccess = true;
				}
			}else{
				logger.debug("Insicde setmet else  #########################################################");
				if(isSuperAdmin(pdRequest) || isWspaceOwner(pdRequest) ||isWorkflowAdmin(pdRequest) || isWspaceMgr(pdRequest))
					canAccess = true;
			}
		}else{
			logger.debug("Insicde scorecard #########################################################");
			if(WorkflowConstants.WORKFLOW_RESOURCE_SCORECARD.equalsIgnoreCase(resource)){
				if(WorkflowConstants.WORKFLOW_SCORECARD_CREATE.equalsIgnoreCase(action) || WorkflowConstants.WORKFLOW_SCORECARD_EDIT.equalsIgnoreCase(action) || WorkflowConstants.WORKFLOW_SCORECARD_ADDQUESTION.equalsIgnoreCase(action)){
					if(isSuperAdmin(pdRequest) || isWspaceOwner(pdRequest) ||isWorkflowAdmin(pdRequest) || isWspaceMgr(pdRequest))
						canAccess = true;
				}
				if(WorkflowConstants.WORKFLOW_CREATE_BASETEMPLATE.equalsIgnoreCase(action) || WorkflowConstants.WORKFLOW_EDIT_BASTEMPLATE.equalsIgnoreCase(action)){
					if(isSuperAdmin(pdRequest) || isWorkflowAdmin(pdRequest))
						canAccess = true;
				}
			}else{
				if(WorkflowConstants.WORKFLOW_RESOURCE_ISSUES.equalsIgnoreCase(resource)){
					if(WorkflowConstants.WORKFLOW_ISSUES_CREATE.equalsIgnoreCase(action)){
						if("document".equalsIgnoreCase(currStage) && (isSuperAdmin(pdRequest) || isWspaceOwner(pdRequest) ||isWorkflowAdmin(pdRequest) || isWspaceMgr(pdRequest)))
							canAccess = true;
					}
				}
			}
			
			
		}
		
		//return false;
		return canAccess;
	}
	
	/**
	 * Adds the error to the errors collection and save it
	 * in the request
	 *
	 * @param  errorKey    String
	 * @param  request     HttpServletRequest
	 */
	protected void addError(HttpServletRequest request, String errorKey){
		if(errors == null) errors  = new ActionErrors();
		errors.add(ActionErrors.GLOBAL_ERROR,
				new ActionError(errorKey));
		saveErrors(request, errors);
	}
	
	/**
	 * Set the error to the errors collection and save it
	 * in the request
	 *
	 * @param  errorKey    String
	 * @param  request     HttpServletRequest
	 */
	protected void setError(HttpServletRequest request, String errorKey){
		errors  = new ActionErrors();
		errors.add(ActionErrors.GLOBAL_ERROR,
				new ActionError(errorKey));
		saveErrors(request, errors);
	}
	
	
	/**
	 * Adds the error to the errors collection and save it
	 * in the request
	 *
	 * @param  property    String
	 * @param  errorKey    String
	 * @param  request     HttpServletRequest
	 */
	protected void addError(HttpServletRequest request, String property,
			String errorKey){
		if(errors == null) errors  = new ActionErrors();
		errors.add(property, new ActionError(errorKey));
		saveErrors(request, errors);
	}
	
	/**
	 * Sets the error to the errors collection and save it
	 * in the request
	 *
	 * @param  property    String
	 * @param  errorKey    String
	 * @param  request     HttpServletRequest
	 */
	protected void setError(HttpServletRequest request, String property,
			String errorKey){
		errors  = new ActionErrors();
		errors.add(property, new ActionError(errorKey));
		saveErrors(request, errors);
	}
	
	
	/**
	 * All action classes will implement this method
	 */
	public abstract ActionForward executeWorkflow(ActionMapping mapping,
			WorkflowForm form,
			HttpServletRequest request,
			HttpServletResponse response)
	throws IOException, ServletException;
	
	/**
	 * @return oem.edge.ets.fe.documents.DocumentDAO
	 */
	protected DocumentDAO getDAO() throws Exception {
		
		DocumentDAO udDocumentDAO = new DocumentDAO();
		udDocumentDAO.prepare();
		
		return udDocumentDAO;
	}
	private Log logger  = WorkflowLogger.getLogger(WorkflowAction.class);
}