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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.ibm.mq.MQC;
import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;

/**
 * @author shingte
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MQSend extends MQHandler implements Runnable {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(MQSend.class);

	/**
	 * @param host
	 * @param channel
	 * @param qm
	 * @param queue
	 * @param port
	 * @param filename
	 */
	public MQSend(String host, String channel, String qm, String queue, String port) 
	 {
		super(host, channel, qm, queue, port);
		// TODO Auto-generated constructor stub
	}
	
	
	/**
     * 
     */
    public MQSend() {
        
        // TODO Auto-generated constructor stub
    }

   
	public void connect(String qName) throws MQException {

		try {
			initial();
			int openOptions =
				MQC.MQOO_OUTPUT
					| MQC.MQOO_FAIL_IF_QUIESCING;
			queue = qmgr.accessQueue(qName, openOptions);
			logger.debug("opened queue " + qName); 
			//this.msg = new MQMessage();
		} catch (MQException mqe) {
			/*
			logger.info(
				"error during the Connect operation: "
					+ "CC = "
					+ mqe.completionCode
					+ "; RC = "
					+ mqe.reasonCode);
					*/
			throw mqe;
		}
	}

    //
	//  initMessage()
	//
	private void initMessage() throws MQException
	{
		if (msg==null)
			msg = new MQMessage();
		//msg.setVersion(MQC.MQMD_VERSION_2);
		msg.persistence = MQC.MQPER_PERSISTENT;
		// set up msg response variables
		//msg.messageType = MQC.MQMT_REQUEST;
		msg.replyToQueueManagerName = "";
		msg.replyToQueueName = this.getResponseToQueueName();
		//use default msgId, the MQ will generate one for you
		if(getCorrelationId() == null)
	        msg.correlationId = MQC.MQCI_NONE;
		else
		    msg.correlationId = getCorrelationId().getBytes();

		if(getMessageId() == null)
	        msg.messageId = MQC.MQMI_NONE;
	    else
	        msg.messageId = getMessageId().getBytes();
		
		//msg.messageId = MQC.MQMI_NONE;
		//msg.correlationId = MQC.MQCI_NONE;
		
		// set msg priority
		if (this.priority > default_priority)
			msg.priority = this.priority;
		// for put response msg, set cid = main msg's msgid 
		if (getReferenceId() != null) {
			msg.correlationId = getReferenceId();
			msg.messageId = getReferenceId();
		}
	}
	
	// retry for each queue put
	private void retryQueuePut(MQMessage mqmsg, MQPutMessageOptions pmo) throws MQException
	{
		for (int i=0; i<=getRetries();i++)
		{
			try {
				if (i!=0)
				{	
				  logger.info("retryPutMessage #"+i+", sleep "+ getWaitTime()+ " millis...");
				  Thread.sleep(getWaitTime());
				  reConnect(getQueueName());
				}
				queue.put(mqmsg, pmo);
			      break;
				} catch (InterruptedException ignore) {
				} catch (MQException ignore) { 
				} 
		}
	}
	
	/*
	 * We set max msg length is 1MB, if the incoming msg size is bigger than this,
	 * then we will do segementation on this msg, and will becompose automatically
	 * when msg was retrievaled from the document queue.
	 */
	public String putMessage(java.io.InputStream in)
		throws java.io.IOException, MQException {
		
		int segmentLength = 1024*1024; //2097100;
		
		try {
			byte[] tmp_message = new byte[segmentLength];
			MQPutMessageOptions pmo = new MQPutMessageOptions();
			//GWA require
			pmo.options = MQC.MQPMO_LOGICAL_ORDER | MQC.MQPMO_FAIL_IF_QUIESCING;
			pmo.options = pmo.options | MQC.MQPMO_SYNCPOINT;
			int flag = 0;
			
			initMessage();
			do {
				flag = in.read(tmp_message);
				int flag2 = flag < segmentLength ? -1 : 0;

				switch (flag2) {
					case -1 :
						byte[] last_message = new byte[flag];
						for (int j = 0; j < flag; j++)
							last_message[j] = tmp_message[j];
						msg.messageFlags = MQC.MQMF_LAST_SEGMENT;
						msg.write(last_message);
						queue.put(msg, pmo);
						//retryQueuePut(msg,pmo);
						commit();
						logger.info("Put message MQMF_LAST_SEGMENT " + flag + "Bytes.");
						flag = -1;
						break;
					default :
						msg.originalLength = segmentLength;
						msg.messageFlags = MQC.MQMF_SEGMENT;
						msg.write(tmp_message);
						queue.put(msg, pmo);
						//retryQueuePut(msg,pmo);
						commit();
						logger.info("GID:"+getHexId(msg.groupId));
						logger.info("Put message MQMF_NOT_LAST_SEGMENT "
								+ segmentLength + "Bytes.");
				}
				
				msg.clearMessage();
			} while (flag != -1);

			// will return the correlationId as message Id for further reference
			msg.correlationId =
				getReferenceId() == null ? msg.messageId : getReferenceId();
			msg.messageId =
				getReferenceId() == null ? msg.messageId : getReferenceId();
			// for put a request msg, set crefId = msgid, and return this id for log 
			//@ b1 if(getReferenceId() != null) 	msg.correlationId = getReferenceId();		   	    

			return getHexId(msg.correlationId);
		} catch (java.io.IOException ioe) {
			logger.info("error: " + ioe.getMessage());
			throw ioe;
		} catch (MQException mqe) {
			backout();
			logger.info(
				"error during the Put Message operation: "
					+ "CC = "
					+ mqe.completionCode
					+ "; RC = "
					+ mqe.reasonCode);
			throw mqe;
		} finally {
			setReferenceId(null);
			this.priority = default_priority;
			// reset msg status
			msg.clearMessage();

		}
	}

	
	//----
	public String putMessage(String filename)
	throws java.io.IOException, MQException, Exception
	{
	     java.io.BufferedInputStream   bis   = null;
         java.io.FileInputStream       fis   = null;
         String corrId=null;
         try {
			fis   = new java.io.FileInputStream( filename );
			bis   = new java.io.BufferedInputStream( fis );
			corrId = putMessage(bis);
			logger.info("put CorrId="+corrId);
			bis.close();
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error("putMessage(String)", e);
			throw(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("putMessage(String)", e);
			throw(e);
		} catch (MQException e) {
			// TODO Auto-generated catch block
			logger.error("putMessage(String)", e);
			throw (e);
		}
		return corrId; 
		
         
	}
	
	/*
	public String putMessage(String msgText, int msg_priority)
	throws java.io.IOException, MQException {
	    this.priority = msg_priority;
	try {
		return putMessage(
			(InputStream) new ByteArrayInputStream(msgText.getBytes()));
	} catch (java.io.IOException ioe) {
		logger.info("error: " + ioe.getMessage());
		throw ioe;
	} catch (MQException mqe) {
		logger.info(
			"error during the Put Message operation: "
				+ "CC = "
				+ mqe.completionCode
				+ "; RC = "
				+ mqe.reasonCode);
		throw mqe;
	}
} 
	*/
	
	//----
	public String putMessage(java.io.InputStream in, int msg_priority)
		throws java.io.IOException, MQException {
		this.priority = msg_priority;
		return putMessage(in);
	}

//	 Put Object into the queue
	public String putObjectMessage(Object msgObj)
		throws java.io.IOException, MQException, Exception {
		// Here can implments with PipeInputStream and PipeOutStream with seperate thread
		InputStream docInputStream;
		ObjectOutputStream o = null;
		if (!(msgObj instanceof InputStream)) {
			ByteArrayOutputStream docOutputStream = new ByteArrayOutputStream();
			o = new ObjectOutputStream(docOutputStream);
			o.writeObject(msgObj);

			docInputStream =
				new ByteArrayInputStream(docOutputStream.toByteArray());
		} else {
			docInputStream = (InputStream) msgObj;
		}
		try {
			return putMessage(docInputStream);
		} catch (java.io.IOException ioe) {
			logger.info("error: " + ioe.getMessage());
			throw ioe;
		} catch (MQException mqe) {
			logger.info(
				"error during the Put Message operation: "
					+ "CC = "
					+ mqe.completionCode
					+ "; RC = "
					+ mqe.reasonCode);
			throw mqe;
		} catch (Exception e) {
			e.toString();
			throw e;
		} finally {
			if (docInputStream != null)
				docInputStream.close();
			if (o != null)
				o.close();
		}
	} 

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			// TODO Auto-generated method stub
			connect(getQueueName());
			putMessage(getFileName());
			disConnect();
		} catch (MQException e) {
			// TODO Auto-generated catch block
			logger.error("run()", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("run()", e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("run()", e);
		}
		logger.info("After put - free->"+Runtime.getRuntime().freeMemory()+", total->"+Runtime.getRuntime().totalMemory());	

	}

	public static void main(String[] args) {
	    
	    PropertyConfigurator.configure(args[0]);
	    
		logger.info("Start - free->"+Runtime.getRuntime().freeMemory()+", total->"+Runtime.getRuntime().totalMemory());
		
	     String hostname="pokxseilpar06.pok.ibm.com"; //"pm20aix.sby.ibm.com"; 
	     String channel="ICC.PMO.CLIENT";
	     String qManager="MDFIX"; //"PM21AIX"; 
	     String qName="ICC.PMO.PMO_TO_EDGE";
	     String port="1414"; //"1420"; 
	     /*
	     String hostname="pm20aix.sby.ibm.com"; 
	     String channel="ICC.PMO.CLIENT";
	     String qManager="PM21AIX"; 
	     String qName="ICC.PMO.PMO_TO_EDGE";
	     String port="1420"; 
	     */
	     /*
	     String hostname="edesign100.rchland.ibm.com"; 
	     String channel="PMO.ICC.SVRCONN";
	     String qManager="D03XMQ102"; 
	     String qName="PMO.PMO_TO_EDGE";
	     String port="1414"; 
         */
	     
	     
	     
	     MQSend m1 = new MQSend(hostname, channel, qManager, qName, port);
	     MQSend m2 = new MQSend(hostname, channel, qManager, qName, port);
	     m1.setFileName(args[1]);
	     //m2.setFileName(args[2]);
	     m1.setCorrelationId("PMO_PROJECT");
	     Thread t1 = new Thread(m1);
	     //Thread t2 = new Thread(m2);
		 t1.start();
		 //t2.start();

	     
	     /*
	     MQSend mqh=null;
	     int retry_cnt=10;
	     long sleep_millis=10000;
         
	     try {
			mqh = new MQSend(hostname, channel, qManager, qName, port, args[0]);
			mqh.connect(qName);
		    String corrId = mqh.putMessage(args[0]);    
			mqh.disConnect();
			 
	     } catch (MQException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			for (int i = 0; i <= retry_cnt; i++) {
				try {
					logger.info("retryPutMessage #"+i+", sleep "+ sleep_millis+ " millis...");
					Thread.sleep(sleep_millis);
					mqh = new MQSend(hostname, channel, qManager, qName,  port, args[0]);
					mqh.connect(qName);
				    //String corrId = mqh.putMessage(args[0]);   
					} catch (InterruptedException ignore) {
					} catch (MQException ignore) {
					} catch (Exception ex) {
						ex.printStackTrace();
					}
			}
		 }  catch (Exception e3) {
		 	e3.printStackTrace();
		 }
		 */
	   	 //
	     logger.info("After put - free->"+Runtime.getRuntime().freeMemory()+", total->"+Runtime.getRuntime().totalMemory());
	     System.out.println("File "+args[1]+" is sent to the queue "+qName+" in qmgr "+qManager+" of host "+hostname);
	     
	}
}
