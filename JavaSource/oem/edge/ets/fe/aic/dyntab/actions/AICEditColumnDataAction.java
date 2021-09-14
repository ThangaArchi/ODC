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
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import oem.edge.ets.fe.aic.common.exception.AICApplicationException;
import oem.edge.ets.fe.aic.common.exception.AICSystemException;
import oem.edge.ets.fe.aic.dyntab.bdlg.AICDynTabTemplateBusinessDelegate;
import oem.edge.ets.fe.aic.dyntab.vo.AICTemplateColumnVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTemplateVO;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;
import org.apache.struts.action.Action;
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
public class AICEditColumnDataAction extends Action{
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";
	
	private static Log logger =
			EtsLogger.getLogger(AICEditColumnDataAction.class);
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
				ActionForward forward = new ActionForward();
				
								
				AICColumnDataForm aicColumnDataForm = (AICColumnDataForm) form;
				
				
				
				
				
				HttpSession session = request.getSession(true);
				AICTemplateVO objAICTemplateVO = (AICTemplateVO)session.getAttribute("Template");
				// Code for Edit Use Case				
				String columnId = request.getParameter("columnId"); 
				session.setAttribute("columnId",columnId);
				
				
				ArrayList aL = (ArrayList)objAICTemplateVO.getTemplateColVOCollection();
				AICTemplateColumnVO newAICTemplateColumnVO = null;
				if(aL!=null)
				{
					Iterator it = aL.iterator();
					AICTemplateColumnVO objAICTemplateColumnVO = null;
					
					
					while(it.hasNext())
					{
						objAICTemplateColumnVO=(AICTemplateColumnVO)it.next();
						if(objAICTemplateColumnVO.getColumnId().equals(columnId))
						{
							newAICTemplateColumnVO = objAICTemplateColumnVO;
						}
					}
				}
				
				if(newAICTemplateColumnVO!=null)
				{
					
							
							aicColumnDataForm.setColumn_id(newAICTemplateColumnVO.getColumnId());
							aicColumnDataForm.setColumn_name(newAICTemplateColumnVO.getColumnName());
							aicColumnDataForm.setColumn_order(newAICTemplateColumnVO.getColumnOrder()+"");
							aicColumnDataForm.setColumn_type(newAICTemplateColumnVO.getColumnType());
							aicColumnDataForm.setIs_active(newAICTemplateColumnVO.getActive());
							aicColumnDataForm.setIs_required(newAICTemplateColumnVO.getRequired());
							aicColumnDataForm.setAction("Edit");
							request.setAttribute("AICColumnDataForm",aicColumnDataForm);
				}
				
				
				
				
				forward = mapping.findForward("success");
				if (logger.isInfoEnabled()) {
					logger.info("<- execute");
				}
				return (forward);			
				
			}
	
}
