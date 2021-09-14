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

package oem.edge.ets.fe.documents.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import oem.edge.common.DbConnect;
import oem.edge.common.Global;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.ETSDetailedObj;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.ETSProjectStatus;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.DocNotify;
import oem.edge.ets.fe.documents.common.ETSDocFile;
import oem.edge.ets.fe.documents.common.Group;
import oem.edge.ets.fe.documents.common.StringUtil;

import org.apache.log4j.Logger;

/**
 * This is the DATA ACCESS Class for the E&TS Documents Module
 * @author v2srikau
 */
public class DocReaderDAO {

	/** Stores the Logging object */
	private Logger m_pdLog = Logger.getLogger(DocReaderDAO.class);

	/** Stores the Connection Object */
	private Connection m_pdConnection = null;

	/**
	 * @throws Exception
	 */
	public void prepare() throws Exception {
		Connection pdConn = getDBConnection();
		setConnection(pdConn);
	}

	/**
	 * Closes and releases a connection
	 */
	public void cleanup() throws SQLException {
		if (m_pdConnection != null && !m_pdConnection.isClosed()) {
			m_pdConnection.close();
		}
		m_pdConnection = null;
	}
	/**
	 * @return
	 */
	public Connection getConnection() {
		return m_pdConnection;
	}

	/**
	 * @param connection
	 */
	public void setConnection(Connection pdConnection) {
		m_pdConnection = pdConnection;
	}

