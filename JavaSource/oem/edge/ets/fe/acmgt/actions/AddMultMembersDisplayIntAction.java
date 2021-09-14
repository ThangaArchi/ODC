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
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.Global;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSErrorCodes;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.acmgt.actions.BaseAddMemberForm;
import oem.edge.ets.fe.acmgt.dao.AddMembrToWrkSpcDAO;
import oem.edge.ets.fe.acmgt.helpers.AddMemberGuiUtils;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcTeamUtils;
import oem.edge.ets.fe.aic.AICWorkspaceDAO;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.ubp.ETSUserDetails;
import oem.edge.userprofile.UserProfile;
import oem.edge.userprofile.UserProfileService;

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
public class AddMultMembersDisplayIntAction extends BaseAcmgtAction {
	
	/**
	 * @see oem.edge.ets.fe.acmgt.actions.BaseAcmgtAction#executeAction(
	 * org.apache.struts.action.ActionMapping,
	 * org.apache.struts.action.ActionForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	
	private static Log logger = EtsLogger.getLogger(AddMultMembersDisplayIntAction.class);
	public static final String VERSION = "1.3";

	
	public AddMultMembersDisplayIntAction() {
		super();
	}
	
	protected ActionForward executeAction(
		ActionMapping pdMapping,
		ActionForm pdForm,
		HttpServletRequest pdRequest,
		HttpServletResponse pdResponse)
		throws Exception {

		
		String strForward = "";
				
		ActionForward forward = new ActionForward();
		ActionErrors pdErrors = null;
			
		BaseAddMemberForm addMemberForm = (BaseAddMemberForm) pdForm;
			
		EdgeAccessCntrl es = new EdgeAccessCntrl();
		Hashtable params = new Hashtable();  //for params
		Connection conn = null;
				
		try {

			if (!Global.loaded) {
				Global.Init();
			}

			//get connection
			conn = ETSDBUtils.getConnection();
			
			if (es.GetProfile(pdResponse, pdRequest)) {
								
				// get linkid, proj, tc 
				String sLink = AmtCommonUtils.getTrimStr(pdRequest.getParameter("linkid"));
				String projectidStr = AmtCommonUtils.getTrimStr(pdRequest.getParameter("proj"));
				String topCatId = AmtCommonUtils.getTrimStr(pdRequest.getParameter("tc"));
								
				ETSProj proj = ETSDatabaseManager.getProjectDetails(conn, projectidStr);
								
				//get params
				params = AmtCommonUtils.getServletParameters(pdRequest);
				
				if(addMemberForm.getInviteUserIds() != null) {
					if(addMemberForm.getInviteUserIds().size() > 0){
											
					Vector finalInvIds = new Vector();
					Vector inviteList = addMemberForm.getInviteUserIds();
									
						  for (int j = 0; j < inviteList.size(); j++) {
						  	AddMembrUserDetails user = (AddMembrUserDetails) inviteList.elementAt(j);
					 		if(!StringUtil.isNullorEmpty(user.getSelectedUser())){
					 			if((user.getSelectedUser().trim()).equalsIgnoreCase("yes")){
					 				logger.debug("Internal Action :: chkd invId ==  " + user.getVerifyId().trim());	
					  		    if(!(user.getEnteredId().trim()).equals(user.getVerifyId().trim())){
					  		    	user.setEnteredId(user.getVerifyId());
					 				addMemberForm = processVerifiedMembers(conn,es,proj,addMemberForm,user);
					 		  }else{
					 				 finalInvIds.addElement(user);
					 		  }
					 		}
					  	 }		
					   }
					if(finalInvIds.size()>0) addMemberForm.setFinalInviteIds(finalInvIds);
				}
			}		
				
							
				if(addMemberForm.getIdsTobeConfmd() != null) {
					if(addMemberForm.getIdsTobeConfmd().size() > 0){
					
					Vector idsToBeConfmd = addMemberForm.getIdsTobeConfmd();
					Vector finalIdsToBeConfmd = new Vector();
					
					
						  for (int j = 0; j < idsToBeConfmd.size(); j++) {
						  	AddMembrUserDetails userDet = (AddMembrUserDetails) idsToBeConfmd.elementAt(j);
						  	if(!StringUtil.isNullorEmpty(userDet.getSelectedUser())){
					 		if((userDet.getSelectedUser().trim()).equalsIgnoreCase("yes")){
					 			logger.debug("Internal Action :: chkd confmId ==  " + userDet.getVerifyId().trim());
					 			if(!(userDet.getEnteredId().trim()).equals(userDet.getVerifyId().trim())){
					 				addMemberForm = processVerifiedMembers(conn,es,proj,addMemberForm,userDet);
					 		  }else{
					 		  		finalIdsToBeConfmd.addElement(userDet);
					 			}
					 		}
						  }
						}
						if(finalIdsToBeConfmd.size()>0) addMemberForm.setFinalToBeConfrmdIds(finalIdsToBeConfmd);
					}
				}
				
				  // Forward to internal ...  			
				  if(addMemberForm.getIntIdsTobeAdded() != null) 
				  			if(addMemberForm.getIntIdsTobeAdded().size() > 0) strForward = "success";
				  			
				  if(addMemberForm.getIntIdsinWS() != null)
				  			if(addMemberForm.getIntIdsinWS().size() > 0) strForward = "success";
				  			
				  if (addMemberForm.getIntUsrsWthMltiEmailId() != null)
				  			if(addMemberForm.getIntUsrsWthMltiEmailId().size() > 0) strForward = "success";
				  			
				  // Forward to external ...		
				  if(!strForward.equals("success")){			    
											    					    
						if(addMemberForm.getExtIdsTobeAdded() != null) 
							 if(addMemberForm.getExtIdsTobeAdded().size() > 0 ) strForward = "external";
						
						if(addMemberForm.getIncompExtIds() != null)
			            		if(addMemberForm.getIncompExtIds().size() > 0) strForward = "external";	 
													 
						if(addMemberForm.getExtIdsinWS()!= null)
							 if(addMemberForm.getExtIdsinWS().size() > 0) strForward = "external";
							 
						if(addMemberForm.getExtUsrsWthMltiEmailId() != null)
							 if(addMemberForm.getExtUsrsWthMltiEmailId().size() > 0) strForward = "external";
						
						if(addMemberForm.getPendingIds() != null)
							 if(addMemberForm.getPendingIds() .size() > 0) strForward = "external";
										
						if(addMemberForm.getAicExtIbmOnly() != null)
							 if(addMemberForm.getAicExtIbmOnly().size() > 0) strForward = "external";
							 
						if(addMemberForm.getNoMultiPOCEntforWO() != null)
							 if(addMemberForm.getNoMultiPOCEntforWO().size() > 0) strForward = "external";	 
						     	
				  }     					
				
				 // Forward to confirm ...	 
				 if(strForward.equals("")) strForward = "confirm";
						
						
			logger.debug("Internal FORWARD : "+strForward);
						
		   }	
	 	  }catch (SQLException se) {
	 	  	PrintWriter out = pdResponse.getWriter();
	 	  	
				if (conn != null) {
					conn.close();
				}
				if (se != null) {
					se.printStackTrace();
					if (logger.isErrorEnabled()) {
		            	logger.error(this,se);
		            }
					ETSUtils.displayError(out, ETSErrorCodes.getErrorCode(se), "Error occurred on AddMultMembersDisplayIntAction.");

				}
			
			} catch (Exception ex) {
				PrintWriter out = pdResponse.getWriter();
				
				if (ex != null) {
					ex.printStackTrace();
					if (logger.isErrorEnabled()) {
		            	logger.error(this,ex);
		            }
					ETSUtils.displayError(out, ETSErrorCodes.getErrorCode(ex), "Error occurred on AddMultMembersDisplayIntAction.");
				}

			} finally {

				if (conn != null)
					conn.close();
				conn = null;
			}
		return pdMapping.findForward(strForward);
	}

	
	private BaseAddMemberForm processVerifiedMembers(Connection conn,EdgeAccessCntrl es,ETSProj proj,BaseAddMemberForm addMemberForm,AddMembrUserDetails userDet)throws Exception{
		
		boolean aicExtIbmOnly = false;
		
		AddMembrToWrkSpcDAO addWrkSpcDao = new AddMembrToWrkSpcDAO();
		AddMemberGuiUtils addMemberUtils = new AddMemberGuiUtils();
		WrkSpcTeamUtils teamUtils = new WrkSpcTeamUtils();
		
		Vector intUsrPrev = teamUtils.getIntUserPrvlgs(proj.getProjectId());
		String sProjId = proj.getProjectId();
		
		String from = addMemberForm.getFrom();
		
		UnbrandedProperties prop = PropertyFactory.getProperty(sProjId);
		
		if((prop.getAppName().equalsIgnoreCase("Collaboration Center")) || (prop.getAppName().equalsIgnoreCase(Defines.AIC_WORKSPACE_TYPE))){
			if(AICWorkspaceDAO.isProjectIBMOnly(sProjId,conn).equalsIgnoreCase("Y")){
				aicExtIbmOnly = true;
				logger.debug("Internal Action :: aicExtIbmOnly ==  " + aicExtIbmOnly);
		 }
		}
		
		Vector aicIbmOnly = null;
		
		if(addMemberForm.getAicExtIbmOnly() == null){
			aicIbmOnly = new Vector();
		}else if(addMemberForm.getAicExtIbmOnly() != null){ 
			aicIbmOnly = addMemberForm.getAicExtIbmOnly();
		}
		
		Vector noMultiPOCEntforWOIds = null;
		
		if(addMemberForm.getNoMultiPOCEntforWO() == null){
			noMultiPOCEntforWOIds = new Vector();
		}else if(addMemberForm.getNoMultiPOCEntforWO() != null){ 
			noMultiPOCEntforWOIds = addMemberForm.getNoMultiPOCEntforWO();
		}
		
		Vector incompExtIds = null;
		
		if(addMemberForm.getIncompExtIds() != null){
			incompExtIds = addMemberForm.getIncompExtIds();
		}else if(addMemberForm.getIncompExtIds() != null){
			incompExtIds = new Vector();
		}
		
		Vector userIdsinWS = new Vector();
		Vector wsIntIds = null;
				
		if(addMemberForm.getIntIdsinWS() == null){
			wsIntIds = new Vector();
		}else if(addMemberForm.getIntIdsinWS() != null){ 
			wsIntIds = addMemberForm.getIntIdsinWS();
		}
		
		Vector wsExtIds = null; 
				
		if(addMemberForm.getExtIdsinWS() == null){
			wsExtIds = new Vector();
		}else if(addMemberForm.getExtIdsinWS() != null){ 
			wsExtIds = addMemberForm.getExtIdsinWS();
		}
		
		Vector chgdInviteUserIds = new Vector();
						
		Vector chgdIdsTobeConfmd = new Vector();
				
		Vector idsTobeAdded = new Vector();
		
		Vector intIdsTobeAdded = null;
				
		if(addMemberForm.getIntIdsTobeAdded() == null){
			intIdsTobeAdded = new Vector();
		}else if(addMemberForm.getIntIdsTobeAdded() != null){ 
			intIdsTobeAdded = addMemberForm.getIntIdsTobeAdded();
		}
		
		Vector extIdsTobeAdded = null;
				
		if(addMemberForm.getExtIdsTobeAdded() == null){
			extIdsTobeAdded = new Vector();
		}else if(addMemberForm.getExtIdsTobeAdded() != null){ 
			extIdsTobeAdded = addMemberForm.getExtIdsTobeAdded();
		}
		
		Vector pendingIds = null;
				
		if(addMemberForm.getPendingIds() == null){
			pendingIds = new Vector();
		}else if(addMemberForm.getPendingIds() != null){ 
			pendingIds = addMemberForm.getPendingIds();
		}
		
		Vector usrsWthMltiEmailId = new Vector();
		
		Vector intUsrsWthMltiEmailId = null;
			
		if(addMemberForm.getIntUsrsWthMltiEmailId() == null){
			intUsrsWthMltiEmailId = new Vector();
		}else if(addMemberForm.getIntUsrsWthMltiEmailId() != null){ 
			intUsrsWthMltiEmailId = addMemberForm.getIntUsrsWthMltiEmailId();
		}

		Vector extUsrsWthMltiEmailId = null;
				
		if(addMemberForm.getExtUsrsWthMltiEmailId() == null){
			extUsrsWthMltiEmailId = new Vector();
		}else if(addMemberForm.getExtUsrsWthMltiEmailId() != null){ 
			extUsrsWthMltiEmailId = addMemberForm.getExtUsrsWthMltiEmailId();
		}	
	
			String notesEmail = userDet.getVerifyId().trim();
			userDet.setEnteredId(notesEmail);
			
			String id = WrkSpcTeamUtils.ExtractInfoFromBluePages(notesEmail);
								
			if(StringUtil.isNullorEmpty(id)){
				id = notesEmail;
			}else{
				UserProfileService userProfileService = UserProfileService.getInstance();
				UserProfile profile = userProfileService.getUserProfile(id);
				id = profile.getEmailAddr();
			}
			
			userDet.setEmailId(id);
			
			// Verify each user
			String userStatus = addMemberUtils.verifyUser(proj,id);
			logger.debug("Internal Action :: USER STATUS " + userStatus);
			
			if(! StringUtil.isNullorEmpty(userStatus)){
		        if(userStatus.equals("userIdsinWS")){
		        	   	
	            	 userIdsinWS.addElement(userDet);
	            		
	            }else if(userStatus.equals("idsTobeAdded")){
	            	   	
	            	idsTobeAdded.addElement(userDet);
	            	
	            }else if(userStatus.equals("usrsWthMltiEmailId")){
	            					            	   	
	            	 usrsWthMltiEmailId.addElement(userDet);
	            	
	            }else if(userStatus.equals("idsTobeConfmd")){
	            				            	   	
	            	chgdIdsTobeConfmd.addElement(userDet);
	            	
	            }else if(userStatus.equals("inviteUserIds")){
	            	   	
	            	chgdInviteUserIds.addElement(userDet);
	            }
		}
			
		
				//set users in corresponding ActionForm attributes...
				
			if(userIdsinWS != null){
		    	if(! userIdsinWS.isEmpty()) {
					
					for (int i = 0; i < userIdsinWS.size(); i++) {
						AddMembrUserDetails user = (AddMembrUserDetails) userIdsinWS.elementAt(i);
						ETSUserDetails defndUser = (ETSUserDetails) addWrkSpcDao.getUserDetails(conn,user.getEmailId());
						user.setUserName(defndUser.getFirstName()+" "+defndUser.getLastName());
						user.setWebId(defndUser.getWebId());
						user.setEmailId(defndUser.getEMail());
						if ((defndUser.getUserType() == defndUser.USER_TYPE_INTERNAL)){
							wsIntIds.addElement(user);
					    }else if((defndUser.getUserType() == defndUser.USER_TYPE_EXTERNAL)){
					    	user.setAddress(defndUser.getStreetAddress());
							user.setCompany(defndUser.getCompany());
							user.setCountryCode(defndUser.getCountryCode());
							user.setCountry(addWrkSpcDao.getCountryName(conn,defndUser.getCountryCode()));	
					    	wsExtIds.addElement(user);
			 	        }
					}
					if(wsIntIds != null)
						if(! wsIntIds.isEmpty()) addMemberForm.setIntIdsinWS(wsIntIds);
					if(wsExtIds != null)
						if(! wsExtIds.isEmpty()) addMemberForm.setExtIdsinWS(wsExtIds);
				  }
				}
				
					   
				if(chgdInviteUserIds != null)
				logger.debug("Verify Action :: settingForm chgdInviteUserIds"); 
				if(! chgdInviteUserIds.isEmpty()) addMemberForm.setChgdInviteUserIds(chgdInviteUserIds);
																
				if(idsTobeAdded != null){
				if(! idsTobeAdded.isEmpty()) {
					
					boolean womultipoc = true;
					
					if(addMemberForm.getWoHasMultiPOC() == 0){
						womultipoc = false;
					}else{
						womultipoc = true;
					}
											
					for (int i = 0; i < idsTobeAdded.size(); i++) {
					AddMembrUserDetails user2 = (AddMembrUserDetails) idsTobeAdded.elementAt(i);
					user2 = addMemberUtils.verifyIdsTobeAdded(conn,user2);
					
					String sUserId = user2.getWebId();
					
					if(user2.getUserType().trim().equals("I")){
						intIdsTobeAdded.addElement(user2);
						
					}else if(user2.getUserType().trim().equals("E")){
						
					   if(!from.equals("import")){
							String accessLevel = "Visitor";
							for(int k=0; k < intUsrPrev.size(); k++){
				 	   	   		UserPrivileges priv = (UserPrivileges) intUsrPrev.elementAt(k);
				 	   	   			 if(priv.getRoleName().equalsIgnoreCase(accessLevel.trim())){
				 	   	   			 	   user2.setPrevilage(priv.getRoleId());
				 	   	   			 }
				 	   	    }
						}
											
						if(aicExtIbmOnly == true){
							aicIbmOnly.addElement(user2);
						}else if(!womultipoc){
							noMultiPOCEntforWOIds.addElement(user2);	
						}else if(StringUtil.isNullorEmpty(user2.getAddress())){
							incompExtIds.addElement(user2);
						}else{
							extIdsTobeAdded.addElement(user2);
						}
						
					}else if(user2.getUserType().trim().equals("P")){
						pendingIds.addElement(user2);
					 }else{
					 		chgdIdsTobeConfmd.addElement(user2);
						}
					}
					
					if(intIdsTobeAdded != null)
						if(! intIdsTobeAdded.isEmpty()) addMemberForm.setIntIdsTobeAdded(intIdsTobeAdded);
					if(extIdsTobeAdded != null)
						if(! extIdsTobeAdded.isEmpty()) addMemberForm.setExtIdsTobeAdded(extIdsTobeAdded);
					if(pendingIds != null)
						if(! pendingIds.isEmpty()) addMemberForm.setPendingIds(pendingIds);
					if(aicIbmOnly != null)	
						if(! aicIbmOnly.isEmpty()) addMemberForm.setAicExtIbmOnly(aicIbmOnly);
					if(incompExtIds != null)	
						if(! incompExtIds.isEmpty()) addMemberForm.setIncompExtIds(incompExtIds);
					if(noMultiPOCEntforWOIds != null)	
						if(! noMultiPOCEntforWOIds.isEmpty()) addMemberForm.setNoMultiPOCEntforWO(noMultiPOCEntforWOIds);
						
			 }	
		  }	
				
					
			if(usrsWthMltiEmailId != null){
				if(! usrsWthMltiEmailId.isEmpty()) {
						for (int i = 0; i < usrsWthMltiEmailId.size(); i++) {
							if(aicIbmOnly == null) aicIbmOnly = new Vector();
							else if(aicIbmOnly.size() < 1) aicIbmOnly = new Vector();
							AddMembrUserDetails user = (AddMembrUserDetails) usrsWthMltiEmailId.elementAt(i);
							ETSUserDetails defndUser = (ETSUserDetails) addWrkSpcDao.getUserDetails(conn,user.getEmailId());
							user.setUserName(defndUser.getFirstName()+" "+defndUser.getLastName());
							user.setWebId(defndUser.getWebId());
							user.setEmailId(defndUser.getEMail());
							if ((defndUser.getUserType() == defndUser.USER_TYPE_INTERNAL)){
								intUsrsWthMltiEmailId.addElement(user);
						    }else if((defndUser.getUserType() == defndUser.USER_TYPE_EXTERNAL)){
						    	if(aicExtIbmOnly == true){
									aicIbmOnly.addElement(user);	
						    	}else{
						    	user.setAddress(defndUser.getStreetAddress());
								user.setCompany(defndUser.getCompany());
								user.setCountryCode(defndUser.getCountryCode());
								user.setCountry(addWrkSpcDao.getCountryName(conn,defndUser.getCountryCode()));	
						    	extUsrsWthMltiEmailId.addElement(user);
				 	        }
						  }else if(defndUser.getUserType() == defndUser.USER_TYPE_INTERNAL_PENDING_VALIDATION){
						  			pendingIds.addElement(user);
						  }else{
						  		chgdIdsTobeConfmd.addElement(user);
						  }  	
						}
						
						if(intUsrsWthMltiEmailId != null)
							if(! intUsrsWthMltiEmailId.isEmpty()) addMemberForm.setIntUsrsWthMltiEmailId(intUsrsWthMltiEmailId);
						if(extUsrsWthMltiEmailId != null)
							if(! extUsrsWthMltiEmailId.isEmpty()) addMemberForm.setExtUsrsWthMltiEmailId(extUsrsWthMltiEmailId);
						if(aicIbmOnly != null)	
							if(! aicIbmOnly.isEmpty()) addMemberForm.setAicExtIbmOnly(aicIbmOnly);
						if(pendingIds != null)
							if(! pendingIds.isEmpty()) addMemberForm.setPendingIds(pendingIds);
													
					   }
					}
				
				if(chgdIdsTobeConfmd != null)
					if(! chgdIdsTobeConfmd.isEmpty()) addMemberForm.setChgdIdsTobeConfmd(chgdIdsTobeConfmd);
				
			return addMemberForm;	
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
