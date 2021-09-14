package oem.edge.ets.fe.ismgt.helpers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.DatesArithmatic;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.ismgt.model.EtsDropDownDataBean;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;
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

/**
 * @author v2phani
 * This class contains basic utils for ets iss filter process
 *
 */
public class EtsIssFilterUtils implements EtsIssueConstants, EtsIssFilterConstants {

	////////////////version of the class////
	public static final String VERSION = "1.54";

	/**
	 * Constructor for EtsIssFilterUtils.
	 */
	public EtsIssFilterUtils() {
		super();
	}

	/**
	 * This method will return boolean, based on the ArrayList is null/size
	 */

//	public static boolean isArrayListDefnd(ArrayList arrList) {
//
//		int asize = 0;
//		boolean balist = false;
//		String chkBlnkVal = "";
//
//		if (arrList != null && !arrList.isEmpty()) {
//
//			asize = arrList.size();
//
//			//for 0
//			if (asize == 0) {
//
//				balist = false;
//			}
//
//			//check blank size vals
//
//			if (asize == 1) {
//
//				chkBlnkVal = AmtCommonUtils.getTrimStr((String) arrList.get(0));
//
//				if (!AmtCommonUtils.isResourceDefined(chkBlnkVal)) {
//
//					balist = false;
//
//				} else {
//
//					balist = true;
//				}
//
//			}
//
//			if (asize > 1) {
//
//				balist = true;
//
//			}
//
//		} //if arr list > 0
//
//		return balist;
//	}

	/**
	 * This method will return boolean, based on the HashMap is null/size
	 */

	public static boolean isHashMapDefnd(HashMap labelMap) {

		int asize = 0;
		boolean balist = false;

		if (labelMap != null && !labelMap.isEmpty()) {

			asize = labelMap.size();

			if (asize > 0) {

				balist = true;
			}

		}

		return balist;
	}
	/**
	 * This method will return boolean, based on the Object is null/size
	 */

	public static boolean isObjectDefnd(Object obj) {

		boolean bobj = false;

		if (obj != null) {

			bobj = true;

		}

		return bobj;
	}

	/**
	 * This method will return boolean, based on the String is null/lenght
	 */

	public static boolean isStringDefnd(String str) {

		boolean bstr = false;

		if (AmtCommonUtils.isResourceDefined(str)) {

			bstr = true;

		}

		return bstr;
	}

	/**
	 * This method will take mm/dd/yyy >> yyyy-mm-dd
	 */

	public static String getSqlDate(String showDate) {

		String strmm = showDate.substring(0, 2);

		String strdd = showDate.substring(3, 5);

		String stryy = showDate.substring(6);

		return stryy + ("-") + strmm + ("-") + strdd;

	}

	/**
	 * This method will return the current date in MM-DD-YYYY format
	 */

	public static String getCurDate() {

		return AmtCommonUtils.getDateString("MM-dd-yyyy");

	}

	/**
	 * This method will compare the dates in YYYY-MM-DD format
	 * comparing fromdate with todate
	 */

	public static boolean diffDates(String fromDate, String toDate, String dtFormat) {

		int df = DatesArithmatic.DateDiff(fromDate, toDate, dtFormat);

		if (df > 0)
			return true;

		if (df < 0)
			return false;

		return false;

	} //end of method

	/**
	 * This method will take the date as input and checks if the day of month is proper
	 * for the month
	 * format of the date is >> MM-DD-YYYY
	 */

	public static boolean checkDayOfMonth(String showDate) {

		ArrayList monEvenList = new ArrayList();
		monEvenList.add("04"); //april
		monEvenList.add("06"); //june
		monEvenList.add("09"); //sept
		monEvenList.add("11"); //nov

		String strmm = showDate.substring(0, 2);

		String strdd = showDate.substring(3, 5);

		String stryy = showDate.substring(6);

		int mm = Integer.parseInt(strmm);

		int dd = Integer.parseInt(strdd);

		int yy = Integer.parseInt(stryy);

		if (monEvenList.contains(strmm)) {

			if (dd > 30) {

				return false;
			} //if greater than 30 

		} //if the month is april/june/sept/nov

		if (strmm.equals("02")) { //for feb

			if ((yy % 4 == 0)) { //if leap year

				if (dd > 29) {

					return false;

				}
			} else {

				if (dd > 28) {
					return false;

				}

			}

		}

		return true;

	}

