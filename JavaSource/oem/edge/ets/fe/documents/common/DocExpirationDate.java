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

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

/**
 * This class acts as a value object to store dd, mm and yy values
 * @author v2srikau
 */
public class DocExpirationDate {

	public static final String[] months =
		new String[] {
			"January",
			"February",
			"March",
			"April",
			"May",
			"June",
			"July",
			"August",
			"September",
			"October",
			"November",
			"December" };

	private String m_strDay = StringUtil.EMPTY_STRING;
	private String m_strMonth = StringUtil.EMPTY_STRING;
	private String m_strYear = StringUtil.EMPTY_STRING;
	
	private String m_strExpires = StringUtil.EMPTY_STRING;
	
	/**
	 * Default Contructor 
	 */
	public DocExpirationDate() {
		// DO NOTHING
	}
	
	/**
	 * @param lTimeStamp
	 */
	public DocExpirationDate(long lTimeStamp) {
		Date dtExpiration = new Date(lTimeStamp);	
		Calendar pdCalendar = Calendar.getInstance();
		pdCalendar.setTime(dtExpiration);
		setDay(String.valueOf(pdCalendar.get(Calendar.DAY_OF_MONTH)));
		setMonth(String.valueOf(pdCalendar.get(Calendar.MONTH)));
		setYear(String.valueOf(pdCalendar.get(Calendar.YEAR)));
		setExpires("yes");
	}
	
	public Vector getMonths() {
		Vector vtMonths = new Vector();
		for (int iCounter = 0; iCounter < months.length; iCounter++) {
			vtMonths.add(months[iCounter]);
		}
		return vtMonths;
		
	}

	/**
	 * @return
	 */
	public String getDay() {
		return m_strDay;
	}

	/**
	 * @return
	 */
	public String getMonth() {
		return m_strMonth;
	}

	/**
	 * @return
	 */
	public String getYear() {
		return m_strYear;
	}

	/**
	 * @param string
	 */
	public void setDay(String strDay) {
		m_strDay = strDay;
	}

	/**
	 * @param string
	 */
	public void setMonth(String strMonth) {
		m_strMonth = strMonth;
	}

	/**
	 * @param string
	 */
	public void setYear(String strYear) {
		m_strYear = strYear;
	}

	/**
	 * @return
	 */
	public String getExpires() {
		return m_strExpires;
	}

	/**
	 * @param string
	 */
	public void setExpires(String strExpires) {
		m_strExpires = strExpires;
	}

	/**
	 * @return
	 */
	public long getDate() {
		Calendar pdCalendar = Calendar.getInstance();
		pdCalendar.setLenient(false);
		pdCalendar.set(Calendar.MONTH, Integer.parseInt(getMonth()));
		pdCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(getDay()));
		pdCalendar.set(Calendar.YEAR, Integer.parseInt(getYear()));
		pdCalendar.set(Calendar.HOUR, 0);
		pdCalendar.set(Calendar.MINUTE, 0);
		pdCalendar.set(Calendar.SECOND, 0);
		pdCalendar.set(Calendar.MILLISECOND, 0);
		
		return pdCalendar.getTimeInMillis();
	}

	/**
	 * @return
	 */
	public long getNextDate() {
		Calendar pdCalendar = Calendar.getInstance();
		pdCalendar.setLenient(false);
		pdCalendar.set(Calendar.MONTH, Integer.parseInt(getMonth()));
		pdCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(getDay()));
		pdCalendar.set(Calendar.YEAR, Integer.parseInt(getYear()));
		pdCalendar.set(Calendar.HOUR, 0);
		pdCalendar.set(Calendar.MINUTE, 0);
		pdCalendar.set(Calendar.SECOND, 0);
		pdCalendar.set(Calendar.MILLISECOND, 0);
		
		pdCalendar.add(Calendar.DATE, 1);
		
		return pdCalendar.getTimeInMillis();
	}

	/**
	 * 
	 */
	public void setCurrent() {
		if (isEmpty()) {
			Date dtExpiration = new Date();
			Calendar pdCalendar = Calendar.getInstance();
			pdCalendar.setTime(dtExpiration);
			pdCalendar.set(Calendar.HOUR, 0);
			pdCalendar.set(Calendar.MINUTE, 0);
			pdCalendar.set(Calendar.SECOND, 0);
			pdCalendar.set(Calendar.MILLISECOND, 0);
			setDay(String.valueOf(pdCalendar.get(Calendar.DAY_OF_MONTH)));
			setMonth(String.valueOf(pdCalendar.get(Calendar.MONTH)));
			setYear(String.valueOf(pdCalendar.get(Calendar.YEAR)));
		}
	}
	
	/**
	 * @return
	 */
	public boolean isEmpty() {
		return StringUtil.isNullorEmpty(getDay());
	}
}
