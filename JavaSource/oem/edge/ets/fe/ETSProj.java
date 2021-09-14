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

import java.util.*;
import oem.edge.common.SysLog;
//import com.ibm.as400.webaccess.common.*;
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

public class ETSProj {
  public final static String Copyright = "(C) Copyright IBM Corp.  2002-2004";
  public static final String VERSION = "1.14";

    private String projectid;
	private String name;
	private long startdate;
	private long enddate;
	private String message;
	private String description;
	private String lotusid;
	private String project_or_proposal;
	private String decaf_project_name;
	private String related_id;

	private String parent_id;
	private String company;
	private String pmo_project_id;
	private String show_issue_owner;
	private String project_status;
	private boolean projBladeType;

	private String DeliveryTeam;
	private String Geography;
	private String Industry;

	private boolean IsITAR;
	private String m_strProjectType;

	private String IsPrivate;
	private String process;

	public ETSProj() {
		projectid = "";
		name = "";
		startdate = new Date().getTime();
		enddate = new Date().getTime();
		message = "";
		description = "";
		lotusid = "";
		project_or_proposal = "";
		decaf_project_name = "";
		related_id = "";
		parent_id = "";
		company = "";
		pmo_project_id = "";
		show_issue_owner = "";
		project_status = "";
		projBladeType = false;

		DeliveryTeam = "";
		Industry = "";
		Geography = "";
		IsITAR = false;
		IsPrivate="";
		process="";
	}

	public void setProjectId(String id) {
		this.projectid = id;
	}

	public String getProjectId() {
		return projectid;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}

	public void setStartDate(long t) {
		this.startdate = t;
	}
	public void setStartDate(java.sql.Timestamp t) {
		this.startdate = t.getTime();
	}
	public long getStartDate() {
		return startdate;
	}

	public void setEndDate(long t) {
		this.enddate = t;
	}
	public void setEndDate(java.sql.Timestamp t) {
		this.enddate = t.getTime();
	}
	public long getEndDate() {
		return enddate;
	}

	public void setMessage(String msg) {
		this.message = msg;
	}
	public String getMessage() {
		return message;
	}

	public void setDescription(String desc) {
		this.description = desc;
	}
	public String getDescription() {
		return description;
	}

	public void setProjectOrProposal(String sInStr) {
		this.project_or_proposal = sInStr;
	}
	public String getProjectOrProposal() {
		return project_or_proposal;
	}

	public void setDecafProject(String sInStr) {
		this.decaf_project_name = sInStr;
	}
	public String getDecafProject() {
		return decaf_project_name;
	}

	public void setLotusProject(String sInStr) {
		this.lotusid = sInStr;
	}
	public String getLotusProject() {
		return lotusid;
	}

	public void setRelatedProjectId(String sInStr) {
		this.related_id = sInStr;
	}
	public String getRelatedProjectId() {
		return related_id;
	}

	public List getRolesPrivs() {
		List l;
		try {
			l = Arrays.asList(ETSDatabaseManager.getRolesPrivs(projectid).toArray());
		}
		catch (Exception e) {
			SysLog.log(SysLog.ERR,this,e);
			l = new ArrayList();
		}
		return l;
	}

	public boolean doesRoleHavePriv(int roleid, int privid) {
		boolean hasIt = false;
		try {
			hasIt = ETSDatabaseManager.doesRoleHavePriv(roleid, privid);
		// works because  roleid is unique across projects  (??)
		}
		catch (Exception e) {
			SysLog.log(SysLog.ERR,this,e);
		}
		return hasIt;
	}


	/**
	 * @return
	 */
	public String getCompany() {
		return this.company;
	}

	/**
	 * @return
	 */
	public String getParent_id() {
		return this.parent_id;
	}

	/**
	 * @return
	 */
	public String getPmo_project_id() {
		return this.pmo_project_id;
	}

	/**
	 * @param string
	 */
	public void setCompany(String string) {
		this.company = string;
	}

	/**
	 * @param string
	 */
	public void setParent_id(String string) {
		this.parent_id = string;
	}

	/**
	 * @param string
	 */
	public void setPmo_project_id(String string) {
		this.pmo_project_id = string;
	}

	/**
	 * @return
	 */
	public String getProject_status() {
		return this.project_status;
	}

	/**
	 * @return
	 */
	public String getShow_issue_owner() {
		return this.show_issue_owner;
	}

	/**
	 * @param string
	 */
	public void setProject_status(String string) {
		this.project_status = string;
	}

	/**
	 * @param string
	 */
	public void setShow_issue_owner(String string) {
		this.show_issue_owner = string;
	}

	/**
	 * @return
	 */
	public boolean isProjBladeType() {
		return projBladeType;
	}

	/**
	 * @param b
	 */
	public void setProjBladeType(boolean b) {
		this.projBladeType = b;
	}

	/**
	 * @return
	 */
	public String getDeliveryTeam() {
		return this.DeliveryTeam;
	}

	/**
	 * @return
	 */
	public String getGeography() {
		return this.Geography;
	}

	/**
	 * @return
	 */
	public String getIndustry() {
		return this.Industry;
	}

	/**
	 * @return
	 */
	public boolean isITAR() {
		return this.IsITAR;
	}

	/**
	 * @param string
	 */
	public void setDeliveryTeam(String string) {
		this.DeliveryTeam = string;
	}

	/**
	 * @param string
	 */
	public void setGeography(String string) {
		this.Geography = string;
	}

	/**
	 * @param string
	 */
	public void setIndustry(String string) {
		this.Industry = string;
	}

	/**
	 * @param b
	 */
	public void setITAR(boolean b) {
		this.IsITAR = b;
	}

	/**
	 * @return
	 */
	public String getProjectType() {
		return m_strProjectType;
	}

	/**
	 * @param strProjectType
	 */
	public void setProjectType(String strProjectType) {
		m_strProjectType = strProjectType;
	}

	/**
	 * @return
	 */
	public String getIsPrivate() {
		return this.IsPrivate;
	}

	/**
	 * @param string
	 */
	public void setIsPrivate(String string) {
		this.IsPrivate = string;
	}

	/**
	 * @return Returns the process.
	 */
	public String getProcess() {
		return process;
	}
	/**
	 * @param process The process to set.
	 */
	public void setProcess(String process) {
		this.process = process;
	}
}

