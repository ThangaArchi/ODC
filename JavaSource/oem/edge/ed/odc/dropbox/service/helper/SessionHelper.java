package oem.edge.ed.odc.dropbox.service.helper;

import oem.edge.ed.odc.util.ProxyDebugInterface;
import oem.edge.ed.odc.dropbox.service.DropboxAccess;
import oem.edge.ed.odc.dsmp.common.DboxException;
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
 * This helper class handles registering and refreshing the sessionid information 
 *  for a dropbox proxy obtained via a ConnectionFactory. Once created, it can fully
 *  manage the refreshing of the tokens for a session. The helper can also be 
 *  configured to automatically terminate the refresh process and close the session
 *  upon the sustained absence of meaningful activity (the proxy is idle).
 * <p>
 * An application can register as a ServiceListener with a SessionHelper instance
 *  to receive asynchronous notification of events as they occur, and to take
 *  corrective action as necessary.  All events generated will be of the type
 *  SessionEvent, and will have an eventType
 * <pre>
 *    REFRESH       - Session refresh successful
 *    REFRESHERROR  - Session refresh failed. Will continue trying
 *    INACTIVITY    - Session will be shutdown due to inactivity
 *    SHUTDOWN      - Session has been shutdown
 * </pre>
 */
public class SessionHelper {
   
   DropboxAccess dropbox;
   Timer sessiontimer     = new Timer(true);
   TimerTask sessiontask  = null;
   
   boolean done           = false;
   boolean quiet          = true;
   
   boolean autoclose      = false;
   TimerTask autotask     = null;
   long autoCloseDelay    = 10*60*1000;   // Default of 10 minutes
   long housekeepingCount = 0;
   
   SessionListener listeners = null;
         
   
  /**
   * Initializes the helper class with the DropboxAccess proxy and initial
   *  map containing SessionID and Expiration (typically obtained via the
   *  createSession method).
   */
   public SessionHelper(DropboxAccess dropbox, HashMap sessionmap)
      throws DboxException, java.rmi.RemoteException {
      
      this.dropbox = dropbox;
      setSessionId(sessionmap);
   }
   
  /**
   * Allows a listener to be registered with the SessoinHelper object. The listener 
   * will receive asychronous calls to the sessionUpdate method as they occur.
   * @param l  Listener being registered with the SessionHelper
   */
   public void addSessionListener(SessionListener l) {
      if (l == null) return;
      listeners = ServiceMulticaster.addSessionListener(listeners,l);
   }
   
  /**
   * Removes a listener which had been previously registered with the SessionHelper
   * @param l  Listener to de-register from the SessoinHelper
   */
   public void removeSessionListener(SessionListener l) {
      if (l == null) return;
      listeners = ServiceMulticaster.removeSessionListener(listeners,l);
   }
   
  /**
   * Causes the REFRESH SessionEvent to be sent to all registered listeners
   */
   public void refreshEvent() {
      if (listeners != null) {
         SessionEvent e = new SessionEvent(SessionEvent.REFRESH, this);
         listeners.sessionUpdate(e);
      }
   }
   
  /**
   * Causes the REFRESHERROR SessionEvent to be sent to all registered listeners
   */
   public void refreshErrorEvent(Throwable t) {
      if (listeners != null) {
         SessionEvent e = new SessionEvent(SessionEvent.REFRESHERROR,this,t);
         listeners.sessionUpdate(e);
      }
   }
   
  /**
   * Causes the INACTIVITY SessionEvent to be sent to all registered listeners
   */
   public void inactivityEvent() {
      if (listeners != null) {
         SessionEvent e = new SessionEvent(SessionEvent.INACTIVITY,this);
         listeners.sessionUpdate(e);
      }
   }
   
  /**
   * Causes the SHUTDOWN SessionEvent to be sent to all registered listeners.
   */
   public void shutdownEvent() {
      shutdownEvent(null);
   }
   
  /**
   * Causes the SHUTDOWN SessionEvent to be sent to all registered listeners
   *  with an associated Throwable.
   */
   public void shutdownEvent(Throwable t) {
      if (listeners != null) {
         SessionEvent e = new SessionEvent(SessionEvent.SHUTDOWN,this,t);
         listeners.sessionUpdate(e);
      }
   }
   
