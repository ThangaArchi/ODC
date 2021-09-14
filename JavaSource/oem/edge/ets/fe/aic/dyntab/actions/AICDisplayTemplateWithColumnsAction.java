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
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import oem.edge.ets.fe.aic.common.exception.AICApplicationException;
import oem.edge.ets.fe.aic.common.exception.AICSystemException;
import oem.edge.ets.fe.aic.dyntab.bdlg.AICDynTabTemplateBusinessDelegate;
import oem.edge.ets.fe.aic.dyntab.vo.AICTemplateColumnLineItemVO;
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
public class AICDisplayTemplateWithColumnsAction extends Action {
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";
		
	private static Log logger =
		EtsLogger.getLogger(AICDisplayTemplateWithColumnsAction.class);
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
		AICDisplayTemplateWithColumnsForm objAICDisplayTemplateWithColumnsForm =
			(AICDisplayTemplateWithColumnsForm) form;
		
			
				
			if ("load".equals(mapping.getParameter())) {
				if (logger.isInfoEnabled()) {
					logger.info("<- execute");
				}
				return loadAdminForm(mapping,request,objAICDisplayTemplateWithColumnsForm);

			} else if ("submit".equals(mapping.getParameter())) {
				if (logger.isInfoEnabled()) {
					logger.info("<- execute");
				}
				return doSubmitAdminForm(mapping,request,objAICDisplayTemplateWithColumnsForm);

			} else {
				if (logger.isInfoEnabled()) {
					logger.info("<- execute");
				}
				throw new IllegalStateException();
			}
					
					
			
	}
	
	private ActionForward doSubmitAdminForm(ActionMapping mapping,HttpServletRequest request,AICDisplayTemplateWithColumnsForm objAICDisplayTemplateWithColumnsForm){
		if (logger.isInfoEnabled()) {
			logger.info("-> doSubmitAdminForm");
		}

			
			if (objAICDisplayTemplateWithColumnsForm.getDeleteButton() != null){
				if(!(objAICDisplayTemplateWithColumnsForm.getDeleteButton().equals("")))
				{
				return doDelete(mapping,request,objAICDisplayTemplateWithColumnsForm);
				}
			}else if (objAICDisplayTemplateWithColumnsForm.getAddButton() != null){
				if(!(objAICDisplayTemplateWithColumnsForm.getAddButton().equals("")))
				{
					return doAdd(mapping,request,objAICDisplayTemplateWithColumnsForm);
				}	
			}
			if (logger.isInfoEnabled()) {
				logger.info("<- doSubmitAdminForm");
			}
			return mapping.findForward("success");
	}
		
	private ActionForward loadAdminForm(
			ActionMapping mapping,HttpServletRequest request,			
	AICDisplayTemplateWithColumnsForm objAICDisplayTemplateWithColumnsForm) {
		if (logger.isInfoEnabled()) {
			logger.info("-> loadAdminForm");
		}
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); 
		
		HttpSession session = request.getSession(true);
	   AICTemplateVO objAICTemplateVO =	(AICTemplateVO) session.getAttribute("Template");
	   Collection coll = new ArrayList();
	   AICTemplateVO anotherAICTemplateVO = null;
			try{
				
		AICDynTabTemplateBusinessDelegate objAICDynTabTemplateBusinessDelegate =
						new AICDynTabTemplateBusinessDelegate();
					anotherAICTemplateVO =
						(
							AICTemplateVO) objAICDynTabTemplateBusinessDelegate
								.viewTemplateWithColums( 
							objAICTemplateVO);
				coll = anotherAICTemplateVO.getTemplateColVOCollection();
				
					if (logger.isDebugEnabled()) {
						logger.debug(
							"Retreived Sucessfully="
								+ anotherAICTemplateVO.getTemplateUpdatedate());
					}
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
				finally{
					objAICTemplateVO.setTemplateColVOCollection(coll);
					session.setAttribute("Template",objAICTemplateVO);
					objAICDisplayTemplateWithColumnsForm.setColumnsList((List) coll);
				}
				forward = mapping.findForward("success");
				
				if (!errors.isEmpty()) {
						forward = mapping.findForward("failure");
						saveErrors(request, errors);
				}
			if (logger.isInfoEnabled()) {
				logger.info("<- loadAdminForm");
			}
				return (forward);
		}
		
	private ActionForward doDelete (ActionMapping mapping,HttpServletRequest request,AICDisplayTemplateWithColumnsForm objAICDisplayTemplateWithColumnsForm) 
	{
		if (logger.isInfoEnabled()) {
			logger.info("-> doDelete");
		}

		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward();
		HttpSession session = request.getSession(true);
		AICTemplateVO objAICTemplateVO = (AICTemplateVO) session.getAttribute("Template");
		AICTemplateColumnLineItemVO[] items = objAICDisplayTemplateWithColumnsForm.getColumnsList();
		ArrayList al = new ArrayList();
		boolean flag = false;
		
			for (int i = 0; i < items.length; i++) {

				AICTemplateColumnLineItemVO lineItem = items[i];				
				if (lineItem.isChecked()) {
					flag = true;
					al.add(lineItem.getAICTemplateColumnVO());				
					
				}
			}
			if(!flag)
			{
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.detail","Please select a Column to Delete"));
			}
			
			//////////////////////////////////////////////
			AICTemplateVO anotherAICTemplateVO = new AICTemplateVO();
			anotherAICTemplateVO.setTemplateId(objAICTemplateVO.getTemplateId());
			anotherAICTemplateVO.setTemplateColVOCollection(al);
			//////////////////////////////////////////////
			
		
						
		AICDynTabTemplateBusinessDelegate objAICDynTabTemplateBusinessDelegate =
					new AICDynTabTemplateBusinessDelegate();
				try {

					anotherAICTemplateVO = (AICTemplateVO)objAICDynTabTemplateBusinessDelegate.deleteColumnFromTemplate(
					anotherAICTemplateVO);
					
					objAICTemplateVO.setTemplateColVOCollection(anotherAICTemplateVO.getTemplateColVOCollection());
					session.setAttribute("Template",objAICTemplateVO);
					
						if(logger.isDebugEnabled())
						{
							logger.debug("Delete Template with columns="
							+ objAICTemplateVO.getTemplateName());
							
						}
					if(flag)
					{
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.detail","Column Deleted Successfully"));
					}
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
				
				forward = mapping.findForward("delete");
				
				if (!errors.isEmpty()) {
					forward = mapping.findForward("delete");
					saveErrors(request, errors);
				}
				
			if (logger.isInfoEnabled()) {
				logger.info("<- doDelete");
			}
			return (forward);

		}
		
	private ActionForward doAdd (ActionMapping mapping,HttpServletRequest request,AICDisplayTemplateWithColumnsForm objAICDisplayTemplateWithColumnsForm) 
		{	
				if (logger.isInfoEnabled()) {
					logger.info("-> doAdd");
				}
				HttpSession session = (HttpSession)request.getSession(true);
				if (logger.isInfoEnabled()) {
					logger.info("<- doAdd");
				}
				return mapping.findForward("add");

		}
}
