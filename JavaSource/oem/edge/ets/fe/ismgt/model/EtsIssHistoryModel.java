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

import java.io.Serializable;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssHistoryModel implements Serializable{
	
	public static final String VERSION = "1.24";
	
	private String edgeProblemId;
	private String actionTs;
	private String userName;
	private String actionName;
	private String issueState;
	

	/**
	 * 
	 */
	public EtsIssHistoryModel() {
		super();
		
	}

	/**
	 * @return
	 */
	public String getActionName() {
		return actionName;
	}

	/**
	 * @return
	 */
	public String getActionTs() {
		return actionTs;
	}

	/**
	 * @return
	 */
	public String getEdgeProblemId() {
		return edgeProblemId;
	}

	/**
	 * @return
	 */
	public String getIssueState() {
		return issueState;
	}

	/**
	 * @return
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param string
	 */
	public void setActionName(String string) {
		actionName = string;
	}

	/**
	 * @param string
	 */
	public void setActionTs(String string) {
		actionTs = string;
	}

	/**
	 * @param string
	 */
	public void setEdgeProblemId(String string) {
		edgeProblemId = string;
	}

	/**
	 * @param string
	 */
	public void setIssueState(String string) {
		issueState = string;
	}

	/**
	 * @param string
	 */
	public void setUserName(String string) {
		userName = string;
	}

}//end of class
