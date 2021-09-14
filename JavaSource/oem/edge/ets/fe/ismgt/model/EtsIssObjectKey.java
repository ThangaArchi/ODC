package oem.edge.ets.fe.ismgt.model;

import java.sql.*;
import java.util.*;
import java.io.*;

import oem.edge.common.*;
import oem.edge.amt.*;

import javax.servlet.*;
import javax.servlet.http.*;

import oem.edge.ets.fe.ETSProj;
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
 * The basic class to represent the params paased by EtsIssueServlet to the subsequent classes
 * and the action key
 */
public class EtsIssObjectKey {

	public static final String VERSION = "1.11";

	//constructor params
	private HttpServletRequest request;
	private EdgeAccessCntrl es;
	private Hashtable params;
	private String sLink;
	private ETSProj proj;
	private int topCatId;

	//optional set param
	private HttpServletResponse response;

	//mandatory set params  in servlet
	private int filenum;
	private String subActionType;
	private String submitValue;
	private int actionkey;
	private String issueClass;
	private String issueAccess;

	////form label map//
	private HashMap formLabelMap;
	private ETSIssue currentIssue;

	//for prop map
	private HashMap propMap;

	//for PCR prop map
	private HashMap pcrPropMap;

	//filtered opn
	private String filopn;

	//issue type iss/chg
	private String istyp;

	//show issue owner
	private boolean showIssueOwner;
	
	//user/role model
	private EtsIssUserRolesModel usrRolesModel; 
	
	//proj is of blade type or not
	private boolean projBladeType;
	
	//proj is pmo enabled or not
	private boolean projPmoEnabled;

	/**
	 * Constructor for EtsIssObjectKey.
	 */
	public EtsIssObjectKey() {
		super();
	}

	/**
	 * Returns the actionkey.
	 * @return int
	 */
	public int getActionkey() {
		return actionkey;
	}

	/**
	 * Returns the es.
	 * @return EdgeAccessCntrl
	 */
	public EdgeAccessCntrl getEs() {
		return es;
	}

	/**
	 * Returns the filenum.
	 * @return int
	 */
	public int getFilenum() {
		return filenum;
	}

	/**
	 * Returns the params.
	 * @return Hashtable
	 */
	public Hashtable getParams() {
		return params;
	}

	/**
	 * Returns the proj.
	 * @return ETSProj
	 */
	public ETSProj getProj() {
		return proj;
	}

	/**
	 * Returns the request.
	 * @return HttpServletRequest
	 */
	public HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * Returns the sLink.
	 * @return String
	 */
	public String getSLink() {
		return sLink;
	}

	/**
	 * Returns the subActionType.
	 * @return String
	 */
	public String getSubActionType() {
		return subActionType;
	}

	/**
	 * Returns the submitValue.
	 * @return String
	 */
	public String getSubmitValue() {
		return submitValue;
	}

	/**
	 * Returns the topCatId.
	 * @return int
	 */
	public int getTopCatId() {
		return topCatId;
	}

	/**
	 * Sets the actionkey.
	 * @param actionkey The actionkey to set
	 */
	public void setActionkey(int actionkey) {
		this.actionkey = actionkey;
	}

	/**
	 * Sets the es.
	 * @param es The es to set
	 */
	public void setEs(EdgeAccessCntrl es) {
		this.es = es;
	}

	/**
	 * Sets the filenum.
	 * @param filenum The filenum to set
	 */
	public void setFilenum(int filenum) {
		this.filenum = filenum;
	}

	/**
	 * Sets the params.
	 * @param params The params to set
	 */
	public void setParams(Hashtable params) {
		this.params = params;
	}

	/**
	 * Sets the proj.
	 * @param proj The proj to set
	 */
	public void setProj(ETSProj proj) {
		this.proj = proj;
	}

	/**
	 * Sets the request.
	 * @param request The request to set
	 */
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * Sets the sLink.
	 * @param sLink The sLink to set
	 */
	public void setSLink(String sLink) {
		this.sLink = sLink;
	}

