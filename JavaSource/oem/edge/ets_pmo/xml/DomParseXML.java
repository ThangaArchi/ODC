package oem.edge.ets_pmo.xml;

import oem.edge.ets_pmo.common.*;
import oem.edge.ets_pmo.common.mail.PostMan;
import oem.edge.ets_pmo.datastore.*;
import oem.edge.ets_pmo.datastore.project.*;
import oem.edge.ets_pmo.datastore.sc.*;
import oem.edge.ets_pmo.datastore.resource.*;
import oem.edge.ets_pmo.datastore.document.*;
import oem.edge.ets_pmo.datastore.exception.*;
import oem.edge.ets_pmo.datastore.project.wbs.*;
import oem.edge.ets_pmo.datastore.project.wbs.deliverable.*;
import oem.edge.ets_pmo.datastore.project.wbs.milestone.*;
import oem.edge.ets_pmo.datastore.project.wbs.summarytask.*;
import oem.edge.ets_pmo.datastore.project.wbs.task.*;
import oem.edge.ets_pmo.datastore.project.wbs.workProduct.*;
import oem.edge.ets_pmo.datastore.util.RTFData;
import oem.edge.ets_pmo.db.*;
import oem.edge.ets_pmo.mq.XMLProcessor;
import oem.edge.ets_pmo.util.FileParser;
import java.io.*;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.xerces.parsers.DOMParser;
import org.xml.sax.ErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import java.io.IOException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
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
public class DomParseXML implements ErrorHandler {
	private static String CLASS_VERSION = "4.5.1";
	static Logger logger = Logger.getLogger(DomParseXML.class);

	private int TabCounter = 0;
	
	private Node transaction;
	private Node operation;
	private Node project = null;

	private  Transaction trans;
	private  Operation oper;
	
	



	private boolean SAXWarningFlag = false;
	private boolean SAXErrorFlag = false;
	private boolean SAXFatalErrorFlag = false;

	private  GenerateBaseXML baseXML;
	public  GenerateProjectCreateUpdateAckXML ackXML;
	public  GenerateProjectCreateUpdateNackXML nackXML;

	private static String SAX_ERROR_CODE;
	private static String SAX_WARNING_CODE;
	private static String SAX_FATALERROR_CODE;
	private static String MISSING_ID_CODE;
	private static String SUCCESS_CODE;

	private static boolean SAXProblemRegistered = false;

	private static boolean NACK = false;

	private static Resource ResourceForMail;
	private String xmlFile;
	
	private String unknownUserID = null;
	private int xmlValidation = 0;
	private  DOMParser parser =null;
	public static String memoryUsageLoggingFile = null;
	private void initialize() {


		baseXML = new GenerateBaseXML();

		baseXML.registerTransactionData(this.trans);

		nackXML = new GenerateProjectCreateUpdateNackXML();

		ackXML = new GenerateProjectCreateUpdateAckXML();

		SAX_ERROR_CODE = ETSPMOGlobalInitialize.getSAX_ERROR_CODE();
		SAX_WARNING_CODE = ETSPMOGlobalInitialize.getSAX_WARNING_CODE();
		SAX_FATALERROR_CODE = ETSPMOGlobalInitialize.getSAX_FATALERROR_CODE();
		MISSING_ID_CODE = ETSPMOGlobalInitialize.getMISSING_ID_CODE();
		SUCCESS_CODE = ETSPMOGlobalInitialize.getSUCCESS_CODE();
		unknownUserID = ETSPMOGlobalInitialize.getUnknownUserId();
		ResourceForMail = new Resource();
		
		xmlValidation = ETSPMOGlobalInitialize.getXmlValidation();

	}

	public DomParseXML() {
		initialize();
	}
	public void cleanUpParser(){
		parser = null;
	}
	public int Parse(String xmlFileN, populateETS_PMO pop) throws Exception {
		/*  subu garbage */
		
		/* sub ugarbage */
		boolean RSLT = true;
		trans = new Transaction();
		oper = new Operation();
		Project proj = new Project();
	//	proj = new Project();
		logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Before calling new DOMParser() )");
		printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Before calling new DOMParser() )");
		parser = new DOMParser();
		logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling new DOMParser() )");
		printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling new DOMParser() )");
		xmlFile = xmlFileN;
		logger.info(
			"\n\n***********" + "The file to parse is: " + xmlFile
			);

		// Turn Validation ON
		if(xmlValidation == 1){
				try {
					parser.setFeature("http://xml.org/sax/features/validation", true);
				} catch (SAXNotRecognizedException e) {
					//catch the exception
					logger.error(getStackTrace(e));
					logger.error(getStackTrace(e));
				} catch (SAXNotSupportedException e) {
					//catch the exception
					logger.error(getStackTrace(e));
					logger.error(getStackTrace(e));
				}

		}
				// Register Error Handler

		parser.setErrorHandler(this);
		try {

			parser.parse(xmlFile);
			/* subu 6/4 remove the system .out*/
			logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling parser.parse )");
			printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling parser.parse )");

			Document document = parser.getDocument();
			logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleDocument-ROOT XML )");
			printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleDocument-ROOT XML )");
			handleDocument(document, proj);
			logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleDocument-ROOT XML )");
			printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleDocument-ROOT XML )");
			if (project == null) {
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleTransaction() )");
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleTransaction() )");
				handleTransaction();
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleTransaction() )");
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleTransaction() )");
				// I always parse till transaction object even if i get the saxparseexception. What happens before
				//transaction...i dont know
				if (this.SAXErrorFlag == true
					|| this.SAXFatalErrorFlag == true
					|| this.SAXWarningFlag == true) {

					SAXProblemRegistered = true;
					//I need to retrieve project id. So i wont return here.
					// I will return in handleProject(proj)

				}
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleOperatation() )");
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleOperatation() )");
				handleOperation(proj);
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleOperatation() )");
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleOperatation() )");
			}
			logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleProject() )");
			boolean b = handleProject(proj);
			logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleProject() )");
			if (b == false) {
				nackXML.setCorrId(ETSPMOGlobalInitialize.getPROJ_NACK_CORR_ID());
				nackXML.setMessId(this.MISSING_ID_CODE + System.currentTimeMillis());
				//I ahve commented to not send NACK while testing */
				boolean g = getReadyToSendMQNack();
				if(g ==  true) return 1;
				else return 0;
				
			}
			
			if (this.oper.getOperationType().equalsIgnoreCase("ACK")
				|| this.oper.getOperationType().equalsIgnoreCase("NACK")) {
				

				try {
					if (this.NACK == true) {
						logger.error(
							"DomParseXML received a NACK with the transaction id: "
								+ trans.getTransactionID());
						String flagState = (String)pop.RetrieveFlagStatusofPCR(trans.getTransactionID());
						if(flagState == null){
							logger.error("Received a NACK for a PCR which doesnt exist in this database. The transaction id that we are receiving does not belong here. May be someother instance of etspmo daemon is running and accessing the same mq. ");
							return 0;
						}
						flagState = flagState.trim();
						if(flagState.equalsIgnoreCase("C")){
							
						//	pop.updateFlagInETS_PMO_TXN(trans.getTransactionID(), ETSPMOGlobalInitialize.getCR_NACKEDCREATE_STATE().trim().charAt(0));
						pop.updateFlagInETS_PMO_TXN(trans.getTransactionID(), ETSPMOGlobalInitialize.getCR_NACKEDCREATE_STATE().trim().charAt(0));
						}
						else if(flagState.equalsIgnoreCase("U")){
						//	pop.updateFlagInETS_PMO_TXN(trans.getTransactionID(), ETSPMOGlobalInitialize.getCR_NACKEDUPDATE_STATE().trim().charAt(0));
						pop.updateFlagInETS_PMO_TXN(trans.getTransactionID(), ETSPMOGlobalInitialize.getCR_NACKEDUPDATE_STATE().trim().charAt(0));
						}
		//				nack error handling

					} else {

						// ACK
						logger.error(
								"DomParseXML received an ACK with the transaction id: "
									+ trans.getTransactionID());
						//I am not hanlding duplicate acks. Should i handle them. I dont think i need to.
						// case 1) If i have C/U then i get A ..which is fine..
						// case 2) If i get an A and then i get a duplicate ack, I think i wont effect anything if i A the A.
						// I think i need to handle the case if the txnid is not present . let me handle with DoesThisTxnIdExistInTxnTable(String TXNid)
						if (pop
							.DoesThisTxnIdExistInTxnTable(
								trans.getTransactionID())
							== false) {
							logger.info(
								trans.getTransactionID()
									+ " doesnt exist in the table. May be"
									+ " this record was deleted when the user who created this record accessed\n"
									+ " the record after it got an ack. Too late to access it again. May be this"
									+ " is a duplicate ack which came in too late.");
							return 0;
						}

						exception exc =
							(exception) (trans
								.getOperation()
								.getProjObject()
								.getVexceptions()
								.get(0));
						String typestr = exc.getType();
						logger.debug(
							"The exception type is  : " + typestr);
						typestr = typestr.trim();

						if (typestr.equalsIgnoreCase("CRIFOLDER")) {
							typestr =
								((exception) (exc.getVexceptions().get(0)))
									.getType();
							while (typestr.equalsIgnoreCase("CRIFOLDER")) {
								// stay in this loop till i get to the type "CHANGEREQUEST"

								exc = (exception) exc.getVexceptions().get(0);
								typestr =
									((exception) (exc.getVexceptions().get(0)))
										.getType();
								logger.debug("criid :" + exc.getId());
								logger.debug(" typestr : " + typestr);
							}
							if (typestr.equalsIgnoreCase("CHANGEREQUEST")) {
								// I am updating the PMO_Id from the ack 
								// This is how i can access the CHANGEREQUEST in the CRIFOLDER. If confusing. Look at the code carefully
								exc = (exception) exc.getVexceptions().get(0);
								pop.updatePMO_IDfromACK(
									trans.getTransactionID(),
									exc.getId(),
									exc.getReference_Number());
								String flagState = (String)pop.RetrieveFlagStatusofPCR(trans.getTransactionID());
								flagState = flagState.trim();
								if(flagState.equalsIgnoreCase("S")){
									pop.populateRTFTableforCreatePCRs(trans.getTransactionID(),exc.getId(), typestr);
								}
									
							}
							else if(typestr.equalsIgnoreCase("ISSUE")){
								// subu 4.5.1 fix
								
								exc = (exception) exc.getVexceptions().get(0);	
								
								
								pop.updatePMO_IDfromACK(
										trans.getTransactionID(),
										exc.getId(),
										exc.getReference_Number());
								/* Retrieve the STAGE ID AND SUBMITTER NAME FOR THE ISSUE HISTORY TABLE */
								String stateAction = pop.retrieveSTATE_ACTIONforException(exc.getId());
								String submitterName = pop.retrieveSUBMITTER_NAMEforException(exc.getId());
								/*
								 * Update State_ACtion from Problem_State in  ets.pmo_issue_info table
								 * so that Phani can display both the State_Action and the Problem_state 
								 * in the front end.
								 */
								stateAction = stateAction.trim();
								pop.populateProblem_StatefromState_Action(stateAction, true, exc.getId());
								Hashtable htFrontEndETStoDaemonIssue = ETSPMOGlobalInitialize.getHtFrontEndETStoDaemonIssueStates();
								String problemState = (String)htFrontEndETStoDaemonIssue.get(stateAction.trim());
								String ets_id = pop.retrieveETS_ID(exc.getId());
								
								pop.AddNewRecordInPMOIssueHistory(true, ets_id , stateAction, submitterName,
																 problemState, null);
					
								String Owner_Id 		= exc.retrieveResource(0).getLogon_name(); //pop.getIssueOwnerId("ETSPMO", "Defect",trans.getOperation().getProjObject().getProjectId());
								String Owner_Name		= null; //pop.getIssueOwnerName(Owner_Id);
								Owner_Id = Owner_Id.trim();
								Resource res = pop.getResourceInfo(Owner_Id, false);
								if (res==null)
									Owner_Name = exc.retrieveResource(0).getElement_name().trim();
								else
									Owner_Name = res.getElement_name().trim();
								logger.debug("Adding Owner ID : " + Owner_Id + " Owner_Name : " + Owner_Name + " information to the ets.PMO_ISSUE_INFO table for the issue : " + exc.getId());
								
								if(pop.updateOwnerInfoForTheIssue(exc.getId(), Owner_Id, Owner_Name) == false){
										logger.error("Couldn't Add Owner ID and Owner Name info for the issue : " + exc.getId());
								}
								String flagState = (String)pop.RetrieveFlagStatusofPCR(trans.getTransactionID());
								flagState = flagState.trim();
								if(flagState.equalsIgnoreCase("S")){
											pop.populateRTFTableforCreatePCRs(trans.getTransactionID(),exc.getId(), typestr);
								}
							}
						}/* Root level CRIFOLDER */ else if (
							typestr.equalsIgnoreCase(
								"CHANGEREQUEST")) { // I am updating the PMO_id from the ack
							exc = (exception) exc.getVexceptions().get(0);
							pop.updatePMO_IDfromACK(
								trans.getTransactionID(),
								exc.getId(),
								exc.getReference_Number());
							String flagState = (String)pop.RetrieveFlagStatusofPCR(trans.getTransactionID());
								flagState = flagState.trim();
								if(flagState.equalsIgnoreCase("S")){
									pop.populateRTFTableforCreatePCRs(trans.getTransactionID(),exc.getId(), typestr);
								}
						} else if(typestr.equalsIgnoreCase("ISSUE")){
								// subu 4.5.1 fix
								exc = (exception) exc.getVexceptions().get(0);
								
								pop.updatePMO_IDfromACK(
											trans.getTransactionID(),
											exc.getId(),
											exc.getReference_Number());
											
									/* Retrieve the STAGE ID AND SUBMITTER NAME FOR THE ISSUE HISTORY TABLE */
									String stateAction = pop.retrieveSTATE_ACTIONforException(exc.getId());
									String submitterName = pop.retrieveSUBMITTER_NAMEforException(exc.getId());
									/*
									 * Update State_ACtion from Problem_State in  ets.pmo_issue_info table
									 * so that Phani can display both the State_Action and the Problem_state 
									 * in the front end.
									 */
									stateAction = stateAction.trim();
									pop.populateProblem_StatefromState_Action(stateAction, true, exc.getId());

									Hashtable htFrontEndETStoDaemonIssue = ETSPMOGlobalInitialize.getHtFrontEndETStoDaemonIssueStates();
									String problemState = (String)htFrontEndETStoDaemonIssue.get(stateAction.trim());
									String ets_id = pop.retrieveETS_ID(exc.getId());
									pop.AddNewRecordInPMOIssueHistory(true, ets_id, stateAction, submitterName,
									 problemState, null);
							
							
									String Owner_Id 		= exc.retrieveResource(0).getLogon_name(); //pop.getIssueOwnerId("ETSPMO", "Defect",trans.getOperation().getProjObject().getProjectId());
									String Owner_Name		= exc.retrieveResource(0).getElement_name(); //pop.getIssueOwnerName(Owner_Id);
									Owner_Id = Owner_Id.trim();
									Owner_Name = Owner_Name.trim();
								
									if(Owner_Id == null){
										logger.error("The Owner ID for the project : " + trans.getOperation().getProjObject().getProjectId() + " is unassigned. Not a problem of this code. A DATA_ID must be assigned in cq.ets_owner_data for the this project to add an owner id for an Issue");
									
									}
									logger.debug("Adding Owner ID : " + Owner_Id + " Owner_Name : " + Owner_Name + " information to the ets.PMO_ISSUE_INFO table for the issue : " + exc.getId());
								
									if(pop.updateOwnerInfoForTheIssue(exc.getId(), Owner_Id, Owner_Name) == false){
										logger.error("Couldn't Add Owner ID and Owner Name info for the issue : " + exc.getId());
									}
								
								String flagState = (String)pop.RetrieveFlagStatusofPCR(trans.getTransactionID());
								flagState = flagState.trim();
								if(flagState.equalsIgnoreCase("S")){
												pop.populateRTFTableforCreatePCRs(trans.getTransactionID(),exc.getId(), typestr);
										}	
						} else {
							logger.error(
								"DomParseXML received an ACK with the transaction id: "
									+ trans.getTransactionID());
							logger.warn(
								"The exception type in the project is not of following types : CRIFolder, CHANGEREQUEST, ISSUE");
							logger.debug(
								"Not updating any flags . Quitting");
							return 0;
						}
						logger.debug(
							"DomParseXML received an ACK with the transaction id: "
								+ trans.getTransactionID());
						
		//				check if the flag is create 
		//				send a mail to project manager abt the PCR ..only for create
		/*subu@us.ibm.com	Sandie, Which table should i go to look upo for Project Manager email id with PMO ProjectID in hand?
sandieps@us.ib...	well, pmo id will give you the ets project id
sandieps@us.ib...	you will then have to get the role id for that project id that has the priv for workspace owner
sandieps@us.ib...	then you can look at the users to see who has that role id
sandieps@us.ib...	then that will give you the ir id of the workspace owner
sandieps@us.ib...	you can then get the email id from amt
subu@us.ibm.com	All the role id and ir id are in which table?
sandieps@us.ib...	the role id are in ets.ets_roles
sandieps@us.ib...	the priv are in ets.ets_priv
sandieps@us.ib...	the users are in ets.ets_users
subu@us.ibm.com	excellent ..Thanks

		 */
						String userid = pop.RetrieveProjectManagerIdForThisTransaction(trans.getTransactionID());
						
						String otherUserIds = null;
						if(typestr.equalsIgnoreCase("ISSUE")){
							otherUserIds = pop.RetrieveETS_CCListForIssues(exc.getId());
							if(otherUserIds ==null){
								logger.debug("No CC list found for Exception id : " + exc.getId());
								otherUserIds = "";
							}
						}
						
						/* This if loop gets executed when the CR is "Create"
						 */
						if(userid != null){
							logger.debug("Project Manager Id for create CR  : " +  userid);
							pop.sendIssueCRInfoInMailToProjMgr(trans.getTransactionID(), userid, otherUserIds, true);
						}
						/* This if loop gets executed when the CR is "Update"
						 */
						else{//here, I retrieve projectManagerid for update flags
							userid = pop.RetrieveProjectManagerIdForThisTransaction(trans.getTransactionID());
							if(userid != null){
								logger.debug("Project Manager Id for Update CR  : " +  userid);
								pop.sendIssueCRInfoInMailToProjMgr(trans.getTransactionID(), userid, otherUserIds, false);
							}
							else{
								logger.debug("No Project Manager Id obtained for the CR. Not sending any mail");
							}
						}
					//Updating the ACK
					pop.updateFlagInETS_PMO_TXN(trans.getTransactionID(), ETSPMOGlobalInitialize.getCR_ACKED_STATE().trim().charAt(0));
					
					//Delete the Issue/CR file and the No Of Trials File. On deleting this file, it means that
					//i will not resend this xml anymore( I resend a xml when i dont get an ack for the xml)
					GenerateBaseXML.deleteXMLFileFromDisk(trans.getTransactionID());
	
					}
					/* Here I am setting NACK to its original setting. Important to reset the value */
					NACK = false;
				} catch (SQLException sqle) {

					logger.error(getStackTrace(sqle));
					logger.error(getStackTrace(sqle));

				} catch (Exception e) {
					logger.error(getStackTrace(e));
					logger.error(getStackTrace(e));
				}
			return -1;
			}

			ExtractProjectXMLData epXML =
				new ExtractProjectXMLData(pop);
			if (project == null) {
				
				
				RSLT =
					epXML.ExtractProjectData(
						trans.getOperation().getProjObject(),
						null,
						null,
						null);
				epXML.populateXMLInfoInResourceTable(trans.getOperation().getUserID(), trans.getSource(), trans.getDestination(), trans.getRepositoryApp(), trans.getTransactionVersion(), trans.getOperation().getProjObject().getProjectId());
			} else {
				//	epXML = new ExtractProjectXMLData(proj, logMsg, Global);
			
				RSLT = epXML.ExtractProjectData(proj, null, null, null);
				epXML.populateXMLInfoInResourceTable(trans.getOperation().getUserID(), trans.getSource(), trans.getDestination(), trans.getRepositoryApp(), trans.getTransactionVersion(), trans.getOperation().getProjObject().getProjectId());
			}

			Project pr = null;
			if (project == null) {
				pr = trans.getOperation().getProjObject();
			} else {
				pr = proj;
			}
			if(epXML.isIsThisNewProject() == true){
				
				if (ResourceForMail.getResourceListForThisProject().size() > 0
					) {
					logger.debug(
						"ResourceList size is greater than 0. This size :"
							+ ResourceForMail.getResourceListForThisProject().size());
					
					
					PostMan.ResourceInfoMail(
						pr.getProjectId(),
						pr.getElement_name(),
						pr.getRef_code(),
						null,
						ResourceForMail.getResourceListForThisProject());
				} else
					logger.warn(
						"ResourceList size is less than 0");

		} 
		else{
			logger.debug("Not sending the Project Resource info in a mail as this project already exists");
			epXML.setIsThisNewProject(true);//Resetting the value 
		}
			}catch (SAXException e) {
			logger.error(this.getStackTrace(e));
			logger.error(this.getStackTrace(e));
			/* This handles the special case to 
			 * send a NACK when we receive a bad XML
			 * which is capable of throwing SAXPARSEException
			 */
			//createFakeTransactionObject();
			return 2;
			
			// lets define how to catch the error later
		} catch (IOException e) {
			RSLT = false;
			logger.error(this.getStackTrace(e));
			logger.error(this.getStackTrace(e));
			//lets define how to catch the error later
		}
		if(RSLT == true){
			return 1;
		}
		else if(RSLT ==  false){
			return 0;
		}
		// This is never reached
		return 0;
		//return RSLT;
	}

	/* ******
	 * I am Iterating from the root of the xml doc to its child "transaction".Returning Transaction
	 * ******/
	private void handleDocument(Node doc, Project proj) {
		print("ROOT XML DOCUMENT");

		for (Node child = doc.getFirstChild();
			child != null;
			child = child.getNextSibling()) {
			int type = child.getNodeType();
			if (type == Node.ELEMENT_NODE) {
				if (doc.getNodeName().equalsIgnoreCase("transaction")) {
					transaction = doc;
					break;
				} else if (doc.getNodeName().equalsIgnoreCase("object")) {
					NamedNodeMap Attr = doc.getAttributes();

					int opAttrLen = Attr.getLength();
					
					for (int i = 0;
						i < opAttrLen;
						i++) { // This for loop sets the operation type from the attributes list
						Node attr = Attr.item(i);
						print(attr, 2);
						if (attr.getNodeName().equalsIgnoreCase("type")) {

							if (attr
								.getNodeValue()
								.equalsIgnoreCase("project")) {
								print(
									"The traversal pointer on project node"
									);
								oper.setProjObject(proj);
								oper.setProjObjectType("project");
								proj.setType("PROJECT");
								project = doc;
							} else if (
								attr.getNodeValue().equalsIgnoreCase(
									"proposal")) {
								print(
									"The traversal pointer on proposal node"
									);
								oper.setProjObject(proj);
								oper.setProjObjectType("proposal");
								proj.setType("PROPOSAL");
								project = doc;

							}
						}
					}

					break;
				}

				handleDocument(child, proj);
			}
		}

	}
	/* **********
	 * Handle transaction extracts version, id, source, destination, app from the xml
	 * and creates a Transaction object
	 * *********/

	private void handleTransaction() {
		print("TRANSACTION");
		NamedNodeMap startAttr = transaction.getAttributes();
		int transAttrLength = startAttr.getLength();
		for (int i = 0;
			i < transAttrLength;
			i++) { // This for loop sets the version # from the attributes list
			Node attr = startAttr.item(i);
			print(attr, 2);
			if (attr.getNodeName().equalsIgnoreCase("version")) {
				trans.setTransactionVersion(attr.getNodeValue());
			}
		}

		for (Node child = transaction.getFirstChild();
			child != null;
			child = child.getNextSibling()) {
			int type = child.getNodeType();
			if (type == Node.ELEMENT_NODE) {

				if (child.getNodeName().equalsIgnoreCase("id")) {
					print(child, 1);
					trans.setTransactionID(
						child.getFirstChild().getNodeValue().trim());

				} else if (child.getNodeName().equalsIgnoreCase("source")) {
					print(child, 1);

					trans.setSource(
						child.getFirstChild().getNodeValue().trim());

				} else if (
					child.getNodeName().equalsIgnoreCase("destination")) {
					print(child, 1);
					trans.setDestination(
						child.getFirstChild().getNodeValue().trim());

				} else if (child.getNodeName().equalsIgnoreCase("app")) {
					print(child, 1);
					trans.setRepositoryApp(
						child.getFirstChild().getNodeValue().trim());

				} else if (child.getNodeName().equalsIgnoreCase("operation")) {
					trans.setOperation(oper);
					operation = child;
				}
			}

		}

	} //private Node handleTransaction(Transaction trans)

	private void handleOperation(Project proj) {
		print("OPERATION");
		NamedNodeMap startAttr = operation.getAttributes();
		int opAttrLength = startAttr.getLength();
		for (int i = 0;
			i < opAttrLength;
			i++) { // This for loop sets the operation type from the attributes list
			Node attr = startAttr.item(i);
			print(attr, 2);
			if (attr.getNodeName().equalsIgnoreCase("type")) {
				oper.setOperationType(attr.getNodeValue());
			
			}
		}

		NodeList children = operation.getChildNodes();
		for (Node child = operation.getFirstChild();
			child != null;
			child = child.getNextSibling()) {
			int type = child.getNodeType();
			if (type == Node.ELEMENT_NODE) {

				if (child.getNodeName().equalsIgnoreCase("userid")) {
					if (child.getFirstChild() != null) {
						print(child, 1);
						oper.setUserID(
							child.getFirstChild().getNodeValue().trim());
					} else {
						logger.info(
							"Unknown user id in operation element"
							);
						oper.setUserID(this.unknownUserID);
					}

				} else if (child.getNodeName().equalsIgnoreCase("object")) {
					NamedNodeMap Attr = child.getAttributes();
					int opAttrLen = Attr.getLength();
					for (int i = 0;
						i < opAttrLen;
						i++) { // This for loop sets the operation type from the attributes list
						Node attr = Attr.item(i);
						print(attr, 2);
						if (attr.getNodeName().equalsIgnoreCase("type")) {

							if (attr
								.getNodeValue()
								.equalsIgnoreCase("project")) {
								print(
									"The traversal pointer on project node"
									);
								oper.setProjObject(proj);
								oper.setProjObjectType("PROJECT");
								proj.setType("PROJECT");
								project = child;
							} else if (
								attr.getNodeValue().equalsIgnoreCase(
									"proposal")) {
								print(
									"The traversal pointer on proposal node");
								oper.setProjObject(proj);
								oper.setProjObjectType("PROPOSAL");
								proj.setType("PROPOSAL");
								project = child;

							}
						}
					}

				} //else if
			}

		}
	}

	private boolean handleProject(Project proj) {
		boolean rtrn = true;
		print("PROJECT/PROPOSAL");
		for (Node child = project.getFirstChild();
			child != null;
			child = child.getNextSibling()) {
			int type = child.getNodeType();
			if (type == Node.ELEMENT_NODE) {

				if (child.getNodeName().equalsIgnoreCase("id")) {
					if (child.getFirstChild() != null) {
						print(child, 1);
						proj.setProjectId(
							child.getFirstChild().getNodeValue().trim());

					} else {
						/*if (oper.getOperationType().equalsIgnoreCase("ACK")) {
							//NOTE: possiblity of nack...remember Boris sends ack and nack with the same operation type ACK. the real difference lies further beneath the tree
							NACK = true;
							return true;
						}*/
						logger.warn(
							"Id is not supplied for project/proposal. Exiting the parser"
							);

						nackXML.registerErrors(
							"PROJECT/PROPOSAL",
							"EXCEPTION",
							"ID UNAVAILABLE",
							this.MISSING_ID_CODE,
							"");

						return false;
					}
					if (SAXProblemRegistered == true)
						return false;
				} else if (child.getNodeName().equalsIgnoreCase("attribute")) {
					//print(child, 1); 
				//subu added the code 6/23/04
					boolean b= handleAttribute(child, proj);
					if (b == false){ 
						NACK = true; 
						logger.error("Have recieved an Exception. The attribute type is EXCEPTION in handleAttribute method");
						return true;
					}
				} else if (child.getNodeName().equalsIgnoreCase("object")) {
					//	print(child, 1); 
					rtrn = handleProjectObject(child, proj);
					if (rtrn == false)
						return false;
				}

			}

		}
		return true;
	}
	private boolean handleProject(Node proNode, Project pr) {
		boolean rslt = true;
		print("PROJECT/PROPOSAL");
		for (Node child = proNode.getFirstChild();
			child != null;
			child = child.getNextSibling()) {
			int type = child.getNodeType();
			if (type == Node.ELEMENT_NODE) {

				if (child.getNodeName().equalsIgnoreCase("id")) {
					if (child.getFirstChild() != null) {
						print(child, 1);
						pr.setProjectId(
							child.getFirstChild().getNodeValue().trim());
					} else {

						logger.warn(
							"Id is not supplied for PROJECT/PROPOSAL. Exiting the parser");

						logger.warn(
							"ERROR - ID Unavailable for project/proposal");

						nackXML.registerErrors(
							"PROJECT/PROPOSAL",
							"EXCEPTION",
							"ID UNAVAILABLE",
							this.MISSING_ID_CODE,
							"");

						return false;
					}
				} else if (child.getNodeName().equalsIgnoreCase("attribute")) {
					//print(child, 1); 

					handleAttribute(child, pr);
				} else if (child.getNodeName().equalsIgnoreCase("object")) {
					//	print(child, 1); 
					rslt = handleProjectObject(child, pr);
					if (rslt == false)
						return false;
				}

			}

		}
		return true;
	}

	/* *************
	 * Handling projects
	 * *************/
	private boolean handleAttribute(Node attrNode, Project proj) {
		return handleAttribute(attrNode, null, null, null, proj);
	}

