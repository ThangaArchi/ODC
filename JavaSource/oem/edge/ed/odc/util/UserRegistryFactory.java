package oem.edge.ed.odc.util;

import java.util.ResourceBundle;
import org.apache.log4j.Logger;

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

public class UserRegistryFactory {
   static UserRegistry reg = null;
   
   static Logger log = Logger.getLogger("oem.edge.ed.odc.util.UserRegistryFactory");
   static String defaultClass = "oem.edge.ed.odc.util.AMTQuery";
   
   static public void setDefaultClassname(String s) {
      defaultClass = s;
      reg = null;
   }
   
   static public UserRegistry getInstance() {
      if (reg != null) return reg;
      
      String cname = defaultClass;
      
      try {
         cname = (String)
            ResourceBundle.getBundle("UserRegistry").getObject("registryclass");
            
        // The startsWith is there to get the default if no variable substitution
        //  occurs on the propfile. Hack
         if (cname == null || cname.trim().equals("") || cname.startsWith("$")) {
            cname = defaultClass;
         }
      } catch(Exception ee) {
      }
      
      try {
         Class c = Class.forName(cname);
         reg = (UserRegistry)c.newInstance();
         log.info("Loaded class: " + cname);
         
      } catch(Exception eee) {
         log.warn("Error loading class: " + cname);
         log.warn(eee);
         throw new java.lang.Error("UserRegistry class could not be loaded: " +
                                   cname);
      }
      return reg;
   }
}
