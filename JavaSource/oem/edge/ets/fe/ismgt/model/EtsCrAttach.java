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

package oem.edge.ets.fe.ismgt.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsCrAttach {

	public static final String VERSION = "1.30";

	private String etsId;
	private String pmoId;
	private String pmoProjectId;
	private String parentPmoId;
	private String docName;
	private int docType;
	private int docNo;
	private String docDesc;
	private String infoSrcFlag;
	private String lastUserId;
	private String lastUserName;
	private String lastUserIrId; //IBM ID
	private Timestamp timeStamp;
	private String timeStampString;

	//misc info
	private byte[] fileData;
	private long fileSize;
	private int startData;
	private int maxSize;
	private boolean missing;
	private String fileMime;
	
	//view url
	private String viewCrUrl;
	private String docSizeInKb;
	

	/**
	 * 
	 */
	public EtsCrAttach() {
		super();

	}

	/**
	 * @return
	 */
	public String getDocDesc() {
		return docDesc;
	}

	/**
	 * @return
	 */
	public String getDocName() {
		return docName;
	}

	/**
	 * @return
	 */
	public int getDocType() {
		return docType;
	}

	/**
	 * @return
	 */
	public String getEtsId() {
		return etsId;
	}

	/**
	 * @return
	 */
	public byte[] getFileData() {
		return fileData;
	}

	/**
	 * @return
	 */
	public long getFileSize() {
		return fileSize;
	}

	/**
	 * @return
	 */
	public String getInfoSrcFlag() {
		return infoSrcFlag;
	}

	/**
	 * @return
	 */
	public String getLastUserId() {
		return lastUserId;
	}

	/**
	 * @return
	 */
	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * @return
	 */
	public boolean isMissing() {
		return missing;
	}

	/**
	 * @return
	 */
	public String getParentPmoId() {
		return parentPmoId;
	}

	/**
	 * @return
	 */
	public String getPmoId() {
		return pmoId;
	}

	/**
	 * @return
	 */
	public String getPmoProjectId() {
		return pmoProjectId;
	}

	/**
	 * @return
	 */
	public int getStartData() {
		return startData;
	}

	/**
	 * @return
	 */
	public Timestamp getTimeStamp() {
		return timeStamp;
	}

	/**
	 * @return
	 */
	public String getTimeStampString() {
		return timeStampString;
	}

	/**
	 * @param string
	 */
	public void setDocDesc(String string) {
		docDesc = string;
	}

	/**
	 * @param string
	 */
	public void setDocName(String string) {
		docName = string;
	}

	/**
	 * @param i
	 */
	public void setDocType(int i) {
		docType = i;
	}

	/**
	 * @param string
	 */
	public void setEtsId(String string) {
		etsId = string;
	}

	/**
	 * @param bs
	 */
	public void setFileData(byte[] bs) {
		fileData = bs;
	}

	/**
	 * @param l
	 */
	public void setFileSize(long l) {
		fileSize = l;
	}

	/**
	 * @param string
	 */
	public void setInfoSrcFlag(String string) {
		infoSrcFlag = string;
	}

	/**
	 * @param string
	 */
	public void setLastUserId(String string) {
		lastUserId = string;
	}

	/**
	 * @param i
	 */
	public void setMaxSize(int i) {
		maxSize = i;
	}

	/**
	 * @param b
	 */
	public void setMissing(boolean b) {
		missing = b;
	}

	/**
	 * @param string
	 */
	public void setParentPmoId(String string) {
		parentPmoId = string;
	}

	/**
	 * @param string
	 */
	public void setPmoId(String string) {
		pmoId = string;
	}

	/**
	 * @param string
	 */
	public void setPmoProjectId(String string) {
		pmoProjectId = string;
	}

	/**
	 * @param i
	 */
	public void setStartData(int i) {
		startData = i;
	}

	/**
	 * @param timestamp
	 */
	public void setTimeStamp(Timestamp timestamp) {
		timeStamp = timestamp;
	}

	/**
	 * @param string
	 */
	public void setTimeStampString(String string) {
		timeStampString = string;
	}

	/**
	 * @return
	 */
	public String getFileMime() {
		return fileMime;
	}

	/**
	 * @param string
	 */
	public void setFileMime(String string) {
		fileMime = string;
	}

	/**
	 * @return
	 */
	public int getDocNo() {
		return docNo;
	}

	/**
	 * @param i
	 */
	public void setDocNo(int i) {
		docNo = i;
	}

	/**
	 * @return
	 */
	public String getLastUserIrId() {
		return lastUserIrId;
	}

	/**
	 * @param string
	 */
	public void setLastUserIrId(String string) {
		lastUserIrId = string;
	}

	/**
	 * @return
	 */
	public String getLastUserName() {
		return lastUserName;
	}

	/**
	 * @param string
	 */
	public void setLastUserName(String string) {
		lastUserName = string;
	}

	/**
	 * @return
	 */
	public String getViewCrUrl() {
		return viewCrUrl;
	}

	/**
	 * @param string
	 */
	public void setViewCrUrl(String string) {
		viewCrUrl = string;
	}

	/**
	 * @return
	 */
	public String getDocSizeInKb() {
		return docSizeInKb;
	}

	/**
	 * @param string
	 */
	public void setDocSizeInKb(String string) {
		docSizeInKb = string;
	}

}
