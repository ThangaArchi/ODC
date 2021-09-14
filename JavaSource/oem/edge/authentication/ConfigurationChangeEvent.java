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

/**
 * @author bjr
 */
public class ConfigurationChangeEvent {
	public static final String VERSION = "1.3";
	private File file = null;
	private long lastModified = -1l;
	
	/**
	 * Returns the filename.
	 * @return String
	 */
	public File getFilen() {
		return file;
	}

	/**
	 * Returns the lastModified.
	 * @return long
	 */
	public long getLastModified() {
		return lastModified;
	}

	/**
	 * Sets the filename.
	 * @param filename The filename to set
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * Sets the lastModified.
	 * @param lastModified The lastModified to set
	 */
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

}
