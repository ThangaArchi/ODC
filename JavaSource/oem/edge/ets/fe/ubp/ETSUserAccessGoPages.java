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
package oem.edge.ets.fe.ubp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.Vector;

import oem.edge.amt.EntitledStatic;
import oem.edge.common.Global;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSMetricsFunctions;
import oem.edge.ets.fe.ETSParams;

/**
 * @author v2ravik
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ETSUserAccessGoPages {
	
	public static final String VERSION = "1.23";

     public ETSUserAccessGoPages(){
         this.etsParms = new ETSParams();
         this.errMsg = "";
         ResourceBundle resBundle = ResourceBundle.getBundle("oem.edge.common.gwa");

//        selfRegistrationURL = resBundle.getString("selfRegistrationURL");
//        selfRegistrationURL = selfRegistrationURL.substring(0,selfRegistrationURL.indexOf("&okurl"));

        changeProfileURL = resBundle.getString("changeProfileURL");
        changeProfileURL = changeProfileURL.substring(0,changeProfileURL.indexOf("&okurl"));

//        changePasswordURL = resBundle.getString("changePasswordURL");
//        changePasswordURL = changePasswordURL.substring(0,changePasswordURL.indexOf("&okurl"));
//
//        forgotPasswordURL = resBundle.getString("forgotPasswordURL");
//        forgotPasswordURL = forgotPasswordURL.substring(0,forgotPasswordURL.indexOf("&okurl"));
//
//        forgotIdURL = resBundle.getString("forgotIdURL");
//        forgotIdURL = forgotIdURL.substring(0,forgotIdURL.indexOf("&okurl"));

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
    public String wkspcPmEmail(){
        StringBuffer sBuff = new StringBuffer();
        sBuff.append("<table>");
        sBuff.append("<tr><td>");
        sBuff.append("Thank you for your interest in E&TS Connect. <br /><br />");
        sBuff.append("Please help us to identify the workspace you’ll need to access.<br /><br />");
        sBuff.append("<ul><li>Provide the project or proposal name, to the best of your knowledge.</li>");
        sBuff.append("<li>Provide the e-mail address of your IBM contact, preferably a principal or project manager, for the project or proposal.</li></ul><br />");
        sBuff.append("You will be notified as soon as your request has been processed.");
        sBuff.append("</td></tr>");
        sBuff.append("<tr><td>&nbsp;</td></tr>");

        if (!this.getErrMsg().equals("")){
            sBuff.append("<tr><td>");                 // an error message
            sBuff.append("<span style=\"color:#ff3333;\">"+this.getErrMsg()+"</span>");
            sBuff.append("</td></tr>");
            sBuff.append("<tr><td>&nbsp;</td></tr>");

        }

        sBuff.append("<tr><td>");
        // wkspc, pmemail id boxes
        sBuff.append("<table>");
        sBuff.append("<tr><td>"+printLabel(true,true,"","Project or proposal")+"<td><td>"+this.printTextField(false,false,"wkspc","",20,32)+"</td><td></td></tr>");
        sBuff.append("<tr><td>"+printLabel(true,true,"","e-mail address of IBM contact")+"<td><td>"+this.printTextField(false,false,"pmemail","",20,80)+"</td><td>"+this.printHyperLink("Look up e-mail address for IBM contact","http://www.ibm.com/contact/employees/us/")+"</td></tr>");
        sBuff.append("</table>");
        sBuff.append("</td></tr>");

        sBuff.append("<tr><td>&nbsp;</td></tr>");
        sBuff.append("<tr><td>"+printLabel(false,true,"","Additional comments")+"</td></tr>");
        sBuff.append("<tr><td>"+this.printTextField(true,false,"comments","",60,5)+"</td></tr>");
        sBuff.append("<tr><td>&nbsp;</td></tr>");
        sBuff.append("<tr><td>"+printLabel(true,true,"","")+"Required fields are indicated with an asterisk(*).</td></tr>");
        sBuff.append("<tr><td>&nbsp;</td></tr>");
        // sumbit
        sBuff.append("<tr><td><table width=\"250\"><tr><td><input type=\"image\"  src=\""+ Defines.BUTTON_ROOT+ "submit.gif\" border=\"0\" alt=\"Submit\" width=\"120\" height=\"21\" /></td><td><table><tr><td><a href=\""+Defines.SERVLET_PATH+"ETSConnectServlet.wss?linkid=251000\"><img src=\""+ Defines.BUTTON_ROOT+ "cancel_rd.gif\" border=\"0\" alt=\"Cancel\" width=\"21\" height=\"21\" /></a></td><td><a class=\"fbox\" href=\""+Defines.SERVLET_PATH+"ETSConnectServlet.wss?linkid=251000\">Cancel</a></td></tr></table></td></tr></table></td></tr>");

        sBuff.append("</table>");
        sBuff.append("<input type=\"hidden\" name=\"ibmid\" value=\""+this.etsParms.getRequest().getParameter("ibmid")+"\" />");
        sBuff.append("<input type=\"hidden\" name=\"option\" value=\"chkwkspc\" />");
        return sBuff.toString();

    }

    // 3. internal user roles
     public String intUserRole(){
        StringBuffer sBuff = new StringBuffer();
        sBuff.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"2\">");
        sBuff.append("<tr><td colspan=\"3\">Please help us to identify your role within E&TS. Check the appropriate box below.</td></tr>");
        sBuff.append("<tr><td colspan=\"3\">&nbsp;</td></tr>");
        sBuff.append("<tr><td valign=\"top\">"+printRadioButton("role","etsclient",true)+"</td><td valign=\"top\">&nbsp;"+printLabel(false,true,"","E&TS CLIENTS")+"</td><td>I am a client of IBM E&TS who would like to gain 24/7, online access to proposal or project information through E&TS Connect — a comprehensive suite of tools for IBM E&TS and E&TS clients that enables us to collaborate as on demand partners.</td></tr>");
        sBuff.append("<tr><td valign=\"top\">"+printRadioButton("role","principals",false)+"</td><td valign=\"top\">&nbsp;"+printLabel(false,true,"","PRACTICE LEADERS/PRINCIPALS")+"</td><td>I am an IBM practice leader or principal, working on a new business development or project proposal. E&TS Connect provides a secure workspace so that I can share technical presentations, proposals, statements of work or contract developments with my clients.</td></tr>");
        sBuff.append("<tr><td valign=\"top\">"+printRadioButton("role","etspm",false)+"</td><td valign=\"top\">&nbsp;"+printLabel(false,true,"","PROJECT MANAGERS")+"</td><td>I am an IBM project manager, responsible for project delivery to my clients. E&TS Connect provides a secure, on demand and collaborative workspace to share project status, issues and documents with my client team. </td></tr>");
        sBuff.append("<tr><td valign=\"top\">"+printRadioButton("role","execs",false)+"</td><td valign=\"top\">&nbsp;"+printLabel(false,true,"","E&TS EXECUTIVES")+"</td><td>I am an E&TS executive or senior-management team member. With E&TS Connect, I can access client-satisfaction data, overall account status and client-project issues.</td></tr>");
        sBuff.append("<tr><td valign=\"top\">"+printRadioButton("role","ccadvocate",false)+"</td><td valign=\"top\">&nbsp;"+printLabel(false,true,"","CLIENT CARE ADVOCATES")+"</td><td>I am an IBM client-care advocate. E&TS Connect enables me to access client-satisfaction data and overall account status, so that I can oversee client-project issues.</td></tr>");
        sBuff.append("<tr><td valign=\"top\">"+printRadioButton("role","other",false)+"</td><td valign=\"top\">&nbsp;"+printLabel(false,true,"","OTHER E&TS TEAM MEMBERS")+"</td><td>I am a member of an IBM proposal, contract or project-delivery team. With E&TS Connect, I can access workspaces that enable collaboration using a secure workspace and comprehensive suite of on demand tools.</td></tr>");
        sBuff.append("<tr><td colspan=\"3\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"3\"><table width=\"250\"><tr><td><input type=\"image\"  src=\""+ Defines.BUTTON_ROOT+ "continue.gif\" border=\"0\" alt=\"Continue\" width=\"120\" height=\"21\" /></td><td><table><tr><td><a href=\""+Defines.SERVLET_PATH+"ETSConnectServlet.wss?linkid=251000\"><img src=\""+ Defines.BUTTON_ROOT+ "cancel_rd.gif\" border=\"0\" alt=\"Cancel\" width=\"21\" height=\"21\" /></a></td><td><a class=\"fbox\" href=\""+Defines.SERVLET_PATH+"ETSConnectServlet.wss?linkid=251000\">Cancel</a></td></tr></table></td></tr></table></td></tr>");
        sBuff.append("</table>");
        sBuff.append("<input type=\"hidden\" name=\"ibmid\" value=\""+this.etsParms.getRequest().getParameter("ibmid")+"\" />");
        sBuff.append("<input type=\"hidden\" name=\"option\" value=\"procroles\" />");
        return sBuff.toString();
    }

    // no profile page for external users
    public String noProfilePage(){
        StringBuffer sBuff = new StringBuffer();

        sBuff.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"2\">");
        sBuff.append("<tr><td>Sorry, your profile information is incomplete. Please fill in the required company, street address and phone information.</td></tr>");
        sBuff.append("<tr><td>&nbsp;</td></tr>");
        sBuff.append("<tr><td>"+printHyperLink("<img src=\""+Defines.BUTTON_ROOT+"arrow_rd.gif\" alt=\"update your profile\" border=\"0\" height=\"21\" width=\"21\" />",changeProfileURL+"&okurl="+webRoot+Defines.SERVLET_PATH+"ETSUserAccessServlet.wss?option=updprofile"));
        sBuff.append(printHyperLink("Update your profile",changeProfileURL+"&okurl="+webRoot+Defines.SERVLET_PATH+"ETSUserAccessServlet.wss?option=updprofile")+"</td></tr>");
        sBuff.append("<tr><td>&nbsp;</td></tr>");
        sBuff.append("</table>");
        sBuff.append("<input type=\"hidden\" name=\"ibmid\" value=\""+this.etsParms.getRequest().getParameter("ibmid")+"\" />");
        sBuff.append("<input type=\"hidden\" name=\"option\" value=\"updprofile\" />");
        return sBuff.toString();
    }
     public String flaggedEmail(){
	        StringBuffer sBuff = new StringBuffer();

	        sBuff.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"2\">");
	        sBuff.append("<tr><td>Your ID is identified as an external ID even though the e-mail address is internal IBM.</td></tr>");
            sBuff.append("<tr><td>Please contact the <a href=\"/oem/edge/cchelp.html\" onclick=\"window.open('/oem/edge/cchelp.html','CustomerConnectHelp','width=400,height=450'); return false;\" onkeypress=\"window.open('/oem/edge/cchelp.html','CustomerConnectHelp','width=400,height=450'); return false;\" target=\"new\" class=\"fbox\">help desk</a> to correct this before requesting access to E&TS Connect.</td></tr>");
	        sBuff.append("<tr><td>&nbsp;</td></tr>");
	        sBuff.append("<tr><td>"+printHyperLink("<img src=\""+Defines.BUTTON_ROOT+"arrow_rd.gif\" alt=\"Back to E&amp;TS Connect\" border=\"0\" height=\"21\" width=\"21\" />",webRoot+Defines.SERVLET_PATH+"ETSConnectServlet.wss"));
	        sBuff.append(printHyperLink("Return to E&TS Connect",webRoot+Defines.SERVLET_PATH+"ETSConnectServlet.wss")+"</td></tr>");
	        sBuff.append("<tr><td>&nbsp;</td></tr>");
	        sBuff.append("</table>");
	        sBuff.append("<input type=\"hidden\" name=\"ibmid\" value=\""+this.etsParms.getRequest().getParameter("ibmid")+"\" />");
	        sBuff.append("<input type=\"hidden\" name=\"option\" value=\"updprofile\" />");
	        return sBuff.toString();
	}


    public String reqSuccess(){
        StringBuffer sBuff = new StringBuffer();
        sBuff.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"2\">");
        sBuff.append("<tr><td>"+this.getErrMsg()+"</td></tr>");
        sBuff.append("<tr><td>&nbsp;</td></tr>");
        sBuff.append("<tr><td><a href=\""+Defines.SERVLET_PATH+"ETSConnectServlet.wss?linkid=251000\"><img src=\""+Defines.BUTTON_ROOT+"arrow_lt.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"back\" /></a> "+printHyperLink("Back to E&TS Home",""+Defines.SERVLET_PATH+"ETSConnectServlet.wss?linkid=251000")+"</td></tr>");
        sBuff.append("</table>");
        return sBuff.toString();
    }

    public String reqSuccess2(){
        StringBuffer sBuff = new StringBuffer();
        sBuff.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"2\">");
        sBuff.append("<tr><td>"+this.getErrMsg()+"</td></tr>");
        sBuff.append("<tr><td>&nbsp;</td></tr>");
        sBuff.append("<tr><td><a href=\""+Defines.SERVLET_PATH+"ETSConnectServlet.wss?linkid=251000\"><img src=\""+Defines.BUTTON_ROOT+"arrow_lt.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"back\" /></a> "+printHyperLink("Back to E&TS Home",""+Defines.SERVLET_PATH+"ETSConnectServlet.wss?linkid=251000")+"</td></tr>");
        sBuff.append("</table>");
        return sBuff.toString();
    }

    public String etsClient(){
        ETSMetricsFunctions metFuncs = new ETSMetricsFunctions();
        setETSParms(this.etsParms);
        StringBuffer sBuff = new StringBuffer();
        sBuff.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"2\">");
        sBuff.append("<tr><td colspan=\"2\">Select the workspace from the drop-down menu.</td></tr>");
        sBuff.append("<tr><td>"+printLabel(true,true,"","<label for=\"wkspc\">Project or proposal</label>")+"</td><td><select id=\"wkspc\" name=\"wkspc\"><option name=\"optval\" value=\"select\" selected>Select a workspace</option>"+getOptionList(getWorkSpaceList())+"</select></td></tr>");
        sBuff.append("<tr><td colspan=\"2\"><span style=\"color:rgb(255,0,0)\">If it is not on the list use the section below to request for a new workspace be created.</span></td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">"+printLabel(false,true,"","Request for new workspace to be created ")+"</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;&nbsp;&nbsp;"+printLabel(false,false,"","Fill in each of these fields to create a new workspace: ")+"</td></tr>");
        sBuff.append("<tr><td nowrap=\"nowrap\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+printLabel(false,false,"","Companies that will be working on this project")+"</td><td>"+this.printTextField(false,false,"companies","",20,80)+"</td></tr>");
        sBuff.append("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+printLabel(false,false,"","Project name from E&TS New Business database")+"</td><td>"+this.printTextField(false,false,"projname","",20,80)+"</td></tr>");
        sBuff.append("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+printLabel(false,false,"","Project manager&#39;s e-mail address")+"</td><td>"+this.printTextField(false,false,"projemail","",20,80)+"</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">"+printLabel(false,true,"","Additional comments")+"</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">"+this.printTextField(true,false,"comments","",60,5)+"</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\"><table width=\"250\"><tr><td><input type=\"image\"  src=\""+ Defines.BUTTON_ROOT+ "submit.gif\" border=\"0\" alt=\"Submit\" width=\"120\" height=\"21\" /></td><td><table><tr><td><a href=\""+Defines.SERVLET_PATH+"ETSConnectServlet.wss?linkid=251000\"><img src=\""+ Defines.BUTTON_ROOT+ "cancel_rd.gif\" border=\"0\" alt=\"Cancel\" width=\"21\" height=\"21\" /></a></td><td><a class=\"fbox\" href=\""+Defines.SERVLET_PATH+"ETSConnectServlet.wss?linkid=251000\">Cancel</a></td></tr></table></td></tr></table></td></tr>");
        sBuff.append("</table>");
        sBuff.append("<input type=\"hidden\" name=\"ibmid\" value=\""+this.etsParms.getRequest().getParameter("ibmid")+"\" />");
        sBuff.append("<input type=\"hidden\" name=\"option\" value=\"etspm\" />");
        return sBuff.toString();
    }

    public String etsPrincipal(){
        ETSMetricsFunctions metFuncs = new ETSMetricsFunctions();
        setETSParms(this.etsParms);
        StringBuffer sBuff = new StringBuffer();
        sBuff.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"2\">");
        sBuff.append("<tr><td colspan=\"2\">E&TS Connect provides workspaces where you can collaborate on projects and proposals. In the appropriate section below, request access to an existing workspace or create a new workspace. </td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">"+printLabel(false,true,"","Request for an existing workspace")+"</td></tr>");
        sBuff.append("<tr><td>"+printLabel(false,false,"","<label for=\"wkspc22\">Select the workspace from the drop-down menu.</label>")+"</td><td><select id=\"wkspc22\" name=\"wkspc\"><option name=\"optval\" value=\"select\" selected>Select a workspace</option>"+getOptionList(getWorkSpaceList())+"</select></td></tr>");
        sBuff.append("<tr><td colspan=\"2\"><span style=\"color:rgb(255,0,0)\">If it is not on the list use the section below to request for a new workspace be created.</span></td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">"+printLabel(false,true,"","Request for new workspace to be created ")+"</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;&nbsp;&nbsp;"+printLabel(false,false,"","Fill in each of these fields to create a new workspace: ")+"</td></tr>");
        sBuff.append("<tr><td nowrap=\"nowrap\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+printLabel(false,false,"","Companies that will be working on this proposal")+"</td><td>"+this.printTextField(false,false,"companies","",20,80)+"</td></tr>");
        sBuff.append("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+printLabel(false,false,"","Proposal name from E&TS New Business database")+"</td><td>"+this.printTextField(false,false,"projname","",20,80)+"</td></tr>");
        sBuff.append("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+printLabel(false,false,"","Principal&#39;s e-mail address")+"</td><td>"+this.printTextField(false,false,"projemail","",20,80)+"</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">"+printLabel(false,true,"","Additional comments")+"</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">"+this.printTextField(true,false,"comments","",60,5)+"</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\"><table width=\"250\"><tr><td><input type=\"image\"  src=\""+ Defines.BUTTON_ROOT+ "submit.gif\" border=\"0\" alt=\"Submit\" width=\"120\" height=\"21\" /></td><td><table><tr><td><a href=\""+Defines.SERVLET_PATH+"ETSConnectServlet.wss?linkid=251000\"><img src=\""+ Defines.BUTTON_ROOT+ "cancel_rd.gif\" border=\"0\" alt=\"Cancel\" width=\"21\" height=\"21\" /></a></td><td><a class=\"fbox\" href=\""+Defines.SERVLET_PATH+"ETSConnectServlet.wss?linkid=251000\">Cancel</a></td></tr></table></td></tr></table></td></tr>");
        sBuff.append("</table>");
        sBuff.append("<input type=\"hidden\" name=\"ibmid\" value=\""+this.etsParms.getRequest().getParameter("ibmid")+"\" />");
        sBuff.append("<input type=\"hidden\" name=\"option\" value=\"principal\" />");
        return sBuff.toString();
    }

    public String etsPM(){
        ETSMetricsFunctions metFuncs = new ETSMetricsFunctions();
        setETSParms(this.etsParms);
        StringBuffer sBuff = new StringBuffer();
        sBuff.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"2\">");
        sBuff.append("<tr><td colspan=\"2\">E&TS Connect provides workspaces where you can collaborate on projects and proposals. In the appropriate section below, request access to an existing workspace or create a new workspace. </td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">"+printLabel(false,true,"","Request for an existing workspace")+"</td></tr>");
        sBuff.append("<tr><td>"+printLabel(false,false,"","<label for=\"wkspc\">Select the workspace from the drop-down menu.</label>")+"</td><td><select id=\"wkspc\" name=\"wkspc\"><option name=\"optval\" value=\"select\" selected>Select a workspace</option>"+getOptionList(getWorkSpaceList())+"</select></td></tr>");
        sBuff.append("<tr><td colspan=\"2\"><span style=\"color:rgb(255,0,0)\">If it is not on the list use the section below to request for a new workspace be created.</span></td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">"+printLabel(false,true,"","Create a new workspace")+"</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;&nbsp;&nbsp;"+printLabel(false,false,"","Fill in each of these fields to create a new workspace: ")+"</td></tr>");
        sBuff.append("<tr><td nowrap=\"nowrap\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+printLabel(false,false,"","Companies that will be working on this project")+"</td><td>"+this.printTextField(false,false,"companies","",20,80)+"</td></tr>");
        sBuff.append("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+printLabel(false,false,"","Project name, according to the project manager")+"</td><td>"+this.printTextField(false,false,"projname","",20,80)+"</td></tr>");
        sBuff.append("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+printLabel(false,false,"","Project manager&#39;s e-mail address")+"</td><td>"+this.printTextField(false,false,"projemail","",20,80)+"</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">"+printLabel(false,true,"","Additional comments")+"</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">"+this.printTextField(true,false,"comments","",60,5)+"</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\"><table width=\"250\"><tr><td><input type=\"image\"  src=\""+ Defines.BUTTON_ROOT+ "submit.gif\" border=\"0\" alt=\"Submit\" width=\"120\" height=\"21\" /></td><td><table><tr><td><a href=\""+Defines.SERVLET_PATH+"ETSConnectServlet.wss?linkid=251000\"><img src=\""+ Defines.BUTTON_ROOT+ "cancel_rd.gif\" border=\"0\" alt=\"Cancel\" width=\"21\" height=\"21\" /></a></td><td><a class=\"fbox\" href=\""+Defines.SERVLET_PATH+"ETSConnectServlet.wss?linkid=251000\">Cancel</a></td></tr></table></td></tr></table></td></tr>");
        sBuff.append("</table>");
        sBuff.append("<input type=\"hidden\" name=\"ibmid\" value=\""+this.etsParms.getRequest().getParameter("ibmid")+"\" />");
        sBuff.append("<input type=\"hidden\" name=\"option\" value=\"etspm\" />");
        return sBuff.toString();
    }

    public String ccAdvocate(){
        ETSMetricsFunctions metFuncs = new ETSMetricsFunctions();
        setETSParms(this.etsParms);
        StringBuffer sBuff = new StringBuffer();
        sBuff.append("<table border=\"0\" cellspacing=\"1\" cellpadding=\"2\">");
        sBuff.append("<tr><td colspan=\"2\">E&TS Connect provides workspaces where you can collaborate on projects and proposals. In the appropriate section below, request access to an existing workspace or create a new workspace. </td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">"+printLabel(false,true,"","Request for an existing workspace")+"</td></tr>");
        sBuff.append("<tr><td>"+printLabel(false,false,"","<label for=\"wkspc\">Select the project name from the drop-down menu.</label>")+"</td><td><select id=\"wkspc\" name=\"wkspc\"><option name=\"optval\" value=\"select\" selected>Select a workspace</option>"+getOptionList(getWorkSpaceList())+"</select></td></tr>");
        sBuff.append("<tr><td colspan=\"2\"><span style=\"color:rgb(255,0,0)\">If it is not on the list use the section below to request for a new workspace be created.</span></td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">"+printLabel(false,true,"","Create a new workspace")+"</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;&nbsp;&nbsp;"+printLabel(false,false,"","Fill in each of these fields to create a new workspace: ")+"</td></tr>");
        sBuff.append("<tr><td nowrap=\"nowrap\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+printLabel(false,false,"","Companies that will be working on this project")+"</td><td>"+this.printTextField(false,false,"companies","",20,80)+"</td></tr>");
        sBuff.append("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+printLabel(false,false,"","Project name, according to the project manager")+"</td><td>"+this.printTextField(false,false,"projname","",20,80)+"</td></tr>");
        sBuff.append("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+printLabel(false,false,"","Project manager&#39;s e-mail address")+"</td><td>"+this.printTextField(false,false,"projemail","",20,80)+"</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">"+printLabel(false,true,"","Additional comments")+"</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">"+this.printTextField(true,false,"comments","",60,5)+"</td></tr>");
        sBuff.append("<tr><td colspan=\"2\">&nbsp;</td></tr>");
        sBuff.append("<tr><td colspan=\"2\"><table width=\"250\"><tr><td><input type=\"image\"  src=\""+ Defines.BUTTON_ROOT+ "submit.gif\" border=\"0\" alt=\"Submit\" width=\"120\" height=\"21\" /></td><td><table><tr><td><a href=\""+Defines.SERVLET_PATH+"ETSConnectServlet.wss?linkid=251000\"><img src=\""+ Defines.BUTTON_ROOT+ "cancel_rd.gif\" border=\"0\" alt=\"Cancel\" width=\"21\" height=\"21\" /></a></td><td><a class=\"fbox\" href=\""+Defines.SERVLET_PATH+"ETSConnectServlet.wss?linkid=251000\">Cancel</a></td></tr></table></td></tr></table></td></tr>");
        sBuff.append("</table>");
        sBuff.append("<input type=\"hidden\" name=\"ibmid\" value=\""+this.etsParms.getRequest().getParameter("ibmid")+"\" />");
        sBuff.append("<input type=\"hidden\" name=\"option\" value=\"ccadvocate\" />");
        return sBuff.toString();
    }


    public String pendValidation(){
        StringBuffer sBuff = new StringBuffer();
        sBuff.append("<table>");
        sBuff.append("<tr><td>Welcome. You should have received an e-mail with your IBM validation code number. Until you register that code  you may not request access to restricted  information  adn functionality that is available through the Microelectronics Web site.</td></tr>");
        sBuff.append("<tr><td>&nbsp;</td></tr>");
        sBuff.append("<tr><td>Please select one of the following choices and then click Continue when you are done.</td></tr>");
        sBuff.append("<tr><td>&nbsp;</td></tr>");
        sBuff.append("<tr><td>"+this.getErrMsg()+"</td></tr>");
        sBuff.append("<tr><td>"+printRadioButton("valoption","code",true)+"&nbsp;&nbsp;I will enter validation code now</td></tr>");
        sBuff.append("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+printTextField(false, false,"valcode","",20, 40)+"</td></tr>");
        sBuff.append("<tr><td>&nbsp;</td></tr>");
        sBuff.append("<tr><td>"+printRadioButton("valoption","email",false)+"&nbsp;&nbsp;Please re-send my validation code to my email address.</td></tr>");
        ETSUserDetails userDetails = new ETSUserDetails();
        userDetails.setWebId(etsParms.getEdgeAccessCntrl().gIR_USERN);
        userDetails.extractUserDetails(etsParms.getConnection());
        sBuff.append("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+userDetails.getEMail()+"</td></tr>");
        sBuff.append("<tr><td>&nbsp;</td></tr>");
        sBuff.append("<tr><td><input type=\"image\"  src=\""+ Defines.BUTTON_ROOT+ "continue.gif\" border=\"0\" alt=\"continue\" width=\"120\" height=\"21\" /></td></tr>");
        sBuff.append("<tr><td>&nbsp;</td></tr>");
        sBuff.append("<tr><td>For more information, please contact the MD Help Desk</td></tr>");
        sBuff.append("<tr><td>1-888-220-3343</td></tr>");
        sBuff.append("<tr><td>email:<a class=\"fbox\" href=\"mailto:econnect@us.ibm.com?subject=Validiation question\">MD Help Desk</a></td></tr>");
        sBuff.append("<input type=\"hidden\" name=\"option\" value=\"validation\" />");
        sBuff.append("</table>");
        return sBuff.toString();
    }

    public String valEmailOk(){
        StringBuffer sBuff = new StringBuffer();
        sBuff.append("<table>");
        sBuff.append("<tr><td>Your validation code have been sent to this email address: "+etsParms.getEdgeAccessCntrl().gEMAIL+"</td></tr>");
        sBuff.append("<tr><td>&nbsp;</td></tr>");
        sBuff.append("<td align=\"center\"><table><tr><td>&nbsp;&nbsp;&nbsp;<a href=\""+Defines.SERVLET_PATH+"jsp/GetAccess.jsp?linkid=255000\"><img src=\""+Defines.BUTTON_ROOT+"arrow_rd.gif\" height=\"21\" width=\"21\" border=\"0\" alt=\"Ok\" /></td><td></a><a class=\"fbox\" href=\""+Defines.SERVLET_PATH+"jsp/GetAccess.jsp?linkid=255000\">Ok</a></td></tr></table></td></tr>");
        sBuff.append("</table>");
        return sBuff.toString();
    }
    public String valOk() throws SQLException{
        StringBuffer sBuff = new StringBuffer();
        String sQuery = "select b.serial_num,a.first_name,a.last_name,b.assoc_company,a.dept,a.email_id,a.mgr_first_name,a.mgr_last_name,a.mgr_div,a.mgr_dept,a.mgr_email_id from decaf.users_bluepages_info a,decaf.users b where b.userid='" + etsParms.getEdgeAccessCntrl().gUSERN + "' and b.serial_num = a.serial_num and b.application_id='APPLN10001' and user_type='T'";
       System.out.println("Query"+sQuery);
        String sUserSerialNo = "";
        String sUserFirstName = "";
        String sUserLastName = "";
        String sUserDiv = "";
        String sUserDept = "";
        String sUserEmailId = "";
        String sMgrFirstName = "";
        String sMgrLastName = "";
        String sMgrDiv = "";
        String sMgrDept = "";
        String sMgrEmailId = "";
        boolean hasNoRecord=true;
              Statement stmt = etsParms.getConnection().createStatement();
              ResultSet rs = stmt.executeQuery(sQuery);

              while (rs.next()) {
                sUserSerialNo = rs.getString(1);
                sUserFirstName = rs.getString(2);
                sUserLastName = rs.getString(3);
                sUserDiv = rs.getString(4);
                sUserDept = rs.getString(5);
                sUserEmailId = rs.getString(6);
                sMgrFirstName = rs.getString(7);
                sMgrLastName = rs.getString(8);
                sMgrDiv = rs.getString(9);
                sMgrDept = rs.getString(10);
                sMgrEmailId = rs.getString(11);
                //check for any null values
                if (sUserFirstName == null) sUserFirstName = "";
                if (sUserLastName == null) sUserLastName = "";
                if (sUserDiv == null) {sUserDiv = "";} else {sUserDiv = sUserDiv.trim();}
                if (sUserDept == null) sUserDept = "";
                if (sUserEmailId == null) sUserEmailId = "";
                if (sMgrFirstName == null) sMgrFirstName = "";
                if (sMgrLastName == null) sMgrLastName = "";
                if (sMgrDiv == null) sMgrDiv = "";
                if (sMgrDept == null) sMgrDept = "";
                if (sMgrEmailId == null) sMgrEmailId = "";
                hasNoRecord = false;
              } //end of while

              if (hasNoRecord) {
//                //TODO work on this
//                sBuff.append("<br /><center><b>");
//                sBuff.append("No infomation is available.");
//                String lastone = "update decaf.users set user_type='E' where userid='" + etsParms.getEdgeAccessCntrl().gUSERN + "'";
//                EntitledStatic.fireUpdate(etsParms.getConnection(),lastone);
//                String mpocid = "MPOC1000";
//                String updatestr = "update decaf.users set poc_id='" + mpocid + "', poc_mail= (select email_id from decaf.users where decaf_id= '" + mpocid + "'),poc_name=(select first_name from decaf.users where decaf_id= '" + mpocid + "') where userid='" + sUserId.trim() + "'";
//                EntitledStatic.fireUpdate(etsParms.getConnection(),lastone);
//                sBuff.append("</b></center>");
              }
              else {
                sBuff.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"3\">");
                sBuff.append("<tr><td headers=\"\"  align=\"left\">If the Information below is correct, click on continue to validate yourself as Internal User and continue with the request process. If you belive that any information is incorrect, click cancel and please talk to your manager identified in the directory record.</td></tr>");
                sBuff.append("<tr><td>&nbsp;</td></tr>");
                //sBuff.append("<tr><td headers=\"\"  align=\"left\">"+printLabel(true,false,"","")+"Select a valid Division if the division shown below is not correct.</td></tr>");
                sBuff.append("</table>");

                //start displaying User directory record
                sBuff.append("<table summary=\"\" border=\"0\" cellspacing=\"1\" cepadding=\"5\">");
                sBuff.append("<tr class=\"tblue\"><td headers=\"\" colspan=\"2\"><b>Directory record:</b></td></tr>");
                sBuff.append("<tr bgcolor=\"#eeeeee\"><td headers=\"\" ><b>Last Name:</b></td><td headers=\"\">"+sUserLastName.trim()+"</td></tr>");
                sBuff.append("<tr bgcolor=\"#eeeeee\"><td headers=\"\" ><b>First Name:</b></td><td headers=\"\" >"+sUserFirstName.trim()+"</td></tr>");
                sBuff.append("<tr bgcolor=\"#eeeeee\"><td headers=\"\" ><b>Serial No:</b></td><td headers=\"\" >"+sUserSerialNo.trim()+"</td></tr>");
                sBuff.append("<tr bgcolor=\"#eeeeee\"><td headers=\"\" ><b>Email Address:</b></td><td headers=\"\" >"+sUserEmailId.trim()+"</td></tr>");

//                sBuff.append("<tr bgcolor=\"#eeeeee\"><td headers=\"\" ><label for=\"div\">"+printLabel(true,true,":","Division")+"</label></td><td headers=\"\" >");
//                sBuff.append("<select id=\"div\" name=\"division\">");
//                // User Division
//                String divrqryStr = " Select parms,values from decaf.decaf_key_table where system_id='TG_MEMBER'";
//                Statement divstmt = etsParms.getConnection().createStatement();
//                ResultSet divrset = divstmt.executeQuery(divrqryStr);
//                String sel="";
//                try {
//                    while (divrset.next()) {
//                        String valDiv = divrset.getString(1);
//                        String descDiv = divrset.getString(2);
//                        if (valDiv==null) valDiv="";
//                        else valDiv=valDiv.trim();
//                        if (valDiv.equals(sUserDiv)) sel=" selected ";
//                        else sel="";
//                        sBuff.append("<option value=\"" + valDiv+ "\" "+sel+">" + valDiv+ "</option>");
//                    }
//                }finally {
//                    if (divrset!= null) divrset.close();
//                    if (divstmt!= null) divstmt.close();
//                }
//                sBuff.append("</select></td></tr>");
                sBuff.append("<tr bgcolor=\"#eeeeee\"><td headers=\"\" ><b>Division:</b></td><td headers=\"\" >"+sUserDiv.trim()+"</td></tr>");
                sBuff.append("<tr bgcolor=\"#eeeeee\"><td headers=\"\" ><b>Department:</b></td><td headers=\"\" >"+sUserDept.trim()+"</td></tr>");
                sBuff.append("<tr bgcolor=\"#eeeeee\"><td headers=\"\" ><b>Manager:</b></td><td headers=\"\" >"+sMgrLastName.trim() + "&nbsp;" + sMgrFirstName.trim()+"</td></tr>");
                sBuff.append("<tr bgcolor=\"#eeeeee\"><td headers=\"\" ><b>Manager Email:</b></td><td headers=\"\" >"+sMgrEmailId.trim()+"</td></tr>");
                sBuff.append("<tr bgcolor=\"#eeeeee\"><td headers=\"\" ><b>Manager Department:</b></td><td headers=\"\" >"+sMgrDept.trim()+"</td></tr>");
                sBuff.append("</table>");

                //display form attributes
                sBuff.append("<input type=\"hidden\" name=\"serialno\" value=\"" + sUserSerialNo.trim() + "\" />");
                sBuff.append("<input type=\"hidden\" name=\"option\" value=\"checkdiv\" />");

                sBuff.append("<br />");
                sBuff.append("<table summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" >");
                sBuff.append("<tr>");
                sBuff.append("<tr><td><input type=\"image\" name=\"reps\" src=\""+ Defines.BUTTON_ROOT+ "continue.gif\"  border=\"0\" width=\"120\" height=\"21\" alt=\"continue\" /></td>");
                sBuff.append("<td><table><tr><td>&nbsp;&nbsp;&nbsp;<a href=\""+Defines.SERVLET_PATH+"ETSConnectServlet.wss?linkid=255000\"><img src=\""+Defines.BUTTON_ROOT+"cancel_rd.gif\" height=\"21\" width=\"21\" border=\"0\" alt=\"Cancel\" /></td><td></a><a class=\"fbox\" href=\""+Defines.SERVLET_PATH+"ETSConnectServlet.wss?linkid=255000\">Cancel</a></td></tr></table></td></tr>");

                sBuff.append("</tr>");
                sBuff.append("</table>");

                sBuff.append("</form>");
              } //end of if of user first name check

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
            return ("<label for=\"hello\"></label><textarea id=\"hello\" name=\""+name+"\" value=\""+value+"\" cols=\""+length+"\" rows=\""+maxlength+"\"></textarea>");
        } else {
            return ("<label for=\"hello\"></label><input type=\""+(isPassword?"password":"text")+"\" id=\"hello\" name=\""+name+"\" value=\""+value+"\" length=\""+length+"\" maxlength=\""+maxlength+"\" />");
        }
    }

    private String printRadioButton(String groupName, String value, boolean checked){
        return ("<label for=\"hello\"></label><input type=\"radio\" id=\"hello\" name=\""+groupName+"\" value=\""+value+"\" "+((checked)?"checked=\"checked\"":"")+" />");
    }
    private String printCheckBox(){return "";}
    private String printHyperLink(String urlText, String urlLink){
        return ("<a class=\"fbox\" href=\""+urlLink+"\">"+urlText+"</a>");
    }
    private String printPrimaryButton() {
        return "";
    }

    //sandra added from etsmetricsfuncs
	private Vector getWorkSpaceList(){
		return getWorkSpaceList("");
	}
	private Vector getWorkSpaceList(String projectType){
		if (!projectType.equals("")){
			projectType = "and project_or_proposal = '"+projectType+"'";
		}
		String query="select ltrim(rtrim(project_id)) as project_id, project_name from ets.ets_projects where project_status not in ('A','D') and project_or_proposal not in ('M') "+projectType+" order by 2";
		Statement stmt = null;
		ResultSet rs = null;
		Vector retVect = new Vector();

		try {
			stmt = this.etsParms.getConnection().createStatement();
			rs = stmt.executeQuery(query);
			Vector tVect = null;
			while (rs.next()){
				tVect = new Vector();
				tVect.add(0,rs.getString("project_id").trim());
				tVect.add(1,rs.getString("project_name").trim());
				retVect.add(tVect);
			}
		} catch (SQLException sqlEx){
			try { rs.close(); stmt.close(); } catch (SQLException sqlEx2){}
		}
		return retVect;
	}

	private String getOptionList(Vector list){
		 StringBuffer sBuff = new StringBuffer();
		 for (int i=0; i < list.size(); i++){
			 Vector tvect = (Vector)list.get(i);
			 sBuff.append("<option name=\"optval\" value=\""+tvect.get(0)+"\">"+tvect.get(1)+"</option>");
		 }
		 return sBuff.toString();
	}
	
    //end of sandra add

    private void getIRProperties(){
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
