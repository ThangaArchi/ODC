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
 * @author v2phani
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class EtsIssDynSubTypeGuiModel {
	
	public static final String VERSION = "1.9";
	
	private String printDynStr;
	private String subTypeStop;
	private String submitStop;
	private String goFlag;
	private String listBoxRefName;

	/**
	 * Constructor for EtsIssDynSubTypeGuiModel.
	 */
	public EtsIssDynSubTypeGuiModel() {
		super();
	}

	/**
	 * Returns the printDynStr.
	 * @return String
	 */
	public String getPrintDynStr() {
		return printDynStr;
	}

	/**
	 * Returns the submitStop.
	 * @return String
	 */
	public String getSubmitStop() {
		return submitStop;
	}

	/**
	 * Returns the subTypeStop.
	 * @return String
	 */
	public String getSubTypeStop() {
		return subTypeStop;
	}

	/**
	 * Sets the printDynStr.
	 * @param printDynStr The printDynStr to set
	 */
	public void setPrintDynStr(String printDynStr) {
		this.printDynStr = printDynStr;
	}

	/**
	 * Sets the submitStop.
	 * @param submitStop The submitStop to set
	 */
	public void setSubmitStop(String submitStop) {
		this.submitStop = submitStop;
	}

	/**
	 * Sets the subTypeStop.
	 * @param subTypeStop The subTypeStop to set
	 */
	public void setSubTypeStop(String subTypeStop) {
		this.subTypeStop = subTypeStop;
	}

	/**
	 * Returns the goFlag.
	 * @return String
	 */
	public String getGoFlag() {
		return goFlag;
	}

	/**
	 * Sets the goFlag.
	 * @param goFlag The goFlag to set
	 */
	public void setGoFlag(String goFlag) {
		this.goFlag = goFlag;
	}

	/**
	 * Returns the listBoxRefName.
	 * @return String
	 */
	public String getListBoxRefName() {
		return listBoxRefName;
	}

	/**
	 * Sets the listBoxRefName.
	 * @param listBoxRefName The listBoxRefName to set
	 */
	public void setListBoxRefName(String listBoxRefName) {
		this.listBoxRefName = listBoxRefName;
	}

}

