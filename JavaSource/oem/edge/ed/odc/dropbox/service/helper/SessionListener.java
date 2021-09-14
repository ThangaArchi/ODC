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

/**
 * Interface definition for all registered session event listeners. 
 *  Applications should implement this Interface and register itself 
 *  with the SessionHelper object from which they wish to receive asynchronous
 *  notification of events.
 */
public interface SessionListener extends EventListener {

/**
 * Listener's method called when an event is generated. Note, processing
 *  should be kept to an absolute minimum as the callback may be occuring on 
 *  a service processing thread.
 */
   public void sessionUpdate(SessionEvent e);
}
