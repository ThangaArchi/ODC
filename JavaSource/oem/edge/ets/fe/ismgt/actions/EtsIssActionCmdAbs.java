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

package oem.edge.ets.fe.ismgt.actions;

import java.sql.SQLException;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.ets.fe.ismgt.bdlg.EtsIssActionDataPrepAbsBean;
import oem.edge.ets.fe.ismgt.bdlg.EtsIssActionDataPrepFactory;
import oem.edge.ets.fe.ismgt.bdlg.EtsIssUserRoleFilter;
import oem.edge.ets.fe.ismgt.dao.IssueInfoDAO;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssUserActionsModel;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class EtsIssActionCmdAbs {

	public static final String VERSION = "1.40";

	private EtsIssObjectKey issobjkey;

	/**
	 * Constructor
	 */
	public EtsIssActionCmdAbs(EtsIssObjectKey issobjkey) {
		super();

		this.issobjkey = issobjkey;

	}

	/***
		 * to get suitable busniess delegate  for the business objects
		 * 
		 */

	public EtsIssActionDataPrepAbsBean getActionBdlgBean() throws Exception {

		EtsIssActionDataPrepFactory actDataFac = new EtsIssActionDataPrepFactory();

		EtsIssActionDataPrepAbsBean actAbsBean = actDataFac.createActionDataPrepAbsBean(issobjkey);

		return actAbsBean;

	}

	/**
	 * core method to process request
	 */

	public abstract int processRequest() throws SQLException, Exception;

	/**
	 * @return
	 */
	public EtsIssObjectKey getIssobjkey() {
		return issobjkey;
	}

	/**
	 * To get the sub state of the given actions
	 */

	/**
	 * tO GET user/actions matrix based on edge problem id
	 * @author V2PHANI
	 */

	public EtsIssUserActionsModel getUserActionModel() throws SQLException, Exception {
		
		IssueInfoDAO infoDao = new IssueInfoDAO();
		
		String edgeProblemId = AmtCommonUtils.getTrimStr((String) getIssobjkey().getParams().get("edge_problem_id"));

		boolean isIssueSrcPMO=infoDao.isIssueSrcPMO(edgeProblemId);


		//		get USER/ROLES HELPER
		EtsIssUserRoleFilter userFilter = new EtsIssUserRoleFilter();

		
		//get user/actions matrix
		EtsIssUserActionsModel usrActionsModel = new EtsIssUserActionsModel();
		
		if (!isIssueSrcPMO) {
			
		usrActionsModel = userFilter.getUserActionMatrix(issobjkey);
		
		}
		
		else {
			
			usrActionsModel = userFilter.getUserActionMatrixForPMO(issobjkey);
			
		}
		

		return usrActionsModel;
	}

} //end of class
