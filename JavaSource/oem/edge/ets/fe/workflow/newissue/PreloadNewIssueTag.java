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

package oem.edge.ets.fe.workflow.newissue;

import javax.servlet.jsp.tagext.TagSupport;

import oem.edge.ets.fe.workflow.newissue.NewIssuePreload;
import javax.servlet.jsp.JspException;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import org.apache.commons.logging.Log;


/**
 * Class       : PreloadNewIssueTag
 * Package     : oem.edge.ets.fe.workflow.newissue
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class PreloadNewIssueTag extends TagSupport {
	private static Log logger = WorkflowLogger.getLogger(PreloadNewIssueTag.class);
	public int doStartTag() throws JspException {
		pageContext.setAttribute("preloadBean",new NewIssuePreload((String)pageContext.getRequest().getParameter("proj")));
		return SKIP_BODY;
	}
}
