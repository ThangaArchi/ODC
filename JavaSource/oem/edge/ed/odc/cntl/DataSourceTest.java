package oem.edge.ed.odc.cntl;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.http.*;
import java.util.*;
import java.io.*;
import javax.servlet.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
//import javax.sql.SavePoint;

import javax.sql.DataSource;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 1998,2004,2005,2006		                 */ 
/*                                                                       */ 
/*     All Rights Reserved					         */ 
/*     US Government Users Restricted Rights			         */ 
/*                                                                       */ 
/*   The source code for this program is not published or otherwise      */ 
/*   divested of its trade secrets, irrespective of what has been        */ 
/*   deposited with the US Copyright Office.                             */ 
/*   ------------------------------------------------------------------  */
/*     Please do not remove any of these commented lines  21 lines       */
/*   ------------------------------------------------------------------  */
/*                       Copyright Footer Check                          */
/*   ------------------------------------------------------------------  */ 

public class DataSourceTest extends HttpServlet {
    
    protected String selstr = "select * from edesign.frontpage";
    protected String dbURL = "java:comp/env/edodc";
    
	public void init(javax.servlet.ServletConfig config) throws ServletException {	
       super.init(config);
       
       String t = config.getInitParameter("selectstring");
       if (t != null) selstr = t;

       t = config.getInitParameter("resourceref");
       if (t != null) dbURL = t;       
    }
    
    public void doGet(javax.servlet.http.HttpServletRequest req, 
                      javax.servlet.http.HttpServletResponse res) 
      throws javax.servlet.ServletException, java.io.IOException {
      
      
      Connection c = null;
      DataSource datasource = null;

      try {
           Context ctx = null;
           Hashtable ht = new Hashtable();
           ht.put(Context.INITIAL_CONTEXT_FACTORY, 
                  "com.ibm.websphere.naming.WsnInitialContextFactory");
           ctx = new InitialContext(ht);
           datasource = (javax.sql.DataSource)ctx.lookup(dbURL);
           ctx.close();
        } catch(Throwable t) {
           IOException tt = 
              new IOException(t.getMessage() + 
                               ": Error creating DataSource from " + dbURL);
           tt.fillInStackTrace();
           throw tt;
        }       	  
        
        try {
            
          c = datasource.getConnection();
        
          PreparedStatement pstmt = c.prepareStatement(selstr);
          ResultSet rs1 = pstmt.executeQuery();
          while(rs1.next()) {
             PreparedStatement pstmt2 = c.prepareStatement(selstr);
             ResultSet rs2 = pstmt2.executeQuery();
             if (rs2.next()) System.out.println("rs2 has next");
             else            System.out.println("rs2 has NO next");
             
             pstmt2.close();
             
            // ----<  Here is the problem. Setting the savepoint, then rolling back to said save point closes rs1
//             System.out.println("Setting savepoint ... then rolling back");
//             Savepoint p2 = c.setSavepoint();
//             c.rollback(p2);             
          }  
          
          System.out.println("Test finished Successfully!");
          
       } catch(Exception ee) {
          System.out.println("Error doing test");
          ee.printStackTrace(System.out);
       } finally {
          try { c.close(); } catch(Exception uu) {}
       }
    }
}
