/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2006                                     */
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


package oem.edge.ets.fe.workflow.setmet.summary.issues;

//TODO: 00 Not yet uploaded in CMVC
/**
 * Class       : Table1Row
 * Package     : oem.edge.ets.fe.workflow.setmet.summary.issues
 * Description : 
 * Date		   : Dec 7, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class Table1Row {
	private String itemNumber  =null;
	private String actionDetails = null;
	
	/**
	 * @return Returns the actionDetails.
	 */
	public String getActionDetails() {
		return actionDetails;
	}
	/**
	 * @param actionDetails The actionDetails to set.
	 */
	public void setActionDetails(String actionDetails) {
		this.actionDetails = actionDetails;
	}
	/**
	 * @return Returns the itemNumber.
	 */
	public String getItemNumber() {
		return itemNumber;
	}
	/**
	 * @param itemNumber The itemNumber to set.
	 */
	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}
}

