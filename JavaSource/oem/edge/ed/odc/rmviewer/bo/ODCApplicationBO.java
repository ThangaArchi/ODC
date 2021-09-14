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

import oem.edge.ed.odc.rmviewer.ODCDAOFactory;
import oem.edge.ed.odc.rmviewer.actions.ApplicationForm;
import oem.edge.ed.odc.rmviewer.dao.IApplicationDAO;
import oem.edge.ed.odc.rmviewer.vo.ApplicationVO;
import oem.edge.ed.odc.utils.ODCLogger;
import oem.edge.ed.odc.utils.ODCServiceLocator;
import oem.edge.ed.odc.utils.ValueObject;

import org.apache.commons.logging.Log;

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
public class ODCApplicationBO {

	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.2";
		
	private static Log logger = ODCLogger.getLogger(ODCApplicationBO.class);


	public ValueObject createApplicationBO(ValueObject objODCApplicationVO) throws Exception
	{
		logger.info("-> createApplicationBO() ");

		ODCDAOFactory factory = ODCDAOFactory.getDAOFactory(ODCDAOFactory.JDBC);
		IApplicationDAO objProjDAO = factory.getApplicationDAO();
		ODCServiceLocator objODCServiceLocator = ODCServiceLocator.getInstance();
		UserTransaction transaction = objODCServiceLocator.getUserTransaction( ODCServiceLocator.USER_TRANSACTION );
		ValueObject objValueObjectProj = null;

		try {
			transaction.begin();
			objValueObjectProj = ( ApplicationVO ) objProjDAO.insert(objODCApplicationVO);
			transaction.commit();
		}catch (Exception exp) {
			exp.printStackTrace();
			logger.error(exp);
			try {
				if (logger.isErrorEnabled()) {
					logger.error("Rolling back in addRowsToTable because of  :: ",exp);
				}
				transaction.rollback();
			} catch (SystemException se) {
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("<- createApplicationBO ");
		}
		return objValueObjectProj;
	}


	public ValueObject deleteApplicationBO(ValueObject objODCApplicationVO) throws Exception
	{
		logger.info("-> deleteApplicationBO() ");

		ODCDAOFactory factory = ODCDAOFactory.getDAOFactory(ODCDAOFactory.JDBC);
		IApplicationDAO objProjDAO = factory.getApplicationDAO();
		ODCServiceLocator objODCServiceLocator = ODCServiceLocator.getInstance();
		UserTransaction transaction = objODCServiceLocator.getUserTransaction( ODCServiceLocator.USER_TRANSACTION );
		ValueObject objValueObjectProj = null;

		try {
			transaction.begin();

			objValueObjectProj = ( ApplicationVO ) objProjDAO.deleteUserApplication(objODCApplicationVO);
			objValueObjectProj = ( ApplicationVO ) objProjDAO.deleteApplication(objODCApplicationVO);

			transaction.commit();
			logger.info("Transaction Status : "+ transaction.getStatus() );
			
		}catch (Exception exp) {
			logger.error(exp);
			exp.printStackTrace();
			try {
				if (logger.isErrorEnabled()) {
					logger.error("Rolling back in addRowsToTable because of  :: ",exp);
				}
				transaction.rollback();
			} catch (SystemException se) {
					logger.error("System Exception : > "+ se);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("<- deleteApplicationBO ");
		}
		return objValueObjectProj;
	}


	public ValueObject editApplicationBO(ValueObject objODCApplicationVO) throws Exception
	{
		logger.info("-> editApplicationBO() ");

		ODCDAOFactory factory = ODCDAOFactory.getDAOFactory(ODCDAOFactory.JDBC);
		IApplicationDAO objProjDAO = factory.getApplicationDAO();
		ODCServiceLocator objODCServiceLocator = ODCServiceLocator.getInstance();
		UserTransaction transaction = objODCServiceLocator.getUserTransaction( ODCServiceLocator.USER_TRANSACTION );
		ValueObject objValueObjectProj = null;

		try {
			transaction.begin();

			objValueObjectProj = ( ApplicationVO ) objProjDAO.editUserApplication(objODCApplicationVO);
			//objValueObjectProj = ( ApplicationVO ) objProjDAO.editApplication(objODCApplicationVO);

			transaction.commit();
		}catch (Exception exp) {
			logger.error(exp);
			exp.printStackTrace();
			try {
				if (logger.isErrorEnabled()) {
					logger.error("Rolling back in addRowsToTable because of  :: ",exp);
				}
				transaction.rollback();
			} catch (SystemException se) {
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("<- editApplicationBO ");
		}
		return objValueObjectProj;
	}


	public Collection findAllApplicationsBO()
	{
		logger.info("-> findAllApplicationsBO");
		Collection collApplications = null;
		try {
			ODCDAOFactory factory = ODCDAOFactory.getDAOFactory(ODCDAOFactory.JDBC);
			IApplicationDAO objProjDAO = factory.getApplicationDAO();
			ODCServiceLocator objODCServiceLocator = ODCServiceLocator.getInstance();
			ValueObject objValueObjectProj = null;
	
			collApplications = objProjDAO.findAllApplications();
	
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		logger.info("<- findAllApplicationsBO");
		return collApplications;
	}
	

	public Collection findSingleApplication_UserBO(ApplicationForm objApplicationForm){
		logger.info("-> findSingleApplication_UserBO");
		Collection collApplications = null;
		try {
			ODCDAOFactory factory = ODCDAOFactory.getDAOFactory(ODCDAOFactory.JDBC);
			IApplicationDAO objProjDAO = factory.getApplicationDAO();
			ODCServiceLocator objODCServiceLocator = ODCServiceLocator.getInstance();
			ValueObject objValueObjectProj = null;
	
			collApplications = objProjDAO.findSingleApplication_User(objApplicationForm);
	
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		logger.info("<- findSingleApplication_UserBO");
		return collApplications;
	}

		




	public static void main(String[] args) {
	}
}
