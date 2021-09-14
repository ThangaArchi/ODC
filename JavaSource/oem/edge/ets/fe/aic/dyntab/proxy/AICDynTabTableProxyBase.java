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
public interface AICDynTabTableProxyBase {
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	
	public ValueObject createTableFromTemplate(ValueObject objValueObject) throws AICDataAccessException;
	public ValueObject addRowsToTable(ValueObject objValueObject) throws AICDataAccessException;
	public ValueObject deleteRowsFromTable(ValueObject objValueObject) throws AICDataAccessException;
	public ValueObject editRowFromTable(ValueObject objValueObject) throws AICDataAccessException;
	public ValueObject viewTable(ValueObject objValueObject) throws AICDataAccessException;
	public void deleteTable(ValueObject objValueObject) throws AICDataAccessException;
	public Collection findByDocID(int intDocId) throws AICDataAccessException;
	public ValueObject createTableFromTemplate(ValueObject objValueObject,Connection conn) throws AICDataAccessException;
	public ValueObject viewTableWithRows(ValueObject objValueObject) throws AICDataAccessException;
		
		
}
