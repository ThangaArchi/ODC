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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ismgt.model.EtsCrProbInfoModel;
import oem.edge.ets.fe.ismgt.model.EtsCrRtfModel;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.resources.EtsCrActionConstants;
import oem.edge.ets.fe.ismgt.resources.EtsCrProcessConstants;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsCrViewGuiUtils implements EtsCrActionConstants, EtsCrProcessConstants {

	public static final String VERSION = "1.17.2.12";
	private EtsIssFilterGuiUtils fltrGuiUtils;
	private EtsIssCommonGuiUtils comGuiUtils;

	/**
	 * 
	 */
	public EtsCrViewGuiUtils() {
		super();
		this.fltrGuiUtils = new EtsIssFilterGuiUtils();
		this.comGuiUtils = new EtsIssCommonGuiUtils();
	}

	/**
			 * This method will print submitter details
			 * 
			 */

	public String printSubmitterDetailsForView(EtsCrProbInfoModel crInfoModel) {

		StringBuffer sb = new StringBuffer();

		String curIssueCustName = AmtCommonUtils.getTrimStr(crInfoModel.getCustName());
		String curIssueCustEmail = AmtCommonUtils.getTrimStr(crInfoModel.getCustEmail());
		String curIssueCustPhone = AmtCommonUtils.getTrimStr(crInfoModel.getCustPhone());
		String curIssueCustCompany = AmtCommonUtils.getTrimStr(crInfoModel.getCustCompany());

		sb.append("<table summary=\"submitter profile\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n");
		sb.append("<tr >\n");
		sb.append("<td valign=\"top\" align=\"left\" width=\"20%\"><b>Submitter</b>:</td>\n");
		sb.append("<td valign=\"top\" align=\"left\" width=\"40%\">" + curIssueCustName + "</td>\n");

		sb.append("<td valign=\"top\" align=\"left\" width=\"20%\"><b>Company</b>:</td>\n");
		sb.append("<td valign=\"top\" align=\"left\" width=\"20%\">" + curIssueCustCompany + "</td>\n");
		sb.append("</tr>\n");
		sb.append("<tr><td colspan=\"4\">&nbsp;</td></tr>\n");
		sb.append("<tr >\n");
		sb.append("<td valign=\"top\" align=\"left\" width=\"20%\"><b>E-mail</b>:</td>\n");
		sb.append("<td valign=\"top\" align=\"left\" width=\"40%\">" + curIssueCustEmail + "</td>\n");
		sb.append("<td valign=\"top\" align=\"left\" width=\"20%\"><b>Phone</b>:</td>\n");
		sb.append("<td valign=\"top\" align=\"left\" width=\"20%\">" + curIssueCustPhone + "</td>\n");
		sb.append("</tr>\n");
		sb.append("</table>\n");

		return sb.toString();

	}

	/**
			 * This method will print submitter details
			 * 
			 */

	public String printIssueDescripDetailsForView(EtsCrProbInfoModel crInfoModel, EtsIssObjectKey etsIssObjKey) {

		StringBuffer sb = new StringBuffer();
		String severity = "";

		HashMap pcrPropMap = etsIssObjKey.getPcrPropMap();

		ArrayList prevSevList = crInfoModel.getPrevProbSevList();

		if (prevSevList != null && !prevSevList.isEmpty()) {

			severity = AmtCommonUtils.getTrimStr((String) prevSevList.get(0));

		}

		String probTitle = AmtCommonUtils.getTrimStr(crInfoModel.getProbTitle());
		String probDesc = comGuiUtils.formatDescStr(AmtCommonUtils.getTrimStr(crInfoModel.getProbDesc()));
		//String stateAction = AmtCommonUtils.getTrimStr(crInfoModel.getStateAction());
		String probState = AmtCommonUtils.getTrimStr(crInfoModel.getProbState());
		String submissionDate = AmtCommonUtils.getTrimStr(crInfoModel.getCreationDateStr());

		//get owner name
		String ownerName = crInfoModel.getOwnerName();

		//

		sb.append("<table summary=\"prob desc profile\" border=\"0\" cellspacing=\"0\" cellpadding=\"4\" width=\"600\">\n");
		//sb.append("<tr><td colspan=\"5\">&nbsp;</td></tr>\n");

		sb.append("<tr>\n");
		sb.append("<td  valign=\"top\" width =\"20%\" align=\"left\"><b>Title</b>:</td>\n");
		sb.append("<td  valign=\"top\" width =\"40%\" align=\"left\">" + probTitle + "</td>\n");
		sb.append("<td  valign=\"top\" width =\"20%\" align=\"left\"><b>Date submitted</b>:</td>\n");
		sb.append("<td  valign=\"top\" width =\"20%\" align=\"left\">" + submissionDate + "</td>\n");
		sb.append("</tr>\n");
		sb.append("</table>\n");

		sb.append("<table summary=\"prob desc profile\" border=\"0\" cellspacing=\"0\" cellpadding=\"4\" width=\"600\">\n");
		sb.append("<tr>\n");
		sb.append("<td  valign=\"top\" width =\"20%\" align=\"left\"><b>Priority</b>:</td>\n");
		sb.append("<td  valign=\"top\" width =\"40%\" align=\"left\">" + severity + "</td>\n");
		sb.append("<td  valign=\"top\" width =\"20%\" align=\"left\"><b>State</b>:</td>\n");

		if (probState.equals((String) pcrPropMap.get("ets_pmo_cri.CR_NACKED_STATE.staction")) || probState.equals((String) pcrPropMap.get("ets_pmo_cri.CR_TIMEOUT_STATE.staction"))) {

			sb.append("<td  valign=\"top\" width =\"20%\" align=\"left\"><span style=\"color:#ff3333\">" + probState + "</span></td>\n");
			
		} else {

			sb.append("<td  valign=\"top\" width =\"20%\" align=\"left\">" + probState + "</td>\n");

		}
		sb.append("</tr>\n");
		sb.append("</table>\n");

		///show only if owner defined

		if (etsIssObjKey.getEs().gDECAFTYPE.equals("I") && AmtCommonUtils.isResourceDefined(ownerName)) {

			if (etsIssObjKey.isShowIssueOwner()) { //show issue owner

				sb.append("<table summary=\"prob desc profile\" border=\"0\" cellspacing=\"0\" cellpadding=\"4\" width=\"600\">\n");
				sb.append("<tr>\n");
				sb.append("<td  valign=\"top\" width =\"20%\" align=\"left\"><b>Owner</b>:</td>\n");
				sb.append("<td  valign=\"top\" width =\"80%\" align=\"left\">" + ownerName + "</td>\n");
				sb.append("</tr>\n");
				sb.append("</table>\n");

			}

		}

		//		sb.append("<table summary=\"prob desc profile\" border=\"0\" cellspacing=\"0\" cellpadding=\"4\" width=\"600\">\n");
		//		sb.append("<tr>\n");
		//		sb.append("<td  valign=\"top\" width =\"20%\" align=\"left\"><b>Description</b>:</td>\n");
		//		sb.append("<td  valign=\"top\" width =\"80%\" align=\"left\">\n");
		//		sb.append(probDesc);
		//		sb.append("</td>\n");
		//		sb.append("</tr>\n");
		//
		//		sb.append("</table>\n");

		sb.append(printDescRtfForComments(etsIssObjKey, crInfoModel));

		return sb.toString();
	}

	public String printAllRtfs(EtsIssObjectKey etsIssObjKey, EtsCrProbInfoModel crInfoModel) {

		StringBuffer sb = new StringBuffer();

		//pcr properties
		HashMap pcrPropMap = etsIssObjKey.getPcrPropMap();

		//rtf map
		HashMap rtfMap = crInfoModel.getRtfMap();

		EtsCrRtfModel crRtfModel = new EtsCrRtfModel();

		String pmo_cri_RTF_DynStr = "";
		String pmo_cri_RTF_Id = "";

		String crRftModelAliasName = "";
		String crRftModelBlobStr = "";
		String pmoId = "";
		
		//get the total count of RTFS from property file, that need to be displayed
		String strRtfCount = AmtCommonUtils.getTrimStr((String) pcrPropMap.get("ets_pmo_cri.RTF.NoOfRTFs"));
		int intRtfCount=0;
		
		if(AmtCommonUtils.isResourceDefined(strRtfCount)) {
			
			intRtfCount=Integer.parseInt(strRtfCount);
		}

		if (rtfMap != null && !rtfMap.isEmpty()) {

			for (int i = 1; i <= intRtfCount; i++) {

				pmo_cri_RTF_DynStr = "ets_pmo_cri.RTF." + i;
				pmo_cri_RTF_Id = AmtCommonUtils.getTrimStr((String) pcrPropMap.get(pmo_cri_RTF_DynStr));

				Global.println("pmo_cri_RTF_DynStr==" + pmo_cri_RTF_DynStr);
				Global.println("pmo_cri_RTF_Id==" + pmo_cri_RTF_Id);
				
				if(AmtCommonUtils.isResourceDefined(pmo_cri_RTF_Id)) {
					
					crRtfModel = (EtsCrRtfModel) rtfMap.get(pmo_cri_RTF_Id);
					
				}
				
				else {
					
					crRtfModel = null;
				}

				

				///

				if (crRtfModel != null) {

					Global.println("crRtfModel NOT NULL FOR==" + pmo_cri_RTF_Id);

					crRftModelAliasName = crRtfModel.getRtfAliasName();
					crRftModelBlobStr = crRtfModel.getRtfBlobStr();
					pmoId = crRtfModel.getPmoId();

					Global.println("crRftModel alias name==" + crRftModelAliasName);
					Global.println("crRftModel Blob Str==" + crRftModelBlobStr);

					if (i == 1) { //for description

						if (!AmtCommonUtils.isResourceDefined(crRftModelBlobStr)) {

							crRftModelBlobStr = crInfoModel.getProbDesc();
						}

					}

					if (i == 9) { //for comments

						if (!AmtCommonUtils.isResourceDefined(crRftModelBlobStr)) {

							crRftModelBlobStr = crInfoModel.getCommFromCust();
						}

					}

					//print RTFs except description to avoid duplicates
					
					if(i!=1) {
					
					sb.append(printRtf(crRftModelAliasName, crRftModelBlobStr, pmoId));
					
					}

				} //crRftModel != null

				else { //for description and comments only

					Global.println("crRtfModel is NULL FOR==" + pmo_cri_RTF_Id);

					if (i == 1 || i == 9) {

						if (i == 1) {

							pmoId = crInfoModel.getPmoId();
							crRftModelBlobStr = crInfoModel.getProbDesc();

							Global.println("printing desc===" + crRftModelBlobStr);

							//	print RTFs, already printed in issue description details
							//sb.append(printDescRtf("Description", crRftModelBlobStr, pmoId));

						}

						if (i == 9) {

							pmoId = crInfoModel.getPmoId();
							crRftModelBlobStr = crInfoModel.getCommFromCust();

							//print RTFs
							sb.append(printDescRtf("Comments", crRftModelBlobStr, pmoId));

						}

					}

				}
			} //for loop

		} //rtfMap != null

		return sb.toString();
	}
	
	
	

	public String printRtf(String sectHeader, String rtfMsg, String pmoId) {

		StringBuffer sb = new StringBuffer();

		//print only if it contains some messg
		if (AmtCommonUtils.isResourceDefined(rtfMsg)) {

			//print section header
			sb.append(comGuiUtils.printSectnHeader(sectHeader));

			String shrtMsg = "";

			if (rtfMsg.length() > PCRRTFMAXSIZE) {

				shrtMsg = rtfMsg.substring(0, PCRRTFMAXSIZE);

			} else {

				shrtMsg = rtfMsg;
			}

			sb.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"4\" width=\"600\">\n");
			sb.append("<tr><td  height=\"18\" width=\"600\" > \n");
			sb.append(getFormatStr(shrtMsg));

			if (rtfMsg.length() > PCRRTFMAXSIZE) {

				sb.append(printViewMoreRTF(pmoId));
			}

			sb.append("</td></tr></table>");

		}

		return sb.toString();

	}

	public String printDescRtf(String sectHeader, String rtfMsg, String pmoId) {

		StringBuffer sb = new StringBuffer();

		//print only if it contains some messg
		if (AmtCommonUtils.isResourceDefined(rtfMsg)) {

			//print section header
			sb.append(comGuiUtils.printSectnHeader(sectHeader));

			sb.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"4\" width=\"600\">\n");
			sb.append("<tr><td  height=\"18\" width=\"600\" > \n");
			sb.append(getFormatStr(rtfMsg));
			sb.append("</td></tr></table>");

		}

		return sb.toString();

	}

	public String viewCRAttachedFilesList(String etsId) {

		EtsIssFileAttachUtils fileUtils = new EtsIssFileAttachUtils();

		return fileUtils.viewCRAttachedFilesList(etsId);
	}

	/**
		 * 
		 * @param helpStr
		 * @return
		 */

	public String printViewMoreRTF(String pmoId) {

		StringBuffer sb = new StringBuffer();

		////

		sb.append("&nbsp;&nbsp;<a  href=\"EtsIssHelpBlurbServlet.wss?OP=RTF&pmoId=" + pmoId + "\" target=\"new\" \n");
		sb.append(" onclick=\"window.open('EtsIssHelpBlurbServlet.wss?OP=RTF&pmoId=" + pmoId + "','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\" \n");
		sb.append(" onkeypress=\"window.open('EtsIssHelpBlurbServlet.wss?OP=RTF&pmoId=" + pmoId + "','Services','toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=0,width=436,height=425,left=387,top=207'); return false;\">View more</a>..\n");

		///

		return sb.toString();
	}

	public String printCommentsLink(EtsIssObjectKey etsIssObjKey, EtsCrProbInfoModel crInfoModel) {

		String etsId = crInfoModel.getEtsId();

		String statusFlag = AmtCommonUtils.getTrimStr(crInfoModel.getStatusFlag());

		String pmoId = AmtCommonUtils.getTrimStr(crInfoModel.getPmoId());

		String infoSrcFlag = AmtCommonUtils.getTrimStr(crInfoModel.getInfoSrcFlag());

		Global.println("info src flag/printCommentsLink===" + infoSrcFlag);

		Global.println("statusFlag/printCommentsLink ===" + statusFlag);

		StringBuffer sbview = new StringBuffer();

		if (!infoSrcFlag.equals("P")) { //for PCR from ETS

			if (AmtCommonUtils.isResourceDefined(pmoId) && (statusFlag.equals("A") || statusFlag.equals("N"))) {

				sbview.append("<table summary=\"prob desc profile\" border=\"0\" cellspacing=\"0\" cellpadding=\"2\" width=\"600\">\n");
				sbview.append("<tr>");
				sbview.append(
					"<td colspan=\"1\" width=\"18\"><a href=\"ETSProjectsServlet.wss?proj="
						+ etsIssObjKey.getProj().getProjectId()
						+ "&tc="
						+ etsIssObjKey.getTopCatId()
						+ "&linkid="
						+ etsIssObjKey.getSLink()
						+ "&actionType=commentChange&op=500&istyp="
						+ etsIssObjKey.getIstyp()
						+ "&flop="
						+ etsIssObjKey.getFilopn()
						+ "&etsId="
						+ etsId
						+ "\""
						+ "><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
				sbview.append(
					"<td align=\"left\"><a href=\"ETSProjectsServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&actionType=commentChange&op=500&istyp=" + etsIssObjKey.getIstyp() + "&flop=" + etsIssObjKey.getFilopn() + "&etsId=" + etsId + "\"" + ">" + "Add comments for this change request" + "</a></td>");
				sbview.append("</tr>");
				sbview.append("</table>");

				sbview.append("<br />");

			}

		} else { //for PCR from PMO 

			if (AmtCommonUtils.isResourceDefined(pmoId) && !AmtCommonUtils.isResourceDefined(statusFlag)) {

				sbview.append("<table summary=\"prob desc profile\" border=\"0\" cellspacing=\"0\" cellpadding=\"2\" width=\"600\">\n");
				sbview.append("<tr>");
				sbview.append(
					"<td colspan=\"1\" width=\"18\"><a href=\"ETSProjectsServlet.wss?proj="
						+ etsIssObjKey.getProj().getProjectId()
						+ "&tc="
						+ etsIssObjKey.getTopCatId()
						+ "&linkid="
						+ etsIssObjKey.getSLink()
						+ "&actionType=commentChange&op=500&istyp="
						+ etsIssObjKey.getIstyp()
						+ "&flop="
						+ etsIssObjKey.getFilopn()
						+ "&etsId="
						+ etsId
						+ "\""
						+ "><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
				sbview.append(
					"<td align=\"left\"><a href=\"ETSProjectsServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&actionType=commentChange&op=500&istyp=" + etsIssObjKey.getIstyp() + "&flop=" + etsIssObjKey.getFilopn() + "&etsId=" + etsId + "\"" + ">" + "Add comments for this change request" + "</a></td>");
				sbview.append("</tr>");
				sbview.append("</table>");

				sbview.append("<br />");

			} else if (AmtCommonUtils.isResourceDefined(pmoId) && AmtCommonUtils.isResourceDefined(statusFlag) && (statusFlag.equals("A") || statusFlag.equals("N"))) {

				sbview.append("<table summary=\"prob desc profile\" border=\"0\" cellspacing=\"0\" cellpadding=\"2\" width=\"600\">\n");
				sbview.append("<tr>");
				sbview.append(
					"<td colspan=\"1\" width=\"18\"><a href=\"ETSProjectsServlet.wss?proj="
						+ etsIssObjKey.getProj().getProjectId()
						+ "&tc="
						+ etsIssObjKey.getTopCatId()
						+ "&linkid="
						+ etsIssObjKey.getSLink()
						+ "&actionType=commentChange&op=500&istyp="
						+ etsIssObjKey.getIstyp()
						+ "&flop="
						+ etsIssObjKey.getFilopn()
						+ "&etsId="
						+ etsId
						+ "\""
						+ "><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
				sbview.append(
					"<td align=\"left\"><a href=\"ETSProjectsServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&actionType=commentChange&op=500&istyp=" + etsIssObjKey.getIstyp() + "&flop=" + etsIssObjKey.getFilopn() + "&etsId=" + etsId + "\"" + ">" + "Add comments for this change request" + "</a></td>");
				sbview.append("</tr>");
				sbview.append("</table>");

				sbview.append("<br />");

			}

		}

		return sbview.toString();
	}

	public String printAddFilesLink(EtsIssObjectKey etsIssObjKey, EtsCrProbInfoModel crInfoModel) {

		String etsId = crInfoModel.getEtsId();

		String statusFlag = AmtCommonUtils.getTrimStr(crInfoModel.getStatusFlag());

		String pmoId = AmtCommonUtils.getTrimStr(crInfoModel.getPmoId());

		String infoSrcFlag = AmtCommonUtils.getTrimStr(crInfoModel.getInfoSrcFlag());

		Global.println("info src flag===" + infoSrcFlag);

		Global.println("statusFlag/printAddFilesLink ===" + statusFlag);

		StringBuffer sbview = new StringBuffer();

		if (!infoSrcFlag.equals("P")) {

			if (AmtCommonUtils.isResourceDefined(pmoId) && (statusFlag.equals("A") || statusFlag.equals("N"))) {

				sbview.append("<table summary=\"prob desc profile\" border=\"0\" cellspacing=\"0\" cellpadding=\"2\" width=\"600\">\n");
				sbview.append("<tr>");
				sbview.append(
					"<td colspan=\"1\" width=\"18\"><a href=\"ETSProjectsServlet.wss?proj="
						+ etsIssObjKey.getProj().getProjectId()
						+ "&tc="
						+ etsIssObjKey.getTopCatId()
						+ "&linkid="
						+ etsIssObjKey.getSLink()
						+ "&actionType=commentChange&op=521&istyp="
						+ etsIssObjKey.getIstyp()
						+ "&flop="
						+ etsIssObjKey.getFilopn()
						+ "&etsId="
						+ etsId
						+ "\""
						+ "><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
				sbview.append(
					"<td align=\"left\"><a href=\"ETSProjectsServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&actionType=commentChange&op=521&istyp=" + etsIssObjKey.getIstyp() + "&flop=" + etsIssObjKey.getFilopn() + "&etsId=" + etsId + "\"" + ">" + "Add files for this change request" + "</a></td>");
				sbview.append("</tr>");
				sbview.append("</table>");

				sbview.append("<br />");

			}

		} //for PCR from ETS

		else { //for PCR from PMO 

			if (AmtCommonUtils.isResourceDefined(pmoId) && !AmtCommonUtils.isResourceDefined(statusFlag)) {

				sbview.append("<table summary=\"prob desc profile\" border=\"0\" cellspacing=\"0\" cellpadding=\"2\" width=\"600\">\n");
				sbview.append("<tr>");
				sbview.append(
					"<td colspan=\"1\" width=\"18\"><a href=\"ETSProjectsServlet.wss?proj="
						+ etsIssObjKey.getProj().getProjectId()
						+ "&tc="
						+ etsIssObjKey.getTopCatId()
						+ "&linkid="
						+ etsIssObjKey.getSLink()
						+ "&actionType=commentChange&op=521&istyp="
						+ etsIssObjKey.getIstyp()
						+ "&flop="
						+ etsIssObjKey.getFilopn()
						+ "&etsId="
						+ etsId
						+ "\""
						+ "><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
				sbview.append(
					"<td align=\"left\"><a href=\"ETSProjectsServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&actionType=commentChange&op=521&istyp=" + etsIssObjKey.getIstyp() + "&flop=" + etsIssObjKey.getFilopn() + "&etsId=" + etsId + "\"" + ">" + "Add files for this change request" + "</a></td>");
				sbview.append("</tr>");
				sbview.append("</table>");

				sbview.append("<br />");

			} else if (AmtCommonUtils.isResourceDefined(pmoId) && AmtCommonUtils.isResourceDefined(statusFlag) && (statusFlag.equals("A") || statusFlag.equals("N"))) {

				sbview.append("<table summary=\"prob desc profile\" border=\"0\" cellspacing=\"0\" cellpadding=\"2\" width=\"600\">\n");
				sbview.append("<tr>");
				sbview.append(
					"<td colspan=\"1\" width=\"18\"><a href=\"ETSProjectsServlet.wss?proj="
						+ etsIssObjKey.getProj().getProjectId()
						+ "&tc="
						+ etsIssObjKey.getTopCatId()
						+ "&linkid="
						+ etsIssObjKey.getSLink()
						+ "&actionType=commentChange&op=521&istyp="
						+ etsIssObjKey.getIstyp()
						+ "&flop="
						+ etsIssObjKey.getFilopn()
						+ "&etsId="
						+ etsId
						+ "\""
						+ "><img src=\"" + Defines.ICON_ROOT  + "fw.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
				sbview.append("<td align=\"left\"><a href=\"ETSProjectsServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&actionType=commentChange&op=521&istyp=" + etsIssObjKey.getIstyp() + "&flop=" + etsIssObjKey.getFilopn() + "&etsId=" + etsId + "\">" + "Add files for this change request" + "</a></td>");
				sbview.append("</tr>");
				sbview.append("</table>");

				sbview.append("<br />");

			}

		}

		return sbview.toString();
	}

	public String printDescRtfForComments(EtsIssObjectKey etsIssObjKey, EtsCrProbInfoModel crInfoModel) {

		StringBuffer sb = new StringBuffer();

		//pcr properties
		HashMap pcrPropMap = etsIssObjKey.getPcrPropMap();

		//rtf map
		HashMap rtfMap = crInfoModel.getRtfMap();

		EtsCrRtfModel crRtfModel = new EtsCrRtfModel();

		String pmo_cri_RTF_DynStr = "";
		String pmo_cri_RTF_Alias_Name = "";
		String pmo_cri_RTF_Id="";

		String crRftModelAliasName = "";
		String crRftModelBlobStr = "";
		String pmoId = "";

		//if (rtfMap != null && !rtfMap.isEmpty()) {

		for (int i = 1; i < 2; i++) {

			pmo_cri_RTF_DynStr = "ets_pmo_cri.RTF." + i;
			pmo_cri_RTF_Id = AmtCommonUtils.getTrimStr((String) pcrPropMap.get(pmo_cri_RTF_DynStr));

			Global.println("pmo_cri_RTF_DynStr==" + pmo_cri_RTF_DynStr);
			Global.println("pmo_cri_RTF_Id==" + pmo_cri_RTF_Alias_Name);

			crRtfModel = (EtsCrRtfModel) rtfMap.get(pmo_cri_RTF_Id);

			///

			if (crRtfModel != null) {

				crRftModelAliasName = crRtfModel.getRtfAliasName();
				crRftModelBlobStr = crRtfModel.getRtfBlobStr();
				pmoId = crRtfModel.getPmoId();

				Global.println("crRftModel alias name==" + crRftModelAliasName);
				Global.println("crRftModel Blob Str==" + crRftModelBlobStr);

				if (i == 1) { //for description

					if (!AmtCommonUtils.isResourceDefined(crRftModelBlobStr)) {

						crRftModelBlobStr = crInfoModel.getProbDesc();
					}

				}

				//print RTFs
				sb.append(printStdDescForComments(crRftModelAliasName, crRftModelBlobStr, pmoId));

			} //crRftModel != null

			else { //for description and comments only

				if (i == 1) {

					pmoId = crInfoModel.getPmoId();
					crRftModelBlobStr = crInfoModel.getProbDesc();

					//	print RTFs
					sb.append(printStdDescForComments("Description", crRftModelBlobStr, pmoId));

				}

			}
		} //for loop

		//} //rtfMap != null

		return sb.toString();
	}

	public String printStdDescForComments(String sectHeader, String rtfMsg, String pmoId) {

		StringBuffer sb = new StringBuffer();

//		sb.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"4\" width=\"600\">\n");
//		sb.append("<tr>");
//		sb.append("<td  height=\"18\" width=\"600\" > \n");
//		sb.append("<b>" + sectHeader + "</b>:");
//		sb.append("</td>");
//		sb.append("</tr>");
//		sb.append("<tr>");
//		sb.append("<td  height=\"18\" width=\"600\" > \n");
//		sb.append(rtfMsg);
//		sb.append("</td>");
//		sb.append("</tr></table>");
		
		sb.append("<table summary=\"prob desc profile\" border=\"0\" cellspacing=\"0\" cellpadding=\"4\" width=\"600\">\n");
		sb.append("<tr>\n");
		sb.append("<td  valign=\"top\" width =\"20%\" align=\"left\"><b>"+sectHeader+"</b>:</td>\n");
		sb.append("<td  valign=\"top\" width =\"80%\" align=\"left\">\n");
		sb.append(rtfMsg);
		sb.append("</td>\n");
		sb.append("</tr>\n");
		sb.append("</table>\n");

		return sb.toString();

	}

	public String getIndepViewFileAttachMsgForCR(EtsIssObjectKey etsIssObjKey, EtsCrProbInfoModel crInfoModel, String actionType) {

		EtsIssFileAttachUtils fileUtils = new EtsIssFileAttachUtils();

		return fileUtils.getIndepViewFileAttachMsgForCR(etsIssObjKey, crInfoModel, actionType);

	}

	public String printAttachedFilesListForCRUpdate(String etsId) throws SQLException, Exception {

		EtsIssFileAttachUtils fileUtils = new EtsIssFileAttachUtils();

		return fileUtils.printAttachedFilesListForCRUpdate(etsId);

	}

	public String printAttachedFilesListForViewCR(String etsId) throws SQLException, Exception {

		EtsIssFileAttachUtils fileUtils = new EtsIssFileAttachUtils();

		return fileUtils.printAttachedFilesListForViewCR(etsId);

	}

	public String printReturnToViewPageForCr(EtsIssObjectKey etsIssObjKey, String etsId) {

		StringBuffer sbsub = new StringBuffer();

		sbsub.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"30%\">");
		sbsub.append("<tr>");
		sbsub.append(
			"<td  ><a href=\"ETSProjectsServlet.wss?proj="
				+ etsIssObjKey.getProj().getProjectId()
				+ "&tc="
				+ etsIssObjKey.getTopCatId()
				+ "&linkid="
				+ etsIssObjKey.getSLink()
				+ "&sc=0&actionType=viewChange&op=60&istyp="
				+ etsIssObjKey.getIstyp()
				+ "&flop="
				+ etsIssObjKey.getFilopn()
				+ "&etsId="
				+ etsId
				+ "\"><img src=\"" + Defines.ICON_ROOT  + "bk.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
		sbsub.append("<td  ><a href=\"ETSProjectsServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&tc=" + etsIssObjKey.getTopCatId() + "&linkid=" + etsIssObjKey.getSLink() + "&sc=0&actionType=viewChange&op=60&istyp=" + etsIssObjKey.getIstyp() + "&flop=" + etsIssObjKey.getFilopn() + "&etsId=" + etsId + "\"" + " class=\"fbox\">Return to Change request</a></td>");
		sbsub.append("</tr>");
		sbsub.append("</table>");

		return sbsub.toString();

	}

	public String getFormatStr(String oldStr) {

		StringBuffer sb = new StringBuffer();

		String sToken = null;
		int iStartIndex = 0;
		ArrayList aList = new ArrayList();

		Global.println("STRING FROM DB======" + oldStr);

		int iCount = 0;
		int iIndex = oldStr.indexOf("\n");

		while (iIndex != -1) {

			sToken = oldStr.substring(0, iIndex);
			//System.out.println(sToken);
			aList.add(sToken);

			oldStr = oldStr.substring(iIndex + 1);
			//System.out.println("Now message: " + msg); 
			iIndex = oldStr.indexOf("\n");

		}

		aList.add(oldStr);

		for (int index = 0; index < aList.size(); index++) {
			System.out.println(index + ", " + aList.get(index));

			sb.append(aList.get(index));
			sb.append("<br /><br />");

		}

		Global.println("print breaks ===" + sb.toString());

		return sb.toString();

	}

	/**
	 * To get the the problem desc
	 */

	public String getRtfValue(EtsIssObjectKey etsIssObjKey, HashMap rtfMap, String pmo_cri_RTF_DynStr) {

		//pcr properties
		HashMap pcrPropMap = etsIssObjKey.getPcrPropMap();

		EtsCrRtfModel crRtfModel = new EtsCrRtfModel();

		String pmo_cri_RTF_Id = "";

		String crRftModelAliasName = "";
		String crRftModelBlobStr = "";
		String pmoId = "";

		//pmo_cri_RTF_DynStr = "ets_pmo_cri.RTF." + i;
		pmo_cri_RTF_Id = AmtCommonUtils.getTrimStr((String) pcrPropMap.get(pmo_cri_RTF_DynStr));

		Global.println("pmo_cri_RTF_DynStr==" + pmo_cri_RTF_DynStr);
		Global.println("pmo_cri_RTF_Id==" + pmo_cri_RTF_Id);

		crRtfModel = (EtsCrRtfModel) rtfMap.get(pmo_cri_RTF_Id);

		///

		if (crRtfModel != null) {

			crRftModelAliasName = crRtfModel.getRtfAliasName();
			crRftModelBlobStr = crRtfModel.getRtfBlobStr();
			pmoId = crRtfModel.getPmoId();

			Global.println("crRftModel alias name==" + crRftModelAliasName);
			Global.println("crRftModel Blob Str==" + crRftModelBlobStr);

			if (!AmtCommonUtils.isResourceDefined(crRftModelBlobStr)) {

				crRftModelBlobStr = "";
			}

			//print RTFs

		} //crRftModel != null

		else { //for description and comments only

			crRftModelBlobStr = "";

		}

		return crRftModelBlobStr;
	}

} //end of class