	/***
	 * tokneize the string into arraylist based on deleimter
	 * 
	 */

	public static ArrayList getArrayListFromStringTok(String actStr, String delimiter) {

		ArrayList ownerList = new ArrayList();

		StringTokenizer stok = new StringTokenizer(actStr, delimiter);

		String tokElem = "";

		if (stok != null) {

			while (stok.hasMoreTokens()) {

				tokElem = stok.nextToken();

				ownerList.add(tokElem);

			}

		}

		return ownerList;

	} //end of method

	/**
	 * get delimit str
	 */

	public static String getDelimitStr(String actStr, String delimit) {

		int iIndex = actStr.indexOf(delimit);
		String sToken = "";

		if (iIndex != -1) {

			sToken = actStr.substring(0, iIndex);

		}

		return sToken;

	}

	/***
	 * get trim str
	 * 
	 */

	public static String getTrimStr(String resStr) {

		String retStr = "";

		retStr = AmtCommonUtils.getTrimStr(resStr);

		if (retStr.equals(STDCQRSNOVAL)) {

			retStr = "";

		}

		return retStr;
	}

	/**
	 * To return java.sql.SQL Timestamp for a given MM/DD/YYYY
	 */

	public static Timestamp getSelDtSqlTimeStamp(String startYear, String startMonth, String startDate) {

		int stYear = 2004;
		int stMonth = 5;
		int stDate = 15;

		if (AmtCommonUtils.isResourceDefined(startYear)) {

			stYear = Integer.parseInt(startYear);
		}

		if (AmtCommonUtils.isResourceDefined(startMonth)) {

			stMonth = Integer.parseInt(startMonth);
		}

		if (AmtCommonUtils.isResourceDefined(startDate)) {

			stDate = Integer.parseInt(startDate);

		}

		java.util.Calendar cal = new java.util.GregorianCalendar(stYear, stMonth - 1, stDate);
		java.util.Date date = cal.getTime();

		return new Timestamp(date.getTime());

	}

	/**
			 * To return java.sql.SQL Timestamp for a given MM/DD/YYYY/HR/MN/SS
			 */

	public static Timestamp getSelDtSqlTimeStamp(String startYear, String startMonth, String startDate, String stHr, String stMn, String stSec) {

		int stYear = 2004;
		int stMonth = 5;
		int stDate = 15;

		int hr = 23;
		int mn = 59;
		int sec = 59;

		if (AmtCommonUtils.isResourceDefined(startYear)) {

			stYear = Integer.parseInt(startYear);
		}

		if (AmtCommonUtils.isResourceDefined(startMonth)) {

			stMonth = Integer.parseInt(startMonth);
		}

		if (AmtCommonUtils.isResourceDefined(startDate)) {

			stDate = Integer.parseInt(startDate);

		}

		if (AmtCommonUtils.isResourceDefined(stHr)) {

			hr = Integer.parseInt(stHr);

		}

		if (AmtCommonUtils.isResourceDefined(stMn)) {

			mn = Integer.parseInt(stMn);

		}

		if (AmtCommonUtils.isResourceDefined(stSec)) {

			sec = Integer.parseInt(stSec);

		}

		java.util.Calendar cal = new java.util.GregorianCalendar(stYear, stMonth - 1, stDate, hr, mn, sec);
		java.util.Date date = cal.getTime();

		return new Timestamp(date.getTime());

	}

	/**
	 * returns the Timestamp of cur date
	 * 
	 */

	public static Timestamp getCurDtSqlTimeStamp() {

		String curDate = getCurDate();

		String MM = "10";
		String DD = "23";
		String YYYY = "2004";

		if (AmtCommonUtils.isResourceDefined(curDate)) {

			MM = curDate.substring(0, 2);
			DD = curDate.substring(3, 5);
			YYYY = curDate.substring(6);

		}

		return getSelDtSqlTimeStamp(YYYY, MM + 1, DD); //since getCurDate returns month in correct format(ie., 0..11)

	}

	/**
	 * to get unique edge problem id
	 */

	public static synchronized String getUniqEdgeProblemId(String edgeUserId) {

		return (edgeUserId + "-" + String.valueOf(System.currentTimeMillis()));

	}

	/**
		 * to get unique edge problem id
		 */

	public static synchronized long getUniqRefNo() {

		return System.currentTimeMillis();

	}

	/**
			 * to get unique edge problem id
			 */

