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


import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.Metrics;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.ETSDocEditHistory;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.aic.common.util.AICUtil;
import oem.edge.ets.fe.aic.dyntab.bdlg.AICDynTabTableBusinessDelegate;
import oem.edge.ets.fe.aic.dyntab.vo.AICTableVO;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.DocExpirationDate;
import oem.edge.ets.fe.documents.common.Group;
import oem.edge.ets.fe.documents.common.MetricsConstants;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.documents.data.DocReaderDAO;
import oem.edge.ets.fe.documents.data.DocumentDAO;

//added by govind
import oem.edge.ets.fe.workflow.setmet.validate.ValidateDocumentStageDAO;
//addition ends

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;

/**
 * @author v2srikau
 */
public class AddEditDocumentAction extends BaseDocumentAction {

	/** Stores the Logging object */
	private static final Log m_pdLog =
		EtsLogger.getLogger(AddEditDocumentAction.class);

	private static final String ERR_DOC_NAME = "doc.name.error";
	private static final String ERR_DOC_DESC = "doc.description.error";
	private static final String ERR_DOC_KEYWORDS = "doc.keywords.error";
	private static final String ERR_DOC_EXPIRATION_DATE = "doc.expdate.error";
	private static final String ERR_FILE_EMPTY = "doc.file.empty.error";
	private static final String ERR_FILE_SIZE = "doc.file.size.error";
	private static final String ERR_DOC_EXPIRATION_DATE_PAST =
		"doc.expdate.past.error";
	private static final String ERR_SEC_NOTSEL =
		"security.classification.error";
	private static final String ERR_DUP_DOCNAME = "doc.name.duplicate";
	private static final String ERR_WORKFLOW_DUP_DOCNAME="workflow.doc.name.duplicate";
	private static final String ERR_FILE_ADD = "doc.file.add.error";
	private static final String ERR_FILE_ADD_INVALID_FILE = "doc.file.addInvalidFile.error";
	
