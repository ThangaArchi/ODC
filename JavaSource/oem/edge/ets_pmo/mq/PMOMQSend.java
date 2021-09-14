package oem.edge.ets_pmo.mq;

import oem.edge.ets_pmo.common.*;
import java.io.*;

/**
 * @author (SOURCE OWNER NAVNEET GUPTA) Modified by SUBU 
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */




import java.util.Properties;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;



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

public class PMOMQSend {
	private static String CLASS_VERSION = "4.5.1";
    private Properties props, defaultProps;
    private String ERR_LOG_FILE;
    File err_log_file = null;

    private String qManagerName, qManagerReplyTo;
    private String qName, q2Name, q, qReplyTo;

    private int[] nonFatalErrorCodes;
  //  private final int[] defaultNonFatalErrorCodes = {2009, 2059, 2162, 2195};

    private int numRetries, sleepTime, expiry;
    private int displayLevel = 1;  // (<= 0 : silent), (> 0 : normal)

    private MQQueueManager qMgr;
    private MQQueue queue;

    private XMLProcessor xmlproc;
    
	static Logger logger = Logger.getLogger(PMOMQSend.class);
    
    private String Test_ProjectXML_filename;

	boolean usingForTest =  false;
	public PMOMQSend(){
		initialize();
	}
	/*Ported subu 6/4..deleted the comments */
	private void initialize(){
	
		this.qName        = ETSPMOGlobalInitialize.getQTo();
        this.qReplyTo     = ETSPMOGlobalInitialize.getQReplyTo();

        this.qManagerName = ETSPMOGlobalInitialize.getQManager();
        this.qManagerReplyTo = ETSPMOGlobalInitialize.getQManagerReplyTo();

  //      this.expiry       = Integer.parseInt(Glob.getq) * 60 * 10;  // tenths of a second
  //      this.sleepTime    = Integer.parseInt(getProperty("ed.MQSeries.sleepTime")) * 1000;

        this.numRetries   = Integer.parseInt(ETSPMOGlobalInitialize.getNumRetries());
//        this.displayLevel = Integer.parseInt(getProperty("ed.MQSeries.displayLevel"));
      
        setNonFatalErrorCodes(ETSPMOGlobalInitialize.getNonFatalErrorCodes());

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
		MQEnvironment.securityExit = new SecurityExit(); //PMOMQReceive();
		
        MQEnvironment.channel = ETSPMOGlobalInitialize.getQChannelName();
        MQEnvironment.hostname = ETSPMOGlobalInitialize.getQHostName();
        MQEnvironment.port = Integer.parseInt(ETSPMOGlobalInitialize.getQPort());
        
   	    this.Test_ProjectXML_filename = ETSPMOGlobalInitialize.getTest_ProjectXML_filename();
   	    
   	    
   	    this.ERR_LOG_FILE = ETSPMOGlobalInitialize.getMQSendErrorLog();
   	    /*if(this.err_log_file == null){
   	    	this.createIfFileDoesntExist(this.ERR_LOG_FILE);
   	    }*/
		
	}
	/*Ported subu 6/4 this method*/
	public synchronized boolean sendMQMessage(String ToQueue, String correlationId, String messageId, String message, boolean persistent, boolean COA, boolean COD) throws Throwable {
			if(ToQueue != null){
				this.qName        = ToQueue;
			}
			boolean b =  this.sendMQMessage(correlationId, messageId, message, persistent, COA, COD);
			if (logger.isDebugEnabled()) {
				logger
						.debug("sendMQMessage(String, String, String, String, boolean, boolean, boolean) - sending mq mes"
								+ b);
			}
			return b;
	}
    public synchronized boolean sendMQMessage(String correlationId, String messageId, String message, boolean persistent, boolean COA, boolean COD) throws Throwable {

		boolean b = true;
		String str = 	"CORRID: " + correlationId	+ "\n" +
						"MESSID: " + messageId		+ "\n" +
						"MESSAGE: " 	+ message	;
		
		/*Ported subu 6/4..next 2 lines */
		logger.debug("Accessing this local Queue  to send the message: " + this.qName);
		logger.debug("\n***********\nThe message that needs to be sent : \n" + str);

		
      /*  if(correlationId.equals("EDQ3_MODEL_TYPES") || correlationId.startsWith("EDQ4_AREA_MEM_") || correlationId.startsWith("EDQ4_REPORT_MEM_"))
            this.q = this.q2Name;
        else
            this.q = this.qName;
		*/
		//this.q = this.qName;

     /*   if( ! correlationId.equals("EDQ3_PING") && messageId != null && ! messageId.startsWith(OrderProcessor.ignoreUserid) )
            print("sending message with correlation id: " + correlationId + " messageId: " + messageId + " " + persistent + " " + COA + " " + COD
                  + "\nQueue: " + this.q + "   QueueManager: " + this.qManagerName, false);
     */   
        int numRetry = 0;
        
        try {
            while(true) {
                try {
                		
                    this.qMgr = new MQQueueManager(qManagerName);
                    
                  
                    int openOptions = MQC.MQOO_OUTPUT;
                    
                    // Open the queue.
                   // try{
                   logger.debug("Accessing this queue  to send message : " + this.qName);
                    this.queue = this.qMgr.accessQueue(
                                                       this.qName,
                                                       openOptions,
                                                       null,       // default queue manager
                                                       null,       // no dynamic queue name
                                                       null        // no alternate user id
                                                       );
                    
                  //  }
                 /*   catch(NullPointerException ne){
                    	log.print(this.getStackTrace(ne),log.DEBUG_LEVEL5);
                    	log.print("The Queue unavailable to retrieve message. Possiblity of MQ MANAGER DOWN", log.DEBUG_LEVEL1); 	
                    	b = false;
                    }*/
                    // defaults (same as MQPMO_DEFAULT)
                    MQPutMessageOptions pmo = new MQPutMessageOptions();

                    // Acquire a buffer to send the message.
                    MQMessage mqMessage = new MQMessage();
                    


                    // set the messageId
                    if(messageId != null)
                        mqMessage.messageId = messageId.getBytes();

				/*NOTE: SUBU : My messages are all persistent. So they dont expire...i dont use the else block here
				 * 
				 * 
				 */
                    if(persistent)
                        mqMessage.persistence = MQC.MQPER_PERSISTENT;
                    else {
                        mqMessage.persistence = MQC.MQPER_NOT_PERSISTENT;
                        mqMessage.expiry = this.expiry;
                    }


                    mqMessage.replyToQueueManagerName = this.qManagerReplyTo;
                    mqMessage.replyToQueueName = this.qReplyTo;


                    if(COA && COD)
                        mqMessage.report = MQC.MQRO_COA | MQC.MQRO_COD | MQC.MQRO_PASS_MSG_ID | MQC.MQRO_PASS_CORREL_ID;
                    else if(COA)
                        mqMessage.report = MQC.MQRO_COA | MQC.MQRO_PASS_MSG_ID | MQC.MQRO_PASS_CORREL_ID;
                    else if(COD)
                        mqMessage.report = MQC.MQRO_COD | MQC.MQRO_PASS_MSG_ID | MQC.MQRO_PASS_CORREL_ID;

					/*NOTE: Setting the format of the string...had to set	 this
					* when working with JMS on Sys side
					* 
					* 
					*/
					


                    // set the correlationId and messageContent
                    mqMessage.correlationId = correlationId.getBytes();
                    
                    /*
                     * writing as Bytes for Boris(SystemCorp). 
                     * But, writing as UTF if i am testing as
                     * my PMOMQReceive only reads UTFs
                     */
                    if(usingForTest == false){
						mqMessage.format = MQC.MQFMT_NONE;
						mqMessage.writeBytes(message);
                    }
                    else{
                    	logger.debug("Total Message Length before writing to MQ:  " + message.length());
                    	mqMessage.format = MQC.MQFMT_STRING;
                    	/* If message size is greater than max UTF size(65535)
                    	 * else it will skip this if loop and execute the mqMessage.writeUTF(message);
                    	 */
                    	if(message.length() > 65535){
		                    	int start = 0;
        		            	while(message.length()> 65535)
   							   		{
					    		    	String firstseq = message.substring(start, start + 65535);
					        			message = message.substring(start+65535,message.length());
							        	mqMessage.writeUTF(firstseq);
						     		}
                    	}
                    	mqMessage.writeUTF(message);
                    }
                    
                    logger.debug("Total MQ Message Length after writing to MQ:  " + mqMessage.getMessageLength() + " This will be messagelength  + 2 bytes for info abt length in the message");
                    

                   /* while(message.length()> 65535)
   					   {System.out.println("iterating");
					        String firstseq = message.substring(start, start + 65535);
					        message = message.substring(start+65535,message.length());
					        mqMessage.writeUTF(firstseq);
				      }*/
				      
				   /* String outstr =  new String(message);
      				ByteArrayOutputStream bytesout = new ByteArrayOutputStream(outstr.length());
      				DataOutputStream dbytesout = new DataOutputStream(bytesout);
      				int start = 0;
      				while(outstr.length()> 65535)
      				{
       				 	String firstseq = outstr.substring(start, start + 65535);
       					 outstr = outstr.substring(start+65535,outstr.length());
						
      				}
				      	mqMessage.writeBytes(message);
*/

                    // Put the message on the queue.
                   
                    this.queue.put(mqMessage, pmo);
						logger.debug("\n***********\nFinished Sending the message \n");
						
						
              /*      if( ! correlationId.equals("EDQ3_PING") && messageId != null && ! messageId.startsWith(OrderProcessor.ignoreUserid) )
                        print("sent message with correlation id: " + correlationId, false);
                        */

                    break;
                } 
                catch(MQException mqe) {
                	
                    if(numRetry <= this.numRetries) {
                        	numRetry++;
	                        if(isNonFatalMQException(mqe)) {
    		                        print("Encountered Non-Fatal MQException: " 
            		                      + mqe.toString()
                    		              + "\nFAILED ATTEMPTS: " + numRetry);

		                            cleanup();
        		                    try {
                			                Thread.sleep(this.sleepTime);
                            			}
		                            catch(InterruptedException ie) {
        			                        print(getStackTrace(ie));
                    		        }
                        		}
                        else {
                        	
                           			 throw mqe;
							}
                    }//if(numRetry <= this.numRetries)
                    else {
                    	b = false;
                    	
                        throw mqe;
		    		}
                }
            }
        }
        catch(Throwable t) {
            handleException(t, message);
            
        }
        finally {
            cleanup();
            
        }
        return b;
    }



