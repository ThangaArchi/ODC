package oem.edge.ed.odc.util.db;

import java.lang.*;
import java.sql.*;
import javax.sql.*;
import javax.naming.*;
import java.util.*;
import oem.edge.ed.odc.util.SharedConnection;
import oem.edge.ed.odc.util.SharedConnectionContainer;
import oem.edge.ed.odc.util.Rollback;

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
/*   (C)Copyright IBM Corp. 2006		                         */ 
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

/**
 ** Upon initial object creation, there is a single empty SavePoint object 
 **  mantained on the savepoint stack. As sub-sequent allocations of the
 **  same proxy occur, a filled in savepoint is added to the stack. As the
 **  proxy object is 'deallocated' by the user, the call driven SavePoint
 **  object is handled.
 ** <p>
 ** A connection.SavePoint (real one) is only rolled back if its marked
 **  as being dirty. It gets dirty if any calls, statement or preparedStatements
 **  have been allocated as well as if any rollback info is registered. This 
 **  is so, since WAS Connection pooling code closes ALL open ResultSets 
 **  when a savepoint is rolled back regardless of where that RS was created.
 **  This is different thatn DB2 JDBC operates, and goes against common sense
 **  (and all docs found on the subject). Anyway, this is a work around so I 
 **  don't get Resultset Closed exceptions when dealing with nested result sets
 **  (routine which gets a result set and calls another routine which allocates
 **  a SavePoint on the same connection, and then does a rollback). 
 ** <p>
 ** You should know that the above case will still occur when when running under WAS
 **  and a real savepoint.rollback does need to occur. You should prevent the 
 **  need by avoiding nested calls and/or avoid calling rollback on a dirty savepoint
 **  unless its driving an error WAY up the chain (which your service routines will
 **  not really know. Sigh).
 */
public class SharedConnectionProxy implements InvocationHandler {

   static Logger log = Logger.getLogger(SharedConnectionProxy.class);

  // Returns true if the provide collection contains the exact object passed in 
  //  (ie. Identity  <refs are equal>).
   static boolean containsExact(Collection c, Object o) {
      Iterator it = c.iterator();
      while(it.hasNext()) {
         Object lo = it.next();
         if (o == lo) return true;
      }
      return false;
   }
   
  // Removes the exact (ie. Identity  <refs are equal>) Object from the Collection
   static boolean removeExact(Collection c, Object o) {
      boolean ret = false ;
      Iterator it = c.iterator();
      while(it.hasNext()) {
         Object lo = it.next();
         if (o == lo) {
            it.remove();
            ret = true;
         }
      }
      return ret;
   }
   
   class SavePoint {
      List statements       = null;
      Savepoint savepoint   = null;
      List      rollbacks   = null;
      boolean   callOrOther = false;
      boolean   isdirty     = false;
      
     /**
      * Create a SavePoint holder, marked as call level or other
      */
      public SavePoint(boolean call, boolean createSavepoint) throws SQLException {
         callOrOther = call;
         if (createSavepoint) {
            savepoint = conn.setSavepoint();
         }
      }
      
      public SavePoint(boolean call) throws SQLException {
         callOrOther = call;
         savepoint = conn.setSavepoint();
      }
      
     /**
      * Mark the current savepoint as dirty
      */
      public void isDirty(boolean dirty) { isdirty = dirty; }
      
     /**
      * Check if current rollback frame is dirty
      */
      public boolean isDirty() { return isdirty; }
      
     /**
      * Add a Rollback object to be called if we do a rollback. So, if the
      *  transaction is canned, even upon return, nonDB Rollback issues
      *  will be managed
      */
      public void addRollback(Rollback r) {
         if (rollbacks == null) rollbacks = new Vector();
         rollbacks.add(r);
         isDirty(true);
      }
      
      public void addRollbacks(Collection c) {
         if (rollbacks == null) rollbacks = new Vector();
         rollbacks.addAll(c);
         isDirty(true);
      }
      
      public void addStatement(Statement s) {
         if (s == null) return;
         if (statements == null) statements = new Vector();
         if (!containsExact(statements, s)) {
            statements.add(s);
            isDirty(true);
         }
      }
      
      public void removeExactStatement(Statement s) {
         if (s == null || statements == null) return;
         removeExact(statements, s);
      }
      
      public void closeStatements() {
         if (statements != null) {
            Iterator it = statements.iterator();
            while(it.hasNext()) {
               Statement s = (Statement)it.next();
               try {
                  s.close();
               } catch(Exception e) {}
            }
            statements.clear();
            statements = null;
         }
      }
      
     /**
      * Rollback the save point (if any), and execute any Rollback items 
      *  previously added to this savepoint. 
      */
      public void rollback(boolean reallocate) throws SQLException {
         if (rollbacks != null) {
            Iterator it = rollbacks.iterator();
            while(it.hasNext()) {
               Rollback roll = (Rollback)it.next();
               roll.execute();
            }
            rollbacks.clear();
         }
      
         closeStatements();
         
         if (savepoint != null) {
            if (isDirty()) {
               conn.rollback(savepoint);
               if (reallocate) {
                  savepoint = conn.setSavepoint();
               } else {
                  savepoint = null;
               }
            } else {
               release(reallocate);
            }
         }         
         isDirty(false);
      }
      
