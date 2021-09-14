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
 * @author 		: v2sathis
 * Created on 		: Apr 27, 2004 
 */
package oem.edge.ets.fe.pmo;

import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSParams;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUtils;

public class ETSPMOfficeDisplay {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.14";

	public ETSPMOfficeDisplay() {
		super();
	}

	/**
	 * @param params
	 */
	public void handleRequest(ETSParams params) throws SQLException, Exception {

		try {
			
			HttpServletRequest request = params.getRequest();
			
			String sPMOID = request.getParameter("pmoid");
			if (sPMOID == null || sPMOID.trim().equals("")){
				displayPMODetails(params);	 
			} else {
				displayPMOObjectDetails(params,sPMOID);				
			}
			
		} catch (SQLException e){
			e.printStackTrace();
			throw e;
		} catch (Exception e){
			e.printStackTrace();
			throw e;
		}
		
	}
	
	private static void printRecursive(Connection con, PrintWriter out, ETSParams params, PreparedStatement pstmt, ETSPMODao pmoDAO, Vector vDetails, String ID, int iLevel) throws SQLException, Exception {
        
		int iMaxCols = 15;
		int iBlankColSpan = 0;
		int iFillColSpan = 0;
		
		try {
        
	        if (iLevel < iMaxCols) {
	        	iBlankColSpan = iLevel;
	        	iFillColSpan = 20 - iLevel;
	        } else {
				iBlankColSpan = iLevel;
				iFillColSpan = 20;
	        }
	
			for (int i = 0; i < vDetails.size(); i++) {        
	            
				ETSPMOffice pmo = new ETSPMOffice();
	            
				pmo = (ETSPMOffice) vDetails.elementAt(i);
	            
				if (pmo.getPMO_Parent_ID().trim().equalsIgnoreCase(ID)) {
					
					// print only the milestones for this release.. 
					// remove the below if statement to print all objects.
					
					System.out.println("OBJECT : " + pmo.getName());
	        		
	        		if (pmo.getName() != null && !pmo.getName().equalsIgnoreCase("") && !pmo.getType().trim().equalsIgnoreCase(Defines.PMO_CRIFOLDER) && !pmo.getType().trim().equalsIgnoreCase(Defines.DOCUMENT_FOLDER)) {
	        			
	        			pstmt.clearParameters();
	        			pstmt.setString(1,pmo.getPMOID());
	        			pstmt.setInt(2,Defines.PMO_MILESTONE_STATUS_RTF_ID);
	        			
	        			boolean isAvailable = pmoDAO.isStatusRTFAvailable(con,pstmt);
						boolean bisDocAvailable = pmoDAO.isDocumentsAvailable(con,params,pmoDAO,vDetails,pmo.getPMOID(),false);
	        			
	        			ETSProj proj = params.getETSProj();
	        			
						String sBaseFinish = "N/A";
						
						if (pmo.getBaseFinish() == null || pmo.getBaseFinish().toString().trim().equals("")) {
							sBaseFinish = "N/A";
						} else {
							sBaseFinish = ETSUtils.formatDate(pmo.getBaseFinish());
						}
						
						String sFinishType = pmo.getCurrFinishType();
						if (sFinishType == null || sFinishType.trim().equals("") || sFinishType.trim().equalsIgnoreCase("null")) {
							sFinishType = "N/A";
						} else {
							sFinishType = sFinishType.trim();
						}
						
						String sCurrDate = "N/A";
						if (pmo.getCurrFinish() == null || pmo.getCurrFinish().toString().trim().equals("")) {
							sCurrDate = "N/A";
						} else {
							sCurrDate = ETSUtils.formatDate(pmo.getCurrFinish());
						}
	        			
						out.println("<tr>");
						out.println("<td headers=\"\" class=\"small\" align=\"left\" width=\"143\" valign=\"top\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
						out.println("<td headers=\"\" align=\"left\"  valign=\"top\" width=\"" + iBlankColSpan * 10 + "\" >&nbsp;</td>");
						
						if (isAvailable || bisDocAvailable) {
							out.println("<td headers=\"\" align=\"left\" valign=\"top\" width=\"" + iFillColSpan * 10 + "\" ><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&linkid=" + params.getLinkId() + "&etsop=pmo&pmoid=" + pmo.getPMOID() + "\">" + pmo.getName() + "</a></td>");
						} else {
							out.println("<td headers=\"\" align=\"left\" valign=\"top\" width=\"" + iFillColSpan * 10 + "\" >" + pmo.getName() + "</td>");
						}
						
						out.println("</tr></table></td>");
						out.println("<td headers=\"\" class=\"small\" align=\"left\" valign=\"top\" width=\"100\">" + sBaseFinish + "</td>");
						out.println("<td headers=\"\" class=\"small\" align=\"left\" valign=\"top\" width=\"100\">" + sFinishType + "</td>");
						out.println("<td headers=\"\" class=\"small\" align=\"left\" valign=\"top\" width=\"100\">" + sCurrDate + " </td>");
		            	out.println("</tr>");
		            	
						// to make it print as a tree format upto 15 levels...
						//printRecursive(con,out,params,pstmt,pmoDAO,vDetails,pmo.getPMOID(),iLevel + 1);
						
						// to make it print as just one level...
						printRecursive(con,out,params,pstmt,pmoDAO,vDetails,pmo.getPMOID(),iLevel);
						
	        		}
					
				}
	            
			}
			
		} catch (SQLException e) {
			throw e;
		} catch (Exception e){
			throw e;
		}
        
	}
	
	private static void displayPMODetails(ETSParams params) throws SQLException, Exception {
		
		PreparedStatement pstmt = null;
		StringBuffer sQuery = new StringBuffer("SELECT RTF_NAME FROM ETS.ETS_PMO_RTF WHERE PARENT_PMO_ID = ? AND RTF_ID = ? for READ ONLY");

		try {
			
			ETSPMODao pmoDAO = new ETSPMODao();
		
			PrintWriter out = params.getWriter();
			Connection con = params.getConnection();
			ETSProj proj = params.getETSProj();
			
			// get the pm office objects...
			
			Vector vDet = pmoDAO.getPMOfficeObjects(con, proj.getPmo_project_id());		
	
			out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  height=\"18\" width=\"443\" class=\"subtitle\">Project information</td></tr></table>");
			out.println("<br />");
	
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"1\" border=\"0\"  width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" align=\"left\" width=\"143\"><b><span class=\"small\">Name</span></b></td>");
			out.println("<td headers=\"\" align=\"left\" width=\"100\"><b><span class=\"small\">Finish baseline</span></b></td>");
			out.println("<td headers=\"\" align=\"left\" width=\"100\"><b><span class=\"small\">Finish type</span></b></td>");
			out.println("<td headers=\"\" align=\"left\" width=\"100\"><b><span class=\"small\">Finish date</span></b></td>");
			out.println("</tr>");
			
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"4\">");
			out.println("<!-- Gray dotted line -->");
			out.println("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("<td headers=\"\" background=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" width=\"431\"><img alt=\"\" border=\"0\" height=\"1\" width=\"100%\" src=\"//www.ibm.com/i/c.gif\"/></td>");
			out.println("<td headers=\"\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("<!-- End Gray dotted line -->");
			out.println("</td>");
			out.println("</tr>");
	
			// display the project detail first and then print the rest of the stuff...
			
			// prepare the statement to get the status rtf from database.
			pstmt = con.prepareStatement(sQuery.toString());
			
			String sPMOProjectID = ""; 
			for (int i = 0; i < vDet.size(); i++) {
				
				ETSPMOffice pmOffice = (ETSPMOffice) vDet.elementAt(i);
				
				//if (pmOffice.getType().trim().equalsIgnoreCase(Defines.PMO_PROJECT)) {
				if (pmOffice.getPMOID().trim().equalsIgnoreCase(proj.getPmo_project_id())) {
					
					sPMOProjectID = pmOffice.getPMOID();
					
					String sBaseFinish = "N/A";
					
					if (pmOffice.getBaseFinish() == null || pmOffice.getBaseFinish().toString().trim().equals("")) {
						sBaseFinish = "N/A";
					} else {
						sBaseFinish = ETSUtils.formatDate(pmOffice.getBaseFinish());
					}
					
					String sFinishType = pmOffice.getCurrFinishType();
					if (sFinishType == null || sFinishType.trim().equals("") || sFinishType.trim().equalsIgnoreCase("null")) {
						sFinishType = "N/A";
					} else {
						sFinishType = sFinishType.trim();
					}
					
					String sCurrDate = "N/A";
					if (pmOffice.getCurrFinish() == null || pmOffice.getCurrFinish().toString().trim().equals("")) {
						sCurrDate = "N/A";
					} else {
						sCurrDate = ETSUtils.formatDate(pmOffice.getCurrFinish());
					}
				
					pstmt.clearParameters();
					pstmt.setString(1,pmOffice.getPMOID());
					pstmt.setInt(2,Defines.PMO_MILESTONE_STATUS_RTF_ID);
	        			
					boolean isAvailable = pmoDAO.isStatusRTFAvailable(con,pstmt);
					//boolean bisDocAvailable = pmoDAO.isDocumentsAvailable(con,params,pmoDAO,vDet,pmOffice.getPMOID(),false);


					out.println("<tr >");
					out.println("<td headers=\"\" class=\"small\" align=\"left\" width=\"200\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
					
					//if (isAvailable || bisDocAvailable) {
					if (isAvailable) {
						out.println("<td headers=\"\" align=\"left\" valign=\"top\" width=\"143\" ><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc="+params.getTopCat()+"&linkid=" + params.getLinkId() + "&etsop=pmo&pmoid=" + pmOffice.getPMOID() + "\">" + pmOffice.getName() + "</a></td>");
					} else {
						out.println("<td headers=\"\" align=\"left\" valign=\"top\" width=\"143\" >" + pmOffice.getName() + "</td>");
					}
					
					out.println("</tr></table></td>");
					out.println("<td headers=\"\" class=\"small\" valign=\"top\" align=\"left\" width=\"100\">" + sBaseFinish + "</td>");
					out.println("<td headers=\"\" class=\"small\" valign=\"top\" align=\"left\" width=\"100\">" + sFinishType + "</td>");
					out.println("<td headers=\"\" class=\"small\" valign=\"top\" align=\"left\" width=\"100\">" + sCurrDate + " </td>");
					out.println("</tr>");
					
					break;
					
				}
			}
			
	
			printRecursive(con,out,params,pstmt,pmoDAO,vDet,sPMOProjectID,1);

			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"4\">&nbsp;</td>");
			out.println("</tr>");
			
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"4\">");
			out.println("<!-- Gray dotted line -->");
			out.println("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("<td headers=\"\" background=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" width=\"431\"><img alt=\"\" border=\"0\" height=\"1\" width=\"100%\" src=\"//www.ibm.com/i/c.gif\"/></td>");
			out.println("<td headers=\"\" width=\"1\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("<!-- End Gray dotted line -->");
			out.println("</td>");
			out.println("</tr>");
			
			out.println("</table>");
			
			out.println("<br /><br />");
			
			boolean bisDocAvailable = pmoDAO.isDocumentsAvailable(con,params,pmoDAO,vDet,sPMOProjectID,false);

			if (bisDocAvailable) {
			
				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
				out.println("<tr>");
				out.println("<td headers=\"\"  height=\"18\" class=\"tblue\">&nbsp;Documents</td>");
				out.println("</tr>");
				out.println("</table>");			
	
				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"1\" border=\"0\"  width=\"443\">");
				out.println("<tr>");
				out.println("<td headers=\"\"  height=\"18\" align=\"left\" valign=\"top\" width=\"200\"><b>Document</b></td>");
				out.println("<td headers=\"\"  height=\"18\" align=\"left\" valign=\"top\" width=\"90\"><b>Modified</b></td>");
				out.println("<td headers=\"\"  height=\"18\" align=\"left\" valign=\"top\" width=\"60\"><b>Type</b></td>");
				out.println("<td headers=\"\"  height=\"18\" align=\"left\" valign=\"top\"><b>Size</b></td>");
				out.println("</tr>");
				out.println("</table>");
	
				boolean bOddRow = false;
	
				// display the object level documents first if available...
				Vector vDocs = pmoDAO.getPMODocuments(con,sPMOProjectID,proj.getPmo_project_id());
				
				if (vDocs != null && vDocs.size() > 0) {
					
					for (int i = 0; i < vDocs.size(); i++) {
						
						ETSPMODoc doc = (ETSPMODoc) vDocs.elementAt(i);
						out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"1\" border=\"0\"  width=\"443\">");
						if (bOddRow) {
							bOddRow = false;
							out.println("<tr style=\"background-color: #eeeeee\">");
						} else {
							bOddRow = true;
							out.println("<tr>");
						}
						
						out.println("<td headers=\"\"  height=\"18\" align=\"left\" valign=\"top\" width=\"200\"><a href=\"" + Defines.SERVLET_PATH + "ETSContentDeliveryServlet.wss/" + URLEncoder.encode(doc.getDocDesc()) + "?projid=" + proj.getProjectId() + "&pmodocid=" + doc.getDocId() + "&linkid=120000\" class=\"fbox\" target=\"new\">" + doc.getDocName() + "</a></td>");
						out.println("<td headers=\"\"  height=\"18\" align=\"left\" valign=\"top\" width=\"90\">" + ETSUtils.formatDate(doc.getUpdateDate()) + "</td>");
						out.println("<td headers=\"\"  height=\"18\" align=\"left\" valign=\"top\" width=\"60\">" + doc.getFileType() + "</td>");
						out.println("<td headers=\"\"  height=\"18\" align=\"left\" valign=\"top\">" +  formatSize(String.valueOf(doc.getUncompressedSize())) + "</td>");
						out.println("</tr>");
						out.println("</table>");				
					}
				}
				
