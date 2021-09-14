
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
 * Created on Feb 21, 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.setmet.validate;

import oem.edge.ets.fe.workflow.stage.ValidateStageObject;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ValidateObject extends ValidateStageObject {

	private String reporttitle = null;
	private String nxtDate     = null;
	private String summary     = null;
	private String comment	   = null;
	private String rating      = null;
	
	
	
	
	/**
	 * @return Returns the rating.
	 */
	public String getRating() {
		return rating;
	}
	/**
	 * @param rating The rating to set.
	 */
	public void setRating(String rating) {
		this.rating = rating;
	}
	/**
	 * @return Returns the comment.
	 */
	public String getComment() {
		return comment;
	}
	/**
	 * @param comment The comment to set.
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	/**
	 * @return Returns the nxtDate.
	 */
	public String getNxtDate() {
		return nxtDate;
	}
	/**
	 * @param nxtDate The nxtDate to set.
	 */
	public void setNxtDate(String nxtDate) {
		this.nxtDate = nxtDate;
	}
	/**
	 * @return Returns the reporttitle.
	 */
	public String getReporttitle() {
		return reporttitle;
	}
	/**
	 * @param reporttitle The reporttitle to set.
	 */
	public void setReporttitle(String reporttitle) {
		this.reporttitle = reporttitle;
	}
	/**
	 * @return Returns the summary.
	 */
	public String getSummary() {
		return summary;
	}
	/**
	 * @param summary The summary to set.
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}
}
