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

package oem.edge.ets.fe.workflow.eventdetailspopupwindow;

import javax.servlet.http.HttpServletRequest;

import oem.edge.ets.fe.workflow.core.WorkflowObject;

import org.apache.struts.action.ActionErrors;

import org.apache.struts.action.ActionMapping;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import org.apache.commons.logging.Log;


/**
 * Class       : WorkflowEventDetailsVO
 * Package     : oem.edge.ets.fe.workflow.eventdetailspopupwindow
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class WorkflowEventDetailsVO extends WorkflowObject

{

	private static Log logger = WorkflowLogger.getLogger(WorkflowEventDetailsVO.class);
    protected String[] month = null;

    protected String[] day = null;

    protected String[] ampm = null;

    protected String notifyEmail = null;

    protected String title = null;

    protected String[] teamMembers = null;

    protected String[] repeatsFor = null;

    protected String[] hour = null;

    protected String[] year = null;

    protected String[] min = null;

    protected String emailOption = null;

    protected String desc = null;

    protected String appliesToAll = null;

    private String workflowID = null;
    
	public String getWorkflowID() {
		return workflowID;
	}
	public void setWorkflowID(String workflowID) {
		this.workflowID = workflowID;
	}
	
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
     * Get ampm
     * @return String[]
     */
    public String[] getAmpm() {
        return ampm;
    }

    /**
     * Set ampm
     * @param <code>String[]</code>
     */
    public void setAmpm(String[] a) {
        this.ampm = a;
    }

    /**
     * Get notifyEmail
     * @return String
     */
    public String getNotifyEmail() {
        return notifyEmail;
    }

    /**
     * Set notifyEmail
     * @param <code>String</code>
     */
    public void setNotifyEmail(String n) {
        this.notifyEmail = n;
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
     * Get teamMembers
     * @return String[]
     */
    public String[] getTeamMembers() {
        return teamMembers;
    }

    /**
     * Set teamMembers
     * @param <code>String[]</code>
     */
    public void setTeamMembers(String[] t) {
        this.teamMembers = t;
    }

    /**
     * Get repeatsFor
     * @return String[]
     */
    public String[] getRepeatsFor() {
        return repeatsFor;
    }

    /**
     * Set repeatsFor
     * @param <code>String[]</code>
     */
    public void setRepeatsFor(String[] r) {
        this.repeatsFor = r;
    }

    /**
     * Get hour
     * @return String[]
     */
    public String[] getHour() {
        return hour;
    }

    /**
     * Set hour
     * @param <code>String[]</code>
     */
    public void setHour(String[] h) {
        this.hour = h;
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
     * Get min
     * @return String[]
     */
    public String[] getMin() {
        return min;
    }

    /**
     * Set min
     * @param <code>String[]</code>
     */
    public void setMin(String[] m) {
        this.min = m;
    }

    /**
     * Get emailOption
     * @return String
     */
    public String getEmailOption() {
        return emailOption;
    }

    /**
     * Set emailOption
     * @param <code>String</code>
     */
    public void setEmailOption(String e) {
        this.emailOption = e;
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

    /**
     * Get appliesToAll
     * @return String
     */
    public String getAppliesToAll() {
        return appliesToAll;
    }

    /**
     * Set appliesToAll
     * @param <code>String</code>
     */
    public void setAppliesToAll(String a) {
        this.appliesToAll = a;
    }

    public void reset() {
        month = null;
        day = null;
        ampm = null;
        notifyEmail = null;
        title = null;
        teamMembers = null;
        repeatsFor = null;
        hour = null;
        year = null;
        min = null;
        emailOption = null;
        desc = null;
        appliesToAll = null;
    }

    public ActionErrors validate(ActionMapping mapping,
            HttpServletRequest request) {

        ActionErrors errors = new ActionErrors();
        // Validate the fields in your form, adding
        // adding each error to this.errors as found, e.g.

        // if ((field == null) || (field.length() == 0)) {
        //   errors.add("field", new org.apache.struts.action.ActionError("error.field.required"));
        // }
        return errors;

    }
	
}
