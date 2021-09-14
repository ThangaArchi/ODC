package oem.edge.ets_pmo.mq;
import oem.edge.ets_pmo.common.*;
import oem.edge.ets_pmo.datastore.util.*;
import oem.edge.ets_pmo.xml.*;
//import oem.edge.mailpref.Glob;
import oem.edge.ets_pmo.db.*;
import oem.edge.ets_pmo.common.mail.PostMan;

import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Vector;
import java.io.*;
import java.util.*;

import org.xml.sax.*;
//import org.apache.log4j.BasicConfigurator;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.ibm.mq.MQException;

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
public class XMLProcessor implements Runnable {
	static Logger logger = Logger.getLogger(XMLProcessor.class);
	private static String CLASS_VERSION = "5.2.1";
	public static int MQManagerIsDown = -1;	
	private String ErrorLog;
	private boolean looping;
	private ResourceBundle rb;
	private Vector vXMLMessages;	
	
    private boolean MessageInMQ =false;
    private int sleepTimeBeforeTroublingMQ = -1;
    private int sleepTimeDown = -1;
	private static int NO_OF_TIMES_TO_SPIN;
	
	private String getProjectCreateUpdateXMLDir ;
	//private populateETS_PMO pop = null;
	//private ExtractProjectXMLData epXML = null;
	
	private int TIMEOUTINTERVAL;
	
	private boolean usingDranoToCleanupTheCloggedMessagesInMQReceiveQueue;
	private static PostMan postman = null;
	public static String memoryUsageLoggingFile;
	public static File memFP = null;
	public static FileOutputStream memOut = null; 
	private static XMLReader parser = null;
	
	public XMLProcessor() { }
	
	public static void main(String args[]){
		if(args.length < 1){
				logger.error("main(String) -  Usage : XMLProcessor <log4jPropertyFileLocation>");
		}
		
		// Set up a simple configuration that logs on the console.
	    //BasicConfigurator.configure();
	   
	   /* 
	    * Tried PropertyConfigurator.configure("oem.edge.ets_pmo.ets_pmo_log4j");
	    * But looks like configure() needs absolute path. 
	    */
	   
	    PropertyConfigurator.configure(args[0]);
	    
		ETSPMOGlobalInitialize Global = new ETSPMOGlobalInitialize();
		if(ETSPMOGlobalInitialize.getProp() == null){
				ETSPMOGlobalInitialize.Init();	
		}
		
		
		XMLProcessor proc = new XMLProcessor();
		Thread t = new Thread(proc);
		proc.init();
		t.start();
		proc.startMerryGoAround();
		
	}
	
	
	// init the process
	public void init ()
	{
		rb			= ETSPMOGlobalInitialize.getProp();
		postman = new PostMan();
		this.looping	= ETSPMOGlobalInitialize.isLoopingFlag();
		this.sleepTimeBeforeTroublingMQ = ETSPMOGlobalInitialize.getSleepTimeBeforeTroublingMQ();
		this.sleepTimeDown =ETSPMOGlobalInitialize.getSleepTimeWhenMQIsDown();
		XMLProcessor.NO_OF_TIMES_TO_SPIN = ETSPMOGlobalInitialize.getNO_OF_TIMES_TO_SPIN_MERRY_GO_ROUND_IF_NO_MSG_FOUND();
		this.getProjectCreateUpdateXMLDir = ETSPMOGlobalInitialize.getProjectCreateUpdateXMLDir();
		memoryUsageLoggingFile		=	ETSPMOGlobalInitialize.getProjectCreateUpdateXMLDir();
		TIMEOUTINTERVAL = ETSPMOGlobalInitialize.getCR_TIMEOUTWINDOW();
		if(ETSPMOGlobalInitialize.getUsingDranoToCleanupTheCloggedMessagesInMQReceiveQueue() == 1){
					this.usingDranoToCleanupTheCloggedMessagesInMQReceiveQueue = true;
		}
		else{
					this.usingDranoToCleanupTheCloggedMessagesInMQReceiveQueue = false;
		}
		
	}
	
	
	
