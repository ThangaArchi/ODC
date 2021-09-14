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

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.documents.BaseDocumentForm;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.StringUtil;

/**
 * @author vishal
 */
public class PaginateDocLinks extends BodyTagSupport {

	/** Index for the start of the page */
	private String m_strCurrentIndex;
	/** URL of the page to be displayed */
	private String m_strActionPageURL;
	/** No of Records displayed on one page **/
	private int m_intRecordsToDisplay;

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

			StringBuffer strBuffer = new StringBuffer("");
			if (udForm == null) {
				pdWriter.write("&nbsp;");
				pdWriter.flush();
				return SKIP_BODY;
			}
			m_intRecordsToDisplay = Defines.DOC_PAGINATE_RECORD_SIZE;
			int intAllDocsCount = udForm.getAllViewDocsCount();
			
			int intCurrentStIndex = intCurrentStartIndex();
			int intCurrentEndIndex = 0;
			if (intCurrentStIndex + m_intRecordsToDisplay < intAllDocsCount) {
				intCurrentEndIndex = intCurrentStIndex + m_intRecordsToDisplay;
			} else {
				intCurrentEndIndex = intAllDocsCount;
			}
			//Display Next/Prev buttons if the need is there.
			if (intAllDocsCount > m_intRecordsToDisplay) {
				strBuffer.append("<table><tr>");
				strBuffer.append("<td>Results " + (intCurrentStIndex + 1 ) 
						+ " - " + intCurrentEndIndex 
						+ " of " + intAllDocsCount + " &nbsp;&nbsp;</td>");
				

				if (intCurrentStIndex > 0) {
					strBuffer.append("<td>" + "<a class=\"fbox\" href=\""
						+ getIsActionPageURL() + "&from="
						+ (intCurrentStIndex - m_intRecordsToDisplay)
						+ "\"><b>" + "Previous Page" + "</b></a>&nbsp;</td>");
				}
				if (intAllDocsCount > (intCurrentStIndex + m_intRecordsToDisplay)) {
					strBuffer.append("<td>" + "&nbsp;<a class=\"fbox\" href=\""
						+ getIsActionPageURL() + "&from="
						+ (intCurrentStIndex + m_intRecordsToDisplay)
						+ "\"><b>" + "Next Page" + "</b></a></td>");
				}

				strBuffer.append("</tr></table>");
			}
			
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
	private int intCurrentStartIndex() {
		int index = 0;
		if (!StringUtil.isNullorEmpty(m_strCurrentIndex)){
			index = Integer.parseInt(m_strCurrentIndex);
			System.out.println("From taglib-iiii--" + index);
		}
			
		return index;
	}

	/**
	 * @return
	 */
	public String getCurrentIndex() {
		return m_strCurrentIndex;
	}

	/**
	 * @param string
	 */
	public void setCurrentIndex(String strCurrentIndex) {
		m_strCurrentIndex = strCurrentIndex;
	}

	/**
	 * @return
	 */
	private String getIsActionPageURL() {
		String url = "";
		if (!StringUtil.isNullorEmpty(m_strActionPageURL)) {
			url = m_strActionPageURL;
		}
		return url;
	}

	/**
	 * @return
	 */
	public String getActionPageURL() {
		return m_strActionPageURL;
	}

	/**
	 * @param string
	 */
	public void setActionPageURL(String strURL) {
		m_strActionPageURL = strURL;
	}

}
