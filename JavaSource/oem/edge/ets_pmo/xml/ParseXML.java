package oem.edge.ets_pmo.xml;

import oem.edge.ets_pmo.common.*;
import oem.edge.ets_pmo.common.mail.PostMan;
import oem.edge.ets_pmo.datastore.*;
import oem.edge.ets_pmo.datastore.project.Project;
import oem.edge.ets_pmo.datastore.project.wbs.WBSElement;
import oem.edge.ets_pmo.datastore.resource.Resource;
import oem.edge.ets_pmo.datastore.util.RTFData;
import oem.edge.ets_pmo.datastore.document.Doc;
import oem.edge.ets_pmo.datastore.exception.exception;
import oem.edge.ets_pmo.db.*;
import oem.edge.ets_pmo.domain.DocObject;
import oem.edge.ets_pmo.domain.ExceptObject;
import oem.edge.ets_pmo.domain.OperObject;
import oem.edge.ets_pmo.domain.ResObject;
import oem.edge.ets_pmo.domain.RtfObject;
import oem.edge.ets_pmo.domain.TransObject;
import oem.edge.ets_pmo.domain.WbsObject;
import oem.edge.ets_pmo.util.FileParser;
import java.io.*;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.xerces.parsers.DOMParser;
import org.xml.sax.ErrorHandler;
//import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


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
 * @author shingte
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ParseXML implements ErrorHandler {
	private static String CLASS_VERSION = "5.2.1";
	static Logger logger = Logger.getLogger(ParseXML.class);

	//private int TabCounter = 0;
	
	//private Node transaction;
	//private Node operation;
	//private Node project = null;

	private  Transaction trans;
	private  Operation oper;
	private  Project proj_root;

	private boolean SAXWarningFlag = false;
	private boolean SAXErrorFlag = false;
	private boolean SAXFatalErrorFlag = false;

	private  GenerateBaseXML baseXML;
	public  GenerateProjectCreateUpdateAckXML ackXML;
	public  GenerateProjectCreateUpdateNackXML nackXML;

	public static String SAX_ERROR_CODE;
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
	
	private static Hashtable htSDTypes;
	private static Hashtable htFDTypes;
	private static Hashtable htIssuePriorityMap;
	private static Hashtable htCrPriorityMap;
	private static Hashtable htIssueStates;
	private static Hashtable htCrStates;
	private static Hashtable htIssueAlias;
	private static Hashtable htCrAlias;
	
	private void initialize() {
		baseXML = new GenerateBaseXML();
		//baseXML.registerTransactionData(this.trans);
		nackXML = new GenerateProjectCreateUpdateNackXML();
		ackXML = new GenerateProjectCreateUpdateAckXML();

		ETSPMOGlobalInitialize.Init();
		SAX_ERROR_CODE = ETSPMOGlobalInitialize.getSAX_ERROR_CODE();
		SAX_WARNING_CODE = ETSPMOGlobalInitialize.getSAX_WARNING_CODE();
		SAX_FATALERROR_CODE = ETSPMOGlobalInitialize.getSAX_FATALERROR_CODE();
		MISSING_ID_CODE = ETSPMOGlobalInitialize.getMISSING_ID_CODE();
		SUCCESS_CODE = ETSPMOGlobalInitialize.getSUCCESS_CODE();
		unknownUserID = ETSPMOGlobalInitialize.getUnknownUserId();
		ResourceForMail = new Resource();
		
		xmlValidation = ETSPMOGlobalInitialize.getXmlValidation();

		htSDTypes = ETSPMOGlobalInitialize.getHtSDtypes();
		htFDTypes = ETSPMOGlobalInitialize.getHtFDtypes();
		htIssuePriorityMap = ETSPMOGlobalInitialize.getHtIssueRankRange();
		htCrPriorityMap = ETSPMOGlobalInitialize.getHtChangeRequestRankRange();
		htIssueStates = ETSPMOGlobalInitialize.getHtPMOtoETSIssueStates();
		htCrStates = ETSPMOGlobalInitialize.getHtPMOtoETSChangeRequestStates();
		htIssueAlias = ETSPMOGlobalInitialize.getHtISSUERTF();
		htCrAlias = ETSPMOGlobalInitialize.getHtCRIRTF();
	}

	public ParseXML() {
		initialize();
	}

	/*
	public int run (String filename)
	{
	    Parser parser = new Parser();
        FileInputStream fis;
        Map params = null; //(fis);
		try {
			fis = new FileInputStream (filename);
			params = parser.parse (new BufferedInputStream(fis));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SAXException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("Memory after parsing -> Free - " + Runtime.getRuntime().freeMemory() + ", Total - " + Runtime.getRuntime().totalMemory());
        TransObject trans=(TransObject)params.remove("transaction");
		return 0;
   
	} */
	
	public int Parse(String xmlFileN, populateETS_PMO pop) throws Exception {
		boolean RSLT = true;
		//trans = new Transaction();
		//oper = new Operation();
		//Project proj = new Project();
	
		Parser parser = new Parser();
		
		xmlFile = xmlFileN;
		logger.info(
			"\n\n***********" + "The file to parse is: " + xmlFile
			);
		FileInputStream fis;
        Map params = null; 
        
        ackXML.setCorrId(ETSPMOGlobalInitialize.getPROJ_ACK_CORR_ID());
        nackXML.setCorrId(ETSPMOGlobalInitialize.getPROJ_NACK_CORR_ID());
		try {
			fis = new FileInputStream (xmlFile);
			params = parser.parse (new BufferedInputStream(fis));
			
			StringBuffer buf = (StringBuffer)params.remove("buffer");
			saveFilterXML(xmlFile+".xml", buf);
			
			TransObject transObj=(TransObject)params.remove("transaction");
			OperObject operObj=transObj.getOperation(); //(OperObject)params.remove("operation");
			WbsObject projObj=(WbsObject)operObj.getObject();
			// ignore the ACK of ACK
			if (projObj==null)
				return -2;
			
			trans = copyTrans(transObj);
			oper = copyOper(operObj);
			trans.setOperation(oper);
			baseXML.registerTransactionData(this.trans);
			
			Project proj = copyProj(projObj);
			oper.setProjObject(proj);
			oper.setProjObjectType(proj.getType());
			
			if (this.oper.getOperationType().equalsIgnoreCase("ACK")
				|| this.oper.getOperationType().equalsIgnoreCase("NACK")) {	
				    if (projObj.getNack() != null) {
				    	return -2;
				    }
					else if (ParseXML.NACK == true) {
						return handleNACK(pop);

					} else {
						return handleACK(pop);
		
					}
			}
			
			
			
			ExtractProjectXMLData epXML = new ExtractProjectXMLData(pop);
			
				RSLT = epXML.ExtractProjectData(proj, null, null, null);
				epXML.populateXMLInfoInResourceTable(trans.getOperation().getUserID(), 
													trans.getSource(), 
													trans.getDestination(), 
													trans.getRepositoryApp(), 
													trans.getTransactionVersion(), 
													trans.getOperation().getProjObject().getProjectId());
	

			if(epXML.isIsThisNewProject() == true){
				// checks if there?s LOTUS_PROJECT_ID in the ETS.ets_projects that matches the 
				// RPM_PROJECT_CODE for this RPM projects
				// insert the value of PMO_PROJECT_ID for this project to the PMO_PROJECT_ID 
				// field of ETS.ets_projects table 
				String workspace = null; //pop.matchProjectCode(proj.getRef_code());
				
				if (Resource.getResourceListForThisProject()!=null && Resource.getResourceListForThisProject().size() > 0) 
				{
					logger.debug("ResourceList size is greater than 0. This size :"
							+ Resource.getResourceListForThisProject().size());
					
					PostMan.ResourceInfoMail(
						proj.getProjectId(),
						proj.getElement_name(),
						proj.getRef_code(),
						workspace,
						Resource.getResourceListForThisProject());
				} else
					logger.warn("ResourceList size is less than 0");

			} 
			else{
				logger.debug("Not sending the Project Resource info in a mail as this project already exists");
				epXML.setIsThisNewProject(true);//Resetting the value 
			}
			
			
		}catch (SAXException e) {
			logger.error(this.getStackTrace(e));
			/* This handles the special case to 
			 * send a NACK when we receive a bad XML
			 * which is capable of throwing SAXPARSEException
			 */
			//createFakeTransactionObject();
			return 2;
			
			// lets define how to catch the error later
		} catch (Exception e) {
			logger.error(this.getStackTrace(e));
			//lets define how to catch the error later
			return 0;
		}
		
		return 1;
		//return RSLT;
	}

	// Save the XML without BLOB_DATA
	// This is the XML sent in the Email
	private void saveFilterXML(String filename, StringBuffer buf) {
		if(buf==null)
			return;
		
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(filename);
			out.write(buf.toString().getBytes());
			out.close();
		} catch (Exception e)
		{
			logger.error("saveFileterXML",e);
		}
		
	}

	private Project copyProj(WbsObject wbsObj) {
		Project wbs = new Project();
		proj_root = wbs;
		
		String SDvalue=wbsObj.getSd();
		if (SDvalue!=null)
		{
			SDvalue=(String)htSDTypes.get(SDvalue);
			wbs.setSD(SDvalue.substring(SDvalue.indexOf(":")+1));
		}
		String FDvalue=wbsObj.getFd();
		if (FDvalue!=null)
		{
			FDvalue=(String)htFDTypes.get(FDvalue);
			wbs.setFD(FDvalue.substring(FDvalue.indexOf(":")+1));
		}
		
		wbs.setIsReportable(wbsObj.isReportable());
		if(wbsObj.getType()!=null)
			wbs.setType(wbsObj.getType());
		if(wbsObj.getId()!=null)
			wbs.setProjectId(wbsObj.getId());
		if(wbsObj.getActual_finish()!=null)
			wbs.setActualFinish(wbsObj.getActual_finish());
		if(wbsObj.getBaseline1_finish()!=null)
			wbs.setBaseline1Finish(wbsObj.getBaseline1_finish());
		if(wbsObj.getBaseline2_finish()!=null)
			wbs.setBaseline2Finish(wbsObj.getBaseline2_finish());
		if(wbsObj.getBaseline3_finish()!=null)
			wbs.setBaseline3Finish(wbsObj.getBaseline3_finish());
		if(wbsObj.getElement_name()!=null)
			wbs.setElement_name(wbsObj.getElement_name());
		if(wbsObj.getCalendar_id()!=null)
			wbs.setCalendar_id(wbsObj.getCalendar_id());
		if(wbsObj.getCurrency_id()!=null)
			wbs.setCurrency_id(wbsObj.getCurrency_id());
		if(wbsObj.getDuration()!=null)
			wbs.setDuration(wbsObj.getDuration());
		if(wbsObj.getEtc()!=null)
			wbs.setEstimatedETC(wbsObj.getEtc());
		if(wbsObj.getStart_dt()!=null)
			wbs.setStart(wbsObj.getStart_dt());
		if(wbsObj.getFinish_dt()!=null)
			wbs.setFinish(wbsObj.getFinish_dt());
		if(wbsObj.getReference_number()!=null)
			wbs.setReference_number(wbsObj.getReference_number());
		if(wbsObj.getRevision_history()!=null)
			wbs.setRevision_history(wbsObj.getRevision_history());
		if(wbsObj.getWork_percent()!=null)
			wbs.setWorkPercent(wbsObj.getWork_percent());
		if(wbsObj.getRef_code()!=null)
			wbs.setRef_code(wbsObj.getRef_code());
		
		// WBS
		List wbo = wbsObj.getWbsobjects();
		if (wbo!=null && wbo.size()>0)
		{
			for (int i=0;i<wbo.size(); i++)
			{
				WbsObject obj = (WbsObject)wbo.get(i);
				WBSElement objcopy = copyWbs(obj);
				wbs.populatevWBS(objcopy);
			}
		}
		// RESOURCE
		List res = wbsObj.getResobjects();
		if (res!=null && res.size()>0)
		{
			for (int i=0;i<res.size(); i++)
			{
				ResObject obj = (ResObject)res.get(i);
				Resource objcopy = copyRes(obj);
				wbs.populateVResources(objcopy);
			}
		}		
		// RTF
		List rtfs = wbsObj.getRtfobjects();
		if (rtfs!=null && rtfs.size()>0)
		{
			for (int i=0;i<rtfs.size(); i++)
			{
				RtfObject obj = (RtfObject)rtfs.get(i);
				RTFData objcopy = copyRtf(obj, null);
				wbs.populatevRTF(objcopy);
			}
		}	
		// DOC
		List docs = wbsObj.getDocobjects();
		if (docs!=null && docs.size()>0)
		{
			for (int i=0;i<docs.size(); i++)
			{
				DocObject obj = (DocObject)docs.get(i);
				Doc objcopy = copyDoc(obj);
				wbs.populateVDocs(objcopy);
			}
		}
		// EXCEPTION
		List excepts = wbsObj.getExceptobjects();
		if (excepts!=null && excepts.size()>0)
		{
			for (int i=0;i<excepts.size(); i++)
			{
				ExceptObject obj = (ExceptObject)excepts.get(i);
				exception objcopy = copyExcept(obj);
				wbs.populatevexception(objcopy);
			}
		}
		
		return wbs;
	}

	private WBSElement copyWbs(WbsObject wbsObj) {
		WBSElement wbs = new WBSElement();
		//String SDvalue=(String)htSDTypes.get(wbsObj.getSd());
		//String FDvalue=(String)htFDTypes.get(wbsObj.getFd());
		//wbs.setSD(SDvalue.substring(SDvalue.indexOf(":")+1));
		//wbs.setFD(FDvalue.substring(FDvalue.indexOf(":")+1));
		String SDvalue=wbsObj.getSd();
		if (SDvalue!=null)
		{
			SDvalue=(String)htSDTypes.get(SDvalue);
			wbs.setSD(SDvalue.substring(SDvalue.indexOf(":")+1));
		}
		String FDvalue=wbsObj.getFd();
		if (FDvalue!=null)
		{
			FDvalue=(String)htFDTypes.get(FDvalue);
			wbs.setFD(FDvalue.substring(FDvalue.indexOf(":")+1));
		}
		
		wbs.setIsReportable(wbsObj.isReportable());
		if(wbsObj.getType()!=null)
			wbs.setType(wbsObj.getType());
		if(wbsObj.getId()!=null)
			wbs.setId(wbsObj.getId());
		if(wbsObj.getActual_finish()!=null)
			wbs.setActualFinish(wbsObj.getActual_finish());
		if(wbsObj.getBaseline1_finish()!=null)
			wbs.setBaseline1Finish(wbsObj.getBaseline1_finish());
		if(wbsObj.getBaseline2_finish()!=null)
			wbs.setBaseline2Finish(wbsObj.getBaseline2_finish());
		if(wbsObj.getBaseline3_finish()!=null)
			wbs.setBaseline3Finish(wbsObj.getBaseline3_finish());
		if(wbsObj.getElement_name()!=null)
			wbs.setElement_name(wbsObj.getElement_name());
		if(wbsObj.getDuration()!=null)
			wbs.setDuration(wbsObj.getDuration());
		if(wbsObj.getEtc()!=null)
			wbs.setETC(wbsObj.getEtc());
		if(wbsObj.getStart_dt()!=null)
			wbs.setStart(wbsObj.getStart_dt());
		if(wbsObj.getFinish_dt()!=null)
			wbs.setFinish(wbsObj.getFinish_dt());
		if(wbsObj.getReference_number()!=null)
			wbs.setReference_number(wbsObj.getReference_number());
		if(wbsObj.getRevision_history()!=null)
			wbs.setRevision_history(wbsObj.getRevision_history());
		if(wbsObj.getWork_percent()!=null)
			wbs.setWork_percent(wbsObj.getWork_percent());
		
		// WBS
		List wbo = wbsObj.getWbsobjects();
		if (wbo!=null && wbo.size()>0)
		{
			for (int i=0;i<wbo.size(); i++)
			{
				WbsObject obj = (WbsObject)wbo.get(i);
				WBSElement objcopy = copyWbs(obj);
				wbs.populatevWBS(objcopy);
			}
		}
		// RESOURCE
		List res = wbsObj.getResobjects();
		if (res!=null && res.size()>0)
		{
			for (int i=0;i<res.size(); i++)
			{
				ResObject obj = (ResObject)res.get(i);
				Resource objcopy = copyRes(obj);
				wbs.populateVResources(objcopy);
			}
		}
		// RTF
		List rtfs = wbsObj.getRtfobjects();
		if (rtfs!=null && rtfs.size()>0)
		{
			for (int i=0;i<rtfs.size(); i++)
			{
				RtfObject obj = (RtfObject)rtfs.get(i);
				RTFData objcopy = copyRtf(obj, null);
				wbs.populatevRTF(objcopy);
			}
		}
		// DOC
		List docs = wbsObj.getDocobjects();
		if (docs!=null && docs.size()>0)
		{
			for (int i=0;i<docs.size(); i++)
			{
				DocObject obj = (DocObject)docs.get(i);
				Doc objcopy = copyDoc(obj);
				wbs.populateVDocs(objcopy);
			}
		}
		/*
		// EXCEPTION
		List excepts = wbsObj.getExceptobjects();
		if (excepts!=null && excepts.size()>0)
		{
			for (int i=0;i<excepts.size(); i++)
			{
				ExceptObject obj = (ExceptObject)excepts.get(i);
				exception objcopy = copyExcept(obj);
				// put the exceptions under the project root
				proj_root.populatevexception(objcopy);
			}
		}
		*/
		
		return wbs;
	}
	
	private Operation copyOper(OperObject operObj) {
		Operation oper = new Operation();
		oper.setOperationType(operObj.getType());
		oper.setUserID(operObj.getUserid());
		return oper;
	}

	private Transaction copyTrans(TransObject transObj) {
		Transaction trans = new Transaction();
		trans.setDestination(transObj.getDestination());
		//trans.setOperation(copyOper(transObj.getOperation()));
		trans.setRepositoryApp(transObj.getApp());
		trans.setSource(transObj.getSource());
		trans.setTransactionID(transObj.getId());
		trans.setTransactionVersion(transObj.getVersion());
		
		return trans;
	}

	private Doc copyDoc(DocObject docObj)
	{
		//System.out.println(docObj.toString());
		Doc doc = new Doc();
		doc.setId(docObj.getId());
		doc.setElement_Name(docObj.getElement_name());
		doc.setDoc_Type(docObj.getType());
		if (docObj.getType()!=null && "DOCUMENT".equalsIgnoreCase(docObj.getType().trim()))
		{
			if(docObj.getCompressed_size()!=null)
				doc.setCompressed_size(docObj.getCompressed_size().trim());
			if(docObj.getCreation_date()!=null)
				doc.setCreation_Date(docObj.getCreation_date());
			if(docObj.getDocument_size()!=null)
				doc.setDocument_Size(docObj.getDocument_size().trim());
			if(docObj.getLast_checkin()!=null)
				doc.setLast_Checkin(docObj.getLast_checkin());
			doc.setRevision(docObj.getRevision());
			doc.setSummary(docObj.getSummary());
			doc.setAttachment(docObj.getAttachement());
			doc.setFilename(docObj.getBlob_data());
		}
		// DOC
		List docs = docObj.getDocobjects();
		if (docs!=null && docs.size()>0)
		{
			for (int i=0;i<docs.size(); i++)
			{
				DocObject obj = (DocObject)docs.get(i);
				Doc objcopy = copyDoc(obj);
				doc.populateVDocs(objcopy);
			}
		}
		
		return doc;
		
	}
	
	private exception copyExcept(ExceptObject excObj)
	{
		exception except = new exception();
		String rankP = excObj.getPriority();
		String rankPValue = null;
		Hashtable ht=null;
		Hashtable htStates=null;
		Hashtable htAlias=null;
		if(excObj.getType().equalsIgnoreCase("CHANGEREQUEST")) {
			ht = htCrPriorityMap;
			htStates = htCrStates;
			htAlias = htCrAlias;
		}
		else if(excObj.getType().equalsIgnoreCase("ISSUE")) {
			ht = htIssuePriorityMap;
			htStates = htIssueStates;
			htAlias = htIssueAlias;
		}
		// Priority
	    if (ht!=null && rankP!=null) {
		  for(Enumeration e = ht.keys(); e.hasMoreElements() ;) {
			String rankRangekey = (String)e.nextElement();
			int RangeMin = Integer.parseInt(rankRangekey.substring(0, rankRangekey.indexOf("-")).trim());
			int RangeMax = Integer.parseInt(rankRangekey.substring(rankRangekey.indexOf("-") + 1).trim());
			if(	Integer.parseInt(rankP) <= RangeMax && Integer.parseInt(rankP) >= RangeMin){
						logger.debug("The rank value is : " + rankPValue);
						rankPValue = (String)ht.get(rankRangekey);
						break;
			}
	 	  }
		  except.setPriority(rankPValue);
	    }
	    // Stage ID
	    if (htStates!=null && excObj.getStage_id()!=null)
	    {
	    	String state = (String)htStates.get(excObj.getStage_id());
	    	if(state == null){
	    		state = excObj.getStage_id();
	    	}
	    	logger.debug("Setting the State(Stage_Id) to: " + state);
	    	except.setStage_id(state);
	    }
	    
	    except.setId(excObj.getId());
	    except.setType(excObj.getType());
		except.setElement_Name(excObj.getElement_name());
		except.setProposed_By(excObj.getProposed_by_name());
		if(excObj.getProposed_datetime()!=null)
			except.setProposed_DateTime(excObj.getProposed_datetime());
		if(excObj.getReference_number()!=null)
			except.setReference_Number(excObj.getReference_number());
		
	
		
		// RESOURCE
		List res = excObj.getResobjects();
		if (res!=null && res.size()>0)
		{
			for (int i=0;i<res.size(); i++)
			{
				ResObject obj = (ResObject)res.get(i);
				Resource objcopy = copyRes(obj);
				except.populateVResources(objcopy);
			}
		}
		// RTF
		List rtfs = excObj.getRtfobjects();
		if (rtfs!=null && rtfs.size()>0)
		{
			for (int i=0;i<rtfs.size(); i++)
			{
				RtfObject obj = (RtfObject)rtfs.get(i);
				RTFData objcopy = copyRtf(obj, htAlias);
				except.populatevRTF(objcopy);
			}
		}
		// DOC
		List docs = excObj.getDocobjects();
		if (docs!=null && docs.size()>0)
		{
			for (int i=0;i<docs.size(); i++)
			{
				DocObject obj = (DocObject)docs.get(i);
				Doc objcopy = copyDoc(obj);
				except.populateVDocs(objcopy);
			}
		}
		// EXCEPTION
		List excepts = excObj.getExceptobjects();
		if (excepts!=null && excepts.size()>0)
		{
			for (int i=0;i<excepts.size(); i++)
			{
				ExceptObject obj = (ExceptObject)excepts.get(i);
				exception objcopy = copyExcept(obj);
				except.populatevexception(objcopy);
			}
		}
		
		return except;
	}
	
	
	private RTFData copyRtf(RtfObject rtfObj, Hashtable ht)
	{
		//logger.debug(rtfObj.toString());
		RTFData rtf = new RTFData();
		String alias=null;
		if (ht!=null && rtfObj.getRank()!=null)
		{
			alias = (String)ht.get(rtfObj.getRank());
			//logger.debug("rank="+rtfObj.getRank()+", alias="+alias);
		}
		if (alias==null)
			alias = rtfObj.getName();
		// (ht==null)? rtfObj.getName() : (String)ht.get(rtfObj.getRank());
		rtf.setValue(rtfObj.getValue());
		rtf.setAliasName(alias);
		rtf.setName(rtfObj.getName());
		if(rtfObj.getRank()!=null)
			rtf.setRank(Integer.parseInt(rtfObj.getRank().trim()));
		
		//logger.debug(rtf.toString());
		return rtf;
		
	}
	
	private Resource copyRes(ResObject resObj)
	{
		Resource res = new Resource();
		res.setElement_name(resObj.getElement_name());
		res.setLogon_name(resObj.getLogon_name());
		res.setResourceID(resObj.getId());
		res.setSecurity_id(resObj.getSecurity_id());
		
		// put together all of the resources
		Resource.populateVResourceListForThisProject(res);
		return res;
	}
	
	
	// handleNACK
	private int handleNACK(populateETS_PMO pop)
	{
		logger.debug(
				"ParseXML received a NACK with the transaction id: "
					+ trans.getTransactionID());
		String flagState;
		try {
				flagState = (String)pop.RetrieveFlagStatusofPCR(trans.getTransactionID());
			
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
		} catch (SQLException e) {
				logger.error("handleNACK(populateETS_PMO)", e);
		} catch (Exception e) {
				logger.error("handleNACK(populateETS_PMO)", e);
		}
		return -1;
	}
	
	//
	private int handleACK(populateETS_PMO pop)
	{
		// ACK
		logger.debug("ParseXML received an ACK with the transaction id: "
					+ trans.getTransactionID());
		//I am not hanlding duplicate acks. Should i handle them. I dont think i need to.
		// case 1) If i have C/U then i get A ..which is fine..
		// case 2) If i get an A and then i get a duplicate ack, I think i wont effect anything if i A the A.
		// I think i need to handle the case if the txnid is not present . let me handle with DoesThisTxnIdExistInTxnTable(String TXNid)
	  try {
		if (pop.DoesThisTxnIdExistInTxnTable(trans.getTransactionID())== false) {
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

		boolean isCreate=false;
		String flagState = (String)pop.RetrieveFlagStatusofPCR(trans.getTransactionID());
		flagState = flagState.trim();
		if (flagState.equalsIgnoreCase(ETSPMOGlobalInitialize.getCR_CREATED_STATE_SENT()))
			isCreate=true;
		
		
		if (typestr.equalsIgnoreCase("CRIFOLDER")) {
			typestr =((exception) (exc.getVexceptions().get(0))).getType();
			while (typestr.equalsIgnoreCase("CRIFOLDER")) {
				// stay in this loop till i get to the type "CHANGEREQUEST"

				exc = (exception) exc.getVexceptions().get(0);
				typestr = ((exception) (exc.getVexceptions().get(0))).getType();
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
				
				if(isCreate){
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
	
				
				if(isCreate){
					pop.populateRTFTableforCreatePCRs(trans.getTransactionID(),exc.getId(), typestr);
							
					// only set the owner info when issue created, but not when it's update
					String Owner_Id 		= pop.getIssueOwnerId("ETSPMO", "Defect",trans.getOperation().getProjObject().getProjectId());
					String Owner_Name		= pop.getIssueOwnerName(Owner_Id);
							
					logger.debug("Adding Owner ID : " + Owner_Id + " Owner_Name : " + Owner_Name + " information to the ets.PMO_ISSUE_INFO table for the issue : " + exc.getId());
							
					if(pop.updateOwnerInfoForTheIssue(exc.getId(), Owner_Id, Owner_Name) == false){
							logger.error("Couldn't Add Owner ID and Owner Name info for the issue : " + exc.getId());
					}
							
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
			
				if(isCreate){
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
			
				if(isCreate){
					pop.populateRTFTableforCreatePCRs(trans.getTransactionID(),exc.getId(), typestr);
					
					//	only set the owner info when issue created, but not when it's update
					String Owner_Id 		= pop.getIssueOwnerId("ETSPMO", "Defect",trans.getOperation().getProjObject().getProjectId());
					String Owner_Name		= pop.getIssueOwnerName(Owner_Id);
							
					logger.debug("Adding Owner ID : " + Owner_Id + " Owner_Name : " + Owner_Name + " information to the ets.PMO_ISSUE_INFO table for the issue : " + exc.getId());
							
					if(pop.updateOwnerInfoForTheIssue(exc.getId(), Owner_Id, Owner_Name) == false){
							logger.error("Couldn't Add Owner ID and Owner Name info for the issue : " + exc.getId());
					}
				}	
		} else {
			logger.error(
				"ParseXML received an ACK with the transaction id: "
					+ trans.getTransactionID());
			logger.warn(
				"The exception type in the project is not of following types : CRIFolder, CHANGEREQUEST, ISSUE");
			logger.debug(
				"Not updating any flags . Quitting");
			return 0;
		}
		logger.debug(
			"ParseXML received an ACK with the transaction id: "
				+ trans.getTransactionID());
		
		String userid = pop.RetrieveProjectManagerIdForThisTransaction(oper.getProjObject().getProjectId());
		
		String otherUserIds = null;
		if(typestr.equalsIgnoreCase("ISSUE")){
			otherUserIds = pop.RetrieveETS_CCListForIssues(exc.getId());
			if(otherUserIds ==null){
				logger.debug("No CC list found for Exception id : " + exc.getId());
				otherUserIds = "";
			}
		}
		
		logger.debug("userId="+userid+", cc userId="+otherUserIds);
		pop.sendIssueCRInfoInMailToProjMgr(trans.getTransactionID(), userid, otherUserIds, isCreate);
		
		
		//Updating the ACK
		pop.updateFlagInETS_PMO_TXN(trans.getTransactionID(), ETSPMOGlobalInitialize.getCR_ACKED_STATE().trim().charAt(0));
	
		//Delete the Issue/CR file and the No Of Trials File. On deleting this file, it means that
		//i will not resend this xml anymore( I resend a xml when i dont get an ack for the xml)
		////GenerateBaseXML.deleteXMLFileFromDisk(trans.getTransactionID());

		/* Here I am setting NACK to its original setting. Important to reset the value */
		NACK = false;
	  } catch (SQLException sqle) {
	  	logger.error(getStackTrace(sqle));
	  } catch (Exception e) {
	  	logger.error(getStackTrace(e));
	  }
	  return -1;

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
		if (ETSPMOGlobalInitialize.getProp() == null) {
			ETSPMOGlobalInitialize.Init();
		}
		//	System.out.println(Global.getXMLMsgLog()); 
		
		populateETS_PMO pop = null;
		try {
			pop = new populateETS_PMO();
		} catch (Exception ex) { 
			logger.error("main(String[])", ex);
		}
		
		String filename = ETSPMOGlobalInitialize.getTestProjectCreateUpdateXMLDir();

		
			
	}

	//  Warning Event Handler
	public void warning(SAXParseException e) throws SAXException {
		SAXWarningFlag = true;
		logger.warn("Warning:  " + e);
		
		nackXML.registerErrors(
			"ROOT",
			"WARNING",
			"SAXPARSEEXCEPTION",
			ParseXML.SAX_WARNING_CODE,
			"");
		nackXML.setCorrId(ETSPMOGlobalInitialize.getPROJ_NACK_CORR_ID());
		nackXML.setMessId(ParseXML.SAX_WARNING_CODE + System.currentTimeMillis());
		

	}

	//  Error Event Handler
	public void error(SAXParseException e) throws SAXException {
		SAXErrorFlag = true;

		logger.error("Error:  " + e);
		nackXML.registerErrors(
			"ROOT",
			"ERROR",
			"SAXPARSEEXCEPTION",
			ParseXML.SAX_ERROR_CODE,
			"");

		nackXML.setCorrId(ETSPMOGlobalInitialize.getPROJ_NACK_CORR_ID());
		nackXML.setMessId(ParseXML.SAX_ERROR_CODE + System.currentTimeMillis());

	}

	//  Fatal Error Event Handler
	public void fatalError(SAXParseException e) throws SAXException {
		SAXFatalErrorFlag = true;
		logger.fatal("Fatal Error:  " + e);
		
		nackXML.registerErrors(
			"ROOT",
			"ERROR",
			"SAXPARSEEXCEPTION",
			ParseXML.SAX_ERROR_CODE,
			"");
		nackXML.setCorrId(ETSPMOGlobalInitialize.getPROJ_NACK_CORR_ID());
		nackXML.setMessId(ParseXML.SAX_FATALERROR_CODE + System.currentTimeMillis());
		
		//this.getReadyToSendMQNack();
		//STOP STOP STOP...i cant  send a xml message with this error. This FATAL error stops my code completely
		// and i cant send a xml nack back because 
		// i just havent parsed anything.
		// one such examples are say </value> end tag missing

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
