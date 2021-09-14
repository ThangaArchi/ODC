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

package oem.edge.ets.fe.documents;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.ETSDoc;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.DocExpirationDate;
import oem.edge.ets.fe.documents.common.DocMetrics;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.documents.data.DocumentDAO;

import org.apache.commons.logging.Log;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

public class DisplayUserHistoryAction extends BaseDocumentAction {

	/** Stores the Logging object */
	private Log m_pdLog = EtsLogger.getLogger(DisplayUserHistoryAction.class);

	private static final String PARAM_SHOW_FILES = "showfiles";

	private static final String FWD_SUCCESS_FILES = "success_files";
	
	private static final String ERR_NO_REPORT_TYPE =
		"document.search.no.report.type";
	private static final String ERR_INVALID_DATE =
		"document.search.error.date.invalid";
	private static final String ERR_DATE_LATER =
		"document.search.error.date.later";
	private static final String ERR_START_END =
		"document.search.error.date.sequence";
	private static final String ERR_NO_HISTORY =
		"document.search.error.no.history";

	/**
	 * @see oem.edge.ets.fe.documents.BaseDocumentAction#executeAction(
	 * org.apache.struts.action.ActionMapping, 
	 * org.apache.struts.action.ActionForm, 
	 * javax.servlet.http.HttpServletRequest, 
	 * javax.servlet.http.HttpServletResponse)
	 */
	protected ActionForward executeAction(
		ActionMapping pdMapping,
		ActionForm pdForm,
		HttpServletRequest pdRequest,
		HttpServletResponse pdResponse)
		throws Exception {

		BaseDocumentForm udForm = (BaseDocumentForm) pdForm;
		String strFormContext = udForm.getFormContext();
		String strProjectId = udForm.getProj();

		ActionErrors pdErrors = validate(udForm);
		if (pdErrors.size() > 0) {
			throw new DocumentException(pdErrors);
		}

		if (udForm.getDocAction().equals("docAccess")) {
		    return pdMapping.findForward("success_docs");
		}
		
		String strSortByParam = udForm.getSortBy();
		String strSortParam = udForm.getSort();

		if (StringUtil.isNullorEmpty(strSortByParam)) {
			strSortByParam = StringUtil.EMPTY_STRING;
		}
		if (StringUtil.isNullorEmpty(strSortParam)) {
			strSortParam = StringUtil.EMPTY_STRING;
		}

		DocumentDAO udDAO = null;
		try {
			udDAO = getDAO();
			List lstAccessHistory =
				udDAO.searchAccessHistory(
					getEdgeAccess(pdRequest).gIR_USERN,
					!DocConstants.IND_YES.equals(
						udForm.getStartDate().getExpires()),
					udForm.getStartDate(),
					udForm.getEndDate(),
					strProjectId,
					strSortByParam,
					strSortParam);
			if (lstAccessHistory == null || lstAccessHistory.size() == 0) {
			    throw new DocumentException(ERR_NO_HISTORY);
			}
			udForm.setAccessHistory(lstAccessHistory);
			if (lstAccessHistory != null
				&& lstAccessHistory.size() > 0
				&& !StringUtil.isNullorEmpty(
					pdRequest.getParameter(PARAM_SHOW_FILES))) {
				List lstDocIds = new ArrayList();
				List lstAccessFiles = new ArrayList();
				for (int i = 0; i < lstAccessHistory.size(); i++) {
					DocMetrics udMetrics = (DocMetrics) lstAccessHistory.get(i);
					if (!lstDocIds
						.contains(Integer.toString(udMetrics.getDocId()))) {
						lstDocIds.add(Integer.toString(udMetrics.getDocId()));
						ETSDoc udDoc =
							udDAO.getDocByIdAndProject(
								udMetrics.getDocId(),
								strProjectId);
						if (udDoc != null) {
							udMetrics.setDocFiles(udDoc.getDocFiles());
						}
						lstAccessFiles.add(udMetrics);
					}
				}

				lstAccessHistory.clear();
				lstAccessHistory.addAll(lstAccessFiles);
				return pdMapping.findForward(FWD_SUCCESS_FILES);
			}
		} catch (SQLException e) {
			m_pdLog.error(e);
			throw e;
		} finally {
			super.cleanup(udDAO);
		}

		return pdMapping.findForward(FWD_SUCCESS);
	}