	/**
	 * Sets the subActionType.
	 * @param subActionType The subActionType to set
	 */
	public void setSubActionType(String subActionType) {
		this.subActionType = subActionType;
	}

	/**
	 * Sets the submitValue.
	 * @param submitValue The submitValue to set
	 */
	public void setSubmitValue(String submitValue) {
		this.submitValue = submitValue;
	}

	/**
	 * Sets the topCatId.
	 * @param topCatId The topCatId to set
	 */
	public void setTopCatId(int topCatId) {
		this.topCatId = topCatId;
	}

	/**
	 * Returns the response.
	 * @return HttpServletResponse
	 */
	public HttpServletResponse getResponse() {
		return response;
	}

	/**
	 * Sets the response.
	 * @param response The response to set
	 */
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	/**
	 * Returns the issueClass.
	 * @return String
	 */
	public String getIssueClass() {
		return issueClass;
	}

	/**
	 * Sets the issueClass.
	 * @param issueClass The issueClass to set
	 */
	public void setIssueClass(String issueClass) {
		this.issueClass = issueClass;
	}

	/**
	 * Returns the formLabelMap.
	 * @return HashMap
	 */
	public HashMap getFormLabelMap() {
		return formLabelMap;
	}

	/**
	 * Sets the formLabelMap.
	 * @param formLabelMap The formLabelMap to set
	 */
	public void setFormLabelMap(HashMap formLabelMap) {
		this.formLabelMap = formLabelMap;
	}

	/**
	 * Returns the issueAccess.
	 * @return String
	 */
	public String getIssueAccess() {
		return issueAccess;
	}

	/**
	 * Sets the issueAccess.
	 * @param issueAccess The issueAccess to set
	 */
	public void setIssueAccess(String issueAccess) {
		this.issueAccess = issueAccess;
	}

	/**
	 * Returns the currentIssue.
	 * @return ETSIssue
	 */
	public ETSIssue getCurrentIssue() {
		return currentIssue;
	}

	/**
	 * Sets the currentIssue.
	 * @param currentIssue The currentIssue to set
	 */
	public void setCurrentIssue(ETSIssue currentIssue) {
		this.currentIssue = currentIssue;
	}

	/**
	 * @return
	 */
	public HashMap getPropMap() {
		return propMap;
	}

	/**
	 * @param map
	 */
	public void setPropMap(HashMap map) {
		propMap = map;
	}

	/**
	 * @return
	 */
	public HashMap getPcrPropMap() {
		return pcrPropMap;
	}

	/**
	 * @param map
	 */
	public void setPcrPropMap(HashMap map) {
		pcrPropMap = map;
	}

	/**
	 * @return
	 */
	public String getFilopn() {
		return filopn;
	}

	/**
	 * @param string
	 */
	public void setFilopn(String string) {
		filopn = string;
	}

	/**
	 * @return
	 */
	public String getIstyp() {
		return istyp;
	}

	/**
	 * @param string
	 */
	public void setIstyp(String string) {
		istyp = string;
	}

	
	/**
	 * @return
	 */
	public boolean isShowIssueOwner() {
		return showIssueOwner;
	}

	/**
	 * @param b
	 */
	public void setShowIssueOwner(boolean b) {
		showIssueOwner = b;
	}

	/**
	 * @return
	 */
	public EtsIssUserRolesModel getUsrRolesModel() {
		return usrRolesModel;
	}

	/**
	 * @param model
	 */
	public void setUsrRolesModel(EtsIssUserRolesModel model) {
		usrRolesModel = model;
	}

	/**
	 * @return
	 */
	public boolean isProjBladeType() {
		return projBladeType;
	}

	/**
	 * @param b
	 */
	public void setProjBladeType(boolean b) {
		projBladeType = b;
	}

	/**
	 * @return
	 */
	public boolean isProjPmoEnabled() {
		return projPmoEnabled;
	}

	/**
	 * @param b
	 */
	public void setProjPmoEnabled(boolean b) {
		projPmoEnabled = b;
	}

}
