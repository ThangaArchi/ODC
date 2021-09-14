package oem.edge.ets.fe;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2000-2004                                     */
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import oem.edge.amt.AmtHeaderFooter;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.LeftNavExceptions;
import oem.edge.ets.fe.common.EtsLogger;
import org.apache.commons.logging.Log;
public class ETSSurveyUploadServlet extends HttpServlet {
	
	public static final String VERSION = "1.7";
	
	private static Log logger= EtsLogger.getLogger(ETSSurveyUploadServlet.class);
	public void service(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter writer= null;
		Connection conn= null;
		EdgeAccessCntrl es= new EdgeAccessCntrl();
		String sLink= null;
		Hashtable params;
		String formCharset= "ISO_8859-1";
		String parm= null;
		InputStream inStream= null;
		String fileName= null;
		String year= "2005";
		String action= "null";
		ArrayList data= new ArrayList();
		
		try {
		
			try {
				conn= ETSDBUtils.getConnection();
				//System.out.println("After DB Connection");
				if (!es.GetProfile(response, req, conn)) {
					return;
				}
				if (!es.qualifyEntitlement(Defines.ETS_ADMIN_ENTITLEMENT)) {
					response.sendRedirect("ETSErrorServlet.wss?ecode=ETS-ACCESS-DENIED&sname=Access+Denied");
					return;
				}
				if (logger.isDebugEnabled()) {
					logger.debug("ETSSurveyUploadServlet");
				}
				//System.out.println("w " + new java.util.Date());
				Vector mult= MimeMultipartParser.getBodyParts(req.getInputStream(), req.getIntHeader("Content-Length"));
				//System.out.println("e " + new java.util.Date());
				int count= mult.size();
				for (int i= 0; i < count; ++i) {
					WebAccessBodyPart part= (WebAccessBodyPart)mult.elementAt(i);
					if (part.getDisposition("name", "ISO_8859-1").equalsIgnoreCase("form-charset")) {
						formCharset= part.getContentAsString("ISO_8859-1");
					}
				}
				for (int i= 0; i < count; ++i) {
					WebAccessBodyPart part= (WebAccessBodyPart)mult.elementAt(i);
					parm= part.getDisposition("name", formCharset);
					String value= (part.getContentAsString(formCharset)).trim();
					if (parm.equalsIgnoreCase(Defines.FILE_FORM_NAME_CLIENT_FILE_NAME)) {
						// Set our input stream
						inStream= part.getContentInputStream();
						//inStream2 = part.getContentInputStream();
						fileName= part.getDisposition(Defines.FILE_MULTIPART_DISPOSITION_FILENAME, formCharset);
						if (fileName.length() > 0) {
							int lastBackSlash= fileName.lastIndexOf("\\"); // Windows based
							int lastForwardSlash= fileName.lastIndexOf(Defines.SLASH); // Unix based
							if (lastBackSlash > 0) {
								fileName= fileName.substring(lastBackSlash + 1, fileName.length());
							} else if (lastForwardSlash > 0) {
								fileName= fileName.substring(lastForwardSlash + 1, fileName.length());
							}
							fileName= fileName.replace(' ', '_');
							//System.out.println("*****filename=" + fileName);
							byte[] bbuf= new byte[1024 * 8];
							int result;
							BufferedReader bif= new BufferedReader(new InputStreamReader(inStream));
							String s;
							while ((s= bif.readLine()) != null) {
								data.add(s);
								// Complicated rules for csv out
								//System.out.println(s);
							}
						} else {
							//System.out.println("No file name was submitted to be uploaded.");
						}
					} else if (parm.equalsIgnoreCase("action")) {
						action= value;
					} else if (parm.equalsIgnoreCase("year")) {
						year= value.trim();
					} else if (parm.equalsIgnoreCase("linkid")) {
						sLink= value.trim();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable hs= new Hashtable();
			if (sLink == null || sLink.equals("")) {
				hs.put("linkid", Defines.LINKID);
				sLink= Defines.LINKID;
			}
			try {
				AmtHeaderFooter EdgeHeader= new AmtHeaderFooter(es, conn, hs);
				response.setContentType("text/html");
				writer= response.getWriter();
				EdgeHeader.setPopUp("J");
				String assocCompany= es.gASSOC_COMP;
				String header= "E&TS Connect";
				EdgeHeader.setPageTitle(header);
				EdgeHeader.setHeader(header);
				EdgeHeader.setSubHeader("Upload Survey " + (action.equals("addSurveyData") ? "Data" : "Questions"));
				writer.println(ETSSearchCommon.getMasthead(EdgeHeader));
				writer.println(EdgeHeader.printBullsEyeLeftNav());
				writer.println(EdgeHeader.printSubHeader());
				PreparedStatement c1= null;
				PreparedStatement c2= null;
				PreparedStatement c3= null;
				try {
					if (action.equals("questionData") || action.equals("referenceData") || action.equals("mappingData")) {
						String q3= "";
						int no_of_field= 0;
						String error= "";
						conn.commit();
						boolean errorFlag= false;
						conn.setAutoCommit(false);
						ArrayList upper= new ArrayList();
						if (action.equals("questionData")) {
							ETSSupportFunctions.fireUpdate(conn, "delete from ets.ETS_SURVEY_QUES where year='" + year + "'");
							q3= "insert into ets.ETS_SURVEY_QUES (year,question_id,DESCRIPTION) values ('" + year + "',(?),(?))";
							no_of_field= 2;
							upper.add(new Integer(0));
						} else if (action.equals("referenceData")) {
							ETSSupportFunctions.fireUpdate(conn, "delete from ets.ETS_SURVEY_REF where year='" + year + "'");
							q3= "insert into ets.ETS_SURVEY_REF (year,reference,key,DESCRIPTION) values ('" + year + "',(?),(?),(?))";
							no_of_field= 3;
						} else {
							ETSSupportFunctions.fireUpdate(conn, "delete from ets.ETS_SURVEY_MAPPING where year='" + year + "'");
							q3=
								"insert into ets.ETS_SURVEY_MAPPING (year,SEQ_NO,QUESTION_ID,QUESTION_GROUP,RESPONSE_TYPE,RESPONSE_OTHER,MAPPING_QUERY) values ('"
									+ year
									+ "',(?),(?),(?),(?),(?),(?))";
							no_of_field= 6;
							upper.add(new Integer(1));
						}
						c3= conn.prepareStatement(q3);
						int currentIndex= 0;
						for (int idx= currentIndex; idx < data.size(); idx++) {
							currentIndex= idx + 1;
							ArrayList values= ETSSupportFunctions.getCSVStringTokens(data, idx, false);
							System.out.println("v is " + values);
							int x= ETSSupportFunctions.parseInt((String)values.get(0), 0);
							if (x > 0)
								idx= idx + x;
							if (values.size() - 1 != no_of_field) {
								error += "<br />The line number " + (idx + 1) + " " + values + " is not uploaded correctly.";
								errorFlag= true;
								continue;
							}
							c3.clearParameters();
							for (int nof= 0; nof < no_of_field; nof++) {
								String val= (String)values.get(nof + 1);
								if (upper.contains(new Integer(nof)))
									c3.setString(nof + 1, val.toUpperCase());
								else
									c3.setString(nof + 1, val);
							}
							c3.executeUpdate();
						}
						if (errorFlag) {
							writer.println("<b>Upload of Survey question file " + fileName + " encountered errors!. </b>" + error + "<br />");
						} else
							writer.println("<b>Upload of Survey question file " + fileName + " Successfully Done. </b><br />");
						if (!errorFlag)
							conn.commit();
						else
							conn.rollback();
						printButton(writer);
					} else if (action.equals("addSurveyData")) {
						ArrayList keys= null;
						String q1= "select count(*) as cnt from ets.ETS_SURVEY_DATA where SURVEY_YEAR=(?) and RESPONSE_ID=(?) and SURVEY_KEY=(?)";
						String q2=
							"update ets.ETS_SURVEY_DATA set SURVEY_VALUE=(?),LAST_TIMESTAMP=current timestamp where SURVEY_YEAR=(?) and RESPONSE_ID=(?) and SURVEY_KEY=(?)";
						String q3=
							"insert into ets.ETS_SURVEY_DATA (SURVEY_YEAR,RESPONSE_ID,SURVEY_KEY,SURVEY_VALUE,LAST_TIMESTAMP) values ((?),(?),(?),(?),current timestamp)";
						c1= conn.prepareStatement(q1);
						c2= conn.prepareStatement(q2);
						c3= conn.prepareStatement(q3);
						String mandatoryFields[]= { "lname", "fname", "coname" };
						// Check for coname -> status is empty or O
						int mandatoryFieldsIndex[]= new int[mandatoryFields.length];
						int total= 0;
						int companyIndex= -1;
						//get key line
						String s= (String)data.get(0);
						keys= ETSSupportFunctions.getCSVStringTokens1(s);
						// first one is the responseid
						//System.out.println("The size is :" + keys.size());
						total= keys.size();
						for (int i= 0; i < mandatoryFields.length; i++) {
							mandatoryFieldsIndex[i]= keys.indexOf(mandatoryFields[i]);
							if (mandatoryFieldsIndex[i] == -1) {
								throw new Exception("<b>Please Check header column. The first Row in header must have mandatory fields : lname, fname and coname</b>");
							}
						}
						companyIndex= keys.indexOf("coname");
						int currentIndex= 1;
						for (int idx= currentIndex; idx < data.size(); idx++) {
							currentIndex= idx + 1;
							ArrayList values= ETSSupportFunctions.getCSVStringTokens(data, idx, false);
							System.out.println("v is " + values);
							int x= ETSSupportFunctions.parseInt((String)values.get(0), 0);
							if (x > 0)
								idx= idx + x;
							String respid= (String)values.get(1);
							boolean canContinue= true;
							for (int i= 0; canContinue && i < mandatoryFieldsIndex.length; i++) {
								if (((String)values.get(mandatoryFieldsIndex[i] + 1)).trim().equals(""))
									canContinue= false;
							}
							if (!canContinue)
								continue;
							String company= (String)values.get(companyIndex + 1);
							if (ETSSupportFunctions
								.getValue(conn, "select STATUS from ets.ETS_SURVEY_STATUS where SURVEY_YEAR='" + year + "' and COMPANY='" + company + "'")
								.equals("C"))
								continue;
							for (int i= 1; i < values.size() && i < total; i++) {
								String key= ((String)keys.get(i)).toUpperCase();
								String val= (String)values.get(i + 1);
								if (presentSurveyValue(c1, year, respid, key))
									updateSurveyValue(c2, year, respid, key, val);
								else
									insertSurveyValue(c3, year, respid, key, val);
							}
						}
						writer.println("<b>Upload of Survey data file " + fileName + " Successfully Done. </b><br />");
						printButton(writer);
					}
				} catch (SQLException e) {
					e.printStackTrace();
					writer.println(e.getMessage());
					try {
						conn.rollback();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
					writer.println(e.getMessage());
				} finally {
					close(c1);
					close(c2);
					close(c3);
					try {
						conn.setAutoCommit(true);
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
				writer.println(EdgeHeader.printBullsEyeFooter());
			} catch (LeftNavExceptions e) {
				e.printStackTrace();
			}
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	boolean presentSurveyValue(PreparedStatement pc, String year, String respid, String key) throws SQLException, Exception {
		ResultSet rs= null;
		pc.clearParameters();
		pc.setString(1, year);
		pc.setString(2, respid);
		pc.setString(3, key);
		try {
			rs= pc.executeQuery();
			if (rs.next()) {
				String cnt= rs.getString("cnt").trim();
				int i= Integer.parseInt(cnt);
				if (i > 0)
					return true;
			}
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	void updateSurveyValue(PreparedStatement pc, String year, String respid, String key, String val) throws SQLException, Exception {
		pc.clearParameters();
		pc.setString(1, val);
		pc.setString(2, year);
		pc.setString(3, respid);
		pc.setString(4, key);
		pc.executeUpdate();
	}
	void insertSurveyValue(PreparedStatement pc, String year, String respid, String key, String val) throws SQLException, Exception {
		pc.clearParameters();
		pc.setString(1, year);
		pc.setString(2, respid);
		pc.setString(3, key);
		pc.setString(4, val);
		pc.executeUpdate();
	}
	void close(PreparedStatement pc) {
		try {
			if (pc != null)
				pc.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	void printButton(PrintWriter writer) {
		writer.println("<br /><table><tr><td headers=\"\" valign=\"middle\">");
		writer.println(
			"<td headers=\"\" valign=\"middle\"><a href=\"ETSSurveyServlet.wss?action=addSurveyQuestion\"><img border=\"0\" name=\"back\" src=\"" + Defines.BUTTON_ROOT +"arrow_lt.gif\" width=\"21\" height=\"21\" align=\"bottom\" alt=\"Back\" /></a></td>");
		writer.println("<td headers=\"\" valign=\"middle\"><a href=\"ETSSurveyServlet.wss?action=addSurveyQuestion\">Upload master data</a></td>");
		writer.println(
			"<td headers=\"\" valign=\"middle\"><a href=\"ETSSurveyServlet.wss?action=addSurveyData\"><img border=\"0\" name=\"back\" src=\"" + Defines.BUTTON_ROOT +"arrow_lt.gif\" width=\"21\" height=\"21\" align=\"bottom\" alt=\"Back\" /></a></td>");
		writer.println("<td headers=\"\" valign=\"middle\"><a href=\"ETSSurveyServlet.wss?action=addSurveyData\">Upload result data</a></td>");
		writer.println(
			"<td headers=\"\" valign=\"middle\"><a href=\"ETSConnectServlet.wss?linkid=251000&pghead=E&TS+Connect&pgtitle=E&TS+Connect\"><img border=\"0\" name=\"back\" src=\"" + Defines.BUTTON_ROOT +"arrow_lt.gif\" width=\"21\" height=\"21\" align=\"bottom\" alt=\"Back\" /></a></td>");
		writer.println(
			"<td headers=\"\" valign=\"middle\"><a href=\"ETSConnectServlet.wss?linkid=251000&pghead=E&TS+Connect&pgtitle=E&TS+Connect\">Back to main page</a></td>");
		writer.println("</tr></table><br />");
	}
}