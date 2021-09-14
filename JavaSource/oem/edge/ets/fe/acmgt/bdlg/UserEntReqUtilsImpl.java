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
package oem.edge.ets.fe.acmgt.bdlg;

import java.sql.SQLException;
import java.util.Vector;

import oem.edge.decaf.ws.DecafEntAccessObj;
import oem.edge.decaf.ws.DecafWsRepObj;
import oem.edge.decaf.ws.useraccess.DecafUserAccessDB;
import oem.edge.ets.fe.acmgt.actions.UserEntitlementsMgrIF;
import oem.edge.ets.fe.acmgt.actions.UserProjectsMgrIF;
import oem.edge.ets.fe.acmgt.dao.AddMembrToWrkSpcDAO;
import oem.edge.ets.fe.acmgt.resources.WrkSpcTeamConstantsIF;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class UserEntReqUtilsImpl implements UserEntitlementsMgrIF, UserProjectsMgrIF,WrkSpcTeamConstantsIF {

	private static Log logger = EtsLogger.getLogger(UserEntReqUtilsImpl.class);
	public static final String VERSION = "1.10";

	/**
	 * 
	 */
	public UserEntReqUtilsImpl() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.acmgt.actions.AddEntitlementToUserIdIF#addEntitlementToUserId()
	 */

	/**
			 * * wrapper around decaf API
			 * entReqIrUserId - the ir user id of the person for whom entitlement is being requested
			 * loginIrUserId  - the ir user id who is requesting the entitlement, if the same person requests for himself
			 * then entReqIrUserId === loginIrUserId
			 * To request an entitlement called 'PointOfContact' with datatype value 'CISCO' the DecafEntAccessObj object
			 * need to be set like this
			 * DecafEntAccessObj decafEntAccObj = new DecafEntAccessObj();
			 * decafEntAccObj.setEntName("PointOfContact");
			 * decafEntAccObj.setLevel(1);
			 * Vector dtVec=new Vector()
			 *  dtVec.add("CISCO")
			 *  decafEntAccObj.setDataTypeVal(dtVec)
			 * possible ret codes/ret mesgs from DecafWsRepObj 
			 * SYS_ERROR - System error occured
				DB_ERROR - Data base error occured
				INVALID_USER_ACCESS - User does not have authorization to access application
				INVALID_DATA_TYPE_VALUES - Invalid datatype values
				APPR_NOT_EXST - Approvers are not existed for given entitlement
				INVALID_PROJ - Invalid project request
				INVALID_LVLS - Invalid no of levels 
				INVALID_DATATYPE_LVL - Invalid data type level
				INVALID_ENT_REQ - Invalid entitlement request
				INVALID_USERID - Invlid user id
				SYS_ERROR
				ENT_REQ_SUCCESS - Created entitlement request successfully
	
			 */
	public boolean requestEntitlementToUser(String entReqIrUserId, String loginIrUserId, DecafEntAccessObj decafEntAccObj) {

		DecafUserAccessDB userAccessDb = new DecafUserAccessDB();

		Vector projVect = new Vector(); //list of projects, blank in this case
		int skipReq = 0; //blank in this case
		String log = ""; //blank in this case
		//entitlement object
		boolean ret = false;

		//return msg object
		DecafWsRepObj wsRepObj = new DecafWsRepObj();

		try {

			wsRepObj = userAccessDb.reqUserAccess(entReqIrUserId, decafEntAccObj, projVect, loginIrUserId, skipReq, ETS_BE_DATASRC, log);
			//userAccessDb.reqUser

			if (logger.isDebugEnabled()) {

				logger.debug("DECAF REQ OBJECT RET CODE==" + wsRepObj.getRetCode());
				logger.debug("DECAF REQ OBJECT RET CODE MSG==" + wsRepObj.getRetMsg());
			}

		} finally {

		}

		if (wsRepObj.getRetCode().equals("ENT_REQ_SUCCESS")) {

			ret = true;
		}

		return ret;

	}

	public DecafWsRepObj requestEntitlementObjToUser(String entReqIrUserId, String loginIrUserId, DecafEntAccessObj decafEntAccObj) {

		DecafUserAccessDB userAccessDb = new DecafUserAccessDB();

		Vector projVect = new Vector(); //list of projects, blank in this case
		int skipReq = 0; //blank in this case
		String log = ""; //blank in this case
		//entitlement object
		boolean ret = false;

		//return msg object
		DecafWsRepObj wsRepObj = new DecafWsRepObj();

		try {

			wsRepObj = userAccessDb.reqUserAccess(entReqIrUserId, decafEntAccObj, projVect, loginIrUserId, skipReq, ETS_BE_DATASRC, log);

			if (logger.isDebugEnabled()) {

				logger.debug("DECAF REQ OBJECT RET CODE==" + wsRepObj.getRetCode());
				logger.debug("DECAF REQ OBJECT RET CODE MSG==" + wsRepObj.getRetMsg());
			}

		} finally {

		}

		return wsRepObj;

	}

	/* (non-Javadoc)
		 * @see oem.edge.ets.fe.acmgt.actions.AddEntitlementToUserIdIF#addEntitlementToUserId()
		 */
	public boolean requestProjectToUser(String entReqIrUserId, String loginIrUserId, Vector projVect) {
		DecafUserAccessDB userAccessDb = new DecafUserAccessDB();
		DecafEntAccessObj decafEntAccObj = new DecafEntAccessObj();

		int skipReq = 0; //blank in this case
		String log = ""; //blank in this case
		//entitlement object

		boolean ret = false;

		//return msg object
		DecafWsRepObj wsRepObj = new DecafWsRepObj();

		try {

			wsRepObj = userAccessDb.reqUserAccess(entReqIrUserId, null, projVect, loginIrUserId, skipReq, ETS_BE_DATASRC, log);

			if (logger.isDebugEnabled()) {

				logger.debug("DECAF REQ OBJECT RET CODE==" + wsRepObj.getRetCode());
				logger.debug("DECAF REQ OBJECT RET CODE MSG==" + wsRepObj.getRetMsg());
				
			
			}

		} finally {

		}

		if (wsRepObj.getRetCode().equals("PROJ_REQ_SUCCESS")) {

			ret = true;
		}

		return ret;

	}

	/* (non-Javadoc)
			 * @see oem.edge.ets.fe.acmgt.actions.AddEntitlementToUserIdIF#addEntitlementToUserId()
			 */
	public DecafWsRepObj requestProjectObjToUser(String entReqIrUserId, String loginIrUserId, Vector projVect) {
		DecafUserAccessDB userAccessDb = new DecafUserAccessDB();
		DecafEntAccessObj decafEntAccObj = new DecafEntAccessObj();

		int skipReq = 0; //blank in this case
		String log = ""; //blank in this case
		//entitlement object

		boolean ret = false;

		//return msg object
		DecafWsRepObj wsRepObj = new DecafWsRepObj();

		try {

			wsRepObj = userAccessDb.reqUserAccess(entReqIrUserId, null, projVect, loginIrUserId, skipReq, ETS_BE_DATASRC, log);

			if (logger.isDebugEnabled()) {

				logger.debug("DECAF REQ OBJECT RET CODE==" + wsRepObj.getRetCode());
				logger.debug("DECAF REQ OBJECT RET CODE MSG==" + wsRepObj.getRetMsg());
			}

		} finally {

		}

		

		return wsRepObj;

	}

	/**
		 * 
		 */

	public boolean isUserHasEntitlement(String userId, String entitlement) throws SQLException, Exception {

		boolean bEntitled = false;

		AddMembrToWrkSpcDAO wrkSpcDao = new AddMembrToWrkSpcDAO();

		bEntitled = wrkSpcDao.userHasEntitlement(userId, entitlement);

		return bEntitled;
	}

	/**
			 * 
			 */

	public boolean isUserHasPendingEntitlement(String userId, String entitlement) throws SQLException, Exception {

		boolean bHasPendEtitlement = false;

		AddMembrToWrkSpcDAO wrkSpcDao = new AddMembrToWrkSpcDAO();

		bHasPendEtitlement = wrkSpcDao.userHasPendEntitlement(userId, entitlement);

		return bHasPendEtitlement;
	}

} //end of class
