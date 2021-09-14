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
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.resources.EtsCrActionConstants;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsCrActionDispatchCntrl implements EtsCrActionConstants {

	public static final String VERSION = "1.28";

	private EtsIssObjectKey etsIssObjKey;

	/**
	 * 
	 */
	public EtsCrActionDispatchCntrl(EtsIssObjectKey etsIssObjKey) {
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

		String dispatcherPath = "/jsp/ismgt/actions/submitnewcr/SubmitNewCrScr1.jsp";

		//error msg jsp
		String errorMsgPath = "/jsp/ismgt/actions/submitnew/EtsActionErrorMsg.jsp";

		SysLog.log(SysLog.DEBUG, "Final state in CR Action dispatcher", "Final state in CR Action dispatcher ===" + processreq + "");

		switch (processreq) {

			case FATALERROR :

			case ERRINACTION :

				dispatcherPath = errorMsgPath;

				break;

			case MAINPAGE :

				dispatcherPath = "/jsp/ismgt/EtsIssueWelcome.jsp";

				break;

			case NEWINITIAL :

			case VALIDERRCONTDESCR :

			case EDITDESCR :

				dispatcherPath = "/jsp/ismgt/actions/submitnewcr/SubmitNewCrScr1.jsp";

				break;

			case CONTDESCR :

			case FILEATTACH :

			case DELETEFILE :

			case EDITFILEATTACH :

				dispatcherPath = "/jsp/ismgt/actions/submitnewcr/SubmitNewCrScr2.jsp";

				break;

			case ADDFILEATTACH :

				dispatcherPath = "/jsp/ismgt/actions/submitnewcr/SubmitNewCrScr3.jsp";

				break;

			case SUBMITTODB :

				dispatcherPath = "/jsp/ismgt/actions/submitnewcr/SubmitNewCrScr4.jsp";

				break;

			case VIEWCRDETS :

				dispatcherPath = "/jsp/ismgt/actions/viewcr/ViewCrScr1.jsp";

				break;

			case COMMENTSCRFIRSTPAGE :

				dispatcherPath = "/jsp/ismgt/actions/commentcr/CommentCrScr1.jsp";

				break;

			case COMMENTSCRSUBMITTODB :
			case COMMENTSCRCANCELDB :

				dispatcherPath = "/jsp/ismgt/actions/viewcr/ViewCrScr1.jsp";

				break;

			case COMMENTSCRCONFIRMPAGE :

				dispatcherPath = "/jsp/ismgt/actions/commentcr/CommentCrScr2.jsp";

				break;

			case COMMENTSCREDITFILEATTACH :
			case COMMENTSCRFILEATTACH :
			case COMMENTSCRDELETEFILE :

				dispatcherPath = "/jsp/ismgt/actions/attchfilecr/AttachFileCrScr1.jsp";

				break;

			case COMMENTSCRSUBTFILEATTACH :

			case COMMENTSCRCANCFILEATTACH :

				dispatcherPath = "/jsp/ismgt/actions/viewcr/ViewCrScr1.jsp";

				break;

			case FILEATTACHSCRCONFIRMPAGE :

				dispatcherPath = "/jsp/ismgt/actions/attchfilecr/AttachFileCrScr2.jsp";

				break;

		}

		//get dispatcher and forward

		RequestDispatcher rd = etsIssObjKey.getRequest().getRequestDispatcher(dispatcherPath);

		rd.forward(etsIssObjKey.getRequest(), etsIssObjKey.getResponse());

	}

	////

} //end of class
