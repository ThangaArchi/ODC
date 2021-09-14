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
package oem.edge.ets.fe.aic.dyntab.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author thanga
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AICTableRowLineItemVO implements Serializable{
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.3";
		
	private AICTableDataLineItemVO[]    aICTableDataLineItemVO    =	new AICTableDataLineItemVO[0];
	
	//private AICTableRowsDataVO AICTableRowsDataVO = new AICTableRowsDataVO();
	
	private boolean checked = false;

	/**
	 * @param b - set checked
	 */
	public void setChecked(boolean b)
	{
		checked = b;
	}

	/**
	 * @return boolean
	 */
	public boolean isChecked()
	{
		return checked;
	}
	
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
		

	/*
	public void setAICTableRowsDataVO(AICTableRowsDataVO rowVO)
	{
		AICTableRowsDataVO = rowVO;
	}

	
	public AICTableRowsDataVO getAICTableRowsDataVO()
	{
		return AICTableRowsDataVO;
	}
	*/
}