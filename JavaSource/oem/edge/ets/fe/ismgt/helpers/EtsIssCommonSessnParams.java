package oem.edge.ets.fe.ismgt.helpers;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

import oem.edge.common.*;
import oem.edge.amt.*;

import oem.edge.ets.fe.ETSProj;

import oem.edge.ets.fe.ismgt.model.*;
import oem.edge.ets.fe.ismgt.dao.*;
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
 * @author v2phani
 * This class provides the basic set/get/is methods for common params across ets
 * like project_id, project primary contact info, project info
 * etsamthf - AmtHeaderFooter object
 * EtsIssCommonSessnParams.PRIMCONTACTINFO
 * EtsIssCommonSessnParams.PROJECT
 */

public class EtsIssCommonSessnParams {

	////////////////version of the class////
	public static final String VERSION = "1.51";

	//////////////////////
	private EtsIssFilterSessnHandler etsIssSessn;
	private String reqProjectId;
	////project params////
	private EtsPrimaryContactInfo etsContInfo;
	private ETSProj etsProj;
	CommonInfoDAO comDao;

	/**
		 * Constructor for EtsIssCommonSessnParams.
		 */
	public EtsIssCommonSessnParams(HttpSession session) throws SQLException, Exception {
		super();
		this.etsIssSessn = new EtsIssFilterSessnHandler(session);
		this.comDao = new CommonInfoDAO();

	}

	/**
	 * Constructor for EtsIssCommonSessnParams.
	 */
	public EtsIssCommonSessnParams(HttpSession session, String reqProjectId) throws SQLException, Exception {
		super();
		this.etsIssSessn = new EtsIssFilterSessnHandler(session);
		this.reqProjectId = reqProjectId;
		this.comDao = new CommonInfoDAO();

		try {
			//set common params into session
			setCommonParams();

		} catch (SQLException se) {
			throw se;

		} catch (Exception e) {
			throw e;

		}
	}

	/**
	 * check based on project_id from request/session,set the latest primary contact info
	 * project_info, based on project id
	 * 
	 */

	private void setCommonParams() throws SQLException, Exception {

		///set the project info 
		setEtsProj();

		//set the project contact info
		setEtsContInfo();

		//set the project_id into session finally
		setSessnProjectId(reqProjectId);

	}

	/**
	 * Returns the etsCont.
	 * @return EtsPrimaryContactInfo
	 */
	public EtsPrimaryContactInfo getEtsContInfo() {

		return etsContInfo;
	}

	/**
	 * Returns the etsProj.
	 * @return ETSProj
	 */
	public ETSProj getEtsProj() {
		return etsProj;
	}

	/**
	 * Sets the etsCont.
	 * @param etsCont The etsCont to set
	 */
	private void setEtsContInfo() throws SQLException {

		String sessnProjId = AmtCommonUtils.getTrimStr(getSessnProjectId());

		/////change the prim contact info as and when the requested project changes session
		////compare the project_id from request, with session,get from DB, if it is different

		//if (!sessnProjId.equals(reqProjectId)) {

			setSessnPrimContactInfo(comDao.getProjContactInfo(reqProjectId));

		//}

		//get always from session

		etsContInfo = getSessnPrimContactInfo();

		//check once again if object retrieved from session is proper if not defined,get from DB

		if (!isPrimContactDefnd(etsContInfo)) {

			etsContInfo = comDao.getProjContactInfo(reqProjectId);
		}

	}

	/**
		 * Sets the etsCont.
		 * @param etsCont The etsCont to set
		 */
	public void setEtsContInfo(String actProjectId) throws SQLException {

		setSessnPrimContactInfo(comDao.getProjContactInfo(actProjectId));

	}

	/**
	 * Sets the etsProj.
	 * @param etsProj The etsProj to set
	 */
	private void setEtsProj() throws SQLException, Exception {

		String sessnProjId = AmtCommonUtils.getTrimStr(getSessnProjectId());

		//change the project info as and when the requested project changes session
		//compare the project_id from request, with session,get from DB, if it is different

		if (!sessnProjId.equals(reqProjectId)) {

			setSessnProjInfo(comDao.getProjectDetails(reqProjectId));
		}

		//always get from session

		etsProj = (ETSProj) getSessnProjInfo();

		//check once again if object retrieved from session is proper if not defined,get from DB

		if (!isEtsProjectDefnd(etsProj)) {

			etsProj = comDao.getProjectDetails(reqProjectId);

		}

	}

