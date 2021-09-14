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

package oem.edge.ets.fe.documents.common;



import java.text.SimpleDateFormat;
import java.util.Date;

import oem.edge.ets.fe.ETSDetailedObj;
/**
 * @author v2srikau
 */
public class Group extends ETSDetailedObj {

	private String m_strGroupId;
	private String m_strGroupName;
	private String m_strDescription;
	private String m_strType;
	private String m_strOwner;
	private String m_strProjectId;
	private long m_lTimestamp;
	private String group_securityClassification;
	private String m_userId;

	/**
	 * 
	 */
	public Group() {

	}

	/**
	 * @param strGroupId
	 * @param strGroupName
	 * @param strType
	 * @param strDescription
	 * @param strOwner
	 * @param strProjectId
	 * @param lTimestamp
	 */
	public Group(
		String strGroupId,
		String strGroupName,
		String strType,
		String strDescription,
		String strOwner,
		String strProjectId,
		long lTimestamp) {
		m_strGroupId = strGroupId;
		m_strGroupName = strGroupName;
		m_strType = strType;
		m_strDescription = strDescription;
		m_strOwner = strOwner;
		m_strProjectId = strProjectId;
		m_lTimestamp = lTimestamp;
	}

	/**
	 * @return
	 */
	public long getTimestamp() {
		return m_lTimestamp;
	}

	/**
	 * @return
	 */
	public String getDescription() {
		return m_strDescription;
	}

	/**
	 * @return
	 */
	public String getGroupId() {
		return m_strGroupId;
	}

	/**
	 * @return
	 */
	public String getGroupName() {
		return m_strGroupName;
	}

	/**
	 * @return
	 */
	public String getOwner() {
		return m_strOwner;
	}

	/**
	 * @return
	 */
	public String getProjectId() {
		return m_strProjectId;
	}

	/**
	 * @return
	 */
	public String getType() {
		return m_strType;
	}

	/**
	 * @return
	 */
    public String getFormattedLastTimestamp() {
		Date dtLastTimestamp = new Date(m_lTimestamp);
		SimpleDateFormat pdDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
		return pdDateFormat.format(dtLastTimestamp);
	}
    public long getLastTimestamp(){
	return m_lTimestamp;
    }
    public void setLastTimestamp(){
	this.m_lTimestamp = new Date().getTime();
    }
    public void setLastTimestamp(long d){
	this.m_lTimestamp = d;
    }
    public void setLastTimestamp(java.sql.Timestamp d){
	this.m_lTimestamp = d.getTime();
    }

	/**
	 * @param strDescription
	 */
	public void setDescription(String strDescription) {
		m_strDescription = strDescription;
	}

	/**
	 * @param strGroupId
	 */
	public void setGroupId(String strGroupId) {
		m_strGroupId = strGroupId;
	}

	/**
	 * @param strGroupName
	 */
	public void setGroupName(String strGroupName) {
		m_strGroupName = strGroupName;
	}

	/**
	 * @param strOwnering
	 */
	public void setOwner(String strOwner) {
		m_strOwner = strOwner;
	}

	/**
	 * @param strProjectId
	 */
	public void setProjectId(String strProjectId) {
		m_strProjectId = strProjectId;
	}

	/**
	 * @param strType
	 */
	public void setType(String strType) {
		m_strType = strType;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object pdObj) {
		if (pdObj instanceof Group) {
			Group udGroup = (Group) pdObj; 
			if (udGroup.getGroupId().equals(this.getGroupId())) {
				return true;
			}
		}
		return false;
	}

	public void setGroupSecurityClassification(String secClassification) {
		 /* if (secClassification.equals("0") || secClassification.equals("1")) {
		   setGroupType("PUBLIC");
		  } else {
		   int sec = Integer.parseInt(secClassification) - 2;
		   secClassification = String.valueOf(sec);
		   setGroupType("PRIVATE");
		  }*/
		   
		this.group_securityClassification = secClassification;
	 }
	 public String getGroupSecurityClassification() {
	  return group_securityClassification;
	 }
	 
	 public void setUserId(String userId)
	 {
	 	m_userId = userId;
	 }
	 public String getUserId()
	 {
	 	return m_userId;
	 }

}
