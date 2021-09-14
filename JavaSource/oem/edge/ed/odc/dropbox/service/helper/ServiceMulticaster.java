package oem.edge.ed.odc.dropbox.service.helper;

import java.util.*;
        
/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 2006                                         */ 
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
        
// Listener linking/management
class ServiceMulticaster implements OperationListener, SessionListener {
   
  // Static Worker routine
   public static EventListener removeInternal(EventListener l, 
                                              EventListener oldl) {
      if (l == oldl || l == null) {
         return null;
      } else if (l instanceof ServiceMulticaster) {
         return ((ServiceMulticaster)l).remove(oldl);
      } else {
         return l;		// it's not here
      }
   }
   
  // Static Worker routine
   public static EventListener addInternal(EventListener a, 
                                           EventListener b) {
      if (a == null)  return b;
      if (b == null)  return a;
      return new ServiceMulticaster(a, b);
   }
   
   public EventListener remove(EventListener oldl) {
      if (oldl == a)  return b;
      if (oldl == b)  return a;
      EventListener a2 = removeInternal(a, oldl);
      EventListener b2 = removeInternal(b, oldl);
      if (a2 == a && b2 == b) {
         return this;	// it's not here
      }
      return addInternal(a2, b2);
   }
   
   
   private final EventListener a, b;
   
   public ServiceMulticaster(EventListener a, EventListener b) {
      this.a = a;
      this.b = b;
   }
   
   
  // -------------  Operation Event processing  --------------
   public static OperationListener addOperationListener(OperationListener a, 
                                                        OperationListener b) {
      return (OperationListener)addInternal(a, b);
   }
   public static OperationListener removeOperationListener(OperationListener l, 
                                                           OperationListener oldl) {
      return (OperationListener)removeInternal(l, oldl);
   }
   
   public void operationUpdate(OperationEvent e) {
     // we are ALWAYS full
      ((OperationListener)a).operationUpdate(e);
      ((OperationListener)b).operationUpdate(e);
   }
   
  // --------------  Session Event processing  ---------------
   public static SessionListener addSessionListener(SessionListener a, 
                                                    SessionListener b) {
      return (SessionListener)addInternal(a, b);
   }
   public static SessionListener removeSessionListener(SessionListener l, 
                                                       SessionListener oldl) {
      return (SessionListener)removeInternal(l, oldl);
   }
   public void sessionUpdate(SessionEvent e) {
     // we are ALWAYS full
      ((SessionListener)a).sessionUpdate(e);
      ((SessionListener)b).sessionUpdate(e);
   }   
}
