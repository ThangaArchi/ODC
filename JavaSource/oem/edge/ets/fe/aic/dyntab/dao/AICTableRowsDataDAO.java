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
import oem.edge.ets.fe.aic.dyntab.vo.AICTableRowsDataVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTableVO;
import oem.edge.ets.fe.common.EtsLogger;

/**
 * @author thanga
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AICTableRowsDataDAO implements IAICTableRowsDataDAO {
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";
		
	private static Log logger = EtsLogger.getLogger(AICTableRowsDataDAO.class);
	public ValueObject findByPrimaryKey(ValueObject objValueObject)
		throws AICDataAccessException {
			
		if (logger.isInfoEnabled()) {
			logger.info("-> findByPrimaryKey");
		}
					
		AICTableVO objAICTableVO = (AICTableVO) objValueObject;

		Connection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		try {
			conn = AICDBUtils.getConnection();
			
			SQLCode sqlCode = SQLCode.getInstance();
			String strSQL =
				sqlCode.getSQLStatement(
					"dyntab.dao.AICTableRowsDataDAO.findByPrimaryKey");
			preparedStatement = conn.prepareStatement(strSQL.toString());

			preparedStatement.setString(1, "Y");
			preparedStatement.setString(2, "Y");
			preparedStatement.setString(3, objAICTableVO.getTableId());
			preparedStatement.setInt(4, objAICTableVO.getDocId());

			rs = preparedStatement.executeQuery();

			AICTableRowsDataVO objAICTableRowsDataVO = null;
			ArrayList tableColVOCollection = new ArrayList();
			
			boolean recordExists = false;
			
			while (rs.next()) {
				recordExists = true;
				objAICTableRowsDataVO = new AICTableRowsDataVO();
				objAICTableRowsDataVO.setActive("Y");
				//objAICTableRowsDataVO.setColumnId(rs.getString("R.COLUMN_ID"));
				objAICTableRowsDataVO.setColumnId(rs.getString("COLUMN_ID"));
				//objAICTableRowsDataVO.setColumnName(rs.getString("C.COLUMN_NAME"));
				objAICTableRowsDataVO.setColumnName(rs.getString("COLUMN_NAME"));
				objAICTableRowsDataVO.setColumnOrder(rs.getInt("COLUMN_ORDER"));
				//objAICTableRowsDataVO.setDataId(rs.getString("R.DATA_ID"));
				objAICTableRowsDataVO.setDataId(rs.getString("DATA_ID"));
				//objAICTableRowsDataVO.setRowId(rs.getString("R.ROW_ID"));
				objAICTableRowsDataVO.setRowId(rs.getString("ROW_ID"));
				//objAICTableRowsDataVO.setDataValue(rs.getString("R.DATA_VALUE"));
				objAICTableRowsDataVO.setDataValue(rs.getString("DATA_VALUE"));
				//objAICTableRowsDataVO.setRowUpdateDate(rs.getTimestamp("R.ROW_UPDATE_DATE"));
				objAICTableRowsDataVO.setRowUpdateDate(rs.getTimestamp("ROW_UPDATE_DATE"));
				objAICTableRowsDataVO.setColumnType(rs.getString("COLUMN_TYPE"));
				objAICTableRowsDataVO.setRequired(rs.getString("IS_REQUIRED"));
				objAICTableRowsDataVO.setTableId(objAICTableVO.getTableId());
				

				tableColVOCollection.add(objAICTableRowsDataVO);

			}
			if(!recordExists)
			{
				throw new AICNoDataFoundException("There are no rows added for Table : " + objAICTableVO.getTableName() );
			}
			objAICTableVO.setAICTableRowsDataCollection(tableColVOCollection);

		} catch (SQLException sql) {			
			throw new AICDataAccessException(
				"Error in AICTableRowsDataDAO.findByPrimaryKey()",
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
					"Unable to close resultset, database connection or statement in AICTableRowsDataDAO.findByPrimaryKey",
					sqle);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("<- findByPrimaryKey");
		}
		return objAICTableVO;
	}

	public ValueObject insert(ValueObject pValueObject)
		throws AICDataAccessException {
		if (logger.isInfoEnabled()) {
			logger.info("-> insert");
		}
					
		AICTableVO objAICTableVO = (AICTableVO) pValueObject;
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		try {
			conn = AICDBUtils.getConnection();

			SQLCode sqlCode = SQLCode.getInstance();
			String strSQL =
				sqlCode.getSQLStatement(
					"dyntab.dao.AICTableRowsDataDAO.insert");
			preparedStatement = conn.prepareStatement(strSQL.toString());

			ArrayList tableColVOCollection =
				(ArrayList) objAICTableVO.getAICTableRowsDataCollection();

			Iterator itTableColVOCollection = tableColVOCollection.iterator();

			AICTableRowsDataVO objAICTableRowsDataVO = null;
			while (itTableColVOCollection.hasNext()) {

				objAICTableRowsDataVO =
					(AICTableRowsDataVO) itTableColVOCollection.next();

				preparedStatement.setString(
					1,
					objAICTableRowsDataVO.getDataId());
				preparedStatement.setString(
					2,
					objAICTableRowsDataVO.getRowId());
				preparedStatement.setString(3, objAICTableVO.getTableId());
				preparedStatement.setString(
					4,
					objAICTableRowsDataVO.getColumnId());
				preparedStatement.setString(
					5,
					objAICTableRowsDataVO.getDataValue());
				preparedStatement.setString(
					6,
					"Y");
				preparedStatement.setTimestamp(
					7,
					new Timestamp(System.currentTimeMillis()));

				preparedStatement.executeUpdate();

			}

		} catch (SQLException sql) {
			throw new AICDataAccessException(
				"Error in AICTableRowsDataDAO.insert()",
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
					"Unable to close resultset, database connection or statement in AICTableRowsDataDAO.insert",
					sqle);
			}
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("<- insert");
		}
				
		return objAICTableVO;
	}

	public ValueObject update(ValueObject pValueObject)
		throws AICDataAccessException, AICOptimisticLockException {
		if (logger.isInfoEnabled()) {
					logger.info("-> update");
		}
					
		AICTableVO objAICTableVO = (AICTableVO) pValueObject;
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		try {
			conn = AICDBUtils.getConnection();

			SQLCode sqlCode = SQLCode.getInstance();
			String strSQL =
				sqlCode.getSQLStatement(
					"dyntab.dao.AICTableRowsDataDAO.update");
			preparedStatement = conn.prepareStatement(strSQL.toString());

			ArrayList tableColVOCollection =
				(ArrayList) objAICTableVO.getAICTableRowsDataCollection();

			Iterator itTableColVOCollection = tableColVOCollection.iterator();
			AICTableRowsDataVO objAICTableRowsDataVO = null;
			while (itTableColVOCollection.hasNext()) {
				objAICTableRowsDataVO =
					(AICTableRowsDataVO) itTableColVOCollection.next();

				preparedStatement.setString(
					1,
					objAICTableRowsDataVO.getDataValue());
				preparedStatement.setString(
					2,
					objAICTableRowsDataVO.getActive());
				preparedStatement.setTimestamp(
					3,
					new Timestamp(System.currentTimeMillis()));
				preparedStatement.setString(
					4,
					objAICTableRowsDataVO.getDataId());
				preparedStatement.setString(
					5,
					objAICTableRowsDataVO.getRowId());
				preparedStatement.setString(6, objAICTableVO.getTableId());

				preparedStatement.executeUpdate();

			}

		} catch (SQLException sql) {
			throw new AICDataAccessException(
				"Error in AICTableRowsDataDAO.update()",
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
					"Unable to close resultset, database connection or statement in AICTableRowsDataDAO.update",
					sqle);
			}
		}
		if (logger.isInfoEnabled()) {
					logger.info("<- update");
		}
		return objAICTableVO;
	}
	public void delete(ValueObject pValueObject)
		throws AICDataAccessException {
		if (logger.isInfoEnabled()) {
					logger.info("-> delete");
		}
					
		AICTableVO objAICTableVO = (AICTableVO) pValueObject;
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		try {
			conn = AICDBUtils.getConnection();

			SQLCode sqlCode = SQLCode.getInstance();
			String strSQL =
				sqlCode.getSQLStatement(
					"dyntab.dao.AICTableRowsDataDAO.delete");
			preparedStatement = conn.prepareStatement(strSQL.toString());

			ArrayList tableColVOCollection =
				(ArrayList) objAICTableVO.getAICTableRowsDataCollection();

			Iterator itTableColVOCollection = tableColVOCollection.iterator();
			AICTableRowsDataVO objAICTableRowsDataVO = null;			
			while (itTableColVOCollection.hasNext()) {
				objAICTableRowsDataVO =	(AICTableRowsDataVO) itTableColVOCollection.next();
				//preparedStatement.setString(1,objAICTableRowsDataVO.getActive());
				preparedStatement.setString(1,"N");
				preparedStatement.setTimestamp(2,new Timestamp(System.currentTimeMillis()));
				preparedStatement.setString(3,objAICTableRowsDataVO.getDataId());
				preparedStatement.setString(4,objAICTableRowsDataVO.getRowId());
				preparedStatement.setString(5,objAICTableVO.getTableId());
				preparedStatement.executeUpdate();

			}

		} catch (SQLException sql) {
			throw new AICDataAccessException(
				"Error in AICTableRowsDataDAO.delete()",
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
					"Unable to close resultset, database connection or statement in AICTableRowsDataDAO.delete",
					sqle);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("<- delete");
		}
				
	}
}
