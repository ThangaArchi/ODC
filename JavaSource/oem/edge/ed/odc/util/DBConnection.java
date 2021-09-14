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

public abstract class DBConnection implements SharedConnectionContainer {
   
   static protected Logger log = Logger.getLogger(DBConnection.class);

   protected ThreadLocal autothreadlocal   = new ThreadLocal();
   protected ThreadLocal committhreadlocal = new ThreadLocal();
   
   protected static final int HOLD_CURSORS_OVER_COMMIT = 1;
//   protected static final int HOLD_CURSORS_OVER_COMMIT = ResultSet.HOLD_CURSORS_OVER_COMMIT;
   
   protected String             dbDriver = "COM.ibm.db2.jdbc.app.DB2Driver";
   protected String                dbURL = "jdbc:db2:dropbox";
   protected String              dbPWDir = "";
   protected String           dbInstance = null;
   protected String               dbUser = null;
   protected String                 dbPW = null;
   
   public String getInstance()         { return dbInstance; }
   public void   setInstance(String s) { dbInstance = s;    }
   
   public String getDriver()           { return dbDriver;   }
   public void   setDriver(String s)   { dbDriver = s;      }
   
   public String getURL()              { return dbURL;      }
   public void   setURL(String s)      { dbURL = s;         }
   
   public String getPasswordDir()         { return dbPWDir; }
   public void   setPasswordDir(String s) { dbPWDir = s;    }
   
   public  String getPassword()         { return dbPW; }
   public  void   setPassword(String s) { dbPW = s;    }
   public  String getUserid()           { return dbUser; }
   public  void   setUserid(String s)   { dbUser = s;    }
   
  /* GREAT idea having both autocommit on and off BUT ...
     does not really work if routines which want it ON
     call routines that want it OFF ... they are now using
     different connections, and are not in the same locking
     program.  Enfore ALL uses to be autocommit = false
  */
     
   public SharedConnection getSharedConnection() throws java.sql.SQLException {
      return getSharedConnection(false);
   }
  
  /**
   * This allows a caller to get the current proxy in play and then null it out so a new
   *  connection is created upon next call to getSharedConnection (essentially push it).
   *  At the end of the callers reign, he should call restoreSharedConnection and provide
   *  the previously obtained SharedConnectionProxy (pop operation). Doing this push
   *  operation will allow the caller to create a new transactional shared connection which
   *  is isolated from the locks/commit hiearchy of any current transaction. This allows
   *  localized commits, which we NEED to do package/file reclaimation on the fly.
   */
   public SharedConnectionProxy saveSharedConnection() {
      SharedConnectionProxy proxy = null;
      ThreadLocal tl = committhreadlocal;
      
      proxy = (SharedConnectionProxy)tl.get();
      if (proxy != null) {
         tl.set(null);
      }
      
      return proxy;
   }
   
   public void restoreSharedConnection(SharedConnectionProxy inp) {
      SharedConnectionProxy proxy = null;
      ThreadLocal tl = committhreadlocal;
      
     /* This is the most correct, instead, we just trust the caller popped it already
      proxy = (SharedConnectionProxy)tl.get();
      if (proxy != null) {
         while(!proxy.popFrame());
      }
     */
      if (inp != null) {
         tl.set(inp);
      }
   }
  
  // These connections can be used for autocommit ONLY!
   public Connection getConnection() throws java.sql.SQLException {
      return getSharedConnection(true);
   }
  
   protected SharedConnection getSharedConnection(boolean autocommit) 
      throws java.sql.SQLException {
      
      SharedConnectionProxy proxy = null;
      ThreadLocal tl;
      if (autocommit) tl = autothreadlocal;
      else            tl = committhreadlocal;
      
      proxy = (SharedConnectionProxy)tl.get();
      
     // If we have one in play, just return it
      if (proxy != null) {
         proxy.newFrame();
         if (log.isDebugEnabled()) {
            log.debug("got Shared Connection for autocommit = " + 
                      autocommit + " dbconn=:" + this.toString() + 
                      " threadlocal = " + tl.toString());
         }
         return proxy.makeProxy();
      }
      
      if (log.isDebugEnabled()) {
         log.debug("make NEW Connection for autocommit = " + 
                      autocommit + " dbconn=:" + this.toString() +
                      " threadlocal = " + tl.toString());
      }
      return null;
   }
  
