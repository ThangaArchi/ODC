package oem.edge.ets.fe.ismgt.cntrl;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.ismgt.dao.ETSIssuesManager;
import oem.edge.ets.fe.ismgt.dao.EtsCrPmoIssueDocDAO;
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
 * @version 	1.0
 * @author
 */
public class ETSIssueAttachServlet extends HttpServlet {

	/**
	* @see javax.servlet.http.HttpServlet#void (javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	*/
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	/**
	* @see javax.servlet.http.HttpServlet#void (javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	*/
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String filenumber = req.getParameter("fileNo");
		String edge_problem_id = req.getParameter("edge_problem_id");
		String action = req.getParameter("action");

		//for CR//
		//String etsId = req.getParameter("etsId");

		if (action.equals("view")) {
			try {
				ETSIssuesManager.viewAttach(resp, edge_problem_id, Integer.valueOf(filenumber).intValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (action.equals("viewcr")) {
			try {

				EtsCrPmoIssueDocDAO crDocDao = new EtsCrPmoIssueDocDAO();
				crDocDao.viewCRAttach(resp, edge_problem_id, Integer.valueOf(filenumber).intValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (action.equals("download")) {
			try {
				ETSIssuesManager.downloadAttach(resp, edge_problem_id, Integer.valueOf(filenumber).intValue());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
