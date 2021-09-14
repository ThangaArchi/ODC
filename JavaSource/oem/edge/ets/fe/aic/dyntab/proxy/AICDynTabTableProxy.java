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
import oem.edge.ets.fe.aic.dyntab.bdlg.AICDynTabTableBusinessDelegate;

/**
 * @author vivek
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AICDynTabTableProxy implements AICDynTabTableProxyBase {
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.2";
		
	private AICDynTabTableBusinessDelegate implementation;
	public AICDynTabTableProxy() {
		implementation = new AICDynTabTableBusinessDelegate();
	}
	public ValueObject createTableFromTemplate(ValueObject objValueObject) throws AICDataAccessException{
		return implementation.createTableFromTemplate(objValueObject);
	}
	public ValueObject addRowsToTable(ValueObject objValueObject) throws AICDataAccessException{
		return implementation.addRowsToTable(objValueObject);
	}
	public ValueObject deleteRowsFromTable(ValueObject objValueObject) throws AICDataAccessException{
		return implementation.deleteRowsFromTable(objValueObject);
	}
	public ValueObject editRowFromTable(ValueObject objValueObject) throws AICDataAccessException{
		return implementation.editRowFromTable(objValueObject);
	}
	public ValueObject viewTable(ValueObject objValueObject) throws AICDataAccessException{
		return implementation.viewTable(objValueObject);
	}
	public void deleteTable(ValueObject objValueObject) throws AICDataAccessException {				
		implementation.deleteTable(objValueObject);
	}
	public Collection findByDocID(int intDocId) throws AICDataAccessException {
				
		return implementation.findByDocID(intDocId);
	}
	public ValueObject createTableFromTemplate(ValueObject objValueObject,Connection conn) throws AICDataAccessException{
		
			return implementation.createTableFromTemplate(objValueObject,conn);
	}
	public String getTableURI(int docid)
	{
		return "/displayAllTablesForDocId.wss?docid="+docid;
	}
	public ValueObject viewTableWithRows(ValueObject objValueObject) throws AICDataAccessException{
				return implementation.viewTableWithRows(objValueObject);
		}
}
