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

/**
 * @author v2srikau
 */
public class DocNotify {

	private String m_strNotifyAllFlag;
	private String m_strUserId;
	private String m_strGroupId;
	private int m_iDocID;

	/**
	 * @return
	 */
	public int getDocID() {
		return m_iDocID;
	}

	/**
	 * @return
	 */
	public boolean isNotifyAll() {
		return !StringUtil.isNullorEmpty(m_strNotifyAllFlag)
			&& DocConstants.IND_YES.equals(m_strNotifyAllFlag);
	}

	/**
	 * @return
	 */
	public String getNotifyAllFlag() {
		return m_strNotifyAllFlag;
	}

	/**
	 * @return
	 */
	public String getUserId() {
		return m_strUserId;
	}

	/**
	 * @param string
	 */
	public void setDocID(int iDocID) {
		m_iDocID = iDocID;
	}

	/**
	 * @param string
	 */
	public void setNotifyAllFlag(String strNotifyAllFlag) {
		m_strNotifyAllFlag = strNotifyAllFlag;
	}

	/**
	 * @param string
	 */
	public void setUserId(String strUserId) {
		m_strUserId = strUserId;
	}

	/**
	 * @return
	 */
	public String getGroupId() {
		return m_strGroupId;
	}

	/**
	 * @param strGroupId
	 */
	public void setGroupId(String strGroupId) {
		m_strGroupId = strGroupId;
	}

}