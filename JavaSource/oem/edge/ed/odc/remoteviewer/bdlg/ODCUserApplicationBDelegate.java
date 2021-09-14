/*
 * Created on Mar 21, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ed.odc.remoteviewer.bdlg;

import java.util.Collection;

import oem.edge.ed.odc.remoteviewer.actions.UserApplicationForm;
import oem.edge.ed.odc.remoteviewer.bo.ODCProjectBO;
import oem.edge.ed.odc.remoteviewer.bo.ODCUserApplicationBO;
import oem.edge.ed.odc.remoteviewer.vo.UserApplicationVO;
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
public class ODCUserApplicationBDelegate {
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";	
	private static Log logger = ODCLogger.getLogger(ODCUserApplicationBDelegate.class);

	ODCUserApplicationBO objUserApplicationBO = null;
	public ODCUserApplicationBDelegate()
	{
		objUserApplicationBO = new ODCUserApplicationBO();
	}
	
	public ValueObject createUserApplication(ValueObject objProjectVO){
		return objUserApplicationBO.createUserApplicationBO(objProjectVO);
	}
	
	public ValueObject deleteUserApplication(ValueObject objProjectVO){
		return objUserApplicationBO.deleteUserApplicationBO(objProjectVO);
	}
	
	public Collection findAllUserApplication() //throws 
	{
		return objUserApplicationBO.findAllUserApplicationBO();
	}
	
	public Collection findSingleUserApplication(UserApplicationForm objUserApplicationForm) //throws 
	{
		return objUserApplicationBO.findSingleUserApplicationBO(objUserApplicationForm);
	}
	
	public Collection findSingleUserNonApplication(UserApplicationForm objUserApplicationForm) //throws 
	{
		return objUserApplicationBO.findSingleUserNonApplicationBO(objUserApplicationForm);
	}
	
	public Collection findAllUserOnly() //throws 
	{
		return objUserApplicationBO.findAllUserOnlyBO();
	}

	public UserApplicationVO addUserApplication(UserApplicationVO objUserApplicationVO) throws Exception //throws 
	{
		return (UserApplicationVO) objUserApplicationBO.addUserApplicationBO(objUserApplicationVO);
	}

	public UserApplicationVO deleteUserApplication(UserApplicationVO objUserApplicationVO) throws Exception //throws 
	{
		return (UserApplicationVO) objUserApplicationBO.deleteUserApplicationBO(objUserApplicationVO);
	}


	public static void main(String[] args) {
	}
}
