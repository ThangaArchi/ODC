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
import java.sql.Connection;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.Metrics;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;


public class ETSUserInfoServlet extends HttpServlet {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	
	private static final String CLASS_VERSION = "1.5";
	
	public static Log logger = EtsLogger.getLogger(ETSUserInfoServlet.class);
	
	public ETSUserInfoServlet() {
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		performGetPost(req, resp);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		performGetPost(req, resp);
	}
	
	protected void performGetPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		String formCharset = "ISO_8859-1";
		String parm = null;
		InputStream inStream = null;
		InputStream inStream2 = null;
		String fileName = ""; //of photo
		String cvfileName = ""; //of cv
		String skills = "";
		boolean cvFlag = false;
		String projectid = null;
		String currentcatid = null;
		String topcatid = null;
		String linkid = "251000";
		int delcvfile = 0;
		String action = "";
		boolean errorFlag = false;
		String[] err = new String[2];
		int inStreamAvail = -1;
		EdgeAccessCntrl es = new EdgeAccessCntrl();
		Connection conn = null;
		String urlAction = "";
		String errCode = "";
		
		try {
			
			try {
				
				conn = ETSDBUtils.getConnection();
				if (!es.GetProfile(resp, req, conn)) {
					return;
				}
				Vector mult = MimeMultipartParser.getBodyParts(req.getInputStream(), req.getIntHeader("Content-Length"));

				int count = mult.size();
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
					if (parm.equalsIgnoreCase("photofile")) {
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
							logger.debug("*****filename=" + fileName);
							urlAction = "addmemph";
						} else {
							logger.debug("No file name was submitted to be uploaded.");
							//String[] err = {"0","No file name was submitted to be upload"};
							errorFlag = true;
							err = new String[] { "1", "1" };
							errCode = "1";
							urlAction = "addmemph";
							break;
							//return err;
						}
					} else if (parm.equalsIgnoreCase("cvfile")) {
						// Set our input stream
						inStream = part.getContentInputStream();
						//inStream2 = part.getContentInputStream();
						cvfileName = part.getDisposition(Defines.FILE_MULTIPART_DISPOSITION_FILENAME, formCharset);
						if (cvfileName.length() > 0) {
							cvFlag = true;
							int lastBackSlash = cvfileName.lastIndexOf("\\"); // Windows based
							int lastForwardSlash = cvfileName.lastIndexOf(Defines.SLASH); // Unix based
							if (lastBackSlash > 0) {
								cvfileName = cvfileName.substring(lastBackSlash + 1, cvfileName.length());
							} else if (lastForwardSlash > 0) {
								cvfileName = cvfileName.substring(lastForwardSlash + 1, cvfileName.length());
							}
							cvfileName = cvfileName.replace(' ', '_');
							logger.debug("*****cvfilename=" + cvfileName);
							urlAction = "editmeminfo";
						}
					} else {
						if (parm.equalsIgnoreCase("action")) {
							action = value;
						} else if (parm.equalsIgnoreCase("proj")) {
							projectid = value;
						} else if (parm.equalsIgnoreCase("cc")) {
							currentcatid = value;
						} else if (parm.equalsIgnoreCase("tc")) {
							topcatid = value;
						} else if (parm.equalsIgnoreCase("linkid")) {
							linkid = value;
						} else if (parm.equalsIgnoreCase("skills")) {
							skills = value;
						} else if (parm.equalsIgnoreCase("delcvfile")) {
							String checkdel = value;
							if (checkdel.equals("1")) {
								delcvfile = 1;
							}
						} else {
							logger.debug("**************in wrong path");
						}
					}
				}
				if (!errorFlag) {
					if (action.equals("addmemph2") || cvFlag) {
						if (inStream == null) {
							logger.debug("writeDocument -- Input stream not found for file.");
							errorFlag = true;
							err = new String[] { "1", "Input connection for file not found, please try again" };
							errCode = "2";
							//return err;
						}
						try {
							inStreamAvail = inStream.available();
							if (inStreamAvail > (1048576)) {
								logger.debug("writeDocument -- File over 1 Meg limit.");
								errorFlag = true;
								err = new String[] { "1", "3" };
								errCode = "3";
								//return err;
							}
						} catch (IOException ioe) {
							logger.error("ioe ex for instreamavail(). e=" + ioe.toString(), ioe);
							//String[] err = {"1","Error occurred, please try again"};
							errorFlag = true;
							err = new String[] { "1", "4" };
							errCode = "4";
							//return err;
						}
						logger.debug("writeDocument -- Input stream reports " + inStreamAvail + " bytes available.");
					}
				}
			} catch (Exception e) {
				//err = new String[]{"1","Error occurred, please try again"};
				err = new String[] { "1", "0" };
				logger.error("error in etsconup:: e=" + e.toString(), e);
				e.printStackTrace();
				errorFlag = true;
				errCode = "4";
				//return err;
			}
			if (errorFlag) {
				resp.sendRedirect("ETSProjectsServlet.wss?action=" + urlAction + "&uid=" + es.gIR_USERN + "&proj="+projectid+"&tc="+topcatid+"&cc="+currentcatid+"&msg="+errCode+"&linkid="+linkid);
			} else {
				try {
					boolean success = false;
					if (action.equals("addmemph2")) {
						if (ETSUtils.isUserPhotoAvailable(conn, es.gIR_USERN)) {
							success = ETSDatabaseManager.updateUserPhoto(es.gIR_USERN, inStream, inStreamAvail);
						} else {
							success = ETSDatabaseManager.addUserPhoto(es.gIR_USERN, inStream, inStreamAvail);
						}
					} else if (action.equals("editmeminfo2")) {
						if (delcvfile == 1) {
							success = ETSDatabaseManager.deleteUserCV(es.gIR_USERN);
						}
						if (cvFlag) {
							if (ETSUtils.isUserPhotoAvailable(conn, es.gIR_USERN)) {
								success = ETSDatabaseManager.updateUserCVSkills(es.gIR_USERN, inStream, inStreamAvail, cvfileName, skills);
							} else {
								success = ETSDatabaseManager.addUserCVSkills(es.gIR_USERN, inStream, inStreamAvail, cvfileName, skills);
							}
						} else {
							success = ETSDatabaseManager.updateUserSkills(es.gIR_USERN, skills);
						}
					}
					logger.debug("success = " + String.valueOf(success));

// To fix appropriate left-nav 7.1.1 (SPR #BSOD724DK2) - thanga
ETSProj udProject =  ETSDatabaseManager.getProjectDetails(projectid);
if( udProject.getProjectType().equals(Defines.AIC_WORKSPACE_TYPE) ){
	linkid = Defines.AIC_LINKID;
} else {
	linkid = Defines.LINKID;
}
					if (success) {
						if (action.equals("addmemph2")) {
							Metrics.appLog(conn, es.gIR_USERN, "ETS_Mem_Photo_Add");
							resp.sendRedirect("ETSProjectsServlet.wss?action=memberdetails&uid=" + es.gIR_USERN + "&proj=" + projectid + "&tc=" + topcatid + "&cc=" + currentcatid + "&linkid=" + linkid);
						}
						if (action.equals("editmeminfo2")) {
							Metrics.appLog(conn, es.gIR_USERN, "ETS_Mem_Info_Edit");
							resp.sendRedirect("ETSProjectsServlet.wss?action=memberdetails&uid=" + es.gIR_USERN + "&proj=" + projectid + "&tc=" + topcatid + "&cc=" + currentcatid + "&linkid=" + linkid);
						}
					} else {
						err = new String[] { "1", "5" };
						if (action.equals("addmemph2")) {
							resp.sendRedirect("ETSProjectsServlet.wss?action=addmemph&uid=" + es.gIR_USERN + "&proj=" + projectid + "&tc=" + topcatid + "&cc=" + currentcatid + "&msg=" + err[1] + "&linkid=" + linkid);
						}
						if (action.equals("editmeminfo2")) {
							resp.sendRedirect("ETSProjectsServlet.wss?action=editmeminfo&uid=" + es.gIR_USERN + "&proj=" + projectid + "&tc=" + topcatid + "&cc=" + currentcatid + "&msg=" + err[1] + "&linkid=" + linkid);
						}
						//return err;
					}
				} catch (Exception e) {
					//resp.sendRedirect(Defines.SERVLET_PATH+"ETSProjectsServlet.wss?"+action_meeting+"proj="+projectid+"&tc="+topcatid+"&cc="+currentcatid+"&msg=0&linkid="+linkid);
					resp.sendRedirect("ETSProjectsServlet.wss?action=addmemph&uid=" + es.gIR_USERN + "&proj=" + projectid + "&tc=" + topcatid + "&cc=" + currentcatid + "&msg=0&linkid=" + linkid);
				}
			}
		} finally {
			ETSDBUtils.close(conn);
		}
	}
	
	private String getParameter(HttpServletRequest req, String key) {
		String value = req.getParameter(key);
		if (value == null) {
			return "";
		} else {
			return value;
		}
	}


}

