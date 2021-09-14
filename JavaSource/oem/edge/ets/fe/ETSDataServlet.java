/*   ------------------------------------------------------------------          */
/*   IBM                                                                                     */
/*                                                                                               */  
/*   OCO Source Materials                                                          */
/*                                                                                               */
/*   Product(s): ICC/PROFIT                                                       */
/*                                                                                               */
/*   (C)Copyright IBM Corp. 2002,2003 		              */ 
/*                                                                                               */  
/*   The source code for this program is not published or otherwise */
/*   divested of its trade secrets, irrespective of what has been        */
/*   deposited with the US Copyright Office.                                  */
/*   ------------------------------------------------------------------           */

package oem.edge.ets.fe;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

/**
 * @version 	1.0
 * @author
 */
public class ETSDataServlet extends HttpServlet {
   public final static String Copyright = "(C) Copyright IBM Corp.  2002, 2003";


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
			out.println(" <td headers=\"\" width=\"200\"><b>Table</b>:</td><td headers=\"\"><b></b>ETS.ETS_PROJECT_INFO</td>");
			out.println("</tr>");
			
			out.println("<tr>");
			out.println(" <td headers=\"\" width=\"200\"><b><label for=\"label_id\">PROJECT_ID</label></b>:</td><td headers=\"\"><input type=\"text\" name=\"PROJECT_ID\" id=\"label_id\" /></td>");
			out.println("</tr>");
	
			out.println("<tr>");
			out.println(" <td headers=\"\" width=\"200\"><b><label for=\"label_info_type\">INFO_TYPE</label></b>:</td><td headers=\"\"><input type=\"text\" name=\"INFO_TYPE\" id=\"label_info_type\" /></td>");
			out.println("</tr>");
	
			out.println("<tr>");
			out.println(" <td headers=\"\" width=\"200\"><b><label for=\"label_info_module\">INFO_MODULE</label></b>:</td><td headers=\"\"><input type=\"text\" name=\"INFO_MODULE\" id=\"label_info_module\" /></td>");
			out.println("</tr>");
	
			out.println("<tr>");
			out.println(" <td headers=\"\" width=\"200\"><b><label for=\"label_image\">IMAGE</label></b>:</td><td headers=\"\"><input type=\"file\" name=\"IMAGE_FILE\" id=\"label_label\" /></td>");
			out.println("</tr>");
	
			out.println("<tr>");
			out.println(" <td headers=\"\" width=\"200\"><b><label for=\"label_alt_text\">IMAGE_ALT_TEXT</label></b>:</td><td headers=\"\"><input type=\"text\" name=\"IMAGE_ALT_TEXT\" id=\"label_alt_text\" /></td>");
			out.println("</tr>");
	
			out.println("<tr>");
			out.println(" <td headers=\"\" width=\"200\"><b><label for=\"label_info_desc\">INFO_DESC</label></b>:</td><td headers=\"\"><input type=\"text\" name=\"INFO_DESC\" id=\"label_info_desc\" /></td>");
			out.println("</tr>");
	
			out.println("<tr>");
			out.println(" <td headers=\"\" width=\"200\"><b><label for=\"label_link\">INFO_LINK</label></b>:</td><td headers=\"\"><input type=\"text\" name=\"INFO_LINK\" id=\"label_link\" /></td>");
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

			uploadContent(db.conn,request,response,out);

			out.println("<form name=\"ets_admin\" enctype=\"multipart/form-data\" method=\"post\" >");

