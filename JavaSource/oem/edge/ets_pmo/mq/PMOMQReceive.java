package oem.edge.ets_pmo.mq;

import oem.edge.ets_pmo.common.*;
import oem.edge.ets_pmo.common.mail.PostMan;
import oem.edge.ets_pmo.datastore.util.MQXMLMessage;
import org.apache.log4j.Logger;
/************************** BOF : HEADER *************************************/
/* IBM Confidential                                                          */
/* OCO Source Materials                                                      */
/* Identifier: PROFIT                                                        */
/* (C) Copyright IBM Corp. 2000                                              */
/* The source code for this program is not published or otherwise divested   */
/* of its trade secrets, irrespective of what has been deposited with the    */
/* U.S. Copyright Office                                                     */
/* SOURCE OWNER : NAVNEET GUPTA. Modified by Subu
 */

import java.io.*;
import java.util.Properties;
import java.util.Date;
import java.util.StringTokenizer;



import com.ibm.mq.*;    // MQSeries classes for Java
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

public class PMOMQReceive  implements com.ibm.mq.MQSecurityExit{//Subu Change : Remove implements after creating SecurityExit class
	private static String CLASS_VERSION = "4.5.1";
    private Properties props, defaultProps;

    private String qManagerName;
    private String qName;

    private int[] nonFatalErrorCodes;
    private final int[] defaultNonFatalErrorCodes = {2009, 2059, 2162, 2195};

    private int numRetries;
    private int sleepTime;
    
    static Logger logger = Logger.getLogger(PMOMQReceive.class);
    private MQQueueManager qMgr;
    private MQQueue queue;
    private MQGetMessageOptions gmo;

    private static final int NO_MSG_AVAILABLE = 2033;
    
    private static String ErrorLogFile;
    private static String XMLMsgLog;
    
    private XMLProcessor xmlproc;
    
    	/*Ported subu 6/4 - next 2 lines*/
	private static String proj_corr_id; 
	
	private static String cr_nack_corr_id;
	private static String cr_ack_corr_id;
	

	
	static boolean sendMailSwitch;// is used to send the annoying mail only once if there is an encounter in MQ shutdown problem.

    public PMOMQReceive(XMLProcessor xmlproc,boolean sendMailSwitch) throws Throwable {

	     this.xmlproc = xmlproc;
	     this.sendMailSwitch = sendMailSwitch;
         initialize();
         
    }

	

