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
 * Created on Jan 20, 2005
 */

package oem.edge.ets.fe.self;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * @author v2sathis
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ETSSelfAssessment {
	/**
	 *
	 */

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.2";

	private String SelfId = "";
	private String ProjectId = "";
	private String Title = "";
	private String AssessmentOwner = "";
	private String PlanOwner = "";
	private Timestamp StartDate = null;
	private String State = "";
	private Timestamp LastTimestamp = null;
	private ArrayList Members = new ArrayList();
	private ArrayList Expectations = new ArrayList();
	private ArrayList Step = new ArrayList();
	private ArrayList DueDates = new ArrayList();


	public ETSSelfAssessment() {
		super();
	}
	/**
	 * @return
	 */
	public String getAssessmentOwner() {
		return AssessmentOwner;
	}

	/**
	 * @return
	 */
	public ArrayList getExpectations() {
		return Expectations;
	}

	/**
	 * @return
	 */
	public ArrayList getMembers() {
		return Members;
	}

	/**
	 * @return
	 */
	public String getPlanOwner() {
		return PlanOwner;
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
	public Timestamp getStartDate() {
		return StartDate;
	}

	/**
	 * @return
	 */
	public String getState() {
		return State;
	}

	/**
	 * @return
	 */
	public ArrayList getStep() {
		return Step;
	}

	/**
	 * @return
	 */
	public String getTitle() {
		return Title;
	}

	/**
	 * @param string
	 */
	public void setAssessmentOwner(String string) {
		AssessmentOwner = string;
	}

	/**
	 * @param list
	 */
	public void setExpectations(ArrayList list) {
		Expectations = list;
	}

	/**
	 * @param list
	 */
	public void setMembers(ArrayList list) {
		Members = list;
	}

	/**
	 * @param string
	 */
	public void setPlanOwner(String string) {
		PlanOwner = string;
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
	 * @param timestamp
	 */
	public void setStartDate(Timestamp timestamp) {
		StartDate = timestamp;
	}

	/**
	 * @param string
	 */
	public void setState(String string) {
		State = string;
	}

	/**
	 * @param list
	 */
	public void setStep(ArrayList list) {
		Step = list;
	}

	/**
	 * @param string
	 */
	public void setTitle(String string) {
		Title = string;
	}

	/**
	 * @return
	 */
	public Timestamp getLastTimestamp() {
		return LastTimestamp;
	}

	/**
	 * @param timestamp
	 */
	public void setLastTimestamp(Timestamp timestamp) {
		LastTimestamp = timestamp;
	}

	/**
	 * @return
	 */
	public ArrayList getDueDates() {
		return DueDates;
	}

	/**
	 * @param list
	 */
	public void setDueDates(ArrayList list) {
		DueDates = list;
	}

}
