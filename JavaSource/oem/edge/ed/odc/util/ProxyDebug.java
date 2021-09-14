package oem.edge.ed.odc.util;

import java.lang.*;
import java.util.*;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;

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

public class ProxyDebug implements InvocationHandler, ProxyDebugInterface {

   protected SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss.SSS",
                                                                java.util.Locale.US);
   protected Object toProxy       = null;
   protected int dodebug          = OFF;
   protected String cname         = "NotSet";
   protected long invocationCount = 0;
   
   protected boolean wrapHessianException = true;
   
  // We support hanging objects off the proxy
   protected HashMap infomap = new HashMap();
   
  // prepost caller list
   protected Vector prepostCallers = null;
   
   public ProxyDebug(Object s) {
      toProxy = s;
      Class intf[] = toProxy.getClass().getInterfaces();
      setName(intf);      
   }
      
   public void setName(Class intf[]) {
      StringBuffer sb = new StringBuffer();
      for(int i=0; i < intf.length; i++) {
         if (sb.length() > 0) sb.append("/");
         sb.append(intf[i].getName());
      }
      if (sb.length() > 0) cname = sb.toString();
      else                 cname = toProxy.getClass().getName();
      
   }   
   
   public void setName(Class intf) {
      cname = intf.getName();
   }   
   
   
  // ---- START These are the ProxyInterface methods  START
   public Object getProxiedObject() { return toProxy; }
   
   public void enableDebug() {
      dodebug = FULL;
   }
   
   public void enableDebug(int lev) {
      if (lev > FULL) lev = FULL;
      if (lev < OFF)  lev = OFF;
      dodebug = lev;
   }
   
   public void disableDebug() {
      dodebug = OFF;
   }
   
   public void setDebug(boolean v) {
      dodebug = (v?FULL:OFF);
   }
   
   public void setDebug(int lev) {
      enableDebug(lev);
   }
   
   public boolean isDebugEnabled() {
      return dodebug > OFF;
   }
   
   
   public void resetInvocationCount() { invocationCount = 0;  }
   public long getInvocationCount() { return invocationCount; }
   
   public Object getProxiedInfo(String s) {
      return infomap.get(s);
   }
   public Object setProxiedInfo(String s, Object o) {
      return infomap.put(s, o);
   }
  // ---- END   These are the ProxyInterface methods   END
   
   
   public String getFormattedDate() {
      return dateformat.format(new Date()) + " ";
   }
   
  // These methods should be customized to do proper logging as appropriate
   public void show(String s) {
      System.out.print(s);
   }
   public void showln(String s) {
      System.out.println(getFormattedDate() + s);
   }
   
   public void show(Throwable t) {
      t.printStackTrace(System.out);
   }
   public void showln(Throwable t) {
      t.printStackTrace(System.out);
   }
   
  // These methods are helpers to flatten maps/cltns for printing
   public static String getMapAsString(Map m) {
      if (m == null) return "null";
      
      if (m.size() == 0) return "empty map";
      
      StringBuffer sb = new StringBuffer();
      Vector displayed = new Vector();
      Iterator it = m.keySet().iterator();
      
      int i=0;
      while(it.hasNext()) {
         Object o = it.next();
         sb.append("[").append(i++).append("]:");
         if (o != null) {
            sb.append(Nester.nest("\nkey: \n"));
            sb.append(Nester.nest(getAsString(o), 2));
            sb.append(Nester.nest("\nvalue: \n"));
            sb.append(Nester.nest(getAsString(m.get(o)), 2));
            sb.append(Nester.nest("\n"));
         } else {
            sb.append(" null");
         }
      }
      return "Map {\n" + Nester.nest(sb.toString()) + "\n}";
   }
   
   public static String getCollectionAsString(Collection c) {
      if (c == null) return "null";
      
      if (c.size() == 0) return "empty cltn";
      
      StringBuffer sb = new StringBuffer();
      Vector displayed = new Vector();
      Iterator it = c.iterator();
      
      int i=0;
      while(it.hasNext()) {
         Object o = it.next();
         sb.append("[").append(i++).append("]:");
         if (o != null) {
            sb.append(Nester.nest(getAsString(o)));
            sb.append(Nester.nest("\n"));
         } else {
            sb.append(" null");
         }
      }
      return "Collection {\n" + Nester.nest(sb.toString()) + "\n}";
   }
   
   public static String getAsString(Object o) {
      if (o == null) return "null";
      if        (o instanceof Map) {
         return getMapAsString((Map)o);
      } else if (o instanceof Collection) {
         return getCollectionAsString((Collection)o);
      } else {
         return o.toString();
      }
   }
   
   
  // The makeProxy methods create the proxy based on the proxy object provided
  // AND, perhaps, other interface info
   public Object makeProxy(Class intf[]) {
      
      ClassLoader cl = toProxy.getClass().getClassLoader();
      setName(intf);
      Class newintf[] = new Class[intf.length + 1];
      System.arraycopy(intf, 0, newintf, 0, intf.length);
      newintf[intf.length] = ProxyDebugInterface.class;
      return Proxy.newProxyInstance(cl, newintf, this);
   }
   
   public Object makeProxy(Class intf) {
      ClassLoader cl = toProxy.getClass().getClassLoader();
      setName(intf);
      Class arr[] = new Class[] { intf, ProxyDebugInterface.class };
      return Proxy.newProxyInstance(cl, arr, this);
   }
   
   public Object makeProxy() {
      ClassLoader cl = toProxy.getClass().getClassLoader();
      Class intf[] = toProxy.getClass().getInterfaces();
      setName(intf);      
      Class newintf[] = new Class[intf.length + 1];
      System.arraycopy(intf, 0, newintf, 0, intf.length);
      newintf[intf.length] = ProxyDebugInterface.class;
      return Proxy.newProxyInstance(cl, newintf, this);
   }
   
   public Object removePrePostCaller(ProxyDebugPrePost o) {
      if (prepostCallers != null) {
         synchronized (prepostCallers) {
            Iterator it = prepostCallers.iterator();
            while(it.hasNext()) {
               if (it.next() == (Object)o) {
                  it.remove();
                  return o;
               }
            }
         }
      }
      return null;
   }
   public Object addPrePostCaller(ProxyDebugPrePost o) {
      if (prepostCallers == null) {
         synchronized (this) {
            if (prepostCallers == null) {
               prepostCallers = new Vector();
            }
         }
      }
      synchronized (prepostCallers) {
         removePrePostCaller(o);
         prepostCallers.add(o);
      }
      return null;
   }
   
   public void doPreCallers(Object o) {
      doPrePostCallers(o, true);
   }
   public void doPostCallers(Object o) {
      doPrePostCallers(o, false);
   }
   
   public void doPrePostCallers(Object o, boolean prePost) {
      ProxyDebugPrePost prepostV = null;
      if (prepostCallers != null) { 
         int sz = prepostCallers.size();
        // While there is a window where the size may change ... 
        //  ... I just don't care today
         if (sz == 0) return;
         if (sz == 1) {
            try {
               prepostV = 
                  ((ProxyDebugPrePost)prepostCallers.elementAt(0));
               if (prePost) prepostV.preCall(o);
               else         prepostV.postCall(o);
            } catch(Exception e) {
            }
         } else {
           // If list changes here ... too bad. Should synchronize and copy
           //  to new Vector, then iterate over that.
            Iterator it = prepostCallers.iterator();
            while(it.hasNext()) {
               prepostV = ((ProxyDebugPrePost)it.next());
               if (prePost) prepostV.preCall(o);
               else         prepostV.postCall(o);
            }
         }
      }
   }
   
   
   
  // Everything goes thru here on actual object
   public Object invoke(Object proxy, 
                        Method method, 
                        Object []args) throws Throwable {
      
     String methodName = method.getName();
     Class []params    = method.getParameterTypes();
     
     boolean ldodebug = true;
     if (methodName.equals("hashCode")) {
        ldodebug = false;
     }
     
     Method objmeth = null;

     try { 
       // If the object being proxied handles the method, let him
        objmeth = toProxy.getClass().getMethod(methodName, params);
     } catch(Exception e) {
     
       // ... Otherwise, I try to handle it
        objmeth = ProxyDebugInterface.class.getMethod(methodName, params);
        
        try {
           return objmeth.invoke(this, args);
        } catch(InvocationTargetException ite) {
           throw ite.getTargetException();
        }
     }
     
    // Set localcount to current invocationCount. If we should NOT count this
    //  invocation (ldodebug is false), then no pre-increment is done
     long localcnt = invocationCount;
     if (!ldodebug) localcnt = ++invocationCount;
     
     if (ldodebug && dodebug > OFF) {
        
        try {
        
           if (dodebug < FULL) {
              showln("ENTER " + methodName + "[" + localcnt + "]");
           } else {
              StringBuffer sb = new StringBuffer();
              sb.append("ENTER [").append(localcnt).append("] ");
              sb.append(cname).append(".");
              sb.append(methodName).append("(");
              for(int i=0; i < params.length; i++) {
                 if (i > 0) sb.append(", ");
                 sb.append(params[i].getName());
              }
              sb.append(")");
              
              
              if (params.length > 0) {
                 for(int i=0; i < args.length; i++) {
                    sb.append("\n   [" +i +"]: \n");
                    sb.append(Nester.nest(getAsString(args[i])));
                 }
              }
              
              showln(sb.toString());
           }
        } catch(Throwable tt) {
           showln("Error showing debug entry [" + localcnt + "]:");
           showln(tt);
        }
     }     
     
     try {
        doPreCallers(toProxy);
        Object o =  objmeth.invoke(toProxy, args);
        doPostCallers(toProxy);
        
        if (ldodebug && dodebug > OFF) {
           StringBuffer sb = new StringBuffer();
           try {
           
              if (dodebug < FULL) {
                 sb.append("EXIT  ").append(methodName).append("[").append(localcnt).append("]");
              } else {
                 sb.append("EXIT [").append(localcnt).append("] ");
                 sb.append(methodName).append("  Result:\n");
                 sb.append(Nester.nest(getAsString(o)));
              }
              
              showln(sb.toString());
           } catch(Throwable tt) {
              showln("Error showing debug RESULT [" + localcnt + "]:");
              showln(tt);
           }
        }
        
        return o;
     } catch(InvocationTargetException ite) {
     
        Throwable target = ite.getTargetException();
        String cn = target.getClass().getName();
        
       // Wrap the Hessian based exceptions with RemoteException if allowed
       // both by proxy (caller) and method throws declaration
        if (wrapHessianException && 
            cn.startsWith("com.caucho.hessian")) {
           
           Class exps[] = objmeth.getExceptionTypes();
           for(int i=0; i < exps.length; i++) {
              if (RemoteException.class.isAssignableFrom(exps[i])) {
                 RemoteException rexp =
                   new RemoteException("Connectivity or remoting protocol issue encountered", 
                                       target);
                 target = rexp;
                 break;
              }
           }
        }
     
        if (ldodebug && dodebug > OFF) {
           showln("@@DEBUG@@ EXIT Exception:");
           showln(target);
        }
        
        throw target;
     }
  }      
}
