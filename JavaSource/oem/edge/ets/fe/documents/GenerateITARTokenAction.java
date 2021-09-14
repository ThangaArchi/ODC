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
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.common.EncodeUtils;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.DocNotify;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.documents.data.DocumentDAO;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author v2srikau
 */
public class GenerateITARTokenAction extends BaseDocumentAction {

	/** Stores the Logging object */
	private static final Log m_pdLog =
		EtsLogger.getLogger(GenerateITARTokenAction.class);
		
	private static final String ERR_GEN_TOKEN = "error.token.generation";
	
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

		BaseDocumentForm udForm = (BaseDocumentForm) pdForm;

		String strEncodedString = StringUtil.EMPTY_STRING;
		DocumentDAO udDAO = getDAO();
		try {
			if (m_pdLog.isDebugEnabled()) {
				m_pdLog.debug("GENERATING TIME ENCRYPTED TOKEN!!!!!");
			} 
			strEncodedString = encode(udForm, pdRequest);
			int iDocID = udForm.getDocument().getId();
			if (iDocID <= 0) {
			    // Means we did not get the document ID.
			    try {
				    iDocID = Integer.parseInt(udForm.getDocid());
			    }
			    catch(NumberFormatException e) {
			        m_pdLog.error("Error occured while getting document ID : ", e);
			    }
			}
			ETSDoc udDoc = udDAO.getDocByIdAndProject(iDocID, udForm.getProj(), true);
			populateNotificationList(iDocID, udDoc, udForm, udDAO);
		} catch (Exception e) {
			m_pdLog.error(e);
			// If token generation failed. We have a lot of problem.
			// Rollback the document that was just created.
			udDAO.deleteITARDocument(udForm.getDocument().getId());
			throw new DocumentException(ERR_GEN_TOKEN);
		}
		finally {
		    udDAO.cleanup();
		}
		udForm.setEncodedToken(strEncodedString);

		String strError = pdRequest.getParameter("error");

		if (!StringUtil.isNullorEmpty(strError)) {
			ActionErrors pdErrors = new ActionErrors();
			pdErrors.add(
				DocConstants.MSG_USER_ERROR,
				new ActionError(strError));
			saveErrors(pdRequest, pdErrors);
		}

