/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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

package oem.edge.ets.fe.documents;

import oem.edge.ets.fe.documents.common.DocMQMessage;
import oem.edge.util.jms.JmsManagerUtils;

import org.apache.log4j.Logger;

/**
 * @author v2srikau
 */
public class MQHelper {

	public final static String Copyright = "(C) Copyright IBM Corp. 2002, 2003";

	/** Stores the Logging object */
	private static final Logger m_pdLog = Logger.getLogger(MQHelper.class);

	public MQHelper() {
		initialize();
	}

	/**
	 * 
	 */
	private void initialize() {
	}

	/**
	 * @param messageId
	 * @param strDocId
	 * @param strProjectId
	 * @param strDocFiles
	 * @param strFileSizes
	 * @param strFileDescription
	 * @param strFileStatus
	 * @throws Exception
	 */
	public synchronized void sendMQMessage(
		String strMessageId,
		String strDocId,
		String strProjectId,
		String strDocFiles,
		String strFileSizes,
		String strFileDescription,
		String strFileStatus,
		String strUserId,
		String strAttachmentNotifyFlag)
		throws Exception {

		int iNumRetry = 0;

		DocMQMessage udMessage = new DocMQMessage();
		udMessage.setDocId(strDocId);
		udMessage.setProjId(strProjectId);
		udMessage.setFileNames(strDocFiles);
		udMessage.setFileSizes(strFileSizes);
		udMessage.setFileDescription(strFileDescription);
		udMessage.setFileStatus(strFileStatus);
		udMessage.setUserId(strUserId);
		udMessage.setNotifyOption(strAttachmentNotifyFlag);

		JmsManagerUtils.sendObject("ets", udMessage);
	}

	/**
	 * @param strDocId
	 * @param strProjectId
	 * @param strDocFiles
	 * @param strFileSizes
	 * @param strUserId
	 * @param strDocName
	 * @param strDocKeywords
	 * @param strDocDescription
	 * @throws Exception
	 */
	public synchronized void sendMQMessage(
			String strDocId,
			String strProjectId,
			String strDocFiles,
			String strFileSizes,
			String strUserId,
			String strDocName,
			String strDocKeywords,
			String strDocDescription)
			throws Exception {

			int iNumRetry = 0;

			DocMQMessage udMessage = new DocMQMessage();
			udMessage.setDocId(strDocId);
			udMessage.setProjId(strProjectId);
			udMessage.setFileNames(strDocFiles);
			udMessage.setFileSizes(strFileSizes);
			udMessage.setUserId(strUserId);
			udMessage.setDocDescription(strDocDescription);
			udMessage.setDocName(strDocName);
			udMessage.setKeywords(strDocKeywords);
			udMessage.setFileDescription("MEETINGS");

			JmsManagerUtils.sendObject("ets", udMessage);
	}

	/**
	 * @param messageId
	 * @param strDocId
	 * @param strProjectId
	 * @param strDocFiles
	 * @param strFileSizes
	 * @param strFileDescription
	 * @param strFileStatus
	 * @throws Exception
	 */
	public synchronized void sendMQMessage(
		String strMessageId,
		String strDocId,
		String strProjectId,
		String strDocFiles,
		String strFileSizes,
		String strUserId,
		String strAttachmentNotifyFlag)
		throws Exception {

		int iNumRetry = 0;

					DocMQMessage udMessage = new DocMQMessage();
					udMessage.setDocId(strDocId);
					udMessage.setProjId(strProjectId);
					udMessage.setFileNames(strDocFiles);
					udMessage.setFileSizes(strFileSizes);
		udMessage.setUserId(strUserId);
		udMessage.setNotifyOption(strAttachmentNotifyFlag);
					
		JmsManagerUtils.sendObject("ets", udMessage);
	}

}
