/*
 * Created on Mar 21, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ed.odc.rmviewer.bo;

import java.util.Collection;

import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.commons.logging.Log;

import oem.edge.ed.odc.rmviewer.ODCDAOFactory;
import oem.edge.ed.odc.rmviewer.actions.UserApplicationForm;
import oem.edge.ed.odc.rmviewer.dao.IUserApplicationDAO;
import oem.edge.ed.odc.rmviewer.vo.UserApplicationVO;
import oem.edge.ed.odc.utils.ODCLogger;
import oem.edge.ed.odc.utils.ODCServiceLocator;
import oem.edge.ed.odc.utils.ValueObject;

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

/**
 * @author tkandhas@in.ibm.com
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ODCUserApplicationBO {

	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.2";
		
	private static Log logger = ODCLogger.getLogger(ODCUserApplicationBO.class);


	public ValueObject addUserApplicationBO(UserApplicationVO objUserApplicationVO) throws Exception
	{
		logger.info("-> addUserApplicationBO() ");

		ODCDAOFactory factory = ODCDAOFactory.getDAOFactory(ODCDAOFactory.JDBC);
		IUserApplicationDAO objUserApplicationDAO = factory.getUserApplicationDAO();
		ODCServiceLocator objODCServiceLocator = ODCServiceLocator.getInstance();

		UserTransaction transaction = objODCServiceLocator.getUserTransaction( ODCServiceLocator.USER_TRANSACTION );
		UserApplicationVO objValueObjectProj = null;

		try {
			transaction.begin();
			objValueObjectProj = ( UserApplicationVO ) objUserApplicationDAO.insert(objUserApplicationVO);
			transaction.commit();
		}catch (Exception e1) {
			logger.error(e1);
			e1.printStackTrace();
			try {
				if (logger.isErrorEnabled()) {
					logger.error("Rolling back in addRowsToTable because of  :: ",e1);
				}
				transaction.rollback();
			} catch (SystemException se) {
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("<- addUserApplicationBO ");
		}
		return objValueObjectProj;
	}


	public ValueObject deleteUserApplicationBO(UserApplicationVO objUserApplicationVO) throws Exception
	{
		logger.info("-> deleteUserApplicationBO() ");

		ODCDAOFactory factory = ODCDAOFactory.getDAOFactory(ODCDAOFactory.JDBC);
		IUserApplicationDAO objUserApplicationDAO = factory.getUserApplicationDAO();
		ODCServiceLocator objODCServiceLocator = ODCServiceLocator.getInstance();

		UserTransaction transaction = objODCServiceLocator.getUserTransaction( ODCServiceLocator.USER_TRANSACTION );
		UserApplicationVO objValueObjectProj = null;

		try {
			transaction.begin();
			objValueObjectProj = ( UserApplicationVO ) objUserApplicationDAO.deleteUserApplication(objUserApplicationVO);
			transaction.commit();
		}catch (Exception e1) {
			logger.error(e1);
			e1.printStackTrace();
			try {
				if (logger.isErrorEnabled()) {
					logger.error("Rolling back in addRowsToTable because of  :: ",e1);
				}
				transaction.rollback();
			} catch (SystemException se) {
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("<- deleteUserApplicationBO ");
		}
		return objValueObjectProj;
	}

	public Collection findAllUserApplicationBO()
	{
		logger.info("-> findAllUserApplicationBO");
		Collection collProjects = null;
		try {
			ODCDAOFactory factory = ODCDAOFactory.getDAOFactory(ODCDAOFactory.JDBC);
			IUserApplicationDAO objUserApplicationDAO = factory.getUserApplicationDAO();
			ODCServiceLocator objODCServiceLocator = ODCServiceLocator.getInstance();
			
			collProjects = objUserApplicationDAO.findAllUserApplication();
			
		} catch (Exception e1) {
			logger.error(e1);
			e1.printStackTrace();
		}
		logger.info("<- findAllUserApplicationBO");
		return collProjects;
	}
			
		
		
	public Collection findSingleUserApplicationBO(UserApplicationForm objUserApplicationForm)
	{
		logger.info("-> findSingleUserApplicationBO");
		Collection collUserApplication = null;
		Collection collApplication = null;
		try {
			ODCDAOFactory factory = ODCDAOFactory.getDAOFactory(ODCDAOFactory.JDBC);
			IUserApplicationDAO objUserApplicationDAO = factory.getUserApplicationDAO();
			ODCServiceLocator objODCServiceLocator = ODCServiceLocator.getInstance();
			
			collUserApplication = objUserApplicationDAO.findSingleUserApplication(objUserApplicationForm);
		} catch (Exception e1) {
			logger.error(e1);
			e1.printStackTrace();
		}
		logger.info("<- findSingleUserApplicationBO");
		return collUserApplication;
	}
			
		
	public Collection findSingleUserNonApplicationBO(UserApplicationForm objUserApplicationForm)
	{
		logger.info("-> findSingleUserNonApplicationBO");
		Collection collUserApplication = null;
		Collection collApplication = null;
		try {
			ODCDAOFactory factory = ODCDAOFactory.getDAOFactory(ODCDAOFactory.JDBC);
			IUserApplicationDAO objUserApplicationDAO = factory.getUserApplicationDAO();
			ODCServiceLocator objODCServiceLocator = ODCServiceLocator.getInstance();
			
			collUserApplication = objUserApplicationDAO.findSingleUserNonApplication(objUserApplicationForm);
		} catch (Exception ex) {
			logger.error(ex);
			ex.printStackTrace();
		}
		logger.info("<- findSingleUserNonApplicationBO");
		return collUserApplication;
	}
			
		
		
	
	public Collection findAllUserOnlyBO()
	{
		logger.info("-> findAllUserOnlyBO");
		Collection collProjects = null;
		try {
			ODCDAOFactory factory = ODCDAOFactory.getDAOFactory(ODCDAOFactory.JDBC);
			IUserApplicationDAO objUserApplicationDAO = factory.getUserApplicationDAO();
			ODCServiceLocator objODCServiceLocator = ODCServiceLocator.getInstance();
		
			collProjects = objUserApplicationDAO.findAllUserOnly();
		
		} catch (Exception ex) {
			logger.error(ex);
			ex.printStackTrace();
		}
		logger.info("<- findAllUserOnlyBO");
		return collProjects;
	}
		
	



	/**
	 * @param objProjectVO
	 * @return
	 */
	public ValueObject createUserApplicationBO(ValueObject objProjectVO) {
		return objProjectVO;
	}


		

	/**
	 * @param objProjectVO
	 * @return
	 */
	public ValueObject deleteUserApplicationBO(ValueObject objProjectVO) {
		return objProjectVO;
	}

}
