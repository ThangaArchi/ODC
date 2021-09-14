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

package oem.edge.ets.fe.ubp;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.AmtHeaderFooter;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSErrorCodes;
import oem.edge.ets.fe.ETSMail;
import oem.edge.ets.fe.ETSParams;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.aic.AICUserRequestFunctions;

import org.apache.commons.logging.Log;

/**
 * @version 	1.0
 * @author
 */
public class ETSNewCompanyServlet extends HttpServlet implements Servlet {

	public final static String Copyright ="(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.4";
	
	private static Log logger = EtsLogger.getLogger(ETSNewCompanyServlet.class);

	private String mailhost;

	public void service(HttpServletRequest request,HttpServletResponse response)
		throws ServletException, IOException {

		Connection conn = null;
		String Msg = null;
		Hashtable params = null;
		String sLink = "";

		StringBuffer sHeader = new StringBuffer("");
		EdgeAccessCntrl es = new EdgeAccessCntrl();
		AccessCntrlFuncs acf = new AccessCntrlFuncs();
		response.setContentType("text/html");
		PrintWriter writer = response.getWriter();
		String strProjectType = "";

		try {

			SimpleDateFormat df = new SimpleDateFormat("EEE, MMM dd, yyyy");
			String sDate = df.format(new Date());
			
			String appType = ETSUtils.checkNull(request.getParameter("appType"));
			if (appType.equals(Defines.AIC_WORKSPACE_TYPE)) {
				strProjectType = Defines.AIC_WORKSPACE_TYPE;
			} else {
				strProjectType = Defines.ETS_WORKSPACE_TYPE;
			}
				
			UnbrandedProperties prop = PropertyFactory.getProperty(strProjectType);
			
			conn = ETSDBUtils.getConnection();
			if (!es.GetProfile(response, request, conn)) { return; }

			sLink = request.getParameter("linkid");
			if (sLink == null || sLink.equals("")) {sLink = prop.getLinkID();}

			
						 
			ETSParams parameters = new ETSParams(); // set the parameters
			parameters.setConnection(conn);
			parameters.setRequest(request);
			parameters.setResponse(response);
			parameters.setWriter(writer);
			parameters.setLinkId(sLink);
			parameters.setEdgeAccessCntrl(es);

			Hashtable hs = ETSUtils.getServletParameters(request);
			AmtHeaderFooter EdgeHeader = new AmtHeaderFooter(es, conn, hs);
			EdgeHeader.setPopUp("J");
            
			String from  = ETSUtils.checkNull(request.getParameter("from"));
			String action  = ETSUtils.checkNull(request.getParameter("act"));
			
			if(appType.equals("AIC")){
				EdgeHeader.setPageTitle("Collaboration Center");
			}
			EdgeHeader.setHeader("Request to add new company");
			EdgeHeader.setSubHeader("Request to create a new client company");
            
			sHeader.insert(0, EdgeHeader.printSubHeader());
			sHeader.insert(0, EdgeHeader.printBullsEyeLeftNav());
			sHeader.insert(0, EdgeHeader.printETSBullsEyeHeader());
			sHeader.append("<form name=\"request\" method=\"post\" action=\"ETSNewCompanyServlet.wss\">");
			sHeader.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
			sHeader.append("<tr valign=\"top\"><td headers=\"\" width=\"600\" valign=\"top\">");
			
			sHeader.append("<table summary=\"\" width=\"100%\"><tr><td  headers=\"\" width=\"443\">");
			sHeader.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\"><td headers=\"\"  width=\"60%\" align=\"left\">" + es.gIR_USERN + "</td><td headers=\"\" width=\"40%\" align=\"right\">" + sDate + "</td></tr></table>");
			sHeader.append("</td>");
			sHeader.append("<td headers=\"\" width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
			sHeader.append("<td headers=\"\" class=\"small\" align=\"right\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\" width=\"\" align=\"right\"><img alt=\"\" src=\"" + Defines.ICON_ROOT + "key.gif\" width=\"16\" height=\"16\" /></td><td headers=\"\" width=\"90\" align=\"right\">Secure content</td></tr></table></td>");
			sHeader.append("</tr></table>");
			
			sHeader.append("<!-- Gray dotted line -->");
			sHeader.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			sHeader.append("<tr>");
			sHeader.append("<td headers=\"\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			sHeader.append("<td headers=\"\" background=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" width=\"431\"><img alt=\"\" border=\"0\" height=\"1\" width=\"100%\" src=\"//www.ibm.com/i/c.gif\"/></td>");
			sHeader.append("<td headers=\"\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			sHeader.append("</tr>");
			sHeader.append("</table>");
			sHeader.append("<!-- End Gray dotted line -->");
			
			writer.println(sHeader.toString()); 
			writer.println("<input type=\"hidden\" name=\"appType\" value=\""+appType+"\" />");
			
			ETSUserRequestFunctions userRequestFunctions = new ETSUserRequestFunctions(parameters);
						   
			if (action.equalsIgnoreCase("")) {
				userRequestFunctions.showRequestNewCompany("");
			} else if (action.equalsIgnoreCase("newcompanyconfirm")) {
				String sError = userRequestFunctions.validateRequestNewCompany();
				if (sError.equalsIgnoreCase("")) {
					// form and send the email.. and show confirmation page.
					ETSMail mail = userRequestFunctions.createCompanyRequestEmail();
					boolean bSent = ETSUtils.sendEmail(mail);
					userRequestFunctions.showCreateCompanyConfirmation(bSent);
				} else {
					//show the same page with errors. 
					userRequestFunctions.showRequestNewCompany(sError);
				}
			}
			
			writer.println("</td>");
			writer.println("</tr>");
			writer.println("</table>");
			// default form variables
			writer.println("<input type=\"hidden\" name=\"linkid\" value=\"" + sLink + "\" />");
			writer.println("<input type=\"hidden\" name=\"from\" value=\"" + from + "\" />");
			writer.println("</form>");

			// content ends
			writer.println(EdgeHeader.printBullsEyeFooter());

		} catch (SQLException e) {
			e.printStackTrace(); 
			logger.error(this,e);
			ETSUtils.displayError(writer,ETSErrorCodes.getErrorCode(e),"Error occurred on ETSNewCompanyServlet.");
		} catch (Exception e) {
			e.printStackTrace(); 
			logger.error(this,e);
			ETSUtils.displayError(writer,ETSErrorCodes.getErrorCode(e),"Error occurred on ETSNewCompanyServlet.");
		} finally {
			ETSDBUtils.close(conn);
			writer.flush();
			writer.close();
		}
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}


	private String getParameter(HttpServletRequest req, String key) {
		String value = req.getParameter(key);
		if (value == null) {return "";} else {return value;}
	}
}

