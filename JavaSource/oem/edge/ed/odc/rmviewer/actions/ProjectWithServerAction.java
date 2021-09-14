package oem.edge.ed.odc.rmviewer.actions;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ed.odc.rmviewer.bdlg.ODCProjectBDelegate;
import oem.edge.ed.odc.rmviewer.bdlg.ODCServerBDelegate;
import oem.edge.ed.odc.rmviewer.dao.ODCProjectDAO;
import oem.edge.ed.odc.rmviewer.vo.ProjectVO;
import oem.edge.ed.odc.rmviewer.vo.ProjectWithServerVO;
import oem.edge.ed.odc.utils.DB_Connection;
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
public class ProjectWithServerAction extends Action {

	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";
	private static Log logger = ODCLogger.getLogger(ProjectWithServerAction.class);

	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value
		ProjectForm projectForm = (ProjectForm) form;

		try {

			if("createProject".equals(mapping.getParameter()) )
			{
				logger.info("<- listProjects : "+projectForm.getProjectName());
				createProject(mapping, projectForm, request);
				logger.info("-> listProjects : "+projectForm.getProjectName());
			}

			if("listSingleProject".equals(mapping.getParameter()) )
			{
				logger.info("<- listProjects : "+projectForm.getProjectName());
				listProjects(mapping, projectForm, request);
				logger.info("-> listProjects : "+projectForm.getProjectName());
			}

		} catch (Exception ex) {
			logger.error(ex);
			ex.printStackTrace();

			// Report the error using the appropriate name and ID.
			errors.add("name", new ActionError("id"));
		}
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
		}
		forward = mapping.findForward("success");
		return (forward);

	}
	
	public ActionForward createProject(ActionMapping mapping, ProjectForm objProjectForm, HttpServletRequest request) throws Exception
	{
		ODCProjectBDelegate objODCProjectBDelegate = new ODCProjectBDelegate();
		ProjectVO objProjectVO = new ProjectVO();
		objProjectVO.setProjectName( objProjectForm.getProjectName() );
		ProjectVO otherProjectVO =(ProjectVO) objODCProjectBDelegate.createProject(objProjectVO);

		return mapping.findForward("success");
	}

	
	public ActionForward listProjects(ActionMapping mapping, ProjectForm objProjectForm, HttpServletRequest request) throws Exception
	{
		ODCProjectBDelegate objODCProjectBDelegate = new ODCProjectBDelegate();
		ProjectVO objProjectVO = new ProjectVO();
		objProjectVO.setProjectName( objProjectForm.getProjectName() );

		Collection colObj = objODCProjectBDelegate.findAllProjects();
		objProjectForm.setProjectWithServerList( colObj );
		return mapping.findForward("success");
	}
}
