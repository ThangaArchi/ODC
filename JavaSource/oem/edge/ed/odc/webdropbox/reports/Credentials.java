package oem.edge.ed.odc.webdropbox.reports;

import oem.edge.ed.odc.dsmp.server.UserInfo;
import java.util.*;
import oem.edge.ed.odc.util.UserRegistryFactory;
import oem.edge.ed.odc.util.AMTQuery;
import oem.edge.ed.odc.util.AMTUserInst;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 2005-2006                                    */ 
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

public class Credentials extends UserInfo {

   boolean issuper = false;
   boolean isfsesuper = false;
   Vector fseprojects = null;
   Hashtable fseFor = null;

   public Credentials(String name) {
      super(name);
   }
   
   public Credentials(UserInfo ui) {
      super(ui.getName());
      setProjects(ui.getProjects());
      setEmail(ui.getEmail());
      setCompany(ui.getCompany());
      setCountry(ui.getCountry());
      setIBMDiv(ui.getIBMDiv());
      setIBMDept(ui.getIBMDept());
      
      if (ui instanceof Credentials) {
         setIsSuper(((Credentials)ui).isSuper());
         setIsFSESuper(((Credentials)ui).isFSESuper());
         setFSEProjects(((Credentials)ui).getFSEProjects());
      }
   }
   
   public boolean isSuper()             { return issuper; }
   public void   setIsSuper(boolean v)  { issuper = v;    }
   public boolean isFSESuper()             { return isfsesuper; }
   public void   setIsFSESuper(boolean v)  { isfsesuper = v;    }
   public void   setFSEProjects(Vector v) {
      fseprojects = (Vector)v.clone();
   }
   public Vector getFSEProjects() { return (Vector)fseprojects.clone(); }
   
   public synchronized boolean isFSEFor(String u) {
      if (fseprojects != null && fseprojects.size() > 0) {
      
        // If we have seen this guy before
         if (fseFor != null) {
            Boolean r = (Boolean)fseFor.get(u);
            if (r != null) return r.booleanValue();
         }
         
         try {
           // Check if we are FSE for this guy in any project
            AMTUserInst amtuser = new AMTUserInst();
            amtuser.setUser(u);
            
            AMTQuery amtq = (AMTQuery)UserRegistryFactory.getInstance();
            Vector projs = amtq.getAsicProjects(amtuser, true);
            if (projs != null && projs.size() > 0) {
               
               Enumeration enum = fseprojects.elements();
               while(enum.hasMoreElements()) {
                  String p = (String)enum.nextElement();
                  if (projs.contains(p)) {
                     if (fseFor == null) fseFor = new Hashtable();
                     fseFor.put(u, Boolean.TRUE);
                     return true;
                  }
               }
               if (fseFor == null) fseFor = new Hashtable();
               fseFor.put(u, Boolean.FALSE);
            }
         } catch(Exception e) {
            System.out.println("Credentials: Error checking isFSEFor: " + u);
            e.printStackTrace(System.out);
         }
      }
      return false;
   }
}
