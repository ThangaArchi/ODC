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
package oem.edge.ets.fe.ubp;

import java.sql.SQLException;
import java.util.Vector;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.EntitledStatic;
import oem.edge.decaf.DecafMailCommon;
import oem.edge.decaf.DecafUserInfoServlet;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSBoardingUtils;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSParams;
import oem.edge.ets.fe.ETSProperties;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.ETSUtils;

/**
 * @author v2ravik
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ETSUserAccessFunctions {

    public ETSUserAccessFunctions (){
        this.etsParms = new ETSParams();
        this.errMsg = "";
        this.goPage="public";
        this.ibmId = "";
    }
    public void setETSParms (ETSParams etsParms){
        this.etsParms = etsParms;
        ibmId = this.etsParms.getEdgeAccessCntrl().gIR_USERN;
    }

    public String processUserId() throws SQLException{
        // 1. get the ibmid and passwd of the user
        // 2. check if the use is in amt else ir else set the err a message
        // 3. direct the flow the appropriate page
                ETSSyncUser syncUser = new ETSSyncUser(); //uid+passwd is ok
                syncUser.setWebId(ibmId); // get the user details into system
                ETSStatus etsStatus= syncUser.syncUser(etsParms.getConnection());

                ETSUserDetails usrDetails = new ETSUserDetails();
                usrDetails.setWebId(ibmId);
                usrDetails.extractUserDetails(etsParms.getConnection());

                if (usrDetails.isUserExists()){
                    if (usrDetails.getUserType() == usrDetails.USER_TYPE_EXTERNAL){
                        // external user
                        if (usrDetails.getEMail().endsWith(".ibm.com")) {
                            // user is external but - email says - internal
                            this.setGoPage("flaggedemail");
                        }else {
                            if (usrDetails.getStreetAddress().equals("")){
                                // street address is not valid  - ask for profile update
                                this.setGoPage("profileupdate");
                            } else {
                                // street address valid  - collect wkspc/pm email address
                                this.setGoPage("getextwkspc");
                            }
                        }
                    }
                    if (usrDetails.getUserType() == usrDetails.USER_TYPE_INTERNAL_PENDING_VALIDATION){
                        // internal user // show validation page
                        this.setGoPage("valpend");
                    }
                    if (usrDetails.getUserType() == usrDetails.USER_TYPE_INTERNAL){
                        // internal user  // show roles page
                        this.setGoPage("roleaccess");
                    }
                    if (usrDetails.getUserType() == usrDetails.USER_TYPE_INVALID){
                        // invalid user
                        this.setErrMsg("There is a problem with your userid. Please contact the administrator : etsadmin@us.ibm.com");
                        this.setGoPage("public");
                    }
                } else {
                    // not in AMT/DECAF
                }
        return (this.getGoPage());         // return pages
    }
    public String processRole(){
        String role = etsParms.getRequest().getParameter("role");

        if (role.equals("etsclient")) this.setGoPage("etsclient");
        if (role.equals("principals")) this.setGoPage("principals");
        if (role.equals("etspm")) this.setGoPage("etspm");
        if (role.equals("execs")) {processExecs();this.setGoPage("reqsuccess");} //TODO just request role and direct the user to the success page
        if (role.equals("ccadvocate")) this.setGoPage("ccadvocate");
        if (role.equals("other")) this.setGoPage("etsclient");

        return this.getGoPage();
    }

    public String processExecs(){
        // just request ETS entitlement - and show success
        ETSUserAccessRequest execReq = new ETSUserAccessRequest();
        execReq.setDecafEntitlement(Defines.ETS_EXECUTIVE_ENTITLEMENT);
        execReq.setIsAProject(false);
        execReq.setUserCompany("IBM");
        execReq.setPmIrId(etsParms.getEdgeAccessCntrl().gIR_USERN);
        execReq.setTmIrId(etsParms.getEdgeAccessCntrl().gIR_USERN);
        execReq.request(etsParms.getConnection()); // entitlement requested
       // since the requestor is an ibmer - it will go to the manager automatically
        this.setErrMsg("and it is pending with your manager. Please follow up with them to expedite the approval process.");
        this.setGoPage("reqsuccess");
        return this.getGoPage();
    }
//6.3.1 Commented by Vishal ... This method is not called from anywhere in the code
// Need to comment ..due to the conflict of ETSBoardingUtils.sendManagersEmail()    
/*    public String processWkspcRequest() throws Exception,SQLException{
        // if both wkspc and pm email is blank -
        // retrun the user to add both
        String ibmId = this.etsParms.getEdgeAccessCntrl().gIR_USERN;
        String wkSpc = getParameter(etsParms,"wkspc");
        String reqComments = getParameter(etsParms,"comments");

        String pmEmail = getParameter(etsParms,"pmemail");

        ETSUserDetails usrDetails = new ETSUserDetails();
        usrDetails.setWebId(ibmId);
        usrDetails.extractUserDetails(etsParms.getConnection());

        String option = etsParms.getRequest().getParameter("option");
        if (option.equals("chkwkspc")){
            // check if both wkspc and pm email are good
            if (wkSpc.trim().equals("")||pmEmail.trim().equals("")){
                // error - return
                this.setErrMsg("Both the fields are mandatory");
                this.setGoPage("getextwkspc");
            } else {
                // first put the request - email is a kind of optional
                ETSUserAccessRequest accessReq = new ETSUserAccessRequest();
                if (isValidEmail(pmEmail)){
                    // if there is any email in the system
                    if (isWkspcOwner(pmEmail)||isWkspcManager(pmEmail)){
                        accessReq.recordRequestWithComments(etsParms.getConnection(),ibmId,wkSpc,pmEmail,ibmId,reqComments,Defines.ETS_WORKSPACE_TYPE);
                        ETSBoardingUtils.sendManagerEMail(usrDetails,pmEmail,wkSpc,reqComments);
                    } else {
                        if (isTeamMbr(pmEmail)){
                            accessReq.recordRequestWithComments(etsParms.getConnection(),ibmId,wkSpc,pmEmail,ibmId,reqComments,Defines.ETS_WORKSPACE_TYPE);
                            ETSBoardingUtils.sendMemberEMail(usrDetails,pmEmail,wkSpc,reqComments);
                        } else {
                            // the id is valid - but not in ETS
                            // send it to etsadmin
                            ETSBoardingUtils.sendAdminEMail(usrDetails,wkSpc,pmEmail,reqComments);
                        }
                    }
                } else {
                    // if nothing is valid  - pm emai is not found
                    // send the request email to ets-admin
                    ETSBoardingUtils.sendAdminEMail(usrDetails,wkSpc,pmEmail,reqComments);
                    // why cant ets admin fwd the request - so that the user doent need to request again ?
                    //accessReq.recordRequestWithComments(etsParms.getConnection(),ibmId,wkSpc,"etsadmin@us.ibm.com",ibmId,reqComments);
                }
                this.setGoPage("reqsuccess");
            }
        } else {
            String companies = getParameter(etsParms,"companies");
            String projname = getParameter(etsParms,"projname");
            String projemail = getParameter(etsParms,"projemail");
            if (wkSpc.equals("select")){
                // only email to ets admin
                System.out.println(companies+":"+projname+":"+projemail);
                // based on the option... choose the email.. no reques
//                if (isValidEmail(projemail)){
//                    ETSBoardingUtils.sendAdminEMail(usrDetails,"New workspace creation request: see the comments below",pmEmail,sBuff.toString());
//                    this.setErrMsg(" to "+pmEmail);
//                    this.setGoPage("reqsuccess");
//                } else {
                if (!companies.equals("")&&!projemail.equals("")&&!projname.equals("")){
                    String wkspcType = "";
                    if (option.equals("principal")) wkspcType = "Proposal";
                    if (option.equals("etspm")) wkspcType = "Project ";
                    if (option.equals("ccadvocate")) wkspcType = "Client Care";
                    StringBuffer sBuff = new StringBuffer();
                    sBuff.append("\n").append("The requestor provided the following information about the new workspace:");
                    sBuff.append("\n").append("Workspace type:").append(wkspcType).append("\n");
                    sBuff.append("Requested workspace owner's email:").append(projemail).append("\n");
                    sBuff.append("Requested workspace name:").append(projname).append("\n");
                    sBuff.append("Companies that will be working in this workspace:").append(companies).append("\n");

                    ETSBoardingUtils.sendAdminWkspcEMail(usrDetails,projname,sBuff.toString(),reqComments);
                    pmEmail = "etsadmin@us.ibm.com";
                    this.setErrMsg(this.getErrMsg()+"The new project creation request is submitted to "+pmEmail+".You will be contacted when it is complete.");
                } else {
                    this.setErrMsg(this.getErrMsg()+"<b><span style=\"color:#ff3333\">The new project creation request is not submitted, since all the fields are necessary to sucessfully create a project. Please review the details and resubmit the request.<span></b>");
                }
                    this.setGoPage("reqsuccess");
//                }
            } else {
                // internal user request - so get the pm for the workspace
                if (companies.equals("")&&projemail.equals("")&&projname.equals("")){
                Vector owners = ETSDatabaseManager.getUsersByProjectPriv(wkSpc, Defines.OWNER, etsParms.getConnection());
                ETSUser owner = (ETSUser) owners.elementAt(0);
                pmEmail  = ETSUtils.getUserEmail(etsParms.getConnection(),owner.getUserId());
                System.out.println("Owner:"+pmEmail);
                wkSpc = AmtCommonUtils.getValue(etsParms.getConnection(),"select project_name from ets.ets_projects where project_id = '"+wkSpc+"'");
                    ETSUserAccessRequest accessReq = new ETSUserAccessRequest();
                    accessReq.recordRequestWithComments(etsParms.getConnection(),ibmId,wkSpc,pmEmail,ibmId,reqComments,Defines.ETS_WORKSPACE_TYPE);
                    ETSBoardingUtils.sendManagerEMail(usrDetails,pmEmail,wkSpc,reqComments);
                    this.setErrMsg("Your request for access has been submitted for processing to "+pmEmail+". You will be contacted when it is complete.");
                } else {
                    if (!companies.equals("")&&!projemail.equals("")&&!projname.equals("")){
                        // request the wkspc
                        Vector owners = ETSDatabaseManager.getUsersByProjectPriv(wkSpc, Defines.OWNER, etsParms.getConnection());
                        ETSUser owner = (ETSUser) owners.elementAt(0);
                        pmEmail  = ETSUtils.getUserEmail(etsParms.getConnection(),owner.getUserId());
                        System.out.println("Owner:"+pmEmail);
                        wkSpc = AmtCommonUtils.getValue(etsParms.getConnection(),"select project_name from ets.ets_projects where project_id = '"+wkSpc+"'");
                ETSUserAccessRequest accessReq = new ETSUserAccessRequest();
                accessReq.recordRequestWithComments(etsParms.getConnection(),ibmId,wkSpc,pmEmail,ibmId,reqComments,Defines.ETS_WORKSPACE_TYPE);
                ETSBoardingUtils.sendManagerEMail(usrDetails,pmEmail,wkSpc,reqComments);
                        this.setErrMsg("Your request for access has been submitted for processing to "+pmEmail+". You will be contacted when it is complete.");

                        // ....and new project
                        String wkspcType = "";
                        if (option.equals("principal")) wkspcType = "Proposal";
                        if (option.equals("etspm")) wkspcType = "Project ";
                        if (option.equals("ccadvocate")) wkspcType = "Client Care";
                        StringBuffer sBuff = new StringBuffer();
                        sBuff.append("\n").append("The requestor provided the following information about the new workspace:");
                        sBuff.append("\n").append("Workspace type:").append(wkspcType).append("\n");
                        sBuff.append("Requested workspace owner's email:").append(projemail).append("\n");
                        sBuff.append("Requested workspace name:").append(projname).append("\n");
                        sBuff.append("Companies that will be working in this workspace:").append(companies).append("\n");
                        pmEmail = "etsadmin@us.ibm.com";
                        this.setErrMsg(this.getErrMsg()+"<br /><br /><br />The new project creation request is submitted to "+pmEmail+".You will be contacted when it is complete.");
                        ETSBoardingUtils.sendAdminWkspcEMail(usrDetails,projname,sBuff.toString(),reqComments);
                    } else {
                        this.setErrMsg(this.getErrMsg()+"<b><span style=\"color:#ff3333\">The new project creation request is not submitted, since all the fields are necessary to sucessfully create a project. Please review the details and resubmit the request.<span></b>");
                    }
                }
                this.setGoPage("reqsuccess");
            }
        }
        return this.getGoPage();
    }
*/
    public String processDiv() throws SQLException, Exception{

        String division=getParameter(etsParms,"division");
        String userid = etsParms.getEdgeAccessCntrl().gUSERN;

        String sQuery = "update decaf.users set user_type='I',assoc_company='"+division+"' where userid='" + userid + "' and user_type='T' and application_id='APPLN10001'";
        String sSpecsQry = "update amt.users set user_type='I' ,future2='decaf' where edge_userId='" + userid + "'";
        EntitledStatic.fireUpdate(etsParms.getConnection(),sQuery);
        EntitledStatic.fireUpdate(etsParms.getConnection(),sSpecsQry);

        // update if user is a manager
          String value = EntitledStatic.getValue(etsParms.getConnection(),"select distinct 1 from decaf.users_bluepages_info where mgr_email_id=(select email_id from decaf.users where userid='"+userid+"')");
            if (value.equals("1")){
                EntitledStatic.fireUpdate(etsParms.getConnection(),"update decaf.users set poc_id=(select decaf_id from decaf.users where userid='"+userid+"'),poc_mail=(select email_Id from decaf.users where userid='"+userid+"') where userid in (select edge_user_Id from decaf.users_bluepages_info where mgr_email_id=(select email_id from decaf.users where userid='"+userid+"') ) and poc_id=''");
            }
        this.setGoPage("roleaccess");
        return this.getGoPage();
    }

    public String processValidation()throws SQLException, Exception{

        String opt = getParameter(etsParms,"valoption");
        String usrcode = getParameter(etsParms,"valcode");

        DecafUserInfoServlet infoServ = new DecafUserInfoServlet();
        String code = infoServ.getCode(etsParms.getConnection(),etsParms.getEdgeAccessCntrl());
        this.setGoPage("valpend");
        if (opt.equals("code")){
            // if success
            if (usrcode.trim().equals(code.toString().trim())){
                // the code is equal - show the info screen
                this.setGoPage("valok");

            } else {
                if (infoServ.getAttempts(etsParms.getConnection(), etsParms.getEdgeAccessCntrl())>3){
                    this.setErrMsg("<span style=\"color:red\"><li>You tried enter the worng code more then 3 times. please contact the MD Help Desk</li></span>");
                    this.setGoPage("valpend");
                } else {
                    EntitledStatic.fireUpdate(etsParms.getConnection(),"update decaf.ibm_code_int set attempts = "+(infoServ.getAttempts(etsParms.getConnection(), etsParms.getEdgeAccessCntrl())+1)+" where userid='"+etsParms.getEdgeAccessCntrl().gUSERN+"'");
                    this.setErrMsg("<span style=\"color:red\"><li>invalid validation code. please check the validation code and try again</li></span>");
                    this.setGoPage("valpend");
                }
            }
        }
        if (opt.equals("email")){
            // send email to the user
            // mail the code to the user - use decaf mail
            String msg = "Userid :\t" + etsParms.getEdgeAccessCntrl().gIR_USERN + "\nIBM CODE :\t" + code;
            DecafMailCommon.sendMailToNewUser(etsParms.getConnection(), etsParms.getEdgeAccessCntrl().gEMAIL, msg);

            this.setGoPage("valemailok");
        }
        return this.getGoPage();
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
    private boolean isValidEmail(String email) throws SQLException, Exception{
        String val=EntitledStatic.getValue(etsParms.getConnection(),"select distinct 1 from amt.users where user_email = '"+email+"' with ur");
        if (val!=null && val.equals("1")){
            return true;
        } else{
            return false;
        }
    }
    private boolean isWkspcOwner(String woEmail)throws SQLException, Exception{
        return this.userHasRole(woEmail,"Workspace Owner");
    }
    private boolean isWkspcManager(String wmEmail)throws SQLException, Exception{
        return this.userHasRole(wmEmail,"Workspace Manager");
    }
    private boolean isTeamMbr(String teamMbr)throws SQLException, Exception{
        return this.userHasRole(teamMbr,"Member");
    }
    public boolean userHasRole(String emailid, String role) throws SQLException, Exception{
        String userid = EntitledStatic.getValue(etsParms.getConnection(),"select a.ir_userid from amt.users a, ets.ets_users b where a.user_email = '"+emailid+"' and a.ir_userid = b.user_id order by a.ir_userid fetch first 1 row only with ur");
        if (userid!=null){
            // user is in ETS
            String val=EntitledStatic.getValue(etsParms.getConnection(),"select 1 from ets.ets_users a, ets.ets_roles b where a.user_id='"+userid+"' and a.user_role_id=b.role_id and b.role_name='"+role+"' fetch first 1 row only");
            if (val!=null && val.equals("1")){ return true;} else{return false;}
        } else {
            // user not in ETS
            return false;
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

}
