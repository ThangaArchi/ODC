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
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.DocTranTypes;
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
public class MoveCatAction extends BaseDocumentAction {

	/** Stores the Logging object */
	private static final Log m_pdLog = EtsLogger.getLogger(MoveCatAction.class);

	private static final String ERR_MOVE_CAT_EMPTY = "category.move.empty";
	private static final String ERR_TARGET_CAT_EMPTY = "category.target.empty";
	private static final String ERR_FOLDER_ACCESS_NOTAUTH =
		"folder.access.notauth";
	private static final String ERR_FOLDER_MOVE_NOTAUTH = "folder.move.notauth";
	private static final String ERR_FOLDER_MOVETO_NOTAUTH =
		"folder.moveto.notauth";
	private static final String ERR_CREATE_EXISTING_CAT = "category.name.exist";
	private static final String ERR_FOLDER_DESTINATION  = "category.move.error.destination";

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

		int iCurrentCatId = 0;
		String strProjectID = DocumentsHelper.getProjectID(pdRequest);

		if (!StringUtil.isNullorEmpty(udForm.getCc())) {
			iCurrentCatId = Integer.parseInt(udForm.getCc());
		}

		String strTargetCatID =
			pdRequest.getParameter(DocConstants.PARAM_TARGETCATEGORY);

		String strConfirm = pdRequest.getParameter("confirm");

		int iTargetCatID =
			StringUtil.isNullorEmpty(strTargetCatID)
				? 0
				: Integer.parseInt(strTargetCatID);
		int iMoveCatID = udForm.getCategory().getId();

