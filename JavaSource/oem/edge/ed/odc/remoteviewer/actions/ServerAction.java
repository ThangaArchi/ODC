package oem.edge.ed.odc.remoteviewer.actions;

/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004                                     */
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

import oem.edge.ed.odc.dsmp.server.UserInfo;
import java.util.Collection;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import oem.edge.ed.odc.remoteviewer.bdlg.ODCServerBDelegate;
import oem.edge.ed.odc.remoteviewer.vo.ServerVO;
import oem.edge.ed.odc.utils.ODCLogger;

import org.apache.commons.logging.Log;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @version 	1.0
 * @author tkandhas@in.ibm.com
 */
public class ServerAction extends Action {

	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";
	private static Log logger = ODCLogger.getLogger(ServerAction.class);

	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value
		ServerForm serverForm = (ServerForm) form;
		HttpSession session = null;
		RequestDispatcher rd = request.getRequestDispatcher("jsp/commonErrorPage.jsp");

		try {

	session = request.getSession();
	if( (session != null) && ( ( (UserInfo)session.getAttribute("rvadmin_credentials") != null )))
	{
			if("ListAllServer".equals(mapping.getParameter()) )
			{
				logger.info("=> listProjects : ");
				listAllServer(mapping, serverForm, request);
				logger.info("<= listProjects : ");
			}

			if("ListUniqueServer".equals(mapping.getParameter()) )
			{
				logger.info("=> listProjects : ");
				listUniqueServer(mapping, serverForm, request);
				logger.info("<= listProjects : ");
			}
	}
	else
	{
		logger.info("ServerAction :: Session expires..................");
		forward = mapping.findForward("SessionFailed");
		rd.forward(request, response);
	}

		} catch(NullPointerException ne)	{
			logger.error(ne);
			rd.forward(request, response);
		} catch (Exception e) {
			logger.error(e);
			// Report the error using the appropriate name and ID.
			errors.add("name", new ActionError("id"));

		}

		// If a message is required, save the specified key(s)
		// into the request for use by the <struts:errors> tag.

		if (!errors.isEmpty()) {
			saveErrors(request, errors);
		}
		// Write logic determining how the user should be forwarded.
		forward = mapping.findForward("success");

		// Finish with
		return (forward);

	}


	public ActionForward listAllServer(ActionMapping mapping, ServerForm objServerForm, HttpServletRequest request) throws Exception
	{
		ODCServerBDelegate objODCServerBDelegate = new ODCServerBDelegate();
		ServerVO objServerVO = new ServerVO();

		Collection colObj = objODCServerBDelegate.findAllServer();
		objServerForm.setServerList( colObj );
		return mapping.findForward("success");
	}

	public ActionForward listUniqueServer(ActionMapping mapping, ServerForm objServerForm, HttpServletRequest request) throws Exception
	{
		ODCServerBDelegate objODCServerBDelegate = new ODCServerBDelegate();
		ServerVO objServerVO = new ServerVO();

		Collection colObj = objODCServerBDelegate.findUniqueServer();
		objServerForm.setServerListUnique( colObj );
		return mapping.findForward("success");
	}

}
