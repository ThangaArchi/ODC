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

package oem.edge.ed.odc.rmviewer;

import oem.edge.ed.odc.rmviewer.dao.IApplicationDAO;
import oem.edge.ed.odc.rmviewer.dao.IProjectDAO;
import oem.edge.ed.odc.rmviewer.dao.IServerDAO;
import oem.edge.ed.odc.rmviewer.dao.IUserApplicationDAO;
import oem.edge.ed.odc.rmviewer.dao.IUserProjectDAO;

/**
 * @author tkandhas@in.ibm.com
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public abstract class ODCDAOFactory {

	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";
	public static final int JDBC = 1;
	public static final int ORM = 2;
	public static final int SPRING = 3;
	  
	public static ODCDAOFactory getDAOFactory(int factory) {
  
		switch (factory) {
			case JDBC: 
					  	return new JDBC_ODC_DAOFactory();
			/*
			case ORM    : 
					  return new ORMDAOFactory();
			case SPRING    : 
					 return new SpringDAOFactory();
					 
					 */
			default           : 
					return new JDBC_ODC_DAOFactory(); 
		}		
	 }
	 
	public abstract IUserProjectDAO getUserProjectDAO();
	
	public abstract IProjectDAO getProjectDAO();
	
	public abstract IApplicationDAO getApplicationDAO();
	
	public abstract IUserApplicationDAO getUserApplicationDAO();
	
	public abstract IServerDAO getServerDAO();
}