				printRecursiveDocs(con,out,params,pmoDAO,vDet,sPMOProjectID, bOddRow);

				out.println("<br /><br />");
				
			}
			

			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\"  colspan=\"2\" align=\"left\"><b>Definitions:</b></td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" class=\"small\" colspan=\"2\">&nbsp;</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\"  width=\"100\" align=\"left\" valign=\"top\" class=\"small\"><b>Finish baseline:</b></td>");
			out.println("<td headers=\"\"  align=\"left\"  class=\"small\" >Finish date for a given task based on most recent contractual arrangement.</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" class=\"small\" colspan=\"2\">&nbsp;</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\"  width=\"100\" align=\"left\" valign=\"top\" class=\"small\"><b>Finish type:</b></td>");
			out.println("<td headers=\"\"  align=\"left\"  class=\"small\" >Finish type describes the type of finish date listed.</td>");
			out.println("</tr>");
//			out.println("<tr>");
//			out.println("<td headers=\"\"  width=\"100\" align=\"left\" valign=\"top\" class=\"small\">&nbsp;</td>");
//			out.println("<td headers=\"\"  align=\"left\"  class=\"small\" ><b>Plan</b></td>");
//			out.println("</tr>");
//			out.println("<tr>");
//			out.println("<td headers=\"\"  width=\"100\" align=\"left\" valign=\"top\" class=\"small\">&nbsp;</td>");
//			out.println("<td headers=\"\"  align=\"left\"  class=\"small\" ><b>Outlook</b></td>");
//			out.println("</tr>");
//			out.println("<tr>");
//			out.println("<td headers=\"\"  width=\"100\" align=\"left\" valign=\"top\" class=\"small\">&nbsp;</td>");
//			out.println("<td headers=\"\"  align=\"left\"  class=\"small\" ><b>Actual</b></td>");
//			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" class=\"small\" colspan=\"2\">&nbsp;</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\"  width=\"100\" align=\"left\" valign=\"top\" class=\"small\"><b>Finish date:</b></td>");
			out.println("<td headers=\"\"  align=\"left\"  class=\"small\" >When task is expected to be or has already completed.</td>");
			out.println("</tr>");
			out.println("</table>");

			
			out.println("<br />");
			
			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\" ><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" alt=\"Back\" border=\"0\" /></a></td>");
			out.println("<td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\"  class=\"fbox\">Back to '" + params.getCurrentTabName() + "'</a></td>");
			out.println("</tr>");
			out.println("</table>");
			
		
		} catch (SQLException e) {
			throw e;
		} catch (Exception e){
			throw e;
		} finally {
			ETSDBUtils.close(pstmt);
		}
		
	}
	
	private static void displayPMOObjectDetails(ETSParams params, String sPMOID) throws SQLException, Exception {
		
		Statement stmt = null;
		StringBuffer sQuery = new StringBuffer("SELECT RTF_BLOB FROM ETS.ETS_PMO_RTF WHERE PARENT_PMO_ID = ? AND RTF_ID = ? for READ ONLY");
		
		PreparedStatement pstmt = null;
		StringBuffer sQuery1 = new StringBuffer("SELECT RTF_NAME FROM ETS.ETS_PMO_RTF WHERE PARENT_PMO_ID = ? AND RTF_ID = ? for READ ONLY");

		try {
			
			ETSPMODao pmoDAO = new ETSPMODao();
		
			PrintWriter out = params.getWriter();
			Connection con = params.getConnection();
			ETSProj proj = params.getETSProj();
			
			// get the pm office object...
			
			ETSPMOffice pmOffice = pmoDAO.getPMOfficeObjectDetail(con, proj.getPmo_project_id(),sPMOID);		
	
	
			String sBaseFinish = "N/A";
					
			if (pmOffice.getBaseFinish() == null || pmOffice.getBaseFinish().toString().trim().equals("")) {
				sBaseFinish = "N/A";
			} else {
				sBaseFinish = ETSUtils.formatDate(pmOffice.getBaseFinish());
			}
					
			String sFinishType = pmOffice.getCurrFinishType();
			if (sFinishType == null || sFinishType.trim().equals("") || sFinishType.trim().equalsIgnoreCase("null")) {
				sFinishType = "N/A";
			} else {
				sFinishType = sFinishType.trim();
			}
					
			String sCurrDate = "N/A";
			if (pmOffice.getCurrFinish() == null || pmOffice.getCurrFinish().toString().trim().equals("")) {
				sCurrDate = "N/A";
			} else {
				sCurrDate = ETSUtils.formatDate(pmOffice.getCurrFinish());
			}

			out.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td headers=\"\"  height=\"18\" width=\"443\" class=\"subtitle\">" + ETSUtils.makeFirstLetterUpperCase(pmOffice.getType()) + "</td></tr></table>");
	
			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"1\" border=\"0\"  width=\"100%\">");
			
			// divider
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"2\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("</tr>");
	
			out.println("<tr >");
			out.println("<td headers=\"\" align=\"left\" width=\"120\"><b>Name:</b></td>");
			out.println("<td headers=\"\" align=\"left\" width=\"323\" >" + pmOffice.getName() + "</td>");
			out.println("</tr>");

			out.println("<tr >");
			out.println("<td headers=\"\" align=\"left\" width=\"120\"><b>Finish baseline:</b></td>");
			out.println("<td headers=\"\" align=\"left\" width=\"323\" >" + sBaseFinish + "</td>");
			out.println("</tr>");
			
			out.println("<tr >");
			out.println("<td headers=\"\" align=\"left\" width=\"120\"><b>Finish type:</b></td>");
			out.println("<td headers=\"\" align=\"left\" width=\"323\" >" + sFinishType + "</td>");
			out.println("</tr>");

			out.println("<tr >");
			out.println("<td headers=\"\" align=\"left\" width=\"120\"><b>Finish date:</b></td>");
			out.println("<td headers=\"\" align=\"left\" width=\"323\" >" + sCurrDate + " </td>");
			out.println("</tr>");
			
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"2\" >&nbsp;</td>");
			out.println("</tr>");

			// prepare the statement to get the status rtf from database.
			pstmt = con.prepareStatement(sQuery1.toString());

			pstmt.clearParameters();
			pstmt.setString(1,pmOffice.getPMOID());
			pstmt.setInt(2,Defines.PMO_MILESTONE_STATUS_RTF_ID);

			if (pmoDAO.isStatusRTFAvailable(con,pstmt)) {

				out.println("<tr>");
				out.println("<td headers=\"\" colspan=\"2\" height=\"18\" class=\"tblue\"><label for=\"label_status\">&nbsp;Status</label></td>");
				out.println("</tr>");
	
				out.println("<tr>");
				out.println("<td headers=\"\" colspan=\"2\" ><textarea id=\"label_status\" class=\"iform\" name=\"status\" cols=\"70\" rows=\"10\">" + pmoDAO.getPMORTF(con,pmOffice.getPMOID(),pmOffice.getPMO_Project_ID(),Defines.PMO_MILESTONE_STATUS_RTF_ID) + "</textarea></td>");
				out.println("</tr>");
	
				out.println("<tr>");
				out.println("<td headers=\"\" colspan=\"2\" >&nbsp;</td>");
				out.println("</tr>");
				
				// divider
				out.println("<tr>");
				out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" colspan=\"2\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"4\" alt=\"\" /></td>");
				out.println("</tr>");
			}
			
			out.println("</table>");
			
			Vector vDet = pmoDAO.getPMOfficeObjects(con, proj.getPmo_project_id());	
			boolean bisDocAvailable = pmoDAO.isDocumentsAvailable(con,params,pmoDAO,vDet,pmOffice.getPMOID(),false);

			if (bisDocAvailable) {
			
				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
				out.println("<tr>");
				out.println("<td headers=\"\"  height=\"18\" class=\"tblue\">&nbsp;Documents</td>");
				out.println("</tr>");
				out.println("</table>");			
	
				out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"1\" border=\"0\"  width=\"443\">");
				out.println("<tr>");
				out.println("<td headers=\"\"  height=\"18\" align=\"left\" valign=\"top\" width=\"200\"><b>Document</b></td>");
				out.println("<td headers=\"\"  height=\"18\" align=\"left\" valign=\"top\" width=\"90\"><b>Modified</b></td>");
				out.println("<td headers=\"\"  height=\"18\" align=\"left\" valign=\"top\" width=\"60\"><b>Type</b></td>");
				out.println("<td headers=\"\"  height=\"18\" align=\"left\" valign=\"top\"><b>Size</b></td>");
				out.println("</tr>");
				out.println("</table>");

				boolean bOddRow = false;
					
				// display the object level documents first if available...
				Vector vDocs = pmoDAO.getPMODocuments(con,sPMOID,proj.getPmo_project_id());
				
				if (vDocs != null && vDocs.size() > 0) {
					
					for (int i = 0; i < vDocs.size(); i++) {
						
						ETSPMODoc doc = (ETSPMODoc) vDocs.elementAt(i);
						out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"1\" border=\"0\"  width=\"443\">");
						if (bOddRow) {
							bOddRow = false;
							out.println("<tr style=\"background-color: #eeeeee\">");
						} else {
							bOddRow = true;
							out.println("<tr>");
						}

						out.println("<td headers=\"\"  height=\"18\" align=\"left\" valign=\"top\" width=\"200\"><a href=\"" + Defines.SERVLET_PATH + "ETSContentDeliveryServlet.wss/" + URLEncoder.encode(doc.getDocDesc()) + "?projid=" + proj.getProjectId() + "&pmodocid=" + doc.getDocId() + "&linkid=120000\" class=\"fbox\" target=\"new\">" + doc.getDocName() + "</a></td>");
						out.println("<td headers=\"\"  height=\"18\" align=\"left\" valign=\"top\" width=\"90\">" + ETSUtils.formatDate(doc.getUpdateDate()) + "</td>");
						out.println("<td headers=\"\"  height=\"18\" align=\"left\" valign=\"top\" width=\"60\">" + doc.getFileType() + "</td>");
						out.println("<td headers=\"\"  height=\"18\" align=\"left\" valign=\"top\">" +  formatSize(String.valueOf(doc.getUncompressedSize())) + "</td>");
						out.println("</tr>");
						out.println("</table>");				
					}
				}
				
				//Vector vDet = pmoDAO.getPMOfficeObjects(con, proj.getPmo_project_id());
				
				printRecursiveDocs(con,out,params,pmoDAO,vDet,sPMOID, bOddRow);

			}


			out.println("<br /><br />");
			
			
			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "&etsop=pmo\" ><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" alt=\"Back\" border=\"0\" /></a></td>");
			out.println("<td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "&etsop=pmo\"  class=\"fbox\">Back to project details</a></td>");
			out.println("</tr>");
			out.println("</table>");

			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"1\" border=\"0\"  width=\"100%\">");
			
			// divider
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"2\" style=\"background-color: #cccccc\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td headers=\"\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("</tr>");

			out.println("</table>");			

						
			out.println("<br /><br />");
			
			out.println("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			out.println("<tr>");
			out.println("<td headers=\"\" width=\"16\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\" ><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" alt=\"Back\" border=\"0\" /></a></td>");
			out.println("<td headers=\"\" align=\"left\"><a href=\"" + Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + params.getTopCat() + "&linkid=" + params.getLinkId() + "\"  class=\"fbox\">Back to '" + params.getCurrentTabName() + "'</a></td>");
			out.println("</tr>");
			out.println("</table>");
			
		
		} catch (SQLException e) {
			throw e;
		} catch (Exception e){
			throw e;
		} finally {
		}
		
	}

	private static boolean printRecursiveDocs(Connection con, PrintWriter out, ETSParams params, ETSPMODao pmoDAO, Vector vDetails, String ID, boolean bOddRow) throws SQLException, Exception {
	    
		
		try {
			
			for (int i = 0; i < vDetails.size(); i++) {        
	            
				ETSPMOffice pmo = new ETSPMOffice();
	            
				pmo = (ETSPMOffice) vDetails.elementAt(i);
	            
				if (pmo.getPMO_Parent_ID().trim().equalsIgnoreCase(ID)) {
					
	        		if (pmo.getType().trim().equalsIgnoreCase(Defines.DOCUMENT_FOLDER)) {
	        			
	        			ETSProj proj = params.getETSProj();
	        			
						// display the object level documents first if available...
						Vector vDocs = pmoDAO.getPMODocuments(con,pmo.getPMOID(),proj.getPmo_project_id());
			
						if (vDocs != null && vDocs.size() > 0) {
				
							for (int j = 0; j < vDocs.size(); j++) {
					
								ETSPMODoc doc = (ETSPMODoc) vDocs.elementAt(j);
								
								out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"1\" border=\"0\"  width=\"443\">");
								if (bOddRow) {
									bOddRow = false;
									out.println("<tr style=\"background-color: #eeeeee\">");
								} else {
									bOddRow = true;
									out.println("<tr>");
								}
								
								out.println("<td headers=\"\"  height=\"18\" align=\"left\" valign=\"top\" width=\"200\"><a href=\"" + Defines.SERVLET_PATH + "ETSContentDeliveryServlet.wss/" + URLEncoder.encode(doc.getDocDesc()) + "?projid=" + proj.getProjectId() + "&pmodocid=" + doc.getDocId() + "&linkid=120000\" class=\"fbox\" target=\"new\">" + doc.getDocName() + "</a></td>");
								out.println("<td headers=\"\"  height=\"18\" align=\"left\" valign=\"top\"width=\"90\">" + ETSUtils.formatDate(doc.getUpdateDate()) + "</td>");
								out.println("<td headers=\"\"  height=\"18\" align=\"left\" valign=\"top\"width=\"60\">" + doc.getFileType() + "</td>");
								out.println("<td headers=\"\"  height=\"18\" align=\"left\" valign=\"top\">" +  formatSize(String.valueOf(doc.getUncompressedSize())) + "</td>");
								out.println("</tr>");
								out.println("</table>");
							}
						}
										
						bOddRow = printRecursiveDocs(con,out,params,pmoDAO,vDetails,pmo.getPMOID(),bOddRow);
						
	        		}
					
				}
	            
			}
			
			return bOddRow;
			
		} catch (SQLException e) {
			throw e;
		} catch (Exception e){
			throw e;
		}
	    
	}

	/**
	 * @return java.lang.String
	 * @param sDate java.lang.String
	 * @exception java.lang.Exception The exception description.
	 */
	private static String formatSize(String sSize) throws java.lang.Exception {
		
		java.text.DecimalFormat df = new java.text.DecimalFormat("0.0");
	    
		String sTempSize = "";
		double dSize = Double.parseDouble(sSize);
		if (dSize < 1024) {
			sTempSize = sSize + " Bytes";
		} else {
			dSize = dSize / 1024;
			if (dSize < 1024) {
				sTempSize = df.format(dSize) + " KB";
			} else {
				dSize = dSize / 1024;
				if (dSize < 1024) {
					sTempSize = df.format(dSize) + " MB";
				} else {
					dSize = dSize / 1024;
					sTempSize = df.format(dSize) + " GB";
				}
			}
		}
	    
		return sTempSize;
	    
	}		

}
