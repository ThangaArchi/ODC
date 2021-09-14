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
package oem.edge.ets.fe.ismgt.actions;

import oem.edge.ets.fe.ismgt.model.*;
import oem.edge.ets.fe.ismgt.model.*;



/**
 * @author v2phani
 * Return an instance of the appropriate subclass of Issue/change as determined from 
 * information provided by the given Key Object
 */
public interface EtsIssueFactoryIF {
	
	public EtsIssChgActionBean createEtsIssChgActionBean(EtsIssObjectKey etsObjKey) throws Exception;

}