	public static synchronized String getUniqRefNoStr() {

		String refStr = "U" + getUniqRefNo();

		return refStr.substring(1);

	}

	/**
				 * to get unique edge problem id
				 */

	public static synchronized String getUniqRefNoStr(String dataId) {

		String refStr = dataId + getUniqRefNo();

		return refStr;

	}

	/**
				 * to get unique edge problem id
				 */

	public static synchronized int getUniqRefNoInt() {

		return Long.valueOf(getUniqRefNoStr()).intValue();

	}

	/**
		 * 
		 * To parse the problem type and returns the issue type
		 * @param prob_type
		 * @return
		 * @throws Exception
		 */

	public static EtsDropDownDataBean getIssueTypeDropDownAttrib(String prob_type) throws Exception {

		Global.println("prob type for $ tok ===" + prob_type);

		ArrayList tokList = new ArrayList();

		if (AmtCommonUtils.isResourceDefined(prob_type)) {

			tokList = EtsIssFilterUtils.getArrayListFromStringTok(prob_type, "$");
		}

		EtsDropDownDataBean dropBean = new EtsDropDownDataBean();

		if (EtsIssFilterUtils.isArrayListDefnd(tokList)) {

			//set issue val//
			dropBean.setIssueType((String) tokList.get(0));

			//set issue source
			dropBean.setIssueSource((String) tokList.get(1));

			//set issue access
			dropBean.setIssueAccess((String) tokList.get(2));

		}

		return dropBean;

	}

	/**
		 * to get issue val for a given pattern
		 * token format >> 	DATA_ID $ ISSUE_TYPE $ ISSUE_SOURCE $ ISSUE_ACCESS $ SUBTYPE_A
		 */

	public static String getDelimitIssueVal(String prob_type) throws Exception {

		ArrayList tokList = new ArrayList();

		if (AmtCommonUtils.isResourceDefined(prob_type)) {

			tokList = EtsIssFilterUtils.getArrayListFromStringTok(prob_type, "$");
		}

		if (EtsIssFilterUtils.isArrayListDefnd(tokList)) {

			if (tokList.size() > 2) {

				EtsDropDownDataBean dropBean = getIssueTypeDropDownAttrib(prob_type);

				return dropBean.getIssueType();

			}

		} else {
			return prob_type;

		}

		return prob_type;

	}

	/**
	 * 
	 * 
	 * @param dateString
	 * @param currFormat
	 * @param newFormat
	 * @return
	 * @throws Exception
	 */

	public static String formatDate(String dateString, String currFormat, String newFormat) throws Exception {

		if (dateString != null)
			dateString = dateString.trim();

		if (currFormat != null)
			currFormat = currFormat.trim();
		else
			currFormat = "yyyy-MM-dd hh:mm:ss.000000";

		if (newFormat != null)
			newFormat = newFormat.trim();
		else
			newFormat = "MMM d, yyyy";

		String dateStringFinal = "";

		java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat(newFormat);
		java.text.SimpleDateFormat dateFormatOld = new java.text.SimpleDateFormat(currFormat);
		dateFormatOld.setLenient(true);
		dateFormat.setLenient(true);

		// Display date in proper date format.
		try {
			java.util.Date myDate = (java.util.Date) dateFormatOld.parse(dateString);
			dateStringFinal = dateFormat.format(myDate);

		} catch (Exception ex) {
			dateStringFinal = dateString;
		}

		return dateStringFinal;
	}

	/**
	 * To get user role
	 * @param es
	 * @param projectId
	 * @return
	 */

	public static String checkUserRole(EdgeAccessCntrl es, String projectId) {

		String checkUserRole = "Defines.INVALID_USER";

		try {

			checkUserRole = ETSUtils.checkUserRole(es, projectId);

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in printWelcomeIssues", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, "General Exception in printWelcomeIssues", ex);
				ex.printStackTrace();

			}
		}

