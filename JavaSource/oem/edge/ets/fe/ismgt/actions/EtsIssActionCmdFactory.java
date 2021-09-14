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

import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssActionCmdFactory implements EtsIssueConstants {

	public static final String VERSION = "1.39";

	/**
	 * 
	 */
	public EtsIssActionCmdFactory() {
		super();
		// TODO Auto-generated constructor stub
	}

	public EtsIssActionCmdAbs createEtsIssActionCmd(EtsIssObjectKey etsObjKey) throws Exception {

		int actionkey = etsObjKey.getActionkey();

		switch (actionkey) {

			case SUBMITISSUE :

				return new EtsIssSubmitNewCmd(etsObjKey);

			case VIEWISSUE :

				return new EtsIssViewIssueCmd(etsObjKey);

			case MODIFYISSUE :

				return new EtsIssModifyCmd(etsObjKey);

			case RESOLVEISSUE :
			case REJECTISSUE :
			case CLOSEISSUE :
			case COMMENTISSUE :
			case WITHDRAW:

				return new EtsIssResolveCmd(etsObjKey);

			case REQNEWISSUETYPE :

				return new EtsIssReqCreateNewIssTypeCmd(etsObjKey);

			case SUBMITCHANGE :

				return new EtsCrSubmitNewCmd(etsObjKey);

			case VIEWCHANGE :

				return new EtsCrViewCmd(etsObjKey);

			case COMMENTCHANGE :

				return new EtsCrUpdateCmd(etsObjKey);
				
			case CHGISSOWNER :
			
				return new EtsChgOwnerCmd(etsObjKey);	
				
			case SUBSCRIBEISSUE:
			
			case UNSUBSCRIBEISSUE :
			
			return new EtsIssSubscrToIssueCmd(etsObjKey);
		}

		return null;

	}

} //end of class