	// ===
	// thread loop to check txn table and send MQ message
	//
	public void run() {
		// The thread to send XML
		boolean Db2IsDown = false;
		long TIME_OF_BOARDING_RUN = System.currentTimeMillis();

		while( this.looping == true ){
			// block until we get a DB2Connection
			populateETS_PMO pop=null;
			 try{
			 	pop = new populateETS_PMO();
			 } catch (Exception e) {
			 	if (Db2IsDown==false) {
				   Db2IsDown = true; 
				   String errorStr = "Send XML Thread DB2 error : "+e;
				   logger.error("Send XML Thread DB2 error : ", e);
				   Date d = new Date();
				   String str = "[Send Thread] DB2 server is down on : " + d;
				   PostMan.DBDown(str+"\n"+errorStr);
				}
				try {
				  Thread.sleep(this.sleepTimeDown);
				}
				catch (InterruptedException ex) {}
				
				continue;	
						
			 }
			 	 
			  if(Db2IsDown == true){/* out of the DB2 Down problem...Now everything is fine */
					Db2IsDown = false;
					Date d = new Date();
					String str = "[Send Thread] DB2 back in running condition on : " + d;	
        	        PostMan.DBUp(str);
			  }	
				
				/*
				 * Here is where i send the CRs that have the flag status:C or U.
				 * I find the status from ets.ets_pmo_txn and 
				 * then get the related record from ets.pmo_issue_info
				 * and send the CRCreate/CRUpdate to MQ
				 */
		    try {
				   logger.debug("Calling pops selectNewlyCreatedRecords");
				   Vector v = pop.selectNewlyCreatedRecords(ETSPMOGlobalInitialize.getCR_CREATEDNEW_STATE());
				  if(v != null){
					     pop.callCreateIssueCRInGenerateIssueCRXML(v);
				  }
				  /*UPDATE*/
				  logger.debug("Calling pops selectNewlyUpdatedRecords");
				  v = pop.selectNewlyUpdatedRecords(ETSPMOGlobalInitialize.getCR_UPDATED_STATE());
				  if(v != null){
				  	pop.callUpdateIssueCRInGenerateIssueCRXML(v);
				  }
				  
					/* i am going to check this every n secs . n can be 200 secs, 500 secs, 5 hrs ( subu..this is the height of explanation)*/
					/*
					 * iN THE BELOW CODE, TIMEOUTINTEVAL IS USED AS HOW OFTEN I WANT checkTxnStatusforCandU to be executed.
					 * I use TIMEOUTINTEVAL in the method checkTxnStatusforCandU() too. But, there it represents the time interval
					 * for which i should wait for an ack.
					 * Q)what does checktxnstatusforCandU method do? 
					 * A) It checks every fixed timerval amnt of time( say 5 hrs) if the transaction record sent by us 
					 * received a ack/nack. If no, then it updates the flag with T or Timeout.
					 * 
					 */
				  
				  /*
				  	logger.debug("Calling pops checkTxnStatusforCandU");
				  	
				  	long TIME_SPENT_IN_RUN = System.currentTimeMillis();
					if(TIME_SPENT_IN_RUN - TIME_OF_BOARDING_RUN > TIMEOUTINTERVAL*60*60*1000){
						long diff = (TIME_SPENT_IN_RUN - TIME_OF_BOARDING_RUN)/1000;
						long timeoutinterval = TIMEOUTINTERVAL*60*60;
						logger.debug("Its been so long in this run loop. I am getting bored." + "Its been " + 
						diff + " secs which is greater than " +   timeoutinterval + " secs. " +
						" Let me do " + 
						" something different. Let me checkTxnStatusForCandU and update timeout to those records who have exceeded the limit");
					
						// create sent
						  v = pop.selectNewlyCreatedRecords(ETSPMOGlobalInitialize.getCR_CREATED_STATE_SENT());
						  if(v != null){
							     pop.callCreateIssueCRInGenerateIssueCRXML(v);
						  }
						  // update sent
						  v = pop.selectNewlyUpdatedRecords(ETSPMOGlobalInitialize.getCR_UPDATED_STATE_SENT());
						  if(v != null){
						  	pop.callUpdateIssueCRInGenerateIssueCRXML(v);
						  }
						  
						//pop.checkTxnStatusforCandU(TIMEOUTINTERVAL);
					}			
					*/
					
					logger.debug("Going To Sleep 2");
                    Thread.sleep(this.sleepTimeBeforeTroublingMQ);		
                    logger.debug("Alright I will wake up 2");
                    
			 } catch (SQLException e) {
				logger.error("SQL exception - run()", e);
			 } catch (Exception e) {
			 	logger.error("Exception - run()", e);
			 } finally {
			 	try {
			 	pop.cleanUpConnection();
			 	} catch (Exception e) { }
			 }
		}
		
	}
	
