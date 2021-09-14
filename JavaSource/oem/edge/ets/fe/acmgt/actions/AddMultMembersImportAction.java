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

import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.Global;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSErrorCodes;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUtils;
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
public class AddMultMembersImportAction extends BaseAcmgtAction {
	
	private static Log logger = EtsLogger.getLogger(AddMultMembersImportAction.class);
	public static final String VERSION = "1.3";

	
	public AddMultMembersImportAction() {
		super();
	}

	/**
	 * @see oem.edge.ets.fe.acmgt.actions.BaseAcmgtAction#executeAction(
	 * org.apache.struts.action.ActionMapping,
	 * org.apache.struts.action.ActionForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	protected ActionForward executeAction(
		ActionMapping pdMapping,
		ActionForm pdForm,
		HttpServletRequest pdRequest,
		HttpServletResponse pdResponse)
		throws Exception {
		
		BaseAddMemberForm addMemberForm = (BaseAddMemberForm) pdForm; 	
		
		String strForward = "";
		ActionErrors pdErrors = null;
		
		EdgeAccessCntrl es = new EdgeAccessCntrl();
		
		Hashtable params = new Hashtable(); 
		Connection conn = null;
						
		try {

			if (!Global.loaded) {
				Global.Init();
			}
			//get connection
			conn = ETSDBUtils.getConnection();
			
			if (es.GetProfile(pdResponse, pdRequest)) {
									
				String fwd = AmtCommonUtils.getTrimStr(pdRequest.getParameter("fwd"));
				
				String sLink = "";
				String projectidStr = "";
				String topCatId = "";
				
				// get linkid, proj, tc 
				sLink = AmtCommonUtils.getTrimStr(pdRequest.getParameter("linkid"));
				projectidStr = AmtCommonUtils.getTrimStr(pdRequest.getParameter("proj"));
				topCatId = AmtCommonUtils.getTrimStr(pdRequest.getParameter("tc"));
				
				if(StringUtil.isNullorEmpty(sLink)) sLink = addMemberForm.getLinkid(); 
				if(StringUtil.isNullorEmpty(projectidStr)) projectidStr = addMemberForm.getProj(); 
				if(StringUtil.isNullorEmpty(topCatId)) topCatId = addMemberForm.getTc();
																											
				ETSProj proj = ETSDatabaseManager.getProjectDetails(conn, projectidStr);
					
				//get params
				params = AmtCommonUtils.getServletParameters(pdRequest);
				
				if(fwd.equals("importResult")){
					 strForward = "importResult";
				}else{
					strForward = "success";
				}
				
			} // if (es.GetProfile(pdResponse, pdRequest))
			
			logger.debug("AddMultMembersImportAction :: FORWARD " + strForward);
									
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
				ETSUtils.displayError(out, ETSErrorCodes.getErrorCode(se), "Error occurred on AddMultMembersImportAction.");

			}
			
		} catch (Exception ex) {
			PrintWriter out = pdResponse.getWriter();
			
			if (ex != null) {
				ex.printStackTrace();
				if (logger.isErrorEnabled()) {
	            	logger.error(this,ex);
	            }
				ETSUtils.displayError(out, ETSErrorCodes.getErrorCode(ex), "Error occurred on AddMultMembersImportAction.");

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
