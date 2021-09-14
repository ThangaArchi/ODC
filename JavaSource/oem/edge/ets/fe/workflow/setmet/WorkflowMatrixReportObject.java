/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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
 * Created on Nov 24, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.setmet;

/**
 * @author ryazuddin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WorkflowMatrixReportObject {
	
	//Common for all Reports
	private String workspace = null;
	private String process = null;
	private String scesector = null;
	private String brand = null;
	private String businesssector = null;
	private String projectID = null;
	private String projectTC = null;
	private String workflowID = null;
	//Report ---  WF0001
	private String clientname = null;
	private String accountcontact = null;
	private String meetingdate = null;
	private String overallscorerating = null;
	private String nsirating = null;
	private String currStageName = null;
	
	//Report  --  WF0002
	private String issuetitle = null;
	private String issueitemno = null;
	private String issueowner= null;
	private String issuereviseddate = null;
	private String issuestatus = null;
	private String issueitemtaskcomments = null;
	private String issueID = null;
	private String issueWorkflowID = null;
	
	//Report --  WF0003
	private String executivesponsor = null;
	private String topissuefromcssurvey = null;

	
	/**
	 * @return Returns the accountcontact.
	 */
	public String getAccountcontact() {
		return accountcontact;
	}
	/**
	 * @param accountcontact The accountcontact to set.
	 */
	public void setAccountcontact(String accountcontact) {
		this.accountcontact = accountcontact;
	}
	/**
	 * @return Returns the brand.
	 */
	public String getBrand() {
		return brand;
	}
	/**
	 * @param brand The brand to set.
	 */
	public void setBrand(String brand) {
		this.brand = brand;
	}
	/**
	 * @return Returns the businesssector.
	 */
	public String getBusinesssector() {
		return businesssector;
	}
	/**
	 * @param businesssector The businesssector to set.
	 */
	public void setBusinesssector(String businesssector) {
		this.businesssector = businesssector;
	}
	/**
	 * @return Returns the clientname.
	 */
	public String getClientname() {
		return clientname;
	}
	/**
	 * @param clientname The clientname to set.
	 */
	public void setClientname(String clientname) {
		this.clientname = clientname;
	}
	/**
	 * @return Returns the executivesponsor.
	 */
	public String getExecutivesponsor() {
		return executivesponsor;
	}
	/**
	 * @param executivesponsor The executivesponsor to set.
	 */
	public void setExecutivesponsor(String executivesponsor) {
		this.executivesponsor = executivesponsor;
	}
	/**
	 * @return Returns the issueitemno.
	 */
	public String getIssueitemno() {
		return issueitemno;
	}
	/**
	 * @param issueitemno The issueitemno to set.
	 */
	public void setIssueitemno(String issueitemno) {
		this.issueitemno = issueitemno;
	}
	/**
	 * @return Returns the issueitemtaskcomments.
	 */
	public String getIssueitemtaskcomments() {
		return issueitemtaskcomments;
	}
	/**
	 * @param issueitemtaskcomments The issueitemtaskcomments to set.
	 */
	public void setIssueitemtaskcomments(String issueitemtaskcomments) {
		this.issueitemtaskcomments = issueitemtaskcomments;
	}
	/**
	 * @return Returns the issueowner.
	 */
	public String getIssueowner() {
		return issueowner;
	}
	/**
	 * @param issueowner The issueowner to set.
	 */
	public void setIssueowner(String issueowner) {
		this.issueowner = issueowner;
	}
	/**
	 * @return Returns the issuereviseddate.
	 */
	public String getIssuereviseddate() {
		return issuereviseddate;
	}
	/**
	 * @param issuereviseddate The issuereviseddate to set.
	 */
	public void setIssuereviseddate(String issuereviseddate) {
		this.issuereviseddate = issuereviseddate;
	}
	/**
	 * @return Returns the issuestatus.
	 */
	public String getIssuestatus() {
		return issuestatus;
	}
	/**
	 * @param issuestatus The issuestatus to set.
	 */
	public void setIssuestatus(String issuestatus) {
		this.issuestatus = issuestatus;
	}
	/**
	 * @return Returns the issuetitle.
	 */
	public String getIssuetitle() {
		return issuetitle;
	}
	/**
	 * @param issuetitle The issuetitle to set.
	 */
	public void setIssuetitle(String issuetitle) {
		this.issuetitle = issuetitle;
	}
	/**
	 * @return Returns the meetingdate.
	 */
	public String getMeetingdate() {
		return meetingdate;
	}
	/**
	 * @param meetingdate The meetingdate to set.
	 */
	public void setMeetingdate(String meetingdate) {
		this.meetingdate = meetingdate;
	}
	/**
	 * @return Returns the nsirating.
	 */
	public String getNsirating() {
		return nsirating;
	}
	/**
	 * @param nsirating The nsirating to set.
	 */
	public void setNsirating(String nsirating) {
		this.nsirating = nsirating;
	}
	/**
	 * @return Returns the overallscorerating.
	 */
	public String getOverallscorerating() {
		return overallscorerating;
	}
	/**
	 * @param overallscorerating The overallscorerating to set.
	 */
	public void setOverallscorerating(String overallscorerating) {
		this.overallscorerating = overallscorerating;
	}
	/**
	 * @return Returns the process.
	 */
	public String getProcess() {
		return process;
	}
	/**
	 * @param process The process to set.
	 */
	public void setProcess(String process) {
		this.process = process;
	}
	/**
	 * @return Returns the scesector.
	 */
	public String getScesector() {
		return scesector;
	}
	/**
	 * @param scesector The scesector to set.
	 */
	public void setScesector(String scesector) {
		this.scesector = scesector;
	}
	/**
	 * @return Returns the topissuefromcssurvey.
	 */
	public String getTopissuefromcssurvey() {
		return topissuefromcssurvey;
	}
	/**
	 * @param topissuefromcssurvey The topissuefromcssurvey to set.
	 */
	public void setTopissuefromcssurvey(String topissuefromcssurvey) {
		this.topissuefromcssurvey = topissuefromcssurvey;
	}
	/**
	 * @return Returns the workspace.
	 */
	public String getWorkspace() {
		return workspace;
	}
	/**
	 * @param workspace The workspace to set.
	 */
	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}
	/**
	 * @return Returns the projectID.
	 */
	public String getProjectID() {
		return projectID;
	}
	/**
	 * @param projectID The projectID to set.
	 */
	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}
	/**
	 * @return Returns the projectTC.
	 */
	public String getProjectTC() {
		return projectTC;
	}
	/**
	 * @param projectTC The projectTC to set.
	 */
	public void setProjectTC(String projectTC) {
		this.projectTC = projectTC;
	}
	/**
	 * @return Returns the issueID.
	 */
	public String getIssueID() {
		return issueID;
	}
	/**
	 * @param issueID The issueID to set.
	 */
	public void setIssueID(String issueID) {
		this.issueID = issueID;
	}
	/**
	 * @return Returns the issueWorkflowID.
	 */
	public String getIssueWorkflowID() {
		return issueWorkflowID;
	}
	/**
	 * @param issueWorkflowID The issueWorkflowID to set.
	 */
	public void setIssueWorkflowID(String issueWorkflowID) {
		this.issueWorkflowID = issueWorkflowID;
	}
	/**
	 * @return Returns the workflowID.
	 */
	public String getWorkflowID() {
		return workflowID;
	}
	/**
	 * @param workflowID The workflowID to set.
	 */
	public void setWorkflowID(String workflowID) {
		this.workflowID = workflowID;
	}
	/**
	 * @return Returns the currStageName.
	 */
	public String getCurrStageName() {
		return currStageName;
	}
	/**
	 * @param currStageName The currStageName to set.
	 */
	public void setCurrStageName(String currStageName) {
		this.currStageName = currStageName;
	}
}
