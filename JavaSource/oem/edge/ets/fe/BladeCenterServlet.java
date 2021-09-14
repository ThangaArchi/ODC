package oem.edge.ets.fe;

import java.io.IOException;
import java.io.PrintWriter;

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
public class BladeCenterServlet extends HttpServlet {

	public static final String VERSION = "1.1";

	public void init(ServletConfig config) throws ServletException {

	}

	/**
	 * This service method is the core method which will do the following actions in ETS Issues Logs
	
	 * 
	 */

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter();

		try {

			out.println("HELLO BLADE CENTER");

			System.out.println("HELLO BLADE CENTER");

		} catch (Exception ex) {

			ex.printStackTrace();

		} finally {

		}

		out.flush();
		out.close();

	} //end of service

} //end of class
