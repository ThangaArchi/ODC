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

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import oem.edge.common.DbConnect;
import oem.edge.common.Global;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.ETSDocEditHistory;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.ETSDocFile;
import oem.edge.ets.fe.documents.common.StringUtil;

import org.apache.log4j.Logger;

/**
 * This is the DATA ACCESS Class for the E&TS Documents Module
 * @author v2srikau
 */
public class DocUpdateDAO {

	/** Stores the Logging object */
	private Logger m_pdLog = Logger.getLogger(DocUpdateDAO.class);

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
	 * @param udDoc
	 * @param strUserID
	 * @param bDelAll
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public synchronized boolean delDoc(
		ETSDoc udDoc,
		String strUserID,
		boolean bDelAll)
		throws SQLException {
		try {
			int iDocID = udDoc.getId();
			String strProjectID = udDoc.getProjectId();

			int lowID =
				(iDocID / DocConstants.MAX_DOC_VERSIONS)
					* DocConstants.MAX_DOC_VERSIONS;
			int highID = lowID + DocConstants.MAX_DOC_VERSIONS;

			Statement stmtDelDoc = m_pdConnection.createStatement();
			String strUpdate =
				"update ETS.ETS_DOC set latest_version='"
					+ DocConstants.FALSE_FLAG
					+ "', delete_flag='"
					+ DocConstants.TRUE_FLAG
					+ "',deleted_by='"
					+ strUserID
					+ "',deletion_date=current timestamp where DOC_ID >= "
					+ lowID
					+ " and DOC_ID < "
					+ highID;

			int iRowCount = stmtDelDoc.executeUpdate(strUpdate);
			if (iRowCount <= 0) {
				if (m_pdLog.isDebugEnabled()) {
					m_pdLog.debug(
						"deldoc executeUpdate("
							+ strUpdate
							+ ") returned "
							+ iRowCount);
				}
				return false;
			}
			addContentLog(
				iDocID,
				'D',
				strProjectID,
				Defines.DELALL_DOC,
				strUserID);

			return true;
		} catch (SQLException e) {
			m_pdLog.error("sql error =" + getStackTrace(e));
			throw e;
		}
	}

	/**
	 * @param iNodeID
	 * @param cNodeType
	 * @param strProjectID
	 * @param strAction
	 * @param strUser
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public synchronized boolean addContentLog(
		int iNodeID,
		char cNodeType,
		String strProjectID,
		String strAction,
		String strUser)
		throws SQLException {
		PreparedStatement stmtAddLog =
			m_pdConnection.prepareStatement(
				"insert into ets.ets_content_log("
					+ "timestamp, node_id,node_type,project_id,"
					+ "action,action_by) "
					+ "values(current timestamp,?,?,?,?,?)");

		try {
			stmtAddLog.setInt(1, iNodeID);
			stmtAddLog.setString(2, String.valueOf(cNodeType));
			stmtAddLog.setString(3, strProjectID);
			stmtAddLog.setString(4, strAction);
			stmtAddLog.setString(5, strUser);
			stmtAddLog.executeUpdate();

			stmtAddLog.close();
			return true;
		} catch (SQLException se) {
			m_pdLog.error("sql error =" + se);
			throw se;
		}

	}

	public synchronized boolean setEditHistory(ETSDocEditHistory pEtsDocHistory) throws SQLException, Exception {
		String strQuery = StringUtil.EMPTY_STRING;
		int iSeqNum = getSequenceNumber(pEtsDocHistory.getDocId());
		try{
			strQuery = "INSERT INTO ETS.DOC_EDIT_HISTORY(DOC_ID, SEQ_NO, USER_ID, " 
						+"ACTION, ACTION_DETAILS, LAST_TIMESTAMP) "
						+ "VALUES (?,?,?,?,?,CURRENT TIMESTAMP)";
			PreparedStatement stmtUpdDocResUsers = 
			    m_pdConnection.prepareStatement(strQuery);
			stmtUpdDocResUsers.setInt(1, pEtsDocHistory.getDocId());
			stmtUpdDocResUsers.setInt(2, iSeqNum);
			stmtUpdDocResUsers.setString(3, pEtsDocHistory.getUserId());
			stmtUpdDocResUsers.setString(4, pEtsDocHistory.getAction());
			stmtUpdDocResUsers.setString(5, pEtsDocHistory.getActionDetails());
		 
			stmtUpdDocResUsers.executeUpdate();
		
		} catch (SQLException e) {
			m_pdLog.error("sql error =" + DocUpdateDAO.getStackTrace(e));
			throw e;
		}
		return true;
	}

	/**
	 * @param idocID
	 * @param strUserId
	 * @param strAction
	 * @param strActionDetails
	 * @return
	 * @throws SQLException In case of database errors
	 * @throws Exception
	 */
	public synchronized boolean setEditHistory(
		int idocID,
		String strUserId,
		String strAction,
		String strActionDetails) throws SQLException, Exception {
		String strQuery = StringUtil.EMPTY_STRING;
		int iSeqNum = getSequenceNumber(idocID);
		try{
			strQuery = "INSERT INTO ETS.DOC_EDIT_HISTORY(DOC_ID, SEQ_NO, USER_ID, " 
											+"ACTION, ACTION_DETAILS, LAST_TIMESTAMP) "
											+ "VALUES"
				+ "(?,?,?,?,?,CURRENT TIMESTAMP )";
			PreparedStatement stmtUpdDocResUsers = 
			    m_pdConnection.prepareStatement(strQuery);
			stmtUpdDocResUsers.setInt(1, idocID);
			stmtUpdDocResUsers.setInt(2, iSeqNum);
			stmtUpdDocResUsers.setString(3, strUserId);
			stmtUpdDocResUsers.setString(4, strAction);
			stmtUpdDocResUsers.setString(5, strActionDetails);
		
			stmtUpdDocResUsers.executeUpdate();

		} catch (SQLException e) {
			m_pdLog.error("sql error =" + DocUpdateDAO.getStackTrace(e));
			throw e;
		}
		return true;
	}

