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

package oem.edge.ets.fe.workflow.be;

import java.sql.SQLException;

import oem.edge.common.GenConnect;
import oem.edge.common.Global;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.workflow.timers.EventsReminder;

import org.apache.commons.logging.Log;


public class EventNotification {
	private static final Log logger = 
		EtsLogger.getLogger(EventNotification.class);

/**
 * @param status
 */
public void process() {
	
	
    if (logger.isErrorEnabled()) { 
    	logger.error("*****************************************");
    	logger.error("* INSIDE PROCESS FOR EventNotificationBean *");
    	logger.error("*****************************************");
    }
    
    GenConnect db= null;
    try
	{
    	Global.Init();
   		db= new GenConnect();
   		db.makeConn();
   		//insert last successful start time
   		DBHandler.fireUpdate(db.conn, "insert into ets.ets_admin_log values(current timestamp, 'SYSTEMID', 'AICWF', 'CRONJOB', 'EventNotifyJob','Started','')");
    	logger.debug("****Processing events****************");
    	EventsReminder eventsReminder = new EventsReminder();
    	eventsReminder.processAllEvents();
    	logger.debug("****Processed events******************");
    	
    	//insert last successful end time
    	DBHandler.fireUpdate(db.conn, "insert into ets.ets_admin_log values(current timestamp, 'SYSTEMID', 'AICWF', 'CRONJOB', 'EventNotifyJob','Ended','')");
    	
    }
    catch(SQLException sqlEx)
	{
    	logger.trace(sqlEx);
	}
    catch(Exception ex)
	{
    	logger.trace(ex);
	}
	
    
}
	public static void main(String[] args) {
		
		EventNotification en = new EventNotification();
		en.process();
	}
}