    private boolean isNonFatalMQException(MQException e) {
        for(int i = 0; i < this.nonFatalErrorCodes.length; i++)
            if(e.reasonCode == this.nonFatalErrorCodes[i])
                return true;
        
        return false;
    }



    public void cleanup() {

        if(this.qMgr != null && this.qMgr.isConnected()) {

            if(this.queue != null) {
                try {
                    this.queue.close();
                }
                catch(MQException e) {
                    print(getStackTrace(e));
                }
                this.queue = null;
            }
            
            try {
                this.qMgr.disconnect();
            }
            catch(MQException e) {
                print(getStackTrace(e));
            }

            this.qMgr = null;

        }
        else {
            this.queue = null;
            this.qMgr = null;
        }

    }



    private void handleException(Throwable t, String message) throws Throwable {

        if(t instanceof MQException) {

            String mqPropsString
                = "Queue: "         + this.q + "\n"
                + "Queue Manager: " + this.qManagerName + "\n"
                + "Hostname: "      + MQEnvironment.hostname + "\n"
                + "Channel: "       + MQEnvironment.channel + "\n"
                + "Port: "          + MQEnvironment.port + "\n";

            print(
                  "An MQSeries error occurred while sending message:\n"
		  + message + "\n"
                  + "\nStackTrace:\n"
                  + getStackTrace(t)
                  + "\nThe properties used were:\n"
                  + mqPropsString
                  );

            throw (MQException) t;
            
        }
        else {

            print(
                  "A Non-MQSeries error occurred while sending message:\n"
		  + message + "\n"
                  + "\nStackTrace:\n"
                  + getStackTrace(t)
                  );

            throw t;

        }

    }



