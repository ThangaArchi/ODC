package oem.edge.ets_pmo.datastore.project;

import java.util.Vector;
import java.sql.Timestamp;

import oem.edge.ets_pmo.datastore.project.wbs.WBSElement;
import oem.edge.ets_pmo.datastore.resource.Resource;
import oem.edge.ets_pmo.datastore.exception.exception;
import oem.edge.ets_pmo.datastore.sc.ScoreCard;
import oem.edge.ets_pmo.datastore.util.RTFData;
import oem.edge.ets_pmo.util.*;
import oem.edge.ets_pmo.datastore.document.*;
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
 * @author subbus
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class Project {
	private static String CLASS_VERSION = "4.5.1";
	Base64Decoder bd = new Base64Decoder();
/* *******************
 * The data stored in xyz and xyz_Rank are the <value>content</value> and <rank> rank content</rank>
 * respectively
 * *******************/
 	/* IsReportable determines if this 
 * WBSElement should be reported to 
 * external customers. This gets stored in
 * the database in ets.ets_pmo_main in isReportable
 * field
 */
private boolean isReportable=false;
 	private String Type;
	private String ProjectId;
	private String ProjectId_Rank = null;
	
	private String element_name;
	private String element_name_Rank = null;
	
	private String reference_number;
	private String reference_number_Rank = null;
	
	private String ref_code;
	
	private String calendar_id;
	private String calendar_id_Rank = null;
	
	private String currency_id;
	private String currency_id_Rank = null;
	
	private String published;
	private String published_Rank = null;
	
	private Timestamp estimatedStart = null;
	private String estimatedStart_Rank = null;
	
	private Timestamp estimatedFinish = null;
	private String estimatedFinish_Rank = null;
	
	private String state;
	private String state_Rank = null;
	
	private String revision_history;
	private String revision_history_Rank = null;
	
	private Timestamp start = null;
	private String start_Rank = null;
	
	private Timestamp finish = null;
	private String finish_Rank = null;
	
	private String duration;
	private String duration_Rank = null;
	
	private String workPercent;
	private String workPercent_Rank = null;
	
	private String estimatedETC;
	private String estimatedETC_Rank = null;
	
	private String percentComplete;
	private String percentComplete_Rank = null;
	

	private String ScopeRTF;
	private String objectivesRTF;
	private String backgroundRTF;
	private String statusRTF;
	private String targetSolnRTF;
	private Vector vRTF;
	
	private ScoreCard scorecard;
	private Vector vResources;
	private Vector vWBS;
	private Vector vexceptions;
	private Vector vDocs;
	private Vector vProj;
	
	private String SD = null;
	private String FD = null;
	
	private Timestamp scheduledStart = null;
	private Timestamp scheduledFinish = null;
	private Timestamp proposedStart = null;
	private Timestamp proposedFinish = null;
	private Timestamp forecastStart = null;
	private Timestamp forecastFinish = null;
	private Timestamp baseline1Finish = null;
	private Timestamp baseline2Finish = null;
	private Timestamp baseline3Finish = null;
	private Timestamp actualFinish		= null;
	
	private String trail_hh_mm_ss_ffffffff = " 00:00:00.00000000";

	public String getRef_code() {
		return ref_code;
	}
	public void setRef_code(String ref_code) {
		this.ref_code = ref_code;
	}
	
	/**
	 * Returns the backgroundRTF.
	 * @return String
	 */
	public String getBackgroundRTF() {
		return backgroundRTF;
	}

	/**
	 * Returns the duration.
	 * @return String
	 */
	public String getDuration() {
		return duration;
	}

	/**
	 * Returns the estimatedETC.
	 * @return String
	 */
	public String getEstimatedETC() {
		return estimatedETC;
	}

	/**
	 * Returns the estimatedFinish.
	 * @return String
	 */
	public Timestamp getEstimatedFinish() {
		return estimatedFinish;
	}

	/**
	 * Returns the estimatedStart.
	 * @return String
	 */
	public Timestamp getEstimatedStart() {
		return estimatedStart;
	}

	/**
	 * Returns the finish.
	 * @return String
	 */
	public Timestamp getFinish() {
		return finish;
	}

	/**
	 * Returns the objectivesRTF.
	 * @return String
	 */
	public String getObjectivesRTF() {
		return objectivesRTF;
	}

	/**
	 * Returns the percentComplete.
	 * @return String
	 */
	public String getPercentComplete() {
		return percentComplete;
	}

	/**
	 * Returns the revision_history.
	 * @return String
	 */
	public String getRevision_history() {
		return revision_history;
	}

	/**
	 * Returns the scopeRTF.
	 * @return String
	 */
	public String getScopeRTF() {
		return ScopeRTF;
	}

	/**
	 * Returns the start.
	 * @return String
	 */
	public Timestamp getStart() {
		return start;
	}

	/**
	 * Returns the state.
	 * @return String
	 */
	public String getState() {
		return state;
	}

	/**
	 * Returns the statusRTF.
	 * @return String
	 */
	public String getStatusRTF() {
		return statusRTF;
	}

	/**
	 * Returns the targetSolnRTF.
	 * @return String
	 */
	public String getTargetSolnRTF() {
		return targetSolnRTF;
	}

	/**
	 * Returns the workPercent.
	 * @return String
	 */
	public String getWorkPercent() {
		return workPercent;
	}

	/**
	 * Sets the backgroundRTF.
	 * @param backgroundRTF The backgroundRTF to set
	 */
	public void setBackgroundRTF(String backgroundRTF) {
		/*
		 * Communication probs with SystemCorp. 
		 * They are sending values in plain text instead 
		 * of RTFs which was the agreement.
		 * Need to change the code here
		 */
		//this.backgroundRTF = bd.decode(backgroundRTF);
		this.backgroundRTF = backgroundRTF;
	}

	/**
	 * Sets the duration.
	 * @param duration The duration to set
	 */
	public void setDuration(String duration) {
		this.duration = duration;
	}

	/**
	 * Sets the estimatedETC.
	 * @param estimatedETC The estimatedETC to set
	 */
	public void setEstimatedETC(String estimatedETC) {
		this.estimatedETC = estimatedETC;
	}

	/**
	 * Sets the estimatedFinish.
	 * @param estimatedFinish The estimatedFinish to set
	 */
	public void setEstimatedFinish(String estimatedFinish) {
		if(estimatedFinish.trim().equalsIgnoreCase(""))
			return;
		try{
			this.estimatedFinish = Timestamp.valueOf(estimatedFinish);	
		}
		catch(IllegalArgumentException ie){
			estimatedFinish = estimatedFinish + this.trail_hh_mm_ss_ffffffff;
			this.estimatedFinish = Timestamp.valueOf(estimatedFinish);
		}
	}

	/**
	 * Sets the estimatedStart.
	 * @param estimatedStart The estimatedStart to set
	 */
	public void setEstimatedStart(String estimatedStart) {
		if(estimatedStart.trim().equalsIgnoreCase(""))
			return;
		try{
			System.out.println("Estimate Start is : " + estimatedStart);
			this.estimatedStart = Timestamp.valueOf(estimatedStart);
		}
		catch(IllegalArgumentException ie){
			estimatedStart = estimatedStart + this.trail_hh_mm_ss_ffffffff;
			this.estimatedStart = Timestamp.valueOf(estimatedStart);
		}
	}

	/**
	 * Sets the finish.
	 * @param finish The finish to set
	 */
	public void setFinish(String finish) {
		if(finish.trim().equalsIgnoreCase(""))
			return;
		try{
			this.finish = Timestamp.valueOf(finish);
		}
		catch(IllegalArgumentException ie){
			finish = finish + this.trail_hh_mm_ss_ffffffff;
			this.finish = Timestamp.valueOf(finish);
		}
	}

	/**
	 * Sets the objectivesRTF.
	 * @param objectivesRTF The objectivesRTF to set
	 */
	public void setObjectivesRTF(String objectivesRTF) {
		//this.objectivesRTF = bd.decode(objectivesRTF);
		this.objectivesRTF = objectivesRTF;
	}

	/**
	 * Sets the percentComplete.
	 * @param percentComplete The percentComplete to set
	 */
	public void setPercentComplete(String percentComplete) {
		this.percentComplete = percentComplete;
	}

	/**
	 * Sets the revision_history.
	 * @param revision_history The revision_history to set
	 */
	public void setRevision_history(String revision_history) {
		this.revision_history = revision_history;
	}

	/**
	 * Sets the scopeRTF.
	 * @param scopeRTF The scopeRTF to set
	 */
	public void setScopeRTF(String scopeRTF) {
		/*
		 * Communication probs with SystemCorp. 
		 * They are sending values in plain text instead 
		 * of RTFs which was the agreement.
		 * Need to change the code here
		 */
		//ScopeRTF = bd.decode(scopeRTF);
		ScopeRTF = scopeRTF;
	}

	/**
	 * Sets the start.
	 * @param start The start to set
	 */
	public void setStart(String start) {
		if(start.trim().equalsIgnoreCase(""))
			return;
		try{
			this.start = Timestamp.valueOf(start);
		}
		catch(IllegalArgumentException ie){
		
		start = start + this.trail_hh_mm_ss_ffffffff;
		this.start = Timestamp.valueOf(start);
		}
	}

	/**
	 * Sets the state.
	 * @param state The state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * Sets the statusRTF.
	 * @param statusRTF The statusRTF to set
	 */
	public void setStatusRTF(String statusRTF) {
		/*
		 * Communication probs with SystemCorp. 
		 * They are sending values in plain text instead 
		 * of RTFs which was the agreement.
		 * Need to change the code here
		 */
		//this.statusRTF = bd.decode(statusRTF);
		this.statusRTF = statusRTF;
	}

	/**
	 * Sets the targetSolnRTF.
	 * @param targetSolnRTF The targetSolnRTF to set
	 */
	public void setTargetSolnRTF(String targetSolnRTF) {
		/*
		 * Communication probs with SystemCorp. 
		 * They are sending values in plain text instead 
		 * of RTFs which was the agreement.
		 * Need to change the code here
		 */
		//this.targetSolnRTF = bd.decode(targetSolnRTF);
		this.targetSolnRTF = targetSolnRTF;
	}

	/**
	 * Sets the workPercent.
	 * @param workPercent The workPercent to set
	 */
	public void setWorkPercent(String workPercent) {
		this.workPercent = workPercent;
	}

	/**
	 * Returns the projectId.
	 * @return String
	 */
	public String getProjectId() {
		return ProjectId;
	}

	/**
	 * Sets the projectId.
	 * @param projectId The projectId to set
	 */
	public void setProjectId(String projectId) {
		ProjectId = projectId;
	}

	/**
	 * Returns the calendar_id.
	 * @return String
	 */
	public String getCalendar_id() {
		return calendar_id;
	}

	/**
	 * Returns the currency_id.
	 * @return String
	 */
	public String getCurrency_id() {
		return currency_id;
	}

	/**
	 * Returns the element_name.
	 * @return String
	 */
	public String getElement_name() {
		return element_name;
	}

	/**
	 * Returns the published.
	 * @return String
	 */
	public String getPublished() {
		return published;
	}

	/**
	 * Returns the reference_number.
	 * @return String
	 */
	public String getReference_number() {
		return reference_number;
	}

	/**
	 * Sets the calendar_id.
	 * @param calendar_id The calendar_id to set
	 */
	public void setCalendar_id(String calendar_id) {
		this.calendar_id = calendar_id;
	}

	/**
	 * Sets the currency_id.
	 * @param currency_id The currency_id to set
	 */
	public void setCurrency_id(String currency_id) {
		this.currency_id = currency_id;
	}

	/**
	 * Sets the element_name.
	 * @param element_name The element_name to set
	 */
	public void setElement_name(String element_name) {
		this.element_name = element_name;
	}

	/**
	 * Sets the published.
	 * @param published The published to set
	 */
	public void setPublished(String published) {
		this.published = published;
	}

	/**
	 * Sets the reference_number.
	 * @param reference_number The reference_number to set
	 */
	public void setReference_number(String reference_number) {
		this.reference_number = reference_number;
	}

	/**
	 * Returns the calendar_id_Rank.
	 * @return String
	 */
	public String getCalendar_id_Rank() {
		return calendar_id_Rank;
	}

	/**
	 * Returns the currency_id_Rank.
	 * @return String
	 */
	public String getCurrency_id_Rank() {
		return currency_id_Rank;
	}

	/**
	 * Returns the duration_Rank.
	 * @return String
	 */
	public String getDuration_Rank() {
		return duration_Rank;
	}

	/**
	 * Returns the element_name_Rank.
	 * @return String
	 */
	public String getElement_name_Rank() {
		return element_name_Rank;
	}

	/**
	 * Returns the estimatedETC_Rank.
	 * @return String
	 */
	public String getEstimatedETC_Rank() {
		return estimatedETC_Rank;
	}

	/**
	 * Returns the estimatedFinish_Rank.
	 * @return String
	 */
	public String getEstimatedFinish_Rank() {
		return estimatedFinish_Rank;
	}

	/**
	 * Returns the estimatedStart_Rank.
	 * @return String
	 */
	public String getEstimatedStart_Rank() {
		return estimatedStart_Rank;
	}

	/**
	 * Returns the finish_Rank.
	 * @return String
	 */
	public String getFinish_Rank() {
		return finish_Rank;
	}

	/**
	 * Returns the percentComplete_Rank.
	 * @return String
	 */
	public String getPercentComplete_Rank() {
		return percentComplete_Rank;
	}

	/**
	 * Returns the projectId_Rank.
	 * @return String
	 */
	public String getProjectId_Rank() {
		return ProjectId_Rank;
	}

	/**
	 * Returns the published_Rank.
	 * @return String
	 */
	public String getPublished_Rank() {
		return published_Rank;
	}

	/**
	 * Returns the reference_number_Rank.
	 * @return String
	 */
	public String getReference_number_Rank() {
		return reference_number_Rank;
	}

	/**
	 * Returns the revision_history_Rank.
	 * @return String
	 */
	public String getRevision_history_Rank() {
		return revision_history_Rank;
	}

	/**
	 * Returns the start_Rank.
	 * @return String
	 */
	public String getStart_Rank() {
		return start_Rank;
	}

	/**
	 * Returns the state_Rank.
	 * @return String
	 */
	public String getState_Rank() {
		return state_Rank;
	}

	/**
	 * Sets the calendar_id_Rank.
	 * @param calendar_id_Rank The calendar_id_Rank to set
	 */
	public void setCalendar_id_Rank(String calendar_id_Rank) {
		this.calendar_id_Rank = calendar_id_Rank;
	}

	/**
	 * Sets the currency_id_Rank.
	 * @param currency_id_Rank The currency_id_Rank to set
	 */
	public void setCurrency_id_Rank(String currency_id_Rank) {
		this.currency_id_Rank = currency_id_Rank;
	}

	/**
	 * Sets the duration_Rank.
	 * @param duration_Rank The duration_Rank to set
	 */
	public void setDuration_Rank(String duration_Rank) {
		this.duration_Rank = duration_Rank;
	}

	/**
	 * Sets the element_name_Rank.
	 * @param element_name_Rank The element_name_Rank to set
	 */
	public void setElement_name_Rank(String element_name_Rank) {
		this.element_name_Rank = element_name_Rank;
	}

	/**
	 * Sets the estimatedETC_Rank.
	 * @param estimatedETC_Rank The estimatedETC_Rank to set
	 */
	public void setEstimatedETC_Rank(String estimatedETC_Rank) {
		this.estimatedETC_Rank = estimatedETC_Rank;
	}

	/**
	 * Sets the estimatedFinish_Rank.
	 * @param estimatedFinish_Rank The estimatedFinish_Rank to set
	 */
	public void setEstimatedFinish_Rank(String estimatedFinish_Rank) {
		this.estimatedFinish_Rank = estimatedFinish_Rank;
	}

	/**
	 * Sets the estimatedStart_Rank.
	 * @param estimatedStart_Rank The estimatedStart_Rank to set
	 */
	public void setEstimatedStart_Rank(String estimatedStart_Rank) {
		this.estimatedStart_Rank = estimatedStart_Rank;
	}

	/**
	 * Sets the finish_Rank.
	 * @param finish_Rank The finish_Rank to set
	 */
	public void setFinish_Rank(String finish_Rank) {
		this.finish_Rank = finish_Rank;
	}

	/**
	 * Sets the percentComplete_Rank.
	 * @param percentComplete_Rank The percentComplete_Rank to set
	 */
	public void setPercentComplete_Rank(String percentComplete_Rank) {
		this.percentComplete_Rank = percentComplete_Rank;
	}

	/**
	 * Sets the projectId_Rank.
	 * @param projectId_Rank The projectId_Rank to set
	 */
	public void setProjectId_Rank(String projectId_Rank) {
		ProjectId_Rank = projectId_Rank;
	}

	/**
	 * Sets the published_Rank.
	 * @param published_Rank The published_Rank to set
	 */
	public void setPublished_Rank(String published_Rank) {
		this.published_Rank = published_Rank;
	}

	/**
	 * Sets the reference_number_Rank.
	 * @param reference_number_Rank The reference_number_Rank to set
	 */
	public void setReference_number_Rank(String reference_number_Rank) {
		this.reference_number_Rank = reference_number_Rank;
	}

	/**
	 * Sets the revision_history_Rank.
	 * @param revision_history_Rank The revision_history_Rank to set
	 */
	public void setRevision_history_Rank(String revision_history_Rank) {
		this.revision_history_Rank = revision_history_Rank;
	}

	/**
	 * Sets the start_Rank.
	 * @param start_Rank The start_Rank to set
	 */
	public void setStart_Rank(String start_Rank) {
		this.start_Rank = start_Rank;
	}

	/**
	 * Sets the state_Rank.
	 * @param state_Rank The state_Rank to set
	 */
	public void setState_Rank(String state_Rank) {
		this.state_Rank = state_Rank;
	}

	/**
	 * Returns the workPercent_Rank.
	 * @return String
	 */
	public String getWorkPercent_Rank() {
		return workPercent_Rank;
	}

	/**
	 * Sets the workPercent_Rank.
	 * @param workPercent_Rank The workPercent_Rank to set
	 */
	public void setWorkPercent_Rank(String workPercent_Rank) {
		this.workPercent_Rank = workPercent_Rank;
	}

	/**
	 * Returns the resources.
	 * @return Resources
	 */
	public int RetrievePopulationOfResources() {
		if(vResources == null)
			return -1;
		return vResources.size();
	}
	public int RetrievePopulationOfWBSElements() {
		if(vWBS == null)
			return -1;
		return vWBS.size();
	}
	public int RetrievePopulationOfexceptions() {
		if(vexceptions == null)
			return -1;
		return vexceptions.size();
	}
	public int RetrievePopulationOfRTFs() {
		if(vRTF == null)
			return -1;
		return vRTF.size();
	}
	

	public Resource retrieveResource(int index) throws IndexOutOfBoundsException{
	Resource res = null;
	if(this.vResources != null &&
		!this.vResources.isEmpty()){
			if(index >= vResources.size()){
				throw new IndexOutOfBoundsException("The index is beyond the limits of the vector: vResources");
			}
	res = (Resource)vResources.get(index);
	}
	return res;
		
}
public WBSElement retrieveWBSElement(int index) throws IndexOutOfBoundsException{
	WBSElement ele = null;
	if(this.vWBS != null &&
		!this.vWBS.isEmpty()){
			if(index >= vWBS.size()){
				throw new IndexOutOfBoundsException("The index is beyond the limits of the vector: vResources");
			}
	ele = (WBSElement)vWBS.get(index);
	}
	return ele;
		
}
public exception retrieve_exception(int index) throws IndexOutOfBoundsException{
	exception exe = null;
	if(this.vexceptions != null &&
		!this.vexceptions.isEmpty()){
			if(index >= vexceptions.size()){
				throw new IndexOutOfBoundsException("The index is beyond the limits of the vector: vexceptions");
			}
	exe = (exception)vexceptions.get(index);
	}
	return exe;
}

public RTFData retrieveRTF(int index) throws IndexOutOfBoundsException{
	RTFData ele = null;
	if(this.vRTF != null &&
		!this.vRTF.isEmpty()){
			
			if(index >= vRTF.size()){
				throw new IndexOutOfBoundsException("The index is beyond the limits of the vector: vRTF");
			}
	ele = (RTFData)vRTF.get(index);
	}
	return ele;
		
}
public Doc retrieveDoc(int index) throws IndexOutOfBoundsException{
	Doc doc = null;
	if(this.vDocs != null &&
		!this.vDocs.isEmpty()){
			if(index >= vDocs.size()){
				throw new IndexOutOfBoundsException("The index is beyond the limits of the vector: vDocs");
			}
	doc = (Doc)vDocs.get(index);
	}
	return doc;
		
}

	public void populateVDocs(Doc doc) {
		if(this.vDocs == null){
			vDocs = new Vector();
		}
		vDocs.add(doc);
	}
	
public int RetrievePopulationOfDocs() {
		if(vDocs == null)
			return -1;
		return vDocs.size();
	}
	
public Project retrieveProject(int index) throws IndexOutOfBoundsException{
	Project pro = null;
	if(this.vProj != null &&
		!this.vProj.isEmpty()){
			if(index >= vProj.size()){
				throw new IndexOutOfBoundsException("The index is beyond the limits of the vector: vProj");
			}
	pro = (Project)vProj.get(index);
	}
	return pro;
		
}

	public void populateVProject(Project pro) {
		if(this.vProj == null){
			vProj = new Vector();
		}
		vProj.add(pro);
	}
	
public int RetrievePopulationOfProject() {
		if(vProj == null)
			return -1;
		return vProj.size();
	}
	/**
	 * Returns the scorecard.
	 * @return ScoreCard
	 */
	public ScoreCard getScorecard() {
		return scorecard;
	}

	/**
	 * Sets the resources.
	 * @param resources The resources to set
	 */
	public void populateVResources(Resource resource) {
		if(this.vResources == null){
			vResources = new Vector();
		}
		vResources.add(resource);
	}

	public void populatevWBS(WBSElement ele) {
		if(this.vWBS == null){
			vWBS = new Vector();
		}
		vWBS.add(ele);
	}
	
	public void populatevexception(exception exe) {
		if(this.vexceptions == null){
			vexceptions = new Vector();
		}
		vexceptions.add(exe);
	}
	public void populatevRTF(RTFData rtf) {
		if(this.vRTF == null){
			vRTF = new Vector();
		}
		vRTF.add(rtf);
	}
	/**
	 * Sets the scorecard.
	 * @param scorecard The scorecard to set
	 */
	public void setScorecard(ScoreCard scorecard) {
		this.scorecard = scorecard;
	}

	public String toString(){
		String str = 	" Project Data : \n" +  
						" [Project_Id : " + ProjectId +  " ]" + "\n" + 
						" [ProjectId_Rank : " + ProjectId_Rank + " ]" + "\n" + 
						" [element_name: " + element_name + " ]" + "\n" + 
						" [element_name_Rank: " + element_name_Rank + " ]" + "\n" + 
						" [reference_number: " + reference_number + " ]" + "\n" + 
						" [reference_number_Rank: " + reference_number_Rank + " ]" + "\n" + 
						" [calendar_id: " + calendar_id + " ]" + "\n" + 
						" [calendar_id_Rank: " + calendar_id_Rank + " ]" +  "\n" + 
						" [currency_id: " + currency_id + " ]" + "\n" + 
						" [currency_id_Rank: " + currency_id_Rank + " ]" + "\n" + 
						" [published: " + published + " ]" + "\n" + 
						" [published_Rank: " + published_Rank + " ]" + "\n" + 
						" [estimatedStart: " + estimatedStart + " ]" +  "\n" + 
						" [estimatedStart_Rank: " + estimatedStart_Rank + " ]" + "\n" + 
						" [estimatedFinish: " + estimatedFinish + " ]" + "\n" + 
						" [estimatedFinish_Rank: " + estimatedFinish_Rank + " ]" + "\n" + 
						" [state: " + state + " ]" + "\n" + 
						" [state_Rank: " + state_Rank + " ]" + "\n" + 
						" [revision_history: " + revision_history + " ]" + "\n" + 
						" [revision_history_Rank: " + revision_history_Rank + " ]"  +  "\n" + 
						" [start: " + start + " ]" + "\n" + 
						" [start_Rank : " + start_Rank + " ]" + "\n" + 
						" [finish: " + finish + " ]" + "\n" + 
						" [finish_Rank: " + finish_Rank + " ]" + "\n" + 
						" [duration: " + duration + " ]" +  "\n" + 
						" [duration_Rank: " + duration_Rank + " ]" + "\n" + 
						" [workPercent: " + workPercent + " ]" +  "\n" + 
						" [workPercent_Rank: " + workPercent_Rank + " ]" +  "\n" + 
						" [estimatedETC: " + estimatedETC + " ]" + "\n" + 
						" [estimatedETC_Rank: "	+ estimatedETC_Rank + " ]" + "\n" + 
						" [percentComplete: " + percentComplete + " ]" + "\n" + 
						" [percentComplete_Rank: " + percentComplete_Rank + " ]" + "\n" + 
						" [ScopeRTF: " + ScopeRTF + " ]" + "\n" + 
						" [objectivesRTF: " + objectivesRTF + " ]" + "\n" + 
						" [backgroundRTF: " + backgroundRTF + " ]" + "\n" + 
						" [statusRTF: " + statusRTF + " ]" + "\n" + 
						" [targetSolnRTF: " + targetSolnRTF;
					
		str += "\n" + "RTFVector : " + "\n";
						System.out.println(RetrievePopulationOfRTFs());
		for(int i = 0; i < RetrievePopulationOfRTFs() ; i++){
			str += 		"[ Rank  : " + retrieveRTF(i).getRank()	+ " ]" +
					 	"[ Name  : " + retrieveRTF(i).getName()	+ " ]" +
					 	"[ Value : " + retrieveRTF(i).getValue() + " ]" + "\n";
			
		}
			return str;
	}
	/**
	 * Returns the vResources.
	 * @return Vector
	 */
	public Vector getVResources() {
		return vResources;
	}

	/**
	 * Returns the vWBS.
	 * @return Vector
	 */
	public Vector getVWBS() {
		return vWBS;
	}

/**
 * Returns the type.
 * @return String
 */
public String getType() {
	return Type;
}

/**
 * Sets the type.
 * @param type The type to set
 */
public void setType(String type) {
	Type = type;
}

	/**
	 * Returns the vRTF.
	 * @return Vector
	 */
	public Vector getVRTF() {
		return vRTF;
	}

	/**
	 * Returns the vProj.
	 * @return Vector
	 */
	public Vector getVProj() {
		return vProj;
	}

	/**
	 * Returns the vDocs.
	 * @return Vector
	 */
	public Vector getVDocs() {
		return vDocs;
	}

	/**
	 * Returns the vexceptions.
	 * @return Vector
	 */
	public Vector getVexceptions() {
		return vexceptions;
	}

/**
 * Returns the isReportable.
 * @return boolean
 */
public boolean isReportable() {
	return isReportable;
}

/**
 * Sets the isReportable.
 * @param isReportable The isReportable to set
 */
public void setIsReportable(boolean isReportable) {
	this.isReportable = isReportable;
}


	/**
	 * Returns the fD.
	 * @return String
	 */
	public String getFD() {
		return FD;
	}

	/**
	 * Returns the sD.
	 * @return String
	 */
	public String getSD() {
		return SD;
	}

	/**
	 * Sets the fD.
	 * @param fD The fD to set
	 */
	public void setFD(String fD) {
		FD = fD;
	}

	/**
	 * Sets the sD.
	 * @param sD The sD to set
	 */
	public void setSD(String sD) {
		SD = sD;
	}

	/**
	 * Returns the forecastFinish.
	 * @return Timestamp
	 */
	public Timestamp getForecastFinish() {
		return forecastFinish;
	}

	/**
	 * Returns the forecastStart.
	 * @return Timestamp
	 */
	public Timestamp getForecastStart() {
		return forecastStart;
	}

	/**
	 * Returns the proposedFinish.
	 * @return Timestamp
	 */
	public Timestamp getProposedFinish() {
		return proposedFinish;
	}

	/**
	 * Returns the proposedStart.
	 * @return Timestamp
	 */
	public Timestamp getProposedStart() {
		return proposedStart;
	}

	/**
	 * Returns the scheduledFinish.
	 * @return Timestamp
	 */
	public Timestamp getScheduledFinish() {
		return scheduledFinish;
	}

	/**
	 * Returns the scheduledStart.
	 * @return Timestamp
	 */
	public Timestamp getScheduledStart() {
		return scheduledStart;
	}

	/**
	 * Sets the forecastFinish.
	 * @param forecastFinish The forecastFinish to set
	 */
	public void setForecastFinish(String forecastFinish) {
		if(forecastFinish.trim().equalsIgnoreCase(""))
			return;
		try{
			this.forecastFinish = Timestamp.valueOf(forecastFinish);	
		}
		catch(IllegalArgumentException ie){
			forecastFinish = forecastFinish + this.trail_hh_mm_ss_ffffffff;
			this.forecastFinish = Timestamp.valueOf(forecastFinish);
		}
	}

	/**
	 * Sets the forecastStart.
	 * @param forecastStart The forecastStart to set
	 */
	public void setForecastStart(String forecastStart) {
		if(forecastStart.trim().equalsIgnoreCase(""))
			return;
		try{
			this.forecastStart = Timestamp.valueOf(forecastStart);	
		}
		catch(IllegalArgumentException ie){
			forecastStart = forecastStart + this.trail_hh_mm_ss_ffffffff;
			this.forecastStart = Timestamp.valueOf(forecastStart);
		}
	}

	/**
	 * Sets the proposedFinish.
	 * @param proposedFinish The proposedFinish to set
	 */
	public void setProposedFinish(String proposedFinish) {
		if(proposedFinish.trim().equalsIgnoreCase(""))
			return;
		try{
			this.proposedFinish = Timestamp.valueOf(proposedFinish);	
		}
		catch(IllegalArgumentException ie){
			proposedFinish = proposedFinish + this.trail_hh_mm_ss_ffffffff;
			this.proposedFinish = Timestamp.valueOf(proposedFinish);
		}
	}

	/**
	 * Sets the proposedStart.
	 * @param proposedStart The proposedStart to set
	 */
	public void setProposedStart(String proposedStart) {
		if(proposedStart.trim().equalsIgnoreCase(""))
			return;
		try{
			this.proposedStart = Timestamp.valueOf(proposedStart);	
		}
		catch(IllegalArgumentException ie){
			proposedStart = proposedStart + this.trail_hh_mm_ss_ffffffff;
			this.proposedStart = Timestamp.valueOf(proposedStart);
		}
	}

	/**
	 * Sets the scheduledFinish.
	 * @param scheduledFinish The scheduledFinish to set
	 */
	public void setScheduledFinish(String scheduledFinish) {
		if(scheduledFinish.trim().equalsIgnoreCase(""))
			return;
		try{
			this.scheduledFinish = Timestamp.valueOf(scheduledFinish);	
		}
		catch(IllegalArgumentException ie){
			scheduledFinish = scheduledFinish + this.trail_hh_mm_ss_ffffffff;
			this.scheduledFinish = Timestamp.valueOf(scheduledFinish);
		}
	}

	/**
	 * Sets the scheduledStart.
	 * @param scheduledStart The scheduledStart to set
	 */
	public void setScheduledStart(String scheduledStart) {
		if(scheduledStart.trim().equalsIgnoreCase(""))
			return;
		try{
			this.scheduledStart = Timestamp.valueOf(scheduledStart);	
		}
		catch(IllegalArgumentException ie){
			scheduledStart = scheduledStart + this.trail_hh_mm_ss_ffffffff;
			this.scheduledStart = Timestamp.valueOf(scheduledStart);
		}
	}
	
		/**
	 * Sets the baseline1Finish.
	 * @param baseline1Finish The baseline1Finish to set
	 */
	public void setBaseline1Finish(String baseline1Finish) {
		if(baseline1Finish.trim().equalsIgnoreCase(""))
			return;
		try{
			this.baseline1Finish = Timestamp.valueOf(baseline1Finish);	
		}
		catch(IllegalArgumentException ie){
			baseline1Finish = baseline1Finish + this.trail_hh_mm_ss_ffffffff;
			this.baseline1Finish = Timestamp.valueOf(baseline1Finish);
		}
	}

		/**
	 * Sets the baseline1Finish.
	 * @param baseline1Finish The baseline1Finish to set
	 */
	public void setBaseline2Finish(String baseline2Finish) {
		if(baseline2Finish.trim().equalsIgnoreCase(""))
			return;
		try{
			this.baseline2Finish = Timestamp.valueOf(baseline2Finish);	
		}
		catch(IllegalArgumentException ie){
			baseline2Finish = baseline2Finish + this.trail_hh_mm_ss_ffffffff;
			this.baseline1Finish = Timestamp.valueOf(baseline2Finish);
		}
	}
			/**
	 * Sets the baseline1Finish.
	 * @param baseline1Finish The baseline1Finish to set
	 */
	public void setBaseline3Finish(String baseline3Finish) {
		if(baseline3Finish.trim().equalsIgnoreCase(""))
			return;
		try{
			this.baseline3Finish = Timestamp.valueOf(baseline3Finish);	
		}
		catch(IllegalArgumentException ie){
			baseline3Finish = baseline3Finish + this.trail_hh_mm_ss_ffffffff;
			this.baseline3Finish = Timestamp.valueOf(baseline3Finish);
		}
	}
				/**
	 * Sets the actualFinish.
	 * @param actualFinish The baseline1Finish to set
	 */
	public void setActualFinish(String actualFinish) {
		if(actualFinish.trim().equalsIgnoreCase(""))
			return;
		try{
			this.actualFinish = Timestamp.valueOf(actualFinish);	
		}
		catch(IllegalArgumentException ie){
			actualFinish = actualFinish + this.trail_hh_mm_ss_ffffffff;
			this.actualFinish = Timestamp.valueOf(actualFinish);
		}
	}
	/**
	 * Returns the baseline1Finish.
	 * @return Timestamp
	 */
	public Timestamp getBaseline1Finish() {
		return baseline1Finish;
	}

	/**
	 * Returns the baseline2Finish.
	 * @return Timestamp
	 */
	public Timestamp getBaseline2Finish() {
		return baseline2Finish;
	}

	/**
	 * Returns the baseline3Finish.
	 * @return Timestamp
	 */
	public Timestamp getBaseline3Finish() {
		return baseline3Finish;
	}

	/**
	 * Returns the actualFinish.
	 * @return Timestamp
	 */
	public Timestamp getActualFinish() {
		return actualFinish;
	}

	/**
	 * @return
	 */
	public static String getCLASS_VERSION() {
		return CLASS_VERSION;
	}
	public void cleanUpJob(){
		
		this.vDocs = null;
		this.vexceptions = null;
		this.vProj = null;
		this.vResources = null;
		this.vRTF = null;
		this.vWBS = null;
	}
}
