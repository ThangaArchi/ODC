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
import oem.edge.ets.fe.documents.data.DocumentDAO;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author v2srikau
 */
public class DisplayFolderTreeAction extends BaseDocumentAction {

	/** Stores the Logging object */
	private static final Log m_pdLog =
		EtsLogger.getLogger(DisplayFolderTreeAction.class);

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

		String strTopCatId = udForm.getTc();
		String strProjectID = DocumentsHelper.getProjectID(pdRequest);
		DocumentDAO udDAO = null;
		try {
		    udDAO = getDAO();

			Vector vtCats =
				udDAO.getSubCats(
					Integer.parseInt(strTopCatId),
					Defines.SORT_BY_TYPE_STR,
					Defines.SORT_ASC_STR);
			
			EdgeAccessCntrl udEdgeAccess = getEdgeAccess(pdRequest);
			boolean bIsIBMer = true;
			if (!"I".equals(udEdgeAccess.gINTERNAL)) {
			    // Means user is external users. so remove IBM Only folders
			    removeIBMOnly(vtCats);
			    bIsIBMer = false;
			}
			if (vtCats !=null && vtCats.size() > 0) {
			    for(int i=0; i < vtCats.size(); i++) {
			        ETSCat udCat = (ETSCat) vtCats.get(i);
			        processCat(udCat, udDAO, bIsIBMer);
			    }
			}
			
			udForm.setCategories(vtCats);
		}
		finally {
		    super.cleanup(udDAO);
		}
		return pdMapping.findForward(FWD_SUCCESS);
	}
	
	/**
	 * @param udCat
	 * @param udDAO
	 * @param bIsIBMer
	 * @throws SQLException
	 */
	private void processCat(ETSCat udCat,DocumentDAO udDAO, boolean bIsIBMer) throws SQLException {
        Vector vtSubCats = udDAO.getSubCats(
                udCat.getId(),
                Defines.SORT_BY_TYPE_STR,
				Defines.SORT_ASC_STR);
        if (!bIsIBMer) {
            removeIBMOnly(vtSubCats);
        }
        if (vtSubCats.size() > 0) {
            for(int i=0; i < vtSubCats.size(); i++) {
                processCat((ETSCat) vtSubCats.get(i), udDAO, bIsIBMer);
            }
        }
        udCat.setSubCats(vtSubCats);
	}
	
	/**
	 * @param vtFolders
	 */
	private void removeIBMOnly(Vector vtFolders) {
	    Vector vtTmp = new Vector();
		if (vtFolders !=null && vtFolders.size() > 0) {
		    for(int i=0; i < vtFolders.size(); i++) {
		        ETSCat udCat = (ETSCat) vtFolders.get(i);
		        if (!udCat.isIbmOnlyOrConf()) {
		            vtTmp.add(udCat);
		        }
		    }
		}
		vtFolders = new Vector();
		vtFolders.addAll(vtTmp);
	}
}
