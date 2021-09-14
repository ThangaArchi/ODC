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


package oem.edge.ets.fe.workflow.issue.edit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import org.apache.commons.logging.Log;

/**
 * Class       : PreloadIssueEditTag
 * Package     : oem.edge.ets.fe.workflow.issue.edit
 * Description : 
 * Date		   : Oct 10, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class PreloadIssueEditTag extends TagSupport{
	private static Log logger = WorkflowLogger.getLogger(PreloadIssueEditTag.class);
	public int doStartTag() throws JspException
	{
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		String issue_id = request.getParameter("id");
		if(issue_id==null)
		{
			issue_id = ((EditIssueVO)(((EditIssueFormBean)request.getAttribute("org.apache.struts.taglib.html.BEAN")).getWorkflowObject())).getIssueID();
		}
		if(issue_id==null)
		{
			issue_id = (String)request.getAttribute("issueID");
		}
		String project_id = request.getParameter("proj");
		IssueEditPreload preloadBean = new IssueEditPreload(issue_id, project_id,(EditIssueFormBean)request.getAttribute("org.apache.struts.taglib.html.BEAN"), request);
		pageContext.setAttribute("preloadBean",preloadBean);
		return SKIP_BODY;
	}
}

