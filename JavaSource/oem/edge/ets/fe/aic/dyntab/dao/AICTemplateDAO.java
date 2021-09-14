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
import oem.edge.ets.fe.aic.common.util.AICUtil;
import oem.edge.ets.fe.aic.common.vo.ValueObject;
import oem.edge.ets.fe.aic.dyntab.vo.AICTemplateVO;
import oem.edge.ets.fe.common.EtsLogger;

/**
 * @author vivek
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AICTemplateDAO implements IAICTemplateDAO {
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.2";
		
	private static Log logger = EtsLogger.getLogger(AICTemplateDAO.class);

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
					"dyntab.dao.AICTemplateDAO.findByPrimaryKey");
			preparedStatement = conn.prepareStatement(strSQL.toString());
			preparedStatement.setString(1, objAICTemplateVO.getTemplateId());
			preparedStatement.setString(2, "Y");

			rs = preparedStatement.executeQuery();
			boolean recordExists = false;
			while (rs.next()) {
				recordExists = true;
				objAICTemplateVO.setTemplateId(rs.getString("TEMPLATE_ID"));
				objAICTemplateVO.setTemplateName(rs.getString("TEMPLATE_NAME"));
				objAICTemplateVO.setActive(rs.getString("ISACTIVE"));
				objAICTemplateVO.setTemplateUpdatedate(
					rs.getTimestamp("TEMPLATE_UPDATE_DATE"));

			}
			if(!recordExists)
			{
				throw new AICNoDataFoundException("Record Not found for Template : " + objAICTemplateVO.getTemplateName());
			}

		} catch (SQLException sql) {
			throw new AICDataAccessException(
				"Error in AICTemplateDAO.findByPrimaryKey()",
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
					"Unable to close resultset, database connection or statement in AICTemplateDAO.findByPrimaryKey",
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
				sqlCode.getSQLStatement("dyntab.dao.AICTemplateDAO.insert");
			preparedStatement = conn.prepareStatement(strSQL.toString());			

			preparedStatement.setString(1, objAICTemplateVO.getTemplateId());
			preparedStatement.setString(2, objAICTemplateVO.getTemplateName());
			preparedStatement.setString(3, "Y");
			preparedStatement.setTimestamp(
				4,
				new Timestamp(System.currentTimeMillis()));

			preparedStatement.executeUpdate();

		} catch (SQLException sql) {
			throw new AICDataAccessException(
				"Error in AICTemplateDAO.insert()",
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
					"Unable to close resultset, database connection or statement in AICTemplateDAO.insert",
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

		return null;
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
				sqlCode.getSQLStatement("dyntab.dao.AICTemplateDAO.delete");
			preparedStatement = conn.prepareStatement(strSQL.toString());

			preparedStatement.setString(1, "N");
			preparedStatement.setTimestamp(
				2,
				new Timestamp(System.currentTimeMillis()));
			preparedStatement.setString(3, objAICTemplateVO.getTemplateId());

			preparedStatement.executeUpdate();
		} catch (SQLException sql) {
			throw new AICDataAccessException(
				"Error in AICTemplateDAO.delete()",
				sql);
		}finally {
			try {

				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException sqle) {
				throw new AICDBException(
					"Unable to close resultset, database connection or statement in AICTemplateDAO.delete",
					sqle);
			}
		}

		if (logger.isInfoEnabled()) {
			logger.info("<- delete");
		}
	}

	public Collection findAllTemplate() throws AICDataAccessException {
		if (logger.isInfoEnabled()) {
			logger.info("-> findAllTemplate");
		}

		ArrayList collTemplate = new ArrayList();
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		try {
			conn = AICDBUtils.getConnection(); 

			SQLCode sqlCode = SQLCode.getInstance();
			String strSQL =
				sqlCode.getSQLStatement(
					"dyntab.dao.AICTemplateDAO.findAllTemplate");
			preparedStatement = conn.prepareStatement(strSQL.toString());

			preparedStatement.setString(1, "Y");

			rs = preparedStatement.executeQuery();

			AICTemplateVO objAICTemplateVO = null;
			
			boolean recordExists = false;
			while (rs.next()) {
				recordExists = true;
				objAICTemplateVO = new AICTemplateVO();
				objAICTemplateVO.setTemplateId(rs.getString("TEMPLATE_ID"));
				objAICTemplateVO.setTemplateName(rs.getString("TEMPLATE_NAME"));
				objAICTemplateVO.setActive(rs.getString("ISACTIVE"));
				objAICTemplateVO.setTemplateUpdatedate(
					rs.getTimestamp("TEMPLATE_UPDATE_DATE"));
				collTemplate.add(objAICTemplateVO);
			}
			if(!recordExists)
			{
				throw new AICNoDataFoundException("NO record for Templates");
			}

		} catch (SQLException sql) {
			throw new AICDataAccessException(
				"Error in AICTemplateDAO.findAllTemplate()",
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
					"Unable to close resultset, database connection or statement in AICTemplateDAO.findAllTemplate",
					sqle);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("<- findAllTemplate");
		}
		return collTemplate;
	}
	
	public ValueObject findByTemplateName(ValueObject objValueObject)
			throws AICDataAccessException {
			if (logger.isInfoEnabled()) {
				logger.info("-> findByTemplateName");
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
						"dyntab.dao.AICTemplateDAO.findByTemplateName");
				preparedStatement = conn.prepareStatement(strSQL.toString());
				preparedStatement.setString(1, objAICTemplateVO.getTemplateName());
				preparedStatement.setString(2, "Y");

				rs = preparedStatement.executeQuery();
				boolean recordExists = false;
				while (rs.next()) {
					recordExists = true;
					objAICTemplateVO.setTemplateId(rs.getString("TEMPLATE_ID"));
					objAICTemplateVO.setTemplateName(rs.getString("TEMPLATE_NAME"));
					objAICTemplateVO.setActive(rs.getString("ISACTIVE"));
					objAICTemplateVO.setTemplateUpdatedate(rs.getTimestamp("TEMPLATE_UPDATE_DATE"));
				}
				if(!recordExists)
				{
					throw new AICNoDataFoundException("Record Not found for Template : " + objAICTemplateVO.getTemplateName());
				}

			} catch (SQLException sql) {
				throw new AICDataAccessException(
					"Error in AICTemplateDAO.findByTemplateName()",
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
						"Unable to close resultset, database connection or statement in AICTemplateDAO.findByTemplateName",
						sqle);
				}
			}
			if (logger.isInfoEnabled()) {
				logger.info("<- findByTemplateName");
			}
			return objAICTemplateVO;
		}
		
	public Collection findAllTemplate(Connection conn) throws AICDataAccessException {
			if (logger.isInfoEnabled()) {
				logger.info("-> findAllTemplate");
			}

			ArrayList collTemplate = new ArrayList();
			
			PreparedStatement preparedStatement = null;
			ResultSet rs = null;
			try {
				//conn = AICDBUtils.getConnection(); 

				SQLCode sqlCode = SQLCode.getInstance();
				String strSQL =
					sqlCode.getSQLStatement(
						"dyntab.dao.AICTemplateDAO.findAllTemplate");
				preparedStatement = conn.prepareStatement(strSQL.toString());

				preparedStatement.setString(1, "Y");

				rs = preparedStatement.executeQuery();

				AICTemplateVO objAICTemplateVO = null;
			
				boolean recordExists = false;
				while (rs.next()) {
					recordExists = true;
					objAICTemplateVO = new AICTemplateVO();
					objAICTemplateVO.setTemplateId(rs.getString("TEMPLATE_ID"));
					objAICTemplateVO.setTemplateName(rs.getString("TEMPLATE_NAME"));
					objAICTemplateVO.setActive(rs.getString("ISACTIVE"));
					objAICTemplateVO.setTemplateUpdatedate(
						rs.getTimestamp("TEMPLATE_UPDATE_DATE"));
					collTemplate.add(objAICTemplateVO);
				}
				if(!recordExists)
				{
					throw new AICNoDataFoundException("NO record for Templates");
				}

			} catch (SQLException sql) {
				throw new AICDataAccessException(
					"Error in AICTemplateDAO.findAllTemplate()",
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
					
				} catch (SQLException sqle) {
					throw new AICDBException(
						"Unable to close resultset, database connection or statement in AICTemplateDAO.findAllTemplate",
						sqle);
				}
			}
			if (logger.isInfoEnabled()) {
				logger.info("<- findAllTemplate");
			}
			return collTemplate;
		}
	










	public ValueObject findByAllTemplateStatus(ValueObject objValueObject)
		throws AICDataAccessException {
		if (logger.isInfoEnabled()) {
			logger.info("-> findByAllTemplateStatus");
		}
		AICTemplateVO objAICTemplateVO = (AICTemplateVO) objValueObject;

		Connection conn = null;
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		try {
			conn = AICDBUtils.getConnection();

			SQLCode sqlCode = SQLCode.getInstance();
			String strSQL = sqlCode.getSQLStatement("dyntab.dao.AICTemplateDAO.findByAllTemplateStatus");
			preparedStatement = conn.prepareStatement(strSQL.toString());
			preparedStatement.setString(1, objAICTemplateVO.getTemplateId());
			//preparedStatement.setString(2, "N");

			rs = preparedStatement.executeQuery();
			boolean recordExists = false;
			while (rs.next()) {
				recordExists = true;
				objAICTemplateVO.setTemplateId(rs.getString("TEMPLATE_ID"));
				objAICTemplateVO.setTemplateName(rs.getString("TEMPLATE_NAME"));
				objAICTemplateVO.setActive(rs.getString("ISACTIVE"));
				objAICTemplateVO.setTemplateUpdatedate(
					rs.getTimestamp("TEMPLATE_UPDATE_DATE"));

			}
			if(!recordExists)
			{
				throw new AICNoDataFoundException("Record Not found for Template : " + objAICTemplateVO.getTemplateName());
			}

		} catch (SQLException sql) {
			throw new AICDataAccessException(
				"Error in AICTemplateDAO.findByAllTemplateStatus()",
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
					"Unable to close resultset, database connection or statement in AICTemplateDAO.findByPrimaryKey",
					sqle);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("<- findByAllTemplateStatus");
		}
		return objAICTemplateVO;
	}

}
