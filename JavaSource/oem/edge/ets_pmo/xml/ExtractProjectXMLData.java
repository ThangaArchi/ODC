package oem.edge.ets_pmo.xml;

import java.sql.Timestamp;
import java.sql.SQLException;
import java.util.*;
import java.io.*;
import org.apache.log4j.Logger;

import oem.edge.ets_pmo.datastore.exception.*;
import oem.edge.ets_pmo.datastore.sc.*;
import oem.edge.ets_pmo.datastore.resource.*;
import oem.edge.ets_pmo.datastore.document.Doc;
import oem.edge.ets_pmo.datastore.project.*;
import oem.edge.ets_pmo.datastore.util.RTFData;
import oem.edge.ets_pmo.db.populateETS_PMO;

import oem.edge.ets_pmo.datastore.project.wbs.*;
import oem.edge.ets_pmo.util.*;

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
public class ExtractProjectXMLData {
//private Transaction trans;
  private static String CLASS_VERSION = "4.5.1";

private populateETS_PMO pop;

private boolean IsThisNewProject = true;

static Logger logger = Logger.getLogger(ExtractProjectXMLData.class);

private ArrayList docIdList = null;

public ExtractProjectXMLData(populateETS_PMO pop){

		//pop = new populateETS_PMO();
		this.pop = pop;
	}
	

/* Subu need to handle when the isReportable flag is 'N' .
 *Not handling in the present scenario. check the methid handleWBSData for help as it 
 * handles the scenario
 */
public boolean ExtractProjectData(Project proj, String PROJ_Id, String Parent_ID, String type){

	String my_Id			= proj.getProjectId();//no separate id for PMO 

 	char isReportable		;
 	if(proj.isReportable() == true){
 		isReportable = 'Y';
 	}
 	else { isReportable = 'N'; }

	String proj_Id			= PROJ_Id;
	if(PROJ_Id == null && 
		Parent_ID == null){  //if parent id is not null then assign to proj
			proj_Id	= proj.getProjectId();
	}
	String parent_Id		= Parent_ID;//automatically assigned to null if it is the root project

	String Name				= null;
	String Calendar			= null;
	int Rank				= -1; // not sure
 	int Priority			= -1; //not sure
 	String assign_Type		= null; //not sure

	Name				= proj.getElement_name();
	String Type 			= type;
	if(type == null){
		Type				=	 proj.getType();
	}

 	int Reference			= Integer.parseInt(proj.getReference_number());
 	
 	Calendar			= proj.getCalendar_id();
 	String Calendar_Rank	= proj.getCalendar_id_Rank(); // this one has the value
 	String currency			= proj.getCurrency_id();
 	String currency_Rank	= proj.getCurrency_id_Rank(); // this one has the value
 	String published		= proj.getPublished();
 	Timestamp Est_Start		= proj.getEstimatedStart();
 	Timestamp Est_Finish	= proj.getEstimatedFinish();
 	String State			= proj.getState() ;
 	String changeBrief		= null;// not available in xml
 	Timestamp start			= proj.getStart();
 	Timestamp finish		= proj.getFinish();
 	String duration			= proj.getDuration();
 	String work				= proj.getWorkPercent();
 	String percent_complete	= proj.getPercentComplete();
 	String rem_Work			= null; // not in xml
 	String EETC				= proj.getEstimatedETC();
 	String Effort_Spent		= null; // not in xml
 	String constraint		= null; //not in xml
 	Timestamp const_Date	= null; //not in xml
 	String current_finish_type = proj.getFD();
 	Timestamp current_finish = null;
 	current_finish			 = proj.getFinish();
 	String ref_code = proj.getRef_code();
 	/*
		 * I have commented all the following code. As per disc, current_finish will the finish date 
		 * All the below dates will be handled in a different table.
		 */
 	/*	if(current_finish_type.equalsIgnoreCase("Plan")){
 				//current_finish = proj.getEstimatedFinish();
 				current_finish = proj.getScheduledFinish();
 		}
 		else if(current_finish_type.equalsIgnoreCase("Expected")){
 				//current_finish = proj.getEstimatedFinish();
 				current_finish = proj.getProposedFinish();
 		}
 		else if(current_finish_type.equalsIgnoreCase("Schedule")){
 				current_finish = proj.getScheduledFinish(); 
 		}
 		else if(current_finish_type.equalsIgnoreCase("Forecast")){
 				current_finish = proj.getForecastFinish();
 		}else if(current_finish_type.equalsIgnoreCase("Actual")){
 				//current_finish = proj.getProposedFinish();
 				current_finish = proj.getActualFinish();
 		}else if(current_finish_type.equalsIgnoreCase("User Actual")){
 				//current_finish = proj.getProposedFinish();
 				current_finish = proj.getActualFinish();
 		}else if(current_finish_type.equalsIgnoreCase("User Forecast")){
 				current_finish = proj.getForecastFinish();
 		}else if(current_finish_type.equalsIgnoreCase("User Planned")){
 				//current_finish = proj.getEstimatedFinish();
 				current_finish = proj.getScheduledFinish();
 		}else if(current_finish_type.equalsIgnoreCase("Incompleted")){
 				current_finish = null;
 		}else{
 			logger.warn("The current_finish_type is not specified in the property file");	
 		}
 		*/
 	Timestamp baseline_finish = proj.getBaseline1Finish();
 	/*
 	 * Lets show the same data as in PMO. Not change it
 	 * 
 	 */
 	/*if(baseline_finish == null){
 		baseline_finish		= proj.getProposedFinish();
 	}*/

 	//Timestamp last_timestamp= null; //current timestamp
 //	String ScopeRTF			= proj.getScopeRTF();
 //	String objectivesRTF	= proj.getObjectivesRTF();
 //	String backgroundRTF	= proj.getBackgroundRTF();
 //	String statusRTF		= proj.getStatusRTF();
 //	String targetSolnRTF	= proj.getTargetSolnRTF();
 	
 
 	String str = null;
 	
 	try{
 		int delno = pop.deleteProjectandItsAssociates(my_Id);
 		if(delno > 0 ) { this.IsThisNewProject = false;}

 		str  = pop.populateProject(my_Id, proj_Id, parent_Id, Name, Type, Reference, Rank, Priority,
 							assign_Type, Calendar, Calendar_Rank, currency, currency_Rank, published,
 							Est_Start, Est_Finish, State, changeBrief, start, finish, duration, work, percent_complete,
 							rem_Work, EETC, Effort_Spent, constraint, const_Date, isReportable, current_finish_type, 
 							current_finish, baseline_finish, ref_code);
 	}
 	catch(SQLException e){
 		logger.error(	"SQLException in populating ProjectData(). " +
 						"Caught in ExtractProjectXMLData:ExtractProjectData(). The string used was :  " + str);
 	}
 	catch(Exception e){
		logger.error("ExtractProjectData(Project, String, String, String)", e);
 		logger.error(	"Exception in populating ProjectData(). " + 
 						"Caught in ExtractProjectXMLData:ExtractProjectData() " + proj.toString());
 	}
	this.populateDocs(proj.getVDocs(), proj_Id, my_Id, Type);
	if (logger.isDebugEnabled()) {
		logger
				.debug("ExtractProjectData(Project, String, String, String) - populating project docs"
						+ proj.getVDocs().size());
	}
	populateMyDocIDVector(proj.getVDocs());
 	ExtractForProjectAssociates(proj, proj_Id, my_Id, Type);
 	try{
 		ArrayList vExistingDocsFromDB = pop.getDocIdsExistingForThisProject(proj_Id);
		ArrayList TheDocsToBeDeleted = getExtraDocsFromDB(vExistingDocsFromDB);
		
		for (Iterator iter=TheDocsToBeDeleted.iterator();iter.hasNext();) {
			String docToDeleteID = (String)iter.next();
			if (logger.isDebugEnabled()) {
				logger
						.debug("ExtractProjectData(Project, String, String, String) - The doc  to be deleted is"
								+ docToDeleteID);
			}
			pop.deleteDocsThatWerentInXML(docToDeleteID);
		}
 	}
 	catch(SQLException sqle){
 		
 	}
	catch(Exception sqle){
 		
	}
 	return true;
}

private boolean ExtractForProjectAssociates(Project proj, String proj_Id, String parent_ID,String  Type){
	// Now, lets populate the project RTFs

		populateRTFData(proj.getVRTF(), proj_Id, proj_Id, parent_ID, Type);
 	
 		//Now Project ScoreCard
 		
 			
 			ScoreCard sc = proj.getScorecard();
 			
 			if(sc!=null){
 				populateScoreCard(sc, proj_Id, parent_ID, Type);
 			}
 
 	// Now project resources
 		this.populateResources(proj.getVResources(), proj_Id, parent_ID, Type);
 	
	// Now project WBS
	
		this.ExtractWBSData(proj.getVWBS(), proj_Id, parent_ID, Type);
		
		
	// Now populating Docs
	//	this.populateDocs(proj.getVDocs(), proj_Id, parent_ID, Type);
		
	// Now CR and Issues
		this.populateCRIData(proj.getVexceptions(), proj_Id, parent_ID, Type);
		
		
 		
 		this.ExtractProjectData(proj.getVProj(), proj_Id, parent_ID, Type);
 		
 								/* subu's auto return --test 
 	int i = 1;
 	if(i == 1) return true;
 	*/	
 	return true;
	
}
public String RTFToString(String rtfStr){

String txtValue = null;

RtfFilterReader inDocument = null;
try{

StringReader strReader = new StringReader(rtfStr);

//inDocument = new RtfFilterReader( reader );
inDocument = new RtfFilterReader( strReader );

}
catch(FileNotFoundException fnfe){
logger.error("RTFToString(String)", fnfe);
}
catch(IOException ioe){
logger.error("RTFToString(String)", ioe);
}
javax.swing.text.DefaultStyledDocument doc = new javax.swing.text.DefaultStyledDocument();
try{
new javax.swing.text.rtf.RTFEditorKit().read(inDocument,doc,0);  // (reader,doc,0);
logger.debug("Converted String from RTF : " + doc.getText(0,doc.getLength()));
txtValue = doc.getText(0,doc.getLength());
}
catch(javax.swing.text.BadLocationException ble){
logger.error("RTFToString(String)", ble);
}
catch(IOException ioe){
logger.error("RTFToString(String)", ioe);
}
return txtValue;


}

private boolean populateRTFData(Vector vRTF, String Pmo_Id, String proj_Id, String parent_Id, String Type){
	boolean rtrn = true;
	if(vRTF == null || vRTF.size() == 0){
		logger.debug("RTF vector empty for " + Type + " : " + Pmo_Id);
		return rtrn;
	}
 	String strRTFData="";
 	
 	try{
 		for(int i = 0 ; i < vRTF.size(); i ++){
				RTFData rtf = 	(RTFData) vRTF.get(i)	;
				strRTFData = " \nPopulating RTFs: " + 
							" \nPmo_Id: " + Pmo_Id + 
							" \nproj_Id: " + proj_Id + 
							" \nparent_Id: " + parent_Id +
							" \nrtf.getRank: " + rtf.getRank() +
							" \nrtf.getName: " + rtf.getName() +
							" \nrtf.getAliasName: " + rtf.getAliasName() +							
							" \nrtf.getValue : " + rtf.getValue();
				
				
				/*
				 * Communication probs with SystemCorp. 
				 * They are sending values in plain text instead 
				 * of RTFs which was the agreement.
				 * Need to change the code here
				 * 
				 * Now back to the original form
				 */
				 
				
				 String rtfValue = rtf.getValue();
				 String txtValue = rtfValue;
				 ByteArrayInputStream is = null;
				 
				 // if it's issue/cr, then do not update the cotent for comment_from_customer RTF
				 //  and also copy the Description to PMO_ISSUE_INFO table 
				 boolean updateContent = true;
				 
				 if (("ISSUE".equalsIgnoreCase(Type) && rtf.getRank()==7) ||
				 	 ("CHANGEREQUEST".equalsIgnoreCase(Type) && rtf.getRank()==9) )
				 	updateContent = false;
				 
				 
				 if(rtfValue != null){
					 	//rtfValue = rtfValue.trim();
					 	/* 
						  * Remember to use rtfValue.startsWith("{\\rtf") .
						  * Faced prob for 10 mins. as {\rtf wouldnt succeed.
						  * had to replace with {\\rtf
						  */
						if(rtfValue.startsWith("{\\rtf") ){
								txtValue = RTFToString(rtf.getValue());
								strRTFData += "\nAfter RTF - Text conversion, the value of the string is : " + txtValue;

						}
						else{
								txtValue = rtf.getValue();
						}
					  
					logger.debug(strRTFData);
					byte [] barr = txtValue.getBytes();
					is = new ByteArrayInputStream(barr);
				}	 //end of if(rtfValue != null)
				
				int length =  (txtValue==null)? 0 : txtValue.length();
				
	 			    pop.populateRTF(Pmo_Id+":"+rtf.getRank(), 
	 							proj_Id, 
								Pmo_Id, 
								rtf.getRank(), 
								rtf.getName(), 
								rtf.getAliasName(), 
								is, 
								length,
								updateContent);
								 
 		}
 		
 
 	}
 	catch(SQLException e){

 		logger.error(e.getMessage() + "\n SQLException in populating Project RTF Data(). " + 
 						"Caught in ExtractProjectXMLData:populateRTFData() " + strRTFData);

 		
 		rtrn = false;
 		
 	}
 	catch(Exception e){
 		logger.error(e.getMessage() + " \nException in populating Project RTF Data(). " + 
 						"Caught in ExtractProjectXMLData:populateRTFData()" + strRTFData);
 		
 		rtrn = false;
 	}	
 	return rtrn;
}
private boolean populateScoreCard(ScoreCard sc, String Project_ID, String sc_Parent_PMO_ID, 
									String ParentType){
										
				
	try{
		pop.populateScoreCard(sc.getScoreCardId(), Project_ID, sc_Parent_PMO_ID, ParentType, sc.getElement_name(), sc.getRating_score());
	}
	catch(SQLException e){

 		logger.error(e.getMessage() +"\nSQLException in populating Project ScoreCard " + 
 						"Caught in ExtractProjectXMLData:populateScoreCard() " + sc.toString());

	}
 	catch(Exception e){
 		logger.error(e.getMessage() + "\nException in populating Project ScoreCard " + 
 						"Caught in ExtractProjectXMLData:populateScoreCard()" + sc.toString());
 		
 	}
 	
 	for(int i= 0 ; i < sc.retrieveScoreCardPopulation(); i ++){
 			populateScoreCard(sc.retrieveScoreCard(i), Project_ID, sc.getScoreCardId(), "SCORECARD");	
 	}
 	
 	return true;
	
		
}

public boolean populateXMLInfoInResourceTable(String userid, String source, String destination, String app, String version, String ProjectID){

	try{//i am using  parent_id for identifying that this is projectuserid
			pop.populateResource(ProjectID, ProjectID,"projectuserid",userid, source, destination, app, version);
								
				}
			catch(SQLException e){
				logger.error(e.getMessage()+ "\nSQLException in populating USERID for this project  " + 
 						"Caught in ExtractProjectXMLData:populateUserIDInResourceTable() ");
				
 				}
 	catch(Exception e){
		 		logger.error(e.getMessage()	+ "\nException in populating USERID for this project " + 
 						"Caught in ExtractProjectXMLData:populateUserIDInResourceTable()");
 				
 				}
 				
 		return true;
	
}
private boolean populateResources(Vector vRes, String ProjectID, String parentID , String Type){
		boolean rtrn = true;
		if(vRes == null || vRes.size() == 0){
			logger.debug("Resource vector empty for " + Type + " : " + parentID);
		return rtrn;
	}
	
		int sz = vRes.size();
		for ( int i = 0; i < sz; i++){
			Resource res = (Resource) vRes.get(i);
			logger.debug(res.toString());
			try{
			pop.populateResource(res.getResourceID(), ProjectID,parentID,res.getElement_name(), res.getElement_name(), res.getCompany_name(), res.getSecurity_id(),
								res.getSecurity_id_Rank());
				}
			catch(SQLException e){
				logger.error(e.getMessage());
				
 				}
 	catch(Exception e){
		 		logger.error(e.getMessage());
 				
 				}
		}
		
		
		return rtrn;
}
/*
 * Let me keep all the docs uncompressed in our database.So i am not implementing the algo to uncompress the doc here
 * 
 */
private boolean populateDocs(Vector vDoc, String ProjectID, String parentID , String Type){
		boolean rtrn = true;
		if(vDoc == null || vDoc.size() == 0){
			logger.info("DOC vector empty for " + Type + " : " + parentID);
		return rtrn;
	}
		
		int sz = vDoc.size();
	
		for ( int i = 0; i < sz; i++){
	
			Doc doc = (Doc) vDoc.get(i);
	
			try{
				if(doc.getDoc_Type() == 2){// DOCUMENT in this case
				//I have assumed that PMOId and DocId are same . I supply same value: doc.getId()
	
				
				/*
				 * sometimes there are no docs attached in pmoffice. 
					But, the xml element tags still exist without doc blob.
					I saw one particular case when i tried creating a doc under a Issue/CR.
					The doc didnt get attached, but the xml carried the doc_type=DOCUMENT and gave me
					an exception here.
					Its very true that we wont be facing the problem with Issue/CR as we dont handle them 
					here. But who knows? We better be safe.
				 */			
				 
				if(doc.getDocument_Size() <= 0){
	
					continue;
				}
	
				int HasDocChanged = pop.checkIfDocChangedSinceLastUpdateToETSTables(
								doc.getId(), doc.getId(), ProjectID, parentID, Type,
								doc.getElement_Name(), doc.getDoc_Type(), doc.getAttachment(),
								doc.getOwner_Id(), doc.getSecurityLevel(),
								doc.getRevision(),doc.getCreation_Date(), doc.getLast_Checkin()
								);
	
				boolean IsCOMPRESSED = false;
				if(doc.getIsCompressed()=='Y') { IsCOMPRESSED = true;}
				
				if(HasDocChanged == 0){// Doc hasnt Changed
					// Good.Dont have to do anything in this case.
					logger.debug("The Document :\tDOC ID : " + 
								doc.getId() + "\tDOC NAME :" +
								doc.getElement_Name() + " is unchanged during this synch");
				}else if(HasDocChanged == 1){// Doc doesnt exist at all.
					logger.debug("The Document :\tDOC ID : " + 
								doc.getId() + "\tDOC NAME :" +
								doc.getElement_Name() + " doesnt exist.  We are now inserting this record.");
								logger.debug("IScompressed : " + doc.getIsCompressed());
//					logger.debug("doc.getBlob_Data length" + doc.getBlob_data(IsCOMPRESSED).length);
					logger.debug("doc size" + doc.getDocument_Size());
					logger.debug("Comp size :" + doc.getCompressed_size());
					pop.populatePMODoc(	doc.getId(), ProjectID, doc.getId(), parentID, Type,
										doc.getElement_Name(), doc.getDoc_Type(), doc.getIsCompressed(),
										doc.getAttachment(),
										doc.getBlob_data_stream(IsCOMPRESSED),
										////new ByteArrayInputStream(doc.getBlob_data(IsCOMPRESSED)), 
										doc.getOwner_Id(), doc.getSecurityLevel(),
										doc.getRevision(),doc.getCreation_Date(), doc.getUploadDate(), doc.getLast_Checkin(),
										doc.getCompressed_size(), doc.getDocument_Size());
					doc.close_data_stream();
	
				}
				else if(HasDocChanged == -1){	// Doc blob has changed since the previous update.Need to update : 
												//doc blob, update date, publish date, version info 
					logger.debug("The Document :\tDOC ID : " + 
								doc.getId() + "\tDOC NAME :" +
								doc.getElement_Name() + ", only the blob data information will be changed. We are updating the record.");
					
					pop.updatePMODoc(doc.getId(), 
							doc.getBlob_data_stream(IsCOMPRESSED),
							////new ByteArrayInputStream(doc.getBlob_data(IsCOMPRESSED)), 
							doc.getLast_Checkin(), 
							doc.getCreation_Date(), 
							doc.getRevision(), 
							doc.getCompressed_size(), 
							doc.getDocument_Size());
					doc.close_data_stream();
				}
				else if(HasDocChanged == -2){	// Only doc data has changed here. The data are :
											 	//					Pmo_Id 				pmo id for this doc							
						 						//					Pmo_Project_Id		pmo project id
						 						// 					Parent_Pmo_Id		parent pmo id
						 						// 					Parent_Type			parent type
						 						// 					Doc_Name			doc name
						 						// 					Doc_Type			doc type
						 						// 					Doc_Desc			doc description
						 						// 					Owner_Id			owner of the doc
						 						// 					Security_Level
					logger.debug("The Document :\tDOC ID : " + 
								 doc.getId() + "\tDOC NAME :" +
								 doc.getElement_Name() + ". The data attributes (doc data ) will be changed");
					pop.updatePMODoc(doc.getId(), doc.getId(), ProjectID, parentID, Type, doc.getElement_Name(),
									doc.getDoc_Type(), doc.getAttachment(), doc.getOwner_Id(), doc.getSecurityLevel());
				}
				else if(HasDocChanged == -3){// This is the union of the if statements with HasDocChanged = -1 and -2
											// In this case all the attributes for the doc and the doc blob get updated.
											// Basically everything!
					logger.debug("The Document :\tDOC ID : " + 
								 doc.getId() + "\tDOC NAME :" + 
								 doc.getElement_Name()  + ". All the data attributes and the doc blob" +								 " will be changed now. Basically everything");
					pop.updatePMODoc(doc.getId(), doc.getId(), ProjectID, parentID, Type, doc.getElement_Name(),
									doc.getDoc_Type(), doc.getAttachment(), doc.getOwner_Id(), doc.getSecurityLevel(),
									////new ByteArrayInputStream(doc.getBlob_data(IsCOMPRESSED)), 
									doc.getBlob_data_stream(IsCOMPRESSED),
									doc.getLast_Checkin(), 
									doc.getCreation_Date(), doc.getRevision(), doc.getCompressed_size(), doc.getDocument_Size());
					doc.close_data_stream();
				
				}
				
				}
				else if(doc.getDoc_Type() == 1){ //DOCUMENTFOLDER in this case
					
				pop.populateProject(doc.getId(), ProjectID, parentID, doc.getElement_Name(),"DOCUMENTFOLDER", -1, -1, -1,
				null, null, null, null, null, null,null, null, null, null, null, null,  null, null,null, null, null,
				 null, null, null, 'Y', null, null, null, null);  
					
				}
				else{
					if (logger.isDebugEnabled()) {
						logger.debug("populateDocs(Vector, String, String, String) - doctype"
										+ doc.getDoc_Type());
					}
				}

				}
			catch(SQLException e){
				logger.error(e.getMessage() + "SQLException in populating Docs  " + 
 						"Caught in ExtractProjectXMLData:populateDocs() " + doc.toString());
				
 				}
			catch(Exception e){
		 		logger.error(e.getMessage() + "Exception in populating Docs " + 
 						"Caught in ExtractProjectXMLData:populateDocs()" + doc.toString());
		 	}
 				String docT = "";
 				
 			if(doc.getDoc_Type() == 1) {
 					docT = "DOCUMENTFOLDER"; 	
 			}
 			else if(doc.getDoc_Type() == 2) {
 					docT = "DOCUMENT"; 	
 			}
			/***** 
			 * 
			 *This case identifies the existence of DOCUMENTS within DOCUMENTS in PMOffice which is a deviation
			 *from the standard behavior of DOCUMENTS within DOCUMENTFOLDER.
			 *
			 *eg:
			 *DOCUMENTFOLDER1
			 * 		DOCUMENT1
			 * 			DOCUMENT2
			 * DOCUMENT1 is the parent of DOCUMENT2
			 * 
			 * In this example, I will make DOCUMENTFOLDER1 to be the direct parent of DOCUMENT1 and DOCUMENT2
			 * to handle the abnormal behavior offered by PMOffice
 			****/
			String ParentFolderID = doc.getId();
 			if(	doc.RetrievePopulationOfDocs() > 0 &&
 				doc.getDoc_Type() == 2){
 					ParentFolderID = parentID;
 				}
 		populateDocs(doc.getVDocs(), ProjectID, ParentFolderID, docT);
		}
		

		return rtrn;
}

/* I shouldnt use ets_id as the primary field. Here in this case. I am doing it. which casuses duplicate insert
 * problems. We need a change in the db and need to remove ets_id from this table and make the pmo_id the primary
 * field. 
 * Temporary soln: I am not using ets_id at all. Instead using pmo_Id in the ets_Id field.
 * I am populating the pmo_Id field in the database also with same value.
 * */
private boolean populateCRIDocs(
	Vector vDoc,
	String ProjectID,
	String parentID,
	String Type,
	String ets_Id,
	char info_src_flag,
	String last_userid) {
		
logger.debug("\n\nPopulating/Updating the Change Request/Issue Docs");
	boolean rtrn = true;
	String my_id = null;
	if (vDoc == null || vDoc.size() == 0) {
		logger.info("CRI doc vector empty for " + Type + " : " + parentID);
		return rtrn;
	} else
		logger.debug("CRI doc size : " + vDoc.size());
	int sz = vDoc.size();

	for (int i = 0; i < sz; i++) {
		Doc doc = (Doc) vDoc.get(i);
		String docT = "";
		boolean IsCOMPRESSED =
							doc.getIsCompressed() == 'Y' ? true : false;
		if (doc.getDocument_Size() > 0) {
			int HasDocChanged = -1;
			try{
				
			HasDocChanged = pop.checkIfCRIDocChangedSinceLastUpdateToETSTables(doc.getId(), doc.getId(), ProjectID,
			parentID,
			doc.getElement_Name(),
			doc.getDoc_Type(),doc.getAttachment(),doc.getOwner_Id(), doc.getLast_Checkin());
			
		}
			catch (SQLException e) {
								logger.error(
									e.getMessage()
										+ "\n SQLException in populating Docs  "
										+ "Caught in ExtractProjectXMLData:populateCRIDocs() while calling checkIfCRIDocChangedSinceLastUpdateToETSTables "
										+ doc.toString());

							} catch (Exception e) {
								logger.error(
									e.getMessage()
										+ "\n Exception in populating Docs "
										+ "Caught in ExtractProjectXMLData:populateCRIDocs()while calling checkIfCRIDocChangedSinceLastUpdateToETSTables"
										+ doc.toString());

							}

			if (HasDocChanged == 0) { // Doc hasnt Changed
				// Good.Dont have to do anything in this case.
				logger.debug(
					"The Document :\tDOC ID : "
						+ doc.getId()
						+ "\tDOC NAME :"
						+ doc.getElement_Name()
						+ " is unchanged during this synch");
			} else if (
				HasDocChanged == 1) {
				// Doc doesnt exist at all. Need to insert one
				try {

					//I have assumed that PMOId and DocId are same . I supply same value: doc.getId()
					my_id = doc.getId();

					logger.debug(
						"\n\nPopulating the following CRI DOC :\n"
							+ "ETS ID : \t"
							+ ets_Id
							+ "\nPMO ID : \t"
							+ my_id
							+ "\nDOC NAME : \t"
							+ doc.getElement_Name()
							+ "\n");

				

					pop.populateCRIDoc(/*ets_Id*/
					ets_Id,
						my_id,
						ProjectID,
						parentID,
						doc.getElement_Name(),
						doc.getDoc_Type(),
						doc.getAttachment(),
						////new ByteArrayInputStream(doc.getBlob_data(IsCOMPRESSED)),
						doc.getBlob_data_stream(IsCOMPRESSED),
						info_src_flag,
						last_userid,
						doc.getCompressed_size(),
						doc.getDocument_Size(),
						doc.getLast_Checkin());
					doc.close_data_stream();

				} catch (SQLException e) {
					logger.error(
						e.getMessage()
							+ "\n SQLException in populating Docs  "
							+ "Caught in ExtractProjectXMLData:populateCRIDocs() while calling pop.populateCRIDoc"
							+ doc.toString());

				} catch (Exception e) {
					logger.error(
						e.getMessage()
							+ "\n Exception in populating Docs "
							+ "Caught in ExtractProjectXMLData:populateCRIDocs() while calling pop.populateCRIDoc"
							+ doc.toString());

				}
			} else if (
				HasDocChanged == -1) {
				// Doc blob has changed since the previous update.Need to update : 
				//doc blob, update date, publish date, version info 
				logger.debug(
					"The Document :\tDOC ID : "
						+ doc.getId()
						+ "\tDOC NAME :"
						+ doc.getElement_Name()
						+ ", only the blob data information will be changed. We are updating the record.");
				try{
				
				pop.updateCRIDoc(
					doc.getId(),
					////new ByteArrayInputStream(doc.getBlob_data(IsCOMPRESSED)),
					doc.getBlob_data_stream(IsCOMPRESSED),
					doc.getCompressed_size(),
					doc.getDocument_Size(),
					doc.getLast_Checkin());
				doc.close_data_stream();
				}
				catch (SQLException e) {
					logger.error(
						e.getMessage()
							+ "\n SQLException in updating Docs  "
							+ "Caught in ExtractProjectXMLData:populateCRIDoc()  while calling updateCRIDOc :First"
							+ doc.toString());

				} catch (Exception e) {
					logger.error(
						e.getMessage()
							+ "\n Exception in updating Docs "
							+ "Caught in ExtractProjectXMLData:populateCRIDoc() while callin updateCRIDoc :First"
							+ doc.toString());

				}
			} else if (
				HasDocChanged == -2) {
				// Only doc data has changed here. The elements that need to be modified are :
				
				/*String pmo_Proj_Id,
				String parent_Pmo_Id,
				String Doc_Name,
				int Doc_Type,
				String Doc_Desc,
				String last_Userid
				*/
				logger.debug(
					"The Document :\tDOC ID : "
						+ doc.getId()
						+ "\tDOC NAME :"
						+ doc.getElement_Name()
						+ ". The data attributes (doc data ) will be changed");
				try{	
				
				pop.updateCRIDoc(
					doc.getId(),
					ProjectID,
					parentID,
					doc.getElement_Name(),
					doc.getDoc_Type(),
					doc.getAttachment(),
					doc.getOwner_Id()
					);
				}			catch (SQLException e) {
										logger.error(
											e.getMessage()
												+ "\n SQLException in updating Docs  "
												+ "Caught in ExtractProjectXMLData:populateCRIDOc while calling updateCRIDoc() :Next"
												+ doc.toString());

									} catch (Exception e) {
										logger.error(
											e.getMessage()
												+ "\n Exception in updating Docs "
												+ "Caught in ExtractProjectXMLData:populateCRIDoc while calling updateCRIDoc(): Next"
												+ doc.toString());

									}
			} else if (
				HasDocChanged == -3) {
				// This is the union of the if statements with HasDocChanged = -1 and -2
				// In this case all the attributes for the doc and the doc blob get updated.
				// Basically everything!
				logger.debug(
					"The Document :\tDOC ID : "
						+ doc.getId()
						+ "\tDOC NAME :"
						+ doc.getElement_Name()
						+ ". All the data attributes and the doc blob"
						+ " will be changed now. Basically everything");
				try{
				
					pop.updateCRIDoc(
						doc.getId(),
						ProjectID,
						parentID,
						doc.getElement_Name(),
						doc.getDoc_Type(),
						doc.getAttachment(),
						doc.getOwner_Id()
						);
					pop.updateCRIDoc(
										doc.getId(),
										////new ByteArrayInputStream(doc.getBlob_data(IsCOMPRESSED)),
										doc.getBlob_data_stream(IsCOMPRESSED),
										doc.getCompressed_size(),
										doc.getDocument_Size(), 
										doc.getLast_Checkin());
					doc.close_data_stream();
				}
				catch (SQLException e) {
										logger.error(
											e.getMessage()
												+ "\n SQLException in updating Docs  "
												+ "Caught in ExtractProjectXMLData:populateCRIDoc while calling updateCRIDoc():Last "
												+ doc.toString());

									} catch (Exception e) {
										logger.error(
											e.getMessage()
												+ "\n Exception in updating Docs "
												+ "Caught in ExtractProjectXMLData:populateCRIDOc while calling updateCRIDoc():LAst"
												+ doc.toString());

									}
									

			}

			if (doc.getDoc_Type() == 1) {
				docT = "DOCUMENTFOLDER";
			} else if (doc.getDoc_Type() == 2) {
				docT = "DOCUMENT";
			}
		} //if doc.getDocument_Size()>0
		
		populateCRIDocs(
			doc.getVDocs(),
			ProjectID,
			parentID,
			docT,
			ets_Id,
			info_src_flag,
			last_userid);
	}
	logger.debug("\n\nPopulating/Updating the Change Request/Issue Docs  done \n"); 
	return rtrn;

}
private boolean ExtractWBSData(Vector vWBS, String proj_Id, String parent_Id, String type){
	boolean rtrn = true;
	if(vWBS == null || vWBS.size() == 0){
			logger.debug("WBSData vector empty for " + type + " : " + parent_Id);
		return rtrn;
	}
	int sz = vWBS.size();
	int i =0 ;
		for(i = 0; i < sz; i++){
			WBSElement wbsEle = (WBSElement)vWBS.get(i);
			rtrn = this.handleWBSData(wbsEle, proj_Id, parent_Id, type);
		}
	return rtrn;
	
}
private boolean ExtractProjectData(Vector vProj, String proj_Id, String parent_Id, String type){
	boolean rtrn = true;
	if(vProj == null || vProj.size() == 0){
			logger.debug("ProjectData vector empty for " + type + " : " + parent_Id);
		return rtrn;
	}
	int sz = vProj.size();
	for(int i = 0; i < sz; i++){
			Project proj = (Project)vProj.get(i);	
	
			rtrn = this.ExtractProjectData(proj, proj_Id, parent_Id, type);
		
		
	}
	
		
	return rtrn;
	
}
private boolean handleWBSData(WBSElement wbsEle, String PROJ_Id, String Parent_ID, String type){
	String Pmo_Id			= wbsEle.getId();//no separate id for PMO
	char isReportable		; 
	if(wbsEle.isReportable() == true){
 		isReportable = 'Y';
 	}
 	else { isReportable = 'N'; }
	String proj_Id			= PROJ_Id;
	String parent_Id		= Parent_ID;
	String Name = null;
	String Type = null;
	int Reference = -1;
	int Rank				= -1;// not sure
 	int Priority			= -1;
 	String assign_Type		= null; //not sure
 	String Calendar			= null;
 	String Calendar_Rank	= null; // this one has the value
 	String currency			= null;
 	String currency_Rank	= null; // this one has the value
 	Timestamp Est_Start		= null;
 	Timestamp Est_Finish	= null;
 	String published		= null;
 	String State			= null;
 	String changeBrief		= null;// not available in xml
 	Timestamp  start		= null;
 	Timestamp finish		= null;
 	String duration			= null;
 	String work				= null;
 	String percent_complete = null;
 	String rem_Work			= null;// not is xml
 	String EETC				= null;
 	String Effort_Spent		= null;//not in xml
 	String constraint		= null;//not in xml
 	Timestamp const_Date	= null;//not in xml 
 	String current_finish_type = null;
 	Timestamp baseline_finish = null;
	Timestamp current_finish = null;
 	if(wbsEle.isReportable() == true){
		Name				= wbsEle.getElement_name();
		Type				= wbsEle.getType();
	 	Reference			= Integer.parseInt(wbsEle.getReference_number());
 	
 		if(wbsEle.getPriority() != null){
		 	Priority			= Integer.parseInt(wbsEle.getPriority()); 
	 	}
 	
 		published		= wbsEle.getPublished();
 	
 		State			= wbsEle.getState() ;
 	
 		start			= wbsEle.getStart();
 		finish		= wbsEle.getFinish();
	 	duration			= wbsEle.getDuration();
	 	work				= wbsEle.getWork_percent2();// might have some problems here..lets check
	 	percent_complete	= wbsEle.getPercent_Complete();
 		EETC				= wbsEle.getETC();
 		current_finish_type = wbsEle.getFD();
		current_finish = null;
		current_finish			 = wbsEle.getFinish();
		/*
		 * I have commented all the following code. As per disc, current_finish will the finish date 
		 * All the below dates will be handled in a different table.
		 */
		/*
 		if(current_finish_type.equalsIgnoreCase("Plan")){
 				//current_finish = wbsEle.getEstimatedFinish();
 				current_finish = wbsEle.getScheduledFinish();
 		}
 		else if(current_finish_type.equalsIgnoreCase("Expected")){
 				//current_finish = wbsEle.getEstimatedFinish();
 				current_finish = wbsEle.getProposedFinish();
 		}
 		else if(current_finish_type.equalsIgnoreCase("Schedule")){
 				current_finish = wbsEle.getScheduledFinish();
 		}
 		else if(current_finish_type.equalsIgnoreCase("Forecast")){
 				current_finish = wbsEle.getForecastFinish();
 		}else if(current_finish_type.equalsIgnoreCase("Actual")){
 				//current_finish = wbsEle.getProposedFinish();
 				current_finish = wbsEle.getActualFinish();
 		}else if(current_finish_type.equalsIgnoreCase("User Actual")){
 				//current_finish = wbsEle.getProposedFinish();
 				current_finish = wbsEle.getActualFinish();
 		}else if(current_finish_type.equalsIgnoreCase("User Forecast")){
 				current_finish = wbsEle.getForecastFinish();
 		}else if(current_finish_type.equalsIgnoreCase("User Planned")){
 				//current_finish = wbsEle.getEstimatedFinish();
 				current_finish = wbsEle.getScheduledFinish();
 		}else if(current_finish_type.equalsIgnoreCase("Incompleted")){
 				current_finish = null;
 		}else{
 			logger.warn("The current_finish_type is not specified in the property file");	
 		}
 		*/
 	baseline_finish = wbsEle.getBaseline1Finish();
 	/* Lets show the same data as in PMO. Not change it.
 	 * baseline_finish is null if the there is no data
 	 */
 	 /*
 	if(baseline_finish == null){
 		baseline_finish		= wbsEle.getProposedFinish();
 	}
 	*/
 	}


 	
 	
 	//Timestamp last_timestamp= null; //current timestamp
 //	String ScopeRTF			= proj.getScopeRTF();
 //	String objectivesRTF	= proj.getObjectivesRTF();
 //	String backgroundRTF	= proj.getBackgroundRTF();
 //	String statusRTF		= proj.getStatusRTF();
 //	String targetSolnRTF	= proj.getTargetSolnRTF();
 	
		logger.debug("Populating following Data : " + wbsEle.toString());
 	String ref_code=null;
 	try{
 		pop.populateProject(Pmo_Id, proj_Id, parent_Id, Name, Type, Reference, Rank, Priority,
 							assign_Type, Calendar, Calendar_Rank, currency, currency_Rank, published,
 							Est_Start, Est_Finish, State, changeBrief, start, finish, duration, work, percent_complete,
 							rem_Work, EETC, Effort_Spent, constraint, const_Date, isReportable, current_finish_type,
 							current_finish, baseline_finish,ref_code);
 	}
 	catch(SQLException e){
 		logger.error("SQLException in populating ProjectData(). " + 
 						"Caught in ExtractProjectXMLData:ExtractProjectData() "  + wbsEle.toString());
 		
 	}
 	catch(Exception e){
		logger.error("handleWBSData(WBSElement, String, String, String)", e);
 		logger.error(	"Exception in populating ProjectData(). " + 
 						"Caught in ExtractProjectXMLData:ExtractProjectData() " + wbsEle.toString());
 	}
 	
 	// Now, lets populate the project RTFs
		populateRTFData(wbsEle.getVRTF(), Pmo_Id, proj_Id, parent_Id, Type);
 	
 		
 			
 	// Now wbselement resources
 		this.populateResources(wbsEle.getVResources(), proj_Id, Pmo_Id, Type);
 		
 		
	// Now  WBS within this work item
	
		this.ExtractWBSData(wbsEle.getVWBS(), proj_Id, Pmo_Id, Type);
		
		// Now populating Docs
		this.populateDocs(wbsEle.getVDocs(), proj_Id, Pmo_Id, Type);
 		populateMyDocIDVector(wbsEle.getVDocs());
 	return true;

	
}
private boolean populateCRIData(Vector vexceptions, String ProjectID, String parentID , String Type){
		boolean rtrn = true;
		logger.debug("\n\nPopulating/Updating the Change Request/Issue\n");
		if(vexceptions == null || vexceptions.size() == 0){
			logger.debug("CRI vector empty for " + Type + " : " + parentID);
		return rtrn;
	}
	int sz = vexceptions.size();
		for ( int i = 0; i < sz; i++){
			exception exc = (exception) vexceptions.get(i);
			
			try{
				//I have assumed that PMOId and DocId are same . I supply same value: doc.getId()
				if(!exc.getType().equalsIgnoreCase("CRIFOLDER")){
					logger.debug("Extracting info for CRI id :" + exc.getId() + " starting...");
									
					Resource res=null;
					String submitter = exc.getProposed_By();
					boolean isUsername = true;
					if (submitter==null)
					{
						submitter = pop.retrieveSUBMITTER_NAMEforException(exc.getId());
						isUsername = true;
						if (submitter==null)
						{
						  submitter = exc.retrieveResource(0).getLogon_name();
						  isUsername = false;
						}
					}
					//String logon_name= exc.retrieveResource(0).getLogon_name();
					
					if (submitter!=null)
						res = pop.getResourceInfo(submitter.trim(), isUsername);
					if (res==null)
						res = exc.retrieveResource(0);
					if (res.getElement_name()==null)
						res.setElement_name(exc.getProposed_By());
					
					String resourceElementName = res.getElement_name();			
					String resourceCompanyName = res.getCompany_name();				
					String resourceEmail		= res.getEmail();						
					String resourcePhone		= res.getPhone();	
					logger.debug("LogonId="+res.getLogon_name()+",username="+resourceElementName);
					logger.debug("resourceCompanyName=" + resourceCompanyName+",resourceEmail=" + resourceEmail+",resourcePhone=" + resourcePhone);
					
					//String comm_from_cust		= null;
					String Owner_Id		= null;
					String Owner_Name	= null;
					int comm_rank = 7;
					if(exc.getType().equalsIgnoreCase("ISSUE")){
						comm_rank = 7;
						
						Owner_Id 		= exc.retrieveResource(0).getLogon_name(); //pop.getIssueOwnerId("ETSPMO", "Defect", ProjectID);
						if (Owner_Id==null)
							Owner_Id = pop.getIssueOwnerId("ETSPMO", "Defect", ProjectID);

						Owner_Name		= exc.retrieveResource(0).getElement_name(); //pop.getIssueOwnerName(Owner_Id);	
						if (Owner_Name==null)
							Owner_Name	= pop.getIssueOwnerName(Owner_Id);
						
						/*
						Owner_Id 		= exc.retrieveResource(0).getLogon_name();
						if (Owner_Id==null)
							Owner_Id 		= pop.getIssueOwnerId("ETSPMO", "Defect", ProjectID);
						Owner_Name		= pop.getIssueOwnerName(Owner_Id);	
						if (Owner_Name==null)
							Owner_Name		= exc.retrieveResource(0).getElement_name(); 
						*/
					
					}
					else if(exc.getType().equalsIgnoreCase("CHANGEREQUEST")){
						comm_rank = 9;
						//comm_from_cust			= exc.getRTF9();	
						//comm_from_cust = "";
					}
					//logger.debug("comm_from_cust : " + comm_from_cust);
					
					
					//case 1)if this issue is under this criflolder...then update the issue/cr
					//..this returns "nochange"
					//case 2)if i dont find this issue in thie crifolder..then insert 
					//..i have to generate a new ets_id..returns "findnewETSid"
					//case 3)if i find this issue but not under this crifolder..then get the parent_id and update the parent_id to be this crifodler id
					//...here i get the parent_id as the return value
					// i am handling all the above commented cases here
					// case 4...this is not any importnat case. since pmo_id is not the primary field,
					// there could be possiblity of more thatn 1 records which should never be the case
					// during such situations, it returns "morethan1recordavailable". this is an error case
					String rslt= pop.IsExceptionInSameFormORDifferentFormBecauseOfThisSync(exc.getId(), parentID, ProjectID);
					String ets_Id = rslt.substring(0, rslt.indexOf(":"));
					rslt		  = rslt.substring(rslt.indexOf(":") + 1);
					
					
					if(rslt.equalsIgnoreCase("nochange")){
						//Basically, i am not altering the ets id that was provided when the record was created from ets side
						pop.updateCRIInfoFromPMO(exc.getId(), ProjectID,parentID,  exc.getReference_Number(),
								exc.getType(), exc.getElement_Name(), 
								exc.getPriority(), exc.getType(), exc.getStage_id(), Owner_Id,  Owner_Name, res);
						}
					else if(rslt.equalsIgnoreCase("findnewETSid")){
						ets_Id = exc.GenerateandRetrieveETSID();	
					//subu 4.5.1 fix
						
						pop.populateCRIInfo(ets_Id, exc.getId(), ProjectID,parentID,  exc.getReference_Number(), exc.getInfo_Src(),
								resourceElementName, resourceCompanyName, resourceEmail, resourcePhone, 
								exc.getStage_id(), null, exc.getProposed_DateTime(), exc.getType(), exc.getElement_Name(), 
								exc.getPriority(), exc.getType(),  
								Owner_Id,  Owner_Name, null, "ALL");
						}
					else if(rslt.equalsIgnoreCase("morethan1recordavailable")){
							logger.debug("more than 1 record with same exceptionID :" + exc.getId() + " int the table pmo_issue_info");
						 	return false;
						 }
					else {// here i get the rslt which has the parent id. 
						// Check out the 3rd parameter in updateIssueCR...its the rslt that i get back
					//Basically, i am not altering the ets id that was provided when the record was created from ets side	
						pop.updateCRIInfoFromPMO(exc.getId(), ProjectID, rslt.trim(), exc.getReference_Number(),
								exc.getType(), exc.getElement_Name(), 
								exc.getPriority(), exc.getType(), exc.getStage_id(), Owner_Id,  Owner_Name, res);
					}																			
				
					if(exc.getType().equalsIgnoreCase("ISSUE")){
						String Issue_State = exc.getStage_id();
					//	if(pop.checkLastActionState(ets_Id, Issue_State) == true){
					
							pop.AddNewRecordInPMOIssueHistory(false, ets_Id, null, resourceElementName, Issue_State, null);
					//	}
					}
					
					if(exc.getVDocs() != null){

						populateCRIDocs(exc.getVDocs(), ProjectID, exc.getId(), exc.getType(), ets_Id, exc.getInfo_Src(), resourceElementName);	
					}
					
					// Need to create RTF entry for Description and Comments in ETS_PMO_RTF table, to be able to store 
					// updates of these RTF from ICC
					boolean hasComm=false;
					boolean hasDesc=false;
					Vector v = exc.getVRTFs();
					if (v!=null)
					  for (Enumeration e = v.elements() ; e.hasMoreElements() ;) {
						 RTFData  rtf = (RTFData)e.nextElement();
						 if (rtf.getRank()==1)
						 	hasDesc=true;
						 if(rtf.getRank()==comm_rank)
						 	hasComm=true;
					  }
					if (hasComm==false)
					{
						RTFData rtf = new RTFData("Comments", "Comments", "", comm_rank);
						exc.populatevRTF(rtf);
					}
					if (hasDesc==false)
					{
						RTFData rtf = new RTFData("Description", "Description", "", 1);
						exc.populatevRTF(rtf);
					}
					
					populateRTFData(exc.getVRTFs(), exc.getId(), ProjectID, exc.getId(), exc.getType());
					
					
					/*
					//boolean IsCommentsPresent = false;
					if(exc.getVRTFs() != null){
						logger.debug("RTF Vector not Empty for the exception : " + exc.getId());	
						populateRTFData(exc.getVRTFs(), exc.getId(), ProjectID, exc.getId(), exc.getType());
						
						
						Vector v = exc.getVRTFs();
						
						for (Enumeration e = v.elements() ; e.hasMoreElements() ;) {
								 RTFData  rtf = (RTFData)e.nextElement();
								 if(rtf.getAliasName().equalsIgnoreCase("Comments")){
								 	IsCommentsPresent = true;
								 	exc.setVRTFs(null);
								 	break;
								 }
								
	 					}
	 					
					}
					// Shingte 070105 
					// Need to handle this differently for Issue (RTF7) and CR (RTF9)
					if(IsCommentsPresent == false){
						logger.debug("RTF Vector Empty for the exception : " + exc.getId() + 
						" Inserting a Comments from Cust RTF to the CR so that updates can be made from the front end");
							
						//RTFData rtf = new RTFData("Comments", "Comments", "", 9 );
						RTFData rtf = new RTFData("Comments", "Comments", "", rank);
						exc.populatevRTF(rtf);
						populateRTFData(exc.getVRTFs(), exc.getId(), ProjectID, exc.getId(), exc.getType());
						
					}
					logger.debug("Extracting info for CRI id :" + exc.getId() + " done");
					*/
				}
				else if(exc.getType().equalsIgnoreCase("CRIFOLDER")){
//										delete crifolder structure and the here i need to insert crifolder
				// populating cri_folders in ets_pmo_issue table. 
				//crifolder is just a folder to me...it is nothing more imp. i am going to add the following elements
				//	the fields imp are : - pmo_id, pmo_project_id, parent_pmo_id, ref_no, title 						
				//This ets_Id for cri_folders are of no use. I am generating bec ets_id is the primary field.										
					String ets_Id = exc.GenerateandRetrieveETSID();	
					/*pop.populateCRIInfo(ets_Id, exc.getId(), ProjectID,parentID,  exc.getReference_Number(), exc.getInfo_Src(),
								null, null, null, null, 
								exc.getStage_id(), null, exc.getStart(), "Class not in xml", exc.getElement_Name(), 
								"severity not in xml", exc.getType(), exc.getRTF1(), null, 
								null,  null, null);*/
					pop.populateProject(exc.getId(), ProjectID, parentID, "CRIFolder", exc.getType(), exc.getReference_Number(), -1, -1,
 							null, null, null, null, null, null,null, null, null, null, null, null,  null, null,null, null, null,
 							 null, null, null, 'Y', null, null, null, null);
 							// duration, work, percent_complete,
 							//rem_Work, EETC, Effort_Spent, constraint, const_Date);
					populateCRIData(exc.getVexceptions(), ProjectID, exc.getId(), exc.getType());
				}
			// handle the documents here
			}
			catch(SQLException e){
				logger.error("SQLException in populating CRI  " + 
 						"Caught in ExtractProjectXMLData:populateCRIData() " + e.getMessage());
			}
 	catch(Exception e){
		 		logger.error("Exception in populating CRI " + 
 						"Caught in ExtractProjectXMLData:populateCRIData()" + e.getMessage());
 				
 				}
 	/*			String docT = "";
 				
 			if(doc.getDoc_Type() == 1) {
 					docT = "DOCUMENTFOLDER"; 	
 			}
 			else if(doc.getDoc_Type() == 2) {
 					docT = "DOCUMENT"; 	
 			}
 		populateDocs(doc.getVDocs(), ProjectID, doc.getId(), docT);
		
		*/
		}
	logger.debug("Populating/Updting the ChangeRequest/Issue Completed");
		return rtrn;
}

/**
 * Returns the isThisNewProject.
 * @return boolean
 */
public boolean isIsThisNewProject() {
	return IsThisNewProject;
}

/**
 * Sets the isThisNewProject.
 * @param isThisNewProject The isThisNewProject to set
 */
public void setIsThisNewProject(boolean isThisNewProject) {
	IsThisNewProject = isThisNewProject;
}

/**
 * @return
 */
public static String getCLASS_VERSION() {
	return CLASS_VERSION;
}
private void populateMyDocIDVector(Vector docVector){
	logger.debug("Populating doc vector for later cleanup of docs that werent synched this time");
	if(docVector == null){
		return ;
	}
	int sz = docVector.size();
	for ( int i = 0; i < sz; i++){
		Doc doc = (Doc) docVector.get(i);
		if(doc.getDoc_Type() == 2){
			if(docIdList == null){
				docIdList = new ArrayList();
			}
			logger.debug("Adding doc id :" + doc.getId() + " doc name : " + doc.getElement_Name() + " to the common doc vector.");
			docIdList.add(doc.getId());
		}
		if(doc.getVDocs() != null){
			populateMyDocIDVector(doc.getVDocs());			
		}
	}
	
	
}
private ArrayList getExtraDocsFromDB(ArrayList vExistingDocsFromDB){
	
	if(docIdList == null){
		return vExistingDocsFromDB; 
	}
	vExistingDocsFromDB.removeAll(docIdList);
	return vExistingDocsFromDB;
	
}

}
