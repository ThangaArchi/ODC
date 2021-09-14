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


/*
 * Created on Jan 25, 2005
 */

package oem.edge.ets.fe.self;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author v2sathis
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ETSSelfAssessmentStatus {
	/**
	 *
	 */

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.2";

	public ETSSelfAssessmentStatus() {
		super();
	}


	public static void completeMemberStatus(Connection con, ETSSelfAssessment self, String sMemberId) throws SQLException, Exception {

		/**
		 * 1. check to see if the logged in person is a member of this self assessment.. (add can be done by admin also)
		 * 2. check to see if this person has completed all three sections.
		 * 3. if he has completed all three sections, then update ETS.ETS_SELF_MEMBERS with the status.
		 * 4. check if all the members in ets.ets_self_members has complete status.
		 * 5. Move the self assessment to the next step.
		 *
		 */


		try {

			String sCurrentStep = "";
			ArrayList steps = self.getStep();

			if (steps != null) {
				ETSSelfAssessmentStep step = (ETSSelfAssessmentStep) steps.get(steps.size()-1);
				sCurrentStep = step.getStep();
			}

			if (sCurrentStep.equalsIgnoreCase(ETSSelfConstants.SELF_STEP_MEMBER_ASSESSMENT)) {
				boolean bIsMember = false;
				ArrayList members = self.getMembers();
				for (int i = 0; i < members.size(); i++) {
					ETSSelfAssessmentMember member = (ETSSelfAssessmentMember) members.get(i);
					if (member.getMemberId().equalsIgnoreCase(sMemberId)) {
						bIsMember = true;
						break;
					}
				}

				if (bIsMember) {

					boolean KeyAttributes = false;
					boolean KeyLeverages = false;
					boolean PositiveEfforts = false;

					ArrayList sections = ETSSelfDAO.getMemberSectionStatus(con,self.getSelfId(),self.getProjectId(),sMemberId);

					for (int i = 0; i < sections.size(); i++) {

						ETSSelfMemberSectionStatus memberstatus = (ETSSelfMemberSectionStatus) sections.get(i);

						if (memberstatus.getSectionId() == ETSSelfConstants.SECTION_KEY_ATTRIBUTES) {
							KeyAttributes = true;
						}
						if (memberstatus.getSectionId() == ETSSelfConstants.SECTION_KEY_LEVERAGES) {
							KeyLeverages = true;
						}
						if (memberstatus.getSectionId() == ETSSelfConstants.SECTION_POSITIVE_EFFORTS) {
							PositiveEfforts = true;
						}
					}

					if (KeyAttributes && KeyLeverages && PositiveEfforts) {
						// update this members's completed status in ETS.ETS_SELF_MEMBERS table.

						ETSSelfDAO.updateMemberStatus(con,self.getSelfId(),self.getProjectId(),sMemberId);

					}

				}

			}




		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	public static boolean checkIfTeamAssessmentStepComplete(Connection con, ETSSelfAssessment self) throws Exception {

		/**
		 * 1. check to see if the logged in person is a member of this self assessment.. (add can be done by admin also)
		 * 2. check to see if this person has completed all three sections.
		 * 3. if he has completed all three sections, then update ETS.ETS_SELF_MEMBERS with the status.
		 * 4. check if all the members in ets.ets_self_members has complete status.
		 * 5. Move the self assessment to the next step.
		 *
		 */

		boolean complete = false;

		try {

			boolean bAllCompleted = true;

			String sCurrentStep = "";
			ArrayList steps = self.getStep();

			if (steps != null) {
				ETSSelfAssessmentStep step = (ETSSelfAssessmentStep) steps.get(steps.size()-1);
				sCurrentStep = step.getStep();
			}

			if (sCurrentStep.equalsIgnoreCase(ETSSelfConstants.SELF_STEP_MEMBER_ASSESSMENT)) {

				ArrayList members = self.getMembers();

				for (int i = 0; i < members.size(); i++) {

					ETSSelfAssessmentMember member = (ETSSelfAssessmentMember) members.get(i);
					if (!member.getCompleted().equalsIgnoreCase(ETSSelfConstants.MEMBER_COMPLETED)) {
						bAllCompleted = false;
						break;
					}
				}
			}

			if (bAllCompleted) {
				complete = true;
			}

		} catch (Exception e) {
			throw e;
		}

		return complete;

	}

}