/*I think i am using this method only to handle project, ScoreCard, Resource. I am not handling WBSElement with this.
 * For WBSElement , i am using handleWBSElementAttribute. So this parameter is redundant
 * returns false if i am handling an attribute type EXCEPTION. EXCEPTION is the case with nacks
 */
	private boolean handleAttribute(
		Node attrNode,
		ScoreCard SCard,
		Resource rs,
		WBSElement wbsItem,
		Project proj) {

		if(proj != null){
			proj.setIsReportable(true);
		}
		
		boolean RTFHandling = false;
		NamedNodeMap startAttr = attrNode.getAttributes();
		int opAttrLength = startAttr.getLength();
		for (int i = 0;
			i < opAttrLength;
			i++) { // This for loop sets the operation type from the attributes list
			Node attr = startAttr.item(i);

			print(attr, 2);

			if (attr.getNodeName().equalsIgnoreCase("type")){
				if(attr.getNodeValue().equalsIgnoreCase("RTF")) {
					RTFHandling = true;
				}
				else if(attr.getNodeValue().equalsIgnoreCase("EXCEPTION")){
						
						return false;
				}
			}
			
		}
		if (RTFHandling == false) {
			int add = -1;
			for (Node child = attrNode.getFirstChild();
				child != null;
				child = child.getNextSibling()) {
				int type = child.getNodeType();
				if (type == Node.ELEMENT_NODE) {

					if (child.getNodeName().equalsIgnoreCase("name")) {
						

						if (child
							.getFirstChild()
							.getNodeValue()
							.trim()
							.equalsIgnoreCase(
								ProjectAttributeHandling.ELEMENT_NAME)) {

							add = ProjectAttributeHandling.ADD_ELEMENT_NAME;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.REFERENCE_NUMBER)) {

							add = ProjectAttributeHandling.ADD_REFERENCE_NUMBER;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.CALENDAR_ID)) {
							add = ProjectAttributeHandling.ADD_CALENDAR_ID;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.CURRENCY_ID)) {
							add = ProjectAttributeHandling.ADD_CURRENCY_ID;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.PUBLISHED)) {
							add = ProjectAttributeHandling.ADD_PUBLISHED;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.START_FINISH_DATE)) {
							add =
								ProjectAttributeHandling.ADD_START_FINISH_DATE;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.FINISH_START_DATE)) {
							add =
								ProjectAttributeHandling.ADD_FINISH_START_DATE;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.STAGE_ID)) {
							add = ProjectAttributeHandling.ADD_STAGE_ID;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.REVISION_HISTORY)) {
							add = ProjectAttributeHandling.ADD_REVISION_HISTORY;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.START_DT)) {
							add = ProjectAttributeHandling.ADD_START_DT;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.FINISH_DT)) {
							add = ProjectAttributeHandling.ADD_FINISH_DT;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.DURATION)) {
							add = ProjectAttributeHandling.ADD_DURATION;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.SD)) {
							add = ProjectAttributeHandling.ADD_SD;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.FD)) {
							add = ProjectAttributeHandling.ADD_FD;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.EST_START_DT)) {
							add = ProjectAttributeHandling.ADD_EST_START_DT;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.EST_FINISH_DT)) {
							add = ProjectAttributeHandling.ADD_EST_FINISH_DT;
							print(child, 1);
						} 
						else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.PROPOSED_START_DT)) {
							add = ProjectAttributeHandling.ADD_PROPOSED_START_DT;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.PROPOSED_FINISH_DT)) {
							add = ProjectAttributeHandling.ADD_PROPOSED_FINISH_DT;
							print(child, 1);
						} 
						else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.SCHEDULED_START_DT)) {
							add = ProjectAttributeHandling.ADD_SCHEDULED_START_DT;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.SCHEDULED_FINISH_DT)) {
							add = ProjectAttributeHandling.ADD_SCHEDULED_FINISH_DT;
							print(child, 1);
						} 
						else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.FORECAST_START_DT)) {
							add = ProjectAttributeHandling.ADD_FORECAST_START_DT;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.FORECAST_FINISH_DT)) {
							add = ProjectAttributeHandling.ADD_FORECAST_FINISH_DT;
							print(child, 1);
						}else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.BASELINE1_FINISH)) {
							add = ProjectAttributeHandling.ADD_BASELINE1_FINISH_DT;
							print(child, 1);
						}
						else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.BASELINE2_FINISH)) {
							add = ProjectAttributeHandling.ADD_BASELINE2_FINISH_DT;
							print(child, 1);
						}
						else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.BASELINE3_FINISH)) {
							add = ProjectAttributeHandling.ADD_BASELINE3_FINISH_DT;
							print(child, 1);
						}
						else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.ACTUAL_FINISH)) {
							add = ProjectAttributeHandling.ADD_ACTUAL_FINISH_DT;
							print(child, 1);
						}
						//There are 2 work_Percents in WBS elements . They differ by type. One is Integer and the other is float
						else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.WORK_PERCENT)) {

							add = ProjectAttributeHandling.ADD_WORK_PERCENT;
							print(child, 1);

						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.ETC)) {
							add = ProjectAttributeHandling.ADD_ETC;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.PERCENT_COMPLETE)) {
							add = ProjectAttributeHandling.ADD_PERCENT_COMPLETE;
							print(child, 1);
						}
						/* ****************
						 * rating_score is an element in ScoreCard
						 * ****************/
						else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ScoreCardAttributeHandling.RATING_SCORE)) {
							add = ScoreCardAttributeHandling.ADD_RATING_SCORE;
							print(child, 1);
						}
						/* ***************
						 * security_id, logon_name, company_name are elements in Resource
						 * ***************/
						else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ResourceAttributeHandling.SECURITY_ID)) {
							add = ResourceAttributeHandling.ADD_SECURITY_ID;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ResourceAttributeHandling.LOGON_NAME)) {
							add = ResourceAttributeHandling.ADD_LOGON_NAME;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ResourceAttributeHandling.COMPANY_NAME)) {
							add = ResourceAttributeHandling.ADD_COMPANY_NAME;
							print(child, 1);
						}
						/* ************
						 *  WBS elements
						 *  ***********/

						/* **************************************
						 * The RTFs are handled in handleRTFAttribute. The following else ifs 
						 * are never reached in present scenario. Could be use for future
						 * enhancements(if the xml gets altered)
						 * **************************************/
						else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.SCOPE_RTF)) {
							add = ProjectAttributeHandling.ADD_SCOPE_RTF;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.OBJECTIVES_RTF)) {
							add = ProjectAttributeHandling.ADD_OBJECTIVES_RTF;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.BACKGROUND_RTF)) {
							add = ProjectAttributeHandling.ADD_BACKGROUND_RTF;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.STATUS_RTF)) {
							add = ProjectAttributeHandling.ADD_STATUS_RTF;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.TARGETSOLN_RTF)) {
							add = ProjectAttributeHandling.ADD_TARGETSOLN_RTF;
							print(child, 1);
						}

					} else if (child.getNodeName().equalsIgnoreCase("value")) {
						// if the xml structure has <value/> then return null and break	
						if (child.getFirstChild() == null) {
							print(null, 1);
							break;
						}

						if (add == AttributeHandling.ADD_ELEMENT_NAME) {
							print(child, 1);
							if (SCard != null) {
								SCard.setElement_name(
									child
										.getFirstChild()
										.getNodeValue()
										.trim());
							} else if (rs != null) {
								rs.setElement_name(
									child
										.getFirstChild()
										.getNodeValue()
										.trim());
							} else {
								proj.setElement_name(
									child
										.getFirstChild()
										.getNodeValue()
										.trim());
							}
							if (AttributeHandling.IsELEMENT_NAME_RANK
								== false) {
								
								break;
							}

						} else if (
							add
								== ProjectAttributeHandling
									.ADD_REFERENCE_NUMBER) {
							print(child, 1);
							proj.setReference_number(
								child.getFirstChild().getNodeValue().trim());
							if (ProjectAttributeHandling
								.IsREFERENCE_NUMBER_RANK
								== false) {
								
								break;
							}
						} else if (
							add == ProjectAttributeHandling.ADD_CALENDAR_ID) {
							print(child, 1);
							proj.setCalendar_id(
								child.getFirstChild().getNodeValue().trim());
							if (ProjectAttributeHandling.IsCALENDAR_ID_RANK
								== false) {
								
								break;
							}
						} else if (
							add == ProjectAttributeHandling.ADD_CURRENCY_ID) {
							print(child, 1);
							proj.setCurrency_id(
								child.getFirstChild().getNodeValue().trim());
							if (ProjectAttributeHandling.IsCURRENCY_ID_RANK
								== false) {
								
								break;
							}
						} else if (
							add == ProjectAttributeHandling.ADD_PUBLISHED) {
							print(child, 1);
							proj.setPublished(
								child.getFirstChild().getNodeValue().trim());
							if (ProjectAttributeHandling.IsPUBLISHED_RANK
								== false) {
								
								break;
							}
						} else if (
							add
								== ProjectAttributeHandling
									.ADD_START_FINISH_DATE) {
							print(child, 1);
							proj.setEstimatedStart(
								child.getFirstChild().getNodeValue().trim());
							if (ProjectAttributeHandling
								.IsSTART_FINISH_DATE_RANK
								== false) {
								
								break;
							}
						} else if (
							add
								== ProjectAttributeHandling
									.ADD_FINISH_START_DATE) {
							print(child, 1);
							proj.setEstimatedFinish(
								child.getFirstChild().getNodeValue().trim());
							if (ProjectAttributeHandling
								.IsFINISH_START_DATE_RANK
								== false) {
								
								break;
							}
						} else if (
							add == ProjectAttributeHandling.ADD_STAGE_ID) {
							print(child, 1);
							proj.setState(
								child.getFirstChild().getNodeValue().trim());
							if (ProjectAttributeHandling.IsSTAGE_ID_RANK
								== false) {
								
								break;
							}
						} else if (
							add
								== ProjectAttributeHandling
									.ADD_REVISION_HISTORY) {
							print(child, 1);
							proj.setRevision_history(
								child.getFirstChild().getNodeValue().trim());
							if (ProjectAttributeHandling
								.IsREVISION_HISTORY_RANK
								== false) {
								
								break;
							}
						} else if (
							add == ProjectAttributeHandling.ADD_START_DT) {
							print(child, 1);
							proj.setStart(
								child.getFirstChild().getNodeValue().trim());
							if (ProjectAttributeHandling.IsSTART_DT_RANK
								== false) {
								
								break;
							}
						} else if (
							add == ProjectAttributeHandling.ADD_FINISH_DT) {
							print(child, 1);
							proj.setFinish(
								child.getFirstChild().getNodeValue().trim());
							if (ProjectAttributeHandling.IsFINISH_DT_RANK
								== false) {
								
								break;
							}
						} else if (
							add == ProjectAttributeHandling.ADD_DURATION) {
							print(child, 1);
							proj.setDuration(
								child.getFirstChild().getNodeValue().trim());
							if (ProjectAttributeHandling.IsDURATION_RANK
								== false) {
								
								break;
							}
						} else if (
							add == ProjectAttributeHandling.ADD_WORK_PERCENT) {
							print(child, 1);
							proj.setWorkPercent(
								child.getFirstChild().getNodeValue().trim());
							if (ProjectAttributeHandling.IsWORK_PERCENT_RANK
								== false) {
								
								break;
							}
						} else if (add == ProjectAttributeHandling.ADD_ETC) {
							print(child, 1);
							proj.setEstimatedETC(
								child.getFirstChild().getNodeValue().trim());
							if (ProjectAttributeHandling.IsETC_RANK == false) {
								
								break;
							}
						} else if (
							add
								== ProjectAttributeHandling
									.ADD_PERCENT_COMPLETE) {
							print(child, 1);
							proj.setPercentComplete(
								child.getFirstChild().getNodeValue().trim());
							if (ProjectAttributeHandling
								.IsPERCENT_COMPLETE_RANK
								== false) {
								
								break;
							}
						}
						else if (
							add
								== ProjectAttributeHandling
									.ADD_SD) {
							print(child, 1);
							Hashtable htSDTypes = ETSPMOGlobalInitialize.getHtSDtypes();
							String SDValue = null;
							SDValue = (String)htSDTypes.get(child.getFirstChild().getNodeValue().trim());
							logger.debug("The SD value : " + SDValue);
							proj.setSD(SDValue.substring(SDValue.indexOf(":") + 1));
							if (ProjectAttributeHandling
								.IsSDRank
								== false) {
								
								break;
							}
						}
						else if (
							add
								== ProjectAttributeHandling
									.ADD_FD) {
							print(child, 1);
							Hashtable htFDTypes = ETSPMOGlobalInitialize.getHtFDtypes();
							String FDValue = null;
							FDValue = (String)htFDTypes.get(child.getFirstChild().getNodeValue().trim());
							logger.debug("The FD value : " + FDValue);
							proj.setFD(FDValue.substring(FDValue.indexOf(":") + 1));
							if (ProjectAttributeHandling
								.IsFDRank
								== false) {
								
								break;
							}
						}else if (
							add == ProjectAttributeHandling.ADD_EST_START_DT) {
							print(child, 1);
							proj.setEstimatedStart(
								child.getFirstChild().getNodeValue().trim());
							if (ProjectAttributeHandling.IsEST_START_DT_RANK
								== false) {
								
								break;
							}
						} else if (
							add == ProjectAttributeHandling.ADD_EST_FINISH_DT) {
							print(child, 1);
							proj.setEstimatedFinish(
								child.getFirstChild().getNodeValue().trim());
							if (ProjectAttributeHandling.IsEST_FINISH_DT_RANK
								== false) {
								
								break;
							}
						}	else if (
							add == ProjectAttributeHandling.ADD_PROPOSED_START_DT) {
							print(child, 1);
							proj.setProposedStart(
								child.getFirstChild().getNodeValue().trim());
							if (ProjectAttributeHandling.IsPROPOSED_START_DT_RANK
								== false) {
								
								break;
							}
						} else if (
							add == ProjectAttributeHandling.ADD_PROPOSED_FINISH_DT) {
							print(child, 1);
							proj.setProposedFinish(
								child.getFirstChild().getNodeValue().trim());
							if (ProjectAttributeHandling.IsPROPOSED_FINISH_DT_RANK
								== false) {
								
								break;
							}
						}	else if (
							add == ProjectAttributeHandling.ADD_SCHEDULED_START_DT) {
							print(child, 1);
							proj.setScheduledStart(
								child.getFirstChild().getNodeValue().trim());
							if (ProjectAttributeHandling.IsSCHEDULED_START_DT_RANK
								== false) {
								
								break;
							}
						} else if (
							add == ProjectAttributeHandling.ADD_SCHEDULED_FINISH_DT) {
							print(child, 1);
							proj.setScheduledFinish(
								child.getFirstChild().getNodeValue().trim());
							if (ProjectAttributeHandling.IsSCHEDULED_FINISH_DT_RANK
								== false) {
								
								break;
							}
						}
							else if (
							add == ProjectAttributeHandling.ADD_FORECAST_START_DT) {
							print(child, 1);
							proj.setForecastStart(
								child.getFirstChild().getNodeValue().trim());
							if (ProjectAttributeHandling.IsFORECAST_START_DT_RANK
								== false) {
								
								break;
							}
						} else if (
							add == ProjectAttributeHandling.ADD_FORECAST_FINISH_DT) {
							print(child, 1);
							proj.setForecastFinish(
								child.getFirstChild().getNodeValue().trim());
							if (ProjectAttributeHandling.IsFORECAST_FINISH_DT_RANK
								== false) {
								
								break;
							}
						}else if (
							add == ProjectAttributeHandling.ADD_BASELINE1_FINISH_DT) {
							print(child, 1);
							proj.setBaseline1Finish(
								child.getFirstChild().getNodeValue().trim());
							if (ProjectAttributeHandling.IsBASELINE1_FINISH_RANK
								== false) {
								
								break;
							}
						}else if (
							add == ProjectAttributeHandling.ADD_BASELINE2_FINISH_DT) {
							print(child, 1);
							proj.setBaseline2Finish(
								child.getFirstChild().getNodeValue().trim());
							if (ProjectAttributeHandling.IsBASELINE2_FINISH_RANK
								== false) {
								
								break;
							}
						}else if (
							add == ProjectAttributeHandling.ADD_BASELINE3_FINISH_DT) {
							print(child, 1);
							proj.setBaseline3Finish(
								child.getFirstChild().getNodeValue().trim());
							if (ProjectAttributeHandling.IsBASELINE3_FINISH_RANK
								== false) {
								
								break;
							}
						}
						else if (
							add == ProjectAttributeHandling.ADD_ACTUAL_FINISH_DT) {
							print(child, 1);
							proj.setActualFinish(
								child.getFirstChild().getNodeValue().trim());
							if (ProjectAttributeHandling.IsACTUAL_FINISH_RANK
								== false) {
								
								break;
							}
						}
						/* ****
						 * ScoreCard
						 * ****/
						else if (
							add
								== ScoreCardAttributeHandling.ADD_RATING_SCORE) {
							print(child, 1);
							SCard.setRating_score(
								child.getFirstChild().getNodeValue().trim());
							if (ScoreCardAttributeHandling.IsRATING_SCORE_RANK
								== false)
								break;

						}
						/* ****
						 * Resource
						 * ****/
						else if (
							add == ResourceAttributeHandling.ADD_SECURITY_ID) {
							print(child, 1);
							rs.setSecurity_id(
								child.getFirstChild().getNodeValue().trim());
							if (ResourceAttributeHandling.IsSECURITY_ID_RANK
								== false)
								break;
						} else if (
							add == ResourceAttributeHandling.ADD_LOGON_NAME) {
							print(child, 1);
							rs.setLogon_name(
								child.getFirstChild().getNodeValue().trim());
							if (ResourceAttributeHandling.IsLOGON_NAME_RANK
								== false)
								break;
						} else if (
							add
								== ResourceAttributeHandling.ADD_COMPANY_NAME) {
							print(child, 1);
							rs.setCompany_name(
								child.getFirstChild().getNodeValue().trim());
							if (ResourceAttributeHandling.IsCOMPANY_NAME_RANK
								== false)
								break;
						}
						/* **************************************
						 * The RTFs are handled in handleRTFAttribute. The following else ifs 
						 * are never reached in present scenario. Could be use for future
						 * enhancements(if the xml gets altered
						 * **************************************/
						else if (
							add == ProjectAttributeHandling.ADD_SCOPE_RTF) {
							print(child, 1);
							proj.setScopeRTF(
								child.getFirstChild().getNodeValue().trim());
							if (ProjectAttributeHandling.IsSCOPE_RTF_RANK
								== false)
								break;
						} else if (
							add
								== ProjectAttributeHandling.ADD_OBJECTIVES_RTF) {
							print(child, 1);
							proj.setObjectivesRTF(
								child.getFirstChild().getNodeValue().trim());
							if (ProjectAttributeHandling.IsOBJECTIVES_RTF_RANK
								== false)
								break;
						} else if (
							add
								== ProjectAttributeHandling.ADD_BACKGROUND_RTF) {
							print(child, 1);
							proj.setBackgroundRTF(
								child.getFirstChild().getNodeValue().trim());
							if (ProjectAttributeHandling.IsBACKGROUND_RTF_RANK
								== false)
								break;
						} else if (
							add == ProjectAttributeHandling.ADD_STATUS_RTF) {
							print(child, 1);
							proj.setStatusRTF(
								child.getFirstChild().getNodeValue().trim());
							if (ProjectAttributeHandling.IsSTATUS_RTF_RANK
								== false)
								break;
						} else if (
							add
								== ProjectAttributeHandling.ADD_TARGETSOLN_RTF) {
							print(child, 1);
							proj.setTargetSolnRTF(
								child.getFirstChild().getNodeValue().trim());
							if (ProjectAttributeHandling.IsTARGETSOLN_RTF_RANK
								== false)
								break;
						}

					} else if (child.getNodeName().equalsIgnoreCase("rank")) {
						if (add == ProjectAttributeHandling.ADD_ELEMENT_NAME) {
							print(child, 1);
							proj.setElement_name_Rank(
								child.getFirstChild().getNodeValue().trim());
							break;
						} else if (
							add
								== ProjectAttributeHandling
									.ADD_REFERENCE_NUMBER) {
							print(child, 1);
							proj.setReference_number_Rank(
								child.getFirstChild().getNodeValue().trim());
							break;
						} else if (
							add == ProjectAttributeHandling.ADD_CALENDAR_ID) {
							print(child, 1);
							proj.setCalendar_id_Rank(
								child.getFirstChild().getNodeValue().trim());
							break;
						} else if (
							add == ProjectAttributeHandling.ADD_CURRENCY_ID) {
							print(child, 1);
							proj.setCurrency_id_Rank(
								child.getFirstChild().getNodeValue().trim());
							break;
						} else if (
							add == ProjectAttributeHandling.ADD_PUBLISHED) {
							print(child, 1);
							proj.setPublished_Rank(
								child.getFirstChild().getNodeValue().trim());
							break;
						} else if (
							add
								== ProjectAttributeHandling
									.ADD_START_FINISH_DATE) {
							print(child, 1);
							proj.setEstimatedStart_Rank(
								child.getFirstChild().getNodeValue().trim());
							break;
						} else if (
							add
								== ProjectAttributeHandling
									.ADD_FINISH_START_DATE) {
							print(child, 1);
							proj.setEstimatedFinish_Rank(
								child.getFirstChild().getNodeValue().trim());
							break;
						} else if (
							add == ProjectAttributeHandling.ADD_STAGE_ID) {
							print(child, 1);
							proj.setState_Rank(
								child.getFirstChild().getNodeValue().trim());
							break;
						} else if (
							add
								== ProjectAttributeHandling
									.ADD_REVISION_HISTORY) {
							print(child, 1);
							proj.setRevision_history_Rank(
								child.getFirstChild().getNodeValue().trim());
							break;
						} else if (
							add == ProjectAttributeHandling.ADD_START_DT) {
							print(child, 1);
							proj.setStart_Rank(
								child.getFirstChild().getNodeValue().trim());
							break;
						} else if (
							add == ProjectAttributeHandling.ADD_FINISH_DT) {
							print(child, 1);
							proj.setFinish_Rank(
								child.getFirstChild().getNodeValue().trim());
							break;
						} else if (
							add == ProjectAttributeHandling.ADD_DURATION) {
							print(child, 1);
							proj.setDuration_Rank(
								child.getFirstChild().getNodeValue().trim());
							break;
						} else if (
							add == ProjectAttributeHandling.ADD_WORK_PERCENT) {
							print(child, 1);
							proj.setWorkPercent_Rank(
								child.getFirstChild().getNodeValue().trim());
							break;
						} else if (add == ProjectAttributeHandling.ADD_ETC) {
							print(child, 1);
							proj.setEstimatedETC_Rank(
								child.getFirstChild().getNodeValue().trim());
							break;
						} else if (
							add
								== ProjectAttributeHandling
									.ADD_PERCENT_COMPLETE) {
							print(child, 1);
							proj.setPercentComplete_Rank(
								child.getFirstChild().getNodeValue().trim());
							break;
						}
						/* *****
						 * Resource 
						 * *****/
						else if (
							add == ResourceAttributeHandling.ADD_SECURITY_ID) {
							print(child, 1);
							rs.setSecurity_id_Rank(
								child.getFirstChild().getNodeValue().trim());
							break;
						}
					}
				}
			}
		} else if (RTFHandling == true) {
			handleRTFAttribute(attrNode, proj);
		}
	return true;
	}

	private void handleRTFAttribute(Node attrNode, Project proj) {

		/* ************************
		 * This for loop is a reverse loop. Traversing from the last child to the first child in reverse
		 * direction as the RTF information is structured in a reverse way.
		 * eg : <attribute type="RTF" >
		* 		<name>Name of RTF</name>
		* 		<value>CpcY3MxMCBcYWRkaXRpdmUgXHNzZW1p
		* 		</value>
		* 		<rank>2</rank>	
		* 		</attribute>
		* The first important information to be handled here is the rank and then the value . Name is irrelevant
		* if it has "Name of RTF" as its content. name could be something that might change later as XML gets 
		* changed.
		* ************************/
		int add = -1;
		RTFData rtf = new RTFData();
		for (Node child = attrNode.getLastChild();
			child != attrNode.getFirstChild();
			child = child.getPreviousSibling()) {
			int type = child.getNodeType();
			if (type == Node.ELEMENT_NODE) {
				if (child.getNodeName().equalsIgnoreCase("rank")) {
					if (Integer
						.parseInt(child.getFirstChild().getNodeValue().trim())
						== ProjectAttributeHandling.SCOPE_RTF_RANKVALUE) {
						add = ProjectAttributeHandling.ADD_SCOPE_RTF;
						rtf.setRank(
							ProjectAttributeHandling.SCOPE_RTF_RANKVALUE);
						rtf.setName(ProjectAttributeHandling.SCOPE_RTF);
						print(child, 1);
					}
					if (Integer
						.parseInt(child.getFirstChild().getNodeValue().trim())
						== ProjectAttributeHandling.OBJECTIVES_RTF_RANKVALUE) {
						add = ProjectAttributeHandling.ADD_OBJECTIVES_RTF;
						rtf.setRank(
							ProjectAttributeHandling.OBJECTIVES_RTF_RANKVALUE);
						rtf.setName(ProjectAttributeHandling.OBJECTIVES_RTF);
						print(child, 1);
					}
					if (Integer
						.parseInt(child.getFirstChild().getNodeValue().trim())
						== ProjectAttributeHandling.BACKGROUND_RTF_RANKVALUE) {
						add = ProjectAttributeHandling.ADD_BACKGROUND_RTF;
						rtf.setRank(
							ProjectAttributeHandling.BACKGROUND_RTF_RANKVALUE);
						rtf.setName(ProjectAttributeHandling.BACKGROUND_RTF);
						print(child, 1);
					}
					if (Integer
						.parseInt(child.getFirstChild().getNodeValue().trim())
						== ProjectAttributeHandling.STATUS_RTF_RANKVALUE) {
						add = ProjectAttributeHandling.ADD_STATUS_RTF;
						rtf.setRank(
							ProjectAttributeHandling.STATUS_RTF_RANKVALUE);
						rtf.setName(ProjectAttributeHandling.STATUS_RTF);
						print(child, 1);
					}
					if (Integer
						.parseInt(child.getFirstChild().getNodeValue().trim())
						== ProjectAttributeHandling.TARGETSOLN_RTF_RANKVALUE) {
						add = ProjectAttributeHandling.ADD_TARGETSOLN_RTF;
						rtf.setRank(
							ProjectAttributeHandling.TARGETSOLN_RTF_RANKVALUE);
						rtf.setName(ProjectAttributeHandling.TARGETSOLN_RTF);
						print(child, 1);
					}
				} else if (child.getNodeName().equalsIgnoreCase("value")) {
					// if the xml structure has <value/> then return null and break	
					if (child.getFirstChild() == null) {
						print(null, 1);
						break;
					}

					if (add == ProjectAttributeHandling.ADD_SCOPE_RTF) {
						print(child, 1);
						proj.setScopeRTF(
							child.getFirstChild().getNodeValue().trim());
						rtf.setValue(
							child.getFirstChild().getNodeValue().trim());
						proj.populatevRTF(rtf);
						break;
					} else if (
						add == ProjectAttributeHandling.ADD_OBJECTIVES_RTF) {
						print(child, 1);
						proj.setObjectivesRTF(
							child.getFirstChild().getNodeValue().trim());
						rtf.setValue(
							child.getFirstChild().getNodeValue().trim());
						proj.populatevRTF(rtf);
						break;
					} else if (
						add == ProjectAttributeHandling.ADD_BACKGROUND_RTF) {
						print(child, 1);
						proj.setBackgroundRTF(
							child.getFirstChild().getNodeValue().trim());
						rtf.setValue(
							child.getFirstChild().getNodeValue().trim());
						proj.populatevRTF(rtf);
						break;
					} else if (
						add == ProjectAttributeHandling.ADD_STATUS_RTF) {
						print(child, 1);
						proj.setStatusRTF(
							child.getFirstChild().getNodeValue().trim());
						rtf.setValue(
							child.getFirstChild().getNodeValue().trim());
						proj.populatevRTF(rtf);
						break;
					} else if (
						add == ProjectAttributeHandling.ADD_TARGETSOLN_RTF) {
						print(child, 1);
						proj.setTargetSolnRTF(
							child.getFirstChild().getNodeValue().trim());
						rtf.setValue(
							child.getFirstChild().getNodeValue().trim());
						proj.populatevRTF(rtf);
						break;
					}

				}
			}
		}

	}
	private boolean handleWBSItemObject(Node wbsItemObj, WBSElement wbsItem) {
		//print("WBSITEM OBJECTS");	
		boolean b = true;
		NamedNodeMap startAttr = wbsItemObj.getAttributes();
		int opAttrLength = startAttr.getLength();
		for (int i = 0;
			i < opAttrLength;
			i++) { // This for loop sets the operation type from the attributes list
			Node attr = startAttr.item(i);
			print(attr, 2);
			if (attr.getNodeName().equalsIgnoreCase("type")
				&& attr.getNodeValue().equalsIgnoreCase("ASSIGNEDRESOURCE")) {
				Resource rs = new Resource();
				wbsItem.populateVResources(rs);
				handleResource(rs, wbsItemObj);
			} else if (
				attr.getNodeName().equalsIgnoreCase("type")
					&& attr.getNodeValue().equalsIgnoreCase("DOCUMENT")) {
				Doc doc = new Doc();
				doc.setDoc_Type("DOCUMENT");
				wbsItem.populateVDocs(doc);
				b = handleDoc(doc, wbsItemObj);
				if (b == false)
					return false;

			} else if (
				attr.getNodeName().equalsIgnoreCase("type")
					&& attr.getNodeValue().equalsIgnoreCase("DOCUMENTFOLDER")) {
				Doc doc = new Doc();
				doc.setDoc_Type("DOCUMENTFOLDER");
				wbsItem.populateVDocs(doc);
				b = handleDocFolder(doc, wbsItemObj);
				if (b == false)
					return false;
			} else if (
				attr.getNodeName().equalsIgnoreCase("type")
					&& attr.getNodeValue().equalsIgnoreCase("TASK")) {

				b = handleTask(wbsItemObj, wbsItem);
				if (b == false)
					return false;
			} else if (
				attr.getNodeName().equalsIgnoreCase("type")
					&& attr.getNodeValue().equalsIgnoreCase("SUMMARYTASK")) {
				b = handleSummaryTask(wbsItemObj, wbsItem);
				if (b == false)
					return false;
			} else if (
				attr.getNodeName().equalsIgnoreCase("type")
					&& attr.getNodeValue().equalsIgnoreCase("MILESTONE")) {
				b = handleMilestone(wbsItemObj, wbsItem);
				if (b == false)
					return false;
			} else if (
				attr.getNodeName().equalsIgnoreCase("type")
					&& attr.getNodeValue().equalsIgnoreCase("DELIVERABLE")) {
				b = handleDeliverable(wbsItemObj, wbsItem);
				if (b == false)
					return false;
			} else if (
				attr.getNodeName().equalsIgnoreCase("type")
					&& attr.getNodeValue().equalsIgnoreCase("WORKPRODUCT")) {
				b = handleWorkProduct(wbsItemObj, wbsItem);
				if (b == false)
					return false;
			}
			/* I assume there are no CR/Issue attached to the task/summary task and aother wbs elements.
			 * They are only attached to Project/proposals/ organizations
			 */
		}
		return b;
	}
	private boolean handleProjectObject(Node prObject, Project proj) {
		print("PROJECT OBJECTS");
		boolean rslt = true;

		NamedNodeMap startAttr = prObject.getAttributes();
		int opAttrLength = startAttr.getLength();
		for (int i = 0;
			i < opAttrLength;
			i++) { // This for loop sets the operation type from the attributes list
			Node attr = startAttr.item(i);
			print(attr, 2);
			if (attr.getNodeName().equalsIgnoreCase("type")
				&& attr.getNodeValue().equalsIgnoreCase("PUBLISHEDSCORECARD")) {
				ScoreCard sc = new ScoreCard();
				proj.setScorecard(sc);
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleScoreCard() )");
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleScoreCard() )");
				rslt = handleScoreCard(sc, prObject);
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleScoreCard() )");
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleScoreCard() )");
				if (rslt == false)
					return false;
			} else if (
				attr.getNodeName().equalsIgnoreCase("type")
					&& attr.getNodeValue().equalsIgnoreCase("ASSIGNEDRESOURCE")) {
				Resource rs = new Resource();
				proj.populateVResources(rs);
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleResource() )");
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleResource() )");
				rslt = handleResource(rs, prObject);
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleResource() )");
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleResource() )");
				if (rslt == false)
					return false;
			} else if (
				attr.getNodeName().equalsIgnoreCase("type")
					&& attr.getNodeValue().equalsIgnoreCase("TASK")) {
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleTask() )");
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleTask() )");
				rslt = handleTask(prObject, proj);
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleTask() )");
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleTask() )");
				if (rslt == false)
					return false;
			} else if (
				attr.getNodeName().equalsIgnoreCase("type")
					&& attr.getNodeValue().equalsIgnoreCase("SUMMARYTASK")) {
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleSummaryTask() )");
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleSummaryTask() )");
				rslt = handleSummaryTask(prObject, proj);
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleSummaryTask() )");
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleSummaryTask() )");
				if (rslt == false)
					return false;
			} else if (
				attr.getNodeName().equalsIgnoreCase("type")
					&& attr.getNodeValue().equalsIgnoreCase("MILESTONE")) {
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleMilestone() )");
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleMilestone() )");
				rslt = handleMilestone(prObject, proj);
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleMilestone() )");
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleMilestone() )");
				if (rslt == false)
					return false;
			} else if (
				attr.getNodeName().equalsIgnoreCase("type")
					&& attr.getNodeValue().equalsIgnoreCase("WBSELEMENT")) {
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleWBSElement() ) " ) ;
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleWBSElement() ) " ) ;
				rslt = handleMilestone(prObject, proj);
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleWBSElement() ) " ) ;
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleWBSElement() ) " ) ;
				if (rslt == false)
					return false;
			} else if (
				attr.getNodeName().equalsIgnoreCase("type")
					&& attr.getNodeValue().equalsIgnoreCase("DELIVERABLE")) {
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleDeliverable() ) " ) ;
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleDeliverable() ) " ) ;
				rslt = handleDeliverable(prObject, proj);
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleDeliverable() ) " ) ;
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleDeliverable() ) " ) ;
				if (rslt == false)
					return false;
			} else if (
				attr.getNodeName().equalsIgnoreCase("type")
					&& attr.getNodeValue().equalsIgnoreCase("WORKPRODUCT")) {
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleWorProduct() ) " ) ;
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleWorProduct() ) " ) ;
				rslt = handleWorkProduct(prObject, proj);
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleWorkProduct() ) " ) ;
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleWorkProduct() ) " ) ;
				if (rslt == false)
					return false;
			} else if (
				attr.getNodeName().equalsIgnoreCase("type")
					&& attr.getNodeValue().equalsIgnoreCase("CRIFOLDER")) {
				print("CRIFOLDER");
				exception exc = new exception();
				exc.setType("CRIFOLDER");
				proj.populatevexception(exc);
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleCRIFolder() ) " ) ;
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleCRIFolder() ) " ) ;
				rslt = handleCRIFolder(exc, prObject);
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleCRIFolder() ) " ) ;
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleCRIFolder() ) " ) ;
				if (rslt == false)
					return false;
			} else if (
				attr.getNodeName().equalsIgnoreCase("type")
					&& attr.getNodeValue().equalsIgnoreCase("ISSUE")) {
				print("ISSUE");
				exception exc = new exception();
				exc.setType("ISSUE");
				proj.populatevexception(exc);
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handle_exception for ISSUES ) " ) ;
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handle_exception for ISSUES ) " ) ;
				rslt = handle_exception(exc, prObject);
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handle_exception for ISSUES ) " ) ;
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handle_exception for ISSUES ) " ) ;
				if (rslt == false)
					return false;
			} else if (
				attr.getNodeName().equalsIgnoreCase("type")
					&& attr.getNodeValue().equalsIgnoreCase("CHANGEREQUEST")) {
				print("CHANGEREQUEST");
				exception exc = new exception();
				exc.setType("CHANGEREQUEST");
				proj.populatevexception(exc);
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handle_exception for CHANGEREQUEST ) " ) ;
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handle_exception for CHANGEREQUEST ) " ) ;
				rslt = handle_exception(exc, prObject);
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handle_exception for CHANGEREQUEST ) " ) ;
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handle_exception for CHANGEREQUEST ) " ) ;
				if (rslt == false)
					return false;

			} else if (
				attr.getNodeName().equalsIgnoreCase("type")
					&& attr.getNodeValue().equalsIgnoreCase("DOCUMENTFOLDER")) {
				Doc doc = new Doc();

				proj.populateVDocs(doc);
				doc.setDoc_Type("DOCUMENTFOLDER");
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleDocFolder ) " ) ;
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleDocFolder ) " ) ;
				rslt = handleDocFolder(doc, prObject);
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleDocFolder ) " ) ;
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleDocFolder ) " ) ;
				if (rslt == false)
					return false;
			} else if (
				attr.getNodeName().equalsIgnoreCase("type")
					&& attr.getNodeValue().equalsIgnoreCase("DOCUMENT")) {
				Doc doc = new Doc();
				proj.populateVDocs(doc);
				doc.setDoc_Type("DOCUMENT");
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleDoc ) " ) ;
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleDoc ) " ) ;
				rslt = handleDoc(doc, prObject);
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleDoc ) " ) ;
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleDoc ) " ) ;
				if (rslt == false)
					return false;

			} else if (
				attr.getNodeName().equalsIgnoreCase("type")
					&& attr.getNodeValue().equalsIgnoreCase("PROPOSAL")) {
				Project pr = new Project();
				pr.setType("PROPOSAL");
				proj.populateVProject(pr);
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleProject for Proposal ) " ) ;
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleProject for Proposal ) " ) ;
				rslt = handleProject(prObject, pr);
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleProject for Proposal ) " ) ;
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleProject for Proposal ) " ) ;
				if (rslt == false)
					return false;

			} else if (
				attr.getNodeName().equalsIgnoreCase("type")
					&& attr.getNodeValue().equalsIgnoreCase("PROJECT")) {
				Project pr = new Project();
				pr.setType("PROJECT");
				proj.populateVProject(pr);
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleProject for Project ) " ) ;
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleProject for Project ) " ) ;
				rslt = handleProject(prObject, pr);
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleProject for Project ) " ) ;
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleProject for Project ) " ) ;
				if (rslt == false)
					return false;
			} else if (
				attr.getNodeName().equalsIgnoreCase("type")
					&& attr.getNodeValue().equalsIgnoreCase("ORGANIZATION")) {
				Project pr = new Project();
				pr.setType("ORGANIZATION");
				proj.populateVProject(pr);
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleProject for Organization ) " ) ;
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before calling handleProject for Organization ) " ) ;
				rslt = handleProject(prObject, pr);
				logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleProject for Organization ) " ) ;
				printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after calling handleProject for Organization ) " ) ;
				if (rslt == false)
					return false;
			} else if (
				attr.getNodeName().equalsIgnoreCase("type")
					&& attr.getNodeValue().equalsIgnoreCase("EXCEPTION")) {
				this.NACK = true;
			}
		}

		return rslt;
	}

	private boolean handleScoreCard(ScoreCard sc, Node ScoreCardNode) {
		print("SCORECARD");
		boolean rslt = true;
		for (Node child = ScoreCardNode.getFirstChild();
			child != null;
			child = child.getNextSibling()) {
			int type = child.getNodeType();
			if (type == Node.ELEMENT_NODE) {

				if (child.getNodeName().equalsIgnoreCase("id")) {
					if (child.getFirstChild() != null) {
						print(child, 1);
						sc.setScoreCardId(
							child.getFirstChild().getNodeValue().trim());
					} else {
						logger.warn(
							"ERROR: SCORECARD Id is not supplied. Exiting the parser");
						
						nackXML.registerErrors(
							"SCORECARD",
							"EXCEPTION",
							"ID UNAVAILABLE",
							this.MISSING_ID_CODE,
							"");
						return false;
					}
				} else if (child.getNodeName().equalsIgnoreCase("attribute")) {
					//print(child, 1); 
					handleAttribute(child, sc, null, null, null);
				} else if (child.getNodeName().equalsIgnoreCase("object")) {
					//	print(child, 1); 
					NamedNodeMap startAttr = child.getAttributes();
					int opAttrLength = startAttr.getLength();
					for (int i = 0;
						i < opAttrLength;
						i++) { // This for loop sets the operation type from the attributes list
						Node attr = startAttr.item(i);
						print(attr, 2);
						if (attr.getNodeName().equalsIgnoreCase("type")
							&& attr.getNodeValue().equalsIgnoreCase(
								"SCORECARD")) {
							ScoreCard scChild = new ScoreCard();
							sc.populate_ScoreCards(scChild);
							rslt = handleScoreCard(scChild, child);
							if (rslt == false)
								return false;
						} else if (
							attr.getNodeName().equalsIgnoreCase("type")
								&& attr.getNodeValue().equalsIgnoreCase(
									"PUBLISHEDSCORECARDCATEGORY")) {
							ScoreCard scChild = new ScoreCard();
							sc.populate_ScoreCards(scChild);
							rslt = handleScoreCard(scChild, child);
							if (rslt == false)
								return false;
						}
					}
				}
			}
		}
		return true;
	}

	private boolean handleResource(Resource rs, Node ResourceNode) {
		print("RESOURCE");
		for (Node child = ResourceNode.getFirstChild();
			child != null;
			child = child.getNextSibling()) {
			int type = child.getNodeType();
			if (type == Node.ELEMENT_NODE) {

				if (child.getNodeName().equalsIgnoreCase("id")) {
					if (child.getFirstChild() != null) {
						print(child, 1);
						rs.setResourceID(
							child.getFirstChild().getNodeValue().trim());
					} else {
						logger.warn(
							"ERROR : Resource Id Unavailable for RESOURCE. Exiting the parser");
						
						
						nackXML.registerErrors(
							"RESOURCE",
							"EXCEPTION",
							"ID UNAVAILABLE",
							this.MISSING_ID_CODE,
							"");
						return false;
					}
				} else if (child.getNodeName().equalsIgnoreCase("attribute")) {
					//print(child, 1); 
					handleAttribute(child, null, rs, null, null);
				}
			}

		}
		// Populating all the resources in the resource vector for the sake of mailing the resource info for this project
		ResourceForMail.populateVResourceListForThisProject(rs);
		return true;
	}

	private boolean handleDocFolder(Doc doc, Node DocNode) {
		print("DOCUMENTFOLDER");
		doc.setDoc_Type("DOCUMENTFOLDER");
		boolean b = true;

		for (Node child = DocNode.getFirstChild();
			child != null;
			child = child.getNextSibling()) {
			int type = child.getNodeType();
			if (type == Node.ELEMENT_NODE) {

				if (child.getNodeName().equalsIgnoreCase("id")) {
					if (child.getFirstChild() != null) {
						print(child, 1);
						doc.setId(child.getFirstChild().getNodeValue().trim());
					} else {
						logger.warn(
							"ERROR - DOCFOLDER Id Unavailable for DOCFOLDER. Exiting the parser");
						
						nackXML.registerErrors(
							"DOCUMENT",
							"EXCEPTION",
							"ID UNAVAILABLE",
							this.MISSING_ID_CODE,
							"");
						return false;
					}
				} else if (child.getNodeName().equalsIgnoreCase("attribute")) {
					//print(child, 1); 
					handleDocFolderAttribute(doc, child);
				} else if (child.getNodeName().equalsIgnoreCase("object")) {
					//	print(child, 1); 
					NamedNodeMap startAttr = child.getAttributes();
					int opAttrLength = startAttr.getLength();
					for (int i = 0;
						i < opAttrLength;
						i++) { // This for loop sets the operation type from the attributes list
						Node attr = startAttr.item(i);
						print(attr, 2);
						if (attr.getNodeName().equalsIgnoreCase("type")
							&& attr.getNodeValue().equalsIgnoreCase(
								"DOCUMENTFOLDER")) {
							Doc childdoc = new Doc();
							childdoc.setDoc_Type("DOCUMENTFOLDER");
							doc.populateVDocs(childdoc);
							logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before handling doc folders for parent [ id : " +
										doc.getId()+ ", name : " + doc.getElement_Name() + "] ) " ) ;
							printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before handling doc folders for parent [ id : " +
															doc.getId()+ ", name : " + doc.getElement_Name() + "] ) " ) ;
							
							b = handleDocFolder(childdoc, child);
							logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after handling doc folders for parent [ id : " +
										doc.getId()+ ", name : " + doc.getElement_Name() + "] ) " ) ;
							if (b == false)
								return false;
						} else if (
							attr.getNodeName().equalsIgnoreCase("type")
								&& attr.getNodeValue().equalsIgnoreCase(
									"DOCUMENT")) {
							Doc childdoc = new Doc();
							childdoc.setDoc_Type("DOCUMENT");
							doc.populateVDocs(childdoc);
							logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before handling docs for parent [ id : " +
										doc.getId()+ ", name : " + doc.getElement_Name() + "] ) " ) ;
							printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before handling docs for parent [ id : " +
										doc.getId()+ ", name : " + doc.getElement_Name() + "] ) " ) ;
							
							b = handleDoc(childdoc, child);
							logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after handling docs for parent [ id : " +
										doc.getId()+ ", name : " + doc.getElement_Name() + "] ) " ) ;
							printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after handling docs for parent [ id : " +
										doc.getId()+ ", name : " + doc.getElement_Name() + "] ) " ) ;
							
							if (b == false)
								return false;
						}
					}
				}
			}
		}

		return true;
	}

	private void handleDocFolderAttribute(Doc doc, Node DocNode) {
		int add = -1;
		for (Node child = DocNode.getFirstChild();
			child != null;
			child = child.getNextSibling()) {

			int type = child.getNodeType();
			if (type == Node.ELEMENT_NODE) {
				if (child.getNodeName().equalsIgnoreCase("name")) {
					if (child
						.getFirstChild()
						.getNodeValue()
						.trim()
						.equalsIgnoreCase(DocAttributeHandling.ELEMENT_NAME)) {
						add = DocAttributeHandling.ADD_ELEMENT_NAME;
						print(child, 1);

					}
				} else if (child.getNodeName().equalsIgnoreCase("value")) {
					// if the xml structure has <value/> then return null and break	
					if (child.getFirstChild() == null) {
						print(null, 1);
						break;
					}

					if (add == DocAttributeHandling.ADD_ELEMENT_NAME) {
						print(child, 1);
						doc.setElement_Name(
							child.getFirstChild().getNodeValue().trim());
						break;
					}
				}
			}
		}
	}

	private boolean handleDoc(Doc doc, Node DocNode) {
		print("DOCUMENT");
		// subu 4.5.1 fix
		boolean b = true;
		doc.setDoc_Type("DOCUMENT");
		for (Node child = DocNode.getFirstChild();
			child != null;
			child = child.getNextSibling()) {
			int type = child.getNodeType();
			if (type == Node.ELEMENT_NODE) {

				if (child.getNodeName().equalsIgnoreCase("id")) {
					if (child.getFirstChild() != null) {
						print(child, 1);
						doc.setId(child.getFirstChild().getNodeValue().trim());
					} else {
						logger.warn(
							"ERROR - DOC Id  Unavailable for DOC. Exiting the parser");
						
						nackXML.registerErrors(
							"DOC",
							"EXCEPTION",
							"ID UNAVAILABLE",
							MISSING_ID_CODE,
							"");
						return false;
					}
				} else if (child.getNodeName().equalsIgnoreCase("attribute")) {
					//print(child, 1); 
					handleDocAttribute(child, doc);
				} else if (child.getNodeName().equalsIgnoreCase("object")) {
									/***/
					NamedNodeMap startAttr = child.getAttributes();
					int opAttrLength = startAttr.getLength();
					for (int i = 0;
						i < opAttrLength;
						i++) { // This for loop sets the operation type from the attributes list
						Node attr = startAttr.item(i);
						print(attr, 2);
						if (
							attr.getNodeName().equalsIgnoreCase("type")
								&& attr.getNodeValue().equalsIgnoreCase(
									"DOCUMENT")) {
							Doc childdoc = new Doc();
							childdoc.setDoc_Type("DOCUMENT");
							doc.populateVDocs(childdoc);
							logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before handling docs for parent [ id : " +
										doc.getId()+ ", name : " + doc.getElement_Name() + "] ) " ) ;
							printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right before handling docs for parent [ id : " +
										doc.getId()+ ", name : " + doc.getElement_Name() + "] ) " ) ;
							
							b = handleDoc(childdoc, child);
							logger.debug("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after handling docs for parent [ id : " +
										doc.getId()+ ", name : " + doc.getElement_Name() + "] ) " ) ;
							printMemUsage("DOMParseXML :: Memory check : Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory() + " ( Right after handling docs for parent [ id : " +
										doc.getId()+ ", name : " + doc.getElement_Name() + "] ) " ) ;
							
							if (b == false)
								return false;
							}
						}

									/***/
					}
			}

		}
		return true;
	}

	private void handleDocAttribute(Node docNode, Doc doc) {
		//print("DOCUMENT ATTRIBUTES");
		int add = -1;
		for (Node child = docNode.getFirstChild();
			child != null;
			child = child.getNextSibling()) {

			int type = child.getNodeType();
			if (type == Node.ELEMENT_NODE) {
				if (child.getNodeName().equalsIgnoreCase("name")) {

					if (child
						.getFirstChild()
						.getNodeValue()
						.trim()
						.equalsIgnoreCase(DocAttributeHandling.ELEMENT_NAME)) {
						add = DocAttributeHandling.ADD_ELEMENT_NAME;
						print(child, 1);

					} else if (
						child
							.getFirstChild()
							.getNodeValue()
							.trim()
							.equalsIgnoreCase(
							DocAttributeHandling.ATTACHMENT) || 
					child
						.getFirstChild()
						.getNodeValue()
						.trim()
						.equalsIgnoreCase(
						DocAttributeHandling.ATTACHEMENT)) {
						add = DocAttributeHandling.ADD_ATTACHMENT;
						print(child, 1);

					} else if (
						child
							.getFirstChild()
							.getNodeValue()
							.trim()
							.equalsIgnoreCase(
							DocAttributeHandling.SUMMARY)) {
						add = DocAttributeHandling.ADD_SUMMARY;
						print(child, 1);
					} else if (
						child
							.getFirstChild()
							.getNodeValue()
							.trim()
							.equalsIgnoreCase(
							DocAttributeHandling.BLOB_DATA)) {
						add = DocAttributeHandling.ADD_BLOB_DATA;
						print(child, 1);
					} else if (
						child
							.getFirstChild()
							.getNodeValue()
							.trim()
							.equalsIgnoreCase(
							DocAttributeHandling.REVISION)) {
						add = DocAttributeHandling.ADD_REVISION;
						print(child, 1);
					} else if (
						child
							.getFirstChild()
							.getNodeValue()
							.trim()
							.equalsIgnoreCase(
							DocAttributeHandling.CREATION_DATE)) {
						add = DocAttributeHandling.ADD_CREATION_DATE;
						print(child, 1);
					} else if (
						child
							.getFirstChild()
							.getNodeValue()
							.trim()
							.equalsIgnoreCase(
							DocAttributeHandling.LAST_CHECKIN)) {
						add = DocAttributeHandling.ADD_LAST_CHECKIN;
						print(child, 1);
					} else if (
						child
							.getFirstChild()
							.getNodeValue()
							.trim()
							.equalsIgnoreCase(
							DocAttributeHandling.COMPRESSED_SIZE)) {
						add = DocAttributeHandling.ADD_COMPRESSED_SIZE;
						print(child, 1);
					} else if (
						child
							.getFirstChild()
							.getNodeValue()
							.trim()
							.equalsIgnoreCase(
							DocAttributeHandling.DOCUMENT_SIZE)) {
						add = DocAttributeHandling.ADD_DOCUMENT_SIZE;
						print(child, 1);
					}

				} else if (child.getNodeName().equalsIgnoreCase("value")) {
					// if the xml structure has <value/> then return null and break	
					if (child.getFirstChild() == null) {
						print(null, 1);
						break;
					}

					if (add == DocAttributeHandling.ADD_ELEMENT_NAME) {
						print(child, 1);
						doc.setElement_Name(
							child.getFirstChild().getNodeValue().trim());
						break;
					} else if (add == DocAttributeHandling.ADD_ATTACHMENT) {
						print(child, 1);
						doc.setAttachment(
							child.getFirstChild().getNodeValue().trim());
						break;
					} else if (add == DocAttributeHandling.ADD_SUMMARY) {
						print(child, 1);
						doc.setSummary(
							child.getFirstChild().getNodeValue().trim());
						break;
					} else if (add == DocAttributeHandling.ADD_BLOB_DATA) {
						print(child, 1);
						doc.setBlob_data(
							child.getFirstChild().getNodeValue().trim(),
							false);
					} else if (add == DocAttributeHandling.ADD_REVISION) {
						print(child, 1);
						doc.setRevision(
							child.getFirstChild().getNodeValue().trim());
						break;
					} else if (add == DocAttributeHandling.ADD_CREATION_DATE) {
						print(child, 1);
						doc.setCreation_Date(
							child.getFirstChild().getNodeValue().trim());
					} else if (add == DocAttributeHandling.ADD_LAST_CHECKIN) {
						print(child, 1);
						doc.setLast_Checkin(
							child.getFirstChild().getNodeValue().trim());
					} else if (
						add == DocAttributeHandling.ADD_COMPRESSED_SIZE) {
						print(child, 1);
						doc.setCompressed_size(
							child.getFirstChild().getNodeValue().trim());
					} else if (add == DocAttributeHandling.ADD_DOCUMENT_SIZE) {
						

						if (child.getFirstChild() != null) {
							print(child, 1);
							doc.setDocument_Size(
								child.getFirstChild().getNodeValue().trim());
						} else
							print(null, 1);
					}
				}

			}
		}

	}
	private boolean handleWBSItem(Node WBSItemNode, WBSElement wbsEle) {
		if (wbsEle instanceof Task) {
			print("TASK");
		} else if (wbsEle instanceof SummaryTask) {
			print("SUMMARY TASK");
		} else if (wbsEle instanceof Milestone) {
			print("MILESTONE");
		} else if (wbsEle instanceof Deliverable) {
			print("DELIVERABLE");
		} else if (wbsEle instanceof WorkProduct) {
			print("WORK PRODUCT");
		}
		for (Node child = WBSItemNode.getFirstChild();
			child != null;
			child = child.getNextSibling()) {
			int type = child.getNodeType();
			if (type == Node.ELEMENT_NODE) {

				if (child.getNodeName().equalsIgnoreCase("id")) {
					if (child.getFirstChild() != null) {
						print(child, 1);
						wbsEle.setId(
							child.getFirstChild().getNodeValue().trim());
					} else {
						logger.warn(
							"ERROR - ID Unavailable for WBSITEM. Exiting the parser");
						
						nackXML.registerErrors(
							"WBSITEM",
							"EXCEPTION",
							"ID UNAVAILABLE",
							this.MISSING_ID_CODE,
							"");
						return false;
					}
				} else if (child.getNodeName().equalsIgnoreCase("attribute")) {
					//print(child, 1); 
					handleWBSItemAttribute(child, wbsEle, null);
				} else if (child.getNodeName().equalsIgnoreCase("object")) {
					//print(child, 1);
					handleWBSItemObject(child, wbsEle);
				}
			}

		}
		return true;
	}
	private boolean handleTask(Node TaskNode, Project proj) {
		boolean b = true;
		Task task = new Task();
		task.setType("TASK");
		proj.populatevWBS(task);
		b = handleWBSItem(TaskNode, task);
		return b;
	}
	private boolean handleTask(Node TaskNode, WBSElement ele) {
		Task task = new Task();
		task.setType("TASK");
		ele.populatevWBS(task);
		boolean b = handleWBSItem(TaskNode, task);
		return b;
	}
	private boolean handleSummaryTask(Node SummaryTaskNode, Project proj) {
		SummaryTask sTask = new SummaryTask();
		sTask.setType("SUMMARYTASK");
		proj.populatevWBS(sTask);
		boolean b = handleWBSItem(SummaryTaskNode, sTask);
		return b;
	}
	private boolean handleSummaryTask(Node SummaryTaskNode, WBSElement ele) {
		SummaryTask sTask = new SummaryTask();
		sTask.setType("SUMMARYTASK");
		ele.populatevWBS(sTask);
		boolean b = handleWBSItem(SummaryTaskNode, sTask);
		return b;
	}
	private boolean handleMilestone(Node MileStoneNode, Project proj) {
		Milestone mStone = new Milestone();
		mStone.setType("MILESTONE");
		proj.populatevWBS(mStone);
		return handleWBSItem(MileStoneNode, mStone);

	}
	private boolean handleMilestone(Node MileStoneNode, WBSElement ele) {
		Milestone mStone = new Milestone();
		mStone.setType("MILESTONE");
		ele.populatevWBS(mStone);
		return handleWBSItem(MileStoneNode, mStone);

	}
	public boolean handleDeliverable(Node DeliverableNode, Project proj) {
		Deliverable del = new Deliverable();
		del.setType("DELIVERABLE");
		proj.populatevWBS(del);
		return handleWBSItem(DeliverableNode, del);
	}
	public boolean handleDeliverable(Node DeliverableNode, WBSElement ele) {
		Deliverable del = new Deliverable();
		del.setType("DELIVERABLE");
		ele.populatevWBS(del);
		return handleWBSItem(DeliverableNode, del);
	}
	public boolean handleWorkProduct(Node WorkProductNode, Project proj) {
		WorkProduct wp = new WorkProduct();
		wp.setType("WORKPRODUCT");
		proj.populatevWBS(wp);
		return handleWBSItem(WorkProductNode, wp);
	}
	public boolean handleWorkProduct(Node WorkProductNode, WBSElement ele) {
		WorkProduct wp = new WorkProduct();
		wp.setType("WORKPRODUCT");
		ele.populatevWBS(wp);
		return handleWBSItem(WorkProductNode, wp);
	}
	private boolean handleCRIFolder(exception exc, Node CRIFolderNode) {

		print("CRIFOLDER");
		exc.setType("CRIFOLDER");
		boolean b = true;
		for (Node child = CRIFolderNode.getFirstChild();
			child != null;
			child = child.getNextSibling()) {
			int type = child.getNodeType();
			if (type == Node.ELEMENT_NODE) {

				if (child.getNodeName().equalsIgnoreCase("id")) {
					if (child.getFirstChild() != null) {
						print(child, 1);
						exc.setId(child.getFirstChild().getNodeValue().trim());
					} else {
						logger.warn(
							"WARNING - ID Unavailable for CRIFolder .CRIFolder Id is not supplied. Exiting the parser");

						nackXML.registerErrors(
							"CRIFolder",
							"EXCEPTION",
							"ID UNAVAILABLE",
							this.MISSING_ID_CODE,
							"");
						return false;
					}

				} else if (child.getNodeName().equalsIgnoreCase("attribute")) {
					//print(child, 1); 
					b = handle_exception(exc, child);
					if (b == false)
						return b;
				} else if (child.getNodeName().equalsIgnoreCase("object")) {
					//	print(child, 1); 
					NamedNodeMap startAttr = child.getAttributes();
					int opAttrLength = startAttr.getLength();
					for (int i = 0;
						i < opAttrLength;
						i++) { // This for loop sets the operation type from the attributes list
						Node attr = startAttr.item(i);
						print(attr, 2);
						if (attr.getNodeName().equalsIgnoreCase("type")
							&& attr.getNodeValue().equalsIgnoreCase(
								"CRIFOLDER")) {
							exception childcrifolder = new exception();
							childcrifolder.setType("CRIFOLDER");
							exc.populatevexception(childcrifolder);
							b = handleCRIFolder(childcrifolder, child);
							if (b == false)
								return b;
						} else if (
							attr.getNodeName().equalsIgnoreCase("type")
								&& attr.getNodeValue().equalsIgnoreCase(
									"CHANGEREQUEST")) {
							exception childexc = new exception();
							childexc.setType("CHANGEREQUEST");
							exc.populatevexception(childexc);
							b = handle_exception(childexc, child);
							if (b == false)
								return b;
						} else if (
							attr.getNodeName().equalsIgnoreCase("type")
								&& attr.getNodeValue().equalsIgnoreCase(
									"ISSUE")) {
							exception childexc = new exception();
							childexc.setType("ISSUE");
							exc.populatevexception(childexc);
							b = handle_exception(childexc, child);
							if (b == false)
								return b;
						}
					}
				}
			}
		}
		return true;
	}
	private boolean handleIssue(Node IssueNode, Project pr) {
		print("Issue");
		exception exc = new exception();
		exc.setType("ISSUE");
		pr.populatevexception(exc);
		return handle_exception(exc, IssueNode);

	}

	private boolean handleCR(Node CRNode, Project pr) {
		print("CR");
		exception exc = new exception();
		exc.setType("CR");
		pr.populatevexception(exc);
		return handle_exception(exc, CRNode);
	}

	private boolean handle_exception(exception exc, Node excNode) {
		boolean b = true;
		for (Node child = excNode.getFirstChild();
			child != null;
			child = child.getNextSibling()) {
			int type = child.getNodeType();
			if (type == Node.ELEMENT_NODE) {

				if (child.getNodeName().equalsIgnoreCase("id")) {
					if (child.getFirstChild() != null) {
						print(child, 1);
						exc.setId(child.getFirstChild().getNodeValue().trim());
					} else {
						logger.warn(
							"ERROR - ID Unavailable for ISSUE/CR . ISSUE/CR :Id is not supplied . Exiting the parser");
						
						nackXML.registerErrors(
							"ISSUE/CR",
							"EXCEPTION",
							"ID UNAVAILABLE",
							this.MISSING_ID_CODE,
							"");
						return false;
					}

				} else if (child.getNodeName().equalsIgnoreCase("attribute")) {
					//print(child, 1); 
					handle_exceptionAttribute(child, exc);
				} else if (child.getNodeName().equalsIgnoreCase("object")) {
					//print(child, 1);
					b = handle_exceptionObject(child, exc);
					if (b == false)
						return b;
				}
			}

		}
		return b;
	}

	private void handle_exceptionAttribute(Node attrNode, exception exc) {
		boolean RTFHandling = false;
		NamedNodeMap startAttr = attrNode.getAttributes();
		int opAttrLength = startAttr.getLength();
		for (int i = 0;
			i < opAttrLength;
			i++) { // This for loop sets the operation type from the attributes list
			Node attr = startAttr.item(i);
			print(attr, 2);
			if (attr.getNodeName().equalsIgnoreCase("type")
				&& attr.getNodeValue().equalsIgnoreCase("RTF")) {
				RTFHandling = true;
			}
		}
		if (RTFHandling == false) {
			int add = -1;
			for (Node child = attrNode.getFirstChild();
				child != null;
				child = child.getNextSibling()) {
				int type = child.getNodeType();
				if (type == Node.ELEMENT_NODE) {

					if (child.getNodeName().equalsIgnoreCase("name")) {
						if (child
							.getFirstChild()
							.getNodeValue()
							.trim()
							.equalsIgnoreCase(
								exceptionAttributeHandling.ELEMENT_NAME)) {
							add = exceptionAttributeHandling.ADD_ELEMENT_NAME;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								exceptionAttributeHandling.REFERENCE_NUMBER)) {
							add =
								exceptionAttributeHandling.ADD_REFERENCE_NUMBER;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								exceptionAttributeHandling.PRIORITY)) {
							add = exceptionAttributeHandling.ADD_PRIORITY;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								exceptionAttributeHandling.STAGE_ID)) {
							add = exceptionAttributeHandling.ADD_STAGE_ID;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								exceptionAttributeHandling.PROPOSED_BY)) {
							add = exceptionAttributeHandling.ADD_PROPOSED_BY;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								exceptionAttributeHandling
									.PROPOSED_DATETIME)) {
							add =
								exceptionAttributeHandling
									.ADD_PROPOSED_BY_DATETIME;
							print(child, 1);

						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.PUBLISHED)) {
							add = exceptionAttributeHandling.ADD_PUBLISHED;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.START_FINISH_DATE)) {
							add =
								exceptionAttributeHandling
									.ADD_START_FINISH_DATE;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.REVISION_HISTORY)) {
							add =
								exceptionAttributeHandling.ADD_REVISION_HISTORY;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.DURATION)) {
							add = exceptionAttributeHandling.ADD_DURATION;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.ETC)) {
							add = exceptionAttributeHandling.ADD_ETC;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ProjectAttributeHandling.PERCENT_COMPLETE)) {
							add =
								exceptionAttributeHandling.ADD_PERCENT_COMPLETE;
							print(child, 1);
						}

					} else if (child.getNodeName().equalsIgnoreCase("value")) {

						if (child.getFirstChild() == null) {
							print(null, 1);
							break;
						}

						if (add
							== exceptionAttributeHandling.ADD_ELEMENT_NAME) {

							print(child, 1);
							exc.setElement_Name(
								child.getFirstChild().getNodeValue().trim());

						} else if (
							add
								== exceptionAttributeHandling
									.ADD_REFERENCE_NUMBER) {

							print(child, 1);
							exc.setReference_Number(
								child.getFirstChild().getNodeValue().trim());

						} else if (
							add == exceptionAttributeHandling.ADD_PRIORITY) {

							print(child, 1);
							Hashtable htRankPriorityMap = null;
							//subu 4.5.1 fix mapping the priority/severity that we get from pmo depending on the range
							if(exc.getType().equalsIgnoreCase("CHANGEREQUEST")){
								htRankPriorityMap = ETSPMOGlobalInitialize.getHtChangeRequestRankRange();
							}
							else if(exc.getType().equalsIgnoreCase("ISSUE")){
								htRankPriorityMap = ETSPMOGlobalInitialize.getHtIssueRankRange();
							}
							String rankP = child.getFirstChild().getNodeValue().trim();
							String rankPValue = null;
							for(Enumeration e = htRankPriorityMap.keys(); e.hasMoreElements() ;) {
								
								String rankRangekey = (String)e.nextElement();
								int RangeMin		= Integer.parseInt(rankRangekey.substring(0, rankRangekey.indexOf("-")).trim());
								int RangeMax		= Integer.parseInt(rankRangekey.substring(rankRangekey.indexOf("-") + 1).trim());
								
								if(	Integer.parseInt(rankP) <= RangeMax &&
										Integer.parseInt(rankP) >= RangeMin){
											logger.debug("The rank value is : " + rankPValue);
											rankPValue = (String)htRankPriorityMap.get(rankRangekey);
										}
    					 	}
    					 	logger.debug("Setting the priority to: " + rankPValue);
							exc.setPriority(rankPValue);
						} else if (
							add == exceptionAttributeHandling.ADD_STAGE_ID) {

							print(child, 1);
							
							String StateValueFromPropertyFile = null;
							String state = (String)child.getFirstChild().getNodeValue().trim();
							Hashtable htStates = null;
							// subu 4.5.1 fix mapping the state that we get from
							// pmo depending on the type of exception
							if(exc.getType().equalsIgnoreCase("CHANGEREQUEST")){
								htStates = ETSPMOGlobalInitialize.getHtPMOtoETSChangeRequestStates() ;
							}
							else if(exc.getType().equalsIgnoreCase("ISSUE")){
								htStates = ETSPMOGlobalInitialize.getHtPMOtoETSIssueStates();
							}
							StateValueFromPropertyFile = (String)htStates.get(state);
							if(StateValueFromPropertyFile == null){
								StateValueFromPropertyFile = state;
							}
							logger.debug("Setting the State(Stage_Id) to: " + StateValueFromPropertyFile);
							exc.setStage_id(
								StateValueFromPropertyFile);
						} else if (
							add
								== exceptionAttributeHandling.ADD_PROPOSED_BY) {

							print(child, 1);
							exc.setProposed_By(
								child.getFirstChild().getNodeValue().trim());
						} else if (
							add
								== exceptionAttributeHandling
									.ADD_PROPOSED_BY_DATETIME) {

							print(child, 1);
							exc.setProposed_DateTime(
								child.getFirstChild().getNodeValue().trim());
						} else if (
							add == exceptionAttributeHandling.ADD_PUBLISHED) {
							print(child, 1);
							exc.setPublished(
								child.getFirstChild().getNodeValue().trim());
						} else if (
							add
								== exceptionAttributeHandling
									.ADD_START_FINISH_DATE) {
							print(child, 1);
							exc.setStart(
								child.getFirstChild().getNodeValue().trim());
						} else if (
							add
								== exceptionAttributeHandling
									.ADD_REVISION_HISTORY) {
							print(child, 1);
							exc.setRevision_history(
								child.getFirstChild().getNodeValue().trim());
						} else if (
							add == exceptionAttributeHandling.ADD_DURATION) {
							print(child, 1);
							exc.setDuration(
								child.getFirstChild().getNodeValue().trim());
						} else if (add == exceptionAttributeHandling.ADD_ETC) {
							print(child, 1);
							exc.setETC(
								child.getFirstChild().getNodeValue().trim());
						} else if (
							add
								== exceptionAttributeHandling
									.ADD_PERCENT_COMPLETE) {
							print(child, 1);
							exc.setPercentComplete(
								child.getFirstChild().getNodeValue().trim());
						}
					}
				} // type == Node.ELEMENT_NODE	
			} // for(Node child = attrNode...
		} // if RTFHandling == false
		else if (RTFHandling == true) {
			handle_exceptionRTFAttribute(attrNode, exc);
		}
	} //end method handleexceptionAttribute

	private void handle_exceptionRTFAttribute(Node attrNode, exception exc) {

					String name = null;
					String aliasName = null;
					String value = null;
					int rank = -1;
					
		/* ************************
		 * This for loop is a reverse loop. Traversing from the last child to the first child in reverse
		 * direction as the RTF information is structured in a reverse way.
		 * eg : <attribute type="RTF" >
		* 		<name>Name of RTF</name>
		* 		<value>CpcY3MxMCBcYWRkaXRpdmUgXHNzZW1p
		* 		</value>
		* 		<rank>2</rank>	
		* 		</attribute>
		* The first important information to be handled here is the rank and then the value . Name is irrelevant
		* if it has "Name of RTF" as its content. name could be something that might change later as XML gets 
		* changed.
		* ************************/
		int add = -1;
		for (Node child = attrNode.getLastChild();
			child != attrNode.getFirstChild();
			child = child.getPreviousSibling()) {
			int type = child.getNodeType();
			//logger.debug("The node name :" + child.getNodeName() + "node type : " + child.getNodeType());
			if (type == Node.ELEMENT_NODE) {
				
					
				if (child.getNodeName().equalsIgnoreCase("rank")) {
					rank	= Integer.parseInt(child.getFirstChild().getNodeValue().trim());
					if (Integer
						.parseInt(child.getFirstChild().getNodeValue().trim())
						== exceptionAttributeHandling.DESCRIPTION_RTF_RANKVALUE) {
						add = exceptionAttributeHandling.ADD_DESCRIPTION_RTF;
						print(child, 1);
					}
					if (Integer
						.parseInt(child.getFirstChild().getNodeValue().trim())
						== exceptionAttributeHandling
							.ISSUE_COMMENTS_RTF_RANKVALUE) {
						add = exceptionAttributeHandling.ADD_ISSUE_COMMENTS_RTF;
						print(child, 1);
					}
					if (Integer
						.parseInt(child.getFirstChild().getNodeValue().trim())
						== exceptionAttributeHandling.CR_COMMENTS_RTF_RANKVALUE) {
						add = exceptionAttributeHandling.ADD_CR_COMMENTS_RTF;
						print(child, 1);
					}
					
						

				}
				/*
					Here, in the child.getNodeName().equalsIgnoreCase("value") block, I try to add the
					RTFs. But these are redundant. I actually have a different algo to add exception RTFs
					which is below the child.getNodeName().equalsIgnoreCase("value") and child.getNodeName().equalsIgnoreCase("name")
					block. I create a RTF vector add all the exception RTFs into the vector.

					On 	7/21/04:I  commented the redundant info
					
					
				
				*/ 
				else if (child.getNodeName().equalsIgnoreCase("value")) {
					if(child.getFirstChild() != null){
						value	=	child.getFirstChild().getNodeValue().trim();
					}
					/*
					 * I have commented below as discussed above
					 * 
					 */
			/*	if (child.getFirstChild() == null) {
						print(null, 1);
					//	break;
					}
					if (add
						== exceptionAttributeHandling.ADD_DESCRIPTION_RTF) {
						print(child, 1);
						exc.setRTF1(
							child.getFirstChild().getNodeValue().trim());
					//	break;
					} else if (
						add
							== exceptionAttributeHandling
								.ADD_ISSUE_COMMENTS_RTF) {
						print(child, 1);
						exc.setRTF7(
							child.getFirstChild().getNodeValue().trim());
					//	break;
					} else if (
						add
							== exceptionAttributeHandling.ADD_CR_COMMENTS_RTF) {
						print(child, 1);
						exc.setRTF9(
							child.getFirstChild().getNodeValue().trim());
					//	break;
					}*/
				}
				else if(child.getNodeName().equalsIgnoreCase("name")) {
							print(child, 1);
							name	=	child.getFirstChild().getNodeValue().trim();
							// I get the mapped value from the ets_pmo_cri property file
							/*
							 *I spent a lot of time thinking why I dont have an alias names for
							 * project RTFs. Well,alias names for RTFs were decided only for PCRs and not projects. 
							 */
							
							if(exc.getType().equalsIgnoreCase("CHANGEREQUEST")){
								aliasName 	= 	(String)ETSPMOGlobalInitialize.getHtCRIRTF().get(new Integer(rank));
								if(aliasName == null){
										aliasName = name;	
								}
							}
							else if(exc.getType().equalsIgnoreCase("ISSUE")){
								aliasName 	= 	(String)ETSPMOGlobalInitialize.getHtISSUERTF().get(new Integer(rank));
								if(aliasName == null){
											aliasName = name;	
								}
							}
							
					
				}
			}
		}
		/*Bug in DOMParser( Strange problem)
		 * 
		 * This is causing problems 
		 * 	<attribute type="RTF"><name>Description</name>
			<value>e1xydGYxXGFuc2lcYW5zaWNwZzEyNTJcZGVmZjBcZGVmbGFuZzEwMzN7XGZvbnR0Ymx7XGYwXGZuaWxcZmNoYXJzZXQwIFRhaG9tYTt9fQ0KXHZpZXdraW5kNFx1YzFccGFyZFxmMFxmczIwIFRha2luZyB0aGUgTWVhc3VyZW1lbnRzIGF0IGNsaWVudCBQbGFjZQ0KXHBhciB9</value>
			<rank>1</rank></attribute>
			<attribute type="RTF"><name>Status</name><value></value><rank>4</rank></attribute>
			<attribute type="RTF"><name>Change Request RTF 9 - reserved for future use</name>
			<value>e1xydGYxXGFuc2lcYW5zaWNwZzEyNTJcZGVmZjBcZGVmbGFuZzEwMzN7XGZvbnR0Ymx7XGYwXGZuaWxcZmNoYXJzZXQwIFRhaG9tYTt9fQ0KXHZpZXdraW5kNFx1YzFccGFyZFxmMFxmczIwIFRha2luZyB0aGUgTWVhc3VyZW1lbnRzIGF0IGNsaWVudCBQbGFjZQ0KXHBhciB9</value><rank>9</rank></attribute>
		 * 
		 * But, this is fine : 
		 * <attribute type="RTF">
		 * <name>Description</name>
			<value>e1xydGYxXGFuc2lcYW5zaWNwZzEyNTJcZGVmZjBcZGVmbGFuZzEwMzN7XGZvbnR0Ymx7XGYwXGZuaWxcZmNoYXJzZXQwIFRhaG9tYTt9fQ0KXHZpZXdraW5kNFx1YzFccGFyZFxmMFxmczIwIFRha2luZyB0aGUgTWVhc3VyZW1lbnRzIGF0IGNsaWVudCBQbGFjZQ0KXHBhciB9</value>
			<rank>1</rank></attribute>
			<attribute type="RTF">
			<name>Status</name><value></value><rank>4</rank></attribute>
			<attribute type="RTF">
			<name>Change Request RTF 9 - reserved for future use</name>
			<value>e1xydGYxXGFuc2lcYW5zaWNwZzEyNTJcZGVmZjBcZGVmbGFuZzEwMzN7XGZvbnR0Ymx7XGYwXGZuaWxcZmNoYXJzZXQwIFRhaG9tYTt9fQ0KXHZpZXdraW5kNFx1YzFccGFyZFxmMFxmczIwIFRha2luZyB0aGUgTWVhc3VyZW1lbnRzIGF0IGNsaWVudCBQbGFjZQ0KXHBhciB9</value><rank>9</rank></attribute>
		 * 
		 * the new line between <attribute type="RTF"> and <name>Description</name> is the solution.
		 * The new line missed in the first example causes problems. <name>Description</name> tag is not parsed at all.
		 * This is the case with all the RTF attributes.
		 * So the solution requested to Boris is to put a line for all RTF attributes.
		 * Strange problem. Think it is a bug.
		 */
		 
			
				logger.debug("Loading the following RTFData constructor into exception's vRTF vector : RTFName : " + name + ", aliasname : " + aliasName + ", RTFValue : " + value + ", RTFRank : " + rank);
				RTFData rtf = new RTFData(name, aliasName, value, rank );
			
			/*
		 * Communication probs with SystemCorp. 
		 * They are sending values in plain text instead 
		 * of RTFs which was the agreement.
		 * Need to comment this so that it doesnt assume that value was in Base64.
		 * 
		 * And now everyone is back to the original version
		 */
			rtf.setValue(value);//Have to Have to Have to do this. setValue decodes the Base64 format 
			exc.populatevRTF(rtf);
			
	}
	private boolean handle_exceptionObject(Node attrNode, exception exc) {
		print("Exception OBJECTS");
		boolean b = true;
		NamedNodeMap startAttr = attrNode.getAttributes();
		int opAttrLength = startAttr.getLength();
		for (int i = 0;
			i < opAttrLength;
			i++) { // This for loop sets the operation type from the attributes list
			Node attr = startAttr.item(i);
			print(attr, 2);
			if (attr.getNodeName().equalsIgnoreCase("type")
				&& attr.getNodeValue().equalsIgnoreCase("DOCUMENT")) {
				Doc doc = new Doc();
				exc.populateVDocs(doc);
				doc.setDoc_Type("DOCUMENT");
				b = handleDoc(doc, attrNode);
				if (b == false)
					return false;

			} else if (
				attr.getNodeName().equalsIgnoreCase("type")
					&& attr.getNodeValue().equalsIgnoreCase("DOCUMENTFOLDER")) {
				Doc doc = new Doc();
				exc.populateVDocs(doc);
				doc.setDoc_Type("DOCUMENTFOLDER");
				b = handleDocFolder(doc, attrNode);
				if (b == false)
					return false;
			} else if (
				attr.getNodeName().equalsIgnoreCase("type")
					&& attr.getNodeValue().equalsIgnoreCase("ASSIGNEDRESOURCE")) {
				Resource rs = new Resource();
				exc.populateVResources(rs);
				handleResource(rs, attrNode);

			}

		}
		return true;
	}

	private void handleWBSItemAttribute(
		Node attrNode,
		WBSElement wbsItem,
		Resource rs) {
		/*if (wbsItem instanceof SummaryTask) {
			print("SUMMARYTASK ATTRIBUTE");			
		}
		else if(wbsItem instanceof Task){
			print("TASK ATTRIBUTE");	
		}
		else if(wbsItem instanceof Milestone){
			print(" MILESTONE ATTRIBUTE");
		}
		else if(wbsItem instanceof Deliverable){
			print(" DELIVERABLE ATTRIBUTE");
		}
		else if(wbsItem instanceof WorkProduct){
			print(" WORKPRODUCT ATTRIBUTE");
		}*/
		
		// Set isReportable flag in the WBSElement to true
		wbsItem.setIsReportable(true);
		
		boolean RTFHandling = false;
		NamedNodeMap startAttr = attrNode.getAttributes();
		int opAttrLength = startAttr.getLength();
		for (int i = 0;
			i < opAttrLength;
			i++) { // This for loop sets the operation type from the attributes list
			Node attr = startAttr.item(i);
			print(attr, 2);
			if (attr.getNodeName().equalsIgnoreCase("type")
				&& attr.getNodeValue().equalsIgnoreCase("RTF")) {
				RTFHandling = true;
			}
		}
		if (RTFHandling == false) {
			int add = -1;
			for (Node child = attrNode.getFirstChild();
				child != null;
				child = child.getNextSibling()) {
				int type = child.getNodeType();
				if (type == Node.ELEMENT_NODE) {

					if (child.getNodeName().equalsIgnoreCase("name")) {

					if (child
							.getFirstChild()
							.getNodeValue()
							.trim()
							.equalsIgnoreCase(
								WBSAttributeHandling.WBSWORK_PERCENT)) {
							add = WBSAttributeHandling.ADD_WBS_WORK_PERCENT;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.ELEMENT_NAME)) {
							add = WBSAttributeHandling.ADD_ELEMENT_NAME;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.PRIORITY)) {
							add = WBSAttributeHandling.ADD_PRIORITY;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.REFERENCE_NUMBER)) {
							add = WBSAttributeHandling.ADD_REFERENCE_NUMBER;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.PUBLISHED)) {
							add = WBSAttributeHandling.ADD_PUBLISHED;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.STAGE_ID)) {
							add = WBSAttributeHandling.ADD_STAGE_ID;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.REVISION_HISTORY)) {
							add = WBSAttributeHandling.ADD_REVISION_HISTORY;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.START_DT)) {
							add = WBSAttributeHandling.ADD_START_DT;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.FINISH_DT)) {
							add = WBSAttributeHandling.ADD_FINISH_DT;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.DURATION)) {
							add = WBSAttributeHandling.ADD_DURATION;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.WORK_PERCENT)) {
							add = WBSAttributeHandling.ADD_WORK_PERCENT;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.ETC)) {
							add = WBSAttributeHandling.ADD_ETC;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.PERCENT_COMPLETE)) {
							add = WBSAttributeHandling.ADD_PERCENT_COMPLETE;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.ACTUAL_EFFORT)) {
							add = WBSAttributeHandling.ADD_ACTUAL_EFFORT;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.REMAINING_EFFORT)) {
							add = WBSAttributeHandling.ADD_REMAINING_EFFORT;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.CONSTRAINT_TYPE)) {
							add = WBSAttributeHandling.ADD_CONSTRAINT_TYPE;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.CONSTRAINT_DATE)) {
							add = WBSAttributeHandling.ADD_CONSTRAINT_DATE;
							print(child, 1);
						}else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.SD)) {
							add = WBSAttributeHandling.ADD_SD;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.FD)) {
							add = WBSAttributeHandling.ADD_FD;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.EST_START_DT)) {
							add = WBSAttributeHandling.ADD_EST_START_DT;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.EST_FINISH_DT)) {
							add = WBSAttributeHandling.ADD_EST_FINISH_DT;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.PROPOSED_START_DT)) {
							add = WBSAttributeHandling.ADD_PROPOSED_START_DT;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.PROPOSED_FINISH_DT)) {
							add = WBSAttributeHandling.ADD_PROPOSED_FINISH_DT;
							print(child, 1);
						} 
						else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.SCHEDULED_START_DT)) {
							add = WBSAttributeHandling.ADD_SCHEDULED_START_DT;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.SCHEDULED_FINISH_DT)) {
							add = WBSAttributeHandling.ADD_SCHEDULED_FINISH_DT;
							print(child, 1);
						} 
						else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.FORECAST_START_DT)) {
							add = WBSAttributeHandling.ADD_FORECAST_START_DT;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.FORECAST_FINISH_DT)) {
							add = WBSAttributeHandling.ADD_FORECAST_FINISH_DT;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.BASELINE1_FINISH)) {
							add = WBSAttributeHandling.ADD_BASELINE1_FINISH_DT;
							print(child, 1);
						}
						else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.BASELINE2_FINISH)) {
							add = WBSAttributeHandling.ADD_BASELINE2_FINISH_DT;
							print(child, 1);
						}
						else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.BASELINE3_FINISH)) {
							add = WBSAttributeHandling.ADD_BASELINE3_FINISH_DT;
							print(child, 1);
						}
						else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.ACTUAL_FINISH)) {
							add = WBSAttributeHandling.ADD_ACTUAL_FINISH_DT;
							print(child, 1);
						}
						
						/* ***************
						 * security_id, logon_name, company_name are elements in Resource
						 * ***************/
						else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ResourceAttributeHandling.SECURITY_ID)) {
							add = ResourceAttributeHandling.ADD_SECURITY_ID;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ResourceAttributeHandling.LOGON_NAME)) {
							add = ResourceAttributeHandling.ADD_LOGON_NAME;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								ResourceAttributeHandling.COMPANY_NAME)) {
							add = ResourceAttributeHandling.ADD_COMPANY_NAME;
							print(child, 1);
						}
						/* ************
						 *  WBS elements
						 *  ***********/

						/* **************************************
						 * The RTFs are handled in handleRTFAttribute. The following else ifs 
						 * are never reached in present scenario. Could be use for future
						 * enhancements(if the xml gets altered)
						 * **************************************/
						else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.RTF1)) {
							add = WBSAttributeHandling.ADD_RTF1;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.RTF2)) {
							add = WBSAttributeHandling.ADD_RTF2;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.RTF3)) {
							add = WBSAttributeHandling.ADD_RTF3;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.RTF4)) {
							add = WBSAttributeHandling.ADD_RTF4;
							print(child, 1);
						} else if (
							child
								.getFirstChild()
								.getNodeValue()
								.trim()
								.equalsIgnoreCase(
								WBSAttributeHandling.RTF5)) {
							add = WBSAttributeHandling.ADD_RTF5;
							print(child, 1);
						}

					} else if (child.getNodeName().equalsIgnoreCase("value")) {
						/* if i dont have any value in my xml, dont set the field and break; */
						if (child.getFirstChild() == null) {
							print(null, 1);
							break;
						}

						if (add == WBSAttributeHandling.ADD_WBS_WORK_PERCENT) {
							print(child, 1);
							wbsItem.setWork_percent(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsWBSWORK_PERCENT_RANK
								== false) {
								
								break;
							}

						} else if (
							add == WBSAttributeHandling.ADD_ELEMENT_NAME) {
							print(child, 1);
							if (rs != null) {
								rs.setElement_name(
									child
										.getFirstChild()
										.getNodeValue()
										.trim());
							} else {
								wbsItem.setElement_name(
									child
										.getFirstChild()
										.getNodeValue()
										.trim());
							}
							if (WBSAttributeHandling.IsELEMENT_NAME_RANK
								== false) {
								
								break;
							}
						} else if (add == WBSAttributeHandling.ADD_PRIORITY) {
							print(child, 1);
							wbsItem.setPriority(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsPRIORITY_RANK
								== false) {
								
								break;
							}

						} else if (
							add == WBSAttributeHandling.ADD_REFERENCE_NUMBER) {
							print(child, 1);
							wbsItem.setReference_number(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsREFERENCE_NUMBER_RANK
								== false) {
								
								break;
							}
						} else if (add == WBSAttributeHandling.ADD_PUBLISHED) {
							print(child, 1);
							wbsItem.setPublished(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsPUBLISHED_RANK
								== false) {
								
								break;
							}
						} else if (add == WBSAttributeHandling.ADD_STAGE_ID) {
							print(child, 1);
							wbsItem.setState(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsSTAGE_ID_RANK
								== false) {
								
								break;
							}
						} else if (
							add == WBSAttributeHandling.ADD_REVISION_HISTORY) {
							print(child, 1);
							wbsItem.setRevision_history(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsREVISION_HISTORY_RANK
								== false) {
								
								break;
							}
						} else if (add == WBSAttributeHandling.ADD_START_DT) {
							print(child, 1);
							wbsItem.setStart(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsSTART_DT_RANK
								== false) {
								
								break;
							}
						} else if (add == WBSAttributeHandling.ADD_FINISH_DT) {
							print(child, 1);
							wbsItem.setFinish(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsFINISH_DT_RANK
								== false) {
								
								break;
							}
						} else if (add == WBSAttributeHandling.ADD_DURATION) {
							print(child, 1);
							wbsItem.setDuration(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsDURATION_RANK
								== false) {
								
								break;
							}
						} else if (
							add == WBSAttributeHandling.ADD_WORK_PERCENT) {
							print(child, 1);
							wbsItem.setWork_percent(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsWORK_PERCENT_RANK
								== false) {
								
								break;
							}
						} else if (add == WBSAttributeHandling.ADD_ETC) {
							print(child, 1);
							wbsItem.setETC(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsETC_RANK == false) {
								
								break;
							}
						} else if (
							add == WBSAttributeHandling.ADD_PERCENT_COMPLETE) {
							print(child, 1);
							wbsItem.setPercent_Complete(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsPERCENT_COMPLETE_RANK
								== false) {
								
								break;
							}
						} else if (
							add == WBSAttributeHandling.ADD_ACTUAL_EFFORT) {
							print(child, 1);
							wbsItem.setActual_Effort(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsACTUAL_EFFORT_RANK
								== false) {
								
								break;
							}
						} else if (
							add == WBSAttributeHandling.ADD_REMAINING_EFFORT) {
							print(child, 1);
							wbsItem.setRemaining_Effort(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsREMAINING_EFFORT_RANK
								== false) {
								
								break;
							}
						} else if (
							add == WBSAttributeHandling.ADD_CONSTRAINT_TYPE) {
							print(child, 1);
							wbsItem.setConstraintType(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsCONSTRAINT_TYPE_RANK
								== false) {
								
								break;
							}
						} else if (
							add == WBSAttributeHandling.ADD_CONSTRAINT_DATE) {
							print(child, 1);
							wbsItem.setConstraintType(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsCONSTRAINT_DATE_RANK
								== false) {
								
								break;
							}
						}
						else if (
							add
								== WBSAttributeHandling
									.ADD_SD) {
							print(child, 1);
							Hashtable htSDTypes = ETSPMOGlobalInitialize.getHtSDtypes();
							String SDValue = null;
							SDValue = (String)htSDTypes.get(child.getFirstChild().getNodeValue().trim());
							logger.debug("The SD value : " + SDValue);
							wbsItem.setSD(SDValue.substring(SDValue.indexOf(":") + 1));
							if (WBSAttributeHandling
								.IsSDRank
								== false) {
								
								break;
							}
						}
						else if (
							add
								== WBSAttributeHandling
									.ADD_FD) {
							print(child, 1);
							Hashtable htFDTypes = ETSPMOGlobalInitialize.getHtFDtypes();
							String FDValue = null;
							FDValue = (String)htFDTypes.get(child.getFirstChild().getNodeValue().trim());
							logger.debug("The FD value : " + FDValue);
							wbsItem.setFD(FDValue.substring(FDValue.indexOf(":") + 1));
							if (ProjectAttributeHandling
								.IsFDRank
								== false) {
								
								break;
							}
						}else if (
							add == WBSAttributeHandling.ADD_EST_START_DT) {
							print(child, 1);
							wbsItem.setEstimatedStart(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsEST_START_DT_RANK
								== false) {
								
								break;
							}
						} else if (
							add == WBSAttributeHandling.ADD_EST_FINISH_DT) {
							print(child, 1);
							wbsItem.setEstimatedFinish(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsEST_FINISH_DT_RANK
								== false) {
								
								break;
							}
						}	else if (
							add == WBSAttributeHandling.ADD_PROPOSED_START_DT) {
							print(child, 1);
							wbsItem.setProposedStart(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsPROPOSED_START_DT_RANK
								== false) {
								
								break;
							}
						} else if (
							add == WBSAttributeHandling.ADD_PROPOSED_FINISH_DT) {
							print(child, 1);
							wbsItem.setProposedFinish(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsPROPOSED_FINISH_DT_RANK
								== false) {
								
								break;
							}
						}	else if (
							add == WBSAttributeHandling.ADD_SCHEDULED_START_DT) {
							print(child, 1);
							wbsItem.setScheduledStart(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsSCHEDULED_START_DT_RANK
								== false) {
								
								break;
							}
						} else if (
							add == WBSAttributeHandling.ADD_SCHEDULED_FINISH_DT) {
							print(child, 1);
							wbsItem.setScheduledFinish(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsSCHEDULED_FINISH_DT_RANK
								== false) {
								
								break;
							}
						}
							else if (
							add == WBSAttributeHandling.ADD_FORECAST_START_DT) {
							print(child, 1);
							wbsItem.setForecastStart(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsFORECAST_START_DT_RANK
								== false) {
								
								break;
							}
						} else if (
							add == WBSAttributeHandling.ADD_FORECAST_FINISH_DT) {
							print(child, 1);
							wbsItem.setForecastFinish(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsFORECAST_FINISH_DT_RANK
								== false) {
								
								break;
							}
						}else if (
							add == WBSAttributeHandling.ADD_BASELINE1_FINISH_DT) {
							print(child, 1);
							wbsItem.setBaseline1Finish(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsBASELINE1_FINISH_RANK
								== false) {
								
								break;
							}
						}else if (
							add == WBSAttributeHandling.ADD_BASELINE2_FINISH_DT) {
							print(child, 1);
							wbsItem.setBaseline2Finish(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsBASELINE2_FINISH_RANK
								== false) {
								
								break;
							}
						}else if (
							add == WBSAttributeHandling.ADD_BASELINE3_FINISH_DT) {
							print(child, 1);
							wbsItem.setBaseline3Finish(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsBASELINE3_FINISH_RANK
								== false) {
								
								break;
							}
						}else if (
							add == WBSAttributeHandling.ADD_ACTUAL_FINISH_DT) {
							print(child, 1);
							wbsItem.setActualFinish(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsACTUAL_FINISH_RANK
								== false) {
								
								break;
							}
						}

						/* ****
						 * Resource
						 * ****/
						else if (
							add == ResourceAttributeHandling.ADD_SECURITY_ID) {
							print(child, 1);
							rs.setSecurity_id(
								child.getFirstChild().getNodeValue().trim());
							if (ResourceAttributeHandling.IsSECURITY_ID_RANK
								== false) {
								
								break;
							}
						} else if (
							add == ResourceAttributeHandling.ADD_LOGON_NAME) {
							print(child, 1);
							rs.setLogon_name(
								child.getFirstChild().getNodeValue().trim());
							if (ResourceAttributeHandling.IsLOGON_NAME_RANK
								== false) {
								
								break;
							}
						} else if (
							add
								== ResourceAttributeHandling.ADD_COMPANY_NAME) {
							print(child, 1);
							rs.setCompany_name(
								child.getFirstChild().getNodeValue().trim());
							if (ResourceAttributeHandling.IsCOMPANY_NAME_RANK
								== false) {
								
								break;
							}
						}
						/* **************************************
						 * The RTFs are handled in handleRTFAttribute. The following else ifs 
						 * are never reached in present scenario. Could be use for future
						 * enhancements(if the xml gets altered
						 * **************************************/
						else if (add == WBSAttributeHandling.ADD_RTF1) {
							print(child, 1);
							wbsItem.setRTF1(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsRTF1_RANK == false) {
								
								break;
							}
						} else if (add == WBSAttributeHandling.ADD_RTF2) {
							print(child, 1);
							wbsItem.setRTF2(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsRTF2_RANK == false) {
								
								break;
							}
						} else if (add == WBSAttributeHandling.ADD_RTF3) {
							print(child, 1);
							wbsItem.setRTF3(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsRTF3_RANK == false) {
								
								break;
							}
						} else if (add == WBSAttributeHandling.ADD_RTF4) {
							print(child, 1);
							wbsItem.setRTF4(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsRTF4_RANK == false) {
								
								break;
							}
						} else if (add == WBSAttributeHandling.ADD_RTF5) {
							print(child, 1);
							wbsItem.setRTF5(
								child.getFirstChild().getNodeValue().trim());
							if (WBSAttributeHandling.IsRTF5_RANK == false) {
								;
								break;
							}
						}

					} else if (child.getNodeName().equalsIgnoreCase("rank")) {
						if (add == WBSAttributeHandling.ADD_ELEMENT_NAME) {
							print(child, 1);
							wbsItem.setElement_name_Rank(
								child.getFirstChild().getNodeValue().trim());
							break;
						} else if (
							add == WBSAttributeHandling.ADD_REFERENCE_NUMBER) {
							print(child, 1);
							wbsItem.setReference_number_Rank(
								child.getFirstChild().getNodeValue().trim());
							break;
						} else if (add == WBSAttributeHandling.ADD_PUBLISHED) {
							print(child, 1);
							wbsItem.setPublished_Rank(
								child.getFirstChild().getNodeValue().trim());
							break;
						} else if (add == WBSAttributeHandling.ADD_STAGE_ID) {
							print(child, 1);
							wbsItem.setState_Rank(
								child.getFirstChild().getNodeValue().trim());
							break;
						} else if (
							add == WBSAttributeHandling.ADD_REVISION_HISTORY) {
							print(child, 1);
							wbsItem.setRevision_history_Rank(
								child.getFirstChild().getNodeValue().trim());
							break;
						} else if (add == WBSAttributeHandling.ADD_START_DT) {
							print(child, 1);
							wbsItem.setStart_Rank(
								child.getFirstChild().getNodeValue().trim());
							break;
						} else if (add == WBSAttributeHandling.ADD_FINISH_DT) {
							print(child, 1);
							wbsItem.setFinish_Rank(
								child.getFirstChild().getNodeValue().trim());
							break;
						} else if (add == WBSAttributeHandling.ADD_DURATION) {
							print(child, 1);
							wbsItem.setDuration_Rank(
								child.getFirstChild().getNodeValue().trim());
							break;
						} else if (
							add == WBSAttributeHandling.ADD_WORK_PERCENT) {
							print(child, 1);
							wbsItem.setWork_percent_Rank(
								child.getFirstChild().getNodeValue().trim());
							break;
						} else if (add == WBSAttributeHandling.ADD_ETC) {
							print(child, 1);
							wbsItem.setETC_Rank(
								child.getFirstChild().getNodeValue().trim());
							break;
						} else if (
							add == WBSAttributeHandling.ADD_PERCENT_COMPLETE) {
							print(child, 1);
							wbsItem.setPercent_Complete_Rank(
								child.getFirstChild().getNodeValue().trim());
							break;
						}
						/* *****
						 * Resource 
						 * *****/
						else if (
							add == ResourceAttributeHandling.ADD_SECURITY_ID) {
							print(child, 1);
							rs.setSecurity_id_Rank(
								child.getFirstChild().getNodeValue().trim());
							break;
						}
					}
				}
			}
		} else if (RTFHandling == true) {
			handleWBSRTFAttribute(attrNode, wbsItem);
		}

	}

	private void handleWBSRTFAttribute(Node attrNode, WBSElement wbsItem) {

		/* ************************
		 * This for loop is a reverse loop. Traversing from the last child to the first child in reverse
		 * direction as the RTF information is structured in a reverse way.
		 * eg : <attribute type="RTF" >
		* 		<name>Name of RTF</name>
		* 		<value>CpcY3MxMCBcYWRkaXRpdmUgXHNzZW1p
		* 		</value>
		* 		<rank>2</rank>	
		* 		</attribute>
		* The first important information to be handled here is the rank and then the value . Name is irrelevant
		* if it has "Name of RTF" as its content. name could be something that might change later as XML gets 
		* changed.
		* ************************/
		int add = -1;
		RTFData rtf = new RTFData();
		for (Node child = attrNode.getLastChild();
			child != attrNode.getFirstChild();
			child = child.getPreviousSibling()) {
			int type = child.getNodeType();
			if (type == Node.ELEMENT_NODE) {
				if (child.getNodeName().equalsIgnoreCase("rank")) {
					if (Integer
						.parseInt(child.getFirstChild().getNodeValue().trim())
						== WBSAttributeHandling.RTF1_RANKVALUE) {
						add = WBSAttributeHandling.ADD_RTF1;
						rtf.setRank(WBSAttributeHandling.RTF1_RANKVALUE);
						rtf.setName(WBSAttributeHandling.RTF1);
						print(child, 1);
					}
					if (Integer
						.parseInt(child.getFirstChild().getNodeValue().trim())
						== WBSAttributeHandling.RTF2_RANKVALUE) {
						add = WBSAttributeHandling.ADD_RTF2;
						rtf.setRank(WBSAttributeHandling.RTF2_RANKVALUE);
						rtf.setName(WBSAttributeHandling.RTF2);
						print(child, 1);
					}
					if (Integer
						.parseInt(child.getFirstChild().getNodeValue().trim())
						== WBSAttributeHandling.RTF3_RANKVALUE) {
						add = WBSAttributeHandling.ADD_RTF3;
						rtf.setRank(WBSAttributeHandling.RTF3_RANKVALUE);
						rtf.setName(WBSAttributeHandling.RTF3);
						print(child, 1);
					}
					if (Integer
						.parseInt(child.getFirstChild().getNodeValue().trim())
						== WBSAttributeHandling.RTF4_RANKVALUE) {
						add = WBSAttributeHandling.ADD_RTF4;
						rtf.setRank(WBSAttributeHandling.RTF4_RANKVALUE);
						rtf.setName(WBSAttributeHandling.RTF4);
						print(child, 1);
					}
					if (Integer
						.parseInt(child.getFirstChild().getNodeValue().trim())
						== WBSAttributeHandling.RTF5_RANKVALUE) {
						add = WBSAttributeHandling.ADD_RTF5;
						rtf.setRank(WBSAttributeHandling.RTF5_RANKVALUE);
						rtf.setName(WBSAttributeHandling.RTF5);
						print(child, 1);
					}
				} else if (child.getNodeName().equalsIgnoreCase("value")) {
					// if the xml structure has <value/> then return null and break	
					if (child.getFirstChild() == null) {
						print(null, 1);
						break;
					}
					if (add == WBSAttributeHandling.ADD_RTF1) {
						print(child, 1);
						rtf.setValue(
							child.getFirstChild().getNodeValue().trim());
						wbsItem.setRTF1(
							child.getFirstChild().getNodeValue().trim());
						rtf.setValue(
							child.getFirstChild().getNodeValue().trim());
						wbsItem.populatevRTF(rtf);
						break;
					} else if (add == WBSAttributeHandling.ADD_RTF2) {
						print(child, 1);
						wbsItem.setRTF2(
							child.getFirstChild().getNodeValue().trim());
						rtf.setValue(
							child.getFirstChild().getNodeValue().trim());
						wbsItem.populatevRTF(rtf);
						break;
					} else if (add == WBSAttributeHandling.ADD_RTF3) {
						print(child, 1);
						wbsItem.setRTF3(
							child.getFirstChild().getNodeValue().trim());
						rtf.setValue(
							child.getFirstChild().getNodeValue().trim());
						wbsItem.populatevRTF(rtf);
						break;
					} else if (add == WBSAttributeHandling.ADD_RTF4) {
						print(child, 1);
						wbsItem.setRTF4(
							child.getFirstChild().getNodeValue().trim());
						rtf.setValue(
							child.getFirstChild().getNodeValue().trim());
						wbsItem.populatevRTF(rtf);
						break;
					} else if (add == WBSAttributeHandling.ADD_RTF5) {
						print(child, 1);
						wbsItem.setRTF5(
							child.getFirstChild().getNodeValue().trim());
						rtf.setValue(
							child.getFirstChild().getNodeValue().trim());
						wbsItem.populatevRTF(rtf);
						break;
					}

				}
			}
		}

	}
