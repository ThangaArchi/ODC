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

import org.apache.commons.logging.Log;

import oem.edge.ed.odc.remoteviewer.ODCDAOFactory;
import oem.edge.ed.odc.remoteviewer.dao.IProjectDAO;
import oem.edge.ed.odc.remoteviewer.dao.IServerDAO;
import oem.edge.ed.odc.remoteviewer.dao.ODCProjectDAO;
import oem.edge.ed.odc.remoteviewer.vo.ProjectVO;
import oem.edge.ed.odc.remoteviewer.vo.ServerVO;
import oem.edge.ed.odc.utils.CommonProperties;
import oem.edge.ed.odc.utils.ODCLogger;
import oem.edge.ed.odc.utils.ODCServiceLocator;
import oem.edge.ed.odc.utils.PropertyTester;
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
public class ODCServerBO {

	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.2";
		
	private static Log logger = ODCLogger.getLogger(ODCServerBO.class);

	CommonProperties comProp = null;
	int REMOTE_ADMIN_PORT;
	
	public ODCServerBO(){
		try {
			comProp = CommonProperties.getInstance();
			REMOTE_ADMIN_PORT = Integer.parseInt( comProp.getCommonProperty("ODC.Common.RemoteAdmin.Port") );
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	public ValueObject createServerBO(ValueObject objODCServerVO) throws Exception
	{
		logger.info("-> createServerBO() ");

		ODCDAOFactory factory = ODCDAOFactory.getDAOFactory(ODCDAOFactory.JDBC);
		IServerDAO objServDAO = factory.getServerDAO();
		ODCServiceLocator objODCServiceLocator = ODCServiceLocator.getInstance();
		UserTransaction transaction = objODCServiceLocator.getUserTransaction( ODCServiceLocator.USER_TRANSACTION );
		ValueObject objValueObjectServ = null;

		try {
			transaction.begin();
			objValueObjectServ = ( ServerVO ) objServDAO.insert(objODCServerVO);
			transaction.commit();
		}catch (Exception ex) {
			logger.error(ex);
			ex.printStackTrace();
			try {
				if (logger.isErrorEnabled()) {
					logger.error("Rolling back in because of  :: ",ex);
				}
				transaction.rollback();
			} catch (SystemException se) {
				logger.error("System Exception is: "+ se);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("<- createServerBO ");
		}
		return objValueObjectServ;
	}


	public Collection findAllServer()
	{
		logger.info("-> findAllServers");
		Collection collServers = null;
		try {
			ODCDAOFactory factory = ODCDAOFactory.getDAOFactory(ODCDAOFactory.JDBC);
			IServerDAO objServDAO = factory.getServerDAO();
			ODCServiceLocator objODCServiceLocator = ODCServiceLocator.getInstance();
			//UserTransaction transaction = objODCServiceLocator.getUserTransaction( ODCServiceLocator.USER_TRANSACTION );
			collServers = objServDAO.findAllServer();
			
//collServers = objServDAO.findAllServer();
	
		} catch (Exception ex) {
			logger.error(ex);
			ex.printStackTrace();
		}
		logger.info("<- findAllServers");
		return collServers;
	}
	



		
	public Collection findServersByProjectName(ValueObject objSerVO)
	{
		logger.info("-> findServersByProjectName");
		Collection collServers = null;
		try {
			ODCDAOFactory factory = ODCDAOFactory.getDAOFactory(ODCDAOFactory.JDBC);
			IServerDAO objServDAO = factory.getServerDAO();
			ODCServiceLocator objODCServiceLocator = ODCServiceLocator.getInstance();
			ValueObject objValueObjectServ = (ServerVO) objServDAO.findServersByProjectName(objSerVO);
	
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		logger.info("<- findServersByProjectName");
		return collServers;
	}

	public Collection findUniqueServer()
   	{
      		logger.info("-> findUniqueServer");
      		Collection collServers = null;
      		try {
         		ODCDAOFactory factory = ODCDAOFactory.getDAOFactory(ODCDAOFactory.JDBC);
         		IServerDAO objServDAO = factory.getServerDAO();
         		ODCServiceLocator objODCServiceLocator = ODCServiceLocator.getInstance();
         		collServers = objServDAO.findUniqueServer();
         
      		} catch (Exception ex) {
         	logger.error(ex);
         	ex.printStackTrace();
      		}
      	logger.info("<- findUniqueServer");
      	return collServers;
   	}

	
}
