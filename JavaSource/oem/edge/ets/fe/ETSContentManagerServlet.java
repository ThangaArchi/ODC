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


package oem.edge.ets.fe;

import oem.edge.amt.*;
import oem.edge.common.*;
import oem.edge.ets.fe.ubp.ETSUserDetails;
import oem.edge.ets.fe.teamgroup.TeamGroupDAO;
import oem.edge.ets.fe.ETSGroup;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;

import java.io.*;
import java.util.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.servlet.*;
import javax.servlet.http.*;
import java.net.URLEncoder;


public class ETSContentManagerServlet extends HttpServlet {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.10.1.41";


   protected ETSDatabaseManager databaseManager;
    private String mailhost;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
	handleRequest(request,response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	handleRequest(request,response);
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	//PrintWriter writer;// = response.getWriter();

	response.setContentType("text/html");
	Connection conn = null;
	String Msg = null;
	EdgeAccessCntrl es = new EdgeAccessCntrl();
	Hashtable params ;
	AmtHeaderFooter header = null;

	try {
	    conn = ETSDBUtils.getConnection();

	    if (!es.GetProfile(response,request,conn)) {
			return;
	    }

	    Hashtable hs = ETSUtils.getServletParameters(request);

	    String action = getParameter(request,"action");
		String project = getParameter(request,"proj");
	    String topcat = getParameter(request,"tc");
	    String current = getParameter(request,"cc");
	    String linkid = getParameter(request,"linkid");

		UnbrandedProperties prop = PropertyFactory.getProperty(conn,project);

		if (linkid == null || linkid.equals("")) {
				hs.put("linkid", prop.getLinkID());
				linkid = prop.getLinkID();
			}

		String op = getParameter(request,"OP");
		String blurbfield = getParameter(request,"BlurbField");

		if (!op.equals("") && !blurbfield.equals("") ){
			boolean internal = false;
			String edge_userid = AccessCntrlFuncs.getEdgeUserId(conn,es.gIR_USERN);
			String decaftype = AccessCntrlFuncs.decafType(edge_userid,conn);
			if (decaftype.equals("I")){
				internal = true;
			}
			printBlurb(op,blurbfield,internal,prop,response);
			return;
		}


	    if (action.equals("")){
			System.out.println("put error with url message here");
	    }
	    else if (project.equals("")){
			System.out.println("put error with project id message here");
	    }
	    else if (current.equals("")){
			System.out.println("put error with current id message here");
	    }
	    else if (action.equals("addcat2")){
			String name = (getParameter(request,"catname")).trim();
			String desc = "";
		    char ibmonly = Defines.ETS_PUBLIC;
			if (getParameter(request,"ibmonly").equals(String.valueOf(Defines.ETS_IBM_ONLY))){
				ibmonly = Defines.ETS_IBM_ONLY;
			}
			else if(getParameter(request,"ibmonly").equals(String.valueOf(Defines.ETS_IBM_CONF))){
				ibmonly = Defines.ETS_IBM_CONF;
			}

			boolean isPrivate = false;

			Vector resUsers = new Vector();
			/*if (getParameter(request,"chusers").equals("yes")){
				isPrivate = true;
				String[] resusers = request.getParameterValues("res_users");
				if (resusers!=null){
					for (int i=0;i<resusers.length;i++){
						resUsers.addElement((String)resusers[i]);
					}
					if (!resUsers.contains(es.gIR_USERN)){
						resUsers.addElement(es.gIR_USERN);
					}
				}
			}
			if (ibmonly !=  Defines.ETS_PUBLIC){
				resUsers = 	getIBMMembers(resUsers,conn);
			}
			*/

			String projId = project;
			int parentId =  (new Integer(current)).intValue();

			if (name.equals("") || name == "" || name == null || name.length() == 0 || name.length() > 128){
			    String msg = "Folder Name must be 1-128 characters long";
				response.sendRedirect("ETSProjectsServlet.wss?action=addcat&proj="+project+"&tc="+topcat+"&cc="+current+"&msg=1&linkid="+linkid);
			}
			else{
			    String[] result = addCat2(projId,parentId,name, desc, ibmonly,isPrivate,resUsers,conn, es);
				String res = (String)result[0];
				String res_msg = (String)result[1];
				if(res.equals("1")){
					response.sendRedirect("ETSProjectsServlet.wss?action=addcat&proj="+project+"&tc="+topcat+"&cc="+current+"&msg=2&linkid="+linkid);
				}
				else{
					response.sendRedirect("ETSProjectsServlet.wss?proj="+project+"&tc="+topcat+"&cc="+current+"&linkid="+linkid);

				}
			}
	    }
	    else if (action.equals("delcat2")){
			if (topcat.equals("")){
			    //writer.println("invalid top category id for this user.");
			    //System.out.println("put error with current topcat id message here");
				//response.sendRedirect(Defines.SERVLET_PATH+"ETSProjectsServlet.wss?action=delcat&proj="+project+"&tc="+topcat+"&cc="+current+"&msg=0&linkid="+linkid);
				response.sendRedirect("ETSProjectsServlet.wss?action=delcat&proj="+project+"&tc="+topcat+"&cc="+current+"&msg=0&linkid="+linkid);
			}
			else{
			    //spn 0312 projid
			    String projId = project;  //(new Integer(project)).intValue();
			    int currentId =  (new Integer(current)).intValue();
			    int topcatid =  (new Integer(topcat)).intValue();
				//String delid = getParameter(request,"delcatid");
				String[] delids = request.getParameterValues("delcatid");

				if (delids==null){
					response.sendRedirect("ETSProjectsServlet.wss?action=delcat&proj="+project+"&tc="+topcat+"&cc="+current+"&msg=4&linkid="+linkid);
					return;
				}

				ETSCat c = ETSDatabaseManager.getCat(currentId,projId);
				Vector v = ETSDocCommon.getValidCatTreeIds(c,es.gIR_USERN,projId,ETSUtils.checkUserRole(es,projId),Defines.DELETE,true);

				for(int i = 0; i < delids.length; i++){
					System.out.println("del["+i+"] = "+delids[i]);

					if(delids[i].equals("")){
						response.sendRedirect("ETSProjectsServlet.wss?action=delcat&proj="+project+"&tc="+topcat+"&cc="+current+"&msg=4&linkid="+linkid);
					}
					int delCatId = (new Integer(delids[i])).intValue();
					if (v.contains(new Integer(delCatId))){
					    String[] result = delCat2(projId,delCatId,currentId,topcatid,conn, es);
						String res = (String)result[0];
						String res_msg = (String)result[1];

					    if(res.equals("1")){
							response.sendRedirect("ETSProjectsServlet.wss?action=delcat&proj="+project+"&tc="+topcat+"&cc="+current+"&msg="+res_msg+"&linkid="+linkid);
						}
					}
				}

				response.sendRedirect("ETSProjectsServlet.wss?proj="+project+"&tc="+topcat+"&cc="+current+"&linkid="+linkid);

			}
	    }
		else if (action.equals("updatecat2")){
			String upid = getParameter(request,"updatecatid");
			String name = (getParameter(request,"catname")).trim();
			String oldibmonly = getParameter(request,"oldibm");
			String desc = "";

			String projId = project;
			int currentId =  (new Integer(current)).intValue();

			String encName = URLEncoder.encode(name);

			if(upid.equals("")){
				response.sendRedirect("ETSProjectsServlet.wss?action=updatecat&proj="+project+"&tc="+topcat+"&cc="+current+"&msg=2&linkid="+linkid);
			}
			if (name.equals("") || name.length()>128){
				response.sendRedirect("ETSProjectsServlet.wss?action=updatecatA&proj="+project+"&tc="+topcat+"&cc="+current+"&msg=1&updatecatid="+upid+"&linkid="+linkid);
			}
			int upCatId = (new Integer(upid)).intValue();

			String sIbmOnly = getParameter(request,"ibmonly");
			char ibmonly = Defines.ETS_PUBLIC;

			if (getParameter(request,"ibmonly").equals(String.valueOf(Defines.ETS_IBM_ONLY))){
				ibmonly = Defines.ETS_IBM_ONLY;
			}
			else if(getParameter(request,"ibmonly").equals(String.valueOf(Defines.ETS_IBM_CONF))){
				ibmonly = Defines.ETS_IBM_CONF;
			}

			String opt = getParameter(request,"propOpt");
			if (opt.equals("")){
				opt="1";
			}

			if ((oldibmonly.equals(String.valueOf(Defines.ETS_IBM_ONLY))) && (ibmonly==Defines.ETS_PUBLIC)){
				//updateCatConf(upid,name,oldibmonly,ibmonly,opt,projId,topcat,current,linkid,conn,es);
				response.sendRedirect("ETSProjectsServlet.wss?action=updatecatc&proj="+project+"&tc="+topcat+"&cc="+current+"&linkid="+linkid+"&updatecatid="+upid+"&name="+encName+"&ibmonly="+ibmonly+"&opt="+opt);
			}
			else{
				boolean ownsAll = true;
				if (oldibmonly.charAt(0) < ibmonly){
					String userRole = ETSUtils.getUserRole(es.gIR_USERN,projId,conn);
					ownsAll = ETSDocCommon.getValidCatSubTree(upCatId,projId,es.gIR_USERN,userRole,Defines.UPDATE,true);
					if (!ownsAll)
						response.sendRedirect("ETSProjectsServlet.wss?action=updatecatc&proj="+project+"&tc="+topcat+"&cc="+current+"&linkid="+linkid+"&updatecatid="+upid+"&name="+encName+"&ibmonly="+ibmonly+"&msg=S&opt="+opt);
				}

				if(ownsAll){
					System.out.println("HEHEHEHE**************************************");
				    String[] result = updateCat2(projId,upCatId,name, desc, ibmonly, opt, conn, es);
					String res = (String)result[0];
					String res_msg = (String)result[1];
			    	if(res.equals("1")){
						response.sendRedirect("ETSProjectsServlet.wss?action=updatecat&proj="+project+"&tc="+topcat+"&cc="+current+"&msg="+res_msg+"&linkid="+linkid);
					}
					else{
						response.sendRedirect("ETSProjectsServlet.wss?proj="+project+"&tc="+topcat+"&cc="+current+"&linkid="+linkid);
					}
				}
			}
	    }
	    else if(action.equals("deldoc2") || action.equals("delprevdoc2")){
			String docidStr = getParameter(request,"docid");
			String action_doc = "deldoc";

			if(action.equals("delprevdoc2")){
				action_doc = "delprevdoc";
			}
			else{
				action_doc = "deldoc";
			}

			if (topcat.equals("")){
			    System.out.println("put error with current topcat id message here");
				response.sendRedirect("ETSProjectsServlet.wss?action="+action_doc+"&proj="+project+"&tc="+topcat+"&cc="+current+"&docid="+docidStr+"&msg=1&linkid="+linkid);
			}
			else if (docidStr.equals("")){
			    System.out.println("put error with current doc id message here");
				response.sendRedirect("ETSProjectsServlet.wss?action="+action_doc+"&proj="+project+"&tc="+topcat+"&cc="+current+"&docid="+docidStr+"&msg=&linkid="+linkid);
			}
			else{
			    //spn 0312 projid
			    String projId = project;  //(new Integer(project)).intValue();
			    int currentId =  (new Integer(current)).intValue();
			    int topcatid =  (new Integer(topcat)).intValue();
			    int docid =  (new Integer(docidStr)).intValue();
			    String delall = getParameter(request,"alldel");
			    String latest_uid = getParameter(request,"luid");
				ETSDoc doc = ETSDatabaseManager.getDocByIdAndProject(docid,projId);
				String userRole = ETSUtils.checkUserRole(es,projId);
				boolean error =false;
				if(doc!=null){
					if (
						(!doc.getUserId().equals(es.gIR_USERN) && !userRole.equals(Defines.WORKSPACE_OWNER) && !userRole.equals(Defines.WORKSPACE_MANAGER) && !userRole.equals(Defines.ETS_ADMIN))
						|| (doc.hasExpired() && userRole.equals(Defines.WORKSPACE_MANAGER))){
						error = true;
						response.sendRedirect("ETSProjectsServlet.wss?action="+action_doc+"&proj="+project+"&tc="+topcat+"&cc="+current+"&docid="+docid+"&msg=8&linkid="+linkid);
					}
				}
				if (!error){
				    String[] result = delDoc2(doc,docid,delall,userRole,projId,currentId,topcatid,action,conn,es);
					//[0] 0=success 1=failed	//[1] message
					String success = result[0];
					String msg = result[1];
					if(success.equals("1")){ //failed
						response.sendRedirect("ETSProjectsServlet.wss?action="+action_doc+"&proj="+project+"&tc="+topcat+"&cc="+current+"&docid="+docid+"&msg="+msg+"&linkid="+linkid);
					}
					else{ //success
						if(action.equals("delprevdoc2")){
							response.sendRedirect("ETSProjectsServlet.wss?action=prev&proj="+project+"&tc="+topcat+"&cc="+current+"&docid="+latest_uid+"&linkid="+linkid);
						}
						else{
							response.sendRedirect("ETSProjectsServlet.wss?proj="+project+"&tc="+topcat+"&cc="+current+"&linkid="+linkid);
						}
					}
				}
			}
	    }
		else if (action.equals("updatedocprop2")){
			String projId = project;  //(new Integer(project)).intValue();
			int parentId =  (new Integer(current)).intValue();

			String docidStr = getParameter(request,"docid");
			String name = (getParameter(request,"docname")).trim();
			String desc = (getParameter(request,"docdesc")).trim();
			String keywords = (getParameter(request,"keywords")).trim();
			String oldibmonly = getParameter(request,"oldibm");
			String sIbmonly = getParameter(request,"ibmonly");
			String sChUsers = getParameter(request, "chusers");
			String[] sResUsers = request.getParameterValues("res_users");

			Vector vResUsers = new Vector();
			if (sResUsers != null){
				for (int u = 0; u <sResUsers.length; u++){
					vResUsers.addElement((String)sResUsers[u]);
				}
			}
			char ibmonly = Defines.ETS_PUBLIC;
			if (!sIbmonly.equals("")){
				if(sIbmonly.trim().charAt(0) == '1'){
					ibmonly = Defines.ETS_IBM_ONLY;
				}
				else if (sIbmonly.trim().charAt(0) == '2'){
					ibmonly = Defines.ETS_IBM_CONF;
				}
			}

			System.out.println("**************="+sChUsers+"=**********");
			if (sChUsers.equals(""))
				sChUsers = "0";
			else
				sChUsers = "1";

			String exDateStr = (getParameter(request,"exdate")).trim();
			String exMonthStr = (getParameter(request,"exmonth")).trim();
			String exDayStr = (getParameter(request,"exday")).trim();
			String exYearStr = (getParameter(request,"exyear")).trim();

			request.getSession(true).setAttribute("ETSDocName",name);
			request.getSession(true).setAttribute("ETSDocDesc",desc);
			request.getSession(true).setAttribute("ETSDocKeywords",keywords);
			request.getSession(true).setAttribute("ETSDocIbmOnly",sIbmonly);
			request.getSession(true).setAttribute("ETSDocExDate",exDateStr);
			request.getSession(true).setAttribute("ETSDocExMonth",exMonthStr);
			request.getSession(true).setAttribute("ETSDocExDay",exDayStr);
			request.getSession(true).setAttribute("ETSDocExYear",exYearStr);
			request.getSession(true).setAttribute("ETSChUsers",sChUsers);
			request.getSession(true).setAttribute("ETSResUsers",vResUsers);


			if (!exDateStr.equals("")){
				int vDate = verifyDate(exMonthStr,exDayStr,exYearStr);
				if (vDate == 1){
					response.sendRedirect("ETSProjectsServlet.wss?action=updatedocprop&proj="+project+"&tc="+topcat+"&cc="+current+"&docid="+docidStr+"&msg=7&linkid="+linkid);
					return;
				}
				else if (vDate == 2){
					response.sendRedirect("ETSProjectsServlet.wss?action=updatedocprop&proj="+project+"&tc="+topcat+"&cc="+current+"&docid="+docidStr+"&msg=8&linkid="+linkid);
					return;
				}
				/*if (!verifyDate(exMonthStr,exDayStr,exYearStr)){
					response.sendRedirect("ETSProjectsServlet.wss?action=updatedocprop&proj="+project+"&tc="+topcat+"&cc="+current+"&docid="+docidStr+"&msg=7&linkid="+linkid);
					return;
				}*/
			}


			if(docidStr.equals("")){  //1
			    //writer.println("error occurred, bad doc id");
				response.sendRedirect("ETSProjectsServlet.wss?action=updatedocprop&proj="+project+"&tc="+topcat+"&cc="+current+"&docid="+docidStr+"&msg=1&linkid="+linkid);
				return;
			}
			else{
			    int docid =  (new Integer(docidStr)).intValue();

			    if (name.equals("") || name == "" || name == null || name.length() == 0 || name.length() > 128){
					//String msg = "Document Name must be 1-128 characters long";  2
					//updateDocProp(projId,parentId,docid,msg,es,response);
					response.sendRedirect("ETSProjectsServlet.wss?action=updatedocprop&proj="+project+"&tc="+topcat+"&cc="+current+"&docid="+docidStr+"&msg=2&linkid="+linkid);
			    }
			    else if (desc.length() > 2000){
				      //String msg = "Document Description must be 1-1024 characters long";
				      //updateDocProp(projId,parentId,docid,msg,es,writer);
					response.sendRedirect("ETSProjectsServlet.wss?action=updatedocprop&proj="+project+"&tc="+topcat+"&cc="+current+"&docid="+docidStr+"&msg=9&linkid="+linkid);
			    }
			    else if (keywords.length() > 500){
				    //String msg = "Keywords must be 1-500 characters long";
					response.sendRedirect("ETSProjectsServlet.wss?action=updatedocprop&proj="+project+"&tc="+topcat+"&cc="+current+"&docid="+docidStr+"&msg=10&linkid="+linkid);
			    }
			    else{
			    	if ((oldibmonly.equals(String.valueOf(Defines.ETS_IBM_ONLY))) && (ibmonly==Defines.ETS_PUBLIC)){
						response.sendRedirect("ETSProjectsServlet.wss?action=updatedocpropc&proj="+project+"&tc="+topcat+"&cc="+current+"&linkid="+linkid+"&docid="+docid);
					}
					else{
						String[] result = updateDocProp2(projId,parentId,docid,name,desc,keywords,ibmonly,exDateStr,exMonthStr,exDayStr,exYearStr,sChUsers,sResUsers,conn, es,response);
						String success = result[0];
						String msg = result[1];
						if(success.equals("1")){ //failed
							response.sendRedirect("ETSProjectsServlet.wss?action=updatedocprop&proj="+project+"&tc="+topcat+"&cc="+current+"&docid="+docid+"&msg="+msg+"&linkid="+linkid);
						}
						else{ //success
							request.getSession(true).removeAttribute("ETSDocName");
							request.getSession(true).removeAttribute("ETSDocDesc");
							request.getSession(true).removeAttribute("ETSDocKeywords");
							request.getSession(true).removeAttribute("ETSDocIbmOnly");
							request.getSession(true).removeAttribute("ETSDocExDate");
							request.getSession(true).removeAttribute("ETSDocExMonth");
							request.getSession(true).removeAttribute("ETSDocExDay");
							request.getSession(true).removeAttribute("ETSDocExYear");
							request.getSession(true).removeAttribute("ETSChUsers");
							request.getSession(true).removeAttribute("ETSResUsers");
							response.sendRedirect("ETSProjectsServlet.wss?action=details&proj="+project+"&tc="+topcat+"&cc="+current+"&docid="+docid+"&linkid="+linkid);
						}
					}
			    }
			}
	    }
	    //spn yes, i realize this isn't content but couldn't get to work in ETSAdminServlet
	    else if (action.equals("editMemberRole")){
			String edit_userid = getParameter(request,"edit_userid");
			//spn 0312 projid
			String projectid = project;  //(new Integer(project)).intValue();
			int currentid = (new Integer(current)).intValue();

			if (edit_userid.equals("")){
			    //writer.println("error occurred, bad user id");
			}
			else{
			    editUserRoles(edit_userid,projectid,currentid,"",es,conn,response);
			}
	    }
	    else if (action.equals("editMemberRole2")){
			String edit_userid = getParameter(request,"edit_userid");
			String roleStr = getParameter(request,"roles");
			String prevRole = getParameter(request,"prevRoleId");
			String job = getParameter(request,"job");
			String prevJob = getParameter(request,"prevJob");
			String selectAction = getParameter(request,"sel_mfy_grp");
			String projectId = getParameter(request,"projectId");
			String userId = getParameter(request,"userId");
			String mID = getParameter(request,"mID").trim();
			String prevMID = getParameter(request,"prevMID").trim();
			String reqUserId = es.gIR_USERN;
		
			boolean roleChanged = false;
			boolean mIDChanged = false; 

			if((! roleStr.equals(prevRole)) || (! prevJob.equals(job))){
				     roleChanged = true;
			}
			
			if (! prevMID.equals(mID)){
				     mIDChanged = true;
			}
	    	
			if (selectAction == null || selectAction.equals("")) {
				selectAction = "";
			}else {
				selectAction = selectAction.trim();
			}

			TeamGroupDAO grpDAO = new TeamGroupDAO();
			grpDAO.prepare();
			Vector allGrpVect = grpDAO.getAllGroupsForEdit(projectId,"","ASC",false,reqUserId);
			grpDAO.cleanup();
			Vector grpVect = grpDAO.getGroupsForUser(projectId,userId,conn);
			boolean delete = false;
			boolean add = false;
			boolean sel_remove = false;
			boolean delAction = false;
			boolean addAction = false;
			String grpModify = "";
			String roleModify = "";
			String mIDModify = "";
			String strAddUser;
			String strLastUserName = es.gIR_USERN;

			if((grpVect.isEmpty()) && (selectAction.equals("delGrp"))){
				int currentid = (new Integer(current)).intValue();
				editUserRoles(edit_userid,projectId,currentid,"Error : No Group found for user "+userId,es,conn,response);
			}else {

			if(selectAction.equals("delGrp")){
			   delAction = true;
			   grpDAO.prepare();
		       for(int i=0; i<grpVect.size(); i++){
		       	 ETSGroup etsGrp = (ETSGroup) grpVect.elementAt(i);
		       	 delete = grpDAO.updateGroupMembersList("","'" + userId + "'",etsGrp);
		       }
			   grpDAO.cleanup();
			}else if(selectAction.startsWith("rem",0)){
				       String grpId = selectAction.substring(3);
						grpDAO.prepare();
						for(int i=0; i<grpVect.size(); i++){
							ETSGroup etsGrp = (ETSGroup) grpVect.elementAt(i);
							if((grpId).equals(etsGrp.group_id))
								sel_remove = grpDAO.updateGroupMembersList("","'" + userId + "'",etsGrp);
						 }
				grpDAO.cleanup();

			}else if(selectAction.startsWith("add",0)){
				        String grpId = selectAction.substring(3);
						grpDAO.prepare();
									for(int i=0; i<allGrpVect.size(); i++){
										ETSGroup etsGrp = (ETSGroup) allGrpVect.elementAt(i);
										if((grpId).equals(etsGrp.group_id)){
										strAddUser ="('"+ etsGrp.getGroupId() + "','"+ userId + "','" + strLastUserName + "',current timestamp)";
										add = grpDAO.updateGroupMembersList(strAddUser,"",etsGrp);
									 }
			 }
			  grpDAO.cleanup();
			}

			if((add) || (sel_remove) || (delete) || (selectAction.equals("leaveGrp"))) {
					grpModify = grpModify + "modified";
			}


			//spn 0312 projid
			String projectid = project;  //(new Integer(project)).intValue();
			int currentid = (new Integer(current)).intValue();

			if (edit_userid.equals("")){
			    //writer.println("error occurred, bad user id");
			}
			else if((! roleChanged) && (! mIDChanged) && (selectAction.equals(""))){
			    editUserRoles(edit_userid,projectid,currentid,"Please Submit after editing role/access level/message ID/modify group action(OR)Please click Cancel to cancel.",es,conn,response);
			}else if((! roleChanged) && (! mIDChanged) && (! selectAction.equals(""))){
				int roleid=0;
				editUserRoles2(edit_userid,projectid,roleid,roleModify,mIDModify,grpModify,es,conn,response);
			}else{
				
				String mIDRes = "";
				String[] res={"0",""}; 
				int roleid = 0;
				boolean mIDUpdate = false;
				
				if(roleChanged){
					roleModify = roleModify + "modified";
					roleid = (new Integer(roleStr)).intValue();
					res= ETSDatabaseManager.updateUserRole(edit_userid,projectid,roleid,job,es.gIR_USERN);
				}
							   
		       if(mIDChanged){
		       	     mIDModify = mIDModify + "modified";
		       	     mIDUpdate = ETSDatabaseManager.updateUserMessengerID(edit_userid,mID);
		       	     if(mIDUpdate == false){
		       	     	mIDRes = "Unknown DB2 problem occured while updating user instant message ID.";
		       	     }
		       }     
		      
		       	if ((res[0].equals("0")) || (mIDUpdate == true)){
					editUserRoles2(edit_userid,projectid,roleid,roleModify,mIDModify,grpModify,es,conn,response);
			    }
			    else{
					editUserRoles(edit_userid,projectid,currentid,"Error:"+res[1]+""+mIDRes,es,conn,response);
			    }

			}
		  }
	  	 }

		else if (action.equals("delmemph")){
			String userid = getParameter(request,"uid");
			boolean success = doDelUserPhoto(userid);
			if (success){
				System.out.println("ETSProjectsServlet.wss?action=memberdetails&uid="+es.gIR_USERN+"&proj="+project+"&tc="+topcat+"&cc="+current+"&linkid="+linkid);
				response.sendRedirect("ETSProjectsServlet.wss?action=memberdetails&uid="+es.gIR_USERN+"&proj="+project+"&tc="+topcat+"&cc="+current+"&linkid="+linkid);
			}
			else{
				//print error message here with back button
			}
		}
		else if (action.equals("movedoc3")){
			String did = getParameter(request,"docid");
			String npid = getParameter(request, "catid");

			String projId = project;

			if (did.equals("") || did == "" || did == null || did.length() == 0){
				String msg = "Invalid url";
				response.sendRedirect("ETSProjectsServlet.wss?proj="+project+"&tc="+topcat+"&cc="+current+"&msg=1&linkid="+linkid);
			}
			if (npid.equals("") || npid == "" || npid == null || npid.length() == 0){
				String msg = "A destination category must be chosen";
				response.sendRedirect("ETSProjectsServlet.wss?action=movedoc&proj="+project+"&tc="+topcat+"&cc="+current+"&msg=1&docid="+did+"&linkid="+linkid);
			}
			else{
				int catid = (new Integer(npid)).intValue();
				int docid = (new Integer(did)).intValue();

				ETSCat cat = ETSDatabaseManager.getCat(catid,project);
				ETSDoc doc = ETSDatabaseManager.getDocByIdAndProject(docid,project);

				String[] result = moveDoc3(projId,current,cat, doc, es);
				String res = (String)result[0];
				String res_msg = (String)result[1];

				if(res.equals("1")){
					response.sendRedirect("ETSProjectsServlet.wss?action=movedoc&proj="+project+"&tc="+topcat+"&cc="+current+"&docid="+docid+"&linkid="+linkid);
				}
				else{
					response.sendRedirect("ETSProjectsServlet.wss?action=moveconf&proj="+project+"&tc="+topcat+"&cc="+npid+"&docid="+docid+"&i="+res_msg+"&linkid="+linkid);

				}
			}
		}
		else if (action.equals("movecat4")){
			String mvid = getParameter(request,"movecatid");
			String mv2id = getParameter(request, "movetocatid");

			String projId = project;

			if (mvid.equals("") || mvid == "" ||mvid == null || mvid.length() == 0){
				String msg = "Invalid url";
				response.sendRedirect("ETSProjectsServlet.wss?proj="+project+"&tc="+topcat+"&cc="+current+"&msg=1&linkid="+linkid);
			}
			if (mv2id.equals("") || mv2id == "" || mv2id == null || mv2id.length() == 0){
				String msg = "A destination category must be chosen";
				response.sendRedirect("ETSProjectsServlet.wss?action=movecat2&proj="+project+"&tc="+topcat+"&cc="+current+"&msg=1&movecatid="+mvid+"&linkid="+linkid);
			}
			else{
				int movecatid = (new Integer(mvid)).intValue();
				int movetocatid = (new Integer(mv2id)).intValue();

				ETSCat movecat = ETSDatabaseManager.getCat(movecatid,project);
				ETSCat movetocat = ETSDatabaseManager.getCat(movetocatid,project);

				String[] result = moveCat4(projId,current,movecat,movetocat, es);
				String res = (String)result[0];
				String res_msg = (String)result[1];

				if(res.equals("1")){
					response.sendRedirect("ETSProjectsServlet.wss?action=movecat2&proj="+project+"&tc="+topcat+"&cc="+current+"&linkid="+linkid);
				}
				else{
					response.sendRedirect("ETSProjectsServlet.wss?action=movecatconf&proj="+project+"&tc="+topcat+"&cc="+movetocatid+"&movecatid="+movecatid+"&i="+res_msg+"&linkid="+linkid);

				}
			}
		}
		else if (action.equals("addcomm2")){
			String projId = project;  //(new Integer(project)).intValue();
			int parentId =  (new Integer(current)).intValue();

			String docidStr = getParameter(request,"docid");
			String currdocidStr = getParameter(request,"currdocid");
			String comment = (getParameter(request,"doccomm")).trim();
			String notifyOptions = getParameter(request,"notifyOption");
			String notifyall = getParameter(request,"notifyall");
			String[] sNotifyUsers = request.getParameterValues("notify");

			String currStr = "";
			if(!currdocidStr.equals("")){
				currStr = "&currdocid="+currdocidStr;
			}
			if(docidStr.equals("")){  //1
				response.sendRedirect("ETSProjectsServlet.wss?action=addcomm&proj="+project+"&tc="+topcat+"&cc="+current+currStr+"&docid="+docidStr+"&msg=1&linkid="+linkid);
			}
			else{
				int docid =  (new Integer(docidStr)).intValue();

				if (comment.equals("") || comment == "" || comment == null || comment.length() == 0 || comment.length() > 32768){
					//String msg = "Document comment must be 1-32K characters long";
					request.getSession(true).setAttribute("ETSDocComment",comment);
					request.getSession(true).setAttribute("ETSNotifyOptions",notifyOptions);
					request.getSession(true).setAttribute("ETSNotifyAll",notifyall);
					request.getSession(true).setAttribute("ETSNotifyUsers",sNotifyUsers);

					response.sendRedirect("ETSProjectsServlet.wss?action=addcomm&proj="+project+"&tc="+topcat+"&cc="+current+"&linkid="+linkid+currStr+"&docid="+docid+"&msg=2");
				}
				else{
					//System.out.println("***************notopts="+notifyOptions);
					String[] result = addDocComment2(projId,docid,currdocidStr,comment,notifyOptions,notifyall,sNotifyUsers,topcat,current,linkid,conn, es,response);
					String success = result[0];
					String msg = result[1];
					if(success.equals("1")){ //failed
						response.sendRedirect("ETSProjectsServlet.wss?action=addcomm&proj="+project+"&tc="+topcat+"&cc="+current+currStr+"&docid="+docid+"&msg="+msg+"&linkid="+linkid);
					}
					else{ //success
						if(currdocidStr.equals("")){
							response.sendRedirect("ETSProjectsServlet.wss?action=details&proj="+project+"&tc="+topcat+"&cc="+current+"&docid="+docid+"&linkid="+linkid);
						}
						else{
							response.sendRedirect("ETSProjectsServlet.wss?action=prevdetails&proj="+project+"&tc="+topcat+"&cc="+current+currStr+"&docid="+docid+"&linkid="+linkid);
						}
					}

				}
			}
		}
	}
	catch (SQLException e) {
	    SysLog.log(SysLog.ERR, this, e);
	    ETSDBUtils.close(conn);
	}
	catch (Exception e) {
	    SysLog.log(SysLog.ERR, this, e);
	}
	finally {
	    ETSDBUtils.close(conn);
	}
    }





