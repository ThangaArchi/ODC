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

import java.io.Serializable;

/**
 * This class acts as a value object to store dd, mm and yy values
 * @author v2srikau
 */
public class DocMQMessage implements Serializable {

	private String m_strDocId;
	private String m_strProjId;
	private String m_strFileNames;
	private String m_strFileSizes;
	
	private String m_strUserId;
	
	private String m_strNotifyOption;
	
	/** Following 2 params are only used in case of Issue Documents */
	private String m_strFileDescription;
	private String m_strFileStatus;
	
	/** Following 3 params are only used in case of Meetings Documents */
	private String m_strKeywords;
	private String m_strDocName;
	private String m_strDocDescription;
	
    /**
     * @return Returns the m_strDocName.
     */
    public String getDocName() {
        return m_strDocName;
    }
    /**
     * @param docName The m_strDocName to set.
     */
    public void setDocName(String docName) {
        m_strDocName = docName;
    }
    /**
     * @return Returns the m_strKeywords.
     */
    public String getKeywords() {
        return m_strKeywords;
    }
    /**
     * @param keywords The m_strKeywords to set.
     */
    public void setKeywords(String keywords) {
        m_strKeywords = keywords;
    }
    /**
     * @return Returns the m_strDocDescription.
     */
    public String getDocDescription() {
        return m_strDocDescription;
    }
    /**
     * @param docName The m_strDocName to set.
     */
    public void setDocDescription(String docDescription) {
        m_strDocDescription = docDescription;
    }
    
	/**
	 * @return
	 */
	public String getDocId() {
		return m_strDocId;
	}

	/**
	 * @return
	 */
	public String getFileNames() {
		return m_strFileNames;
	}

	/**
	 * @return
	 */
	public String getFileSizes() {
		return m_strFileSizes;
	}

	/**
	 * @return
	 */
	public String getProjId() {
		return m_strProjId;
	}

	/**
	 * @param strDocId
	 */
	public void setDocId(String strDocId) {
		m_strDocId = strDocId;
	}

	/**
	 * @param strFileNames
	 */
	public void setFileNames(String strFileNames) {
		m_strFileNames = strFileNames;
	}

	/**
	 * @param strFileSizes
	 */
	public void setFileSizes(String strFileSizes) {
		m_strFileSizes = strFileSizes;
	}

	/**
	 * @param strProjId
	 */
	public void setProjId(String strProjId) {
		m_strProjId = strProjId;
	}

	/**
	 * @return
	 */
	public String getFileDescription() {
		return m_strFileDescription;
	}

	/**
	 * @return
	 */
	public String getFileStatus() {
		return m_strFileStatus;
	}

	/**
	 * @param strFileDescription
	 */
	public void setFileDescription(String strFileDescription) {
		m_strFileDescription = strFileDescription;
	}

	/**
	 * @param strFileStatus
	 */
	public void setFileStatus(String strFileStatus) {
		m_strFileStatus = strFileStatus;
	}

    /**
     * @return Returns the m_strUserId.
     */
    public String getUserId() {
        return m_strUserId;
    }
    /**
     * @param userId The m_strUserId to set.
     */
    public void setUserId(String userId) {
        m_strUserId = userId;
    }
    
    /**
     * @return Returns the m_strNotifyOption.
     */
    public String getNotifyOption() {
        return m_strNotifyOption;
    }
    
    /**
     * @param notifyOption The m_strNotifyOption to set.
     */
    public void setNotifyOption(String notifyOption) {
        m_strNotifyOption = notifyOption;
    }
}