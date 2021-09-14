package oem.edge.ed.odc.dsmp.server;

import  oem.edge.ed.odc.util.*;

import java.lang.*;
import java.util.*;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004,2005,2006                           */
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

public class UserInfo implements UserInterface {
   protected String name;
   protected Vector projects    = new Vector();
   protected String company   = null;
   protected String country   = null;
   protected String email     = null;
   
   protected String ibmdiv     = null;
   protected String ibmdept    = null;
   
   public UserInfo() {
   }
   
   public UserInfo(String name) {
      this.name = name;
   }
   public UserInfo(String name, Vector projs) {
      this.name = name;
      if (projs != null && projs.size() != 0) {
         Enumeration e = projs.elements();
         while(e.hasMoreElements()) {
            projects.addElement(e.nextElement());
         }
      }
   }
      
   public String  getName()         { return name;     }
   public void    setName(String s) { name = s;        }
   
   public String  getEmail()         { return email;    }
   public void    setEmail(String c) { email = c;       }
   
   public String  getCountry()            { return country==null?"":country;   }
   public void    setCountry  (String c)  { country = c;      }
   public String  getCompany()            { return company==null?"":company;   }
   public void    setCompany  (String c)  { company = c;      }
   
   public void   setIBMDept(String v)     { ibmdept      = v;                }
   public String getIBMDept()             { return ibmdept;                  }
   public void   setIBMDiv(String v)      { ibmdiv       = v;                }
   public String getIBMDiv()              { return ibmdiv;                   }
      
   public Vector  getProjects()     { return projects; } 
   public void    setProjects(Vector v)  { 
      synchronized(projects) {
         projects.clear();
         if (v != null) {
            projects.addAll(v);
         }
      }
   }
   
   public void    addProjects(Vector v)  { 
      if (v != null) {
         Enumeration e = v.elements();
         while(e.hasMoreElements()) {
            addProject((String)e.nextElement());
         }
      }
   }
   
   public void    addProject(String p)  { 
      boolean doit = true;
      synchronized (projects) {
         if (!projects.contains(p)) {
            projects.addElement(p);
         }
      }
   }
      
   public String toString() {
      String ret =  "UserInfo: " + getName() + 
                    " Company: " + getCompany() + 
                    " Country: " + getCountry() + 
                    " Email: " + getEmail() + 
                    " IBMDept: " + ibmdept +
                    " IBMDiv: " + ibmdiv   +
         Nester.nest("\nProjects:");
            
      Enumeration enum = projects.elements();
      int i=0;
      while(enum.hasMoreElements()) {
         if (++i > 15) {
            ret += Nester.nest("\n\t ... more than 15 ...");
            break;
         }
         ret += Nester.nest("\n\t" + enum.nextElement().toString());
      }
      return ret;
   }
}
