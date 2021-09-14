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
 * Created on Jan 22, 2005
 */

package oem.edge.ets.fe.self;

import java.sql.Timestamp;

/**
 * @author v2sathis
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ETSSelfMemberSectionStatus {
	/**
	 *
	 */

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.2";

	private String SelfId = "";
	private String ProjectId = "";
	private int SectionId = 0;
	private String MemberId = "";
	private String MemberName = "";
	private String Status = "";
	private Timestamp LastTimestamp = null;


	public ETSSelfMemberSectionStatus() {
		super();
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
	public String getStatus() {
		return Status;
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
	 * @param string
	 */
	public void setStatus(String string) {
		Status = string;
	}

}
