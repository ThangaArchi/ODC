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

import java.util.Date;
import java.util.List;

import javax.ejb.TimedObject;

import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.dealtracker.ETSTask;
import oem.edge.ets.fe.dealtracker.dao.TaskHandlerDAO;

import org.apache.commons.logging.Log;

/**
 * Bean implementation class for Enterprise Bean: ETSTaskHandler
 */
public class ETSTaskHandlerBean extends BaseSessionBean implements TimedObject {
    
    private static final Log m_pdLog = 
        EtsLogger.getLogger(ETSTaskHandlerBean.class);
    
    /**
     * @param status
     */
    public void process() {
        if (m_pdLog.isErrorEnabled()) { 
	        m_pdLog.error("*****************************************");
	        m_pdLog.error("* INSIDE PROCESS FOR ETSTaskHandlerBean *");
	        m_pdLog.error("*****************************************");
        }
        TaskHandlerDAO udDAO = new TaskHandlerDAO();
        try {
            udDAO.prepare();
            Date dtCurrent = udDAO.getCurrentTime();
            if (dtCurrent != null) {
                List lstTasks = udDAO.getAllTasksNotInStatus(Defines.GREEN);
                
                if (lstTasks.size() > 0) {
                    for(int i=0; i < lstTasks.size(); i++) {
                        ETSTask udTask = (ETSTask) lstTasks.get(i);
                        udDAO.updateTaskAsLate(udTask);
                    }
                }
            }
        }
        finally {
            udDAO.cleanup();
        }
        
    }
}
