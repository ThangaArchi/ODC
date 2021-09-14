package oem.edge.ets.fe;

/************************** BOF : HEADER *************************************/
/* IBM Confidential                                                          */
/* OCO Source Materials                                                      */
/* Identifier: IBM Customer Connect                                          */
/* (C) Copyright IBM Corp. 2002,2003                                     */
/* The source code for this program is not published or otherwise divested   */
/* of its trade secrets, irrespective of what has been deposited with the    */
/* U.S. Copyright Office                                                     */
/************************** EOF : HEADER *************************************/
/*																			 */
/*	File Name 	: 	EdesignMyFSEInfoServlet.java							 */
/*	Class Path	:	/servlet/oem/edge/ed/fe									 */
/*	Release		:	2.7, 2.8, 2.9											 */
/*	Description	:	Displays My Account Section Menu.						 */
/*	Created By	: 	unknown													 */
/*	Date		:	unknown													 */
/*****************************************************************************/
/*  Change Log 	: 	Please Enter Changed on, Changed by and Desc			 */
/*****************************************************************************/
/*	Changed On  : 	06/20/2001												 */
/*	Changed By  : 	Sathish													 */
/*	Change Desc : 	Added Log4j logging									 	 */
/*					Changed getting details of FSE Details to get 			 */
/*					from FSE Function 										 */
/* 					Displays one MASTER FSE if no FSE found for user		 */
/*****************************************************************************/

