/*
 * Created on Mar 21, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ed.odc.remoteviewer.bdlg;

import java.util.Collection;

import oem.edge.ed.odc.remoteviewer.actions.ApplicationForm;
import oem.edge.ed.odc.remoteviewer.bo.ODCApplicationBO;
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
public class ODCApplicationBDelegate {
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";	
	private static Log logger = ODCLogger.getLogger(ODCApplicationBDelegate.class);

	ODCApplicationBO objODCApplicationBO = null;
	public ODCApplicationBDelegate()
	{
		objODCApplicationBO = new ODCApplicationBO();
	}
	
	public ValueObject createApplication(ValueObject objApplicationVO) throws Exception{
		return objODCApplicationBO.createApplicationBO(objApplicationVO);
	}
	
	public ValueObject deleteApplication(ValueObject objApplicationVO) throws Exception{
		return objODCApplicationBO.deleteApplicationBO(objApplicationVO);
	}
	
	public ValueObject editApplication(ValueObject objApplicationVO) throws Exception{
		return objODCApplicationBO.editApplicationBO(objApplicationVO);
	}
	
	public Collection findAllApplications() //throws 
	{
		return objODCApplicationBO.findAllApplicationsBO();
	}
	
	public Collection findSingleUserApplication(ApplicationForm objApplicationForm){
		return objODCApplicationBO.findSingleApplication_UserBO(objApplicationForm);
	}
	
	public static void main(String[] args) {
	}
}
