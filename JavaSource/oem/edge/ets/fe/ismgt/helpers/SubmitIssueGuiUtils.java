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
package oem.edge.ets.fe.ismgt.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.ismgt.dao.EtsIssueAddFieldsDAO;
import oem.edge.ets.fe.ismgt.model.EtsCustFieldInfoModel;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssProbInfoUsr1Model;
import oem.edge.ets.fe.ismgt.model.EtsIssUserRolesModel;
import oem.edge.ets.fe.ismgt.model.EtsIssueAddFieldsBean;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SubmitIssueGuiUtils {

	public static final String VERSION = "1.1";
	private EtsIssFilterGuiUtils fltrGuiUtils;
	private EtsIssCommonGuiUtils comGuiUtils;

	/**
	 * 
	 */
	public SubmitIssueGuiUtils() {
		super();
		this.fltrGuiUtils = new EtsIssFilterGuiUtils();
		this.comGuiUtils = new EtsIssCommonGuiUtils();
	}

	/**
			 * This method will print submitter details
			 * 
			 */

	public String printIssueDetails(EtsIssProbInfoUsr1Model usr1Model) {

		StringBuffer sb = new StringBuffer();
		String severity = "";

		try {

			ArrayList prevSevList = usr1Model.getPrevProbSevList();

			if (prevSevList != null && !prevSevList.isEmpty()) {

				severity = AmtCommonUtils.getTrimStr((String) prevSevList.get(0));

			}

			//		remove like 1-,2- from severity 	
			int inx = severity.indexOf("-");

			if (inx != -1) {

				severity = severity.substring(inx + 1);

			}

			String probTitle = AmtCommonUtils.getTrimStr(usr1Model.getProbTitle());
			String probDesc = comGuiUtils.formatDescStr(AmtCommonUtils.getTrimStr(usr1Model.getProbDesc()));
			ArrayList prevProbTypeList = usr1Model.getPrevProbTypeList();
			String issueType = AmtCommonUtils.getTrimStr(usr1Model.getIssueType());

			String curIssueCustName = AmtCommonUtils.getTrimStr(usr1Model.getCustName());
			String curIssueCustEmail = AmtCommonUtils.getTrimStr(usr1Model.getCustEmail());
			String curIssueCustPhone = AmtCommonUtils.getTrimStr(usr1Model.getCustPhone());
			String curIssueCustCompany = AmtCommonUtils.getTrimStr(usr1Model.getCustCompany());

			sb.append("<table summary=\"submitter profile\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n");
			sb.append("<tr><td colspan=\"4\">&nbsp;</td></tr>\n");
			sb.append("<tr >\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"25%\"><b>Submitter</b>:</td>\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"35%\">" + curIssueCustName + "</td>\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"15%\"><b>Company</b>:</td>\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"25%\">" + curIssueCustCompany + "</td>\n");
			sb.append("</tr>\n");
			//sb.append("<tr><td colspan=\"4\">&nbsp;</td></tr>\n");

			sb.append("<tr >\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"25%\"><b>E-mail</b>:</td>\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"35%\">" + curIssueCustEmail + "</td>\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"15%\"><b>Phone</b>:</td>\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"25%\">" + curIssueCustPhone + "</td>\n");
			sb.append("</tr>\n");

			sb.append("<tr><td colspan=\"4\">&nbsp;</td></tr>\n");

			sb.append("<tr >\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"25%\"><b>Severity</b>:</td>\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"35%\">" + severity + "</td>\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"15%\"><b>Title</b>:</td>\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"25%\">" + probTitle + "</td>\n");
			sb.append("</tr>\n");

			sb.append("<tr >\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"25%\"><b>Issue Type</b>:</td>\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"75%\" colspan=\"3\" >" + issueType + "</td>\n");
			sb.append("</tr>\n");

			sb.append("<tr >\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"25%\"><b>Description</b>:</td>\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"75%\" colspan=\"3\"><pre>" + probDesc + "</pre></td>\n");
			sb.append("</tr>\n");

			sb.append("</table>\n");

		} catch (Exception ex) {

			ex.printStackTrace();
		}

		return sb.toString();
	}

	/**
	 * 
	 * @param etsIssObjKey
	 * @param usr1InfoModel
	 * @param propMap
	 * @return
	 */
	public String printCustomFieldCntrl(EtsIssObjectKey etsIssObjKey) {

		StringBuffer sbview = new StringBuffer();
		StringBuffer sbCustFieldId = new StringBuffer();
		String loginUserIrId = etsIssObjKey.getEs().gIR_USERN;
		String etsprojid = etsIssObjKey.getProj().getProjectId();
		EtsIssUserRolesModel usrRolesModel = etsIssObjKey.getUsrRolesModel();
		EtsIssueAddFieldsDAO addFieldsDAO = new EtsIssueAddFieldsDAO();
		EtsIssueAddFieldsBean [] addFieldsBean =  addFieldsDAO.getAllCustFields(etsprojid);
				
		boolean bladeUsrInt = false;

		try {
			bladeUsrInt = usrRolesModel.isBladeUsrInt();
			
			sbview.append("<table summary=\"SubmitIssueCustField\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"400\">\n");
			if (usrRolesModel.isUsrReqIssueType()) {		
				for(int i=0; i < addFieldsBean.length; i++ ) {
					sbview.append("<tr>\n");
					sbview.append("<td  valign=\"top\"> \n");
					sbview.append("<label for=\"custFieldLabel"  + addFieldsBean[i].getFieldId() + "\">&nbsp;<b>" + addFieldsBean[i].getFieldLabel() +  "</b> : </label>");					
					sbview.append("</td> \n");
					sbview.append("<td  valign=\"top\"> \n");
					sbview.append("<input id=\"custFieldVal"  + addFieldsBean[i].getFieldId() + "\" align=\"left\" class=\"iform\" maxlength=\"30\" name=\"custFieldVal" + addFieldsBean[i].getFieldId() + "\" size=\"100\" src=\"\" type=\"text\" style=\"width:250px\" width=\"250px\" value=\"\"  />\n");
					sbview.append("</td> \n");					
					sbview.append("<td  valign=\"top\">  &nbsp;&nbsp; \n");
					sbview.append("</td> \n");
					sbview.append("</tr> \n");
					
					if(i==0)
						sbCustFieldId.append(addFieldsBean[i].getFieldId() );
					else
						sbCustFieldId.append("," + addFieldsBean[i].getFieldId());
					
				}	
			}
			sbview.append("</table> \n");
			sbview.append("<input type=\"hidden\" name=\"custFieldIds\" value=\""+ sbCustFieldId.toString() +"\" />");
			
		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in printCustomFieldCntrl ", loginUserIrId );

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();
			}
		}	  
		return sbview.toString();		
	}
	
	/**
		 * 
		 * @param etsIssObjKey
		 * @param usr1InfoModel
		 * @param propMap
		 * @return
		 */

	public String printAccessCntrl(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) {

		StringBuffer sb = new StringBuffer();
		String issueAccess = AmtCommonUtils.getTrimStr(usr1InfoModel.getIssueAccess());
		String chkIssTypeIbmOnly = AmtCommonUtils.getTrimStr(usr1InfoModel.getChkIssTypeIbmOnly());
		Global.println("chkIssTypeIbmOnly== in printAccessCntrl"+chkIssTypeIbmOnly);
		//String chkIssTypeIbmOnly = AmtCommonUtils.getTrimStr((String)etsIssObjKey.getParams().get("chkissibmonly"));
		String propAccMsg = "";

		//5.2.1
		String userType = AmtCommonUtils.getTrimStr(usr1InfoModel.getUserType());

		//show this option only for IBM Internals//

		if (userType.equals("I")) {

			if (issueAccess.equals("ALL:EXT")) {

				propAccMsg = (String) propMap.get("issue.act.new.msg5");

				sb.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">");
				sb.append("<tr>\n");
				sb.append("<td  colspan=\"2\"  width=\"600\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"2\" alt=\"\" />\n");
				sb.append("</td>\n");
				sb.append("</tr>\n");

				sb.append("<tr><td  colspan=\"2\" height=\"18\" >\n");
				sb.append(propAccMsg);
				sb.append("</td></tr>\n");
				sb.append("</table>\n");

			}

			if (issueAccess.equals("IBM:IBM")) {

				propAccMsg = (String) propMap.get("issue.act.new.msg51");

				//print section header

				sb.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">");
				sb.append("<tr>\n");
				sb.append("<td  width=\"600\" colspan=\"2\" ><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"2\" alt=\"\" />\n");
				sb.append("</td>\n");
				sb.append("</tr>\n");
				sb.append("<tr valign=\"top\" >\n");
				sb.append("<td  valign=\"top\" align=\"left\" width=\"25%\"><b>IBM only</b>:</td>\n");
				sb.append("<td  valign=\"top\" align=\"left\" width=\"75%\">\n");
				sb.append("<input type=\"checkbox\" name=\"chkissibmonly1\" value=\"Y\" id=\"lblchkibm\" checked=\"checked\" class=\"iform\"  disabled=\"disabled\" />\n");
				sb.append("<input type=\"hidden\" name=\"chkissibmonly\" value=\"Y\"/>\n");
				sb.append("</td>\n");
				sb.append("</tr>\n");
				sb.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");
				sb.append("<tr><td  colspan=\"2\" height=\"18\" >\n");
				sb.append(propAccMsg);
				sb.append("</td></tr>\n");
				sb.append("</table>\n");

			}

			if (issueAccess.equals("ALL:IBM")) {

				propAccMsg = (String) propMap.get("issue.act.new.msg52");

				sb.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">");
				sb.append("<tr>\n");
				sb.append("<td  width=\"600\" colspan=\"2\" ><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"2\" alt=\"\" />\n");
				sb.append("</td>\n");
				sb.append("</tr>\n");
				sb.append("<tr valign=\"top\" >\n");
				sb.append("<td  valign=\"top\" align=\"left\" width=\"25%\"><b>IBM only</b>:</td>\n");
				sb.append("<td  valign=\"top\" align=\"left\" width=\"75%\">\n");

				if (chkIssTypeIbmOnly.equals("Y")) {

					sb.append("<input type=\"checkbox\" name=\"chkissibmonly\" value=\"Y\" id=\"lblchkibm\" checked=\"checked\" class=\"iform\"  onclick=\"changeNotifyList()\"  onkeypress=\"changeNotifyList()\" />\n");

				} else {

					sb.append("<input type=\"checkbox\" name=\"chkissibmonly\" value=\"Y\" id=\"lblchkibm\" onclick=\"changeNotifyList()\"  onkeypress=\"changeNotifyList()\"/>\n");

				}
				sb.append("</td>\n");
				sb.append("</tr>\n");
				sb.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");
				sb.append("<tr><td  colspan=\"2\" height=\"18\" >\n");
				sb.append(propAccMsg);
				sb.append("</td></tr>\n");
				sb.append("</table>\n");

			}

		} //show only for internals

		return sb.toString();

	}

	/**
		 * 
		 * 
		 * @param etsIssObjKey
		 * @param usr1InfoModel
		 * @param propMap
		 * @return
		 */

	public String printNotifyList(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) {

		StringBuffer sbsub = new StringBuffer();

		ArrayList notifyList = usr1InfoModel.getNotifyList();
		ArrayList prevNotifyList = usr1InfoModel.getPrevNotifyList();
		String chkIssIbmType = AmtCommonUtils.getTrimStr(usr1InfoModel.getChkIssTypeIbmOnly());
		String propMsg = "";
		EtsIssUserRolesModel usrRolesModel = etsIssObjKey.getUsrRolesModel();

		//		5.2.1
		String userType = AmtCommonUtils.getTrimStr(usr1InfoModel.getUserType());

		//get prop mgs
		propMsg = (String) propMap.get("issue.act.new.msg6");

		//String userType = etsIssObjKey.getEs().gDECAFTYPE;

		Global.println("NOTIFY LIST USER TYPE===" + userType);

		int notsize = 0;

		if (notifyList != null && !notifyList.isEmpty()) {

			notsize = notifyList.size();

		}
		sbsub.append("<script type=\"text/javascript\" language=\"javascript1.2\">");
		String notVal = "";
		for (int i = 0; i < notsize; i += 3) {

			notVal = (String) notifyList.get(i) + "$" + (String) notifyList.get(i + 1) + "$" + (String) notifyList.get(i + 2);

			if (i == 0) {

				sbsub.append("var notlist= new Array(\"" + notVal + "\"");

			} else {

				sbsub.append(",\"" + notVal + "\"");
			}

		}
		sbsub.append(")");
		sbsub.append("</script>");

		//print notify list
		sbsub.append(printNotifyList(notifyList, prevNotifyList, propMsg, userType, chkIssIbmType));

		//5.2.1 chk if it is for blade project
		String extBhf = AmtCommonUtils.getTrimStr((String) etsIssObjKey.getParams().get("extbhf"));

		if (!etsIssObjKey.isProjBladeType()) {

			return sbsub.toString();

		} else {

			if (!extBhf.equals("1")) { //if non ext bhf case

				if (usrRolesModel.isBladeUsrInt()) {

					return sbsub.toString();

				} else {

					return "";
				}

			} else { //if ext bhf case, the user should not see notify list at all

				return "";

			}

		} //end of proj blade type

		//return sbsub.toString();

	}

	/**
		 * 
		 * @param notifyList
		 * @param prevNotifyList
		 * @param propMsg
		 * @param userType
		 * @param chkIssIbmType
		 * @return
		 */

	public String printNotifyList(ArrayList notifyList, ArrayList prevNotifyList, String propMsg, String userType, String chkIssIbmType) {

		StringBuffer sbsub = new StringBuffer();

		ArrayList updList = new ArrayList();
		int size = 0;

		if (notifyList != null && !notifyList.isEmpty()) {
			size = notifyList.size();
		}
		for (int i = 0; i < size; i += 3) {

			updList.add(notifyList.get(i));
			updList.add(notifyList.get(i + 1));
		}

		sbsub.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n");
		sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");
		sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >\n");
		sbsub.append(propMsg);
		sbsub.append("</td></tr>\n");
		sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");
		sbsub.append("<tr><td  colspan=\"2\" height=\"18\" ><span class=\"small\">[ To select more than one name, hold Ctrl and click the names. ]</span></td></tr>\n");
		sbsub.append("<tr valign=\"top\" >\n");
		sbsub.append("<td valign=\"top\" align=\"left\"  width=\"443\">\n");
		sbsub.append("<select id=\"ff5\" multiple=\"multiple\" size=\"5\" name=\"notifylist\"  align=\"left\" class=\"iform\" style=\"width:443px\" width=\"443px\"  >\n");
		sbsub.append(fltrGuiUtils.printSelectOptionsWithValue(updList, prevNotifyList));
		sbsub.append("</select>\n");
		sbsub.append("</td>\n");
		sbsub.append("</tr>\n");
		sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

		if (userType.equals("I") && !chkIssIbmType.equals("Y")) {

			sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >\n");
			sbsub.append("<b>* IBM employee</b>");
			sbsub.append("</td>\n");
			sbsub.append("</tr>\n");

		}

		sbsub.append("</table>\n");

		return sbsub.toString();

	}

} //end of class
