/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004                                          */
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

package oem.edge.ets.fe.ismgt.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSMimeDataList;
import oem.edge.ets.fe.MimeMultipartParser;
import oem.edge.ets.fe.WebAccessBodyPart;
import oem.edge.ets.fe.ismgt.dao.EtsCrPmoIssueDocDAO;
import oem.edge.ets.fe.ismgt.model.EtsCrAttach;
import oem.edge.ets.fe.ismgt.model.EtsCrProbInfoModel;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PmoFileUtils implements EtsIssFilterConstants, EtsIssueConstants {

	public static final String VERSION = "1.1";

	/**
	 * 
	 */
	public PmoFileUtils() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
				 * to attach files for a given issue/change
				 * returns an array of code/messg for success/failure
				 * 
				 */

	public String[] doAttachForCr(HttpServletRequest request, EtsCrProbInfoModel crInfoModel) {

		try {

			Vector mult = MimeMultipartParser.getBodyParts(request.getInputStream());
			int count = mult.size();
			String edge_problem_id = AmtCommonUtils.getTrimStr(request.getParameter("edge_problem_id"));
			String etsId = AmtCommonUtils.getTrimStr(request.getParameter("etsId"));
			String filenumber = AmtCommonUtils.getTrimStr(request.getParameter("attach_file_no"));
			String cancelstate = AmtCommonUtils.getTrimStr(request.getParameter("cancelstate"));

			Global.println("etsId in doAttach for CR===" + etsId);
			Global.println("edge problem Id in doAttach for CR===" + edge_problem_id);
			Global.println("filenumber in doAttach===" + filenumber);
			Global.println("cancelstate===" + cancelstate);

			if (!AmtCommonUtils.isResourceDefined(etsId) && AmtCommonUtils.isResourceDefined(edge_problem_id)) {

				etsId = edge_problem_id;
			}

			Hashtable params = new Hashtable();

			String formCharset = "ISO_8859-1";
			String parm = null;
			InputStream inStream = null;
			InputStream inStream2 = null;
			String file_desc = null;
			String fileName = null;
			String docname = null;
			String docdesc = null;
			String keywords = null;
			String notify = null;

			EtsCrAttach attachment = new EtsCrAttach();
			attachment.setEtsId(etsId);
			attachment.setPmoId(crInfoModel.getPmoId());
			attachment.setPmoProjectId(crInfoModel.getPmoProjectId());
			attachment.setParentPmoId(crInfoModel.getParentPmoId());
			attachment.setInfoSrcFlag("T"); //files marked temp
			attachment.setDocType(1); //fixed for all attachements
			attachment.setDocNo(Integer.valueOf(filenumber).intValue());
			attachment.setLastUserId(crInfoModel.getLastUserId());

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

				if (parm.equalsIgnoreCase("upload_file")) {
					// Set our input stream
					inStream = part.getContentInputStream();
					//inStream2 = part.getContentInputStream();
					fileName = part.getDisposition("filename", formCharset);

					if (fileName.length() > 0) {
						int lastBackSlash = fileName.lastIndexOf("\\"); // Windows based
						int lastForwardSlash = fileName.lastIndexOf(Defines.SLASH); // Unix based

						if (lastBackSlash > 0) {
							fileName = fileName.substring(lastBackSlash + 1, fileName.length());
						} else if (lastForwardSlash > 0) {
							fileName = fileName.substring(lastForwardSlash + 1, fileName.length());
						}

						fileName = fileName.replace(' ', '_');
						System.out.println("*filename=" + fileName);

						//set doc Name
						attachment.setDocName(fileName);
						String extension = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
						attachment.setFileMime(ETSMimeDataList.getMimeTypeByExtension(extension));

					} else {
						System.out.println("Please provide proper file name to be uploaded.");
						String[] err = { "0", "Please provide proper file name to be uploaded." };
						return err;
					}
				} else if (parm.equalsIgnoreCase("file_desc")) {
					System.out.println(parm + "=" + value);
					file_desc = value;
					//set doc desc
					attachment.setDocDesc(file_desc);

				} else {

					params.put(parm, value);
				}

			}

			if (!AmtCommonUtils.isResourceDefined(file_desc)) {

				//System.out.println("No file name description was entered for the file to be uploaded.");
				//String[] err = { "0", "Please enter file description for the file to be uploaded." };
				//return err;

			}

			if (inStream == null) {

				System.out.println("writeDocument -- Input stream not found for file.");
				String[] err = { "0", "Input connection for file not found, please try again" };
				return err;

			}

			int inStreamAvail = -1;

			try {

				inStreamAvail = inStream.available();

				if (inStreamAvail > (104857600)) {

					System.out.println("writeDocument -- File over 100 MB limit.");
					String[] err = { "0", "The File is over the 100 MB limit.  Please use the DropBox for this file." };
					return err;
				}

			} catch (IOException ioe) {

				System.out.println("ioe ex for instreamavail(). e=" + ioe);
				String[] err = { "0", "File IO Error occurred, please try again" };
				return err;
			}

			System.out.println("writeDocument -- Input stream reports " + inStreamAvail + " bytes available.");
			attachment.setFileSize(inStreamAvail);

			byte[] temp = new byte[inStreamAvail];
			int amountread = 0;
			int offset = 0;
			while (amountread < inStreamAvail) {
				try {
					int readnow = inStream.read(temp, offset, inStreamAvail - amountread);
					if (readnow > 0) {
						amountread += readnow;
						offset += readnow;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			attachment.setFileData(temp);

			boolean success = false;

			System.out.println("before loading file into DB success = " + String.valueOf(success));

			EtsCrPmoIssueDocDAO crDocDao = new EtsCrPmoIssueDocDAO();

			success = crDocDao.createCRAttachment(attachment);

			System.out.println("after loading file into DB success = " + String.valueOf(success));

			if (!success) {

				String[] err = { "0", "Errror occurred while uploading document. Please try again." };
				return err;

			}

		} catch (Exception e) {
			e.printStackTrace();
			String[] err = { "0", "General Error has occurred, please try again" };
			return err;
		}

		String[] err = { "1", "" };
		return err;

	}
	
	/**
								 * To display attached files for Modify problem
								 * 
								 */

		public ArrayList getCurAttachedFilesListForViewCR(String etsId) throws SQLException, Exception {

			ArrayList curList = new ArrayList();

			Global.println("eTS prob id in getCurAttachedFilesListForViewCR file list===" + etsId);

			EtsCrPmoIssueDocDAO crDocDao = new EtsCrPmoIssueDocDAO();

			int attachCount = crDocDao.getCRAttachmentCountWithFlag(etsId, "E");

			if (!Global.loaded) {

				Global.Init();
			}

			if (attachCount > 0) {

				Vector aList = crDocDao.getAttachedFilesWithSrcFlg(etsId, "E");

				int count = 1;
				String viewIssueUrl = "";
				String viewDocSizeInKb = "";

				if (aList != null && !aList.isEmpty()) {

					for (Iterator k = aList.iterator(); k.hasNext();) {

						EtsCrAttach attached = (EtsCrAttach) k.next();

						String fileName = attached.getDocName();
						long fileSize = attached.getFileSize();
						String fileDesc = attached.getDocDesc();
						int fileNo = attached.getDocNo();
						String fileDate = attached.getTimeStampString();

						viewIssueUrl = Global.getUrl("ets") + "/" + "ETSIssueAttachServlet.wss?action=viewcr&fileNo=" + fileNo + "&edge_problem_id=" + etsId + " ";

						viewDocSizeInKb = CommonFileUtils.printFileSize(fileSize + "");

						//get new stuff//
						EtsCrAttach crNewAttach = new EtsCrAttach();

						crNewAttach.setDocName(fileName);
						crNewAttach.setDocDesc(fileDesc);
						crNewAttach.setDocSizeInKb(viewDocSizeInKb);
						crNewAttach.setViewCrUrl(viewIssueUrl);

						curList.add(crNewAttach);

					} //end of for loop

				} //end of aList != empty

			} //if attachcount > 0

			if (curList != null && !curList.isEmpty()) {

				int csize = curList.size();

				for (int j = 0; j < csize; j++) {

					EtsCrAttach crNewAttach = (EtsCrAttach) curList.get(j);

					if (crNewAttach != null) {

						Global.println("CUR doc name===" + crNewAttach.getDocName());
						Global.println("CUR doc desc===" + crNewAttach.getDocDesc());
						Global.println("CUR doc size in KB===" + crNewAttach.getDocSizeInKb());
						Global.println("CUR doc URL ===" + crNewAttach.getViewCrUrl());

					}

				} //end of csize > 0

			} //end of cur list not null

			return curList;

		}
		
	/**
					 * To display attached files for Modify problem
					 * 
					 */

	public String printPrevAttachedFilesListForCR(String etsId) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();

		Global.println("edge prob id in printPrevAttachedFilesListForCR file list===" + etsId);

		EtsCrPmoIssueDocDAO crDocDao = new EtsCrPmoIssueDocDAO();

		int attachCount = crDocDao.getCRAttachmentCountWithFlag(etsId, "O");

		if (attachCount > 0) {

			sb.append("	<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\"> \n");
			sb.append("	     <tr >\n");
			sb.append("	       <td  height=\"18\" width=\"600\">\n");
			sb.append("	         <b>&nbsp;Previously attached files</b>\n");
			sb.append("	       </td>\n");
			sb.append("	     </tr>\n");
			sb.append("	</table>\n");
			sb.append("	<br />\n");

			sb.append("	<table summary=\"Table to display file attachments\" border=\"0\" cellpadding=\"1\" cellspacing=\"0\" width=\"600\"> \n");
			sb.append("	      <tr> \n");
			sb.append("	        <td bgcolor=\"#666666\" colspan=\"3\"> \n");
			sb.append("	          <table border=\"0\" cellpadding=\"1\" cellspacing=\"1\" width=\"100%\"> \n");
			sb.append("	            <tr height=\"18\"> \n");
			sb.append("	              <th id=\"atcol2\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
			sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;File name</span></b> \n");
			sb.append("	              </th> \n");
			sb.append("	              <th id=\"atcol3\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
			sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;Size</span></b> \n");
			sb.append("	              </th> \n");
			sb.append("	              <th id=\"atcol4\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
			sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;Description</span></b> \n");
			sb.append("	              </th> \n");
			sb.append("	            </tr> \n");

			Vector aList = crDocDao.getAttachedFilesWithSrcFlg(etsId, "O");

			int count = 1;

			for (Iterator k = aList.iterator(); k.hasNext();) {
				EtsCrAttach attached = (EtsCrAttach) k.next();

				String fileName = attached.getDocName();
				long fileSize = attached.getFileSize();
				String fileDesc = attached.getDocDesc();
				int fileNo = attached.getDocNo();
				String fileDate = attached.getTimeStampString();

				String viewIssueUrl = "ETSIssueAttachServlet.wss?action=viewcr&fileNo=" + fileNo + "&edge_problem_id=" + etsId + " ";

				sb.append("	            <tr> \n");
				sb.append("	              <td headers=\"atcol2\" bgcolor=\"#ffffff\" align=\"left\">&nbsp;\n");
				sb.append(
					"	      <a href=\""
						+ viewIssueUrl
						+ "\" target=\"new\" onclick=\"window.open('"
						+ viewIssueUrl
						+ "','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=500,height=500,left=387,top=207'); return false;\" onkeypress =\"window.open('"
						+ viewIssueUrl
						+ "','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=500,height=500,left=387,top=207'); return false;\">");

				sb.append("" + fileName + " \n");

				sb.append(" </a> \n");

				sb.append("	              </td> \n");
				sb.append("	              <td headers=\"atcol3\" bgcolor=\"#ffffff\" align=\"left\"> \n");

				/* Display file size in proper size format */
				StringBuffer sbDisplay = new StringBuffer();

				sbDisplay.append(CommonFileUtils.printFileSize(fileSize + ""));

				sb.append("&nbsp;" + sbDisplay.toString() + " \n");
				sb.append("	              </td> \n");
				sb.append("	              <td headers=\"atcol4\" bgcolor=\"#ffffff\" align=\"left\"> \n");
				sb.append("&nbsp;" + fileDesc + " \n");
				sb.append("	              </td> \n");

				//show delete only for new files not for old ones

				sb.append("	  </tr> \n");

				count++;
			}
			sb.append("	            </table> \n");

			sb.append("		   </td> \n");
			sb.append("		</tr> \n");
			sb.append("	 </table> \n");

		} //if attachcount > 0

		return sb.toString();

	}
	
	/**
					 * 
					 * 
					 * @param filecount
					 * @return
					 */

		public String getIndepFileAttachMsgForCR(EtsIssObjectKey etsIssObjKey, EtsCrProbInfoModel crInfoModel, String actionType) {

			StringBuffer sbfile = new StringBuffer();

			int attachCount = 0;
			String etsId = crInfoModel.getEtsId();
			int cancelstate = crInfoModel.getCancelActionState();
			String issueClass = etsIssObjKey.getIssueClass();

			String dynProbType = "change request";

			EtsCrPmoIssueDocDAO crIssueDao = new EtsCrPmoIssueDocDAO();

			try {

				attachCount = crIssueDao.getAttachmentCountForView(etsId);

			} catch (Exception e) {

				SysLog.log(SysLog.ERR, this, e);
				e.printStackTrace();
			}

			sbfile.append(" <table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n");
			sbfile.append("<tr><td > \n");
			sbfile.append(CommonFileUtils.getFileAttachMsg(attachCount, dynProbType, actionType));
			sbfile.append("</td>\n");
			sbfile.append("</tr>\n");
			sbfile.append("</table>\n");

			return sbfile.toString();
		}
		
	/**
		  * 
		  * 
		  * @param filecount
		  * @return
		  */

		public String getIndepViewFileAttachMsgForCR(EtsIssObjectKey etsIssObjKey, EtsCrProbInfoModel crInfoModel, String actionType) {

			StringBuffer sbfile = new StringBuffer();

			int attachCount = 0;
			String etsId = crInfoModel.getEtsId();

			String dynProbType = "change request";

			EtsCrPmoIssueDocDAO crIssueDao = new EtsCrPmoIssueDocDAO();

			try {

				attachCount = crIssueDao.getAttachmentCountForView(etsId);

			} catch (Exception e) {

				SysLog.log(SysLog.ERR, this, e);
				e.printStackTrace();
			}

			sbfile.append(" <table summary=\"\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"600\">\n");
			sbfile.append("<tr><td  valign=\"top\" width =\"100%\" align=\"left\"> \n");
			sbfile.append(CommonFileUtils.getViewFileAttachMsg(attachCount, dynProbType));
			sbfile.append("</td>\n");
			sbfile.append("</tr>\n");
			sbfile.append("</table>\n");

			return sbfile.toString();
		}
		
	/**
									 * To display attached files for Modify problem
									 * 
									 */

		public ArrayList getPrevAttachedFilesListForViewCR(String etsId) throws SQLException, Exception {

			ArrayList curList = new ArrayList();

			Global.println("eTS prob id in getPrevAttachedFilesListForViewCR file list===" + etsId);

			EtsCrPmoIssueDocDAO crDocDao = new EtsCrPmoIssueDocDAO();

			int attachCount = crDocDao.getCRAttachmentCountWithFlag(etsId, "O");

			if (!Global.loaded) {

				Global.Init();
			}

			if (attachCount > 0) {

				Vector aList = crDocDao.getAttachedFilesWithSrcFlg(etsId, "O");

				int count = 1;
				String viewIssueUrl = "";
				String viewDocSizeInKb = "";

				if (aList != null && !aList.isEmpty()) {

					for (Iterator k = aList.iterator(); k.hasNext();) {

						EtsCrAttach attached = (EtsCrAttach) k.next();

						String fileName = attached.getDocName();
						long fileSize = attached.getFileSize();
						String fileDesc = attached.getDocDesc();
						int fileNo = attached.getDocNo();
						String fileDate = attached.getTimeStampString();

						viewIssueUrl = Global.getUrl("ets") + "/" + "ETSIssueAttachServlet.wss?action=viewcr&fileNo=" + fileNo + "&edge_problem_id=" + etsId + " ";

						viewDocSizeInKb = CommonFileUtils.printFileSize(fileSize + "");

						//get new stuff//
						EtsCrAttach crNewAttach = new EtsCrAttach();

						crNewAttach.setDocName(fileName);
						crNewAttach.setDocDesc(fileDesc);
						crNewAttach.setDocSizeInKb(viewDocSizeInKb);
						crNewAttach.setViewCrUrl(viewIssueUrl);

						curList.add(crNewAttach);

					} //end of for loop

				} //end of aList != empty

			} //if attachcount > 0

			if (curList != null && !curList.isEmpty()) {

				int csize = curList.size();

				for (int j = 0; j < csize; j++) {

					EtsCrAttach crNewAttach = (EtsCrAttach) curList.get(j);

					if (crNewAttach != null) {

						Global.println("PREV doc name===" + crNewAttach.getDocName());
						Global.println("PREV doc desc===" + crNewAttach.getDocDesc());
						Global.println("PREV doc size in KB===" + crNewAttach.getDocSizeInKb());
						Global.println("PREV doc URL ===" + crNewAttach.getViewCrUrl());

					}

				} //end of csize > 0

			} //end of cur list not null

			return curList;

		}
		
	/**
					 * To display attached files for Modify problem
					 * 
					 */

		public String printAttachedFilesListForCRUpdate(String etsId) throws SQLException, Exception {

			StringBuffer sb = new StringBuffer();

			Global.println("edge prob id in printAttachedFilesListForCRUpdate file list===" + etsId);

			EtsCrPmoIssueDocDAO crDocDao = new EtsCrPmoIssueDocDAO();

			int attachCount = 0;

			attachCount = crDocDao.getAttachmentCountForView(etsId);

			if (attachCount > 0) {

				sb.append("	<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\"> \n");
				sb.append("	     <tr >\n");
				sb.append("	       <td  height=\"18\" width=\"600\">\n");
				sb.append("	         <b>&nbsp;Attached file list</b>\n");
				sb.append("	       </td>\n");
				sb.append("	     </tr>\n");
				sb.append("	</table>\n");

				sb.append("    <table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\"> \n");
				sb.append("      <tr> \n");
				sb.append("       <td  height=\"18\" width=\"600\"> \n");
				sb.append("        [<span class=\"small\">Please note that you can view the file attachments using browser, only if your browser supports them. If you have any difficulty in viewing attachments using browser, please download it and view in compatible application. \n");
				//sb.append("        Please note that <span class=\"ast\">*</span> indicates the currently attached files.]</span>\n");
				sb.append("        </td> \n");
				sb.append("      </tr> \n");
				sb.append("    </table> \n");
				sb.append("	<br />\n");

				//prev attached files
				sb.append(printPrevAttachedFilesListForCR(etsId));

				sb.append("	<br />\n");
				sb.append(printCurAttachedFilesListForCR(etsId));

			} //if attachcount > 0

			return sb.toString();

		}
		
	/**
						 * To display attached files for Modify problem
						 * 
						 */

		public String printCurAttachedFilesListForCR(String etsId) throws SQLException, Exception {

			StringBuffer sb = new StringBuffer();

			Global.println("edge prob id in printPrevAttachedFilesListForCR file list===" + etsId);

			EtsCrPmoIssueDocDAO crDocDao = new EtsCrPmoIssueDocDAO();

			int attachCount = crDocDao.getCRAttachmentCountWithFlag(etsId, "T");

			if (attachCount > 0) {

				sb.append("	<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\"> \n");
				sb.append("	     <tr >\n");
				sb.append("	       <td  height=\"18\" width=\"600\">\n");
				sb.append("	         <b>&nbsp;Currently attached files</b>\n");
				sb.append("	       </td>\n");
				sb.append("	     </tr>\n");
				sb.append("	</table>\n");
				sb.append("	<br />\n");

				sb.append("	<table summary=\"Table to display file attachments\" border=\"0\" cellpadding=\"1\" cellspacing=\"0\" width=\"600\"> \n");
				sb.append("	      <tr> \n");
				sb.append("	        <td bgcolor=\"#666666\" colspan=\"3\"> \n");
				sb.append("	          <table border=\"0\" cellpadding=\"1\" cellspacing=\"1\" width=\"100%\"> \n");
				sb.append("	            <tr height=\"18\"> \n");
				sb.append("	              <th id=\"atcol2\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
				sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;File name</span></b> \n");
				sb.append("	              </th> \n");
				sb.append("	              <th id=\"atcol3\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
				sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;Size</span></b> \n");
				sb.append("	              </th> \n");
				sb.append("	              <th id=\"atcol4\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
				sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;Description</span></b> \n");
				sb.append("	              </th> \n");
				sb.append("	              <th id=\"atcol6\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
				sb.append("	              	    &nbsp;\n");
				sb.append("	              </th> \n");
				sb.append("	            </tr> \n");

				Vector aList = crDocDao.getAttachedFilesWithSrcFlg(etsId, "T");

				int count = 1;

				for (Iterator k = aList.iterator(); k.hasNext();) {
					EtsCrAttach attached = (EtsCrAttach) k.next();

					String fileName = attached.getDocName();
					long fileSize = attached.getFileSize();
					String fileDesc = attached.getDocDesc();
					int fileNo = attached.getDocNo();
					String fileDate = attached.getTimeStampString();

					String viewIssueUrl = "ETSIssueAttachServlet.wss?action=viewcr&fileNo=" + fileNo + "&edge_problem_id=" + etsId + " ";

					sb.append("	            <tr> \n");
					sb.append("	              <td headers=\"atcol2\" bgcolor=\"#ffffff\" align=\"left\">&nbsp;\n");
					sb.append(
						"	      <a href=\""
							+ viewIssueUrl
							+ "\" target=\"new\" onclick=\"window.open('"
							+ viewIssueUrl
							+ "','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=500,height=500,left=387,top=207'); return false;\" onkeypress =\"window.open('"
							+ viewIssueUrl
							+ "','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=500,height=500,left=387,top=207'); return false;\">");
					sb.append("" + fileName + " \n");
					sb.append(" </a> \n");
					sb.append("	              </td> \n");
					sb.append("	              <td headers=\"atcol3\" bgcolor=\"#ffffff\" align=\"left\"> \n");

					/* Display file size in proper size format */
					StringBuffer sbDisplay = new StringBuffer();

					sbDisplay.append(CommonFileUtils.printFileSize(fileSize + ""));

					sb.append("&nbsp;" + sbDisplay.toString() + " \n");
					sb.append("	              </td> \n");
					sb.append("	              <td headers=\"atcol4\" bgcolor=\"#ffffff\" align=\"left\"> \n");
					sb.append("&nbsp;" + fileDesc + " \n");
					sb.append("	              </td> \n");
					sb.append("	              <td headers=\"atcol6\" bgcolor=\"#ffffff\" align=\"left\"> \n");

					sb.append("	<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"15%\"> \n");
					sb.append("	  <tr> \n");
					sb.append("	    <td > \n");
					sb.append("	       &nbsp;<input type=\"image\"  src=\"" + Defines.BUTTON_ROOT  + "cancel_rd.gif\" name=\"delete" + String.valueOf(fileNo).trim() + "\" value=\"attachDelete\" border=\"0\" width=\"21\" height=\"21\" alt=\"Delete\" />&nbsp;&nbsp;");
					sb.append("	    </td> \n");
					sb.append("	    <td > \n");
					sb.append("Delete");
					sb.append("	    </td> \n");
					sb.append("	  </tr> \n");
					sb.append("	</table> \n");

					sb.append("	    </td> \n");
					sb.append("	  </tr> \n");

					count++;
				}
				sb.append("	            </table> \n");

				sb.append("		   </td> \n");
				sb.append("		</tr> \n");
				sb.append("	 </table> \n");

			} //if attachcount > 0

			return sb.toString();

		}
		
	/**
							 * To display attached files for Modify problem
							 * 
							 */

		public String printAttachedFilesListForPMOIssuesResolveScr3(String etsId) throws SQLException, Exception {

			StringBuffer sb = new StringBuffer();

			Global.println("edge prob id in printAttachedFilesListForCRUpdate file list===" + etsId);

			EtsCrPmoIssueDocDAO crDocDao = new EtsCrPmoIssueDocDAO();

			int attachCount = 0;

			attachCount = crDocDao.getAttachmentCountForView(etsId);

			if (attachCount > 0) {

				sb.append("	<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\"> \n");
				sb.append("	     <tr >\n");
				sb.append("	       <td  height=\"18\" width=\"600\">\n");
				sb.append("	         <b>&nbsp;Attached file list</b>\n");
				sb.append("	       </td>\n");
				sb.append("	     </tr>\n");
				sb.append("	</table>\n");

				sb.append("    <table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\"> \n");
				sb.append("      <tr> \n");
				sb.append("       <td  height=\"18\" width=\"600\"> \n");
				sb.append("        [<span class=\"small\">Please note that you can view the file attachments using browser, only if your browser supports them. If you have any difficulty in viewing attachments using browser, please download it and view in compatible application. \n");
				//sb.append("        Please note that <span class=\"ast\">*</span> indicates the currently attached files.]</span>\n");
				sb.append("        </td> \n");
				sb.append("      </tr> \n");
				sb.append("    </table> \n");
				sb.append("	<br />\n");

				//prev attached files
				sb.append(printPrevAttachedFilesListForCR(etsId));

				sb.append("	<br />\n");
				sb.append(printCurAttachedFilesListForTYStates(etsId));

			} //if attachcount > 0

			return sb.toString();

		}
		
	/**
							 * To display attached files for Modify problem
							 * 
							 */

		public String printCurAttachedFilesListForTYStates(String etsId) throws SQLException, Exception {

			StringBuffer sb = new StringBuffer();

			Global.println("edge prob id in printPrevAttachedFilesListForCR file list===" + etsId);

			EtsCrPmoIssueDocDAO crDocDao = new EtsCrPmoIssueDocDAO();

			int attachTCount = crDocDao.getCRAttachmentCountWithFlag(etsId, "T");

			int attachYCount = crDocDao.getCRAttachmentCountWithFlag(etsId, "Y");

			if (attachTCount > 0 || attachYCount > 0) {

				sb.append("	<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\"> \n");
				sb.append("	     <tr >\n");
				sb.append("	       <td  height=\"18\" width=\"600\">\n");
				sb.append("	         <b>&nbsp;Currently attached files</b>\n");
				sb.append("	       </td>\n");
				sb.append("	     </tr>\n");
				sb.append("	</table>\n");
				sb.append("	<br />\n");

				sb.append("	<table summary=\"Table to display file attachments\" border=\"0\" cellpadding=\"1\" cellspacing=\"0\" width=\"600\"> \n");
				sb.append("	      <tr> \n");
				sb.append("	        <td bgcolor=\"#666666\" colspan=\"3\"> \n");
				sb.append("	          <table border=\"0\" cellpadding=\"1\" cellspacing=\"1\" width=\"100%\"> \n");
				sb.append("	            <tr height=\"18\"> \n");
				sb.append("	              <th id=\"atcol2\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
				sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;File name</span></b> \n");
				sb.append("	              </th> \n");
				sb.append("	              <th id=\"atcol3\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
				sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;Size</span></b> \n");
				sb.append("	              </th> \n");
				sb.append("	              <th id=\"atcol4\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
				sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;Description</span></b> \n");
				sb.append("	              </th> \n");
				sb.append("	              <th id=\"atcol6\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
				sb.append("	              	    &nbsp;\n");
				sb.append("	              </th> \n");
				sb.append("	            </tr> \n");

				Vector aTList = crDocDao.getAttachedFilesWithSrcFlg(etsId, "T");

				Vector aYList = crDocDao.getAttachedFilesWithSrcFlg(etsId, "Y");

				int tcount = 1;

				int ycount = 1;

				////show prev ones first with Y flag, then currently with T flag

				for (Iterator m = aYList.iterator(); m.hasNext();) {
					EtsCrAttach attached = (EtsCrAttach) m.next();

					String fileName = attached.getDocName();
					long fileSize = attached.getFileSize();
					String fileDesc = attached.getDocDesc();
					int fileNo = attached.getDocNo();
					String fileDate = attached.getTimeStampString();

					String viewIssueUrl = "ETSIssueAttachServlet.wss?action=viewcr&fileNo=" + fileNo + "&edge_problem_id=" + etsId + " ";

					sb.append("	            <tr> \n");
					sb.append("	              <td headers=\"atcol2\" bgcolor=\"#ffffff\" align=\"left\">&nbsp;\n");
					sb.append(
						"	      <a href=\""
							+ viewIssueUrl
							+ "\" target=\"new\" onclick=\"window.open('"
							+ viewIssueUrl
							+ "','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=500,height=500,left=387,top=207'); return false;\" onkeypress =\"window.open('"
							+ viewIssueUrl
							+ "','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=500,height=500,left=387,top=207'); return false;\">");
					sb.append("" + fileName + " \n");
					sb.append(" </a> \n");
					sb.append("	              </td> \n");
					sb.append("	              <td headers=\"atcol3\" bgcolor=\"#ffffff\" align=\"left\"> \n");

					/* Display file size in proper size format */
					StringBuffer sbDisplay = new StringBuffer();

					sbDisplay.append(CommonFileUtils.printFileSize(fileSize + ""));

					sb.append("&nbsp;" + sbDisplay.toString() + " \n");
					sb.append("	              </td> \n");
					sb.append("	              <td headers=\"atcol4\" bgcolor=\"#ffffff\" align=\"left\"> \n");
					sb.append("&nbsp;" + fileDesc + " \n");
					sb.append("	              </td> \n");
					sb.append("	              <td headers=\"atcol6\" bgcolor=\"#ffffff\" align=\"left\"> \n");

					sb.append("	<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"15%\"> \n");
					sb.append("	  <tr> \n");
					sb.append("	    <td > \n");
					sb.append("	       &nbsp;<input type=\"image\"  src=\"" + Defines.BUTTON_ROOT  + "cancel_rd.gif\" name=\"delete" + String.valueOf(fileNo).trim() + "\" value=\"attachDelete\" border=\"0\" width=\"21\" height=\"21\" alt=\"Delete\" />&nbsp;&nbsp;");
					sb.append("	    </td> \n");
					sb.append("	    <td > \n");
					sb.append("Delete");
					sb.append("	    </td> \n");
					sb.append("	  </tr> \n");
					sb.append("	</table> \n");

					sb.append("	    </td> \n");
					sb.append("	  </tr> \n");

					ycount++;
				}

				//////////////////now with T flag

				for (Iterator k = aTList.iterator(); k.hasNext();) {
					EtsCrAttach attached = (EtsCrAttach) k.next();

					String fileName = attached.getDocName();
					long fileSize = attached.getFileSize();
					String fileDesc = attached.getDocDesc();
					int fileNo = attached.getDocNo();
					String fileDate = attached.getTimeStampString();

					String viewIssueUrl = "ETSIssueAttachServlet.wss?action=viewcr&fileNo=" + fileNo + "&edge_problem_id=" + etsId + " ";

					sb.append("	            <tr> \n");
					sb.append("	              <td headers=\"atcol2\" bgcolor=\"#ffffff\" align=\"left\">&nbsp;\n");
					sb.append(
						"	      <a href=\""
							+ viewIssueUrl
							+ "\" target=\"new\" onclick=\"window.open('"
							+ viewIssueUrl
							+ "','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=500,height=500,left=387,top=207'); return false;\" onkeypress =\"window.open('"
							+ viewIssueUrl
							+ "','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=500,height=500,left=387,top=207'); return false;\">");
					sb.append("" + fileName + " \n");
					sb.append(" </a> \n");
					sb.append("	              </td> \n");
					sb.append("	              <td headers=\"atcol3\" bgcolor=\"#ffffff\" align=\"left\"> \n");

					/* Display file size in proper size format */
					StringBuffer sbDisplay = new StringBuffer();

					sbDisplay.append(CommonFileUtils.printFileSize(fileSize + ""));

					sb.append("&nbsp;" + sbDisplay.toString() + " \n");
					sb.append("	              </td> \n");
					sb.append("	              <td headers=\"atcol4\" bgcolor=\"#ffffff\" align=\"left\"> \n");
					sb.append("&nbsp;" + fileDesc + " \n");
					sb.append("	              </td> \n");
					sb.append("	              <td headers=\"atcol6\" bgcolor=\"#ffffff\" align=\"left\"> \n");

					sb.append("	<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"15%\"> \n");
					sb.append("	  <tr> \n");
					sb.append("	    <td > \n");
					sb.append("	       &nbsp;<input type=\"image\"  src=\"" + Defines.BUTTON_ROOT  + "cancel_rd.gif\" name=\"delete" + String.valueOf(fileNo).trim() + "\" value=\"attachDelete\" border=\"0\" width=\"21\" height=\"21\" alt=\"Delete\" />&nbsp;&nbsp;");
					sb.append("	    </td> \n");
					sb.append("	    <td > \n");
					sb.append("Delete");
					sb.append("	    </td> \n");
					sb.append("	  </tr> \n");
					sb.append("	</table> \n");

					sb.append("	    </td> \n");
					sb.append("	  </tr> \n");

					tcount++;
				}
				sb.append("	            </table> \n");

				sb.append("		   </td> \n");
				sb.append("		</tr> \n");
				sb.append("	 </table> \n");

			} //if attachcount > 0

			return sb.toString();

		}
		
	/**
						 * To display attached files for Modify problem
						 * 
						 */

		public String printAttachedFilesListForPMOIssuesResolveScr6(String etsId) throws SQLException, Exception {

			StringBuffer sb = new StringBuffer();

			Global.println("edge prob id in printAttachedFilesListForCRUpdate file list===" + etsId);

			EtsCrPmoIssueDocDAO crDocDao = new EtsCrPmoIssueDocDAO();

			int attachCount = 0;

			attachCount = crDocDao.getAttachmentCountForView(etsId);

			if (attachCount > 0) {

				sb.append("	<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\"> \n");
				sb.append("	     <tr >\n");
				sb.append("	       <td  height=\"18\" width=\"600\">\n");
				sb.append("	         <b>&nbsp;Attached file list</b>\n");
				sb.append("	       </td>\n");
				sb.append("	     </tr>\n");
				sb.append("	</table>\n");

				sb.append("    <table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\"> \n");
				sb.append("      <tr> \n");
				sb.append("       <td  height=\"18\" width=\"600\"> \n");
				sb.append("        [<span class=\"small\">Please note that you can view the file attachments using browser, only if your browser supports them. If you have any difficulty in viewing attachments using browser, please download it and view in compatible application. \n");
				//sb.append("        Please note that <span class=\"ast\">*</span> indicates the currently attached files.]</span>\n");
				sb.append("        </td> \n");
				sb.append("      </tr> \n");
				sb.append("    </table> \n");
				sb.append("	<br />\n");

				//prev attached files
				sb.append(printPrevAttachedFilesListForCR(etsId));

				sb.append("	<br />\n");
				sb.append(printCurAttachedFilesListForPMOIssuesScr6(etsId));

			} //if attachcount > 0

			return sb.toString();

		}
		
	/**
							 * To display attached files for Modify problem
							 * 
							 */

		public String printCurAttachedFilesListForPMOIssuesScr6(String etsId) throws SQLException, Exception {

			StringBuffer sb = new StringBuffer();

			Global.println("edge prob id in printPrevAttachedFilesListForCR file list===" + etsId);

			EtsCrPmoIssueDocDAO crDocDao = new EtsCrPmoIssueDocDAO();

			int attachCount = crDocDao.getCRAttachmentCountWithFlag(etsId, "Y");

			if (attachCount > 0) {

				sb.append("	<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\"> \n");
				sb.append("	     <tr >\n");
				sb.append("	       <td  height=\"18\" width=\"600\">\n");
				sb.append("	         <b>&nbsp;Currently attached files</b>\n");
				sb.append("	       </td>\n");
				sb.append("	     </tr>\n");
				sb.append("	</table>\n");
				sb.append("	<br />\n");

				sb.append("	<table summary=\"Table to display file attachments\" border=\"0\" cellpadding=\"1\" cellspacing=\"0\" width=\"600\"> \n");
				sb.append("	      <tr> \n");
				sb.append("	        <td bgcolor=\"#666666\" colspan=\"3\"> \n");
				sb.append("	          <table border=\"0\" cellpadding=\"1\" cellspacing=\"1\" width=\"100%\"> \n");
				sb.append("	            <tr height=\"18\"> \n");
				sb.append("	              <th id=\"atcol2\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
				sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;File name</span></b> \n");
				sb.append("	              </th> \n");
				sb.append("	              <th id=\"atcol3\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
				sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;Size</span></b> \n");
				sb.append("	              </th> \n");
				sb.append("	              <th id=\"atcol4\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
				sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;Description</span></b> \n");
				sb.append("	              </th> \n");
				sb.append("	              <th id=\"atcol6\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
				sb.append("	              	    &nbsp;\n");
				sb.append("	              </th> \n");
				sb.append("	            </tr> \n");

				Vector aList = crDocDao.getAttachedFilesWithSrcFlg(etsId, "Y");

				int count = 1;

				for (Iterator k = aList.iterator(); k.hasNext();) {
					EtsCrAttach attached = (EtsCrAttach) k.next();

					String fileName = attached.getDocName();
					long fileSize = attached.getFileSize();
					String fileDesc = attached.getDocDesc();
					int fileNo = attached.getDocNo();
					String fileDate = attached.getTimeStampString();

					String viewIssueUrl = "ETSIssueAttachServlet.wss?action=viewcr&fileNo=" + fileNo + "&edge_problem_id=" + etsId + " ";

					sb.append("	            <tr> \n");
					sb.append("	              <td headers=\"atcol2\" bgcolor=\"#ffffff\" align=\"left\">&nbsp;\n");
					sb.append(
						"	      <a href=\""
							+ viewIssueUrl
							+ "\" target=\"new\" onclick=\"window.open('"
							+ viewIssueUrl
							+ "','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=500,height=500,left=387,top=207'); return false;\" onkeypress =\"window.open('"
							+ viewIssueUrl
							+ "','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=500,height=500,left=387,top=207'); return false;\">");
					sb.append("" + fileName + " \n");
					sb.append(" </a> \n");
					sb.append("	              </td> \n");
					sb.append("	              <td headers=\"atcol3\" bgcolor=\"#ffffff\" align=\"left\"> \n");

					/* Display file size in proper size format */
					StringBuffer sbDisplay = new StringBuffer();

					sbDisplay.append(CommonFileUtils.printFileSize(fileSize + ""));

					sb.append("&nbsp;" + sbDisplay.toString() + " \n");
					sb.append("	              </td> \n");
					sb.append("	              <td headers=\"atcol4\" bgcolor=\"#ffffff\" align=\"left\"> \n");
					sb.append("&nbsp;" + fileDesc + " \n");
					sb.append("	              </td> \n");
					sb.append("	              <td headers=\"atcol6\" bgcolor=\"#ffffff\" align=\"left\"> \n");

					sb.append("	<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"15%\"> \n");
					sb.append("	  <tr> \n");
					sb.append("	    <td > \n");
					sb.append("	       &nbsp;<input type=\"image\"  src=\"" + Defines.BUTTON_ROOT  + "cancel_rd.gif\" name=\"delete" + String.valueOf(fileNo).trim() + "\" value=\"attachDelete\" border=\"0\" width=\"21\" height=\"21\" alt=\"Delete\" />&nbsp;&nbsp;");
					sb.append("	    </td> \n");
					sb.append("	    <td > \n");
					sb.append("Delete");
					sb.append("	    </td> \n");
					sb.append("	  </tr> \n");
					sb.append("	</table> \n");

					sb.append("	    </td> \n");
					sb.append("	  </tr> \n");

					count++;
				}
				sb.append("	            </table> \n");

				sb.append("		   </td> \n");
				sb.append("		</tr> \n");
				sb.append("	 </table> \n");

			} //if attachcount > 0

			return sb.toString();

		}
		
	/**
						 * To display attached files for Modify problem
						 * 
						 */

		public String printAttachedFilesListForViewCR(String etsId) throws SQLException, Exception {

			StringBuffer sb = new StringBuffer();

			Global.println("edge prob id in printAttachedFilesListForCRUpdate file list===" + etsId);

			EtsCrPmoIssueDocDAO crDocDao = new EtsCrPmoIssueDocDAO();

			int attachCount = 0;

			attachCount = crDocDao.getAttachmentCountForView(etsId);

			if (attachCount > 0) {

				sb.append("	<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\"> \n");
				sb.append("	     <tr >\n");
				sb.append("	       <td  height=\"18\" width=\"600\">\n");
				sb.append("	         <b>&nbsp;Attached file list</b>\n");
				sb.append("	       </td>\n");
				sb.append("	     </tr>\n");
				sb.append("	</table>\n");

				sb.append("    <table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\"> \n");
				sb.append("      <tr> \n");
				sb.append("       <td  height=\"18\" width=\"600\"> \n");
				sb.append("        [<span class=\"small\">Please note that you can view the file attachments using browser, only if your browser supports them. If you have any difficulty in viewing attachments using browser, please download it and view in compatible application. \n");
				//sb.append("        Please note that <span class=\"ast\">*</span> indicates the currently attached files.]</span>\n");
				sb.append("        </td> \n");
				sb.append("      </tr> \n");
				sb.append("    </table> \n");
				sb.append("	<br />\n");

				//prev attached files
				sb.append(printPrevAttachedFilesListForCR(etsId));

				ArrayList pList = getPrevAttachedFilesListForViewCR(etsId);

				sb.append("	<br />\n");
				sb.append(printCurAttachedFilesListForViewCR(etsId));

				ArrayList cList = getCurAttachedFilesListForViewCR(etsId);

			} //if attachcount > 0

			return sb.toString();

		}
		
	/**
							 * To display attached files for Modify problem
							 * 
							 */

		public String printCurAttachedFilesListForViewCR(String etsId) throws SQLException, Exception {

			StringBuffer sb = new StringBuffer();

			Global.println("edge prob id in printCurAttachedFilesListForViewCR file list===" + etsId);

			EtsCrPmoIssueDocDAO crDocDao = new EtsCrPmoIssueDocDAO();

			int attachCount = crDocDao.getCRAttachmentCountWithFlag(etsId, "E");

			if (attachCount > 0) {

				sb.append("	<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\"> \n");
				sb.append("	     <tr >\n");
				sb.append("	       <td  height=\"18\" width=\"600\">\n");
				sb.append("	         <b>&nbsp;Currently attached files</b>\n");
				sb.append("	       </td>\n");
				sb.append("	     </tr>\n");
				sb.append("	</table>\n");
				sb.append("	<br />\n");

				sb.append("	<table summary=\"Table to display file attachments\" border=\"0\" cellpadding=\"1\" cellspacing=\"0\" width=\"600\"> \n");
				sb.append("	      <tr> \n");
				sb.append("	        <td bgcolor=\"#666666\" colspan=\"3\"> \n");
				sb.append("	          <table border=\"0\" cellpadding=\"1\" cellspacing=\"1\" width=\"100%\"> \n");
				sb.append("	            <tr height=\"18\"> \n");
				sb.append("	              <th id=\"atcol2\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
				sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;File name</span></b> \n");
				sb.append("	              </th> \n");
				sb.append("	              <th id=\"atcol3\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
				sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;Size</span></b> \n");
				sb.append("	              </th> \n");
				sb.append("	              <th id=\"atcol4\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
				sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;Description</span></b> \n");
				sb.append("	              </th> \n");
				sb.append("	            </tr> \n");

				Vector aList = crDocDao.getAttachedFilesWithSrcFlg(etsId, "E");

				int count = 1;

				for (Iterator k = aList.iterator(); k.hasNext();) {
					EtsCrAttach attached = (EtsCrAttach) k.next();

					String fileName = attached.getDocName();
					long fileSize = attached.getFileSize();
					String fileDesc = attached.getDocDesc();
					int fileNo = attached.getDocNo();
					String fileDate = attached.getTimeStampString();

					String viewIssueUrl = "ETSIssueAttachServlet.wss?action=viewcr&fileNo=" + fileNo + "&edge_problem_id=" + etsId + " ";

					sb.append("	            <tr> \n");
					sb.append("	              <td headers=\"atcol2\" bgcolor=\"#ffffff\" align=\"left\">&nbsp;\n");
					sb.append(
						"	      <a href=\""
							+ viewIssueUrl
							+ "\" target=\"new\" onclick=\"window.open('"
							+ viewIssueUrl
							+ "','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=500,height=500,left=387,top=207'); return false;\" onkeypress =\"window.open('"
							+ viewIssueUrl
							+ "','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=500,height=500,left=387,top=207'); return false;\">");

					sb.append("" + fileName + " \n");

					sb.append(" </a> \n");

					sb.append("	              </td> \n");
					sb.append("	              <td headers=\"atcol3\" bgcolor=\"#ffffff\" align=\"left\"> \n");

					/* Display file size in proper size format */
					StringBuffer sbDisplay = new StringBuffer();

					sbDisplay.append(CommonFileUtils.printFileSize(fileSize + ""));

					sb.append("&nbsp;" + sbDisplay.toString() + " \n");
					sb.append("	              </td> \n");
					sb.append("	              <td headers=\"atcol4\" bgcolor=\"#ffffff\" align=\"left\"> \n");
					sb.append("&nbsp;" + fileDesc + " \n");
					sb.append("	              </td> \n");

					//show delete only for new files not for old ones

					sb.append("	  </tr> \n");

					count++;
				}
				sb.append("	            </table> \n");

				sb.append("		   </td> \n");
				sb.append("		</tr> \n");
				sb.append("	 </table> \n");

			} //if attachcount > 0

			return sb.toString();

		}


	/**
				 * To display attached files
				 * 
				 */

		public String printCRAttachedFilesList(String etsId) throws SQLException, Exception {

			StringBuffer sb = new StringBuffer();

			EtsCrPmoIssueDocDAO crDocDao = new EtsCrPmoIssueDocDAO();

			int attachCount = crDocDao.getAttachmentCountForView(etsId);

			if (attachCount > 0) {

				sb.append("	<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\"> \n");
				sb.append("	     <tr >\n");
				sb.append("	       <td  height=\"18\" width=\"600\">\n");
				sb.append("	         <b>&nbsp;Attached file list</b>\n");
				sb.append("	       </td>\n");
				sb.append("	     </tr>\n");
				sb.append("	</table>\n");

				sb.append("    <table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\"> \n");
				sb.append("      <tr> \n");
				sb.append("       <td  height=\"18\" width=\"600\"> \n");
				sb.append("        [<span class=\"small\">Please note that you can view the file attachments using browser, only if your browser supports them. If you have any difficulty in viewing attachments using browser, please download it and view in compatible application.]</span><br />  \n");
				sb.append("        </td> \n");
				sb.append("      </tr> \n");
				sb.append("    </table> \n");
				sb.append("	<br />\n");

				sb.append("	<table summary=\"Table to display file attachments\" border=\"0\" cellpadding=\"1\" cellspacing=\"0\" width=\"600\"> \n");
				sb.append("	      <tr> \n");
				sb.append("	        <td bgcolor=\"#666666\" colspan=\"3\"> \n");
				sb.append("	          <table border=\"0\" cellpadding=\"1\" cellspacing=\"1\" width=\"100%\"> \n");
				sb.append("	            <tr height=\"18\"> \n");
				sb.append("	              <th id=\"atcol2\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
				sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;File name</span></b> \n");
				sb.append("	              </th> \n");
				sb.append("	              <th id=\"atcol3\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
				sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;Size</span></b> \n");
				sb.append("	              </th> \n");
				sb.append("	              <th id=\"atcol4\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
				sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;Description</span></b> \n");
				sb.append("	              </th> \n");
				sb.append("	              <th id=\"atcol6\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
				sb.append("	              	    &nbsp;\n");
				sb.append("	              </th> \n");
				sb.append("	            </tr> \n");

				Vector aList = crDocDao.getAttachedFiles(etsId);

				int count = 1;

				for (Iterator k = aList.iterator(); k.hasNext();) {
					EtsCrAttach attached = (EtsCrAttach) k.next();

					String fileName = attached.getDocName();
					long fileSize = attached.getFileSize();
					String fileDesc = attached.getDocDesc();
					int fileNo = attached.getDocNo();
					String fileDate = attached.getTimeStampString();

					String viewIssueUrl = "ETSIssueAttachServlet.wss?action=viewcr&fileNo=" + fileNo + "&edge_problem_id=" + etsId + " ";

					sb.append("	            <tr> \n");
					sb.append("	              <td headers=\"atcol2\" bgcolor=\"#ffffff\" align=\"left\">&nbsp;\n");
					sb.append(
						"	      <a href=\""
							+ viewIssueUrl
							+ "\" target=\"new\" onclick=\"window.open('"
							+ viewIssueUrl
							+ "','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=500,height=500,left=387,top=207'); return false;\" onkeypress =\"window.open('"
							+ viewIssueUrl
							+ "','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=500,height=500,left=387,top=207'); return false;\">");
					sb.append("" + fileName + " \n");
					sb.append(" </a> \n");
					sb.append("	              </td> \n");
					sb.append("	              <td headers=\"atcol3\" bgcolor=\"#ffffff\" align=\"left\"> \n");

					/* Display file size in proper size format */
					StringBuffer sbDisplay = new StringBuffer();

					sbDisplay.append(CommonFileUtils.printFileSize(fileSize + ""));

					sb.append("&nbsp;" + sbDisplay.toString() + " \n");
					sb.append("	              </td> \n");
					sb.append("	              <td headers=\"atcol4\" bgcolor=\"#ffffff\" align=\"left\"> \n");
					sb.append("&nbsp;" + fileDesc + " \n");
					sb.append("	              </td> \n");
					sb.append("	              <td headers=\"atcol6\" bgcolor=\"#ffffff\" align=\"left\"> \n");

					sb.append("	<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"15%\"> \n");
					sb.append("	  <tr> \n");
					sb.append("	    <td > \n");
					sb.append("	       &nbsp;<input type=\"image\"  src=\"" + Defines.BUTTON_ROOT  + "cancel_rd.gif\" name=\"delete" + String.valueOf(fileNo).trim() + "\" value=\"attachDelete\" border=\"0\" width=\"21\" height=\"21\" alt=\"Delete\" />&nbsp;&nbsp;");
					sb.append("	    </td> \n");
					sb.append("	    <td > \n");
					sb.append("Delete");
					sb.append("	    </td> \n");
					sb.append("	  </tr> \n");
					sb.append("	</table> \n");

					sb.append("	    </td> \n");
					sb.append("	  </tr> \n");

					count++;
				}
				sb.append("	            </table> \n");

				sb.append("		   </td> \n");
				sb.append("		</tr> \n");
				sb.append("	 </table> \n");

			} //if attachcount > 0

			return sb.toString();

		}
		
	/**
			 * 
			 * 
			 * @param etsIssObjKey
			 * @param edgeProblemId
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

		public String printFileAttachPageForCr(EtsIssObjectKey etsIssObjKey, EtsCrProbInfoModel crInfoModel, String actionType) throws SQLException, Exception {

			StringBuffer sbfile = new StringBuffer();

			int attachCount = 0;
			String etsId = crInfoModel.getEtsId();
			int cancelstate = crInfoModel.getCancelActionState();
			String issueClass = etsIssObjKey.getIssueClass();

			String dynProbType = "change request";

			EtsCrPmoIssueDocDAO docDao = new EtsCrPmoIssueDocDAO();

			attachCount = docDao.getAttachmentCount(etsId);

			int fileattachcount = 0;

			fileattachcount = docDao.getAttachmentCountForView(etsId);

			sbfile.append(
				"<form enctype=\"multipart/form-data\" method=\"post\" action=\"ETSProjectsServlet.wss?proj="
					+ etsIssObjKey.getProj().getProjectId()
					+ "&tc="
					+ etsIssObjKey.getTopCatId()
					+ "&linkid="
					+ etsIssObjKey.getSLink()
					+ "&op=2&actionType="
					+ actionType
					+ "&attach_file_no="
					+ String.valueOf(attachCount)
					+ "&etsId="
					+ etsId
					+ "&istyp="
					+ etsIssObjKey.getIstyp()
					+ "&flop="
					+ etsIssObjKey.getFilopn()
					+ "&cancelstate="
					+ cancelstate
					+ "\" >");
			sbfile.append("<table summary=\"File attachment summary\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n");
			sbfile.append("<tr>\n");
			sbfile.append("<td>\n");
			sbfile.append(CommonFileUtils.getFileAttachMsg(fileattachcount, dynProbType, actionType));
			sbfile.append("</td>\n");
			sbfile.append("</tr>\n");
			sbfile.append("</table>\n");
			sbfile.append("<br />\n");
			sbfile.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n");
			sbfile.append("<tr valign=\"top\">\n");
			sbfile.append("<td>\n");
			sbfile.append("<span class=\"small\"><span style=\"color:#ff0000\">\n");
			sbfile.append("(Note: Attachments cannot be deleted once " + dynProbType + " is submitted. Each file attachment size limit : " + MAXFILE_SIZE_IN_MB + " MB)\n");
			sbfile.append("</span>\n");
			sbfile.append("</span>\n");
			sbfile.append("</td>\n");
			sbfile.append("</tr>\n");
			sbfile.append("</table>\n");

			sbfile.append("<br />\n");

			sbfile.append(CommonFileUtils.printBasicFileAttachElements(etsIssObjKey));

			sbfile.append("</form> \n");

			return sbfile.toString();

		}		
		
	/**
						 * To display attached files
						 * 
						 */

		public String viewCRAttachedFilesListWithNoLinks(String etsId) {

			StringBuffer sb = new StringBuffer();

			EtsCrPmoIssueDocDAO crDocDao = new EtsCrPmoIssueDocDAO();

			int attachCount = 0;

			try {

				attachCount = crDocDao.getCRAttachmentCountWithFlag(etsId, "E");

				if (attachCount > 0) {

					sb.append("	<table summary=\"\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"600\"> \n");
					sb.append("	     <tr >\n");
					sb.append("	       <td  height=\"18\" width=\"600\">\n");
					sb.append("	         <b>&nbsp;Attached file list</b>\n");
					sb.append("	       </td>\n");
					sb.append("	     </tr>\n");
					sb.append("	</table>\n");

					sb.append("    <table summary=\"\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"600\"> \n");
					sb.append("      <tr> \n");
					sb.append("       <td  height=\"18\" width=\"600\"> \n");
					sb.append("        [<span class=\"small\">Please note that you can view the file attachments using browser, only if your browser supports them. If you have any difficulty in viewing attachments using browser, please download it and view in compatible application.]</span><br />  \n");
					sb.append("        </td> \n");
					sb.append("      </tr> \n");
					sb.append("    </table> \n");
					sb.append("	<br />\n");

					sb.append("	<table summary=\"Table to display file attachments\" border=\"0\" cellpadding=\"1\" cellspacing=\"0\" width=\"600\"> \n");
					sb.append("	      <tr> \n");
					sb.append("	        <td bgcolor=\"#666666\" colspan=\"3\"> \n");
					sb.append("	          <table border=\"0\" cellpadding=\"1\" cellspacing=\"1\" width=\"100%\"> \n");
					sb.append("	            <tr height=\"18\"> \n");
					sb.append("	              <th id=\"atcol2\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
					sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;File name</span></b> \n");
					sb.append("	              </th> \n");
					sb.append("	              <th id=\"atcol3\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
					sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;Size</span></b> \n");
					sb.append("	              </th> \n");
					sb.append("	              <th id=\"atcol4\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
					sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;Description</span></b> \n");
					sb.append("	              </th> \n");
					sb.append("	            </tr> \n");

					Vector aList = crDocDao.getAttachedFilesWithSrcFlg(etsId, "E");

					int count = 1;

					for (Iterator k = aList.iterator(); k.hasNext();) {
						EtsCrAttach attached = (EtsCrAttach) k.next();

						String fileName = attached.getDocName();
						long fileSize = attached.getFileSize();
						String fileDesc = attached.getDocDesc();
						int fileNo = attached.getDocNo();
						String fileDate = attached.getTimeStampString();

						String viewIssueUrl = "ETSIssueAttachServlet.wss?action=viewcr&fileNo=" + fileNo + "&edge_problem_id=" + etsId + " ";

						sb.append("	            <tr> \n");
						sb.append("	              <td headers=\"atcol2\" bgcolor=\"#ffffff\" align=\"left\">&nbsp;\n");
						sb.append("" + fileName + " \n");
						sb.append("	              </td> \n");
						sb.append("	              <td headers=\"atcol3\" bgcolor=\"#ffffff\" align=\"left\"> \n");

						/* Display file size in proper size format */
						StringBuffer sbDisplay = new StringBuffer();

						sbDisplay.append(CommonFileUtils.printFileSize(fileSize + ""));

						sb.append("&nbsp;" + sbDisplay.toString() + " \n");
						sb.append("	              </td> \n");
						sb.append("	              <td headers=\"atcol4\" bgcolor=\"#ffffff\" align=\"left\"> \n");
						sb.append("&nbsp;" + fileDesc + " \n");
						sb.append("	              </td> \n");
						sb.append("	  </tr> \n");

						count++;
					}
					sb.append("	            </table> \n");

					sb.append("		   </td> \n");
					sb.append("		</tr> \n");
					sb.append("	 </table> \n");
					sb.append("</table> ");

				} //if attachcount > 0

			} //end of try

			catch (Exception ex) {

				AmtCommonUtils.LogGenExpMsg(ex, "General Exception in viewCRAttachedFilesList", ETSLSTUSR);

				if (ex != null) {
					SysLog.log(SysLog.ERR, this, ex);
					ex.printStackTrace();

				}

			}

			return sb.toString();

		}
		
	/**
					 * To display attached files
					 * 
					 */

		public String viewCRAttachedFilesList(String etsId) {

			StringBuffer sb = new StringBuffer();

			EtsCrPmoIssueDocDAO crDocDao = new EtsCrPmoIssueDocDAO();

			int attachCount = 0;

			try {

				attachCount = crDocDao.getCRAttachmentCountWithFlag(etsId, "E");

				if (attachCount > 0) {

					sb.append("	<table summary=\"\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"600\"> \n");
					sb.append("	     <tr >\n");
					sb.append("	       <td  height=\"18\" width=\"600\">\n");
					sb.append("	         <b>&nbsp;Attached file list</b>\n");
					sb.append("	       </td>\n");
					sb.append("	     </tr>\n");
					sb.append("	</table>\n");

					sb.append("    <table summary=\"\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"600\"> \n");
					sb.append("      <tr> \n");
					sb.append("       <td  height=\"18\" width=\"600\"> \n");
					sb.append("        [<span class=\"small\">Please note that you can view the file attachments using browser, only if your browser supports them. If you have any difficulty in viewing attachments using browser, please download it and view in compatible application.]</span><br />  \n");
					sb.append("        </td> \n");
					sb.append("      </tr> \n");
					sb.append("    </table> \n");
					sb.append("	<br />\n");

					sb.append("	<table summary=\"Table to display file attachments\" border=\"0\" cellpadding=\"1\" cellspacing=\"0\" width=\"600\"> \n");
					sb.append("	      <tr> \n");
					sb.append("	        <td bgcolor=\"#666666\" colspan=\"3\"> \n");
					sb.append("	          <table border=\"0\" cellpadding=\"1\" cellspacing=\"1\" width=\"100%\"> \n");
					sb.append("	            <tr height=\"18\"> \n");
					sb.append("	              <th id=\"atcol2\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
					sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;File name</span></b> \n");
					sb.append("	              </th> \n");
					sb.append("	              <th id=\"atcol3\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
					sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;Size</span></b> \n");
					sb.append("	              </th> \n");
					sb.append("	              <th id=\"atcol4\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
					sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;Description</span></b> \n");
					sb.append("	              </th> \n");
					sb.append("	            </tr> \n");

					Vector aList = crDocDao.getAttachedFilesWithSrcFlg(etsId, "E");

					int count = 1;

					for (Iterator k = aList.iterator(); k.hasNext();) {
						EtsCrAttach attached = (EtsCrAttach) k.next();

						String fileName = attached.getDocName();
						long fileSize = attached.getFileSize();
						String fileDesc = attached.getDocDesc();
						int fileNo = attached.getDocNo();
						String fileDate = attached.getTimeStampString();

						String viewIssueUrl = "ETSIssueAttachServlet.wss?action=viewcr&fileNo=" + fileNo + "&edge_problem_id=" + etsId + " ";

						sb.append("	            <tr> \n");
						sb.append("	              <td headers=\"atcol2\" bgcolor=\"#ffffff\" align=\"left\">&nbsp;\n");
						sb.append(
							"	      <a href=\""
								+ viewIssueUrl
								+ "\" target=\"new\" onclick=\"window.open('"
								+ viewIssueUrl
								+ "','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=500,height=500,left=387,top=207'); return false;\" onkeypress =\"window.open('"
								+ viewIssueUrl
								+ "','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=500,height=500,left=387,top=207'); return false;\">");
						sb.append("" + fileName + " \n");
						sb.append(" </a> \n");
						sb.append("	              </td> \n");
						sb.append("	              <td headers=\"atcol3\" bgcolor=\"#ffffff\" align=\"left\"> \n");

						/* Display file size in proper size format */
						StringBuffer sbDisplay = new StringBuffer();

						sbDisplay.append(CommonFileUtils.printFileSize(fileSize + ""));

						sb.append("&nbsp;" + sbDisplay.toString() + " \n");
						sb.append("	              </td> \n");
						sb.append("	              <td headers=\"atcol4\" bgcolor=\"#ffffff\" align=\"left\"> \n");
						sb.append("&nbsp;" + fileDesc + " \n");
						sb.append("	              </td> \n");
						sb.append("	  </tr> \n");

						count++;
					}
					sb.append("	            </table> \n");

					sb.append("		   </td> \n");
					sb.append("		</tr> \n");
					sb.append("	 </table> \n");
					sb.append("</table> ");

				} //if attachcount > 0

			} //end of try

			catch (Exception ex) {

				AmtCommonUtils.LogGenExpMsg(ex, "General Exception in viewCRAttachedFilesList", ETSLSTUSR);

				if (ex != null) {
					SysLog.log(SysLog.ERR, this, ex);
					ex.printStackTrace();

				}

			}

			return sb.toString();

		}
	



} //end of class
