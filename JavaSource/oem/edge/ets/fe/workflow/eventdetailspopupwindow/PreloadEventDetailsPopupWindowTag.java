/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2006                                     */
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

package oem.edge.ets.fe.workflow.eventdetailspopupwindow;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import oem.edge.ets.fe.workflow.issue.edit.EditIssueFormBean;
import oem.edge.ets.fe.workflow.issue.edit.EditIssueVO;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import org.apache.commons.logging.Log;


/**
 * Class       : PreloadEventDetailsPopupWindowTag
 * Package     : oem.edge.ets.fe.workflow.eventdetailspopupwindow
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class PreloadEventDetailsPopupWindowTag extends TagSupport{
	private static Log logger = WorkflowLogger.getLogger(PreloadEventDetailsPopupWindowTag.class);
	public int doStartTag() throws JspException
	{
		String project_id = pageContext.getRequest().getParameter("proj");
		WorkflowEventDetailsBean f = ((WorkflowEventDetailsBean)pageContext.getRequest().getAttribute("org.apache.struts.taglib.html.BEAN"));
		WorkflowEventDetailsVO vo =null;
		String extend = pageContext.getRequest().getParameter("extend");
		if(f!=null)
		 vo = ((WorkflowEventDetailsVO)(f.getWorkflowObject()));
		EventDetailsPopupWindowPreload preloadBean = new EventDetailsPopupWindowPreload(project_id,vo,extend);
		pageContext.getRequest().setAttribute("preloadBean",preloadBean);
		return SKIP_BODY;
	}
}
