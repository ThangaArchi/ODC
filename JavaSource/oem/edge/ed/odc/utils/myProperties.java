package oem.edge.ed.odc.utils;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @version 	1.0
 * @author thanga
 */
public class myProperties extends HttpServlet {

	/**
	* @see javax.servlet.GenericServlet#void ()
	*/
	String strSQL="";
	public void init() throws ServletException
	{
		super.init();
		try{
					PropertyTester sqlCode = PropertyTester.getInstance();
					strSQL = sqlCode.getSQLStatement("ODC.DAO.ODCServerDAO.findAllServer");
					System.out.println(strSQL);
		}
		catch(Exception e)
		{
			System.out.println(e);					
		}

	}


	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter out = response.getWriter();
		out.println(" SQL ========> "+strSQL);
	}
}
