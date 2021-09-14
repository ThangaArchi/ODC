/*   ------------------------------------------------------------------          */
/*   IBM                                                                                     */
/*                                                                                               */  
/*   OCO Source Materials                                                          */
/*                                                                                               */
/*   Product(s): ICC/PROFIT                                                       */
/*                                                                                               */
/*   (C)Copyright IBM Corp. 2002,2003 		              */ 
/*                                                                                               */  
/*   The source code for this program is not published or otherwise */
/*   divested of its trade secrets, irrespective of what has been        */
/*   deposited with the US Copyright Office.                                  */
/*   ------------------------------------------------------------------           */

package oem.edge.ets.fe;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AmtErrorHandler;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
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


public class ETSErrorServlet extends HttpServlet {
   public final static String Copyright = "(C) Copyright IBM Corp.  2002, 2003";
 
	

	public ETSErrorServlet() {
		super();
	}

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		EdgeAccessCntrl es = new EdgeAccessCntrl();
		if (!es.GetProfile(response, request)) {
			SysLog.log(SysLog.DEBUG, this, "Authentication Process Failed");
			return;
		}
		
		String Msg = null;
		try {

			String ecode = (String) request.getParameter("ecode");
			if (ecode == null) {
				ecode = "";
			} else {
				ecode = ecode.trim();
			}
			
			String sname = (String) request.getParameter("sname");
			if (sname == null) {
				sname = "";
			} else {
				sname = sname.trim();
			}

			AmtErrorHandler Err = new AmtErrorHandler();
			StringBuffer sMsg = new StringBuffer();
			sMsg.append("<table sumary=\"\" width=\"100%\"><tr><td headers=\"\">");
			sMsg.append("The document you requested does not exist on this server or cannot be served. <br /><br /> Please contact IBM Customer Connect help desk with the following details: <br /><br /><b>Error Code</b>: ");
			sMsg.append(ecode + "<br />");
			sMsg.append("<b>Description</b>: ");
			sMsg.append(sname);
			sMsg.append("</td><tr></table>");
			out.println(Err.printErrorScreen(sMsg.toString()));

		} catch (Exception ex) {
			SysLog.log(SysLog.ERR, this, ex);
			Global.poperrorMsg(out, "If this problem persists, please contact the IBM Customer Connect Help Desk at 1-888-220-3343");
		} finally {
			out.flush();
			out.close();
		}
	}
}
