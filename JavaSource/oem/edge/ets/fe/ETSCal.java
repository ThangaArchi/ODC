/*   ------------------------------------------------------------------          */
/*   IBM                                                                                     */
/*                                                                                               */
/*   OCO Source Materials                                                          */
/*                                                                                               */
/*   Product(s): ICC/PROFIT                                                       */
/*                                                                                               */
/*   (C)Copyright IBM Corp. 2002,2003 		              */
/*                                                                                               */
/*   The source code for this program is not published or otherwise */
/*   divested of its trade secrets, irrespective of what has been        */
/*   deposited with the US Copyright Office.                                  */
/*   ------------------------------------------------------------------           */

package oem.edge.ets.fe;

import java.sql.Timestamp;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004                                     */
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

/**
 * @author v2sathis
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ETSCal {
   
   public final static String Copyright = "(C) Copyright IBM Corp.  2002, 2003";
   private static final String CLASS_VERSION = "1.11";

	protected String sProjectId;
	protected String sCalendarId;
	protected String sCalType;
	protected Timestamp sScheduleDate;
	protected String sScheduleBy;
	protected Timestamp sStartTime;
	protected int iDuration;
	protected String sSubject;
	protected String sDescription;
	protected String sInviteesID;
	protected String sCCList;
	protected String sWebFlag;
	protected String sCancelFlag;
	protected String sEmailFlag;
	protected String sIBMOnly;
    protected String sCallIn;
    protected String sPass;

    protected String sRepeatType;
    protected String sRepeatID;
    protected Timestamp sRepeatStart;
    protected Timestamp sRepeatEnd;
    
    protected String NotifyType;
    
    protected int FolderId;

	/**
	 * Returns the iDuration.
	 * @return int
	 */
	public int getIDuration() {
		return iDuration;
	}

	/**
	 * Returns the sCalendarId.
	 * @return String
	 */
	public String getSCalendarId() {
		return sCalendarId;
	}

	/**
	 * Returns the sCalType.
	 * @return String
	 */
	public String getSCalType() {
		return sCalType;
	}

	/**
	 * Returns the sCancelFlag.
	 * @return String
	 */
	public String getSCancelFlag() {
		return sCancelFlag;
	}

	/**
	 * Returns the sCCList.
	 * @return String
	 */
	public String getSCCList() {
		return sCCList;
	}

	/**
	 * Returns the sDescription.
	 * @return String
	 */
	public String getSDescription() {
		return sDescription;
	}

	/**
	 * Returns the sEmailFlag.
	 * @return String
	 */
	public String getSEmailFlag() {
		return sEmailFlag;
	}

	/**
	 * Returns the sIBMOnly.
	 * @return String
	 */
	public String getSIBMOnly() {
		return sIBMOnly;
	}

	/**
	 * Returns the sInviteesID.
	 * @return String
	 */
	public String getSInviteesID() {
		return sInviteesID;
	}

	/**
	 * Returns the sProjectId.
	 * @return String
	 */
	public String getSProjectId() {
		return sProjectId;
	}

	/**
	 * Returns the sScheduleBy.
	 * @return String
	 */
	public String getSScheduleBy() {
		return sScheduleBy;
	}

	/**
	 * Returns the sScheduleDate.
	 * @return Timestamp
	 */
	public Timestamp getSScheduleDate() {
		return sScheduleDate;
	}

	/**
	 * Returns the sStartTime.
	 * @return Timestamp
	 */
	public Timestamp getSStartTime() {
		return sStartTime;
	}

	/**
	 * Returns the sSubject.
	 * @return String
	 */
	public String getSSubject() {
		return sSubject;
	}

	/**
	 * Returns the sWebFlag.
	 * @return String
	 */
	public String getSWebFlag() {
		return sWebFlag;
	}

	/**
	 * Sets the iDuration.
	 * @param iDuration The iDuration to set
	 */
	public void setIDuration(int iDuration) {
		this.iDuration = iDuration;
	}

	/**
	 * Sets the sCalendarId.
	 * @param sCalendarId The sCalendarId to set
	 */
	public void setSCalendarId(String sCalendarId) {
		this.sCalendarId = sCalendarId;
	}

	/**
	 * Sets the sCalType.
	 * @param sCalType The sCalType to set
	 */
	public void setSCalType(String sCalType) {
		this.sCalType = sCalType;
	}

	/**
	 * Sets the sCancelFlag.
	 * @param sCancelFlag The sCancelFlag to set
	 */
	public void setSCancelFlag(String sCancelFlag) {
		this.sCancelFlag = sCancelFlag;
	}

	/**
	 * Sets the sCCList.
	 * @param sCCList The sCCList to set
	 */
	public void setSCCList(String sCCList) {
		this.sCCList = sCCList;
	}

	/**
	 * Sets the sDescription.
	 * @param sDescription The sDescription to set
	 */
	public void setSDescription(String sDescription) {
		this.sDescription = sDescription;
	}

	/**
	 * Sets the sEmailFlag.
	 * @param sEmailFlag The sEmailFlag to set
	 */
	public void setSEmailFlag(String sEmailFlag) {
		this.sEmailFlag = sEmailFlag;
	}

	/**
	 * Sets the sIBMOnly.
	 * @param sIBMOnly The sIBMOnly to set
	 */
	public void setSIBMOnly(String sIBMOnly) {
		this.sIBMOnly = sIBMOnly;
	}

	/**
	 * Sets the sInviteesID.
	 * @param sInviteesID The sInviteesID to set
	 */
	public void setSInviteesID(String sInviteesID) {
		this.sInviteesID = sInviteesID;
	}

	/**
	 * Sets the sProjectId.
	 * @param sProjectId The sProjectId to set
	 */
	public void setSProjectId(String sProjectId) {
		this.sProjectId = sProjectId;
	}

	/**
	 * Sets the sScheduleBy.
	 * @param sScheduleBy The sScheduleBy to set
	 */
	public void setSScheduleBy(String sScheduleBy) {
		this.sScheduleBy = sScheduleBy;
	}

	/**
	 * Sets the sScheduleDate.
	 * @param sScheduleDate The sScheduleDate to set
	 */
	public void setSScheduleDate(Timestamp sScheduleDate) {
		this.sScheduleDate = sScheduleDate;
	}

	/**
	 * Sets the sStartTime.
	 * @param sStartTime The sStartTime to set
	 */
	public void setSStartTime(Timestamp sStartTime) {
		this.sStartTime = sStartTime;
	}

	/**
	 * Sets the sSubject.
	 * @param sSubject The sSubject to set
	 */
	public void setSSubject(String sSubject) {
		this.sSubject = sSubject;
	}

	/**
	 * Sets the sWebFlag.
	 * @param sWebFlag The sWebFlag to set
	 */
	public void setSWebFlag(String sWebFlag) {
		this.sWebFlag = sWebFlag;
	}

	public String getSCallIn() {
		return sCallIn;
	}

	public String getSPass() {
		return sPass;
	}

	public void setSCallIn(String sCallIn) {
		this.sCallIn = sCallIn;
	}

	public void setSPass(String sPass) {
		this.sPass = sPass;
	}

	/**
	 * @return
	 */
	public String getSRepeatType() {
		return this.sRepeatType;
	}

	/**
	 * @return
	 */
	public Timestamp getSRepeatEnd() {
		return this.sRepeatEnd;
	}

	/**
	 * @return
	 */
	public Timestamp getSRepeatStart() {
		return this.sRepeatStart;
	}

	/**
	 * @param string
	 */
	public void setSRepeatType(String string) {
		this.sRepeatType = string;
	}

	/**
	 * @param timestamp
	 */
	public void setSRepeatEnd(Timestamp timestamp) {
		this.sRepeatEnd = timestamp;
	}

	/**
	 * @param timestamp
	 */
	public void setSRepeatStart(Timestamp timestamp) {
		this.sRepeatStart = timestamp;
	}

	/**
	 * @return
	 */
	public String getSRepeatID() {
		return this.sRepeatID;
	}

	/**
	 * @param string
	 */
	public void setSRepeatID(String string) {
		this.sRepeatID = string;
	}

	/**
	 * @return
	 */
	public String getNotifyType() {
		return this.NotifyType;
	}

	/**
	 * @param string
	 */
	public void setNotifyType(String string) {
		this.NotifyType = string;
	}

	/**
	 * @return
	 */
	public int getFolderId() {
		return FolderId;
	}

	/**
	 * @param i
	 */
	public void setFolderId(int i) {
		FolderId = i;
	}

}
