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

import oem.edge.ed.odc.remoteviewer.bdlg.ODCUserProjectBDelegate;
import oem.edge.ed.odc.remoteviewer.vo.UserProjectVO;
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
public class UserProjectAction extends Action {

	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";
	private static Log logger = ODCLogger.getLogger(UserProjectAction.class);

	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value
		UserProjectForm userProjectForm = (UserProjectForm) form;
		HttpSession session = null;
		RequestDispatcher rd = request.getRequestDispatcher("jsp/commonErrorPage.jsp");

		try {

	session = request.getSession();
	if( (session != null) && ( ( (UserInfo)session.getAttribute("rvadmin_credentials") != null )))
	{
			if("loadAllUserOnly".equals(mapping.getParameter()) )
			{
				logger.info("-> loadAllUserOnly");
				loadAllUserOnly(mapping, userProjectForm, request);
				userProjectForm.setListPage("true");
				logger.info("<- loadAllUserOnly");
			}

			if("loadAllUserOnly_list".equals(mapping.getParameter()) )
			{
				logger.info("-> loadAllUserOnly");
				loadAllUserOnly(mapping, userProjectForm, request);
				userProjectForm.setListPage("false");
				logger.info("<- loadAllUserOnly");
			}

			if("loadUserProject_Add".equals(mapping.getParameter()) )
			{
				if( (userProjectForm.getUserName() != null) && (userProjectForm.getUserName().trim().length() > 0) )
    				{

				logger.info("-> loadUserProject_Add");
				listAllUserProject(mapping, userProjectForm, request);
				userProjectForm.setMessage("Add Project for : "+userProjectForm.getUserName());
				userProjectForm.setModuleLabel("add");
				logger.info("<- loadUserProject_Add");
				 }
    				else
     				userProjectForm.setErrorMessage("Please enter valid ICC user-name ....!!!");


			}

			if("loadUserProject_Delete".equals(mapping.getParameter()) )
			{
				logger.info("-> loadUserProject_Delete");
				listAllUserProject(mapping, userProjectForm, request);
				userProjectForm.setMessage("delete Project for : "+userProjectForm.getUserName());
				userProjectForm.setModuleLabel("delete");
				logger.info("<- loadUserProject_Delete");
			}

			if("addUserProject".equals(mapping.getParameter()) )
			{
				logger.info("-> addUserProject");
				addUserProject(mapping, userProjectForm, request);
				logger.info("<- addUserProject");
			}

			if("deleteUserProject".equals(mapping.getParameter()) )
			{
				logger.info("-> deleteUserProject");
				deleteUserProject(mapping, userProjectForm, request);
				logger.info("<- deleteUserProject");
			}
	}
	else
	{
		logger.info(" UserProjectAction:: Session expires..................");
		forward = mapping.findForward("SessionFailed");
		rd.forward(request, response);
	}

		} catch(NullPointerException ne)	{
			logger.error(ne);
			rd.forward(request, response);
		} catch (Exception e) {
			logger.error(e);
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

	public ActionForward loadAllUserOnly(ActionMapping mapping, UserProjectForm objUserProjectForm, HttpServletRequest request) throws Exception
	{
		ODCUserProjectBDelegate objODCUserProjectBDelegate = new ODCUserProjectBDelegate();
		UserProjectVO objUserProjectVO = new UserProjectVO();
		Collection colObj = objODCUserProjectBDelegate.findAllUserOnly();
		objUserProjectForm.setUserOnly( colObj );

		return mapping.findForward("success");
	}


	public ActionForward listAllUserProject(ActionMapping mapping, UserProjectForm objUserProjectForm, HttpServletRequest request) throws Exception
	{
		ODCUserProjectBDelegate objODCUserProjectBDelegate = new ODCUserProjectBDelegate();
		UserProjectVO objUserProjectVO = new UserProjectVO();
				
		objUserProjectVO.setUserName( objUserProjectForm.getUserName() );
		
		Collection colObjOne = objODCUserProjectBDelegate.findSingleUserProject(objUserProjectForm);
		Collection colObjTwo = objODCUserProjectBDelegate.findSingleUserNonProject(objUserProjectForm);
		
		objUserProjectForm.setUserProjectList( colObjOne );
		objUserProjectForm.setUserNonProjectList( colObjTwo );
		return mapping.findForward("success");
	}


	public ActionForward addUserProject(ActionMapping mapping, UserProjectForm objUserProjectForm, HttpServletRequest request) throws Exception
	{
		ODCUserProjectBDelegate objODCUserProjectBDelegate = new ODCUserProjectBDelegate();
		UserProjectVO objUserProjectVO = new UserProjectVO();
		if(objUserProjectForm.getProjectId() != null){
				try{
					StringTokenizer tokenizer = new StringTokenizer(objUserProjectForm.getProjectId(), "@");
					if( tokenizer.hasMoreTokens() ){
						objUserProjectForm.setProjectId( tokenizer.nextToken().trim() );
						objUserProjectForm.setProjectName( tokenizer.nextToken().trim() );
						objUserProjectForm.setServerName( tokenizer.nextToken().trim() );
						objUserProjectForm.setGridPath( tokenizer.nextToken().trim() );
					}
				}catch(Exception ex){
					logger.error(ex);
					ex.printStackTrace();
				}

			objUserProjectVO.setUserName( objUserProjectForm.getUserName() );
			objUserProjectVO.setServerName( objUserProjectForm.getServerName() );
			objUserProjectVO.setProjectId( objUserProjectForm.getProjectId() );
			objUserProjectVO.setProjectName( objUserProjectForm.getProjectName() );
			objUserProjectVO.setGridPath( objUserProjectForm.getGridPath() );
			
			objUserProjectVO.setSqlMessage( objUserProjectForm.getSqlMessage() );
			objUserProjectVO.setErrorMessage("Success");
		}
		else
			objUserProjectVO.setErrorMessage("Operation Failure");
		
		UserProjectVO anotherObjUserProjectVO = objODCUserProjectBDelegate.addUserProject(objUserProjectVO);
		
		objUserProjectForm.setSqlMessage( anotherObjUserProjectVO.getSqlMessage() );
		objUserProjectForm.setErrorMessage( anotherObjUserProjectVO.getErrorMessage() );
		
		return mapping.findForward("success");
	}

	public ActionForward deleteUserProject(ActionMapping mapping, UserProjectForm objUserProjectForm, HttpServletRequest request) throws Exception
	{
		ODCUserProjectBDelegate objODCUserProjectBDelegate = new ODCUserProjectBDelegate();
		UserProjectVO objUserProjectVO = new UserProjectVO();
		if(objUserProjectForm.getProjectId() != null){
				try{
					StringTokenizer tokenizer = new StringTokenizer(objUserProjectForm.getProjectId(), "@");
					if( tokenizer.hasMoreTokens() ){
						objUserProjectForm.setProjectId( tokenizer.nextToken().trim() );
						objUserProjectForm.setProjectName( tokenizer.nextToken().trim() );
						objUserProjectForm.setServerName( tokenizer.nextToken().trim() );
						objUserProjectForm.setGridPath( tokenizer.nextToken().trim() );
					}
				}catch(Exception ex){
					logger.error(ex);
					ex.printStackTrace();
				}

			objUserProjectVO.setUserName( objUserProjectForm.getUserName() );
			objUserProjectVO.setProjectId( objUserProjectForm.getProjectId() );
			objUserProjectVO.setProjectName( objUserProjectForm.getProjectName() );

			objUserProjectVO.setServerName( objUserProjectForm.getServerName() );
			objUserProjectVO.setGridPath( objUserProjectForm.getGridPath() );
			
			objUserProjectVO.setSqlMessage( objUserProjectForm.getSqlMessage() );
			objUserProjectVO.setErrorMessage("Success");
		}
		else
			objUserProjectVO.setErrorMessage("Operation Failure");

		UserProjectVO anotherObjUserProjectVO = objODCUserProjectBDelegate.deleteUserProject(objUserProjectVO);
		objUserProjectForm.setSqlMessage( anotherObjUserProjectVO.getSqlMessage() );
		objUserProjectForm.setErrorMessage( anotherObjUserProjectVO.getErrorMessage() );

		return mapping.findForward("success");
	}
}