package oem.edge.ets.fe.ismgt.cntrl;

//import oem.edge.ed.cm.common.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import oem.edge.common.*;
import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ismgt.actions.EtsIssActionCmdFactory;
import oem.edge.ets.fe.ismgt.actions.EtsIssChgActionBean;
import oem.edge.ets.fe.ismgt.actions.EtsIssueFactory;
import oem.edge.ets.fe.ismgt.bdlg.EtsDropDownDataPrepBean;
import oem.edge.ets.fe.ismgt.helpers.EtsAmtHfBean;
import oem.edge.ets.fe.ismgt.helpers.EtsIssActionObjKeyPrep;
import oem.edge.ets.fe.ismgt.helpers.EtsIssCommonSessnParams;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterObjKeyPrep;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsPrimaryContactInfo;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
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

public class ETSIssuesServlet implements EtsIssFilterConstants {

	public static final String VERSION = "1.13";

	PrintWriter writer = null;
	Connection conn = null;
	EdgeAccessCntrl es = null;
	Hashtable params = null;
	ETSProj proj = null;
	int topCatId = 0;
	String sLink = null;
	Hashtable actionMap = null;
	HttpServletRequest request = null;
	HttpServletResponse response = null;
	AccessCntrlFuncs acf = null;

	public ETSIssuesServlet(Hashtable Params, ETSProj Proj, int TopCatId, PrintWriter Writer, Connection Conn, EdgeAccessCntrl Es, AccessCntrlFuncs Acf, String Linkid, HttpServletRequest Request, HttpServletResponse Response) {

		params = Params;
		proj = Proj;
		topCatId = TopCatId;
		writer = Writer;
		conn = Conn;
		es = Es;
		acf = Acf;
		sLink = Linkid;
		request = Request;
		response = Response;

	}

	ETSIssuesServlet() {

	}

	/**
	 * the core request method, which handles different requests
	 * 
	 */

	public void processRequest() throws SQLException, IOException, Exception {

		String actiontype = AmtCommonUtils.getTrimStr((String) params.get("actionType"));
		
		Global.println("action tyoe==="+actiontype);

		EtsIssActionObjKeyPrep etsActKeyPrep = new EtsIssActionObjKeyPrep(params, proj, topCatId, es, sLink, request, response);
		EtsIssObjectKey etsIssObjKey = etsActKeyPrep.getEtsIssActionObjKey();

		//get session
		HttpSession session = request.getSession(true);
		request.setAttribute("issactionobjkey", etsIssObjKey);

		//		helper objects//
		EtsIssCommonSessnParams etsCommonParams = null;
		EtsIssFilterObjKeyPrep etsKeyObjPrep = null;
		EtsAmtHfBean amtHf = null;
		EtsIssFilterObjectKey issFilterObjkey = new EtsIssFilterObjectKey();

		//		get key object
		etsKeyObjPrep = new EtsIssFilterObjKeyPrep(request, es);
		issFilterObjkey = etsKeyObjPrep.getEtsIssFilterObjKey(request, es,proj);

		//		initialize the common params bean//
		etsCommonParams = new EtsIssCommonSessnParams(session, proj.getProjectId());
		ETSProj etsProj = etsCommonParams.getEtsProj();
		EtsPrimaryContactInfo etsContInfo = etsCommonParams.getEtsContInfo();

		///set amthf in session	
		amtHf = new EtsAmtHfBean(request, response, issFilterObjkey);
		request.setAttribute("etsamthf", amtHf);

		request.setAttribute("issfilterkey", issFilterObjkey);

		//EtsIssueFactory issueFactory = new EtsIssueFactory();

		//get the facotry object
		//EtsIssChgActionBean issActObj = issueFactory.createEtsIssChgActionBean(etsIssObjKey);

		//get the content
		//writer.println(issActObj.processRequest());

		if (actiontype.equals("submitIssue") || actiontype.equals("viewIssue") || actiontype.equals("modifyIssue") || actiontype.equals("resolveIssue") || actiontype.equals("rejectIssue") || actiontype.equals("closeIssue") || actiontype.equals("commentIssue") || actiontype.equals("reqNewIssTyp")||actiontype.equals("submitChange") || actiontype.equals("viewChange") || actiontype.equals("commentChange") || actiontype.equals("chgOwner")|| actiontype.equals("withDrwIssue") || actiontype.equals("subscrIssue")|| actiontype.equals("unSubscrIssue")) {

			//		get command factory and assign toit
			EtsIssActionCmdFactory actComFac = new EtsIssActionCmdFactory();
			int processreq = actComFac.createEtsIssActionCmd(etsIssObjKey).processRequest();
			
			Global.println("processreq in ETSIssuesServlet==="+processreq);

			if (actiontype.equals("submitIssue") || actiontype.equals("viewIssue") || actiontype.equals("modifyIssue") || actiontype.equals("resolveIssue") || actiontype.equals("rejectIssue") || actiontype.equals("closeIssue") || actiontype.equals("commentIssue") || actiontype.equals("reqNewIssTyp") || actiontype.equals("chgOwner") || actiontype.equals("withDrwIssue") || actiontype.equals("subscrIssue")|| actiontype.equals("unSubscrIssue")) {
				//dispatch to suitable view
				EtsIssActionDispatchCntrl dispatchCntrl = new EtsIssActionDispatchCntrl(etsIssObjKey);
				dispatchCntrl.dispatchRequest(processreq);

			}

			if (actiontype.equals("submitChange") || actiontype.equals("viewChange") || actiontype.equals("commentChange")) {

				//dispatch to suitable view
				EtsCrActionDispatchCntrl crDispatchCntrl = new EtsCrActionDispatchCntrl(etsIssObjKey);
				crDispatchCntrl.dispatchRequest(processreq);

			}

		} else {

			EtsIssueFactory issueFactory = new EtsIssueFactory();

			//  get the facotry object
			EtsIssChgActionBean issActObj = issueFactory.createEtsIssChgActionBean(etsIssObjKey);

			//get the content
			writer.println(issActObj.processRequest());
		}

	} //end of process request

} // end of class
