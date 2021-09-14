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
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import oem.edge.ets.fe.aic.dyntab.helper.DynTabHelper;
import oem.edge.ets.fe.aic.dyntab.helper.ValidateHelper;
import oem.edge.ets.fe.aic.dyntab.vo.AICTableDataLineItemVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTableRowLineItemVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTableRowsDataVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTemplateColumnLineItemVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTemplateColumnVO;
import oem.edge.ets.fe.documents.BaseDocumentForm;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/** 
 * @version 	1.0
 * @author	vivek
 */
public class AICRowDataForm extends BaseDocumentForm {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";
	private String action = "";
	 
	private AICTemplateColumnLineItemVO[] aICTemplateColumnLineItemVO =	new AICTemplateColumnLineItemVO[0];		
	private AICTableDataLineItemVO[]    aICTableDataLineItemVO    =	new AICTableDataLineItemVO[0];
	
	
	
	public void setAICTableDataLineItemVO(int index, AICTableDataLineItemVO objAICTableDataLineItemVO)
	{
		this.aICTableDataLineItemVO[index] = objAICTableDataLineItemVO;
	}
	
	public AICTableDataLineItemVO getAICTableDataLineItemVO(int index )
	{
				return aICTableDataLineItemVO[index];
	}
	
	
	public void setDataList(List list)
	{
		List newList = new ArrayList(list.size());

		Iterator iter = list.iterator();
		while (iter.hasNext())
		{
			/*
			AICTableDataLineItemVO objAICTableDataLineItemVO = new AICTableDataLineItemVO();
			AICTableRowsDataVO objAICTableRowsVO = (AICTableRowsDataVO) iter.next();
			objAICTableDataLineItemVO.setAICTableRowsDataVO(objAICTableRowsVO);
			*/
			AICTableDataLineItemVO objAICTableDataLineItemVO = (AICTableDataLineItemVO) iter.next();
			newList.add(objAICTableDataLineItemVO);
		}

		aICTableDataLineItemVO = (AICTableDataLineItemVO[]) newList.toArray(new AICTableDataLineItemVO[newList.size()]);
	}
	
	public AICTableDataLineItemVO[] getDataList()
	{
		return aICTableDataLineItemVO;
	}	

	/**
	 * @return
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param string
	 */
	public void setAction(String string) {
		action = string;
	}
	
	///***********************************************************************************************************/
	
	/**
	 * @return
	 */
	public AICTemplateColumnLineItemVO getAICTemplateColumnLineItemVO(int index) {

		return aICTemplateColumnLineItemVO[index];
	}

	/**
	 * @param list
	 */
	//AICTemplateColumnVO

	public void setAICTemplateColumnVO(	int index, AICTemplateColumnLineItemVO objAICTemplateColumnLineItemVO)
	{
			this.aICTemplateColumnLineItemVO[index] = objAICTemplateColumnLineItemVO;
	}

	public AICTemplateColumnLineItemVO getAICTemplateColumnVO(int index)
	{
				return aICTemplateColumnLineItemVO[index];
	}
	
	public void setAICTemplateColumnLineItemVO(int index, AICTemplateColumnLineItemVO objAICTemplateColumnLineItemVO)
	{
		this.aICTemplateColumnLineItemVO[index] = objAICTemplateColumnLineItemVO;
	}

	public void setColumnsList(List list)
	{
		List newList = new ArrayList(list.size());

		Iterator iter = list.iterator();
		while (iter.hasNext())
		{
			AICTemplateColumnLineItemVO objAICTemplateColumnLineItemVO = new AICTemplateColumnLineItemVO();
			AICTemplateColumnVO objAICTemplateColumnVO = (AICTemplateColumnVO) iter.next();
			objAICTemplateColumnLineItemVO.setAICTemplateColumnVO(objAICTemplateColumnVO);
			newList.add(objAICTemplateColumnLineItemVO);
		}

		aICTemplateColumnLineItemVO = (AICTemplateColumnLineItemVO[]) newList.toArray(new AICTemplateColumnLineItemVO[newList.size()]);
	}


	public AICTemplateColumnLineItemVO[] getColumnsList()
	{
		return aICTemplateColumnLineItemVO;
	}
	
