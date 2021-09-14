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

package oem.edge.ets.fe.documents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.ETSDocEditHistory;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.DocExpirationDate;
import oem.edge.ets.fe.documents.common.ETSDocFile;
import oem.edge.ets.fe.documents.common.Group;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.pmo.ETSPMOffice;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

/**
 * @author v2srikau
 */
public class BaseDocumentForm extends ActionForm {

	private String m_strDocAction;
	private String m_strFormContext;
	private Vector m_vtCategories;
	private Vector m_vtComments;
	private Vector m_vtDocuments;
	private ETSCat m_udCategory = new ETSCat();
	private ETSDoc m_udDocument = new ETSDoc();
	private String[] m_pdSubmitCategories;
	private String[] m_pdNotifyUsers;
	private String[] m_pdRestrictedUsers;
	private String[] m_pdRestrictedUsersEdit;

	private String m_strIBMOnly;

	private String m_strDocId;

	private DocExpirationDate m_udExpDate = new DocExpirationDate();

	private DocExpirationDate m_udStartDate = new DocExpirationDate();
	private DocExpirationDate m_udEndDate = new DocExpirationDate();
	private List m_lstAccessHistory = new ArrayList();

	private Vector m_vtUsers;
	private Vector m_vtIBMUsers;
	private Vector m_vtNotifyAllUsers;

	private ETSCat m_udParentCategory = new ETSCat();

	private String m_strSortBy;
	private String m_strSort;

	private String m_strPmoCat;
	private ETSPMOffice m_udPMOffice;

	private Vector m_vtBreadCrumbs;

	private String m_strTopCatId;
	private String m_strCurrentCatId;
	private String m_strProjectId;
	private String m_strLinkId;

	private String m_strChUsers;
	private String m_strChUsersEdit;

	private String m_strNotifyFlag;
	private String m_strNotifyOption;
	private String m_strAttachmentNotifyFlag;

	private String m_strDocComments;

	private FormFile m_pdUploadFile;

	private List m_lstUploadFiles = new ArrayList();
	private Vector m_vtAICTemplateList = new Vector();

	private String m_strEncodedToken;
	private String m_strTemplateId;

	private List m_lstGroups = new ArrayList();
	private List m_lstGroupsEdit = new ArrayList();

	private List m_lstSelectedGroups = new ArrayList();
	private List m_lstSelectedEditGroups = new ArrayList();
	private String[] m_pdNotifyGroups;
	private String[] m_pdNotifyGroupsEdit;
	private Vector m_vtUsersEdit;

	private String[] m_pdSelectedAttachments;
	private  ETSDocFile[] etsDocFile = new ETSDocFile[0];
	private String[] delDocFileIds;
	private String delDocAttachmentNames;
	//	Edit history variables

	private ETSDocEditHistory editDocHistory = null;
	private Vector editHistoryVector = null;
	private int m_intAllViewDocsCount = 0;
	private int m_intCurrentStartIndex = 0;

	// Add attachment variables
	
	private ArrayList uploadedFilesList = null;
	private ArrayList existingFilesList = null;
	private ArrayList errorFilesList = null;

	//Security classification variable
	
	private Vector m_vtIBMGroups = null;

	private Vector m_vtDocNotifyUsers = null;
	private List m_vtDocNotifyGroups = null;
	private List m_vtDocNotifyGroupsId = null;
	private String appType  = null;
	private String workflowID = null;
	/**
	 * @return Returns the appType.
	 */
	public String getAppType() {
		return appType;
	}
	
	/**
	 * @param appType The appType to set.
	 */
	public void setAppType(String appType) {
		this.appType = appType;
	}
	/**
	 * @return Returns the workflowID.
	 */
	public String getWorkflowID() {
		return workflowID;
	}
	/**
	 * @param workflowID The workflowID to set.
	 */
	public void setWorkflowID(String workflowID) {
		this.workflowID = workflowID;
	}

