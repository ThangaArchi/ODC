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
 * @author 		: v2sathis
 * Created on 		: Apr 27, 2004 
 */
package oem.edge.ets.fe.pmo;

import java.sql.Timestamp;

import oem.edge.ets.fe.documents.common.StringUtil;

public class ETSPMOffice {
    
    private String PMOID = "";
	private String PMO_Project_ID = "";
	private String PMO_Parent_ID = "";
	private String Name = "";
	private String Type = "";
	private int Reference = 0;
	private int Rank = 0;
	private int Priority = 0;
	private String AssignType = "";
	private String Calendar = "";
	private String CalendarRank = "";
	private String Currency = "";
	private String CurrencyRank = "";
	private String Published = "";
	private Timestamp EstimatedStartDate = null;
	private Timestamp EstimatedFinishDate = null;
	private String State = "";
	private String ChangeBriefDesc = "";
	private Timestamp StartDate = null;
    private Timestamp FinishDate = null;
	private String Duration = "";
	private String Work = "";
	private String PercentComplete = "";
	private String RemainingWork = "";
	private String EETC = "";
	private String EffortSpent = "";
	private String Constraint = "";
	private Timestamp ConstraintDate = null;
	private Timestamp LastTimestamp = null;

	private String isReportable = "";
	private Timestamp BaseFinish = null;
	private Timestamp CurrFinish = null;
	private String CurrFinishType = ""; 

	public ETSPMOffice() {
		super();
	}
    

	/**
	 * @return
	 */
	public String getAssignType() {
		return this.AssignType;
	}

	/**
	 * @return
	 */
	public String getCalendar() {
		return this.Calendar;
	}

	/**
	 * @return
	 */
	public String getCalendarRank() {
		return this.CalendarRank;
	}

	/**
	 * @return
	 */
	public String getChangeBriefDesc() {
		return this.ChangeBriefDesc;
	}

	/**
	 * @return
	 */
	public String getConstraint() {
		return this.Constraint;
	}

	/**
	 * @return
	 */
	public Timestamp getConstraintDate() {
		return this.ConstraintDate;
	}

	/**
	 * @return
	 */
	public String getCurrency() {
		return this.Currency;
	}

	/**
	 * @return
	 */
	public String getCurrencyRank() {
		return this.CurrencyRank;
	}

	/**
	 * @return
	 */
	public String getDuration() {
		return this.Duration;
	}

	/**
	 * @return
	 */
	public String getEETC() {
		return this.EETC;
	}

	/**
	 * @return
	 */
	public String getEffortSpent() {
		return this.EffortSpent;
	}

	/**
	 * @return
	 */
	public Timestamp getEstimatedFinishDate() {
		return this.EstimatedFinishDate;
	}

	/**
	 * @return
	 */
	public Timestamp getEstimatedStartDate() {
		return this.EstimatedStartDate;
	}

	/**
	 * @return
	 */
	public String getFormattedFinishDate() {
		if (this.FinishDate == null) {
			return StringUtil.EMPTY_STRING;
		}
		
		return StringUtil.formatDate(this.FinishDate);
	}

	/**
	 * @return
	 */
	public Timestamp getFinishDate() {
		return this.FinishDate;
	}

	/**
	 * @return
	 */
	public Timestamp getLastTimestamp() {
		return this.LastTimestamp;
	}

	/**
	 * @return
	 */
	public String getName() {
		return this.Name;
	}

	/**
	 * @return
	 */
	public String getPercentComplete() {
		return this.PercentComplete;
	}

	/**
	 * @return
	 */
	public String getPMO_Parent_ID() {
		return this.PMO_Parent_ID;
	}

	/**
	 * @return
	 */
	public String getPMO_Project_ID() {
		return this.PMO_Project_ID;
	}

	/**
	 * @return
	 */
	public String getPMOID() {
		return this.PMOID;
	}

	/**
	 * @return
	 */
	public int getPriority() {
		return this.Priority;
	}

	/**
	 * @return
	 */
	public String getPublished() {
		return this.Published;
	}

	/**
	 * @return
	 */
	public int getRank() {
		return this.Rank;
	}

	/**
	 * @return
	 */
	public int getReference() {
		return this.Reference;
	}

	/**
	 * @return
	 */
	public String getRemainingWork() {
		return this.RemainingWork;
	}

	/**
	 * @return
	 */
	public Timestamp getStartDate() {
		return this.StartDate;
	}

	/**
	 * @return
	 */
	public String getState() {
		return this.State;
	}

	/**
	 * @return
	 */
	public String getType() {
		return this.Type;
	}

