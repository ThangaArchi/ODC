package oem.edge.ets.fe.ismgt.model;

import java.util.HashMap;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
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
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class EtsIssFilterObjectKey {

	public static final String VERSION = "1.10";

	private EdgeAccessCntrl es;
	private String projectId;
	private String problemType;
	private String issueSubType;
	private String opn;
	private int state;
	private String stateDesc;
	private String tc;
	private String cc;
	private String linkid;
	private HashMap propMap;

	private EtsFilterRepViewParamsBean repViewBean;
	private EtsFilterCondsViewParamsBean fcViewBean;

	private ETSProj proj;

	//	constructor params
	private HttpServletRequest request;

	//	optional set param
	private HttpServletResponse response;

	//form params//
	private Hashtable params;
	
	//sort state
	private int sortState;
	
	//filter operation (from where >> Back state)
	private int flopstate;
	
	//issue type issue/change
	private String istyp;
	
	//show issue owner
	private boolean showIssueOwner;
	
	//show user/roles matrix
	private EtsIssUserRolesModel usrRolesModel; 
	
	//proj is of blade type or not
	  private boolean projBladeType;
	  
	//proj is pmo enabled or not
	  private boolean projPmoEnabled;
	  
	//for PCR prop map
	  private HashMap pcrPropMap;
	

	/**
	 * Constructor for EtsIssFilterObjectKey.
	 */
	public EtsIssFilterObjectKey() {
		super();
	}

	/**
	 * Returns the es.
	 * @return EdgeAccessCntrl
	 */
	public EdgeAccessCntrl getEs() {
		return es;
	}

	/**
	 * Returns the opn.
	 * @return String
	 */
	public String getOpn() {
		return opn;
	}

	/**
	 * Returns the projectId.
	 * @return String
	 */
	public String getProjectId() {
		return projectId;
	}

	/**
	 * Returns the state.
	 * @return int
	 */
	public int getState() {
		return state;
	}

	/**
	 * Sets the es.
	 * @param es The es to set
	 */
	public void setEs(EdgeAccessCntrl es) {
		this.es = es;
	}

	/**
	 * Sets the opn.
	 * @param opn The opn to set
	 */
	public void setOpn(String opn) {
		this.opn = opn;
	}

	/**
	 * Sets the projectId.
	 * @param projectId The projectId to set
	 */
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	/**
	 * Sets the state.
	 * @param state The state to set
	 */
	public void setState(int state) {
		this.state = state;
	}

	/**
	 * Returns the problemType.
	 * @return String
	 */
	public String getProblemType() {
		return problemType;
	}

	/**
	 * Sets the problemType.
	 * @param problemType The problemType to set
	 */
	public void setProblemType(String problemType) {
		this.problemType = problemType;
	}

	/**
	 * Returns the cc.
	 * @return String
	 */
	public String getCc() {
		return cc;
	}

	/**
	 * Returns the linkid.
	 * @return String
	 */
	public String getLinkid() {
		return linkid;
	}

	/**
	 * Returns the tc.
	 * @return String
	 */
	public String getTc() {
		return tc;
	}

	/**
	 * Sets the cc.
	 * @param cc The cc to set
	 */
	public void setCc(String cc) {
		this.cc = cc;
	}

	/**
	 * Sets the linkid.
	 * @param linkid The linkid to set
	 */
	public void setLinkid(String linkid) {
		this.linkid = linkid;
	}

	/**
	 * Sets the tc.
	 * @param tc The tc to set
	 */
	public void setTc(String tc) {
		this.tc = tc;
	}

	/**
	 * Returns the issueSubType.
	 * @return String
	 */
	public String getIssueSubType() {
		return issueSubType;
	}

	/**
	 * Sets the issueSubType.
	 * @param issueSubType The issueSubType to set
	 */
	public void setIssueSubType(String issueSubType) {
		this.issueSubType = issueSubType;
	}

	/**
	 * Returns the stateDesc.
	 * @return String
	 */
	public String getStateDesc() {
		return stateDesc;
	}

	/**
	 * Sets the stateDesc.
	 * @param stateDesc The stateDesc to set
	 */
	public void setStateDesc(String stateDesc) {
		this.stateDesc = stateDesc;
	}

	/**
	 * Returns the fcViewBean.
	 * @return EtsFilterCondsViewParamsBean
	 */
	public EtsFilterCondsViewParamsBean getFcViewBean() {
		return fcViewBean;
	}

	/**
	 * Returns the repViewBean.
	 * @return EtsFilterRepViewParamsBean
	 */
	public EtsFilterRepViewParamsBean getRepViewBean() {
		return repViewBean;
	}

	/**
	 * Sets the fcViewBean.
	 * @param fcViewBean The fcViewBean to set
	 */
	public void setFcViewBean(EtsFilterCondsViewParamsBean fcViewBean) {
		this.fcViewBean = fcViewBean;
	}

	/**
	 * Sets the repViewBean.
	 * @param repViewBean The repViewBean to set
	 */
	public void setRepViewBean(EtsFilterRepViewParamsBean repViewBean) {
		this.repViewBean = repViewBean;
	}

	/**
	 * Returns the propMap.
	 * @return HashMap
	 */
	public HashMap getPropMap() {
		return propMap;
	}

	/**
	 * Sets the propMap.
	 * @param propMap The propMap to set
	 */
	public void setPropMap(HashMap propMap) {
		this.propMap = propMap;
	}

	/**
	 * @return
	 */
	public ETSProj getProj() {
		return proj;
	}

	/**
	 * @param proj
	 */
	public void setProj(ETSProj proj) {
		this.proj = proj;
	}

	/**
	 * @return
	 */
	public Hashtable getParams() {
		return params;
	}

	/**
	 * @return
	 */
	public HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * @return
	 */
	public HttpServletResponse getResponse() {
		return response;
	}

	/**
	 * @param hashtable
	 */
	public void setParams(Hashtable hashtable) {
		params = hashtable;
	}

	/**
	 * @param request
	 */
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * @param response
	 */
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	/**
	 * @return
	 */
	public int getSortState() {
		return sortState;
	}

	/**
	 * @param i
	 */
	public void setSortState(int i) {
		sortState = i;
	}

	/**
	 * @return
	 */
	public int getFlopstate() {
		return flopstate;
	}

	/**
	 * @param i
	 */
	public void setFlopstate(int i) {
		flopstate = i;
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

}
