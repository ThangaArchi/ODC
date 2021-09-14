/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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
/*
 * Created on Jul 18, 2006
 *
 * Author:Amareswara Kathi @ amakathi@in.ibm.com
 */
package oem.edge.ets.fe.documents.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;

import oem.edge.amt.AMTException;
import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.common.EtsLogger;


/**
 * @author amar
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DocExpiryNotificationDAO
{
	
	/** Stores the Logging object */
	private static final Log m_pdLog =
		EtsLogger.getLogger(DocExpiryNotificationDAO.class);
	/**
	 * @param rsDoc
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public ETSDoc getDoc(ResultSet rsDoc) throws SQLException {
		ETSDoc udDoc = new ETSDoc();

		/*
	   
	    These are the columns in the database table ETS_DOC 
		
		DOC_ID,CAT_ID,DOC_NAME,DOC_DESCRIPTION,DOC_KEYWORDS
		DOC_SIZE,DOC_TYPE,DOC_UPLOAD_DATE,DOC_PUBLISH_DATE
		DOC_UPDATE_DATE,UPDATED_BY,PROJECT_ID,HAS_PREV_VERSION
		LATEST_VERSION,LOCK_FINAL_FLAG,LOCKED_BY,DELETE_FLAG
		DELETION_DATE,DELETED_BY,MEETING_ID,IBM_ONLY,USER_ID
		PMO,DOCUMENT_STATUS,APPROVAL_COMMENTS,APPROVE_DATE
		EXPIRY_DATE,APPROVER_ID,SELF_ID,ISPRIVATE,IBM_CONF
		ITAR_UPLOAD_STATUS,ISSUE_ID
		
		 */
		
		udDoc.setId(rsDoc.getInt("DOC_ID"));
		udDoc.setProjectId(rsDoc.getString("PROJECT_ID"));
		udDoc.setUserId(rsDoc.getString("USER_ID"));
		udDoc.setName(rsDoc.getString("DOC_NAME"));
		udDoc.setCatId(rsDoc.getInt("CAT_ID"));
		udDoc.setDescription(rsDoc.getString("DOC_DESCRIPTION"));
		udDoc.setSize(rsDoc.getInt("DOC_SIZE"));
		udDoc.setUploadDate(rsDoc.getTimestamp("DOC_UPLOAD_DATE"));
		udDoc.setUpdateDate(rsDoc.getTimestamp("DOC_UPDATE_DATE"));
		udDoc.setPublishDate(rsDoc.getTimestamp("DOC_PUBLISH_DATE"));
		//		m_pdLog.debug("FILE NAME===" + rsDoc.getString("DOCFILE_NAME"));
		//		udDoc.setFileName(rsDoc.getString("DOCFILE_NAME"));
		//		udDoc.setFileUpdateDate(rsDoc.getTimestamp("DOCFILE_UPDATE_DATE"));
		udDoc.setKeywords(rsDoc.getString("DOC_KEYWORDS"));
		udDoc.setDocType(rsDoc.getInt("DOC_TYPE"));
		udDoc.setUpdatedBy(rsDoc.getString("UPDATED_BY"));
		udDoc.setHasPreviousVersion(rsDoc.getString("HAS_PREV_VERSION"));
		udDoc.setIsLatestVersion(rsDoc.getString("LATEST_VERSION"));
		udDoc.setLockFinalFlag(rsDoc.getString("LOCK_FINAL_FLAG"));
		udDoc.setLockedBy(rsDoc.getString("LOCKED_BY"));
		udDoc.setDeleteFlag(rsDoc.getString("DELETE_FLAG"));
		udDoc.setDeletedBy(rsDoc.getString("DELETED_BY"));
		udDoc.setMeetingId(rsDoc.getString("MEETING_ID"));
		udDoc.setIbmOnly(rsDoc.getString("IBM_ONLY"));
		//udDoc.setDocHits(rsDoc.getInt("hits"));
		udDoc.setDPrivate(rsDoc.getString("ISPRIVATE"));

		udDoc.setIBMConfidential(rsDoc.getString("IBM_CONF"));

		if (rsDoc.getTimestamp("EXPIRY_DATE") != null) {
			udDoc.setExpiryDate(rsDoc.getTimestamp("EXPIRY_DATE"));
		} else {
			udDoc.setExpiryDate(0);
		}

		return udDoc;
	}

	/**
	 * 
	 * 
	 * @param iDaysToExpire
	 * @return returns a Vector object containing ETSDoc object as an element
	 * 
	 * 
	 */	
