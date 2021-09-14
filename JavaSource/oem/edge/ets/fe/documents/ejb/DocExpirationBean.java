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



package oem.edge.ets.fe.documents.ejb;

import java.sql.SQLException;

import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.daemon.DocExpiryNotification;
import oem.edge.ets.fe.dealtracker.ejb.BaseSessionBean;

import org.apache.commons.logging.Log;

/**
 * Bean implementation class for Enterprise Bean: DocExpiration
 */
public class DocExpirationBean extends BaseSessionBean implements javax.ejb.TimedObject {
	private javax.ejb.SessionContext mySessionCtx;
	/**
	 * getSessionContext
	 */
	private static final Log m_pdLog = 
        EtsLogger.getLogger(DocExpirationBean.class);
    
    /**
     * @param status
     */
    public void process() {
        if (m_pdLog.isErrorEnabled()) { 
	        m_pdLog.error("*****************************************");
	        m_pdLog.error("* INSIDE PROCESS FOR DocExpirationBean *");
	        m_pdLog.error("*****************************************");
        }
        
        try
		{
        	m_pdLog.debug("****Processing expiried documents.......");
        	DocExpiryNotification docExpiryNotification = new DocExpiryNotification();
    		docExpiryNotification.processExpiryDocs();	
    		m_pdLog.debug("****Processed expiried documents.......");
        }
        catch(SQLException sqlEx)
		{
        	m_pdLog.trace(sqlEx);
		}
        catch(Exception ex)
		{
        	m_pdLog.trace(ex);
		}
        
    }
	}