public static void printMemUsage(String str){
	
	//str += "\n";
	//XMLProcessor.writeMemoryUsageToDisk(memoryUsageLoggingFile, str);
	
}
public static void cleanUp(){
	//XMLProcessor.cleanMemoryUsageToDisk();
}
	public static void main(String[] args) {
		//		DomParseXML dom = new DomParseXML("../XMLTutorial/projectCreateUpdateV1.xml");

		if(args.length < 1){
				if (logger.isDebugEnabled()) {
					logger
							.debug("main(String[]) -  Usage : DomParseXML <PropertyFileLocation> <NoOfTimesToExecuteDomParseXML>");
				}
				System.exit(0);
			}
		String str_NoOfTimesToExecuteDomParseXML = args[1];
		str_NoOfTimesToExecuteDomParseXML = str_NoOfTimesToExecuteDomParseXML.trim();
		int NoOfTimesToExecuteDomParseXML = Integer.parseInt(str_NoOfTimesToExecuteDomParseXML);
		PropertyConfigurator.configure(args[0]);	
		ETSPMOGlobalInitialize Global = new ETSPMOGlobalInitialize();
		if (Global.getProp() == null) {
			Global.Init();
		}
		//	System.out.println(Global.getXMLMsgLog()); 
		
		populateETS_PMO pop = null;
		try {
			pop = new populateETS_PMO();
		} catch (Exception ex)
		{ 
			logger.error("main(String[])", ex);
		}
		
		String filename = Global.getTestProjectCreateUpdateXMLDir();
		memoryUsageLoggingFile		=	ETSPMOGlobalInitialize.getProjectCreateUpdateXMLDir();
		if (logger.isDebugEnabled()) {
			logger
					.debug("main(String[]) - The file to be parsed is"
							+ filename);
		}
		

		int b = 1;
		
			long size = 0 ;
			for(int count = 0;count < NoOfTimesToExecuteDomParseXML; count ++){
				DomParseXML dom = new DomParseXML();
				try {
						long sz = (new File(filename).length());
						size += sz ;
						if (logger.isDebugEnabled()) {
							logger.debug("main(String[]) - Iteration "
									+ (count + 1) + " Parsing filename"
									+ filename + "(sz" + size + " Bytes)");
						}
						if (logger.isDebugEnabled()) {
							logger.debug("main(String[]) - Total size parsed"
									+ size);
						}
						printMemUsage("\n****************************************************************\n");
						printMemUsage("Iteration " + (count + 1) + " Parsing filename : " + filename + "(sz : " + size + " Bytes)");
						printMemUsage("Total size parsed : " + size);
						
						b = dom.Parse(filename, pop);
			
		} catch (Exception e) {
			logger.error(dom.getStackTrace(e));
			
		}

		if (b == 1) {
			//dom.ackXML.corrId = "ETS_PMO" + "ACK";
			//logMsg.print("\n\n********Could not process : " + dom.getXmlFile() + " as the file is formatted improperly" , logMsg.DEBUG_LEVEL1);
		
			dom.ackXML.setCorrId(Global.getPROJ_ACK_CORR_ID());
		
			dom.ackXML.setMessId(dom.getSUCCESS_CODE() + System.currentTimeMillis());
		
		//subu....commenting this for now . Remove this 
			//dom.getReadyToSendMQAck(dom.ackXML);
		
			//	dom.ackXML.SendToPMOMQ(true);
			}
		}
		cleanUp();
			
	}

	//  Warning Event Handler
	public void warning(SAXParseException e) throws SAXException {
		SAXWarningFlag = true;
		logger.warn("Warning:  " + e);
		
		nackXML.registerErrors(
			"ROOT",
			"WARNING",
			"SAXPARSEEXCEPTION",
			this.SAX_WARNING_CODE,
			"");
		nackXML.setCorrId(ETSPMOGlobalInitialize.getPROJ_NACK_CORR_ID());
		nackXML.setMessId(this.SAX_WARNING_CODE + System.currentTimeMillis());
		

	}

	//  Error Event Handler
	public void error(SAXParseException e) throws SAXException {
		SAXErrorFlag = true;

		logger.error("Error:  " + e);
		nackXML.registerErrors(
			"ROOT",
			"ERROR",
			"SAXPARSEEXCEPTION",
			this.SAX_ERROR_CODE,
			"");

		nackXML.setCorrId(ETSPMOGlobalInitialize.getPROJ_NACK_CORR_ID());
		nackXML.setMessId(this.SAX_ERROR_CODE + System.currentTimeMillis());

	}

	//  Fatal Error Event Handler
	public void fatalError(SAXParseException e) throws SAXException {
		SAXFatalErrorFlag = true;
		logger.fatal("Fatal Error:  " + e);
		
		nackXML.registerErrors(
			"ROOT",
			"ERROR",
			"SAXPARSEEXCEPTION",
			this.SAX_ERROR_CODE,
			"");
		nackXML.setCorrId(ETSPMOGlobalInitialize.getPROJ_NACK_CORR_ID());
		nackXML.setMessId(this.SAX_FATALERROR_CODE + System.currentTimeMillis());
		
		//this.getReadyToSendMQNack();
		//STOP STOP STOP...i cant  send a xml message with this error. This FATAL error stops my code completely
		// and i cant send a xml nack back because 
		// i just havent parsed anything.
		// one such examples are say </value> end tag missing

	}
	public void print(Node doc, int i) {
		if (doc != null) {
			if (i == 1){
				
				/* logger.debug("ELEMENT: " +
					doc.getNodeName() + " VALUE: " + 
					doc.getFirstChild().getNodeValue().trim()
					);
				*/	
			}
			else if (i == 2){
				
				logger.debug("ELEMENT: " +
					doc.getNodeName() + " VALUE: " + 
					doc.getNodeValue().trim());
					
			}

		} else {
			logger.debug("Value: " + "NULL\n");

		}
	}
	public void print(String step) {

		if (step != null) {
			step = step.trim();

			logger.debug("\n**************************************");
			logger.debug("Handling " + step);
			logger.debug("**************************************\n");
		}
	}
	
	public boolean createFakeTransactionObject(String fileAbsPath){
//		FileParser fp = new FileParser("C:\\Documents and Settings\\Administrator\\My Documents\\IBM\\wsappdevie\\workspace\\ETS-PMOIntegration\\StoredXMLs\\Project\\ProjectCreateUpdate\\V7-01-04\\RTFChanges.xml");
		FileParser fp = new FileParser(fileAbsPath);
		String transID 			= fp.ExtractTransactionID();
		String transVersion 	= fp.ExtractTransactionVersion();
		String source 			= fp.ExtractSource();
		String dest 			= fp.ExtractDestination();
		String app 				= fp.ExtractAppName();
		String userid 			= fp.ExtractUserID();
		String projectid 		= fp.ExtractProjectID();
		
		trans.setTransactionID(transID);
		trans.setTransactionVersion(transVersion);
		trans.setSource(source);
		trans.setDestination(dest);
		trans.setRepositoryApp(app);
		oper.setUserID(userid);
		
		Project pObj = new Project();
		pObj.setProjectId(projectid);
		oper.setProjObject(pObj);
		
		
		trans.setOperation(this.oper);
		nackXML.registerTransactionData(trans);

		nackXML.loadTransactionData(trans);

		nackXML.generate();
		nackXML.print();
		if (logger.isDebugEnabled()) {
			logger
					.debug("createFakeTransactionObject(String) - Printing nack 1 done");
		}
		
		nackXML.SendToPMOMQ(false); //false denotes this is a nack
		return false;
	}

	public boolean getReadyToSendMQNack() {
		nackXML.registerTransactionData(trans);

		nackXML.loadTransactionData(trans);

		nackXML.generate();
		
		/* 4.5.1 fix..subu  Changed to not send the NACKS anymore
		 * 
		 */
		/*nackXML.print();
		  ckXML.SendToPMOMQ(false); //false denotes this is a nack
		*/
		return false;

	}
		public boolean getReadyToSendMQNack(GenerateProjectCreateUpdateNackXML nackXML) {
		nackXML.registerTransactionData(trans);

		nackXML.loadTransactionData(trans);

		nackXML.generate();
		nackXML.print();
		
	
		nackXML.SendToPMOMQ(false); //false denotes this is a nack
		return false;

	}
	public boolean getReadyToSendMQAck() {
		ackXML.registerTransactionData(trans);

		ackXML.loadTransactionData(trans);

		ackXML.generate();
		ackXML.print();
		
		ackXML.SendToPMOMQ(true); //false denotes this is a nack
		return true;

	}
	
	/* This method is only used for main(String args[]) -- for testing purposes*/
		public boolean getReadyToSendMQAck(GenerateProjectCreateUpdateAckXML ackXML) {
		ackXML.registerTransactionData(trans);

		ackXML.loadTransactionData(trans);

		ackXML.generate();
		ackXML.print();
		
		ackXML.SendToPMOMQ(true); //false denotes this is a nack
		return true;

	}
	/*
	//  Recursive function : Traverse DOM Tree.  Print out Element Names
	private void traverse (Node node) {
	   	
		 	String content = "";
	  int type = node.getNodeType();
	  
	  
	  if (type == Node.ELEMENT_NODE){
	  		FormatTree(TabCounter);
	  		System.out.println("Element: " + node.getNodeName());
	
	  		if(node.hasAttributes()){
	  			NamedNodeMap AttributesList = node.getAttributes();
	  			for(int j = 0; j < AttributesList.getLength(); j++) {
	  					FormatTree(TabCounter);
	  					System.out.println("Attribute: " + 
	  										AttributesList.item(j).getNodeName() + 
	  										"=" + 
	  										AttributesList.item(j).getNodeValue());
	  								
	  			}// for(int j = 0...
	  		}// if(node.hasAttriutes()
	  }else if (type == Node.TEXT_NODE) {
	  	content = node.getNodeValue();
	    if (!content.trim().equals("")){
	            FormatTree(TabCounter);
	            System.out.println ("Character data: " + content);
			}
	  }else if (type == Node.COMMENT_NODE) {
	  			content = node.getNodeValue();
	            if (!content.trim().equals("")){
	          	  FormatTree(TabCounter);
	          	  System.out.println ("Comment: " + content);
	        	}	
	  }// end of if, else ifs on : type ==
	  
	  
	  NodeList children = node.getChildNodes();
	  if (children != null) {
	     for (int i=0; i< children.getLength(); i++){
	     	TabCounter ++ ;
	        traverse (children.item(i));
	        TabCounter -- ;
	     }  
	  }
	}
		
	private void FormatTree (int TabCounter) {
	    for(int j = 0; j < TabCounter; j++) {
	        System.out.print("\t");
	
	   }
	}
	*/

	/**
	 * Returns the sUCCESS_CODE.
	 * @return String
	 */
	public static String getSUCCESS_CODE() {
		return SUCCESS_CODE;
	}
	private String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		String stackTrace = sw.toString();
		pw.close();
		return stackTrace;
	}
	/**
	 * Returns the xmlFile.
	 * @return String
	 */
	public String getXmlFile() {
		return xmlFile;
	}

	/**
	 * @return
	 */
	public static String getCLASS_VERSION() {
		return CLASS_VERSION;
	}

}
