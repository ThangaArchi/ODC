/*
 * Created on Mar 21, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ed.odc.remoteviewer.bdlg;

import java.util.Collection;

import oem.edge.ed.odc.remoteviewer.bo.ODCProjectBO;
import oem.edge.ed.odc.utils.ODCLogger;
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
public class ODCProjectBDelegate {
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";	
	private static Log logger = ODCLogger.getLogger(ODCProjectBDelegate.class);

	ODCProjectBO objODCProjectBO = null;
	public ODCProjectBDelegate()
	{
		objODCProjectBO = new ODCProjectBO();
	}
	
	public ValueObject createProject(ValueObject objProjectVO) throws Exception{
		return objODCProjectBO.createProjectBO(objProjectVO);
	}
	
	public ValueObject deleteProject(ValueObject objProjectVO) throws Exception{
		return objODCProjectBO.deleteProjectBO(objProjectVO);
	}
	
	public Collection findAllProjects() //throws 
	{
		return objODCProjectBO.findAllProjectsBO();
	}
	
	public static void main(String[] args) {
	}
}
