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

/**
 * @author 		: v2sathis
 * Created on 		: Feb 19, 2004 
 */
package oem.edge.ets.fe.ismgt.actions;

import java.sql.Timestamp;

public class ETSFeedbackBean {
    
    public static final String VERSION = "1.2";
    
    protected String ApplicationId;
    protected String EdgeProblemId;
    protected String CQTrackId;
    protected String ProblemState;
    protected int SeqNo;
    protected String ProblemCreator;
    protected String CustName;
    protected String CustEmail;
    protected String CustPhone;
    protected String CustCompany;
    protected String CustProjectName;
    protected String ETSProjectId;
    protected String ProblemClass;
    protected String Title;
    protected String Severity;
    protected String ProblemType;
    protected String Comments;
    protected String LastUserId;
    protected Timestamp LastTime;
    

	public String getApplicationId() {
		return ApplicationId;
	}

	public String getComments() {
		return Comments;
	}

	public String getCQTrackId() {
		return CQTrackId;
	}

	public String getCustCompany() {
		return CustCompany;
	}

	public String getCustEmail() {
		return CustEmail;
	}

	public String getCustName() {
		return CustName;
	}

	public String getCustPhone() {
		return CustPhone;
	}

	public String getCustProjectName() {
		return CustProjectName;
	}

	public String getEdgeProblemId() {
		return EdgeProblemId;
	}

	public String getLastUserId() {
		return LastUserId;
	}

	public String getProblemClass() {
		return ProblemClass;
	}

	public String getProblemCreator() {
		return ProblemCreator;
	}

	public String getProblemState() {
		return ProblemState;
	}

	public String getProblemType() {
		return ProblemType;
	}

	public int getSeqNo() {
		return SeqNo;
	}

	public String getSeverity() {
		return Severity;
	}

	public Timestamp getLastTime() {
		return LastTime;
	}

	public String getTitle() {
		return Title;
	}

	public String getETSProjectId() {
		return ETSProjectId;
	}

	public void setApplicationId(String applicationId) {
		ApplicationId = applicationId;
	}

	public void setComments(String comments) {
		Comments = comments;
	}

	public void setCQTrackId(String cQTrackId) {
		CQTrackId = cQTrackId;
	}

	public void setCustCompany(String custCompany) {
		CustCompany = custCompany;
	}

	public void setCustEmail(String custEmail) {
		CustEmail = custEmail;
	}

	public void setCustName(String custName) {
		CustName = custName;
	}

	public void setCustPhone(String custPhone) {
		CustPhone = custPhone;
	}

	public void setCustProjectName(String custProjectName) {
		CustProjectName = custProjectName;
	}

	public void setEdgeProblemId(String edgeProblemId) {
		EdgeProblemId = edgeProblemId;
	}

	public void setLastUserId(String lastUserId) {
		LastUserId = lastUserId;
	}

	public void setProblemClass(String problemClass) {
		ProblemClass = problemClass;
	}

	public void setProblemCreator(String problemCreator) {
		ProblemCreator = problemCreator;
	}

	public void setProblemState(String problemState) {
		ProblemState = problemState;
	}

	public void setProblemType(String problemType) {
		ProblemType = problemType;
	}

	public void setSeqNo(int seqNo) {
		SeqNo = seqNo;
	}

	public void setSeverity(String severity) {
		Severity = severity;
	}

	public void setLastTime(Timestamp lastTime) {
		LastTime = lastTime;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public void setETSProjectId(String eTSProjectId) {
		ETSProjectId = eTSProjectId;
	}

}
