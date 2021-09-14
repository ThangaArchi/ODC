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
 * Created on Jun 21, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package oem.edge.ets.fe;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.Vector;

import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.AmtHfConstants;
import oem.edge.amt.EntitledStatic;
import oem.edge.common.Global;
import oem.edge.ets.fe.acmgt.helpers.InviteGuiUtils;
import oem.edge.ets.fe.acmgt.helpers.WrkSpcTeamUtils;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.common.EtsLogger;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.ubp.ETSStatus;
import oem.edge.ets.fe.ubp.ETSUserAccessRequest;
import oem.edge.ets.fe.ubp.ETSUserDetails;
import oem.edge.ets.fe.wspace.ETSWorkspaceDAO;

import org.apache.commons.logging.Log;

/**
 * @author v2ravik
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 * 
 * sandra fixed job responsibility code  6/3/05
 */
public class ETSProcReqAccessGoPages {
	
	public static final String VERSION = "1.37";
	private static Log logger = EtsLogger.getLogger(ETSProcReqAccessGoPages.class);

     public ETSProcReqAccessGoPages(){
         this.etsParms = new ETSParams();
         this.errMsg = "";
		if(!Global.loaded) {
         	
		   Global.Init();
         	
		   webRoot=Global.server;
         	
		}
         
         
	   Global.println("web root=="+webRoot);
     }
    public void setETSParms (ETSParams etsParms){ this.etsParms = etsParms; }
    public void setErrMsg(String errMsg){this.errMsg = errMsg;   }

    // goPages
    //  2. Project Name and  and password
    public String showReq() throws Exception,SQLException{
        StringBuffer sBuff = new StringBuffer();
        String requestid = etsParms.getRequest().getParameter("requestid");
        Connection conn = etsParms.getConnection();
       
        sBuff.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"1\">");
        sBuff.append("<tr><td colspan=\"2\" width=\"443\">You are listed as the IBM contact for the request below. Please complete the information needed for IBM Export Control Regulations and processing.</td></tr>");

