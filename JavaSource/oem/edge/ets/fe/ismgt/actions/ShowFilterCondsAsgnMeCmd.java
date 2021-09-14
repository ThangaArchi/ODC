package oem.edge.ets.fe.ismgt.actions;

import java.sql.*;
import java.util.*;
import java.io.*;

import oem.edge.common.*;
import oem.edge.amt.*;

import javax.servlet.*;
import javax.servlet.http.*;

import oem.edge.ets.fe.ismgt.model.*;
import oem.edge.ets.fe.ismgt.resources.*;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004                                          */
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
/**
 * @author v2phani
 * This class will process command of showing filter cnds,when user clicks Issues assigned to me
 */
public class ShowFilterCondsAsgnMeCmd extends FilterCommandAbsBean implements EtsIssFilterConstants{
	
	public static final String VERSION = "1.10";

	/**
	 * Constructor for ShowFilterCondsAsgnMeCmd.
	 * @param request
	 * @param response
	 * @param issobjkey
	 */
	public ShowFilterCondsAsgnMeCmd(HttpServletRequest request, HttpServletResponse response, EtsIssFilterObjectKey issobjkey) {
		super(request, response, issobjkey);
	}
	
	/**
	 * key process request method
	 */

	public int processRequest() {

		int processreq = 0;

		try {

			//set the bean details into request//

			getRequest().setAttribute("etsfilterdets", getFilterBldgBean().getFilterDetails());

			//process Request success

			processreq = 1;

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in ShowFilterCondsAsgnMeCmd", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in ShowFilterCondsAsgnMeCmd", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		}

		return processreq;

	}



}

