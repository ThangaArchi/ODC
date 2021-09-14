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

package oem.edge.ets.fe.aic;


import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.Hashtable;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;

import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.AmtHeaderFooter;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.Metrics;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSProjectInfoBean;
import oem.edge.ets.fe.ETSSearchCommon;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.documents.DocumentsHelper;

/**
 * @author vivek
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AICHeaderFooter {
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.2";



	private static Log logger = EtsLogger.getLogger(AICHeaderFooter.class);
	private static final String BUNDLE_NAME = "oem.edge.ets.fe.aic.AICResources";
	private static final ResourceBundle aic_rb = ResourceBundle.getBundle(BUNDLE_NAME);

	protected StringBuffer sHeader = new StringBuffer("");
	protected StringBuffer sFooter = new StringBuffer("");
	protected StringBuffer sOnlyHeader = new StringBuffer("");
	protected StringBuffer sTabStr = new StringBuffer("");

	public AICHeaderFooter() {
		super();
	}

	public void init(HttpServletRequest request, HttpServletResponse response) throws SQLException, Exception {



		Connection con = null;
		EdgeAccessCntrl es = new EdgeAccessCntrl();
		AccessCntrlFuncs acf = new AccessCntrlFuncs();

		Hashtable params;
		String sLink;


		try {

			UnbrandedProperties prop = PropertyFactory.getProperty(Defines.AIC_WORKSPACE_TYPE);

			SimpleDateFormat df = new SimpleDateFormat("EEE, MMM dd, yyyy");
			String sDate = df.format(new Date());

			con = ETSDBUtils.getConnection();

			if (!es.GetProfile(response, request, con)) {
				return;
			}

			ETSProjectInfoBean projBean = (ETSProjectInfoBean) request.getSession(false).getAttribute("ETSProjInfo");

			if (projBean == null || !projBean.isLoaded()) {
				projBean = ETSUtils.getProjInfoBean(con);
				request.getSession(false).setAttribute("ETSProjInfo", projBean);
			}

			Hashtable hs = ETSUtils.getServletParameters(request);

			sLink = request.getParameter("linkid");
			if (sLink == null || sLink.equals("")) {
				hs.put("linkid", prop.getLinkID());
				sLink = prop.getLinkID();
			}

			String edgeuserid = AccessCntrlFuncs.getEdgeUserId(con,es.gIR_USERN);
			Vector userents = AccessCntrlFuncs.getUserEntitlements(con,edgeuserid,true, true);

			if (userents.contains(Defines.COLLAB_CENTER_ADMIN_ENTITLEMENT)) {
				boolean	bAdmin = true;
			} else {
				response.sendRedirect("AICUnAuthorizedServlet.wss");
				return;
			}

		
			AmtHeaderFooter EdgeHeader = new AmtHeaderFooter(es, con, hs);
			EdgeHeader.setPopUp("J");
			String assocCompany = es.gASSOC_COMP;
			String header = "";
			header = prop.getAppName();
			

			EdgeHeader.setPageTitle(header);
			EdgeHeader.setHeader(header);

			EdgeHeader.setSubHeader("Welcome, " + es.gFIRST_NAME.trim() + " " + es.gLAST_NAME);

			this.sHeader.insert(0,EdgeHeader.printSubHeader());
			this.sHeader.insert(0,EdgeHeader.printBullsEyeLeftNav());
			this.sHeader.insert(0,ETSSearchCommon.getMasthead(EdgeHeader,Defines.AIC_WORKSPACE_TYPE));




			//display the home page for the user

			 Metrics.appLog(con, es.gIR_USERN, "AIC_Landing");

			 this.sHeader.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\" >");
			 this.sHeader.append("<tr valign=\"top\"><td headers=\"col1\" width=\"443\" valign=\"top\" class=\"small\"><table summary=\"\" width=\"100%\"><tr><td headers=\"col2\" width=\"60%\">" + es.gIR_USERN + "</td><td headers=\"col3\" width=\"40%\" align=\"right\">" + sDate + "</td></tr></table></td>");
			 this.sHeader.append("<td headers=\"col4\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
			 this.sHeader.append("<td headers=\"col5\" class=\"small\" align=\"right\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col6\" width=\"\" align=\"right\"><img alt=\"\" src=\"" + Defines.ICON_ROOT + "key.gif\" width=\"16\" height=\"16\" /></td><td headers=\"col7\"  width=\"90\" align=\"right\">Secure content</td></tr></table></td></tr>");
			 this.sHeader.append("<tr valign=\"top\">");

			this.sHeader.append("<br />");
			this.sHeader.append("</td>");

			this.sHeader.append("</tr>");
			this.sHeader.append("</table>");

			this.sHeader.append("</td>");
			this.sHeader.append("</tr>");
			this.sHeader.append("</table>");

			this.sHeader.append("<br /><br />");
			//this.sHeader.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"col8\" width=\"16\" align=\"left\"><img alt=\"protected content\" src=\"" + Defines.ICON_ROOT + "key.gif\" width=\"16\" height=\"16\" /></td><td headers=\"col9\"  align=\"left\"><span class=\"fnt\">A key icon displayed in a page indicates that the page is secure and password-protected.</span></td></tr></table>");
			printGreyDottedLine();
			this.sFooter.append(EdgeHeader.printBullsEyeFooter());

		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error(this,e);
			}
			e.printStackTrace();
		} finally {
			ETSDBUtils.close(con);

		}


	}

	/**
		 * @return
		 */
	public StringBuffer getFooter() {
		return this.sFooter;
	}

	/**
	 * @return
	 */
	public StringBuffer getHeader() {
		return this.sHeader;
	}

	private void printGreyDottedLine() {

		this.sHeader.append("<!-- Gray dotted line -->");
		this.sHeader.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		this.sHeader.append("<tr>");
		this.sHeader.append("<td headers=\"col10\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
		this.sHeader.append("<td headers=\"col11\" background=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" width=\"431\"><img alt=\"\" border=\"0\" height=\"1\" width=\"100%\" src=\"//www.ibm.com/i/c.gif\"/></td>");
		this.sHeader.append("<td headers=\"col12\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
		this.sHeader.append("</tr>");
		this.sHeader.append("<tr>");
		this.sHeader.append("<tr/>");
		this.sHeader.append("<tr/>");
		this.sHeader.append("<tr/>");
		this.sHeader.append("</tr>");
		this.sHeader.append("</table>");
		this.sHeader.append("<!-- End Gray dotted line -->");


	   }


}
