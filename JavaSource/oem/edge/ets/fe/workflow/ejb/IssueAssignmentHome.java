/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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


package oem.edge.ets.fe.workflow.ejb;

import oem.edge.ets.fe.dealtracker.ejb.ETSBaseHome;

/**
 * Home interface for Enterprise Bean: DocExpiration
 */
public interface IssueAssignmentHome extends ETSBaseHome {
	/**
	 * Creates a default instance of Session Bean: DocExpiration
	 */
	public oem.edge.ets.fe.dealtracker.ejb.ETSBaseTimer create()
		throws javax.ejb.CreateException, java.rmi.RemoteException;
}
