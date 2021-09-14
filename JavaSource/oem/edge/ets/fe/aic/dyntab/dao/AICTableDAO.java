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
import java.util.Collection;

import org.apache.commons.logging.Log;

import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.aic.common.exception.AICDBException;
import oem.edge.ets.fe.aic.common.exception.AICDataAccessException;
import oem.edge.ets.fe.aic.common.exception.AICNoDataFoundException;
import oem.edge.ets.fe.aic.common.exception.AICOptimisticLockException;
import oem.edge.ets.fe.aic.common.helper.SQLCode;
import oem.edge.ets.fe.aic.common.util.AICDBUtils;
import oem.edge.ets.fe.aic.common.vo.ValueObject;
import oem.edge.ets.fe.aic.dyntab.vo.AICTableVO;
import oem.edge.ets.fe.common.EtsLogger;

/**
 * @author vivek
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AICTableDAO implements IAICTableDAO {
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.2";
		
	private static Log logger = EtsLogger.getLogger(AICTableDAO.class);
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
					"dyntab.dao.AICTableDAO.findByPrimaryKey");
			preparedStatement = conn.prepareStatement(strSQL.toString());
			preparedStatement.setString(1, objAICTableVO.getTableId());
			preparedStatement.setString(2, "Y");
			rs = preparedStatement.executeQuery();
			boolean recordExists = false;
			while (rs.next()) {
				recordExists = true;
				objAICTableVO.setTableId(rs.getString("TABLE_ID"));
				objAICTableVO.setTableName(rs.getString("TABLE_NAME"));
				objAICTableVO.setActive(rs.getString("ISACTIVE"));
				objAICTableVO.setTableUpdateDate(
					rs.getTimestamp("DOCFILE_UPDATE_DATE"));
				objAICTableVO.setDocId(rs.getInt("DOC_ID"));
				objAICTableVO.setTemplateId(rs.getString("TEMPLATE_ID"));
				
			}
			if(!recordExists)
			{
				throw new AICNoDataFoundException("Record Not found for Table : " + objAICTableVO.getTableName());
			}

		} catch (SQLException sql) {
			throw new AICDataAccessException(
				"Error in AICTableDAO.findByPrimaryKey()",
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
					"Unable to close resultset, database connection or statement in AICTableDAO.findByPrimaryKey",
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
				sqlCode.getSQLStatement("dyntab.dao.AICTableDAO.insert");
			preparedStatement = conn.prepareStatement(strSQL.toString());

			preparedStatement.setInt(1, objAICTableVO.getDocId());
			preparedStatement.setString(2, objAICTableVO.getTableId());
			preparedStatement.setString(3, objAICTableVO.getTableName());
			preparedStatement.setString(4, "Y");
			preparedStatement.setString(5, objAICTableVO.getTemplateId());
			//preparedStatement.setTimestamp(6,objAICTableVO.getTableUpdateDate());
			preparedStatement.setTimestamp(6,new Timestamp(System.currentTimeMillis()));
			preparedStatement.executeUpdate();

		} catch (SQLException sql) {
			throw new AICDataAccessException(
				"Error in AICTableDAO.insert()",
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
					"Unable to close resultset, database connection or statement in AICTableDAO.insert",
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
		return null;
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
				sqlCode.getSQLStatement("dyntab.dao.AICTableDAO.delete");
			preparedStatement = conn.prepareStatement(strSQL.toString());

			preparedStatement.setString(1, "N");
			preparedStatement.setTimestamp(2,new Timestamp(System.currentTimeMillis()));
			preparedStatement.setString(3, objAICTableVO.getTableId());

			preparedStatement.executeUpdate();
		} catch (SQLException sql) {
			throw new AICDataAccessException(
				"Error in AICTableDAO.delete()",
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
					"Unable to close resultset, database connection or statement in AICTableDAO.delete",
					sqle);
			}
		}
		
		if (logger.isInfoEnabled()) {
				logger.info("<- delete");
		}
				
	}

	public Collection findTablesByDocId(int intDocID)
		throws AICDataAccessException {
		if (logger.isInfoEnabled()) {
				logger.info("-> findTablesByDocId");
		}
		

		Connection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		ArrayList collTable = new ArrayList();
		try {
			conn = AICDBUtils.getConnection();

			SQLCode sqlCode = SQLCode.getInstance();
			String strSQL =
				sqlCode.getSQLStatement(
					"dyntab.dao.AICTableDAO.findTablesByDocId");
			preparedStatement = conn.prepareStatement(strSQL.toString());
			preparedStatement.setInt(1, intDocID);
			preparedStatement.setString(2, "Y");
			rs = preparedStatement.executeQuery();
			AICTableVO objAICTableVO = null;
			
			boolean recordExists = false;
			
			while (rs.next()) {
				recordExists = true;
				objAICTableVO = new AICTableVO();
				
				objAICTableVO.setTableName(rs.getString("TABLE_NAME"));
				objAICTableVO.setActive(rs.getString("ISACTIVE"));
				objAICTableVO.setTableUpdateDate(
					rs.getTimestamp("DOCFILE_UPDATE_DATE"));
				objAICTableVO.setTableId(rs.getString("TABLE_ID"));
				objAICTableVO.setTemplateId(rs.getString("TEMPLATE_ID"));
				objAICTableVO.setDocId(rs.getInt("DOC_ID"));
				
				collTable.add(objAICTableVO);
			}
			if(!recordExists)
			{
				throw new AICNoDataFoundException("Record Not found for Doc id " + intDocID);
			}

		} catch (SQLException sql) {
			throw new AICDataAccessException(
				"Error in AICTableDAO.findByPrimaryKey()",
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
					"Unable to close resultset, database connection or statement in AICTableDAO.findByPrimaryKey",
					sqle);
			}
		}
		if (logger.isInfoEnabled()) {
				logger.info("<- findTablesByDocId");
		}
		return collTable;
	}
	
	public ValueObject insert(ValueObject pValueObject,Connection conn)
			throws AICDataAccessException {
			
			if (logger.isInfoEnabled()) {
						logger.info("-> insert");
			}	
					
			AICTableVO objAICTableVO = (AICTableVO) pValueObject;
			
			PreparedStatement preparedStatement = null;
			try {
				

				SQLCode sqlCode = SQLCode.getInstance();
				String strSQL =
					sqlCode.getSQLStatement("dyntab.dao.AICTableDAO.insert");
				preparedStatement = conn.prepareStatement(strSQL.toString());

				preparedStatement.setInt(1, objAICTableVO.getDocId());
				preparedStatement.setString(2, objAICTableVO.getTableId());
				preparedStatement.setString(3, objAICTableVO.getTableName());
				preparedStatement.setString(4, "Y");
				preparedStatement.setString(5, objAICTableVO.getTemplateId());
				//preparedStatement.setTimestamp(6,objAICTableVO.getTableUpdateDate());
				preparedStatement.setTimestamp(6,new Timestamp(System.currentTimeMillis()));
				preparedStatement.executeUpdate();

			} catch (SQLException sql) {
				throw new AICDataAccessException(
					"Error in AICTableDAO.insert()",
					sql);
			} finally {
				try {

					if (preparedStatement != null) {
						preparedStatement.close();
					}
					
				} catch (SQLException sqle) {
					throw new AICDBException(
						"Unable to close resultset, database connection or statement in AICTableDAO.insert",
						sqle);
				}
			}
			if (logger.isInfoEnabled()) {
					logger.info("<- insert");
			}	
			return objAICTableVO;
		}
}
