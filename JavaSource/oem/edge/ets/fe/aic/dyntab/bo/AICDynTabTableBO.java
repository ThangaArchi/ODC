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
package oem.edge.ets.fe.aic.dyntab.bo;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.commons.logging.Log;

import oem.edge.ets.fe.aic.common.dao.AICDAOFactory;
import oem.edge.ets.fe.aic.common.exception.AICDAORuntimeException;
import oem.edge.ets.fe.aic.common.exception.AICDataAccessException;
import oem.edge.ets.fe.aic.common.exception.AICSystemException;
import oem.edge.ets.fe.aic.common.helper.AICServiceLocator;
import oem.edge.ets.fe.aic.common.vo.ValueObject;
import oem.edge.ets.fe.aic.dyntab.dao.IAICTableDAO;
import oem.edge.ets.fe.aic.dyntab.dao.IAICTableRowsDataDAO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTableRowsDataVO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTableVO;
import oem.edge.ets.fe.common.EtsLogger;

/**
 * @author vivek
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AICDynTabTableBO {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.2";
		
	private static Log logger = EtsLogger.getLogger(AICDynTabTableBO.class);

	public ValueObject createTableFromTemplate(ValueObject objValueObject)
		throws AICDataAccessException {
		if (logger.isInfoEnabled()) {
			logger.info("-> createTableFromTemplate");
		}
		AICDAOFactory factory = AICDAOFactory.getDAOFactory(AICDAOFactory.JDBC);
		IAICTableDAO objAICTableDAO = factory.getAICTableDAO();
		AICServiceLocator objAICServiceLocator =
			AICServiceLocator.getInstance();
		UserTransaction transaction =
			objAICServiceLocator.getUserTransaction(
				AICServiceLocator.USER_TRANSACTION);
		ValueObject objValueObjectRet = null;
		try {

			transaction.begin();
			objValueObjectRet = objAICTableDAO.insert(objValueObject);
			transaction.commit();
		} catch (AICDataAccessException ape) {
			throw new AICDataAccessException(ape.getMessage());
		} catch (AICSystemException ase) {
			throw new AICSystemException(ase.getMessage());
		} catch (Exception exp) {
			try {
				if (logger.isErrorEnabled()) {
					logger.error(
						"Rolling back in createTableFromTemplate because of  :: ",
						exp);
				}
				transaction.rollback();
			} catch (SystemException se) {
				throw new AICDAORuntimeException(
					"Error in RollingBackTransaction :: " + se);
			}
			throw new AICDAORuntimeException(
				"Transaction Rolled Back :: " + exp);
		}
		if (logger.isInfoEnabled()) {
			logger.info("<- createTableFromTemplate ");
		}
		return objValueObjectRet;
	}

	public ValueObject addRowsToTable(ValueObject objValueObject)
		throws AICDataAccessException {

		if (logger.isInfoEnabled()) {
			logger.info("-> addRowsToTable");
		}
		AICDAOFactory factory = AICDAOFactory.getDAOFactory(AICDAOFactory.JDBC);
		IAICTableRowsDataDAO objAICTableRowsData =
			factory.getAICTableRowsData();
		AICServiceLocator objAICServiceLocator =
			AICServiceLocator.getInstance();
		UserTransaction transaction =
			objAICServiceLocator.getUserTransaction(
				AICServiceLocator.USER_TRANSACTION);
		ValueObject objValueObjectRet = null;
		try {

			transaction.begin();
			objValueObjectRet = objAICTableRowsData.insert(objValueObject);
			objValueObjectRet =	objAICTableRowsData.findByPrimaryKey(objValueObject);
			transaction.commit();
		} catch (AICDataAccessException ape) {
			throw new AICDataAccessException(ape.getMessage());
		} catch (AICSystemException ase) {
			throw new AICSystemException(ase.getMessage());
		} catch (Exception exp) {
			try {
				if (logger.isErrorEnabled()) {
					logger.error(
						"Rolling back in addRowsToTable because of  :: ",
						exp);
				}
				transaction.rollback();
			} catch (SystemException se) {
				throw new AICDAORuntimeException(
					"Error in RollingBackTransaction :: " + se);
			}
			throw new AICDAORuntimeException(
				"Transaction Rolled Back :: " + exp);
		}
		if (logger.isInfoEnabled()) {
			logger.info("<- addRowsToTable ");
		}
		return objValueObjectRet;
	}

	public ValueObject deleteRowsFromTable(ValueObject objValueObject)
		throws AICDataAccessException {
		if (logger.isInfoEnabled()) {
			logger.info("-> deleteRowsFromTable");
		}
		AICDAOFactory factory = AICDAOFactory.getDAOFactory(AICDAOFactory.JDBC);
		IAICTableRowsDataDAO objAICTableRowsData =
			factory.getAICTableRowsData();
		AICServiceLocator objAICServiceLocator =
			AICServiceLocator.getInstance();
		UserTransaction transaction =
			objAICServiceLocator.getUserTransaction(
				AICServiceLocator.USER_TRANSACTION);
				
		ValueObject objValueObjectRet = null;
		
		try {

			transaction.begin();
			objAICTableRowsData.delete(objValueObject);
			objValueObjectRet =	objAICTableRowsData.findByPrimaryKey(objValueObject);
			transaction.commit();
		} catch (AICDataAccessException ape) {
			try{			
					transaction.commit();
				}catch(Exception ex)
				{
					if (logger.isErrorEnabled()) {
					logger.error("Rolling back in deleteColumnFromTemplate because of :: ",ex);
					}
					try{

					transaction.rollback();
					}catch(Exception e)
					{
						if (logger.isErrorEnabled()) {
						logger.error("Rolling back problem in deleteColumnFromTemplate because of :: ",e);
						}
					}
				}
							
			throw new AICDataAccessException(ape.getMessage());
		} catch (AICSystemException ase) {
			try{
				transaction.rollback();
				}catch(Exception e)
				{
					if (logger.isErrorEnabled()) {
					logger.error("Rolling back problem in deleteColumnFromTemplate because of :: ",e);
					}
				}
			throw new AICSystemException(ase.getMessage());
		} catch (Exception exp) {
			try {
				if (logger.isErrorEnabled()) {
					logger.error(
						"Rolling back in deleteRowsFromTable because of  :: ",
						exp);
				}
				transaction.rollback();
			} catch (SystemException se) {
				throw new AICDAORuntimeException(
					"Error in RollingBackTransaction :: " + se);
			}
			throw new AICDAORuntimeException(
				"Transaction Rolled Back :: " + exp);
		}
		if (logger.isInfoEnabled()) {
			logger.info("<- deleteRowsFromTable ");
		}
		return objValueObjectRet;
	}

	public ValueObject editRowFromTable(ValueObject objValueObject)
		throws AICDataAccessException {
		if (logger.isInfoEnabled()) {
			logger.info("-> editRowFromTable");
		}
		AICDAOFactory factory = AICDAOFactory.getDAOFactory(AICDAOFactory.JDBC);
		IAICTableRowsDataDAO objAICTableRowsData =
			factory.getAICTableRowsData();
		AICServiceLocator objAICServiceLocator =
			AICServiceLocator.getInstance();
		UserTransaction transaction =
			objAICServiceLocator.getUserTransaction(
				AICServiceLocator.USER_TRANSACTION);
		ValueObject objValueObjectRet = null;
		try {
			
			AICTableVO pudhuAICTableVO = new AICTableVO();
			AICTableVO objAICTableVO = (AICTableVO)objValueObject;
			pudhuAICTableVO.setActive(objAICTableVO.getActive());
			pudhuAICTableVO.setDocId(objAICTableVO.getDocId());
			pudhuAICTableVO.setTableId(objAICTableVO.getTableId());
			pudhuAICTableVO.setTableName(objAICTableVO.getTableName());
			pudhuAICTableVO.setTableUpdateDate(objAICTableVO.getTableUpdateDate());
			pudhuAICTableVO.setTemplateId(objAICTableVO.getTemplateId());
			Iterator it = objAICTableVO.getAICTableRowsDataCollection().iterator();
			AICTableRowsDataVO objAICTableRowsDataVO = null;
			ArrayList newAL = new ArrayList();
			while(it.hasNext())
			{
				objAICTableRowsDataVO = (AICTableRowsDataVO)it.next();
				if(objAICTableRowsDataVO.getInsertFlag())
				{
					newAL.add(objAICTableRowsDataVO);
				}
			}
			pudhuAICTableVO.setAICTableRowsDataCollection(newAL);
			
			
			transaction.begin();
			objValueObjectRet = objAICTableRowsData.update(objValueObject);
			objValueObjectRet = objAICTableRowsData.insert(pudhuAICTableVO);
			
			objValueObjectRet =	objAICTableRowsData.findByPrimaryKey(objValueObject);
			transaction.commit();
		} catch (AICDataAccessException ape) {
			throw new AICDataAccessException(ape.getMessage());
		} catch (AICSystemException ase) {
			throw new AICSystemException(ase.getMessage());
		} catch (Exception exp) {
			try {
				if (logger.isErrorEnabled()) {
					logger.error(
						"Rolling back in editRowFromTable because of  :: ",
						exp);
				}
				transaction.rollback();
			} catch (SystemException se) {
				throw new AICDAORuntimeException(
					"Error in RollingBackTransaction :: " + se);
			}
			throw new AICDAORuntimeException(
				"Transaction Rolled Back :: " + exp);
		}
		if (logger.isInfoEnabled()) {
			logger.info("<- editRowFromTable ");
		}
		return objValueObjectRet;
	}

	public ValueObject viewTableWithRows(ValueObject objValueObject)
		throws AICDataAccessException {
		if (logger.isInfoEnabled()) {
			logger.info("-> viewTableWithRows");
		}
		AICDAOFactory factory = AICDAOFactory.getDAOFactory(AICDAOFactory.JDBC);
		IAICTableRowsDataDAO objAICTableRowsData =
			factory.getAICTableRowsData();
		AICServiceLocator objAICServiceLocator =
			AICServiceLocator.getInstance();
		UserTransaction transaction =
			objAICServiceLocator.getUserTransaction(
				AICServiceLocator.USER_TRANSACTION);
		ValueObject objValueObjectRet = null;
		objValueObjectRet =
			objAICTableRowsData.findByPrimaryKey(objValueObject);
		if (logger.isInfoEnabled()) {
			logger.info("<- viewTableWithRows");
		}
		return objValueObjectRet;
	}

	public void deleteTable(ValueObject objValueObject)
		throws AICDataAccessException {
		if (logger.isInfoEnabled()) {
			logger.info("-> deleteTable");
		}
		AICDAOFactory factory = AICDAOFactory.getDAOFactory(AICDAOFactory.JDBC);
		IAICTableDAO objAICTableDAO =
			factory.getAICTableDAO();
		AICServiceLocator objAICServiceLocator =
			AICServiceLocator.getInstance();
		UserTransaction transaction =
			objAICServiceLocator.getUserTransaction(
				AICServiceLocator.USER_TRANSACTION);
		
		try {

			transaction.begin();
			objAICTableDAO.delete(objValueObject);
			transaction.commit();
		} catch (AICDataAccessException ape) {			
			throw new AICDataAccessException(ape.getMessage());
		} catch (AICSystemException ase) {
			throw new AICSystemException(ase.getMessage());
		} catch (Exception exp) {
			try {
				if (logger.isErrorEnabled()) {
					logger.error(
						"Rolling back in deleteTable because of  :: ",
						exp);
				}
				transaction.rollback();
			} catch (SystemException se) {
				throw new AICDAORuntimeException(
					"Error in RollingBackTransaction :: " + se);
			}
			throw new AICDAORuntimeException(
				"Transaction Rolled Back :: " + exp);
		}
		if (logger.isInfoEnabled()) {
			logger.info("<- deleteTable ");
		}
		
	}

	public Collection findByDocID(int intDocId) throws AICDataAccessException {
		if (logger.isInfoEnabled()) {
			logger.info("-> findByDocID");
		}
		AICDAOFactory factory = AICDAOFactory.getDAOFactory(AICDAOFactory.JDBC);
		IAICTableDAO objAICTableDAO = factory.getAICTableDAO();
		AICServiceLocator objAICServiceLocator =
			AICServiceLocator.getInstance();
		UserTransaction transaction =
			objAICServiceLocator.getUserTransaction(
				AICServiceLocator.USER_TRANSACTION);
		Collection collTable = null;
		collTable = objAICTableDAO.findTablesByDocId(intDocId);
		if (logger.isInfoEnabled()) {
			logger.info("<- findByDocID");
		}
		return collTable;
	}
	
	public ValueObject createTableFromTemplate(ValueObject objValueObject,Connection conn)
			throws AICDataAccessException {
			if (logger.isInfoEnabled()) {
				logger.info("-> createTableFromTemplate with Connection Object");
			}
			AICDAOFactory factory = AICDAOFactory.getDAOFactory(AICDAOFactory.JDBC);
			IAICTableDAO objAICTableDAO = factory.getAICTableDAO();
			AICServiceLocator objAICServiceLocator =
				AICServiceLocator.getInstance();
			
			ValueObject objValueObjectRet = null;
			try {

				
				objValueObjectRet = objAICTableDAO.insert(objValueObject,conn);
				
			} catch (AICDataAccessException ape) {
				throw new AICDataAccessException(ape.getMessage());
			} catch (AICSystemException ase) {
				throw new AICSystemException(ase.getMessage());
			} catch (Exception exp) {
				
				throw new AICDAORuntimeException(
					"Runtime Exception Occured :: " + exp);
			}
			if (logger.isInfoEnabled()) {
				logger.info("<- createTableFromTemplate with Connection Object");
			}
			return objValueObjectRet;
		}
		
	public ValueObject viewTable(ValueObject objValueObject)
			throws AICDataAccessException {
			if (logger.isInfoEnabled()) {
				logger.info("-> viewTable");
			}
			AICDAOFactory factory = AICDAOFactory.getDAOFactory(AICDAOFactory.JDBC);
			IAICTableDAO objAICTableDAO =
				factory.getAICTableDAO();
			AICServiceLocator objAICServiceLocator =
				AICServiceLocator.getInstance();
			UserTransaction transaction =
				objAICServiceLocator.getUserTransaction(
					AICServiceLocator.USER_TRANSACTION);
			ValueObject objValueObjectRet = null;
			objValueObjectRet =
				objAICTableDAO.findByPrimaryKey(objValueObject);
			if (logger.isInfoEnabled()) {
				logger.info("<- viewTable");
			}
			return objValueObjectRet;
		}	

}
