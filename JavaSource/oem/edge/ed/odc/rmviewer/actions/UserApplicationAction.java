package oem.edge.ed.odc.rmviewer.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ed.odc.rmviewer.bdlg.ODCUserApplicationBDelegate;
import oem.edge.ed.odc.rmviewer.vo.UserApplicationVO;
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
public class UserApplicationAction extends Action {

	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";
	private static Log logger = ODCLogger.getLogger(UserApplicationAction.class);

	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value
		UserApplicationForm userApplicationForm = (UserApplicationForm) form;

		try {

			if("loadUserApplication_Add".equals(mapping.getParameter()) )
			{
				logger.info("<- loadUserApplication_Add");
				listAllUserApplication(mapping, userApplicationForm, request);
				userApplicationForm.setMessage("Add application for : "+userApplicationForm.getUserName());
				userApplicationForm.setModuleLabel("add");
				logger.info("-> loadUserApplication_Add");
			}

			if("loadUserApplication_Delete".equals(mapping.getParameter()) )
			{
				logger.info("<- loadUserApplication_Delete");
				listAllUserApplication(mapping, userApplicationForm, request);
				userApplicationForm.setMessage("delete application for : "+userApplicationForm.getUserName());
				userApplicationForm.setModuleLabel("delete");
				logger.info("-> loadUserApplication_Delete");
			}

			if("loadSingleUserApplication".equals(mapping.getParameter()) )
			{
				logger.info("<- listAllUserApplication");
				loadSingleUserApplication(mapping, userApplicationForm, request);
				logger.info("-> listAllUserApplication");
			}

			if("loadAllUserOnly".equals(mapping.getParameter()) )
			{
				logger.info("<- loadAllUserOnly");
				loadAllUserOnly(mapping, userApplicationForm, request);
				userApplicationForm.setListPage("true");
				logger.info("-> loadAllUserOnly");
			}
			
			if("loadAllUserOnly_list".equals(mapping.getParameter()) )
			{
				logger.info("<- loadAllUserOnly");
				loadAllUserOnly(mapping, userApplicationForm, request);
				userApplicationForm.setListPage("false");
				logger.info("-> loadAllUserOnly");
			}
			
			if("addUserApplication".equals(mapping.getParameter()) )
			{
				logger.info("<- addUserApplication");
				addUserApplication(mapping, userApplicationForm, request);
				logger.info("-> addUserApplication");
			}

			if("deleteUserApplication".equals(mapping.getParameter()) )
			{
				logger.info("<- deleteUserApplication");
				deleteUserApplication(mapping, userApplicationForm, request);
				logger.info("-> deleteUserApplication");
			}
			
		} catch (Exception e) {
			// Report the error using the appropriate name and ID.
			logger.error(e);
			e.printStackTrace();
			errors.add("name", new ActionError("id"));
		}
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
		}
		// Write logic determining how the user should be forwarded.
		forward = mapping.findForward("success");

		// Finish with
		return (forward);
	}


	public ActionForward listAllUserApplication(ActionMapping mapping, UserApplicationForm objUserApplicationForm, HttpServletRequest request) throws Exception
	{
ODCUserApplicationBDelegate objODCUserApplicationBDelegate = new ODCUserApplicationBDelegate();
UserApplicationVO objUserApplicationVO = new UserApplicationVO();
		
objUserApplicationVO.setUserName( objUserApplicationForm.getUserName() );

Collection colObjOne = objODCUserApplicationBDelegate.findSingleUserApplication(objUserApplicationForm);
Collection colObjTwo = objODCUserApplicationBDelegate.findSingleUserNonApplication(objUserApplicationForm);

objUserApplicationForm.setUserApplicationList( colObjOne );
objUserApplicationForm.setUserNonApplicationList( colObjTwo );
		return mapping.findForward("success");
	}

	public ActionForward loadSingleUserApplication(ActionMapping mapping, UserApplicationForm objUserApplicationForm, HttpServletRequest request) throws Exception
	{
		ODCUserApplicationBDelegate objODCUserApplicationBDelegate = new ODCUserApplicationBDelegate();
		UserApplicationVO objUserApplicationVO = new UserApplicationVO();
		
		objUserApplicationVO.setUserName( objUserApplicationForm.getUserName() );
		objUserApplicationVO.setUserName( objUserApplicationForm.getUserName() );

		Collection colObjOne = objODCUserApplicationBDelegate.findSingleUserApplication(objUserApplicationForm);
		objUserApplicationForm.setUserApplicationList( colObjOne );

		return mapping.findForward("success");
	}

	public ActionForward loadAllUserOnly(ActionMapping mapping, UserApplicationForm objUserApplicationForm, HttpServletRequest request) throws Exception
	{
		ODCUserApplicationBDelegate objODCUserApplicationBDelegate = new ODCUserApplicationBDelegate();
		UserApplicationVO objUserApplicationVO = new UserApplicationVO();
		Collection colObj = objODCUserApplicationBDelegate.findAllUserOnly();
		objUserApplicationForm.setUserOnly( colObj );

		return mapping.findForward("success");
	}

	public ActionForward addUserApplication(ActionMapping mapping, UserApplicationForm objUserApplicationForm, HttpServletRequest request) throws Exception
	{
		ODCUserApplicationBDelegate objODCUserApplicationBDelegate = new ODCUserApplicationBDelegate();
		UserApplicationVO objUserApplicationVO = new UserApplicationVO();
		if(objUserApplicationForm.getApplicationId() != null){
				try{
						String serverDet[] = objUserApplicationForm.getApplicationId().split("@"); 
						objUserApplicationForm.setApplicationId( serverDet[0] );
						objUserApplicationForm.setApplicationName( serverDet[1] );
						objUserApplicationForm.setServerName( serverDet[2] );
						objUserApplicationForm.setApplicationPath( serverDet[3] );
				}catch(Exception ex) {
					logger.error(ex);
					ex.printStackTrace();
				}
				objUserApplicationVO.setUserName( objUserApplicationForm.getUserName() );
				objUserApplicationVO.setApplicationId( objUserApplicationForm.getApplicationId() );
				objUserApplicationVO.setApplicationName( objUserApplicationForm.getApplicationName() );
				objUserApplicationVO.setSqlMessage( objUserApplicationForm.getSqlMessage() );
			objUserApplicationVO.setErrorMessage("Success");
		}
		else
			objUserApplicationVO.setErrorMessage("Operation Failure");
		
		UserApplicationVO anotherObjUserApplicationVO = objODCUserApplicationBDelegate.addUserApplication(objUserApplicationVO);
		
		objUserApplicationForm.setSqlMessage( anotherObjUserApplicationVO.getSqlMessage() );
		objUserApplicationForm.setErrorMessage( anotherObjUserApplicationVO.getErrorMessage() );

		return mapping.findForward("success");
	}

	public ActionForward deleteUserApplication(ActionMapping mapping, UserApplicationForm objUserApplicationForm, HttpServletRequest request) throws Exception
	{
		ODCUserApplicationBDelegate objODCUserApplicationBDelegate = new ODCUserApplicationBDelegate();
		UserApplicationVO objUserApplicationVO = new UserApplicationVO();
		if(objUserApplicationForm.getApplicationId() != null){
				try{
						String serverDet[] = objUserApplicationForm.getApplicationId().split("@"); 
						objUserApplicationForm.setApplicationId( serverDet[0] );
						objUserApplicationForm.setApplicationName( serverDet[1] );
						objUserApplicationForm.setServerName( serverDet[2] );
						objUserApplicationForm.setApplicationPath( serverDet[3] );
				}catch(Exception ex){
					logger.error(ex);
					ex.printStackTrace();
				}
				objUserApplicationVO.setUserName( objUserApplicationForm.getUserName() );
				objUserApplicationVO.setApplicationId( objUserApplicationForm.getApplicationId() );
				objUserApplicationVO.setApplicationName( objUserApplicationForm.getApplicationName() );
				objUserApplicationVO.setSqlMessage( objUserApplicationForm.getSqlMessage() );
			objUserApplicationVO.setErrorMessage("Success");
		}
		else
			objUserApplicationVO.setErrorMessage("Operation Failure");
		
		UserApplicationVO anotherObjUserApplicationVO = objODCUserApplicationBDelegate.deleteUserApplication(objUserApplicationVO);
		objUserApplicationForm.setSqlMessage( anotherObjUserApplicationVO.getSqlMessage() );
		objUserApplicationForm.setErrorMessage( anotherObjUserApplicationVO.getErrorMessage() );

		return mapping.findForward("success");
	}
}