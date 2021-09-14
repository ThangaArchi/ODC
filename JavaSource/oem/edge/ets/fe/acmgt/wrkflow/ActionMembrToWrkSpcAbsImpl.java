/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005                                          */
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
package oem.edge.ets.fe.acmgt.wrkflow;

import java.sql.SQLException;
import java.util.HashMap;

import oem.edge.ets.fe.acmgt.helpers.WrkSpcTeamUtils;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcTmParamsKeyPrep;
import oem.edge.ets.fe.acmgt.model.WrkSpcTeamActionsInpModel;
import oem.edge.ets.fe.acmgt.model.WrkSpcTeamObjKey;
import oem.edge.ets.fe.acmgt.model.WrkSpcTmParamsKey;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class ActionMembrToWrkSpcAbsImpl {
	private WrkSpcTeamUtils spcTmUtils;
	public static final String VERSION = "1.8";
	private static Log logger = EtsLogger.getLogger(ActionMembrToWrkSpcAbsImpl.class);


	/**
	 * 
	 */
	public ActionMembrToWrkSpcAbsImpl() {
		super();
		spcTmUtils = new WrkSpcTeamUtils();
		// TODO Auto-generated constructor stub
	}

	/**
			 * 
			 * @param projectId
			 * @param userId
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	public boolean isUserDefndInWrkSpc(String projectId, String userId) throws SQLException, Exception {

		return spcTmUtils.isUserDefndInWrkSpc(projectId, userId);
	}

	/**
	 * 
	 * @param userId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public boolean isUserDefndinICC(String userId) throws SQLException, Exception {

		return spcTmUtils.isUserDefndinICC(userId);
	}

	/**
		 * 
		 * @param userId
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public boolean isUserIdDefndInUD(String userId) throws SQLException, Exception {

		return spcTmUtils.isUserIdDefndInUD(userId);

	}
	
	/**
	 * 
	 * @param wrkSpcType
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	
	public WrkSpcTmParamsKey  getWrkSpcTmParamsKey(String wrkSpcType) throws SQLException,Exception {
		
		WrkSpcTmParamsKeyPrep paramKeyPrep = new WrkSpcTmParamsKeyPrep();
		
		return paramKeyPrep.getWrkSpcTeamParamsKey(wrkSpcType);
		
		
	}
	
	/**
	 * 
	 * @param wrkSpcType
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	
	public HashMap getWrkSpcPropMap(String wrkSpcType) throws SQLException,Exception {
		
		return getWrkSpcTmParamsKey(wrkSpcType).getPropMap();
		
		
	}
	
	/**
	 * 
	 * @param actInpModel
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	
	public WrkSpcTeamObjKey getWrkSpcTeamObjKey(WrkSpcTeamActionsInpModel actInpModel) throws SQLException,Exception{
		
		return spcTmUtils.getWrkSpcTeamObjDets(actInpModel);
		
	}

}//end of class
