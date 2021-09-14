package oem.edge.ets.fe.ismgt.model;

/**
 * A placeholder for all the ETS attachments.
 * Creation date: (5/27/2002 8:51:27 PM)
 * @author: JV Rao [jeetrao@us.ibm.com]
 */
import java.io.Serializable;
import java.sql.Timestamp;
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

public class ETSIssueAttach implements java.io.Serializable {

	private static final String CQ_CLASS_VERSION = "3.12.0";

	private String applicationId;
	private String edgeProblemId;
	private String cqTrackId;
	//NEW - 3.2.3
	private int seqNo;
	private int fileNo;
	private String fileName;
	private String fileDesc;
	private String fileMime;
	private byte[] fileData;
	private long fileSize;
	private String fileNewFlag;

	private String user;
	private Timestamp timeStamp;
	private String timeStampString;

	private int startData;
	private int maxSize;
	private boolean missing;

	/**
	 * EdCQAttach constructor comment.
	 */
	public ETSIssueAttach() {
		super();

		applicationId = "";
		edgeProblemId = "";
		cqTrackId = "";

		fileNo = 0;
		fileName = "";
		fileMime = "";
		fileData = null;
		fileSize = 0L;
		fileNewFlag = "";
		timeStampString = "";

		startData = 0;
		maxSize = 0;
		missing = false;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/27/2002 9:08:48 PM)
	 * @return java.lang.String
	 */
	public String getApplicationId() {
		return applicationId;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/2/2002 3:56:33 PM)
	 */
	public String getAttachDate() {
		return "";
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/23/2002 7:59:46 PM)
	 * @return java.lang.String
	 */
	public static String getClassVersion() {
		return CQ_CLASS_VERSION;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/27/2002 9:10:55 PM)
	 * @return java.lang.String
	 */
	public String getCqTrackId() {
		return cqTrackId;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/27/2002 9:09:54 PM)
	 * @return java.lang.String
	 */
	public String getEdgeProblemId() {
		return edgeProblemId;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/27/2002 9:20:02 PM)
	 * @return java.sql.Blob
	 */
	public byte[] getFileData() {
		return fileData;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/30/2002 11:58:20 AM)
	 * @return java.lang.String
	 */
	public String getFileDesc() {
		return fileDesc;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/27/2002 9:15:37 PM)
	 * @return java.lang.String
	 */
	public String getFileMime() {
		return fileMime;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/27/2002 9:13:55 PM)
	 * @return java.lang.String
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/27/2002 9:19:00 PM)
	 * @return java.lang.String
	 */
	public String getFileNewFlag() {
		return fileNewFlag;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/27/2002 9:12:19 PM)
	 * @return java.lang.String
	 */
	public int getFileNo() {
		return fileNo;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/27/2002 9:17:40 PM)
	 * @return long
	 */
	public long getFileSize() {
		return fileSize;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/28/2002 12:01:14 AM)
	 * @return int
	 */
	public int getMaxSize() {
		return maxSize;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/27/2002 11:59:13 PM)
	 * @return int
	 */
	public int getStartData() {
		return startData;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/28/2002 12:53:14 AM)
	 * @return java.sql.Timestamp
	 */
	public Timestamp getTimeStamp() {
		return timeStamp;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/28/2002 1:05:07 AM)
	 * @return java.lang.String
	 */
	public String getTimeStampString() {
		return timeStampString;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/28/2002 12:51:24 AM)
	 * @return java.lang.String
	 */
	public String getUser() {
		return user;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/28/2002 12:07:21 AM)
	 * @return boolean
	 */
	public boolean isMissing() {
		return missing;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/27/2002 9:09:16 PM)
	 * @param applicationId java.lang.String
	 */
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/27/2002 9:11:37 PM)
	 * @param cqTrackId java.lang.String
	 */
	public void setCqTrackId(String cqTrackId) {
		this.cqTrackId = cqTrackId;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/27/2002 9:10:17 PM)
	 * @param edgeProblemId java.lang.String
	 */
	public void setEdgeProblemId(String edgeProblemId) {
		this.edgeProblemId = edgeProblemId;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/27/2002 9:20:26 PM)
	 * @param fileData java.sql.Blob
	 */
	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/30/2002 11:58:49 AM)
	 * @param fileDesc java.lang.String
	 */
	public void setFileDesc(String fileDesc) {
		this.fileDesc = fileDesc;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/27/2002 9:15:58 PM)
	 * @param fileMime java.lang.String
	 */
	public void setFileMime(String fileMime) {
		this.fileMime = fileMime;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/27/2002 9:14:20 PM)
	 * @param fileName java.lang.String
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/27/2002 9:19:26 PM)
	 * @param fileNewFlag java.lang.String
	 */
	public void setFileNewFlag(String fileNewFlag) {
		this.fileNewFlag = fileNewFlag;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/27/2002 9:12:39 PM)
	 * @param fileNo java.lang.String
	 */
	public void setFileNo(int fileNo) {
		this.fileNo = fileNo;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/27/2002 9:18:08 PM)
	 * @param fileSize long
	 */
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/28/2002 12:01:37 AM)
	 * @param maxSize int
	 */
	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/28/2002 12:07:43 AM)
	 * @param missing boolean
	 */
	public void setMissing(boolean missing) {
		this.missing = missing;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/27/2002 11:59:42 PM)
	 * @param startData int
	 */
	public void setStartData(int startData) {
		this.startData = startData;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/28/2002 12:53:43 AM)
	 * @param timeStamp java.sql.Timestamp
	 */
	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/28/2002 1:05:28 AM)
	 * @param timeStampString java.lang.String
	 */
	public void setTimeStampString(String timeStampString) {
		this.timeStampString = timeStampString;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/28/2002 12:51:43 AM)
	 * @param user java.lang.String
	 */
	public void setUser(String user) {
		this.user = user;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/12/2002 11:11:40 PM)
	 * @return java.lang.String
	 */
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("[ applicationId = " + getApplicationId() + " ] \n");
		sb.append("[ edgeProblemId = " + getEdgeProblemId() + " ] \n");
		sb.append("[ cqTrackId = " + getCqTrackId() + " ] \n");
		sb.append("[ fileNo = " + getFileNo() + " ] \n");
		sb.append("[ fileName = " + getFileName() + " ] \n");
		sb.append("[ fileDesc = " + getFileDesc() + " ] \n");
		sb.append("[ fileMime = " + getFileMime() + " ] \n");
		sb.append("[ fileSize = " + getFileSize() + " ] \n");
		sb.append("[ fileNewFlag = " + getFileNewFlag() + " ] \n");

		return sb.toString();
	}

	/**
	 * Gets the seqNo
	 * @return Returns a int
	 */
	public int getSeqNo() {
		return seqNo;
	}
	/**
	 * Sets the seqNo
	 * @param seqNo The seqNo to set
	 */
	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

}