   private String[] addCat2(String projectid, int parentid, String name, String desc, char ibmonly, boolean isPrivate, Vector resUsers,Connection conn, EdgeAccessCntrl es){
	try{

	    Vector p = new Vector();
	    if ((ETSUtils.checkUserRole(es,projectid)).equals(Defines.ETS_ADMIN)){
			p = ETSDatabaseManager.getProject(projectid);
	    }
	    else{
		    p = ETSDatabaseManager.getProjects(es.gIR_USERN,projectid);
	    }
	    //Vector breadcrumb = new Vector();

	    if (p.size() <= 0){
		//writer.println("error occured: invalid project id for this user");
		System.out.println("put bad projet id message here");
	    }
	    else{
		ETSProj proj  = (ETSProj)p.elementAt(0);

		ETSCat parent_cat = ETSDatabaseManager.getCat(parentid);
		if (parent_cat != null){

		    //add cat here
		    ETSCat newc = new ETSCat();
		    newc.setName(name);
		    newc.setProjectId(proj.getProjectId());
		    newc.setDescription(desc);
		    newc.setParentId(parent_cat.getId());
		    newc.setUserId(es.gIR_USERN);
		    newc.setOrder(0);
		    newc.setViewType(parent_cat.getViewType());
		    newc.setProjDesc(0);
		    newc.setPrivs("");
		    newc.setIbmOnly(ibmonly);
		    //newc.setCPrivate(isPrivate);
			newc.setCPrivate(false);

		    String[] result = ETSDatabaseManager.addCat(newc);
		    int success = (new Integer(result[0])).intValue();

		    if (success == 0){
		    	/*
				if (resUsers.size()>0){
					ETSDatabaseManager.addCatResUsers(resUsers,	result[1],proj.getProjectId());
				}
				*/

				if (proj.getProjectOrProposal().equals("P")){
			    	Metrics.appLog(conn, es.gIR_USERN,"ETS_Project_Cat_Add");
				}
				else{  //proposal
			    	Metrics.appLog(conn, es.gIR_USERN,"ETS_Proposal_Cat_Add");
				}
		    	return new String[]{"0","success"};

		    }
		    else{
			///writer.println("error occurred");

		    }
		}
	    }
	}
	catch(Exception e) {
	    //writer.println("error occurred");
	    System.out.println("error here");
	}

		return new String[]{"1","error occurred"};
    }






    private String[] delCat2(String projectid, int delcatid, int currentid, int topcatid, Connection conn, EdgeAccessCntrl es){
	try{
	    //Vector p = ETSDatabaseManager.getProjects(es.gIR_USERN,projectid);
		Vector p = new Vector();
		if ((ETSUtils.checkUserRole(es,projectid)).equals(Defines.ETS_ADMIN)){
			p = ETSDatabaseManager.getProject(projectid);
		}
		else{
			p = ETSDatabaseManager.getProjects(es.gIR_USERN,projectid);
		}
	    Vector breadcrumb = new Vector();

	    if (p.size() <= 0){
			//writer.println("error occurred: invalid project id for this user.");

			//System.out.println("put bad projet id message here")	;
			return new String[]{"1","1"};
	    }
	    else{
		ETSProj proj  = (ETSProj)p.elementAt(0);

		ETSCat current_cat = ETSDatabaseManager.getCat(delcatid);
		if (current_cat != null){
		    boolean success = ETSDatabaseManager.delCat(current_cat,es.gIR_USERN);
		    if (success){
				if (proj.getProjectOrProposal().equals("P")){
				    Metrics.appLog(conn, es.gIR_USERN,"ETS_Project_Cat_Delete");
				}
				else{  //proposal
				    Metrics.appLog(conn, es.gIR_USERN,"ETS_Proposal_Cat_Delete");
				}
				return new String[]{"0","success"};
		    }
		    else{
				//writer.println("error occurred while deleting the folder <b>"+current_cat.getName()+"</b>");
				return new String[]{"1","3"};
		    }
		}
		else{
			//writer.println("error occurred: invalid cat id for this user.");
		    System.out.print("put bad current cat id message here");
			return new String[]{"1","2"};
		}
	   }
	}
	catch(Exception e) {
	    //writer.println("error occurred");
	    System.out.println("error here");
	    return new String[]{"1","3"};
	}
    }





