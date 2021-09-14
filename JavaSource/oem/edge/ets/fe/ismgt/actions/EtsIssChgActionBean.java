package oem.edge.ets.fe.ismgt.actions;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.Global;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSMimeDataList;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSStringUtils;
import oem.edge.ets.fe.MimeMultipartParser;
import oem.edge.ets.fe.WebAccessBodyPart;
import oem.edge.ets.fe.ismgt.bdlg.EtsIssLogsDataPrepBean;
import oem.edge.ets.fe.ismgt.dao.ETSIssuesManager;
import oem.edge.ets.fe.ismgt.dao.EtsDropDownDAO;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.model.ETSIssueAttach;
import oem.edge.ets.fe.ismgt.model.EtsDropDownDataBean;
import oem.edge.ets.fe.ismgt.model.EtsIssLogActionDetails;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;
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

/**
 * @author v2phani
 * This basic class is an abstract class for representing various actions
 * on issue/change
 */
public abstract class EtsIssChgActionBean implements EtsIssFilterConstants, EtsIssueConstants {

	public static final String VERSION = "1.63";

	//constructor params
	private HttpServletRequest request;
	private EdgeAccessCntrl es;
	private Hashtable params;
	private String sLink;
	private ETSProj proj;
	private int topCatId;

	//optional set param
	private HttpServletResponse response;

	//mandatory set params  in servlet
	private int filenum;
	private String subActionType;
	private String submitValue;
	private EtsIssObjectKey etsIssObjKey;

	/**
	 * Constructor for EtsIssChgActionBean.
	 */

	public EtsIssChgActionBean(EtsIssObjectKey etsIssObjKey) {
		super();
		this.request = etsIssObjKey.getRequest();
		this.params = etsIssObjKey.getParams();
		this.es = etsIssObjKey.getEs();
		this.sLink = etsIssObjKey.getSLink();
		this.proj = etsIssObjKey.getProj();
		this.topCatId = etsIssObjKey.getTopCatId();
		this.filenum = etsIssObjKey.getFilenum();
		this.subActionType = etsIssObjKey.getSubActionType();
		this.submitValue = etsIssObjKey.getSubmitValue();
		this.etsIssObjKey = etsIssObjKey;
		this.response = etsIssObjKey.getResponse();
	}

	/**
	 * Returns the es.
	 * @return EdgeAccessCntrl
	 */
	public EdgeAccessCntrl getEs() {
		return es;
	}

	/**
	 * Returns the params.
	 * @return Hashtable
	 */
	public Hashtable getParams() {
		return params;
	}

	/**
	 * Returns the proj.
	 * @return ETSProj
	 */
	public ETSProj getProj() {
		return proj;
	}

	/**
	 * Returns the request.
	 * @return HttpServletRequest
	 */
	public HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * Returns the sLink.
	 * @return String
	 */
	public String getSLink() {
		return sLink;
	}

	/**
	 * Returns the topCatId.
	 * @return int
	 */
	public int getTopCatId() {
		return topCatId;
	}

	/**
	 * Sets the es.
	 * @param es The es to set
	 */
	public void setEs(EdgeAccessCntrl es) {
		this.es = es;
	}

	/**
	 * Sets the params.
	 * @param params The params to set
	 */
	public void setParams(Hashtable params) {
		this.params = params;
	}

	/**
	 * Sets the proj.
	 * @param proj The proj to set
	 */
	public void setProj(ETSProj proj) {
		this.proj = proj;
	}

	/**
	 * Sets the request.
	 * @param request The request to set
	 */
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * Sets the sLink.
	 * @param sLink The sLink to set
	 */
	public void setSLink(String sLink) {
		this.sLink = sLink;
	}

	/**
	 * Sets the topCatId.
	 * @param topCatId The topCatId to set
	 */
	public void setTopCatId(int topCatId) {
		this.topCatId = topCatId;
	}

	/**
	 * to attach files for a given issue/change
	 * returns an array of code/messg for success/failure
	 *
	 */

