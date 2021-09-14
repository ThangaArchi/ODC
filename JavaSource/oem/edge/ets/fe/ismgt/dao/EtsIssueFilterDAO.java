package oem.edge.ets.fe.ismgt.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterUtils;
import oem.edge.ets.fe.ismgt.middleware.ETSMWIssue;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterRepTabBean;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssOwnerInfo;
import oem.edge.ets.fe.ismgt.model.EtsIssProjectMember;

import org.apache.commons.logging.Log;
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

/**
 * @author v2phani
 * Issue Filter DAO
 * 
 */
public class EtsIssueFilterDAO extends FilterDAOAbs {

	public static final String VERSION = "1.47";

	private static Log logger = EtsLogger.getLogger(EtsIssueFilterDAO.class);
	
	
	/**
	 * Constructor for EtsIssueFilterDAO.
	 */
	public EtsIssueFilterDAO(EtsIssFilterObjectKey issobjkey) {
		super(issobjkey);
	}

	/**
		 * Constructor for EtsIssueActionDAO.
		 */
	public EtsIssueFilterDAO(EtsIssObjectKey etsIssObjKey) {
		super(etsIssObjKey);
	}

	/**
		 * This method will give an ArrayList of Severity Types
		 * All values are now hard-coded
		 */

	public ArrayList getSeverityTypes() {

		ArrayList sevTypeList = new ArrayList();

		sevTypeList.add("All");
		sevTypeList.add("All");
		sevTypeList.add("1-Critical");
		sevTypeList.add("Critical");
		sevTypeList.add("2-Major");
		sevTypeList.add("Major");
		sevTypeList.add("3-Average");
		sevTypeList.add("Average");
		sevTypeList.add("4-Minor");
		sevTypeList.add("Minor");
		sevTypeList.add("5-Enhancement");
		sevTypeList.add("Enhancement");

		return sevTypeList;

	}

	/**
	 * This method will give an ArrayList of Status of Issue Types
	 * All values are now hard-coded
	 */

	public ArrayList getStatusTypes() {

		ArrayList statusList = new ArrayList();

		statusList.add("All");
		statusList.add("Assigned"); //submitted changed to Assigned
		statusList.add("Closed");
		statusList.add("In Process");
		statusList.add("Modified");
		statusList.add("Open");
		statusList.add("Rejected");
		statusList.add("Resolved");
		statusList.add("Withdrawn");

		return statusList;
	}

	/***
	 * To return the ArrayList of Submitters
	 */

	public ArrayList getSubmitterList() throws SQLException, Exception {

		return getProjMemberList();
	}

	/***
	 * To return the ArrayList of Owners 
	 */

	public ArrayList getOwnersList() throws SQLException, Exception {

		return getProjMemberList();

	}

	
} //end of class