		return pdMapping.findForward(FWD_SUCCESS);
	}

	/**
	 * @param strDocId
	 * @param strProjectId
	 * @param strEdgeId
	 * @param strTopCatId
	 * @param strCurrCatId
	 * @return
	 * @throws IOException
	 */
	public static String encode(
		String strDocId,
		String strProjectId,
		String strEdgeId,
		String strTopCatId,
		String strCurrCatId)
		throws Exception {

			return EncodeUtils.encode(strDocId,
			strProjectId,
			 strEdgeId,
			strTopCatId,
			strCurrCatId);

	}

	/**
	 * @param udForm
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public String encode(BaseDocumentForm udForm, HttpServletRequest pdRequest)
		throws IOException, Exception {

		try {

			String strDocId = Integer.toString(udForm.getDocument().getId());

			if (StringUtil.isNullorEmpty(strDocId) || strDocId.equals("0")) {
				strDocId = udForm.getDocid();
			}
			String strProjectId = udForm.getProj();
			String strEdgeId = getEdgeAccess(pdRequest).gIR_USERN;
			String strTopCatId = udForm.getTc();
			String strCurrCatId = udForm.getCc();

			if (m_pdLog.isDebugEnabled()) {
				m_pdLog.debug("DOCID : " + strDocId);
				m_pdLog.debug("PROJID : " + strProjectId);
				m_pdLog.debug("TOPCATID : " + strTopCatId);
				m_pdLog.debug("CURCATID : " + strCurrCatId);
				m_pdLog.debug("EDGEID : " + strEdgeId);
			}

			String sEncodedString =
				encode(
					strDocId,
					strProjectId,
					strEdgeId,
					strTopCatId,
					strCurrCatId);
			return sEncodedString;

		} catch (IOException e) {
			m_pdLog.error(e);
			throw e;
		} catch (Exception e) {
			m_pdLog.error(e);
			throw e;
		}
	}

	public boolean populateNotificationList(
			int iDocID, ETSDoc udDoc, BaseDocumentForm udForm, DocumentDAO udDAO) throws SQLException
	{
		Vector vtTmpList = new Vector();
		List ltGroup = new ArrayList();
		List ltGroupId = new ArrayList();
		List ltALLNotificationList = new ArrayList();
		boolean bNotifyList=false;
		try {
			//Check for Notification List (Whether to be pre-populated)
			Vector vtNotifyList = udDAO.getDocNotifyList(udDoc.getId());
			Vector vtNotifyAllUsers = new Vector();
			int iTotalGroup = 0;
			if (vtNotifyList.size() > 0) {
				DocNotify udDocNotify = null;
				ETSUser udUser = new ETSUser();
				// Check the first element for the Notify All Flag
				udDocNotify = (DocNotify) vtNotifyList.get(0);
				if ( !StringUtil.isNullorEmpty(udDocNotify.getNotifyAllFlag() )
					&& DocConstants.IND_YES.equals( udDocNotify.getNotifyAllFlag()) ) {
					// Means notify all was selected.
					udForm.setNotifyFlag(udDocNotify.getNotifyAllFlag());

					Vector vtProjMembers = udDAO.getProjMembersWithNames(udDoc.getProjectId());
					if( udDoc.getIbmOnly() == '0' ) {
						// All team members
						vtNotifyAllUsers = vtProjMembers;
					} else if( (udDoc.getIbmOnly() == '1') || (udDoc.getIbmOnly() == '2' ) ) {
						//All IBM members permenently
						Vector vtIBMMembers = udDAO.getIBMMembersWithNames( vtProjMembers );
						vtNotifyAllUsers = vtIBMMembers;
					}
					udForm.setNotifyAllUsers( vtNotifyAllUsers );
					for(int i=0;i<vtNotifyAllUsers.size();i++) {
						ETSUser udUserTemp = (ETSUser) vtNotifyAllUsers.get(i);
					}
				} else {
					Vector vtSubList = udDAO.getUserListWithNames( udDoc.getProjectId(), vtNotifyList);
					//Vector vtSubList = DocumentDAO.getUserList(vtNotifyList);
					for (int iCounter = 0; iCounter < vtSubList.size(); iCounter++) {
						udUser = (ETSUser) vtSubList.get(iCounter);
						udUser.setUserId( udUser.getUserId().trim() );
						// Check if user is in unsubscribe list
						if (udDAO.isUserInNotificationList(udUser.getUserId(), iDocID)) {
							vtTmpList.add(udUser);
							//ETSUser udTempUser = (ETSUser) ltTemp.get(i);
							ltALLNotificationList.add( udUser );
						}
					}
					if(vtTmpList.size()>0) {
						udForm.setDocNotifyUsers( vtTmpList );
						ltALLNotificationList.add( vtTmpList );
					}
					
					vtSubList = DocumentDAO.getGroupList(vtNotifyList);
					String[] notifyGroups = new String[vtSubList.size()];
					String strNotifyGroupID = "";
					List ltTemp = new ArrayList();
					for (int iCounter = 0; iCounter < vtSubList.size(); iCounter++) {
						udDocNotify = (DocNotify) vtSubList.get(iCounter);
						notifyGroups[iCounter] = udDocNotify.getGroupId();
						ltGroupId.add(notifyGroups[iCounter]);
						ltGroup.add( udDAO.getGroupNameById( notifyGroups[iCounter] ) );
						ltTemp = udDAO.getAvailableMembersList(udDoc.getProjectId(), notifyGroups[iCounter]);
					}

					for(int i=0;((ltTemp.size()>0)&&i<ltTemp.size() ); i++) {
						ETSUser udTempUser = (ETSUser) ltTemp.get(i);
						ltALLNotificationList.add( udTempUser );
					}
					udForm.setDocNotifyGroups( ltGroup );
					udForm.setDocNotifyGroupsId( ltGroupId );
					bNotifyList = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		return bNotifyList;
	}
}
