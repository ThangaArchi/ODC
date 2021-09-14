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

import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

/**
 * @author v2sathis
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ETSSetMet {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.4";
	
	private static Log logger = EtsLogger.getLogger(ETSSetMet.class);
	
	private String SetMetID = "";
	private String ProjectID = "";
	private String SetMetName = "";
	private String ClientIRID = "";
	private String SetMetPractice = "";
	private String SetMetBSE = "";
	private Timestamp MeetingDate = null;
	private Timestamp NextMeetingDate = null;
	private String State = "";
	private String InterviewByIRID = "";
	private Timestamp LastTimestamp = null;
	private Vector SetMetStates = null;
	private String SupressFlags = null;
	private String ClientName = "";

	/**
	 * @return
	 */
	public String getClientIRID() {
		return this.ClientIRID;
	}

	/**
	 * @return
	 */
	public String getInterviewByIRID() {
		return this.InterviewByIRID;
	}

	/**
	 * @return
	 */
	public Timestamp getLastTimestamp() {
		return this.LastTimestamp;
	}

	/**
	 * @return
	 */
	public Timestamp getMeetingDate() {
		return this.MeetingDate;
	}

	/**
	 * @return
	 */
	public Timestamp getNextMeetingDate() {
		return this.NextMeetingDate;
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
	public String getSetMetBSE() {
		return this.SetMetBSE;
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
	public String getSetMetName() {
		return this.SetMetName;
	}

	/**
	 * @return
	 */
	public String getSetMetPractice() {
		return this.SetMetPractice;
	}

	/**
	 * @return
	 */
	public String getState() {
		return this.State;
	}

	/**
	 * @param string
	 */
	public void setClientIRID(String string) {
		this.ClientIRID = string;
	}

	/**
	 * @param string
	 */
	public void setInterviewByIRID(String string) {
		this.InterviewByIRID = string;
	}

	/**
	 * @param timestamp
	 */
	public void setLastTimestamp(Timestamp timestamp) {
		this.LastTimestamp = timestamp;
	}

	/**
	 * @param timestamp
	 */
	public void setMeetingDate(Timestamp timestamp) {
		this.MeetingDate = timestamp;
	}

	/**
	 * @param timestamp
	 */
	public void setNextMeetingDate(Timestamp timestamp) {
		this.NextMeetingDate = timestamp;
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
	public void setSetMetBSE(String string) {
		this.SetMetBSE = string;
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
	public void setSetMetName(String string) {
		this.SetMetName = string;
	}

	/**
	 * @param string
	 */
	public void setSetMetPractice(String string) {
		this.SetMetPractice = string;
	}

	/**
	 * @param string
	 */
	public void setState(String string) {
		this.State = string;
	}

	/**
	 * @return
	 */
	public Vector getSetMetStates() {
		return this.SetMetStates;
	}

	/**
	 * @param vector
	 */
	public void setSetMetStates(Vector vector) {
		this.SetMetStates = vector;
	}

	/**
	 * @return
	 */
	public String getSupressFlags() {
		return this.SupressFlags;
	}

	/**
	 * @param string
	 */
	public void setSupressFlags(String string) {
		this.SupressFlags = string;
	}

	/**
	 * @return
	 */
	public String getClientName() {
		return this.ClientName;
	}

	/**
	 * @param string
	 */
	public void setClientName(String string) {
		this.ClientName = string;
	}

}
