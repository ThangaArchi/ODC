package oem.edge.ets_pmo.xml;

import oem.edge.ets_pmo.common.*;
import oem.edge.ets_pmo.common.mail.PostMan;
import oem.edge.ets_pmo.mq.*;

import oem.edge.ets_pmo.util.*;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;

import org.apache.log4j.Logger;

import oem.edge.ets_pmo.db.populateETS_PMO;
import java.io.*;
import java.util.*;
import java.sql.*;

import java.net.*;

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

public class GenerateIssueCRXML extends GenerateBaseXML {
	private static String CLASS_VERSION = "5.2.1";
	private Vector vAttributes;
	private String Issue_CR_ID = null;
	private String PMO_ISSUE_CR_IDForUpdate = null;
	private Vector ParentCRIFolderVector;
	private String ParentCRIFolderIDforThisCR;
	private String ProblemType = null;
	private String CRI_Root_User_Id = null;
	private String[] CRICreateAttrList;
	private String[] CRIUpdateAttrList;
	private char CR_CREATEDNEW_STATE;
	private char CR_UPDATED_STATE;

	String ets_Id;
	String pmo_Id = "";
	String isCR;
	String ProjectID;
	int refNumber;

	String qName = null; //Ported subu 6/4

	//These attributes have contents needed for vAttributes in the form of STRING$element_name$value$-1
	String ElementNameForXML;
	String PriorityForXML;
	String Stage_IdForXML;
	String Proposed_ByforXML;
	String ProposedDateTimeForXML;
	String DescriptionForXML;
	String COMM_FROM_CUSTForXML;

	String ElementName;
	String Priority;
	String Stage_Id;
	String Proposed_By;

	Timestamp ProposedDateTime;
	String ProposedByCompany;
	String ProposedByEmail;
	String proposedByPhone;
	String Description;
	String COMM_FROM_CUST;
	String user_id;

	String ProposedByIRID;
	//String classS;
	String ownerIR_ID;
	String owner_name;
	String last_userID;

	private populateETS_PMO pop;

	private String TxnID;

	//	ETSPMOGlobalInitialize  Global = null;
	//	ETSPMOCRIGlobalInitialize CRIGlobal =null;

	static Logger logger = Logger.getLogger(GenerateIssueCRXML.class);

	private String CR_CORR_ID;
	public GenerateIssueCRXML() {

		//Global = new ETSPMOGlobalInitialize(propFile);

	}

