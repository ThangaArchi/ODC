/*   ------------------------------------------------------------------          */
/*   IBM                                                                                     */
/*                                                                                               */
/*   OCO Source Materials                                                          */
/*                                                                                               */
/*   Product(s): ICC/PROFIT                                                       */
/*                                                                                               */
/*   (C)Copyright IBM Corp. 2002,2003 		              */
/*                                                                                               */
/*   The source code for this program is not published or otherwise */
/*   divested of its trade secrets, irrespective of what has been        */
/*   deposited with the US Copyright Office.                                  */
/*   ------------------------------------------------------------------           */

package oem.edge.ets.fe;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

public class ETSDoc extends ETSDetailedObj implements ETSObj {
	public final static String Copyright =
		"(C) Copyright IBM Corp.  2002, 2003";

	protected int id;
	protected int catid;
	//spn 0312 projid
	protected String projectid;
	protected String userid;
	protected String username;
	protected String name;
	protected String description;
	protected String keywords;
	protected int size;
	protected long uploadDate;
	protected long publishdate;
	protected long updatedate;
	//protected long  meetingdate; 
	protected String filename;
	protected String filetype;
	protected long fileupdatedate;

	protected char isLatestVersion;
	protected char hasPreviousVersion;
	protected int doctype;
	protected String updatedBy;
	protected char lockFinalFlag;
	protected String lockedBy;
	protected char deleteFlag;
	protected String deletedBy;
	protected long deleteDate;
	protected String meetingId;
	protected char ibmOnly;
	//4.5.1
	protected char doc_status;
	protected String approval_comments;
	protected String approver_id;
	protected long approve_date;
	protected long expiry_date;
	protected int hits;

	//5.1.1
	protected String selfId;

	//5.2.1
	protected String dprivate;

	//5.4.1
	protected List m_lstDocFiles;
	protected String m_strIBMConfidential;
	protected String m_strItarStatus;
	protected String m_strProblemId;
	
	//6.3.1 mini release
	protected String dprivateEdit;
	protected String docReadEditRestricted; 

	// protected Vector privs;  maybe we need this????

	public ETSDoc() {
		id = 0;
		//projectid = 0;
		projectid = "";
		userid = "";
		name = "";
		catid = -1;
		description = "";
		keywords = "";
		size = 0;
		uploadDate = 0;
		publishdate = 0;
		updatedate = 0;
		filename = "";
		filetype = "";
		fileupdatedate = 0;

		isLatestVersion = Defines.NOT_SET_FLAG;
		hasPreviousVersion = Defines.NOT_SET_FLAG;
		doctype = 0;
		updatedBy = "";
		lockFinalFlag = Defines.NOT_SET_FLAG;
		lockedBy = "";
		deleteFlag = Defines.NOT_SET_FLAG;
		deletedBy = "";
		deleteDate = 0;
		meetingId = "";
		ibmOnly = Defines.ETS_PUBLIC;

		doc_status = Defines.DOC_PUBLISH;
		approver_id = "";
		approval_comments = "";
		approve_date = 0;
		expiry_date = 0;
		hits = 0;
		//privs = new Vector();
		selfId = "";
		dprivate = "0";
		dprivateEdit = "0";
		
		docReadEditRestricted = "0";
	}

	public void setIsLatestVersion(char c) {
		isLatestVersion = c;
	}
	public void setIsLatestVersion(String s) {
		if (s != null && !s.equals("")) {
			char c = s.charAt(0);
			isLatestVersion = c;
		} else {
			isLatestVersion = Defines.NOT_SET_FLAG;
		}
	}
	public char getIsLatestVersion() {
		return isLatestVersion;
	}
	public boolean isLatestVersion() {
		if (isLatestVersion == Defines.TRUE_FLAG)
			return true;
		else if (isLatestVersion == Defines.FALSE_FLAG)
			return false;
		else
			throw new RuntimeException("flag: isLatestVersion: not set");
	}