	/**
	 * @param iDocID 
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public synchronized int getSequenceNumber(int iDocID) throws SQLException {

		try {

			Statement stmtGetDocID = m_pdConnection.createStatement();
			ResultSet rsGetDocID =
				stmtGetDocID.executeQuery("SELECT MAX(SEQ_NO) FROM ETS.DOC_EDIT_HISTORY WHERE DOC_ID=" + iDocID);

			int iMaxID = 0;

			if (rsGetDocID.next()) {
				iMaxID = rsGetDocID.getInt(1);
			} else {
				m_pdLog.error(
					"Empty ResultSet: select MAX(SEQ_NO) from ETS.DOC_EDIT_HISTORY");
			}

			rsGetDocID.close();
			stmtGetDocID.close();

			return (iMaxID + 1);
		} catch (SQLException e) {
			m_pdLog.error("error= " + DocUpdateDAO.getStackTrace(e));
			throw e;
		}
	}

	/**
	 * @param t
	 * @return
	 */
	public static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		String stackTrace = sw.toString();
		pw.close();
		return stackTrace;
	}

	/**
	 * @param udDoc
	 * @param pdInputStream
	 * @param iExistingDocID
	 * @param bIsITAR
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public synchronized boolean addDocMethod(
		ETSDoc udDoc,
		List lstFiles,
		int iExistingDocID,
		boolean bIsITAR)
		throws SQLException, Exception {
		boolean success = false;
		boolean newdoc = true;
		try {

			int newDocID = -1;
			int mustUpdateIsLatestVersion = -1;

			if (iExistingDocID > DocConstants.STARTING_DOC_ID) {
				// update to an existing doc
				newdoc = false;
				newDocID = getUpdateDocID(iExistingDocID);
				udDoc.setHasPreviousVersion(DocConstants.TRUE_FLAG);
				mustUpdateIsLatestVersion = newDocID - 1;
				// doc id of previous latest doc.
				//The IsLatestVersion flag of this doc needs to be changed
			} else { // brand new doc
				newdoc = true;
				newDocID = getNewDocID();
				udDoc.setHasPreviousVersion(DocConstants.FALSE_FLAG);
			}

			if (newDocID < DocConstants.STARTING_DOC_ID
				|| newDocID > DocConstants.MAXIMUM_DOC_ID) {
				m_pdLog.error("out of range value for newDocID");
				return false;
			}

			udDoc.setId(newDocID);
			udDoc.setIsLatestVersion(DocConstants.TRUE_FLAG);

			if (bIsITAR) {
				udDoc.setItarStatus(DocConstants.ITAR_STATUS_PENDING);
			} else {
				udDoc.setItarStatus(DocConstants.ITAR_STATUS_COMPLETE);
			}

			StringBuffer strAttachmentNames = new StringBuffer(StringUtil.EMPTY_STRING);
			for (int iCounter = 0; iCounter < lstFiles.size(); iCounter++) {
//				FormFile pdFormFile = (FormFile) lstFiles.get(iCounter);
				ETSDocFile udDocFile = (ETSDocFile) lstFiles.get(iCounter);
//				if (!StringUtil.isNullorEmpty(pdFormFile.getFileName())) {
				if (!StringUtil.isNullorEmpty(udDocFile.getFileName())) {
				    strAttachmentNames.append("'");
//				    strAttachmentNames.append(pdFormFile.getFileName());
				    strAttachmentNames.append(udDocFile.getFileName());
				    strAttachmentNames.append("'");
				    strAttachmentNames.append(", ");
				}
			}
			if (strAttachmentNames.length() > 0) {
			    strAttachmentNames =
			    	new StringBuffer(
			    	        strAttachmentNames.substring(0, 
			    	        strAttachmentNames.length()-2));
			}
			
			success = addDoc(udDoc, strAttachmentNames.toString());
			if (!success) {
				m_pdLog.error("error in add doc");
				return false;
			} else if (
				mustUpdateIsLatestVersion >= DocConstants.STARTING_DOC_ID
					&& mustUpdateIsLatestVersion < DocConstants.MAXIMUM_DOC_ID) {
				// The IsLatestVersion flag of the older version to be changed to false
				success =
					updateIsLatestVersionFlag(
						mustUpdateIsLatestVersion,
						DocConstants.FALSE_FLAG);
				if (!success) {
					m_pdLog.error(
						"error in updateIsLatestVersionFlag: "
							+ mustUpdateIsLatestVersion);
					return false;
				}
			}

			if (lstFiles == null) {
				lstFiles = new ArrayList();
			}
			for (int iCounter = 0; iCounter < lstFiles.size(); iCounter++) {
//				FormFile pdFormFile = (FormFile) lstFiles.get(iCounter);
				ETSDocFile udDocFile = (ETSDocFile) lstFiles.get(iCounter);
				if (!StringUtil.isNullorEmpty(udDocFile.getFileName())) {
					success =
						addDocFile(
							udDoc.getId(),
							udDocFile.getFileName(),
							udDocFile.getFileSize(),
							udDocFile.getInputStream());
					if (!success) {
						m_pdLog.error("error in add doc file");
						return false;
					}
				}
			}

			//setEditHistory(udDoc.getId(), udDoc.getUserId(), "Add Document", "New Document Added "+udDoc.getUserId());
			
			
			if (newdoc) {
				if (udDoc.getDocType() == Defines.DOC) {
					addContentLog(
						udDoc.getId(),
						'D',
						udDoc.getProjectId(),
						Defines.ADD_DOC,
						udDoc.getUserId());
				} else if (udDoc.getDocType() == Defines.PROJECT_PLAN) {
					addContentLog(
						udDoc.getId(),
						'D',
						udDoc.getProjectId(),
						Defines.ADD_PROJ_PLAN_DOC,
						udDoc.getUserId());
				} else if (udDoc.getDocType() == Defines.MEETING) {
					addContentLog(
						udDoc.getId(),
						'D',
						udDoc.getProjectId(),
						Defines.ADD_MEETING_DOC,
						udDoc.getUserId());
				} else if (udDoc.getDocType() == Defines.EVENT) {
					addContentLog(
						udDoc.getId(),
						'D',
						udDoc.getProjectId(),
						Defines.ADD_EVENT_DOC,
						udDoc.getUserId());
				} else if (udDoc.getDocType() == Defines.SETMET_PLAN) {
					addContentLog(
						udDoc.getId(),
						'D',
						udDoc.getProjectId(),
						Defines.ADD_ACTION_PLAN,
						udDoc.getUserId());
				}
			} else {
				addContentLog(
					udDoc.getId(),
					'D',
					udDoc.getProjectId(),
					Defines.UPDATE_DOC,
					udDoc.getUserId());
			}
		} catch (SQLException e) {
			success = false;
			m_pdLog.error("error= " + DocUpdateDAO.getStackTrace(e));
			throw e;
		} catch (Exception ex) {
			success = false;
			m_pdLog.error("ex error= " + DocUpdateDAO.getStackTrace(ex));
			throw ex;
		}
		return success;
	}

	/**
	 * @return
	 * @throws SQLException In case of database errors
	 */
	private synchronized int getNewDocID() throws SQLException {

		try {

			Statement stmtGetDocID = m_pdConnection.createStatement();
			ResultSet rsGetDocID =
				stmtGetDocID.executeQuery(
					"select MAX(DOC_ID) from ETS.ETS_DOC");

			int iMaxID = -1;

			if (rsGetDocID.next()) {
				iMaxID = rsGetDocID.getInt(1);
			} else {
				m_pdLog.error(
					"Empty ResultSet for query: select MAX(DOC_ID) "
						+ "from ETS.ETS_DOC");
			}

			rsGetDocID.close();
			stmtGetDocID.close();

			if (iMaxID < DocConstants.STARTING_DOC_ID)
				iMaxID = DocConstants.STARTING_DOC_ID;

			if (iMaxID >= DocConstants.MAXIMUM_DOC_ID) {
				m_pdLog.error("Will exceed MAXIMUM_DOC_ID");
				return -1;
			} else {
				return (
					((iMaxID / DocConstants.MAX_DOC_VERSIONS) + 1)
						* DocConstants.MAX_DOC_VERSIONS);
			}

		} catch (SQLException e) {
			m_pdLog.error("error= " + DocUpdateDAO.getStackTrace(e));
			throw e;
		}

	}

	/**
	 * @param iExistingDocID
	 * @return
	 * @throws SQLException In case of database errors
	 */
	private synchronized int getUpdateDocID(int iExistingDocID)
		throws SQLException {

		try {

			int lowestPossibleID =
				(iExistingDocID / DocConstants.MAX_DOC_VERSIONS)
					* DocConstants.MAX_DOC_VERSIONS;
			int highestPossibleID =
				lowestPossibleID + DocConstants.MAX_DOC_VERSIONS;

			Statement statement = m_pdConnection.createStatement();
			ResultSet rs =
				statement.executeQuery(
					"select MAX(DOC_ID) from ETS.ETS_DOC where DOC_ID >= "
						+ lowestPossibleID
						+ " and DOC_ID < "
						+ highestPossibleID);

			int iMaxID = -1;
			if (rs.next()) {
				iMaxID = rs.getInt(1);
			} else {
				m_pdLog.error(
					"Empty ResultSet for query: select MAX(DOC_ID) "
						+ "from ETS.ETS_DOC");
			}

			rs.close();
			statement.close();

			if (iMaxID < DocConstants.STARTING_DOC_ID) { // should never happen
				m_pdLog.error(
					iExistingDocID
						+ ": maxid < STARTING_DOC_ID in getUpdateDocID: "
						+ "should never happen");
			} else if (
				((iMaxID % DocConstants.MAX_DOC_VERSIONS) + 1)
					>= DocConstants.MAX_DOC_VERSIONS) {
				// will exceed MAX_DOC_VERSIONS
				m_pdLog.error(
					iExistingDocID + ": will exceed MAX_DOC_VERSIONS");
			} else if (iMaxID >= DocConstants.MAXIMUM_DOC_ID) {
				m_pdLog.error("Will exceed MAXIMUM_DOC_ID");
			} else {
				return (iMaxID + 1);
			}

			return -1;

		} catch (SQLException e) {
			m_pdLog.error("error= " + DocUpdateDAO.getStackTrace(e));
			throw e;
		}

	}

	/**
	 * @param iDocID
	 * @param cFlag
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public synchronized boolean updateIsLatestVersionFlag(
		int iDocID,
		char cFlag)
		throws SQLException {

		Statement stmtUpdLatestVer = m_pdConnection.createStatement();
		try {
			String update =
				"update ETS.ETS_DOC set LATEST_VERSION = '"
					+ cFlag
					+ "' where DOC_ID = "
					+ iDocID;
			int iRowsUpdated = stmtUpdLatestVer.executeUpdate(update);

			if (iRowsUpdated == 1)
				return true;
			else {
				m_pdLog.error(
					"executeUpdate(" + update + ") returned: " + iRowsUpdated);
				return false;
			}
		} finally {
			stmtUpdLatestVer.close();
		}

	}

	/**
	 * @param udDoc
	 * @return
	 * @throws SQLException In case of database errors
	 */
	private synchronized boolean addDoc(ETSDoc udDoc, String strAttachmentNames) 
		throws SQLException {
		boolean success = false;
		try {

			PreparedStatement stmtInsDoc =
				m_pdConnection.prepareStatement(
					"insert into ets.ets_doc("
						+ "doc_id,project_id,cat_id,user_id,"
						+ "doc_name,doc_description,doc_keywords,"
						+ "doc_size,doc_upload_date,doc_update_date,"
						+ "doc_publish_date, doc_type,updated_by,"
						+ "has_prev_version,latest_version,lock_final_flag,"
						+ "locked_by,delete_flag,deleted_by,meeting_id,"
						+ "ibm_only,document_status,approval_comments,"
						+ "expiry_date,self_id,isprivate,ibm_conf,"
						+ "itar_upload_status,issue_id) "
						+ "values(?,?,?,?,?,?,?,?,current timestamp,"
						+ "current timestamp,current timestamp,"
						+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			stmtInsDoc.setInt(1, udDoc.getId());
			stmtInsDoc.setString(2, udDoc.getProjectId());
			stmtInsDoc.setInt(3, udDoc.getCatId());
			stmtInsDoc.setString(4, udDoc.getUserId());
			stmtInsDoc.setString(5, udDoc.getName());
			stmtInsDoc.setString(6, udDoc.getDescription());
			stmtInsDoc.setString(7, udDoc.getKeywords());
			stmtInsDoc.setInt(8, udDoc.getSize());

			stmtInsDoc.setInt(9, udDoc.getDocType());
			stmtInsDoc.setString(10, udDoc.getUpdatedBy());
			stmtInsDoc.setString(
				11,
				String.valueOf(udDoc.getHasPreviousVersion()));
			stmtInsDoc.setString(
				12,
				String.valueOf(udDoc.getIsLatestVersion()));
			stmtInsDoc.setString(13, String.valueOf(udDoc.getLockFinalFlag()));
			stmtInsDoc.setString(14, udDoc.getLockedBy());
			stmtInsDoc.setString(15, String.valueOf(udDoc.getDeleteFlag()));
			stmtInsDoc.setString(16, udDoc.getDeletedBy());
			stmtInsDoc.setString(17, udDoc.getMeetingId());
			stmtInsDoc.setString(18, String.valueOf(udDoc.getIbmOnly()));
			stmtInsDoc.setString(19, String.valueOf(udDoc.getDocStatus()));

			stmtInsDoc.setString(20, udDoc.getApprovalComments());
			if (udDoc.getExpiryDate() == 0) {
				stmtInsDoc.setTimestamp(21, null);
			} else
				stmtInsDoc.setTimestamp(
					21,
					new Timestamp(udDoc.getExpiryDate()));

			stmtInsDoc.setString(22, udDoc.getSelfId());
			
			stmtInsDoc.setString(23, udDoc.getDPrivate());

			stmtInsDoc.setString(24, udDoc.getIBMConfidential());
			stmtInsDoc.setString(25, udDoc.getItarStatus());
			if (!StringUtil.isNullorEmpty(udDoc.getProblemId())) {
				stmtInsDoc.setString(26, udDoc.getProblemId());
			} else {
				stmtInsDoc.setNull(26, Types.VARCHAR);
			}

			stmtInsDoc.executeUpdate();
			stmtInsDoc.close();
			success = true;
			
			try {
			    String strEditHistory = null;
			    if (udDoc.hasPreviousVersion()) {
				    strEditHistory = "New document version added: '"+udDoc.getName() + "'.";
			    }
			    else {
				    strEditHistory = "New document added: '"+udDoc.getName() + "'.";
			    }
			    if (!StringUtil.isNullorEmpty(strAttachmentNames)) {
			        strEditHistory = strEditHistory + "\nAttachments: " + strAttachmentNames;
			    }
				setEditHistory(udDoc.getId(), udDoc.getUserId(), "Add Document", strEditHistory);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
		} catch (SQLException e) {
			e.printStackTrace(System.err);
			success = false;
			m_pdLog.error("sql error in add doc= " + e);
			throw e;
		} finally {
			
		}
		return success;
	}

	/**
	 * @param iDocID
	 * @param strDocFileName
	 * @param iDocSize
	 * @param pdInStream
	 * @return
	 * @throws SQLException In case of SQL Errors
	 */
	public synchronized boolean addDocFile(
		int iDocID,
		String strDocFileName,
		int iDocSize,
		InputStream pdInStream)
		throws SQLException {
		boolean bSuccess = false;

		try {
			int iNewDocFileID = getNewDocFileID(iDocID);

			PreparedStatement stmtInsDocFile =
				m_pdConnection.prepareStatement(
					"insert into ets.ets_docfile("
						+ "doc_id,docfile_id,docfile_name,docfile,"
						+ "docfile_size,docfile_update_date) "
						+ "values(?,?,?,?,?,current timestamp)");

			stmtInsDocFile.setInt(1, iDocID);
			stmtInsDocFile.setInt(2, iNewDocFileID);
			stmtInsDocFile.setString(3, strDocFileName);
			if (pdInStream != null) {
				stmtInsDocFile.setBinaryStream(4, pdInStream, iDocSize);
			} else {
				stmtInsDocFile.setNull(4, Types.BLOB);
			}
			stmtInsDocFile.setInt(5, iDocSize);
			stmtInsDocFile.executeUpdate();
			stmtInsDocFile.close();
			bSuccess = true;
		} catch (SQLException e) {
			bSuccess = false;
			m_pdLog.error("sql error in add doc file= " + e);
			throw e;
		}
		return bSuccess;
	}

	/**
	 * @param 
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public synchronized int getNewDocFileID(int iDocID) throws SQLException {

		try {

			Statement stmtGetDocID = m_pdConnection.createStatement();
			ResultSet rsGetDocID =
				stmtGetDocID.executeQuery(
					"select MAX(DOCFILE_ID) from ETS.ETS_DOCFILE WHERE DOC_ID="
						+ iDocID);

			int iMaxID = 0;

			if (rsGetDocID.next()) {
				iMaxID = rsGetDocID.getInt(1);
			} else {
				m_pdLog.error(
					"Empty ResultSet: select MAX(DOC_ID) from ETS.ETS_DOC");
			}

			rsGetDocID.close();
			stmtGetDocID.close();

			return (iMaxID + 1);
		} catch (SQLException e) {
			m_pdLog.error("error= " + DocUpdateDAO.getStackTrace(e));
			throw e;
		}

	}

	/**
	 * @param vtUsers
	 * @param strDocID
	 * @param strProjectId
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public boolean addAdditionalEditors(
		Vector vtUsers,
		String strDocID,
		String strProjectId)
		throws SQLException {
		boolean bSuccess = true;

		try {
			int iDocID = new Integer(strDocID).intValue();
			for (int iCounter = 0; iCounter < vtUsers.size(); iCounter++) {
				bSuccess = 
				    addAdditionalEditors(
						(String) vtUsers.elementAt(iCounter),  
						iDocID,  
						strProjectId, 
						Defines.DOC_EDIT_ACCESS );
			}
		} catch (SQLException e) {
			m_pdLog.error("sql error =" + DocUpdateDAO.getStackTrace(e));
			throw e;
		}
		return bSuccess;
	}

	/**
	* @param strUserID
	* @param iDocID
	* @param strProjectID
	* @param strAccessType
	* @return
	* @throws SQLException In case of database errors
	*/
	private boolean addAdditionalEditors(
		String strUserID,
		int iDocID,
		String strProjectID,
		String strAccessType)
		throws SQLException {
		try {
			String strQuery =
				"insert into ETS.ETS_PRIVATE_DOC(DOC_ID, USER_ID, PROJECT_ID, ACCESS_TYPE) "
					+ "values ("
					+ iDocID
					+ ",'"
					+ strUserID
					+ "','"
					+ strProjectID
					+ "','"+
					strAccessType +"')";

			Statement stmtInsDocUser = m_pdConnection.createStatement();
			int iRowCount = stmtInsDocUser.executeUpdate(strQuery);
			stmtInsDocUser.close();

			return true;
		} catch (SQLException e) {
			m_pdLog.error("sql error =" + DocUpdateDAO.getStackTrace(e));
			throw e;
		}
	}

	/**
	* @param strGroupIDs
	* @param iDocID
	* @param strProjectID
	* @return
	* @throws SQLException In case of database errors
	*/
	public boolean addDocResGroupsEdit(
		String[] strGroupIDs,
		int iDocID,
		String strProjectID)
		throws SQLException {
		try {
			String strQuery =
				"INSERT INTO ETS.ETS_PRIVATE_DOC "
					+ "(DOC_ID, PROJECT_ID, GROUP_ID, ACCESS_TYPE) "
					+ "VALUES (?, ?, ?, ?)";

			PreparedStatement stmtInsDocGrp =
				m_pdConnection.prepareStatement(strQuery);
			stmtInsDocGrp.setInt(1, iDocID);
			stmtInsDocGrp.setString(2, strProjectID);
			for (int iCount = 0; iCount < strGroupIDs.length; iCount++) {
				stmtInsDocGrp.setString(3, strGroupIDs[iCount]);
				stmtInsDocGrp.setString(4, Defines.DOC_EDIT_ACCESS);
				stmtInsDocGrp.executeUpdate();
			}

			stmtInsDocGrp.close();

			return true;
		} catch (SQLException e) {
			m_pdLog.error("sql error =" + DocUpdateDAO.getStackTrace(e));
			throw e;
		}
	}

	/**
	 * @param vtGroupIDs
	 * @param iDocID
	 * @param strProjectID
	 * @return
	 * @throws SQLException
	 */
	public boolean addDocResGroupsEdit(
			Vector vtGroupIDs,
			int iDocID,
			String strProjectID)
			throws SQLException {
	    String []strGroupIds = new String[vtGroupIDs.size()];
	    vtGroupIDs.toArray(strGroupIds);
	    
	    return addDocResGroupsEdit(strGroupIds, iDocID, strProjectID);
	}

	/**
	* @param strGroupIDs
	* @param iDocID
	* @param strProjectID
	* @return
	* @throws SQLException In case of database errors
	*/
	public boolean addDocResGroups(
		String[] strGroupIDs,
		int iDocID,
		String strProjectID)
		throws SQLException {
		try {
			String strQuery =
				"INSERT INTO ETS.ETS_PRIVATE_DOC "
					+ "(DOC_ID, PROJECT_ID, GROUP_ID, ACCESS_TYPE) "
					+ "VALUES (?,?,?, ?)";

			PreparedStatement stmtInsDocGrp =
				m_pdConnection.prepareStatement(strQuery);
			stmtInsDocGrp.setInt(1, iDocID);
			stmtInsDocGrp.setString(2, strProjectID);
			for (int iCount = 0; iCount < strGroupIDs.length; iCount++) {
				stmtInsDocGrp.setString(3, strGroupIDs[iCount]);
				stmtInsDocGrp.setString(4, Defines.DOC_READ_ACCESS);
				stmtInsDocGrp.executeUpdate();
			}

			stmtInsDocGrp.close();

			return true;
		} catch (SQLException e) {
			m_pdLog.error("sql error =" + DocUpdateDAO.getStackTrace(e));
			throw e;
		}
	}

	/**
	 * @param vtGroupIDs
	 * @param iDocID
	 * @param strProjectID
	 * @return
	 * @throws SQLException
	 */
	public boolean addDocResGroups(
			Vector vtGroupIDs,
			int iDocID,
			String strProjectID)
			throws SQLException {
	    String []strGroupIds = new String[vtGroupIDs.size()];
	    vtGroupIDs.toArray(strGroupIds);
	    
	    return addDocResGroups(strGroupIds, iDocID, strProjectID);
	}

	/**
	 * @param vtUsers
	 * @param strDocID
	 * @param strProjectId
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public boolean addDocResUsers(
		Vector vtUsers,
		String strDocID,
		String strProjectId)
		throws SQLException {
		boolean bSuccess = true;
		try {
			int iDocID = new Integer(strDocID).intValue();
			for (int iCounter = 0; iCounter < vtUsers.size(); iCounter++) {
				bSuccess = addDocResUsers(
						(String) vtUsers.elementAt(iCounter),  iDocID,  strProjectId, Defines.DOC_READ_ACCESS );
			}
		} catch (SQLException e) {
			m_pdLog.error("sql error =" + DocUpdateDAO.getStackTrace(e));
			throw e;
		}
		return bSuccess;
	}

	/**
	* @param strUserID
	* @param iDocID
	* @param strProjectID
	* @return
	* @throws SQLException In case of database errors
	*/
	public boolean addDocResUsers(
		String strUserID,
		int iDocID,
		String strProjectID,
		String strAccessType)
		throws SQLException {
		try {
			String strQuery =
				"insert into ETS.ETS_PRIVATE_DOC(DOC_ID, USER_ID, PROJECT_ID, ACCESS_TYPE) "
					+ "values ("
					+ iDocID
					+ ",'"
					+ strUserID
					+ "','"
					+ strProjectID
					+ "','"+
					strAccessType +"')";

			Statement stmtInsDocUser = m_pdConnection.createStatement();
			int iRowCount = stmtInsDocUser.executeUpdate(strQuery);
			stmtInsDocUser.close();

			return true;
		} catch (SQLException e) {
			m_pdLog.error("sql error =" + DocUpdateDAO.getStackTrace(e));
			throw e;
		}
	}

	/**
	 * @param iDocID
	 * @param bIsNotifyAll
	 * @param vtNotifyUsers
	 * @throws SQLException In case of database errors
	 */
	public synchronized void addDocNotificationList(
		int iDocID,
		boolean bIsNotifyAll,
		Vector vtNotifyUsers,
		String[] strGroups)
		throws SQLException {
		if (!bIsNotifyAll
			&& (vtNotifyUsers == null || vtNotifyUsers.size() == 0)
			&& (strGroups == null || strGroups.length == 0)) {
			return;
		}
		String strQuery = null;
		if (bIsNotifyAll) {
			strQuery =
				"INSERT INTO ETS.ETS_DOC_NOTIFY "
					+ "(DOC_ID, NOTIFY_ALL_FLAG, USER_ID, GROUP_ID) "
					+ "VALUES (?,'Y',NULL,NULL)";
		} else {
			strQuery =
				"INSERT INTO ETS.ETS_DOC_NOTIFY "
					+ "(DOC_ID, NOTIFY_ALL_FLAG, USER_ID, GROUP_ID) "
					+ "VALUES (?,'N',?,?)";
		}
		PreparedStatement stmtAddNotification =
			m_pdConnection.prepareStatement(strQuery);
		stmtAddNotification.setInt(1, iDocID);
		if (bIsNotifyAll) {
			stmtAddNotification.executeUpdate();
		} else {
			// Prepare Comma separated List of User Ids
			StringBuffer strBuffer = new StringBuffer(StringUtil.EMPTY_STRING);
			for (int iCounter = 0;
				iCounter < vtNotifyUsers.size();
				iCounter++) {
				String strUserID = (String) vtNotifyUsers.get(iCounter);
				strBuffer.append(strUserID);
				if (iCounter < (vtNotifyUsers.size() - 1)) {
					strBuffer.append(StringUtil.COMMA);
				}
			}
			stmtAddNotification.setString(2, strBuffer.toString());

			if (strGroups != null && strGroups.length > 0) {
				// Prepare Comma separated List of GROUP Ids
				strBuffer = new StringBuffer(StringUtil.EMPTY_STRING);
				for (int iCounter = 0;
					iCounter < strGroups.length;
					iCounter++) {
					String strGroupID = (String) strGroups[iCounter];
					strBuffer.append(strGroupID);
					if (iCounter < (strGroups.length - 1)) {
						strBuffer.append(StringUtil.COMMA);
					}
				}
				stmtAddNotification.setString(3, strBuffer.toString());
			} else {
				stmtAddNotification.setNull(3, Types.VARCHAR);
			}

			stmtAddNotification.executeUpdate();
		}
		stmtAddNotification.close();
	}

	/**
	 * @param strMailType
	 * @param strKey1
	 * @param strKey2
	 * @param strKey3
	 * @param strProjectId
	 * @param strSubject
	 * @param strToList
	 * @param strCCList
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public synchronized boolean addEmailLog(
		String strMailType,
		String strKey1,
		String strKey2,
		String strKey3,
		String strProjectId,
		String strSubject,
		String strToList,
		String strCCList)
		throws SQLException {
		PreparedStatement stmtAddEmailLog =
			m_pdConnection.prepareStatement(
				"insert into ets.ets_email_log("
					+ "timestamp, mail_type,key1,key2,key3,"
					+ "project_id,subject,to,cc) "
					+ "values(current timestamp,?,?,?,?,?,?,?,?)");

		if (!StringUtil.isNullorEmpty(strKey1) && strKey1.length() > 128) {
		    strKey1 = strKey1.substring(0, 128);
		}
		if (!StringUtil.isNullorEmpty(strKey2) && strKey2.length() > 128) {
		    strKey2 = strKey2.substring(0, 128);
		}
		if (!StringUtil.isNullorEmpty(strKey3) && strKey3.length() > 128) {
		    strKey3 = strKey3.substring(0, 128);
		}
		if (!StringUtil.isNullorEmpty(strSubject) && strSubject.length() > 120) {
		    strSubject = strSubject.substring(0, 120);
		}
		if (!StringUtil.isNullorEmpty(strToList) && strToList.length() > 1000) {
		    strToList = strToList.substring(0, 1000);
		}
		if (!StringUtil.isNullorEmpty(strCCList) && strCCList.length() > 1000) {
		    strCCList = strCCList.substring(0, 1000);
		}
		
		try {
			stmtAddEmailLog.setString(1, strMailType);
			stmtAddEmailLog.setString(2, strKey1);
			stmtAddEmailLog.setString(3, strKey2);
			stmtAddEmailLog.setString(4, strKey3);
			stmtAddEmailLog.setString(5, strProjectId);
			stmtAddEmailLog.setString(6, strSubject);
			stmtAddEmailLog.setString(7, strToList);
			stmtAddEmailLog.setString(8, strCCList);
			stmtAddEmailLog.executeUpdate();

			stmtAddEmailLog.close();
			return true;
		} catch (SQLException se) {
			m_pdLog.error("sql error =" + se);
			throw se;
		}

	}

	/**
	 * @param iDocID
	 * @param strDocFileName
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public boolean uploadProjectStatus(
            String strProjectId,
            String strSourceID,
            String strTabName,
            String strType,
            InputStream pdInStream,
            int iDocFileSize,
            String strUserID)
		throws SQLException {
	    boolean bStatus = false;
		String strQuery = 
		    "INSERT INTO ETS.WS_PROJECT_STATUS "
		    	+ "(PROJECT_ID, SOURCE_ID, DEST_ID, TYPE, STATUS_INFO, LAST_USERID, LAST_TIMESTAMP) "
		    	+ "VALUES (?,?,?,?,?,?,current timestamp)";
	    
		PreparedStatement stmtProjectStatus = m_pdConnection.prepareStatement(strQuery);
		try {
		    stmtProjectStatus.setString(1, strProjectId);
		    stmtProjectStatus.setString(2, strSourceID);
		    stmtProjectStatus.setString(3, strTabName);
		    stmtProjectStatus.setString(4, strType);
			if (pdInStream != null) {
			    stmtProjectStatus.setBinaryStream(5, pdInStream, iDocFileSize);
			} else {
			    stmtProjectStatus.setNull(5, Types.BLOB);
			}
		    stmtProjectStatus.setString(6, strUserID);
		    int iRowsUpdated = stmtProjectStatus.executeUpdate();
		    
		    bStatus = iRowsUpdated > 0;

		} finally {
			stmtProjectStatus.close();
		}
		return bStatus;

	}

    /**
     * @param strProjectId
     * @param strSourceID
     * @param strTabName
     * @return
     * @throws SQLException
     */
    public boolean deleteExistingProjectStatus(
            String strProjectId,
            String strSourceID,
            String strTabName
    	) throws SQLException {
        boolean bStatus = false;
		String strQuery = 
		    "DELETE FROM ETS.WS_PROJECT_STATUS "
		    	+ "WHERE PROJECT_ID=?";
	    
		PreparedStatement stmtProjectStatus = m_pdConnection.prepareStatement(strQuery);
		try {
		    stmtProjectStatus.setString(1, strProjectId);
		    //stmtProjectStatus.setString(2, strSourceID);
		    //stmtProjectStatus.setString(3, strTabName);
		    int iRowsUpdated = stmtProjectStatus.executeUpdate();
		    
		    bStatus = iRowsUpdated > 0;

		} finally {
			stmtProjectStatus.close();
		}
        return bStatus;
    }

    /**
     * @param strProjectId
     * @param strSourceID
     * @param strTabName
     * @return
     * @throws SQLException
     */
    public boolean deleteAllDocRestrictions(
            int iDocID, String strAccessType) throws SQLException {
        boolean bStatus = false;
		String strQuery = 
		    "DELETE FROM ETS.ETS_PRIVATE_DOC "
		    	+ "WHERE DOC_ID=? AND ACCESS_TYPE=?";
	    
		PreparedStatement stmtProjectStatus = m_pdConnection.prepareStatement(strQuery);
		try {
		    stmtProjectStatus.setInt(1, iDocID);
		    stmtProjectStatus.setString(2, strAccessType);
		    int iRowsUpdated = stmtProjectStatus.executeUpdate();
		    
		    bStatus = iRowsUpdated > 0;

		} finally {
			stmtProjectStatus.close();
		}
        return bStatus;
    }

    /**
	 * @param udDoc
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public synchronized boolean updateDocProp(ETSDoc udDoc)
		throws SQLException {
		PreparedStatement stmtUpdDoc =
			m_pdConnection.prepareStatement(
				"update ets.ets_doc "
					+ "set project_id=?,cat_id=?,doc_name=?,"
					+ "doc_description=?,doc_keywords=?,ibm_only=?,"
					+ "updated_by=?,doc_update_date=current timestamp,"
					+ "expiry_date=?,isprivate=?,ibm_conf=? where doc_id=?");
		try {
			//spn 0312 projid
			stmtUpdDoc.setString(1, udDoc.getProjectId());
			stmtUpdDoc.setInt(2, udDoc.getCatId());
			//stmtUpdDoc.setString(3, udDoc.getUserId());
			stmtUpdDoc.setString(3, udDoc.getName());
			stmtUpdDoc.setString(4, udDoc.getDescription());
			stmtUpdDoc.setString(5, udDoc.getKeywords());
			stmtUpdDoc.setString(6, String.valueOf(udDoc.getIbmOnly()));
			stmtUpdDoc.setString(7, udDoc.getUpdatedBy());
			//FIX FIX FIX FIX EXPIRE DATE
			if (udDoc.getExpiryDate() == 0)
				stmtUpdDoc.setTimestamp(8, null);
			else
				stmtUpdDoc.setTimestamp(
					8,
					new Timestamp(udDoc.getExpiryDate()));

			stmtUpdDoc.setString(9, udDoc.getDPrivate());
			stmtUpdDoc.setString(10, udDoc.getIBMConfidential());
			stmtUpdDoc.setInt(11, udDoc.getId());

			stmtUpdDoc.executeUpdate();
			stmtUpdDoc.close();
			addContentLog(
				udDoc.getId(),
				'D',
				udDoc.getProjectId(),
				Defines.UPDATE_DOC_PROP,
				udDoc.getUserId());
			return true;
		} catch (SQLException se) {
			m_pdLog.error("sql error =" + se);
			throw se;
		}
	}

	/**
	 * @param iDocID
	 * @return boolean
	 * @throws SQLException In case of database errors
	 * @throws Exception
	 */
	public synchronized boolean clearNotificationList(int iDocID)
		throws SQLException {

		String strQuery = StringUtil.EMPTY_STRING;
		Statement stmtDocRes = m_pdConnection.createStatement();

		strQuery = "delete from ets.ets_doc_notify where doc_id = "+ iDocID;
		stmtDocRes.executeUpdate(strQuery);

		stmtDocRes.close();

		return true;
	}
}