	public void initialize(String operationType, boolean isCR) {

		/*
		if(Global.getProp() == null){
				Global.Init();	
		}
		
		*/
		this.CRI_Root_User_Id = ETSPMOGlobalInitialize.getCRI_ROOT_USER_Id();

		CR_CORR_ID = ETSPMOGlobalInitialize.getCR_CORR_ID();
		if (operationType.equalsIgnoreCase("CREATE")) {
			this.CR_CREATEDNEW_STATE =
				(
					(String) ETSPMOGlobalInitialize
						.getCR_CREATEDNEW_STATE())
						.charAt(
					0);

			if(isCR == true){ // ChangeRequest case
			this.CRICreateAttrList =
				ETSPMOGlobalInitialize.getCRCreateElements();
			}
			else{ //Issue 
			this.CRICreateAttrList =
				ETSPMOGlobalInitialize.getISSUECreateElements();
			}
			ElementNameForXML = this.CRICreateAttrList[0];
			PriorityForXML = this.CRICreateAttrList[1];
			Stage_IdForXML = this.CRICreateAttrList[2];
			Proposed_ByforXML = this.CRICreateAttrList[3];
			ProposedDateTimeForXML = this.CRICreateAttrList[4];
			DescriptionForXML = this.CRICreateAttrList[5];
			COMM_FROM_CUSTForXML = this.CRICreateAttrList[6];
			user_id = ETSPMOGlobalInitialize.getCRI_ROOT_USER_Id();
		} else if (operationType.equalsIgnoreCase("UPDATE")) {
			this.CR_UPDATED_STATE =
				((String) ETSPMOGlobalInitialize.getCR_UPDATED_STATE()).charAt(
					0);

			if(isCR == true){
			this.CRIUpdateAttrList =
				ETSPMOGlobalInitialize.getCRUpdateElements();
			}else{
			this.CRIUpdateAttrList =
				ETSPMOGlobalInitialize.getISSUEUpdateElements();
			}
			COMM_FROM_CUSTForXML = this.CRIUpdateAttrList[0];
						
		}

		//pop = new populateETS_PMO(log);

	}
	public boolean UpdateIssueCR(
		boolean isCR,
		String ProjectID,
		Vector ParentCRIFolder,
		String ETS_ISSUECRID,
		String ClassInfo,
		String stateAction,
		String comm_from_cust,
		String txnid,
		String pmo_IssueCRID,
		String resourceID,
		String source,
		String destination,
		String repApp,
		String version)
		throws Exception {
	
		initialize("UPDATE", isCR);
		boolean b = true;
		//String str = pop.whatDoesTheStatusFlagSayForThisUserIDandCRID(last_userID, ETS_ISSUECRID);

		//logger.debug("the pop.whatDoesTheStatusFlagSayForThisUserIDandCRID returns : " + str);
		/* The FLAg state in ets_pmo_txn is C/U */
		/*if(str.equalsIgnoreCase("NONEDITABLE")){
			
			return false;	
		}*/

		this.setOperationType("UPDATE");
		
		// subu 4.5.1 fix :added the below line and commented the lines below this line below.
		// To avoid hardcoding CHANGEREQUEST and ISSUE
		//this.setProblemType(ClassInfo);
		if (isCR == true) {
			this.setProblemType("CHANGEREQUEST");
		} else
			this.setProblemType("ISSUE");
		
		
		/* need to do this because decided to send RTFs to Boris in last minute */
		TextToRTFConverter text2rtf = new TextToRTFConverter();
		Base64Encoder encodeRTF = new Base64Encoder();
		comm_from_cust =
			encodeRTF.encode(text2rtf.convertTextToRTF(comm_from_cust));

		this.ParentCRIFolderVector = ParentCRIFolder;
		if (ParentCRIFolder != null) {

			//get me the last element in the vector which is a cri folder who is the parent
			// of this cr
			GenerationSructOfCRIssue gen =
				(GenerationSructOfCRIssue) ParentCRIFolder.get(
					ParentCRIFolder.size() - 1);
			this.ParentCRIFolderIDforThisCR =
				(String) gen.getGenerationParentID();

		}
		this.setSource(source);
		this.setDestination(destination);
		this.setVTransactionXML(version);
		//this.TxnID = ETS_ISSUECRID + System.currentTimeMillis();
		this.TxnID = txnid;
		this.setTransactionID(txnid);
		this.setApp(repApp);
		this.setProject_ID(ProjectID);

		this.setIssue_CR_ID(ETS_ISSUECRID);
		this.setPMO_ISSUE_CR_IDForUpdate(pmo_IssueCRID);
		this.setUserid(resourceID); /*Ported subu 6/4 PR3 */
		//this.setUserid("Boris");
		/*
		if(user_id!=null){
			this.setUserid(user_id);
		}
		*/

		this.ets_Id = ETS_ISSUECRID;

		this.COMM_FROM_CUST = comm_from_cust;

		vAttributes = new Vector();
		// I will be adding the elements in Vecctor vAttributes  as i create the records below.
		String rank;

		rank =
			COMM_FROM_CUSTForXML.substring(
				COMM_FROM_CUSTForXML.lastIndexOf("$"));

		COMM_FROM_CUSTForXML =
			COMM_FROM_CUSTForXML.substring(
				0,
				COMM_FROM_CUSTForXML.lastIndexOf("$") + 1);

		COMM_FROM_CUSTForXML = COMM_FROM_CUSTForXML + comm_from_cust + rank;
		logger.debug("Comm from Cust : " + COMM_FROM_CUSTForXML);
		vAttributes.addElement(COMM_FROM_CUSTForXML);
	/*	ENABLE THIS BOTTOM PIECE OF CODE WHEN THERE ARE CHANGE OF PLANS( TO SEND THE STAGE_ID OR STATUS INFO TO PMOFFICE) 
	 *  WITH THE UPDATE XMLS . 
	 *  The present protocol between PMOffice and E&TS doesnt 
	 * expect any stateAction as the XML attributes for UPDATES
	 */
	/*	
		if(this.getProblemType().equalsIgnoreCase("ISSUE")){
			
			Stage_IdForXML =
			Stage_IdForXML.substring(0, Stage_IdForXML.lastIndexOf("$") + 1);
			Hashtable htPMOIssueStageIDRank = ETSPMOGlobalInitialize.getHtPMOIssueStageIDRank();
			rank = (String)htPMOIssueStageIDRank.get(stateAction);	
			Stage_IdForXML = Stage_IdForXML + stateAction + "$" + rank;
			logger.debug("Stage_Id: " + Stage_IdForXML);
			vAttributes.addElement(Stage_IdForXML);
		}
	*/
		generate();
		print();
		// System.out.println(getXmlMsg());
		//updateCRData();
		//populateNewTransactionData("UPDATE");
		b = SendToPMOMQ();
		if (b)
		{	
		  PostMan p = new PostMan();
		  PostMan.sendCRInfoFromETS(false, isCR, this.getXmlMsg().toString());
		}
		return b;

	}
	public boolean CreateIssueCR(
		boolean isCR,
		String ProjectID,
		int ref,
		Vector ParentCRIFolder,
		String ETS_ISSUECRID,
		String element_name,
		String priority,
		String stage_id,
		String stage_idrank,
		String proposed_by,
		Timestamp proposed_datetime,
		String description,
		String ProposedByCompany,
		String ProposedByEmail,
		String ProposedByPhone,
		String ProposedByIRID,
		String ClassInfo,
		String ownerIR_ID,
		String owner_name,
		String last_userID,
		String comm_from_cust,
		String txnid,
		String ResourceID,
		String source,
		String destination,
		String repositoryApp,
		String version)
		throws Exception {

	
		initialize("CREATE", isCR);
		boolean b = true;
		//String str = pop.whatDoesTheStatusFlagSayForThisUserIDandCRID(last_userID, ETS_ISSUECRID);

		//logger.debug("the pop.whatDoesTheStatusFlagSayForThisUserIDandCRID returns : " + str);
		/* The FLAg state in ets_pmo_txn is C/U */
		/*if(str.equalsIgnoreCase("NONEDITABLE")){
			
			return false;	
		}*/

		this.setOperationType("CREATE");

		//subu 4.5.1 fix :added the below line and commented the lines below this line below.
		// To avoid hardcoding CHANGEREQUEST and ISSUE
		//this.setProblemType(ClassInfo);
		
		if (isCR == true) {
			this.setProblemType("CHANGEREQUEST");
		} else
			this.setProblemType("ISSUE");
		
		
		this.ParentCRIFolderVector = ParentCRIFolder;

		if (ParentCRIFolder != null) {

			//get me the last element in the vector which is a cri folder who is the parent
			// of this cr
			GenerationSructOfCRIssue gen =
				(GenerationSructOfCRIssue) ParentCRIFolder.get(
					ParentCRIFolder.size() - 1);
			this.ParentCRIFolderIDforThisCR =
				(String) gen.getGenerationParentID();

		}

		/* need to do this because decided to send RTFs to Boris in last minute */
		TextToRTFConverter text2rtf = new TextToRTFConverter();
		Base64Encoder encodeRTF = new Base64Encoder();
		description = encodeRTF.encode(text2rtf.convertTextToRTF(description));
		comm_from_cust =
			encodeRTF.encode(text2rtf.convertTextToRTF(comm_from_cust));

		this.setSource(source);
		this.setDestination(destination);
		this.setApp(repositoryApp);
		this.setVTransactionXML(version);
		//this.TxnID = ETS_ISSUECRID + System.currentTimeMillis();
		this.TxnID = txnid;
		this.setTransactionID(txnid);

		this.setProject_ID(ProjectID);
		this.refNumber = ref;
		this.setIssue_CR_ID(ETS_ISSUECRID);

		this.setUserid(ResourceID);
		logger.debug(
			"The user id that will be the owner of the CR/Issue: "
				+ ResourceID);

		this.ets_Id = ETS_ISSUECRID;

		this.ProjectID = ProjectID;
		this.Stage_Id = stage_id;

		this.ElementName = handleSpecialChars(element_name);
		logger.debug(
			" The element name : after calling handleSpecialChars(element_name) is : "
				+ this.ElementName);
		this.Priority = priority;
		this.Description = description;
		this.Proposed_By = proposed_by;
		this.ProposedDateTime = proposed_datetime;
		this.ProposedByCompany = ProposedByCompany;
		this.ProposedByEmail = ProposedByEmail;
		this.proposedByPhone = ProposedByPhone;
		this.ProposedByIRID = ProposedByIRID;
	//	this.classS = classS;
		this.ownerIR_ID = ownerIR_ID;
		this.owner_name = owner_name;
		this.last_userID = last_userID;
		vAttributes = new Vector();
		// I will be adding the elements in Vecctor vAttributes  as i create the records below.
		String rank;

		rank = ElementNameForXML.substring(ElementNameForXML.lastIndexOf("$"));
		//	System.out.println("rank: " + rank);
		ElementNameForXML =
			ElementNameForXML.substring(
				0,
				ElementNameForXML.lastIndexOf("$") + 1);
		//	System.out.println("El : " + ElementName);
		ElementNameForXML = ElementNameForXML + this.ElementName + rank;
		logger.debug("Element_name: " + ElementNameForXML);
		vAttributes.addElement(ElementNameForXML);

		rank = PriorityForXML.substring(PriorityForXML.lastIndexOf("$"));
		PriorityForXML =
			PriorityForXML.substring(0, PriorityForXML.lastIndexOf("$") + 1);
		PriorityForXML = PriorityForXML + priority + rank;
		logger.debug("Priority: " + PriorityForXML);
		vAttributes.addElement(PriorityForXML);

		rank = Stage_IdForXML.substring(Stage_IdForXML.lastIndexOf("$"));
		Stage_IdForXML =
			Stage_IdForXML.substring(0, Stage_IdForXML.lastIndexOf("$") + 1);
		if(this.getProblemType().equalsIgnoreCase("ISSUE")){
				rank="4809";	
		}
		
		Stage_IdForXML = Stage_IdForXML + stage_id + "$" + rank;
		logger.debug("Stage_Id: " + Stage_IdForXML);
		vAttributes.addElement(Stage_IdForXML);

		rank = Proposed_ByforXML.substring(Proposed_ByforXML.lastIndexOf("$"));
		Proposed_ByforXML =
			Proposed_ByforXML.substring(
				0,
				Proposed_ByforXML.lastIndexOf("$") + 1);
		Proposed_ByforXML = Proposed_ByforXML + proposed_by + rank;
		logger.debug("Proposed_By: " + Proposed_ByforXML);
		vAttributes.addElement(Proposed_ByforXML);

		rank =
			ProposedDateTimeForXML.substring(
				ProposedDateTimeForXML.lastIndexOf("$"));
		ProposedDateTimeForXML =
			ProposedDateTimeForXML.substring(
				0,
				ProposedDateTimeForXML.lastIndexOf("$") + 1);
		ProposedDateTimeForXML =
			ProposedDateTimeForXML + proposed_datetime.toString() + rank;
		logger.debug("ProposedDateTime: " + ProposedDateTimeForXML);
		vAttributes.addElement(ProposedDateTimeForXML);

		rank = DescriptionForXML.substring(DescriptionForXML.lastIndexOf("$"));
		DescriptionForXML =
			DescriptionForXML.substring(
				0,
				DescriptionForXML.lastIndexOf("$") + 1);
		DescriptionForXML = DescriptionForXML + description + rank;
		logger.debug("Description: " + DescriptionForXML);
		vAttributes.addElement(DescriptionForXML);

		rank =
			COMM_FROM_CUSTForXML.substring(
				COMM_FROM_CUSTForXML.lastIndexOf("$"));
		COMM_FROM_CUSTForXML =
			COMM_FROM_CUSTForXML.substring(
				0,
				COMM_FROM_CUSTForXML.lastIndexOf("$") + 1);
		COMM_FROM_CUSTForXML = COMM_FROM_CUSTForXML + comm_from_cust + rank;
		logger.debug("Comm from Cust : " + COMM_FROM_CUSTForXML);
		vAttributes.addElement(COMM_FROM_CUSTForXML);

		generate();
		print();
		//  System.out.println("********************************");
		//  System.out.println(getXmlMsg());
		//  System.out.println("********************************");							    
		//populateCRData();

		//populateNewTransactionData("CREATE");

		b = SendToPMOMQ();
		//	public static void sendCRInfoFromETS(boolean isCreate, String content){
		if (b)
		{
			PostMan p = new PostMan();
			PostMan.sendCRInfoFromETS(true, isCR, this.getXmlMsg().toString());
		}
		return b;
	}

