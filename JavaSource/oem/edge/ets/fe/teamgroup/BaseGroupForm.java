/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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

package oem.edge.ets.fe.teamgroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import oem.edge.ets.fe.ETSGroup;

import org.apache.struts.action.ActionForm;

/**
 * @author vishal
 */
public class BaseGroupForm extends ActionForm {

	private String m_strGroupAction;
	private String m_strFormContext;
	private Vector m_vtGroups;
	private ETSGroup m_udGroup = new ETSGroup();
	private String m_strIBMOnly;

	private String[] m_pdGroupIds;

	private Vector m_vtUsers;
	private Vector m_vtibmUsers;
	private Vector m_vtGroupList;

	private String m_strSortBy;
	private String m_strSort;
	private String m_udCategory;
	private String m_strTopCatId;
	private Vector m_vtBreadCrumbs;

	private String m_strProjectId;
	private String m_strLinkId;

	private String m_strEncodedToken;
	
	private List m_lstGroupMembers = new ArrayList();
	private List m_lstAvailableMembers = new ArrayList();
	
	private String m_strGrpSelect; 
	private String m_strDisableAddModifyButton;
	
	// added for Delete grps functionality
	private boolean m_bGrpDependentNotification;
	private boolean m_bGrpDependentDocuments = false;
	private Vector m_vtDocs;
	private int m_intDocumentsTopCatId;

	/**
	 * @return String m_strGrpSelect
	 */
	public String getGrpSelect() {
		return m_strGrpSelect;
	}
	/**
	 * @param strGrpSelect
	 */
	public void setGrpSelect(String strGrpSelect) {
		m_strGrpSelect = strGrpSelect;
	}
	/**
	 * @return ETSGroup
	 */
	public ETSGroup getGroup() {
		return m_udGroup;
	}

	/**
	 * @param udDocument
	 */
	public void setGroup(ETSGroup udGroup) {
		m_udGroup = udGroup;
	}
	/**
	 * @return
	 */
	public String getSort() {
		return m_strSort;
	}

	/**
	 * @return
	 */
	public String getSortBy() {
		return m_strSortBy;
	}

	/**
	 * @param strSort
	 */
	public void setSort(String strSort) {
		m_strSort = strSort;
	}

	/**
	 * @param strSortBy
	 */
	public void setSortBy(String strSortBy) {
		m_strSortBy = strSortBy;
	}


	/**
	 * @return
	 */
	public String getTc() {
		return m_strTopCatId;
	}

	/**
	 * @param strTopCatId
	 */
	public void setTc(String strTopCatId) {
		m_strTopCatId = strTopCatId;
	}

	/**
	 * @return
	 */
	public Vector getBreadCrumbs() {
		return m_vtBreadCrumbs;
	}

	/**
	 * @param vtBreadCrumbs
	 */
	public void setBreadCrumbs(Vector vtBreadCrumbs) {
		m_vtBreadCrumbs = vtBreadCrumbs;
	}

	/**
	 * @return
	 */
	public Vector getGroupList() {
		return m_vtGroupList;
	}

	/**
	 * @return
	 */
	public String getFormContext() {
		return m_strFormContext;
	}

	/**
	 * @param pdDocuments
	 */
	public void setGroupList(Vector pdGroupList) {
		m_vtGroupList = pdGroupList;
	}

	/**
	 * @param strFormContext
	 */
	public void setFormContext(String strFormContext) {
		m_strFormContext = strFormContext;
	}

	/**
	 * @return
	 */
	public Vector getUsers() {
		return m_vtUsers;
	}

	/**
	 * @param vtUsers
	 */
	public void setUsers(Vector vtUsers) {
		m_vtUsers = vtUsers;
	}

	/**
	 * @return
	 */
	public Vector getIbmUsers() {
		return m_vtibmUsers;
	}

