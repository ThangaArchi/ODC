package oem.edge.util;

import java.util.*;
import java.io.*;
import java.beans.*;
import java.lang.reflect.*;
import org.apache.commons.logging.*;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004-2004                                     */
/*                                                                          */
/*     All Rights Reserved                                                  */
/*     US Government Users Restricted Rigts                                 */
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
 * @author bjr
 * xxx.maxActive
 */
public class PropertiesLoader {
	private static Log log = LogFactory.getLog(PropertiesLoader.class);
	
	public static void loadConfigurationBean(ResourceBundle rb, String prefix, Object bean) {
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(bean.getClass());
		} catch (IntrospectionException x) {
		}
		PropertyDescriptor properties[] = beanInfo.getPropertyDescriptors();
		for (int i=0; i<properties.length; i++) {
			String name = properties[i].getName();
			Class type = properties[i].getPropertyType();
			String val = null;
			try {
				rb.getString(prefix+"."+name);
			} catch (MissingResourceException x) {
				log.info("missing property "+prefix+"."+name+", using default.");
			}
			if (val == null)
				continue;
			Object args[] = null;
			Method setter = properties[i].getWriteMethod();
			if (type.equals(Integer.TYPE)) {
				args = new Object[] { new Integer(val) };
			} else if (type.equals(Long.TYPE)) {
				args = new Object[] { new Long(val) };
			} else if (type.equals(Boolean.TYPE)) {
				args = new Object[] { new Boolean(val) };
			} else if (type.equals(String.class)) {
				args = new Object[] { val };
			}
			if (args != null) {
				try {
					setter.invoke(bean, args);
				} catch (InvocationTargetException x) {
					log.error(x);
				} catch (IllegalAccessException x) {
					log.error(x);
				}
			}
		}
		Field fields[] = bean.getClass().getFields();
		for (int i=0; i<fields.length; i++) {
			String name = fields[i].getName();
			Class type = fields[i].getType();
			String val = null;
			try {
				val = rb.getString(prefix+"."+name);
			} catch (MissingResourceException x) {
				log.info(x);
			}
			if (val == null)
				continue;
			Object arg = null;
			if (type.equals(Integer.TYPE)) {
				arg = new Integer(val);
			} else if (type.equals(Long.TYPE)) {
				arg = new Long(val);
			} else if (type.equals(Boolean.TYPE)) {
				arg = new Boolean(val);
			} else if (type.equals(String.class)) {
				arg = val;
			}
			if (arg != null) {
				try {
					fields[i].set(bean, arg);
				} catch (IllegalAccessException x) {
					log.error(x);
				}
			}
		}
	}
	
	public static void main(String args[]) {
		try {
			ResourceBundle rb = ResourceBundle.getBundle("test");
			org.apache.commons.pool.impl.GenericKeyedObjectPool.Config bean = new org.apache.commons.pool.impl.GenericKeyedObjectPool.Config();
			PropertiesLoader.loadConfigurationBean(rb, "test", bean);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}
