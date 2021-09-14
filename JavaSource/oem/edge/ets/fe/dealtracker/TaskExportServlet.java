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

package oem.edge.ets.fe.dealtracker;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSParams;
import oem.edge.ets.fe.ETSProj;

/**
 * @author v2srikau
 */
public class TaskExportServlet extends HttpServlet {

    private static final String EXPORT_FILE_NAME = "tasklist.csv";

    private static final String SEL_TEMPLATE_ONLY = "T";

    private static final String SEL_ALL_TASKS = "A";

    private static final String SEL_TASKS_BY_STATUS = "S";

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Cache-Control", null);
        response.setHeader("Content-Disposition", "attachment; filename="
                .concat(EXPORT_FILE_NAME));
        response.setHeader("Content-Type", "application/octet-stream");
        StringBuffer strExportBuffer = new StringBuffer("");
        Connection pdConnection = null;
        try {
            pdConnection = ETSDBUtils.getConnection();
            ETSParams parameters = new ETSParams();
            parameters.setRequest(request);
            parameters.setResponse(response);
            boolean user_external = false;
            EdgeAccessCntrl es = new EdgeAccessCntrl();
            es.GetProfile(response, request);
            if (!(es.gDECAFTYPE.trim()).equalsIgnoreCase("I")) {
                user_external = true;
            }
            parameters.setEdgeAccessCntrl(es);
            parameters.setTopCat(Integer.parseInt(ETSDealTrackerCommonFuncs
                    .getParameter(request, "tc")));

            String strProjectID = ETSDealTrackerCommonFuncs.getParameter(
                    request,
                    "proj");

            ETSProj udProj = null;

            try {
                udProj = ETSDatabaseManager.getProjectDetails(
                        pdConnection,
                        strProjectID);
            }
            catch (Exception e) {
                e.printStackTrace(System.out);
                udProj = new ETSProj();
                udProj.setProjectId(strProjectID);
            }
            parameters.setETSProj(udProj);
            parameters.setConnection(pdConnection);

            ETSDealTrackerFunctions funcs = new ETSDealTrackerFunctions(
                    parameters);
            ETSDealTrackerResultObj o = new ETSDealTrackerResultObj();
            o = funcs.getDashboardTasks(o, null, user_external, false);
            Vector vtTasks = o.getResultTasks();
            appendHeader(strExportBuffer);
            String strSelectionCriteria = request.getParameter("selcriteria");
            if (!SEL_TEMPLATE_ONLY.equals(strSelectionCriteria)) {
                boolean bIsByStatus = SEL_TASKS_BY_STATUS.equals(strSelectionCriteria);
                String strStatus[] = request.getParameterValues("taskstatus");
                if (vtTasks != null && vtTasks.size() > 0) {
                    SimpleDateFormat pdFormat = new SimpleDateFormat("MM/dd/yyyy");
                    for (int i = 0; i < vtTasks.size(); i++) {
                        ETSTask task = (ETSTask) vtTasks.get(i);

                        if (bIsByStatus && !isStatusPresent(task.getStatus(), strStatus)) {
                            continue;
                        }
                        
                        strExportBuffer.append(task.getId());
                        strExportBuffer.append(",");

                        strExportBuffer.append("\"");
                        strExportBuffer.append(task.getTitle());
                        strExportBuffer.append("\"");
                        strExportBuffer.append(",");

                        strExportBuffer.append((task.getIbmOnly() == '1') ? "Y" : "N");
                        strExportBuffer.append(",");

                        strExportBuffer.append(pdFormat.format(new Date(task
                                .getDueDate())));
                        strExportBuffer.append(",");

                        strExportBuffer.append(task.getStatusString());
                        strExportBuffer.append(",");

                        strExportBuffer.append(task.getOwnerId());
                        strExportBuffer.append(",");

                        strExportBuffer.append(task.getActionRequired());

                        if (i < (vtTasks.size() - 1)) {
                            strExportBuffer.append("\n");
                        }
                    }
                }
            }
            OutputStream responseOut = response.getOutputStream();
            responseOut.write(strExportBuffer.toString().getBytes());
        }
        catch (SQLException e) {
            e.printStackTrace(System.out);
        }
        catch (IOException e) {
            e.printStackTrace(System.out);
        }
        catch (Exception e) {
            e.printStackTrace(System.out);
        }
        finally {
            ETSDBUtils.close(pdConnection);
        }
    }

    /**
     * @param strStatus
     * @param strSelect
     * @return
     */
    private boolean isStatusPresent(String strStatus, String []strSelect) {
        boolean bIsPresent = false;
        if (strSelect != null && strSelect.length > 0) {
            for(int i=0; i < strSelect.length; i++) {
                if (strSelect[i].equals(strStatus)) {
                    bIsPresent = true;
                    break;
                }
            }
        }
        return bIsPresent;
    }
    
    /**
     * @param strExportBuffer
     */
    private void appendHeader(StringBuffer strExportBuffer) {
        strExportBuffer.append("Task ID");
        strExportBuffer.append(",");
        strExportBuffer.append("Title");
        strExportBuffer.append(",");
        strExportBuffer.append("IBM Only (Y/N)");
        strExportBuffer.append(",");
        strExportBuffer.append("Due date (MM/DD/YYYY)");
        strExportBuffer.append(",");
        strExportBuffer.append("Status");
        strExportBuffer.append(",");
        strExportBuffer.append("Owner email");
        strExportBuffer.append(",");
        strExportBuffer.append("Action required");
        strExportBuffer.append("\n");
    }
}