  /**
   * Sets the verbosity of the SessionHelper (default is false). If true,
   *  then any exceptions/errors occuring during operation will be
   *  logged to stdout
   */
   public void setVerbose(boolean v) { quiet = !v; }
   
   /**
   * Set the debug capability of the proxy. All method calls made via the
   * DropboxAccess proxy in question will be logged to System.out (with 
   * parameters) when debug is enabled
   */
   public void setProxyDebug(boolean v) { 
      if (dropbox != null && !done) {
         ProxyDebugInterface pd = (ProxyDebugInterface)dropbox;
         pd.setDebug(v);
      }
   }
   
   /**
   * Get the debug status of the proxy
   */
   public boolean isDebugEnabled() { 
      if (dropbox != null && !done) {
         ProxyDebugInterface pd = (ProxyDebugInterface)dropbox;
         return pd.isDebugEnabled();
      }
      return false;
   }
   
  /**
   * Apply the inactivity check logic
   */
   protected void manageShutdownCheck() {
      boolean inactive = false;
      synchronized(this) {
//         System.out.println("manageShutdownCheck: enter");
         if (dropbox != null && !done && autoclose) {
//            System.out.println("manageShutdownCheck: In check");
            
            ProxyDebugInterface pd = (ProxyDebugInterface)dropbox;
            
           // Only do it if the proxy has seen action since last refresh
            long invCount = pd.getInvocationCount();
//            System.out.println("manageShutdownCheck: invCount = " + invCount + " hk = " + housekeepingCount);
          
            
            if (invCount == housekeepingCount) {
               inactive = true;
            } else {
               housekeepingCount = 0;
               pd.resetInvocationCount();
            }
         }
      }
      
      if (inactive) {
         inactivityEvent();    // tell them what we are about to do
         cleanup();            // Do the shutdown, that will send another event
      } else if (autoclose) {
         manageAutoTask();
      }
   }
   
  /**
   * Setup the TimerTask to manage the shutdown check as needed
   */
   protected void manageAutoTask() {
//      System.out.println("manageAutoTask: enter");
      if (autotask != null) {
         autotask.cancel();
         autotask = null;
      }
      if (!done && autoclose) {
//         System.out.println("manageAutoTask: add autoTask for " + autoCloseDelay);
         autotask = new TimerTask() {
               public void run() {
                  manageShutdownCheck();
               }
            };
         sessiontimer.schedule(autotask, autoCloseDelay);
      }
   }
   
  /**
   * Sets the number of seconds of inactivity which must be eclisped before
   *  cleanup is called when autoClose is enabled. There is no gaurentee
   *  the cleanup will be called precisely when <i>secs</i> seconds of inactivity
   *  occurs. Instead, a check will be made approximately every <i>secs</i> seconds
   *  to see if any activity has occurred. If so, the activity count is reset, and
   *  the cycle continues. If NO activity has occurred, however, then the helper
   *  cleanup method will be invoked.
   */
   public void setAutoCloseDelay(int secs) { 
      
      autoCloseDelay = secs*1000; 
      
      if (autoclose) {
         manageAutoTask();
      }
   }
   
  /**
   * Get the currently set auto close delay (secs into the future)
   */
   public int getAutoCloseDelay() { return (int)(autoCloseDelay/1000); }
   
  /**
   * Enable/Disable the auto close feature. 
   */
   public void setAutoClose(boolean v) { 
   
     // If we are turning it on
      synchronized(this) {
         if (v && !autoclose) {
            
           // Start count now
            ProxyDebugInterface pd = (ProxyDebugInterface)dropbox;
            housekeepingCount = 0;
            pd.resetInvocationCount();
         }
      
         autoclose = v; 
      }
      
     // manage autoclose timer. Will setup or tear down per autoclose setting
      manageAutoTask();
   }
   
  /**
   * Query the current autoclose setting
   */
   public boolean isAutoCloseEnabled() { return autoclose; }
   
