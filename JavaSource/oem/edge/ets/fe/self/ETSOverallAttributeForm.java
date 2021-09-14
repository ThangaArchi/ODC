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

import org.apache.struts.action.ActionForm;

/**
 * @author v2sathis
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ETSOverallAttributeForm extends ActionForm {
	/**
	 *
	 */

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.1";


	private String SelfId = "";
	private String ProjectId = "";
	private String SectionId = "";
	private String SubSectionId = "";
	private String SequenceNo = "";
	private String MemberId = "";
	private String Comments = "";
	private String Rating = "";
	private String ExpectId = "";
	private String Operation = "";
	private String OverallRating = "";

	public ETSOverallAttributeForm() {
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
	public String getExpectId() {
		return ExpectId;
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
	public String getProjectId() {
		return ProjectId;
	}

	/**
	 * @return
	 */
	public String getRating() {
		return Rating;
	}

	/**
	 * @return
	 */
	public String getSectionId() {
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
	public String getSequenceNo() {
		return SequenceNo;
	}

	/**
	 * @return
	 */
	public String getSubSectionId() {
		return SubSectionId;
	}

	/**
	 * @param string
	 */
	public void setComments(String string) {
		Comments = string;
	}

	/**
	 * @param string
	 */
	public void setExpectId(String string) {
		ExpectId = string;
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
	public void setProjectId(String string) {
		ProjectId = string;
	}

	/**
	 * @param string
	 */
	public void setRating(String string) {
		Rating = string;
	}

	/**
	 * @param string
	 */
	public void setSectionId(String string) {
		SectionId = string;
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
	public void setSequenceNo(String string) {
		SequenceNo = string;
	}

	/**
	 * @param string
	 */
	public void setSubSectionId(String string) {
		SubSectionId = string;
	}

	/**
	 * @return
	 */
	public String getOperation() {
		return Operation;
	}

	/**
	 * @param string
	 */
	public void setOperation(String string) {
		Operation = string;
	}

	/**
	 * @return
	 */
	public String getOverallRating() {
		return OverallRating;
	}

	/**
	 * @param string
	 */
	public void setOverallRating(String string) {
		OverallRating = string;
	}

}
