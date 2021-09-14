package com.caucho.hessian.server;
/*
** InvocationFilter
**
** JMC 11/14/05
**
** Used by HessianSkeleton to allow precall checking/actions. This will allow
**  other context type info (such as headers) to be injected into the object
**  which is the target of the method call prior to actual invoke. 
**
*/
import java.lang.reflect.Method;

public interface InvocationFilter {
   public void preInvoke(Method method, Object object, Object [] args);
   public void postInvoke(Method method, Object object, Object [] args);
}
