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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


import oem.edge.ets.fe.aic.common.util.AICUtil;
import oem.edge.ets.fe.aic.dyntab.vo.AICTableDataLineItemVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTableRowLineItemVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTableRowsDataVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTemplateColumnLineItemVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTemplateColumnVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTemplateVO;
import oem.edge.ets.fe.documents.BaseDocumentForm;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * @author thanga
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AICDisplayTableWithDataForm extends BaseDocumentForm {
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.4";
		
	private AICTemplateColumnLineItemVO[] aICTemplateColumnLineItemVO =	new AICTemplateColumnLineItemVO[0];	
	private AICTableRowLineItemVO[]    aICTableRowLineItemVO    =	new AICTableRowLineItemVO[0];
		private String deleteButton;
		private String addButton;
		private String template_name;
		private String tableName;
		
		private String templateStatus="";
		private String recFlag = "";
		private String disableAll = "N";
		private String deleteMsgFlag = "N";

	private AICTemplateVO objAICTemplateVO = null;
	/**
	 * @return
	 */
	public AICDisplayTableWithDataForm()
	{
		recFlag = "Y";
		objAICTemplateVO = new AICTemplateVO();
		disableAll = "N";
	}
			
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


	public void setAICTableRowLineItemVO(int index, AICTableRowLineItemVO objAICTableRowLineItemVO)
	{
		this.aICTableRowLineItemVO[index] = objAICTableRowLineItemVO;
	}

	public AICTableRowLineItemVO getAICTableRowLineItemVO(int index )
	{
				return aICTableRowLineItemVO[index];
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





















	public void setTemplateStatus(List list)
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


	public AICTemplateColumnLineItemVO[] getTemplateStatus()
	{
		return aICTemplateColumnLineItemVO;
	}






/*

	public void setRowsList(List rowList)
	{
		Iterator iterRow = rowList.iterator();
				
		AICTableRowsDataVO objAICTableRowsDataVO = null;
		AICTableRowsDataVO objAICTableRowsDataVOInside = null;
		
		HashMap hm = new HashMap();
		
		List singleRow = null;
		List actualRowList = new ArrayList();
		int maxColumnCount = 0;
		int tempCount=0;
		while(iterRow.hasNext())
		{
			objAICTableRowsDataVO = (AICTableRowsDataVO)iterRow.next();
			String strRowId = objAICTableRowsDataVO.getRowId();
			singleRow = new ArrayList();
			AICTableRowLineItemVO objAICTableRowLineItemVO = new AICTableRowLineItemVO();
			tempCount= 0;
			if(!hm.containsKey(strRowId))
			{			
				Iterator iterRowInside = rowList.iterator();			
				while(iterRowInside.hasNext())
				{
					objAICTableRowsDataVOInside = (AICTableRowsDataVO)iterRowInside.next();
					String strRowIdInside = objAICTableRowsDataVOInside.getRowId();
					AICTableDataLineItemVO objAICTableDataLineItemVO = new AICTableDataLineItemVO();
					if(strRowId.equals(strRowIdInside))
					{
						tempCount++;
						objAICTableDataLineItemVO.setAICTableRowsDataVO(objAICTableRowsDataVOInside);
						singleRow.add(objAICTableDataLineItemVO);
						hm.put(strRowIdInside,strRowIdInside);		
						if(tempCount > maxColumnCount)
						{
							maxColumnCount = tempCount;
						}
					}
						
				}
				objAICTableRowLineItemVO.setDataList(singleRow);
				actualRowList.add(objAICTableRowLineItemVO);
			}
			
		}
		
		List newRowList = new ArrayList(actualRowList.size());

		Iterator iter = actualRowList.iterator();
		while (iter.hasNext())
		{
			AICTableRowLineItemVO objAICTableRowLineItemVO = (AICTableRowLineItemVO) iter.next();			
			newRowList.add(objAICTableRowLineItemVO);
			
		}
		
		aICTableRowLineItemVO = (AICTableRowLineItemVO[]) newRowList.toArray(new AICTableRowLineItemVO[newRowList.size()]);
	} */

	public void setRowsList(List rowList)
					{
						ArrayList collColumnVO = new ArrayList();  
					
					
						Iterator iterRow = rowList.iterator();
				
						AICTableRowsDataVO objAICTableRowsDataVO = null;
						AICTableRowsDataVO objAICTableRowsDataVOInside = null;
		
						HashMap hm = new HashMap();
		
						List singleRow = null;
						List actualRowList = new ArrayList();
						int maxColumnCount = 0;
						int tempCount=0;
						while(iterRow.hasNext())
						{
							objAICTableRowsDataVO = (AICTableRowsDataVO)iterRow.next();
							String strRowId = objAICTableRowsDataVO.getRowId();
							singleRow = new ArrayList();
							AICTableRowLineItemVO objAICTableRowLineItemVO = new AICTableRowLineItemVO();
						
							if(!hm.containsKey(strRowId))
							{			
								Iterator iterRowInside = rowList.iterator();
								tempCount= 0;			
								while(iterRowInside.hasNext())
								{
									objAICTableRowsDataVOInside = (AICTableRowsDataVO)iterRowInside.next();
									String strRowIdInside = objAICTableRowsDataVOInside.getRowId();
									AICTableDataLineItemVO objAICTableDataLineItemVO = new AICTableDataLineItemVO();
									if(strRowId.equals(strRowIdInside))
									{
										AICTemplateColumnVO objAICTemplateColumnVO = new AICTemplateColumnVO();
										objAICTemplateColumnVO.setColumnId(objAICTableRowsDataVOInside.getColumnId());
										objAICTemplateColumnVO.setColumnName(objAICTableRowsDataVOInside.getColumnName());
										objAICTemplateColumnVO.setColumnOrder(objAICTableRowsDataVOInside.getColumnOrder());
										objAICTemplateColumnVO.setColumnType(objAICTableRowsDataVOInside.getColumnType());
										objAICTemplateColumnVO.setRequired(objAICTableRowsDataVOInside.getRequired());
										objAICTemplateColumnVO.setActive("Y");
										objAICTemplateColumnVO.setRowId(objAICTableRowsDataVOInside.getRowId());
										objAICTemplateColumnVO.setTableId(objAICTableRowsDataVOInside.getTableId());
									
										tempCount++;
										objAICTableDataLineItemVO.setAICTableRowsDataVO(objAICTableRowsDataVOInside);
										singleRow.add(objAICTableDataLineItemVO);
										hm.put(strRowIdInside,strRowIdInside);		
										if(tempCount > maxColumnCount)
										{
											maxColumnCount = tempCount;
											collColumnVO.add(tempCount-1,objAICTemplateColumnVO);
										}
									}
						
								}
								objAICTableRowLineItemVO.setDataList(singleRow);
								actualRowList.add(objAICTableRowLineItemVO);
							}
			
						}
						///////////
						maxColumnCount = this.objAICTemplateVO.getTemplateColVOCollection().size();
		
						List newRowList = new ArrayList(actualRowList.size());

						Iterator iter = actualRowList.iterator();
						int count = 0;
						while (iter.hasNext())
						{
							AICTableRowLineItemVO objAICTableRowLineItemVO = (AICTableRowLineItemVO) iter.next();
						
							AICTableDataLineItemVO arrAICTableDataLineItemVO[] = objAICTableRowLineItemVO.getDataList();
						
							List listTableDataList = new ArrayList(arrAICTableDataLineItemVO.length);
							String strRowId = "";
							try{
							
							AICTableDataLineItemVO objAICTableDataLineItemVO1 = arrAICTableDataLineItemVO[0];
							strRowId = objAICTableDataLineItemVO1.getAICTableRowsDataVO().getRowId();
							}catch(ArrayIndexOutOfBoundsException ai)
							{
								
							}
							
							
							for(int i=0;i<maxColumnCount;i++)
							{
								AICTableDataLineItemVO objAICTableDataLineItemVO = new AICTableDataLineItemVO();
							
								if(i>=arrAICTableDataLineItemVO.length)
								{
									/*
									AICTemplateColumnVO objAICTemplateColumnVO = (AICTemplateColumnVO)collColumnVO.get(i);
									*/
									
									ArrayList tempColumnVOColl =  (ArrayList)this.objAICTemplateVO.getTemplateColVOCollection();
									AICTemplateColumnVO objAICTemplateColumnVO = (AICTemplateColumnVO)tempColumnVOColl.get(i);
									
								
									AICTableRowsDataVO objAICTableRowsDataVO2 = new AICTableRowsDataVO();
									objAICTableRowsDataVO2.setActive(objAICTemplateColumnVO.getActive());
									objAICTableRowsDataVO2.setColumnId(objAICTemplateColumnVO.getColumnId());
									objAICTableRowsDataVO2.setColumnName(objAICTemplateColumnVO.getColumnName());
									objAICTableRowsDataVO2.setColumnOrder(objAICTemplateColumnVO.getColumnOrder());
									objAICTableRowsDataVO2.setColumnType(objAICTemplateColumnVO.getColumnType());
									objAICTableRowsDataVO2.setDataId(AICUtil.getUniqueId());
									objAICTableRowsDataVO2.setDataValue("");
									objAICTableRowsDataVO2.setRequired(objAICTemplateColumnVO.getRequired());
								
									//objAICTableRowsDataVO2.setRowId(objAICTemplateColumnVO.getRowId());
									objAICTableRowsDataVO2.setRowId(strRowId);
									objAICTableRowsDataVO2.setTableId(objAICTemplateColumnVO.getTableId());
																
									objAICTableDataLineItemVO.setAICTableRowsDataVO(objAICTableRowsDataVO2);		
														
								
								}
								else
								{
									objAICTableDataLineItemVO = arrAICTableDataLineItemVO[i];
								}
								listTableDataList.add(i,objAICTableDataLineItemVO);
							
							
							}
						
							objAICTableRowLineItemVO.setDataList(listTableDataList);
											
							newRowList.add(objAICTableRowLineItemVO);
						
			
						}
		
						aICTableRowLineItemVO = (AICTableRowLineItemVO[]) newRowList.toArray(new AICTableRowLineItemVO[newRowList.size()]);
	}
					
	public AICTableRowLineItemVO[] getRowsList()
	{
		return aICTableRowLineItemVO;
	}

	/*
	public AICTemplateColumnLineItemVO[] getColumnList() {
		return objAICTemplateColumnLineItemVO;
	}

	public void setUser(AICTemplateColumnLineItemVO[] items) {
		objAICTemplateColumnLineItemVO = items;
	}
	*/


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



	public void reset(ActionMapping mapping, HttpServletRequest request)
	{
		if("submit".equals(mapping.getParameter()))
		{
			for (int i = 0; i < aICTemplateColumnLineItemVO.length; i++)
			{
				aICTemplateColumnLineItemVO[i].setChecked(false);
			}
			for (int i = 0; i < aICTableRowLineItemVO.length; i++)
			{
				aICTableRowLineItemVO[i].setChecked(false);
			}
		}
		
		deleteButton = null;
		addButton = null;
		disableAll = "N";
		
	}
	 /**
		 * @return
		 */

		public String getDeleteButton()
		{
			return deleteButton;
		}

		/**
		 * @param string
		 */
		public void setDeleteButton(String string)
		{
			deleteButton = string;
		}

		/**
		 * @return
		 */
		public String getAddButton() {
			return addButton;
		}

		/**
		 * @param string
		 */
		public void setAddButton(String string) {
			addButton = string;
		}


		/**
		 * @return
		 */
		public String getTemplate_name() {
			return template_name;
		}

		/**
		 * @param string
		 */
		public void setTemplate_name(String string) {
			template_name = string;
		}

	/**
	 * @return tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @param string
	 */
	public void setTableName(String s) {
		tableName = s;
	}
		

		/**
		 * @return
		 */
		public String getRecFlag() {
			return recFlag;
		}

		/**
		 * @param string
		 */
		public void setRecFlag(String string) {
			recFlag = string;
		}

	/**
	 * @return
	 */
	public AICTemplateVO getAICTemplateVO() {
		return objAICTemplateVO;
	}

	/**
	 * @param templateVO
	 */
	public void setAICTemplateVO(AICTemplateVO templateVO) {
		objAICTemplateVO = templateVO;
	}

		/**
		 * @return
		 */
		public String getDisableAll() {
			return disableAll;
		}

		/**
		 * @param string
		 */
		public void setDisableAll(String string) {
			disableAll = string;
		}

		/**
		 * @return
		 */
		public String getDeleteMsgFlag() {
			return deleteMsgFlag;
		}

		/**
		 * @param string
		 */
		public void setDeleteMsgFlag(String deleteMsg) {
			deleteMsgFlag = deleteMsg;
		}

}