		return checkUserRole;

	}

	/**
	 * 
	 * @param checkUserRole
	 * @return
	 */

	public static boolean isUserIssViewOnly(String checkUserRole) {
			

		if (checkUserRole.equals(Defines.WORKSPACE_VISITOR) || checkUserRole.equals(Defines.ETS_EXECUTIVE)) {
						
			return true;

		} else {
						
			return false;
		}
	}

	/**
		 * 
		 * @param checkUserRole
		 * @return
		 */

	public static boolean isUserIssViewOnly(EdgeAccessCntrl es, String projectId) {

		return isUserIssViewOnly(checkUserRole(es, projectId));
	}

	/**
		 * This method will return boolean, based on the ArrayList is null/size
		 */

	public static boolean isArrayListDefndWithObj(ArrayList arrList) {

		int asize = 0;
		boolean balist = false;
		String chkBlnkVal = "";

		if (arrList != null && !arrList.isEmpty()) {

			asize = arrList.size();

			//for 0
			if (asize == 0) {

				balist = false;
			}

			//check blank size vals

			if (asize > 0) {

				balist = true;

			}

		} //if arr list > 0

		return balist;
	}

	/**
	 * To show issue owner
	 * @param proj
	 * @return
	 */

	public static boolean isShowIssueOwner(ETSProj proj) {

		return isShowIssueOwner(proj.getShow_issue_owner());

	}

	/**
		 * To show issue owner
		 * @param proj
		 * @return
		 */

	public static boolean isShowIssueOwner(String projIssueOwner) {

		//	set issue owner
		boolean showOwner = false;

		String showIssueOwner = AmtCommonUtils.getTrimStr(projIssueOwner);

		if (showIssueOwner.equals("Y")) {

			showOwner = true;

		} else {

			showOwner = false;

		}

		return showOwner;

	}

	/**
	 * To get comma sep string from list
	 * @param arrList
	 * @return
	 */

//	public static String getCommSepStrFromStrList(ArrayList arrList) {
//
//		StringBuffer sb = new StringBuffer();
//		int asize = 0;
//
//		if (EtsIssFilterUtils.isArrayListDefnd(arrList)) {
//
//			asize = arrList.size();
//
//			for (int i = 0; i < asize; i++) {
//
//				if (i == 0) {
//
//					sb.append((String) arrList.get(i));
//
//				} else {
//
//					sb.append(",");
//					sb.append((String) arrList.get(i));
//
//				}
//			}
//		}
//
//		return sb.toString();
//
//	} //end of method
	
	/**
	 * 
	 * @param oldStr
	 * @param formatStr
	 * @return
	 */
	
	public static List formatMsgStringIntoList(String oldStr,String formatStr) {

			StringBuffer sb = new StringBuffer();

			String sToken = null;
			int iStartIndex = 0;
			List aList = new ArrayList();
			
			int formatlen=0;

			//Global.println("OLD STR======" + oldStr);
			
			if(AmtCommonUtils.isResourceDefined(formatStr)) {
				
				formatlen=formatStr.length();
			}

			int iCount = 0;
			int iIndex = oldStr.indexOf(formatStr);

			while (iIndex != -1) {

				sToken = oldStr.substring(0, iIndex);
				//Global.println(sToken);
				aList.add(sToken);

				oldStr = oldStr.substring(iIndex + formatlen);
				//Global.println("Now message: " + msg); 
				iIndex = oldStr.indexOf(formatStr);

			}

			aList.add(oldStr);

			
			return aList;
		}
		
	/**
		 * To get comma sep string from list
		 * @param arrList
		 * @return
		 */

		public static String getCommSepStrFromStrList(List arrList) {

			StringBuffer sb = new StringBuffer();
			int asize = 0;

			if (EtsIssFilterUtils.isArrayListDefnd(arrList)) {

				asize = arrList.size();

				for (int i = 0; i < asize; i++) {

					if (i == 0) {

						sb.append((String) arrList.get(i));

					} else {

						sb.append(",");
						sb.append((String) arrList.get(i));

					}
				}
			}

			return sb.toString();

		} //end of method
		
	/**
		 * This method will return boolean, based on the ArrayList is null/size
		 */

		public static boolean isArrayListDefnd(List arrList) {

			int asize = 0;
			boolean balist = false;
			String chkBlnkVal = "";

			if (arrList != null && !arrList.isEmpty()) {

				asize = arrList.size();

				//for 0
				if (asize == 0) {

					balist = false;
				}

				//check blank size vals

				if (asize == 1) {

					chkBlnkVal = AmtCommonUtils.getTrimStr((String) arrList.get(0));

					if (!AmtCommonUtils.isResourceDefined(chkBlnkVal)) {

						balist = false;

					} else {

						balist = true;
					}

				}

				if (asize > 1) {

					balist = true;

				}

			} //if arr list > 0

			return balist;
		}


		
	

} //end of class
