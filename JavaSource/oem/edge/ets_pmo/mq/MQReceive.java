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

import oem.edge.ets_pmo.common.ETSPMOGlobalInitialize;
import oem.edge.ets_pmo.datastore.util.MQXMLMessage;

import org.apache.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ResourceBundle;

import com.ibm.mq.MQC;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;

/**
 * @author shingte
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MQReceive extends MQHandler implements Runnable {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(MQReceive.class);
	private static int _wait_mill = 15000;
	private int _buf_limit = 512*1024; // 512KB ; 

	/**
	 * @param host
	 * @param channel
	 * @param qm
	 * @param queue
	 * @param port
	 * @param filename
	 */
	public MQReceive(String host, String channel, String qm, String queue,
			String port) {
		super(host, channel, qm, queue, port);
		// TODO Auto-generated constructor stub
	}

	/**
     * 
     */
    public MQReceive() {
        
        // TODO Auto-generated constructor stub
    }

  
   
	
	public void connect(String qName) throws MQException {

		try {
			initial();
			int openOptions =
				MQC.MQOO_INPUT_SHARED
					| MQC.MQOO_FAIL_IF_QUIESCING;
			openOptions |= MQC.MQOO_INQUIRE;
			
			queue = qmgr.accessQueue(qName, openOptions);
			logger.debug("opened queue " + qName); //n3111
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

    //===========
	// getMessage
	//===========
	public void getMessage(XMLProcessor xmlproc) //,boolean sendMailSwitch) //OutputStream out)
	throws java.io.IOException, MQException {
	BufferedOutputStream bos = null;
	FileOutputStream fos = null;
	String filename=null;
	String[] message = new String[3];
	long fsize=0;
	
	try {
		MQGetMessageOptions gmo = new MQGetMessageOptions();
		// GWA Requirement
		gmo.options =
			MQC.MQGMO_WAIT
		//		| MQC.MQGMO_ALL_SEGMENTS_AVAILABLE
				| MQC.MQGMO_FAIL_IF_QUIESCING
				| MQC.MQGMO_SYNCPOINT;
		if (isQmgrSegmentation())
			gmo.options |= MQC.MQGMO_COMPLETE_MSG; // qmgr assemble seg
		else
			gmo.options |= MQC.MQGMO_LOGICAL_ORDER; // app assemble seg
	/*
		String wait = ETSPMOGlobalInitialize.getWaitInterval();
		if (wait!=null)
			gmo.waitInterval = Integer.parseInt(wait)*1000;
		else
	*/
			gmo.waitInterval = _wait_mill; // set wait to wail_mill msec
		// reset the msg, this should prevent 2033 Errort
		
	if (msg==null)
		msg = new MQMessage();
	
	if(getCorrelationId() == null)
        msg.correlationId = MQC.MQCI_NONE;
	else
	    msg.correlationId = getCorrelationId().getBytes();

	if(getMessageId() == null)
        msg.messageId = MQC.MQMI_NONE;
    else
        msg.messageId = getMessageId().getBytes();

	int depth = getCurrentDepth();
	if (depth>0)
		logger.info("Current MQ Queue Depth="+getCurrentDepth());

	boolean isFirst=true;
	do {	

		queue.get(msg, gmo);
		/*
		if (msg.backoutCount > this.BOTHRESH) {
			// commit will remove the poison message
			if (logger.isInfoEnabled()) {
				logger.info("getMessage() - backoutCount="+msg.backoutCount+", threshold="+this.BOTHRESH);
			}
			this.commit();
			// get another msg
			msg.messageId = MQC.MQMI_NONE;
			if (getCurrentDepth() > 0)
				queue.get(msg, gmo);
		}
		*/
		
		if (isFirst && msg.getDataLength() <= 0)
		    return;
		
		if (filename==null)
		{
		    String prefix = ""+ETSPMOGlobalInitialize.getProjectCreateUpdateXMLDir();
		    filename = prefix+"mq_" +  System.currentTimeMillis()+".xml";
			fos   = new FileOutputStream( filename );
			bos   = new BufferedOutputStream(fos );
			isFirst=false;
		}
		
		
		int buf_limit=_buf_limit = 512*1024; // 512KB ;
		int bytes_left;
		byte[] bytes = new byte[buf_limit];
		while((bytes_left=msg.getDataLength()) > 0){
			if (bytes_left<buf_limit)
				bytes = new byte[bytes_left];

			if (logger.isInfoEnabled()) {
				logger.info("GID:"+getHexId(msg.groupId));
				logger.info(" getMessage()->bytes_left="+bytes_left+",offset="+msg.getDataOffset());
			}
			msg.readFully(bytes,0,bytes.length);
			bos.write(bytes);
			fsize+=bytes.length;
		}
	} while (gmo.segmentStatus == MQC.MQSS_SEGMENT);
		bos.close();
		fos.close();
		logger.info("Save MQ message to file "+filename+", size = "+fsize+" bytes");

		
		//after the MQ receive
		MQXMLMessage mes	=	 new MQXMLMessage();
        
        message[0] = new String(msg.correlationId).trim();
        mes.setCorrID(message[0]);
        message[1] = new String(msg.messageId).trim();
        mes.setMessageID(message[1]);
        mes.setFileName(filename);

        
        // now check if we should populate
    	
    	boolean shouldIpopulate=false;
    	
    	String cr_ack_corr_id = ETSPMOGlobalInitialize.getCR_ACK_CORR_ID();
		String cr_nack_corr_id = ETSPMOGlobalInitialize.getCR_NACK_CORR_ID();
		String proj_corr_id	=	ETSPMOGlobalInitialize.getPROJ_CORR_ID();
		
        if((message[0].trim()).equalsIgnoreCase(proj_corr_id)   ||
           (message[0].trim()).equalsIgnoreCase(cr_ack_corr_id) ||
           (message[0].trim()).equalsIgnoreCase(cr_nack_corr_id))
        {
            shouldIpopulate = true;
        }
        
        if(shouldIpopulate == true){
            xmlproc.setMessageInMQ(true);
            xmlproc.populatevXMLMessages(mes);
        }
        else{
        	logger.info("Received an inappropriate message with corr id: " + message[0] + 
        				" and the messageid: " + message[1]);
        	deleteFile(filename);
        }
        XMLProcessor.printMem("after populateMessage");
		//return true;
		
	} catch (java.io.IOException ioe) {
		//logger.info("error: " + ioe.getMessage());
		//return false;
		throw ioe;
	} catch (MQException mqe) {
		if (mqe.reasonCode == MQException.MQRC_NO_MSG_AVAILABLE)
		{
		    xmlproc.setMessageInMQ(false);
			//return true;
		}
		else if (mqe.reasonCode == MQException.MQRC_INCOMPLETE_MSG) 
		{
		    xmlproc.setMessageInMQ(false);
		    deleteFile(filename);
		    logger.error("file "+filename+" is deleted, due to incomplete message");
			//return true;
		}
        /*
		logger.info(
			"error during the Get Message operation: "
				+ "CC = "
				+ mqe.completionCode
				+ "; RC = "
				+ mqe.reasonCode);
		return false;
		*/
		else 
		throw mqe;
	} finally {
		// reset msg status
		msg.clearMessage();
	}
	
}
	


	
	private void deleteFile(String filename) {
     	  if (filename!=null)
          {
            try {
              File file = new File(filename);
              if (file!=null)
                  file.delete();
            } catch (Exception e)
            {
                logger.info("Error in handling file "+filename,e);
            }
            
          }
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
	    /*
	    String filename;
		try {
			// TODO Auto-generated method stub
			connect(getQueueName());
			filename = getMessage();
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
		*/
		logger.debug("After get - free->"+Runtime.getRuntime().freeMemory()+", total->"+Runtime.getRuntime().totalMemory());
	}

	public static String getParam(ResourceBundle prop, String param)
	{
		String tmp = prop.getString(param);
		if (tmp!=null)
			tmp=tmp.trim();
		return tmp;
	}
	
	public static void main(String[] args) {
		logger.info("Start - free->"+Runtime.getRuntime().freeMemory()+", total->"+Runtime.getRuntime().totalMemory());
		/*
	     String hostname="pm20aix.sby.ibm.com"; 
	     String channel="ICC.PMO.CLIENT";
	     String qManager="PM21AIX"; 
	     String qName="ICC.PMO.EDGE_TO_PMO";
	     String port="1420"; 
	     
	     String hostname="edesign100.rchland.ibm.com"; 
	     String channel="PMO.ICC.SVRCONN";
	     String qManager="D03XMQ102"; 
	     String qName="PMO.PMO_TO_EDGE";
	     String port="1414"; 
         */
		ResourceBundle prop = ResourceBundle.getBundle("oem.edge.ets_pmo.ets_pmo");
		String tmpDir = getParam(prop,"ets_pmo.ProjectTmpDir");
		String xmlDir = getParam(prop, "ets_pmo.ProjectCreateUpdateXMLDir");
		ResourceBundle mqprop = ResourceBundle.getBundle("oem.edge.ets_pmo.ets_pmo_mq");

		String qFrom = getParam(mqprop, "ets_pmo.MQSeries.Queue.From.Name");
		String qReplyTo = getParam(mqprop, "ets_pmo.MQSeries.Queue.ReplyTo.Name");
		String qTo = getParam(mqprop, "ets_pmo.MQSeries.Queue.To.Name");
		String qManager = getParam(mqprop, "ets_pmo.MQSeries.Queue.Manager.Name");
		String qHostName = getParam(mqprop, "ets_pmo.MQSeries.Host.Name");
		String qChannelName = getParam(mqprop, "ets_pmo.MQSeries.Channel.Name");
		String qPort = getParam(mqprop, "ets_pmo.MQSeries.Port");
		String waitInterval = getParam(mqprop, "ets_pmo.MQSeries.waitInterval");
	     
	     MQReceive m1 = new MQReceive(qHostName, qChannelName, qManager, qFrom, qPort);
	     //MQReceive m2 = new MQReceive(hostname, channel, qManager, qName, port, args[1]);
	     Thread t1 = new Thread(m1);
	     //Thread t2 = new Thread(m2);
		 t1.start();
		 //t2.start();
		 

			
		 logger.info("After get - free->"+Runtime.getRuntime().freeMemory()+", total->"+Runtime.getRuntime().totalMemory());
	   
		 
	}
}
