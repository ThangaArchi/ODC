package oem.edge.ets.fe.ismgt.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.ismgt.model.EtsIssFilterObjectKey;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004                                          */
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
 * @author v2phani
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class EtsIssTypesWelcomeCmd extends FilterCommandAbsBean {

	/**
	 * Constructor for EtsIssFilterWelcomeCmd.
	 * @param request
	 * @param response
	 * @param issobjkey
	 */
	public EtsIssTypesWelcomeCmd(HttpServletRequest request, HttpServletResponse response, EtsIssFilterObjectKey issobjkey) {
		super(request, response, issobjkey);
	}
	
	
	/**
	 * key process request method
	 */
	
	public int processRequest()  {
		
		int processreq = 2200;
		
				
		return processreq;	
		
		
	}

}

