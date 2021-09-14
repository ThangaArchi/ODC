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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSParams;
import oem.edge.ets.fe.ETSUser;
import oem.edge.ets.fe.ETSUtils;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

/**
 * @author v2srikau
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class UploadTaskAction extends Action {

    private static final String DELIM = ",";

    private static final String[] HEADERS = { "Task ID", "Title",
            "IBM Only (Y/N)", "Due date (MM/DD/YYYY)", "Status", "Owner email",
            "Action required" };

    private static final String FILE_EMPTY = "1";
    private static final String INCORRECT_HEADER = "2";
    private static final String INCORRECT_DATA = "3";

    private static final String ERR_MISSING_ELEMENTS= "Input Error: Missing data elements";
    private static final String ERR_BLANK_TITLE= "Input Error: Title cannot be left blank";
    private static final String ERR_BLANK_IBMONLY = "Input Error: IBM Only flag cannot be left blank";
    private static final String ERR_INCORRECT_IBMONLY = "Input Error: IBM Only flag must be Y / N";
    private static final String ERR_BLANK_DATE = "Input Error: Due date cannot be left blank";
    private static final String ERR_BLANK_STATUS = "Input Error: Status cannot be left blank";
    private static final String ERR_INCORRECT_STATUS = "Input Error: Status can only have values 'Not Started' / 'In Progress' / 'Complete'";
    private static final String ERR_BLANK_OWNER = "Input Error: Owner cannot be left blank";
    private static final String ERR_INCORRECT_OWNER = "Input Error: Invalid Owner ID";
    private static final String ERR_BLANK_ACTION = "Input Error: Action required cannot be left blank";
    private static final String ERR_INCORRECT_DATE = "Input Error: Invalid Due date";
    private static final String ERR_SYSTEM = "System Error: Unknown error occured while uploading this task.";
    private static final String ERR_INCORRECT_TASK_ID = "Input Error: Task ID does not exist.";

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping pdMapping, ActionForm pdForm,
            HttpServletRequest pdRequest, HttpServletResponse pdResponse)
            throws Exception {

        UploadTaskForm udForm = (UploadTaskForm) pdForm;

        String strProjectId = udForm.getProj();
        String strLinkId = udForm.getLinkid();
        String strCurrentCatId = udForm.getCc();
        String strTopCatId = udForm.getTc();
        String strURL = "ETSProjectsServlet.wss?proj="
                + strProjectId
                + "&tc="
                + strTopCatId
                + "&cc="
                + strCurrentCatId
                + "&linkid="
                + strLinkId;
        String strError = "&error=";
        String strAction = "&action=importtask";
        String strActionDone = "&action=importtask2";
        FormFile pdTaskList = udForm.getTaskList();
        if (pdTaskList == null
                || pdTaskList.getFileName() == null
                || pdTaskList.getFileSize() == 0) {
            // Means no file has been selected for upload.
            pdResponse.sendRedirect(strURL + strAction + strError + FILE_EMPTY);

            // We are returning NULL as sendRedirect will take care of
            // forwarding
            return null;
        }

        Connection pdConn = null;
        try {
            pdConn = ETSDBUtils.getConnection();
            // If we reach here means we have a file with data in it.
            BufferedReader pdInputBuffer = new BufferedReader(
                    new InputStreamReader(pdTaskList.getInputStream()));
            String strEachLine = null;
            int iLineNo = 0;
            EdgeAccessCntrl es = new EdgeAccessCntrl();
            es.GetProfile(pdResponse, pdRequest, pdConn);
            List lstTokenErrors = new ArrayList();

            // We are creating this object with just the basic values
            // so that we can use ETSDealTrackerFunctions for validations
            ETSParams params = new ETSParams();
            params.setRequest(pdRequest);
            params.setResponse(pdResponse);
            params.setEdgeAccessCntrl(es);
            params.setTopCat(Integer.parseInt(strTopCatId));
            params.setLinkId(strLinkId);
            params.setConnection(pdConn);
            
            // Get the project Details...
            
            params.setETSProj(ETSUtils.getProjectDetails(pdConn, strProjectId));
            SimpleDateFormat pdFormat = new SimpleDateFormat("MM/dd/yy");
            ETSDealTrackerFunctions functions = new ETSDealTrackerFunctions(
                    params);
            int iSuccessCount = 0;
            int iUpdateCount = 0;
            StringBuffer strTaskUpdateList = new StringBuffer("");
            while ((strEachLine = pdInputBuffer.readLine()) != null) {
                if (iLineNo == 0) {
                    if (!checkHeaders(strEachLine)) {
                        // No point moving forward as headers are wrong.
                        pdResponse.sendRedirect(strURL
                                + strAction
                                + strError
                                + INCORRECT_HEADER);
                        return null;
                    }
                }
                else {
                    // If we reach here means headers checked out fine.
                    // Now we parse the data.
                    String strValidate = null;
                    String strTokens[] = strEachLine.split(",");
                    if (strTokens.length != HEADERS.length) {
                        // Means this data line does not have all the data.
                        lstTokenErrors.add(new TaskUploadObj(strEachLine,
                                TaskUploadObj.ERR_TOKENS, ERR_MISSING_ELEMENTS));
                    }
                    else {
                        // Process this data row.
                        String strTaskID = strTokens[0];
                        String strTitle = strTokens[1];
                        String strIBMOnly = strTokens[2];
                        String strDueDate = strTokens[3];
                        String strStatus = strTokens[4];
                        String strOwnerId = strTokens[5];
                        String strActionRequired = strTokens[6];
                        String strUserId = es.gIR_USERN;
                        if (isNullOrEmpty(strTitle)) {
                            strValidate = ERR_BLANK_TITLE;
                        }
                        else if (isNullOrEmpty(strIBMOnly)) {
                            strValidate = ERR_BLANK_IBMONLY;
                        }
                        else if (!strIBMOnly.equalsIgnoreCase("N")
                                && !strIBMOnly.equalsIgnoreCase("Y")) {
                            strValidate = ERR_INCORRECT_IBMONLY;
                        }
                        else if (isNullOrEmpty(strDueDate)) {
                            strValidate = ERR_BLANK_DATE;
                        }
                        else if (isNullOrEmpty(strStatus)) {
                            strValidate = ERR_BLANK_STATUS;
                        }
                        else if (!isValidStatus(strStatus)) {
                            strValidate = ERR_INCORRECT_STATUS;
                        }
                        else if (isNullOrEmpty(strOwnerId)) {
                            strValidate = ERR_BLANK_OWNER;
                        }
                        else if (!isValidOwner(
                                strOwnerId,
                                strProjectId,
                                strIBMOnly,
                                pdConn)) {
                            strValidate = ERR_INCORRECT_OWNER;
                        }
                        else if (isNullOrEmpty(strActionRequired)) {
                            strValidate = ERR_BLANK_ACTION;
                        }

                        try {
                            if (strDueDate.length() == 9) {
                                strDueDate = "0" + strDueDate;
                            }
                            pdFormat.parse(strDueDate);
                        }
                        catch (Exception e) {
                            strValidate = ERR_INCORRECT_DATE;
                        }

                        if (!isNullOrEmpty(strValidate)) {
                            lstTokenErrors.add(new TaskUploadObj(strEachLine,
                                    TaskUploadObj.ERR_OTHERS, strValidate));
                            continue;
                        }

                        boolean bIsIBMOnly = strIBMOnly.equals("Y") ? true
                                : false;
                        ETSTask udTask = new ETSTask();
                        if (strTaskID != null && !strTaskID.equals("")) {
                            udTask.setId(Integer.parseInt(strTaskID));
                        }
                        udTask.setProjectId(strProjectId);
                        udTask.setCreatorId(es.gIR_USERN);
                        udTask.setLastUserid(es.gIR_USERN);
                        
                        // Strip "" from title
                        if (strTitle.startsWith("\"")) {
                            strTitle = strTitle.substring(0, strTitle.length()-1);
                        }
                        udTask.setTitle(strTitle);
                        udTask.setIbmOnly(bIsIBMOnly);
                        udTask.setStatus(strStatus);
                        udTask.setOwnerId(strOwnerId);
                        udTask.setCompany(es.gUSERS_COMPANY);
                        udTask.setActionRequired(strActionRequired);
                        if (strDueDate.length() < 6) {
                            strDueDate = "0" + strDueDate;
                        }

                        String strMonth = strDueDate.substring(0, 2);
                        // Month should be deducted by 1 as months start from 0
                        int iMonth = Integer.parseInt(strMonth);
                        iMonth -= 1;
                        if (iMonth > 9) {
                            strMonth = String.valueOf(iMonth);
                        }
                        else {
                            strMonth = "0" + String.valueOf(iMonth);
                        }

                        String strDay = strDueDate.substring(2, 4);
                        String strYear = strDueDate.substring(4);
                        udTask.setDueDate(strMonth, strDay, strYear);
                        String[] results = functions
                                .verifyAddTaskFields(udTask);
                        if (results[0].equals("0")) {
                            // Means there is some error
                            lstTokenErrors.add(new TaskUploadObj(strEachLine,
                                    TaskUploadObj.ERR_OTHERS, results[1]));
                        }
                        else {
                            if (strTaskID == null || strTaskID.equals("")) {
                                // Means this is a new task
                                String strTaskId = ETSDealTrackerDAO.addTask(
                                        udTask,
                                        pdConn);
                                if (strTaskId.equals("0")) {
                                    lstTokenErrors
                                            .add(new TaskUploadObj(strEachLine,
                                                    TaskUploadObj.ERR_OTHERS,
                                                    ERR_SYSTEM));
                                }
                                else {
                                    // Send task email
                                    iSuccessCount++;
                                    functions.newTaskEmail(strTaskId, udTask);
                                }
                            }
                            else {
                                // means this is an existing task
                                ETSTask udExistingTask = ETSDealTrackerDAO
                                        .getTask(
                                                strTaskID,
                                                strProjectId,
                                                "",
                                                pdConn);

                                if (udExistingTask == null) {
                                    // Means task does not exist.
                                    lstTokenErrors
                                            .add(new TaskUploadObj(strEachLine,
                                                    TaskUploadObj.ERR_OTHERS,
                                                    ERR_INCORRECT_TASK_ID));
                                    continue;
                                }
                                else {
                                    udExistingTask.setLastUserid(es.gIR_USERN);
                                    udExistingTask.setTitle(strTitle);
                                    udExistingTask.setIbmOnly(bIsIBMOnly);
                                    udExistingTask.setStatus(strStatus);
                                    udExistingTask.setOwnerId(strOwnerId);
                                    udExistingTask
                                            .setActionRequired(strActionRequired);
                                    if (ETSDealTrackerDAO.editTask(
                                            udExistingTask,
                                            pdConn)) {
                                        if (strTaskUpdateList.toString().equals("")) {
                                            strTaskUpdateList.append(udExistingTask.getId());
                                        }
                                        else {
                                            strTaskUpdateList.append(", ");
                                            strTaskUpdateList.append(udExistingTask.getId());
                                        }
                                        functions.editTaskEmail(udExistingTask);
                                        iUpdateCount++;
                                    }
                                }
                            }
                        }
                    }
                }
                iLineNo++;
            }
            // Close the input stream
            pdInputBuffer.close();
            pdRequest.getSession().setAttribute(
                    "_taskImportCount",
                    String.valueOf(iSuccessCount));
            pdRequest.getSession().setAttribute(
                    "_taskUpdateCount",
                    String.valueOf(iUpdateCount));
            pdRequest.getSession().setAttribute(
                    "_taskUpdateList", strTaskUpdateList.toString());
            if (lstTokenErrors.size() > 0) {
                pdRequest.getSession().setAttribute(
                        "_taskImportErrors",
                        lstTokenErrors);
                pdResponse.sendRedirect(strURL
                        + strActionDone
                        + strError
                        + INCORRECT_DATA);
            }

            pdResponse.sendRedirect(strURL + strActionDone);
            pdResponse.sendRedirect(strURL);
        }
        finally {
            ETSDBUtils.close(pdConn);
        }
        return null;
    }

    /**
     * @param strHeaderLine
     * @return
     */
    private boolean checkHeaders(String strHeaderLine) {
        StringTokenizer strHeaderTokens = new StringTokenizer(strHeaderLine,
                DELIM);
        boolean bIsHeaderOK = true;
        int iCounter = 0;
        if (strHeaderTokens.countTokens() != HEADERS.length) {
            bIsHeaderOK = false;
        }
        else {
            while (strHeaderTokens.hasMoreTokens()) {
                String strToken = strHeaderTokens.nextToken();
                if (!strToken.equalsIgnoreCase(HEADERS[iCounter])) {
                    bIsHeaderOK = false;
                }
                iCounter++;
            }
        }

        return bIsHeaderOK;
    }

    /**
     * @param strUserId
     * @param strProjectId
     * @param strIBMOnly
     * @return
     */
    private boolean isValidOwner(String strUserId, String strProjectId,
            String strIBMOnly, Connection pdConn) {
        boolean bIsValidOwner = false;
        boolean bIsIBMOnly = strIBMOnly.equals("Y") ? true : false;
        try {
            Vector vtUsers = ETSDatabaseManager.getProjMembersWithOutPriv(
                    strProjectId,
                    Defines.VISITOR,
                    true,
                    pdConn);
            if (bIsIBMOnly) {
                vtUsers = ETSDealTrackerCommonFuncs
                        .getIBMMembers(vtUsers, pdConn);
            }

            if (vtUsers != null && vtUsers.size() > 0) {
                for (int i = 0; i < vtUsers.size(); i++) {
                    ETSUser udUser = (ETSUser) vtUsers.get(i);
                    if (udUser.getUserId().equals(strUserId)) {
                        bIsValidOwner = true;
                    }
                }
            }
        }
        catch (Exception e) {
            bIsValidOwner = false;
        }

        return bIsValidOwner;
    }

    /**
     * @param strStatus
     * @return
     */
    private boolean isValidStatus(String strStatus) {
        return strStatus.equalsIgnoreCase(Defines.GREEN_STATUS)
                || strStatus.equalsIgnoreCase(Defines.YELLOW_STATUS)
                || strStatus.equalsIgnoreCase(Defines.RED_STATUS);
    }

    /**
     * @param str
     * @return
     */
    private boolean isNullOrEmpty(String str) {
        boolean bIsNullorEmpty = false;
        bIsNullorEmpty = (str == null) || str.trim().equals("");
        return bIsNullorEmpty;
    }
}
