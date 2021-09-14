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
 * Created on Jan 17, 2005
 */

package oem.edge.ets.fe.self;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import oem.edge.common.DatesArithmatic;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * @author v2sathis
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ETSSelfAssessmentForm extends ActionForm implements Serializable{
	/**
	 *
	 */

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.2";

	private String SelfId = "";
	private String Title = "";
	private String StartMonth = "";
	private String StartDay = "";
	private String StartYear = "";
	private String AssessmentOwner = "";
	private String PlanOwner = "";

	private String AssessDueDay = "";
	private String AssessDueMonth = "";
	private String AssessDueYear = "";

	private String PlanDueDay = "";
	private String PlanDueMonth = "";
	private String PlanDueYear = "";

	private String MemberDueDay= "";
	private String MemberDueMonth = "";
	private String MemberDueYear = "";

	String Members[] = new String[]{};

	public ETSSelfAssessmentForm() {
		super();
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
	public void setTitle(String string) {
		Title = string;
	}



	/**
	 * @return
	 */
	public String getStartDay() {
		return StartDay;
	}

	/**
	 * @return
	 */
	public String getStartMonth() {
		return StartMonth;
	}

	/**
	 * @return
	 */
	public String getStartYear() {
		return StartYear;
	}

	/**
	 * @param string
	 */
	public void setStartDay(String string) {
		StartDay = string;
	}

	/**
	 * @param string
	 */
	public void setStartMonth(String string) {
		StartMonth = string;
	}

	/**
	 * @param string
	 */
	public void setStartYear(String string) {
		StartYear = string;
	}

	/**
	 * @return
	 */
	public String getAssessmentOwner() {
		return AssessmentOwner;
	}

	/**
	 * @param string
	 */
	public void setAssessmentOwner(String string) {
		AssessmentOwner = string;
	}


	/**
	 * @return
	 */
	public String getAssessDueDay() {
		return AssessDueDay;
	}

	/**
	 * @return
	 */
	public String getAssessDueMonth() {
		return AssessDueMonth;
	}

	/**
	 * @return
	 */
	public String getAssessDueYear() {
		return AssessDueYear;
	}

	/**
	 * @return
	 */
	public String getPlanDueDay() {
		return PlanDueDay;
	}

	/**
	 * @return
	 */
	public String getPlanDueMonth() {
		return PlanDueMonth;
	}

	/**
	 * @return
	 */
	public String getPlanDueYear() {
		return PlanDueYear;
	}

	/**
	 * @param string
	 */
	public void setAssessDueDay(String string) {
		AssessDueDay = string;
	}

	/**
	 * @param string
	 */
	public void setAssessDueMonth(String string) {
		AssessDueMonth = string;
	}

	/**
	 * @param string
	 */
	public void setAssessDueYear(String string) {
		AssessDueYear = string;
	}

	/**
	 * @param string
	 */
	public void setPlanDueDay(String string) {
		PlanDueDay = string;
	}

	/**
	 * @param string
	 */
	public void setPlanDueMonth(String string) {
		PlanDueMonth = string;
	}

	/**
	 * @param string
	 */
	public void setPlanDueYear(String string) {
		PlanDueYear = string;
	}

	/**
	 * @return
	 */
	public String getMemberDueDay() {
		return MemberDueDay;
	}

	/**
	 * @return
	 */
	public String getMemberDueMonth() {
		return MemberDueMonth;
	}

	/**
	 * @return
	 */
	public String getMemberDueYear() {
		return MemberDueYear;
	}

	/**
	 * @param string
	 */
	public void setMemberDueDay(String string) {
		MemberDueDay = string;
	}

	/**
	 * @param string
	 */
	public void setMemberDueMonth(String string) {
		MemberDueMonth = string;
	}

	/**
	 * @param string
	 */
	public void setMemberDueYear(String string) {
		MemberDueYear = string;
	}

	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {

		ActionErrors errors = new ActionErrors();

//		if (Title == null || Title.trim().equals("")) {
//			errors.add("Title",new ActionError("Title cannot be blank. Please enter the title of self assessment."));
//		}

		return errors;
	}

	/**
	 * @return
	 */
	public String[] getMembers() {
		return Members;
	}

	/**
	 * @param strings
	 */
	public void setMembers(String[] strings) {
		Members = strings;
	}

	/**
	 * @return
	 */
	public String getPlanOwner() {
		return PlanOwner;
	}

	/**
	 * @param string
	 */
	public void setPlanOwner(String string) {
		PlanOwner = string;
	}

	/**
	 * @return
	 */
	public String getSelfId() {
		return SelfId;
	}

	/**
	 * @param string
	 */
	public void setSelfId(String string) {
		SelfId = string;
	}

}
