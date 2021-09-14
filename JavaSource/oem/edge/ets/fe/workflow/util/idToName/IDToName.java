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

/**
 * Class       : IDToName
 * Package     : oem.edge.ets.fe.workflow.util.idToName
 * Description : Interface for things like client name from client ID and Name from userid
 * Date		   : Feb 19, 2007
 * 
 * @author     : Pradyumna Achar
 */
public interface IDToName {
	public String convert(String ID);
	public String convert(String ID, DBAccess db)throws Exception;
}

