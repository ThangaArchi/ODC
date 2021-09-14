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

import oem.edge.ed.odc.remoteviewer.bdlg.ODCApplicationBDelegate;
import oem.edge.ed.odc.remoteviewer.vo.ApplicationVO;
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
public class ApplicationAction extends Action {

	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";
	private static Log logger = ODCLogger.getLogger(ApplicationAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form, 
		HttpServletRequest request, HttpServletResponse response) throws Exception {

		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value
		ApplicationForm applicationForm = (ApplicationForm) form;
		HttpSession session = null;
		RequestDispatcher rd = request.getRequestDispatcher("jsp/commonErrorPage.jsp");
		
		try {


	session = request.getSession();
	if( (session != null) && ( ( (UserInfo)session.getAttribute("rvadmin_credentials") != null )))
	{
			if( "create_application".equals(mapping.getParameter()) )
			{
				forward = createApplication(mapping, applicationForm, request);
			}

			if( "list_application".equals(mapping.getParameter()) )
			{
				forward = listApplications(mapping, applicationForm, request);
			}

			if( ( "list_application_user_Delete".equals(mapping.getParameter()) ) || ( "list_application_user_Edit".equals(mapping.getParameter()) ) )
			{
				forward = listApplicationDetail(mapping, applicationForm, request);
			}

			if( ("edit_delete_application".equals(mapping.getParameter()))  && applicationForm.getOperation().equals("Delete") )
			{
				forward = deleteApplication(mapping, applicationForm, request);
			}

			if( ("edit_delete_application".equals(mapping.getParameter())) && applicationForm.getOperation().equals("Edit") )
			{
				forward = editApplication(mapping, applicationForm, request);
			}
	}
	else
	{
		logger.info("ApplicationAction :: Session expires..................");
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
		return (forward);
	}


	// MODIFIED METHOD
	public ActionForward createApplication(ActionMapping mapping, ApplicationForm objApplicationForm, HttpServletRequest request) throws Exception
	{
		logger.info("->:: createApplication()");
		
		if ( checkApplicationFields(objApplicationForm) )
		{
			ODCApplicationBDelegate objODCApplicationBDelegate = new ODCApplicationBDelegate();
			ApplicationVO objApplicationVO = new ApplicationVO();
			objApplicationVO.setApplicationName( objApplicationForm.getApplicationName() );
			objApplicationVO.setServerName( objApplicationForm.getServerName() );
			objApplicationVO.setApplicationPath( objApplicationForm.getApplicationPath() );
			objApplicationVO.setIdPrefix( objApplicationForm.getIdPrefix() );
			objApplicationVO.setNumberOfUsers( objApplicationForm.getNumberOfUsers() );
			objApplicationVO.setFileSystemType( objApplicationForm.getFileSystemType() );
		
			ApplicationVO otherApplicationVO =(ApplicationVO) objODCApplicationBDelegate.createApplication(objApplicationVO);
		
			objApplicationForm.setSqlMessage( otherApplicationVO.getSqlMessage() );
		}
		else
			objApplicationForm.setSqlMessage("One of the required fields were either not entered or were entered incorrectly. Please try creating the application again" );
		logger.info("<-:: createApplication()");
		return mapping.findForward("success");
	}


	// NEWLY ADDED ONE try-cath Block
	public boolean checkApplicationFields(ApplicationForm objApplicationForm)
	{
		boolean flag = false;
		
		if( 	(objApplicationForm.getApplicationName() != null) && (objApplicationForm.getApplicationName().trim().length() > 0) &&
				(objApplicationForm.getApplicationPath() != null) && (objApplicationForm.getApplicationPath().trim().length() > 0) &&
				(objApplicationForm.getNumberOfUsers() != null) && (objApplicationForm.getNumberOfUsers().trim().length() > 0)  &&
				(objApplicationForm.getServerName() != null) && ( objApplicationForm.getServerName().trim().length()>0) )
			flag = true;
		else
			flag = false;

		for(int i=0; i<objApplicationForm.getApplicationName().length(); i++)
		{
			char x = objApplicationForm.getApplicationName().charAt(i);
			if(		(x == '~') || (x == '!') || (x == '~') || (x == '!') || (x == '@') || (x == '#') ||
					(x == '$') || (x == '%') || (x == '^') || (x == '&') || (x == '*') || (x == '(') ||
					(x == ')') || (x == '!') || (x == '=') || (x == '/') || (x == '\\') || (x == '?') ||
					(x == ',') || (x == '<') || (x == '.') || (x == '>') || (x == '`') || (x == '?') ||
					(x == '-') || (x == '=')|| (x == '+') || (x == '|') || (x == ':')|| (x == ';') || (x == ' ') )
				return false;
		}
		try{    // NEWLY ADDDED BLOCK
			int x = Integer.parseInt( objApplicationForm.getNumberOfUsers() );
		}catch(Exception e) {
			return false;
		}
		return flag;
	}

	public ActionForward listApplications(ActionMapping mapping, ApplicationForm objApplicationForm, HttpServletRequest request) throws Exception
	{
		logger.info("->:: listApplications()");
	
		ODCApplicationBDelegate objODCApplicationBDelegate = new ODCApplicationBDelegate();
		ApplicationVO objApplicationVO = new ApplicationVO();

		objApplicationVO.setApplicationId( objApplicationForm.getApplicationId() );
		objApplicationVO.setApplicationName( objApplicationForm.getApplicationName() );

		Collection colObj = objODCApplicationBDelegate.findAllApplications();

		objApplicationForm.setApplicationList( colObj );
		logger.info("<-:: listApplications()");
		return mapping.findForward("success");
	}


	public ActionForward listApplicationDetail(ActionMapping mapping, ApplicationForm objApplicationForm, HttpServletRequest request) throws Exception
	{
		logger.info("->:: listApplications()");
		ODCApplicationBDelegate objODCApplicationBDelegate = new ODCApplicationBDelegate();
		ApplicationVO objApplicationVO = new ApplicationVO();
		try{
			StringTokenizer tokenizer = new StringTokenizer(objApplicationForm.getApplicationId(), "@");
			if( tokenizer.hasMoreTokens() ){
				objApplicationForm.setApplicationId( tokenizer.nextToken().trim() );
				objApplicationForm.setApplicationName( tokenizer.nextToken().trim() );
				objApplicationForm.setServerName( tokenizer.nextToken().trim() );
				objApplicationForm.setApplicationPath( tokenizer.nextToken().trim() );
				//objApplicationForm.setIdPrefix( tokenizer.nextToken().trim() );
				objApplicationForm.setNumberOfUsers( tokenizer.nextToken().trim() );
				objApplicationForm.setFileSystemType( tokenizer.nextToken().trim() );
			}
		}catch(Exception e){
			logger.error(e);
			e.printStackTrace();
		}

		Collection colObjTwo = objODCApplicationBDelegate.findSingleUserApplication(objApplicationForm);
		objApplicationForm.setApplicationList( colObjTwo );
		objApplicationForm.setMessage("This operation can not be undone ");
		
		if( "list_application_user_Delete".equals(mapping.getParameter()) ){
			objApplicationForm.setOperation("Delete");
		} else if ( "list_application_user_Edit".equals(mapping.getParameter()) ) {
			objApplicationForm.setOperation("Edit");
		}
		logger.info("<-:: listApplicationDetail()");
		return mapping.findForward("success");
	}


	public ActionForward deleteApplication(ActionMapping mapping, ApplicationForm objApplicationForm, HttpServletRequest request) throws Exception
	{
		logger.info("->:: deleteApplication()");
		ODCApplicationBDelegate objODCApplicationBDelegate = new ODCApplicationBDelegate();
		ApplicationVO objApplicationVO = new ApplicationVO();
	
		objApplicationVO.setFormtoVO(objApplicationForm);
		ApplicationVO otherApplicationVO =(ApplicationVO) objODCApplicationBDelegate.deleteApplication(objApplicationVO);

		objApplicationForm.setMessage("Delete Application: "+ objApplicationForm.getApplicationName() );
		objApplicationForm.setSqlMessage( otherApplicationVO.getSqlMessage() );
		logger.info("<-:: deleteApplication()");
		return mapping.findForward("success");
	}


	public ActionForward editApplication(ActionMapping mapping, ApplicationForm objApplicationForm, HttpServletRequest request) throws Exception
	{
		logger.info("->:: editApplication()");
		ODCApplicationBDelegate objODCApplicationBDelegate = new ODCApplicationBDelegate();
		ApplicationVO objApplicationVO = new ApplicationVO();
	
		objApplicationVO.setFormtoVO(objApplicationForm);
		ApplicationVO otherApplicationVO =(ApplicationVO) objODCApplicationBDelegate.editApplication(objApplicationVO);

		objApplicationForm.setMessage("Edit Application: "+ objApplicationForm.getApplicationName() );
		objApplicationForm.setSqlMessage( otherApplicationVO.getSqlMessage() );
		logger.info("<-:: editApplication()");
		return mapping.findForward("success");
	}
}