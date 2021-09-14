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

package oem.edge.ets.fe.aic.dyntab.bdlg;

import java.sql.Connection;
import java.util.Collection;

import oem.edge.ets.fe.aic.common.exception.AICDataAccessException;
import oem.edge.ets.fe.aic.common.vo.ValueObject;
import oem.edge.ets.fe.aic.dyntab.bo.AICDynTabTemplateBO;
import oem.edge.ets.fe.aic.dyntab.proxy.AICDynTabTemplateProxyBase;

/**
 * @author vivek
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AICDynTabTemplateBusinessDelegate
	implements AICDynTabTemplateProxyBase {
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.2";
			
	AICDynTabTemplateBO objAICDynTabTemplateBO = null;
	public AICDynTabTemplateBusinessDelegate() {
		objAICDynTabTemplateBO = new AICDynTabTemplateBO();
	}
	public ValueObject createTemplate(ValueObject objValueObject) throws AICDataAccessException {

		return objAICDynTabTemplateBO.createTemplate(objValueObject); 
	}

	public ValueObject createTemplateWithColumns(ValueObject objValueObject) throws AICDataAccessException{

		return objAICDynTabTemplateBO.createTemplateWithColumns(objValueObject);
	}

	public ValueObject deleteColumnFromTemplate(ValueObject objValueObject) throws AICDataAccessException{
		
		return objAICDynTabTemplateBO.deleteColumnFromTemplate(objValueObject);
	}

	public ValueObject editColumnFromTemplate(ValueObject objValueObject) throws AICDataAccessException{

		return objAICDynTabTemplateBO.editColumnFromTemplate(objValueObject);
	}

	public ValueObject viewTemplateWithColums(ValueObject objValueObject) throws AICDataAccessException {
		return objAICDynTabTemplateBO.viewTemplateWithColums(objValueObject);
	}
	
	public void deleteTemplate(ValueObject objValueObject) throws AICDataAccessException
	{
		objAICDynTabTemplateBO.deleteTemplate(objValueObject);
	}
	
	public Collection findAllTemplates() throws AICDataAccessException
	{
		return objAICDynTabTemplateBO.findAllTemplates();
	}
	
	public ValueObject viewTemplate(ValueObject objValueObject) throws AICDataAccessException {
			return objAICDynTabTemplateBO.viewTemplate(objValueObject);
	}
	

	public ValueObject viewTemplateStatus(ValueObject objValueObject) throws AICDataAccessException {
			return objAICDynTabTemplateBO.viewTemplateStatus(objValueObject);
	}
	

	public ValueObject viewTemplateByName(ValueObject objValueObject) throws AICDataAccessException {
				return objAICDynTabTemplateBO.viewTemplateByName(objValueObject);
	}
	public Collection findAllTemplates(Connection conn) throws AICDataAccessException
	{	
		return objAICDynTabTemplateBO.findAllTemplates(conn);
	}
	
}