	private boolean checkIssueCRCreateData() {

		boolean success = true;
		success = super.checkData();
		if (getProblemType() == null) {
			logger.warn(" Problem type: Issue/CR Unassigned");
			success = false;
		}
		if (getIssue_CR_ID() == null) {
			logger.warn(" Issue/CR id unassigned");
			success = false;
		}

		if (vAttributes == null) {
			logger.warn(
				"Attributes Vector empty. Vector needs to be populated.");
			success = false;
		}
		return success;
	}
	
	/*
	private void CRCreate() {

		setSource("ETS");
		setDestination("PMOFFICE");
		setVTransactionXML("1.0");
		setTransactionID("TTRRAANNSSAACCTTIIOONNIIDD");
		setOperationType("CREATE");
		setProject_ID("PPRROOJJEECCTTIIDD");
		setProblemType("CR");
		setIssue_CR_ID("WE4545SDSFGSDFDSSDFGSDFGSDFG");
		setUserid("PMO_SUPERVISOR");
		setApp("PMOFFICE");
		vAttributes = new Vector();
		//       vAttributes has data of type type$name$value$rank
		vAttributes.addElement("STRING$ELEMENT_NAME$Taskof the day$-1");
		vAttributes.addElement("INTEGER$PRIORITY$500$-1");
		vAttributes.addElement("STRING$STAGE_ID$Specifications Assessment$741");
		vAttributes.addElement("STRING$PROPOSED_BY$Marie Curie$-1");
		vAttributes.addElement("DATE$PROPOSED_DATETIME$2004-03-23$-1");
		vAttributes.addElement("RTF$DESCRIPTION$adfsfdsdfsfd$1");

	}
	private void IssueCreate() {

		setSource("ETS");
		setDestination("PMOFFICE");
		setVTransactionXML("1.0");
		setTransactionID("TTRRAANNSSAACCTTIIOONNIIDD");
		setOperationType("CREATE");
		setProject_ID("PPRROOJJEECCTTIIDD");
		setProblemType("ISSUE");
		setIssue_CR_ID("WE4545SDSFGSDFDSSDFGSDFGSDFG");
		setUserid("PMO_SUPERVISOR");
		setApp("PMOFFICE");
		vAttributes = new Vector();
		//       vAttributes has data of type type$name$value$rank
		vAttributes.addElement("STRING$ELEMENT_NAME$Taskof the day$-1");
		vAttributes.addElement("INTEGER$PRIORITY$500$-1");
		vAttributes.addElement("STRING$STAGE_ID$Specifications Assessment$741");
		vAttributes.addElement("STRING$PROPOSED_BY$Marie Curie$-1");
		vAttributes.addElement("DATE$PROPOSED_DATETIME$2004-03-23$-1");
		vAttributes.addElement("RTF$DESCRIPTION$Hello How are you$1");

	}
	private void IssueUpdate() {

		setSource("ETS");
		setDestination("PMOFFICE");
		setVTransactionXML("1.0");
		setTransactionID("TTRRAANNSSAACCTTIIOONNIIDD");
		setOperationType("UPDATE");
		setProject_ID("PPRROOJJEECCTTIIDD");
		setProblemType("ISSUE");
		setIssue_CR_ID("WE4545SDSFGSDFDSSDFGSDFGSDFG");
		setUserid("PMO_SUPERVISOR");
		setApp("PMOFFICE");
		vAttributes = new Vector();
		vAttributes.addElement(
			"RTF$Comments from Cutomer$2. My opinion is to do it this way$7");

	}
	private void CRUpdate() {

		setSource("ETS");
		setDestination("PMOFFICE");
		setVTransactionXML("1.0");
		setTransactionID("TTRRAANNSSAACCTTIIOONNIIDD");
		setOperationType("UPDATE");
		setProject_ID("PPRROOJJEECCTTIIDD");
		setProblemType("CR");
		setIssue_CR_ID("WE4545SDSFGSDFDSSDFGSDFGSDFG");
		setUserid("PMO_SUPERVISOR");
		setApp("PMOFFICE");
		vAttributes = new Vector();
		vAttributes.addElement(
			"RTF$Comments from Customer$2. My opinion is to do it this way$9");

	}

	private void loadIssueCRCreateData() {
		CRCreate();
		//CRUpdate();
		//IssueCreate();
		//IssueUpdate();
	}
	
	*/

