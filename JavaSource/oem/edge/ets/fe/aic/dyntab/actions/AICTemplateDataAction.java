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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import oem.edge.ets.fe.aic.common.exception.AICApplicationException;
import oem.edge.ets.fe.aic.common.exception.AICSystemException;
import oem.edge.ets.fe.aic.common.util.AICUtil;
import oem.edge.ets.fe.aic.dyntab.bdlg.AICDynTabTemplateBusinessDelegate;
import oem.edge.ets.fe.aic.dyntab.vo.AICTemplateVO;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * @version 	1.0
 * @author
 */
public class AICTemplateDataAction extends Action {
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";
		
	private static Log logger = EtsLogger.getLogger(AICTemplateDataAction.class);
	String template_name = null;
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
		ActionMessages messages = new ActionMessages();
		ActionForward forward = new ActionForward(); // return value
		AICTemplateDataForm aICTableTemplateDataForm =
			(AICTemplateDataForm) form;
		try {
			template_name = aICTableTemplateDataForm.getTemplate_name();
			AICTemplateVO objAICTemplateVO = new AICTemplateVO();
			objAICTemplateVO.setTemplateName(template_name);
			objAICTemplateVO.setTemplateId(AICUtil.getUniqueId());
			objAICTemplateVO.setActive("Y");

			//request.setAttribute("template_name", template_name);
			HttpSession session = request.getSession(true); 
			session.setAttribute("Template", objAICTemplateVO);

			AICDynTabTemplateBusinessDelegate objAICDynTabTemplateBusinessDelegate =
				new AICDynTabTemplateBusinessDelegate();
			AICTemplateVO anotherAICTemplateVO =
				(
					AICTemplateVO) objAICDynTabTemplateBusinessDelegate
						.createTemplate(
					objAICTemplateVO);
					if(logger.isDebugEnabled())
					{
						logger.debug("Inserted Sucessfully="+ anotherAICTemplateVO.getTemplateUpdatedate());
					}
			

		} catch (AICApplicationException ape) {
			
			if(logger.isDebugEnabled())
			{
				logger.debug("App Message = " + ape.getMessage());
			}
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.detail",ape.getMessage()));
			//messages.add("template_name", new ActionMessage(ape.getMessage()));
			
		} catch (AICSystemException ase) {
			
			if(logger.isDebugEnabled())
			{
				logger.debug("Sys Message = " + ase.getMessage());
			}
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.detail",ase.getMessage()));
			//messages.add("template_name", new ActionMessage(ase.getMessage()));

		} catch (Exception ex) {
			if(logger.isDebugEnabled())
			{
				logger.debug("Exp Message = " + ex.getMessage());
			}
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.detail",ex.getMessage()));
			//messages.add("template_name", new ActionMessage(ex.getMessage()));

		} 

		
		// Write logic determining how the user should be forwarded.
		forward = mapping.findForward("success");
		
		//	If a message is required, save the specified key(s)
		// into the request for use by the <struts:errors> tag.

			 if (!errors.isEmpty()) {
			//if (!messages.isEmpty()) {
				 forward = mapping.findForward("failure");
				 saveErrors(request, errors);
				 //saveMessages(request, errors);
			 }

		// Finish with
		if (logger.isInfoEnabled()) {
			logger.info("<- execute");
		}
		return (forward);

	}
}
