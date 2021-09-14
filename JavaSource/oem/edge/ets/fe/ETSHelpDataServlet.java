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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.PopupHeaderFooter;
import oem.edge.common.DbConnect;
import oem.edge.common.Global;
import oem.edge.common.SysLog;

/**
 * @version 	1.0
 * @author
 */
public class ETSHelpDataServlet extends HttpServlet {


	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.3";



	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		DbConnect db = null;



		try {

			EdgeAccessCntrl es = new EdgeAccessCntrl();
			if (!es.GetProfile(response, request)) {
				SysLog.log(SysLog.DEBUG, this, "Authentication Process Failed");
				return;
			}

			if (!Global.loaded) {
				Global.Init();
			}

			db = new DbConnect();
			db.makeConn();

			PopupHeaderFooter header = new PopupHeaderFooter();
			header.setPageTitle("ET&S - Admin module");
			header.setHeader("Admin support module");
			out.println(header.printPopupHeader());
			out.println(header.printSubHeader());

			out.println("<form name=\"ets_admin\" enctype=\"multipart/form-data\" method=\"post\" >");

			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"1\">");
			out.println("<tr>");
			out.println(" <td headers=\"\" width=\"200\"><b>Table</b>:</td><td headers=\"\"><b></b>ETS.ETS_HELP</td>");
			out.println("</tr>");

			out.println("<tr>");
			out.println(" <td headers=\"\" width=\"200\"><b><label for=\"label_id\">Project ID</label></b>:</td><td headers=\"\"><input type=\"text\" name=\"project_id\" id=\"label_id\" /></td>");
			out.println("</tr>");

			out.println("<tr>");
			out.println(" <td headers=\"\" width=\"200\"><b><label for=\"label_info_type\">Help document Type</label></b>:</td><td headers=\"\"><select name=\"doc_type\" id=\"label_info_type\"><option value=\"HTML\" selected=\"selected\">HTML</option><option value=\"PDF\">PDF</option></select></td>");
			out.println("</tr>");

			out.println("<tr>");
			out.println(" <td headers=\"\" width=\"200\"><b><label for=\"label_image\">Help document</label></b>:</td><td headers=\"\"><input type=\"file\" name=\"help_document\" id=\"label_label\" /></td>");
			out.println("</tr>");

			out.println("<tr>");
			out.println(" <td headers=\"\" width=\"200\"><b><label for=\"label_alt_text\">Active</label></b>:</td><td headers=\"\"><select name=\"active\" id=\"label_alt_text\"><option value=\"Y\" selected=\"selected\">Yes</option><option value=\"N\">No</option></select></td>");
			out.println("</tr>");

			out.println("</table>");

			out.println("<br /><br />");

			out.println("<hr noshade=\"noshade\" size=\"1\" />");
			out.println("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr><td headers=\"\" valign=\"middle\" width=\"16\"><input type=\"image\" name=\"image_submit\" alt=\"Submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" align=\"absmiddle\" border=\"0\" /></td></tr></table>");
			out.println("<br /><br />");