	/**
	 * @see oem.edge.ets.fe.documents.BaseDocumentAction#executeAction(
	 * org.apache.struts.action.ActionMapping, 
	 * org.apache.struts.action.ActionForm, 
	 * javax.servlet.http.HttpServletRequest, 
	 * javax.servlet.http.HttpServletResponse)
	 */
	protected ActionForward executeAction(
		ActionMapping pdMapping,
		ActionForm pdForm,
		HttpServletRequest pdRequest,
		HttpServletResponse pdResponse)
		throws Exception {

	    String strDocumentDesc = pdRequest.getParameter("description_contents");
	    // Since all doc descriptions will now be HTML, we don't need to preserve new lines
	    strDocumentDesc = StringUtil.removeLineBreaks(strDocumentDesc);
	    
		BaseDocumentForm udForm = (BaseDocumentForm) pdForm;

//added by govind

		String appType= (String)pdRequest.getParameter("appType");
		String workflowID = (String)pdRequest.getParameter("workflowID");
//addition ends

		String strAction = udForm.getDocAction();
		
		ETSDocEditHistory objETSDocEditHistory = new ETSDocEditHistory();
		ETSDoc udDoc = udForm.getDocument();
		udDoc.setDescription(strDocumentDesc);

		DocExpirationDate udExpDate = udForm.getExpDate();
		String strExpires = udExpDate.getExpires();
		String strFormContext = udForm.getDocAction();
		int iOldDocID = -1;
		boolean bIsUpdateDoc =
			DocConstants.ACTION_UPDATE_DOC.equals(strFormContext)
				|| DocConstants.ACTION_UPDATE_DOC_PROPS.equals(strFormContext);
		boolean bIsUpdateDocProp =
			DocConstants.ACTION_UPDATE_DOC_PROPS.equals(strFormContext);

		if (bIsUpdateDoc && !StringUtil.isNullorEmpty(udForm.getDocid())) {
			iOldDocID = Integer.parseInt(udForm.getDocid());
		}

		DocumentDAO udDAO = null;
		try {
			String strProjectID = DocumentsHelper.getProjectID(pdRequest);

			udDAO = getDAO();
			ETSProj udProject = udDAO.getProjectDetails(strProjectID);

			ActionErrors pdErrors = null;

			pdErrors =
				validate(
					udDAO,
					udForm,
					bIsUpdateDocProp,
					bIsUpdateDoc,
					udProject.isITAR());

			if (pdErrors.size() > 0) {
				throw new DocumentException(pdErrors);
			}

			String action1 = "adddoc";
			int iParentID =
				Integer.parseInt(DocumentsHelper.getCurrentCatID(pdRequest));
			int iDocType = Defines.DOC;
			String strMeetingID = StringUtil.EMPTY_STRING;
			String strRepeatID = StringUtil.EMPTY_STRING;
			String strCVID = StringUtil.EMPTY_STRING;
			String strMeetingAction = "action=" + action1;
			String strOptions = StringUtil.EMPTY_STRING;
			udDoc.setProjectId(strProjectID);
			udDoc.setCatId(iParentID);
			udDoc.setDocType(iDocType);
			udDoc.setSelfId(strCVID);

			if (strRepeatID.trim().equals(StringUtil.EMPTY_STRING)) {
				udDoc.setMeetingId(strMeetingID);
			} else {
				udDoc.setMeetingId(strRepeatID);
			}
			if (strOptions.equals(String.valueOf(Defines.DOC_DRAFT))) {
				udDoc.setDocStatus(Defines.DOC_DRAFT);
			} else if (
				strOptions.equals(String.valueOf(Defines.DOC_SUB_APP))) {
				udDoc.setDocStatus(Defines.DOC_SUB_APP);
			} else {
				udDoc.setDocStatus(StringUtil.EMPTY_STRING);
			}

			if (!StringUtil.isNullorEmpty(strExpires)) {
				udDoc.setExpiryDate(
					udExpDate.getMonth(),
					udExpDate.getDay(),
					udExpDate.getYear());
			} else {
				udDoc.setExpiryDate(new Timestamp(0));
			}

			EdgeAccessCntrl udEdgeAccess = DocumentsHelper.getEdgeAccess(pdRequest);
			udDoc.setUserId(udEdgeAccess.gIR_USERN);

			if(udForm.getChUsers().equalsIgnoreCase(DocConstants.DOC_RESTRICTED))
				udDoc.setDPrivate(true);
			else if(udForm.getChUsers().equals(DocConstants.DOC_UNRESTRICTED))
				udDoc.setDPrivate(false);
			
/* ----------------------------------------------------- */	
			if (!StringUtil.isNullorEmpty(udForm.getRestrictedUsersEdit())) {
				udDoc.setDPrivateEdit(true);
			} else {
				udDoc.setDPrivateEdit(false);
			}
/* ----------------------------------------------------- */	

			if (bIsUpdateDoc || bIsUpdateDocProp) {
				udDoc.setId(Integer.parseInt(udForm.getDocid()));
			}

			boolean bSuccess = true;
			if (!bIsUpdateDocProp) {
				if (bIsUpdateDoc) {
				    ETSDoc udOldDoc = udDAO.getDocByIdAndProject(iOldDocID, strProjectID);
				    if (udOldDoc != null) {
				        udDoc.setUserId(udOldDoc.getUserId());
				        udDoc.setUpdatedBy(udEdgeAccess.gIR_USERN);
				    }
				}
				bSuccess =
					udDAO.addDocMethod(
						udDoc, udForm.getUploadedFiles(), iOldDocID, udProject.isITAR());
				udDoc = udDAO.getDocByIdAndProject(udDoc.getId(), strProjectID, udProject.isITAR());

				// The previous statement will set empty additional editors, by default.
				// So, the below statements will check and set the actual additional editor status. 
				if (!StringUtil.isNullorEmpty(udForm.getRestrictedUsersEdit())) {
					udDoc.setDPrivateEdit(true);
				} else {
					udDoc.setDPrivateEdit(false);
				}
				
				if (!StringUtil.isNullorEmpty(udForm.getTemplateId())) {
				AICTableVO udAICTable = new AICTableVO();
				udAICTable.setTableId(AICUtil.getUniqueId());
				udAICTable.setTableName(udDoc.getName());
				udAICTable.setDocId(udDoc.getId());
				udAICTable.setTemplateId(udForm.getTemplateId());
				AICDynTabTableBusinessDelegate udAicDelegate =
					new AICDynTabTableBusinessDelegate();
				udAicDelegate.createTableFromTemplate(udAICTable);
				}
				udDoc.setUploadDate();
			}

			if (bSuccess) {
				String[] strNotifyUsers = udForm.getNotifyUsers();
				String[] strRestrictedUsers = udForm.getRestrictedUsersWithoutGroups();
				boolean bIsRestrictedSelected = udDoc.isReadRestricted();
				Vector vtResUsers = new Vector();

/* ---------------------------------------------------- */
				String[] strRestrictedUsersEdit = udForm.getRestrictedUsersWithoutGroupsEdit();
				boolean bIsRestrictedSelectedEdit = !StringUtil.isNullorEmpty(udForm.getRestrictedUsersEdit());
				Vector vtResUsersEdit = new Vector();
 /* --------------------------------------------------- */				
				
				Vector vtIBMMembers =
					udDAO.getIBMMembers(udDAO.getProjMembers(strProjectID));
				Vector vtMembers = new Vector();	 
				Vector vtMembersEdit = new Vector(); 
				
				if (udDoc.isIbmOnlyOrConf()) {
					for (int iCounter = 0; iCounter < vtIBMMembers.size(); iCounter++) {
						ETSUser udUser = (ETSUser) vtIBMMembers.get(iCounter);
						vtMembers.add(udUser.getUserId());
					}
				}
				vtMembersEdit = vtMembers;  // to check ibm member with Edit Access 
				if (bIsRestrictedSelected && strRestrictedUsers != null) {
					for (int iCounter = 0; iCounter < strRestrictedUsers.length; iCounter++) {
						String strRestrictedUser = strRestrictedUsers[iCounter];
						if (udDoc.isIbmOnlyOrConf()) {
							if (vtMembers.contains(strRestrictedUser)) {
								vtResUsers.add(strRestrictedUser);
							}
						} else {
							vtResUsers.add(strRestrictedUsers[iCounter]);
						}
					}

					// Means document has to be restricted to these users
					String strTmpDocID =
						(new Integer(udDoc.getId())).toString();
					if (!bIsUpdateDocProp) {
						udDAO.addDocResUsers(
							vtResUsers,
							strTmpDocID,
							strProjectID);
					}

				}

/////////////////////////////////////////////////////////////////////////////////////////////
				if (bIsRestrictedSelectedEdit && strRestrictedUsersEdit != null) {
					for (int iCounter = 0; iCounter < strRestrictedUsersEdit.length; iCounter++) {
						String strRestrictedUserEdit = strRestrictedUsersEdit[iCounter];
						if (udDoc.isIbmOnlyOrConf()) {
							if (vtMembersEdit.contains(strRestrictedUserEdit)) {
								vtResUsersEdit.add(strRestrictedUserEdit);
							}
						} else {
							vtResUsersEdit.add(strRestrictedUsersEdit[iCounter]);
						}
					}

					// Means document has to be restricted to these users
					String strTmpDocID = (new Integer(udDoc.getId())).toString();
					if (!bIsUpdateDocProp) {
						udDAO.addAdditionalEditors(
							vtResUsersEdit,		strTmpDocID,		strProjectID);
					}

				}
/////////////////////////////////////////////////////////////////////////////////////////////
				
				// Do the same for Groups as well
				String []strRestrictedGroups = udForm.getRestrictedGroups();
				if (bIsRestrictedSelected && strRestrictedGroups != null) {
					if (!bIsUpdateDocProp) {
						udDAO.addDocResGroups(
							strRestrictedGroups,
							udDoc.getId(),
							strProjectID);
					}
/*for(int iCounter=0; iCounter < strRestrictedGroups.length; iCounter++) {
    String strEachGroup = strRestrictedGroups[iCounter];
    List lstUsers = udDAO.getGroupUsers(strEachGroup);
    for(int i=0; i< lstUsers.size(); i++) {
        String strUserId = (String) lstUsers.get(i);
		if (vtResUsers.contains(strUserId)) {
		    continue;
		}
		else {
	        if (udDoc.isIbmOnlyOrConf()) {
				if (vtMembers.contains(strUserId)) {
					vtResUsers.add(strUserId);
				}
			} else {
				vtResUsers.add(strUserId);
			}
		}
    }
} */
				}
				
/* ---------------------------------------------------------------------------- */
				
				// Do the same for EDIT ACCESS Groups also
				String []strRestrictedGroupsEdit = udForm.getRestrictedGroupsEdit();
				if (bIsRestrictedSelectedEdit && strRestrictedGroupsEdit != null) {
					if (!bIsUpdateDocProp) {
						udDAO.addDocResGroupsEdit(
							strRestrictedGroupsEdit,
							udDoc.getId(),
							strProjectID);
					}
					
/*for(int iCounter=0; iCounter < strRestrictedGroupsEdit.length; iCounter++) {
    String strEachGroup = strRestrictedGroupsEdit[iCounter];
    List lstUsers = udDAO.getGroupUsersEdit(strEachGroup);

    for(int i=0; i< lstUsers.size(); i++) {
        String strUserId = (String) lstUsers.get(i);
		if (vtResUsersEdit.contains(strUserId)) {
		    continue;
		}
		else {
	        if (udDoc.isIbmOnlyOrConf()) {
				if (vtMembers.contains(strUserId)) {
					vtResUsersEdit.add(strUserId);
				}
			} else {
				vtResUsersEdit.add(strUserId);
			}
		}
    }
} */
				}
/* ---------------------------------------------------------------------------- */

				// IF THIS IS UPLOAD A NEW VERSION, THE RESTRICTED USER LIST
				// FOR THE OLD VERSION SHOULD ALSO BE CHANGED
				if (bIsUpdateDoc) {
					udDoc.setUpdatedBy(udEdgeAccess.gIR_USERN);
					String strAdd = StringUtil.EMPTY_STRING;
					String strRemove = StringUtil.EMPTY_STRING;

					String strAddGroups = StringUtil.EMPTY_STRING;
					String strRemoveGroups = StringUtil.EMPTY_STRING;

					Vector vtAdd = new Vector();
					Vector vtAddGroups = new Vector();

////////////////////////////////////////////////////////////////////////////
					String strAddEdit = StringUtil.EMPTY_STRING;
					String strRemoveEdit = StringUtil.EMPTY_STRING;
					String strAddGroupsEdit = StringUtil.EMPTY_STRING;
					String strRemoveGroupsEdit = StringUtil.EMPTY_STRING;
					Vector vtAddEdit = new Vector();
					Vector vtAddGroupsEdit = new Vector();
////////////////////////////////////////////////////////////////////////////
					
					ETSDoc udOldDoc = udDAO.getDocByIdAndProject(iOldDocID, strProjectID);
					
					boolean bChangeRes = !udOldDoc.getDPrivate().equals(udDoc.getDPrivate());
					boolean bChangeResEdit = !udOldDoc.getDPrivateEdit().equals(udDoc.getDPrivateEdit());

			if (bChangeRes || udDoc.IsDPrivate()) {
				if (bChangeRes && !udDoc.IsDPrivate()) { //remove all
					Vector vtRes = udDAO.getRestrictedProjMemberIds(strProjectID,		iOldDocID,		false);
						for (int i = 0; i < vtRes.size(); i++) {
							if (i != 0) {
							strRemove =	strRemove + ",'"+ vtRes.elementAt(i) + "'";
						} else {
								strRemove = "'" + vtRes.elementAt(i) + "'";
							}
						}
				
						// Do the same for groups
						List lstResGrp = udDAO.getAllDocRestrictedGroupIds(iOldDocID);
						for (int i = 0; i < lstResGrp.size(); i++) {
							if (i != 0) {
								strRemoveGroups = strRemoveGroups+ ",'" +((Group) lstResGrp.get(i)).getGroupId()+ "'";
							} else {
								strRemoveGroups = "'"+ ((Group) lstResGrp.get(i)).getGroupId()+ "'";
						}
					}
				} else if (	bChangeRes && udDoc.IsDPrivate() ) { //add all
						for (int i = 0; i < vtResUsers.size(); i++) {
							if (i != 0)
								strAdd = strAdd	+ ",(" + udDoc.getId() + ",'" + vtResUsers.elementAt(i)	
												+ "','" + strProjectID + "','"+ Defines.DOC_READ_ACCESS +"')";
							else
								strAdd = strAdd	+ "(" + udDoc.getId() + ",'" + vtResUsers.elementAt(i) 
												+ "','"	+ strProjectID + "','"+ Defines.DOC_READ_ACCESS +"')";
							vtAdd.addElement(vtResUsers.elementAt(i));
						}
				
						// Do the same for Groups
						for (int i = 0; i < strRestrictedGroups.length; i++) {
					    	if (i != 0)
					    		strAddGroups = strAddGroups	+ ",(" + udDoc.getId() + ",'" 
													+ strRestrictedGroups[i] + "','" + strProjectID + "','"+ Defines.DOC_READ_ACCESS +"')";
					    	else
					    		strAddGroups = strAddGroups	+ "(" + udDoc.getId() + ",'"
													+ strRestrictedGroups[i] + "','" + strProjectID + "','"+ Defines.DOC_READ_ACCESS +"')";
				    	vtAddGroups.addElement(strRestrictedGroups[i]);
					}
				} else if (!bChangeRes && udDoc.IsDPrivate()) {
				
					//add and remove
					Vector vtAllUsers = udDAO.getProjMembers(strProjectID);
					Vector vtRes = udDAO.getRestrictedProjMemberIds( strProjectID, 	iOldDocID,	false);
					
					for (int iCounter = 0;		iCounter < vtAllUsers.size();		iCounter++) {
						String strUserId =	((ETSUser) vtAllUsers.elementAt(iCounter)).getUserId();
						if (vtRes.contains(strUserId) && !vtResUsers.contains(strUserId)) {
							if (!strRemove.equals(StringUtil.EMPTY_STRING))
								strRemove = strRemove + ",'" + strUserId + "'";
							else
								strRemove = "'" + strUserId + "'";
						} else if (	!vtRes.contains(strUserId) && vtResUsers.contains(strUserId)) {
							if (!strAdd.equals(StringUtil.EMPTY_STRING)) 
								strAdd = strAdd	+ ",(" + udDoc.getId() + ",'" + strUserId + "','" + strProjectID  + "','"+ Defines.DOC_READ_ACCESS +"')";
							else
								strAdd = strAdd + "("+ udDoc.getId() + ",'"	+ strUserId + "','"	+ strProjectID + "','"+ Defines.DOC_READ_ACCESS +"')";
							vtAdd.addElement(strUserId);
							}
						}
				
						// do the same for groups
						List lstOldGroups = udDAO.getAllDocRestrictedGroupIds(iOldDocID);
						List lstOldGrpIds = new ArrayList();
						for (int i = 0; i < lstOldGroups.size(); i++) {
							String strGroupId = ((Group) lstOldGroups.get(i)).getGroupId();
							lstOldGrpIds.add(strGroupId);
						}
						List lstNewGrpIds = new ArrayList();
						for (int i = 0; i < strRestrictedGroups.length; i++)
								lstNewGrpIds.add(strRestrictedGroups[i]);
						
						for(int i=0; i < lstOldGrpIds.size(); i++) {
							String strGroupId = (String) lstOldGrpIds.get(i);
							if (!lstNewGrpIds.contains(strGroupId)) {
								if (!strRemoveGroups.equals(StringUtil.EMPTY_STRING))
									strRemoveGroups = strRemoveGroups + ",'"+ strGroupId	+ "'";
								else
									strRemoveGroups = "'" + strGroupId + "'";
							}
						}
						for(int i=0; i < lstNewGrpIds.size(); i++) {
							String strGroupId = (String) lstNewGrpIds.get(i);
							if (!lstOldGrpIds.contains(strGroupId)) {
								if (!strAddGroups.equals(StringUtil.EMPTY_STRING))
									strAddGroups =	strAddGroups + ",(" + udDoc.getId()	+ ",'"
														+ strGroupId + "','"
														+ strProjectID + "','"
														+ Defines.DOC_READ_ACCESS +"')";
								else
									strAddGroups = strAddGroups + "(" + udDoc.getId() + ",'"
														+ strGroupId + "','"
														+ strProjectID + "','"
														+ Defines.DOC_READ_ACCESS + "')";
								vtAddGroups.addElement(strGroupId);
							}
						}
								
					}
				}
			
//////////////////////////////  START [UPDATE PROPERTIES-EDIT] CHANGES //////////////////////////////////////
			if ( bChangeResEdit || udDoc.IsDPrivateEdit() ) {
				if ( bChangeResEdit && !udDoc.IsDPrivateEdit() ) { //remove all
					Vector vtResEdit2 = udDAO.getRestrictedProjMemberIdsEdit(strProjectID,		iOldDocID,		false);
						for (int i = 0; i < vtResEdit2.size(); i++) {
							if (i != 0) {
								strRemoveEdit =	strRemoveEdit + ",'"+ vtResEdit2.elementAt(i) + "'";
						} else {
								strRemoveEdit = "'" + vtResEdit2.elementAt(i) + "'";
							}
						}
				
						// Do the same for EDIT groups
						List lstResGrpEdit = udDAO.getAllDocRestrictedEditGroupIds(iOldDocID);
						for (int i = 0; i < lstResGrpEdit.size(); i++) {
							if (i != 0) {
								strRemoveGroupsEdit = strRemoveGroupsEdit+ ",'" +((Group) lstResGrpEdit.get(i)).getGroupId()+ "'";
							} else {
								strRemoveGroupsEdit = "'"+ ((Group) lstResGrpEdit.get(i)).getGroupId()+ "'";
						}
					}
				} else if (	bChangeResEdit && udDoc.IsDPrivateEdit() ) { //add all
						for (int i = 0; i < vtResUsersEdit.size(); i++) {
							if (i != 0)
								strAddEdit = strAddEdit	+ ",(" + udDoc.getId() + ",'" + vtResUsersEdit.elementAt(i)	
												+ "','" + strProjectID + "','"+ Defines.DOC_EDIT_ACCESS +"')";
							else
								strAddEdit = strAddEdit	+ "(" + udDoc.getId() + ",'" + vtResUsersEdit.elementAt(i) 
												+ "','"	+ strProjectID  + "','"+ Defines.DOC_EDIT_ACCESS +"')";
							vtAddEdit.addElement(vtResUsersEdit.elementAt(i));
						}
				
						// Do the same for Groups
						for (int i = 0; i < strRestrictedGroupsEdit.length; i++) {
					    	if (i != 0)
					    		strAddGroupsEdit = strAddGroupsEdit	+ ",(" + udDoc.getId() + ",'" 
													+ strRestrictedGroupsEdit[i] + "','" + strProjectID + "','"+ Defines.DOC_EDIT_ACCESS +"')";
					    	else
					    		strAddGroupsEdit = strAddGroupsEdit	+ "(" + udDoc.getId() + ",'"
													+ strRestrictedGroupsEdit[i] + "','" + strProjectID + "','"+ Defines.DOC_EDIT_ACCESS +"')";
				    	vtAddGroupsEdit.addElement(strRestrictedGroupsEdit[i]);
					}
				} else if (!bChangeResEdit && udDoc.IsDPrivateEdit() ) {
				
					//add and remove
					Vector vtAllUsers = udDAO.getProjMembers(strProjectID);
					Vector vtResEdit = udDAO.getRestrictedProjMemberIdsEdit( strProjectID, 	iOldDocID,	false);

					for (int iCounter = 0;		iCounter < vtAllUsers.size();		iCounter++) {
						String strUserId =	((ETSUser) vtAllUsers.elementAt(iCounter)).getUserId();
						if (vtResEdit.contains(strUserId) && !vtResUsersEdit.contains(strUserId)) {
							if (!strRemoveEdit.equals(StringUtil.EMPTY_STRING))
								strRemoveEdit = strRemoveEdit + ",'" + strUserId + "'";
							else
								strRemoveEdit = "'" + strUserId + "'";
						} else if (	!vtResEdit.contains(strUserId) && vtResUsersEdit.contains(strUserId)) {
							if (!strAddEdit.equals(StringUtil.EMPTY_STRING)) 
								strAddEdit = strAddEdit	+ ",(" + udDoc.getId() + ",'" + strUserId + "','" + strProjectID  + "','"+ Defines.DOC_EDIT_ACCESS +"')";
							else
								strAddEdit = strAddEdit + "("+ udDoc.getId() + ",'"	+ strUserId + "','"	+ strProjectID + "','"+ Defines.DOC_EDIT_ACCESS +"')";
							vtAddEdit.addElement(strUserId);
						}
					}

						// do the same for groups
						List lstOldGroupsEdit = udDAO.getAllDocRestrictedEditGroupIds(iOldDocID);
						List lstOldGrpIdsEdit = new ArrayList();
						for (int i = 0; i < lstOldGroupsEdit.size(); i++) {
							String strGroupIdEdit = ((Group) lstOldGroupsEdit.get(i)).getGroupId();
							lstOldGrpIdsEdit.add(strGroupIdEdit);
						}
						List lstNewGrpIdsEdit = new ArrayList();
						for (int i = 0; i < strRestrictedGroupsEdit.length; i++)
								lstNewGrpIdsEdit.add(strRestrictedGroupsEdit[i]);
						
						for(int i=0; i < lstOldGrpIdsEdit.size(); i++) {
							String strGroupIdEdit = (String) lstOldGrpIdsEdit.get(i);
							if (!lstNewGrpIdsEdit.contains(strGroupIdEdit)) {
								if (!strRemoveGroupsEdit.equals(StringUtil.EMPTY_STRING))
									strRemoveGroupsEdit = strRemoveGroupsEdit + ",'"+ strGroupIdEdit + "'";
								else
									strRemoveGroupsEdit = "'" + strGroupIdEdit + "'";
							}
						}
						for(int i=0; i < lstNewGrpIdsEdit.size(); i++) {
							String strGroupIdEdit = (String) lstNewGrpIdsEdit.get(i);
							if (!lstOldGrpIdsEdit.contains(strGroupIdEdit)) {
								if (!strAddGroupsEdit.equals(StringUtil.EMPTY_STRING))
									strAddGroupsEdit =	strAddGroupsEdit + ",(" + udDoc.getId()	+ ",'"
														+ strGroupIdEdit + "','"
														+ strProjectID + "','"
														+ Defines.DOC_EDIT_ACCESS +"')";
								else
									strAddGroupsEdit = strAddGroupsEdit + "(" + udDoc.getId() + ",'"
														+ strGroupIdEdit + "','"
														+ strProjectID + "','"
														+ Defines.DOC_EDIT_ACCESS + "')";
								vtAddGroupsEdit.addElement(strGroupIdEdit);
							}
						}
								
					}
				}
			if (!udDoc.IsDPrivateEdit())
				vtResUsersEdit = new Vector();

////////////////////////////// END [UPDATE PROPERTIES-EDIT] CHANGES //////////////////////////////////////

			if (!udDoc.IsDPrivate())
				vtResUsers = new Vector();
			
			if (bIsUpdateDocProp)
			{
				udDAO.updateDocProp( udDoc,			!udOldDoc.getIBMOnlyStr().equals(udDoc.getIBMOnlyStr()), 
										!udOldDoc.getDPrivate().equals(udDoc.getDPrivate()), 	
										vtResUsers,
										strAdd,   	
										strAddGroups,			
										strRemove,		
										strRemoveGroups,		
										vtAdd,		
										vtAddGroups);
			} else {
				udDAO.updateDocProp( udDoc, 		!udOldDoc.getIBMOnlyStr().equals(udDoc.getIBMOnlyStr()),
										!udOldDoc.getDPrivate().equals(udDoc.getDPrivate()),
										vtResUsers,
										strAdd,
										strAddGroups,
										strRemove,
										strRemoveGroups,
										vtAdd,
										vtAddGroups,
										true);
			}
		

////////////////////// UPDATE CHECK - EDIT /////////////////////////////
			if (bIsUpdateDocProp)
			{
				udDAO.updateDocPropEdit( udDoc,			!udOldDoc.getIBMOnlyStr().equals(udDoc.getIBMOnlyStr()), 
										!udOldDoc.getDPrivateEdit().equals(udDoc.getDPrivateEdit()), 	
										vtResUsersEdit,
										strAddEdit,   	
										strAddGroupsEdit,			
										strRemoveEdit,		
										strRemoveGroupsEdit,		
										vtAddEdit,		
										vtAddGroupsEdit);
				
			} else {
				udDAO.updateDocPropEdit( udDoc, 		!udOldDoc.getIBMOnlyStr().equals(udDoc.getIBMOnlyStr()),
										!udOldDoc.getDPrivateEdit().equals(udDoc.getDPrivateEdit()),
										vtResUsersEdit,
										strAddEdit,
										strAddGroupsEdit,
										strRemoveEdit,
										strRemoveGroupsEdit,
										vtAddEdit,
										vtAddGroupsEdit,
										true);
			}
////////////////////////////////////////////////////////////////////////////////
			udForm.setDocid(String.valueOf(udDoc.getId()));

			if (bIsUpdateDocProp) {
				objETSDocEditHistory.setDocId(udDoc.getId());
				objETSDocEditHistory.setUserId( udEdgeAccess.gIR_USERN );
				objETSDocEditHistory.setAction("Update Document");
				objETSDocEditHistory.setActionDetails("Document properties Updated");
				udDAO.setEditHistory(objETSDocEditHistory);
			}
		}
				
/*				if( (bIsUpdateDocProp) && ( !DocConstants.ACTION_UPDATE_DOC.equals(strFormContext) ) ) {
					udDAO.clearNotificationList( udDoc.getId() );
				}
*/				
				//Check notification list - We do not notify in case of 
				// properties update
				if ( bIsUpdateDocProp ) {
					if( (strFormContext.equals(DocConstants.ACTION_UPDATE_DOC_PROPS)) 
							&& ( !DocConstants.ACTION_UPDATE_DOC.equals(strFormContext) ) ) {
						udDAO.clearNotificationList( udDoc.getId() );
					}
				}
					
					if ((!StringUtil.isNullorEmpty(udForm.getNotifyFlag()))
						|| ((strNotifyUsers != null)
							&& (strNotifyUsers.length > 0))) {
					// Means notification has to be performed.
					Vector vtNotifyMembers = new Vector();
					if (bIsUpdateDoc) {
						vtNotifyMembers =
							NotificationHelper.performUpdateNotification(
								udForm,
								vtResUsers,
								vtResUsersEdit,
								udForm.getNotifyUsersWithoutGroups(),
								udProject,
								udDoc,
								udEdgeAccess,
								udDAO,
								strFormContext);
					} else {
						vtNotifyMembers =
							NotificationHelper.performAddNotification(
								udForm.getNotifyFlag(),
								udForm.getNotifyOption(),
								udForm.getTc(),
								udForm.getCc(),
								udForm.getLinkid(),
								DocumentsHelper.getUniqueUserList(udForm),
								udForm.getChUsers(),
								vtResUsers,
								vtResUsersEdit, 
								udProject,
								udDoc,
								udEdgeAccess,
								udDAO);
					}
					
					String []lstGroups = udForm.getNotifyGroups();
					DocReaderDAO udReader = new DocReaderDAO();
					udReader.setConnection(udDAO.getConnection());
					NotificationMsgHelper.finalizeNotificationList(
					    lstGroups,
					    vtNotifyMembers,
					    udReader
						);
					
						udDAO.addDocNotificationList(
							udDoc.getId(),
							!StringUtil.isNullorEmpty(udForm.getNotifyFlag()),
							vtNotifyMembers,
							udForm.getNotifyGroups());
					}

				if (udProject
					.getProjectOrProposal()
					.equals(DocConstants.TYPE_PROJECT)) {
					Metrics.appLog(
						udDAO.getConnection(),
						udEdgeAccess.gIR_USERN,
						bIsUpdateDoc
							? MetricsConstants.PROJECT_DOC_UPDATE
							: bIsUpdateDocProp
							? MetricsConstants.PROJECT_DOC_PROP_UPDATE
							: MetricsConstants.PROJECT_DOC_ADD);
				} else { //proposal
					Metrics.appLog(
						udDAO.getConnection(),
						udEdgeAccess.gIR_USERN,
						bIsUpdateDoc
							? MetricsConstants.PROPOSAL_DOC_UPDATE
							: bIsUpdateDocProp
							? MetricsConstants.PROPOSAL_DOC_PROP_UPDATE
							: MetricsConstants.PROPOSAL_DOC_ADD);
				}

			}

		} catch (SQLException e) {
			e.printStackTrace(System.out);
			throw e;
		} finally {
			super.cleanup(udDAO);
		}
		
		pdRequest.setAttribute(DocConstants.DOC_FORM, udForm);
		
		
		//added by govind
		
		if(!"workflow".equalsIgnoreCase(appType))
			return pdMapping.findForward(FWD_SUCCESS);
		else{
			ValidateDocumentStageDAO.saveAttachment(udForm.getProj(),workflowID,udDoc.getId(),udDoc.getUserId());
			return new ActionForward("/showstage.wss?proj="+udForm.getProj()+"&workflowID="+workflowID+"&tc="+udForm.getTc());
		}

      //return pdMapping.findForward(FWD_SUCCESS);


	   //addition ends
	}

