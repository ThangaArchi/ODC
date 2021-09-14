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
package oem.edge.ets.fe.ismgt.helpers;

import java.util.Calendar;

/*
 * Created on Jul 12, 2006
 */

/**
 * @author v2sagar
 */
public class UserProfileTimeZone {
	
	 private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss.S a";
	 private static final String HEADER_DATE_FORMAT = "EEE, MMM dd, yyyy";

	public static void main(String[] args) {	
		getUTCDateTime();		
	   }
//This method returns GMT Value
	public static String getUTCDateTime() {		
		java.util.Calendar cal = java.util.Calendar.getInstance();
		int utcoffset = cal.get(Calendar.DST_OFFSET) + cal.get(Calendar.ZONE_OFFSET);
		java.util.Date GMTDate = new java.util.Date(System.currentTimeMillis() - utcoffset);
		cal.setTime(GMTDate);
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
		String datetime = sdf.format(cal.getTime()).toString();
		
		return datetime;
	} 
	
	public static String getUTCHeaderDate() {		
		java.util.Calendar cal = java.util.Calendar.getInstance();
		int utcoffset = cal.get(Calendar.DST_OFFSET) + cal.get(Calendar.ZONE_OFFSET);
		java.util.Date GMTDate = new java.util.Date(System.currentTimeMillis() - utcoffset);
		cal.setTime(GMTDate);
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(HEADER_DATE_FORMAT);
		String datetime = sdf.format(cal.getTime()).toString();
		
		return datetime;
	} 
//Generic to Date Format
//Note: You can get month..year..hours..etc by passing specific value in the DATE_FORMAT 
	public static String getUTCHeaderDate(String DATE_FORMAT) {		
		java.util.Calendar cal = java.util.Calendar.getInstance();
		int utcoffset = cal.get(Calendar.DST_OFFSET) + cal.get(Calendar.ZONE_OFFSET);
		java.util.Date GMTDate = new java.util.Date(System.currentTimeMillis() - utcoffset);
		cal.setTime(GMTDate);
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
		String datetime = sdf.format(cal.getTime()).toString();
		
		return datetime;
	}
	

}
