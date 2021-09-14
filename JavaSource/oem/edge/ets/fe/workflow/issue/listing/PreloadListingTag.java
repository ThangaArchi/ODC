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



package oem.edge.ets.fe.workflow.issue.listing;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import oem.edge.ets.fe.workflow.log.WorkflowLogger;

import org.apache.commons.logging.Log;


/**
 * Class       : PreloadListingTag
 * Package     : oem.edge.ets.fe.workflow.issue.listing
 * Description : 
 * Date		   : Oct 9, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class PreloadListingTag extends TagSupport{
	private static Log logger = WorkflowLogger.getLogger(PreloadListingTag.class);
	public int doStartTag() throws JspException
	{
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		String project_id = request.getParameter("proj");
		String wf_id = request.getParameter("workflowID");
		ListingPreload preloadBean = new ListingPreload(project_id, wf_id); 
		pageContext.setAttribute("preloadBean",preloadBean);
		return SKIP_BODY;
	}
}

