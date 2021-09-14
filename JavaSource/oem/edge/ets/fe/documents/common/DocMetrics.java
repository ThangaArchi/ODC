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
import java.util.List;

/**
 * This class acts as a value object to store doc id and access date/time
 * @author v2srikau
 */
public class DocMetrics {

	private int m_iDocId;
	private String m_strDocName;
	private Date m_dtAccessTime;
	private List m_lstDocFiles;

	public DocMetrics(int iDocId, String strDocName, Date dtAccessTime) {
		m_iDocId = iDocId;
		m_strDocName = strDocName;
		m_dtAccessTime = dtAccessTime;
	}
	/**
	 * @return
	 */
	public Date getAccessTime() {
		return m_dtAccessTime;
	}

	/**
	 * @return
	 */
	public int getDocId() {
		return m_iDocId;
	}

	/**
	 * @param dtAccessTime
	 */
	public void setAccessTime(Date dtAccessTime) {
		m_dtAccessTime = dtAccessTime;
	}

	/**
	 * @param iDocId
	 */
	public void setDocId(int iDocId) {
		m_iDocId = iDocId;
	}

	/**
	 * @return
	 */
	public String getDocName() {
		return m_strDocName;
	}

	/**
	 * @param strDocName
	 */
	public void setDocName(String strDocName) {
		m_strDocName = strDocName;
	}

	/**
	 * @return
	 */
	public String getFormattedAccessTime() {
		SimpleDateFormat pdDateFormat =
			new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
		return pdDateFormat.format(m_dtAccessTime);
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

}
