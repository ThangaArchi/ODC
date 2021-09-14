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

package oem.edge.ets.fe.aic.dyntab.proxy;

import java.sql.Connection;
import java.util.Collection;

import oem.edge.ets.fe.aic.common.exception.AICDataAccessException;
import oem.edge.ets.fe.aic.common.vo.ValueObject;
import oem.edge.ets.fe.aic.dyntab.bdlg.AICDynTabTemplateBusinessDelegate;

/**
 * @author vivek
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AICDynTabTemplateProxy implements AICDynTabTemplateProxyBase {
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.2";
		
	private AICDynTabTemplateProxyBase implementation;
	public AICDynTabTemplateProxy() {
		implementation = new AICDynTabTemplateBusinessDelegate();
	}
	public ValueObject createTemplate(ValueObject objValueObject) throws AICDataAccessException{
		return implementation.createTemplate(objValueObject);
	}
	public ValueObject createTemplateWithColumns(ValueObject objValueObject) throws AICDataAccessException{
		return implementation.createTemplateWithColumns(objValueObject);
	}
	public ValueObject deleteColumnFromTemplate(ValueObject objValueObject) throws AICDataAccessException{
		return implementation.deleteColumnFromTemplate(objValueObject);
	}
	public ValueObject editColumnFromTemplate(ValueObject objValueObject) throws AICDataAccessException{
		return implementation.editColumnFromTemplate(objValueObject);
	}
	public ValueObject viewTemplateWithColums(ValueObject objValueObject) throws AICDataAccessException {
		return implementation.viewTemplateWithColums(objValueObject);
	}
	public void deleteTemplate(ValueObject objValueObject) throws AICDataAccessException
	{
		implementation.deleteTemplate(objValueObject);
	}
	public Collection findAllTemplates() throws AICDataAccessException
	{
		return implementation.findAllTemplates();
	}
	public ValueObject viewTemplate(ValueObject objValueObject) throws AICDataAccessException {
				return implementation.viewTemplate(objValueObject);
	}
	public ValueObject viewTemplateByName(ValueObject objValueObject) throws AICDataAccessException{
				return implementation.viewTemplateByName(objValueObject);
	}
	
	public Collection findAllTemplates(Connection conn) throws AICDataAccessException
	{
		return implementation.findAllTemplates();
	}
	
	public ValueObject viewTemplateStatus(ValueObject objValueObject) throws AICDataAccessException {
				return implementation.viewTemplateStatus(objValueObject);
		}

}
