/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005                                          */
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
package oem.edge.ets.fe.acmgt.model;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class UserIccStatusModel {
	
	public static final String VERSION = "1.8";
	
	private int amtuidcount;
	private int amtemailcount;
	private int decafidcount;
	
	private boolean bEntitled;
	private boolean bHasPendEtitlement;
	private boolean bReqstEntitlement;
	
	

	/**
	 * 
	 */
	public UserIccStatusModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return
	 */
	public int getAmtemailcount() {
		return amtemailcount;
	}

	/**
	 * @return
	 */
	public int getAmtuidcount() {
		return amtuidcount;
	}

	
	/**
	 * @param i
	 */
	public void setAmtemailcount(int i) {
		amtemailcount = i;
	}

	/**
	 * @param i
	 */
	public void setAmtuidcount(int i) {
		amtuidcount = i;
	}

	

	/**
	 * @return
	 */
	public boolean isBEntitled() {
		return bEntitled;
	}

	/**
	 * @return
	 */
	public boolean isBHasPendEtitlement() {
		return bHasPendEtitlement;
	}

	/**
	 * @param b
	 */
	public void setBEntitled(boolean b) {
		bEntitled = b;
	}

	/**
	 * @param b
	 */
	public void setBHasPendEtitlement(boolean b) {
		bHasPendEtitlement = b;
	}

	/**
	 * @return
	 */
	public boolean isBReqstEntitlement() {
		return bReqstEntitlement;
	}

	/**
	 * @param b
	 */
	public void setBReqstEntitlement(boolean b) {
		bReqstEntitlement = b;
	}

	/**
	 * @return
	 */
	public int getDecafidcount() {
		return decafidcount;
	}

	/**
	 * @param i
	 */
	public void setDecafidcount(int i) {
		decafidcount = i;
	}

}