        String requestorId = AmtCommonUtils.getValue(conn,"select user_id from ets.ets_access_req where request_id="+requestid+" with ur");
        ETSUserDetails usrDetails  = new ETSUserDetails();
        usrDetails.setWebId(requestorId.trim());
        usrDetails.extractUserDetails(conn);
        if (!this.errMsg.equals("")){
            sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
            sBuff.append("<tr><td colspan=\"2\"><span style=\"color:red\">"+errMsg+"</span></td></tr>");
        }
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td class=\"tdblue\" colspan=\"2\" >"+usrDetails.getFirstName()+"&nbsp;"+usrDetails.getLastName()+"["+requestorId+"]</td></tr>");

        String submittedOn = AmtCommonUtils.getValue(conn,"select char(date(date_requested),usa) from ets.ets_access_req where request_id="+requestid+"  with ur");
        String submittedBy = AmtCommonUtils.getValue(conn,"select requested_by from ets.ets_access_req where request_id="+requestid+"  with ur");
        sBuff.append("<tr><td width=\"40%\">"+printLabel(false,true,":","Submitted on")+"</td><td>"+printLabel(false,false,"",submittedOn)+"</td></tr>");
        if (!submittedBy.equals(requestorId.toString())){
            sBuff.append("<tr><td>"+printLabel(false,true,":","Submitted by")+"</td><td>"+printLabel(false,false,"",submittedBy)+"</td></tr>");
        }

        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\"><img src=\""+Defines.V11_IMAGE_ROOT+"rules/dotted_rule_443.gif\" border=\"0\" height=\"1\" width=\"443\" alt=\"\" /></td></tr>");
        sBuff.append("<tr><td colspan=\"2\">"+printLabel(false,true,":","User Information")+"</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td>"+printLabel(false,true,":","User name")+"</td><td>"+printLabel(false,true,"",usrDetails.getFirstName()+" "+usrDetails.getLastName())+"</td></tr>");
        sBuff.append("<tr><td>"+printLabel(false,true,":","IBM ID")+"</td><td>"+printLabel(false,false,"",usrDetails.getWebId())+"</td></tr>");
        sBuff.append("<tr><td>"+printLabel(false,true,":","E-mail")+"</td><td>"+printLabel(false,false,"",usrDetails.getEMail())+"</td></tr>");
        sBuff.append("<tr><td>"+printLabel(false,true,":","Company")+"</td><td>"+printLabel(false,false,"",usrDetails.getCompany())+"</td></tr>");
        sBuff.append("<tr><td>"+printLabel(false,true,":","Address")+"</td><td>"+printLabel(false,false,"",usrDetails.getStreetAddress())+"</td></tr>");
        String typedProjName = AmtCommonUtils.getValue(conn,"select project_name from ets.ets_access_req where request_id="+requestid+"  with ur");
        sBuff.append("<tr><td>"+printLabel(false,true,":","User typed in the <br />following project name")+"</td><td>"+printLabel(false,false,"",typedProjName)+"</td></tr>");
        String comments = AmtCommonUtils.getValue(conn,"select action from ets.ets_access_log where request_id="+requestid+"  with ur");
        sBuff.append("<tr><td>"+printLabel(false,true,":","Additional comments")+"</td><td>"+printLabel(false,false,"",comments)+"</td></tr>");

        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\"><img src=\""+Defines.V11_IMAGE_ROOT+"rules/dotted_rule_443.gif\" border=\"0\" height=\"1\" width=\"443\" alt=\"\" /></td></tr>");
        
		
 	    sBuff.append("<tr><td colspan=\"2\">"+printLabel(false,true,":","IBM Export Control Regulations")+"<span class=\"small\">[</span>"+ibmReg()+"<span class=\"small\">]</span>"+"</td></tr>");

        if (usrDetails.getUserType()==usrDetails.USER_TYPE_INTERNAL){
            sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
            sBuff.append("<tr><td colspan=\"2\">Export regulation check is not required because this is an IBM internal user</td></tr>");
        }else {
//        oem.edge.decaf.DecafSuperSoldTos decafCompanies = new oem.edge.decaf.DecafSuperSoldTos();
//        Vector companies = decafCompanies.getSupSoldToList(conn, "'-'");

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
        
        String wkSpc = etsParms.request.getParameter("wkspc");
        if (wkSpc==null) {wkSpc = "";
        }
        
		//check with laxman
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\"><img src=\""+Defines.V11_IMAGE_ROOT+"rules/dotted_rule_443.gif\" border=\"0\" height=\"1\" width=\"443\" alt=\"\" /></td></tr>");
        sBuff.append("<tr><td colspan=\"2\">"+printLabel(false,true,":","E&TS Connect access processing")+"<span class=\"small\">[</span>"+accReg()+"<span class=\"small\">]</span>"+"</td></tr>");
        sBuff.append("<tr><td>"+printLabel(true,true,":","<label for=\"wkspc\">Workspace name</label>")+"</td><td><select id=\"wkspc\" name=\"wkspc\"><option name=\"opwk\" selected value=\"select\">Select workspace</option>");
        Vector projvect = null;
        // if the user has IBM_EXECUTIVE_ENTITLEMENT - list all the active projects //
        // else - list user specific projects
        int isExec = Integer.parseInt(EntitledStatic.getValue(conn, "select count(*) from amt.s_user_access_view where userid='"+etsParms.es.gUSERN+"' and entitlement='"+Defines.ETS_EXECUTIVE_ENTITLEMENT+"'  with ur"));
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
            projvect  = ETSDatabaseManager.getAllUserProjects(etsParms.getEdgeAccessCntrl().gIR_USERN,conn); // for which this user has access
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

        sBuff.append("<tr><td valign=\"top\">"+printLabel(true,true,":","Action")+"</td>");
        sBuff.append("<td><table><tr><td>"+printRadioButton("approveact","accept",false)+printLabel(false,false,"","Accept")+"</td></tr>");
        if (this.errMsg.equals("<li><b>When rejecting you must type in a message to the user</b></li>")){
            sBuff.append("<tr><td>"+printRadioButton("approveact","reject",true)+printLabel(false,false,"","Reject&nbsp;(message to requestor is required)")+"</td></tr>");
            sBuff.append("<tr><td>"+printRadioButton("approveact","defer",false)+printLabel(false,false,"","Defer")+"</td></tr>");
        }else {
        sBuff.append("<tr><td>"+printRadioButton("approveact","reject",false)+printLabel(false,false,"","Reject&nbsp;(message to requestor is required)")+"</td></tr>");
        sBuff.append("<tr><td>"+printRadioButton("approveact","defer",true)+printLabel(false,false,"","Defer")+"</td></tr>");
        }
        sBuff.append("</table></td></tr>");

        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">"+printLabel(false,true,":","Message to the User/Requestor")+"</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">"+printTextField(true,false,"comments","",53,5,"message")+"</td></tr>");


        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td><input type=\"image\" name=\"approve\" src=\""+ Defines.BUTTON_ROOT+ "continue.gif\"  border=\"0\" width=\"120\" height=\"21\" alt=\"continue\" /></td>");
        sBuff.append("<td><table><tr><td><a href=\""+Defines.SERVLET_PATH+"ETSConnectServlet.wss?linkid=251000\"><img src=\""+Defines.BUTTON_ROOT+"cancel_rd.gif\" height=\"21\" width=\"21\" border=\"0\" alt=\"Cancel\" /></td><td></a><a href=\""+Defines.SERVLET_PATH+"ETSConnectServlet.wss?linkid=251000\" class=\"fbox\">Cancel</a></td></tr></table></td></tr>");

        sBuff.append("<tr><td colspan=\"2\">"+printLabel(true,false,"","")+"Required fields are indicated with an asterisk(&#42;).</td></tr>");
        sBuff.append("<input type=\"hidden\" name=\"requestid\" value=\""+requestid+"\" />");
        sBuff.append("<input type=\"hidden\" name=\"ibmid\" value=\""+requestorId+"\" />");
        sBuff.append("<input type=\"hidden\" name=\"option\" value=\"procreq\" />");
        sBuff.append("<input type=\"hidden\" name=\"linkid\" value=\"251000\" />");
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
		ETSProj etsProj = ETSDatabaseManager.getProjectDetails(wkspc);
        
		UnbrandedProperties unBrandedprop = WrkSpcTeamUtils.getBrandPropsFromProjId(wkspc);
	
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


        String requestor = etsParms.getRequest().getParameter("ibmid");
        sBuff.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"1\">");
        //sBuff.append("<tr><td colspan=\"2\" width=\"443\">This confirmation page summarizes the access you are granting access to this user:</td></tr>");
		sBuff.append("<tr><td colspan=\"2\" width=\"443\">Please review the following user details and click 'Submit' to grant access to the user.</td></tr>");

        ETSUserDetails usrDetails  = new ETSUserDetails();
        usrDetails.setWebId(requestor.trim());
        usrDetails.extractUserDetails(etsParms.con);
        if (usrDetails.getUserType()==usrDetails.USER_TYPE_EXTERNAL){
            company = AmtCommonUtils.getTrimStr(etsParms.getRequest().getParameter("company"));
            country = etsParms.getRequest().getParameter("country");

        }
        
		else if (usrDetails.getUserType()==usrDetails.USER_TYPE_INTERNAL){
				
			company = usrDetails.getCompany();
			country="";
		}
        // getwkspcNam
        String wkspcname = EntitledStatic.getValue(etsParms.con,"select project_name from ets.ets_projects where project_id = '"+wkspc+"'  with ur");
        // getcountry name
        String countryname = EntitledStatic.getValue(etsParms.con,"select country_name from decaf.country where country_code = '"+country+"'  with ur");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td>"+printLabel(false,true,":","User Information")+"</td><td colspan=\"2\">"+printLabel(false,true,"","IBM Confidential")+"</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td>"+printLabel(false,true,":","User name")+"</td><td>"+printLabel(false,true,"",ETSUtils.getUsersName(etsParms.con,requestor))+"</td></tr>");
        sBuff.append("<tr><td>"+printLabel(false,true,":","IBM ID")+"</td><td>"+printLabel(false,false,"",usrDetails.getWebId())+"</td></tr>");
        sBuff.append("<tr><td>"+printLabel(false,true,":","E-mail")+"</td><td>"+printLabel(false,false,"",usrDetails.getEMail())+"</td></tr>");
        sBuff.append("<tr><td>"+printLabel(false,true,":","Company")+"</td><td>"+printLabel(false,false,"",usrDetails.getCompany())+"</td></tr>");
        sBuff.append("<tr><td>"+printLabel(false,true,":","Address")+"</td><td>"+printLabel(false,false,"",usrDetails.getStreetAddress())+"</td></tr>");
        String usrComments = EntitledStatic.getValue(etsParms.con,"select action from ets.ets_access_log where request_id = "+requestId+"  with ur");

        sBuff.append("<tr><td>"+printLabel(false,true,":","Additional comments")+"</td><td>"+printLabel(false,false,"",usrComments)+"</td></tr>");


        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        if (usrDetails.getUserType()==usrDetails.USER_TYPE_EXTERNAL){
            sBuff.append("<tr><td colspan=\"2\"><img src=\""+Defines.V11_IMAGE_ROOT+"rules/dotted_rule_443.gif\" border=\"0\" height=\"1\" width=\"443\" alt=\"\" /></td></tr>");
            sBuff.append("<tr><td colspan=\"2\">"+printLabel(false,true,":","IBM Export Control Regulations")+"<span class=\"small\">[</span>"+ibmReg()+"<span class=\"small\">]</span>"+"</td></tr>");
            sBuff.append("<tr><td>"+printLabel(true,true,":","TG SAP company name")+"</td><td>"+company+"</td></tr>");
            sBuff.append("<tr><td>"+printLabel(true,true,":","Country")+"</td><td>"+countryname+"</td></tr>");
        }
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\"><img src=\""+Defines.V11_IMAGE_ROOT+"rules/dotted_rule_443.gif\" border=\"0\" height=\"1\" width=\"443\" alt=\"\" /></td></tr>");
        sBuff.append("<tr><td colspan=\"2\">"+printLabel(false,true,":",""+unBrandedprop.getAppName()+" access processing")+"</td></tr>");
        sBuff.append("<tr><td>"+printLabel(false,true,":","Workspace name")+"</td><td>"+wkspcname+"</td></tr>");
        String wkspcCmp = AmtCommonUtils.getTrimStr(EntitledStatic.getValue(etsParms.con,"select company from ets.ets_projects where project_id = '"+wkspc+"'  with ur"));
        sBuff.append("<tr><td>"+printLabel(false,true,":","Workspace company")+"</td><td>"+wkspcCmp+"</td></tr>");
        
        Global.println("WK SPACE COMPANY FROM GOPAGES====="+wkspcCmp);
		Global.println("ASSIGNED COMPANY FROM GOPAGES====="+company);
		
		//show this msg only when the user type is external
		if (usrDetails.getUserType()==usrDetails.USER_TYPE_EXTERNAL){
		
        if (!wkspcCmp.equals(company)){
			
            sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
            sBuff.append("<tr><td colspan=\"2\"><b><span style=\"color:rgb(255,0,0)\">Warning: The external user you are adding to this workspace is not profiled for this company, and requires a reason for entering and participating in this workspace.</span></b></td></tr>");
            sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
            sBuff.append("<tr><td><b><label for=\"label_reason\">Reason:</label></b></td><td><textarea id=\"cmpRsn\" name=\"cmpRsn\" rows=\"3\" cols=\"30\" style=\"border-style: solid;\"></textarea></td></tr>");
			if (etsProj.getProjectType().equalsIgnoreCase(Defines.AIC_WORKSPACE_TYPE)){
				sBuff.append("<tr><td headers=\"col37a\" colspan=\"2\">Adding an external user who&#39;s ICC" 
							+ " company&#39;s profile does not match the WS company profile requires the WS Owner"
							+ " to log the external user&#39;s name, date of invite and reason for adding this "
							+ "person to the workspace.  Please create a document for this purpose and file in"
							+ " the documents section of this workspace.</td></tr>");
			}            
        }
        
		}
		
				
        sBuff.append("<tr><td valign=\"top\">"+printLabel(false,true,":","Access level")+"</td>");
        String role = etsParms.getRequest().getParameter("role");
        sBuff.append("<td>"+ETSDatabaseManager.getRoleName(etsParms.con,Integer.parseInt(role))+"</td></tr>");

        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        String job = etsParms.getRequest().getParameter("job");
        if (job == null) job = "";
        sBuff.append("<tr><td>"+printLabel(false,true,":","Job responsibility")+"</td><td>"+job+"</td></tr>");

        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">"+printLabel(false,true,":","Optional Message to the User/Requestor")+"</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">"+comments+"</td></tr>");


        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td><input type=\"image\" name=\"approve\" src=\""+ Defines.BUTTON_ROOT+ "submit.gif\" onclick=\"return Validate()\" onkeypress=\"return Validate()\"  border=\"0\" width=\"120\" height=\"21\" alt=\"continue\" /></td>");
        sBuff.append("<td><table><tr><td><a href=\""+Defines.SERVLET_PATH+"ETSProcReqAccessServlet.wss?linkid="+unBrandedprop.getLinkID()+"&option=showreq&requestid="+requestId+"&ibmid="+requestor+"\"><img src=\""+Defines.BUTTON_ROOT+"arrow_lt.gif\" height=\"21\" width=\"21\" border=\"0\" alt=\"Back\" /></td><td></a><a href=\""+Defines.SERVLET_PATH+"ETSProcReqAccessServlet.wss?linkid="+unBrandedprop.getLinkID()+"&option=showreq&requestid="+requestId+"&ibmid="+requestor+"\" class=\"fbox\">Back</a></td></tr></table></td></tr>");

