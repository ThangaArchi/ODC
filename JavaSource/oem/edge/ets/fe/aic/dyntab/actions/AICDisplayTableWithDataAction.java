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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.aic.common.exception.AICApplicationException;
import oem.edge.ets.fe.aic.common.exception.AICSystemException;
import oem.edge.ets.fe.aic.dyntab.bdlg.AICDynTabTableBusinessDelegate;
import oem.edge.ets.fe.aic.dyntab.bdlg.AICDynTabTemplateBusinessDelegate;
import oem.edge.ets.fe.aic.dyntab.helper.DynTabHelper;
import oem.edge.ets.fe.aic.dyntab.vo.AICTableDataLineItemVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTableRowLineItemVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTableVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTemplateColumnLineItemVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTemplateVO;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.BaseDocumentAction;
import oem.edge.ets.fe.documents.DocumentsHelper;

import org.apache.commons.logging.Log;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author thanga
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AICDisplayTableWithDataAction extends BaseDocumentAction {
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.5";

	private static Log logger =
		EtsLogger.getLogger(AICDisplayTableWithDataAction.class);
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
		AICDisplayTableWithDataForm objAICDisplayTableWithDataForm =
			(AICDisplayTableWithDataForm) form;



			if ("load".equals(mapping.getParameter())) {
				if (logger.isInfoEnabled()) {
					logger.info("<- executeAction");
				}
				return loadAdminForm(mapping,request,objAICDisplayTableWithDataForm);

			} else if ("submit".equals(mapping.getParameter())) {
				if (logger.isInfoEnabled()) {
					logger.info("<- executeAction");
				}
				return doSubmitAdminForm(mapping,request,response,objAICDisplayTableWithDataForm);

			} else {
				if (logger.isInfoEnabled()) {
					logger.info("<- executeAction");
				}
				throw new IllegalStateException();
			}



	}

	private ActionForward doSubmitAdminForm(ActionMapping mapping,HttpServletRequest request,HttpServletResponse response,AICDisplayTableWithDataForm objAICDisplayTableWithDataForm){
		if (logger.isInfoEnabled()) {
			logger.info("-> doSubmitAdminForm");
		}
			String strDownload = (String)request.getParameter("download");

			if (objAICDisplayTableWithDataForm.getDeleteButton() != null){
				if(!(objAICDisplayTableWithDataForm.getDeleteButton().equals("")))
				{
				return doDelete(mapping,request,objAICDisplayTableWithDataForm);
				}
			}else if (objAICDisplayTableWithDataForm.getAddButton() != null){
				if(!(objAICDisplayTableWithDataForm.getAddButton().equals("")))
				{
					return doAdd(mapping,request,objAICDisplayTableWithDataForm);
				}
			}else{
					return doDownload(mapping,request,response,objAICDisplayTableWithDataForm);
			}
			if (logger.isInfoEnabled()) {
				logger.info("<- doSubmitAdminForm");
			}
			return mapping.findForward("success");
	}

	private ActionForward loadAdminForm(ActionMapping mapping,HttpServletRequest request, AICDisplayTableWithDataForm objAICDisplayTableWithDataForm)
	{
		if (logger.isInfoEnabled())
		{
			logger.info("-> loadAdminForm");
		}
		request = DynTabHelper.setAttributesForDocumentTAB(request,objAICDisplayTableWithDataForm);

		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward();

		HttpSession session = request.getSession(true);

	   Collection coll = new ArrayList();
	   Collection collRow = new ArrayList();
	   AICTemplateVO anotherAICTemplateVO = null;
	   AICTableVO anotherAICTableVO = new AICTableVO();
	   AICTableVO objAICTableVO = new AICTableVO();
	   AICTemplateVO objAICTemplateVO = new AICTemplateVO();

	   String strTableID = (String)request.getParameter("tableid");
	   String strTemplateID = (String)request.getParameter("templateid");
	   if(strTableID == null)
	   {
		strTableID =(String)request.getAttribute("tableid");
	   }
	   if(strTemplateID == null)
	  {
		strTemplateID = (String)request.getAttribute("templateid");
	  }
	   objAICTableVO.setTableId(strTableID);
	   objAICTableVO.setTemplateId(strTemplateID);
	   objAICTemplateVO.setTemplateId(strTemplateID);
			try{

		AICDynTabTemplateBusinessDelegate objAICDynTabTemplateBusinessDelegate = new AICDynTabTemplateBusinessDelegate();
		//objAICTemplateVO = (AICTemplateVO)objAICDynTabTemplateBusinessDelegate.viewTemplate(objAICTemplateVO);
		objAICTemplateVO = (AICTemplateVO)objAICDynTabTemplateBusinessDelegate.viewTemplateStatus(objAICTemplateVO);
		anotherAICTemplateVO =(AICTemplateVO) objAICDynTabTemplateBusinessDelegate.viewTemplateWithColums(objAICTemplateVO);
		coll = anotherAICTemplateVO.getTemplateColVOCollection();


		AICDynTabTableBusinessDelegate objAICDynTabTableBusinessDelegate = new AICDynTabTableBusinessDelegate();
		objAICTableVO = (AICTableVO)objAICDynTabTableBusinessDelegate.viewTable(objAICTableVO);
		anotherAICTableVO = (AICTableVO)objAICDynTabTableBusinessDelegate.viewTableWithRows(objAICTableVO);
		collRow = anotherAICTableVO.getAICTableRowsDataCollection();


					objAICDisplayTableWithDataForm.setDeleteMsgFlag("Y");
				} catch (AICApplicationException ape) {
					if(logger.isDebugEnabled())
					{
						if( ape.getMessage().equals("There are no rows added for Table : "+objAICTableVO.getTableName()) )
						{
							objAICDisplayTableWithDataForm.setDeleteMsgFlag("N");
						}
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
					objAICTableVO.setAICTableRowsDataCollection(collRow);
					session.setAttribute("Template",objAICTemplateVO);
					session.setAttribute("Table",objAICTableVO);
					objAICDisplayTableWithDataForm.setAICTemplateVO(objAICTemplateVO);
					objAICDisplayTableWithDataForm.setColumnsList((List) coll);
					objAICDisplayTableWithDataForm.setRowsList((List)collRow);

					String strUserRole = DocumentsHelper.getUserRole(request);
					if(strUserRole.equals(Defines.WORKSPACE_VISITOR))
					{

						objAICDisplayTableWithDataForm.setDisableAll("Y");
					}

					if(objAICTemplateVO != null)
					{
						if(objAICTemplateVO.getActive().trim().equals("N"))
						{
							objAICDisplayTableWithDataForm.setDisableAll("Y");
						}
					}
				}
				forward = mapping.findForward("success");

				if (!errors.isEmpty()) {
						forward = mapping.findForward("success");
						saveErrors(request, errors);
				}
			if (logger.isInfoEnabled()) {
				logger.info("<- loadAdminForm");
			}
				return (forward);
		}

	private ActionForward doDelete (ActionMapping mapping,HttpServletRequest request,AICDisplayTableWithDataForm objAICDisplayTableWithDataForm)
	{
		if (logger.isInfoEnabled()) {
			logger.info("-> doDelete");
		}
		request = DynTabHelper.setAttributesForDocumentTAB(request,objAICDisplayTableWithDataForm);

		ActionErrors errors = new ActionErrors();
		ActionForward forward = new ActionForward();
		HttpSession session = request.getSession(true);
		AICTemplateVO objAICTemplateVO = (AICTemplateVO) session.getAttribute("Template");
		AICTableVO objAICTableVO = (AICTableVO) session.getAttribute("Table");

		//AICTemplateColumnLineItemVO[] items = objAICDisplayTableWithDataForm.getColumnsList();

		AICTableRowLineItemVO[] itemsRows = objAICDisplayTableWithDataForm.getRowsList();
		boolean flag = false;
		ArrayList al = new ArrayList();
			for (int i = 0; i < itemsRows.length; i++) {

				AICTableRowLineItemVO lineItem = itemsRows[i];
				if (lineItem.isChecked()) {
					flag = true;
					AICTableDataLineItemVO[]  aICTableDataLineItemVO = lineItem.getDataList();
					for(int j=0;j<aICTableDataLineItemVO.length;j++)
					{
						AICTableDataLineItemVO objAICTableDataLineItemVO = (AICTableDataLineItemVO)aICTableDataLineItemVO[j];
						al.add(objAICTableDataLineItemVO.getAICTableRowsDataVO());

					}
				}
			}

			if(!flag)
			{
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.detail","Please select a Row to Delete"));
			}

			//////////////////////////////////////////////
			AICTableVO anotherAICTableVO = new AICTableVO();
			anotherAICTableVO.setTableId(objAICTableVO.getTableId());
			anotherAICTableVO.setTableName(objAICTableVO.getTableName());
			anotherAICTableVO.setActive(objAICTableVO.getActive());
			anotherAICTableVO.setDocId(objAICTableVO.getDocId());
			anotherAICTableVO.setTableUpdateDate(objAICTableVO.getTableUpdateDate());
			anotherAICTableVO.setTemplateId(objAICTableVO.getTemplateId());
			anotherAICTableVO.setAICTableRowsDataCollection(al);
			//////////////////////////////////////////////



		AICDynTabTableBusinessDelegate objAICDynTabTableBusinessDelegate =
					new AICDynTabTableBusinessDelegate();
				try {

					anotherAICTableVO = (AICTableVO)objAICDynTabTableBusinessDelegate.deleteRowsFromTable(anotherAICTableVO);

					objAICTableVO.setAICTableRowsDataCollection(anotherAICTableVO.getAICTableRowsDataCollection());
					if(flag)
					{
						errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.detail","Row/s Deleted Successfully"));
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
					session.setAttribute("Table",objAICTableVO);
					request.setAttribute("tableid",objAICTableVO.getTableId());
					request.setAttribute("templateid",objAICTemplateVO.getTemplateId());
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

	private ActionForward doAdd (ActionMapping mapping,HttpServletRequest request,AICDisplayTableWithDataForm objAICDisplayTableWithDataForm)
		{
				if (logger.isInfoEnabled()) {
					logger.info("-> doAdd");
				}
			request = DynTabHelper.setAttributesForDocumentTAB(request,objAICDisplayTableWithDataForm);
				if (logger.isInfoEnabled()) {
					logger.info("<- doAdd");
				}
				return mapping.findForward("add");

		}

	private ActionForward doDownload(ActionMapping mapping,HttpServletRequest request,HttpServletResponse response,AICDisplayTableWithDataForm objAICDisplayTableWithDataForm)
	{
		if (logger.isInfoEnabled()) {
				logger.info("-> doDownload");
			}
			HttpSession session = request.getSession(true);
			AICTemplateVO objAICTemplateVO = (AICTemplateVO) session.getAttribute("Template");
			AICTableVO objAICTableVO = (AICTableVO) session.getAttribute("Table");

			request = DynTabHelper.setAttributesForDocumentTAB(request,objAICDisplayTableWithDataForm);
			//StringBuffer sb_csv = new StringBuffer("1,2,3,4");
			StringBuffer sb_csv = DynTabHelper.getCSVResults(objAICTemplateVO,objAICTableVO);


			response.setHeader("Cache-Control", null);
			response.setHeader("Content-disposition", "attachment; filename=" + objAICTableVO.getTableName() + ".csv");
			response.setHeader("Content-Type", "application/octet-stream");
			response.setContentLength(sb_csv.length());
			PrintWriter out = null;
			try{

			out = response.getWriter();
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
			finally{
				request.setAttribute("tableid",objAICTableVO.getTableId());
				request.setAttribute("templateid",objAICTemplateVO.getTemplateId());
			}
			out.println(sb_csv.toString());
			out.close();
			out.flush();

			if (logger.isInfoEnabled()) {
				logger.info("<- doDownload");
			}
			return mapping.findForward("download");
	}
}
