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
import oem.edge.ed.odc.rmviewer.dao.IProjectDAO;
import oem.edge.ed.odc.rmviewer.dao.ODCProjectDAO;
import oem.edge.ed.odc.rmviewer.vo.ProjectVO;
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
public class ODCProjectBO {

	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.2";
		
	private static Log logger = ODCLogger.getLogger(ODCProjectBO.class);


	public ValueObject createProjectBO(ValueObject objODCProjectVO) throws Exception
	{
		logger.info("-> createProjectBO() ");

		ODCDAOFactory factory = ODCDAOFactory.getDAOFactory(ODCDAOFactory.JDBC);
		IProjectDAO objProjDAO = factory.getProjectDAO();
		ODCServiceLocator objODCServiceLocator = ODCServiceLocator.getInstance();
		UserTransaction transaction = objODCServiceLocator.getUserTransaction( ODCServiceLocator.USER_TRANSACTION );
		ValueObject objValueObjectProj = null;

		try {
			transaction.begin();
			objValueObjectProj = ( ProjectVO ) objProjDAO.insert(objODCProjectVO);
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
			logger.info("<- createProjectBO ");
		}
		return objValueObjectProj;
	}


	public ValueObject deleteProjectBO(ValueObject objODCProjectVO) throws Exception
	{
		logger.info("-> deleteProjectBO() ");

		ODCDAOFactory factory = ODCDAOFactory.getDAOFactory(ODCDAOFactory.JDBC);
		IProjectDAO objProjDAO = factory.getProjectDAO();
		ODCServiceLocator objODCServiceLocator = ODCServiceLocator.getInstance();
		UserTransaction transaction = objODCServiceLocator.getUserTransaction( ODCServiceLocator.USER_TRANSACTION );
		ValueObject objValueObjectProj = null;

		try {
			transaction.begin();

			objValueObjectProj = ( ProjectVO ) objProjDAO.deleteUserProject(objODCProjectVO);
			objValueObjectProj = ( ProjectVO ) objProjDAO.deleteProject(objODCProjectVO);

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
			logger.info("<- deleteProjectBO ");
		}
		return objValueObjectProj;
	}


	public Collection findAllProjectsBO()
	{
		logger.info("-> findAllProjects");
		Collection collProjects = null;
		try {
			ODCDAOFactory factory = ODCDAOFactory.getDAOFactory(ODCDAOFactory.JDBC);
			IProjectDAO objProjDAO = factory.getProjectDAO();
			ODCServiceLocator objODCServiceLocator = ODCServiceLocator.getInstance();
			ValueObject objValueObjectProj = null;
	
			collProjects = objProjDAO.findAllProjects();
	
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		logger.info("<- findAllProjects");
		return collProjects;
	}
	



		




	public static void main(String[] args) {
	}
}
