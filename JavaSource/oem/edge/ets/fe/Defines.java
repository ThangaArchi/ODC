
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

//////////////////////////////////////////////////////////////////////    /
//     CHANGE ACTIVITY:
//       Flag PTR/DCR   Release   Date      Userid      Comments
    //
    //
//     END CHANGE ACTIVITY
//////////////////////////////////////////////////////////////////////    /

    package oem.edge.ets.fe;

    public class Defines {

    	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
    	private static final String CLASS_VERSION = "1.47";

        public static final String PARM_FORM_CHARSET = "form-charset";


        ////////////////////////////////////////////////////////////////////
        // File section

        // File form constants
        public static final String FILE_FORM_NAME_DOC_NAME ="docname";
        public static final String FILE_FORM_NAME_DOC_DESC ="docdesc";
        public static final String FILE_FORM_NAME_DOC_KEYWORDS ="keywords";
        public static final String FILE_FORM_NAME_NOTIFY_OPTION ="notify";
        public static final String FILE_FORM_NAME_CLIENT_FILE_NAME  = "docfile";


        public static final String FILE_PARM_PATH = "docfile"; //@B0A
        public static final String FILE_FORM_VALUE_ENCTYPE = "multipart/form-data";


        public static final String SLASH = "/";

        // SPN public static final int    FILE_READ_ONLY = 1;
        // SPN public static final int    FILE_READ_WRITE = 2;

        //@B3A - See RFC 1867 -- http://www.ietf.org/rfc/rfc1867.txt
        public static final String FILE_MULTIPART_CONTENT_DISPOSITION = "Content-Disposition";
        public static final String FILE_MULTIPART_DISPOSITION_FILENAME = "filename";
        public static final String FILE_MULTIPART_DISPOSITION_NAME = "name";
        public static final String FILE_MULTIPART_ENCODING_ISO = "ISO_8859-1";

        //public static final String FILE_MULTIPART_DISPOSITION_FILENAME = "docfile";
        public static final String FILE_MULTIPART_DISPOSITION_DOCNAME = "docname";
        public static final String FILE_MULTIPART_DISPOSITION_DOCDESC = "docdesc";
        public static final String FILE_MULTIPART_DISPOSITION_KEYWORDS = "keywords";




        ////////////////////////////////////////////////////////////////////
        // Header values
        // SPN public static final String HEADER_ACCEPT_CHARSET = "Accept-Charset";
        // SPN public static final String HEADER_ACCEPT_LANGUAGE = "Accept-Language";
        // SPN public static final String HEADER_ACCEPT_RANGES = "Accept-Ranges";
        // SPN public static final String HEADER_AUTHORIZATION = "Authorization";
        // SPN public static final String HEADER_CACHE_CONTROL = "Cache-Control";
        // SPN public static final String HEADER_CONTENT_DISPOSITION  = "content-disposition";
        public static final String HEADER_CONTENT_LENGTH  = "content-length";
        // SPN public static final String HEADER_HOST = "Host";
        // SPN public static final String HEADER_IF_MOD_SINCE  = "If-Modified-Since";
        // SPN public static final String HEADER_LAST_MOD  = "Last-Modified";
        public static final String HEADER_REFERER  = "Referer";

        // SPN public static final String ACCEPT_RANGES_NONE = "none";
        // SPN public static final String AUTHORIZATION_BASIC = "Basic";
        // SPN public static final String CACHE_CONTROL_NO_CACHE = "no-cache";
        // SPN public static final String CACHE_CONTROL_MAX_AGE_3600 = "max-age=3600";
        // SPN public static final String CACHE_CONTROL_PRIVATE = "private";  //@B2A
        // SPN public static final String CACHE_CONTROL_PROXY_REVALIDATE = "proxy-revalidate";  //@B2A



        //PRIV table integers
        public static final int ADMIN = 1;
        public static final int USER = 2;
        public static final int LOCK_UNLOCK = 3;
        public static final int UPDATE = 4;
        public static final int MANAGE_USERS = 5;
        public static final int DELETE = 6;
        public static final int IBM_ONLY = 7;
        public static final int OWNER = 8;
    	public static final int VISITOR = 9;
    	public static final int CLIENT = 10;

        //DOC_TYPES for doct_type in ets.ets_doc table
        public static final int DOC = 0;
        public static final int PROJECT_PLAN = 1;
        public static final int MEETING = 2;
        public static final int EVENT = 3;
        public static final int DROP_USERGUIDE = 4;
        public static final int WEB_USERGUIDE = 5;
    	public static final int TASK = 6;
    	public static final int SETMET_PLAN = 7;
    	public static final int ISSUES_DOC = 8;


        //VIEW_TYPES by top_cat_name
        public static final int MAIN_VT = 0;
        public static final int MEETINGS_VT = 1;
        public static final int DOCUMENTS_VT = 9;
        public static final int ISSUES_CHANGES_VT = 2;
        public static final int TEAM_VT = 3;
        // added for 4.4.1 release
    	public static final int CONTRACTS_VT = 4;
    	public static final int METRICS_VT = 5;
    	public static final int SETMET_VT = 6;
    	public static final int FEEDBACK_VT = 7;
    	public static final int PROJECTS_VT = 8;
    	public static final int ABOUT_SETMET_VT = 10;

    	// end of addition for 4.4.1

    	//	added for 5.1.1 release by sathish
    	public static final int SELF_ASSESSMENT_VT = 11;
    	// end of addition
    	public static final int ASIC_VT = 12;

    	public static final int SURVEY_VT = 13; 			// added by sathish for 5.4.1 release

        public static final int WorkFlow_Maintab = 15;       //added by Ryazuddin for Workflow main tab
        public static final int WorkFlow_Assessment = 16;    //added by Ryazuddin for Workflow Assessment tab
        //yes/no
        public static final char YES = 'Y';
        public static final char NO = 'N';


        // added by Sathish.
        public static final String SERVLET_PATH = "/technologyconnect/ets/";
        public static final String TOP_IMAGE_ROOT = "//www.ibm.com/i/";
        //modified by v2sagar for One X Complaince
        public static final String V11_IMAGE_ROOT = "//www.ibm.com/i/v14/";
        public static final String BUTTON_ROOT = "//www.ibm.com/i/v14/buttons/";
        public static final String ICON_ROOT = "//www.ibm.com/i/v14/icons/";
        //till here

        public static final String HELP_LINK = "";
        public static final int PROJECT_PLAN_CATEGORY = -1;
        public static final String LINKID = "251000";
        public static final String AIC_LINKID = "1k0000";

        //for ets_content_log
        public static final String ADD_DOC = "add new document";
        public static final String ADD_PROJ_PLAN_DOC = "add new project plan document (delete old one)";
        public static final String ADD_MEETING_DOC = "add new meeting document";
        public static final String ADD_EVENT_DOC = "add new event document";
        public static final String DEL_DOC = "delete document";
        public static final String DELALL_DOC = "delete document and all previous versions";
        public static final String UPDATE_DOC = "update document";
        public static final String UPDATE_DOC_PROP = "update document properties";
        public static final String ADD_CAT = "add new category";
        public static final String DEL_CAT = "delete category";
        public static final String UPDATE_CAT = "update category";
        public static final char NODE_CAT = 'C';
        public static final char NODE_DOC = 'D';

        // added by Sathish for access request (boarding process)
        public static final String ACCESS_PENDING = "PENDING";
        public static final String ACCESS_REJECTED = "REJECTED";
        public static final String ACCESS_APPROVED = "APPROVED";


        public static final String REQUEST_PROJECT = "E&TS Connect";
        public static final String ETS_ENTITLEMENT = "ETS_PROJECTS";

        public static final String POINT_OF_CONTACT_ENTTITLEMENT = "PointOfContact";

    	// added for release 4.4.1
    	public static final String GREEN = "1";
    	public static final String YELLOW = "2";
        public static final String RED = "3";

    	public static final char ETS_PUBLIC = '0';
     	public static final char ETS_IBM_ONLY = '1';
    	public static final char ETS_IBM_CONF = '2';


        public static final String ETS_ADMIN_ENTITLEMENT = "ETS_ADMIN";
    	public static final String ETS_EXECUTIVE_ENTITLEMENT = "ETS_EXECUTIVE";

    	public static final String PMO_IS_REPORTABLE = "Y";

    	public static final String PMO_PROJECT = "PROJECT";
    	public static final String PMO_PROPOSAL = "PROPOSAL";
    	public static final String PMO_CRIFOLDER = "CRIFOLDER";
    	public static final String PMO_ISSUE = "ISSUE";
    	public static final String PMO_CHANGEREQUEST = "CHANGEREQUEST";
    	public static final String PMO_ORGANIZATION = "ORGANIZATION";
    	public static final String PMO_TASK = "TASK";
    	public static final String PMO_SUMMARY_TASK = "SUMMARYTASK";
    	public static final String PMO_MILESTONE = "MILESTONE";
    	public static final String PMO_SCORECARD = "SCORECARD";
    	public static final String PMO_DELIVERABLE = "DELIVERABLE";
    	public static final String DOCUMENT_FOLDER = "DOCUMENTFOLDER";

    	public static final int PMO_MILESTONE_SCOPE_RTF_ID = 1;
    	public static final int PMO_MILESTONE_OBJECTIVES_RTF_ID = 2;
    	public static final int PMO_MILESTONE_BACKGROUND_RTF_ID = 3;
    	public static final int PMO_MILESTONE_STATUS_RTF_ID = 4;
    	public static final int PMO_MILESTONE_COMPLETION_CRITERIA_RTF_ID = 5;

    	public static final String WORKSPACE_OWNER = "WORKSPACE OWNER";
    	public static final String WORKSPACE_MANAGER = "WORKSPACE MANAGER";
    	public static final String WORKSPACE_CLIENT = "WORKSPACE CLIENT";
    	public static final String WORKSPACE_MEMBER = "WORKSPACE MEMBER";
    	public static final String WORKSPACE_VISITOR = "WORKSPACE VISITOR";
    	public static final String WORKFLOW_ADMIN	=	"AIC_Workflow_Admin";

    	public static final String ETS_ADMIN = "SUPER WORKSPACE ADMIN";
    	public static final String ETS_EXECUTIVE = "IBM EXECUTIVE";
    	public static final String INVALID_USER = "INVALID USER";


    	public static final String SETMET_CLIENT_INTERVIEW = "CLIENT_INTERVIEW";
    	public static final String SETMET_CLIENT_APPROVED = "CLIENT_APPROVED";
    	public static final String SETMET_PRINCIPAL_APPROVED = "PRINCIPAL_APPROVED";
    	public static final String SETMET_ACTION_PLAN = "ACTION_PLAN";
    	public static final String SETMET_ACTION_PLAN_APPROVED = "ACTION_PLAN_APPROVED";
    	public static final String SETMET_CLOSE = "SETMET_CLOSE";
    	public static final String SETMET_FINAL_RATING = "SETMET_FINAL_RATING";

    	public static final String SETMET_OPEN = "OPEN";
    	public static final String SETMET_CLOSED = "CLOSED";


    	public static final String SORT_BY_NAME_STR="1";
    	public static final String SORT_BY_DATE_STR="2";
    	public static final String SORT_BY_TYPE_STR="3";
    	public static final String SORT_BY_AUTH_STR="4";
    	public static final String SORT_BY_COMP_STR="5";
    	public static final String SORT_BY_ACCLEV_STR="6";
    	public static final String SORT_BY_USERNAME_STR="7";
    	public static final String SORT_BY_USERID_STR="8";
    	public static final String SORT_BY_PROJROLE_STR="9";
    	public static final String SORT_BY_CUSTOM_STR="10";
    	public static final String SORT_BY_UPDATE_DATE_STR="11";

    	public static final String SORT_ASC_STR="asc";
    	public static final String SORT_DES_STR="desc";


    	public static final String SORT_BY_DT_OWNER_STR="10";
        public static final String SORT_BY_DT_TASKID_STR="11";
    	public static final String SORT_BY_DT_TITLE_STR="12";
    	public static final String SORT_BY_DT_SECT_STR="13";
    	public static final String SORT_BY_DT_DATE_STR="14";
    	public static final String SORT_BY_DT_STATUS_STR="15";

        // end of addition for release 4.4.1

        // added for 4.5.1

        public static final String WORKSPACE_ARCHIVE = "A";
    	public static final String WORKSPACE_DELETE = "D";


    	public static final char DOC_DRAFT = 'D';
        public static final char DOC_APPROVED = 'A';
        public static final char DOC_REJECTED = 'R';
        public static final char DOC_SUB_APP = 'S';
        public static final char DOC_PUBLISH = 'P';

        public static final String[] months = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};

    	public static final String SORT_BY_SIZE_STR="16";
        public static final String SORT_BY_HITS_STR="17";

    	public static final String USER_ENTITLED ="A";
    	public static final String USER_PENDING ="P";
    	public static final String USER_REJECTED ="R";

    	public static final int ID_PROJECT_PLAN=100;
    	public static final int ID_ACTION_PLAN=200;
    	public static final String ADD_ACTION_PLAN = "add action plan";
    	public static final String DEL_PROJECT_PLAN = "delete project plan";
    	public static final String DEL_ACTION_PLAN = "delete action plan";
        // end of addition for 4.5.1

    	//5.1.1
    	public static final String METRICS_CLIENTCARE_STR = "Client care accounts (plus focus accounts)";
    	public static final String METRICS_FOCUSACCT_STR = "Focus accounts";
    	public static final String METRICS_CLIENTCARE = "Client care account";
    	public static final String METRICS_FOCUSACCT = "Focus account";


        public static final char FALSE_FLAG    = '0';
        public static final char TRUE_FLAG     = '1';
        public static final char NOT_SET_FLAG = 'x';


    	// added for 5.2.1 by sathish
    	public static final String GEOGRAPHY = "GEOGRAPHY";
    	public static final String DELIVERY_TEAM = "DELIVERY";
    	public static final String INDUSTRY = "INDUSTRY";

    	public static final String GEO_INFO_TYPE = "MASTER";


    	// added for 5.2.1 by sathish for sandra
    	public static final String METRICS_SA = "Self Assess";
    	public static final String METRICS_SM = "Set/Met";
    	public static final String METRICS_SU = "Survey";

    	// added in 5.2.1 by sathish
    	public static final String MULTIPOC = "MultiPOC";

    	// added in 5.4.1 by sathish
    	public static final String ITAR_PROJECT = "E&TS ITAR Certification";
    	public static final String ITAR_ENTITLEMENT = "ETS_ITAR";

    	public static final String ETS_WORKSPACE_TYPE = "ETS";

    	public static final int MEETINGS_PARENT_FOLDER_ID = -1;

    	// added for 5.4 by vishal
    	public static final String TECHNOLOGY = "TECHNOLOGY";
    	public static final String PROCESS = "PROCESS";
    	public static final String SECTOR = "SECTOR";
    	public static final String BRAND = "BRAND";

    	public static final String AIC_ADMIN_ENTITLEMENT = "SALES_ADMIN";
    	public static final String AIC_EXECUTIVE_ENTITLEMENT = "EXECUTIVE";
    	public static final String AIC_TEAM_ENTITLEMENT = "SALES_TEAM";
    	public static final String AIC_STAFF_ENTITLEMENT = "NONSALES_STAFF";
    	public static final String AIC_OPERATIVE_ENTITLEMENT = "NONSALES_OPERATIVE";
    	public static final String AIC_CLIENT_GROUP_ENTITLEMENT = "CLIENT";

    	// by deepak
    	public static final String ETS_PROJ_VAR = "func";
    	public static final String ETS_SURVEY_METRICS_FIELD="osat - ibm";

    	//by phani

    	public static final String AIC_WORKSPACE_TYPE="AIC";
    	public static final String AIC_ENTITLEMENT = "SALES_COLLAB";
    	public static final String AIC_PROJECT = "AIC PROJECT";

    	public static final String SORT_BY_INVITEID_STR="INVITEID";
    	public static final String SORT_BY_INV_PRIV_STR="ROLENAME";
    	public static final String SORT_BY_INV_REQST_STR="REQUESTORNAME";
    	public static final String SORT_BY_INV_COMP_STR="COMPANY";
    	public static final String SORT_BY_INV_COUNTRY_STR="COUNTRYNAME";

    	public static final String ETS_PROJ_DATATYPE="IBM";
        public static final String AIC_PROJ_DATATYPE="IBM";

    	public static final String AIC_IS_PRIVATE_RESTRICTED = "R";
    	public static final String AIC_IS_PRIVATE_PRIVATE = "P";
    	public static final String AIC_IS_PRIVATE_PUBLIC = "A";


    	public static final String AIC_SALES_DECAF_PROJECT = "OEM Team Project – Sales";
    	public static final String AIC_NON_SALES_DECAF_PROJECT = "OEM Team Project - Non Sales";
    	public static final String AIC_EXECUTIVE_DECAF_PROJECT = "OEM Team Project - Exec & Staff";
    	public static final String AIC_ADMIN_DECAF_PROJECT = "OEM Team Project - SalesAdmin";

    	public static final String COLLAB_CENTER_EXECUTIVE_ENTITLEMENT = "OEM_EXEC_STAFF_TEAM";
    	public static final String COLLAB_CENTER_NON_SALES_ENTITLEMENT = "OEM_NON_SALES_TEAM";
    	public static final String COLLAB_CENTER_SALES_ENTITLEMENT = "OEM_SALES_TEAM";
    	public static final String COLLAB_CENTER_ENTITLEMENT = "SALES_COLLAB";
    	public static final String COLLAB_CENTER_ADMIN_ENTITLEMENT = "SALES_COLLAB_ADMIN";

    	// Added for aic 5.4.1   --  vishal
    	public static final String SCE_SECTOR = "SCE_SECTOR";
    	public static final String SUB_SECTOR = "SUB_SECTOR";

    	// added for 6.3.1 -- srikanth
    	public static final String GREEN_STATUS = "Complete";
    	public static final String YELLOW_STATUS = "In Progress";
    	public static final String RED_STATUS = "Not Started";

    	// Added for 6.3.1 -- Thanga
    	public static final String DOC_READ_ACCESS = "R";
    	public static final String DOC_EDIT_ACCESS = "E";
    	public static final String DOC_EXPIRY_NOTIFICATION_DAYS = "1,3,7";
    	public static final int DOC_PAGINATE_RECORD_SIZE = 30;

    	// New types of AIC workspace - 6.3.1
    	public static final String AIC_IS_PRIVATE_TEAMROOM = "2";
    	public static final String AIC_IS_RESTRICTED_TEAMROOM = "1";

    	//Team room entitlements
    	public static final String BPSOWNER_ENT = "BPS_OWNER";
    	public static final String BPSREADER_ENT = "BPS_READER";
    	public static final String BPSAUTHOR_ENT = "BPS_AUTHOR";
    	public static final String BPSEDITOR_ENT = "BPS_EDITOR";

    	// Team room datatype

    	public static final String BPSTEAMS_DATATYPE = "BPSTeams";
    	public static final String TEAMROOM_ENT_APPR_LEVEL = "1";
    	public static final String TEAMROOM_ENTITLEMENT_APPROVER = "MAN1000";
    	public static final String TEAMROOM_END_DATE = "01-01-2020";
    	public static final String TEAMROOM_PROJECT_TYPE = "P";

	// All users group details
	public static final String GRP_ALL_USERS = "All Users";
	public static final String GRP_ALL_USERS_DESC = "All Users group";
	public static final String GRP_ALL_USERS_OWNER = "System";
	public static final String GRP_ALL_USERS_TYPE = "PUBLIC";
	public static final String GRP_ALL_USERS_SECURITY = "0";

		// for 7.1.1 aic workflow - thanga
		public static final String AIC_WORKFLOW_PROCESS = "Workflow" ;
    }