	/*
		 * this.ParentCRIFolderVectorIds has crifolder  and proposal/project with highest parent in the first element in vector
		 * 													second highest parent in the second element vector
		 * 													lowest parent in the last element vector
		 * 
		 * Use recursion
		 */
	public Element populatecrifolder(Element object, int recordNo) {
		boolean b = true;
		Element crifolder, crifolderid;
		crifolder = super.getDoc().createElement("object");
		GenerationSructOfCRIssue gen =
			(GenerationSructOfCRIssue) this.ParentCRIFolderVector.get(recordNo);
		crifolder.setAttribute("type", gen.getGenerationParentType());
		crifolderid = super.getDoc().createElement("ID");

		crifolderid.appendChild(
			super.getDoc().createTextNode(
				(String) gen.getGenerationParentID()));

		crifolder.appendChild(super.getDoc().createTextNode("\n"));
		crifolder.appendChild(crifolderid);
		crifolder.appendChild(super.getDoc().createTextNode("\n"));
		object.appendChild(crifolder);
		if (recordNo < this.ParentCRIFolderVector.size() - 1) {
			int nextone = recordNo + 1;

			populatecrifolder(crifolder, nextone);
		} else {
			b = generateObjectIssue_or_CR(crifolder);

		}
		if (b == true) {
			return object;
		} else
			return null;
	}

