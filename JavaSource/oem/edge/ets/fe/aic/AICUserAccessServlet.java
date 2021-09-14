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

package oem.edge.ets.fe.aic;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.ResourceBundle;

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
import oem.edge.ets.fe.ETSParams;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.aic.AICUserRequestFunctions;

/**
 * @version 	1.0
 * @author
 */
public class AICUserAccessServlet extends HttpServlet implements Servlet {

	public final static String Copyright ="(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.4";

	private static final String BUNDLE_NAME = "oem.edge.ets.fe.aic.AICResources";
	private static final ResourceBundle aic_rb = ResourceBundle.getBundle(BUNDLE_NAME);
	private static final String APPLICATION_NAME = aic_rb.getString("aic.AICApplicationName");
	private static final String LANDING_PAGE = aic_rb.getString("aic.AICLandingPage");
	private static final String APPLICATION_STRING = aic_rb.getString("aic.AICApplicationString");


	private String mailhost;

	public void service(HttpServletRequest request,HttpServletResponse response)
		throws ServletException, IOException {

		response.setContentType("text/html");

		Connection conn = null;
		String Msg = null;
        Hashtable params = null;
        String sLink = "";

        StringBuffer sHeader = new StringBuffer("");
		EdgeAccessCntrl es = new EdgeAccessCntrl();
		AccessCntrlFuncs acf = new AccessCntrlFuncs();
        PrintWriter writer = response.getWriter();

		try {

			UnbrandedProperties prop = PropertyFactory.getProperty(Defines.AIC_WORKSPACE_TYPE);

			SimpleDateFormat df = new SimpleDateFormat("EEE, MMM dd, yyyy");
			String sDate = df.format(new Date());

			conn = ETSDBUtils.getConnection();
			System.out.println("After DB Connection");
            if (!es.GetProfile(response, request, conn)) { return; }

			Hashtable hs = ETSUtils.getServletParameters(request);

			sLink = request.getParameter("linkid");
			if (sLink == null || sLink.equals("")) {
				hs.put("linkid", prop.getLinkID());
				sLink = prop.getLinkID();
			}

			ETSParams parameters = new ETSParams(); // set the parameters
			parameters.setConnection(conn);
			parameters.setRequest(request);
			parameters.setResponse(response);
			parameters.setWriter(writer);
			parameters.setLinkId(sLink);
            parameters.setEdgeAccessCntrl(es);

            AmtHeaderFooter EdgeHeader = new AmtHeaderFooter(es, conn, hs);
            EdgeHeader.setPageTitle("Collaboration Center");
            EdgeHeader.setPopUp("J");

            String from  = ETSUtils.checkNull(request.getParameter("from"));
			String action  = ETSUtils.checkNull(request.getParameter("act"));

			System.out.println("action " + action);


			AICUserRequestFunctions userRequestFunctions = new AICUserRequestFunctions(parameters);

			if (from.equalsIgnoreCase("req")) {
				EdgeHeader.setHeader("Request access to " + APPLICATION_STRING + " workspace");
			} else {
				EdgeHeader.setHeader("Create a Collaboration Center Workspace");
			}


			if (action.equalsIgnoreCase("")) {
				if (from.equalsIgnoreCase("req")) {
					EdgeHeader.setHeader("Request access to " + APPLICATION_STRING + " workspace");
				} else {
					EdgeHeader.setHeader("Create a Collaboration Center Workspace");
				}

				if (!es.gDECAFTYPE.equalsIgnoreCase("I")) {
					EdgeHeader.setSubHeader("Request access to " + APPLICATION_STRING + " workspace");
				} else {
					EdgeHeader.setSubHeader("Select a client");
				}
			} else if (action.equalsIgnoreCase("clientcontinue")) {

				if (from.equalsIgnoreCase("req")) {
					EdgeHeader.setHeader("Request access to " + APPLICATION_STRING + " workspace");
				} else {
					EdgeHeader.setHeader("Create a Collaboration Center Workspace");
				}


				String sError = userRequestFunctions.validateClientScreen();
				if (!sError.trim().equalsIgnoreCase("")) {
					EdgeHeader.setSubHeader("Select a client");
				} else {
					EdgeHeader.setSubHeader("Select workspaces");
				}
			} else if (action.equalsIgnoreCase("requestconfirm")) {
				String sError = userRequestFunctions.validateRequestWorkspace();

				if (sError.trim().equalsIgnoreCase("")) {
					EdgeHeader.setHeader("Request access to Collaboration Center workspace");
				} else {
					if (from.equalsIgnoreCase("req")) {
						EdgeHeader.setHeader("Request access to " + APPLICATION_STRING + " workspace");
					} else {
						EdgeHeader.setHeader("Create a Collaboration Center Workspace");
					}
				}

				if (!sError.trim().equalsIgnoreCase("")) {
					EdgeHeader.setSubHeader("Select workspaces");
				} else {
					EdgeHeader.setSubHeader("Request confirmation");
				}
			} else if (action.equalsIgnoreCase("externalclient")) {

				if (from.equalsIgnoreCase("req")) {
					EdgeHeader.setHeader("Request access to " + APPLICATION_STRING + " workspace");
				} else {
					EdgeHeader.setHeader("Create a Collaboration Center Workspace");
				}


				String sError = userRequestFunctions.validateClientExternal();
				if (!sError.trim().equalsIgnoreCase("")) {
					EdgeHeader.setSubHeader("Request access to " + APPLICATION_STRING + " workspace");
				} else {
					EdgeHeader.setSubHeader("Request access to " + APPLICATION_STRING + " workspace");
				}
			}

            sHeader.insert(0, EdgeHeader.printSubHeader());
            sHeader.insert(0, EdgeHeader.printBullsEyeLeftNav());
            sHeader.insert(0, EdgeHeader.printETSBullsEyeHeader());
            sHeader.append("<form name=\"request\" method=\"post\" action=\"AICUserAccessServlet.wss\">");
			sHeader.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
			sHeader.append("<tr valign=\"top\"><td width=\"600\" valign=\"top\">");

			sHeader.append("<table summary=\"\" width=\"100%\"><tr><td width=\"443\">");
			sHeader.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\"><td width=\"60%\" align=\"left\">" + es.gIR_USERN + "</td><td width=\"40%\" align=\"right\">" + sDate + "</td></tr></table>");
			sHeader.append("</td>");
			sHeader.append("<td width=\"7\"><img alt=\"\" src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"7\" height=\"1\" /></td>");
			sHeader.append("<td class=\"small\" align=\"right\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td width=\"\" align=\"right\"><img alt=\"\" src=\"" + Defines.ICON_ROOT + "key.gif\" width=\"16\" height=\"16\" /></td><td headers=\"\" width=\"90\" align=\"right\">Secure content</td></tr></table></td>");
			sHeader.append("</tr></table>");

			sHeader.append("<!-- Gray dotted line -->");
			sHeader.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			sHeader.append("<tr>");
			sHeader.append("<td width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			sHeader.append("<td background=\"" + Defines.V11_IMAGE_ROOT + "rules/dotted_rule_443.gif\" width=\"431\"><img alt=\"\" border=\"0\" height=\"1\" width=\"100%\" src=\"//www.ibm.com/i/c.gif\"/></td>");
			sHeader.append("<td width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			sHeader.append("</tr>");
			sHeader.append("</table>");
			sHeader.append("<!-- End Gray dotted line -->");

			writer.println(sHeader.toString()); // output end with a open <td>


			if (action.equalsIgnoreCase("")) {
				if (!es.gDECAFTYPE.equalsIgnoreCase("I")) {
					userRequestFunctions.showRequestScreenForExternal("");
				//	userRequestFunctions.invalidFunctionality();
				} else {
					userRequestFunctions.showClientScreen("");
				}
			} else if (action.equalsIgnoreCase("clientcontinue")) {
				String sError = userRequestFunctions.validateClientScreen();
				if (!sError.trim().equalsIgnoreCase("")) {
					userRequestFunctions.showClientScreen(sError);
				} else {
					userRequestFunctions.showWorkspacesScreen("");
				}
			} else if (action.equalsIgnoreCase("requestconfirm")) {
				String sError = userRequestFunctions.validateRequestWorkspace();
				if (!sError.trim().equalsIgnoreCase("")) {
					userRequestFunctions.showWorkspacesScreen(sError);
				} else {
					userRequestFunctions.confirmRequestWorkspacesScreen();
				}
			} else if (action.equalsIgnoreCase("externalclient")) {
				String sError = userRequestFunctions.validateClientExternal();
				if (!sError.trim().equalsIgnoreCase("")) {
					userRequestFunctions.showRequestScreenForExternal(sError);
				} else {
					userRequestFunctions.confirmRequestFromExternal();
				}
			}

            //... necessary page

            writer.println("</td>");
			writer.println("</tr>");
			writer.println("</table>");
            // default form variables
            writer.println("<input type=\"hidden\" name=\"linkid\" value=\"" + parameters.getLinkId() + "\" />");
			writer.println("<input type=\"hidden\" name=\"from\" value=\"" + from + "\" />");
            writer.println("</form>");

            // content ends
			writer.println(EdgeHeader.printBullsEyeFooter());

		} catch (SQLException e) {
			e.printStackTrace();
			SysLog.log(SysLog.ERR, this, e);
			ETSUtils.displayError(writer,ETSErrorCodes.getErrorCode(e),"Error occurred on " + APPLICATION_NAME);
		} catch (Exception e) {
			e.printStackTrace();
			SysLog.log(SysLog.ERR, this, e);
			ETSUtils.displayError(writer,ETSErrorCodes.getErrorCode(e),"Error occurred on IBM " + APPLICATION_NAME);
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
