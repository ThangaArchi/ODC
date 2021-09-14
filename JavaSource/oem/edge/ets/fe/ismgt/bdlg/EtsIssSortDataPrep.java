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
package oem.edge.ets.fe.ismgt.bdlg;

import java.sql.SQLException;

import oem.edge.common.Global;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterDetailsBean;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterQryPrepBean;
import oem.edge.ets.fe.ismgt.model.EtsIssSortObjModel;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssSortDataPrep implements EtsIssFilterConstants {

	public static final String VERSION = "1.30";
	private EtsIssFilterObjectKey issfilterkey;

	/**
	 * 
	 */
	public EtsIssSortDataPrep(EtsIssFilterObjectKey issfilterkey) {

		super();
		this.issfilterkey = issfilterkey;

	}

	/**
		 * 
		 * @param edgeProblemId
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 * get history list
		 */

	public EtsIssFilterQryPrepBean getSortInfoBean(EtsIssFilterDetailsBean etsFilterBean) throws SQLException, Exception {

		int state = 0;
		String sortColumn = "lasttime";
		String sortOrder = "desc";

		//get sort state		
		state = issfilterkey.getSortState();

		Global.println("SORT STATE IN ISSUE SORT PREP BEAN====" + state);

		switch (state) {

			case 0 :

				sortColumn = "lasttime";
				sortOrder = "desc";

				break;

			case SORTTRKID_A :

				sortColumn = "refid";
				sortOrder = "";

				break;

			case SORTTRKID_D :

				sortColumn = "refid";
				sortOrder = "desc";

				break;

			case SORTISSUETITLE_A :

				sortColumn = "title";
				sortOrder = "";

				break;

			case SORTISSUETITLE_D :

				sortColumn = "title";
				sortOrder = "desc";

				break;

			case SORTISSUETYPE_A :

				sortColumn = "problemtype";
				sortOrder = "";

				break;

			case SORTISSUETYPE_D :

				sortColumn = "problemtype";
				sortOrder = "desc";

				break;

			case SORTSUBMITTER_A :

				sortColumn = "submittername";
				sortOrder = "";

				break;

			case SORTSUBMITTER_D :

				sortColumn = "submittername";
				sortOrder = "desc";

				break;

			case SORTOWNER_A :

				sortColumn = "ownername";
				sortOrder = "";

				break;

			case SORTOWNER_D :

				sortColumn = "ownername";
				sortOrder = "desc";

				break;

			case SORTSEVERITY_A :

				sortColumn = "severity";
				sortOrder = "";

				break;

			case SORTSEVERITY_D :

				sortColumn = "severity";
				sortOrder = "desc";

				break;

			case SORTSTATUS_A :

				sortColumn = "problemstate";
				sortOrder = "";

				break;

			case SORTSTATUS_D :

				sortColumn = "problemstate";
				sortOrder = "desc";

				break;

			default :

				sortColumn = "lasttime";
				sortOrder = "desc";

				break;
		}

		etsFilterBean.setSortColumn(sortColumn);
		etsFilterBean.setSortOrder(sortOrder);

		//delegate qry prep to another bean
		EtsIssFilterQryPrepBean qryPrepBean = new EtsIssFilterQryPrepBean(etsFilterBean, issfilterkey);

		return qryPrepBean;

	}

	/**
			 * 
			 * @param edgeProblemId
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 * get history list
			 */

	public EtsIssSortObjModel getIssTypeListSortInfoBean() throws SQLException, Exception {

		int state = 0;
		EtsIssSortObjModel sortObjModel = new EtsIssSortObjModel();
		String sortColumn = "issuetype";
		String sortOrder = "";

		//get sort state		
		state = issfilterkey.getSortState();

		Global.println("SORT STATE IN ISSUE SORT PREP BEAN====" + state);

		switch (state) {

			case 0 :

				sortColumn = "issuetype";
				sortOrder = "";

				break;

			case SORTLISTISSTYPENAME_A :

				sortColumn = "issuetype";
				sortOrder = "";

				break;

			case SORTLISTISSTYPENAME_D :

				sortColumn = "issuetype";
				sortOrder = "desc";

				break;

			case SORTLISTISSTYPEACCESS_A :

				sortColumn = "issueaccess";
				sortOrder = "";

				break;

			case SORTLISTISSTYPEACCESS_D :

				sortColumn = "issueaccess";
				sortOrder = "desc";

				break;

			case SORTLISTISSTYPEOWNER_A :

				sortColumn = "ownername";
				sortOrder = "";

				break;

			case SORTLISTISSTYPEOWNER_D :

				sortColumn = "ownername";
				sortOrder = "desc";

				break;

			case SORTLISTISSTYPEOWNEREMAIL_A :

				sortColumn = "owneremail";
				sortOrder = "";

				break;

			case SORTLISTISSTYPEOWNEREMAIL_D :

				sortColumn = "owneremail";
				sortOrder = "desc";

				break;
				
			case SORTLISTISSTYPEBACKUPOWNER_A :

				sortColumn = "backupownername";
				sortOrder = "";

				break;

			case SORTLISTISSTYPEBACKUPOWNER_D :

				sortColumn = "backupownername";
				sortOrder = "desc";

				break;

			case SORTLISTISSTYPEBACKUPOWNEREMAIL_A :

				sortColumn = "backupowneremail";
				sortOrder = "";

				break;

			case SORTLISTISSTYPEBACKUPOWNEREMAIL_D :

				sortColumn = "backupowneremail";
				sortOrder = "desc";

				break;
				
				

			default :

				sortColumn = "issuetype";
				sortOrder = "";

				break;
		}

		sortObjModel.setSortColumn(sortColumn);
		sortObjModel.setSortOrder(sortOrder);

		
		return sortObjModel;

	}

} //end of class
