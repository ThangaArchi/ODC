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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;

import org.apache.commons.logging.Log;

/**
 * Class       : UserUtils
 * Package     : oem.edge.ets.fe.workflow.util
 * Description : 
 * Date		   : Oct 17, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class UserUtils {
	private static Log logger = WorkflowLogger.getLogger(UserUtils.class);

	/**
	 * @param role : S_ADMIN, WF_ADM_SETMET, WS_OWNER, WS_MGR, MEM, MEM_EXPL, MEM_IMPL, VISITOR
	 * @return
	 */
	public static ArrayList getUserByRoleName(String role, String projectID)
	{
	/* WS_OWNER
	 * WS_OWNER_ALL
	 * WS_MGR
	 * WS_MGR_ALL
	 * MEM
	 * MEM_EXPL
	 * MEM_IMPL
	 * VISITOR
	 * NO_VISITOR
	 */
		
		final int limit = 10;
		
		ArrayList users = new ArrayList();
		DBAccess db = null; 
		
		
		try{
		if(role.equalsIgnoreCase("S_ADMIN"))
		{
			//NOT YET DONE.
		}
		if(role.equalsIgnoreCase("WF_ADM_SETMET"))
		{
			//NOT YET DONE.
		}
		if(role.equalsIgnoreCase("WS_OWNER"))
		{
			Vector v = ETSDatabaseManager.getProjMembers(projectID);
			db = new DBAccess(); 
			for(int i = 0; i < v.size(); i++)
			{
				if(ETSUtils.getUserRole(((ETSUser)v.get(i)).getUserId(),projectID,db.getConnection()).equals(Defines.WORKSPACE_OWNER))
					users.add(((ETSUser)v.get(i)).getUserId());
			}
			db.close();db=null;
		}
		if(role.equalsIgnoreCase("NO_VISITOR"))
		{
			Vector v = ETSDatabaseManager.getProjMembers(projectID);
			db = new DBAccess(); 
			for(int i = 0; i < v.size(); i++)
			{
				if(!(ETSUtils.getUserRole(((ETSUser)v.get(i)).getUserId(),projectID,db.getConnection()).equals(Defines.WORKSPACE_VISITOR)))
					users.add(((ETSUser)v.get(i)).getUserId());
			}
			db.close();db=null;
		}
		if(role.equalsIgnoreCase("WS_OWNER_ALL"))
		{
			ArrayList pids = getAllProjectIDs();
			ArrayList v = new ArrayList();
			for(int m = 0; m < pids.size() && m < limit  ; m++)
			{
				System.out.println("Getting Workspace owners for project "+(String)pids.get(m));
				v.addAll(getUserByRoleName("WS_OWNER",(String)pids.get(m)));
			}
		}
		if(role.equalsIgnoreCase("WS_MGR"))
		{
			Vector v = ETSDatabaseManager.getProjMembers(projectID);
			db = new DBAccess(); 
			for(int i = 0; i < v.size(); i++)
			{
				if(ETSUtils.getUserRole(((ETSUser)v.get(i)).getUserId(),projectID,db.getConnection()).equals(Defines.WORKSPACE_MANAGER))
					users.add(((ETSUser)v.get(i)).getUserId());
			}
			db.close();db=null;
		}
		if(role.equalsIgnoreCase("WS_MGR_ALL"))
		{
			ArrayList pids = getAllProjectIDs();
			ArrayList v = new ArrayList();
			for(int m = 0; m < pids.size() && m < limit ; m++)
			{
				System.out.println("Getting Workspace managers for project "+(String)pids.get(m));
				v.addAll(getUserByRoleName("WS_MGR",(String)pids.get(m)));
			}
		}

		if(role.equalsIgnoreCase("MEM"))
		{
			users = UserUtils.getUserByRoleName("MEM_EXPL",projectID);
			ArrayList temp = UserUtils.getUserByRoleName("MEM_IMPL",projectID);
			for(int i = 0; i<temp.size(); i++)
				users.add(temp.get(i));
		}
		if(role.equalsIgnoreCase("MEM_EXPL"))
		{
			Vector v = ETSDatabaseManager.getProjMembers(projectID);
			
			db = new DBAccess(); 
			for(int i = 0; i < v.size(); i++)
			{
				if(ETSUtils.getUserRole(((ETSUser)v.get(i)).getUserId(),projectID,db.getConnection()).equals(Defines.WORKSPACE_MEMBER))
					users.add(((ETSUser)v.get(i)).getUserId());
			}
			db.close();db=null;
		}
		if(role.equalsIgnoreCase("MEM_IMPL"))
		{
			//NOT YET DONE.	
		}
		if(role.equalsIgnoreCase("VISITOR"))
		{
			Vector v = ETSDatabaseManager.getProjMembers(projectID);
			db = new DBAccess(); 
			for(int i = 0; i < v.size(); i++)
			{
				if(ETSUtils.getUserRole(((ETSUser)v.get(i)).getUserId(),projectID,db.getConnection()).equals(Defines.WORKSPACE_VISITOR))
					users.add(((ETSUser)v.get(i)).getUserId());
			}
			db.close();db=null;
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(db!=null){
				try{
					 db.close();
					 db=null;
					
				}catch(Exception ex){
					
				}
			}
		}
		System.out.println("UserUtils says: "+role+"s in "+projectID+" are:");
		for(int i = 0; i< users.size(); i++)
			System.out.println(users.get(i));
		return users;
	}
	
	private static ArrayList getAllProjectIDs()
	{
		ArrayList u = new ArrayList();
		DBAccess db = null;
		try{
			db = new DBAccess();
			db.prepareDirectQuery("SELECT project_id FROM ETS.ETS_PROJECTS where project_or_proposal='P' and project_type='AIC'");
			int rows = db.execute();
			for(int i = 0; i< rows; i++)
			{
				u.add(db.getString(i,0));
			}
			db.close();
			db = null;
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(db!=null){
				try{
					 db.close();
					 db=null;
					
				}catch(Exception ex){
					
				}
			}
		}
		
		return u;
	}
//	method added by riyazuddin on 02-11-06
	
	public static boolean doesUserHaveAIC_Workflow_AdminCollabEntitlement(Connection con, String sUserId) throws SQLException, Exception {
		
		Statement stmt = null;
		ResultSet rs = null;
	
		StringBuffer sQuery = new StringBuffer("");
		
		int iCount = 0;
		boolean bSub = false;
		
		try {
			boolean isIBMer = ETSUtils.isIBMer(sUserId, con);
			
				sQuery.append("SELECT COUNT(ENTITLEMENT) FROM AMT.S_USER_ACCESS_VIEW WHERE USERID IN (SELECT EDGE_USERID FROM AMT.USERS WHERE IR_USERID='" + sUserId + "') AND ENTITLEMENT in ('AIC_Workflow_Admin') WITH UR");
			
				stmt = con.createStatement();
				rs = stmt.executeQuery(sQuery.toString());
			
				if (rs.next()) {
					iCount = rs.getInt(1);
				}
				System.out.println("user has got AIC_Workflow_Admin Entitlements --" + iCount);
				System.out.println("Entitlements query  " + sQuery);
				if (iCount > 0) {
					bSub = true;
				}
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}
		
		return bSub;
	}
	
	
	/**
	 * This supports only the NO_VISITOR role
	 */
	public static ArrayList getUserByRoleName(String role, String projectID, DBAccess db)
	{
		ArrayList users = new ArrayList();
		try{
		if(role.equalsIgnoreCase("NO_VISITOR"))
		{
			Vector v = ETSDatabaseManager.getProjMembers(projectID);
			for(int i = 0; i < v.size(); i++)
			{
				if(!(ETSUtils.getUserRole(((ETSUser)v.get(i)).getUserId(),projectID,db.getConnection()).equals(Defines.WORKSPACE_VISITOR)))
					users.add(((ETSUser)v.get(i)).getUserId());
			}
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("UserUtils says: "+role+"s in "+projectID+" are:");
		for(int i = 0; i< users.size(); i++)
			System.out.println(users.get(i));
		return users;
	}
}