   private String[] updateCat2(String projectid, int upcatid, String name, String desc, char ibmonly, String opt, Connection conn, EdgeAccessCntrl es){
	try{
	    //Vector p = ETSDatabaseManager.getProjects(es.gIR_USERN,projectid);
		Vector p = new Vector();
		if ((ETSUtils.checkUserRole(es,projectid)).equals(Defines.ETS_ADMIN)){
			p = ETSDatabaseManager.getProject(projectid);
		}
		else{
			p = ETSDatabaseManager.getProjects(es.gIR_USERN,projectid);
		}

		System.out.println("OPT1="+opt);

	    if (p.size() <= 0){
			//writer.println("error occurred: invalid project id for this user.");
			System.out.println("put bad projet id message here");
			return new String[]{"1","3"};
	    }
	    else{
			ETSProj proj  = (ETSProj)p.elementAt(0);

			ETSCat cat = ETSDatabaseManager.getCat(upcatid);
			if (cat != null){
			    //update cat here
			    if(!(name.equals(""))){
			    	cat.setName(name);
			    }
				//cat.setUserId(es.gIR_USERN);  //to keep same owner
			  	String userRole = ETSUtils.getUserRole(es.gIR_USERN,proj.getProjectId(),conn);
			  	boolean isAdmin = false;
			  	if (userRole.equals(Defines.WORKSPACE_OWNER) || userRole.equals(Defines.WORKSPACE_MANAGER) || userRole.equals(Defines.ETS_ADMIN)){
					isAdmin = true;
			  	}

			    if (ibmonly == 'X'){ //ibm only not an option
			    	cat.setIbmOnly(cat.getIbmOnly());
			    }
			    else{
			    	if(cat.getIbmOnly() == ibmonly){  //ibmonly not changed
			    		opt="1";
			    	}
					else{ //ibmonly changed
						if (ibmonly == Defines.ETS_IBM_ONLY || ibmonly == Defines.ETS_IBM_CONF){
							opt = "3";	  //propogate
							isAdmin = true;  // to get all ids
						}
					}
					cat.setIbmOnly(ibmonly);
			    }

				System.out.println("OPT="+opt+"     admin="+isAdmin);

			    boolean success = ETSDatabaseManager.updateCat(cat,isAdmin,es.gIR_USERN,opt);
			    if (success){
					//writer.println("The folder <b>"+cat.getName()+"</b> has been successfully updated.");
					if (proj.getProjectOrProposal().equals("P")){
					    Metrics.appLog(conn, es.gIR_USERN,"ETS_Project_Cat_Update");
					}
					else{  //proposal
					    Metrics.appLog(conn, es.gIR_USERN,"ETS_Proposal_Cat_Update");
					}
					return new String[]{"0","success"};
			    }
			    else{
					//writer.println("An error occurred while trying to update the folder <b>"+cat.getName()+"</b>");
					return new String[]{"1","4"};
			    }
			}
			else{
				return new String[]{"1","4"};
			}
	    }
	}
	catch(Exception e) {
	    //writer.println("error occurred");
	    System.out.println("error here");
		return new String[]{"1","4"};

	}
    }

/*  ***********************************************************************************************
	//updateCatConf(upid,name,oldibmonly,ibmonly,opt,projId,topcat,current,linkid,conn,es);
	private void updateCatConf2(String upcatid,String name,String oldibmonly,char ibmonly,String opt,String projId,String topcat,String ccat,String linkid, Connection conn, EdgeAccessCntrl es){
	 try{



	 }
	 catch(Exception e) {
		 //writer.println("error occurred");

	 }
	 }
*/


/*
    public String[] addDoc2(String projectid, int parentid, String docidStr, String notify, String action, String meeting_id, String ibmonly, String topcat, String current, String linkid, EdgeAccessCntrl es, Connection conn){

	try{
	    Vector p = ETSDatabaseManager.getProjects(es.gIR_USERN,projectid);
	    int docid = (new Integer(docidStr)).intValue();

	    if (p.size() <= 0){
			//writer.println("error occurred: invalid project id for this user.");
			//System.out.println("put bad projet id message here");
	    }
	    else{
			ETSProj proj  = (ETSProj)p.elementAt(0);
			ETSDoc doc = ETSDatabaseManager.getDocByIdAndProject(docid,proj.getProjectId());
			ETSCat parent_cat;

			if (action.equals("adddoc2")){
			    parent_cat = ETSDatabaseManager.getCat(parentid);
			    if (parent_cat != null){

			    }
			    else{
					//writer.println("error occurred: invalid cat id for this user.");
					System.out.print("put bad parent cat id message here");
					//return;
			    }
			}

			Vector members = new Vector();
			if (!notify.equals("")){
				notify = notify.substring(0,notify.length()-1);
				System.out.println("snotify"+notify);
			    StringTokenizer st = new StringTokenizer(notify, ",");
			    while (st.hasMoreTokens()){
					String uid = st.nextToken();
					members.addElement(uid);
			    }
			}

			//if (notify.equals("all")){
			if (members.size() >0){
			    SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
			    java.util.Date date = new java.util.Date(doc.getUploadDate());
			    String dateStr=df.format(date);

			    AccessCntrlFuncs acf = new AccessCntrlFuncs();
			    //Vector members =ETSDatabaseManager.getProjMembers(proj.getProjectId());

			    String emailids = "";
			    StringBuffer message = new StringBuffer();

			    message.append("\n\n");
			    message.append("A new document was added to the project: \n");
			    message.append(proj.getName()+" \n\n");
			    message.append("The details of the document are as follows: \n\n");
			    message.append("==============================================================\n");

			    message.append("  Name:           " + ETSUtils.formatEmailStr(doc.getName()) + "\n");
			    message.append("  Description:    " + ETSUtils.formatEmailStr(doc.getDescription()) + "\n");
			    message.append("  Keywords:       " + ETSUtils.formatEmailStr(doc.getKeywords()) + " \n");
			    message.append("  Author:         " + ETSUtils.getUsersName(conn, doc.getUserId()) + "\n");
			    message.append("  Date:           " + dateStr + " (mm/dd/yyyy)\n\n");
				if (ibmonly.equals(String.valueOf(ETSDatabaseManager.TRUE_FLAG))){
					message.append("  This document is marked IBM Only\n\n");
				}

			    message.append("To view this document, click on the following  URL:  \n");
			    //SPN url change
			    //String url = Global.getUrl("ets/ETSProjectsServlet.wss")+"?action=details&proj="+doc.getProjectId()+"&tc="+topcat+"&cc="+current+"&docid="+doc.getId();
			    String url = Global.getUrl("ets/ETSProjectsServlet.wss")+"?action=details&proj="+doc.getProjectId()+"&tc="+topcat+"&cc="+current+"&docid="+doc.getId()+"&linkid="+linkid;

			    message.append(url+"\n\n");

			    message.append("==============================================================\n");
			    message.append("Delivered by E&TS Connect.\n");
			    message.append("This is a system generated email. \n");
			    message.append("==============================================================\n\n");

				if (ibmonly.equals(String.valueOf(ETSDatabaseManager.TRUE_FLAG))){
					members = getIBMMembers(members,conn);
				}

			    if (members.size() >0){
					for (int i = 0; i<members.size();i++){
					    //get amt information
					    //ETSUser memb = (ETSUser)members.elementAt(i);
						String memb = (String)members.elementAt(i);
					    try{
							//UserObject uo = acf.getUserObject(conn,memb.getUserId(),true,false);
							//UserObject uo = acf.getUserObject(conn,memb,true,false);
							//emailids = emailids + uo.gEMAIL +",";
							String userEmail = ETSUtils.getUserEmail(conn,memb);
							emailids = emailids + userEmail +",";
					    }
					    catch(AMTException ae){
							//writer.println("amt exception caught. e= "+ae);
				   		}
					}

					String subject = "E&TS Connect - New Document: "+doc.getName();
					subject = ETSUtils.formatEmailSubject(subject);

					String toList = "";
					toList = emailids;
					//System.out.println("toList = "+toList);
					//toList = "sandieps@us.ibm.com";
					boolean bSent = false;

					if (!toList.trim().equals("")) {
					    bSent = ETSUtils.sendEMail(es.gEMAIL,toList,"",Global.mailHost,message.toString(),subject,es.gEMAIL);
					}

		       		if (bSent){
					    ETSDatabaseManager.addEmailLog("Document",String.valueOf(doc.getId()),"Add document",es.gIR_USERN,proj.getProjectId(),subject,toList,"");
					    //writer.println("All team members have been notified of new document.");
					}
					else{
						System.out.println("Error occurred while notifying project members.");
					    //writer.println("Error occurred while notifying project members.");
					}

			    }
			    else{
					//writer.println("There are no project members to notify. <br />");
			    }
			}
			else{
			    if (action.equals("adddoc2")){
					//writer.println("You have chosen not to notify project members of this document. <br />");
			    }
			}

			if (action.equals("adddoc2")){
			    if (proj.getProjectOrProposal().equals("P")){
					Metrics.appLog(conn, es.gIR_USERN,"ETS_Project_Doc_Add");
			    }
			    else{  //proposal
					Metrics.appLog(conn, es.gIR_USERN,"ETS_Proposal_Doc_Add");
			    }
			}
		}
		return new String[]{"0","success"};
	}
	catch(Exception e) {
	    //writer.println("error occurred");
	    System.out.println("error here "+e);
		return new String[]{"1","6"};
	}
    }
*/

/*
	private Vector getIBMMembers(Vector membs, Connection conn){
		Vector new_members = new Vector();

		for (int i = 0; i<membs.size();i++){
			//ETSUser mem= (ETSUser)membs.elementAt(i);
			String mem = (String)membs.elementAt(i);
			//System.out.println("muid="+mem.getUserId());
			try{
				String edge_userid = AccessCntrlFuncs.getEdgeUserId(conn,mem);
				String decaftype = AccessCntrlFuncs.decafType(edge_userid,conn);
				if (decaftype.equals("I")){
					//System.out.println("INTERNAL");
					new_members.addElement(mem);
				}
			}
			catch(AMTException a){
				System.out.println("amt exception in getibmmembers err= "+a);
			}
			catch(SQLException s){
				System.out.println("sql exception in getibmmembers err= "+s);
			}
		}
		return new_members;
	}
*/

     private String[] delDoc2(ETSDoc doc,int docid, String delall, String userRole,String projectid, int currentid, int topcatid, String action, Connection conn, EdgeAccessCntrl es){
	try{
	    //Vector p = ETSDatabaseManager.getProjects(es.gIR_USERN,projectid);
		Vector p = new Vector();
		if (userRole.equals(Defines.ETS_ADMIN)){
			p = ETSDatabaseManager.getProject(projectid);
		}
		else{
			p = ETSDatabaseManager.getProjects(es.gIR_USERN,projectid);
		}

	    if (p.size() <= 0){
			System.out.println("put bad projet id message here");
			return new String[]{"1","7"};
	    }
	    else{
			ETSProj proj  = (ETSProj)p.elementAt(0);
			ETSCat current_cat = ETSDatabaseManager.getCat(currentid);
			//ETSDoc doc = ETSDatabaseManager.getDocByIdAndProject(docid,projectid);
			String docname = doc.getName();
			if (current_cat != null){
			    if (doc != null){

					boolean success = false;
					if (delall.equals("")){
					    success = ETSDatabaseManager.delDoc(doc,es.gIR_USERN);
					}
					else{
					    success = ETSDatabaseManager.delDoc(doc,true,es.gIR_USERN);
					}

					if (success){
					    if (delall.equals("")){
							//writer.println("The document <b>"+docname+"</b> has been successfully deleted.");
							if (proj.getProjectOrProposal().equals("P")){
							    Metrics.appLog(conn, es.gIR_USERN,"ETS_Project_Doc_Delete");
							}
							else{  //proposal
							    Metrics.appLog(conn, es.gIR_USERN,"ETS_Proposal_Doc_Delete");
							}
							return new String[]{"0","success"};
				  		}
					    else{
							//writer.println("The document <b>"+docname+"</b> and all its previous versions have been successfully deleted.");
							if (proj.getProjectOrProposal().equals("P")){
							    Metrics.appLog(conn, es.gIR_USERN,"ETS_Project_Doc_Delete_All");
							}
							else{  //proposal
							    Metrics.appLog(conn, es.gIR_USERN,"ETS_Proposal_Doc_Delete_All");
							}
							return new String[]{"0","success"};
					    }
					}
					else{
						//writer.println("An error occurred while deleting the document <b>"+docname+"</b>");
						return new String[]{"1","3"};
					}
		    	}
		    	else{
					//writer.println("error occurred: document not found.");
					return new String[]{"1","4"};
		    	}
			}
			else{
			    //writer.println("error occurred: invalid cat id for this user.");
			    System.out.print("put bad current cat id message here");
				return new String[]{"1","5"};
			}
	    }
	}
	catch(Exception e) {
	    System.out.println("error here");
		return new String[]{"1","6"};
	}
    }





    private String[] updateDocProp2(String projectid, int catid, int docid,String name, String desc, String keywords,char ibmonly,String exDateStr,String exMonthStr,String exDayStr,String exYearStr,String chUsers, String[] resUsers, Connection conn, EdgeAccessCntrl es, HttpServletResponse response){
	try{
	    //Vector p = ETSDatabaseManager.getProjects(es.gIR_USERN,projectid);
		Vector p = new Vector();
		if ((ETSUtils.checkUserRole(es,projectid)).equals(Defines.ETS_ADMIN)){
			p = ETSDatabaseManager.getProject(projectid);
		}
		else{
			p = ETSDatabaseManager.getProjects(es.gIR_USERN,projectid);
		}

	    if (p.size() <= 0){
			//writer.println("error occurred: invalid project id for this user.");
			System.out.println("put bad projet id message here");
			return new String[]{"1","3"};
	    }
	    else{
			ETSProj proj  = (ETSProj)p.elementAt(0);
			Vector vResUsers = new Vector();

			ETSCat cat = ETSDatabaseManager.getCat(catid);
			if (cat != null){
				if ((!chUsers.equals("0")) && (ibmonly != Defines.ETS_PUBLIC)){
					vResUsers =	getIBMMembers(resUsers,conn);
				}
				else if (!chUsers.equals("0")){
					vResUsers =	getVector(resUsers);
				}

				if((!chUsers.equals("0")) && (!vResUsers.contains(es.gIR_USERN))){
					String userRole = ETSUtils.checkUserRole(es,projectid);
					if(!userRole.equals(Defines.ETS_ADMIN) && !userRole.equals(Defines.ETS_EXECUTIVE))
						vResUsers.addElement(es.gIR_USERN);
				}


			    ETSDoc doc = ETSDatabaseManager.getDocByIdAndProject(docid,proj.getProjectId());
				boolean changeIbm = false;
				if (ibmonly != doc.getIbmOnly()){
						changeIbm = true;
				}
				boolean changeRes = false;
				if (!chUsers.equals(doc.getDPrivate())){
					changeRes = true;
				}

				if((!chUsers.equals("0")) && (!vResUsers.contains(doc.getUserId()))){
					String userRole = ETSUtils.getUserRole(doc.getUserId(),proj.getProjectId(),conn);
					if(!userRole.equals(Defines.ETS_ADMIN) && !userRole.equals(Defines.ETS_EXECUTIVE))
						vResUsers.addElement(doc.getUserId());
				}


			    //doc.setProjectId(projId);
			    //doc.setCatId(parentId);
			    //doc.setUserId(es.gIR_USERN);
			    doc.setName(name);
			    doc.setDescription(desc);
			    doc.setKeywords(keywords);
			    doc.setUpdatedBy(es.gIR_USERN);
			    doc.setIbmOnly(ibmonly);
			    if (!exDateStr.equals("")){
				    doc.setExpiryDate(exMonthStr,exDayStr,exYearStr);
			    }
			    else{
			    	doc.setExpiryDate(new Timestamp(0));
			    }
			    doc.setDPrivate(chUsers);

			    //doc.setPublishDate(publishdate);
			    //doc.setMeetingDate(meetingdate);
			    //doc.setUpdateDate(updatedate);


				//if changeRes == true and chusers == 0 remove all
				//if changeRes == true and chusers == 1 add all
				//if changeRes == false and chusers ==1
					// for all users
					// if in new and not in old == add
					// if not in new and in old == remove
				String add = "";
				String remove = "";
				Vector vAdd = new Vector();

				if(changeRes && chUsers.equals("0")){ //remove all
					Vector resu = ETSDatabaseManager.getRestrictedProjMemberIds(projectid,doc.getId(),false);
					for (int i=0;i<resu.size();i++){
						if (i!=0)
							remove = remove+",'"+resu.elementAt(i)+"'";
						else
							remove = "'"+resu.elementAt(i)+"'";
					}
				}
				else if (changeRes && chUsers.equals("1")){ //add all
					for (int i=0;i<vResUsers.size();i++){
						if (i!=0)
							add = add+",("+doc.getId()+",'"+vResUsers.elementAt(i)+"','"+projectid+"')";
						else
							add = add+"("+doc.getId()+",'"+vResUsers.elementAt(i)+"','"+projectid+"')";
						vAdd.addElement(vResUsers.elementAt(i));
					}
				}
				else if (!changeRes && chUsers.equals("1")){ //add and remove
					Vector allu = ETSDatabaseManager.getProjMembers(projectid);
					Vector resu = ETSDatabaseManager.getRestrictedProjMemberIds(projectid,doc.getId(),false);
					for (int i=0; i<allu.size(); i++){
						String userid = ((ETSUser)allu.elementAt(i)).getUserId();

						if (resu.contains(userid) && !vResUsers.contains(userid)){ //remove
							if (!remove.equals(""))
								remove = remove+",'"+userid+"'";
							else
								remove = "'"+userid+"'";
						}
						else if(!resu.contains(userid) && vResUsers.contains(userid)){ //add
							if (!add.equals(""))
								add = add+",("+doc.getId()+",'"+userid+"','"+projectid+"')";
							else
								add = add+"("+doc.getId()+",'"+userid+"','"+projectid+"')";
							vAdd.addElement(userid);
						}


					}
				}



			    boolean success = ETSDatabaseManager.updateDocProp(doc,changeIbm,changeRes,new Vector(),add,remove,vAdd);
			    if (success){
					//writer.println("The document <b>"+doc.getName()+"</b> has been successfully updated.");
					if (proj.getProjectOrProposal().equals("P")){
					    Metrics.appLog(conn, es.gIR_USERN,"ETS_Project_Doc_Prop_Update");
					}
					else{  //proposal
					    Metrics.appLog(conn, es.gIR_USERN,"ETS_Proposal_Doc_Prop_Update");
					}
					return new String[]{"0","success"};
		    	}
			    else{
					//writer.println("An error occurred while trying to update the properties for document <b>"+doc.getName()+"</b>");
			    	return new String[]{"1","4"};
		    	}
			}
			else{  //invlaid cat id
		    	return new String[]{"1","6"};
			}
	    }
	}
	catch(Exception e) {
	    //writer.println("error occurred");
	    System.out.println("error here");
	    e.printStackTrace();
	    return new String[]{"1","5"};
	}
    }



/*
    public String[] updateDoc2(String projectid, int parentid, String docidStr, String notify, String sIbmOnly, String topcat, String current, String linkid, EdgeAccessCntrl es, HttpServletResponse response, Connection conn){
	String res_1 = "";
	String res_2 = "";

	try{
	    Vector p = ETSDatabaseManager.getProjects(es.gIR_USERN,projectid);
	    int docid = (new Integer(docidStr)).intValue();

	    if (p.size() <= 0){
			//writer.println("error occurred: invalid project id for this user.");
			System.out.println("put bad projet id message here");
			return new String[]{"1",""};
	    }
	    else{
			ETSProj proj  = (ETSProj)p.elementAt(0);
			ETSDoc doc = ETSDatabaseManager.getDocByIdAndProject(docid,proj.getProjectId());

			ETSCat parent_cat = ETSDatabaseManager.getCat(parentid);
			if (parent_cat != null){


				Vector members = new Vector();
				if (!notify.equals("")){
					notify = notify.substring(0,notify.length()-1);
					System.out.println("snotify"+notify);
				    StringTokenizer st = new StringTokenizer(notify, ",");
				    while (st.hasMoreTokens()){
						String uid = st.nextToken();
						members.addElement(uid);
				    }
				}

				//if (notify.equals("all")){
				if (members.size() >0){
					SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
					java.util.Date date = new java.util.Date(doc.getUploadDate());
					String dateStr=df.format(date);

					AccessCntrlFuncs acf = new AccessCntrlFuncs();
					//Vector members = ETSDatabaseManager.getProjMembers(proj.getProjectId());
					String emailids = "";
					StringBuffer message = new StringBuffer();

					message.append("\n\n");

					message.append("A new version of a document has been added to the project: \n"+proj.getName()+". \n\n");
					message.append("The details of the document are as follows: \n\n");

					message.append("==============================================================\n");

					message.append("  Name:           " + ETSUtils.formatEmailStr(doc.getName()) + "\n");
					message.append("  Description:    " + ETSUtils.formatEmailStr(doc.getDescription()) + "\n");
					message.append("  Keywords:       " + ETSUtils.formatEmailStr(doc.getKeywords()) + " \n");
					//message.append("  Author:         " + ETSUtils.formatEmailStr(doc.getUserId()) + "\n");
					message.append("  Author:         " + ETSUtils.formatEmailStr(ETSUtils.getUsersName(conn,doc.getUserId())) + "\n");
					message.append("  Date:           " + dateStr + " (mm/dd/yyyy)\n\n");
					if (sIbmOnly.equals(String.valueOf(ETSDatabaseManager.TRUE_FLAG))){
						message.append("  This document is marked IBM Only\n\n");
					}
					message.append("To view this document, click on the following URL:  \n");
					//SPN url change
					//String url = Global.getUrl("ets/ETSProjectsServlet.wss")+"?action=details&proj="+doc.getProjectId()+"&tc="+topcat+"&cc="+current+"&docid="+doc.getId();
					String url = Global.getUrl("ets/ETSProjectsServlet.wss")+"?action=details&proj="+doc.getProjectId()+"&tc="+topcat+"&cc="+current+"&docid="+doc.getId()+"&linkid="+linkid;
					message.append(url+"\n\n");

					message.append("==============================================================\n");
					message.append("Delivered by E&TS Connect.\n");
					message.append("This is a system generated email. \n");
					message.append("==============================================================\n\n");

					if (sIbmOnly.equals(String.valueOf(ETSDatabaseManager.TRUE_FLAG))){
						members = getIBMMembers(members,conn);
					}


					if (members.size() >0){
						for (int i = 0; i<members.size();i++){
						    //get amt information
						    //ETSUser memb = (ETSUser)members.elementAt(i);
							String memb = (String)members.elementAt(i);
						    try{
								//UserObject uo = acf.getUserObject(conn,memb.getUserId(),true,false);
								//UserObject uo = acf.getUserObject(conn,memb,true,false);
								//emailids = emailids + uo.gEMAIL +",";
								String userEmail = ETSUtils.getUserEmail(conn,memb);
								emailids = emailids + userEmail +",";
						    }
						    catch(AMTException ae){
								//writer.println("amt exception caught. e= "+ae);
					   		}
						}

					    String subject = "E&TS Connect - Document Updated: "+doc.getName();
					    subject = ETSUtils.formatEmailSubject(subject);

					    String toList = "";
					    toList = emailids;
					    //System.out.println("toList= "+toList);
					    //toList = "sandieps@us.ibm.com";
					    boolean bSent = false;

					    if (!toList.trim().equals("")) {
							//sendEMail(String from, String to, String sCC, String host, String sMessage, String Subject, String reply)
							bSent = ETSUtils.sendEMail(es.gEMAIL,toList,"",Global.mailHost,message.toString(),subject,es.gEMAIL);
					    }

					    if (bSent){
							 //addEmailLog(String mail_type, String key1, String key2, String key3, String project_id, String subject, String to, String cc)
							ETSDatabaseManager.addEmailLog("Document",String.valueOf(doc.getId()),"Update document",es.gIR_USERN,proj.getProjectId(),subject,toList,"");
							//writer.println("All team members have been notified of new document.");
							res_1 = "0";
							res_2 = "success";
					    }
					    else{
							//writer.println("Error occurred while notifying project members.");
							res_1 = "0";
							res_2 = "document updated, email failed";
					    }
					}
					else{
					    //writer.println("There are no project members to notify. <br />");
							res_1 = "0";
							res_2 = "no project members to notify";
					}
			    }
			    else{
					//writer.println("You have chosen not to notify project members of the update to this document. <br />");
					res_1 = "0";
					res_2 = "success";
			    }

			    if (proj.getProjectOrProposal().equals("P")){
					Metrics.appLog(conn, es.gIR_USERN,"ETS_Project_Doc_Update");
			    }
			    else{  //proposal
					Metrics.appLog(conn, es.gIR_USERN,"ETS_Proposal_Doc_Update");
			    }
			}
			else{ //no valid parent cat
				return new String[] {"1","invalid parent cat"};
			}
	    }
	    return new String[] {res_1,res_2};
	}
	catch(Exception e) {
	    //writer.println("error occurred");
	    System.out.println("error here");
	    return new String[] {"1","error"};
	}
    }
*/

