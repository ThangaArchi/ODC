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
 * Created on Nov 22, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.setmet;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import oem.edge.ets.fe.workflow.core.WorkflowForm;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * @author ryazuddin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WorkflowMatrixForm extends WorkflowForm{
	
	private ArrayList workspaceslist = null;
	private ArrayList brandlist = null;
	private ArrayList processlist = null;
	private ArrayList businesssectorlist = null;
	private ArrayList scesectorlist = null;
	private String reportid = null;
	private String[] chkboxcolumn =null;
    public void reset()
    {
    	chkboxcolumn = new String[]{
    			"WF_DEF.WF_NAME",
    			"WF_DEF.WF_CURR_STAGE_NAME",
    			"ETS_CALENDAR.SCHEDULE_DATE",
    			"WF_STAGE_DOCUMENT_SETMET.OVERAL_SCORE",
    			"WF_STAGE_IDENTIFY_SETMET.NSI_RATING",
    			"WF_ISSUE.ISSUE_TITLE",
    			"WF_ISSUE.ISSUE_ID",
    			"WF_ISSUE_OWNER.OWNER_ID",
    			"WF_ISSUE.TARGET_DATE",
    			"WF_ISSUE.STATUS",
    			"WF_ISSUE.ISSUE_DESC",
				"WF_STAGE_IDENTIFY_SETMET.EXEC_SPONSOR"
    	};
    }
	/**
	 * @return Returns the reportid.
	 */
	public String getReportid() {
		return reportid;
	}
	/**
	 * @param reportid The reportid to set.
	 */
	public void setReportid(String reportid) {
		this.reportid = reportid;
	}
	/**
	 * @return Returns the chkboxcolumn.
	 */
	public String[] getChkboxcolumn() {
		return chkboxcolumn;
	}
	/**
	 * @param chkboxcolumn The chkboxcolumn to set.
	 */
	public void setChkboxcolumn(String[] chkboxcolumn) {
		this.chkboxcolumn = chkboxcolumn;
	}
	/**
	 * @return Returns the brandlist.
	 */
	public ArrayList getBrandlist() {
		return brandlist;
	}
	/**
	 * @param brandlist The brandlist to set.
	 */
	public void setBrandlist(ArrayList brandlist) {
		this.brandlist = brandlist;
	}
	/**
	 * @return Returns the businesssectorlist.
	 */
	public ArrayList getBusinesssectorlist() {
		return businesssectorlist;
	}
	/**
	 * @param businesssectorlist The businesssectorlist to set.
	 */
	public void setBusinesssectorlist(ArrayList businesssectorlist) {
		this.businesssectorlist = businesssectorlist;
	}
	/**
	 * @return Returns the processlist.
	 */
	public ArrayList getProcesslist() {
		return processlist;
	}
	/**
	 * @param processlist The processlist to set.
	 */
	public void setProcesslist(ArrayList processlist) {
		this.processlist = processlist;
	}
	/**
	 * @return Returns the scesectorlist.
	 */
	public ArrayList getScesectorlist() {
		return scesectorlist;
	}
	/**
	 * @param scesectorlist The scesectorlist to set.
	 */
	public void setScesectorlist(ArrayList scesectorlist) {
		this.scesectorlist = scesectorlist;
	}
	/**
	 * @return Returns the workspaceslist.
	 */
	public ArrayList getWorkspaceslist() {
		return workspaceslist;
	}
	/**
	 * @param workspaceslist The workspaceslist to set.
	 */
	public void setWorkspaceslist(ArrayList workspaceslist) {
		this.workspaceslist = workspaceslist;
	}
	public WorkflowMatrixForm(){
		workspaceslist = new ArrayList();
		brandlist = new ArrayList();
		processlist = new ArrayList();
		businesssectorlist = new ArrayList();
		scesectorlist = new ArrayList();
		workflowObject = new WorkflowMatrixObject();
		reset();
	}
/*	public ActionErrors validate( 
		      ActionMapping mapping, HttpServletRequest request ) {
		      ActionErrors errors = new ActionErrors();
		      
		      if( getWorkspaceslist() == null || getWorkspaceslist().isEmpty() ) {
		        errors.add("Workspaceslist",new ActionMessage("Workspaceslist required"));
		      }if( getProcesslist() == null || getProcesslist().isEmpty() ) {
		        errors.add("processlist",new ActionMessage("processlist required"));
		      }if( getScesectorlist() == null || getScesectorlist().isEmpty() ) {
		        errors.add("scesectorlist",new ActionMessage("scesectorlist required"));
		      }if( getBusinesssectorlist() == null || getBusinesssectorlist().isEmpty() ) {
		        errors.add("businesssectorlist",new ActionMessage("businesssectorlist required"));
		      }if( getBrandlist() == null || getBrandlist().isEmpty() ) {
		        errors.add("brandlist",new ActionMessage("brandlist required"));
		      }
		      return errors;
		  }*/

}
