/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2005                                     */
/*                                                                           */
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
/**
 * @author v2sathis
 *
 */
public abstract class UnbrandedProperties {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2005";
	public static final String CLASS_VERSION = "1.5";

	protected String MessageText;
	protected String PrevMessageText;
	protected String VisitorMessageText;
	protected String VisitorMessagePrevText;
	
	protected String EventText;
	protected String PrevEventText;
	protected String VisitorEventText;
	protected String VisitorEventPrevText;

	protected String MeetingText;
	protected String PrevMeetingText;
	protected String VisitorMeetingText;
	protected String VisitorMeetingPrevText;
	
	protected String LinkId;
	protected String ContactUsURL;
	protected String LandingPageURL;
	protected String AdminEmailID;
	protected String POCRequestEmailID;
	protected String SwitchWorkspaceName;
	protected String EmailFooter;
	protected String AppName;
	
	protected String ProjectTitle;
	protected String ProposalTitle;
	protected String ClientVoiceTitle;
	protected String MetricsTitle;
	
	protected String BrandAbbrvn;
	protected String BrandExpsn;
	protected String HtmlPageTitle;
	protected String HtmlPageHead;	
	
	protected String UnauthorizedURL;
	
	protected String ProcReqstServletURL;
	
	abstract void loadProperties();	

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.brand.UnbrandedProperties#getLinkID()
	 */
	public String getLinkID() {
		return this.LinkId;
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.brand.UnbrandedProperties#getSwitchWorkspaceName()
	 */
	public String getSwitchWorkspaceName() {
		return this.SwitchWorkspaceName;
	}
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.brand.UnbrandedProperties#getLandingPageURL()
	 */
	public String getLandingPageURL() {
		return this.LandingPageURL;
	}
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.brand.UnbrandedProperties#getAdminEmailID()
	 */
	public String getAdminEmailID() {
		return this.AdminEmailID;
	}
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.brand.UnbrandedProperties#getEmailFooter()
	 */
	public String getEmailFooter() {
		return this.EmailFooter;
	}
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.brand.UnbrandedProperties#getPOCEmail()
	 */
	public String getPOCEmail() {
		return this.POCRequestEmailID;
	}
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.brand.UnbrandedProperties#getAppName()
	 */
	public String getAppName() {
		return this.AppName;
	}
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.brand.UnbrandedProperties#getProjectTitle()
	 */
	public String getProjectTitle() {
		return this.ProjectTitle;
	}
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.brand.UnbrandedProperties#getProposalTitle()
	 */
	public String getProposalTitle() {
		return this.ProposalTitle;
	}
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.brand.UnbrandedProperties#getClientVoiceTitle()
	 */
	public String getClientVoiceTitle() {
		return this.ClientVoiceTitle;
	}
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.brand.UnbrandedProperties#getMetricsTitle()
	 */
	public String getMetricsTitle() {
		return this.MetricsTitle;
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.brand.UnbrandedProperties#getTextPrevMessagesText()
	 */
	public String getMessagesText() {
		return this.MessageText;
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.brand.UnbrandedProperties#getTextPrevMessagesText()
	 */
	public String getPrevMessagesText() {
		return this.PrevMessageText;
	}
	
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.brand.UnbrandedProperties#getTextPrevMessagesText()
	 */
	public String getVisitorMessagesText() {
		return this.VisitorMessageText;
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.brand.UnbrandedProperties#getTextPrevMessagesText()
	 */
	public String getVisitorPrevMessagesText() {
		return this.VisitorMessagePrevText;
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.brand.UnbrandedProperties#getTextPrevMessagesText()
	 */
	public String getMeetingText() {
		return this.MeetingText;
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.brand.UnbrandedProperties#getTextPrevMessagesText()
	 */
	public String getPrevMeetingText() {
		return this.PrevMeetingText;
	}
	
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.brand.UnbrandedProperties#getTextPrevMessagesText()
	 */
	public String getVisitorMeetingText() {
		return this.VisitorMeetingText;
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.brand.UnbrandedProperties#getTextPrevMessagesText()
	 */
	public String getVisitorPrevMeetingText() {
		return this.VisitorMeetingPrevText;
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.brand.UnbrandedProperties#getTextPrevMessagesText()
	 */
	public String getEventText() {
		return this.EventText;
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.brand.UnbrandedProperties#getTextPrevMessagesText()
	 */
	public String getPrevEventText() {
		return this.PrevEventText;
	}
	
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.brand.UnbrandedProperties#getTextPrevMessagesText()
	 */
	public String getVisitorEventText() {
		return this.VisitorEventText;
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.brand.UnbrandedProperties#getTextPrevMessagesText()
	 */
	public String getVisitorPrevEventText() {
		return this.VisitorEventPrevText;
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.brand.UnbrandedProperties#getUnauthorizedURL()
	 */
	public String getUnauthorizedURL() {
		return this.UnauthorizedURL;
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.brand.UnbrandedProperties#getBrandAbrvn()
	 */
	public String getBrandAbrvn() {
		return this.BrandAbbrvn;
	}
	
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.brand.UnbrandedProperties#getBrandExpsn()
	 */
	public String getBrandExpsn() {
		return this.BrandExpsn;
	}
	
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.brand.UnbrandedProperties#getHtmlPgTitle()
	 */
	public String getHtmlPgTitle() {
		return this.HtmlPageTitle;
	}
	
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.brand.UnbrandedProperties#getHtmlPgHead()
	 */
	public String getHtmlPgHead() {
		return this.HtmlPageHead;
	}
	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.brand.UnbrandedProperties#getProcRequestServletURL()
	 */
	public String getProcReqstServletURL() {
		return this.ProcReqstServletURL;
	}

	
}
