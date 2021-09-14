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

import javax.servlet.http.HttpServletRequest;

import oem.edge.ets.fe.aic.dyntab.vo.AICTemplateVO;
import oem.edge.ets.fe.documents.BaseDocumentAction;
import oem.edge.ets.fe.documents.BaseDocumentForm;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * @author vivek
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AICTableShowForm extends BaseDocumentForm{
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.3";
		
	private AICTemplateVO[] aicTemplateVO = new AICTemplateVO[0];


	public void setAICTemplateVO(int index,AICTemplateVO aicTemplateVO) {
				this.aicTemplateVO[index] =	aicTemplateVO;
	}

	public AICTemplateVO getAICTemplateVO(int index) {
				return aicTemplateVO[index];
	}


	public void setTemplateList(List list) {
		aicTemplateVO =
					(AICTemplateVO[]) list.toArray(
						new AICTemplateVO[list.size()]);
	}

	public AICTemplateVO[] getTemplateList()
	{
		return aicTemplateVO;
	}


	public void reset(ActionMapping mapping, HttpServletRequest request) {

	}


}
