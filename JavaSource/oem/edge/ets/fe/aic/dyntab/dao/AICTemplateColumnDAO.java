/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004                                     */
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

package oem.edge.ets.fe.aic.dyntab.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;

import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.aic.common.exception.AICDBException;
import oem.edge.ets.fe.aic.common.exception.AICDataAccessException;
import oem.edge.ets.fe.aic.common.exception.AICNoDataFoundException;
import oem.edge.ets.fe.aic.common.exception.AICOptimisticLockException;
import oem.edge.ets.fe.aic.common.helper.SQLCode;

import oem.edge.ets.fe.aic.common.util.AICDBUtils;
import oem.edge.ets.fe.aic.common.vo.ValueObject;
import oem.edge.ets.fe.aic.dyntab.vo.AICTemplateColumnVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTemplateVO;
import oem.edge.ets.fe.common.EtsLogger;

/**
 * @author vivek
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AICTemplateColumnDAO implements IAICTemplateColumnDAO {
	public final static String Copyright =
		"(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";

	private static Log logger = EtsLogger.getLogger(AICTemplateColumnDAO.class);

	public ValueObject findByPrimaryKey(ValueObject objValueObject)
		throws AICDataAccessException {
		if (logger.isInfoEnabled()) {
			logger.info("-> findByPrimaryKey");
		}
		AICTemplateVO objAICTemplateVO = (AICTemplateVO) objValueObject;

		Connection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		try {
			conn = AICDBUtils.getConnection();

			SQLCode sqlCode = SQLCode.getInstance();
			String strSQL =
				sqlCode.getSQLStatement(
					"dyntab.dao.AICTemplateColumnDAO.findByPrimaryKey");
			preparedStatement = conn.prepareStatement(strSQL.toString());
			preparedStatement.setString(1, objAICTemplateVO.getTemplateId());
			preparedStatement.setString(2, "Y");

			rs = preparedStatement.executeQuery();

			AICTemplateColumnVO objAICTemplateColumnVO = null;
			ArrayList templateColVOCollection = new ArrayList();
			boolean recordExists = false;
			while (rs.next()) {
				recordExists = true;
				objAICTemplateColumnVO = new AICTemplateColumnVO();
				objAICTemplateColumnVO.setColumnId(rs.getString("COLUMN_ID"));
				objAICTemplateColumnVO.setColumnName(
					rs.getString("COLUMN_NAME"));
				objAICTemplateColumnVO.setRequired(rs.getString("IS_REQUIRED"));
				objAICTemplateColumnVO.setColumnOrder(
					rs.getInt("COLUMN_ORDER"));
				objAICTemplateColumnVO.setActive(rs.getString("ISACTIVE"));
				objAICTemplateColumnVO.setColumnType(
					rs.getString("COLUMN_TYPE"));
				objAICTemplateColumnVO.setColumnUpdateDate(
					rs.getTimestamp("COLUMN_UPDATE_DATE"));
				objAICTemplateColumnVO.setTemplateId(
					rs.getString("TEMPLATE_ID"));
				templateColVOCollection.add(objAICTemplateColumnVO);

			}
			if (!recordExists) {
				throw new AICNoDataFoundException(
					"There are no columns defined for this template : "
						+ objAICTemplateVO.getTemplateName()
						+ ", You may  use the 'Add' button to Add Columns to this template");
			}
			objAICTemplateVO.setTemplateColVOCollection(
				templateColVOCollection);

		} catch (SQLException sql) {
			throw new AICDataAccessException(
				"Error in AICTemplateColumnDAO.findByPrimaryKey()",
				sql);
		} finally {
			try {

				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (preparedStatement != null) {
					preparedStatement.close();
					preparedStatement = null;
				}
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (SQLException sqle) {
				throw new AICDBException(
					"Unable to close resultset, database connection or statement in AICTemplateColumnDAO.findByPrimaryKey",
					sqle);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("<- findByPrimaryKey");
		}
		return objAICTemplateVO;

	}

	public ValueObject insert(ValueObject pValueObject)
		throws AICDataAccessException {
		if (logger.isInfoEnabled()) {
			logger.info("-> insert");
		}
		AICTemplateVO objAICTemplateVO = (AICTemplateVO) pValueObject;
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		try {
			conn = AICDBUtils.getConnection();

			SQLCode sqlCode = SQLCode.getInstance();
			String strSQL =
				sqlCode.getSQLStatement(
					"dyntab.dao.AICTemplateColumnDAO.insert");
			preparedStatement = conn.prepareStatement(strSQL.toString());

			ArrayList templateColVOCollection =
				(ArrayList) objAICTemplateVO.getTemplateColVOCollection();

			Iterator itTemplateColVOCollection =
				templateColVOCollection.iterator();
			AICTemplateColumnVO objAICTemplateColumnVO = null;
			while (itTemplateColVOCollection.hasNext()) {
				objAICTemplateColumnVO =
					(AICTemplateColumnVO) itTemplateColVOCollection.next();

				preparedStatement.setString(
					1,
					objAICTemplateVO.getTemplateId());
				preparedStatement.setString(
					2,
					objAICTemplateColumnVO.getColumnName());
				preparedStatement.setString(
					3,
					objAICTemplateColumnVO.getColumnId());
				preparedStatement.setString(
					4,
					objAICTemplateColumnVO.getRequired());
				preparedStatement.setString(5, "Y");
				preparedStatement.setInt(
					6,
					objAICTemplateColumnVO.getColumnOrder());
				preparedStatement.setString(
					7,
					objAICTemplateColumnVO.getColumnType());
				preparedStatement.setTimestamp(
					8,
					new Timestamp(System.currentTimeMillis()));

				preparedStatement.executeUpdate();

			}

		} catch (SQLException sql) {
			throw new AICDataAccessException(
				"Error in AICTemplateColumnDAO.insert()",
				sql);
		} finally {
			try {

				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				throw new AICDBException(
					"Unable to close resultset, database connection or statement in AICTemplateColumnDAO.insert",
					sqle);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("<- insert");
		}
		return objAICTemplateVO;
	}

	public ValueObject update(ValueObject pValueObject)
		throws AICDataAccessException, AICOptimisticLockException {
		if (logger.isInfoEnabled()) {
			logger.info("-> update");
		}

		AICTemplateVO objAICTemplateVO = (AICTemplateVO) pValueObject;
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		try {
			conn = AICDBUtils.getConnection();

			SQLCode sqlCode = SQLCode.getInstance();
			String strSQL =
				sqlCode.getSQLStatement(
					"dyntab.dao.AICTemplateColumnDAO.update");
			preparedStatement = conn.prepareStatement(strSQL.toString());

			ArrayList templateColVOCollection =
				(ArrayList) objAICTemplateVO.getTemplateColVOCollection();

			Iterator itTemplateColVOCollection =
				templateColVOCollection.iterator();
			AICTemplateColumnVO objAICTemplateColumnVO = null;
			while (itTemplateColVOCollection.hasNext()) {
				objAICTemplateColumnVO =
					(AICTemplateColumnVO) itTemplateColVOCollection.next();

				preparedStatement.setString(
					1,
					objAICTemplateColumnVO.getColumnName());
				preparedStatement.setString(
					2,
					objAICTemplateColumnVO.getRequired());
				preparedStatement.setString(
					3,
					objAICTemplateColumnVO.getActive());
				preparedStatement.setInt(
					4,
					objAICTemplateColumnVO.getColumnOrder());
				preparedStatement.setString(
					5,
					objAICTemplateColumnVO.getColumnType());
				preparedStatement.setTimestamp(
					6,
					new Timestamp(System.currentTimeMillis()));
				preparedStatement.setString(
					7,
					objAICTemplateVO.getTemplateId());
				preparedStatement.setString(
					8,
					objAICTemplateColumnVO.getColumnId());
				preparedStatement.executeUpdate();

			}

		} catch (SQLException sql) {
			throw new AICDataAccessException(
				"Error in AICTemplateColumnDAO.update()",
				sql);
		} finally {
			try {

				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				throw new AICDBException(
					"Unable to close resultset, database connection or statement in AICTemplateColumnDAO.update",
					sqle);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("<- update");
		}
		return objAICTemplateVO;
	}
	public void delete(ValueObject pValueObject)
		throws AICDataAccessException {
		if (logger.isInfoEnabled()) {
			logger.info("-> delete");
		}
		AICTemplateVO objAICTemplateVO = (AICTemplateVO) pValueObject;
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		try {
			conn = AICDBUtils.getConnection();

			SQLCode sqlCode = SQLCode.getInstance();
			String strSQL =
				sqlCode.getSQLStatement(
					"dyntab.dao.AICTemplateColumnDAO.delete");
			preparedStatement = conn.prepareStatement(strSQL.toString());

			ArrayList templateColVOCollection =
				(ArrayList) objAICTemplateVO.getTemplateColVOCollection();

			Iterator itTemplateColVOCollection =
				templateColVOCollection.iterator();
			AICTemplateColumnVO objAICTemplateColumnVO = null;
			while (itTemplateColVOCollection.hasNext()) {
				objAICTemplateColumnVO =
					(AICTemplateColumnVO) itTemplateColVOCollection.next();
				//preparedStatement.setString(1,objAICTemplateColumnVO.getActive());	
				preparedStatement.setString(1, "N");
				preparedStatement.setTimestamp(
					2,
					new Timestamp(System.currentTimeMillis()));
				preparedStatement.setString(
					3,
					objAICTemplateVO.getTemplateId());
				preparedStatement.setString(
					4,
					objAICTemplateColumnVO.getColumnId());
				preparedStatement.executeUpdate();

			}

		} catch (SQLException sql) {
			throw new AICDataAccessException(
				"Error in AICTemplateColumnDAO.delete()",
				sql);
		} finally {
			try {

				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				throw new AICDBException(
					"Unable to close resultset, database connection or statement in AICTemplateColumnDAO.delete",
					sqle);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("<- delete");
		}
	}

}