		DocumentDAO udDAO = null;
		try {
			udDAO = getDAO();
			ETSCat udMoveCat = udDAO.getCat(iMoveCatID, strProjectID);
			ETSCat udTargetCat = udDAO.getCat(iTargetCatID, strProjectID);

			ActionErrors pdErrors = validate(udMoveCat, udTargetCat);
			if (pdErrors.size() > 0) {
			  //To avoid sending null request 
			  String[] strCategories = { String.valueOf( udMoveCat.getId() ) };  //We know that this array should have only one element
			  udForm.setSubmitCategories(strCategories); 
			  throw new DocumentException(pdErrors);
			}
			EdgeAccessCntrl udEdgeAccess = getEdgeAccess(pdRequest);
			if (udMoveCat.isIbmOnlyOrConf()
				&& (!(udEdgeAccess.gDECAFTYPE.trim()).equalsIgnoreCase("I"))) {
				throw new DocumentException(ERR_FOLDER_ACCESS_NOTAUTH);
			}
			if ((!udMoveCat.getUserId().equals(udEdgeAccess.gIR_USERN))
				&& (!udDAO
					.hasProjectPriv(
						udEdgeAccess.gIR_USERN,
						strProjectID,
						Defines.ADMIN))
				&& (!isSuperAdmin(pdRequest))) {
				throw new DocumentException(ERR_FOLDER_MOVE_NOTAUTH);
			}
			if (udTargetCat.isIbmOnlyOrConf()
				&& (!(udEdgeAccess.gDECAFTYPE.trim()).equalsIgnoreCase("I"))) {
				throw new DocumentException(ERR_FOLDER_MOVETO_NOTAUTH);
			}

			// Add the Category and Target Category to the Form
			udForm.setCategory(udMoveCat);
			Vector vtCategories = new Vector();
			vtCategories.add(udTargetCat);
			udForm.setCategories(vtCategories);

			// if strConfirm is present means we have already shown the confirm
			// page
			if (!StringUtil.isNullorEmpty(strConfirm)) {
				String[] result =
					moveCat(
						strProjectID,
						iCurrentCatId,
						udMoveCat,
						udTargetCat,
						udEdgeAccess,
						udDAO);
				String res = (String) result[0];
				String strIBMOnly = (String) result[1];
				if (res.equals("1")) {
					/*pdResponse.sendRedirect("ETSProjectsServlet.wss?action=movecat2&proj="+strProjectID+"&tc="+topcat+"&cc="+current+"&linkid="+);*/
				} else {
					/*pdResponse.sendRedirect("ETSProjectsServlet.wss?action=movecatconf&proj="+strProjectID+"&tc="+topcat+"&cc="+movetocatid+"&movecatid="+movecatid+"&i="+res_msg+"&linkid="+linkid);*/
				}

				udForm.setIBMOnly(strIBMOnly);
			} else {

				udForm.setUsers(
					udDAO.getCatSubTreeOwners(
						new Vector(),
						udMoveCat.getId(),
						strProjectID,
						udEdgeAccess.gIR_USERN,
						getUserRole(pdRequest),
						Defines.UPDATE,
						true));

			}

			return pdMapping.findForward(FWD_SUCCESS);

		} catch (SQLException e) {
			e.printStackTrace(System.err);
			throw e;
		} catch(Exception e) {
			e.printStackTrace(System.err);
			throw e;
		}
		finally {
			super.cleanup(udDAO);
		}
	}

	/**
	 * @param strProjectId
	 * @param strParentID
	 * @param udMoveCat
	 * @param udTargetCat
	 * @param udEdgeAccess
	 * @return
	 */
	private String[] moveCat(
		String strProjectId,
		int iParentID,
		ETSCat udMoveCat,
		ETSCat udTargetCat,
		EdgeAccessCntrl udEdgeAccess,
		DocumentDAO udDAO)
		throws Exception {
		boolean bSuccess = false;
		char cIBMOnly = 'x';

		try {
			if (((udMoveCat.getIbmOnly() == Defines.ETS_PUBLIC)
				&& udTargetCat.isIbmOnlyOrConf())
				|| ((udMoveCat.getIbmOnly() == Defines.ETS_IBM_ONLY)
					&& (udTargetCat.getIbmOnly() == Defines.ETS_IBM_CONF))) {
				if (udTargetCat.getIbmOnly() == Defines.ETS_IBM_ONLY) {
					cIBMOnly = Defines.ETS_IBM_ONLY;
				} else {
					cIBMOnly = Defines.ETS_IBM_CONF;
				}
			}

			bSuccess =
				udDAO.updateParentId(
					Defines.NODE_CAT,
					udMoveCat.getId(),
					udTargetCat.getId(),
					cIBMOnly,
					strProjectId,
					udEdgeAccess.gIR_USERN);
		} catch (Exception e) {
			e.printStackTrace();

		}

		if (bSuccess) {
			return new String[] { "0", String.valueOf(cIBMOnly)};
		} else {
			return new String[] { "1", "error" };
		}

	}

	/**
	 * @param udForm
	 * @return
	 */
	private ActionErrors validate(ETSCat udMoveCat, ETSCat udTargetCat)  
		throws Exception {
		ActionErrors pdErrors = new ActionErrors();

		if (udMoveCat == null) {
			pdErrors.add(
				DocConstants.MSG_USER_ERROR,
				new ActionMessage(ERR_MOVE_CAT_EMPTY));
		} else if (udTargetCat == null) {
			pdErrors.add(
				DocConstants.MSG_USER_ERROR,
				new ActionMessage(ERR_TARGET_CAT_EMPTY));
		} else if( udMoveCat.getParentId() == udTargetCat.getId() ) {
			pdErrors.add(
					DocConstants.MSG_USER_ERROR,
					new ActionMessage(ERR_FOLDER_DESTINATION));
		} else {
			DocumentDAO tempDAO = null;
			try {
				tempDAO = getDAO();
		        ETSCat udCatCheckExists = tempDAO.getCatByName( 
		        								udMoveCat.getName(), 
												udTargetCat.getId(), 
												udTargetCat.getProjectId() );
				if( udCatCheckExists != null ) {
					pdErrors.add( DocConstants.MSG_USER_ERROR, new ActionMessage(ERR_CREATE_EXISTING_CAT) );
				}
			} catch (SQLException e) {
				m_pdLog.error(e);
			} finally {
				super.cleanup(tempDAO);
			}
		}
		
		return pdErrors;
	}
}