/**
 * @author: Sathish
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.PopupHeaderFooter;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.EncodeUtils;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.data.DocumentDAO;

import org.apache.commons.logging.Log;
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

public class ETSDeleteDocServlet extends javax.servlet.http.HttpServlet {
    
    public final static String Copyright = "(C) Copyright IBM Corp.  2002, 2003";
	private static final String CLASS_VERSION = "1.11";

	private static Log logger = EtsLogger.getLogger(ETSProjectsServlet.class);
	/**
	 * @see java.lang.Object#Object()
	 */
	public ETSDeleteDocServlet() {
		super();
	}


	/**
	 * Method getClassVersion.
	 * @return String
	 */
	public static String getClassVersion() {
		return CLASS_VERSION;
	}


	/**
	 * Method service.
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void service(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.io.IOException {

	    response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		Connection conn = null;
		String Msg = null;
		EdgeAccessCntrl es = new EdgeAccessCntrl();
		Hashtable params;

		int topCatId = 0;
		ETSCat topCat;
		ETSCat subCat;
		ETSCat currentCat;
		ETSUser user;
		String sLink;

		try {

			conn = ETSDBUtils.getConnection();

			if (!es.GetProfile(response, request, conn)) {
				return;
			}

			String sProjId = request.getParameter("proj");
			if (sProjId == null || sProjId.trim().equals("")) {
				sProjId = "";
			} else {
				sProjId = sProjId.trim();
			}

			String sCalId = request.getParameter("calid");
			if (sCalId == null || sCalId.trim().equals("")) {
				sCalId = "";
			} else {
				sCalId = sCalId.trim();
			}

			String sSubmitFlag = request.getParameter("image_submit.x");
			if (sSubmitFlag == null || sSubmitFlag.trim().equals("")) {
				sSubmitFlag = "";
			} else {
				sSubmitFlag = sSubmitFlag.trim();
			}
			if (sSubmitFlag == null || sSubmitFlag.trim().equals("")) {
				sSubmitFlag = request.getParameter("isDelete");
				if (sSubmitFlag == null || sSubmitFlag.trim().equals("")) {
					sSubmitFlag = "";
				}
			}

			UnbrandedProperties unBrandedprop = PropertyFactory.getProperty(conn,sProjId);
			
			params = ETSUtils.getServletParameters(request);

			PopupHeaderFooter header = new PopupHeaderFooter();
			header.setPageTitle(unBrandedprop.getAppName() + " - Details");
			//header.setHeader("Delete meeting documents");
			out.println(header.printPopupHeader());
			//out.println(header.printSubHeader());
			ETSUtils.popupHeaderLeft("Meeting documents","Delete meeting documents",out);

			DocumentDAO udDAO = new DocumentDAO();
			udDAO.prepare();
			ETSProj udProject = udDAO.getProjectDetails(sProjId);
			try {

				ResourceBundle pdResources = ResourceBundle.getBundle("oem.edge.ets.fe.ets-itar");
				String strBTV = pdResources.getString("ets.doc.btv.server");
				if ( udProject.isITAR() )  {
					String strPostAction = strBTV + "delDocFilesITAR.wss";
					out.println("<form onsubmit=\"return Validate()\" action=\"" +strPostAction +"\" method=\"post\" enctype=\"multipart/form-data\" name=\"deldocForm\" >");

					String strProjectId = udProject.getProjectId();
					String tc = request.getParameter("tc");
					int cc = (new Integer(request.getParameter("cc"))).intValue();
					int iDocId = -1;
					String strEncodedString = EncodeUtils.encode(String.valueOf(iDocId), strProjectId, es.gIR_USERN, tc, String.valueOf(cc) );
					
					out.println("<input type=\"hidden\" name=\"encodedToken\" value=\""+ strEncodedString+ "\" />");
					out.println("<input type=\"hidden\" name=\"formContext\" value=\"MEETINGS\" />");

//					String strReturnURL = "ETSProjectsServlet.wss?tc=" + tc + "&proj=" + udProject.getProjectId() + "&etsop=viewmeeting&meetid=" + sCalId; // + "&linkid=" + linkid;
					String strReturnURL = "ETSDeleteDocServlet.wss?tc=" + tc + "&cc=" + cc + "&proj=" + udProject.getProjectId() + "&calid=" + sCalId + "&cal_notify=N&isDelete=Y"; // + "&linkid=" + linkid;
	                out.println("<input type=\"hidden\" name=\"docAction\" value=\"" + strReturnURL + "\" />");
				} else {
					out.println("<form name=\"DeleteDocuments\" method=\"post\" action=\"" + Defines.SERVLET_PATH + "ETSDeleteDocServlet.wss\">");
				}
			} catch (SQLException e) {
				e.printStackTrace(System.out);
				throw e;
			} finally {
				udDAO.cleanup();
			}

			out.println("<script type=\"text/javascript\" language=\"javascript\">function cancel(){self.close();}</script>");
			out.println("<script type=\"text/javascript\" language=\"javascript\">function ok_close(){opener.location.reload();self.close();}</script>");

			out.println("<input type=\"hidden\" name=\"proj\" value=\"" + sProjId + "\" />");
			out.println("<input type=\"hidden\" name=\"calid\" value=\"" + sCalId + "\" />");

		    String docid = "docid";
			if( udProject.isITAR() ){
				docid = "delDocIds";
			}
			if (!sSubmitFlag.trim().equals("")) {

				String sDocId[] = request.getParameterValues(""+ docid +"");
				if (udProject.isITAR()) {
					Enumeration enum = request.getParameterNames();
					List lstDocIds = new ArrayList();
					while (enum.hasMoreElements()) {
					    String strParamName = (String) enum.nextElement();
					    if (strParamName.startsWith("delDocIds")) {
					        lstDocIds.add(request.getParameter(strParamName));
					    }
					}
					logger.debug("INSIDE DELETE PARAMS BLOCK");
					sDocId = new String[lstDocIds.size()];
					for(int i=0; i < lstDocIds.size(); i++) {
					    sDocId[i] = (String) lstDocIds.get(i);
					}
					logger.debug("DONE DELETE PARAMS BLOCK" + sDocId.length);
				}
				
				if (sDocId != null && sDocId.length > 0) {
					cancelCalendarEntry(conn,out,request,es);
					
					out.println("<br /><br />");
					out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td headers=\"\" width=\"30\" align=\"left\" valign=\"middle\"><a href=\"javascript:ok_close()\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"OK\" /></td><td headers=\"\" align=\"left\" valign=\"middle\"><a href=\"javascript:ok_close()\">Ok</a></a></td></tr></table>");
					out.println("<noscript><br />Javascript is not enabled, to continue please click the 'X' at the top right of this window and refresh your main window.</noscript>");
													
				}else{
					out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr><td headers=\"\"><span style=\"color:#cc6600\"><b>No Document(s) were selected for delete.</b></span><br /></td></tr></table>");
					out.println("<br /><br />");
				    out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td headers=\"\" width=\"30\" align=\"left\" valign=\"middle\"><a href=\"javascript:cancel()\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"OK\" /></td><td headers=\"\" align=\"left\" valign=\"middle\"><a href=\"javascript:cancel()\">Ok</a></a></td></tr></table>");
				    out.println("<noscript><br />Javascript is not enabled, to continue please click the 'X' at the top right of this window and refresh your main window.</noscript>");
			    }

			} else {
				displayCalendarEntry(conn,out,sProjId,sCalId);
			}

			out.println("</form>");
			out.println("<br /><br />");

			ETSUtils.popupHeaderRight(out);

			out.println(header.printPopupFooter());


		} catch (SQLException e) {
			logger.error(this,e);
		} catch (Exception e) {
			logger.error(this,e);
		} finally {
			ETSDBUtils.close(conn);
			out.flush();
			out.close();
		}
	}

	/**
	 * Method displayCalendarEntry.
	 * @param con
	 * @param out
	 * @param sProjectId
	 * @param sCalendarType
	 * @param sCalendarId
	 * @throws SQLException
	 * @throws Exception
	 */
	private static void displayCalendarEntry(Connection con, PrintWriter out, String sProjectId, String sCalendarId) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");

		try {

			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

			sQuery.append("SELECT CALENDAR_ID,SCHEDULE_DATE,SCHEDULED_BY,START_TIME,DURATION,SUBJECT,DESCRIPTION,INVITEES_ID,CC_LIST,WEB_CONF_FLAG,CANCEL_FLAG,EMAIL_FLAG,IBM_ONLY,REPEAT_TYPE,REPEAT_ID,REPEAT_END FROM ETS.ETS_CALENDAR WHERE CALENDAR_TYPE = 'M' AND PROJECT_ID = '" + sProjectId + "' AND ");
			sQuery.append("CALENDAR_ID = '" + sCalendarId + "' for READ ONLY ");

			logger.debug("ETSDeleteDocServlet::displayCalendarEntry()::QUERY : " + sQuery.toString());

			stmt = con.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			if (rs.next()) {

				String sCalID = ETSUtils.checkNull(rs.getString("CALENDAR_ID"));
				String sProjectID = sProjectId;
				String sCalType = "M";
				Timestamp tScheduleDate = rs.getTimestamp("SCHEDULE_DATE");
				String sScheduleBy = ETSUtils.checkNull(rs.getString("SCHEDULED_BY"));
				Timestamp tStartTime = rs.getTimestamp("START_TIME");
				int iDuration = rs.getInt("DURATION");
				String sSubject = ETSUtils.checkNull(rs.getString("SUBJECT"));
				String sDescription = ETSUtils.checkNull(rs.getString("DESCRIPTION"));
				String sInvitees = ETSUtils.checkNull(rs.getString("INVITEES_ID"));
				String sCCList = ETSUtils.checkNull(rs.getString("CC_LIST"));
				String sWebFlag = ETSUtils.checkNull(rs.getString("WEB_CONF_FLAG"));
				String sCancelFlag = ETSUtils.checkNull(rs.getString("CANCEL_FLAG"));
				String sEmailFlag = ETSUtils.checkNull(rs.getString("EMAIL_FLAG"));
				String sIBMOnly = ETSUtils.checkNull(rs.getString("IBM_ONLY"));
				String sRepeatType = ETSUtils.checkNull(rs.getString("REPEAT_TYPE"));
				String sRepeatID = ETSUtils.checkNull(rs.getString("REPEAT_ID"));
				Timestamp tRepeatEnd = rs.getTimestamp("REPEAT_END");

				ETSCal cal = new ETSCal();
				cal.setSCalendarId(sCalID);
				cal.setSProjectId(sProjectID);
				cal.setSCalType(sCalType);
				cal.setSScheduleDate(tScheduleDate);
				cal.setSScheduleBy(sScheduleBy);
				cal.setSStartTime(tStartTime);
				cal.setIDuration(iDuration);
				cal.setSSubject(sSubject);
				cal.setSDescription(sDescription);
				cal.setSInviteesID(sInvitees);
				cal.setSCCList(sCCList);
				cal.setSWebFlag(sWebFlag);
				cal.setSCancelFlag(sCancelFlag);
				cal.setSEmailFlag(sEmailFlag);
				cal.setSIBMOnly(sIBMOnly);
				cal.setSRepeatType(sRepeatType);
				cal.setSRepeatID(sRepeatID);
				cal.setSRepeatEnd(tRepeatEnd);

				String sTempDate = cal.getSStartTime().toString();

				String sDate = sTempDate.substring(5, 7) + "/" + sTempDate.substring(8, 10) + "/" + sTempDate.substring(0, 4);
				String sTime = sTempDate.substring(11, 16);


				out.println("<br />");

				out.println("<input type=\"hidden\" name=\"cal_notify\" value=\"" + sEmailFlag + "\" />");


				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
				out.println("<tr valign=\"top\">");
				out.println("<td colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				out.println("</tr>");
				out.println("<tr valign=\"top\">");
				out.println("<td colspan=\"4\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
				out.println("</tr>");
				out.println("<tr valign=\"top\">");
				out.println("<td colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				out.println("</tr>");

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

				String sTempDate1 = cal.getSScheduleDate().toString();

				String sDate1 = sTempDate1.substring(5, 7) + "/" + sTempDate1.substring(8, 10) + "/" + sTempDate1.substring(0, 4);

				long lTime = cal.getSStartTime().getTime() + (cal.getIDuration() * 60 * 1000);

				Timestamp timeEnd = new Timestamp(lTime);
				String sEndDate = timeEnd.toString();

				String sHour1 = sEndDate.substring(11, 13);
				String sMin1 = sEndDate.substring(14, 16);
				String sAMPM1 = "AM";

				if (Integer.parseInt(sHour1) == 12) {
					sHour1 = String.valueOf(Integer.parseInt(sHour1));
					sAMPM1 = "PM";
				} else if (Integer.parseInt(sHour1) > 12) {
					sHour1 = String.valueOf(Integer.parseInt(sHour1) - 12);
					sAMPM1 = "PM";
				}

				out.println("<tr valign=\"top\">");
				out.println("<td colspan=\"4\" align=\"left\"><span class=\"subtitle\">" + cal.getSSubject() + "</span><br /><br /></td>");
				out.println("</tr>");

				out.println("<tr valign=\"top\">");
				out.println("<td width=\"92\" class=\"small\" align=\"left\"><b>Meeting date</b>:</td>");
				out.println("<td width=\"132\" class=\"small\" align=\"left\">" + sDate + "</td>");
				out.println("<td width=\"94\" class=\"small\" align=\"left\"><b>Organized by</b>:</td>");
				out.println("<td width=\"125\" class=\"small\" align=\"left\">" + ETSUtils.getUsersName(con,cal.getSScheduleBy()) + "</td>");
				out.println("</tr>");

				out.println("<tr valign=\"top\">");
				out.println("<td width=\"92\" class=\"small\" align=\"left\"><b>Start time</b>:</td>");
				out.println("<td width=\"132\" class=\"small\" align=\"left\">" + sHour + ":" + sMin + sAMPM.toLowerCase() + " Eastern US</td>");
				out.println("<td width=\"94\" class=\"small\" align=\"left\"><b>Post date</b>:</td>");
				out.println("<td width=\"135\" class=\"small\" align=\"left\">" + sDate1 + "</td>");
				out.println("</tr>");

				out.println("<tr valign=\"top\">");
				out.println("<td width=\"92\" class=\"small\" align=\"left\"><b>End time</b>:</td>");
				out.println("<td width=\"132\" class=\"small\" align=\"left\">" + sHour1 + ":" + sMin1 + sAMPM1.toLowerCase() + " Eastern US</td>");
				out.println("<td width=\"94\" class=\"small\" align=\"left\">&nbsp;</td>");
				out.println("<td width=\"135\" class=\"small\" align=\"left\">&nbsp;</td>");
				out.println("</tr>");

				out.println("<tr valign=\"top\">");
				out.println("<td colspan=\"4\" align=\"left\"><br /></td>");
				out.println("</tr>");

				out.println("<tr valign=\"top\">");
				out.println("<td width=\"92\" class=\"small\" align=\"left\"><b>Description</b>:</td>");
				out.println("<td colspan=\"3\" class=\"small\" align=\"left\">" + cal.getSDescription() + "</td>");
				out.println("</tr>");

				out.println("<tr>");
				out.println("<td headers=\"\" colspan=\"4\" height=\"18\" >&nbsp;</td>");
				out.println("</tr>");

				String sRepeatValue = "";
				if (sRepeatID.trim().equalsIgnoreCase("")) {
					sRepeatValue = "THIS-IS-NOT-A-REPEAT";
				} else {
					sRepeatValue = sRepeatID;
				}

				Vector vDocs = ETSDatabaseManager.getDocsForMeetings(con,sProjectId,sCalendarId,sRepeatValue);

				if (vDocs != null && vDocs.size() > 0) {

					out.println("<tr valign=\"top\">");
					out.println("<td colspan=\"4\">");

					out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");
					if (cal.getSRepeatType() != null && !cal.getSRepeatType().trim().equalsIgnoreCase("N")) {
						out.println("<tr>");
						out.println("<td headers=\"\" colspan=\"4\" height=\"18\" class=\"small\">Documents marked with <span style=\"color:#cc6600\">*</span> applies to all instances of this meeting.</td>");
						out.println("</tr>");
					}
					out.println("<tr>");
					out.println("<td headers=\"\" colspan=\"4\" height=\"18\" class=\"tblue\">&nbsp;Meeting documents</td>");
					out.println("</tr>");

					out.println("<tr valign=\"top\">");
					out.println("<th id=\"nam\" colspan=\"2\" align=\"left\">");
					out.println("<span class=\"small\"><b>Name</b></span>");
					out.println("</th>");
					out.println("<th id=\"mod\" width=\"120\" align=\"left\">");
					out.println("<span class=\"small\"><b>Modified</b></span>");
					out.println("</th>");
					out.println("</tr>");
					out.println("<tr valign=\"top\">");
					out.println("<td colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"7\" alt=\"\" /></td>");
					out.println("</tr>");

				    DocumentDAO udDao = new DocumentDAO();
				    udDao.prepare();
				    ETSProj etsProj = udDao.getProjectDetails(sProjectId);

					for (int j = 0; j < vDocs.size(); j++) {

					    String docid = "docid";
						if( etsProj.isITAR() ){
							docid = "delDocIds"+ j;
						}

						ETSDoc doc = (ETSDoc) vDocs.elementAt(j);

						java.util.Date date = new java.util.Date(doc.getUpdateDate());
						String sDocDate = df.format(date);

						if ((j % 2) == 0) {
							out.println("<tr style=\"background-color: #eeeeee\">");
						} else {
							out.println("<tr >");
						}
						out.println("<td headers=\"nam\" width=\"16\" height=\"21\" align=\"left\"><input type=\"checkbox\" name=\""+ docid +"\" id=\"label_doc" + j + "\" value=\"" + doc.getId() + "\" /></td>");
						if (doc.getMeetingId().equalsIgnoreCase(sRepeatID)) {
							out.println("<td headers=\"nam\" width=\"150\" height=\"21\" align=\"left\" class=\"small\" valign=\"top\"><label for=\"label_doc" + j + "\"><span style=\"color:#cc6600\">*</span>" + doc.getName() + "</label></td>");
						} else {
							out.println("<td headers=\"nam\" width=\"150\" height=\"21\" align=\"left\" class=\"small\" valign=\"top\"><label for=\"label_doc" + j + "\">" + doc.getName() + "</label></td>");
						}

						out.println("<td headers=\"mod\" width=\"120\" height=\"21\" align=\"left\" class=\"small\" valign=\"top\">" + sDocDate + "</td>");
						out.println("</tr>");

					}

					out.println("</table>");

				}

				out.println("<tr valign=\"top\">");
				out.println("<td colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				out.println("</tr>");
				out.println("<tr valign=\"top\">");
				out.println("<td colspan=\"4\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
				out.println("</tr>");
				out.println("<tr valign=\"top\">");
				out.println("<td colspan=\"4\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				out.println("</tr>");

				out.println("</table>");
				out.println("<br />");

				out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				out.println("<td headers=\"\" align=\"left\"><b>Please select the documents you want to delete and click Submit.</b>");
				out.println("</td></tr></table>");

				out.println("<br />");

				if (cal.getSRepeatType() != null && !cal.getSRepeatType().trim().equalsIgnoreCase("N")) {
					out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
					out.println("<td headers=\"\" align=\"left\"><b><span style=\"color:#ff3333\">NOTE: If you have selected documents marked with <span style=\"color:#cc6600\">*</span>, then these documents will be deleted from all instances of the meeting including past meetings.</span></span></b>");
					out.println("</td></tr></table>");
				}


				out.println("<br />");

				out.println("<table summary=\"\" cellpadding\"0\" cellspacing=\"0\" width=\"100%\"><tr>");
				out.println("<td headers=\"\" width=\"140\" align=\"left\"><input type=\"image\" name=\"image_submit\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" width=\"120\" height=\"21\" alt=\"submit\" /></td>");
				out.println("<td headers=\"\" align=\"left\">");
				out.println("  <table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"><tr><td headers=\"\" width=\"25\" align=\"left\"><a href=\"javascript:cancel()\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"Cancel\" /></a></td><td headers=\"\">&nbsp;&nbsp;<a href=\"javascript:cancel()\">Cancel</a></td></tr></table>");
				out.println("  <noscript><br />Javascript is not enabled. To cancel, please click the 'X' at the top right corner of this window.</noscript>");
				out.println("</td></tr></table>");

			} else {
				out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\"><tr><td headers=\"\"><b>Calendar entry not found.</b></td></tr></table>");
			}

			out.println("<br /><br />");

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

	}

	/**
	 * Method updateCalendarEntry.
	 * @param conn
	 * @param out
	 * @param request
	 */
	private void cancelCalendarEntry(Connection conn, PrintWriter out, HttpServletRequest request, EdgeAccessCntrl es) throws SQLException, Exception {

		boolean bSuccess = false;

		try {

			String sCalendarId = ETSUtils.checkNull(request.getParameter("calid"));
			String sProjectId = ETSUtils.checkNull(request.getParameter("proj"));

		    DocumentDAO udDao = new DocumentDAO();
			udDao.setConnection(conn);
		    ETSProj etsProj = udDao.getProjectDetails(sProjectId);
		    String docid = "docid";
			if( etsProj.isITAR() ){
				docid = "delDocIds";
				// First order-of-business. If there are any docIds in the request
				// which need to be deleted - delete them first
				Enumeration enum = request.getParameterNames();
				while (enum.hasMoreElements()) {
				    String strParamName = (String) enum.nextElement();
				    if (strParamName.startsWith("delDocIds")) {
				        //Means this is a document to be marked for deletion
				        ETSDoc udDoc = new ETSDoc();
				        udDoc.setId(Integer.parseInt(request.getParameter(strParamName)));
				        udDoc.setProjectId(sProjectId);
				        udDao.delDoc(udDoc, es.gIR_USERN, true);
				        udDao.deleteDocFile(1, udDoc.getId());
			}
				}
			}
			else {
			String sDocId[] = request.getParameterValues(""+ docid +"");

			if (sDocId != null && sDocId.length > 0) {

				for (int i = 0; i < sDocId.length; i++) {

					ETSDoc doc = ETSDatabaseManager.getDocByIdAndProject(Integer.parseInt(sDocId[i]),sProjectId,conn);

					ETSDatabaseManager.delDoc(doc,es.gIR_USERN,true,conn);

				}
				}
			}
				out.println("<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr><td headers=\"\"><span style=\"color:#cc6600\"><b>Document(s) were successfully deleted.</b></span><br /></td></tr></table>");
				
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
		}

	}

}
