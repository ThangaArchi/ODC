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
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSMimeDataList;
import oem.edge.ets.fe.MimeMultipartParser;
import oem.edge.ets.fe.WebAccessBodyPart;
import oem.edge.ets.fe.common.EncodeUtils;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.DocumentsHelper;
import oem.edge.ets.fe.ismgt.dao.EtsCrPmoIssueDocDAO;
import oem.edge.ets.fe.ismgt.model.ETSIssueAttach;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssProbInfoUsr1Model;
import oem.edge.ets.fe.ismgt.model.EtsIssUserRolesModel;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;

import org.apache.commons.logging.Log;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DocFileUtils implements EtsIssFilterConstants, EtsIssueConstants, IsmgtFileUtilsIF {

	public static final String VERSION = "1.3";
	private EtsIssCommonGuiUtils comGuiUtils;
	private static Log logger = EtsLogger.getLogger(DocFileUtils.class);
	

	/**
	 * 
	 */
	public DocFileUtils() {
		super();
		comGuiUtils = new EtsIssCommonGuiUtils();

	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.ismgt.helpers.IsmgtFileUtilsIF#getIssuesDoc(java.lang.String, java.lang.String)
	 */
	public int getIssuesDoc(String strProjectId, String strProblemId) {

		return DocumentsHelper.getIssuesDoc(strProjectId, strProblemId);
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.ismgt.helpers.IsmgtFileUtilsIF#getIssuesDoc(java.lang.String, java.lang.String, java.lang.String)
	 */
	public int getIssuesDoc(String strProjectId, String strProblemId, String strUserId) {

		return DocumentsHelper.getIssuesDoc(strProjectId, strProblemId, strUserId);
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.ismgt.helpers.IsmgtFileUtilsIF#attachIssueFile(java.lang.String, java.lang.String, int, oem.edge.ets.fe.ismgt.model.ETSIssueAttach)
	 */
	public boolean attachIssueFile(String strProjectId, String strStatusFlag, int iDocId, ETSIssueAttach udIssueDetails) {

		return DocumentsHelper.attachIssueFile(strProjectId, strStatusFlag, iDocId, udIssueDetails);
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.ismgt.helpers.IsmgtFileUtilsIF#getIssueFiles(java.lang.String, java.lang.String, java.lang.String)
	 */
	public List getIssueFiles(String strProjectId, String strProblemId, String strStatusFlag) {

		return DocumentsHelper.getIssueFiles(strProjectId, strProblemId, strStatusFlag);
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.ismgt.helpers.IsmgtFileUtilsIF#deleteIssueFile(java.lang.String, java.lang.String, int)
	 */
	public boolean deleteIssueFile(String strProjectId, String strProblemId, int iDocFileId) {

		return DocumentsHelper.deleteIssueFile(strProjectId, strProblemId, iDocFileId);
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.ismgt.helpers.IsmgtFileUtilsIF#updateIssueFileStatus(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean updateIssueFileStatus(String strProjectId, String strProblemId, String strOldStatus, String strNewStatus) {

		return DocumentsHelper.updateIssueFileStatus(strProjectId, strProblemId, strOldStatus, strNewStatus);
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.ismgt.helpers.IsmgtFileUtilsIF#updateIssueFileStatus(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean updateIssueFileStatus(String strProjectId, String strProblemId, String strNewStatus) {

		return DocumentsHelper.updateIssueFileStatus(strProjectId, strProblemId, strNewStatus);
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.ismgt.helpers.IsmgtFileUtilsIF#getIssueFilesWithoutFlag(java.lang.String, java.lang.String, java.lang.String)
	 */
	public List getIssueFilesWithoutFlag(String strProjectId, String strProblemId, String strStatusFlag) {

		return DocumentsHelper.getIssueFilesWithoutFlag(strProjectId, strProblemId, strStatusFlag);
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.ismgt.helpers.IsmgtFileUtilsIF#deleteIssueFilesWithoutStatus(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean deleteIssueFilesWithoutStatus(String strProjectId, String strProblemId, String strStatus) {

		return DocumentsHelper.deleteIssueFilesWithoutStatus(strProjectId, strProblemId, strStatus);
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.ismgt.helpers.IsmgtFileUtilsIF#deleteIssueFilesWithStatus(java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean deleteIssueFilesWithStatus(String strProjectId, String strProblemId, String strStatus) {

		return DocumentsHelper.deleteIssueFilesWithStatus(strProjectId, strProblemId, strStatus);
	}

	/**
		 * 
		 * @param etsIssObjKey
		 * @param edgeProblemId
		 * @return
		 * @throws Exception
		 */

	public String getDocPubKeyStr(EtsIssObjectKey etsIssObjKey, String edgeProblemId) throws Exception {

		String strEdgeId = etsIssObjKey.getEs().gUSERN;

		String strProjectId = etsIssObjKey.getProj().getProjectId();

		int iDocId = getIssuesDoc(strProjectId, edgeProblemId);

		String strDocId = AmtCommonUtils.getTrimStr(iDocId + "");

		String strTopCatId = AmtCommonUtils.getTrimStr(etsIssObjKey.getTopCatId() + "");

		String tokenId = EncodeUtils.encode(strDocId, strProjectId, strEdgeId, strTopCatId, "0");

		return tokenId;

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

	public String printFileAttachPageForITAR(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, String actionType) throws SQLException, Exception {

		StringBuffer sbfile = new StringBuffer();

		int attachCount = 0;
		String edgeProblemId = usr1InfoModel.getEdgeProblemId();
		int cancelstate = usr1InfoModel.getCancelActionState();
		String issueClass = etsIssObjKey.getIssueClass();
		EtsIssUserRolesModel usrRolesModel = etsIssObjKey.getUsrRolesModel();
		String projectId = etsIssObjKey.getProj().getProjectId();

		String dynProbType = "issue";

		if (issueClass.equals(ETSISSUESUBTYPE)) {

			dynProbType = "issue";

		} else if (issueClass.equals(ETSCHANGESUBTYPE)) {

			dynProbType = "change request";
		} else {

			dynProbType = "issue";

		}

		try {

			//attachCount = ETSIssuesManager.getAttachmentCount(edgeProblemId, null);
			//6.1.1 migrating to documents repository
			attachCount = getIssuesDoc(projectId, edgeProblemId);

		} catch (Exception e) {

			SysLog.log(SysLog.ERR, this, e);
			e.printStackTrace();
		}

		int fileattachcount = 0;

		try {

			//fileattachcount = ETSIssuesManager.getAttachmentCountForView(edgeProblemId, null);
			//6.1.1 migrating to documents repository
			fileattachcount = getIssueFileCount(projectId, edgeProblemId, "");

		} catch (Exception e) {

			SysLog.log(SysLog.ERR, this, e);
			e.printStackTrace();
		}

		//get the special token
		String publicKeyToken = getDocPubKeyStr(etsIssObjKey, edgeProblemId);

		String itarActionPath = EncodeUtils.getITARDocUploadPath();

		////for issues from PMO////

		String issueSource = AmtCommonUtils.getTrimStr(usr1InfoModel.getIssueSource());

		if (issueSource.equals(ETSPMOSOURCE)) {

			EtsCrPmoIssueDocDAO docDao = new EtsCrPmoIssueDocDAO();

			attachCount = docDao.getAttachmentCount(edgeProblemId);
			fileattachcount = docDao.getAttachmentCountForView(edgeProblemId);

		}

		////////////////////////////////
		//chk if it is on ext behalf
		String extBhf = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("extbhf"));

		String errorCode = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("error"));
		
		logger.debug("err code in itar file attach=="+errorCode);

		String errMsg = "";
		
		//get file empty code err msg
		if (AmtCommonUtils.isResourceDefined(errorCode)) {

			errMsg = getDocMsgResource(errorCode);

		}

		//		sbfile.append(
		//			"<form enctype=\"multipart/form-data\" method=\"post\" action=\""
		//				+ itarActionPath
		//				+ "?proj="
		//				+ etsIssObjKey.getProj().getProjectId()
		//				+ "&tc="
		//				+ etsIssObjKey.getTopCatId()
		//				+ "&linkid="
		//				+ etsIssObjKey.getSLink()
		//				+ "&op=2&actionType="
		//				+ actionType
		//				+ "&attach_file_no="
		//				+ String.valueOf(attachCount)
		//				+ "&edge_problem_id="
		//				+ edgeProblemId
		//				+ "&extbhf="
		//				+ extBhf
		//				+ "&istyp="
		//				+ etsIssObjKey.getIstyp()
		//				+ "&flop="
		//				+ etsIssObjKey.getFilopn()
		//				+ "&cancelstate="
		//				+ cancelstate
		//				+ "\" >");

		sbfile.append("<form enctype=\"multipart/form-data\" method=\"post\" action=\"" + itarActionPath + "\" >");

		String issUrl = getSendRedirectUrl(etsIssObjKey, actionType, edgeProblemId, extBhf, cancelstate, attachCount);

		sbfile.append("<input type=\"hidden\" name=\"docid\" value=\"" + String.valueOf(attachCount) + "\" />");
		sbfile.append("<input type=\"hidden\" name=\"encodedToken\" value=\"" + publicKeyToken + "\" />");
		sbfile.append("<input type=\"hidden\" name=\"docAction\" value=\"" + issUrl + "\" />");
		sbfile.append("<input type=\"hidden\" name=\"formContext\" value=\"ISSUES\" />");
		sbfile.append("<input type=\"hidden\" name=\"document.itarStatus\" value=\"T\" />");
		//		////new post vars as hidden for post//
		sbfile.append("<input type=\"hidden\" name=\"proj\" value=\"" + etsIssObjKey.getProj().getProjectId() + "\" />");
		sbfile.append("<input type=\"hidden\" name=\"tc\" value=\"" + etsIssObjKey.getTopCatId() + "\" />");
		sbfile.append("<input type=\"hidden\" name=\"linkid\" value=\"" + etsIssObjKey.getSLink() + "\" />");
		sbfile.append("<input type=\"hidden\" name=\"op\" value=\"2\" />");
		sbfile.append("<input type=\"hidden\" name=\"actionType\" value=\"" + actionType + "\" />");
		sbfile.append("<input type=\"hidden\" name=\"attach_file_no\" value=\"" + String.valueOf(attachCount) + "\" />");
		sbfile.append("<input type=\"hidden\" name=\"edge_problem_id\" value=\"" + edgeProblemId + "\" />");
		sbfile.append("<input type=\"hidden\" name=\"extbhf\" value=\"" + extBhf + "\" />");
		sbfile.append("<input type=\"hidden\" name=\"istyp\" value=\"" + etsIssObjKey.getIstyp() + "\" />");
		sbfile.append("<input type=\"hidden\" name=\"flop\" value=\"" + etsIssObjKey.getFilopn() + "\" />");
		sbfile.append("<input type=\"hidden\" name=\"cancelstate\" value=\"" + cancelstate + "\" />");
		//////

		sbfile.append(comGuiUtils.printStdErrMsg(errMsg));

		///////////////
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
		sbfile.append("(Note: Attachments cannot be deleted once " + dynProbType + " is submitted. Each file attachment size limit : " + MAXFILE_SIZE_IN_MB + " MB.\n");

		///blade type proj
		if (!etsIssObjKey.isProjBladeType()) {

		} else {

			if (usrRolesModel.isBladeUsrInt()) {

				if (AmtCommonUtils.isResourceDefined(extBhf)) { //for ext bhf

					sbfile.append("Attachments should not contain any confidential information about the customer project or company. )\n");

				}

			} else {

				sbfile.append("Attachments should not contain any confidential information about the customer project or company. )\n");

			}

		}
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
	 * To get the send redirect url from btv server > bld server
	 * @param etsIssObjKey
	 * @param actionType
	 * @param edgeProblemId
	 * @param extBhf
	 * @param cancelstate
	 * @param attachCount
	 * @return
	 */

	public String getSendRedirectUrl(EtsIssObjectKey etsIssObjKey, String actionType, String edgeProblemId, String extBhf, int cancelstate, int attachCount) {

		StringBuffer sbfile = new StringBuffer();

		sbfile.append(
			"ETSProjectsServlet.wss?proj="
				+ etsIssObjKey.getProj().getProjectId()
				+ "&tc="
				+ etsIssObjKey.getTopCatId()
				+ "&linkid="
				+ etsIssObjKey.getSLink()
				+ "&op=2&actionType="
				+ actionType
				+ "&attach_file_no="
				+ String.valueOf(attachCount)
				+ "&edge_problem_id="
				+ edgeProblemId
				+ "&extbhf="
				+ extBhf
				+ "&istyp="
				+ etsIssObjKey.getIstyp()
				+ "&attch=I&flop="
				+ etsIssObjKey.getFilopn()
				+ "&cancelstate="
				+ cancelstate
				+ "");

		return sbfile.toString();
	}

	/**
		 * To get the issue files attach count
		 * @param projectId
		 * @param edgeProblemId
		 * @return
		 */

	public int getIssueFileCount(String projectId, String edgeProblemId, String statusFlag) {

		int attachCount = 0;

		List cList = getIssueFiles(projectId, edgeProblemId, statusFlag);

		if (cList != null && !cList.isEmpty()) {

			attachCount = cList.size();
		}

		return attachCount;

	}

	/**
			 * to attach files for a given issue/change
			 * returns an array of code/messg for success/failure
			 * 
			 */

	public String[] doAttach(HttpServletRequest request) {

		try {

			Vector mult = MimeMultipartParser.getBodyParts(request.getInputStream());
			int count = mult.size();
			String edge_problem_id = AmtCommonUtils.getTrimStr(request.getParameter("edge_problem_id"));
			String filenumber = AmtCommonUtils.getTrimStr(request.getParameter("attach_file_no"));
			String cancelstate = AmtCommonUtils.getTrimStr(request.getParameter("cancelstate"));
			String strProjectId = AmtCommonUtils.getTrimStr(request.getParameter("proj"));

			Global.println("edge problem id in doAttach===" + edge_problem_id);
			Global.println("filenumber in doAttach===" + filenumber);
			Global.println("cancelstate===" + cancelstate);

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

			ETSIssueAttach attachment = new ETSIssueAttach();
			attachment.setApplicationId("ETS");
			attachment.setCqTrackId("x");
			attachment.setEdgeProblemId(edge_problem_id);
			attachment.setFileNewFlag("T");
			attachment.setFileNo(Integer.valueOf(filenumber).intValue());
			attachment.setSeqNo(1);

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
						attachment.setFileName(fileName);
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
					attachment.setFileDesc(file_desc);

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

				System.out.println("writeDocument -- Input stream not found for file. Please specify proper file location path.");
				String[] err = { "0", "Input connection for file not found, please try again" };
				return err;

			}

			int inStreamAvail = -1;

			try {

				inStreamAvail = inStream.available();

				if (inStreamAvail == 0) {

					System.out.println("writeDocument -- Input stream not found for file. Please specify proper file location path.");
					String[] err = { "0", "Input connection for file not found, please try again" };
					return err;
				}

				if (inStreamAvail > (MAXFILE_SIZE_IN_BYTES)) {

					System.out.println("writeDocument -- File over " + MAXFILE_SIZE_IN_MB + " MB limit.");
					String[] err = { "0", "The File is over the " + MAXFILE_SIZE_IN_MB + " MB limit.  Please use the DropBox for this file." };
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

			Global.println("before loading file into DB success = " + String.valueOf(success));

			//int iDocId=getIssuesDoc()
			Global.println("DOC ID IS : " + filenumber);

			success = attachIssueFile(strProjectId, "T", Integer.valueOf(filenumber).intValue(), attachment);

			//success = ETSIssuesManager.createAttachment(attachment, null);

			Global.println("after loading file into DB success = " + String.valueOf(success));

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

	public String printPrevAttachedFilesList(String projectId, String edgeProblemId) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();

		Global.println("edge prob id in modify file list===" + edgeProblemId);

		//int attachCount = ETSIssuesManager.getAttachmentCountForPrevFiles(edgeProblemId, null);

		int iDocId = getIssuesDoc(projectId, edgeProblemId);

		int attachCount = 0;

		List cList = getIssueFiles(projectId, edgeProblemId, "N");

		if (cList != null && !cList.isEmpty()) {

			attachCount = cList.size();

		}

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

			//Vector aList = (Vector) ETSIssuesManager.getAttachedFiles(edgeProblemId, null);
			//doc migrtn to 6.1.1
			int count = 1;

			for (Iterator k = cList.iterator(); k.hasNext();) {
				ETSIssueAttach attached = (ETSIssueAttach) k.next();

				String fileName = attached.getFileName();
				long fileSize = attached.getFileSize();
				String fileDesc = attached.getFileDesc();
				int fileNo = attached.getFileNo();
				String fileDate = attached.getTimeStampString();
				String fileNewFlg = attached.getFileNewFlag();

				//String viewIssueUrl = "ETSIssueAttachServlet.wss?action=view&fileNo=" + fileNo + "&edge_problem_id=" + edgeProblemId + " ";
				String viewIssueUrl = getDocAttachUrl(projectId, fileName, iDocId, fileNo);

				Global.println("fileNewFlg===" + fileNewFlg);

				if (fileNewFlg.equals("N")) { //show only old files

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

				} //show only old files

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
		 * To get view attach url for docs attached under non-pmo issues
		 * @param projectId
		 * @param fileName
		 * @param docId
		 * @param docFileId
		 * @return
		 */

	public String getDocAttachUrl(String projectId, String fileName, int docId, int docFileId) {

		StringBuffer sb = new StringBuffer();

		sb.append("ETSContentDeliveryServlet.wss/");
		sb.append(URLEncoder.encode(fileName));
		sb.append("?projid=" + projectId + "");
		sb.append("&docid=" + docId + "");
		sb.append("&docfileid=" + docFileId + "");

		return sb.toString();

	}

	/**
				 * 
				 * 
				 * @param filecount
				 * @return
				 */

	public String getIndepFileAttachMsg(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, String actionType) {

		StringBuffer sbfile = new StringBuffer();

		int attachCount = 0;
		String projectId = etsIssObjKey.getProj().getProjectId();
		String edgeProblemId = usr1InfoModel.getEdgeProblemId();
		int cancelstate = usr1InfoModel.getCancelActionState();
		String issueClass = etsIssObjKey.getIssueClass();
		String issueSource = usr1InfoModel.getIssueSource();

		String dynProbType = "issue";

		if (issueClass.equals(ETSISSUESUBTYPE)) {

			dynProbType = "issue";

		} else if (issueClass.equals(ETSCHANGESUBTYPE)) {

			dynProbType = "change request";
		} else {

			dynProbType = "issue";

		}

		if (!issueSource.equals(ETSPMOSOURCE)) {

			try {

				//6.1.1 migrating to documents repository
				//attachCount = ETSIssuesManager.getAttachmentCountForView(edgeProblemId, null);
				attachCount = getIssueFileCount(projectId, edgeProblemId, "");

			} catch (Exception e) {

				SysLog.log(SysLog.ERR, this, e);
				e.printStackTrace();
			}

		} else {

			EtsCrPmoIssueDocDAO crIssueDao = new EtsCrPmoIssueDocDAO();

			try {

				attachCount = crIssueDao.getAttachmentCountForView(edgeProblemId);

			} catch (Exception e) {

				SysLog.log(SysLog.ERR, this, e);
				e.printStackTrace();
			}

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

	public String getIndepViewFileAttachMsg(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, String actionType) {

		StringBuffer sbfile = new StringBuffer();

		int attachCount = 0;
		String projectId = AmtCommonUtils.getTrimStr(etsIssObjKey.getProj().getProjectId());
		String edgeProblemId = AmtCommonUtils.getTrimStr(usr1InfoModel.getEdgeProblemId());
		int cancelstate = usr1InfoModel.getCancelActionState();
		String issueClass = AmtCommonUtils.getTrimStr(etsIssObjKey.getIssueClass());

		///////////////////////////
		String issueSource = AmtCommonUtils.getTrimStr(usr1InfoModel.getIssueSource());

		String dynProbType = "issue";

		if (issueClass.equals(ETSISSUESUBTYPE)) {

			dynProbType = "issue";

		} else if (issueClass.equals(ETSCHANGESUBTYPE)) {

			dynProbType = "change request";
		} else {

			dynProbType = "issue";

		}

		if (!issueSource.equals(ETSPMOSOURCE)) {

			try {

				//6.1.1 migrating to documents repository
				//attachCount = ETSIssuesManager.getAttachmentCountForView(edgeProblemId, null);
				attachCount = getIssueFileCount(projectId, edgeProblemId, "");

			} catch (Exception e) {

				SysLog.log(SysLog.ERR, this, e);
				e.printStackTrace();
			}

		} else {

			EtsCrPmoIssueDocDAO crIssueDao = new EtsCrPmoIssueDocDAO();

			try {

				attachCount = crIssueDao.getAttachmentCountForView(edgeProblemId);

			} catch (Exception e) {

				SysLog.log(SysLog.ERR, this, e);
				e.printStackTrace();
			}

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
				 * To display attached files
				 * 
				 */

	public String printAttachedFilesList(String projectId, String edgeProblemId) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();

		//6.1.1 migrating to documents repository
		//int attachCount = ETSIssuesManager.getAttachmentCountForView(edgeProblemId, null);

		int iDocId = getIssuesDoc(projectId, edgeProblemId);

		int attachCount = 0;

		List cList = getIssueFiles(projectId, edgeProblemId, "");

		if (cList != null && !cList.isEmpty()) {

			attachCount = cList.size();
		}

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

			//6.1.1 migrating to documents repository
			//Vector aList = (Vector) ETSIssuesManager.getAttachedFiles(edgeProblemId, null);
			int count = 1;

			for (Iterator k = cList.iterator(); k.hasNext();) {
				ETSIssueAttach attached = (ETSIssueAttach) k.next();

				String fileName = attached.getFileName();
				long fileSize = attached.getFileSize();
				String fileDesc = attached.getFileDesc();
				int fileNo = attached.getFileNo();
				String fileDate = attached.getTimeStampString();

				//String viewIssueUrl = "ETSIssueAttachServlet.wss?action=view&fileNo=" + fileNo + "&edge_problem_id=" + edgeProblemId + " ";

				String viewIssueUrl = getDocAttachUrl(projectId, fileName, iDocId, fileNo);

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

	public String printAttachedFilesListForModify(String projectId, String edgeProblemId) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();

		Global.println("edge prob id in modify file list===" + edgeProblemId);

		//int attachCount = ETSIssuesManager.getAttachmentCountForView(edgeProblemId, null);
		//6.1.1 migrating to documents repository

		int attachCount = getIssueFileCount(projectId, edgeProblemId, "");

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
			sb.append(printPrevAttachedFilesList(projectId, edgeProblemId));

			sb.append("	<br />\n");
			sb.append(printCurAttachedFilesList(projectId, edgeProblemId));

		} //if attachcount > 0

		return sb.toString();

	}

	/**
					 * To display attached files for Modify problem
					 * 
					 */

	public String printCurAttachedFilesList(String projectId, String edgeProblemId) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();

		Global.println("edge prob id in modify file list===" + edgeProblemId);

		//int attachCount = ETSIssuesManager.getAttachmentCountForNewFiles(edgeProblemId, null);
		//		6.1.1 change to doc repository

		int iDocId = getIssuesDoc(projectId, edgeProblemId);

		int attachCount = 0;

		//since all the old files are set to flag > N, all new files are set to flag (T >> Y)

		List cList = getIssueFilesWithoutFlag(projectId, edgeProblemId, "N");

		if (cList != null && !cList.isEmpty()) {

			attachCount = cList.size();
		}

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

			//Vector aList = (Vector) ETSIssuesManager.getAttachedFiles(edgeProblemId, null);
			//6.1.1 change to doc repository
			int count = 1;

			for (Iterator k = cList.iterator(); k.hasNext();) {
				ETSIssueAttach attached = (ETSIssueAttach) k.next();

				String fileName = attached.getFileName();
				long fileSize = attached.getFileSize();
				String fileDesc = attached.getFileDesc();
				int fileNo = attached.getFileNo();
				String fileDate = attached.getTimeStampString();
				String fileNewFlg = attached.getFileNewFlag();

				//String viewIssueUrl = "ETSIssueAttachServlet.wss?action=view&fileNo=" + fileNo + "&edge_problem_id=" + edgeProblemId + " ";
				String viewIssueUrl = getDocAttachUrl(projectId, fileName, iDocId, fileNo);

				if (!fileNewFlg.equals("N")) { //show only old files

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

				}

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

	public String printFileAttachPageForNonITAR(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, String actionType) throws SQLException, Exception {

		StringBuffer sbfile = new StringBuffer();

		int attachCount = 0;
		String edgeProblemId = usr1InfoModel.getEdgeProblemId();
		int cancelstate = usr1InfoModel.getCancelActionState();
		String issueClass = etsIssObjKey.getIssueClass();
		EtsIssUserRolesModel usrRolesModel = etsIssObjKey.getUsrRolesModel();
		String projectId = etsIssObjKey.getProj().getProjectId();

		String dynProbType = "issue";

		if (issueClass.equals(ETSISSUESUBTYPE)) {

			dynProbType = "issue";

		} else if (issueClass.equals(ETSCHANGESUBTYPE)) {

			dynProbType = "change request";
		} else {

			dynProbType = "issue";

		}

		try {

			//attachCount = ETSIssuesManager.getAttachmentCount(edgeProblemId, null);
			//6.1.1 migrating to documents repository
			attachCount = getIssuesDoc(projectId, edgeProblemId);

		} catch (Exception e) {

			SysLog.log(SysLog.ERR, this, e);
			e.printStackTrace();
		}

		int fileattachcount = 0;

		try {

			//fileattachcount = ETSIssuesManager.getAttachmentCountForView(edgeProblemId, null);
			//6.1.1 migrating to documents repository
			fileattachcount = getIssueFileCount(projectId, edgeProblemId, "");

		} catch (Exception e) {

			SysLog.log(SysLog.ERR, this, e);
			e.printStackTrace();
		}

		////for issues from PMO////

		String issueSource = AmtCommonUtils.getTrimStr(usr1InfoModel.getIssueSource());

		if (issueSource.equals(ETSPMOSOURCE)) {

			EtsCrPmoIssueDocDAO docDao = new EtsCrPmoIssueDocDAO();

			attachCount = docDao.getAttachmentCount(edgeProblemId);
			fileattachcount = docDao.getAttachmentCountForView(edgeProblemId);

		}

		////////////////////////////////
		//chk if it is on ext behalf
		String extBhf = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("extbhf"));

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
				+ "&edge_problem_id="
				+ edgeProblemId
				+ "&extbhf="
				+ extBhf
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
		sbfile.append("(Note: Attachments cannot be deleted once " + dynProbType + " is submitted. Each file attachment size limit : " + MAXFILE_SIZE_IN_MB + " MB.\n");

		///blade type proj
		if (!etsIssObjKey.isProjBladeType()) {

		} else {

			if (usrRolesModel.isBladeUsrInt()) {

				if (AmtCommonUtils.isResourceDefined(extBhf)) { //for ext bhf

					sbfile.append("Attachments should not contain any confidential information about the customer project or company. )\n");

				}

			} else {

				sbfile.append("Attachments should not contain any confidential information about the customer project or company. )\n");

			}

		}
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

	public String viewAttachedFilesList(String projectId, String edgeProblemId) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();

		//6.1.1 migrating to documents repository
		//int attachCount = ETSIssuesManager.getAttachmentCountForView(edgeProblemId, null);

		int iDocId = getIssuesDoc(projectId, edgeProblemId);

		int attachCount = 0;

		List cList = getIssueFiles(projectId, edgeProblemId, "");

		if (cList != null && !cList.isEmpty()) {

			attachCount = cList.size();
		}

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

			//Vector aList = (Vector) ETSIssuesManager.getAttachedFiles(edgeProblemId, null);
			int count = 1;

			for (Iterator k = cList.iterator(); k.hasNext();) {
				ETSIssueAttach attached = (ETSIssueAttach) k.next();

				String fileName = attached.getFileName();
				long fileSize = attached.getFileSize();
				String fileDesc = attached.getFileDesc();
				int fileNo = attached.getFileNo();
				String fileDate = attached.getTimeStampString();

				//String viewIssueUrl = "ETSIssueAttachServlet.wss?action=view&fileNo=" + fileNo + "&edge_problem_id=" + edgeProblemId + " ";
				String viewIssueUrl = getDocAttachUrl(projectId, fileName, iDocId, fileNo);

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

		return sb.toString();

	}

	/**
				 * To display attached files
				 * 
				 */

	public String viewAttachedFilesListWithNoLinks(String projectId, String edgeProblemId) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();

		//6.1.1 migrating to documents repository
		//int attachCount = ETSIssuesManager.getAttachmentCountForView(edgeProblemId, null);

		int iDocId = getIssuesDoc(projectId, edgeProblemId);

		int attachCount = 0;

		List cList = getIssueFiles(projectId, edgeProblemId, "");

		if (cList != null && !cList.isEmpty()) {

			attachCount = cList.size();
		}

		if (attachCount > 0) {

			sb.append("	<table summary=\"\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"600\"> \n");
			sb.append("	     <tr >\n");
			sb.append("	       <td  height=\"18\" width=\"600\">\n");
			sb.append("	         <b>&nbsp;Attached file list</b>\n");
			sb.append("	       </td>\n");
			sb.append("	     </tr>\n");
			sb.append("	</table>\n");

			//sb.append("	<br />\n");

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

			//Vector aList = (Vector) ETSIssuesManager.getAttachedFiles(edgeProblemId, null);
			int count = 1;

			for (Iterator k = cList.iterator(); k.hasNext();) {
				ETSIssueAttach attached = (ETSIssueAttach) k.next();

				String fileName = attached.getFileName();
				long fileSize = attached.getFileSize();
				String fileDesc = attached.getFileDesc();
				int fileNo = attached.getFileNo();
				String fileDate = attached.getTimeStampString();

				//String viewIssueUrl = "ETSIssueAttachServlet.wss?action=view&fileNo=" + fileNo + "&edge_problem_id=" + edgeProblemId + " ";
				String viewIssueUrl = getDocAttachUrl(projectId, fileName, iDocId, fileNo);

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

		return sb.toString();

	}

	/** @param strResourceID
	  * @return
	  */
	private String getDocMsgResource(String strResourceID) {
		String strResource = null;
		String ITAR_DOC_RESOURCE_BUNDLE = "oem.edge.ets.fe.DocumentMessages";

		try {
			ResourceBundle pdResources = ResourceBundle.getBundle(ITAR_DOC_RESOURCE_BUNDLE);

			String strKeyPath = null;
			if (pdResources != null) {
				strResource = pdResources.getString(strResourceID);
			}
		} catch (Exception e) {

			e.printStackTrace();

		}

		return strResource;
	}

} //end of class
