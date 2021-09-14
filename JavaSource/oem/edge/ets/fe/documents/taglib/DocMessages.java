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
import java.util.Iterator;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.documents.BaseDocumentForm;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.StringUtil;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.util.RequestUtils;

/**
 * @author v2srikau
 */
public class DocMessages extends BodyTagSupport {

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
			StringBuffer strBuffer = new StringBuffer(StringUtil.EMPTY_STRING);
			String strProjectId =
				pdRequest.getParameter(DocConstants.PARAM_PROJECTID);
			String strTopCatID =
				pdRequest.getParameter(DocConstants.PARAM_TOPCATEGORY);
			String sLink = pdRequest.getParameter(DocConstants.PARAM_LINKID);
			if (StringUtil.isNullorEmpty(sLink)) {
				sLink = Defines.LINKID;
			}

			BaseDocumentForm udForm =
				(BaseDocumentForm) pageContext.getRequest().getAttribute(
					DocConstants.DOC_FORM);

			ActionErrors pdErrors =
				(ActionErrors) pdRequest.getAttribute(
					"org.apache.struts.action.ERROR");

			HttpSession pdSession = pageContext.getSession();
			ActionErrors pdProcessorErrors =
				(ActionErrors) pdSession.getAttribute(
					DocConstants.SESS_ATTR_PROCERROR);
			if (pdProcessorErrors != null && pdProcessorErrors.size() > 0) {
				if (pdErrors != null) {
					pdErrors.add(pdProcessorErrors);
				} else {
					pdErrors = pdProcessorErrors;
				}
				pdSession.removeAttribute(DocConstants.SESS_ATTR_PROCERROR);
			}

			if (pdErrors != null && pdErrors.size() > 0) {

				strBuffer.append("<table>");

				Iterator pdIterator = pdErrors.get();
				while (pdIterator.hasNext()) {
					ActionMessage pdEachMessage =
						(ActionMessage) pdIterator.next();

					String strMessage =
						RequestUtils.message(
							pageContext,
							"DocumentMessages",
							null,
							pdEachMessage.getKey(),
							pdEachMessage.getValues());
					strBuffer.append("<tr>");
					strBuffer.append("<td>");
					strBuffer.append(
						"<span style=\"color:#ff3333\">"
							+ strMessage
							+ "</span>");
					strBuffer.append("</td>");
					strBuffer.append("</tr>");
				}
				strBuffer.append("</table>");
			}

			pdWriter.write(strBuffer.toString());
			pdWriter.flush();
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
		return SKIP_BODY;
	}
}
