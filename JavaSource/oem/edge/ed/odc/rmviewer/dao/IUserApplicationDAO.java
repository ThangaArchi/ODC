/*
 * Created on Mar 21, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ed.odc.rmviewer.dao;

import java.util.Collection;

import oem.edge.ed.odc.rmviewer.actions.UserApplicationForm;
import oem.edge.ed.odc.rmviewer.vo.UserApplicationVO;

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

public interface IUserApplicationDAO {

	public UserApplicationVO insert(UserApplicationVO objUserApplicationVO) throws Exception;
	
	public Collection findAllUserApplication() throws Exception;

	public Collection findSingleUserApplication(UserApplicationForm objUserApplicationForm) throws Exception;

	public Collection findSingleUserNonApplication(UserApplicationForm objUserApplicationForm) throws Exception;
	
	public Collection findAllUserOnly() throws Exception;
	
	public UserApplicationVO deleteUserApplication(UserApplicationVO objUserApplicationVO) throws Exception;

}
