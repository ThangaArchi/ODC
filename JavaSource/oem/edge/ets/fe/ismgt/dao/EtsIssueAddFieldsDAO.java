/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005-2008                                     */
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

package oem.edge.ets.fe.ismgt.dao;

import java.sql.Connection;import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;
import oem.edge.ets.fe.ismgt.model.EtsIssueAddFieldsBean;

import org.apache.commons.logging.Log;

/**
 * @author Dharanendra Prasad
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EtsIssueAddFieldsDAO implements EtsIssFilterConstants, EtsIssueConstants {

	public static final String VERSION = "1.48";

	private static Log logger = EtsLogger.getLogger(EtsIssueAddFieldsDAO.class);

	/**
	 * Constructor for EtsIssueAddFieldsDAO.
	 */
	public EtsIssueAddFieldsDAO() {
		super();
	}

	/**
	 * 
	 * @param projectId
	 * @return
	 */
	public ArrayList getCurrentCustFieldId(String projectId) {
		StringBuffer sb = new StringBuffer();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		//int custFieldId = 0;
		ArrayList arrLstCustFieldId = new ArrayList();
		try {
			conn = ETSDBUtils.getConnection(ETSDATASRC);
			//conn.setAutoCommit(false);
			//sb.append("SELECT MAX(FIELD_ID) FROM " + ISMGTSCHEMA + ".ETS_ISSUE_ADD_FIELDS WHERE PROJECT_ID = ?  with ur");			
			sb.append("SELECT FIELD_ID, FIELD_LABEL FROM " + ISMGTSCHEMA + ".ETS_ISSUE_ADD_FIELDS WHERE PROJECT_ID = ? ORDER BY FIELD_ID ASC with ur");
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.clearParameters();
			pstmt.setString(1, projectId);
			if (AmtCommonUtils.isResourceDefined(projectId) ) {				
				rset = pstmt.executeQuery();
				//if(rset.next()) {
				while(rset.next()) {
					//custFieldId = rset.getInt(1);
					arrLstCustFieldId.add(new Integer(rset.getInt(1)));
				}
				//custFieldId += 1;
			}				
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException se) {
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException sqlEx) {
					sqlEx.printStackTrace();
				}
			}
			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in addCustomFields", ETSLSTUSR);
			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();	
			}
		} catch (Exception ex) {
			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in addCustomFields", ETSLSTUSR);
			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();
			}
		} finally {
			ETSDBUtils.close(rset);
			ETSDBUtils.close(pstmt);			
			ETSDBUtils.close(conn);		
		}			
		//return custFieldId;
		return arrLstCustFieldId;
	}
	
	/**
	 * 
	 * @param issueAddFieldBean
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public boolean addCustField(EtsIssueAddFieldsBean [] arrIssueAddFieldBean) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		Connection conn = null;
		PreparedStatement pstmt = null;		
		int inscount = 0;
		boolean flag = false;

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			conn.setAutoCommit(false);

			sb.append("INSERT INTO " + ISMGTSCHEMA + ".ETS_ISSUE_ADD_FIELDS (PROJECT_ID, FIELD_ID, FIELD_LABEL, LAST_USERID, LAST_TIMESTAMP) ");
			sb.append("VALUES(?,?,?,?,current timestamp)");

			pstmt = conn.prepareStatement(sb.toString());

			pstmt.clearParameters();
			
			EtsIssueAddFieldsBean issueAddFieldsBean;
			String projectId = "";
			String lastUserId = "";
			int fieldId = 0;
			String fieldLabel = "";
			
			for(int i=0; i<arrIssueAddFieldBean.length; i++) {
				issueAddFieldsBean = arrIssueAddFieldBean[i];
				
				projectId = issueAddFieldsBean.getProjectId();
				lastUserId = issueAddFieldsBean.getLastUserId();			
				fieldId = issueAddFieldsBean.getFieldId();
				fieldLabel = issueAddFieldsBean.getFieldLabel();				
	
				pstmt.setString(1, projectId);
				pstmt.setInt(2, fieldId);
				pstmt.setString(3, fieldLabel);
				pstmt.setString(4, lastUserId);
	
				if (AmtCommonUtils.isResourceDefined(projectId) && fieldId > 0 ) {
	
					inscount += pstmt.executeUpdate();
	
				}
					
			}
			
			if (inscount == arrIssueAddFieldBean.length ) {
				flag = true;
			} 			
			
			conn.commit();
			conn.setAutoCommit(true);

		} catch (SQLException se) {
	
			if (conn != null) {
	
				try {
	
					conn.rollback();
	
				} catch (SQLException sqlEx) {
	
					sqlEx.printStackTrace();
				}
			}
	
			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in addCustomFields", ETSLSTUSR);
	
			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();
	
			}
	
		} catch (Exception ex) {
	
			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in addCustomFields", ETSLSTUSR);
	
			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();
	
			}
	
		} finally {
			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(conn);		
		}
			
		return flag;

	}
	
	/**
	 * 
	 * @param arrIssueAddFieldBean
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public boolean updateCustField(EtsIssueAddFieldsBean [] arrIssueAddFieldBean) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		Connection conn = null;
		PreparedStatement pstmt = null;		
		int inscount = 0;
		boolean flag = false;

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			conn.setAutoCommit(false);
			
			//UPDATE ETS.ETS_ISSUE_ADD_FIELDS SET FIELD_LABEL='xxxyyyzzz', LAST_USERID='', LAST_TIMESTAMP=current timestamp  WHERE PROJECT_ID='1164367608641' AND FIELD_ID=5;
			
			sb.append("UPDATE " + ISMGTSCHEMA + ".ETS_ISSUE_ADD_FIELDS SET FIELD_LABEL=?, LAST_USERID=?, LAST_TIMESTAMP=current timestamp  WHERE PROJECT_ID=? AND  FIELD_ID=? ");

			pstmt = conn.prepareStatement(sb.toString());

			pstmt.clearParameters();
			
			EtsIssueAddFieldsBean issueAddFieldsBean;
			String fieldLabel = "";
			String lastUserId = "";
			String projectId = "";
			int fieldId = 0;

			for(int i=0; i<arrIssueAddFieldBean.length; i++) {
				issueAddFieldsBean = arrIssueAddFieldBean[i];
				
				fieldLabel = issueAddFieldsBean.getFieldLabel();
				lastUserId = issueAddFieldsBean.getLastUserId();				
				projectId = issueAddFieldsBean.getProjectId();		
				fieldId = issueAddFieldsBean.getFieldId();			
	
				pstmt.setString(1, fieldLabel);
				pstmt.setString(2, lastUserId);
				pstmt.setString(3, projectId);
				pstmt.setInt(4, fieldId);

				if (AmtCommonUtils.isResourceDefined(projectId) && fieldId > 0  ) {
	
					inscount += pstmt.executeUpdate();
	
				}
					
			}
			
			if (inscount == arrIssueAddFieldBean.length ) {
				flag = true;
			} 			
			
			conn.commit();
			conn.setAutoCommit(true);

		} catch (SQLException se) {
	
			if (conn != null) {
	
				try {
	
					conn.rollback();
	
				} catch (SQLException sqlEx) {
	
					sqlEx.printStackTrace();
				}
			}
	
			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in UpdateCustomFields", ETSLSTUSR);
	
			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();
	
			}
	
		} catch (Exception ex) {
	
			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in updateCustomFields", ETSLSTUSR);
	
			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();
	
			}
	
		} finally {
			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(conn);		
		}
			
		return flag;

	}

	/**
	 * 
	 * @param arrIssueAddFieldBean
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public boolean removeCustField(EtsIssueAddFieldsBean [] arrIssueAddFieldBean) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		Connection conn = null;
		PreparedStatement pstmt = null;		
		int inscount = 0;
		boolean flag = false;

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			conn.setAutoCommit(false);

			sb.append("DELETE FROM " + ISMGTSCHEMA + ".ETS_ISSUE_ADD_FIELDS  WHERE PROJECT_ID=? AND  FIELD_ID=? ");

			pstmt = conn.prepareStatement(sb.toString());

			pstmt.clearParameters();
			
			EtsIssueAddFieldsBean issueAddFieldsBean;
			String projectId = "";
			int fieldId = 0;

			for(int i=0; i<arrIssueAddFieldBean.length; i++) {
				issueAddFieldsBean = arrIssueAddFieldBean[i];
				
				projectId = issueAddFieldsBean.getProjectId();		
				fieldId = issueAddFieldsBean.getFieldId();			
	
				pstmt.setString(1, projectId);
				pstmt.setInt(2, fieldId);

				if (AmtCommonUtils.isResourceDefined(projectId) && fieldId > 0 ) {
	
					inscount += pstmt.executeUpdate();
	
				}
					
			}
			
			if (inscount == arrIssueAddFieldBean.length ) {
				flag = true;
			} 			
			
			conn.commit();
			conn.setAutoCommit(true);

		} catch (SQLException se) {
	
			if (conn != null) {
	
				try {
	
					conn.rollback();
	
				} catch (SQLException sqlEx) {
	
					sqlEx.printStackTrace();
				}
			}
	
			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in removeCustomFields", ETSLSTUSR);
	
			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();
	
			}
	
		} catch (Exception ex) {
	
			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in removeCustomFields", ETSLSTUSR);
	
			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();
	
			}
	
		} finally {
			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(conn);		
		}
			
		return flag;

	}
	
	
	public EtsIssueAddFieldsBean [] getAllCustFields(String projectId) {
		StringBuffer sb = new StringBuffer();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;		
		EtsIssueAddFieldsBean [] arrIssueAddFieldsBean = null;				
		EtsIssueAddFieldsBean  issueAddFieldsBean; 

		try {
			conn = ETSDBUtils.getConnection(ETSDATASRC);
			//conn.setAutoCommit(false);
			sb.append("SELECT * FROM " + ISMGTSCHEMA + ".ETS_ISSUE_ADD_FIELDS WHERE PROJECT_ID = ?  ORDER BY FIELD_ID ASC  with ur");			
			pstmt = conn.prepareStatement(sb.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pstmt.clearParameters();
			
			if (AmtCommonUtils.isResourceDefined(projectId) ) {				
				pstmt.setString(1, projectId);
				rset = pstmt.executeQuery();
		
				rset.afterLast();
				rset.previous();
				int rows = rset.getRow();
				arrIssueAddFieldsBean = new EtsIssueAddFieldsBean [rows];
				rset.beforeFirst();
				int indx = 0;
				while(rset.next()) {
					issueAddFieldsBean = new EtsIssueAddFieldsBean();
					issueAddFieldsBean.setProjectId(projectId);					
					issueAddFieldsBean.setFieldId(rset.getInt("FIELD_ID") );
					issueAddFieldsBean.setFieldLabel(rset.getString("FIELD_LABEL"));
					issueAddFieldsBean.setLastUserId(rset.getString("LAST_USERID"));
					issueAddFieldsBean.setLastTimeStamp(rset.getTimestamp("LAST_TIMESTAMP"));					
					arrIssueAddFieldsBean[indx] = issueAddFieldsBean;
					indx++;					
				}			
			}				
			//conn.commit();
			//conn.setAutoCommit(true);
		} catch (SQLException se) {
			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in removeCustomFields", ETSLSTUSR);
			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();	
			}
		} catch (Exception ex) {
			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in removeCustomFields", ETSLSTUSR);
			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();
			}
		} finally {
			ETSDBUtils.close(rset);
			ETSDBUtils.close(pstmt);
			ETSDBUtils.close(conn);		
		}			
		return arrIssueAddFieldsBean;				
		
	}
}