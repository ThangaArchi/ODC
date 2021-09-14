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
import oem.edge.ets.fe.aic.dyntab.vo.AICTableRowsDataVO;

/**
 * @author thanga
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AICTableDataLineItemVO implements Serializable{
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.3";
		
		
	private AICTableRowsDataVO AICTableRowsDataVO = new AICTableRowsDataVO();
	
	
	/*
	private boolean checked = false;

	
	public void setChecked(boolean b)
	{
		checked = b;
	}

	
	public boolean isChecked()
	{
		return checked;
	}
	*/

	/**
	 * @param b - setAICTableRowsDataVO
	 */
	public void setAICTableRowsDataVO(AICTableRowsDataVO rowVO)
	{
		AICTableRowsDataVO = rowVO;
	}

	/**
	 * @return AICTableRowsDataVO
	 */
	public AICTableRowsDataVO getAICTableRowsDataVO()
	{
		return AICTableRowsDataVO;
	}
}