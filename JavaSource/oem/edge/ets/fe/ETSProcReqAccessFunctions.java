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
package oem.edge.ets.fe;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

import oem.edge.amt.AMTException;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.EntitledStatic;
import oem.edge.amt.Metrics;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.acmgt.dao.AddMembrToWrkSpcDAO;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcTeamUtils;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.ubp.ETSStatus;
import oem.edge.ets.fe.ubp.ETSUserAccessRequest;
import oem.edge.ets.fe.ubp.ETSUserDetails;

import org.apache.commons.logging.Log;

/**
 * @author v2ravik
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ETSProcReqAccessFunctions {
	
	public static final String VERSION = "1.33";
	private static Log logger = EtsLogger.getLogger(ETSProcReqAccessFunctions.class);

    public ETSProcReqAccessFunctions (){
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
    public void setETSParms (ETSParams etsParms) throws SQLException{
        this.etsParms = etsParms;
        ibmId = this.etsParms.getEdgeAccessCntrl().gIR_USERN;
    }

    public String showReq(){this.setGoPage("showreq");return this.getGoPage();}
    public String procReq() throws SQLException, Exception {
        String requestId = etsParms.getRequest().getParameter("requestid");
        String comments = etsParms.getRequest().getParameter("comments");
        String company = etsParms.getRequest().getParameter("company");
        String country = etsParms.getRequest().getParameter("country");
        String wkspc = etsParms.getRequest().getParameter("wkspc");
        String requestor = etsParms.getRequest().getParameter("ibmid");
        String apprAction=etsParms.getRequest().getParameter("approveact");
        
		
		UnbrandedProperties unBrandedprop = WrkSpcTeamUtils.getBrandPropsFromProjId(wkspc);
		

        //System.out.println("Level:"+ETSUtils.checkUserRole(etsParms.getEdgeAccessCntrl(),wkspc));
        ETSAccessRequestHome arh = new ETSAccessRequestHome(etsParms.con);
        ETSUserDetails u = new ETSUserDetails();
        u.setWebId(requestor);
        u.extractUserDetails(etsParms.con);
        String woWebId = this.woWebId(wkspc,etsParms.con);
        String woEmail  = ETSUtils.getUserEmail(etsParms.con,woWebId);
        String projName = EntitledStatic.getValue(etsParms.con,"select project_name from ets.ets_projects where project_id = '"+wkspc+"'  with ur");
        String projType = EntitledStatic.getValue(etsParms.con,"select project_or_proposal from ets.ets_projects where project_id = '"+wkspc+"'  with ur");

        if (apprAction.equals("defer")){
            this.setGoPage("redirect");
        }
        if (apprAction.equals("reject")){
            // reject the request
            if (comments.equals("")){
                this.setErrMsg("<li><b>When rejecting you must type in a message to the user</b></li>");
                this.setGoPage("showreq");
            } else {
                arh.Reject(Integer.parseInt(requestId), etsParms.es.gIR_USERN, comments);
                if (u.isUserExists() && u.getEMail() != null && u.getEMail().length() > 0) {
                    ETSAccessRequest ar  = new ETSAccessRequest();
                    if (projName.equals("")){
                        projName = EntitledStatic.getValue(etsParms.con,"select PROJECT_NAME from ets.ets_access_req where request_id = "+requestId+"  with ur");
                    }
                    ar.setProjName(projName);
                    ar.setMgrEMail(etsParms.es.gEMAIL);
                    boolean sentOK = ETSBoardingUtils.sendRejectEMail(u, ar, projName, etsParms.es.gFIRST_NAME.trim() + " " + etsParms.es.gLAST_NAME.trim(), comments,woEmail);
                }
                this.setErrMsg("<li>Request for user: <b>" + u.getFirstName().trim() + " " + u.getLastName().trim() + " [ " + requestor + " ]</b> has been rejected.</li>");
                this.setGoPage("redirect"); // send the approver back to landing page
            }
        }
        if (apprAction.equals("accept")){
            if (wkspc.equals("select")){
                this.setErrMsg("<li>Select a workspace to accept a request</li>");
                this.setGoPage("showreq");
            } else {
                if (u.getUserType()==u.USER_TYPE_EXTERNAL&&(company.equals("select")||country.equals("select"))){
                    this.setErrMsg("<li>Company and country are mandatory for an external user</li>");
                    this.setGoPage("showreq");
                } else {
                    if (u.isUserExists()){
                    	
                    	logger.debug("PROC REQ :: PROJ ID == "+wkspc);
                		logger.debug("PROC REQ :: USER == "+etsParms.getEdgeAccessCntrl().gIR_USERN);
                		logger.debug("PROC REQ :: USER ROLE == "+ETSUtils.checkUserRole(etsParms.getEdgeAccessCntrl(),wkspc));
                		
                        if (ETSUtils.checkUserRole(etsParms.getEdgeAccessCntrl(),wkspc).equals(Defines.WORKSPACE_OWNER.toString())
                            ||ETSUtils.checkUserRole(etsParms.getEdgeAccessCntrl(),wkspc).equals(Defines.WORKSPACE_MANAGER.toString())
                            ||ETSUtils.checkUserRole(etsParms.getEdgeAccessCntrl(),wkspc).equals(Defines.WORKFLOW_ADMIN.toString())
							||(ETSUtils.checkUserRole(etsParms.getEdgeAccessCntrl(),wkspc).equals(Defines.ETS_ADMIN.toString()) && (ETSDatabaseManager.hasProjectPriv(etsParms.getEdgeAccessCntrl().gIR_USERN, wkspc, Defines.OWNER, etsParms.con)))
	                    ) {
                                if (((ETSUtils.checkUserRole(etsParms.es,wkspc).equals(Defines.WORKSPACE_MANAGER.toString()))&&(u.getUserType()==u.USER_TYPE_EXTERNAL))){
                                      // fwd the request - no email
                                         EntitledStatic.fireUpdate(etsParms.getConnection(),"update ets.ets_access_req set mgr_email='"+woEmail+"', action_by='"+etsParms.getEdgeAccessCntrl().gIR_USERN+"',reply_text='"+comments+"',last_userid='"+etsParms.getEdgeAccessCntrl().gIR_USERN+"', last_timestamp=(current timestamp), fwd_by='"+etsParms.getEdgeAccessCntrl().gIR_USERN+"' where request_id="+requestId);
                                         ETSBoardingUtils.sendFwdEmailToWo(u,projName,etsParms.es.gFIRST_NAME+" "+etsParms.es.gLAST_NAME,comments,etsParms.es.gEMAIL,woEmail);
                                         this.setGoPage("reqfwd");
                                         this.setErrMsg("Since the user you worked on is not an IBM internal user, and you have been identified as the workspace manager of this workspace, so this request was forwareded to "+woEmail+" , who is the owner of this workspace.");
                                } else {
                                    // else show the level page
                                    // check if this is a sub-workspace and the user is a member of parent
                                    ETSProj cProj= ETSDatabaseManager.getProjectDetails(etsParms.con,wkspc);
                                    ETSProj pProj = null;
                                    int count=1; // assume the user a memeber of the parent wkspc
                                    if (!cProj.getParent_id().equals("0")){                 
                                        pProj= ETSDatabaseManager.getProjectDetails(etsParms.con,cProj.getParent_id());
                                        String cnt = EntitledStatic.getValue(etsParms.con,"select count(*) from ets.ets_users where active_flag='A' and user_id = '"+u.getWebId()+"' and user_project_id = '"+cProj.getParent_id()+"'  with ur");
                                        count = Integer.parseInt(cnt);
                                        System.out.println("Count:"+count);
                                    }
                                    if (count < 1){ 
                                        this.setErrMsg("<li><b>This user is not an approved user of the parent workspace, "+pProj.getName()+". Please add the user to the parent workspace and then add the user to this workspace.</b></li>"); 
                                        this.setGoPage("showreq");
                                    } else {
                                         if (ETSDatabaseManager.isUserInProject(u.getWebId(),wkspc, etsParms.con)){    
                                             this.setErrMsg("<li><b>This user is an approved user of this workspace, "+cProj.getName()+". You could either reject the request with an appropriate message to the user or defer the request for later consideration.</b></li>"); 
                                             this.setGoPage("showreq");
                                         } else {
                                             this.setGoPage("wolevel");
                                         }
                                         
                                    }
                                }
                            } else {
                                // if only teammember or any other role- just fwd to wo
                                // show success page - take back to ets connect
                                EntitledStatic.fireUpdate(etsParms.getConnection(),"update ets.ets_access_req set mgr_email='"+woEmail+"', action_by='"+etsParms.getEdgeAccessCntrl().gIR_USERN+"',reply_text='"+comments+"',last_userid='"+etsParms.getEdgeAccessCntrl().gIR_USERN+"', last_timestamp=(current timestamp), fwd_by='"+etsParms.getEdgeAccessCntrl().gIR_USERN+"' where request_id="+requestId);
                                ETSBoardingUtils.sendFwdEmailToWo(u,projName,etsParms.es.gFIRST_NAME+" "+etsParms.es.gLAST_NAME,comments,etsParms.es.gEMAIL,woEmail);
                                this.setGoPage("reqfwd");
    
                                this.setErrMsg("You are not the workspace owner for the workspace you assigned. This request will be forwarded to the workspace owner, "+ETSUtils.getUsersName(etsParms.con, woWebId)+" ["+woWebId+"], for approval.");
                            }
                    } else {
                        this.setGoPage("showreq");
                    }
                }
            }
        }
        return this.getGoPage();
    }
    public String procReq2() throws SQLException, AMTException,Exception {
        String requestId = etsParms.getRequest().getParameter("requestid");
        String comments = "";
        String company = "";
        String country = etsParms.getRequest().getParameter("country");
        String role = etsParms.getRequest().getParameter("role");
        String wkspc = etsParms.getRequest().getParameter("wkspc");
        String requestor = etsParms.getRequest().getParameter("ibmid");
        String jobRep = etsParms.getRequest().getParameter("job");
        if (jobRep == null){jobRep = "";} else {jobRep = jobRep.trim(); }
        
        String sCmpRsn = etsParms.getRequest().getParameter("cmpRsn");
		if (sCmpRsn == null || sCmpRsn.trim().equals("")) {
			sCmpRsn = "";
		} else {
			sCmpRsn = sCmpRsn.trim();
		}

		System.out.println("SPNSPN="+jobRep);
        String apprAction=etsParms.getRequest().getParameter("approveact");

        ETSAccessRequestHome arh = new ETSAccessRequestHome(etsParms.con);
        ETSUserDetails u = new ETSUserDetails();
        u.setWebId(requestor);
        u.extractUserDetails(etsParms.con);
        if (u.getUserType()==u.USER_TYPE_EXTERNAL){
        company = etsParms.getRequest().getParameter("company");
        country = etsParms.getRequest().getParameter("country");
        }

        String userid = u.getEdgeId(); // the requestor
        
        ETSProj Project = ETSDatabaseManager.getProjectDetails(wkspc);
        
		//541 new//
		String reqstdEntitlement = WrkSpcTeamUtils.getWrkSpcReqEntitlement(Project);
		logger.debug("reqstdEntitlement entitlement in ACCESS REQUEST=== " + reqstdEntitlement);

		String reqstdProject = WrkSpcTeamUtils.getWrkSpcReqProject(Project);
		logger.debug("reqstdProject in ACCESS REQUEST===" + reqstdProject);

        String woEmail  = ETSUtils.getUserEmail(etsParms.con,this.woWebId(wkspc,etsParms.con));
        String projName = EntitledStatic.getValue(etsParms.con,"select project_name from ets.ets_projects where project_id = '"+wkspc+"'  with ur");
        String projType = EntitledStatic.getValue(etsParms.con,"select project_or_proposal from ets.ets_projects where project_id = '"+wkspc+"'  with ur");

            if ((ETSUtils.checkUserRole(etsParms.es,wkspc).equals(Defines.WORKSPACE_OWNER))
            		|| (ETSUtils.checkUserRole(etsParms.es,wkspc).equals(Defines.WORKFLOW_ADMIN))
				||((ETSUtils.checkUserRole(etsParms.es,wkspc).equals(Defines.WORKSPACE_MANAGER))&&(u.getUserType()==u.USER_TYPE_INTERNAL))
				||(ETSUtils.checkUserRole(etsParms.getEdgeAccessCntrl(),wkspc).equals(Defines.ETS_ADMIN.toString()) && (ETSDatabaseManager.hasProjectPriv(etsParms.getEdgeAccessCntrl().gIR_USERN, wkspc, Defines.OWNER, etsParms.con)))
            ) {
                // complete the request
                //1. add the member to necessary workspace
                //2. request entitlement if necessary
                // check if this user is in this project already. If yes, ignore this user.
                if (!ETSDatabaseManager.isUserInProjectForDel(requestor, wkspc,etsParms.con)) {

                    boolean bHasEntitlement = ETSAdminServlet.userHasEntitlement(etsParms.con,userid,reqstdEntitlement);
                    boolean bHasPendEntitlement = this.userHasPendEntitlement(etsParms.con,userid,reqstdProject);

                    boolean bRequested = false;
                    boolean bRequestedEnt = false;
                    if (!bHasEntitlement&&!bHasPendEntitlement) {
                        // user does not have entitlement. So request the project for this user.
                        ETSUserAccessRequest uar = new ETSUserAccessRequest();
                        uar.setTmIrId(requestor);
                        uar.setPmIrId(etsParms.es.gIR_USERN);
                        uar.setDecafEntitlement(reqstdProject);
                        uar.setIsAProject(WrkSpcTeamUtils.isReqAccessProject(Project.getProjectType()));
                        if (u.getUserType()==u.USER_TYPE_INTERNAL){
                            uar.setUserCompany(u.getCompany()); // no change of company
                        } else {
                            uar.setUserCompany(company); // set the company seleted by the approver
                        }

                        ETSStatus status = uar.request(etsParms.con);
                        if (status.getErrCode() == 0) {
                            bRequested = true;
                            bRequestedEnt = true;
                        } else {
                            bRequested = false;
                        }
                    } else {
                        bRequested = true;
                    }
                    if (u.getUserType()==u.USER_TYPE_EXTERNAL){
                        EntitledStatic.fireUpdate(etsParms.con,"update decaf.users set country_code = '"+country+"'  where userid = '"+userid+"'");
                        if (!bRequestedEnt){ // if there is an entitlement request - dont update the company
                            EntitledStatic.fireUpdate(etsParms.con,"update decaf.users set assoc_company = '"+company+"'  where userid = '"+userid+"'");
                        }
                    }

                    if (bRequested) { // user has entitlement - just add the user to the project - at the requested role
                        ETSUser user = new ETSUser();
                        user.setUserId(requestor);
                        user.setProjectId(wkspc);
                        user.setUserJob(jobRep);
                        user.setPrimaryContact(Defines.NO);
                        user.setLastUserId(etsParms.es.gIR_USERN);
                        user.setRoleId(Integer.parseInt(role));
                        String[] res = ETSDatabaseManager.addProjectMember(user,etsParms.con);
                        String success = res[0];

                        if (success.equals("0")) {
                            if (u.getUserType()==u.USER_TYPE_EXTERNAL){
                            	
                            	if(! StringUtil.isNullorEmpty(sCmpRsn)){
            	                	int updt = 0;
            	                	String rsn = sCmpRsn.trim();
            	                	updt = AddMembrToWrkSpcDAO.insertReasonToAdminLog(requestor,wkspc,rsn,etsParms.con);
            	                	if(updt == 1) logger.debug("This Reason to ets_admin_log is updated  == " +rsn);
            	                }
                            	
                                if (!company.trim().equals("")) {
                                    EntitledStatic.fireUpdate(etsParms.con,"update decaf.users set assoc_company = '"+company+"'  where userid = '"+u.getEdgeId()+"'");
                                } else {
                                    EntitledStatic.fireUpdate(etsParms.con,"update decaf.users set assoc_company = '"+u.getCompany()+"'  where userid = '"+u.getEdgeId()+"'");
                                }
                                if (!country.trim().equals("")) { // set the country
                                    EntitledStatic.fireUpdate(etsParms.con,"update decaf.users set country_code = '"+country+"'  where userid = '"+u.getEdgeId()+"'");
                                } else {
                                    EntitledStatic.fireUpdate(etsParms.con,"update decaf.users set country_code = '"+u.getCountryCode()+"'  where userid = '"+u.getEdgeId()+"'");
                                }
                            }

                            if (bRequestedEnt){// entitlement requested - so the active flag is "P"
                                EntitledStatic.fireUpdate(etsParms.con,"update ets.ets_users set active_flag = 'P'  where user_id = '"+requestor+"' and user_project_id = '"+wkspc+"'");
                            } else { // "A"
                                if (bHasPendEntitlement){
                                    EntitledStatic.fireUpdate(etsParms.con,"update ets.ets_users set active_flag = 'P'  where user_id = '"+requestor+"' and user_project_id = '"+wkspc+"'");
                                }else {
                                EntitledStatic.fireUpdate(etsParms.con,"update ets.ets_users set active_flag = 'A'  where user_id = '"+requestor+"' and user_project_id = '"+wkspc+"'");
                                }
                            }
                           
                           
							Metrics.appLog(etsParms.con, etsParms.es.gIR_USERN,WrkSpcTeamUtils.getMetricsLogMsg(Project,"Team_Add"));
                           
                            arh.Accept(Integer.parseInt(requestId), etsParms.es.gIR_USERN, comments);

                            // send email
                            u.extractUserDetails(etsParms.con); // regenerate the details
                            ETSAccessRequest ar = new ETSAccessRequest();
                            ar.setProjName(projName);
                            ar.setMgrEMail(etsParms.es.gEMAIL);

                            if (bRequestedEnt) {
                                if (u.getUserType()==u.USER_TYPE_INTERNAL){
                                    this.setErrMsg("<li>The request for <b>" + u.getFirstName().trim() + " " + u.getLastName().trim() + " [ " + requestor + " ]</b> has been forwarded to their manager.</li>");
                                }
                                if (u.getUserType()==u.USER_TYPE_EXTERNAL){
                                    if (etsParms.es.gEMAIL.equals(u.getPocEmail().toString())){
                                        this.setErrMsg("<li>The request for  <b>" + u.getFirstName().trim() + " " + u.getLastName().trim() + " [ " + requestor + " ]</b> is being processed and they will have access to the workspace when all the tables have been updated (approximately 2 hours).</li>");
                                    } else {
                                        this.setErrMsg("<li>The request for <b>" + u.getFirstName().trim() + " " + u.getLastName().trim() + " [ " + requestor + " ]</b> has been forwarded to their point of contact for IBM Customer Connect.</li>");
                                    }
                                }
                                //this.setErrMsg("<li>Request for user: <b>" + u.getFirstName().trim() + " " + u.getLastName().trim() + " [ " + requestor + " ]</b> has been approved successfully. The corresponding entitlement to access E&TS Connect has been requested for the user and is pending with their manager or IBM contact.</li>");
                                boolean sentOK = ETSBoardingUtils.sendAcceptEMailPendEnt(u, ar, projName, etsParms.es.gFIRST_NAME.trim() + " " + etsParms.es.gLAST_NAME.trim(), comments,woEmail);
                                this.setGoPage("acceptok");
                            } else {
                                boolean sentOK = ETSBoardingUtils.sendAcceptEMail(u, ar, projName, etsParms.es.gFIRST_NAME.trim() + " " + etsParms.es.gLAST_NAME.trim(), comments,woEmail);
                                this.setErrMsg("<li>Request for user: <b>" + u.getFirstName().trim() + " " + u.getLastName().trim() + " [ " + requestor + " ]</b> has been processed and they have access to the workspace.</li>");
                                this.setGoPage("acceptok");
                            }
                        } else {
                            SysLog.log(SysLog.ERR, this, "FAILED:  User " + requestor + " could not be added to Project=" + projName);
                            this.setErrMsg("<li>Request for user: <b>" + u.getFirstName().trim() + " " + u.getLastName().trim() + " [ " + requestor + " ]</b> could not be processed. There was an error processing this request.</li>");
                            this.setGoPage("acceptok");
                        }
                    } else {
                        SysLog.log(SysLog.ERR, this, "FAILED:  User " + requestor + " could not be added to Project=" + projName);
                        this.setErrMsg("<li>Request for user: <b>" + u.getFirstName().trim() + " " + u.getLastName().trim() + " [ " + requestor + " ]</b> could not be processed. There was an error processing this request.</li>");
                        this.setGoPage("acceptok");
                    }
                } else {
                    arh.Accept(Integer.parseInt(requestId), etsParms.es.gIR_USERN, comments);
                    ETSAccessRequest ar = new ETSAccessRequest();
                    ar.setProjName(projName);
                    ar.setMgrEMail(etsParms.es.gEMAIL);
                    //??????ERROR??????
                    boolean sentOK = ETSBoardingUtils.sendAcceptEMail(u, ar, projName, etsParms.es.gFIRST_NAME.trim() + " " + etsParms.es.gLAST_NAME.trim(), comments,woEmail);
                    this.setErrMsg("<li>Request for user: <b>" + u.getFirstName().trim() + " " + u.getLastName().trim() + " [ " + requestor + " ]</b> has not been processed because user is already a member of this workspace.</li>");
                    this.setGoPage("acceptok");
                }
            }
        return this.getGoPage();
    } /* proc request */


    public boolean userHasPendEntitlement(Connection conn, String edgeid, String entitlement) throws AMTException, SQLException{
        String qry = "select count(*) from decaf.req_approval_tracking where req_serial_id in (select req_serial_id from decaf.req_approval_details where decaf_id = (select decaf_id from decaf.users where userid = '"
                        + edgeid
                        + "') and access_id = (select project_id from decaf.project where project_name = '"
                        + entitlement
                        + "')) and appr_result in ('P','O') with ur";
        System.out.println(qry);
        String val =
                EntitledStatic.getValue(
                    conn,qry);
        if (Integer.parseInt(val)==0){
            // no pend entitlement
            return false;
        } else {
            // fas pend entitlement
            return true;
        }

    }

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
        sBuff.append(webRoot+Defines.SERVLET_PATH+"ETSUserAccessServlet.wss").append("\n");
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
        sBuff.append(webRoot+Defines.SERVLET_PATH+"ETSUserAccessServlet.wss").append("\n");
        sBuff.append("\n").append("3.Log in and enter the validation code from your e-mail into the pop-up window.").append("\n");
        sBuff.append("\n").append("4.Follow the instructions on the Request Access Web page, entering the information below when prompted.").append("\n");
        sBuff.append("\n").append("\n");
        sBuff.append("Workspace:          "+projectName).append("\n");
        sBuff.append("\n").append("\n");
        sBuff.append("If you experience problems with any of these steps, please send a message to econnect@us.ibm.com, requesting validation of your IBM ID.");
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

    public boolean isWO(String webId, String projectId) throws Exception {
        if (ETSUtils.checkUserRole(etsParms.es, projectId).equals(Defines.WORKSPACE_OWNER)){
            return true;
        } else {
            return false;
        }
    }
    public boolean isWO(EdgeAccessCntrl es, String projectId) throws Exception {
        if (ETSUtils.checkUserRole(es, projectId).equals(Defines.WORKSPACE_OWNER)){
            return true;
        } else {
            return false;
        }
    }
    
	public boolean isSuperAdmin(EdgeAccessCntrl es, String projectId) throws Exception {
			if (ETSUtils.checkUserRole(es, projectId).equals(Defines.ETS_ADMIN)){
				return true;
			} else {
				return false;
			}
		}

    public boolean isWM(EdgeAccessCntrl es, String projectId) throws Exception{
        if (ETSUtils.checkUserRole(es,projectId).equals(Defines.WORKSPACE_MANAGER)){
            return true;
        } else {
            return false;
        }
    }
    public boolean isTM(String webId, String projectId) throws Exception{
        if (ETSUtils.checkUserRole(etsParms.es,projectId).equals(Defines.WORKSPACE_MEMBER)){
            return true;
        } else {
            return false;
        }
    }

    public String woWebId(String projectId, Connection conn) throws SQLException, AMTException{
        String qry = "select a.user_id from ets.ets_users a, ets.ets_roles b where a.user_project_id ='"+projectId+"' and a.user_role_id=b.role_id and a.user_project_id = b.project_id and b.role_name='Workspace Owner' fetch first 1 row only with ur";
        String val="";
        val = EntitledStatic.getValue(conn,qry);
        return val;
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
			boolean hasEntitlement = ETSAdminServlet.userHasEntitlement(conn, usrDetails.getEdgeId(), Defines.ETS_ENTITLEMENT);
			boolean hasITAREntitlement = ETSAdminServlet.userHasEntitlement(conn, usrDetails.getEdgeId(), Defines.ITAR_ENTITLEMENT);
			boolean hasPendEntitlement = !hasEntitlement && userHasPendEntitlement(conn, usrDetails.getEdgeId(), Defines.REQUEST_PROJECT);
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


    /****************************************/
    // private  metohds and variables
    /****************************************/


    private String getParameter(ETSParams etsParms, String key) {
        String value = etsParms.getRequest().getParameter(key);
        if (value == null) {
            return "";
        } else {
            return value;
        }
    }
    public String getGoPage(){return this.goPage;}
    public void setGoPage(String goPage){this.goPage = goPage;   }
    public String getErrMsg(){return this.errMsg;}
    private void setErrMsg(String errMsg){this.errMsg = errMsg;   }

    private String ibmId ;
    private ETSParams etsParms;
    private String goPage;
    private String errMsg;
    private String webRoot;

}