//        sBuff.append("<tr><td colspan=\"2\">"+printLabel(true,false,"","")+"Required fields are indicated with an asterisk(&#42;).</td></tr>");
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
        sBuff.append("<tr><td colspan=\"2\"><span style=\"color:#ff3333\"><b>Sorry, the information you entered is not associated with an approved IBM ID.</b></span></td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
		sBuff.append("<tr><td colspan=\"2\">To continue the process of adding a new member, you can send a message inviting the user to register. Simply fill in the information below.</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("</table>");
        		        
		sBuff.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"1\" width=\"443\">");
        sBuff.append("<tr><td colspan=\"2\"><table><tr><td>");
        sBuff.append("<tr><td>To:</td><td>"+printTextField(false,false,"toid",""+valHash.get("toid"),20,32,"to")+"</td></tr>");
        String comma = ((String)(valHash.get("woemailcc"))).equals("")?"":" ,";
        sBuff.append("<tr><td>cc:</td><td>"+printTextField(false,false,"ccid","",20,32,"cc")+" ,"+valHash.get("woemailcc")+"</td></tr>");
        sBuff.append("<tr><td>From:</td><td>"+valHash.get("reqemail")+"</td></tr>");
        sBuff.append("<tr><td>Subject:</td><td>Invitation to IBM "+unBrandedprop.getAppName()+"</td></tr>");
        sBuff.append("</td></tr></table></td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">Hello, you have been invited to join an "+unBrandedprop.getAppName()+" workspace.</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">At "+unBrandedprop.getAppName()+", clients of IBM "+unBrandedprop.getBrandExpsn()+" ("+unBrandedprop.getBrandAbrvn()+") and IBM team members collaborate on proposals and project information. "+unBrandedprop.getAppName()+" provides a secure workspace and comprehensive suite of on demand tools that are available online, 24/7.</td></tr>");
       // sBuff.append("<tr><td colspan=\"2\">E&TS Connect provides a secure workspace and comprehensive suite of on demand tools that are available online, 24/7.</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">Click on the link below to register and request access. Please provide the information below when prompted.</td></tr>");
        ResourceBundle resBundle = ResourceBundle.getBundle("oem.edge.common.gwa");
        String selfRegistrationURL = resBundle.getString("selfRegistrationURL");
        selfRegistrationURL = selfRegistrationURL.substring(0,selfRegistrationURL.indexOf("&okurl"));
        
		String regURL = "";
        if (etsProj.getProjectType().equals(Defines.AIC_WORKSPACE_TYPE)) {
			regURL = selfRegistrationURL+"&okurl="+webRoot+Defines.SERVLET_PATH+"AICUserAccessServlet.wss?linkid=" + unBrandedprop.getLinkID();
        } else {
			regURL = selfRegistrationURL+"&okurl="+webRoot+Defines.SERVLET_PATH+"ETSUserAccessServlet.wss?linkid=251000";
        }        
        sBuff.append("<tr><td colspan=\"2\">"+regURL+"</td></tr>");
        sBuff.append("<tr><td>IBM ID:</td><td>"+valHash.get("ibmid")+"</td></tr>");
        sBuff.append("<tr><td>Project or proposal:</td><td>"+valHash.get("projname")+"</td></tr>");
        sBuff.append("<tr><td>E-mail address of IBM contact:</td><td>"+valHash.get("woemail")+"</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">Additional comments for the invited user:</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">"+printTextField(true,false,"comments","",50,5,"addComm")+"</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
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
			
		ResourceBundle resBundle = ResourceBundle.getBundle("oem.edge.common.gwa");
		String selfRegistrationURL = resBundle.getString("selfRegistrationURL");
		selfRegistrationURL = selfRegistrationURL.substring(0,selfRegistrationURL.indexOf("&okurl"));
		String loginURL = Global.WebRoot+"/"+"index.jsp";
		
			
			sBuff.append("<form name=\"nouid\" method=\"post\" action=\"ETSProcReqAccessServlet.wss\">");
			sBuff.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"1\" width=\"443\">");
			sBuff.append("<tr><td colspan=\"2\"><span style=\"color:#ff3333\"><b>Sorry, the information you entered is not associated with an approved IBM ID.</b></span></td></tr>");
			sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
			sBuff.append("<tr><td colspan=\"2\">To continue the process of adding a new member, you can send a message inviting the user to register. Please assign Company, Country and privilege to the user and fill in the information below so the user can be automatically enabled when they login.</td></tr>");
			//sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
			sBuff.append("</table>");
        
			//invite gui utils again//
			if(!invUsrType.equals("I")) {
			InviteGuiUtils invUtils= new InviteGuiUtils();
			sBuff.append(invUtils.printInvitePage(valHash,errMsg));
			}
        
			sBuff.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"1\" width=\"443\">");
			sBuff.append("<tr><td colspan=\"2\">");
			sBuff.append("<table>");
			sBuff.append("<tr>");
			sBuff.append("<td>");
			sBuff.append("<tr><td>To:</td><td>"+printTextField(false,false,"toid",""+valHash.get("toid"),20,32,"toAddr")+"</td></tr>");
			String comma = ((String)(valHash.get("woemailcc"))).equals("")?"":" ,";
			sBuff.append("<tr><td>cc:</td><td>"+printTextField(false,false,"ccid","",20,32,"ccAddr")+" ,"+valHash.get("woemailcc")+"</td></tr>");
			sBuff.append("<tr><td>From:</td><td>"+valHash.get("reqemail")+"</td></tr>");
			sBuff.append("<tr><td>Subject:</td><td>Invitation to IBM "+unBrandedprop.getAppName()+" web portal</td></tr>");
			sBuff.append("</td></tr>");
			sBuff.append("</table>");
			sBuff.append("</td></tr>");
			
			//sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
			sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
			sBuff.append("<tr><td colspan=\"2\">I would like you to join a workspace for '"+valHash.get("projname")+"' on IBM "+unBrandedprop.getAppName()+" web portal. We will use this workspace  to collaborate and share information securely.</td></tr>");
			sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
		   	sBuff.append("<tr><td colspan=\"2\">Please follow these two steps:</td></tr>");
			sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
			
			sBuff.append("<tr><td colspan=\"2\">---------------------------------------------------------------------------------------</td></tr>");
			sBuff.append("<tr><td colspan=\"2\">STEP #1: Create an IBM ID and password</td></tr>");
			sBuff.append("<tr><td colspan=\"2\">---------------------------------------------------------------------------------------</td></tr>");
			sBuff.append("<tr><td colspan=\"2\">Click and follow URL to register your ID of <"+valHash.get("ibmid")+">. Make sure you fill your \"company address\" completely as it will be required for access to the workspace.</td></tr>");
			sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");  
			sBuff.append("<tr><td colspan=\"2\">"+selfRegistrationURL+"</td></tr>");
			sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
			
			sBuff.append("<tr><td colspan=\"2\">---------------------------------------------------------------------------------------</td></tr>");
			sBuff.append("<tr><td colspan=\"2\">STEP #2: Log in to initiate access to the workspace</td></tr>");
			sBuff.append("<tr><td colspan=\"2\">---------------------------------------------------------------------------------------</td></tr>");
						
			sBuff.append("<tr><td colspan=\"2\">Click on the link below and login with your newly created IBM ID and password. You do not need to do anything further,the system will initiate the final processing based on your  login and you will be informed by an email once your access is complete.  </td></tr>");
			sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
			
			sBuff.append("<tr><td colspan=\"2\">"+loginURL+"</td></tr>");
			
			sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
		
			sBuff.append("<tr><td colspan=\"2\">If you run into any difficulty, you can contact me or our 24x7 help desk at "+AmtHfConstants.sAmtHfEdgeContPhoneNum+" for US & Canada or "+AmtHfConstants.sAmtHfEdgeContPhoneNumOtherGeos+" for international users.</td></tr>");
			
			sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
			
			sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
			sBuff.append("<tr><td colspan=\"2\">Additional comments for the invited user:</td></tr>");
			sBuff.append("<tr><td colspan=\"2\">"+printTextField(true,false,"comments","",50,5,"invUsrComm")+"</td></tr>");
			sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
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
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");

        sBuff.append("<tr><td colspan=\"2\"><table><tr><td>");
        sBuff.append("<tr><td>To:</td><td>"+printTextField(false,false,"toid",""+valHash.get("toid"),20,32,"toFiled")+"</td></tr>");
        String comma = ((String)(valHash.get("woemailcc"))).equals("")?"":" ,";
        sBuff.append("<tr><td>cc:</td><td>"+printTextField(false,false,"ccid","",20,32,"ccField")+comma+valHash.get("woemailcc")+"</td></tr>");
        sBuff.append("<tr><td>From:</td><td>"+valHash.get("reqemail")+"</td></tr>");
        sBuff.append("<tr><td>Subject:</td><td>Invitation to IBM "+unBrandedprop.getAppName()+"</td></tr>");
        sBuff.append("</td></tr></table></td></tr>");

        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">Hello, you have been invited to join an "+unBrandedprop.getAppName()+" workspace.</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">At "+unBrandedprop.getAppName()+", clients of IBM "+unBrandedprop.getBrandExpsn()+" ("+unBrandedprop.getBrandAbrvn()+") and IBM team members collaborate on proposals and project information.");
        sBuff.append(" "+unBrandedprop.getAppName()+" provides a secure workspace and comprehensive suite of on demand tools that are available online, 24/7.</td></tr>");
        
        //start donot show registartion info for Internal users >> UD change
        
        if(!invUsrType.equals("I")) {
        
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">Click on the link below to register and request access. Please provide the information below when prompted.</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">Registration link</td></tr>");
        ResourceBundle resBundle = ResourceBundle.getBundle("oem.edge.common.gwa");
        String selfRegistrationURL = resBundle.getString("selfRegistrationURL");
        selfRegistrationURL = selfRegistrationURL.substring(0,selfRegistrationURL.indexOf("&okurl"));
        
        String regURL = "";
        if (unBrandedprop.getBrandAbrvn().equals(Defines.AIC_WORKSPACE_TYPE)){
			regURL = selfRegistrationURL+"&okurl="+webRoot+Defines.SERVLET_PATH+"AICUserAccessServlet.wss?linkid="+unBrandedprop.getLinkID()+"";	
        } else {
			regURL = selfRegistrationURL+"&okurl="+webRoot+Defines.SERVLET_PATH+"ETSUserAccessServlet.wss?linkid="+unBrandedprop.getLinkID()+"";
        }
        sBuff.append("<tr><td colspan=\"2\">"+regURL+"</td></tr>");
        
        }
        
        //End donot
        
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">Registration information</td></tr>");
        sBuff.append("<tr><td>IBM ID:</td><td>"+valHash.get("ibmid")+"</td></tr>");
        sBuff.append("<tr><td>Project or proposal:</td><td>"+valHash.get("projname")+"</td></tr>");
        sBuff.append("<tr><td>E-mail address of IBM contact:</td><td>"+valHash.get("woemail")+"</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">Additional comments for the invited user:</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">"+printTextField(true,false,"comments","",50,5,"invComment")+"</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
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
		
		String reqURL = "";
		if (unBrandedprop.getBrandAbrvn().equals(Defines.AIC_WORKSPACE_TYPE)) {
			reqURL = webRoot+Defines.SERVLET_PATH+"AICUserAccessServlet.wss";
		}else {
			reqURL = webRoot+Defines.SERVLET_PATH+"ETSUserAccessServlet.wss";
		}
		
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
        sBuff.append("<tr><td colspan=\"2\"><span style=\"color:#ff3333\"><b>"+head1+"</b></span></td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">"+head2+"</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");

        sBuff.append("<tr><td colspan=\"2\"><table><tr><td>");
        sBuff.append("<tr><td>To:</td><td>"+printTextField(false,false,"toid",""+valHash.get("toid"),20,32,"ToMsg")+"</td></tr>");
        String comma = ((String)(valHash.get("woemailcc"))).equals("")?"":" ,";
        sBuff.append("<tr><td>cc:</td><td>"+printTextField(false,false,"ccid","",20,32,"frmMsg")+comma+valHash.get("woemailcc")+"</td></tr>");
        sBuff.append("<tr><td>From:</td><td>"+valHash.get("reqemail")+"</td></tr>");
        sBuff.append("<tr><td>Subject:</td><td>"+subj+"</td></tr>");
        sBuff.append("</td></tr></table></td></tr>");

        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        ResourceBundle resBundle = ResourceBundle.getBundle("oem.edge.common.gwa");
        String changeProfileURL = resBundle.getString("changeProfileURL");
        changeProfileURL = changeProfileURL.substring(0,changeProfileURL.indexOf("&okurl"));

        if (errCode==1){
            sBuff.append("<tr><td colspan=\"2\">Hello, you have been invited to join an "+unBrandedprop.getAppName()+"  workspace.</td></tr>");
            sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
            sBuff.append("<tr><td colspan=\"2\">At "+unBrandedprop.getAppName()+" , clients of IBM "+unBrandedprop.getBrandExpsn()+"  ("+unBrandedprop.getBrandAbrvn()+") and IBM team members collaborate  on proposals and project information. "+unBrandedprop.getAppName()+"  provides a secure workspace and comprehensive suite of on demand tools that are available online, 24/7.</td></tr>");
            sBuff.append("<tr><td colspan=\"2\">A member has tried to add you to a workspace, but your address in the IBM user profile is empty or is not valid. You must update your profile to continue the process.</td></tr>");
            sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
            sBuff.append("<tr><td colspan=\"2\">1.Click Change Profile link below and update your IBM profile.</td></tr>");
            sBuff.append("<tr><td colspan=\"2\">"+changeProfileURL+"</td></tr>");
            sBuff.append("<tr><td colspan=\"2\">2.Click Request Access link below. Follow the instructions on the Web page, entering the information below when prompted.</td></tr>");
            sBuff.append("<tr><td colspan=\"2\">"+ reqURL +"</td></tr>");
            sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
            sBuff.append("<tr><td colspan=\"2\">Workspace:          "+valHash.get("projname")+"</td></tr>");
            sBuff.append("<tr><td colspan=\"2\">IBM contact e-mail: "+valHash.get("woemail")+"</td></tr>");
            sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");

        }
        if (errCode==2){
           

            sBuff.append("<tr><td colspan=\"2\">Hello, you have been invited to join an "+unBrandedprop.getAppName()+"  workspace.</td></tr>");
            sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
            sBuff.append("<tr><td colspan=\"2\">At "+unBrandedprop.getAppName()+" , clients of IBM "+unBrandedprop.getBrandExpsn()+"  ("+unBrandedprop.getBrandAbrvn()+") and IBM team members collaborate  on proposals and project information. "+unBrandedprop.getAppName()+" provides a secure workspace and comprehensive suite of on demand tools that are available online, 24/7.</td></tr>");
            sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
            sBuff.append("<tr><td colspan=\"2\">A member has tried to add you to a workspace, but your IBM ID has not been validated. To join the workspace, please complete these steps:</td></tr>");
            sBuff.append("<tr><td colspan=\"2\">1.Check your e-mail for the validation code that we have sent to your e-mail address.</td></tr>");
            sBuff.append("<tr><td colspan=\"2\">2.Click Request Access link below.</td></tr>");
			sBuff.append("<tr><td colspan=\"2\">"+ reqURL +"</td></tr>");
            sBuff.append("<tr><td colspan=\"2\">3.Log in and enter the validation code from your e-mail into the pop-up window.</td></tr>");
            sBuff.append("<tr><td colspan=\"2\">4.Follow the instructions on the Request Access Web page, entering the information below when prompted.</td></tr>");
            sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
            sBuff.append("<tr><td colspan=\"2\">Workspace:"+valHash.get("projname")+"</td></tr>");
            sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
            sBuff.append("<tr><td colspan=\"2\">If you experience problems with any of these steps, please send a message to econnect@us.ibm.com, requesting validation of your IBM ID.</td></tr>");
        }

        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">Additional comments for the invited user:</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">"+printTextField(true,false,"comments","",50,5,"msgToInvUsr")+"</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
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

//	check with laxman
    public String reqFwd() throws SQLException,Exception{
        StringBuffer sBuff = new StringBuffer();
        boolean brand=false;
        
		String wkspc = etsParms.getRequest().getParameter("wkspc");
		
		UnbrandedProperties unBrandedprop=null;
				
		
        if(AmtCommonUtils.isResourceDefined(wkspc)) {
        
			
		unBrandedprop = WrkSpcTeamUtils.getBrandPropsFromProjId(wkspc);
		
			if(unBrandedprop != null && AmtCommonUtils.isResourceDefined(unBrandedprop.getAppName())) {
		
			brand=true;
		
			}
		
        }
		
        sBuff.append("<table>");
        sBuff.append("<tr><td>"+this.getErrMsg()+"</td></tr>");
        sBuff.append("<tr><td>&nbsp;</td></tr>");
        if(brand) {
        	
			sBuff.append("<tr><td><table><tr><td><a href=\""+Defines.SERVLET_PATH+unBrandedprop.getLandingPageURL()+"?linkid="+unBrandedprop.getLinkID()+"\"><img src=\""+Defines.BUTTON_ROOT+"arrow_rd.gif\" height=\"21\" width=\"21\" border=\"0\" alt=\"Ok\" /></td><td></a><a href=\""+Defines.SERVLET_PATH+unBrandedprop.getLandingPageURL()+"?linkid="+unBrandedprop.getLinkID()+"\" class=\"fbox\">Ok</a></td></tr></table></td></tr>");
        }
        else {
        	
			sBuff.append("<tr><td><table><tr><td><a href=\""+Defines.SERVLET_PATH+"ETSConnectServlet.wss?linkid=251000\"><img src=\""+Defines.BUTTON_ROOT+"arrow_rd.gif\" height=\"21\" width=\"21\" border=\"0\" alt=\"Ok\" /></td><td></a><a href=\""+Defines.SERVLET_PATH+"ETSConnectServlet.wss?linkid=251000\" class=\"fbox\">Ok</a></td></tr></table></td></tr>");
        }
        //sBuff.append("<tr><td><table><tr><td><a href=\""+Defines.SERVLET_PATH+"ETSConnectServlet.wss?linkid=251000\"><img src=\""+Defines.BUTTON_ROOT+"arrow_rd.gif\" height=\"21\" width=\"21\" border=\"0\" alt=\"Ok\" /></td><td></a><a href=\""+Defines.SERVLET_PATH+"ETSConnectServlet.wss?linkid=251000\" class=\"fbox\">Ok</a></td></tr></table></td></tr>");
        sBuff.append("</table>");
        return sBuff.toString();

    }
    
//	check with laxman
    public String acceptOk() throws SQLException,Exception{
    	
    	boolean brand=false;
    	
		String wkspc = etsParms.getRequest().getParameter("wkspc");
		
		UnbrandedProperties unBrandedprop=null;
				
		
		if(AmtCommonUtils.isResourceDefined(wkspc)) {
        
			
			unBrandedprop = WrkSpcTeamUtils.getBrandPropsFromProjId(wkspc);
		
			if(unBrandedprop != null && AmtCommonUtils.isResourceDefined(unBrandedprop.getAppName())) {
		
				brand=true;
		
			}
		
		}
        StringBuffer sBuff = new StringBuffer();
        sBuff.append("<table>");
        sBuff.append("<tr><td>"+this.getErrMsg()+"</td></tr>");
        sBuff.append("<tr><td>&nbsp;</td></tr>");
        
        if(brand) {
        	
			sBuff.append("<tr><td><table><tr><td><a href=\""+Defines.SERVLET_PATH+unBrandedprop.getLandingPageURL()+"?linkid="+unBrandedprop.getLinkID()+"\"><img src=\""+Defines.BUTTON_ROOT+"arrow_rd.gif\" height=\"21\" width=\"21\" border=\"0\" alt=\"Ok\" /></td><td></a><a href=\""+Defines.SERVLET_PATH+unBrandedprop.getLandingPageURL()+"?linkid="+unBrandedprop.getLinkID()+"\" class=\"fbox\">Ok</a></td></tr></table></td></tr>");
        }
        
        else {
        	
			sBuff.append("<tr><td><table><tr><td><a href=\""+Defines.SERVLET_PATH+"ETSConnectServlet.wss?linkid=251000\"><img src=\""+Defines.BUTTON_ROOT+"arrow_rd.gif\" height=\"21\" width=\"21\" border=\"0\" alt=\"Ok\" /></td><td></a><a href=\""+Defines.SERVLET_PATH+"ETSConnectServlet.wss?linkid=251000\" class=\"fbox\">Ok</a></td></tr></table></td></tr>");
        }
        //sBuff.append("<tr><td><table><tr><td><a href=\""+Defines.SERVLET_PATH+"ETSConnectServlet.wss?&linkid=251000\"><img src=\""+Defines.BUTTON_ROOT+"arrow_rd.gif\" height=\"21\" width=\"21\" border=\"0\" alt=\"Ok\" /></td><td></a><a href=\""+Defines.SERVLET_PATH+"ETSConnectServlet.wss?&linkid=251000\" class=\"fbox\">Ok</a></td></tr></table></td></tr>");
        sBuff.append("</table>");
        return sBuff.toString();
    }
    public String woLevel() throws Exception{
        StringBuffer sBuff = new StringBuffer();
        String requestId = etsParms.getRequest().getParameter("requestid");
        String comments = etsParms.getRequest().getParameter("comments");
        String company = "";
        String country = "";
        String wkspc = etsParms.getRequest().getParameter("wkspc");
        String requestor = etsParms.getRequest().getParameter("ibmid");
        
		ETSProj etsProj = ETSDatabaseManager.getProjectDetails(wkspc);
       
		UnbrandedProperties unBrandedprop = WrkSpcTeamUtils.getBrandPropsFromProjId(wkspc);
        
		String reqstdEntitlement = WrkSpcTeamUtils.getWrkSpcReqEntitlement(etsProj);
		logger.debug("reqstdEntitlement entitlement in woLevel=== " + reqstdEntitlement);

		String reqstdProject = WrkSpcTeamUtils.getWrkSpcReqProject(etsProj);
		logger.debug("reqstdProject in woLevel===" + reqstdProject);
        
        sBuff.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"1\">");
        sBuff.append("<tr><td colspan=\"2\" width=\"443\">You are listed as the IBM contact for the request below. Please complete the information needed for IBM Export Control Regulations and processing.</td></tr>");

		boolean bExternal = false;  //spn 5.2.1
		
        ETSUserDetails usrDetails  = new ETSUserDetails();
        usrDetails.setWebId(requestor.trim());
        usrDetails.extractUserDetails(etsParms.con);
        if (usrDetails.getUserType()==usrDetails.USER_TYPE_EXTERNAL){
            company = etsParms.getRequest().getParameter("company");
            country = etsParms.getRequest().getParameter("country");
			bExternal = true;
        }
        
        // getwkspcNam
        String wkspcname = EntitledStatic.getValue(etsParms.con,"select project_name from ets.ets_projects where project_id = '"+wkspc+"'  with ur");
        // getcountry name
        String countryname = EntitledStatic.getValue(etsParms.con,"select country_name from decaf.country where country_code = '"+country+"'  with ur");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td class=\"tdblue\" colspan=\"2\" >"+usrDetails.getFirstName()+"&nbsp;"+usrDetails.getLastName()+"["+requestor+"]</td></tr>");

        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\"><img src=\""+Defines.V11_IMAGE_ROOT+"rules/dotted_rule_443.gif\" border=\"0\" height=\"1\" width=\"443\" alt=\"\" /></td></tr>");
        sBuff.append("<tr><td colspan=\"2\">"+printLabel(false,true,":","User Information")+"</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td>"+printLabel(false,true,":","User name")+"</td><td>"+printLabel(false,true,"",ETSUtils.getUsersName(etsParms.con,requestor))+"</td></tr>");
        sBuff.append("<tr><td>"+printLabel(false,true,":","IBM ID")+"</td><td>"+printLabel(false,false,"",usrDetails.getWebId())+"</td></tr>");
        sBuff.append("<tr><td>"+printLabel(false,true,":","E-mail")+"</td><td>"+printLabel(false,false,"",usrDetails.getEMail())+"</td></tr>");
        sBuff.append("<tr><td>"+printLabel(false,true,":","Company")+"</td><td>"+printLabel(false,false,"",usrDetails.getCompany())+"</td></tr>");
        sBuff.append("<tr><td>"+printLabel(false,true,":","Address")+"</td><td>"+printLabel(false,false,"",usrDetails.getStreetAddress())+"</td></tr>");
        String usrComments = EntitledStatic.getValue(etsParms.con,"select action from ets.ets_access_log where request_id = "+requestId+"  with ur");

        sBuff.append("<tr><td>"+printLabel(false,true,":","Additional comments")+"</td><td>"+printLabel(false,false,"",usrComments)+"</td></tr>");


        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        if (usrDetails.getUserType()==usrDetails.USER_TYPE_EXTERNAL){
            sBuff.append("<tr><td colspan=\"2\"><img src=\""+Defines.V11_IMAGE_ROOT+"rules/dotted_rule_443.gif\" border=\"0\" height=\"1\" width=\"443\" alt=\"\" /></td></tr>");
            sBuff.append("<tr><td colspan=\"2\">"+printLabel(false,true,":","IBM Export Control Regulations")+"<span class=\"small\">[</span>"+ibmReg()+"<span class=\"small\">]</span>"+"</td></tr>");
            sBuff.append("<tr><td>"+printLabel(true,true,":","TG SAP company name")+"</td><td>"+company+"</td></tr>");
            sBuff.append("<tr><td>"+printLabel(true,true,":","Country")+"</td><td>"+countryname+"</td></tr>");
        }
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\"><img src=\""+Defines.V11_IMAGE_ROOT+"rules/dotted_rule_443.gif\" border=\"0\" height=\"1\" width=\"443\" alt=\"\" /></td></tr>");
        sBuff.append("<tr><td colspan=\"2\">"+printLabel(false,true,":",""+unBrandedprop.getAppName()+" access processing")+"</td></tr>");
        sBuff.append("<tr><td>"+printLabel(true,true,":","Workspace name")+"</td><td>"+wkspcname+"</td></tr>");

		boolean bHasPOCEnt = ETSWorkspaceDAO.doesUserHaveEntitlement(etsParms.con,etsParms.es.gIR_USERN,Defines.MULTIPOC);
		boolean bPOCEntRequested = false;
		boolean bUserProjectsEnt = ETSWorkspaceDAO.doesUserHaveEntitlement(etsParms.con,usrDetails.getWebId(),reqstdEntitlement);
		logger.debug("****="+usrDetails.getWebId()+"****="+bUserProjectsEnt);

		if (!bHasPOCEnt && !wkspc.trim().equalsIgnoreCase("")) {  //spn 5.2.1
		   ETSUserAccessRequest uar = new ETSUserAccessRequest();
		   uar.setTmIrId(etsParms.es.gIR_USERN);
		   uar.setPmIrId(etsParms.es.gIR_USERN);
		   uar.setDecafEntitlement(Defines.MULTIPOC);
		   uar.setIsAProject(false);
		   uar.setUserCompany("");
			ETSStatus status = uar.request(etsParms.con);
  			if (status.getErrCode() == 0) {
				bPOCEntRequested = true;
			} 
			else {
				bPOCEntRequested = false;
			}
		}
		
		if (wkspc.trim().equalsIgnoreCase("")){ //spn 5.2.1
			sBuff.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
			sBuff.append("<tr><td colspan=\"2\"><span style=\"color:#ff3333\"><b>Warning: This request can not be processed further as a valid workspace was not chosen.</b></span></td></tr>");
			sBuff.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");


			sBuff.append("<tr><td colspan=\"2\"><table><tr><td valign=\"middle\"><a href=\""+Defines.SERVLET_PATH+unBrandedprop.getLandingPageURL()+"?linkid="+unBrandedprop.getLinkID()+"\"><img src=\""+Defines.BUTTON_ROOT+"arrow_rd.gif\" height=\"21\" width=\"21\" border=\"0\" alt=\"Ok\" /></a></td><td valign=\"middle\"><a href=\""+Defines.SERVLET_PATH+unBrandedprop.getLandingPageURL()+"?linkid="+unBrandedprop.getLinkID()+"\" class=\"fbox\">Ok</a></td></tr></table></td></tr>");

		}
		else if (!bHasPOCEnt && (bExternal && !bUserProjectsEnt)) {  //spn 5.2.1
		
			sBuff.append("<tr><td colspan=\"2\"><img src=\""+Defines.V11_IMAGE_ROOT+"rules/dotted_rule_443.gif\" border=\"0\" height=\"1\" width=\"443\" alt=\"\" /></td></tr>");
        	
        	
			String manager = "";
			manager = ETSUtils.getManagersEMailFromDecafTable(etsParms.con, etsParms.es.gUSERN);
			if (!manager.equals("")){
				manager = "at "+manager;
			}

			sBuff.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
			sBuff.append("<tr><td colspan=\"2\"><span style=\"color:#ff3333\"><b>Warning: This request can not be processed further as you do not have the MultiPOC  entitlement</b></span></td></tr>");
			sBuff.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
			sBuff.append("<tr><td colspan=\"2\">A MultiPOC  entitlement request will be sent to your manager "+manager+" and then to the system level administrator for approval. You will receive an e-mail from IBM Customer Connect when this has been processed.</td></tr>");
			sBuff.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");

			
			sBuff.append("<tr><td colspan=\"2\"><table><tr><td valign=\"middle\"><a href=\""+Defines.SERVLET_PATH+unBrandedprop.getLandingPageURL()+"?linkid="+unBrandedprop.getLinkID()+"\"><img src=\""+Defines.BUTTON_ROOT+"arrow_rd.gif\" height=\"21\" width=\"21\" border=\"0\" alt=\"Ok\" /></a></td><td valign=\"middle\"><a href=\""+Defines.SERVLET_PATH+unBrandedprop.getLandingPageURL()+"?linkid="+unBrandedprop.getLinkID()+"\" class=\"fbox\">Ok</a></td></tr></table></td></tr>");
		}
		else  {
	        // get member roleid for
	        // get WM role id for the workspcae
	        sBuff.append("<tr bgcolor=\"#eeeeee\"><td valign=\"top\">"+printLabel(true,true,":","Access level")+"</td>");
	        sBuff.append("<td><table>");
	        Vector r = ETSDatabaseManager.getRolesPrivs(wkspc,etsParms.con);
	        if (usrDetails.getUserType()==usrDetails.USER_TYPE_INTERNAL){
	            for (int i = 0; i<r.size(); i++){
	                String[] rp = (String[])r.elementAt(i);
	                int roleid = (new Integer(rp[0])).intValue();
	                String rolename = rp[1];
	                String privids = rp[3];
	                if (!(ETSDatabaseManager.doesRoleHavePriv(roleid,Defines.OWNER,etsParms.con))) {
	                    sBuff.append("<tr><td>"+printRadioButton("role",""+roleid,true)+printLabel(false,false,"",rolename)+"</td></tr>");
	                }
	            }
	        }
	
	        if (usrDetails.getUserType()==usrDetails.USER_TYPE_EXTERNAL){
	            for (int i = 0; i<r.size(); i++){
	                String[] rp = (String[])r.elementAt(i);
	                int roleid = (new Integer(rp[0])).intValue();
	                String rolename = rp[1];
	                String privids = rp[3];
	                if (!(ETSDatabaseManager.doesRoleHavePriv(roleid,Defines.OWNER,etsParms.con)) && !(ETSDatabaseManager.doesRoleHavePriv(roleid,Defines.ADMIN,etsParms.con))){
	                    sBuff.append("<tr><td>"+printRadioButton("role",""+roleid,true)+printLabel(false,false,"",rolename)+"</td></tr>");
	                }
	            }
	        }
	
	        sBuff.append("</table></td></tr>");
	
	        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
	        sBuff.append("<tr><td>"+printLabel(false,true,":","Job responsibility")+"</td><td>"+printTextField(false,false,"job","",20,40,"jobResp")+"</td></tr>");
	
	        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
	        sBuff.append("<tr><td colspan=\"2\">"+printLabel(false,true,":","Optional Message to the User/Requestor")+"</td></tr>");
	        sBuff.append("<tr><td colspan=\"2\"><textarea name=\"comments\" cols=\"53\" rows=\"5\">"+comments+"</textarea></td></tr>");
	
	
	        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
	        sBuff.append("<tr><td><input type=\"image\" name=\"approve\" src=\""+ Defines.BUTTON_ROOT+ "submit.gif\"  border=\"0\" width=\"120\" height=\"21\" alt=\"continue\" /></td>");
	        sBuff.append("<td><table><tr><td><a href=\""+Defines.SERVLET_PATH+unBrandedprop.getLandingPageURL()+"?linkid="+unBrandedprop.getLinkID()+"\"><img src=\""+Defines.BUTTON_ROOT+"cancel_rd.gif\" height=\"21\" width=\"21\" border=\"0\" alt=\"Cancel\" /></td><td></a><a href=\""+Defines.SERVLET_PATH+unBrandedprop.getLandingPageURL()+"?linkid="+unBrandedprop.getLinkID()+"\" class=\"fbox\">Cancel</a></td></tr></table></td></tr>");
	
	        sBuff.append("<tr><td colspan=\"2\">"+printLabel(true,false,"","")+"Required fields are indicated with an asterisk(&#42;).</td></tr>");
	        sBuff.append("<input type=\"hidden\" name=\"requestid\" value=\""+requestId+"\" />");
	        sBuff.append("<input type=\"hidden\" name=\"ibmid\" value=\""+requestor+"\" />");
	        if (usrDetails.getUserType()==usrDetails.USER_TYPE_EXTERNAL){
	            sBuff.append("<input type=\"hidden\" name=\"company\" value=\""+company+"\" />");
	            sBuff.append("<input type=\"hidden\" name=\"country\" value=\""+country+"\" />");
	        }
	        sBuff.append("<input type=\"hidden\" name=\"wkspc\" value=\""+wkspc+"\" />");
	        sBuff.append("<input type=\"hidden\" name=\"option\" value=\"procreqz\" />");
	        sBuff.append("<input type=\"hidden\" name=\"linkid\" value=\""+unBrandedprop.getLinkID()+"" +
	        	"\" />");
		}
        sBuff.append("</table>");


        return sBuff.toString();
    }

    public String useridChoice(Hashtable vals, Connection conn) throws SQLException,Exception{
    	
		Statement st  =null;
		ResultSet rs1 = null;
		ResultSet rs =  null;
		StringBuffer sBuff = new StringBuffer();
		
		try {
		
		
        String emailid = (String)vals.get("email");
        String projectId = (String)vals.get("projid");
		
		UnbrandedProperties unBrandedprop = WrkSpcTeamUtils.getBrandPropsFromProjId(projectId);
        
        sBuff.append("<form name=\"multiusr\" method=\"post\" action=\"ETSProjectsServlet.wss\">");
        sBuff.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"3\">");

        sBuff.append("<td colspan=\"4\">This email address, "+emailid+" is shared by the following userids:</td>");
        sBuff.append("<tr><td colspan=\"4\">&nbsp;</td></tr>");

        sBuff.append("<tr><td  colspan=\"4\" valign=\"top\" ><b>The following userids have access to this workspace.</b></tr>");
        sBuff.append("<tr class=\"tblue\">");
        sBuff.append("<td>&nbsp;</td><td>IBM ID</td><td>User name</td><td>Last logon<br />mm/dd/yyyy</td>");
        sBuff.append("</tr>");
        String bgColor = "#eeeeee";
        st  = conn.createStatement();
        String qry1 = "select a.ir_userid, a.user_fname||' '||a.user_lname, char(date(a.last_logon),usa) from amt.users a, ets.ets_users b where a.user_email = '"+emailid+"' and a.status='A' and a.ir_userid = b.user_id and b.user_project_id = '"+projectId+"' order by 3 with ur";
        rs1 = st.executeQuery(qry1);
        while (rs1.next()){
            sBuff.append("<tr bgcolor=\""+(bgColor=bgColor.equals("#eeeeee")?"#ffffff":"#eeeeee")+"\">"); //add_id
            sBuff.append("<td>&nbsp;</td><td>"+rs1.getString(1).trim()+"</td><td>"+rs1.getString(2).trim()+"</td><td>"+rs1.getString(3).trim()+"</td>");
            sBuff.append("</tr>");
        }
        //rs1.close();
        // divider

        sBuff.append("<tr><td colspan=\"4\">&nbsp;</td></tr>");
        sBuff.append("<tr><td  colspan=\"4\" valign=\"top\" ><b>The following userids do not have access to this workspace. Please choose the right userid and submit the request.</b></td></tr>");
        sBuff.append("<tr class=\"tblue\"><td>Select</td><td>IBM ID</td><td>User name</td><td>Last logon<br />mm/dd/yyyy</td></tr>");
        String qry = "select a.ir_userid, a.user_fname||' '||a.user_lname, char(date(a.last_logon),usa) from amt.users a where a.user_email = '"+emailid+"' and a.status='A' and a.ir_userid not in (select a.ir_userid from amt.users a, ets.ets_users b where a.user_email = '"+emailid+"' and a.status='A' and a.ir_userid = b.user_id and b.user_project_id = '"+projectId+"') order by 3 with ur";
        rs = st.executeQuery(qry);
        bgColor="#eeeeee";
        while (rs.next()){
            sBuff.append("<tr bgcolor=\""+(bgColor = bgColor.equals("#eeeeee")?"#ffffff":"#eeeeee")+"\"><td>"+printRadioButton("uid",rs.getString(1).trim(),false)+"</td><td>"+rs.getString(1).trim()+"</td><td>"+rs.getString(2).trim()+"</td><td>"+rs.getString(3).trim()+"</td></tr>");
        }
        sBuff.append("<tr><td colspan=\"4\">&nbsp;</td></tr>");

        sBuff.append("<tr><td colspan=\"2\"><input type=\"image\" name=\"submit\" src=\""+ Defines.BUTTON_ROOT+ "submit.gif\"  border=\"0\" width=\"120\" height=\"21\" alt=\"\" /></td>");
        sBuff.append("<td colspan=\"2\"><table><tr><td><a href=\""+Defines.SERVLET_PATH+"ETSProjectsServlet.wss?option=addreqmember&proj="+vals.get("projid")+"&tc="+vals.get("tc")+"&cc="+vals.get("cc")+"&linkid="+unBrandedprop.getLinkID()+"\"><img src=\""+Defines.BUTTON_ROOT+"cancel_rd.gif\" height=\"21\" width=\"21\" border=\"0\" alt=\"Cancel\" /></td><td></a><a href=\""+Defines.SERVLET_PATH+"ETSProjectsServlet.wss?option=addreqmember&proj="+vals.get("projid")+"&tc="+vals.get("tc")+"&cc="+vals.get("cc")+"&linkid="+unBrandedprop.getLinkID()+"\" class=\"fbox\">Cancel</a></td></tr></table></td></tr>");
        sBuff.append("</table>");
        sBuff.append("<input type=\"hidden\" name=\"action\" value=\"addreqmember\" />");
        sBuff.append("<input type=\"hidden\" name=\"proj\" value=\""+vals.get("projid")+"\" />");
        sBuff.append("<input type=\"hidden\" name=\"tc\" value=\""+vals.get("tc")+"\" />");
        sBuff.append("<input type=\"hidden\" name=\"cc\" value=\""+vals.get("cc")+"\" />");
        sBuff.append("</form>");
        
		}
		
		finally {
			
			ETSDBUtils.close(rs1);
			ETSDBUtils.close(rs);
			ETSDBUtils.close(st);
		}
        return sBuff.toString();
    }

    public String ibmReg (){
        StringBuffer sBuff = new StringBuffer();
        sBuff.append(" <a class=\"fbox\" name=\"BLBEXPORT_HELP\" href=\"#BLBEXPORT_HELP\"");
        sBuff.append(" onclick=\"window.open('EtsIssHelpBlurbServlet.wss?OP=Blurb&BlurbOfField=EXPORT_HELP','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=350,height=450'); return false\" ");
        sBuff.append(" onkeypress=\"window.open('EtsIssHelpBlurbServlet.wss?OP=Blurb&BlurbOfField=EXPORT_HELP','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=350,height=450')\">click here for more details on export control</a>");
        return sBuff.toString();
    }

    public String accReg (){
        StringBuffer sBuff = new StringBuffer();
        sBuff.append(" <a class=\"fbox\" name=\"BLBACCESS_HELP\" href=\"#BLBACCESS_HELP\"");
        sBuff.append(" onclick=\"window.open('EtsIssHelpBlurbServlet.wss?OP=Blurb&BlurbOfField=ACCESS_HELP','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=350,height=450'); return false\" ");
        sBuff.append(" onkeypress=\"window.open('EtsIssHelpBlurbServlet.wss?OP=Blurb&BlurbOfField=ACCESS_HELP','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=350,height=450')\">More details on E&amp;TS Connect access processing</a>");
        return sBuff.toString();
    }



    /*************************************************/
    // private methods
    /*************************************************/


    private String printLabel (boolean isMandatory, boolean isBold, String sperator, String labelString){
        return ("<span style=\""+(isBold?"font-weight:bold":"")+"\">"+(isMandatory?"<span style=\"color:red\">*</span>":"")+""+labelString+sperator+"</span>");
    }
    private String printTextField(boolean isTextArea, boolean isPassword, String name, String value, int length, int maxlength, String label){
        if (isTextArea){
            return ("<label for=\""+label+"\"></label><textarea  id=\""+label+"\" name=\""+name+"\" value=\""+value+"\" cols=\""+length+"\" rows=\""+maxlength+"\"></textarea>");
        } else {
            return ("<label for=\""+label+"\"><input type=\""+(isPassword?"password":"text")+"\" id=\""+label+"\" name=\""+name+"\" value=\""+value+"\" length=\""+length+"\" maxlength=\""+maxlength+"\" /></label>");
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
    
    public String printInviteBkMsg(String projectId,String topCatId,String linkid) {
    	
    	StringBuffer sb = new StringBuffer();
    	   	
    	
		sb.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"1\" width=\"443\">");
		sb.append("<tr>");
		sb.append("<td>");
		sb.append("The invited user information has been registered and an email has been sent to the invited user with registration details. Please click 'Back to team listing' to see the invitations list.");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("<tr><td>&nbsp;</td></tr>");
		sb.append("</table>");
		
		sb.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"1\" width=\"443\">");
		sb.append("<tr>");
		sb.append("<td  align=\"right\" valign=\"top\" width=\"16\">");
		sb.append("<a href=\"ETSProjectsServlet.wss?proj=" + projectId + "&tc=" + topCatId + "&cc=" + topCatId + "&linkid=" + linkid + "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" width=\"16\" height=\"16\" border=\"0\" alt=\"back\" /></a>&nbsp;");
		sb.append("</td> ");
		sb.append("<td  align=\"left\" valign=\"top\">");
		sb.append("<a href=\"ETSProjectsServlet.wss?proj=" + projectId + "&tc=" + topCatId + "&cc=" + topCatId + "&linkid=" + linkid + "\" class=\"fbox\">Back to team listing</a>");
		sb.append("</td> ");
		sb.append("</tr>");
		sb.append("</table>");		
    	
    	return sb.toString();
    }
    private String getErrMsg(){return this.errMsg;}
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
