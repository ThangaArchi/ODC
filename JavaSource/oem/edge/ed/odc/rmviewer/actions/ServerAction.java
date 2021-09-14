package oem.edge.ed.odc.rmviewer.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ed.odc.rmviewer.bdlg.ODCServerBDelegate;
import oem.edge.ed.odc.rmviewer.vo.ServerVO;
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

		try {


			if("List_AllServer".equals(mapping.getParameter()) )
			{
				logger.info("<- listProjects : ");
				listAllServer(mapping, serverForm, request);
				logger.info("-> listProjects : ");
			}

		} catch (Exception e) {

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

}
