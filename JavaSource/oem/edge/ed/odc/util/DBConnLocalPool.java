package oem.edge.ed.odc.util;

import java.lang.*;
import java.sql.*;
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

public class DBConnLocalPool extends DBConnection {

   static Logger log = Logger.getLogger(DBConnLocalPool.class);
   
//   private Connection persistentConnection = null;
   private Vector persistentConnectionPool    = new Vector();
   private Vector persistentConnectionPoolAge = new Vector();
   public  int             maxPoolSize = 3;
   
   private static final long MAXAGE_DELTA_MS = 60000;
   private boolean        complainOnce = false;
   
   public SharedConnection getSharedConnection(boolean autocommit) 
      throws java.sql.SQLException {
   
     // If there is a shared proxy avail (super class has it) ret ot
      SharedConnection ret = super.getSharedConnection(autocommit);
      if (ret != null) return ret;
   
      Connection conn = null;
      log.debug("DbConnect: makeConn: Enter");
      synchronized(persistentConnectionPool) {
      
         destroyExpiredConnections();
         
        // If any persistent connections left, take it
         if (persistentConnectionPool.size() > 0) {
            conn = (Connection)persistentConnectionPool.lastElement();
                
            int sz = persistentConnectionPool.size();
            persistentConnectionPool.removeElementAt(--sz);
            persistentConnectionPoolAge.removeElementAt(sz);
            
            log.debug("DBConnect: makeConn: Returning persistent connection");
         }
            
        // If no connection yet, make a new one
         if (conn == null) {
         
            String dbUserid   = dbUser;
            String dbPassword = dbPW;
            
            if (dbUserid == null) {
               dbUserid   = PasswordUtils.getPassword(dbPWDir+"/._afsd435");
            }
            if (dbPassword == null) {
               dbPassword = PasswordUtils.getPassword(dbPWDir+"/._afsde7e");
            }
            
            try {
               Class.forName(dbDriver).newInstance();
            } catch(Exception ee) {
               if (!complainOnce) {
                  log.warn("Error loading SQL driver " + dbDriver);
                  log.warn(ee);
                  complainOnce = true;
               }
            }
            
            if (log.isDebugEnabled()) {
               log.debug("DBURL [" + dbURL + "] user = " + dbUserid + 
                         "\nDBDriver [" + dbDriver + "]");
            }
            
            try {
               if (dbUserid != null && dbPassword != null) {
                  conn = DriverManager.getConnection(dbURL, dbUserid, 
                                                     dbPassword);
                  log.debug("DBConnect: makeConn: Returning new connection");
               } else {
                  throw new SQLException("userid and/or password NULL");
               }
            } finally {
               dbUserid   = null;
               dbPassword = null;
            }
         }            
      }
      
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
  
   public void destroyExpiredConnections() {
      synchronized(persistentConnectionPool) {
        // Timeout and check each persistent connection
         int nc = persistentConnectionPool.size();
         long curtime = System.currentTimeMillis();
         while(nc > 0) {
            Connection persistentConnection = 
               (Connection)persistentConnectionPool.elementAt(--nc);
            Long age = (Long)persistentConnectionPoolAge.elementAt(nc);
            
            long delttime = curtime - age.longValue();
            boolean closeit = false;
            if (delttime < 0 || delttime > MAXAGE_DELTA_MS) {
               closeit = true;
               log.debug("DBConnect: destroyExpiredConnections: Persistent Connection is past maxage. Remove: " + (delttime/1000) + " seconds");
            }               
            
            if (!closeit) {
               try {
                  closeit = persistentConnection.isClosed();
               } catch(SQLException sqle) {
                  closeit = true;
               }
               
               if (closeit) {
                  log.debug("DBConnect: destroyExpiredConnections: Persistent Connection is marked Closed");
               }
            }
            
            if (closeit) {
               persistentConnectionPool.removeElementAt(nc);
               persistentConnectionPoolAge.removeElementAt(nc);
               destroyConnection(persistentConnection);
            }         
         }
      }
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
      
         log.debug("DbConnect: returnConn: Enter");
         synchronized(persistentConnectionPool) {
            destroyExpiredConnections();
            int poolsz = persistentConnectionPool.size();
            if (poolsz < maxPoolSize) {
               Long age = new Long(System.currentTimeMillis()); 
               persistentConnectionPool.addElement(conn);
               persistentConnectionPoolAge.addElement(age);
            } else {
               log.debug("DBConnect: returnConn: PersistentConnections already filled");
               destroyConnection(conn);
            }
         }
      }
   }
  
   public void destroyConnection(Connection conn) {
      if (conn != null) {
      
        // if this is a SharedConnection, call the parent, who will call here eventually
         if (conn instanceof SharedConnection) {
            destroySharedConnection((SharedConnection)conn);
            return;
         }
      
         log.debug("DbConnect: destroyConn: Enter");
         try { conn.close(); } catch(Throwable tt) {}
      }
   }
}
