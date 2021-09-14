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


package oem.edge.ets.fe;

import java.util.Enumeration;
import java.util.ResourceBundle;

import oem.edge.common.SysLog;

/*****************************************************************************/
/*	Date Created	: Feb 18, 2004												 */
/*  File name		: ETSProperties.java												 */
/*****************************************************************************/

/**
 * @author 		: v2sathis
 */


public class ETSProperties {


	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.13";

    boolean bShowHelp = false;


    String sMessageSuccess = "";
    String sMessageFailure = "";
    String sMessageUpdate = "";
    String sMessageCancel = "";

    String sMeetingSuccess = "";
    String sMeetingFailure = "";
    String sMeetingUpdate = "";
    String sMeetingCancel = "";

    String sEventSuccess = "";
    String sEventFailure = "";
    String sEventUpdate = "";
    String sEventCancel = "";

    String sFeedDefault = "";
    String sFeedSuccess = "";
    String sFeedFailure = "";
    String sFeedUpdate = "";

    String sMyFeedbackDefalut = "";
    String sTeamFeedbackDefalut = "";

    String sFeedbackProcessing = "";

    String sApprovalDefault = "";

    String AdminEmail = "";

    String change_password_url = "";
    String forgot_id_url = "";
    String forgot_password_url = "";
    String self_register_url = "";
    
    String sContactUsURL = "";
    
    String sHelpURL = "";
    
	String POCContactEmail = "";
	
	String ITARExternalHelp = "";
	String ITARInternalHelp = "";
	
	String ITARExternalLink1= "";
	String ITARExternalLink2= "";
	String ITARExternalLink3= "";

	String ITARInternalLink1= "";
	String ITARInternalLink2= "";
	String ITARInternalLink3= "";
	String ITARInternalLink4 = "";


	String InternalHelpLink = "";
	String ExternalHelpLink = "";
	
    public ETSProperties() {
        loadProperties();
    }