	// ===
	// This is the main loop to receive and process
	// MQ message
	// ===
	public void startMerryGoAround(){
	     String hostname=ETSPMOGlobalInitialize.getQHostName();
	     String channel=ETSPMOGlobalInitialize.getQChannelName();
	     String qManager=ETSPMOGlobalInitialize.getQManager();
	     String qName=ETSPMOGlobalInitialize.getQFrom();
	     String port=ETSPMOGlobalInitialize.getQPort();
	     
		//try{
			//PMOMQReceive rec = new PMOMQReceive(this, false);
		    ////MQReceive rec = new MQReceive(hostname, channel, qManager, qName, port);
			boolean sendMailFlagInPMOMQSend = true;
			
			/* MQManagerIsDown flag will have 3 states
			 * state = -1 : Never been in MQManager Down problem
			 * state = 1 : In MQManager Problem
			 * state = -2: Was in MQManagerProblem earlier. But now ok.
			 */
			
			if(this.looping == false){
				logger.info("Loop flag in the property file is not assigned to true");
			}
			
			int counter = NO_OF_TIMES_TO_SPIN;
			logger.info("Starting etspmo daemon....");
			boolean boo = true;
			//If the property NO_OF_TIMES_TO_SPIN_MERRY_GO_ROUND_IF_NO_MSG_FOUND is negative, this loop will go forever
			MQReceive rec = null;
			boolean Db2IsDown = false;
			boolean MqIsDown  = false;
			while( this.looping == true && counter != 0){
				
				// block until we get a DB2 Connection
				populateETS_PMO pop=null;
				 try{
				 	pop = new populateETS_PMO();
				 	if (pop!=null)
				 		pop.cleanUpConnection();
				 } catch (Exception e) {
				 	if (Db2IsDown==false) {
					   Db2IsDown = true; 
					   String errorStr = "Send XML Thread DB2 error : "+e;
					   logger.error("Receive XML Thread DB2 error : ", e);
					   Date d = new Date();
					   String str = "[Receive Thread] DB2 server is down on : " + d;
					   PostMan.DBDown(str+"\n"+errorStr);
					}
					try {
					  Thread.sleep(this.sleepTimeDown);
					}
					catch (InterruptedException ex) {}
					
					continue;	
							
				 }
				 	 
				  if(Db2IsDown == true){/* out of the DB2 Down problem...Now everything is fine */
						Db2IsDown = false;
						Date d = new Date();
						String str = "[Receive Thread] DB2 back in running condition on : " + d;	
	        	        PostMan.DBUp(str);
				  }		
				
				  
				  // MQ
				  try {
				  	rec = new MQReceive(hostname, channel, qManager, qName, port);
					rec.connect();
					rec.getMessage(this);
				  } catch (MQException mqe) {
				 	if (MqIsDown==false) {
						   MqIsDown = true; 
						   String errorStr="error during the Connect operation: "+ "CC = "+ mqe.completionCode+ "; RC = "+ mqe.reasonCode;
						   logger.error(errorStr);
						   
						   Date d = new Date();
						   String str = "MQ server is down on : " + d;
						   PostMan.MQDown(str+"\n"+errorStr);
						}
						try {
						  Thread.sleep(this.sleepTimeDown);
						}
						catch (InterruptedException ex) {}
						
						continue;	
								
					 } catch (IOException e) {
					 	if (MqIsDown==false) {
							   MqIsDown = true; 
							   String errorStr="error during the Connect operation: "+ e;
							   logger.error(errorStr);
							   
							   Date d = new Date();
							   String str = "IO error during the MQ processing on : " + d + "\nWill try to delete XML files";
							   // now delete the xml file so that it does not clug the file space
							   cleanUp(ETSPMOGlobalInitialize.getProjectCreateUpdateXMLDir(),"xml");
							   
							   PostMan.MQDown(str+"\n"+errorStr);
							}
							try {
							  Thread.sleep(this.sleepTimeDown);
							}
							catch (InterruptedException ex) {}
							
							continue;	
					
				   }  finally {
				 	if (rec!=null)
						try {
							rec.disConnect();
							rec = null;
						} catch (Throwable t)
						{}
				   }
					 	 
				    if(MqIsDown == true){/* out of the MQ Down problem...Now everything is fine */
							MqIsDown = false;
							Date d = new Date();
							String str = "MQ server back in running condition on : " + d;	
		        	        PostMan.MQUp(str);
					  }		
				  
					 
     			
				if(this.usingDranoToCleanupTheCloggedMessagesInMQReceiveQueue == false){
					if (this.RetrievePopulationOfXMLMessages() > 0){
						// this will takethe message and proess it.
						// the vector now becomes empty.
						processMQMessage();
					}
				}
				else {
					logger.warn("NOTE :Using Drano to clean up all the clogged messages");	
					logger.warn("NOTE: Will not be parsing/populating the messages ");
					logger.warn("NOTE: ets_pmo_mq's usingDranoToCleanupTheCloggedMessagesInMQReceiveQueue need an alteration from 1 to 0 to handle the messages"); 
				}
				// at this stage, the vector is always empty
				
				try {
                      Thread.sleep(this.sleepTimeBeforeTroublingMQ);
                     }
                catch(InterruptedException ie) { }			

		
		}//end while(this.looping == true)
			
		logger.debug("\n\nEnough of Merry-Go-Round. Go Home");
		/*
		}
		catch(Throwable t){
			logger.error(getStackTrace(t));
            
		} */
	}
	
	
	// ===
	// processMQMessage
	// ===
	public void processMQMessage(){
		MQXMLMessage msg	= (MQXMLMessage)this.retrieveXMLMessage(0);
		
		/*System.out.println(	"CorrID : " + msg.getCorrID() +
							"MessageID : " + msg.getMessageID() +
							"XML Data Content : " + msg.getXmlData());
							*/
		//logger.info("\n\n****************\nReceived : \n" + msg.toString() + "\n");
		////logger.info("\n\n**********\nWriting XML to the local disk: \n");
		File file = new File(msg.getFileName()); ////writeXMLFileToDisk(msg);
		ParseXML par = new ParseXML();
		populateETS_PMO pop = null;
		int parseRslt = -1;
		int isAck= -1; // not Ack
		try{
			pop = new populateETS_PMO();
			parseRslt = par.Parse(file.getAbsolutePath(), pop);


			if(parseRslt == 0){
				logger.info("\n\n********Could not process : " + file.getAbsolutePath() + " as error occurred");
				logger.info("\n\n********Error type : SQL or File IO error");
			isAck=0;
			PostMan.SendfileReceivedFromMQ(file.getAbsolutePath(),"SQL or File IO error");
			////PostMan.SendfileReceivedFromMQ(msg.getFileName()+".xml","SQL or File IO error");
			
			//dom.ackXML.setCorrId(ETSPMOGlobalInitialize.getPROJ_NACK_CORR_ID());
			//dom.getReadyToSendMQNack(); I already send it in DOMPArseXML
			
			}
			/* this is the SAXParseException case. I will not create a Nack and send back as I havent successfully
			 * completed parse() in DOMParseXML's parse method.
			 * 
			 */
			else if(parseRslt ==2){
				logger.info("\n\n********Could not process : " + file.getAbsolutePath() + " as the file is formatted improperly");
				logger.info("\n\n********XML file is  of type 'BadFormat'");
				//	changeFileName(file, "BadFormat.xml");
			isAck=0;
			PostMan.SendfileReceivedFromMQ(file.getAbsolutePath(),"BadFormat.xml");
			////PostMan.SendfileReceivedFromMQ(msg.getFileName()+".xml","BadFormat.xml");
			
			/* Here is where I process the file that threw SAXParseException.
			 */
			//dom.createFakeTransactionObject(file.getAbsolutePath());	 
			}
			else if(parseRslt == 1){
				logger.info("\n\n********finished Processing : " + file.getAbsolutePath());
				//	changeFileName(file, "Processed");
			
				isAck=1;
			//PostMan.SendfileReceivedFromMQ(file.getAbsolutePath(),"Sync Processed");
				PostMan.SendfileReceivedFromMQ(msg.getFileName()+".xml","Sync Processed");
			
				//dom.ackXML.setCorrId(ETSPMOGlobalInitialize.getPROJ_ACK_CORR_ID());
				//dom.ackXML.setMessId(DomParseXML.getSUCCESS_CODE());
				//dom.getReadyToSendMQAck();
			}
			else if(parseRslt == -1){
				//	logger.info("\n\n********finished Processing : " + file.getAbsolutePath());
				logger.info("\n********ACK XML file is  'Processed'");
				//	changeFileName(file, "Processed");
			    isAck= -1; // CR/ISSUE ACK/NACK, no action
				PostMan.SendfileReceivedFromMQ(file.getAbsolutePath(),"ACK for Issue/CR Processed");
				logger.info("Finished processing CR/ISSUE ACK or NACK");
			}
			
			
	
		}
		catch(SQLException e) {
			logger.error(XMLProcessor.getStackTrace(e));
		}
		catch(Exception e){
			logger.error(XMLProcessor.getStackTrace(e));
		} finally {
			//
			try {
				if (isAck==1) {
					par.ackXML.setCorrId(ETSPMOGlobalInitialize.getPROJ_ACK_CORR_ID());
					par.ackXML.setMessId(ParseXML.getSUCCESS_CODE());
					par.getReadyToSendMQAck();
					
				} else if (isAck==0) {
					par.nackXML.setCorrId(ETSPMOGlobalInitialize.getPROJ_NACK_CORR_ID());
					par.nackXML.setMessId(ParseXML.SAX_ERROR_CODE + System.currentTimeMillis());
					par.getReadyToSendMQNack();
					
				}
				
			} catch (Exception e) {
				logger.error("send Ack/Nack - ",e);
			}
			//
			if (pop != null)
				try {
				pop.cleanUpConnection();
				} catch (Exception e) {}
            //	remove the xml files
			logger.info("*** Deleting XML file : " + file.getAbsolutePath() +  " *****") ;
			//file.delete();
			cleanUp(ETSPMOGlobalInitialize.getProjectCreateUpdateXMLDir(),"xml");
			clearMessage(0);
			// remove the  tmp files
			cleanUp(ETSPMOGlobalInitialize.getProjectTmpDir(),"zip");
		}
		
	}
	
	
	public void initialize_LogFiles(){
		
	}
	