     /**
      * Releases the savepoint (if any) and return any registered Rollback items.
      *  The returned Rollback items should be added to any parent SavePoint
      */
      public List release(boolean reallocate) throws SQLException {
      
         List ret = rollbacks;
         rollbacks = null;
      
         closeStatements();
      
         if (savepoint != null) {
            conn.releaseSavepoint(savepoint);
            if (reallocate) {
               savepoint = conn.setSavepoint();
            } else {
               savepoint = null;
            }
         }
         
         isDirty(false);
         
         return ret;
      }
      
     /**
      * Commit the savepoint. This means releasing the savepoint (if any), and
      *  clearing any rollbacks. 
      */
      public void commit(boolean reallocate) throws SQLException {
         rollbacks = release(reallocate);
         if (rollbacks != null) {
            Iterator it = rollbacks.iterator();
            while(it.hasNext()) {
               Rollback roll = (Rollback)it.next();
               roll.clear();
            }
            rollbacks.clear();
         }         
         isDirty(false);
      }
   }

   Connection conn;
   boolean autocommit = false;
   Vector savePoints  = new Vector();
   SharedConnectionContainer allocatingContainer;
   
   public boolean    getAutoCommit() { return autocommit; }
   public Connection getConnection() { return conn;       }
   
   public SharedConnectionProxy(SharedConnectionContainer container,
                                boolean autocommit,
                                Connection c) throws SQLException {
                                
      conn = c;
      allocatingContainer = container;
      this.autocommit = autocommit;
      
     // Create the toplevel CALL based SavePoint with no connection savepoint attached
      savePoints.add(new SavePoint(true, false));
   }
   
   public void newFrame() throws SQLException {
      savePoints.add(new SavePoint(true, !autocommit));
   }
   
   public boolean popFrame() throws SQLException {
      int sz = savePoints.size();
      if (sz > 0) {
         SavePoint sp = (SavePoint)savePoints.remove(sz-1);
         
         sp.release(false);
      }
      return sz <= 1;
   }
   
  /**   
   * Creates a SharedConnection proxy object for the connection
   */
   public SharedConnection makeProxy() {
      ClassLoader cl = SharedConnection.class.getClassLoader();
      Class arr[] = new Class[] {SharedConnection.class};
      return (SharedConnection)Proxy.newProxyInstance(cl, arr, this);
   }
   
  /**
   * Add the Statement to current frame. When frame is popped, statement will be closed
   */
   public void addStatement(Statement s) {
   
      int sz = savePoints.size();
      
      if (sz > 0) {
         ((SavePoint)savePoints.elementAt(sz-1)).addStatement(s);
      } else {
        // no place to save it. Just close it now???
         log.error("Statement being added to current frame, and there are NO frames. Close statement");
         try { s.close(); } catch(Exception e) {}
      }
   }
   
  /**
   * Add the Statement to parent frame. When frame is popped, statement will be closed
   */
   public void addStatementToParentFrame(Statement s) {
   
      int sz = savePoints.size();
      if (sz > 1) {
         removeStatement(s);   // Remove the statement from current frame
         sz--;
      } else if (sz == 1) {
         log.error("Statement being added to parent frame, and there is NO parent. Add to current");
      }
      
      if (sz > 0) {
         ((SavePoint)savePoints.elementAt(sz-1)).addStatement(s);
      } else {
        // no place to save it. Just close it now???
         log.error("Statement being added to parent frame, and there are NO frames. Close statement");
         try { s.close(); } catch(Exception e) {}
      }
   }
   
   public void removeStatement(Statement s) {
      int sz = savePoints.size();
      if (sz > 0) {
         ((SavePoint)savePoints.elementAt(sz-1)).removeExactStatement(s);
      } else if (sz == 1) {
         log.error("Can't remove statement ... no current frame");
      }
   }
   
   
  /**
   * Add the Rollback to the current savepoint to be invoked if rollback is called
   */
   public void addRollback(Rollback r) throws SQLException {
   
      if (autocommit) {
         throw new SQLException("Autocommit is TRUE. Rollbacks are not supported");
      }
      
      ((SavePoint)savePoints.lastElement()).addRollback(r);
   }
   
