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
package oem.edge.ets_pmo.mq;

import org.apache.log4j.Logger;

/*
 * Licensed Materials - Property of IBM
 *
 * (c) Copyright IBM Corp. 2002, 2003 All Rights Reserved
 *
 * US Goverment Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
*/

import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;

public abstract class MQHandler {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(MQHandler.class);

	private String hostName;
	private String channelName;
	private String qmgrName;
	private int port;
	private String queueName;
	private String fileName=null;
	
	private int retries=10;
	private long waitTime=20000;
	private String trace;
	private int traceLevel;
	static private String _default_trace_eable = "No";
	static private int _default_trace_level = 2;
	
	protected MQQueueManager qmgr;
	protected MQQueue queue;
	protected MQMessage msg=null;
	protected String referenceId=null;
	protected String messageId=null;
	protected String correlationId=null;
	protected String responseToQueueName;
	protected boolean qmgrSegmentation=false; // qmgr segmentation need more memory


	// log the trace if been enabled or not
	protected static boolean enableTrace;
	// the max backout threshold times for each msg retry
	final protected int BOTHRESH = 3;

	final protected int default_priority = 0;
	protected int priority;


	/**
	 * Constructor for DocumentQueueConnector
	 */
	public MQHandler() {}
	
	public MQHandler(
		String host,
		String channel,
		String qm,
		String queue,
		String port) {
		setHostName(host);
		setChannelName(channel);
		setQmgrName(qm);
		setQueueName(queue);
		setPort(port);
		
		setTrace(_default_trace_eable,_default_trace_level);
		//initial();
	}

	private void setQmgrSegmentation(boolean value)
	{
		qmgrSegmentation=value;
	}
	protected boolean isQmgrSegmentation()
	{
		return qmgrSegmentation;
	}
	/**
	 * Initial MQEnvironment Object & Connect to Qmgr
	 */
	protected void initial() throws MQException {
		MQException.log = null;
		MQEnvironment.securityExit = new SecurityExit();
		MQEnvironment.channel = getChannelName();
		MQEnvironment.hostname = getHostName();
		MQEnvironment.port = getPort();
		// use Pooled Connection, require MQ Java API5.2.0 and JDK1.3
		//MQPoolToken token=MQEnvironment.addConnectionPoolToken();
		//MQEnvironment.removeConnectionPoolToken(token);


		if (getIfEnableTrace() != null && "Yes".equals(getIfEnableTrace())) {
			MQEnvironment.enableTracing(getTraceLevel());
			synchronized (this) {
				if (!enableTrace)
					enableTrace = true;
			}
		}

		try {
			qmgr = new MQQueueManager(getQmgrName());
		} catch (MQException mqe) {
			/*
			logger.info(
				"error during the connect Qmanager operation: "
					+ "CC = "
					+ mqe.completionCode
					+ "RC = "
					+ mqe.reasonCode);
			if (MQException.MQRC_CONNECTION_BROKEN == mqe.reasonCode)
				logger.info(
					"Connection to Queue Manager broken, retry the operation later!");
					*/
			throw mqe;
		}
	}

	/**
	 * Connect to the Document Queue
	 */
	
	public void connect() throws MQException {
	    connect(getQueueName());
	}
	
	abstract void connect(String qName) throws MQException ;
	
	
	/*
	{

		try {
			initial();
			int openOptions =
				MQC.MQOO_INPUT_SHARED
					| MQC.MQOO_OUTPUT
		//			| MQC.MQOO_INQUIRE
					| MQC.MQOO_FAIL_IF_QUIESCING;
			queue = qmgr.accessQueue(qName, openOptions);
			logger.debug("opened queue " + qName); //n3111
			if (logger.isDebugEnabled()) {
				logger.debug("connect(String)" + queue.getMaximumMessageLength()
						+ " max message len queue");
			}
			//this.msg = new MQMessage();
		} catch (MQException mqe) {
			
			throw mqe;
		}
	}
*/

	/**
	 * disConnect from the Queue Manager
	 */
	public void disConnect() throws MQException {
		try {
			if (this.queue!=null)
			 this.queue.close();
			logger.debug("close the queue !"); //n3111
			// disable the trace function if it enable before.
			if (enableTrace == true)
				MQEnvironment.disableTracing();
			if (this.qmgr!=null && qmgr.isConnected())
			{
			 this.qmgr.disconnect();
			 this.qmgr = null;
			 logger.debug("disconnected from qmgr");
			}
		} catch (MQException mqe) {
			/*
			logger.info(
				"error during the DisConnect operation: "
					+ "CC = "
					+ mqe.completionCode
					+ "; RC = "
					+ mqe.reasonCode);
			*/
			throw mqe;
		}
	}
	
	/**
	 * reConnect to the Queue Manager
	 */
	public void reConnect() throws MQException {
	    reConnect(getQueueName());
	}
	public void reConnect(String qName) throws MQException {
	  for (int i=1;i<=retries;i++)
	  {
	    try {
            Thread.sleep(waitTime);
        } catch (InterruptedException e) { }
	    logger.debug("retryPutMessage #"+i+", sleep "+ waitTime+ " millis...");
			
		try {
		if (isConnected())
		 disConnect();
		} catch (MQException ex) {}
		connect(qName);
		if (isConnected())
		  break;
	  }
	}

