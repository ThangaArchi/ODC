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

/**
 * @author vivek
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface AICDynTabTemplateProxyBase {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	
	public ValueObject createTemplate(ValueObject objValueObject) throws AICDataAccessException;
	public ValueObject createTemplateWithColumns(ValueObject objValueObject) throws AICDataAccessException;
	public ValueObject deleteColumnFromTemplate(ValueObject objValueObject) throws AICDataAccessException;
	public ValueObject editColumnFromTemplate(ValueObject objValueObject) throws AICDataAccessException;
	public ValueObject viewTemplateWithColums(ValueObject objValueObject) throws AICDataAccessException ;
	public void deleteTemplate(ValueObject objValueObject) throws AICDataAccessException;
	public Collection findAllTemplates() throws AICDataAccessException;
	public ValueObject viewTemplate(ValueObject objValueObject) throws AICDataAccessException;
	public ValueObject viewTemplateByName(ValueObject objValueObject) throws AICDataAccessException;
	public Collection findAllTemplates(Connection conn) throws AICDataAccessException;
	public ValueObject viewTemplateStatus(ValueObject objValueObject) throws AICDataAccessException;		
}
