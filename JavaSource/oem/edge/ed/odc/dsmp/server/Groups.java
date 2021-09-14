package oem.edge.ed.odc.dsmp.server;

import oem.edge.ed.odc.dsmp.common.*;
import oem.edge.ed.odc.tunnel.common.DebugPrint;
import oem.edge.ed.odc.util.*;
import oem.edge.ed.util.*;

import java.util.*;

import java.sql.*;
import org.apache.log4j.Logger;


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
public class Groups {

   private static Logger log = 
      Logger.getLogger(Groups.class.getName());
   

   static public GroupInfo getGroup(String group,
                                    boolean returnMembers, 
                                    boolean returnAccess) throws DboxException {
      GroupInfo gi = null;
      
      if (group == null) group = "";
      
      Connection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;	
      StringBuffer sql = null;
      DBConnection dbconn = null;
      try {	
         dbconn = DBSource.getDBConnection("GROUPS");
         connection = dbconn.getConnection();
         sql = new StringBuffer
            ("SELECT GROUPNAME,OWNER,COMPANY,VISIBILITY,LISTABILITY,CREATED ");
         sql.append(" FROM EDESIGN.GROUPS where GROUPNAME=?");
         sql.append(" FOR READ ONLY WITH UR");
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setString(1, group);
         if (DebugPrint.doDebug())
            DebugPrint.println(DebugPrint.DEBUG, "Groups: getGroup: "+
                               sql.toString() + 
                               " group=" + group);
         
         rs=dbconn.executeQuery(pstmt);
         
         if (rs.next()) {
            gi = new GroupInfo();
            int i = 1;
            gi.setGroupName(rs.getString(i++));
            gi.setGroupOwner(rs.getString(i++));
            gi.setGroupCompany(rs.getString(i++));
            gi.setGroupVisibility(rs.getByte(i++));
            gi.setGroupListability(rs.getByte(i++));
            gi.setGroupCreated(rs.getTimestamp(i++).getTime());
            
            if (returnMembers) {
               try {
                  pstmt.close();
               } catch(SQLException e) {}
               pstmt = null;
               
               sql = new StringBuffer("SELECT USERID ");
               sql.append(" FROM EDESIGN.GROUPMEMBERS where GROUPNAME=?");
               sql.append(" FOR READ ONLY WITH UR");
               
               pstmt=connection.prepareStatement(sql.toString());
               pstmt.setString(1, group);
               if (DebugPrint.doDebug())
                  DebugPrint.println(DebugPrint.DEBUG, "Groups: getGroup: "+
                                     sql.toString() + 
                                     " group=" + group);
               
               rs=dbconn.executeQuery(pstmt);
               
               Vector members = gi.getGroupMembers();
               while (rs.next()) {
                  members.addElement(rs.getString(1));
               }
               gi.setGroupMembers(members);
            }
            
            if (returnAccess) {
               try {
                  pstmt.close();
               } catch(SQLException e) {}
               pstmt = null;
               
               sql = new StringBuffer("SELECT USERID ");
               sql.append(" FROM EDESIGN.GROUPACCESS where GROUPNAME=?");
               sql.append(" FOR READ ONLY WITH UR");
               
               pstmt=connection.prepareStatement(sql.toString());
               pstmt.setString(1, group);
               if (DebugPrint.doDebug())
                  DebugPrint.println(DebugPrint.DEBUG, "Groups: getGroup: "+
                                     sql.toString() + 
                                     " group=" + group);
               
               rs=dbconn.executeQuery(pstmt);
               
               Vector access = gi.getGroupAccess();
               while (rs.next()) {
                  access.addElement(rs.getString(1));
               }
               gi.setGroupAccess(access);
            }
         }
      } catch (SQLException e) {
         log.error("Groups.getGroup: SQL except doing: " + 
                   ((sql==null)?"":sql.toString()));         
         if (dbconn != null) dbconn.destroyConnection(connection);
         pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("Groups: getGroup:>> SQL failure getting group" + group, 
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         if (dbconn != null) dbconn.returnConnection(connection);
      }
      
      if (gi == null) {
         throw new DboxException("getGroup:>> Group does not exist: " + group, 
                                 0);
      }
      return gi;
   }

   static public GroupInfo getGroupWithAccess(UserInterface user, 
                                              String group,
                                              boolean returnMembers,
                                              boolean returnAccess)
      throws DboxException {
      
      GroupInfo gi = getGroup(group, true, true);
      if (!gi.getGroupOwner().equals(user.getName())    && 
          !gi.getGroupAccess().contains(user.getName()) &&
          !(gi.getGroupMembers().contains(user.getName()) &&
            gi.getGroupVisibility() >= CommonGenerator.GROUP_SCOPE_MEMBER)) {
         throw new DboxException("getGroupWithAccess:>> User '" + 
                                 user.getName() + 
                                 "' does not have access to group: " + group,
                                 0);
      }
      if (!returnAccess) {
         gi.setGroupAccess(new Vector());
         gi.setGroupAccessValid(false);
      }
      if (!returnMembers) {
         gi.setGroupMembers(new Vector());
         gi.setGroupMembersValid(false);
      }
      return gi;
   }
   
   static public Hashtable getMatchingGroups(UserInterface user,
                                             String  group,
                                             boolean regexSearch,
                                             boolean owner,
                                             boolean modify,
                                             boolean member,
                                             boolean visible,
                                             boolean listable,
                                      
                                             boolean returnGI,
                                             boolean returnMembers,
                                             boolean returnAccess)
      throws DboxException {
      
      Hashtable ret = new Hashtable();
      
     /*
      SELECT g.GROUPNAME FROM EDESIGN.GROUPS g,
                            EDESIGN.GROUPMEMBERS m 
                            EDESIGN.GROUPACCESS  a 
         WHERE      
   VLO               g.OWNER=? 
   VLA           OR (g.GROUPNAME=a.GROUPNAME AND a.USERID=?)
   M             OR (g.GROUPNAME=m.GROUPNAME AND m.USERID=?)
   V             OR (g.VISIBILITY='A' OR 
                     (g.VISIBILITY='M' AND g.GROUPNAME==m.GROUPNAME AND 
                      m.USERID=?))
   L             OR (g.LISTABILITY='A' OR 
                     (g.LISTABILITY='M' AND g.GROUPNAME==m.GROUPNAME AND 
                      m.USERID=?))
     */  
     
      if (group != null && group.trim().length() == 0) group = null;
         
      Connection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;	
      StringBuffer sql = null;
      DBConnection dbconn = null;
      try {	
         dbconn = DBSource.getDBConnection("GROUPS");
         connection = dbconn.getConnection();
         sql = new StringBuffer ("SELECT DISTINCT g.GROUPNAME FROM EDESIGN.GROUPS g,");
         sql.append("EDESIGN.GROUPMEMBERS m,EDESIGN.GROUPACCESS a WHERE "); 
         
         if (group != null) {
            if (regexSearch) {
               group = SearchEtc.sqlEscape(group, true);
               sql.append("g.GROUPNAME like ? ESCAPE '\\' AND (");
            } else {
               sql.append("g.GROUPNAME=? AND (");
            }
         } else {
            sql.append("(");
         }
         
         int i=0;
         if (owner || visible || listable) {
            sql.append(" g.OWNER=? "); i++;
         }
         
         if (modify || visible || listable) {
            if (i != 0) sql.append("OR ");
            sql.append("(g.GROUPNAME=a.GROUPNAME AND a.USERID=?) "); i++;
         }
         
         if (member) {
            if (i != 0) sql.append("OR ");
            sql.append("(g.GROUPNAME=m.GROUPNAME AND m.USERID=?) "); i++;
         }
         
         if (visible) {
            if (i != 0) sql.append("OR ");
            sql.append("(g.VISIBILITY=10 OR ");
            sql.append(" (g.VISIBILITY=5 AND g.GROUPNAME=m.GROUPNAME ");
            sql.append("  AND m.USERID=?)) "); i++;
         } else if (listable) {
           // Only check listability if we did not check visibility above
           // Cause its not listable if its not visible
           
            if (i != 0) sql.append("OR ");
            sql.append("((g.VISIBILITY=10 OR ");
            sql.append(" (g.VISIBILITY=5 AND g.GROUPNAME=m.GROUPNAME ");
            sql.append("  AND m.USERID=?)) AND "); 
            sql.append("(g.LISTABILITY=10 OR ");
            sql.append(" (g.LISTABILITY=5 AND g.GROUPNAME=m.GROUPNAME ");
            sql.append("  AND m.USERID=?))) "); i+=2;
         }
         
         sql.append(")");
         
         if (i == 0) {
            throw new DboxException("getMatchingGroups:>> No selection made!", 
                                    0);
         }
         
         sql.append(" FOR READ ONLY WITH UR");
         
         pstmt=connection.prepareStatement(sql.toString());
         
         int j=1;
         if (group != null) {
            pstmt.setString(j++, group);
            i++;
         }
         
         for(; j <= i; j++) {
            pstmt.setString(j, user.getName());
         }
         
         if (DebugPrint.doDebug())
            DebugPrint.println(DebugPrint.DEBUG, "Groups: getMatchingGroups: "+
                               sql.toString() + " user=" + user.getName());
         
         rs=dbconn.executeQuery(pstmt);
         
         while (rs.next()) {
            String s = rs.getString(1);
            if (returnGI) {
               ret.put(s, getGroup(s, returnMembers, returnAccess));
            } else {
               ret.put(s, s);
            }
         }
      } catch (SQLException e) {
         log.error("Groups.getMatchingGroups: SQL except doing: " + 
                   ((sql==null)?"":sql.toString()));         
         if (dbconn != null) dbconn.destroyConnection(connection);
         connection=null; pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("Groups: getMatchingGroups:>> SQL failure" +
                          " getting groups: " + user.getName(), 
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         if (dbconn != null) dbconn.returnConnection(connection);
      }
      
      return ret;
   }
   
   static public void createGroup(UserInterface owner, 
                                  String group) throws DboxException {
      if (group == null || group.length() == 0) {
         throw new DboxException("createGroup:>> Invalid group name: " + 
                                 group, 0);
         
      }
      
      GroupInfo gi = null;
      try {
         gi=getGroup(group, false, false);
      } catch(DboxException dbe) {
         ;
      }
      if (gi != null) {
         throw new DboxException("createGroup:>> Group already exists: " + 
                                 group, 0);
      }
      
      
      if (group.indexOf("/") >= 0 || group.indexOf("\\") >= 0) {
         throw new DboxException(
            "createGroup:>> Group name CANNOT contain separator chars [" +
            group + "]", 0);
      }
      
      Connection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;	
      StringBuffer sql = null;
      DBConnection dbconn = null;
      try {	
         dbconn = DBSource.getDBConnection("GROUPS");
         connection = dbconn.getConnection();
         sql = new StringBuffer
            ("INSERT INTO EDESIGN.GROUPS (GROUPNAME,OWNER,COMPANY,VISIBILITY,LISTABILITY) VALUES(?,?,?,?,?)");
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setString(1, group);
         pstmt.setString(2, owner.getName());
         pstmt.setString(3, owner.getCompany());
         pstmt.setByte(4, CommonGenerator.GROUP_SCOPE_OWNER);
         pstmt.setByte(5, CommonGenerator.GROUP_SCOPE_OWNER);
         
         if (DebugPrint.doDebug())
            DebugPrint.println(DebugPrint.DEBUG, "Groups: createGroup: "+
                               sql.toString() + 
                               " group=" + group +
                               " owner=" + owner.getName() +
                               " company=" + owner.getCompany());
         
         if (dbconn.executeUpdate(pstmt) != 1) {
            throw new DboxException("createGroup:>> Error creating group?? "
                                    + group, 0);
         }
         
      } catch (SQLException e) {
         log.error("Groups.createGroup: SQL except doing: " + 
                   sql==null?"":sql.toString());         
         if (dbconn != null) dbconn.destroyConnection(connection);
         connection=null;
         pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("Groups: createGroup:>> SQL failure creating group" + group, 
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         if (dbconn != null) dbconn.returnConnection(connection);
      }
   }
   
   static public void deleteGroup(UserInterface user, 
                                  String group) throws DboxException {
      Connection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;	
      StringBuffer sql = null;
      DBConnection dbconn = null;
      try {	
         dbconn = DBSource.getDBConnection("GROUPS");
         connection = dbconn.getConnection();
         sql = new StringBuffer
            ("DELETE FROM EDESIGN.GROUPS WHERE GROUPNAME=? AND OWNER=?");
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setString(1, group);
         pstmt.setString(2, user.getName());
            
         if (DebugPrint.doDebug())
            DebugPrint.println(DebugPrint.DEBUG, "Groups: deleteGroup: "+
                               sql.toString() + 
                               " group=" + group +
                               " owner=" + user.getName() +
                               " company=" + user.getCompany());
            
         if (dbconn.executeUpdate(pstmt) != 1) {
            throw new 
               DboxException("deleteGroup:>> Group doesn't exist or not owner: "
                             + group, 0);
         }
         
      } catch (SQLException e) {
         log.error("Groups.deleteGroup: SQL except doing: " + 
                   sql==null?"":sql.toString());         
         if (dbconn != null) dbconn.destroyConnection(connection);
         connection=null;
         pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("Groups: deleteGroup:>> SQL failure deleting group" + group, 
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         if (dbconn != null) dbconn.returnConnection(connection);
      }
   }   
   
   static public void modifyGroupAttributes(UserInterface owner, 
                                            String group,
                                            byte visibility,
                                            byte listability)
      throws DboxException {      
      Connection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;	
      StringBuffer sql = null;
      DBConnection dbconn = null;
      try {	
         
         boolean dovis = false;
         switch(visibility) {
            case CommonGenerator.GROUP_SCOPE_NONE:
               break;
//            case CommonGenerator.GROUP_SCOPE_ALL:
            case CommonGenerator.GROUP_SCOPE_MEMBER:
            case CommonGenerator.GROUP_SCOPE_OWNER:
               dovis = true;
               break;
            default:
               throw new 
                  DboxException("modifyGroupAttr:>> Visibility value invalid: "
                             + group + " " + visibility, 0);
         }
      
         boolean dolist = false;
         switch(listability) {
            case CommonGenerator.GROUP_SCOPE_NONE:
               break;
//            case CommonGenerator.GROUP_SCOPE_ALL:
            case CommonGenerator.GROUP_SCOPE_MEMBER:
            case CommonGenerator.GROUP_SCOPE_OWNER:
               dolist = true;
               break;
            default:
               throw new 
                  DboxException("modifyGroupAttr:>> Listability value invalid: "
                             + group + " " + listability, 0);
         }
         
         if (!dovis && !dolist) {
            return;
         }
         
         dbconn = DBSource.getDBConnection("GROUPS");
         connection = dbconn.getConnection();
      
         sql = new StringBuffer
            ("UPDATE EDESIGN.GROUPS SET ");
         if (dovis) {
            sql.append("VISIBILITY=");
            sql.append(visibility);
            if (dolist) sql.append(",");
         }
         if (dolist) {
            sql.append("LISTABILITY=");
            sql.append(listability);
         }
         
         sql.append(" WHERE GROUPNAME=? AND (OWNER=? OR ");
         sql.append(" GROUPNAME IN ");
         sql.append(" (SELECT a.GROUPNAME FROM EDESIGN.GROUPACCESS a ");
         sql.append(" WHERE a.GROUPNAME=? AND a.USERID=?))");
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setString(1, group);
         pstmt.setString(2, owner.getName());
         pstmt.setString(3, group);
         pstmt.setString(4, owner.getName());
            
         if (DebugPrint.doDebug())
            DebugPrint.println(DebugPrint.DEBUG, "Groups: modifyGroupAttr: "+
                               sql.toString() + 
                               " group=" + group +
                               " caller=" + owner.getName());
            
         if (dbconn.executeUpdate(pstmt) != 1) {
            throw new 
               DboxException("modifyGroupAttr:>> Group does not exist "
                             + " OR permission denied" + group, 0);
         }
         
      } catch (SQLException e) {
         log.error("Groups.modifyGroupAttr: SQL except doing: " + 
                   sql==null?"":sql.toString());         
         if (dbconn != null) dbconn.destroyConnection(connection);
         connection=null;
         pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("Groups: modGroupAttrs:>> SQL failure modifying attrs for group" + group, 
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         if (dbconn != null) dbconn.returnConnection(connection);
      }
   }
   
   static public void addGroupAcl(UserInterface owner, 
                                  String group,
                                  String name,
                                  boolean memberOrAccess)
      throws DboxException {
      Connection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;	
      StringBuffer sql = null;
      DBConnection dbconn = null;
      try {	
         dbconn = DBSource.getDBConnection("GROUPS");
         connection = dbconn.getConnection();
      
         String ganame = memberOrAccess
            ? "EDESIGN.GROUPMEMBERS"
            : "EDESIGN.GROUPACCESS";
         
         
         sql = new StringBuffer
            ("SELECT g.GROUPNAME FROM EDESIGN.GROUPS g,EDESIGN.GROUPACCESS a");
         sql.append(" WHERE g.GROUPNAME=? AND (g.OWNER=? OR ");
         sql.append(" (a.GROUPNAME=g.GROUPNAME AND a.USERID=?)) AND ");
         sql.append(" g.GROUPNAME NOT IN (SELECT aa.GROUPNAME FROM ");
         sql.append(ganame).append(" aa WHERE aa.GROUPNAME=g.GROUPNAME AND");
         sql.append(" aa.USERID=?");
         
         sql.append(" ) ");
         
         sql.append(" FOR READ ONLY WITH UR");
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setString(1, group);
         pstmt.setString(2, owner.getName());
         pstmt.setString(3, owner.getName());
         pstmt.setString(4, name);
         
         if (DebugPrint.doDebug())
            DebugPrint.println(DebugPrint.DEBUG, "Groups: addGroupAcl P1: "+
                               sql.toString() + 
                               " group=" + group +
                               " name=" + name +
                               " caller=" + owner.getName());
         rs=dbconn.executeQuery(pstmt);
         
         if (!rs.next()) {
            throw new 
               DboxException("addGroupAcl:>> Group does not exist, ACL already "
                             + "exists, OR permission denied" + group, 0);
         }
         
         try {
            pstmt.close();
         } catch(SQLException e) {}
         
         pstmt = null;
         
         sql = new StringBuffer("INSERT INTO ").append(ganame);
         sql.append(" (GROUPNAME,USERID) VALUES(?,?)");
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setString(1, group);
         pstmt.setString(2, name);
            
         if (DebugPrint.doDebug())
            DebugPrint.println(DebugPrint.DEBUG, "Groups: addGroupAcl P2: "+
                               sql.toString() + 
                               " group=" + group +
                               " name=" + name +
                               " caller=" + owner.getName());
            
         if (dbconn.executeUpdate(pstmt) != 1) {
            throw new 
               DboxException("addGroupAcl:>> Group or member does not exist "
                             + " OR permission denied: group=" + group
                             + " name=" + name, 0);
         }
         
      } catch (SQLException e) {
         log.error("Groups.addGroupAcl: SQL except doing: " + 
                   sql==null?"":sql.toString());         
         if (dbconn != null) dbconn.destroyConnection(connection);
         connection=null;
         pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("Groups: addGroupAcl:>> SQL failure adding "
                          + name + " to group: " 
                          + group,
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         if (dbconn != null) dbconn.returnConnection(connection);
      }
   }
   
   static public void removeGroupAcl(UserInterface owner, 
                                     String group,
                                     String name,
                                     boolean memberOrAccess)
      throws DboxException {
      Connection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;	
      StringBuffer sql = null;
      DBConnection dbconn = null;
      try {	
         dbconn = DBSource.getDBConnection("GROUPS");
         connection = dbconn.getConnection();
         
         sql = new StringBuffer
            ("DELETE FROM EDESIGN.GROUP");
         if (memberOrAccess) sql.append("MEMBERS am");
         else                sql.append("ACCESS  am");

         sql.append(" WHERE am.GROUPNAME=? AND am.USERID=? AND ");
         sql.append(" am.GROUPNAME IN ");
         
         sql.append(" (SELECT g.GROUPNAME FROM EDESIGN.GROUPS g ");
         sql.append(" WHERE g.GROUPNAME=? AND g.OWNER=? ");
                  
         sql.append(" UNION SELECT a.GROUPNAME FROM EDESIGN.GROUPACCESS a ");
         sql.append(" WHERE a.GROUPNAME=? AND a.USERID=? ");
         
         sql.append(") ");
         
         pstmt=connection.prepareStatement(sql.toString());
         pstmt.setString(1, group);
         pstmt.setString(2, name);
         pstmt.setString(3, group);
         pstmt.setString(4, owner.getName());
         pstmt.setString(5, group);
         pstmt.setString(6, owner.getName());
            
         if (DebugPrint.doDebug())
            DebugPrint.println(DebugPrint.DEBUG, "Groups: deleteGroupAcl: "+
                               sql.toString() + 
                               " group=" + group +
                               " name=" + name +
                               " caller=" + owner.getName());
            
         if (dbconn.executeUpdate(pstmt) != 1) {
            throw new 
               DboxException("deleteGroupAcl:>> Group or member does not exist "
                             + " OR permission denied" + group, 0);
         }
         
      } catch (SQLException e) {
         log.error("Groups.remGroupAcl: SQL except doing: " + 
                   sql==null?"":sql.toString());         
         if (dbconn != null) dbconn.destroyConnection(connection);
         connection=null;
         pstmt=null;
         DboxAlert.alert(e);  // Generic Alert
         throw new 
            DboxException("Groups: remGroupAcl:>> SQL failure reming "
                          + name + " from group: " 
                          + group,
                          DboxException.SQL_ERROR);
      } finally {
         if (pstmt!=null) try {pstmt.close();} catch (SQLException e) {}
         if (dbconn != null) dbconn.returnConnection(connection);
      }
   }
}
