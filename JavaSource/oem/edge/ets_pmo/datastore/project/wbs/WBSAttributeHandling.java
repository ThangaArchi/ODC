package oem.edge.ets_pmo.datastore.project.wbs;

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
public class WBSAttributeHandling extends AttributeHandling{
private static String CLASS_VERSION = "4.5.1";	
public static String WBSWORK_PERCENT = "WORK_PERCENT";
public static boolean IsWBSWORK_PERCENT_RANK = false;

public static String PRIORITY = "PRIORITY";
public static boolean IsPRIORITY_RANK = false;

public static String ACTUAL_EFFORT = "ACTUAL_EFFORT";
public static boolean IsACTUAL_EFFORT_RANK = false;

public static String REMAINING_EFFORT = "REMAINING_EFFORT";
public static boolean IsREMAINING_EFFORT_RANK = false;

public static String CONSTRAINT_TYPE = "CONSTRAINT_TYPE";
public static boolean IsCONSTRAINT_TYPE_RANK = true;

public static String CONSTRAINT_DATE = "CONSTRAINT_DT";
public static boolean IsCONSTRAINT_DATE_RANK = false;




public static String REFERENCE_NUMBER = "REFERENCE_NUMBER";
public static boolean IsREFERENCE_NUMBER_RANK = false;

public static String PUBLISHED = "PUBLISHED";
public static boolean IsPUBLISHED_RANK = true;

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

public static String RTF1 = "SCOPE_RTF";
public static boolean IsRTF1_RANK = true;
public static int RTF1_RANKVALUE = 1;


public static String RTF2 = "OBJECTIVES_RTF";
public static boolean IsRTF2_RANK = true;
public static int RTF2_RANKVALUE = 2;

public static String RTF3 = "BACKGROUND_RTF";
public static boolean IsRTF3_RANK = true;
public static int RTF3_RANKVALUE = 3;

public static String RTF4 = "STATUS_RTF";
public static boolean IsRTF4_RANK = true;
public static int RTF4_RANKVALUE = 4;

public static String RTF5 = "COMPLETION_CRITERIA";
public static boolean IsRTF5_RANK = true;
public static int RTF5_RANKVALUE = 5;



public final static int ADD_REFERENCE_NUMBER= 2;

public final static int ADD_PUBLISHED = 5;

public final static int ADD_STAGE_ID = 8;
public final static int ADD_REVISION_HISTORY = 9;
public final static int ADD_START_DT = 10;
public final static int ADD_FINISH_DT = 11;
public final static int ADD_DURATION = 12;
public final static int ADD_WORK_PERCENT = 13;
public final static int ADD_ETC = 14;
public final static int ADD_PERCENT_COMPLETE = 15;
public final static int ADD_RTF1 = 16;
public final static int ADD_RTF1_RANK = 17;
public final static int ADD_RTF2 = 18;
public final static int ADD_RTF2_RANK = 19;
public final static int ADD_RTF3 = 20;
public final static int ADD_RTF3_RANK = 21;
public final static int ADD_RTF4 = 22;
public final static int ADD_RTF4_RANK = 23;
public final static int ADD_RTF5 = 24;
public final static int ADD_RTF5_RANK = 25;

public final static int ADD_WBS_WORK_PERCENT= 26;
public final static int ADD_PRIORITY = 27;
public final static int ADD_ACTUAL_EFFORT = 28;
public final static int ADD_REMAINING_EFFORT = 29;
public final static int ADD_CONSTRAINT_TYPE = 30;
public final static int ADD_CONSTRAINT_DATE = 31;
public final static int ADD_SD=32;
public final static int ADD_FD=33;
public final static int ADD_EST_START_DT=34;
public final static int ADD_EST_FINISH_DT=35;
public final static int ADD_PROPOSED_START_DT=36;
public final static int ADD_PROPOSED_FINISH_DT=37;
public final static int ADD_SCHEDULED_START_DT=38;
public final static int ADD_SCHEDULED_FINISH_DT=39;
public final static int ADD_FORECAST_START_DT=40;
public final static int ADD_FORECAST_FINISH_DT=41;
public final static int ADD_BASELINE1_FINISH_DT=42;
public final static int ADD_BASELINE2_FINISH_DT=43;
public final static int ADD_BASELINE3_FINISH_DT=44;
public final static int ADD_ACTUAL_FINISH_DT=45;

/**
 * @return
 */
public static String getCLASS_VERSION() {
	return CLASS_VERSION;
}

}
