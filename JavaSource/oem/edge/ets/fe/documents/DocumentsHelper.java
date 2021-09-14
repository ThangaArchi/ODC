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

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.Global;
import oem.edge.common.cipher.ODCipherData;
import oem.edge.common.cipher.ODCipherRSA;
import oem.edge.common.cipher.ODCipherRSAFactory;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.aic.dyntab.proxy.AICDynTabTableProxy;
import oem.edge.ets.fe.common.EncodeUtils;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.Group;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.documents.data.DocumentDAO;
import oem.edge.ets.fe.ismgt.model.ETSIssueAttach;
import oem.edge.ets.fe.ismgt.model.EtsPrimaryContactInfo;

import org.apache.commons.logging.Log;

import com.ibm.as400.webaccess.common.ConfigObject;

/**
 * @author v2srikau
 */
public class DocumentsHelper {

	/** Stores the Logger Object */
	private static final Log m_pdLog =
		EtsLogger.getLogger(DocumentsHelper.class);

	/** Used only by the AIC helper method */
	private static final String SERVLET_PATH = "/technologyconnect/ets";

	/**
	 * @param strUserID
	 * @param strProjectID
	 * @param iPrivilige
	 * @return
	 */
	public static boolean hasPriviliges(
		String strUserID,
		String strProjectID,
		int iPrivilige) {
		DocumentDAO udDocumentDAO = new DocumentDAO();
		boolean bHasPrivilige = false;
		try {
			udDocumentDAO.prepare();
			bHasPrivilige =
				udDocumentDAO.hasProjectPriv(
					strUserID,
					strProjectID,
					iPrivilige);
			udDocumentDAO.cleanup();
		} catch (SQLException e) {
			m_pdLog.error(e);
		} catch (Exception e) {
			m_pdLog.error(e);
		} finally {
			try {
				if (udDocumentDAO != null) {
					udDocumentDAO.cleanup();
				}
			} catch (SQLException e) {
				m_pdLog.error(e);
			}
		}

		return bHasPrivilige;
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static EdgeAccessCntrl getEdgeAccess(HttpServletRequest pdRequest) {
		EdgeAccessCntrl pdAccess =
			(EdgeAccessCntrl) pdRequest.getAttribute(
				DocConstants.REQ_ATTR_EDGEACCESS);

		return pdAccess;
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static String getUserRole(HttpServletRequest pdRequest) {
		String strUserRole =
			(String) pdRequest.getAttribute(DocConstants.REQ_ATTR_USERROLE);

		return strUserRole;
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static boolean isInternal(HttpServletRequest pdRequest) {
		boolean bIsInternal = false;

		EdgeAccessCntrl pdAccess =
			(EdgeAccessCntrl) pdRequest.getAttribute(
				DocConstants.REQ_ATTR_EDGEACCESS);
		if (pdAccess
			.gDECAFTYPE
			.trim()
			.equals(DocConstants.DECAFTYPE_INTERNAL)) {
			bIsInternal = true;
		}
		return bIsInternal;
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static String getFormContext(HttpServletRequest pdRequest) {
		return (
			(BaseDocumentForm) pdRequest.getAttribute(DocConstants.DOC_FORM))
			.getFormContext();
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static BaseDocumentForm getDocumentForm(ServletRequest pdRequest) {
		return (
			(BaseDocumentForm) pdRequest.getAttribute(DocConstants.DOC_FORM));
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static BaseDocumentForm getDocumentForm(HttpServletRequest pdRequest) {
		return (
			(BaseDocumentForm) pdRequest.getAttribute(DocConstants.DOC_FORM));
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static String getLinkID(HttpServletRequest pdRequest) {
		String strLinkID = null;
		strLinkID = pdRequest.getParameter(DocConstants.PARAM_LINKID);

		if (StringUtil.isNullorEmpty(strLinkID)) {
			BaseDocumentForm udForm = getDocumentForm(pdRequest);
			if (udForm != null) {
				strLinkID = udForm.getLinkid();
			}
		}

		return strLinkID;
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static String getCurrentCatID(HttpServletRequest pdRequest) {
		String strCurrentCatID = null;
		strCurrentCatID =
			pdRequest.getParameter(DocConstants.PARAM_CURCATEGORY);

		if (StringUtil.isNullorEmpty(strCurrentCatID)) {
			BaseDocumentForm udForm = getDocumentForm(pdRequest);
			if (udForm != null) {
				strCurrentCatID = udForm.getCc();
			}
		}

		return strCurrentCatID;
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static String getTopCatID(HttpServletRequest pdRequest) {
		String strTopCatID = null;
		strTopCatID = pdRequest.getParameter(DocConstants.PARAM_TOPCATEGORY);

		if (StringUtil.isNullorEmpty(strTopCatID)) {
			BaseDocumentForm udForm = getDocumentForm(pdRequest);
			if (udForm != null) {
				strTopCatID = udForm.getTc();
			}
		}

		return strTopCatID;
	}
	/**
	 * 
	 * @param pdRequest
	 * @return
	 */
	public static String getWorkflowID(HttpServletRequest pdRequest) {
		String workflowID = null;
		workflowID = pdRequest.getParameter("workflowID");
		if (StringUtil.isNullorEmpty(workflowID)) {
			BaseDocumentForm udForm = getDocumentForm(pdRequest);
			if (udForm != null) {
				workflowID = udForm.getWorkflowID();
			}
		}

		return workflowID;
	}
	/**
	 * 
	 * @param pdRequest
	 * @return
	 */
	public static String getAppType(HttpServletRequest pdRequest) {
		String appType = null;
		appType = pdRequest.getParameter("appType");
		if (StringUtil.isNullorEmpty(appType)) {
			BaseDocumentForm udForm = getDocumentForm(pdRequest);
			if (udForm != null) {
				appType = udForm.getAppType();
			}
		}

		return appType;
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static String getProjectID(HttpServletRequest pdRequest) {
		String strProjectID = null;
		strProjectID = pdRequest.getParameter(DocConstants.PARAM_PROJECTID);

		if (StringUtil.isNullorEmpty(strProjectID)) {
			BaseDocumentForm udForm = getDocumentForm(pdRequest);
			if (udForm != null) {
				strProjectID = udForm.getProj();
			}
		}

		return strProjectID;
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static boolean isUpdateCategory(HttpServletRequest pdRequest) {
		return DocConstants.FRM_CTX_UPDATE_CAT.equals(
			getFormContext(pdRequest));
	}

	/**
	 * @param pdRequest
	 * @param strKey
	 * @return
	 */
	public static String getParameter(
		HttpServletRequest pdRequest,
		String strKey) {
		String strValue = pdRequest.getParameter(strKey);

		if (StringUtil.isNullorEmpty(strValue)) {
			return StringUtil.EMPTY_STRING;
		} else {
			return strValue;
		}
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static int countDocComments(HttpServletRequest pdRequest) {
		BaseDocumentForm udForm = getDocumentForm(pdRequest);
		int iCount = 0;
		if (udForm.getComments() != null) {
			iCount = udForm.getComments().size();
		}
		return iCount;
	}

	/**
	 * @param strOwnerID
	 * @param iDocID
	 * @param strProjectId
	 * @param isViewOnly
	 * @param isCat
	 * @param userid
	 * @param pdRequest
	 * @return
	 */
	public static boolean isAuthorized(
		String strOwnerID,
		int iDocID,
		String strProjectId,
		boolean isViewOnly,
		boolean isCat,
		HttpServletRequest pdRequest) {

		String strUserRole = getUserRole(pdRequest);
		boolean bIsSuperAdmin = Defines.ETS_ADMIN.equals(strUserRole);
		boolean bIsExecutive = Defines.ETS_EXECUTIVE.equals(strUserRole);
		String strUserId = getEdgeAccess(pdRequest).gIR_USERN;

		if (isViewOnly
			&& (strUserRole.equals(Defines.WORKSPACE_OWNER)
				|| bIsSuperAdmin
				|| bIsExecutive
				|| strOwnerID.equals(strUserId)))
			return true;
		else if (
			(!isViewOnly)
				&& (strUserRole.equals(Defines.WORKSPACE_OWNER)
					|| bIsSuperAdmin
					|| strOwnerID.equals(strUserId)))
			return true;
		else {
			DocumentDAO udDocumentDAO = new DocumentDAO();
			try {
				udDocumentDAO.prepare();
				Vector users =
					udDocumentDAO.getRestrictedProjMemberIds(
						strProjectId,
						iDocID,
						isCat);
				for (int u = 0; u < users.size(); u++) {
					if (strUserId.equals(users.elementAt(u))) {
						if (isViewOnly
							|| (strUserRole.equals(Defines.WORKSPACE_MANAGER)))
							return true;
					}
				}
				udDocumentDAO.cleanup();
			} catch (SQLException e) {
				m_pdLog.error(e);
			} catch (Exception e) {
				m_pdLog.error(e);
			} finally {
				try {
					if (udDocumentDAO != null) {
						udDocumentDAO.cleanup();
					}
				} catch (SQLException e) {
					m_pdLog.error(e);
				}
			}
		}
		return false;
	}

	/**
	 * @param pdRequest
	 * @param strValue
	 * @return
	 */
	public static String getSelectedStatus(
		HttpServletRequest pdRequest,
		String strValue) {
		String strSelected = StringUtil.EMPTY_STRING;
		String[] strRestrictedUsers =
			getDocumentForm(pdRequest).getRestrictedUsers();
		if (strRestrictedUsers == null || strRestrictedUsers.length == 0) {
			return strSelected;
		}
		for (int iCounter = 0;
			iCounter < strRestrictedUsers.length;
			iCounter++) {
			if (strRestrictedUsers[iCounter].equals(strValue)) {
				strSelected = StringUtil.SELECTED;
				return strSelected;
			}
		}
		return strSelected;
	}

	/**
	 * @param pdRequest
	 * @param strValue
	 * @return
	 */
	public static String getNotifySelectedStatus(
		HttpServletRequest pdRequest,
		String strValue) {
		String strSelected = StringUtil.EMPTY_STRING;
		String[] strNotifyUsers = getDocumentForm(pdRequest).getNotifyUsers();
		if (strNotifyUsers == null || strNotifyUsers.length == 0) {
			return strSelected;
		}
		for (int iCounter = 0; iCounter < strNotifyUsers.length; iCounter++) {
			if (strNotifyUsers[iCounter].equals(strValue)) {
				strSelected = StringUtil.SELECTED;
				return strSelected;
			}
		}
		return strSelected;
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static boolean isAICProject(ServletRequest pdRequest) {
		return isAICProject((HttpServletRequest) pdRequest);
	}
	/**
	 * @param pdRequest
	 * @return
	 */
	public static boolean isAICProject(HttpServletRequest pdRequest) {
		boolean bIsAICProject = false;

		String strProjectId = getProjectID(pdRequest);
		ETSProj udProj = null;
		DocumentDAO udDAO = new DocumentDAO();
		try {
			udDAO.prepare();
			udProj = udDAO.getProjectDetails(strProjectId);
			if (udProj != null) {
				bIsAICProject =
					DocConstants.PROJECT_TYPE_AIC.equals(
						udProj.getProjectType());
			}
		} catch (Exception e) {
			// CONSUME THIS ERROR.
			m_pdLog.error(e);
		} finally {
			try {
				if (udDAO != null) {
					udDAO.cleanup();
				}
			} catch (SQLException e) {
				m_pdLog.error(e);
			}
		}
		return bIsAICProject;
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static boolean isITARProject(HttpServletRequest pdRequest) {
		boolean bIsITARProject = false;
		String strProjectId = getProjectID(pdRequest);
		ETSProj udProj = null;
		DocumentDAO udDAO = new DocumentDAO();
		try {
			udDAO.prepare();
			udProj = udDAO.getProjectDetails(strProjectId);
			if (udProj != null) {
				bIsITARProject = udProj.isITAR();
			}
		} catch (Exception e) {
			// CONSUME THIS ERROR.
			m_pdLog.error(e);
		} finally {
			try {
				if (udDAO != null) {
					udDAO.cleanup();
				}
			} catch (SQLException e) {
				m_pdLog.error(e);
			}
		}
		return bIsITARProject;
	}

	/**
	 * @param iCatID
	 * @param strMeetingName
	 * @param strProjectId
	 * @param strUserName
	 * @return Whether Update was succesfull or not.
	 * @throws Exception
	 */
	public static boolean updateMeetingFolder(
		int iCatID,
		String strMeetingName,
		String strProjectId,
		String strUserName)
		throws Exception {

		// First check if Meetings Folder for this workspace already exists
		DocumentDAO udDAO = new DocumentDAO();
		boolean bWasUpdateSuccessful = false;
		try {
			udDAO.prepare();

			ETSCat udCat = udDAO.getCat(iCatID);
			if (udCat == null) {
				bWasUpdateSuccessful = false;
			} else {
				udCat.setUserId(strUserName);
				udCat.setName(strMeetingName);
				bWasUpdateSuccessful = udDAO.updateCat(udCat);
			}

		} catch (SQLException e) {
			m_pdLog.error(e);
			throw e;
		} catch (Exception e) {
			m_pdLog.error(e);
			throw e;
		} finally {
			if (udDAO != null) {
				udDAO.cleanup();
			}
		}
		return bWasUpdateSuccessful;

	}

	/**
	 * @param iCatID
	 * @return Parent Category ID
	 * @throws Exception
	 */
	public static int getParentCatID(int iCatID) throws Exception {

		DocumentDAO udDAO = new DocumentDAO();
		int iParentCatId = -1;
		try {
			udDAO.prepare();

			ETSCat udCat = udDAO.getCat(iCatID);
			if (udCat != null) {
				iParentCatId = udCat.getParentId();
			}

		} catch (SQLException e) {
			m_pdLog.error(e);
			throw e;
		} catch (Exception e) {
			m_pdLog.error(e);
			throw e;
		} finally {
			if (udDAO != null) {
				udDAO.cleanup();
			}
		}
		return iParentCatId;
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static boolean isAllUsersGroup(HttpServletRequest pdRequest, String strGroupID) {
	    String strAllUsersGroupID = getAllUsersGroupID(pdRequest);
	    return strGroupID.equalsIgnoreCase(strAllUsersGroupID);
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static String getAllUsersGroupID(HttpServletRequest pdRequest) {
	    BaseDocumentForm udForm = getDocumentForm(pdRequest);
	    List lstGroups = udForm.getGroups();
	    String strGroupID = StringUtil.EMPTY_STRING;
	    if (lstGroups != null && lstGroups.size() > 0) {
	        for(int i=0; i < lstGroups.size(); i++) {
	            Group udGroup = (Group) lstGroups.get(i);
	            if (Defines.GRP_ALL_USERS.equalsIgnoreCase(udGroup.getGroupName())) {
	                strGroupID = udGroup.getGroupId();
	                break;
	            }
	        }
	    }
	    
	    return strGroupID;
	}
	
	/**
	 * @param pdRequest
	 * @return
	 */
	public static List getDefaultEditors(HttpServletRequest pdRequest, BaseDocumentForm udForm, boolean bIsNewDocument) {
	    
	    if (udForm == null) {
	        udForm = getDocumentForm(pdRequest);
	    }
	    
	    List lstDefaultEditors = new ArrayList();
	    String strDocOwner = null;
	    
	    if (bIsNewDocument) {
	        strDocOwner = getEdgeAccess(pdRequest).gIR_USERN;
	    }
	    else {
	        strDocOwner = udForm.getDocument().getUserId();
	    }
	    
	    List lstUsers = udForm.getUsers();
	    if (lstUsers != null && lstUsers.size() > 0) {
	        for(int iCounter=0; iCounter < lstUsers.size(); iCounter++) {
	            ETSUser udEachUser = (ETSUser) lstUsers.get(iCounter);
	            if (strDocOwner.equalsIgnoreCase(udEachUser.getUserId()) 
	                    || udEachUser.getUserType().equalsIgnoreCase(Defines.WORKSPACE_MANAGER) 
	                    || udEachUser.getUserType().equalsIgnoreCase(Defines.WORKSPACE_OWNER)) {
	                // Means this user can by default edit this document.
	                if (!lstDefaultEditors.contains(udEachUser)) {
		                lstDefaultEditors.add(udEachUser);
	                }
	            }
	        }
	    }
	    return lstDefaultEditors;
	}
	
	/**
	 * @param strMeetingName
	 * @param strProjectId
	 * @return
	 * @throws Exception
	 */
	public static int addMeetingsFolder(
		int iParentCatID,
		String strMeetingName,
		String strProjectId,
		String strUserName)
		throws Exception {
		int iNewFolderId = -1;

		// First check if Meetings Folder for this workspace already exists
		DocumentDAO udDAO = new DocumentDAO();
		try {
			udDAO.prepare();

			ETSCat udCat = null;
			if (iParentCatID != -1) {
				udCat = udDAO.getCat(iParentCatID);

			} else {
				udCat =
					udDAO.getCatByName(
						DocConstants.MEETINGS_DOC_FOLDER,
						strProjectId);
			}

			String[] strResult = null;
			if (udCat == null) {
				// Means the top level folder does not exist. So add it.
				udCat = udDAO.getCatByName("Documents", strProjectId);

				if (udCat == null) {
					udCat = new ETSCat();
				}
				udCat.setName(DocConstants.MEETINGS_DOC_FOLDER);
				udCat.setIbmOnly(DocConstants.ETS_PUBLIC);
				udCat.setUserId(strUserName);
				udCat.setParentId(udCat.getId());
				udCat.setDisplayFlag(DocConstants.IND_NO);

				strResult = udDAO.addCat(udCat);
				if (strResult != null
					&& strResult.length == 2
					&& !StringUtil.isNullorEmpty(strResult[1])) {
					udCat.setId(Integer.parseInt(strResult[1]));
				}
			}

			udCat.setUserId(strUserName);
			udCat.setName(strMeetingName);
			udCat.setParentId(udCat.getId());
			udCat.setId(0);
			strResult = udDAO.addCat(udCat);
			if (strResult != null && strResult.length == 2) {
				iNewFolderId = Integer.parseInt(strResult[1]);
			}

		} catch (SQLException e) {
			m_pdLog.error(e);
			throw e;
		} catch (Exception e) {
			m_pdLog.error(e);
			throw e;
		} finally {
			if (udDAO != null) {
				udDAO.cleanup();
			}
		}
		return iNewFolderId;
	}

	/**
	 * @param pdRequest
	 * @return
	 */
	public static EtsPrimaryContactInfo getPrimaryContactInfo(HttpServletRequest pdRequest) {
		return (EtsPrimaryContactInfo) pdRequest.getAttribute(
			DocConstants.REQ_ATTR_PRIMARYCONTACT);
	}

	/**
	 * @param iDocId
	 * @param strProjectId
	 * @param strCurrentCatId
	 * @param strTopCatId
	 * @param strEdgeId
	 * @return
	 */
	public static String encode(int iDocId, String strProjectId, String strCurrentCatId, String strTopCatId, String strEdgeId) {
	    String strToken = "";
	    try {
	        strToken = EncodeUtils.encode(Integer.toString(iDocId), strProjectId, strEdgeId, strTopCatId, strCurrentCatId);
	    }
	    catch(Exception e) {
	        m_pdLog.error(e);
	    }
	    
	    return strToken;
	}
	
	/**
	 * @param udForm
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public static String encode(
		String strDocId,
		String strDocFileId,
		String strProjectId)
		throws  Exception {

		try {

			if (!Global.loaded) {
				Global.Init();
			}

//			RSAKeyPair keypair =
//				RSAKeyPair.load(Global.encode_keypath + "boulder.key");
//
//			ODCipherRSA edgecipher = new ODCipherRSA(keypair);
			ODCipherRSA edgecipher = null;

			ODCipherRSAFactory fac = ODCipherRSAFactory.newFactoryInstance();
			try {
				edgecipher = fac.newInstance(Global.encode_keypath + "boulder.key");
			} catch(Throwable t) {
				System.out.println("Error loading CipherFile! [" + Global.encode_keypath + "boulder.key" + "]");
			}

			ConfigObject pdConfigObject = new ConfigObject();
			pdConfigObject.setProperty("DOCID", strDocId);
			pdConfigObject.setProperty("DOCFILEID", strDocFileId);
			pdConfigObject.setProperty("PROJID", strProjectId);

			String sToEncode = pdConfigObject.toString();

			ODCipherData cipherdata = edgecipher.encode(60 * 10, sToEncode);
			String sEncodedString = cipherdata.getExportString();

			return sEncodedString;

		} catch (Exception e) {
			m_pdLog.error(e);
			throw e;
		}
	}

	/**
	 * @return
	 */
	public static boolean isAICTable(int iDocId) {
		boolean bIsAICTable = false;

		AICDynTabTableProxy udAICProxy = new AICDynTabTableProxy();

		try {
			Collection udTables = udAICProxy.findByDocID(iDocId);
			if (udTables != null && udTables.size() > 0) {
				bIsAICTable = true;
			}
		} catch (Exception e) {
			m_pdLog.error(e);
		}

		return bIsAICTable;

	}

	/**
	 * @return
	 */
	public static String getAICTableLink(
		int iDocId,
		String strLink,
		String strProjectId,
		String strCurrentCatID,
		String strTopCatID) {
		String strAICTableLink = "";

		AICDynTabTableProxy udAICProxy = new AICDynTabTableProxy();
		strAICTableLink = udAICProxy.getTableURI(iDocId);

		strAICTableLink = strAICTableLink + "&proj=" + strProjectId;
		strAICTableLink = strAICTableLink + "&tc=" + strTopCatID;
		strAICTableLink = strAICTableLink + "&cc=" + strCurrentCatID;
		strAICTableLink = strAICTableLink + "&linkid=" + strLink;

		return SERVLET_PATH + strAICTableLink;

	}

	/**
	 * @param pdRequest
	 * @param iTabType
	 * @return
	 */
	public static int getTopCatForTab(
		HttpServletRequest pdRequest,
		int iTabType) {

		DocumentDAO udDAO = new DocumentDAO();
		int iTopCatId = -1;
		try {
			udDAO.prepare();
			int iTopCatID =
				udDAO.getTopCatId(getProjectID(pdRequest), iTabType);
		} catch (SQLException e) {
			m_pdLog.error(e);
		} catch (Exception e) {
			m_pdLog.error(e);
		} finally {
			if (udDAO != null) {
				try {
					udDAO.cleanup();
				} catch (SQLException e) {
					m_pdLog.error(e);
				}
			}
		}
		return iTopCatId;

	}

	/**
	 * @param pdRequest
	 * @param strValue
	 * @return
	 */
	public static String getNotifyGroupStatus(
		HttpServletRequest pdRequest,
		String strValue) {
		String strSelected = StringUtil.EMPTY_STRING;
		String[] strNotifyGroups =
			getDocumentForm(pdRequest).getSelectedNotifyGroups();
		if (strNotifyGroups == null || strNotifyGroups.length == 0) {
			return strSelected;
				}
		for (int iCounter = 0; iCounter < strNotifyGroups.length; iCounter++) {
			if (strNotifyGroups[iCounter].equals(strValue)) {
				strSelected = StringUtil.SELECTED;
				return strSelected;
			}
		}
		return strSelected;
	}

	/**
	 * @param pdRequest
	 * @param strValue
	 * @return
	 */
	public static String getGroupSelectedStatus(
		HttpServletRequest pdRequest,
		String strValue) {
		String strSelected = StringUtil.EMPTY_STRING;
		List lstRestrictedGroups =
			getDocumentForm(pdRequest).getSelectedGroups();
		if (lstRestrictedGroups == null || lstRestrictedGroups.size() == 0) {
			return strSelected;
			}
		for (int iCounter = 0;
			iCounter < lstRestrictedGroups.size();
			iCounter++) {
			Group udGroup = (Group) lstRestrictedGroups.get(iCounter);
			if (udGroup.getGroupId().equals(strValue)) {
				strSelected = StringUtil.SELECTED;
				return strSelected;
				}
			}
		return strSelected;
	}

	/**
	 * @param udForm
	 * @return
	 */
	public static String[] getUniqueUserList(BaseDocumentForm udForm) {
		String[] strGroups = udForm.getNotifyGroups();
		String[] strUsers = udForm.getNotifyUsersWithoutGroups();

		List lstUsers = new ArrayList();

		// Add all the users
		for (int iCounter = 0; iCounter < strUsers.length; iCounter++) {
			lstUsers.add(strUsers[iCounter]);
		}

		// Add all the users from the groups
		for (int iCounter = 0; iCounter < strGroups.length; iCounter++) {
			String strGroup = strGroups[iCounter];

			// Get the users for this group
		DocumentDAO udDAO = new DocumentDAO();
		try {
			udDAO.prepare();
				List lstGrpUsers = udDAO.getGroupUsers(strGroup);
				for (int iUsers = 0; iUsers < lstGrpUsers.size(); iUsers++) {
					String strUserId = (String) lstGrpUsers.get(iUsers);
					if (!lstUsers.contains(strUserId)) {
						lstUsers.add(strUserId);
					}
			}
		} catch (Exception e) {
			m_pdLog.error(e);
		} finally {
				try {
					udDAO.cleanup();
				} catch (Exception e) {
					m_pdLog.error(e);
				}
			}
		}
		String[] strFinalUsers = new String[lstUsers.size()];
		lstUsers.toArray(strFinalUsers);
		return strFinalUsers;
	}

	/**
	 * @see IssuesHelper.attachIssueFile(String,String,int,ETSIssueAttach)
	 */
	public static boolean attachIssueFile(
		String strProjectId,
		String strStatusFlag,
		int iDocId,
		ETSIssueAttach udIssueDetails) {
		return IssuesHelper.attachIssueFile(
			strProjectId,
			strStatusFlag,
			iDocId,
			udIssueDetails);
	}

	/**
	 * @see IssuesHelper.deleteIssueFile(String,String,int)
	 */
	public static boolean deleteIssueFile(
		String strProjectId,
		String strProblemId,
		int iDocFileId) {
		return IssuesHelper.deleteIssueFile(
			strProjectId,
			strProblemId,
			iDocFileId);
	}

	/**
	 * @see IssuesHelper.deleteIssueFilesWithoutStatus(String,String,String)
	 */
	public static boolean deleteIssueFilesWithoutStatus(
		String strProjectId,
		String strProblemId,
		String strStatus) {
		return IssuesHelper.deleteIssueFilesWithoutStatus(
			strProjectId,
			strProblemId,
			strStatus);
	}

	/**
	 * @see IssuesHelper.deleteIssueFilesWithStatus(String,String,String)
	 */
	public static boolean deleteIssueFilesWithStatus(
		String strProjectId,
		String strProblemId,
		String strStatus) {
		return IssuesHelper.deleteIssueFilesWithStatus(
			strProjectId,
			strProblemId,
			strStatus);
	}

	/**
	 * @see IssuesHelper.getIssueFiles(String,String,String)
	 */
	public static List getIssueFiles(
		String strProjectId,
		String strProblemId,
		String strStatusFlag) {
		return IssuesHelper.getIssueFiles(
			strProjectId,
			strProblemId,
			strStatusFlag);
	}

	/**
	 * @see IssuesHelper.getIssueFilesWithoutFlag(String,String,String)
	 */
	public static List getIssueFilesWithoutFlag(
		String strProjectId,
		String strProblemId,
		String strStatusFlag) {
		return IssuesHelper.getIssueFilesWithoutFlag(
			strProjectId,
			strProblemId,
			strStatusFlag);
	}

	/**
	 * @see IssuesHelper.getIssuesDoc(String,String)
	 */
	public static int getIssuesDoc(String strProjectId, String strProblemId) {
		return IssuesHelper.getIssuesDoc(
			strProjectId,
			strProblemId,
			DocConstants.ISSUES_DOC_CREATOR);
	}

	/**
	 * @see IssuesHelper.getIssuesDoc(String,String,String)
	 */
	public static int getIssuesDoc(
		String strProjectId,
		String strProblemId,
		String strUserId) {
		return IssuesHelper.getIssuesDoc(strProjectId, strProblemId, strUserId);
	}

	/**
	 * @see IssuesHelper.updateIssueFileStatus(String,String,int,String)
	 */
	public static void updateIssueFileStatus(
		String strProjectId,
		String strProblemId,
		int iDocFileId,
		String strStatus) {
		IssuesHelper.updateIssueFileStatus(
			strProjectId,
			strProblemId,
			iDocFileId,
			strStatus);
	}

	/**
	 * @see IssuesHelper.updateIssueFileStatus(String,String,String)
	 */
	public static boolean updateIssueFileStatus(
		String strProjectId,
		String strProblemId,
		String strStatus) {
		return IssuesHelper.updateIssueFileStatus(
			strProjectId,
			strProblemId,
			strStatus);
	}

	/**
	 * @see IssuesHelper.updateIssueFileStatus(String,String,String,String)
	 */
	public static boolean updateIssueFileStatus(
		String strProjectId,
		String strProblemId,
		String strOldStatus,
		String strStatus) {
		return IssuesHelper.updateIssueFileStatus(
			strProjectId,
			strProblemId,
			strOldStatus,
			strStatus);
	}

	/**
	 * @param strGroupId
	 * @return
	 */
	public static boolean onRemoveGroup(String strGroupId) {
		boolean bSuccess = true;
		DocumentDAO udDAO = new DocumentDAO();
		try {
			udDAO.prepare();
			udDAO.removeDocGroups(strGroupId);
		} catch (Exception e) {
			m_pdLog.error(e);
		} finally {
			try {
				udDAO.cleanup();
			} catch (Exception e) {
				m_pdLog.error(e);
			}
		}
		return bSuccess;
	}


	/**
	 * @param idoc_Id
	 * @param strUser_Id
	 * @param strProj_Id
	 * @return
	 */
	public static boolean checkDocumentEditPriv(int idoc_Id, String strUser_Id, String strProj_Id)
	{
		DocumentDAO udDocumentDAO = new DocumentDAO();
		boolean bHasEditPrivilige = false;
		try {
			udDocumentDAO.prepare();
			bHasEditPrivilige = udDocumentDAO.checkDocumentEditPriv(idoc_Id, strUser_Id, strProj_Id);
			udDocumentDAO.cleanup();
		} catch (SQLException e) {
			m_pdLog.error(e);
		} catch (Exception e) {
			m_pdLog.error(e);
		} finally {
			try {
				if (udDocumentDAO != null) {
					udDocumentDAO.cleanup();
				}
			} catch (SQLException e) {
				m_pdLog.error(e);
			}
		}
		return bHasEditPrivilige;
	}

	/**
	 * @param idoc_Id
	 * @param strUser_Id
	 * @param strProj_Id
	 * @return
	 */
	public static boolean checkDocumentEditPrivForGroup(int idoc_Id, String strUser_Id, String strProj_Id)
	{
		DocumentDAO udDocumentDAO = new DocumentDAO();
		boolean bHasEditPrivilige = false;
		try {
			udDocumentDAO.prepare();
			bHasEditPrivilige = udDocumentDAO.checkDocumentEditPrivForGroup(idoc_Id, strUser_Id, strProj_Id);
			udDocumentDAO.cleanup();
		} catch (SQLException e) {
			m_pdLog.error(e);
		} catch (Exception e) {
			m_pdLog.error(e);
		} finally {
			try {
				if (udDocumentDAO != null) {
					udDocumentDAO.cleanup();
				}
			} catch (SQLException e) {
				m_pdLog.error(e);
			}
		}
		return bHasEditPrivilige;
	}


	/**
	 * @param idoc_Id
	 * @param strUser_Id
	 * @param strProj_Id
	 * @return
	 */
	public static boolean hasDocumentEditPriv(int idoc_Id, String strUser_Id, String strProj_Id)
	{
		DocumentDAO udDocumentDAO = new DocumentDAO();
		boolean bHasEditPrivilige = false;
		if ( checkDocumentEditPriv(idoc_Id, strUser_Id, strProj_Id) ||
												checkDocumentEditPrivForGroup(idoc_Id, strUser_Id, strProj_Id) )
		{
				bHasEditPrivilige = true;
		}
		return bHasEditPrivilige;
	}


	/**
	 * @param pdRequest
	 * @param strValue
	 * @return
	 */
	public static String getSelectedStatusEditUser(
		HttpServletRequest pdRequest,
		String strValue) {
		String strSelected = StringUtil.EMPTY_STRING;
		String[] strRestrictedUsers =
			getDocumentForm(pdRequest).getRestrictedUsersEdit();
		if (strRestrictedUsers == null || strRestrictedUsers.length == 0) {
			return strSelected;
		}
		for (int iCounter = 0;
			iCounter < strRestrictedUsers.length;
			iCounter++) {
			if (strRestrictedUsers[iCounter].equals(strValue)) {
				strSelected = StringUtil.SELECTED;
				return strSelected;
			}
		}
		return strSelected;
	}


	/**
	 * @param pdRequest
	 * @param strValue
	 * @return
	 */
	public static String getGroupSelectedStatusEdit(
		HttpServletRequest pdRequest,
		String strValue) {
		String strSelected = StringUtil.EMPTY_STRING;
		List lstRestrictedGroups =
			getDocumentForm(pdRequest).getSelectedEditGroups();
		if (lstRestrictedGroups == null || lstRestrictedGroups.size() == 0) {
			return strSelected;
			}
		for (int iCounter = 0;
			iCounter < lstRestrictedGroups.size();
			iCounter++) {
			Group udGroup = (Group) lstRestrictedGroups.get(iCounter);
			if (udGroup.getGroupId().equals(strValue)) {
				strSelected = StringUtil.SELECTED;
				return strSelected;
				}
			}
		return strSelected;
	}


	/**
	 * @param idGroup
	 * @param strUserId
	 * @return
	 */
	public static boolean checkGroupType(Group idGroup, String strUserId){
		boolean groupFlag = false;
		if( (idGroup.getType().equals("0")) || (idGroup.getType().equals("1")) || (idGroup.getType().equals("2")) ||
			(idGroup.getType().equals("3") && (idGroup.getOwner().equals(strUserId)) ) ) {
				groupFlag = true;
		}
		return groupFlag;
	}


	/**
	 * @param udForm
	 * @return
	 */
	public static String[] getAllUniqueUserList(BaseDocumentForm udForm) {
		String[] strUsers = udForm.getNotifyUsers();
		String[] strGroups = udForm.getSelectedNotifyGroups();

		List lstUsers = new ArrayList();

		if (strUsers != null) {
		// Add all the users
		for (int iCounter = 0; iCounter < strUsers.length; iCounter++) {
			lstUsers.add(strUsers[iCounter]);
		}
		}

		if (strGroups !=  null) {

		// Add all the users from the groups
		for (int iCounter = 0; iCounter < strGroups.length; iCounter++) {
			String strGroup = strGroups[iCounter];

			// Get the users for this group
		DocumentDAO udDAO = new DocumentDAO();
		try {
			udDAO.prepare();
				List lstGrpUsers = udDAO.getGroupUsers(strGroup);
				for (int iUsers = 0; iUsers < lstGrpUsers.size(); iUsers++) {
					String strUserId = (String) lstGrpUsers.get(iUsers);
					if (!lstUsers.contains(strUserId)) {
						lstUsers.add(strUserId);
					}
			}
		} catch (Exception e) {
			m_pdLog.error(e);
		} finally {
				try {
					udDAO.cleanup();
				} catch (Exception e) {
					m_pdLog.error(e);
				}
			}
		}
		}
		String[] strFinalUsers = new String[lstUsers.size()];
		lstUsers.toArray(strFinalUsers);
		return strFinalUsers;
	}


	/**
	 * @param strProjectId
	 * @param iCatId
	 * @param strMeetingId
	 * @param strUserId
	 * @return
	 */
	public static int createTmpITARMeetingDoc(
		String strProjectId,
		int iCatId,
		String strMeetingId,
		String strUserId) {
		DocumentDAO udDAO = new DocumentDAO();
		int iDocId = -1;
		try {
			udDAO.prepare();
			ETSDoc udDoc = new ETSDoc();
			udDoc.setName(strMeetingId);
			udDoc.setDescription(strMeetingId);
			udDoc.setProjectId(strProjectId);
			udDoc.setCatId(iCatId);
			udDoc.setUserId(strUserId);
			udDoc.setMeetingId(strMeetingId);
			udDoc.setDocType(Defines.MEETING);
			udDAO.addDocMethod(udDoc, new ArrayList(), 0, true);
			iDocId = udDoc.getId();
		} catch (SQLException e) {
			m_pdLog.error(e);
		} catch (Exception e) {
			m_pdLog.error(e);
		} finally {
			if (udDAO != null) {
				try {
					udDAO.cleanup();
				} catch (SQLException e) {
					m_pdLog.error(e);
				}
			}
		}
		return iDocId;
	}

}