   public void returnSharedConnection(SharedConnection conn) {
      if (conn != null) {
         if (Proxy.isProxyClass(conn.getClass())) {
            SharedConnectionProxy proxy = 
               (SharedConnectionProxy)Proxy.getInvocationHandler(conn);
               
            try {
              // Do a rollback for non autocommit connection upon return. 
              //  If it was committed, hopefully a NOOP
               if (!conn.getAutoCommit()) proxy.rollback(false);
            } catch(SQLException e) {
               log.warn("Error in returnSharedConnection while rolling back!", e);
            }
            
            try {
               boolean auto = proxy.getAutoCommit();
               ThreadLocal tl;
               if (auto) tl = autothreadlocal;
               else      tl = committhreadlocal;
               
               if (proxy.popFrame()) {
               
                  tl.set(null);
                  
                  if (log.isDebugEnabled()) {
                     log.debug("Return Connection TOPLEVEL autocommit=" + 
                               proxy.getAutoCommit() + 
                               " dbconn=:" + this.toString() + 
                               " threadlocal = " + tl.toString()); 
                  }
                  
                  returnConnection(proxy.getConnection());
               } else {
                  if (log.isDebugEnabled()) {
                     log.debug("Return Connection MORE FRAMES autocommit=" + 
                               proxy.getAutoCommit() + 
                               " dbconn=:" + this.toString() + 
                               " threadlocal = " + tl.toString()); 
                  }
               }
            } catch(SQLException e) {
               log.warn("Error in returnConnection while popping frame!", e);
            }
         }
      }
   }
  
   public void destroySharedConnection(SharedConnection conn) {
      if (conn != null) {
         if (Proxy.isProxyClass(conn.getClass())) {
            SharedConnectionProxy proxy = 
               (SharedConnectionProxy)Proxy.getInvocationHandler(conn);
            
            try {
            
               boolean auto = proxy.getAutoCommit();
               ThreadLocal tl;
               if (auto) tl = autothreadlocal;
               else      tl = committhreadlocal;
               
               if (proxy.popFrame()) {
               
                  tl.set(null);
                  
                  if (log.isDebugEnabled()) {
                     log.debug("DESTROY Connection TOPLEVEL autocommit=" + 
                               proxy.getAutoCommit() + 
                               " dbconn=:" + this.toString() + 
                               " threadlocal = " + tl.toString()); 
                  }
                  
                  destroyConnection(proxy.getConnection());
               } else {
                  if (log.isDebugEnabled()) {
                     log.debug("DESTROY Connection MORE FRAMES autocommit=" + 
                               proxy.getAutoCommit() + 
                               " dbconn=:" + this.toString() + 
                               " threadlocal = " + tl.toString()); 
                  }
               }
            } catch(SQLException e) {
               log.warn("Error in destroyConnection while popping frame!", e);
            }
         }
      }
   }
  
   public abstract void returnConnection(Connection conn);
  
   public abstract void destroyConnection(Connection conn);
   
   public ResultSet executeQuery(PreparedStatement stmt) 
      throws SQLException {
      return (ResultSet)execute(stmt, 1);
   }
   public int       executeUpdate(PreparedStatement stmt)
      throws SQLException {
      return ((Integer)execute(stmt, 2)).intValue();
   }
   private Object execute(PreparedStatement stmt, 
                                 int type) throws SQLException {
      int trynum=0;
      SQLException lastex = null;
      
      if (log.isDebugEnabled()) {
         log.debug(stmt.toString());
      }
      
      while(trynum < 5) {
         try {
            if (type == 1) {
               return stmt.executeQuery();
            } else {
               return new Integer(stmt.executeUpdate());
            }
         } catch(SQLException e) {
            int code     = e.getErrorCode();
           // String state = e.getSQLState();
            
            switch(code) {
               case -904:
                  log.warn("DB2 datalocked - Trying again");
                  trynum++;
                  lastex = e;
                  continue;
               case -911:
                  log.warn("DB2 Deadlock - Rollback Done");
                  break;
               case -913:
                  log.warn("DB2 Deadlock - No Rollback Done");
                  break;
               default:
                  break;
            }
            
           // If its NOT a complaint about constraint, log it
// Don't complain at all here ...            
//            if (e.getSQLState().equals("23505")) {
//               log.warn(e);
//            }
            
            throw e;
         }
      }
      throw lastex;
   }
}
