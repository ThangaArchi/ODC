package oem.edge.ed.odc.rmviewer.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ed.odc.rmviewer.bdlg.ODCApplicationBDelegate;
import oem.edge.ed.odc.rmviewer.vo.ApplicationVO;
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

		try {

			if( "create_application".equals(mapping.getParameter()) )
			{
				createProject(mapping, applicationForm, request);
			}

			if( "list_application".equals(mapping.getParameter()) )
			{
				listApplications(mapping, applicationForm, request);
			}

			if( ( "list_application_user_Delete".equals(mapping.getParameter()) ) || ( "list_application_user_Edit".equals(mapping.getParameter()) ) )
			{
				listApplicationDetail(mapping, applicationForm, request);
			}

			if( ("edit_delete_application".equals(mapping.getParameter()))  && applicationForm.getOperation().equals("Delete") )
			{
				deleteApplication(mapping, applicationForm, request);
			}

			if( ("edit_delete_application".equals(mapping.getParameter())) && applicationForm.getOperation().equals("Edit") )
			{
				editApplication(mapping, applicationForm, request);
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


	public ActionForward createProject(ActionMapping mapping, ApplicationForm objApplicationForm, HttpServletRequest request) throws Exception
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

		return mapping.findForward("success");
	}

	public ActionForward listApplications(ActionMapping mapping, ApplicationForm objApplicationForm, HttpServletRequest request) throws Exception
	{
		ODCApplicationBDelegate objODCApplicationBDelegate = new ODCApplicationBDelegate();
		ApplicationVO objApplicationVO = new ApplicationVO();

		objApplicationVO.setApplicationId( objApplicationForm.getApplicationId() );
		objApplicationVO.setApplicationName( objApplicationForm.getApplicationName() );

		Collection colObj = objODCApplicationBDelegate.findAllApplications();

		objApplicationForm.setApplicationList( colObj );

		return mapping.findForward("success");
	}


	public ActionForward listApplicationDetail(ActionMapping mapping, ApplicationForm objApplicationForm, HttpServletRequest request) throws Exception
	{
		ODCApplicationBDelegate objODCApplicationBDelegate = new ODCApplicationBDelegate();
		ApplicationVO objApplicationVO = new ApplicationVO();

		try{
				String serverDet[] = objApplicationForm.getApplicationId().split("@"); 
					
				objApplicationForm.setApplicationId( serverDet[0] );
				objApplicationForm.setApplicationName( serverDet[1] );
				objApplicationForm.setServerName( serverDet[2] );
				objApplicationForm.setApplicationPath( serverDet[3] );
				objApplicationForm.setIdPrefix( serverDet[4] );
				objApplicationForm.setNumberOfUsers( serverDet[5] );
				objApplicationForm.setFileSystemType( serverDet[6] );
			}catch(Exception e)
			{
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

		return mapping.findForward("success");
	}


	public ActionForward deleteApplication(ActionMapping mapping, ApplicationForm objApplicationForm, HttpServletRequest request) throws Exception
	{
		ODCApplicationBDelegate objODCApplicationBDelegate = new ODCApplicationBDelegate();
		ApplicationVO objApplicationVO = new ApplicationVO();
	
		objApplicationVO.setFormtoVO(objApplicationForm);
		ApplicationVO otherApplicationVO =(ApplicationVO) objODCApplicationBDelegate.deleteApplication(objApplicationVO);

		objApplicationForm.setMessage("Delete Application: "+ objApplicationForm.getApplicationName() );
		objApplicationForm.setSqlMessage( otherApplicationVO.getSqlMessage() );
		return mapping.findForward("success");
	}


	public ActionForward editApplication(ActionMapping mapping, ApplicationForm objApplicationForm, HttpServletRequest request) throws Exception
	{
		ODCApplicationBDelegate objODCApplicationBDelegate = new ODCApplicationBDelegate();
		ApplicationVO objApplicationVO = new ApplicationVO();
	
		objApplicationVO.setFormtoVO(objApplicationForm);
		ApplicationVO otherApplicationVO =(ApplicationVO) objODCApplicationBDelegate.editApplication(objApplicationVO);

		objApplicationForm.setMessage("Edit Application: "+ objApplicationForm.getApplicationName() );
		objApplicationForm.setSqlMessage( otherApplicationVO.getSqlMessage() );
		return mapping.findForward("success");
	}
}
