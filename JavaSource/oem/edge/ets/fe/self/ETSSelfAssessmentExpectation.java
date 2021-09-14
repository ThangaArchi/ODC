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
public class ETSSelfAssessmentExpectation {
	/**
	 *
	 */

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.2";

	private String SelfId = "";
	private String ProjectId = "";
	private int SectionId = 0;
	private int SubSectionId = 0;
	private int SequenceNo = 0;
	private String MemberId = "";
	private String MemberName = "";
	private String Comments = "";
	private int Rating = 0;
	private int ExpectId = 0;
	private String ExpectName = "";
	private String LastUserId = "";
	private String LastUserName = "";
	private Timestamp LastTimestamp = null;

	public ETSSelfAssessmentExpectation() {
		super();
	}
	/**
	 * @return
	 */
	public String getComments() {
		return Comments;
	}

	/**
	 * @return
	 */
	public int getExpectId() {
		return ExpectId;
	}

	/**
	 * @return
	 */
	public String getExpectName() {
		return ExpectName;
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
	public String getProjectId() {
		return ProjectId;
	}

	/**
	 * @return
	 */
	public int getRating() {
		return Rating;
	}

	/**
	 * @return
	 */
	public int getSectionId() {
		return SectionId;
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
	public int getSequenceNo() {
		return SequenceNo;
	}

	/**
	 * @return
	 */
	public int getSubSectionId() {
		return SubSectionId;
	}

	/**
	 * @param string
	 */
	public void setComments(String string) {
		Comments = string;
	}

	/**
	 * @param i
	 */
	public void setExpectId(int i) {
		ExpectId = i;
	}

	/**
	 * @param string
	 */
	public void setExpectName(String string) {
		ExpectName = string;
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
	public void setProjectId(String string) {
		ProjectId = string;
	}

	/**
	 * @param i
	 */
	public void setRating(int i) {
		Rating = i;
	}

	/**
	 * @param i
	 */
	public void setSectionId(int i) {
		SectionId = i;
	}

	/**
	 * @param string
	 */
	public void setSelfId(String string) {
		SelfId = string;
	}

	/**
	 * @param i
	 */
	public void setSequenceNo(int i) {
		SequenceNo = i;
	}

	/**
	 * @param i
	 */
	public void setSubSectionId(int i) {
		SubSectionId = i;
	}

	/**
	 * @return
	 */
	public String getMemberId() {
		return MemberId;
	}

	/**
	 * @param string
	 */
	public void setMemberId(String string) {
		MemberId = string;
	}

	/**
	 * @return
	 */
	public String getMemberName() {
		return MemberName;
	}

	/**
	 * @param string
	 */
	public void setMemberName(String string) {
		MemberName = string;
	}

}
