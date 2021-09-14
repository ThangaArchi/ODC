package oem.edge.ets_pmo.datastore.exception;
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
public class exceptionAttributeHandling {

private static String CLASS_VERSION = "4.5.1";
public static String ELEMENT_NAME = "ELEMENT_NAME";
public static String REFERENCE_NUMBER = "REFERENCE_NUMBER";
public static String PRIORITY = "PRIORITY";
public static String STAGE_ID = "STAGE_ID";
public static String PROPOSED_BY = "PROPOSED_BY";
public static String PROPOSED_DATETIME = "PROPOSED_DATETIME";

public static String DESCRIPTION_RTF = "DESCRIPTION_RTF";
public static boolean IsDESCRIPTION_RTF_RANK = true;
public static int DESCRIPTION_RTF_RANKVALUE = 1;

public static String ISSUE_COMMENTS_RTF = "ISSUE_COMMENTS_RTF";
public static boolean IsISSUE_COMMENTS_RTF_RANK = true;
public static int ISSUE_COMMENTS_RTF_RANKVALUE = 7;

public static String CR_COMMENTS_RTF = "CR_COMMENTS_RTF";
public static boolean IsCR_COMMENTS_RTF_RANK = true;
public static int CR_COMMENTS_RTF_RANKVALUE = 10;//got changed from 9 to 10


public static int ADD_ELEMENT_NAME = 40;
public static int ADD_REFERENCE_NUMBER = 41;
public static int ADD_PRIORITY = 42;
public static int ADD_STAGE_ID = 43;
public static int ADD_PROPOSED_BY = 44;
public static int ADD_PROPOSED_BY_DATETIME = 45;
public static int ADD_DESCRIPTION_RTF = 46;
public static int ADD_ISSUE_COMMENTS_RTF = 47;
public static int ADD_CR_COMMENTS_RTF = 48;
public static int ADD_PUBLISHED = 49;
public static int ADD_START_FINISH_DATE = 50;
public static int ADD_REVISION_HISTORY = 51;
public static int ADD_DURATION = 52;
public static int ADD_ETC = 53;
public static int ADD_PERCENT_COMPLETE = 54;
/**
 * @return
 */
public static String getCLASS_VERSION() {
	return CLASS_VERSION;
}

}

