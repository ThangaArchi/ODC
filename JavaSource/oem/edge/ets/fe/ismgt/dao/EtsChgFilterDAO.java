package oem.edge.ets.fe.ismgt.dao;

import java.sql.*;
import java.util.*;

import oem.edge.common.*;
import oem.edge.amt.*;

import oem.edge.ets.fe.ismgt.helpers.*;
import oem.edge.ets.fe.ismgt.model.*;
import oem.edge.ets.fe.ETSDBUtils;
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
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class EtsChgFilterDAO extends FilterDAOAbs {

	public static final String VERSION = "1.48";

	/**
	 * Constructor for EtsChgFilterDAO.
	 */
	public EtsChgFilterDAO(EtsIssFilterObjectKey issobjkey) {
		super(issobjkey);
	}

	/**
		 * Constructor for EtsIssueActionDAO.
		 */
	public EtsChgFilterDAO(EtsIssObjectKey etsIssObjKey) {
		super(etsIssObjKey);
	}

	/**
	 * This method will give an ArrayList of Severity Types
	 * All values are now hard-coded
	 */

	public ArrayList getSeverityTypes() {

		ArrayList sevTypeList = new ArrayList();

		///
		sevTypeList.add("All");
		sevTypeList.add("All");
		sevTypeList.add("Critical");
		sevTypeList.add("Critical");
		sevTypeList.add("Urgent");
		sevTypeList.add("Urgent");
		sevTypeList.add("High Focus");
		sevTypeList.add("High Focus");
		sevTypeList.add("Normal");
		sevTypeList.add("Normal");
		sevTypeList.add("Low");
		sevTypeList.add("Low");
		return sevTypeList;

	}

	/**
	 * This method will give an ArrayList of Status of Issue Types
	 * All values are now hard-coded
	 */

	public ArrayList getStatusTypes() {

		ArrayList statusList = new ArrayList();

		statusList.add("All");
		statusList.add("Open");
		statusList.add("Under Review");
		statusList.add("Approved");
		statusList.add("Rejected");
		statusList.add("Deferred");
		statusList.add("Closed");
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

}
