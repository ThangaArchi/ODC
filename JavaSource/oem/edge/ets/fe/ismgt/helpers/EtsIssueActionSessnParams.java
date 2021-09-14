package oem.edge.ets.fe.ismgt.helpers;

import java.util.HashMap;

import oem.edge.ets.fe.ismgt.model.EtsCrProbInfoModel;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssProbInfoUsr1Model;
import oem.edge.ets.fe.ismgt.model.EtsIssTypeInfoModel;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004                                          */
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

/** 
 * This class provides the critical methods for set/get/checking the objects/Strings into session
 * Each Object set/get from session is provided separate method, to control the values put into session
 * and get them back
 * These are the following objects set into session and get them back
 * 
 * FORMLABELDATA - HASHMAP 		- 	HashMap of label form data (DB)
 *
 * 
 */

public class EtsIssueActionSessnParams {

	////////////////version of the class////
	public static final String VERSION = "1.47";
	private EtsIssFilterSessnHandler etsIssSessn;
	private EtsIssObjectKey issObjKey;
	private String uniqIssueSessnKey;


	/**
	 * Constructor for EtsIssueActionSessnParams.
	 */
	public EtsIssueActionSessnParams(EtsIssObjectKey issObjKey) {
		super();
		this.etsIssSessn = new EtsIssFilterSessnHandler(issObjKey.getRequest().getSession(true));
		this.issObjKey = issObjKey;
		this.uniqIssueSessnKey = "ETSISSUESACTION" + issObjKey.getProj().getProjectId() + issObjKey.getIssueClass()+issObjKey.getActionkey(); //to make a unique combination
	}

	/**
	 * This method will get basic label form data from session
	 */
	public HashMap getFormLabelData() {

		return (HashMap) etsIssSessn.getSessionObjValue(uniqIssueSessnKey + "FORMLABELDATA");

	}

	/**
	 * This method will set basic label form data into session
	 */
	public void setFormLabelData(HashMap formLabelMap) {

		etsIssSessn.setSessionObjValue(uniqIssueSessnKey + "FORMLABELDATA", formLabelMap);

	}

	/**
	 * This method will return boolean, taking label form data  from session
	 */
	public boolean isFormLabelDataDefnd() {

		return EtsIssFilterUtils.isHashMapDefnd(getFormLabelData());

	}

	/**
		 * This method will check in session, if a EtsIssProbInfoUsr1Model object is there,
		 * if not in session, returns null , otw returns object 
		 */

	public EtsIssProbInfoUsr1Model getSessnProbUsr1InfoModel(String edgeProblemId) {

		String uniqActionSessnKey = uniqIssueSessnKey + edgeProblemId;

		EtsIssProbInfoUsr1Model usr1InfoModel = (EtsIssProbInfoUsr1Model) etsIssSessn.getSessionObjValue(uniqActionSessnKey + "EtsIssueProbInfoUsr1");

		return usr1InfoModel;

	}

	/**
		 * This method will check in session, if a EtsIssProbInfoUsr1Model object is there,
		 * if not in session, returns null , otw returns object 
		 */

	public void setSessnProbUsr1InfoModel(EtsIssProbInfoUsr1Model usr1InfoModel, String edgeProblemId) {

		String uniqActionSessnKey = uniqIssueSessnKey + edgeProblemId;

		etsIssSessn.setSessionObjValue(uniqActionSessnKey + "EtsIssueProbInfoUsr1", usr1InfoModel);

	}

	/**
		 * This method will check in session, if a EtsIssProbInfoUsr1Model object is there,
		 * if not in session, creates 
		 */

	public boolean isUsr1InfoObjDefnd(String edgeProblemId) {

		String uniqActionSessnKey = uniqIssueSessnKey + edgeProblemId;

		EtsIssProbInfoUsr1Model usr1InfoModel = (EtsIssProbInfoUsr1Model) etsIssSessn.getSessionObjValue(uniqActionSessnKey + "EtsIssueProbInfoUsr1");

		if (usr1InfoModel != null) {

			return true;
		}

		return false;

	}

	/**
			 * This method will check in session, if a EtsCrProbInfoModel object is there,
			 * if not in session, returns null , otw returns object 
			 */

	public EtsCrProbInfoModel getSessnCrProbInfoModel(String edgeProblemId) {

		String uniqActionSessnKey = uniqIssueSessnKey + edgeProblemId;

		EtsCrProbInfoModel crInfoModel = (EtsCrProbInfoModel) etsIssSessn.getSessionObjValue(uniqActionSessnKey + "EtsCrProbInfoModel");

		return crInfoModel;

	}

	/**
		 * This method will check in session, if a EtsCrProbInfoModel object is there,
		 * if not in session, returns null , otw returns object 
		 */

	public void setSessnCrProbInfoModel(EtsCrProbInfoModel crInfoModel, String edgeProblemId) {

		String uniqActionSessnKey = uniqIssueSessnKey + edgeProblemId;

		etsIssSessn.setSessionObjValue(uniqActionSessnKey + "EtsCrProbInfoModel", crInfoModel);

	}

	/**
		 * This method will check in session, if a EtsCrProbInfoModel object is there,
		 * if not in session, creates 
		 */

	public boolean isCrInfoObjDefnd(String edgeProblemId) {

		String uniqActionSessnKey = uniqIssueSessnKey + edgeProblemId;

		EtsCrProbInfoModel crInfoModel = (EtsCrProbInfoModel) etsIssSessn.getSessionObjValue(uniqActionSessnKey + "EtsCrProbInfoModel");

		if (crInfoModel != null) {

			return true;
		}

		return false;

	}
	
	

} //end of class
