package oem.edge.ed.odc.dropbox.service.helper;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.Iterator;
import java.util.Vector;

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
* This abstract class provides a common base for the Upload and Download transfer
*  objects. This allows cancel/abort of transfer by all operation types
*/
public abstract class DataTransfer {

   protected Vector            listeners = new Vector();
   protected boolean           doOperation = true;
   

   public void abortTransfer() {
      doOperation = false;
   }
   
  /**
   * Allows external entity to receive an ActionEvent whenever data is transferred.
   * The event id will be the length of the completed transfer and the name will be
   * "update"
   */
   public void addProgressListener(ActionListener list) { 
      listeners.add(list);
   }
   
  /**
   * Allows an asynchronous event to be dispatched to registered listeners
   */
   protected void sendEvent(ActionEvent ev) {
     // If there are any registered listeners, call them now
      Iterator it = listeners.iterator();
      while(it.hasNext()) {
         ((ActionListener)it.next()).actionPerformed(ev);
      }
   }
}
