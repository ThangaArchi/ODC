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

/*
 * Created on Jun 18, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ets.fe.aic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.Vector;
import oem.edge.amt.EntitledStatic;
import oem.edge.amt.AmtCommonUtils;
import oem.edge.common.Global;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.ETSParams;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcTeamUtils;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.ETSProcReqAccessGoPages;
import oem.edge.ets.fe.ubp.ETSUserDetails;
import org.apache.commons.logging.Log;

/**
 * @author v2ravik
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AICProcReqAccessGoPages extends ETSProcReqAccessGoPages{

	public static final String VERSION = "1.1";
	private static Log logger = EtsLogger.getLogger(AICProcReqAccessFunctions.class);

    public AICProcReqAccessGoPages (){
		super();
		this.etsParms = new ETSParams();
		this.errMsg = "";
	   if(!Global.loaded) {
		  Global.Init();
         webRoot=Global.server;
	   }
	  Global.println("web root=="+webRoot);


    }

	public String showReq() throws Exception,SQLException{
		StringBuffer sBuff = new StringBuffer();
		String requestid = etsParms.getRequest().getParameter("requestid");
		Connection conn = etsParms.getConnection();

		sBuff.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"1\">");
		sBuff.append("<tr><td headers=\"col1\" colspan=\"2\" width=\"443\">You are listed as the IBM contact for the request below. Please complete the information needed for IBM Export Control Regulations and processing.</td></tr>");

		String requestorId = AmtCommonUtils.getValue(conn,"select user_id from ets.ets_access_req where request_id="+requestid+" with ur");
		ETSUserDetails usrDetails  = new ETSUserDetails();
		usrDetails.setWebId(requestorId.trim());
		usrDetails.extractUserDetails(conn);
		if (!this.errMsg.equals("")){
			sBuff.append("<tr><td headers=\"col2\" colspan=\"2\">&nbsp;</td></tr>");
			sBuff.append("<tr><td headers=\"col3\" colspan=\"2\"><span style=\"color:red\">"+errMsg+"</span></td></tr>");
		}
		sBuff.append("<tr><td headers=\"col4\" colspan=\"2\">&nbsp;</td></tr>");
		sBuff.append("<tr><td headers=\"col5\" class=\"tdblue\" colspan=\"2\" >"+usrDetails.getFirstName()+"&nbsp;"+usrDetails.getLastName()+"["+requestorId+"]</td></tr>");

		String submittedOn = AmtCommonUtils.getValue(conn,"select char(date(date_requested),usa) from ets.ets_access_req where request_id="+requestid+"  with ur");
		String submittedBy = AmtCommonUtils.getValue(conn,"select requested_by from ets.ets_access_req where request_id="+requestid+"  with ur");
		sBuff.append("<tr><td headers=\"col6\" width=\"40%\">"+printLabel(false,true,":","Submitted on")+"</td><td>"+printLabel(false,false,"",submittedOn)+"</td></tr>");
		if (!submittedBy.equals(requestorId.toString())){
			sBuff.append("<tr><td>"+printLabel(false,true,":","Submitted by")+"</td><td>"+printLabel(false,false,"",submittedBy)+"</td></tr>");
		}

		sBuff.append("<tr><td headers=\"col7\" colspan=\"2\">&nbsp;</td></tr>");
		sBuff.append("<tr><td headers=\"col8\" colspan=\"2\"><img src=\""+Defines.V11_IMAGE_ROOT+"rules/dotted_rule_443.gif\" border=\"0\" height=\"1\" width=\"443\" alt=\"\" /></td></tr>");
		sBuff.append("<tr><td headers=\"col9\" colspan=\"2\">"+printLabel(false,true,":","User Information")+"</td></tr>");
		sBuff.append("<tr><td headers=\"col10\" colspan=\"2\">&nbsp;</td></tr>");
		sBuff.append("<tr><td>"+printLabel(false,true,":","User name")+"</td><td>"+printLabel(false,true,"",usrDetails.getFirstName()+" "+usrDetails.getLastName())+"</td></tr>");
		sBuff.append("<tr><td>"+printLabel(false,true,":","IBM ID")+"</td><td>"+printLabel(false,false,"",usrDetails.getWebId())+"</td></tr>");
		sBuff.append("<tr><td>"+printLabel(false,true,":","E-mail")+"</td><td>"+printLabel(false,false,"",usrDetails.getEMail())+"</td></tr>");
		sBuff.append("<tr><td>"+printLabel(false,true,":","Company")+"</td><td>"+printLabel(false,false,"",usrDetails.getCompany())+"</td></tr>");
		sBuff.append("<tr><td>"+printLabel(false,true,":","Address")+"</td><td>"+printLabel(false,false,"",usrDetails.getStreetAddress())+"</td></tr>");
		String typedProjName = AmtCommonUtils.getValue(conn,"select project_name from ets.ets_access_req where request_id="+requestid+"  with ur");
		sBuff.append("<tr><td>"+printLabel(false,true,":","User typed in the <br />following project name")+"</td><td>"+printLabel(false,false,"",typedProjName)+"</td></tr>");
		String comments = AmtCommonUtils.getValue(conn,"select action from ets.ets_access_log where request_id="+requestid+"  with ur");
		sBuff.append("<tr><td>"+printLabel(false,true,":","Additional comments")+"</td><td>"+printLabel(false,false,"",comments)+"</td></tr>");

		sBuff.append("<tr><td headers=\"col11\" colspan=\"2\">&nbsp;</td></tr>");
		sBuff.append("<tr><td headers=\"col12\" colspan=\"2\"><img src=\""+Defines.V11_IMAGE_ROOT+"rules/dotted_rule_443.gif\" border=\"0\" height=\"1\" width=\"443\" alt=\"\" /></td></tr>");


		sBuff.append("<tr><td headers=\"col13\" colspan=\"2\">"+printLabel(false,true,":","IBM Export Control Regulations")+"<span class=\"small\">[</span>"+ibmReg()+"<span class=\"small\">]</span>"+"</td></tr>");

		if (usrDetails.getUserType()==usrDetails.USER_TYPE_INTERNAL){
			sBuff.append("<tr><td headers=\"col14\" colspan=\"2\">&nbsp;</td></tr>");
			sBuff.append("<tr><td headers=\"col15\" colspan=\"2\">Export regulation check is not required because this is an IBM internal user</td></tr>");
		}else {
//		  oem.edge.decaf.DecafSuperSoldTos decafCompanies = new oem.edge.decaf.DecafSuperSoldTos();
//		  Vector companies = decafCompanies.getSupSoldToList(conn, "'-'");

		Vector companies  = EntitledStatic.getValues(conn,"select distinct parent from decafobj.company_view with ur");
		
		sBuff.append("<tr>");
		
        if(StringUtil.isNullorEmpty(usrDetails.getCompany())){
        		sBuff.append("<tr><td>"+printLabel(true,true,":","<label for=\"wkspc\">TG SAP company name</label>")+"</td>");
        		sBuff.append("<td   valign=\"top\" ><select name=\"company\" id=\"company\">");
        		sBuff.append("<option value=\"\">Select company</option>");
        		for (int i = 0; i < companies.size(); i++) {
        		    String sTemp = (String) companies.elementAt(i);
        		    sBuff.append("<option value=\"" + sTemp + "\">" + sTemp + "</option>");
        		}
        		sBuff.append("</select></td>");	

        }else{

        	sBuff.append("<tr><td>"+printLabel(false,true,":","<label for=\"wkspc\">TG SAP company name</label>")+"</td>");
        	sBuff.append("<td   valign=\"top\" >" + usrDetails.getCompany() + "</td>");
        	sBuff.append("<input type=\"hidden\" name=\"company\" value=\"" + usrDetails.getCompany() + "\" />");
					   	
		}
	
        sBuff.append("</tr>");
		                
        String qry="select rtrim(country_code),rtrim(country_name) from decaf.country order by 2  with ur";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(qry);
        
        sBuff.append("<tr>");
		
		if(StringUtil.isNullorEmpty(usrDetails.getCountryCode())){
				sBuff.append("<tr><td>"+printLabel(true,true,":","<label for=\"wkspc\">Country</label>")+"</td>");
				sBuff.append("<td><select id=\"country\" name=\"country\"><option name=\"compval\" value=\"select\">Select country</option>");
				while (rs.next()) {
					String countryCode = rs.getString(1);
					sBuff.append("<option name=\"compval\" value=\"" + countryCode + "\">" + rs.getString(2) + "</option>");
				}
				 sBuff.append("</select></td>");
		}else{
										
			String cntryName = EntitledStatic.getValue(conn, "select country_name from decaf.country where country_code = '" + usrDetails.getCountryCode() + "' with ur");
			sBuff.append("<tr><td>"+printLabel(false,true,":","<label for=\"wkspc\">Country</label>")+"</td>");
			sBuff.append("<td   valign=\"top\" >" + cntryName + "</td>");
			sBuff.append("<input type=\"hidden\" name=\"country\" value=\"" + usrDetails.getCountryCode() + "\" />");
			
		}
		
		    sBuff.append("</tr>");
		}
		
		String wkSpc = etsParms.getRequest().getParameter("wkspc");
		if (wkSpc==null) {wkSpc = "";
		}

		//check with laxman
		//sBuff.append("<tr><td headers=\"col16\" colspan=\"2\">&nbsp;</td></tr>");
		sBuff.append("<tr><td headers=\"col17\" colspan=\"2\"><img src=\""+Defines.V11_IMAGE_ROOT+"rules/dotted_rule_443.gif\" border=\"0\" height=\"1\" width=\"443\" alt=\"\" /></td></tr>");
		sBuff.append("<tr><td headers=\"col18\" colspan=\"2\">"+printLabel(false,true,":","Collaboration Center access processing") + "</td></tr>");
		sBuff.append("<tr><td>"+printLabel(true,true,":","<label for=\"wkspc\">Workspace name</label>")+"</td><td><select id=\"wkspc\" name=\"wkspc\"><option name=\"opwk\" selected value=\"select\">Select workspace</option>");
		Vector projvect = null;
		// if the user has IBM_EXECUTIVE_ENTITLEMENT - list all the active projects //
		// else - list user specific projects
		int isExec = Integer.parseInt(EntitledStatic.getValue(conn, "select count(*) from amt.s_user_access_view where userid='"+etsParms.getEdgeAccessCntrl().gUSERN+"' and entitlement='"+Defines.ETS_EXECUTIVE_ENTITLEMENT+"'  with ur"));
		if (isExec > 0){
			projvect = EntitledStatic.getVQueryResult(conn, "select project_id, project_name from ets.ets_projects where project_or_proposal !='M'  and project_status not in ('A','D') order by 2  with ur",2);
			for (int i=0; i <projvect.size(); i++){
				String[] tempvect = (String[])projvect.get(i);
				 if (tempvect[0].equals(wkSpc.toString())){
					sBuff.append("<option name=\"opwk\" selected value=\""+tempvect[0]+"\">"+tempvect[1]+"</option>");
				} else {
					sBuff.append("<option name=\"opwk\" value=\""+tempvect[0]+"\">"+tempvect[1]+"</option>");
				}
			}
		} else {
			projvect  = AICWorkspaceDAO.getAicUserProjects(etsParms.getEdgeAccessCntrl().gIR_USERN,conn); // for which this user has access
			for (int i=0; i <projvect.size(); i++){
				ETSProj proj = (ETSProj)projvect.get(i);
				if (proj.getProjectId().equals(wkSpc.toString())){
					sBuff.append("<option name=\"opwk\" selected value=\""+proj.getProjectId()+"\">"+proj.getName()+"</option>");
				} else {
					sBuff.append("<option name=\"opwk\" value=\""+proj.getProjectId()+"\">"+proj.getName()+"</option>");
				}
			}
		}
		sBuff.append("</select></td></tr>");

		String acceptStr = "";
		String acceptVal = "";

		sBuff.append("<tr><td headers=\"col19\" valign=\"top\">"+printLabel(true,true,":","Action")+"</td>");
		sBuff.append("<td><table><tr><td>"+printRadioButton("approveact","accept",false)+printLabel(false,false,"","Accept")+"</td></tr>");
		if (this.errMsg.equals("<li><b>When rejecting you must type in a message to the user</b></li>")){
			sBuff.append("<tr><td>"+printRadioButton("approveact","reject",true)+printLabel(false,false,"","Reject&nbsp;(message to requestor is required)")+"</td></tr>");
			sBuff.append("<tr><td>"+printRadioButton("approveact","defer",false)+printLabel(false,false,"","Defer")+"</td></tr>");
		}else {
		sBuff.append("<tr><td>"+printRadioButton("approveact","reject",false)+printLabel(false,false,"","Reject&nbsp;(message to requestor is required)")+"</td></tr>");
		sBuff.append("<tr><td>"+printRadioButton("approveact","defer",true)+printLabel(false,false,"","Defer")+"</td></tr>");
		}
		sBuff.append("</table></td></tr>");

		sBuff.append("<tr><td headers=\"col20\" colspan=\"2\">&nbsp;</td></tr>");
		sBuff.append("<tr><td headers=\"col21\" colspan=\"2\">"+printLabel(false,true,":","Message to the User/Requestor")+"</td></tr>");
		sBuff.append("<tr><td headers=\"col22\" colspan=\"2\">"+printTextField(true,false,"comments","",53,5)+"</td></tr>");


		sBuff.append("<tr><td headers=\"col23\" colspan=\"2\">&nbsp;</td></tr>");
		sBuff.append("<tr><td><input type=\"image\" name=\"approve\" src=\""+ Defines.BUTTON_ROOT+ "continue.gif\"  border=\"0\" width=\"120\" height=\"21\" alt=\"continue\" /></td>");
		sBuff.append("<td><table><tr><td><a href=\""+Defines.SERVLET_PATH+"AICConnectServlet.wss?linkid=" + etsParms.getLinkId() + "\"><img src=\""+Defines.BUTTON_ROOT+"cancel_rd.gif\" height=\"21\" width=\"21\" border=\"0\" alt=\"Cancel\" /></td><td></a><a href=\""+Defines.SERVLET_PATH+"AICConnectServlet.wss?linkid="+ etsParms.getLinkId() + "\" class=\"fbox\">Cancel</a></td></tr></table></td></tr>");

		sBuff.append("<tr><td headers=\"col24\" colspan=\"2\">"+printLabel(true,false,"","")+"Required fields are indicated with an asterisk(&#42;).</td></tr>");
		sBuff.append("<input type=\"hidden\" name=\"requestid\" value=\""+requestid+"\" />");
		sBuff.append("<input type=\"hidden\" name=\"ibmid\" value=\""+requestorId+"\" />");
		sBuff.append("<input type=\"hidden\" name=\"option\" value=\"procreq\" />");
		sBuff.append("<input type=\"hidden\" name=\"linkid\" value=\"" + etsParms.getLinkId() + "\" />");
		sBuff.append("</table>");
		return sBuff.toString();
	}

	public String showSummary() throws Exception, SQLException{
		StringBuffer sBuff = new StringBuffer();
		String requestId = etsParms.getRequest().getParameter("requestid");
		String comments = etsParms.getRequest().getParameter("comments");
		String company = "";
		String country = "";
		String wkspc = etsParms.getRequest().getParameter("wkspc");
		Connection con = etsParms.getConnection();
		ETSProj etsProj = ETSDatabaseManager.getProjectDetails(wkspc);

		UnbrandedProperties unBrandedprop = WrkSpcTeamUtils.getBrandPropsFromProjId(wkspc);

		String requestor = etsParms.getRequest().getParameter("ibmid");
				
		sBuff.append("<script type=\"text/javascript\" language=\"javascript\">");

		sBuff.append("function Validate() {");
		sBuff.append("Message = \"\";");
		sBuff.append("Message = Message + CheckReason();");
		sBuff.append("if (Message == \"\") {");
		sBuff.append("return true;");
		sBuff.append("}");
		sBuff.append("else {");
		sBuff.append("alert(Message);");
		sBuff.append("return false;");
		sBuff.append("}");
		sBuff.append("}");
		
		sBuff.append("function trim(sString){");
		sBuff.append("while (sString.substring(0,1) == ' '){");
		sBuff.append("sString = sString.substring(1, sString.length);");
		sBuff.append("}");
			
		sBuff.append("while (sString.substring(sString.length-1, sString.length) == ' '){");
		sBuff.append("sString = sString.substring(0,sString.length-1);");
		sBuff.append("}");
		sBuff.append("return sString;");
		sBuff.append("}");

		sBuff.append("function CheckReason() {");
		sBuff.append("Message = \"\";");    
		sBuff.append("maxlength=127;"); 
		sBuff.append("id=document.getElementById(\"cmpRsn\").value;");
		sBuff.append("if((trim(id) == \"\") || (trim(id) == \" \")){");
		sBuff.append("Message = Message + \"Please enter a reason for the checked users\";");
		sBuff.append("}else if(trim(id).length > maxlength){");
		sBuff.append("Message = Message + \"Reason must be 128 characters or less\";");
		sBuff.append("}");       	                                 
		sBuff.append("return Message;");
		sBuff.append("}");
		 
		sBuff.append("</script>");
		
		sBuff.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"1\">");
		//sBuff.append("<tr><td headers=\"col25\" colspan=\"2\" width=\"443\">This confirmation page summarizes the access you are granting access to this user:</td></tr>");
		sBuff.append("<tr><td headers=\"col26\" colspan=\"2\" width=\"443\">Please review the following user details and click 'Submit' to grant access to the user.</td></tr>");

		ETSUserDetails usrDetails  = new ETSUserDetails();
		usrDetails.setWebId(requestor.trim());
		usrDetails.extractUserDetails(con);
		if (usrDetails.getUserType()==usrDetails.USER_TYPE_EXTERNAL){
			company = AmtCommonUtils.getTrimStr(etsParms.getRequest().getParameter("company"));
			country = etsParms.getRequest().getParameter("country");

		}

		else if (usrDetails.getUserType()==usrDetails.USER_TYPE_INTERNAL){

			company = usrDetails.getCompany();
			country="";
		}
		// getwkspcNam
		String wkspcname = EntitledStatic.getValue(con,"select project_name from ets.ets_projects where project_id = '"+wkspc+"'  with ur");
		// getcountry name
		String countryname = EntitledStatic.getValue(con,"select country_name from decaf.country where country_code = '"+country+"'  with ur");
		sBuff.append("<tr><td headers=\"col27\" colspan=\"2\">&nbsp;</td></tr>");
		sBuff.append("<tr><td>"+printLabel(false,true,":","User Information")+"</td><td headers=\"col28\" colspan=\"2\">"+printLabel(false,true,"","IBM Confidential")+"</td></tr>");
		sBuff.append("<tr><td headers=\"col29\" colspan=\"2\">&nbsp;</td></tr>");
		sBuff.append("<tr><td>"+printLabel(false,true,":","User name")+"</td><td>"+printLabel(false,true,"",ETSUtils.getUsersName(con,requestor))+"</td></tr>");
		sBuff.append("<tr><td>"+printLabel(false,true,":","IBM ID")+"</td><td>"+printLabel(false,false,"",usrDetails.getWebId())+"</td></tr>");
		sBuff.append("<tr><td>"+printLabel(false,true,":","E-mail")+"</td><td>"+printLabel(false,false,"",usrDetails.getEMail())+"</td></tr>");
		sBuff.append("<tr><td>"+printLabel(false,true,":","Company")+"</td><td>"+printLabel(false,false,"",usrDetails.getCompany())+"</td></tr>");
		sBuff.append("<tr><td>"+printLabel(false,true,":","Address")+"</td><td>"+printLabel(false,false,"",usrDetails.getStreetAddress())+"</td></tr>");
		String usrComments = EntitledStatic.getValue(con,"select action from ets.ets_access_log where request_id = "+requestId+"  with ur");

		sBuff.append("<tr><td>"+printLabel(false,true,":","Additional comments")+"</td><td>"+printLabel(false,false,"",usrComments)+"</td></tr>");


		sBuff.append("<tr><td headers=\"col30\" colspan=\"2\">&nbsp;</td></tr>");
		if (usrDetails.getUserType()==usrDetails.USER_TYPE_EXTERNAL){
			sBuff.append("<tr><td headers=\"col31\" colspan=\"2\"><img src=\""+Defines.V11_IMAGE_ROOT+"rules/dotted_rule_443.gif\" border=\"0\" height=\"1\" width=\"443\" alt=\"\" /></td></tr>");
			sBuff.append("<tr><td headers=\"col32\" colspan=\"2\">"+printLabel(false,true,":","IBM Export Control Regulations")+"<span class=\"small\">[</span>"+ibmReg()+"<span class=\"small\">]</span>"+"</td></tr>");
			sBuff.append("<tr><td>"+printLabel(true,true,":","TG SAP company name")+"</td><td>"+company+"</td></tr>");
			sBuff.append("<tr><td>"+printLabel(true,true,":","Country")+"</td><td>"+countryname+"</td></tr>");
		}
		sBuff.append("<tr><td headers=\"col33\" colspan=\"2\">&nbsp;</td></tr>");
		sBuff.append("<tr><td headers=\"col34\" colspan=\"2\"><img src=\""+Defines.V11_IMAGE_ROOT+"rules/dotted_rule_443.gif\" border=\"0\" height=\"1\" width=\"443\" alt=\"\" /></td></tr>");
		sBuff.append("<tr><td headers=\"col35\" colspan=\"2\">"+printLabel(false,true,":",""+unBrandedprop.getAppName()+" access processing")+"</td></tr>");
		sBuff.append("<tr><td>"+printLabel(false,true,":","Workspace name")+"</td><td>"+wkspcname+"</td></tr>");
		String wkspcCmp = AmtCommonUtils.getTrimStr(EntitledStatic.getValue(con,"select company from ets.ets_projects where project_id = '"+wkspc+"'  with ur"));
		sBuff.append("<tr><td>"+printLabel(false,true,":","Workspace company")+"</td><td>"+wkspcCmp+"</td></tr>");

		Global.println("WK SPACE COMPANY FROM GOPAGES====="+wkspcCmp);
		Global.println("ASSIGNED COMPANY FROM GOPAGES====="+company);


		if (!wkspcCmp.equals(company)){
			sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
            sBuff.append("<tr><td colspan=\"2\"><b><span style=\"color:rgb(255,0,0)\">Warning: The external user you are adding to this workspace is not profiled for this company, and requires a reason for entering and participating in this workspace.</span></b></td></tr>");
            sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
            sBuff.append("<tr><td><b><label for=\"label_reason\">Reason:</label></b></td><td><textarea id=\"cmpRsn\" name=\"cmpRsn\" rows=\"3\" cols=\"30\" style=\"border-style: solid;\"></textarea></td></tr>");
			sBuff.append("<tr><td headers=\"col38\" colspan=\"2\">&nbsp;</td></tr>");			
		}


		sBuff.append("<tr><td headers=\"col39\" valign=\"top\">"+printLabel(false,true,":","Access level")+"</td>");
		String role = etsParms.getRequest().getParameter("role");
		sBuff.append("<td>"+ETSDatabaseManager.getRoleName(con,Integer.parseInt(role))+"</td></tr>");

		sBuff.append("<tr><td headers=\"col40\" colspan=\"2\">&nbsp;</td></tr>");
		String job = etsParms.getRequest().getParameter("job");
		if (job == null) job = "";
		sBuff.append("<tr><td>"+printLabel(false,true,":","Job responsibility")+"</td><td>"+job+"</td></tr>");

		sBuff.append("<tr><td headers=\"col41\" colspan=\"2\">&nbsp;</td></tr>");
		sBuff.append("<tr><td headers=\"col42\" colspan=\"2\">"+printLabel(false,true,":","Optional Message to the User/Requestor")+"</td></tr>");
		sBuff.append("<tr><td headers=\"col43\" colspan=\"2\">"+comments+"</td></tr>");


		sBuff.append("<tr><td headers=\"col44\" colspan=\"2\">&nbsp;</td></tr>");
		sBuff.append("<tr><td><input type=\"image\" name=\"approve\" src=\""+ Defines.BUTTON_ROOT+ "submit.gif\" onclick=\"return Validate()\" onkeypress=\"return Validate()\" border=\"0\" width=\"120\" height=\"21\" alt=\"continue\" /></td>");
		sBuff.append("<td><table><tr><td><a href=\""+Defines.SERVLET_PATH+"AICProcReqAccessServlet.wss?linkid="+unBrandedprop.getLinkID()+"&option=showreq&requestid="+requestId+"&ibmid="+requestor+"\"><img src=\""+Defines.BUTTON_ROOT+"arrow_lt.gif\" height=\"21\" width=\"21\" border=\"0\" alt=\"Back\" /></td><td></a><a href=\""+Defines.SERVLET_PATH+"ETSProcReqAccessServlet.wss?linkid="+unBrandedprop.getLinkID()+"&option=showreq&requestid="+requestId+"&ibmid="+requestor+"\" class=\"fbox\">Back</a></td></tr></table></td></tr>");

//		  sBuff.append("<tr><td headers=\"col45\" colspan=\"2\">"+printLabel(true,false,"","")+"Required fields are indicated with an asterisk(&#42;).</td></tr>");
		if (usrDetails.getUserType()==usrDetails.USER_TYPE_EXTERNAL){
			sBuff.append("<input type=\"hidden\" name=\"company\" value=\""+company+"\" />");
			sBuff.append("<input type=\"hidden\" name=\"country\" value=\""+country+"\" />");
		}
		sBuff.append("<input type=\"hidden\" name=\"requestid\" value=\""+requestId+"\" />");
		sBuff.append("<input type=\"hidden\" name=\"ibmid\" value=\""+requestor+"\" />");
		sBuff.append("<input type=\"hidden\" name=\"role\" value=\""+role+"\" />");
		sBuff.append("<input type=\"hidden\" name=\"job\" value=\""+job+"\" />");
		sBuff.append("<input type=\"hidden\" name=\"wkspc\" value=\""+wkspc+"\" />");
		sBuff.append("<input type=\"hidden\" name=\"option\" value=\"procreq2\" />");
		sBuff.append("<input type=\"hidden\" name=\"linkid\" value=\""+unBrandedprop.getLinkID()+"\" />");

		sBuff.append("</table>");

		return sBuff.toString();

	}

	public String invite(Hashtable valHash) throws SQLException,Exception{
		StringBuffer sBuff = new StringBuffer();

		String projId=(String)valHash.get("projid");

		ETSProj etsProj = ETSDatabaseManager.getProjectDetails(projId);

		UnbrandedProperties unBrandedprop = WrkSpcTeamUtils.getBrandPropsFromProjId(projId);

		String invUsrType=AmtCommonUtils.getTrimStr((String)valHash.get("invUsrType"));

		sBuff.append("<form name=\"nouid\" method=\"post\" action=\"ETSProcReqAccessServlet.wss\">");
		sBuff.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"1\" width=\"443\">");
		sBuff.append("<tr><td headers=\"col46\" colspan=\"2\"><span style=\"color:#ff3333\"><b>Sorry, the information you entered is not associated with an approved IBM ID.</b></span></td></tr>");
		sBuff.append("<tr><td headers=\"col47\" colspan=\"2\">&nbsp;</td></tr>");
		sBuff.append("<tr><td headers=\"col48\" colspan=\"2\">To continue the process of adding a new member, you can send a message inviting the user to register. Simply fill in the information below.</td></tr>");
		sBuff.append("<tr><td headers=\"col49\" colspan=\"2\">&nbsp;</td></tr>");
		sBuff.append("</table>");

		sBuff.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"1\" width=\"443\">");
		sBuff.append("<tr><td headers=\"col50\" colspan=\"2\"><table><tr><td>");
		sBuff.append("<tr><td>To:</td><td>"+printTextField(false,false,"toid",""+valHash.get("toid"),20,32)+"</td></tr>");
		String comma = ((String)(valHash.get("woemailcc"))).equals("")?"":" ,";
		sBuff.append("<tr><td>cc:</td><td>"+printTextField(false,false,"ccid","",20,32)+" ,"+valHash.get("woemailcc")+"</td></tr>");
		sBuff.append("<tr><td>From:</td><td>"+valHash.get("reqemail")+"</td></tr>");
		sBuff.append("<tr><td>Subject:</td><td>Invitation to IBM "+unBrandedprop.getAppName()+"</td></tr>");
		sBuff.append("</td></tr></table></td></tr>");
		sBuff.append("<tr><td headers=\"col51\" colspan=\"2\">&nbsp;</td></tr>");
		sBuff.append("<tr><td headers=\"col52\" colspan=\"2\">Hello, you have been invited to join an "+unBrandedprop.getAppName()+" workspace.</td></tr>");
		sBuff.append("<tr><td headers=\"col53\" colspan=\"2\">&nbsp;</td></tr>");
		sBuff.append("<tr><td headers=\"col54\" colspan=\"2\">At "+unBrandedprop.getAppName()+", clients of IBM "+unBrandedprop.getBrandExpsn()+" ("+unBrandedprop.getBrandAbrvn()+") and IBM team members collaborate on proposals and project information. "+unBrandedprop.getAppName()+" provides a secure workspace and comprehensive suite of on demand tools that are available online, 24/7.</td></tr>");
	   // sBuff.append("<tr><td headers=\"col55\" colspan=\"2\">E&TS Connect provides a secure workspace and comprehensive suite of on demand tools that are available online, 24/7.</td></tr>");
		sBuff.append("<tr><td headers=\"col56\" colspan=\"2\">&nbsp;</td></tr>");
		sBuff.append("<tr><td headers=\"col57\" colspan=\"2\">Click on the link below to register and request access. Please provide the information below when prompted.</td></tr>");
		ResourceBundle resBundle = ResourceBundle.getBundle("oem.edge.common.gwa");
		String selfRegistrationURL = resBundle.getString("selfRegistrationURL");
		selfRegistrationURL = selfRegistrationURL.substring(0,selfRegistrationURL.indexOf("&okurl"));

		String regURL = selfRegistrationURL+"&okurl="+webRoot+Defines.SERVLET_PATH+"AICUserAccessServlet.wss?linkid=1k0000";
		sBuff.append("<tr><td headers=\"col58\" colspan=\"2\">"+regURL+"</td></tr>");
		sBuff.append("<tr><td>IBM ID:</td><td>"+valHash.get("ibmid")+"</td></tr>");
		sBuff.append("<tr><td>Project or proposal:</td><td>"+valHash.get("projname")+"</td></tr>");
		sBuff.append("<tr><td>E-mail address of IBM contact:</td><td>"+valHash.get("woemail")+"</td></tr>");
		sBuff.append("<tr><td headers=\"col59\" colspan=\"2\">&nbsp;</td></tr>");
		sBuff.append("<tr><td headers=\"col60\" colspan=\"2\">Additional comments for the invited user:</td></tr>");
		sBuff.append("<tr><td headers=\"col61\" colspan=\"2\">"+printTextField(true,false,"comments","",50,5)+"</td></tr>");
		sBuff.append("<tr><td headers=\"col62\" colspan=\"2\">&nbsp;</td></tr>");
		sBuff.append("<tr><td><input type=\"image\" name=\"approve\" src=\""+ Defines.BUTTON_ROOT+ "submit.gif\"  border=\"0\" width=\"120\" height=\"21\" alt=\"\" /></td>");
		sBuff.append("<td><table><tr><td><a href=\""+Defines.SERVLET_PATH+"ETSProjectsServlet.wss?option=addreqmember&proj="+valHash.get("projid")+"&tc="+valHash.get("tc")+"&cc="+valHash.get("cc")+"&linkid="+unBrandedprop.getLinkID()+"\"><img src=\""+Defines.BUTTON_ROOT+"cancel_rd.gif\" height=\"21\" width=\"21\" border=\"0\" alt=\"Cancel\" /></td><td></a><a href=\""+Defines.SERVLET_PATH+"ETSProjectsServlet.wss?option=addreqmember&proj="+valHash.get("projid")+"&tc="+valHash.get("tc")+"&cc="+valHash.get("cc")+"&linkid="+unBrandedprop.getLinkID()+"\">Cancel</a></td></tr></table></td></tr>");


		// sending this will return the user to add memeber page again
		sBuff.append("</table>");
		sBuff.append("<input type=\"hidden\" name=\"option\" value=\"nouidmail\" />");
		sBuff.append("<input type=\"hidden\" name=\"proj\" value=\""+valHash.get("projid")+"\" />");

		sBuff.append("<input type=\"hidden\" name=\"cc\" value=\""+valHash.get("cc")+"\" />");
		sBuff.append("<input type=\"hidden\" name=\"tc\" value=\""+valHash.get("tc")+"\" />");
		sBuff.append("<input type=\"hidden\" name=\"woemail\" value=\""+valHash.get("woemail")+"\" />");
		sBuff.append("<input type=\"hidden\" name=\"projname\" value=\""+valHash.get("projname")+"\" />");
		sBuff.append("<input type=\"hidden\" name=\"ibmid\" value=\""+valHash.get("ibmid")+"\" />");
		if (valHash.get("action")==null || ((String)valHash.get("action")).equals("")){
			sBuff.append("<input type=\"hidden\" name=\"action\" value=\"addreqmember\" />");
		} else {
			sBuff.append("<input type=\"hidden\" name=\"action\" value=\""+valHash.get("action")+"\" />");
		}
		sBuff.append("<input type=\"hidden\" name=\"linkid\" value=\""+unBrandedprop.getLinkID()+"\" />");
		sBuff.append("</form>");
		return sBuff.toString();
	}

	////new 541///
	public String inviteByWOWM(Hashtable valHash,String errMsg) throws SQLException,Exception{
			StringBuffer sBuff = new StringBuffer();
			String invUsrType=AmtCommonUtils.getTrimStr((String)valHash.get("invUsrType"));

			String projId=(String)valHash.get("projid");


			UnbrandedProperties unBrandedprop = WrkSpcTeamUtils.getBrandPropsFromProjId(projId);

			sBuff.append("<form name=\"nouid\" method=\"post\" action=\"ETSProcReqAccessServlet.wss\">");
			sBuff.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"1\" width=\"443\">");
			sBuff.append("<tr><td headers=\"col63\" colspan=\"2\"><span style=\"color:#ff3333\"><b>Sorry, the information you entered is not associated with an approved IBM ID.</b></span></td></tr>");
			sBuff.append("<tr><td headers=\"col64\" colspan=\"2\">&nbsp;</td></tr>");
			sBuff.append("<tr><td headers=\"col65\" colspan=\"2\">To continue the process of adding a new member, you can send a message inviting the user to register. Please assign Company, Country and privilege to the user and fill in the information below.</td></tr>");
			//sBuff.append("<tr><td headers=\"col66\" colspan=\"2\">&nbsp;</td></tr>");
			sBuff.append("</table>");

			//invite gui utils again//
			if(!invUsrType.equals("I")) {
			//InviteGuiUtils invUtils= new InviteGuiUtils();
			//sBuff.append(invUtils.printInvitePage(valHash,errMsg));
				sBuff.append("Application is not available for External users presently\n");
				return sBuff.toString();
			}

			sBuff.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"1\" width=\"443\">");
			sBuff.append("<tr><td headers=\"col67\" colspan=\"2\"><table><tr><td>");
			sBuff.append("<tr><td>To:</td><td>"+printTextField(false,false,"toid",""+valHash.get("toid"),20,32)+"</td></tr>");
			String comma = ((String)(valHash.get("woemailcc"))).equals("")?"":" ,";
			sBuff.append("<tr><td>cc:</td><td>"+printTextField(false,false,"ccid","",20,32)+" ,"+valHash.get("woemailcc")+"</td></tr>");
			sBuff.append("<tr><td>From:</td><td>"+valHash.get("reqemail")+"</td></tr>");
			sBuff.append("<tr><td>Subject:</td><td>Invitation to IBM "+unBrandedprop.getAppName()+"</td></tr>");
			sBuff.append("</td></tr></table></td></tr>");
			sBuff.append("<tr><td headers=\"col68\" colspan=\"2\">&nbsp;</td></tr>");
			sBuff.append("<tr><td headers=\"col69\" colspan=\"2\">Hello, you have been invited to join an "+unBrandedprop.getAppName()+" workspace.</td></tr>");
			sBuff.append("<tr><td headers=\"col70\" colspan=\"2\">&nbsp;</td></tr>");
			sBuff.append("<tr><td headers=\"col71\" colspan=\"2\">At "+unBrandedprop.getAppName()+", clients of IBM  "+unBrandedprop.getBrandExpsn()+" ("+unBrandedprop.getBrandAbrvn()+") and IBM team members collaborate on proposals and project information. "+unBrandedprop.getAppName()+" provides a secure workspace and comprehensive suite of on demand tools that are available online, 24/7.</td></tr>");
		   // sBuff.append("<tr><td headers=\"col72\" colspan=\"2\">E&TS Connect provides a secure workspace and comprehensive suite of on demand tools that are available online, 24/7.</td></tr>");
			sBuff.append("<tr><td headers=\"col73\" colspan=\"2\">&nbsp;</td></tr>");
			sBuff.append("<tr><td headers=\"col74\" colspan=\"2\">Click on the link below to register and request access. Please provide the information below when prompted.</td></tr>");
			ResourceBundle resBundle = ResourceBundle.getBundle("oem.edge.common.gwa");
			String selfRegistrationURL = resBundle.getString("selfRegistrationURL");
			selfRegistrationURL = selfRegistrationURL.substring(0,selfRegistrationURL.indexOf("&okurl"));

			String regURL = selfRegistrationURL+"&okurl="+webRoot+Defines.SERVLET_PATH+"ETSUserAccessServlet.wss?linkid="+unBrandedprop.getLinkID()+"";
			sBuff.append("<tr><td headers=\"col75\" colspan=\"2\">"+regURL+"</td></tr>");
			sBuff.append("<tr><td>IBM ID:</td><td>"+valHash.get("ibmid")+"</td></tr>");
			sBuff.append("<tr><td>Project or proposal:</td><td>"+valHash.get("projname")+"</td></tr>");
			sBuff.append("<tr><td>E-mail address of IBM contact:</td><td>"+valHash.get("woemail")+"</td></tr>");
			sBuff.append("<tr><td headers=\"col76\" colspan=\"2\">&nbsp;</td></tr>");
			sBuff.append("<tr><td headers=\"col77\" colspan=\"2\">Additional comments for the invited user:</td></tr>");
			sBuff.append("<tr><td headers=\"col78\" colspan=\"2\">"+printTextField(true,false,"comments","",50,5)+"</td></tr>");
			sBuff.append("<tr><td headers=\"col79\" colspan=\"2\">&nbsp;</td></tr>");
			sBuff.append("<tr><td><input type=\"image\" name=\"approve\" src=\""+ Defines.BUTTON_ROOT+ "submit.gif\"  border=\"0\" width=\"120\" height=\"21\" alt=\"\" /></td>");
			sBuff.append("<td><table><tr><td><a href=\""+Defines.SERVLET_PATH+"ETSProjectsServlet.wss?option=addreqmember&proj="+valHash.get("projid")+"&tc="+valHash.get("tc")+"&cc="+valHash.get("cc")+"&linkid="+unBrandedprop.getLinkID()+"\"><img src=\""+Defines.BUTTON_ROOT+"cancel_rd.gif\" height=\"21\" width=\"21\" border=\"0\" alt=\"Cancel\" /></td><td></a><a href=\""+Defines.SERVLET_PATH+"ETSProjectsServlet.wss?option=addreqmember&proj="+valHash.get("projid")+"&tc="+valHash.get("tc")+"&cc="+valHash.get("cc")+"&linkid="+unBrandedprop.getLinkID()+"\">Cancel</a></td></tr></table></td></tr>");


			// sending this will return the user to add memeber page again
			sBuff.append("</table>");
			sBuff.append("<input type=\"hidden\" name=\"option\" value=\"nouidmail\" />");
			sBuff.append("<input type=\"hidden\" name=\"suboption\" value=\"invbywo\" />");
			sBuff.append("<input type=\"hidden\" name=\"proj\" value=\""+valHash.get("projid")+"\" />");
			sBuff.append("<input type=\"hidden\" name=\"cc\" value=\""+valHash.get("cc")+"\" />");
			sBuff.append("<input type=\"hidden\" name=\"tc\" value=\""+valHash.get("tc")+"\" />");
			sBuff.append("<input type=\"hidden\" name=\"woemail\" value=\""+valHash.get("woemail")+"\" />");
			sBuff.append("<input type=\"hidden\" name=\"projname\" value=\""+valHash.get("projname")+"\" />");
			sBuff.append("<input type=\"hidden\" name=\"ibmid\" value=\""+valHash.get("ibmid")+"\" />");
			if (valHash.get("action")==null || ((String)valHash.get("action")).equals("")){
				sBuff.append("<input type=\"hidden\" name=\"action\" value=\"addreqmember\" />");
			} else {
				sBuff.append("<input type=\"hidden\" name=\"action\" value=\""+valHash.get("action")+"\" />");
			}
			sBuff.append("<input type=\"hidden\" name=\"linkid\" value=\""+unBrandedprop.getLinkID()+"\" />");
			sBuff.append("<input type=\"hidden\" name=\"invUsrType\" value=\""+valHash.get("invUsrType")+"\" />");
			sBuff.append("</form>");
			return sBuff.toString();
		}


	///new 541 ends///
	public String inviteEmail(Hashtable valHash) throws SQLException,Exception{
		StringBuffer sBuff = new StringBuffer();

		String projId=(String)valHash.get("projid");


		UnbrandedProperties unBrandedprop = WrkSpcTeamUtils.getBrandPropsFromProjId(projId);


		String invUsrType=AmtCommonUtils.getTrimStr((String)valHash.get("invUsrType"));

		sBuff.append("<form name=\"nouid\" method=\"post\" action=\"ETSProcReqAccessServlet.wss\">");
		sBuff.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"1\" width=\"443\">");
		sBuff.append("<tr><td headers=\"col80\" colspan=\"2\">&nbsp;</td></tr>");

		sBuff.append("<tr><td headers=\"col81\" colspan=\"2\"><table><tr><td>");
		sBuff.append("<tr><td>To:</td><td>"+printTextField(false,false,"toid",""+valHash.get("toid"),20,32)+"</td></tr>");
		String comma = ((String)(valHash.get("woemailcc"))).equals("")?"":" ,";
		sBuff.append("<tr><td>cc:</td><td>"+printTextField(false,false,"ccid","",20,32)+comma+valHash.get("woemailcc")+"</td></tr>");
		sBuff.append("<tr><td>From:</td><td>"+valHash.get("reqemail")+"</td></tr>");
		sBuff.append("<tr><td>Subject:</td><td>Invitation to IBM "+unBrandedprop.getAppName()+"</td></tr>");
		sBuff.append("</td></tr></table></td></tr>");

		sBuff.append("<tr><td headers=\"col82\" colspan=\"2\">&nbsp;</td></tr>");
		sBuff.append("<tr><td headers=\"col83\" colspan=\"2\">Hello, you have been invited to join an "+unBrandedprop.getAppName()+" workspace.</td></tr>");
		sBuff.append("<tr><td headers=\"col84\" colspan=\"2\">&nbsp;</td></tr>");
		sBuff.append("<tr><td headers=\"col85\" colspan=\"2\">At "+unBrandedprop.getAppName()+", clients of IBM "+unBrandedprop.getBrandExpsn()+" ("+unBrandedprop.getBrandAbrvn()+") and IBM team members collaborate on proposals and project information.");
		sBuff.append(" "+unBrandedprop.getAppName()+" provides a secure workspace and comprehensive suite of on demand tools that are available online, 24/7.</td></tr>");

		//start donot show registartion info for Internal users >> UD change

		if(!invUsrType.equals("I")) {

		sBuff.append("<tr><td headers=\"col86\" colspan=\"2\">&nbsp;</td></tr>");
		sBuff.append("<tr><td headers=\"col87\" colspan=\"2\">Click on the link below to register and request access. Please provide the information below when prompted.</td></tr>");
		sBuff.append("<tr><td headers=\"col88\" colspan=\"2\">&nbsp;</td></tr>");
		sBuff.append("<tr><td headers=\"col89\" colspan=\"2\">Registration link</td></tr>");
		ResourceBundle resBundle = ResourceBundle.getBundle("oem.edge.common.gwa");
		String selfRegistrationURL = resBundle.getString("selfRegistrationURL");
		selfRegistrationURL = selfRegistrationURL.substring(0,selfRegistrationURL.indexOf("&okurl"));

		String regURL = selfRegistrationURL+"&okurl="+webRoot+Defines.SERVLET_PATH+"ETSUserAccessServlet.wss?linkid="+unBrandedprop.getLinkID()+"";
		sBuff.append("<tr><td headers=\"col90\" colspan=\"2\">"+regURL+"</td></tr>");

		}

		//End donot

		sBuff.append("<tr><td headers=\"col91\" colspan=\"2\">&nbsp;</td></tr>");
		sBuff.append("<tr><td headers=\"col92\" colspan=\"2\">Registration information</td></tr>");
		sBuff.append("<tr><td>IBM ID:</td><td>"+valHash.get("ibmid")+"</td></tr>");
		sBuff.append("<tr><td>Project or proposal:</td><td>"+valHash.get("projname")+"</td></tr>");
		sBuff.append("<tr><td>E-mail address of IBM contact:</td><td>"+valHash.get("woemail")+"</td></tr>");
		sBuff.append("<tr><td headers=\"col93\" colspan=\"2\">&nbsp;</td></tr>");
		sBuff.append("<tr><td headers=\"col94\" colspan=\"2\">Additional comments for the invited user:</td></tr>");
		sBuff.append("<tr><td headers=\"col95\" colspan=\"2\">"+printTextField(true,false,"comments","",50,5)+"</td></tr>");
		sBuff.append("<tr><td headers=\"col96\" colspan=\"2\">&nbsp;</td></tr>");
		sBuff.append("<tr><td><input type=\"image\" name=\"approve\" src=\""+ Defines.BUTTON_ROOT+ "submit.gif\"  border=\"0\" width=\"120\" height=\"21\" alt=\"\" /></td>");
		sBuff.append("<td><table><tr><td><a href=\""+Defines.SERVLET_PATH+"ETSProjectsServlet.wss?option=addreqmember&proj="+valHash.get("projid")+"&tc="+valHash.get("tc")+"&cc="+valHash.get("cc")+"&linkid="+unBrandedprop.getLinkID()+"\"><img src=\""+Defines.BUTTON_ROOT+"cancel_rd.gif\" height=\"21\" width=\"21\" border=\"0\" alt=\"Cancel\" /></td><td></a><a href=\""+Defines.SERVLET_PATH+"ETSProjectsServlet.wss?option=addreqmember&proj="+valHash.get("projid")+"&tc="+valHash.get("tc")+"&cc="+valHash.get("cc")+"&linkid="+unBrandedprop.getLinkID()+"\">Cancel</a></td></tr></table></td></tr>");

		// sending this will return the user to add memeber page again
		sBuff.append("</table>");
		sBuff.append("<input type=\"hidden\" name=\"option\" value=\"nouidmail\" />");
		sBuff.append("<input type=\"hidden\" name=\"proj\" value=\""+valHash.get("projid")+"\" />");
		sBuff.append("<input type=\"hidden\" name=\"cc\" value=\""+valHash.get("cc")+"\" />");
		sBuff.append("<input type=\"hidden\" name=\"tc\" value=\""+valHash.get("tc")+"\" />");
		sBuff.append("<input type=\"hidden\" name=\"woemail\" value=\""+valHash.get("woemail")+"\" />");
		sBuff.append("<input type=\"hidden\" name=\"projname\" value=\""+valHash.get("projname")+"\" />");
		sBuff.append("<input type=\"hidden\" name=\"ibmid\" value=\""+valHash.get("ibmid")+"\" />");
		if (valHash.get("action")==null || ((String)valHash.get("action")).equals("")){
			sBuff.append("<input type=\"hidden\" name=\"action\" value=\"addreqmember\" />");
		} else {
			sBuff.append("<input type=\"hidden\" name=\"action\" value=\""+valHash.get("action")+"\" />");
		}
		sBuff.append("<input type=\"hidden\" name=\"linkid\" value=\""+unBrandedprop.getLinkID()+"\" />");
		sBuff.append("</form>");
		return sBuff.toString();
	}


	public String errPage(int errCode, Hashtable valHash, ETSUserDetails u) throws SQLException,Exception{
		String head1 = "";
		String head2 = "";
		String subj = "";

		String projId=(String)valHash.get("projid");

		UnbrandedProperties unBrandedprop = WrkSpcTeamUtils.getBrandPropsFromProjId(projId);

		switch (errCode){
			case 1:
				// address incomplete
				head1="Sorry, the user you have requested has an ID, however, their IBM profile information is incomplete.";
				head2="To continue, fill out the fields and click Send. The user will receive an e-mail with instructions on updating profile information";
				subj="IBM "+unBrandedprop.getAppName()+" access is on hold due to incomplete profile information";
			break;
			case 2:
				// user not validated
				head1="Sorry, the user you have requested has an ID but has not completed the validation process.";
				head2="To continue, fill out the fields and click Send. The user will receive an e-mail message with instructions on completing the validation process.";
				subj="IBM "+unBrandedprop.getAppName()+" access is on hold due to pending validation";
			break;
			default:
			break;
		}

		StringBuffer sBuff = new StringBuffer();
		sBuff.append("<form name=\"nouid\" method=\"post\" action=\"ETSProcReqAccessServlet.wss\">");
		sBuff.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"1\" width=\"443\">");
		sBuff.append("<tr><td headers=\"col97\" colspan=\"2\"><span style=\"color:#ff3333\"><b>"+head1+"</b></span></td></tr>");
		sBuff.append("<tr><td headers=\"col98\" colspan=\"2\">&nbsp;</td></tr>");
		sBuff.append("<tr><td headers=\"col99\" colspan=\"2\">"+head2+"</td></tr>");
		sBuff.append("<tr><td headers=\"col100\" colspan=\"2\">&nbsp;</td></tr>");

		sBuff.append("<tr><td headers=\"col101\" colspan=\"2\"><table><tr><td>");
		sBuff.append("<tr><td>To:</td><td>"+printTextField(false,false,"toid",""+valHash.get("toid"),20,32)+"</td></tr>");
		String comma = ((String)(valHash.get("woemailcc"))).equals("")?"":" ,";
		sBuff.append("<tr><td>cc:</td><td>"+printTextField(false,false,"ccid","",20,32)+comma+valHash.get("woemailcc")+"</td></tr>");
		sBuff.append("<tr><td>From:</td><td>"+valHash.get("reqemail")+"</td></tr>");
		sBuff.append("<tr><td>Subject:</td><td>"+subj+"</td></tr>");
		sBuff.append("</td></tr></table></td></tr>");

		sBuff.append("<tr><td headers=\"col102\" colspan=\"2\">&nbsp;</td></tr>");
		ResourceBundle resBundle = ResourceBundle.getBundle("oem.edge.common.gwa");
		String changeProfileURL = resBundle.getString("changeProfileURL");
		changeProfileURL = changeProfileURL.substring(0,changeProfileURL.indexOf("&okurl"));

		if (errCode==1){
			sBuff.append("<tr><td headers=\"col103\" colspan=\"2\">Hello, you have been invited to join an "+unBrandedprop.getAppName()+"  workspace.</td></tr>");
			sBuff.append("<tr><td headers=\"col104\" colspan=\"2\">&nbsp;</td></tr>");
			sBuff.append("<tr><td headers=\"col105\" colspan=\"2\">At "+unBrandedprop.getAppName()+" , clients of IBM "+unBrandedprop.getBrandExpsn()+"  ("+unBrandedprop.getBrandAbrvn()+") and IBM team members collaborate  on proposals and project information. "+unBrandedprop.getAppName()+"  provides a secure workspace and comprehensive suite of on demand tools that are available online, 24/7.</td></tr>");
			sBuff.append("<tr><td headers=\"col106\" colspan=\"2\">A member has tried to add you to a workspace, but your address in the IBM user profile is empty or is not valid. You must update your profile to continue the process.</td></tr>");
			sBuff.append("<tr><td headers=\"col107\" colspan=\"2\">&nbsp;</td></tr>");
			sBuff.append("<tr><td headers=\"col108\" colspan=\"2\">1.Click Change Profile link below and update your IBM profile.</td></tr>");
			sBuff.append("<tr><td headers=\"col109\" colspan=\"2\">"+changeProfileURL+"</td></tr>");
			sBuff.append("<tr><td headers=\"col110\" colspan=\"2\">2.Click Request Access link below. Follow the instructions on the Web page, entering the information below when prompted.</td></tr>");
			sBuff.append("<tr><td headers=\"col111\" colspan=\"2\">"+webRoot+Defines.SERVLET_PATH+"ETSUserAccessServlet.wss</td></tr>");
			sBuff.append("<tr><td headers=\"col112\" colspan=\"2\">&nbsp;</td></tr>");
			sBuff.append("<tr><td headers=\"col113\" colspan=\"2\">Workspace:          "+valHash.get("projname")+"</td></tr>");
			sBuff.append("<tr><td headers=\"col114\" colspan=\"2\">IBM contact e-mail: "+valHash.get("woemail")+"</td></tr>");
			sBuff.append("<tr><td headers=\"col115\" colspan=\"2\">&nbsp;</td></tr>");

		}
		if (errCode==2){


			sBuff.append("<tr><td headers=\"col116\" colspan=\"2\">Hello, you have been invited to join an "+unBrandedprop.getAppName()+"  workspace.</td></tr>");
			sBuff.append("<tr><td headers=\"col117\" colspan=\"2\">&nbsp;</td></tr>");
			sBuff.append("<tr><td headers=\"col118\" colspan=\"2\">At "+unBrandedprop.getAppName()+" , clients of IBM "+unBrandedprop.getBrandExpsn()+"  ("+unBrandedprop.getBrandAbrvn()+") and IBM team members collaborate  on proposals and project information. "+unBrandedprop.getAppName()+" provides a secure workspace and comprehensive suite of on demand tools that are available online, 24/7.</td></tr>");
			sBuff.append("<tr><td headers=\"col119\" colspan=\"2\">&nbsp;</td></tr>");
			sBuff.append("<tr><td headers=\"col120\" colspan=\"2\">A member has tried to add you to a workspace, but your IBM ID has not been validated. To join the workspace, please complete these steps:</td></tr>");
			sBuff.append("<tr><td headers=\"col121\" colspan=\"2\">1.Check your e-mail for the validation code that we have sent to your e-mail address.</td></tr>");
			sBuff.append("<tr><td headers=\"col122\" colspan=\"2\">2.Click Request Access link below.</td></tr>");
			sBuff.append("<tr><td headers=\"col123\" colspan=\"2\">"+webRoot+Defines.SERVLET_PATH+"ETSUserAccessServlet.wss</td></tr>");
			sBuff.append("<tr><td headers=\"col124\" colspan=\"2\">3.Log in and enter the validation code from your e-mail into the pop-up window.</td></tr>");
			sBuff.append("<tr><td headers=\"col125\" colspan=\"2\">4.Follow the instructions on the Request Access Web page, entering the information below when prompted.</td></tr>");
			sBuff.append("<tr><td headers=\"col126\" colspan=\"2\">&nbsp;</td></tr>");
			sBuff.append("<tr><td headers=\"col127\" colspan=\"2\">Workspace:"+valHash.get("projname")+"</td></tr>");
			sBuff.append("<tr><td headers=\"col128\" colspan=\"2\">&nbsp;</td></tr>");
			sBuff.append("<tr><td headers=\"col129\" colspan=\"2\">If you experience problems with any of these steps, please send a message to econnect@us.ibm.com, requesting validation of your IBM ID.</td></tr>");
		}

		sBuff.append("<tr><td headers=\"col130\" colspan=\"2\">&nbsp;</td></tr>");
		sBuff.append("<tr><td headers=\"col131\" colspan=\"2\">Additional comments for the invited user:</td></tr>");
		sBuff.append("<tr><td headers=\"col132\" colspan=\"2\">"+printTextField(true,false,"comments","",50,5)+"</td></tr>");
		sBuff.append("<tr><td headers=\"col133\" colspan=\"2\">&nbsp;</td></tr>");
		sBuff.append("<tr><td><input type=\"image\" name=\"approve\" src=\""+ Defines.BUTTON_ROOT+ "submit.gif\"  border=\"0\" width=\"120\" height=\"21\" alt=\"\" /></td>");
		sBuff.append("<td><table><tr><td><a href=\""+Defines.SERVLET_PATH+"ETSProjectsServlet.wss?option=addreqmember&proj="+valHash.get("projid")+"&tc="+valHash.get("tc")+"&cc="+valHash.get("cc")+"&linkid="+unBrandedprop.getLinkID()+"\"><img src=\""+Defines.BUTTON_ROOT+"cancel_rd.gif\" height=\"21\" width=\"21\" border=\"0\" alt=\"Cancel\" /></td><td></a><a href=\""+Defines.SERVLET_PATH+"ETSProjectsServlet.wss?option=addreqmember&proj="+valHash.get("projid")+"&tc="+valHash.get("tc")+"&cc="+valHash.get("cc")+"&linkid="+unBrandedprop.getLinkID()+"\">Cancel</a></td></tr></table></td></tr>");

		// sending this will return the user to add memeber page again
		sBuff.append("</table>");
		sBuff.append("<input type=\"hidden\" name=\"option\" value=\""+valHash.get("option")+"\" />");
		sBuff.append("<input type=\"hidden\" name=\"proj\" value=\""+valHash.get("projid")+"\" />");
		sBuff.append("<input type=\"hidden\" name=\"cc\" value=\""+valHash.get("cc")+"\" />");
		sBuff.append("<input type=\"hidden\" name=\"tc\" value=\""+valHash.get("tc")+"\" />");
		sBuff.append("<input type=\"hidden\" name=\"woemail\" value=\""+valHash.get("woemail")+"\" />");
		sBuff.append("<input type=\"hidden\" name=\"projname\" value=\""+valHash.get("projname")+"\" />");
		sBuff.append("<input type=\"hidden\" name=\"ibmid\" value=\""+valHash.get("ibmid")+"\" />");
		if (valHash.get("action")==null || ((String)valHash.get("action")).equals("")){
			sBuff.append("<input type=\"hidden\" name=\"action\" value=\"addreqmember\" />");
		} else {
			sBuff.append("<input type=\"hidden\" name=\"action\" value=\""+valHash.get("action")+"\" />");
		}
		sBuff.append("<input type=\"hidden\" name=\"linkid\" value=\""+unBrandedprop.getLinkID()+"\" />");
		sBuff.append("</form>");
		return sBuff.toString();
	}

	/*************************************************/
	// private methods
	/*************************************************/


	private String printLabel (boolean isMandatory, boolean isBold, String sperator, String labelString){
		return ("<span style=\""+(isBold?"font-weight:bold":"")+"\">"+(isMandatory?"<span style=\"color:red\">*</span>":"")+""+labelString+sperator+"</span>");
	}
	private String printTextField(boolean isTextArea, boolean isPassword, String name, String value, int length, int maxlength){
		if (isTextArea){
			return ("<label for=\"hello\"></label><textarea  id=\"hello\" name=\""+name+"\" value=\""+value+"\" cols=\""+length+"\" rows=\""+maxlength+"\"></textarea>");
		} else {
			return ("<label for=\"hello\"></label><input type=\""+(isPassword?"password":"text")+"\" id=\"hello\" name=\""+name+"\" value=\""+value+"\" length=\""+length+"\" maxlength=\""+maxlength+"\" />");
		}
	}

	private String printRadioButton(String groupName, String value, boolean checked){
		return ("<label for=\"hello\"></label><input type=\"radio\" id=\"hello\" name=\""+groupName+"\" value=\""+value+"\" "+((checked)?"checked=\"checked\"":"")+" />");
	}
	private String printCheckBox(){return "";}
	private String printHyperLink(String urlText, String urlLink){
		return printHyperLink(urlText, urlLink, false);
	}
	private String printHyperLink(String urlText, String urlLink, boolean newWindow){
			if (newWindow){
				return ("<a class=\"fbox\" target=\"_blank\" href=\""+urlLink+"\">"+urlText+"</a>");
			}else {
				return ("<a class=\"fbox\" href=\""+urlLink+"\">"+urlText+"</a>");
			}

	}

	private String printPrimaryButton() {
		return "";
	}

	private void getIRProperties(){
	}
	private String getErrMsg(){return this.errMsg;}


	public void setETSParmameters (ETSParams etsParms){
		this.etsParms = etsParms;
		super.setETSParms(etsParms);
	}
	public void setErrMsg(String errMsg){
		this.errMsg = errMsg;
		super.setErrMsg(errMsg);
	}

	private String errMsg;
	private ETSParams etsParms;
	private String selfRegistrationURL ;
	private String changeProfileURL ;
	private String changePasswordURL;
	private String forgotPasswordURL;
	private String faqWinRegistrationURL;
	private String forgotIdURL;
	private String webRoot;


}