    private boolean initialize() throws Throwable {
		// close any previous connections and queues
        cleanup();
		this.qName        = ETSPMOGlobalInitialize.getQFrom();

		this.qManagerName = ETSPMOGlobalInitialize.getQManager();
 		

  //      this.expiry       = Integer.parseInt(Glob.getq) * 60 * 10;  // tenths of a second
  //      this.sleepTime    = Integer.parseInt(getProperty("ed.MQSeries.sleepTime")) * 1000;

//        this.numRetries   = Integer.parseInt(Glob.get);
//        this.displayLevel = Integer.parseInt(getProperty("ed.MQSeries.displayLevel"));
        
 //       setNonFatalErrorCodes("ed.MQSeries.nonFatalErrorCodes");

/** After deploying in Production, the daemon couldnt connect
 * to MQ because of definition of Secutity exit it prod daemon which 
 * was not the case in dev or int daemon. 
 * 
 * * 
 * Bummer...
 * 
 * So I needed to take care of it with following code.
 * since, once fixpack season starts, one cannot create
 * new file to check in, I had to slip in the code with PMOMQReceive.java.
 * I need to create a new class called SecurityExit.java which has all the functionalities 
 * slipped in PMOMQReceive.java
 * 
 */
	
		MQEnvironment.securityExit = new PMOMQReceive();
        MQEnvironment.channel = ETSPMOGlobalInitialize.getQChannelName();
        MQEnvironment.hostname = ETSPMOGlobalInitialize.getQHostName();
        MQEnvironment.port = Integer.parseInt(ETSPMOGlobalInitialize.getQPort());
        

        int waitInterval  = Integer.parseInt(ETSPMOGlobalInitialize.getWaitInterval()) * 1000;
        this.sleepTime    = Integer.parseInt(ETSPMOGlobalInitialize.getSleepTime()) * 1000;
        this.numRetries   = Integer.parseInt(ETSPMOGlobalInitialize.getNumRetries());

        


        setNonFatalErrorCodes(ETSPMOGlobalInitialize.getNonFatalErrorCodes().trim());
 
        /*Ported subu 6/4 next 2 lines */
		
		cr_ack_corr_id = ETSPMOGlobalInitialize.getCR_ACK_CORR_ID();
		cr_nack_corr_id = ETSPMOGlobalInitialize.getCR_NACK_CORR_ID();
		proj_corr_id	=	ETSPMOGlobalInitialize.getPROJ_CORR_ID();

        int numRetry = 0;

        //try {
            while(true) {
                try {
                	
                	
                    this.qMgr = new MQQueueManager(this.qManagerName);
                   

                    // Set the options for the queue we wish to open. 
                    // Don't open if MQ is stopping
                    int openOptions = MQC.MQOO_INPUT_SHARED | MQC.MQOO_FAIL_IF_QUIESCING;
                    
                    // Open the queue.
                    queue = qMgr.accessQueue(
                                             this.qName,
                                             openOptions,
                                             null,       // default queue manager
                                             null,       // no dynamic queue name
                                             null        // no alternate user id
                                             );

                    // defaults (same as MQGMO_DEFAULT)
                    this.gmo = new MQGetMessageOptions();
                    
                    this.gmo.options = MQC.MQGMO_WAIT | MQC.MQGMO_SYNCPOINT;
                    this.gmo.waitInterval = waitInterval;
                    
                    print("initialized PMOMQReceive  on Queue: " + this.qName + "   QueueManager: " + this.qManagerName, false);

                    break;
                }
                catch(MQException mqe) {
                    if(numRetry <= this.numRetries) {
                        numRetry++;
                        if(isNonFatalMQException(mqe)) {
                            print("Encountered Non-Fatal MQException: " 
                                  + mqe.toString()
                                  + "\nFAILED ATTEMPTS: " + numRetry);

                          //  cleanup();
                            try {
                                Thread.sleep(this.sleepTime);
                            }
                            catch(InterruptedException ie) {
                                logger.error((getStackTrace(ie)));
                            }
                        }
                        else{
                        		print("Possiblity of MQManager down");
                            	throw mqe;
                        }
                    }
                    else{
                    		
                    		if(sendMailSwitch == true){
                        		String str = " MQ Manager : " + this.qManagerName + " found to be down  on " + new Date() + " when ETSDaemonForPMOffice started its engine.\n ETSDaemonForPMOffice doesnt need to be restarted. Will automatically pick up from where it left";
	                        	logger.error(str);
    	                    	PostMan p = new PostMan();
        	                	PostMan.MQDown(str);
                    		}
                        	return false;
                    }
                }
            }
            
            return true;
        }
      /*  catch(Throwable t) {
            cleanup();
            handleException(t);
        }
    }*/



    public boolean recieveMQMessage() throws Throwable {

        return recieveMQMessage(null, null);

    }



