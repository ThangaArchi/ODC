/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                           */
/*     (C)Copyright IBM Corp. 2003-2005                                     */
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
 * Created on Oct 3, 2005
 * @author v2sathis@us.ibm.com
 */
 
package oem.edge.ets.fe.brand;

import java.util.Enumeration;
import java.util.ResourceBundle;

import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;


/**
 * @author v2sathis
 *
 */
public class ETSUnbrandedProperties extends UnbrandedProperties {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2005";
	public static final String CLASS_VERSION = "1.5";
	
	private static Log logger = EtsLogger.getLogger(ETSUnbrandedProperties.class);
	
	
	public ETSUnbrandedProperties() {
		loadProperties();
	}
	
	protected void loadProperties() {

		try {

			ResourceBundle rb = ResourceBundle.getBundle("oem.edge.ets.fe.etsbrand");
			Enumeration e = rb.getKeys();

			this.MessageText = rb.getString("ets.message_default").trim();
			this.PrevMessageText = rb.getString("ets.message_prev_default").trim();
			this.VisitorMessageText = rb.getString("ets.visitor_message_default").trim();
			this.VisitorMessagePrevText = rb.getString("ets.visitor_message_prev").trim();

			this.MeetingText = rb.getString("ets.meeting_default").trim();
			this.PrevMeetingText = rb.getString("ets.meeting_prev_default").trim();
			this.VisitorMeetingText = rb.getString("ets.visitor_meeting_default").trim();
			this.VisitorMeetingPrevText = rb.getString("ets.visitor_meeting_prev").trim();
			
			this.EventText = rb.getString("ets.event_default").trim();
			this.PrevEventText = rb.getString("ets.event_prev_default").trim();
			this.VisitorEventText = rb.getString("ets.visitor_event_default").trim();
			this.VisitorEventPrevText = rb.getString("ets.visitor_event_prev").trim();
			
			this.ProjectTitle = rb.getString("ets.project_title").trim();
			this.ProposalTitle = rb.getString("ets.proposal_title").trim();
			this.ClientVoiceTitle = rb.getString("ets.client_voice_title").trim();
			this.MetricsTitle = rb.getString("ets.metrics_title").trim();
			
			this.LinkId = rb.getString("ets.linkid").trim();
			
			this.ContactUsURL = rb.getString("ets.contactus_url").trim();
			this.LandingPageURL = rb.getString("ets.landing_page_url").trim();
			this.AdminEmailID = rb.getString("ets.admin_email_id").trim();
			this.POCRequestEmailID = rb.getString("ets.pocrequest_email_id").trim();
			
			this.SwitchWorkspaceName = rb.getString("ets.switch_header_name").trim();
			
			this.EmailFooter = rb.getString("ets.email_footer").trim();
			
			this.AppName = rb.getString("ets.app_name").trim();
			
			this.UnauthorizedURL = rb.getString("ets.unauthorized_url").trim();
				
			this.BrandAbbrvn = rb.getString("ets.brand_abbrvn").trim();
			this.BrandExpsn = rb.getString("ets.brand_expsn").trim();
			this.HtmlPageTitle=rb.getString("ets.html_pg_title").trim();
			this.HtmlPageHead=rb.getString("ets.html_pg_head").trim();
			
			this.ProcReqstServletURL=rb.getString("ets.proc_reqst_servlet").trim();
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(this,e);
			logger.error("*** ERROR (FATAL) *** Property(s) for Text message not found on etsbrand.properties file. Please update/add properties." + e.getMessage());
		}
	}
		
}