	/**
	 * Returns the sessnProjectId.
	 * @return String
	 */
	private String getSessnProjectId() {

		return (String) etsIssSessn.getSessionStrValue("EtsIssCommonSessnParams.PROJECTID");
	}

	/**
	 * Sets the sessnProjectId.
	 * @param sessnProjectId The sessnProjectId to set
	 */
	private void setSessnProjectId(String sessnProjectId) {

		etsIssSessn.setSessionStrValue("EtsIssCommonSessnParams.PROJECTID", sessnProjectId);
	}

	/**
	 * This method will return boolean, taking basic project_id  from session
	 */
	private boolean isSessnProjectIdDefnd() {

		return EtsIssFilterUtils.isStringDefnd(getSessnProjectId());

	}

	/**
	 * This method will get primary contact object info object  from session
	 */
	private EtsPrimaryContactInfo getSessnPrimContactInfo() {

		return (EtsPrimaryContactInfo) etsIssSessn.getSessionObjValue("EtsIssCommonSessnParams.PRIMCONTACTINFO");

	}

	/**
	 * This method will set  primary contact object info object  into session
	 */
	public void setSessnPrimContactInfo(EtsPrimaryContactInfo primContactInfo) {

		etsIssSessn.setSessionObjValue("EtsIssCommonSessnParams.PRIMCONTACTINFO", primContactInfo);

	}

	/**
	 * This method will return boolean, taking primary object  from session and check for object attribute value,say full name
	 * which must be there for every primary contact defined
	 */
	private boolean isSessnPrimContactDefnd() {

		return isPrimContactDefnd(getSessnPrimContactInfo());

	}

	/**
	 * This method will return boolean, taking primary object and check for object attribute value,say full name
	 * which must be there for every primary contact defined
	 */
	private boolean isPrimContactDefnd(EtsPrimaryContactInfo etsContInfo) {

		return (EtsIssFilterUtils.isObjectDefnd(etsContInfo) && EtsIssFilterUtils.isStringDefnd(etsContInfo.getUserFullName()));

	}

	/**
	 * This method will get project info object from session
	 */
	private ETSProj getSessnProjInfo() {

		return (ETSProj) etsIssSessn.getSessionObjValue("EtsIssCommonSessnParams.PROJECT");

	}

	/**
	 * This method will set  project info  object  into session
	 */
	private void setSessnProjInfo(ETSProj etsProj) {

		etsIssSessn.setSessionObjValue("EtsIssCommonSessnParams.PROJECT", etsProj);

	}

	/**
	 * This method will return boolean, taking primary object  from session and check for object attribute value,say full name
	 * which must be there for every primary contact defined
	 */
	private boolean isSessnProjectDefnd() {

		return isEtsProjectDefnd(getSessnProjInfo());

	}

	/**
	 * This method will return boolean, taking primary object and check for object attribute value,say full name
	 * which must be there for every primary contact defined
	 */
	private boolean isEtsProjectDefnd(ETSProj etsProj) {

		return (EtsIssFilterUtils.isObjectDefnd(etsProj) && EtsIssFilterUtils.isStringDefnd(etsProj.getProjectId()));

	}

	/**
	 * this method will get EtsAmtHfBean object
	 */

	public EtsAmtHfBean getIssueAmtHf() {

		return (EtsAmtHfBean) etsIssSessn.getSessionObjValue("etsamthf");

	}

	/**
	 * This method will set EtsAmtHfBean into session
	 */

	public void setIssueAmtHf(EtsAmtHfBean amtHf) {

		etsIssSessn.setSessionObjValue("etsamthf", amtHf);
	}

	/**
	 * This method will return boolean, taking amthf all param  and checks say linkid param of AmtHf object
	 */
	public boolean isIssueAmtHfDefnd() {

		if (getIssueAmtHf() != null) {

			return EtsIssFilterUtils.isStringDefnd(getIssueAmtHf().getLinkId());

		}

		return false;

	}

} //end of class