			out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"1\">");
			out.println("<tr>");
			out.println(" <td headers=\"\" width=\"200\"><b>Table</b>:</td><td headers=\"\"><b></b>ETS.ETS_PROJECT_INFO</td>");
			out.println("</tr>");
			
			out.println("<tr>");
			out.println(" <td headers=\"\" width=\"200\"><b><label for=\"label_id\">PROJECT_ID</label></b>:</td><td headers=\"\"><input type=\"text\" name=\"PROJECT_ID\" id=\"label_id\" /></td>");
			out.println("</tr>");
	
			out.println("<tr>");
			out.println(" <td headers=\"\" width=\"200\"><b><label for=\"label_info_type\">INFO_TYPE</label></b>:</td><td headers=\"\"><input type=\"text\" name=\"INFO_TYPE\" id=\"label_info_type\" /></td>");
			out.println("</tr>");
	
			out.println("<tr>");
			out.println(" <td headers=\"\" width=\"200\"><b><label for=\"label_info_module\">INFO_MODULE</label></b>:</td><td headers=\"\"><input type=\"text\" name=\"INFO_MODULE\" id=\"label_info_module\" /></td>");
			out.println("</tr>");
	
			out.println("<tr>");
			out.println(" <td headers=\"\" width=\"200\"><b><label for=\"label_image\">IMAGE</label></b>:</td><td headers=\"\"><input type=\"file\" name=\"IMAGE_FILE\" id=\"label_label\" /></td>");
			out.println("</tr>");
	
			out.println("<tr>");
			out.println(" <td headers=\"\" width=\"200\"><b><label for=\"label_alt_text\">IMAGE_ALT_TEXT</label></b>:</td><td headers=\"\"><input type=\"text\" name=\"IMAGE_ALT_TEXT\" id=\"label_alt_text\" /></td>");
			out.println("</tr>");
	
			out.println("<tr>");
			out.println(" <td headers=\"\" width=\"200\"><b><label for=\"label_info_desc\">INFO_DESC</label></b>:</td><td headers=\"\"><input type=\"text\" name=\"INFO_DESC\" id=\"label_info_desc\" /></td>");
			out.println("</tr>");
	
			out.println("<tr>");
			out.println(" <td headers=\"\" width=\"200\"><b><label for=\"label_link\">INFO_LINK</label></b>:</td><td headers=\"\"><input type=\"text\" name=\"INFO_LINK\" id=\"label_link\" /></td>");
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
	



	
	public static void uploadContent(Connection con, HttpServletRequest request, HttpServletResponse response, PrintWriter out) throws Exception {

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
			String sInfoType = "";
			String sInfoModule = "";
			String sImageAltText = "";
			String sInfoDesc = "";
			String sInfoLink = "";
			
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
	
				if (parm.equalsIgnoreCase("IMAGE_FILE")) {
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
					if (parm.equalsIgnoreCase("PROJECT_ID")) {
						sProjId = value;
					} else if (parm.equalsIgnoreCase("INFO_TYPE")) {
						sInfoType = value;
					} else if (parm.equalsIgnoreCase("INFO_MODULE")) {
						sInfoModule = value;
					} else if (parm.equalsIgnoreCase("IMAGE_ALT_TEXT")) {
						sImageAltText = value;
					} else if (parm.equalsIgnoreCase("INFO_DESC")) {
						sInfoDesc = value;
					} else if (parm.equalsIgnoreCase("INFO_LINK")) {
						sInfoLink = value;
					} else if (parm.equalsIgnoreCase("image_submit.x")) {
						sUpdateFlag = value;
					}
				}
			}
	
			int inStreamAvail = -1;
			
			if (inStream != null) {
				try {
					inStreamAvail = inStream.available();
					if (inStreamAvail > (100000000)) {
						sError.append("The File is over the 100MB limit.  Please use the DropBox for this file.<br /><br />");
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
				success = insertIntoTable(con,sProjId,sInfoType,sInfoModule,inStream,sImageAltText,sInfoDesc,sInfoLink, inStreamAvail);
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


	
	private static boolean insertIntoTable(Connection con, String sProjectId, String sInfoType, String sInfoModule, InputStream inStream, String sImageAltText, String sInfoDesc, String sInfoLink, int iSize) throws Exception {
		
		PreparedStatement pstmt = null;
		StringBuffer sQuery = new StringBuffer();
		boolean bReturn = false;
		
		try {

			sQuery.append("UPDATE ETS.ETS_PROJECT_INFO SET IMAGE=?,IMAGE_ALT_TEXT=?,INFO_DESC=?,INFO_LINK=? ");
			sQuery.append("WHERE PROJECT_ID=? AND INFO_TYPE=? AND INFO_MODULE=? ");

			pstmt = con.prepareStatement(sQuery.toString());

			pstmt.setBinaryStream(1,inStream,iSize);
			
			pstmt.setString(2, ETSUtils.escapeString(sImageAltText));
			pstmt.setString(3,ETSUtils.escapeString(sInfoDesc));
			pstmt.setString(4, ETSUtils.escapeString(sInfoLink));

			pstmt.setString(5,sProjectId);
			pstmt.setString(6,sInfoType);
			pstmt.setInt(7,Integer.parseInt(sInfoModule));
			
			int iResult = pstmt.executeUpdate();
			
			if (iResult <=0) {
			
				ETSDBUtils.close(pstmt);
				sQuery.setLength(0);
				
				sQuery.append("INSERT INTO ETS.ETS_PROJECT_INFO (PROJECT_ID,INFO_TYPE,INFO_MODULE,IMAGE,IMAGE_ALT_TEXT,INFO_DESC,INFO_LINK) ");
				sQuery.append("VALUES (?,?,?,?,?,?,?) ");
			
					
				pstmt = con.prepareStatement(sQuery.toString());
				pstmt.setString(1,sProjectId);
				pstmt.setString(2,sInfoType);
				pstmt.setInt(3,Integer.parseInt(sInfoModule));
				
				pstmt.setBinaryStream(4,inStream,iSize);
				
				pstmt.setString(5, ETSUtils.escapeString(sImageAltText));
				pstmt.setString(6,ETSUtils.escapeString(sInfoDesc));
				pstmt.setString(7, ETSUtils.escapeString(sInfoLink));
			
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
