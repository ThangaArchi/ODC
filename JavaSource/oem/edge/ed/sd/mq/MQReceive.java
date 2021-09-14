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
/*     (C)Copyright IBM Corp. 2004                                          */
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


public class MQReceive {

    public final static String Copyright = "(C) Copyright IBM Corp. 2002, 2003";
    private Properties props, defaultProps;
    private String ERR_LOG_FILE;

    private String qManagerName;
    private String qName;

    private int[] nonFatalErrorCodes;
    private final int[] defaultNonFatalErrorCodes = {2009, 2059, 2162, 2195};

    private int numRetries;
    private int sleepTime;
    private int displayLevel = 1;  // (<= 0 : silent), (> 0 : normal)
    
    private MQQueueManager qMgr;
    private MQQueue queue;
    private MQGetMessageOptions gmo;

    private static final int NO_MSG_AVAILABLE = 2033;



    public MQReceive(Properties props, Properties defaultProps, String ERR_LOG_FILE) throws Throwable {
        this.props = props;
        this.defaultProps = defaultProps;
        this.ERR_LOG_FILE = ERR_LOG_FILE;
        initialize();
    }



    public MQReceive(String propsFile) throws Throwable {
        this(propsFile, null);
    }



    public MQReceive(String propsFile, String ERR_LOG_FILE) throws Throwable {
        this.props = new Properties();
        this.ERR_LOG_FILE = ERR_LOG_FILE;
        try {
            FileInputStream in = new FileInputStream(propsFile);
            this.props.load(in);
            in.close();
        }
        catch(IOException e) {
            print("ERROR reading from: " +  propsFile + " in MQReceive constructor");
            throw new RuntimeException(e.toString());
        }
        initialize();
    }



