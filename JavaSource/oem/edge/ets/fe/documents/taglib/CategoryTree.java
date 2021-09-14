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
import java.util.Vector;

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
public class CategoryTree extends BodyTagSupport {

	private String m_strMoveCategory;

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
			BaseDocumentForm udForm =
				DocumentsHelper.getDocumentForm(pdRequest);
			Vector vtCats = udForm.getCategories();
			StringBuffer strBuffer = new StringBuffer(StringUtil.EMPTY_STRING);
			String strProjectId =
				pdRequest.getParameter(DocConstants.PARAM_PROJECTID);
			String strTopCatID =
				pdRequest.getParameter(DocConstants.PARAM_TOPCATEGORY);
			String sLink = pdRequest.getParameter(DocConstants.PARAM_LINKID);
			if (StringUtil.isNullorEmpty(sLink)) {
				sLink = Defines.LINKID;
			}

			EdgeAccessCntrl udEdgeAccess =
				(EdgeAccessCntrl) pdRequest.getAttribute(
					DocConstants.REQ_ATTR_EDGEACCESS);

			Vector vtNoShows = new Vector();
			if (!isMoveCategory()) {
				vtNoShows.add(new Integer(udForm.getParentCategory().getId()));
				if (!isMoveCategory()) {
					vtNoShows.add(new Integer(udForm.getTc()));
				}
			}
			printRecursive(
				strBuffer,
				vtCats,
				vtNoShows,
				0,
				udForm.getParentCategory().getId(),
				isMoveCategory() ? udForm.getCategory().getId() : 0,
				1,
				udEdgeAccess);

			pdWriter.write(strBuffer.toString());
			pdWriter.flush();
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
		return SKIP_BODY;
	}

	/**
	 * @param strBuffer
	 * @param vDetails
	 * @param noshows
	 * @param ID
	 * @param oldParentId
	 * @param movecatid
	 * @param iLevel
	 * @param udEdgeAccess
	 */
	private void printRecursive(
		StringBuffer strBuffer,
		Vector vDetails,
		Vector noshows,
		int ID,
		int oldParentId,
		int movecatid,
		int iLevel,
		EdgeAccessCntrl udEdgeAccess) {
		int iMaxCols = 15;
		int iBlankColSpan = 0;
		int iFillColSpan = 0;

		iBlankColSpan = (iLevel - 1) * 25;

		for (int iCounter = 0; iCounter < vDetails.size(); iCounter++) {
			ETSCat udCat = (ETSCat) vDetails.elementAt(iCounter);
			boolean bShowRadio = true;

			if (udCat.isIbmOnlyOrConf()
				&& !(udEdgeAccess.gDECAFTYPE.trim().equals("I"))) {
				// DO NOTHING
			} else {
				if (udCat.getParentId() == ID) {
					strBuffer.append(
						"<tr><td class=\"small\" align=\"left\" "
							+ "valign=\"middle\" "
							+ "height=\"1\" >\n");
					strBuffer.append(
						"<img src=\""
							+ Defines.V11_IMAGE_ROOT
							+ "gray_dotted_line.gif\" alt=\"\" width=\"600\" "
							+ "height=\"1\" />\n");
					strBuffer.append("</td></tr>\n");

					strBuffer.append("<tr>\n");
					strBuffer.append(
						"<td class=\"small\" align=\"left\" valign=\"top\">\n");

					String strCatName = udCat.getName();
					if (movecatid == udCat.getId()) {
						noshows.addElement(new Integer(udCat.getId()));
						strCatName = "<b>" + strCatName + "</b>";
						bShowRadio = false;
					} else {
						if (isMoveCategory()
							&& noshows.contains(
								new Integer(udCat.getParentId()))) {
							noshows.addElement(new Integer(udCat.getId()));
							bShowRadio = false;
						} else if (
							!isMoveCategory()
								&& noshows.contains(new Integer(udCat.getId()))) {
							bShowRadio = false;
						}
					}

					if (bShowRadio) {
						if (udCat.getId() == oldParentId) {
							strBuffer.append(
								"<input id=\"move2cat\" type=\"radio\" "
									+ "name=\"movetocat\" value=\""
									+ udCat.getId()
									+ "\" checked=\"checked\" />\n");
						} else {
							strBuffer.append(
								"<input id=\"move2cat\" type=\"radio\" "
									+ "name=\"movetocat\" value=\""
									+ udCat.getId()
									+ "\" />\n");
						}
					} else {
						strBuffer.append(
							"<img alt=\"\" src=\""
								+ Defines.TOP_IMAGE_ROOT
								+ "c.gif\" width=\"20\" height=\"20\" />\n");
					}

					strBuffer.append(
						"<img alt=\"\" src=\""
							+ Defines.TOP_IMAGE_ROOT
							+ "c.gif\" width=\""
							+ iBlankColSpan
							+ "\" height=\"1\" />\n");
					strBuffer.append(
						"<img src=\""
							+ Defines.SERVLET_PATH
							+ "ETSImageServlet.wss?proj=ETS_CAT_IMG&mod=0\" "
							+ "width=\"13\" height=\"9\" alt=\"folder\" />\n");

					if (udCat.getIbmOnly() == Defines.ETS_IBM_ONLY) {
						strBuffer.append(
							strCatName + "<span class=\"ast\">*</span>");
					} else if (udCat.getIbmOnly() == Defines.ETS_IBM_CONF) {
						strBuffer.append(
							strCatName + "<span class=\"ast\">**</span>");
					} else {
						strBuffer.append(strCatName);
					}

					strBuffer.append("</td>");
					strBuffer.append("</tr>");

					// to make it print as a tree format upto 15 levels...
					printRecursive(
						strBuffer,
						vDetails,
						noshows,
						udCat.getId(),
						oldParentId,
						movecatid,
						iLevel + 1,
						udEdgeAccess);
				}
			}
		}
	}

	/**
	 * @return
	 */
	public String getMoveCategory() {
		return m_strMoveCategory;
	}

	/**
	 * @param string
	 */
	public void setMoveCategory(String strMoveCategory) {
		m_strMoveCategory = strMoveCategory;
	}

	private boolean isMoveCategory() {
		return "true".equals(m_strMoveCategory);
	}

}
