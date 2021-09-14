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
import oem.edge.ed.odc.rmviewer.actions.UserProjectForm;
import oem.edge.ed.odc.rmviewer.dao.IUserProjectDAO;
import oem.edge.ed.odc.rmviewer.vo.UserProjectVO;
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
public class ODCUserProjectBO {

	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.2";
		
	private static Log logger = ODCLogger.getLogger(ODCUserProjectBO.class);


	public ValueObject addUserProjectBO(UserProjectVO objUserProjectVO) throws Exception
	{
		logger.info("-> createUserProjectBO() ");

		ODCDAOFactory factory = ODCDAOFactory.getDAOFactory(ODCDAOFactory.JDBC);
		IUserProjectDAO objUserProjectDAO = factory.getUserProjectDAO();
		ODCServiceLocator objODCServiceLocator = ODCServiceLocator.getInstance();

		UserTransaction transaction = objODCServiceLocator.getUserTransaction( ODCServiceLocator.USER_TRANSACTION );
		UserProjectVO objValueObjectProj = null;

		try {
			transaction.begin();
			objValueObjectProj = ( UserProjectVO ) objUserProjectDAO.insert(objUserProjectVO);
			transaction.commit();
		}catch (Exception exp) {
			System.out.println(exp);
			try {
				if (logger.isErrorEnabled()) {
					logger.error("Rolling back in addRowsToTable because of  :: ",exp);
				}
				transaction.rollback();
			} catch (SystemException se) {
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("<- createUserProjectBO ");
		}
		return objValueObjectProj;
	}


	public ValueObject deleteUserProjectBO(UserProjectVO objUserProjectVO) throws Exception
	{
		logger.info("-> deleteUserProjectBO() ");

		ODCDAOFactory factory = ODCDAOFactory.getDAOFactory(ODCDAOFactory.JDBC);
		IUserProjectDAO objUserProjectDAO = factory.getUserProjectDAO();
		ODCServiceLocator objODCServiceLocator = ODCServiceLocator.getInstance();

		UserTransaction transaction = objODCServiceLocator.getUserTransaction( ODCServiceLocator.USER_TRANSACTION );
		UserProjectVO objValueObjectProj = null;

		try {
			transaction.begin();
			objValueObjectProj = ( UserProjectVO ) objUserProjectDAO.deleteUserProject(objUserProjectVO);
			transaction.commit();
		}catch (Exception exp) {
			System.out.println(exp);
			try {
				if (logger.isErrorEnabled()) {
					logger.error("Rolling back in addRowsToTable because of  :: ",exp);
				}
				transaction.rollback();
			} catch (SystemException se) {
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("<- deleteUserProjectBO ");
		}
		return objValueObjectProj;
	}

	public Collection findAllUserProjectBO()
	{
		logger.info("-> findAllUserProjectBO");
		Collection collProjects = null;
		try {
			ODCDAOFactory factory = ODCDAOFactory.getDAOFactory(ODCDAOFactory.JDBC);
			IUserProjectDAO objUserProjectDAO = factory.getUserProjectDAO();
			ODCServiceLocator objODCServiceLocator = ODCServiceLocator.getInstance();
			
			collProjects = objUserProjectDAO.findAllUserProject();
			
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		logger.info("<- findAllUserProjectBO");
		System.out.println( collProjects.size() );
		return collProjects;
	}
			
		
		
	public Collection findSingleUserProjectBO(UserProjectForm objUserProjectForm)
	{
		logger.info("-> findAllUserProjectBO");
		Collection collUserProject = null;
		Collection collProject = null;
		try {
			ODCDAOFactory factory = ODCDAOFactory.getDAOFactory(ODCDAOFactory.JDBC);
			IUserProjectDAO objUserProjectDAO = factory.getUserProjectDAO();
			ODCServiceLocator objODCServiceLocator = ODCServiceLocator.getInstance();
			
			collUserProject = objUserProjectDAO.findSingleUserProject(objUserProjectForm);
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		logger.info("<- findAllUserProjectBO");
		System.out.println( collUserProject.size() );
		return collUserProject;
	}
			
		
	public Collection findSingleUserNonProjectBO(UserProjectForm objUserProjectForm)
	{
		logger.info("-> findAllUserProjectBO");
		Collection collUserProject = null;
		Collection collProject = null;
		try {
			ODCDAOFactory factory = ODCDAOFactory.getDAOFactory(ODCDAOFactory.JDBC);
			IUserProjectDAO objUserProjectDAO = factory.getUserProjectDAO();
			ODCServiceLocator objODCServiceLocator = ODCServiceLocator.getInstance();
			
			collUserProject = objUserProjectDAO.findSingleUserNonProject(objUserProjectForm);
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		logger.info("<- findAllUserProjectBO");
		return collUserProject;
	}
			
		
		
	
	public Collection findAllUserOnlyBO()
	{
		logger.info("-> findAllUserProjectBO");
		Collection collProjects = null;
		try {
			ODCDAOFactory factory = ODCDAOFactory.getDAOFactory(ODCDAOFactory.JDBC);
			IUserProjectDAO objUserProjectDAO = factory.getUserProjectDAO();
			ODCServiceLocator objODCServiceLocator = ODCServiceLocator.getInstance();
		
			collProjects = objUserProjectDAO.findAllUserOnly();
		
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		logger.info("<- findAllUserProjectBO");
		System.out.println( collProjects.size() );
		return collProjects;
	}
		
	



	/**
	 * @param objProjectVO
	 * @return
	 */
	public ValueObject createUserProjectBO(ValueObject objProjectVO) {
		// TODO Auto-generated method stub
		return null;
	}


		




	public static void main(String[] args) {
	}


	/**
	 * @param objProjectVO
	 * @return
	 */
	public ValueObject deleteUserProjectBO(ValueObject objProjectVO) {
		// TODO Auto-generated method stub
		return null;
	}

}
