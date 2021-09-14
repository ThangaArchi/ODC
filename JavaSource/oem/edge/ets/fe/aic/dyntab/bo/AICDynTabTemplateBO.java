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
import java.util.Collection;

import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import oem.edge.ets.fe.aic.common.dao.AICDAOFactory;
import oem.edge.ets.fe.aic.common.exception.AICDAORuntimeException;
import oem.edge.ets.fe.aic.common.exception.AICDataAccessException;
import oem.edge.ets.fe.aic.common.exception.AICNoDataFoundException;
import oem.edge.ets.fe.aic.common.exception.AICSystemException;
import oem.edge.ets.fe.aic.common.helper.AICServiceLocator;
import oem.edge.ets.fe.aic.common.vo.ValueObject;
import oem.edge.ets.fe.aic.dyntab.dao.IAICTemplateColumnDAO;
import oem.edge.ets.fe.aic.dyntab.dao.IAICTemplateDAO;
import oem.edge.ets.fe.aic.dyntab.vo.AICTemplateVO;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

/**
 * @author vivek
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AICDynTabTemplateBO {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.2";
		
	private static Log logger = EtsLogger.getLogger(AICDynTabTemplateBO.class);

	public ValueObject createTemplate(ValueObject objValueObject)
		throws AICDataAccessException {
		if (logger.isInfoEnabled()) {
			logger.info("-> createTemplate");
		}
		AICDAOFactory factory = AICDAOFactory.getDAOFactory(AICDAOFactory.JDBC);
		IAICTemplateDAO objAICTemplateDAO = factory.getAICTemplateDAO();
		AICServiceLocator objAICServiceLocator =
			AICServiceLocator.getInstance();
		UserTransaction transaction =
			objAICServiceLocator.getUserTransaction(
				AICServiceLocator.USER_TRANSACTION);
		if (logger.isDebugEnabled()) {
			logger.debug("Inside createTemplate=" + transaction);
		}
		ValueObject objValueObjectRet = null;
		try {

			AICTemplateVO objAICTemplateVO =
				(AICTemplateVO) objAICTemplateDAO.findByTemplateName(
					objValueObject);
			//if (!objAICTemplateVO.getTemplateId().trim().equals("")) {
				// Duplicate Template Name
				throw new AICDataAccessException("Duplicate Template Name, Please retry with different Template Name");
			//}

		}
		/*
		catch (NotSupportedException ns) {
		} catch (SystemException se) {
		} catch (HeuristicRollbackException hre) {
		} catch (RollbackException re) {
			try {
		
				transaction.rollback();
			} catch (SystemException see) {
		
			}
		} catch (HeuristicMixedException hme) {
		
		}
		*/
		catch (AICNoDataFoundException andfe) {
			try {

				transaction.begin();
				objValueObjectRet = objAICTemplateDAO.insert(objValueObject);
				transaction.commit();
			} catch (AICDataAccessException ape) {
				throw new AICDataAccessException(ape.getMessage());
			} catch (AICSystemException ase) {
				throw new AICSystemException(ase.getMessage());
			} catch (Exception exp) {
				try {
					if (logger.isErrorEnabled()) {
						logger.error(
							"Rolling back in createTemplate because of  :: ",
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
		} catch (AICDataAccessException ape) {
			throw new AICDataAccessException(ape.getMessage());
		} catch (AICSystemException ase) {
			throw new AICSystemException(ase.getMessage());
		} catch (Exception exp) {
			try {
				if (logger.isErrorEnabled()) {
					logger.error(
						"Rolling back in createTemplate because of  :: ",
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
			logger.info("<- createTemplate ");
		}
		return objValueObjectRet;
	}

	public ValueObject createTemplateWithColumns(ValueObject objValueObject)
		throws AICDataAccessException {
		if (logger.isInfoEnabled()) {
			logger.info("-> createTemplateWithColumns");
		}

		AICDAOFactory factory = AICDAOFactory.getDAOFactory(AICDAOFactory.JDBC);
		IAICTemplateColumnDAO objAICTemplateColumnDAO =
			factory.getAICTemplateColumnDAO();
		AICServiceLocator objAICServiceLocator =
			AICServiceLocator.getInstance();
		UserTransaction transaction =
			objAICServiceLocator.getUserTransaction(
				AICServiceLocator.USER_TRANSACTION);
		ValueObject objValueObjectRet = null;
		try {

			transaction.begin();
			objValueObjectRet = objAICTemplateColumnDAO.insert(objValueObject);
			objValueObjectRet =	objAICTemplateColumnDAO.findByPrimaryKey(objValueObject);
			transaction.commit();
		} catch (AICDataAccessException ape) {
			throw new AICDataAccessException(ape.getMessage());
		} catch (AICSystemException ase) {
			throw new AICSystemException(ase.getMessage());
		} catch (Exception exp) {
			try {
				if (logger.isErrorEnabled()) {
					logger.error(
						"Rolling back in createTemplate because of :: ",
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
			logger.info("<- createTemplateWithColumns");
		}
		return objValueObjectRet;
	}

	public ValueObject deleteColumnFromTemplate(ValueObject objValueObject)
		throws AICDataAccessException {
		if (logger.isInfoEnabled()) {
			logger.info("-> deleteColumnFromTemplate");
		}

		AICDAOFactory factory = AICDAOFactory.getDAOFactory(AICDAOFactory.JDBC);
		IAICTemplateColumnDAO objAICTemplateColumnDAO =
			factory.getAICTemplateColumnDAO();
		AICServiceLocator objAICServiceLocator =
			AICServiceLocator.getInstance();
		UserTransaction transaction =
			objAICServiceLocator.getUserTransaction(
				AICServiceLocator.USER_TRANSACTION);
		ValueObject objValueObjectRet = null;
		try {

			transaction.begin();
			objAICTemplateColumnDAO.delete(objValueObject);			
			objValueObjectRet =	objAICTemplateColumnDAO.findByPrimaryKey(objValueObject);
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
						"Rolling back in deleteColumnFromTemplate because of :: ",
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
			logger.info("<- deleteColumnFromTemplate");
		}
		return objValueObjectRet;
	}

	public ValueObject editColumnFromTemplate(ValueObject objValueObject)
		throws AICDataAccessException {

		if (logger.isInfoEnabled()) {
			logger.info("-> editColumnFromTemplate");
		}

		AICDAOFactory factory = AICDAOFactory.getDAOFactory(AICDAOFactory.JDBC);
		IAICTemplateColumnDAO objAICTemplateColumnDAO =
			factory.getAICTemplateColumnDAO();
		AICServiceLocator objAICServiceLocator =
			AICServiceLocator.getInstance();
		UserTransaction transaction =
			objAICServiceLocator.getUserTransaction(
				AICServiceLocator.USER_TRANSACTION);
		ValueObject objValueObjectRet = null;
		try {

			transaction.begin();
			objValueObjectRet = objAICTemplateColumnDAO.update(objValueObject);
			objValueObjectRet =	objAICTemplateColumnDAO.findByPrimaryKey(objValueObject);
			transaction.commit();
		} catch (AICDataAccessException ape) {
			throw new AICDataAccessException(ape.getMessage());
		} catch (AICSystemException ase) {
			throw new AICSystemException(ase.getMessage());
		} catch (Exception exp) {
			try {
				if (logger.isErrorEnabled()) {
					logger.error(
						"Rolling back in editColumnFromTemplate because of :: ",
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
			logger.info("<- editColumnFromTemplate");
		}
		return objValueObjectRet;
	}

	public ValueObject viewTemplateWithColums(ValueObject objValueObject)
		throws AICDataAccessException {
		if (logger.isInfoEnabled()) {
			logger.info("-> viewTemplateWithColumns");
		}

		AICDAOFactory factory = AICDAOFactory.getDAOFactory(AICDAOFactory.JDBC);
		IAICTemplateColumnDAO objAICTemplateColumnDAO =
			factory.getAICTemplateColumnDAO();
		AICServiceLocator objAICServiceLocator =
			AICServiceLocator.getInstance();

		ValueObject objValueObjectRet = null;
		objValueObjectRet =
			objAICTemplateColumnDAO.findByPrimaryKey(objValueObject);

		if (logger.isInfoEnabled()) {
			logger.info("<- viewTemplateWithColumns");
		}
		return objValueObjectRet;
	}

	public void deleteTemplate(ValueObject objValueObject)
		throws AICDataAccessException {
		if (logger.isInfoEnabled()) {
			logger.info("-> deleteTemplate");
		}

		AICDAOFactory factory = AICDAOFactory.getDAOFactory(AICDAOFactory.JDBC);
		IAICTemplateDAO objAICTemplateDAO = factory.getAICTemplateDAO();
		AICServiceLocator objAICServiceLocator =
			AICServiceLocator.getInstance();
		UserTransaction transaction =
			objAICServiceLocator.getUserTransaction(
				AICServiceLocator.USER_TRANSACTION);

		try {

			transaction.begin();
			objAICTemplateDAO.delete(objValueObject);
			transaction.commit();
		} catch (AICDataAccessException ape) {
			
			throw new AICDataAccessException(ape.getMessage());
		} catch (AICSystemException ase) {
			try{				
				transaction.rollback();
				}catch(Exception e)
				{
					if (logger.isErrorEnabled()) {
					logger.error("Rolling back problem in deleteTemplate because of :: ",e);
					}
				}
			throw new AICSystemException(ase.getMessage());
		} catch (Exception exp) {
			try {
				if (logger.isErrorEnabled()) {
					logger.error(
						"Rolling back in deleteTemplate because of :: ",
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
			logger.info("<- deleteTemplate");
		}

	}

	public Collection findAllTemplates() throws AICDataAccessException {
		if (logger.isInfoEnabled()) {
			logger.info("-> findAllTemplates");
		}

		AICDAOFactory factory = AICDAOFactory.getDAOFactory(AICDAOFactory.JDBC);
		IAICTemplateDAO objIAICTemplateDAO = factory.getAICTemplateDAO();
		AICServiceLocator objAICServiceLocator =
			AICServiceLocator.getInstance();

		ValueObject objValueObjectRet = null;
		Collection collTemplates = null;

		collTemplates = objIAICTemplateDAO.findAllTemplate();

		if (logger.isInfoEnabled()) {
			logger.info("<- findAllTemplates");
		}
		return collTemplates;
	}
	
	public ValueObject viewTemplate(ValueObject objValueObject)
	throws AICDataAccessException {
	if (logger.isInfoEnabled()) {
		logger.info("-> viewTemplate");
	}

	AICDAOFactory factory = AICDAOFactory.getDAOFactory(AICDAOFactory.JDBC);
	IAICTemplateDAO objAICTemplateDAO =	factory.getAICTemplateDAO();
	AICServiceLocator objAICServiceLocator = AICServiceLocator.getInstance();

	ValueObject objValueObjectRet = null;
	objValueObjectRet =	objAICTemplateDAO.findByPrimaryKey(objValueObject);

	if (logger.isInfoEnabled()) {
		logger.info("<- viewTemplate");
	}
	return objValueObjectRet;
}

public ValueObject viewTemplateByName(ValueObject objValueObject)
	throws AICDataAccessException {
	if (logger.isInfoEnabled()) {
		logger.info("-> viewTemplateByTemplateName");
	}

	AICDAOFactory factory = AICDAOFactory.getDAOFactory(AICDAOFactory.JDBC);
	IAICTemplateDAO objAICTemplateDAO =	factory.getAICTemplateDAO();
	AICServiceLocator objAICServiceLocator = AICServiceLocator.getInstance();

	ValueObject objValueObjectRet = null;
	objValueObjectRet =	objAICTemplateDAO.findByTemplateName(objValueObject);

	if (logger.isInfoEnabled()) {
		logger.info("<- viewTemplateByTemplateName");
	}
	return objValueObjectRet;
}

	public Collection findAllTemplates(Connection conn) throws AICDataAccessException {
		if (logger.isInfoEnabled()) {
			logger.info("-> findAllTemplates with Connection Object");
		}

		AICDAOFactory factory = AICDAOFactory.getDAOFactory(AICDAOFactory.JDBC);
		IAICTemplateDAO objIAICTemplateDAO = factory.getAICTemplateDAO();
		AICServiceLocator objAICServiceLocator =
			AICServiceLocator.getInstance();

		ValueObject objValueObjectRet = null;
		Collection collTemplates = null;

		collTemplates = objIAICTemplateDAO.findAllTemplate(conn);

		if (logger.isInfoEnabled()) {
			logger.info("<- findAllTemplates with Connection Object");
		}
		return collTemplates;
	}
	





















	public ValueObject viewTemplateStatus(ValueObject objValueObject) throws AICDataAccessException
	{
	if (logger.isInfoEnabled()) {
		logger.info("-> viewTemplateStatus");
	}

	AICDAOFactory factory = AICDAOFactory.getDAOFactory(AICDAOFactory.JDBC);
	IAICTemplateDAO objAICTemplateDAO =	factory.getAICTemplateDAO();
	AICServiceLocator objAICServiceLocator = AICServiceLocator.getInstance();

	ValueObject objValueObjectRet = null;
	objValueObjectRet =	objAICTemplateDAO.findByAllTemplateStatus(objValueObject);

	if (logger.isInfoEnabled()) {
		logger.info("<- viewTemplateStatus");
	}
	return objValueObjectRet;
}

}
