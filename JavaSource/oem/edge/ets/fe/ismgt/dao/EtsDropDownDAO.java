package oem.edge.ets.fe.ismgt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.model.EtsDropDownDataBean;
import oem.edge.ets.fe.ismgt.model.EtsIssOwnerInfo;
import oem.edge.ets.fe.ismgt.model.EtsIssProjectMember;
import oem.edge.ets.fe.ismgt.model.EtsIssTypeInfoModel;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;

import org.apache.commons.logging.Log;


/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004                                          */
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

/**
 * @author v2phani
 * This class will handle the DAO calls to handle loading of ISSUE drop down data
 */
public class EtsDropDownDAO implements EtsIssFilterConstants, EtsIssueConstants {

	public static final String VERSION = "1.48";

	private static Log logger = EtsLogger.getLogger(EtsDropDownDAO.class);

	/**
	 * Constructor for EtsDropDownDAO.
	 */
	public EtsDropDownDAO() {
		super();
	}

	/**
	 * To get the dynamic drop down valsdata
	 * 
	 */

	public ArrayList getDynamicSubTypeAVals(EtsDropDownDataBean dropModel) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		ArrayList dropDownList = new ArrayList();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		int count = 0;

		//qry vars
		String qryProjectId = dropModel.getProjectId();
		String qryIssueClass = dropModel.getIssueClass();
		String qryIssueType = dropModel.getIssueType();
		String qrySubTypeA = dropModel.getSubTypeA();
		String qrySubTypeB = dropModel.getSubTypeB();
		String qrySubTypeC = dropModel.getSubTypeC();
		String qrySubTypeD = dropModel.getSubTypeD();
		String qryfieldName = dropModel.getFieldName();
		String qryIssueSource = dropModel.getIssueSource();
		String qryIssueAccess = dropModel.getIssueAccess();
		String qryActiveFlag = dropModel.getActiveFlag();

		//qry//
		sb.append("select ");
		sb.append(" distinct");
		sb.append(" rtrim(subtype_a) as subtypea ");
		sb.append(" from");
		sb.append(" " + ISMGTSCHEMA + ".ets_dropdown_data");
		sb.append(" where");
		sb.append(" project_id='" + qryProjectId + "' ");
		sb.append(" and issue_class='" + qryIssueClass + "' ");
		sb.append(" and issuetype='" + qryIssueType + "' ");

		if (!qryIssueAccess.equals("IBM")) {

			sb.append(" and issue_access like 'ALL%' ");

		}

		sb.append(" and active_flag='" + qryActiveFlag + "' ");

		sb.append(" order by 1");

		sb.append(" for read only");

		Global.println("getDynamicSubTypeAVals qry =" + sb.toString() + ":");

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					EtsDropDownDataBean dropBean = new EtsDropDownDataBean();

					dropBean.setSubTypeA(EtsIssFilterUtils.getTrimStr(rs.getString("SUBTYPEA")));

