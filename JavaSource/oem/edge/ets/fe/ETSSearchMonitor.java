package oem.edge.ets.fe;

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
/*     US Government Users Restricted Rights                                */
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
 * The <code>ETSSearchMonitor</code> class runs as an independent thread.
 * It is used to monitor the ETS search index on disk and to load 
 * the core index into memory when it changes.
 * <p />
 * It masks the 10-15 second index load time from end users conducting searches.
 *
 * @author  Navneet Gupta (navneet@us.ibm.com)
 * @since   custcont.3.7.1
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.MissingResourceException;

import org.apache.commons.logging.Log;

import com.ibm.hrl.juru.DocScore;
import com.ibm.hrl.juru.Index;
import com.ibm.hrl.juru.Result;
import com.ibm.hrl.juru.TfIdfQuery;

public class ETSSearchMonitor extends Thread {

	public static final String Copyright = "(C) Copyright IBM Corp. 2003, 2004";

	public static final String VERSION_SID = "1.24";
	public static final String LAST_UPDATE = "10/13/05 09:13:20";

	private static final Class thisClass = ETSSearchMonitor.class;

	private static final Log log = ETSSearchCommon.getLog(thisClass);

	private static final int checkInterval = 5; // minutes
	private static final long checkIntervalMillis = checkInterval * 60 * 1000;

	private final String indexDirectory;
	private final File indexPointer;

	private String indexDirInUse;

	private Index indexObj;

	private long indexLoadTime;
	private long indexLoadDuration;
	private long indexPointerTimestamp;

	private long threadInitTime;
	private static long lastThreadInitTime;

	private boolean stopThread = false;

	private static boolean started = false;

	static boolean hasStarted() {
		return started;
	}

	static synchronized ETSSearchMonitor startThread() throws Exception {
		if (!started) {
			ETSSearchMonitor thread = new ETSSearchMonitor();
			thread.start();
			started = true;
			return thread;
		} else {
			logError("Cannot start multiple instances of ETSSearchMonitor");
			return null;
		}
	}

	boolean stopThread() {
		if (!started) {
			logError("Thread not yet started");
			return false;
		} else if (stopThread) {
			logError("Thread already stopped");
			return false;
		} else {
			log.warn("Stopping thread");
			stopThread = true;
			return true;
		}
	}

	DocScore[] queryIndex(
		TfIdfQuery query,
		int expansionType,
		boolean applySearchOperators)
		throws IOException {

		Result result =
			query.execute(indexObj, expansionType, applySearchOperators);

		return result.getNormalizedDocScores();

	}

	boolean refreshIndices() throws Exception {
		return loadIndices(false, true);
	}

	private ETSSearchMonitor() throws Exception {

		String edgelogDir =
			ETSSearchCommon.getGwaProperty("gwa.edge_log_dir", null);

		if (edgelogDir == null) {
			throw new MissingResourceException(
				"Missing key: gwa.edge_log_dir in oem.edge.common.gwa.properties",
				"oem.edge.common.gwa",
				"gwa.edge_log_dir");
		}

		if (!edgelogDir.endsWith(File.separator)) {
			edgelogDir += File.separator;
		}

		indexDirectory = edgelogDir + "ets_search" + File.separator;

		indexPointer = new File(indexDirectory + "indexInUse");

		loadIndices(true, false);

		threadInitTime = System.currentTimeMillis();
		lastThreadInitTime = threadInitTime;

	}

	public void run() {

		try {

			long nextCheckTime = getRoundedTime();

			while (true) {

				long time = System.currentTimeMillis();
				long sleepTime = nextCheckTime - time;
				if (sleepTime < 30000) {
					nextCheckTime = getRoundedTime();
					sleepTime = nextCheckTime - System.currentTimeMillis();
				}

				sleep(sleepTime);

				if (stopThread) {
					log.warn(
						"Exiting this thread which started at: "
							+ new Date(threadInitTime));
					return;
				} else if (log.isDebugEnabled()) {
					log.debug(
						"Checking indices. This thread started at: "
							+ new Date(threadInitTime));
				}

				loadIndices(false, false);

				nextCheckTime += checkIntervalMillis;

			}

		} catch (Throwable t) {
			started = false;
			logError("FATAL exception in ETSSearchMonitor.run() !!!", t);
		}

	}

	static long getRoundedTime() {
		// it is probably better not to synchronize the two servers
		// since that can potentially cause race conditions in accessing files
		return System.currentTimeMillis() + checkIntervalMillis;

		/*
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.SECOND, 0);
		int minutes = cal.get(Calendar.MINUTE);
		int addMinutes = checkInterval - (minutes % checkInterval);
		cal.add(Calendar.MINUTE, addMinutes);
		return cal.getTime().getTime();
		*/
	}

	private boolean loadIndices(boolean initialize, boolean force)
		throws IOException {

		synchronized (this.getClass()) {

			boolean success = true;

			// try {
			long newTimestamp = indexPointer.lastModified();

			if (force || newTimestamp > indexPointerTimestamp) {
				log.info("Loading index");

				indexDirInUse = readFile(indexPointer).trim();

				long startTime = System.currentTimeMillis();
				Index tmpIndex = new Index(indexDirInUse, false);
				indexLoadTime = System.currentTimeMillis();

				indexObj = tmpIndex;

				indexLoadDuration = (indexLoadTime - startTime) / 1000;
				indexPointerTimestamp = newTimestamp;
			}
			/*
			} catch (Exception e) {
				success = false;
				if (initialize)
					throw e;
				else
					logError("Exception in ETSSearchMonitor.loadIndices", e);
			}
			*/

			return success;
		}

	}

	private String readFile(File f) throws IOException {

		InputStreamReader reader =
			new InputStreamReader(new FileInputStream(f));

		StringBuffer content = new StringBuffer(64);

		int charsRead = 0;
		char[] arr = new char[64];

		while ((charsRead = reader.read(arr)) > 0) {
			content.append(arr, 0, charsRead);
		}

		reader.close();

		return content.toString();

	}

	String getIndexInfo() {
		return ""
			+ "IndexDirectory:               "
			+ indexDirectory
			+ "\n"
			+ "indexPointerTimestamp:        "
			+ new Date(indexPointerTimestamp)
			+ "\n"
			+ "indexLoadTime:                "
			+ new Date(indexLoadTime)
			+ "\n"
			+ "indexDirInUse:                "
			+ indexDirInUse
			+ "\n"
			+ "indexLoadDuration:            "
			+ indexLoadDuration
			+ "\n"
			+ "monitor started:              "
			+ started
			+ "\n"
			+ "lastThreadInitTime:           "
			+ new Date(lastThreadInitTime)
			+ "\n";

	}

	private static void logError(String subject) {
		log.error(subject);
		ETSSearchCommon.sendMail(subject, "", thisClass);
	}

	private static void logError(String subject, Throwable t) {
		log.error(subject, t);
		ETSSearchCommon.sendMail(subject, "", t, thisClass);
	}

}
