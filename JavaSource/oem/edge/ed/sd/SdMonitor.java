package oem.edge.ed.sd;

/************************** BOF : HEADER *************************************/
/* IBM Confidential                                                          */
/* OCO Source Materials                                                      */
/* Identifier: ICC/PROFIT                                                    */
/* (C) Copyright IBM Corp. 2002, 2003                                        */
/* The source code for this program is not published or otherwise divested   */
/* of its trade secrets, irrespective of what has been deposited with the    */
/* U.S. Copyright Office                                                     */
/************************** RCS & COPYRT *************************************/
/************************** EOF : HEADER *************************************/
/////////////////////////////////////////////////////////////////////////////
//
//                            Edge 3.1.1
//                      Dev: Navneet Gupta
//
//////////////////////////////////////////////////////////////////////////////

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.io.*;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2002-2004                                     */
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

public class SdMonitor extends Thread {

    public final static String Copyright = "(C) Copyright IBM Corp. 2002, 2003";
    final static String version = "v072202.1: ";
    private static final int logInterval = 5;    // minutes
    private static final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy-HH:mm:ss");

    private static boolean started = false;
    private static long intervalMillis = logInterval * 60 * 1000;    // milliseconds
    private static long nextCheckTime;
    private static int newThreadCount = 0;

    private static String log;

    private static Hashtable threadInfo;


    static void startSdMonitor(String log) {
	if( ! started ) {
	    new SdMonitor(log).start();
	    started = true;
	}
    }


    private SdMonitor(String log) {
	this.log = log;
	threadInfo = new Hashtable();
	formatter.setCalendar(Calendar.getInstance());
    }


    public void run() {
	try {
	    nextCheckTime = getRoundedTime();
	    if(nextCheckTime - System.currentTimeMillis() < 30000)
		nextCheckTime += intervalMillis;

	    while(true) {
		long sleepTime = nextCheckTime - System.currentTimeMillis();
	        sleep(sleepTime);

	        long[] stats = scanTable();
	        long totalBytesDownloaded = stats[0];
                int KBperSec = (int) (totalBytesDownloaded / sleepTime);
	        int allThreads = (int) stats[1];
	        int newThreads = (int) stats[2];
	        logStats(KBperSec, allThreads, newThreads);

	        nextCheckTime += intervalMillis;
	    }
	}
	catch(Throwable t) {
            SdUtils.sendImpAlert("Fatal exception in SdMonitor!\nStacktrace:\n" + getStackTrace(t));
	}
    }


    private static long getRoundedTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 0);
        int minutes = cal.get(Calendar.MINUTE);
        int addMinutes = logInterval - (minutes % logInterval);
        cal.add(Calendar.MINUTE, addMinutes);
        return cal.getTime().getTime();
    }


    private static void logStats(int KBperSec, int allThreads, int newThreads) {
      //  if( ! SdUtils.authenticate() )
      //    SdUtils.sendImpAlert("SdUtils failed to authenticate");
	String str = formatter.format(new Date(nextCheckTime)) + " " + KBperSec + " " + allThreads + " " + newThreads + "\n";
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(log, true);
            out.write(str.getBytes());
        }
        catch(Throwable t) {
            SdUtils.sendImpAlert("Exception writing the following to " + log + ":\n" + str + "\nStacktrace:\n" + getStackTrace(t));
        }
        finally {
            try {
                out.close();
            }
            catch(Throwable t) { }
        }
    }


    private static synchronized long[] scanTable() {
	long totalBytesDownloaded = 0;
	long count = 0;

        Enumeration list = threadInfo.keys();
        while(list.hasMoreElements()) {
            SdWriter t = (SdWriter) list.nextElement();
            int[] info = (int[]) threadInfo.get(t);

	    totalBytesDownloaded += info[0];
	    count++;

            if(info[1] == 1)
                threadInfo.remove(t);
            else
                info[0] = 0;
	}

	long[] stats = new long[] {totalBytesDownloaded, count, newThreadCount};
	newThreadCount = 0;

	return stats;
    }


    static synchronized void addThread(SdWriter t) {
	threadInfo.put(t, new int[] {0, 0});
	newThreadCount++;
    }


    static synchronized void markThreadForDeletion(SdWriter t) {
	int[] info = (int[]) threadInfo.get(t);
	info[1] = 1;
    }


    static synchronized void updateThreadInfo(SdWriter t, int bytes) {
	int[] info = (int[]) threadInfo.get(t);
	info[0] += bytes;
    }


    private static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        String stackTrace = sw.toString();
        pw.close();
        return stackTrace;
    }

}