	public void setHasPreviousVersion(char c) {
		hasPreviousVersion = c;
	}
	public void setHasPreviousVersion(String s) {
		if (s != null && !s.equals("")) {
			char c = s.charAt(0);
			hasPreviousVersion = c;
		} else {
			hasPreviousVersion = Defines.NOT_SET_FLAG;
		}
	}
	public char getHasPreviousVersion() {
		return hasPreviousVersion;
	}
	public boolean hasPreviousVersion() {
		if (hasPreviousVersion == Defines.TRUE_FLAG)
			return true;
		else if (hasPreviousVersion == Defines.FALSE_FLAG)
			return false;
		else
			throw new RuntimeException("flag: hasPreviousVersion: not set");
	}

	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}

	//spn 0312 projid
	public void setProjectId(String projectid) {
		this.projectid = projectid;
	}
	public String getProjectId() {
		return projectid;
	}

	public void setCatId(int parentid) {
		this.catid = parentid;
	}
	public int getCatId() {
		return catid;
	}

	public void setUserId(String userid) {
		this.userid = userid;
	}
	public String getUserId() {
		return userid;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}

	public void setDescription(String desc) {
		this.description = desc;
	}
	public String getDescription() {
		return description;
	}

	public void setKeywords(String key) {
		this.keywords = key;
	}
	public String getKeywords() {
		return keywords;
	}

	public void setSize(int size) {
		this.size = size;
	}
	public int getSize() {
		return size;
	}

	public String getSizeStr() {
		return Integer.toString(size);
	}

	public String getFormattedUpdateDate() {
		Date dtUpdateDate = new Date(updatedate);
		SimpleDateFormat pdDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		return pdDateFormat.format(dtUpdateDate);
	}

	public String getFormattedUploadDate() {
		Date dtUploadDate = new Date(uploadDate);
		SimpleDateFormat pdDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		return pdDateFormat.format(dtUploadDate);
	}
	public long getUploadDate() {
		return uploadDate;
	}
	public void setUploadDate() {
		this.uploadDate = new Date().getTime();
	}
	public void setUploadDate(long uploaddate) {
		this.uploadDate = uploaddate;
	}
	public void setUploadDate(java.sql.Timestamp d) {
		this.uploadDate = d.getTime();
	}

	public long getPublishDate() {
		return publishdate;
	}
	public void setPublishDate() {
		this.publishdate = new Date().getTime();
	}
	public void setPublishDate(long publishdate) {
		this.publishdate = publishdate;
	}
	public void setPublishDate(java.sql.Timestamp d) {
		this.publishdate = d.getTime();
	}

	public long getUpdateDate() {
		return updatedate;
	}
	public void setUpdateDate() {
		this.updatedate = new Date().getTime();
	}
	public void setUpdateDate(long date) {
		this.updatedate = date;
	}
	public void setUpdateDate(java.sql.Timestamp d) {
		this.updatedate = d.getTime();
	}

	/*
	long getMeetingDate(){
	return meetingdate;
	}
	void setMeetingDate(){
	this.meetingdate = new Date().getTime();
	}
	void setMeetingDate(long date){
	this.meetingdate = date;
	}
	void setMeetingDate(java.sql.Timestamp d){
	this.meetingdate = d.getTime();
	}
	*/

	public void setFileName(String filename) {
		this.filename = filename;
		setFileType(filename);
	}
	public String getFileName() {
		return filename;
	}
	private void setFileType(String filename) {
		int index = filename.lastIndexOf(".");
		filetype =
			(filename.substring(index + 1, filename.length())).toLowerCase();
	}
	public String getFileType() {
		return filetype;
	}

	public long getFileUpdateDate() {
		return fileupdatedate;
	}
	public void setFileUpdateDate() {
		this.fileupdatedate = new Date().getTime();
	}
	public void setFileUpdateDate(long date) {
		this.fileupdatedate = date;
	}
	public void setFileUpdateDate(java.sql.Timestamp d) {
		this.fileupdatedate = d.getTime();
	}

	public void setDocType(int type) {
		this.doctype = type;
	}
	public int getDocType() {
		return doctype;
	}

	public void setUpdatedBy(String name) {
		this.updatedBy = name;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setLockFinalFlag(char c) {
		lockFinalFlag = c;
	}
	public void setLockFinalFlag(String s) {
		if (s != null && !s.equals("")) {
			char c = s.charAt(0);
			lockFinalFlag = c;
		} else {
			lockFinalFlag = Defines.NOT_SET_FLAG;
		}
	}
	public char getLockFinalFlag() {
		return lockFinalFlag;
	}

	public void setLockedBy(String name) {
		this.lockedBy = name;
	}
	public String getLockedBy() {
		return lockedBy;
	}

	public void setDeleteFlag(char c) {
		deleteFlag = c;
	}
	public void setDeleteFlag(String s) {
		if (s != null && !s.equals("")) {
			char c = s.charAt(0);
			deleteFlag = c;
		} else {
			deleteFlag = Defines.NOT_SET_FLAG;
		}
	}
	public char getDeleteFlag() {
		return deleteFlag;
	}
	public boolean isDeleted() {
		if (deleteFlag == Defines.TRUE_FLAG)
			return true;
		else if (
			deleteFlag == Defines.FALSE_FLAG
				|| deleteFlag == Defines.NOT_SET_FLAG)
			return false;
		else
			throw new RuntimeException("flag: isDeleted: not true, not false, and not set");
	}

	public void setDeletedBy(String name) {
		this.deletedBy = name;
	}
	public String getDeletedBy() {
		return deletedBy;
	}

	public long getDeleteDate() {
		return deleteDate;
	}
	public void setDeleteDate() {
		this.deleteDate = new Date().getTime();
	}
	public void setDeleteDate(long deletedate) {
		this.deleteDate = deletedate;
	}
	public void setDeleteDate(java.sql.Timestamp d) {
		this.deleteDate = d.getTime();
	}

	public void setMeetingId(String id) {
		this.meetingId = id;
	}
	public String getMeetingId() {
		return meetingId;
	}

	public void setIbmOnly(char c) {
		ibmOnly = c;
	}
	public void setIbmOnly(String s) {
		if (s != null && !s.equals("")) {
			char c = s.charAt(0);
			if (c == Defines.NOT_SET_FLAG) {
				c = Defines.ETS_PUBLIC;
			}
			ibmOnly = c;
		} else {
			ibmOnly = Defines.ETS_PUBLIC;
		}
	}

	public String getIBMOnlyStr() {
		char[] ibmonly = new char[1];
		ibmonly[0] = this.ibmOnly;
		return new String(ibmonly);
	}
	public void setIBMOnlyStr(String strIBMOnly) {
		ibmOnly = strIBMOnly.charAt(0);
	}

	public char getIbmOnly() {
		return ibmOnly;
	}
	public boolean isIbmOnlyOrConf() {
		if (ibmOnly == Defines.ETS_IBM_CONF || ibmOnly == Defines.ETS_IBM_ONLY)
			return true;
		else if (ibmOnly == Defines.ETS_PUBLIC)
			return false;
		else
			throw new RuntimeException("flag: ibmOnly: not ibmonly, ibm conf, or public");
	}

	public void setDocStatus(char c) {
		doc_status = c;
	}
	public void setDocStatus(String s) {
		if (s != null && !s.equals("")) {
			char c = s.charAt(0);
			doc_status = c;
		} else {
			doc_status = Defines.DOC_PUBLISH;
		}
	}
	public char getDocStatus() {
		return doc_status;
	}
	public String getDocStatusString() {
		if (doc_status == Defines.DOC_DRAFT)
			return "Draft";
		else if (doc_status == Defines.DOC_SUB_APP)
			return "Submitted for approval";
		else if (doc_status == Defines.DOC_REJECTED)
			return "Rejected";
		else if (doc_status == Defines.DOC_APPROVED)
			return "Approved";
		else
			return "Live";

	}
	public boolean isDocLive() {
		if (doc_status == Defines.DOC_DRAFT)
			return false;
		else if (doc_status == Defines.DOC_SUB_APP)
			return false;
		else if (doc_status == Defines.DOC_REJECTED)
			return false;
		else
			return true;
	}

	public void setApprovalComments(String c) {
		this.approval_comments = c;
	}
	public String getApprovalComments() {
		return approval_comments;
	}

	public void setApproverId(String c) {
		this.approver_id = setString(c);
	}
	public String getApproverId() {
		return approver_id;
	}

	public long getApproveDate() {
		return approve_date;
	}
	public void setApproveDate() {
		this.approve_date = new Date().getTime();
	}
	public void setApproveDate(long edate) {
		this.approve_date = edate;
	}
	public void setApproveDate(java.sql.Timestamp d) {
		this.approve_date = d.getTime();
	}

	public String getFormattedExpiryDate() {
		Date dtExpiryDate = new Date(expiry_date);
		SimpleDateFormat pdDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		return pdDateFormat.format(dtExpiryDate);
	}

	public long getExpiryDate() {
		return expiry_date;
	}
	public void setExpiryDate() {
		this.expiry_date = new Date().getTime();
	}
	public void setExpiryDate(long edate) {
		this.expiry_date = edate;
	}
	public void setExpiryDate(java.sql.Timestamp d) {
		this.expiry_date = d.getTime();
	}
	public void setExpiryDate(String smonth, String sday, String syear) {
		int month = Integer.parseInt(smonth.trim());
		int day = Integer.parseInt(sday.trim());
		int year = Integer.parseInt(syear.trim());

		Calendar c = Calendar.getInstance();
		c.set(year, month, day);
		Date d = c.getTime();
		this.expiry_date = d.getTime();
	}
	public boolean hasExpired() {
		//Calendar  c=  Calendar.getInstance();
		//c.set(1970,1,1);

		System.out.println("EEE1 expiry_date=" + expiry_date);

		//if (expiry_date!=0 && expiry_date > c.getTimeInMillis()){
		if (expiry_date != 0) {
			System.out.println("EEE expiry_date=" + expiry_date);
			java.util.Date d = new java.util.Date();
			System.out.println("EEE d=" + d);
			java.util.Date exd = new java.util.Date(expiry_date);
			Calendar pdCalendar = Calendar.getInstance();
			pdCalendar.setTime(exd);
			pdCalendar.set(Calendar.HOUR_OF_DAY, 0);
			pdCalendar.set(Calendar.MINUTE, 0);
			exd = pdCalendar.getTime();
			System.out.println("EEE exd=" + exd);
			if (exd.before(d))
				return true;
		}

		return false;
	}

	public void setDocHits(int hits) {
		this.hits = hits;
	}
	public int getDocHits() {
		return hits;
	}
	public String getDocHitsStr() {
		return Integer.toString(hits);
	}

	public void setSelfId(String s) {
		this.selfId = s;
	}
	public String getSelfId() {
		return selfId;
	}
/* ***************************************************************** */
	public void setDPrivate(String b) {
		if (b == null)
			dprivate = "0";
		else if (b.equals("1"))
			this.dprivate = b;
		else
			this.dprivate = "0";
	}

	
	public void setDPrivate(boolean b) {
		if (b)
			this.dprivate = "1";
		else
			this.dprivate = "0";
	}

	public String getDPrivate() {
		return dprivate;
	}
	
	public boolean isReadRestricted()
	{
		if( this.dprivate.equals("0") )
			return false;
		return true;
	}
	
	
	public boolean IsDPrivate() {
		if (dprivate == null)
			return false;
		else if (dprivate.equals("1"))
			return true;
		else
			return false;
	}

	/* ------------------------------------------------------------------- */
	public void setDPrivateEdit(String b) {
		if (b == null)
			dprivateEdit = "0";
		else if (b.equals("1"))
			this.dprivateEdit = b;
		else
			this.dprivateEdit = "0";
	}

	
	public void setDPrivateEdit(boolean b) {
		if (b)
			this.dprivateEdit = "1";
		else
			this.dprivateEdit = "0";
	}

	public String getDPrivateEdit() {
		return dprivateEdit;
	}
	

	public boolean IsDPrivateEdit() {
		if (dprivateEdit == null)
			return false;
		else if (dprivateEdit.equals("1"))
			return true;
		else
			return false;
	}

	public void setDocReadEditRestricted(String a, String b) {
		if ( (a == null) || (b == null) )
			docReadEditRestricted = "0";
		else if ( (a.equals("1")) || (b.equals("1")) )
			this.docReadEditRestricted = "1";
		else
			this.docReadEditRestricted = "0";
	}

	public String getDocReadEditRestricted() {
		return docReadEditRestricted;
	}
	/* --------------------------------------------------------  */
	

	/*
	void setPrivs(Vector privsList){
	privs = privsList;
	}
	void setPrivs(String privsList){
	if (privsList != null){ 
	    StringTokenizer st = new StringTokenizer(privsList, ",");
	    privs = new Vector();
	    while (st.hasMoreTokens()){
		String priv = st.nextToken();
		privs.addElement(priv);
	    }
	}
	}
	Vector getPrivs(){
	return privs;
	}
	String getPrivsString(){
	String s = "";
	for (int i = 0; i<privs.size(); i++){
	    if (i > 0){
		s += ",";
	    }	
	    
	    s += (String)privs.elementAt(i);
	}
	
	return s;
	}		
	Vector getPrivsInts(){
	Vector s = new Vector();
	for (int i = 0; i<privs.size(); i++){
	    s.addElement(new Integer((String)privs.elementAt(i)));
	}
	
	return s;
	}		
	*/

	public String getStringKey(String key) {
		if (key.equals(Defines.SORT_BY_NAME_STR))
			return name;
		else if (key.equals(Defines.SORT_BY_TYPE_STR))
			return filetype;
		else if (key.equals(Defines.SORT_BY_AUTH_STR))
			return userid;
		else
			return "";

	}
	public int getIntKey(String key) {
		return 0;
	}

	private String setString(String c) {
		if (c == null)
			return "";

		return c;
	}

	public void setUserName(String username) {
		this.username = username;
	}
	public String getUserName() {
		return username;
	}

	/**
	 * @return
	 */
	public List getDocFiles() {
		return m_lstDocFiles;
	}

	/**
	 * @param lstDocFiles
	 */
	public void setDocFiles(List lstDocFiles) {
		m_lstDocFiles = lstDocFiles;
	}

	/**
	 * @return
	 */
	public String getIBMConfidential() {
		return m_strIBMConfidential;
	}

	/**
	 * @param strIBMConfidential
	 */
	public void setIBMConfidential(String strIBMConfidential) {
		m_strIBMConfidential = strIBMConfidential;
	}

	/**
	 * @return
	 */
	public String getItarStatus() {
		return m_strItarStatus;
	}

	/**
	 * @param strItarStatus
	 */
	public void setItarStatus(String strItarStatus) {
		m_strItarStatus = strItarStatus;
	}

	/**
	 * @param strProblemId
	 */
	public void setProblemId(String strProblemId) {
		this.m_strProblemId = strProblemId;
	}
	
	/**
	 * @return
	 */
	public String getProblemId() {
		return m_strProblemId;
	}
}
