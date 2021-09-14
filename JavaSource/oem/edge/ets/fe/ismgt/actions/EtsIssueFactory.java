package oem.edge.ets.fe.ismgt.actions;

import oem.edge.ets.fe.ismgt.model.*;
import oem.edge.ets.fe.ismgt.resources.*;
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
 * The class which returns the EtsIssChgObject bean based on variou states and which implements Factory IF
 */
public class EtsIssueFactory implements EtsIssueFactoryIF, EtsIssueConstants {
	
	public static final String VERSION = "1.12";

	/**
	 * Constructor for EtsIssueFactory.
	 */
	public EtsIssueFactory() {
		super();
	}

	public EtsIssChgActionBean createEtsIssChgActionBean(EtsIssObjectKey etsObjKey) throws Exception {

		int actionkey = etsObjKey.getActionkey();

		switch (actionkey) {

			
            case FEEDBACK :

                return new EtsFeedbackProcess(etsObjKey);
                
            
		}

		return null;
	} //end of method

} //end of class

