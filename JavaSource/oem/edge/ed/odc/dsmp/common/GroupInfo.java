package oem.edge.ed.odc.dsmp.common;

import oem.edge.ed.odc.util.*;

import java.lang.*;
import java.util.*;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004-2006                                     */
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
 * Bean returned for group queries.
 */
public class GroupInfo implements java.io.Serializable {
   protected String  name          = "";
   protected String  owner         = "";
   protected String  company       = "";
   protected long    created       = 0L;
   protected byte    visibility    = CommonGenerator.GROUP_SCOPE_NONE;
   protected byte    listability   = CommonGenerator.GROUP_SCOPE_NONE;
   protected boolean memvalid      = true;
   protected boolean accvalid      = true;
   protected Vector  members       = new Vector();
   protected Vector  access        = new Vector();
   
  /**
   * Copy construtor
   * @param gi Source GroupInfo to copy 
   */
   public GroupInfo(GroupInfo gi) {
      this.name  = gi.name;
      this.owner = gi.owner;
      this.company = gi.company;
      this.created = gi.created;
      this.visibility = gi.visibility;
      this.listability = gi.listability;
      this.memvalid = gi.memvalid;
      this.accvalid = gi.accvalid;
      if (gi.members != null) this.members  = (Vector)gi.members.clone();
      if (gi.access  != null) this.access   = (Vector)gi.access.clone();
   }
   
  /**
   * Empty/default constructor.
   */
   public GroupInfo() { }
   
  /**
   * Set the name of the group
   * @param v name of the group
   */
   public void setGroupName(String v)          { name        = v; }
   
  /**
   * Set the user id for owner of the group
   * @param v user id for owner of the group
   */
   public void setGroupOwner(String v)         { owner       = v; }
   
  /**
   * Set the company for owner of the group
   * @param v company for owner of the group
   */
   public void setGroupCompany(String v)       { company     = v; }
   
  /**
   * Set the milliseconds since 70 GMT when the group was created
   * @param v milliseconds since 70 GMT when the group was created
   */
   public void setGroupCreated(long v)         { created     = v; }
   
  /**
   * Set the visibility value for the group. 
   * @param v visibility value for the group. 
   */
   public void setGroupVisibility(byte v)      { visibility  = v; }
   
  /**
   * Set the listability value for the group. 
   * @param v listability value for the group. 
   */
   public void setGroupListability(byte v)     { listability = v; }
   
  /**
   * Set whether the members vector is complete
   * @param v True if members vector is complete, false otherwise
   */
   public void setGroupMembersValid(boolean v) { memvalid    = v; }
   
  /**
   * Set whether the members vector is complete
   * @param v True if access vector is complete, false otherwise
   */
   public void setGroupAccessValid(boolean v)  { accvalid    = v; }
   
  /**
   * Set the members of the group (vector of String - Userid)
   * @param v members of the group (vector of String - Userid)
   */
   public void setGroupMembers(Vector v)       { 
      members     = v; 
      memvalid    = true;
   }
   
  /**
   * Set the admins of the group (vector of String - Userid)
   * @param v admins of the group (vector of String - Userid)
   */
   public void setGroupAccess(Vector v)        { 
      access      = v; 
      accvalid    = true;
   }
   
  /**
   * Get the name of the group
   * @return Name of the group
   */
   public String  getGroupName()         { return name;        }
   
  /**
   * Get the user id for the owner of the group
   * @return user id for the owner of the group
   */
   public String  getGroupOwner()        { return owner;       }
   
  /**
   * Get the company name for the owner of the group
   * @return company name for the owner of the group
   */
   public String  getGroupCompany()      { return company;     }
   
  /**
   * Get the milliseconds since 70 GMT that the group was created
   * @return milliseconds since 70 GMT that the group was created
   */
   public long    getGroupCreated()      { return created;     }
   
  /**
   * Get the visibility value of the group
   * @return visibility value of the group
   */
   public byte    getGroupVisibility()   { return visibility;  }
   
  /**
   * Get the listability value of the group
   * @return listability value of the group
   */
   public byte    getGroupListability()  { return listability; }
   
  /**
   * Returns true if the members vector is complete, false otherwise
   * @return true if the members vector is complete, false otherwise
   */
   public boolean getGroupMembersValid() { return memvalid;    }
   
  /**
   * Returns true if the access vector is complete, false otherwise
   * @return true if the access vector is complete, false otherwise
   */
   public boolean getGroupAccessValid()  { return accvalid;    }
   
  /**
   * Returns true if the members vector is complete, false otherwise
   * @return true if the members vector is complete, false otherwise
   */
   public boolean isGroupMembersValid()  { return memvalid;    }
   
  /**
   * Returns true if the access vector is complete, false otherwise
   * @return true if the access vector is complete, false otherwise
   */
   public boolean isGroupAccessValid()   { return accvalid;    }
   
  /**
   * Get the vector containing the members (String userid) of the group (can be null)
   * @return vector containing the members (String userid) of the group (can be null)
   */
   public Vector  getGroupMembers()      { return members;     }
   
  /**
   * Get the vector containing the admins (String userid) of the group (can be null)
   * @return vector containing the admins (String userid) of the group (can be null)
   */
   public Vector  getGroupAccess()       { return access;      }
   
   public String toString() {
      String mems = "";
      String accs = "";
      
      if (members != null) {
         Enumeration venum = members.elements();
         while(venum.hasMoreElements()) {
            String v = (String)venum.nextElement();
            mems += "     " + v + "\n";
         }
      }
      
      if (access != null) {
         Enumeration venum = access.elements();
         while(venum.hasMoreElements()) {
            String v = (String)venum.nextElement();
            accs += "     " + v + "\n";
         }
      }
      
      return "GroupInfo" +
         Nester.nest("\ngroupname   = " + name  +
                     "\ngroupowner  = " + owner +
                     "\ncreated     = " + created + 
                     "\nvisibility  = " + visibility + 
                     "\nlistability = " + listability + 
                     "\nmemvalid    = " + memvalid + 
                     "\naccvalid    = " + accvalid +
                     "\nmembers  \n"    + mems + 
                     "\naccess   \n"    + accs
            );
   }
   
   public int hashCode() {
      return name != null?name.hashCode():0;
   }
   
  /**
   * Compare objects to test for a match. 
   * Compare groupname to determine a match.
   */
   public boolean equals(Object o) {
      int num = 0;
      if (o instanceof GroupInfo) {
         GroupInfo to = (GroupInfo)o;
         if (name != null && to.name != null) {
            if (!name.equals(to.name)) return false;
         } else if (name != to.name)   return false;
         
         return true;
      } 
      return false;
   }   
}
