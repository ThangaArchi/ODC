/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                           */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
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
public class AICUnbrandedProperties extends UnbrandedProperties {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2005";
	public static final String CLASS_VERSION = "1.5";
	
	private static Log logger = EtsLogger.getLogger(AICUnbrandedProperties.class);
	
			
	public AICUnbrandedProperties() {
		loadProperties();
	}
	
	protected void loadProperties() {

		try {

			ResourceBundle rb = ResourceBundle.getBundle("oem.edge.ets.fe.aicbrand");
			Enumeration e = rb.getKeys();


			this.MessageText = rb.getString("aic.message_default").trim();
			this.PrevMessageText = rb.getString("aic.message_prev_default").trim();
			this.VisitorMessageText = rb.getString("aic.visitor_message_default").trim();
			this.VisitorMessagePrevText = rb.getString("aic.visitor_message_prev").trim();

			this.MeetingText = rb.getString("aic.meeting_default").trim();
			this.PrevMeetingText = rb.getString("aic.meeting_prev_default").trim();
			this.VisitorMeetingText = rb.getString("aic.visitor_meeting_default").trim();
			this.VisitorMeetingPrevText = rb.getString("aic.visitor_meeting_prev").trim();
			
			this.EventText = rb.getString("aic.event_default").trim();
			this.PrevEventText = rb.getString("aic.event_prev_default").trim();
			this.VisitorEventText = rb.getString("aic.visitor_event_default").trim();
			this.VisitorEventPrevText = rb.getString("aic.visitor_event_prev").trim();
						
			this.ProjectTitle = rb.getString("aic.project_title").trim();
			this.ProposalTitle = rb.getString("aic.proposal_title").trim();
			this.ClientVoiceTitle = rb.getString("aic.client_voice_title").trim();
			this.MetricsTitle = rb.getString("aic.metrics_title").trim();
			
			this.LinkId = rb.getString("aic.linkid").trim();
			
			this.ContactUsURL = rb.getString("aic.contactus_url").trim();
			this.LandingPageURL = rb.getString("aic.landing_page_url").trim();
			this.AdminEmailID = rb.getString("aic.admin_email_id").trim();
			this.POCRequestEmailID = rb.getString("aic.pocrequest_email_id").trim();
			
			this.SwitchWorkspaceName = rb.getString("aic.switch_header_name").trim();
			
			this.EmailFooter = rb.getString("aic.email_footer").trim();
			
			this.AppName = rb.getString("aic.app_name").trim();
			
			this.UnauthorizedURL = rb.getString("aic.unauthorized_url").trim();
				
			this.BrandAbbrvn = rb.getString("aic.brand_abbrvn").trim();
			this.BrandExpsn = rb.getString("aic.brand_expsn").trim();
			this.HtmlPageTitle=rb.getString("aic.html_pg_title").trim();
			this.HtmlPageHead=rb.getString("aic.html_pg_head").trim();	
			
			this.ProcReqstServletURL=rb.getString("aic.proc_reqst_servlet").trim();			
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(this,e);
			logger.error("*** ERROR (FATAL) *** Property(s) for Text message not found on etsbrand.properties file. Please update/add properties." + e.getMessage());
		}
	}

}
