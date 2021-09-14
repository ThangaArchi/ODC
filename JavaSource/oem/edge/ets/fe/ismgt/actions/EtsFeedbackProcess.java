/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004                                     */
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



package oem.edge.ets.fe.ismgt.actions;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Vector;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.Global;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.ETSContact;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSHeaderFooter;
import oem.edge.ets.fe.ETSMetricsDAO;
import oem.edge.ets.fe.ETSMetricsPrintResults;
import oem.edge.ets.fe.ETSMetricsReports;
import oem.edge.ets.fe.ETSMetricsResultObj;
import oem.edge.ets.fe.ETSParams;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSProperties;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.CommonEmailHelper;
import oem.edge.ets.fe.ismgt.dao.ETSFeedbackDAO;
import oem.edge.ets.fe.ismgt.middleware.ETSMWIssue;
import oem.edge.ets.fe.ismgt.middleware.IssMWProcessor;
import oem.edge.ets.fe.ismgt.middleware.IssMWProcessor_Creator;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.setmet.ETSClientCareContact;

/**
 * @author v2phani
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class EtsFeedbackProcess extends EtsIssChgActionBean {

	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.72";
	private String REPORT_ID = "WS0004";
	private String REPORT_NAME = "Issue Activity Details";



	private String displayFeedbackEntryForm(boolean bDisplayError) throws SQLException, Exception {

        StringBuffer sBuffer = new StringBuffer("");

		ETSCat cat = ETSDatabaseManager.getCat(getTopCatId(),getProj().getProjectId());

        sBuffer.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" height=\"18\" width=\"443\" class=\"subtitle\">We're listening</td></tr></table>");
        sBuffer.append("<br />");

        sBuffer.append("<form name=\"cal_edit\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\" method=\"post\">");
        sBuffer.append("<input type=\"hidden\" name=\"linkid\" value=\"" + getSLink() + "\" />");
        sBuffer.append("<input type=\"hidden\" name=\"tc\" value=\"" + getTopCatId() + "\" />");
        sBuffer.append("<input type=\"hidden\" name=\"proj\" value=\"" + getProj().getProjectId() + "\" />");
        sBuffer.append("<input type=\"hidden\" name=\"actionType\" value=\"feedback\" />");
        sBuffer.append("<input type=\"hidden\" name=\"subactionType\" value=\"insertfeedback\" />");

        sBuffer.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" height=\"18\" width=\"443\" >We value your feedback. Please take a few moments to tell us what you like and do not like about this web site. Give us your suggestions for new items to include and let us know about any problems you have had with this site. You can also include your thoughts on your overall relationship with IBM Engineering and Technology Services team.<br /><br />");
        sBuffer.append("Your comments will be e-mailed to the project delivery team and will be used to enhance our services. We are continuosly working to improve it to meet your needs and expectations.<br /><br />");
		if (cat.getViewType() != Defines.FEEDBACK_VT) {
        	sBuffer.append("Use the <a href=\"" + Defines.SERVLET_PATH + "EtsIssFilterCntrlServlet.wss?tc=" + getTopCatId() + "&proj=" + getProj().getProjectId() + "&linkid=" + getSLink() + "&istyp=iss&opn=10\">Issues/changes</a> tab to submit and track project specific issues and change requests.<br /><br />");
		}
        sBuffer.append("To report a technical problem with this site, use <a href=\"" + Global.getUrl("EdCQMDServlet.wss?linkid=" + getSLink()) + "\">online problem reporting</a> tool. <br />");
        sBuffer.append("</td></tr></table>");
        sBuffer.append("<br />");

        sBuffer.append("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");

        String sTitle = "";
        String sComments = "";

        sTitle = (String) getParams().get("feedback_title");
        if (sTitle == null) sTitle = "";
        sComments = (String) getParams().get("feedback_comments");
        if (sComments == null) sComments = "";

        if (bDisplayError) {

            StringBuffer sError = new StringBuffer("");

            if (sTitle.trim().equals("")) {
                sError.append("Title for feedback cannot be empty. Please enter an appropriate title for the feedback.");
            }

            if (sComments.trim().equals("")) {
                if (sError.toString().trim().equals("")) {
                    sError.append("Comments for feedback cannot be empty. Please enter appropriate comments for the feedback.");
                } else {
                    sError.append("<br />Comments for feedback cannot be empty. Please enter appropriate comment for the feedback.");
                }
            }
            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
            sBuffer.append("</tr>");
            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"2\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
            sBuffer.append("</tr>");
            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
            sBuffer.append("</tr>");
            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"2\"><span class=\"small\"><span style=\"color:#cc6600\">" + sError.toString() + "</span></span></td>");
            sBuffer.append("</tr>");
            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
            sBuffer.append("</tr>");
            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"2\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
            sBuffer.append("</tr>");
            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
            sBuffer.append("</tr>");
        }

        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td colspan=\"2\"><span class=\"small\">Fields marked with <span style=\"color:#cc6600\">*</span> are mandatory fields.</span></td>");
        sBuffer.append("</tr>");
        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
        sBuffer.append("</tr>");
        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td colspan=\"2\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
        sBuffer.append("</tr>");
        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
        sBuffer.append("</tr>");

        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td width=\"100\" class=\"small\" align=\"left\"><b>Name</b>:</td>");
        sBuffer.append("<td width=\"343\" class=\"small\" align=\"left\">" + getEs().gFIRST_NAME.trim() + " " + getEs().gLAST_NAME.trim() + "</td>");
        sBuffer.append("</tr>");


        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td width=\"100\" class=\"small\" align=\"left\"><b>Company</b>:</td>");
        if (getEs().gDECAFTYPE.trim().equals("I")) {
            sBuffer.append("<td width=\"343\" class=\"small\" align=\"left\">IBM</td>");
        } else {
            sBuffer.append("<td width=\"343\" class=\"small\" align=\"left\">" + getEs().gASSOC_COMP + "</td>");
        }
        sBuffer.append("</tr>");


        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td width=\"100\" class=\"small\" align=\"left\"><b>Email</b>:</td>");
        sBuffer.append("<td width=\"343\" class=\"small\" align=\"left\">" + getEs().gEMAIL + "</td>");
        sBuffer.append("</tr>");


        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td width=\"100\" class=\"small\" align=\"left\"><b>Phone</b>:</td>");
        sBuffer.append("<td width=\"343\" class=\"small\" align=\"left\">" + getEs().gPHONE + "</td>");
        sBuffer.append("</tr>");

        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
        sBuffer.append("</tr>");
        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td colspan=\"4\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
        sBuffer.append("</tr>");
        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /><br /></td>");
        sBuffer.append("</tr>");

        sBuffer.append("<tr>");
        sBuffer.append("<td headers=\"\" colspan=\"2\" height=\"18\" >&nbsp;</td>");
        sBuffer.append("</tr>");


        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td width=\"100\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"label_title\">Title</label></b>:</td>");
        sBuffer.append("<td width=\"343\" align=\"left\"><input type=\"text\" name=\"feedback_title\" id=\"label_title\" class=\"iform\" maxlength=\"100\" size=\"53\" value=\"" + sTitle + "\" /></td>");
        sBuffer.append("</tr>");

        sBuffer.append("<tr>");
        sBuffer.append("<td headers=\"\" colspan=\"2\" height=\"18\" >&nbsp;</td>");
        sBuffer.append("</tr>");

        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td width=\"100\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"label_comments\">Comments</label></b>:</td>");
        sBuffer.append("<td width=\"343\" align=\"left\"><textarea name=\"feedback_comments\" cols=\"55\" rows=\"10\"  class=\"iform\"  id=\"label_comments\">" + sComments + "</textarea></td>");
        sBuffer.append("</tr>");

        sBuffer.append("<tr>");
        sBuffer.append("<td headers=\"\" colspan=\"2\" height=\"18\" >&nbsp;</td>");
        sBuffer.append("</tr>");

        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
        sBuffer.append("</tr>");
        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td colspan=\"2\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
        sBuffer.append("</tr>");
        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
        sBuffer.append("</tr>");

        sBuffer.append("<tr>");
        sBuffer.append("<td headers=\"\" colspan=\"2\" height=\"18\" >&nbsp;</td>");
        sBuffer.append("</tr>");

        sBuffer.append("<tr valign=\"top\"><td colspan=\"4\" align=\"left\"><br />");
            sBuffer.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
            sBuffer.append("<tr>");
            sBuffer.append("<td width=\"130\" align=\"left\"><input type=\"image\" name=\"image_continue\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" border=\"0\" /></td>");

			if (cat.getViewType() == Defines.FEEDBACK_VT) {
				sBuffer.append("<td align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + getProj().getProjectId() + "&tc=" + getTopCatId() + "&linkid=" + getSLink() + "&actionType=feedback&subactionType=welcome\">Cancel</a></td></tr></table></td>");
			} else {
				sBuffer.append("<td align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "EtsIssFilterCntrlServlet.wss?tc=" + getTopCatId() + "&proj=" + getProj().getProjectId() + "&linkid=" + getSLink() + "&istyp=iss&opn=10\">Cancel</a></td></tr></table></td>");
			}

            sBuffer.append("</tr>");
            sBuffer.append("</table>");
        sBuffer.append("</td></tr>");

        sBuffer.append("</table>");
        sBuffer.append("<br />");

        sBuffer.append("</form>");

        return sBuffer.toString();

	}

	public static final String VERSION = "1.4";

	/**
	 * Constructor for EtsIssAcceptChange.
	 * @param etsIssObjKey
	 */
	public EtsFeedbackProcess(EtsIssObjectKey etsIssObjKey) {
		super(etsIssObjKey);
	}

	/***
	 * Basic method to print the reject action 1st page
	 *
	 */

	String displayActionIssue(String errMsg, boolean firstTime) throws SQLException, Exception {

        return "";

	}

	/**
	 *
	 * This method will show the attachments/previous issue details
	 */

	String displayProblemClassification(String msg) throws Exception {

        return "";

	} //end ofmethod

	/**
	 * reject final
	 *
	 */

	String actionFinal() throws SQLException, Exception {

		return "";

	} //end of method


    String displayConfirmation(boolean bInserted, String sOp) throws SQLException, Exception {

        StringBuffer sBuffer = new StringBuffer();

        ETSProperties prop = new ETSProperties();
        String sMess = "";

		try {

	        sBuffer.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" height=\"18\" width=\"443\" class=\"subtitle\">Submit a feedback</td></tr></table>");
	        sBuffer.append("<br />");

	        sBuffer.append("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
	        sBuffer.append("<tr valign=\"top\">");
	        sBuffer.append("<td ><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
	        sBuffer.append("</tr>");
	        sBuffer.append("<tr valign=\"top\">");
	        sBuffer.append("<td style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
	        sBuffer.append("</tr>");
	        sBuffer.append("<tr valign=\"top\">");
	        sBuffer.append("<td ><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
	        sBuffer.append("</tr>");
	        sBuffer.append("<tr valign=\"top\">");
	        sBuffer.append("<td ><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
	        sBuffer.append("</tr>");
	        if (bInserted) {
	            if (sOp.equalsIgnoreCase("insert")) {
	                sMess = prop.getSFeedSuccess();
	            } else {
	                sMess = prop.getSFeedUpdate();
	            }
	            sBuffer.append("<tr valign=\"top\">");
	            sBuffer.append("<td><b>" + sMess + "</b></td>");
	            sBuffer.append("</tr>");
	        } else {
	            sMess = prop.getSFeedFailure();
	            sBuffer.append("<tr valign=\"top\">");
	            sBuffer.append("<td><span style=\"color:#ff3333\"><b>" + sMess + "</b><span></td>");
	            sBuffer.append("</tr>");

	        }

	        sBuffer.append("<tr valign=\"top\">");
	        sBuffer.append("<td ><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
	        sBuffer.append("</tr>");
	        sBuffer.append("<tr valign=\"top\">");
	        sBuffer.append("<td style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
	        sBuffer.append("</tr>");
	        sBuffer.append("<tr valign=\"top\">");
	        sBuffer.append("<td ><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
	        sBuffer.append("</tr>");
	        sBuffer.append("<tr valign=\"top\">");
	        sBuffer.append("<td ><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
	        sBuffer.append("</tr>");

	        sBuffer.append("<tr valign=\"top\">");
	        sBuffer.append("<td>&nbsp;</td>");
	        sBuffer.append("</tr>");

			ETSCat cat = ETSDatabaseManager.getCat(getTopCatId(),getProj().getProjectId());

	        sBuffer.append("<tr valign=\"top\">");

	        if (cat.getViewType() == Defines.FEEDBACK_VT) {
				sBuffer.append("<td><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + getProj().getProjectId() + "&tc=" + getTopCatId() + "&linkid=" + getSLink() + "&actionType=feedback&subactionType=welcome\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
	        } else {
				sBuffer.append("<td><a href=\"" + Defines.SERVLET_PATH + "EtsIssFilterCntrlServlet.wss?tc=" + getTopCatId() + "&proj=" + getProj().getProjectId() + "&linkid=" + getSLink() + "&istyp=iss&opn=10\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" width=\"120\" height=\"21\" alt=\"continue\" border=\"0\" /></a></td>");
	        }

	        sBuffer.append("</tr>");
	        sBuffer.append("</table>");

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

        return sBuffer.toString();

    }
	/***
	 * This method will make changes to problem_state and comm_from_cust to cq.problem_info_usr1 tables
	 * for reject state
	 */

	String commitIssue() throws SQLException, Exception {

        return "";

	} //end of method

	/**
		 * core method to call other methods based on various action
		 */

	public String processRequest() throws SQLException, Exception {

        Connection con = null;

        StringBuffer buffer = new StringBuffer("");

        try {

            con = ETSDBUtils.getConnection();
            
            ETSCat cat = ETSDatabaseManager.getCat(getTopCatId(),getProj().getProjectId());

            ETSHeaderFooter headerfooter = new ETSHeaderFooter();
            headerfooter.init(getRequest(), getResponse());

			
			

    		StringBuffer sbaccp = new StringBuffer();

    		if (getSubActionType() == null || getSubActionType().trim().equals("")) { // feedback entry form

				sbaccp.append(headerfooter.getHeader());

                // display the entry form for feedback
    			sbaccp.append(displayFeedbackEntryForm(false));

				sbaccp.append("</td>");

				sbaccp.append("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
				// Right column start
				sbaccp.append("<td headers=\"\" width=\"150\" valign=\"top\">");
				if (cat.getViewType() == Defines.FEEDBACK_VT) {
					ETSClientCareContact contact = new ETSClientCareContact(con,getProj().getProjectId(), getRequest());
					sbaccp.append(contact.getContactBox());
				} else {
					ETSContact contact = new ETSContact(getProj().getProjectId(), getRequest());
					sbaccp.append(contact.getContactBox());
				}
				
				sbaccp.append("<br />");

			} else if (getSubActionType().equals("welcome")) {

				sbaccp.append(headerfooter.getHeader());

				sbaccp.append(displayWelcomeForm());

				sbaccp.append("</td>");

				sbaccp.append("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
				// Right column start
				sbaccp.append("<td headers=\"\" width=\"150\" valign=\"top\">");
				if (cat.getViewType() == Defines.FEEDBACK_VT) {
					ETSClientCareContact contact = new ETSClientCareContact(con,getProj().getProjectId(), getRequest());
					sbaccp.append(contact.getContactBox());
				} else {
					ETSContact contact = new ETSContact(getProj().getProjectId(), getRequest());
					sbaccp.append(contact.getContactBox());
				}

				sbaccp.append("<br />");

    		} else if (getSubActionType().equals("insertfeedback")) {

                // insert into database and redirect to issues/changes page.

				sbaccp.append(headerfooter.getHeader());

                if (validateFeedback()) {
                    sbaccp.append(displayConfirmation(insertFeedback(con),"INSERT"));
                } else {
                    sbaccp.append(displayFeedbackEntryForm(true));
                }

				sbaccp.append("</td>");

				sbaccp.append("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
				// Right column start
				sbaccp.append("<td headers=\"\" width=\"150\" valign=\"top\">");
				if (cat.getViewType() == Defines.FEEDBACK_VT) {
					ETSClientCareContact contact = new ETSClientCareContact(con,getProj().getProjectId(), getRequest());
					sbaccp.append(contact.getContactBox());
				} else {
					ETSContact contact = new ETSContact(getProj().getProjectId(), getRequest());
					sbaccp.append(contact.getContactBox());
				}

				sbaccp.append("<br />");

    		} else if (getSubActionType().equals("editfeedback")) {

				sbaccp.append(headerfooter.getHeader());

                //display the feedback for edit
                sbaccp.append(displayFeedbackEditForm(con,false));

				sbaccp.append("</td>");

				sbaccp.append("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
				// Right column start
				sbaccp.append("<td headers=\"\" width=\"150\" valign=\"top\">");
				if (cat.getViewType() == Defines.FEEDBACK_VT) {
					ETSClientCareContact contact = new ETSClientCareContact(con,getProj().getProjectId(), getRequest());
					sbaccp.append(contact.getContactBox());
				} else {
					ETSContact contact = new ETSContact(getProj().getProjectId(), getRequest());
					sbaccp.append(contact.getContactBox());
				}

				sbaccp.append("<br />");

            } else if (getSubActionType().equals("updatefeedback")) {

				sbaccp.append(headerfooter.getHeader());

                // insert into database and redirect to issues/changes page.
                if (validateFeedback()) {
                    sbaccp.append(displayConfirmation(updateFeedback(con),"UPDATE"));
                } else {
                    sbaccp.append(displayFeedbackEditForm(con,true));
                }

				sbaccp.append("</td>");

				sbaccp.append("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
				// Right column start
				sbaccp.append("<td headers=\"\" width=\"150\" valign=\"top\">");
				if (cat.getViewType() == Defines.FEEDBACK_VT) {
					ETSClientCareContact contact = new ETSClientCareContact(con,getProj().getProjectId(), getRequest());
					sbaccp.append(contact.getContactBox());
				} else {
					ETSContact contact = new ETSContact(getProj().getProjectId(), getRequest());
					sbaccp.append(contact.getContactBox());
				}

				sbaccp.append("<br />");

            } else if (getSubActionType().equals("viewfeedback")) {

				sbaccp.append(headerfooter.getHeader());

                // display just my feedback
                sbaccp.append(displayFeedbackDetails(con));

				sbaccp.append("</td>");

				sbaccp.append("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
				// Right column start
				sbaccp.append("<td headers=\"\" width=\"150\" valign=\"top\">");
				if (cat.getViewType() == Defines.FEEDBACK_VT) {
					ETSClientCareContact contact = new ETSClientCareContact(con,getProj().getProjectId(), getRequest());
					sbaccp.append(contact.getContactBox());
				} else {
					ETSContact contact = new ETSContact(getProj().getProjectId(), getRequest());
					sbaccp.append(contact.getContactBox());
				}

				sbaccp.append("<br />");

    		} else if (getSubActionType().equals("myfeedback")) {

				sbaccp.append(headerfooter.getHeader());

                // display just my feedback
    			sbaccp.append(displayMyFeedback(con));

				sbaccp.append("</td>");

				sbaccp.append("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
				// Right column start
				sbaccp.append("<td headers=\"\" width=\"150\" valign=\"top\">");
				if (cat.getViewType() == Defines.FEEDBACK_VT) {
					ETSClientCareContact contact = new ETSClientCareContact(con,getProj().getProjectId(), getRequest());
					sbaccp.append(contact.getContactBox());
				} else {
					ETSContact contact = new ETSContact(getProj().getProjectId(), getRequest());
					sbaccp.append(contact.getContactBox());
				}

				sbaccp.append("<br />");

    		} else if (getSubActionType().equals("allfeedback")) {

				sbaccp.append(headerfooter.getHeader());

                // display all feedback for this project
                sbaccp.append(displayAllProjectFeedback(con));

				sbaccp.append("</td>");

				sbaccp.append("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
				// Right column start
				sbaccp.append("<td headers=\"\" width=\"150\" valign=\"top\">");
				if (cat.getViewType() == Defines.FEEDBACK_VT) {
					ETSClientCareContact contact = new ETSClientCareContact(con,getProj().getProjectId(), getRequest());
					sbaccp.append(contact.getContactBox());
				} else {
					ETSContact contact = new ETSContact(getProj().getProjectId(), getRequest());
					sbaccp.append(contact.getContactBox());
				}

				sbaccp.append("<br />");

			} else if (getSubActionType().equals("viewIssues")) {	



				if (!ETSUtils.checkUserRole(getEs(),getProj().getProjectId()).equals(Defines.WORKSPACE_CLIENT)) {

					boolean download = false;
					boolean validate = false;

					String error = "";

					if (getRequest().getParameter("download.x") != null && getRequest().getParameter("download.y") != null) {
						download = true;
						validate = true;
					}

					if (getRequest().getParameter("report.x") != null && getRequest().getParameter("report.y") != null) {
						validate = true;
					}

					if (validate) {
						error = validateReportFilter();
					}

					if (download && error.equalsIgnoreCase("")) {

						sbaccp.append(displayIssueReport(con,true));

					} else {

						sbaccp.append(headerfooter.getHeader());

						sbaccp.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" height=\"18\" width=\"443\" class=\"subtitle\">Issue activity details</td></tr></table>");
						sbaccp.append("<br />");

						sbaccp.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" height=\"18\" width=\"443\" >Please select the issue filter details and report columns and click on <b>Submit</b> button to see the issue activity details report.</td></tr></table>");

						sbaccp.append("<br />");
						sbaccp.append("<br />");

						sbaccp.append("<form name=\"feedback\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\" method=\"post\">");
						sbaccp.append("<input type=\"hidden\" name=\"linkid\" value=\"" + getSLink() + "\" />");
						sbaccp.append("<input type=\"hidden\" name=\"tc\" value=\"" + getTopCatId() + "\" />");
						sbaccp.append("<input type=\"hidden\" name=\"proj\" value=\"" + getProj().getProjectId() + "\" />");
						sbaccp.append("<input type=\"hidden\" name=\"actionType\" value=\"feedback\" />");
						sbaccp.append("<input type=\"hidden\" name=\"subactionType\" value=\"viewIssues\" />");
						sbaccp.append("<input type=\"hidden\" name=\"func\" value=\"ETS\" />");


						// display all feedback for this project
						sbaccp.append(displayIssueReportFilter(con,error));

						sbaccp.append("</td>");

						sbaccp.append("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
						// Right column start
						sbaccp.append("<td headers=\"\" width=\"150\" valign=\"top\">");
						if (cat.getViewType() == Defines.FEEDBACK_VT) {
							ETSClientCareContact contact = new ETSClientCareContact(con,getProj().getProjectId(), getRequest());
							sbaccp.append(contact.getContactBox());
						} else {
							ETSContact contact = new ETSContact(getProj().getProjectId(), getRequest());
							sbaccp.append(contact.getContactBox());
						}

						sbaccp.append("<br />");

						sbaccp.append("</table>");

						sbaccp.append("<br />");

						sbaccp.append(displayIssueReportColumns(con));

						sbaccp.append("</form>");

						sbaccp.append("<br />");

						if (error.equalsIgnoreCase("")) {
							if (getRequest().getParameter("report.x") != null && getRequest().getParameter("report.y") != null) {
								// user selected submit.. show the report...
								sbaccp.append(displayIssueReport(con,false));

								sbaccp.append("<table></tr><td>");
							}
						}

					}


				} else {
					sbaccp.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" height=\"18\" width=\"443\" class=\"subtitle\">Issue activity details</td></tr></table>");
					sbaccp.append("<br />");
					sbaccp.append("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
					sbaccp.append("<tr><td valign=\"top\" ><b>You are not authorized to access this feature. Please contact your Client Care Advocate.</b></td>");
					sbaccp.append("</tr>");
					sbaccp.append("</table");

					sbaccp.append("<br />");
					sbaccp.append("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");
					sbaccp.append("<td align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"middle\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + getProj().getProjectId() + "&tc=" + getTopCatId() + "&linkid=" + getSLink() + "&actionType=feedback&subactionType=welcome\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></a></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + getProj().getProjectId() + "&tc=" + getTopCatId() + "&linkid=" + getSLink() + "&actionType=feedback&subactionType=welcome\">Cancel</a></td></tr></table></td>");
					sbaccp.append("</tr></table>");


				}
            }

    		return sbaccp.toString();

        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        } finally {
            ETSDBUtils.close(con);
        }

	}

	/**
	 * @return
	 */
	private String displayWelcomeForm() throws Exception {

		StringBuffer sOut = new StringBuffer("");

		sOut.append(ETSUtils.getBookMarkString("Feedback / Issues","#setmet", true));

		sOut.append("<table summary=\"feedback links\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\"	width=\"443\">");
		if (ETSUtils.checkUserRole(getEs(),getProj().getProjectId()).equals(Defines.ETS_ADMIN) || ETSUtils.checkUserRole(getEs(),getProj().getProjectId()).equals(Defines.WORKSPACE_MANAGER) || ETSUtils.checkUserRole(getEs(),getProj().getProjectId()).equals(Defines.WORKSPACE_OWNER) || ETSUtils.checkUserRole(getEs(),getProj().getProjectId()).equals(Defines.WORKSPACE_MEMBER)) {
			sOut.append("<tr>");
			sOut.append("<td valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + getProj().getProjectId() + "&tc=" + getTopCatId() + "&linkid=" + getSLink() + "&actionType=feedback\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" alt=\"Submit your feedback\" align=\"top\" width=\"16\" height=\"16\" border=\"0\" /></a><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + getProj().getProjectId() + "&tc=" + getTopCatId() + "&linkid=" + getSLink() + "&actionType=feedback\">Submit your feedback</a></td>");
			sOut.append("</tr>");
		}
		sOut.append("<tr>");
		sOut.append("<td valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + getProj().getProjectId() + "&tc=" + getTopCatId() + "&linkid=" + getSLink() + "&actionType=feedback&subactionType=myfeedback\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" alt=\"Feedbacks submitted by me\" align=\"top\" width=\"16\" height=\"16\" border=\"0\" /></a><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + getProj().getProjectId() + "&tc=" + getTopCatId() + "&linkid=" + getSLink() + "&actionType=feedback&subactionType=myfeedback\">Feedback submitted by me</a></td>");
		sOut.append("</tr>");
		sOut.append("<tr>");
		sOut.append("<td valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + getProj().getProjectId() + "&tc=" + getTopCatId() + "&linkid=" + getSLink() + "&actionType=feedback&subactionType=allfeedback\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" alt=\"Feedbacks submitted by team members\" align=\"top\" width=\"16\" height=\"16\" border=\"0\" /></a><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + getProj().getProjectId() + "&tc=" + getTopCatId() + "&linkid=" + getSLink() + "&actionType=feedback&subactionType=allfeedback\">Feedback submitted by team members</a></td>");
		sOut.append("</tr>");
		// dont show the following to anyone with client role...
		if (!ETSUtils.checkUserRole(getEs(),getProj().getProjectId()).equals(Defines.WORKSPACE_CLIENT)) {
			sOut.append("<tr>");
			sOut.append("<td valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + getProj().getProjectId() + "&tc=" + getTopCatId() + "&linkid=" + getSLink() + "&actionType=feedback&subactionType=viewIssues\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" alt=\"Issue activity details\" align=\"top\" width=\"16\" height=\"16\" border=\"0\" /></a><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + getProj().getProjectId() + "&tc=" + getTopCatId() + "&linkid=" + getSLink() + "&actionType=feedback&subactionType=viewIssues\">Issue activity details</a></td>");
			sOut.append("</tr>");
		}
		sOut.append("</table>");


		return sOut.toString();
	}

	private String displayFeedbackDetails(Connection con) throws SQLException, Exception {

        try {

            String sEdgeProblemId = (String) getParams().get("edge_problem_id");
            if (sEdgeProblemId == null || sEdgeProblemId.trim().equals("")) {
                sEdgeProblemId = "";
            }

            String sFrom = getParams().get("from").toString();
            if (sFrom == null || sFrom.trim().equals("")) {
                sFrom = "myfeedback";
            } else {
                sFrom = sFrom.trim();
            }

            ETSFeedbackBean feedBack = ETSFeedbackDAO.getFeedback(con,"ETS",sEdgeProblemId);

            StringBuffer sBuffer = new StringBuffer("");

            sBuffer.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" height=\"18\" width=\"443\" class=\"subtitle\">Feedback details</td></tr></table>");
            sBuffer.append("<br />");

            sBuffer.append("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
            sBuffer.append("</tr>");
            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"2\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
            sBuffer.append("</tr>");
            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
            sBuffer.append("</tr>");

            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td width=\"100\" align=\"left\"><b>Name</b>:</td>");
            sBuffer.append("<td width=\"343\" align=\"left\">" + feedBack.getCustName() + "</td>");
            sBuffer.append("</tr>");


            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td width=\"100\" align=\"left\"><b>Company</b>:</td>");
            sBuffer.append("<td width=\"343\" align=\"left\">" + feedBack.getCustCompany() + "</td>");
            sBuffer.append("</tr>");

            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td width=\"100\" align=\"left\"><b>Email</b>:</td>");
            sBuffer.append("<td width=\"343\" align=\"left\">" + feedBack.getCustEmail() + "</td>");
            sBuffer.append("</tr>");

            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td width=\"100\" align=\"left\"><b>Phone</b>:</td>");
            sBuffer.append("<td width=\"343\" align=\"left\">" + feedBack.getCustPhone() + "</td>");
            sBuffer.append("</tr>");

			sBuffer.append("<tr valign=\"top\">");
			sBuffer.append("<td colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			sBuffer.append("</tr>");
			sBuffer.append("<tr valign=\"top\">");
			sBuffer.append("<td colspan=\"2\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			sBuffer.append("</tr>");
			sBuffer.append("<tr valign=\"top\">");
			sBuffer.append("<td colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
			sBuffer.append("</tr>");

            int iCQ1SequenceNo = ETSFeedbackDAO.getSeqNoFromCQ1(con,sEdgeProblemId);

            int iSeqNo = feedBack.getSeqNo();

            String sTempDate = feedBack.getLastTime().toString();

            String sDate = sTempDate.substring(5, 7) + "/" + sTempDate.substring(8, 10) + "/" + sTempDate.substring(0, 4);
            String sTime = sTempDate.substring(11, 16);

            String sHour = sTempDate.substring(11, 13);
            String sMin = sTempDate.substring(14, 16);
            String sAMPM = "AM";

            if (Integer.parseInt(sHour) == 12) {
                sHour = String.valueOf(Integer.parseInt(sHour));
                sAMPM = "PM";
            } else if (Integer.parseInt(sHour) > 12) {
                sHour = String.valueOf(Integer.parseInt(sHour) - 12);
                sAMPM = "PM";
            }


            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"2\" align=\"left\">&nbsp;</td>");
            sBuffer.append("</tr>");

            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td width=\"100\" align=\"left\"><b>Title</b>:</td>");
            sBuffer.append("<td width=\"343\" align=\"left\">" + feedBack.getTitle() + "</td>");
            sBuffer.append("</tr>");

            if (iSeqNo > iCQ1SequenceNo) {

				sBuffer.append("<tr valign=\"top\">");
				sBuffer.append("<td colspan=\"2\" align=\"left\">&nbsp;<br /></td>");
				sBuffer.append("</tr>");

                sBuffer.append("<tr valign=\"top\">");
                sBuffer.append("<td width=\"100\" align=\"left\"><b>Comments</b>:</td>");
                sBuffer.append("<td width=\"343\" align=\"left\">" + feedBack.getComments() + "</td>");
                sBuffer.append("</tr>");

            }


			sBuffer.append("<tr valign=\"top\">");
			sBuffer.append("<td colspan=\"2\" align=\"left\">&nbsp;<br /></td>");
			sBuffer.append("</tr>");

            sBuffer.append("<tr valign=\"top\">");
			sBuffer.append("<td colspan=\"2\" align=\"left\" height=\"18\" class=\"tblue\"><label for=\"label_comments\">&nbsp;Comments log</label></td>");
			sBuffer.append("</tr>");

//			sBuffer.append("<tr valign=\"top\">");
//			sBuffer.append("<td colspan=\"2\" align=\"left\" height=\"10\">&nbsp;</td>");
//			sBuffer.append("</tr>");

			sBuffer.append("<tr valign=\"top\">");
			sBuffer.append("<td colspan=\"2\" align=\"left\" ><textarea name=\"feedback_comments\" cols=\"70\" rows=\"10\"  class=\"iform\"  id=\"label_comments\">" + ETSFeedbackDAO.getCommentsLog(con,sEdgeProblemId) + "</textarea></td>");
			sBuffer.append("</tr>");

            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"2\" align=\"left\">&nbsp;<br /></td>");
            sBuffer.append("</tr>");

			sBuffer.append("<tr valign=\"top\">");
			sBuffer.append("<td width=\"100\" align=\"left\"><b>Last updated by</b>:</td>");
			sBuffer.append("<td width=\"343\" align=\"left\">" + ETSUtils.getUsersNameFromEdgeId(con,feedBack.getLastUserId()) + "</td>");
			sBuffer.append("</tr>");

			sBuffer.append("<tr valign=\"top\">");
			sBuffer.append("<td width=\"100\" align=\"left\"><b>Last updated on</b>:</td>");
			sBuffer.append("<td width=\"343\" align=\"left\">" + sDate + " (mm/dd/yyyy)</td>");
			sBuffer.append("</tr>");


            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
            sBuffer.append("</tr>");
            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"2\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
            sBuffer.append("</tr>");
            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
            sBuffer.append("</tr>");
            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
            sBuffer.append("</tr>");
            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"2\">&nbsp;</td>");
            sBuffer.append("</tr>");


            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"2\">");

			if (ETSUtils.checkUserRole(getEs(),getProj().getProjectId()).equals(Defines.ETS_ADMIN) || ETSUtils.checkUserRole(getEs(),getProj().getProjectId()).equals(Defines.WORKSPACE_MANAGER) || ETSUtils.checkUserRole(getEs(),getProj().getProjectId()).equals(Defines.WORKSPACE_OWNER) || ETSUtils.checkUserRole(getEs(),getProj().getProjectId()).equals(Defines.WORKSPACE_MEMBER) || ETSUtils.checkUserRole(getEs(),getProj().getProjectId()).equals(Defines.WORKSPACE_CLIENT)) {
	            if (iSeqNo > iCQ1SequenceNo) {

	                ETSProperties prop = new ETSProperties();
	                String sMsg = prop.getSFeedbackProcessing();
	                sBuffer.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
	                sBuffer.append("<tr>");
	                sBuffer.append("<td ><b>" + sMsg + "</b><br /><br /></td>");
	                sBuffer.append("</tr>");
	                sBuffer.append("<tr valign=\"top\">");
	                sBuffer.append("<td ><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
	                sBuffer.append("</tr>");
	                sBuffer.append("<tr valign=\"top\">");
	                sBuffer.append("<td style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
	                sBuffer.append("</tr>");
	                sBuffer.append("<tr valign=\"top\">");
	                sBuffer.append("<td ><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
	                sBuffer.append("</tr>");

	                sBuffer.append("<tr>");
	                sBuffer.append("<td headers=\"\" ><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + getProj().getProjectId() + "&tc=" + getTopCatId() + "&actionType=feedback&subactionType=" + sFrom + "&linkid=" + getSLink() + "\" ><img src=\"" + Defines.BUTTON_ROOT + "cancel.gif\" width=\"120\" height=\"21\" alt=\"Cancel\" border=\"0\" /></a></td>");
	                sBuffer.append("</tr>");
	                sBuffer.append("</table><br />");
	            } else {
	                sBuffer.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\">");
	                sBuffer.append("<tr>");
	                sBuffer.append("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?edge_problem_id=" + feedBack.getEdgeProblemId() + "&tc=" + getTopCatId() + "&proj=" + getProj().getProjectId() + "&actionType=feedback&subactionType=editfeedback&linkid=" + getSLink() + "\"><img src=\"" + Defines.ICON_ROOT + "fw_c.gif\" width=\"16\" height=\"16\" alt=\"Add more comments\" border=\"0\" /></a></td>");
	                sBuffer.append("<td headers=\"\" align=\"left\" width=\"150\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?edge_problem_id=" + feedBack.getEdgeProblemId() + "&tc=" + getTopCatId() + "&proj=" + getProj().getProjectId() + "&actionType=feedback&subactionType=editfeedback&linkid=" + getSLink() + "\">Add more comments</a></td>");
	                sBuffer.append("<td headers=\"\" ><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"middle\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + getProj().getProjectId() + "&tc=" + getTopCatId() + "&actionType=feedback&subactionType=" + sFrom + "&linkid=" + getSLink() + "\" ><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></a></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + getProj().getProjectId() + "&tc=" + getTopCatId() + "&actionType=feedback&subactionType=" + sFrom + "&linkid=" + getSLink() + "\" >Cancel</a></td></tr></table></td>");
	                sBuffer.append("</tr>");
	                sBuffer.append("</table><br />");
	            }
			} else {
				sBuffer.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\">");
				sBuffer.append("<tr>");
				sBuffer.append("<td headers=\"\" ><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"middle\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + getProj().getProjectId() + "&tc=" + getTopCatId() + "&actionType=feedback&subactionType=" + sFrom + "&linkid=" + getSLink() + "\" ><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></a></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + getProj().getProjectId() + "&tc=" + getTopCatId() + "&actionType=feedback&subactionType=" + sFrom + "&linkid=" + getSLink() + "\" >Cancel</a></td></tr></table></td>");
				sBuffer.append("</tr>");
				sBuffer.append("</table><br />");
			}

            sBuffer.append("</tr>");
            sBuffer.append("</table>");

            return sBuffer.toString();

        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
	}


	private String displayFeedbackEditForm(Connection con, boolean bError) throws SQLException, Exception {

        StringBuffer sBuffer = new StringBuffer("");


        String sEdgeProblemId = (String) getParams().get("edge_problem_id");
        if (sEdgeProblemId == null || sEdgeProblemId.trim().equals("")) {
            sEdgeProblemId = "";
        }

        sBuffer.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" height=\"18\" width=\"443\" class=\"subtitle\">Add more comments</td></tr></table>");
        sBuffer.append("<br />");

        sBuffer.append("<form name=\"cal_edit\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\" method=\"post\">");
        sBuffer.append("<input type=\"hidden\" name=\"linkid\" value=\"" + getSLink() + "\" />");
        sBuffer.append("<input type=\"hidden\" name=\"tc\" value=\"" + getTopCatId() + "\" />");
        sBuffer.append("<input type=\"hidden\" name=\"proj\" value=\"" + getProj().getProjectId() + "\" />");
        sBuffer.append("<input type=\"hidden\" name=\"actionType\" value=\"feedback\" />");
        sBuffer.append("<input type=\"hidden\" name=\"subactionType\" value=\"updatefeedback\" />");
        sBuffer.append("<input type=\"hidden\" name=\"edge_problem_id\" value=\"" + sEdgeProblemId + "\" />");

        int iCQ1SequenceNo = ETSFeedbackDAO.getSeqNoFromCQ1(con,sEdgeProblemId);
        String sCQTrkId = ETSFeedbackDAO.getCQTrkId(con,sEdgeProblemId);

        sBuffer.append("<input type=\"hidden\" name=\"cq_seq_no\" value=\"" + String.valueOf(iCQ1SequenceNo) + "\" />");
        sBuffer.append("<input type=\"hidden\" name=\"cq_trk_id\" value=\"" + String.valueOf(sCQTrkId) + "\" />");

        ETSFeedbackBean feed = ETSFeedbackDAO.getFeedback(con,"ETS",sEdgeProblemId);
        String sTitle = feed.getTitle();
        String sComments = feed.getComments();
        if (sTitle == null) sTitle = "";
        if (sComments == null) sComments = "";

        // making the comments always blank for edit...
        sComments = "";

        sBuffer.append("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");

        if (bError) {

            sTitle = (String) getParams().get("feedback_title");
            if (sTitle == null) sTitle = "";
            sComments = (String) getParams().get("feedback_comments");
            if (sComments == null) sComments = "";

            StringBuffer sError = new StringBuffer("");

            if (sTitle.trim().equals("")) {
                sError.append("Title for feedback cannot be empty. Please enter an appropriate title for the feedback.");
            }

            if (sComments.trim().equals("")) {
                if (sError.toString().trim().equals("")) {
                    sError.append("Comments for feedback cannot be empty. Please enter appropriate comments for the feedback.");
                } else {
                    sError.append("<br />Comments for feedback cannot be empty. Please enter appropriate comment for the feedback.");
                }
            }
            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
            sBuffer.append("</tr>");
            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"2\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
            sBuffer.append("</tr>");
            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
            sBuffer.append("</tr>");
            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"2\"><span class=\"small\"><span style=\"color:#cc6600\">" + sError.toString() + "</span></span></td>");
            sBuffer.append("</tr>");
            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
            sBuffer.append("</tr>");
            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"2\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
            sBuffer.append("</tr>");
            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
            sBuffer.append("</tr>");
        }

        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td colspan=\"2\"><span class=\"small\">Fields marked with <span style=\"color:#cc6600\">*</span> are mandatory fields.</span></td>");
        sBuffer.append("</tr>");
        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
        sBuffer.append("</tr>");
        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td colspan=\"2\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
        sBuffer.append("</tr>");
        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
        sBuffer.append("</tr>");

        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td width=\"100\" align=\"left\"><b>Name</b>:</td>");
        sBuffer.append("<td width=\"343\" align=\"left\">" + feed.getCustName() + "</td>");
        sBuffer.append("</tr>");

        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td width=\"100\" align=\"left\"><b>Company</b>:</td>");
        sBuffer.append("<td width=\"343\" align=\"left\">" + feed.getCustCompany() + "</td>");
        sBuffer.append("</tr>");

        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td width=\"100\" align=\"left\"><b>Email</b>:</td>");
        sBuffer.append("<td width=\"343\" align=\"left\">" + feed.getCustEmail() + "</td>");
        sBuffer.append("</tr>");

        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td width=\"100\" align=\"left\"><b>Phone</b>:</td>");
        sBuffer.append("<td width=\"343\" align=\"left\">" + feed.getCustPhone() + "</td>");
        sBuffer.append("</tr>");

        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
        sBuffer.append("</tr>");
        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td colspan=\"2\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
        sBuffer.append("</tr>");
        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /><br /></td>");
        sBuffer.append("</tr>");

		sBuffer.append("<tr valign=\"top\">");
		sBuffer.append("<td colspan=\"2\" align=\"left\">&nbsp;<br /></td>");
		sBuffer.append("</tr>");

		sBuffer.append("<tr valign=\"top\">");
		sBuffer.append("<td colspan=\"2\" align=\"left\" height=\"18\" class=\"tblue\"><label for=\"label_comments\">&nbsp;Comments log</label></td>");
		sBuffer.append("</tr>");

//		sBuffer.append("<tr valign=\"top\">");
//		sBuffer.append("<td colspan=\"2\" align=\"left\" height=\"10\">&nbsp;</td>");
//		sBuffer.append("</tr>");

		sBuffer.append("<tr valign=\"top\">");
		sBuffer.append("<td colspan=\"2\" align=\"left\" ><textarea name=\"feedback_comments1\" cols=\"70\" rows=\"10\"  class=\"iform\"  id=\"label_comments\">" + ETSFeedbackDAO.getCommentsLog(con,sEdgeProblemId) + "</textarea></td>");
		sBuffer.append("</tr>");


		sBuffer.append("<tr valign=\"top\">");
		sBuffer.append("<td colspan=\"2\" align=\"left\">&nbsp;<br /></td>");
		sBuffer.append("</tr>");

        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td width=\"100\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"label_title\">Title</label></b>:</td>");
        sBuffer.append("<td width=\"343\" align=\"left\"><input type=\"text\" name=\"feedback_title\" id=\"label_title\" class=\"iform\" maxlangth=\"100\" size=\"53\" value=\"" + sTitle + "\" /></td>");
        sBuffer.append("</tr>");

        sBuffer.append("<tr>");
        sBuffer.append("<td headers=\"\" colspan=\"2\" height=\"18\" >&nbsp;</td>");
        sBuffer.append("</tr>");

        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td width=\"100\" align=\"left\"><span style=\"color:#cc6600\">*</span><b><label for=\"label_comments\">Comments</label></b>:</td>");
        sBuffer.append("<td width=\"343\" align=\"left\"><textarea name=\"feedback_comments\" cols=\"55\" rows=\"10\"  class=\"iform\"  id=\"label_comments\">" + sComments + "</textarea></td>");
        sBuffer.append("</tr>");

        sBuffer.append("<tr>");
        sBuffer.append("<td headers=\"\" colspan=\"2\" height=\"18\" >&nbsp;</td>");
        sBuffer.append("</tr>");

        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
        sBuffer.append("</tr>");
        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td colspan=\"2\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
        sBuffer.append("</tr>");
        sBuffer.append("<tr valign=\"top\">");
        sBuffer.append("<td colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
        sBuffer.append("</tr>");

        sBuffer.append("<tr>");
        sBuffer.append("<td headers=\"\" colspan=\"2\" height=\"18\" >&nbsp;</td>");
        sBuffer.append("</tr>");

        sBuffer.append("<tr valign=\"top\"><td colspan=\"4\" align=\"left\"><br />");
            sBuffer.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
            sBuffer.append("<tr>");
            sBuffer.append("<td width=\"130\" align=\"left\"><input type=\"image\" name=\"image_continue\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" border=\"0\" /></td>");

			ETSCat cat = ETSDatabaseManager.getCat(getTopCatId(),getProj().getProjectId());
			if (cat.getViewType() == Defines.FEEDBACK_VT) {
				sBuffer.append("<td align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + getProj().getProjectId() + "&tc=" + getTopCatId() + "&linkid=" + getSLink() + "&actionType=feedback&subactionType=welcome\">Cancel</a></td></tr></table></td>");
			} else {
				sBuffer.append("<td align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"middle\" align=\"left\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "EtsIssFilterCntrlServlet.wss?tc=" + getTopCatId() + "&proj=" + getProj().getProjectId() + "&linkid=" + getSLink() + "&istyp=iss&opn=10\" >Cancel</a></td></tr></table></td>");
			}
            sBuffer.append("</tr>");
            sBuffer.append("</table>");
        sBuffer.append("</td></tr>");

        sBuffer.append("</table>");
        sBuffer.append("<br />");

        sBuffer.append("</form>");

        return sBuffer.toString();

	}

    private boolean insertFeedback(Connection con) throws SQLException, Exception {

//        ETSFeedbackBean feedbackBean = new ETSFeedbackBean();
//
//        feedbackBean.setApplicationId("ETS");
//        feedbackBean.setETSProjectId(ETSUtils.checkNull(getParams().get("proj").toString()));
//        feedbackBean.setComments(getParams().get("feedback_comments").toString());
//        feedbackBean.setCQTrackId("-");
//        if (getEs().gDECAFTYPE.trim().equals("I")) {
//            feedbackBean.setCustCompany("IBM");
//        } else {
//            feedbackBean.setCustCompany(getEs().gASSOC_COMP);
//        }
//        feedbackBean.setCustEmail(getEs().gEMAIL);
//        feedbackBean.setCustName(getEs().gFIRST_NAME.trim() + " " + getEs().gLAST_NAME.trim());
//        feedbackBean.setCustPhone(getEs().gPHONE);
//        feedbackBean.setCustProjectName(getProj().getName());
//        //String sUniqueId = getEs().gUSERN + new Long(System.currentTimeMillis());
//        feedbackBean.setEdgeProblemId(sUniqueId);
//        feedbackBean.setLastUserId(getEs().gUSERN);
//        feedbackBean.setProblemClass("Feedback");
//        feedbackBean.setProblemCreator(getEs().gUSERN);
//        feedbackBean.setProblemState("New");
//        feedbackBean.setProblemType("Feedback");
//        feedbackBean.setSeqNo(1);
//        feedbackBean.setSeverity("4");
//        feedbackBean.setLastTime(new Timestamp(System.currentTimeMillis()));
//        feedbackBean.setTitle(getParams().get("feedback_title").toString());

		ETSMWIssue issue = new ETSMWIssue();

		issue.application_id = "ETS";
		issue.ets_project_id = ETSUtils.checkNull(getParams().get("proj").toString());
		issue.comm_from_cust = getParams().get("feedback_comments").toString();
		issue.cq_trk_id =  "-";
		if (getEs().gDECAFTYPE.trim().equals("I")) {
			issue.cust_company = "IBM";
		} else {
			issue.cust_company = getEs().gASSOC_COMP;
		}
		issue.cust_email =  getEs().gEMAIL;
		issue.cust_name = getEs().gFIRST_NAME.trim() + " " + getEs().gLAST_NAME.trim();
		issue.cust_phone = getEs().gPHONE;
		issue.cust_project = getProj().getName();
		String sUniqueId = getEs().gUSERN + new Long(System.currentTimeMillis());
		issue.edge_problem_id  = sUniqueId;
		issue.last_userid = getEs().gUSERN;
		issue.problem_class = "Feedback";
		issue.problem_creator = getEs().gUSERN;
		issue.problem_state = "Submit";
		issue.problem_type = "Feedback";
		issue.seq_no = 1;
		issue.severity = "4";
		issue.field_C14 = getEs().gFIRST_NAME.trim();
		issue.field_C15 = getEs().gLAST_NAME.trim();
		//issue. setLastTime(new Timestamp(System.currentTimeMillis()));
		issue.title = getParams().get("feedback_title").toString();

		IssMWProcessor_Creator createMWproc = new IssMWProcessor_Creator();
		IssMWProcessor mwproc = createMWproc.factoryMethod(getEtsIssObjKey());
		mwproc.setIssue(issue);
		boolean bSuccess = mwproc.processRequest();

        //boolean bSuccess = ETSFeedbackDAO.insertFeedback(con,feedbackBean,getEs().gFIRST_NAME,getEs().gLAST_NAME);

		if (bSuccess) {
			//	sendUpdateEmailNotification(con,"Y",feedbackBean,getEs());
			sendUpdateEmailNotification(con,"Y",issue,getEs());
		}

        return bSuccess;

    }


    private boolean updateFeedback(Connection con) throws SQLException, Exception {


//        ETSFeedbackBean feedbackBean = new ETSFeedbackBean();
//
        String iSeqNoStr = getParams().get("cq_seq_no").toString();

        if (iSeqNoStr == null || iSeqNoStr.trim().equals("")) {
          iSeqNoStr = "0";
        } else {
            iSeqNoStr = iSeqNoStr.trim();
        }

        int iSeqNo = Integer.parseInt(iSeqNoStr) + 1;

//        feedbackBean.setApplicationId("ETS");
//        feedbackBean.setETSProjectId(ETSUtils.checkNull(getParams().get("proj").toString()));
//        feedbackBean.setComments(getParams().get("feedback_comments").toString());
//        feedbackBean.setCQTrackId(getParams().get("cq_trk_id").toString());
//        if (getEs().gDECAFTYPE.trim().equals("I")) {
//            feedbackBean.setCustCompany("IBM");
//        } else {
//            feedbackBean.setCustCompany(getEs().gASSOC_COMP);
//        }
//        feedbackBean.setCustEmail(getEs().gEMAIL);
//        feedbackBean.setCustName(getEs().gFIRST_NAME.trim() + " " + getEs().gLAST_NAME.trim());
//        feedbackBean.setCustPhone(getEs().gPHONE);
//        feedbackBean.setCustProjectName(getProj().getName());
//        feedbackBean.setEdgeProblemId(ETSUtils.checkNull(getParams().get("edge_problem_id").toString()));
//        feedbackBean.setLastUserId(getEs().gUSERN);
//        feedbackBean.setProblemClass("Feedback");
//        feedbackBean.setProblemCreator(getEs().gUSERN);
//        feedbackBean.setProblemState("Modify");
//        feedbackBean.setProblemType("Feedback");
//        feedbackBean.setSeqNo(iSeqNo);
//        feedbackBean.setSeverity("4");
//        feedbackBean.setLastTime(new Timestamp(System.currentTimeMillis()));
//        feedbackBean.setTitle(getParams().get("feedback_title").toString());


		ETSMWIssue issue = new ETSMWIssue();

		issue.application_id = "ETS";
		issue.ets_project_id = ETSUtils.checkNull(getParams().get("proj").toString());
		issue.comm_from_cust = getParams().get("feedback_comments").toString();
		issue.cq_trk_id =  getParams().get("cq_trk_id").toString();
		if (getEs().gDECAFTYPE.trim().equals("I")) {
			issue.cust_company = "IBM";
		} else {
			issue.cust_company = getEs().gASSOC_COMP;
		}
		issue.cust_email =  getEs().gEMAIL;
		issue.cust_name = getEs().gFIRST_NAME.trim() + " " + getEs().gLAST_NAME.trim();
		issue.cust_phone = getEs().gPHONE;
		issue.cust_project = getProj().getName();
		//String sUniqueId = getEs().gUSERN + new Long(System.currentTimeMillis());
		issue.edge_problem_id = ETSUtils.checkNull(getParams().get("edge_problem_id").toString());
		//issue.edge_problem_id  = sUniqueId;
		issue.last_userid = getEs().gUSERN;
		issue.problem_class = "Feedback";
		issue.problem_creator = getEs().gUSERN;
		issue.problem_state = "Modify";
		issue.problem_type = "Feedback";
		issue.seq_no = iSeqNo;
		issue.severity = "4";
		issue.field_C14 = getEs().gFIRST_NAME.trim();
		issue.field_C15 = getEs().gLAST_NAME.trim();
		//issue. setLastTime(new Timestamp(System.currentTimeMillis()));
		issue.title = getParams().get("feedback_title").toString();


		IssMWProcessor_Creator createMWproc = new IssMWProcessor_Creator();
		IssMWProcessor mwproc = createMWproc.factoryMethod(getEtsIssObjKey());
		mwproc.setIssue(issue);
		boolean bSuccess = mwproc.processRequest();

        //boolean bSuccess = ETSFeedbackDAO.updateFeedback(con,feedbackBean, getEs().gFIRST_NAME, getEs().gLAST_NAME);

		if (bSuccess) {
			sendUpdateEmailNotification(con,"U",issue,getEs());
		}

        return bSuccess;

    }
    private String displayMyFeedback(Connection con) throws SQLException, Exception {

        try {

            StringBuffer sBuffer = new StringBuffer("");

            sBuffer.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" height=\"18\" width=\"443\" class=\"subtitle\">Feedbacks submitted by me</td></tr></table>");
            sBuffer.append("<br />");

            sBuffer.append("<form name=\"cal_edit\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\" method=\"post\">");
            sBuffer.append("<input type=\"hidden\" name=\"linkid\" value=\"" + getSLink() + "\" />");
            sBuffer.append("<input type=\"hidden\" name=\"tc\" value=\"" + getTopCatId() + "\" />");
            sBuffer.append("<input type=\"hidden\" name=\"proj\" value=\"" + getProj().getProjectId() + "\" />");

            sBuffer.append("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"3\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
            sBuffer.append("</tr>");
            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"3\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
            sBuffer.append("</tr>");
            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"3\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
            sBuffer.append("</tr>");

            Vector vMyFeedBacks = ETSFeedbackDAO.getAllMyFeedbacks(con,"ETS", getProj().getProjectId(), getEs().gUSERN);

            if (vMyFeedBacks != null && vMyFeedBacks.size() > 0) {

                for (int i = 0; i < vMyFeedBacks.size(); i++) {

                    ETSFeedbackBean feedback = (ETSFeedbackBean) vMyFeedBacks.elementAt(i);

                    String sTempDate = feedback.getLastTime().toString();

                    String sDate = sTempDate.substring(5, 7) + "/" + sTempDate.substring(8, 10) + "/" + sTempDate.substring(0, 4);
                    String sTime = sTempDate.substring(11, 16);

                    String sHour = sTempDate.substring(11, 13);
                    String sMin = sTempDate.substring(14, 16);
                    String sAMPM = "AM";

                    if (Integer.parseInt(sHour) == 12) {
                        sHour = String.valueOf(Integer.parseInt(sHour));
                        sAMPM = "PM";
                    } else if (Integer.parseInt(sHour) > 12) {
                        sHour = String.valueOf(Integer.parseInt(sHour) - 12);
                        sAMPM = "PM";
                    }

                    if (i == 0) {
                        sBuffer.append("<tr>");
                        sBuffer.append("<th id=\"feed_title\" height=\"18\" width=\"200\" align=\"left\"><span class=\"small\">Title</span></th>");
                        sBuffer.append("<th id=\"feed_date\" height=\"18\" width=\"143\" align=\"left\"><span class=\"small\">Last updated on</span></th>");
                        sBuffer.append("<th id=\"feed_id\" height=\"18\" width=\"100\" align=\"left\"><span class=\"small\">Last updated by</span></th>");
                        sBuffer.append("</tr>");
                    }

                    if ((i % 2) == 0) {
                        sBuffer.append("<tr style=\"background-color: #eeeeee\">");
                    } else {
                        sBuffer.append("<tr >");
                    }

                    sBuffer.append("<td headers=\"feed_title\" height=\"18\" width=\"200\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + getTopCatId() + "&proj=" + getProj().getProjectId() + "&actionType=feedback&subactionType=viewfeedback&edge_problem_id=" + feedback.getEdgeProblemId() + "&linkid=" + getSLink() + "&from=myfeedback\">" + feedback.getTitle() + "</a></td>");
                    //sBuffer.append("<td headers=\"feed_date\" height=\"18\" width=\"143\" align=\"left\" valign=\"top\">" + sDate + " " + sHour + ":" + sMin + sAMPM.toLowerCase() + "</td>");
                    sBuffer.append("<td headers=\"feed_date\" height=\"18\" width=\"143\" align=\"left\" valign=\"top\">" + sDate + "</td>");
                    sBuffer.append("<td headers=\"feed_id\" height=\"18\" width=\"100\" align=\"left\" valign=\"top\"><span class=\"small\">" + ETSUtils.getUsersNameFromEdgeId(con,feedback.getLastUserId()) + "</span></td>");

                }

            } else {
                ETSProperties prop = new ETSProperties();
                String sMsg = prop.getSMyFeedbackDefalut();
                sBuffer.append("<td headers=\"feed_title\" colspan=\"3\"><b>" + sMsg + "</b></td>");
            }

            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"3\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
            sBuffer.append("</tr>");
            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"3\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
            sBuffer.append("</tr>");
            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"3\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
            sBuffer.append("</tr>");

            sBuffer.append("<tr valign=\"top\"><td colspan=\"3\" align=\"left\"><br />");

			ETSCat cat = ETSDatabaseManager.getCat(getTopCatId(),getProj().getProjectId());
			if (cat.getViewType() == Defines.FEEDBACK_VT) {
				sBuffer.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"120\" valign=\"top\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + getProj().getProjectId() + "&tc=" + getTopCatId() + "&linkid=" + getSLink() + "&actionType=feedback&subactionType=welcome\"><img src=\"" + Defines.BUTTON_ROOT + "cancel.gif\" width=\"120\" height=\"21\" alt=\"Cancel\" border=\"0\" /></a></td></tr></table>");
			} else {
				sBuffer.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"120\" valign=\"top\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "EtsIssFilterCntrlServlet.wss?tc=" + getTopCatId() + "&proj=" + getProj().getProjectId() + "&linkid=" + getSLink() + "&istyp=iss&opn=10\" ><img src=\"" + Defines.BUTTON_ROOT + "cancel.gif\" width=\"120\" height=\"21\" alt=\"Cancel\" border=\"0\" /></a></td></tr></table>");
			}

            sBuffer.append("</td></tr>");

            sBuffer.append("</table>");
            sBuffer.append("<br />");

            sBuffer.append("</form>");

            return sBuffer.toString();

        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }

    }

    private String displayAllProjectFeedback(Connection con) throws SQLException, Exception {

        try {

            StringBuffer sBuffer = new StringBuffer("");

            sBuffer.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" height=\"18\" width=\"443\" class=\"subtitle\">Feedback submitted by team members</td></tr></table>");
            sBuffer.append("<br />");

            sBuffer.append("<form name=\"cal_edit\" action=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss\" method=\"post\">");
            sBuffer.append("<input type=\"hidden\" name=\"linkid\" value=\"" + getSLink() + "\" />");
            sBuffer.append("<input type=\"hidden\" name=\"tc\" value=\"" + getTopCatId() + "\" />");
            sBuffer.append("<input type=\"hidden\" name=\"proj\" value=\"" + getProj().getProjectId() + "\" />");

            sBuffer.append("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"3\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
            sBuffer.append("</tr>");
            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"3\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
            sBuffer.append("</tr>");

            Vector vMyFeedBacks = ETSFeedbackDAO.getAllProjectFeedbacks(con,"ETS", getProj().getProjectId());

            if (vMyFeedBacks != null && vMyFeedBacks.size() > 0) {

                for (int i = 0; i < vMyFeedBacks.size(); i++) {

                    ETSFeedbackBean feedback = (ETSFeedbackBean) vMyFeedBacks.elementAt(i);

                    String sTempDate = feedback.getLastTime().toString();

                    String sDate = sTempDate.substring(5, 7) + "/" + sTempDate.substring(8, 10) + "/" + sTempDate.substring(0, 4);
                    String sTime = sTempDate.substring(11, 16);

                    String sHour = sTempDate.substring(11, 13);
                    String sMin = sTempDate.substring(14, 16);
                    String sAMPM = "AM";

                    if (Integer.parseInt(sHour) == 12) {
                        sHour = String.valueOf(Integer.parseInt(sHour));
                        sAMPM = "PM";
                    } else if (Integer.parseInt(sHour) > 12) {
                        sHour = String.valueOf(Integer.parseInt(sHour) - 12);
                        sAMPM = "PM";
                    }

                    if (i == 0) {
                        sBuffer.append("<tr>");
                        sBuffer.append("<th id=\"feed_title\" height=\"18\" width=\"200\" align=\"left\"><span class=\"small\">Title</span></th>");
                        sBuffer.append("<th id=\"feed_date\" height=\"18\" width=\"143\" align=\"left\"><span class=\"small\">Last updated on</span></th>");
                        sBuffer.append("<th id=\"feed_id\" height=\"18\" width=\"100\" align=\"left\"><span class=\"small\">Last updated by</span></th>");
                        sBuffer.append("</tr>");
                    }

                    if ((i % 2) == 0) {
                        sBuffer.append("<tr style=\"background-color: #eeeeee\">");
                    } else {
                        sBuffer.append("<tr >");
                    }

                    sBuffer.append("<td headers=\"feed_title\" height=\"18\" width=\"200\" align=\"left\" valign=\"top\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?tc=" + getTopCatId() + "&proj=" + getProj().getProjectId() + "&actionType=feedback&subactionType=viewfeedback&edge_problem_id=" + feedback.getEdgeProblemId() + "&linkid=" + getSLink() + "&from=allfeedback\">" + feedback.getTitle() + "</a></td>");
                    //sBuffer.append("<td headers=\"feed_date\" height=\"18\" width=\"143\" align=\"left\" valign=\"top\">" + sDate + " " + sHour + ":" + sMin + sAMPM.toLowerCase() + "</td>");
                    sBuffer.append("<td headers=\"feed_date\" height=\"18\" width=\"143\" align=\"left\" valign=\"top\">" + sDate + "</td>");
                    sBuffer.append("<td headers=\"feed_id\" height=\"18\" width=\"100\" align=\"left\" valign=\"top\"><span class=\"small\">" + ETSUtils.getUsersNameFromEdgeId(con,feedback.getLastUserId()) + "</span></td>");

                }

            } else {
                ETSProperties prop = new ETSProperties();
                String sMsg = prop.getSTeamFeedbackDefalut();
                sBuffer.append("<td headers=\"feed_title\" colspan=\"3\"><b>" + sMsg + "</b></td>");
            }

            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"3\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
            sBuffer.append("</tr>");
            sBuffer.append("<tr valign=\"top\">");
            sBuffer.append("<td colspan=\"3\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
            sBuffer.append("</tr>");

            sBuffer.append("<tr valign=\"top\"><td colspan=\"3\" align=\"left\"><br />");

			ETSCat cat = ETSDatabaseManager.getCat(getTopCatId(),getProj().getProjectId());
			if (cat.getViewType() == Defines.FEEDBACK_VT) {
				sBuffer.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"top\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + getProj().getProjectId() + "&tc=" + getTopCatId() + "&linkid=" + getSLink() + "&actionType=feedback&subactionType=welcome\"><img src=\"" + Defines.BUTTON_ROOT + "cancel.gif\" width=\"120\" height=\"21\" alt=\"Cancel\" border=\"0\" /></a></td></tr></table>");
			} else {
				sBuffer.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"top\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "EtsIssFilterCntrlServlet.wss?tc=" + getTopCatId() + "&proj=" + getProj().getProjectId() + "&linkid=" + getSLink() + "&istyp=iss&opn=10\" ><img src=\"" + Defines.BUTTON_ROOT + "cancel.gif\" width=\"120\" height=\"21\" alt=\"Cancel\" border=\"0\" /></a></td></tr></table>");
			}


            sBuffer.append("</td></tr>");

            sBuffer.append("</table>");
            sBuffer.append("<br />");

            sBuffer.append("</form>");

            return sBuffer.toString();

        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }

    }

	private String displayIssueReportFilter(Connection con, String error) throws SQLException, Exception {

	    try {

	        StringBuffer sBuffer = new StringBuffer("");

			if (!error.equalsIgnoreCase("")) {

				sBuffer.append("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
				sBuffer.append("<tr valign=\"top\">");
				sBuffer.append("<td ><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				sBuffer.append("</tr>");
				sBuffer.append("<tr valign=\"top\">");
				sBuffer.append("<td style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
				sBuffer.append("</tr>");
				sBuffer.append("<tr valign=\"top\">");
				sBuffer.append("<td ><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				sBuffer.append("</tr>");
				sBuffer.append("<tr valign=\"top\">");
				sBuffer.append("<td ><span class=\"small\"><span style=\"color:#ff3333\">" + error + "</span></span></td>");
				sBuffer.append("</tr>");
				sBuffer.append("<tr valign=\"top\">");
				sBuffer.append("<td ><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				sBuffer.append("</tr>");
				sBuffer.append("<tr valign=\"top\">");
				sBuffer.append("<td style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
				sBuffer.append("</tr>");
				sBuffer.append("<tr valign=\"top\">");
				sBuffer.append("<td ><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				sBuffer.append("</tr>");
				sBuffer.append("</table>");

			}


			ETSMetricsResultObj rep = new ETSMetricsResultObj();

			// to  make all columns selected as default...
			if (getRequest().getParameter("report.x") == null && getRequest().getParameter("report.y") == null) {
				rep.setOption2("");
			} else {
				rep.setOption2("report");		// set something here...
			}

			if (rep.getOption2().equals("")) {
				if (getRequest().getParameter("download.x") == null && getRequest().getParameter("download.y") == null) {
					rep.setOption2("");
				} else {
					rep.setOption2("report");		// set something here...
				}
			}

			String[] issList = new String[]{"All values","Submitted","Assigned","Rejected","Resolved","Closed","Withdrawn"};
			String[] issStatus = getRequest().getParameterValues("issStatus");

			rep.setColumnsToShow(ETSMetricsReports.getColumnsToShowSetting(rep,REPORT_ID));

			rep.setSelectedColsToShow(getSelColParameters(rep));

			rep.setSelectedIssueStatus(issStatus);

			String issStatusStr = "Issue status:";

			sBuffer.append("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");

			sBuffer.append("<tr><td valign=\"top\" width=\"150\"><label for=\"issStatus\"><b>" + issStatusStr + "</b></label></td>");
			sBuffer.append("<td align=\"left\" valign=\"top\"><select id=\"issStatus\" name=\"issStatus\" multiple=\"multiple\" size=\"4\">");

			// hardcoded as in metrics reports also...

			System.out.println("\n\n\n\n\n\n\n\n" + rep.getSelectedIssueStatus().size() + "\n\n\n\n\n\n\n\n\n");


			for (int i = 0; i < issList.length; i++) {
				String s = "";
				if (rep.getSelectedIssueStatus().size() > 0 && rep.getSelectedIssueStatus().contains(issList[i])) {
					s = "selected=\"selected\"";
				} else if (rep.getOption2().equals("") && i == 0) {
					s = "selected=\"selected\"";
				}

				System.out.println("\n\n\n\n\n\n\n\nSSSSSSSSSSSSSSSSSs" + s + "s\n\n\n\n\n\n\n\n\n");

				sBuffer.append("<option value=\"" + issList[i] + "\" " + s + ">" + issList[i] + "</option>");
			}
			sBuffer.append("</select></td></tr>");
			sBuffer.append("<tr><td colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");

			String sel_str = "";

			String[] months = new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec" };

 			// date from and to...

			String bAllDates = ETSUtils.checkNull(getRequest().getParameter("alldates"));
			String frommonth = ETSUtils.checkNull(getRequest().getParameter("frommonth"));
			String fromday = ETSUtils.checkNull(getRequest().getParameter("fromday"));
			String fromyear = ETSUtils.checkNull(getRequest().getParameter("fromyear"));
			String tomonth = ETSUtils.checkNull(getRequest().getParameter("tomonth"));
			String today = ETSUtils.checkNull(getRequest().getParameter("today"));
			String toyear = ETSUtils.checkNull(getRequest().getParameter("toyear"));

 			Timestamp currentdate = new Timestamp(System.currentTimeMillis());

 			int imonth = new Integer(currentdate.toString().substring(5, 7)).intValue() - 1;

			String smonth = String.valueOf(imonth);
			String sday =  currentdate.toString().substring(8, 10);
			String syear = currentdate.toString().substring(0, 4);

 			if (frommonth.equalsIgnoreCase("")) {
 				frommonth = smonth;
 			}

			if (fromday.equalsIgnoreCase("")) {
				fromday = sday;
			}

			if (fromyear.equalsIgnoreCase("")) {
				fromyear = syear;
			}

			if (tomonth.equalsIgnoreCase("")) {
				tomonth = smonth;
			}

			if (today.equalsIgnoreCase("")) {
				today = sday;
			}

			if (toyear.equalsIgnoreCase("")) {
				toyear = syear;
			}

 			rep.setFromMonth(frommonth);
 			rep.setFromDay(fromday);
 			rep.setFromYear(fromyear);
 			rep.setAllDates(bAllDates);
 			rep.setToDay(today);
 			rep.setToYear(toyear);
 			rep.setToMonth(tomonth);

			rep.setFromDate();
			rep.setToDate();

			Calendar cal = Calendar.getInstance();
			String fromStr = "From:";

			sBuffer.append("<tr><td valign=\"top\"><label for=\"fromdate\"><b>" + fromStr + "</b></label></td>");
			sBuffer.append("<td valign=\"top\"><select name=\"frommonth\" id=\"fromdate\" class=\"iform\">");

			for (int m = 0; m < 12; m++) {
				sel_str = "";
				if (rep.getFromMonth().equals(String.valueOf(m)))
					sel_str = "selected=\"selected\"";
				sBuffer.append("<option value=\"" + m + "\" " + sel_str + ">" + months[m] + "</option>");
			}
			sBuffer.append("</select>\n");

			sBuffer.append("<select name=\"fromday\" id=\"fromdate\" class=\"iform\">");
			//sBuffer.append("<option value=\"0\" selected=\"selected\">&nbsp;</option>");
			for (int d = 1; d <= 31; d++) {
				sel_str = "";
				if (rep.getFromDay().equals(String.valueOf(d)))
					sel_str = "selected=\"selected\"";
				sBuffer.append("<option value=\"" + d + "\" " + sel_str + ">" + d + "</option>");
			}
			sBuffer.append("</select>\n");

			int year = (cal.get(Calendar.YEAR));
			sBuffer.append("<select name=\"fromyear\" id=\"fromdate\" class=\"iform\">");
			//sBuffer.append("<option value=\"0\" selected=\"selected\">&nbsp;</option>");
			for (int c = 2002; c <= year; c++) {
				sel_str = "";
				if (rep.getFromYear().equals(String.valueOf(c)))
					sel_str = "selected=\"selected\"";
				sBuffer.append("<option value=\"" + c + "\" " + sel_str + ">" + c + "</option>");
			}
			sBuffer.append("</select>\n");
			sBuffer.append("<img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"15\" height=\"1\" alt=\"\" />");
			if (rep.getAllDates() || rep.getOption2().equals(""))
				sel_str = "checked=\"checked\"";
			sBuffer.append("<input type=\"checkbox\" name=\"alldates\" value=\"alldates\" " + sel_str + " id=\"alldates\" />");
			sBuffer.append("<label for=\"alldates\"><b>All Dates</b></label></td></tr>");

			String toStr = "To:";

			sBuffer.append("<tr><td colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
			sBuffer.append("<tr><td valign=\"top\"><label for=\"todate\"><b>" + toStr + "</b></label></td>");
			sBuffer.append("<td valign=\"top\"><select name=\"tomonth\" id=\"todate\" class=\"iform\">");
			//sBuffer.append("<option value=\"-1\" selected=\"selected\">&nbsp;</option>");
			for (int m = 0; m < 12; m++) {
				sel_str = "";
				if (rep.getToMonth().equals(String.valueOf(m)))
					sel_str = "selected=\"selected\"";
				sBuffer.append("<option value=\"" + m + "\" " + sel_str + ">" + months[m] + "</option>");
			}
			sBuffer.append("</select>\n");

			sBuffer.append("<select name=\"today\" id=\"todate\" class=\"iform\">");
			//sBuffer.append("<option value=\"0\" selected=\"selected\">&nbsp;</option>");
			for (int d = 1; d <= 31; d++) {
				sel_str = "";
				if (rep.getToDay().equals(String.valueOf(d)))
					sel_str = "selected=\"selected\"";
				sBuffer.append("<option value=\"" + d + "\" " + sel_str + ">" + d + "</option>");
			}
			sBuffer.append("</select>\n");

			sBuffer.append("<select name=\"toyear\" id=\"todate\" class=\"iform\">");
			//sBuffer.append("<option value=\"0\" selected=\"selected\">&nbsp;</option>");
			for (int c = 2002; c <= year; c++) {
				sel_str = "";
				if (rep.getToYear().equals(String.valueOf(c)))
					sel_str = "selected=\"selected\"";
				sBuffer.append("<option value=\"" + c + "\" " + sel_str + ">" + c + "</option>");
			}
			sBuffer.append("</select>\n");
			sBuffer.append("</td></tr>");
			sBuffer.append("<tr><td colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");

 			// end of date from and to...

 			sBuffer.append("</table>");

			sBuffer.append("<br /><br />");

//			ETSCat cat = ETSDatabaseManager.getCat(getTopCatId(),getProj().getProjectId());
//			sBuffer.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"top\" align=\"left\"><input type=\"image\" name=\"report\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"Submit\" border=\"0\" /></td><td headers=\"\" valign=\"top\" align=\"left\">&nbsp;&nbsp;&nbsp;<input type=\"image\" name=\"download\" src=\"" + Defines.BUTTON_ROOT + "download_now.gif\" width=\"120\" height=\"21\" alt=\"Download now\" border=\"0\" /></td></tr></table>");
//
//	        sBuffer.append("<br />");

	        //sBuffer.append("</form>");

	        return sBuffer.toString();

	    } catch (Exception e) {
	        throw e;
	    }

	}

		private String displayIssueReportColumns(Connection con) throws SQLException, Exception {

		    try {


		        StringBuffer sBuffer = new StringBuffer("");

				ETSMetricsResultObj rep = new ETSMetricsResultObj();

				String[] issList = new String[]{"All values","Submitted","Assigned","Rejected","Resolved","Closed","Withdrawn"};
				String[] issStatus = getRequest().getParameterValues("issStatus");

				rep.setColumnsToShow(ETSMetricsReports.getColumnsToShowSetting(rep,REPORT_ID));

				rep.setSelectedColsToShow(getSelColParameters(rep));

				rep.setSelectedIssueStatus(issStatus);

				String issStatusStr = "Issue status:";

				sBuffer.append("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");

				// to  make all columns selected as default...
				if (getRequest().getParameter("report.x") == null && getRequest().getParameter("report.y") == null) {
					rep.setOption2("");
				} else {
					rep.setOption2("report");		// set something here...
				}

				sBuffer.append(ETSMetricsPrintResults.printColumns(rep));

	 			sBuffer.append("</table>");

				sBuffer.append("<br /><br />");

				ETSCat cat = ETSDatabaseManager.getCat(getTopCatId(),getProj().getProjectId());
				sBuffer.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"top\" align=\"left\"><input type=\"image\" name=\"report\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"Submit\" border=\"0\" /></td><td headers=\"\" valign=\"top\" align=\"left\">&nbsp;&nbsp;&nbsp;<input type=\"image\" name=\"download\" src=\"" + Defines.BUTTON_ROOT + "download_now.gif\" width=\"120\" height=\"21\" alt=\"Download now\" border=\"0\" /></td>");
				sBuffer.append("<td align=\"left\" align=\"left\" valign=\"top\">&nbsp;&nbsp;</td>");
				sBuffer.append("<td align=\"left\" align=\"left\" valign=\"top\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"25\" valign=\"top\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + getProj().getProjectId() + "&tc=" + getTopCatId() + "&linkid=" + getSLink() + "&actionType=feedback&subactionType=welcome\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></a></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + getProj().getProjectId() + "&tc=" + getTopCatId() + "&linkid=" + getSLink() + "&actionType=feedback&subactionType=welcome\">Cancel</a></td></tr></table></td>");
				sBuffer.append("</tr></table>");

		        sBuffer.append("<br />");

		        //sBuffer.append("</form>");

		        return sBuffer.toString();

		    } catch (SQLException e) {
		        throw e;
		    } catch (Exception e) {
		        throw e;
		    }

		}

		private String displayIssueReport(Connection con, boolean download) throws SQLException, Exception {


		    try {

		        StringBuffer sBuffer = new StringBuffer("");

				ETSMetricsResultObj rep = new ETSMetricsResultObj();

				String[] issStatus = getRequest().getParameterValues("issStatus");

				rep.setReportId(REPORT_ID);
				rep.setReportName(REPORT_NAME);

				rep.setSelectedIssueStatus(issStatus);

				String bAllDates = ETSUtils.checkNull(getRequest().getParameter("alldates"));
				String frommonth = ETSUtils.checkNull(getRequest().getParameter("frommonth"));
				String fromday = ETSUtils.checkNull(getRequest().getParameter("fromday"));
				String fromyear = ETSUtils.checkNull(getRequest().getParameter("fromyear"));
				String tomonth = ETSUtils.checkNull(getRequest().getParameter("tomonth"));
				String today = ETSUtils.checkNull(getRequest().getParameter("today"));
				String toyear = ETSUtils.checkNull(getRequest().getParameter("toyear"));

	 			rep.setFromMonth(frommonth);
	 			rep.setFromDay(fromday);
	 			rep.setFromYear(fromyear);
	 			rep.setAllDates(bAllDates);
	 			rep.setToDay(today);
	 			rep.setToYear(toyear);
	 			rep.setToMonth(tomonth);

	 			rep.setFromDate();
	 			rep.setToDate();

	 			rep.setSelectedComps(new String[]{getProj().getCompany()});
	 			rep.setSelectedClientDes(new String[]{"All values"});
	 			rep.setSelectedInds(new String[]{"All values"});
	 			rep.setSelectedGeos(new String[]{"All values"});

				rep.setSelectedIntExt("0");

				rep.setColumnsToShow(ETSMetricsReports.getColumnsToShowSetting(rep,REPORT_ID));

				rep.setSelectedColsToShow(getSelColParameters(rep));

				ETSParams params = new ETSParams();
				params.setRequest(getRequest());
				params.setExecutive(false);
				params.setSuperAdmin(true); 		// setting all uses as admin...

				rep.setSearchResult(ETSMetricsDAO.filterIssueActivityDetails(rep,params,con));

	 			//rep.setSearchResult(filterIssueActivityDetails(rep,true,con));

				sBuffer.append(ETSMetricsPrintResults.printSearchResults(rep,download));

	 			if (!download) {

			        sBuffer.append("<br />");
			        sBuffer.append("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>");
					sBuffer.append("<td align=\"left\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"16\" valign=\"middle\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + getProj().getProjectId() + "&tc=" + getTopCatId() + "&linkid=" + getSLink() + "&actionType=feedback&subactionType=welcome\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" alt=\"Back to feedback\" border=\"0\" /></a></td><td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + getProj().getProjectId() + "&tc=" + getTopCatId() + "&linkid=" + getSLink() + "&actionType=feedback&subactionType=welcome\">Back to feedback</a></td></tr></table></td>");
					sBuffer.append("</tr></table>");
		    	} else {
					getResponse().setHeader("Content-disposition", "attachment; filename=" + rep.getEncReportName() + ".csv");
					//response.setHeader("Content-disposition","attachment; filename=metrics.csv");
					getResponse().setHeader("Content-Type", "application/octet-stream");
					//response.setContentType("application/csv");
					getResponse().setContentLength(sBuffer.length());
		    	}
		        return sBuffer.toString();

		    } catch (SQLException e) {
		        throw e;
		    } catch (Exception e) {
		        throw e;
		    }

		}

    /**
     * Method validateMeeting.
     * @param params
     * @return boolean
     */
    boolean validateFeedback() throws Exception {

        try {

            String sTitle = ETSUtils.checkNull(getParams().get("feedback_title").toString());
            if (sTitle.trim().equals("")) {
                return false;
            }

            String sComments = ETSUtils.checkNull(getParams().get("feedback_comments").toString());
            if (sComments.trim().equals("")) {
                return false;
            }

            return true;

        } catch (Exception e) {
            throw e;
        }

    }

    /**
     * Method sendEmailNotification.
     * @param conn
     * @param sInsFlag
     * @param sProjectId
     * @param sCalendarId
     * @param sCalendarEntry
     * @throws SQLException
     * @throws Exception
     */
    private void sendUpdateEmailNotification(Connection conn, String sInsFlag, ETSMWIssue issue, EdgeAccessCntrl es) throws SQLException, Exception {

        try {

            StringBuffer sEmailStr = new StringBuffer("");

            String sTempDate = new Timestamp(System.currentTimeMillis()).toString();

            String sDate = sTempDate.substring(5, 7) + "/" + sTempDate.substring(8, 10) + "/" + sTempDate.substring(0, 4);
            String sProjectId = issue.ets_project_id;
            ETSProj proj = ETSUtils.getProjectDetails(conn,sProjectId);

			UnbrandedProperties prop = PropertyFactory.getProperty(proj.getProjectType());

            String sEdgeProblemId = issue.edge_problem_id;

            String sProjProposal = proj.getProjectOrProposal();

            if (sProjProposal.trim().equalsIgnoreCase("P")) {
                sProjProposal = "project";
            } else if (sProjProposal.trim().equalsIgnoreCase("O")) {
                sProjProposal = "proposal";
            } else {
				sProjProposal = prop.getClientVoiceTitle();
            }

            String sSubject = issue.title;
            String sEmailSubject = "";

            if (sInsFlag.trim().equals("Y")) {
                sEmailSubject = ETSUtils.formatEmailSubject(prop.getAppName() + " - Feedback submitted: " + sSubject);
            } else {
                sEmailSubject = ETSUtils.formatEmailSubject(prop.getAppName() + " - Feedback modified: " + sSubject);
            }
            
            if (sInsFlag.trim().equals("Y")) {
				if (proj.getProjectOrProposal().trim().equalsIgnoreCase("C")) {
                	sEmailStr.append("New feedback has been submitted on " + prop.getAppName() + " for \n" + sProjProposal + ": " + proj.getCompany() + " \n\n");
				} else {
					sEmailStr.append("New feedback has been submitted on " + prop.getAppName() + " for \n" + sProjProposal + ": " + proj.getName() + " \n\n");
				}
            } else {
                if (proj.getProjectOrProposal().equalsIgnoreCase("C")) {
                    sEmailStr.append("More comments have been added for feedback on " + prop.getAppName() + " for \n" + sProjProposal + ": " + proj.getCompany() + " \n\n");
                } else {
                    sEmailStr.append("More comments have been added for feedback on " + prop.getAppName() + " for \n" + sProjProposal + ": " + proj.getName() + " \n\n");
                }
            }

			int iTC = 0;

			ETSCat cat = ETSDatabaseManager.getCat(getTopCatId(),getProj().getProjectId());
			if (cat.getViewType() == Defines.FEEDBACK_VT) {
				iTC = ETSDatabaseManager.getTopCatId(sProjectId,Defines.FEEDBACK_VT);
			} else {
				iTC = ETSDatabaseManager.getTopCatId(sProjectId,Defines.ISSUES_CHANGES_VT);
			}

            sEmailStr.append("The details of the feedback are as follows: \n\n");

            sEmailStr.append("===============================================================\n");

            sEmailStr.append("  Title:          " + ETSUtils.formatEmailStr(sSubject) + "\n");
            sEmailStr.append("  Date:           " + sDate + " (mm/dd/yyyy) \n");
            sEmailStr.append("  Comments:       " + ETSUtils.formatEmailStr(issue.comm_from_cust) + " \n");
            sEmailStr.append("  By:             " + ETSUtils.getUsersNameFromEdgeId(conn,issue.last_userid) + " \n\n");

            sEmailStr.append("To view the feedback, click on the following URL:\n");
            sEmailStr.append(Global.getUrl("ets/ETSProjectsServlet.wss") + "?proj=" + sProjectId + "&tc=" + iTC + "&actionType=feedback&subactionType=viewfeedback&edge_problem_id=" + sEdgeProblemId + "&from=allfeedback&linkid=" + prop.getLinkID() + "");

			//sEmailStr.append(prop.getEmailFooter());
            //v2sagar for common footer
            sEmailStr.append(CommonEmailHelper.getEmailFooter(prop.getAppName()));

            String sToList = "";

            Vector vOwner = ETSDatabaseManager.getUsersByProjectPriv(issue.ets_project_id,Defines.OWNER,conn);

            if (vOwner != null && vOwner.size() > 0) {
                 ETSUser user = (ETSUser) vOwner.elementAt(0);
                 sToList = ETSUtils.getUserEmail(conn,user.getUserId());
            }

            boolean bSent = false;

            if (!sToList.trim().equals("")) {
                bSent = ETSUtils.sendEMail(es.gEMAIL,sToList,es.gEMAIL,Global.mailHost,sEmailStr.toString(),sEmailSubject,es.gEMAIL);

                if (sInsFlag.trim().equals("Y")) {
                    ETSUtils.insertEmailLog(conn,"FEEDBACK",issue.edge_problem_id,"ADD",es.gEMAIL,sProjectId,sEmailSubject,sToList,"");
                } else {
                    ETSUtils.insertEmailLog(conn,"FEEDBACK",issue.edge_problem_id,"MODIFY",es.gEMAIL,sProjectId,sEmailSubject,sToList,"");
                }
            }

        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }

    }

	private Vector getSelColParameters(ETSMetricsResultObj obj){
		Vector v = new Vector();

		Vector cols = obj.getColumnsToShow();
		for (int i=0;i<cols.size();i++){
			String[] s = (String[])cols.elementAt(i);
			if ((getParameter("colsel_"+s[0])!="")){
				v.addElement(s[0]);
			}
		}

		return v;
	}

	private String getParameter(String key) {
		  String value = getRequest().getParameter(key);

		  if (value == null) {
			System.out.println(key+" = null");
			  return "";
		  } else {
			  return value;
		  }
	  }

		private String validateReportFilter() throws SQLException, Exception {

		    try {

		        StringBuffer error = new StringBuffer("");


				String[] issStatus = getRequest().getParameterValues("issStatus");

				if (issStatus == null) {
					error.append("Please select a value for <b>Issue status</b>.<br />");
				}


				String bAllDates = ETSUtils.checkNull(getRequest().getParameter("alldates"));

				if (bAllDates.equalsIgnoreCase("")) {
					String frommonth = ETSUtils.checkNull(getRequest().getParameter("frommonth"));
					String fromday = ETSUtils.checkNull(getRequest().getParameter("fromday"));
					String fromyear = ETSUtils.checkNull(getRequest().getParameter("fromyear"));
					String tomonth = ETSUtils.checkNull(getRequest().getParameter("tomonth"));
					String today = ETSUtils.checkNull(getRequest().getParameter("today"));
					String toyear = ETSUtils.checkNull(getRequest().getParameter("toyear"));

		 			if (!frommonth.equalsIgnoreCase("") && !fromday.equalsIgnoreCase("") && !fromyear.equalsIgnoreCase("") && !tomonth.equalsIgnoreCase("") && !today.equalsIgnoreCase("") && !toyear.equalsIgnoreCase("")) {
						Timestamp timeStart = Timestamp.valueOf(fromyear + "-" + frommonth+ "-" + fromday+ " 00:00:00.000000000");
						Timestamp timeEnd = Timestamp.valueOf(toyear + "-" + tomonth+ "-" + today + " 01:00:00.000000000");

						if (timeStart.before(timeEnd)) {
						} else {
							error.append("From date should be less than To date.<br />");
						}
		 			}

					Calendar tcal = Calendar.getInstance();

					tcal.set(Calendar.YEAR,Integer.parseInt(fromyear));
					tcal.set(Calendar.MONTH,Integer.parseInt(frommonth));
					int iMaxDaysInMonth =  tcal.getActualMaximum(Calendar.DAY_OF_MONTH);
					int iMinDaysInMonth = tcal.getActualMinimum(Calendar.DAY_OF_MONTH);

					if(iMinDaysInMonth<=Integer.parseInt(fromday) && Integer.parseInt(fromday)<=iMaxDaysInMonth){
					 } else {
					 	error.append("From date selected is invalid. Please select a valid date.<br />");
					}

					tcal.set(Calendar.YEAR,Integer.parseInt(toyear));
					tcal.set(Calendar.MONTH,Integer.parseInt(tomonth));
					iMaxDaysInMonth =  tcal.getActualMaximum(Calendar.DAY_OF_MONTH);
					iMinDaysInMonth = tcal.getActualMinimum(Calendar.DAY_OF_MONTH);

					if(iMinDaysInMonth<=Integer.parseInt(today) && Integer.parseInt(today)<=iMaxDaysInMonth){
					 } else {
						error.append("To date selected is invalid. Please select a valid date.<br />");
					}

				}

				ETSMetricsResultObj rep = new ETSMetricsResultObj();

				rep.setColumnsToShow(ETSMetricsReports.getColumnsToShowSetting(rep,REPORT_ID));
				Vector v = getSelColParameters(rep);
				if (v.size() <= 0) {
					error.append("Please select atleast one column for the report.<br />");
				}

				return error.toString();

		    } catch (Exception e) {
		        throw e;
		    }

		}

} //end of class
