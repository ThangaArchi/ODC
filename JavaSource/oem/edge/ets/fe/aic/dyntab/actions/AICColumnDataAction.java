/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004                                     */
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

package oem.edge.ets.fe.aic.dyntab.actions;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import oem.edge.ets.fe.aic.common.exception.AICApplicationException;
import oem.edge.ets.fe.aic.common.exception.AICSystemException;
import oem.edge.ets.fe.aic.common.util.AICUtil;
import oem.edge.ets.fe.aic.dyntab.bdlg.AICDynTabTemplateBusinessDelegate;
import oem.edge.ets.fe.aic.dyntab.vo.AICTemplateColumnVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTemplateVO;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @version 	1.0
 * @author
 */
public class AICColumnDataAction extends Action {
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";
		
	private static Log logger =
				EtsLogger.getLogger(AICColumnDataAction.class);
	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("-> execute");
		}
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value
		HttpSession session = request.getSession(true);
		AICTemplateVO objAICTemplateVO =
			(AICTemplateVO) session.getAttribute("Template");
			
			String strColumnId = (String)session.getAttribute("columnId");

		AICColumnDataForm aICColumnDataForm = (AICColumnDataForm) form;

		AICTemplateColumnVO objAICTemplateColumnVO = new AICTemplateColumnVO();
		objAICTemplateColumnVO.setActive("Y");
		if(strColumnId==null || strColumnId.equals(""))
		{
			
				objAICTemplateColumnVO.setColumnId(AICUtil.getUniqueId());
			
				
		}
		else
		{
			
				objAICTemplateColumnVO.setColumnId(strColumnId);
			
			
		}
		
		objAICTemplateColumnVO.setColumnName(
			aICColumnDataForm.getColumn_name());
		int intColumnOrder =
			Integer.parseInt(aICColumnDataForm.getColumn_order());
		objAICTemplateColumnVO.setColumnOrder(intColumnOrder);
		objAICTemplateColumnVO.setColumnType(
			aICColumnDataForm.getColumn_type());
		objAICTemplateColumnVO.setRequired(aICColumnDataForm.getIs_required());

		//objAICTemplateVO.getTemplateColVOCollection().add(objAICTemplateColumnVO);

		AICTemplateVO anotherAICTemplateVO = new AICTemplateVO();
		anotherAICTemplateVO.setTemplateId(objAICTemplateVO.getTemplateId());
		anotherAICTemplateVO.setTemplateName(objAICTemplateVO.getTemplateName());
		anotherAICTemplateVO.setTemplateUpdatedate(objAICTemplateVO.getTemplateUpdatedate());
		anotherAICTemplateVO.setActive(objAICTemplateVO.getActive());
		
		
		Collection c = new ArrayList();
		c.add(objAICTemplateColumnVO);
		anotherAICTemplateVO.setTemplateColVOCollection(c);

		AICDynTabTemplateBusinessDelegate objAICDynTabTemplateBusinessDelegate =
			new AICDynTabTemplateBusinessDelegate();
		try {
			if(strColumnId==null || strColumnId.equals(""))
			{
				objAICTemplateVO =(	AICTemplateVO) objAICDynTabTemplateBusinessDelegate.createTemplateWithColumns(anotherAICTemplateVO);
			}
			else
			{
				objAICTemplateVO =(	AICTemplateVO) objAICDynTabTemplateBusinessDelegate.editColumnFromTemplate(anotherAICTemplateVO);
			}
			session.setAttribute("Template", objAICTemplateVO);	

		} catch (AICApplicationException ape) {
			if (logger.isDebugEnabled()) {
				logger.debug("App Message = " + ape.getMessage());
			}
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.detail",ape.getMessage()));

		} catch (AICSystemException ase) {
			if (logger.isDebugEnabled()) {
				logger.debug("Sys Message = " + ase.getMessage());
			}
			 
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.detail",ase.getMessage()));

		} catch (Exception ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("Exp Message = " + ex.getMessage());
			}
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.detail",ex.getMessage()));

		}
		
		forward = mapping.findForward("success");
		
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			forward = mapping.findForward("failure");
		}
		// Write logic determining how the user should be forwarded.
		

		// Finish with
		if (logger.isInfoEnabled()) {
			logger.info("<- execute");
		}
		return (forward);

	}
}