/*
 * Created on Sep 18, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.setmet;

import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import javax.print.attribute.Size2DSyntax;

/**
 * @author ryazuddin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Test {

	public  String getCurrentQTR(){
		String QTR=null;
		Calendar cal = new GregorianCalendar();
		int currentMonth = cal.get(Calendar.MONTH);
		switch(currentMonth){
		case 0:case 1:case 2:   QTR = "01"; break;
		case 3:case 4:case 5:   QTR = "02"; break;
		case 6:case 7:case 8:   QTR = "03"; break;
		case 9:case 10:case 11: QTR = "04"; break;
		}
		return QTR;
	}
	public static String generateUniqueWorkflowID() throws SQLException, Exception {

		String wfId = "";
		Long lDate = new Long(System.currentTimeMillis());

		wfId = lDate.toString();

		return wfId;
	}
	public static void main(String[] args) {
		Test t = new Test();
		/*int x = Integer.parseInt(t.getCurrentQTR().trim());
		java.sql.Timestamp ts = new java.sql.Timestamp(System.currentTimeMillis());
		System.out.println("Timestamp"+ts.toString());
		try {
			System.out.println(x  + "Testing ....." +generateUniqueWorkflowID());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	/*	String date ="2006-01-01 00:00:00.0";
		StringTokenizer st = new StringTokenizer(date,"-");
		String year = st.nextElement().toString();
		String month = st.nextElement().toString();
		String dd = st.nextElement().toString();
		StringTokenizer st1 = new StringTokenizer(dd);
		String day = st1.nextElement().toString();
		String schduledate =  month+"/"+day+"/"+year.substring(2);
		System.out.println(schduledate);*/
		
		String x = "aa,bb,cc,dd,ry" ;
		int pos = x.length()-1;
		System.out.println(pos + "oooo" + x.charAt(pos));
		System.out.println(x.substring(0,(x.length()-1))+x.substring(x.length()));
				
	}

}
