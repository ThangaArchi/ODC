/*
 * Created on Mar 21, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ed.odc.rmviewer.dao;

import java.util.Collection;

import oem.edge.ed.odc.rmviewer.actions.ApplicationForm;
import oem.edge.ed.odc.rmviewer.vo.ApplicationVO;
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
public interface IApplicationDAO {

	/**
	 * @param objODCApplicationVO
	 * @return
	 */
	public ApplicationVO insert(ValueObject objODCApplicationVO);

	/**
	 * @param objODCApplicationVO
	 * @return
	 */
	public ApplicationVO deleteApplication(ValueObject objODCApplicationVO);

	/**
	 * @param objODCApplicationVO
	 * @return
	 */
	public ApplicationVO deleteUserApplication(ValueObject objODCApplicationVO);


	/**
	 * @param objODCApplicationVO
	 * @return
	 */
	public ApplicationVO editUserApplication(ValueObject objODCApplicationVO);

	/**
	 * @return
	 */
	public Collection findAllApplications();

	/**
	 * @return
	 */
	public Collection findSingleApplication_User(ApplicationForm objApplicationForm);

}
