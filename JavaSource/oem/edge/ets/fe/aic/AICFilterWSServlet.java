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

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import java.util.ResourceBundle;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.*;
import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.AmtHeaderFooter;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.Metrics;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.setmet.ETSSetMet;
import oem.edge.ets.fe.setmet.ETSSetMetDAO;
import oem.edge.ets.fe.setmet.ETSSetMetExpectation;
import oem.edge.ets.fe.ubp.ETSUserDetails;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;

import org.apache.commons.logging.Log;

public class AICFilterWSServlet extends HttpServlet {

	public final static String Copyright =
		"(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.3";

	protected ETSDatabaseManager databaseManager;
	private String mailhost;

	private static Log logger = EtsLogger.getLogger(AICConnectServlet.class);

	private static final String BUNDLE_NAME =
		"oem.edge.ets.fe.aic.AICResources";
	private static final ResourceBundle aic_rb =
		ResourceBundle.getBundle(BUNDLE_NAME);

	static private final String[] months = new String[]
	{ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
	  "Sept", "Oct", "Nov", "Dec" };

	public void service(
		HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException, IOException {

		response.setContentType("text/html");

		PrintWriter writer = response.getWriter();

		Connection con = null;
		EdgeAccessCntrl es = new EdgeAccessCntrl();
		AccessCntrlFuncs acf = new AccessCntrlFuncs();

		Hashtable params;
		String sLink;
		int iFilter;

		Statement custStmt = null;
		Statement sectStmt = null;
		Statement prcsStmt = null;
		Statement brandStmt = null;

		ResultSet custRS = null;
		ResultSet sectRS = null;
		ResultSet prcsRS = null;
		ResultSet brandRS = null;

		StringBuffer custQry = new StringBuffer("");
		StringBuffer sectQry = new StringBuffer("");
		StringBuffer prcsQry = new StringBuffer("");
		StringBuffer brandQry = new StringBuffer("");
		String sel_str = "";

		try {

			SimpleDateFormat df = new SimpleDateFormat("EEE, MMM dd, yyyy");
			String sDate = df.format(new Date());

			con = ETSDBUtils.getConnection();

			if (!es.GetProfile(response, request, con)) {
				return;
			}

			UnbrandedProperties prop =
							PropertyFactory.getProperty(Defines.AIC_WORKSPACE_TYPE);

			ETSProjectInfoBean projBean =
				(ETSProjectInfoBean) request.getSession(false).getAttribute(
					"ETSProjInfo");

			if (projBean == null || !projBean.isLoaded()) {
				projBean = ETSUtils.getProjInfoBean(con);
				request.getSession(false).setAttribute("ETSProjInfo", projBean);
			}

			Hashtable hs = ETSUtils.getServletParameters(request);

			sLink = request.getParameter("linkid");
			if (sLink == null || sLink.equals("")) {
				hs.put("linkid", prop.getLinkID()); //210000
				sLink = prop.getLinkID(); //210000
			}

			String sSort = request.getParameter("sort");
			if (sSort == null || sSort.equals("")) {
				sSort = "";
			} else {
				sSort = sSort.trim();
			}

			AmtHeaderFooter EdgeHeader = new AmtHeaderFooter(es, con, hs);
			EdgeHeader.setPopUp("J");
			String header = "";
  			header = aic_rb.getString("aic.wsfilter.header").trim();
			EdgeHeader.setPageTitle(header);
			EdgeHeader.setHeader(header);
			EdgeHeader.setSubHeader(
				aic_rb.getString("aic.wsfilter.subheader").trim());
			writer.println(
				ETSSearchCommon.getMasthead(
					EdgeHeader,
					Defines.AIC_WORKSPACE_TYPE));
			writer.println(EdgeHeader.printBullsEyeLeftNav());
			writer.println(EdgeHeader.printSubHeader());
			Metrics.appLog(con, es.gIR_USERN, "AIC_Landing");
			writer.println(
				"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" " +
				"width=\"600\" border=\"0\" >");
			writer.println(
				"<tr valign=\"top\"><td headers=\"col1\" width=\"443\" " +
				"valign=\"top\" class=\"small\"><table summary=\"\" " +
				"width=\"100%\"><tr><td headers=\"col2\" width=\"60%\">"
					+ es.gIR_USERN
					+ "</td><td headers=\"col3\" width=\"40%\" align=\"right\">"
					+ sDate
					+ "</td></tr></table></td>");
			writer.println(
				"<td headers=\"col4\" width=\"7\"><img alt=\"\" src=\""
					+ Defines.TOP_IMAGE_ROOT
					+ "c.gif\" width=\"7\" height=\"1\" /></td>");
			writer.println(
				"<td headers=\"col5\" class=\"small\" align=\"right\">" +
				"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" " +
				"border=\"0\"><tr><td headers=\"col6\" width=\"\" align=\"right\">" +
				"<img alt=\"\" src=\""
					+ Defines.ICON_ROOT
					+ "key.gif\" width=\"16\" height=\"16\" /></td>" +
						"<td headers=\"col7\"  width=\"90\" align=\"right\">" +
						"Secure content</td></tr></table></td></tr>");
			writer.println("</table>");

			String wsFilter = request.getParameter("filter");
			if (wsFilter == null) {
				iFilter = 1;
			} else {
				iFilter = Integer.parseInt(wsFilter);
			}

			writer.println(
				"<form name=\"wsfilter\" method=\"post\" action=\""
					+ Defines.SERVLET_PATH
					+ "AICSalesWSListServlet.wss?linkid="
					+ sLink
					+ "&from=search&filter="
					+ iFilter
					+ "\">");
			writer.println(
				"<script type=\"text/javascript\" language=\"javascript\">");

			writer.println("function cancel(){");
			writer.println(
				"document.getElementById(\"wsfilter\").reset();");
			writer.println("}</script>");
			writer.println(
				"<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" " +
				"width=\"443\">");
			writer.println(
				"<tbody><tr><td headers=\"col8\" height=\"18\" width=\"443\">" +
				"Please select the values for the filter conditions and " +
				"click Search.</td>");
			writer.println("</tr></tbody></table></br>");
			writer.println(
				"<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" " +
				"width=\"600\">");
			writer.println(
				"<tbody><tr><td headers=\"col9\" class=\"tblue\" height=\"18\" width=\"600\">" +
				"<b>&nbsp;Filter conditions</b></td></tr></tbody></table>");
			writer.println(
				"<br /><table summary=\"filter conditions details\" " +
				"border=\"0\" cellpadding=\"4\" cellspacing=\"0\" " +
				"width=\"600\"><tbody>");
			writer.println("<tr bgcolor=\"#cccccc\" height=\"12\">");
			writer.println("<th id=\"fcid1\" align=\"left\" valign=\"top\">");
			writer.println(
				"<b><label for=\"ff2\">SCE Sector</label></b>:</th>" +
				"<th id=\"fcid2\" align=\"left\" valign=\"top\">&nbsp;</th>");
			writer.println(
				"<th id=\"fcid3\" align=\"left\" valign=\"top\"><b>" +
				"<label for=\"ff3\">Business Sector</label></b>:</th>");
			writer.println(
				"<th id=\"fcid4\" align=\"left\" valign=\"top\">&nbsp;</th>");
			writer.println(
				"<th id=\"fcid5\" align=\"left\" valign=\"top\"><b>" +
				"<label for=\"ff4\">Business Sub-Sector</label></b>:" +
				"</th></tr>");
			writer.println(
				"<tr><td headers=\"fcid1\" align=\"left\" valign=\"top\">");
			writer.println(
				"<select id=\"ff2\" multiple=\"multiple\" size=\"5\" " +
				"name=\"sceSectList\" class=\"iform\" " +
				"style=\"width: 180px;\" width=\"180px\">");
			writer.println(
				"<option value=\"All\" selected=\"selected\">All</option>");

			Vector vSceSector =
				getValues(
					con,
					aic_rb.getString("aic.wsfilter.sceSector").trim());

			if (vSceSector != null && vSceSector.size() > 0) {

				for (int i = 0; i < vSceSector.size(); i++) {

					String sValue = (String) vSceSector.elementAt(i);
					writer.println(
						"<option value=\""
							+ sValue
							+ "\" >"
							+ sValue
							+ "</option>");

				}
			}

			writer.println(
				"</select></td><td headers=\"fcid2\" align=\"left\" " +
				"valign=\"top\">&nbsp;</td>");
			writer.println(
				"<td headers=\"fcid3\" align=\"left\" valign=\"top\">");
			writer.println(
				"<select id=\"ff3\" multiple=\"multiple\" size=\"5\" " +
				"name=\"sectorList\" class=\"iform\" style=\"width: 180px;\" " +
				"width=\"180px\" align=\"left\">");
			writer.println(
				"<option value=\"All\" selected=\"selected\">All</option>");

			Vector vSector =
				getValues(con, aic_rb.getString("aic.wsfilter.sector").trim());

			if (vSector != null && vSector.size() > 0) {

				for (int i = 0; i < vSector.size(); i++) {

					String sValue = (String) vSector.elementAt(i);
					writer.println(
						"<option value=\""
							+ sValue
							+ "\" >"
							+ sValue
							+ "</option>");

				}
			}

			writer.println(
				"</select></td><td headers=\"fcid4\" align=\"left\" " +
				"valign=\"top\">&nbsp;</td>");
			writer.println(
				"<td headers=\"fcid5\" align=\"left\" valign=\"top\">");
			writer.println(
				"<select id=\"ff4\" multiple=\"multiple\" size=\"5\" " +
				"name=\"subSectList\" class=\"iform\" " +
				"style=\"width: 180px;\" width=\"180px\" align=\"left\">");
			writer.println(
				"<option value=\"All\" selected=\"selected\">All</option>");

			Vector vSubSector = getSubSectorValues(con);

			if (vSubSector != null && vSubSector.size() > 0) {

				for (int i = 0; i < vSubSector.size(); i++) {

					String sValue = (String) vSubSector.elementAt(i);
					writer.println(
						"<option value=\""
							+ sValue
							+ "\" >"
							+ sValue
							+ "</option>");

				}
			}

			writer.println("</select></td></tr></tbody></table><br />");
			writer.println(
				"<table summary=\"\" border=\"0\" cellpadding=\"4\" " +
				"cellspacing=\"0\" width=\"600\">");
			writer.println("<tbody><tr bgcolor=\"#cccccc\" height=\"12\">");
			writer.println(
				"<th id=\"fcid6\" align=\"left\" valign=\"top\"><b>" +
				"<label for=\"ff5\">Process</label></b>:</th>");
			writer.println(
				"<th id=\"fcid7\" align=\"left\" valign=\"top\">&nbsp;</th>");
			writer.println(
				"<th id=\"fcid8\" align=\"left\" valign=\"top\"><b>" +
				"<label for=\"ff6\">Brand</label></b>:</th></tr>");
			writer.println(
				"<tr><td headers=\"fcid6\" align=\"left\" valign=\"top\">");
			writer.println(
				"<select id=\"ff5\" multiple=\"multiple\" size=\"5\" " +
				"name=\"procsList\" class=\"iform\" style=\"width: 280px;\" " +
				"width=\"280px\" align=\"left\">");
			writer.println(
				"<option value=\"All\" selected=\"selected\">All</option>");

			Vector vProcess =
				getValues(con, aic_rb.getString("aic.wsfilter.process").trim());

			if (vProcess != null && vProcess.size() > 0) {

				for (int i = 0; i < vProcess.size(); i++) {

					String sValue = (String) vProcess.elementAt(i);
					writer.println(
						"<option value=\""
							+ sValue
							+ "\" >"
							+ sValue
							+ "</option>");

				}
			}

			writer.println("</select></td>");
			writer.println(
				"<td headers=\"fcid7\" align=\"left\" valign=\"top\" " +
				"width=\"10\">&nbsp;</td>");
			writer.println(
				"<td headers=\"fcid8\" align=\"left\" valign=\"top\">");
			writer.println(
				"<select id=\"ff5\" multiple=\"multiple\" size=\"5\" " +
				"name=\"brandList\" class=\"iform\" style=\"width: 280px;\" " +
				"width=\"280px\" align=\"left\">");
			writer.println(
				"<option value=\"All\" selected=\"selected\">All</option>");

			Vector vBrand =
				getValues(con, aic_rb.getString("aic.wsfilter.brand").trim());

			if (vBrand != null && vBrand.size() > 0) {

				for (int i = 0; i < vBrand.size(); i++) {

					String sValue = (String) vBrand.elementAt(i);
					writer.println(
						"<option value=\""
							+ sValue
							+ "\" >"
							+ sValue
							+ "</option>");

				}
			}

			writer.println(
				"</option></select></td></tr></tbody></table><br />");
			writer.println(
				"<table summary=\"\" border=\"0\" cellpadding=\"4\" " +
				"cellspacing=\"0\" width=\"600\">");
			writer.println("<tbody><tr bgcolor=\"#cccccc\" height=\"12\">");
			writer.println(
				"<th id=\"fcid9\" align=\"left\" valign=\"top\" " +
				"width=\"320\"><b>Date Created</b>: " +
				"<span class=\"small\">(Check all dates or select date range)"+
				"</span></th></tr>");
			writer.println(
				"<tr><td headers=\"fcid9\" align=\"left\" valign=\"top\" " +
				"width=\"320\">");
			writer.println(
				"<table summary=\"datesall\" border=\"0\" " +
				"cellpadding=\"4\" cellspacing=\"0\" width=\"200\">" +
				"<tbody><tr><td headers=\"col10\" align=\"left\" valign=\"top\">");
			writer.println(
				"<input id=\"lblchkidalldates\" name=\"allDates\" " +
				"checked=\"checked\" align=\"left\" type=\"checkbox\"></td>");
			writer.println(
				"<td headers=\"col11\" align=\"left\" valign=\"top\"><b>All dates</b>&nbsp;" +
				"&nbsp;&nbsp; - or -</td> </tr></tbody></table>");
			writer.println(
				"<table summary=\"datesall\" border=\"0\" " +
				"cellpadding=\"4\" cellspacing=\"0\" width=\"200\">");
			writer.println(
				"<tbody><tr> <td headers=\"col12\" align=\"left\" valign=\"top\" width=\"200\">");
			writer.println(
				"<table summary=\"alldates\" border=\"0\" " +
				"cellpadding=\"0\" cellspacing=\"0\" width=\"200\"><tbody>");
			writer.println(
				"<tr><td><b>From</b>:<table summary=\"\" border=\"0\" " +
				"cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">");

			writer.println("<tbody><tr><td headers=\"col13\">");

			Calendar calendar = Calendar.getInstance();
			String strMonth = String.valueOf(calendar.get(Calendar.MONTH));
			String strDay = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
			String strYear = String.valueOf(calendar.get(Calendar.YEAR));

			Date today=new Date();
			writer.println(
				"<select id=\"m1\" name=\"fromMonth\" class=\"iform\">");
			for (int m = 0; m < 12; m++) {
					sel_str = "";
					if (strMonth.equals(String.valueOf(m)))
						sel_str = "selected=\"selected\"";
					writer.println("<option value=\"" + m + "\" "
						+ sel_str + ">" + months[m] + "</option>");
				}
			writer.println("</select></td>");

			writer.println(
				"<td headers=\"col14\"><select id=\"d1\" name=\"fromDay\" " +
				"class=\"iform\">");
			for (int d = 1; d <= 31; d++) {
					sel_str = "";
					if (strDay.equals(String.valueOf(d)))
						sel_str = "selected=\"selected\"";
					writer.println("<option value=\"" + d + "\" "
					+ sel_str + ">" + d +	"</option>");
				}
			writer.println("</select></td>");

			writer.println("<td headers=\"col15\">");
			writer.println(
				"<select id=\"y1\" name=\"fromYear\" class=\"iform\">");
			for (int c = 2002; c <= 3000; c++) {
					sel_str = "";
					if (strYear.equals(String.valueOf(c)))
						sel_str = "selected=\"selected\"";
					writer.println("<option value=\"" + c + "\" "
					+ sel_str + ">" + c + "</option>");
				}
			writer.println("</select>");

			writer.println(
				"</td></tr></tbody></table></td>" +
				"</tr><tr><td headers=\"col16\" height=\"14\" width=\"200\">" +
				"<b>To</b>:</td> </tr> <tr> <td headers=\"col17\" height=\"14\" width=\"200\">");
			writer.println(
				"<table summary=\"\" border=\"0\" cellpadding=\"0\" " +
				"cellspacing=\"0\" width=\"100%\">");
			writer.println("<tbody><tr><td headers=\"col18\">");

			writer.println(
				"<select id=\"m2\" name=\"toMonth\" class=\"iform\">");
			for (int m = 0; m < 12; m++) {
					sel_str = "";
					if (strMonth.equals(String.valueOf(m)))
							sel_str = "selected=\"selected\"";
					writer.println("<option value=\"" + m + "\" "
					+ sel_str + ">" + months[m] + "</option>");
				}
			writer.println("</select></td>");

			writer.println(
				"<td headers=\"col19\"><select id=\"d2\" name=\"toDay\" " +
				"class=\"iform\">");
			for (int d = 1; d <= 31; d++) {
					sel_str = "";
					if (strDay.equals(String.valueOf(d)))
							sel_str = "selected=\"selected\"";
					writer.println("<option value=\"" + d + "\" "
					+ sel_str + ">" + d + "</option>");
				}
			writer.println("</select></td>");

			writer.println(
				"<td headers=\"col20\"><select id=\"y2\" name=\"toYear\" " +
				"class=\"iform\">");
			for (int c = 2002; c <= 3000; c++) {
					sel_str = "";
					if (strYear.equals(String.valueOf(c)))
								sel_str = "selected=\"selected\"";
				writer.println("<option value=\"" + c + "\" "
				+ sel_str + ">" + c + "</option>");
				}
			writer.println("</select> ");

			writer.println(
				"</td></tr></tbody></table></td></tr></tbody></table>" +
				"</td></tr></tbody></table> </td></tr></tbody></table><br />");
			writer.println(
				"<table summary=\"\" border=\"0\" cellpadding=\"4\" " +
				"cellspacing=\"0\" width=\"600\">");
			writer.println(
				"<tbody><tr bgcolor=\"#cccccc\" height=\"12\">" +
				"<th id=\"fcid9\" align=\"left\" valign=\"top\" " +
				"width=\"320\">Save filter conditions</th></tr><tr>" +
				"<td>Please click the check box to save the selected filter " +
				"conditions as default search criteria.</td></tr>");
			writer.println(
				"<tr><td headers=\"col21\" align=\"left\" valign=\"top\" width=\"280\">");
			writer.println(
				"<table summary=\"save_qry\" border=\"0\" cellpadding=\"4\" " +
				"cellspacing=\"0\" width=\"200\"> ");
			writer.println(
				"<tbody><tr><td headers=\"col22\" align=\"left\" valign=\"top\">" +
				"<input id=\"lblchkidsaveqry\" name=\"chk_save_qry\" " +
				"align=\"left\" type=\"checkbox\"></td>");
			writer.println(
				"<td headers=\"col23\" align=\"left\" valign=\"top\"><b>Save search criteria" +
				"</b></td></tr></tbody></table></td></tr></tbody>" +
				"</table><br />");
			writer.println(
				"<table summary=\"backinfo\" border=\"0\" " +
				"cellpadding=\"0\" cellspacing=\"0\" width=\"600\">");

			writer.println(
				"<tr><td><input src=\""
					+ Defines.BUTTON_ROOT
					+ "search_t.gif\" alt=\"Search\" name=\"Search\" " +
					"border=\"0\" height=\"21\" type=\"image\" " +
					"width=\"120\"/></td>");
			writer.println(
				"<td headers=\"col24\" align=\"left\" valign=\"top\"><table border=\"0\" " +
				"cellpadding=\"0\" cellspacing=\"0\">");
			writer.println(
				"<tbody><tr><td><img src=\""
					+ Defines.BUTTON_ROOT
					+ "cancel_rd.gif\" alt=\"Reset\" border=\"0\" " +
					"height=\"21\" width=\"21\"/></td>");
			writer.println(
				"<td>&nbsp;<a href=\"javascript:cancel()\" >Reset</a></td>" +
				"</tr></tbody></table>");
			writer.println("</td><td headers=\"col25\" align=\"left\" valign=\"top\">");
			writer.println(
				"&nbsp;<- <a href=\""
					+ Defines.SERVLET_PATH
					+ "AICSalesWSListServlet.wss?linkid="
					+ sLink
					+ "&filter="
					+ iFilter
					+ "\" class=\"fbox\">Back to Workspaces list</a>");
			writer.println("</td>");
			writer.println("</td><td headers=\"col26\" align=\"left\" valign=\"top\">");
			writer.println(
				"&nbsp;<<- <a href=\""
					+ Defines.SERVLET_PATH
					+ "AICConnectServlet.wss?linkid="
					+ sLink
					+ "\" class=\"fbox\">Back to Collaboration Center</a>");
			writer.println(
				"</td></tr></tbody></table><input name=\"srchon\" " +
				"value=\"Y\" type=\"hidden\">");
			writer.println("</tr></table></form>");
			writer.println("<br /><br />");
			writer.println(
				"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" " +
				"border=\"0\"><tr><td headers=\"col27\" width=\"16\" " +
				"align=\"left\"><img alt=\"protected content\" src=\""
				+ Defines.ICON_ROOT
				+ "key.gif\" width=\"16\" height=\"16\"/></td>" +
				"<td headers=\"col28\"  align=\"left\"><span class=\"fnt\">" +
				"A key icon displayed in a page indicates that the page is " +
				"secure and password-protected.</span></td></tr></table>");
			writer.println(EdgeHeader.printBullsEyeFooter());

		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error(this, e);
			}
			e.printStackTrace();
		} finally {
			ETSDBUtils.close(con);
			writer.flush();
			writer.close();
		}

	}


	/**
	 * Get the vector containing all possible
	 * values of the list for eg.,sector/sce-sector list
	 *
	 * @param con
	 * @param sType
	 * @return vValues
	 * @throws SQLException
	 * @throws Exception
	 */
	private static Vector getValues(Connection con, String sType)
		throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");

		Vector vValues = new Vector();

		try {

			sQuery.append(
				"SELECT DISTINCT INFO_LINK, INFO_MODULE " +
				"FROM ETS.ETS_PROJECT_INFO WHERE PROJECT_ID = '"
					+ sType
					+ "' AND INFO_TYPE = '"
					+ Defines.GEO_INFO_TYPE
					+ "' ORDER BY INFO_MODULE for READ ONLY");

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			while (rs.next()) {

				String sValue = rs.getString("INFO_LINK");

				vValues.addElement(sValue);

			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return vValues;
	}

	/**
		 * Get the vector containing all possible
		 * sub-sector values fromsector_details table.
		 *
		 * @param con
		 * @return vValues
		 * @throws SQLException
		 * @throws Exception
		 */
		private static Vector getSubSectorValues(Connection con)
			throws SQLException, Exception {

			Statement stmt = null;
			ResultSet rs = null;
			StringBuffer sQuery = new StringBuffer("");

			Vector vValues = new Vector();

			try {

				sQuery.append(
					"SELECT DISTINCT SECTOR_VALUE " +
					"FROM ETS.AIC_SECTOR_DETAILS WHERE INFO_TYPE='SECTOR'");

				stmt = con.createStatement();
				rs = stmt.executeQuery(sQuery.toString());

				while (rs.next()) {

					String sValue = rs.getString("SECTOR_VALUE");

					vValues.addElement(sValue);

				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				ETSDBUtils.close(rs);
				ETSDBUtils.close(stmt);
			}

			return vValues;
		}

}
