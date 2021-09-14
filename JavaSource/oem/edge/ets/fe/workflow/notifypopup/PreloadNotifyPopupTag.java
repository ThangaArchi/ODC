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

package oem.edge.ets.fe.workflow.notifypopup;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import org.apache.commons.logging.Log;


/**
 * Class       : PreloadNotifyPopupTag
 * Package     : oem.edge.ets.fe.workflow.notifypopup
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class PreloadNotifyPopupTag extends TagSupport{
	private static Log logger = WorkflowLogger.getLogger(PreloadNotifyPopupTag.class);
	public int doStartTag() throws JspException
	{
		String project_id = pageContext.getRequest().getParameter("proj");
		String userid = (String)pageContext.getSession().getAttribute("userid");
		pageContext.getSession().removeAttribute("userid");
		NotifyPopupPreload preloadBean = new NotifyPopupPreload(project_id,userid);
		pageContext.getRequest().setAttribute("preloadBean",preloadBean);
		return SKIP_BODY;
	}
}