    public boolean recieveMQMessage(String correlationId, String messageId) throws Throwable {

        String[] message = new String[3];
        int numRetry = 0;

        try {
            while(true) {
			     try {
			     	XMLProcessor.printMem("before MQMessage ");
                    // Acquire a buffer to get the message.
                    MQMessage mqMessage = new MQMessage();


                    if(correlationId == null)
                        mqMessage.correlationId = MQC.MQCI_NONE;
                    else
                        mqMessage.correlationId = correlationId.getBytes();

                    
                    if(messageId == null)
                        mqMessage.messageId = MQC.MQMI_NONE;
                    else
                        mqMessage.messageId = messageId.getBytes();

                    XMLProcessor.printMem("before getMessage ");
                    
                    // Get the message from the queue.
                    try{
	                    this.queue.get(mqMessage, this.gmo);
                    }
                    catch(NullPointerException ne){
                    	logger.error(this.getStackTrace(ne));
                    	logger.error("The Queue unavailable to retrieve message. Possiblity of MQ MANAGER DOWN"); 	
                    	return false;
                    }
                   
                    XMLProcessor.printMem("after getMessage ");
                    
                    xmlproc.setMessageInMQ(true);
                    
                    MQXMLMessage mes	=	 new MQXMLMessage();
            
                    message[0] = new String(mqMessage.correlationId).trim();
                    mes.setCorrID(message[0]);
                    message[1] = new String(mqMessage.messageId).trim();
                    mes.setMessageID(message[1]);


                    if(mqMessage.messageType == MQC.MQMT_REPORT) {
//NOTE: need to create report files.
           			 if(mqMessage.feedback == MQC.MQFB_COA){
                  //   	 logReport(message[0], message[1], "COA");
                     }
                     else if(mqMessage.feedback == MQC.MQFB_COD){
                   //     	logReport(message[0], message[1], "COD");
                      }
                     else{
                            print("Received Report with invalid feedback: " + mqMessage.feedback + "\n"
                                  + "correlationId: " + message[0] + "   messageId: " + message[1], true);
                     }

                        break;

                    }


                    try {
                    	/* subu 6/4 */

                      		logger.info("Total MQ Message Length:  " + mqMessage.getMessageLength());
                      		
                      		
                      		String fileN = ""+ETSPMOGlobalInitialize.getProjectCreateUpdateXMLDir() + "Corr_" + message[0] + "_" +  System.currentTimeMillis();	
                      		BufferedWriter out = null;
                    		  try {
                    		  	
                    		  	out = new BufferedWriter(new FileWriter(fileN));
          
                        	////StringBuffer buff = new StringBuffer("");

                        	logger.info("The MQ Message's datalength : " + mqMessage.getDataLength());
							
                        	 
                        	   
                        	while(mqMessage.getDataLength() > 0){
                        		String str = mqMessage.readUTF();
								//String str = mqMessage.readLine();
                        		if(str != null){
                        			
                        			out.write(str);
    
	                        		////buff.append(str);
	                        		//XMLProcessor.printMem("during readUTF ");
	                        	//System.out.println("This time str length : " + str.length());
	                        	//	System.out.println("and str is : " + str);
                        		}
                        	//	else System.out.println("this chunk read is null");
                        //	System.out.println("The data length now is " + mqMessage.getDataLength());
                        		mqMessage.setDataOffset(mqMessage.getTotalMessageLength() - mqMessage.getDataLength());
                        	}
                    	}
                              catch(Throwable t) {
                                  logger.warn(
                                                     "\n\n******************** !!! writeXMLToDisk ERROR !!! ********************\n"
                                                     + "WARNING!: Error writing to " + fileN + " at " 
                                                     + new Date() + "\n"
                                                     + "Stack Trace:\n" 
                                                     + getStackTrace(t) + "\n"
                                                     + "While writing xml data\n"                                                           
                                                     + "*****************************************************************\n\n\n"
                                                     );
                  
                              }
                              out.close();
                       mes.setFileName(fileN);
                       ////message[2] = buff.toString();
                       //System.out.println("The message is : " + message[2]);
                  /*     for(int i = 0; i < 4 ; i ++){
                       	
                       		mqMessage.seek(i);
                       		System.out.println("Byte # : " + i + " is " + mqMessage.readUnsignedByte());
                       }*/
                        	
                       XMLProcessor.printMem("after setMessage2");	
                        		

                    }
                    catch(IOException e) {

                        try {
                            //byte[] arr = new byte[mqMessage.getMessageLength()];
                            //mqMessage.readFully(arr);
                            logger.error("The following message with correlationId: " + message[0] + " was NOT in UTF format:\n");
                               //   + new String(arr));
                           mes.setIsUTF(false);
                        }
                        catch(Throwable t1) { }

                        message[2] = "";

                    }
                    ////mes.setXmlData(message[2]);
                    
/*                    Boris is sending ack for the project acks that i send..need to ignore them
 * 
 * 
 */
 /* subu 6/4 removed the system.out changed the shouldipopulate's too..check out next 10 lines*/
 			//System.out.println("message[0]" + message[0]);
 					boolean shouldIpopulate = false;
                    if((message[0].trim()).equalsIgnoreCase(proj_corr_id) ||
                    	(message[0].trim()).equalsIgnoreCase(this.cr_ack_corr_id) ||
                    	 (message[0].trim()).equalsIgnoreCase(this.cr_nack_corr_id)){
		                   shouldIpopulate = true;
                    }
                    if(shouldIpopulate == true){
                     this.xmlproc.populatevXMLMessages(mes);
                    }
                    else{
                    	logger.info("Received an inappropriate message with corr id: " + message[0] + 
                    				" and the messageid: " + message[1]);	
                    	logger.debug("The message is : " + message[2]);
                    }

                    XMLProcessor.printMem("after populateMessage");
                    
                    break;

                } 
                catch(MQException mqe) {

                    if (mqe.reasonCode == NO_MSG_AVAILABLE) {

                        try {
                            this.qMgr.backout();
                        }
                        catch(MQException mqe1) {
                            logger.error(getStackTrace(mqe1));
                        }
						xmlproc.setMessageInMQ(false);
                        return true;

                    }


                    if(numRetry <= this.numRetries) {

                        numRetry++;

                        if(isNonFatalMQException(mqe)) {

                            try {
                                this.qMgr.backout();
                            }
                            catch(MQException mqe1) {
                                logger.error(getStackTrace(mqe1));
                            }

                            logger.info("Encountered Non-Fatal MQException: " 
                                  + mqe.toString()
                                  + "\nFAILED ATTEMPTS: " + numRetry);

                            cleanup();

                            try {
                                Thread.sleep(this.sleepTime);
                            }
                            catch(InterruptedException ie) {
                                logger.error(getStackTrace(ie));
                            }

                        }
                        else
                            throw mqe;
                    }
                    else
                        throw mqe;
                }
            }
        }
        catch(Throwable t) {
        	String str = " MQ Manager : " + this.qManagerName + " down and confirmed on " + new Date() + ".\n XMLProcessor is still running";
        	
        	PostMan p = new PostMan();
                        	PostMan.MQDown(str);

            try {
                this.qMgr.backout();
            }
            catch(Throwable t1) {
                logger.error(getStackTrace(t1));
            }

          //  cleanup();

           // handleException(t);
           return false;

        }

        this.qMgr.commit();
        return true;

    }



