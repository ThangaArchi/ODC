/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004                                     */
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


/*
 * Created on Jan 22, 2005
 */

package oem.edge.ets.fe.self;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.ETSDBUtils;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author v2sathis
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ETSDeleteKeyAttributeAction extends Action {
	/**
	 *
	 */

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.2";

	public ETSDeleteKeyAttributeAction() {
		super();
	}

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		Connection con = null;
		EdgeAccessCntrl es = new EdgeAccessCntrl();

		ActionForward forward = new ActionForward();

		try {

			con = ETSDBUtils.getConnection();

			if (!es.GetProfile(response, request, con)) {
				return new ActionForward("/login.jsp");
			}

			ETSAttributeForm key = (ETSAttributeForm) form;


			String sOp = request.getParameter("etsop");
			if (sOp == null || sOp.trim().equalsIgnoreCase("")) {
				sOp = "";
			} else {
				sOp = sOp.trim();
			}

			if (sOp.equalsIgnoreCase("delete") || sOp.equalsIgnoreCase("")) {

				forward = mapping.findForward("show");

			} else if (sOp.equalsIgnoreCase("deleteconfirm")) {

				forward = mapping.findForward("confirm");
				StringBuffer sPath = new StringBuffer(forward.getPath());

				key.setSelfId(request.getParameter("self"));
				key.setProjectId(request.getParameter("proj"));
				key.setSectionId(String.valueOf(ETSSelfConstants.SECTION_KEY_ATTRIBUTES));
				key.setSubSectionId(request.getParameter("subsection"));
				key.setSequenceNo(request.getParameter("seqno"));
				key.setMemberId(es.gIR_USERN);

				boolean deleted = ETSSelfDAO.deleteSelfAssessmentExpectation(con,key,es.gIR_USERN);

				if (deleted) {
					sPath.append("?success=true");
					forward = new ActionForward(sPath.toString());
				} else {
					sPath.append("?success=false");
					forward = new ActionForward(sPath.toString());
				}
			}


			return forward;

		} catch (SQLException e) {
			e.printStackTrace();
			return mapping.findForward("error");
		} catch (Exception e) {
			e.printStackTrace();
			return mapping.findForward("error");
		} finally {
			ETSDBUtils.close(con);
		}

	}
}
