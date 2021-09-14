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

package oem.edge.ets.fe.ismgt.helpers;

/**
 * @author Dharanendra Prasad
 *
 */
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsCustFieldInfoModel;;

public class EtsCustFieldSessionParams {

	public static final String VERSION = "1.10";

	private EtsIssFilterSessnHandler etsIssSessn;
	private EtsIssObjectKey issObjKey;
	private String uniqCustFieldSessnKey;

	/**
	 * 
	 */
	public EtsCustFieldSessionParams(EtsIssObjectKey issObjKey) {
		super();
		this.etsIssSessn = new EtsIssFilterSessnHandler(issObjKey.getRequest().getSession(true));
		this.issObjKey = issObjKey;
		this.uniqCustFieldSessnKey = "ETSCUSTFIELD" + issObjKey.getProj().getProjectId() + issObjKey.getIssueClass() + issObjKey.getActionkey(); //to make a unique combination
	}

	/**
				 * This method will check in session, if a EtsCustFieldInfoModel object is there,
				 * if not in session, returns null , otw returns object 
				 */

	public void setSessnCustFieldInfoModel(EtsCustFieldInfoModel custFieldInfoModel, String edgeProblemId) {

		String uniqActionSessnKey = uniqCustFieldSessnKey + edgeProblemId;

		etsIssSessn.setSessionObjValue(uniqActionSessnKey + "EtsCustFieldInfoModel", custFieldInfoModel);

	}
	/**
	 * 
	 * @param edgeProblemId
	 * @return
	 */
	public EtsCustFieldInfoModel getSessnCustFieldInfoModel(String edgeProblemId) {

		String uniqActionSessnKey = uniqCustFieldSessnKey + edgeProblemId;

		return (EtsCustFieldInfoModel) etsIssSessn.getSessionObjValue(uniqActionSessnKey + "EtsCustFieldInfoModel");

	}

}
