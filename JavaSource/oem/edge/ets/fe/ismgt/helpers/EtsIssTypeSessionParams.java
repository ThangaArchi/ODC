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

package oem.edge.ets.fe.ismgt.helpers;

import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssTypeInfoModel;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssTypeSessionParams {

	public static final String VERSION = "1.10";

	private EtsIssFilterSessnHandler etsIssSessn;
	private EtsIssObjectKey issObjKey;
	private String uniqIssueSessnKey;

	/**
	 * 
	 */
	public EtsIssTypeSessionParams(EtsIssObjectKey issObjKey) {
		super();
		this.etsIssSessn = new EtsIssFilterSessnHandler(issObjKey.getRequest().getSession(true));
		this.issObjKey = issObjKey;
		this.uniqIssueSessnKey = "ETSISSUETYPE" + issObjKey.getProj().getProjectId() + issObjKey.getIssueClass() + issObjKey.getActionkey(); //to make a unique combination
	}

	/**
				 * This method will check in session, if a EtsIssTypeInfoModel object is there,
				 * if not in session, returns null , otw returns object 
				 */

	public void setSessnIssTypeInfoModel(EtsIssTypeInfoModel issTypeInfoModel, String edgeProblemId) {

		String uniqActionSessnKey = uniqIssueSessnKey + edgeProblemId;

		etsIssSessn.setSessionObjValue(uniqActionSessnKey + "EtsIssTypeInfoModel", issTypeInfoModel);

	}
	/**
	 * 
	 * @param edgeProblemId
	 * @return
	 */
	public EtsIssTypeInfoModel getSessnIssTypeInfoModel(String edgeProblemId) {

		String uniqActionSessnKey = uniqIssueSessnKey + edgeProblemId;

		return (EtsIssTypeInfoModel) etsIssSessn.getSessionObjValue(uniqActionSessnKey + "EtsIssTypeInfoModel");

	}

}
