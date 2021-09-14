package oem.edge.ed.odc.util.db;

import java.lang.*;
import java.sql.*;
import javax.sql.*;
import javax.naming.*;
import java.util.*;

import org.apache.log4j.Logger;

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

public class PreparedStatementProxy implements InvocationHandler {

   static Logger log = Logger.getLogger(PreparedStatementProxy.class);
   static HashSet captureMethods = null;
   
   protected PreparedStatement statement = null;
   protected String sql = null;
   protected HashMap parms = new HashMap();
   
   public PreparedStatementProxy(PreparedStatement s, String sqlIn) {
      statement = s;
      sql = sqlIn;
      if (captureMethods == null) {
         HashSet m = new HashSet();
        // These are the biggies
         m.add("setTimestamp");
         m.add("setTime");
         m.add("setBigDecimal");
         m.add("setBoolean");
         m.add("setObject");
         m.add("setByte");
         m.add("setInt");
         m.add("setLong");
         m.add("setDouble");
         m.add("setFloat");
         m.add("setString");
         m.add("setDate");
         m.add("setShort");
         captureMethods = m;
      }
   }
   
   public PreparedStatement makeProxy() {
      ClassLoader cl = PreparedStatement.class.getClassLoader();
      Class arr[] = new Class[] {PreparedStatement.class};
      return (PreparedStatement)Proxy.newProxyInstance(cl, arr, this);
   }
   
   
  // Everything goes thru here on actual object
   public Object invoke(Object proxy, 
                        Method method, 
                        Object []args) throws Throwable {
      
     String methodName = method.getName();
     Class []params    = method.getParameterTypes();
     
    // Do any overrides
     if (methodName.equals("toString")) {
       // Do my toString and return
        try {
           String cinfo = "Unknown";
           Exception ee = new Exception();
           ee.fillInStackTrace();
           StackTraceElement ste[] = ee.getStackTrace();
           if (ste != null && ste.length >= 6) {
              String cn=ste[5].getClassName();
              int idx = cn.lastIndexOf(".");
              if (idx > -1) cn = cn.substring(idx+1);
              cinfo=cn + '.' + ste[5].getMethodName() + 
                 '(' + ste[5].getFileName() + ':' + ste[5].getLineNumber() + ')';
           }
           
           ParameterMetaData pmd = statement.getParameterMetaData();
           int cnt = pmd.getParameterCount();
           if (sql == null) {
              log.warn(cinfo + ": SQL is null and asked to do toString()!");
           } else {
              log.debug(cinfo + ": SQL: " + sql);
              for (int i=1; i <= cnt; i++) {
                 Integer iv = new Integer(i);
                 String v = (String)parms.get(iv);
                 if (v == null) v = "Unknown Value";
                 log.debug("   Parm[" + i + "] value[" + v + "] type[" + 
                           pmd.getParameterTypeName(i) +"]");
              }
           }
              
        } catch(Exception e) {
           log.error("Error while trying to log PreparedStatement for debug!", e);
        }
        return null;
     } else if (methodName.equals("clearParameters")) { 
        parms.clear();
     } else if (captureMethods.contains(methodName)) {
        parms.put(args[0], args[1].toString());
     }
     
     Method objmeth = PreparedStatement.class.getMethod(methodName, params);
     try {
        return objmeth.invoke(statement, args);
     } catch(InvocationTargetException ite) {
        throw ite.getTargetException();
     }
  }      
}
