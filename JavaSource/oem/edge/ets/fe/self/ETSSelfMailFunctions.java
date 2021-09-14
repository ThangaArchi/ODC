/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2005                                     */
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
 * Created on Jan 26, 2005
 */
 
package oem.edge.ets.fe.self;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import oem.edge.common.Global;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;

/**
 * @author v2sathis
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ETSSelfMailFunctions {
	/**
	 * 
	 */
	
	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.4";
	
	public ETSSelfMailFunctions() {
		super();
	}
	
	public static ETSSelfMail createNewSelfAssessmentMail(Connection con, String sSelfId, String sProjectId, String sCreatedById) throws SQLException, Exception {
		
		ETSSelfMail mail = new ETSSelfMail();
		StringBuffer sEmailStr = new StringBuffer("");
		StringBuffer sEmailTo = new StringBuffer("");
		
		
		try {
			
			UnbrandedProperties prop = PropertyFactory.getProperty(con,sProjectId);
			
			int iTCForSelfAssessment = ETSDatabaseManager.getTopCatId(sProjectId,Defines.SELF_ASSESSMENT_VT);
			
			String sPrimaryContactID = ETSSelfDAO.getPrimaryContact(con,sProjectId);
			String sPrimaryContactEmail = ETSUtils.getUserEmail(con,sPrimaryContactID);
			
			ETSSelfAssessment self = ETSSelfDAO.getSelfAssessment(con,sProjectId,sSelfId);
			ArrayList members = self.getMembers();

			mail.setSubject(prop.getAppName() + " Self Assessment notification: A new self assessment has been created.");

			sEmailStr.append("Self assessment: " + self.getTitle() + "\n\n");

			sEmailStr.append("A new self assessment has been created on IBM " + prop.getClientVoiceTitle() + " workspace.\n");
			sEmailStr.append("The details of the self assessment are as follows:\n\n");

			sEmailStr.append("  Title:          " + self.getTitle() + "\n");
			for (int i = 0; i < members.size(); i++) {
				ETSSelfAssessmentMember member = (ETSSelfAssessmentMember) members.get(i);
				
				if (i == 0) {
					sEmailStr.append("  Members:        " + member.getMemberName() + "\n");
				} else {
					sEmailStr.append("                  " + member.getMemberName() + "\n");
				}
				
			}
			sEmailStr.append("  Owner:          " + ETSUtils.getUsersName(con,self.getAssessmentOwner()) + " \n");
			sEmailStr.append("  Plan Owner:     " + ETSUtils.getUsersName(con,self.getPlanOwner()) + " \n");
			sEmailStr.append("  Created By:     " + ETSUtils.getUsersName(con,sCreatedById) + " \n");
			sEmailStr.append("  Created on:     " + ETSUtils.formatDate(self.getLastTimestamp()) + " (mm/dd/yyyy) \n\n");

			sEmailStr.append("To view this self assessment, click on the following URL:\n");
			sEmailStr.append(Global.getUrl("ets/ETSProjectsServlet.wss") + "?proj=" + self.getProjectId() + "&tc=" + iTCForSelfAssessment + "&self=" + self.getSelfId() + "&linkid=" + prop.getLinkID() + "\n\n");

			sEmailStr.append(prop.getEmailFooter());
//			sEmailStr.append("===============================================================\n");
//			sEmailStr.append("IBM E&TS Connect enables clients of IBM Engineering and Technology Services (E&TS)\n");
//			sEmailStr.append("and IBM team members to collaborate on proposals and project information.\n");
//			sEmailStr.append("E&TS Connect provides a highly secure workspace and a comprehensive suite\n");
//			sEmailStr.append("of on demand tools that is available online 24/7.\n");
//			sEmailStr.append("===============================================================\n");
//			sEmailStr.append("This is a system-generated e-mail delivered by E&TS Connect. \n");
//			sEmailStr.append("===============================================================\n\n");
			
			mail.setMessage(sEmailStr.toString());
			
			for (int i = 0; i < members.size(); i++) {
				ETSSelfAssessmentMember member = (ETSSelfAssessmentMember) members.get(i);
				
				if (sEmailTo.length() == 0) {
					sEmailTo.append(member.getMemberEmail());
				} else {
					sEmailTo.append("," + member.getMemberEmail());
				}
			}
			
			if (sEmailTo.length() == 0) {
				sEmailTo.append(ETSUtils.getUserEmail(con,self.getPlanOwner()));
			} else {
				sEmailTo.append("," + ETSUtils.getUserEmail(con,self.getPlanOwner()));
			}
			
			if (sEmailTo.length() == 0) {
				sEmailTo.append(ETSUtils.getUserEmail(con,self.getAssessmentOwner()));
			} else {
				sEmailTo.append("," + ETSUtils.getUserEmail(con,self.getAssessmentOwner()));
			}
			
			mail.setTo(sEmailTo.toString());
			mail.setFrom(sPrimaryContactEmail);
			mail.setBcc("");
			mail.setCc("");
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return mail;
	}

	public static ETSSelfMail createEditSelfAssessmentMail(Connection con, String sSelfId, String sProjectId, String sEditedBy) throws SQLException, Exception {
		
		ETSSelfMail mail = new ETSSelfMail();
		StringBuffer sEmailStr = new StringBuffer("");
		StringBuffer sEmailTo = new StringBuffer("");
		
		
		try {
			
			UnbrandedProperties prop = PropertyFactory.getProperty(con,sProjectId);
			
			int iTCForSelfAssessment = ETSDatabaseManager.getTopCatId(sProjectId,Defines.SELF_ASSESSMENT_VT);
			
			String sPrimaryContactID = ETSSelfDAO.getPrimaryContact(con,sProjectId);
			String sPrimaryContactEmail = ETSUtils.getUserEmail(con,sPrimaryContactID);
			
						
			ETSSelfAssessment self = ETSSelfDAO.getSelfAssessment(con,sProjectId,sSelfId);
			ArrayList members = self.getMembers();
	
			mail.setSubject(prop.getAppName() + " Self Assessment notification: A self assessment has been updated.");
	
			sEmailStr.append("Self assessment: " + self.getTitle() + "\n\n");
	
			sEmailStr.append("A self assessment has been updated on IBM " + prop.getClientVoiceTitle() + " workspace.\n");
			sEmailStr.append("The details of the self assessment are as follows:\n\n");
	
			sEmailStr.append("  Title:          " + self.getTitle() + "\n");
			
			for (int i = 0; i < members.size(); i++) {
				ETSSelfAssessmentMember member = (ETSSelfAssessmentMember) members.get(i);
				
				if (i == 0) {
					sEmailStr.append("  Members:        " + member.getMemberName() + "\n");
				} else {
					sEmailStr.append("                  " + member.getMemberName() + "\n");
				}
				
			}
			sEmailStr.append("  Owner:          " + ETSUtils.getUsersName(con,self.getAssessmentOwner()) + " \n");
			sEmailStr.append("  Plan Owner:     " + ETSUtils.getUsersName(con,self.getPlanOwner()) + " \n");
			sEmailStr.append("  Updated by:     " + ETSUtils.getUsersName(con,sEditedBy) + " \n\n");
	
			sEmailStr.append("To view this self assessment, click on the following URL:\n");
			sEmailStr.append(Global.getUrl("ets/ETSProjectsServlet.wss") + "?proj=" + self.getProjectId() + "&tc=" + iTCForSelfAssessment + "&self=" + self.getSelfId() + "&linkid=" + prop.getLinkID() + "\n\n");
	
			sEmailStr.append(prop.getEmailFooter());
//			sEmailStr.append("===============================================================\n");
//			sEmailStr.append("IBM E&TS Connect enables clients of IBM Engineering and Technology Services (E&TS)\n");
//			sEmailStr.append("and IBM team members to collaborate on proposals and project information.\n");
//			sEmailStr.append("E&TS Connect provides a highly secure workspace and a comprehensive suite\n");
//			sEmailStr.append("of on demand tools that is available online 24/7.\n");
//			sEmailStr.append("===============================================================\n");
//			sEmailStr.append("This is a system-generated e-mail delivered by E&TS Connect. \n");
//			sEmailStr.append("===============================================================\n\n");
			
			mail.setMessage(sEmailStr.toString());
			
			for (int i = 0; i < members.size(); i++) {
				ETSSelfAssessmentMember member = (ETSSelfAssessmentMember) members.get(i);
				
				if (sEmailTo.length() == 0) {
					sEmailTo.append(member.getMemberEmail());
				} else {
					sEmailTo.append("," + member.getMemberEmail());
				}
			}
			
			if (sEmailTo.length() == 0) {
				sEmailTo.append(ETSUtils.getUserEmail(con,self.getPlanOwner()));
			} else {
				sEmailTo.append("," + ETSUtils.getUserEmail(con,self.getPlanOwner()));
			}
			
			if (sEmailTo.length() == 0) {
				sEmailTo.append(ETSUtils.getUserEmail(con,self.getAssessmentOwner()));
			} else {
				sEmailTo.append("," + ETSUtils.getUserEmail(con,self.getAssessmentOwner()));
			}
			
			mail.setTo(sEmailTo.toString());
			mail.setFrom(sPrimaryContactEmail);
			mail.setBcc("");
			mail.setCc("");
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	
		return mail;
	}

	public static ETSSelfMail createTeamAssessmentStepCompleteMail(Connection con, String sSelfId, String sProjectId, String sUserId) throws SQLException, Exception {
		
		ETSSelfMail mail = new ETSSelfMail();
		StringBuffer sEmailStr = new StringBuffer("");
		StringBuffer sEmailTo = new StringBuffer("");
		
		
		try {
			
			UnbrandedProperties prop = PropertyFactory.getProperty(con,sProjectId);
			
			int iTCForSelfAssessment = ETSDatabaseManager.getTopCatId(sProjectId,Defines.SELF_ASSESSMENT_VT);
			
			String sPrimaryContactID = ETSSelfDAO.getPrimaryContact(con,sProjectId);
			String sPrimaryContactEmail = ETSUtils.getUserEmail(con,sPrimaryContactID);
			
						
			ETSSelfAssessment self = ETSSelfDAO.getSelfAssessment(con,sProjectId,sSelfId);
			ArrayList members = self.getMembers();
	
			mail.setSubject(prop.getAppName() + " Self Assessment notification: Team member assessment completed.");
	
			sEmailStr.append("Self assessment: " + self.getTitle() + "\n\n");
	
			sEmailStr.append("The assessment for the following self assessment has been completed.\n");
			sEmailStr.append("Compiled assessment is now available for view.\n\n");
			
			sEmailStr.append("The details of the self assessment are as follows:\n\n");
	
			sEmailStr.append("  Title:          " + self.getTitle() + "\n");
			
			for (int i = 0; i < members.size(); i++) {
				ETSSelfAssessmentMember member = (ETSSelfAssessmentMember) members.get(i);
				
				if (i == 0) {
					sEmailStr.append("  Members:        " + member.getMemberName() + "\n");
				} else {
					sEmailStr.append("                  " + member.getMemberName() + "\n");
				}
				
			}
			sEmailStr.append("  Owner:          " + ETSUtils.getUsersName(con,self.getAssessmentOwner()) + " \n");
			sEmailStr.append("  Plan Owner:     " + ETSUtils.getUsersName(con,self.getPlanOwner()) + " \n");
			if (sUserId.trim().equals("")) {
				sEmailStr.append("  Completed by:   " + prop.getAppName() + " system \n\n");
			} else {
				sEmailStr.append("  Completed by:   " + ETSUtils.getUsersName(con,sUserId) + " \n\n");
			}
	
			sEmailStr.append("To view this self assessment, click on the following URL:\n");
			sEmailStr.append(Global.getUrl("ets/ETSProjectsServlet.wss") + "?proj=" + self.getProjectId() + "&tc=" + iTCForSelfAssessment + "&self=" + self.getSelfId() + "&linkid=" + prop.getLinkID() + "\n\n");
	
			sEmailStr.append(prop.getEmailFooter());
//			sEmailStr.append("===============================================================\n");
//			sEmailStr.append("IBM E&TS Connect enables clients of IBM Engineering and Technology Services (E&TS)\n");
//			sEmailStr.append("and IBM team members to collaborate on proposals and project information.\n");
//			sEmailStr.append("E&TS Connect provides a highly secure workspace and a comprehensive suite\n");
//			sEmailStr.append("of on demand tools that is available online 24/7.\n");
//			sEmailStr.append("===============================================================\n");
//			sEmailStr.append("This is a system-generated e-mail delivered by E&TS Connect. \n");
//			sEmailStr.append("===============================================================\n\n");
			
			mail.setMessage(sEmailStr.toString());
			
			
			
			for (int i = 0; i < members.size(); i++) {
				ETSSelfAssessmentMember member = (ETSSelfAssessmentMember) members.get(i);
				
				if (sEmailTo.length() == 0) {
					sEmailTo.append(member.getMemberEmail());
				} else {
					sEmailTo.append("," + member.getMemberEmail());
				}
			}
			
			if (sEmailTo.length() == 0) {
				sEmailTo.append(ETSUtils.getUserEmail(con,self.getPlanOwner()));
			} else {
				sEmailTo.append("," + ETSUtils.getUserEmail(con,self.getPlanOwner()));
			}
			
			if (sEmailTo.length() == 0) {
				sEmailTo.append(ETSUtils.getUserEmail(con,self.getAssessmentOwner()));
			} else {
				sEmailTo.append("," + ETSUtils.getUserEmail(con,self.getAssessmentOwner()));
			}
			
			mail.setTo(sEmailTo.toString());
			mail.setFrom(sPrimaryContactEmail);
			
			mail.setBcc("");
			mail.setCc("");
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	
		return mail;
	}

	public static ETSSelfMail createCompiledAssessmentStepCompleteMail(Connection con, String sSelfId, String sProjectId, String sUserId) throws SQLException, Exception {
		
		ETSSelfMail mail = new ETSSelfMail();
		StringBuffer sEmailStr = new StringBuffer("");
		StringBuffer sEmailTo = new StringBuffer("");
		
		
		try {
			
			UnbrandedProperties prop = PropertyFactory.getProperty(con,sProjectId);
			
			int iTCForSelfAssessment = ETSDatabaseManager.getTopCatId(sProjectId,Defines.SELF_ASSESSMENT_VT);
	
			String sPrimaryContactID = ETSSelfDAO.getPrimaryContact(con,sProjectId);
			String sPrimaryContactEmail = ETSUtils.getUserEmail(con,sPrimaryContactID);
			
			
			ETSSelfAssessment self = ETSSelfDAO.getSelfAssessment(con,sProjectId,sSelfId);
			ArrayList members = self.getMembers();
	
			mail.setSubject(prop.getAppName() + " Self Assessment notification: Compiled assessment completed.");
	
			sEmailStr.append("Self assessment: " + self.getTitle() + "\n\n");
	
			sEmailStr.append("The assessment for the following self assessment has been \n");
			sEmailStr.append("reviewed and completed. Action plan documentation can now begin. \n\n");
			
			sEmailStr.append("The details of the self assessment are as follows:\n\n");
	
			sEmailStr.append("  Title:          " + self.getTitle() + "\n");
			
			for (int i = 0; i < members.size(); i++) {
				ETSSelfAssessmentMember member = (ETSSelfAssessmentMember) members.get(i);
				
				if (i == 0) {
					sEmailStr.append("  Members:        " + member.getMemberName() + "\n");
				} else {
					sEmailStr.append("                  " + member.getMemberName() + "\n");
				}
				
			}
			sEmailStr.append("  Owner:          " + ETSUtils.getUsersName(con,self.getAssessmentOwner()) + " \n");
			sEmailStr.append("  Plan Owner:     " + ETSUtils.getUsersName(con,self.getPlanOwner()) + " \n");
			sEmailStr.append("  Completed by:   " + ETSUtils.getUsersName(con,sUserId) + " \n\n");
	
			sEmailStr.append("To view this self assessment, click on the following URL:\n");
			sEmailStr.append(Global.getUrl("ets/ETSProjectsServlet.wss") + "?proj=" + self.getProjectId() + "&tc=" + iTCForSelfAssessment + "&self=" + self.getSelfId() + "&linkid=" + prop.getLinkID() + "\n\n");
	
			sEmailStr.append(prop.getEmailFooter());
//			sEmailStr.append("===============================================================\n");
//			sEmailStr.append("IBM E&TS Connect enables clients of IBM Engineering and Technology Services (E&TS)\n");
//			sEmailStr.append("and IBM team members to collaborate on proposals and project information.\n");
//			sEmailStr.append("E&TS Connect provides a highly secure workspace and a comprehensive suite\n");
//			sEmailStr.append("of on demand tools that is available online 24/7.\n");
//			sEmailStr.append("===============================================================\n");
//			sEmailStr.append("This is a system-generated e-mail delivered by E&TS Connect. \n");
//			sEmailStr.append("===============================================================\n\n");
			
			mail.setMessage(sEmailStr.toString());
			
			
			
			for (int i = 0; i < members.size(); i++) {
				ETSSelfAssessmentMember member = (ETSSelfAssessmentMember) members.get(i);
				
				if (sEmailTo.length() == 0) {
					sEmailTo.append(member.getMemberEmail());
				} else {
					sEmailTo.append("," + member.getMemberEmail());
				}
			}
			
			if (sEmailTo.length() == 0) {
				sEmailTo.append(ETSUtils.getUserEmail(con,self.getPlanOwner()));
			} else {
				sEmailTo.append("," + ETSUtils.getUserEmail(con,self.getPlanOwner()));
			}
			
			if (sEmailTo.length() == 0) {
				sEmailTo.append(ETSUtils.getUserEmail(con,self.getAssessmentOwner()));
			} else {
				sEmailTo.append("," + ETSUtils.getUserEmail(con,self.getAssessmentOwner()));
			}
			
			mail.setTo(sEmailTo.toString());
			mail.setFrom(sPrimaryContactEmail);
			
			mail.setBcc("");
			mail.setCc("");
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	
		return mail;
	}

	public static ETSSelfMail createActionPlanStepCompleteMail(Connection con, String sSelfId, String sProjectId, String sUserId) throws SQLException, Exception {
		
		ETSSelfMail mail = new ETSSelfMail();
		StringBuffer sEmailStr = new StringBuffer("");
		StringBuffer sEmailTo = new StringBuffer("");
		
		
		try {
			
			UnbrandedProperties prop = PropertyFactory.getProperty(con,sProjectId);
			
			int iTCForSelfAssessment = ETSDatabaseManager.getTopCatId(sProjectId,Defines.SELF_ASSESSMENT_VT);
			
			String sPrimaryContactID = ETSSelfDAO.getPrimaryContact(con,sProjectId);
			String sPrimaryContactEmail = ETSUtils.getUserEmail(con,sPrimaryContactID);
			
						
			ETSSelfAssessment self = ETSSelfDAO.getSelfAssessment(con,sProjectId,sSelfId);
			ArrayList members = self.getMembers();
	
			mail.setSubject(prop.getAppName() + " Self Assessment notification: Action plan has been closed.");
	
			sEmailStr.append("Self assessment: " + self.getTitle() + "\n\n");
	
			sEmailStr.append("The action plan has been documented and closed for \n");
			sEmailStr.append("the following self assessment. \n\n");
			
			sEmailStr.append("The details of the self assessment are as follows:\n\n");
	
			sEmailStr.append("  Title:          " + self.getTitle() + "\n");
			
			for (int i = 0; i < members.size(); i++) {
				ETSSelfAssessmentMember member = (ETSSelfAssessmentMember) members.get(i);
				
				if (i == 0) {
					sEmailStr.append("  Members:        " + member.getMemberName() + "\n");
				} else {
					sEmailStr.append("                  " + member.getMemberName() + "\n");
				}
				
			}
			sEmailStr.append("  Owner:          " + ETSUtils.getUsersName(con,self.getAssessmentOwner()) + " \n");
			sEmailStr.append("  Plan Owner:     " + ETSUtils.getUsersName(con,self.getPlanOwner()) + " \n");
			sEmailStr.append("  Completed by:   " + ETSUtils.getUsersName(con,sUserId) + " \n\n");
	
			sEmailStr.append("To view this self assessment, click on the following URL:\n");
			sEmailStr.append(Global.getUrl("ets/ETSProjectsServlet.wss") + "?proj=" + self.getProjectId() + "&tc=" + iTCForSelfAssessment + "&self=" + self.getSelfId() + "&linkid=" + prop.getLinkID() + "\n\n");
	
			sEmailStr.append(prop.getEmailFooter());
//			sEmailStr.append("===============================================================\n");
//			sEmailStr.append("IBM E&TS Connect enables clients of IBM Engineering and Technology Services (E&TS)\n");
//			sEmailStr.append("and IBM team members to collaborate on proposals and project information.\n");
//			sEmailStr.append("E&TS Connect provides a highly secure workspace and a comprehensive suite\n");
//			sEmailStr.append("of on demand tools that is available online 24/7.\n");
//			sEmailStr.append("===============================================================\n");
//			sEmailStr.append("This is a system-generated e-mail delivered by E&TS Connect. \n");
//			sEmailStr.append("===============================================================\n\n");
			
			mail.setMessage(sEmailStr.toString());
			
			
			
			for (int i = 0; i < members.size(); i++) {
				ETSSelfAssessmentMember member = (ETSSelfAssessmentMember) members.get(i);
				
				if (sEmailTo.length() == 0) {
					sEmailTo.append(member.getMemberEmail());
				} else {
					sEmailTo.append("," + member.getMemberEmail());
				}
			}
			
			if (sEmailTo.length() == 0) {
				sEmailTo.append(ETSUtils.getUserEmail(con,self.getPlanOwner()));
			} else {
				sEmailTo.append("," + ETSUtils.getUserEmail(con,self.getPlanOwner()));
			}
			
			if (sEmailTo.length() == 0) {
				sEmailTo.append(ETSUtils.getUserEmail(con,self.getAssessmentOwner()));
			} else {
				sEmailTo.append("," + ETSUtils.getUserEmail(con,self.getAssessmentOwner()));
			}
			
			mail.setTo(sEmailTo.toString());
			mail.setFrom(sPrimaryContactEmail);
			
			mail.setBcc("");
			mail.setCc("");
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	
		return mail;
	}

	public static ETSSelfMail createCloseSelfAssessmentMail(Connection con, String sSelfId, String sProjectId, String sUserId) throws SQLException, Exception {
		
		ETSSelfMail mail = new ETSSelfMail();
		StringBuffer sEmailStr = new StringBuffer("");
		StringBuffer sEmailTo = new StringBuffer("");
		
		
		try {
			
			UnbrandedProperties prop = PropertyFactory.getProperty(con,sProjectId);
			
			int iTCForSelfAssessment = ETSDatabaseManager.getTopCatId(sProjectId,Defines.SELF_ASSESSMENT_VT);
	
			String sPrimaryContactID = ETSSelfDAO.getPrimaryContact(con,sProjectId);
			String sPrimaryContactEmail = ETSUtils.getUserEmail(con,sPrimaryContactID);
			
						
			ETSSelfAssessment self = ETSSelfDAO.getSelfAssessment(con,sProjectId,sSelfId);
			ArrayList members = self.getMembers();
	
			mail.setSubject(prop.getAppName() + " Self Assessment notification: Self assesement closed.");
	
			sEmailStr.append("Self assessment: " + self.getTitle() + "\n\n");
	
			sEmailStr.append("The self assessment has been completed and closed. \n\n");
			
			sEmailStr.append("The details of the self assessment are as follows:\n\n");
	
			sEmailStr.append("  Title:          " + self.getTitle() + "\n");
			
			for (int i = 0; i < members.size(); i++) {
				ETSSelfAssessmentMember member = (ETSSelfAssessmentMember) members.get(i);
				
				if (i == 0) {
					sEmailStr.append("  Members:        " + member.getMemberName() + "\n");
				} else {
					sEmailStr.append("                  " + member.getMemberName() + "\n");
				}
				
			}
			sEmailStr.append("  Owner:          " + ETSUtils.getUsersName(con,self.getAssessmentOwner()) + " \n");
			sEmailStr.append("  Plan Owner:     " + ETSUtils.getUsersName(con,self.getPlanOwner()) + " \n");
			sEmailStr.append("  Completed by:   " + ETSUtils.getUsersName(con,sUserId) + " \n\n");
	
			sEmailStr.append("To view this self assessment, click on the following URL:\n");
			sEmailStr.append(Global.getUrl("ets/ETSProjectsServlet.wss") + "?proj=" + self.getProjectId() + "&tc=" + iTCForSelfAssessment + "&self=" + self.getSelfId() + "&linkid=" + prop.getLinkID() + "\n\n");
	
			sEmailStr.append(prop.getEmailFooter());
//			sEmailStr.append("===============================================================\n");
//			sEmailStr.append("IBM E&TS Connect enables clients of IBM Engineering and Technology Services (E&TS)\n");
//			sEmailStr.append("and IBM team members to collaborate on proposals and project information.\n");
//			sEmailStr.append("E&TS Connect provides a highly secure workspace and a comprehensive suite\n");
//			sEmailStr.append("of on demand tools that is available online 24/7.\n");
//			sEmailStr.append("===============================================================\n");
//			sEmailStr.append("This is a system-generated e-mail delivered by E&TS Connect. \n");
//			sEmailStr.append("===============================================================\n\n");
			
			mail.setMessage(sEmailStr.toString());
			
			
			
			for (int i = 0; i < members.size(); i++) {
				ETSSelfAssessmentMember member = (ETSSelfAssessmentMember) members.get(i);
				
				if (sEmailTo.length() == 0) {
					sEmailTo.append(member.getMemberEmail());
				} else {
					sEmailTo.append("," + member.getMemberEmail());
				}
			}
			
			if (sEmailTo.length() == 0) {
				sEmailTo.append(ETSUtils.getUserEmail(con,self.getPlanOwner()));
			} else {
				sEmailTo.append("," + ETSUtils.getUserEmail(con,self.getPlanOwner()));
			}
			
			if (sEmailTo.length() == 0) {
				sEmailTo.append(ETSUtils.getUserEmail(con,self.getAssessmentOwner()));
			} else {
				sEmailTo.append("," + ETSUtils.getUserEmail(con,self.getAssessmentOwner()));
			}
			
			mail.setTo(sEmailTo.toString());
			mail.setFrom(sPrimaryContactEmail);
			
			mail.setBcc("");
			mail.setCc("");
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	
		return mail;
	}
	
	public static boolean sendEmail(ETSSelfMail mail) throws Exception {
		
		boolean sent = false;
		
		try {
			
			sent = ETSUtils.sendEMail(mail.getFrom(),mail.getTo(),mail.getCc(), Global.mailHost,mail.getMessage(),mail.getSubject(),mail.getFrom());
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return sent;
	} 
}
