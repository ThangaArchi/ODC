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

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.documents.BaseDocumentForm;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.pmo.ETSPMOffice;

/**
 * @author v2srikau
 */
public class DocumentHeaderTag extends BodyTagSupport {

	/** Flag whether this Header is on the categories only page */
	private String m_strIsCategoryPage;

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
				(BaseDocumentForm) pageContext.getRequest().getAttribute(
					DocConstants.DOC_FORM);

			StringBuffer strBuffer = new StringBuffer(StringUtil.EMPTY_STRING);
			if (udForm == null) {
				pdWriter.write("&nbsp;");
				pdWriter.flush();
				return SKIP_BODY;
			}
			String strProjectId = udForm.getProj();
			String strTopCatID = udForm.getTc();
			String sLink = udForm.getLinkid();
			if (StringUtil.isNullorEmpty(sLink)) {
				sLink = Defines.LINKID;
			}

			Vector vtBreadCrumbs = udForm.getBreadCrumbs();
			//Remove the breadcrumb , if the size is 1, & category is true.
			//changes for 6.2.2 -- Vishal
			if (vtBreadCrumbs.size() == 1 && isCategoryPage()) {
				pdWriter.flush();
				return SKIP_BODY;
			}			
			//End of changes - 6.2.2 -- Vishal
			
			for (int iCounter = (vtBreadCrumbs.size()) - 1;
				iCounter >= 0;
				iCounter--) {
				Object obj = vtBreadCrumbs.elementAt(iCounter);
				if (obj instanceof ETSCat) {
					ETSCat udCat = (ETSCat) obj;
					if (iCounter != (vtBreadCrumbs.size()) - 1) {
						//buf.append("&nbsp>&nbsp;");
						strBuffer.append(" &gt; ");
					}

					if (iCounter == 0) {
						if (isCategoryPage()) {
							strBuffer.append("<b>" + udCat.getName() + "</b>");
						} else {
							strBuffer.append(
								"<a href=\"ETSProjectsServlet.wss?proj="
									+ strProjectId
									+ "&tc="
									+ strTopCatID
									+ "&cc="
									+ udCat.getId()
									+ "&linkid="
									+ sLink
									+ "\"><b>"
									+ udCat.getName()
									+ "</b></a>");
						}
					} else {
						strBuffer.append(
							"<a href=\"ETSProjectsServlet.wss?proj="
								+ strProjectId
								+ "&tc="
								+ strTopCatID
								+ "&cc="
								+ udCat.getId()
								+ "&linkid="
								+ sLink
								+ "\">"
								+ udCat.getName()
								+ "</a>");
					}
					
				}
				else if (obj instanceof ETSPMOffice){
					ETSPMOffice udCat = (ETSPMOffice) obj;
					if (iCounter != (vtBreadCrumbs.size()) - 1) {
						//buf.append("&nbsp>&nbsp;");
						strBuffer.append(" &gt; ");
					}

					if (iCounter == 0) {
						if (isCategoryPage()) {
							strBuffer.append("<b>" + udCat.getName() + "</b>");
						} else {
							strBuffer.append(
								"<a href=\"displayDocumentList.wss?proj="
									+ strProjectId
									+ "&tc="
									+ strTopCatID
									+ "&cc="
									+ udCat.getPMO_Parent_ID()
									+ "&pmoCat="
									+ udCat.getPMOID()
									+ "&linkid="
									+ sLink
									+ "\"><b>"
									+ udCat.getName()
									+ "</b></a>");
						}
					} else {
						strBuffer.append(
							"<a href=\"displayDocumentList.wss?proj="
								+ strProjectId
								+ "&tc="
								+ strTopCatID
								+ "&cc="
								+ udCat.getPMO_Parent_ID()
								+ "&pmoCat="
								+ udCat.getPMOID()
								+ "&linkid="
								+ sLink
								+ "\"><b>"
								+ udCat.getName()
								+ "</b></a>");
					}
				}
			}
			// Removed breaks from the header.jsp file & added the same here
			strBuffer.append("<br /><br />");
			pdWriter.write(strBuffer.toString());
			pdWriter.flush();
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
		return SKIP_BODY;
	}

	/**
	 * @return
	 */
	private boolean isCategoryPage() {
		return (
			(!StringUtil.isNullorEmpty(m_strIsCategoryPage))
				&& (StringUtil.FLAG_TRUE.equals(m_strIsCategoryPage)));
	}

	/**
	 * @return
	 */
	public String getIsCategoryPage() {
		return m_strIsCategoryPage;
	}

	/**
	 * @param string
	 */
	public void setIsCategoryPage(String strIsCategoryPage) {
		m_strIsCategoryPage = strIsCategoryPage;
	}

}
