/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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

package oem.edge.ets.fe;

import oem.edge.common.*;
//import oem.edge.ed.fe.*;

import java.io.*;
import java.lang.*;
import java.util.*;
import java.sql.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.net.URLEncoder;

import oem.edge.amt.*;

public class ETSCVDeliveryServlet extends HttpServlet {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.10";

//	private Connection conn = null;
//	private boolean isConnected = false;


    protected ETSDatabaseManager databaseManager;


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
	handleRequest(request,response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	handleRequest(request,response);
    }


    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	EdgeAccessCntrl es = new EdgeAccessCntrl();
	String Msg = null;
	ETSDoc document = null;
	Connection conn = null;
	int size = 0;
	String filename = "";
	String userid;
	String projid = "";

	try{
	    conn = ETSDBUtils.getConnection();

	    if (!es.GetProfile(resp, req, conn)){
		return;
	    }


	   userid = req.getParameter("uid");
	   projid = req.getParameter("projid");

	   if (!ETSDatabaseManager.isUserInProject(es.gIR_USERN,projid,conn)) {
		   resp.sendRedirect("ETSConnectServlet.wss");
		   return;
	   }
	   if (!ETSDatabaseManager.isUserInProject(userid,projid,conn)) {
		   resp.sendRedirect("ETSConnectServlet.wss");
		   return;
	   }

	   if (userid != null){
	        String[] s = ETSDatabaseManager.getCVNameSizeById(userid);
	       String sizeStr = s[0];
	       filename = s[1];
	       
	       if (filename.equals("")){
				System.out.println("The cv does not exist");
				return;
	       }
	       
	       if (sizeStr.equals("0")){
		   	System.out.println("The cv does not exist");
		    return;
	       }
	       size = (new Integer(sizeStr)).intValue();

	   }
	   else{
	       System.out.println("userid == null");
	       return;
	   }

	}
	catch (Exception ex) {
	    System.out.println("error in cvdels 1:"+ex);
	    return;
	}
	finally{
	    System.out.println("finally");
	    ETSDBUtils.close(conn);
	}

       // start delivery
       try
       {
          conn = ETSDBUtils.getConnection();
	      deliverContent(req, resp, size,filename,userid, conn);
	     
       }
       catch (Exception ex)
       {
	   System.out.println("error in cvdels 2:"+ex);
	   return;
       }
       finally
	   {
           ETSDBUtils.close(conn);
	   }
    }


    public void init(ServletConfig config)
	throws ServletException
    {
	try{
	    super.init(config);
	    if (!Global.loaded)
		Global.Init();
	   	}
	catch (Exception e)
	    {
		e.printStackTrace();
		throw new ServletException(e.getMessage());
 	    }
    }

    public void destroy()
    {
    }

    private String getParameter(HttpServletRequest req, String key)
    {
	String value = req.getParameter(key);

	if (value == null)
	    {
		return "";
	    }
	else
	    {
		return value;
	    }
    }


// from file content handler  ***************************************************************

	private void deliverContent(HttpServletRequest req, HttpServletResponse resp, int size,String filename,String userid, Connection conn)
		 throws ServletException, IOException
	 {
		 OutputStream out = null;
		 EdgeAccessCntrl es = null;

		 
		  String mmtype = ETSMimeDataList.getMimeTypeByExtension((getFileType(filename)).toLowerCase());
		  resp.setContentType(mmtype);
		  System.out.println("************************* mmtype = "+ mmtype);
		 
		  resp.setContentLength(size);

		 out = resp.getOutputStream();

		 int fsize = retrieve(userid, out, conn);
		 System.out.println("Retrieve file CV for user = "+userid +", Size = " + fsize);
		 
		 out.close();
	 }



	 protected void readFile(InputStream in, OutputStream out)
	 throws IOException
	 {
		 byte[] buffer = new byte[2 * 1024 * 1024];
		 int totalRead = 0;
		 int bytesRead = 0;
		 while ((bytesRead = in.read(buffer)) != -1)
		 {
			 out.write(buffer, 0, bytesRead);
			 if (bytesRead > 0)
			 {
				 totalRead += bytesRead;
			 }
		 }
	 }


	  //
	 // retrieve
	 //
	 private int retrieve(String userid, OutputStream out, Connection conn)
	 {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
	   
	   int size = 0;
	   try
		{
		  pstmt = conn.prepareStatement("SELECT USER_CV FROM ETS.ETS_USER_INFO WHERE USER_ID = ? for READ ONLY");
	   pstmt.setString(1, userid);

	  rset = pstmt.executeQuery();

	  if (rset.next())
		  {
		  Blob blob = rset.getBlob(1);
		  size = (int)blob.length();

		  InputStream in = blob.getBinaryStream();
		  size = readData(in, out);
		  System.out.println("etscvdoc size ="+size);
		  in.close();
		  }
	  rset.close();
	  pstmt.close();
		}
	   catch (java.sql.SQLException ex) {
	   System.out.println("sql error = "+ex);
	   }
	   catch (IOException ex1) {
	   System.out.println("io error = "+ex1);
	   }
	   catch (Exception ex2) {
	   System.out.println("error = "+ex2);
	   }
	   return size;
	 }


	 //
	 // readData
	 //
	 protected int readData(InputStream in, OutputStream out)
		 throws IOException
	 {
		 byte[] buffer = new byte[1024 * 1024];
		 int totalRead = 0;
		 int bytesRead = 0;
		 while ((bytesRead = in.read(buffer)) != -1)
		 {
			 out.write(buffer, 0, bytesRead);
			 if (bytesRead > 0)
			 {
				 totalRead += bytesRead;
			 }
		 }
		 return totalRead;
	 }


	private String getFileType(String filename) {
		String filetype = "";
		
		int index = filename.lastIndexOf(".");
		filetype = (filename.substring(index + 1, filename.length())).toLowerCase();
		return filetype;
	}

}







