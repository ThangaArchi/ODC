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

package oem.edge.ed.odc.remoteviewer;

import oem.edge.ed.odc.remoteviewer.dao.IServerDAO;
import oem.edge.ed.odc.remoteviewer.dao.IUserApplicationDAO;

import oem.edge.ed.odc.remoteviewer.dao.IApplicationDAO;
import oem.edge.ed.odc.remoteviewer.dao.IProjectDAO;
import oem.edge.ed.odc.remoteviewer.dao.IServerDAO;
import oem.edge.ed.odc.remoteviewer.dao.IUserApplicationDAO;
import oem.edge.ed.odc.remoteviewer.dao.IUserProjectDAO;

import oem.edge.ed.odc.remoteviewer.dao.ApplicationDAO;
import oem.edge.ed.odc.remoteviewer.dao.ProjectDAO;
import oem.edge.ed.odc.remoteviewer.dao.ServerDAO;
import oem.edge.ed.odc.remoteviewer.dao.UserApplicationDAO;
import oem.edge.ed.odc.remoteviewer.dao.UserProjectDAO;

/**
 * @author tkandhas@in.ibm.com
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class JDBC_ODC_DAOFactory extends ODCDAOFactory {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";
		
	public IUserProjectDAO getUserProjectDAO(){
		return new UserProjectDAO();
	}
	
	public IProjectDAO getProjectDAO(){
		return new ProjectDAO();
	}
	
	public IApplicationDAO getApplicationDAO(){
		return new ApplicationDAO();
	}
	
	public IUserApplicationDAO getUserApplicationDAO(){
		return new UserApplicationDAO();
	}
	
	public IServerDAO getServerDAO(){
		return new ServerDAO();
	}
}