	public boolean generateObjectProject(Element main) {
		boolean b = true;
		Element projId, object, crifolder, crifolderid;
		object = super.getDoc().createElement("object");
		object.setAttribute("type", "PROJECT");
		object.appendChild(super.getDoc().createTextNode("\n"));

		projId = super.getDoc().createElement("id");
		projId.appendChild(super.getDoc().createTextNode(getProject_ID()));
		object.appendChild(projId);
		object.appendChild(super.getDoc().createTextNode("\n\n"));

		/*	crifolder =  super.getDoc().createElement("object");
				crifolder.setAttribute("type", "CRIFOLDER");
				crifolderid  = super.getDoc().createElement("ID");
				//crifolderid.appendChild(super.getDoc().createTextNode("should i get this id ffrom db for CRUPDATE?"));
				//crifolderid.appendChild(super.getDoc().createTextNode("\n\n"));
				//crifolder.appendChild(crifolderid);
				crifolder.appendChild(super.getDoc().createTextNode((String)this.ParentCRIFolderVectorIds.get(0)));
				crifolder.appendChild(super.getDoc().createTextNode("\n\n"));
			
			
			
			object.appendChild(crifolder);*/
		//start recurisng and adding crifolders in the object element
		// if i have crifolder structure, then i go here

		if (this.ParentCRIFolderVector != null) {
			/*Ported subu 6/3 add PR0 6/3.. the if loop */

			if (this.ParentCRIFolderVector.size() > 0) {
				populatecrifolder(object, 0);
			} else { // if i dont have any crifolder structure, then i go in the else block. I add the cr directly to the project

				b = generateObjectIssue_or_CR(object);
				if (b == false)
					return b;

			}
		} else /* This else loop is accessed when there is no folders available for the issue/cr. The cr/issue is sent without
						any wrap arounds as in the case in if loop 
						*/ { /*Ported subu add PR0 6/3.. the else loop */
			b = generateObjectIssue_or_CR(object);
			if (b == false)
				return b;
		}

		main.appendChild(object);
		main.appendChild(super.getDoc().createTextNode("\n\n"));

		//generateObjectIssue_or_CR(crifolder);
		return b;
	}

