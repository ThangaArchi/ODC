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

/**
 * @author vivek
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AICTemplateDeleteAction extends Action{
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";
		
	private static Log logger =
					EtsLogger.getLogger(AICTemplateDeleteAction.class);
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
					AICTemplateDataForm objAICTemplateDataForm =
						(AICTemplateDataForm) form;

					AICDynTabTemplateBusinessDelegate objAICDynTabTemplateBusinessDelegate =
								new AICDynTabTemplateBusinessDelegate();
					try {
						AICTemplateVO objAICTemplateVO = new AICTemplateVO();
						objAICTemplateVO.setTemplateId(objAICTemplateDataForm.getTemplate_id());
						objAICDynTabTemplateBusinessDelegate.deleteTemplate(objAICTemplateVO);	

					} catch (AICApplicationException ape) {
						if(logger.isDebugEnabled())
						{
							logger.debug("App Message = " + ape.getMessage());
						}
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.detail",ape.getMessage()));

					} catch (AICSystemException ase) {
						if(logger.isDebugEnabled())
						{
							logger.debug("Sys Message = " + ase.getMessage());
						}
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.detail",ase.getMessage()));

					} catch (Exception ex) {
						if(logger.isDebugEnabled())
						{
							logger.debug("Exp Message = " + ex.getMessage());
						}
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.detail",ex.getMessage()));
					}

					forward = mapping.findForward("success");
					if (!errors.isEmpty()) {
						forward = mapping.findForward("failure");
						saveErrors(request, errors);

					}
					if (logger.isInfoEnabled()) {
						logger.info("<- execute");
					}
					return (forward);

				}

}
