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

package oem.edge.ets.fe.ismgt.actions;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.ismgt.bdlg.EtsCrRtfPopupDataPrep;
import oem.edge.ets.fe.ismgt.helpers.EtsPopUpBean;
import oem.edge.ets.fe.ismgt.model.EtsCrRtfModel;
import oem.edge.ets.fe.ismgt.resources.EtsCrActionConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsCrShowRtfDetailsCmd implements EtsIssFilterConstants,EtsCrActionConstants {

	public static final String VERSION = "1.30";
	private HttpServletRequest request = null;
	private HttpServletResponse response = null;

	/**
	 * 
	 */
	public EtsCrShowRtfDetailsCmd(HttpServletRequest request, HttpServletResponse response) {
		super();
		this.request = request;
		this.response = response;

	}

	/**
		 * key process request method
		 */

	public int processRequest() {

		int processreq = 0; //process failure
		String popHeader = "";

		try {

			String pmoId = AmtCommonUtils.getTrimStr(request.getParameter("pmoId"));

			EtsCrRtfPopupDataPrep etsCrRtfPrep = new EtsCrRtfPopupDataPrep();

			EtsCrRtfModel crRtfModel = etsCrRtfPrep.getCrRtf(pmoId);
			
			Global.println("Alias name=="+crRtfModel.getRtfAliasName());
			
			Global.println("RTF Blob str=="+crRtfModel.getRtfBlobStr());

			popHeader = crRtfModel.getRtfAliasName();

			EtsPopUpBean etsPop = new EtsPopUpBean();
			etsPop.init(popHeader, popHeader);

			request.setAttribute("etspopup", etsPop);
			request.setAttribute("crrtfmodel", crRtfModel);

			processreq = VIEWCRRTF; //process success

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsCrDShowRtfDetailsCmd", ETSLSTUSR);

			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in EtsCrDShowRtfDetailsCmd", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		}

		return processreq;

	}

} //end of class
