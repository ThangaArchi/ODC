/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005                                          */
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
package oem.edge.ets.fe.acmgt.bdlg;

import java.sql.SQLException;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.ets.fe.acmgt.dao.InvMembrToWrkSpcDAO;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcTeamUtils;
import oem.edge.ets.fe.acmgt.model.UserInviteStatusModel;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;


/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class InvMemberProcDataPrep {
	
	private static Log logger = EtsLogger.getLogger(InvMemberProcDataPrep.class);
	public static final String VERSION = "1.7";

	private InvMembrToWrkSpcDAO invWrkSpcDao;

	/**
	 * 
	 */
	public InvMemberProcDataPrep() {
		super();
		invWrkSpcDao = new InvMembrToWrkSpcDAO();
		// TODO Auto-generated constructor stub
	}

	public String validateReqParams(HttpServletRequest request) {

		StringBuffer errsb = new StringBuffer();

		String assignCountry = AmtCommonUtils.getTrimStr(request.getParameter("assign_country"));

		String assignCompany = AmtCommonUtils.getTrimStr(request.getParameter("assign_company"));
		
		String sRoleId = AmtCommonUtils.getTrimStr(request.getParameter("roles"));

		if (!AmtCommonUtils.isResourceDefined(assignCompany)) {

			errsb.append("Please provide company name.");
			errsb.append("<br />");

		}

		if (!AmtCommonUtils.isResourceDefined(assignCountry)) {

			errsb.append("Please provide country.");
			errsb.append("<br />");

		}
		
		if (!AmtCommonUtils.isResourceDefined(sRoleId)) {

			errsb.append("Please provide privilege.");
			errsb.append("<br />");

		}

		return errsb.toString();

	}

	public boolean loadInviteDataFromReqParams(HttpServletRequest request, String requestorIRId, String requestorEdgeId) throws SQLException, Exception {

		return addInviteData(transformParamsToInvModel(request, requestorIRId, requestorEdgeId));
	}

	public UserInviteStatusModel transformParamsToInvModel(HttpServletRequest request, String requestorIRId, String requestorEdgeId) throws SQLException,Exception {

		UserInviteStatusModel invStatModel = new UserInviteStatusModel();
		String userId = AmtCommonUtils.getTrimStr(request.getParameter("ibmid"));
		String projectId = AmtCommonUtils.getTrimStr(request.getParameter("proj"));
		String sRoleId = AmtCommonUtils.getTrimStr(request.getParameter("roles"));

		int role = 0;

		if (AmtCommonUtils.isResourceDefined(sRoleId)) {

			role = Integer.parseInt(sRoleId);
		}

		String assignCountry = AmtCommonUtils.getTrimStr(request.getParameter("assign_country"));

		String assignCompany = AmtCommonUtils.getTrimStr(request.getParameter("assign_company"));
		
		String rolesName=invWrkSpcDao.getRolesName(role);
		
		String countryName=invWrkSpcDao.getCountryName(assignCountry);

		invStatModel.setUserId(userId);
		invStatModel.setWrkSpcId(projectId);
		invStatModel.setInviteStatus("I");
		invStatModel.setRoleId(role);
		invStatModel.setRoleName(rolesName);
		invStatModel.setRequestorId(requestorIRId);
		invStatModel.setUserCompany(assignCompany);
		invStatModel.setUserCountryCode(assignCountry);
		invStatModel.setUserCountryName(countryName);
		invStatModel.setLastUserId(requestorEdgeId);

		WrkSpcTeamUtils.printInvStatRecord(invStatModel);

		return invStatModel;

	}

	public boolean addInviteData(UserInviteStatusModel invStatModel) throws SQLException, Exception {

		return invWrkSpcDao.addMemberToInviteStatus(invStatModel);

	}
	
	public Hashtable getPrevReqParams(HttpServletRequest request,String reqEmail) {
		
		Hashtable valHash=new Hashtable();
		
		String invUsrType=AmtCommonUtils.getTrimStr(request.getParameter("invUsrType"));
		String toid=AmtCommonUtils.getTrimStr(request.getParameter("toid"));
		String woemailcc=AmtCommonUtils.getTrimStr(request.getParameter("woemailcc"));
		String ibmid=AmtCommonUtils.getTrimStr(request.getParameter("ibmid"));
		String projname=AmtCommonUtils.getTrimStr(request.getParameter("projname"));
		String woemail=AmtCommonUtils.getTrimStr(request.getParameter("woemail"));
		String projid=AmtCommonUtils.getTrimStr(request.getParameter("proj"));
		String cc=AmtCommonUtils.getTrimStr(request.getParameter("cc"));
		String tc=AmtCommonUtils.getTrimStr(request.getParameter("tc"));
		String action=AmtCommonUtils.getTrimStr(request.getParameter("action"));
		String option=AmtCommonUtils.getTrimStr(request.getParameter("option"));
		String suboption=AmtCommonUtils.getTrimStr(request.getParameter("suboption"));
		String assigncountry=AmtCommonUtils.getTrimStr(request.getParameter("assign_country"));
		String assigncompany=AmtCommonUtils.getTrimStr(request.getParameter("assign_company"));
		String roles=AmtCommonUtils.getTrimStr(request.getParameter("roles"));
		
		logger.debug("prev company in invite data proc=="+assigncountry);
		logger.debug("prev country in invite data proc=="+assigncompany);
		logger.debug("prev roles id in invite data proc=="+roles);
		
		if(!AmtCommonUtils.isResourceDefined(invUsrType)) {
			
			invUsrType="E";
		}
		
		valHash.put("invUsrType",invUsrType);
		valHash.put("toid",toid);
		valHash.put("woemailcc",woemail);
		valHash.put("reqemail",reqEmail);
		valHash.put("ibmid",ibmid);
		valHash.put("projname",projname);
		valHash.put("woemail",woemail);
		valHash.put("projid",projid);
		valHash.put("cc",cc);
		valHash.put("tc",tc);
		valHash.put("action",action);
		valHash.put("option",option);
		valHash.put("suboption",suboption);
		valHash.put("assigncountry",assigncountry);
		valHash.put("assigncompany",assigncompany);
		valHash.put("roles",roles);
			
		
		return valHash;
	}

} //end of class
