/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004-2004                                     */
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

package oem.edge.ets.fe.ismgt.bdlg;

import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;

/**
 * @author V2PHANI
 * This class generates the appropriate data prep object
 * to reflect the state of current action (new/modify/resolve/close/comment)
 *
 */
public class EtsIssActionDataPrepFactory implements EtsIssueConstants {
	
	public static final String VERSION = "1.36";

	/**
	 * constructor
	 */
	public EtsIssActionDataPrepFactory() {

		super();

	}

	public EtsIssActionDataPrepAbsBean createActionDataPrepAbsBean(EtsIssObjectKey etsIssObjKey) throws Exception {

		int actionkey = etsIssObjKey.getActionkey();

		switch (actionkey) {

			case SUBMITISSUE :

				//return new EtsIssSubmitNewDataPrep(etsIssObjKey);

		}

		return null;

	}

} //end of 