					dropDownList.add(dropBean);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return dropDownList;
	}

	/**
	 * To get the dynamic drop down valsdata
	 * 
	 */

	public ArrayList getDynamicSubTypeBVals(EtsDropDownDataBean dropModel) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		ArrayList dropDownList = new ArrayList();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		int count = 0;

		//qry vars
		String qryProjectId = dropModel.getProjectId();
		String qryIssueClass = dropModel.getIssueClass();
		String qryIssueType = dropModel.getIssueType();
		String qrySubTypeA = dropModel.getSubTypeA();
		String qrySubTypeB = dropModel.getSubTypeB();
		String qrySubTypeC = dropModel.getSubTypeC();
		String qrySubTypeD = dropModel.getSubTypeD();
		String qryfieldName = dropModel.getFieldName();
		String qryIssueSource = dropModel.getIssueSource();
		String qryIssueAccess = dropModel.getIssueAccess();
		String qryActiveFlag = dropModel.getActiveFlag();

		//qry//
		sb.append("select ");
		sb.append(" distinct");
		sb.append(" rtrim(subtype_b) as subtypeb ");
		sb.append(" from");
		sb.append(" " + ISMGTSCHEMA + ".ets_dropdown_data");
		sb.append(" where");
		sb.append(" project_id='" + qryProjectId + "' ");
		sb.append(" and issue_class='" + qryIssueClass + "' ");
		sb.append(" and issuetype='" + qryIssueType + "' ");
		sb.append(" and subtype_A= '" + qrySubTypeA + "' ");

		if (!qryIssueAccess.equals("IBM")) {

			sb.append(" and issue_access like 'ALL%' ");

		}

		sb.append(" and active_flag='" + qryActiveFlag + "' ");

		sb.append(" order by 1");

		sb.append(" for read only");

		Global.println("getDynamicSubTypeBVals qry =" + sb.toString() + ":");

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					EtsDropDownDataBean dropBean = new EtsDropDownDataBean();

					dropBean.setSubTypeB(EtsIssFilterUtils.getTrimStr(rs.getString("SUBTYPEB")));

					dropDownList.add(dropBean);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return dropDownList;
	}

	/**
	 * To get the dynamic drop down valsdata
	 * 
	 */

	public ArrayList getDynamicSubTypeCVals(EtsDropDownDataBean dropModel) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		ArrayList dropDownList = new ArrayList();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		int count = 0;

		//qry vars
		String qryProjectId = dropModel.getProjectId();
		String qryIssueClass = dropModel.getIssueClass();
		String qryIssueType = dropModel.getIssueType();
		String qrySubTypeA = dropModel.getSubTypeA();
		String qrySubTypeB = dropModel.getSubTypeB();
		String qrySubTypeC = dropModel.getSubTypeC();
		String qrySubTypeD = dropModel.getSubTypeD();
		String qryfieldName = dropModel.getFieldName();
		String qryIssueSource = dropModel.getIssueSource();
		String qryIssueAccess = dropModel.getIssueAccess();
		String qryActiveFlag = dropModel.getActiveFlag();

		//qry//
		sb.append("select ");
		sb.append(" distinct");
		sb.append(" rtrim(subtype_c) as subtypec ");
		sb.append(" from");
		sb.append(" " + ISMGTSCHEMA + ".ets_dropdown_data");
		sb.append(" where");
		sb.append(" project_id='" + qryProjectId + "' ");
		sb.append(" and issue_class='" + qryIssueClass + "' ");
		sb.append(" and issuetype='" + qryIssueType + "' ");
		sb.append(" and subtype_A= '" + qrySubTypeA + "' ");
		sb.append(" and subtype_B= '" + qrySubTypeB + "' ");

		if (!qryIssueAccess.equals("IBM")) {

			sb.append(" and issue_access like 'ALL%' ");

		}

		sb.append(" and active_flag='" + qryActiveFlag + "' ");

		sb.append(" order by 1");

		sb.append(" for read only");

		Global.println("getDynamicSubTypeCVals qry =" + sb.toString() + ":");

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					EtsDropDownDataBean dropBean = new EtsDropDownDataBean();

					dropBean.setSubTypeC(EtsIssFilterUtils.getTrimStr(rs.getString("SUBTYPEC")));

					dropDownList.add(dropBean);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return dropDownList;
	}

	/**
	 * To get the dynamic drop down valsdata
	 * 
	 */

	public ArrayList getDynamicSubTypeDVals(EtsDropDownDataBean dropModel) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		ArrayList dropDownList = new ArrayList();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		int count = 0;

		//qry vars
		String qryProjectId = dropModel.getProjectId();
		String qryIssueClass = dropModel.getIssueClass();
		String qryIssueType = dropModel.getIssueType();
		String qrySubTypeA = dropModel.getSubTypeA();
		String qrySubTypeB = dropModel.getSubTypeB();
		String qrySubTypeC = dropModel.getSubTypeC();
		String qrySubTypeD = dropModel.getSubTypeD();
		String qryfieldName = dropModel.getFieldName();
		String qryIssueSource = dropModel.getIssueSource();
		String qryIssueAccess = dropModel.getIssueAccess();
		String qryActiveFlag = dropModel.getActiveFlag();

		//qry//
		sb.append("select ");
		sb.append(" distinct");
		sb.append(" rtrim(subtype_d) as subtyped ");
		sb.append(" from");
		sb.append(" " + ISMGTSCHEMA + ".ets_dropdown_data");
		sb.append(" where");
		sb.append(" project_id='" + qryProjectId + "' ");
		sb.append(" and issue_class='" + qryIssueClass + "' ");
		sb.append(" and issuetype='" + qryIssueType + "' ");
		sb.append(" and subtype_A= '" + qrySubTypeA + "' ");
		sb.append(" and subtype_B= '" + qrySubTypeB + "' ");
		sb.append(" and subtype_C= '" + qrySubTypeC + "' ");

		if (!qryIssueAccess.equals("IBM")) {

			sb.append(" and issue_access like 'ALL%' ");

		}

		sb.append(" and active_flag='" + qryActiveFlag + "' ");

		sb.append(" order by 1");

		sb.append(" for read only");

		Global.println("getDynamicSubTypeDVals qry =" + sb.toString() + ":");

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					EtsDropDownDataBean dropBean = new EtsDropDownDataBean();

					dropBean.setSubTypeD(EtsIssFilterUtils.getTrimStr(rs.getString("SUBTYPED")));

					dropDownList.add(dropBean);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return dropDownList;
	}

	/**
	 * To get the default static CQ data
	 * 
	 */

	public ArrayList getStaticDropDownVals(EtsDropDownDataBean dropModel) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		ArrayList dropDownList = new ArrayList();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		int count = 0;

		//qry vars
		String qryProjectId = dropModel.getProjectId();
		String qryIssueClass = dropModel.getIssueClass();
		String qryIssueType = dropModel.getIssueType();
		String qrySubTypeA = dropModel.getSubTypeA();
		String qrySubTypeB = dropModel.getSubTypeB();
		String qrySubTypeC = dropModel.getSubTypeC();
		String qrySubTypeD = dropModel.getSubTypeD();
		String qryfieldName = dropModel.getFieldName();
		String qryIssueSource = dropModel.getIssueSource();
		String qryIssueAccess = dropModel.getIssueAccess();
		String qryActiveFlag = dropModel.getActiveFlag();

		//qry//
		sb.append("select ");
		sb.append(" distinct");
		sb.append(" rtrim(field_value) as fieldvalue");
		sb.append(" from");
		sb.append(" " + ISMGTSCHEMA + ".ets_indep_data");
		sb.append(" where");
		sb.append(" project_id='" + qryProjectId + "' ");
		sb.append(" and issue_class='" + qryIssueClass + "' ");

		if (AmtCommonUtils.isResourceDefined(qryIssueType)) {

			sb.append(" and issuetype='" + qryIssueType + "' ");

		}

		if (AmtCommonUtils.isResourceDefined(qrySubTypeA)) {

			sb.append(" and subtype_A= '" + qrySubTypeA + "' ");

		}

		if (AmtCommonUtils.isResourceDefined(qrySubTypeB)) {

			sb.append(" and subtype_B= '" + qrySubTypeB + "' ");

		}

		if (AmtCommonUtils.isResourceDefined(qrySubTypeC)) {

			sb.append(" and subtype_C= '" + qrySubTypeC + "' ");

		}

		if (AmtCommonUtils.isResourceDefined(qrySubTypeD)) {

			sb.append(" and subtype_D= '" + qrySubTypeD + "' ");

		}

		sb.append(" and field_name='" + qryfieldName + "' "); //field name

		if (AmtCommonUtils.isResourceDefined(qryIssueSource)) {

			sb.append(" and issue_source='" + qryIssueSource + "' ");

		}

		if (!qryIssueAccess.equals("IBM")) {

			sb.append(" and issue_access like 'ALL%' ");

		}

		sb.append(" and active_flag='" + qryActiveFlag + "' ");

		sb.append(" order by 1");
		sb.append(" for read only");

		Global.println("getStaticDropDownVals qry =" + sb.toString() + ":");

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					EtsDropDownDataBean dropBean = new EtsDropDownDataBean();
					dropBean.setFieldValue(EtsIssFilterUtils.getTrimStr(rs.getString("FIELDVALUE")));

					dropDownList.add(dropBean);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return dropDownList;
	}

	/**
	 * to get issue type drop down list, based on project_id and user_type
	 * issue_access='ALL' or issue_access='IBM' 
	 * 
	 */

	public HashMap getFormLabelData(EtsDropDownDataBean dropModel) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		HashMap labelMap = new HashMap();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		int count = 0;

		////qry vars//
		String projectId = dropModel.getProjectId();
		String issueClass = dropModel.getIssueClass();

		///key and val pair///
		String issueTypeName = "";
		String issueTypeValue = "";

		sb.append("SELECT ");
		sb.append(" RTRIM(ISSUETYPE_NAME) as ISSUETYPENAME, ");
		sb.append(" RTRIM(ISSUETYPE_VALUE) as ISSUETYPEVALUE ");
		sb.append(" FROM " + ISMGTSCHEMA + ".ETS_FORM_LABEL_DATA ");
		sb.append(" WHERE ");
		sb.append(" PROJECT_ID = '" + projectId + "' ");
		sb.append(" AND ISSUE_CLASS = '" + issueClass + "' ");
		sb.append(" for read only");

		Global.println("getFormLabelData qry =" + sb.toString() + ":");

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					issueTypeName = EtsIssFilterUtils.getTrimStr(rs.getString("ISSUETYPENAME"));
					issueTypeValue = EtsIssFilterUtils.getTrimStr(rs.getString("ISSUETYPEVALUE"));

					labelMap.put(issueTypeName, issueTypeValue);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return labelMap;
	}

	/**
	 * To get the dynamic drop down valsdata
	 * 
	 */

	public ArrayList getIssueTypes(EtsDropDownDataBean dropModel) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		ArrayList dropDownList = new ArrayList();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		int count = 0;

		//qry vars
		String qryProjectId = AmtCommonUtils.getTrimStr(dropModel.getProjectId());
		String qryIssueClass = AmtCommonUtils.getTrimStr(dropModel.getIssueClass());
		String qryIssueAccess = AmtCommonUtils.getTrimStr(dropModel.getIssueAccess());
		String qryActiveFlag = AmtCommonUtils.getTrimStr(dropModel.getActiveFlag());
		String qryIssueSource = AmtCommonUtils.getTrimStr(dropModel.getIssueSource());

		Global.println("qry issue source in drop DAO==" + qryIssueSource);

		//qry//
		sb.append("select ");
		sb.append(" distinct");
		sb.append(" rtrim(issuetype) as issuetype,");
		sb.append(" rtrim(issue_access) as issueaccess,");
		sb.append(" rtrim(issue_source) as issuesource");
		sb.append(" from");
		sb.append(" " + ISMGTSCHEMA + ".ets_dropdown_data");
		sb.append(" where");
		sb.append(" project_id='" + qryProjectId + "' ");
		sb.append(" and issue_class='" + qryIssueClass + "' ");

		if (!qryIssueAccess.equals("IBM")) {

			sb.append(" and issue_access like 'ALL%' ");

		}

		sb.append(" and active_flag='" + qryActiveFlag + "' ");

		if (qryIssueSource.equals(STDETSOLD)) {

			sb.append(" and issue_source='" + qryIssueSource + "' ");

		}

		sb.append(" order by 1");
		sb.append(" for read only");

		Global.println("getIssueTypes qry =" + sb.toString() + ":");

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					EtsDropDownDataBean dropBean = new EtsDropDownDataBean();
					dropBean.setIssueType(EtsIssFilterUtils.getTrimStr(rs.getString("ISSUETYPE")));
					dropBean.setIssueAccess(EtsIssFilterUtils.getTrimStr(rs.getString("ISSUEACCESS")));
					dropBean.setIssueSource(EtsIssFilterUtils.getTrimStr(rs.getString("ISSUESOURCE")));

					dropDownList.add(dropBean);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return dropDownList;
	}

	/**
	 * To get the dynamic drop down valsdata
	 * 
	 */

	public ArrayList getChangeTypes(EtsDropDownDataBean dropModel) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		ArrayList dropDownList = new ArrayList();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		int count = 0;

		//qry vars
		String qryProjectId = AmtCommonUtils.getTrimStr(dropModel.getProjectId());
		String qryIssueClass = AmtCommonUtils.getTrimStr(dropModel.getIssueClass());
		String qryIssueAccess = AmtCommonUtils.getTrimStr(dropModel.getIssueAccess());
		String qryActiveFlag = AmtCommonUtils.getTrimStr(dropModel.getActiveFlag());
		String qryIssueSource = AmtCommonUtils.getTrimStr(dropModel.getIssueSource());

		//qry//
		sb.append("select ");
		sb.append(" distinct");
		sb.append(" rtrim(issuetype) as issuetype ");
		//sb.append(" rtrim(issue_access) as issueaccess,");
		//sb.append(" rtrim(issue_source) as issuesource");
		sb.append(" from");
		sb.append(" " + ISMGTSCHEMA + ".ets_dropdown_data");
		sb.append(" where");
		sb.append(" project_id='" + qryProjectId + "' ");
		sb.append(" and issue_class='" + qryIssueClass + "' ");

		if (!qryIssueAccess.equals("IBM")) {

			sb.append(" and issue_access like 'ALL%' ");

		}

		sb.append(" and active_flag='" + qryActiveFlag + "' ");

		if (qryIssueSource.equals(STDETSOLD)) {

			sb.append(" and issue_source='" + qryIssueSource + "' ");

		}

		sb.append(" order by 1");
		sb.append(" for read only");

		Global.println("getChangeTypes qry =" + sb.toString() + ":");

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					EtsDropDownDataBean dropBean = new EtsDropDownDataBean();
					dropBean.setIssueType(EtsIssFilterUtils.getTrimStr(rs.getString("ISSUETYPE")));
					//dropBean.setIssueAccess(EtsIssFilterUtils.getTrimStr(rs.getString("ISSUEACCESS")));
					//dropBean.setIssueSource(EtsIssFilterUtils.getTrimStr(rs.getString("ISSUESOURCE")));

					dropDownList.add(dropBean);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return dropDownList;
	}

	/**
	 * To get the dynamic drop down valsdata
	 * 
	 */

	public ArrayList getIssueTypeSrcAccessStr(EtsDropDownDataBean dropModel) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		ArrayList dropDownList = new ArrayList();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		int count = 0;

		//qry vars
		String qryProjectId = dropModel.getProjectId();
		String qryIssueClass = dropModel.getIssueClass();
		String qryIssueAccess = dropModel.getIssueAccess();
		String qryActiveFlag = dropModel.getActiveFlag();
		String qryIssueType = dropModel.getIssueType();

		//qry//
		sb.append("select ");
		sb.append(" distinct");
		sb.append(" rtrim(issuetype) as issuetype, ");
		sb.append(" rtrim(issue_access) as issueaccess, ");
		sb.append(" rtrim(issue_source) as issuesource  ");
		sb.append(" from");
		sb.append(" " + ISMGTSCHEMA + ".ets_dropdown_data");
		sb.append(" where");
		sb.append(" project_id='" + qryProjectId + "' ");
		sb.append(" and issue_class='" + qryIssueClass + "' ");

		if (!qryIssueAccess.equals("IBM")) {

			sb.append(" and issue_access like 'ALL%' ");

		}

		sb.append(" and active_flag='" + qryActiveFlag + "' ");
		sb.append(" and issuetype='" + qryIssueType + "' ");

		sb.append(" order by 1");
		sb.append(" for read only");

		Global.println("getIssueTypes Access/source qry =" + sb.toString() + ":");

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					EtsDropDownDataBean dropBean = new EtsDropDownDataBean();

					dropBean.setIssueType(EtsIssFilterUtils.getTrimStr(rs.getString("ISSUETYPE")));
					dropBean.setIssueAccess(EtsIssFilterUtils.getTrimStr(rs.getString("ISSUEACCESS")));
					dropBean.setIssueSource(EtsIssFilterUtils.getTrimStr(rs.getString("ISSUESOURCE")));

					dropDownList.add(dropBean);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return dropDownList;
	}

	/**
	 * 
	 * @param dropModel
	 * @param conn
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public boolean insertEtsDropDownData(EtsDropDownDataBean dropModel, Connection conn) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		PreparedStatement pstmt = null;
		int inscount = 0;
		boolean flag = false;

		try {

			String dataId = dropModel.getDataId();
			String projectId = dropModel.getProjectId();
			String projectName = dropModel.getProjectName();
			String issueClass = dropModel.getIssueClass();
			String issueType = dropModel.getIssueType();
			String subTypeA = dropModel.getSubTypeA();
			String subTypeB = dropModel.getSubTypeB();
			String subTypeC = dropModel.getSubTypeC();
			String subTypeD = dropModel.getSubTypeD();
			String issueSource = dropModel.getIssueSource();
			String issueAccess = dropModel.getIssueAccess();
			String activeFlag = dropModel.getActiveFlag();
			String issueEtsR1 = dropModel.getIssueEtsR1();
			String issueEtsR2 = dropModel.getIssueEtsR2();
			String lastUserId = dropModel.getLastUserId();

			sb.append("INSERT INTO " + ISMGTSCHEMA + ".ETS_DROPDOWN_DATA (DATA_ID, PROJECT_ID, PROJECT_NAME, ISSUE_CLASS, ISSUETYPE, SUBTYPE_A, SUBTYPE_B, SUBTYPE_C, SUBTYPE_D, ISSUE_SOURCE,");
			sb.append(" ISSUE_ACCESS, ETS_R1, ETS_R2, ACTIVE_FLAG, LAST_USERID, LAST_TIMESTAMP) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,current timestamp)");

			pstmt = conn.prepareStatement(sb.toString());

			pstmt.clearParameters();

			pstmt.setString(1, dataId);
			pstmt.setString(2, projectId);
			pstmt.setString(3, projectName);
			pstmt.setString(4, issueClass);
			pstmt.setString(5, issueType);
			pstmt.setString(6, subTypeA);
			pstmt.setString(7, subTypeB);
			pstmt.setString(8, subTypeC);
			pstmt.setString(9, subTypeD);
			pstmt.setString(10, issueSource);
			pstmt.setString(11, issueAccess);
			pstmt.setString(12, issueEtsR1);
			pstmt.setString(13, issueEtsR2);
			pstmt.setString(14, activeFlag);
			pstmt.setString(15, lastUserId);

			if (AmtCommonUtils.isResourceDefined(dataId) && AmtCommonUtils.isResourceDefined(issueType)) {

				inscount += pstmt.executeUpdate();

			}

			if (inscount > 0) {

				flag = true;
			}

		} finally {

			ETSDBUtils.close(pstmt);
		}

		return flag;
	}

	/**
	 * 
	 * @param dropModel
	 * @param conn
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public boolean insertEtsOwnerData(EtsDropDownDataBean dropModel, Connection conn) throws SQLException, Exception {
		StringBuffer sb = new StringBuffer();
		PreparedStatement pstmt = null;
		EtsIssOwnerInfo backupOwnerInfo = null;
		int inscount = 0;
		boolean flag = false;
		int isBackupOwner = 0;
		try {
			String dataId = dropModel.getDataId();
			EtsIssOwnerInfo ownerInfo = dropModel.getOwnerInfo();

			String ownerId = "";
			String ownerEmail = "";
			if (ownerInfo != null) {
				ownerId = AmtCommonUtils.getTrimStr(ownerInfo.getUserEdgeId());
				ownerEmail = AmtCommonUtils.getTrimStr(ownerInfo.getUserEmail());
			}
			
			if(dropModel.getBackupOwnerInfo() != null ) 
				backupOwnerInfo = dropModel.getBackupOwnerInfo();			

			String backupOwnerId = "";
			String backupOwnerEmail = "";
			if (backupOwnerInfo != null) {
				backupOwnerId = AmtCommonUtils.getTrimStr(backupOwnerInfo.getUserEdgeId());
				backupOwnerEmail = AmtCommonUtils.getTrimStr(backupOwnerInfo.getUserEmail());

				if(AmtCommonUtils.isResourceDefined(backupOwnerId) &&  AmtCommonUtils.isResourceDefined(backupOwnerEmail) ) 
					isBackupOwner = 1;
			}

			String activeFlag = dropModel.getActiveFlag();
			String lastUserId = dropModel.getLastUserId();

			if( isBackupOwner == 1 ) {
				sb.append("INSERT INTO " + ISMGTSCHEMA + ".ETS_OWNER_DATA (DATA_ID, ISSUE_OWNER_ID, ISSUE_OWNER_EMAIL, ACTIVE_FLAG, LAST_USERID, ISSUE_BK_OWNER_ID, ISSUE_BK_OWNER_EMAIL, LAST_TIMESTAMP)");
				sb.append(" VALUES(?,?,?,?,?,?,?,current timestamp)");				
			} else {
				sb.append("INSERT INTO " + ISMGTSCHEMA + ".ETS_OWNER_DATA (DATA_ID, ISSUE_OWNER_ID, ISSUE_OWNER_EMAIL, ACTIVE_FLAG, LAST_USERID, LAST_TIMESTAMP)");
				sb.append(" VALUES(?,?,?,?,?,current timestamp)");				
			}
			
			pstmt = conn.prepareStatement(sb.toString());

			pstmt.clearParameters();

			pstmt.setString(1, dataId);
			pstmt.setString(2, ownerId);
			pstmt.setString(3, ownerEmail);
			pstmt.setString(4, activeFlag);
			pstmt.setString(5, lastUserId);
			if( isBackupOwner == 1 ) {
				pstmt.setString(6, backupOwnerId);
				pstmt.setString(7, backupOwnerEmail);				
			}

			if (AmtCommonUtils.isResourceDefined(dataId) && AmtCommonUtils.isResourceDefined(ownerId) && AmtCommonUtils.isResourceDefined(ownerEmail)) {

				inscount += pstmt.executeUpdate();
			}

			if (inscount > 0) {

				flag = true;
			}

		} finally {

			ETSDBUtils.close(pstmt);
		}

		return flag;
	}

	/**
		 * 
		 * @param dropModel
		 * @param conn
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public boolean addIssueType(EtsDropDownDataBean dropModel) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		Connection conn = null;
		int inscount = 0;
		boolean flag = false;

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);

			conn.setAutoCommit(false);

			if (insertEtsDropDownData(dropModel, conn) && insertEtsOwnerData(dropModel, conn) ) {

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

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in addIssueType", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in addIssueType", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		} finally {

			ETSDBUtils.close(conn);

		}

		return flag;

	}

	/**
	 * 
	 * @param projectId
	 * @param issueClass
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public boolean isIssueTypeExistsForProj(EtsIssTypeInfoModel issTypeModel) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		int count = 0;
		Connection conn = null;
		boolean flag = false;

		try {

			String projectId = AmtCommonUtils.getTrimStr(issTypeModel.getProjectId());
			String issueClass = AmtCommonUtils.getTrimStr(issTypeModel.getIssueClass());
			String issueType = AmtCommonUtils.getTrimStr(issTypeModel.getIssueType());

			sb.append("SELECT COUNT(DATA_ID) FROM " + ISMGTSCHEMA + ".ETS_DROPDOWN_DATA");
			sb.append(" WHERE");
			sb.append(" PROJECT_ID='" + projectId + "' ");
			sb.append(" AND ISSUE_CLASS='" + issueClass + "' ");
			sb.append(" AND ISSUETYPE ='" + issueType + "' ");
			sb.append(" FOR READ ONLY");

			conn = ETSDBUtils.getConnection();

			count = AmtCommonUtils.getRecCount(conn, sb.toString());

			if (count > 0) {

				flag = true;

			}

		} finally {

			ETSDBUtils.close(conn);
		}

		return flag;

	}

	/**
	 * Take all the issue types from drop_down_data table, which are not there in problem_info_usr1 table and laos
	 * take all issue types from drop_down_table, which are present in cq1, with Closed and Withdrawn state
	 * @param issTypeModel
	 * @return
	 */

	public ArrayList getDeleteIssueTypesList(EtsIssTypeInfoModel issTypeModel) throws SQLException, Exception {

		ArrayList delList = new ArrayList();

		StringBuffer sb = new StringBuffer();
		Connection conn = null;

		Statement stmt = null;
		ResultSet rs = null;
		int count = 0;

		try {

			String projectId = AmtCommonUtils.getTrimStr(issTypeModel.getProjectId());
			String issueClass = AmtCommonUtils.getTrimStr(issTypeModel.getIssueClass());

			//sb.append("select data_id,issuetype from " + ISMGTSCHEMA + ".ets_dropdown_data");
			sb.append("select distinct issuetype from " + ISMGTSCHEMA + ".ets_dropdown_data");
			sb.append(" where ");
			sb.append(" issue_class='" + issueClass + "' ");
			sb.append(" and project_id='" + projectId + "' ");
			sb.append(" and active_flag='Y' ");
			sb.append(" order by 1");
			sb.append(" for read only");

			Global.println("DELETE ISSUE TYPE QRY===" + sb.toString());

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					EtsDropDownDataBean dropBean = new EtsDropDownDataBean();
					//dropBean.setDataId(EtsIssFilterUtils.getTrimStr(rs.getString("DATA_ID")));
					dropBean.setIssueType(EtsIssFilterUtils.getTrimStr(rs.getString("ISSUETYPE")));
					
					delList.add(dropBean);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return delList;
	}

	/**
		 * 
		 * @param projectId
		 * @param issueClass
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public String getIssueTypeFromDataId(String dataId) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		String issueType = "";
		Connection conn = null;

		try {

			sb.append("SELECT ISSUETYPE FROM " + ISMGTSCHEMA + ".ETS_DROPDOWN_DATA");
			sb.append(" WHERE");
			sb.append(" DATA_ID='" + dataId + "' ");
			sb.append(" FOR READ ONLY");

			conn = ETSDBUtils.getConnection();

			issueType = AmtCommonUtils.getTrimStr(AmtCommonUtils.getValue(conn, sb.toString()));

		} finally {

			ETSDBUtils.close(conn);
		}

		return issueType;

	}

	/**
			 * 
			 * @param projectId
			 * @param issueClass
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	public String getDataIdFromIssueType(String projectId, String issueClass, String issueType) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		String dataId = "";
		Connection conn = null;

		try {

			sb.append("SELECT DATA_ID FROM " + ISMGTSCHEMA + ".ETS_DROPDOWN_DATA");
			sb.append(" WHERE");
			sb.append(" PROJECT_ID='" + projectId + "' ");
			sb.append(" AND ISSUE_CLASS='" + issueClass + "' ");
			sb.append(" AND ISSUETYPE='" + issueType + "' ");
			sb.append(" FOR READ ONLY");

			conn = ETSDBUtils.getConnection();

			dataId = AmtCommonUtils.getTrimStr(AmtCommonUtils.getValue(conn, sb.toString()));

		} finally {

			ETSDBUtils.close(conn);
		}

		return dataId;

	}

	/**
			 * 
			 * @param dropModel
			 * @param conn
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	public boolean deleteIssueType(EtsIssTypeInfoModel issTypeModel) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		Connection conn = null;
		int inscount = 0;
		boolean flag = false;

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);

			conn.setAutoCommit(false);

			if (deleteOwnerData(issTypeModel, conn) && deleteDropDownData(issTypeModel, conn)) {

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

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in addIssueType", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in addIssueType", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		} finally {

			ETSDBUtils.close(conn);

		}

		return flag;

	}

	/**
				 * 
				 * @param projectId
				 * @param issueClass
				 * @return
				 * @throws SQLException
				 * @throws Exception
				 */

	public boolean deleteDropDownData(EtsIssTypeInfoModel issTypeModel, Connection conn) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();

		Statement stmt = null;
		int count = 0;

		boolean flag = false;

		try {

			stmt = conn.createStatement();

			String projectId = AmtCommonUtils.getTrimStr(issTypeModel.getProjectId());
			String issueClass = AmtCommonUtils.getTrimStr(issTypeModel.getIssueClass());
			String issueType = AmtCommonUtils.getTrimStr((String) issTypeModel.getPrevIssueTypeList().get(0));

			//sb.append("DELETE FROM " + ISMGTSCHEMA + ".ETS_DROPDOWN_DATA");
			sb.append("UPDATE  " + ISMGTSCHEMA + ".ETS_DROPDOWN_DATA");
			sb.append(" SET ACTIVE_FLAG='N' ");
			sb.append(" WHERE");
			sb.append(" ISSUETYPE='" + issueType + "' ");
			sb.append(" and PROJECT_ID='" + projectId + "' ");
			sb.append(" and ISSUE_CLASS='" + issueClass + "' ");

			Global.println("DELETE ISSUE TYPE QRY====" + sb.toString());

			count += stmt.executeUpdate(sb.toString());

			if (count > 0) {

				flag = true;
			}

		} finally {

			ETSDBUtils.close(stmt);

		}

		return flag;

	}

	/**
					 * 
					 * @param projectId
					 * @param issueClass
					 * @return
					 * @throws SQLException
					 * @throws Exception
					 */

	public boolean deleteOwnerData(EtsIssTypeInfoModel issTypeModel, Connection conn) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();

		Statement stmt = null;
		int count = 0;

		boolean flag = false;

		try {

			stmt = conn.createStatement();

			String projectId = AmtCommonUtils.getTrimStr(issTypeModel.getProjectId());
			String issueClass = AmtCommonUtils.getTrimStr(issTypeModel.getIssueClass());
			String issueType = AmtCommonUtils.getTrimStr((String) issTypeModel.getPrevIssueTypeList().get(0));

			//sb.append("DELETE FROM " + ISMGTSCHEMA + ".ETS_OWNER_DATA");
			sb.append(" UPDATE " + ISMGTSCHEMA + ".ETS_OWNER_DATA");
			sb.append(" SET ACTIVE_FLAG='N' ");
			sb.append(" WHERE");
			sb.append(" DATA_ID IN ");
			sb.append(" ( SELECT DATA_ID FROM " + ISMGTSCHEMA + ".ETS_DROPDOWN_DATA");
			sb.append("       WHERE ");
			sb.append(" 		 ISSUETYPE='" + issueType + "' ");
			sb.append(" 		 and PROJECT_ID='" + projectId + "' ");
			sb.append("          and ISSUE_CLASS='" + issueClass + "' ");
			sb.append(" ) ");

			Global.println("DELETE ISSUE TYPE OWNER QRY====" + sb.toString());

			count += stmt.executeUpdate(sb.toString());

			if (count > 0) {

				flag = true;
			}

		} finally {

			ETSDBUtils.close(stmt);

		}

		return flag;

	}

	/**
					 * 
					 * @param projectId
					 * @param issueClass
					 * @return
					 * @throws SQLException
					 * @throws Exception
					 */

	public EtsIssTypeInfoModel getEtsIssueTypeInfoDetails(String projectId, String issueClass, String issueType, Connection conn) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();

		Statement stmt = null;
		ResultSet rs = null;

		EtsIssTypeInfoModel dbIssTypeModel = new EtsIssTypeInfoModel();
		EtsIssProjectMember projMem = new EtsIssProjectMember();
		String dataId = "";

		try {

			stmt = conn.createStatement();

			sb.append("SELECT DATA_ID, PROJECT_ID, PROJECT_NAME, ISSUE_CLASS, ISSUETYPE, ");
			sb.append(" SUBTYPE_A, SUBTYPE_B, SUBTYPE_C, SUBTYPE_D, ISSUE_SOURCE, ISSUE_ACCESS, ");
			sb.append(" ETS_R1, ETS_R2, ACTIVE_FLAG, LAST_USERID,LAST_TIMESTAMP");
			sb.append(" FROM " + ISMGTSCHEMA + ".ETS_DROPDOWN_DATA");
			sb.append(" WHERE");
			sb.append(" ISSUETYPE='" + issueType + "' ");
			sb.append(" AND PROJECT_ID='" + projectId + "' ");
			sb.append(" AND ISSUE_CLASS='" + issueClass + "' ");
			sb.append(" FOR READ ONLY");

			Global.println("SELECT ISSUE TYPE QRY====" + sb.toString());

			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					dataId = EtsIssFilterUtils.getTrimStr(rs.getString("DATA_ID"));

					dbIssTypeModel.setDataId(dataId);
					dbIssTypeModel.setIssueType(EtsIssFilterUtils.getTrimStr(rs.getString("ISSUETYPE")));
					dbIssTypeModel.setIssueSource(EtsIssFilterUtils.getTrimStr(rs.getString("ISSUE_SOURCE")));
					dbIssTypeModel.setIssueAccess(EtsIssFilterUtils.getTrimStr(rs.getString("ISSUE_ACCESS")));

				}

			}

			projMem = getEtsOwnerDetails(dataId, conn);

			dbIssTypeModel.setOwnerProfile(projMem);

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);

		}

		return dbIssTypeModel;

	}

	/**
						 * 
						 * @param projectId
						 * @param issueClass
						 * @return
						 * @throws SQLException
						 * @throws Exception
						 */

	public EtsIssTypeInfoModel getEtsIssueTypeInfoDetails(String dataId, Connection conn) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();

		Statement stmt = null;
		ResultSet rs = null;

		EtsIssTypeInfoModel dbIssTypeModel = new EtsIssTypeInfoModel();
		EtsIssProjectMember projMem = new EtsIssProjectMember();
		EtsIssProjectMember bkProjMem = new EtsIssProjectMember();

		try {

			stmt = conn.createStatement();

			sb.append("SELECT DATA_ID, PROJECT_ID, PROJECT_NAME, ISSUE_CLASS, ISSUETYPE, ");
			sb.append(" SUBTYPE_A, SUBTYPE_B, SUBTYPE_C, SUBTYPE_D, ISSUE_SOURCE, ISSUE_ACCESS, ");
			sb.append(" ETS_R1, ETS_R2, ACTIVE_FLAG, LAST_USERID,LAST_TIMESTAMP");
			sb.append(" FROM " + ISMGTSCHEMA + ".ETS_DROPDOWN_DATA");
			sb.append(" WHERE");
			sb.append(" DATA_ID='" + dataId + "' ");
			sb.append(" FOR READ ONLY");

			Global.println("SELECT ISSUE TYPE DATA ID QRY====" + sb.toString());

			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					dbIssTypeModel.setDataId(dataId);
					dbIssTypeModel.setIssueType(EtsIssFilterUtils.getTrimStr(rs.getString("ISSUETYPE")));
					dbIssTypeModel.setIssueSource(EtsIssFilterUtils.getTrimStr(rs.getString("ISSUE_SOURCE")));
					dbIssTypeModel.setIssueAccess(EtsIssFilterUtils.getTrimStr(rs.getString("ISSUE_ACCESS")));

				}

			}

			projMem = getEtsOwnerDetails(dataId, conn);
			bkProjMem = getEtsBackupOwnerDetails(dataId,conn);
			
			if(AmtCommonUtils.isResourceDefined(bkProjMem.getUserEdgeId() ) )
				dbIssTypeModel.setBackupOwnershipInternal("ALL");
			
			dbIssTypeModel.setOwnerProfile(projMem);
			dbIssTypeModel.setBackupOwnerProfile(bkProjMem);

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);

		}

		return dbIssTypeModel;

	}

	/**
						 * 
						 * @param projectId
						 * @param issueClass
						 * @return
						 * @throws SQLException
						 * @throws Exception
						 */

	public EtsIssProjectMember getEtsOwnerDetails(String dataId, Connection conn) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();

		Statement stmt = null;
		ResultSet rs = null;

		EtsIssProjectMember projMem = new EtsIssProjectMember();
		CommonInfoDAO commDao = new CommonInfoDAO();
		String issueOwnerId = "";
		String ownerEmail = "";

		try {

			stmt = conn.createStatement();

			sb.append("SELECT ISSUE_OWNER_ID, ISSUE_OWNER_EMAIL");
			sb.append(" FROM " + ISMGTSCHEMA + ".ETS_OWNER_DATA");
			sb.append(" WHERE");
			sb.append(" DATA_ID='" + dataId + "' ");
			sb.append(" FOR READ ONLY");

			Global.println("SELECT owner details QRY====" + sb.toString());

			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					issueOwnerId = AmtCommonUtils.getTrimStr(rs.getString("ISSUE_OWNER_ID"));
					ownerEmail = AmtCommonUtils.getTrimStr(rs.getString("ISSUE_OWNER_EMAIL"));

				}

			}

			projMem = commDao.getUserDetailsInfo(conn, issueOwnerId);

			//
			projMem.setUserEdgeId(issueOwnerId);
			projMem.setUserEmail(ownerEmail);

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);

		}

		return projMem;

	}

	/**
	 * 
	 * @param dataId
	 * @param conn
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public EtsIssProjectMember getEtsBackupOwnerDetails(String dataId, Connection conn) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();

		Statement stmt = null;
		ResultSet rs = null;

		EtsIssProjectMember projMem = new EtsIssProjectMember();
		CommonInfoDAO commDao = new CommonInfoDAO();
		String issueBackupOwnerId = "";
		String backupOwnerEmail = "";

		try {

			stmt = conn.createStatement();

			sb.append("SELECT ISSUE_BK_OWNER_ID, ISSUE_BK_OWNER_EMAIL");
			sb.append(" FROM " + ISMGTSCHEMA + ".ETS_OWNER_DATA");
			sb.append(" WHERE");
			sb.append(" DATA_ID='" + dataId + "' ");
			sb.append(" FOR READ ONLY");

			Global.println("SELECT BACKUPOWNER DETAILS QRY====" + sb.toString());

			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					issueBackupOwnerId = AmtCommonUtils.getTrimStr(rs.getString("ISSUE_BK_OWNER_ID"));
					backupOwnerEmail = AmtCommonUtils.getTrimStr(rs.getString("ISSUE_BK_OWNER_EMAIL"));

				}

			}
			if(AmtCommonUtils.isResourceDefined(issueBackupOwnerId) ) {
				projMem = commDao.getUserDetailsInfo(conn, issueBackupOwnerId);
				
				projMem.setUserEdgeId(issueBackupOwnerId);
				projMem.setUserEmail(backupOwnerEmail);
			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);

		}

		return projMem;

	}
	
	
	/**
	 * 
	 * @param issTypeModel
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public EtsIssTypeInfoModel getEtsIssueTypeInfoDetails(String projectId, String issueClass, String issueType) throws SQLException, Exception {

		EtsIssTypeInfoModel issTypeModel = new EtsIssTypeInfoModel();
		Connection conn = null;

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);

			issTypeModel = getEtsIssueTypeInfoDetails(projectId, issueClass, issueType, conn);

		} finally {

			ETSDBUtils.close(conn);

		}

		return issTypeModel;
	}

	/**
		 * 
		 * @param issTypeModel
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public EtsIssTypeInfoModel getEtsIssueTypeInfoDetails(String dataId) throws SQLException, Exception {

		EtsIssTypeInfoModel issTypeModel = new EtsIssTypeInfoModel();
		Connection conn = null;

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);

			issTypeModel = getEtsIssueTypeInfoDetails(dataId, conn);

		} finally {

			ETSDBUtils.close(conn);

		}

		return issTypeModel;
	}

	/**
	 * To get the update issue type list
	 * @param issTypeModel
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public ArrayList getUpdateIssueTypesList(EtsIssTypeInfoModel issTypeModel) throws SQLException, Exception {

		ArrayList delList = new ArrayList();

		StringBuffer sb = new StringBuffer();
		Connection conn = null;

		Statement stmt = null;
		ResultSet rs = null;
		int count = 0;

		try {

			String projectId = AmtCommonUtils.getTrimStr(issTypeModel.getProjectId());
			String issueClass = AmtCommonUtils.getTrimStr(issTypeModel.getIssueClass());

			sb.append("select data_id,issuetype from " + ISMGTSCHEMA + ".ets_dropdown_data");
			sb.append(" where ");
			sb.append(" issue_class='" + issueClass + "' ");
			sb.append(" and project_id='" + projectId + "' ");
			sb.append(" and active_flag='Y' ");
			sb.append(" order by 2");
			sb.append(" for read only");

			Global.println("UPDATE ISSUE TYPE QRY===" + sb.toString());

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					EtsDropDownDataBean dropBean = new EtsDropDownDataBean();
					dropBean.setDataId(EtsIssFilterUtils.getTrimStr(rs.getString("DATA_ID")));
					dropBean.setIssueType(EtsIssFilterUtils.getTrimStr(rs.getString("ISSUETYPE")));

					delList.add(dropBean);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return delList;
	}

	/**
			 * 
			 * @param dropModel
			 * @param conn
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	public boolean updIssueType(EtsDropDownDataBean dropModel) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		Connection conn = null;
		int inscount = 0;
		boolean flag = false;

		try {

			conn = ETSDBUtils.getConnection(ETSDATASRC);

			conn.setAutoCommit(false);

			if (updateEtsDropDownData(dropModel, conn) && updateEtsOwnerData(dropModel, conn)) {

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

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in addIssueType", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in addIssueType", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		} finally {

			ETSDBUtils.close(conn);

		}

		return flag;

	}

	/**
		 * 
		 * @param dropModel
		 * @param conn
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public boolean updateEtsDropDownData(EtsDropDownDataBean dropModel, Connection conn) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		PreparedStatement pstmt = null;
		int inscount = 0;
		boolean flag = false;

		try {

			String dataId = dropModel.getDataId();
			String projectId = dropModel.getProjectId();
			String projectName = dropModel.getProjectName();
			String issueClass = dropModel.getIssueClass();
			String issueType = dropModel.getIssueType();
			String subTypeA = dropModel.getSubTypeA();
			String subTypeB = dropModel.getSubTypeB();
			String subTypeC = dropModel.getSubTypeC();
			String subTypeD = dropModel.getSubTypeD();
			String issueSource = dropModel.getIssueSource();
			String issueAccess = dropModel.getIssueAccess();
			String activeFlag = dropModel.getActiveFlag();
			String issueEtsR1 = dropModel.getIssueEtsR1();
			String issueEtsR2 = dropModel.getIssueEtsR2();
			String lastUserId = dropModel.getLastUserId();

			sb.append("UPDATE " + ISMGTSCHEMA + ".ETS_DROPDOWN_DATA");
			sb.append(" SET");
			sb.append(" ISSUETYPE=?, ");
			sb.append(" ISSUE_ACCESS=?, ");
			sb.append(" LAST_USERID=?, ");
			sb.append(" LAST_TIMESTAMP=current timestamp ");
			sb.append(" WHERE");
			sb.append(" DATA_ID=?");

			pstmt = conn.prepareStatement(sb.toString());

			pstmt.clearParameters();

			pstmt.setString(1, issueType);
			pstmt.setString(2, issueAccess);
			pstmt.setString(3, lastUserId);
			pstmt.setString(4, dataId);

			if (AmtCommonUtils.isResourceDefined(dataId)) {

				inscount += pstmt.executeUpdate();

			}

			if (inscount > 0) {

				flag = true;
			}

		} finally {

			ETSDBUtils.close(pstmt);
		}

		return flag;
	}

	/**
			 * 
			 * @param dropModel
			 * @param conn
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	public boolean updateEtsOwnerData(EtsDropDownDataBean dropModel, Connection conn) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		PreparedStatement pstmt = null;
		int inscount = 0;
		boolean flag = false;
		boolean isBackupOwner = false;
		
		try {

			String dataId = dropModel.getDataId();
			EtsIssOwnerInfo ownerInfo = dropModel.getOwnerInfo();
				
			String ownerId = "";
			String ownerEmail = "";

			if (ownerInfo != null) {

				ownerId = AmtCommonUtils.getTrimStr(ownerInfo.getUserEdgeId());
				ownerEmail = AmtCommonUtils.getTrimStr(ownerInfo.getUserEmail());

			}

			EtsIssOwnerInfo backupOwnerInfo = dropModel.getBackupOwnerInfo();
		
			String backupOwnerId = "";
			String backupOwnerEmail = "";			
			
			if (backupOwnerInfo != null) {
				backupOwnerId = AmtCommonUtils.getTrimStr(backupOwnerInfo.getUserEdgeId());
				backupOwnerEmail = AmtCommonUtils.getTrimStr(backupOwnerInfo.getUserEmail());
				isBackupOwner = true;
			}
						

			String activeFlag = dropModel.getActiveFlag();
			String lastUserId = dropModel.getLastUserId();

			sb.append("UPDATE " + ISMGTSCHEMA + ".ETS_OWNER_DATA ");
			sb.append(" set");
			sb.append(" ISSUE_OWNER_ID=?, ");
			sb.append(" ISSUE_OWNER_EMAIL=?, ");
			if(isBackupOwner) {
				sb.append(" ISSUE_BK_OWNER_ID=?, ");
				sb.append(" ISSUE_BK_OWNER_EMAIL=?, ");			
			}
			sb.append(" LAST_USERID=?, ");
			sb.append("LAST_TIMESTAMP=current timestamp");
			sb.append(" WHERE");
			sb.append(" DATA_ID=? ");

			Global.println("UPDATE ISSUE TYPE OWNER DATA QRY === " + sb.toString());
						
			pstmt = conn.prepareStatement(sb.toString());

			pstmt.clearParameters();

			pstmt.setString(1, ownerId);
			pstmt.setString(2, ownerEmail);
			if(isBackupOwner) {
				pstmt.setString(3, backupOwnerId);
				pstmt.setString(4, backupOwnerEmail);
				pstmt.setString(5, lastUserId);
				pstmt.setString(6, dataId);				
			} else {
				pstmt.setString(3, lastUserId);
				pstmt.setString(4, dataId);
			}
			if (AmtCommonUtils.isResourceDefined(dataId) && AmtCommonUtils.isResourceDefined(ownerId) && AmtCommonUtils.isResourceDefined(ownerEmail)) {

				inscount += pstmt.executeUpdate();

			}

			if (inscount > 0) {

				flag = true;
			}

		} finally {

			ETSDBUtils.close(pstmt);
		}

		return flag;
	}

	/**
		 * Take all the issue types from drop_down_data table, which are not there in problem_info_usr1 table and laos
		 * take all issue types from drop_down_table, which are present in cq1, with Closed and Withdrawn state
		 * @param issTypeModel
		 * @return
		 */

	public ArrayList getProbStateList(EtsIssTypeInfoModel issTypeModel) throws SQLException, Exception {

		ArrayList probStateList = new ArrayList();

		StringBuffer sb = new StringBuffer();
		Connection conn = null;

		Statement stmt = null;
		ResultSet rs = null;
		int count = 0;

		String probState = "";

		try {

			String projectId = AmtCommonUtils.getTrimStr(issTypeModel.getProjectId());
			String issueClass = AmtCommonUtils.getTrimStr(issTypeModel.getIssueClass());
			String issueType = AmtCommonUtils.getTrimStr(issTypeModel.getIssueType());

			//sb.append("select data_id,issuetype from " + ISMGTSCHEMA + ".ets_dropdown_data");
			sb.append("select distinct problem_state as problemstate from " + ISMGTSCHEMA + ".problem_info_cq1 a");
			sb.append(" where ");
			sb.append(" a.problem_class='" + issueClass + "' ");
			sb.append(" and a.project_id = ");
			sb.append("       (");
			sb.append("       select ets_project_id from " + ISMGTSCHEMA + ".problem_info_usr1 x ");
			sb.append("        where ");
			sb.append("         x.edge_problem_id=a.edge_problem_id");
			sb.append("         and x.problem_class=a.problem_class");
			sb.append("        ) ");
			sb.append(" and a.issue_type_id in (");
			sb.append("        select data_id from " + ISMGTSCHEMA + ".ets_dropdown_data");
			sb.append("         where");
			sb.append("         project_id='" + projectId + "' ");
			sb.append("         and issue_class='" + issueClass + "' ");
			sb.append("         and issuetype='" + issueType + "' ");
			sb.append(" 		and active_flag='Y' ");
			sb.append("                        )");
			sb.append(" order by 1");
			sb.append(" for read only");

			Global.println("DELETE ISSUE TYPE QRY===" + sb.toString());

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					probState = AmtCommonUtils.getTrimStr(rs.getString("PROBLEMSTATE"));
					probStateList.add(probState);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return probStateList;
	}

	/**
	 * 
	 * @param dataId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public boolean isIssueTypeIdActive(String dataId) throws SQLException, Exception {
		String activeFlag = "Y";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "select active_flag from " + ISMGTSCHEMA + ".ets_dropdown_data where data_id = '" + dataId + "' for read only";

			Global.println("ISSUE TYPE ID ACTIVE QRY==" + sql);
			conn = ETSDBUtils.getConnection();

			stmt = conn.createStatement();

			rs = stmt.executeQuery(sql);

			if (rs != null) {

				if (rs.next()) {

					activeFlag = AmtCommonUtils.getTrimStr(rs.getString("active_flag"));

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);
		}

		if (activeFlag.equals("Y")) {

			return true;
		}

		return false;

	}

	/**
			 * 
			 * @param projectId
			 * @param issueClass
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	public String getSTDPMOIssueType(String projectId) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		String issueType = "";
		Connection conn = null;

		try {

			sb.append("SELECT ISSUETYPE FROM " + ISMGTSCHEMA + ".ETS_DROPDOWN_DATA");
			sb.append(" WHERE");
			sb.append(" PROJECT_ID='" + projectId + "' ");
			sb.append(" AND ISSUE_SOURCE='ETSPMO'");
			sb.append(" AND ISSUE_CLASS='Defect' ");
			sb.append(" FOR READ ONLY");

			conn = ETSDBUtils.getConnection();

			issueType = AmtCommonUtils.getTrimStr(AmtCommonUtils.getValue(conn, sb.toString()));

		} finally {

			ETSDBUtils.close(conn);
		}

		return issueType;

	}

	/**
	 * 
	 * @param projectId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public ArrayList getIssueTypeOwnerInfoList(String projectId, String sortColumn, String sortOrder) throws SQLException, Exception {

		StringBuffer sb = new StringBuffer();
		ArrayList dropDownList = new ArrayList();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {

			sb.append("select ");
			sb.append(" a.issuetype as issuetype, ");
			sb.append(" a.issue_access as access, ");
			sb.append(" case when a.ISSUE_ACCESS='ALL:IBM' then 'All Team members' ");
			sb.append("      when a.ISSUE_ACCESS='ALL:EXT' then 'All Team members' ");
			sb.append("      when a.issue_access='IBM:IBM' then 'All IBM Team members only' end as issueaccess, ");
			sb.append(" a.issue_source as issuesource, ");
			sb.append(" b.issue_owner_email as owneremail, ");
			//sb.append(" c.user_fullname as ownername ");

			sb.append(" (select y.user_fullname from  amt.users y  where y.edge_userid=b.issue_owner_id) as ownername, ");
			sb.append(" b.issue_bk_owner_email as backupowneremail, "); 
			sb.append(" (select x.user_fullname from  amt.users x  where x.edge_userid=b.issue_bk_owner_id) as backupownername "); 
									
			sb.append(" from ets.ets_dropdown_data a,ets.ets_owner_data b ");
			sb.append(" where ");
			sb.append(" a.project_id='" + projectId + "' ");
			sb.append(" and a.issue_class='Defect' ");
			sb.append(" and a.data_id=b.data_id ");
			sb.append(" and a.active_flag='Y' ");
			sb.append(" and b.active_flag='Y' ");

			sb.append(" ORDER BY");

			//sort column

			if (AmtCommonUtils.isResourceDefined(sortColumn)) {

				sb.append(" " + sortColumn + " ");

			}

			//sort order

			if (AmtCommonUtils.isResourceDefined(sortOrder)) {

				sb.append(" " + sortOrder + " ");

			}

			Global.println("LIST ISSUE TYPE INFO QRY==" + sb.toString());

			if (logger.isDebugEnabled()) {

				logger.debug("LIST ISSUE TYPE INFO QRY==" + sb.toString());
			}

			conn = ETSDBUtils.getConnection(ETSDATASRC);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sb.toString());

			if (rs != null) {

				while (rs.next()) {

					EtsDropDownDataBean dropBean = new EtsDropDownDataBean();
					EtsIssOwnerInfo ownerInfo = new EtsIssOwnerInfo();
					EtsIssOwnerInfo bkupOwnerInfo = new EtsIssOwnerInfo(); 

					ownerInfo.setUserEmail(EtsIssFilterUtils.getTrimStr(rs.getString("OWNEREMAIL")));
					ownerInfo.setUserFullName(EtsIssFilterUtils.getTrimStr(rs.getString("OWNERNAME")));
					
					bkupOwnerInfo.setUserEmail(EtsIssFilterUtils.getTrimStr(rs.getString("BACKUPOWNEREMAIL")));
					bkupOwnerInfo.setUserFullName(EtsIssFilterUtils.getTrimStr(rs.getString("BACKUPOWNERNAME")));
					
					dropBean.setIssueType(EtsIssFilterUtils.getTrimStr(rs.getString("ISSUETYPE")));
					dropBean.setIssueAccess(EtsIssFilterUtils.getTrimStr(rs.getString("ISSUEACCESS")));
					dropBean.setIssueSource(EtsIssFilterUtils.getTrimStr(rs.getString("ISSUESOURCE")));

					dropBean.setOwnerInfo(ownerInfo);
					dropBean.setBackupOwnerInfo(bkupOwnerInfo);

					dropDownList.add(dropBean);

				}

			}

		} finally {

			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
			ETSDBUtils.close(conn);

		}

		return dropDownList;

	}

} //end of class