	/**
	 * @param udForm
	 * @param bIsUpdateDocProp
	 * @return
	 */
	private ActionErrors validate(
		DocumentDAO udDAO,
		BaseDocumentForm udForm,
		boolean bIsUpdateDocProp,
		boolean bIsUpdateDoc,
		boolean bIsITAR)
		throws SQLException {
		ActionErrors pdErrors = new ActionErrors();
        
		ETSDoc udDoc = udForm.getDocument();
		// Check Doc Name
		if (StringUtil.isNullorEmpty(udDoc.getName())
			|| udDoc.getName().length() > DocConstants.MAX_NAME_LENGTH) {
			pdErrors.add(
				DocConstants.MSG_USER_ERROR,
				new ActionMessage(ERR_DOC_NAME));
		} else {
			int iCatId = Integer.parseInt(udForm.getCc());
			if (!bIsUpdateDoc) {
				int iCount =
					udDAO.getDocByNameAndCat(iCatId, udDoc.getName(), -1);
				if (iCount > 0) {
					// Means document with this name already exists in this category
					if(!"workflow".equalsIgnoreCase(udForm.getAppType())){
						pdErrors.add(
								DocConstants.MSG_USER_ERROR,
								new ActionMessage(ERR_DUP_DOCNAME));
					}else{
						pdErrors.add(
								DocConstants.MSG_USER_ERROR,
								new ActionMessage(ERR_WORKFLOW_DUP_DOCNAME));
					
					}
				}
			} else if (bIsUpdateDoc) {
				int iCount =
					udDAO.getDocByNameAndCat(
						iCatId,
						udDoc.getName(),
						Integer.parseInt(udForm.getDocid()));
			if (iCount > 0) {
				// Means document with this name already exists in this category
				pdErrors.add(
					DocConstants.MSG_USER_ERROR,
					new ActionMessage(ERR_DUP_DOCNAME));
			}
		}
		}

//		// Check Doc Descrption
//		if (!StringUtil.isNullorEmpty(udDoc.getDescription())
//			&& (udDoc.getDescription().length()
//				> DocConstants.MAX_DESCRIPTION_LENGTH)) {
//			pdErrors.add(
//				DocConstants.MSG_USER_ERROR,
//				new ActionMessage(ERR_DOC_DESC));
//		}

		// Check Doc Keywords
		if (!StringUtil.isNullorEmpty(udDoc.getKeywords())
			&& (udDoc.getKeywords().length() > DocConstants.MAX_KEYWORD_LENGTH)) {
			pdErrors.add(
				DocConstants.MSG_USER_ERROR,
				new ActionMessage(ERR_DOC_KEYWORDS));
		}

		// Check Security Classification
		if (DocConstants
			.SEL_OPT_NONE
			.equals(udForm.getDocument().getIBMOnlyStr())) {
			pdErrors.add(
				DocConstants.MSG_USER_ERROR,
				new ActionMessage(ERR_SEC_NOTSEL));
		}

		//Check Doc File - Only if it is Add Document / Upload new version
		
		if (!bIsUpdateDocProp
			&& !bIsITAR
			&& !udForm.getDocAction().equals(DocConstants.ACTION_ADD_AIC_DOC)) {
			List lstUploadedFiles = udForm.getUploadedFiles();
			// Must have at-least one file
			if (lstUploadedFiles == null || lstUploadedFiles.size() == 0) {
				// DO NOTHING.
			    m_pdLog.error("FILE LENGTH : 0def");
			} else {
			    m_pdLog.error("FILE LENGTH : " + lstUploadedFiles.size());
				boolean bIsFirstFile = true;
				for (int iCounter = 0;
					iCounter < lstUploadedFiles.size();
					iCounter++) {
					FormFile udFormFile =
						(FormFile) lstUploadedFiles.get(iCounter);

					/*
					if (StringUtil.isNullorEmpty(udFormFile.getFileName())
						&& bIsFirstFile) {
						pdErrors.add(
							DocConstants.MSG_USER_ERROR,
							new ActionMessage(ERR_FILE_EMPTY));
					}
					*/
				    m_pdLog.error("FILE NAME : " + udFormFile.getFileName());
				    m_pdLog.error("FILE SIZE : " + udFormFile.getFileSize());
					if (!StringUtil.isNullorEmpty(udFormFile.getFileName())) {
						int iFileSize = udFormFile.getFileSize();
						if (iFileSize > DocConstants.MAX_FILE_SIZE) {
							pdErrors.add(
								DocConstants.MSG_USER_ERROR,
								new ActionMessage(ERR_FILE_SIZE));
						} else if (iFileSize == 0) {
							pdErrors.add(
								DocConstants.MSG_USER_ERROR,
								new ActionMessage(ERR_FILE_ADD_INVALID_FILE));
						}
					}
					bIsFirstFile = false;
				}
			}
		}
		
		// Validate Date if Expiry Date checkbox is checked
		DocExpirationDate udDate = udForm.getExpDate();
		if (!StringUtil.isNullorEmpty(udDate.getExpires())) {
			// MEANS Expiration date was checked
			String strDateCheck = isCorrectDate(udDate);
			if (!StringUtil.isNullorEmpty(strDateCheck)) {
				pdErrors.add(
					DocConstants.MSG_USER_ERROR,
					new ActionMessage(strDateCheck));
			}
		}

		return pdErrors;
	}

