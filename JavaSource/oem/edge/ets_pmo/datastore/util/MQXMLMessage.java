package oem.edge.ets_pmo.datastore.util;

import java.io.File;

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
public class MQXMLMessage {
	private static String CLASS_VERSION = "4.5.1";
	private String corrID;
	private String messageID;
	private String xmlData;
	private boolean isUTF	= true;
	private String fileName;
	/**
	 * Returns the corrID.
	 * @return int
	 */
	public String getCorrID() {
		return corrID;
	}

	/**
	 * Returns the messageID.
	 * @return int
	 */
	public String getMessageID() {
		return messageID;
	}

	/**
	 * Returns the xmlData.
	 * @return String
	 */
	public String getXmlData() {
		return xmlData;
	}

	/**
	 * Sets the corrID.
	 * @param corrID The corrID to set
	 */
	public void setCorrID(String corrID) {
		this.corrID = corrID;
	}

	/**
	 * Sets the messageID.
	 * @param messageID The messageID to set
	 */
	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}

	/**
	 * Sets the xmlData.
	 * @param xmlData The xmlData to set
	 */
	public void setXmlData(String xmlData) {
		this.xmlData = xmlData;
	}

	/**
	 * Returns the isUTF.
	 * @return boolean
	 */
	public boolean isUTF() {
		return isUTF;
	}

	/**
	 * Sets the isUTF.
	 * @param isUTF The isUTF to set
	 */
	public void setIsUTF(boolean isUTF) {
		this.isUTF = isUTF;
	}
	
	public String toString(){
		String str = 	"CORRID : " + corrID + " \n" +
						"messageID : " + messageID + "\n" +
						"xmlData : " + xmlData ;
		return str;
	}

	/**
	 * @return
	 */
	public static String getCLASS_VERSION() {
		return CLASS_VERSION;
	}

	/**
	 * @return Returns the file.
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param file The file to set.
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
