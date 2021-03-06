/*
 * Copyright (c) 2001-2004 Caucho Technology, Inc.  All rights reserved.
 *
 * The Apache Software License, Version 1.1
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Caucho Technology (http://www.caucho.com/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "Hessian", "Resin", and "Caucho" must not be used to
 *    endorse or promote products derived from this software without prior
 *    written permission. For written permission, please contact
 *    info@caucho.com.
 *
 * 5. Products derived from this software may not be called "Resin"
 *    nor may "Resin" appear in their names without prior written
 *    permission of Caucho Technology.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL CAUCHO TECHNOLOGY OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Scott Ferguson
 */

package com.caucho.hessian.server;

import java.io.InputStream;
import java.io.IOException;

import java.util.HashMap;

// JMC Logging is a 1.4 thin
//import java.util.logging.Logger;
//import java.util.logging.Level;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import com.caucho.services.server.AbstractSkeleton;
import com.caucho.services.server.ServiceContext;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

// JMC 11/14/05 - We allow an InvocationFilter
import com.caucho.hessian.server.InvocationFilter;

/**
 * Proxy class for Hessian services.
 */
public class HessianSkeleton extends AbstractSkeleton {
  // JMC Logging is a 1.4 thing
  //private static final Logger log = Logger.getLogger(HessianSkeleton.class.getName());
  
  private Object _service;
  
  // JMC 11/14/05 - We allow an InvocationFilter
   private InvocationFilter _invocationFilter = null;
   public void setInvocationFilter(InvocationFilter f) {
      _invocationFilter = f;
   }
  
  /**
   * Create a new hessian skeleton.
   *
   * @param service the underlying service object.
   * @param apiClass the API interface
   */
  public HessianSkeleton(Object service, Class apiClass)
  {
    super(apiClass);

    _service = service;
    
    if (! apiClass.isAssignableFrom(service.getClass()))
      throw new IllegalArgumentException("Service " + service + " must be an instance of " + apiClass.getName());
  }

  /**
   * Invoke the object with the request from the input stream.
   *
   * @param in the Hessian input stream
   * @param out the Hessian output stream
   */
  public void invoke(HessianInput in, HessianOutput out)
    throws Throwable
  {
    in.readCall();

    ServiceContext context = ServiceContext.getContext();
    
    String header;
    while ((header = in.readHeader()) != null) {
      Object value = in.readObject();

      context.addHeader(header, value);
    }

    String methodName = in.readMethod();
    Method method = getMethod(methodName);

    if (method != null) {
    }
    else if ("_hessian_getAttribute".equals(methodName)) {
      String attrName = in.readString();
      in.completeCall();

      String value = null;

      if ("java.api.class".equals(attrName))
	value = getAPIClassName();
      else if ("java.home.class".equals(attrName))
	value = getHomeClassName();
      else if ("java.object.class".equals(attrName))
	value = getObjectClassName();

      out.startReply();

      out.writeObject(value);

      out.completeReply();
      return;
    }
    else if (method == null) {
      out.startReply();
      out.writeFault("NoSuchMethodException",
		     "The service has no method named: " + in.getMethod(),
		     null);
      out.completeReply();
      return;
    }

    Class []args = method.getParameterTypes();
    Object []values = new Object[args.length];

    for (int i = 0; i < args.length; i++)
      values[i] = in.readObject(args[i]);

    in.completeCall();

    Object result = null;
    
    try {
    
      // JMC 11/14/05 - We allow an InvocationFilter
       if (_invocationFilter != null) {
          _invocationFilter.preInvoke(method, _service, values);
       }
       
       result = method.invoke(_service, values);
       
    } catch (Throwable e) {
      if (e instanceof InvocationTargetException)
        e = ((InvocationTargetException) e).getTargetException();

     // Logging is a 1.4 thing
     //log.log(Level.WARNING, e.toString(), e);
      
      out.startReply();
      out.writeFault("ServiceException", e.getMessage(), e);
      out.completeReply();
      return;
      
    } finally {
    
      // JMC 11/14/05 - We allow an InvocationFilter
       if (_invocationFilter != null) {
          try {
             _invocationFilter.postInvoke(method, _service, values);
          } catch(Exception ll) {
          }
       }
    }

    out.startReply();

    out.writeObject(result);
    
    out.completeReply();
  }
}
