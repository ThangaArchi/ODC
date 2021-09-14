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

package oem.edge.ets.fe.dealtracker.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.dealtracker.ETSTask;

import org.apache.commons.logging.Log;

/**
 * @author v2srikau
 */
public class TaskHandlerDAO {
    
    private static final Log m_pdLog = EtsLogger.getLogger(TaskHandlerDAO.class);

    private Connection m_pdConnection;
    
    private static final String SQL_DATE = 
        "SELECT current timestamp as CURRDATE " 
        	+ "FROM ETS.ETS_PROJECTS " 
        	+ "with UR";
    
    private static final String SQL_TASKS = 
        "SELECT * FROM ETS.ETS_TASK_MAIN WHERE "
	        + "(PROJECT_ID IS NOT NULL AND PROJECT_ID != '') "
	        + "AND (SELF_ID IS NULL OR SELF_ID = '') "
	        + "AND STATUS != ? "
	        + "AND (IS_LATE IS NULL OR IS_LATE = 'N') "
	        + "AND DUE_DATE < CURRENT TIMESTAMP with UR";
    
    private static final String SQL_UPDATE_TASK =
        "UPDATE ETS.ETS_TASK_MAIN SET IS_LATE = ? WHERE PROJECT_ID = ? AND TASK_ID = ?";
    
    /**
     * 
     */
    public void prepare() {
        try {
            m_pdConnection = ETSDBUtils.getConnection();
        }
        catch(Exception e) {
            m_pdLog.error(e);
        }
    }
    
    /**
     * 
     */
    public void cleanup() {
        if (m_pdConnection != null) {
            ETSDBUtils.close(m_pdConnection);
        }
    }
    
    /**
     * @return
     */
    public Date getCurrentTime() {
        
        Date dtCurrent = null;
        try {
            Statement stmtTime = m_pdConnection.createStatement();
            
            ResultSet rsTime = stmtTime.executeQuery(SQL_DATE);
            
            if (rsTime.next()) {
                dtCurrent = rsTime.getTimestamp("CURRDATE");
            }
            
            rsTime.close();
            stmtTime.close();
        }
        catch(SQLException e) {
            e.printStackTrace();
            m_pdLog.error(e);
        }
        return dtCurrent;
    }
    
    /**
     * @param strStatus
     * @param dtCurrent
     * @return
     */
    public List getAllTasksNotInStatus(String strStatus) {
        List lstAllTasks = new ArrayList();
        try {
            PreparedStatement stmtTasks = m_pdConnection.prepareStatement(SQL_TASKS);
            stmtTasks.setString(1, strStatus);
            
            ResultSet rsTasks = stmtTasks.executeQuery();
            
            while (rsTasks.next()) {
                ETSTask udTask = new ETSTask();
                udTask.setId(rsTasks.getInt("TASK_ID"));
                udTask.setProjectId(rsTasks.getString("PROJECT_ID"));
                lstAllTasks.add(udTask);
            }
            
            rsTasks.close();
            stmtTasks.close();
        }
        catch(SQLException e) {
            e.printStackTrace();
            m_pdLog.error(e);
        }
        return lstAllTasks;
    }
    
    /**
     * @param udTask
     * @return
     */
    public boolean updateTaskAsLate(ETSTask udTask) {
        boolean bIsUpdateSuccess = true;
        try {
            PreparedStatement stmtTasks = m_pdConnection.prepareStatement(SQL_UPDATE_TASK);
            stmtTasks.setString(1, "Y");
            stmtTasks.setString(2, udTask.getProjectId());
            stmtTasks.setInt(3, udTask.getId());
            
            int iUpdateCount = stmtTasks.executeUpdate();
            
            if (iUpdateCount != 1) {
                bIsUpdateSuccess = false;
            }
            stmtTasks.close();
        }
        catch(SQLException e) {
            e.printStackTrace();
            m_pdLog.error(e);
            bIsUpdateSuccess = false;
        }
        return bIsUpdateSuccess;
    }
    /**
     * @return Returns the m_pdConnection.
     */
    public Connection getConnection() {
        return m_pdConnection;
    }
    /**
     * @param connection The m_pdConnection to set.
     */
    public void setConnection(Connection connection) {
        m_pdConnection = connection;
    }
}
