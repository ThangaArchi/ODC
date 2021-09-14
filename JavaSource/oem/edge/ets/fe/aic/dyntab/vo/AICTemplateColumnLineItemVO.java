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

import oem.edge.ets.fe.aic.dyntab.actions.AICColumnDataForm;

/**
 * @author Richard Hightower
 * ArcMind Inc. http://www.arc-mind.com
 */
public class AICTemplateColumnLineItemVO implements Serializable {
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";
	
	private AICTemplateColumnVO AICTemplateColumnVO = new AICTemplateColumnVO();
	private boolean checked = false;


	

	

	/**
	 * @return
	 */
	public boolean isChecked() {
		return checked;
	}

	

	/**
	 * @param b
	 */
	public void setChecked(boolean b) {
		checked = b;
	}

	
	/**
	 * @return
	 */
	public AICTemplateColumnVO getAICTemplateColumnVO() {
		return AICTemplateColumnVO;
	}

	/**
	 * @param columnVO
	 */
	public void setAICTemplateColumnVO(AICTemplateColumnVO columnVO) {
		AICTemplateColumnVO = columnVO;
	}

	

}
