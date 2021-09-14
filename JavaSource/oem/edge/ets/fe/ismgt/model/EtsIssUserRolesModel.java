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
public class EtsIssUserRolesModel {
	
	public static final String VERSION = "1.19";
	
	///state actions///
	private boolean usrSubmitIssue;
	private boolean usrReqIssueType;
	
	///other fields
	private boolean showOwnerName;
	
	//VISITOR && EXECUTIVE 
	private boolean usrVisitor;
	
	//LOGGED IN user internal
	private boolean usrInternal;
		
	//BLADE USER INTERNAL
	private boolean bladeUsrInt;
	
	
	

	/**
	 * 
	 */
	public EtsIssUserRolesModel() {
		super();
		
	}

	
		/**
	 * @return
	 */
	public boolean isUsrReqIssueType() {
		return usrReqIssueType;
	}



	/**
	 * @return
	 */
	public boolean isUsrSubmitIssue() {
		return usrSubmitIssue;
	}

	
	/**
	 * @param b
	 */
	public void setUsrReqIssueType(boolean b) {
		usrReqIssueType = b;
	}

	
	/**
	 * @param b
	 */
	public void setUsrSubmitIssue(boolean b) {
		usrSubmitIssue = b;
	}

	/**
	 * @return
	 */
	public boolean isShowOwnerName() {
		return showOwnerName;
	}

	/**
	 * @param b
	 */
	public void setShowOwnerName(boolean b) {
		showOwnerName = b;
	}

	/**
	 * @return
	 */
	public boolean isUsrVisitor() {
		return usrVisitor;
	}

	/**
	 * @param b
	 */
	public void setUsrVisitor(boolean b) {
		usrVisitor = b;
	}


	/**
	 * @return
	 */
	public boolean isUsrInternal() {
		return usrInternal;
	}


	/**
	 * @param b
	 */
	public void setUsrInternal(boolean b) {
		usrInternal = b;
	}

	
	/**
	 * @return
	 */
	public boolean isBladeUsrInt() {
		return bladeUsrInt;
	}

	
	/**
	 * @param b
	 */
	public void setBladeUsrInt(boolean b) {
		bladeUsrInt = b;
	}

}//end of class
