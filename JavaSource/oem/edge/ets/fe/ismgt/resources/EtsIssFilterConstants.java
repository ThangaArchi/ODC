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
package oem.edge.ets.fe.ismgt.resources;


/**
 * @author v2phani
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface EtsIssFilterConstants {
	
	public static final String ETSDATASRC="etsds";//ets data source
	public static final String ETSLINKID="251000";//ets connect link id
	public static final String ETSLSTUSR="etsusr";//ets connect link id
	
	public static final String ETSAPPLNID="ETS";//ets id defined from CQ tables 
	public static final String ETSISSUESUBTYPE="Defect";//ets issues subtype defined from CQ tables 
	public static final String ETSCHANGESUBTYPE="Change";//ets changes subtype defined from CQ tables 
	
	public static final int ETSISSUEFILTER=1; //ISSUE FILTER
	public static final int ETSCHANGEFILTER=2; //ISSUE FILTER
	public static final int ETSFILTERNORECS=3; //ISSUE FILTER
	
	public static final int ETSISSUEERR=0;//ets issues error state
	public static final int ETSISSUESUCC=1;//ets issues success state
	
	public static final int ETSISSUEINITIAL=10;//ets issues initial state
	
	///////work all existing issues ////
	public static final int ETSISSRPTWALL=20;//ets issues filter working with all issues
	public static final int ETSISSRPTWALLFC=21;//ets issues filter working with all issues,filter conds
	public static final int ETSISSRPTWALLFCGO=22;//ets issues filter working with all issues,filter conds,go
	
	////issues i submitted //
	public static final int ETSISSRPTISUB=30;//ets issues filteR I HAVE submitted
	public static final int ETSISSRPTISUBFC=31;//ets issues filteR I HAVE submitted,filter conds
	public static final int ETSISSRPTISUBFCGO=32;//ets issues filteR I HAVE submitted,filter conds,go
	
	///issues assigned to me//
	public static final int ETSISSRPTASGND=40;//ets issues filter ASSIGNED to me
	public static final int ETSISSRPTASGNDFC=41;//ets issues filteR ASSIGNED to me conds
	public static final int ETSISSRPTASGNDFCGO=42;//ets issues filteR ASSIGNED to me,filter conds,go
	
	///////work all existing change requests ////
	public static final int ETSCHGRPTWALL=50;//working with all change requests
	public static final int ETSCHGRPTWALLFC=51;//working with all change requests,filter conds
	public static final int ETSCHGRPTWALLFCGO=52;//working with all change requests,filter conds,go
	
	////change requests i submitted //
	public static final int ETSCHGRPTISUB=60;//ets change requests filteR I HAVE submitted
	public static final int ETSCHGRPTISUBFC=61;//ets change requests filteR I HAVE submitted,filter conds
	public static final int ETSCHGRPTISUBFCGO=62;//ets change requests filteR I HAVE submitted,filter conds,go
	
	///change requests assigned to me//
	public static final int ETSCHGRPTASGND=70;//ets change requests filter ASSIGNED to me
	public static final int ETSCHGRPTASGNDFC=71;//ets change requests filteR ASSIGNED to me conds
	public static final int ETSCHGRPTASGNDFCGO=72;//ets change requests filteR ASSIGNED to me,filter conds,go
		
	
	public static final int ETSISSUESHOWLOG=110;//ets issues show logs state
	public static final int ETSSHOWUSERINFO=120;//ets issues show logs state
	
	
	public static final String ETSISSUECATHEADER="Issues/changes"; //ets Issues/changes header
	
	//for 4.5.1 sort functions///
	public static final int SORTISSUETITLE_A = 1100;
	public static final int SORTISSUETITLE_D = 1101;
	public static final int SORTISSUETYPE_A = 1102;
	public static final int SORTISSUETYPE_D = 1103;
	public static final int SORTSUBMITTER_A = 1104;
	public static final int SORTSUBMITTER_D = 1105;
	public static final int SORTOWNER_A = 1106;
	public static final int SORTOWNER_D = 1107;
	public static final int SORTSEVERITY_A = 1108;
	public static final int SORTSEVERITY_D = 1109;
	public static final int SORTSTATUS_A = 1110;
	public static final int SORTSTATUS_D = 1111;
	public static final int SORTTRKID_A = 1112;
	public static final int SORTTRKID_D = 1113;
	
	///sort constants for issue type info list
	public static final int SORTLISTISSTYPENAME_A = 1114;
	public static final int SORTLISTISSTYPENAME_D = 1115;
	public static final int SORTLISTISSTYPEACCESS_A = 1116;
	public static final int SORTLISTISSTYPEACCESS_D = 1117;
	public static final int SORTLISTISSTYPEOWNER_A = 1118;
	public static final int SORTLISTISSTYPEOWNER_D = 1119;
	public static final int SORTLISTISSTYPEOWNEREMAIL_A = 1120;
	public static final int SORTLISTISSTYPEOWNEREMAIL_D = 1121;
	// Added by Prasad for Sort by  Issue Type Backup Owner
	public static final int SORTLISTISSTYPEBACKUPOWNER_A = 1122;
	public static final int SORTLISTISSTYPEBACKUPOWNER_D = 1123;
	public static final int SORTLISTISSTYPEBACKUPOWNEREMAIL_A = 1124;
	public static final int SORTLISTISSTYPEBACKUPOWNEREMAIL_D = 1125;
	
	
	
	///user active flag
	public static final String ETSACTIVEUSERFLAG="A";
	
	////
	public static final String ETSPMOISSUESUBTYPE="ISSUE";//ETS PMO ISSUE TYPE 
	public static final String ETSPMOCHANGESUBTYPE="CHANGEREQUEST";//ETS PCR TYPE 
	
	//
	public static final int DOWNLOADTOCSV=2100;//ets issues show logs state
	public static final int ETSISSTYPESWELCOME=2200;//ets issue types welcome
	public static final int ETSCUSTOMFIELDSWELCOME=2400;//ets manage custom fields welcome
	public static final int ETSCUSTOMFIELDSADD=2410;//ets add custom fields welcome
	public static final int ETSCUSTOMFIELDSREMOVE=2450;//ets remove custom fields welcome
	public static final int ETSISSNUMSRCH=8000;//ets issue NUMBER SEARCH
	public static final int ETSLISTISSTYPESINFO=1500;//ETS ISS TYPES INFO LIST
	
	
	
	

}
