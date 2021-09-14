package oem.edge.ed.odc.dropbox.service.helper;

import java.util.EventObject;

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
 * Supplied to the OperationListener class to describe an asynchronous event and its
 *  source operation
 */
public class OperationEvent extends EventObject {
   
  /**
   * type value for a Data transferred event
   */
   static public int DATA = 0;
   
  /**
   * type value for a an operation ended event
   */
   static public int ENDED = 1;
   
  /**
   * type value for a MD5 calculation event
   */
   static public int MD5 = 2;
   
   public int eventType;

  /**
   * Creates an operation event of the specified type and source
   */
   public OperationEvent(int type, Operation source) {
      super(source);
      this.eventType = type;
   }

  /**
   * Returns true if the event instance is of the ENDED type
   */
   public boolean isEnded() {
      return eventType == ENDED;
   }
   
  /**
   * Returns true if the event instance is of the DATA type
   */
   public boolean isData() {
      return eventType == DATA;
   }
   
  /**
   * Returns true if the event instance is of the MD5 type
   */
   public boolean isMD5() {
      return eventType == MD5;
   }
	
  /**
   * Returns the event type for this event instance
   */
   public int getType() {
      return eventType;
   }
        
  /**
   * Returns the source object for this event
   */
   public Object getSource() {
      return source;
   }
   
  /**
   * Returns the source object for this event, cast to an Operation
   */
   public Operation getOperation() {
      return (Operation)source;
   }
}
