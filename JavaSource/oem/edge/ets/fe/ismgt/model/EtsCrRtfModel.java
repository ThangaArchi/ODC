/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004                                          */
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

package oem.edge.ets.fe.ismgt.model;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsCrRtfModel {

	public static final String VERSION = "1.29";
	private String pmoId;
	private String pmoProjId;
	private String parentPmoId;
	private int rtfId;
	private String rtfName;
	private String rtfAliasName;
	private String rtfBlobStr;
	private int rtfLength;

	/**
	 * 
	 */
	public EtsCrRtfModel() {
		super();

	}

	/**
	 * @return
	 */
	public String getParentPmoId() {
		return parentPmoId;
	}

	/**
	 * @return
	 */
	public String getPmoId() {
		return pmoId;
	}

	/**
	 * @return
	 */
	public String getPmoProjId() {
		return pmoProjId;
	}

	/**
	 * @return
	 */
	public String getRtfAliasName() {
		return rtfAliasName;
	}

	/**
	 * @return
	 */
	public String getRtfBlobStr() {
		return rtfBlobStr;
	}

	
	/**
	 * @return
	 */
	public String getRtfName() {
		return rtfName;
	}

	/**
	 * @param string
	 */
	public void setParentPmoId(String string) {
		parentPmoId = string;
	}

	/**
	 * @param string
	 */
	public void setPmoId(String string) {
		pmoId = string;
	}

	/**
	 * @param string
	 */
	public void setPmoProjId(String string) {
		pmoProjId = string;
	}

	/**
	 * @param string
	 */
	public void setRtfAliasName(String string) {
		rtfAliasName = string;
	}

	/**
	 * @param string
	 */
	public void setRtfBlobStr(String string) {
		rtfBlobStr = string;
	}

	
	/**
	 * @param string
	 */
	public void setRtfName(String string) {
		rtfName = string;
	}

	

	/**
	 * @return
	 */
	public int getRtfLength() {
		return rtfLength;
	}

	/**
	 * @param i
	 */
	public void setRtfLength(int i) {
		rtfLength = i;
	}

	/**
	 * @return
	 */
	public int getRtfId() {
		return rtfId;
	}

	/**
	 * @param i
	 */
	public void setRtfId(int i) {
		rtfId = i;
	}

} //end of class
