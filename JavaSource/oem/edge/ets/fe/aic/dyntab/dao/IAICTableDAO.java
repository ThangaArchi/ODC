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

package oem.edge.ets.fe.aic.dyntab.dao;

import java.sql.Connection;
import java.util.Collection;

import oem.edge.ets.fe.aic.common.exception.AICDataAccessException;
import oem.edge.ets.fe.aic.common.exception.AICNoDataFoundException;
import oem.edge.ets.fe.aic.common.exception.AICOptimisticLockException;
import oem.edge.ets.fe.aic.common.vo.ValueObject;

/**
 * @author vivek
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface IAICTableDAO {
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	
		
	public ValueObject findByPrimaryKey(ValueObject objValueObject) throws AICDataAccessException ;
		
	public ValueObject insert(ValueObject pValueObject) throws AICDataAccessException;
	
	public ValueObject update(ValueObject pValueObject)
			throws AICDataAccessException, AICOptimisticLockException;
			
	public void delete(ValueObject pValueObject) throws AICDataAccessException;
	public Collection findTablesByDocId(int intDocID)
				throws AICDataAccessException;		
	public ValueObject insert(ValueObject pValueObject,Connection conn) throws AICDataAccessException;
	
}