	/**
	 * @param vtIBMUsers
	 */
	public void setIbmUsers(Vector vtIBMUsers) {
		m_vtibmUsers = vtIBMUsers;
	}

	/**
	 * @return
	 */
	public String getIBMOnly() {
		return m_strIBMOnly;
	}

	/**
	 * @param strIBMOnly
	 */
	public void setIBMOnly(String strIBMOnly) {
		m_strIBMOnly = strIBMOnly;
	}

	/**
	 * @return
	 */
	public String getLinkid() {
		return m_strLinkId;
	}

	/**
	 * @return
	 */
	public String getProj() {
		return m_strProjectId;
	}

	/**
	 * @param strLinkId
	 */
	public void setLinkid(String strLinkId) {
		m_strLinkId = strLinkId;
	}

	/**
	 * @param strProjectId
	 */
	public void setProj(String strProjectId) {
		m_strProjectId = strProjectId;
	}

	/**
	 * @return
	 */
	public String getEncodedToken() {
		return m_strEncodedToken;
	}

	/**
	 * @param string
	 */
	public void setEncodedToken(String strEncodedToken) {
		m_strEncodedToken = strEncodedToken;
	}

	/**
	 * @return
	 */
	public String getGroupAction() {
		return m_strGroupAction;
	}

	/**
	 * @param strAction
	 */
	public void setGroupAction(String strGroupAction) {
		m_strGroupAction = strGroupAction;
	}

	/**
	 * @return
	 */
	public String[] getGroupIds() {
		return m_pdGroupIds;
	}

	/**
	 * @param pdNotifyUsers
	 */
	public void setGroupIds(String[] pdGroupIds) {
		this.m_pdGroupIds = pdGroupIds;
	}
	
	public void setGroupMembersList(List lstGroupMembers) {
		this.m_lstGroupMembers = lstGroupMembers;
	}

	public List getGroupMembersList() {
		return m_lstGroupMembers;
	}

	public void setAvailableMembersList(List lstAvailableMembers) {
		this.m_lstAvailableMembers = lstAvailableMembers;
	}
	public List getAvailableMembersList() {
		return m_lstAvailableMembers;
	}

	/**
	 * @return
	 */
	public String getDisableButtons() {
		return m_strDisableAddModifyButton;
	}

	/**
	 * @param string
	 */
	public void setDisableButtons(String strDisable) {
		this.m_strDisableAddModifyButton = strDisable;
	}


	/**
	 * @return Returns the m_bGrpDependentNotification.
	 */
	public boolean getGrpDependentNotification() {
		return m_bGrpDependentNotification;
	}
	/**
	 * @param grpDependentNotification The m_bGrpDependentNotification to set.
	 */
	public void setGrpDependentNotification(boolean grpDependentNotification) {
		m_bGrpDependentNotification = grpDependentNotification;
	}
	/**
	 * @return Returns the m_vtDocs.
	 */
	public Vector getDocs() {
		return m_vtDocs;
	}
	/**
	 * @param docs The m_vtDocs to set.
	 */
	public void setDocs(Vector docs) {
		m_vtDocs = docs;
	}
	/**
	 * @return Returns the m_intDocumentsTopCatId.
	 */
	public int getDocumentsTopCatId() {
		return m_intDocumentsTopCatId;
	}
	/**
	 * @param documentsTopCatId The m_intDocumentsTopCatId to set.
	 */
	public void setDocumentsTopCatId(int documentsTopCatId) {
		m_intDocumentsTopCatId = documentsTopCatId;
	}
	/**
	 * @return Returns the m_bGrpDependentDocuments.
	 */
	public boolean getGrpDependentDocuments() {
		return m_bGrpDependentDocuments;
	}
	/**
	 * @param grpDependentDocuments The m_bGrpDependentDocuments to set.
	 */
	public void setGrpDependentDocuments(boolean grpDependentDocuments) {
		m_bGrpDependentDocuments = grpDependentDocuments;
	}
}
