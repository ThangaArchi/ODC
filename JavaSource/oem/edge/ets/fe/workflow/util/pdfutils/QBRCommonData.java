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
 * Class       : QBRCommonData
 * Package     : oem.edge.ets.fe.workflow.util.pdfutils
 * Description : 
 * Date		   : Mar 1, 2007
 * 
 * @author     : Pradyumna Achar
 */
public class QBRCommonData {
	private String qbrName = "";
	private String quarter = "";
	private String year = "";
	private String client = "";
	private String areaRated = "";
	private String segment = "";
	private String ratingPeriod = "";
	private String currentScore = "";
	private String oldScore = "";
	private String rank = "";
	private String change = "";
	private String overall_comments = "";
	private String client_attendees = "";
	private String reportTitle = "";
	
	
	public String getReportTitle() {
		return reportTitle;
	}
	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}
	public String getClient_attendees() {
		return client_attendees;
	}
	public void setClient_attendees(String client_attendees) {
		this.client_attendees = client_attendees;
	}
	public String getAreaRated() {
		return areaRated;
	}
	public void setAreaRated(String areaRated) {
		this.areaRated = areaRated;
	}
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
	public String getCurrentScore() {
		return currentScore;
	}
	public void setCurrentScore(String currentScore) {
		this.currentScore = currentScore;
	}
	public String getOldScore() {
		return oldScore;
	}
	public void setOldScore(String oldScore) {
		this.oldScore = oldScore;
	}
	public String getOverall_comments() {
		return overall_comments;
	}
	public void setOverall_comments(String overall_comments) {
		this.overall_comments = overall_comments;
	}
	public String getQbrName() {
		return qbrName;
	}
	public void setQbrName(String qbrName) {
		this.qbrName = qbrName;
	}
	public String getQuarter() {
		return quarter;
	}
	public void setQuarter(String quarter) {
		this.quarter = quarter;
	}
	public String getRank() {
		return rank;
	}
	public void setRank(String rank) {
		this.rank = rank;
	}
	public String getRatingPeriod() {
		return ratingPeriod;
	}
	public void setRatingPeriod(String ratingPeriod) {
		this.ratingPeriod = ratingPeriod;
	}
	public String getSegment() {
		return segment;
	}
	public void setSegment(String segment) {
		this.segment = segment;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
}

