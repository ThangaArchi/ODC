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

package oem.edge.ets.fe.acmgt.actions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AMTException;
import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.UserObject;
import oem.edge.common.DbConnect;
import oem.edge.common.Global;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSHeaderFooter;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.acmgt.bdlg.RemoveMembrBdlg;
import oem.edge.ets.fe.acmgt.model.RemoveMembrModel;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.ismgt.dao.EtsProjMemberDAO;
import oem.edge.ets.fe.ismgt.helpers.EtsAmtHfBean;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterObjKeyPrep;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterObjectKey;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;

import org.apache.commons.logging.Log;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RemoveMembrAction extends Action implements EtsIssFilterConstants {

	private static Log logger = EtsLogger.getLogger(RemoveMembrAction.class);

	public static final String VERSION = "1.3";

	/**
	 * 
	 */
	public RemoveMembrAction() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		ActionForward forward = new ActionForward();
		ActionErrors errors = new ActionErrors();

		logger.debug("entered print action");

		EdgeAccessCntrl es = new EdgeAccessCntrl();

		//helper objects//

		EtsIssFilterObjKeyPrep etsFilterKeyObjPrep = null;
		EtsIssFilterObjectKey issFilterObjkey = new EtsIssFilterObjectKey();
		EtsAmtHfBean amtHf = null;

		Hashtable params = new Hashtable(); //for params
		DbConnect db = null;

		try {

			if (!Global.loaded) {
				Global.Init();
			}

			//get connection
			db = new DbConnect();
			db.makeConn(ETSDATASRC);

			if (es.GetProfile(response, request)) {

				String projectidStr = AmtCommonUtils.getTrimStr(request.getParameter("proj"));

				ETSProj proj = ETSDatabaseManager.getProjectDetails(db.conn, projectidStr);

				//get params
				params = AmtCommonUtils.getServletParameters(request);

				//get link id
				String sLink = AmtCommonUtils.getTrimStr(request.getParameter("linkid"));

				// if not superadmin and not executive and not member, then redirect the user to landing page.
				// changed for 4.4.1
				if (ETSUtils.checkUserRole(es, proj.getProjectId()).equals(Defines.INVALID_USER)) {

					return new ActionForward("chkUserRole");

				}

				ETSHeaderFooter headerfooter = new ETSHeaderFooter();
				headerfooter.init(request, response);

				ETSCat topCat = headerfooter.getTopCat();

				int topCatId = topCat.getId();

				//prepare iss filter key object
				//get key object
				etsFilterKeyObjPrep = new EtsIssFilterObjKeyPrep(request, es);
				issFilterObjkey = etsFilterKeyObjPrep.getEtsIssFilterObjKey(request, es, proj);

				/// set amthf in session	
				amtHf = new EtsAmtHfBean(request, response, issFilterObjkey);
				request.setAttribute("etsamthf", amtHf);

				request.setAttribute("issfilterkey", issFilterObjkey);

				///
				int nextstate=processRequest(request, proj, params, es);

				forward = mapping.findForward(getForwardMap(nextstate));

			} //end of es-profile

		} catch (AMTException amtException) {

			Exception innerException = amtException.getException();

			if (innerException != null && (innerException instanceof java.sql.SQLException)) {

				db.removeConn((SQLException) innerException);

			}

			if (innerException != null) {
				logger.error("AMTException in RemoveMembrAction", innerException);
				innerException.printStackTrace();

			}

			return mapping.findForward("error");

		} catch (SQLException se) {

			if (db != null) {
				db.removeConn(se);
			}
			if (se != null) {
				logger.error("SQLException in RemoveMembrAction", se);
				se.printStackTrace();

			}

			return mapping.findForward("error");

		} catch (Exception ex) {

			if (ex != null) {
				logger.error("Exception in RemoveMembrAction", ex);
				ex.printStackTrace();

			}

			return mapping.findForward("error");

		} finally {

			if (db != null)
				db.closeConn();
			db = null;
		}

		return forward;

	}

	/**
	 * 
	 * @param nextpg
	 * @return
	 */

	String getForwardMap(int nextpg) {

		String forward = "";

		Global.println("nextpg orward map===" + nextpg);

		switch (nextpg) {

			case 2 :

				forward = "remMembr2ndPage";

				break;

			case 3 :

				forward = "remMembr3rdPage";

				break;

			default :

				forward = "remMembr2ndPage";

				break;
		}

		Global.println("ffff forward===" + forward);

		return forward;
	}

	/**
	 * 
	 * @param request
	 * @param proj
	 * @param params
	 * @param es
	 * @throws SQLException
	 * @throws Exception
	 */

	private int processRequest(HttpServletRequest request, ETSProj proj, Hashtable params, EdgeAccessCntrl es) throws SQLException, Exception {

		int nextpg = getCurrentState(request);
		int nextstate = nextpg;

		switch (nextpg) {

			case 2 :

				nextstate = execute1stPage(request, params, proj, es);

				break;

			case 3 :

				nextstate = execute2ndPage(request, params, proj, es);

				break;

			default :

				break;
		}

		return nextstate;

	}

	/**
	 * 
	 * @param request
	 * @param proj
	 * @throws SQLException
	 * @throws Exception
	 */

	private int execute1stPage(HttpServletRequest request, Hashtable params, ETSProj proj, EdgeAccessCntrl es) throws SQLException, Exception {
		int nextpg = getCurrentState(request);

		String projectidStr = proj.getProjectId();

		String oldUserId = AmtCommonUtils.getTrimStr(request.getParameter("uid"));

		//			call the ismgt functions//
		RemoveMembrBdlg remBdlg = new RemoveMembrBdlg();
		List issueList = remBdlg.getIssuesRecsForRemoveMember(projectidStr, oldUserId);
		List taskList = remBdlg.getTaskRecsForRemoveMember(projectidStr, oldUserId);
		List clientList = remBdlg.getClientVoiceForRemoveMembr(projectidStr, oldUserId);
		UserObject userObj = remBdlg.getRemoveUserDets(oldUserId);
		
		Vector subWSforUsrVect = remBdlg.getSubWrkSpcsForUser(projectidStr,oldUserId);
		ArrayList subWsList = new ArrayList();
		if(!subWSforUsrVect.isEmpty()){
			for(int i=0; i<subWSforUsrVect.size(); i++){
				String sWsId = ETSUtils.checkNull(subWSforUsrVect.elementAt(i).toString());
				String subWrkName = remBdlg.getSubWsName(sWsId);
				subWsList.add(subWrkName);
			}
		}
		
		request.setAttribute("subWsList", subWsList);
		//get the user object
		request.setAttribute("userObj", userObj);

		//set issue list recs
		request.setAttribute("issueList", issueList);
		request.setAttribute("taskList", taskList);
		request.setAttribute("clientList", clientList);

		//get the user list
		List userList = remBdlg.getProjMembrListWithoutVisitors(projectidStr, proj.isProjBladeType(),oldUserId);
		request.setAttribute("userList", userList);

		//set the selected user
		List prevUserList = new ArrayList();
		String currentUserId = es.gIR_USERN;
		prevUserList.add(currentUserId);
		request.setAttribute("prevUserList", prevUserList);

		EtsProjMemberDAO projDao = new EtsProjMemberDAO();
		String primaryContact = projDao.getProjPrimaryContact(oldUserId, projectidStr);

		request.setAttribute("primaryContact", primaryContact);

		if (isUserIdReadyToDelete(primaryContact, issueList, taskList, clientList,subWsList)) {

			RemoveMembrModel remModel = new RemoveMembrModel();

			remModel = remBdlg.replaceUserId(params, proj, oldUserId, es.gUSERN, es.gIR_USERN);
			request.setAttribute("remModel", remModel);
			nextpg = 3;

		}

		return nextpg;

	}

	/**
	 * 
	 * @param request
	 * @param params
	 * @param proj
	 * @param es
	 */

	private int execute2ndPage(HttpServletRequest request, Hashtable params, ETSProj proj, EdgeAccessCntrl es) throws Exception{

		RemoveMembrModel remModel = new RemoveMembrModel();
		RemoveMembrBdlg remBdlg = new RemoveMembrBdlg();
		int nextpg = getCurrentState(request);

		String oldUserId = AmtCommonUtils.getTrimStr(request.getParameter("uid"));

		logger.debug("userid====" + oldUserId);

		String submitall = AmtCommonUtils.getTrimStr(request.getParameter("submitall.x"));

		if (AmtCommonUtils.isResourceDefined(submitall)) {

			remModel = remBdlg.replaceUserId(params, proj, oldUserId, es.gUSERN, es.gIR_USERN);

		}

		request.setAttribute("remModel", remModel);

		return nextpg;
	}

	/**
	 * 
	 * @param request
	 * @return
	 */

	private int getCurrentState(HttpServletRequest request) {

		int nextpg = 0;

		String nextpgStr = AmtCommonUtils.getTrimStr(request.getParameter("nextpg"));

		Global.println("nextpg orward map===" + nextpgStr);

		if (AmtCommonUtils.isResourceDefined(nextpgStr)) {

			nextpg = Integer.parseInt(nextpgStr);
		}

		return nextpg;

	}

	public boolean isUserIdReadyToDelete(String primaryContact, List issueList, List taskList, List clientList,ArrayList subWsList) {

		int isssize = 0;
		int tasksize = 0;
		int clientsize = 0;
		int swsize = 0;
		boolean flag = false;

		if (issueList != null && !issueList.isEmpty()) {

			isssize = issueList.size();
		}

		if (taskList != null && !taskList.isEmpty()) {

			tasksize = taskList.size();
		}

		if (clientList != null && !clientList.isEmpty()) {

			clientsize = clientList.size();
		}
		
		if (subWsList != null && !subWsList.isEmpty()) {

			swsize = subWsList.size();
		}

		
		if (!primaryContact.equals("Y") && isssize == 0 && tasksize == 0 && clientsize == 0 && swsize == 0) {

			flag = true;
		}

		return flag;
	}

} //end of class
