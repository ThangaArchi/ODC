/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004                                          */
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

package oem.edge.ets.fe.ismgt.bdlg;

import java.sql.SQLException;
import java.util.List;

import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.ismgt.dao.IssueInfoDAO;

import org.apache.commons.logging.Log;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class IssueRemoveMembrBdlg {
	
	private static Log logger = EtsLogger.getLogger(IssueRemoveMembrBdlg.class);

			public static final String VERSION = "1.1";


	/**
	 * 
	 */
	public IssueRemoveMembrBdlg() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public List getIssuesRecsForRemoveMember(String projectId,String userId) throws SQLException,Exception {
		
		IssueInfoDAO issueDao = new IssueInfoDAO();
		
		return issueDao.getIssuesRecsForRemoveMember(projectId,userId);
		
	}

}//class
