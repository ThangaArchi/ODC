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

public class Category implements Serializable {

	private int m_iCatId;
	private String m_strCat;
	private int m_iParentId;

	/* This field is for logic purposes only. Doesn't need to be serialized */
	private transient boolean m_bIBMOnly;

	/**
	 * 
	 */
	public Category() {
	}

	/**
	 * 
	 */
	public Category(int iCatId, int iParentId, String strCat) {
		m_iCatId = iCatId;
		m_iParentId = iParentId;
		m_strCat = strCat;
	}

	/**
	 * @return
	 */
	public String getCat() {
		return m_strCat;
	}

	/**
	 * @return
	 */
	public int getCatId() {
		return m_iCatId;
	}

	/**
	 * @param strWorkspaceId
	 */
	public void setCat(String strCat) {
		m_strCat = strCat;
	}

	/**
	 * @param iCatId
	 */
	public void setCatId(int iCatId) {
		m_iCatId = iCatId;
	}

	/**
	 * @return
	 */
	public int getParentId() {
		return m_iParentId;
	}

	/**
	 * @param iParentId
	 */
	public void setParentId(int iParentId) {
		m_iParentId = iParentId;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer strBuffer = new StringBuffer("");
		strBuffer.append(
			"CATID="
				+ getCatId()
				+ ":CATNAME="
				+ getCat()
				+ ":PARENTID="
				+ getParentId());
		return strBuffer.toString();
	}

    /**
     * @return Returns the m_bIBMOnly.
     */
    public boolean isIBMOnly() {
        return m_bIBMOnly;
    }
    
    /**
     * @param only The m_bIBMOnly to set.
     */
    public void setIBMOnly(boolean only) {
        m_bIBMOnly = only;
    }
}