package oem.edge.ed.odc.util;

import java.util.Vector;

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

/**
* Interface to abstract access to the User Registry
*/
public interface UserRegistry {
  /** 
   * Returns a Vector of AMTUser objects which match the specified name. 
   *  Normally there should be 0 or 1 matches, but systems which allow multiple
   *  records for the same user (for whatever reason) are accomodated.
   *
   * @param name             login name for AMTUser record retrieval
   * @param regex            if true, name is regex, otherwise, static string
   * @param getentitlements  if true, all the users entitlements will be 
   *                         included
   * @param getprojects      if true, all the users projects will be 
   *                         included
   *
   */
   public Vector lookup(String name, 
                        boolean regex,
                        boolean getentitlements, 
                        boolean getprojects) throws DBException;
                     
  /** 
   * Returns a Vector of AMTUser objects which have the specified email 
   *  address. There can be 0 - n matches, as a single email address (person)  
   *  might own multiple IDs.
   *
   * @param name             email address for AMTUser record retrieval
   * @param regex            if true, name is regex, otherwise, static string
   * @param getentitlements  if true, all the users entitlements will be 
   *                         included
   * @param getprojects      if true, all the users projects will be 
   *                         included
   *
   */
   public Vector lookupByEmail(String email, 
                               boolean regex,
                               boolean getentitlements, 
                               boolean getprojects) throws DBException;
                            
  /** 
   * Returns a Vector of Strings ir AMTUser objects for users who have access
   *  to the specified project (based on retstring parm).
   *
   * @param projname         projname for access to specified project
   * @param retstring        true if this method should return strings 
   *                         else AMTUser objects
   *
   */
   public Vector lookupUsersWithProject(String projname, 
                                        boolean retstring) throws DBException; 
   
  /** 
   * Returns a Vector of String objects for users who have access
   *  to the specified project.
   *
   * @param projname         projname for access to specified project
   *
   */
   public Vector lookupUsersWithProject(String projname) throws DBException; 
}