    private String getProperty(String key) {
        String value = this.props.getProperty(key);
        if(value != null)
            value = value.trim();
        if(this.defaultProps == null) {
            if(value == null || value.length() == 0) {
                print("ERROR! Key: " + key + " not found in MQ properties file");
                throw new RuntimeException("ERROR! Key: " + key + " not found in MQ properties file");
            }
            else
                return value;
        }
        
        String defaultValue = this.defaultProps.getProperty(key);
        if(value == null || value.length() == 0) {
            if(defaultValue == null || defaultValue.trim().length() == 0) {
                print("ERROR!: Key: " + key + " not defined in both MQ properties file as well as default MQ properties string(in code)");
                throw new RuntimeException("ERROR!: Key: " + key + " not defined in both MQ properties file as well as default MQ properties string(in code)");
            }
            else {
                print("WARNING!: Key: " + key + " not found in MQ properties file. Using default value: " + defaultValue);
                return defaultValue.trim();
            }
        }

        return value;
    }



    private void print(String str) {
        print(str, true);
    }



    private void print(String str, boolean error) {
        if(error || this.displayLevel > 0) {
            if(error)
                str 
                    = "********************* !!! MQ SEND ERROR !!! **********************\n"
                    + "Printed at: " + new Date() + "\n"
                    + str
                    + "\n\n\n\n";
            else
                str 
                    = "**************************** MQ Send *****************************\n"
                    + "Printed at: " + new Date() + "\n"
                    + str
                    + "\n\n\n\n";


            if(this.ERR_LOG_FILE == null){

            }
            else {
                try {
              
                
                    FileOutputStream out = new FileOutputStream(this.ERR_LOG_FILE, true);   // append str to log file
                    out.write(str.getBytes());
                    out.close();
                }
                catch(Throwable t) {
                    logger.error(
                                       "\n\n\n********************* !!! MQ SEND ERROR !!! *********************\n"
                                       + "WARNING!: Error writing to " + this.ERR_LOG_FILE + " at " 
                                       + new Date() + "\n"
                                       + "Stack Trace:\n" 
                                       + getStackTrace(t) + "\n"
                                       + "While writing:\n" 
                                       + str + "\n"
                                       + "*****************************************************************\n\n\n"
                                       );
                }
            }
        }
    }




