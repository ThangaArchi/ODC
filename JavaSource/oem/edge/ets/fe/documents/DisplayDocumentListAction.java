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
import java.util.Collections;
import java.util.Vector;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.ETSComparator;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.documents.data.DocumentDAO;
import oem.edge.ets.fe.pmo.ETSPMODao;
import oem.edge.ets.fe.pmo.ETSPMOffice;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author v2srikau
 */
public class DisplayDocumentListAction extends BaseDocumentAction {

	/** Stores the Logging object */
	private static final Log m_pdLog =
		EtsLogger.getLogger(DisplayDocumentListAction.class);

	/**
	 * @see oem.edge.ets.fe.documents.BaseDocumentAction#executeAction(
	 * org.apache.struts.action.ActionMapping, 
	 * org.apache.struts.action.ActionForm, 
	 * javax.servlet.http.HttpServletRequest, 
	 * javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward executeAction(
		ActionMapping pdMapping,
		ActionForm pdForm,
		HttpServletRequest pdRequest,
		HttpServletResponse pdResponse)
		throws Exception {

		BaseDocumentForm udForm = (BaseDocumentForm) pdForm;

		int iCurrentCatId = 0;
		int iTopCatID = 0;
		
		//pagination start index
		String strStartIndex = DocumentsHelper.getParameter(pdRequest,"from");
		if (!StringUtil.isNullorEmpty(strStartIndex)) {
			udForm.setCurrentStartIndex(Integer.parseInt(strStartIndex));
		} else {
			udForm.setCurrentStartIndex(0);
		}
			
		String strCurrentCatId = udForm.getCc();
		String strTopCatId = udForm.getTc();
		String strProjectID = DocumentsHelper.getProjectID(pdRequest);
		boolean bShowAllDocs = false;
		String strAction = udForm.getDocAction();
		if (DocConstants.ACTION_SHOW_ALL_DOCS.equals(strAction)) {
			bShowAllDocs = true;
		}
		String strSortByParam = udForm.getSortBy();
		String strSortParam = udForm.getSort();

		if (StringUtil.isNullorEmpty(strSortByParam)) {
			strSortByParam = Defines.SORT_BY_NAME_STR;
		}
		if (StringUtil.isNullorEmpty(strSortParam)) {
			strSortParam = Defines.SORT_ASC_STR;
		}

		DocumentDAO udDAO = null;
		try {

			String strPmoCat = udForm.getPmoCat();
			
			udDAO = getDAO();
			ETSProj udProject = udDAO.getProjectDetails(strProjectID);
			ETSPMOffice udPMO = null;
			String strPmoProjectId = udProject.getPmo_project_id();
			/** If PMO Project details are to be shown, read them from the DB */
			if (!bShowAllDocs
				&& (!StringUtil.isNullorEmpty(strPmoProjectId))
				&& (strCurrentCatId.equals(strTopCatId))
				&& (!strPmoProjectId.equals("0"))) {
				ETSPMODao udPmoDAO = new ETSPMODao();
				udPMO =
					udPmoDAO.getPMOfficeProjectDetails(
						udDAO.getConnection(),
						strPmoProjectId);

				udForm.setPMOffice(udPMO);
			}

