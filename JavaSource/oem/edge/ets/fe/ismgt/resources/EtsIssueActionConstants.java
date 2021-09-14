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

/*
 * Created on May 26, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ets.fe.ismgt.resources;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface EtsIssueActionConstants {

	public static final int FATALERROR = -1;

	///MAINPAGE
	//public static final int MAINPAGE=0;
	public static final int MAINPAGE = 50;

	//DEFAULT 1 PAGE
	public static final int NEWINITIAL = 1;

	//CONTINUE AND CANCEL 1ST PAGE
	public static final int CONTDESCR = 11; //CONT DESCR

	public static final int CANCDESCR = 12; //CANC DESCR
	public static final int CANCSUBTYPE = 121; //CANC DESCR
	public static final int CANCISSUEIDENTF = 122; //CANC DESCR

	public static final int VALIDERRCONTDESCR = 111; // VALIDATION ERROR CONT DESCR

	//EDIT,CONTINUE AND CANCEL 1ST PAGE
	public static final int EDITDESCR = 13; //EDIT ISSUE DESCRIPTION

	//EDIT ISSUE TYPE
	public static final int EDITISSUETYPE = 14; //EDIT ISSUE TYPE

	//GO SUB TYPES 2 PAGE
	public static final int GOSUBTYPEA = 141;
	public static final int GOSUBTYPEB = 142;
	public static final int GOSUBTYPEC = 143;
	public static final int GOSUBTYPED = 144;

	//EDIT SUBTYPES 
	public static final int EDITSUBTYPEA = 151;
	public static final int EDITSUBTYPEB = 152;
	public static final int EDITSUBTYPEC = 153;
	public static final int EDITSUBTYPED = 154;

	//CONT IDENTIFICATION
	public static final int CONTIDENTF = 16; //CONT DESCR
	public static final int CANCIDENTF = 122; //CANC DESCR

	//EDIT  ISSUE IDENTIFICATION
	public static final int EDITISSUEIDENTF = 17; //Edit issue identification

	public static final int VALIDERRCONTIDENTF = 171; // VALIDATION ERROR CONT IDENTF

	//DEFAULT FILE ATTACH
	public static final int FILEATTACH = 18;
	public static final int DELETEFILE = 19;

	//ADD FILE ATTACH
	public static final int ADDFILEATTACH = 20;
	public static final int CANCFILEATTACH = 123;
	public static final int EDITFILEATTACH = 21;

	///add access cntrl
	public static final int ADDACCESSCNTRL = 22;
	public static final int EDITACCESSCNTRL = 23;
	public static final int CANCACCESSCNTRL = 124;

	//add notify list
	public static final int ADDNOTIFYLIST = 24;
	public static final int ADDNOTIFYLISTFOREXT = 241;
	public static final int EDITNOTIFYLIST = 25;
	public static final int CANCNOTIFYLIST = 125;

	//final submit
	public static final int ISSUEFINALSUBMIT = 26;
	public static final int ISSUEFINALCANCEL = 126;

	//submit to db
	public static final int SUBMITTODB = 27;

	public static final int ADDISSUETYPE = 28; //CONT DESCR
	public static final int CANCADDISSUETYPE = 128; //CONT DESCR
	public static final int VALIDERRADDISSTYPE = 129; //CONT DESCR

	//
	public static final int ERRINACTION = 40;

	////view issues constants
	public static final int VIEWISSUEDETS = 60;
	public static final int VIEWISSUEDETSREFRESHFILES = 61;
	public static final int SORTHISTDATETIME_A = 620;
	public static final int SORTHISTDATETIME_D = 621;
	public static final int SORTHISTACTIONBY_A =622;
	public static final int SORTHISTACTIONBY_D =623;
	public static final int SORTHISTACTIONNAME_A =624;
	public static final int SORTHISTACTIONNAME_D =625;
	public static final int SORTHISTISSUESTATE_A =626;
	public static final int SORTHISTISSUESTATE_D =627;
	
	///

	//modify issues constansts
	public static final int MODIFYISSUEFIRSTPAGE = 300;
	public static final int MODIFYEDITDESCR = 313;

	public static final int MODIFYCONTDESCR = 311; // VALIDATION ERROR CONT DESCR IN MODIFY
	public static final int MODIFYCANCDESCR = 312;
	public static final int MODIFYVALIDERRCONTDESCR = 3111; // VALIDATION ERROR CONT DESCR IN MODIFY
	public static final int MODIFYEDITFILEATTACH = 321; //file attach from modify
	//	DEFAULT FILE ATTACH
	public static final int MODIFYFILEATTACH = 318;
	public static final int MODIFYDELETEFILE = 319;
	public static final int MODIFYCONTFILEATTACH = 320;
	public static final int MODIFYCANCFILEATTACH = 3123;

	//NOTIFY LIST
	public static final int MODIFYEDITNOTIFYLIST = 325;
	public static final int MODIFYCONTNOTIFYLIST = 324;
	public static final int MODIFYCANCNOTIFYLIST = 3125;

	//ISSUE IDENTF
	public static final int MODIFYEDITISSUEIDENTF = 317;
	
	// EDIT CUSTOM FIELDS
	public static final int MODIFYEDITCUSTOMFIELDS = 322;
	public static final int MODIFYEDITCUSTOMFIELDSCONT = 3221;
	public static final int MODIFYVALIDERRCONTCUSTFIELD = 3223;
	
	
	//
	public static final int MODIFYCANCADDISSUETYPE = 3128;

	public static final int MODIFYISSUEIDENTFDEFAULT = 329;

	//	EDIT ISSUE TYPE
	public static final int MODIFYEDITISSUETYPE = 314; //EDIT ISSUE TYPE

	//GO SUB TYPES 2 PAGE
	public static final int MODIFYGOSUBTYPEA = 3141;
	public static final int MODIFYGOSUBTYPEB = 3142;
	public static final int MODIFYGOSUBTYPEC = 3143;
	public static final int MODIFYGOSUBTYPED = 3144;

	//EDIT SUBTYPES 
	public static final int MODIFYEDITSUBTYPEA = 3151;
	public static final int MODIFYEDITSUBTYPEB = 3152;
	public static final int MODIFYEDITSUBTYPEC = 3153;
	public static final int MODIFYEDITSUBTYPED = 3154;

	//CONT IDENTIFICATION
	public static final int MODIFYCONTIDENTF = 316; //CONT DESCR
	public static final int MODIFYCANCIDENTF = 3122; //CANC DESCR

	public static final int MODIFYADDISSUETYPE = 328; //CONT DESCR

	public static final int MODIFYVALIDERRCONTIDENTF = 3171; //validation error 
	public static final int MODIFYVALIDERRADDISSTYPE = 3129; //validation error  on issue indet final page

	public static final int MODIFYCANCSUBTYPE = 3121;

	public static final int MODIFYUNIFIEDCANCEL = 3200;
	public static final int MODIFYSUBMITTODB = 326;

	////////////////RESOLVE STATES
	public static final int RESOLVEISSUEFIRSTPAGE = 500;
	public static final int RESOLVESUBMITTODB = 526;
	//	DEFAULT FILE ATTACH
	public static final int RESOLVEEDITFILEATTACH = 521; //file attach from modify
	public static final int RESOLVEFILEATTACH = 518;
	public static final int RESOLVEDELETEFILE = 519;
	public static final int RESOLVECONTFILEATTACH = 520;
	public static final int RESOLVECANCFILEATTACH = 5123;
	
	//create new issue types
	public static final int CREATEISSUETYPE1STPAGE = 700;
	public static final int CREATEISSUETYPECONTINUE = 701;
	public static final int CREATEISSUETYPECANCEL = 702;
	public static final int EDITCREATEISSUETYPE = 703;
	public static final int CREATEISSUETYPESUBMIT = 704;
	public static final int CREATEISSUETYPEMAILERR = 705;
	
	//change owner of an issue type
	public static final int CHGOWNER1STPAGE = 800;
	public static final int CHGOWNERSUBMIT = 801;
	
	
	
	public static final String REQCREATEISSTYPEUNIQID="REQCRTISTYPE";
	
	//ACTION ERROR PAGE
	public static final int ACTION_NOTAUTHORIZED =6000;
	public static final int ACTION_INPROCESS =6001;
	
	//
	public static final int SUBMIT_ONBEHALF_EXT=3;
	public static final int CONT_SUBMIT_ONBEHALF_EXT=33;
	
	/////////////521///
	public static final String ADDISSUETYPEUNIQID = "ADDISSTYPE";
	public static final String DELISSUETYPEUNIQID = "DELISSTYPE";
	public static final String UPDISSUETYPEUNIQID = "UPDISSTYPE";

	// add custom fields
	public static final String ADDCUSTFIELDUNIQID = "ADDCUSTFIELD";
	public static final int ADDCUSTFIELD1STPAGE = 2410;
	public static final int ADDCUSTFIELDCONTINUE = 2411;	
	public static final int ADDCUSTFIELDCANCEL = 2412;	
	public static final int ADDCUSTFIELDEXTCONTINUE = 2413;
	public static final int ADDCUSTFIELDSUBMIT = 2414;
	public static final int ADDCUSTFIELDCONFIRM = 2415;

	// update custom fields
	public static final String UPDATECUSTFIELDUNIQID = "UPDATECUSTFIELD";
	public static final int UPDATECUSTFIELD1STPAGE = 2430;
	public static final int UPDATECUSTFIELDCONTINUE = 2431;	
	public static final int UPDATECUSTFIELDCANCEL = 2432;	
	public static final int UPDATECUSTFIELDEXTCONTINUE = 2433;
	public static final int UPDATECUSTFIELDSUBMIT = 2434;
	public static final int UPDATECUSTFIELDBACK = 2435;	
	
	// remove custom fields
	public static final String REMOVECUSTFIELDUNIQID = "REMOVECUSTFIELD";
	public static final int REMOVECUSTFIELD1STPAGE = 2450;
	public static final int REMOVECUSTFIELDCONTINUE = 2451;	
	public static final int REMOVECUSTFIELDCANCEL = 2452;	
	public static final int REMOVECUSTFIELDEXTCONTINUE = 2453;
	public static final int REMOVECUSTFIELDSUBMIT = 2454;
	public static final int REMOVECUSTFIELDBACK = 2455;
	
		
	//	add  issue types		
	public static final int ADDISSUETYPE1STPAGE = 700;
	public static final int ADDISSUETYPECONTINUE = 701;
	public static final int ADDISSUETYPEEXTCONTINUE = 7011;
	public static final int ADDISSUETYPECANCEL = 702;
	public static final int EDITADDISSUETYPE = 703;
	public static final int ADDISSUETYPEOWNCONT = 704;
	public static final int EDITISSUETYPEOWNCONT = 7041;
	public static final int EDITISSUETYPEBACKUPOWNCONT = 7042;
	public static final int ADDISSUETYPESUBMIT = 705;
	public static final int ADDISSTYPEOWNSHIPCONT = 7022;
	public static final int EDITADDISSUEOWNERSHIP=7031;
	
	//delete issue types
	
	public static final int DELISSUETYPE1STPAGE = 900;
	public static final int DELISSUETYPECONTINUE = 901;
	public static final int DELISSUETYPECANCEL = 902;
	public static final int EDITDELISSUETYPE = 903;
	public static final int DELISSUETYPESUBMIT = 904;
	
	
	//update issue types
	public static final int UPDISSUETYPE1STPAGE = 1100;
	public static final int UPDISSUETYPEMAINPAGE = 1101;
	public static final int UPDEDITISSTYPNAME = 1102;
	public static final int UPDEDITISSTYPNAMECONT = 11021;
	public static final int UPDEDITISSTYPNAMECANC = 11022;
	public static final int UPDEDITISSTYPACCESS = 1103;
	public static final int UPDEDITISSTYPACCESSCONT = 11031;
	public static final int UPDEDITISSTYPACCESSCONTEXT = 110311;
	public static final int UPDEDITISSTYPACCESSCONTEXTCONT = 1103111;
	public static final int UPDEDITISSTYPACCESSCONTEXTCANC = 1103112;
	public static final int UPDEDITISSTYPACCESSCANC = 11032;
	public static final int UPDEDITISSTYPOWNER = 1104;
	public static final int UPDEDITISSTYPOWNERCONT = 11041;
	public static final int UPDEDITISSTYPOWNERCANC = 11042;
	
	public static final int UPDEDITISSTYPBKOWNER = 1106;
	public static final int UPDEDITISSTYPBKOWNERCONT = 11061;
	public static final int UPDEDITISSTYPBKOWNERCANC = 11062;	
	
	public static final int UPDEDITISSTYPSUBMIT = 1105;
	
	//subscribe issue types
	public static final int SUBSISSTYP1STPAGE=1200;
	public static final int SUBSISSTYPE=1201;
	public static final int UNSUBSISSTYPE=1202;
	
	//subscribe to issues
	public static final int SUBSCRISSUE=1300;
	public static final int UNSUBSCRISSUE=1301;
	
	//comment box attributes
	public static final int COMMENT_ROWS=20;
	public static final int COMMENT_COLS=96;
		

}