    private boolean isNonFatalMQException(MQException e) {
        for(int i = 0; i < this.nonFatalErrorCodes.length; i++)
            if(e.reasonCode == this.nonFatalErrorCodes[i])
                return true;
        
        return false;
    }
    


    public void cleanup() {
    	//gmo=null;
    	//xmlproc=null;
        if(this.qMgr != null && this.qMgr.isConnected()) {

            logger.debug("Cleaning up PMOMQReceive...");

            if(this.queue != null) {
                try {
                    this.queue.close();
                }
                catch(MQException e) {
                    logger.error(getStackTrace(e));
                }
                this.queue = null;
            }
            
            try {
                this.qMgr.disconnect();
            }
            catch(MQException e) {
               logger.error(getStackTrace(e));
            }

            this.qMgr = null;

        }
        else {
            this.queue = null;
            this.qMgr = null;
        }

    }


    
    
    private void handleException(Throwable t) throws Throwable {

        if(t instanceof MQException) {

            String mqPropsString
                = "Queue: "         + this.qName + "\n"
                + "Queue Manager: " + this.qManagerName + "\n"
                + "Hostname: "      + MQEnvironment.hostname + "\n"
                + "Channel: "       + MQEnvironment.channel + "\n"
                + "Port: "          + MQEnvironment.port + "\n";

            logger.error(
                  "An MQSeries error occurred:"
                  + "\nStackTrace:\n"
                  + getStackTrace(t)
                  + "\nThe properties used were:\n"
                  + mqPropsString);
                  

            throw (MQException) t;
            
        }
        else {
            logger.error(
                  "A NON-MQSeries error occurred:"
                  + "\nStackTrace:\n"
                  + getStackTrace(t));

            throw t;
        }
    }

