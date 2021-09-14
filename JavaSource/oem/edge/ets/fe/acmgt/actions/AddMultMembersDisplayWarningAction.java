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
import java.util.Vector;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.Global;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSErrorCodes;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.acmgt.actions.BaseAddMemberForm;
import oem.edge.ets.fe.acmgt.dao.AddMembrToWrkSpcDAO;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.StringUtil;

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
public class AddMultMembersDisplayWarningAction extends BaseAcmgtAction {

	/**
	 * @see oem.edge.ets.fe.acmgt.actions.BaseAcmgtAction#executeAction(
	 * org.apache.struts.action.ActionMapping,
	 * org.apache.struts.action.ActionForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	
	private static Log logger = EtsLogger.getLogger(AddMultMembersDisplayWarningAction.class);
	public static final String VERSION = "1.3";

	
	public AddMultMembersDisplayWarningAction() {
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
		AddMembrToWrkSpcDAO addWrkSpcDao = new AddMembrToWrkSpcDAO();
		
		EdgeAccessCntrl es = new EdgeAccessCntrl();
		Hashtable params = new Hashtable();  //for params
		Connection conn = null;
		boolean isWarnMsg = false;
				
		try {

			if (!Global.loaded) {
				Global.Init();
			}

			//get connection
			conn = ETSDBUtils.getConnection();
			
			if (es.GetProfile(pdResponse, pdRequest)) {
								
				// get linkid, proj, tc 
				logger.debug("warning Action 1 entering getProfile = " +strForward);
				String sLink = AmtCommonUtils.getTrimStr(pdRequest.getParameter("linkid"));
				String projectidStr = AmtCommonUtils.getTrimStr(pdRequest.getParameter("proj"));
				String topCatId = AmtCommonUtils.getTrimStr(pdRequest.getParameter("tc"));
								
				ETSProj proj = ETSDatabaseManager.getProjectDetails(conn, projectidStr);
				addMemberForm.setWsCompany(addWrkSpcDao.getWSCompany(conn,proj.getProjectId()));
				logger.debug("WS Compant == " +addMemberForm.getWsCompany());
				logger.debug("warning Action 2 after projectDetails = " +strForward);

				//get params
				params = AmtCommonUtils.getServletParameters(pdRequest);
				logger.debug("warning Action 3 after params = " +strForward);
						
			 	Vector warnExtIds = new Vector();
			 	Vector warnIncompExtIds = new Vector();
			 	Vector chkdExtList = new Vector();
			 	Vector chkdIncompExtList = new Vector();
			 	String wsCompany = addMemberForm.getWsCompany();
											
				if(addMemberForm.getExtIdsTobeAdded() != null){
				  if(addMemberForm.getExtIdsTobeAdded().size() > 0){
					
				  	Vector extToBeAdded = addMemberForm.getExtIdsTobeAdded();
				 					 		
					 		for( int j = 0; j<extToBeAdded.size(); j++){
					 			AddMembrUserDetails userDet = (AddMembrUserDetails) extToBeAdded.elementAt(j);
					 			if(!StringUtil.isNullorEmpty(userDet.getSelectedUser())){
					 			  if((userDet.getSelectedUser().trim()).equalsIgnoreCase("yes")){
						 			logger.debug("Warning Action :: chkd extId ==  " + userDet.getWebId().trim());	
					 				logger.debug("COUNTRY in Warning Action = " +userDet.getCompany());
				 				 	   if(! userDet.getCompany().equals(wsCompany) ) {
				 				 	   			isWarnMsg = true; 
				 				 	   			warnExtIds.addElement(userDet);
				 				 	   			logger.debug("warnExtIds in Warning Action = " +userDet.getEnteredId());
				 				 	    }else{
				 				 	    		chkdExtList.addElement(userDet);
				 				 	   			logger.debug("warnExtIds in Warning Action = " +warnExtIds);
				 					    }
				 			    }	
				 			}	   
				 		}
				  	}
				}
			   
				  
				 if(addMemberForm.getIncompExtIds() != null) {
				 	if(addMemberForm.getIncompExtIds().size() > 0){
				 	 
				 	 Vector incompIds = addMemberForm.getIncompExtIds();
				 					 				 	
			 			for( int j = 0; j<incompIds.size(); j++){
					 			
					 			AddMembrUserDetails userDet = (AddMembrUserDetails) incompIds.elementAt(j);
					 			if(!StringUtil.isNullorEmpty(userDet.getSelectedUser())){
						 			 if((userDet.getSelectedUser().trim()).equalsIgnoreCase("yes")){
							 		   logger.debug("Warning Action :: chkd incompExtId ==  " + userDet.getWebId().trim());	
					 				   logger.debug("COUNTRY in Warning Action = " +userDet.getCompany());
				 				 	   if(! userDet.getCompany().equals(wsCompany) ) {
				 				 	   			isWarnMsg = true; 
				 				 	   			warnIncompExtIds.addElement(userDet);
				 				 	   			logger.debug("warnExtIds in Warning Action = " +userDet.getEnteredId());
				 				 	    }else{
				 				 	    		chkdIncompExtList.addElement(userDet);
				 				 	   			logger.debug("warnExtIds in Warning Action = " +warnExtIds);
				 					    }
				 			    }	
				 			}	   
				 		}
				  	}
				 }
				 	
	
				 	addMemberForm.setWarnExtIds(warnExtIds);
				 	addMemberForm.setChkdExtIds(chkdExtList);
				 	addMemberForm.setChkdIncompExtIds(chkdIncompExtList);
				 	addMemberForm.setIncompWarnExtIds(warnIncompExtIds);
									
					if(isWarnMsg == true)  strForward = "success";
						else  strForward = "confirm"; 
			
				logger.debug("Warning FORWARD : "+strForward);
										
		     }	
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
				ETSUtils.displayError(out, ETSErrorCodes.getErrorCode(se), "Error occurred on AddMultMembersDisplayWarningAction.");

			}
			
		} catch (Exception ex) {
			PrintWriter out = pdResponse.getWriter();
			
			if (ex != null) {
				ex.printStackTrace();
				if (logger.isErrorEnabled()) {
	            	logger.error(this,ex);
	            }
				ETSUtils.displayError(out, ETSErrorCodes.getErrorCode(ex), "Error occurred on AddMultMembersDisplayWarningAction.");

			}
			
		} finally {

			if (conn != null)
				conn.close();
			conn = null;
		}
		return pdMapping.findForward(strForward);
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