	/**
	 * @return
	 */
	public String getWork() {
		return this.Work;
	}

	/**
	 * @param string
	 */
	public void setAssignType(String string) {
		this.AssignType = string;
	}

	/**
	 * @param string
	 */
	public void setCalendar(String string) {
		this.Calendar = string;
	}

	/**
	 * @param string
	 */
	public void setCalendarRank(String string) {
		this.CalendarRank = string;
	}

	/**
	 * @param string
	 */
	public void setChangeBriefDesc(String string) {
		this.ChangeBriefDesc = string;
	}

	/**
	 * @param string
	 */
	public void setConstraint(String string) {
		this.Constraint = string;
	}

	/**
	 * @param timestamp
	 */
	public void setConstraintDate(Timestamp timestamp) {
		this.ConstraintDate = timestamp;
	}

	/**
	 * @param string
	 */
	public void setCurrency(String string) {
		this.Currency = string;
	}

	/**
	 * @param string
	 */
	public void setCurrencyRank(String string) {
		this.CurrencyRank = string;
	}

	/**
	 * @param string
	 */
	public void setDuration(String string) {
		this.Duration = string;
	}

	/**
	 * @param string
	 */
	public void setEETC(String string) {
		this.EETC = string;
	}

	/**
	 * @param string
	 */
	public void setEffortSpent(String string) {
		this.EffortSpent = string;
	}

	/**
	 * @param timestamp
	 */
	public void setEstimatedFinishDate(Timestamp timestamp) {
		this.EstimatedFinishDate = timestamp;
	}

	/**
	 * @param timestamp
	 */
	public void setEstimatedStartDate(Timestamp timestamp) {
		this.EstimatedStartDate = timestamp;
	}

	/**
	 * @param timestamp
	 */
	public void setFinishDate(Timestamp timestamp) {
		this.FinishDate = timestamp;
	}

	/**
	 * @param timestamp
	 */
	public void setLastTimestamp(Timestamp timestamp) {
		this.LastTimestamp = timestamp;
	}

	/**
	 * @param string
	 */
	public void setName(String string) {
		this.Name = string;
	}

	/**
	 * @param string
	 */
	public void setPercentComplete(String string) {
		this.PercentComplete = string;
	}

	/**
	 * @param string
	 */
	public void setPMO_Parent_ID(String string) {
		this.PMO_Parent_ID = string;
	}

	/**
	 * @param string
	 */
	public void setPMO_Project_ID(String string) {
		this.PMO_Project_ID = string;
	}

	/**
	 * @param string
	 */
	public void setPMOID(String string) {
		this.PMOID = string;
	}

	/**
	 * @param i
	 */
	public void setPriority(int i) {
		this.Priority = i;
	}

	/**
	 * @param string
	 */
	public void setPublished(String string) {
		this.Published = string;
	}

	/**
	 * @param i
	 */
	public void setRank(int i) {
		this.Rank = i;
	}

	/**
	 * @param i
	 */
	public void setReference(int i) {
		this.Reference = i;
	}

	/**
	 * @param string
	 */
	public void setRemainingWork(String string) {
		this.RemainingWork = string;
	}

	/**
	 * @param timestamp
	 */
	public void setStartDate(Timestamp timestamp) {
		this.StartDate = timestamp;
	}

	/**
	 * @param string
	 */
	public void setState(String string) {
		this.State = string;
	}

	/**
	 * @param string
	 */
	public void setType(String string) {
		this.Type = string;
	}

	/**
	 * @param string
	 */
	public void setWork(String string) {
		this.Work = string;
	}

	/**
	 * @return
	 */
	public Timestamp getBaseFinish() {
		return this.BaseFinish;
	}

	/**
	 * @return
	 */
	public Timestamp getCurrFinish() {
		return this.CurrFinish;
	}

	/**
	 * @return
	 */
	public String getCurrFinishType() {
		return this.CurrFinishType;
	}

	/**
	 * @return
	 */
	public String getIsReportable() {
		return this.isReportable;
	}

	/**
	 * @param timestamp
	 */
	public void setBaseFinish(Timestamp timestamp) {
		this.BaseFinish = timestamp;
	}

	/**
	 * @param timestamp
	 */
	public void setCurrFinish(Timestamp timestamp) {
		this.CurrFinish = timestamp;
	}

	/**
	 * @param string
	 */
	public void setCurrFinishType(String string) {
		this.CurrFinishType = string;
	}

	/**
	 * @param string
	 */
	public void setIsReportable(String string) {
		this.isReportable = string;
	}

}
