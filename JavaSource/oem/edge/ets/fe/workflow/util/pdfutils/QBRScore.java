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


package oem.edge.ets.fe.workflow.util.pdfutils;

/**
 * Class       : QBRScore
 * Package     : oem.edge.ets.fe.workflow.util.pdfutils
 * Description : 
 * Date		   : Mar 1, 2007
 * 
 * @author     : Pradyumna Achar
 */
public class QBRScore {
	private String clientAttribute = "";
	private String oldScorePercent = "";
	private String newScorePercent = "";
	private String commentsProvided = "";
	
	public String getClientAttribute() {
		return clientAttribute;
	}
	public void setClientAttribute(String clientAttribute) {
		this.clientAttribute = clientAttribute;
	}
	public String getCommentsProvided() {
		return commentsProvided;
	}
	public void setCommentsProvided(String commentsProvided) {
		this.commentsProvided = commentsProvided;
	}
	public String getNewScorePercent() {
		return newScorePercent;
	}
	public void setNewScorePercent(String newScorePercent) {
		this.newScorePercent = newScorePercent;
	}
	public String getOldScorePercent() {
		return oldScorePercent;
	}
	public void setOldScorePercent(String oldScorePercent) {
		this.oldScorePercent = oldScorePercent;
	}
}

