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

package oem.edge.ets.fe.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @version 	1.0
 * @author
 */
public class CSVServlet extends HttpServlet implements Servlet {

	

	public void init(ServletConfig config) throws javax.servlet.ServletException {
		super.init(config);
	}
	
	/**
	 * 
	 * 
	 */

	public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		PrintWriter out = null;

		try {

			String csvname = (String) req.getAttribute("CSVNAME");

			if (csvname == null || csvname.equals("")) {

				csvname = "DefaultFile.csv";

			}

			out = res.getWriter();
			res.setContentType("application/csv");
			res.setHeader("Content-disposition", "inline; filename=\"" + csvname + "\"");
			res.setHeader("Content-encoding", "binary");
			ArrayList inputlist = (ArrayList) req.getAttribute("CSVARRAY");

			//get the method from interface
			CSVEncoder csvEncoder = new CSVEncoderImpl();
			out.print(csvEncoder.encode(inputlist).toString());
			out.flush();

		} catch (Exception ex) {

			ex.printStackTrace();

		} finally {

			if (out != null) {

				out.close();

			}
		}
	}

}
