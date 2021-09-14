/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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
package oem.edge.ets.fe.acmgt.resources;

/**
 * @author Suresh
 */

public interface WrkSpcAddMemberContantsIF extends WrkSpcTeamConstantsIF{
	
	public static final String TYPE_PROJECT = "P";

	public static final String PARAM_PROJECTID = "proj";
	public static final String PARAM_TOPCATEGORY = "tc";
	public static final String PARAM_CURCATEGORY = "cc";
	public static final String PARAM_LINKID = "linkid";
	public static final String PARAM_SELF = "self";
	public static final String PARAM_SET = "set";
	public static final String PARAM_SETMET = "setmet";
		
	public static final String ADD_MEMBR_FORM = "addMembrForm";
	public static final String DECAFTYPE_INTERNAL = "I";
	
	public static final String REQ_ATTR_USERROLE = "addMembr.userRole";
	public static final String REQ_ATTR_EDGEACCESS = "addMembr.edgeAccess";
	public static final String REQ_ATTR_PRIMARYCONTACT =
		"document.primaryContact";
	
	public static final String SEL_OPT_NONE = "X";
	public static final String FRM_CTX_SUBMIT = "Submit";

	public static final String ETS_PUBLIC = "0";
	public static final String ETS_IBM_ONLY = "1";
	public static final String ETS_IBM_CONF = "2";


	public static final String PROJECT_TYPE_AIC = "AIC";

	public static final String MSG_DATA_SEP = ":";

}
