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

package oem.edge.ets.fe.ismgt.cntrl;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import oem.edge.common.SysLog;
import oem.edge.amt.*;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.resources.EtsIssueActionConstants;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssActionDispatchCntrl implements EtsIssueActionConstants {

	public static final String VERSION = "1.39";

	private EtsIssObjectKey etsIssObjKey;

	/**
	 * 
	 */
	public EtsIssActionDispatchCntrl(EtsIssObjectKey etsIssObjKey) {
		super();

		this.etsIssObjKey = etsIssObjKey;

	}

	///

	/**
	 * dispatch method to different navigations,based on states and other rules
	 * 
	 */

	public void dispatchRequest(int processreq) throws ServletException, IOException {

		int actionkey = etsIssObjKey.getActionkey();

		//		print flag
		String printRep = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("prnt"));

		String dispatcherPath = "/jsp/ismgt/actions/submitissue/SubmitNew.jsp";

		//error msg jsp
		String errorMsgPath = "/jsp/ismgt/actions/submitissue/EtsActionErrorMsg.jsp";

		SysLog.log(SysLog.DEBUG, "Final state in Action dispatcher", "Final state in Action dispatcher ===" + processreq + "");

		switch (processreq) {

			case FATALERROR :

			case ERRINACTION :

				dispatcherPath = errorMsgPath;

				break;

			case ACTION_NOTAUTHORIZED :
			case ACTION_INPROCESS :

				dispatcherPath = "/jsp/ismgt/actions/viewissue/EtsIssNotAuth.jsp";

				break;

			case MAINPAGE :

				dispatcherPath = "/jsp/ismgt/EtsIssueWelcome.jsp";

				break;

			case SUBMIT_ONBEHALF_EXT :

				dispatcherPath = "/jsp/ismgt/actions/submitissue/SubmitIssueScr0.jsp";

				break;

			case NEWINITIAL :

			case VALIDERRCONTDESCR :

			case EDITDESCR :

				dispatcherPath = "/jsp/ismgt/actions/submitissue/SubmitIssueScr11.jsp";

				break;

			case CONTDESCR :
			
			case ADDFILEATTACH :

			case FILEATTACH :

			case DELETEFILE :

			case EDITFILEATTACH :

				dispatcherPath = "/jsp/ismgt/actions/submitissue/SubmitIssueScr3.jsp";

				break;
		

			case SUBMITTODB :
			case ISSUEFINALSUBMIT :
			

				dispatcherPath = "/jsp/ismgt/actions/submitissue/SubmitIssueScr7.jsp";

				break;

				//////////////////VIEW ISSUES////////////////////////////////////////////////		

			case VIEWISSUEDETS :
			case VIEWISSUEDETSREFRESHFILES :

				if (printRep.equals("Y")) {

					dispatcherPath = "/jsp/ismgt/actions/viewissue/ViewIssuePrint.jsp";

				} else {

					dispatcherPath = "/jsp/ismgt/actions/viewissue/ViewIssueScr1.jsp";

				}
				break;

				////////////////MODIFY ISSUES////////////////////////////////////////////////			

			case MODIFYISSUEFIRSTPAGE :
				
			case MODIFYEDITCUSTOMFIELDSCONT :

				dispatcherPath = "/jsp/ismgt/actions/modifyissue/ModifyIssueScr6.jsp";

				break;

			case MODIFYEDITDESCR :

			case MODIFYVALIDERRCONTDESCR :

				dispatcherPath = "/jsp/ismgt/actions/modifyissue/ModifyIssueScr1.jsp";

				break;

			case MODIFYEDITFILEATTACH :
			case MODIFYFILEATTACH :
			case MODIFYDELETEFILE :

				dispatcherPath = "/jsp/ismgt/actions/modifyissue/ModifyIssueScr3.jsp";

				break;
				
			case MODIFYEDITCUSTOMFIELDS :

				dispatcherPath = "/jsp/ismgt/actions/modifyissue/ModifyIssueScr4.jsp";

				break;
	
				
			case MODIFYEDITNOTIFYLIST :

				dispatcherPath = "/jsp/ismgt/actions/modifyissue/ModifyIssueScr5.jsp";

				break;

			case MODIFYISSUEIDENTFDEFAULT :
			case MODIFYVALIDERRADDISSTYPE :

				dispatcherPath = "/jsp/ismgt/actions/modifyissue/ModifyIssueScr22.jsp";

				break;

			case MODIFYEDITISSUEIDENTF :
			case MODIFYADDISSUETYPE :
			case MODIFYGOSUBTYPEA :

			case MODIFYGOSUBTYPEB :

			case MODIFYGOSUBTYPEC :

			case MODIFYGOSUBTYPED :

			case MODIFYEDITSUBTYPEA :

			case MODIFYEDITSUBTYPEB :

			case MODIFYEDITSUBTYPEC :

			case MODIFYEDITSUBTYPED :

			case MODIFYVALIDERRCONTIDENTF :

				dispatcherPath = "/jsp/ismgt/actions/modifyissue/ModifyIssueScr23.jsp";

				break;

			case MODIFYSUBMITTODB :

				dispatcherPath = "/jsp/ismgt/actions/modifyissue/ModifyIssueScr7.jsp";

				break;

				////////////////RESOLVE ISSUES////////////////////////////////////////////////			

			case RESOLVEISSUEFIRSTPAGE :

				dispatcherPath = "/jsp/ismgt/actions/resolveissue/ResolveIssueScr6.jsp";

				break;

			case RESOLVEEDITFILEATTACH :
			case RESOLVEFILEATTACH :
			case RESOLVEDELETEFILE :

				dispatcherPath = "/jsp/ismgt/actions/resolveissue/ResolveIssueScr3.jsp";

				break;

			case RESOLVESUBMITTODB :

				dispatcherPath = "/jsp/ismgt/actions/resolveissue/ResolveIssueScr7.jsp";

				break;

			case CREATEISSUETYPE1STPAGE :
			case EDITCREATEISSUETYPE :

				dispatcherPath = "/jsp/ismgt/actions/createisstyp/CreateIssueTypeScr1.jsp";

				break;

			case CREATEISSUETYPECONTINUE :

				dispatcherPath = "/jsp/ismgt/actions/createisstyp/CreateIssueTypeScr2.jsp";

				break;

			case CREATEISSUETYPESUBMIT :

				dispatcherPath = "/jsp/ismgt/actions/createisstyp/CreateIssueTypeScr3.jsp";

				break;

			case CREATEISSUETYPECANCEL :

				dispatcherPath = "/jsp/ismgt/EtsIssueWelcome.jsp";

				break;

				/////////////change issue owner type

			case CHGOWNER1STPAGE :

				dispatcherPath = "/jsp/ismgt/actions/chgowner/ChangeOwnerScr1.jsp";

				break;

			case CHGOWNERSUBMIT :

				dispatcherPath = "/jsp/ismgt/actions/chgowner/ChangeOwnerScr2.jsp";

				break;

			case SUBSCRISSUE :

				dispatcherPath = "/jsp/ismgt/actions/subscrissue/SubscribeIssueScr1.jsp";

				break;

			case UNSUBSCRISSUE :

				dispatcherPath = "/jsp/ismgt/actions/subscrissue/UnSubscribeIssueScr1.jsp";

				break;

		}

		//get dispatcher and forward

		RequestDispatcher rd = etsIssObjKey.getRequest().getRequestDispatcher(dispatcherPath);

		rd.forward(etsIssObjKey.getRequest(), etsIssObjKey.getResponse());

	}

	////

} //end of class