	String[] doAttach() {

		try {

			String action = request.getParameter("action");
			Vector mult = MimeMultipartParser.getBodyParts(request.getInputStream());
			int count = mult.size();
			String edge_problem_id = (String) params.get("edge_problem_id");
			String filenumber = (String) params.get("attach_file_no");

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
			attachment.setFileNewFlag("Y");
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

				System.out.println("writeDocument -- Input stream not found for file.");
				String[] err = { "0", "Input connection for file not found, please try again" };
				return err;

			}

			int inStreamAvail = -1;

			try {

				inStreamAvail = inStream.available();

				if (inStreamAvail > (4194304)) {

					System.out.println("writeDocument -- File over 4 MB limit.");
					String[] err = { "0", "The File is over the 4 MB limit.  Please use the DropBox for this file." };
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

			System.out.println("success = " + String.valueOf(success));

			success = ETSIssuesManager.createAttachment(attachment, null);

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
	 * To display attached files
	 *
	 */

	public String displayAttachedFiles() {

		StringBuffer sb = new StringBuffer();

		try {

			String edge_problem_id = AmtCommonUtils.getTrimStr((String) params.get("edge_problem_id"));
			String prob_class = AmtCommonUtils.getTrimStr((String) params.get("problem_class"));
			String prob_severity = AmtCommonUtils.getTrimStr((String) params.get("severity"));
			String prob_title = AmtCommonUtils.getTrimStr((String) params.get("title"));
			String prob_type = AmtCommonUtils.getTrimStr((String) params.get("problem_type"));
			String prob_desc = AmtCommonUtils.getTrimStr((String) params.get("description"));
			String actionType = AmtCommonUtils.getTrimStr((String) params.get("actionType"));
			String resolveAction = AmtCommonUtils.getTrimStr((String) params.get("resolveAction"));

			String cust_comm = "";
			String cq_trk_id = null;
			String issue_seq_no = "";

			if (actionType.equals("modifyIssue") || actionType.equals("modifyChange") || actionType.equals("resolveChange") || actionType.equals("resolveIssue") || actionType.equals("rejectIssue") || actionType.equals("closeIssue") || actionType.equals("rejectChange") || actionType.equals("closeChange") || actionType.equals("acceptChange")) {

				cust_comm = AmtCommonUtils.getTrimStr((String) params.get("cust_comm"));
				cq_trk_id = AmtCommonUtils.getTrimStr((String) params.get("cq_trk_id"));
				issue_seq_no = AmtCommonUtils.getTrimStr((String) params.get("issue_seq_no"));

			}
			//	Commented by v2sagar for PROBLEM_INFO_USR2
			int attachCount=0;
			//int attachCount = ETSIssuesManager.getAttachmentCount(edge_problem_id, null);

			java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MMM d, yyyy");
			java.text.SimpleDateFormat dateFormatOld = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss.000000");

			sb.append("<form name=\"attach_display_form\" method=\"post\" action=\"ETSProjectsServlet.wss\" > \n");
			sb.append("<input type=\"hidden\" name=\"problem_class\" value=\"" + prob_class + "\" />");
			sb.append("<input type=\"hidden\" name=\"severity\" value=\"" + prob_severity + "\" />");
			sb.append("<input type=\"hidden\" name=\"title\" value=\"" + prob_title + "\" />");
			sb.append("<input type=\"hidden\" name=\"problem_type\" value=\"" + prob_type + "\" />");
			sb.append("<input type=\"hidden\" name=\"description\" value=\"" + prob_desc + "\" />");
			sb.append("<input type=\"hidden\" name=\"proj\" value=\"" + proj.getProjectId() + "\" />");
			sb.append("<input type=\"hidden\" name=\"tc\" value=\"" + topCatId + "\" />");
			sb.append("<input type=\"hidden\" name=\"linkid\" value=\"" + sLink + "\" />");
			sb.append("<input type=\"hidden\" name=\"actionType\" value=\"" + actionType + "\" />");
			sb.append("<input type=\"hidden\" name=\"edge_problem_id\" value=\"" + edge_problem_id + "\" />");
			sb.append("<input type=\"hidden\" name=\"resolveAction\" value=\"" + resolveAction + "\" />");
			sb.append("<input type=\"hidden\" name=\"subactionType\" value=\"" + "" + "\" />");

			if (actionType.equals("modifyIssue") || actionType.equals("modifyChange") || actionType.equals("resolveChange") || actionType.equals("resolveIssue") || actionType.equals("rejectIssue") || actionType.equals("closeIssue") || actionType.equals("rejectChange") || actionType.equals("closeChange") || actionType.equals("acceptChange")) {
				sb.append("<input type=\"hidden\" name=\"cust_comm\" value=\"" + cust_comm + "\" /> \n");
				sb.append("<input type=\"hidden\" name=\"cq_trk_id\" value=\"" + cq_trk_id + "\" /> \n");
				sb.append("<input type=\"hidden\" name=\"issue_seq_no\" value=\"" + issue_seq_no + "\" /> \n");
			}

			/////////////new 4.2.1////////////////////////////////////////////////
			if (actionType.equals("submitIssue") || actionType.equals("modifyIssue") || actionType.equals("resolveIssue") || actionType.equals("rejectIssue") || actionType.equals("closeIssue")) {

				String prevFieldC1Val = AmtCommonUtils.getTrimStr((String) getParams().get(STDFIELDC1NAME));
				String prevFieldC2Val = AmtCommonUtils.getTrimStr((String) getParams().get(STDFIELDC2NAME));
				String prevFieldC3Val = AmtCommonUtils.getTrimStr((String) getParams().get(STDFIELDC3NAME));
				String prevFieldC4Val = AmtCommonUtils.getTrimStr((String) getParams().get(STDFIELDC4NAME));
				String prevFieldC5Val = AmtCommonUtils.getTrimStr((String) getParams().get(STDFIELDC5NAME));
				String prevFieldC6Val = AmtCommonUtils.getTrimStr((String) getParams().get(STDFIELDC6NAME));
				String prevFieldC7Val = AmtCommonUtils.getTrimStr((String) getParams().get(STDFIELDC7NAME));

				//SUBTYPES
				//prev vals always user reference name
				String prevSubTypeAVal = AmtCommonUtils.getTrimStr((String) getParams().get(STDSUBTYPE_A));
				String prevSubTypeBVal = AmtCommonUtils.getTrimStr((String) getParams().get(STDSUBTYPE_B));
				String prevSubTypeCVal = AmtCommonUtils.getTrimStr((String) getParams().get(STDSUBTYPE_C));
				String prevSubTypeDVal = AmtCommonUtils.getTrimStr((String) getParams().get(STDSUBTYPE_D));

				//ISSUE HIDDEN
				String issueSource = AmtCommonUtils.getTrimStr((String) getParams().get("issuesource"));
				String issueAccess = AmtCommonUtils.getTrimStr((String) getParams().get("issueaccess"));
				String issueType = AmtCommonUtils.getTrimStr((String) getParams().get("issuetype"));

				String notifylist = AmtCommonUtils.getTrimStr((String) getParams().get("notifylist"));
				String testcase = AmtCommonUtils.getTrimStr((String) getParams().get("testcase"));
				String chkissibmonly = AmtCommonUtils.getTrimStr((String) getParams().get("chkissibmonly"));

				//dyn vals
				sb.append("<input type=\"hidden\" name=\"" + STDSUBTYPE_A + "\" value=\"" + prevSubTypeAVal + "\" />");
				sb.append("<input type=\"hidden\" name=\"" + STDSUBTYPE_B + "\" value=\"" + prevSubTypeBVal + "\" />");
				sb.append("<input type=\"hidden\" name=\"" + STDSUBTYPE_C + "\" value=\"" + prevSubTypeCVal + "\" />");
				sb.append("<input type=\"hidden\" name=\"" + STDSUBTYPE_D + "\" value=\"" + prevSubTypeDVal + "\" />");

				//declare all static field names///
				sb.append("<input type=\"hidden\" name=\"" + STDFIELDC1NAME + "\" value=\"" + prevFieldC1Val + "\" /> \n");
				sb.append("<input type=\"hidden\" name=\"" + STDFIELDC2NAME + "\" value=\"" + prevFieldC2Val + "\" /> \n");
				sb.append("<input type=\"hidden\" name=\"" + STDFIELDC3NAME + "\" value=\"" + prevFieldC3Val + "\" /> \n");
				sb.append("<input type=\"hidden\" name=\"" + STDFIELDC4NAME + "\" value=\"" + prevFieldC4Val + "\" /> \n");
				sb.append("<input type=\"hidden\" name=\"" + STDFIELDC5NAME + "\" value=\"" + prevFieldC5Val + "\" /> \n");
				sb.append("<input type=\"hidden\" name=\"" + STDFIELDC6NAME + "\" value=\"" + prevFieldC6Val + "\" /> \n");
				sb.append("<input type=\"hidden\" name=\"" + STDFIELDC7NAME + "\" value=\"" + prevFieldC7Val + "\" /> \n");

				///issue hiddens
				sb.append("<input type=\"hidden\" name=\"notifylist\" value=\"" + notifylist + "\" /> \n");
				sb.append("<input type=\"hidden\" name=\"issuesource\" value=\"" + issueSource + "\" /> \n");
				sb.append("<input type=\"hidden\" name=\"issueaccess\" value=\"" + issueAccess + "\" /> \n");
				sb.append("<input type=\"hidden\" name=\"issuetype\" value=\"" + issueType + "\" /> \n");
				sb.append("<input type=\"hidden\" name=\"testcase\" value=\"" + testcase + "\" /> \n");
				sb.append("<input type=\"hidden\" name=\"chkissibmonly\" value=\"" + chkissibmonly + "\" /> \n");
				sb.append("<input type=\"hidden\" name=\"submitDBAction\" value=\"DBACTION\" /> \n");

				/////

			}
			//////////////file part/////////////////////////////////////////////////

			sb.append("<input type=\"hidden\" name=\"attach_file_no\" value=\"\" /> \n");
			sb.append("<input type=\"hidden\" name=\"display_action\" value=\"\" /> \n");

			sb.append("<a name=\"fileattach\"></a>\n");
			sb.append("	<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\"> \n");
			sb.append("	     <tr class=\"tblue\" height=\"18\">\n");
			sb.append("	       <td  height=\"18\" width=\"443\">\n");
			sb.append("	         <b><span style=\"color:#ffffff\">&nbsp;Attached file list</span></b>\n");
			sb.append("	       </td>\n");
			sb.append("	     </tr>\n");
			sb.append("	</table>\n");
			sb.append("	<br />\n");
			//			Commented by v2sagar for PROBLEM_INFO_USR2
			if (attachCount > 0) {
				sb.append("    <table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\"> \n");
				sb.append("      <tr> \n");
				sb.append("       <td  height=\"18\" width=\"443\"> \n");
				sb.append("        [<span class=\"small\">Please note that you can view the file attachments using browser, only if your browser supports them. If you have any difficulty in viewing attachments using browser, please download it and view in compatible application.]</span><br />  \n");
				sb.append("        </td> \n");
				sb.append("      </tr> \n");
				sb.append("    </table> \n");
				sb.append("	<br />\n");
			}

			sb.append("	<table summary=\"Table to display file attachments\" border=\"0\" cellpadding=\"1\" cellspacing=\"0\" width=\"443\"> \n");
			sb.append("	      <tr> \n");
			sb.append("	        <td bgcolor=\"#666666\" colspan=\"3\"> \n");
			sb.append("	          <table border=\"0\" cellpadding=\"1\" cellspacing=\"1\" width=\"100%\"> \n");
			sb.append("	            <tr height=\"18\"> \n");
			sb.append("	              <th id=\"atcol1\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
			sb.append("	              	    &nbsp;\n");
			sb.append("	              </th> \n");
			sb.append("	              <th id=\"atcol2\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
			sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;File name</span></b> \n");
			sb.append("	              </th> \n");
			sb.append("	              <th id=\"atcol3\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
			sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;File size</span></b> \n");
			sb.append("	              </th> \n");
			sb.append("	              <th id=\"atcol4\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
			sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;Description</span></b> \n");
			sb.append("	              </th> \n");
			sb.append("	              <th id=\"atcol5\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
			sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;Date</span></b> \n");
			sb.append("	              </th> \n");
			sb.append("	              <th id=\"atcol6\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
			sb.append("	              	    &nbsp;\n");
			sb.append("	              </th> \n");
			sb.append("	            </tr> \n");
//			Commented by v2sagar for PROBLEM_INFO_USR2
			if (attachCount > 0) {

				Vector aList = (Vector) ETSIssuesManager.getAttachedFiles(edge_problem_id, null);
				int count = 1;

				for (Iterator k = aList.iterator(); k.hasNext();) {
					ETSIssueAttach attached = (ETSIssueAttach) k.next();

					String fileName = attached.getFileName();
					long fileSize = attached.getFileSize();
					String fileDesc = attached.getFileDesc();
					int fileNo = attached.getFileNo();
					String fileDate = attached.getTimeStampString();

					sb.append("	            <tr> \n");
					sb.append("	              <td headers=\"atcol1\" bgcolor=\"#ffffff\" align=\"middle\" valign=\"middle\"> \n");
					sb.append("	                 " + count + " \n");
					sb.append("	              </td> \n");
					sb.append("	              <td headers=\"atcol2\" bgcolor=\"#ffffff\" align=\"left\"> \n");
					sb.append("	                 " + fileName + " \n");
					sb.append("	              </td> \n");
					sb.append("	              <td headers=\"atcol3\" bgcolor=\"#ffffff\" align=\"left\"> \n");

					
					StringBuffer sbDisplay = new StringBuffer();
					if (fileSize >= 1024) {
						sbDisplay.append(ETSStringUtils.displaySize(fileSize + ""));
						sbDisplay.append(" (");
						sbDisplay.append(ETSStringUtils.displayWithComma(fileSize + ""));
						sbDisplay.append(" bytes)");

					} else {
						sbDisplay.append(ETSStringUtils.displayWithComma(fileSize + ""));
						sbDisplay.append(" bytes");
					}

					sb.append("	                " + sbDisplay.toString() + " \n");
					sb.append("	              </td> \n");
					sb.append("	              <td headers=\"atcol4\" bgcolor=\"#ffffff\" align=\"left\"> \n");
					sb.append("	                 " + fileDesc + " \n");
					sb.append("	              </td> \n");
					sb.append("	              <td headers=\"atcol5\" bgcolor=\"#ffffff\" align=\"left\"> \n");

					String dateString = ETSStringUtils.formatDate(fileDate, "yyyy-MM-dd hh:mm:ss", "MMM d, yyyy");
					sb.append("	                " + dateString + " \n");
					sb.append("	              </td> \n");
					sb.append("	              <td headers=\"atcol6\" bgcolor=\"#ffffff\" align=\"left\"> \n");

					sb.append("	<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"> \n");
					sb.append("	  <tr> \n");
					sb.append("	    <td > \n");
					sb.append("	       <input type=\"image\" src=\"" + Defines.BUTTON_ROOT  + "cancel_rd.gif\" name=\"delete" + String.valueOf(fileNo).trim() + "\" value=\"attachDelete\" border=\"0\" width=\"21\" height=\"21\" alt=\"Delete\" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
					sb.append("	    </td> \n");
					sb.append("	    <td > \n");
					sb.append("Delete");
					sb.append("	    </td> \n");
					sb.append("	  </tr> \n");
					sb.append("	</table> \n");

					String viewIssueUrl = "ETSIssueAttachServlet.wss?action=view&fileNo=" + fileNo + "&edge_problem_id=" + edge_problem_id + " ";
					sb.append("	<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"> \n");
					sb.append("	  <tr> \n");
					sb.append("	    <td > \n");
					sb.append(
						"	      <a href=\""
							+ viewIssueUrl
							+ "\" target=\"new\" onclick=\"window.open('"
							+ viewIssueUrl
							+ "','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=500,height=500,left=387,top=207'); return false;\" onkeypress =\"window.open('"
							+ viewIssueUrl
							+ "','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=500,height=500,left=387,top=207'); return false;\">");
					sb.append("      <img src=\"" + Defines.BUTTON_ROOT  + "view_rd.gif\"  border=\"0\" alt=\"View attachment\" height=\"21\" width=\"21\" align=\"absbottom\" /></a> \n");
					sb.append("	    </td> \n");
					sb.append("	    <td > \n");
					sb.append(
						"	       &nbsp&nbsp;&nbsp;&nbsp;&nbsp;<a href=\""
							+ viewIssueUrl
							+ "\" target=\"new\" onclick=\"window.open('"
							+ viewIssueUrl
							+ "','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=500,height=500,left=387,top=207'); return false;\" onkeypress =\"window.open('"
							+ viewIssueUrl
							+ "','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=500,height=500,left=387,top=207'); return false;\" >View</a>\n");
					sb.append("	    </td> \n");
					sb.append("	    <td > \n");
					sb.append("	  </tr> \n");
					sb.append("	</table> \n");

					sb.append("	<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"> \n");
					sb.append("	  <tr> \n");
					sb.append("	    <td > \n");
					sb.append("	      <a href=\"ETSIssueAttachServlet.wss?action=download&fileNo=" + fileNo + "&edge_problem_id=" + edge_problem_id + "\"><img src=\"" + Defines.BUTTON_ROOT  + "download_now_rd.gif\"  border=\"0\" alt=\"Download Attachment\" height=\"21\" width=\"21\" align=\"absbottom\" /></a>\n");
					sb.append("	    </td> \n");
					sb.append("	    <td > \n");
					sb.append("	       &nbsp&nbsp;&nbsp;&nbsp;&nbsp;<a href=\"ETSIssueAttachServlet.wss?action=download&fileNo=" + fileNo + "&edge_problem_id=" + edge_problem_id + "\">Download</a>\n");
					sb.append("	    </td> \n");
					sb.append("	    <td > \n");
					sb.append("	  </tr> \n");
					sb.append("	</table> \n");

					sb.append("	              </td> \n");
					sb.append("	            </tr> \n");

					count++;
				}
				sb.append("	            </table> \n");
				sb.append("		   </td> \n");
				sb.append("		</tr> \n");
				count--;

			} else {

				sb.append("	<table border=\"0\" cellpadding=\"1\" cellspacing=\"1\" width=\"100%\"> \n");
				sb.append("	  <tr height=\"18\"> \n");
				sb.append("	   <td  bgcolor=\"#ffffff\" height=\"18\" align=\"left\" colspan=\"4\"> \n");
				sb.append("			Currently there are no file attachments.\n");
				sb.append("	   </td>\n");
				sb.append("	  </tr>\n");
				sb.append("	</table>\n");

			}
			sb.append("	 </table> \n");
			sb.append("	<br /> \n");

			String whatAction = "NEW";

			sb.append("<table summary=\"\" border=\"0\" cellpadding=\"1\" cellspacing=\"0\" width=\"443\">");
			sb.append("<tr><td colspan=\"2\"><img src=\"" + Defines.V11_IMAGE_ROOT + "rules/grey_rule.gif\" alt=\"\" width=\"443\" height=\"1\" /></td></tr>");
			sb.append("<tr><td>&nbsp;</td></tr>");
			sb.append("</table> ");

			sb.append("<table summary=\"\" border=\"0\" cellpadding=\"1\" cellspacing=\"0\" width=\"250\">");
			sb.append("<tr>");
			sb.append("<td   align=\"left\">");
			sb.append("<input type=\"image\" name=\"finalsubmit\" src=\"" + Defines.BUTTON_ROOT  + "submit.gif\" height=\"21\" width=\"120\" border=\"0\" alt=\"Submit\" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			sb.append("</td> ");
			sb.append("<td  align=\"left\">");
			sb.append("<a href=\"EtsIssFilterCntrlServlet.wss?istyp=iss&opn=10&proj=" + proj.getProjectId() + "&tc=" + topCatId + "&linkid=" + sLink + "&issues=cancel" + "\"" + ">" + "<img src=\"" + Defines.BUTTON_ROOT  + "cancel_rd.gif\" align=\"middle\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" /></a>");
			sb.append("<a href=\"EtsIssFilterCntrlServlet.wss?istyp=iss&opn=10&proj=" + proj.getProjectId() + "&tc=" + topCatId + "&linkid=" + sLink + "&issues=cancel" + "\"" + "> Cancel </a>");
			sb.append("</td> ");
			sb.append("</tr>");
			sb.append("</table> ");

			sb.append("</form>");

		} catch (Exception E) {
			E.printStackTrace();
		}

		return sb.toString();

	} //end of methd

	/**
	 * display file upload page
	 *
	 */

	public String displayAttachPage(String errMsg) {

		StringBuffer sbfile = new StringBuffer();

		String edge_problem_id = AmtCommonUtils.getTrimStr((String) params.get("edge_problem_id"));
		String prob_class = AmtCommonUtils.getTrimStr((String) params.get("problem_class"));
		String prob_severity = AmtCommonUtils.getTrimStr((String) params.get("severity"));
		String prob_title = AmtCommonUtils.getTrimStr((String) params.get("title"));
		String prob_type = AmtCommonUtils.getTrimStr((String) params.get("problem_type"));
		String prob_desc = AmtCommonUtils.getTrimStr((String) params.get("description"));
		String actionType = AmtCommonUtils.getTrimStr((String) params.get("actionType"));
		String resolveAction = AmtCommonUtils.getTrimStr((String) params.get("resolveAction"));

		String cust_comm = "";
		String cq_trk_id = "";
		String issue_seq_no = "";

		if (actionType.equals("modifyIssue") || actionType.equals("modifyChange") || actionType.equals("resolveChange") || actionType.equals("resolveIssue") || actionType.equals("rejectIssue") || actionType.equals("closeIssue") || actionType.equals("rejectChange") || actionType.equals("closeChange") || actionType.equals("acceptChange")) {
			cust_comm = AmtCommonUtils.getTrimStr((String) params.get("cust_comm"));
			cq_trk_id = AmtCommonUtils.getTrimStr((String) params.get("cq_trk_id"));
			issue_seq_no = AmtCommonUtils.getTrimStr((String) params.get("issue_seq_no"));
		}

		int attachCount = 0;
		/*try {
			attachCount = ETSIssuesManager.getAttachmentCount(edge_problem_id, null);
		} catch (Exception e) {

			SysLog.log(SysLog.ERR, this, e);
			e.printStackTrace();
		}*/

		sbfile.append("<form enctype=\"multipart/form-data\" method=\"post\" action=\"ETSProjectsServlet.wss?proj=" + proj.getProjectId() + "&tc=" + topCatId + "&linkid=" + sLink + "&actionType=" + actionType.trim() + "&subactionType=attachfinal&attach_file_no=" + String.valueOf(attachCount) + "&edge_problem_id=" + edge_problem_id + "\"" + ">");
		sbfile.append("<input type=\"hidden\" name=\"edge_problem_id\" value=\"" + edge_problem_id.trim() + "\"" + "/>");
		sbfile.append("<input type=\"hidden\" name=\"linkid\" value=" + sLink + " />");
		sbfile.append("<input type=\"hidden\" name=\"tc\" value=\"" + topCatId + "\" />");
		sbfile.append("<input type=\"hidden\" name=\"problem_class\" value=\"" + prob_class + "\"" + " />");
		sbfile.append("<input type=\"hidden\" name=\"severity\" value=\"" + prob_severity + "\"" + " />");
		sbfile.append("<input type=\"hidden\" name=\"title\" value=\"" + prob_title + "\"" + " />");
		sbfile.append("<input type=\"hidden\" name=\"problem_type\" value=\"" + prob_type + "\"" + " />");
		sbfile.append("<input type=\"hidden\" name=\"description\" value=\"" + prob_desc + "\"" + " />");
		sbfile.append("<input type=\"hidden\" name=\"resolveAction\" value=\"" + resolveAction + "\"" + " />");

		if (actionType.equals("modifyIssue") || actionType.equals("modifyChange") || actionType.equals("resolveChange") || actionType.equals("resolveIssue") || actionType.equals("rejectIssue") || actionType.equals("closeIssue") || actionType.equals("rejectChange") || actionType.equals("closeChange") || actionType.equals("acceptChange")) {
			sbfile.append("<input type=\"hidden\" name=\"cust_comm\" value=\"" + cust_comm + "\" /> \n");
			sbfile.append("<input type=\"hidden\" name=\"cq_trk_id\" value=\"" + cq_trk_id + "\" /> \n");
			sbfile.append("<input type=\"hidden\" name=\"issue_seq_no\" value=\"" + issue_seq_no + "\" /> \n");

		}

		/////////////new 4.2.1////////////////////////////////////////////////
		if (actionType.equals("submitIssue") || actionType.equals("modifyIssue") || actionType.equals("resolveIssue") || actionType.equals("rejectIssue") || actionType.equals("closeIssue")) {

			String prevFieldC1Val = AmtCommonUtils.getTrimStr((String) getParams().get(STDFIELDC1NAME));
			String prevFieldC2Val = AmtCommonUtils.getTrimStr((String) getParams().get(STDFIELDC2NAME));
			String prevFieldC3Val = AmtCommonUtils.getTrimStr((String) getParams().get(STDFIELDC3NAME));
			String prevFieldC4Val = AmtCommonUtils.getTrimStr((String) getParams().get(STDFIELDC4NAME));
			String prevFieldC5Val = AmtCommonUtils.getTrimStr((String) getParams().get(STDFIELDC5NAME));
			String prevFieldC6Val = AmtCommonUtils.getTrimStr((String) getParams().get(STDFIELDC6NAME));
			String prevFieldC7Val = AmtCommonUtils.getTrimStr((String) getParams().get(STDFIELDC7NAME));

			//SUBTYPES
			//prev vals always user reference name
			String prevSubTypeAVal = AmtCommonUtils.getTrimStr((String) getParams().get(STDSUBTYPE_A));
			String prevSubTypeBVal = AmtCommonUtils.getTrimStr((String) getParams().get(STDSUBTYPE_B));
			String prevSubTypeCVal = AmtCommonUtils.getTrimStr((String) getParams().get(STDSUBTYPE_C));
			String prevSubTypeDVal = AmtCommonUtils.getTrimStr((String) getParams().get(STDSUBTYPE_D));

			//ISSUE HIDDEN
			String issueSource = AmtCommonUtils.getTrimStr((String) getParams().get("issuesource"));
			String issueAccess = AmtCommonUtils.getTrimStr((String) getParams().get("issueaccess"));
			String issueType = AmtCommonUtils.getTrimStr((String) getParams().get("issuetype"));

			String notifylist = AmtCommonUtils.getTrimStr((String) getParams().get("notifylist"));
			String testcase = AmtCommonUtils.getTrimStr((String) getParams().get("testcase"));
			String chkissibmonly = AmtCommonUtils.getTrimStr((String) getParams().get("chkissibmonly"));

			//dyn vals
			sbfile.append("<input type=\"hidden\" name=\"" + STDSUBTYPE_A + "\" value=\"" + prevSubTypeAVal + "\" />");
			sbfile.append("<input type=\"hidden\" name=\"" + STDSUBTYPE_B + "\" value=\"" + prevSubTypeBVal + "\" />");
			sbfile.append("<input type=\"hidden\" name=\"" + STDSUBTYPE_C + "\" value=\"" + prevSubTypeCVal + "\" />");
			sbfile.append("<input type=\"hidden\" name=\"" + STDSUBTYPE_D + "\" value=\"" + prevSubTypeDVal + "\" />");

			//declare all static field names///
			sbfile.append("<input type=\"hidden\" name=\"" + STDFIELDC1NAME + "\" value=\"" + prevFieldC1Val + "\" /> \n");
			sbfile.append("<input type=\"hidden\" name=\"" + STDFIELDC2NAME + "\" value=\"" + prevFieldC2Val + "\" /> \n");
			sbfile.append("<input type=\"hidden\" name=\"" + STDFIELDC3NAME + "\" value=\"" + prevFieldC3Val + "\" /> \n");
			sbfile.append("<input type=\"hidden\" name=\"" + STDFIELDC4NAME + "\" value=\"" + prevFieldC4Val + "\" /> \n");
			sbfile.append("<input type=\"hidden\" name=\"" + STDFIELDC5NAME + "\" value=\"" + prevFieldC5Val + "\" /> \n");
			sbfile.append("<input type=\"hidden\" name=\"" + STDFIELDC6NAME + "\" value=\"" + prevFieldC6Val + "\" /> \n");
			sbfile.append("<input type=\"hidden\" name=\"" + STDFIELDC7NAME + "\" value=\"" + prevFieldC7Val + "\" /> \n");

			///issue hiddens
			sbfile.append("<input type=\"hidden\" name=\"notifylist\" value=\"" + notifylist + "\" /> \n");
			sbfile.append("<input type=\"hidden\" name=\"issuesource\" value=\"" + issueSource + "\" /> \n");
			sbfile.append("<input type=\"hidden\" name=\"issueaccess\" value=\"" + issueAccess + "\" /> \n");
			sbfile.append("<input type=\"hidden\" name=\"issuetype\" value=\"" + issueType + "\" /> \n");
			sbfile.append("<input type=\"hidden\" name=\"testcase\" value=\"" + testcase + "\" /> \n");
			sbfile.append("<input type=\"hidden\" name=\"chkissibmonly\" value=\"" + chkissibmonly + "\" /> \n");

			/////

		}

		sbfile.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\">");
		sbfile.append("<tr class=\"tdblue\">");
		sbfile.append("<td colspan=\"4\" height=\"18\" class=\"tblue\">&nbsp;&nbsp;Add attachments (optional)</td>");
		sbfile.append("</tr>");
		sbfile.append("</table>");

		sbfile.append("<br />");

		sbfile.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\">");
		sbfile.append("<tr ><td>");
		sbfile.append(" Please click on this link, to see the ");
		sbfile.append(
			" <a  href=\"jsp/ismgt/fileattach.jsp\" target=\"new\"   onclick=\"window.open('jsp/ismgt/fileattach.jsp','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\" onkeypress=\"window.open('jsp/ismgt/fileattach.jsp','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\">steps to attach files</a>");
		sbfile.append(" </td></tr>");
		sbfile.append("</table>");

		sbfile.append("<table summary=\"\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"443\">");
		sbfile.append("<tr>");
		sbfile.append(" <td  valign=\"top\" align=\"left\" >");
		sbfile.append(" <span class=\"small\">[The fields indicated with an asterisk <span class=\"ast\"><b>*</b></span> are required]</span>");
		sbfile.append(" </td>");
		sbfile.append("</tr>");
		sbfile.append("<tr>");
		sbfile.append(" </tr>");
		sbfile.append("</table>");

		///
		if (AmtCommonUtils.isResourceDefined(errMsg)) {
			sbfile.append("<table summary=\"\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"443\">");
			sbfile.append("<tr>");
			sbfile.append("<td  valign=\"top\" align=\"left\" >");
			sbfile.append("<span style=\"color:#ff3333\">" + errMsg + "</span>");
			sbfile.append("</td>");
			sbfile.append("</tr>");
			sbfile.append("<tr><td>&nbsp;</td></tr>");
			sbfile.append("</table>");
		}

		////
		sbfile.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\">");
		sbfile.append(" <tr valign=\"top\"> ");
		sbfile.append("<td  valign=\"top\" align=\"left\" width=\"130\">");
		sbfile.append(" <span class=\"ast\"><b>*</b></span><label for=\"uploadf\"> <b>Select file to attach</b>:</label>");
		sbfile.append("</td>");
		sbfile.append("<td  valign=\"top\" align=\"left\" width=\"313\">");
		sbfile.append("<input type=\"file\" id=\"uploadf\" name=\"upload_file\" size=\"40\" class=\"iform\" style=\"width:250px\" width=\"250px\"/> ");
		sbfile.append("</td>");
		sbfile.append("</tr> ");
		sbfile.append("</table> ");
		sbfile.append("<br /> ");
		sbfile.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\"> ");
		sbfile.append("<tr> ");
		sbfile.append("<td  valign=\"top\" align=\"left\" width=\"130\"><span class=\"ast\"><b>*</b></span>");
		sbfile.append("<label for=\"filed\"> <b>File description</b>:</label>");
		sbfile.append("</td>");
		sbfile.append("<td  valign=\"top\" align=\"left\" width=\"313\">");
		sbfile.append("<input align=\"left\" id=\"filed\" class=\"iform\" label=\"file_desc\" maxlength=\"50\" name=\"file_desc\" size=\"40\" src=\"\" type=\"text\" value=\"\" style=\"width:250px\" width=\"250px\" /> ");
		sbfile.append("</td> ");
		sbfile.append("</tr> ");
		sbfile.append("</table> ");
		sbfile.append("<br /> ");
		sbfile.append("<img src=\"" + Defines.V11_IMAGE_ROOT + "rules/black_rule.gif\" border=\"0\" height=\"1\" width=\"443\" alt=\"\" />");
		sbfile.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"15%\"> ");
		sbfile.append("<tr>");
		sbfile.append("<td  align=\"left\">");
		sbfile.append("<p /><input type=\"image\"  src=\"" + Defines.BUTTON_ROOT  + "arrow_rd.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"Attach file\" /></a>");
		sbfile.append("</td>");
		sbfile.append("<td  align=\"left\" nowrap=\"nowrap\" >");
		sbfile.append("&nbsp;<b>Attach file<b>");
		sbfile.append("</td>");
		sbfile.append("</tr>");
		sbfile.append("</table>");
		sbfile.append("<br /> ");
		sbfile.append("</form> ");

		return sbfile.toString();

	}

	/**
	 * prints common cancel button across the issue pages
	 *
	 */

	String printCommonCancel() {

		StringBuffer sbsub = new StringBuffer();

		sbsub.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
		sbsub.append("<tr><td  width=\"25\" valign=\"middle\" align=\"left\">");
		sbsub.append("<a href=\"EtsIssFilterCntrlServlet.wss?istyp=iss&opn=10&proj=" + getProj().getProjectId() + "&tc=" + getTopCatId() + "&linkid=" + getSLink() + "\">");
		sbsub.append("<img src=\"" + Defines.BUTTON_ROOT  + "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"cancel\" border=\"0\" /></a></td>");
		sbsub.append("<td  align=\"left\">");
		sbsub.append("<a href=\"EtsIssFilterCntrlServlet.wss?istyp=iss&opn=10&proj=" + getProj().getProjectId() + "&tc=" + getTopCatId() + "&linkid=" + getSLink() + "\">Cancel</a>");
		sbsub.append("</td></tr></table>");

		return sbsub.toString();

	}

	/***
	 *
	 * prints common back across issue pages
	 */

	String printCommonBack() {

		StringBuffer sbcls = new StringBuffer();

		sbcls.append("<!-- Row of buttons ===== -->");
		sbcls.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		sbcls.append("<tr><td><br /></td></tr>");
		sbcls.append("<tr>");
		sbcls.append("<td width=\"18\"><a href=\"EtsIssFilterCntrlServlet.wss?istyp=iss&opn=10&proj=" + getProj().getProjectId() + "&tc=" + getTopCatId() + "&linkid=" + getSLink() + "\"" + "><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
		sbcls.append("<td valign=\"middle\"><a href=\"EtsIssFilterCntrlServlet.wss?istyp=iss&opn=10&proj=" + getProj().getProjectId() + "&tc=" + getTopCatId() + "&linkid=" + getSLink() + "\"" + " class=\"fbox\">Return to Issues/changes</a></td>");
		sbcls.append("</tr><tr><td></td></tr></table>");

		return sbcls.toString();
	}

	/**
	 * takes the action on issue/change, based on subaction type and action value
	 *
	 */

	public abstract String processRequest() throws SQLException, Exception;

	/**
	 * common method across to display the initial or next pages in action(s)
	 * new,modify,resolve,reject,close,accept issue or change
	 *
	 */

	abstract String displayActionIssue(String errMsg, boolean firstTime) throws SQLException, Exception;

	/**
	 * common method across to display the initial or next pages in action(s)
	 * new,modify,resolve,reject,close,accept issue or change
	 *
	 */

	abstract String displayProblemClassification(String msg) throws SQLException, Exception;

	/**
	 * common method for  action in action(s)
	 * new,modify,resolve,reject,close,accept issue or change
	 *
	 */

	abstract String actionFinal() throws SQLException, Exception;

	/**
	 * common method DB actions
	 *
	 */

	abstract String commitIssue() throws SQLException, Exception;

	/**
	 * Returns the subActionType.
	 * @return String
	 */
	public String getSubActionType() {
		return subActionType;
	}

	/**
	 * Sets the subActionType.
	 * @param subActionType The subActionType to set
	 */
	public void setSubActionType(String subActionType) {
		this.subActionType = subActionType;
	}

	/**
	 * Returns the submitValue.
	 * @return String
	 */
	public String getSubmitValue() {
		return submitValue;
	}

	/**
	 * Sets the submitValue.
	 * @param submitValue The submitValue to set
	 */
	public void setSubmitValue(String submitValue) {
		this.submitValue = submitValue;
	}

	/**
	 * Returns the filenum.
	 * @return int
	 */
	public int getFilenum() {
		return filenum;
	}

	/**
	 * Sets the filenum.
	 * @param filenum The filenum to set
	 */
	public void setFilenum(int filenum) {
		this.filenum = filenum;
	}

	/**
	 * create Issue type drop down
	 *
	 */

	public String createIssueTypeDropDown(String prevIssueVal, boolean firstTime) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		EtsDropDownDAO dropDao = new EtsDropDownDAO();

		//get the drop list
		EtsDropDownDataBean dropModel = new EtsDropDownDataBean();

		//
		String qryIssueSource = AmtCommonUtils.getTrimStr((String) params.get("issuesource"));

		///get params
		dropModel.setProjectId(getProj().getProjectId());
		dropModel.setIssueClass(ETSISSUESUBTYPE);
		dropModel.setActiveFlag("Y");
		dropModel.setIssueSource(qryIssueSource);

		String userType = getEs().gDECAFTYPE;

		if (userType.equals("I")) {

			dropModel.setIssueAccess("IBM");
		} else {

			dropModel.setIssueAccess("ALL");

		}

		ArrayList dropList = dropDao.getIssueTypes(dropModel);

		///

		String issueType = "";
		String issueSource = ""; //CQ or Other
		String issueAccess = ""; //ISSUE access
		String dispVal = "";

		int size = 0;

		if (dropList != null && !dropList.isEmpty()) {

			size = dropList.size();

		}

		sb.append("<option></option>\n");

		for (int i = 0; i < size; i++) {

			EtsDropDownDataBean dropBean = (EtsDropDownDataBean) dropList.get(i);

			issueType = dropBean.getIssueType();
			issueSource = dropBean.getIssueSource();
			issueAccess = dropBean.getIssueAccess();

			if (!AmtCommonUtils.isResourceDefined(issueType)) {

				issueType = ETSNOVAL;

			}

			if (!AmtCommonUtils.isResourceDefined(issueSource)) {

				issueSource = ETSNOVAL;
			}

			if (!AmtCommonUtils.isResourceDefined(issueAccess)) {

				issueAccess = ETSNOVAL;
			}

			dispVal = issueType + "$" + issueSource + "$" + issueAccess;

			Global.println("issue Type=="+issueType);
			Global.println("disp Val=="+dispVal);
			Global.println("prev Issue Val=="+prevIssueVal);

			if (!firstTime) {

				if (dispVal.equals(prevIssueVal)) {

					sb.append("<option value=\"" + dispVal + "\" selected=\"selected\" >" + issueType + "</option>\n");

				} else {

					sb.append("<option value=\"" + dispVal + "\"  >" + issueType + "</option>\n");

				}

			} else {

				if (issueType.equals(prevIssueVal)) {

					sb.append("<option value=\"" + dispVal + "\" selected=\"selected\" >" + issueType + "</option>\n");

				} else {

					sb.append("<option value=\"" + dispVal + "\"  >" + issueType + "</option>\n");

				}

			}

		}

		return sb.toString();

	}

	/**
	 * create change type drop down
	 *
	 */

	public String createChangeTypeDropDown(String prevIssueVal) throws SQLException, Exception {

		//		Vector dropdown = new Vector();
		//Vector dropdown = ETSIssuesManager.getChangeTypes(getProj().getName(), null);

		//		dropdown.add("BGL Project");
		//		dropdown.addElement("Customer Connect Issues");
		//		dropdown.addElement("Hardware (General)");

		/*StringBuffer sb = new StringBuffer();

		int i = 0;
		int size = dropdown.size();

		if (size == 0) {
			dropdown.addElement("Default Value");
			size = 1;
		}
		for (i = 0; i < size; i++) {
			String value = ((String) dropdown.elementAt(i)).trim();
			sb.append("<option value=\"");
			sb.append(value);
			sb.append("\" ");
			if (issueType != null)
				if (issueType.endsWith(value))
					sb.append("selected ");
			sb.append(">");
			sb.append(value);
			sb.append("</option>\n");

		}*/

		StringBuffer sb = new StringBuffer();
		EtsDropDownDAO dropDao = new EtsDropDownDAO();

		//get the drop list
		EtsDropDownDataBean dropModel = new EtsDropDownDataBean();

		//
		String qryIssueSource = AmtCommonUtils.getTrimStr((String) params.get("issuesource"));

		///get params
		dropModel.setProjectId(getProj().getProjectId());
		dropModel.setIssueClass(ETSCHANGESUBTYPE);
		dropModel.setActiveFlag("Y");
		dropModel.setIssueSource(qryIssueSource);

		String userType = getEs().gDECAFTYPE;

		if (userType.equals("I")) {

			dropModel.setIssueAccess("IBM");
		} else {

			dropModel.setIssueAccess("ALL");

		}

		ArrayList dropList = dropDao.getChangeTypes(dropModel);

		///

		String issueType = "";
		String issueSource = ""; //CQ or Other
		String issueAccess = ""; //ISSUE access
		String dispVal = "";

		int size = 0;

		if (dropList != null && !dropList.isEmpty()) {

			size = dropList.size();

		}

		sb.append("<option></option>\n");

		for (int i = 0; i < size; i++) {

			EtsDropDownDataBean dropBean = (EtsDropDownDataBean) dropList.get(i);

			issueType = dropBean.getIssueType();
			/*issueSource = dropBean.getIssueSource();
			issueAccess = dropBean.getIssueAccess();

			if (!AmtCommonUtils.isResourceDefined(issueType)) {

				issueType = ETSNOVAL;

			}

			if (!AmtCommonUtils.isResourceDefined(issueSource)) {

				issueSource = ETSNOVAL;
			}

			if (!AmtCommonUtils.isResourceDefined(issueAccess)) {

				issueAccess = ETSNOVAL;
			}

			dispVal = issueType + "$" + issueSource + "$" + issueAccess;*/
			dispVal = issueType;

			if (dispVal.equals(prevIssueVal)) {

				sb.append("<option value=\"" + dispVal + "\" selected=\"selected\" >" + issueType + "</option>\n");

			} else {

				sb.append("<option value=\"" + dispVal + "\"  >" + issueType + "</option>\n");

			}

		}

		return sb.toString();

	}

	/**
	 * To display attached files in view issue
	 *
	 */

	public String ViewAttachedFiles() throws Exception {

		StringBuffer sb = new StringBuffer();
		String edge_problem_id = (String) getParams().get("edge_problem_id");
		String actionType = (String) getParams().get("actionType");
		int attachCount=0;
//		Commented by v2sagar for PROBLEM_INFO_USR2
		//int attachCount = ETSIssuesManager.getAttachmentCount(edge_problem_id, null);

		java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MMM d, yyyy");
		java.text.SimpleDateFormat dateFormatOld = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss.000000");

		sb.append("<a name=\"fileattach\"></a>\n");
		sb.append("	<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\"> \n");
		sb.append("	     <tr class=\"tblue\" height=\"18\">\n");
		sb.append("	       <td  height=\"18\" width=\"443\">\n");
		sb.append("	         <b><span style=\"color:#ffffff\">&nbsp;Attached file list</span></b>\n");
		sb.append("	       </td>\n");
		sb.append("	     </tr>\n");
		sb.append("	</table>\n");
		sb.append("	<br />\n");

		if (attachCount > 0) {
			sb.append("    <table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\"> \n");
			sb.append("      <tr> \n");
			sb.append("       <td  height=\"18\" width=\"443\"> \n");
			sb.append("        [<span class=\"small\">Please note that you can view the file attachments using browser, only if your browser supports them. If you have any difficulty in viewing attachments using browser, please download it and view in compatible application.]</span><br />  \n");
			sb.append("        </td> \n");
			sb.append("      </tr> \n");
			sb.append("    </table> \n");
			sb.append("	<br />\n");
		}

		sb.append("	<table summary=\"Table to display file attachments\" border=\"0\" cellpadding=\"1\" cellspacing=\"0\" width=\"443\"> \n");
		sb.append("	      <tr> \n");
		sb.append("	        <td bgcolor=\"#666666\" colspan=\"3\"> \n");
		sb.append("	          <table border=\"0\" cellpadding=\"1\" cellspacing=\"1\" width=\"100%\"> \n");
		sb.append("	            <tr height=\"18\"> \n");
		sb.append("	              <th id=\"atcol1\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
		sb.append("	              	    &nbsp;\n");
		sb.append("	              </th> \n");
		sb.append("	              <th id=\"atcol2\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
		sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;File name</span></b> \n");
		sb.append("	              </th> \n");
		sb.append("	              <th id=\"atcol3\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
		sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;File size</span></b> \n");
		sb.append("	              </th> \n");
		sb.append("	              <th id=\"atcol4\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
		sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;Description</span></b> \n");
		sb.append("	              </th> \n");
		sb.append("	              <th id=\"atcol5\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
		sb.append("	                 	<b><span style=\"color:#ffffff\">&nbsp;Date</span></b> \n");
		sb.append("	              </th> \n");
		sb.append("	              <th id=\"atcol6\" bgcolor=\"#999966\" height=\"18\" align=\"left\"> \n");
		sb.append("	              	    &nbsp;\n");
		sb.append("	              </th> \n");
		sb.append("	            </tr> \n");

		if (attachCount > 0) {

			Vector aList = (Vector) ETSIssuesManager.getAttachedFiles(edge_problem_id, null);
			int count = 1;

			for (Iterator k = aList.iterator(); k.hasNext();) {
				ETSIssueAttach attached = (ETSIssueAttach) k.next();

				String fileName = attached.getFileName();
				long fileSize = attached.getFileSize();
				String fileDesc = attached.getFileDesc();
				int fileNo = attached.getFileNo();
				String fileDate = attached.getTimeStampString();

				sb.append("	            <tr> \n");
				sb.append("	              <td headers=\"atcol1\" bgcolor=\"#ffffff\" align=\"middle\" valign=\"middle\"> \n");
				sb.append("	                 " + count + " \n");
				sb.append("	              </td> \n");
				sb.append("	              <td headers=\"atcol2\" bgcolor=\"#ffffff\" align=\"left\"> \n");
				sb.append("	                 " + fileName + " \n");
				sb.append("	              </td> \n");
				sb.append("	              <td headers=\"atcol3\" bgcolor=\"#ffffff\" align=\"left\"> \n");

				/* Display file size in proper size format */
				StringBuffer sbDisplay = new StringBuffer();
				if (fileSize >= 1024) {
					sbDisplay.append(ETSStringUtils.displaySize(fileSize + ""));
					sbDisplay.append(" (");
					sbDisplay.append(ETSStringUtils.displayWithComma(fileSize + ""));
					sbDisplay.append(" bytes)");

				} else {
					sbDisplay.append(ETSStringUtils.displayWithComma(fileSize + ""));
					sbDisplay.append(" bytes");
				}

				sb.append("	                " + sbDisplay.toString() + " \n");
				sb.append("	              </td> \n");
				sb.append("	              <td headers=\"atcol4\" bgcolor=\"#ffffff\" align=\"left\"> \n");
				sb.append("	                 " + fileDesc + " \n");
				sb.append("	              </td> \n");
				sb.append("	              <td headers=\"atcol5\" bgcolor=\"#ffffff\" align=\"left\"> \n");

				String dateString = ETSStringUtils.formatDate(fileDate, "yyyy-MM-dd hh:mm:ss", "MMM d, yyyy");
				sb.append("	                " + dateString + " \n");
				sb.append("	              </td> \n");
				sb.append("	              <td headers=\"atcol6\" bgcolor=\"#ffffff\" align=\"left\"> \n");

				String viewIssueUrl = "ETSIssueAttachServlet.wss?action=view&fileNo=" + fileNo + "&edge_problem_id=" + edge_problem_id + " ";
				sb.append("	<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"> \n");
				sb.append("	  <tr> \n");
				sb.append("	    <td > \n");
				sb.append(
					"	      <a href=\""
						+ viewIssueUrl
						+ "\" target=\"new\" onclick=\"window.open('"
						+ viewIssueUrl
						+ "','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=500,height=500,left=387,top=207'); return false;\" onkeypress =\"window.open('"
						+ viewIssueUrl
						+ "','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=500,height=500,left=387,top=207'); return false;\">");
				sb.append("      <img src=\"" + Defines.BUTTON_ROOT  + "view_rd.gif\"  border=\"0\" alt=\"View attachment\" height=\"21\" width=\"21\" align=\"absbottom\" /></a> \n");
				sb.append("	    </td> \n");
				sb.append("	    <td > \n");
				sb.append(
					"	       &nbsp&nbsp;&nbsp;&nbsp;&nbsp;<a href=\""
						+ viewIssueUrl
						+ "\" target=\"new\" onclick=\"window.open('"
						+ viewIssueUrl
						+ "','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=500,height=500,left=387,top=207'); return false;\" onkeypress =\"window.open('"
						+ viewIssueUrl
						+ "','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=500,height=500,left=387,top=207'); return false;\" >View</a>\n");
				sb.append("	    </td> \n");
				sb.append("	    <td > \n");
				sb.append("	  </tr> \n");
				sb.append("	</table> \n");

				sb.append("	<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"> \n");
				sb.append("	  <tr> \n");
				sb.append("	    <td > \n");
				sb.append("	      <a href=\"ETSIssueAttachServlet.wss?action=download&fileNo=" + fileNo + "&edge_problem_id=" + edge_problem_id + "\" ><img src=\"" + Defines.BUTTON_ROOT  + "download_now_rd.gif\"  border=\"0\" alt=\"Download Attachment\" height=\"21\" width=\"21\" align=\"absbottom\" /></a> \n");
				sb.append("	    </td> \n");
				sb.append("	    <td > \n");
				sb.append("	       &nbsp&nbsp;&nbsp;&nbsp;&nbsp;<a href=\"ETSIssueAttachServlet.wss?action=download&fileNo=" + fileNo + "&edge_problem_id=" + edge_problem_id + "\">Download</a>\n");
				sb.append("	    </td> \n");
				sb.append("	    <td > \n");
				sb.append("	  </tr> \n");
				sb.append("	</table> \n");

				sb.append("	              </td> \n");
				sb.append("	            </tr> \n");

				count++;
			}
			sb.append("	            </table> \n");
			sb.append("		   </td> \n");
			sb.append("		</tr> \n");
			count--;

		} else {

			sb.append("	<table border=\"0\" cellpadding=\"1\" cellspacing=\"1\" width=\"100%\"> \n");
			sb.append("	  <tr height=\"18\"> \n");
			sb.append("	   <td  bgcolor=\"#ffffff\" height=\"18\" align=\"left\" colspan=\"4\"> \n");
			sb.append("			Currently there are no file attachments.\n");
			sb.append("	   </td>\n");
			sb.append("	  </tr>\n");
			sb.append("	</table>\n");

		}
		sb.append("	 </table> \n");
		sb.append("	<br /> \n");

		return sb.toString();

	}

	/**
	 * Sets the response.
	 * @param response The response to set
	 */
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	/**
	 * Returns the etsIssObjKey.
	 * @return EtsIssObjectKey
	 */
	public EtsIssObjectKey getEtsIssObjKey() {
		return etsIssObjKey;
	}

	/**
	 * Returns the response.
	 * @return HttpServletResponse
	 */
	public HttpServletResponse getResponse() {
		return response;
	}

	/**
	 * to get issue type attributes
	 * token format >> 	ISSUETYPE $ ISSUE_SOURCE $ ISSUE_ACCESS
	 */

	public EtsDropDownDataBean getIssueTypeDropDownAttrib(String prob_type) throws Exception {

		ArrayList tokList = new ArrayList();

		if (AmtCommonUtils.isResourceDefined(prob_type)) {

			tokList = EtsIssFilterUtils.getArrayListFromStringTok(prob_type, "$");
		}

		EtsDropDownDataBean dropBean = new EtsDropDownDataBean();

		if (EtsIssFilterUtils.isArrayListDefnd(tokList)) {

			//set issue val//
			dropBean.setIssueType((String) tokList.get(0));

			//set issue source
			dropBean.setIssueSource((String) tokList.get(1));

			//set issue access
			dropBean.setIssueAccess((String) tokList.get(2));

			//issue class
			dropBean.setIssueClass(getIssueClass());

			//project id
			dropBean.setProjectId(getEtsIssObjKey().getProj().getProjectId());

		}

		return dropBean;

	}

	/**
	 * get Issue class
	 */

	public String getIssueClass() {

		String actionType = AmtCommonUtils.getTrimStr((String) (getEtsIssObjKey().getParams()).get("actionType"));

		if (actionType.equals("submitIssue") || actionType.equals("modifyIssue")) {

			return ETSISSUESUBTYPE;

		}

		if (actionType.equals("submitChange") || actionType.equals("modifyChange")) {

			return ETSCHANGESUBTYPE;

		}

		return ETSISSUESUBTYPE;

	}

	/**
	 * to get issue val for a given pattern
	 * token format >> 	DATA_ID $ ISSUE_TYPE $ ISSUE_SOURCE $ ISSUE_ACCESS $ SUBTYPE_A
	 */

	public String getDelimitIssueVal(String prob_type) throws Exception {

		EtsDropDownDataBean dropBean = getIssueTypeDropDownAttrib(prob_type);

		return dropBean.getIssueType();

	}

	/**
	  * To display the window popup for log commentary
	  *
	  */

	String displayLogPopup(String logdesc, String edge_problem_id) {

		StringBuffer sb = new StringBuffer();

		sb.append("<a href=\"#\" onclick=\"window.open('EtsIssLogActionServlet.wss?opn=110&edge_problem_id=" + java.net.URLEncoder.encode(edge_problem_id) + "',");
		sb.append(" 'history','toolbar=no,scrollbars=yes,location=0,statusbar=0,menubar=0,resizable=yes,left=60,top=100,width=400,height=450')\"  ");
		sb.append(" onkeypress=\"window.open('EtsIssLogActionServlet.wss?opn=110&edge_problem_id=" + java.net.URLEncoder.encode(edge_problem_id) + "',");
		sb.append(" 'history','toolbar=no,scrollbars=yes,location=0,statusbar=0,menubar=0,resizable=yes,left=60,top=100,width=400,height=450')\" >" + logdesc + "</a> ");

		return sb.toString();
	}

	/***
	 * to determine if the log commentary is defined
	 */
	boolean isLogCommentaryDefnd() throws SQLException, Exception {

		String edge_problem_id = AmtCommonUtils.getTrimStr((String) getParams().get("edge_problem_id"));

		EtsIssLogsDataPrepBean etsLogDataPrep = new EtsIssLogsDataPrepBean(edge_problem_id);

		EtsIssLogActionDetails etsLogDet = etsLogDataPrep.createIssueLogs();

		if (AmtCommonUtils.isResourceDefined(etsLogDet.getIssueCommentsLog())) {

			return true;

		}

		return false;

	}

	/**
	 * to get issue type label
	 */
	String getDefaultIssueTypeLabel() {

		HashMap defLabelMap = etsIssObjKey.getFormLabelMap();

		String issueTypeLabel=DEFUALTSTDISSUETYPE;

		if(EtsIssFilterUtils.isHashMapDefnd(defLabelMap)) {

		issueTypeLabel = (String) defLabelMap.get(STDISSUETYPE);

		if(!AmtCommonUtils.isResourceDefined(issueTypeLabel)) {

			issueTypeLabel=DEFUALTSTDISSUETYPE;
		}

		}

		return issueTypeLabel;
	}

	/**
	 * to get issue type label
	 */
	String getDefaultSubTypeStr(String stdSubType,String defaultStdSubType) {

		HashMap defLabelMap = etsIssObjKey.getFormLabelMap();

		String issueTypeLabel=defaultStdSubType;

		if(EtsIssFilterUtils.isHashMapDefnd(defLabelMap)) {

		issueTypeLabel = (String) defLabelMap.get(stdSubType);

		if(!AmtCommonUtils.isResourceDefined(issueTypeLabel)) {

			issueTypeLabel=defaultStdSubType;
		}

		}

		return issueTypeLabel;
	}





} //end of class

