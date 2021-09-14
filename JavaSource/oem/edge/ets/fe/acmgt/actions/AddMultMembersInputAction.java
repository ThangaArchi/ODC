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

package oem.edge.ets.fe.acmgt.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.ets.fe.acmgt.actions.BaseAddMemberForm;
import oem.edge.ets.fe.acmgt.dao.AddMembrToWrkSpcDAO;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * @author Suresh
 *
 */
public class AddMultMembersInputAction extends BaseAcmgtAction {
	
	private static Log logger = EtsLogger.getLogger(AddMultMembersInputAction.class);
	public static final String VERSION = "1.3";

	
	public AddMultMembersInputAction() {
		super();
	}

	/**
	 * @see oem.edge.ets.fe.acmgt.actions.BaseAcmgtAction#executeAction(
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
		
		
		
		String strForward = "";
		pdRequest.getSession().removeAttribute("addMemberForm");
				
		BaseAddMemberForm addMemberForm = new BaseAddMemberForm();
		
		String sLink = AmtCommonUtils.getTrimStr(pdRequest.getParameter("linkid"));
		String projectidStr = AmtCommonUtils.getTrimStr(pdRequest.getParameter("proj"));
		String topCatId = AmtCommonUtils.getTrimStr(pdRequest.getParameter("tc"));
		
		addMemberForm.setLinkid(sLink);
		addMemberForm.setProj(projectidStr);
		addMemberForm.setTc(topCatId);
		
		logger.debug("Input Action :: LINKID == "+addMemberForm.getLinkid());
		logger.debug("Input Action :: PROJ == "+addMemberForm.getProj());
		logger.debug("Input Action :: TC == "+addMemberForm.getTc());
		
		pdRequest.setAttribute("proj",projectidStr);
		
		pdForm = addMemberForm; 
		strForward = "success";
				
		ActionErrors pdErrors = null;
						
		return pdMapping.findForward(strForward);
	}

	/**
	 * @param udForm
	 * @param udDAO
	 * @return
	 */
	private ActionErrors validate(
		AddMembrToWrkSpcDAO udDAO,
		BaseAddMemberForm udForm)
		throws Exception {
		ActionErrors pdErrors = new ActionErrors();
		
			pdErrors.add("",
					new ActionMessage(""));
		

		return pdErrors;
	}
	
} //end of class
