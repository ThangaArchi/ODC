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

/*
* Created on May 10, 2005
*
* To change the template for this generated file go to
* Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
*/
package oem.edge.ets.fe.ismgt.middleware;

import java.sql.Timestamp;

import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.ismgt.helpers.UserProfileTimeZone;

/**
* @author jetendra
*
* To change the template for this generated type comment go to
* Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
*/
public class ComLogGen {
	public static final String VERSION = "1.6";

	public static boolean generateCommLog(ETSMWIssue currentRecord) throws 
Exception {
		boolean returnvalue = false;
		try {
			String ETS_ISSUES_TYPE = "";
			if(currentRecord.etsIssuesType!= null)
				ETS_ISSUES_TYPE = currentRecord.etsIssuesType;
			System.out.println(
				"ETS_ISSUES_TYPE in generateCommlog " + ETS_ISSUES_TYPE);

			String lastactionby = null;
			String endofline = "\n";
			StringBuffer commlog = new StringBuffer();
			commlog.append("==== ");
			if (currentRecord.problem_state.equals("New")
				|| currentRecord.problem_state.equals("Submit")
				|| currentRecord.problem_state.equals("Create")
				|| currentRecord.problem_state.equals("Submit")) {

				commlog.append("Submitted by ");
				lastactionby = "'Team Member'";

			}
			if (currentRecord.problem_state.equals("Resolve")) {
				lastactionby = "'Support'";
				commlog.append("Resolved by ");
			}

			if (currentRecord.problem_state.equals("Modify")) {
				commlog.append("Modified by ");
				if (currentRecord
					.last_userid
					.equals(currentRecord.problem_creator))
					lastactionby = "'Team Member'";
				else
					lastactionby = "'Support'";
			}
			if (currentRecord.problem_state.equals("Reject")) {
				commlog.append("Rejected by ");
				lastactionby = "'Team Member'";
			}
			if (currentRecord.problem_state.equals("Accept"))
				commlog.append("Accepted by ");
			if (currentRecord.problem_state.equals("Close")) {

				commlog.append("Closed by ");
				lastactionby = "'Team Member'";
			}
			if (currentRecord.problem_state.equals("Comment")) {
				commlog.append("Commented by ");
				if (currentRecord
					.last_userid
					.equals(currentRecord.problem_creator))
					lastactionby = "'Team Member'";
				else
					lastactionby = "'Support'";
			}
			if (currentRecord.problem_state.equals("Changeowner")) {

				commlog.append("Changed owner by ");
				lastactionby = "'Support'";
			}
			if (currentRecord.problem_state.equals("Withdraw")) {

				commlog.append("Withdrawn by ");
				lastactionby = "'Support'";
			}

			//String lastactionby=db2util.getFullNameofLastUser(db2util.getid_edge_from_CQ(currentRecord.getfieldValue("CQ_TRK_ID")));

			// changed by sathish because the above line was not working for new issues...
			if (!ETS_ISSUES_TYPE.equals("SUPPORT"))
				lastactionby =
					currentRecord.field_C14 + " " + currentRecord.field_C15;
			// end of change

			if (currentRecord.issue_source.equals("CQROC"))
				commlog.append(lastactionby);
			else
				commlog.append(lastactionby);
			commlog.append(" on ");

			Timestamp t = new Timestamp(System.currentTimeMillis());
								try {
									
									//commlog.append( ETSUtils.formatDateTime(t));	
									//v2sagar
									commlog.append( ETSUtils.formatDateTime(UserProfileTimeZone.getUTCDateTime()));
								} catch (Exception e) {
									//sendAlert("ERROR IN TIMESTAMP APPENDING");
								}

			commlog.append(" ====");
			commlog.append(endofline);
			commlog.append(endofline);
			commlog.append("Comments: ");
			commlog.append(endofline);
			if (currentRecord.problem_state.equals("New")
				|| currentRecord.problem_state.equals("Submit"))
				commlog.append(currentRecord.problem_desc);
			else {
				if (currentRecord.issue_source.endsWith("CQROC"))
					commlog.append(currentRecord.comm_from_cust);
				else{
					// To hide Changed owner name in comm log for bladetype issues
					if( (ETS_ISSUES_TYPE.equalsIgnoreCase("SUPPORT") && 
currentRecord.problem_state.equalsIgnoreCase("Changeowner")))
						commlog.append("Owner reassigned within support.");
					else
					commlog.append(currentRecord.comm_from_cust);

				}

			}
			commlog.append(endofline);
			commlog.append(endofline);

			currentRecord.comm_log = ETSDBUtils.escapeString(commlog.toString());
			returnvalue = true;

		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			throw new Exception("Could not generate commentary log");
			//		  ETSCQUpdate.sendAlert("ERROR IN Generating COMM_LOG \n" + e.getMessage());
		}
		return returnvalue;
	}

