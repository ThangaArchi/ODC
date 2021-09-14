/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004                                          */
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

package oem.edge.ets.fe.ismgt.model;

import oem.edge.ets.fe.ismgt.middleware.ETSMWIssue;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RemindIssueActionModel {
	
	
	public static final String VERSION = "1.2";
	private String problemId;
	private ETSIssue issue;
	private int mailcount;
	private EtsIssProjectMember projMem;
	

	/**
	 * 
	 */
	public RemindIssueActionModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return
	 */
	public int getMailcount() {
		return mailcount;
	}



	/**
	 * @return
	 */
	public String getProblemId() {
		return problemId;
	}

	/**
	 * @param i
	 */
	public void setMailcount(int i) {
		mailcount = i;
	}


	/**
	 * @param string
	 */
	public void setProblemId(String string) {
		problemId = string;
	}

	/**
	 * @return
	 */
	public EtsIssProjectMember getProjMem() {
		return projMem;
	}

	/**
	 * @param member
	 */
	public void setProjMem(EtsIssProjectMember member) {
		projMem = member;
	}

	

	/**
	 * @return
	 */
	public ETSIssue getIssue() {
		return issue;
	}

	/**
	 * @param issue
	 */
	public void setIssue(ETSIssue issue) {
		this.issue = issue;
	}

}
