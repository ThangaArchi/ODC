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

package oem.edge.ets.fe.teamgroup;

/**
 * This Class defines constants used across the Groups Module
 * @author vishal
 */
public class GroupConstants {

	public static final String REQ_ATTR_TITLE = "group.page.title";
	public static final String MSG_USER_ERROR = "teamgroup.messages";



	public static final int WIDTH_NAME = 239;
	public static final int WIDTH_MOD = 100;
	public static final int WIDTH_TYPE = 50;
	public static final int WIDTH_AUTHOR = 170;
	public static final int WIDTH_INFO = 25;

	public static final int REPO_WIDTH_NAME = 250;
	public static final int REPO_WIDTH_MOD = 150;
	public static final int REPO_WIDTH_SIZE = 100;
	public static final int REPO_WIDTH_HITS = 100;

	public static final String TYPE_PROJECT = "P";

	public static final String PARAM_PROJECTID = "proj";
	public static final String PARAM_TOPCATEGORY = "tc";
	public static final String PARAM_CURCATEGORY = "cc";
	public static final String PARAM_LINKID = "linkid";
	public static final String PARAM_SELF = "self";
	public static final String PARAM_SET = "set";
	public static final String PARAM_SETMET = "setmet";
	public static final String PARAM_GROUPID = "grpid";
	
	public static final String ACTION_GROUP = "grpaction";
	public static final String ACTION_ADD_GROUP = "addgroup";
	public static final String ACTION_UPDATE_GROUP = "updategroup";
	public static final String ACTION_SHOW_ALL_GROUPS = "showallgroups";

	public static final String GROUP_FORM = "groupForm";

	public static final char FALSE_FLAG = '0';
	public static final char TRUE_FLAG = '1';
	public static final char NOT_SET_FLAG = 'x';
	public static final String IND_YES = "Y";
	public static final String IND_NO = "N";
	public static final String DELETED_PROJECT_FLAG = "D";


	public static final String REQ_ATTR_USERROLE = "group.userRole";
	public static final String REQ_ATTR_EDGEACCESS = "group.edgeAccess";
	public static final String REQ_ATTR_PRIMARYCONTACT =
		"document.primaryContact";

	public static final String DECAFTYPE_INTERNAL = "I";


	// Group PROPERTIES
	public static final int MAX_NAME_LENGTH = 50;
	public static final int MAX_DESCRIPTION_LENGTH = 2000;
	public static final int MAX_KEYWORD_LENGTH = 500;

	public static final String SEL_OPT_NONE = "X";
	public static final String FRM_CTX_SUBMIT = "Submit";

	public static final String ETS_PUBLIC = "0";
	public static final String ETS_IBM_ONLY = "1";
	public static final String ETS_IBM_CONF = "2";
	public static final String ETS_PRIVATE_PUBLIC_GROUP = "3";
	public static final String ETS_PRIVATE_IBM_ONLY_GROUP = "4";
	
	public static final String PROJECT_TYPE_AIC = "AIC";

	public static final String MSG_DATA_SEP = ":";

}