	private boolean generateObjectIssue_or_CR(Element main) {
		Element id, sourceid, object, root, item;
		Attr attributeNodeType;

		//the Top element

		object = super.getDoc().createElement("object");
		object.setAttribute("type", ProblemType);
		object.appendChild(super.getDoc().createTextNode("\n\n"));
		main.appendChild(object);
		main.appendChild(super.getDoc().createTextNode("\n"));

		if (this.getOperationType().equalsIgnoreCase("UPDATE")) {
			id = super.getDoc().createElement("id");
			id.appendChild(
				super.getDoc().createTextNode(getPMO_ISSUE_CR_IDForUpdate()));
			object.appendChild(id);
			object.appendChild(super.getDoc().createTextNode("\n"));
		}

		sourceid = super.getDoc().createElement("sourceid");
		sourceid.appendChild(super.getDoc().createTextNode(getIssue_CR_ID()));
		object.appendChild(sourceid);
		object.appendChild(super.getDoc().createTextNode("\n\n"));

		String type = "";
		String name = "";
		String value = "";
		String rank = "";
		try {
			for (int i = 0; i < vAttributes.size(); i++) {
				//extract data from the vector
				String data = (String) vAttributes.elementAt(i);
				StringTokenizer st = new StringTokenizer(data, "$");

				//should check whether there is data not implemented            
				type = st.nextToken().trim();
				if (logger.isDebugEnabled()) {
					logger.debug("generateObjectIssue_or_CR(Element) - type"
							+ type);
				}
				name = st.nextToken().trim();
				if (logger.isDebugEnabled()) {
					logger.debug("generateObjectIssue_or_CR(Element) - name"
							+ name);
				}
				value = st.nextToken().trim();
				if (logger.isDebugEnabled()) {
					logger.debug("generateObjectIssue_or_CR(Element) - value"
							+ value);
				}
				rank = st.nextToken().trim();
				if (logger.isDebugEnabled()) {
					logger.debug("generateObjectIssue_or_CR(Element) - rank"
							+ rank);
				}
				/* 4.5.1 fix : Doing some manipulation for STAGE_ID
				 * Boris has asked me to swap value and rank. Thats how the code works now???
				 * Boris says that the present mapping is 
				 * 	740,"None"
					741,"Request for Quotation"
					742,"Request for Proposal"
					743,"Value Proposition"
					744,"Short Listed"
					745,"Selected"
					746,"In Budget Approval"
					747,"Contract Review"
					748,"Awarded"
					749,"Lost"
					4899,"In Progress"
					4948,"To be validated"
					4949,"Closed"
					As numbers map to the value, Boris can use the number from the xml to get the value
					I will have to swap value and rank for STAGE_ID alone to achieve this. 
				 */
				 if(name.equalsIgnoreCase("STAGE_ID")){
				 	String temp = value;
				 	value 	= 	rank ;
				 	rank	=	temp;
				 }
				root = super.getDoc().createElement("attribute");
				root.setAttribute("type", type);
				root.appendChild(super.getDoc().createTextNode("\n"));

				//add name Element
				item = super.getDoc().createElement("name");
				item.appendChild(super.getDoc().createTextNode(name));
				root.appendChild(item);
				root.appendChild(super.getDoc().createTextNode("\n"));

				//add value Element
				item = super.getDoc().createElement("value");

				if (!value.equalsIgnoreCase("NULL")) {
					item.appendChild(super.getDoc().createTextNode(value));
				}
				root.appendChild(item);
				root.appendChild(super.getDoc().createTextNode("\n"));

				//add rank Element if it exists
				if (rank != null) {
					item = super.getDoc().createElement("rank");
					item.appendChild(super.getDoc().createTextNode(rank));
					root.appendChild(item);
					root.appendChild(super.getDoc().createTextNode("\n"));
				}

				//add to the Top Element

				object.appendChild(root);
				object.appendChild(super.getDoc().createTextNode("\n\n"));
			} //end of for
		} catch (Exception e) {
			logger.error(getStackTrace(e));
			System.exit(0);
			return false;
		}

		return true;
	} //end of generateObjectIssue_or_CR

