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
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.DbConnect;
import oem.edge.common.Global;
import oem.edge.decaf.GenFunctions;
import oem.edge.ets.fe.ismgt.actions.EtsCrShowRtfDetailsCmd;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.bdlg.*;

/**
 * This servlet is to print Help Blurb for ets FUNCTIONS
 *
 */

public class EtsIssHelpBlurbServlet extends HttpServlet implements EtsIssFilterConstants {

	public void init(ServletConfig config) throws javax.servlet.ServletException {
		super.init(config);
	}

	public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		if (!Global.loaded) {

			Global.Init();

		}

		String Op = AmtCommonUtils.getTrimStr(req.getParameter("OP"));

		if (Op.equals("Blurb")) {

			res.setContentType("text/html");
			PrintWriter out = res.getWriter();
			DbConnect db = null;

			try {
				db = new DbConnect();
				db.makeConn(ETSDATASRC);
				GenFunctions.printBlurb(db.conn, out, req);

			} catch (SQLException ex) {

				ex.printStackTrace();

			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (db != null)
					db.closeConn();
			}
			out.close();
		} //end of BLURB

		if (Op.equals("RTF")) {

			EtsCrShowRtfDetailsCmd rtfCmd = new EtsCrShowRtfDetailsCmd(req, res);

			int processreq = rtfCmd.processRequest();

			String dispatcherPath = "/jsp/ismgt/actions/viewcr/ViewCrRtf.jsp";

			//get dispatcher and forward

			RequestDispatcher rd = req.getRequestDispatcher(dispatcherPath);

			rd.forward(req, res);

		} //end of RTF
		
		//for doc migrn
		
		if(Op.equals("MIGRN")) {
			
			
			
			DocMigrationDataPrep migrPrep=new DocMigrationDataPrep();
			migrPrep.migrIssueFiles(req,res);
			
			
			
		}

	} //end of service

}
