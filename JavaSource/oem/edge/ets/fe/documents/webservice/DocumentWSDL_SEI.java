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

package oem.edge.ets.fe.documents.webservice;

/**
 * @author v2srikau
 */
public interface DocumentWSDL_SEI extends java.rmi.Remote {

	/**
	 * @param strWorkspaceId
	 * @return
	 * @throws oem.edge.ets.fe.documents.webservice.ServiceException
	 */
	public oem.edge.ets.fe.documents.webservice.Category[] getFolders(
		java.lang.String strWorkspaceId)
		throws oem.edge.ets.fe.documents.webservice.ServiceException;

	/**
	 * @param strUserId
	 * @param strPassword
	 * @return
	 * @throws oem.edge.ets.fe.documents.webservice.ServiceException
	 */
	public oem.edge.ets.fe.documents.webservice.Workspace[] getWorkspaces(
		java.lang.String strUserId,
		java.lang.String strPassword)
		throws oem.edge.ets.fe.documents.webservice.ServiceException;
}