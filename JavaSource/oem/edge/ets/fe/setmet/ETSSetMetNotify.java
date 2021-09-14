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

package oem.edge.ets.fe.setmet;

import java.sql.Timestamp;
import java.util.Vector;

/**
 * @author v2sathis
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ETSSetMetNotify {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.1";
	
	private String SetMetID = "";
	private String ProjectID = "";
	private String Step = "";
	private Timestamp DueDate = null;
	private String Notified = "";
	private Timestamp LstTimestamp = null;

	/**
	 * @return
	 */
	public Timestamp getDueDate() {
		return this.DueDate;
	}

	/**
	 * @return
	 */
	public Timestamp getLastTimestamp() {
		return this.LstTimestamp;
	}

	/**
	 * @return
	 */
	public String getNotified() {
		return this.Notified;
	}

	/**
	 * @return
	 */
	public String getProjectID() {
		return this.ProjectID;
	}

	/**
	 * @return
	 */
	public String getSetMetID() {
		return this.SetMetID;
	}

	/**
	 * @return
	 */
	public String getStep() {
		return this.Step;
	}

	/**
	 * @param timestamp
	 */
	public void setDueDate(Timestamp timestamp) {
		this.DueDate = timestamp;
	}

	/**
	 * @param timestamp
	 */
	public void setLastTimestamp(Timestamp timestamp) {
		this.LstTimestamp = timestamp;
	}

	/**
	 * @param string
	 */
	public void setNotified(String string) {
		this.Notified = string;
	}

	/**
	 * @param string
	 */
	public void setProjectID(String string) {
		this.ProjectID = string;
	}

	/**
	 * @param string
	 */
	public void setSetMetID(String string) {
		this.SetMetID = string;
	}

	/**
	 * @param string
	 */
	public void setStep(String string) {
		this.Step = string;
	}

}
