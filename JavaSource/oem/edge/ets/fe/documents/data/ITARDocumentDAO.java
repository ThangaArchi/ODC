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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import oem.edge.common.DbConnect;
import oem.edge.common.Global;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.documents.common.ETSDocFile;

/**
 * This is the DATA ACCESS Class for the E&TS Documents Module
 * @author v2srikau
 */
public class ITARDocumentDAO {

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

	/**
	 * @param iDocID
	 * @param strProjectId
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public ETSDoc getDocByIdAndProject(int iDocID, String strProjectId)
		throws SQLException {

	    ETSDoc udDoc = new ETSDoc();
	    udDoc.setDocFiles(getDocFiles(iDocID));

		return udDoc;
	}

	/**
	 * @param iDocId
	 * @return
	 * @throws SQLException
	 */
	public List getDocFiles(int iDocId) throws SQLException {
		Statement stmtDocDetails = m_pdConnection.createStatement();

		List lstDocFiles = new ArrayList();
		ResultSet rsDocDetails =
			stmtDocDetails.executeQuery(
				"SELECT DOCFILE_ID, DOCFILE_NAME, "
					+ "DOCFILE_SIZE, DOCFILE_UPDATE_DATE "
					+ "FROM ETS.ETS_DOCFILE WHERE DOC_ID = "
					+ iDocId
					+ " WITH UR");
		while (rsDocDetails.next()) {
			ETSDocFile udDocFile = new ETSDocFile();
			udDocFile.setDocfileId(rsDocDetails.getInt("DOCFILE_ID"));
			udDocFile.setFileName(rsDocDetails.getString("DOCFILE_NAME"));
			udDocFile.setSize(rsDocDetails.getInt("DOCFILE_SIZE"));

			lstDocFiles.add(udDocFile);
		}

		rsDocDetails.close();
		stmtDocDetails.close();

		return lstDocFiles;
	}

	/**
	 * @param iDocId
	 * @param iDocFileId
	 * @return
	 * @throws SQLException
	 */
	public ETSDocFile getDocFile(int iDocId, int iDocFileId)
		throws SQLException {
		Statement stmtDocDetails = m_pdConnection.createStatement();

		ResultSet rsDocDetails =
			stmtDocDetails.executeQuery(
				"SELECT DOCFILE_ID, DOCFILE_NAME, "
					+ "DOCFILE_SIZE, DOCFILE_UPDATE_DATE "
					+ "FROM ETS.ETS_DOCFILE WHERE DOC_ID = "
					+ iDocId
					+ " AND DOCFILE_ID = "
					+ iDocFileId
					+ " WITH UR");

		ETSDocFile udDocFile = new ETSDocFile();
		if (rsDocDetails.next()) {
			udDocFile.setDocfileId(rsDocDetails.getInt("DOCFILE_ID"));
			udDocFile.setFileName(rsDocDetails.getString("DOCFILE_NAME"));
			udDocFile.setSize(rsDocDetails.getInt("DOCFILE_SIZE"));
		}

		rsDocDetails.close();
		stmtDocDetails.close();

		return udDocFile;
	}

	/**
	 * @param iDocId
	 * @param iDocFileId
	 * @return
	 * @throws SQLException
	 */
	public boolean delDocFile(int iDocId, int iDocFileId)
		throws SQLException {
		Statement stmtDocDetails = m_pdConnection.createStatement();

		int iUpdateCount =
			stmtDocDetails.executeUpdate(
				"DELETE FROM ETS.ETS_DOCFILE WHERE DOC_ID = "
					+ iDocId
					+ " AND DOCFILE_ID = "
					+ iDocFileId);

		stmtDocDetails.close();

		return (iUpdateCount > 0);
	}

	/**
	 * @param iDocId
	 * @param iDocFileId
	 * @return
	 * @throws SQLException
	 */
	public boolean delDocFile(int iDocId)
		throws SQLException {
		Statement stmtDocDetails = m_pdConnection.createStatement();

		int iUpdateCount =
			stmtDocDetails.executeUpdate(
				"DELETE FROM ETS.ETS_DOCFILE WHERE DOC_ID = " + iDocId);

		stmtDocDetails.close();
		return (iUpdateCount > 0);
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
		//		m_pdLog.info("FILE NAME===" + rsDoc.getString("DOCFILE_NAME"));
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

		udDoc.setIBMConfidential(rsDoc.getString("IBM_CONF"));

		if (rsDoc.getTimestamp("EXPIRY_DATE") != null) {
			udDoc.setExpiryDate(rsDoc.getTimestamp("EXPIRY_DATE"));
		} else {
			udDoc.setExpiryDate(0);
		}

		return udDoc;
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
			if (e.getCause() != null) {
				e.getCause().printStackTrace(System.err);
			}
			e.printStackTrace();
			throw e;
		} catch (Exception ex) {
			if (ex.getCause() != null) {
				ex.getCause().printStackTrace(System.err);
			}
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

			ResourceBundle rb =
				ResourceBundle.getBundle("oem.edge.ets.fe.ets-itar");
				
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
			e.printStackTrace(System.err);
			throw e;
		}
		return bSuccess;
	}

	/**
	 * @param iDocID
	 * @param strDocFileName
	 * @param strFileDescription
	 * @param iDocSize
	 * @param pdInStream
	 * @return
	 * @throws SQLException In case of SQL Errors
	 */
	public synchronized boolean addDocFile(
		int iDocID,
		String strDocFileName,
		int iDocSize,
		InputStream pdInStream,
		String strFileDescription,
		String strFileStatus)
		throws SQLException {
		boolean bSuccess = false;

		try {
			int iNewDocFileID = getNewDocFileID(iDocID);

			PreparedStatement stmtInsDocFile =
				m_pdConnection.prepareStatement(
					"insert into ets.ets_docfile("
						+ "doc_id,docfile_id,docfile_name,docfile,"
						+ "docfile_size,docfile_update_date,"
						+ "file_description,file_status) "
						+ "values(?,?,?,?,?,current timestamp,?,?)");

			stmtInsDocFile.setInt(1, iDocID);
			stmtInsDocFile.setInt(2, iNewDocFileID);
			stmtInsDocFile.setString(3, strDocFileName);
			if (pdInStream != null) {
				stmtInsDocFile.setBinaryStream(4, pdInStream, iDocSize);
			} else {
				stmtInsDocFile.setNull(4, Types.BLOB);
			}
			stmtInsDocFile.setInt(5, iDocSize);
			stmtInsDocFile.setString(6, strFileDescription);
			stmtInsDocFile.setString(7, strFileStatus);
			stmtInsDocFile.executeUpdate();
			stmtInsDocFile.close();
			bSuccess = true;
		} catch (SQLException e) {
			bSuccess = false;
			e.printStackTrace(System.err);
			throw e;
		}
		return bSuccess;
	}

	/**
	 * @param 
	 * @return
	 * @throws SQLException In case of database errors
	 */
	private synchronized int getNewDocFileID(int iDocID) throws SQLException {

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
				System.out.println(
					"Empty ResultSet: select MAX(DOC_ID) from ETS.ETS_DOC");
			}

			rsGetDocID.close();
			stmtGetDocID.close();

			return (iMaxID + 1);
		} catch (SQLException e) {
			e.printStackTrace(System.err);
			throw e;
		}

	}
}
