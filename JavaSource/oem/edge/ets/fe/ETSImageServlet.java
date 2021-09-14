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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.DbConnect;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.ETSDBUtils;
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

/**
 * @version 	1.0
 * @author
 */
public class ETSImageServlet extends HttpServlet {

  public final static String Copyright = "(C) Copyright IBM Corp.  2002, 2003";
 
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("html/image");
		//PrintWriter out = response.getWriter();

		DbConnect db = null;
		
		try {

			EdgeAccessCntrl es = new EdgeAccessCntrl();
			if (!es.GetProfile(response, request)) {
				SysLog.log(SysLog.DEBUG, this, "Authentication Process Failed");
				return;
			}

			if (!Global.loaded) {
				Global.Init();
			}

			db = new DbConnect();
			db.makeConn();

			getImage(db.conn,request,response); 

		
		} catch (SQLException e) {
			SysLog.log(SysLog.ERR, this, e);
		} catch (Exception e) {
			SysLog.log(SysLog.ERR, this, e);
		} finally {
			ETSDBUtils.close(db.conn);
			db = null;
		}		
	}
	



	
	/**
	* @see javax.servlet.GenericServlet#void ()
	*/
	public void init() throws ServletException {

		super.init();

	}

	private static void getImage(Connection con, HttpServletRequest request, HttpServletResponse response) throws SQLException, Exception {

		PreparedStatement pstmt = null;
		StringBuffer sQuery = new StringBuffer();
		ResultSet rs = null;
		
		try {

			sQuery.append("SELECT IMAGE FROM ETS.ETS_PROJECT_INFO WHERE ");
			sQuery.append("PROJECT_ID = ? AND ");
			sQuery.append("INFO_MODULE = ? FOR READ ONLY");
		
			pstmt = con.prepareStatement(sQuery.toString());
			
			pstmt.setString(1,request.getParameter("proj"));
			pstmt.setInt(2,Integer.parseInt(request.getParameter("mod")));
			
			rs = pstmt.executeQuery();

			//ByteArrayOutputStream out1 = new ByteArrayOutputStream();

			if (rs.next()) {

				InputStream input = rs.getBinaryStream(1);
				byte buf[] = new byte[512];
				int n = 0;
				int total = 0;
				while ((n = input.read(buf)) > 0) {
					total += n;
					response.getOutputStream().write(buf, 0, n);
					//out1.write(buf, 0, n);
					//out1.flush();
				}
				input.close();
			}
			
			//out1.close();
					
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ETSDBUtils.close(pstmt);
		}
	
	}
	
}
