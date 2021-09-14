/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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

package oem.edge.ets.fe.workflow.ejb;

import java.sql.SQLException;

import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.dealtracker.ejb.BaseSessionBean;
import oem.edge.ets.fe.workflow.timers.EventsReminder;

import org.apache.commons.logging.Log;

/**
 * Bean implementation class for Enterprise Bean: DocExpiration
 */
public class EventNotificationBean extends BaseSessionBean implements
		javax.ejb.TimedObject {
	private javax.ejb.SessionContext mySessionCtx;

	/**
	 * getSessionContext
	 */
	private static final Log logger = EtsLogger
			.getLogger(EventNotificationBean.class);

	/**
	 * @param status
	 */
	public void process() {
		if (logger.isErrorEnabled()) {
			logger.error("*****************************************");
			logger.error("* INSIDE PROCESS FOR EventNotificationBean *");
			logger.error("*****************************************");
		}

		try {
			logger.debug("****Processing events****************");
			EventsReminder eventsReminder = new EventsReminder();
			eventsReminder.processAllEvents();
			
			//reminder for next MSA due date
			eventsReminder.notifyMSAMeetingDueDate();
			
			//just update workflow sattus to close
			//after one month of rating period
			//for QBR and MSA only
			eventsReminder.closeWFAfterRatingPeriod();
			
			logger.debug("****Processed events******************");
		} catch (SQLException sqlEx) {
			logger.trace(sqlEx);
		} catch (Exception ex) {
			logger.trace(ex);
		}

	}
}