    // -----------------------------------
    // admin servlet functions  (spn)
    // -----------------------------------
    public void editUserRoles(String edit_userid,String projectid, int currentid, String msg, EdgeAccessCntrl es, Connection conn,HttpServletResponse response){
	try{
		boolean isSuperAdmin = false;
		PrintWriter writer = response.getWriter();
	    //Vector p = ETSDatabaseManager.getProjects(edit_userid,projectid);
		Vector p = new Vector();
		if ((ETSUtils.checkUserRole(es,projectid)).equals(Defines.ETS_ADMIN)){
			p = ETSDatabaseManager.getProject(projectid);
			isSuperAdmin = true;
		}
		else{
			p = ETSDatabaseManager.getProjects(es.gIR_USERN,projectid);
		}

	    if (p.size() <= 0){
		writer.println("error occurred: invalid project id for this user.");
		System.out.print("put bad projet id message here");
	    }
	    else{

		UnbrandedProperties prop = PropertyFactory.getProperty(conn,projectid);

		ETSProj proj  = (ETSProj)p.elementAt(0);
		PopupHeaderFooter header;
		header = new PopupHeaderFooter();
		if(prop.getAppName().equals("E&TS Connect")){
			header.setPageTitle("E&TS Connect");
		}else if(prop.getAppName().equals("Collaboration Center")){
			header.setPageTitle("Collaboration Center");
		}else{
			header.setPageTitle("E&TS Connect");
		}
		writer.println(header.printPopupHeader());
		ETSUtils.popupHeaderLeft("Edit user privileges",proj.getName(),writer);

		writer.println("<script type=\"text/javascript\" language=\"javascript\">function cancel(){self.close();}</script>");
		writer.println("<form action=\"ETSContentManagerServlet.wss\" method=\"get\" name=\"edituserForm\">");
		if (!((msg.trim()).equals(""))){
		    writer.println("<table><tr><td><span style=\"color:#ff3333\">"+msg+"</span></td></tr></table>");
		}

		writer.println("<input type=\"hidden\" name=\"action\" id=\"label_action\" value=\"editMemberRole2\" />");
		writer.println("<input type=\"hidden\" name=\"proj\" id=\"label_proj\" value=\""+proj.getProjectId()+"\" />");
		writer.println("<input type=\"hidden\" name=\"cc\" id=\"label_cc\" value=\""+currentid+"\" />");
		writer.println("<input type=\"hidden\" name=\"edit_userid\" id=\"label_edUid\" value=\""+edit_userid+"\" />");
		//AccessCntrlFuncs acf = new AccessCntrlFuncs();
		UserObject uo = AccessCntrlFuncs.getUserObject(conn,edit_userid,true,false);
		ETSUser user = ETSDatabaseManager.getETSUser(edit_userid,proj.getProjectId());

		writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"20\" height=\"1\" alt=\"\" /></td>");
		writer.println("<td>");

		writer.println("<table cellspacting=\"0\" cellpadding=\"0\" border=\"0\">");
		writer.println("<tr><td nowrap=\"nowrap\" height=\"21\"><b>User id:</b></td><td>"+user.getUserId()+"</td></tr>");
		writer.println("<tr><td nowrap=\"nowrap\" height=\"21\"><b>User name:</b></td><td>"+uo.gUSER_FULLNAME+"</td></tr>");

		writer.println("<tr><td nowrap=\"nowrap\" height=\"21\"><label for=\"job\"><b>Role:</b></label></td><td>");

		writer.println("<input id=\"job\" class=\"iform\" maxlength=\"64\" name=\"job\" size=\"35\" type=\"text\" value=\""+user.getUserJob()+"\" />");
		writer.println("<input type=\"hidden\" name=\"prevJob\" value=\""+user.getUserJob()+"\" />");
		writer.println("</td></tr>");

		writer.println("<tr>");
		writer.println("<td valign=\"top\" nowrap=\"nowrap\"><b>Access level:</b></td>");
		int currentRoleId = user.getRoleId();
		System.err.println("cri="+currentRoleId);
		writer.println("<input type=\"hidden\" name=\"prevRoleId\" id=\"label_prevRoleId\" value=\""+currentRoleId+"\" />");

		boolean workspaceowner = false;
		if(ETSDatabaseManager.hasProjectPriv(user.getUserId(),proj.getProjectId(),Defines.OWNER)){
			workspaceowner = true;
		}

		if (ETSDatabaseManager.hasProjectPriv(es.gIR_USERN,proj.getProjectId(),Defines.ADMIN) || ETSDatabaseManager.hasProjectPriv(es.gIR_USERN,proj.getProjectId(),Defines.MANAGE_USERS)||isSuperAdmin) {
		    ETSProjectInfoBean projBean = ETSUtils.getProjInfoBean(conn);
		    writer.println("<td>");
		    Vector r = ETSDatabaseManager.getRolesPrivs(projectid);
		    writer.println("<table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\">");


		    for (int i = 0; i<r.size(); i++){
				String[] rp = (String[])r.elementAt(i);
				int roleid = (new Integer(rp[0])).intValue();
				String rolename = rp[1];
				//String privs = rp[2];
				String privids = rp[3];

				System.out.println("ri="+roleid);

				boolean showrole = false;
				boolean ownerrole = false;
				if (ETSDatabaseManager.doesRoleHavePriv(roleid,Defines.IBM_ONLY)) {
				    String edge_userid = AccessCntrlFuncs.getEdgeUserId(conn,edit_userid);
				    String decaftype = AccessCntrlFuncs.decafType(edge_userid,conn);
				    if (decaftype.equals("I")){
						showrole= true;
				    }
				}
				else {
				    showrole = true;
				}

				if (ETSDatabaseManager.doesRoleHavePriv(roleid,Defines.VISITOR) && user.isPrimaryContact()) {
					showrole = false;
				}


				if (ETSDatabaseManager.doesRoleHavePriv(roleid,Defines.OWNER)) {
						ownerrole= true;
				}


				//if (!(ETSDatabaseManager.doesRoleHavePriv(roleid,Defines.IBM_ONLY) && uo.gIBM_SPN_INTERNAL != 1)){
				if ((showrole && !ownerrole && !workspaceowner) || (workspaceowner && ownerrole)){
					writer.println("<tr>");

				    if (roleid == currentRoleId){
						writer.println("<td align=\"left\" width=\"3%\" class=\"lgray\"><input id=\"role_"+i+"\" type=\"radio\" name=\"roles\" value=\""+roleid+"\" checked=\"checked\" /></td>");
				    }
				    else{
						writer.println("<td align=\"left\" width=\"3%\" class=\"lgray\"><input id=\"role_"+i+"\" type=\"radio\" name=\"roles\" value=\""+roleid+"\" /></td>");
				    }
				    writer.println("<td align=\"left\" width=\"97%\" class=\"lgray\"><label for=\"role_"+i+"\">"+rolename+"</label></td>");
				    writer.println("</tr>");

				    writer.println("<tr>");
				    //writer.println("<td>&nbsp;</td><td align=\"left\" valign=\"top\">Privileges: "+privs+"</td>");
				    writer.println("<td>&nbsp;</td><td align=\"left\" valign=\"top\">Privileges: ");
				    String priv_desc = "";
				    StringTokenizer st = new StringTokenizer(privids, ",");
				    Vector privs = new Vector();
				    while (st.hasMoreTokens()){
						String priv = st.nextToken();
						privs.addElement(priv);
				    }
				    for (int j = 0; j < privs.size(); j++){
						String s = (String)privs.elementAt(j);
						String  desc = projBean.getInfoDescription("PRIV_"+s,0);
						if (!desc.equals("")){
						    if(!priv_desc.equals("")){
								priv_desc = priv_desc+"; "+desc;
						    }
						    else{
								priv_desc = desc;
						    }
						}
				    }
				    writer.println(priv_desc);
				    //projBean.getInfoDescription("ROLE_DESC_"+roleid,0)+"
				    writer.println("</td>");
				    writer.println("</tr>");
				    writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"7\" width=\"1\" alt=\"\" /></td></tr>");
				}
		    }
		    writer.println("</table>");
		    writer.println("</td>");
		}
		else{
		    String[] roles = ETSDatabaseManager.getUserRole(user.getUserId(), proj.getProjectId());
		    //int roleid= (new Integer(roles[0])).intValue();
		    String rolename = roles[1];
		    //String privs = roles[2];

		    writer.println("<td align=\"left\"><input type=\"hidden\" name=\"roles\" id=\"label_roles\" value=\""+currentRoleId+"\" />");
		    writer.println(rolename+"</td>");
		}

		writer.println("</tr>");
				
		writer.println("</tr>");
		String currentMID = ETSDatabaseManager.getUserMessengerID(user.getUserId());
		
		writer.println("<tr>");
		writer.println("<td valign=\"top\" nowrap=\"nowrap\"><label for=\"mID\"><b>Instant Message ID:</b></label></td>");
		writer.println("<td><input id=\"mID\" class=\"iform\" maxlength=\"64\" name=\"mID\" size=\"35\" type=\"text\" value=\""+currentMID+"\" />");
		writer.println("<input type=\"hidden\" name=\"prevMID\" value=\""+currentMID+"\" />");
		writer.println("</td>");
		writer.println("</tr>");

		ETSUserDetails details = new ETSUserDetails();
		details.setWebId(user.getUserId());
		details.extractUserDetails(conn);
		String reqUserId = es.gIR_USERN;
		TeamGroupDAO grpDAO = new TeamGroupDAO();
		grpDAO.prepare();
		String userGrpList = grpDAO.getGrpListStringForUser(proj.getProjectId(), user.getUserId(),conn);
		Vector allGrpVect = grpDAO.getAllGroupsForEdit(proj.getProjectId(),"","ASC",false,reqUserId);
		grpDAO.cleanup();
        boolean noGrpForUser=false;

		if(!allGrpVect.isEmpty()){
		writer.println("<tr>");
		writer.println("<td valign=\"top\" nowrap=\"nowrap\"><b>Member of Group:</b></td>");
		if(userGrpList.length()>0){
			 writer.println("<td>"+userGrpList+"</td>");
		}else{
			//noGrpForUser=true;
			writer.println("<td>None</td>");
		}
		writer.println("</tr>");

		writer.println("<input type=\"hidden\" name=\"projectId\" id=\"label_projId\" value=\""+proj.getProjectId()+"\" />");
		writer.println("<input type=\"hidden\" name=\"userId\" id=\"label_userId\" value=\""+user.getUserId()+"\" />");


		StringTokenizer st = new StringTokenizer(userGrpList,",");
		Vector vGrpList = new Vector();
		while (st.hasMoreTokens()){
			String uGrp = st.nextToken();
			vGrpList.addElement(uGrp);
		}
		// remove all users grp from the dropdown list
		if(vGrpList.contains("All Users")) {
			vGrpList.remove("All Users");
		}
		if (vGrpList.size()== 0) {
			noGrpForUser = true;
		}
		
		boolean grpMemb = false;
		boolean grpIntFound = false;
		boolean grpExtFound = false;
		int intCtr = 0;
		int extCtr = 0;

		if (details.getUserType() == details.USER_TYPE_INTERNAL) {

			for (int i=0; i < allGrpVect.size(); i++) {
				if(intCtr == 0){
					writer.println("<tr>");
					writer.println("<td valign=\"top\" nowrap=\"nowrap\"><b>Modify Group:</b></td>");
					writer.println("<td><select id=\"mfyGrp\" name=\"sel_mfy_grp\">");
					writer.println("<option value=\"\" selected=\"selected\">Select an Action</option>");
					grpIntFound = true;
					if(!noGrpForUser){
						writer.println("<option value=\"leaveGrp\">Leave in current group(s)</option>");
						writer.println("<option value=\"delGrp\">Remove from group(s)</option>");
					}
				}
				grpMemb = false;
				ETSGroup group = (ETSGroup) allGrpVect.elementAt(i);
				for (int j = 0; j < vGrpList.size(); j++){
					String s = (String)vGrpList.elementAt(j);
					if(s.trim().equals(group.group_name)){
						grpMemb = true;
						writer.println("<option value=\"rem"+group.group_id+"\">Remove from Group " +group.group_name+ "</option>");
						break;
					}
				}
				if(!grpMemb)
						writer.println("<option value=\"add"+group.group_id+"\">Put in Group " +group.group_name+ "</option>");
				++intCtr;
			}
		 }else if(details.getUserType() == details.USER_TYPE_EXTERNAL) {

			for (int i=0; i < allGrpVect.size(); i++) {
				grpMemb = false;
				ETSGroup group = (ETSGroup) allGrpVect.elementAt(i);
				if(group.getGroupSecurityClassification().equals("0")){
					grpExtFound = true;
					if(extCtr == 0){
						writer.println("<tr>");
						writer.println("<td valign=\"top\" nowrap=\"nowrap\"><b>Modify Group:</b></td>");
						writer.println("<td><select id=\"mfyGrp\" name=\"sel_mfy_grp\">");
						writer.println("<option value=\"\" selected=\"selected\">Select an Action</option>");
						if(!noGrpForUser){
							writer.println("<option value=\"leaveGrp\">Leave in current group(s)</option>");
							writer.println("<option value=\"delGrp\">Remove from group(s)</option>");
						}
					}
					for (int j = 0; j < vGrpList.size(); j++){
						String s = (String)vGrpList.elementAt(j);
						if(s.trim().equals(group.group_name)){
							grpMemb = true;
							writer.println("<option value=\"rem"+group.group_id+"\">Remove from Group " +group.group_name+ "</option>");
							break;
					 }
				}
				if(!grpMemb)
						writer.println("<option value=\"add"+group.group_id+"\">Put in Group " +group.group_name+ "</option>");
				++extCtr;
				}
			  }
			  if(!grpExtFound){
			  		writer.println("<tr><td colspan=\"2>&nbsp;</td></tr>");
					writer.println("<tr><td colspan=\"2><span style=\"color:#000000\">Presently no groups exist for external users in the workspace.</span></td></tr>");
					writer.println("<tr><td colspan=\"2>&nbsp;</td></tr>");
			  	}
		  }
			if(grpIntFound || grpExtFound){
				writer.println("</select></td>");
				writer.println("</tr>");
				writer.println("<tr><td colspan=\"2>&nbsp;</td></tr>");
			}
		}else{
			writer.println("<tr><td colspan=\"2><span style=\"color:#000000\">Presently no groups exist for the workspace.</span></td></tr>");
		}

		writer.println("<tr><td colspan=\"2>&nbsp;</td></tr>");
		UserObject uo2 = AccessCntrlFuncs.getUserObject(conn,user.getLastUserId(),true,false);
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, new Locale("en","US"));

