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
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.Metrics;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.DocTranTypes;
import oem.edge.ets.fe.documents.common.MetricsConstants;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.documents.data.DocumentDAO;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * @author v2srikau
 */
public class AddCategoryAction extends BaseDocumentAction {

	/** Stores the Logging object */
	private static final Log m_pdLog =
		EtsLogger.getLogger(AddCategoryAction.class);

	private static final String ERR_CAT_NAME = "category.name.error";

	private static final String ERR_SEC_NOTSEL =
		"security.classification.error";

	private static final String ERR_CREATE_EXISTING_CAT = "category.name.exist";
	private static final String ERR_CREATE_MEETINGS_CAT = "category.name.meetings";
	
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
		ActionErrors pdErrors = validate(udForm, pdMapping, pdRequest);

		if (pdErrors.size() > 0) {
			throw new DocumentException(pdErrors);
		}

		// If we reach here, means validations succeeded.
		DocumentDAO udDAO = null;
		try {

			udDAO = getDAO();

			EdgeAccessCntrl udEdgeAccess = getEdgeAccess(pdRequest);
			// Check if Transaction Type is UPDCAT
			if (getTransType(pdMapping).equals(DocTranTypes.TRAN_TYPE_UPDCAT)) {

				// do update code here.	
				char cIBMOnly = udForm.getCategory().getIbmOnly();
				String strOldIBMOnly = pdRequest.getParameter("oldibm");
				char cOldIBMOnly = strOldIBMOnly.charAt(0);
				String strCatID =
					pdRequest.getParameter(DocConstants.PARAM_UPDATECAT);
				String strProjectID = DocumentsHelper.getProjectID(pdRequest);
				String strOption = pdRequest.getParameter("propOpt");

				String strConfirm = pdRequest.getParameter("confirm");

				boolean bContinue = false;
				if (StringUtil.isNullorEmpty(strConfirm)) {
					if ((cOldIBMOnly == Defines.ETS_IBM_ONLY)
						&& (cIBMOnly == Defines.ETS_PUBLIC)) {
						return pdMapping.findForward(FWD_SUCCESS + "_confirm");
					} else {
						boolean bOwnsAll = true;
						if (cOldIBMOnly < cIBMOnly) {
							String strUserRole = getUserRole(pdRequest);
							bOwnsAll =
								udDAO.getValidCatSubTree(
									Integer.parseInt(strCatID),
									strProjectID,
									udEdgeAccess.gIR_USERN,
									strUserRole,
									Defines.UPDATE,
									true);
							if (!bOwnsAll) {
								Vector vtCatTree =
									udDAO.getCatSubTreeOwners(
										new Vector(),
										Integer.parseInt(strCatID),
										strProjectID,
										udEdgeAccess.gIR_USERN,
										strUserRole,
										Defines.UPDATE,
										true);
								udForm.setCategories(vtCatTree);
								return pdMapping.findForward(
									FWD_SUCCESS + "_confirm");
							} else {
								bContinue = true;
							}
						} else {
							bContinue = true;
						}
					}
				} else {
					bContinue = true;
				}
				if (bContinue) {
					// MEANS we have already shown the confirm page.
					// and the user has clicked CONTINUE
					String[] result =
						updateCat(
							strProjectID,
							Integer.parseInt(strCatID),
							udForm.getCategory().getName(),
							StringUtil.EMPTY_STRING,
							cIBMOnly,
							strOption,
							udEdgeAccess,
							udDAO);
					String res = (String) result[0];
					String res_msg = (String) result[1];
					if (res.equals("1")) {
						/*
						response.sendRedirect(
							"ETSProjectsServlet.wss?action=updatecat&proj="
								+ strProjectID
								+ "&tc="
								+ topcat
								+ "&cc="
								+ current
								+ "&msg="
								+ res_msg
								+ "&linkid="
								+ linkid);
						*/
					} else {
						/*
						response.sendRedirect(
							"ETSProjectsServlet.wss?proj="
								+ strProjectID
								+ "&tc="
								+ topcat
								+ "&cc="
								+ current
								+ "&linkid="
								+ linkid);
						*/
					}

				}

				return pdMapping.findForward(FWD_SUCCESS);
			} else {
				// THIS IS ADD FUNCTIONALITY
				String strCategoryName = udForm.getCategory().getName();
				char cIBMOnly = udForm.getCategory().getIbmOnly();

				int iCurrentCatId = 0;
				if (!StringUtil.isNullorEmpty(udForm.getCc())) {
					iCurrentCatId = Integer.parseInt(udForm.getCc());
				}

				ETSCat udParentCategory = udDAO.getCat(iCurrentCatId);

				String strProjectID = DocumentsHelper.getProjectID(pdRequest);

				ETSProj udProj = udDAO.getProjectDetails(strProjectID);

				ETSCat udNewCategory = new ETSCat();
				udNewCategory.setName(strCategoryName);
				udNewCategory.setProjectId(strProjectID);
				udNewCategory.setDescription(StringUtil.EMPTY_STRING);
				udNewCategory.setParentId(iCurrentCatId);
				udNewCategory.setUserId(udEdgeAccess.gIR_USERN);
				udNewCategory.setOrder(0);
				udNewCategory.setViewType(udParentCategory.getViewType());
				udNewCategory.setProjDesc(0);
				udNewCategory.setPrivs(StringUtil.EMPTY_STRING);
				udNewCategory.setIbmOnly(cIBMOnly);
				udNewCategory.setCPrivate(false);

				String[] strResults = udDAO.addCat(udNewCategory);
				int iSuccess = Integer.parseInt(strResults[0]);
				if (iSuccess == 0) {

					if (udProj
						.getProjectOrProposal()
						.equals(DocConstants.TYPE_PROJECT)) {
						Metrics.appLog(
							udDAO.getConnection(),
							udEdgeAccess.gUSERN,
							MetricsConstants.PROJECT_CAT_ADD);
					} else {
						Metrics.appLog(
							udDAO.getConnection(),
							udEdgeAccess.gUSERN,
							MetricsConstants.PROPOSAL_CAT_ADD);
					}
				}
			}
		} catch (SQLException e) {
		    m_pdLog.error(e);
		} finally {
			super.cleanup(udDAO);
		}