  /**
   *  Commit the changes for the current save point. If its the root savepoint, then
   *   ALL Rollbacks are discarded upon successful connection commit. If root, and 
   *   commit failed, then rollback.
   *<p>
   *  If not root, then simply release the savepoint and reallocate. Any Rollbacks
   *   are added to parent
   */
   public void commit(boolean reallocate) throws SQLException {
   
      if (autocommit) {
         throw new SQLException("Autocommit is TRUE. calling commit redundent");
      }
      
      int spsize = savePoints.size();
      if (spsize > 0) {
      
         SavePoint sp = (SavePoint)savePoints.elementAt(spsize-1);
         
         if (spsize == 1) {
            try {
               log.debug("COMMIT called on toplevel");
               conn.commit();
               sp.commit(reallocate);
            } catch(SQLException se) {
               try { 
                  log.warn("SQLException while commit ... rolling back");
                  log.warn(se);
              
                  sp.rollback(reallocate);
               } catch(SQLException sqe) {
                  log.warn("SQLException while commit->rollback");
                  log.warn(sqe);
               }
               throw se;
            }
         } else {
            log.debug("COMMIT called on FRAME depth = " + spsize);
            SavePoint spp = (SavePoint)savePoints.elementAt(spsize-2);
            List l = sp.release(reallocate);
            if (l != null) spp.addRollbacks(l);
         }
      } else {
         log.warn("\n\nCommit called and NO savepoints!!\n\n");
      }
   }
   
  /**
   * Run the Rollback list on current savepoint (if any), and rollback the 
   * current savepoint (if any).
   */
   public void rollback(boolean reallocate) throws SQLException {
   
      if (autocommit) {
         throw new SQLException("Autocommit is TRUE. Calling rollback disallowed");
      }
      
      int spsize = savePoints.size();
      if (spsize > 0) {
      
         SavePoint sp = (SavePoint)savePoints.elementAt(spsize-1);
         
         sp.rollback(reallocate);      
         if (spsize == 1) {
            log.debug("Rollback called on TOPLEVEL");
            conn.rollback();  // If root, really do a rollback
         } else {
            log.debug("Rollback called on FRAME depth = " + spsize);
         }
      } else {
         log.warn("\n\nRollback called and NO savepoints!!\n\n");
      } 
   }
   
  /**
   * Mark the current savepoint as dirty
   */
   public void isDirty(boolean dirty) {
      int spsize = savePoints.size();
      if (spsize > 0) {
         SavePoint sp = (SavePoint)savePoints.elementAt(spsize-1);
         sp.isDirty(dirty);
      } 
   }
   
  /**
   * Check if current rollback frame is dirty
   */
   public boolean isDirty() {
      int spsize = savePoints.size();
      if (spsize > 0) {
         SavePoint sp = (SavePoint)savePoints.elementAt(spsize-1);
         return sp.isDirty();
      } 
      return false;
   }
   
   public Object invoke(Object proxy, 
                        Method method, 
                        Object []args) throws Throwable {
      
     String methodName = method.getName();
     Class []params    = method.getParameterTypes();
     
     boolean doAddStatement = false;
     
    // Do any overrides
     if (methodName.equals("setAutoCommit")) {
        
       // we don't allow the autoCommit value to be changed from the initial val
     
        if (((Boolean)args[0]).booleanValue() != autocommit) {
           throw new Exception("SharedConnection: setAutoCommit called changing value");
        }
        
       // Fall thru and allow call to complete (why not)
       
     } else if (methodName.equals("addRollback")) {
     
        addRollback((Rollback)args[0]);
        return null;
        
     } else if (methodName.equals("commit")) {
     
        commit(true);
        return null;
        
     } else if (methodName.equals("close")) {
     
       // We always tell our allocating container to returnSharedConnection
       //  and IT may then call close on the underlying connection.
        if (allocatingContainer != null) {
           allocatingContainer.returnSharedConnection((SharedConnection)this);
        } else {
           throw new SQLException("SharedConnection.close() w/o SharedContainer set!");
        }
        
        return null;
        
     } else if (methodName.equals("addStatement")) {
     
        addStatement((Statement)args[0]);
        return null;
        
     } else if (methodName.equals("addStatementToParentFrame")) {
     
        addStatementToParentFrame((Statement)args[0]);
        return null;
        
     } else if (methodName.equals("rollback")) {
     
        rollback(true);
        return null;
        
     } else if (methodName.equals("createStatement")) {
        isDirty(); // Set that current save point is dirty
        doAddStatement = true;
     } else if (methodName.equals("prepareCall")) {
        isDirty(); // Set that current save point is dirty
        doAddStatement = true;
     } else if (methodName.equals("prepareStatement")) {
        isDirty(); // Set that current save point is dirty
     
       // If we are enabled for debug logging, then create debug proxy
        if (log.isDebugEnabled()) {
       
           Method objmeth = Connection.class.getMethod(methodName, params);
           try { 
              Object ret = objmeth.invoke(conn, args);
              if (ret != null) {
                 PreparedStatementProxy pproxy = 
                    new PreparedStatementProxy((PreparedStatement)ret, (String)args[0]);
                 PreparedStatement ps = pproxy.makeProxy();
                 addStatement(ps);
                 return ps;
              }
           } catch(InvocationTargetException ite) {
              throw ite.getTargetException();
           }
        }
        doAddStatement = true;
     }        
     
     try {
        Method objmeth = Connection.class.getMethod(methodName, params);
        Object retobj = objmeth.invoke(conn, args);
        if (doAddStatement) {
           addStatement((Statement)retobj);
        }
        return retobj;
     } catch(InvocationTargetException ite) {
        throw ite.getTargetException();
     }
  }      
}