	/**
	 * For Syncpoint control
	 */
	public void commit() throws com.ibm.mq.MQException {
		try {
			this.qmgr.commit();
			logger.debug("Operations are commited !"); //n3111
		} catch (MQException mqe) {
			logger.info(
				"error during the commite operation: "
					+ "CC = "
					+ mqe.completionCode
					+ "; RC = "
					+ mqe.reasonCode);
			throw mqe;
		}
	}

	/**
	 * For Syncpoint control
	 */
	public void backout() throws MQException {
		try {
			this.qmgr.backout();
			logger.info("Msgs are reinstated on the queue !");
		} catch (MQException mqe) {
			logger.info(
				"error during the BackOut operation: "
					+ "CC = "
					+ mqe.completionCode
					+ "; RC = "
					+ mqe.reasonCode);
			throw mqe;
		}
	}

	/*
	 * you can check how many docs left in the queue by using this msg.
	 */
	public int getCurrentDepth() throws MQException {
		try {
			return queue.getCurrentDepth();
		} catch (MQException mqe) {
			logger.info(
				"error during the Get Message operation: "
					+ "CC = "
					+ mqe.completionCode
					+ "; RC = "
					+ mqe.reasonCode);
			throw mqe;
		}
	}
    //----------
	//
	public boolean isConnected() {
		if (qmgr==null)
			return false;
		return qmgr.isConnected();
	}

	

    /**
     * @return Returns the correlationId.
     */
    public String getCorrelationId() {
        return correlationId;
    }
    /**
     * @param correlationId The correlationId to set.
     */
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
    /**
     * @return Returns the messageId.
     */
    public String getMessageId() {
        return messageId;
    }
    /**
     * @param messageId The messageId to set.
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
	
	//----------
	protected void setReferenceId(String referenceId) {
		if (referenceId != null)
			this.referenceId = referenceId.trim();
	}
	//----------
	protected byte[] getReferenceId() {
		return referenceId == null ? null : referenceId.getBytes();
	}
	//----------
	public String getMsgId() {
		return getHexId(msg.messageId);
	}
	//----------
	public void setResponseToQueueName(String response) {
		this.responseToQueueName = response;
	}
	//----------
	public String getResponseToQueueName() {
		return this.responseToQueueName;
	}

	/**
	 * @return Returns the fileName.
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName The fileName to set.
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * @return Returns the qmgrName.
	 */
	public String getQmgrName() {
		return qmgrName;
	}
	/**
	 * @param qmgrName The qmgrName to set.
	 */
	public void setQmgrName(String qmgrName) {
		this.qmgrName = qmgrName;
	}
	//------------
	public String getChannelName() {
		return channelName;
	}

	public String getHostName() {
		return hostName;
	}

	public int getPort() {
		return port;
	}

	public String getQueueName() {
		return this.queueName;
	}

	public int getTraceLevel() {
		return this.traceLevel;
	}

	public String getIfEnableTrace() {
		return this.trace;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setTrace(String trace, int traceLevel) {
		this.trace = trace;
		this.traceLevel = traceLevel;
	}

	public void setPort(String port) {
		this.port = (new Integer(port)).intValue();
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
	
	/**
	 * @return Returns the retries.
	 */
	public int getRetries() {
		return retries;
	}
	/**
	 * @param retries The retries to set.
	 */
	public void setRetries(int retries) {
		this.retries = retries;
	}
	/**
	 * @return Returns the waitTime.
	 */
	public long getWaitTime() {
		return waitTime;
	}
	/**
	 * @param waitTime The waitTime to set.
	 */
	public void setWaitTime(long waitTime) {
		this.waitTime = waitTime;
	}
	
	//-----------
	//	 getHexId
	public String getHexId(byte[] myId) {
		    StringBuffer buf=new StringBuffer();
		    buf.append("X");
			for (int i = 0; i < myId.length; i++) {
				char b = (char) (myId[i] & 0xFF);
				if (b < 0x10) {
					buf.append("0");
				}
				buf.append((String) (Integer.toHexString(b)).toUpperCase());
			}
			return buf.toString();
		}
	
	//--------------------
	// main
	//--------------------
	public static void main(String[] args) {
		logger.info("Start - free->"+Runtime.getRuntime().freeMemory()+", total->"+Runtime.getRuntime().totalMemory());
		
	     String hostname="pm20aix.sby.ibm.com"; 
	     String channel="ICC.PMO.CLIENT";
	     String qManager="PM21AIX"; 
	     String qName="ICC.PMO.EDGE_TO_PMO";
	     String port="1420"; 
	     /*
	     String hostname="edesign100.rchland.ibm.com"; 
	     String channel="PMO.ICC.SVRCONN";
	     String qManager="D03XMQ102"; 
	     String qName="PMO.PMO_TO_EDGE";
	     String port="1414"; 
         */
	     MQHandler mqh=null;
	     int retry_cnt=10;
	     long sleep_millis=10000;
         /*
	     try {
			mqh = new MQHandler(hostname, channel, qManager, qName, port);
		//	mqh.connect(qName);
		//    String corrId = mqh.putMessage(args[0]);    
			mqh.disConnect();
			 
	     } catch (MQException e) {
			// TODO Auto-generated catch block
			logger.error("main(String[])", e);
		 }  catch (Exception e3) {
			logger.error("main(String[])", e3);
		 }
		 */
		 logger.info("After - free->"+Runtime.getRuntime().freeMemory()+", total->"+Runtime.getRuntime().totalMemory());
	}
	


} 