	private String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		String stackTrace = sw.toString();
		pw.close();
		return stackTrace;
	}

	public void print() {
		super.print();
	}
	public static void main(String args[]) {
		if (args.length < 1) {
			if (logger.isDebugEnabled()) {
				logger
						.debug("main(String) -  Usage : GenerateIssueCRXML <PropertyFileLocation> ");
			}
			System.exit(0);
		}

		Vector CRIFolderIds = new Vector();
		CRIFolderIds.add("7BEFBDFD8F284AF4B53886DB9022EBDD");
		CRIFolderIds.add("B915C4347D764E23A6F753A45F03A656");
		CRIFolderIds.add("374A743BCE324FF7B9DF705040992160");
		if (logger.isDebugEnabled()) {
			logger.debug("main(String) - 0th element" + CRIFolderIds.get(0));
		}
		if (logger.isDebugEnabled()) {
			logger.debug("main(String) - 1th element" + CRIFolderIds.get(1));
		}
		if (logger.isDebugEnabled()) {
			logger.debug("main(String) - 2th element" + CRIFolderIds.get(2));
		}
		//	System.out.println("3th element" + CRIFolderIds.get(3));
		GenerateIssueCRXML gxml = null;

		try {
			/*Ported subu 6/4*/
			gxml = new GenerateIssueCRXML();
			/*Ported subu 6/4 next 4 lines*/
			if (args.length == 1) {
				gxml.qName = args[0];
			}
			//gxml."\nUsage: GenerateIssueCRXML <qToName>... Sends to the q in property file if arg is empty\n",gxml.log.DEBUG_LEVEL1);
			/* Option 1 : Tested*/

			gxml.CreateIssueCR(
				true,
				"80F066227B9B4236B08F61140F2DCC68",
				2004,
				CRIFolderIds,
				"ETSISSUEIDSUBS",
				"IHAVE ANAME",
				"300",
				"stage id",
				"400",
				"Subu sundaram",
				Timestamp.valueOf("2004-02-03 12:23:12.000000"),
				"This is description for this CR",
				"proposedbycom",
				"mail@us,vm",
				"324-234-2344",
				"subbus",
				"CHANGEREQUEST",
				"owneridid",
				"ownername",
				"last_userid",
				"My comments arent imp.",
				"txnid",
				"Some resource id",
				"source",
				"destination",
				"app",
				"1.0");

			/* Option 2 : Tested*/
			/*	gxml.CreateIssueCR(true, "80F066227B9B4236B08F61140F2DCC68",2004, null, "ETSISSUEIDSUBS", "IHAVE ANAME", 
												"300","stage id","400", "Subu sundaram", Timestamp.valueOf("2004-02-03 12:23:12.000000"),
												"This is description for this CR", "proposedbycom", "mail@us,vm","324-234-2344", "subbus",
												"classs", "owneridid", "ownername", "last_userid", "some of my comments");
						*/
			/* Option 3 : Tested*/
			/*	gxml.UpdateIssueCR(true, "80F066227B9B4236B08F61140F2DCC68", null, "ETSISSUEIDSUBS", "This proj goes for ever");											
			
			*/
			/* Option 4 : Tested*/
			//gxml.UpdateIssueCR(true, "80F066227B9B4236B08F61140F2DCC68", CRIFolderIds, "ETSISSUEIDSUBS", "This proj goes for ever");											

		} catch (SQLException sqle) {
			logger.error("main(String)", sqle);
		} catch (Exception e) {
			logger.error("main(String)", e);
		}

		// gxml.CreateHexInetAddress();

	} //end of main

	public String CreateHexInetAddress() {
		try {
			InetAddress inet = InetAddress.getLocalHost();
			byte[] bytes = inet.getAddress();
			getInt(bytes);
		} catch (UnknownHostException un) {
			logger.error("CreateHexInetAddress()", un);
		}

		return null;
		//    	String hexInetAddress = 
	}
	public int getInt(byte[] bytes) {
		String b = new String(bytes);
		Integer i = new Integer(b);
		//System.out.println(i.intValue());
		return i.intValue();

	}

	/**
	 * Returns the issue_CR_ID.
	 * @return String
	 */
	public String getIssue_CR_ID() {
		return Issue_CR_ID;
	}

	/**
	 * Sets the issue_CR_ID.
	 * @param issue_CR_ID The issue_CR_ID to set
	 */
	public void setIssue_CR_ID(String issue_CR_ID) {
		Issue_CR_ID = issue_CR_ID;
	}

	/**
	 * Returns the problemType.
	 * @return String
	 */
	public String getProblemType() {
		return ProblemType;
	}

	/**
	 * Sets the problemType.
	 * @param problemType The problemType to set
	 */
	public void setProblemType(String problemType) {
		ProblemType = problemType;
	}

	/**
	 * Returns the vAttributes.
	 * @return Vector
	 */
	public Vector getVAttributes() {
		return vAttributes;
	}

	/**
	 * Sets the vAttributes.
	 * @param vAttributes The vAttributes to set
	 */
	public void setVAttributes(Vector vAttributes) {
		this.vAttributes = vAttributes;
	}
