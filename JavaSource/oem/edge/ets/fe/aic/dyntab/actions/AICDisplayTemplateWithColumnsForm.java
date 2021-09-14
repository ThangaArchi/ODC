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

import oem.edge.ets.fe.aic.dyntab.vo.AICTemplateColumnLineItemVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTemplateColumnVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTemplateVO;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * @author vivek
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AICDisplayTemplateWithColumnsForm extends ActionForm {
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";

	
	private AICTemplateColumnLineItemVO[] aICTemplateColumnLineItemVO =
		new AICTemplateColumnLineItemVO[0];
		private String deleteButton;
		private String addButton;
		private String template_name;
		private String recFlag = "";
	/**
	 * @return
	 */
	public AICDisplayTemplateWithColumnsForm()
		{
			recFlag = "Y";
		}
	public AICTemplateColumnLineItemVO getAICTemplateColumnLineItemVO(int index) {

		return aICTemplateColumnLineItemVO[index];
	}

	/**
	 * @param list
	 */
	//AICTemplateColumnVO
	
	public void setAICTemplateColumnVO(
			int index,
			AICTemplateColumnLineItemVO objAICTemplateColumnLineItemVO) {
			this.aICTemplateColumnLineItemVO[index] =
				objAICTemplateColumnLineItemVO;
		}
	public AICTemplateColumnLineItemVO getAICTemplateColumnVO(
				int index) {
				return aICTemplateColumnLineItemVO[index];
			}
		
	
	public void setAICTemplateColumnLineItemVO(
		int index,
		AICTemplateColumnLineItemVO objAICTemplateColumnLineItemVO) {
		this.aICTemplateColumnLineItemVO[index] =
			objAICTemplateColumnLineItemVO;
	}
	

	public void setColumnsList(List list) {
		List newList = new ArrayList(list.size());

		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			AICTemplateColumnLineItemVO objAICTemplateColumnLineItemVO =
				new AICTemplateColumnLineItemVO();
			AICTemplateColumnVO objAICTemplateColumnVO =
				(AICTemplateColumnVO) iter.next();
			objAICTemplateColumnLineItemVO.setAICTemplateColumnVO(
			objAICTemplateColumnVO);
			newList.add(objAICTemplateColumnLineItemVO);
		}

		aICTemplateColumnLineItemVO =
			(AICTemplateColumnLineItemVO[]) newList.toArray(
				new AICTemplateColumnLineItemVO[newList.size()]);

	}
	
	public AICTemplateColumnLineItemVO[] getColumnsList()
	{
		return aICTemplateColumnLineItemVO;
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
	public AICTemplateColumnLineItemVO[] getAICTemplateColumnVO() {
		return aICTemplateColumnLineItemVO;
	}

	/**
	 * @param items
	 */
	public void setAICTemplateColumnVO(AICTemplateColumnLineItemVO[] items) {
		this.aICTemplateColumnLineItemVO = items;
	}
		
		

	public void reset(ActionMapping mapping, HttpServletRequest request) {
		if("submit".equals(mapping.getParameter()))
		{		
			for (int i = 0; i < aICTemplateColumnLineItemVO.length; i++) {
				aICTemplateColumnLineItemVO[i].setChecked(false);
			}
		}
		deleteButton = null;
		addButton = null;
		
	}
	 /**
		 * @return
		 */
		public String getDeleteButton() {
			return deleteButton;
		}

		/**
		 * @param string
		 */
		public void setDeleteButton(String string) {
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

}