	/*
	Gets a new connection object.
	*/
	public static Connection getDBConnection() throws SQLException, Exception {

		Connection connection = null;
		DbConnect db = null;

		try {

			if (!Global.loaded) {
				Global.Init();
			}

			db = new DbConnect();
			db.makeConn(getDataSource());
			connection = db.conn;

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}

		return connection;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	private static String getDataSource() throws Exception {

		String sDataSource = "";

		try {

			ResourceBundle rb = ResourceBundle.getBundle("oem.edge.ets.fe.ets");

			if (rb == null) {
				sDataSource = "etsds";
				return sDataSource;
			}
			Enumeration e = rb.getKeys();

			sDataSource = rb.getString("ets.datasource");
			if (sDataSource == null || sDataSource.trim().equals("")) {
				sDataSource = "etsds";
			} else {
				sDataSource = sDataSource.trim();
			}

		} catch (Exception e) {
			throw e;
		}

		return sDataSource;
	}

	/**
	 * @param rsDoc
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public ETSDoc getDoc(ResultSet rsDoc) throws SQLException {
		ETSDoc udDoc = new ETSDoc();
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
			udDoc.setDocHits(rsDoc.getInt("hits"));
			
			udDoc.setDPrivate(rsDoc.getString("ISPRIVATE"));
			udDoc.setDPrivateEdit( hasAdditionalEditors( String.valueOf(udDoc.getId()), udDoc.getProjectId() ) );

			udDoc.setIBMConfidential(rsDoc.getString("IBM_CONF"));

			if (rsDoc.getTimestamp("EXPIRY_DATE") != null) {
				udDoc.setExpiryDate(rsDoc.getTimestamp("EXPIRY_DATE"));
			} else {
				udDoc.setExpiryDate(0);
			}
		return udDoc;
	}

	/**
	 * @param iDocID
	 * @param strProjectId
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public ETSDoc getDocByIdAndProject(
		int iDocID,
		String strProjectId,
		boolean bIsITAR)
		throws SQLException {
		Statement stmtDocDetails = m_pdConnection.createStatement();

		String strITARClause = " and itar_upload_status !='P'";
		if (bIsITAR) {
			strITARClause = StringUtil.EMPTY_STRING;	
		}
		
		String strQuery =
			"select d.*, "
				+ "(select count(m.doc_id) from ets.ets_doc_metrics m "
				+ "where d.doc_id=m.doc_id) as hits "
				+ "from ETS.ETS_DOC d "
				+ "where d.doc_id = "
				+ iDocID
				+ " and d.project_id = '"
				+ strProjectId
				+ "'"
				+ strITARClause
				+ " and delete_flag!='"
				+ DocConstants.TRUE_FLAG
				+ "' with ur";
		ResultSet rsDocDetails = stmtDocDetails.executeQuery(strQuery);

		ETSDoc udDoc = null;
		if (rsDocDetails.next()) {
			udDoc = getDoc(rsDocDetails);
		}

		rsDocDetails.close();
		stmtDocDetails.close();

		if (udDoc == null) {
			return udDoc;
		}

		// Populate Doc Files
		stmtDocDetails = m_pdConnection.createStatement();
		rsDocDetails =
			stmtDocDetails.executeQuery(
				"SELECT DOCFILE_ID, DOCFILE_NAME, "
					+ "DOCFILE_SIZE, DOCFILE_UPDATE_DATE, "
					+ "FILE_DESCRIPTION, FILE_STATUS "
					+ "FROM ETS.ETS_DOCFILE WHERE DOC_ID = "
					+ iDocID + " WITH UR");

		List lstDocFiles = new ArrayList();
		while (rsDocDetails.next()) {
			ETSDocFile udDocFile = new ETSDocFile();
			udDocFile.setDocfileId(rsDocDetails.getInt("DOCFILE_ID"));
			udDocFile.setFileName(rsDocDetails.getString("DOCFILE_NAME"));
			udDocFile.setSize(rsDocDetails.getInt("DOCFILE_SIZE"));
			udDocFile.setFileDescription(
				rsDocDetails.getString("FILE_DESCRIPTION"));
			udDocFile.setFileStatus(rsDocDetails.getString("FILE_STATUS"));
			lstDocFiles.add(udDocFile);
		}

		rsDocDetails.close();
		stmtDocDetails.close();

		udDoc.setDocFiles(lstDocFiles);
		populateUserName(udDoc);
		try {
	    	if (!StringUtil.isNullorEmpty(udDoc.getUpdatedBy())) {
			udDoc.setUpdatedBy(getUsersName(udDoc.getUpdatedBy()));
	    	}
		} catch (Exception e) {
		    m_pdLog.error(e);
		    throw new SQLException(e.getMessage());
		}
		return udDoc;
	}

	/**
	 * @param iDocID
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public ETSDoc getDocById(int iDocID) throws SQLException {
		Statement stmtDocDetails = m_pdConnection.createStatement();

		String strQuery =
			"select d.*, "
				+ "(select count(m.doc_id) from ets.ets_doc_metrics m "
				+ "where d.doc_id=m.doc_id) as hits "
				+ "from ETS.ETS_DOC d "
				+ "where d.doc_id = "
				+ iDocID
				+ " with ur";
		ResultSet rsDocDetails = stmtDocDetails.executeQuery(strQuery);

		ETSDoc udDoc = null;
		if (rsDocDetails.next()) {
			udDoc = getDoc(rsDocDetails);
		}

		rsDocDetails.close();
		stmtDocDetails.close();

		if (udDoc == null) {
			return udDoc;
		}

		// Populate Doc Files
		stmtDocDetails = m_pdConnection.createStatement();
		rsDocDetails =
			stmtDocDetails.executeQuery(
				"SELECT DOCFILE_ID, DOCFILE_NAME, "
					+ "DOCFILE_SIZE, DOCFILE_UPDATE_DATE, "
					+ "FILE_DESCRIPTION, FILE_STATUS "
					+ "FROM ETS.ETS_DOCFILE WHERE DOC_ID = "
					+ iDocID + " WITH UR");

		List lstDocFiles = new ArrayList();
		while (rsDocDetails.next()) {
			ETSDocFile udDocFile = new ETSDocFile();
			udDocFile.setDocfileId(rsDocDetails.getInt("DOCFILE_ID"));
			udDocFile.setFileName(rsDocDetails.getString("DOCFILE_NAME"));
			udDocFile.setSize(rsDocDetails.getInt("DOCFILE_SIZE"));
			udDocFile.setFileDescription(
				rsDocDetails.getString("FILE_DESCRIPTION"));
			udDocFile.setFileStatus(rsDocDetails.getString("FILE_STATUS"));
			lstDocFiles.add(udDocFile);
		}

		rsDocDetails.close();
		stmtDocDetails.close();

		udDoc.setDocFiles(lstDocFiles);
		populateUserName(udDoc);
		try {
	    	if (!StringUtil.isNullorEmpty(udDoc.getUpdatedBy())) {
			udDoc.setUpdatedBy(getUsersName(udDoc.getUpdatedBy()));
	    	}
		} catch (Exception e) {
		    m_pdLog.error(e);
		    throw new SQLException(e.getMessage());
		}
		return udDoc;
	}

	/**
	 * @param udObj
	 */
	public void populateUserName(ETSDetailedObj udObj) {
		try {
			udObj.setUserName(getUsersName(udObj.getUserId()));
		} catch (Exception e) {
			udObj.setUserName(udObj.getUserId());
		}
	}

	/**
	 * @param vtObjects
	 * @return
	 */
	private Vector populateUserNames(Vector vtObjects) {
		Vector vtPopulatedObjects = new Vector();
		for (int iCounter = 0; iCounter < vtObjects.size(); iCounter++) {
			ETSDetailedObj udObj = (ETSDetailedObj) vtObjects.get(iCounter);
			populateUserName(udObj);
			vtPopulatedObjects.add(udObj);
		}

		return vtPopulatedObjects;
	}


	/**
	 * @param iDocID
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public Vector getDocNotifyList(int iDocID) throws SQLException {
		Vector vtNotifyList = new Vector();

		String strQuery =
			"SELECT * FROM ETS.ETS_DOC_NOTIFY WHERE DOC_ID = ? WITH UR";
		PreparedStatement stmtNotify =
			m_pdConnection.prepareStatement(strQuery);

		stmtNotify.setInt(1, iDocID);

		ResultSet rsNotifyList = stmtNotify.executeQuery();
		// We know this resultset will only have one row
		DocNotify udNotifyObj = new DocNotify();
		if (rsNotifyList.next()) {
			String strNotifyAllFlag = rsNotifyList.getString("NOTIFY_ALL_FLAG");
			udNotifyObj.setDocID(iDocID);
			udNotifyObj.setNotifyAllFlag(strNotifyAllFlag);
			if (!udNotifyObj.isNotifyAll()) {
				String strList = rsNotifyList.getString("USER_ID");
				if (!StringUtil.isNullorEmpty(strList)) {
					if (strList.indexOf(StringUtil.COMMA) != -1) {
						StringTokenizer strTokens =
							new StringTokenizer(strList, StringUtil.COMMA);
						while (strTokens.hasMoreTokens()) {
							udNotifyObj = new DocNotify();
							udNotifyObj.setDocID(iDocID);
							udNotifyObj.setNotifyAllFlag(strNotifyAllFlag);
							udNotifyObj.setUserId(strTokens.nextToken());
							vtNotifyList.add(udNotifyObj);
						}
					} else {
						udNotifyObj = new DocNotify();
						udNotifyObj.setDocID(iDocID);
						udNotifyObj.setNotifyAllFlag(strNotifyAllFlag);
						udNotifyObj.setUserId(strList);
						vtNotifyList.add(udNotifyObj);
					}
				}

				// Get groups
				strList = rsNotifyList.getString("GROUP_ID");
				if (!StringUtil.isNullorEmpty(strList)) {
					if (strList.indexOf(StringUtil.COMMA) != -1) {
						StringTokenizer strTokens =
							new StringTokenizer(strList, StringUtil.COMMA);
						while (strTokens.hasMoreTokens()) {
							udNotifyObj = new DocNotify();
							udNotifyObj.setDocID(iDocID);
							udNotifyObj.setNotifyAllFlag(strNotifyAllFlag);
							udNotifyObj.setGroupId(strTokens.nextToken());
							vtNotifyList.add(udNotifyObj);
						}
					} else {
						udNotifyObj = new DocNotify();
						udNotifyObj.setDocID(iDocID);
						udNotifyObj.setNotifyAllFlag(strNotifyAllFlag);
						udNotifyObj.setGroupId(strList);
						vtNotifyList.add(udNotifyObj);
					}
				}
			} else {
				udNotifyObj.setUserId(null);
				vtNotifyList.add(udNotifyObj);
			}
		}

		rsNotifyList.close();
		stmtNotify.close();

		return vtNotifyList;
	}

	/**
	 * @param strIRID
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public String getUsersName(String strIRID) throws SQLException, Exception {

		PreparedStatement stmtUserName = null;
		ResultSet rsUserName = null;
		String sUserName = StringUtil.EMPTY_STRING;

		try {
			String strQuery =
				"SELECT LTRIM(RTRIM(USER_FNAME)) "
					+ "|| ' ' || "
					+ "LTRIM(RTRIM(USER_LNAME)) "
					+ "FROM AMT.USERS WHERE IR_USERID = ? for READ ONLY";

			if (m_pdLog.isDebugEnabled()) {
				m_pdLog.debug("QUERY : " + strQuery);
			}

			stmtUserName = m_pdConnection.prepareStatement(strQuery);
			stmtUserName.setString(1, strIRID);

			rsUserName = stmtUserName.executeQuery();

			if (rsUserName.next()) {
				sUserName = rsUserName.getString(1);
				if (StringUtil.isNullorEmpty(sUserName)) {
					sUserName = StringUtil.EMPTY_STRING;
				}
			} else {
				sUserName = strIRID;
			}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			rsUserName.close();
			stmtUserName.close();
		}

		return sUserName;

	}

	/**
	 * @param iCatID
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public ETSCat getCat(int iCatID) throws SQLException {
		PreparedStatement stmtGetCat =
			m_pdConnection.prepareStatement(
				"select * from ETS.ETS_CAT where cat_id=? with ur");
		if (m_pdLog.isDebugEnabled()) {
			m_pdLog.debug(
				"******select * from ETS.ETS_CAT where cat_id=? with ur");
			m_pdLog.debug("******Param {1} : " + iCatID);
		}
		stmtGetCat.setInt(1, iCatID);
		ResultSet rsGetCat = stmtGetCat.executeQuery();
		ETSCat udCat = null;

		while (rsGetCat.next()) {
			udCat = getCat(rsGetCat);
		}

		stmtGetCat.close();
		return udCat;
	}


	/**
	 * @param iCatID
	 * @param strProjectID
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public ETSCat getCat(int iCatID, String strProjectID) throws SQLException {
		PreparedStatement stmtCat =
			m_pdConnection.prepareStatement(
				"select * from ETS.ETS_CAT "
					+ "where cat_id=? and project_id=? with ur");
		stmtCat.setInt(1, iCatID);
		stmtCat.setString(2, strProjectID);
		ResultSet rsCat = stmtCat.executeQuery();
		ETSCat udCat = null;

		while (rsCat.next()) {
			udCat = getCat(rsCat);
		}

		stmtCat.close();
		return udCat;
	}


	/**
	 * @param rs
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public ETSCat getCat(ResultSet rsSubCat) throws SQLException {
		ETSCat udCat = new ETSCat();

		udCat.setId(rsSubCat.getInt("CAT_ID"));

		//spn 0312 projid
		udCat.setProjectId(rsSubCat.getString("PROJECT_ID"));
		udCat.setUserId(rsSubCat.getString("USER_ID"));
		udCat.setName(rsSubCat.getString("CAT_NAME"));
		udCat.setParentId(rsSubCat.getInt("PARENT_ID"));
		udCat.setCatType(rsSubCat.getInt("CAT_TYPE"));
		udCat.setDescription(rsSubCat.getString("CAT_DESCRIPTION"));
		udCat.setOrder(rsSubCat.getInt("ORDER"));
		udCat.setViewType(rsSubCat.getInt("VIEW_TYPE"));
		udCat.setProjDesc(rsSubCat.getInt("PROJ_DESC"));
		//cat.setProjMsg(rs.getInt("PROJ_MSG"));
		udCat.setPrivs(rsSubCat.getString("PRIVS"));
		udCat.setLastTimestamp(rsSubCat.getTimestamp("LAST_TIMESTAMP"));
		
		if( rsSubCat.getTimestamp("FIRST_TIMESTAMP") != null )
			udCat.setFirstTimestamp(rsSubCat.getTimestamp("FIRST_TIMESTAMP"));
		else
			udCat.setFirstTimestamp(0);
		
		udCat.setIbmOnly(rsSubCat.getString("IBM_ONLY"));
		udCat.setCPrivate(rsSubCat.getString("ISPRIVATE"));
		udCat.setDisplayFlag(rsSubCat.getString("DISPLAY_FLAG"));
		return udCat;
	}


	/**
	 * @param strCatName
	 * @param strProjectID
	 * @return
	 * @throws SQLException
	 */
	public ETSCat getCatByName(String strCatName, String strProjectID)
		throws SQLException {
		PreparedStatement stmtCat =
			m_pdConnection.prepareStatement(
				"select * from ETS.ETS_CAT "
					+ "where cat_name=? and project_id=? with ur");
		stmtCat.setString(1, strCatName);
		stmtCat.setString(2, strProjectID);
		ResultSet rsCat = stmtCat.executeQuery();
		ETSCat udCat = null;

		while (rsCat.next()) {
			udCat = getCat(rsCat);
		}

		stmtCat.close();
		return udCat;
	}



	/**
	 * @param strCatName
	 * @param iParentID
	 * @param strProjectID
	 * @return
	 * @throws SQLException
	 */
	public ETSCat getCatByName(String strCatName, int iParentID, String strProjectID)
		throws SQLException {
		strCatName = strCatName.toLowerCase();
		String strQuery = "select * from ETS.ETS_CAT "
			+ "where lower(cat_name)=? and parent_id=? and project_id=? with ur";
		PreparedStatement stmtCat = m_pdConnection.prepareStatement(strQuery);
		stmtCat.setString(1, strCatName );
		stmtCat.setInt(2, iParentID );
		stmtCat.setString(3, strProjectID);
		ResultSet rsCat = stmtCat.executeQuery();
		ETSCat udCat = null;

		while (rsCat.next()) {
			udCat = getCat(rsCat);
		}

		stmtCat.close();
		return udCat;
	}

	/**
	 * @param strProjectId
	 * @param iViewType
	 * @return
	 * @throws SQLException
	 */
	public int getTopCatId(String strProjectId, int iViewType)
		throws SQLException {

		int iTopCatID = 0;

		String query =
			"select cat_id from ets.ets_cat"
				+ " where view_type = "
				+ iViewType
				+ " and project_id = '"
				+ strProjectId
				+ "'"
				+ " and parent_id=0 with ur";

		Statement stmtTopCat = m_pdConnection.createStatement();
		ResultSet rsTopCat = stmtTopCat.executeQuery(query);

		if (rsTopCat.next()) {
			iTopCatID = rsTopCat.getInt("cat_id");
		}
		rsTopCat.close();
		stmtTopCat.close();

		return iTopCatID;
	}


	/**
	 * @param strDocId
	 * @param strProjectID
	 * @return
	 * @throws SQLException
	 */
	public boolean hasAdditionalEditors(String strDocId, String strProjectID) throws SQLException {
		int iCount = 0 ;
		
		String strQuery =
			"SELECT COUNT(*) AS COUNT FROM ETS.ETS_PRIVATE_DOC "
				+ "WHERE DOC_ID = ?  AND ACCESS_TYPE =  ? ";

		if (m_pdLog.isDebugEnabled()) {
			m_pdLog.debug(strQuery);
		}

		PreparedStatement stmtEditCount =
			m_pdConnection.prepareStatement(strQuery);
		stmtEditCount.setInt(1, Integer.parseInt(strDocId));
		stmtEditCount.setString(2, Defines.DOC_EDIT_ACCESS);
		ResultSet rsEditCount = stmtEditCount.executeQuery();

		if (rsEditCount.next()) {
			iCount = rsEditCount.getInt("COUNT");
		}

		rsEditCount.close();
		stmtEditCount.close();
		if (iCount > 0 ) {
			return true;
		}

		
//		try {
//			DocumentDAO udDAO = new DocumentDAO(); 
//			udDAO.prepare();
//			
//			Vector vtResUsersEdit = udDAO.getAllDocRestrictedEditUserIds( strDocId, strProjectID );
//			List lstGroupsEdit = udDAO.getAllDocRestrictedEditGroupIds( Integer.parseInt(strDocId) );
//			
//			udDAO.cleanup();
//			if( !vtResUsersEdit.isEmpty() || !lstGroupsEdit.isEmpty() )
//				return true;
//		} catch (NumberFormatException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return false;
	}

	
	
	/**
	 * @param strProjectId
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public String getProjectName(String strProjectId) throws SQLException {
		PreparedStatement stmtCat =
			m_pdConnection.prepareStatement(
				"select project_name from ETS.ETS_PROJECTS "
					+ "where project_id=? with ur");
		stmtCat.setString(1, strProjectId);
		ResultSet rsCat = stmtCat.executeQuery();
		String strProjectName = "";

		if (rsCat.next()) {
			strProjectName = rsCat.getString("project_name");
		}

		rsCat.close();
		stmtCat.close();

		return strProjectName;
	}

	/**
	 * @param strProjectId
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public String getProjectType(String strProjectId) throws SQLException {
		PreparedStatement stmtCat =
			m_pdConnection.prepareStatement(
				"select project_type from ETS.ETS_PROJECTS "
					+ "where project_id=? with ur");
		stmtCat.setString(1, strProjectId);
		ResultSet rsCat = stmtCat.executeQuery();
		String strProjectType = "";

		if (rsCat.next()) {
			strProjectType = rsCat.getString("project_type");
		}

		rsCat.close();
		stmtCat.close();

		return strProjectType;
	}

	/**
	 * @param strProjectId
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public String getProjMemberEmails(String strProjectId)
		throws SQLException {
		PreparedStatement stmtProjMembers =
			m_pdConnection.prepareStatement(
				"select a.user_email, u.user_id, a.user_fname, a.user_lname "
					+ "from ETS.ETS_USERS u, AMT.USERS a "
					+ "where u.user_project_id = ?"
					+ " and u.user_id = a.ir_userid "
					+ "and u.active_flag='A' "
					+ "order by a.USER_FULLNAME with ur");

		stmtProjMembers.setString(1, strProjectId);
		ResultSet rsProjMembers = stmtProjMembers.executeQuery();

		StringBuffer strEmailIDs = new StringBuffer("");
		
		while (rsProjMembers.next()) {
			String strUser = rsProjMembers.getString("user_id");
			String strEmailID = rsProjMembers.getString("user_email");
			strEmailIDs.append(strEmailID);
			strEmailIDs.append(",");	
		}
		rsProjMembers.close();
		stmtProjMembers.close();
		return strEmailIDs.substring(0, strEmailIDs.length());
	}

	/**
	 * @param strProjectId
	 * @param vtNotifyList
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public String getProjMemberEmails(String strProjectId, Vector vtNotifyList)
		throws SQLException {
		PreparedStatement stmtProjMembers =
			m_pdConnection.prepareStatement(
				"select a.user_email, u.user_id, a.user_fname, a.user_lname "
					+ "from ETS.ETS_USERS u, AMT.USERS a "
					+ "where u.user_project_id = ?"
					+ " and u.user_id = a.ir_userid "
					+ "and u.active_flag='A' "
					+ "order by a.USER_FULLNAME with ur");

		stmtProjMembers.setString(1, strProjectId);
		ResultSet rsProjMembers = stmtProjMembers.executeQuery();

		StringBuffer strEmailIDs = new StringBuffer("");
		
		boolean bIsNotifyAllFlag = false; 
			
		List lstUserIds = new ArrayList();
		if (vtNotifyList != null && vtNotifyList.size() > 0) {
			for(int i=0; i < vtNotifyList.size(); i++) {
				DocNotify udNotify = (DocNotify) vtNotifyList.get(i);
				if ((i == 0) && DocConstants.IND_YES.equalsIgnoreCase(udNotify.getNotifyAllFlag())) {
				    bIsNotifyAllFlag = true;
				}
				lstUserIds.add(udNotify.getUserId());
			}
		}
		
		while (rsProjMembers.next()) {
			String strUser = rsProjMembers.getString("user_id");
			String strEmailID = rsProjMembers.getString("user_email");
			if (lstUserIds.size() > 0) {
				if (bIsNotifyAllFlag || lstUserIds.contains(strUser)) {
					strEmailIDs.append(strEmailID);
					strEmailIDs.append(",");	
				}
			}
			else {
				strEmailIDs.append(strEmailID);
				strEmailIDs.append(",");	
			}
		}
		rsProjMembers.close();
		stmtProjMembers.close();
		return strEmailIDs.substring(0, strEmailIDs.length());
	}

	/**
	 * @param projectid
	 * @param priv
	 * @param conn
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public Vector getUsersByProjectPriv(String projectid, int priv) throws SQLException, Exception {

		String query = "select u.* from ets.ets_roles r, ets.ets_users u" + " where r.priv_id = " + priv + " and r.priv_value = 1" + " and u.user_project_id = '" + projectid + "'" + " and u.user_role_id = r.role_id" + " and u.user_project_id = r.project_id" + " and u.active_flag='A'" + " with ur";

		Statement statement = m_pdConnection.createStatement();
		ResultSet rs = statement.executeQuery(query);
		Vector v = getUsers(rs);

		rs.close();
		statement.close();
		return v;
	}

	/**
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private Vector getUsers(ResultSet rs) throws SQLException {
		Vector v = new Vector();

		while (rs.next()) {
			ETSUser user = getUser(rs);
			v.addElement(user);
		}
		return v;
	}

	/**
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private ETSUser getUser(ResultSet rs) throws SQLException {
		ETSUser user = new ETSUser();

		user.setUserId(rs.getString("USER_ID"));
		//System.out.println(user.getUserId());
		//spn 0312 projid
		user.setProjectId(rs.getString("USER_PROJECT_ID"));
		user.setRoleId(rs.getInt("USER_ROLE_ID"));
		user.setUserJob(rs.getString("USER_JOB"));
		user.setPrimaryContact(rs.getString("PRIMARY_CONTACT"));
		user.setLastUserId(rs.getString("LAST_USERID"));
		user.setLastTimestamp(rs.getTimestamp("LAST_TIMESTAMP"));
		user.setActiveFlag(rs.getString("ACTIVE_FLAG"));

		return user;
	}

	/**
	 * @param idoc_Id
	 * @param strUser_Id
	 * @param strProj_Id
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public boolean checkDocumentEditPriv(int idoc_Id, String strUser_Id, String strProj_Id) throws SQLException {
		boolean editPriv = false;
		String strQuery = "SELECT USER_ID FROM ETS.ETS_PRIVATE_DOC "
								+ "WHERE DOC_ID = ? AND "
								+ "USER_ID = ? AND "
								+ "PROJECT_ID = ? AND " 
								+ "ACCESS_TYPE = ? "
								+ "with ur";
		PreparedStatement pstmtPrivilige = m_pdConnection.prepareStatement(strQuery);

		pstmtPrivilige.setInt(1, idoc_Id);
		pstmtPrivilige.setString(2, strUser_Id);
		pstmtPrivilige.setString(3, strProj_Id);
		pstmtPrivilige.setString(4, Defines.DOC_EDIT_ACCESS);
		ResultSet rs = pstmtPrivilige.executeQuery();

		ResultSet rsPrivilige = pstmtPrivilige.executeQuery();
		if (rsPrivilige.next()) {
			editPriv = true;
		}
		rsPrivilige.close();
		pstmtPrivilige.close();

		return editPriv;
	}

	/**
	 * @param idoc_Id
	 * @param strUser_Id
	 * @param strProj_Id
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public boolean checkDocumentEditPrivForGroup(int idoc_Id, String strUser_Id, String strProj_Id) throws SQLException {
		boolean editPriv = false;
		
		// If the All Users group has been given privilige for the document, we don't need to check any other details
		// as to whether the user is in the group or not.
		editPriv = isAllUserAccessToDoc(idoc_Id, strProj_Id, Defines.DOC_EDIT_ACCESS);
		if (!editPriv) {
		    // Further check other access values
			String strQuery = "SELECT USER_ID FROM ETS.USER_GROUPS "
				+"WHERE  GROUP_ID IN (SELECT GROUP_ID FROM ETS.ETS_PRIVATE_DOC "
								+"WHERE DOC_ID = ? AND " 
								+" PROJECT_ID =? AND "
								+" ACCESS_TYPE = ?) "  
				+"AND USER_ID = ? WITH UR";
			PreparedStatement pstmtPrivilige = m_pdConnection.prepareStatement(strQuery);
			
			pstmtPrivilige.setInt(1, idoc_Id);
			pstmtPrivilige.setString(2, strProj_Id);
			pstmtPrivilige.setString(3, Defines.DOC_EDIT_ACCESS);
			pstmtPrivilige.setString(4, strUser_Id);
			ResultSet rs = pstmtPrivilige.executeQuery();
			
			ResultSet rsPrivilige = pstmtPrivilige.executeQuery();
			if (rsPrivilige.next()) {
			editPriv = true;
			}
			rsPrivilige.close();
			pstmtPrivilige.close();
		}

		return editPriv;
	}

	/**
	 * @param iDocID
	 * @param strProjectId
	 * @param strAccessType
	 * @return
	 * @throws SQLException
	 */
	private boolean isAllUserAccessToDoc(int iDocID, String strProjectId, String strAccessType) throws SQLException {
		boolean editPriv = false;
		
		// If the All Users group has been given privilige for the document, we don't need to check any other details
		// as to whether the user is in the group or not.
		
		String strQuery = "SELECT * FROM ETS.ETS_PRIVATE_DOC "
							+ "WHERE DOC_ID = ? AND " 
							+ "ACCESS_TYPE = ? "  
							+ "AND GROUP_ID = " 
							+ "(SELECT GROUP_ID FROM ETS.GROUPS WHERE NAME=? AND PROJECT_ID=?)";

		
		PreparedStatement pstmtPrivilige = m_pdConnection.prepareStatement(strQuery);

		pstmtPrivilige.setInt(1, iDocID);
		pstmtPrivilige.setString(2, strAccessType);
		pstmtPrivilige.setString(3, Defines.GRP_ALL_USERS);
		pstmtPrivilige.setString(4, strProjectId);
		ResultSet rs = pstmtPrivilige.executeQuery();

		ResultSet rsPrivilige = pstmtPrivilige.executeQuery();
		if (rsPrivilige.next()) {
			editPriv = true;
		}
		rsPrivilige.close();
		pstmtPrivilige.close();

		return editPriv;
	}

	/**
	 * @param iParentId
	 * @param strName
	 * @param iDocId
	 * @return
	 * @throws SQLException
	 */
	public int getDocByNameAndCat(int iParentId, String strName, int iDocId)
		throws SQLException {

		int iCount = 0;
		String strQuery =
			"SELECT COUNT(*) AS COUNT FROM ETS.ETS_DOC "
				+ "WHERE RTRIM(LTRIM(DOC_NAME)) = ? "
				+ "AND CAT_ID = ? AND DELETE_FLAG != '"
				+ DocConstants.TRUE_FLAG
				+ "' AND ITAR_UPLOAD_STATUS != '"
				+ DocConstants.ITAR_STATUS_PENDING
				+ "' AND LATEST_VERSION = '"+DocConstants.TRUE_FLAG+"'";
		if (iDocId != -1) {
			strQuery = strQuery + " AND DOC_ID != " + iDocId;
		}

		strQuery = strQuery + " WITH UR";

		if (m_pdLog.isDebugEnabled()) {
			m_pdLog.debug(strQuery);
		}

		PreparedStatement stmtGetDocs =
			m_pdConnection.prepareStatement(strQuery);
		stmtGetDocs.setString(1, strName.trim());
		stmtGetDocs.setInt(2, iParentId);
		ResultSet rsGetDocs = stmtGetDocs.executeQuery();

		if (rsGetDocs.next()) {
			iCount = rsGetDocs.getInt("COUNT");
		}

		rsGetDocs.close();
		stmtGetDocs.close();
		return iCount;
	}

	/**
	 * @param strProjectId
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public Vector getProjMembers(String strProjectId) throws SQLException {
		PreparedStatement stmtProjMembers =
			m_pdConnection.prepareStatement(
				"select * from ETS.ETS_USERS "
					+ "where user_project_id = ? "
					+ "and active_flag='A' "
					+ "order by user_id with ur");

		stmtProjMembers.setString(1, strProjectId);
		ResultSet rsProjMembers = stmtProjMembers.executeQuery();

		Vector vtMembers = null; //new Vector();
		vtMembers = getUsers(rsProjMembers);
		rsProjMembers.close();
		stmtProjMembers.close();
		return vtMembers;
	}

	/**
	 * @param strProjectId
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public Vector getProjMembersWithNames(String strProjectId) throws SQLException {
		PreparedStatement stmtProjMembers =
			m_pdConnection.prepareStatement(
				"select * from ETS.ETS_USERS "
					+ "where user_project_id = ? "
					+ "and active_flag='A' "
					+ "order by user_id with ur");

		stmtProjMembers.setString(1, strProjectId);
		ResultSet rsProjMembers = stmtProjMembers.executeQuery();

		Vector vtMembers = null; //new Vector();
		vtMembers = getUsers(rsProjMembers);
		rsProjMembers.close();
		stmtProjMembers.close();
		return populateUserNames( vtMembers );
	}

	/**
	 * @param strProjectId
	 * @param bCheckAmtTables
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public Vector getProjMembers(String strProjectId, boolean bCheckAmtTables)
		throws SQLException {
		PreparedStatement stmtProjMembers =
			m_pdConnection.prepareStatement(
				"select u.*, a.user_fname, a.user_lname "
					+ "from ETS.ETS_USERS u, AMT.USERS a "
					+ "where u.user_project_id = ?"
					+ " and u.user_id = a.ir_userid "
					+ "and u.active_flag='A' "
					+ "order by a.USER_FULLNAME with ur");

		stmtProjMembers.setString(1, strProjectId);
		ResultSet rsProjMembers = stmtProjMembers.executeQuery();

		Vector vtProjMembers = getUsers(rsProjMembers);
		rsProjMembers.close();
		stmtProjMembers.close();
		return vtProjMembers;
	}

	/**
	 * @param strProjectId
	 * @param iID
	 * @param bCheckAmtTables
	 * @param bIsCat
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public Vector getRestrictedProjMembers(
		String strProjectId,
		int iID,
		boolean bCheckAmtTables,
		boolean bIsCat)
		throws SQLException {

		String strTable = "cat";
		if (!bIsCat)
			strTable = "doc";

		if (!bCheckAmtTables) {
			return getRestrictedProjMembers(strProjectId, iID, bIsCat);
		}
		
		String strQuery = "select u.* from ETS.ETS_USERS u, AMT.USERS a,ets.ets_private_"
								+ strTable
								+ " t where u.user_project_id = ?"
								+ " and u.user_id = a.ir_userid and u.active_flag='A' "
								+ "and u.user_id=t.user_id "
								+ "and u.user_project_id=t.project_id and t."
								+ strTable
								+ "_id=? AND (t.ACCESS_TYPE is null or t.ACCESS_TYPE = ?)" 
								+ " order by a.USER_FULLNAME with ur";
		PreparedStatement getProjMemSt = m_pdConnection.prepareStatement(strQuery);

		getProjMemSt.setString(1, strProjectId);
		getProjMemSt.setInt(2, iID);
		getProjMemSt.setString(3, Defines.DOC_READ_ACCESS);
		ResultSet rs = getProjMemSt.executeQuery();

		Vector vtMembers = null;
		vtMembers = getUsers(rs);
		rs.close();
		getProjMemSt.close();
		return populateUserNames(vtMembers);
	}

	/**
	 * @param strProjectId
	 * @param iID
	 * @param bIsCat
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public Vector getRestrictedProjMembers(
		String strProjectId,
		int iID,
		boolean bIsCat)
		throws SQLException {

		String strTable = "cat";
		if (!bIsCat)
			strTable = "doc";

		String strQuery = 	"select u.* from ETS.ETS_USERS u, ets.ets_private_"
								+ strTable
								+ " t where u.user_project_id = ?"
								+ " and u.active_flag='A' and u.user_id=t.user_id "
								+ "and u.user_project_id=t.project_id and t."
								+ strTable
								+ "_id=? AND t.ACCESS_TYPE = ? " 
								+ "order by u.user_id with ur";
		PreparedStatement getProjMemSt = m_pdConnection.prepareStatement(strQuery);

		getProjMemSt.setString(1, strProjectId);
		getProjMemSt.setInt(2, iID);
		getProjMemSt.setString(3, Defines.DOC_READ_ACCESS);
		ResultSet rs = getProjMemSt.executeQuery();

		Vector vtMembers = null;
		vtMembers = getUsers(rs);
		rs.close();
		getProjMemSt.close();
		return populateUserNames(vtMembers);
	}

	/**
	 * @param strProjectId
	 * @param iID
	 * @param bCheckAmtTables
	 * @param bIsCat
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public Vector getRestrictedProjMembersEdit(
		String strProjectId,
		int iID,
		boolean bCheckAmtTables,
		boolean bIsCat)
		throws SQLException {

		String strTable = "cat";
		if (!bIsCat)
			strTable = "doc";

		if (!bCheckAmtTables) {
			return getRestrictedProjMembersEdit(strProjectId, iID, bIsCat);
		}

		PreparedStatement getProjMemSt =
			m_pdConnection.prepareStatement(
				"select u.* from ETS.ETS_USERS u, AMT.USERS a,ets.ets_private_"
					+ strTable
					+ " t where u.user_project_id = ?"
					+ " and u.user_id = a.ir_userid and u.active_flag='A' "
					+ "and u.user_id=t.user_id "
					+ "and u.user_project_id=t.project_id and t."
					+ strTable
					+ "_id=? AND t.ACCESS_TYPE = ? " 
					+ " order by a.USER_FULLNAME with ur");

		getProjMemSt.setString(1, strProjectId);
		getProjMemSt.setInt(2, iID);
		getProjMemSt.setString(3, Defines.DOC_EDIT_ACCESS);
		ResultSet rs = getProjMemSt.executeQuery();

		Vector vtMembers = null;
		vtMembers = getUsers(rs);
		rs.close();
		getProjMemSt.close();
		return populateUserNames(vtMembers);
	}

	/**
	 * @param strProjectId
	 * @param iID
	 * @param bIsCat
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public Vector getRestrictedProjMembersEdit(
		String strProjectId,
		int iID,
		boolean bIsCat)
		throws SQLException {

		String strTable = "cat";
		if (!bIsCat)
			strTable = "doc";

		PreparedStatement getProjMemSt =
			m_pdConnection.prepareStatement(
				"select u.* from ETS.ETS_USERS u, ets.ets_private_"
					+ strTable
					+ " t where u.user_project_id = ?"
					+ " and u.active_flag='A' and u.user_id=t.user_id "
					+ "and u.user_project_id=t.project_id and t."
					+ strTable
					+ "_id=? AND t.ACCESS_TYPE = ? " 
					+ "order by u.user_id with ur");

		getProjMemSt.setString(1, strProjectId);
		getProjMemSt.setInt(2, iID);
		getProjMemSt.setString(3, Defines.DOC_EDIT_ACCESS);
		ResultSet rs = getProjMemSt.executeQuery();

		Vector vtMembers = null;
		vtMembers = getUsers(rs);
		rs.close();
		getProjMemSt.close();
		return populateUserNames(vtMembers);
	}

	/**
	 * @param strGroupId
	 * @return
	 * @throws SQLException
	 */
	public List getGroupUsers(String strGroupId) throws SQLException {
		List lstUsers = new ArrayList();

		PreparedStatement stmtGroups =
			m_pdConnection.prepareStatement(
				"SELECT DISTINCT USER_ID FROM ETS.USER_GROUPS "
					+ "WHERE GROUP_ID = ? WITH UR");
		stmtGroups.setString(1, strGroupId);
		ResultSet rsUsers = stmtGroups.executeQuery();

		while (rsUsers.next()) {
			lstUsers.add(rsUsers.getString("USER_ID"));
		}

		rsUsers.close();
		stmtGroups.close();
		return lstUsers;
	}

	/**
	 * @param strProjectId
	 * @return
	 * @throws SQLException
	 */
	public List getGroups(String strProjectId) throws SQLException {
		List lstGroups = new ArrayList();

		PreparedStatement stmtGroups =
			m_pdConnection.prepareStatement(
				"SELECT * FROM ETS.GROUPS WHERE PROJECT_ID=? WITH UR");
		stmtGroups.setString(1, strProjectId);
		ResultSet rsGroups = stmtGroups.executeQuery();

		while (rsGroups.next()) {
			lstGroups.add(populateGroup(rsGroups));
		}

		rsGroups.close();
		stmtGroups.close();
		return lstGroups;
	}

	/**
	 * @param rsGroup
	 * @return
	 * @throws SQLException
	 */
	public Group populateGroup(ResultSet rsGroup) throws SQLException {
		Group udGrp =
			new Group(
				rsGroup.getString("GROUP_ID"),
				rsGroup.getString("NAME"),
				rsGroup.getString("TYPE"),
				rsGroup.getString("DESCRIPTION"),
				rsGroup.getString("OWNER"),
				rsGroup.getString("PROJECT_ID"),
				rsGroup.getTimestamp("LAST_TIMESTAMP").getTime());

		return udGrp;
	}

	/**
	 * @param strProjectId
	 * @param strSourceId
	 * @param strDestId
	 * @return
	 * @throws SQLException
	 */
	public ETSProjectStatus getProjectStatus(String strProjectId, String strSourceId, String strDestId) throws SQLException {
	    
		ETSProjectStatus udProjectStatus = null;
		PreparedStatement stmtStatus =
			m_pdConnection.prepareStatement(
				"SELECT * FROM ETS.WS_PROJECT_STATUS WHERE " 
			        + "PROJECT_ID=? WITH UR");
		stmtStatus.setString(1, strProjectId);
//		stmtStatus.setString(2, strSourceId);
//		stmtStatus.setString(3, strDestId);
		ResultSet rsStatus = stmtStatus.executeQuery();
		if (rsStatus.next()) {
		    udProjectStatus = new ETSProjectStatus();
		    udProjectStatus.setProjectId(strProjectId);
		    udProjectStatus.setDestId(rsStatus.getString("DEST_ID"));
		    udProjectStatus.setSourceId(rsStatus.getString("SOURCE_ID"));
		    udProjectStatus.setLastUserId(rsStatus.getString("LAST_USERID"));
		    udProjectStatus.setLastTimestamp(rsStatus.getTimestamp("LAST_TIMESTAMP"));
		    udProjectStatus.setType(rsStatus.getString("TYPE"));
			Date dtStatus = rsStatus.getDate("LAST_TIMESTAMP");
		}

		rsStatus.close();
		stmtStatus.close();
		return udProjectStatus;
	}

	/**
	 * @param strProjectId
	 * @return
	 * @throws SQLException
	 */
	public boolean isWorkflowProject(String strProjectId) throws SQLException {
	    boolean bIsWorkflowProject = false;
	    
		PreparedStatement stmtGroups =
			m_pdConnection.prepareStatement(
				"SELECT * FROM ETS.WF_DEF WHERE PROJECT_ID=? WITH UR");
		stmtGroups.setString(1, strProjectId);
		ResultSet rsGroups = stmtGroups.executeQuery();

		if (rsGroups.next()) {
			bIsWorkflowProject = true;
		}

		rsGroups.close();
		stmtGroups.close();
		return bIsWorkflowProject;
	}
}
