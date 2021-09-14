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

package oem.edge.ets.fe.documents.taglib;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.documents.BaseDocumentForm;
import oem.edge.ets.fe.documents.DocumentsHelper;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.StringUtil;

/**
 * @author v2srikau
 */
public class DocActionButtons extends BodyTagSupport {

	private boolean m_bHasSubCats;

	private boolean m_bOwnsCats;

	/**
	 * @return int
	 * @throws javax.servlet.jsp.JspException
	 */
	public int doEndTag() throws JspException {
		return 0;
	}

	/**
	 * @return int
	 * @throws javax.servlet.jsp.JspException
	 */
	public int doStartTag() throws JspException {
		JspWriter pdWriter = pageContext.getOut();
		if (pdWriter == null) {
			return SKIP_BODY;
		}
		try {
			ServletRequest pdRequest = pageContext.getRequest();

			printCatActionButtons(
				getOwnsCat(),
				getHasSubCats(),
				pdRequest,
				pdWriter);

			pdWriter.flush();
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
		return SKIP_BODY;
	}

	/**
	 * @return
	 */
	public boolean getHasSubCats() {
		return m_bHasSubCats;
	}

	/**
	 * @return
	 */
	public boolean getOwnsCat() {
		return m_bOwnsCats;
	}

	/**
	 * @param string
	 */
	public void setHasSubCats(boolean bHasSubCats) {
		m_bHasSubCats = bHasSubCats;
	}

	/**
	 * @param string
	 */
	public void setOwnsCat(boolean bOwnsCat) {
		m_bOwnsCats = bOwnsCat;
	}

	/**
	 * @param owns
	 * @param hasSubCats
	 * @param pdRequest
	 * @param pdWriter
	 */
	public void printCatActionButtons(
		boolean owns,
		boolean hasSubCats,
		ServletRequest pdRequest,
		JspWriter pdWriter)
		throws IOException {
		StringBuffer buf = new StringBuffer();

		BaseDocumentForm udForm =
			(BaseDocumentForm) pageContext.getRequest().getAttribute(
				DocConstants.DOC_FORM);

		ETSCat udCat = udForm.getCategory();

		EdgeAccessCntrl udEdgeAccess =
			(EdgeAccessCntrl) pdRequest.getAttribute(
				DocConstants.REQ_ATTR_EDGEACCESS);

		String strUserRole =
			(String) pdRequest.getAttribute(DocConstants.REQ_ATTR_USERROLE);
		String strProjectId = udForm.getProj();
		String strTopCatID = udForm.getTc();
		String CurrentCatId = udForm.getCc();
		String sLink = udForm.getLinkid();
		if (StringUtil.isNullorEmpty(sLink)) {
			sLink = Defines.LINKID;
		}

		boolean isSuperAdmin = strUserRole.equals(Defines.ETS_ADMIN);

		boolean print_flag = false;

		buf.append("<br />");

		buf.append(
			"<table cellpadding=\"0\" cellspacing=\"1\" width=\"600\" "
				+ "border=\"0\">");
		buf.append(
			"<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"4\" "
				+ "width=\"1\" alt=\"\" /></td></tr>");
		buf.append("</table>");

		// This check takes care of Meetings Document
		if (DocConstants.IND_NO.equals(udCat.getDisplayFlag())
			|| !StringUtil.isNullorEmpty(pdRequest.getParameter("pmoCat"))) {
			pdWriter.println(buf.toString());
			return;
		}
		if ((!(strUserRole.equals(Defines.ETS_EXECUTIVE)
			|| strUserRole.equals(Defines.WORKSPACE_VISITOR)))
			|| isSuperAdmin) {
			buf.append(
				"<table  cellpadding=\"0\" cellspacing=\"1\" width=\"600\" border=\"0\">");
			buf.append("<tr>");

			//add document
			if (udCat.getCatType() != 0) {
				buf.append("<td>");
				buf.append(
					"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
				buf.append(
					"<td valign=\"top\" algin=\"right\">"
						+ "<a href=\"displayAddDocument.wss?docAction=adddoc&proj="
						+ strProjectId
						+ "&tc="
						+ strTopCatID
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ sLink
						+ "\" class=\"fbox\">"
						+ "<img src=\"//www.ibm.com/i/v14/icons/fw_c.gif\" "
						+ "border=\"0\"  height=\"16\" width=\"16\" "
						+ "alt=\"add new document\" /></a>&nbsp;</td>");
				buf.append(
					"<td valign=\"top\" align\"left\"><a href=\"displayAddDocument.wss?docAction=adddoc&proj="
						+ strProjectId
						+ "&tc="
						+ strTopCatID
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ sLink
						+ "\" class=\"fbox\">Add new document</a></td>");
				buf.append("</tr></table>");
				buf.append("</td>");
			    /*
				if (DocumentsHelper.isAICProject(pdRequest)) {
					buf.append("<td>");
					buf.append(
						"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
					buf.append(
						"<td valign=\"top\" algin=\"right\">"
							+ "<a href=\"displayAddDocument.wss?docAction=addaicdoc&proj="
							+ strProjectId
							+ "&tc="
							+ strTopCatID
							+ "&cc="
							+ CurrentCatId
							+ "&linkid="
							+ sLink
							+ "\" class=\"fbox\">"
							+ "<img src=\"//www.ibm.com/i/v14/icons/fw_c.gif\" "
							+ "border=\"0\"  height=\"16\" width=\"16\" "
							+ "alt=\"add new document\" /></a>&nbsp;</td>");
					buf.append(
						"<td valign=\"top\" align\"left\"><a href=\"displayAddDocument.wss?docAction=addaicdoc&proj="
							+ strProjectId
							+ "&tc="
							+ strTopCatID
							+ "&cc="
							+ CurrentCatId
							+ "&linkid="
							+ sLink
							+ "\" class=\"fbox\">Add table</a></td>");
					buf.append("</tr></table>");
					buf.append("</td>");
				}
				*/
			}

			//add category
			buf.append("<td>");
			buf.append(
				"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
			buf.append(
				"<td valign=\"top\" align=\"right\">"
					+ "<a href=\"displayAddCategory.wss?&proj="
					+ strProjectId
					+ "&tc="
					+ strTopCatID
					+ "&cc="
					+ CurrentCatId
					+ "&linkid="
					+ sLink
					+ "\" class=\"fbox\">"
					+ "<img src=\"//www.ibm.com/i/v14/icons/fw.gif\" "
					+ "border=\"0\"  height=\"16\" width=\"16\" "
					+ "alt=\"add new subfolder\" /></a>&nbsp;</td>");
			buf.append(
				"<td valign=\"top\" align=\"left\">"
					+ "<a href=\"displayAddCategory.wss?&proj="
					+ strProjectId
					+ "&tc="
					+ strTopCatID
					+ "&cc="
					+ CurrentCatId
					+ "&linkid="
					+ sLink
					+ "\" class=\"fbox\">Add new subfolder</a></td>");
			buf.append("</tr></table>");
			buf.append("</td>");

			//rename category
			//if cat_type>=2 and (own catgory || priv == update)
			if (hasSubCats
				&& (owns
					|| DocumentsHelper.hasPriviliges(
						udEdgeAccess.gIR_USERN,
						strProjectId,
						Defines.UPDATE)
					|| isSuperAdmin)) {
				buf.append("<td>");

				buf.append("<table  cellpadding=\"0\" cellspacing=\"0\">");
				buf.append("<tr>");
				buf.append(
					"<td valign=\"top\" align=\"right\">"
						+ "<a href=\"displayUpdateCategory.wss?proj="
						+ strProjectId
						+ "&tc="
						+ strTopCatID
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ sLink
						+ "\" class=\"fbox\">"
						+ "<img src=\"//www.ibm.com/i/v14/icons/fw.gif\" "
						+ "border=\"0\"  height=\"16\" width=\"16\" "
						+ "alt=\"rename subfolder\" /></a>&nbsp;</td>");
				buf.append(
					"<td valign=\"top\" align=\"left\">"
						+ "<a href=\"displayUpdateCategory.wss?proj="
						+ strProjectId
						+ "&tc="
						+ strTopCatID
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ sLink
						+ "\" class=\"fbox\">Update subfolder</a>");
				buf.append("</td></tr></table>");

				buf.append("</td>");
			}

			//move category
			if (hasSubCats
				&& (owns
					|| DocumentsHelper.hasPriviliges(
						udEdgeAccess.gIR_USERN,
						strProjectId,
						Defines.UPDATE)
					|| isSuperAdmin)) {
				buf.append("<td>");
				buf.append("<table  cellpadding=\"0\" cellspacing=\"0\">");
				buf.append("<tr>");
				buf.append(
					"<td valign=\"top\" align=\"right\">"
						+ "<a href=\"displayMoveCategory.wss?proj="
						+ strProjectId
						+ "&tc="
						+ strTopCatID
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ sLink
						+ "\" class=\"fbox\">"
						+ "<img src=\"//www.ibm.com/i/v14/icons/fw.gif\" "
						+ "border=\"0\"  height=\"16\" width=\"16\" "
						+ "alt=\"move a folder\" /></a>&nbsp;</td>");
				buf.append(
					"<td valign=\"top\" align=\"left\">"
						+ "<a href=\"displayMoveCategory.wss?proj="
						+ strProjectId
						+ "&tc="
						+ strTopCatID
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ sLink
						+ "\" class=\"fbox\">Move subfolder</a>");
				buf.append("</td></tr></table>");
				buf.append("</td>");
			}

			//delete category
			if (hasSubCats
				&& (owns
					|| DocumentsHelper.hasPriviliges(
						udEdgeAccess.gIR_USERN,
						strProjectId,
						Defines.DELETE)
					|| isSuperAdmin)) {
				buf.append("<td>");
				buf.append("<table  cellpadding=\"0\" cellspacing=\"0\">");
				buf.append("<tr>");
				buf.append(
					"<td valign=\"top\" align=\"right\">"
						+ "<a href=\"displayDeleteCategory.wss?proj="
						+ strProjectId
						+ "&tc="
						+ strTopCatID
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ sLink
						+ "\" class=\"fbox\">"
						+ "<img src=\"//www.ibm.com/i/v14/icons/fw.gif\" "
						+ "border=\"0\"  height=\"16\" width=\"16\" "
						+ "alt=\"delete a folder\" /></a>&nbsp;</td>");
				buf.append(
					"<td valign=\"top\" align=\"left\">"
						+ "<a href=\"displayDeleteCategory.wss?proj="
						+ strProjectId
						+ "&tc="
						+ strTopCatID
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ sLink
						+ "\" class=\"fbox\">Delete subfolder</a>");
				buf.append("</td></tr></table>");
				buf.append("</td>");
			}
/*
			if (udCat.getCatType() == 0
				&& (DocumentsHelper
					.hasPriviliges(
						udEdgeAccess.gIR_USERN,
						strProjectId,
						Defines.ADMIN)
					|| isSuperAdmin)) {
				buf.append("<td>");
				buf.append("<table  cellpadding=\"0\" cellspacing=\"0\">");
				buf.append("<tr>");
				buf.append(
					"<td valign=\"top\" align=\"right\">"
						+ "<a href=\"displayAccessHistory.wss?proj="
						+ strProjectId
						+ "&tc="
						+ strTopCatID
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ sLink
						+ "\" class=\"fbox\">"
						+ "<img src=\"//www.ibm.com/i/v14/icons/fw.gif\" "
						+ "border=\"0\"  height=\"16\" width=\"16\" "
						+ "alt=\"Document access history\" /></a>&nbsp;</td>");
				buf.append(
					"<td valign=\"top\" align=\"left\">"
						+ "<a href=\"displayAccessHistory.wss?proj="
						+ strProjectId
						+ "&tc="
						+ strTopCatID
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ sLink
						+ "\" class=\"fbox\">Document access history</a>");
				buf.append("</td></tr></table>");
				buf.append("</td>");
			}
*/
			if (udCat.getCatType() == 0) {
				buf.append("<td>");
				buf.append("<table  cellpadding=\"0\" cellspacing=\"0\">");
				buf.append("<tr>");
				buf.append(
					"<td valign=\"top\" align=\"right\">"
						+ "<a href=\"displayUserHistory.wss?proj="
						+ strProjectId
						+ "&tc="
						+ strTopCatID
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ sLink
						+ "\" class=\"fbox\">"
						+ "<img src=\"//www.ibm.com/i/v14/icons/fw.gif\" "
						+ "border=\"0\"  height=\"16\" width=\"16\" "
						+ "alt=\"Access history\" /></a>&nbsp;</td>");
				buf.append(
					"<td valign=\"top\" align=\"left\">"
						+ "<a href=\"displayUserHistory.wss?proj="
						+ strProjectId
						+ "&tc="
						+ strTopCatID
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ sLink
						+ "\" class=\"fbox\">Access history</a>");
				buf.append("</td></tr></table>");
				buf.append("</td>");
			}

			if (udCat.getCatType() == 0) {
				buf.append("<td>");
				buf.append("<table  cellpadding=\"0\" cellspacing=\"0\">");
				buf.append("<tr>");
				buf.append(
					"<td valign=\"top\" align=\"right\">"
						+ "<a href=\"displayDocumentList.wss?docAction=showalldocs"
						+ "&proj="
						+ strProjectId
						+ "&tc="
						+ strTopCatID
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ sLink
						+ "\" class=\"fbox\">"
						+ "<img src=\"//www.ibm.com/i/v14/icons/fw.gif\" "
						+ "border=\"0\"  height=\"16\" width=\"16\" "
						+ "alt=\"List all documents\" /></a>&nbsp;</td>");
				buf.append(
					"<td valign=\"top\" align=\"left\">"
						+ "<a href=\"displayDocumentList.wss?docAction=showalldocs"
						+ "&proj="
						+ strProjectId
						+ "&tc="
						+ strTopCatID
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ sLink
						+ "\" class=\"fbox\">List all documents</a>");
				buf.append("</td></tr></table>");
				buf.append("</td>");
			}

			if (udCat.getCatType() == 0) {
				buf.append("<td>");
				buf.append("<table  cellpadding=\"0\" cellspacing=\"0\">");
				buf.append("<tr>");
				buf.append(
					"<td valign=\"top\" align=\"right\">"
						+ "<a href=\"displayFolderTree.wss?proj="
						+ strProjectId
						+ "&tc="
						+ strTopCatID
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ sLink
						+ "\" class=\"fbox\">"
						+ "<img src=\"//www.ibm.com/i/v14/icons/fw.gif\" "
						+ "border=\"0\"  height=\"16\" width=\"16\" "
						+ "alt=\"List folders/subfolders\" /></a>&nbsp;</td>");
				buf.append(
					"<td valign=\"top\" align=\"left\">"
						+ "<a href=\"displayFolderTree.wss?proj="
						+ strProjectId
						+ "&tc="
						+ strTopCatID
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ sLink
						+ "\" class=\"fbox\">List folders/subfolders</a>");
				buf.append("</td></tr></table>");
				buf.append("</td>");
			}

			buf.append("</tr>");
			buf.append("</table>");

			//to add doc line for top cat
			if (udCat.getCatType() == 0) {
				buf.append("<table cellpadding=\"0\" cellspacing=\"0\">");
				buf.append(
					"<tr><td><img src=\"//www.ibm.com/i/c.gif\" "
						+ "height=\"4\" width=\"1\" alt=\"\" /></td></tr>");

				buf.append(
					"<tr><td valign=\"top\" "
						+ "align=\"left\" class=\"small\">");
				buf.append(
					"*To add a document, "
						+ "please select a folder to place it in.");
				buf.append("</td></tr>");
				buf.append("</table>");
			}

		}
		else if (strUserRole.equals(Defines.ETS_EXECUTIVE)) {
			buf.append(
				"<table  cellpadding=\"0\" cellspacing=\"1\" width=\"600\" border=\"0\">");
			buf.append("<tr>");
			if (udCat.getCatType() == 0) {
				buf.append("<td>");
				buf.append("<table  cellpadding=\"0\" cellspacing=\"0\">");
				buf.append("<tr>");
				buf.append(
					"<td valign=\"top\" align=\"right\">"
						+ "<a href=\"displayDocumentList.wss?docAction=showalldocs"
						+ "&proj="
						+ strProjectId
						+ "&tc="
						+ strTopCatID
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ sLink
						+ "\" class=\"fbox\">"
						+ "<img src=\"//www.ibm.com/i/v14/icons/fw.gif\" "
						+ "border=\"0\"  height=\"16\" width=\"16\" "
						+ "alt=\"delete a folder\" /></a>&nbsp;</td>");
				buf.append(
					"<td valign=\"top\" align=\"left\">"
						+ "<a href=\"displayDocumentList.wss?docAction=showalldocs"
						+ "&proj="
						+ strProjectId
						+ "&tc="
						+ strTopCatID
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ sLink
						+ "\" class=\"fbox\">List all documents</a>");
				buf.append("</td></tr></table>");
				buf.append("</td>");
			}
			buf.append("</tr>");
			buf.append("</table>");
		}

		pdWriter.println(buf.toString());
	}
}