	/**
	 * @return
	 */
	public AICTemplateColumnLineItemVO[] getAICTemplateColumnVO()
	{
		return aICTemplateColumnLineItemVO;
	}

	/**
	 * @param items
	 */
	public void setAICTemplateColumnVO(AICTemplateColumnLineItemVO[] items)
	{
		this.aICTemplateColumnLineItemVO = items;
	}
	
//	/***********************************************************************************************************/

	public void reset(ActionMapping mapping, HttpServletRequest request)
	{
		action = null;
		//aICTableDataLineItemVO    =	new AICTableDataLineItemVO[0];
		//aICTemplateColumnLineItemVO =	new AICTemplateColumnLineItemVO[0];
	}	
	
	public ActionErrors validate(
			ActionMapping mapping,
			HttpServletRequest request) 
	{
		String strDataValue = "";
		String strColumnType = "";
		String strColumnName = "";
		String strRequired = "";
		
		
		ActionErrors errors = new ActionErrors();
		
		request = DynTabHelper.setAttributesForDocumentTAB(request,this);
		
		if(this.action != null)
		{
			System.out.println("Inside IF ="+this.action);
			if(action.equals("Edit"))
			{
//			   Use aICTableDataLineItemVO= AICTableDataLineItemVO[2]  (id=806068)
				for(int i=0;i<aICTableDataLineItemVO.length;i++)
				{
					AICTableDataLineItemVO objAICTableDataLineItemVO = (AICTableDataLineItemVO)aICTableDataLineItemVO[i];
					AICTableRowsDataVO objAICTableRowsDataVO = objAICTableDataLineItemVO.getAICTableRowsDataVO();
					strDataValue = objAICTableRowsDataVO.getDataValue();
					strColumnType = objAICTableRowsDataVO.getColumnType();
					strColumnName = objAICTableRowsDataVO.getColumnName();
					strRequired = objAICTableRowsDataVO.getRequired();
					validateData(strColumnType,strColumnName,strDataValue,strRequired,errors);
					
				}
			   


			}else if(action.equals("Add"))
			{

				for(int i=0;i<aICTemplateColumnLineItemVO.length;i++)
				{				
					AICTemplateColumnLineItemVO objAICTemplateColumnLineItemVO = (AICTemplateColumnLineItemVO)aICTemplateColumnLineItemVO[i];
					AICTemplateColumnVO objAICTemplateColumnVO = objAICTemplateColumnLineItemVO.getAICTemplateColumnVO();
					strDataValue = objAICTemplateColumnVO.getDataValue();
					strColumnType = objAICTemplateColumnVO.getColumnType();
					strColumnName = objAICTemplateColumnVO.getColumnName();
					strRequired = objAICTemplateColumnVO.getRequired();
					validateData(strColumnType,strColumnName,strDataValue,strRequired,errors);
					
				}

			}
		
		}
		else
		{
			System.out.println("Inside ELSE");
		}
		// Validate the fields in your form, adding
		// adding each error to this.errors as found, e.g.

		// if ((field == null) || (field.length() == 0)) {
		//   errors.add("field", new org.apache.struts.action.ActionError("error.field.required"));
		// }
		return errors;

	}
	
	
	private void validateData(String strColumnType,String strColumnName, String strDataValue,String strRequired,ActionErrors errors)
	{
		String strError = "";
		
		strError = ValidateHelper.validateForRequired(strRequired,strColumnName,strDataValue);
		if(!strError.equals(""))
		{
			ActionError error = new ActionError(strError,strColumnName);			
			//errors.add(strColumnName,error);
			errors.add(ActionErrors.GLOBAL_ERROR,error);
		}
		strError = ValidateHelper.validateForMaxLength(strDataValue);
		if(!strError.equals(""))
		{
			ActionError error = new ActionError(strError,strColumnName);			
			errors.add(ActionErrors.GLOBAL_ERROR,error);
		}
		strError = ValidateHelper.validateForDataType(strColumnType,strColumnName,strDataValue);
		if(!strError.equals(""))
		{
			ActionError error = new ActionError(strError,strColumnName);			
			errors.add(ActionErrors.GLOBAL_ERROR,error);
		}
		
		
	}

}