    void loadProperties() {

        try {

            ResourceBundle rb = ResourceBundle.getBundle("oem.edge.ets.fe.ets");
            Enumeration e = rb.getKeys();

            String sShowHelp = "";

            try {

                sShowHelp = rb.getString("ets.showhelp").trim();

            } catch (Exception e1) {
                e1.printStackTrace();
                SysLog.log(SysLog.WARNING, "", "*** WARNING *** Property (ets.showhelp) not found on ets.properties. Defaulting to NO" + e1.getMessage());
            }


            if (sShowHelp == null || sShowHelp.trim().equals("")) {
                sShowHelp = "";
            } else {
                sShowHelp = sShowHelp.trim();
            }

            if (sShowHelp.trim().equalsIgnoreCase("Y")) {
                this.bShowHelp = true;
            } else {
                this.bShowHelp = false;
            }

            try {

                this.sMessageSuccess = rb.getString("ets.messsuccess").trim();
                this.sMessageFailure = rb.getString("ets.messfailure").trim();
                this.sMessageUpdate = rb.getString("ets.messupdate").trim();
                this.sMessageCancel = rb.getString("ets.messcancel").trim();

                this.sMeetingSuccess = rb.getString("ets.meetsuccess").trim();
                this.sMeetingFailure = rb.getString("ets.meetfailure").trim();
                this.sMeetingUpdate = rb.getString("ets.meetupdate").trim();
                this.sMeetingCancel = rb.getString("ets.meetcancel").trim();

                this.sEventSuccess = rb.getString("ets.eventsuccess").trim();
                this.sEventFailure = rb.getString("ets.eventfailure").trim();
                this.sEventUpdate = rb.getString("ets.eventupdate").trim();
                this.sEventCancel = rb.getString("ets.eventcancel").trim();

                this.sFeedDefault = rb.getString("ets.feeddefault").trim();
                this.sFeedSuccess = rb.getString("ets.feedsuccess").trim();
                this.sFeedFailure = rb.getString("ets.feedfailure").trim();
                this.sFeedUpdate = rb.getString("ets.feedupdate").trim();

                this.sMyFeedbackDefalut = rb.getString("ets.myfeeddefault").trim();
                this.sTeamFeedbackDefalut = rb.getString("ets.teamfeeddefault").trim();

                this.sFeedbackProcessing = rb.getString("ets.feedprocessing").trim();

                this.sApprovalDefault = rb.getString("ets.approvaldefault").trim();

                this.AdminEmail = rb.getString("ets.admin_email").trim();

                this.change_password_url = rb.getString("ets.change_password_url").trim();
                this.forgot_id_url = rb.getString("ets.forgot_id_url").trim();
                this.forgot_password_url = rb.getString("ets.forgot_password_url").trim();
                this.self_register_url = rb.getString("ets.self_register_url").trim();
                
                this.sContactUsURL = rb.getString("ets.contactus_url").trim();
                
				//this.sHelpURL = rb.getString("ets.help_url").trim();
				
				this.sHelpURL = "some_url_here";

				
				this.POCContactEmail = rb.getString("ets.poc_request_email").trim();
				
				this.ITARExternalHelp = rb.getString("ets.itar_external_help").trim();
				this.ITARExternalLink1 = rb.getString("ets.itar_external_link1").trim();
				this.ITARExternalLink2 = rb.getString("ets.itar_external_link2").trim();
				this.ITARExternalLink3 = rb.getString("ets.itar_external_link3").trim();
				
				this.ITARInternalHelp = rb.getString("ets.itar_internal_help").trim();
				this.ITARInternalLink1 = rb.getString("ets.itar_internal_link1").trim();
				this.ITARInternalLink2 = rb.getString("ets.itar_internal_link2").trim();
				this.ITARInternalLink3 = rb.getString("ets.itar_internal_link3").trim();
				this.ITARInternalLink4 = rb.getString("ets.itar_internal_link4").trim();
				
				this.InternalHelpLink= rb.getString("ets.internal_help_link").trim();
				this.ExternalHelpLink = rb.getString("ets.external_help_link").trim();
				
				

            } catch (Exception e2) {
                e2.printStackTrace();
                SysLog.log(SysLog.ERR, "", "*** ERROR (FATAL) *** Property(s) for default message not found on ets.properties file. Please update/add properties." + e2.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            SysLog.log(SysLog.ERR, "", "*** ERROR (FATAL) *** When reading oem.edge.ets.fe.ets.properties " + e.getMessage());

        }
    }

    public boolean displayHelp() {
        return this.bShowHelp;
    }


	public String getSMeetingFailure() {
		return this.sMeetingFailure;
	}

	public String getSMeetingSuccess() {
		return this.sMeetingSuccess;
	}

	public String getSMeetingUpdate() {
		return this.sMeetingUpdate;
	}

	public String getSMessageFailure() {
		return this.sMessageFailure;
	}

	public String getSMessageSuccess() {
		return this.sMessageSuccess;
	}

	public String getSMessageUpdate() {
		return this.sMessageUpdate;
	}

	public String getSEventFailure() {
		return this.sEventFailure;
	}

	public String getSEventSuccess() {
		return this.sEventSuccess;
	}

	public String getSEventUpdate() {
		return this.sEventUpdate;
	}

	public String getSEventCancel() {
		return this.sEventCancel;
	}

	public String getSMeetingCancel() {
		return this.sMeetingCancel;
	}

	public String getSMessageCancel() {
		return this.sMessageCancel;
	}

	public String getSFeedDefault() {
		return this.sFeedDefault;
	}

	public String getSFeedFailure() {
		return this.sFeedFailure;
	}

	public String getSFeedSuccess() {
		return this.sFeedSuccess;
	}

	public String getSFeedUpdate() {
		return this.sFeedUpdate;
	}

	public String getSMyFeedbackDefalut() {
		return this.sMyFeedbackDefalut;
	}

	public String getSTeamFeedbackDefalut() {
		return this.sTeamFeedbackDefalut;
	}

	public String getSFeedbackProcessing() {
		return this.sFeedbackProcessing;
	}

	public String getSApprovalDefault() {
		return this.sApprovalDefault;
	}

	public String getAdminEmail() {
		return this.AdminEmail;
	}

	public String getChange_password_url() {
		return this.change_password_url;
	}

	public String getForgot_id_url() {
		return this.forgot_id_url;
	}

	public String getForgot_password_url() {
		return this.forgot_password_url;
	}

	public String getSelf_register_url() {
		return this.self_register_url;
	}

	public String getContactUsURL() {
		return this.sContactUsURL;
	}

	public void setContactUsURL(String sContactUsURL) {
		this.sContactUsURL = sContactUsURL;
	}
	
	public String getHelpURL() {
		return this.sHelpURL;
	}

	public void setHelpURL(String HelpURL) {
		this.sHelpURL = HelpURL;
	}
	

	/**
	 * @return
	 */
	public String getPOCContactEmail() {
		return this.POCContactEmail;
	}

	/**
	 * @param string
	 */
	public void setPOCContactEmail(String string) {
		this.POCContactEmail = string;
	}

	/**
	 * @return
	 */
	public String getExternalHelpLink() {
		return this.ExternalHelpLink;
	}

	/**
	 * @return
	 */
	public String getInternalHelpLink() {
		return this.InternalHelpLink;
	}

	/**
	 * @return
	 */
	public String getITARExternalHelp() {
		return this.ITARExternalHelp;
	}

	/**
	 * @return
	 */
	public String getITARExternalLink1() {
		return this.ITARExternalLink1;
	}

	/**
	 * @return
	 */
	public String getITARExternalLink2() {
		return this.ITARExternalLink2;
	}

	/**
	 * @return
	 */
	public String getITARExternalLink3() {
		return this.ITARExternalLink3;
	}

	/**
	 * @return
	 */
	public String getITARInternalHelp() {
		return this.ITARInternalHelp;
	}

	/**
	 * @return
	 */
	public String getITARInternalLink1() {
		return ITARInternalLink1;
	}

	/**
	 * @return
	 */
	public String getITARInternalLink2() {
		return this.ITARInternalLink2;
	}

	/**
	 * @return
	 */
	public String getITARInternalLink3() {
		return this.ITARInternalLink3;
	}

	/**
	 * @return
	 */
	public String getITARInternalLink4() {
		return this.ITARInternalLink4;
	}

	/**
	 * @param string
	 */
	public void setExternalHelpLink(String string) {
		this.ExternalHelpLink = string;
	}

	/**
	 * @param string
	 */
	public void setInternalHelpLink(String string) {
		this.InternalHelpLink = string;
	}

	/**
	 * @param string
	 */
	public void setITARExternalHelp(String string) {
		this.ITARExternalHelp = string;
	}

	/**
	 * @param string
	 */
	public void setITARExternalLink1(String string) {
		this.ITARExternalLink1 = string;
	}

	/**
	 * @param string
	 */
	public void setITARExternalLink2(String string) {
		this.ITARExternalLink2 = string;
	}

	/**
	 * @param string
	 */
	public void setITARExternalLink3(String string) {
		this.ITARExternalLink3 = string;
	}

	/**
	 * @param string
	 */
	public void setITARInternalHelp(String string) {
		this.ITARInternalHelp = string;
	}

	/**
	 * @param string
	 */
	public void setITARInternalLink1(String string) {
		this.ITARInternalLink1 = string;
	}

	/**
	 * @param string
	 */
	public void setITARInternalLink2(String string) {
		this.ITARInternalLink2 = string;
	}

	/**
	 * @param string
	 */
	public void setITARInternalLink3(String string) {
		this.ITARInternalLink3 = string;
	}

	/**
	 * @param string
	 */
	public void setITARInternalLink4(String string) {
		this.ITARInternalLink4 = string;
	}

}
