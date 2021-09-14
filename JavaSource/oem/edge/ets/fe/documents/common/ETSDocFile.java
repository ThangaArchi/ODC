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

import java.io.InputStream;

/**
 * This is the wrapper object for DOC FILE
 * @author v2srikau
 */
public class ETSDocFile {

	private String m_strFileName;
	private int m_iSize;
	private String m_strType;
	private int m_iDocfileId;

	private String m_strFileDescription;
	private String m_strFileStatus;

	private int m_lFileSize;
	private transient InputStream m_pdInputStream;
	
	/**
	 * @return
	 */
	public int getDocfileId() {
		return m_iDocfileId;
	}

	/**
	 * @return
	 */
	public int getSize() {
		return m_iSize;
	}

	/**
	 * @return
	 */
	public String getSizeStr() {
		return Integer.toString(m_iSize);
	}

	/**
	 * @return
	 */
	public String getFileName() {
		return m_strFileName;
	}

	/**
	 * @return
	 */
	public String getType() {
		return m_strType;
	}

	/**
	 * @param iDocfileId
	 */
	public void setDocfileId(int iDocfileId) {
		m_iDocfileId = iDocfileId;
	}

	/**
	 * @param iSize
	 */
	public void setSize(int iSize) {
		m_iSize = iSize;
	}

	/**
	 * @param strFileName
	 */
	public void setFileName(String strFileName) {
		m_strFileName = strFileName;
		setType(strFileName);
	}

	/**
	 * @param strFileName
	 */
	public void setType(String strFileName) {
		int index = strFileName.lastIndexOf(".");
		m_strType =
			(strFileName.substring(index + 1, strFileName.length()))
				.toLowerCase();
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
     * @return Returns the m_lFilSize.
     */
    public int getFileSize() {
        return m_lFileSize;
    }
    /**
     * @param fileSize The m_lFilSize to set.
     */
    public void setFileSize(int fileSize) {
        m_lFileSize = fileSize;
    }
    /**
     * @return Returns the m_pdInputStream.
     */
    public InputStream getInputStream() {
        return m_pdInputStream;
    }
    /**
     * @param inputStream The m_pdInputStream to set.
     */
    public void setInputStream(InputStream inputStream) {
        m_pdInputStream = inputStream;
    }

}
