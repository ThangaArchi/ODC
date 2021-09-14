/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     © Copyright IBM Corp. 2001-2006                                     */
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


package oem.edge.ets.fe.workflow.util.idToName;

import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import org.apache.commons.logging.Log;
//
/**
 * Class       : IdentityTransformer
 * Package     : oem.edge.ets.fe.workflow.util.idToName
 * Description : 
 * Date		   : Feb 19, 2007
 * 
 * @author     : Pradyumna Achar
 */
public class IdentityTransformer implements IDToName{
	private static Log logger = WorkflowLogger.getLogger(IdentityTransformer.class);

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.util.idToName.IDToName#convert(java.lang.String)
	 */
	public String convert(String ID) {
		return ID;
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.util.idToName.IDToName#convert(java.lang.String, oem.edge.ets.fe.workflow.dao.DBAccess)
	 */
	public String convert(String ID, DBAccess db) throws Exception {
		return ID;
	}
}