	public static boolean generateFeedbackCommLog(ETSMWIssue currentRecord) 
throws Exception {
		boolean returnvalue = false;
		try {
			String ETS_ISSUES_TYPE = "";
			if(currentRecord.etsIssuesType!= null)
				ETS_ISSUES_TYPE = currentRecord.etsIssuesType;
			System.out.println(
				"ETS_ISSUES_TYPE in generateCommlog " + ETS_ISSUES_TYPE);

			String lastactionby = null;
			String endofline = "\n";
			StringBuffer commlog = new StringBuffer();
			commlog.append("==== ");
			if (currentRecord.problem_state.equals("New")
				|| currentRecord.problem_state.equals("Submit")
				|| currentRecord.problem_state.equals("Create")
				|| currentRecord.problem_state.equals("Submit")) {

				commlog.append("Submitted by ");
				lastactionby = "'Team Member'";

			}
			if (currentRecord.problem_state.equals("Resolve")) {
				lastactionby = "'Support'";
				commlog.append("Resolved by ");
			}

			if (currentRecord.problem_state.equals("Modify")) {
				commlog.append("Modified by ");
				if (currentRecord
					.last_userid
					.equals(currentRecord.problem_creator))
					lastactionby = "'Team Member'";
				else
					lastactionby = "'Support'";
			}
			if (currentRecord.problem_state.equals("Reject")) {
				commlog.append("Rejected by ");
				lastactionby = "'Team Member'";
			}
			if (currentRecord.problem_state.equals("Accept"))
				commlog.append("Accepted by ");
			if (currentRecord.problem_state.equals("Close")) {

				commlog.append("Closed by ");
				lastactionby = "'Team Member'";
			}
			if (currentRecord.problem_state.equals("Comment")) {
				commlog.append("Commented by ");
				if (currentRecord
					.last_userid
					.equals(currentRecord.problem_creator))
					lastactionby = "'Team Member'";
				else
					lastactionby = "'Support'";
			}
			if (currentRecord.problem_state.equals("Changeowner")) {

				commlog.append("Changed owner by ");
				lastactionby = "'Support'";
			}
			if (currentRecord.problem_state.equals("Withdraw")) {

				commlog.append("Withdrawn by ");
				lastactionby = "'Support'";
			}

			//String lastactionby=db2util.getFullNameofLastUser(db2util.getid_edge_from_CQ(currentRecord.getfieldValue("CQ_TRK_ID")));

			// changed by sathish because the above line was not working for new issues...
			if (!ETS_ISSUES_TYPE.equals("SUPPORT"))
				lastactionby =
					currentRecord.field_C14 + " " + currentRecord.field_C15;
			// end of change

			commlog.append(lastactionby);
			commlog.append(" on ");

			Timestamp t = new Timestamp(System.currentTimeMillis());
								try {
									//commlog.append( ETSUtils.formatDateTime(t));
									//v2sagar
									commlog.append( ETSUtils.formatDateTime(UserProfileTimeZone.getUTCDateTime()));
								} catch (Exception e) {
									//sendAlert("ERROR IN TIMESTAMP APPENDING");
								}

			commlog.append(" ====");
			commlog.append(endofline);
			commlog.append(endofline);
			commlog.append("Comments: ");
			commlog.append(endofline);
			if (currentRecord.problem_state.equals("New")
				|| currentRecord.problem_state.equals("Submit"))
				commlog.append(currentRecord.comm_from_cust);
			else {

					// To hide Changed owner name in comm log for bladetype issues
					if( (ETS_ISSUES_TYPE.equalsIgnoreCase("SUPPORT") && 
currentRecord.problem_state.equalsIgnoreCase("Changeowner")))
						commlog.append("Owner reassigned within support.");
					else
					commlog.append(currentRecord.comm_from_cust);



			}
			commlog.append(endofline);
			commlog.append(endofline);

			currentRecord.comm_log = ETSDBUtils.escapeString(commlog.toString());
			returnvalue = true;

		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			throw new Exception("Could not generate commentary log");
			//		  ETSCQUpdate.sendAlert("ERROR IN Generating COMM_LOG \n" + e.getMessage());
		}
		return returnvalue;
	}
}

