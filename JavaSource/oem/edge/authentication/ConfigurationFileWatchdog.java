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
package oem.edge.authentication;

import java.io.*;
import java.util.*;
import org.apache.commons.logging.*;

/**
 * @author bjr
 */
public class ConfigurationFileWatchdog implements Runnable {
	public static final String VERSION = "1.3";
	private static Log log = LogFactory.getLog(ConfigurationFileWatchdog.class);
	private long pollingPeriod = 60*1000;
	private File file = null;
	private long lastModified = 0;
	private Vector listeners = new Vector();

	public ConfigurationFileWatchdog() {
	}
	
	public ConfigurationFileWatchdog(String filename) {
		setFile(filename);
	}
	
	public ConfigurationFileWatchdog(File file) {
		this.file = file;
	}
	
	public ConfigurationFileWatchdog(String filename, long pollingPeriod) {
		setFile(filename);
		setPollingPeriod(pollingPeriod);
	}

	public ConfigurationFileWatchdog(File file, long pollingPeriod) {
		this.file = file;
		setPollingPeriod(pollingPeriod);
	}
	
	public void addConfigurationChangeListener(ConfigurationChangeListener l) {
		listeners.add(l);
	}

	public void removeConfigurationChangeListener(ConfigurationChangeListener l) {
		listeners.remove(l);
	}
	
	protected void notifyListeners(ConfigurationChangeEvent event) {
		for (Iterator iter=listeners.iterator(); iter.hasNext(); ) {
			ConfigurationChangeListener l = (ConfigurationChangeListener) iter.next();
			l.configurationChange(event);
		}
	}
	
	public void setPollingPeriod(long pollingPeriod) {
		this.pollingPeriod = pollingPeriod;
	}
	
	public long getPollingPeriod() {
		return pollingPeriod;
	}
	
	private void checkForModification() {
		try {
			boolean b = file.exists();
			if (file.exists()) {
				long t = file.lastModified();
				if (t > lastModified) {
					log.info("Configuration File change");
					lastModified = t;
					ConfigurationChangeEvent event = new ConfigurationChangeEvent();
					event.setFile(file);
					event.setLastModified(lastModified);
					notifyListeners(event);
				}
			} else {
				log.error("Configuration file ("+file+") does not exist. ");
			}
		} catch (Exception x) {
			log.error(x);
		}
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	
	public void setFile(String filename) {
		file = new File(filename);
	}
	
	public void run() {
		for (;;) {
			try {
				checkForModification();
				Thread.sleep(pollingPeriod);
			} catch (InterruptedException x) {
			}
		}
	}	
}
