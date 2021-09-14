/*
 * Created on Mar 21, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ed.odc.rmviewer.bdlg;

import java.util.Collection;

/* import oem.edge.ed.odc.rmviewer.actions.UserApplicationForm;
import oem.edge.ed.odc.rmviewer.bo.ODCProjectBO;
import oem.edge.ed.odc.rmviewer.bo.ODCUserApplicationBO;
import oem.edge.ed.odc.rmviewer.vo.UserApplicationVO; */
import oem.edge.ed.odc.rmviewer.actions.UserProjectForm;
import oem.edge.ed.odc.rmviewer.bo.ODCUserProjectBO;
import oem.edge.ed.odc.rmviewer.vo.UserProjectVO;
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
public class ODCUserProjectBDelegate {
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.1";	
	private static Log logger = ODCLogger.getLogger(ODCUserProjectBDelegate.class);

	ODCUserProjectBO objUserProjectBO = null;
	public ODCUserProjectBDelegate()
	{
		objUserProjectBO = new ODCUserProjectBO();
	}
	
	public ValueObject createUserProject(ValueObject objProjectVO){
		return objUserProjectBO.createUserProjectBO(objProjectVO);
	}
	
	public ValueObject deleteUserProject(ValueObject objProjectVO){
		return objUserProjectBO.deleteUserProjectBO(objProjectVO);
	}
	
	public Collection findAllUserProject() //throws 
	{
		return objUserProjectBO.findAllUserProjectBO();
	}
	
	public Collection findSingleUserProject(UserProjectForm objUserProjectForm) //throws 
	{
		return objUserProjectBO.findSingleUserProjectBO(objUserProjectForm);
	}
	
	public Collection findSingleUserNonProject(UserProjectForm objUserProjectForm) //throws 
	{
		return objUserProjectBO.findSingleUserNonProjectBO(objUserProjectForm);
	}
	
	public Collection findAllUserOnly() //throws 
	{
		return objUserProjectBO.findAllUserOnlyBO();
	}

	public UserProjectVO addUserProject(UserProjectVO objUserProjectVO) throws Exception //throws 
	{
		return (UserProjectVO) objUserProjectBO.addUserProjectBO(objUserProjectVO);
	}

	public UserProjectVO deleteUserProject(UserProjectVO objUserProjectVO) throws Exception //throws 
	{
		return (UserProjectVO) objUserProjectBO.deleteUserProjectBO(objUserProjectVO);
	}


	public static void main(String[] args) {
	}
}
