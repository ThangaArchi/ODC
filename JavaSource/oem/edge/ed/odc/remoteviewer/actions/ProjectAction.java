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
import java.util.StringTokenizer;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import oem.edge.ed.odc.remoteviewer.bdlg.ODCProjectBDelegate;
import oem.edge.ed.odc.remoteviewer.vo.ProjectVO;
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
public class ProjectAction extends Action {

	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";
	private static Log logger = ODCLogger.getLogger(ProjectAction.class);

	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value
		ProjectForm projectForm = (ProjectForm) form;
		HttpSession session = null;
		RequestDispatcher rd = request.getRequestDispatcher("jsp/commonErrorPage.jsp");

		try {

	session = request.getSession();
	if( (session != null) && ( ( (UserInfo)session.getAttribute("rvadmin_credentials") != null )))
	{
					if("showProject".equals(mapping.getParameter()) )
					{
						logger.info("-> showProject ");
						try{
							StringTokenizer tokenizer = new StringTokenizer(projectForm.getServerInstanceId(), "@");
							if( tokenizer.hasMoreTokens() ){
								projectForm.setProjectName( tokenizer.nextToken().trim() );
								projectForm.setServerName( tokenizer.nextToken().trim() );
								projectForm.setGridPath( tokenizer.nextToken().trim() );
								projectForm.setMessage("List Project: "+ projectForm.getProjectName() );
								projectForm.setErrorMessage("Success");
							}
						}catch(Exception ex){
							logger.error(ex);
							ex.printStackTrace();
						}
						logger.info("<- showProject ");
					}
		

			if("createProject".equals(mapping.getParameter()) )
			{
				logger.info("-> createProject : "+projectForm.getProjectName());
				projectForm.setProjectName( projectForm.getProjectName().trim() );
				if( checkProjectFields(projectForm)	)
				{
					try{
						StringTokenizer tokenizer = new StringTokenizer(projectForm.getServerInstanceId(), "@");
						if( tokenizer.hasMoreTokens() ){
							projectForm.setServerInstanceId( tokenizer.nextToken().trim() );
							projectForm.setServerName( tokenizer.nextToken().trim() );
							projectForm.setGridPath( tokenizer.nextToken().trim() );
						}
					}catch(Exception ex){
						logger.error(ex);
						ex.printStackTrace();
					}
					createProject(mapping, projectForm, request);
					projectForm.setErrorMessage("Success");
				}
				else
					projectForm.setErrorMessage("Operation failure");
				logger.info("<- createProject : "+projectForm.getProjectName());
			}
			if("DeleteProject".equals(mapping.getParameter()) )
			{
				logger.info("<- DeleteProject : "+projectForm.getProjectName());
				deleteProject(mapping, projectForm, request);
				projectForm.setErrorMessage("Success");
				logger.info("-> DeleteProject : "+projectForm.getProjectName());
			}


			if("listProjects".equals(mapping.getParameter()) )
			{
				logger.info("<- listProjects : "+projectForm.getProjectName());
				listProjects(mapping, projectForm, request);
				projectForm.setErrorMessage("Success");
				logger.info("-> listProjects : "+projectForm.getProjectName());
			}

			if("listSingleProject".equals(mapping.getParameter()) )
			{
				logger.info("<- listProjects : "+projectForm.getProjectName());
				listProjects(mapping, projectForm, request);
				logger.info("-> listProjects : "+projectForm.getProjectName());
			}
	}
	else
	{
		logger.info("ProjectAction :: Session expires..................");
		forward = mapping.findForward("SessionFailed");
		rd.forward(request, response);
	}

		} catch(NullPointerException ne)	{
			logger.error(ne);
			rd.forward(request, response);
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();

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
		if(objProjectForm.getProjectName().length() > 0 )
		{
			ODCProjectBDelegate objODCProjectBDelegate = new ODCProjectBDelegate();
			ProjectVO objProjectVO = new ProjectVO();
			
			objProjectVO.setProjectName( objProjectForm.getProjectName() );
			objProjectVO.setServerName( objProjectForm.getServerName() );
			objProjectVO.setGridPath( objProjectForm.getGridPath() );
			objProjectVO.setServerInstanceId( objProjectForm.getServerInstanceId() );
			
			ProjectVO otherProjectVO =(ProjectVO) objODCProjectBDelegate.createProject(objProjectVO);

			objProjectForm.setMessage("Create Project: "+ otherProjectVO.getProjectName() );
			objProjectForm.setSqlMessage( otherProjectVO.getSqlMessage() );

			return mapping.findForward("success");
		}
		else
			return mapping.findForward("failure");
	}

	
	public ActionForward deleteProject(ActionMapping mapping, ProjectForm objProjectForm, HttpServletRequest request) throws Exception
	{
		ODCProjectBDelegate objODCProjectBDelegate = new ODCProjectBDelegate();
		ProjectVO objProjectVO = new ProjectVO();
		String prj_id="", prj_name="", srv_name="", g_path="";
		try{
			StringTokenizer tokenizer = new StringTokenizer(objProjectForm.getProjectId(), "@");
			if( tokenizer.hasMoreTokens() ){
				objProjectForm.setProjectId( tokenizer.nextToken().trim() );
				objProjectForm.setProjectName( tokenizer.nextToken().trim() );
				objProjectForm.setServerName( tokenizer.nextToken().trim() );
				objProjectForm.setGridPath( tokenizer.nextToken().trim() );
			}
		}catch(Exception ex){
			logger.error(ex);
			ex.printStackTrace();
		}

		objProjectVO.setProjectId( objProjectForm.getProjectId() );
		objProjectVO.setProjectName( objProjectForm.getProjectName() );
		objProjectVO.setServerName( objProjectForm.getServerName() );
		objProjectVO.setGridPath( objProjectForm.getGridPath() );
		ProjectVO otherProjectVO =(ProjectVO) objODCProjectBDelegate.deleteProject(objProjectVO);

		objProjectForm.setMessage("Delete Project: "+ objProjectForm.getProjectName() );
		objProjectForm.setSqlMessage( otherProjectVO.getSqlMessage() );
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

	// NEWLY ADDED METHOD
	public boolean checkProjectFields(ProjectForm objProjectForm)
	{
		boolean flag=true;
	
		if( (objProjectForm.getProjectName() != null) && (objProjectForm.getProjectName().trim().length() > 0) )
			flag=true;
		else
			return false;
		
		for(int i=0; i<objProjectForm.getProjectName().length(); i++)
		{
			char x = objProjectForm.getProjectName().charAt(i);
			if(		(x == '~') || (x == '!') || (x == '~') || (x == '!') || (x == '@') || (x == '#') ||
					(x == '$') || (x == '%') || (x == '^') || (x == '&') || (x == '*') || (x == '(') ||
					(x == ')') || (x == '!') || (x == '=') || (x == '/') || (x == '\\') || (x == '?') ||
					(x == ',') || (x == '<') || (x == '.') || (x == '>') || (x == '`') || (x == '?') ||
					(x == '-') || (x == '=')|| (x == '+') || (x == '|') || (x == ':')|| (x == ';') || (x == ' '))
				return false;
		}
		return flag;
	}


}