    private void initialize() throws Throwable {

        print("initializing MQReceive...", false);

        // close any previous connections and queues
        cleanup();

        this.qName        = getProperty("ed.MQSeries.Queue.From.Name");
        this.qManagerName = getProperty("ed.MQSeries.Queue.Manager.Name");

        int waitInterval  = Integer.parseInt(getProperty("ed.MQSeries.waitInterval")) * 1000;
        this.sleepTime    = Integer.parseInt(getProperty("ed.MQSeries.sleepTime")) * 1000;
        this.numRetries   = Integer.parseInt(getProperty("ed.MQSeries.numRetries"));
        this.displayLevel = Integer.parseInt(getProperty("ed.MQSeries.displayLevel"));

        setNonFatalErrorCodes("ed.MQSeries.nonFatalErrorCodes");
        print(qName+" "+qManagerName+" ", true);
        print("before MQE "+getProperty("ed.MQSeries.Channel.Name"), true);
        MQEnvironment.channel  = getProperty("ed.MQSeries.Channel.Name");
        print("After MQE "+getProperty("ed.MQSeries.Channel.Name"), true);
        
        MQEnvironment.hostname = getProperty("ed.MQSeries.Host.Name");
        MQEnvironment.port     = Integer.parseInt(getProperty("ed.MQSeries.Port"));
        MQEnvironment.securityExit = new SecurityExit();

        int numRetry = 0;

        try {
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

                    print("initialized MQReceive  on Queue: " + this.qName + "   QueueManager: " + this.qManagerName, false);

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
                        else
                            throw mqe;
                    }
                    else
                        throw mqe;
                }
            }
        }
        catch(Throwable t) {
            cleanup();
            handleException(t);
        }
    }



    public void recieveMQMessage() throws Throwable {

        recieveMQMessage(null, null);

    }



    public void recieveMQMessage(String correlationId, String messageId) throws Throwable {

        String[] message = new String[3];
        int numRetry = 0;

        try {
            while(true) {

                if(this.queue == null)
                    initialize();

                try {
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


                    
                    // Get the message from the queue.
                    this.queue.get(mqMessage, this.gmo);
            
                    message[0] = new String(mqMessage.correlationId).trim();
                    message[1] = new String(mqMessage.messageId).trim();


                  /*  if(mqMessage.messageType == MQC.MQMT_REPORT) {

                        if(mqMessage.feedback == MQC.MQFB_COA)
                            OrderProcessor.logReport(message[0], message[1], "COA");
                        else if(mqMessage.feedback == MQC.MQFB_COD)
                            OrderProcessor.logReport(message[0], message[1], "COD");
                        else
                            print("Received Report with invalid feedback: " + mqMessage.feedback + "\n"
                                  + "correlationId: " + message[0] + "   messageId: " + message[1], true);

                        break;

                    }
*/

                    try {
                    
                      //new for 4.5.1
                       StringBuffer buff = new StringBuffer("");
                       print("length "+mqMessage.getTotalMessageLength());
                     
                       while (mqMessage.getDataLength() > 0){
                         
                          String str = mqMessage.readUTF();
                         
                          if (str != null){
                             buff.append(str);
                          }
                         
                         
                          mqMessage.setDataOffset(mqMessage.getTotalMessageLength()-mqMessage.getDataLength());
                          print("size left "+mqMessage.getDataLength());
                          
                          
                       }
                      
                       message[2] = buff.toString();
                      //end of new for 4.5.1
                       mqMessage = null; //new for 4.5.1 fixpack
                      
                    }
                    catch(IOException e) {

                        try {
                            byte[] arr = new byte[mqMessage.getMessageLength()];
                            mqMessage.readFully(arr);
                            print("The following message with correlationId: " + message[0] + " was NOT in UTF format:\n"
                                  + new String(arr), true);
                        }
                        catch(Throwable t1) { }

                        message[2] = "";

                    }


// if( ! message[0].equals("EDQ1_PING") )
//   print("Got message with correlation id: " + message[0]
//       + "\nQueue: " + this.qName + "   QueueManager: " + this.qManagerName, false);

                    OrderProcessor.saveOrder(message);

                    break;

                } 
                catch(MQException mqe) {

                    if (mqe.reasonCode == NO_MSG_AVAILABLE) {

                        try {
                            this.qMgr.backout();
                        }
                        catch(MQException mqe1) {
                            print(getStackTrace(mqe1));
                        }

                        return;

                    }


                    if(numRetry <= this.numRetries) {

                        numRetry++;

                        if(isNonFatalMQException(mqe)) {

                            try {
                                this.qMgr.backout();
                            }
                            catch(MQException mqe1) {
                                print(getStackTrace(mqe1));
                            }

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
                        else
                            throw mqe;
                    }
                    else
                        throw mqe;
                }
            }
        }
        catch(Throwable t) {

            try {
                this.qMgr.backout();
            }
            catch(Throwable t1) {
                print(getStackTrace(t1));
            }

            cleanup();

            handleException(t);

        }

        this.qMgr.commit();

    }



    private boolean isNonFatalMQException(MQException e) {
        for(int i = 0; i < this.nonFatalErrorCodes.length; i++)
            if(e.reasonCode == this.nonFatalErrorCodes[i])
                return true;
        
        return false;
    }
    


    public void cleanup() {

        if(this.qMgr != null && this.qMgr.isConnected()) {

            print("cleaning up MQReceive...", false);

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


    
    
    private void handleException(Throwable t) throws Throwable {

        if(t instanceof MQException) {

            String mqPropsString
                = "Queue: "         + this.qName + "\n"
                + "Queue Manager: " + this.qManagerName + "\n"
                + "Hostname: "      + MQEnvironment.hostname + "\n"
                + "Channel: "       + MQEnvironment.channel + "\n"
                + "Port: "          + MQEnvironment.port + "\n";

            print(
                  "An MQSeries error occurred:"
                  + "\nStackTrace:\n"
                  + getStackTrace(t)
                  + "\nThe properties used were:\n"
                  + mqPropsString
                  );

            throw (MQException) t;
            
        }
        else {
            print(
                  "A NON-MQSeries error occurred:"
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
        
        String defaultValue = this.defaultProps.getProperty(key).trim();
        if(value == null || value.length() == 0) {
            if(defaultValue == null || defaultValue.length() == 0) {
                print("ERROR!: Key: " + key + " not defined in both MQ properties file as well as default MQ properties string(in code)");
                throw new RuntimeException("ERROR!: Key: " + key + " not defined in both MQ properties file as well as default MQ properties string(in code)");
            }
            else {
                print("WARNING!: Key: " + key + " not found in MQ properties file. Using default value: " + defaultValue);
                return defaultValue;
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
                    = "******************** !!! MQ RECEIVE ERROR !!! ********************\n"
                    + "Printed at: " + new Date() + "\n"
                    + str
                    + "\n\n\n\n";
            
            else
                str 
                    = "*************************** MQ Receive ***************************\n"
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
                                       "\n\n\n******************** !!! MQ RECEIVE ERROR !!! ********************\n"
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
