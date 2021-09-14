package oem.edge.ets_pmo.datastore.project;

import oem.edge.ets_pmo.datastore.AttributeHandling;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004                                     */
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

/**
 * @author subbus
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ProjectAttributeHandling extends AttributeHandling{
	
private static String CLASS_VERSION = "4.5.1";

public static String REFERENCE_NUMBER = "REFERENCE_NUMBER";
public static boolean IsREFERENCE_NUMBER_RANK = false;

public static String CALENDAR_ID = "CALENDAR_ID";
public static boolean IsCALENDAR_ID_RANK = true;

public static String CURRENCY_ID = "CURRENCY_ID";
public static boolean IsCURRENCY_ID_RANK = true;

public static String PUBLISHED = "PUBLISHED";
public static boolean IsPUBLISHED_RANK = true;

public static String START_FINISH_DATE = "START_FINISH_DATE";
public static boolean IsSTART_FINISH_DATE_RANK = false;

public static String FINISH_START_DATE = "FINISH_START_DATE";
public static boolean IsFINISH_START_DATE_RANK = false;

public static String STAGE_ID = "STAGE_ID";
public static boolean IsSTAGE_ID_RANK  = true;

public static String REVISION_HISTORY = "REVISION_HISTORY";
public static boolean IsREVISION_HISTORY_RANK = false;

public static String START_DT = "START_DT";
public static boolean IsSTART_DT_RANK = false;

public static String FINISH_DT = "FINISH_DT";
public static boolean IsFINISH_DT_RANK = false;

public static String EST_START_DT = "PLANNED_START";
public static boolean IsEST_START_DT_RANK = false;

public static String EST_FINISH_DT = "PLANNED_FINISH";
public static boolean IsEST_FINISH_DT_RANK = false;

public static String PROPOSED_START_DT = "PROPOSED_START";
public static boolean IsPROPOSED_START_DT_RANK = false;

public static String PROPOSED_FINISH_DT = "PROPOSED_FINISH";
public static boolean IsPROPOSED_FINISH_DT_RANK = false;

public static String SCHEDULED_START_DT = "SCHED_START";
public static boolean IsSCHEDULED_START_DT_RANK = false;

public static String SCHEDULED_FINISH_DT = "SCHED_FINISH";
public static boolean IsSCHEDULED_FINISH_DT_RANK = false;

public static String FORECAST_START_DT = "FORECAST_START";
public static boolean IsFORECAST_START_DT_RANK = false;

public static String FORECAST_FINISH_DT = "FORECAST_FINISH";
public static boolean IsFORECAST_FINISH_DT_RANK = false;

public static String BASELINE1_FINISH = "BASELINE1_FINISH";
public static boolean IsBASELINE1_FINISH_RANK = false;

public static String BASELINE2_FINISH = "BASELINE2_FINISH";
public static boolean IsBASELINE2_FINISH_RANK = false;

public static String BASELINE3_FINISH = "BASELINE3_FINISH";
public static boolean IsBASELINE3_FINISH_RANK = false;

public static String ACTUAL_FINISH = "ACTUAL_FINISH";
public static boolean IsACTUAL_FINISH_RANK = false;


public static String DURATION = "DURATION";
public static boolean IsDURATION_RANK = false;

public static String WORK_PERCENT = "WORK_PERCENT";
public static boolean IsWORK_PERCENT_RANK = false;

public static String ETC = "ETC";
public static boolean IsETC_RANK = false;

public static String PERCENT_COMPLETE = "PERCENT_COMPLETE";
public static boolean IsPERCENT_COMPLETE_RANK = false;

public static String SD= "SD";
public static boolean IsSDRank = false;

public static String FD = "FD";
public static boolean IsFDRank = false;

public static String SCOPE_RTF = "SCOPE_RTF";
public static boolean IsSCOPE_RTF_RANK = true;
public static int SCOPE_RTF_RANKVALUE = 1;


public static String OBJECTIVES_RTF = "OBJECTIVES_RTF";
public static boolean IsOBJECTIVES_RTF_RANK = true;
public static int OBJECTIVES_RTF_RANKVALUE = 2;

public static String BACKGROUND_RTF = "BACKGROUND_RTF";
public static boolean IsBACKGROUND_RTF_RANK = true;
public static int BACKGROUND_RTF_RANKVALUE = 3;

public static String STATUS_RTF = "STATUS_RTF";
public static boolean IsSTATUS_RTF_RANK = true;
public static int STATUS_RTF_RANKVALUE = 4;

public static String TARGETSOLN_RTF = "TARGETSOLN_RTF";
public static boolean IsTARGETSOLN_RTF_RANK = true;
public static int TARGETSOLN_RTF_RANKVALUE = 5;



public final static int ADD_REFERENCE_NUMBER= 2;
public final static int ADD_CALENDAR_ID = 3;
public final static int ADD_CURRENCY_ID = 4;
public final static int ADD_PUBLISHED = 5;
public final static int ADD_START_FINISH_DATE = 6;
public final static int ADD_FINISH_START_DATE = 7;
public final static int ADD_STAGE_ID = 8;
public final static int ADD_REVISION_HISTORY = 9;
public final static int ADD_START_DT = 10;
public final static int ADD_FINISH_DT = 11;
public final static int ADD_DURATION = 12;
public final static int ADD_WORK_PERCENT = 13;
public final static int ADD_ETC = 14;
public final static int ADD_PERCENT_COMPLETE = 15;
public final static int ADD_SCOPE_RTF = 16;
public final static int ADD_SCOPE_RTF_RANK = 17;
public final static int ADD_OBJECTIVES_RTF = 18;
public final static int ADD_OBJECTIVES_RTF_RANK = 19;
public final static int ADD_BACKGROUND_RTF = 20;
public final static int ADD_BACKGROUND_RTF_RANK = 21;
public final static int ADD_STATUS_RTF = 22;
public final static int ADD_STATUS_RTF_RANK = 23;
public final static int ADD_TARGETSOLN_RTF = 24;
public final static int ADD_TARGETSOLN_RTF_RANK = 25;
public final static int ADD_SD=26;
public final static int ADD_FD=27;
public final static int ADD_EST_START_DT=28;
public final static int ADD_EST_FINISH_DT=29;
public final static int ADD_PROPOSED_START_DT=30;
public final static int ADD_PROPOSED_FINISH_DT=31;
public final static int ADD_SCHEDULED_START_DT=32;
public final static int ADD_SCHEDULED_FINISH_DT=33;
public final static int ADD_FORECAST_START_DT=34;
public final static int ADD_FORECAST_FINISH_DT=35;
public final static int ADD_BASELINE1_FINISH_DT=36;
public final static int ADD_BASELINE2_FINISH_DT=37;
public final static int ADD_BASELINE3_FINISH_DT=38;
public final static int ADD_ACTUAL_FINISH_DT=39;
/* When u change this file check if it doesnt clash with the 
 * resource ResourceAttributeHandling.java. We
 * need to change this clash problems in next release
 */




/**
 * @return
 */
public static String getCLASS_VERSION() {
	return CLASS_VERSION;
}

}
