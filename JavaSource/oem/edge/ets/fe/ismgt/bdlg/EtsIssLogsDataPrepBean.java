package oem.edge.ets.fe.ismgt.bdlg;

import java.sql.*;
import java.util.*;
import java.io.*;

import oem.edge.common.*;
import oem.edge.ets.fe.ismgt.dao.*;
import oem.edge.ets.fe.ismgt.model.EtsIssLogActionDetails;
import oem.edge.ets.fe.ismgt.resources.*;
import oem.edge.amt.*;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004-2004                                     */
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

/**
 * @author v2phani
 * This class will prepare data required for log comments
 */
public class EtsIssLogsDataPrepBean {

	public static final String VERSION = "1.11";
	private String edgeProblemId;

	/**
	 * Constructor for EtsIssLogActionBean.
	 */
	public EtsIssLogsDataPrepBean(String edgeProblemId) {
		super();
		this.edgeProblemId = edgeProblemId;
	}

	/**
	 * This method will query the tables CQ.PROBLEM_INFO_USR1 and takes
	 * the comments,last_action_date,submitter names,last_user from the table
	 * to prepare the log commentary
	 * 
	 */

	public EtsIssLogActionDetails createIssueLogs() throws SQLException,Exception{

		IssueInfoDAO infoDAO = new IssueInfoDAO();
		return infoDAO.getIssueLogsObj(edgeProblemId);
		
		

	}

} //end of class

