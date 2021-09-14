/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005-2005                                     */
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
import java.util.ArrayList;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.ets.fe.ismgt.dao.EtsIssSearchByNumDAO;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterDetailsBean;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssProbInfoUsr1Model;
import oem.edge.ets.fe.ismgt.model.EtsIssSearchByNumModel;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssSearchByNumBdlg {

	public static final String VERSION = "1.2";
	

	/**
	 * 
	 */
	public EtsIssSearchByNumBdlg() {

	super();
	// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 *
	 */
	public EtsIssSearchByNumModel submitSrchByNum(String searchNum,String projectId) throws SQLException, Exception {
		
		//if the srch num matches single entry  then display the view details of issue directly
		EtsIssSearchByNumDAO srchNumDao = new EtsIssSearchByNumDAO();

		EtsIssSearchByNumModel srchNumModel = new EtsIssSearchByNumModel();
		
		int count=0;
		
		if(AmtCommonUtils.isResourceDefined(searchNum)) {
			
			//first check the count of srch num
			count = srchNumDao.getRecCountForSrchNum(searchNum,projectId);
		}
		
		else {
			
			count = -1000;
			srchNumModel.setSrchcount(count);
		}

		

		if (count == 0) {
			
			srchNumModel.setSrchcount(0);

		}

		if (count == 1) {

			srchNumModel = submitSrchResultOne(searchNum,projectId);
			srchNumModel.setSrchcount(count);
		}

		if (count > 1) {

			srchNumModel = submitMultiSrch(searchNum);
			srchNumModel.setSrchcount(count);
		}

		return srchNumModel;

	} //end of method

	/**
	 * 
	 * @param searchNum
	 * @throws SQLException
	 * @throws Exception
	 */

	private EtsIssSearchByNumModel submitSrchResultOne(String searchNum,String projectId) throws SQLException, Exception {

		EtsIssSearchByNumDAO srchNumDao = new EtsIssSearchByNumDAO();
		
		//get edge problem id based on search num	
		String edgeProblemId = srchNumDao.getProblemIdForSrchNum(searchNum,projectId);
		
		//set the srch by num model params
		EtsIssSearchByNumModel srchNumModel = new EtsIssSearchByNumModel();
		srchNumModel.setEdgeProblemId(edgeProblemId);
		srchNumModel.setSrchByNum(searchNum);

		return srchNumModel;

	}
	
	/**
	 * 
	 * @param searchNum
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	private EtsIssSearchByNumModel submitMultiSrch(String searchNum) throws SQLException, Exception {
		
		EtsIssSearchByNumModel srchNumModel = new EtsIssSearchByNumModel();
		srchNumModel.setSrchByNum(searchNum);

		return srchNumModel;

	}

} //end of class
