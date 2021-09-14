/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2006                                     */
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


package oem.edge.ets.fe.workflow.util;

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;

import org.apache.commons.logging.Log;

/**
 * Class       : RoleUtils
 * Package     : oem.edge.ets.fe.workflow.util
 * Description : 
 * Date		   : Oct 17, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class RoleUtils {

	private static Log logger = WorkflowLogger.getLogger(RoleUtils.class);
	
	public static boolean isSuperAdmin(String userid, String projectID)
	{
		boolean flag = false;
		DBAccess db = null;
		try {
			db=new DBAccess();
			if((ETSUtils.getUserRole(userid,projectID,db.getConnection())).equals(Defines.ETS_ADMIN))
			{
				flag = true;
			}
			db.close();
			db=null;
		} catch (Exception e) {
			db.doRollback();
			e.printStackTrace();
		}finally{
			if(db!=null){
				try{
					 db.close();
					 db=null;
				}catch(Exception ex){
				}
				return false;
			}
		}
		return flag;
	}
	
	public static boolean isWFAdmin(String userid, String datatype)
	{
		return false;
	}
	
	public static boolean isWSOwner(String userid, String projectID)
	{
		boolean flag = false;
		DBAccess db = null;
		try {
			db=new DBAccess();
			if((ETSUtils.getUserRole(userid,projectID,db.getConnection())).equals(Defines.WORKSPACE_OWNER))
			{
				flag = true;
			}
			db.close();
			db=null;
		} catch (Exception e) {
			db.doRollback();
			e.printStackTrace();
		}finally{
			if(db!=null){
				try{
					 db.close();
					 db=null;
				}catch(Exception ex){
				}
				return false;
			}
		}
		return flag;
	
	}
	public static boolean isWSMgr(String userid, String projectID)
	{
		boolean flag = false;
		DBAccess db = null;
		try {
			db=new DBAccess();
			if((ETSUtils.getUserRole(userid,projectID,db.getConnection())).equals(Defines.WORKSPACE_MANAGER))
			{
				flag = true;
			}
			db.close();
			db=null;
		} catch (Exception e) {
			db.doRollback();
			e.printStackTrace();
		}finally{
			if(db!=null){
				try{
					 db.close();
					 db=null;
				}catch(Exception ex){
				}
				return false;
			}
		}
		return flag;
	
	}
	public static boolean isExplicitMember(String userid, String projectID)
	{
		boolean flag = false;
		DBAccess db = null;
		try {
			db=new DBAccess();
			if((ETSUtils.getUserRole(userid,projectID,db.getConnection())).equals(Defines.WORKSPACE_MEMBER))
			{
				flag = true;
			}
			db.close();
			db=null;
		} catch (Exception e) {
			db.doRollback();
			e.printStackTrace();
		}finally{
			if(db!=null){
				try{
					 db.close();
					 db=null;
				}catch(Exception ex){
				}
				return false;
			}
		}
		return flag;
	
	}
	public static boolean isImplicitMember(String useid, String projectID)
	{
		return false;
	}
	
	public static boolean isMember(String userid, String projectID)
	{
		return isExplicitMember(userid,projectID) || isImplicitMember(userid, projectID);
	}
	
	
	public static boolean isAccoutContact(String userid, String projectID, String wfID)
	{
		String  q="SELECT * FROM ETS.WF_DEF WHERE ACCT_CONTACT='"+userid+"' AND WF_ID='"+wfID+"' AND PROJECT_ID='"+projectID+"' WITH UR";
		int nrows = 0;
		
		DBAccess db = null;
		try {
			db=new DBAccess();
			db.prepareDirectQuery(q);
			System.out.println("DB:"+q);
			nrows = db.execute();
			System.out.println("DB:Done");
			db.close();
			db=null;
		} catch (Exception e) {
			db.doRollback();
			e.printStackTrace();
		}finally{
			if(db!=null){
				try{
					 db.close();
					 db=null;
				}catch(Exception ex){
				}
				return false;
			}
		}
		if(nrows > 0)
			return true;
		else
			return false;
		
	}
	public static boolean isBkupContact(String userid, String projectID, String wfID)
	{
		String  q="SELECT * FROM ETS.WF_DEF WHERE BACKUP_ACCT_CONTACT='"+userid+"' AND WF_ID='"+wfID+"' AND PROJECT_ID='"+projectID+"' WITH UR";
		int nrows = 0;
		
		DBAccess db = null;
		try {
			db=new DBAccess();
			db.prepareDirectQuery(q);
			System.out.println("DB:"+q);
			nrows = db.execute();
			System.out.println("DB:Done");
			db.close();
			db=null;
		} catch (Exception e) {
			db.doRollback();
			e.printStackTrace();
		}finally{
			if(db!=null){
				try{
					 db.close();
					 db=null;
				}catch(Exception ex){
				}
				return false;
			}
		}
		if(nrows > 0)
			return true;
		else
			return false;
		

	}
	
	public static boolean isIssueOwner(String userID, String issueID)
	{

		String q ="SELECT * FROM ETS.WF_ISSUE_OWNER WHERE OWNER_ID='"+userID+"' AND ISSUE_ID='"+issueID+"' WITH UR";
		
		int nrows = 0;
		
		DBAccess db = null;
		try {
			db=new DBAccess();
			db.prepareDirectQuery(q);
			System.out.println("DB:"+q);
			nrows = db.execute();
			System.out.println("DB:Done");
			db.close();
			db=null;
		} catch (Exception e) {
			db.doRollback();
			e.printStackTrace();
		}finally{
			if(db!=null){
				try{
					 db.close();
					 db=null;
				}catch(Exception ex){
				}
				return false;
			}
		}
		if(nrows > 0)
			return true;
		else
			return false;
	}
	public static boolean isTouchedOwner(String userID, String issueID)
	{

		String q ="SELECT OWNERSHIP_STATE FROM ETS.WF_ISSUE_OWNER WHERE OWNER_ID='"+userID+"' AND ISSUE_ID='"+issueID+"' WITH UR";
		
		int nrows = 0;
		boolean flag = false;
		DBAccess db = null;
		try {
			db=new DBAccess();
			db.prepareDirectQuery(q);
			System.out.println("DB:"+q);
			nrows = db.execute();
			if(nrows > 0)
				if(db.getString(0,0)!=null && db.getString(0,0).trim().length()!=0)
					flag = true;
			System.out.println("DB:Done");
			db.close();
			db=null;
		} catch (Exception e) {
			db.doRollback();
			e.printStackTrace();
		}finally{
			if(db!=null){
				try{
					 db.close();
					 db=null;
				}catch(Exception ex){
				}
				return false;
			}
		}
		return flag;
	}
	public static boolean isAcceptedOwner(String userID, String issueID)
	{

		String q ="SELECT OWNERSHIP_STATE FROM ETS.WF_ISSUE_OWNER WHERE OWNER_ID='"+userID+"' AND ISSUE_ID='"+issueID+"' WITH UR";
		
		int nrows = 0;
		boolean flag = false;
		DBAccess db = null;
		try {
			db=new DBAccess();
			db.prepareDirectQuery(q);
			System.out.println("DB:"+q);
			nrows = db.execute();
			if(nrows > 0)
				if(db.getString(0,0)!=null && db.getString(0,0).equalsIgnoreCase("ACCEPTED"))
					flag = true;
			System.out.println("DB:Done");
			db.close();
			db=null;
		} catch (Exception e) {
			db.doRollback();
			e.printStackTrace();
		}finally{
			if(db!=null){
				try{
					 db.close();
					 db=null;
				}catch(Exception ex){
				}
				return false;
			}
		}
		return flag;
	}
	
	public static boolean isIssueContact(String userID, String issueID)

	{
		
		String q = "SELECT * FROM ETS.WF_ISSUE WHERE ISSUE_CONTACT='"+userID+"' AND ISSUE_ID='"+issueID+"' WITH UR";
		
		int nrows = 0;
		
		DBAccess db = null;
		try {
			db=new DBAccess();
			db.prepareDirectQuery(q);
			System.out.println("DB:"+q);
			nrows = db.execute();
			System.out.println("DB:Done");
			db.close();
			db=null;
		} catch (Exception e) {
			db.doRollback();
			e.printStackTrace();
		}finally{
			if(db!=null){
				try{
					 db.close();
					 db=null;
				}catch(Exception ex){
				}
				return false;
			}
		}
		if(nrows > 0)
			return true;
		else
			return false;
	}
	
}



