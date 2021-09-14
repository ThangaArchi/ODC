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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import oem.edge.amt.AMTException;
import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.AmtCommonUtils;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSDetailedObj;
import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.ETSDocComment;
import oem.edge.ets.fe.ETSDocEditHistory;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.dealtracker.ETSTask;
import oem.edge.ets.fe.documents.BaseDocumentForm;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.DocExpirationDate;
import oem.edge.ets.fe.documents.common.DocMetrics;
import oem.edge.ets.fe.documents.common.DocNotify;
import oem.edge.ets.fe.documents.common.ETSDocFile;
import oem.edge.ets.fe.documents.common.Group;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.ismgt.model.EtsPrimaryContactInfo;
import oem.edge.ets.fe.pmo.ETSPMOffice;
import oem.edge.ets.fe.teamgroup.GroupConstants;

import org.apache.commons.logging.Log;
import org.apache.struts.upload.FormFile;

/**
 * This is the DATA ACCESS Class for the E&TS Documents Module
 * @author v2srikau
 */
public class DocumentDAO {

	/** Stores the Logging object */
	private Log m_pdLog = EtsLogger.getLogger(DocumentDAO.class);

	/** Stores the Connection Object */
	private Connection m_pdConnection = null;

	/**
	 * @throws Exception
	 */
	public void prepare() throws Exception {
		Connection pdConn = ETSDBUtils.getConnection();
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
	 * Read the RESOURCES TABLE and return populated HashMap
	 * @return HashMap containing URL to ACTION mapping
	 * @throws SQLException In case of database errors
	 */
	public Map getResources() throws SQLException {
		HashMap mapResources = new HashMap();

		String strQuery =
			"SELECT "
				+ "URL, ACTION "
				+ "FROM ETS.RESOURCES "
				+ "ORDER BY ACTION "
				+ "WITH UR";

		Statement stmtResURL = m_pdConnection.createStatement();
		ResultSet rsResources = stmtResURL.executeQuery(strQuery);

		while (rsResources.next()) {
			mapResources.put(
				rsResources.getString("URL"),
				rsResources.getString("ACTION"));
		}

		rsResources.close();
		stmtResURL.close();

		return mapResources;
	}

	/**
	 * Read the RESOURCE_ROLES TABLE and return populated HashMap
	 * @return HashMap containing ACTION as key and Vector of Roles as values
	 * @throws SQLException In case of database errors
	 */
	public Map getActionRoles() throws SQLException {
		HashMap mapActions = new HashMap();

		String strQuery =
			"SELECT "
				+ "ACTION, ROLE "
				+ "FROM ETS.RESOURCE_ROLES "
				+ "ORDER BY ACTION, ROLE "
				+ "WITH UR";

		Statement stmtActionRole = m_pdConnection.createStatement();
		ResultSet rsActionRole = stmtActionRole.executeQuery(strQuery);

		while (rsActionRole.next()) {
			String strAction = rsActionRole.getString("ACTION");

			Vector vtRoles = null;
			// Check if this action already exists in the Map
			if (mapActions.keySet().contains(strAction)) {
				vtRoles = (Vector) mapActions.get(strAction);
			} else {
				vtRoles = new Vector();
				mapActions.put(strAction, vtRoles);
			}
			vtRoles.add(rsActionRole.getString("ROLE"));
		}

		rsActionRole.close();
		stmtActionRole.close();

		return mapActions;
	}

	/**
	 * @param iParentID
	 * @param strSortBy
	 * @param a_d
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public Vector getSubCats(int iParentID, String strSortBy, String a_d)
		throws SQLException {

		String sb = "c.cat_name";

		if (strSortBy.equals(Defines.SORT_BY_DATE_STR)) {
			sb = "c.first_timestamp " + a_d + ",c.cat_name";
		} else if (strSortBy.equals(Defines.SORT_BY_TYPE_STR)) {
			sb = "c.cat_name";
		} else if (strSortBy.equals(Defines.SORT_BY_AUTH_STR)) {
			sb = "c.user_id " + a_d + ",c.cat_name";
		} else if (strSortBy.equals(Defines.SORT_BY_UPDATE_DATE_STR)) {
			sb = "c.last_timestamp " + a_d + ",c.cat_name";
		}

		Statement stmtSubCats = m_pdConnection.createStatement();

		String strQuery =
			"select c.* from ETS.ETS_CAT c where c.parent_id="
				+ iParentID
				+ " order by "
				+ sb
				+ " "
				+ a_d
				+ " with ur";

		if (m_pdLog.isDebugEnabled()) {
			m_pdLog.debug("******" + strQuery);
		}
		ResultSet rsSubCats = stmtSubCats.executeQuery(strQuery);

		Vector vtSubCats = getCats(rsSubCats);

		vtSubCats = populateUserNames(vtSubCats);

		rsSubCats.close();
		stmtSubCats.close();
		return vtSubCats;
	}

	/**
	 * @param rs
	 * @return
	 * @throws SQLException In case of database errors
	 */
	private Vector getCats(ResultSet rsSubCats) throws SQLException {
		Vector vtCats = new Vector();

		while (rsSubCats.next()) {
			ETSCat udCat = getCat(rsSubCats);
			vtCats.addElement(udCat);
		}
		return vtCats;
	}

	/**
	 * @param iCatID
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public Vector getBreadCrumbTrail(int iCatID) throws SQLException {
		ETSCat udCat = getCat(iCatID);
		Vector vtBreadCrumbs = new Vector();

		if (udCat != null) {
			vtBreadCrumbs.addElement(udCat);
			ETSCat udParentCat = udCat;
			while (true) {
				udParentCat = getCat(udParentCat.getParentId());
				if (udParentCat != null) {
					vtBreadCrumbs.addElement(udParentCat);
					if (udParentCat.getParentId() == 0) {
						break;
					}
				} else {
					break;
				}
			}
		}

		return vtBreadCrumbs;
	}

	/**
	 * @param strCatName
	 * @param strProjectID
	 * @return
	 * @throws SQLException
	 */
	public ETSCat getMeetingsCatByName(String strCatName, String strProjectID)
		throws SQLException {
		PreparedStatement stmtCat =
			m_pdConnection.prepareStatement(
				"select * from ETS.ETS_CAT "
					+ "where cat_name=? "
					+ "and project_id=? "
					+ "and DISPLAY_FLAG = 'N' with ur");
		stmtCat.setString(1, strCatName);
		stmtCat.setString(2, strProjectID);
		ResultSet rsCat = stmtCat.executeQuery();
		ETSCat udCat = null;

		while (rsCat.next()) {
			udCat = getCat(rsCat);
		}

		rsCat.close();
		stmtCat.close();
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
		DocReaderDAO udReaderDAO = new DocReaderDAO();
		udReaderDAO.setConnection(m_pdConnection);
		return udReaderDAO.getCatByName(strCatName,strProjectID);
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
		DocReaderDAO udReaderDAO = new DocReaderDAO();
		udReaderDAO.setConnection(m_pdConnection);
		return udReaderDAO.getCatByName(strCatName, iParentID, strProjectID);
	}

	/**
	 * @param iCatID
	 * @param strProjectID
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public ETSCat getCat(int iCatID, String strProjectID) throws SQLException {
		DocReaderDAO udReaderDAO = new DocReaderDAO();
		udReaderDAO.setConnection(m_pdConnection);
		return udReaderDAO.getCat(iCatID, strProjectID);
	}

	/**
	 * @param iCatID
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public ETSCat getCat(int iCatID) throws SQLException {
		DocReaderDAO udReaderDAO = new DocReaderDAO();
		udReaderDAO.setConnection(m_pdConnection);
		return udReaderDAO.getCat(iCatID);
	}

	/**
	 * @param rs
	 * @return
	 * @throws SQLException In case of database errors
	 */
	private ETSCat getCat(ResultSet rsSubCat) throws SQLException {
		DocReaderDAO udReaderDAO = new DocReaderDAO();
		return udReaderDAO.getCat(rsSubCat);
	}

	/**
	 * @param pdCon
	 * @param strProjectID
	 * @return
	 * @throws SQLException In case of database errors
	 * @throws Exception
	 */
	public ETSProj getProjectDetails(String strProjectID)
		throws SQLException, Exception {

		Statement stmtProjDetails = null;
		ResultSet rsProjDetails = null;
		StringBuffer strQuery = new StringBuffer(StringUtil.EMPTY_STRING);
		ETSProj udProjDetails = new ETSProj();

		strQuery.append(
			"SELECT PROJECT_ID,PROJECT_DESCRIPTION,PROJECT_NAME,"
				+ "PROJECT_START,"
				+ "PROJECT_END,DECAF_PROJECT_NAME,PROJECT_OR_PROPOSAL,"
				+ "LOTUS_PROJECT_ID,RELATED_ID,"
				+ "PARENT_ID,COMPANY,PMO_PROJECT_ID,SHOW_ISSUE_OWNER,"
				+ "PROJECT_STATUS,DELIVERY_TEAM,GEOGRAPHY,INDUSTRY,"
				+ "IS_ITAR, PROJECT_TYPE "
				+ "FROM ETS.ETS_PROJECTS "
				+ "WHERE PROJECT_ID = '"
				+ strProjectID
				+ "' with ur");

		if (m_pdLog.isDebugEnabled()) {
			m_pdLog.debug("QUERY : " + strQuery.toString());
		}

		stmtProjDetails = m_pdConnection.createStatement();
		rsProjDetails = stmtProjDetails.executeQuery(strQuery.toString());

		if (rsProjDetails.next()) {

			String sIsITAR =
				ETSUtils.checkNull(rsProjDetails.getString("IS_ITAR"));

			udProjDetails.setProjectId(
				ETSUtils.checkNull(rsProjDetails.getString("PROJECT_ID")));
			udProjDetails.setDescription(
				ETSUtils.checkNull(
					rsProjDetails.getString("PROJECT_DESCRIPTION")));
			udProjDetails.setName(
				ETSUtils.checkNull(rsProjDetails.getString("PROJECT_NAME")));
			udProjDetails.setStartDate(
				rsProjDetails.getTimestamp("PROJECT_START"));
			udProjDetails.setEndDate(rsProjDetails.getTimestamp("PROJECT_END"));
			udProjDetails.setDecafProject(
				ETSUtils.checkNull(
					rsProjDetails.getString("DECAF_PROJECT_NAME")));
			udProjDetails.setProjectOrProposal(
				ETSUtils.checkNull(
					rsProjDetails.getString("PROJECT_OR_PROPOSAL")));
			udProjDetails.setLotusProject(
				ETSUtils.checkNull(
					rsProjDetails.getString("LOTUS_PROJECT_ID")));
			udProjDetails.setRelatedProjectId(
				ETSUtils.checkNull(rsProjDetails.getString("RELATED_ID")));
			udProjDetails.setParent_id(
				ETSUtils.checkNull(rsProjDetails.getString("PARENT_ID")));
			udProjDetails.setCompany(
				ETSUtils.checkNull(rsProjDetails.getString("COMPANY")));
			udProjDetails.setPmo_project_id(
				ETSUtils.checkNull(rsProjDetails.getString("PMO_PROJECT_ID")));
			udProjDetails.setShow_issue_owner(
				ETSUtils.checkNull(
					rsProjDetails.getString("SHOW_ISSUE_OWNER")));
			udProjDetails.setProject_status(
				ETSUtils.checkNull(rsProjDetails.getString("PROJECT_STATUS")));

			udProjDetails.setDeliveryTeam(
				ETSUtils.checkNull(rsProjDetails.getString("DELIVERY_TEAM")));
			udProjDetails.setGeography(
				ETSUtils.checkNull(rsProjDetails.getString("GEOGRAPHY")));
			udProjDetails.setIndustry(
				ETSUtils.checkNull(rsProjDetails.getString("INDUSTRY")));

			if (sIsITAR.equalsIgnoreCase(DocConstants.IND_YES)) {
				udProjDetails.setITAR(true);
			} else {
				udProjDetails.setITAR(false);
			}

			udProjDetails.setProjectType(
				rsProjDetails.getString("PROJECT_TYPE"));

		}

		udProjDetails.setProjBladeType(
				ETSUtils.isEtsProjBladeProject(strProjectID, m_pdConnection));

		rsProjDetails.close();
		stmtProjDetails.close();
		return udProjDetails;
	}

	/**
	 * @param iParentId
	 * @param strSortBy
	 * @param strSortOrder
	 * @param bIsAdmin
	 * @param strUserId
	 * @return
	 * @throws SQLException In case of database errors
	 * @throws Exception
	 */
	public Vector getDocs(
		int iParentId,
		String strSortBy,
		String strSortOrder,
		boolean bIsAdmin,
		String strUserId)
		throws SQLException, Exception {
		String strBuffer = "d.doc_name";

		if (strSortBy.equals(Defines.SORT_BY_DATE_STR)) {
			strBuffer = "d.doc_upload_date " + strSortOrder + ",d.doc_name";
		} else if (strSortBy.equals(Defines.SORT_BY_TYPE_STR)) {
			strBuffer = "d.doc_name";
		} else if (strSortBy.equals(Defines.SORT_BY_AUTH_STR)) {
			strBuffer = "d.user_id " + strSortOrder + ",d.doc_name";
		} else if (strSortBy.equals(Defines.SORT_BY_UPDATE_DATE_STR)) {
			strBuffer = "d.doc_update_date " + strSortOrder + ",d.doc_name";
		}

		String strQuery =
			"select d.*,"
				+ "(select count(m.doc_id) from ets.ets_doc_metrics m "
				+ "where d.doc_id=m.doc_id) as hits "
				+ "from ETS.ETS_DOC d "
				+ "where d.cat_id="
				+ iParentId
				+ " and itar_upload_status !='P'"
				+ " and delete_flag !='"
				+ DocConstants.TRUE_FLAG
				+ "' order by "
				+ strBuffer
				+ " "
				+ strSortOrder
				+ " with ur";

		if (!bIsAdmin) {
			strQuery =
				"select d.*,"
					+ " (select count(m.doc_id) from ets.ets_doc_metrics m "
					+ "where d.doc_id=m.doc_id) as hits "
					+ " from ETS.ETS_DOC d "
					+ " where d.cat_id="
					+ iParentId
					+ " and itar_upload_status !='P'"
					+ " and delete_flag !='"
					+ DocConstants.TRUE_FLAG
					+ "'"
					+ " and (d.isprivate!='1' or d.isprivate is null"
					// add check for all users group
					+ " or '" + Defines.GRP_ALL_USERS + "' in"
					+ " (select name from ets.ets_private_doc pd, ets.groups grp where pd.doc_id = d.doc_id and grp.group_id = pd.group_id)"
					+ " or d.user_id='"
					+ strUserId
					+ "' or '"
					+ strUserId
					+ "' in ((select u.user_id from ets.ets_private_doc u "
					+ "where u.doc_id=d.doc_id)"
					+ "union"
					+ "(select u.user_id from ets.user_groups u where group_id in "
					+ "(select group_id from ets.ets_private_doc pd where pd.doc_id = d.doc_id)) )) "
					+ " order by "
					+ strBuffer
					+ " "
					+ strSortOrder
					+ " with ur";
		}

		if (m_pdLog.isDebugEnabled()) {
			m_pdLog.debug(strQuery);
		}
		Statement stmtGetDocs = m_pdConnection.createStatement();
		ResultSet rsGetDocs = stmtGetDocs.executeQuery(strQuery);

		Vector docs = new Vector();
		docs = populateUserNames(getLatestDocs(rsGetDocs));

		rsGetDocs.close();
		stmtGetDocs.close();
		return docs;
	}

	/**
	 * @param strProjectId
	 * @param strSortBy
	 * @param strSortOrder
	 * @param bIsAdmin
	 * @param strUserId
	 * @return
	 * @throws SQLException In case of database errors
	 * @throws Exception
	 */
	public Vector getAllDocs(
		String strProjectId,
		String strSortBy,
		String strSortOrder,
		boolean bIsAdmin,
		String strUserId)
		throws SQLException, Exception {
		String strBuffer = "d.doc_name";

		if (strSortBy.equals(Defines.SORT_BY_CUSTOM_STR)) {
		    strBuffer = "d.doc_upload_date desc, d.user_id asc, upper(d.doc_name)";
		} else if (strSortBy.equals(Defines.SORT_BY_DATE_STR)) {
			strBuffer = "d.doc_upload_date " + strSortOrder + ",d.doc_name";
		} else if (strSortBy.equals(Defines.SORT_BY_TYPE_STR)) {
			strBuffer = "d.doc_name";
		} else if (strSortBy.equals(Defines.SORT_BY_AUTH_STR)) {
			strBuffer = "d.user_id " + strSortOrder + ",d.doc_name";
		} else if (strSortBy.equals(Defines.SORT_BY_UPDATE_DATE_STR)) {
			strBuffer = "d.doc_update_date " + strSortOrder + ",d.doc_name";
		}

		String strQuery =
			"select d.*,"
				+ "(select count(m.doc_id) from ets.ets_doc_metrics m "
				+ "where d.doc_id=m.doc_id) as hits "
				+ "from ETS.ETS_DOC d "
				+ "where d.project_id='"
				+ strProjectId
				+ "' and d.issue_id is null and itar_upload_status !='P'"
				+ " and cat_id > 0 and delete_flag !='"
				+ DocConstants.TRUE_FLAG
				+ "' order by "
				+ strBuffer
				+ " "
				+ strSortOrder
				+ " with ur";

		if (!bIsAdmin) {
			strQuery =
				"select d.*,"
					+ " (select count(m.doc_id) from ets.ets_doc_metrics m "
					+ "where d.doc_id=m.doc_id) as hits "
					+ " from ETS.ETS_DOC d "
					+ " where d.project_id='"
					+ strProjectId
					+ "' and d.issue_id is null and itar_upload_status !='P'"
					+ " and cat_id > 0 and delete_flag !='"
					+ DocConstants.TRUE_FLAG
					+ "'"
					+ " and (d.isprivate!='1' or d.isprivate is null"
					// add check for all users group
					+ " or '" + Defines.GRP_ALL_USERS + "' in"
					+ " (select name from ets.ets_private_doc pd, ets.groups grp where pd.doc_id = d.doc_id and grp.group_id = pd.group_id)"
					+ " or d.user_id='"
					+ strUserId
					+ "' or '"
					+ strUserId
					+ "' in ((select u.user_id from ets.ets_private_doc u "
					+ "where u.doc_id=d.doc_id)"
					+ "union"
					+ "(select u.user_id from ets.user_groups u where group_id in "
					+ "(select group_id from ets.ets_private_doc pd where pd.doc_id = d.doc_id)) )) "
					+ " order by "
					+ strBuffer
					+ " "
					+ strSortOrder
					+ " with ur";
		}

		if (m_pdLog.isDebugEnabled()) {
			m_pdLog.debug(strQuery);
		}
		Statement stmtGetDocs = m_pdConnection.createStatement();
		ResultSet rsGetDocs = stmtGetDocs.executeQuery(strQuery);

		Vector docs = new Vector();
		docs = populateUserNames(getLatestDocs(rsGetDocs));

		rsGetDocs.close();
		stmtGetDocs.close();
		return docs;
	}

	/**
	 * @param rsDocs
	 * @return
	 * @throws SQLException In case of database errors
	 */
	private Vector getLatestDocs(ResultSet rsDocs) throws SQLException {
		Vector vtDocs = new Vector();

		while (rsDocs.next()) {
			ETSDoc doc = getDoc(rsDocs);
			if (doc.isLatestVersion()) {
				vtDocs.addElement(doc);
			}
		}

		return vtDocs;
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
	 * @param strUserId
	 * @param strProjId
	 * @param iPrivilige
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public boolean hasProjectPriv(
		String strUserId,
		String strProjId,
		int iPrivilige)
		throws SQLException {
		int iPrivValue = 0;
		Statement stmtPrivilige = m_pdConnection.createStatement();

		String strQuery = "select PRIV_VALUE from ETS.ETS_ROLES "
								+ "where PRIV_ID = "
								+ iPrivilige
								+ " and ROLE_ID = ("
								+ "select USER_ROLE_ID from ETS.ETS_USERS where USER_ID = '"
								+ strUserId
								+ "' and ACTIVE_FLAG='A' and USER_PROJECT_ID = '"
								+ strProjId
								+ "') with ur";
		System.out.println( "SQL : hasProjectPriv :: "+ strQuery );
		ResultSet rsPrivilige =
			stmtPrivilige.executeQuery(strQuery);

		if (rsPrivilige.next()) {
			iPrivValue = rsPrivilige.getInt("PRIV_VALUE");
		}

		rsPrivilige.close();
		stmtPrivilige.close();

		return (iPrivValue == 1);
	}

	/**
	 * This is a synchronized method as it need to get the unique CAT_ID
	 * @return
	 * @throws SQLException In case of database errors
	 */
	private synchronized int getCatId() throws SQLException {
		int iCatID = 0;
		Statement stmtCatID = m_pdConnection.createStatement();

		ResultSet rsCatID =
			stmtCatID.executeQuery(
				"select max(cat_id) as cat_id from ets.ets_cat with ur");

		if (rsCatID.next()) {
			iCatID = rsCatID.getInt("cat_id") + 1;
		}

		rsCatID.close();
		stmtCatID.close();
		return iCatID;
	}

	/**
	 * Add a new category to the database
	 * @param udCat
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public String[] addCat(ETSCat udCat) throws SQLException {
		int iCatID = getCatId();

		PreparedStatement stmtAddCat =
			m_pdConnection.prepareStatement(
				"insert into ets.ets_cat("
					+ "cat_id,project_id,user_id,cat_name,parent_id,"
					+ "cat_type,cat_description,order,view_type,proj_desc,"
					+ "privs,ibm_only,isPrivate,last_timestamp, display_flag, first_timestamp ) "
					+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,current timestamp,?, current timestamp)");

		try {
			//spn 0312 projid
			stmtAddCat.setInt(1, iCatID);
			stmtAddCat.setString(2, udCat.getProjectId());
			stmtAddCat.setString(3, udCat.getUserId());
			stmtAddCat.setString(4, udCat.getName());
			stmtAddCat.setInt(5, udCat.getParentId());
			stmtAddCat.setInt(6, 2);
			stmtAddCat.setString(7, udCat.getDescription());
			stmtAddCat.setInt(8, udCat.getOrder());
			stmtAddCat.setInt(9, udCat.getViewType());
			stmtAddCat.setInt(10, udCat.getProjDesc());
			stmtAddCat.setString(11, udCat.getPrivsString());
			stmtAddCat.setString(12, String.valueOf(udCat.getIbmOnly()));
			stmtAddCat.setString(13, udCat.getCPrivate());
			if (StringUtil.isNullorEmpty(udCat.getDisplayFlag())) {
				stmtAddCat.setNull(14, Types.VARCHAR);
			} else {
				stmtAddCat.setString(14, udCat.getDisplayFlag());
			}
			stmtAddCat.executeUpdate();

			stmtAddCat.close();
			String strCatID = String.valueOf(iCatID);
			addContentLog(
				iCatID,
				'C',
				udCat.getProjectId(),
				Defines.ADD_CAT,
				udCat.getUserId());
			return new String[] { "0", strCatID };
		} catch (SQLException se) {
			m_pdLog.error("sql error =" + se);
			throw se;
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
	private synchronized boolean addContentLog(
		int iNodeID,
		char cNodeType,
		String strProjectID,
		String strAction,
		String strUser)
		throws SQLException {

	    DocUpdateDAO udUpdate = new DocUpdateDAO();
	    udUpdate.setConnection(m_pdConnection);
	    return udUpdate.addContentLog(iNodeID, cNodeType, strProjectID, strAction, strUser);

	}

	/**
	 * @param udCat
	 * @param strUserID
	 * @param strProjectID
	 * @param strRole
	 * @param iPrivilige
	 * @param bTraverse
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public Vector getValidCatTree(
		ETSCat udCat,
		String strUserID,
		String strProjectID,
		String strRole,
		int iPrivilige,
		boolean bTraverse)
		throws SQLException {
		Vector vtCats = new Vector();
		try {
			Vector vtSubCats = getSubCats(udCat.getId());

			if (vtSubCats.size() > 0) {
				for (int iCounter = 0;
					iCounter < vtSubCats.size();
					iCounter++) {
					ETSCat udEachCat = (ETSCat) vtSubCats.elementAt(iCounter);
					boolean bResult =
						getValidCatSubTree(
							udEachCat.getId(),
							strProjectID,
							strUserID,
							strRole,
							iPrivilige,
							bTraverse);
					if (bResult) {
						vtCats.addElement(udEachCat);
					}
				}
			}
		} catch (SQLException se) {
			m_pdLog.error("sql error= " + se);
			throw se;
		}

		return vtCats;
	}

	/**
	 * @param udCat
	 * @param strUserID
	 * @param strProjectID
	 * @param strRole
	 * @param iPrivilige
	 * @param bTraverse
	 * @return
	 */
	public Vector getValidCatTreeIds(
		ETSCat udCat,
		String strUserID,
		String strProjectID,
		String strRole,
		int iPrivilige,
		boolean bTraverse)
		throws SQLException {
		Vector vtCats = new Vector();
		Vector vtSubCats =
			getValidCatTree(
				udCat,
				strUserID,
				strProjectID,
				strRole,
				iPrivilige,
				bTraverse);

		if (vtSubCats != null && vtSubCats.size() > 0) {
			for (int iCounter = 0; iCounter < vtSubCats.size(); iCounter++) {
				ETSCat udTmpCat = (ETSCat) vtSubCats.get(iCounter);
				vtCats.add(new Integer(udTmpCat.getId()));
			}
		}

		return vtCats;
	}

	/**
	 * @param iCatID
	 * @param strProjectID
	 * @param strUserID
	 * @param strUserRole
	 * @param iPrivilige
	 * @param bTraverse
	 * @return
	 */
	public boolean getValidCatSubTree(
		int iCatID,
		String strProjectID,
		String strUserID,
		String strUserRole,
		int iPrivilige,
		boolean bTraverse)
		throws SQLException {

		boolean bEditable = true;

		try {

			ETSCat udCat = getCat(iCatID);

			if (udCat.getCatType() == 0
				|| ((!udCat.getUserId().equals(strUserID))
					&& (!hasProjectPriv(strUserID, strProjectID, iPrivilige))
					&& (!strUserRole.equals(Defines.ETS_ADMIN)))) {
				bEditable = false;
				return bEditable;
			}

			// If Role is Workspace Manager. User is allowed to delete.
			if (Defines.WORKSPACE_MANAGER.equals(strUserRole)) {
				bEditable = true;
				return bEditable;
			}

			if (bTraverse) {
				Vector vtSubCats = getSubCats(iCatID);
				for (int i = 0; i < vtSubCats.size(); i++) {
					ETSCat c = (ETSCat) vtSubCats.elementAt(i);
					bEditable =
						getValidCatSubTree(
							c.getId(),
							strProjectID,
							strUserID,
							strUserRole,
							iPrivilige,
							bTraverse);
				}

				Vector vtDocs = getDocs(iCatID, StringUtil.SPACE, true);
				boolean bHasPriv =
					hasProjectPriv(strUserID, strProjectID, iPrivilige);

				for (int j = 0; j < vtDocs.size(); j++) {
					ETSDoc udDoc = (ETSDoc) vtDocs.elementAt(j);
					if (((!udDoc.getUserId().equals(strUserID))
						&& !bHasPriv
						&& !strUserRole.equals(Defines.ETS_ADMIN))
						|| (udDoc.hasExpired()
							&& strUserRole == Defines.WORKSPACE_MANAGER)) {
						bEditable = false;
						return bEditable;
					}
					if (udDoc.IsDPrivate()) {
						if (!isAuthorized(udDoc.getUserId(),
							udDoc.getId(),
							udDoc.getProjectId(),
							strUserRole,
							strUserRole.equals(Defines.ETS_ADMIN),
							strUserRole.equals(Defines.ETS_EXECUTIVE),
							false,
							false,
							strUserID)) {
							bEditable = false;
							return bEditable;
						}
					}
				}
			}
		} catch (SQLException se) {
			m_pdLog.error("sql error= " + se);
			bEditable = false;
			throw se;
		}

		return bEditable;
	}

	/**
	 * @param iParentID
	 * @param strUserID
	 * @param bIsAdmin
	 * @return
	 * @throws SQLException In case of database errors
	 * @throws Exception
	 */
	private Vector getDocs(int iParentID, String strUserID, boolean bIsAdmin)
		throws SQLException {
		PreparedStatement stmtDocs =
			m_pdConnection.prepareStatement(
				"select d.*,"
					+ "(select count(m.doc_id) from ets.ets_doc_metrics m "
					+ "where d.doc_id=m.doc_id) as hits  "
					+ "from ETS.ETS_DOC d "
					+ "where "
					+ "d.cat_id = ? and "
					+ " itar_upload_status !='P'"
					+ " and delete_flag !='"
					+ DocConstants.TRUE_FLAG
					+ "' order by d.doc_name with ur");

		stmtDocs.setInt(1, iParentID);
		//getDocsSt.setString(2,TRUE_FLAG);
		ResultSet rsDocs = stmtDocs.executeQuery();

		Vector docs = null;
		docs = getLatestDocs(rsDocs);

		rsDocs.close();
		stmtDocs.close();
		return docs;
	}

	/**
	 * @param iParentID
	 * @return
	 * @throws SQLException In case of database errors
	 */
	private Vector getSubCats(int iParentID) throws SQLException {

		Statement stmtSubCats = m_pdConnection.createStatement();
		String strQuery =
			"select * from ETS.ETS_CAT where parent_id="
				+ iParentID
				+ " order by order,cat_name with ur";

		ResultSet rsSubCats = stmtSubCats.executeQuery(strQuery);

		Vector vtSubCats = null;
		vtSubCats = getCats(rsSubCats);

		rsSubCats.close();
		stmtSubCats.close();
		return vtSubCats;
	}

	/**
	 * @param strOwnerID
	 * @param iID
	 * @param strProjectID
	 * @param strUserRole
	 * @param isSuperAdmin
	 * @param isExecutive
	 * @param isViewOnly
	 * @param bIsCat
	 * @param strUserID
	 * @return
	 */
	public boolean isAuthorized(
		String strOwnerID,
		int iID,
		String strProjectID,
		String strUserRole,
		boolean isSuperAdmin,
		boolean isExecutive,
		boolean isViewOnly,
		boolean bIsCat,
		String strUserID) {

		if (isViewOnly
			&& (strUserRole.equals(Defines.WORKSPACE_OWNER)
				|| isSuperAdmin
				|| isExecutive
				|| strOwnerID.equals(strUserID)))
			return true;
		else if (
			(!isViewOnly)
				&& (strUserRole.equals(Defines.WORKSPACE_OWNER)
					|| isSuperAdmin
					|| strOwnerID.equals(strUserID)))
			return true;
		else {
			try {
				Vector vtUsers =
					getRestrictedProjMemberIds(strProjectID, iID, bIsCat);
				for (int iCounter = 0; iCounter < vtUsers.size(); iCounter++) {
					if (strUserID.equals(vtUsers.elementAt(iCounter))) {
						if (isViewOnly
							|| (strUserRole.equals(Defines.WORKSPACE_MANAGER)))
							return true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	/**
	 * @param strProjectID
	 * @param iID
	 * @param bIsCat
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public Vector getRestrictedProjMemberIds(
		String strProjectID,
		int iID,
		boolean bIsCat)
		throws SQLException {

		String strTable = "cat";
		if (!bIsCat) {
			strTable = "doc";
		}

		PreparedStatement stmtProjMembers =
			m_pdConnection.prepareStatement(
				"select u.user_id from ETS.ETS_USERS u, ets.ets_private_"
					+ strTable
					+ " t where u.user_project_id = ?"
					+ " and u.active_flag='A' and u.user_id=t.user_id and "
					+ "u.user_project_id=t.project_id and t."
					+ strTable
					+ "_id=? AND t.ACCESS_TYPE=? order by u.user_id with ur");

		stmtProjMembers.setString(1, strProjectID);
		stmtProjMembers.setInt(2, iID);
		stmtProjMembers.setString(3, Defines.DOC_READ_ACCESS);
		ResultSet rsProjMembers = stmtProjMembers.executeQuery();

		Vector vtMembers = new Vector();
		while (rsProjMembers.next()) {
			vtMembers.addElement(rsProjMembers.getString("user_id"));
		}
		rsProjMembers.close();
		stmtProjMembers.close();
		return vtMembers;
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
	    DocReaderDAO udReader = new DocReaderDAO();
	    udReader.setConnection(m_pdConnection);
		return udReader.getRestrictedProjMembers(strProjectId, iID, bCheckAmtTables, bIsCat);
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
	    DocReaderDAO udReader = new DocReaderDAO();
	    udReader.setConnection(m_pdConnection);
		return udReader.getRestrictedProjMembers(strProjectId, iID, bIsCat);
	}

	/**
	 * @param udCat
	 * @param strUserID
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public synchronized boolean delCat(ETSCat udCat, String strUserID)
		throws SQLException {
		PreparedStatement stmtDelCat =
			m_pdConnection.prepareStatement(
				"delete from ets.ets_cat where cat_id=?");

		try {
			int iCatID = udCat.getId();
			String strProjectID = udCat.getProjectId();

			// delete child categories
			Vector vtCategories = getSubCats(udCat.getId());
			for (int iCounter = 0;
				iCounter < vtCategories.size();
				iCounter++) {
				ETSCat udSubCat = (ETSCat) vtCategories.elementAt(iCounter);
				delCat(udSubCat, strUserID);
			}

			// delete child documents
			Vector vtDocs = getDocs(udCat.getId(), strUserID, true);
			// //5.2.1  todo
			for (int iCounter = 0; iCounter < vtDocs.size(); iCounter++) {
				ETSDoc doc = (ETSDoc) vtDocs.elementAt(iCounter);
				delDoc(doc, strUserID, true);
			}

			stmtDelCat.setInt(1, udCat.getId());
			stmtDelCat.executeUpdate();

			stmtDelCat.close();
			addContentLog(
				iCatID,
				'C',
				strProjectID,
				Defines.DEL_CAT,
				strUserID);
			return true;
		} catch (SQLException se) {
			m_pdLog.error("sql error =" + se);
			throw se;
		}

	}

	/**
	 * @param udDoc
	 * @param strUserId
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public synchronized boolean delDoc(ETSDoc udDoc, String strUserId)
		throws SQLException {
		try {
			int iDocID = udDoc.getId();
			String strProjectId = udDoc.getProjectId();

			Statement stmtDelDoc = m_pdConnection.createStatement();
			//String update = "delete from ETS.ETS_DOC where DOC_ID = " + docid;
			String strUpdateSQL =
				"update ETS.ETS_DOC set latest_version='"
					+ DocConstants.FALSE_FLAG
					+ "', delete_flag='"
					+ DocConstants.TRUE_FLAG
					+ "',deleted_by='"
					+ strUserId
					+ "',deletion_date=current timestamp where DOC_ID = "
					+ iDocID;

			int iRowCount = stmtDelDoc.executeUpdate(strUpdateSQL);

			if (iRowCount != 1) {
				m_pdLog.error(
					"executeUpdate("
						+ strUpdateSQL
						+ ") returned "
						+ iRowCount);
				return false;
			}

			addContentLog(
				iDocID,
				'D',
				strProjectId,
				Defines.DEL_DOC,
				strUserId);

			int iLowestPossibleID =
				(iDocID / DocConstants.MAX_DOC_VERSIONS)
					* DocConstants.MAX_DOC_VERSIONS;
			int iHighestPossibleID =
				iLowestPossibleID + DocConstants.MAX_DOC_VERSIONS;

			String strQuery =
				"select MIN(DOC_ID) as MIN_ID, MAX(DOC_ID) as MAX_ID from "
					+ "ETS.ETS_DOC where DOC_ID >= "
					+ iLowestPossibleID
					+ " and DOC_ID < "
					+ iHighestPossibleID
					+ " and delete_flag!='"
					+ DocConstants.TRUE_FLAG
					+ "' with ur";

			ResultSet rsDocID = stmtDelDoc.executeQuery(strQuery);

			int iMinId = -1;
			int iMaxId = -1;

			if (rsDocID.next()) {
				iMinId = rsDocID.getInt("MIN_ID");
				iMaxId = rsDocID.getInt("MAX_ID");
			}

			rsDocID.close();
			stmtDelDoc.close();

			if (iMinId == -1) {
				// no other versions, so no doc flags to update
				return true;
			}

			if (iMinId > iDocID) {
				if (!updateHasPreviousVersionFlag(iMinId,
					DocConstants.FALSE_FLAG)) {
					m_pdLog.error(
						"error in updateHasPreviousVersionFlag: " + iMinId);
					return false;
				}
			}

			if (iMaxId < iDocID && iMaxId >= DocConstants.STARTING_DOC_ID) {
			    DocUpdateDAO udUpdateDAO = new DocUpdateDAO();
			    udUpdateDAO.setConnection(m_pdConnection);
				if (!udUpdateDAO.updateIsLatestVersionFlag(iMaxId,
					DocConstants.TRUE_FLAG)) {
					m_pdLog.error(
						"error in updateIsLatestVersionFlag: " + iMaxId);
					return false;
				}
			}

			return true;
		} catch (SQLException e) {
			m_pdLog.error("sql error =" + DocUpdateDAO.getStackTrace(e));
			throw e;
		}
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
	    DocUpdateDAO udUpdate = new DocUpdateDAO();
	    udUpdate.setConnection(m_pdConnection);
	    return udUpdate.delDoc(udDoc, strUserID, bDelAll);
	}

	/**
	 * @param iDocID
	 * @param cHasPrevVersionFlag
	 * @return
	 * @throws SQLException In case of database errors
	 */
	synchronized boolean updateHasPreviousVersionFlag(
		int iDocID,
		char cHasPrevVersionFlag)
		throws SQLException {
		Statement stmtUpdateDoc = m_pdConnection.createStatement();
		try {
			String update =
				"update ETS.ETS_DOC set HAS_PREV_VERSION = '"
					+ cHasPrevVersionFlag
					+ "' where DOC_ID = "
					+ iDocID;
			int rowsUpdated = stmtUpdateDoc.executeUpdate(update);

			if (rowsUpdated == 1)
				return true;
			else {
				m_pdLog.error(
					"executeUpdate(" + update + ") returned: " + rowsUpdated);
				return false;
			}
		} finally {
			stmtUpdateDoc.close();
		}
	}

	/**
	 * @param vtSubTree
	 * @param iCatID
	 * @param strProjectID
	 * @param strUserID
	 * @param strUserRole
	 * @param iPrivilige
	 * @param bTraverse
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public Vector getCatSubTreeOwners(
		Vector vtSubTree,
		int iCatID,
		String strProjectID,
		String strUserID,
		String strUserRole,
		int iPrivilige,
		boolean bTraverse)
		throws Exception {
		ETSCat udCat = getCat(iCatID);
		if (udCat.getCatType() == 0
			|| ((!udCat.getUserId().equals(strUserID))
				&& (!hasProjectPriv(strUserID, strProjectID, iPrivilige))
				&& (!strUserRole.equals(Defines.ETS_ADMIN)))) {
			if (!vtSubTree.contains(udCat.getUserId()))
				vtSubTree.addElement(udCat.getUserId());
		}
		if (bTraverse) {
			Vector vtSubCats = getSubCats(iCatID);
			for (int iCounter = 0; iCounter < vtSubCats.size(); iCounter++) {
				ETSCat udSubCat = (ETSCat) vtSubCats.elementAt(iCounter);
				vtSubTree =
					getCatSubTreeOwners(
						vtSubTree,
						udSubCat.getId(),
						strProjectID,
						strUserID,
						strUserRole,
						iPrivilige,
						bTraverse);
			}

			Vector docs = getDocs(iCatID, StringUtil.SPACE, true);
			boolean hasPriv =
				hasProjectPriv(strUserID, strProjectID, iPrivilige);

			for (int iCounter = 0; iCounter < docs.size(); iCounter++) {
				ETSDoc udDoc = (ETSDoc) docs.elementAt(iCounter);
				if (((!udDoc.getUserId().equals(strUserID))
					&& !hasPriv
					&& !strUserRole.equals(Defines.ETS_ADMIN))
					|| (udDoc.hasExpired()
						&& strUserRole == Defines.WORKSPACE_MANAGER)) {
					if (!vtSubTree.contains(udDoc.getUserId()))
						vtSubTree.addElement(udDoc.getUserId());
				}
			}
		}

		return vtSubTree;
	}

	/**
	 * @param strUserID
	 * @param strProjectID
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public Vector getProjects(String strUserID, String strProjectID)
		throws SQLException {
		PreparedStatement stmtProjects =
			m_pdConnection.prepareStatement(
				"select p.* from ETS.ETS_PROJECTS p, ETS.ETS_USERS u "
					+ "where u.user_id = ? and p.project_id = ? "
					+ "and u.active_flag='A' "
					+ "and u.user_project_id = p.project_id with ur");

		stmtProjects.setString(1, strUserID);
		stmtProjects.setString(2, strProjectID);
		ResultSet rsProjects = stmtProjects.executeQuery();

		Vector projects = null;
		projects = getProjs(rsProjects);

		rsProjects.close();
		stmtProjects.close();
		return projects;
	}

	/**
	 * @param strProjectID
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public Vector getProject(String strProjectID) throws SQLException {
		PreparedStatement stmtProjects =
			m_pdConnection.prepareStatement(
				"select p.* from ETS.ETS_PROJECTS p "
					+ "where p.project_id = ? with ur");

		stmtProjects.setString(1, strProjectID);
		ResultSet rsProjects = stmtProjects.executeQuery();

		Vector vtProjects = null;
		vtProjects = getProjs(rsProjects);

		rsProjects.close();
		stmtProjects.close();
		return vtProjects;
	}

	/**
	 * @param rsProjects
	 * @return
	 * @throws SQLException In case of database errors
	 */
	private Vector getProjs(ResultSet rsProjects) throws SQLException {
		Vector vtProjects = new Vector();

		while (rsProjects.next()) {
			ETSProj proj = getProj(rsProjects);
			vtProjects.addElement(proj);
		}
		return vtProjects;
	}

	/**
	 * @param rsProject
	 * @return
	 * @throws SQLException In case of database errors
	 */
	private ETSProj getProj(ResultSet rsProject) throws SQLException {
		ETSProj udProject = new ETSProj();

		String sProjId = ETSUtils.checkNull(rsProject.getString("PROJECT_ID"));
		String sProjDesc =
			ETSUtils.checkNull(rsProject.getString("PROJECT_DESCRIPTION"));
		String sProjName =
			ETSUtils.checkNull(rsProject.getString("PROJECT_NAME"));
		Timestamp tProjStart = rsProject.getTimestamp("PROJECT_START");
		Timestamp tProjEnd = rsProject.getTimestamp("PROJECT_END");
		String sDecafProjName =
			ETSUtils.checkNull(rsProject.getString("DECAF_PROJECT_NAME"));
		String sProjOrProposal =
			ETSUtils.checkNull(rsProject.getString("PROJECT_OR_PROPOSAL"));
		String sLotusProjID =
			ETSUtils.checkNull(rsProject.getString("LOTUS_PROJECT_ID"));
		String sRelatedId =
			ETSUtils.checkNull(rsProject.getString("RELATED_ID"));
		String sParentId = ETSUtils.checkNull(rsProject.getString("PARENT_ID"));
		String sCompany = ETSUtils.checkNull(rsProject.getString("COMPANY"));
		String sPmoProjectId =
			ETSUtils.checkNull(rsProject.getString("PMO_PROJECT_ID"));
		String sShowIssueOwner =
			ETSUtils.checkNull(rsProject.getString("SHOW_ISSUE_OWNER"));
		String sProjectStatus =
			ETSUtils.checkNull(rsProject.getString("PROJECT_STATUS"));

		udProject.setProjectId(sProjId);
		udProject.setDescription(sProjDesc);
		udProject.setName(sProjName);
		udProject.setStartDate(tProjStart);
		udProject.setEndDate(tProjEnd);
		udProject.setDecafProject(sDecafProjName);
		udProject.setProjectOrProposal(sProjOrProposal);
		udProject.setLotusProject(sLotusProjID);
		udProject.setRelatedProjectId(sRelatedId);
		udProject.setParent_id(sParentId);
		udProject.setCompany(sCompany);
		udProject.setPmo_project_id(sPmoProjectId);
		udProject.setShow_issue_owner(sShowIssueOwner);
		udProject.setProject_status(sProjectStatus);

		return udProject;
	}

	/**
	 * @param udCat
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public synchronized boolean updateCat(ETSCat udCat) throws SQLException {
		PreparedStatement stmtUpdateCat =
			m_pdConnection.prepareStatement(
				"update ets.ets_cat "
					+ "set project_id=?,user_id=?,cat_name=?,parent_id=?,"
					+ "cat_type=?,cat_description=?,order=?,view_type=?,"
					+ "proj_desc=?,privs=?,ibm_only=?," 
					+ "last_timestamp=current timestamp, "
					+ "isPrivate=? "
					+ "where cat_id=?");

		try {
			//spn 0312 projid
			stmtUpdateCat.setString(1, udCat.getProjectId());
			stmtUpdateCat.setString(2, udCat.getUserId());
			stmtUpdateCat.setString(3, udCat.getName());
			stmtUpdateCat.setInt(4, udCat.getParentId());
			stmtUpdateCat.setInt(5, udCat.getCatType());
			stmtUpdateCat.setString(6, udCat.getDescription());
			stmtUpdateCat.setInt(7, udCat.getOrder());
			stmtUpdateCat.setInt(8, udCat.getViewType());
			stmtUpdateCat.setInt(9, udCat.getProjDesc());
			//upCatSt.setInt(10, cat.getProjMsg());
			stmtUpdateCat.setString(10, udCat.getPrivsString());
			stmtUpdateCat.setString(11, String.valueOf(udCat.getIbmOnly()));
			stmtUpdateCat.setString(12, udCat.getCPrivate());
			stmtUpdateCat.setInt(13, udCat.getId());

			stmtUpdateCat.executeUpdate();
			stmtUpdateCat.close();
			addContentLog(
				udCat.getId(),
				'C',
				udCat.getProjectId(),
				Defines.UPDATE_CAT,
				udCat.getUserId());
			return true;
		} catch (SQLException se) {
			m_pdLog.error("sql error =" + se);
			throw se;
		}

	}

	/**
	 * @param iParentID
	 * @param strDocIDs
	 * @param bIsAdmin
	 * @param strUserID
	 * @return
	 * @throws SQLException In case of database errors
	 * @throws Exception
	 */
	private synchronized String getChildDocIds(
		int iParentID,
		String strDocIDs,
		boolean bIsAdmin,
		String strUserID)
		throws SQLException {
		String strDocIdsTmp = strDocIDs;

		Statement stmtDocs = m_pdConnection.createStatement();

		String strQuery =
			"select d.doc_id from ETS.ETS_DOC d where d.cat_id="
				+ iParentID
				+ " and d.ibm_only!='"
				+ String.valueOf(Defines.ETS_IBM_CONF)
				+ "' with ur";

		if (!bIsAdmin) {
			//what about versions?
			strQuery =
				"SELECT d.doc_id FROM   ets.ets_doc d WHERE d.cat_id="
					+ iParentID
					+ " and d.ibm_only!='"
					+ String.valueOf(Defines.ETS_IBM_CONF)
					+ "'"
					+ " and d.user_id='"
					+ strUserID
					+ "'"
					+ " and d.latest_version='"
					+ Defines.TRUE_FLAG
					+ "'"
					+ " OR ("
					+ " and d.cat_id="
					+ iParentID
					+ " and d.ibm_only!='"
					+ String.valueOf(Defines.ETS_IBM_CONF)
					+ "'"
					+ " and doc_id IN"
					+ " (select doc_id FROM ets.ets_doc s"
					+ " WHERE EXISTS (SELECT doc_id"
					+ " FROM   ets.ets_doc ss"
					+ " WHERE ss.cat_id="
					+ iParentID
					+ " AND   ss.ibm_only!='"
					+ String.valueOf(Defines.ETS_IBM_CONF)
					+ "'"
					+ " AND   ss.user_id='"
					+ strUserID
					+ "'"
					+ " AND    ss.latest_version='"
					+ Defines.TRUE_FLAG
					+ "'"
					+ " AND    ss.doc_id"
					+ " between (s.doc_id/"
					+ DocConstants.MAX_DOC_VERSIONS
					+ ")*"
					+ DocConstants.MAX_DOC_VERSIONS
					+ ")"
					+ " and  (s.doc_id/"
					+ DocConstants.MAX_DOC_VERSIONS
					+ ")*("
					+ DocConstants.MAX_DOC_VERSIONS
					+ ")+"
					+ (DocConstants.MAX_DOC_VERSIONS - 1)
					+ "))"
					+ ") with ur";
		} else {

			ResultSet rsDocIDs = stmtDocs.executeQuery(strQuery);

			while (rsDocIDs.next()) {
				if (m_pdLog.isDebugEnabled()) {
					m_pdLog.debug("docid=" + rsDocIDs.getInt("doc_id"));
				}
				strDocIdsTmp =
					strDocIdsTmp
						+ String.valueOf(rsDocIDs.getInt("doc_id"))
						+ StringUtil.COMMA;
			}
			rsDocIDs.close();

		}

		if (m_pdLog.isDebugEnabled()) {
			m_pdLog.debug(strDocIdsTmp);
		}

		stmtDocs.close();
		return strDocIdsTmp;

	}

	/**
	 * @param iParentID
	 * @param vtChildren
	 * @param bIsAdmin
	 * @param strUserID
	 * @return
	 */
	private synchronized Vector getAllChildrenIds(
		int iParentID,
		Vector vtChildren,
		boolean bIsAdmin,
		String strUserID)
		throws SQLException {
		String strCats = StringUtil.EMPTY_STRING;
		String strDocs = StringUtil.EMPTY_STRING;

		try {
			vtChildren.addElement(strCats);
			vtChildren.addElement(strDocs);

			Vector vtSubCats = getSubCats(iParentID);

			if (vtSubCats.size() > 0) {
				for (int i = 0; i < vtSubCats.size(); i++) {
					ETSCat udCat = (ETSCat) vtSubCats.elementAt(i);
					if (udCat.getIbmOnly() != Defines.ETS_IBM_CONF
						&& (bIsAdmin || udCat.getUserId().equals(strUserID))) {
						vtChildren =
							getAllSubChildrenIds(
								udCat.getId(),
								vtChildren,
								bIsAdmin,
								strUserID);
						String strTmp = (String) vtChildren.elementAt(0);
						strTmp = strTmp + String.valueOf(udCat.getId()) + ",";
						vtChildren.setElementAt(strTmp, 0);
					}
				}
			}

			String d_temp = (String) vtChildren.elementAt(1);
			d_temp = getChildDocIds(iParentID, d_temp, bIsAdmin, strUserID);
			vtChildren.setElementAt(d_temp, 1);

		} catch (SQLException se) {
			m_pdLog.error("sql error= " + se);
			throw se;
		}

		return vtChildren;
	}

	/**
	 * @param iParentID
	 * @param vtChildren
	 * @param bIsAdmin
	 * @param strUserID
	 * @return
	 * @throws SQLException In case of database errors
	 * @throws Exception
	 */
	private synchronized Vector getAllSubChildrenIds(
		int iParentID,
		Vector vtChildren,
		boolean bIsAdmin,
		String strUserID)
		throws SQLException {

		try {
			Vector subcats = getSubCats(iParentID);

			for (int i = 0; i < subcats.size(); i++) {
				ETSCat c = (ETSCat) subcats.elementAt(i);
				if (c.getIbmOnly() != Defines.ETS_IBM_CONF
					&& (bIsAdmin || c.getUserId().equals(strUserID))) {
					vtChildren =
						getAllSubChildrenIds(
							c.getId(),
							vtChildren,
							bIsAdmin,
							strUserID);
					String c_temp = (String) vtChildren.elementAt(0);
					c_temp = c_temp + String.valueOf(c.getId()) + ",";
					vtChildren.setElementAt(c_temp, 0);
				}
			}

			String d_temp = (String) vtChildren.elementAt(1);
			d_temp = getChildDocIds(iParentID, d_temp, bIsAdmin, strUserID);
			vtChildren.setElementAt(d_temp, 1);

		} catch (SQLException se) {
			m_pdLog.error("sql error= " + se);
			throw se;
		}

		return vtChildren;
	}

	/**
	 * @param cIBMOnly
	 * @param strDocIDs
	 * @throws SQLException In case of database errors
	 * @throws Exception
	 */
	private synchronized void updateDocIbmOnly(char cIBMOnly, String strDocIDs)
		throws SQLException {
		PreparedStatement stmtUpdateDocs =
			m_pdConnection.prepareStatement(
				"update ets.ets_doc set ibm_only=? where doc_id in ("
					+ strDocIDs
					+ ")");

		try {
			stmtUpdateDocs.setString(1, String.valueOf(cIBMOnly));

			stmtUpdateDocs.executeUpdate();
			stmtUpdateDocs.close();
		} catch (SQLException se) {
			m_pdLog.error("sql error= " + se);
			throw se;
		}
	}

	/**
	 * @param cIBMOnly
	 * @param strCatIDs
	 * @throws SQLException In case of database errors
	 * @throws Exception
	 */
	private synchronized void updateCatIbmOnly(char cIBMOnly, String strCatIDs)
		throws SQLException {
		PreparedStatement stmtUpdateCat =
			m_pdConnection.prepareStatement(
				"update ets.ets_cat set ibm_only=? where cat_id in ("
					+ strCatIDs
					+ ")");

		try {
			stmtUpdateCat.setString(1, String.valueOf(cIBMOnly));

			stmtUpdateCat.executeUpdate();
			stmtUpdateCat.close();
		} catch (SQLException se) {
			m_pdLog.error("sql error= " + se);
			throw se;
		}
	}

	/**
	 * @param udCat
	 * @param bIsAdmin
	 * @param strUserID
	 * @param strOption
	 * @return
	 * @throws SQLException In case of database errors
	 * @throws Exception
	 */
	public synchronized boolean updateCat(
		ETSCat udCat,
		boolean bIsAdmin,
		String strUserID,
		String strOption)
		throws SQLException, Exception {
		boolean bSuccess = false;
		try {

			bSuccess = updateCat(udCat);

			if (!StringUtil.isNullorEmpty(strOption)
				&& !strOption.equals("1")) {
				String strCatIDs = StringUtil.EMPTY_STRING;
				String strDocIDs = StringUtil.EMPTY_STRING;
				Vector vtChildren = new Vector();

				if (strOption.equals("2")) {
					//only docs
					strDocIDs =
						getChildDocIds(
							udCat.getId(),
							strDocIDs,
							bIsAdmin,
							strUserID);
				} else if (strOption.equals("3")) {
					// propogate to all nodes under this one
					vtChildren.addElement(strCatIDs);
					vtChildren.addElement(strDocIDs);
					vtChildren =
						getAllChildrenIds(
							udCat.getId(),
							vtChildren,
							bIsAdmin,
							strUserID);
					strCatIDs = (String) vtChildren.elementAt(0);
					strDocIDs = (String) vtChildren.elementAt(1);
				}

				if (m_pdLog.isDebugEnabled()) {
					m_pdLog.debug("c: " + strCatIDs);
					m_pdLog.debug("d: " + strDocIDs);
				}

				if (!StringUtil.isNullorEmpty(strCatIDs)) {
					strCatIDs = strCatIDs.substring(0, strCatIDs.length() - 1);
					updateCatIbmOnly(udCat.getIbmOnly(), strCatIDs);
				}
				if (!StringUtil.isNullorEmpty(strDocIDs)) {
					strDocIDs = strDocIDs.substring(0, strDocIDs.length() - 1);
					updateDocIbmOnly(udCat.getIbmOnly(), strDocIDs);
				}

				if (!StringUtil.isNullorEmpty(strDocIDs)
					&& udCat.getIbmOnly() != Defines.ETS_PUBLIC) {
					Vector vUsers =
						getAllDocRestrictedUserIds(
							strDocIDs,
							udCat.getProjectId());
					String strUserIDs = StringUtil.EMPTY_STRING;
					for (int u = 0; u < vUsers.size(); u++) {
						try {
							String strIRID = (String) vUsers.elementAt(u);
							String strEdgeUserID =
								AccessCntrlFuncs.getEdgeUserId(
									m_pdConnection,
									strIRID);
							String strDecafType =
								AccessCntrlFuncs.decafType(
									strEdgeUserID,
									m_pdConnection);
							if (!strDecafType.equals("I")) {
								if (!StringUtil.isNullorEmpty(strUserIDs)) {
									strUserIDs = ",'" + strIRID + "'";
								} else {
									strUserIDs = "'" + strIRID + "'";
								}
							}
						} catch (AMTException a) {
							m_pdLog.error(
								"amt exception in getibmmembers err= " + a);
						} catch (SQLException s) {
							m_pdLog.error("sql exception= " + s);
							throw s;
						}
					}

					if (!strUserIDs.equals("")) {
						updateDocResUsers(
							strDocIDs,
							udCat.getProjectId(),
							strUserIDs);
					}
				}

			}
		} catch (SQLException e) {
			m_pdLog.error("sql error=", e);
			throw e;
		} catch (Exception ex) {
			m_pdLog.error("error=", ex);
			throw ex;
		}
		return bSuccess;
	}

	/**
	 * @param strDocIDs
	 * @param strProjectID
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public Vector getAllDocRestrictedUserIds(
		String strDocIDs,
		String strProjectID)
		throws SQLException {

		Statement stmtProjectMem = m_pdConnection.createStatement();

		String query =
			"select distinct u.user_id from ETS.ETS_USERS u, "
				+ "ets.ets_private_doc t "
				+ " where u.user_project_id = '"
				+ strProjectID
				+ "'"
				+ " and u.user_id=t.user_id and u.user_project_id=t.project_id "
				+ "and t.doc_id in("
				+ strDocIDs
				+ ")  and  t.ACCESS_TYPE= '"+ Defines.DOC_READ_ACCESS +"' with ur";

		ResultSet rsProjectMembers = stmtProjectMem.executeQuery(query);

		Vector vtMembers = new Vector();
		while (rsProjectMembers.next()) {
			vtMembers.addElement(rsProjectMembers.getString("user_id"));
		}
		rsProjectMembers.close();
		stmtProjectMem.close();
		return vtMembers;
	}

	/**
	 * @param strDocIDs
	 * @param strProjectID
	 * @param strRemove
	 * @return
	 * @throws SQLException In case of database errors
	 * @throws Exception
	 */
	private synchronized boolean updateDocResUsers(
		String strDocIDs,
		String strProjectID,
		String strRemove)
		throws SQLException {
		String strQuery = StringUtil.EMPTY_STRING;
		Statement stmtDocRes = m_pdConnection.createStatement();

		if (!StringUtil.isNullorEmpty(strRemove)) {
			strQuery =
				"delete from ets.ets_private_doc"
					+ " where doc_id in ("
					+ strDocIDs
					+ ")"
					+ " and project_id = '"
					+ strProjectID
					+ "'"
					+ " and user_id in ("
					+ strRemove
					+ ")";
			if (m_pdLog.isDebugEnabled()) {
				m_pdLog.debug("updateALLDocResUsers=" + strQuery);
			}
			stmtDocRes.executeUpdate(strQuery);
		}

		stmtDocRes.close();

		return true;
	}

	/**
	 * @param udCat
	 * @param bIsCatView
	 * @return
	 */
	public Vector getPMOBreadCrumbTrail(ETSPMOffice udCat)
		throws SQLException {
		Vector vtPMOBreadCrumbs = new Vector();

		try {

			if (udCat != null) {
				vtPMOBreadCrumbs.addElement(udCat);
				ETSPMOffice udPMOffice = udCat;
				while (!udPMOffice
					.getPMO_Project_ID()
					.equals(udPMOffice.getPMOID())) {
					udPMOffice =
						getPMOfficeObjectDetail(
							udPMOffice.getPMO_Project_ID(),
							udPMOffice.getPMO_Parent_ID());
					vtPMOBreadCrumbs.addElement(udPMOffice);

				}
			}
		} catch (SQLException se) {
			m_pdLog.error("sql error in docman pmo breadcrumb= ", se);
			throw se;
		}

		return vtPMOBreadCrumbs;
	}

	/**
	 * @param sPMOProjectID
	 * @param sPMOID
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public ETSPMOffice getPMOfficeObjectDetail(
		String sPMOProjectID,
		String sPMOID)
		throws SQLException {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery =
			new StringBuffer(
				"SELECT PMO_ID,PMO_PROJECT_ID,PARENT_PMO_ID,NAME,TYPE,"
					+ "EST_START,EST_FINISH,STATE,START,FINISH,PERCENT_COMPLETE,"
					+ "BASE_FINISH,CURR_FINISH,CURR_FINISH_TYPE,LAST_TIMESTAMP "
					+ "FROM ETS.ETS_PMO_MAIN WHERE PMO_PROJECT_ID = '"
					+ sPMOProjectID
					+ "' AND PMO_ID = '"
					+ sPMOID
					+ "' for READ ONLY");
		ETSPMOffice pmOffice = new ETSPMOffice();

		stmt = m_pdConnection.createStatement();
		rs = stmt.executeQuery(sQuery.toString());

		if (rs.next()) {

			pmOffice.setPMOID(rs.getString("PMO_ID"));
			pmOffice.setPMO_Project_ID(rs.getString("PMO_PROJECT_ID"));
			pmOffice.setPMO_Parent_ID(rs.getString("PARENT_PMO_ID"));
			pmOffice.setName(rs.getString("NAME"));
			pmOffice.setType(rs.getString("TYPE"));
			pmOffice.setEstimatedStartDate(rs.getTimestamp("EST_START"));
			pmOffice.setEstimatedFinishDate(rs.getTimestamp("EST_FINISH"));
			pmOffice.setState(rs.getString("STATE"));
			pmOffice.setStartDate(rs.getTimestamp("START"));
			pmOffice.setFinishDate(rs.getTimestamp("FINISH"));
			pmOffice.setPercentComplete(rs.getString("PERCENT_COMPLETE"));

			pmOffice.setBaseFinish(rs.getTimestamp("BASE_FINISH"));
			pmOffice.setCurrFinish(rs.getTimestamp("CURR_FINISH"));
			pmOffice.setCurrFinishType(rs.getString("CURR_FINISH_TYPE"));
			pmOffice.setLastTimestamp(rs.getTimestamp("LAST_TIMESTAMP"));

		}

		rs.close();
		stmt.close();

		return pmOffice;

	}

	/**
	 * @param strProjectId
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public Vector getAllCats(String strProjectId) throws SQLException {
		try {
			Vector vtCats = new Vector();

			PreparedStatement stmtCats;
			stmtCats =
				m_pdConnection.prepareStatement(
					"select * from ets.ets_cat where "
						+ "project_id=? "
						+ "and view_type=? "
						+ "order by parent_id WITH UR");
			stmtCats.setString(1, strProjectId);
			stmtCats.setInt(2, 9);

			ResultSet rsCats = stmtCats.executeQuery();
			vtCats = getCats(rsCats);

			rsCats.close();
			stmtCats.close();

			vtCats = populateUserNames(vtCats);

			return vtCats;
		} catch (SQLException e) {
			m_pdLog.error("sql error =" + DocUpdateDAO.getStackTrace(e));
			throw e;
		}

	}

	/**
	 * @param cNode
	 * @param iNodeID
	 * @param iNewParentCat
	 * @param strUserID
	 * @param cIBMOnly
	 * @param strProjectID
	 * @return
	 * @throws SQLException In case of database errors
	 * @throws Exception
	 */
	public boolean updateParentId(
		char cNode,
		int iNodeID,
		int iNewParentCat,
		char cIBMOnly,
		String strProjectID,
		String strUserID)
		throws SQLException {
		boolean bUpdated = false;

		try {

			PreparedStatement stmtUpdateCat;
			String strQuery = "";

			if (cNode == Defines.NODE_CAT) {
				if (cIBMOnly == 'x') {
					strQuery =
						"update ets.ets_cat set parent_id=?,user_id=?,"
							+ "last_timestamp=current timestamp where cat_id=?";
					stmtUpdateCat = m_pdConnection.prepareStatement(strQuery);
					stmtUpdateCat.setInt(1, iNewParentCat);
					stmtUpdateCat.setString(2, strUserID);
					stmtUpdateCat.setInt(3, iNodeID);
				} else {
					strQuery =
						"update ets.ets_cat set parent_id=?,user_id=?,"
							+ "ibm_only=?,last_timestamp=current timestamp "
							+ "where cat_id=?";
					stmtUpdateCat = m_pdConnection.prepareStatement(strQuery);
					stmtUpdateCat.setInt(1, iNewParentCat);
					stmtUpdateCat.setString(2, strUserID);
					stmtUpdateCat.setString(3, String.valueOf(cIBMOnly));
					stmtUpdateCat.setInt(4, iNodeID);

				}
			} else {
				int iLowestPossibleID =
					(iNodeID / DocConstants.MAX_DOC_VERSIONS)
						* DocConstants.MAX_DOC_VERSIONS;
				int iHighestPossibleID =
					iLowestPossibleID + DocConstants.MAX_DOC_VERSIONS;
				if (m_pdLog.isDebugEnabled()) {
					m_pdLog.debug("lpid=" + iLowestPossibleID);
					m_pdLog.debug("hid=" + iHighestPossibleID);
				}
				if (cIBMOnly == 'x') {
					strQuery =
						"update ets.ets_doc set cat_id=?, "
							+ "updated_by=?,doc_update_date=current timestamp "
							+ "where doc_id>=? and doc_id<?";
					stmtUpdateCat = m_pdConnection.prepareStatement(strQuery);
					stmtUpdateCat.setInt(1, iNewParentCat);
					stmtUpdateCat.setString(2, strUserID);
					stmtUpdateCat.setInt(3, iLowestPossibleID);
					stmtUpdateCat.setInt(4, iHighestPossibleID);
				} else {
					strQuery =
						"update ets.ets_doc set cat_id=?,ibm_only=?,"
							+ "updated_by=?,doc_update_date=current timestamp "
							+ "where doc_id>=? and doc_id<?";
					stmtUpdateCat = m_pdConnection.prepareStatement(strQuery);
					stmtUpdateCat.setInt(1, iNewParentCat);
					stmtUpdateCat.setString(2, String.valueOf(cIBMOnly));
					stmtUpdateCat.setString(3, strUserID);
					stmtUpdateCat.setInt(4, iLowestPossibleID);
					stmtUpdateCat.setInt(5, iHighestPossibleID);
				}
			}

			int iResult = stmtUpdateCat.executeUpdate();

			if (iResult >= 1) {
				bUpdated = true;
			} else {
				bUpdated = false;
			}

			stmtUpdateCat.close();

			boolean bIsAdmin = true; //to get all nodes

			if (cIBMOnly != 'x' && cNode == Defines.NODE_CAT) {
				String strCatIDs = StringUtil.EMPTY_STRING;
				String strDocIDs = StringUtil.EMPTY_STRING;
				Vector vtChildren = new Vector();

				vtChildren.addElement(strCatIDs);
				vtChildren.addElement(strDocIDs);
				vtChildren =
					getAllChildrenIds(iNodeID, vtChildren, bIsAdmin, strUserID);
				strCatIDs = (String) vtChildren.elementAt(0);
				strDocIDs = (String) vtChildren.elementAt(1);

				if (m_pdLog.isDebugEnabled()) {
					m_pdLog.debug("c: " + strCatIDs);
					m_pdLog.debug("d: " + strDocIDs);
				}

				if (!strCatIDs.equals(StringUtil.EMPTY_STRING)) {
					strCatIDs = strCatIDs.substring(0, strCatIDs.length() - 1);
					updateCatIbmOnly(cIBMOnly, strCatIDs);
				}
				if (!strDocIDs.equals(StringUtil.EMPTY_STRING)) {
					strDocIDs = strDocIDs.substring(0, strDocIDs.length() - 1);
					updateDocIbmOnly(cIBMOnly, strDocIDs);
				}

				if (!strDocIDs.equals(StringUtil.EMPTY_STRING)
					&& cIBMOnly != Defines.ETS_PUBLIC) {
					Vector vUsers =
						getAllDocRestrictedUserIds(strDocIDs, strProjectID);
					String strUserIds = StringUtil.EMPTY_STRING;
					for (int u = 0; u < vUsers.size(); u++) {
						try {
							String irid = (String) vUsers.elementAt(u);
							String strEdgeUserID =
								AccessCntrlFuncs.getEdgeUserId(
									m_pdConnection,
									irid);
							String strDecafType =
								AccessCntrlFuncs.decafType(
									strEdgeUserID,
									m_pdConnection);
							if (!strDecafType.equals("I")) {
								if (!strUserIds.equals(""))
									strUserIds = ",'" + irid + "'";
								else
									strUserIds = "'" + irid + "'";
							}
						} catch (AMTException a) {
							m_pdLog.error(
								"amt exception in getibmmembers err= " + a);
						} catch (SQLException s) {
							m_pdLog.error(
								"sql exception in getibmmembers err= " + s);
							throw s;
						}
					}

					if (!strUserIds.equals("")) {
						updateDocResUsers(strDocIDs, strProjectID, strUserIds);
					}
				}
			} else if (cIBMOnly != 'x' && cNode == Defines.NODE_DOC) {
				Vector vtPrevVersions = getPreviousVersions(iNodeID, 0);
				String strDocIDs = StringUtil.EMPTY_STRING;
				for (int i = 0; i < vtPrevVersions.size(); i++) {
					ETSDoc pd = (ETSDoc) vtPrevVersions.elementAt(i);
					//strDocIDs = strDocIDs + pd.getId() + ",";
					if (!strDocIDs.equals(""))
						strDocIDs = ",'" + pd.getId() + "'";
					else
						strDocIDs = "'" + pd.getId() + "'";
				}

				Vector vUsers =
					getAllDocRestrictedUserIds(strDocIDs, strProjectID);
				String strUserIDs = StringUtil.EMPTY_STRING;
				for (int u = 0; u < vUsers.size(); u++) {
					try {
						String irid = (String) vUsers.elementAt(u);
						String strEdgeUserID =
							AccessCntrlFuncs.getEdgeUserId(
								m_pdConnection,
								irid);
						String strDecafType =
							AccessCntrlFuncs.decafType(
								strEdgeUserID,
								m_pdConnection);
						if (!strDecafType.equals("I")) {
							if (!strUserIDs.equals(""))
								strUserIDs = ",'" + irid + "'";
							else
								strUserIDs = "'" + irid + "'";
						}
					} catch (AMTException a) {
						m_pdLog.error(
							"amt exception in getibmmembers err= " + a);
					} catch (SQLException s) {
						m_pdLog.error(
							"sql exception in getibmmembers err= " + s);
						throw s;
					}
				}

				if (!strUserIDs.equals("")) {
					updateDocResUsers(strDocIDs, strProjectID, strUserIDs);
				}
			}

		} catch (SQLException e) {
			m_pdLog.error("sql error =" + DocUpdateDAO.getStackTrace(e));
			throw e;
		}

		return bUpdated;
	}

	/**
	 * @param docID
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public Vector getPreviousVersions(int docID, int iHighID) throws SQLException {

		int iLowID =
			(docID / DocConstants.MAX_DOC_VERSIONS)
				* DocConstants.MAX_DOC_VERSIONS;
		if (iHighID == 0) {
		    iHighID = iLowID + DocConstants.MAX_DOC_VERSIONS;
		}

		String query =
			"select d.*, "
				+ "(select count(m.doc_id) from ets.ets_doc_metrics m "
				+ "where d.doc_id=m.doc_id) as hits "
				+ "from ETS.ETS_DOC d where d.DOC_ID >= "
				+ iLowID
				+ " and d.DOC_ID < "
				+ iHighID
				+ " and delete_flag!='"
				+ DocConstants.TRUE_FLAG
				+ "' order by d.DOC_ID desc with ur";

		Statement stmtPreviousVersions = m_pdConnection.createStatement();
		ResultSet rsPreviousVersions = stmtPreviousVersions.executeQuery(query);

		Vector vtAllVersions = getDocs(rsPreviousVersions);

		rsPreviousVersions.close();
		stmtPreviousVersions.close();

		if (vtAllVersions.size() > 0) {
			return populateUserNames(vtAllVersions);
		} else {
			return null;
		}
	}

	/**
	 * @param rsDocs
	 * @return
	 * @throws SQLException In case of database errors
	 */
	private Vector getDocs(ResultSet rsDocs) throws SQLException {
		Vector vtDocs = new Vector();
		while (rsDocs.next()) {
			ETSDoc doc = getDoc(rsDocs);
			vtDocs.addElement(doc);
		}
		return vtDocs;
	}

	/**
	 * @param strProjectID
	 * @param strSortBy
	 * @param strSortOrder
	 * @return
	 * @throws SQLException In case of database errors
	 * @throws Exception
	 */
	public Vector getAllDocMetrics(
		String strProjectID,
		String strSortBy,
		String strSortOrder,
		DocExpirationDate dtStartDate,
		DocExpirationDate dtEndDate)
		throws SQLException, Exception {

		Vector vtDocMetrics = new Vector();

		String strDocNameCol = "UPPER(d.doc_name)";

		if (strSortBy.equals(Defines.SORT_BY_DATE_STR)) {
			strDocNameCol =
				"d.doc_upload_date " + strSortOrder + ",UPPER(d.doc_name)";
		} else if (strSortBy.equals(Defines.SORT_BY_SIZE_STR)) {
			strDocNameCol = "d.doc_size";
		} else if (strSortBy.equals(Defines.SORT_BY_HITS_STR)) {
			strDocNameCol = "hits " + strSortOrder + ",UPPER(d.doc_name)";
		}

		String strMetricsSQL = "select count(m.doc_id) from ets.ets_doc_metrics m where m.doc_id=d.doc_id";
		
		boolean bIsByDate = false;
		if (dtStartDate != null && dtEndDate != null) {
		    strMetricsSQL = strMetricsSQL + " and timestamp between ? and ?";
		    bIsByDate = true;
		}
		
		try {
			PreparedStatement stmtDocMetrics;
			stmtDocMetrics =
				m_pdConnection.prepareStatement(
					"select d.*,"
						+ "("+ strMetricsSQL +") as hits "
						+ "from ets.ets_doc d "
						+ "where d.project_id=? and d.latest_version='1' "
						+ " and itar_upload_status !='P'"
						+ "and d.delete_flag !='"
						+ DocConstants.TRUE_FLAG
						+ "' and d.doc_type="
						+ Defines.DOC
						+ " order by "
						+ strDocNameCol
						+ " "
						+ strSortOrder
						+ " with ur");

			if (bIsByDate) {
				stmtDocMetrics.setDate(1, new java.sql.Date(dtStartDate.getDate()));
				stmtDocMetrics.setDate(2, new java.sql.Date(dtEndDate.getDate()));
				stmtDocMetrics.setString(3, strProjectID);
			}
			else {
			stmtDocMetrics.setString(1, strProjectID);
			}

			ResultSet rsDocMetrics = stmtDocMetrics.executeQuery();

			while (rsDocMetrics.next()) {
				ETSDoc doc = getDoc(rsDocMetrics);
				//doc.setDocHits(rs.getInt("hits"));
				vtDocMetrics.addElement(doc);
			}

			rsDocMetrics.close();
			stmtDocMetrics.close();

			return vtDocMetrics;
		} catch (SQLException e) {
			m_pdLog.error("sql error =" + DocUpdateDAO.getStackTrace(e));
			throw e;
		}
	}

	/**
	 * @param strProjectId
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public Vector getProjMembers(String strProjectId) throws SQLException {
	    DocReaderDAO udReader = new DocReaderDAO();
	    udReader.setConnection(m_pdConnection);
	    return udReader.getProjMembers(strProjectId);
	}


	/**
	 * @param strProjectId
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public Vector getProjMembersWithNames(String strProjectId) throws SQLException {
	    DocReaderDAO udReader = new DocReaderDAO();
	    udReader.setConnection(m_pdConnection);
	    return udReader.getProjMembersWithNames(strProjectId);
	}

	/**
	 * @param strProjectId
	 * @param bCheckAmtTables
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public Vector getProjMembers(String strProjectId, boolean bCheckAmtTables)
		throws SQLException {
	    DocReaderDAO udReader = new DocReaderDAO();
	    udReader.setConnection(m_pdConnection);
	    return udReader.getProjMembers(strProjectId, bCheckAmtTables);
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

	    DocUpdateDAO udUpdateDAO = new DocUpdateDAO();
	    udUpdateDAO.setConnection(m_pdConnection);

	    // Since this method has been re-factored and moved to DocUpdateDAO,
	    // The list is now expected to contain ETSDocfile objects rather than
	    // FormFile objects. So for each FormFile object in lstFiles, convert it 
	    // to ETSDocFile.
	    List lstDocFiles = new ArrayList();
			for (int iCounter = 0; iCounter < lstFiles.size(); iCounter++) {
				FormFile pdFormFile = (FormFile) lstFiles.get(iCounter);
			ETSDocFile udDocFile = new ETSDocFile();
			udDocFile.setFileName(pdFormFile.getFileName());
			udDocFile.setFileSize(pdFormFile.getFileSize());
			udDocFile.setInputStream(pdFormFile.getInputStream());
			lstDocFiles.add(udDocFile);
			}

	    return udUpdateDAO.addDocMethod(udDoc, lstDocFiles, iExistingDocID, bIsITAR);
			
	}

	/**
	 * @param udDoc
	 * @param pdInStream
	 * @return
	 * @throws SQLException In case of database errors
	 */
	private synchronized boolean addDocFile(
		ETSDoc udDoc,
		InputStream pdInStream)
		throws SQLException {
		boolean bSuccess = false;

		try {
			PreparedStatement stmtInsDocFile =
				m_pdConnection.prepareStatement(
					"insert into ets.ets_docfile("
						+ "doc_id,docfile_name,docfile,"
						+ "docfile_size,docfile_update_date) "
						+ "values(?,?,?,?,current timestamp)");

			stmtInsDocFile.setInt(1, udDoc.getId());
			stmtInsDocFile.setString(2, udDoc.getFileName());
			stmtInsDocFile.setBinaryStream(3, pdInStream, udDoc.getSize());
			stmtInsDocFile.setInt(4, udDoc.getSize());
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

	    DocUpdateDAO udUpdateDAO = new DocUpdateDAO();
	    udUpdateDAO.setConnection(m_pdConnection);
	    return udUpdateDAO.addDocFile(iDocID, strDocFileName, iDocSize, pdInStream);
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
		String strFileDescription,
		String strFileStatus,
		InputStream pdInStream)
		throws SQLException {
		boolean bSuccess = false;

		try {
		    DocUpdateDAO udUpdateDAO = new DocUpdateDAO();
		    udUpdateDAO.setConnection(m_pdConnection);
		    
			int iNewDocFileID = udUpdateDAO.getNewDocFileID(iDocID);

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

			if (!StringUtil.isNullorEmpty(strFileDescription)) {
				stmtInsDocFile.setString(6, strFileDescription);	
			} else {
				stmtInsDocFile.setNull(6, Types.VARCHAR);
			}
			
			if (!StringUtil.isNullorEmpty(strFileStatus)) {
				stmtInsDocFile.setString(7, strFileStatus);	
			} else {
				stmtInsDocFile.setNull(7, Types.VARCHAR);
			}

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


/* ************************************************ START **************************************************** */
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
	    DocUpdateDAO udUpdateDAO = new DocUpdateDAO();
	    udUpdateDAO.setConnection(m_pdConnection);
	    return udUpdateDAO.addAdditionalEditors(vtUsers, strDocID, strProjectId);
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
	    DocUpdateDAO udUpdateDAO = new DocUpdateDAO();
	    udUpdateDAO.setConnection(m_pdConnection);
	    return udUpdateDAO.addDocResGroupsEdit(strGroupIDs, iDocID, strProjectID);
	}

	/**
	 * @param strGroupId
	 * @return
	 * @throws SQLException
	 */
	public List getGroupUsersEdit(String strGroupId) throws SQLException {
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
	 * @param strDocIDs
	 * @param strProjectID
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public Vector getAllDocRestrictedEditUserIds(
		String strDocIDs,
		String strProjectID)
		throws SQLException {

		Statement stmtProjectMem = m_pdConnection.createStatement();

		String query =
			"select distinct u.user_id from ETS.ETS_USERS u, "
				+ "ets.ets_private_doc t "
				+ " where u.user_project_id = '"
				+ strProjectID
				+ "'"
				+ " and u.user_id=t.user_id and u.user_project_id=t.project_id "
				+ "and t.doc_id in("
				+ strDocIDs
				+ ") and t.ACCESS_TYPE = '"+ Defines.DOC_EDIT_ACCESS +"' with ur";

		ResultSet rsProjectMembers = stmtProjectMem.executeQuery(query);

		Vector vtMembers = new Vector();
		while (rsProjectMembers.next()) {
			vtMembers.addElement(rsProjectMembers.getString("user_id"));
		}
		rsProjectMembers.close();
		stmtProjectMem.close();
		return vtMembers;
	}


	/**
	 * @param iDocId
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public List getAllDocRestrictedEditGroupIds(int iDocId) throws SQLException {

		String strQuery =
			"SELECT * FROM ETS.GROUPS "
				+ "WHERE GROUP_ID IN ("
				+ "SELECT DISTINCT GROUP_ID FROM ETS.ETS_PRIVATE_DOC"
				+ " WHERE DOC_ID = ? AND ACCESS_TYPE =  ?)"
				+ " WITH UR";

		PreparedStatement stmtGroups =
			m_pdConnection.prepareStatement(strQuery);

		stmtGroups.setInt(1, iDocId);
		stmtGroups.setString(2, Defines.DOC_EDIT_ACCESS);
		ResultSet rsGroups = stmtGroups.executeQuery();

		List lstMembers = new ArrayList();
		while (rsGroups.next()) {
			lstMembers.add(populateGroup(rsGroups));
		}
		rsGroups.close();
		stmtGroups.close();
		return lstMembers;
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

		DocReaderDAO udReader = new DocReaderDAO();
		udReader.setConnection(m_pdConnection);
		return udReader.getRestrictedProjMembersEdit(strProjectId, iID, bCheckAmtTables, bIsCat);
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
		DocReaderDAO udReader = new DocReaderDAO();
		udReader.setConnection(m_pdConnection);
		return udReader.getRestrictedProjMembersEdit(strProjectId, iID, bIsCat);
	}


	/**
	 * @param strUserId
	 * @param strProjectId
	 * @return
	 * @throws SQLException
	 */
	public List getUserEditGroups(String strUserId, String strProjectId)
		throws SQLException {
		List lstGroups = new ArrayList();

		PreparedStatement stmtGroups =
			m_pdConnection.prepareStatement(
				"SELECT * FROM ETS.GROUPS "
					+ "WHERE PROJECT_ID=? AND (GROUP_ID IN ("
					+ "SELECT GROUP_ID FROM ETS.USER_GROUPS WHERE USER_ID = ?) "
					+ "OR NAME=?) "
					+ "WITH UR");
		stmtGroups.setString(1, strProjectId);
		stmtGroups.setString(2, strUserId);
		stmtGroups.setString(3, Defines.GRP_ALL_USERS);
		ResultSet rsGroups = stmtGroups.executeQuery();

		while (rsGroups.next()) {
			lstGroups.add(populateGroup(rsGroups));
		}

		rsGroups.close();
		stmtGroups.close();
		return lstGroups;
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
		DocReaderDAO udReader = new DocReaderDAO();
		udReader.setConnection(m_pdConnection);
		editPriv = udReader.checkDocumentEditPriv(idoc_Id, strUser_Id, strProj_Id);

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
		
		DocReaderDAO udReader = new DocReaderDAO();
		udReader.setConnection(m_pdConnection);
		editPriv = udReader.checkDocumentEditPrivForGroup(idoc_Id, strUser_Id, strProj_Id);

		return editPriv;
	}


	/**
	 * @param udDoc
	 * @param bChangeIBM
	 * @param bChangeRes
	 * @param bAddAllRes
	 * @param strAdd
	 * @param strRemove
	 * @param vtAdd
	 * @return
	 * @throws SQLException In case of database errors
	 * @throws Exception
	 */
	public synchronized boolean updateDocPropEdit(
		ETSDoc udDoc,		boolean bChangeIBM,		boolean bChangeRes,		Vector bAddAllRes,
		String strAdd,		String strAddGroups,	String strRemove,		String strRemoveGroups,
		Vector vtAdd,		Vector vtAddGroups) 	throws SQLException, Exception {
		boolean bSuccess = false;
		try {
			if (bChangeIBM || (bChangeRes || udDoc.IsDPrivateEdit())) {
				bSuccess = updateDocPropEdit(	udDoc,		bChangeIBM,		bChangeRes,		bAddAllRes,
											strAdd,		strAddGroups,	strRemove,		strRemoveGroups,
											vtAdd,		vtAddGroups,	false);
			} else {
				bSuccess = updateDocPropEdit(udDoc);
			}

			//setEditHistory(udDoc.getId(), udDoc.getUpdatedBy(), "Update Document", "document properties updated "+udDoc.getUserId());
			
		} catch (SQLException e) {
			m_pdLog.error("sql error=", e);
			throw e;
		} catch (Exception ex) {
			m_pdLog.error("error=", ex);
			throw ex;
		}
			return bSuccess;
	}



	/**
	 * @param udDoc
	 * @param bChangeIbm
	 * @param bChangeRes
	 * @param vtAllAddResUsers
	 * @param strAdd
	 * @param strRemove
	 * @param vtAdd
	 * @param bIsNewUpload
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public synchronized boolean updateDocPropEdit(
		ETSDoc udDoc,
		boolean bChangeIbm,
		boolean bChangeRes,
		Vector vtAllAddResUsers,
		String strAdd,
		String strAddGroups,
		String strRemove,
		String strRemoveGroups,
		Vector vtAdd,
		Vector vtAddGroups,
		boolean bIsNewUpload)
		throws SQLException {
		PreparedStatement upDocSt = null;

		
		if (bIsNewUpload) {
			upDocSt =
				m_pdConnection.prepareStatement(
					"update ets.ets_doc set project_id=?,cat_id=?,"
						+ "doc_name=?,doc_description=?,doc_keywords=?,"
						+ "ibm_only=?,updated_by=?,"
						+ "doc_update_date=doc_upload_date,expiry_date=?,"
						+ "isprivate=?,ibm_conf=? where doc_id=?");
		} else {
			upDocSt =
				m_pdConnection.prepareStatement(
					"update ets.ets_doc set project_id=?,cat_id=?,"
						+ "doc_name=?,doc_description=?,doc_keywords=?,"
						+ "ibm_only=?,updated_by=?,"
						+ "doc_update_date=current timestamp,expiry_date=?,"
						+ "isprivate=?,ibm_conf=? where doc_id=?");
		}

		try {
			//spn 0312 projid
			upDocSt.setString(1, udDoc.getProjectId());
			upDocSt.setInt(2, udDoc.getCatId());
			//upDocSt.setString(3, udDoc.getUserId());
			upDocSt.setString(3, udDoc.getName());
			upDocSt.setString(4, udDoc.getDescription());
			upDocSt.setString(5, udDoc.getKeywords());
			upDocSt.setString(6, String.valueOf(udDoc.getIbmOnly()));
			upDocSt.setString(7, udDoc.getUpdatedBy());
			//FIX FIX FIX FIX EXPIRE DATE
			if (udDoc.getExpiryDate() == 0)
				upDocSt.setTimestamp(8, null);
			else
				upDocSt.setTimestamp(8, new Timestamp(udDoc.getExpiryDate()));

			upDocSt.setString(9, udDoc.getDPrivate() );
			upDocSt.setString(10, udDoc.getIBMConfidential());
			upDocSt.setInt(11, udDoc.getId());

			upDocSt.executeUpdate();
			upDocSt.close();
			addContentLog(
				udDoc.getId(),
				'D',
				udDoc.getProjectId(),
				Defines.UPDATE_DOC_PROP,
				udDoc.getUserId());

			if (!bIsNewUpload && (bChangeRes || udDoc.IsDPrivateEdit())) {

				updateDocResUsersEdit(
					udDoc,
					strAdd,
					strAddGroups,
					strRemove,
					strRemoveGroups);
			}

				updateDocPropPrevEdit(
				udDoc,
				bChangeRes,
				strRemove,
				strRemoveGroups,
				vtAdd,
				vtAddGroups);

			return true;
		} catch (SQLException se) {
			m_pdLog.error("sql error =" + se);
			throw se;
		} catch (Exception e) {
			m_pdLog.error("exception error =" + e);
			return false;
		}
	}



	/**
	 * @param udDoc
	 * @param strAdd
	 * @param strRemove
	 * @return
	 * @throws SQLException In case of database errors
	 * @throws Exception
	 */
	private synchronized boolean updateDocResUsersEdit(
		ETSDoc udDoc,
		String strAdd,
		String strAddGroups,
		String strRemove,
		String strRemoveGroups)
		throws SQLException, Exception {
		String strQuery = StringUtil.EMPTY_STRING;
		Statement stmtUpdDocResUsers = m_pdConnection.createStatement();

		if ((!strAdd.equals(StringUtil.EMPTY_STRING)) && udDoc.IsDPrivateEdit()) {
			strQuery =
				"insert into ets.ets_private_doc(doc_id,user_id,project_id, ACCESS_TYPE) "
					+ "values "
					+ strAdd ;
			System.out.println("SQL :: "+ strQuery);			
			stmtUpdDocResUsers.executeUpdate(strQuery);
		}

		if ((!strAddGroups.equals(StringUtil.EMPTY_STRING)) && udDoc.IsDPrivateEdit()) {
			strQuery =
				"insert into ets.ets_private_doc(doc_id,group_id,project_id, ACCESS_TYPE) "
					+ "values "
					+ strAddGroups ;
			System.out.println("SQL :: "+ strQuery);			
			stmtUpdDocResUsers.executeUpdate(strQuery);
		}

		if (!strRemove.equals(StringUtil.EMPTY_STRING)) {
			strQuery =
				"delete from ets.ets_private_doc"
					+ " where doc_id = "
					+ udDoc.getId()
					+ " and project_id = '"
					+ udDoc.getProjectId()
					+ "'"
					+ " and user_id in ("
					+ strRemove
					+ ") AND ACCESS_TYPE = '"+ Defines.DOC_EDIT_ACCESS +"'";
			System.out.println("SQL :: "+ strQuery);			
			stmtUpdDocResUsers.executeUpdate(strQuery);
		}

		if (!strRemoveGroups.equals(StringUtil.EMPTY_STRING)) {
			// Do the same for Groups
			strQuery =
				"delete from ets.ets_private_doc"
					+ " where doc_id = "
					+ udDoc.getId()
					+ " and project_id = '"
					+ udDoc.getProjectId()
					+ "'"
					+ " and group_id in ("
					+ strRemoveGroups
					+ ") AND ACCESS_TYPE = '"+ Defines.DOC_EDIT_ACCESS +"'";
			System.out.println("SQL :: "+ strQuery);			
			stmtUpdDocResUsers.executeUpdate(strQuery);
		}
		stmtUpdDocResUsers.close();
		return true;
	}



	/**
	 * @param udDoc
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public synchronized boolean updateDocPropEdit(ETSDoc udDoc)
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
	 * @param udDoc
	 * @param bChangeRes
	 * @param strRemove
	 * @param vtAdd
	 * @return
	 * @throws SQLException In case of database errors
	 * @throws Exception
	 */
	private synchronized boolean updateDocPropPrevEdit(
		ETSDoc udDoc,
		boolean bChangeRes,
		String strRemove,
		String strRemoveGroups,
		Vector vtAdd,
		Vector vtAddGroups)
		throws SQLException, Exception {
		Vector vtPrev = getPreviousVersions(udDoc.getId(), 0);
		String strDocIds = StringUtil.EMPTY_STRING;
		for (int iCounter = 0; iCounter < vtPrev.size(); iCounter++) {
			ETSDoc udDocTmp = (ETSDoc) vtPrev.elementAt(iCounter);
			strDocIds = strDocIds + udDocTmp.getId() + StringUtil.COMMA;
		}

		if (!strDocIds.equals(StringUtil.EMPTY_STRING)) {
			strDocIds = strDocIds.substring(0, strDocIds.length() - 1);
			updateDocIbmOnlyPrivate(
				udDoc.getIbmOnly(),
				udDoc.getDPrivate(),
				strDocIds);

			if (bChangeRes || udDoc.IsDPrivateEdit()) {
				
				updateDocPrevResUsersEdit(
					udDoc.getId(),
					vtPrev,
					strDocIds,
					udDoc.getProjectId(),
					bChangeRes,
					strRemove,
					strRemoveGroups,
					vtAdd,
					vtAddGroups);
			}
		}

		return true;
	}


	/**
	 * @param iDocID
	 * @param vtPrevDocs
	 * @param strDocIDs
	 * @param strProjectID
	 * @param bChangeRes
	 * @param strRemove
	 * @param vtAdd
	 * @return
	 * @throws SQLException In case of database errors
	 * @throws Exception
	 */
	private synchronized boolean updateDocPrevResUsersEdit(
		int iDocID,
		Vector vtPrevDocs,
		String strDocIDs,
		String strProjectID,
		boolean bChangeRes,
		String strRemove,
		String strRemoveGroups,
		Vector vtAdd,
		Vector vtAddGroups)
		throws SQLException, Exception {
		String strQuery = StringUtil.EMPTY_STRING;
		Statement stmtUpdate = m_pdConnection.createStatement();
		String strPrevAdd = StringUtil.EMPTY_STRING;

		if (vtAdd.size() > 0 && vtPrevDocs.size() > 0) {
			for (int iCounter = 0; iCounter < vtPrevDocs.size(); iCounter++) {
				if (((ETSDoc) vtPrevDocs.elementAt(iCounter)).getId() != iDocID) {
					for (int iInner = 0; iInner < vtAdd.size(); iInner++) {
						if (!strPrevAdd.equals(StringUtil.EMPTY_STRING))
							strPrevAdd =
								strPrevAdd
									+ StringUtil.COMMA
									+ "("
									+ ((ETSDoc) vtPrevDocs.elementAt(iCounter))
										.getId()
									+ StringUtil.COMMA
									+ "'"
									+ vtAdd.elementAt(iInner)
									+ "'" + StringUtil.COMMA + "'"
									+ strProjectID
									+ "'"+ StringUtil.COMMA +"'" 
									+ Defines.DOC_EDIT_ACCESS + "')";
						else
							strPrevAdd =
								"("
									+ ((ETSDoc) vtPrevDocs.elementAt(iCounter))
										.getId()
									+ StringUtil.COMMA
									+ "'"
									+ vtAdd.elementAt(iInner)
									+ "'" + StringUtil.COMMA + "'"
									+ strProjectID
									+ "'"+ StringUtil.COMMA +"'" 
									+ Defines.DOC_EDIT_ACCESS + "')";
					}
				}

			}
			if (!strPrevAdd.equals(StringUtil.EMPTY_STRING)) {
				strQuery =
					"insert into ets.ets_private_doc"
						+ "(doc_id,user_id,project_id,ACCESS_TYPE)"
						+ " values "
						+ strPrevAdd;
				m_pdLog.error("SQL IS : " + strQuery);
				stmtUpdate.executeUpdate(strQuery);
			}
		}
		if (!strRemove.equals(StringUtil.EMPTY_STRING)) {
			strQuery =
				"delete from ets.ets_private_doc"
					+ " where doc_id in ("
					+ strDocIDs
					+ ")"
					+ " and project_id = '"
					+ strProjectID
					+ "'"
					+ " and user_id in ("
					+ strRemove
					+ ") AND ACCESS_TYPE = '"+ Defines.DOC_EDIT_ACCESS +"'";
			m_pdLog.error("SQL IS : " + strQuery);
			stmtUpdate.executeUpdate(strQuery);
		}

		strQuery = StringUtil.EMPTY_STRING;
		strPrevAdd = StringUtil.EMPTY_STRING;

		// Add and Remove for Groups
		if (vtAddGroups.size() > 0 && vtPrevDocs.size() > 0) {
			for (int iCounter = 0; iCounter < vtPrevDocs.size(); iCounter++) {
				if (((ETSDoc) vtPrevDocs.elementAt(iCounter)).getId()
					!= iDocID) {
					for (int iInner = 0;
						iInner < vtAddGroups.size();
						iInner++) {
						if (!strPrevAdd.equals(StringUtil.EMPTY_STRING))
							strPrevAdd =
								strPrevAdd
									+ StringUtil.COMMA
									+ "("
									+ ((ETSDoc) vtPrevDocs.elementAt(iCounter))
										.getId()
									+ StringUtil.COMMA
									+ "'"
									+ vtAddGroups.elementAt(iInner)
									+ "'" + StringUtil.COMMA + "'"
									+ strProjectID
									+ "'"+ StringUtil.COMMA +"'" 
									+ Defines.DOC_EDIT_ACCESS + "')";
						else
							strPrevAdd =
								"("
									+ ((ETSDoc) vtPrevDocs.elementAt(iCounter))
										.getId()
									+ StringUtil.COMMA
									+ "'"
									+ vtAddGroups.elementAt(iInner)
									+ "'"
									+ StringUtil.COMMA
									+ "'"
									+ strProjectID
									+ "'"+ StringUtil.COMMA +"'" 
									+ Defines.DOC_EDIT_ACCESS + "')";
					}
				}
			}
			if (!strPrevAdd.equals(StringUtil.EMPTY_STRING)) {
				strQuery =
					"insert into ets.ets_private_doc"
						+ "(doc_id,group_id,project_id, ACCESS_TYPE)"
						+ " values " 
						+ strPrevAdd;
				m_pdLog.error("SQL IS : " + strQuery);
				stmtUpdate.executeUpdate(strQuery);
			}
		}
		if (!strRemoveGroups.equals(StringUtil.EMPTY_STRING)) {
			strQuery =
				"delete from ets.ets_private_doc"
					+ " where doc_id in ("
					+ strDocIDs
					+ ")"
					+ " and project_id = '"
					+ strProjectID
					+ "'"
					+ " and group_id in ("
					+ strRemoveGroups
					+ ") AND ACCESS_TYPE = '"+ Defines.DOC_EDIT_ACCESS +"'";
			m_pdLog.error("SQL IS : " + strQuery);
			stmtUpdate.executeUpdate(strQuery);
		}

		stmtUpdate.close();

		return true;
	}

	

	/**
	 * @param strProjectID
	 * @param iID
	 * @param bIsCat
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public Vector getRestrictedProjMemberIdsEdit(
		String strProjectID,
		int iID,
		boolean bIsCat)
		throws SQLException {

		String strTable = "cat";
		if (!bIsCat) {
			strTable = "doc";
		}

		PreparedStatement stmtProjMembers =
			m_pdConnection.prepareStatement(
				"select u.user_id from ETS.ETS_USERS u, ets.ets_private_"
					+ strTable
					+ " t where u.user_project_id = ?"
					+ " and u.active_flag='A' and u.user_id=t.user_id and "
					+ "u.user_project_id=t.project_id and t."
					+ strTable
					+ "_id=? AND t.ACCESS_TYPE=? order by u.user_id with ur");

		stmtProjMembers.setString(1, strProjectID);
		stmtProjMembers.setInt(2, iID);
		stmtProjMembers.setString(3, Defines.DOC_EDIT_ACCESS);
		ResultSet rsProjMembers = stmtProjMembers.executeQuery();

		Vector vtMembers = new Vector();
		while (rsProjMembers.next()) {
			vtMembers.addElement(rsProjMembers.getString("user_id"));
		}
		rsProjMembers.close();
		stmtProjMembers.close();
		return vtMembers;
	}


	/**
	 * @param iDocID 
	 * @return
	 * @throws SQLException In case of database errors
	 */
	private synchronized int getSequenceNumber(int iDocID) throws SQLException {
	    DocUpdateDAO udUpdate = new DocUpdateDAO();
	    udUpdate.setConnection(m_pdConnection);
	    return udUpdate.getSequenceNumber(iDocID);
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
	private synchronized boolean setEditHistory(
		int idocID,
		String strUserId,
		String strAction,
		String strActionDetails) throws SQLException, Exception {
		
	    DocUpdateDAO udUpdate = new DocUpdateDAO();
	    udUpdate.setConnection(m_pdConnection);
	    return udUpdate.setEditHistory(idocID, strUserId, strAction, strActionDetails);

}

	public synchronized boolean setEditHistory(ETSDocEditHistory pEtsDocHistory) throws SQLException, Exception {
	    DocUpdateDAO udUpdate = new DocUpdateDAO();
	    udUpdate.setConnection(m_pdConnection);
	    return udUpdate.setEditHistory(pEtsDocHistory);
		}
	

	/**
	 * @param vtdocFileID
	 * @param idocId
	 */
	public synchronized boolean deleteAttachments(
		Vector vtdocFileID,
		int idocId) throws SQLException, Exception {
		try{
			
			if ( !vtdocFileID.isEmpty() )
			{
				for (int iCounter = 0; iCounter < vtdocFileID.size(); iCounter++) {
					boolean bSuccess = 
						deleteDocFile(Integer.parseInt( (String)vtdocFileID.elementAt(iCounter).toString() ),  idocId);
				}
			}

		} catch (SQLException e) {
			m_pdLog.error("sql error =" + DocUpdateDAO.getStackTrace(e));
			throw e;
		}
		return true;
}

	/**
	 * @param idocID
	 * @param idocFileId
	 */
	private synchronized boolean deleteAttachment(
		int idocID,
		int idocFileId) throws SQLException, Exception {
		String strQuery = StringUtil.EMPTY_STRING;
		try{
		Statement stmtUpdDocResUsers = m_pdConnection.createStatement();
			strQuery = "DELETE FROM ETS.ETS_DOCFILE WHERE DOC_ID = "+ idocID +" AND DOCFILE_ID="+ idocFileId ;
			stmtUpdDocResUsers.executeUpdate(strQuery);
			stmtUpdDocResUsers.close();

		} catch (SQLException e) {
			m_pdLog.error("sql error =" + DocUpdateDAO.getStackTrace(e));
			throw e;
		}
		return true;
}
	


	public Vector getAllVersionsDocEditHistory(ETSDoc etsDoc) throws SQLException, Exception
	{
		  Vector vtDocEditHistory = new Vector();
		  try
		  {		  		
		  		if(etsDoc.hasPreviousVersion())
		  		{
		  			Vector vtPrevVersions = getPreviousVersions(etsDoc.getId(),0);
		  			if (vtPrevVersions != null && vtPrevVersions.size() > 0)
		  			{
		  		        int iSize = vtPrevVersions.size();
		  		        for(int iCounter=0; iCounter < iSize; iCounter++) 
		  		        	{
		  		        		ETSDoc udPrevDoc = (ETSDoc) vtPrevVersions.get(iCounter);
		  		        		vtDocEditHistory.addAll(getDocEditHistory(udPrevDoc.getId())); 
		  		            }
		  		       }
		  		}
		  		else 
		  			vtDocEditHistory.addAll(getDocEditHistory(etsDoc.getId()));
		  } 
		  catch (SQLException e)
		 {
			e.printStackTrace();
		 }
		return vtDocEditHistory;
	}


	public Vector getDocEditHistory(int iDocId)throws SQLException, Exception
	{
		  String strQuery = StringUtil.EMPTY_STRING;
		  Vector vtdocHistory = new Vector();
		  ResultSet rsDocHistory = null;
		  try{
		  	  
		   	  Statement stmtDocHistory = m_pdConnection.createStatement();
//			  strQuery ="select * from ets.doc_edit_history where doc_id="+iDocId;
		   	  strQuery ="select * from ets.doc_edit_history where doc_id="+iDocId +" order by last_timestamp desc";

			  m_pdLog.debug("SQL :: getDocEditHistory() - "+ strQuery);   
			  
			  rsDocHistory = stmtDocHistory.executeQuery(strQuery);
			  while(rsDocHistory.next())
			  {
			  	ETSDocEditHistory etsDocHistory = getETSDocHistory(rsDocHistory);
			  	System.out.println("doc history---" + etsDocHistory.getDocId());
			  	vtdocHistory.add(etsDocHistory);
			  }
			  rsDocHistory.close();
			  stmtDocHistory.close();
		  } 
		  catch (SQLException e)
		  {
			  m_pdLog.error("sql error =" + DocUpdateDAO.getStackTrace(e));
			  throw e;
		}
		 catch (Exception e)
		 {
			  m_pdLog.error("common error =" + DocUpdateDAO.getStackTrace(e));
			  throw e;
		 }
		return populateUserNames(vtdocHistory);
	}

	private ETSDocEditHistory getETSDocHistory(ResultSet etsDocHistoryResultSet) throws SQLException, Exception
	{
		ETSDocEditHistory etsDocHistory = new ETSDocEditHistory();
		
		etsDocHistory.setDocId(etsDocHistoryResultSet.getInt("DOC_ID"));
		etsDocHistory.setSeqNo(etsDocHistoryResultSet.getInt("SEQ_NO"));
		etsDocHistory.setUserId(etsDocHistoryResultSet.getString("USER_ID"));
		etsDocHistory.setAction(etsDocHistoryResultSet.getString("ACTION"));
		etsDocHistory.setActionDetails(etsDocHistoryResultSet.getString("ACTION_DETAILS"));
		etsDocHistory.setLastTimestamp(etsDocHistoryResultSet.getTimestamp("LAST_TIMESTAMP"));
		
		return etsDocHistory;
	}	
	
	/**
	 * @param strProjectId
	 * @param bCheckAmtTables
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public Vector getProjMembersWithRole(String strProjectId, boolean bCheckAmtTables)
		throws SQLException {

		String strQuery = 
			"select distinct u.user_id, r.role_name, u.*, a.user_fname, a.user_lname " 
			+ "from ETS.ETS_USERS u, AMT.USERS a , ets.ets_roles r "
			+ "where u.user_project_id = ? "
		  	+ "and u.user_id = a.ir_userid "
			+ "and u.active_flag='A' "
		 	+ "and u.user_role_id = r.role_id order by user_fname with ur";

		PreparedStatement stmtProjMembers = m_pdConnection.prepareStatement(strQuery);
		stmtProjMembers.setString(1, strProjectId);
		ResultSet rsProjMembers = stmtProjMembers.executeQuery();

		Vector vtProjMembers = getUsersWithRole(rsProjMembers);
		rsProjMembers.close();
		stmtProjMembers.close();
		return vtProjMembers;
	}


	/**
	 * @param rsUsers
	 * @return
	 * @throws SQLException In case of database errors
	 */
	private Vector getUsersWithRole(ResultSet rsUsers) throws SQLException {
		Vector v = new Vector();

		while (rsUsers.next()) {
			ETSUser udUser = getUserWithRole(rsUsers);
			v.addElement(udUser);
		}
		return v;
	}

	/**
	 * @param rsUser
	 * @return
	 * @throws SQLException In case of database errors
	 */
	private ETSUser getUserWithRole(ResultSet rsUser) throws SQLException {
		ETSUser udUser = new ETSUser();

		udUser.setUserType(rsUser.getString("ROLE_NAME"));
		udUser.setUserId(rsUser.getString("USER_ID"));
		//m_pdLog.debug(user.getUserId());
		//spn 0312 projid
		udUser.setProjectId(rsUser.getString("USER_PROJECT_ID"));
		udUser.setRoleId(rsUser.getInt("USER_ROLE_ID"));
		udUser.setUserJob(rsUser.getString("USER_JOB"));
		udUser.setPrimaryContact(rsUser.getString("PRIMARY_CONTACT"));
		udUser.setLastUserId(rsUser.getString("LAST_USERID"));
		udUser.setLastTimestamp(rsUser.getTimestamp("LAST_TIMESTAMP"));
		udUser.setActiveFlag(rsUser.getString("ACTIVE_FLAG"));

		// Set the User Name as well. This will avoid unnecessary round-trips to
		// the database to get the Full Name
		try {
			udUser.setUserName(
				StringUtil.trim(rsUser.getString("USER_FNAME"))
					+ StringUtil.SPACE
					+ StringUtil.trim(rsUser.getString("USER_LNAME")));
		} catch (Exception e) {
			// DO NOTHING
		}

		return udUser;
	}
	
	/**
	 * @param strDocId
	 * @param strProjectID
	 * @return
	 * @throws SQLException
	 */
	public boolean hasAdditionalEditors(String strDocId, String strProjectID) throws SQLException {

		Vector vtResUsersEdit = getAllDocRestrictedEditUserIds( strDocId, strProjectID );
		List lstGroupsEdit = getAllDocRestrictedEditGroupIds( Integer.parseInt(strDocId) );
        if( !vtResUsersEdit.isEmpty() || !lstGroupsEdit.isEmpty() )
        	return true;
        return false;
	}
/* ********************************************* END ******************************************************* */
		
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
	    DocUpdateDAO udUpdateDAO = new DocUpdateDAO();
	    udUpdateDAO.setConnection(m_pdConnection);
	    return udUpdateDAO.addDocResUsers(vtUsers, strDocID, strProjectId);
	}

	/**
	 * @param iDocID
	 * @param strProjectId
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public ETSDoc getDocByIdAndProject(int iDocID, String strProjectId)
		throws SQLException {
		DocReaderDAO udReader = new DocReaderDAO();
		udReader.setConnection(m_pdConnection);

		ETSDoc udDoc =
			udReader.getDocByIdAndProject(iDocID, strProjectId, false);

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
		DocReaderDAO udReader = new DocReaderDAO();
		udReader.setConnection(m_pdConnection);

		ETSDoc udDoc =
			udReader.getDocByIdAndProject(iDocID, strProjectId, bIsITAR);

		return udDoc;
	}

	/**
	 * @param iDocID
	 * @param strProjectId
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public ETSDoc getITARDocByIdAndProject(int iDocID, String strProjectId)
		throws SQLException {
		Statement stmtDocDetails = m_pdConnection.createStatement();

		String strQuery =
			"select d.*, "
				+ "(select count(m.doc_id) from ets.ets_doc_metrics m "
				+ "where d.doc_id=m.doc_id) as hits "
				+ "from ETS.ETS_DOC d "
				+ "where d.doc_id = "
				+ iDocID
				+ " and d.project_id = '"
				+ strProjectId
				+ "' and delete_flag!='"
				+ DocConstants.TRUE_FLAG
				+ "' with ur";
		ResultSet rsDocDetails = stmtDocDetails.executeQuery(strQuery);

		ETSDoc udDoc = null;
		if (rsDocDetails.next()) {
			udDoc = getDoc(rsDocDetails);
		}

		rsDocDetails.close();
		stmtDocDetails.close();

		populateUserName(udDoc);
		try {
		    if (!StringUtil.isNullorEmpty(udDoc.getUpdatedBy())) {
			udDoc.setUpdatedBy(
				ETSUtils.getUsersName(m_pdConnection, udDoc.getUpdatedBy()));
		    }
		} catch (Exception e) {
			m_pdLog.error(e);
		}
		return udDoc;
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
	 * @param iDocID
	 * @param strProjectId
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public Vector getDocComments(int iDocID, String strProjectId, long lVersion)
		throws SQLException {
		Vector vtComments = new Vector();
		try {
			String strQuery =
				"select * from ets.ets_doc_comments where doc_id="
					+ iDocID
					+ " and project_id='"
					+ strProjectId
					+ "' order by last_timestamp desc WITH UR";

			Statement stmtComments = m_pdConnection.createStatement();
			ResultSet rsComments = stmtComments.executeQuery(strQuery);

			while (rsComments.next()) {
				ETSDocComment dc = new ETSDocComment();
				dc.setId(iDocID);
				dc.setProjectId(strProjectId);
				dc.setUserId(rsComments.getString("user_id"));
				dc.setComment(rsComments.getString("comment"));
				dc.setCommentDate(rsComments.getTimestamp("last_timestamp"));
				dc.setDocVersion(lVersion);
				vtComments.addElement(dc);
			}

			rsComments.close();
			stmtComments.close();

		} catch (SQLException e) {
			m_pdLog.error("sql error =" + DocUpdateDAO.getStackTrace(e));
			throw e;
		}

		return populateUserNames(vtComments);

	}

	/**
	 * @param iDocId
	 * @param strProjectId
	 * @param strSort
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public Vector getDocMetrics(
		int iDocId,
		String strProjectId,
		String strSort,
		DocExpirationDate dtStartDate,
		DocExpirationDate dtEndDate)
		throws SQLException {
		Vector vtDocMetrics = new Vector();

		try {
		    boolean bIsFiltered = false;
		    String strClause = "";
		    if (dtStartDate != null && dtEndDate != null) {
		        strClause = " and timestamp between ? and ? ";
		        bIsFiltered = true;
		    }
			PreparedStatement stmtDocMetrics;
			stmtDocMetrics =
				m_pdConnection.prepareStatement(
					"select m.* from ets.ets_doc_metrics m where "
						+ "m.project_id=? and m.doc_id=? " 
						+ strClause 
						+ "order by m.timestamp "
						+ strSort
						+ " with ur");

			stmtDocMetrics.setString(1, strProjectId);
			stmtDocMetrics.setInt(2, iDocId);
			if (bIsFiltered) {
				stmtDocMetrics.setDate(3, new java.sql.Date(dtStartDate.getDate()));
				stmtDocMetrics.setDate(4, new java.sql.Date(dtEndDate.getDate()));
			}

			ResultSet rsDocMetrics = stmtDocMetrics.executeQuery();

			while (rsDocMetrics.next()) {
				ETSUser udAccessUser = new ETSUser();
				udAccessUser.setUserId(rsDocMetrics.getString("user_id"));
				udAccessUser.setLastTimestamp(
					rsDocMetrics.getTimestamp("timestamp"));

				vtDocMetrics.addElement(udAccessUser);
			}

			rsDocMetrics.close();
			stmtDocMetrics.close();

			return populateUserNames(vtDocMetrics);
		} catch (SQLException e) {
			m_pdLog.error("sql error =" + DocUpdateDAO.getStackTrace(e));
			throw e;
		}
	}

	/**
	 * @param udObj
	 */
	private void populateUserName(ETSDetailedObj udObj) {
		try {
			udObj.setUserName(
				ETSUtils.getUsersName(m_pdConnection, udObj.getUserId()));
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
	 * @param vtUsers
	 * @return
	 */
	public Vector getIBMMembers(Vector vtUsers) {
		Vector vtIBMMembers = new Vector();

		if (vtUsers == null) {
			return new Vector();
		}
		for (int iCounter = 0; iCounter < vtUsers.size(); iCounter++) {
			ETSUser udMember = (ETSUser) vtUsers.elementAt(iCounter);
			try {
				String edge_userid =
					AccessCntrlFuncs.getEdgeUserId(
						m_pdConnection,
						udMember.getUserId());
				String decaftype =
					AccessCntrlFuncs.decafType(edge_userid, m_pdConnection);
				if (decaftype.equals("I")) {
					vtIBMMembers.addElement(udMember);
				}
			} catch (AMTException a) {
				m_pdLog.error("amt error=", a);
			} catch (SQLException s) {
				m_pdLog.error("sql error=", s);
			}
		}

		return vtIBMMembers;
	}

	/**
	 * @param vtUsers
	 * @return
	 */
	public Vector getIBMMembersWithNames(Vector vtUsers) {
		Vector vtIBMMembers = new Vector();

		if (vtUsers == null) {
			return new Vector();
		}
		for (int iCounter = 0; iCounter < vtUsers.size(); iCounter++) {
			ETSUser udMember = (ETSUser) vtUsers.elementAt(iCounter);
			try {
				String edge_userid =
					AccessCntrlFuncs.getEdgeUserId(
						m_pdConnection,
						udMember.getUserId());
				String decaftype =
					AccessCntrlFuncs.decafType(edge_userid, m_pdConnection);
				if (decaftype.equals("I")) {
					vtIBMMembers.addElement(udMember);
				}
			} catch (AMTException a) {
				m_pdLog.error("amt error=", a);
			} catch (SQLException s) {
				m_pdLog.error("sql error=", s);
			}
		}

		return populateUserNames( vtIBMMembers );
	}

	/**
	 * @param udComment
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public boolean addDocComment(ETSDocComment udComment) throws SQLException {
		boolean bSuccess = false;

		try {
			PreparedStatement stmtAddComment;
			stmtAddComment =
				m_pdConnection.prepareStatement(
					"insert into ets.ets_doc_comments("
						+ "doc_id,project_id,comment,user_id,last_timestamp)"
						+ "values (?,?,?,?,current timestamp)");

			stmtAddComment.setInt(1, udComment.getId());
			stmtAddComment.setString(2, udComment.getProjectId());
			stmtAddComment.setString(3, udComment.getComment());
			stmtAddComment.setString(4, udComment.getUserId());

			int iRowCount = stmtAddComment.executeUpdate();
			stmtAddComment.close();

			if (iRowCount == 1) {
				bSuccess = true;
			}

		} catch (SQLException e) {
			m_pdLog.error("sql error =" + DocUpdateDAO.getStackTrace(e));
			throw e;
		}

		return bSuccess;

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
		DocUpdateDAO udUpdateDAO = new DocUpdateDAO();
		udUpdateDAO.setConnection(m_pdConnection);
		return udUpdateDAO.addEmailLog(strMailType, strKey1, strKey2, strKey3, strProjectId, strSubject, strToList, strCCList);
	}

	/**
	 * @param udDoc
	 * @param bChangeIbm
	 * @param bChangeRes
	 * @param vtAllAddResUsers
	 * @param strAdd
	 * @param strRemove
	 * @param vtAdd
	 * @param bIsNewUpload
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public synchronized boolean updateDocProp(
		ETSDoc udDoc,
		boolean bChangeIbm,
		boolean bChangeRes,
		Vector vtAllAddResUsers,
		String strAdd,
		String strAddGroups,
		String strRemove,
		String strRemoveGroups,
		Vector vtAdd,
		Vector vtAddGroups,
		boolean bIsNewUpload)
		throws SQLException {
		PreparedStatement upDocSt = null;

		if (bIsNewUpload) {
			upDocSt =
				m_pdConnection.prepareStatement(
					"update ets.ets_doc set project_id=?,cat_id=?,"
						+ "doc_name=?,doc_description=?,doc_keywords=?,"
						+ "ibm_only=?,updated_by=?,"
						+ "doc_update_date=doc_upload_date,expiry_date=?,"
						+ "isprivate=?,ibm_conf=? where doc_id=?");
		} else {
			upDocSt =
				m_pdConnection.prepareStatement(
					"update ets.ets_doc set project_id=?,cat_id=?,"
						+ "doc_name=?,doc_description=?,doc_keywords=?,"
						+ "ibm_only=?,updated_by=?,"
						+ "doc_update_date=current timestamp,expiry_date=?,"
						+ "isprivate=?,ibm_conf=? where doc_id=?");
		}

		try {
			//spn 0312 projid
			upDocSt.setString(1, udDoc.getProjectId());
			upDocSt.setInt(2, udDoc.getCatId());
			//upDocSt.setString(3, udDoc.getUserId());
			upDocSt.setString(3, udDoc.getName());
			upDocSt.setString(4, udDoc.getDescription());
			upDocSt.setString(5, udDoc.getKeywords());
			upDocSt.setString(6, String.valueOf(udDoc.getIbmOnly()));
			upDocSt.setString(7, udDoc.getUpdatedBy());
			//FIX FIX FIX FIX EXPIRE DATE
			if (udDoc.getExpiryDate() == 0)
				upDocSt.setTimestamp(8, null);
			else
				upDocSt.setTimestamp(8, new Timestamp(udDoc.getExpiryDate()));

			upDocSt.setString(9, udDoc.getDPrivate());
			upDocSt.setString(10, udDoc.getIBMConfidential());
			upDocSt.setInt(11, udDoc.getId());

			upDocSt.executeUpdate();
			upDocSt.close();
			addContentLog(
				udDoc.getId(),
				'D',
				udDoc.getProjectId(),
				Defines.UPDATE_DOC_PROP,
				udDoc.getUserId());

			if (!bIsNewUpload && (bChangeRes || udDoc.IsDPrivate())) {

				updateDocResUsers(
					udDoc,
					strAdd,
					strAddGroups,
					strRemove,
					strRemoveGroups);
			}

			updateDocPropPrev(
				udDoc,
				bChangeRes,
				strRemove,
				strRemoveGroups,
				vtAdd,
				vtAddGroups);

			return true;
		} catch (SQLException se) {
			m_pdLog.error("sql error =" + se);
			throw se;
		} catch (Exception e) {
			m_pdLog.error("exception error =" + e);
			return false;
		}
	}

	/**
	 * @param udDoc
	 * @param bChangeRes
	 * @param strRemove
	 * @param vtAdd
	 * @return
	 * @throws SQLException In case of database errors
	 * @throws Exception
	 */
	private synchronized boolean updateDocPropPrev(
		ETSDoc udDoc,
		boolean bChangeRes,
		String strRemove,
		String strRemoveGroups,
		Vector vtAdd,
		Vector vtAddGroups)
		throws SQLException, Exception {
		Vector vtPrev = getPreviousVersions(udDoc.getId(), 0);
		String strDocIds = StringUtil.EMPTY_STRING;
		for (int iCounter = 0; iCounter < vtPrev.size(); iCounter++) {
			ETSDoc udDocTmp = (ETSDoc) vtPrev.elementAt(iCounter);
			strDocIds = strDocIds + udDocTmp.getId() + StringUtil.COMMA;
		}

		if (!strDocIds.equals(StringUtil.EMPTY_STRING)) {
			strDocIds = strDocIds.substring(0, strDocIds.length() - 1);
			updateDocIbmOnlyPrivate(
				udDoc.getIbmOnly(),
				udDoc.getDPrivate(),
				strDocIds);

			if (bChangeRes || udDoc.IsDPrivate()) {
				updateDocPrevResUsers(
					udDoc.getId(),
					vtPrev,
					strDocIds,
					udDoc.getProjectId(),
					bChangeRes,
					strRemove,
					strRemoveGroups,
					vtAdd,
					vtAddGroups);
			}
		}

		return true;
	}

	/**
	 * @param iDocID
	 * @param vtPrevDocs
	 * @param strDocIDs
	 * @param strProjectID
	 * @param bChangeRes
	 * @param strRemove
	 * @param vtAdd
	 * @return
	 * @throws SQLException In case of database errors
	 * @throws Exception
	 */
	private synchronized boolean updateDocPrevResUsers(
		int iDocID,
		Vector vtPrevDocs,
		String strDocIDs,
		String strProjectID,
		boolean bChangeRes,
		String strRemove,
		String strRemoveGroups,
		Vector vtAdd,
		Vector vtAddGroups)
		throws SQLException, Exception {
		String strQuery = StringUtil.EMPTY_STRING;
		Statement stmtUpdate = m_pdConnection.createStatement();
		String strPrevAdd = StringUtil.EMPTY_STRING;

		if (vtAdd.size() > 0 && vtPrevDocs.size() > 0) {
			for (int iCounter = 0; iCounter < vtPrevDocs.size(); iCounter++) {
				if (((ETSDoc) vtPrevDocs.elementAt(iCounter)).getId() != iDocID) {
					for (int iInner = 0; iInner < vtAdd.size(); iInner++) {
						if (!strPrevAdd.equals(StringUtil.EMPTY_STRING))
							strPrevAdd =
								strPrevAdd
									+ StringUtil.COMMA
									+ "("
									+ ((ETSDoc) vtPrevDocs.elementAt(iCounter))
										.getId()
									+ StringUtil.COMMA
									+ "'"
									+ vtAdd.elementAt(iInner)
									+ "'"
									+ StringUtil.COMMA
									+ "'"
									+ strProjectID
									+ "'"+ StringUtil.COMMA +"'" 
									+ Defines.DOC_READ_ACCESS + "')" ;
						else
							strPrevAdd =
								"("
									+ ((ETSDoc) vtPrevDocs.elementAt(iCounter))
										.getId()
									+ StringUtil.COMMA
									+ "'"
									+ vtAdd.elementAt(iInner)
									+ "'"
									+ StringUtil.COMMA
									+ "'"
									+ strProjectID
									+ "'"+ StringUtil.COMMA +"'" 
									+ Defines.DOC_READ_ACCESS + "')" ;
					}
				}

			}
			if (!strPrevAdd.equals(StringUtil.EMPTY_STRING)) {
				strQuery =
					"insert into ets.ets_private_doc"
						+ "(doc_id,user_id,project_id, ACCESS_TYPE)"
						+ " values "
						+ strPrevAdd;
				m_pdLog.error("SQL IS : " + strQuery);
				stmtUpdate.executeUpdate(strQuery);
			}
		}
		if (!strRemove.equals(StringUtil.EMPTY_STRING)) {
			strQuery =
				"delete from ets.ets_private_doc"
					+ " where doc_id in ("
					+ strDocIDs
					+ ")"
					+ " and project_id = '"
					+ strProjectID
					+ "'"
					+ " and user_id in ("
					+ strRemove
					+ ") AND ACCESS_TYPE = '"+ Defines.DOC_READ_ACCESS +"'";
			m_pdLog.error("SQL IS : " + strQuery);
			stmtUpdate.executeUpdate(strQuery);
		}

		strQuery = StringUtil.EMPTY_STRING;
		strPrevAdd = StringUtil.EMPTY_STRING;

		// Add and Remove for Groups
		if (vtAddGroups.size() > 0 && vtPrevDocs.size() > 0) {
			for (int iCounter = 0; iCounter < vtPrevDocs.size(); iCounter++) {
				if (((ETSDoc) vtPrevDocs.elementAt(iCounter)).getId()
					!= iDocID) {
					for (int iInner = 0;
						iInner < vtAddGroups.size();
						iInner++) {
						if (!strPrevAdd.equals(StringUtil.EMPTY_STRING))
							strPrevAdd =
								strPrevAdd
									+ StringUtil.COMMA
									+ "("
									+ ((ETSDoc) vtPrevDocs.elementAt(iCounter))
										.getId()
									+ StringUtil.COMMA
									+ "'"
									+ vtAddGroups.elementAt(iInner)
									+ "'"
									+ StringUtil.COMMA
									+ "'"
									+ strProjectID
									+ "'"+ StringUtil.COMMA +"'" 
									+ Defines.DOC_READ_ACCESS + "')" ;
						else
							strPrevAdd =
								"("
									+ ((ETSDoc) vtPrevDocs.elementAt(iCounter))
										.getId()
									+ StringUtil.COMMA
									+ "'"
									+ vtAddGroups.elementAt(iInner)
									+ "'"
									+ StringUtil.COMMA
									+ "'"
									+ strProjectID
									+ "'"+ StringUtil.COMMA +"'" 
									+ Defines.DOC_READ_ACCESS + "')" ;
					}
				}
			}
			if (!strPrevAdd.equals(StringUtil.EMPTY_STRING)) {
				strQuery =
					"insert into ets.ets_private_doc"
						+ "(doc_id,group_id,project_id, ACCESS_TYPE)"
						+ " values "
						+ strPrevAdd;
				m_pdLog.error("SQL IS : " + strQuery);
				stmtUpdate.executeUpdate(strQuery);
			}
		}
		if (!strRemoveGroups.equals(StringUtil.EMPTY_STRING)) {
			strQuery =
				"delete from ets.ets_private_doc"
					+ " where doc_id in ("
					+ strDocIDs
					+ ")"
					+ " and project_id = '"
					+ strProjectID
					+ "'"
					+ " and group_id in ("
					+ strRemoveGroups
					+ ") AND ACCESS_TYPE = '"+ Defines.DOC_READ_ACCESS +"'";
			m_pdLog.error("SQL IS : " + strQuery);
			stmtUpdate.executeUpdate(strQuery);
		}

		stmtUpdate.close();

		return true;
	}

	/**
	 * @param udDoc
	 * @param strAdd
	 * @param strRemove
	 * @return
	 * @throws SQLException In case of database errors
	 * @throws Exception
	 */
	private synchronized boolean updateDocResUsers(
		ETSDoc udDoc,
		String strAdd,
		String strAddGroups,
		String strRemove,
		String strRemoveGroups)
		throws SQLException, Exception {
		String strQuery = StringUtil.EMPTY_STRING;
		Statement stmtUpdDocResUsers = m_pdConnection.createStatement();

		if ((!strAdd.equals(StringUtil.EMPTY_STRING)) && udDoc.IsDPrivate()) {
			strQuery =
				"insert into ets.ets_private_doc(doc_id,user_id,project_id, ACCESS_TYPE) "
					+ "values "
					+ strAdd ;
			System.out.println("SQL :: "+ strQuery);			
			stmtUpdDocResUsers.executeUpdate(strQuery);
		}

		if ((!strAddGroups.equals(StringUtil.EMPTY_STRING))
			&& udDoc.IsDPrivate()) {
			strQuery =
				"insert into ets.ets_private_doc(doc_id,group_id,project_id, ACCESS_TYPE) "
					+ "values "
					+ strAddGroups ;
			System.out.println("SQL :: "+ strQuery);			
			stmtUpdDocResUsers.executeUpdate(strQuery);
		}

		if (!strRemove.equals(StringUtil.EMPTY_STRING)) {
			strQuery =
				"delete from ets.ets_private_doc"
					+ " where doc_id = "
					+ udDoc.getId()
					+ " and project_id = "
					+ "'" + udDoc.getProjectId() + "'"
					+ " and user_id in ("
					+ strRemove + ") AND ACCESS_TYPE = '"
					+ Defines.DOC_READ_ACCESS +"'";
			System.out.println("SQL :: "+ strQuery);			
			stmtUpdDocResUsers.executeUpdate(strQuery);
		}

		if (!strRemoveGroups.equals(StringUtil.EMPTY_STRING)) {
			// Do the same for Groups
			strQuery =
				"delete from ets.ets_private_doc"
					+ " where doc_id = "
					+ udDoc.getId()
					+ " and project_id = "
					+ "'" + udDoc.getProjectId() + "'"
					+ " and group_id in ("
					+ strRemoveGroups + ") AND ACCESS_TYPE = '"
					+ Defines.DOC_READ_ACCESS +"'";
			System.out.println("SQL :: "+ strQuery);			
			stmtUpdDocResUsers.executeUpdate(strQuery);
		}

		stmtUpdDocResUsers.close();

		return true;
	}

	/**
	 * @param cIBMOnly
	 * @param strPrivate
	 * @param strDocIDs
	 * @throws SQLException In case of database errors
	 * @throws Exception
	 */
	private synchronized void updateDocIbmOnlyPrivate(
		char cIBMOnly,
		String strPrivate,
		String strDocIDs)
		throws SQLException, Exception {
		PreparedStatement upDocSt =
			m_pdConnection.prepareStatement(
				"update ets.ets_doc set ibm_only=?,isprivate=? "
					+ "where doc_id in ("
					+ strDocIDs
					+ ")");

		try {
			upDocSt.setString(1, String.valueOf(cIBMOnly));
			upDocSt.setString(2, strPrivate);

			upDocSt.executeUpdate();
			upDocSt.close();
		} catch (SQLException se) {
			m_pdLog.error("sql error in updatedocibmonly= " + se);
			throw se;
		}
	}

	/**
	 * @param iDocID
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public Vector getDocNotifyList(int iDocID) throws SQLException {

		DocReaderDAO udReaderDAO = new DocReaderDAO();
		udReaderDAO.setConnection(m_pdConnection);

		return udReaderDAO.getDocNotifyList(iDocID);
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
		DocUpdateDAO udUpdateDAO = new DocUpdateDAO();
		udUpdateDAO.setConnection(m_pdConnection);
		udUpdateDAO.addDocNotificationList(iDocID, bIsNotifyAll, vtNotifyUsers, strGroups);
	}

	/**
	 * @param udDoc
	 * @param bChangeIBM
	 * @param bChangeRes
	 * @param bAddAllRes
	 * @param strAdd
	 * @param strRemove
	 * @param vtAdd
	 * @return
	 * @throws SQLException In case of database errors
	 * @throws Exception
	 */
	public synchronized boolean updateDocProp(
		ETSDoc udDoc,
		boolean bChangeIBM,
		boolean bChangeRes,
		Vector bAddAllRes,
		String strAdd,
		String strAddGroups,
		String strRemove,
		String strRemoveGroups,
		Vector vtAdd,
		Vector vtAddGroups)
		throws SQLException, Exception {
		boolean bSuccess = false;
		try {
			if (bChangeIBM || (bChangeRes || udDoc.IsDPrivate())) {
				bSuccess =
					updateDocProp(
						udDoc,
						bChangeIBM,
						bChangeRes,
						bAddAllRes,
						strAdd,
						strAddGroups,
						strRemove,
						strRemoveGroups,
						vtAdd,
						vtAddGroups,
						false);
			} else {
				bSuccess = updateDocProp(udDoc);
			}
		} catch (SQLException e) {
			m_pdLog.error("sql error=", e);
			throw e;
		} catch (Exception ex) {
			m_pdLog.error("error=", ex);
			throw ex;
		}
			return bSuccess;
	}

	/**
	 * @param udDoc
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public synchronized boolean updateDocProp(ETSDoc udDoc)
		throws SQLException {
	    DocUpdateDAO udUpdateDAO = new DocUpdateDAO();
	    udUpdateDAO.setConnection(m_pdConnection);
	    return udUpdateDAO.updateDocProp(udDoc);
	}

	/**
	 * @param iDocId
	 * @param strStatus
	 * @throws SQLException
	 */
	public synchronized void updateDocStatus(int iDocId, String strStatus)
		throws SQLException {
		PreparedStatement upDocSt = null;

		upDocSt =
			m_pdConnection.prepareStatement(
				"UPDATE ETS.ETS_DOC SET ITAR_UPLOAD_STATUS=? WHERE DOC_ID=?");
		try {
			upDocSt.setString(1, strStatus);
			upDocSt.setInt(2, iDocId);

			upDocSt.executeUpdate();
			upDocSt.close();
		} catch (SQLException se) {
			m_pdLog.error("sql error =" + se);
			throw se;
		}
	}

	/**
	 * @param iDocId
	 * @param strStatus
	 * @throws SQLException
	 */
	public synchronized void deleteITARDocument(int iDocId)
		throws SQLException {
		PreparedStatement upDocSt = null;

		upDocSt =
			m_pdConnection.prepareStatement(
				"DELETE FROM ETS.ETS_DOC WHERE DOC_ID=?");
		try {
			upDocSt.setInt(1, iDocId);

			upDocSt.executeUpdate();
			upDocSt.close();
		} catch (SQLException se) {
			m_pdLog.error("sql error =" + se);
			throw se;
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
					+ "AND PROJ_ID = ? "
					+ "AND ITAR_UPLOAD_STATUS=?");
		try {
			upDocSt.setString(1, strStatus);
			upDocSt.setInt(2, iDocId);
			upDocSt.setString(3, strProjectId);
			upDocSt.setString(4, DocConstants.ITAR_STATUS_PENDING);

			upDocSt.executeUpdate();
			upDocSt.close();
		} catch (SQLException se) {
			m_pdLog.error("sql error =" + se);
			throw se;
		}
	}

	/**
	 * @param sProjectId
	 * @param userid
	 * @param isExec
	 * @param iMaxRows
	 * @return
	 * @throws SQLException
	 */
	public Vector getLatestDocs(
		String sProjectId,
		String userid,
		boolean isExec,
		int iMaxRows)
		throws SQLException {

		if (iMaxRows == 0) {
			iMaxRows = 5;
		}

		java.util.Date today = new java.util.Date();
		java.sql.Date sqlToday = new java.sql.Date(today.getTime());

		String privDoc = "";
		if (!isExec) {
			   privDoc = "and (d.isprivate!='1' or d.isprivate is null "
						// add check for all users group
						+ " or '" + Defines.GRP_ALL_USERS + "' in"
						+ " (select name from ets.ets_private_doc pd, ets.groups grp where pd.doc_id = d.doc_id and grp.group_id = pd.group_id)"
					     + " or d.user_id='" + userid + "' or ('" + userid
					     + "' in ((select dp.user_id from ets.ets_private_doc dp "
					     + "where dp.doc_id=d.doc_id) "
					     + " union "
					     + "(select u.user_id from ets.user_groups u where group_id in "
					     + "(select group_id from ets.ets_private_doc pd where pd.doc_id=d.doc_id)) )))";
			 
		  }

		String strQuery = 	"select d.*, "
							+ "(select count(m.doc_id) from ets.ets_doc_metrics m "
							+ "where d.doc_id=m.doc_id) as hits "
							+ "from ETS.ETS_DOC d where d.project_id = ? "
							+ "and d.latest_version= '"
							+ DocConstants.TRUE_FLAG
							+ "' and itar_upload_status !='P'"
							+ " and delete_flag!='"
							+ DocConstants.TRUE_FLAG
							+ "' "
							+ "and ((date(d.expiry_date)>date('"
							+ sqlToday
							+ "')) or (d.user_id=?) or d.expiry_date=? "
							+ "or d.expiry_date is null) "
							+ privDoc
							+ "and doc_type="
							+ Defines.DOC
							+ " order by d.doc_upload_date desc fetch first "
							+ iMaxRows
							+ " rows only with ur";

		PreparedStatement getDocsSt = m_pdConnection.prepareStatement(strQuery);

		getDocsSt.setString(1, sProjectId);
		getDocsSt.setString(2, userid);
		getDocsSt.setTimestamp(3, new Timestamp(0));

		ResultSet rs = getDocsSt.executeQuery();

		Vector docs = null;
		docs = getDocs(rs);

		rs.close();
		getDocsSt.close();
		return docs;

	}

	/**
	 * @param sProjectId
	 * @param iMaxRows
	 * @return
	 * @throws SQLException
	 */
	public Vector getLatestDocs(String sProjectId, int iMaxRows)
		throws SQLException {

		if (iMaxRows == 0) {
			iMaxRows = 5;
		}

		PreparedStatement getDocsSt =
			m_pdConnection.prepareStatement(
				"select d.*, "
					+ "(select count(m.doc_id) from ets.ets_doc_metrics m "
					+ "where d.doc_id=m.doc_id) as hits "
					+ "from ETS.ETS_DOC d where d.project_id = ? "
					+ "and d.latest_version= '"
					+ DocConstants.TRUE_FLAG
					+ "' and itar_upload_status !='P'"
					+ " and delete_flag!='"
					+ DocConstants.TRUE_FLAG
					+ "' "
					+ "and doc_type="
					+ Defines.DOC
					+ " order by d.doc_upload_date desc fetch first "
					+ iMaxRows
					+ " rows only with ur");

		getDocsSt.setString(1, sProjectId);

		ResultSet rs = getDocsSt.executeQuery();

		Vector docs = null;
		docs = getDocs(rs);

		rs.close();
		getDocsSt.close();
		return docs;

	}

	/**
	 * @param sProjectId
	 * @param userid
	 * @param iMaxRows
	 * @param isInternal
	 * @param isExec
	 * @return
	 * @throws SQLException
	 */
	public Vector getLatestDocs(
		String sProjectId,
		String userid,
		int iMaxRows,
		boolean isInternal,
		boolean isExec)
		throws SQLException {

		if (iMaxRows == 0) {
			iMaxRows = 5;
		}

		java.util.Date today = new java.util.Date();
		java.sql.Date sqlToday = new java.sql.Date(today.getTime());

		System.out.println("DATE=" + sqlToday);

		String privDoc = "";
		if (!isExec) {
			   privDoc = "and (d.isprivate!='1' or d.isprivate is null"
						// add check for all users group
						+ " or '" + Defines.GRP_ALL_USERS + "' in"
						+ " (select name from ets.ets_private_doc pd, ets.groups grp where pd.doc_id = d.doc_id and grp.group_id = pd.group_id)"
					     + " or d.user_id='" + userid + "' or ('" + userid
					     + "' in ((select dp.user_id from ets.ets_private_doc dp "
					     + "where dp.doc_id=d.doc_id) "
					     + " union "
					     + "(select u.user_id from ets.user_groups u where group_id in "
					     + "(select group_id from ets.ets_private_doc pd where pd.doc_id=d.doc_id)) )))";
			 
		  }

		String strQuery =  	"select d.*, "
							+ "(select count(m.doc_id) from ets.ets_doc_metrics m "
							+ "where d.doc_id=m.doc_id) as hits from ETS.ETS_DOC d "
							+ "where d.project_id = ? and d.latest_version= '"
							+ DocConstants.TRUE_FLAG
							+ "' "
							+ "and ibm_only='"
							+ Defines.ETS_PUBLIC
							+ "' and itar_upload_status !='P'"
							+ " and delete_flag!='"
							+ DocConstants.TRUE_FLAG
							+ "' "
							+ "and ((date(d.expiry_date)>date('"
							+ sqlToday
							+ "')) or (d.user_id=?) or date(d.expiry_date)=? "
							+ "or d.expiry_date is null) "
							+ privDoc
							+ "and doc_type="
							+ Defines.DOC
							+ " order by d.doc_upload_date desc fetch first "
							+ iMaxRows
							+ " rows only with ur";

		PreparedStatement getDocsSt = m_pdConnection.prepareStatement( strQuery );

		getDocsSt.setString(1, sProjectId);
		getDocsSt.setString(2, userid);
		getDocsSt.setDate(3, new Date((new Timestamp(0)).getTime()));

		ResultSet rs = getDocsSt.executeQuery();

		Vector docs = null;
		docs = getDocs(rs);

		rs.close();
		getDocsSt.close();
		return docs;

	}

	/**
	 * @param projectId
	 * @return
	 * @throws SQLException
	 */
	public EtsPrimaryContactInfo getProjContactInfo(String projectId)
		throws SQLException {

		ArrayList contList = new ArrayList();
		StringBuffer sb = new StringBuffer();
		EtsPrimaryContactInfo etsContInfo = new EtsPrimaryContactInfo();

		Statement stmt = null;
		ResultSet rs = null;

		sb.append("select a.ir_userid as iruserid,");
		sb.append("a.user_fullname as userfullname,");
		sb.append("a.user_email as useremail,a.user_phone as userphone");
		sb.append("   from amt.users a, ets.ets_users b ");
		sb.append("   where ");
		sb.append("   a.ir_userid=b.user_id");
		sb.append("   and   b.primary_contact='Y' ");
		sb.append("   and   b.user_project_id='" + projectId + "'");
		sb.append("  with ur");

		stmt = m_pdConnection.createStatement();
		rs = stmt.executeQuery(sb.toString());

		if (rs != null) {
			while (rs.next()) {

				etsContInfo.setUserIrId(
					AmtCommonUtils.getTrimStr(rs.getString("IRUSERID")));
				etsContInfo.setUserFullName(
					AmtCommonUtils.getTrimStr(rs.getString("USERFULLNAME")));
				etsContInfo.setUserEmail(
					AmtCommonUtils.getTrimStr(rs.getString("USEREMAIL")));
				etsContInfo.setUserContPhone(
					AmtCommonUtils.getTrimStr(rs.getString("USERPHONE")));

			}

		}

		rs.close();
		stmt.close();
		return etsContInfo;

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

	    DocReaderDAO udReaderDAO = new DocReaderDAO();
		udReaderDAO.setConnection(m_pdConnection);
		return udReaderDAO.getDocByNameAndCat(iParentId, strName, iDocId);
	}

	/**
	 * @param strProjectId
	 * @param iViewType
	 * @return
	 * @throws SQLException
	 */
	public int getTopCatId(String strProjectId, int iViewType)
		throws SQLException {
		
		DocReaderDAO udReaderDAO = new DocReaderDAO();
		udReaderDAO.setConnection(m_pdConnection);
		return udReaderDAO.getTopCatId(strProjectId, iViewType);
	}

	/**
	 * @param iDocId
	 * @param strProjectId
	 * @param strUserName
	 */
	public void logHit(int iDocId, String strProjectId, String strUserName) {

		try {
			PreparedStatement stmtHit =
				m_pdConnection.prepareStatement(
					"insert into ets.ets_doc_metrics("
						+ "project_id,doc_id,user_id,timestamp) "
						+ "values(?,?,?,current timestamp)");

			stmtHit.setString(1, strProjectId);
			stmtHit.setInt(2, iDocId);
			stmtHit.setString(3, strUserName);

			int iCount = stmtHit.executeUpdate();
			stmtHit.close();
		} catch (SQLException e) {
			m_pdLog.error("sql error =" + e);
		}
	}

	/**
	 * @param strUserId
	 * @param bIsDateSpecified
	 * @param udStartDate
	 * @param udEndDate
	 * @param strProjectId
	 * @param strSortBy
	 * @param strSortOrder
	 * @return List of Metrics
	 */
	public List searchAccessHistory(
		String strUserId,
		boolean bIsDateSpecified,
		DocExpirationDate udStartDate,
		DocExpirationDate udEndDate,
		String strProjectId,
		String strSortBy,
		String strSortOrder)
		throws SQLException {

		String strSortParams = StringUtil.EMPTY_STRING;
		if (!StringUtil.isNullorEmpty(strSortBy)
			&& !StringUtil.isNullorEmpty(strSortOrder)) {
			strSortParams = "order by ";

			if (strSortBy.equals(Defines.SORT_BY_NAME_STR)) {
				strSortParams = strSortParams + "UPPER(doc.doc_name)";
			}
			if (strSortBy.equals(Defines.SORT_BY_DATE_STR)) {
				strSortParams = strSortParams + "metrics.timestamp";
			}

			strSortParams = strSortParams + " " + strSortOrder;
		}
		PreparedStatement stmtAccess = null;
		if (bIsDateSpecified) {
			stmtAccess =
				m_pdConnection.prepareStatement(
					"select doc.doc_id, doc.doc_name, metrics.timestamp ts from "
						+ "ets.ets_doc_metrics metrics, ets.ets_doc doc "
						+ "where metrics.user_id = ? and timestamp "
						+ "between ? and ? "
						+ "and doc.doc_id = metrics.doc_id "
						+ "and doc.project_id = ?"
						+ strSortParams
						+ " with UR");
			stmtAccess.setString(1, strUserId);
			stmtAccess.setDate(2, new java.sql.Date(udStartDate.getDate()));
			stmtAccess.setDate(3, new java.sql.Date(udEndDate.getNextDate()));
			stmtAccess.setString(4, strProjectId);
		} else {
			stmtAccess =
				m_pdConnection.prepareStatement(
					"select doc.doc_id, doc.doc_name, metrics.timestamp ts from "
						+ "ets.ets_doc_metrics metrics, ets.ets_doc doc "
						+ "where metrics.user_id = ? "
						+ "and doc.doc_id = metrics.doc_id "
						+ "and doc.project_id = ?"
						+ strSortParams
						+ " with UR");
			stmtAccess.setString(1, strUserId);
			stmtAccess.setString(2, strProjectId);
		}
		ResultSet rsResults = stmtAccess.executeQuery();
		List lstHistory = new ArrayList();
		while (rsResults.next()) {
			DocMetrics udDocMetrics =
				new DocMetrics(
					rsResults.getInt("doc_id"),
					rsResults.getString("doc_name"),
					rsResults.getTimestamp("ts"));
			lstHistory.add(udDocMetrics);
		}
		rsResults.close();
		stmtAccess.close();
		return lstHistory;
	}

	/**
	 * @param strUserId
	 * @param iDocId
	 * @return
	 * @throws SQLException
	 */
	public boolean isUserInNotificationList(String strUserId, int iDocId)
		throws SQLException {
		boolean bIsUserPresent = false;
		Vector vtNotificationList = getDocNotifyList(iDocId);
		if (vtNotificationList.size() == 0) {
			bIsUserPresent = false;
		} else {
			DocNotify udDocNotify = (DocNotify) vtNotificationList.get(0);
			if (udDocNotify.isNotifyAll()) {
				bIsUserPresent = true;
			} else {
				Vector vtUserList = getUserList(vtNotificationList);
				for (int iCounter = 0;
					iCounter < vtUserList.size();
					iCounter++) {
					udDocNotify = (DocNotify) vtUserList.get(iCounter);
					if (strUserId.equals(udDocNotify.getUserId())) {
						bIsUserPresent = true;
						break;
					}
				}
			}
		}
		PreparedStatement stmtNotify = null;

		// We need to check if user is present in any Group which has been
		// selected for notification. We don't need to go thru this whole 
		// check if we already know that the user is present in the user list
		if (!bIsUserPresent) {
			Vector vtGroupList = getGroupList(vtNotificationList);
			if (vtGroupList != null && vtGroupList.size() > 0) {
				StringBuffer strGroups =
					new StringBuffer(StringUtil.EMPTY_STRING);
				for (int iCounter = 0;
					iCounter < vtGroupList.size();
					iCounter++) {
					strGroups.append("'");
					strGroups.append(
						((DocNotify) vtGroupList.get(iCounter)).getGroupId());
					strGroups.append("'");
					if (iCounter < vtGroupList.size() - 1) {
						strGroups.append(StringUtil.COMMA);
					}
				}
				String strQuery =
					"SELECT * FROM ETS.USER_GROUPS "
						+ "WHERE USER_ID = ? AND GROUP_ID IN ("
						+ strGroups.toString()
						+ ") WITH UR";
				stmtNotify = m_pdConnection.prepareStatement(strQuery);
				stmtNotify.setString(1, strUserId);
				ResultSet rsResults = stmtNotify.executeQuery();
				if (rsResults.next()) {
					bIsUserPresent = true;
				}
				rsResults.close();
				stmtNotify.close();
			}
		}

		if (!bIsUserPresent) {
			// We don't need to check unsubscribe list
			return bIsUserPresent;
		}

		String strQuery =
			"SELECT * FROM ETS.DOC_UNSUBSCRIBE WHERE "
				+ "DOC_ID = ? AND USER_ID = ? WITH UR";

		stmtNotify = m_pdConnection.prepareStatement(strQuery);

		stmtNotify.setInt(1, iDocId);
		stmtNotify.setString(2, strUserId);

		ResultSet rsResults = stmtNotify.executeQuery();

		if (rsResults.next()) {
			bIsUserPresent = false;
		}

		rsResults.close();
		stmtNotify.close();

		return bIsUserPresent;
	}

	/**
	 * @param strProjectId
	 * @param strUserId
	 * @param iDocId
	 * @param bSubscribe
	 * @throws SQLException
	 */
	public void addRemoveNotification(
		String strProjectId,
		String strUserId,
		int iDocId,
		boolean bSubscribe)
		throws SQLException {
		String strQuery = null;
		if (bSubscribe) {
			Vector vtNotificationList = getDocNotifyList(iDocId);
			if (vtNotificationList.size() == 0) {
				// Means notification list does not exist for this doc
			strQuery =
				"INSERT INTO ETS.ETS_DOC_NOTIFY "
					+ "(DOC_ID, NOTIFY_ALL_FLAG, USER_ID) VALUES (?,'N',?)";

				PreparedStatement stmtNotify =
					m_pdConnection.prepareStatement(strQuery);

				stmtNotify.setInt(1, iDocId);
				stmtNotify.setString(2, strUserId);

				stmtNotify.executeUpdate();
				stmtNotify.close();
			} else {
				// Check if this doc has notify all.
				DocNotify udNotify = (DocNotify) vtNotificationList.get(0);
				if (udNotify.isNotifyAll()) {
					// If this ID already exists in DOC_UNSUBSCRIBE table, remove it.
		} else {
					// If user is part of a group which is already subscribed
					// to this document then do nothing here.
					boolean bUserExistsInGroup = false;
					boolean bUserAlreadyExists = false;
					Vector vtGrpList = getGroupList(vtNotificationList);
					if (vtGrpList != null && vtGrpList.size() > 0) {
						List lstUserGroups =
							getUserGroups(strUserId, strProjectId);
						// Check if any of the user groups is in the 
						// notifcation groups
						for (int iCount = 0;
							iCount < lstUserGroups.size();
							iCount++) {
							String strGroupId =
								((Group) lstUserGroups.get(iCount))
									.getGroupId();
							for (int iGrpCount = 0;
								iGrpCount < vtGrpList.size();
								iGrpCount++) {
								DocNotify udDocNotify =
									(DocNotify) vtGrpList.get(iGrpCount);
								if (strGroupId
									.equals(udDocNotify.getGroupId())) {
									bUserExistsInGroup = true;
									break;
								}
							}
						}
					}
					if (!bUserExistsInGroup) {
			strQuery =
						"UPDATE ETS.ETS_DOC_NOTIFY "
							+ "SET USER_ID = ? WHERE DOC_ID = ?";
					PreparedStatement stmtNotify =
						m_pdConnection.prepareStatement(strQuery);

						Vector vtUserList = getUserList(vtNotificationList);
					StringBuffer strBuffer =
						new StringBuffer(StringUtil.EMPTY_STRING);
					for (int iCounter = 0;
							iCounter < vtUserList.size();
						iCounter++) {
						String strUserID =
								((DocNotify) vtUserList.get(iCounter))
								.getUserId();
						if (strUserID.equals(strUserId)) {
							bUserAlreadyExists = true;
						}
						strBuffer.append(strUserID);
							if (iCounter < (vtUserList.size() - 1)) {
							strBuffer.append(StringUtil.COMMA);
						}
					}
					if (!bUserAlreadyExists) {
						strBuffer.append(StringUtil.COMMA);
						strBuffer.append(strUserId);
					}
					stmtNotify.setString(1, strBuffer.toString());
					stmtNotify.setInt(2, iDocId);
					stmtNotify.executeUpdate();
					stmtNotify.close();
				}
			}
			}
		} else {
			strQuery =
				"INSERT INTO ETS.DOC_UNSUBSCRIBE "
					+ "(DOC_ID,USER_ID,LAST_TIMESTAMP) "
					+ "VALUES (?,?,current timestamp)";
		PreparedStatement stmtNotify =
			m_pdConnection.prepareStatement(strQuery);

		stmtNotify.setInt(1, iDocId);
		stmtNotify.setString(2, strUserId);

		stmtNotify.executeUpdate();
			stmtNotify.close();
		}

		if (bSubscribe) {
			// If this ID already exists in DOC_UNSUBSCRIBE table, remove it.
			PreparedStatement stmtNotify =
				m_pdConnection.prepareStatement(
					"DELETE FROM ETS.DOC_UNSUBSCRIBE "
						+ "WHERE DOC_ID = ? AND USER_ID = ?");
			stmtNotify.setInt(1, iDocId);
			stmtNotify.setString(2, strUserId);
			stmtNotify.executeUpdate();
		stmtNotify.close();
	}
	}

	/**
	 * @param strProblemID
	 * @param strProjectId
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public ETSDoc getDocByProblemId(String strProblemID, String strProjectId)
		throws SQLException {
		Statement stmtDocDetails = m_pdConnection.createStatement();

		String strQuery =
			"SELECT D.*, "
				+ "(SELECT COUNT(M.DOC_ID) FROM ETS.ETS_DOC_METRICS M "
				+ "WHERE D.DOC_ID=M.DOC_ID) AS HITS "
				+ "FROM ETS.ETS_DOC D "
				+ "WHERE D.ISSUE_ID = '"
				+ strProblemID
				+ "' AND D.PROJECT_ID = '"
				+ strProjectId
				+ "' AND DELETE_FLAG != '"
				+ DocConstants.TRUE_FLAG
				+ "' WITH UR";
		ResultSet rsDocDetails = stmtDocDetails.executeQuery(strQuery);

		ETSDoc udDoc = null;
		if (rsDocDetails.next()) {
			udDoc = getDoc(rsDocDetails);
		}

		rsDocDetails.close();
		stmtDocDetails.close();

		return udDoc;
	}

	/**
	 * @param strCatName
	 * @param strProjectID
	 * @return
	 * @throws SQLException
	 */
	public ETSCat getInvisibleCatByName(String strCatName, String strProjectID)
		throws SQLException {
		PreparedStatement stmtCat =
			m_pdConnection.prepareStatement(
				"SELECT * FROM ETS.ETS_CAT "
					+ "WHERE CAT_NAME=? "
					+ "AND PROJECT_ID=? "
					+ "AND PARENT_ID = -1 WITH UR");
		stmtCat.setString(1, strCatName);
		stmtCat.setString(2, strProjectID);
		ResultSet rsCat = stmtCat.executeQuery();
		ETSCat udCat = null;

		while (rsCat.next()) {
			udCat = getCat(rsCat);
		}

		rsCat.close();
		stmtCat.close();
		return udCat;
	}

	/**
	 * @param iDocId
	 * @param strOldStatus
	 * @param strStatus
	 * @return
	 * @throws SQLException
	 */
	public synchronized boolean updateDocFileStatus(
		int iDocId,
		String strStatus)
		throws SQLException {
		PreparedStatement upDocSt = null;
		boolean bSuccess = false;
		upDocSt =
			m_pdConnection.prepareStatement(
				"UPDATE ETS.ETS_DOCFILE "
					+ "SET FILE_STATUS=? "
					+ "WHERE DOC_ID = ?");

		try {
			upDocSt.setString(1, strStatus);
			upDocSt.setInt(2, iDocId);

			bSuccess = upDocSt.executeUpdate() > 0;
			upDocSt.close();
		} catch (SQLException se) {
			m_pdLog.error("sql error =" + se);
			throw se;
		}
		return bSuccess;
	}

	/**
	 * @param iDocId
	 * @param iDocFileId
	 * @param strStatus
	 * @throws SQLException
	 */
	public synchronized void updateDocFileStatus(
		int iDocId,
		int iDocFileId,
		String strStatus)
		throws SQLException {
		PreparedStatement upDocSt = null;

		upDocSt =
			m_pdConnection.prepareStatement(
				"UPDATE ETS.ETS_DOCFILE "
					+ "SET FILE_STATUS=? "
					+ "WHERE DOC_ID=? "
					+ "AND DOCFILE_ID = ?");
		try {
			upDocSt.setString(1, strStatus);
			upDocSt.setInt(2, iDocId);
			upDocSt.setInt(3, iDocFileId);

			upDocSt.executeUpdate();
			upDocSt.close();
		} catch (SQLException se) {
			m_pdLog.error("sql error =" + se);
			throw se;
		}
	}

	/**
	 * @param iDocId
	 * @param strOldStatus
	 * @param strStatus
	 * @return
	 * @throws SQLException
	 */
	public synchronized boolean updateDocFileStatus(
		int iDocId,
		String strOldStatus,
		String strStatus)
		throws SQLException {
		PreparedStatement upDocSt = null;
		boolean bSuccess = false;
		upDocSt =
			m_pdConnection.prepareStatement(
				"UPDATE ETS.ETS_DOCFILE "
					+ "SET FILE_STATUS=? "
					+ "WHERE FILE_STATUS=? "
					+ "AND DOC_ID = ?");
		try {
			upDocSt.setString(1, strStatus);
			upDocSt.setString(2, strOldStatus);
			upDocSt.setInt(3, iDocId);

			bSuccess = upDocSt.executeUpdate() > 0;
			upDocSt.close();
		} catch (SQLException se) {
			m_pdLog.error("sql error =" + se);
			throw se;
		}
		return bSuccess;
	}

	/**
	 * @param iDocFileId
	 * @param iDocId
	 * @throws SQLException
	 */
	public synchronized boolean deleteDocFile(int iDocFileId,int iDocId)
		throws SQLException {
		PreparedStatement upDocSt = null;
		boolean bSuccess = false;
		String qry = "DELETE FROM ETS.ETS_DOCFILE "
							+ "WHERE DOCFILE_ID = ?"
							+ " AND DOC_ID= ?";
		upDocSt = m_pdConnection.prepareStatement(qry);
		try {
			upDocSt.setInt(1, iDocFileId);
			upDocSt.setInt(2, iDocId);

			bSuccess = (upDocSt.executeUpdate() > 0);
			upDocSt.close();
			
		} catch (SQLException se) {
			m_pdLog.error("sql error =" + se);
			throw se;
		}
		return bSuccess;
	}

	/**
	 * @param iDocId
	 * @param strStatus
	 * @throws SQLException
	 */
	public synchronized boolean deleteDocFile(int iDocId, String strStatus)
		throws SQLException {
		PreparedStatement upDocSt = null;
		boolean bSuccess = false;
		upDocSt =
			m_pdConnection.prepareStatement(
				"DELETE FROM ETS.ETS_DOCFILE "
					+ "WHERE DOC_ID=? "
					+ "AND FILE_STATUS <> ?");
		try {
			upDocSt.setInt(1, iDocId);
			upDocSt.setString(2, strStatus);

			bSuccess = (upDocSt.executeUpdate() > 0);
			upDocSt.close();

		} catch (SQLException se) {
			m_pdLog.error("sql error =" + se);
			throw se;
		}
		return bSuccess;
	}

	/**
	 * @param rsGroup
	 * @return
	 * @throws SQLException
	 */
	private Group populateGroup(ResultSet rsGroup) throws SQLException {
	    DocReaderDAO udReaderDAO = new DocReaderDAO();
	    return udReaderDAO.populateGroup(rsGroup);
	}

	/**
	 * @param strProjectId
	 * @return
	 * @throws SQLException
	 */
	public List getGroups(String strProjectId) throws SQLException {
	    DocReaderDAO udReaderDAO = new DocReaderDAO();
	    udReaderDAO.setConnection(m_pdConnection);
	    return udReaderDAO.getGroups(strProjectId);
	}

	/**
	 * @param strUserId
	 * @param strProjectId
	 * @return
	 * @throws SQLException
	 */
	public List getUserGroups(String strUserId, String strProjectId)
		throws SQLException {
		List lstGroups = new ArrayList();

		PreparedStatement stmtGroups =
			m_pdConnection.prepareStatement(
				"SELECT * FROM ETS.GROUPS "
					+ "WHERE PROJECT_ID=? AND (GROUP_ID IN ("
					+ "SELECT GROUP_ID FROM ETS.USER_GROUPS WHERE USER_ID = ?) OR NAME = ?) WITH UR");
		stmtGroups.setString(1, strProjectId);
		stmtGroups.setString(2, strUserId);
		stmtGroups.setString(3, Defines.GRP_ALL_USERS);
		ResultSet rsGroups = stmtGroups.executeQuery();

		while (rsGroups.next()) {
			lstGroups.add(populateGroup(rsGroups));
		}

		rsGroups.close();
		stmtGroups.close();
		return lstGroups;
	}

	/**
	 * @param vtNotifyList
	 * @return
	 */
	public static Vector getUserList(Vector vtNotifyList) {
		Vector vtUserList = new Vector();

		for (int iCounter = 0; iCounter < vtNotifyList.size(); iCounter++) {
			DocNotify udDocNotify = (DocNotify) vtNotifyList.get(iCounter);
			if (!StringUtil.isNullorEmpty(udDocNotify.getUserId())) {
				vtUserList.add(udDocNotify);
			}
		}
		return vtUserList;
	}


	/**
	 * @param strProjectId
	 * @param vtNotifyList
	 * @return
	 */
	public Vector getUserListWithNames(String strProjectId, Vector vtNotifyList) throws SQLException 
	{
		try {
			Vector vtUserList = new Vector();

			Statement stmtNotifyUsers = null;
			ResultSet rsNotifyUsers = null;
			for (int iCounter = 0; iCounter < vtNotifyList.size(); iCounter++) {
				DocNotify udDocNotify = (DocNotify) vtNotifyList.get(iCounter);
				if (!StringUtil.isNullorEmpty(udDocNotify.getUserId())) {
					
					String strQuery = "select * from amt.users a, ets.ets_users e " +
							" where a.ir_userid = '"+ udDocNotify.getUserId() +"' and "+
							" e.user_project_id = '"+ strProjectId +"' with ur";

					stmtNotifyUsers = m_pdConnection.createStatement();
					rsNotifyUsers = stmtNotifyUsers.executeQuery(strQuery);

					if (rsNotifyUsers.next()) {
						ETSUser udUser = new ETSUser();
						udUser.setUserId( rsNotifyUsers.getString("IR_USERID").trim() );
						udUser.setUserName( rsNotifyUsers.getString("USER_FULLNAME").trim() );
						vtUserList.add(udUser);
					}
			
				}
			}
			if(rsNotifyUsers!=null) {
				rsNotifyUsers.close();
			}
			if(stmtNotifyUsers!=null) {
				stmtNotifyUsers.close();
			}

			return vtUserList;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * @param vtNotifyList
	 * @return
	 */
	public static Vector getGroupList(Vector vtNotifyList) {
		Vector vtGrpList = new Vector();
		for (int iCounter = 0; iCounter < vtNotifyList.size(); iCounter++) {
			DocNotify udDocNotify = (DocNotify) vtNotifyList.get(iCounter);
			if (!StringUtil.isNullorEmpty(udDocNotify.getGroupId())) {
				vtGrpList.add(udDocNotify);
			}
		}
		return vtGrpList;
	}

	/**
	 * @param vtNotifyList
	 * @return
	 */
	public static List getGroupListDetailed(Vector vtNotifyList) {
		Vector vtGrpList = new Vector();
		for (int iCounter = 0; iCounter < vtNotifyList.size(); iCounter++) {
			DocNotify udDocNotify = (DocNotify) vtNotifyList.get(iCounter);
			if (!StringUtil.isNullorEmpty(udDocNotify.getGroupId())) {
				vtGrpList.add(udDocNotify);
			}
		}
		return vtGrpList;
	}

	/**
	 * @param iParentId
	 * @param strName
	 * @return
	 * @throws SQLException
	 */
	public List getDocListByNameAndCat(int iParentId, String strName)
		throws SQLException {

		int iCount = 0;
		String strQuery =
			"select d.*,"
				+ "(select count(m.doc_id) from ets.ets_doc_metrics m "
				+ "where d.doc_id=m.doc_id) as hits  "
				+ "from ETS.ETS_DOC d "
				+ "where d.doc_name LIKE '"
				+ strName
				+ "%' and "
				+ "d.cat_id = ? and "
				+ " itar_upload_status !='P'"
				+ " and delete_flag !='"
				+ DocConstants.TRUE_FLAG
				+ "' order by d.doc_name with ur";

		if (m_pdLog.isDebugEnabled()) {
			m_pdLog.debug(strQuery);
		}

		PreparedStatement stmtGetDocs =
			m_pdConnection.prepareStatement(strQuery);
		stmtGetDocs.setInt(1, iParentId);
		ResultSet rsGetDocs = stmtGetDocs.executeQuery();

		List lstDocs = new ArrayList();
		while (rsGetDocs.next()) {
			lstDocs.add(getDoc(rsGetDocs));
		}

		rsGetDocs.close();
		stmtGetDocs.close();
		return lstDocs;
	}

	/**
	 * @param iDocId
	 * @param strStatus
	 * @throws SQLException
	 */
	public synchronized boolean deleteDocFileWithStatus(
		int iDocId,
		String strStatus)
		throws SQLException {
		PreparedStatement upDocSt = null;
		boolean bSuccess = false;
		upDocSt =
			m_pdConnection.prepareStatement(
				"DELETE FROM ETS.ETS_DOCFILE "
					+ "WHERE DOC_ID = ? "
					+ "AND FILE_STATUS = ?");
		try {
			upDocSt.setInt(1, iDocId);
			upDocSt.setString(2, strStatus);

			bSuccess = (upDocSt.executeUpdate() > 0);
			upDocSt.close();

		} catch (SQLException se) {
			m_pdLog.error("sql error =" + se);
			throw se;
		}
		return bSuccess;
	}

	/**
	 * @param strGroupId
	 * @return
	 * @throws SQLException
	 */
	public List getGroupUsers(String strGroupId) throws SQLException {
	    DocReaderDAO udReaderDAO = new DocReaderDAO();
	    udReaderDAO.setConnection(m_pdConnection);
	    return udReaderDAO.getGroupUsers(strGroupId);
	}

	/**
	 * @param iDocId
	 * @return
	 * @throws SQLException In case of database errors
	 */
	public List getAllDocRestrictedGroupIds(int iDocId) throws SQLException {

		String strQuery =
			"SELECT * FROM ETS.GROUPS "
				+ "WHERE GROUP_ID IN ("
				+ "SELECT DISTINCT GROUP_ID FROM ETS.ETS_PRIVATE_DOC"
				+ " WHERE DOC_ID = ? AND ( ACCESS_TYPE is null or ACCESS_TYPE = ? ))"
				+ " WITH UR";

		PreparedStatement stmtGroups =
			m_pdConnection.prepareStatement(strQuery);

		stmtGroups.setInt(1, iDocId);
		stmtGroups.setString(2, Defines.DOC_READ_ACCESS);
		ResultSet rsGroups = stmtGroups.executeQuery();

		List lstMembers = new ArrayList();
		while (rsGroups.next()) {
			lstMembers.add(populateGroup(rsGroups));
		}
		rsGroups.close();
		stmtGroups.close();
		return lstMembers;
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
	    DocUpdateDAO udUpdateDAO = new DocUpdateDAO();
	    udUpdateDAO.setConnection(m_pdConnection);
	    return udUpdateDAO.addDocResGroups(strGroupIDs, iDocID, strProjectID);
	}

	/**
	 * @param strGroupId
	 * @throws SQLException
	 */
	public void removeDocGroups(String strGroupId) throws SQLException {
		String strQuery = "DELETE FROM ETS.ETS_PRIVATE_DOC WHERE GROUP_ID = ?";

		try {
			PreparedStatement stmtDelDocGroups =
				m_pdConnection.prepareStatement(strQuery);
			stmtDelDocGroups.setString(1, strGroupId);
			stmtDelDocGroups.executeUpdate();

			stmtDelDocGroups.close();

		} catch (SQLException e) {
			m_pdLog.error("sql error =" + DocUpdateDAO.getStackTrace(e));
			throw e;
		}
	}

	/**
	 * @param strUserId
	 * @param strProjectId
	 * @return
	 * @throws SQLException
	 */
	public List getOpenTasks(String strUserId, String strProjectId)
		throws SQLException {
		List lstTasks = new ArrayList();

		String strQuery =
			"SELECT TASK_ID,TITLE,OWNER_ID,"
				+ "(SELECT EDGE_USERID FROM AMT.USERS X WHERE X.IR_USERID=OWNER_ID) AS OWNERID, "
			    + "(SELECT USER_FULLNAME FROM AMT.USERS X WHERE X.IR_USERID=OWNER_ID) AS OWNERNAME, " 
				+ "(SELECT EDGE_USERID FROM AMT.USERS X WHERE X.IR_USERID=LAST_USERID) AS CREATORID, "
				+ "(SELECT USER_FULLNAME FROM AMT.USERS X WHERE X.IR_USERID=LAST_USERID) AS CREATORNAME, "
				+ "LAST_USERID,STATUS "
				+ "FROM ETS.ETS_TASK_MAIN "
				+ "WHERE OWNER_ID = ? AND PROJECT_ID = ? AND STATUS != ?";
		PreparedStatement stmtTasks = m_pdConnection.prepareStatement(strQuery);
		stmtTasks.setString(1, strUserId);
		stmtTasks.setString(2, strProjectId);
		stmtTasks.setString(3, Defines.GREEN);
		ResultSet rsTasks = stmtTasks.executeQuery();
		while (rsTasks.next()) {
			ETSTask udTask = new ETSTask();
			udTask.setId(rsTasks.getInt("TASK_ID"));
			udTask.setTitle(rsTasks.getString("TITLE"));
			udTask.setOwnerId(rsTasks.getString("OWNERID"));
			udTask.setOwnerName(rsTasks.getString("OWNERNAME"));
			udTask.setCreatorId(rsTasks.getString("CREATORID"));
			udTask.setCreatorName(rsTasks.getString("CREATORNAME"));
			udTask.setStatus(rsTasks.getString("STATUS"));
			lstTasks.add(udTask);
		}

		rsTasks.close();
		stmtTasks.close();
		return lstTasks;
	}

	/**
	 * @param strProjectId
	 * @param strOldUserId
	 * @param strNewUserId
	 * @return
	 * @throws SQLException
	 */
	public boolean transferTasks(
		String strProjectId,
		String strOldUserId,
		String strNewUserId)
		throws SQLException {

		boolean bSuccess = false;
		String strQuery =
			"UPDATE ETS.ETS_TASK_MAIN "
				+ "SET OWNER_ID = ? "
				+ "WHERE OWNER_ID = ? AND PROJECT_ID = ? AND STATUS != ?";
		PreparedStatement stmtTasks = m_pdConnection.prepareStatement(strQuery);
		stmtTasks.setString(1, strNewUserId);
		stmtTasks.setString(2, strOldUserId);
		stmtTasks.setString(3, strProjectId);
		stmtTasks.setString(4, Defines.GREEN);
		int iUpdateCount = stmtTasks.executeUpdate();
		
		if (iUpdateCount > 0) {
			bSuccess = true;
		}
		
		stmtTasks.close();
		return bSuccess;
	}
	
	/*
	 * 
	 * Groups functionality is implemented in the following methods.
	 * 
	 * 
	 */
	/**
	  * @param iParentId
	  * @param strSortBy
	  * @param strSortOrder
	  * @param bIsAdmin
	  * @param strUserId
	  * @return
	  * @throws SQLException In case of database errors
	  * @throws Exception
	  */
	 public Vector getAllGroups(
	  String strProjectId,
	  String strSortBy,
	  String strSortOrder,
	  boolean bIsAdmin,
	  String strUserId)
	  throws SQLException, Exception {
	  String strBuffer = "name";
	 
	  if (strSortBy.equals(Defines.SORT_BY_DATE_STR)) {
	   strBuffer = "last_timestamp " + strSortOrder + ",name";
	  } else if (strSortBy.equals(Defines.SORT_BY_TYPE_STR)) {
	   strBuffer = "name";
	  } else if (strSortBy.equals(Defines.SORT_BY_AUTH_STR)) {
	   strBuffer = "owner " + strSortOrder + ",name";
	  }
	 
	  String strQuery = 
	   "select g.group_id,g.project_id,g.name,g.type,g.description,g.owner,g.last_timestamp "
	    + "from ETS.GROUPS g "
	    + "where g.project_id= '"
	    + strProjectId
	    + "' "
	    + " order by "
	    + strBuffer
	    + " "
	    + strSortOrder
	    + " with ur";
	 
	  if (!bIsAdmin) {
	   strQuery = "(select g.group_id,g.project_id,g.name,g.type,g.description,g.owner,g.last_timestamp " +
	      "from ETS.GROUPS g " +
	      "where g.project_id= '" +
	      strProjectId +
	      "' and g.type in ('" + 
	      GroupConstants.ETS_PUBLIC + "','" +
	      GroupConstants.ETS_IBM_ONLY +
	      "' ))" +
	      "union (" +
	      " select g.group_id,g.project_id,g.name,g.type,g.description,g.owner,g.last_timestamp" + 
	     " from ETS.GROUPS g " +
	     " where g.project_id='" +  
	     strProjectId  +"' " +
	     " and g.type in ('" + GroupConstants.ETS_PRIVATE_IBM_ONLY_GROUP + "','" +
	     GroupConstants.ETS_PRIVATE_PUBLIC_GROUP + "')" +
	     " and (g.owner = '" + strUserId + "'" +
	     " or g.group_id in ( select u.group_id from ets.user_groups u " +
	     " where u.user_id='" + strUserId + "'" +
	     " and u.group_id = g.group_id ))) " +
	     " order by " +
	      strBuffer +
	      " " +
	      strSortOrder +
	      " with ur ";
	     
	     
	  }
	  if (m_pdLog.isDebugEnabled()) {
	   m_pdLog.debug(strQuery);
	  }
	  Statement stmtGetGroups = m_pdConnection.createStatement();
	  ResultSet rsGetGroups = stmtGetGroups.executeQuery(strQuery);
	 
	  Vector groups = new Vector();
	 
	  while (rsGetGroups.next() != false) {
	   Group udGroup = new Group();
	   udGroup = getGroup(rsGetGroups);
	   groups.add(udGroup);
	  }
	  rsGetGroups.close();
	  stmtGetGroups.close();
	  return populateUserNames(groups);
	 }





	/**
	  * @param rsGroup
	  * @return
	  * @throws SQLException In case of database errors
	  */
	 public Group getGroup(ResultSet rsGroup) throws SQLException {
	  Group udGroup = new Group();
	 
	  udGroup.setGroupId(rsGroup.getString("GROUP_ID"));
	  udGroup.setProjectId(rsGroup.getString("PROJECT_ID"));
	  udGroup.setOwner(rsGroup.getString("OWNER"));
	  udGroup.setUserId(rsGroup.getString("OWNER"));
	  udGroup.setGroupName(rsGroup.getString("NAME"));
	  udGroup.setDescription(rsGroup.getString("DESCRIPTION"));
	  
	  // The security classification & group type are saved in the same column in db
	  if (rsGroup.getString("TYPE").equals(GroupConstants.ETS_PRIVATE_IBM_ONLY_GROUP) || 
	      rsGroup.getString("TYPE").equals(GroupConstants.ETS_PRIVATE_PUBLIC_GROUP)) {
	  		udGroup.setType("PRIVATE");
	  		if (rsGroup.getString("TYPE").equals(GroupConstants.ETS_PRIVATE_PUBLIC_GROUP))
	  		{
	  			udGroup.setGroupSecurityClassification("0");
	  		} else {
	  			udGroup.setGroupSecurityClassification("1");
	  		}
	   
	  } else {
	  		udGroup.setType("PUBLIC");
	  		udGroup.setGroupSecurityClassification(rsGroup.getString("TYPE"));
	  }
	  udGroup.setLastTimestamp(rsGroup.getTimestamp("LAST_TIMESTAMP"));
	 
	  return udGroup;
	 }
	 
	 public Vector getIBMGroups(Vector vtGroups)
	 {
	 	Vector vtIBMGroups = new Vector();
	 	
	 	Iterator iterGroups = vtGroups.iterator();
	 	
	 	while(iterGroups.hasNext())
	 	{
	 		Group grpIBMGroup = (Group)iterGroups.next();
	 		
	 		if (!grpIBMGroup.getGroupSecurityClassification().equals(GroupConstants.ETS_PUBLIC))
	 		{
	 			vtIBMGroups.add(grpIBMGroup);
	 		}
	 	}
	 	return vtIBMGroups;
	 }

	 


		/**
		 * @param strUserId
		 * @param strProjectId
		 * @return
		 * @throws SQLException
		 */
		public Group getGroupNameById(String strGroupID)
			throws SQLException {
			//List lstGroups = new ArrayList();
			Group udGrp = new Group();
			String strQuery = "select * from ets.groups where group_id = '"+ strGroupID +"' with ur";

			Statement stmtNotifyGroup = m_pdConnection.createStatement();
			ResultSet rsGroup = stmtNotifyGroup.executeQuery(strQuery);

			String notifyGroupName = "";
			while (rsGroup.next()) {
				udGrp.setGroupId( rsGroup.getString("GROUP_ID") );
				udGrp.setGroupName( rsGroup.getString("NAME") );
				udGrp.setType( rsGroup.getString("TYPE") );
				udGrp.setDescription( rsGroup.getString("DESCRIPTION") );
				udGrp.setOwner( rsGroup.getString("OWNER") );
				udGrp.setProjectId( rsGroup.getString("PROJECT_ID") );
				udGrp.setLastTimestamp( rsGroup.getTimestamp("LAST_TIMESTAMP").getTime() );
			}
	
			rsGroup.close();
			stmtNotifyGroup.close();

			return udGrp;
		}




		/**
		 * @param iDocID
		 * @return boolean
		 * @throws SQLException In case of database errors
		 * @throws Exception
		 */
		public synchronized boolean clearNotificationList(int iDocID)
			throws SQLException {
		    DocUpdateDAO udDocUpdate = new DocUpdateDAO();
		    udDocUpdate.setConnection(m_pdConnection);
		    return udDocUpdate.clearNotificationList(iDocID);
		}

		/*
		 * @param  projectid
		 * @param groupid
		 * 
		 */
		public List getAvailableMembersList(String projectid, String groupid) throws SQLException {
			String sb = "a.user_fname " + Defines.SORT_ASC_STR + ", a.user_lname";

			PreparedStatement getAvailMemSt = 
				m_pdConnection.prepareStatement("select u.*,'' as role_name,a.user_fname,a.user_lname,d.user_type,d.assoc_company,d.user_type  "
					 + "from ETS.ETS_USERS u, AMT.USERS a,decaf.users d where u.user_project_id = ?"
					 + " and u.user_id = a.ir_userid and u.active_flag in ('A','P','R') and u.user_id in ( select user_id from ETS.USER_GROUPS where group_id = ? )and a.edge_userid = d.userid order by "
					 + sb + " " + Defines.SORT_ASC_STR + " with ur");
			
			getAvailMemSt.setString(1, projectid);
			getAvailMemSt.setString(2, groupid);
			ResultSet rs = getAvailMemSt.executeQuery();

			Vector mems = null; //new Vector();
			mems = populateUserNames(getAllUsersData(rs));
			getAvailMemSt.close();
			List lstMembers = new ArrayList();
			for (int u=0; u < mems.size(); u++) {
				lstMembers.add(mems.elementAt(u));
			}
			return lstMembers;
		}

		private Vector getAllUsersData(ResultSet rs) throws SQLException {
			Vector v = new Vector();

			while (rs.next()) {
				ETSUser user = getAllUserData(rs);
				v.addElement(user);
			}
			return v;
			
		}

		private ETSUser getAllUserData(ResultSet rs) throws SQLException {
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

			user.setUserName(rs.getString("USER_FNAME") + "&nbsp;" + rs.getString("USER_LNAME"));

			String comp = "";
			if ((rs.getString("user_type")).equalsIgnoreCase("I")) {
				comp = "IBM";
			} else {
				String c = rs.getString("assoc_company");
				if (c != null) {
					comp = c;
				} else {
					comp = "";
				}
			}
			user.setCompany(comp);
			user.setRoleName(rs.getString("role_name"));
			user.setUserType(AmtCommonUtils.getTrimStr(rs.getString("user_type")));

			//System.out.println("usertype= "+rs.getString("user_type")+"  comp="+comp);
			return user;
		}

		
		
		
		public List populateNotificationList(
				int iDocID, ETSDoc udDoc, BaseDocumentForm udForm, boolean bPopuplateAllList) throws SQLException
		{
			List ltGroup = new ArrayList();
			List ltGroupId = new ArrayList();
			List ltALLNotificationList = new ArrayList();
			boolean bNotifyList=false;
			try {
				//Check for Notification List (Whether to be pre-populated)
				Vector vtNotifyList = getDocNotifyList(udDoc.getId());
				Vector vtNotifyAllUsers = new Vector();
				int iTotalGroup = 0;
				if (vtNotifyList.size() > 0) {
					DocNotify udDocNotify = null;
					ETSUser udUser = new ETSUser();
					// Check the first element for the Notify All Flag
					udDocNotify = (DocNotify) vtNotifyList.get(0);
					if ( !StringUtil.isNullorEmpty(udDocNotify.getNotifyAllFlag() )
						&& DocConstants.IND_YES.equals( udDocNotify.getNotifyAllFlag()) ) {
						// Means notify all was selected.
						udForm.setNotifyFlag(udDocNotify.getNotifyAllFlag());

						Vector vtProjMembers = getProjMembersWithNames(udDoc.getProjectId());
						if( udDoc.getIbmOnly() == '0' )
							vtNotifyAllUsers = vtProjMembers;
						else if( (udDoc.getIbmOnly() == '1') || (udDoc.getIbmOnly() == '2' ) ) {
							//All IBM members permenently
							Vector vtIBMMembers = getIBMMembersWithNames( vtProjMembers );
							vtNotifyAllUsers = vtIBMMembers;
						}
						
						if (bPopuplateAllList) {
						    Vector vtSubscribedUsers = new Vector();
//							String[] strNotifyusers = new String[vtNotifyAllUsers.size()];
						for(int i=0;i<vtNotifyAllUsers.size();i++) {
							ETSUser udUserTemp = (ETSUser) vtNotifyAllUsers.get(i);
								// Check if user is in unsubscribe list
								if (isUserInNotificationList(udUserTemp.getUserId(), iDocID)) {
								    vtSubscribedUsers.add(udUserTemp);
								}
//								strNotifyusers[i] = udUserTemp.getUserId();
							}
							String[] strNotifyusers = new String[vtSubscribedUsers.size()];
							for(int i=0;i<vtSubscribedUsers.size();i++) {
								ETSUser udUserTemp = (ETSUser) vtSubscribedUsers.get(i);
							strNotifyusers[i] = udUserTemp.getUserId();
						}
							udForm.setNotifyAllUsers(vtSubscribedUsers);
							udForm.setNotifyUsers(strNotifyusers);
							ltALLNotificationList.add(vtSubscribedUsers);
						}

						} else {
						Vector vtSubList = getUserListWithNames( udDoc.getProjectId(), vtNotifyList);
ltALLNotificationList.add(vtSubList);

						Vector vtTmpList = new Vector();
						Vector vtTmpETSUser = new Vector();
						for (int iCounter = 0; iCounter < vtSubList.size(); iCounter++) {
							ETSUser udUserTemp = (ETSUser) vtSubList.get(iCounter);
							udUserTemp.setUserId( udUserTemp.getUserId().trim() );
							// Check if user is in unsubscribe list
							if (isUserInNotificationList(udUserTemp.getUserId(), iDocID)) {
								vtTmpList.add(udUserTemp.getUserId());
								vtTmpETSUser.add(udUserTemp);
							}
						}
						String[] notifyUsers = new String[vtTmpList.size()];
						notifyUsers = (String[]) vtTmpList.toArray(notifyUsers);						
udForm.setNotifyUsers( notifyUsers );
udForm.setDocNotifyUsers( vtTmpETSUser );				
						vtSubList = DocumentDAO.getGroupList(vtNotifyList);
						
						String[] notifyGroups = new String[vtSubList.size()];
						String strNotifyGroupID = "";
						List ltTemp = new ArrayList();
						for (int iCounter = 0; iCounter < vtSubList.size(); iCounter++) {
							udDocNotify = (DocNotify) vtSubList.get(iCounter);
							notifyGroups[iCounter] = udDocNotify.getGroupId();
							ltGroupId.add(notifyGroups[iCounter]);
							ltGroup.add( getGroupNameById( notifyGroups[iCounter] ) );
							//ltTemp = getAvailableMembersList(udDoc.getProjectId(), notifyGroups[iCounter]);
						}
udForm.setSelectedNotifyGroups(notifyGroups);
ltALLNotificationList.add(ltGroup);
						udForm.setDocNotifyGroups( ltGroup );
						udForm.setDocNotifyGroupsId( ltGroupId );
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw e;
			}
			return ltALLNotificationList;
		}




}