public Vector getExpiryEtsDocs(int iDaysToExpire) throws SQLException , Exception
	{
			
		Vector etsDocVector = new Vector();
		
		Connection conn = ETSDBUtils.getConnection();
		
		String etsDocQuery = "select DOC_ID,CAT_ID,DOC_NAME,DOC_DESCRIPTION, "+
								"DOC_KEYWORDS, DOC_SIZE, DOC_TYPE, DOC_UPLOAD_DATE, DOC_PUBLISH_DATE, "+
								"DOC_UPDATE_DATE, UPDATED_BY, PROJECT_ID, HAS_PREV_VERSION, "+
								"LATEST_VERSION, LOCK_FINAL_FLAG, LOCKED_BY, DELETE_FLAG, DELETION_DATE,"+
								" DELETED_BY, MEETING_ID, IBM_ONLY, USER_ID, PMO, DOCUMENT_STATUS, "+
								"APPROVAL_COMMENTS, APPROVE_DATE, EXPIRY_DATE, APPROVER_ID, SELF_ID, ISPRIVATE, "+
								"IBM_CONF, ITAR_UPLOAD_STATUS, ISSUE_ID,  "+
								" (days(EXPIRY_DATE) - days(current timestamp))"+
			  					" as dateDiff from ets.ets_DOC"+
	 		 					" where (days(EXPIRY_DATE)- days(current timestamp)) = ? and latest_version='1' "+
								" and delete_flag !='1' and project_id in (select Project_id from ets.ets_projects "+
								" where project_status != 'D') with ur";
	   				
		System.out.println(etsDocQuery);
		m_pdLog.debug(etsDocQuery);
		
		PreparedStatement docQueryStmt = null;
	   	ResultSet docsResultSet = null;
	   try 
		   {
			docQueryStmt = conn.prepareStatement(etsDocQuery);
		   	docQueryStmt.setString(1, new Integer(iDaysToExpire).toString());
		   	docsResultSet = docQueryStmt.executeQuery();
		   	
		   	while(docsResultSet.next())
		   		{
		   		ETSDoc etsDoc = getDoc(docsResultSet);		   		
		   		etsDocVector.add(etsDoc);
		   		}

		   }//try block 
		catch (SQLException e) 
		   {
			e.printStackTrace();
		   }
		catch(Exception ex)
		   {
		   	ex.printStackTrace();
		   }
		finally
		{
			try
			{
				docsResultSet.close();
				docsResultSet = null;
				
				docQueryStmt.close();
				docQueryStmt = null;
				
				ETSDBUtils.close(conn);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		System.out.println("doc vector size===>" + etsDocVector.size());
		m_pdLog.debug("doc vector size===>" + etsDocVector.size());
		
		return etsDocVector;
	}
/**
 * This method gives an arraylist of the managers and authors and owners for the give projectId.
 * 
 * @param sProjectId
 * @return
 * @throws SQLException
 * @throws Exception
 */
	
public ArrayList getProjManagersList(String sProjectId) throws SQLException, Exception
{
	 Connection conn = null;
	 
	 conn = ETSDBUtils.getConnection();
	 
	 ArrayList managersList = new ArrayList();
	 
	 String managersQuery = "select a.user_id, b.role_name  from ets.ets_users a, ets.ets_roles b"+
	 						" where a.user_project_id = '" + sProjectId + "'"+
							" and a.user_role_id = b.role_id"+
							" and a.user_project_id = b.project_id"+
							" and a.active_flag='A'"+
							" and b.priv_id =" + Defines.OWNER;
	 System.out.println("getManagersList::" + managersQuery);
	 m_pdLog.debug("getManagersList:: "+managersQuery);
	 
	  	Statement roleStmt = null;
	  	ResultSet  rs = null;
	  try
	  {
	  	roleStmt = conn.createStatement();
	  	rs = roleStmt.executeQuery(managersQuery);
	  	
	  	while(rs.next())
	  	{
	  		managersList.add(rs.getString("user_id"));
	  	}
	  } catch (SQLException e) {
		  throw e;
	  } catch (Exception ex) {
		  throw ex;
	  } finally {
		//dbConnect.closeConn();
	  	rs.close();
	  	rs = null;
	  	
	  	roleStmt.close();
	  	roleStmt = null;
	  	
	  	ETSDBUtils.close(conn);
	  }
	
	
	return managersList;
}

public ArrayList getRestrictedEditUsersList(int iDocId, String sProjectId) throws SQLException, Exception
{
	 Connection conn = null;
	 
	 conn = ETSDBUtils.getConnection();
	 
	 ArrayList restrictedUsersList = new ArrayList();
	 
	 String restrictedUsersQuery = "select a.user_id  "+
	 							   "from ets.ets_private_doc a , "+
								   "ets.ets_users b ,"+
								   "ets.ets_roles c "+
								   "where a.user_id = b.user_id  "+
								   "and   a.project_id = b.user_project_id  "+
								   "and   b.user_project_id = c.project_id " +
								   "and   b.active_flag = 'A' " +
								   "and   a.doc_id ="+ iDocId + " "+
								   "and   a.access_type = 'E' "+
								   "and   a.project_id ="+"'"+sProjectId+"' "+
								   "and   b.user_role_id = c.role_id  "+
								   "and   c.priv_id = 2";
	 System.out.println("getRestrictedEditUsersList::" + restrictedUsersQuery);
	 m_pdLog.debug("getRestrictedEditUsersList:: " + restrictedUsersQuery);

	 Statement roleStmt = null;
	 ResultSet  rs = null;
	 try
	  {
	  	roleStmt = conn.createStatement();
	  	rs = roleStmt.executeQuery(restrictedUsersQuery);
	  	
	  	while(rs.next())
	  	{
	  		restrictedUsersList.add(rs.getString("user_id"));
	  	}
	  }
	 catch (SQLException e)
	  {
		  throw e;
	  } 
	 catch (Exception ex)
	  {
		  throw ex;
	  }
	 finally
	 {
	  	rs.close();
	  	rs = null;
	  	
	  	roleStmt.close();
	  	roleStmt = null;
	  	
	  	ETSDBUtils.close(conn);
	  }
	 
	 return restrictedUsersList;
}

public ArrayList getRestrictedEditGroupsList(int iDocId, String sProjectId) throws SQLException, Exception
{
	 Connection conn = null;
	 
	 conn = ETSDBUtils.getConnection();
	 
	 ArrayList restrictedGroupsList = new ArrayList();
	 
	 String restrictedGroupsQuery = "select a.group_id "+
	 								"from ets.ets_private_doc a , "+
									"ets.groups b "+ 
     								"where a.group_id = b.group_id "+
									"and   a.project_id = b.project_id "+
									"and   a.doc_id ="+iDocId+"  "+
									"and   a.access_type = 'E' "+
									"and   a.project_id ='"+sProjectId+"' " ;
	 System.out.println("restrictedGroupsQuery::" + restrictedGroupsQuery);
	 m_pdLog.debug("restrictedGroupsQuery:: "+restrictedGroupsQuery);
	 
	 Statement roleStmt = null;
	 ResultSet  rs = null;

	 try
	  {
	  	roleStmt = conn.createStatement();
	  	
	  	rs = roleStmt.executeQuery(restrictedGroupsQuery);
	  	
	  	while(rs.next())
	  	{
	  		restrictedGroupsList.add(rs.getString("group_id"));
	  	}
	  }
	 catch (SQLException e)
	  {
		  throw e;
	  } 
	 catch (Exception ex)
	  {
		  throw ex;
	  }
	 finally
	 {
		rs.close();
	 	rs = null;
	 	
	 	roleStmt.close();
	 	roleStmt = null;
	 		 	
	 	//dbConnect.closeConn();
		 ETSDBUtils.close(conn);
	  }
	 
	 return restrictedGroupsList;
}

public ArrayList getEditGroupUsersList(String sGroupId, String sProjectId) throws SQLException, Exception
{
	Connection conn = null;
	 
	conn = ETSDBUtils.getConnection();
	 
	ArrayList editGroupUsersList = new ArrayList();
	
	String editGroupUsersQuery = "select a.user_id "+
								 "from ets.user_groups a, "+
								 "ets.ets_users b , "+
								 "ets.ets_roles c "+
								 "where a.user_id = b.user_id "+
								 "and   a.group_id ='"+ sGroupId+"' "+
								 "and   b.user_project_id ='"+sProjectId+"' "+
								 "and   b.active_flag = 'A' "+
								 "and   b.user_role_id = c.role_id "+
								 "and   c.priv_id = 2";
	System.out.println("editGroupUsersQuery::" + editGroupUsersQuery);
	m_pdLog.debug("editGroupUsersQuery:: "+editGroupUsersQuery);
	
	Statement roleStmt = null;
	ResultSet rs = null;
	try
	  {
		roleStmt = conn.createStatement();
		rs = roleStmt.executeQuery(editGroupUsersQuery);
	  	
	  	while(rs.next())
	  	{
	  		editGroupUsersList.add(rs.getString("user_id"));
	  	}
	  }
	 catch (SQLException e)
	  {
		  throw e;
	  } 
	 catch (Exception ex)
	  {
		  throw ex;
	  }
	 finally
	 {
	  	rs.close();
	  	rs = null;
	  	
	  	roleStmt.close();
	  	roleStmt = null;
	  	
		  ETSDBUtils.close(conn);
	  }
	 
	 return editGroupUsersList;
}

/**
 * @param vtUsers
 * @return
 */
public ArrayList getIBMMembers(ArrayList vtUsers) throws SQLException, Exception {
	
	ArrayList vtIBMMembers = new ArrayList();

	Connection m_pdConnection = ETSDBUtils.getConnection();
	
	if (vtUsers == null) 
	{
		return new ArrayList();
	}
	try
	{
	Iterator usersIterator = vtUsers.iterator();
	
	while(usersIterator.hasNext())
	
	{
		String udMember = (String) usersIterator.next();
		try {
			String edge_userid =
				AccessCntrlFuncs.getEdgeUserId(
					m_pdConnection,
					udMember);
			String decaftype =
				AccessCntrlFuncs.decafType(edge_userid, m_pdConnection);
			
			if (decaftype.equals("I")) {
				vtIBMMembers.add(udMember);
			}
		} 
		catch (AMTException a)
		{
			a.printStackTrace();
		} 
		catch (SQLException s)
		{
			s.printStackTrace();
		}
		
	}
	}	
	catch(Exception ex)
	   {
	   	ex.printStackTrace();
	   }
	finally
	{
		try
		{
			ETSDBUtils.close(m_pdConnection);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	

	return vtIBMMembers;
}

//--------getDocNotifyList


/**
 * @param strProjectId
 * @param vtNotifyList
 * @return
 * @throws SQLException In case of database errors
 */
public String getProjMemberEmails(String strProjectId, ArrayList vtNotifyList)
 		throws SQLException, Exception
{
	Connection m_pdConnection = ETSDBUtils.getConnection();
	
	StringBuffer strEmailIDs = null;
	
	PreparedStatement stmtProjMembers = null;
	ResultSet rsProjMembers = null;
	try
	{
		stmtProjMembers =
			m_pdConnection.prepareStatement(
			   "select a.user_email, u.user_id, a.user_fname, a.user_lname "
				+ "from ETS.ETS_USERS u, AMT.USERS a "
				+ "where u.user_project_id = ?"
				+ " and u.user_id = a.ir_userid "
				+ "and u.active_flag='A' "
				+ "order by a.USER_FULLNAME with ur");

	stmtProjMembers.setString(1, strProjectId);
	rsProjMembers = stmtProjMembers.executeQuery();

	strEmailIDs = new StringBuffer("");
 
	List lstUserIds = new ArrayList();
	
	if (vtNotifyList != null && vtNotifyList.size() > 0) 
	{
		for(int i=0; i < vtNotifyList.size(); i++) 
		{
			String userId = (String) vtNotifyList.get(i);
			lstUserIds.add(userId.trim());
		}
	}
 
	while (rsProjMembers.next()) 
	{
		String strUser = rsProjMembers.getString("user_id");
		String strEmailID = rsProjMembers.getString("user_email");
		
		if (lstUserIds.size() > 0)
		{
			if (lstUserIds.contains(strUser))
			{
				strEmailIDs.append(strEmailID.trim());
				strEmailIDs.append(","); 
			}
		}
		else 
		{
			strEmailIDs.append(strEmailID);
			strEmailIDs.append(","); 
		}
	}
	}
	catch (SQLException e) 
	   {
		e.printStackTrace();
	   }
	catch(Exception ex)
	   {
	   	ex.printStackTrace();
	   }
	finally
	{
		try
		{
			rsProjMembers.close();
			rsProjMembers = null;
			
			stmtProjMembers.close();
			stmtProjMembers = null;
			
			ETSDBUtils.close(m_pdConnection);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	 
	return strEmailIDs.substring(0, strEmailIDs.length());
}

}