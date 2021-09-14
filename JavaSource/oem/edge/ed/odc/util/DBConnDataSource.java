package oem.edge.ed.odc.util;

import java.lang.*;
import java.sql.*;
import javax.sql.*;
import javax.naming.*;
import java.util.*;
import oem.edge.ed.util.*;
import oem.edge.ed.odc.util.db.*;
import oem.edge.ed.odc.tunnel.common.*;

import org.apache.log4j.Logger;

import java.lang.reflect.*;

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

public class DBConnDataSource extends DBConnection {

   static Logger log = Logger.getLogger(DBConnDataSource.class);

   private javax.sql.DataSource datasource = null;
   
   private boolean failedLookup = false;
      
   public DBConnDataSource() {
      dbURL = "java:comp/env/jdbc/edodc";  // Set the default 
   }
   
   public SharedConnection getSharedConnection(boolean autocommit) 
      throws java.sql.SQLException {
      
     // If there is a shared proxy avail (super class has it) ret ot
      SharedConnection ret = super.getSharedConnection(autocommit);
      if (ret != null) return ret;
      
      Connection conn = null;
      if (datasource == null) {
         Context ctx = null;
         try {
            Hashtable ht = new Hashtable();
            ht.put(Context.INITIAL_CONTEXT_FACTORY, 
                   "com.ibm.websphere.naming.WsnInitialContextFactory");
            ctx = new InitialContext(ht);
            datasource = (javax.sql.DataSource)ctx.lookup(dbURL);
            ctx.close();
         } catch(Throwable t) {
            SQLException tt = 
               new SQLException(t.getMessage() + 
                                ": Error creating DataSource from " + dbURL);
            tt.fillInStackTrace();
            throw tt;
         }
      }
      
     /*
      String dbUserid   = dbUser;
      String dbPassword = dbPW;
      
      if (dbPWDir != null && !failedLookup) {
         if (dbUserid == null) {
            dbUserid   = PasswordUtils.getPassword(dbPWDir+"/._afsd435");
            if (dbUserid == null) failedLookup = true;
         }
         if (dbPassword == null) {
            dbPassword = PasswordUtils.getPassword(dbPWDir+"/._afsde7e");
            if (dbPassword == null) failedLookup = true;
         }
      }
            
      if (dbUserid != null && dbPassword != null) {
         conn = datasource.getConnection(dbUserid, dbPassword);
      } else {
         conn = datasource.getConnection();
     }
      dbUserid   = null;
      dbPassword = null;
     */
      conn = datasource.getConnection();
      
      if (conn == null) {
         log.error("getConnection: conn == null!");
         return null;
      }
      
      conn.setAutoCommit(autocommit);
      
     // Set Holdability ON
     // Use reflection as this is compiled with 1.3 today
     //conn.setHoldability(HOLD_CURSORS_OVER_COMMIT);
      try {
         Class clsarr[] = {int.class};
         Object parmarr[] = {new Integer(HOLD_CURSORS_OVER_COMMIT)};
         Method m = conn.getClass().getMethod("setHoldability", clsarr);
         m.invoke(conn, parmarr);
      } catch(Exception eee) {
         log.warn("Error setting holdability on connection", eee);
      }
      
     // If we have one in play, just return it
      SharedConnectionProxy proxy = new SharedConnectionProxy(this, autocommit, conn);
      
      log.debug("create new proxy for autocommit = " + autocommit);
      
      if (autocommit) {
         autothreadlocal.set(proxy);
      } else {
         committhreadlocal.set(proxy);
      }
      
      return proxy.makeProxy();
   }
  
   public void returnConnection(Connection conn) {
      if (conn != null) {
      
        // if this is a SharedConnection, call the parent, who will call here eventually
         if (conn instanceof SharedConnection) {
            returnSharedConnection((SharedConnection)conn);
            return;
         }
         
        // Make sure we store it as AutoCommit
         try {
            if (!conn.getAutoCommit()) {
               conn.setAutoCommit(true);
            } 
         } catch(Exception e) {}
               
         try {conn.close();} catch(Exception e) {}
      }
   }
   
   public void destroyConnection(Connection conn) {
      returnConnection(conn);
   }
}
