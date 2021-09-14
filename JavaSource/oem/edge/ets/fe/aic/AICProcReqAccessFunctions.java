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
 * Created on Jun 18, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ets.fe.aic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

import oem.edge.amt.AMTException;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.EntitledStatic;
import oem.edge.amt.Metrics;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcTeamUtils;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.ubp.ETSStatus;
import oem.edge.ets.fe.ubp.ETSUserAccessRequest;
import oem.edge.ets.fe.ubp.ETSUserDetails;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.ETSBoardingUtils;
import oem.edge.ets.fe.ETSParams;
import oem.edge.ets.fe.ETSProcReqAccessFunctions;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.ETSAdminServlet;
import oem.edge.ets.fe.ETSAccessRequest;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSAccessRequestHome;
import oem.edge.ets.fe.ETSProj;

import org.apache.commons.logging.Log;

/**
 * @author v2ravik
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AICProcReqAccessFunctions extends ETSProcReqAccessFunctions {
	
	public static final String VERSION = "1.1";
	private static Log logger = EtsLogger.getLogger(AICProcReqAccessFunctions.class);

    	public AICProcReqAccessFunctions (){
    	   super();
		   this.etsParms = new ETSParams();
		   this.errMsg = "";
		   this.goPage="public";
		   this.ibmId = "";
		   if(!Global.loaded) {
  			   Global.Init();
			   webRoot=Global.server;
		  }
  				   Global.println("web root=="+webRoot);
    	}
    	
	private ETSParams etsParms;

	public void setETSParms (ETSParams etsParms){ 
		this.etsParms = etsParms; 
		ibmId = this.etsParms.getEdgeAccessCntrl().gIR_USERN;

		}
	public String showReq(){this.setGoPage("showreq");return this.getGoPage();}

	public void sendAddressEmail(String from, String to, String cc,String mailhost, String projectName, String woEmail)throws SQLException,Exception{
		sendAddressEmail(from, to, cc,mailhost, projectName, woEmail,"");
	}
	public void sendAddressEmail(String from, String to, String cc,String mailhost, String projectName, String woEmail, String comments) throws SQLException,Exception{

		StringBuffer sBuff = new StringBuffer();
		// TODO ... urls?
		ResourceBundle resBundle = ResourceBundle.getBundle("oem.edge.common.gwa");
		String changeProfileURL = resBundle.getString("changeProfileURL");
		changeProfileURL = changeProfileURL.substring(0,changeProfileURL.indexOf("&okurl"));
        
		
		UnbrandedProperties unBrandedprop = WrkSpcTeamUtils.getBrandPropsFromProjName(projectName);
		       

		sBuff.append("Hello, you have been invited to join an "+unBrandedprop.getAppName()+" workspace.").append("\n");;
		sBuff.append("\n");
		sBuff.append("At "+unBrandedprop.getAppName()+", clients of IBM "+unBrandedprop.getBrandExpsn()+" ("+unBrandedprop.getBrandAbrvn()+") and IBM team members collaborate  on proposals and project information. "+unBrandedprop.getAppName()+" provides a secure workspace and comprehensive suite of on demand tools that are available online, 24/7.").append("\n");
		sBuff.append("A member has tried to add you to a workspace, but your address in the IBM user profile is empty or is not valid. You must update your profile to continue the process.").append("\n");
		sBuff.append("\n").append("1.Click Change Profile link below and update your IBM profile.").append("\n");
		sBuff.append(changeProfileURL).append("\n");
		sBuff.append("\n").append("2.Click Request Access link below. Follow the instructions on the Web page, entering the information below when prompted.").append("\n");
		sBuff.append(webRoot+Defines.SERVLET_PATH+"AICUserAccessServlet.wss").append("\n");
		sBuff.append("\n").append("\n");
		sBuff.append("Workspace:          "+projectName).append("\n");
		sBuff.append("IBM contact e-mail: "+woEmail).append("\n");
		sBuff.append("\n");
		if (!comments.equals("")){
			sBuff.append("Addidtional comments from the invitee:\n");
			sBuff.append(comments).append("\n");
		}

		System.out.println(sBuff.toString());
		try {
			ETSUtils.sendEMail(from,to,"",mailhost,sBuff.toString(),"IBM "+unBrandedprop.getAppName()+" access is on hold due to invalid address",woEmail);
		} catch (Exception eX){
			eX.printStackTrace();
		}
	}
	public void sendValidateEmail(String from, String to, String cc,String mailhost, String projectName, String woEmail) throws SQLException,Exception{
		sendValidateEmail(from, to, cc,mailhost, projectName,woEmail,"");
	}
	public void sendValidateEmail(String from, String to, String cc,String mailhost, String projectName, String woEmail, String comments) throws SQLException,Exception{

		StringBuffer sBuff = new StringBuffer();
		// TODO ... urls?
		ResourceBundle resBundle = ResourceBundle.getBundle("oem.edge.common.gwa");
		String changeProfileURL = resBundle.getString("changeProfileURL");
		changeProfileURL = changeProfileURL.substring(0,changeProfileURL.indexOf("&okurl"));
        		
		UnbrandedProperties unBrandedprop = WrkSpcTeamUtils.getBrandPropsFromProjName(projectName);
        

		sBuff.append("Hello, you have been invited to join an "+unBrandedprop.getAppName()+" workspace.").append("\n");;
		sBuff.append("\n");
		sBuff.append("At "+unBrandedprop.getAppName()+", clients of IBM "+unBrandedprop.getBrandExpsn()+" ("+unBrandedprop.getBrandAbrvn()+") and IBM team members collaborate  on proposals and project information. "+unBrandedprop.getAppName()+" provides a secure workspace and comprehensive suite of on demand tools that are available online, 24/7.").append("\n");
		sBuff.append("A member has tried to add you to a workspace, but your IBM ID has not been validated. To join the workspace, please complete these steps:").append("\n");
		sBuff.append("\n").append("1.Check your e-mail for the validation code that we have sent to your e-mail address. .").append("\n");
		sBuff.append("\n").append("2.Click Request Access link below.").append("\n");
		sBuff.append(webRoot+Defines.SERVLET_PATH+"AICUserAccessServlet.wss").append("\n");
		sBuff.append("\n").append("3.Log in and enter the validation code from your e-mail into the pop-up window.").append("\n");
		sBuff.append("\n").append("4.Follow the instructions on the Request Access Web page, entering the information below when prompted.").append("\n");
		sBuff.append("\n").append("\n");
		sBuff.append("Workspace:          "+projectName).append("\n");
		sBuff.append("\n").append("\n");
		sBuff.append("If you experience problems with any of these steps, please send a message to aicadmin@us.ibm.com, requesting validation of your IBM ID.");
		sBuff.append("\n");
		if (!comments.equals("")){
			sBuff.append("Addidtional comments from the invitee:\n");
			sBuff.append(comments).append("\n");
		}

		System.out.println(sBuff.toString());
		try {
			ETSUtils.sendEMail(from,to,"",mailhost,sBuff.toString(),"IBM "+unBrandedprop.getAppName()+" access is on hold due to IBM ID validation",woEmail);
		} catch (Exception eX){
			eX.printStackTrace();
		}
	}


	public String getStatus(String requestId, Connection conn) throws SQLException, AMTException, Exception {
		String status = "";
		String qry = "select value(action_by,'BLANK') from ets.ets_access_req where request_id = " + requestId + " and status = 'PENDING' and fwd_by is null  with ur";
		String val = EntitledStatic.getValue(conn, qry);
		if (val.equals("BLANK")) {
			status = "New Request";
			return status;
		}
		qry = "select (select b.user_fname||' '||b.user_lname from amt.users b where ir_userid = action_by) from ets.ets_access_req where request_id = " + requestId + " and status = 'PENDING' and fwd_by is not null  with ur";
		val = EntitledStatic.getValue(conn, qry);
		if (!val.equals("")) {
			status = "Forwarded by " + val;
			return status;
		}
		if (status.equals("")) { // no status till now and the request is not PENDING
			String uid = EntitledStatic.getValue(conn, "select user_id from ets.ets_access_req where request_id = " + requestId+"  with ur");
			String projName = EntitledStatic.getValue(conn, "select PROJECT_NAME from ets.ets_access_req where request_id = " + requestId+"  with ur");
			//check with laxman
			//			ETSProj etsProj = ETSDatabaseManager.getProjectDetsFromProjName(projName);			
			//			UnbrandedProperties unBrandedprop = WrkSpcTeamUtils.getBrandPropsFromProjName(projName);
			//			String reqstdEntitlement = WrkSpcTeamUtils.getWrkSpcReqEntitlement(etsProj);
			//			logger.debug("reqstdEntitlement entitlement in getStatus=== " + reqstdEntitlement);
			//
			//			String reqstdProject = WrkSpcTeamUtils.getWrkSpcReqProject(etsProj);
			//			logger.debug("reqstdProject in getStatus===" + reqstdProject);
			//        
			ETSUserDetails usrDetails = new ETSUserDetails();
			usrDetails.setWebId(uid);
			usrDetails.extractUserDetails(conn);
			// boolean hasEntitlement = ETSAdminServlet.userHasEntitlement(conn,usrDetails.getEdgeId(), reqstdEntitlement);
			//boolean hasPendEntitlement = userHasPendEntitlement(conn,usrDetails.getEdgeId(), reqstdProject);
			boolean hasEntitlement = ETSAdminServlet.userHasEntitlement(conn, usrDetails.getEdgeId(), Defines.AIC_ENTITLEMENT);
			boolean hasITAREntitlement = ETSAdminServlet.userHasEntitlement(conn, usrDetails.getEdgeId(), Defines.ITAR_ENTITLEMENT);
			boolean hasPendEntitlement = !hasEntitlement && userHasPendEntitlement(conn, usrDetails.getEdgeId(), Defines.AIC_ENTITLEMENT);
			boolean hasPendITAREntitlement = !hasITAREntitlement && userHasPendEntitlement(conn, usrDetails.getEdgeId(), Defines.ITAR_PROJECT);
			if ((hasEntitlement && !hasPendITAREntitlement) || (hasITAREntitlement && !hasPendEntitlement)) {
				// the request is approved and the user has entitlement - so no need to show the request
				status = "hasEntitlement";
			} else {
				if (hasPendEntitlement || hasPendITAREntitlement) {
					String mgr = ETSUtils.getUsersNameFromEdgeId(conn, EntitledStatic.getValue(conn, "select userid from decaf.users where decaf_id = '" + usrDetails.getPocId() + "'  with ur"));
					if (mgr == null || mgr.equals("")) {
						if (usrDetails.getUserType() == usrDetails.USER_TYPE_EXTERNAL) {
							mgr = "your P.O.C";
						} else {
							mgr = "your Manager";
						}
					}
					status = "Pending with " + mgr;
				} else {
					// no pending entitlement neither entitlement
					//so in the the process
					status = "processing";
				}
			}
		}
		return status;
	}

	private String goPage;
	private String errMsg;
	private String webRoot;
	private String ibmId ;
	public String getGoPage(){return this.goPage;}
	public void setGoPage(String goPage){this.goPage = goPage;   }
	public String getErrMsg(){return this.errMsg;}
	private void setErrMsg(String errMsg){this.errMsg = errMsg;   }

}
