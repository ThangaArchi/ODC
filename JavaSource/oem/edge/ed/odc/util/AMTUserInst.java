package oem.edge.ed.odc.util;
import  java.util.Vector;
import  java.util.Enumeration;

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

public class AMTUserInst implements AMTUser {
   
   protected String edgeuser     = null;
   protected String user         = null;
   protected String email        = null;
   protected String poccomp      = null;
   protected String company      = null;
   protected String amtcomp      = null;
   protected String ibmdept      = null;
   protected String ibmdiv       = null;
   protected String country      = null;
   protected String state        = null;
   protected String first        = null;
   protected String last         = null;
   protected Vector entitlements = new Vector();
   protected Vector projects     = new Vector();
   
   public void setEdgeUser (String v)    { edgeuser     = v; }
   public void setAMTCompany(String v)   { amtcomp      = v; }
   public void setPOCCompany(String v)   { poccomp      = v; }
   
   public void setUser (String v)        { user         = v; }
   public void setEmail(String v)        { email        = v; }
   public void setCompany(String v)      { company      = v; }
   public void setIBMDept(String v)      { ibmdept      = v; }
   public void setIBMDiv(String v)       { ibmdiv       = v; }
   public void setCountry(String v)      { country      = v; }
   public void setState(String v)        { state        = v; }
   public void setFirstName(String v)    { first        = v; }
   public void setLastName(String v)     { last         = v; }

   public void setEntitlements(Vector v)     { 
      synchronized(entitlements) {
         entitlements.clear();
         addEntitlements(v);   
      }
   }
   
   public void addEntitlement(String e)      {
      synchronized (entitlements) {
         if (e != null && !entitlements.contains(e)) {
            entitlements.addElement(e);
         }
      }
   }
   
   public void addEntitlements(Vector v)     { 
      synchronized(entitlements) {
         if (v != null) {
            Enumeration enum = v.elements();
            while(enum.hasMoreElements()) {
               String e = (String)enum.nextElement();
               if (!entitlements.contains(e)) {
                  entitlements.addElement(e);
               }
            }
         }
      }
   }
   
   public void setProjects(Vector v)     { 
      synchronized(projects) {
         projects.clear();
         addProjects(v);   
      }
   }
   
   public void addProject(String p)      {
      synchronized (projects) {
         if (p != null) {
            p = encode(p);
            if (!projects.contains(p)) {
               projects.addElement(p);
            }
         }
      }
   }
   
   public void addProjects(Vector v)     { 
      synchronized (projects) {
         if (v != null) {
            Enumeration enum = v.elements();
            while(enum.hasMoreElements()) {
               String p = (String)enum.nextElement();
               
               p = encode(p);
               
               if (!projects.contains(p)) {
                  projects.addElement(p);
               }
            }
         }
      }
   }
   
   public String getEdgeUser ()    { return edgeuser;     }
   public String getPOCCompany()   { return poccomp;      }
   public String getAMTCompany()   { return amtcomp;      }
   
   public String getUser ()        { return user;         }
   public String getEmail()        { return email;        }
   public String getCompany()      { return company;      }
   public String getIBMDept()      { return ibmdept;      }
   public String getIBMDiv()       { return ibmdiv;       }
   public String getCountry()      { return country;      }
   public String getState()        { return state;        }
   public String getFirstName()    { return first;        }
   public String getLastName()     { return last;         }
   public Vector getEntitlements() { return entitlements; }
   public Vector getProjects()     { return projects;     }
   
   public boolean isEntitled(String s) {
      return ((s!=null)?entitlements.contains(s):false);
   }
   
   
   static public String encode(String p) {
     // Pseudo URLEncode to eliminate /\: (%S, %B, %C ... %->%%)
      int idx=0;
      while((idx=p.indexOf('%', idx)) >= 0) {
         p = p.substring(0,idx) + "%%" + p.substring(idx+1);
         idx += 2;
      }
      idx=0;
      while((idx=p.indexOf('/', idx)) >= 0) {
         p = p.substring(0,idx) + "%S" + p.substring(idx+1);
      }
      idx=0;
      while((idx=p.indexOf('\\', idx)) >= 0) {
         p = p.substring(0,idx) + "%B" + p.substring(idx+1);
      }
      idx=0;
      while((idx=p.indexOf(':', idx)) >= 0) {
         p = p.substring(0,idx) + "%C" + p.substring(idx+1);
      }
      return p;
   }
   
   static public String decode(String p) {
      int idx=0;
      while((idx=p.indexOf("%%", idx)) >= 0) {
         p = p.substring(0,idx) + "%" + p.substring(idx+2);
         idx += 1;
      }
      idx=0;
      while((idx=p.indexOf("%S", idx)) >= 0) {
         p = p.substring(0,idx) + "/" + p.substring(idx+2);
         idx += 1;
      }
      idx=0;
      while((idx=p.indexOf("%B", idx)) >= 0) {
         p = p.substring(0,idx) + "\\" + p.substring(idx+2);
         idx += 1;
      }
      idx=0;
      while((idx=p.indexOf("%C", idx)) >= 0) {
         p = p.substring(0,idx) + ":" + p.substring(idx+2);
         idx += 1;
      }
      return p;
   }
   
  // We are qual enough if the userName matches
   public boolean equals(Object o) {
      if (o instanceof AMTUser) {
         return ((AMTUser)o).getUser().equals(getUser());
      }
      return false;
   }
   
  // We are qual enough if the userName matches
   public int hashCode() { return getUser().hashCode(); }
   
   public String toString() {
      String ret = "AMTUserIntf {\n"        +
         "   userid     = " + user     + "\n" +
         "   edgeuserid = " + edgeuser + "\n" +
         "   email      = " + email    + "\n" +
         "   firstname  = " + first    + "\n" +
         "   lastname   = " + last     + "\n" +
         "   POCCompany = " + poccomp  + "\n" +
         "   AMTCompany = " + amtcomp  + "\n" +
         "   IBMDept    = " + ibmdept  + "\n" +
         "   IBMDiv     = " + ibmdiv   + "\n" +
         "   company    = " + company  + "\n" +
         "   country    = " + country  + "\n" +
         "   state      = " + state    + "\n" +
         "   Entitlements<" + 
         (entitlements != null?entitlements.size():0) + ">:\n";
      
      if (entitlements != null) {
         Enumeration enum = entitlements.elements();
         while(enum.hasMoreElements()) {
            String s = (String)enum.nextElement();
            ret += "      " + s + "\n";
         }
      }
      
      ret += "   Projects<" + (projects != null?projects.size():0) + ">:\n";
      
      if (projects != null) {
         Enumeration enum = projects.elements();
         while(enum.hasMoreElements()) {
            String s = (String)enum.nextElement();
            ret += "      " + s + "\n";
         }
      }
         
      return ret + "}";
   }
}
