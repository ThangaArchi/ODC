package oem.edge.ed.sd.mq;

/************************** BOF : HEADER *************************************/
/* IBM Confidential                                                          */
/* OCO Source Materials                                                      */
/* Identifier: ICC/PROFIT                                                    */
/* (C) Copyright IBM Corp. 2002, 2003                                        */
/* The source code for this program is not published or otherwise divested   */
/* of its trade secrets, irrespective of what has been deposited with the    */
/* U.S. Copyright Office                                                     */
/************************** RCS & COPYRT *************************************/
/************************** EOF : HEADER *************************************/
/////////////////////////////////////////////////////////////////////////////
//
//                            Edge 2.10
//                      Dev: Navneet Gupta
//
//////////////////////////////////////////////////////////////////////////////

import java.io.*;
import java.util.Properties;
import java.util.Date;
import java.util.StringTokenizer;

import oem.edge.ed.sd.ordproc.OrderProcessor;

import com.ibm.mq.*;    // MQSeries classes for Java
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2002-2004                                     */
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


public class MQSend {

    public final static String Copyright = "(C) Copyright IBM Corp. 2002, 2003";
    private Properties props, defaultProps;
    private String ERR_LOG_FILE;

    private String qManagerName, qManagerReplyTo;
    private String qName, q2Name, q, qReplyTo;

    private int[] nonFatalErrorCodes;
    private final int[] defaultNonFatalErrorCodes = {2009, 2059, 2162, 2195};

    private int numRetries, sleepTime, expiry;
    private int displayLevel = 1;  // (<= 0 : silent), (> 0 : normal)

    private MQQueueManager qMgr;
    private MQQueue queue;



    public MQSend(Properties props, Properties defaultProps, String ERR_LOG_FILE) {
        this.props = props;
        this.defaultProps = defaultProps;
        this.ERR_LOG_FILE = ERR_LOG_FILE;
        initialize();
    }



    public MQSend(String propsFile) {
        this(propsFile, null);
    }



    public MQSend(String propsFile, String ERR_LOG_FILE) {
        this.props = new Properties();
        this.ERR_LOG_FILE = ERR_LOG_FILE;
        try {
            FileInputStream in = new FileInputStream(propsFile);
            this.props.load(in);
            in.close();
        }
        catch(IOException e) {
            print("ERROR reading from: " +  propsFile + " in MQSend constructor");
            throw new RuntimeException(e.toString());
        }
        initialize();
    }



    private void initialize() {
        print("initializing MQSend...", false);

        this.qName        = getProperty("ed.MQSeries.Queue.To.Name");
        this.q2Name       = getProperty("ed.MQSeries.Queue2.To.Name");

        this.qReplyTo     = getProperty("ed.MQSeries.Queue.ReplyTo.Name");

        this.qManagerName = getProperty("ed.MQSeries.Queue.Manager.Name");
        this.qManagerReplyTo = getProperty("ed.MQSeries.Queue.Manager.ReplyTo.Name");

        this.expiry       = Integer.parseInt(getProperty("ed.MQSeries.expiry")) * 60 * 10;  // tenths of a second
        this.sleepTime    = Integer.parseInt(getProperty("ed.MQSeries.sleepTime")) * 1000;

        this.numRetries   = Integer.parseInt(getProperty("ed.MQSeries.numRetries"));
        this.displayLevel = Integer.parseInt(getProperty("ed.MQSeries.displayLevel"));
        
        setNonFatalErrorCodes("ed.MQSeries.nonFatalErrorCodes");

        MQEnvironment.channel = getProperty("ed.MQSeries.Channel.Name");
        MQEnvironment.hostname = getProperty("ed.MQSeries.Host.Name");
        MQEnvironment.port = Integer.parseInt(getProperty("ed.MQSeries.Port"));
        MQEnvironment.securityExit = new SecurityExit();
        print("initialized MQSend on Queues: " + this.qName + " and " + this.q2Name + "   QueueManager: " + this.qManagerName, false);
    }




    public synchronized void sendMQMessage(String correlationId, String messageId, String message, boolean persistent) throws Throwable {

        if(correlationId.equals("EDQ3_MODEL_TYPES") || correlationId.startsWith("EDQ4_AREA_MEM_") || correlationId.startsWith("EDQ4_REPORT_MEM_"))
            this.q = this.q2Name;
        else
            this.q = this.qName;


        if( ! correlationId.equals("EDQ3_PING") && messageId != null && ! messageId.startsWith(OrderProcessor.ignoreUserid) )
            print("sending message with correlation id: " + correlationId + " messageId: " + messageId + " " + persistent + " "       + "\nQueue: " + this.q + "   QueueManager: " + this.qManagerName, false);
        
        int numRetry = 0;
        
        try {
            while(true) {
                try {
                    this.qMgr = new MQQueueManager(qManagerName);
                    
                    int openOptions = MQC.MQOO_OUTPUT;
                    
                    // Open the queue.
                    this.queue = this.qMgr.accessQueue(
                                                       this.q,
                                                       openOptions,
                                                       null,       // default queue manager
                                                       null,       // no dynamic queue name
                                                       null        // no alternate user id
                                                       );

                    // defaults (same as MQPMO_DEFAULT)
                    MQPutMessageOptions pmo = new MQPutMessageOptions();

                    // Acquire a buffer to send the message.
                    MQMessage mqMessage = new MQMessage();


                    // set the messageId
                    if(messageId != null)
                        mqMessage.messageId = messageId.getBytes();


                    if(persistent)
                        mqMessage.persistence = MQC.MQPER_PERSISTENT;
                    else {
                        mqMessage.persistence = MQC.MQPER_NOT_PERSISTENT;
                        mqMessage.expiry = this.expiry;
                    }


                    mqMessage.replyToQueueManagerName = this.qManagerReplyTo;
                    mqMessage.replyToQueueName = this.qReplyTo;

/*Changes for 6.1
                    if(COA && COD)
                        mqMessage.report = MQC.MQRO_COA | MQC.MQRO_COD | MQC.MQRO_PASS_MSG_ID | MQC.MQRO_PASS_CORREL_ID;
                    else if(COA)
                        mqMessage.report = MQC.MQRO_COA | MQC.MQRO_PASS_MSG_ID | MQC.MQRO_PASS_CORREL_ID;
                    else if(COD)
                        mqMessage.report = MQC.MQRO_COD | MQC.MQRO_PASS_MSG_ID | MQC.MQRO_PASS_CORREL_ID;
*/

                    // set the correlationId and messageContent
                    mqMessage.correlationId = correlationId.getBytes();
                    mqMessage.writeUTF(message);

                    // Put the message on the queue.
                    this.queue.put(mqMessage, pmo);

                    if( ! correlationId.equals("EDQ3_PING") && messageId != null && ! messageId.startsWith(OrderProcessor.ignoreUserid) )
                        print("sent message with correlation id: " + correlationId, false);

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
                    }
                    else {
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


            if(this.ERR_LOG_FILE == null)
                System.out.print(str);
            else {
                try {
                    FileOutputStream out = new FileOutputStream(this.ERR_LOG_FILE, true);   // append str to log file
                    out.write(str.getBytes());
                    out.close();
                }
                catch(Throwable t) {
                    System.out.println(
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




    private void setNonFatalErrorCodes(String key) {
        String s = this.props.getProperty(key);
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
                print(getStackTrace(t));
            }
        }
        else
            this.nonFatalErrorCodes = this.defaultNonFatalErrorCodes;
    }



    private String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        String stackTrace = sw.toString();
        pw.close();
        return stackTrace;
    }
}