    private void setNonFatalErrorCodes(String s) {
    
       
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
                this.nonFatalErrorCodes = this.nonFatalErrorCodes;
                print(getStackTrace(t));
            }
        }
        else
            this.nonFatalErrorCodes = this.nonFatalErrorCodes;
    }



    private String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        String stackTrace = sw.toString();
        pw.close();
        return stackTrace;
    }
    public  void sendProjectXMLForTesting(String s) throws Throwable{
    		
    			File file  = null;
    			if(s == null){
	    			file = new File(Test_ProjectXML_filename);
    			}
    			else file = new File(s);
				StringBuffer strbuf = new StringBuffer();    			
				this.qName="PMO.PMO_TO_EDGE";
				//this.qName="PMO.EDGE_TO_PMO";
				//this.qName="ICC.PMO.PMO_TO_EDGE";
				usingForTest = true;
			try{
				FileInputStream fin = new FileInputStream(file);
			//	fin.read(
				String ls_str ;

				BufferedReader ls_in
          					= new BufferedReader(new InputStreamReader(fin));

					    try {
								while ((ls_str = ls_in.readLine()) != null) {
									//log.print(ls_str,log.DEBUG_LEVEL5);
									strbuf.append(ls_str + "\n");
							    	
							}
	   				 	} catch (IOException e) {
						logger.error("IOException in reading Test_Project_XML_file from this path: ");
				    }
  			  } catch (IOException e1) {
					logger.error("sendProjectXMLForTesting(String)" + e1, e1);
				    System.exit(1);
			}
				
				//sendMQMessage(ETSPMOGlobalInitialize.getPROJ_CORR_ID(),"ID1dd",new String(strbuf), true, true, true);
				sendMQMessage(ETSPMOGlobalInitialize.getCR_CORR_ID(),"v2sathis-1094761108271",new String(strbuf), true, true, true);
				    
    }
    public static void main(String args[]){
    	if(args.length < 1){
				if (logger.isDebugEnabled()) {
					logger
							.debug("main(String) -  Usage : PMOMQSend <PropertyFileLocation> ");
				}
				System.exit(0);
			}
			
		ETSPMOGlobalInitialize newone = new ETSPMOGlobalInitialize();

    	PMOMQSend one = new PMOMQSend();
		
    	try{
    //	one.sendMQMessage("ETS_PMO","HELLO","YABBAmudipa..",true, true, true);
    	if(args.length  > 1){
    		one.Test_ProjectXML_filename = args[0];
    	}			
    	one.sendProjectXMLForTesting(null);	
    //	one.sendMQMessage("EDQ3_PIN1","HELLO2","123rendu moonu naala",true, true, true);
    	}
   
        catch (Throwable e) {
			logger.error("main(String)", e);

        }
    }
    
	/**
	 * Returns the test_ProjectXML_filename.
	 * @return String
	 */
	public String getTest_ProjectXML_filename() {
		return Test_ProjectXML_filename;
	}

	/**
	 * Sets the test_ProjectXML_filename.
	 * @param test_ProjectXML_filename The test_ProjectXML_filename to set
	 */
	public void setTest_ProjectXML_filename(String test_ProjectXML_filename) {
		this.Test_ProjectXML_filename = test_ProjectXML_filename;
	}


	/**
	 * @return
	 */
	public static String getCLASS_VERSION() {
		return CLASS_VERSION;
	}

}
