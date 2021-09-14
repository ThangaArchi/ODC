/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004                                          */
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

package oem.edge.ets.fe.acmgt.actions;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.AmtHfConstants;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.EntitledStatic;
import oem.edge.amt.Metrics;
import oem.edge.amt.UserObject;
import oem.edge.common.Global;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSAccessRequest;
import oem.edge.ets.fe.ETSBoardingUtils;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSErrorCodes;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSProcReqAccessFunctions;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcTeamUtils;
import oem.edge.ets.fe.acmgt.model.UserInviteStatusModel;
import oem.edge.ets.fe.acmgt.dao.AddMembrToWrkSpcDAO;
import oem.edge.ets.fe.acmgt.dao.InvMembrToWrkSpcDAO;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.CommonEmailHelper;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.ubp.ETSStatus;
import oem.edge.ets.fe.ubp.ETSUserAccessRequest;
import oem.edge.ets.fe.ubp.ETSUserDetails;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * @author Suresh
 *
 */
public class AddMultMembersConfirmAction extends BaseAcmgtAction {
	
	/**
	 * @see oem.edge.ets.fe.acmgt.actions.BaseAcmgtAction#executeAction(
	 * org.apache.struts.action.ActionMapping,
	 * org.apache.struts.action.ActionForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	
	private static Log logger = EtsLogger.getLogger(AddMultMembersConfirmAction.class);
	public static final String VERSION = "1.3";

	AddMembrToWrkSpcDAO addWrkSpcDao = new AddMembrToWrkSpcDAO();
		
	public AddMultMembersConfirmAction() {
		super();
	}
	
	protected ActionForward executeAction(
		ActionMapping pdMapping,
		ActionForm pdForm,
		HttpServletRequest pdRequest,
		HttpServletResponse pdResponse)
		throws Exception {

		
		String strForward = "success";
				
		ActionForward forward = new ActionForward();
		ActionErrors pdErrors = null;
		int noResults = 1;

		BaseAddMemberForm addMemberForm = (BaseAddMemberForm) pdForm; 
		EdgeAccessCntrl es = new EdgeAccessCntrl();
		Hashtable params = new Hashtable();  //for params
		Connection conn = null;
		PrintWriter out = pdResponse.getWriter();
				
		try {

			if (!Global.loaded) {
				Global.Init();
			}

			//get connection
			conn = ETSDBUtils.getConnection();
			
			if (es.GetProfile(pdResponse, pdRequest,conn)) {
								
				// get linkid, proj, tc 
				String sLink = AmtCommonUtils.getTrimStr(pdRequest.getParameter("linkid"));
				String projectidStr = AmtCommonUtils.getTrimStr(pdRequest.getParameter("proj"));
				String topCatId = AmtCommonUtils.getTrimStr(pdRequest.getParameter("tc"));
												
				ETSProj proj = ETSDatabaseManager.getProjectDetails(conn, projectidStr);

				//get params
				params = AmtCommonUtils.getServletParameters(pdRequest);
								
				Vector extReqWo = new Vector();
				Vector intAdedReqEntPen = new Vector();
				Vector extAdedReqEnt = new Vector();
				Vector extHasPendReq = new Vector();
				Vector extAdedReqEntPen = new Vector();
				Vector addedSucess = new Vector();
				Vector addError = new Vector();
				
				if((addMemberForm.getFinalInviteIds() != null) || (addMemberForm.getChgdInviteUserIds() != null)) {
					
					Vector finalInvIds = null;
					Vector chgdVerIds = null;
					String inviteResult = "";
					
				if((addMemberForm.getFinalInviteIds() != null)){
						if(!addMemberForm.getFinalInviteIds().isEmpty()){
								finalInvIds = addMemberForm.getFinalInviteIds();
						 }else{
								finalInvIds = new Vector();
						 }
				 }else{
					finalInvIds = new Vector();
				 }
					
					Vector invitedIds = new Vector();
					Vector inviteError = new Vector();
					
						if(addMemberForm.getChgdInviteUserIds() != null){
							if(!addMemberForm.getChgdInviteUserIds().isEmpty()){
								chgdVerIds = addMemberForm.getChgdInviteUserIds();
								for(int i=0; i<chgdVerIds.size(); i++){
									AddMembrUserDetails userDet = (AddMembrUserDetails) chgdVerIds.elementAt(i);
									finalInvIds.addElement(userDet);
								}
							}
						 }
					
						if(finalInvIds.size()>0){
					 	   for(int i=0; i<finalInvIds.size(); i++){
					 			AddMembrUserDetails userDet = (AddMembrUserDetails) finalInvIds.elementAt(i);
					 			userDet.setCountry(addWrkSpcDao.getCountryName(conn,userDet.getCountryCode()));
					 			int roleid = 0;
								String roleStr = userDet.getPrevilage();
								if(!StringUtil.isNullorEmpty(roleStr)){
									roleid = (new Integer(roleStr)).intValue();
								}
					 			userDet.setAccessLevel(ETSDatabaseManager.getRoleName(conn,roleid));
					 			inviteResult = processInviteMember(conn,es,pdRequest,proj,userDet,pdResponse,out);
                               	if(inviteResult.equals("INVITE_SUCCESS")){
					 				 logger.debug("INVITE status in confirm Action == " + inviteResult);
					 				 noResults = 0;
					 				invitedIds.addElement(userDet);
					 			}else if(inviteResult.equals("INVITE_ERROR")){
					 				 logger.debug("INVITE status in confirm Action == " + inviteResult);
					 				 noResults = 0;
					 				inviteError.addElement(userDet);
					 			}else{
					 				logger.debug("INVITE status in confirm Action == " + inviteResult);
					 				noResults = 0;
					 				inviteError.addElement(userDet);
					 			}
                          }
					 	   
					 addMemberForm.setInvitedUsers(invitedIds);
					 addMemberForm.setInviteError(inviteError);
				}
			}			 	
								
				if((addMemberForm.getFinalToBeConfrmdIds() != null) || (addMemberForm.getChgdIdsTobeConfmd() != null)){
						
						Vector finalToBeConfIds = null;						
						Vector chgdToBeConfIds = null;
						
						if((addMemberForm.getFinalToBeConfrmdIds() != null)) finalToBeConfIds = addMemberForm.getFinalToBeConfrmdIds();
						else finalToBeConfIds = new Vector();
												
							if(addMemberForm.getChgdIdsTobeConfmd() != null){
								if(!addMemberForm.getChgdIdsTobeConfmd().isEmpty()){
									chgdToBeConfIds = addMemberForm.getChgdIdsTobeConfmd();
									for(int i=0; i<chgdToBeConfIds.size(); i++){
										AddMembrUserDetails userDet = (AddMembrUserDetails) chgdToBeConfIds.elementAt(i);
										finalToBeConfIds.addElement(userDet);
									}
								}
							 }
				 					  
					addMemberForm.setFinalToBeConfrmdIds(finalToBeConfIds);
					
					if(addMemberForm.getFinalToBeConfrmdIds().size() > 0) noResults = 0;
					
				}
				
				//get the ExtUsersList for processing...
				if(addMemberForm.getWarnExtIds() != null) {
										
						Vector warnExtIds = addMemberForm.getWarnExtIds();
						Vector chkdExtIds;
						
						if (! addMemberForm.getChkdExtIds().isEmpty()){
							chkdExtIds = addMemberForm.getChkdExtIds();
							logger.debug("@@@@@@@@_SURESH_VOS_@@@@@@ -> getting addMemberForm.getChkdExtIds()");
		 				}else{
							chkdExtIds = new Vector();
						}
						
						  for (int j = 0; j < warnExtIds.size(); j++) {
						  	AddMembrUserDetails userDet = (AddMembrUserDetails) warnExtIds.elementAt(j);
						  	if(!StringUtil.isNullorEmpty(userDet.getSelectedUser())){
					 			if((userDet.getSelectedUser().trim()).equalsIgnoreCase("yes")){
						 			logger.debug("Confirm Action :: chkd warnExtId ==  " + userDet.getWebId().trim());	
					 			chkdExtIds.addElement(userDet);
					 		}
						  } 
						}
					  	  
						addMemberForm.setChkdExtIds(chkdExtIds);
						
						
						Vector extUserList = addMemberForm.getChkdExtIds();
						for (int i = 0; i < extUserList.size(); i++) {
					 		AddMembrUserDetails userDet = (AddMembrUserDetails) extUserList.elementAt(i);
					 		AddMembrUserDetails retndUserDet = processAddMember(conn,es,userDet,proj,pdResponse,out);
					 		String status = retndUserDet.getStatus();
					 		
					 		if(status.equals("EXT_REQ_WO")){
					 			noResults = 0;
					 			extReqWo.addElement(retndUserDet);
					 		}else if(status.equals("INT_ADDED_REQ_ENT_PEN")){
					 			noResults = 0;
					 			intAdedReqEntPen.addElement(retndUserDet);
					 		}else if(status.equals("EXT_ADDED_REQ_ENT")){
					 			noResults = 0;
					 			extAdedReqEnt.addElement(retndUserDet);
					 		}else if(status.equals("EXT_ADDED_REQ_ENT_PEN")){
					 			noResults = 0;
					 			extAdedReqEntPen.addElement(retndUserDet);
					 		}else if(status.equals("ADDED_SUCCESS")){
					 			noResults = 0;
					 			addedSucess.addElement(retndUserDet);
					 		}else if(status.equals("EXT_HAS_PEND_REQ")){
					 			noResults = 0;
					 			extHasPendReq.addElement(retndUserDet);
					 		}else if(status.equals("ADD_ERROR")){
					 			noResults = 0;
					 			addError.addElement(retndUserDet);
					 		}else{
					 			noResults = 0;
					 			addError.addElement(retndUserDet);
					 		}
					 		
					  }
										
				}else if(addMemberForm.getChkdExtIds() != null){
					    Vector chkdExtIds = addMemberForm.getChkdExtIds();
					  	for (int i = 0; i < chkdExtIds.size(); i++) {
					 			AddMembrUserDetails userDet = (AddMembrUserDetails) chkdExtIds.elementAt(i);
					 			AddMembrUserDetails retndUserDet = processAddMember(conn,es,userDet,proj,pdResponse,out);
					 			String status = retndUserDet.getStatus();
					 		
					 		if(status.equals("EXT_REQ_WO")){
					 			noResults = 0;
					 			extReqWo.addElement(retndUserDet);
					 		}else if(status.equals("INT_ADDED_REQ_ENT_PEN")){
					 			noResults = 0;
					 			intAdedReqEntPen.addElement(retndUserDet);
					 		}else if(status.equals("EXT_ADDED_REQ_ENT")){
					 			noResults = 0;
					 			extAdedReqEnt.addElement(retndUserDet);
					 		}else if(status.equals("EXT_ADDED_REQ_ENT_PEN")){
					 			noResults = 0;
					 			extAdedReqEntPen.addElement(retndUserDet);
					 		}else if(status.equals("ADDED_SUCCESS")){
					 			noResults = 0;
					 			addedSucess.addElement(retndUserDet);
					 		}else if(status.equals("EXT_HAS_PEND_REQ")){
					 			noResults = 0;
					 			extHasPendReq.addElement(retndUserDet);
					 		}else if(status.equals("ADD_ERROR")){
					 			noResults = 0;
					 			addError.addElement(retndUserDet);
					 		}else{
					 			noResults = 0;
					 			addError.addElement(retndUserDet);
					 		}
						}
					 }
				
				// get the IncompExtUsersList for processing...
				if(addMemberForm.getIncompWarnExtIds() != null) {
						
						String incompMailResult = "";
						Vector incompMailSentIds = new Vector();
						Vector incompMailErrorIds = new Vector();
						Vector incompWarnExtIds = addMemberForm.getIncompWarnExtIds();
						Vector chkdIncompExtIds;
						
						if (! addMemberForm.getChkdIncompExtIds().isEmpty()){
							chkdIncompExtIds = addMemberForm.getChkdIncompExtIds();
							logger.debug("@@@@@@@@_SURESH_VOS_@@@@@@ -> getting addMemberForm.getChkdIncompExtIds()");
		 				}else{
		 					chkdIncompExtIds = new Vector();
						}
						
						  for (int j = 0; j < incompWarnExtIds.size(); j++) {
						  	AddMembrUserDetails userDet = (AddMembrUserDetails) incompWarnExtIds.elementAt(j);
						  	if(!StringUtil.isNullorEmpty(userDet.getSelectedUser())){
					 		if((userDet.getSelectedUser().trim()).equalsIgnoreCase("yes")){
						 		logger.debug("Confirm Action :: chkd incompWarnExtIds ==  " + userDet.getWebId().trim());
					 			chkdIncompExtIds.addElement(userDet);
					 		}	
						  } 
						}
						addMemberForm.setChkdIncompExtIds(chkdIncompExtIds);
						
						Vector incompExtUserList = addMemberForm.getChkdIncompExtIds();
						for (int i = 0; i < incompExtUserList.size(); i++) {
					 		AddMembrUserDetails userDet = (AddMembrUserDetails) incompExtUserList.elementAt(i);
					 		userDet.setCountry(addWrkSpcDao.getCountryName(conn,userDet.getCountryCode()));
				 			int roleid = 0;
							String roleStr = userDet.getPrevilage();
							if(!StringUtil.isNullorEmpty(roleStr)){
								roleid = (new Integer(roleStr)).intValue();
							}
				 			userDet.setAccessLevel(ETSDatabaseManager.getRoleName(conn,roleid));
					 		incompMailResult = sendMailToUserofIncompProf(conn,es,pdRequest,pdResponse,proj,userDet,out);
					 		if(incompMailResult.equals("INCOMP_MAIL_SENT")){
				 				 logger.debug("INCOMP_MAIL status in confirm Action == " + incompMailResult);
				 				 noResults = 0;
				 				incompMailSentIds.addElement(userDet);
				 			}else if(incompMailResult.equals("INCOMP_MAIL_ERROR")){
				 				 logger.debug("INCOMP_MAIL status in confirm Action == " + incompMailResult);
				 				 noResults = 0;
				 				incompMailErrorIds.addElement(userDet);
				 			}else{
				 				logger.debug("INCOMP_MAIL status in confirm Action == " + incompMailResult);
				 				noResults = 0;
				 				incompMailErrorIds.addElement(userDet);
				 			}
				 	    }
						 addMemberForm.setIncompMailSentIds(incompMailSentIds);
						 addMemberForm.setIncompMailErrorIds(incompMailErrorIds);	
					  			     
			 
				}else if(addMemberForm.getChkdIncompExtIds() != null){
					
					String incompMailResult = "";
					Vector incompMailSentIds = new Vector();
					Vector incompMailErrorIds = new Vector();
					Vector incompExtUserList = addMemberForm.getChkdIncompExtIds();
					
					for (int i = 0; i < incompExtUserList.size(); i++) {
				 		AddMembrUserDetails userDet = (AddMembrUserDetails) incompExtUserList.elementAt(i);
				 		userDet.setCountry(addWrkSpcDao.getCountryName(conn,userDet.getCountryCode()));
			 			int roleid = 0;
						String roleStr = userDet.getPrevilage();
						if(!StringUtil.isNullorEmpty(roleStr)){
							roleid = (new Integer(roleStr)).intValue();
						}
			 			userDet.setAccessLevel(ETSDatabaseManager.getRoleName(conn,roleid));
				 		incompMailResult = sendMailToUserofIncompProf(conn,es,pdRequest,pdResponse,proj,userDet,out);
				 		if(incompMailResult.equals("INCOMP_MAIL_SENT")){
			 				 logger.debug("INCOMP_MAIL status in confirm Action == " + incompMailResult);
			 				 noResults = 0;
			 				incompMailSentIds.addElement(userDet);
			 			}else if(incompMailResult.equals("INCOMP_MAIL_ERROR")){
			 				 logger.debug("INCOMP_MAIL status in confirm Action == " + incompMailResult);
			 				 noResults = 0;
			 				incompMailErrorIds.addElement(userDet);
			 			}else{
			 				logger.debug("INCOMP_MAIL status in confirm Action == " + incompMailResult);
			 				noResults = 0;
			 				incompMailErrorIds.addElement(userDet);
			 			}
			 	    }
					
					 addMemberForm.setIncompMailSentIds(incompMailSentIds);
					 addMemberForm.setIncompMailErrorIds(incompMailErrorIds);	
				 }
				
				// get the IBMUsersList for processing...
				if(addMemberForm.getIntIdsTobeAdded() != null) {
					if(addMemberForm.getIntIdsTobeAdded().size() > 0) {
					
					Vector intToBeAdded = addMemberForm.getIntIdsTobeAdded();
										
						for (int j = 0; j < intToBeAdded.size(); j++) {
						AddMembrUserDetails userDet = (AddMembrUserDetails) intToBeAdded.elementAt(j);
						if(!StringUtil.isNullorEmpty(userDet.getSelectedUser())){
				 			if((userDet.getSelectedUser().trim()).equalsIgnoreCase("yes")){
					 			logger.debug("Confirm Action :: chkd Internal IDs ==  " + userDet.getWebId().trim());
				 		AddMembrUserDetails retndUserDet = processAddMember(conn,es,userDet,proj,pdResponse,out);
				 		String status = retndUserDet.getStatus();
				 		
				 		if(status.equals("EXT_REQ_WO")){
				 			noResults = 0;
				 			extReqWo.addElement(retndUserDet);
				 		}else if(status.equals("INT_ADDED_REQ_ENT_PEN")){
				 			noResults = 0;
				 			intAdedReqEntPen.addElement(retndUserDet);
				 		}else if(status.equals("EXT_ADDED_REQ_ENT")){
				 			noResults = 0;
				 			extAdedReqEnt.addElement(retndUserDet);
				 		}else if(status.equals("EXT_ADDED_REQ_ENT_PEN")){
				 			noResults = 0;
				 			extAdedReqEntPen.addElement(retndUserDet);
				 		}else if(status.equals("ADDED_SUCCESS")){
				 			noResults = 0;
				 			addedSucess.addElement(retndUserDet);
				 		}else if(status.equals("EXT_HAS_PEND_REQ")){
				 			noResults = 0;
				 			extHasPendReq.addElement(retndUserDet);
				 		}else if(status.equals("ADD_ERROR")){
				 			noResults = 0;
				 			addError.addElement(retndUserDet);
				 		}else{
				 			noResults = 0;
				 			addError.addElement(retndUserDet);
				 		}
					}
				  }
				 }		
				}
			}
				
				addMemberForm.setAddedSuccess(addedSucess);
				addMemberForm.setExtAdedReqEnt(extAdedReqEnt);
				addMemberForm.setIntAdedReqEntPen(intAdedReqEntPen);
				addMemberForm.setExtAdedReqEntPen(extAdedReqEntPen);
				addMemberForm.setExtReqWo(extReqWo);
				addMemberForm.setAddError(addError);
				addMemberForm.setExtHasPendReq(extHasPendReq);
				
				logger.debug("noResults in confirm Action == " + noResults);
				addMemberForm.setNoResults(noResults);
				
		  	 }
		
		} catch (SQLException se) {
		 			 	
			if (conn != null) {
				conn.close();
			}
			
			if (se != null) {
				se.printStackTrace();
				if (logger.isErrorEnabled()) {
	            	logger.error(this,se);
	            }
				ETSUtils.displayError(out, ETSErrorCodes.getErrorCode(se), "Error occurred on AddMultMembersConfirmAction.");

			}
			
		} catch (Exception ex) {
						
			if (ex != null) {
				ex.printStackTrace();
				if (logger.isErrorEnabled()) {
	            	logger.error(this,ex);
	            }
				ETSUtils.displayError(out, ETSErrorCodes.getErrorCode(ex), "Error occurred on AddMultMembersConfirmAction.");

			}
			
		} finally {

			if (conn != null)
				conn.close();
			conn = null;
		}

		return pdMapping.findForward(strForward);
	}

	
	
	private AddMembrUserDetails processAddMember(Connection conn,EdgeAccessCntrl es,AddMembrUserDetails userDet,ETSProj proj,HttpServletResponse pdResponse, PrintWriter out)throws SQLException, Exception{
				
		AddMembrUserDetails retnUserDet = userDet;
		
		try{
						
		UnbrandedProperties unBrandedprop = PropertyFactory.getProperty(proj.getProjectType());
		
		String sIRId = es.gIR_USERN;
		String sProjId = proj.getProjectId();
				
		String reqstdEntitlement = WrkSpcTeamUtils.getWrkSpcReqEntitlement(proj);
		logger.debug("reqstdEntitlement entitlement in ADD MEMBER=== " + reqstdEntitlement);

		String reqstdProject = WrkSpcTeamUtils.getWrkSpcReqProject(proj);
		logger.debug("reqstdProject in ADD MEMBER ==" + reqstdProject);
		
		ETSUserDetails details = (ETSUserDetails) addWrkSpcDao.getUserDetails(conn,userDet.getWebId());
		
		String sUserId = details.getWebId();
		String sAssignCompany = userDet.getCompany();
		String sAssignCty = userDet.getCountryCode();
		
		boolean bEntitled = addWrkSpcDao.userHasEntitlement(sUserId, reqstdEntitlement);
		logger.debug("user :  "+ sUserId +"  in process ADD MEMBER is entitled ? :  " + bEntitled);
		ETSProcReqAccessFunctions procFuncs = new ETSProcReqAccessFunctions();
		boolean bHasPendEtitlement = procFuncs.userHasPendEntitlement(conn, details.getEdgeId(), reqstdProject);
		boolean bStatus = false;
		boolean bSuccess = false;
		boolean bReqEnt = false;
		boolean bCreateRequest = false;
		boolean bHasPendReq = false;
		
		retnUserDet.setUserName(details.getFirstName()+" "+details.getLastName());
		retnUserDet.setWebId(details.getWebId());
		retnUserDet.setEmailId(details.getEMail());
						
		if (details.getUserType() == details.USER_TYPE_EXTERNAL) {
		
		// if the person logged in is a workspace manager and the person
		// being added is an external user without entitlement, then
		// just create a request to workspace owner...

		// check if the user is external or not...
		
			// the user is external and does not have the entitlement..
			// just create it as a request to the workspace owner...
			// find out if the logged in person is a manager or an owner...

			boolean workspaceowner = ETSDatabaseManager.hasProjectPriv(es.gIR_USERN, proj.getProjectId(), Defines.OWNER, conn);
				
			if (workspaceowner) {

				// user not entitled, request project for him..
				boolean mailSent = false;
				if (!bEntitled && !bHasPendEtitlement) {
					if (proj.getProjectType().equals(Defines.AIC_WORKSPACE_TYPE)) {
						bStatus = AddMembrToWrkSpcDAO.setDefaultAICDecafEntitlementExtUsers(conn,details.getEdgeId());
						logger.debug("Status of Create default Entitlement for AIC user ++ " + bStatus);
						if ( bStatus ) {
							bEntitled = true;
							//send inforrmational email to the POC owner for ext user
							if (!details.getPocEmail().equals(es.gEMAIL) 
								&& (details.getPocEmail() !=null) 
								&& !details.getPocEmail().equals("")) {
								mailSent = addWrkSpcDao.sendPOCInformationEmail(conn,es,proj.getProjectId(),proj.getName(), details.getWebId());
							}
						}
					} else {

						ETSUserAccessRequest reqAccess = new ETSUserAccessRequest();
						reqAccess.setTmIrId(details.getWebId());
						reqAccess.setPmIrId(es.gIR_USERN);
						reqAccess.setIsAProject(WrkSpcTeamUtils.isReqAccessProject(proj.getProjectType()));
						if (! sAssignCompany.trim().equals("")) {
							reqAccess.setUserCompany(sAssignCompany);
						} else {
							reqAccess.setUserCompany(sAssignCompany);
						}
						if (! sAssignCty.trim().equals("")) { // set the country
							EntitledStatic.fireUpdate(conn, "update decaf.users set country_code = '" + sAssignCty + "'  where userid = '" + details.getEdgeId() + "'");
						}

						reqAccess.setDecafEntitlement(reqstdProject);
						ETSStatus status = reqAccess.request(conn);

						if (status.getErrCode() == 0) {
							bStatus = true;
							bReqEnt = true;
						} else {
							bStatus = false;
						}
					}
				} else {
					// since the user has an entitlement, just add the member
					// to the workspace 
					bStatus = true;
				}

				logger.debug("bStatus flag value in add member Internal==" + bStatus);
				logger.debug("bReqEnt flag value in add member Internal==" + bReqEnt);

			} else {
				// the person logged in is a workspace manager.. so create a request..

				ETSUserAccessRequest uar = new ETSUserAccessRequest();

				Vector owners = ETSDatabaseManager.getUsersByProjectPriv(proj.getProjectId(), Defines.OWNER, conn);

				if (owners.size() > 0) {

					ETSUser owner = (ETSUser) owners.elementAt(0); // take the first

					UserObject uo = AccessCntrlFuncs.getUserObject(conn, owner.getUserId(), true, false);
					String owner_email = uo.gEMAIL;

					ETSUserDetails u = new ETSUserDetails();
					u.setWebId(details.getWebId());
					u.extractUserDetails(conn);
					boolean sent = false;
					
					if (! AddMembrToWrkSpcDAO.hasPendingRequest(conn,sUserId,sProjId)) {
						uar.recordRequest(conn, details.getWebId(), proj.getName(), owner_email, es.gIR_USERN, proj.getProjectType());
						Metrics.appLog(conn, es.gIR_USERN, WrkSpcTeamUtils.getMetricsLogMsg(proj, "Team_Add_Request"));
						sent = ETSBoardingUtils.sendManagerEMail(u, owner_email, proj, es.gEMAIL);
					}else{
						bHasPendReq = true;
					}
					bCreateRequest = true;
				}
			}
		} else {
			// internal user
			// user not entitled, request project for him..
			if (!bEntitled && !bHasPendEtitlement) {
				ETSUserAccessRequest reqAccess = new ETSUserAccessRequest();
				reqAccess.setTmIrId(sUserId);
				reqAccess.setPmIrId(sIRId);
				reqAccess.setIsAProject(WrkSpcTeamUtils.isReqAccessProject(proj.getProjectType()));
				reqAccess.setUserCompany(details.getCompany());
				reqAccess.setDecafEntitlement(reqstdProject);
				ETSStatus status = reqAccess.request(conn);

				if (status.getErrCode() == 0) {
					bStatus = true;
					bReqEnt = true;
				} else {
					bStatus = false;
				}
			} else {
				// since the user has an entitlement, just add the member
				// to the workspace CSR: IBMCC00007177
				bStatus = true;
			}
		}

		logger.debug("bStatus flag value in add member Internal==" + bStatus);
		logger.debug("bReqEnt flag value in add member Internal==" + bReqEnt);

		if (bStatus && !bCreateRequest) {
            
			String job = "";
			
			if(!StringUtil.isNullorEmpty(userDet.getJob())){
				job = userDet.getJob().trim();
			}
			
			logger.debug("This user Job is   == " +job);
			
			int roleid = 0;
			String roleStr = userDet.getPrevilage();
			if(!StringUtil.isNullorEmpty(roleStr)){
				roleid = (new Integer(roleStr)).intValue();
			}
			userDet.setAccessLevel(ETSDatabaseManager.getRoleName(conn,roleid));
			logger.debug("@@@@@@@@_SURESH_VOS_@@@@@@ -> roleName =  "+roleStr+"&&&&&&  roleId =  "+roleid);
			logger.debug("@@@@@@@@_SURESH_VOS_@@@@@@ -> roleName =  "+roleStr+"&&&&&&  RoleName =  "+userDet.getAccessLevel());
			
			ETSUser new_user = new ETSUser();
			new_user.setUserId(sUserId);
			new_user.setRoleId(roleid);
			new_user.setProjectId(sProjId);
			new_user.setPrimaryContact(Defines.NO);
			new_user.setLastUserId(sIRId);
			new_user.setUserJob(job);

			String[] res = AddMembrToWrkSpcDAO.addProjectMember(new_user, conn);
			if (details.getUserType() == details.USER_TYPE_EXTERNAL) {
				if (!sAssignCompany.trim().equals("")) {
					EntitledStatic.fireUpdate(conn, "update decaf.users set assoc_company = '" + sAssignCompany + "'  where userid = '" + details.getEdgeId() + "'");
				} else {
					EntitledStatic.fireUpdate(conn, "update decaf.users set assoc_company = '" + details.getCompany() + "'  where userid = '" + details.getEdgeId() + "'");
				}
				if (!sAssignCty.trim().equals("")) { // set the country
					EntitledStatic.fireUpdate(conn, "update decaf.users set country_code = '" + sAssignCty + "'  where userid = '" + details.getEdgeId() + "'");
				} else {
					EntitledStatic.fireUpdate(conn, "update decaf.users set country_code = '" + details.getCountryCode() + "'  where userid = '" + details.getEdgeId() + "'");
				}
			}

			String success = res[0];

			logger.debug("success flag value in add member==" + success);

			if (success.equals("0")) {

				if(bEntitled) {

					EntitledStatic.fireUpdate(conn, "update ets.ets_users set active_flag = 'A'  where user_id = '" + sUserId + "' and user_project_id = '" + sProjId + "'");

				}

				else {  //not entitled

				if (bReqEnt) { // entitlement requested - so the active flag is "P"
					EntitledStatic.fireUpdate(conn, "update ets.ets_users set active_flag = 'P'  where user_id = '" + sUserId + "' and user_project_id = '" + sProjId + "'");
				} else { // "A"
					if (bHasPendEtitlement) {
						EntitledStatic.fireUpdate(conn, "update ets.ets_users set active_flag = 'P'  where user_id = '" + sUserId + "' and user_project_id = '" + sProjId + "'");
					}
				}

				}
				
                if(! StringUtil.isNullorEmpty(userDet.getEnteredId())){
                	
                	String enteredId = userDet.getEnteredId().trim();
                	String webId = userDet.getWebId();
                	boolean updateNotesId = false;
                	if(enteredId.indexOf("/IBM") > 0){
                		updateNotesId = ETSDatabaseManager.updateNotesMailID(webId,enteredId);
                		if(updateNotesId) logger.debug("This notesId is updated  == " +enteredId);
                	}
                	
                }
                
                if(! StringUtil.isNullorEmpty(userDet.getMsgrID())){
                	
                	String msgrId = userDet.getMsgrID().trim();
                	String webId = userDet.getWebId();
                	boolean updateMsgrId = false;
                	updateMsgrId = ETSDatabaseManager.updateUserMessengerID(webId,msgrId);
                		if(updateMsgrId) logger.debug("This msgrId is updated  == " +msgrId);
                	                	
                }
                
                if(! StringUtil.isNullorEmpty(userDet.getCmpDifRsn())){
                	int updt = 0;
                	String rsn = userDet.getCmpDifRsn().trim();
                	String webId = userDet.getWebId();
                	updt = AddMembrToWrkSpcDAO.insertReasonToAdminLog(webId,sProjId,rsn,conn);
                	if(updt == 1) logger.debug("This Reason to ets_admin_log is updated  == " +rsn);
                }
                
				Metrics.appLog(conn, sIRId, WrkSpcTeamUtils.getMetricsLogMsg(proj, "Team_Add"));

				bSuccess = true;
			} else {
				bSuccess = false;
			}

		}

		
		if (bCreateRequest) {
			if(bHasPendReq == true){
				logger.debug("There is already a pending request for this ID.");
				retnUserDet.setStatus("EXT_HAS_PEND_REQ");	
			}else{
				logger.debug("A request has been forwarded to the workspace owner because this user is new and requires additional processing.");
				retnUserDet.setStatus("EXT_REQ_WO");
			}
		} else {
			ETSAccessRequest ar = new ETSAccessRequest();
			ar.setProjName(proj.getName());
			ar.setMgrEMail(es.gEMAIL);

			if (bSuccess) {
				if (bReqEnt) {
					ETSBoardingUtils.sendAcceptEMailPendEnt(details, ar, ar.getProjName(), "", "", es.gEMAIL);
					if (details.getUserType() == details.USER_TYPE_INTERNAL) {
						logger.debug("The user has been added to the workspace successfully. The corresponding entitlement to access has been requested for the user and is pending with their manager.");
						retnUserDet.setStatus("INT_ADDED_REQ_ENT_PEN");
					} else {
						ETSUserDetails u = new ETSUserDetails();
						u.setWebId(sUserId);
						u.extractUserDetails(conn);

						if (u.getPocEmail().equals(es.gEMAIL)) {
							logger.debug("The user has been added to the workspace successfully. The corresponding entitlement to access has been requested for the user.");
							retnUserDet.setStatus("EXT_ADDED_REQ_ENT");
						} else {
							logger.debug("The user has been added to the workspace successfully. The corresponding entitlement to access has been requested for the user and is pending their IBM contact.");
							retnUserDet.setStatus("EXT_ADDED_REQ_ENT_PEN");
						}
					}
				} else {
					ETSBoardingUtils.sendAcceptEMail(details, ar, ar.getProjName(), "", "", es.gEMAIL);
					logger.debug("The user has been added to the workspace successfully.");
					retnUserDet.setStatus("ADDED_SUCCESS");
				}
			} else {
				logger.debug("An error occured when adding the user to workspace. Please try again later.");
				retnUserDet.setStatus("ADD_ERROR");
			}
		 }
		} catch (SQLException se) {
		 			 		
			if (se != null) {
				se.printStackTrace();
				if (logger.isErrorEnabled()) {
	            	logger.error(this,se);
	            }
				ETSUtils.displayError(out, ETSErrorCodes.getErrorCode(se), "Error occurred on AddMultMembersConfirmAction.");

			}
			
		} catch (Exception ex) {
						
			if (ex != null) {
				ex.printStackTrace();
				if (logger.isErrorEnabled()) {
	            	logger.error(this,ex);
	            }
				ETSUtils.displayError(out, ETSErrorCodes.getErrorCode(ex), "Error occurred on AddMultMembersConfirmAction.");

			}
			
		} 
		
		return retnUserDet;
	}
	
	private String processInviteMember(Connection conn, EdgeAccessCntrl es,HttpServletRequest request,ETSProj proj,AddMembrUserDetails userDet, HttpServletResponse pdResponse, PrintWriter out)throws SQLException,Exception{
			
			String inviteResult = ""; 
		
			try{
			
			InvMembrToWrkSpcDAO invWrkSpcDao = new InvMembrToWrkSpcDAO();
			StringBuffer sBuff = new StringBuffer();
			
			UnbrandedProperties unBrandedprop = PropertyFactory.getProperty(proj.getProjectType());
			String strCustConnect = "";
		    if ("Collaboration Center".equalsIgnoreCase(unBrandedprop.getAppName())) {
		        strCustConnect = "Customer Connect ";
		    }
		    
			boolean invited = false;
						
			String reqUserWebId = es.gIR_USERN;
			String from = es.gEMAIL;
			logger.debug("@@@@@@@@_SURESH_VOS_@@@@@@ -> FROM =  "+es.gEMAIL);
			String invStat = "I";
			int roleid = 1;
			
			String roleStr = userDet.getPrevilage();
			if(!StringUtil.isNullorEmpty(roleStr)){
			roleid = (new Integer(roleStr)).intValue();
			}
			logger.debug("@@@@@@@@_SURESH_VOS_@@@@@@ -> roleName =  "+roleStr+"&&&&&&  roleId =  "+roleid);
			
			UserInviteStatusModel invStatModel = new UserInviteStatusModel();
			invStatModel.setUserId(userDet.getEmailId());
			invStatModel.setWrkSpcId(proj.getProjectId());
			invStatModel.setInviteStatus(invStat);
			invStatModel.setRoleId(roleid);
			invStatModel.setRequestorId(reqUserWebId);
			invStatModel.setUserCompany(userDet.getCompany());
			invStatModel.setUserCountryCode(userDet.getCountryCode());
			invStatModel.setLastUserId(from);
			
			userDet.setCountry(addWrkSpcDao.getCountryName(conn,userDet.getCountryCode()));
			
			
				
			String woemail="";
			Vector owners = ETSDatabaseManager.getUsersByProjectPriv(proj.getProjectId(), Defines.OWNER, conn);
			if (owners.size() > 0) {

				ETSUser owner = (ETSUser) owners.elementAt(0); // take the first

				UserObject uo = AccessCntrlFuncs.getUserObject(conn, owner.getUserId(), true, false);
				woemail = uo.gEMAIL;
			}
						
			String to = userDet.getEmailId();
			String ccEmail = "";
			String comments = "";
			String subject = "Invitation to join a workspace for '" + proj.getName() + "' on IBM " + unBrandedprop.getAppName() + " web portal.";
			
			String loginURL = Global.WebRoot + "/" + "index.jsp";
			ResourceBundle resBundle = ResourceBundle.getBundle("oem.edge.common.gwa");
			String selfRegistrationURL = resBundle.getString("selfRegistrationURL");
			selfRegistrationURL = selfRegistrationURL.substring(0, selfRegistrationURL.indexOf("&okurl"));
						
			//send mail start

			sBuff.append("Hello,").append("\n");
			sBuff.append("I would like you to join a workspace for '" + proj.getName() + "' in the " + unBrandedprop.getAppName() + " on the IBM Customer Connect web portal. We will use this workspace to collaborate and share information securely.").append("\n");

			sBuff.append("\n");
			sBuff.append("Please follow these two steps:").append("\n");
			sBuff.append("\n");
			sBuff.append("---------------------------------------------------------------------------------------").append("\n");
			sBuff.append("STEP #1: Create an IBM ID and password").append("\n");
			sBuff.append("---------------------------------------------------------------------------------------").append("\n");
			sBuff.append("\n");
			sBuff.append("Click the following URL to register your ID of <" + userDet.getEmailId() + ">. Make sure you provide your complete company \"address\" as it will be required for access to the workspace.").append("\n");
			sBuff.append("\n");
			sBuff.append("" + selfRegistrationURL + "").append("\n");
			sBuff.append("\n");
			sBuff.append("---------------------------------------------------------------------------------------").append("\n");
			sBuff.append("STEP #2: Log in to initiate access to the workspace").append("\n");
			sBuff.append("---------------------------------------------------------------------------------------").append("\n");
			sBuff.append("\n");
			sBuff.append("Click on the link below and login with your newly created IBM ID and password. You do not need to do anything further. The system will initiate the final processing based on your  login. Your access will be complete once the workspace owner and/or your IBM point-of-contact verifies your information and approves.").append("\n");
			sBuff.append("\n");
			sBuff.append("" + loginURL + "").append("\n");
			sBuff.append("\n");
			sBuff.append("If you experience any difficulty, you can contact me or our 24x7 help desk. Help desk information is at the bottom of this e-mail.").append("\n");
			sBuff.append("\n");
			sBuff.append("\n");
			sBuff.append("Registration information").append("\n");
			sBuff.append("IBM ID:                               " + userDet.getEmailId()).append("\n");
			sBuff.append("Project or proposal:                  " + proj.getName()).append("\n");
			sBuff.append("Country:                              " + userDet.getCountry()).append("\n");
			sBuff.append("Company:                              " + userDet.getCompany()).append("\n");
			sBuff.append("Privilege:                            " + userDet.getAccessLevel()).append("\n");
			sBuff.append("E-mail address of IBM contact:        " + woemail).append("\n");
			sBuff.append("\n");
			if (!comments.equals("")) {
				sBuff.append("Additions comments if any from the the invitee").append("\n");
				sBuff.append(comments).append("\n");
			}
			
			sBuff.append(CommonEmailHelper.getEmailFooter(unBrandedprop.getAppName()));
			
			Global.println("mail messg==" + sBuff.toString());
			Global.Init();

			logger.debug("userId in INVITE == " + invStatModel.getUserId());
			logger.debug("wrkspcId in INVITE == " + invStatModel.getWrkSpcId());
			logger.debug("Invstatus in INVITE == " + invStatModel.getInviteStatus());
			logger.debug("roleId in INVITE == " + invStatModel.getRoleId());
			logger.debug("reqstrId in INVITE == " + invStatModel.getRequestorId());
			logger.debug("userCompnyId in INVITE == " + invStatModel.getUserCompany());
			logger.debug("userCntryCodeId in INVITE == " + invStatModel.getUserCountryCode());
			logger.debug("LastUserId in INVITE == " + invStatModel.getLastUserId());
			
			logger.debug("FROM Addr in INVITE == " + from);
			logger.debug("TO Addr in INVITE == " + to);
			logger.debug("CC Addr in INVITE == " + ccEmail);
			logger.debug("mailHost Addr in INVITE == " + Global.mailHost);
			logger.debug("Subject Addr in INVITE == " + subject);
			
			ETSUtils.sendEMail(from, to, ccEmail, Global.mailHost, sBuff.toString(), subject, from);
							
			invited = invWrkSpcDao.addMemberToInviteStatus(invStatModel);
			logger.debug("INVITE result == " + invited);
						
			if(invited){
				logger.debug("INVITE status == " + inviteResult);
				inviteResult = "INVITE_SUCCESS";
			}else{
				inviteResult = "INVITE_ERROR";
				logger.debug("INVITE status == " + inviteResult);
			}
	
		} catch (SQLException se) {
			 				 		
				if (se != null) {
					se.printStackTrace();
					if (logger.isErrorEnabled()) {
		            	logger.error(this,se);
		            }
					ETSUtils.displayError(out, ETSErrorCodes.getErrorCode(se), "Error occurred on AddMultMembersConfirmAction.");

				}
				
		} catch (Exception ex) {
								
				if (ex != null) {
					ex.printStackTrace();
					if (logger.isErrorEnabled()) {
		            	logger.error(this,ex);
		            }
					ETSUtils.displayError(out, ETSErrorCodes.getErrorCode(ex), "Error occurred on AddMultMembersConfirmAction.");

				}
				
			} 

	
		return inviteResult;
	}

	
	private String sendMailToUserofIncompProf(Connection conn, EdgeAccessCntrl es, HttpServletRequest request, HttpServletResponse pdResponse, ETSProj proj, AddMembrUserDetails userDet, PrintWriter out)throws SQLException, Exception{
		
		String incompMailResult = ""; 
		
		try{
	    StringBuffer sBuff = new StringBuffer();
		UnbrandedProperties unBrandedprop = PropertyFactory.getProperty(proj.getProjectType());
		boolean sentMail = false;
		
		String strCustConnect = "";
	    if ("Collaboration Center".equalsIgnoreCase(unBrandedprop.getAppName())) {
	        strCustConnect = "Customer Connect ";
	    }
					
		String senderWebId = es.gIR_USERN;
		String from = es.gEMAIL;
								
					
		String woemail="";
		Vector owners = ETSDatabaseManager.getUsersByProjectPriv(proj.getProjectId(), Defines.OWNER, conn);
		if (owners.size() > 0) {

			ETSUser owner = (ETSUser) owners.elementAt(0); // take the first

			UserObject uo = AccessCntrlFuncs.getUserObject(conn, owner.getUserId(), true, false);
			woemail = uo.gEMAIL;
		}	
		
		String to = userDet.getEmailId();
		String ccEmail = "";
		String comments = "";
		String subject = "Please edit your IBM " + unBrandedprop.getAppName() + " profile to provide your complete address.";
		
		String loginURL = Global.WebRoot + "/" + "index.jsp";
				
		//send mail start

		sBuff.append("This is to inform you that your " + unBrandedprop.getAppName() + " profile on IBM Customer Connect has an incomplete address.").append("\n");
		sBuff.append("---------------------------------------------------------------------------------------").append("\n");
		sBuff.append("\n");
		sBuff.append("You can update your profile by clicking on the link below and logging in with your IBM ID and password. Make sure you provide your \"Address\" completely as it is required for access to the workspace.").append("\n");
		sBuff.append("\n");
		sBuff.append("" + loginURL + "").append("\n");
		sBuff.append("\n");
		sBuff.append("---------------------------------------------------------------------------------------").append("\n");
		sBuff.append("If you experience any difficulty, you can contact me or our 24x7 help desk. Help desk information is at the bottom of this e-mail.").append("\n");
		sBuff.append("\n");
		sBuff.append("\n");
		sBuff.append("Your profile information").append("\n");
		sBuff.append("---------------------------------------------------------------------------------------").append("\n");
		sBuff.append("IBM ID:                               " + userDet.getWebId()).append("\n");
		sBuff.append("Project or proposal:                  " + proj.getName()).append("\n");
		sBuff.append("Country:                              " + userDet.getCountry()).append("\n");
		sBuff.append("Company:                              " + userDet.getCompany()).append("\n");
		sBuff.append("Privilege:                            " + userDet.getAccessLevel()).append("\n");
		sBuff.append("Address:                              " + userDet.getAddress()).append("\n");
		sBuff.append("E-mail address of IBM contact:        " + woemail).append("\n");
		sBuff.append("\n");
		if (!comments.equals("")) {
			sBuff.append("Additions comments if any from the the invitee").append("\n");
			sBuff.append(comments).append("\n");
		}
		
		sBuff.append(CommonEmailHelper.getEmailFooter(unBrandedprop.getAppName()));
		
		Global.println("mail messg==" + sBuff.toString());
		Global.Init();
		
		sentMail = ETSUtils.sendEMail(from, to, ccEmail, Global.mailHost, sBuff.toString(), subject, from);
						
		
		logger.debug("INVITE result == " + sentMail);
		
		
		
		if(sentMail == true){
			incompMailResult = "INCOMP_MAIL_SENT";
			logger.debug("INCOMP_MAIL status == " + incompMailResult);
		}else{
			incompMailResult = "INCOMP_MAIL_ERROR";
			logger.debug("INCOMP_MAIL status == " + incompMailResult);
		}
		
		} catch (SQLException se) {
		 			 		
			if (se != null) {
				se.printStackTrace();
				if (logger.isErrorEnabled()) {
	            	logger.error(this,se);
	            }
				ETSUtils.displayError(out, ETSErrorCodes.getErrorCode(se), "Error occurred on AddMultMembersConfirmAction.");

			}
			
		} catch (Exception ex) {
						
			if (ex != null) {
				ex.printStackTrace();
				if (logger.isErrorEnabled()) {
	            	logger.error(this,ex);
	            }
				ETSUtils.displayError(out, ETSErrorCodes.getErrorCode(ex), "Error occurred on AddMultMembersConfirmAction.");

			}
			
		} 
		
	return incompMailResult;
}

	
	/**
	 * @param udForm
	 * @param udDAO
	 * @return
	 */
	private ActionErrors validate(
		AddMembrToWrkSpcDAO udDAO,
		BaseAddMemberForm udForm)
		throws Exception {
		ActionErrors pdErrors = new ActionErrors();
		
			pdErrors.add("",
					new ActionMessage(""));
		

		return pdErrors;
	}
	
} //end of class
