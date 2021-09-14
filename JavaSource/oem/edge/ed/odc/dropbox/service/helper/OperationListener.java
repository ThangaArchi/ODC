package oem.edge.ed.odc.dropbox.service.helper;

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

import java.util.*;
/**
 * Interface definition for all registered Operation event listeners. 
 *  Applications should implement this Interface and itself to the Operation objects
 *  in question to receive asynchronous notification of events occuring on said
 *  Operation.
 */
public interface OperationListener extends EventListener {

/**
 * Listener's method called when an operation event is generated. Note, processing
 *  should be kept to an absolute minimum as the callback occurs on the Operation's
 *  processing thread.
 */
   void operationUpdate(OperationEvent e);
}
