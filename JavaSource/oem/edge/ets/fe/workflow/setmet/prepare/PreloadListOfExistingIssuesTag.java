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

package oem.edge.ets.fe.workflow.setmet.prepare;


import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import org.apache.commons.logging.Log;


/**
 * Class       : PreloadListOfExistingIssuesTag
 * Package     : oem.edge.ets.fe.workflow.prepare
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class PreloadListOfExistingIssuesTag extends TagSupport{

	private static Log logger = WorkflowLogger.getLogger(PreloadListOfExistingIssuesTag.class);	
	 HttpServletRequest request = null;
	public int doStartTag() throws JspException
	{ 
		request=(HttpServletRequest)pageContext.getRequest();
		String pid = (String)request.getParameter("proj");
	    String wid = (String)request.getParameter("wid");
	    if(wid==null)wid = (String)request.getParameter("workflowID");
	    	
	    Enumeration e = request.getAttributeNames();
	    System.out.println("........Attributes are :");
	    while(e.hasMoreElements())
	    {
	    	System.out.println(e.nextElement());
	    }
		ListOfExistingIssuesPreload preloadBean = new ListOfExistingIssuesPreload(pid,wid,(WorkflowForm)request.getAttribute("org.apache.struts.taglib.html.BEAN"));
		request.setAttribute("preloadBean",preloadBean);
		System.out.println("Exiting preloader tag.");
		return SKIP_BODY;
	}
	
}
