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

package oem.edge.ets.fe.documents.webservice;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import oem.edge.common.DbConnect;
import oem.edge.common.Global;
import oem.edge.ets.fe.Defines;

import org.apache.log4j.Logger;

/**
 * This is the DATA ACCESS Class for the E&TS Documents Module
 * 
 * @author v2srikau
 */
public class DocumentServiceDAO {

	/** Stores the Logging object */
    private static final Logger m_pdLog = Logger
            .getLogger(DocumentServiceDAO.class);

	/** Stores the Connection Object */
	private Connection m_pdConnection = null;

	public static final int MAX_DOC_VERSIONS = 1000;
	public static final int STARTING_DOC_ID = 10000 * MAX_DOC_VERSIONS;
	public static final int MAXIMUM_DOC_ID = 99999 * MAX_DOC_VERSIONS;
	
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
     * Gets a new connection object.
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

        }
        catch (SQLException e) {
			m_pdLog.error(e.getMessage());
			throw e;
        }
        catch (Exception ex) {
			m_pdLog.error(ex.getMessage());
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
            }
            else {
				sDataSource = sDataSource.trim();
			}

        }
        catch (Exception e) {
			throw e;
		}

		return sDataSource;
	}

	/**
	 * @param strUserId
	 * @return
	 * @throws SQLException
	 */
    public List getProjects(String strUserId, boolean bIsAdmin) throws SQLException {
		List lstProjects = new ArrayList();
		String strQuery = null;
		String[] strTypes = new String[2];
        if (bIsAdmin) {
	            strQuery = "SELECT PROJECT_ID,PROJECT_NAME,PROJECT_TYPE,IS_PRIVATE,IS_ITAR "
	                    + "FROM ETS.ETS_PROJECTS "
	                    + "WHERE UCASE(PROJECT_OR_PROPOSAL) IN ('P','O') "
	                    + "AND IS_ITAR = 'N' "
	                    + "AND PROJECT_STATUS NOT IN ('"
	                    + Defines.WORKSPACE_DELETE
					+ "' , '"
	                    + Defines.WORKSPACE_ARCHIVE
	                    +"') "
	                    + "AND PROJECT_ID IN ( "
	                    + "SELECT PROJECT_ID FROM ETS.ETS_CAT "
	                    + "WHERE PARENT_ID = 0 AND VIEW_TYPE = "
	                    + Defines.DOCUMENTS_VT
	                    + ") "
	                    + "AND PROJECT_TYPE = 'ETS' "
	                    + "ORDER BY PROJECT_NAME with UR";
        }
        else {
            strQuery = "SELECT PROJECT_ID,PROJECT_NAME,"
                    + "PROJECT_TYPE,IS_PRIVATE,IS_ITAR "
                    + "FROM ETS.ETS_PROJECTS "
                    + "WHERE UCASE(PROJECT_OR_PROPOSAL) IN ('P','O') "
                    + "AND IS_ITAR = 'N' "
                    + "AND PROJECT_ID IN ( "
                    + "SELECT USER_PROJECT_ID "
                    + "FROM ETS.ETS_USERS "
                    + "WHERE USER_ID = '"
                    + strUserId
                    + "' AND ACTIVE_FLAG = '"
                    + Defines.USER_ENTITLED
                    + "') "
                    + "AND PROJECT_STATUS NOT IN ('"
                    + Defines.WORKSPACE_DELETE
                    + "', '"
                    +Defines.WORKSPACE_ARCHIVE
                    +"') "                    
                    + "AND PROJECT_ID IN ( "
                    + "SELECT PROJECT_ID FROM ETS.ETS_CAT "
                    + "WHERE PARENT_ID = 0 AND VIEW_TYPE = "
                    + Defines.DOCUMENTS_VT
                    + ") "
                    + "AND PROJECT_TYPE = 'ETS' "
                    + "ORDER BY PROJECT_NAME WITH UR";
        }

        Statement stmtProjects = m_pdConnection.createStatement();
        ResultSet rsProjects = stmtProjects.executeQuery(strQuery);

        while (rsProjects.next()) {
            Workspace udWorkspace = new Workspace();
            udWorkspace.setWorkspaceId(rsProjects.getString("PROJECT_ID"));
            udWorkspace.setWorkspaceName(rsProjects.getString("PROJECT_NAME"));
            udWorkspace.setWorkspaceType(rsProjects.getString("PROJECT_TYPE"));
            udWorkspace.setPrivate(rsProjects.getString("IS_PRIVATE"));
            udWorkspace.setITAR("Y".equalsIgnoreCase(rsProjects
                    .getString("IS_ITAR")));

            lstProjects.add(udWorkspace);
		}
        rsProjects.close();
        stmtProjects.close();

			return lstProjects;
		}

    /**
     * @param strUserId
     * @return
     * @throws SQLException
     */
    public List getAICProjects(String strUserId, boolean bIsAdmin) throws SQLException {
        List lstProjects = new ArrayList();
        String strQuery = null;
        String[] strTypes = new String[2];
        if (bIsAdmin) {
            strQuery = "SELECT PROJECT_ID,PROJECT_NAME,PROJECT_TYPE,IS_PRIVATE,IS_ITAR "
                    + "FROM ETS.ETS_PROJECTS "
                    + "WHERE UCASE(PROJECT_OR_PROPOSAL) IN ('P','O') "
                    + "AND IS_ITAR = 'N' "
                    + "AND PROJECT_STATUS NOT IN ('"
                    + Defines.WORKSPACE_DELETE
                    + "', '"
                    + Defines.WORKSPACE_ARCHIVE
                    + "') "
                    + "AND PROJECT_ID IN ( "
                    + "SELECT PROJECT_ID FROM ETS.ETS_CAT "
                    + "WHERE PARENT_ID = 0 AND VIEW_TYPE = "
                    + Defines.DOCUMENTS_VT
                    + ") "
                    + "AND PROJECT_TYPE = 'AIC' "
                    + "ORDER BY PROJECT_NAME WITH UR";
        }
        else {
            strQuery = "SELECT PROJECT_ID,PROJECT_NAME,"
                    + "PROJECT_TYPE,IS_PRIVATE,IS_ITAR "
					+ "FROM ETS.ETS_PROJECTS "
				+ "WHERE UCASE(PROJECT_OR_PROPOSAL) IN ('P','O') "
					+ "AND IS_ITAR = 'N' "
					+ "AND PROJECT_ID IN ("
                    + "SELECT USER_PROJECT_ID "
                    + "FROM ETS.ETS_USERS "
                    + "WHERE USER_ID = '"
					+ strUserId
					+ "' AND ACTIVE_FLAG = '"
				+ Defines.USER_ENTITLED
                    + "') "
                    + "AND PROJECT_STATUS NOT IN ('"
				+ Defines.WORKSPACE_DELETE
                    + "', '"
                    + Defines.WORKSPACE_ARCHIVE
                    + "') "
				+ "AND PROJECT_ID IN ("
				+ "SELECT PROJECT_ID FROM ETS.ETS_CAT "
				+ "WHERE PARENT_ID = 0 AND VIEW_TYPE = "
				+ Defines.DOCUMENTS_VT
				+ ")"
                    + "AND PROJECT_TYPE = 'AIC' "
                    + "ORDER BY PROJECT_NAME WITH UR";
        }

		Statement stmtProjects = m_pdConnection.createStatement();
		ResultSet rsProjects = stmtProjects.executeQuery(strQuery);

		while (rsProjects.next()) {
			Workspace udWorkspace = new Workspace();
			udWorkspace.setWorkspaceId(rsProjects.getString("PROJECT_ID"));
			udWorkspace.setWorkspaceName(rsProjects.getString("PROJECT_NAME"));
			udWorkspace.setWorkspaceType(rsProjects.getString("PROJECT_TYPE"));
			udWorkspace.setPrivate(rsProjects.getString("IS_PRIVATE"));
            udWorkspace.setITAR("Y".equalsIgnoreCase(rsProjects
                    .getString("IS_ITAR")));

			lstProjects.add(udWorkspace);
		}
		rsProjects.close();
		stmtProjects.close();

		return lstProjects;
	}

	/**
     * @param strUserId
     * @return
     * @throws SQLException
     */
    public List getAICPublicProjects(String strUserId) throws SQLException {
        List lstProjects = new ArrayList();
        String strQuery = null;
        
        strQuery = "SELECT PROJECT_ID,PROJECT_NAME,"
	        + "PROJECT_TYPE,IS_PRIVATE,IS_ITAR "
			+ "FROM ETS.ETS_PROJECTS "
			+ "WHERE UCASE(PROJECT_OR_PROPOSAL) IN ('P','O') "
			+ "AND IS_ITAR = 'N' "
			+ "AND IS_PRIVATE ='"
			+ Defines.AIC_IS_PRIVATE_PUBLIC
			+ "' "
			+ "AND PROJECT_ID NOT IN ("
	        + "SELECT USER_PROJECT_ID "
	        + "FROM ETS.ETS_USERS "
	        + "WHERE USER_ID = '"
			+ strUserId
			+ "' AND ACTIVE_FLAG = '"
			+ Defines.USER_ENTITLED
	        + "') "
	        + "AND PROJECT_STATUS != '"
			+ Defines.WORKSPACE_DELETE
	        + "' "
			+ "AND PROJECT_ID IN ("
			+ "SELECT PROJECT_ID FROM ETS.ETS_CAT "
			+ "WHERE PARENT_ID = 0 AND VIEW_TYPE = "
			+ Defines.DOCUMENTS_VT
			+ ")"
			+ "AND PROJECT_TYPE ='"
			+ Defines.AIC_WORKSPACE_TYPE 
			+ "' "
			+ "ORDER BY PROJECT_NAME WITH UR";
        
        Statement stmtProjects = m_pdConnection.createStatement();
        ResultSet rsProjects = stmtProjects.executeQuery(strQuery);

        while (rsProjects.next()) {
            Workspace udWorkspace = new Workspace();
            udWorkspace.setWorkspaceId(rsProjects.getString("PROJECT_ID"));
            udWorkspace.setWorkspaceName(rsProjects.getString("PROJECT_NAME"));
            udWorkspace.setWorkspaceType(rsProjects.getString("PROJECT_TYPE"));
            udWorkspace.setPrivate(rsProjects.getString("IS_PRIVATE"));
            udWorkspace.setITAR("Y".equalsIgnoreCase(rsProjects
                    .getString("IS_ITAR")));

            lstProjects.add(udWorkspace);
        }
        rsProjects.close();
        stmtProjects.close();

        return lstProjects;
    }

    /**
	 * @param strWorkspaceId
	 * @return
	 * @throws SQLException
	 */
    public List getFolders(String strWorkspaceId, boolean bIsAllFolders) throws SQLException {
		List lstFolders = new ArrayList();
		String strQuery = null;
        if (bIsAllFolders) {
        strQuery = "SELECT CAT_ID, CAT_NAME, PARENT_ID, IBM_ONLY "
				+ "FROM ETS.ETS_CAT "
				+ "WHERE PROJECT_ID = '"
				+ strWorkspaceId
				+ "' AND DISPLAY_FLAG IS NULL "
				+ "AND VIEW_TYPE = "
                + Defines.DOCUMENTS_VT
                + " AND PARENT_ID != 0 WITH UR";
        }
        else {
            strQuery = "SELECT CAT_ID, CAT_NAME, PARENT_ID, IBM_ONLY "
                + "FROM ETS.ETS_CAT "
                + "WHERE PROJECT_ID = '"
                + strWorkspaceId
                + "' AND DISPLAY_FLAG IS NULL "
                + "AND VIEW_TYPE = "
                + Defines.DOCUMENTS_VT
                + " WITH UR";
        }

		Statement stmtFolders = m_pdConnection.createStatement();
		ResultSet rsFolders = stmtFolders.executeQuery(strQuery);

		while (rsFolders.next()) {
			Category udCat = new Category();
			udCat.setCat(rsFolders.getString("CAT_NAME"));
			udCat.setCatId(rsFolders.getInt("CAT_ID"));
			udCat.setParentId(rsFolders.getInt("PARENT_ID"));

            if (rsFolders.getInt("IBM_ONLY") != 0) {
                udCat.setIBMOnly(true);
            }
            else {
                udCat.setIBMOnly(false);
            }

			lstFolders.add(udCat);
		}

        rsFolders.close();
        stmtFolders.close();
		return lstFolders;
	}

	/**
	 * @param iCatID
	 * @param strName
	 * @param strDescription
	 * @param iSize
	 * @return
	 * @throws SQLException
	 */
    public synchronized int addDoc(int iCatID, String strName,
            String strDescription, String strDocAuthor, String strIBMOnly,
            String strProjectId, int iSize, Date dtExpiry) throws SQLException {
		int iNewDocID = -1;
		try {

			iNewDocID = getNewDocID();
            PreparedStatement stmtInsDoc = m_pdConnection
                    .prepareStatement("insert into ets.ets_doc("
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

            stmtInsDoc.setInt(1, iNewDocID); // DOC_ID
            stmtInsDoc.setString(2, strProjectId); // PROJECT_ID
            stmtInsDoc.setInt(3, iCatID); // CAT_ID
            stmtInsDoc.setString(4, strDocAuthor); // USER_ID
            stmtInsDoc.setString(5, strName); // DOC_NAME
            stmtInsDoc.setString(6, strDescription); // DOC_DESCRIPTION
            stmtInsDoc.setNull(7, Types.VARCHAR); // DOC_KEYWORDS
            stmtInsDoc.setInt(8, iSize); // DOC_SIZE

            stmtInsDoc.setInt(9, 0); // DOC_TYPE
            stmtInsDoc.setNull(10, Types.VARCHAR); // UPDATED_BY
            stmtInsDoc.setString(11, "0"); // HAS_PREVIOUS_VERSION
            stmtInsDoc.setString(12, "1"); // LATEST_VERSION
            stmtInsDoc.setNull(13, Types.VARCHAR); // LOCK_FINAL_FLAG
            stmtInsDoc.setNull(14, Types.VARCHAR); // LOCKED_BY
            stmtInsDoc.setString(15, "0"); // DELETE_FLAG
            stmtInsDoc.setNull(16, Types.VARCHAR); // DELETED_BY
            stmtInsDoc.setNull(17, Types.VARCHAR); // MEETING_ID
            stmtInsDoc.setString(18, strIBMOnly); // IBM_ONLY
            stmtInsDoc.setString(19, "P"); // DOCUMENT_STATUS

            stmtInsDoc.setNull(20, Types.VARCHAR); // APPROVAL_COMMENTS

            if (dtExpiry == null) {
                stmtInsDoc.setTimestamp(21, null); // EXPIRY_DATE
            }
            else {
                stmtInsDoc.setTimestamp(21, new Timestamp(dtExpiry.getTime()));
            }
            
            
            stmtInsDoc.setNull(22, Types.VARCHAR); // SELF_ID
            stmtInsDoc.setNull(23, Types.VARCHAR); // ISPRIVATE
            stmtInsDoc.setNull(24, Types.VARCHAR); // IBM_CONF
            stmtInsDoc.setString(25, "C"); // ITAR_UPLOAD_STATUS
            stmtInsDoc.setNull(26, Types.VARCHAR); // ISSUE_ID

			stmtInsDoc.executeUpdate();
			stmtInsDoc.close();
        }
        catch (SQLException e) {
			e.printStackTrace(System.err);
			m_pdLog.error("sql error in add doc= " + e);
			throw e;
        }
			return iNewDocID;
		}

	/**
	 * @return
	 * @throws SQLException
	 */
	private synchronized int getNewDocID() throws SQLException {

		try {

			Statement stmtGetDocID = m_pdConnection.createStatement();
            ResultSet rsGetDocID = stmtGetDocID
                    .executeQuery("select MAX(DOC_ID) from ETS.ETS_DOC WITH UR");

			int iMaxID = -1;

			if (rsGetDocID.next()) {
				iMaxID = rsGetDocID.getInt(1);
            }
            else {
                m_pdLog.error("Empty ResultSet for query: select MAX(DOC_ID) "
						+ "from ETS.ETS_DOC");
			}

			rsGetDocID.close();
			stmtGetDocID.close();

			if (iMaxID < STARTING_DOC_ID)
				iMaxID = STARTING_DOC_ID;

			if (iMaxID >= MAXIMUM_DOC_ID) {
				m_pdLog.error("Will exceed MAXIMUM_DOC_ID");
				return -1;
            }
            else {
				return (((iMaxID / MAX_DOC_VERSIONS) + 1) * MAX_DOC_VERSIONS);
			}

        }
        catch (SQLException e) {
			m_pdLog.error(e);
			throw e;
		}

	}

	/**
	 * @param iDocID
	 * @param bIsNotifyAll
	 * @param vtNotifyUsers
     * @throws SQLException
     *             In case of database errors
	 */
	public synchronized void addDocNotificationList(int iDocID)
		throws SQLException {
        String strQuery = "INSERT INTO ETS.ETS_DOC_NOTIFY "
				+ "(DOC_ID, NOTIFY_ALL_FLAG, USER_ID, GROUP_ID) "
				+ "VALUES (?,'Y',NULL,NULL)";
        PreparedStatement stmtAddNotification = m_pdConnection
                .prepareStatement(strQuery);
		stmtAddNotification.setInt(1, iDocID);
		stmtAddNotification.executeUpdate();
		stmtAddNotification.close();
	}

	/**
	 * @param iCatID
	 * @return
     * @throws SQLException
     *             In case of database errors
	 */
    public String isCatIBMOnly(int iCatID) throws SQLException {
		String strIBMOnly = null;
        PreparedStatement stmtProjMembers = m_pdConnection
                .prepareStatement("SELECT IBM_ONLY "
					+ "FROM ETS.ETS_CAT "
					+ "WHERE CAT_ID = ? WITH UR");

		stmtProjMembers.setInt(1, iCatID);
		ResultSet rsProjMembers = stmtProjMembers.executeQuery();

		if (rsProjMembers.next()) {
			strIBMOnly = rsProjMembers.getString("IBM_ONLY");
		}

		rsProjMembers.close();
		stmtProjMembers.close();
		return strIBMOnly;
    }


	/**
	 * @param iDocID
	 * @param strDocFileName
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public boolean deleteAttachment(
		int iDocID,
		String strDocFileName)
		throws SQLException {

		String strQuery = 
		    "DELETE FROM ETS.ETS_DOCFILE " 
		    	+ "WHERE DOC_ID = ? AND DOCFILE_NAME = ?";
	    
		PreparedStatement stmtDeleteFile = m_pdConnection.prepareStatement(strQuery);
		try {
		    stmtDeleteFile.setInt(1, iDocID);
		    stmtDeleteFile.setString(2, strDocFileName);
		    int iRowsUpdated = stmtDeleteFile.executeUpdate();

			if (iRowsUpdated > 0) {
				return true;
			}
			else {
				return false;
			}
		} finally {
			stmtDeleteFile.close();
		}

	}

    /**
     * @param strProjectId
     * @return
     * @throws SQLException
     */
    public Workspace getWorkspaceDetails(String strProjectId) throws SQLException {
        String strQuery = null;
        
        strQuery = "SELECT PROJECT_ID,PROJECT_NAME,"
	        + "PROJECT_TYPE,IS_PRIVATE,IS_ITAR, COMPANY "
			+ "FROM ETS.ETS_PROJECTS "
			+ "WHERE PROJECT_ID = ? with ur";
        
        PreparedStatement stmtProjects = m_pdConnection.prepareStatement(strQuery);
        stmtProjects.setString(1, strProjectId);
        ResultSet rsProjects = stmtProjects.executeQuery();
        
        Workspace udWorkspace = null;
        if (rsProjects.next()) {
            udWorkspace = new Workspace();
            udWorkspace.setWorkspaceId(rsProjects.getString("PROJECT_ID"));
            udWorkspace.setWorkspaceName(rsProjects.getString("PROJECT_NAME"));
            udWorkspace.setWorkspaceType(rsProjects.getString("PROJECT_TYPE"));
            udWorkspace.setPrivate(rsProjects.getString("IS_PRIVATE"));
            udWorkspace.setITAR("Y".equalsIgnoreCase(rsProjects
                    .getString("IS_ITAR")));
            udWorkspace.setCompany(rsProjects.getString("COMPANY"));
        }
        rsProjects.close();
        stmtProjects.close();

        return udWorkspace;
        
    }
}
