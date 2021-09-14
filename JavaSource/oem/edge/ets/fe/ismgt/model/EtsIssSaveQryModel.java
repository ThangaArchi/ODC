/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005                                          */
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

import java.sql.Timestamp;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssSaveQryModel {
	
	public static final String VERSION = "1.12";
	
	///in params
	private String edgeUserId;
	private String projectId;
	private String queryView;
	private String queryName;
	private String queryComment;
	private String querySql;
	private String lastUserId;
	private Timestamp lastTimestamp;
	
	
	/**
	 * 
	 */
	public EtsIssSaveQryModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return
	 */
	public String getEdgeUserId() {
		return edgeUserId;
	}

	/**
	 * @return
	 */
	public Timestamp getLastTimestamp() {
		return lastTimestamp;
	}

	/**
	 * @return
	 */
	public String getLastUserId() {
		return lastUserId;
	}

	/**
	 * @return
	 */
	public String getProjectId() {
		return projectId;
	}

	/**
	 * @return
	 */
	public String getQueryComment() {
		return queryComment;
	}

	/**
	 * @return
	 */
	public String getQueryName() {
		return queryName;
	}

	/**
	 * @return
	 */
	public String getQuerySql() {
		return querySql;
	}

	/**
	 * @return
	 */
	public String getQueryView() {
		return queryView;
	}

	/**
	 * @param string
	 */
	public void setEdgeUserId(String string) {
		edgeUserId = string;
	}

	/**
	 * @param timestamp
	 */
	public void setLastTimestamp(Timestamp timestamp) {
		lastTimestamp = timestamp;
	}

	/**
	 * @param string
	 */
	public void setLastUserId(String string) {
		lastUserId = string;
	}

	/**
	 * @param string
	 */
	public void setProjectId(String string) {
		projectId = string;
	}

	/**
	 * @param string
	 */
	public void setQueryComment(String string) {
		queryComment = string;
	}

	/**
	 * @param string
	 */
	public void setQueryName(String string) {
		queryName = string;
	}

	/**
	 * @param string
	 */
	public void setQuerySql(String string) {
		querySql = string;
	}

	/**
	 * @param string
	 */
	public void setQueryView(String string) {
		queryView = string;
	}

}