	/**
	 * @param udForm
	 * @param bIsUpdateDocProp
	 * @return
	 */
	private ActionErrors validate(BaseDocumentForm udForm)
		throws DocumentException {
		ActionErrors pdErrors = new ActionErrors();

		if (StringUtil.isNullorEmpty(udForm.getDocAction())) {
			pdErrors.add(
					DocConstants.MSG_USER_ERROR,
					new ActionMessage(ERR_NO_REPORT_TYPE));
		}
		// Validate Date if search range checkbox is checked
		DocExpirationDate udDate = udForm.getStartDate();
		boolean bIsDateSelected =
			!StringUtil.isNullorEmpty(udDate.getExpires());

		if (!bIsDateSelected) {
			// Start Date should be before current date
			// End Date should be before/on current date
			// End Date should be after/same as Start Date

			boolean bHasErrors = false;
			String strDateCheck = isCorrectDate(udDate);
			if (!StringUtil.isNullorEmpty(strDateCheck)) {
				pdErrors.add(
					DocConstants.MSG_USER_ERROR,
					new ActionMessage(strDateCheck, "From Date"));
				bHasErrors = true;
			}
			strDateCheck = isCorrectDate(udForm.getEndDate());
			if (!StringUtil.isNullorEmpty(strDateCheck)) {
				// For To Date we donot need Later than current date check
				if (!strDateCheck.equals(ERR_DATE_LATER)) {
					pdErrors.add(
						DocConstants.MSG_USER_ERROR,
						new ActionMessage(strDateCheck, "To Date"));
					bHasErrors = true;
				}
			}
			if (!bHasErrors) {
				// Check for 3rd criteria
				if (udDate.getDate() > udForm.getEndDate().getDate()) {
					pdErrors.add(
						DocConstants.MSG_USER_ERROR,
						new ActionMessage(
							ERR_START_END,
							"To Date",
							"From Date"));
				}
			}
		}

		return pdErrors;
	}

	/**
	 * @param udDate
	 * @return
	 */
	private String isCorrectDate(DocExpirationDate udDate)
		throws DocumentException {
		String strDateCheck = null;
		String strDay = udDate.getDay();
		String strMonth = udDate.getMonth();
		String strYear = udDate.getYear();

		if (StringUtil.isNullorEmpty(strDay)
			|| StringUtil.isNullorEmpty(strMonth)
			|| StringUtil.isNullorEmpty(strYear)) {
			//Basic check failed. None of the date elements can be empty	
			strDateCheck = ERR_INVALID_DATE;
		} else {
			int iDay = Integer.parseInt(strDay);
			int iMonth = Integer.parseInt(strMonth);
			int iYear = Integer.parseInt(strYear);

			if (iDay == -1 || iMonth == -1 || iYear == -1) {
				strDateCheck = ERR_INVALID_DATE;
			} else {
				Calendar pdCalendar = Calendar.getInstance();
				pdCalendar.set(Calendar.YEAR, iYear);
				pdCalendar.set(Calendar.MONTH, iMonth);
				int iMaxDate =
					pdCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				int iMinDate =
					pdCalendar.getActualMinimum(Calendar.DAY_OF_MONTH);

				if ((iMinDate <= iDay) && (iDay <= iMaxDate)) {
					Date dtCurrent = new Date();
					if (udDate.getDate() > dtCurrent.getTime()) {
						strDateCheck = ERR_DATE_LATER;
					}
				} else {
					strDateCheck = ERR_INVALID_DATE;
				}
			}
		}
		return strDateCheck;
	}
}
