package oem.edge.ed.odc.util;
import  java.util.Vector;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 1998,2004,2005,2006		                 */ 
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

public interface AMTUser {
   
   public void setUser (String v);
   public void setEmail(String v);
   public void setCompany(String v);
   public void setIBMDept(String v);
   public void setIBMDiv(String v);
   public void setCountry(String v);
   public void setState(String v);
   public void setFirstName(String v);
   public void setLastName(String v);
   public void setEntitlements(Vector v);
   public void addEntitlements(Vector v);
   public void addEntitlement(String p);
   public void setProjects(Vector v);
   public void addProjects(Vector v);
   public void addProject(String p);
   
   public String getUser ();
   public String getEmail();
   public String getCompany();
   public String getIBMDept();
   public String getIBMDiv();
   public String getCountry();
   public String getState();
   public String getFirstName();
   public String getLastName();
   public Vector getEntitlements();
   public Vector getProjects();
   
   public boolean isEntitled(String s);
}
