package oem.edge.ed.odc.dropbox.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2006                                     */
/*                                                                          */
/*     All Rights Reserved                                                  */
/*     US Government Users Restricted Rights                                */
/*                                                                          */
/*     The source code for this program is not published or otherwise       */
/*     divested of its trade secrets, irrespective of what has been         */
/*     deposited with the US Copyright Office.                              */
/*                                                                          */
/*   --------------------------------------------------------------------   */
/*     Please do not remove any of these commented lines  20 lines          */
/*   --------------------------------------------------------------------   */
/*                       Copyright Footer Check                             */

/**
 * @author zarnick
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MethodRunner implements Runnable {
	private Object object;
	private String method;
	static private Class[] parms = new Class[0];
	static private Object[] args = new Object[0];
	public MethodRunner(Object object, String method) {
		this.object = object;
		this.method = method;
	}
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			Class c = object.getClass();
			//System.out.println("MethodRunner.run: Attempting to run " + method + " on " + c.toString());
			Method m = c.getMethod(method,parms);
			m.invoke(object,args);
		}
		catch (NoSuchMethodException nsm) {
			System.out.println("MethodRunner.run: no such method: " + method);
			System.out.println(nsm.getMessage());
		}
		catch (InvocationTargetException it) {
			System.out.println("MethodRunner.run: invocation target exception.");
			if (it.getTargetException() != null) {
				System.out.println(it.getTargetException().getMessage());
				it.getTargetException().printStackTrace();
			}
			if (it.getMessage() != null) {
				System.out.println(it.getMessage());
			}
		}
		catch (IllegalAccessException ia) {
			System.out.println("MethodRunner.run: illegal access exception.");
			System.out.println(ia.getMessage());
		}
	}

}
