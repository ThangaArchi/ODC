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

/**
 * @author v2sathis
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ETSSelfAssessmentMember {
	/**
	 *
	 */

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.2";

	private String SelfId = "";
	private String ProjectId = "";
	private String MemberId = "";
	private String MemberName = "";
	private String MemberEmail = "";
	private String Completed = "";
	private Timestamp DueDate = null;
	private String LastUserId = "";
	private String LastUserName = "";


	public ETSSelfAssessmentMember() {
		super();
	}
	/**
	 * @return
	 */
	public String getCompleted() {
		return Completed;
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
	public String getLastUserId() {
		return LastUserId;
	}

	/**
	 * @return
	 */
	public String getLastUserName() {
		return LastUserName;
	}

	/**
	 * @return
	 */
	public String getMemberEmail() {
		return MemberEmail;
	}

	/**
	 * @return
	 */
	public String getMemberId() {
		return MemberId;
	}

	/**
	 * @return
	 */
	public String getMemberName() {
		return MemberName;
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
	 * @param string
	 */
	public void setCompleted(String string) {
		Completed = string;
	}

	/**
	 * @param timestamp
	 */
	public void setDueDate(Timestamp timestamp) {
		DueDate = timestamp;
	}

	/**
	 * @param string
	 */
	public void setLastUserId(String string) {
		LastUserId = string;
	}

	/**
	 * @param string
	 */
	public void setLastUserName(String string) {
		LastUserName = string;
	}

	/**
	 * @param string
	 */
	public void setMemberEmail(String string) {
		MemberEmail = string;
	}

	/**
	 * @param string
	 */
	public void setMemberId(String string) {
		MemberId = string;
	}

	/**
	 * @param string
	 */
	public void setMemberName(String string) {
		MemberName = string;
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

}
