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



package oem.edge.ets.fe;

import java.sql.SQLException;

import java.io.*;
import javax.servlet.http.*;

import oem.edge.common.SysLog;
import oem.edge.amt.AmtCommonUtils;

import oem.edge.ets.fe.ismgt.helpers.EtsIssCommonSessnParams;
import oem.edge.ets.fe.ismgt.helpers.EtsIssFilterGuiUtils;
import oem.edge.ets.fe.ismgt.model.EtsPrimaryContactInfo;

/**
 * 01/24/2004
 * changed by phani to get the project contact info from session,instead of from DB, to reduce 2 qrys for
 * every page
 */

/**
 * 03/04/2004
 * changed by Navneet
 * added a method: public StringBuffer getContactBox()
 * and changed printContactBox(PrintWriter) to print the StringBuffer returned by this new method
 * also both these methods now throw Exception instead of silently failing
 */

public class ETSContact implements Serializable {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.13";

	private String projid;
	private HttpServletRequest request;
	private HttpSession session; //for filtering issues

	// ETSContact(String p_id,ETSDatabaseManager databaseManager) {
	public ETSContact(String p_id, HttpServletRequest request) {
		this.projid = p_id;
		this.request = request;
		this.session = request.getSession(true);
	}

	/**
	 * 01/24/2004
	 * changed by phani to get the project contact info from session,instead of from DB, to reduce 2 qrys for
	 * every page, folowing what has been done in Issues
	 */

	/**
	 * 03/04/2004
	 * changed by Navneet
	 * added a method: public StringBuffer getContactBox()
	 * and changed printContactBox(PrintWriter) to print the StringBuffer returned by this new method
	 * also both these methods now throw Exception instead of silently failing
	 */
	public void printContactBox(PrintWriter writer) throws Exception {
		writer.println(getContactBox());
	}

	public StringBuffer getContactBox() throws Exception {

		StringBuffer buf = new StringBuffer();
		EtsPrimaryContactInfo etsContInfo = new EtsPrimaryContactInfo();
		EtsIssFilterGuiUtils etgGuiUtil = new EtsIssFilterGuiUtils();

		try {

			//initialize the common params bean//
			EtsIssCommonSessnParams etsCommonParams =
				new EtsIssCommonSessnParams(session, projid);

			ETSProj proj = etsCommonParams.getEtsProj();
			etsContInfo = etsCommonParams.getEtsContInfo();

			buf.append(
				etgGuiUtil.getPrimaryContactModule(
					etsContInfo,
					proj.getProjectId()));

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(
				se,
				"SQL Exception in Ets contact",
				"etsuser");

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();
			}

			throw se;

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(
				ex,
				"SQL Exception in Ets contact",
				"etsuser");

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();
			}

			throw ex;

		}

		return buf;

	}

} // end of class
