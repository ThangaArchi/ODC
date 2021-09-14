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


package oem.edge.ets.fe.workflow.issue.edit;

import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import org.apache.commons.logging.Log;


/**
 * Class       : EditIssueVO
 * Package     : oem.edge.ets.fe.workflow.issue.edit
 * Description : 
 * Date		   : Oct 10, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class EditIssueVO extends WorkflowObject {
	
	private static Log logger = WorkflowLogger.getLogger(EditIssueVO.class);
	 private String[] month = null;

	 private String issueID =null;
	 
     private String userid = null;

     private String[] day = null;

     private String[] owners = null;

     private String[] year = null;

     private String[] type = null;
     
     private String[] category = null;

     private String title = null;

     private String[] focalPt = null;

     private String comment = null;

     private String desc = null;
     
     private String workflowID = null;
     

     /**
      * Get month
      * @return String[]
      */
     public String[] getMonth() {
         return month;
     }

     /**
      * Set month
      * @param <code>String[]</code>
      */
     public void setMonth(String[] m) {
         this.month = m;
     }

     /**
      * Get userid
      * @return String
      */
     public String getUserid() {
         return userid;
     }

     /**
      * Set userid
      * @param <code>String</code>
      */
     public void setUserid(String u) {
         this.userid = u;
     }

     /**
      * Get day
      * @return String[]
      */
     public String[] getDay() {
         return day;
     }

     /**
      * Set day
      * @param <code>String[]</code>
      */
     public void setDay(String[] d) {
         this.day = d;
     }

     /**
      * Get owners
      * @return String[]
      */
     public String[] getOwners() {
         return owners;
     }

     /**
      * Set owners
      * @param <code>String[]</code>
      */
     public void setOwners(String[] o) {
         this.owners = o;
     }

     /**
      * Get year
      * @return String[]
      */
     public String[] getYear() {
         return year;
     }

     /**
      * Set year
      * @param <code>String[]</code>
      */
     public void setYear(String[] y) {
         this.year = y;
     }

     /**
      * Get type
      * @return String[]
      */
     public String[] getType() {
         return type;
     }

     /**
      * Set type
      * @param <code>String[]</code>
      */
     public void setType(String[] t) {
         this.type = t;
     }

     /**
      * Get title
      * @return String
      */
     public String getTitle() {
         return title;
     }

     /**
      * Set title
      * @param <code>String</code>
      */
     public void setTitle(String t) {
         this.title = t;
     }

     /**
      * Get focalPt
      * @return String[]
      */
     public String[] getFocalPt() {
         return focalPt;
     }

     /**
      * Set focalPt
      * @param <code>String[]</code>
      */
     public void setFocalPt(String[] f) {
         this.focalPt = f;
     }

     /**
      * Get comment
      * @return String
      */
     public String getComment() {
         return comment;
     }

     /**
      * Set comment
      * @param <code>String</code>
      */
     public void setComment(String c) {
         this.comment = c;
     }

     /**
      * Get desc
      * @return String
      */
     public String getDesc() {
         return desc;
     }

     /**
      * Set desc
      * @param <code>String</code>
      */
     public void setDesc(String d) {
         this.desc = d;
     }
     public void reset() {
		month = null;

		userid = null;

		day = null;

		owners = null;

		year = null;

		type = null;

		title = null;

		focalPt = null;

		comment = null;

		desc = null;

	}
     
	/**
	 * @return Returns the issueID.
	 */
	public String getIssueID() {
		return issueID;
	}
	/**
	 * @param issueID The issueID to set.
	 */
	public void setIssueID(String issueID) {
		this.issueID = issueID;
	}
	/**
	 * @return Returns the workflowID.
	 */
	public String getWorkflowID() {
		return workflowID;
	}
	/**
	 * @param workflowID The workflowID to set.
	 */
	public void setWorkflowID(String workflowID) {
		this.workflowID = workflowID;
	}

	/**
	 * @return Returns the category.
	 */
	public String[] getCategory() {
		return category;
	}
	/**
	 * @param category The category to set.
	 */
	public void setCategory(String[] category) {
		this.category = category;
	}
}

