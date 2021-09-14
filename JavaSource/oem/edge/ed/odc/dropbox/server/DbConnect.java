package oem.edge.ed.odc.dropbox.server;

import oem.edge.ed.odc.ftp.common.*;
import oem.edge.ed.odc.dsmp.common.*;
import oem.edge.ed.util.*;
import oem.edge.ed.odc.util.*;
import oem.edge.ed.odc.util.db.SharedConnectionProxy;

import java.lang.*;
import java.sql.*;
import java.util.*;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 2001-2006                                    */ 
/*                                                                       */ 
/*     All Rights Reserved                                               */ 
/*     US Government Users Restricted Rights                             */ 
/*                                                                       */ 
/*   The source code for this program is not published or otherwise      */ 
/*   divested of its trade secrets, irrespective of what has been        */ 
/*   deposited with the US Copyright Office.                             */ 
/*   ------------------------------------------------------------------  */
/*     Please do not remove any of these commented lines  21 lines       */
/*   ------------------------------------------------------------------  */
/*                       Copyright Footer Check                          */
/*   ------------------------------------------------------------------  */ 


// Thin shell now, legacy... should use DBConnection directly

public class DbConnect {

   public static String getInstance() { 
      return DBSource.getDBConnection("dropbox").getInstance(); 
   }
   public static void   setInstance(String s) {
      DBSource.getDBConnection("dropbox").setInstance(s); 
   }
   public static String getDriver()           { 
      return DBSource.getDBConnection("dropbox").getDriver(); 
   }
   public static void   setDriver(String s) {
      DBSource.getDBConnection("dropbox").setDriver(s);
   }
   public static String getURL()           { 
      return DBSource.getDBConnection("dropbox").getURL(); 
   }
   public static void   setURL(String s) {
      DBSource.getDBConnection("dropbox").setURL(s);
   }
   public static String getPasswordDir()           { 
      return DBSource.getDBConnection("dropbox").getPasswordDir(); 
   }
   public static void   setPasswordDir(String s) {
      DBSource.getDBConnection("dropbox").setPasswordDir(s);
   }
   
   static public Connection makeConn() throws java.sql.SQLException {
      return DBSource.getDBConnection("dropbox").getConnection();
   }
  
   static public SharedConnection makeSharedConn() 
      throws java.sql.SQLException {
      return DBSource.getDBConnection("dropbox").getSharedConnection();
   }
  
   static public SharedConnectionProxy saveSharedConn() {
      return DBSource.getDBConnection("dropbox").saveSharedConnection();
   }
   static public void restoreSharedConn(SharedConnectionProxy c) {
      DBSource.getDBConnection("dropbox").restoreSharedConnection(c);
   }
  
   static public void returnConnection(SharedConnection conn) {
      DBSource.getDBConnection("dropbox").returnConnection(conn);
   }
  
   static public void destroyConnection(SharedConnection conn) {
      DBSource.getDBConnection("dropbox").destroyConnection(conn);
   }
   
   static public void returnConnection(Connection conn) {
      DBSource.getDBConnection("dropbox").returnConnection(conn);
   }
  
   static public void destroyConnection(Connection conn) {
      DBSource.getDBConnection("dropbox").destroyConnection(conn);
   }
   
   static public ResultSet executeQuery(PreparedStatement stmt) 
      throws SQLException {
      return DBSource.getDBConnection("dropbox").executeQuery(stmt);
   }
   static public int       executeUpdate(PreparedStatement stmt)
      throws SQLException {
      return DBSource.getDBConnection("dropbox").executeUpdate(stmt);
   }
}
