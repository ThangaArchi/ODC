package oem.edge.ed.odc.dropbox.common;


import  oem.edge.ed.odc.ftp.common.*;
import  oem.edge.ed.odc.dsmp.common.*;
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

/**
 * The Access list (Acl) object is used to describe ones permission to access
 *  a package within the dropbox. It also has an overloaded use to present
 *  the status of a users access to a file or package.
 */
public class AclInfo implements java.io.Serializable {
   protected byte   status        = DropboxGenerator.STATUS_NONE;
   protected String name          = "";
   protected String projname      = "";
   protected String company       = "";
   protected long    created      = 0;
   
  // This is a hack ... used for setting xferspeed with FileManager
   protected int    xferspeed     = 0;
   
  /**
   * Empty/default constructor.
   */
   public AclInfo() { }
   
  /**
   * Copy construtor
   * @param a Source AclInfo to copy 
   */
   public AclInfo(AclInfo a) {
      status    = a.status;
      name      = a.name;
      projname  = a.projname;
      company   = a.company;
      xferspeed = a.xferspeed;
      created   = a.created;
   }
   
  /**
   * Set the name for the Acl. For a User acl, it would be the userid,
   *  for a group, the group name and for a project, the project name
   * @param v ACL name to set
   */
   public void setAclName(String v)        { name      = v; }
   
  /**
   * @deprecated This field is no longer set
   */
   public void setAclProjectName(String v) { projname  = v; }
   
  /**
   * Set the company name for the file access acl
   * @param v company name to set into the acl
   */
   public void setAclCompany(String v)     { company   = v; }
   
  /**
   * @deprecated Use setAclType instead
   */
   public void setAclStatus(byte v)        { status    = v; }
   
  /**
   * Set the type of the ACL
   * @param v Type of the acl (user, group or project)
   */
   public void setAclType(byte v)        { status    = v; }
   
  /**
   * Internal use 
   */
   public void setXferRate(int v)          { xferspeed = v; }
   
  /**
   * Sets the time which the ACL was created/added to package
   * @param v Millisec since 70 GMT in which ACL was created
   */
   public void setAclCreateTime(long v)    { created   = v; }
   
  /**
   * Gets the name associated with the ACL
   * @return String Acl name
   */
   public String getAclName()         { return name;      }
   
  /**
   * @deprecated This field is no longer set
   */
   public String getAclProjectName()  { return projname;  }
   
  /**
   * Gets the companyname associated with the ACL. The company will be the
   *  empty string ("") if the acl type is different than STATUS_USER, or if
   *  the company is unknown.
   * @return String Acl company name
   */
   public String getAclCompany()      { return company;   }
   
  /**
   *  Get the type of the acl
   *  <ul>
   *    <li>STATUS_USER    - User acl</li>
   *    <li>STATUS_GROUP   - Group acl</li>
   *    <li>STATUS_PROJECT - Project acl</li>
   *  </ul>
   */
   public byte   getAclType()         { return status;    }
   
  /**
   * @deprecated Use getAclType instead
   */
   public byte   getAclStatus()       { return status;    }
   
  /**
   * Gets the byte per second transfer rate associated with the ACL. The will be 
   *  the value set into a file access record at the time the record was 
   *  created/modified.
   * <p>
   *  This field is only pertinent when querying package file acls
   * @return String Acl company name
   */
   public int    getXferRate()        { return xferspeed; }
   
  /**
   * Get the time (milliseconds since 70 GMT) which the ACL was created
   */
   public long   getAclCreateTime()   { return created;   }
   
   public String toString() {
      return "AclInfo" +
         Nester.nest("\naclname   = " + name  +
                     "\naclstatus = " + status +
                     "\nprojname  = " + projname +
                     "\ncompany   = " + company);
   }
   
   public int hashCode() {
      return name != null?name.hashCode():0;
   }
   
  /**
   * Compares status, aclname, projectname and company fields. If they all
   *  match, considered a match
   */
   public boolean equals(Object o) {
      int num = 0;
      if (o instanceof AclInfo) {
         AclInfo to = (AclInfo)o;
         if (status == to.status) {
         
            if (name != null && to.name != null) {
               if (!name.equals(to.name)) return false;
            } else if (name != to.name)   return false;
            
            if (projname != null && to.projname != null) {
               if (!projname.equals(to.projname)) return false;
            } else if (projname != to.projname)   return false;
            
            if (company != null && to.company != null) {
               if (!company.equals(to.company)) return false;
            } else if (company != to.company)   return false;
            return true;
         } 
      } 
      return false;
   }
}
