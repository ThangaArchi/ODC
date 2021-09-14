/*
 * Created on Mar 21, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ed.odc.remoteviewer.bo;

import java.util.Collection;

import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import oem.edge.ed.odc.remoteviewer.ODCDAOFactory;
import oem.edge.ed.odc.remoteviewer.RemoteAdminClient;
import oem.edge.ed.odc.remoteviewer.actions.ApplicationForm;
import oem.edge.ed.odc.remoteviewer.dao.IApplicationDAO;
import oem.edge.ed.odc.remoteviewer.vo.ApplicationVO;
import oem.edge.ed.odc.utils.CommonProperties;
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

	CommonProperties comProp = null;
	int REMOTE_ADMIN_PORT;
	String KEY_STORE=null, KEY_STORE_PW=null, TRUST_STORE=null, TRUST_STORE_PW = null;

	
	public ODCApplicationBO(){
		try {
			comProp = CommonProperties.getInstance();
			REMOTE_ADMIN_PORT = Integer.parseInt( comProp.getCommonProperty("ODC.Common.RemoteAdmin.Port") );
			KEY_STORE = comProp.getCommonProperty("ODC.Common.RemoteAdmin.keyStore");
			KEY_STORE_PW = comProp.getCommonProperty("ODC.Common.RemoteAdmin.keyStorePW");
			TRUST_STORE = comProp.getCommonProperty("ODC.Common.RemoteAdmin.trustStore");
			TRUST_STORE_PW = comProp.getCommonProperty("ODC.Common.RemoteAdmin.trustStorePW");

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public ValueObject createApplicationBO(ValueObject objODCApplicationVO) throws Exception
	{
		logger.info("-> createApplicationBO() ");

		ODCDAOFactory factory = ODCDAOFactory.getDAOFactory(ODCDAOFactory.JDBC);
		IApplicationDAO objProjDAO = factory.getApplicationDAO();
		ODCServiceLocator objODCServiceLocator = ODCServiceLocator.getInstance();
		UserTransaction transaction = objODCServiceLocator.getUserTransaction( ODCServiceLocator.USER_TRANSACTION );
		ValueObject objValueObjectProj = null;
		RemoteAdminClient rmclient = null;

		try {
			transaction.begin();
			objValueObjectProj = ( ApplicationVO ) objProjDAO.insert(objODCApplicationVO);
			ApplicationVO appVO = (ApplicationVO) objValueObjectProj; 
			
			logger.info(appVO.getServerName() +" <=> "+ appVO.getApplicationName() +" <=> "+ appVO.getApplicationPath() +" <=> "+ Integer.parseInt(appVO.getNumberOfUsers())  +" <=> "+ appVO.getIdPrefix() +" <=> "+ "/home/griddir");
			rmclient = new RemoteAdminClient(appVO.getServerName(), REMOTE_ADMIN_PORT, KEY_STORE,KEY_STORE_PW,TRUST_STORE,TRUST_STORE_PW);
			rmclient.createApplication(appVO.getApplicationName(), appVO.getApplicationPath(), Integer.parseInt(appVO.getNumberOfUsers()) , appVO.getIdPrefix(), "/home/griddir" );
			
			transaction.commit();
		}catch (Exception exp) {
			exp.printStackTrace();
			logger.error(exp);
			try {
				if (logger.isErrorEnabled()) {
					logger.error("Rolling back in because of  :: ",exp);
				}
				transaction.rollback();
			} catch (SystemException se) {
				logger.error("System Exception is: "+ se);
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
		RemoteAdminClient rmclient = null;

		try {
			transaction.begin();

			objValueObjectProj = ( ApplicationVO ) objProjDAO.deleteUserApplication(objODCApplicationVO);
			// thanga commented this method because the methods are merged in to the above method
			//objValueObjectProj = ( ApplicationVO ) objProjDAO.deleteApplication(objODCApplicationVO);
			
			ApplicationVO appVO = (ApplicationVO) objValueObjectProj;

			logger.info(appVO.getServerName() +" <=> "+ appVO.getApplicationName() +" <=> "+ appVO.getApplicationPath() +" <=> "+ Integer.parseInt(appVO.getNumberOfUsers())  +" <=> "+ appVO.getIdPrefix() +" <=> "+ "/home/griddir");
			rmclient = new RemoteAdminClient(appVO.getServerName(), REMOTE_ADMIN_PORT, KEY_STORE,KEY_STORE_PW,TRUST_STORE,TRUST_STORE_PW);
			rmclient.deleteApplication( appVO.getApplicationName() );

			transaction.commit();
			logger.info("Transaction Status : "+ transaction.getStatus() );
			
		}catch (Exception exp) {
			logger.error(exp);
			exp.printStackTrace();
			try {
				if (logger.isErrorEnabled()) {
					logger.error("Rolling back because of  :: ",exp);
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
					logger.error("Rolling back in because of  :: ",exp);
				}
				transaction.rollback();
			} catch (SystemException se) {
				logger.error("System Exception is: "+ se);
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
			logger.error("System Exception is: "+ e);
			e.printStackTrace();
		}
		logger.info("<- findSingleApplication_UserBO");
		return collApplications;
	}

		




	public static void main(String[] args) {
	}
}
