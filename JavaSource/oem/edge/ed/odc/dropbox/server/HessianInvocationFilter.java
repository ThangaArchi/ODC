package oem.edge.ed.odc.dropbox.server;

import com.caucho.hessian.server.InvocationFilter;
import com.caucho.services.server.ServiceContext;
import java.lang.reflect.Method;

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

public class HessianInvocationFilter 
   implements com.caucho.hessian.server.InvocationFilter {
   
  // All we want to do is get the sessionID (if its there) and install it
   public void preInvoke(Method method, Object object, Object [] args) {
      if (object instanceof DropboxAccessSrv) {
         ServiceContext context = ServiceContext.getContext();
         Object val = context.getHeader("dboxsessionid");
         if (val != null && val instanceof String) {
            DropboxAccessSrv srv = (DropboxAccessSrv)object;
            srv.setThreadSessionID((String)val);
         }
      }
   }
   
  // Clean out any sessionid
   public void postInvoke(Method method, Object object, Object [] args) {
      if (object instanceof DropboxAccessSrv) {
         DropboxAccessSrv srv = (DropboxAccessSrv)object;
         srv.setThreadSessionID(null);
      }
   }
}
