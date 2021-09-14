package oem.edge.ets.fe.ismgt.cntrl;

import java.sql.*;
import java.util.*;
import java.io.*;

import oem.edge.common.*;
import oem.edge.amt.*;

import javax.servlet.*;
import javax.servlet.http.*;

import oem.edge.ets.fe.ismgt.model.*;
import oem.edge.ets.fe.ismgt.resources.*;
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

/**
 * @author v2phani
 *
 * dispataches to various JSP(s) based on various states and user_type
 */
public class EtsIssFilterDispatchCntrl implements EtsIssFilterConstants {

	public static final String VERSION = "1.10";
	private HttpServletRequest request;
	private HttpServletResponse response;
	private EtsIssFilterObjectKey issobjkey;

	/**
	 * Constructor for EtsIssFilterDispatchCntrl.
	 */
	public EtsIssFilterDispatchCntrl(HttpServletRequest request, HttpServletResponse response, EtsIssFilterObjectKey issobjkey) {
		super();
		this.request = request;
		this.response = response;
		this.issobjkey = issobjkey;
	}

	/**
	 * dispatch method to different navigations,based on states and other rules
	 * 
	 */

	public void dispatchRequest(int processreq) throws ServletException, IOException {

		String userType = issobjkey.getEs().gDECAFTYPE;
		int state = issobjkey.getState();
		String localIssSubType = issobjkey.getIssueSubType();
		//print flag
		String printRep = AmtCommonUtils.getTrimStr(request.getParameter("prnt"));

		//down load flag
		String downLd = AmtCommonUtils.getTrimStr(request.getParameter("dwnld"));

		if (processreq == 0 || processreq == 3 || processreq == ETSISSRPTWALLFC || processreq == ETSISSRPTISUBFC || processreq == ETSISSRPTASGNDFC) {

			state = processreq;

		}

		//default
		String dispatcherPath = "/jsp/ismgt/EtsIssueWelcome.jsp";

		//error msg jsp
		String errorMsgPath = "jsp/ismgt/EtsIssueErrorMsg.jsp";

		//error msg jsp
		String noRecsMsgPath = "jsp/ismgt/EtsIssNoRecs.jsp";

		SysLog.log(SysLog.DEBUG, "Final state in dispatcher", "Final state in dispatcher ===" + state + "");

		switch (state) {

			case ETSISSUEERR :

				dispatcherPath = errorMsgPath;

				break;

			case ETSFILTERNORECS :

				dispatcherPath = noRecsMsgPath;

				break;

			case ETSISSUEINITIAL :

				dispatcherPath = "/jsp/ismgt/EtsIssueWelcome.jsp";
				break;

			case ETSISSTYPESWELCOME :

				dispatcherPath = "/jsp/ismgt/EtsIssTypesWelcome.jsp";
				break;

			case ETSCUSTOMFIELDSWELCOME :

				dispatcherPath = "/jsp/ismgt/EtsCustomFieldsWelcome.jsp";
				break;				
						
			case ETSISSRPTWALL :

				if (localIssSubType.equals(ETSISSUESUBTYPE)) {

					if (userType.equals("I")) {

						if (downLd.equals("Y")) {

							dispatcherPath = "CSVServlet.wss";

						} else if (printRep.equals("Y")) {

							dispatcherPath = "/jsp/ismgt/EtsIssRepWorkAllPrint.jsp";
						} else {

							dispatcherPath = "/jsp/ismgt/EtsIssRepWorkAll.jsp";

						}

					} else {

						if (printRep.equals("Y")) {

							dispatcherPath = "/jsp/ismgt/EtsIssRepWorkAllExtPrint.jsp";

						} else {

							dispatcherPath = "/jsp/ismgt/EtsIssRepWorkAllExt.jsp";

						}

					}

				} else if (localIssSubType.equals(ETSCHANGESUBTYPE)) {

					if (printRep.equals("Y")) {

						dispatcherPath = "/jsp/ismgt/filtercr/EtsCrRepWorkAllPrint.jsp";

					} else {

						dispatcherPath = "/jsp/ismgt/filtercr/EtsCrRepWorkAll.jsp";

					}

				}

				break;

			case ETSISSRPTWALLFC :

				if (localIssSubType.equals(ETSISSUESUBTYPE)) {

					if (userType.equals("I")) {

						dispatcherPath = "/jsp/ismgt/EtsIssFilterWorkAll.jsp";

					} else {

						dispatcherPath = "/jsp/ismgt/EtsIssFilterWorkAllExt.jsp";

					}

				} else if (localIssSubType.equals(ETSCHANGESUBTYPE)) {

					dispatcherPath = "/jsp/ismgt/filtercr/EtsCrFilterWorkAll.jsp";

				}

				break;

			case ETSISSRPTWALLFCGO :

				if (localIssSubType.equals(ETSISSUESUBTYPE)) {

					if (userType.equals("I")) {

						if (printRep.equals("Y")) {

							dispatcherPath = "/jsp/ismgt/EtsIssRepWorkAllPrint.jsp";

						} else {

							dispatcherPath = "/jsp/ismgt/EtsIssRepWorkAll.jsp";

						}

					} else {

						if (printRep.equals("Y")) {

							dispatcherPath = "/jsp/ismgt/EtsIssRepWorkAllExtPrint.jsp";

						} else {

							dispatcherPath = "/jsp/ismgt/EtsIssRepWorkAllExt.jsp";

						}

					}

				} else if (localIssSubType.equals(ETSCHANGESUBTYPE)) {

					if (printRep.equals("Y")) {

						dispatcherPath = "/jsp/ismgt/filtercr/EtsCrRepWorkAllPrint.jsp";

					} else {

						dispatcherPath = "/jsp/ismgt/filtercr/EtsCrRepWorkAll.jsp";

					}

				}

				break;

			case ETSISSRPTISUB :

				if (localIssSubType.equals(ETSISSUESUBTYPE)) {

					if (userType.equals("I")) {

						if (printRep.equals("Y")) {

							dispatcherPath = "/jsp/ismgt/EtsIssRepISubPrint.jsp";

						} else {

							dispatcherPath = "/jsp/ismgt/EtsIssRepISub.jsp";

						}

					} else {

						if (printRep.equals("Y")) {

							dispatcherPath = "/jsp/ismgt/EtsIssRepISubExtPrint.jsp";

						} else {

							dispatcherPath = "/jsp/ismgt/EtsIssRepISubExt.jsp";
						}

					}

				} else if (localIssSubType.equals(ETSCHANGESUBTYPE)) {

					if (printRep.equals("Y")) {

						dispatcherPath = "/jsp/ismgt/filtercr/EtsCrRepISubPrint.jsp";

					} else {

						dispatcherPath = "/jsp/ismgt/filtercr/EtsCrRepISub.jsp";

					}

				}

				break;

			case ETSISSRPTISUBFC :

				if (localIssSubType.equals(ETSISSUESUBTYPE)) {

					if (userType.equals("I")) {

						dispatcherPath = "/jsp/ismgt/EtsIssFilterISub.jsp";

					} else {

						dispatcherPath = "/jsp/ismgt/EtsIssFilterISubExt.jsp";

					}

				} else if (localIssSubType.equals(ETSCHANGESUBTYPE)) {

					dispatcherPath = "/jsp/ismgt/filtercr/EtsCrFilterISub.jsp";

				}

				break;

			case ETSISSRPTISUBFCGO :

				if (localIssSubType.equals(ETSISSUESUBTYPE)) {

					if (userType.equals("I")) {

						if (printRep.equals("Y")) {

							dispatcherPath = "/jsp/ismgt/EtsIssRepISubPrint.jsp";

						} else {

							dispatcherPath = "/jsp/ismgt/EtsIssRepISub.jsp";

						}

					} else {

						if (printRep.equals("Y")) {

							dispatcherPath = "/jsp/ismgt/EtsIssRepISubExtPrint.jsp";

						} else {

							dispatcherPath = "/jsp/ismgt/EtsIssRepISubExt.jsp";

						}

					}

				} else if (localIssSubType.equals(ETSCHANGESUBTYPE)) {

					if (printRep.equals("Y")) {

						dispatcherPath = "/jsp/ismgt/filtercr/EtsCrRepISubPrint.jsp";

					} else {

						dispatcherPath = "/jsp/ismgt/filtercr/EtsCrRepISub.jsp";

					}

				}

				break;

			case ETSISSRPTASGND :

				if (!issobjkey.isProjBladeType()) {

					if (printRep.equals("Y")) {

						dispatcherPath = "/jsp/ismgt/EtsIssRepAsgndMePrint.jsp";

					} else {

						dispatcherPath = "/jsp/ismgt/EtsIssRepAsgndMe.jsp";
					}

				} else {

					if (userType.equals("I")) {

						if (printRep.equals("Y")) {

							dispatcherPath = "/jsp/ismgt/EtsIssRepAsgndMePrint.jsp";

						} else {

							dispatcherPath = "/jsp/ismgt/EtsIssRepAsgndMe.jsp";
						}

					} else {

						dispatcherPath = "jsp/ismgt/EtsIssNotAuthMsg.jsp";
					}

				}

				break;

			case ETSISSRPTASGNDFC :

				if (!issobjkey.isProjBladeType()) {

					dispatcherPath = "/jsp/ismgt/EtsIssFilterAsgndMe.jsp";

				} else {

					if (userType.equals("I")) {

						dispatcherPath = "/jsp/ismgt/EtsIssFilterAsgndMe.jsp";

					} else {

						dispatcherPath = "jsp/ismgt/EtsIssNotAuthMsg.jsp";

					}

				}

				break;

			case ETSISSRPTASGNDFCGO :

				if (!issobjkey.isProjBladeType()) {

					if (printRep.equals("Y")) {

						dispatcherPath = "/jsp/ismgt/EtsIssRepAsgndMePrint.jsp";

					} else {

						dispatcherPath = "/jsp/ismgt/EtsIssRepAsgndMe.jsp";
					}

				} else {

					if (userType.equals("I")) {

						if (printRep.equals("Y")) {

							dispatcherPath = "/jsp/ismgt/EtsIssRepAsgndMePrint.jsp";

						} else {

							dispatcherPath = "/jsp/ismgt/EtsIssRepAsgndMe.jsp";
						}

					} else {

						dispatcherPath = "jsp/ismgt/EtsIssNotAuthMsg.jsp";

					}

				}

				break;

			case ETSISSUESHOWLOG :

				dispatcherPath = "jsp/ismgt/EtsIssueCommentsLog.jsp";

				break;

			case ETSSHOWUSERINFO :

				dispatcherPath = "jsp/ismgt/EtsIssUserInfo.jsp";

				break;

			case DOWNLOADTOCSV :

				dispatcherPath = "/servlet/oem/edge/ets/CSVServlet.wss";

				break;

		}

		Global.println("dispatcher path in filter dispatch cntrl====" + dispatcherPath);
		//get dispatcher and forward

		RequestDispatcher rd = request.getRequestDispatcher(dispatcherPath);

		rd.forward(request, response);

	}

}
