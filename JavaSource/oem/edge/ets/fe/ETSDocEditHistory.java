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

/*
 * Created on Jul 27, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author amar
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ETSDocEditHistory extends ETSDetailedObj
{
	private int docId;
	private int seqNo;
	private String userId;
	private String action;
	private String actionDetails;
	private Timestamp lastTimestamp;
	private String timeStampString;
	/**
	 * @return Returns the lastTimeStamp.
	 */
	public Timestamp getLastTimestamp() {
		return lastTimestamp;
	}
	/**
	 * @param lastTimestamp The lastTimeStamp to set.
	 */
	public void setLastTimestamp(Timestamp lastTimestamp)
	{
		SimpleDateFormat formatter
	     = new SimpleDateFormat ("MMM d, yyyy hh:mm:ss");
				
		timeStampString = formatter.format(new Date(lastTimestamp.getTime()));
		
		this.lastTimestamp = lastTimestamp;
	}

	/**
	 * @return Returns the action.
	 */
	public String getAction() {
		return action;
	}
	/**
	 * @param action The action to set.
	 */
	public void setAction(String action) {
		this.action = action;
	}
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
	 * @return Returns the docId.
	 */
	public int getDocId() {
		return docId;
	}
	/**
	 * @param docId The docId to set.
	 */
	public void setDocId(int docId) {
		this.docId = docId;
	}
	/**
	 * @return Returns the seqNo.
	 */
	public int getSeqNo() {
		return seqNo;
	}
	/**
	 * @param seqNo The seqNo to set.
	 */
	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}
	/**
	 * @return Returns the userId.
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId The userId to set.
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
	/**
	 * @return Returns the timeStampString.
	 */
	public String getTimeStampString()
	{
		return timeStampString;
	}
	
}