	public BaseDocumentForm() {
		m_strChUsers = "0";
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return Returns the editDocHistory.
	 */
	public ETSDocEditHistory getEditDocHistory()
	{
		return editDocHistory;
	}
	/**
	 * @param editDocHistory The editDocHistory to set.
	 */
	public void setEditDocHistory(ETSDocEditHistory editDocHistory)
	{
		this.editDocHistory = editDocHistory;
	}
	/**
	 * @return Returns the editHistoryVector.
	 */
	public Vector getEditHistoryVector() {
		return editHistoryVector;
	}
	/**
	 * @param editHistoryVector The editHistoryVector to set.
	 */
	public void setEditHistoryVector(Vector editHistoryVector)
	{
		this.editHistoryVector = editHistoryVector;
	}
	//end of edit history variables
	/**
	 * @return Returns the delDocFileId.
	 */
	public String[] getDelDocFileId() {
		return delDocFileIds;
	}
	/**
	 * @param delDocFileId The delDocFileId to set.
	 */
	public void setDelDocFileId(String delDocFileId) {
		StringTokenizer fileId = new StringTokenizer(delDocFileId,",");
		String delDocFileIDS[] = new String[fileId.countTokens()];
		int x=0;
		while(fileId.hasMoreTokens())
		{
			delDocFileIDS[x]= fileId.nextToken();
			x++;
		}
		this.delDocFileIds = delDocFileIDS;
	}

	/**
	 * @return Returns the etsDocFile.
	 */
	public ETSDocFile[] getETSDocAttachments() {
		return etsDocFile;
	}
	/**
	 * @param etsDocFile The etsDocFile to set.
	 */
	public void setEtsDocAttachments(Collection colObj) {
		try{
			etsDocFile = (ETSDocFile[]) colObj.toArray( new ETSDocFile[colObj.size()]);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * @return ETSCat
	 */
	public ETSCat getCategory() {
		return m_udCategory;
	}

	/**
	 * @param udCategory
	 */
	public void setCategory(ETSCat udCategory) {
		m_udCategory = udCategory;
	}

	/**
	 * @return ETSDoc
	 */
	public ETSDoc getDocument() {
		return m_udDocument;
	}

	/**
	 * @param udDocument
	 */
	public void setDocument(ETSDoc udDocument) {
		m_udDocument = udDocument;
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
	public String getPmoCat() {
		return m_strPmoCat;
	}

	/**
	 * @param strPmoCat
	 */
	public void setPmoCat(String strPmoCat) {
		m_strPmoCat = strPmoCat;
	}

	/**
	 * @return
	 */
	public String getCc() {
		if (StringUtil.isNullorEmpty(m_strCurrentCatId)) {
			return m_strTopCatId;
		}
		return m_strCurrentCatId;
	}

	/**
	 * @param strCurrentCatId
	 */
	public void setCc(String strCurrentCatId) {
		m_strCurrentCatId = strCurrentCatId;
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
	public ETSCat getParentCategory() {
		return m_udParentCategory;
	}

	/**
	 * @param udParentCategory
	 */
	public void setParentCategory(ETSCat udParentCategory) {
		m_udParentCategory = udParentCategory;
	}

	/**
	 * @return
	 */
	public Vector getCategories() {
		return m_vtCategories;
	}

	/**
	 * @return
	 */
	public Vector getDocuments() {
		return m_vtDocuments;
	}

	/**
	 * @return
	 */
	public String getFormContext() {
		return m_strFormContext;
	}

	/**
	 * @param pdCategories
	 */
	public void setCategories(Vector pdCategories) {
		m_vtCategories = pdCategories;
	}

	/**
	 * @param pdDocuments
	 */
	public void setDocuments(Vector pdDocuments) {
		m_vtDocuments = pdDocuments;
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
	public ETSPMOffice getPMOffice() {
		return m_udPMOffice;
	}

	/**
	 * @param udPMOffice
	 */
	public void setPMOffice(ETSPMOffice udPMOffice) {
		m_udPMOffice = udPMOffice;
	}

	/**
	 * @return
	 */
	public String[] getSubmitCategories() {
		return m_pdSubmitCategories;
	}

	/**
	 * @param pdSubmitCategories
	 */
	public void setSubmitCategories(String[] pdSubmitCategories) {
		m_pdSubmitCategories = pdSubmitCategories;
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
	public Vector getIBMUsers() {
		return m_vtIBMUsers;
	}

	/**
	 * @param vtIBMUsers
	 */
	public void setIBMUsers(Vector vtIBMUsers) {
		m_vtIBMUsers = vtIBMUsers;
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
	public String getChUsers() {
		return m_strChUsers;
	}

	/**
	 * @return
	 */
	public String getChUsersEdit() {
		return m_strChUsersEdit;
	}

	/**
	 * @param strChUsers
	 */
	public void setChUsers(String strChUsers) {
		m_strChUsers = strChUsers;
	}

	/**
	 * @param strChUsersEdit
	 */
	public void setChUsersEdit(String strChUsersEdit) {
		m_strChUsersEdit = strChUsersEdit;
	}

	/**
	 * @return
	 */
	public DocExpirationDate getExpDate() {
		return m_udExpDate;
	}

	/**
	 * @param udExpDate
	 */
	public void setExpDate(DocExpirationDate udExpDate) {
		m_udExpDate = udExpDate;
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
	public String getNotifyFlag() {
		return m_strNotifyFlag;
	}

	/**
	 * @return
	 */
	public String getAttachmentNotifyFlag() {
		return m_strAttachmentNotifyFlag;
	}

	/**
	 * @param strNotifyFlag
	 */
	public void setNotifyFlag(String strNotifyFlag) {
		m_strNotifyFlag = strNotifyFlag;
	}

	/**
	 * @param strNotifyFlag
	 */
	public void setAttachmentNotifyFlag(String strAttachmentNotifyFlag) {
		m_strAttachmentNotifyFlag = strAttachmentNotifyFlag;
	}

	/**
	 * @return
	 */
	public String getNotifyOption() {
		return m_strNotifyOption;
	}

	/**
	 * @param strNotifyOption
	 */
	public void setNotifyOption(String strNotifyOption) {
		m_strNotifyOption = strNotifyOption;
	}

	/**
	 * @return
	 */
	public String[] getNotifyUsers() {
		return m_pdNotifyUsers;
	}

	/**
	 * @param pdNotifyUsers
	 */
	public void setNotifyUsers(String[] pdNotifyUsers) {
		m_pdNotifyUsers = pdNotifyUsers;
	}

	/**
	 * @return
	 */
	public String[] getRestrictedUsers() {
		return m_pdRestrictedUsers;
	}

	/**
	 * @param pdRestrictedUsers
	 */
	public void setRestrictedUsers(String[] pdRestrictedUsers) {
		m_pdRestrictedUsers = pdRestrictedUsers;
	}

	/**
	 * @return
	 */
	public String[] getRestrictedUsersEdit() {
		return m_pdRestrictedUsersEdit;
	}

	/**
	 * @param pdRestrictedUsers
	 */
	public void setRestrictedUsersEdit(String[] pdRestrictedUsersEdit) {
		m_pdRestrictedUsersEdit = pdRestrictedUsersEdit;
	}

	/**
	 * @return
	 */
	public String getDocid() {
		return m_strDocId;
	}

	/**
	 * @param strDocId
	 */
	public void setDocid(String strDocId) {
		m_strDocId = strDocId;
	}

	/**
	 * @return
	 */
	public Vector getComments() {
		return m_vtComments;
	}

	/**
	 * @param vtComments
	 */
	public void setComments(Vector vtComments) {
		m_vtComments = vtComments;
	}

	/**
	 * @return
	 */
	public String getDocComments() {
		return m_strDocComments;
	}

	/**
	 * @param strDocComments
	 */
	public void setDocComments(String strDocComments) {
		m_strDocComments = strDocComments;
	}

	/**
	 * @return
	 */
	public List getUploadedFiles() {
		return m_lstUploadFiles;
	}

	/**
	 * @param iIndex
	 * @return
	 */
	public FormFile getUploadedFile(int iIndex) {
		if (m_lstUploadFiles.size() < (iIndex + 1)) {
			int iDiff = 1 + iIndex - m_lstUploadFiles.size();
			for (int iCounter = 0; iCounter < iDiff; iCounter++) {
				m_lstUploadFiles.add(m_pdUploadFile);
			}
		}

		return (FormFile) m_lstUploadFiles.get(iIndex);
	}

	/**
	 * @param iIndex
	 * @param udFormFile
	 */
	public void setUploadedFile(int iIndex, FormFile udFormFile) {
		if (m_lstUploadFiles.size() < (iIndex + 1)) {
			int iDiff = 1 + iIndex - m_lstUploadFiles.size();
			for (int iCounter = 0; iCounter < iDiff; iCounter++) {
				m_lstUploadFiles.add(m_pdUploadFile);
			}
		}
		m_lstUploadFiles.set(iIndex, udFormFile);
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
	public String getDocAction() {
		return m_strDocAction;
	}

	/**
	 * @param strAction
	 */
	public void setDocAction(String strDocAction) {
		m_strDocAction = strDocAction;
	}

	/**
	 * @return
	 */
	public Vector getAICTemplateList() {
		return m_vtAICTemplateList;
	}

	/**
	 * @param vector
	 */
	public void setAICTemplateList(Vector vtAICTemplateList) {
		m_vtAICTemplateList = vtAICTemplateList;
	}

	/**
	 * @return
	 */
	public String getTemplateId() {
		return m_strTemplateId;
	}

	/**
	 * @param string
	 */
	public void setTemplateId(String strTemplateId) {
		m_strTemplateId = strTemplateId;
	}

	/**
	 * @return
	 */
	public DocExpirationDate getEndDate() {
		return m_udEndDate;
	}

	/**
	 * @return
	 */
	public DocExpirationDate getStartDate() {
		return m_udStartDate;
	}

	/**
	 * @param udEndDate
	 */
	public void setEndDate(DocExpirationDate udEndDate) {
		m_udEndDate = udEndDate;
	}

	/**
	 * @param udEndDate
	 */
	public void setStartDate(DocExpirationDate udEndDate) {
		m_udStartDate = udEndDate;
	}

	/**
	 * @return
	 */
	public List getAccessHistory() {
		return m_lstAccessHistory;
	}

	/**
	 * @param lstAccessHistory
	 */
	public void setAccessHistory(List lstAccessHistory) {
		m_lstAccessHistory = lstAccessHistory;
	}

	/**
	 * @return
	 */
	public List getGroups() {
		return m_lstGroups;
	}

	/**
	 * @param lstGroups
	 */
	public void setGroups(List lstGroups) {
		m_lstGroups = lstGroups;
	}

	/**
	 * @return
	 */
	public List getSelectedGroups() {
		return m_lstSelectedGroups;
	}

	/**
	 * @param lstGroups
	 */
	public void setSelectedGroups(List lstGroups) {
		m_lstSelectedGroups = lstGroups;
	}

	/**
	 * @return
	 */
	public String[] getNotifyUsersWithoutGroups() {
		List lstUsers = new ArrayList();
		if (m_pdNotifyUsers != null && m_pdNotifyUsers.length > 0) {
			for (int i = 0; i < m_pdNotifyUsers.length; i++) {
				if (!m_pdNotifyUsers[i]
					.startsWith(DocConstants.GROUP_PREFIX)) {
					lstUsers.add(m_pdNotifyUsers[i]);
				}
			}
		}
		String[] strUsers = new String[lstUsers.size()];
		lstUsers.toArray(strUsers);
		return strUsers;
	}

	/**
	 * @return
	 */
	public String[] getNotifyGroups() {
		List lstUsers = new ArrayList();
		if (m_pdNotifyUsers != null && m_pdNotifyUsers.length > 0) {
			for (int i = 0; i < m_pdNotifyUsers.length; i++) {
				if (m_pdNotifyUsers[i].startsWith(DocConstants.GROUP_PREFIX)) {
					lstUsers.add(
						m_pdNotifyUsers[i].substring(
							DocConstants.GROUP_PREFIX_LENGTH));
				}
			}
		}
		String[] strUsers = new String[lstUsers.size()];
		lstUsers.toArray(strUsers);
		return strUsers;
	}

	/**
	 * @return
	 */
	public String[] getSelectedNotifyGroups() {
		return m_pdNotifyGroups;
	}

	/**
	 * @param pdNotifyGroups
	 */
	public void setSelectedNotifyGroups(String[] pdNotifyGroups) {
		m_pdNotifyGroups = pdNotifyGroups;
	}

	/**
	 * @return
	 */
	public String[] getRestrictedUsersWithoutGroups() {
		List lstUsers = new ArrayList();
		if (m_pdRestrictedUsers != null && m_pdRestrictedUsers.length > 0) {
			for (int i = 0; i < m_pdRestrictedUsers.length; i++) {
				if (!m_pdRestrictedUsers[i]
					.startsWith(DocConstants.GROUP_PREFIX)) {
					lstUsers.add(m_pdRestrictedUsers[i]);
				}
			}
		}
		String[] strUsers = new String[lstUsers.size()];
		lstUsers.toArray(strUsers);
		return strUsers;
	}

	/**
	 * @return
	 */
	public String[] getRestrictedGroups() {
		List lstUsers = new ArrayList();
		if (m_pdRestrictedUsers != null && m_pdRestrictedUsers.length > 0) {
			for (int i = 0; i < m_pdRestrictedUsers.length; i++) {
				if (m_pdRestrictedUsers[i]
					.startsWith(DocConstants.GROUP_PREFIX)) {
					lstUsers.add(
						m_pdRestrictedUsers[i].substring(
							DocConstants.GROUP_PREFIX_LENGTH));
				}
			}
		}
		String[] strUsers = new String[lstUsers.size()];
		lstUsers.toArray(strUsers);
		return strUsers;
	}

/////////////////////////////////////// additionalEditor[set() & get()] - START ////////////////////////////////

	/**
	 * @return
	 */
	public Vector getEditUsers() {
		return m_vtUsersEdit;
	}

	/**
	 * @param vtUsers
	 */
	public void setEditUsers(Vector vtUsersEdit) {
		m_vtUsersEdit = vtUsersEdit;
	}


	/**
	 * @return
	 */
	public List getGroupsEdit() {
		return m_lstGroupsEdit;
	}

	/**
	 * @param lstGroupsEdit
	 */
	public void setGroupsEdit(List lstGroupsEdit) {
		m_lstGroupsEdit = lstGroupsEdit;
	}

	/**
	 * @return
	 */
	public List getSelectedEditGroups() {
		return m_lstSelectedEditGroups;
	}

	/**
	 * @param lstGroupsEdit
	 */
	public void setSelectedEditGroups(List lstGroupsEdit) {
		m_lstSelectedEditGroups = lstGroupsEdit;
	}

	/**
	 * @return
	 */
	public String[] getNotifyUsersWithoutGroupsEdit() {
		List lstUsers = new ArrayList();
		if (m_pdNotifyUsers != null && m_pdNotifyUsers.length > 0) {
			for (int i = 0; i < m_pdNotifyUsers.length; i++) {
				if (!m_pdNotifyUsers[i]
					.startsWith(DocConstants.GROUP_PREFIX)) {
					lstUsers.add(m_pdNotifyUsers[i]);
				}
			}
		}
		String[] strUsers = new String[lstUsers.size()];
		lstUsers.toArray(strUsers);
		return strUsers;
	}

	/**
	 * @return
	 */
	public String[] getNotifyGroupsEdit() {
		List lstUsers = new ArrayList();
		if (m_pdNotifyUsers != null && m_pdNotifyUsers.length > 0) {
			for (int i = 0; i < m_pdNotifyUsers.length; i++) {
				if (m_pdNotifyUsers[i].startsWith(DocConstants.GROUP_PREFIX)) {
					lstUsers.add(
						m_pdNotifyUsers[i].substring(
							DocConstants.GROUP_PREFIX_LENGTH));
				}
			}
		}
		String[] strUsers = new String[lstUsers.size()];
		lstUsers.toArray(strUsers);
		return strUsers;
	}

	/**
	 * @return
	 */
	public String[] getSelectedNotifyGroupsEdit() {
		return m_pdNotifyGroupsEdit;
	}

	/**
	 * @param pdNotifyGroupsEdit
	 */
	public void setSelectedNotifyGroupsEdit(String[] pdNotifyGroupsEdit) {
		m_pdNotifyGroupsEdit = pdNotifyGroupsEdit;
	}

	/**
	 * @return
	 */
	public String[] getRestrictedUsersWithoutGroupsEdit() {
		List lstUsers = new ArrayList();
		if (m_pdRestrictedUsersEdit != null && m_pdRestrictedUsersEdit.length > 0) {
			for (int i = 0; i < m_pdRestrictedUsersEdit.length; i++) {
				if (!m_pdRestrictedUsersEdit[i]
					.startsWith(DocConstants.GROUP_PREFIX)) {
					lstUsers.add(m_pdRestrictedUsersEdit[i]);
				}
			}
		}
		String[] strUsers = new String[lstUsers.size()];
		lstUsers.toArray(strUsers);
		return strUsers;
	}

	/**
	 * @return
	 */
	public String[] getRestrictedGroupsEdit() {
		List lstUsers = new ArrayList();
		if (m_pdRestrictedUsersEdit != null && m_pdRestrictedUsersEdit.length > 0) {
			for (int i = 0; i < m_pdRestrictedUsersEdit.length; i++) {
				if (m_pdRestrictedUsersEdit[i]
					.startsWith(DocConstants.GROUP_PREFIX)) {
					lstUsers.add(
						m_pdRestrictedUsersEdit[i].substring(
							DocConstants.GROUP_PREFIX_LENGTH));
				}
			}
		}
		String[] strUsers = new String[lstUsers.size()];
		lstUsers.toArray(strUsers);
		return strUsers;
	}

	/**
	 * @return Returns the selectedAttachments.
	 */
	public String[] getSelectedAttachments() {
		return m_pdSelectedAttachments;
	}

	/**
	 * @param selectedAttachments The selectedAttachments to set.
	 */
	public void setSelectedAttachments(String[] selectedAttachments) {
		this.m_pdSelectedAttachments = selectedAttachments;
	}
/////////////////////////////////////// additionalEditor[set() - get()] - END ////////////////////////////////
	/**
	 * @return Returns the etsDocFile.
	 */
	public ETSDocFile[] getEtsDocFile() {
		return etsDocFile;
	}
	/**
	 * @param etsDocFile The etsDocFile to set.
	 */
	public void setEtsDocFile(ETSDocFile[] etsDocFile) {
		this.etsDocFile = etsDocFile;
	}
	/**
	 * @return Returns the delDocAttachmentNames.
	 */
	public String getDelDocAttachmentNames() {
		return delDocAttachmentNames;
	}
	/**
	 * @param delDocAttachmentNames The delDocAttachmentNames to set.
	 */
	public void setDelDocAttachmentNames(String delDocAttachmentNames) {
		this.delDocAttachmentNames = delDocAttachmentNames;
	}
	/**
	 * @return Returns the m_intAllViewDocsCount.
	 */
	public int getAllViewDocsCount() {
		return m_intAllViewDocsCount;
	}
	/**
	 * @param allViewDocsCount The m_intAllViewDocsCount to set.
	 */
	public void setAllViewDocsCount(int allViewDocsCount) {
		m_intAllViewDocsCount = allViewDocsCount;
	}
	/**
	 * @return Returns the m_intCurrentStartIndex.
	 */
	public int getCurrentStartIndex() {
		return m_intCurrentStartIndex;
	}
	/**
	 * @param currentStartIndex The m_intCurrentStartIndex to set.
	 */
	public void setCurrentStartIndex(int currentStartIndex) {
		m_intCurrentStartIndex = currentStartIndex;
	}

	/**
	 * @return Returns the uploadedFilesList.
	 */
	public ArrayList getUploadedFilesList() {
		return uploadedFilesList;
	}
	/**
	 * @param uploadedFilesList The uploadedFilesList to set.
	 */
	public void setUploadedFilesList(ArrayList uploadedFilesList) {
		this.uploadedFilesList = uploadedFilesList;
	}
	/**
	 * @return Returns the existingFilesList.
	 */
	public ArrayList getExistingFilesList() {
		return existingFilesList;
	}
	/**
	 * @param existingFilesList The existingFilesList to set.
	 */
	public void setExistingFilesList(ArrayList existingFilesList) {
		this.existingFilesList = existingFilesList;
	}

	
	
	/**
	 * @return Returns the errorFilesList.
	 */
	public ArrayList getErrorFilesList() {
		return errorFilesList;
	}
	/**
	 * @param existingFilesList The errorFilesList to set.
	 */
	public void setErrorFilesList(ArrayList errorFilesList) {
		this.errorFilesList = errorFilesList;
	}


	/**
	 * @param vtIBMGroups.
	 */
	public void setIBMGroups(Vector vtIBMGroups)
	{
		m_vtIBMGroups = vtIBMGroups;
	}
	/**
	 * @return vtIBMGroups.
	 */
	public Vector getIBMGroups()
	{
		return m_vtIBMGroups;
	}
	
	/**
	 * @param vtUsers
	 */
	public void setNotifyAllUsers(Vector vtNotifyAllUsers) {
		m_vtNotifyAllUsers = vtNotifyAllUsers;
	}

	/**
	 * @return
	 */
	public Vector getNotifyAllUsers() {
		return m_vtNotifyAllUsers;
	}

	
	/**
	 * @param vtUsers
	 */
	public void setDocNotifyUsers(Vector vtDocNotifyUsers) {
		m_vtDocNotifyUsers = vtDocNotifyUsers;
	}

	/**
	 * @return
	 */
	public Vector getDocNotifyUsers() {
		return m_vtDocNotifyUsers;
	}

	
	/**
	 * @param vtUsers
	 */
	public void setDocNotifyGroups(List ltDocNotifyGroups) {
		m_vtDocNotifyGroups = ltDocNotifyGroups;
	}

	/**
	 * @return
	 */
	public List getDocNotifyGroups() {
		return m_vtDocNotifyGroups;
	}


	
	/**
	 * @param vtUsers
	 */
	public void setDocNotifyGroupsId(List vtDocNotifyGroupsId) {
		m_vtDocNotifyGroupsId = vtDocNotifyGroupsId;
	}

	/**
	 * @return
	 */
	public List getDocNotifyGroupsId() {
		return m_vtDocNotifyGroupsId;
	}


	
	public Vector getNotifyAllUsers(char cDocNotifyFlag, List ltALLNotificationList) {
		Vector vtNotifyAllUsers = new Vector();
		if( (cDocNotifyFlag == DocConstants.TRUE_FLAG) && ( !ltALLNotificationList.isEmpty() )) {
			vtNotifyAllUsers = (Vector) ltALLNotificationList.get(0);
		}
		setNotifyAllUsers(vtNotifyAllUsers);
		return vtNotifyAllUsers;
	}
	
	
	public Vector getDocNotifyUsers(char cDocNotifyFlag, List ltALLNotificationList) {
		Vector vtDocNotifyUsers = new Vector();
		if( (cDocNotifyFlag == DocConstants.FALSE_FLAG) && ( !ltALLNotificationList.isEmpty() )) {
			vtDocNotifyUsers = (Vector) ltALLNotificationList.get(0);
		}
		setDocNotifyUsers(vtDocNotifyUsers);
		return vtDocNotifyUsers;
	}
	
	
	public List getDocNotifyGroups(char cDocNotifyFlag, List ltALLNotificationList) {
		List ltGroups = new ArrayList();
		if( (cDocNotifyFlag == DocConstants.FALSE_FLAG) && ( !ltALLNotificationList.isEmpty() )) {
			if(ltALLNotificationList.size() >= 2) {
				ltGroups = (List) ltALLNotificationList.get(1);
			}
		}
		setDocNotifyGroups(ltGroups);
		return ltGroups;
	}

	
}
