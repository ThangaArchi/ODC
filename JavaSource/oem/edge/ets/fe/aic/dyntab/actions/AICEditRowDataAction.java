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

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import oem.edge.ets.fe.aic.common.util.AICUtil;
import oem.edge.ets.fe.aic.dyntab.dao.AICTableRowsDataDAO;
import oem.edge.ets.fe.aic.dyntab.helper.DynTabHelper;
import oem.edge.ets.fe.aic.dyntab.vo.AICTableDataLineItemVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTableRowsDataVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTableVO;
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
public class AICEditRowDataAction extends BaseDocumentAction {
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.4";
	private static Log logger =
					EtsLogger.getLogger(AICEditRowDataAction.class);

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
		
		AICRowDataForm aicAICRowDataForm = (AICRowDataForm) form;
		
		request = DynTabHelper.setAttributesForDocumentTAB(request,aicAICRowDataForm);
		
		AICTableVO objAICTableVO = (AICTableVO)session.getAttribute("Table");
		AICTemplateVO objAICTemplateVO = (AICTemplateVO)session.getAttribute("Template");
		request.setAttribute("tableid",objAICTableVO.getTableId());
		request.setAttribute("templateid",objAICTemplateVO.getTemplateId());
		// Code for Edit Use Case				
		String rowId = request.getParameter("rowid"); 
		session.setAttribute("rowid",rowId);
		
		String strColumnId = "";
		String strColumnName = "";
		int intColumnOrder = 0;
		String strColumnType = "";
		String strRowId = "";
		String strTableId = "";
		
		ArrayList aL = (ArrayList)objAICTableVO.getAICTableRowsDataCollection();
		
		AICTableRowsDataVO newAICTableRowsDataVO = null;
		List li = new ArrayList();
		if(aL!=null)
		{
			Iterator it = aL.iterator();
			AICTableRowsDataVO objAICTableRowsDataVO = null;
			AICTableDataLineItemVO objAICTableDataLineItemVO = null;
			
			while(it.hasNext())
			{
				objAICTableRowsDataVO=(AICTableRowsDataVO)it.next();
				
				if(objAICTableRowsDataVO.getRowId().equals(rowId))
				{
					/*
					strColumnId = objAICTableRowsDataVO.getColumnId();
					strColumnName = objAICTableRowsDataVO.getColumnName();
					intColumnOrder = objAICTableRowsDataVO.getColumnOrder();					
					strColumnType = objAICTableRowsDataVO.getColumnType();
					*/
					strRowId = objAICTableRowsDataVO.getRowId();
					strTableId = objAICTableRowsDataVO.getTableId();
					
					// This flag is set to know in the BO that we need to update
					objAICTableRowsDataVO.setUpdateFlag(true);
					
					objAICTableDataLineItemVO = new AICTableDataLineItemVO();
					objAICTableDataLineItemVO.setAICTableRowsDataVO(objAICTableRowsDataVO);
					li.add(objAICTableDataLineItemVO);
					
				}
			}
		}


		
		if(li!=null && li.size()>0)
		{
			
			aicAICRowDataForm.setColumnsList((List)objAICTemplateVO.getTemplateColVOCollection());
			int maxColCount = objAICTemplateVO.getTemplateColVOCollection().size();
			int liSizeCount = li.size();
			int patchCount = maxColCount - liSizeCount;
			
			List al =  (List)objAICTemplateVO.getTemplateColVOCollection();
			
			
			
			for(int i=0;i<patchCount;i++)
			{
				AICTemplateColumnVO objAICTemplateColumnVO = (AICTemplateColumnVO)al.get(liSizeCount+i);
				
				AICTableDataLineItemVO objAICTableDataLineItemVO = new AICTableDataLineItemVO();
				AICTableRowsDataVO objAICTableRowsDataVO2 = new AICTableRowsDataVO();
				
				objAICTableRowsDataVO2.setActive("Y");
				objAICTableRowsDataVO2.setColumnId(objAICTemplateColumnVO.getColumnId());
				objAICTableRowsDataVO2.setColumnName(objAICTemplateColumnVO.getColumnName());
				objAICTableRowsDataVO2.setColumnOrder(objAICTemplateColumnVO.getColumnOrder());
				objAICTableRowsDataVO2.setColumnType(objAICTemplateColumnVO.getColumnType());
				objAICTableRowsDataVO2.setDataId(AICUtil.getUniqueId()+i);
				objAICTableRowsDataVO2.setDataValue("");
				objAICTableRowsDataVO2.setRequired(objAICTemplateColumnVO.getRequired());

				objAICTableRowsDataVO2.setRowId(strRowId);
				objAICTableRowsDataVO2.setTableId(strTableId);
				
				// This flag is set to know in the BO that we need to insert
				objAICTableRowsDataVO2.setInsertFlag(true);
				
				objAICTableDataLineItemVO.setAICTableRowsDataVO(objAICTableRowsDataVO2);
				
				li.add(objAICTableDataLineItemVO);
			}
			
			
			aicAICRowDataForm.setDataList(li);		
			//aicAICRowDataForm.setAction("Edit");			
			request.setAttribute("AICRowDataForm",aicAICRowDataForm);
			forward = mapping.findForward("edit");
		}
		else
		{
			aicAICRowDataForm.setColumnsList((List)objAICTemplateVO.getTemplateColVOCollection());
			//aicAICRowDataForm.setAction("Add");
			request.setAttribute("AICRowDataForm",aicAICRowDataForm);
			forward = mapping.findForward("add");
		}
		
		
		
		
		
		if (logger.isInfoEnabled()) {
			logger.info("<- executeAction");
		}
		return (forward);	

	}
}