			out.println(header.printPopupFooter());

		} catch (SQLException e) {
			SysLog.log(SysLog.ERR, this, e);
			e.printStackTrace();
			//EdesignCommonFuncs.displayError(out, EdesignErrorCodes.getErrorCode(e), "SQL Exception Occured in EdesignMyFSEInfoServlet with Error " + e.getErrorCode());
		} catch (Exception e) {
			SysLog.log(SysLog.ERR, this, e);
			e.printStackTrace();
			//EdesignCommonFuncs.displayError(out, EdesignErrorCodes.getErrorCode(e), "Exception Occured in EdesignMyFSEInfoServlet");
		} finally {
			ETSDBUtils.close(db.conn);
			db = null;
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		DbConnect db = null;



		try {

			EdgeAccessCntrl es = new EdgeAccessCntrl();
			if (!es.GetProfile(response, request)) {
				SysLog.log(SysLog.DEBUG, this, "Authentication Process Failed");
				return;
			}

			if (!Global.loaded) {
				Global.Init();
			}

			db = new DbConnect();
			db.makeConn();

			PopupHeaderFooter header = new PopupHeaderFooter();
			header.setPageTitle("ET&S - Admin module");
			header.setHeader("Admin support module");
			out.println(header.printPopupHeader());
			out.println(header.printSubHeader());

			uploadContent(db.conn,request,response,out,es);

            out.println("<form name=\"ets_admin\" enctype=\"multipart/form-data\" method=\"post\" >");

            out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"1\">");
            out.println("<tr>");
            out.println(" <td headers=\"\" width=\"200\"><b>Table</b>:</td><td headers=\"\"><b></b>ETS.ETS_HELP</td>");
            out.println("</tr>");

            out.println("<tr>");
            out.println(" <td headers=\"\" width=\"200\"><b><label for=\"label_id\">Project ID</label></b>:</td><td headers=\"\"><input type=\"text\" name=\"project_id\" id=\"label_id\" /></td>");
            out.println("</tr>");

            out.println("<tr>");
            out.println(" <td headers=\"\" width=\"200\"><b><label for=\"label_info_type\">Help document Type</label></b>:</td><td headers=\"\"><select name=\"doc_type\" id=\"label_info_type\"><option value=\"HTML\" selected=\"selected\">HTML</option><option value=\"PDF\">PDF</option></select></td>");
            out.println("</tr>");

            out.println("<tr>");
            out.println(" <td headers=\"\" width=\"200\"><b><label for=\"label_image\">Help document</label></b>:</td><td headers=\"\"><input type=\"file\" name=\"help_document\" id=\"label_label\" /></td>");
            out.println("</tr>");

            out.println("<tr>");
            out.println(" <td headers=\"\" width=\"200\"><b><label for=\"label_alt_text\">Active</label></b>:</td><td headers=\"\"><select name=\"active\" id=\"label_alt_text\"><option value=\"Y\" selected=\"selected\">Yes</option><option value=\"N\">No</option></select></td>");
            out.println("</tr>");

            out.println("</table>");

            out.println("<br /><br />");

            out.println("<hr noshade=\"noshade\" size=\"1\" />");
            out.println("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr><td headers=\"\" valign=\"middle\" width=\"16\"><input type=\"image\" name=\"image_submit\" alt=\"Submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" align=\"absmiddle\" border=\"0\" /></td></tr></table>");
            out.println("<br /><br />");

            out.println(header.printPopupFooter());


		} catch (SQLException e) {
			SysLog.log(SysLog.ERR, this, e);
			e.printStackTrace();
			//EdesignCommonFuncs.displayError(out, EdesignErrorCodes.getErrorCode(e), "SQL Exception Occured in EdesignMyFSEInfoServlet with Error " + e.getErrorCode());
		} catch (Exception e) {
			SysLog.log(SysLog.ERR, this, e);
			e.printStackTrace();
			//EdesignCommonFuncs.displayError(out, EdesignErrorCodes.getErrorCode(e), "Exception Occured in EdesignMyFSEInfoServlet");
		} finally {
			ETSDBUtils.close(db.conn);
			db = null;
		}
	}





	public static void uploadContent(Connection con, HttpServletRequest request, HttpServletResponse response, PrintWriter out, EdgeAccessCntrl es) throws Exception {

		StringBuffer sError = new StringBuffer("");

		try {

			Vector mult = MimeMultipartParser.getBodyParts(request.getInputStream());
			int count = mult.size();

			String formCharset = "ISO_8859-1";
			String parm = null;
			InputStream inStream = null;
			InputStream inStream2 = null;

			String fileName = "";

			String sProjId = "";
            String sDocType = "";
            String sActive = "";

			String sUpdateFlag = "";


			// Since the parts can be in any order, we loop through once to find our
			// form charset, so we know how to convert the rest of the data in the other parts.
			for (int i = 0; i < count; ++i) {
				WebAccessBodyPart part = (WebAccessBodyPart) mult.elementAt(i);
				if (part.getDisposition("name", "ISO_8859-1").equalsIgnoreCase("form-charset")) {
					formCharset = part.getContentAsString("ISO_8859-1");
				}
			}

			for (int i = 0; i < count; ++i) {

				WebAccessBodyPart part = (WebAccessBodyPart) mult.elementAt(i);

				parm = part.getDisposition("name", formCharset);
				String value = (part.getContentAsString(formCharset)).trim();

				if (parm.equalsIgnoreCase("help_document")) {
					// Set our input stream
					inStream = part.getContentInputStream();
					//inStream2 = part.getContentInputStream();

					fileName = part.getDisposition(Defines.FILE_MULTIPART_DISPOSITION_FILENAME, formCharset);

					if (fileName.length() > 0) {
						int lastBackSlash = fileName.lastIndexOf("\\"); // Windows based
						int lastForwardSlash = fileName.lastIndexOf(Defines.SLASH); // Unix based

						if (lastBackSlash > 0) {
							fileName = fileName.substring(lastBackSlash + 1, fileName.length());
						} else if (lastForwardSlash > 0) {
							fileName = fileName.substring(lastForwardSlash + 1, fileName.length());
						}

						fileName = fileName.replace(' ', '_');
					}
				} else {

					System.out.println(parm + "=" + value);
					if (parm.equalsIgnoreCase("project_id")) {
						sProjId = value;
					} else if (parm.equalsIgnoreCase("doc_type")) {
						sDocType = value;
					} else if (parm.equalsIgnoreCase("active")) {
						sActive = value;
					}
				}
			}

			int inStreamAvail = -1;

			if (inStream != null) {
				try {
					inStreamAvail = inStream.available();
					if (inStreamAvail > (3000000)) {
						sError.append("The File is over the 3MB limit.  Please use a file less than 3MB for help documents.<br /><br />");
					}
				} catch (IOException ioe) {
					System.err.println("ioe ex for instreamavail(). e=" + ioe);
					sError.append("Error occurred, please try again" + ioe.toString() + "<br /><br />");
				}
			} else {
				inStreamAvail = -1;
			}

			boolean success = false;
			try {
				success = insertIntoTable(con,sProjId,sDocType, sActive,inStream,inStreamAvail,es.gIR_USERN);
			} catch (Exception e) {
				success = false;
			}

			if (!success) {
				out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr><td headers=\"\">");
				out.println("<hr noshade=\"noshade\" size=\"1\" />");
				out.println("<br />");
				out.println("<span style=\"color:#ff3333\">An error occured when uploading data. Please check again and try to upload.</span>");
				out.println("<br />");
				out.println("</td></tr></table>");
			}

		} catch (Exception ex) {
			throw ex;
		}
	}

	/**
	* @see javax.servlet.GenericServlet#void ()
	*/
	public void init() throws ServletException {

		super.init();

	}



	private static boolean insertIntoTable(Connection con, String sProjectId, String sDocType, String sActive, InputStream inStream, int iSize, String sIRID) throws Exception {

		PreparedStatement pstmt = null;
		StringBuffer sQuery = new StringBuffer();
		boolean bReturn = false;

		try {

			sQuery.append("UPDATE ETS.ETS_HELP SET HELP_DOC=?,HELP_DOC_TYPE=?,ACTIVE=?,LAST_USERID=?,LAST_TIMESTAMP=? ");
			sQuery.append("WHERE PROJECT_ID=?");

			pstmt = con.prepareStatement(sQuery.toString());

			pstmt.setBinaryStream(1,inStream,iSize);

			pstmt.setString(2, ETSUtils.escapeString(sDocType));
			pstmt.setString(3,ETSUtils.escapeString(sActive));
            pstmt.setString(4,ETSUtils.escapeString(sIRID));
            pstmt.setTimestamp(5,new Timestamp(System.currentTimeMillis()));

			int iResult = pstmt.executeUpdate();

			if (iResult <=0) {

				ETSDBUtils.close(pstmt);
				sQuery.setLength(0);

				sQuery.append("INSERT INTO ETS.ETS_HELP (PROJECT_ID,HELP_DOC_TYPE,HELP_DOC,ACTIVE,LAST_USERID,LAST_TIMESTAMP) ");
				sQuery.append("VALUES (?,?,?,?,?,?) ");


				pstmt = con.prepareStatement(sQuery.toString());

				pstmt.setString(1,sProjectId);
                pstmt.setString(2,sDocType);
                pstmt.setBinaryStream(3,inStream,iSize);
                pstmt.setString(4, sActive);
                pstmt.setString(5,sIRID);
                pstmt.setTimestamp(6,new Timestamp(System.currentTimeMillis()));

				pstmt.executeUpdate();
			}

			bReturn = true;

		} catch (Exception e) {
			e.printStackTrace();
			bReturn = false;
			throw e;
		} finally {
			ETSDBUtils.close(pstmt);
		}

		return bReturn;

	}

}