	/**
	 * @param udDate
	 * @return
	 */
	private String isCorrectDate(DocExpirationDate udDate) {
		String strDateCheck = null;
		String strDay = udDate.getDay();
		String strMonth = udDate.getMonth();
		String strYear = udDate.getYear();
		if (StringUtil.isNullorEmpty(strDay)
			|| StringUtil.isNullorEmpty(strMonth)
			|| StringUtil.isNullorEmpty(strYear)) {
			//Basic check failed. None of the date elements can be empty	
			strDateCheck = ERR_DOC_EXPIRATION_DATE;
		} else {
			int iDay = Integer.parseInt(strDay);
			int iMonth = Integer.parseInt(strMonth);
			int iYear = Integer.parseInt(strYear);
			if (iDay == -1 || iMonth == -1 || iYear == -1) {
				strDateCheck = ERR_DOC_EXPIRATION_DATE;
			} else {
				Calendar pdCalendar = Calendar.getInstance();
				pdCalendar.set(Calendar.YEAR, iYear);
				pdCalendar.set(Calendar.MONTH, iMonth);
				int iMaxDate =
					pdCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				int iMinDate =
					pdCalendar.getActualMinimum(Calendar.DAY_OF_MONTH);

				if ((iMinDate <= iDay) && (iDay <= iMaxDate)) {
					pdCalendar.set(Calendar.DAY_OF_MONTH, iDay);
					if (pdCalendar.before(Calendar.getInstance())) {
						strDateCheck = ERR_DOC_EXPIRATION_DATE_PAST;
					}
				} else {
					strDateCheck = ERR_DOC_EXPIRATION_DATE;
				}
			}
		}
		return strDateCheck;
	}
}