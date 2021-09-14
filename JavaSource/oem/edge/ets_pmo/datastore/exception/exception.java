package oem.edge.ets_pmo.datastore.exception;

import java.util.Vector;
import java.sql.Timestamp;

import oem.edge.ets_pmo.common.ETSPMOGlobalInitialize;
import oem.edge.ets_pmo.datastore.document.*;
import oem.edge.ets_pmo.datastore.resource.Resource;
import oem.edge.ets_pmo.datastore.util.RTFData;
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
public class exception {
private static String CLASS_VERSION = "4.5.1";	
private String id;
private String type;
private String element_Name;
private int reference_Number;
private String priority;
private String stage_id;
private String proposed_By;
private Timestamp proposed_DateTime;

private String published;
private String revision_history;
private String duration;
private String ETC;
private String percentComplete;
private Timestamp start;

private String RTF1;
private String RTF7;
private String RTF9;
private Vector vDocs;
private Vector vResources;
private String trail_hh_mm_ss_ffffffff = " 00:00:00.00000000";

private String ets_ID = null;
private char info_Src; // , 'e' - new from ets and not sent to pmo, 'f' - new from ets and
								// sent to pmo, 'p' - new from pmo, 'q' - from pmo and already in the tables and not new.
private Vector vRTFs;
private Vector vexceptions;
/**
 * Returns the element_Name.
 * @return String
 */
public String getElement_Name() {
	return element_Name;
}
public String GenerateandRetrieveETSID(){
	ets_ID =  ETSPMOGlobalInitialize.getCR_CREATEDINPMO_ETSID() + System.currentTimeMillis();
	return ets_ID;
}

/**
 * Returns the id.
 * @return String
 */
public String getId() {
	return id;
}

/**
 * Returns the priority.
 * @return String
 */
public String getPriority() {
	return priority;
}

/**
 * Returns the proposed_By.
 * @return String
 */
public String getProposed_By() {
	return proposed_By;
}

/**
 * Returns the proposed_DateTime.
 * @return String
 */
public Timestamp getProposed_DateTime() {
	return proposed_DateTime;
}

/**
 * Returns the reference_Number.
 * @return String
 */
public int getReference_Number() {
	return reference_Number;
}

/**
 * Returns the rTF1.
 * @return String
 */
public String getRTF1() {
	return RTF1;
}

/**
 * Returns the rTF7.
 * @return String
 */
public String getRTF7() {
	return RTF7;
}

/**
 * Returns the rTF9.
 * @return String
 */
public String getRTF9() {
	return RTF9;
}

/**
 * Returns the stage_id.
 * @return String
 */
public String getStage_id() {
	return stage_id;
}

/**
 * Returns the vDocs.
 * @return Vector
 */
public Vector getVDocs() {
	return vDocs;
}

/**
 * Sets the element_Name.
 * @param element_Name The element_Name to set
 */
public void setElement_Name(String element_Name) {
	this.element_Name = element_Name;
}

/**
 * Sets the id.
 * @param id The id to set
 */
public void setId(String id) {
	this.id = id;
}

/**
 * Sets the priority.
 * @param priority The priority to set
 */
public void setPriority(String priority) {
	this.priority = priority;
}

/**
 * Sets the proposed_By.
 * @param proposed_By The proposed_By to set
 */
public void setProposed_By(String proposed_By) {
	this.proposed_By = proposed_By;
}

/**
 * Sets the proposed_DateTime.
 * @param proposed_DateTime The proposed_DateTime to set
 */
public void setProposed_DateTime(String proposed_DateTime) {
	if(proposed_DateTime.trim().equalsIgnoreCase(""))
			return;
		try{
			this.proposed_DateTime = Timestamp.valueOf(proposed_DateTime);
		}
		catch(IllegalArgumentException ie){
		
		proposed_DateTime = proposed_DateTime + this.trail_hh_mm_ss_ffffffff;
		this.proposed_DateTime = Timestamp.valueOf(proposed_DateTime);
		}
}

/**
 * Sets the reference_Number.
 * @param reference_Number The reference_Number to set
 */
public void setReference_Number(String reference_Number) {
	this.reference_Number = Integer.parseInt(reference_Number.trim());
}

/**
 * Sets the rTF1.
 * @param rTF1 The rTF1 to set
 */
public void setRTF1(String rTF1) {
	RTF1 = rTF1;
}

/**
 * Sets the rTF7.
 * @param rTF7 The rTF7 to set
 */
public void setRTF7(String rTF7) {
	RTF7 = rTF7;
}

/**
 * Sets the rTF9.
 * @param rTF9 The rTF9 to set
 */
public void setRTF9(String rTF9) {
	RTF9 = rTF9;
}

/**
 * Sets the stage_id.
 * @param stage_id The stage_id to set
 */
public void setStage_id(String stage_id) {
	this.stage_id = stage_id;
}

/**
 * Sets the vDocs.
 * @param vDocs The vDocs to set
 */
public void setVDocs(Vector vDocs) {
	this.vDocs = vDocs;
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
public boolean setType(String type) {
	
	if(!(type.equalsIgnoreCase("ISSUE") ||
		type.equalsIgnoreCase("CHANGEREQUEST") ||
		type.equalsIgnoreCase("CRIFOLDER"))){
			return false;
		}
	this.type = type;
	
	return true;
		
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
	
public int RetrievePopulationOfResources() {
		if(vResources == null)
			return -1;
		return vResources.size();
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

/**
 * Returns the duration.
 * @return String
 */
public String getDuration() {
	return duration;
}

/**
 * Returns the eTC.
 * @return String
 */
public String getETC() {
	return ETC;
}

/**
 * Returns the percentComplete.
 * @return String
 */
public String getPercentComplete() {
	return percentComplete;
}

/**
 * Returns the published.
 * @return String
 */
public String getPublished() {
	return published;
}

/**
 * Returns the revision_history.
 * @return String
 */
public String getRevision_history() {
	return revision_history;
}

/**
 * Sets the duration.
 * @param duration The duration to set
 */
public void setDuration(String duration) {
	this.duration = duration;
}

/**
 * Sets the eTC.
 * @param eTC The eTC to set
 */
public void setETC(String eTC) {
	ETC = eTC;
}

/**
 * Sets the percentComplete.
 * @param percentComplete The percentComplete to set
 */
public void setPercentComplete(String percentComplete) {
	this.percentComplete = percentComplete;
}

/**
 * Sets the published.
 * @param published The published to set
 */
public void setPublished(String published) {
	this.published = published;
}

/**
 * Sets the revision_history.
 * @param revision_history The revision_history to set
 */
public void setRevision_history(String revision_history) {
	this.revision_history = revision_history;
}

/**
 * Returns the start.
 * @return Timestamp
 */
public Timestamp getStart() {
	return start;
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
 * Returns the ets_ID.
 * @return String
 */
public String getEts_ID() {
	return ets_ID;
}

/**
 * Sets the ets_ID.
 * @param ets_ID The ets_ID to set
 */
public void setEts_ID(String ets_ID) {
	this.ets_ID = ets_ID;
}

/**
 * Returns the info_Src.
 * @return char
 */
public char getInfo_Src() {
	return ETSPMOGlobalInitialize.getCR_CREATEDINPMO_STATE().charAt(0);
}

/**
 * Sets the info_Src.
 * @param info_Src The info_Src to set
 */
public void setInfo_Src(char info_Src) {
	this.info_Src = info_Src;
}
	
public void populatevexception(exception exe) {
		if(this.vexceptions == null){
			vexceptions = new Vector();
		}
		vexceptions.add(exe);
	}
	
public int RetrievePopulationOfexceptions() {
		if(vexceptions == null)
			return -1;
		return vexceptions.size();
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

								/**
								 * Returns the vexceptions.
								 * @return Vector
								 */
								public Vector getVexceptions() {
									return vexceptions;
								}
								
public void populatevRTF(RTFData rtfD) {
		if(this.vRTFs == null){
			vRTFs = new Vector();
		}
		vRTFs.add(rtfD);
	}
	
public int RetrievePopulationOfRTFs() {
		if(vRTFs == null)
			return -1;
		return vRTFs.size();
	}
	
public RTFData retrieve_RTF(int index) throws IndexOutOfBoundsException{
	RTFData rtfData = null;
	if(this.vRTFs != null &&
		!this.vRTFs.isEmpty()){
			if(index >= vRTFs.size()){
				throw new IndexOutOfBoundsException("The index is beyond the limits of the vector: vRTFs");
			}
	rtfData = (RTFData)vRTFs.get(index);
	}
	return rtfData;
}

								/**
								 * Returns the vexceptions.
								 * @return Vector
								 */
								public Vector getVRTFs() {
									return vRTFs;
								}

/**
 * @return
 */
public static String getCLASS_VERSION() {
	return CLASS_VERSION;
}

/**
 * @param vector
 */
public void setVRTFs(Vector vector) {
		vRTFs = vector;
}

}
