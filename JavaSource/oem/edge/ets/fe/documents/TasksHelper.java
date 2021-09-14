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

package oem.edge.ets.fe.documents;

import java.util.List;

import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.data.DocumentDAO;

import org.apache.commons.logging.Log;

/**
 * @author v2srikau
 */
public class TasksHelper {

	/** Stores the Logger Object */
	private static final Log m_pdLog = EtsLogger.getLogger(TasksHelper.class);

	/**
	 * @param strUserId
	 * @param strProjectId
	 * @return
	 */
	public static List getOpenTasksForUser(
		String strUserId,
		String strProjectId) {
		DocumentDAO udDAO = new DocumentDAO();
		List lstTasks = null;
		try {
			udDAO.prepare();
			lstTasks = udDAO.getOpenTasks(strUserId, strProjectId);
		} catch (Exception e) {
			m_pdLog.error(e);
		} finally {
			try {
				udDAO.cleanup();
			} catch (Exception e) {
				m_pdLog.error(e);
			}
		}
			return lstTasks;
		}

	/**
	 * @param strProjectId
	 * @param strNewUserId
	 * @param strOldUserId
	 * @return
	 */
	public static boolean transferTasks(
		String strProjectId,
		String strOldUserId,
		String strNewUserId) {
		DocumentDAO udDAO = new DocumentDAO();
		boolean bSuccess = false;
		try {
			udDAO.prepare();
			bSuccess =
				udDAO.transferTasks(strProjectId, strOldUserId, strNewUserId);
		} catch (Exception e) {
			m_pdLog.error(e);
		} finally {
			try {
				udDAO.cleanup();
			} catch (Exception e) {
				m_pdLog.error(e);
			}
		}
			return bSuccess;
	}
}