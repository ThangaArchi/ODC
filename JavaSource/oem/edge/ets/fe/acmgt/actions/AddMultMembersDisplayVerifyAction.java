
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.userprofile.UserProfile;
import oem.edge.userprofile.UserProfileService;
import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.ubp.ETSUserDetails;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSErrorCodes;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.common.Global;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSProj;

import oem.edge.ets.fe.acmgt.actions.BaseAddMemberForm;
import oem.edge.ets.fe.acmgt.dao.AddMembrToWrkSpcDAO;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcTeamUtils;
import oem.edge.ets.fe.acmgt.helpers.AddMemberGuiUtils;
import oem.edge.ets.fe.aic.AICWorkspaceDAO;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * @author Suresh
 *
 */
public class AddMultMembersDisplayVerifyAction extends BaseAcmgtAction {

	/**
	 * @see oem.edge.ets.fe.acmgt.actions.BaseAcmgtAction#executeAction(
	 * org.apache.struts.action.ActionMapping,
	 * org.apache.struts.action.ActionForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	
	private static Log logger = EtsLogger.getLogger(AddMultMembersDisplayVerifyAction.class);
	public static final String VERSION = "1.3";

	
	public AddMultMembersDisplayVerifyAction() {
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

		BaseAddMemberForm addMemberForm = (BaseAddMemberForm) pdForm; 
		
		WrkSpcTeamUtils teamUtils = new WrkSpcTeamUtils();
		AddMembrToWrkSpcDAO addWrkSpcDao = new AddMembrToWrkSpcDAO();
		AddMemberGuiUtils addMemberUtils = new AddMemberGuiUtils();
		
		EdgeAccessCntrl es = new EdgeAccessCntrl();
		
		Hashtable params = new Hashtable(); 
		Connection conn = null;
		boolean multipleUsers = true;
		boolean aicWS = false;
		boolean aicExtIbmOnly = false;
		boolean aicAllUsers = false;
		Vector usersVector = null;
		
		try {

			if (!Global.loaded) {
				Global.Init();
			}
			//get connection
			conn = ETSDBUtils.getConnection();
						
			if (es.GetProfile(pdResponse, pdRequest)) {
				
				String sLink = "";
				String projectidStr = "";
	    		String topCatId = "";
	    		String from = "";
	    		String multiIds = "";
	    		
				// get linkid, proj, tc 
				sLink = AmtCommonUtils.getTrimStr(pdRequest.getParameter("linkid"));
				projectidStr = AmtCommonUtils.getTrimStr(pdRequest.getParameter("proj"));
				topCatId = AmtCommonUtils.getTrimStr(pdRequest.getParameter("tc"));
				multiIds = AmtCommonUtils.getTrimStr(pdRequest.getParameter("MultiIds"));
				from = AmtCommonUtils.getTrimStr(pdRequest.getParameter("from"));
											
				if(StringUtil.isNullorEmpty(sLink)) sLink = addMemberForm.getLinkid(); 
				if(StringUtil.isNullorEmpty(projectidStr)) projectidStr = addMemberForm.getProj(); 
				if(StringUtil.isNullorEmpty(topCatId)) topCatId = addMemberForm.getTc();
				if(StringUtil.isNullorEmpty(from)) from = addMemberForm.getFrom();
				
	 		    if(from.equalsIgnoreCase("import")) usersVector = (Vector) addMemberForm.getImportUsers();
	 		    																			
				ETSProj proj = ETSDatabaseManager.getProjectDetails(conn, projectidStr);
				String sProjId = proj.getProjectId();
							
				//get params
				params = AmtCommonUtils.getServletParameters(pdRequest);
				
				// Reset the ActionForm.
				addMemberForm = resetAddMemberForm(addMemberForm);
				
				UnbrandedProperties prop = PropertyFactory.getProperty(proj.getProjectType());
				pdRequest.setAttribute("prop",prop);
				addMemberForm.setPropAppName(prop.getAppName());
				
				if((prop.getAppName().equalsIgnoreCase("Collaboration Center")) || (prop.getAppName().equalsIgnoreCase(Defines.AIC_WORKSPACE_TYPE))){
				 	aicWS = true;
					
					String isIbmOnly = AICWorkspaceDAO.isProjectIBMOnly(proj.getProjectId(),conn);
					if(isIbmOnly.equalsIgnoreCase("Y")){
						aicExtIbmOnly = true;
						logger.debug("Verify Action :: aicExtIbmOnly ==  " + aicExtIbmOnly);
				    }else if(isIbmOnly.equalsIgnoreCase("N")){
				    	aicAllUsers = true;
				    }
				    
				}
				
				String userIds = null;
				String importIds = "";
				
				if(from.equalsIgnoreCase("import")){
									
					addMemberForm.setFrom("import");
					
					for(int i=0; i < usersVector.size(); i++){
						
						AddMembrUserDetails importUsers = (AddMembrUserDetails) usersVector.elementAt(i);
						
						importIds = importIds + importUsers.getEnteredId() + ",";
						logger.debug("Verify Action :: inside IMPORT block ID ==  " + importIds);
						
					}
					userIds = importIds;
					logger.debug("Verify Action :: inside IMPORT block UserIDs ==  " + userIds);
					
				}else{
				
					addMemberForm.setFrom("inputEntered");
					//get multiple userIds
					userIds = multiIds;
				
				}
				if ((!StringUtil.isNullorEmpty(userIds)) && ( !userIds.equalsIgnoreCase("Please enter the IDs here."))) {
					
					addMemberForm.setMultIds(userIds);
					Vector userIdsVect = validateString(userIds);
					int idsLen = userIdsVect.size();
									    
			   if(idsLen>0){
			   	
				  if((idsLen == 1) && (!from.equalsIgnoreCase("import"))){
				  	 if(!aicWS){
				  	 	multipleUsers = false; 
				  		strForward = "singleAddMember";
				  	 }	
				  }   				       	
				       
				Vector userIdsinWS = new Vector();
				Vector inviteUserIds = new Vector();
				Vector idsTobeConfmd = new Vector();
				Vector idsTobeAdded = new Vector();
				Vector usrsWthMltiEmailId = new Vector();
											
				//set company,country,privileges list
				Vector intUsrPrev = teamUtils.getIntUserPrvlgs(proj.getProjectId());
				Vector extUsrPrev = teamUtils.getExtUserPrvlgs(proj.getProjectId());
				Vector ctryList = teamUtils.getCntryList();
				ArrayList cmpnyList = teamUtils.getCompList();
				
				if(! intUsrPrev.isEmpty()) addMemberForm.setIntUsrPrvlgs(intUsrPrev);
				if(! extUsrPrev.isEmpty()) addMemberForm.setExtUsrPrvlgs(extUsrPrev);
				if(! ctryList.isEmpty()) addMemberForm.setCountryList(ctryList);
				if(! cmpnyList.isEmpty()) addMemberForm.setCompanyList(cmpnyList);
				
				//process the userIds Vector...
				for (int i = 0; i < userIdsVect.size(); i++) {
					
					AddMembrUserDetails user = new AddMembrUserDetails();
					String notesEmail = (String) userIdsVect.elementAt(i);
					String id = "";
										
					if(!StringUtil.isNullorEmpty(notesEmail)){
						user.setEnteredId(notesEmail);
						id = WrkSpcTeamUtils.ExtractInfoFromBluePages(notesEmail);
					}
					
					if(StringUtil.isNullorEmpty(id)){
						id = notesEmail;
					}else{
						UserProfileService userProfileService = UserProfileService.getInstance();
						UserProfile profile = userProfileService.getUserProfile(id);
						id = profile.getEmailAddr();
					}
					
					user.setEmailId(id);
					
					if(from.equalsIgnoreCase("import")){
						if((usersVector != null) && (usersVector.size()>0)){
							for(int j=0; j < usersVector.size(); j++){
						 	  AddMembrUserDetails importUsers = (AddMembrUserDetails) usersVector.elementAt(j);
						 	 if((!StringUtil.isNullorEmpty(user.getEnteredId())) && (!StringUtil.isNullorEmpty(importUsers.getEnteredId()))){
						 	   if(user.getEnteredId().equalsIgnoreCase(importUsers.getEnteredId())){
						 	   	   if(importUsers.getAccessLevel() != null){
						 	   	logger.debug("Verify Action :: IMPORT importUsers.getAccessLevel() ==  " + importUsers.getAccessLevel()+ "for user == " +importUsers.getEnteredId());  	
						 	   	   		for(int k=0; k < intUsrPrev.size(); k++){
						 	   	   		UserPrivileges priv = (UserPrivileges) intUsrPrev.elementAt(k);
						 	   	   			 if(priv.getRoleName().equalsIgnoreCase(importUsers.getAccessLevel().trim())){
						 	   	   			logger.debug("Verify Action :: IMPORT priv.getRoleName()() ==  " + priv.getRoleName()+ "for user == " +importUsers.getEnteredId());
						 	   	   			 	   user.setPrevilage(priv.getRoleId());
						 	   	   			 }
						 	   	   		logger.debug("Verify Action :: IMPORT user.getPrevilage() ==  " + user.getPrevilage()+ "for user == " +importUsers.getEnteredId());	 
						 	   	   		}
						 	   	    }
						 	   	   
						 	   	 if(importUsers.getJob() != null){
						 	   	  	       user.setJob(importUsers.getJob());
						 	   	   }
						 	   	 if(importUsers.getMsgrID() != null){
					 	   	  	       user.setMsgrID(importUsers.getMsgrID());
					 	   	     }
						 	   	if(importUsers.getUserName() != null){
					 	   	  	       user.setUserName(importUsers.getUserName());
					 	   	   }
						 	   	
						 	  if(StringUtil.isNullorEmpty(user.getPrevilage())){
							 	   		String accessLevel = "Member";
							 	   		for(int k=0; k < intUsrPrev.size(); k++){
						 	   	   		UserPrivileges priv = (UserPrivileges) intUsrPrev.elementAt(k);
						 	   	   			 if(priv.getRoleName().equalsIgnoreCase(accessLevel.trim())){
						 	   	   			 	   user.setPrevilage(priv.getRoleId());
						 	   	   			 }
						 	   	   		logger.debug("Verify Action :: IMPORT INSIDE DEFAULT setting ==  " + user.getPrevilage()+ "for user == " +importUsers.getEnteredId());			 
						 	   	   		}
						 	   	}
						 	   	
						 	   } // if enteredId equal...
							}
						} 
					  }	
					} // imported users...
					else{
						String accessLevel = "Member";
						for(int k=0; k < intUsrPrev.size(); k++){
			 	   	   		UserPrivileges priv = (UserPrivileges) intUsrPrev.elementAt(k);
			 	   	   			 if(priv.getRoleName().equalsIgnoreCase(accessLevel.trim())){
			 	   	   			 	   user.setPrevilage(priv.getRoleId());
			 	   	   			 }
			 	   	   		}
					} // for entered users...
					
					
					// Verify each user
					String userStatus = addMemberUtils.verifyUser(proj,id);
					
					if(from.equalsIgnoreCase("import")){
						if(userStatus.equalsIgnoreCase("idsTobeConfmd") || userStatus.equalsIgnoreCase("inviteUserIds")){
						if((usersVector != null) && (usersVector.size()>0)){
							for(int j=0; j < usersVector.size(); j++){
						 	  AddMembrUserDetails importUsers = (AddMembrUserDetails) usersVector.elementAt(j);
						 	 if((!StringUtil.isNullorEmpty(user.getEnteredId())) && (!StringUtil.isNullorEmpty(importUsers.getEnteredId()))){
						 	   if(user.getEnteredId().equalsIgnoreCase(importUsers.getEnteredId())){
						 	   	   if(!StringUtil.isNullorEmpty(importUsers.getEnteredId2())){
						 	          if(!importUsers.getEnteredId2().equalsIgnoreCase(importUsers.getEnteredId())){ 	   	
						 	   	   	    user.setEnteredId(importUsers.getEnteredId2());
						 	   	   	    logger.debug("Verify Action :: for import users USER STATUS b4 verifying email == " + userStatus);
						 	   	   	    String id2 = WrkSpcTeamUtils.ExtractInfoFromBluePages(importUsers.getEnteredId2());
										if(StringUtil.isNullorEmpty(id2)){
											id2 = importUsers.getEnteredId2();
										}else{
											UserProfileService userProfileService = UserProfileService.getInstance();
											UserProfile profile = userProfileService.getUserProfile(id2);
											id2 = profile.getEmailAddr();
										}
										user.setEmailId(id2);
										
										//  Verify user again using emailId(enteredId2) for import users only...
										userStatus = addMemberUtils.verifyUser(proj,id2);
						 	   	   	
						 	   	    }
						 	     } // if enteredId equal...
						 	  }	   
							}
						  }	   
						} 
				  	  }	
					}
					
					logger.debug("Verify Action :: USER STATUS " + userStatus);
					// check the status after verifyUser
					if(! StringUtil.isNullorEmpty(userStatus)){
						        if(userStatus.equalsIgnoreCase("userIdsinWS")){
						        	if(!multipleUsers){pdRequest = processSingleAddMember(pdRequest,user.getEnteredId(),id); break;}   	
					            	else userIdsinWS.addElement(user);
					            		
					            }else if(userStatus.equalsIgnoreCase("idsTobeAdded")){
					            	if(!multipleUsers) {pdRequest = processSingleAddMember(pdRequest,user.getEnteredId(),id); break;}   	
					            	else idsTobeAdded.addElement(user);
					            	
					            }else if(userStatus.equalsIgnoreCase("usrsWthMltiEmailId")){
					            	
					            	if(!multipleUsers) {pdRequest = processSingleAddMember(pdRequest,user.getEnteredId(),id); break;}   	
					            	else usrsWthMltiEmailId.addElement(user);
					            	
					            }else if(userStatus.equalsIgnoreCase("idsTobeConfmd")){
					            	 strForward = "success"; 			            	   	
					            	 idsTobeConfmd.addElement(user);
					            	
					            }else if(userStatus.equalsIgnoreCase("inviteUserIds")){
					            	if(!multipleUsers) {pdRequest = processSingleAddMember(pdRequest,user.getEnteredId(),id); break;}   	
					            	else inviteUserIds.addElement(user);
					            }
						}
				} 
				
				//set users in corresponding ActionForm attributes...
				
				if(aicAllUsers == true){
					addMemberForm.setAicAllUsers(1);
				}
				
			    if(userIdsinWS != null){
			    	if(! userIdsinWS.isEmpty()) {
						Vector wsIntIds = new Vector();
						Vector wsExtIds = new Vector(); 
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
			   			
			   				   
				if(inviteUserIds != null)
					if(! inviteUserIds.isEmpty()) addMemberForm.setInviteUserIds(inviteUserIds);
										
				Vector aicIbmOnly = null;
				Vector pendingIds = null;
				Vector noMultiPOCEntforWOIds = null;
								
				if(idsTobeAdded != null){
				if(! idsTobeAdded.isEmpty()) {
										
					Vector intIdsTobeAdded = new Vector();
					aicIbmOnly = new Vector();
					noMultiPOCEntforWOIds = new Vector();
					Vector extIdsTobeAdded = new Vector();
					pendingIds = new Vector();
					Vector incompExtIds = new Vector();
					
					boolean womultipoc = true;
					// multi poc check for externals
					if (ETSUtils.checkUserRole(es, sProjId) == Defines.WORKSPACE_OWNER) {
							womultipoc = WrkSpcTeamUtils.checkOwnerMultiPOC(conn, sProjId, es.gIR_USERN);
					}
										
					if(!womultipoc){
						addMemberForm.setWoHasMultiPOC(0);
					}else{
						addMemberForm.setWoHasMultiPOC(1);
					}
										
					for (int i = 0; i < idsTobeAdded.size(); i++) {
						AddMembrUserDetails user = (AddMembrUserDetails) idsTobeAdded.elementAt(i);
						user = addMemberUtils.verifyIdsTobeAdded(conn,user);
						
						String sUserId = user.getWebId();
												
						if(user.getUserType().trim().equalsIgnoreCase("I")){
								intIdsTobeAdded.addElement(user);
								
						}else if(user.getUserType().trim().equalsIgnoreCase("E")){
																					
							if(aicExtIbmOnly == true){
								aicIbmOnly.addElement(user);	
							}else if(!womultipoc){
								noMultiPOCEntforWOIds.addElement(user);	
							}else if(StringUtil.isNullorEmpty(user.getAddress())){
								incompExtIds.addElement(user);
							}else{	
								extIdsTobeAdded.addElement(user);
							}
							
						}else if(user.getUserType().trim().equalsIgnoreCase("P")){
								pendingIds.addElement(user);
						}else if(user.getUserType().trim().equalsIgnoreCase("INVALID")){
							idsTobeConfmd.addElement(user);
						}
						
					 }
						if(intIdsTobeAdded != null)
							if(! intIdsTobeAdded.isEmpty()) addMemberForm.setIntIdsTobeAdded(intIdsTobeAdded);
						if(extIdsTobeAdded != null)
							if(! extIdsTobeAdded.isEmpty()) addMemberForm.setExtIdsTobeAdded(extIdsTobeAdded);
						if(pendingIds != null)
							if(! pendingIds.isEmpty()) addMemberForm.setPendingIds(pendingIds);
						if(incompExtIds != null)	
							if(! incompExtIds.isEmpty()) addMemberForm.setIncompExtIds(incompExtIds);
						if(aicIbmOnly != null)	
							if(! aicIbmOnly.isEmpty()) addMemberForm.setAicExtIbmOnly(aicIbmOnly);
						if(noMultiPOCEntforWOIds != null)	
							if(! noMultiPOCEntforWOIds.isEmpty()) addMemberForm.setNoMultiPOCEntforWO(noMultiPOCEntforWOIds);	
				   }	
				}	
						 				
			 if(usrsWthMltiEmailId != null){
				if(! usrsWthMltiEmailId.isEmpty()) {
					if(pendingIds == null) pendingIds = new Vector();
					else if(pendingIds.size() < 1) pendingIds = new Vector();
					if(aicIbmOnly == null) aicIbmOnly = new Vector();
					else if(aicIbmOnly.size() < 1) aicIbmOnly = new Vector();
					Vector intUsrsWthMltiEmailId = new Vector();
					Vector extUsrsWthMltiEmailId = new Vector();
					for (int i = 0; i < usrsWthMltiEmailId.size(); i++) {
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
					   		idsTobeConfmd.addElement(user);
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
			 
			 if(idsTobeConfmd != null)
				if(! idsTobeConfmd.isEmpty()) addMemberForm.setIdsTobeConfmd(idsTobeConfmd);
			 
			   	logger.debug("Verify Action :: before fwd");   
				
				// Forward the request...
			if(!strForward.equalsIgnoreCase("singleAddMember")){	
				if((addMemberForm.getInviteUserIds() != null) || (addMemberForm.getIdsTobeConfmd() != null)){
							strForward = "success";
											 
				}else if((addMemberForm.getIntIdsTobeAdded()!= null) || (addMemberForm.getIntIdsinWS()!= null)|| (addMemberForm.getIntUsrsWthMltiEmailId() != null)){
							strForward = "internal";
					 								 	
				}else if((addMemberForm.getExtIdsTobeAdded() != null) || (addMemberForm.getExtIdsinWS()!= null)
						|| (addMemberForm.getExtUsrsWthMltiEmailId() != null) || (addMemberForm.getPendingIds() != null)
					    || (addMemberForm.getIncompExtIds() != null) ||  (addMemberForm.getAicExtIbmOnly() != null)
						|| (addMemberForm.getNoMultiPOCEntforWO() != null)){
					
					 	          	strForward = "external";
					}
			 	}
				
				logger.debug("Verify Action :: FORWARD " + strForward);
										 					 
		   	   				       
			  }	// idsLen > 0
				else {
		    	 
		    	logger.debug("----- UserIds is null ------");
		    	addMemberForm.setUserIdsNull(1);
		      } // idsLen > 0
					
		    }else {
		    	 
		    	logger.debug("----- UserIds is null ------");
		    	addMemberForm.setUserIdsNull(1);
		    	
		     }  // if (!StringUtil.isNullorEmpty(userIds))	 
		    	
		} // if (es.GetProfile(pdResponse, pdRequest))
			
			logger.debug("Verify Action :: FORWARD " + strForward);
												
		} catch (SQLException se) {
			
			PrintWriter out = pdResponse.getWriter();

			if (conn != null) {
				conn.close();
			}
			if (se != null) {
				se.printStackTrace();
				if (logger.isErrorEnabled()) {
	            	logger.error(this,se);
	            }
				ETSUtils.displayError(out, ETSErrorCodes.getErrorCode(se), "Error occurred on AddMultMembersDisplayVerifyAction.");

			}
			
		} catch (Exception ex) {
			
			PrintWriter out = pdResponse.getWriter();

			if (ex != null) {
				ex.printStackTrace();
				if (logger.isErrorEnabled()) {
	            	logger.error(this,ex);
	            }
				ETSUtils.displayError(out, ETSErrorCodes.getErrorCode(ex), "Error occurred on AddMultMembersDisplayVerifyAction.");

			}

		} finally {

			if (conn != null)
				conn.close();
				conn = null;
		}
		
		return pdMapping.findForward(strForward);
	}
	
		
	private Vector validateString(String userIds){
		
		Vector IdsVect = new Vector(); 
		if(userIds.indexOf(",") == -1){
			IdsVect.addElement(userIds.toLowerCase().trim());
		}else{
		StringTokenizer st = new StringTokenizer(userIds, ",");
		while (st.hasMoreTokens()) {
			String tok = st.nextToken();
			if(! StringUtil.isNullorEmpty(tok)) IdsVect.addElement(tok.toLowerCase().trim());
		  }
		}
	
		return IdsVect;
	}
	
	
	private BaseAddMemberForm resetAddMemberForm(BaseAddMemberForm addMemberForm){
		
		addMemberForm.setPropAppName("");
		addMemberForm.setUserIdsinWS(null);
		addMemberForm.setInviteUserIds(null);
		addMemberForm.setIdsTobeConfmd(null);
		addMemberForm.setWsCompany(null);
		addMemberForm.setIntIdsTobeAdded(null);
		addMemberForm.setExtIdsTobeAdded(null);
		addMemberForm.setIntUsrsWthMltiEmailId(null);
		addMemberForm.setExtUsrsWthMltiEmailId(null);
		addMemberForm.setInvEmailIds(null);
		addMemberForm.setChkEmailIds(null);
		addMemberForm.setIbmUserList(null);
		addMemberForm.setExtUserList(null);
		addMemberForm.setWarnExtIds(null);
		addMemberForm.setChkWarnIds(null);
		addMemberForm.setUserIdsNull(0);
		addMemberForm.setNoResults(0);
		addMemberForm.setAicAllUsers(0);
		addMemberForm.setInvitedUsers(null);
		addMemberForm.setInviteError(null);
		addMemberForm.setFinalVerifyIds(null);
		addMemberForm.setAddedSuccess(null);
		addMemberForm.setAicExtIbmOnly(null);
		addMemberForm.setExtAdedReqEnt(null);
		addMemberForm.setIntAdedReqEntPen(null);
		addMemberForm.setExtAdedReqEntPen(null);
		addMemberForm.setExtReqWo(null);
		addMemberForm.setAddError(null);
		addMemberForm.setExtHasPendReq(null);
		addMemberForm.setChgdIdsTobeConfmd(null);
		addMemberForm.setChgdInviteUserIds(null);
		addMemberForm.setChkdIncompExtIds(null);
		addMemberForm.setChkIncompWarnIds(null);
		addMemberForm.setIntIdsinWS(null);
		addMemberForm.setExtIdsinWS(null);
		addMemberForm.setExtIncompIds(null);
		addMemberForm.setFinalInviteIds(null);
		addMemberForm.setFinalIncompExtIds(null);
		addMemberForm.setFinalPendingIds(null);
		addMemberForm.setFinalToBeConfrmdIds(null);
		addMemberForm.setIncompMailErrorIds(null);
		addMemberForm.setPendingIds(null);
		addMemberForm.setIncompMailSentIds(null);
		addMemberForm.setIncompWarnExtIds(null);
		addMemberForm.setNoMultiPOCEntforWO(null);
						
		return addMemberForm;
	}	
	
	private HttpServletRequest processSingleAddMember(HttpServletRequest pdRequest,String entrdSingleId,String extractedSingleId) throws Exception{
		       	
       	pdRequest.setAttribute("entd_add_id",entrdSingleId);
		pdRequest.setAttribute("add_id",extractedSingleId);
	
		return pdRequest;
	}

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