		if(uo2 != null){
		    writer.println("<tr><td colspan=\"2\" height=\"21\" class=\"small\">Last updated by "+uo2.gUSER_FULLNAME+" ["+user.getLastUserId()+"] on "+df.format(new java.util.Date(user.getLastTimestamp()))+"</td></tr>");
		}
		else{
		    writer.println("<tr><td colspan=\"2\" height=\"21\" class=\"small\">Last updated by "+user.getLastUserId()+" on "+df.format(new java.util.Date(user.getLastTimestamp()))+"</td></tr>");
		}
		writer.println("</table>");
		writer.println("</td></tr></table>");
		
		writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		writer.println("<tr><td>&nbsp;</td></tr>");
		writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"20\" height=\"1\" alt=\"\" /></td>");
		writer.println("<td headers=\"editUser_submit\" width=\"140\" align=\"left\">");
		writer.println("<input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" border=\"0\" id=\"label_sub\" height=\"21\" width=\"120\" alt=\"submit\" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		writer.println("</td><td headers=\"editUser_cancel\" align=\"right\" valign=\"top\" width=\"16\" >");
		writer.println("<a href=\"javascript:cancel()\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" /></a>&nbsp;");
		writer.println("</td><td headers=\"editUser_Cancel\" align=\"left\" valign=\"middle\" >");
		writer.println("<a href=\"javascript:cancel()\">Cancel</a>");
		writer.println("</td></tr></table>");
		writer.println("<noscript><br />Javascript is not enabled. To cancel, please click the 'X' at the top right corner of this window.</noscript>");
		
		writer.println("</form>");

		ETSUtils.popupHeaderRight(writer);
		writer.println(header.printPopupFooter());
	    } //end of esle
	}
	catch(Exception e) {
	    //writer.println("error occurred");
	    System.out.println("error here = "+e);
	}

    }



    public void editUserRoles2(String edit_userid,String projectid, int roleid,String roleModify,String mIDModify,String grpModify, EdgeAccessCntrl es,Connection conn,HttpServletResponse response){
	try{
		PrintWriter writer = response.getWriter();

	    //Vector p = ETSDatabaseManager.getProjects(edit_userid,projectid);
		Vector p = new Vector();
		if ((ETSUtils.checkUserRole(es,projectid)).equals(Defines.ETS_ADMIN)){
			p = ETSDatabaseManager.getProject(projectid);
		}
		else{
			p = ETSDatabaseManager.getProjects(es.gIR_USERN,projectid);
		}

	    if (p.size() <= 0){
		writer.println("error occurred: invalid project id for this user.");
		System.out.print("put bad projet id message here");
	    }
	    else{

		ETSProj proj  = (ETSProj)p.elementAt(0);
		PopupHeaderFooter header;
		header = new PopupHeaderFooter();
		UnbrandedProperties prop = PropertyFactory.getProperty(conn,projectid);
		if(prop.getAppName().equals("E&TS Connect")){
			header.setPageTitle("E&TS Connect");
		}else if(prop.getAppName().equals("Collaboration Center")){
			header.setPageTitle("Collaboration Center");
		}else{
			header.setPageTitle("E&TS Connect");
		}
		writer.println(header.printPopupHeader());

		ETSUtils.popupHeaderLeft("Edit user privileges",proj.getName(),writer);

		writer.println("<script type=\"text/javascript\" language=\"javascript\">function ok_close(){opener.location.reload();self.close();}</script>");
		writer.println("<form action=\"ETSContentManagerServlet.wss\" method=\"get\" name=\"edituser2Form\">");

		 writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		 writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"20\" height=\"1\" alt=\"\" /></td>");
		 writer.println("<td>");

        if((roleModify.equals("modified")) || (mIDModify.equals("modified"))){
        		writer.println("Access level/role/message ID for User Id <b>"+edit_userid+"</b> has been successfully updated.");
        }
		writer.println("<br /><br />");
        if(grpModify.equals("modified")){
			writer.println("Group(s) has been updated for User Id <b>"+edit_userid+"</b>.");
        }
		writer.println("<br /><br />");

		writer.println("<a href=\"javascript:ok_close()\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"OK\" />OK</a>");
		writer.println("<noscript><br />Javascript is not enabled, to continue please click the 'X' at the top right of this window and refresh your main window.</noscript>");

		writer.println("</td></tr></table>");
		writer.println("</form>");

		ETSUtils.popupHeaderRight(writer);
		writer.println(header.printPopupFooter());
	    } //end of esle
	}
	catch(Exception e) {
	    //writer.println("error occurred");
	    System.out.println("error here");
	}

    }

	private boolean doDelUserPhoto(String userid){
		boolean success = false;

		try{
			success = ETSDatabaseManager.deleteUserPhoto(userid);
		}
		catch(Exception e){
			System.out.println("exception="+e);
			e.printStackTrace();
		}

		return success;

	}



    private Vector getCatTree(ETSCat cat, String userid, String projid,PrintWriter writer){
	String first = "";
	Vector v = new Vector();
	StringBuffer buf = new StringBuffer();
	int indent = 0;
	try{
	    String space = "";
	    for (int s = 0; s < 4; s++){
		space = space + "&nbsp;";
	    }

	    v.addElement(first);
	    v.addElement(buf);

	    Vector subcats = ETSDatabaseManager.getSubCats(cat.getId());
	    Vector docs = ETSDatabaseManager.getDocs(cat.getId());

	    if(subcats.size()>0 || docs.size()>0){
		first="0";
		buf.append("<table>");
		buf.append("<tr><td height=\"15\" class=\"small\"><img src=\""+Defines.SERVLET_PATH+"ETSImageServlet.wss?proj=ETS_CAT_IMG&mod=0\" width=\"13\" height=\"9\" alt=\"folder\" /><b>"+cat.getName()+"</b></td></tr>");

		v.setElementAt(first,0);
		v.setElementAt(buf,1);

		for (int i = 0; i < subcats.size(); i++){
		    ETSCat c = (ETSCat)subcats.elementAt(i);
		    //getCatSubTree(c.getId(),indent,buf);
		    v = getCatSubTree(c.getId(),indent,v,projid,userid);
		}
		first = (String)v.elementAt(0);
		buf = (StringBuffer)v.elementAt(1);

		for (int j = 0; j < docs.size(); j++){
		    ETSDoc d = (ETSDoc)docs.elementAt(j);
		    if ((!d.getUserId().equals(userid)) && (!ETSDatabaseManager.hasProjectPriv(userid,projid,Defines.DELETE))){
			first="2";
		    }
		    buf.append("<tr><td height=\"15\" class=\"small\">"+space);
		    buf.append("<img src=\""+Defines.SERVLET_PATH+"ETSImageServlet.wss?proj=ETS_DOC_IMG&mod=0\" width=\"12\" height=\"12\" alt=\"document\" />"+d.getName()+"</td></tr>");
		}
		buf.append("</table>");
	    }
	    else{
		first="1";
	    }
	}
	catch(SQLException se){
	    System.out.println("esql rror occurred in getCatTree= "+se);
	    first="";
	    buf.append("error occurred");
	}
	catch(Exception e){
	    System.out.println("error occurred in getCatTree= "+e);
	    first="";
	    buf.append("error occurred");
	}
	v.setElementAt(first,0);
	v.setElementAt(buf,1);
	return v;
    }


    //private StringBuffer getCatSubTree(int catid, int indent, String userid, StringBuffer buf){
    private Vector getCatSubTree(int catid, int indent, Vector v,String projid, String userid){
	String first = (String)v.elementAt(0);
	StringBuffer buf = (StringBuffer)v.elementAt(1);

	indent = indent +4;
	try{
	    String space = "";
	    for (int s = 0; s < indent; s++){
		space = space + "&nbsp;";
	    }

	    ETSCat cat = ETSDatabaseManager.getCat(catid);
	    if (cat.getCatType() == 0 || ((!cat.getUserId().equals(userid)) && (!ETSDatabaseManager.hasProjectPriv(userid,projid,Defines.DELETE)))){
		first="2";
	    }
	    buf.append("<tr><td height=\"15\" class=\"small\">"+space);
	    buf.append("<img src=\""+Defines.SERVLET_PATH+"ETSImageServlet.wss?proj=ETS_CAT_IMG&mod=0\" width=\"13\" height=\"9\" alt=\"folder\" />"+cat.getName()+"</td></tr>");

	    v.setElementAt(first,0);
	    v.setElementAt(buf,1);

	    Vector subcats = ETSDatabaseManager.getSubCats(catid);
	    for (int i = 0; i < subcats.size(); i++){
		ETSCat c = (ETSCat)subcats.elementAt(i);
		//getCatSubTree(c.getId(),indent,buf);
		v = getCatSubTree(c.getId(),indent,v,projid,userid);
	    }
	    first = (String)v.elementAt(0);
	    buf = (StringBuffer)v.elementAt(1);

	    Vector docs = ETSDatabaseManager.getDocs(catid);
	    for (int j = 0; j < docs.size(); j++){
		ETSDoc d = (ETSDoc)docs.elementAt(j);
		if ((!d.getUserId().equals(userid)) && (!ETSDatabaseManager.hasProjectPriv(userid,projid,Defines.DELETE))){
		    first="2";
		}

		buf.append("<tr><td height=\"15\" class=\"small\">"+space+"&nbsp;&nbsp;&nbsp;&nbsp;");
		buf.append("<img src=\""+Defines.SERVLET_PATH+"ETSImageServlet.wss?proj=ETS_DOC_IMG&mod=0\" width=\"12\" height=\"12\" alt=\"document\" />"+d.getName()+"</td></tr>");
	    }
	}
	catch(SQLException se){
	    System.out.println("sql error occurred in getCatSubTree= "+se);
	    first="";
	    buf.append("error occurred");
	}
	catch(Exception e){
	    System.out.println("error occurred in getCatSubTree= "+e);
	    first="";
	    buf.append("error occurred");
	}
	v.setElementAt(first,0);
	v.setElementAt(buf,1);
	//return buf;
	return v;
    }


    public void init(ServletConfig config)
	throws ServletException
    {
	super.init(config);

	try{
	    mailhost = Global.mailHost;
	    System.out.println("Using " + mailhost);
	    if (mailhost == null)
		{
		    mailhost = "us.ibm.com";
		}
	    databaseManager = new ETSDatabaseManager();
	}
	catch (Exception e){
	    e.printStackTrace();
	    throw new ServletException(e.getMessage());
	}
    }

    public void destroy()
    {
    }

    private String getParameter(HttpServletRequest req, String key)
    {
	String value = req.getParameter(key);

	if (value == null)
	    {
		return "";
	    }
	else
	    {
		return value;
	    }
    }
    /*
    private String[] getParameterValues(HttpServletRequest req, String key)
    {
	String[] value = req.getParameterValues(key);

	if (value == null)
	    {
		return "";
	    }
	else
	    {
		return value;
	    }
    }
*/


    private Vector getBreadcrumb(ETSCat parent_cat) throws SQLException{
	Vector breadcrumb = new Vector();
	try{
	    if (parent_cat != null){
		breadcrumb.addElement(parent_cat);
		ETSCat c = parent_cat;
		while (true){
		    c = ETSDatabaseManager.getCat(c.getParentId());
		    if (c != null){
			breadcrumb.addElement(c);
			if (c.getParentId() == 0){
			    break;
			}
		    }
		    else{
			break;
		    }
		}

	    }
	    return breadcrumb;
	}
	catch (SQLException se){
	    throw se;
	}
    }


/*
   private void printBreadcrumb(Vector breadcrumb, PrintWriter writer){
	writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td>");
	for (int i = (breadcrumb.size())-1; i >= 0; i--){
	    ETSCat bc = (ETSCat)breadcrumb.elementAt(i);
	    if (i != (breadcrumb.size())-1){
		writer.println(" &gt; ");
	    }

	    if(i == 0){
		writer.println("<b>"+bc.getName()+"</b>");
	    }
	    else{
		writer.println(bc.getName());
	    }
	}
	writer.println("</td></tr></table>");
    }
*/

 private void printBreadcrumb(Vector breadcrumb, PrintWriter writer){
	StringBuffer buf = new StringBuffer();

	buf.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td class=\"small\">");
	for (int i = (breadcrumb.size())-1; i >= 0; i--){
	    ETSCat bc = (ETSCat)breadcrumb.elementAt(i);
	    if (i != (breadcrumb.size())-1){
			buf.append(" &gt; ");
	    }

	    if(i == 0){
			buf.append("<b>"+bc.getName()+"</b>");
	    }
	    else{
			buf.append(bc.getName());
	    }
	}
	buf.append("</td></tr>");

	//gray dotted line
	buf.append("<tr><td height=\"21\">");
	buf.append("<img src=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" height=\"1\" width=\"443\" alt=\"\" />");
	buf.append("</td></tr>");
	buf.append("</table>");
	writer.println(buf.toString());

    }






