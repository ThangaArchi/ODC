/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004-2004                                     */
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.ismgt.dao.ETSIssuesManager;
import oem.edge.ets.fe.ismgt.dao.EtsCrPmoDAO;
import oem.edge.ets.fe.ismgt.dao.IssueInfoDAO;
import oem.edge.ets.fe.ismgt.helpers.EmailFormatUtils;
import oem.edge.ets.fe.ismgt.model.ETSIssue;
import oem.edge.ets.fe.ismgt.model.EtsIssProjectMember;
import oem.edge.ets.fe.ismgt.model.RemindIssueActionModel;
import oem.edge.ets.fe.ismgt.resources.RemindIssueActionConstants;
import oem.edge.ets.fe.ismgt.resources.RemindIssueResource;

import org.apache.commons.logging.Log;
/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RemindIssueBdlg implements RemindIssueActionConstants {

	public static final String VERSION = "1.4";

	private static Log logger = EtsLogger.getLogger(RemindIssueBdlg.class);

	private IssueInfoDAO infoDao = null;
	private EtsCrPmoDAO pmoDao = null;

	/**
	 * 
	 */
	public RemindIssueBdlg() {
		super();
		if (!Global.loaded) {

			Global.Init();
		}

		infoDao = new IssueInfoDAO();
		pmoDao = new EtsCrPmoDAO();
		// TODO Auto-generated constructor stub
	}

	/**
		 * 
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public List getRemindIssueRecs() throws SQLException, Exception {

		List remindList = new ArrayList();

		//get the remindjob props
		HashMap propMap = RemindIssueResource.getInstance().getRemindPropMap();

		//get the remind List to be processed
		remindList = infoDao.getRemindIssueRecs("", propMap);

		return remindList;

	}

	/**
			 * 
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	public List getRemindPmoIssueRecs() throws SQLException, Exception {

		List remindList = new ArrayList();

		//get the remindjob props
		HashMap propMap = RemindIssueResource.getInstance().getRemindPropMap();

		//get the remind List to be processed
		remindList = pmoDao.getRemindPmoIssueRecs("", propMap);

		return remindList;

	}

	/**
	 * 
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public void sendRemindEmails(List remindList) {

		//get the remindjob props
		HashMap propMap = RemindIssueResource.getInstance().getRemindPropMap();
		
		
		//v2sagar this needs to be changed ,should be speicific to AIC or ETS
		String mailFrom=""; 
		mailFrom = AmtCommonUtils.getTrimStr((String) propMap.get("remind.mail.from"));
		
		String mailDetour = AmtCommonUtils.getTrimStr((String) propMap.get("remind.mail.route.detour"));
		String detourEmailId = AmtCommonUtils.getTrimStr((String) propMap.get("remind.mail.detour.dev.emailid"));

		int remsize = 0;

		if (remindList != null && !remindList.isEmpty()) {

			remsize = remindList.size();
		}

		logger.debug("reminder email rec size==" + remsize);

		String edgeProblemId = "";
		String ownerUserId = "";
		String ownerEmailId = "";
		String wsOwnerEmailId = "";
		ETSIssue issue = new ETSIssue();
		EtsIssProjectMember projMem = new EtsIssProjectMember();
		EmailFormatUtils emailUtils = new EmailFormatUtils();
		int mailcount = 0;
		int maxcount = 0;
		String severity = "";
		boolean mailsent = false;
		
		String projectType="ETS";

		for (int i = 0; i < remsize; i++) {

			RemindIssueActionModel remindModel = (RemindIssueActionModel) remindList.get(i);
			edgeProblemId = remindModel.getProblemId();
			issue = remindModel.getIssue();
			severity = issue.severity;
			System.out.println("Remind Job--->>>Project Id in sendRemindEmails-->>"+issue.ets_project_id);			
			projectType=EmailFormatUtils.getProjectType(issue.ets_project_id.trim());
			projectType=projectType.trim();
			System.out.println("Remind Job--->>>projectType in sendRemindEmails-->>"+projectType);
			
			if(projectType.equalsIgnoreCase("AIC"))
				mailFrom="jeetrao@us.ibm.com";
			else
				mailFrom="etsadmin@us.ibm.com";
			
			ownerUserId = ETSIssuesManager.getOwnerUserId(issue);
			ownerEmailId = ETSIssuesManager.getOwnerEmailId(issue);
			projMem = remindModel.getProjMem();
			wsOwnerEmailId = projMem.getUserEmail();
			mailcount = remindModel.getMailcount();
			maxcount = getMaxSentCount(propMap, severity);

			logger.debug("record iii===" + i);
			logger.debug("edgeProblemId===" + edgeProblemId);
			logger.debug("severity===" + severity);
			logger.debug("ownerId==" + ownerEmailId);
			logger.debug("wsOwnerEmailId==" + wsOwnerEmailId);
			logger.debug("mailcount==" + mailcount);
			logger.debug("maxcount==" + maxcount);
			logger.debug("mailDetour===" + mailDetour);

			if (mailDetour.equals("Y")) {

				ownerEmailId = detourEmailId;
				wsOwnerEmailId = detourEmailId;
			}

			if (mailcount < maxcount) {

				//catch any exception while sending emails and continue

				try {

					mailsent = ETSUtils.sendEMail(mailFrom, ownerEmailId, wsOwnerEmailId, Global.mailHost, emailUtils.generateEmailContent(issue), emailUtils.generateEmailSubjectForRemind(issue), "");

				} catch (Exception ex) {

					logger.error("Exception in sending email for " + ownerEmailId + "/" + wsOwnerEmailId + "", ex);
					mailsent = false;
				}

				if (mailsent) {

					mailcount = mailcount + 1;

					//catch any exception while logging and continue

					try {

						infoDao.insertIntoRemindLog(edgeProblemId, ownerUserId, mailcount);

					} catch (SQLException sqlEx) {

						logger.error("SQL Exception while inserting remind log", sqlEx);

					} catch (Exception ex) {

						logger.error("general Exception while inserting remind log", ex);

					}

				} //end of mailsent true

			} //end of checking max count

			logger.debug("mailsent==" + mailsent);
			logger.debug("############################");
		} //end of main loop

	}

	/**
		 * 
		 * @return
		 * @throws SQLException
		 * @throws Exception
		 */

	public void sendRemindPMOEmails(List remindList) {

		//get the remindjob props
		HashMap propMap = RemindIssueResource.getInstance().getRemindPropMap();

		String mailFrom = AmtCommonUtils.getTrimStr((String) propMap.get("remind.mail.from"));
		String mailDetour = AmtCommonUtils.getTrimStr((String) propMap.get("remind.mail.route.detour"));
		String detourEmailId = AmtCommonUtils.getTrimStr((String) propMap.get("remind.mail.detour.dev.emailid"));

		int remsize = 0;

		if (remindList != null && !remindList.isEmpty()) {

			remsize = remindList.size();
		}

		logger.debug("reminder email rec size==" + remsize);

		String edgeProblemId = "";
		String ownerUserId = "";
		String ownerEmailId = "";
		String wsOwnerEmailId = "";
		ETSIssue issue = new ETSIssue();
		EtsIssProjectMember projMem = new EtsIssProjectMember();
		EmailFormatUtils emailUtils = new EmailFormatUtils();
		int mailcount = 0;
		int maxcount = 0;
		String severity = "";
		boolean mailsent = false;
		
		//v2sagar
		String projectType="ETS";

		for (int i = 0; i < remsize; i++) {

			RemindIssueActionModel remindModel = (RemindIssueActionModel) remindList.get(i);
			edgeProblemId = remindModel.getProblemId();
			issue = remindModel.getIssue();
			severity = issue.severity;
			ownerUserId = pmoDao.getOwnerUserId(issue);
			ownerEmailId = pmoDao.getOwnerEmailId(issue);
			projMem = remindModel.getProjMem();
			wsOwnerEmailId = projMem.getUserEmail();
			mailcount = remindModel.getMailcount();
			maxcount = getMaxSentCount(propMap, severity);
			
			System.out.println("Remind Job--->>>Project Id in sendRemindPMOEmails-->>"+issue.ets_project_id);			
			projectType=EmailFormatUtils.getProjectType(issue.ets_project_id.trim());
			projectType=projectType.trim();
			System.out.println("Remind Job--->>>projectType in sendRemindPMOEmails-->>"+projectType);

			
			if(projectType.equalsIgnoreCase("AIC"))
				mailFrom="jeetrao@us.ibm.com";
			else
				mailFrom="etsadmin@us.ibm.com";

			logger.debug("record iii===" + i);
			logger.debug("edgeProblemId===" + edgeProblemId);
			logger.debug("severity===" + severity);
			logger.debug("ownerId==" + ownerEmailId);
			logger.debug("wsOwnerEmailId==" + wsOwnerEmailId);
			logger.debug("mailcount==" + mailcount);
			logger.debug("maxcount==" + maxcount);
			logger.debug("mailDetour===" + mailDetour);

			if (mailDetour.equals("Y")) {

				ownerEmailId = detourEmailId;
				wsOwnerEmailId = detourEmailId;
			}

			if (mailcount < maxcount) {

				//catch any exception while sending emails and continue

				try {

					mailsent = ETSUtils.sendEMail(mailFrom, ownerEmailId, wsOwnerEmailId, Global.mailHost, emailUtils.generateEmailContent(issue), emailUtils.generateEmailSubjectForRemind(issue), "");

				} catch (Exception ex) {

					logger.error("Exception in sending email for " + ownerEmailId + "/" + wsOwnerEmailId + "", ex);
					mailsent = false;
				}

				if (mailsent) {

					mailcount = mailcount + 1;

					//catch any exception while logging and continue

					try {

						infoDao.insertIntoRemindLog(edgeProblemId, ownerUserId, mailcount);

					} catch (SQLException sqlEx) {

						logger.error("SQL Exception while inserting remind log", sqlEx);

					} catch (Exception ex) {

						logger.error("general Exception while inserting remind log", ex);

					}

				} //end of mailsent true

			} //end of checking max count

			logger.debug("mailsent==" + mailsent);
			logger.debug("############################");
		} //end of main loop

	}

	/**
	 * 
	 * @param propMap
	 * @param severity
	 * @return
	 */

	public int getMaxSentCount(HashMap propMap, String severity) {

		int maxcount = 0;

		String strMaxCount = (String) propMap.get(REMIND_COUNT_STR + severity);

		if (AmtCommonUtils.isResourceDefined(strMaxCount)) {

			maxcount = Integer.parseInt(strMaxCount);
		}

		return maxcount;
	}

} //end of class