    private static void print(String str) {
        print(str, true);
    }



    private static void print(String str, boolean error) {
     
            if(error){
                str 
                    = "******************** !!! MQ RECEIVE ERROR !!! ********************\n"
                    + "Printed at: " + new Date() + "\n"
                    + str
                    + "\n\n\n\n";
               logger.error(str);
            }
            else{
                str 
                    = "*************************** MQ Receive ***************************\n"
                    + "Printed at: " + new Date() + "\n"
                    + str
                    + "\n\n\n\n";
            }
			
                    
    }



    private void setNonFatalErrorCodes(String key) {
        String s = key;//this.props.getProperty(key);
        if(s != null && s.trim().length() != 0) {
            try {
                StringTokenizer st = new StringTokenizer(s.trim(), ",");
                int numTokens = st.countTokens();
                this.nonFatalErrorCodes = new int[numTokens];
                int i = 0;
                while(st.hasMoreTokens())
                    this.nonFatalErrorCodes[i++] = Integer.parseInt(st.nextToken().trim());
            }
            catch(Throwable t) {
                this.nonFatalErrorCodes = this.defaultNonFatalErrorCodes;
                logger.error(getStackTrace(t));
            }
        }
        else
            this.nonFatalErrorCodes = this.defaultNonFatalErrorCodes;
    }



    private static  String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        String stackTrace = sw.toString();
        pw.close();
        return stackTrace;
    }
    
    /*
     * NOTE..Need to create the Repost file with this method.
     * */
 public static void logReport(String correlationId, String messageId, String feedback) {

        try {
            
                String str = feedback + " for " + correlationId + " received at " + new Date() + "\n\n";
// Thread.sleep(1000);
// 
                String filename = getMQXMLMsgFileName(messageId, XMLMsgLog);
			logger.debug("The filename where i am writing COA and COD is : " + filename);
/*
int tries = 0;
do {
Thread.yield();
filename = getOrderFileName(messageId, LOGS_DIR + "/OrderLogs/");
tries++;
}
while(filename == null && tries < 10);
*/

                FileOutputStream out = new FileOutputStream(filename, true);
                out.write(str.getBytes());
                out.close();
            }
        
        catch(Exception e) {
            handleMinorException(e, "thrown processing " + feedback + " for CorrelID: " + correlationId + " MessageID: " + messageId);
        }

    }
    private static String getMQXMLMsgFileName(String prefix, String dir) {
			logger.debug("the dir is : " +  dir);
			logger.debug("The prefix is : " + prefix);
        try {
            prefix = prefix.trim() + ".";
            dir = dir.trim();

            if( ! dir.endsWith("/"))
                dir += "/";

            int matches = 0;
            String filename = null;

            String[] list = new File(dir).list();

            if(list == null)
                return null;

            for(int i = 0; i < list.length; i++)
                if(list[i].startsWith(prefix)) {
                    filename = dir + list[i];
                    matches++;
                }

            if(matches == 1)
                return filename;
            else if (matches == 0)
                return null;
            else {
                handleMinorException("Matches: " + matches + " for prefix: " + prefix + " and directory: " + dir);
                return null;
            }

        }
        catch(Exception e) {
                handleMinorException(e, "thrown getting OrderFileName for prefix: " + prefix + " and directory: " + dir);
                return null;
        }

    }
    
 static void handleMinorException(String str) {
        handleMinorException(new RuntimeException(), str);
    }
   static void handleMinorException(Throwable t, String str) {

        if(t.getClass().getName().endsWith("HandledException"))
            return;

        else {
            String stackTrace = getStackTrace(t);
            str += "\n\nStackTrace:\n" 
                + stackTrace 
                + "\n\n" 
                + "This exception was thrown at: " 
                + new Date();

            logger.error(str);

            try {
            	PostMan.mail("Warning from OrderProcessor", str);

            }
            catch(Throwable t1) {
            	logger.error(getStackTrace(t1));

            }
        }

    }
 


    public static void main(String [] args){
    	if(args.length < 1){
				System.out.println(" Usage : PMOMQReceive <PropertyFileLocation> " );
				System.exit(0);
			}
			

    	try{
    	PMOMQReceive one = new PMOMQReceive(null, true);
    	//one.recieveMQMessage("ETS_PROJsdf", null);

    	
    	}
    	catch(Throwable t){
    	System.out.println("caught n main(String [] args) ...Throwable : " + t);	
    	}
    	
    }
    
