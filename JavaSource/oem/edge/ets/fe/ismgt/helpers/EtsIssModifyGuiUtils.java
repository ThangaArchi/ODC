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
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.ismgt.dao.EtsProjMemberDAO;
import oem.edge.ets.fe.ismgt.dao.IssueInfoDAO;
import oem.edge.ets.fe.ismgt.model.EtsIssObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsIssProbInfoUsr1Model;
import oem.edge.ets.fe.ismgt.model.EtsIssUserRolesModel;
import oem.edge.ets.fe.ismgt.resources.EtsIssueActionConstants;
import oem.edge.ets.fe.ismgt.resources.EtsIssueConstants;

/**
 * @author V2PHANI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EtsIssModifyGuiUtils implements EtsIssueConstants, EtsIssueActionConstants {

	public static final String VERSION = "1.20.1.17";

	private EtsIssCommonGuiUtils comGuiUtils;
	private EtsIssFilterGuiUtils fltrGuiUtils;

	/**
	 * 
	 */
	public EtsIssModifyGuiUtils() {
		super();
		this.comGuiUtils = new EtsIssCommonGuiUtils();
		this.fltrGuiUtils = new EtsIssFilterGuiUtils();
	}

	/**
		 * 
		 * 
		 * @return
		 */

	public String printAnchorLinksForModify(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) throws Exception {

		StringBuffer sbview = new StringBuffer();

		//		get roles model
		EtsIssUserRolesModel usrRolesModel = etsIssObjKey.getUsrRolesModel();

		String probState = AmtCommonUtils.getTrimStr(usr1InfoModel.getProbState());
		int usr_seq_no = usr1InfoModel.getUsr_seq_no();
		int cq_seq_no = usr1InfoModel.getCq_seq_no();

		String checkUserRole = ETSUtils.checkUserRole(etsIssObjKey.getEs(), etsIssObjKey.getProj().getProjectId());

		sbview.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"600\">");
		sbview.append("<tr>");
		sbview.append("<td>");
		sbview.append("<a href=\"#0\">Description</a>&nbsp;&nbsp;|&nbsp;&nbsp;");
		sbview.append("<a href=\"#1\">Identification</a>&nbsp;&nbsp;|&nbsp;&nbsp;");
		sbview.append("<a href=\"#2\">Attachments</a>&nbsp;&nbsp;|&nbsp;&nbsp;");
		sbview.append("<a href=\"#3\">Custom fields</a>&nbsp;&nbsp;|&nbsp;&nbsp;");
		
		if (etsIssObjKey.getEs().gDECAFTYPE.equals("I")) { //only for internals
			sbview.append("<a href=\"#4\">Security classification</a>&nbsp;&nbsp;|&nbsp;&nbsp;");
		}

		if (!etsIssObjKey.isProjBladeType()) {
			sbview.append("<a href=\"#5\">Notification list</a>&nbsp;&nbsp;|&nbsp;&nbsp;");
		} else {

			if (usrRolesModel.isBladeUsrInt()) {

				sbview.append("<a href=\"#5\">Notification list</a>&nbsp;&nbsp;|&nbsp;&nbsp;");

			}

		}
		sbview.append("<a href=\"#6\">Comments</a>");
		sbview.append("</td>");
		sbview.append("</tr>");
		sbview.append("</table>");

		return sbview.toString();
	}

	/**
	 * 
	 * @param etsIssObjKey
	 * @param usr1InfoModel
	 * @param propMap
	 * @return
	 */
	public String printCustFieldsForMod(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) {

		StringBuffer sbview = new StringBuffer();
		int counter = 0;
		// custom  field c1..c8 vals
		String prevFieldC1Val = "";
		String prevFieldC2Val = "";
		String prevFieldC3Val = "";
		String prevFieldC4Val = "";
		String prevFieldC5Val = "";
		String prevFieldC6Val = "";
		String prevFieldC7Val = "";
		String prevFieldC8Val = "";
		
		// custom  field c1..c8 label
		String FieldC1Label = "";
		String FieldC2Label = "";
		String FieldC3Label = "";
		String FieldC4Label = "";
		String FieldC5Label = "";
		String FieldC6Label = "";
		String FieldC7Label = "";
		String FieldC8Label = "";

		ArrayList prevFieldC1ValList = usr1InfoModel.getPrevFieldC1List();
		ArrayList prevFieldC2ValList = usr1InfoModel.getPrevFieldC2List();
		ArrayList prevFieldC3ValList = usr1InfoModel.getPrevFieldC3List();
		ArrayList prevFieldC4ValList = usr1InfoModel.getPrevFieldC4List();
		ArrayList prevFieldC5ValList = usr1InfoModel.getPrevFieldC5List();
		ArrayList prevFieldC6ValList = usr1InfoModel.getPrevFieldC6List();
		ArrayList prevFieldC7ValList = usr1InfoModel.getPrevFieldC7List();
		ArrayList prevFieldC8ValList = usr1InfoModel.getPrevFieldC8List();

		//cust fields c1..c8
		if (AmtCommonUtils.isResourceDefined( usr1InfoModel.getFieldC1DispName()  ) ) {
			if(EtsIssFilterUtils.isArrayListDefnd(prevFieldC1ValList) )
				prevFieldC1Val = (String) prevFieldC1ValList.get(0);
			else 
				prevFieldC1Val = "- - -";
			
			FieldC1Label =  usr1InfoModel.getFieldC1DispName();
		}

		if (AmtCommonUtils.isResourceDefined( usr1InfoModel.getFieldC2DispName()  ) ) {
			if(EtsIssFilterUtils.isArrayListDefnd(prevFieldC2ValList) )
				prevFieldC2Val = (String) prevFieldC2ValList.get(0);
			else
				prevFieldC2Val = "- - -";
			
			FieldC2Label =  usr1InfoModel.getFieldC2DispName();			
		}

		if (AmtCommonUtils.isResourceDefined( usr1InfoModel.getFieldC3DispName()  ) ) {
			if( EtsIssFilterUtils.isArrayListDefnd(prevFieldC3ValList)  ) 
				prevFieldC3Val = (String) prevFieldC3ValList.get(0);
			else
				prevFieldC3Val = "- - -";
			
			FieldC3Label =  usr1InfoModel.getFieldC3DispName();
		}

		if (AmtCommonUtils.isResourceDefined( usr1InfoModel.getFieldC4DispName()  ) ) {
			if(EtsIssFilterUtils.isArrayListDefnd(prevFieldC4ValList) ) 				
				prevFieldC4Val = (String) prevFieldC4ValList.get(0);
			else 
				prevFieldC4Val = "- - -";
			
			FieldC4Label =  usr1InfoModel.getFieldC4DispName();
		}

		if (AmtCommonUtils.isResourceDefined( usr1InfoModel.getFieldC5DispName()  ) ) {
			if(EtsIssFilterUtils.isArrayListDefnd(prevFieldC5ValList))
				prevFieldC5Val = (String) prevFieldC5ValList.get(0);
			else 
				prevFieldC5Val = "- - -";
			
			FieldC5Label =  usr1InfoModel.getFieldC5DispName();
		}

		if (AmtCommonUtils.isResourceDefined( usr1InfoModel.getFieldC6DispName()  ) ) {
			if(EtsIssFilterUtils.isArrayListDefnd(prevFieldC6ValList) )
				prevFieldC6Val = (String) prevFieldC6ValList.get(0);
			else
				prevFieldC6Val = "- - -";
			
			FieldC6Label =  usr1InfoModel.getFieldC6DispName();
		}

		if (AmtCommonUtils.isResourceDefined( usr1InfoModel.getFieldC7DispName()  ) ) {
			if(EtsIssFilterUtils.isArrayListDefnd(prevFieldC7ValList))
				prevFieldC7Val = (String) prevFieldC7ValList.get(0);
			else
				prevFieldC7Val = "- - -";
			
			FieldC7Label =  usr1InfoModel.getFieldC7DispName();
		}

		if (AmtCommonUtils.isResourceDefined( usr1InfoModel.getFieldC8DispName()  ) ) {
			if(EtsIssFilterUtils.isArrayListDefnd(prevFieldC8ValList))
				prevFieldC8Val = (String) prevFieldC8ValList.get(0);
			else
				prevFieldC8Val = "- - -";
			
			FieldC8Label =  usr1InfoModel.getFieldC8DispName();
		}

		sbview.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"4\" width=\"600\">\n");

		///cust fields vals
		if (AmtCommonUtils.isResourceDefined(prevFieldC1Val) && AmtCommonUtils.isResourceDefined(FieldC1Label) ) {
			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"25%\"><b>" + FieldC1Label + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"75%\">" + prevFieldC1Val + "</td>");
			sbview.append("</tr>");
			counter++;
		}

		if (AmtCommonUtils.isResourceDefined(prevFieldC2Val) && AmtCommonUtils.isResourceDefined(FieldC2Label) ) {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"25%\"><b>" + FieldC2Label + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"75%\">" + prevFieldC2Val + "</td>");
			sbview.append("</tr>");
			counter++;
		}

		if (AmtCommonUtils.isResourceDefined(prevFieldC3Val) && AmtCommonUtils.isResourceDefined(FieldC3Label) ) {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"25%\"><b>" + FieldC3Label + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"75%\">" + prevFieldC3Val + "</td>");
			sbview.append("</tr>");
			counter++;
		}

		if (AmtCommonUtils.isResourceDefined(prevFieldC4Val) && AmtCommonUtils.isResourceDefined(FieldC4Label) ) {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"25%\"><b>" + FieldC4Label + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"75%\">" + prevFieldC4Val + "</td>");
			sbview.append("</tr>");
			counter++;
		}

		if (AmtCommonUtils.isResourceDefined(prevFieldC5Val) && AmtCommonUtils.isResourceDefined(FieldC5Label) ) {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"25%\"><b>" + FieldC5Label + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"75%\">" + prevFieldC5Val + "</td>");
			sbview.append("</tr>");
			counter++;
		}

		if (AmtCommonUtils.isResourceDefined(prevFieldC6Val) && AmtCommonUtils.isResourceDefined(FieldC6Label) ) {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"25%\"><b>" + FieldC6Label + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"75%\">" + prevFieldC6Val + "</td>");
			sbview.append("</tr>");
			counter++;
		}

		if (AmtCommonUtils.isResourceDefined(prevFieldC7Val) && AmtCommonUtils.isResourceDefined(FieldC7Label) ) {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"25%\"><b>" + FieldC7Label + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"75%\">" + prevFieldC7Val + "</td>");
			sbview.append("</tr>");
			counter++;
		}

		if (AmtCommonUtils.isResourceDefined(prevFieldC8Val) && AmtCommonUtils.isResourceDefined(FieldC8Label) ) {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"25%\"><b>" + FieldC8Label + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"75%\">" + prevFieldC8Val + "</td>");
			sbview.append("</tr>");
			counter++;
		}		
		
	
		if(counter == 0 ) {
			sbview.append("<tr><td  valign=\"top\" width =\"100%\" align=\"left\"> \n");
			sbview.append("Currently, there are no custom fields added to this workspace.");
			sbview.append("</td>\n");
			sbview.append("</tr>\n");
			
		}	
		
		sbview.append("</table>");

		return sbview.toString();
	}
	
	
	public String printCustFieldsForModifyEdit(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) {

		StringBuffer sbview = new StringBuffer();
		int counter = 0;
		// custom  field c1..c8 vals
		String prevFieldC1Val = "";
		String prevFieldC2Val = "";
		String prevFieldC3Val = "";
		String prevFieldC4Val = "";
		String prevFieldC5Val = "";
		String prevFieldC6Val = "";
		String prevFieldC7Val = "";
		String prevFieldC8Val = "";
		
		// custom  field c1..c8 label
		String FieldC1Label = "";
		String FieldC2Label = "";
		String FieldC3Label = "";
		String FieldC4Label = "";
		String FieldC5Label = "";
		String FieldC6Label = "";
		String FieldC7Label = "";
		String FieldC8Label = "";

		ArrayList prevFieldC1ValList = usr1InfoModel.getPrevFieldC1List();
		ArrayList prevFieldC2ValList = usr1InfoModel.getPrevFieldC2List();
		ArrayList prevFieldC3ValList = usr1InfoModel.getPrevFieldC3List();
		ArrayList prevFieldC4ValList = usr1InfoModel.getPrevFieldC4List();
		ArrayList prevFieldC5ValList = usr1InfoModel.getPrevFieldC5List();
		ArrayList prevFieldC6ValList = usr1InfoModel.getPrevFieldC6List();
		ArrayList prevFieldC7ValList = usr1InfoModel.getPrevFieldC7List();
		ArrayList prevFieldC8ValList = usr1InfoModel.getPrevFieldC8List();

		//cust fields c1..c8
		if (AmtCommonUtils.isResourceDefined( usr1InfoModel.getFieldC1DispName()  ) ) {
			if(EtsIssFilterUtils.isArrayListDefnd(prevFieldC1ValList) )
				prevFieldC1Val = prevFieldC1ValList.get(0).toString().replaceAll("\"", "&quot;");
			
			FieldC1Label =  usr1InfoModel.getFieldC1DispName();		
		}

		if (AmtCommonUtils.isResourceDefined( usr1InfoModel.getFieldC2DispName()  ) ) {
			if(EtsIssFilterUtils.isArrayListDefnd(prevFieldC2ValList))
				prevFieldC2Val = prevFieldC2ValList.get(0).toString().replaceAll("\"", "&quot;");
				
			FieldC2Label =  usr1InfoModel.getFieldC2DispName();			
		}

		if (AmtCommonUtils.isResourceDefined( usr1InfoModel.getFieldC3DispName()  ) ) {
			if(EtsIssFilterUtils.isArrayListDefnd(prevFieldC3ValList) )
				prevFieldC3Val = prevFieldC3ValList.get(0).toString().replaceAll("\"", "&quot;");
			
			FieldC3Label =  usr1InfoModel.getFieldC3DispName();
		}

		if (AmtCommonUtils.isResourceDefined( usr1InfoModel.getFieldC4DispName()  ) ) {
			if( EtsIssFilterUtils.isArrayListDefnd(prevFieldC4ValList) )
				prevFieldC4Val =  prevFieldC4ValList.get(0).toString().replaceAll("\"", "&quot;");
			
			FieldC4Label =  usr1InfoModel.getFieldC4DispName();
		}

		if (AmtCommonUtils.isResourceDefined( usr1InfoModel.getFieldC5DispName()  ) ) {
			if(EtsIssFilterUtils.isArrayListDefnd(prevFieldC5ValList) )
				prevFieldC5Val =  prevFieldC5ValList.get(0).toString().replaceAll("\"", "&quot;");
			
			FieldC5Label =  usr1InfoModel.getFieldC5DispName();
		}

		if (AmtCommonUtils.isResourceDefined( usr1InfoModel.getFieldC6DispName()  ) ) {
			if(EtsIssFilterUtils.isArrayListDefnd(prevFieldC6ValList) )
				prevFieldC6Val =  prevFieldC6ValList.get(0).toString().replaceAll("\"", "&quot;");
			
			FieldC6Label =  usr1InfoModel.getFieldC6DispName();
		}

		if (AmtCommonUtils.isResourceDefined( usr1InfoModel.getFieldC7DispName()  ) ) {
			if(EtsIssFilterUtils.isArrayListDefnd(prevFieldC7ValList) )
				prevFieldC7Val =  prevFieldC7ValList.get(0).toString().replaceAll("\"", "&quot;");
			
			FieldC7Label =  usr1InfoModel.getFieldC7DispName();
		}

		if (AmtCommonUtils.isResourceDefined( usr1InfoModel.getFieldC8DispName()  ) ) {
			if(EtsIssFilterUtils.isArrayListDefnd(prevFieldC8ValList) )
				prevFieldC8Val =  prevFieldC8ValList.get(0).toString().replaceAll("\"", "&quot;");
			
			FieldC8Label =  usr1InfoModel.getFieldC8DispName();
		}

		sbview.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"4\" width=\"600\">\n");

		///cust fields vals
		//if (AmtCommonUtils.isResourceDefined(prevFieldC1Val) && AmtCommonUtils.isResourceDefined(FieldC1Label) ) {
		if (AmtCommonUtils.isResourceDefined(FieldC1Label) ) {			
			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"25%\"><b>" + FieldC1Label + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"75%\">");

			//sbview.append("<input type=\"hidden\" name=\"FieldC1\" value=\"" + FieldC1Label +  "\" id=\"FieldC1\" />");
			sbview.append("<input type=\"text\" name=\"FieldC1Val\" value=\"" + prevFieldC1Val + "\" id=\"FieldC1Val\" maxlength=\"30\"  size=\"31\" />");			
			
			sbview.append("</td></tr>");
			counter++;
		}
		
		//if (AmtCommonUtils.isResourceDefined(prevFieldC2Val) && AmtCommonUtils.isResourceDefined(FieldC2Label) ) {
		if (AmtCommonUtils.isResourceDefined(FieldC2Label) ) {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"25%\"><b>" + FieldC2Label + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"75%\">");

			//sbview.append("<input type=\"hidden\" name=\"FieldC2\" value=\"" + FieldC2Label +  "\" id=\"FieldC2\" />");
			sbview.append("<input type=\"text\" name=\"FieldC2Val\" value=\"" + prevFieldC2Val + "\" id=\"FieldC2Val\" maxlength=\"30\"  size=\"31\" />");			
			
			sbview.append("</td></tr>");
			counter++;
		}

		//if (AmtCommonUtils.isResourceDefined(prevFieldC3Val) && AmtCommonUtils.isResourceDefined(FieldC3Label) ) {
		if (AmtCommonUtils.isResourceDefined(FieldC3Label) ) {
			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"25%\"><b>" + FieldC3Label + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"75%\">");
			sbview.append("<input type=\"text\" name=\"FieldC3Val\" value=\"" + prevFieldC3Val + "\" id=\"FieldC3Val\" maxlength=\"30\"  size=\"31\" />");						
			sbview.append("</td></tr>");
			counter++;
		}

		//if (AmtCommonUtils.isResourceDefined(prevFieldC4Val) && AmtCommonUtils.isResourceDefined(FieldC4Label) ) {
		if (AmtCommonUtils.isResourceDefined(FieldC4Label) ) {
			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"25%\"><b>" + FieldC4Label + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"75%\">");
			sbview.append("<input type=\"text\" name=\"FieldC4Val\" value=\"" + prevFieldC4Val + "\" id=\"FieldC4Val\" maxlength=\"30\"  size=\"31\" />");			
			
			sbview.append("</td></tr>");
			counter++;
		}

		//if (AmtCommonUtils.isResourceDefined(prevFieldC5Val) && AmtCommonUtils.isResourceDefined(FieldC5Label) ) {
		if (AmtCommonUtils.isResourceDefined(FieldC5Label) ) {
			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"25%\"><b>" + FieldC5Label + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"75%\">");
			sbview.append("<input type=\"text\" name=\"FieldC5Val\" value=\"" + prevFieldC5Val + "\" id=\"FieldC5Val\" maxlength=\"30\"  size=\"31\" />");						
			sbview.append("</td></tr>");
			counter++;
		}

		//if (AmtCommonUtils.isResourceDefined(prevFieldC6Val) && AmtCommonUtils.isResourceDefined(FieldC6Label) ) {
		if (AmtCommonUtils.isResourceDefined(FieldC6Label) ) {
			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"25%\"><b>" + FieldC6Label + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"75%\">");
			sbview.append("<input type=\"text\" name=\"FieldC6Val\" value=\"" + prevFieldC6Val + "\" id=\"FieldC6Val\" maxlength=\"30\"  size=\"31\" />");						
			sbview.append("</td></tr>");
			counter++;
		}

		//if (AmtCommonUtils.isResourceDefined(prevFieldC7Val) && AmtCommonUtils.isResourceDefined(FieldC7Label) ) {
		if (AmtCommonUtils.isResourceDefined(FieldC7Label) ) {
			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"25%\"><b>" + FieldC7Label + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"75%\">");
			sbview.append("<input type=\"text\" name=\"FieldC7Val\" value=\"" + prevFieldC7Val + "\" id=\"FieldC7Val\" maxlength=\"30\"  size=\"31\" />");						
			sbview.append("</td></tr>");
			counter++;
		}

		//if (AmtCommonUtils.isResourceDefined(prevFieldC8Val) && AmtCommonUtils.isResourceDefined(FieldC8Label) ) {
		if (AmtCommonUtils.isResourceDefined(FieldC8Label) ) {
			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"25%\"><b>" + FieldC8Label + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"75%\">");
			sbview.append("<input type=\"text\" name=\"FieldC8Val\" value=\"" + prevFieldC8Val + "\" id=\"FieldC8Val\" maxlength=\"30\"  size=\"31\" />");						
			sbview.append("</td></tr>");
			counter++;
		}		
		
	
		if(counter == 0 ) {
			sbview.append("<tr><td  valign=\"top\" width =\"100%\" align=\"left\"> \n");
			sbview.append("Currently, there are no custom fields added to this workspace.");
			sbview.append("</td>\n");
			sbview.append("</tr>\n");
			
		}	
		
		sbview.append("</table>");

		return sbview.toString();
	}
	

	
	/**
	 * 
	 * @param etsIssObjKey
	 * @param usr1InfoModel
	 * @param propMap
	 * @return
	 */
	public String printIssTypeDynvalsForMod(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) {
		StringBuffer sbview = new StringBuffer();		
		//prev sub types
		String prevSubTypeAVal = "";
		String prevSubTypeBVal = "";
		String prevSubTypeCVal = "";
		String prevSubTypeDVal = "";
		
		String testcase = AmtCommonUtils.getTrimStr(usr1InfoModel.getTestCase());

		ArrayList prevSubTypeAList = usr1InfoModel.getPrevSubTypeAList();
		ArrayList prevSubTypeBList = usr1InfoModel.getPrevSubTypeBList();
		ArrayList prevSubTypeCList = usr1InfoModel.getPrevSubTypeCList();
		ArrayList prevSubTypeDList = usr1InfoModel.getPrevSubTypeDList();

		ArrayList prevProbTypeList = usr1InfoModel.getPrevProbTypeList();
		String issueType = AmtCommonUtils.getTrimStr(usr1InfoModel.getIssueType());

		//sub type a..d
		if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeAList)) {

			prevSubTypeAVal = (String) prevSubTypeAList.get(0);

		}

		if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeBList)) {

			prevSubTypeBVal = (String) prevSubTypeBList.get(0);

		}

		if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeCList)) {

			prevSubTypeCVal = (String) prevSubTypeCList.get(0);

		}

		if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeDList)) {

			prevSubTypeDVal = (String) prevSubTypeDList.get(0);

		}

		sbview.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"4\" width=\"600\">\n");
		sbview.append("<tr>");
		sbview.append("<td  valign=\"top\" align=\"left\" width =\"25%\"><b>" + comGuiUtils.getDefaultIssueTypeLabel(etsIssObjKey) + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"75%\">" + issueType + "</td>");
		sbview.append("</tr>");

		///////////

		//dyn vals//

		if (AmtCommonUtils.isResourceDefined(prevSubTypeAVal) && !prevSubTypeAVal.equals(ETSDEFAULTCQ)) {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"25%\"><b>" + comGuiUtils.getDefaultSubTypeStr(etsIssObjKey, STDSUBTYPE_A, DEFUALTSTDSUBTYPE_A) + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"75%\">" + prevSubTypeAVal + "</td>");
			sbview.append("</tr>");

		}

		if (AmtCommonUtils.isResourceDefined(prevSubTypeBVal) && !prevSubTypeBVal.equals(ETSDEFAULTCQ)) {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"25%\"><b>" + comGuiUtils.getDefaultSubTypeStr(etsIssObjKey, STDSUBTYPE_B, DEFUALTSTDSUBTYPE_B) + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"75%\">" + prevSubTypeBVal + "</td>");
			sbview.append("</tr>");

		}

		if (AmtCommonUtils.isResourceDefined(prevSubTypeCVal) && !prevSubTypeCVal.equals(ETSDEFAULTCQ)) {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"25%\"><b>" + comGuiUtils.getDefaultSubTypeStr(etsIssObjKey, STDSUBTYPE_C, DEFUALTSTDSUBTYPE_C) + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"75%\">" + prevSubTypeCVal + "</td>");
			sbview.append("</tr>");

		}

		if (AmtCommonUtils.isResourceDefined(prevSubTypeDVal) && !prevSubTypeDVal.equals(ETSDEFAULTCQ)) {

			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"25%\"><b>" + comGuiUtils.getDefaultSubTypeStr(etsIssObjKey, STDSUBTYPE_D, DEFUALTSTDSUBTYPE_D) + "</b>:</td><td  valign=\"top\" align=\"left\" width =\"75%\">" + prevSubTypeDVal + "</td>");
			sbview.append("</tr>");

		}
				
		if (AmtCommonUtils.isResourceDefined(testcase)) {
			sbview.append("<tr>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"25%\"> ");
			sbview.append("<b>Test case</b>:");
			sbview.append("</td>");
			sbview.append("<td  valign=\"top\" align=\"left\" width =\"75%\">");
			sbview.append("<pre>");
			sbview.append(testcase);
			sbview.append("</pre>");
			sbview.append("</td>");
			sbview.append("</tr>");

		}

		sbview.append("</table>");

		return sbview.toString();
	}

	/**
	 * To print continue and cancel buttons
	 * @param contnName
	 * @param cancName
	 * @return
	 */
	public String printContCancel(EtsIssProbInfoUsr1Model usr1InfoModel) {

		StringBuffer sbview = new StringBuffer();

		String contnName = "";
		String cancName = "";

		//			states
		int actionstate = usr1InfoModel.getCurrentActionState();
		int usr1nextstate = usr1InfoModel.getNextActionState();
		

		if (usr1nextstate > 0) {

			actionstate = usr1nextstate;
		}

		Global.println("actionstate in print cont button in modify==" + actionstate);

		switch (actionstate) {

			case MODIFYEDITDESCR :

			case MODIFYVALIDERRCONTDESCR :

				contnName = "op_311";
				cancName = "op_312";

				break;

			case MODIFYEDITFILEATTACH :
			case MODIFYFILEATTACH :
			case MODIFYDELETEFILE :

				contnName = "op_320";
				cancName = "op_3123";

				break;


			case MODIFYEDITCUSTOMFIELDS :
				 MODIFYEDITCUSTOMFIELDSCONT :
			     MODIFYVALIDERRCONTCUSTFIELD :				 	

				contnName = "op_3221";
				cancName = "op_3225";

				break;
				
				
				
			case MODIFYEDITNOTIFYLIST :

				contnName = "op_324";
				cancName = "op_3125";

				break;

			case MODIFYISSUEIDENTFDEFAULT :
			case MODIFYVALIDERRADDISSTYPE :

				contnName = "op_328";
				//cancName = "op_3128";
				cancName = "op_unifcancel";

				break;

			case RESOLVEEDITFILEATTACH :
			case RESOLVEFILEATTACH :
			case RESOLVEDELETEFILE :

				contnName = "op_520";
				cancName = "op_5123";

				break;
				

		}

		sbview.append("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n");
		sbview.append("<tr>\n");
		sbview.append("<td width=\"130\" align=\"left\">\n");
		sbview.append(comGuiUtils.printContinue(contnName));
		sbview.append("</td>\n");
		sbview.append("<td align=\"left\">\n");
		sbview.append(comGuiUtils.printCancel(cancName));
		sbview.append("</td>\n");
		sbview.append("</tr>\n");
		sbview.append("</table>\n");

		return sbview.toString();
	}

	/**
			 * 
			 * 
			 * @param etsIssObjKey
			 * @param edgeProblemId
			 * @return
			 * @throws SQLException
			 * @throws Exception
			 */

	public String printAttachedFilesListForModify(String projectId,String edgeProblemId) throws SQLException, Exception {

		EtsIssFileAttachUtils fileUtils = new EtsIssFileAttachUtils();

		IssueInfoDAO infoDao = new IssueInfoDAO();

		if (!infoDao.isIssueSrcPMO(edgeProblemId)) {

			return fileUtils.printAttachedFilesListForModify(projectId,edgeProblemId);

		} else {

			return fileUtils.printAttachedFilesListForCRUpdate(edgeProblemId);

		}

	}

	/**
				 * 
				 * 
				 * @param etsIssObjKey
				 * @param edgeProblemId
				 * @return
				 * @throws SQLException
				 * @throws Exception
				 */

	public String printAttachedFilesListForResolveScr6(String projectId,String edgeProblemId) throws SQLException, Exception {

		EtsIssFileAttachUtils fileUtils = new EtsIssFileAttachUtils();

		IssueInfoDAO infoDao = new IssueInfoDAO();

		if (!infoDao.isIssueSrcPMO(edgeProblemId)) {

			return fileUtils.printAttachedFilesListForModify(projectId,edgeProblemId);

		} else {

			return fileUtils.printAttachedFilesListForPMOIssuesResolveScr6(edgeProblemId);

		}

	}

	/**
					 * 
					 * 
					 * @param etsIssObjKey
					 * @param edgeProblemId
					 * @return
					 * @throws SQLException
					 * @throws Exception
					 */

	public String printAttachedFilesListForResolveScr3(String projectId,String edgeProblemId) throws SQLException, Exception {

		EtsIssFileAttachUtils fileUtils = new EtsIssFileAttachUtils();

		IssueInfoDAO infoDao = new IssueInfoDAO();

		if (!infoDao.isIssueSrcPMO(edgeProblemId)) {

			return fileUtils.printAttachedFilesListForModify(projectId,edgeProblemId);

		} else {

			return fileUtils.printAttachedFilesListForPMOIssuesResolveScr3(edgeProblemId);

		}

	}

	/**
	 * 
	 * 
	 * @param etsIssObjKey
	 * @param usr1InfoModel
	 * @param propMap
	 * @return
	 */

	public String printNotifyListForModify(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) {

		StringBuffer sbsub = new StringBuffer();

		ArrayList notifyList = usr1InfoModel.getNotifyList();
		ArrayList prevNotifyList = usr1InfoModel.getPrevNotifyList();
		String chkIssIbmType = AmtCommonUtils.getTrimStr(usr1InfoModel.getChkIssTypeIbmOnly());
		String propMsg = "";

		//get prop mgs
		propMsg = (String) propMap.get("issue.act.mod.msg6");

		String userType = etsIssObjKey.getEs().gDECAFTYPE;

		//print notify list
		EtsIssActionGuiUtils actGuiUtils = new EtsIssActionGuiUtils();
		sbsub.append(actGuiUtils.printNotifyList(notifyList, prevNotifyList, propMsg, userType, chkIssIbmType));

		return sbsub.toString();

	}

	public String getDynNotifyListMsg(int notifycount) {

		StringBuffer sbfile = new StringBuffer();

		if (notifycount == 0) {

			sbfile.append("Currently, there are no members on issue notification list. You can add or delete members to this list before submitting the issue.");
		}

		if (notifycount == 1) {

			sbfile.append("Currently, there is " + notifycount + " member on issue notification list. You can add or delete members to this list before submitting the issue. They will also be notified when any action is taken on this issue.");
		}

		if (notifycount > 1) {

			sbfile.append("Currently, there are " + notifycount + " members on issue notification list. You can add or delete members to this list before submitting the issue. They will also be notified when any action is taken on this issue.");

		}

		return sbfile.toString();

	}

	public String printPrevNotifyListWithView(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) throws Exception {

		StringBuffer sbsub = new StringBuffer();

		EtsIssActionGuiUtils actGuiUtils = new EtsIssActionGuiUtils();
		EtsIssViewGuiUtils viewGuiUtils = new EtsIssViewGuiUtils();

		EtsIssUserRolesModel usrRolesModel = etsIssObjKey.getUsrRolesModel();

		//sbsub.append(actGuiUtils.printSectnHeader((String) propMap.get("issue.act.mod.step5")));
		sbsub.append("<br />");
		sbsub.append(viewGuiUtils.printPrevNotifyListWithView(etsIssObjKey, usr1InfoModel, propMap));
		sbsub.append("<br />");
		sbsub.append(actGuiUtils.printEditIssueBtn("op_325", "Edit notification list"));
		sbsub.append(actGuiUtils.printGreyLine());
		sbsub.append("<br />");

		if (!etsIssObjKey.isProjBladeType()) {

			return sbsub.toString();

		} else {

			if (usrRolesModel.isBladeUsrInt()) {

				return sbsub.toString();

			} else {

				return "";
			}

		}

	}

	/**
			 * 
			 * 
			 * @param etsIssObjKey
			 * @param usr1InfoModel
			 * @param propMap
			 * @return
			 */

	public String printPrevNotifyListForModify(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) throws SQLException, Exception {

		StringBuffer sbsub = new StringBuffer();

		ArrayList prevNotifyList = usr1InfoModel.getPrevNotifyList();

		String propMsg = "";
		//////////////////view new/////////////

		int viewnotsize = getPrevNotifyListCountForView(prevNotifyList, etsIssObjKey.getProj().getProjectId());
		/////////////////////////////////////

		if (viewnotsize > 0) { //if there are any email addresses in prevNotifyList

			EtsIssViewGuiUtils viewGuiUtils = new EtsIssViewGuiUtils();

			sbsub.append(viewGuiUtils.printPrevNotifyListWithView(etsIssObjKey, usr1InfoModel, propMap));

		} else { //or if they contain edge_ids

			EtsProjMemberDAO projMemDao = new EtsProjMemberDAO();

			//get the display members
			ArrayList projMemList = projMemDao.getProjMemberListFromEdgeId(prevNotifyList);

			int notsize = 0;

			if (EtsIssFilterUtils.isArrayListDefnd(projMemList)) {

				notsize = projMemList.size();

			}

			sbsub.append("<br />\n");
			propMsg = getDynNotifyListMsg(notsize);
			sbsub.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n");
			sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >\n");
			sbsub.append(propMsg);
			sbsub.append("</td></tr>\n");
			sbsub.append("</table>\n");
			if (notsize > 0) {

				sbsub.append("<br />\n");
				sbsub.append(comGuiUtils.printGreyLine());

			}
			sbsub.append("<br />\n");

			sbsub.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"4\" width=\"600\">\n");
			sbsub.append("<tr>\n");

			if (notsize > 0) {

				sbsub.append("<td  colspan=\"2\" height=\"18\" ><b>Notification list<b/></td></tr>\n");

			}

			for (int i = 0; i < notsize; i++) {

				sbsub.append("<tr valign=\"top\" >\n");
				sbsub.append("<td valign=\"top\" align=\"left\"  width=\"600\">\n");
				sbsub.append(projMemList.get(i));
				sbsub.append("</td>\n");

				sbsub.append("</tr>\n");

			}

			sbsub.append("</table>\n");

		} //modiyf notifiy msg

		return sbsub.toString();

	}

	/**
	 * 
	 * @param prevNotifyList
	 * @param projectId
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */

	public int getPrevNotifyListCountForView(ArrayList prevNotifyList, String projectId) throws SQLException, Exception {

		EtsProjMemberDAO projMemDao = new EtsProjMemberDAO();

		//get the display members
		ArrayList projMemList = projMemDao.getProjMemberListFromEmailId(prevNotifyList, projectId);

		int notsize = 0;

		if (EtsIssFilterUtils.isArrayListDefnd(projMemList)) {

			notsize = projMemList.size();

		}

		return notsize;
	}

	/**
	 * 
	 */

	public int getPrevNotifyListCountForModify(ArrayList prevNotifyList) throws SQLException, Exception {

		EtsProjMemberDAO projMemDao = new EtsProjMemberDAO();

		//get the display members
		ArrayList projMemList = projMemDao.getProjMemberListFromEdgeId(prevNotifyList);

		int notsize = 0;

		if (EtsIssFilterUtils.isArrayListDefnd(projMemList)) {

			notsize = projMemList.size();

		}

		return notsize;

	}

	/**
		 * To print issue types for a given change/defect and project
		 */

	public String printIssueTypesForModify(ArrayList issueTypeList, ArrayList prevIssueTypeList) {

		StringBuffer sbview = new StringBuffer();

		sbview.append("<select id=\"lblisstype\" name=\"problem_type\" size=\"\" align=\"left\" class=\"iform\" style=\"width:200px\" width=\"200px\" >\n");
		sbview.append("<option value=\"NONE\">Select an issue type</option>");
		sbview.append(fltrGuiUtils.printSelectOptionsWithValue(issueTypeList, prevIssueTypeList, true));
		sbview.append("</select>\n");

		return sbview.toString();
	}

	/**
		 * To print dynamic problem submission
		 * 
		 * @param usr1Model
		 * @return
		 */

	public String printDynamicProblemSubmForms(EtsIssProbInfoUsr1Model usr1InfoModel) throws Exception {

		StringBuffer sbsub = new StringBuffer();

		//states
		int actionstate = usr1InfoModel.getCurrentActionState();
		int usr1nextstate = usr1InfoModel.getNextActionState();

		if (usr1nextstate > 0) {

			actionstate = usr1nextstate;
		}

		String defSubTypeAVal = usr1InfoModel.getSubTypeADefnd();
		String defSubTypeBVal = usr1InfoModel.getSubTypeBDefnd();
		String defSubTypeCVal = usr1InfoModel.getSubTypeCDefnd();
		String defSubTypeDVal = usr1InfoModel.getSubTypeDDefnd();

		String editSubTypeAVal = usr1InfoModel.getEditSubTypeADefnd();
		String editSubTypeBVal = usr1InfoModel.getEditSubTypeBDefnd();
		String editSubTypeCVal = usr1InfoModel.getEditSubTypeCDefnd();
		String editSubTypeDVal = usr1InfoModel.getEditSubTypeDDefnd();

		//			/curr Ref vals
		String subTypeARefName = usr1InfoModel.getSubTypeARefName();
		String subTypeBRefName = usr1InfoModel.getSubTypeBRefName();
		String subTypeCRefName = usr1InfoModel.getSubTypeCRefName();
		String subTypeDRefName = usr1InfoModel.getSubTypeDRefName();

		///curr Disp vals
		String subTypeADispName = usr1InfoModel.getSubTypeADispName();
		String subTypeBDispName = usr1InfoModel.getSubTypeBDispName();
		String subTypeCDispName = usr1InfoModel.getSubTypeCDispName();
		String subTypeDDispName = usr1InfoModel.getSubTypeDDispName();

		//curr lists
		ArrayList dispSubTypeAValList = usr1InfoModel.getSubTypeAList();
		ArrayList dispSubTypeBValList = usr1InfoModel.getSubTypeBList();
		ArrayList dispSubTypeCValList = usr1InfoModel.getSubTypeCList();
		ArrayList dispSubTypeDValList = usr1InfoModel.getSubTypeDList();

		//prev lists
		ArrayList prevSubTypeAValList = usr1InfoModel.getPrevSubTypeAList();
		ArrayList prevSubTypeBValList = usr1InfoModel.getPrevSubTypeBList();
		ArrayList prevSubTypeCValList = usr1InfoModel.getPrevSubTypeCList();
		ArrayList prevSubTypeDValList = usr1InfoModel.getPrevSubTypeDList();

		//			/sizes of arraylists//
		int subadispsize = 0;
		int subbdispsize = 0;
		int subcdispsize = 0;
		int subddispsize = 0;

		//
		int nextstate = 0;

		if (EtsIssFilterUtils.isArrayListDefnd(dispSubTypeAValList)) {

			subadispsize = dispSubTypeAValList.size();
		}

		if (EtsIssFilterUtils.isArrayListDefnd(dispSubTypeBValList)) {

			subbdispsize = dispSubTypeBValList.size();
		}

		if (EtsIssFilterUtils.isArrayListDefnd(dispSubTypeCValList)) {

			subcdispsize = dispSubTypeCValList.size();
		}

		if (EtsIssFilterUtils.isArrayListDefnd(dispSubTypeDValList)) {

			subddispsize = dispSubTypeDValList.size();
		}

		sbsub.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n");
		sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

		Global.println("action state in print Dynamic sub forms==" + actionstate);

		switch (actionstate) {

			case MODIFYADDISSUETYPE :

			case MODIFYEDITSUBTYPEA :

				if (subadispsize > 0) {

					if (!dispSubTypeAValList.contains(ETSDEFAULTCQ)) {

						sbsub.append(printDynSubmFrmListBox(subTypeADispName, subTypeARefName, dispSubTypeAValList, prevSubTypeAValList));
						nextstate = MODIFYGOSUBTYPEA;

					} else {

						sbsub.append(printStaticProblemSubmForms(usr1InfoModel));
						sbsub.append(printHidDefualtCq(subTypeARefName, ETSDEFAULTCQ));
						nextstate = MODIFYCONTIDENTF;

					}

				} else {

					nextstate = MODIFYCONTIDENTF;

				}

				sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

				break;

			case MODIFYGOSUBTYPEA :

			case MODIFYEDITSUBTYPEB :

				if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeAValList) && !prevSubTypeAValList.contains(ETSDEFAULTCQ)) {

					sbsub.append(printDynSubmFrmListSelecText(subTypeADispName, "op_3151", prevSubTypeAValList));
					sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

				}

				if (subbdispsize > 0) {

					if (!dispSubTypeBValList.contains(ETSDEFAULTCQ)) {

						sbsub.append(printDynSubmFrmListBox(subTypeBDispName, subTypeBRefName, dispSubTypeBValList, prevSubTypeBValList));
						nextstate = MODIFYGOSUBTYPEB;

					} else {

						sbsub.append(printStaticProblemSubmForms(usr1InfoModel));
						sbsub.append(printHidDefualtCq(subTypeBRefName, ETSDEFAULTCQ));
						nextstate = MODIFYCONTIDENTF;

					}

				} else {

					nextstate = MODIFYCONTIDENTF;

				}

				sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

				break;

			case MODIFYGOSUBTYPEB :

			case MODIFYEDITSUBTYPEC :

				if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeAValList) && !prevSubTypeAValList.contains(ETSDEFAULTCQ)) {

					sbsub.append(printDynSubmFrmListSelecText(subTypeADispName, "op_3151", prevSubTypeAValList));
					sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

				}

				if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeBValList) && !prevSubTypeBValList.contains(ETSDEFAULTCQ)) {

					sbsub.append(printDynSubmFrmListSelecText(subTypeBDispName, "op_3152", prevSubTypeBValList));
					sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

				}

				if (subcdispsize > 0) {

					if (!dispSubTypeCValList.contains(ETSDEFAULTCQ)) {

						sbsub.append(printDynSubmFrmListBox(subTypeCDispName, subTypeCRefName, dispSubTypeCValList, prevSubTypeCValList));
						nextstate = MODIFYGOSUBTYPEC;

					} else {

						sbsub.append(printStaticProblemSubmForms(usr1InfoModel));
						sbsub.append(printHidDefualtCq(subTypeCRefName, ETSDEFAULTCQ));
						nextstate = MODIFYCONTIDENTF;

					}

				} else {

					nextstate = MODIFYCONTIDENTF;

				}
				sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

				break;

			case MODIFYGOSUBTYPEC :

			case MODIFYEDITSUBTYPED :

				if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeAValList) && !prevSubTypeAValList.contains(ETSDEFAULTCQ)) {

					sbsub.append(printDynSubmFrmListSelecText(subTypeADispName, "op_3151", prevSubTypeAValList));
					sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");
				}

				if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeBValList) && !prevSubTypeBValList.contains(ETSDEFAULTCQ)) {

					sbsub.append(printDynSubmFrmListSelecText(subTypeBDispName, "op_3152", prevSubTypeBValList));
					sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

				}

				if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeCValList) && !prevSubTypeCValList.contains(ETSDEFAULTCQ)) {

					sbsub.append(printDynSubmFrmListSelecText(subTypeCDispName, "op_3153", prevSubTypeCValList));
					sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

				}

				if (subddispsize > 0) {

					if (!dispSubTypeDValList.contains(ETSDEFAULTCQ)) {

						sbsub.append(printDynSubmFrmListBox(subTypeDDispName, subTypeDRefName, dispSubTypeDValList, prevSubTypeDValList));
						nextstate = MODIFYCONTIDENTF;

					} else {

						sbsub.append(printStaticProblemSubmForms(usr1InfoModel));
						sbsub.append(printHidDefualtCq(subTypeDRefName, ETSDEFAULTCQ));
						nextstate = MODIFYCONTIDENTF;

					}

				} else {

					//sbsub.append(printStaticProblemSubmForms(usr1InfoModel));
					nextstate = MODIFYCONTIDENTF;

				}
				sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

				break;

			case MODIFYEDITISSUEIDENTF :

				if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeAValList) && !prevSubTypeAValList.contains(ETSDEFAULTCQ)) {

					sbsub.append(printDynSubmFrmListSelecText(subTypeADispName, "op_3151", prevSubTypeAValList));
					sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");
				}

				if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeBValList) && !prevSubTypeBValList.contains(ETSDEFAULTCQ)) {

					sbsub.append(printDynSubmFrmListSelecText(subTypeBDispName, "op_3152", prevSubTypeBValList));
					sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");
				}

				if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeCValList) && !prevSubTypeCValList.contains(ETSDEFAULTCQ)) {

					sbsub.append(printDynSubmFrmListSelecText(subTypeCDispName, "op_3153", prevSubTypeCValList));
					sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");
				}

				if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeDValList) && !prevSubTypeDValList.contains(ETSDEFAULTCQ)) {

					sbsub.append(printDynSubmFrmListBox(subTypeDDispName, subTypeDRefName, dispSubTypeDValList, prevSubTypeDValList));
					sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

				}

				boolean showstaticforms = false;

				if (EtsIssFilterUtils.isArrayListDefnd(usr1InfoModel.getPrevFieldC1List())) {

					showstaticforms = true;
				}

				if (EtsIssFilterUtils.isArrayListDefnd(usr1InfoModel.getPrevFieldC2List())) {

					showstaticforms = true;
				}

				if (EtsIssFilterUtils.isArrayListDefnd(usr1InfoModel.getPrevFieldC3List())) {

					showstaticforms = true;
				}

				if (EtsIssFilterUtils.isArrayListDefnd(usr1InfoModel.getPrevFieldC4List())) {

					showstaticforms = true;
				}

				if (EtsIssFilterUtils.isArrayListDefnd(usr1InfoModel.getPrevFieldC5List())) {

					showstaticforms = true;
				}

				if (EtsIssFilterUtils.isArrayListDefnd(usr1InfoModel.getPrevFieldC6List())) {

					showstaticforms = true;
				}

				if (EtsIssFilterUtils.isArrayListDefnd(usr1InfoModel.getPrevFieldC7List())) {

					showstaticforms = true;
				}

				if (AmtCommonUtils.isResourceDefined(usr1InfoModel.getTestCase())) {

					showstaticforms = true;
				}

				if (showstaticforms) {
					sbsub.append(printStaticProblemSubmForms(usr1InfoModel));
					sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");
				}

				nextstate = MODIFYCONTIDENTF;

				break;

		} //end of switch

		sbsub.append("</table>\n");

		sbsub.append(comGuiUtils.printGreyLine());

		sbsub.append("<br />");

		Global.println("Next state in printDynamic sub forms===" + nextstate);

		sbsub.append(printContCancel(nextstate));

		return sbsub.toString();

	}

	/**
			 * to print the common select for subm forms
			 * 
			 */
	public String printDynSubmFrmListBox(String listBoxDispName, String listBoxRefName, ArrayList dropValList, ArrayList prevDropValList) {

		StringBuffer sbsub = new StringBuffer();

		sbsub.append("<tr valign=\"top\" >");
		sbsub.append("<td  valign=\"top\" align=\"left\" width=\"25%\"><span class=\"ast\"><b>*</b></span><label for=\"lb" + listBoxRefName + "\"><b>" + listBoxDispName + "</b>:</label></td>");
		sbsub.append("<td  valign=\"top\" align=\"left\" width=\"75%\">");
		sbsub.append("<select id=\"lb" + listBoxRefName + "\" name=\"" + listBoxRefName + "\" size=\"\" align=\"left\" class=\"iform\" style=\"width:150px\" width=\"150px\" >");
		sbsub.append("<option value=\"NONE\">Select " + listBoxDispName + " </option>\n");
		sbsub.append(fltrGuiUtils.printSelectOptions(dropValList, prevDropValList));
		sbsub.append("</select>");
		sbsub.append("<input type=\"hidden\" name=\"def" + listBoxRefName + "\" value=\"Y\" />");
		sbsub.append("<input type=\"hidden\" name=\"hiddisp" + listBoxRefName + "\"  value=\"" + listBoxDispName + "\" />");
		sbsub.append("</td>");
		sbsub.append("</tr>");

		return sbsub.toString();

	}

	/**
			 * to print the common select for subm forms
			 * 
			 */
	String printDynSubmFrmListSelecText(String listBoxDispName, String listBoxRefName, ArrayList prevSubTypeValList) throws Exception {

		StringBuffer sbsub = new StringBuffer();
		String dispValue = "";

		if (EtsIssFilterUtils.isArrayListDefnd(prevSubTypeValList)) {

			dispValue = (String) prevSubTypeValList.get(0);

		}

		sbsub.append("<tr >");
		sbsub.append("<td  valign=\"top\" align=\"left\" width=\"25%\"><b>" + listBoxDispName + "</b>:</td>");

		//
		sbsub.append("<td  valign=\"top\" align=\"left\" width=\"75%\">");

		////////////////////////////
		//inner table 1//
		sbsub.append("<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"323\">");
		sbsub.append("<tr>");
		sbsub.append("<td  valign=\"top\" align=\"left\" width=\"150\" >" + dispValue + " ");
		sbsub.append("</td>");
		sbsub.append("<td  valign=\"top\" align=\"left\" width=\"5\">&nbsp;</td>");

		sbsub.append("<td  valign=\"top\" align=\"left\" width=\"168\">");

		//inner table 2//
		sbsub.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"75%\">");
		sbsub.append("<tr>");
		sbsub.append("<td  align=\"left\" >");
		sbsub.append("<input type=\"image\"  name =\"" + listBoxRefName + "\" src=\"" + Defines.BUTTON_ROOT  + "arrow_lt.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"Edit " + listBoxDispName + "\" />");
		sbsub.append("</td>");
		sbsub.append("<td   align=\"left\" >");
		sbsub.append("<b>Edit " + listBoxDispName + "</b> ");
		sbsub.append("</td>");
		sbsub.append("</tr>");
		sbsub.append("</table>");
		///inner 2 tab end//
		sbsub.append("</td>");
		sbsub.append("</tr>");
		sbsub.append("</table>");
		///inner 1 tab end//

		//
		sbsub.append("<input type=\"hidden\" name=\"edit" + listBoxRefName + "\"  value=\"" + dispValue + "\" />");
		sbsub.append("</td>");
		sbsub.append("</tr>");

		///////////////////////////////

		return sbsub.toString();

	}

	/**
			 * to print the common select for subm forms for static lists
			 * 
			 */
	public String printStaticSubmFrmListBox(String listBoxDispName, String listBoxRefName, ArrayList dropValList, ArrayList prevDropValList) {

		StringBuffer sbsub = new StringBuffer();
		EtsIssFilterGuiUtils guiUtil = new EtsIssFilterGuiUtils();

		sbsub.append("<tr valign=\"top\" >");
		sbsub.append("<td  valign=\"top\" align=\"left\" width=\"25%\"><span class=\"ast\"><b>*</b></span><label for=\"lb" + listBoxRefName + "\"><b>" + listBoxDispName + "</b>:</label></td>");
		sbsub.append("<td  valign=\"top\" align=\"left\" width=\"75%\">");
		sbsub.append("<select id=\"lb" + listBoxRefName + "\" name=\"" + listBoxRefName + "\" size=\"\" align=\"left\" class=\"iform\" style=\"width:150px\" width=\"150px\" >");
		sbsub.append("<option value=\"NONE\">Select " + listBoxDispName + " </option>\n");
		sbsub.append(guiUtil.printSelectOptions(dropValList, prevDropValList));
		sbsub.append("</select>");
		sbsub.append("<input type=\"hidden\" name=\"def" + listBoxRefName + "\" value=\"Y\" />");
		sbsub.append("</td>");
		sbsub.append("</tr>");

		return sbsub.toString();

	}

	/***
			 * 
			 * to print Issue description
			 * 
			 */

	public String printTestCase(String prevTestCaseVal) {

		StringBuffer sbsub = new StringBuffer();

		String test_case = "";

		sbsub.append("<tr valign=\"top\" >");
		sbsub.append("<td  valign=\"top\" align=\"left\" width=\"25%\">");
		sbsub.append("<span class=\"ast\"><b>*</b></span><label for=\"pdesc\"><b>Test case</b>:</label>");
		sbsub.append("</td>");
		sbsub.append("<td  valign=\"top\" align=\"left\" width=\"75%\">");

		sbsub.append(comGuiUtils.printStdTextArea("lbltcase", "testcase", prevTestCaseVal));
		sbsub.append("<input type=\"hidden\" name=\"deftestcase\" value=\"Y\" />");
		sbsub.append("</td>");
		sbsub.append("</tr>");

		sbsub.append("<tr>");
		sbsub.append("<td  valign=\"top\" align=\"left\" width=\"25%\">&nbsp;</td>");
		sbsub.append("<td  valign=\"top\" align=\"left\" width=\"75%\"><span");
		sbsub.append("class=\"small\">[ maximum 1000 characters ]</span></td>");
		sbsub.append("</tr>");

		sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

		return sbsub.toString();
	}

	/**
		 * To print continue and cancel buttons
		 * @param contnName
		 * @param cancName
		 * @return
		 */

	public String printContCancel(int state) {

		StringBuffer sbview = new StringBuffer();

		String contnName = "";
		String cancName = "";

		Global.println("state in print cont button with int param in Modify==" + state);

		switch (state) {

			case MODIFYISSUEIDENTFDEFAULT :
			case MODIFYVALIDERRADDISSTYPE :

				contnName = "op_328";
				//cancName = "op_3128";
				cancName = "op_unifcancel";

				break;

			case MODIFYGOSUBTYPEA :
			case MODIFYEDITSUBTYPEA :

				contnName = "op_3141";
				//cancName = "op_3121";
				cancName = "op_unifcancel";
				break;

			case MODIFYGOSUBTYPEB :
			case MODIFYEDITSUBTYPEB :

				contnName = "op_3142";
				//cancName = "op_3121";
				cancName = "op_unifcancel";
				break;

			case MODIFYGOSUBTYPEC :
			case MODIFYEDITSUBTYPEC :

				contnName = "op_3143";
				//cancName = "op_3121";
				cancName = "op_unifcancel";
				break;

			case MODIFYCONTIDENTF :
			case MODIFYVALIDERRCONTIDENTF :

				contnName = "op_316";
				//cancName = "op_3122";
				cancName = "op_unifcancel";

				break;

		}

		sbview.append("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n");
		sbview.append("<tr>\n");
		sbview.append("<td width=\"130\" align=\"left\">\n");
		sbview.append(comGuiUtils.printContinue(contnName));
		sbview.append("</td>\n");
		sbview.append("<td align=\"left\">\n");
		sbview.append(comGuiUtils.printCancel(cancName));
		sbview.append("</td>\n");
		sbview.append("</tr>\n");
		sbview.append("</table>\n");

		return sbview.toString();
	}

	/***
			 * this method will contain the html tags to print issue submission forms
			 * 
			 */

	String printStaticProblemSubmForms(EtsIssProbInfoUsr1Model usr1InfoModel) {

		StringBuffer sbsub = new StringBuffer();

		///curr Ref vals
		String fieldC1RefName = usr1InfoModel.getFieldC1RefName();
		String fieldC2RefName = usr1InfoModel.getFieldC2RefName();
		String fieldC3RefName = usr1InfoModel.getFieldC3RefName();
		String fieldC4RefName = usr1InfoModel.getFieldC4RefName();
		String fieldC5RefName = usr1InfoModel.getFieldC5RefName();
		String fieldC6RefName = usr1InfoModel.getFieldC6RefName();
		String fieldC7RefName = usr1InfoModel.getFieldC7RefName();

		///curr Disp vals
		String fieldC1DispName = usr1InfoModel.getFieldC1DispName();
		String fieldC2DispName = usr1InfoModel.getFieldC2DispName();
		String fieldC3DispName = usr1InfoModel.getFieldC3DispName();
		String fieldC4DispName = usr1InfoModel.getFieldC4DispName();
		String fieldC5DispName = usr1InfoModel.getFieldC5DispName();
		String fieldC6DispName = usr1InfoModel.getFieldC6DispName();
		String fieldC7DispName = usr1InfoModel.getFieldC7DispName();

		//curr lists
		ArrayList fieldC1ValList = usr1InfoModel.getFieldC1List();
		ArrayList fieldC2ValList = usr1InfoModel.getFieldC2List();
		ArrayList fieldC3ValList = usr1InfoModel.getFieldC3List();
		ArrayList fieldC4ValList = usr1InfoModel.getFieldC4List();
		ArrayList fieldC5ValList = usr1InfoModel.getFieldC5List();
		ArrayList fieldC6ValList = usr1InfoModel.getFieldC6List();
		ArrayList fieldC7ValList = usr1InfoModel.getFieldC7List();

		//prev lists
		ArrayList prevFieldC1ValList = usr1InfoModel.getPrevFieldC1List();
		ArrayList prevFieldC2ValList = usr1InfoModel.getPrevFieldC2List();
		ArrayList prevFieldC3ValList = usr1InfoModel.getPrevFieldC3List();
		ArrayList prevFieldC4ValList = usr1InfoModel.getPrevFieldC4List();
		ArrayList prevFieldC5ValList = usr1InfoModel.getPrevFieldC5List();
		ArrayList prevFieldC6ValList = usr1InfoModel.getPrevFieldC6List();
		ArrayList prevFieldC7ValList = usr1InfoModel.getPrevFieldC7List();

		//test case value
		String testCaseVal = usr1InfoModel.getTestCase();

		sbsub.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">");

		sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

		if (EtsIssFilterUtils.isArrayListDefnd(fieldC1ValList)) {

			sbsub.append(printStaticSubmFrmListBox(fieldC1DispName, fieldC1RefName, fieldC1ValList, prevFieldC1ValList));

			sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

		}

		if (EtsIssFilterUtils.isArrayListDefnd(fieldC2ValList)) {

			sbsub.append(printStaticSubmFrmListBox(fieldC2DispName, fieldC2RefName, fieldC2ValList, prevFieldC2ValList));

			sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

		}

		if (EtsIssFilterUtils.isArrayListDefnd(fieldC3ValList)) {

			sbsub.append(printStaticSubmFrmListBox(fieldC3DispName, fieldC3RefName, fieldC3ValList, prevFieldC3ValList));

			sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

		}

		if (EtsIssFilterUtils.isArrayListDefnd(fieldC4ValList)) {

			sbsub.append(printStaticSubmFrmListBox(fieldC4DispName, fieldC4RefName, fieldC4ValList, prevFieldC4ValList));

			sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

		}

		if (EtsIssFilterUtils.isArrayListDefnd(fieldC5ValList)) {

			sbsub.append(printStaticSubmFrmListBox(fieldC5DispName, fieldC5RefName, fieldC5ValList, prevFieldC5ValList));

			sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

		}

		if (EtsIssFilterUtils.isArrayListDefnd(fieldC6ValList)) {

			sbsub.append(printStaticSubmFrmListBox(fieldC6DispName, fieldC6RefName, fieldC6ValList, prevFieldC6ValList));

			sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

		}

		if (EtsIssFilterUtils.isArrayListDefnd(fieldC7ValList)) {

			sbsub.append(printStaticSubmFrmListBox(fieldC7DispName, fieldC7RefName, fieldC7ValList, prevFieldC7ValList));

			sbsub.append("<tr><td  colspan=\"2\" height=\"18\" >&nbsp;</td></tr>\n");

		}

		//print test case//

		sbsub.append(printTestCase(testCaseVal));

			

		sbsub.append("</table>");

		return sbsub.toString();
	}

	/**
			 * to print the hidden default cq param
			 * 
			 */
	String printHidDefualtCq(String listBoxRefName, String hidDefCqVal) {

		StringBuffer sbsub = new StringBuffer();

		sbsub.append("<input type=\"hidden\" name=\"DFCQ" + listBoxRefName + "\" value=\"" + hidDefCqVal + "\" />");

		return sbsub.toString();

	}
	/**
	 * 
	 * @param subName
	 * @param etsIssObjKey
	 * @param edgeProblemId
	 * @return
	 */

	public String printSubmitRetToView(String subName, EtsIssObjectKey etsIssObjKey, String edgeProblemId) {

		return comGuiUtils.printSubmitRetToView(subName, etsIssObjKey, edgeProblemId);
	}

	/**
				 * 
				 * 
				 * @param etsIssObjKey
				 * @param usr1InfoModel
				 * @return
				 */

	public String printChkIssTypeIbmOnlyForView(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1InfoModel, HashMap propMap) throws Exception {

		StringBuffer sbsub = new StringBuffer();
		String issueAccess = AmtCommonUtils.getTrimStr(usr1InfoModel.getIssueAccess());

		if (etsIssObjKey.getEs().gDECAFTYPE.equals("I")) { //only for internals

			Global.println("issueAccess in print check Iss Type Ibm Only in View==" + issueAccess);

			//	print section header
			sbsub.append(comGuiUtils.printSectnHeader((String) propMap.get("issue.act.mod.step4")));

			sbsub.append("<br />");

			sbsub.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"4\" width=\"600\">\n");

			sbsub.append("<tr valign=\"top\" >");

			if (issueAccess.equals("IBM")) {

				sbsub.append("<td valign=\"top\" width=\"20%\" nowrap=\"nowrap\"><b>Security classification</b>:</td><td valign=\"top\" align=\"left\" width=\"80%\" >IBM team member only</td>");

			} else {

				sbsub.append("<td valign=\"top\" width=\"20%\" nowrap=\"nowrap\"><b>Security classification</b>:</td><td valign=\"top\" align=\"left\" width=\"80%\" >Any team member in workspace can access this issue.</td>");

			}
			sbsub.append("</tr>");
			//sbsub.append("<tr><td  colspan=\"2\" align=\"left\"> <img src=\"//www.ibm.com/i/c.gif\" border=\"0\" height=\"4\" width=\"21\" alt=\"\" /></td></tr>\n");
			sbsub.append("</table>");

			sbsub.append("<br />");

			sbsub.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"4\" width=\"600\">\n");

			sbsub.append("<tr valign=\"top\" >");
			sbsub.append((String) propMap.get("issue.act.mod.msg9"));

			sbsub.append("</tr>");

			sbsub.append("</table>");

			sbsub.append("<br />");

		}

		return sbsub.toString();

	}

	public String printBladeCommntsForModify(EtsIssObjectKey etsIssObjKey, HashMap propMap) {

		StringBuffer sb = new StringBuffer();

		EtsIssUserRolesModel usrRolesModel = etsIssObjKey.getUsrRolesModel();

		sb.append("<table summary=\"welcome\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\">\n");
		sb.append("<tr><td >&nbsp;</td></tr>\n");

		sb.append("<tr>\n");
		sb.append("<td  height=\"18\" width=\"600\">\n");

		//blade logic starts

		if (!etsIssObjKey.isProjBladeType()) {

			sb.append((String) propMap.get("issue.act.mod.msg8"));

		} else {

			if (usrRolesModel.isBladeUsrInt()) {

				sb.append((String) propMap.get("issue.act.mod.msg8"));

			} else {

				sb.append((String) propMap.get("issue.act.mod.msg8"));
				sb.append((String) propMap.get("issue.blade.comm.msg"));

			}
		}

		//	blade logic end 

		sb.append("</td>\n");
		sb.append("</tr>\n");
		sb.append("</table>\n");

		return sb.toString();

	}

	/**
			 * This method will print submitter details
			 * 
			 */

	public String printSubmitterDetailsForModify(EtsIssObjectKey etsIssObjKey, EtsIssProbInfoUsr1Model usr1Model) {

		StringBuffer sb = new StringBuffer();

		//		get roles model
		EtsIssUserRolesModel usrRolesModel = etsIssObjKey.getUsrRolesModel();

		String curIssueCustName = AmtCommonUtils.getTrimStr(usr1Model.getCustName());
		String curIssueCustEmail = AmtCommonUtils.getTrimStr(usr1Model.getCustEmail());
		String curIssueCustPhone = AmtCommonUtils.getTrimStr(usr1Model.getCustPhone());
		String curIssueCustCompany = AmtCommonUtils.getTrimStr(usr1Model.getCustCompany());
		String curIssueSubmitterEdgeId = AmtCommonUtils.getTrimStr(usr1Model.getProbCreator());

		if (!etsIssObjKey.isProjBladeType()) {

			sb.append("<table summary=\"submitter profile\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n");
			sb.append("<tr><td colspan=\"4\">&nbsp;</td></tr>\n");
			sb.append("<tr >\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"25%\"><b>Submitter</b>:</td>\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"35%\">" + curIssueCustName + "</td>\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"15%\"><b>Company</b>:</td>\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"25%\">" + curIssueCustCompany + "</td>\n");
			sb.append("</tr>\n");
			sb.append("<tr><td colspan=\"4\">&nbsp;</td></tr>\n");
			sb.append("<tr >\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"25%\"><b>E-mail</b>:</td>\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"35%\">" + curIssueCustEmail + "</td>\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"15%\"><b>Phone</b>:</td>\n");
			sb.append("<td valign=\"top\" align=\"left\" width=\"25%\">" + curIssueCustPhone + "</td>\n");
			sb.append("</tr>\n");
			sb.append("</table>\n");

		} else {

			if (usrRolesModel.isBladeUsrInt()) {

				sb.append("<table summary=\"submitter profile\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n");
				sb.append("<tr><td colspan=\"4\">&nbsp;</td></tr>\n");
				sb.append("<tr >\n");
				sb.append("<td valign=\"top\" align=\"left\" width=\"25%\"><b>Submitter</b>:</td>\n");
				sb.append("<td valign=\"top\" align=\"left\" width=\"35%\">" + curIssueCustName + "</td>\n");
				sb.append("<td valign=\"top\" align=\"left\" width=\"15%\"><b>Company</b>:</td>\n");
				sb.append("<td valign=\"top\" align=\"left\" width=\"25%\">" + curIssueCustCompany + "</td>\n");
				sb.append("</tr>\n");
				sb.append("<tr><td colspan=\"4\">&nbsp;</td></tr>\n");
				sb.append("<tr >\n");
				sb.append("<td valign=\"top\" align=\"left\" width=\"25%\"><b>E-mail</b>:</td>\n");
				sb.append("<td valign=\"top\" align=\"left\" width=\"35%\">" + curIssueCustEmail + "</td>\n");
				sb.append("<td valign=\"top\" align=\"left\" width=\"15%\"><b>Phone</b>:</td>\n");
				sb.append("<td valign=\"top\" align=\"left\" width=\"25%\">" + curIssueCustPhone + "</td>\n");
				sb.append("</tr>\n");
				sb.append("</table>\n");

			} else {

				if (etsIssObjKey.getEs().gUSERN.equals(curIssueSubmitterEdgeId)) {

					sb.append("<table summary=\"submitter profile\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">\n");
					sb.append("<tr><td colspan=\"4\">&nbsp;</td></tr>\n");
					sb.append("<tr >\n");
					sb.append("<td valign=\"top\" align=\"left\" width=\"25%\"><b>Submitter</b>:</td>\n");
					sb.append("<td valign=\"top\" align=\"left\" width=\"35%\">" + curIssueCustName + "</td>\n");
					sb.append("<td valign=\"top\" align=\"left\" width=\"15%\"><b>Company</b>:</td>\n");
					sb.append("<td valign=\"top\" align=\"left\" width=\"25%\">" + curIssueCustCompany + "</td>\n");
					sb.append("</tr>\n");
					sb.append("<tr><td colspan=\"4\">&nbsp;</td></tr>\n");
					sb.append("<tr >\n");
					sb.append("<td valign=\"top\" align=\"left\" width=\"25%\"><b>E-mail</b>:</td>\n");
					sb.append("<td valign=\"top\" align=\"left\" width=\"35%\">" + curIssueCustEmail + "</td>\n");
					sb.append("<td valign=\"top\" align=\"left\" width=\"15%\"><b>Phone</b>:</td>\n");
					sb.append("<td valign=\"top\" align=\"left\" width=\"25%\">" + curIssueCustPhone + "</td>\n");
					sb.append("</tr>\n");
					sb.append("</table>\n");

				} else {

				}

			}

		}

		//balde logic ends
		return sb.toString();
	}

} //end of class
