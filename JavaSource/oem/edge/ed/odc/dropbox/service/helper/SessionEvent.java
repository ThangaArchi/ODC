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
 * Supplied to the ServiceListener class to describe an asynchronous event and its
 *  source operation
 */
public class SessionEvent extends EventObject {
   
  /**
   * type value for a successful sessionid REFRESH event
   */
   static public int REFRESH = 0;
   
  /**
   * type value for a failed sessionid refresh event. Exception may accompany
   */
   static public int REFRESHERROR = 1;
   
  /**
   * type value for an INACTIVE session event. When event processing
   *  returns from this type of event, a cleanup is performed, so 
   *  a SHUTDOWN event will follow
   */
   static public int INACTIVITY = 2;
   
  /**
   * type value for a SHUTDOWN event. When this event is sent, the session
   *  close has already been done/attempted.
   */
   static public int SHUTDOWN = 3;
   
  /**
   * Type for the event instance. This is public for legacy reasons.
   * @deprecated Accessing this field directly should be avoided. 
   */
   public int eventType;
   
   protected Throwable throwable;

  /**
   * Creates an operation event of the specified type and source
   */
   public SessionEvent(int type, SessionHelper source) {
      super(source);
      this.eventType = type;
   }

  /**
   * Creates an operation event of the specified type, source and throwable
   */
   public SessionEvent(int type, SessionHelper source, Throwable t) {
      super(source);
      this.eventType = type;
      this.throwable = t;
   }
   
  /**
   * Returns true if the event instance is of the REFRESH type
   */
   public boolean isRefresh() {
      return eventType == REFRESH;
   }
   
  /**
   * Returns true if the event instance is of the REFRESHERROR type
   */
   public boolean isRefreshError() {
      return eventType == REFRESHERROR;
   }
   
  /**
   * Returns true if the event instance is of the INACTIVITY type
   */
   public boolean isInactivity() {
      return eventType == INACTIVITY;
   }
   
  /**
   * Returns true if the event instance is of the SHUTDOWN type
   */
   public boolean isShutdown() {
      return eventType == SHUTDOWN;
   }
	
  /**
   * Returns the event type for this event instance
   */
   public int getEventType() {
      return eventType;
   }
        
  /**
   * Returns any Throwable that is associated with the event
   */
   public Throwable getCause() {
      return throwable;
   }
   
  /**
   * Returns the source object for this event
   */
   public Object getSource() {
      return source;
   }
   
  /**
   * Returns the source object for this event, cast to a SessionHelper
   */
   public SessionHelper getSessionHelper() {
      return (SessionHelper)source;
   }
}
