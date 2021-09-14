/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2005-2008                                     */
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
import oem.edge.common.SysLog;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ismgt.bdlg.EtsIssUpdateCustFieldBdlg;
import oem.edge.ets.fe.ismgt.dao.EtsIssueAddFieldsDAO;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssUserRolesModel;
import oem.edge.ets.fe.ismgt.model.EtsIssueAddFieldsBean;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueActionConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;
import oem.edge.ets.fe.ismgt.model.EtsCustFieldInfoModel;
/**
 * @author Dharanendra Prasad
 *
 */
public class EtsCustomFieldsGuiUtils  implements EtsIssueConstants, EtsIssueActionConstants, EtsIssFilterConstants {

	private EtsIssFilterGuiUtils fltrGuiUtils;
	private EtsIssCommonGuiUtils comGuiUtils;	
	private oem.edge.ets.fe.ismgt.helpers.EtsIssActionGuiUtils actGuiUtils;
	
	
	public EtsCustomFieldsGuiUtils() {
		super();
		this.fltrGuiUtils = new EtsIssFilterGuiUtils();
		this.comGuiUtils = new EtsIssCommonGuiUtils();		
		this.actGuiUtils = new EtsIssActionGuiUtils();
	}
	
	/**
	 * 
	 * @param issfilterkey
	 * @return
	 */
	public String printCustomFieldsActionLinks (EtsIssFilterObjectKey issfilterkey) {

		StringBuffer sbview = new StringBuffer();

		String loginUserIrId = issfilterkey.getEs().gIR_USERN;
		String etsprojid = issfilterkey.getProjectId();
		String istype = issfilterkey.getProblemType();
		String tc = issfilterkey.getTc();
		String linkid = issfilterkey.getLinkid();
		String issopn = issfilterkey.getOpn();
		HashMap propMap = issfilterkey.getPropMap();
		EtsIssUserRolesModel usrRolesModel = issfilterkey.getUsrRolesModel();
		boolean bladeUsrInt = false;

		try {
			bladeUsrInt = usrRolesModel.isBladeUsrInt();

			sbview.append("<table summary=\"issues\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"600\">\n");
			if (usrRolesModel.isUsrReqIssueType()) {		
				sbview.append("<tr>\n");
				sbview.append("<td  valign=\"top\"><a href=\"addCustField.wss?proj=" + etsprojid + "&linkid=" + linkid + "&opn=2410&actionType=addCustField&tc=" + tc + "\">\n");
				sbview.append("<img  src=\"" + Defines.ICON_ROOT  + "fw.gif\" \n");
				sbview.append("alt=\"" + propMap.get("add.custom.field.link") + "\" align=\"top\" width=\"16\" height=\"16\" \n");
				sbview.append("border=\"0\" /></a> \n");
				sbview.append("<a href=\"addCustField.wss?proj=" + etsprojid + "&linkid=" + linkid + "&opn=2410&actionType=addCustField&tc=" + tc + "\">" + propMap.get("add.custom.field.link") + " \n");
				sbview.append("</a> \n");
				sbview.append("</td> \n");
				sbview.append("</tr> \n");							

				
				sbview.append("<tr>\n");
				sbview.append("<td  valign=\"top\"><a href=\"updateCustField.wss?proj=" + etsprojid + "&linkid=" + linkid + "&opn=2430&actionType=updateCustField&tc=" + tc + "\">\n");
				sbview.append("<img  src=\"" + Defines.ICON_ROOT  + "fw.gif\" \n");
				sbview.append("alt=\"" + propMap.get("update.custom.field.link") + "\" align=\"top\" width=\"16\" height=\"16\" \n");
				sbview.append("border=\"0\" /></a> \n");
				sbview.append("<a href=\"updateCustField.wss?proj=" + etsprojid + "&linkid=" + linkid + "&opn=2430&actionType=updateCustField&tc=" + tc + "\">" + propMap.get("update.custom.field.link") + " \n");
				sbview.append("</a> \n");
				sbview.append("</td> \n");
				sbview.append("</tr> \n");
															
				
				sbview.append("<tr>\n");
				sbview.append("<td  valign=\"top\"><a href=\"removeCustField.wss?proj=" + etsprojid + "&linkid=" + linkid + "&opn=2450&actionType=removeCustField&tc=" + tc + "\">\n");
				sbview.append("<img  src=\"" + Defines.ICON_ROOT  + "fw.gif\" \n");
				sbview.append("alt=\"" + propMap.get("remove.custom.field.link") + "\" align=\"top\" width=\"16\" height=\"16\" \n");
				sbview.append("border=\"0\" /></a> \n");
				sbview.append("<a href=\"removeCustField.wss?proj=" + etsprojid + "&linkid=" + linkid + "&opn=2450&actionType=removeCustField&tc=" + tc + "\">" + propMap.get("remove.custom.field.link") + " \n");
				sbview.append("</a> \n");
				sbview.append("</td> \n");
				sbview.append("</tr> \n");
							
			}
			sbview.append("</table> \n");
			
		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in printCustomFieldsActionLinks ", loginUserIrId );

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();
			}
		}	  
			return sbview.toString();
	}

	
	/**
	 * @param listBoxDispName
	 * @param listBoxRefName
	 * @param contName
	 * @param etsIssObjKey
	 * @return
	 */
	public String printDynamicAddCustField(String listBoxDispName, String listBoxRefName, String contName, EtsIssObjectKey etsIssObjKey ) {
		String projectId = etsIssObjKey.getProj().getProjectId(); 
		StringBuffer sbsub = new StringBuffer();
		EtsIssueAddFieldsDAO issAddFieldsDAO = new EtsIssueAddFieldsDAO();
		
		//int currentCustFieldID  = issAddFieldsDAO.getCurrentCustFieldId(projectId);
		ArrayList arrLstCustFieldIDs = issAddFieldsDAO.getCurrentCustFieldId(projectId);
		
		if(arrLstCustFieldIDs.size() > 0) {
			sbsub.append("<br />");
			sbsub.append("<table summary=\"custField\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"600\">\n");
			EtsIssueAddFieldsBean []issueAddFieldsBean =  issAddFieldsDAO.getAllCustFields(projectId);
			EtsIssueAddFieldsBean  bean = null;
			for(int i=0; i< issueAddFieldsBean.length; i++ ) {
				bean = issueAddFieldsBean[i];
				sbsub.append("<tr>\n");
				sbsub.append("<td  valign=\"top\"> \n");
				sbsub.append("<label for=\"custFieldId"  + i + "\">&nbsp;<b> Field " + bean.getFieldId() +  " label</b> </label>\n");
				sbsub.append("<label for=\"custFieldName"  + i + "\">&nbsp;<b> : " + bean.getFieldLabel() +  "</b> </label>\n");
				sbsub.append("</td> \n");
				sbsub.append("</tr> \n");							
			}	
			sbsub.append("</table> \n");
			sbsub.append("<br />");
			sbsub.append(actGuiUtils.printGreyLine());
			sbsub.append("<br />");			
		}
		
		int fields =  EtsIssueConstants.MAXCUSTFIELDS - arrLstCustFieldIDs.size();
		
		if(fields > 0) {
			
			sbsub.append(printDynamicListBox("Select the number of fields ","selCustFields", projectId));
			sbsub.append("<br />");
			sbsub.append(actGuiUtils.printGreyLine());
			sbsub.append("<br />");
			sbsub.append(printContinueRetToMainPage("op_2411",etsIssObjKey));
			sbsub.append("<br />");
			sbsub.append(actGuiUtils.printGreyLine());			
		} else {
			sbsub.append("<br />");
			sbsub.append("You have reached the maximum limit (<b> " + EtsIssueConstants.MAXCUSTFIELDS + " </b>) for the custom issue fields. You cannot add more custom fields until you remove an existing field.</b>");
			sbsub.append("<br /><br />");
			sbsub.append(actGuiUtils.printGreyLine());
			sbsub.append(printReturnToManageCustomFieldsPage(etsIssObjKey));			
		}		
	    return sbsub.toString();	
	}
	

	public String printReturnToManageCustomFieldsPage(EtsIssObjectKey etsIssObjKey) {

		StringBuffer sbsub = new StringBuffer();

		sbsub.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"443\">");
		sbsub.append("<tr><td>&nbsp;</td></tr>");
		sbsub.append("<tr>");
		sbsub.append(
			"<td width=\"18\"><a href=\"EtsIssFilterCntrlServlet.wss?proj="
				+ etsIssObjKey.getProj().getProjectId()
				+ "&linkid="
				+ etsIssObjKey.getSLink()
				+ "&istyp="
				+ etsIssObjKey.getIstyp()				
				+ "&opn=2400"
				+ "&tc="
				+ etsIssObjKey.getTopCatId()
				+ "\""
				+ "><img src=\"" + Defines.ICON_ROOT  + "bk.gif\" width=\"16\" height=\"16\" alt=\"\" border=\"0\" /></a></td>");
		sbsub.append("<td valign=\"middle\"><a href=\"EtsIssFilterCntrlServlet.wss?proj=" + etsIssObjKey.getProj().getProjectId() + "&linkid=" + etsIssObjKey.getSLink() + "&istyp=" + etsIssObjKey.getIstyp() + "&opn=2400&tc=" + etsIssObjKey.getTopCatId() + "\"" + " class=\"fbox\">Return to manage custom fields</a></td>");
		sbsub.append("</tr><tr><td>&nbsp;</td></tr></table>");

		return sbsub.toString();

	}	
	
	
	/**
	 * to print the common select for subm forms
	 * 
	 */
	public String printDynamicListBox(String listBoxDispName, String listBoxRefName, String projectId) {
	
		StringBuffer sbsub = new StringBuffer();
		EtsIssueAddFieldsDAO issAddFieldsDAO = new EtsIssueAddFieldsDAO();
		
		//int currentCustFieldID  = issAddFieldsDAO.getCurrentCustFieldId(projectId);
		ArrayList arrLstCustFieldIDs = issAddFieldsDAO.getCurrentCustFieldId(projectId);
		
		int fields =  EtsIssueConstants.MAXCUSTFIELDS - arrLstCustFieldIDs.size();
		
		sbsub.append("<br /><br />");
		sbsub.append(listBoxDispName + " : <select id=\"lb" + listBoxRefName + "\" name=\"" + listBoxRefName + "\" size=\"\" class=\"iform\" style=\"width:50px\" width=\"50px\" >");
		sbsub.append(printSelectOptions(fields ));
		sbsub.append("</select>");
		sbsub.append("<input type=\"hidden\" name=\"def" + listBoxRefName + "\" value=\"Y\" />");
		sbsub.append("<input type=\"hidden\" name=\"hiddisp" + listBoxRefName + "\"  value=\"" + listBoxDispName + "\" />");
		sbsub.append("<input type=\"hidden\" name=\"hidCustFieldId\"  value=\"" + arrLstCustFieldIDs.size() + "\" />");		
		sbsub.append("<input type=\"hidden\" name=\"op\" value=\"2411\" />");
		sbsub.append("<br /><br />");
		return sbsub.toString();
	
	}	
	
	/**
	 * 
	 * @param issfilterkey
	 * @param issactionobjkey
	 * @param contName
	 * @param currentstate
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public String printDynamicUpdateCustomFields(EtsIssFilterObjectKey issfilterkey, EtsIssObjectKey issactionobjkey, String contName, int currentstate) throws SQLException, Exception {
		StringBuffer sbview = new StringBuffer();
		EtsIssUpdateCustFieldBdlg updateCustFieldBdlg = new EtsIssUpdateCustFieldBdlg(issactionobjkey, currentstate);
		
		EtsCustFieldInfoModel custFieldModel = updateCustFieldBdlg.getFirstPageDets();
	
		if(custFieldModel.getFieldLabel().size() > 0 ) {
			
			sbview.append( printUpdateCustomFields(issfilterkey, custFieldModel) );			
			sbview.append("<br />");
			sbview.append(actGuiUtils.printGreyLine());
			sbview.append("<br />");
			sbview.append( printContinueRetToMainPage(contName, issactionobjkey) );
			sbview.append("<br />");
			sbview.append(actGuiUtils.printGreyLine());
			
		} else {
			sbview.append("User can update an existing custom fields of this workspace. Currently there is no custom fields added to this workspace.");  
			sbview.append("<br /><br />");
			sbview.append(actGuiUtils.printGreyLine());
			sbview.append("<br />");
			sbview.append(printReturnToManageCustomFieldsPage(issactionobjkey));	
		}
				
		return sbview.toString();
	}
	
	/**
	 * 
	 * @param issfilterkey
	 * @param issactionobjkey
	 * @param contName
	 * @param currentstate
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public String printDynamicRemoveCustomFields(EtsIssFilterObjectKey issfilterkey, EtsIssObjectKey issactionobjkey, String contName, int currentstate) throws SQLException, Exception { 
		StringBuffer sbview = new StringBuffer();
		EtsIssUpdateCustFieldBdlg updateCustFieldBdlg = new EtsIssUpdateCustFieldBdlg(issactionobjkey, currentstate);
		
		EtsCustFieldInfoModel custFieldModel = updateCustFieldBdlg.getFirstPageDets();
	
		if(custFieldModel.getFieldLabel().size() > 0 ) {
			sbview.append( printExistingCustomFields(issfilterkey, custFieldModel) );			
			sbview.append("<br />");
			sbview.append(actGuiUtils.printGreyLine());
			sbview.append("<br />");
			sbview.append( printContinueRetToMainPage(contName, issactionobjkey) );
			sbview.append("<br />");
			sbview.append(actGuiUtils.printGreyLine());
		
		} else {
			sbview.append("User can remove an existing custom fields of this workspace. Currently there is no custom fields added to this workspace.");  
			sbview.append("<br /><br />");
			sbview.append(actGuiUtils.printGreyLine());
			sbview.append("<br />");
			sbview.append(printReturnToManageCustomFieldsPage(issactionobjkey));				
		}
		return sbview.toString();
	}
	
	/**
	 * 
	 * @param issfilterkey
	 * @param custFieldModel
	 * @return
	 */	
	public String printUpdateCustomFields(EtsIssFilterObjectKey issfilterkey, EtsCustFieldInfoModel custFieldModel) {
		
		StringBuffer sbview = new StringBuffer();

		String loginUserIrId = issfilterkey.getEs().gIR_USERN;
		String etsprojid = issfilterkey.getProjectId();
		String istype = issfilterkey.getProblemType();
		String tc = issfilterkey.getTc();
		String linkid = issfilterkey.getLinkid();
		String issopn = issfilterkey.getOpn();
		HashMap propMap = issfilterkey.getPropMap();
		EtsIssUserRolesModel usrRolesModel = issfilterkey.getUsrRolesModel();
		
		boolean bladeUsrInt = false;

		try {
			bladeUsrInt = usrRolesModel.isBladeUsrInt();

			sbview.append("<table summary=\"custField\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"600\">\n");
			if (usrRolesModel.isUsrReqIssueType()) {
				ArrayList arrLstFieldLabel = custFieldModel.getFieldLabel();
				ArrayList arrLstFieldId = custFieldModel.getFieldId();	
				for(int i=1; i<= arrLstFieldLabel.size(); i++ ) {
					sbview.append("<tr>\n");
					sbview.append("<td  valign=\"top\"> \n");
					sbview.append("<label for=\"custField"  + i + "\">&nbsp;<b>Field " + arrLstFieldId.get(i-1).toString() +  " label : </b> </label>\n");
					sbview.append("<input type=\"hidden\" name=\"custFieldId"  + i + "\" value=\"" + arrLstFieldId.get(i-1).toString() +  "\" id=\"custFieldId" + i + "\" />");					
					sbview.append("<input type=\"text\"  id=\"custFieldLabel" + i + "\" align=\"left\" class=\"iform\" maxlength=\"12\" name=\"custFieldLabel" + i + "\" size=\"12\" src=\"\" style=\"width:100px\" width=\"100px\" value=\"" + arrLstFieldLabel.get(i-1).toString().replaceAll("\"", "&quot;") + "\" />");					
					sbview.append("</td> \n");
					sbview.append("</tr> \n");							
				}	
			}
			sbview.append("</table> \n");

			
			sbview.append("<input type=\"hidden\" name=\"op\" value=\"2431\" />");
		
		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in printUpdateCustomFields ", loginUserIrId );

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();
			}
		}	  		
		return sbview.toString();		
	}
	
	/**
	 * 
	 * @param issfilterkey
	 * @param custFieldModel
	 * @return
	 */
	public String printExistingCustomFields(EtsIssFilterObjectKey issfilterkey, EtsCustFieldInfoModel custFieldModel) {
		
			StringBuffer sbview = new StringBuffer();

			String loginUserIrId = issfilterkey.getEs().gIR_USERN;
			String etsprojid = issfilterkey.getProjectId();
			String istype = issfilterkey.getProblemType();
			String tc = issfilterkey.getTc();
			String linkid = issfilterkey.getLinkid();
			String issopn = issfilterkey.getOpn();
			HashMap propMap = issfilterkey.getPropMap();
			EtsIssUserRolesModel usrRolesModel = issfilterkey.getUsrRolesModel();
			
			boolean bladeUsrInt = false;

			try {
				bladeUsrInt = usrRolesModel.isBladeUsrInt();

				sbview.append("<table summary=\"custField\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"600\">\n");
				if (usrRolesModel.isUsrReqIssueType()) {
					ArrayList arrLstFieldLabel = custFieldModel.getFieldLabel();
					ArrayList arrLstFieldId = custFieldModel.getFieldId();	
					for(int i=1; i<= arrLstFieldLabel.size(); i++ ) {
						sbview.append("<tr>\n");
						sbview.append("<td  valign=\"top\"> \n");
						sbview.append("<input type=\"checkbox\" name=\"custField" + i + "\" value=\"" + arrLstFieldId.get(i-1).toString()+ "\" id=\"custField" + i + "\" />");
						sbview.append("<label for=\"custField"  + i + "\">&nbsp;<b>" + arrLstFieldLabel.get(i-1).toString() +  "</b> </label>\n");
						sbview.append("</td> \n");
						sbview.append("</tr> \n");							
					}	
				}
				sbview.append("</table> \n");

				
				sbview.append("<input type=\"hidden\" name=\"op\" value=\"2451\" />");
			
			} catch (Exception ex) {

				AmtCommonUtils.LogGenExpMsg(ex, "General Exception in printCustomFieldsActionLinks ", loginUserIrId );

				if (ex != null) {
					SysLog.log(SysLog.ERR, this, ex);
					ex.printStackTrace();
				}
			}	  
			
			return sbview.toString();
		
		}	

	/**
	 * 
	 * @param issfilterkey
	 * @param custFieldModel
	 * @return
	 */
	public String printUpdatedCustomFields(EtsIssFilterObjectKey issfilterkey, EtsCustFieldInfoModel custFieldModel) {
	
		StringBuffer sbview = new StringBuffer();

		String loginUserIrId = issfilterkey.getEs().gIR_USERN;
		String etsprojid = issfilterkey.getProjectId();
		String istype = issfilterkey.getProblemType();
		String tc = issfilterkey.getTc();
		String linkid = issfilterkey.getLinkid();
		String issopn = issfilterkey.getOpn();
		HashMap propMap = issfilterkey.getPropMap();
		EtsIssUserRolesModel usrRolesModel = issfilterkey.getUsrRolesModel();
		
		boolean bladeUsrInt = false;

		try {
			bladeUsrInt = usrRolesModel.isBladeUsrInt();
		
			sbview.append("<table summary=\"custField\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"600\">\n");
			if (usrRolesModel.isUsrReqIssueType()) {
				ArrayList arrLstFieldLabel = custFieldModel.getFieldLabel();
				ArrayList arrLstFieldId = custFieldModel.getFieldId();	
				for(int i=1; i<= arrLstFieldLabel.size(); i++ ) {
					sbview.append("<tr>\n");
					sbview.append("<td  valign=\"top\"> \n");
					sbview.append("<label for=\"custFieldId"  + i + "\">&nbsp;<b>Field " + arrLstFieldId.get(i-1).toString() +  " : </b> </label>\n");
					sbview.append("<label for=\"custFieldLabel"  + i + "\">&nbsp;<b>" + arrLstFieldLabel.get(i-1).toString() +  "</b> </label>\n");
					sbview.append("</td> \n");
					sbview.append("</tr> \n");							
				}	
			}
			sbview.append("</table> \n");				
			sbview.append("<input type=\"hidden\" name=\"op\" value=\"2434\" />");
		
		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in printUpdatedCustomFields ", loginUserIrId );

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();
			}
		}	  		
		return sbview.toString();
	}	

	
	/**
	 * 
	 * @param issfilterkey
	 * @param custFieldModel
	 * @return
	 */
	public String printSelectedCustomFields(EtsIssFilterObjectKey issfilterkey, EtsCustFieldInfoModel custFieldModel) {
		
			StringBuffer sbview = new StringBuffer();

			String loginUserIrId = issfilterkey.getEs().gIR_USERN;
			String etsprojid = issfilterkey.getProjectId();
			String istype = issfilterkey.getProblemType();
			String tc = issfilterkey.getTc();
			String linkid = issfilterkey.getLinkid();
			String issopn = issfilterkey.getOpn();
			HashMap propMap = issfilterkey.getPropMap();
			EtsIssUserRolesModel usrRolesModel = issfilterkey.getUsrRolesModel();
			
			boolean bladeUsrInt = false;

			try {
				bladeUsrInt = usrRolesModel.isBladeUsrInt();
			
				sbview.append("<table summary=\"custField\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"600\">\n");
				if (usrRolesModel.isUsrReqIssueType()) {
					ArrayList arrLstFieldLabel = custFieldModel.getFieldLabel();
					ArrayList arrLstFieldId = custFieldModel.getFieldId();	
					for(int i=1; i<= arrLstFieldLabel.size(); i++ ) {
						sbview.append("<tr>\n");
						sbview.append("<td  valign=\"top\"> \n");
						sbview.append("<label for=\"custField"  + i + "\">&nbsp;<b>Field " + arrLstFieldId.get(i-1).toString() +  " : </b> </label>\n");
						sbview.append("<label for=\"custField"  + i + "\">&nbsp;<b>" + arrLstFieldLabel.get(i-1).toString() +  "</b> </label>\n");
						sbview.append("</td> \n");
						sbview.append("</tr> \n");							
					}	
				}
				sbview.append("</table> \n");				
				sbview.append("<input type=\"hidden\" name=\"op\" value=\"2454\" />");
			
			} catch (Exception ex) {

				AmtCommonUtils.LogGenExpMsg(ex, "General Exception in printCustomFieldsActionLinks ", loginUserIrId );

				if (ex != null) {
					SysLog.log(SysLog.ERR, this, ex);
					ex.printStackTrace();
				}
			}	  
			
			return sbview.toString();
		
		}	
		
	
    /**
     * 
     * @param custFields
     * @return
     */
	public String printSelectOptions(int custFields) {
		StringBuffer sb = new StringBuffer();
		for (int i = 1; i <= custFields; i++) {			
			sb.append("<option value=\"" + i + "\" >" + i + "</option>\n");
		}
		return sb.toString();
	}
	
	
	/**
	 * 
	 * @param issfilterkey
	 * @param newCustFields
	 * @return
	 */
	public String printNewCustomFields(EtsIssFilterObjectKey issfilterkey, int newCustFields, Object  obj) {

		StringBuffer sbview = new StringBuffer();
		
		String loginUserIrId = issfilterkey.getEs().gIR_USERN;
		String etsprojid = issfilterkey.getProjectId();
		String istype = issfilterkey.getProblemType();
		String tc = issfilterkey.getTc();
		String linkid = issfilterkey.getLinkid();
		String issopn = issfilterkey.getOpn();
		HashMap propMap = issfilterkey.getPropMap();
		EtsIssUserRolesModel usrRolesModel = issfilterkey.getUsrRolesModel();
		EtsIssueAddFieldsDAO issAddFieldsDAO = new EtsIssueAddFieldsDAO();
		ArrayList arrLstCustFieldIDs = issAddFieldsDAO.getCurrentCustFieldId(etsprojid);
		ArrayList arrLstAvailableCustFieldIDs = new ArrayList();
				
		for(int i = 1; i <= EtsIssueConstants.MAXCUSTFIELDS; i++ ) {
		  if(! arrLstCustFieldIDs.contains(new Integer(i) ) ) {
		  	//arrLstAvailableCustFieldIDs.add((i-1),  (new Integer( i ) ) );
		  	arrLstAvailableCustFieldIDs.add( (new Integer( i ) ) );
		  }
		}
		
		int currentCustFieldID = 0;
		//String currentCustFieldId = "";
		if(obj != null ) {			
			if(AmtCommonUtils.isResourceDefined(obj.toString()) ) 
				currentCustFieldID = Integer.parseInt(obj.toString());
			
		}
				
		
		boolean bladeUsrInt = false;

		try {
			bladeUsrInt = usrRolesModel.isBladeUsrInt();

			sbview.append("<table summary=\"custField\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"600\">\n");
			if (usrRolesModel.isUsrReqIssueType()) {		
				for(int i=1; i<= newCustFields; i++ ) {
					sbview.append("<tr>\n");
					sbview.append("<td  valign=\"top\"> \n");
					sbview.append("<label for=\"custField"  + i + "\">&nbsp;<b>Field " + (arrLstAvailableCustFieldIDs.get(i-1).toString() ) +  " label</b> : </label>");
					sbview.append("<input id=\"custField"  + i + "\" align=\"left\" class=\"iform\" maxlength=\"12\" name=\"custField" + i + "\" size=\"12\" src=\"\" type=\"text\" style=\"width:100px\" width=\"100px\" value=\"\" />\n");
					sbview.append("</td> \n");
					sbview.append("</tr> \n");							
				}	
			}
			sbview.append("</table> \n");
			sbview.append("<input type=\"hidden\" name=\"numCustFields\" value=\""+ newCustFields +"\" />");
			sbview.append("<input type=\"hidden\" name=\"hidCustFieldId\"  value=\"" + currentCustFieldID + "\" />");	
			
			sbview.append("<input type=\"hidden\" name=\"op\" value=\"2413\" />");
		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in printCustomFieldsActionLinks ", loginUserIrId );

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();
			}
		}	  
			return sbview.toString();
	}
	
	public String printNewCustomFieldsInfo(EtsIssFilterObjectKey issfilterkey, EtsCustFieldInfoModel custFieldModel) {

		StringBuffer sbview = new StringBuffer();
		
		String loginUserIrId = issfilterkey.getEs().gIR_USERN;
		String etsprojid = issfilterkey.getProjectId();
		String istype = issfilterkey.getProblemType();
		String tc = issfilterkey.getTc();
		String linkid = issfilterkey.getLinkid();
		String issopn = issfilterkey.getOpn();
		HashMap propMap = issfilterkey.getPropMap();
		EtsIssUserRolesModel usrRolesModel = issfilterkey.getUsrRolesModel();
				
		boolean bladeUsrInt = false;

		try {
			bladeUsrInt = usrRolesModel.isBladeUsrInt();

			sbview.append("<table summary=\"custField\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" width=\"600\">\n");
			if (usrRolesModel.isUsrReqIssueType()) {		
				for(int i=1; i<= custFieldModel.getFieldId().size(); i++ ) {
					sbview.append("<tr>\n");
					sbview.append("<td  valign=\"top\"> \n");
					sbview.append("<label for=\"custField"  + i + "\">&nbsp;<b>Field " + custFieldModel.getFieldId().get(i-1).toString() +  " label</b> : </label>");
					//sbview.append("<input id=\"custField"  + i + "\" align=\"left\" class=\"iform\" maxlength=\"12\" name=\"custField" + i + "\" size=\"12\" src=\"\" type=\"text\" style=\"width:100px\" width=\"100px\" value=\"" + custFieldModel.getFieldLabel().get(i-1).toString() + "\" READONLY />\n");
					sbview.append("<label for=\"custField"  + i + "\">  " + custFieldModel.getFieldLabel().get(i-1).toString() +  "</label>");
					sbview.append("</td> \n");
					sbview.append("</tr> \n");							
				}	
			}
			sbview.append("</table> \n");
			//sbview.append("<input type=\"hidden\" name=\"numCustFields\" value=\""+ newCustFields +"\" />");
			sbview.append("<input type=\"hidden\" name=\"op\" value=\"2414\" />");
		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in printCustomFieldsActionLinks ", loginUserIrId );

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();
			}
		}	  
			return sbview.toString();
	}
	
	
	/**
	 * This method will print Section Headers required for each of section in blue
	 */

	public String printSectnHeader(String headerName) {
		return comGuiUtils.printSectnHeader(headerName);
	}


	/**
	 * To print submit button
	 * @param contnName
	 * @return
	 */
	public String printSubmit(String subName) {			
		return comGuiUtils.printSubmit(subName);
	}
	
	/**
	 * To print continue button
	 * @param contName
	 * @return
	 */
	public String printContinue(String contName) {
		return comGuiUtils.printContinue(contName);
	}
	
	/**
	 * To print Submit and cancel buttons
	 * @param contnName
	 * @param cancName
	 * @return
	 */
	public String printSubmitCancel(String subName, String cancName) {
		return comGuiUtils.printSubmitCancel(subName, cancName);
	}

	public String printSubmitRetToMainPage(String subName, EtsIssObjectKey etsIssObjKey) {		
		StringBuffer sbview = new StringBuffer();
		sbview.append("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n");
		sbview.append("<tr>\n");
		sbview.append("<td width=\"130\" align=\"left\">\n");
		sbview.append(comGuiUtils.printSubmit(subName));
		sbview.append("</td>\n");
		sbview.append("<td align=\"left\">\n");
		sbview.append(comGuiUtils.printReturnToMainPageWithCancel(etsIssObjKey));
		sbview.append("</td>\n");
		sbview.append("</tr>\n");
		sbview.append("</table>\n");
		return sbview.toString();				
	}		
	
	/**
	 * 
	 * @param contName
	 * @param etsIssObjKey
	 * @return
	 */
	public String printContinueRetToMainPage(String contName, EtsIssObjectKey etsIssObjKey) {
		return comGuiUtils.printContinueRetToMainPage(contName, etsIssObjKey);
	}	
}