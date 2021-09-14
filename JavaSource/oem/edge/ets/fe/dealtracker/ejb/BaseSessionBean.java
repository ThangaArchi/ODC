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

package oem.edge.ets.fe.dealtracker.ejb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.ejb.Timer;
import javax.ejb.TimerHandle;
import javax.ejb.TimerService;

import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

/**
 * @author v2srikau
 */
public abstract class BaseSessionBean implements javax.ejb.SessionBean {

    private static final Log m_pdLog = EtsLogger.getLogger(BaseSessionBean.class);
    private javax.ejb.SessionContext mySessionCtx;
    
    /**
     * getSessionContext
     */
    public javax.ejb.SessionContext getSessionContext() {
        return mySessionCtx;
    }
    /**
     * setSessionContext
     */
    public void setSessionContext(javax.ejb.SessionContext ctx) {
        mySessionCtx = ctx;
    }
    /**
     * ejbCreate
     */
    public void ejbCreate() throws javax.ejb.CreateException {
    }
    /**
     * ejbActivate
     */
    public void ejbActivate() {
    }
    /**
     * ejbPassivate
     */
    public void ejbPassivate() {
    }
    /**
     * ejbRemove
     */
    public void ejbRemove() {
    }

    /**
     * @param info
     * @param duration
     * @return
     */
    public TimerHandle createTimer(String info, long duration) {
        m_pdLog.debug("createTimer()");
        TimerService timerService = getSessionContext().getTimerService();

        Date dtCurrentTime = new Date();
        
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.DATE, 1);

        Date dtFirstExpiration = c.getTime();

        Timer timer = timerService.createTimer(dtFirstExpiration, duration, info);
        return timer.getHandle();
    }
    
    /**
     * @return
     */
    public Collection getTimers() {
        m_pdLog.debug("getTimers()");
        TimerService timerService = getSessionContext().getTimerService();
        Collection timers = timerService.getTimers();
        ArrayList infos = new ArrayList();
        for (Iterator iter=timers.iterator(); iter.hasNext(); ) {
            Timer timer = (Timer) iter.next();
            infos.add(timer.getInfo());
            m_pdLog.debug(" timer: ["+timer.getInfo()+ "] : ["+timer+"]");
        }
        return infos;
    }
    
    /**
     * @param info
     * @return
     */
    public Timer getTimer(String info) {
        Timer timer = null;
        TimerService timerService = getSessionContext().getTimerService();
        Collection timers = timerService.getTimers();
        for (Iterator iter=timers.iterator(); timer == null && iter.hasNext(); ) {
            Timer t = (Timer) iter.next();
            // is this the timer we are interested in
            Object i = t.getInfo();
            if (info instanceof String) {
                if (i.equals(info)) {
                    timer = t;
                }
            }
        }
        return timer;
    }

    /**
     * @param handle
     */
    public void cancelTimer(String handle) {
        m_pdLog.debug("cancelTimer("+handle+")");
        Timer timer = getTimer(handle);
        if (timer != null) {
            timer.cancel();
            m_pdLog.debug(" cancelled "+handle);
        } else {
            m_pdLog.debug(" "+handle+" not found.");
        }
    }
    
    /* (non-Javadoc)
     * @see javax.ejb.TimedObject#ejbTimeout(javax.ejb.Timer)
     */
    public void ejbTimeout(Timer timer) {
        if (m_pdLog.isErrorEnabled()) { 
	        m_pdLog.error("*****************************************");
	        m_pdLog.error("* INSIDE ejbTimeout of BaseSessionBean  *");
	        m_pdLog.error("*****************************************");
        }
        process();
    }

    /**
     * 
     */
    public abstract void process();
}
