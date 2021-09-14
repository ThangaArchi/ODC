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

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

public class DocumentServiceImpl implements DocumentService {

	/** Stores the Logging object */
	private Logger m_pdLog = Logger.getLogger(DocumentServiceImpl.class);

	/**
	 * @param strUserId
	 * @param strPassword
	 * @return
	 */
	public Workspace[] getWorkspaces(String strUserId, String strPassword)
		throws ServiceException {

		m_pdLog.error(
			"getWorkspaces Servicing client : "
				+ strUserId
				+ " : "
				+ strPassword);

		Workspace[] udWorkspaces = null;

		DocumentServiceDAO udDAO = new DocumentServiceDAO();
		try {
			udDAO.prepare();
			List lstWorkspaces = udDAO.getProjects(strUserId, true);
			udWorkspaces = new Workspace[lstWorkspaces.size()];
			lstWorkspaces.toArray(udWorkspaces);
		} catch (Exception e) {
			m_pdLog.error(e);
			throw new ServiceException(
				"Error fetching Workspaces from database:" + e.getMessage());
		} finally {
			try {
				udDAO.cleanup();
			} catch (SQLException e) {
				m_pdLog.error(e);
				throw new ServiceException(
					"Error fetching Workspaces from database:"
						+ e.getMessage());
			}
		}

		return udWorkspaces;
	}

	/**
	 * @param strWorkspaceId
	 * @return
	 */
	public Category[] getFolders(String strWorkspaceId)
		throws ServiceException {
		m_pdLog.error("getFolders Servicing client : " + strWorkspaceId);

		Category[] udFolders = null;

		DocumentServiceDAO udDAO = new DocumentServiceDAO();
		try {
			udDAO.prepare();
			List lstFolders = udDAO.getFolders(strWorkspaceId, false);
			udFolders = new Category[lstFolders.size()];
			lstFolders.toArray(udFolders);
		} catch (Exception e) {
			m_pdLog.error(e);
			throw new ServiceException(
				"Error fetching Folders from database:" + e.getMessage());
		} finally {
			try {
				udDAO.cleanup();
			} catch (SQLException e) {
				m_pdLog.error(e);
				throw new ServiceException(
					"Error fetching Workspaces from database:"
						+ e.getMessage());
			}
		}

		return udFolders;
	}

}