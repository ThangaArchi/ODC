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
 * To represent various states of issues/changes
 */
public interface EtsIssueConstants {

	public static final int SUBMITISSUE = 1;
	public static final int MODIFYISSUE = 2;
	public static final int RESOLVEISSUE = 3;
	public static final int VIEWISSUE = 4;
	public static final int REJECTISSUE = 5;
	public static final int CLOSEISSUE = 6;
	public static final int SUBMITCHANGE = 11;
	public static final int MODIFYCHANGE = 12;
	public static final int RESOLVECHANGE = 13;
	public static final int VIEWCHANGE = 14;
	public static final int REJECTCHANGE = 15;
	public static final int CLOSECHANGE = 16;
	public static final int ACCEPTCHANGE = 17;

	//sathish
	public static final int FEEDBACK = 18;
	
	//comments
	public static final int COMMENTISSUE=19;
	public static final int COMMENTCHANGE=20;
	public static final int REQNEWISSUETYPE=21;
	

	///////////////////////
	public static final String STDCQSOURCE = "CQROC"; //standard CQ source
	public static final String ETSDEFAULTCQ = "DEFAULTCQ";
	public static final String STDETSOLD = "ETSOLD";
	public static final String ETSNOVAL = "NOVAL";
	

	/////////////
	public static final String STDISSUETYPE="Issue type";	

	///////////////////STD SUBTYPES///
	public static final String STDSUBTYPE_A = "Subtype_A";
	public static final String STDSUBTYPE_B = "Subtype_B";
	public static final String STDSUBTYPE_C = "Subtype_C";
	public static final String STDSUBTYPE_D = "Subtype_D";

	///////////////////STD STATIC FIELD NAMES/////
	public static final String STDFIELDC1NAME = "Class";
	public static final String STDFIELDC2NAME = "Types";
	public static final String STDFIELDC3NAME = "Stages";
	public static final String STDFIELDC4NAME = "Chip_id";
	public static final String STDFIELDC5NAME = "Sim_complete";
	public static final String STDFIELDC6NAME = "ECO_required";
	public static final String STDFIELDC7NAME = "Verify_complete";
	
	/////STD NO VAL/////////////////
	public static final String STDCQRSNOVAL="-";//result set no value
	
	/////////////
	public static final String DEFUALTSTDISSUETYPE="Issue type";	
	
	public static final String DEFUALTSTDSUBTYPE_A = "Subtype A";
	public static final String DEFUALTSTDSUBTYPE_B = "Subtype B";
	public static final String DEFUALTSTDSUBTYPE_C = "Subtype C";
	public static final String DEFUALTSTDSUBTYPE_D = "Subtype D";

	///////////////////STD STATIC FIELD NAMES/////
	public static final String DEFUALTSTDFIELDC1NAME = "Class";
	public static final String DEFUALTSTDFIELDC2NAME = "Types";
	public static final String DEFUALTSTDFIELDC3NAME = "Stages";
	public static final String DEFUALTSTDFIELDC4NAME = "Chip id";
	public static final String DEFUALTSTDFIELDC5NAME = "Sim complete";
	public static final String DEFUALTSTDFIELDC6NAME = "ECO required";
	public static final String DEFUALTSTDFIELDC7NAME = "Verify complete";
	
	///////static states in dispatch actions
	public static final int ISSUEACTIONERR=0;
	public static final int ISSUESUBMITNEW=1;
	
	//CHNAGE issue owner
	public static final int CHGISSOWNER=22;
	
	//withdraw
	public static final int WITHDRAW=23;
	
	//
	public static final String ETSPMOSOURCE = "ETSPMO"; //ETS PMO CQ source
	
	//ISSUES SCHEMA make it configuarable
	public static final String ISMGTSCHEMA="ETS";  //ETS ISMGT DB2 SCHEMA
	
	public static final int SUBSCRIBEISSUE=28;	
	public static final int UNSUBSCRIBEISSUE=29;
	
	///////////file attahc params
	public static final int MAXFILE_SIZE_IN_BYTES=104857600;
	public static final int MAXFILE_SIZE_IN_MB=100;
	
	public static final int MAXCUSTFIELDS=8;
	

}
