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

package oem.edge.ets.fe;

import java.util.Date;

/**
 * @author v2srikau
 */
public class ETSProjectStatus {
    private String m_strProjectId;
    private String m_strSourceId;
    private String m_strDestId;
    private String m_strType;
    private String m_strLastUserId;
    private Date m_dtLastTimestamp;
    
    /**
     * Default Constructor
     */
    public ETSProjectStatus() {
        // EMPTY CONSTRUCTOR
    }
    
    /**
     * @param strProjectId
     * @param strSourceId
     * @param strDestId
     * @param strType
     * @param strLastUserId
     * @param lastTimestamp
     */
    public ETSProjectStatus(
            String strProjectId, 
            String strSourceId, 
            String strDestId, 
            String strType, 
            String strLastUserId, 
            Date lastTimestamp) {
        m_strProjectId = strProjectId; 
        m_strSourceId = strSourceId; 
        m_strDestId = strDestId; 
        m_strType = strType; 
        m_strLastUserId = strLastUserId; 
        m_dtLastTimestamp = lastTimestamp;
    }
    
    /**
     * @return Returns the m_dtLastTimestamp.
     */
    public Date getLastTimestamp() {
        return m_dtLastTimestamp;
    }
    /**
     * @param lastTimestamp The m_dtLastTimestamp to set.
     */
    public void setLastTimestamp(Date lastTimestamp) {
        m_dtLastTimestamp = lastTimestamp;
    }
    /**
     * @return Returns the m_strDestId.
     */
    public String getDestId() {
        return m_strDestId;
    }
    /**
     * @param destId The m_strDestId to set.
     */
    public void setDestId(String destId) {
        m_strDestId = destId;
    }
    /**
     * @return Returns the m_strLastUserId.
     */
    public String getLastUserId() {
        return m_strLastUserId;
    }
    /**
     * @param lastUserId The m_strLastUserId to set.
     */
    public void setLastUserId(String lastUserId) {
        m_strLastUserId = lastUserId;
    }
    /**
     * @return Returns the m_strProjectId.
     */
    public String getProjectId() {
        return m_strProjectId;
    }
    /**
     * @param projectId The m_strProjectId to set.
     */
    public void setProjectId(String projectId) {
        m_strProjectId = projectId;
    }
    /**
     * @return Returns the m_strSourceId.
     */
    public String getSourceId() {
        return m_strSourceId;
    }
    /**
     * @param sourceId The m_strSourceId to set.
     */
    public void setSourceId(String sourceId) {
        m_strSourceId = sourceId;
    }
    /**
     * @return Returns the m_strType.
     */
    public String getType() {
        return m_strType;
    }
    /**
     * @param type The m_strType to set.
     */
    public void setType(String type) {
        m_strType = type;
    }
}