// =============================== OLD METHODS ======================================
// =============================== OLD METHODS ======================================
// =============================== OLD METHODS ======================================
// =============================== OLD METHODS ======================================
// =============================== OLD METHODS ======================================

    private void addCat(String projectid, int parentid, String topcat, String linkid, String msg, EdgeAccessCntrl es, HttpServletResponse response){
	try{
		PrintWriter writer = response.getWriter();
	    Vector p = ETSDatabaseManager.getProjects(es.gIR_USERN,projectid);
	    Vector breadcrumb = new Vector();

	    if (p.size() <= 0){
		writer.println("error occurred: invalid userid for this project");
		System.out.println("put bad projet id message here");
	    }
	    else{
		ETSProj proj  = (ETSProj)p.elementAt(0);

		ETSCat parent_cat = ETSDatabaseManager.getCat(parentid);

		if (parent_cat != null){
		    breadcrumb = getBreadcrumb(parent_cat);
		    printBreadcrumb(breadcrumb,writer);

		    writer.println("<form action=\"ETSContentManagerServlet.wss\" method=\"get\" name=\"addcatForm\">");
		    if (!((msg.trim()).equals(""))){
			writer.println("<table><tr><td><span style=\"color:#ff3333\">"+msg+"</span></td></tr></table>");
		    }

		    writer.println("<input type=\"hidden\" name=\"action\" id=\"label_action\" value=\"addcat2\" />");
		    writer.println("<input type=\"hidden\" name=\"proj\" value=\""+proj.getProjectId()+"\" />");
		    writer.println("<input type=\"hidden\" name=\"tc\" value=\""+topcat+"\" />");
		    writer.println("<input type=\"hidden\" name=\"cc\" value=\""+parent_cat.getId()+"\" />");
		    writer.println("<input type=\"hidden\" name=\"linkid\" value=\""+linkid+"\" />");

		    writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr>");
		    writer.println("<td><span class=\"ast\"><b>*</b></span></td><td><span class=\"small\"> Denotes required fields</span></td>");
		    writer.println("</tr></table>");


		    writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		    writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"20\" height=\"10\" alt=\"\" /></td>");
		    writer.println("<td>");

		    writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		    writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");
		    writer.println("<tr>");
		    writer.println("<td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
		    writer.println("<td align=\"left\" width=\"99%\"><label for=\"catname\">New folder name: </label></td></tr>");
		    writer.println("<tr><td colspan=\"2\"><input type=\"text\" id=\"catname\" size=\"30\" name=\"catname\" value=\"\" /></td></tr>");

		    writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");
		    writer.println("</table>");

		    writer.println("<br />");
		    writer.println("<input type=\"image\" id=\"label_sub\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"submit\" />");
		    writer.println("&nbsp; &nbsp; <a href=\"ETSProjectsServlet.wss?proj="+proj.getProjectId()+"&tc="+topcat+"&cc="+parent_cat.getId()+"&linkid="+linkid+"\">"
		    	+"<img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" />Cancel</a>");

		    writer.println("</td></tr></table>");

		    writer.println("</form>");

		}
		else{
		    writer.println("error occured: invalid parent cat id for this user.");
		    System.out.println("put bad parent cat id message here");
		}
	    }
	}
	catch(Exception e) {
	    //writer.println("error occurred");
	    System.out.println("error here");
	}

    }


    private void delCat(String projectid, int currentid, int topcatid, EdgeAccessCntrl es, HttpServletResponse response){
	try{
		PrintWriter writer = response.getWriter();
	    Vector p = ETSDatabaseManager.getProjects(es.gIR_USERN,projectid);
	    Vector breadcrumb = new Vector();

	    if (p.size() <= 0){
		writer.println("error occurred: invalid project id for this user.");
		System.out.println("put bad projet id message here");
	    }
	    else{
		ETSProj proj  = (ETSProj)p.elementAt(0);

		ETSCat current_cat = ETSDatabaseManager.getCat(currentid);
		if (current_cat != null){

		    breadcrumb = getBreadcrumb(current_cat);

		    PopupHeaderFooter header;
		    header = new PopupHeaderFooter();
		    header.setPageTitle("E&TS Connect");
		    writer.println(header.printPopupHeader());

		    ETSUtils.popupHeaderLeft("Delete folder", proj.getName(), writer);

		    printBreadcrumb(breadcrumb,writer);

		    writer.println("<script type=\"text/javascript\" language=\"javascript\">function cancel(){self.close();}</script>");
		    writer.println("<form action=\"ETSContentManagerServlet.wss\" method=\"get\" name=\"delcatForm\">");
		    writer.println("<input type=\"hidden\" name=\"action\" id=\"label_action\" value=\"delcat2\" />");
		    writer.println("<input type=\"hidden\" name=\"proj\" value=\""+proj.getProjectId()+"\" />");
		    writer.println("<input type=\"hidden\" name=\"cc\" value=\""+current_cat.getId()+"\" />");
		    writer.println("<input type=\"hidden\" name=\"tc\" value=\""+topcatid+"\" />");

		    writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");

		    Vector b = getCatTree(current_cat,es.gIR_USERN,proj.getProjectId(),writer);  //[0]=children, allowed status  [1]=buf
		    if (b.elementAt(0).equals("1")){  //no children
			writer.println("<tr><td><span style=\"color:#ff3333\">This action can not be undone.</span></td></tr>");
			writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");
				writer.println("You are about to delete the folder <b>"+current_cat.getName()+"</b>.</td></tr>");
		    }
		    else if (b.elementAt(0).equals("2")){  //does not own or have privilege
			StringBuffer buf  = (StringBuffer)b.elementAt(1);
		writer.println("<tr><td><span style=\"color:#ff3333\">You are not allowed to delete this folder.</span></td></tr>");
			writer.println("<tr><td>You do not own all subfolders and documents under this folder. Please have team members delete their respective folders and documents or contact a workspace manager to delete this folder for you.</td></tr>");
			writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");
			writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");
			writer.println("<tr><td>The following are the folders and documents under this folder:<br />");
			writer.println(buf.toString());
			writer.println("</td>");
		    }
		    else if (b.elementAt(0).equals("0")){
			StringBuffer buf = (StringBuffer)b.elementAt(1);
			writer.println("<tr><td><span style=\"color:#ff3333\">This action can not be undone.</span></td></tr>");
			writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");
			writer.println("<td>");
			writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td>");
			writer.println("You are about to delete the folder <b>"+current_cat.getName()+"</b> and all of the categories and documents associated with it.</td></tr>");
			writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");
			writer.println("<tr><td>The following will be deleted:<br />");
			writer.println(buf.toString());
			writer.println("</td></tr></table>");
			writer.println("</td>");
		    }
		    else{
			writer.println("<tr><td><span style=\"color:#ff3333\">Error</span></td></tr>");
			writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");
		    }
		    writer.println("</table>");

		    writer.println("<br /><br /><table border=\"0\" cellspacing=\"2\" cellpadding=\"0\"><tr>");
		    if(b.elementAt(0).equals("0") || b.elementAt(0).equals("1")){
			writer.println("<td><input type=\"image\" id=\"label_sub\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"delete folder\" /></td>");
			writer.println("<td><a href=\"javascript:cancel()\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" />Cancel</a></td></tr>");
			writer.println("<tr><td colspan=\"2\"><noscript><br />Javascript is not enabled. To cancel, please click the 'X' at the top right corner of this window.</noscript>");
		    }
		    else{
			writer.println("<td><a href=\"javascript:cancel()\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"OK\" />OK</a>");
			writer.println("<tr><td colspan=\"2\"><noscript><br />Javascript is not enabled. To close, please click the 'X' at the top right corner of this window.</noscript>");
		    }

		    writer.println("</tr></table>");

		    writer.println("</td></tr></table>");
		    writer.println("</form>");

		    ETSUtils.popupHeaderRight(writer);
		    writer.println(header.printPopupFooter());
		}
		else{
		    writer.println("error occurred:invalid cat id for this user.");
		    System.out.print("put bad current cat id message here");
		}
	    }
	}
	catch(Exception e) {
	    //writer.println("error occurred");
	    System.out.println("error here="+e);
	}
    }


    private void updateCat(String projectid, int catid, String msg, EdgeAccessCntrl es, HttpServletResponse response){
	try{
		PrintWriter writer = response.getWriter();
	    Vector p = ETSDatabaseManager.getProjects(es.gIR_USERN,projectid);
	    Vector breadcrumb = new Vector();

	    if (p.size() <= 0){
		writer.println("error occurred: invalid project id for this user.");
		System.out.println("put bad projet id message here");
	    }
	    else{
		ETSProj proj  = (ETSProj)p.elementAt(0);

		ETSCat cat = ETSDatabaseManager.getCat(catid);
		if (cat != null){
		    breadcrumb = getBreadcrumb(cat);

		    PopupHeaderFooter header;
		    header = new PopupHeaderFooter();
		    header.setPageTitle("E&TS Connect");
		    writer.println(header.printPopupHeader());

		    ETSUtils.popupHeaderLeft("Update folder", proj.getName(), writer);

		    printBreadcrumb(breadcrumb, writer);

		    writer.println("<script type=\"text/javascript\" language=\"javascript\">function cancel(){self.close();}</script>");
		    writer.println("<form action=\"ETSContentManagerServlet.wss\" method=\"get\" name=\"updatecatForm\">");
		    if (!((msg.trim()).equals(""))){
			writer.println("<table><tr><td><span style=\"color:#ff3333\">"+msg+"</span></td></tr></table>");
		    }

		    writer.println("<input type=\"hidden\" name=\"action\" id=\"label_action\" value=\"updatecat2\" />");
		    writer.println("<input type=\"hidden\" name=\"proj\" value=\""+proj.getProjectId()+"\" />");
		    writer.println("<input type=\"hidden\" name=\"cc\" value=\""+cat.getId()+"\" />");

		     writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr>");
		    writer.println("<td><span class=\"ast\"><b>*</b></span></td><td><span class=\"small\"> Denotes required fields</span></td>");
		    writer.println("</tr></table>");

		    writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		    writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"20\" height=\"1\" alt=\"\" /></td>");
		    writer.println("<td>");

		    writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		    writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");

		    writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
		    writer.println("<td align=\"left\" width=\"99%\"><label for=\"catname\">Folder name:</label></td></tr>");
		    writer.println("<tr><td colspan=\"2\"><input type=\"text\" id=\"catname\" size=\"30\" name=\"catname\" value=\""+cat.getName()+"\" /></td></tr>");

		    writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");

		    //writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
		    //writer.println("<td align=\"left\" width=\"99%\"><label for=\"catdesc\">New folder description (max. 1024 chars.)</label></td></tr>");
		    //writer.println("<tr><td colspan=\"2\"><textarea id=\"catdesc\" name=\"catdesc\" cols=\"30\" rows=\"3\" wrap=\"soft\" value=\"\" />");
		    //writer.println(cat.getDescription());
		    //writer.println("</textarea></td></tr>");
		    writer.println("</table>");

		    /*
		    writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr>");
		    writer.println("<td><span class=\"ast\"><b>*</b></span></td><td><span class=\"small\"> Required fields</span></td>");
		    writer.println("</tr></table>");
		    */

		    writer.println("<input type=\"image\" id=\"label_sub\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"submit\" />");
		    writer.println("<a href=\"javascript:cancel()\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" />Cancel</a>");
		    writer.println("<noscript><br />Javascript is not enabled. To cancel, please click the 'X' at the top right corner of this window.</noscript>");

		    writer.println("</td></tr></table>");

		    writer.println("</form>");

		    ETSUtils.popupHeaderRight(writer);
		    writer.println(header.printPopupFooter());
		}
		else{
		    writer.println("error occurred: invalid cat id for this user.");
		    System.out.print("put bad cat id message here");
		}
	    }
	}
	catch(Exception e) {
	    //writer.println("error occurred");
	    System.out.print("error here");
	}

    }


    private void addDoc(String projectid, int parentid, String msg, String action, String meeting_id, String topcat, EdgeAccessCntrl es, HttpServletResponse response){
	try{
		PrintWriter writer = response.getWriter();
	    Vector p = ETSDatabaseManager.getProjects(es.gIR_USERN,projectid);
	    Vector breadcrumb = new Vector();

	    if (p.size() <= 0){
		writer.println("error occurred: invalid project id for this user.");
		System.out.print("put bad projet id message here");
	    }
	    else{
		ETSProj proj  = (ETSProj)p.elementAt(0);
		ETSCat parent_cat = null;
		if (action.equals("adddoc")){
		    parent_cat = ETSDatabaseManager.getCat(parentid);
		}


		if (action.equals("adddoc")){
		    parent_cat = ETSDatabaseManager.getCat(parentid);
		    if (parent_cat != null){
			breadcrumb = getBreadcrumb(parent_cat);
		    }
		    else{
			writer.println("error occurred: invalid cat id for this user.");
			System.out.print("put bad parent cat id message here");
			return;
		    }
		}

		PopupHeaderFooter header;
		header = new PopupHeaderFooter();
		header.setPageTitle("E&TS Connect");
		writer.println(header.printPopupHeader());

		ETSUtils.popupHeaderLeft("Add document",proj.getName(), writer);

		if (action.equals("adddoc")){
		    printBreadcrumb(breadcrumb,writer);
		}

		writer.println("<script type=\"text/javascript\" language=\"javascript\">function cancel(){self.close();}</script>");

		writer.println("<script type=\"text/javascript\" language=\"javascript\">");
		writer.println("function openMessage() {");
		writer.println("alert('Your document is about to be transferred.  Depending on the size of your file, this may take a few minutes. Your window will not be updated until the document has finished uploading.')");
		writer.println("return (true);");
		writer.println("}</script>");


		String actionStr = "";
		if (action.equals("addmeetingdoc")){
		    actionStr="ETSContentManagerServlet.wss?action=addmeetingdoc2&proj="+proj.getProjectId()+"&tc=0&cc=0&meetingid="+meeting_id;
		}
		else if (action.equals("addprojectplan")){
		    actionStr="ETSContentManagerServlet.wss?action=addprojectplan2&proj="+proj.getProjectId()+"&tc=0&cc=0";
		}
		else{
		    actionStr="ETSContentManagerServlet.wss?action=adddoc2&proj="+proj.getProjectId()+"&tc="+topcat+"&cc="+parent_cat.getId();
		}

		writer.println("<form action=\""+actionStr+"\" method=\"post\" enctype=\"multipart/form-data\" name=\"adddocForm\" onsubmit=\"return openMessage()\" >");

		if (!((msg.trim()).equals(""))){
		    writer.println("<table><tr><td><span style=\"color:#ff3333\">"+msg+"</span></td></tr></table>");
		}

		//writer.println("<input type=\"hidden\" name=\"action\" id=\"label_action\" value=\"adddoc2\" />");
		//writer.println("<input type=\"hidden\" name=\"proj\" id=\"label_proj\" value=\""+proj.getProjectId()+"\" />");
		//writer.println("<input type=\"hidden\" name=\"cc\" id=\"label_cc\" value=\""+parent_cat.getId()+"\" />");

		writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr>");
		writer.println("<td><span class=\"ast\"><b>*</b></span></td><td><span class=\"small\"> Denotes required fields</span></td>");
		writer.println("</tr></table>");


		writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"20\" height=\"1\" alt=\"\" /></td>");
		writer.println("<td>");

		writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");

		//name
		writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
		writer.println("<td align=\"left\" width=\"99%\"><label for=\"docname\">Name:</label></td></tr>");
		writer.println("<tr><td colspan=\"2\"><input type=\"text\" id=\"docname\" size=\"30\" name=\"docname\" value=\"\" /></td></tr>");


		//description
		writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"5\" alt=\"\" /></td></tr>");
		writer.println("<tr><td align=\"left\" width=\"1%\">&nbsp;</td>");
		writer.println("<td align=\"left\" width=\"99%\"><label for=\"docdesc\">Description (max. 1024 chars.)</label></td></tr>");
		writer.println("<tr><td colspan=\"2\"><textarea id=\"docdesc\" name=\"docdesc\" cols=\"30\" rows=\"3\" wrap=\"soft\" value=\"\" /></textarea></td></tr>");

		//keywords
		writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"5\" alt=\"\" /></td></tr>");
		writer.println("<tr><td align=\"left\" width=\"1%\">&nbsp;</td>");
		writer.println("<td align=\"left\" width=\"99%\"><label for=\"keywords\">Keywords:</label></td></tr>");
		writer.println("<tr><td colspan=\"2\"><input type=\"text\" id=\"keywords\" size=\"30\" name=\"keywords\" value=\"\" /></td></tr>");
		//file
		writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"5\" alt=\"\" /></td></tr>");
		writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
		writer.println("<td align=\"left\" width=\"99%\"><label for=\"docfile\">File:</label></td></tr>");
		writer.println("<tr><td colspan=\"2\"><input type=\"file\" id=\"docfile\" size=\"30\" name=\"docfile\" value=\"\" /></td></tr>");

		//notify option
		if (action.equals("adddoc")){
		    writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"5\" alt=\"\" /></td></tr>");
		    writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
		    writer.println("<td align=\"left\" width=\"99%\">Notify options:</td></tr>");

		    writer.println("<tr><td colspan=\"2\"><table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		    writer.println("<tr><td><input id=\"none\" type=\"radio\" name=\"notify\" value=\"none\" checked=\"checked\" /></td>");
		    writer.println("<td align=\"left\"><label for=\"none\"> None</label>&nbsp;&nbsp;</td>");
		    writer.println("<td>&nbsp;&nbsp;<input id=\"all\" type=\"radio\" name=\"notify\" value=\"all\" /></td>");
		    writer.println("<td align=\"left\"><label for=\"all\"> All team members</label></td></tr>");
		    writer.println("</table>");
		    writer.println("</td></tr>");
		}
		else{
		    writer.println("<input type=\"hidden\" name=\"notify\" value=\"none\" />");
		}
		writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");
		writer.println("</table>");

		/*
		writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr>");
		writer.println("<td><span class=\"ast\"><b>*</b></span></td><td><span class=\"small\"> Required fields</span></td>");
		writer.println("</tr>");
		writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"3\" alt=\"\" /></td></tr>");
		writer.println("</table>");
		*/

		writer.println("<input type=\"image\" id=\"label_sub\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"add document\" /> &nbsp; &nbsp;");
		writer.println("<a href=\"javascript:cancel()\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" /> Cancel</a>");
		writer.println("<noscript><br />Javascript is not enabled. To cancel, please click the 'X' at the top right corner of this window.</noscript>");
		writer.println("</td></tr></table>");

		writer.println("<table  border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		writer.println("<tr><td><span class=\"small\">Note: If posting material that may be used as training information for other customers, send to your IBM contact before posting.</span></td></tr></table>");
		writer.println("</form>");

		ETSUtils.popupHeaderRight(writer);
		writer.println(header.printPopupFooter());
	    }
	}
    	catch(Exception e) {
	    //writer.println("error occurred");
	    System.out.println("error here");
	}
    }



    private void delDoc(int docid, String projectid, int currentid, int topcatid, String action, String latest_uid, EdgeAccessCntrl es,HttpServletResponse response){
	try{
		PrintWriter writer = response.getWriter();
	    Vector p = ETSDatabaseManager.getProjects(es.gIR_USERN,projectid);
	    Vector breadcrumb = new Vector();

	    if (p.size() <= 0){
		//writer.println("error occurred: invalid project id for this user.");
		System.out.println("put bad projet id message here");
	    }
	    else{
		ETSProj proj  = (ETSProj)p.elementAt(0);

		ETSCat current_cat = ETSDatabaseManager.getCat(currentid);
		ETSDoc doc = ETSDatabaseManager.getDocByIdAndProject(docid,projectid);

		if (current_cat != null){
		    breadcrumb = getBreadcrumb(current_cat);

		    PopupHeaderFooter header;
		    header = new PopupHeaderFooter();
		    header.setPageTitle("E&TS Connect");
		    writer.println(header.printPopupHeader());

		    ETSUtils.popupHeaderLeft("Delete document",proj.getName(),writer);

		    printBreadcrumb(breadcrumb,writer);

		    writer.println("<script type=\"text/javascript\" language=\"javascript\">function cancel(){self.close();}</script>");

		    writer.println("<form action=\"ETSContentManagerServlet.wss\" method=\"get\" name=\"deldocForm\">");
		    if (action.equals("deldoc")){
			writer.println("<input type=\"hidden\" name=\"action\" id=\"label_action\" value=\"deldoc2\" />");
		    }
		    else{
			writer.println("<input type=\"hidden\" name=\"action\" value=\"delprevdoc2\" />");
			writer.println("<input type=\"hidden\" name=\"luid\" value=\""+latest_uid+"\" />");
		    }
		    writer.println("<input type=\"hidden\" name=\"proj\" value=\""+proj.getProjectId()+"\" />");
		    writer.println("<input type=\"hidden\" name=\"cc\" value=\""+current_cat.getId()+"\" />");
		    writer.println("<input type=\"hidden\" name=\"tc\" value=\""+topcatid+"\" />");
		    writer.println("<input type=\"hidden\" name=\"docid\" value=\""+docid+"\" />");

		    writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		    writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"20\" height=\"1\" alt=\"\" /></td>");
		    writer.println("<td>");

		    if (doc != null){
			writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			writer.println("<tr><td>You are about to delete the document <b>"+doc.getName()+"</b>. </td></tr>");

			if(doc.isLatestVersion() && doc.hasPreviousVersion()){
			    writer.println("<tr><td>");
			    writer.println("<br /> &nbsp; &nbsp; <input id=\"alldel\" type=\"checkbox\" name=\"alldel\" value=\"yes\" />");
			    writer.println("<label for=\"alldel\"> Delete all versions of this document</label>");
			    writer.println("</td></tr>");
			}

			writer.println("<tr><td><br /><span style=\"color:#ff3333\">This action can not be undone.</span></td></tr>");
			writer.println("</table>");
			writer.println("<br /><br />");

			writer.println("<table border=\"0\" cellspacing=\"2\" cellpadding=\"0\"><tr>");
			writer.println("<td><input type=\"image\" id=\"label_sub\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"delete document\" /></td>");
			writer.println("<td><a href=\"javascript:cancel()\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" />Cancel</a></td>");
			writer.println("</td></tr></table>");

			writer.println("<noscript><br />Javascript is not enabled. To cancel, please click the 'X' at the top right corner of this window.</noscript>");
		    }
		    else{
			writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td>");
			writer.println("The document you are trying to delete does not exist for the current project or an error has occurred.");
			writer.println("</td></tr></table>");
			writer.println("<br /><br />");

			writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr>");
			writer.println("<td><a href=\"javascript:cancel()\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" />Cancel</a></td>");
			writer.println("</td></tr></table>");

			writer.println("<noscript><br />Javascript is not enabled. To cancel, please click the 'X' at the top right corner of this window.</noscript>");
		    }

		    writer.println("</td></tr></table>");
		    writer.println("</form>");

		    ETSUtils.popupHeaderRight(writer);
		    writer.println(header.printPopupFooter());
		}
		else{
		    writer.println("error occurred: invalid cat id for this user.");
		    System.out.print("put bad current doc id message here");
		}
	    }
	}
	catch(Exception e) {
	    //writer.println("error occurred");
	    System.out.println("error here");
	}
    }

   private void updateDocProp(String projectid, int parentid, int docid, String msg, EdgeAccessCntrl es, HttpServletResponse response){
	try{
		PrintWriter writer = response.getWriter();

	    Vector p = ETSDatabaseManager.getProjects(es.gIR_USERN,projectid);
	    Vector breadcrumb = new Vector();

	    if (p.size() <= 0){
		writer.println("error occurred: invalid project id for this user.");
		System.out.println("put bad projet id message here");
	    }
	    else{
		ETSProj proj  = (ETSProj)p.elementAt(0);

		ETSCat parent_cat = ETSDatabaseManager.getCat(parentid);
		if (parent_cat != null){

		    breadcrumb = getBreadcrumb(parent_cat);

		    ETSDoc doc = ETSDatabaseManager.getDocByIdAndProject(docid,proj.getProjectId());

		    PopupHeaderFooter header;
		    header = new PopupHeaderFooter();
		    header.setPageTitle("E&TS Connect");
		    writer.println(header.printPopupHeader());

		    ETSUtils.popupHeaderLeft("Update document properties",proj.getName(),writer);

		    printBreadcrumb(breadcrumb,writer);

		    writer.println("<script type=\"text/javascript\" language=\"javascript\">function cancel(){self.close();}</script>");
		    writer.println("<form action=\"ETSContentManagerServlet.wss\" method=\"post\" name=\"updatedocpropForm\">");
		    if (!((msg.trim()).equals(""))){
			writer.println("<table><tr><td><span style=\"color:#ff3333\">"+msg+"</span></td></tr></table>");
		    }

		    writer.println("<input type=\"hidden\" name=\"action\" id=\"label_action\" value=\"updatedocprop2\" />");
		    writer.println("<input type=\"hidden\" name=\"proj\" id=\"label_proj\" value=\""+proj.getProjectId()+"\" />");
		    writer.println("<input type=\"hidden\" name=\"cc\" id=\"label_cc\" value=\""+parent_cat.getId()+"\" />");
		    writer.println("<input type=\"hidden\" name=\"docid\" id=\"label_docid\" value=\""+docid+"\" />");

		    writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr>");
		    writer.println("<td><span class=\"ast\"><b>*</b></span></td><td><span class=\"small\"> Denotes required fields</span></td>");
		    writer.println("</tr></table>");

		    writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		    writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"20\" height=\"1\" alt=\"\" /></td>");
		    writer.println("<td>");

		    if(doc != null){

			writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");

			//name
			writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
			writer.println("<td align=\"left\" width=\"99%\"><label for=\"docname\">Name:</label></td></tr>");
			writer.println("<tr><td colspan=\"2\"><input type=\"text\" id=\"docname\" size=\"30\" name=\"docname\" value=\""+doc.getName()+"\" /></td></tr>");

			//description
			writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");
			writer.println("<tr><td align=\"left\" width=\"1%\">&nbsp;</td>");
			writer.println("<td align=\"left\" width=\"99%\"><label for=\"docdesc\">Description (max. 1024 chars.)</label></td></tr>");
			writer.println("<tr><td colspan=\"2\"><textarea id=\"docdesc\" name=\"docdesc\" cols=\"30\" rows=\"3\" wrap=\"soft\" value=\"\">");
			writer.println(doc.getDescription());
			writer.println("</textarea></td></tr>");


			//keywords
			writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");
			writer.println("<tr><td align=\"left\" width=\"1%\">&nbsp;</td>");
			writer.println("<td align=\"left\" width=\"99%\"><label for=\"keywords\">Keywords:</label></td></tr>");
			writer.println("<tr><td colspan=\"2\"><input type=\"text\" id=\"keywords\" size=\"30\" name=\"keywords\" value=\""+doc.getKeywords()+"\" /></td></tr>");

			writer.println("</table>");
			/*
			writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr>");
			writer.println("<td><span class=\"ast\"><b>*</b></span></td><td><span class=\"small\"> Required fields</span></td>");
			writer.println("</tr></table><br />");
			*/
			writer.println("<br /><br />");
			writer.println("<input type=\"image\" id=\"label_sub\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"update document\" /> &nbsp; &nbsp; ");
		    }
		    else{

			writer.println("error occurred.  document not found.<br />");
		    }
		    writer.println("<a href=\"javascript:cancel()\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" /> Cancel</a>");
		    writer.println("<noscript><br />Javascript is not enabled. To cancel, please click the 'X' at the top right corner of this window.</noscript>");

		    writer.println("</td></tr></table>");
		    writer.println("</form>");

		    ETSUtils.popupHeaderRight(writer);
		     writer.println(header.printPopupFooter());
		}
		else{
		    writer.println("error occurred: invalid cat id for this user.");
		    System.out.print("put bad parent cat id message here");
		}
	    }
	}
	catch(Exception e) {
	    //writer.println("error occurred");
	    System.out.println("error here");
	}
    }


    private void updateDoc(String projectid, int parentid,int docid, String topcatid,String msg, EdgeAccessCntrl es, HttpServletResponse response){
	try{
		PrintWriter writer = response.getWriter();

	    Vector p = ETSDatabaseManager.getProjects(es.gIR_USERN,projectid);
	    Vector breadcrumb = new Vector();

	    if (p.size() <= 0){
		writer.println("error occurred: invalid project id for this user.");
		System.out.println("put bad projet id message here");
	    }
	    else{
		ETSProj proj  = (ETSProj)p.elementAt(0);

		ETSCat parent_cat = ETSDatabaseManager.getCat(parentid);
		if (parent_cat != null){

		    breadcrumb = getBreadcrumb(parent_cat);

		    PopupHeaderFooter header;
		    header = new PopupHeaderFooter();
		    header.setPageTitle("E&TS Connect");
		    writer.println(header.printPopupHeader());

		    ETSUtils.popupHeaderLeft("Update document",proj.getName(), writer);

		    printBreadcrumb(breadcrumb,writer);

		    writer.println("<script type=\"text/javascript\" language=\"javascript\">function cancel(){self.close();}</script>");

		    writer.println("<script type=\"text/javascript\" language=\"javascript\">");
		writer.println("function openMessage() {");
		writer.println("var upWin = open('"+Defines.SERVLET_PATH+"UploadProgress.jsp','wpWin','width=500,height=20,top=350,left=350')");
		writer.println("alert('Your document is about to be transferred.  Depending on the size of your file, this may take a few minutes.  Your window will not be updated until the document has finished uploading.')");
		writer.println("return (true);");
		writer.println("}</script>");

		writer.println("<form action=\"ETSContentManagerServlet.wss?action=updatedoc2&proj="+proj.getProjectId()+"&cc="+parent_cat.getId()+"&tc="+topcatid+"&docid="+docid+"\" method=\"post\" enctype=\"multipart/form-data\" name=\"updatedocForm\" onsubmit=\"return openMessage()\" >");
		    ETSDoc doc = ETSDatabaseManager.getDocByIdAndProject(docid,proj.getProjectId());
		    writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		    writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"20\" height=\"1\" alt=\"\" /></td>");
		    writer.println("<td>");

		    if(doc != null){
			if (!((msg.trim()).equals(""))){
			    writer.println("<table><tr><td><span style=\"color:#ff3333\">"+msg+"</span></td></tr></table>");
			}

			writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr>");
			writer.println("<td><span class=\"ast\"><b>*</b></span></td><td><span class=\"small\"> Denotes required fields</span></td>");
			writer.println("</tr></table>");


			writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"20\" height=\"1\" alt=\"\" /></td>");
			writer.println("<td>");


			writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");

			//name
			 writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
			 writer.println("<td align=\"left\" width=\"99%\"><label for=\"docname\">Name:</label></td></tr>");
			 writer.println("<tr><td colspan=\"2\"><input type=\"text\" id=\"docname\" size=\"30\" name=\"docname\" value=\""+doc.getName()+"\" /></td></tr>");

			//description
			writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"5\" alt=\"\" /></td></tr>");
			writer.println("<tr><td align=\"left\" width=\"1%\">&nbsp;</td>");
			writer.println("<td align=\"left\" width=\"99%\"><label for=\"docdesc\">Description (max. 1024 chars.)</label></td></tr>");
			writer.println("<tr><td colspan=\"2\"><textarea id=\"docdesc\" name=\"docdesc\" cols=\"30\" rows=\"3\" wrap=\"soft\" value=\"\">"+doc.getDescription()+"</textarea></td></tr>");


			//keywords
			writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"5\" alt=\"\" /></td></tr>");
			writer.println("<tr><td align=\"left\" width=\"1%\">&nbsp;</td>");
			writer.println("<td align=\"left\" width=\"99%\"><label for=\"keywords\">Keywords:</label></td></tr>");
			writer.println("<tr><td colspan=\"2\"><input type=\"text\" id=\"keywords\" size=\"30\" name=\"keywords\" value=\""+doc.getKeywords()+"\" /></td></tr>");


			//file
			writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"5\" alt=\"\" /></td></tr>");
			writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
			writer.println("<td align=\"left\" width=\"99%\"><label for=\"docfile\">File:</label></td></tr>");
			writer.println("<tr><td colspan=\"2\"><input type=\"file\" id=\"docfile\" size=\"30\" name=\"docfile\" value=\"\" /></td>");


			//notify option
			writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"5\" alt=\"\" /></td></tr>");
			writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
			writer.println("<td align=\"left\" width=\"99%\">Notify options:</td></tr>");

			writer.println("<tr><td colspan=\"2\"><table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			writer.println("<tr><td><input id=\"none\" type=\"radio\" name=\"notify\" value=\"none\" checked=\"checked\" /></td>");
			writer.println("<td align=\"left\"><label for=\"none\"> None</label>&nbsp;&nbsp;</td>");
			writer.println("<td>&nbsp;&nbsp;<input id=\"all\" type=\"radio\" name=\"notify\" value=\"all\" /></td>");
			writer.println("<td align=\"left\"><label for=\"all\"> All team members</label></td></tr>");
			writer.println("</table>");
			writer.println("</td></tr>");
			writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"5\" alt=\"\" /></td></tr>");
			writer.println("</table>");

			writer.println("<br />");


			writer.println("<input type=\"image\" id=\"label_sub\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"update document\" /> &nbsp; &nbsp; ");
			writer.println("<a href=\"javascript:cancel()\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" /> Cancel</a>");
			writer.println("<noscript><br />Javascript is not enabled. To cancel, please click the 'X' at the top right corner of this window.</noscript>");

			writer.println("</td></tr></table>");
		    }
		    else{
			writer.println("error occurred.  document not found to update<br />");
			writer.println("<a href=\"javascript:cancel()\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" />Close</a>");
		    }

		    writer.println("</td></tr></table>");
		    writer.println("</form>");

		    ETSUtils.popupHeaderRight(writer);
		    writer.println(header.printPopupFooter());
		}
		else{
		    writer.println("invalid cat id for this user.");
		    System.out.print("put bad parent cat id message here");
		}
	    }
	}
	catch(Exception e) {
	    System.out.println("error here");
	}
    }



	public String[] moveDoc3(String projId,String parentId,ETSCat cat, ETSDoc doc,EdgeAccessCntrl es){
		boolean success = false;
		char ibmonly = 'x';

		try{
			if (((doc.getIbmOnly()==Defines.ETS_PUBLIC) && cat.isIbmOnlyOrConf()) || ((doc.getIbmOnly()==Defines.ETS_IBM_ONLY) && (cat.getIbmOnly()==Defines.ETS_IBM_CONF))){
				if (cat.getIbmOnly() == Defines.ETS_IBM_ONLY){
					ibmonly = Defines.ETS_IBM_ONLY;
				}
				else{
					ibmonly = Defines.ETS_IBM_CONF;
				}
			}

			success = ETSDatabaseManager.updateParentId(Defines.NODE_DOC,doc.getId(),cat.getId(),ibmonly,projId,es.gIR_USERN);
		}
		catch(Exception e){
			e.printStackTrace();

		}

		if (success){
			return new String[]{"0",String.valueOf(ibmonly)};
		}
		else{
			return new String[]{"1","error"};
		}



	}



	public String[] moveCat4(String projId,String parentId,ETSCat movecat, ETSCat movetocat,EdgeAccessCntrl es){
		boolean success = false;
		char ibmonly = 'x';

		try{
			if (((movecat.getIbmOnly()==Defines.ETS_PUBLIC) && movetocat.isIbmOnlyOrConf()) || ((movecat.getIbmOnly()==Defines.ETS_IBM_ONLY) && (movetocat.getIbmOnly()==Defines.ETS_IBM_CONF))){
				if (movetocat.getIbmOnly() == Defines.ETS_IBM_ONLY){
					ibmonly = Defines.ETS_IBM_ONLY;
				}
				else{
					ibmonly = Defines.ETS_IBM_CONF;
				}
			}

			success = ETSDatabaseManager.updateParentId(Defines.NODE_CAT,movecat.getId(),movetocat.getId(),ibmonly,projId,es.gIR_USERN);
		}
		catch(Exception e){
			e.printStackTrace();

		}

		if (success){
			return new String[]{"0",String.valueOf(ibmonly)};
		}
		else{
			return new String[]{"1","error"};
		}



	}

	private String[] addDocComment2(String projectid,int docid,String currdocidStr,String comment,String notifyOptions,String notifyall,String[] sNotifyUsers,String topcat, String current,String linkid,Connection conn, EdgeAccessCntrl es, HttpServletResponse response){
	try{
		//Vector p = ETSDatabaseManager.getProjects(es.gIR_USERN,projectid);
		Vector p = new Vector();
		if ((ETSUtils.checkUserRole(es,projectid)).equals(Defines.ETS_ADMIN)){
			p = ETSDatabaseManager.getProject(projectid);
		}
		else{
			p = ETSDatabaseManager.getProjects(es.gIR_USERN,projectid);
		}

		if (p.size() <= 0){
			System.out.println("put bad project id message here");
			return new String[]{"1","3"};
		}
		else{
			ETSProj proj  = (ETSProj)p.elementAt(0);

			ETSDoc doc = ETSDatabaseManager.getDocByIdAndProject(docid,proj.getProjectId());
			Vector users = ETSDatabaseManager.getProjMembers(proj.getProjectId(),true);  //ETSUsers
			Vector notifyusers = new Vector();

			Vector user_temp = new Vector();
			for (int i = 0; i<users.size();i++){
					user_temp.addElement(((ETSUser)users.elementAt(i)).getUserId());
			}
			users = user_temp;
			if (doc.IsDPrivate()){
				users  = ETSDatabaseManager.getRestrictedProjMemberIds(proj.getProjectId(),doc.getId(),false);
			}
			if (doc.isIbmOnlyOrConf()) {
				users = getIBMMembers(users,conn);
			}

			if(!notifyall.equals("")){
				notifyusers = users;
			}
			else if (sNotifyUsers != null){
				for (int i = 0; i< sNotifyUsers.length; i++){
					String s = (String)sNotifyUsers[i];
					if (users.contains(s)){
						notifyusers.addElement(s);
					}
				}
			}

			ETSDocComment dc = new ETSDocComment();
			dc.setId(doc.getId());
			dc.setUserId(es.gIR_USERN);
			dc.setComment(comment);
			dc.setProjectId(proj.getProjectId());

			boolean success = ETSDatabaseManager.addDocComment(dc);

			if (success){
				if(notifyusers.size()>0){
					System.out.println("notopt="+notifyOptions);
					notifyDocCommUsers(notifyusers,comment,doc,currdocidStr,notifyOptions,proj.getName(),topcat,current,linkid,es,conn);
				}

				if (proj.getProjectOrProposal().equals("P")){
					Metrics.appLog(conn, es.gIR_USERN,"ETS_Project_Doc_Comment");
				}
				else{  //proposal
					Metrics.appLog(conn, es.gIR_USERN,"ETS_Proposal_Doc_Comment");
				}
				return new String[]{"0","success"};
			}
			else{
				return new String[]{"1","4"};
			}
		}
	}
	catch(Exception e) {
		//writer.println("error occurred");
		System.out.println("error here");
		e.printStackTrace();
		return new String[]{"1","5"};
	}
	}

	private void notifyDocCommUsers(Vector users,String comm,ETSDoc d,String currdocidStr,String notifyOpt,String projName,String topcat,String current,String linkid,EdgeAccessCntrl es,Connection conn){
		try{
			if (users.size() >0){
				SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
				java.util.Date date = new java.util.Date();
				String dateStr=df.format(date);

				AccessCntrlFuncs acf = new AccessCntrlFuncs();

				String emailids = "";
				StringBuffer message = new StringBuffer();

				message.append("\n\n");
				if(d.isLatestVersion()){
					message.append("A new comment was added to the document: \n");
				}
				else{
					message.append("A new comment was added to the previous version document: \n");
				}
				message.append(d.getName()+" \n\n");
				message.append("The details of the comment are as follows: \n\n");
				message.append("==============================================================\n");

				message.append("  User:           " + ETSUtils.formatEmailStr(es.gIR_USERN) + "\n");
				message.append("  Document name:  " + ETSUtils.formatEmailStr(d.getName()) + "\n");
				message.append("  Project name:   " + ETSUtils.formatEmailStr(projName) + " \n");
				message.append("  Date:           " + dateStr + " (mm/dd/yyyy)\n");
				message.append("  Comment:        " + ETSUtils.formatEmailStr(comm) + " \n\n");

				if (d.isIbmOnlyOrConf()){
					message.append("  This document is marked IBM Only\n\n");
				}

				message.append("To view this document, click on the following  URL:  \n");
				String url = Global.getUrl("ets/ETSProjectsServlet.wss")+"?action=details&proj="+d.getProjectId()+"&tc="+topcat+"&cc="+current+"&docid="+d.getId()+"&linkid="+linkid;
				if(!d.isLatestVersion()){
					url = Global.getUrl("ets/ETSProjectsServlet.wss")+"?action=prevdetails&proj="+d.getProjectId()+"&tc="+topcat+"&cc="+current+"&currdocid="+currdocidStr+"&docid="+d.getId()+"&linkid="+linkid;
				}
				message.append(url+"\n\n");

				message.append("==============================================================\n");
				message.append("Delivered by E&TS Connect.\n");
				message.append("This is a system generated email. \n");
				message.append("==============================================================\n\n");


				if (users.size() >0){
					for (int i = 0; i<users.size();i++){
						String memb = (String)users.elementAt(i);
						try{
							String userEmail = ETSUtils.getUserEmail(conn,memb);
							emailids = emailids + userEmail +",";
						}
						catch(AMTException ae){
							//writer.println("amt exception caught. e= "+ae);
						}
					}

					System.out.println("******* comment emailids ="+emailids);
					String subject = "E&TS Connect - New Comment for:"+ d.getName();
					subject = ETSUtils.formatEmailSubject(subject);

					String toList = "";
					String bccList = "";
					//emailids = "sandieps@us.ibm.com";

					if(notifyOpt.equals("bcc")){
						//System.out.println("*bcc");
						bccList = emailids;
					}
					else{
						//System.out.println("*to");
						toList = emailids;
					}

					boolean bSent = false;

					if (!toList.trim().equals("") || !bccList.trim().equals("")) {
						bSent = ETSUtils.sendEMail(es.gEMAIL,toList,"",bccList,Global.mailHost,message.toString(),subject,es.gEMAIL);
					}

					if (bSent){
						ETSDatabaseManager.addEmailLog("DocComment",String.valueOf(d.getId()),"Add doc comment",es.gIR_USERN,d.getProjectId(),subject,toList,"");
					}
					else{
						System.out.println("Error occurred while notifying project members.");
					}

				}
			}
		}
		catch (Exception e){

		}
	}

	private int verifyDate(String smonth,String sday,String syear){
		//date
		System.out.println("month="+smonth);
		System.out.println("day="+sday);
		System.out.println("year="+syear);

		if (smonth.equals("") || smonth.equals("-1")){
			return 1;
		}
		else if (sday.equals("") || sday.equals("-1")){
			return 1;
		}
		else if (syear.equals("") || syear.equals("-1")){
			return 1;
		}
		else{
			int month = Integer.parseInt(smonth.trim());
			int day = Integer.parseInt(sday.trim());
			int year = Integer.parseInt(syear.trim());

			Calendar today = Calendar.getInstance();
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR,year);
			cal.set(Calendar.MONTH,month);
			int iMaxDaysInMonth =  cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			int iMinDaysInMonth = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
			System.out.println("max  for '"+cal.get(Calendar.MONTH)+"'= "+iMaxDaysInMonth);
			System.out.println("min  for '"+cal.get(Calendar.MONTH)+"'= "+iMinDaysInMonth);

			if(iMinDaysInMonth<=day && day<=iMaxDaysInMonth){
				cal.set(Calendar.DAY_OF_MONTH,day);
			}
			else{
				return 1;
			}

			if (cal.before(today)){
				return 2;
			}
		}

		return 0;
	}



	private void printBlurb(String op, String field,boolean internal,UnbrandedProperties prop,HttpServletResponse response){
		try{
			
			PrintWriter writer = response.getWriter();
						
			PopupHeaderFooter header;
			header = new PopupHeaderFooter();
			header.setPageTitle(" "+ prop.getAppName() +" help");
			writer.println(header.printPopupHeader());


			ETSUtils.popupHeaderLeft(op,field,writer);

			writer.println("<script type=\"text/javascript\" language=\"javascript\">function cancel(){self.close();}</script>");

			writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"20\" height=\"1\" alt=\"\" /></td>");
			writer.println("<td>");
			writer.println("<table cellspacting=\"0\" cellpadding=\"0\" border=\"0\">");
			if (op.equals("Documents")){
				if (field.equals("Name")){
					writer.println("<tr><td>The name of a document will be displayed as a link when viewing the folder in which the " +						"document resides.  The name of a document should be 1-128 characters long and be descriptive enough for users to " +						"have an idea of the content of this document.</td></tr>");
					writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");
					writer.println("<tr><td>This field will also influence search results.</td></tr>");
				}
				else if (field.equals("Description")){
					writer.println("<tr><td>The description of a document will be displayed when viewing the details of a document. " +
							"The description should contain any additional information for users to have " +
							"an idea of the content of this document. </td></tr>");
					writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");
					writer.println("<tr><td>This field will also influence search results.</td></tr>");
				}
				else if (field.equals("Keywords")){
					writer.println("<tr><td>The keywords of a document will be displayed when viewing the details of a document. " +
							"The keywords should be 0-512 characters long and contain any additional information for users to have " +
							"an idea of the content of this document.</td></tr>");
					writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");
					writer.println("<tr><td>This field will also influence search results.</td></tr>");
				}
				else if (field.equals("Security")){
					writer.println("<tr><td class=\"subtitle\">Security classification details</td></tr>");
					writer.println("<tr><td><b>Level 1 - </b>\"All team members\": Available to all workspace members.  The IBM author" +						" or workspace owner/manager can change the level as needed.</td></tr>");
					writer.println("<tr><td><b>Level 2 - </b>\"All IBM team members\": Available to IBM team members only.  The IBM author" +
						" or workspace owner/manager can change the level as needed.</td></tr>");
					writer.println("<tr><td><b>Level 3 - </b>\"All IBM team members permanently\": Available to IBM workspace members only." +						"  Remains Level 3 permanently, and cannot be changed.</td></tr>");
				}
				else if (field.equals("Additional Editors")){
						writer.println("<tr><td>You can select additional editors to give full access to this document. " +
								"The available users/groups for additional editors are limited by the security classification " +
								"selection for the document.</td></tr>");
						writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");
						writer.println("<tr><td>Workspace owner and the author of the document are default editors.  Members of the " +
								"workspace can only be additional editors, no visitors are allowed to have edit privilege.</td></tr>");
				}
				else if (field.equals("Restrict Users")){
					if (internal){
						writer.println("<tr><td>Access to documents can be restricted further, in addition to the security classification. " +
								"Within a given security classification, the author can further restrict access to a subset of the team. " +
								"For example, access to a document with \"All IBM team members\" security classification can be further restricted to " +
								"just one IBM team member. Other IBM team members will not see this document, except for the workspace owner. " +
								"Another possible use is to restrict access to a limited number of client and IBM team members. " +
								"The security classification for this scenario would be \"All team members\".</td></tr>");
						writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");
						writer.println("<tr><td>In addition to the folder/document owner, workspace owner has the authority " +
							"to modify security classification and access list.</td></tr>");
					}
					else{
						writer.println("<tr><td>Access to documents can be restricted by the author to a select list " +
							"of workspace members.  In addition to the document owner, the IBM workspace owner has the authority " +
							"to modify the access list.</td></tr>");
					}
				}
				else if (field.equals("Notification option")){
					writer.println("<tr><td>Document notifications are sent via internet e-mail, with the e-mail addresses " +						"listed in the \"To\" field of the e-mail header.  If you choose \"Bcc\" as the e-mail notification " +						"option, then all e-mail addresses are listed in the blind copy (Bcc) field of the e-mail header, " +						"thus, hiding the list of notification recipients.</td></tr>");

				}
			}
			else if (op.equalsIgnoreCase("Contracts")) {
			    if (field.equals("Import Tasks")) {
					writer.println("<tr><td>");
					writer.println("The file to be imported must be in CSV (Comma Separated) format. The first line in the file defines the header and must be as follows:<br /><br />");
					writer.println("Task ID,Title,IBM Only (Y/N),Due date (MMDDYYYY),Status,Owner email,Company,Action required<br /><br />");
					writer.println("The CSV file must have the following columns:<br /><br />");
					writer.println("Task ID (leave blank if creating a new task)<br />");
					writer.println("Title<br />");
					writer.println("IBM Only (Y/N)<br />");
					writer.println("Due Date (MMDDYYYY)<br />"); 
					writer.println("Status (Not Started / In Progress / Complete)<br />");
					writer.println("Owner (email)<br /> ");
					writer.println("Action required (free text)<br /><br />");
					writer.println("For Task ID, leave it blank for new tasks, otherwise it has to match the existing Task ID in the system for updating that task.");
					writer.println("</td></tr>");
			    }
			    else if (field.equals("Export Tasks")) {
					writer.println("<tr><td>");
					writer.println("Select one of the following options:<br /><br />");
					writer.println("Template only - to download a CSV template which can be used to update existing tasks OR add new tasks<br /><br />");
					writer.println("All tasks - to download the template along with all existing tasks in the workspace<br /><br />");
					writer.println("Tasks by status - to download the template along with tasks filtered by any of the following status:<br /><br />");					
					writer.println("<ul>");
					writer.println("<li>Not Started</li>");
					writer.println("<li>In Progress</li>");
					writer.println("<li>Complete</li>");
					writer.println("</ul>");
					writer.println("</td></tr>");
			    }
			}
			else if(op.equalsIgnoreCase("Notification help")){
				writer.println("<tr><td>Notifications are sent via internet e-mail, with the e-mail addresses " +
						"listed in the \"To\" field of the e-mail header.  If you choose \"Bcc\" as the e-mail notification " +
						"option, then all e-mail addresses are listed in the blind copy (Bcc) field of the e-mail header, " +
						"thus, hiding the list of notification recipients.</td></tr>");
			}else if (op.equalsIgnoreCase("Groups")) {
			  
				if (field.equals("Security Classification")){
					//writer.println("<tr><td class=\"subtitle\">Security classification details</td></tr>");
					//writer.println("<tr><td><br /></td></tr>");
				     writer.println("<tr><td><b>Any team member : </b> Any one from the workspace team can be part of this group. " +
				      "A group with this classification can be accessed by all in the workspace.</td></tr>");
				     writer.println("<tr><td><br /></td></tr>");
				     writer.println("<tr><td><b>IBM team members : </b> Only IBM internal personnel can be part of this group. " +
				      "A group with this classification is available to IBM internals only (in the workspace).</td></tr>");

			  }else if (field.equals("Group Type")){
			     //writer.println("<tr><td class=\"subtitle\">Group Type details</td></tr>");
			     //writer.println("<tr><td><br /></td></tr>");
			     writer.println("<tr><td><b>Public : </b> Public groups can only be created/edited by workspace owner or manager. " +
			      "These groups are available to use for all in the workspace.</td></tr>");
			     writer.println("<tr><td><br /></td></tr>");
			     writer.println("<tr><td><b>Private : </b> Any member of the workspace can create a private group (including workspace owner and manager). " +
			      "These groups can be used by the members who are part of such group(s). " +
			      "Workspace owner and manager can always access all the private groups.</td></tr>");
			   }
			} else if (op.equalsIgnoreCase("Add Member")) {
				
			    if (field.equals("Import csv file")){
			     writer.println("<tr><td colspan=\"2\">New members can be added to the workspace by importing a CSV file. This CSV file must adhere to the format below <br />which can be obtained from the \"Download add member template\" link.</td></tr>");
			     writer.println("<tr><td><br /></td></tr>");
			     writer.println("<tr><td width=\"100\">&nbsp;&nbsp;&nbsp;<b>Name</b>(optional)</td><td>This column must exist but the name does not need to be provided for each member.</td></tr>");
			     writer.println("<tr><td><br /></td></tr>");
			     writer.println("<tr><td width=\"100\">&nbsp;&nbsp;&nbsp;<b>User Id</b>(required)<br /><b>User Email</b>(required)</td><td>For IBMers the ID is their internet email. Externals will most likely have their email as their ID.</td></tr>");
			     writer.println("<tr><td><br /></td></tr>");
			     writer.println("<tr><td width=\"100\">&nbsp;&nbsp;&nbsp;<b>Access Level</b>(required) </td><td>Must be one of the following: Workspace Manager, Member, Visitor, (client voice workspaces can also use Client).</td></tr>");
			     writer.println("<tr><td><br /></td></tr>");
			     writer.println("<tr><td width=\"100\">&nbsp;&nbsp;&nbsp;<b>Job Responsibility</b>(optional) </td><td>free text that describes their role on the team.</td></tr>");
			     writer.println("<tr><td><br /></td></tr>");
			     writer.println("<tr><td width=\"100\">&nbsp;&nbsp;&nbsp;<b>Messenger Id</b>(optional) </td><td>free text of their instant messager ID.</td></tr>");
			    }
			   
			    if (field.equals("Workspace privileges")){
			         writer.println("<tr><td><b>1.Workspace Manager :</b>&nbsp;Privileges: ability to add, update, and delete own folders and documents; ability to update folders and documents owned by another member; ability to add and delete team members, ability to manage team member roles; ability to delete folders and documents owned by another member; can only be assigned to internal IBMers.<br /></td></tr>");
				     writer.println("<tr><td>&nbsp;<br /></td></tr>");
				     writer.println("<tr><td><b>2.Member :</b>&nbsp;Privileges: ability to add, update, and delete own folders and documents <br /></td></tr>"); 
				     writer.println("<tr><td>&nbsp;<br /></td></tr>");
				     writer.println("<tr><td><b>3.Visitor :</b>&nbsp;Privileges: Read-only access<br /></td></tr>"); 
				}
			    
			    if (field.equals("View invitaion email")) {
			    
			    	 ResourceBundle resBundle = ResourceBundle.getBundle("oem.edge.common.gwa");
					 String selfRegistrationURL = resBundle.getString("selfRegistrationURL");
					 selfRegistrationURL = selfRegistrationURL.substring(0, selfRegistrationURL.indexOf("&okurl"));
			    	 String loginURL = Global.WebRoot + "/" + "index.jsp";
			    	 
				     writer.println("<tr><td>I would like you to join a workspace for 'Workspace Name' on IBM 'Application Name' web portal. We will use this workspace  to collaborate and share information securely.<br /></td></tr>");
				     writer.println("<tr><td>Please follow these two steps:<br /></td></tr>");
				     writer.println("<tr><td>---------------------------------------------------------------------------------------<br /></td></tr>");
				     writer.println("<tr><td><b>STEP #1: Create an IBM ID and password</b><br /></td></tr>");
				     writer.println("<tr><td>---------------------------------------------------------------------------------------<br /></td></tr>");
				     writer.println("<tr><td>Click and follow URL to register your ID of 'User Email ID' . Make sure you fill your \"company address\" completely as it will be required for access to the workspace.<br /></td></tr>");
				     writer.println("<tr><td>" + selfRegistrationURL + "<br /></td></tr>");
				     writer.println("<tr><td>---------------------------------------------------------------------------------------<br /></td></tr>");
				     writer.println("<tr><td><b>STEP #2: Log in to initiate access to the workspace</b><br /></td></tr>");
				     writer.println("<tr><td>---------------------------------------------------------------------------------------<br /></td></tr>");
				     writer.println("<tr><td>Click on the link below and login with your newly created IBM ID and password. You do not need to do anything further,the system will initiate the final processing based on your  login. Your access will be complete approximately 2 hours  after you have logged in.<br /></td></tr>");
				     writer.println("<tr><td>" + loginURL + "<br /></td></tr>");
				     writer.println("<tr><td>If you run into any difficulty, you can contact me or our 24x7 help desk at 'Help Desk Phone No' for US & Canada or 'Help Desk Phone No' for international users.<br /></td></tr>");
				     writer.println("<tr><td>&nbsp;<br /></td></tr>");
				     writer.println("<tr><td><b>Registration information&nbsp;</b><br /></td></tr>");
				     writer.println("<tr><td>IBM ID:&nbsp;User IBM ID<br /></td></tr>");
				     writer.println("<tr><td>Project or proposal:&nbsp;Workspace Name<br /></td></tr>");
				     writer.println("<tr><td>Country:&nbsp;User Country<br /></td></tr>");
				     writer.println("<tr><td>Company:&nbsp;User Company<br /></td></tr>");
				     writer.println("<tr><td>Privilege:&nbsp;User Privilage<br /></td></tr>");
				     writer.println("<tr><td>E-mail address of IBM contact:&nbsp;Email of IBM Contact<br /></td></tr>");
				     
				}
			    
			    if (field.equals("Incomplete profile")) {
				    				    	 
				    	 String loginURL = Global.WebRoot + "/" + "index.jsp";
				    	 
					     writer.println("<tr><td>This is to inform you that your profile is found to be Incomplete.<br /></td></tr>");
					     writer.println("<tr><td>---------------------------------------------------------------------------------------<br /></td></tr>");
					     writer.println("<tr><td>Click on the link below and login with your IBM ID and password. Make sure you fill your \"Address\" completely as it will be required for access to the workspace.<br /></td></tr>");
					     writer.println("<tr><td>" + loginURL + "<br /></td></tr>");
					     writer.println("<tr><td>---------------------------------------------------------------------------------------<br /></td></tr>");
					     writer.println("<tr><td>If you run into any difficulty, you can contact me or our 24x7 help desk at 'Help Desk Phone No' for US & Canada or 'Help Desk Phone No' for international users.<br /></td></tr>");
					     writer.println("<tr><td>&nbsp;<br /></td></tr>");
					     writer.println("<tr><td><b>Your profile information&nbsp;</b><br /></td></tr>");
					     writer.println("<tr><td>---------------------------------------------------------------------------------------<br /></td></tr>");
					     writer.println("<tr><td>IBM ID:&nbsp;User IBM ID<br /></td></tr>");
					     writer.println("<tr><td>Project or proposal:&nbsp;Workspace Name<br /></td></tr>");
					     writer.println("<tr><td>Country:&nbsp;User Country<br /></td></tr>");
					     writer.println("<tr><td>Company:&nbsp;User Company<br /></td></tr>");
					     writer.println("<tr><td>Privilege:&nbsp;User Privilage<br /></td></tr>");
					     writer.println("<tr><td>Address:&nbsp;User Address<br /></td></tr>"); 
					     writer.println("<tr><td>E-mail address of IBM contact:&nbsp;Email of IBM Contact<br /></td></tr>");
					     
					}
			}
		
	
			writer.println("</table>");

			writer.println("<noscript><br />Javascript is not enabled. To cancel, please click the 'X' at the top right corner of this window.</noscript>");

			writer.println("</td></tr></table");
			writer.println("</form>");

			ETSUtils.popupHeaderRight(writer);
			writer.println(header.printPopupFooter());
		}
		catch(Exception e) {
			//writer.println("error occurred");
			System.out.println("error here = "+e);
		}

	}


	private Vector getIBMMembers(Vector membs, Connection conn) {
		Vector new_members = new Vector();

		for (int i = 0; i < membs.size(); i++) {
			String mem = (String)membs.elementAt(i);
			try {
				String edge_userid = AccessCntrlFuncs.getEdgeUserId(conn, mem);
				String decaftype = AccessCntrlFuncs.decafType(edge_userid, conn);
				if (decaftype.equals("I")) {
					new_members.addElement(mem);
				}
			}
			catch (AMTException a) {
				System.out.println("amt exception in getibmmembers err= " + a);
			}
			catch (SQLException s) {
				System.out.println("sql exception in getibmmembers err= " + s);
			}
		}

		return new_members;
	}

	private Vector getIBMMembers(String[] membs, Connection conn) {
		Vector new_members = new Vector();
		if (membs == null)
			return new_members;

		for (int i = 0; i < membs.length; i++) {
			String mem = (String)membs[i];
			try {
				String edge_userid = AccessCntrlFuncs.getEdgeUserId(conn, mem);
				String decaftype = AccessCntrlFuncs.decafType(edge_userid, conn);
				if (decaftype.equals("I")) {
					new_members.addElement(mem);
				}
			}
			catch (AMTException a) {
				System.out.println("amt exception in getibmmembers err= " + a);
			}
			catch (SQLException s) {
				System.out.println("sql exception in getibmmembers err= " + s);
			}
		}

		return new_members;
	}
	private Vector getVector(String[] membs) {
		Vector new_members = new Vector();

		if (membs == null)
			return new_members;

		for (int i = 0; i < membs.length; i++) {
			String mem = (String)membs[i];
			new_members.addElement(mem);
		}

		return new_members;
	}


}






