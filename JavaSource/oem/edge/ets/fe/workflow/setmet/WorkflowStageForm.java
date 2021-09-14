/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2006                                     */
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
 * Created on Sep 25, 2006
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

import java.util.ArrayList;
import java.util.Vector;

import oem.edge.ets.fe.workflow.core.WorkflowForm;

import org.apache.struts.upload.FormFile;

public class WorkflowStageForm extends WorkflowForm {
	
	private ArrayList workflowStageList = null;
	private String projectID 		    = null;
	private String tabID			    = null;
	FormFile uploadedFile ;
	private String uploadedFileDesc     = null;
	private  Vector documents = null;
	
	private String fileName = null;
	private int fileSize = 0;
	private String description = null;
	private String date = null;
	
	private String workflowName = null;
	private String workflowID = null;
	private String meetingDate = null;
	private String workflowOwner = null;
	private String status = null;
	
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
	 * @return Returns the workflowName.
	 */
	public String getWorkflowName() {
		return workflowName;
	}
	/**
	 * @param workflowName The workflowName to set.
	 */
	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}
	/**
	 * @return Returns the uploadedFile.
	 */
	public FormFile getUploadedFile() {
		return uploadedFile;
	}
	/**
	 * @param uploadedFile The uploadedFile to set.
	 */
	public void setUploadedFile(FormFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}
	/**
	 * @return Returns the uploadedFileDesc.
	 */
	public String getUploadedFileDesc() {
		return uploadedFileDesc;
	}
	/**
	 * @param uploadedFileDesc The uploadedFileDesc to set.
	 */
	public void setUploadedFileDesc(String uploadedFileDesc) {
		this.uploadedFileDesc = uploadedFileDesc;
	}
	/**
	 * @return Returns the projectID.
	 */
	public String getProjectID() {
		System.out.println("projectID is -=-=-=-=-=-=-="+projectID);
		return projectID;
	}
	/**
	 * @param projectID The projectID to set.
	 */
	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}
	/**
	 * @return Returns the tabID.
	 */
	public String getTabID() {
		return tabID;
	}
	/**
	 * @param tabID The tabID to set.
	 */
	public void setTabID(String tabID) {
		this.tabID = tabID;
	}
	public WorkflowStageForm(){
		workflowStageList = new ArrayList();
	}
	/**
	 * @return Returns the workflowStageList.
	 */
	public ArrayList getWorkflowStageList() {
		return workflowStageList;
	}
	/**
	 * @param workflowStageList The workflowStageList to set.
	 */
	public void setWorkflowStageList(ArrayList workflowStageList) {
		this.workflowStageList = workflowStageList;
	}
	public void reset(){
		
	}

	/**
	 * @return Returns the date.
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date The date to set.
	 */
	public void setDate(String date) {
		this.date = date;
	}
	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return Returns the fileName.
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName The fileName to set.
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * @return Returns the fileSize.
	 */
	public int getFileSize() {
		return fileSize;
	}
	/**
	 * @param fileSize The fileSize to set.
	 */
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}
	/**
	 * @return Returns the meetingDate.
	 */
	public String getMeetingDate() {
		return meetingDate;
	}
	/**
	 * @param meetingDate The meetingDate to set.
	 */
	public void setMeetingDate(String meetingDate) {
		this.meetingDate = meetingDate;
	}
	/**
	 * @return Returns the workflowOwner.
	 */
	public String getWorkflowOwner() {
		return workflowOwner;
	}
	/**
	 * @param workflowOwner The workflowOwner to set.
	 */
	public void setWorkflowOwner(String workflowOwner) {
		this.workflowOwner = workflowOwner;
	}
	/**
	 * @return Returns the status.
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status The status to set.
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return Returns the documents.
	 */
	public Vector getDocuments() {
		return documents;
	}
	/**
	 * @param documents The documents to set.
	 */
	public void setDocuments(Vector documents) {
		this.documents = documents;
	}
}

