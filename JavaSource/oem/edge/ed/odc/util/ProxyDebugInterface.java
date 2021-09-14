package oem.edge.ed.odc.util;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 2006    		                         */ 
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
 * Interface to manipulate a ProxyDebug proxied object. It allows one to dive down
 *  and object the proxied object, manipulate the debug printing state, and to 
 *  store/retrieve additional info on the proxy.
 *<p>
 * Note, that the ProxyDebugInterface is always the LAST supported interface 
 *  assocated with the proxy, so other interfaces may occlude the methods. 
 *  Buyer beware.
 */
public interface ProxyDebugInterface {

   static public final int OFF   = 0;
   static public final int NAMES = 1;
   static public final int FULL  = 2;

  // Get ahold of the actual object being proxied
   public Object getProxiedObject();
   
  /**
   * Change the debug print level to FULL
   */
   public void enableDebug();
   
  /**
   * Change the debug print level to the specified level. If level is <= 0 (OFF), then
   *  debug printing will be disabled. Setting to NAMES will print just the method
   *  names, FULL will print names as well as parms and return value (if any)
   */
   public void enableDebug(int lev);
   
  /**
   * Sets the debug level to OFF
   */
   public void disableDebug();
   
  /**
   * See enableDebug(int lev)
   */
   public void setDebug(int lev);
   
  /**
   * Setting true will enable FULL debug, otherwise disables debug.
   */
   public void setDebug(boolean v);
   
  /**
   * Return true if debug level > OFF
   */
   public boolean isDebugEnabled();
   
   public void resetInvocationCount();
   public long getInvocationCount();
   
  // Store and retrieve info with the DebugProxy
   public Object getProxiedInfo(String s);
   public Object setProxiedInfo(String s, Object o);
   
   public Object removePrePostCaller(ProxyDebugPrePost o);
   public Object addPrePostCaller(ProxyDebugPrePost o);
   
   public void doPreCallers(Object o);
   public void doPostCallers(Object o);

}
