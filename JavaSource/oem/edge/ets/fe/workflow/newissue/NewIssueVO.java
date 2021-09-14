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

package oem.edge.ets.fe.workflow.newissue;

import oem.edge.ets.fe.workflow.stage.DocumentStageObject;

import java.lang.String;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import org.apache.commons.logging.Log;


/**
 * Class       : NewIssueVO
 * Package     : oem.edge.ets.fe.workflow.newissue
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class NewIssueVO extends DocumentStageObject {

	private static Log logger = WorkflowLogger.getLogger(NewIssueVO.class);
	
	private String focalPointID = null;


	private String[] ownerID  =null;


	private String issueTypeID =null;


	private String desc= null;


	private String notifyOption = null;


	private String[] notificationList = null;

	private String title=null;

	 private String[] month = null;
	 private String[] day = null;
	 private String[] year = null;
	
	 private String issueCategory=null;
	 
	 /**
	 * @param focalPointID The focalPointID to set.
	 */
	public void setFocalPointID(String focalPointID) {
		this.focalPointID = focalPointID;
	}


	/**
	 * @return Returns the focalPointID.
	 */
	public String getFocalPointID() {
		return focalPointID;
	}


	/**
	 * @param ownerID The ownerID to set.
	 */
	public void setOwnerID(String[] ownerID) {
		this.ownerID = ownerID;
	}


	/**
	 * @return Returns the ownerID.
	 */
	public String[] getOwnerID() {
		return ownerID;
	}


	/**
	 * @param issueTypeID The issueTypeID to set.
	 */
	public void setIssueTypeID(String issueTypeID) {
		this.issueTypeID = issueTypeID;
	}


	/**
	 * @return Returns the issueTypeID.
	 */
	public String getIssueTypeID() {
		return issueTypeID;
	}


	/**
	 * @param desc The desc to set.
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}


	/**
	 * @return Returns the desc.
	 */
	public String getDesc() {
		return desc;
	}


	/**
	 * @param notifyOption The notifyOption to set.
	 */
	public void setNotifyOption(String notifyOption) {
		this.notifyOption = notifyOption;
	}


	/**
	 * @return Returns the notifyOption.
	 */
	public String getNotifyOption() {
		return notifyOption;
	}


	/**
	 * @param notificationList The notificationList to set.
	 */
	public void setNotificationList(String[] notificationList) {
		this.notificationList = notificationList;
	}


	/**
	 * @return Returns the notificationList.
	 */
	public String[] getNotificationList() {
		return notificationList;
	}

	

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return Returns the day.
	 */
	public String[] getDay() {
		return day;
	}
	/**
	 * @param day The day to set.
	 */
	public void setDay(String[] day) {
		this.day = day;
	}
	/**
	 * @return Returns the month.
	 */
	public String[] getMonth() {
		return month;
	}
	/**
	 * @param month The month to set.
	 */
	public void setMonth(String[] month) {
		this.month = month;
	}
	/**
	 * @return Returns the year.
	 */
	public String[] getYear() {
		return year;
	}
	/**
	 * @param year The year to set.
	 */
	public void setYear(String[] year) {
		this.year = year;
	}
	/**
	 * 
	 */
	public void reset() {
		
		 focalPointID = null;


		 ownerID  =null;


		 issueTypeID =null;


		 desc= null;


		 notifyOption = null;


		 notificationList = null;
		 
		 title=null;
		 month=null;day=null;year=null;
		 issueCategory = null;
		 System.out.println("Reset vo fields.");
		 
		
	}


	public String getWorkflowType() {
		return "SETMET";
	}

	
	public String getIssueCategory() {
		return issueCategory;
	}
	public void setIssueCategory(String issueCategory) {
		this.issueCategory= issueCategory;
	}



	
}
