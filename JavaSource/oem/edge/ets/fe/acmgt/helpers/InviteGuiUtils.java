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
package oem.edge.ets.fe.acmgt.helpers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSProjectInfoBean;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.acmgt.dao.AddMembrToWrkSpcDAO;
import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class InviteGuiUtils {

	private static Log logger = EtsLogger.getLogger(InviteGuiUtils.class);
	public static final String VERSION = "1.9";

	/**
	 *
	 */
	public InviteGuiUtils() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String printInvitePage(Hashtable valHash,String errMsg) {

		StringBuffer sb = new StringBuffer();

		String sProjId = (String) valHash.get("projid");
		String topCatStr = (String) valHash.get("tc");
		String linkid = (String) valHash.get("linkid");

		String prevCompany=AmtCommonUtils.getTrimStr((String) valHash.get("assigncompany"));
		String prevCountry=AmtCommonUtils.getTrimStr((String) valHash.get("assigncountry"));
		String prevRole=AmtCommonUtils.getTrimStr((String) valHash.get("roles"));

		logger.debug("prev company in invite gui utils=="+prevCompany);
		logger.debug("prev country in invite gui utils=="+prevCountry);
		logger.debug("prev roles id in invite gui utils=="+prevRole);

		sb.append("<table cellspacing=\"1\" cellpadding=\"2\" border=\"0\" width=\"443\"> ");
		//sb.append("<tr><td  colspan=\"2\" valign=\"top\">Please assign Company, Country and privilege to the user.</td></tr>");
		sb.append("<tr valign=\"top\"><td colspan=\"2\">&nbsp;</td></tr>");
		//sb.append("<tr valign=\"top\"><td colspan=\"2\">&nbsp;</td></tr>");
		sb.append("<tr>");
		sb.append("<td  colspan=\"2\" valign=\"top\" align=\"left\">");
		sb.append("<span class=\"small\">[ Fields marked with <span class=\"ast\"><b>*</b></span> are mandatory. ]</span>");
		sb.append("</td>");
		sb.append("</tr>");

		sb.append("<tr><td colspan=\"2\"><img src=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" border=\"0\" height=\"1\" width=\"443\" alt=\"\" /></td></tr>");
		//sb.append("<tr valign=\"top\"><td colspan=\"2\">&nbsp;</td></tr>");
		sb.append("<tr><td colspan=\"2\">"+printLabel(false,true,":","IBM Export Control Regulations")+"<span class=\"small\">[</span>"+ibmReg()+"<span class=\"small\">]</span>"+"</td></tr>");
		sb.append("<tr valign=\"top\">");
		sb.append("<td colspan=\"2\">");
		sb.append("<span style=\"color:#ff3333\">" + errMsg + "</span>");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("<tr valign=\"top\"><td colspan=\"2\">&nbsp;</td></tr>");
		sb.append(printCompanyList(prevCompany));
		sb.append("<tr valign=\"top\"><td colspan=\"2\">&nbsp;</td></tr>");
		sb.append(printCountryList(prevCountry));

		sb.append("<tr><td colspan=\"2\"><img src=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" border=\"0\" height=\"1\" width=\"443\" alt=\"\" /></td></tr>");


		sb.append("<tr valign=\"top\"><td colspan=\"2\">&nbsp;</td></tr>");
		sb.append(printPrivileges(sProjId,prevRole));

		sb.append("<tr><td colspan=\"2\"><img src=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" border=\"0\" height=\"1\" width=\"443\" alt=\"\" /></td></tr>");

		//sb.append(printJobResponsibility());

		sb.append("</table>");
		sb.append("<br />");

		return sb.toString();
	}

	/**
	 *
	 * @param userProfCompany
	 * @return
	 */

	public String printCompanyList(String userProfCompany) {

		AddMembrToWrkSpcDAO wrkSpcDao = new AddMembrToWrkSpcDAO();
		StringBuffer sb = new StringBuffer();

		try {

			ArrayList compList = wrkSpcDao.getCompanyList();
			int size = 0;

			if (compList != null && !compList.isEmpty()) {

				size = compList.size();
			}


			String tempComp = "";


			sb.append("<tr>");
			sb.append("<td  width=\"150\" valign=\"top\"><span class=\"ast\"><b>*</b></span><b><label for=\"label_company\">TG SAP company name:</label></b></td>");
			sb.append("<td   valign=\"top\" ><select name=\"assign_company\" id=\"label_company\">");
			sb.append("<option value=\"\">Select company</option>");

			for (int i = 0; i < size; i++) {

				tempComp = (String) compList.get(i);


				if (userProfCompany.equals(tempComp)) {

					sb.append("<option selected=\"selected\" value=\"" + tempComp + "\">" + tempComp + "</option>");

				} else {

					sb.append("<option value=\"" + tempComp + "\">" + tempComp + "</option>");
				}

			}
			sb.append("</select></td>");
			sb.append("</tr>");

		} catch (SQLException sqlEx) {

			logger.error("SQLException in getCompanyList", sqlEx);
			sqlEx.printStackTrace();

		} catch (Exception ex) {

			logger.error("Exception in getCompanyList", ex);
			ex.printStackTrace();

		}

		return sb.toString();

	}

	/**
	 *
	 * @param prevCountryCode
	 * @return
	 */

	public String printCountryList(String prevCountryCode) {

		AddMembrToWrkSpcDAO wrkSpcDao = new AddMembrToWrkSpcDAO();
		StringBuffer sb = new StringBuffer();
		String countryCode = "";
		String countryName = "";

		try {

			ArrayList countryList = wrkSpcDao.getCountryList();
			int size = 0;

			if (countryList != null && !countryList.isEmpty()) {

				size = countryList.size();
			}

			sb.append("<tr><td><span class=\"ast\"><b>*</b></span><b><label for=\"wkspc\">Country:</label></b></td>");
			sb.append("<td><select id=\"wkspc\" name=\"assign_country\"><option name=\"compval\" value=\"\">Select country</option>");

			for (int i = 0; i < size; i = i + 2) {

				countryCode = (String) countryList.get(i);
				countryName = (String) countryList.get(i + 1);

				if (countryCode.equals(prevCountryCode)) {

					sb.append("<option name=\"compval\" selected value=\"" + countryCode + "\">" + countryName + "</option>");

				} else {

					sb.append("<option name=\"compval\" value=\"" + countryCode + "\">" + countryName + "</option>");
				}
			}
			sb.append("</td></tr>");
			sb.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");

		} catch (SQLException sqlEx) {

			logger.error("SQLException in getCountryList", sqlEx);
			sqlEx.printStackTrace();

		} catch (Exception ex) {

			logger.error("Exception in getCountrysList", ex);
			ex.printStackTrace();

		}

		return sb.toString();
	}

	public String printPrivileges(String projectId,String prevRoles) {

		StringBuffer sb = new StringBuffer();
		int prevRolesId=0;

		try {

			if(AmtCommonUtils.isResourceDefined(prevRoles)) {

				prevRolesId=Integer.parseInt(prevRoles);
			}

			sb.append("<tr>");
			sb.append("<td  width=\"150\" valign=\"top\" ><span class=\"ast\"><b>*</b></span><b>Privileges:</b></td>");
			sb.append("<td   valign=\"top\" >");

			Vector r = ETSDatabaseManager.getRolesPrivs(projectId);

			ETSProjectInfoBean projBean = ETSUtils.getProjInfoBean();

			sb.append("<table cellpadding=\"0\" cellspacing=\"0\" width=\"293\" border=\"0\">");

			for (int i = 0; i < r.size(); i++) {
				String[] rp = (String[]) r.elementAt(i);
				int roleid = (new Integer(rp[0])).intValue();
				String rolename = rp[1];
				//String privs = rp[2];
				String privids = rp[3];

				if (!(ETSDatabaseManager.doesRoleHavePriv(roleid, Defines.OWNER)) && !(ETSDatabaseManager.doesRoleHavePriv(roleid, Defines.ADMIN))) {
					sb.append("<tr>");
					sb.append("<td align=\"left\" width=\"3%\" class=\"lgray\">");
					if(prevRolesId==roleid) {

						sb.append("<input id=\"role_" + i + "\" type=\"radio\" name=\"roles\" value=\"" + roleid + "\" checked=\"checked\" />");
					}
					else {

						sb.append("<input id=\"role_" + i + "\" type=\"radio\" name=\"roles\" value=\"" + roleid + "\"  />");
					}

					sb.append("</td>");
					sb.append("<td align=\"left\" width=\"97%\" class=\"lgray\"><label for=\"role_" + i + "\">" + rolename + "</label></td>");
					sb.append("</tr>");
					sb.append("<tr>");
					sb.append("<td>&nbsp;</td>");
					sb.append("<td align=\"left\" valign=\"top\">Privileges: ");
					String priv_desc = "";
					StringTokenizer st = new StringTokenizer(privids, ",");
					Vector privs = new Vector();
					while (st.hasMoreTokens()) {
						String priv = st.nextToken();
						privs.addElement(priv);
					}
					for (int j = 0; j < privs.size(); j++) {
						String s = (String) privs.elementAt(j);
						String desc = projBean.getInfoDescription("PRIV_" + s, 0);
						if (!desc.equals("")) {
							if (!priv_desc.equals("")) {
								priv_desc = priv_desc + "; " + desc;
							} else {
								priv_desc = desc;
							}
						}
					}
					sb.append(priv_desc);
					sb.append("</td>");
					sb.append("</tr>");
					sb.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"7\" width=\"1\" alt=\"\" /></td></tr>");
				}
			}

			sb.append("</table>");
			sb.append("</td>");
			sb.append("</tr>");

		} catch (SQLException sqlEx) {

			logger.error("SQLException in printPrivileges", sqlEx);
			sqlEx.printStackTrace();

		} catch (Exception ex) {

			logger.error("Exception in printPrivileges", ex);
			ex.printStackTrace();

		}

		return sb.toString();

	}

	public String printJobResponsibility() {

		StringBuffer sb = new StringBuffer();

		sb.append("<tr>");
		sb.append("<td  width=\"150\" valign=\"top\" ><b><label for=\"label_job\">Job responsibility:</label></b></td>");
		sb.append("<td   valign=\"top\" ><input type=\"text\" id=\"label_job\" name=\"job\" value=\"\" /></td>");
		sb.append("</tr>");
		sb.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");

		return sb.toString();
	}

	/**
				 * To print the error msg
				 */

		public String printStdErrMsg(String errMsg) {

			StringBuffer sbview = new StringBuffer();

			if (AmtCommonUtils.isResourceDefined(errMsg)) {

				sbview.append("<table summary=\"\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"443\">");
				sbview.append("<tr>");
				sbview.append("<td  valign=\"top\" align=\"left\" >");
				sbview.append("<span style=\"color:#ff3333\">" + errMsg + "</span>");
				sbview.append("</td>");
				sbview.append("</tr>");
				//sbview.append("<tr><td>&nbsp;</td></tr>");
				sbview.append("</table>");
			}

			return sbview.toString();

		}

	private String printLabel (boolean isMandatory, boolean isBold, String sperator, String labelString){
			return ("<span style=\""+(isBold?"font-weight:bold":"")+"\">"+(isMandatory?"<span style=\"color:red\">*</span>":"")+""+labelString+sperator+"</span>");
		}

	public String ibmReg (){
			StringBuffer sBuff = new StringBuffer();
			sBuff.append(" <a class=\"fbox\" name=\"BLBEXPORT_HELP\" href=\"#BLBEXPORT_HELP\"");
			sBuff.append(" onclick=\"window.open('EtsIssHelpBlurbServlet.wss?OP=Blurb&BlurbOfField=EXPORT_HELP','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=350,height=450'); return false\" ");
			sBuff.append(" onkeypress=\"window.open('EtsIssHelpBlurbServlet.wss?OP=Blurb&BlurbOfField=EXPORT_HELP','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=350,height=450')\">click here for more details on export control</a>");
			return sBuff.toString();
		}



} //end of class
