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
 * Created on Nov 10, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.setmet;

import oem.edge.ets.fe.workflow.core.WorkflowObject;

/**
 * @author ryazuddin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SetMetHistoryObject extends WorkflowObject {

	private String dateModified = null;
	private String modifiedField = null;
	private String previousValue = null;
	private String newValue = null;
	private String timeModified = null;
	private String author = null;
	private String comments = null;
	
	
	/**
	 * @return Returns the author.
	 */
	public String getAuthor() {
		return author;
	}
	/**
	 * @param author The author to set.
	 */
	public void setAuthor(String author) {
		this.author = author;
	}
	/**
	 * @return Returns the comments.
	 */
	public String getComments() {
		return comments;
	}
	/**
	 * @param comments The comments to set.
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}
	/**
	 * @return Returns the dateModified.
	 */
	public String getDateModified() {
		return dateModified;
	}
	/**
	 * @param dateModified The dateModified to set.
	 */
	public void setDateModified(String dateModified) {
		this.dateModified = dateModified;
	}
	/**
	 * @return Returns the modifiedField.
	 */
	public String getModifiedField() {
		return modifiedField;
	}
	/**
	 * @param modifiedField The modifiedField to set.
	 */
	public void setModifiedField(String modifiedField) {
		this.modifiedField = modifiedField;
	}
	/**
	 * @return Returns the newValue.
	 */
	public String getNewValue() {
		return newValue;
	}
	/**
	 * @param newValue The newValue to set.
	 */
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
	/**
	 * @return Returns the previousValue.
	 */
	public String getPreviousValue() {
		return previousValue;
	}
	/**
	 * @param previousValue The previousValue to set.
	 */
	public void setPreviousValue(String previousValue) {
		this.previousValue = previousValue;
	}
	/**
	 * @return Returns the timeModified.
	 */
	public String getTimeModified() {
		return timeModified;
	}
	/**
	 * @param timeModified The timeModified to set.
	 */
	public void setTimeModified(String timeModified) {
		this.timeModified = timeModified;
	}
}