			if (StringUtil.isNullorEmpty(strPmoCat)) {
				Vector vtCats = null;
				Vector vtDocs = null;

				if (!StringUtil.isNullorEmpty(udForm.getCc())) {
					iCurrentCatId = Integer.parseInt(udForm.getCc());
				}
				if (!StringUtil.isNullorEmpty(udForm.getTc())) {
					iTopCatID = Integer.parseInt(udForm.getTc());
				}

				ETSCat udCurrCat = udDAO.getCat(iCurrentCatId);
				udForm.setCategory(udCurrCat);

				ETSCat udParentCat = null;

				if (udCurrCat.getParentId() != 0) {
					udParentCat = udDAO.getCat(udCurrCat.getParentId());
					udForm.setParentCategory(udParentCat);
				} else if (bShowAllDocs) {
					udParentCat = udDAO.getCat(udCurrCat.getId());
					udForm.setParentCategory(udParentCat);
				}

				pdRequest.setAttribute(
					DocConstants.REQ_ATTR_TITLE,
					udCurrCat.getName());

				Vector vtIBMMembers =
					udDAO.getIBMMembers(udDAO.getProjMembers(strProjectID));

				EdgeAccessCntrl udEdgeAccess = getEdgeAccess(pdRequest);
				boolean bIsUserInternal = false;
				String strUserId = getEdgeAccess(pdRequest).gIR_USERN;
				for (int iCounter = 0;
					iCounter < vtIBMMembers.size();
					iCounter++) {
					ETSUser udUser = (ETSUser) vtIBMMembers.get(iCounter);
					if (udUser.getUserId().equals(strUserId)) {
						bIsUserInternal = true;
						break;
					}
				}

				if (bShowAllDocs) {
					vtCats = new Vector();
					vtDocs =
						udDAO.getAllDocs(
							strProjectID,
							strSortByParam,
							strSortParam,
							(isSuperAdmin(pdRequest)
								|| isExecutive(pdRequest)
								|| getUserRole(pdRequest).equals(Defines.WORKSPACE_OWNER)),
							udEdgeAccess.gIR_USERN);
				} else {
					vtCats =
						udDAO.getSubCats(
							iCurrentCatId,
							strSortByParam,
							strSortParam);
					vtDocs =
						udDAO.getDocs(
							iCurrentCatId,
							strSortByParam,
							strSortParam,
							(isSuperAdmin(pdRequest)
								|| isExecutive(pdRequest)
								|| getUserRole(pdRequest).equals(Defines.WORKSPACE_OWNER)),
							udEdgeAccess.gIR_USERN);
				}

				Vector vtNewDocs = new Vector();
				Vector vtNewCats = new Vector();
				if (!bIsUserInternal && !isExecutive(pdRequest) && !isSuperAdmin(pdRequest)) {
					// Filter out IBM ONLY documents
					for (int iCounter = 0;
						iCounter < vtDocs.size();
						iCounter++) {
						ETSDoc udDoc = (ETSDoc) vtDocs.get(iCounter);
						if (!udDoc.isIbmOnlyOrConf()) {
							vtNewDocs.add(udDoc);
						}
					}
					// Filter out IBM ONLY categories
					for (int iCounter = 0;
						iCounter < vtCats.size();
						iCounter++) {
						ETSCat udCat = (ETSCat) vtCats.get(iCounter);
						if (!udCat.isIbmOnlyOrConf()) {
							vtNewCats.add(udCat);
						}
					}
					vtDocs.removeAllElements();
					vtDocs.addAll(vtNewDocs);
					vtCats.removeAllElements();
					vtCats.addAll(vtNewCats);
				}

				boolean bIsWorkspaceMember =
					getUserRole(pdRequest).equals(Defines.WORKSPACE_MEMBER);
				boolean bIsWorkspaceManager = 
					getUserRole(pdRequest).equals(Defines.WORKSPACE_MANAGER);
					
				if (m_pdLog.isDebugEnabled()) {
					m_pdLog.debug("bIsWorkspaceMember : " + bIsWorkspaceMember);
					m_pdLog.debug("bIsWorkspaceManager: " + bIsWorkspaceManager);
				}

				boolean bHasExpiryFilter = false;
				String strUserRole = getUserRole(pdRequest);
				// Check for Expired Documents
				if (!isSuperAdmin(pdRequest)
					&& !strUserRole.equals(Defines.WORKSPACE_OWNER)
					&& !strUserRole.equals(Defines.WORKSPACE_MANAGER)) {
					Vector vtExpired = new Vector();
					for (int iCounter = 0;
						iCounter < vtDocs.size();
						iCounter++) {
						ETSDoc udDoc = (ETSDoc) vtDocs.get(iCounter);
						if (udDoc.hasExpired()) {
							if ((bIsWorkspaceMember || bIsWorkspaceManager)
								&& udDoc.getUserId().equals(
									udEdgeAccess.gIR_USERN)) {
								vtExpired.add(udDoc);
							}
							bHasExpiryFilter = true;
						} else {
							vtExpired.add(udDoc);
						}
					}
					vtDocs.removeAllElements();
					vtDocs.addAll(vtExpired);
				}

				if (strSortByParam.equals(Defines.SORT_BY_AUTH_STR)
					|| strSortByParam.equals(Defines.SORT_BY_TYPE_STR)) {
					byte sortOrder = ETSComparator.getSortOrder(strSortByParam);
					byte sortAD = ETSComparator.getSortBy(strSortParam);
					Collections.sort(
						vtCats,
						new ETSComparator(sortOrder, sortAD));
					Collections.sort(
						vtDocs,
						new ETSComparator(sortOrder, sortAD));
				}

				udForm.setCategories(vtCats);
				udForm.setDocuments(vtDocs);
				bHasExpiryFilter = bHasExpiryFilter && (vtDocs.size() == 0) && (vtCats.size() == 0);
				if (bHasExpiryFilter) {
					pdRequest.setAttribute(
					        DocConstants.REQ_ATTR_HAS_EXPIRED_DOCS, 
					        String.valueOf(DocConstants.TRUE_FLAG));
				}
			} else {
				ETSPMODao pmoDao = new ETSPMODao();
				Vector vDetails =
					pmoDao.getPMOfficeObjects(
						udDAO.getConnection(),
						strPmoProjectId);
				ETSPMOffice udPMOOffice =
					pmoDao.getPMOfficeObjectDetail(
						udDAO.getConnection(),
						strPmoProjectId,
						strPmoCat);
				Vector vtPMOCats =
					pmoDao.getPMOfficeSubCats(
						udDAO.getConnection(),
						strPmoProjectId,
						strPmoCat,
						strSortByParam,
						strSortParam);
				Vector vtPMODocs =
					pmoDao.getPMODocuments(
						udDAO.getConnection(),
						strPmoCat,
						strPmoProjectId,
						strSortByParam,
						strSortParam);

				ETSPMOffice udParentPMO = 
					pmoDao.getPMOfficeObjectDetail(
						udDAO.getConnection(),
						strPmoProjectId,
						udPMOOffice.getPMO_Parent_ID());
				if (StringUtil.isNullorEmpty(udParentPMO.getPMOID())) {
					udParentPMO.setPMOID(strTopCatId);
					udParentPMO.setName("Documents");
				}
				pdRequest.setAttribute(
					"rpmcat",
					udParentPMO
				);
				//udForm.getBreadCrumbs().add(0, udParentPMO);

				pdRequest.setAttribute(
					DocConstants.REQ_ATTR_TITLE,
					udPMOOffice.getName());

				if (strSortByParam.equals(Defines.SORT_BY_AUTH_STR)
					|| strSortByParam.equals(Defines.SORT_BY_TYPE_STR)) {
					byte sortOrder = ETSComparator.getSortOrder(strSortByParam);
					byte sortAD = ETSComparator.getSortBy(strSortParam);
					Collections.sort(
						vtPMOCats,
						new ETSComparator(sortOrder, sortAD));
					Collections.sort(
						vtPMODocs,
						new ETSComparator(sortOrder, sortAD));
				}

				udForm.setCategories(vtPMOCats);
				udForm.setDocuments(vtPMODocs);
				udForm.setPMOffice(null);
			}
			// Sets the Total view Records =Documents + categories 
			udForm.setAllViewDocsCount(udForm.getCategories().size() + udForm.getDocuments().size());
			subListDisplayData(udForm,udForm.getCurrentStartIndex());
			
		} catch (SQLException e) {
			e.printStackTrace(System.out);
		} finally {
			super.cleanup(udDAO);
		}

		pdRequest.setAttribute(DocConstants.DOC_FORM, udForm);

		return pdMapping.findForward(FWD_SUCCESS);
	}
	
	private void subListDisplayData(BaseDocumentForm udForm, int intPageStartIndex) {
		Vector vtCats = udForm.getCategories();
		Vector vtDocs = udForm.getDocuments();
		List subCatList = null;
		List subDocsList = null;
		int intDocStartIndex;
		int intDocEndIndex;
		
		int intPageEndIndex = intPageStartIndex + Defines.DOC_PAGINATE_RECORD_SIZE;
		if (intPageStartIndex >= vtCats.size()) {
			intDocStartIndex = intPageStartIndex - vtCats.size();
			intDocEndIndex = intPageEndIndex - vtCats.size();
			vtCats = new Vector();
			subCatList = (List)vtCats;
		} else {
			if (vtCats.size() > Defines.DOC_PAGINATE_RECORD_SIZE){
				if (vtCats.size() <= intPageEndIndex) {
					subCatList = (List)vtCats.subList(intPageStartIndex,vtCats.size());
				} else {
					subCatList = (List)vtCats.subList(intPageStartIndex,intPageEndIndex);
				}
				intDocStartIndex = 0;
				intDocEndIndex = intPageEndIndex - intPageStartIndex - subCatList.size();
			} else {
				subCatList = (List)vtCats;
				intDocStartIndex = 0;
				intDocEndIndex = intPageEndIndex - intPageStartIndex - subCatList.size();
			}
		}
		
		//Paginate Docs
		m_pdLog.debug("Paginate page start::end index - " + intPageStartIndex + "::" + intPageEndIndex);
		m_pdLog.debug("Paginate for Docs start::end index - " + intDocStartIndex + "::" + intDocEndIndex);
		if ((intDocEndIndex - intDocStartIndex) > 0) {
			if (vtDocs.size() >  (intDocEndIndex - intDocStartIndex)) {
				if (vtDocs.size() <= intDocEndIndex){
					intDocEndIndex = vtDocs.size();
					subDocsList = (List) vtDocs.subList(intDocStartIndex,intDocEndIndex);
				}else {
					subDocsList = (List) vtDocs.subList(intDocStartIndex,intDocEndIndex);
				}
			} else {
		    	intDocEndIndex = vtDocs.size();
		    	subDocsList = (List) vtDocs.subList(intDocStartIndex,intDocEndIndex);
		    }
		} else {
			vtDocs = new Vector();
			subDocsList = (List)vtDocs;
		}
		
		m_pdLog.debug("Paginate View Docs Returned - Cats Records::" + subCatList.size()
						+ " Documents Records::" + subDocsList.size());
		//vtCats.removeAllElements();
		//vtDocs.removeAllElements();
		
		Vector vtCats1 = new Vector();
		Vector vtDocs1 = new Vector();
		vtCats1.addAll(subCatList);
		vtDocs1.addAll(subDocsList);
		
		udForm.setCategories(vtCats1);
		udForm.setDocuments(vtDocs1);
		
	}
}
