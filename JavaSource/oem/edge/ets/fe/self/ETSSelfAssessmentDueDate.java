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


/*
 * Created on Jan 21, 2005
 */

package oem.edge.ets.fe.self;

import java.sql.Timestamp;

/**
 * @author v2sathis
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ETSSelfAssessmentDueDate {
	/**
	 *
	 */

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.2";

	private String SelfId = "";
	private String ProjectId = "";
	private String Step = "";
	private Timestamp DueDate = null;
	private String Notified = "";
	private Timestamp LastTimestamp = null;

	public ETSSelfAssessmentDueDate() {
		super();
	}
	/**
	 * @return
	 */
	public Timestamp getDueDate() {
		return DueDate;
	}

	/**
	 * @return
	 */
	public Timestamp getLastTimestamp() {
		return LastTimestamp;
	}

	/**
	 * @return
	 */
	public String getNotified() {
		return Notified;
	}

	/**
	 * @return
	 */
	public String getProjectId() {
		return ProjectId;
	}

	/**
	 * @return
	 */
	public String getSelfId() {
		return SelfId;
	}

	/**
	 * @return
	 */
	public String getStep() {
		return Step;
	}

	/**
	 * @param timestamp
	 */
	public void setDueDate(Timestamp timestamp) {
		DueDate = timestamp;
	}

	/**
	 * @param timestamp
	 */
	public void setLastTimestamp(Timestamp timestamp) {
		LastTimestamp = timestamp;
	}

	/**
	 * @param string
	 */
	public void setNotified(String string) {
		Notified = string;
	}

	/**
	 * @param string
	 */
	public void setProjectId(String string) {
		ProjectId = string;
	}

	/**
	 * @param string
	 */
	public void setSelfId(String string) {
		SelfId = string;
	}

	/**
	 * @param string
	 */
	public void setStep(String string) {
		Step = string;
	}

}
