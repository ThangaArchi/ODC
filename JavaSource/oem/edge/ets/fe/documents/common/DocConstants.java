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

package oem.edge.ets.fe.documents.common;

/**
 * This Class defines constants used across the Documents Module
 * @author v2srikau
 */
public class DocConstants {

	public static final String REQ_ATTR_TITLE = "documents.page.title";
	public static final String MSG_USER_ERROR = "documents.messages";

	public static final String SESS_ATTR_PROCERROR =
		"documents.processorerrors";

	public static final int MAX_FOLDER_NAME_LENGTH = 128;

	public static final int WIDTH_NAME = 229;
	public static final int WIDTH_MOD = 100;
	public static final int WIDTH_TYPE = 50;
	public static final int WIDTH_AUTHOR = 170;
	public static final int WIDTH_INFO = 25;
	public static final int WIDTH_MIN = 10;
	public static final int WIDTH_DATE = 80;

	public static final int REPO_WIDTH_NAME = 250;
	public static final int REPO_WIDTH_MOD = 150;
	public static final int REPO_WIDTH_SIZE = 100;
	public static final int REPO_WIDTH_HITS = 100;

	public static final String TYPE_PROJECT = "P";

	public static final String PARAM_PROJECTID = "proj";
	public static final String PARAM_TOPCATEGORY = "tc";
	public static final String PARAM_CURCATEGORY = "cc";
	public static final String PARAM_LINKID = "linkid";
	public static final String PARAM_UPDATECAT = "updatecatid";
	public static final String PARAM_TARGETCATEGORY = "movetocat";
	public static final String PARAM_DOCACTION = "docAction";
	public static final String PARAM_MEETINGID = "meetingid";
	public static final String PARAM_REPEATID = "repeatid";
	public static final String PARAM_TASKID = "taskid";
	public static final String PARAM_SELF = "self";
	public static final String PARAM_SET = "set";
	public static final String PARAM_SETMET = "setmet";
	public static final String PARAM_CURRDOCID = "currdocid";
	public static final String PARAM_HITREQ = "hitrequest";

	public static final String ACTION_ADD_DOC = "adddoc";
	public static final String ACTION_ADD_AIC_DOC = "addaicdoc";
	public static final String ACTION_ADD_MEETING_DOC = "addmeetingdoc";
	public static final String ACTION_ADD_ACTION_PLAN = "addactionplan";
	public static final String ACTION_ADD_PROJECT_PLAN = "addprojectplan";
	public static final String ACTION_ADD_TASK_DOC = "addtaskdoc";
	public static final String ACTION_UPDATE_DOC = "updatedoc";
	public static final String ACTION_UPDATE_DOC_PROPS = "updatedocprop";
	public static final String ACTION_SHOW_ALL_DOCS = "showalldocs";

	public static final String DOC_FORM = "documentForm";

	public static final char FALSE_FLAG = '0';
	public static final char TRUE_FLAG = '1';
	public static final char NOT_SET_FLAG = 'x';
	public static final String IND_YES = "Y";
	public static final String IND_NO = "N";
	public static final String DELETED_PROJECT_FLAG = "D";

	public static final int MAX_DOC_VERSIONS = 1000;
	public static final int STARTING_DOC_ID = 10000 * MAX_DOC_VERSIONS;
	public static final int MAXIMUM_DOC_ID = 99999 * MAX_DOC_VERSIONS;

	public static final String REQ_ATTR_USERROLE = "document.userRole";
	public static final String REQ_ATTR_EDGEACCESS = "document.edgeAccess";
	public static final String REQ_ATTR_PRIMARYCONTACT =
		"document.primaryContact";

	public static final String DECAFTYPE_INTERNAL = "I";

	public static final String FRM_CTX_UPDATE_CAT = "updateCategory";
	public static final String FRM_CTX_MOVE_CAT = "moveCategory";

	public static final String FRM_CTX_FROM_DETAILS = "Details";
	public static final String FRM_CTX_FROM_HISTORY = "History";

	// DOCUMENT PROPERTIES
	public static final int INITIAL_DOC_COUNT = 3;
	public static final int MAX_NAME_LENGTH = 128;
	public static final int MAX_DESCRIPTION_LENGTH = 2000;
	public static final int MAX_KEYWORD_LENGTH = 500;
	public static final long MAX_FILE_SIZE = 100000000;
	public static final int MAX_COMMENTS_LENGTH = 32768;

	public static final String SEL_OPT_NONE = "X";
	public static final String FRM_CTX_SUBMIT = "Submit";

	public static final String ETS_PUBLIC = "0";
	public static final String ETS_IBM_ONLY = "1";
	public static final String ETS_IBM_CONF = "2";

	public static final String ITAR_STATUS_PENDING = "P";
	public static final String ITAR_STATUS_COMPLETE = "C";

	public static final String MEETINGS_DOC_FOLDER = "Meeting Documents";
	public static final String PREV_MEETINGS_DOC_FOLDER = "Previous Meeting Docs";
	public static final int MEETINGS_DOC_TYPE = 10;

	public static final String PROJECT_TYPE_AIC = "AIC";
	
	public static final String MSG_DATA_SEP = ":";

	public static final String ISSUES_DOC_FOLDER = "Issue Documents";
	public static final String ISSUES_DOC_CREATOR = "SYSTEM";
	public static final String TOP_DOC_FOLDER = "Documents";
	
	public static final int GROUP_PREFIX_LENGTH = 4;
	public static final String GROUP_PREFIX = "grp-";
	
	public static final String ACTION_ADD_ATTACHMENT_CURRENT_VER    = "addattachment";
	public static final String ACTION_DELETE_ATTACHMENT_CURRENT_VER = "delattachment";
	public static final String ACTION_UPDATE_DOC_ATTACHMENT = "Update Document";
	
	public static final String STRING_COMMA = ",";
	public static final String DEFAULT_FOLDER_CREATION_DATE = "01/01/1970";

	public static final String REQ_ATTR_START_DATE = "ets.documents.startDate";
	public static final String REQ_ATTR_END_DATE = "ets.documents.endDate";
	
	public static final String DOC_UNRESTRICTED = "0";
	public static final String DOC_RESTRICTED = "1";
	
	public static final String REQ_ATTR_HAS_EXPIRED_DOCS = "_ETSHasExpiredDocs";
}