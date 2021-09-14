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


/*
 * Created on Oct 25, 2004
 * Created by v2sathis@us.ibm.com
 */
 
package oem.edge.ets.fe.pmo;

import java.sql.Timestamp;

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSObj;

public class ETSPMODoc implements ETSObj {
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2003 - 2004";
	private static final String CLASS_VERSION = "1.2";
	
	private String PMOId;
	private String PMOProjectId;
	private String DocId;
	private String ParentPMOId;
	private String ParentType;
	private String DocName;
	private int DocType;
	private String IsCompressed;
	private String DocDesc;
	private String OwnerId;
	private String SecurityLevel;
	private String VersionInfo;
	private Timestamp PublishDate;
	private Timestamp UploadDate;
	private Timestamp UpdateDate;
	private int CompressedSize;
	private int UncompressedSize;
	private Timestamp LastTimestamp;
	
	
	/**
	 * @return
	 */
	public int getCompressedSize() {
		return this.CompressedSize;
	}

	/**
	 * @return
	 */
	public String getDocDesc() {
		return this.DocDesc;
	}

	/**
	 * @return
	 */
	public String getDocId() {
		return this.DocId;
	}

	/**
	 * @return
	 */
	public String getDocName() {
		return this.DocName;
	}

	/**
	 * @return
	 */
	public int getDocType() {
		return this.DocType;
	}

	/**
	 * @return
	 */
	public String getIsCompressed() {
		return this.IsCompressed;
	}

	/**
	 * @return
	 */
	public Timestamp getLastTimestamp() {
		return this.LastTimestamp;
	}

	/**
	 * @return
	 */
	public String getOwnerId() {
		return this.OwnerId;
	}

	/**
	 * @return
	 */
	public String getParentPMOId() {
		return this.ParentPMOId;
	}

	/**
	 * @return
	 */
	public String getParentType() {
		return this.ParentType;
	}

	/**
	 * @return
	 */
	public String getPMOId() {
		return this.PMOId;
	}

	/**
	 * @return
	 */
	public String getPMOProjectId() {
		return this.PMOProjectId;
	}

	/**
	 * @return
	 */
	public Timestamp getPublishDate() {
		return this.PublishDate;
	}

	/**
	 * @return
	 */
	public String getSecurityLevel() {
		return this.SecurityLevel;
	}

	/**
	 * @return
	 */
	public int getUncompressedSize() {
		return this.UncompressedSize;
	}

	/**
	 * @return
	 */
	public Timestamp getUpdateDate() {
		return this.UpdateDate;
	}

	/**
	 * @return
	 */
	public Timestamp getUploadDate() {
		return this.UploadDate;
	}

	/**
	 * @return
	 */
	public String getVersionInfo() {
		return this.VersionInfo;
	}

	/**
	 * @param i
	 */
	public void setCompressedSize(int i) {
		this.CompressedSize = i;
	}

	/**
	 * @param string
	 */
	public void setDocDesc(String string) {
		this.DocDesc = string;
	}

	/**
	 * @param string
	 */
	public void setDocId(String string) {
		this.DocId = string;
	}

	/**
	 * @param string
	 */
	public void setDocName(String string) {
		this.DocName = string;
	}

	/**
	 * @param i
	 */
	public void setDocType(int i) {
		this.DocType = i;
	}

	/**
	 * @param string
	 */
	public void setIsCompressed(String string) {
		this.IsCompressed = string;
	}

	/**
	 * @param timestamp
	 */
	public void setLastTimestamp(Timestamp timestamp) {
		this.LastTimestamp = timestamp;
	}

	/**
	 * @param string
	 */
	public void setOwnerId(String string) {
		this.OwnerId = string;
	}

	/**
	 * @param string
	 */
	public void setParentPMOId(String string) {
		this.ParentPMOId = string;
	}

	/**
	 * @param string
	 */
	public void setParentType(String string) {
		this.ParentType = string;
	}

	/**
	 * @param string
	 */
	public void setPMOId(String string) {
		this.PMOId = string;
	}

	/**
	 * @param string
	 */
	public void setPMOProjectId(String string) {
		this.PMOProjectId = string;
	}

	/**
	 * @param timestamp
	 */
	public void setPublishDate(Timestamp timestamp) {
		this.PublishDate = timestamp;
	}

	/**
	 * @param string
	 */
	public void setSecurityLevel(String string) {
		this.SecurityLevel = string;
	}

	/**
	 * @param i
	 */
	public void setUncompressedSize(int i) {
		this.UncompressedSize = i;
	}

	/**
	 * @param timestamp
	 */
	public void setUpdateDate(Timestamp timestamp) {
		this.UpdateDate = timestamp;
	}

	/**
	 * @param timestamp
	 */
	public void setUploadDate(Timestamp timestamp) {
		this.UploadDate = timestamp;
	}

	/**
	 * @param string
	 */
	public void setVersionInfo(String string) {
		this.VersionInfo = string;
	}

	public String getFileType(){
		 if (getDocDesc() == null || getDocDesc().equals("")){
			return "";   
		 }
		 else{
			int index = getDocDesc().lastIndexOf(".");
			return (getDocDesc().substring(index + 1, getDocDesc().length())).toLowerCase();
         
		 }
	  }

	public String getStringKey(String key){
		 if (key.equals(Defines.SORT_BY_TYPE_STR))
			return getFileType();
		 else
			return "";
         
	  }
	  public int getIntKey(String key){
		 return 0;
	  }

}