	public static void printMem(String str)
	{
		java.lang.Runtime rt = Runtime.getRuntime();
		long free=rt.freeMemory();
		long total=rt.totalMemory();
		if (logger.isDebugEnabled()) {
			logger.debug("printMem(String)" + str + " -> Free:" + free
					+ ", Total:" + total);
		}
		
	}
	
	//
	//===
	//
	public void cleanUp(String pathname, String extension){
		//String pathname = ETSPMOGlobalInitialize.getProjectTmpDir();
		if (pathname==null)
			return;
		try {
			File dir = new File(pathname);
			if (dir.isDirectory())
			{
				File[] files = dir.listFiles();
				for (int i=0;i<files.length; i++)
				{
					File file=files[i];
					logger.debug("File to be deleted is "+ file.getName());
					if (file.getName().endsWith(extension))  //("zip"))
						file.delete();
				}
			}
		}
		catch (Exception e)
		{
			logger.error("cleanUp()", e);
		}
	}
	
	
	public boolean clearMessage(int i){
		boolean rtrn = true;
		if( (i > RetrievePopulationOfXMLMessages()) ||
			(i < 0)){
				rtrn = false;
				
			}
		logger.debug("\n\n***************\nI have been issued a clearMessage Command and I am removing the following message from the vXMLMessages vector\n\n");
		
		MQXMLMessage mes = (MQXMLMessage)retrieveXMLMessage(i);
		logger.debug("Corr ID : " + mes.getCorrID() + " Message ID : " + mes.getMessageID());
		vXMLMessages.remove(i);
		logger.debug("Message vector size :" + vXMLMessages.size());
		return rtrn;
		
	}
	public int RetrievePopulationOfXMLMessages() {
		if(vXMLMessages == null)
			return -1;
		return vXMLMessages.size();
	}
	
	public MQXMLMessage retrieveXMLMessage(int index) throws IndexOutOfBoundsException{
	MQXMLMessage res = null;
	if(this.vXMLMessages != null &&
		!this.vXMLMessages.isEmpty()){
			if(index >= vXMLMessages.size()){
				throw new IndexOutOfBoundsException("The index is beyond the limits of the vector: vXMLMessages");
			}
	res = (MQXMLMessage)vXMLMessages.get(index);
	}
	return res;
		
}
	public void populatevXMLMessages(MQXMLMessage Str) {
		if(this.vXMLMessages == null){
			vXMLMessages = new Vector();
		}
		vXMLMessages.add(Str);
	}
	/**
	 * Returns the messageInMQ.
	 * @return boolean
	 */
	public boolean isMessageInMQ() {
		return MessageInMQ;
	}

	/**
	 * Sets the messageInMQ.
	 * @param messageInMQ The messageInMQ to set
	 */
	public void setMessageInMQ(boolean messageInMQ) {
		MessageInMQ = messageInMQ;
	}
	

    public static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        String stackTrace = sw.toString();
        pw.close();
        return stackTrace;
    }

	/**
	 * @return
	 */
	public static String getCLASS_VERSION() {
		return CLASS_VERSION;
	}

	

}