/*
	public void populateCRData() throws SQLException, Exception {

		pop.populateCRIInfo(
			this.ets_Id,
			this.pmo_Id,
			this.ProjectID,
			this.ParentCRIFolderIDforThisCR,
			this.refNumber,
			this.CR_CREATEDNEW_STATE,
			this.Proposed_By,
			this.ProposedByCompany,
			this.ProposedByEmail,
			this.proposedByPhone,
			this.Stage_Id,
			this.ProposedByIRID,
			this.ProposedDateTime,
			this.classS,
			this.ElementName,
			this.Priority,
			"CR",
			this.Description,
			this.COMM_FROM_CUST);
	}
	*/
	public void updateCRData() throws SQLException, Exception {
		pop.updateCRIInfoFromETS(this.ets_Id, this.COMM_FROM_CUST);

	}
	/*
	 * NOTE : Change required. Something wrong with ID in ets.ets_pmo_txn table. Hence i am using
	 * PMO_PROJ_ID field . When u change pop.populatePMOTransaction() back to the normal code when ID field is rectified. 
	 * Make sure that u also change pop.deleteCRI()
	 * 
	 * change 2: I am using last_userid in the Type field. This is to identify who is the last person involved with 
	 * a transaction. If i happen to get a NACK or TIMEOUT for a record, I will need to find a way to notify
	 * the customer with his last_user id. Notification will be in phani's code
	 * After notification, phani will have to delte the record.
	 * 	
	 * 
	 */
	public boolean populateNewTransactionData(String actiontype)
		throws SQLException, Exception {
		boolean rslt = true;
		//System.out.println("ets_id" + ets_Id);
		//System.out.println("pmo_id" + pmo_Id);
		//System.out.println("proj" + ProjectID);
		logger.debug("Performing : " + actiontype + " on the TXN table");
		if (actiontype.equalsIgnoreCase("CREATE")) {
			logger.debug("Creating a new record in the transaction table");
			//pop.deleteCRI(this.ets_Id);
			pop.populatePMOTransaction(
				this.ets_Id,
				this.TxnID,
				this.ProjectID,
				this.last_userID,
				this.CR_CREATEDNEW_STATE);
		} else if (actiontype.equalsIgnoreCase("UPDATE")) {
			logger.debug(
				"Updating the record with A in the transaction table. \nThe frist step to achieve it is deleting the record if there is A and inserting new record with same ets_id. \nThis is to simulate updating");
			//pop.deleteCRIwithFlag(this.ets_Id, 'A');// I am deleting the ids with ack and replacing the record
			pop.populatePMOTransaction(
				this.ets_Id,
				this.TxnID,
				this.ProjectID,
				this.last_userID,
				this.CR_UPDATED_STATE);
		} else
			rslt = false;
		return rslt;
	}
	
	
	//
	// ReSendIssueCRXML
	//
	public static int ReSendIssueCRXML(String TxnID){
		int returnValue = 0;
		PMOMQSend one = null;
		String XmlMsg = null;
		boolean doResend = true;
		boolean b = false; // whether sending is successfully done in this resend
		try {
			
			XmlMsg = retrieveXMLFileFromDisk(TxnID); //this method is in the parent class
			String retrialNo = retrieveRetrialNumberFromDisk(TxnID);
			int i = -1;
			if(retrialNo != null && XmlMsg != null){
				i = Integer.parseInt(retrialNo.trim());
				if(i == 10){// I only resend 10 times
					doResend = false;
					returnValue = 1;
				}
				if(doResend == true){
					i +=1;
					writeReSendRetrialNumberToDisk(TxnID, i);
				}
			}
			else 
				doResend = false;
			
			if(doResend == true){
				logger.debug(
						"Re-Sending MQMessage : "
					+ "\n CorrID: "
					+ ETSPMOGlobalInitialize.getCR_CORR_ID()
					+ "\n MessageID: "
					+ TxnID
					+ "\n Message: "
					+ XmlMsg);
				/*Ported subu 6/4 this call*/
			
				one = new PMOMQSend();
				b =	one.sendMQMessage(
					null,
					ETSPMOGlobalInitialize.getCR_CORR_ID(),
					TxnID,
					XmlMsg,
					true,
					true,
					true);
				if(b == false){
					returnValue = -1;
				}
			}//end doResend == true
		} catch (Throwable e) {
			logger.error("ReSendIssueCRXML(String)", e);
			logger.error(
				"Error sending the MQMEssage  :"
					+ "\n CorrID: "
					+ ETSPMOGlobalInitialize.getCR_CORR_ID()
					+ "\nMessageID: "
					+ TxnID
					+ "\nMessage: "
					+ XmlMsg);
			returnValue = -1;
			
		} finally {
			if (one != null)
				one.cleanup();
			/* -- need to update flag as well, if delete file
			if (b)
			{
				deleteXMLFileFromDisk(TxnID);
			}
			*/
			
		}
		
		return returnValue;
	}
	
	public boolean SendToPMOMQ() {
		boolean b = true;

		PMOMQSend one = null;

		try {
			one = new PMOMQSend();

			logger.info(
				"Sending MQMessage : "
					+ "\n CorrID: "
					+ ETSPMOGlobalInitialize.getCR_CORR_ID()
					+ "\n MessageID: "
					+ this.TxnID
					+ "\n Message: "
					+ this.getXmlMsg());
			/*Ported subu 6/4 this call*/
			//Commenting this for now
			b =
				one.sendMQMessage(
					qName,
					this.CR_CORR_ID,
					this.TxnID,
					this.getXmlMsg().toString(),
					true,
					true,
					true);
					
		if (logger.isInfoEnabled()) {
			logger.info("SendToPMOMQ() - Value for b im SendToPMOMQ" + b);
		}
		} catch (Throwable e) {
			logger.error("SendToPMOMQ()", e);
			logger.error(
				"Error sending the MQMEssage  :"
					+ "\n CorrID: "
					+ ETSPMOGlobalInitialize.getCR_CORR_ID()
					+ "\nMessageID: "
					+ this.TxnID
					+ "\nMessage: "
					+ this.getXmlMsg());
			b = false;
		} finally {
			one.cleanup();
			/*
			if (b==false)
			{
				super.writeXMLFileToDisk(this.TxnID, this.getProblemType());
				super.writeReSendRetrialNumberToDisk(this.TxnID, 1);
			}
			*/
		}

		return b;
	}

	/**
	 * Returns the pMO_ISSUE_CR_IDForUpdate.
	 * @return String
	 */
	public String getPMO_ISSUE_CR_IDForUpdate() {
		return PMO_ISSUE_CR_IDForUpdate;
	}

	/**
	 * Sets the pMO_ISSUE_CR_IDForUpdate.
	 * @param pMO_ISSUE_CR_IDForUpdate The pMO_ISSUE_CR_IDForUpdate to set
	 */
	public void setPMO_ISSUE_CR_IDForUpdate(String pMO_ISSUE_CR_IDForUpdate) {
		PMO_ISSUE_CR_IDForUpdate = pMO_ISSUE_CR_IDForUpdate;
	}

	/**
	 * @return
	 */
	public static String getCLASS_VERSION() {
		return CLASS_VERSION;
	}

} //end of generateIssueCRXML
