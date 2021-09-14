/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005-2008                                     */
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

package oem.edge.ets.fe.ismgt.actions;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.ismgt.model.EtsIssFilterObjectKey;

/**
 * @author Dharanendra Prasad
 *
 */
public class EtsCustFieldsWelcomeCmd extends FilterCommandAbsBean {
	
	/**
	 * Constructor for EtsCustFieldsWelcomeCmd.
	 * @param request
	 * @param response
	 * @param issobjkey
	 */
	public EtsCustFieldsWelcomeCmd(HttpServletRequest request, HttpServletResponse response, EtsIssFilterObjectKey issobjkey) {
		super(request, response, issobjkey);
	}
		

	
	/**
	 * key process request method
	 */
	public int processRequest()  {
		int processreq = 2400;
		return processreq;				
	}	

}
