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

package oem.edge.ets.fe.dealtracker.startup;

import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.naming.InitialContext;

import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.dealtracker.ejb.BaseSessionBean;
import oem.edge.ets.fe.dealtracker.ejb.ETSBaseHome;
import oem.edge.ets.fe.dealtracker.ejb.ETSBaseTimer;

import org.apache.commons.logging.Log;

/**
 * Bean implementation class for Enterprise Bean: ETSTaskStartup
 */
public class ETSTaskStartupBean extends BaseSessionBean {
    
    private static final Log m_pdLog = EtsLogger.getLogger(ETSTaskStartupBean.class);
    
    /**
     * @return
     */
    public boolean start() {
        boolean result = true;
        if (m_pdLog.isErrorEnabled()) { 
	        m_pdLog.error("*****************************************");
	        m_pdLog.error("* INSIDE START                          *");
	        m_pdLog.error("*****************************************");
        }

        StringTokenizer strHandlers = null;
        StringTokenizer strJNDINames = null;
        StringTokenizer strTimerNames = null;
        StringTokenizer strTimerValues = null;
        try {
            InitialContext ctx = new InitialContext();
                ResourceBundle pdResources =
            		ResourceBundle.getBundle("oem.edge.ets.fe.ets");
            strHandlers = new StringTokenizer(pdResources.getString("ets.job.handlers"),",");
            strJNDINames = new StringTokenizer(pdResources.getString("ets.job.handlers.jndinames"),",");
            strTimerNames = new StringTokenizer(pdResources.getString("ets.job.timers"),",");
            strTimerValues = new StringTokenizer(pdResources.getString("ets.job.repeat.minutes"),",");
            
            while (strHandlers.hasMoreTokens()) {
                String strHandler = strHandlers.nextToken();
                String strJNDIName = strJNDINames.nextToken();
                String strTimerName = strTimerNames.nextToken();
                String strTimerMinutes = strTimerValues.nextToken();
    	        m_pdLog.error("*****************************************");
    	        m_pdLog.error("* STARTING TIMER FOR:" + strHandler + ":" + strJNDIName + ":" + strTimerName + ":" + strTimerMinutes + "*");
    	        m_pdLog.error("*****************************************");
                
    	        ETSBaseHome pdHome = (ETSBaseHome) ctx.lookup(strJNDIName); 
                ETSBaseTimer timedObject = pdHome.create();
                timedObject.cancelTimer(strTimerName);
                
                timedObject.createTimer(
                        strTimerName, Integer.parseInt(strTimerMinutes)*60*1000);
        }
        }
        catch(Exception e) {
            e.printStackTrace(System.err);
            m_pdLog.error("Cannot startup ETS handlers as properties are missing");
        }

        return result;
    }

    /**
     * 
     */
    public void stop() {
        if (m_pdLog.isErrorEnabled()) { 
	        m_pdLog.error("*****************************************");
	        m_pdLog.error("* INSIDE stop                           *");
	        m_pdLog.error("*****************************************");
        }
        try {
            InitialContext ctx = new InitialContext();
            ResourceBundle pdResources =
        		ResourceBundle.getBundle("oem.edge.ets.fe.ets");
            StringTokenizer strHandlers = new StringTokenizer(pdResources.getString("ets.job.handlers"),",");
            StringTokenizer strJNDINames = new StringTokenizer(pdResources.getString("ets.job.handlers.jndinames"),",");
            StringTokenizer strTimerNames = new StringTokenizer(pdResources.getString("ets.job.timers"),",");
            StringTokenizer strTimerValues = new StringTokenizer(pdResources.getString("ets.job.repeat.minutes"),",");
            
            while (strHandlers.hasMoreTokens()) {
                String strHandler = strHandlers.nextToken();
                String strJNDIName = strJNDINames.nextToken();
                String strTimerName = strTimerNames.nextToken();
                String strTimerMinutes = strTimerValues.nextToken();
    	        m_pdLog.error("*****************************************");
    	        m_pdLog.error("* STOPPING TIMER FOR:" + strHandler + ":" + strJNDIName + ":" + strTimerName + ":" + strTimerMinutes + "*");
    	        m_pdLog.error("*****************************************");
            
    	        ETSBaseHome pdHome = (ETSBaseHome) ctx.lookup(strJNDIName); 
                ETSBaseTimer timedObject = pdHome.create();
                timedObject.cancelTimer(strTimerName);
	    }
	    }
        catch(Exception e) {
            e.printStackTrace(System.err);
	        m_pdLog.error(e);
	    }
	    }
    
    /* (non-Javadoc)
     * @see oem.edge.ets.fe.dealtracker.ejb.BaseSessionBean#process()
     */
    public  void process() {
        // DO NOTHING METHOD
    }
}
