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
public class EtsIssUserActionsModel {
	
	public static final String VERSION = "1.19";
	
	///state actions///
	private boolean usrSubmitIssue;
	private boolean usrModifyIssue;
	private boolean usrViewIssue;
	private boolean usrResolveIssue;
	private boolean usrRejectIssue;
	private boolean usrCloseIssue;
	private boolean usrCommentIssue;
	private boolean usrChangeOwner;
	private boolean usrWithDraw;
	private boolean usrSubscribe;
	
	//actions available
	private boolean actionavailable;
	
		
	
	/**
	 * 
	 */
	public EtsIssUserActionsModel() {
		super();
		
	}

	
	/**
	 * @return
	 */
	public boolean isUsrChangeOwner() {
		return usrChangeOwner;
	}

	/**
	 * @return
	 */
	public boolean isUsrCloseIssue() {
		return usrCloseIssue;
	}

	/**
	 * @return
	 */
	public boolean isUsrCommentIssue() {
		return usrCommentIssue;
	}

	/**
	 * @return
	 */
	public boolean isUsrModifyIssue() {
		return usrModifyIssue;
	}

	/**
	 * @return
	 */
	public boolean isUsrRejectIssue() {
		return usrRejectIssue;
	}

	
	/**
	 * @return
	 */
	public boolean isUsrResolveIssue() {
		return usrResolveIssue;
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
	public void setUsrChangeOwner(boolean b) {
		usrChangeOwner = b;
	}

	/**
	 * @param b
	 */
	public void setUsrCloseIssue(boolean b) {
		usrCloseIssue = b;
	}

	/**
	 * @param b
	 */
	public void setUsrCommentIssue(boolean b) {
		usrCommentIssue = b;
	}

	/**
	 * @param b
	 */
	public void setUsrModifyIssue(boolean b) {
		usrModifyIssue = b;
	}

	/**
	 * @param b
	 */
	public void setUsrRejectIssue(boolean b) {
		usrRejectIssue = b;
	}

	
	/**
	 * @param b
	 */
	public void setUsrResolveIssue(boolean b) {
		usrResolveIssue = b;
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
	public boolean isActionavailable() {
		return actionavailable;
	}

	/**
	 * @param b
	 */
	public void setActionavailable(boolean b) {
		actionavailable = b;
	}

	/**
	 * @return
	 */
	public boolean isUsrWithDraw() {
		return usrWithDraw;
	}

	/**
	 * @param b
	 */
	public void setUsrWithDraw(boolean b) {
		usrWithDraw = b;
	}

	
	/**
	 * @return
	 */
	public boolean isUsrSubscribe() {
		return usrSubscribe;
	}

	/**
	 * @param b
	 */
	public void setUsrSubscribe(boolean b) {
		usrSubscribe = b;
	}

	/**
	 * @return
	 */
	public boolean isUsrViewIssue() {
		return usrViewIssue;
	}

	/**
	 * @param b
	 */
	public void setUsrViewIssue(boolean b) {
		usrViewIssue = b;
	}

}//end of class
