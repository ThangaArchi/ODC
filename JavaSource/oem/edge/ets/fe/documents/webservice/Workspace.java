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

package oem.edge.ets.fe.documents.webservice;

import java.io.Serializable;

public class Workspace implements Serializable {
	
	private String m_strWorkspaceId;
	private String m_strWorkspaceName;
	
	private String m_strWorkspaceType;
	private String m_strPrivate;
	private boolean m_bIsITAR;
	
	/** Following 3 variables are used to store data for generating emails **/
	private transient String m_strAppName;
	private transient String m_strLinkID;
	private transient int m_iTopCatID;
	private transient String m_strCompany;
	
	/**
	 * 
	 */
	public Workspace() {
	}

	/**
	 * 
	 */
	public Workspace(String strWorkspaceId, String strWorkspaceName) {
		m_strWorkspaceId = strWorkspaceId;
		m_strWorkspaceName = strWorkspaceName;
	}
	
	/**
	 * @return
	 */
	public String getWorkspaceId() {
		return m_strWorkspaceId;
	}

	/**
	 * @return
	 */
	public String getWorkspaceName() {
		return m_strWorkspaceName;
	}

	/**
	 * @param strWorkspaceId
	 */
	public void setWorkspaceId(String strWorkspaceId) {
		m_strWorkspaceId = strWorkspaceId;
	}

	/**
	 * @param strWorkspaceName
	 */
	public void setWorkspaceName(String strWorkspaceName) {
		m_strWorkspaceName = strWorkspaceName;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer strBuffer = new StringBuffer("");
		strBuffer.append(
			"WSID="
				+ getWorkspaceId()
				+ ":WSNAME="
				+ getWorkspaceName());
		return strBuffer.toString();
	}
	
	/**
	 * @return
	 */
	public boolean isITAR() {
		return m_bIsITAR;
	}

	/**
	 * @return
	 */
	public String getPrivate() {
		return m_strPrivate;
	}

	/**
	 * @return
	 */
	public String getWorkspaceType() {
		return m_strWorkspaceType;
	}

	/**
	 * @param bIsITAR
	 */
	public void setITAR(boolean bIsITAR) {
		m_bIsITAR = bIsITAR;
	}

	/**
	 * @param strPrivate
	 */
	public void setPrivate(String strPrivate) {
		m_strPrivate = strPrivate;
	}

	/**
	 * @param strWorkspaceType
	 */
	public void setWorkspaceType(String strWorkspaceType) {
		m_strWorkspaceType = strWorkspaceType;
	}

    /**
     * @return Returns the m_strAppName.
     */
    public String getAppName() {
        return m_strAppName;
    }
    /**
     * @param appName The m_strAppName to set.
     */
    public void setAppName(String appName) {
        m_strAppName = appName;
    }
    /**
     * @return Returns the m_strLinkID.
     */
    public String getLinkID() {
        return m_strLinkID;
    }
    /**
     * @param linkID The m_strLinkID to set.
     */
    public void setLinkID(String linkID) {
        m_strLinkID = linkID;
    }

    /**
     * @return Returns the m_iTopCatID.
     */
    public int getTopCatID() {
        return m_iTopCatID;
    }
    /**
     * @param topCatID The m_iTopCatID to set.
     */
    public void setTopCatID(int topCatID) {
        m_iTopCatID = topCatID;
    }

    /**
     * @return Returns the m_strCompany.
     */
    public String getCompany() {
        return m_strCompany;
    }
    /**
     * @param company The m_strCompany to set.
     */
    public void setCompany(String company) {
        m_strCompany = company;
    }
}