		return pdMapping.findForward(FWD_SUCCESS);
	}

	/**
	 * @param udForm
	 * @return
	 */
	private ActionErrors validate(BaseDocumentForm udForm, ActionMapping pdMapping, HttpServletRequest pdRequest ) 
		throws Exception {

		ActionErrors pdErrors = new ActionErrors();

		ETSCat udCat = udForm.getCategory();
		String strCategoryName = udCat.getName();

		if (StringUtil.isNullorEmpty(strCategoryName)
			|| strCategoryName.length() > DocConstants.MAX_FOLDER_NAME_LENGTH) {
			pdErrors.add(
				DocConstants.MSG_USER_ERROR,
				new ActionMessage(ERR_CAT_NAME));
		} else if (DocConstants.SEL_OPT_NONE.equals(udForm.getCategory().getIBMOnlyStr())) {
			pdErrors.add(
				DocConstants.MSG_USER_ERROR,
				new ActionMessage(ERR_SEC_NOTSEL));
		} else {
			DocumentDAO udDAO = null;
			if ( getTransType(pdMapping).equals(DocTranTypes.TRAN_TYPE_ADDCAT) ) {
			    // Check that the name that the user has entered is not Meeting Documents
			    if (strCategoryName.equalsIgnoreCase(DocConstants.MEETINGS_DOC_FOLDER) && udForm.getTc().equals(udForm.getCc())) {
			        pdErrors.add( DocConstants.MSG_USER_ERROR, new ActionMessage(ERR_CREATE_MEETINGS_CAT) );
			        return pdErrors;
			    }
			}
			try {
					udDAO = getDAO();
					int iCurrentCatId = 0;
					if (!StringUtil.isNullorEmpty(udForm.getCc())) {
						iCurrentCatId = Integer.parseInt(udForm.getCc());
					}
	ETSCat udCatCheckExists = udDAO.getCatByName( strCategoryName, iCurrentCatId, DocumentsHelper.getProjectID(pdRequest) );
				if ( getTransType(pdMapping).equals(DocTranTypes.TRAN_TYPE_ADDCAT) ) {
					if( udCatCheckExists != null ) {
						pdErrors.add( DocConstants.MSG_USER_ERROR, new ActionMessage(ERR_CREATE_EXISTING_CAT) );
					}
				}
				else {
					String strCatID = pdRequest.getParameter(DocConstants.PARAM_UPDATECAT);
				    if( (udCatCheckExists != null) && 
				    		(!String.valueOf(udCatCheckExists.getId()).equals(strCatID)) ) {
				    	pdErrors.add( DocConstants.MSG_USER_ERROR, new ActionMessage(ERR_CREATE_EXISTING_CAT) );
				    }
				}
			} catch (SQLException e) {
				m_pdLog.error(e);
			} finally {
				super.cleanup(udDAO);
			}
		}

		return pdErrors;
	}


	/**
	 * @param strProjectID
	 * @param iUpdCatID
	 * @param strName
	 * @param strDescription
	 * @param cIBMOnly
	 * @param strOption
	 * @param udEdge
	 * @param udDAO
	 * @return
	 */
	private String[] updateCat(
		String strProjectID,
		int iUpdCatID,
		String strName,
		String strDescription,
		char cIBMOnly,
		String strOption,
		EdgeAccessCntrl udEdge,
		DocumentDAO udDAO) {
		try {
			Vector p = new Vector();
			if ((ETSUtils.checkUserRole(udEdge, strProjectID, udDAO.getConnection()))
				.equals(Defines.ETS_ADMIN)) {
				p = udDAO.getProject(strProjectID);
			} else {
				p = udDAO.getProjects(udEdge.gIR_USERN, strProjectID);
			}

			if (p.size() <= 0) {
				//writer.println("error occurred: invalid project id for this user.");
				m_pdLog.error("put bad projet id message here");
				return new String[] { "1", "3" };
			} else {
				ETSProj udProj = (ETSProj) p.elementAt(0);

				ETSCat udCat = udDAO.getCat(iUpdCatID);
				if (udCat != null) {
					//update cat here
					if (!StringUtil.isNullorEmpty(strName)) {
						udCat.setName(strName);
					}
					String userRole =
						ETSUtils.getUserRole(
							udEdge.gIR_USERN,
							udProj.getProjectId(),
							udDAO.getConnection());
					boolean bIsAdmin = false;
					if (userRole.equals(Defines.WORKSPACE_OWNER)
						|| userRole.equals(Defines.WORKSPACE_MANAGER)
						|| userRole.equals(Defines.ETS_ADMIN)) {
						bIsAdmin = true;
					}

					if (cIBMOnly == 'X') { //ibm only not an option
						udCat.setIbmOnly(udCat.getIbmOnly());
					} else {
						if (udCat.getIbmOnly() == cIBMOnly) {
							//ibmonly not changed
							strOption = "1";
						} else { //ibmonly changed
							if (cIBMOnly == Defines.ETS_IBM_ONLY
								|| cIBMOnly == Defines.ETS_IBM_CONF) {
								strOption = "3"; //propogate
								bIsAdmin = true; // to get all ids
							}
						}
						udCat.setIbmOnly(cIBMOnly);
					}

					boolean bSuccess =
						udDAO.updateCat(
							udCat,
							bIsAdmin,
							udEdge.gIR_USERN,
							strOption);
					if (bSuccess) {
						if (udProj
							.getProjectOrProposal()
							.equals(DocConstants.TYPE_PROJECT)) {
							Metrics.appLog(
								udDAO.getConnection(),
								udEdge.gIR_USERN,
								MetricsConstants.PROJECT_CAT_UPDATE);
						} else { //proposal
							Metrics.appLog(
								udDAO.getConnection(),
								udEdge.gIR_USERN,
								MetricsConstants.PROPOSAL_CAT_UPDATE);
						}
						return new String[] { "0", "success" };
					} else {
						return new String[] { "1", "4" };
					}
				} else {
					return new String[] { "1", "4" };
				}
			}
		} catch (Exception e) {
			//writer.println("error occurred");
			m_pdLog.error(e);
			return new String[] { "1", "4" };

		}
	}
}