  /**
   * Sets the OS and ClientType session options. OS is generated using
   *  JVM settings, while client type value is provided by the caller.
   */
   public void setSessionInformation(String clienttype) 
      throws DboxException, java.rmi.RemoteException {
      
      HashMap h = new HashMap();
      h.put(DropboxAccess.OS, 
            System.getProperty("os.name") + " " +
            System.getProperty("os.arch") + " " +
            System.getProperty("os.version"));
      h.put(DropboxAccess.ClientType, clienttype);
      dropbox.setOptions(h);
   }
   
  /**
   * This method uses the SessionID and Expiration fields provided in the 
   * sessionmap parameter to properly register the sessionid with the proxy.
   * Further, it will also setup a Timer to ensure that the session is 
   * refreshed prior to its expiration. The refresh cycle will continue until
   * the cleanup method is called, or the JVM is exited. The Timer thread is
   * set to be a daemon, so will not hold up an application from exiting.
   */
   public void setSessionId(HashMap sessionmap) 
      throws DboxException, java.rmi.RemoteException {
   
     // Return if done
      if (done) return;
      
      synchronized(this) {
         ConnectionFactory factory = (ConnectionFactory)
            ((ProxyDebugInterface)dropbox).getProxiedInfo("FACTORY");      
         
        // If we have a null sessionmap, just reup for another 60 seconds
         long tottime = 60000;
         if (sessionmap != null) {
            factory.setSessionId(dropbox, sessionmap);
         
            long curtime = System.currentTimeMillis();
            
           // If we have a SessionTTL, use that rather than GMT clock yuckiness
            Long ttl = (Long)sessionmap.get("SessionTTL");
            if (ttl != null) {
               tottime = (ttl.longValue()*1000)/2;
            } else {
               Long expires = (Long)sessionmap.get(dropbox.Expiration);
            
               tottime = (expires.longValue() - curtime)/2;
            }
            
           // No more than once every 60 seconds
            if (tottime < 60000) tottime = 60000;
         }
         
        // Cancel any registered task (which MAY be our invoker). No harm
         if (sessiontask != null) {
            sessiontask.cancel();
         }
         
        // Create a new sessiontask to refresh the session
         sessiontask = new TimerTask() {
               public void run() {
                  refreshSession();
               }
            };
         
        // If we are still going ... schedule it
         if (!done && sessiontimer != null) {
            sessiontimer.schedule(sessiontask, tottime);
            
         }
      }
   }
   
  /**
   * Using the DropboxAccess proxy provided upon creation, this method will
   *  refresh the active session, and will then invoke setSessionId to properly 
   *  register the sessionid and schedule a future refresh action.
   */
   protected synchronized void refreshSession() {
      try {
         if (dropbox != null) {
            housekeepingCount++;
            setSessionId(dropbox.refreshSession());
            refreshEvent();
         }
      } catch(Exception e) {
         if (!quiet) {
            System.out.println("SessionHelper: Error refreshing Session!");
            e.printStackTrace(System.out);
         }
         try {
            setSessionId(null);  // Just reset the timer to try again
         } catch(Exception ee) {}
         refreshErrorEvent(e);  // Let listener know
      }
   }
   
  /**
   * Returns the DropboxAccess proxy being refreshed by this helper
   *
   */
   public DropboxAccess getProxy() { return dropbox; }
   
  /**
   * This method will remove any pending refresh tasks, release all resources, 
   *  including calling closeSession on the DropboxAccess proxy. 
   */
   public void cleanup() {
      done = true;
      autoclose = false;
      
      try { 
         if (sessiontask != null) {
            sessiontask.cancel();
         }
      } catch(Exception ee) {}
      sessiontask = null;
      
      try { 
         if (autotask != null) {
            autotask.cancel();
         }
      } catch(Exception ee) {}
      autotask = null;
      
      try { 
         sessiontimer.cancel(); 
      } catch(Exception ee) {}
      sessiontimer = null;
      
      try { 
         if (dropbox != null) {
            dropbox.closeSession();
         }
      } catch(Exception ee) {}
      dropbox = null;
      
      shutdownEvent();
   }
}
