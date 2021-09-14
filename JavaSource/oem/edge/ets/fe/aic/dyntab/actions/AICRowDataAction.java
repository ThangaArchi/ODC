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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import oem.edge.ets.fe.aic.common.exception.AICApplicationException;
import oem.edge.ets.fe.aic.common.exception.AICSystemException;
import oem.edge.ets.fe.aic.common.util.AICUtil;
import oem.edge.ets.fe.aic.dyntab.bdlg.AICDynTabTableBusinessDelegate;
import oem.edge.ets.fe.aic.dyntab.helper.DynTabHelper;
import oem.edge.ets.fe.aic.dyntab.vo.AICTableDataLineItemVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTableRowsDataVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTableVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTemplateColumnLineItemVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTemplateColumnVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTemplateVO;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.BaseDocumentAction;

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
public class AICRowDataAction extends BaseDocumentAction {
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.5";
	private static Log logger =
					EtsLogger.getLogger(AICRowDataAction.class);
	public ActionForward executeAction(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {
		if (logger.isInfoEnabled()) {
					logger.info("-> executeAction");
				}
		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward(); // return value
		HttpSession session = request.getSession(true);
		
		AICTemplateVO objAICTemplateVO =
			(AICTemplateVO) session.getAttribute("Template");
		AICTableVO objAICTableVO = 	(AICTableVO) session.getAttribute("Table");
			String strRowId = (String)session.getAttribute("rowid");

		AICRowDataForm aicRowDataForm = (AICRowDataForm) form;
		
		request = DynTabHelper.setAttributesForDocumentTAB(request,aicRowDataForm);
		
		//AICTemplateColumnVO objAICTemplateColumnVO = new AICTemplateColumnVO();
		//objAICTemplateColumnVO.setActive("Y");
		ArrayList aL = new ArrayList();
		if(strRowId==null || strRowId.equals(""))
		{
			String strRowIdGen = AICUtil.getUniqueId();
			
			AICTemplateColumnLineItemVO[]  aicTemplateColumnLineItemVO = aicRowDataForm.getColumnsList();
			
			
				for(int i=0;i<aicTemplateColumnLineItemVO.length;i++)
				{
					AICTemplateColumnLineItemVO objAICTemplateColumnLineItemVO = aicTemplateColumnLineItemVO[i];
					AICTemplateColumnVO objAICTemplateColumnVO = objAICTemplateColumnLineItemVO.getAICTemplateColumnVO();					
					AICTableRowsDataVO objAICTableRowsDataVO = new AICTableRowsDataVO();
					objAICTableRowsDataVO.setActive("Y");
					objAICTableRowsDataVO.setRowId(strRowIdGen);
					objAICTableRowsDataVO.setTableId(objAICTableVO.getTableId());
					objAICTableRowsDataVO.setDataId(AICUtil.getUniqueId()+i+"");
					objAICTableRowsDataVO.setDataValue(objAICTemplateColumnVO.getDataValue());
					objAICTableRowsDataVO.setColumnId(objAICTemplateColumnVO.getColumnId());
					objAICTableRowsDataVO.setColumnName(objAICTemplateColumnVO.getColumnName());
					objAICTableRowsDataVO.setColumnOrder(objAICTemplateColumnVO.getColumnOrder());
					aL.add(objAICTableRowsDataVO);					
				}
				
		}
		else
		{
			
			AICTableDataLineItemVO[] aicTableDataLineItemVO = aicRowDataForm.getDataList();
			
			for(int i=0;i<aicTableDataLineItemVO.length;i++)
			{
				AICTableDataLineItemVO objAICTableDataLineItemVO = aicTableDataLineItemVO[i];
				AICTableRowsDataVO objAICTableRowsDataVO = objAICTableDataLineItemVO.getAICTableRowsDataVO();
				objAICTableRowsDataVO.setActive("Y");
				objAICTableRowsDataVO.setRowId(strRowId);
				objAICTableRowsDataVO.setTableId(objAICTableVO.getTableId());
				//objAICTableRowsDataVO.setDataId(AICUtil.getUniqueId()+i+"");
				if(objAICTableRowsDataVO.getDataId().trim().equals(""))
				{
					objAICTableRowsDataVO.setDataId(AICUtil.getUniqueId()+i+"");
				}
				aL.add(objAICTableRowsDataVO);					
			}
			
			
		}
		
		

		AICTableVO anotherAICTableVO = new AICTableVO();
		
		
		anotherAICTableVO.setTableId(objAICTableVO.getTableId());
		anotherAICTableVO.setTableName(objAICTableVO.getTableName());
		anotherAICTableVO.setActive(objAICTableVO.getActive());
		anotherAICTableVO.setDocId(objAICTableVO.getDocId());
		anotherAICTableVO.setTableUpdateDate(objAICTableVO.getTableUpdateDate());
		anotherAICTableVO.setTemplateId(objAICTableVO.getTemplateId());
		anotherAICTableVO.setAICTableRowsDataCollection(aL);
		
		

		AICDynTabTableBusinessDelegate objAICDynTabTableBusinessDelegate = new AICDynTabTableBusinessDelegate();
		try {
			if(strRowId==null || strRowId.equals(""))
			{
				objAICTableVO =(AICTableVO) objAICDynTabTableBusinessDelegate.addRowsToTable(anotherAICTableVO);
			}
			else
			{
				objAICTableVO =(AICTableVO) objAICDynTabTableBusinessDelegate.editRowFromTable(anotherAICTableVO);
			}
			
				

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
		finally{
			session.setAttribute("Table", objAICTableVO);
			request.setAttribute("tableid",objAICTableVO.getTableId());
			request.setAttribute("templateid",objAICTemplateVO.getTemplateId());
		}
		
		forward = mapping.findForward("success");
		
		if (!errors.isEmpty()) {
			saveErrors(request, errors);
			forward = mapping.findForward("failure");
		}
		// Write logic determining how the user should be forwarded.
		

		// Finish with
		if (logger.isInfoEnabled()) {
			logger.info("<- executeAction");
		}
		return (forward);

	}
}
