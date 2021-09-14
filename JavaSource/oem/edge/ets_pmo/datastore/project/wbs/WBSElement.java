package oem.edge.ets_pmo.datastore.project.wbs;


import oem.edge.ets_pmo.datastore.resource.*;
import oem.edge.ets_pmo.datastore.document.*;
import oem.edge.ets_pmo.datastore.util.*;
import java.util.Vector;
import java.sql.Timestamp;

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
public class WBSElement {
private static String CLASS_VERSION = "4.5.1";	
private String type;

private String id;
private String id_Rank =null;

/* IsReportable determines if this 
 * WBSElement should be reported to 
 * external customers. This gets stored in
 * the database in ets.ets_pmo_main in isReportable
 * field
 */
private boolean isReportable=false;

private String work_percent;
private String work_percent_Rank  =null;

private String Element_name;
private String Element_name_Rank = null;

private String priority;
private String priority_Rank	= null;

private String reference_number;
private String reference_number_Rank = null;

private String published;
private String published_Rank =  null;

private String state;
private String state_Rank = null;

private String revision_history;
private String revision_history_Rank = null;

private Timestamp start;
private String start_Rank = null;

private Timestamp finish;
private String finish_Rank = null;

private String duration;
private String duration_Rank = null;

private String work_percent2;
private String work_percent2_Rank = null;

private String ETC;
private String ETC_Rank = null;

private String percent_Complete;
private String percent_Complete_Rank = null;

private String actual_Effort;
private String actual_Effort_Rank = null;

private String remaining_Effort;
private String remaining_Effort_Rank = null;

private String constraintType;
private String constraintType_Rank = null;

private String constraintDate;
private String constraintDate_Rank = null;

private String SD = null;
private String FD = null;
	
private String RTF1;
private String RTF2;
private String RTF3;
private String RTF4;
private String RTF5;
private Vector vResources;
private Vector vDocs;
private Vector vWBS;
private Vector vRTF;

private Timestamp estimatedStart = null;
private Timestamp estimatedFinish = null;
private Timestamp scheduledStart = null;
private Timestamp scheduledFinish = null;
private Timestamp proposedStart = null;
private Timestamp proposedFinish = null;
private Timestamp forecastStart = null;
private Timestamp forecastFinish = null;
private Timestamp baseline1Finish = null;
private Timestamp baseline2Finish = null;
private Timestamp baseline3Finish = null;
private Timestamp actualFinish	   = null;
	
private String trail_hh_mm_ss_ffffffff = " 00:00:00.00000000";

public int RetrievePopulationOfWBSElements() {
		if(vWBS == null)
			return -1;
		return vWBS.size();
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

public void populatevWBS(WBSElement ele) {
		if(this.vWBS == null){
			vWBS = new Vector();
		}
		vWBS.add(ele);
	}
	
	public void populatevRTF(RTFData rtf) {
		if(this.vRTF == null){
			vRTF = new Vector();
		}
		vRTF.add(rtf);
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

	public int RetrievePopulationOfRTFs() {
		if(vRTF == null)
			return -1;
		return vRTF.size();
	}
/**
 * Returns the actual_Effort.
 * @return String
 */
public String getActual_Effort() {
	return actual_Effort;
}

/**
 * Returns the constraintDate.
 * @return String
 */
public String getConstraintDate() {
	return constraintDate;
}

/**
 * Returns the constraintType.
 * @return String
 */
public String getConstraintType() {
	return constraintType;
}

/**
 * Returns the duration.
 * @return String
 */
public String getDuration() {
	return duration;
}

/**
 * Returns the element_name.
 * @return String
 */
public String getElement_name() {
	return Element_name;
}

/**
 * Returns the eTC.
 * @return String
 */
public String getETC() {
	return ETC;
}

/**
 * Returns the finish_dt.
 * @return String
 */
public Timestamp getFinish() {
	return finish;
}

/**
 * Returns the percent_Complete.
 * @return String
 */
public String getPercent_Complete() {
	return percent_Complete;
}

/**
 * Returns the priority.
 * @return String
 */
public String getPriority() {
	return priority;
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
 * Returns the remaining_Efort.
 * @return String
 */
public String getRemaining_Effort() {
	return remaining_Effort;
}

/**
 * Returns the revision_history.
 * @return String
 */
public String getRevision_history() {
	return revision_history;
}

/**
 * Returns the rTF1.
 * @return String
 */
public String getRTF1() {
	return RTF1;
}

/**
 * Returns the rTF2.
 * @return String
 */
public String getRTF2() {
	return RTF2;
}

/**
 * Returns the rTF3.
 * @return String
 */
public String getRTF3() {
	return RTF3;
}

/**
 * Returns the rTF4.
 * @return String
 */
public String getRTF4() {
	return RTF4;
}

/**
 * Returns the rTF5.
 * @return String
 */
public String getRTF5() {
	return RTF5;
}

/**
 * Returns the stage_id.
 * @return String
 */
public String getState() {
	return state;
}

/**
 * Returns the start_dt.
 * @return String
 */
public Timestamp getStart() {
	return start;
}

/**
 * Returns the work_percent.
 * @return String
 */
public String getWork_percent() {
	return work_percent;
}

/**
 * Sets the actual_Effort.
 * @param actual_Effort The actual_Effort to set
 */
public void setActual_Effort(String actual_Effort) {
	this.actual_Effort = actual_Effort;
}

/**
 * Sets the constraintDate.
 * @param constraintDate The constraintDate to set
 */
public void setConstraintDate(String constraintDate) {
	this.constraintDate = constraintDate;
}

/**
 * Sets the constraintType.
 * @param constraintType The constraintType to set
 */
public void setConstraintType(String constraintType) {
	this.constraintType = constraintType;
}

/**
 * Sets the duration.
 * @param duration The duration to set
 */
public void setDuration(String duration) {
	this.duration = duration;
}

/**
 * Sets the element_name.
 * @param element_name The element_name to set
 */
public void setElement_name(String element_name) {
	Element_name = element_name;
}

/**
 * Sets the eTC.
 * @param eTC The eTC to set
 */
public void setETC(String eTC) {
	ETC = eTC;
}

/**
 * Sets the finish_dt.
 * @param finish_dt The finish_dt to set
 */
public void setFinish(String Finish) {
	if(Finish.trim().equalsIgnoreCase(""))
			return;
		try{
			this.finish = Timestamp.valueOf(Finish);	
		}
		catch(IllegalArgumentException ie){
			Finish = Finish + this.trail_hh_mm_ss_ffffffff;
			this.finish = Timestamp.valueOf(Finish);
		}
	
}

/**
 * Sets the percent_Complete.
 * @param percent_Complete The percent_Complete to set
 */
public void setPercent_Complete(String percent_Complete) {
	this.percent_Complete = percent_Complete;
}

/**
 * Sets the priority.
 * @param priority The priority to set
 */
public void setPriority(String priority) {
	this.priority = priority;
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
 * Sets the remaining_Efort.
 * @param remaining_Efort The remaining_Efort to set
 */
public void setRemaining_Effort(String remaining_Effort) {
	this.remaining_Effort = remaining_Effort;
}

/**
 * Sets the revision_history.
 * @param revision_history The revision_history to set
 */
public void setRevision_history(String revision_history) {
	this.revision_history = revision_history;
}

/**
 * Sets the rTF1.
 * @param rTF1 The rTF1 to set
 */
public void setRTF1(String rTF1) {
	RTF1 = rTF1;
}

/**
 * Sets the rTF2.
 * @param rTF2 The rTF2 to set
 */
public void setRTF2(String rTF2) {
	RTF2 = rTF2;
}

/**
 * Sets the rTF3.
 * @param rTF3 The rTF3 to set
 */
public void setRTF3(String rTF3) {
	RTF3 = rTF3;
}

/**
 * Sets the rTF4.
 * @param rTF4 The rTF4 to set
 */
public void setRTF4(String rTF4) {
	RTF4 = rTF4;
}

/**
 * Sets the rTF5.
 * @param rTF5 The rTF5 to set
 */
public void setRTF5(String rTF5) {
	RTF5 = rTF5;
}

/**
 * Sets the stage_id.
 * @param stage_id The stage_id to set
 */
public void setState(String stage_id) {
	this.state = stage_id;
}

/**
 * Sets the start_dt.
 * @param start_dt The start_dt to set
 */
public void setStart(String start_dt) {
	if(start_dt.trim().equalsIgnoreCase(""))
			return;
		try{
			this.start = Timestamp.valueOf(start_dt);	
		}
		catch(IllegalArgumentException ie){
			start_dt = start_dt + this.trail_hh_mm_ss_ffffffff;
			this.start = Timestamp.valueOf(start_dt);
		}
}

/**
 * Sets the work_percent.
 * @param work_percent The work_percent to set
 */
public void setWork_percent(String work_percent) {
	this.work_percent = work_percent;
}

/**
 * Returns the work_percent2.
 * @return String
 */
public String getWork_percent2() {
	return work_percent2;
}

/**
 * Sets the work_percent2.
 * @param work_percent2 The work_percent2 to set
 */
public void setWork_percent2(String work_percent2) {
	this.work_percent2 = work_percent2;
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


/**
 * Returns the id.
 * @return String
 */
public String getId() {
	return id;
}

/**
 * Sets the id.
 * @param id The id to set
 */
public void setId(String id) {
	this.id = id;
}

/**
 * Returns the actual_Effort_Rank.
 * @return String
 */
public String getActual_Effort_Rank() {
	return actual_Effort_Rank;
}

/**
 * Returns the constraintDate_Rank.
 * @return String
 */
public String getConstraintDate_Rank() {
	return constraintDate_Rank;
}

/**
 * Returns the constraintType_Rank.
 * @return String
 */
public String getConstraintType_Rank() {
	return constraintType_Rank;
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
	return Element_name_Rank;
}

/**
 * Returns the eTC_Rank.
 * @return String
 */
public String getETC_Rank() {
	return ETC_Rank;
}

/**
 * Returns the id_Rank.
 * @return String
 */
public String getId_Rank() {
	return id_Rank;
}

/**
 * Returns the percent_Complete_Rank.
 * @return String
 */
public String getPercent_Complete_Rank() {
	return percent_Complete_Rank;
}

/**
 * Returns the priority_Rank.
 * @return String
 */
public String getPriority_Rank() {
	return priority_Rank;
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
 * Returns the remaining_Effort_Rank.
 * @return String
 */
public String getRemaining_Effort_Rank() {
	return remaining_Effort_Rank;
}

/**
 * Returns the revision_history_Rank.
 * @return String
 */
public String getRevision_history_Rank() {
	return revision_history_Rank;
}

/**
 * Returns the start_dt_Rank.
 * @return String
 */
public String getStart_dt_Rank() {
	return start_Rank;
}

/**
 * Returns the work_percent_Rank.
 * @return String
 */
public String getWork_percent_Rank() {
	return work_percent_Rank;
}

/**
 * Returns the work_percent2_Rank.
 * @return String
 */
public String getWork_percent2_Rank() {
	return work_percent2_Rank;
}

/**
 * Sets the actual_Effort_Rank.
 * @param actual_Effort_Rank The actual_Effort_Rank to set
 */
public void setActual_Effort_Rank(String actual_Effort_Rank) {
	this.actual_Effort_Rank = actual_Effort_Rank;
}

/**
 * Sets the constraintDate_Rank.
 * @param constraintDate_Rank The constraintDate_Rank to set
 */
public void setConstraintDate_Rank(String constraintDate_Rank) {
	this.constraintDate_Rank = constraintDate_Rank;
}

/**
 * Sets the constraintType_Rank.
 * @param constraintType_Rank The constraintType_Rank to set
 */
public void setConstraintType_Rank(String constraintType_Rank) {
	this.constraintType_Rank = constraintType_Rank;
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
	Element_name_Rank = element_name_Rank;
}

/**
 * Sets the eTC_Rank.
 * @param eTC_Rank The eTC_Rank to set
 */
public void setETC_Rank(String eTC_Rank) {
	ETC_Rank = eTC_Rank;
}

/**
 * Sets the id_Rank.
 * @param id_Rank The id_Rank to set
 */
public void setId_Rank(String id_Rank) {
	this.id_Rank = id_Rank;
}

/**
 * Sets the percent_Complete_Rank.
 * @param percent_Complete_Rank The percent_Complete_Rank to set
 */
public void setPercent_Complete_Rank(String percent_Complete_Rank) {
	this.percent_Complete_Rank = percent_Complete_Rank;
}

/**
 * Sets the priority_Rank.
 * @param priority_Rank The priority_Rank to set
 */
public void setPriority_Rank(String priority_Rank) {
	this.priority_Rank = priority_Rank;
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
 * Sets the remaining_Effort_Rank.
 * @param remaining_Effort_Rank The remaining_Effort_Rank to set
 */
public void setRemaining_Effort_Rank(String remaining_Effort_Rank) {
	this.remaining_Effort_Rank = remaining_Effort_Rank;
}

/**
 * Sets the revision_history_Rank.
 * @param revision_history_Rank The revision_history_Rank to set
 */
public void setRevision_history_Rank(String revision_history_Rank) {
	this.revision_history_Rank = revision_history_Rank;
}

/**
 * Sets the start_dt_Rank.
 * @param start_dt_Rank The start_dt_Rank to set
 */
public void setStart_dt_Rank(String start_dt_Rank) {
	this.start_Rank = start_dt_Rank;
}

/**
 * Sets the work_percent_Rank.
 * @param work_percent_Rank The work_percent_Rank to set
 */
public void setWork_percent_Rank(String work_percent_Rank) {
	this.work_percent_Rank = work_percent_Rank;
}

/**
 * Sets the work_percent2_Rank.
 * @param work_percent2_Rank The work_percent2_Rank to set
 */
public void setWork_percent2_Rank(String work_percent2_Rank) {
	this.work_percent2_Rank = work_percent2_Rank;
}

/**
 * Returns the finish_Rank.
 * @return String
 */
public String getFinish_Rank() {
	return finish_Rank;
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
 * Sets the finish_Rank.
 * @param finish_Rank The finish_Rank to set
 */
public void setFinish_Rank(String finish_Rank) {
	this.finish_Rank = finish_Rank;
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
 * Returns the type.
 * @return String
 */
public String getType() {
	return type;
}

/**
 * Sets the type.
 * @param type The type to set
 */
public void setType(String type) {
	this.type = type;
}
	public String toString(){
		String str = 	" Project Data : \n" +  
						" [Id : " + this.id +  " ]" + "\n" + 
						" [Id_Rank : " + this.id_Rank + " ]" + "\n" + 
						" [element_name: " + this.Element_name + " ]" + "\n" + 
						" [element_name_Rank: " + this.Element_name_Rank + " ]" + "\n" + 
						" [reference_number: " + reference_number + " ]" + "\n" + 
						" [reference_number_Rank: " + reference_number_Rank + " ]" + "\n" + 
						" [published: " + published + " ]" + "\n" + 
						" [published_Rank: " + published_Rank + " ]" + "\n" + 
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
						" [workPercent: " + this.work_percent2 + " ]" +  "\n" + 
						" [workPercent_Rank: " + this.work_percent2_Rank + " ]" +  "\n" + 
						" [estimatedETC: " + this.ETC + " ]" + "\n" + 
						" [estimatedETC_Rank: "	+ this.ETC_Rank + " ]" + "\n" + 
						" [percentComplete: " + this.percent_Complete + " ]" + "\n" + 
						" [percentComplete_Rank: " + this.percent_Complete_Rank + " ]" + "\n" + 
						" [RTF1: " + this.RTF1 + " ]" + "\n" + 
						" [RTF2: " + this.RTF2 + " ]" + "\n" + 
						" [RTF3: " + this.RTF3 + " ]" + "\n" + 
						" [RTF4: " + this.RTF4 + " ]" + "\n" + 
						" [RTF5: " + this.RTF5;
					
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
 * Returns the vRTF.
 * @return Vector
 */
public Vector getVRTF() {
	return vRTF;
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
 * Returns the vDocs.
 * @return Vector
 */
public Vector getVDocs() {
	return vDocs;
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
 * Returns the estimatedStart.
 * @return Timestamp
 */
public Timestamp getEstimatedStart() {
	return estimatedStart;
}

/**
 * Sets the estimatedStart.
 * @param estimatedStart The estimatedStart to set
 */
public void setEstimatedStart(String estimatedStart) {
	if(estimatedStart.trim().equalsIgnoreCase(""))
			return;
		try{
			this.estimatedStart = Timestamp.valueOf(estimatedStart);	
		}
		catch(IllegalArgumentException ie){
			estimatedStart = estimatedStart + this.trail_hh_mm_ss_ffffffff;
			this.estimatedStart = Timestamp.valueOf(estimatedStart);
		}
}

/**
 * Returns the estimatedFinish.
 * @return Timestamp
 */
public Timestamp getEstimatedFinish() {
	return estimatedFinish;
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

}
