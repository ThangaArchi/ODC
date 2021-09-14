/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     © Copyright IBM Corp. 2001-2006                                     */
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


package oem.edge.ets.fe.workflow.sa.summary;

/**
 * Class       : CommonData
 * Package     : oem.edge.ets.fe.workflow.sa.summary
 * Description : 
 * Date		   : Mar 24, 2007
 * 
 * @author     : Pradyumna Achar
 */
public class CommonData {

	private String client = "NA";
	private String segment = "NA";
	private String oldScore = "NA";
	private String newScore = "NA";
	private String statusBit = "NA";
	private String change = "NA";
	private String clientAttendees = "NA";
	private String oldMonth = "Previous Month";
	private String newMonth = "Current Month";	
	
	
	public String getChange() {
		return change;
	}
	public void setChange(String change) {
		this.change = change;
	}
	public String getClient() {
		return client;
	}
	public void setClient(String client) {
		this.client = client;
	}
	public String getClientAttendees() {
		return clientAttendees;
	}
	public void setClientAttendees(String clientAttendees) {
		this.clientAttendees = clientAttendees;
	}
	public String getNewScore() {
		return newScore;
	}
	public void setNewScore(String newScore) {
		this.newScore = newScore;
	}
	public String getOldScore() {
		return oldScore;
	}
	public void setOldScore(String oldScore) {
		this.oldScore = oldScore;
	}
	public String getSegment() {
		return segment;
	}
	public void setSegment(String segment) {
		this.segment = segment;
	}
	public String getStatusBit() {
		return statusBit;
	}
	public void setStatusBit(String statusBit) {
		this.statusBit = statusBit;
	}
	public String getNewMonth() {
		return newMonth;
	}
	public void setNewMonth(String newMonth) {
		this.newMonth = newMonth;
	}
	public String getOldMonth() {
		return oldMonth;
	}
	public void setOldMonth(String oldMonth) {
		this.oldMonth = oldMonth;
	}
}

