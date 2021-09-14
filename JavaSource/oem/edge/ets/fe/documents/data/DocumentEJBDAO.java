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
import java.util.Enumeration;
import java.util.ResourceBundle;

import oem.edge.common.DbConnect;
import oem.edge.common.Global;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.ETSDocEditHistory;
import oem.edge.ets.fe.documents.common.StringUtil;

import org.apache.log4j.Logger;

/**
 * This is the DATA ACCESS Class for the E&TS Documents Module
 * @author v2srikau
 */
public class DocumentEJBDAO {

	/** Stores the Logging object */
	private Logger m_pdLog = Logger.getLogger(DocumentEJBDAO.class);

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
				ResourceBundle.getBundle("oem.edge.ets.fe.ets");

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
						+ "docfile_size,docfile_update_date," +							"file_description,file_status) "
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
				m_pdLog.error(
					"Empty ResultSet: select MAX(DOC_ID) from ETS.ETS_DOC");
			}

			rsGetDocID.close();
			stmtGetDocID.close();

			return (iMaxID + 1);
		} catch (SQLException e) {
			m_pdLog.error("error= " + e);
			throw e;
		}

	}

	/**
	 * @param iDocId
	 * @param strProjectId
	 * @param strStatus
	 * @throws SQLException
	 */
	public synchronized void updateDocStatus(
		int iDocId,
		String strProjectId,
		String strStatus)
		throws SQLException {
		PreparedStatement upDocSt = null;

		upDocSt =
			m_pdConnection.prepareStatement(
				"UPDATE ETS.ETS_DOC "
					+ "SET ITAR_UPLOAD_STATUS=? "
					+ "WHERE DOC_ID=? "
					+ "AND PROJECT_ID = ? "
					+ "AND ITAR_UPLOAD_STATUS=?");
		try {
			upDocSt.setString(1, strStatus);
			upDocSt.setInt(2, iDocId);
			upDocSt.setString(3, strProjectId);
			upDocSt.setString(4, "P");

			upDocSt.executeUpdate();
			upDocSt.close();
		} catch (SQLException se) {
			m_pdLog.error("sql error =" + se);
			throw se;
		}
	}

	/**
	 * @param iDocID 
	 * @return
	 * @throws SQLException In case of database errors
	 */
	private synchronized int getSequenceNumber(int iDocID) throws SQLException {

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
			m_pdLog.error("error= " + e);
			throw e;
		}
	}

	public synchronized boolean setEditHistory(ETSDocEditHistory pEtsDocHistory) throws SQLException, Exception {
		 String strQuery = StringUtil.EMPTY_STRING;
		 int iSeqNum = getSequenceNumber(pEtsDocHistory.getDocId());
		 try{
		 Statement stmtUpdDocResUsers = m_pdConnection.createStatement();

		  strQuery = "INSERT INTO ETS.DOC_EDIT_HISTORY(DOC_ID, SEQ_NO, USER_ID, " 
		          +"ACTION, ACTION_DETAILS, LAST_TIMESTAMP) "
		          + "VALUES"
		          + "("+ pEtsDocHistory.getDocId() +","+ iSeqNum +",'"+ pEtsDocHistory.getUserId() +"','"
		          + pEtsDocHistory.getAction() +"','"+ pEtsDocHistory.getActionDetails() +"', CURRENT TIMESTAMP )";
		 
		  //m_pdLog.debug("SQL :: setEditHistory() - "+ strQuery);   
		  
		  stmtUpdDocResUsers.executeUpdate(strQuery);

		 } catch (SQLException e) {
		  m_pdLog.error("sql error =" + e);
		  throw e;
		 }
		 return true;
		}

	/**
	 * @param udDoc
	 * @return
	 * @throws SQLException
	 */
	public synchronized boolean updateDocProperties(ETSDoc udDoc) 
		throws SQLException {
	    boolean bSuccess = false;
		PreparedStatement stmtUpdateDoc = null;

		stmtUpdateDoc =
			m_pdConnection.prepareStatement(
				"UPDATE ETS.ETS_DOC "
					+ "SET DOC_NAME=?, DOC_DESCRIPTION=?, DOC_KEYWORDS=? "
					+ "WHERE DOC_ID=?");
		try {
			stmtUpdateDoc.setString(1, udDoc.getName());
			stmtUpdateDoc.setString(2, udDoc.getDescription());
			stmtUpdateDoc.setString(3, udDoc.getKeywords());
			stmtUpdateDoc.setInt(4, udDoc.getId());

			int iCount = stmtUpdateDoc.executeUpdate();
			stmtUpdateDoc.close();
			bSuccess = (iCount == 1);
		} catch (SQLException se) {
			m_pdLog.error("sql error =" + se);
			throw se;
		}
	    
	    return bSuccess;
	}
}