/** Changing here for including MQ's SecurityExit class. Need
 to create a new class when we get  a chance that can be 
 obtained from oem.edge.cc.mq */
	private boolean verbose = false;
    private String userName = null;

    /**
    * SecurityExit constructor comment.
    */
    /**
    * SecurityExit constructor comment.
    */
    public PMOMQReceive() {
        super();

        // get verbosity and other parameters
        // try {
            // exitProps = new Properties();
            // exitProps.load(new FileInputStream(propertyFileName));
            // setVerbose(exitProps.getProperty(exitVerbosePropertyName, "false"));

            if (verbose) {
                System.out.println("channel exit: verbose = " + verbose);
            }
        // }
        // catch (FileNotFoundException nfe) {
            // nfe.printStackTrace();
        // }
        // catch (IOException ioe) {
            // ioe.printStackTrace();
        // }
    }
	    /**
    * This method was created in VisualAge.
    * @return java.lang.String
    * @param stringVar java.lang.String
    */
    private String decryptString(String stringVar) {
        return null;
    }

    /**
    * This method was created in VisualAge.
    * @return java.lang.String
    * @param stringVar java.lang.String
    */
    private String encryptString(String stringVar) {
        return stringVar;
    }

    /**
    * securityExit method comment.
    */
    public byte[] securityExit(com.ibm.mq.MQChannelExit arg1,
            com.ibm.mq.MQChannelDefinition arg2, byte[] arg3) {

        switch (arg1.exitReason) {
            case MQChannelExit.MQXR_INIT :
                if (verbose) {
                    System.out.println("channel exit: Channel INIT state");
                }

                // get user name
                userName = System.getProperty("user.name");
			//System.out.println("yahoo..channel exit: user name = " + userName);
			
			//userName = "iccadm";
			//userName = "db2bms/db2inst1";
                // get host name
                // hostName = exitProps.getProperty("MQSeries.Host.Name",
                        // "junkHost");

                if (verbose) {
                    System.out.println("channel exit: user name = " + userName);
                    // System.out.println("channel exit: host name = " + hostName);
                }
                break;

            case MQChannelExit.MQXR_INIT_SEC :
                if (verbose) {
                    System.out.println("channel exit: Channel INIT_SEC state");
                }
                break;

            case MQChannelExit.MQXR_SEC_MSG :
                if (verbose) {
                    System.out.println("channel exit: Channel SEC_MSG state");
                    System.out.println("channel exit: rcvd msg = " + new String(arg3));
                }

                arg1.exitResponse = MQChannelExit.MQXCC_SEND_SEC_MSG;
                //return encryptString(userName).getBytes();
                return userName.getBytes();
                //return arg3;
            case MQChannelExit.MQXR_TERM :
                if (verbose) {
                    System.out.println("channel exit: Channel TERM state");
                }
                break;
        }

        // conditions that fall through return OK and no data
        arg1.exitResponse = MQChannelExit.MQXCC_OK;
        return null;
    }
    /**
    * This method was created in VisualAge.
    * @param verbosity boolean
    */
    private void setVerbose(String verbosity) {
        verbose = Boolean.valueOf(verbosity).booleanValue();
    }
    
    /************ Done implementing SecurityExit Class ****/
/**
 * @return
 */
public static String getCLASS_VERSION() {
	return CLASS_VERSION;
}

}

