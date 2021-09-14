/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
/*                                                                          */
/*     All Rights Reserved                                                  */
/*     US Government Users Restricted Rights                                 */
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

import oem.edge.common.*;

import java.io.*;
import java.util.*;
import java.sql.*;
import java.text.*;

import javax.servlet.*;
import javax.servlet.http.*;

import java.net.URLEncoder;

import oem.edge.amt.*;
import oem.edge.ets.fe.common.EncodeUtils;
import oem.edge.ets.fe.documents.DocumentsHelper;
import oem.edge.ets.fe.documents.common.DocMessages;
import oem.edge.ets.fe.pmo.ETSPMODao;
import oem.edge.ets.fe.pmo.ETSPMODoc;
import oem.edge.ets.fe.pmo.ETSPMOffice;

public class ETSDocumentManager {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.54";

	protected ETSParams Params;
	protected ETSProj Project;
	protected int TopCatId;
	protected String linkid;
	protected Connection conn;
	protected EdgeAccessCntrl es;
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected PrintWriter writer;
	protected boolean isSuperAdmin = false;
	protected boolean isExecutive = false;
	protected String userRole;

	protected ETSDatabaseManager databaseManager;
	protected int CurrentCatId;
	protected ETSCat this_current_cat;
	
	public ETSDocumentManager(ETSParams parameters) {
		this.Params = parameters;
		this.Project = parameters.getETSProj();
		this.TopCatId = parameters.getTopCat();
		this.linkid = parameters.getLinkId();
		this.conn = parameters.getConnection();
		this.es = parameters.getEdgeAccessCntrl();
		this.request = parameters.getRequest();
		this.response = parameters.getResponse();
		this.writer = parameters.getWriter();
		this.isSuperAdmin = parameters.isSuperAdmin();
		this.isExecutive = parameters.isExecutive();

		this.databaseManager = new ETSDatabaseManager();
		String currentCatIdStr = getParameter(request, "cc");
		if (!currentCatIdStr.equals("")) {
			this.CurrentCatId = (new Integer(currentCatIdStr)).intValue();
		}
		else {
			this.CurrentCatId = TopCatId;
		}

	}

	public void ETSDocumentHandler() {

		AccessCntrlFuncs acf = new AccessCntrlFuncs();
		String action = getParameter(request, "action");

		try {
			this_current_cat = ETSDatabaseManager.getCat(CurrentCatId, Project.getProjectId());
			this.userRole = ETSUtils.checkUserRole(es, Project.getProjectId());
		}
		catch (Exception e) {
			this_current_cat = null;
		}
		if (this_current_cat == null) {
			printHeader("Error", false);
			writer.println(
				"<br />Invalid folder: current category not valid for this project");
			return;
		}

		boolean user_external = false;
		if (!(es.gDECAFTYPE.trim()).equalsIgnoreCase("I")) {
			user_external = true;
		}

		if (user_external && (this_current_cat.isIbmOnlyOrConf())) {
			printHeader("Error", false);
			writer.println("<br />You are not authorized to view this folder.");
			return;
		}

		/*
		if (this_current_cat.IsCPrivate()) {
			Vector resusers = new Vector();
			try{
				resusers = ETSDatabaseManager.getRestrictedProjMemberIds(Project.getProjectId(),this_current_cat.getId(),true);
			}
			catch(Exception e){
				
			}
			if(!(isSuperAdmin || isExecutive || this_current_cat.getUserId().equals(es.gIR_USERN) || resusers.contains(es.gIR_USERN))){
				printHeader("Error", false);
				writer.println("<br />You are not authorized to view this folder.");
				return;
			}
		}
		*/

		if (!action.equals("")) {
			if (action.equals("details")) {
				String header = getBreadCrumbTrail(this_current_cat, false);
				printHeader(header, false);
				writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");

				String docidStr = getParameter(request, "docid");
				if (!docidStr.equals("")) {
					int docid = (new Integer(docidStr)).intValue();
					try {
						ETSDoc doc =ETSDatabaseManager.getDocByIdAndProject(docid,Project.getProjectId());
						if (doc != null) {
							if (doc.isIbmOnlyOrConf() && user_external) {
								writer.println("You are not authorized to view this document");
								return;
							}
							
							Vector resUsers = new Vector();
							boolean authorized = false;
							
							if (doc.IsDPrivate()){
								resUsers = ETSDatabaseManager.getRestrictedProjMembers(Project.getProjectId(),doc.getId(),false,false);
								if (!ETSDocCommon.isAuthorized(doc.getUserId(),userRole,isSuperAdmin,isExecutive,resUsers,true,es.gIR_USERN)){
									writer.println("You are not authorized to view this document.");
									return;
								}
							}
							
							
							displayDocDetails(doc, resUsers,acf);
							/*
							if (doc.getDocStatus()==Defines.DOC_DRAFT){
								printDraftDocActionButtons(doc);
							}
							else if (doc.getDocStatus()==Defines.DOC_SUB_APP){
								printSubAppDocActionButtons(doc);
							}
							else{
								printDocActionButtons(doc);
							}
							*/
							//printDocActionButtons(doc);
						}
						else {
							writer.println(
								"invalid url: invalid docid for this project");
						}
					}
					catch (SQLException se) {
						//System.out.println("SQLEx in DocMan getDocByIdAndProject(docid,projid)="+ se);
						writer.println("Error occurred:SQLEx in DocMan getDocByIdAndProject(docid,projid)");
					}
					catch (Exception e) {
						//System.out.println("Ex in DocMan getDocByIdAndProject(docid,projid)="+ e);
						writer.println("Error occurred:Ex in DocMan getDocByIdAndProject(docid,projid)");
					}
				}
				else {
					writer.println("invalid url: no docid");
				}
			}
			else if (action.equals("prev")) {
				String header = getBreadCrumbTrail(this_current_cat, false);
				printHeader(header, false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");

				String docidStr = getParameter(request, "docid");
				if (!docidStr.equals("")) {
					int docid = (new Integer(docidStr)).intValue();
					try {
						ETSDoc doc =ETSDatabaseManager.getDocByIdAndProject(docid,Project.getProjectId());
						if (doc != null) {
							if (doc.isIbmOnlyOrConf() && user_external) {
								writer.println("You are not authorized to view this document");
								return;
							}
							
							if(doc.IsDPrivate()){
								//Vector resusers = ETSDatabaseManager.getRestrictedProjMemberIds(Project.getProjectId(),doc.getId(),false);
								Vector resusers = ETSDatabaseManager.getRestrictedProjMembers(Project.getProjectId(),doc.getId(),false,false);
								//if(!resusers.contains(es.gIR_USERN) || !isSuperAdmin || !userRole.equals(Defines.WORKSPACE_OWNER)){
								if (!ETSDocCommon.isAuthorized(doc.getUserId(),userRole,isSuperAdmin,isExecutive,resusers,true,es.gIR_USERN)){
									writer.println("You are not authorized to view this document");
									return;
								}
							}

							Vector prev_docs = ETSDatabaseManager.getPreviousVersions(docid);
							if (prev_docs != null) {
								displayPreviousDocs(prev_docs, doc,acf);
							}
							else {
								writer.println("no previous versions for this document");
							}
						}
						else {
							writer.println("invalid url: invalid docid");
						}
					}
					catch (SQLException se) {
						//System.out.println("SQLEx in DocMan action_prev=" + se);
						writer.println("Error occurred:SQLEx in DocMan previous doc view");
					}
					catch (Exception e) {
						//System.out.println("Ex in DocMan action_prev=" + e);
						e.printStackTrace();
						writer.println("Error occurred:Ex in DocMan previous doc view");
					}
				}
				else {
					writer.println("invalid url: no docid");
				}
			}
			else if (action.equals("prevdetails")) {
				String header = getBreadCrumbTrail(this_current_cat, false);
				printHeader(header, false);
				writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");

				String docidStr = getParameter(request, "docid");
				String currdocid = getParameter(request, "currdocid");
				if (!docidStr.equals("")) {
					if (!currdocid.equals("")) {
						int docid = (new Integer(docidStr)).intValue();
						try {
							ETSDoc doc =ETSDatabaseManager.getDocByIdAndProject(docid,Project.getProjectId());
							if (doc != null) {
								if (doc.isIbmOnlyOrConf() && user_external) {
									writer.println("You are not authorized to view this document");
									return;
								}
								
								Vector resusers = new Vector();
								if (doc.IsDPrivate()){
									resusers = ETSDatabaseManager.getRestrictedProjMembers(Project.getProjectId(),doc.getId(),false,false);
									if (!ETSDocCommon.isAuthorized(doc.getUserId(),userRole,isSuperAdmin,isExecutive,resusers,true,es.gIR_USERN)){
										writer.println("You are not authorized to view this document.");
										return;
									}
								}
								displayPreviousDocDetails(doc, currdocid, resusers,acf);
								//printPrevDocActionButtons(doc, currdocid);
							}
							else {
								writer.println("invalid url: invalid docid");
							}
						}
						catch (SQLException se) {
							//System.out.println("SQLEx in DocMan action_prevdet=" + se);
							writer.println("Error occurred:SQLEx in DocMan previous doc details view");
						}
						catch (Exception e) {
							//System.out.println("Ex in DocMan action_prevdet=" + e);
							writer.println("Error occurred:Ex in DocMan previous doc details view");
						}
					}
					else {
						writer.println("invalid url: no current docid");
					}
				}
				else {
					writer.println("invalid url: no docid");
				}
			}
			else if (action.equals("addcat")) {
				try {
					String msg = getParameter(request, "msg");
					doAddCat(msg);
				}
				catch (Exception i) {
					//System.out.println("error in docman addcat=" + i);
				}
			}
			else if (action.equals("delcat")) {
				try {
					String msg = getParameter(request, "msg");
					doDelCat(msg);
				}
				catch (Exception i) {
					//System.out.println("error in docman delcat=" + i);
				}
			}
			else if (action.equals("updatecat")) {
				try {
					String msg = getParameter(request, "msg");
					doUpdateCat(msg);
				}
				catch (Exception i) {
					//System.out.println("error in docman updatecat=" + i);
				}
			}
			else if (action.equals("updatecatA")) {
				try {
					String msg = getParameter(request, "msg");
					String upcatid = getParameter(request, "updatecatid");
					doUpdateCatA(upcatid, msg);
				}
				catch (Exception i) {
					//System.out.println("error in docman updatecat=" + i);
				}
			}
			else if (action.equals("updatecatc")) {
				try {
					String upcatid = getParameter(request, "updatecatid");
					String name = getParameter(request, "name");
					String ibmonly = getParameter(request, "ibmonly");
					String opt = getParameter(request, "opt");

					doUpdateCatC(upcatid, name, ibmonly, opt);
				}
				catch (Exception i) {
					//System.out.println("error in docman updatecat=" + i);
				}
			}
			else if (
				action.equals("adddoc")
					|| action.equals("addmeetingdoc")
					|| action.equals("addprojectplan")
					|| action.equals("addtaskdoc")
					|| action.equals("addactionplan")) {
				try {
					boolean reg_doc = false;
					int parentId;
					String meeting_id = "";
					String repeat_id = "";
					String self_id = "";
					String set_id = "";

					if (action.equals("addmeetingdoc")) {
						parentId = -2;
						meeting_id = getParameter(request, "meetingid");
						repeat_id = getParameter(request, "repeatid");
					}
					else if (action.equals("addprojectplan")) {
						parentId = -1;
						meeting_id = "";
					}
					else if (action.equals("addtaskdoc")) {
						parentId = -3;
						meeting_id = getParameter(request, "taskid");
						self_id = getParameter(request,"self");
						set_id = getParameter(request,"set");
					}
					else if (action.equals("addactionplan")) {
						parentId = -4;
						meeting_id = getParameter(request, "setmet");
						;
					}
					else { //doc
						parentId = (new Integer(CurrentCatId)).intValue();
						reg_doc = true;
						meeting_id = "";
					}

					String msg = getParameter(request, "msg");
					/*
					if (reg_doc) doAddRegDoc(parentId,meeting_id,msg);
					else doAddDoc(action,parentId,meeting_id,msg);
					*/
					doAddDoc(action, parentId, meeting_id, repeat_id, self_id,set_id,msg);

				}
				catch (Exception i) {
					//System.out.println("error in docman updatecat=" + i);
				}
			}
			else if (action.equals("adddoc2")) {
				try {
					int parentId;
					String meeting_id = "";

					parentId = (new Integer(CurrentCatId)).intValue();
					String name = getParameter(request, "docname");
					String desc = getParameter(request, "docdesc");
					String keywords = getParameter(request, "keywords");
					String options = getParameter(request, "options");

					if (name.equals("")
						|| name.length() <= 0
						|| name.length() > 128) {
						String msg = "Name must be 1-128 characters long";
						doAddRegDoc(parentId, meeting_id, msg);
					}
					else if (keywords.length() > 500) {
						String msg = "Keywords must be 0-500 characters long";
						doAddRegDoc(parentId, meeting_id, msg);
					}
					else if (desc.length() > 500) {
						String msg =
							"Description must be 0-1024 characters long";
						doAddRegDoc(parentId, meeting_id, msg);
					}
					else if (options.equals("")) {
						String msg = "Option must be chosen";
						doAddRegDoc(parentId, meeting_id, msg);
					}
					else {
						doAddRegDoc2(
							parentId,
							meeting_id,
							name,
							desc,
							keywords,
							options,
							"");
					}
				}
				catch (Exception i) {
					//System.out.println("error in docman updatecat=" + i);
				}
			}
			else if (action.equals("deldoc") || action.equals("delprevdoc")) {
				String docidStr = getParameter(request, "docid");
				String msg = getParameter(request, "msg");

				if (docidStr.equals("")) {
					//System.out.println("put error with current doc id message here");
				}
				else {
					int docid = (new Integer(docidStr)).intValue();
					String latest_uid = getParameter(request, "luid");
					doDelDoc(docid, action, latest_uid, msg);
				}
			}
			else if (action.equals("updatedocprop")) {
				String docidStr = getParameter(request, "docid");
				String msg = getParameter(request, "msg");

				if (docidStr.equals("")) {
					//System.out.println("put error with current doc id message here");
				}
				else {
					int docid = (new Integer(docidStr)).intValue();
					doUpdateDocProp(docid, msg);
				}
			}
			else if (action.equals("updatedocpropc")) {
				String docidStr = getParameter(request, "docid");
				String name = (String) request.getSession(true).getAttribute("ETSDocName");
				String desc = (String) request.getSession(true).getAttribute("ETSDocDesc");
				String keywords = (String) request.getSession(true).getAttribute("ETSDocKeywords");
				String ibmonly = (String) request.getSession(true).getAttribute("ETSDocIbmOnly");
				String exdate = (String) request.getSession(true).getAttribute("ETSDocExDate");
				String exmonth = (String) request.getSession(true).getAttribute("ETSDocExMonth");
				String exday = (String) request.getSession(true).getAttribute("ETSDocExDay");
				String exyear = (String) request.getSession(true).getAttribute("ETSDocExYear");
				String chusers = (String) request.getSession(true).getAttribute("ETSChUsers");
				Vector resusers = (Vector)request.getSession(true).getAttribute("ETSResUsers");

				if (docidStr.equals("")) {
					//System.out.println("put error with current doc id message here");
				}
				else {
					int docid = (new Integer(docidStr)).intValue();
					doUpdateDocPropC(docid,name,desc,keywords,ibmonly,exdate,exmonth,exday,exyear,chusers,resusers);
				}
			}
			else if (
				action.equals("updatedoc") || action.equals("replacedoc")) {
				String olddocidStr = getParameter(request, "docid");
				String msg = getParameter(request, "msg");
				boolean replace = false;

				if (olddocidStr.equals("")) {
					//System.out.println("put error with current doc id message here");
				}
				else {
					int olddocid = (new Integer(olddocidStr)).intValue();
					if (action.equals("replacedoc"))
						replace = true;
					doUpdateDoc(olddocid, replace, msg);
				}
			}
			else if (
				action.equals("updatedoc2") || action.equals("replacedoc2")) {
				String olddocidStr = getParameter(request, "docid");
				String msg = getParameter(request, "msg");
				String name = getParameter(request, "docname");
				String desc = getParameter(request, "docdesc");
				String keywords = getParameter(request, "keywords");
				String options = getParameter(request, "options");
				boolean replace = false;
				if (action.equals("replacedoc2"))
					replace = true;

				if (olddocidStr.equals("")) {
					//System.out.println("put error with current doc id message here");
					writer.println("Error with document id");
					return;
				}

				int olddocid = (new Integer(olddocidStr)).intValue();

				if (name.equals("")
					|| name.length() <= 0
					|| name.length() > 128) {
					msg = "Name must be 1-128 characters long";
					doUpdateDoc(olddocid, replace, msg);
				}
				else if (keywords.length() > 500) {
					msg = "Keywords must be 0-500 characters long";
					doUpdateDoc(olddocid, replace, msg);
				}
				else if (desc.length() > 500) {
					msg = "Description must be 0-1024 characters long";
					doUpdateDoc(olddocid, replace, msg);
				}
				else if (options.equals("")) {
					msg = "Option must be chosen";
					doUpdateDoc(olddocid, replace, msg);
				}
				else {
					doUpdateDoc2(olddocid,name,desc,keywords,options,replace,msg);
				}
			}
			else if (action.equals("updatedocerror")) {
				printHeader("", "Update document", false);
				writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");

				writer.println("Your document was successfully uploaded but a problem occurred while notifying your team.");

			}
			else if (action.equals("movedoc")) {
				String docidStr = getParameter(request, "docid");
				String msg = getParameter(request, "msg");
				doMoveDoc(docidStr, msg);

			}
			else if (action.equals("movedoc2")) {
				String docidStr = getParameter(request, "docid");
				String catidStr = getParameter(request, "movetocat");

				doMoveDoc2(docidStr, catidStr);

			}
			else if (action.equals("moveconf")) {
				String docidStr = getParameter(request, "docid");
				String catidStr = getParameter(request, "movetocat");
				String ibmonly = getParameter(request, "i");

				if (ibmonly.equals("")) {
					ibmonly = "x";
				}
				doMoveConf(docidStr, catidStr, ibmonly);

			}
			else if (action.equals("movecat")) {
				try {
					String msg = getParameter(request, "msg");
					doMoveCat(msg);
				}
				catch (Exception i) {
					//System.out.println("error in docmanager movecat=" + i);
				}
			}
			else if (action.equals("movecat2")) {
				try {
					String catidStr = getParameter(request, "movecatid");
					String msg = getParameter(request, "msg");
					if (!catidStr.equals("")) {
						int catid = (new Integer(catidStr)).intValue();

						ETSCat movecat = ETSDatabaseManager.getCat(catid,Project.getProjectId());
						if (movecat != null)
							doMoveCat2(movecat, msg);
						else
							System.out.println("move cat is null");
					}
					else {
						//System.out.println("error in movecat2");
					}
				}
				catch (Exception i) {
					//System.out.println("error in docmanager movecat=" + i);
				}
			}
			else if (action.equals("movecat3")) {
				try {
					String movecatStr = getParameter(request, "movecatid");
					String movetocatStr = getParameter(request, "movetocat");

					doMoveCat3(movecatStr, movetocatStr);
				}
				catch (Exception i) {
					//System.out.println("error in docmanager movecat=" + i);
				}
			}
			else if (action.equals("movecatconf")) {
				String movecatidStr = getParameter(request, "movecatid");
				String movetocatidStr = getParameter(request, "movetocatid");
				String ibmonly = getParameter(request, "i");

				if (ibmonly.equals("")) {
					ibmonly = "x";
				}
				doMoveCatConf(movecatidStr, movetocatidStr, ibmonly);
			}
			else if (action.equals("updatedocstatus")) {
				String docidStr = getParameter(request, "docid");

				if (!docidStr.equals("")) {
					int docid = (new Integer(docidStr)).intValue();
					try {
						ETSDoc doc =
							ETSDatabaseManager.getDocByIdAndProject(
								docid,
								Project.getProjectId());
						if (doc != null) {
							if (doc.isIbmOnlyOrConf() && user_external) {
								writer.println(
									"You are not authorized to access this document");
								return;
							}
							doUpdateDocStatus(doc, "");
						}
						else {
							writer.println(
								"invalid url: invalid docid for this project");
						}
					}
					catch (SQLException se) {
						//System.out.println("SQLEx in DocMan getDocByIdAndProject(docid,projid)="+ se);
						writer.println("Error occurred:SQLEx in DocMan getDocByIdAndProject(docid,projid)");
					}
				}
				else {
					writer.println("invalid url: no docid");
				}

			}
			else if (action.equals("updatedocstatus2")) {
				String docidStr = getParameter(request, "docid");
				String status = getParameter(request, "status");
				String comm = getParameter(request, "comm");

				if (!docidStr.equals("")) {
					int docid = (new Integer(docidStr)).intValue();
					try {
						ETSDoc doc =
							ETSDatabaseManager.getDocByIdAndProject(
								docid,
								Project.getProjectId());
						if (doc != null) {
							if (doc.isIbmOnlyOrConf() && user_external) {
								writer.println(
									"You are not authorized to access this document");
								return;
							}
							if (status == "") {
								doUpdateDocStatus(
									doc,
									"Status must be selected");
							}
							else if (comm.length() > 1024) {
								doUpdateDocStatus(
									doc,
									"Comments are restricted to 1024 characters.");
							}
							else {
								doUpdateStatus2(doc, status, comm);
							}
						}
						else {
							writer.println(
								"invalid url: invalid docid for this project");
						}
					}
					catch (SQLException se) {
						//System.out.println("SQLEx in DocMan getDocByIdAndProject(docid,projid)="+ se);
						writer.println("Error occurred:SQLEx in DocMan getDocByIdAndProject(docid,projid)");
					}
				}
				else {
					writer.println("invalid url: no docid");
				}
			}
			else if (action.equals("publishdoc")) {
				String docidStr = getParameter(request, "docid");

				if (!docidStr.equals("")) {
					int docid = (new Integer(docidStr)).intValue();
					try {
						ETSDoc doc =
							ETSDatabaseManager.getDocByIdAndProject(
								docid,
								Project.getProjectId());
						if (doc != null) {
							if (doc.isIbmOnlyOrConf() && user_external) {
								writer.println(
									"You are not authorized to access this document");
								return;
							}
							doPublishDoc(doc, "");
						}
						else {
							writer.println(
								"invalid url: invalid docid for this project");
						}
					}
					catch (SQLException se) {
						//System.out.println("SQLEx in DocMan getDocByIdAndProject(docid,projid)="+ se);
						writer.println("Error occurred:SQLEx in DocMan getDocByIdAndProject(docid,projid)");
					}
				}
				else {
					writer.println("invalid url: no docid");
				}

			}
			else if (action.equals("publishdoc2")) {
				String docidStr = getParameter(request, "docid");

				if (!docidStr.equals("")) {
					int docid = (new Integer(docidStr)).intValue();
					try {
						ETSDoc doc =
							ETSDatabaseManager.getDocByIdAndProject(
								docid,
								Project.getProjectId());
						if (doc != null) {
							if (doc.isIbmOnlyOrConf() && user_external) {
								writer.println(
									"You are not authorized to access this document");
								return;
							}
							doPublishDoc2(doc, "");
						}
						else {
							writer.println(
								"invalid url: invalid docid for this project");
						}
					}
					catch (SQLException se) {
						//System.out.println("SQLEx in DocMan getDocByIdAndProject(docid,projid)="+ se);
						writer.println("Error occurred:SQLEx in DocMan getDocByIdAndProject(docid,projid)");
					}
				}
				else {
					writer.println("invalid url: no docid");
				}

			}
			else if (action.equals("sendappdoc")) {
				String docidStr = getParameter(request, "docid");

				if (!docidStr.equals("")) {
					int docid = (new Integer(docidStr)).intValue();
					try {
						ETSDoc doc =
							ETSDatabaseManager.getDocByIdAndProject(
								docid,
								Project.getProjectId());
						if (doc != null) {
							if (doc.isIbmOnlyOrConf() && user_external) {
								writer.println(
									"You are not authorized to access this document");
								return;
							}
							doSendAppDoc(doc, "");
						}
						else {
							writer.println(
								"invalid url: invalid docid for this project");
						}
					}
					catch (SQLException se) {
						//System.out.println("SQLEx in DocMan getDocByIdAndProject(docid,projid)="+ se);
						writer.println("Error occurred:SQLEx in DocMan getDocByIdAndProject(docid,projid)");
					}
				}
				else {
					writer.println("invalid url: no docid");
				}

			}
			else if (action.equals("sendappdoc2")) {
				String docidStr = getParameter(request, "docid");

				if (!docidStr.equals("")) {
					int docid = (new Integer(docidStr)).intValue();
					try {
						ETSDoc doc =
							ETSDatabaseManager.getDocByIdAndProject(
								docid,
								Project.getProjectId());
						if (doc != null) {
							if (doc.isIbmOnlyOrConf() && user_external) {
								writer.println(
									"You are not authorized to access this document");
								return;
							}
							doSendAppDoc2(doc, "");
						}
						else {
							writer.println(
								"invalid url: invalid docid for this project");
						}
					}
					catch (SQLException se) {
						//System.out.println("SQLEx in DocMan getDocByIdAndProject(docid,projid)="+ se);
						writer.println("Error occurred:SQLEx in DocMan getDocByIdAndProject(docid,projid)");
					}
				}
				else {
					writer.println("invalid url: no docid");
				}
			}
			else if (action.equals("report")) {
				String sortbyParam = request.getParameter("sort_by");
				String sortParam = request.getParameter("sort");

				if (sortbyParam == null) {
					sortbyParam = Defines.SORT_BY_NAME_STR;
				}
				if (sortParam == null) {
					sortParam = Defines.SORT_ASC_STR;
				}

				doProjectReport(sortbyParam, sortParam);
			}
			else if (action.equals("docreport")) {
				String docid = getParameter(request, "docid");
				String sortbyParam = request.getParameter("sort_by");
				String sortParam = request.getParameter("sort");

				if (docid.equals("")) {
					writer.println("invalid url");
				}
				else {
					if (sortbyParam == null) {
						sortbyParam = Defines.SORT_BY_DATE_STR;
					}
					if (sortParam == null) {
						sortParam = Defines.SORT_ASC_STR;
					}
					doDocProjectReport(docid, sortbyParam, sortParam);
				}
			}
			else if (action.equals("addcomm")) {
				String docidStr = getParameter(request, "docid");
				String currdocidStr = getParameter(request, "currdocid");

				String msg = getParameter(request, "msg");
				
				if (docidStr.equals("")) {
					//System.out.println("put error with current doc id message here");
				}
				else {
					int docid = (new Integer(docidStr)).intValue();
					doAddDocComment(docid, currdocidStr,msg);
				}
			}
			else if (action.equals("allcomm")) {
				String header = getBreadCrumbTrail(this_current_cat, false);
				printHeader(header, false);
				writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");

				String docidStr = getParameter(request, "docid");
				String currdocidStr = getParameter(request, "currdocid");
				if (!docidStr.equals("")) {
					int docid = (new Integer(docidStr)).intValue();
					try {
						ETSDoc doc =ETSDatabaseManager.getDocByIdAndProject(docid,Project.getProjectId());
						if (doc != null) {
							if (doc.isIbmOnlyOrConf() && user_external) {
								writer.println("You are not authorized to view this document");
								return;
							}
			
							Vector resUsers = new Vector();
							boolean authorized = false;
			
							if (doc.IsDPrivate()){
								resUsers = ETSDatabaseManager.getRestrictedProjMembers(Project.getProjectId(),doc.getId(),false,false);
								if (!ETSDocCommon.isAuthorized(doc.getUserId(),userRole,isSuperAdmin,isExecutive,resUsers,true,es.gIR_USERN)){
									writer.println("You are not authorized to view this document.");
									return;
								}
							}
			
							displayAllDocComments(doc,resUsers,currdocidStr,acf);
						
						}
						else {
							writer.println("invalid url: invalid docid for this project");
						}
					}
					catch (SQLException se) {
						//System.out.println("SQLEx in DocMan getDocByIdAndProject(docid,projid)="+ se);
						writer.println("Error occurred:SQLEx in DocMan getDocByIdAndProject(docid,projid)");
					}
					catch (Exception e) {
						//System.out.println("Ex in DocMan getDocByIdAndProject(docid,projid)="+ e);
						writer.println("Error occurred:Ex in DocMan getDocByIdAndProject(docid,projid)");
					}
				}
			}
			else {
				writer.println("error action not valid");
				writer.println("</td>");
			}
		}
		else { //cat view
			try {
				String sortbyParam = request.getParameter("sort_by");
				String sortParam = request.getParameter("sort");

				if (sortbyParam == null) {
					sortbyParam = Defines.SORT_BY_NAME_STR;
				}
				if (sortParam == null) {
					sortParam = Defines.SORT_ASC_STR;
				}

				String pmocat = getParameter(request, "pmocat");
				if (pmocat.equals("")) {
					Vector cats =
						ETSDatabaseManager.getSubCats(
							CurrentCatId,
							sortbyParam,
							sortParam);
					Vector docs =
						ETSDatabaseManager.getDocs(
							CurrentCatId,
							sortbyParam,
							sortParam,(isSuperAdmin|| isExecutive || userRole.equals(Defines.WORKSPACE_OWNER)),es.gIR_USERN);

					String header = getBreadCrumbTrail(this_current_cat, true);
					printHeader(header, true);
					writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
					writer.println("<tr><td>");

					boolean owns =	printChildren(cats, docs, sortbyParam, sortParam);

					printCatActionButtons(owns, (cats.size() > 0));
				}
				else {
					ETSPMODao pmoDao = new ETSPMODao();
					Vector vDetails = pmoDao.getPMOfficeObjects(conn,Project.getPmo_project_id());
					ETSPMOffice pmo_curr_cat =
						pmoDao.getPMOfficeObjectDetail(
							conn,
							Project.getPmo_project_id(),
							pmocat);
					Vector pmocats =
						pmoDao.getPMOfficeSubCats(
							conn,
							Project.getPmo_project_id(),
							pmocat,
							sortbyParam,
							sortParam);
					Vector pmodocs =
						pmoDao.getPMODocuments(
							conn,
							pmocat,
							Project.getPmo_project_id(),
							sortbyParam,
							sortParam);
					//Vector pmodocs = new Vector();

					String header =	getPMOBreadCrumbTrail(pmo_curr_cat, true, pmoDao);
					//String header = "";
					printHeader(header, pmo_curr_cat.getName(), true);
					writer.println(
						"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
					writer.println("<tr><td>");

					printPMOChildren(
						pmo_curr_cat,
						pmocats,
						pmodocs,
						pmoDao,
						vDetails,
						sortbyParam,
						sortParam);
				}
			}
			catch (SQLException se) {
				//System.out.println("sql error in docman.documenthandler = " + se);
			}
			catch (IOException ioe) {
				//System.out.println("io error in docman.documenthandler = " + ioe);
			}
			catch (Exception e) {
				//System.out.println("exception error in docman.documenthandler = " + e);
				e.printStackTrace();
			}
		}
		//System.out.println("A spn " + new java.util.Date(System.currentTimeMillis()));

	}

	private void printHeader(String msg, boolean printBM) {
		StringBuffer buf = new StringBuffer();
		try {
			//gutter between content and right column
			writer.println("<td rowspan=\"5\" width=\"7\"><img alt=\"\" src=\""
					+ Defines.TOP_IMAGE_ROOT+ "c.gif\" width=\"7\" height=\"1\" /></td>");
			// Right column start
			writer.println("<td rowspan=\"5\" width=\"150\" valign=\"top\">");
			ETSContact contact =new ETSContact(Project.getProjectId(), request);
			contact.printContactBox(writer);
			writer.println("</td></tr>");
		}
		catch (Exception e) {
		}

		String header =ETSUtils.getBookMarkString(this_current_cat.getName(), "", printBM);

		buf.append("<tr valign=\"bottom\"><td width=\"443\" valign=\"top\">");
		buf.append(header + "</td></tr>");
		buf.append("<tr valign=\"bottom\"><td width=\"443\" valign=\"bottom\" class=\"small\">");
		buf.append(msg + "</td></tr>");
		buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"5\" width=\"1\" alt=\"\" /></td></tr>");
		buf.append("<tr valign=\"bottom\"><td width=\"443\" valign=\"bottom\">");
		buf.append("<img src=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" height=\"1\" width=\"443\" valign=\"bottom\" alt=\"\" />");
		buf.append("</td></tr>");
		buf.append("</table>");

		writer.println(buf.toString());
	}

	private void printHeader(String msg, String title, boolean printBM) {
		StringBuffer buf = new StringBuffer();
		try {
			//gutter between content and right column
			writer.println(
				"<td rowspan=\"5\" width=\"7\"><img alt=\"\" src=\""
					+ Defines.TOP_IMAGE_ROOT
					+ "c.gif\" width=\"7\" height=\"1\" /></td>");
			// Right column start
			writer.println("<td rowspan=\"5\" width=\"150\" valign=\"top\">");
			ETSContact contact =
				new ETSContact(Project.getProjectId(), request);
			contact.printContactBox(writer);
			writer.println("</td></tr>");
		}
		catch (Exception e) {

		}

		String header = ETSUtils.getBookMarkString(title, "", printBM);
		buf.append("<tr valign=\"bottom\"><td width=\"443\" valign=\"top\">");
		buf.append(header + "</td></tr>");
		buf.append(
			"<tr valign=\"bottom\"><td width=\"443\" valign=\"bottom\" class=\"small\">");
		buf.append(msg + "</td></tr>");
		buf.append(
			"<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"5\" width=\"1\" alt=\"\" /></td></tr>");
		buf.append(
			"<tr valign=\"bottom\"><td width=\"443\" valign=\"bottom\">");
		buf.append(
			"<img src=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" height=\"1\" width=\"443\" valign=\"bottom\" alt=\"\" />");
		buf.append("</td></tr>");
		buf.append("</table>");

		writer.println(buf.toString());
	}

	//go up tree until document parent_id =0
	public void printBreadCrumbTrail(ETSCat cat, boolean isCatView) {
		StringBuffer buf = new StringBuffer();
		Vector breadcrumb = new Vector();

		try {
			//ETSCat cat = databaseManager.getCat(CurrentCatId);
			if (cat != null) {
				breadcrumb.addElement(cat);
				ETSCat c = cat;
				while (true) {
					c = ETSDatabaseManager.getCat(c.getParentId());
					if (c != null) {
						breadcrumb.addElement(c);
						if (c.getParentId() == 0) {
							break;
						}
					}
					else {
						break;
					}
				}
			}
		}
		catch (SQLException se) {
			//System.out.println("sql error in docman breadcrumb= " + se);
		}
		catch (Exception e) {
			//System.out.println("except error in docman breadcrumb= " + e);
		}

		//buf.append("<p class=\"small\">");
		buf.append(
			"<table  cellpadding=\"0\" cellspacing=\"0\" width=\"443\" border=\"0\">");
		buf.append("<tr><td class=\"small\">");
		for (int i = (breadcrumb.size()) - 1; i >= 0; i--) {
			ETSCat bc = (ETSCat) breadcrumb.elementAt(i);
			if (i != (breadcrumb.size()) - 1) {
				//buf.append("&nbsp>&nbsp;");
				buf.append(" &gt; ");
			}

			if (i == 0) {
				if (isCatView) {
					buf.append("<b>" + bc.getName() + "</b>");
				}
				else {
					buf.append(
						"<a href=\"ETSProjectsServlet.wss?proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ bc.getId()
							+ "&linkid="
							+ linkid
							+ "\"><b>"
							+ bc.getName()
							+ "</b></a>");
				}
			}
			else {
				buf.append(
					"<a href=\"ETSProjectsServlet.wss?proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ bc.getId()
						+ "&linkid="
						+ linkid
						+ "\">"
						+ bc.getName()
						+ "</a>");
			}
		}
		buf.append("</td></tr>");
		//gray dotted line
		buf.append("<tr><td height=\"21\">");
		buf.append(
			"<img src=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" height=\"1\" width=\"443\" alt=\"\" />");
		buf.append("</td></tr>");
		buf.append("</table>");
		writer.println(buf.toString());

	}
	public String getBreadCrumbTrail(ETSCat cat, boolean isCatView) {
		StringBuffer buf = new StringBuffer();
		Vector breadcrumb = new Vector();

		try {
			if (cat != null) {
				breadcrumb.addElement(cat);
				ETSCat c = cat;
				while (true) {
					c = ETSDatabaseManager.getCat(c.getParentId());
					if (c != null) {
						breadcrumb.addElement(c);
						if (c.getParentId() == 0) {
							break;
						}
					}
					else {
						break;
					}
				}
			}
		}
		catch (SQLException se) {
			//System.out.println("sql error in docman breadcrumb= " + se);
		}
		catch (Exception e) {
			//System.out.println("except error in docman breadcrumb= " + e);
		}

		for (int i = (breadcrumb.size()) - 1; i >= 0; i--) {
			ETSCat bc = (ETSCat) breadcrumb.elementAt(i);
			if (i != (breadcrumb.size()) - 1) {
				//buf.append("&nbsp>&nbsp;");
				buf.append(" &gt; ");
			}

			if (i == 0) {
				if (isCatView) {
					buf.append("<b>" + bc.getName() + "</b>");
				}
				else {
					buf.append(
						"<a href=\"ETSProjectsServlet.wss?proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ bc.getId()
							+ "&linkid="
							+ linkid
							+ "\"><b>"
							+ bc.getName()
							+ "</b></a>");
				}
			}
			else {
				buf.append(
					"<a href=\"ETSProjectsServlet.wss?proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ bc.getId()
						+ "&linkid="
						+ linkid
						+ "\">"
						+ bc.getName()
						+ "</a>");
			}
		}

		return buf.toString();

	}

	public String getPMOBreadCrumbTrail(
		ETSPMOffice cat,
		boolean isCatView,
		ETSPMODao pmoDao) {
		StringBuffer buf = new StringBuffer();
		Vector breadcrumb = new Vector();

		try {
			if (cat != null) {
				breadcrumb.addElement(cat);
				ETSPMOffice c = cat;
				while (!c.getPMO_Project_ID().equals(c.getPMOID())) {
					c =
						pmoDao.getPMOfficeObjectDetail(
							conn,
							c.getPMO_Project_ID(),
							c.getPMO_Parent_ID());
					breadcrumb.addElement(c);

				}
			}
		}
		catch (SQLException se) {
			//System.out.println("sql error in docman pmo breadcrumb= " + se);
		}
		catch (Exception e) {
			//System.out.println("except error in docman pmo breadcrumb= " + e);
		}

		try {
			ETSCat cattab = ETSDatabaseManager.getCat(TopCatId);
			buf.append(
				"<a href=\"ETSProjectsServlet.wss?proj="
					+ Project.getProjectId()
					+ "&tc="
					+ TopCatId
					+ "&cc="
					+ cattab.getId()
					+ "&linkid="
					+ linkid
					+ "\">"
					+ cattab.getName()
					+ "</a>");
		}
		catch (SQLException se) {
			//System.out.println("sql error in docman pmo tab breadcrumb= " + se);
		}
		catch (Exception e) {
			//System.out.println("except error in docman pmo tab breadcrumb= " + e);
		}

		for (int i = (breadcrumb.size()) - 1; i >= 0; i--) {
			ETSPMOffice bc = (ETSPMOffice) breadcrumb.elementAt(i);
			//if (i != (breadcrumb.size())-1){
			buf.append(" &gt; ");
			//}

			if (i == 0) {
				if (isCatView) {
					buf.append("<b>" + bc.getName() + "</b>");
				}
				else {
					buf.append(
						"<a href=\"ETSProjectsServlet.wss?proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&pmocat="
							+ bc.getPMOID()
							+ "&linkid="
							+ linkid
							+ "\"><b>"
							+ bc.getName()
							+ "</b></a>");
				}
			}
			else {
				buf.append(
					"<a href=\"ETSProjectsServlet.wss?proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&pmocat="
						+ bc.getPMOID()
						+ "&linkid="
						+ linkid
						+ "\">"
						+ bc.getName()
						+ "</a>");
			}
		}

		return buf.toString();

	}

	public boolean printChildren(
		Vector cats,
		Vector docs,
		String sortby,
		String ad)
		throws SQLException, IOException {
		AccessCntrlFuncs acf = new AccessCntrlFuncs();
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		StringBuffer buf = new StringBuffer();

		Vector sortedcats = cats; //sortcats(cats,1);
		Vector sorteddocs = docs; //sortdocs(docs,1);

		if (sortby.equals(Defines.SORT_BY_AUTH_STR)
			|| sortby.equals(Defines.SORT_BY_TYPE_STR)) {
			byte sortOrder = ETSComparator.getSortOrder(sortby);
			byte sortAD = ETSComparator.getSortBy(ad);
			Collections.sort(sortedcats, new ETSComparator(sortOrder, sortAD));
			Collections.sort(sorteddocs, new ETSComparator(sortOrder, sortAD));
		}

		boolean gray_flag = true;
		boolean child_flag = false;
		boolean owns_a_cat = false;
		boolean bExpDoc = false;

		int width_name = 239;
		int width_mod = 100;
		int width_type = 50;
		int width_author = 170;
		int width_info = 25;

		boolean internal = false;
		boolean ibmonlyFlag = false;
		boolean ibmconfFlag = false;
		boolean resUserFlag = false;

		try {
			ETSCat curr_cat = this_current_cat;

			if ((es.gDECAFTYPE.trim()).equalsIgnoreCase("I")) {
				internal = true;
			}

			if (curr_cat.isIbmOnlyOrConf() && !internal) {
				buf.append("You are not authorized to view this folder.");
				return false;
			}
			
			/*
			Vector resUsers = new Vector();
			if (curr_cat.IsCPrivate()){
				boolean authorized = false;
				resUsers = ETSDatabaseManager.getRestrictedProjMembers(Project.getProjectId(),curr_cat.getId(),false,true);
				
				if (!isAuthorized(curr_cat.getUserId(),resUsers,true)){
					buf.append("You are not authorized to view this folder.");
					return false;	
				}					
				
			}
			*/
			
			buf.append("<table  cellpadding=\"2\" cellspacing=\"0\" width=\"600\" border=\"0\">");
			buf.append("<tr><td colspan=\"6\"><img src=\"//www.ibm.com/i/c.gif\" height=\"16\" width=\"1\" alt=\"\" /></td></tr>");

			/*if(curr_cat.IsCPrivate()){
				buf.append("<tr><td colspan=\"6\">This folder is restricted to: ");
				for (int u=0;u<resUsers.size();u++){
					if (u==0)
						buf.append(((ETSUser)resUsers.elementAt(u)).getUserId());	
					else
						buf.append(", "+ ((ETSUser)resUsers.elementAt(u)).getUserId());	
				}
				buf.append("</td></tr>");
			
				buf.append("<tr><td colspan=\"6\"><img src=\"//www.ibm.com/i/c.gif\" height=\"16\" width=\"1\" alt=\"\" /></td></tr>");
			}*/
			buf.append("<tr><td colspan=\"6\" class=\"small\">Click on the column heading to sort</td></tr>\n");
			buf.append("<tr><td colspan=\"6\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");

			buf.append("<tr><th id=\"list_name\" colspan=\"2\" align=\"left\" valign=\"middle\" height=\"16\">");
			//sort by name
			if (sortby.equals(Defines.SORT_BY_NAME_STR)) {
				if (ad.equals(Defines.SORT_ASC_STR)) {
					buf.append(
						"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&linkid="
							+ linkid
							+ "&sort_by="
							+ Defines.SORT_BY_NAME_STR
							+ "&sort="
							+ Defines.SORT_DES_STR
							+ "\" class=\"fbox\">");
					buf.append("Name</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append("<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
					buf.append("</table></th>");
				}
				else {
					buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="
							+ Project.getProjectId()+ "&tc="+ TopCatId
							+ "&cc="+ CurrentCatId+ "&linkid="+ linkid
							+ "&sort_by="+ Defines.SORT_BY_NAME_STR+ "&sort="+ Defines.SORT_ASC_STR
							+ "\">");
					buf.append("Name</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append("<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");
					buf.append("</table></th>");
				}
			}
			else {
				buf.append(
					"<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="+ Project.getProjectId()
						+ "&tc="+ TopCatId+ "&cc="+ CurrentCatId+ "&linkid="+ linkid
						+ "&sort_by="+ Defines.SORT_BY_NAME_STR+ "&sort="+ Defines.SORT_ASC_STR+ "\">");
				buf.append("Name</a></th>");
			}

			//sort by date
			buf.append("<th id=\"list_date\" align=\"left\" valign=\"middle\">");
			if (sortby.equals(Defines.SORT_BY_DATE_STR)) {
				if (ad.equals(Defines.SORT_ASC_STR)) {
					buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(	"<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="+ Project.getProjectId()
							+ "&tc="+ TopCatId+ "&cc="+ CurrentCatId+ "&linkid="+ linkid
							+ "&sort_by="+ Defines.SORT_BY_DATE_STR+ "&sort="+ Defines.SORT_DES_STR+ "\" class=\"fbox\">");
					buf.append("Modified</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append("<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
					buf.append("</table></th>");
				}
				else {
					buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="+ Project.getProjectId()
							+ "&tc="+ TopCatId+ "&cc="+ CurrentCatId+ "&linkid="+ linkid
							+ "&sort_by="+ Defines.SORT_BY_DATE_STR+ "&sort="+ Defines.SORT_ASC_STR+ "\" class=\"fbox\">");
					buf.append("Modified</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append("<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");
					buf.append("</table></th>");
				}
			}
			else {
				buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="+ Project.getProjectId()
						+ "&tc="+ TopCatId+ "&cc="+ CurrentCatId+ "&linkid="+ linkid
						+ "&sort_by="+ Defines.SORT_BY_DATE_STR+ "&sort="+ Defines.SORT_ASC_STR+ "\" class=\"fbox\">");
				buf.append("Modified</a></th>");
			}

			//sort by type
			buf.append("<th id=\"list_type\" align=\"left\" valign=\"middle\">");
			if (sortby.equals(Defines.SORT_BY_TYPE_STR)) {
				if (ad.equals(Defines.SORT_ASC_STR)) {
					buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="+ Project.getProjectId()
							+ "&tc="+ TopCatId+ "&cc="+ CurrentCatId
							+ "&linkid="+ linkid+ "&sort_by="+ Defines.SORT_BY_TYPE_STR
							+ "&sort="+ Defines.SORT_DES_STR+ "\" class=\"fbox\">");
					//buf.append("<a href=\"ETSProjectsServlet.wss?proj="+Project.getProjectId()+"&tc="+TopCatId+"&cc="+CurrentCatId+"&linkid="+linkid+"&sort_by="+Defines.SORT_BY_TYPE_STR+"&sort="+Defines.SORT_DES_STR+"\" class=\"fbox\">");
					buf.append("Type</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append("<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
					buf.append("</table></th>");
				}
				else {
					buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="+ Project.getProjectId()
							+ "&tc="+ TopCatId+ "&cc="+ CurrentCatId+ "&linkid="+ linkid
							+ "&sort_by="+ Defines.SORT_BY_TYPE_STR+ "&sort="+ Defines.SORT_ASC_STR+ "\" class=\"fbox\">");
					buf.append("Type</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append("<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");
					buf.append("</table></th>");
				}
			}
			else {
				buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="+ Project.getProjectId()
						+ "&tc="+ TopCatId+ "&cc="+ CurrentCatId+ "&linkid="+ linkid
						+ "&sort_by="+ Defines.SORT_BY_TYPE_STR+ "&sort="+ Defines.SORT_ASC_STR+ "\" class=\"fbox\">");
				buf.append("Type</a></th>");
			}

			//sort by author
			buf.append("<th id=\"list_author\" align=\"left\" valign=\"middle\">");
			if (sortby.equals(Defines.SORT_BY_AUTH_STR)) {
				if (ad.equals(Defines.SORT_ASC_STR)) {
					buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="+ Project.getProjectId()
							+ "&tc="+ TopCatId+ "&cc="+ CurrentCatId+ "&linkid="+ linkid
							+ "&sort_by="+ Defines.SORT_BY_AUTH_STR+ "&sort="+ Defines.SORT_DES_STR+ "\" class=\"fbox\">");
					buf.append("Author</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append("<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
					buf.append("</table></th>");
				}
				else {
					buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="+ Project.getProjectId()
							+ "&tc="+ TopCatId+ "&cc="+ CurrentCatId+ "&linkid="+ linkid
							+ "&sort_by="+ Defines.SORT_BY_AUTH_STR+ "&sort="+ Defines.SORT_ASC_STR+ "\" class=\"fbox\">");
					buf.append("Author</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append("<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");
					buf.append("</table></th>");
				}
			}
			else {
				buf.append("<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="+ Project.getProjectId()
						+ "&tc="+ TopCatId+ "&cc="+ CurrentCatId+ "&linkid="+ linkid
						+ "&sort_by="+ Defines.SORT_BY_AUTH_STR+ "&sort="+ Defines.SORT_ASC_STR+ "\" class=\"fbox\">");
				buf.append("Author</a></th>");
			}

			buf.append("<th id=\"list_details\" class=\"small\">&nbsp;</th></tr>");
			// -----------------------------------------------------------------------------------------------------------	    

			if (curr_cat.getParentId() != 0) {
				if (gray_flag) {
					buf.append("<tr style=\"background-color:#eeeeee\">");
					gray_flag = false;
				}
				else {
					buf.append("<tr>");
					gray_flag = true;
				}

				ETSCat parent = (ETSCat) ETSDatabaseManager.getCat(curr_cat.getParentId());

				buf.append("<td width=\"16\" height=\"17\" align=\"left\" valign=\"top\"><img src=\""
						+ Defines.SERVLET_PATH
						+ "ETSImageServlet.wss?proj=ETS_BACK_IMG&mod=0\" width=\"12\" height=\"8\" alt=\"back\" /></td>");
				//img
				buf.append("<td headers=\"list_name\" height=\"17\" width=\""
						+ width_name+ "\" align=\"left\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?proj="
						+ Project.getProjectId()+ "&tc="+ TopCatId
						+ "&cc="+ parent.getId()+ "&linkid="+ linkid+ "\" class=\"fbox\">Back to '"+ parent.getName()+ "'</a></td>");
				//filename

				buf.append("<td headers=\"list_date\" height=\"17\" width=\""
						+ width_mod+ "\" align=\"left\" valign=\"top\"> &nbsp; </td>");
				//date
				buf.append("<td headers=\"list_type\" height=\"17\" align=\"left\" class=\"small\" width=\""
						+ width_type+ "\" valign=\"top\"> &nbsp; </td>");
				//format
				buf.append("<td headers=\"list_author\" height=\"17\" align=\"left\" class=\"small\" width=\""
						+ width_author+ "\" valign=\"top\"> &nbsp; </td>");
				//author
				buf.append("<td headers=\"list_details\" height=\"17\" align=\"left\" width=\""
						+ width_info+ "\" valign=\"top\"> &nbsp; </td>");
				//details
				buf.append("</tr>");
			}

			if (sortedcats != null) {
				for (int i = 0; i < sortedcats.size(); i++) {
					ETSCat cat = (ETSCat) sortedcats.elementAt(i);
					if ((cat.isIbmOnlyOrConf() && internal) || (!cat.isIbmOnlyOrConf())) {
						child_flag = true;
						if (es.gIR_USERN.equals(cat.getUserId())) {
							owns_a_cat = true;
						}

						if (gray_flag) {
							buf.append("<tr style=\"background-color:#eeeeee\">");
							gray_flag = false;
						}
						else {
							buf.append("<tr>");
							gray_flag = true;
						}
						buf.append("<td width=\"16\" height=\"17\" align=\"left\" valign=\"top\"><img src=\""
								+ Defines.SERVLET_PATH
								+ "ETSImageServlet.wss?proj=ETS_CAT_IMG&mod=0\" width=\"13\" height=\"9\" alt=\"folder\" /></td>");
						
						String resUserStr = "";
						/*if (cat.IsCPrivate()){
							resUserFlag = true;
							resUserStr = "<span class=\"ast\">#</span>";
						}*/
						//img
						if (cat.getIbmOnly() == Defines.ETS_IBM_CONF) {
							ibmconfFlag = true;
							buf.append("<td headers=\"list_name\" height=\"17\" width=\""+ width_name
									+ "\" align=\"left\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?proj="+ Project.getProjectId()
									+ "&tc="+ TopCatId+ "&cc="+ cat.getId()+ "&linkid="+ linkid+ "\" class=\"fbox\">"+ cat.getName()
									+ "<span class=\"ast\">**</span>"+resUserStr+"</a></td>");
							//filename
						}
						else if (cat.getIbmOnly() == Defines.ETS_IBM_ONLY) {
							ibmonlyFlag = true;
							buf.append("<td headers=\"list_name\" height=\"17\" width=\""+ width_name
									+ "\" align=\"left\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?proj="+ Project.getProjectId()
									+ "&tc="+ TopCatId+ "&cc="+ cat.getId()+ "&linkid="+ linkid+ "\" class=\"fbox\">"
									+ cat.getName()+ "<span class=\"ast\">*</span>"+resUserStr+"</a></td>");
							//filename
						}
						else {
							buf.append(
								"<td headers=\"list_name\" height=\"17\" width=\""+ width_name
									+ "\" align=\"left\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?proj="+ Project.getProjectId()
									+ "&tc="+ TopCatId+ "&cc="+ cat.getId()+ "&linkid="+ linkid+ "\" class=\"fbox\">"+ cat.getName()
									+ resUserStr+"</a></td>");
							//filename
						}
						java.util.Date date = new java.util.Date(cat.getLastTimestamp());
						buf.append(
							"<td headers=\"list_date\" height=\"17\" width=\""+ width_mod+ "\" align=\"left\" valign=\"top\">"
								+ df.format(date)+ "</td>");
						//date
						buf.append("<td headers=\"list_type\" height=\"17\" width=\""
								+ width_type+ "\" align=\"left\" class=\"small\" valign=\"top\"> - </td>");
						//format

						buf.append("<td headers=\"list_author\" height=\"17\" width=\""
								+ width_author+ "\" class=\"small\" align=\"left\" valign=\"top\">"
								+ ETSUtils.getUsersName(conn, cat.getUserId())+ "</td>");
						//author

						buf.append("<td headers=\"list_details\" height=\"17\" width=\""+ width_info
								+ "\" align=\"left\" valign=\"top\">&nbsp;</td>");
						//details
						buf.append("</tr>");
					}
				}

			}

			if (sorteddocs != null) {
				for (int i = 0; i < sorteddocs.size(); i++) {
					String exStr = "";
					ETSDoc doc = (ETSDoc) sorteddocs.elementAt(i);
					if ((doc.isIbmOnlyOrConf() && internal)
						|| (!doc.isIbmOnlyOrConf())) {
						if ((!doc.hasExpired())|| (userRole == Defines.WORKSPACE_OWNER)|| doc.getUserId().equals(es.gIR_USERN)
							|| isSuperAdmin) {
							child_flag = true;

							if (gray_flag) {
								buf.append("<tr style=\"background-color:#eeeeee\">");
								gray_flag = false;
							}
							else {
								buf.append("<tr>");
								gray_flag = true;
							}

							/*
							if (doc.getDocStatus()==Defines.DOC_DRAFT)
								buf.append("<td width=\"16\" height=\"17\" align=\"left\" valign=\"top\"><img src=\""+Defines.SERVLET_PATH+"ETSImageServlet.wss?proj=ETS_DOC_DRAFT_IMG&mod=0\" width=\"12\" height=\"12\" alt=\"document\" /></td>"); //img
							else if (doc.getDocStatus()==Defines.DOC_SUB_APP)
								buf.append("<td width=\"16\" height=\"17\" align=\"left\" valign=\"top\"><img src=\""+Defines.SERVLET_PATH+"ETSImageServlet.wss?proj=ETS_DOC_SUB_APP_IMG&mod=0\" width=\"12\" height=\"12\" alt=\"document\" /></td>"); //img
							else
							*/
							
							String resUserStr = "";
							if (doc.IsDPrivate()){
								resUserFlag = true;
								resUserStr = "<span class=\"ast\">#</span>";
							}
							buf.append("<td width=\"16\" height=\"17\" align=\"left\" valign=\"top\"><img src=\""
									+ Defines.SERVLET_PATH
									+ "ETSImageServlet.wss?proj=ETS_DOC_IMG&mod=0\" width=\"12\" height=\"12\" alt=\"document\" /></td>");
							//img

							if (doc.hasExpired()) {
								exStr ="<span class=\"small\"><span class=\"ast\"><b>&#8224;</b></span></span>";
								bExpDoc = true;
							}

							if (doc.getIbmOnly() == Defines.ETS_IBM_CONF) {
								ibmconfFlag = true;
								buf.append("<td headers=\"list_name\" height=\"17\" width=\""+ width_name
										+ "\" align=\"left\" valign=\"top\"><a href=\"ETSContentDeliveryServlet.wss/"
								//+ doc.getFileName()
								+URLEncoder.encode(doc.getFileName())
									+ "?projid="+ Project.getProjectId()+ "&docid="+ doc.getId()
									+ "&linkid="+ linkid+ "\" target=\"new\" class=\"fbox\">"
									+ doc.getName()+ "<span class=\"ast\">**</span>"+ exStr+resUserStr
									+ "</a></td>");
								//filename
							}
							else if (
								doc.getIbmOnly() == Defines.ETS_IBM_ONLY) {
								ibmonlyFlag = true;
								buf.append("<td headers=\"list_name\" height=\"17\" width=\""
										+ width_name+ "\" align=\"left\" valign=\"top\"><a href=\"ETSContentDeliveryServlet.wss/"
								//+ doc.getFileName()
								+URLEncoder.encode(doc.getFileName())
									+ "?projid="+ Project.getProjectId()+ "&docid="+ doc.getId()
									+ "&linkid="+ linkid+ "\" target=\"new\" class=\"fbox\">"
									+ doc.getName()+ "<span class=\"ast\">*</span>"+ exStr+resUserStr+ "</a></td>");
								//filename
							}
							else {
								buf.append("<td headers=\"list_name\" height=\"17\" width=\""
										+ width_name+ "\" align=\"left\" valign=\"top\"><a href=\"ETSContentDeliveryServlet.wss/"
								//+ doc.getFileName()
								+URLEncoder.encode(doc.getFileName())
									+ "?projid="+ Project.getProjectId()+ "&docid="+ doc.getId()
									+ "&linkid="+ linkid+ "\" target=\"new\" class=\"fbox\">"
									+ doc.getName()+ exStr+resUserStr+ "</a></td>");
								//filename
							}
							java.util.Date date =new java.util.Date(doc.getUploadDate());
							buf.append("<td headers=\"list_date\" height=\"17\" width=\""
									+ width_mod+ "\" align=\"left\" valign=\"top\">"
									+ df.format(date)+ "</td>");
							//date
							buf.append("<td headers=\"list_type\" height=\"17\" width=\""
									+ width_type+ "\" align=\"left\" class=\"small\" valign=\"top\">"
									+ doc.getFileType()+ "</td>");
							//format
							buf.append(
								"<td headers=\"list_author\" height=\"17\" width=\""+ width_author
									+ "\" class=\"small\" align=\"left\" valign=\"top\">"
									+ ETSUtils.getUsersName(conn,doc.getUserId())+ "</td>");
							//author
							buf.append("<td headers=\"list_details\" height=\"17\" width=\""
									+ width_info+ "\" class=\"small\" align=\"left\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?action=details&proj="
									+ Project.getProjectId()+ "&tc="+ TopCatId+ "&cc="+ CurrentCatId
									+ "&docid="+ doc.getId()+ "&linkid="+ linkid+ "\"> Details </a> </td>");
							//details
							buf.append("</tr>");
						}
					}
				}
			}
			else {
				//System.out.println("sorted doc == null");
			}

			if (!child_flag) {
				buf.append("<tr>");
				buf.append("<td colspan=\"6\" height=\"17\" class=\"small\"> This folder is currently empty.</td>");
				buf.append("</tr>");
			}

			// PMO DOCUMENTS
			if (Project.getPmo_project_id() != null && (CurrentCatId==TopCatId)) {
				if (!(Project.getPmo_project_id().equals("0") || Project.getPmo_project_id().equals(""))) {
					ETSPMODao pmoDAO = new ETSPMODao();
					ETSPMOffice pmo = pmoDAO.getPMOfficeProjectDetails(conn,Project.getPmo_project_id());
					if (pmo != null) {
						buf.append("<tr><td colspan=\"6\"><img src=\"//www.ibm.com/i/c.gif\" height=\"8\" width=\"1\" alt=\"\" /></td></tr>");
						buf.append("<tr><td colspan=\"6\"class=\"small\" valign=\"bottom\"><b>Additional project documents</b></td></tr>");
						//buf.append("<tr><td colspan=\"6\"><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"1\" alt=\"\" /></td></tr>");

						buf.append("<tr style=\"background-color:#eeeeee\" >");
						buf.append("<td width=\"16\" height=\"17\" align=\"left\" valign=\"top\"><img src=\"" + Defines.SERVLET_PATH
								+ "ETSImageServlet.wss?proj=ETS_CAT_IMG&mod=0\" width=\"13\" height=\"9\" alt=\"folder\" /></td>");
						//img
						buf.append( "<td headers=\"list_name\" height=\"17\" width=\"" + width_name
								+ "\" align=\"left\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?proj=" + Project.getProjectId()
								+ "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&pmocat=" + pmo.getPMOID()
								+ "&linkid=" + linkid + "\" class=\"fbox\">" + pmo.getName() + "</a></td>");
						//filename
						String dateStr = "";
						if (pmo.getFinishDate() != null) {
							java.util.Date date =
								new java.util.Date(
									(pmo.getFinishDate()).getTime());
							dateStr = df.format(date);
						}
						buf.append(
							"<td headers=\"list_date\" height=\"17\" width=\"" + width_mod
								+ "\" align=\"left\" valign=\"top\">" + dateStr + "</td>");
						//date
						buf.append( "<td headers=\"list_type\" height=\"17\" width=\"" + width_type
								+ "\" align=\"left\" class=\"small\" valign=\"top\"> &nbsp; </td>");
						//format
						buf.append( "<td headers=\"list_author\" height=\"17\" width=\"" + width_author
								+ "\" class=\"small\" align=\"left\" valign=\"top\">&nbsp;</td>");
						//author
						buf.append( "<td headers=\"list_details\" height=\"17\" width=\"" + width_info
								+ "\" align=\"left\" valign=\"top\">&nbsp;</td>");
						//details
						buf.append("</tr>");
					}
					else {
						//System.out.println( "hey==null  pmoid=" + Project.getPmo_project_id());
					}
				}
			}
			// end PMO

			if (ibmonlyFlag) {
				buf.append("<tr><td colspan=\"6\"><img src=\"//www.ibm.com/i/c.gif\" height=\"4\" width=\"1\" alt=\"\" /></td></tr>");
				buf.append("<tr><td colspan=\"6\" class=\"small\" valign=\"bottom\"><span class=\"ast\">*</span>Denotes IBM Only folder/document</td></tr>");
			}
			if (ibmconfFlag) {
				buf.append("<tr><td colspan=\"6\"><img src=\"//www.ibm.com/i/c.gif\" height=\"4\" width=\"1\" alt=\"\" /></td></tr>");
				buf.append("<tr><td colspan=\"6\" class=\"small\" valign=\"bottom\"><span class=\"ast\">**</span>Denotes permanent IBM Only folder/document</td></tr>");
			}
			if (resUserFlag) {
				buf.append("<tr><td colspan=\"6\"><img src=\"//www.ibm.com/i/c.gif\" height=\"4\" width=\"1\" alt=\"\" /></td></tr>");
				buf.append("<tr><td colspan=\"6\" class=\"small\" valign=\"bottom\"><span class=\"ast\">#</span>Access to this document is restricted to selected team members</td></tr>");
			}
			if (bExpDoc) {
				buf.append("<tr><td colspan=\"6\"><img src=\"//www.ibm.com/i/c.gif\" height=\"4\" width=\"1\" alt=\"\" /></td></tr>");
				buf.append("<tr><td colspan=\"6\" class=\"small\" valign=\"bottom\"><span class=\"ast\"><b>&#8224;</b></span>Denotes expired document</td></tr>");
			}

			buf.append("</table>");
			writer.println(buf.toString());
		}
		catch (SQLException se) {
			//System.out.println("sql error in docman print children= " + se);
		}
		catch (AMTException ae) {
			//System.out.println("amt error in docman print children= " + ae);
		}
		catch (Exception e) {
			//System.out.println("except error in docman print children= " + e);
			e.printStackTrace();
		}
		return owns_a_cat;

	}

	public void printCatActionButtons(boolean owns, boolean hasSubCats) {
		StringBuffer buf = new StringBuffer();
		boolean print_flag = false;

		buf.append("<br />");

		try {
			ETSCat cat = this_current_cat;
			buf.append("<table cellpadding=\"0\" cellspacing=\"1\" width=\"600\" border=\"0\">");
			buf.append("<tr><td><img src=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" height=\"1\" width=\"600\" alt=\"\" /></td></tr>");
			buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"4\" width=\"1\" alt=\"\" /></td></tr>");
			buf.append("</table>");

			if ((!(userRole.equals(Defines.ETS_EXECUTIVE)|| userRole.equals(Defines.WORKSPACE_VISITOR)))
				|| isSuperAdmin) {
				buf.append("<table  cellpadding=\"0\" cellspacing=\"1\" width=\"600\" border=\"0\">");
				buf.append("<tr>");

				//add document
				if (cat.getCatType() != 0) {
					buf.append("<td>");
					buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
					buf.append("<td valign=\"top\" algin=\"right\"><a href=\"ETSProjectsServlet.wss?action=adddoc&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&linkid="
							+ linkid
							+ "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw_c.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"add new document\" /></a>&nbsp;</td>");
					buf.append(
						"<td valign=\"top\" align\"left\"><a href=\"ETSProjectsServlet.wss?action=adddoc&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&linkid="
							+ linkid
							+ "\" class=\"fbox\">Add new document</a></td>");
					buf.append("</tr></table>");
					buf.append("</td>");
				}

				//add category
				buf.append("<td>");
				buf.append(
					"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
				buf.append(
					"<td valign=\"top\" align=\"right\"><a href=\"ETSProjectsServlet.wss?action=addcat&proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ linkid
						+ "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"add new subfolder\" /></a>&nbsp;</td>");
				buf.append(
					"<td valign=\"top\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=addcat&proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ linkid
						+ "\" class=\"fbox\">Add new subfolder</a></td>");
				buf.append("</tr></table>");
				buf.append("</td>");

				//rename category
				//if cat_type>=2 and (own catgory || priv == update)
				if (hasSubCats
					&& (owns
						|| ETSDatabaseManager.hasProjectPriv(
							es.gIR_USERN,
							Project.getProjectId(),
							Defines.UPDATE)
						|| isSuperAdmin)) {
					buf.append("<td>");

					buf.append("<table  cellpadding=\"0\" cellspacing=\"0\">");
					buf.append("<tr>");
					buf.append(
						"<td valign=\"top\" align=\"right\"><a href=\"ETSProjectsServlet.wss?action=updatecat&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&linkid="
							+ linkid
							+ "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"rename subfolder\" /></a>&nbsp;</td>");
					buf.append(
						"<td valign=\"top\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=updatecat&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&linkid="
							+ linkid
							+ "\" class=\"fbox\">Update subfolder</a>");
					buf.append("</td></tr></table>");

					buf.append("</td>");
				}

				//move category
				if (hasSubCats
					&& (owns
						|| ETSDatabaseManager.hasProjectPriv(
							es.gIR_USERN,
							Project.getProjectId(),
							Defines.UPDATE)
						|| isSuperAdmin)) {
					buf.append("<td>");
					buf.append("<table  cellpadding=\"0\" cellspacing=\"0\">");
					buf.append("<tr>");
					buf.append(
						"<td valign=\"top\" align=\"right\"><a href=\"ETSProjectsServlet.wss?action=movcat&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&linkid="
							+ linkid
							+ "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"move a folder\" /></a>&nbsp;</td>");
					buf.append(
						"<td valign=\"top\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=movecat&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&linkid="
							+ linkid
							+ "\" class=\"fbox\">Move subfolder</a>");
					buf.append("</td></tr></table>");
					buf.append("</td>");
				}

				//delete category
				if (hasSubCats
					&& (owns
						|| ETSDatabaseManager.hasProjectPriv(
							es.gIR_USERN,
							Project.getProjectId(),
							Defines.DELETE)
						|| isSuperAdmin)) {
					buf.append("<td>");
					buf.append("<table  cellpadding=\"0\" cellspacing=\"0\">");
					buf.append("<tr>");
					buf.append(
						"<td valign=\"top\" align=\"right\"><a href=\"ETSProjectsServlet.wss?action=delcat&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&linkid="
							+ linkid
							+ "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"delete a folder\" /></a>&nbsp;</td>");
					buf.append(
						"<td valign=\"top\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=delcat&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&linkid="
							+ linkid
							+ "\" class=\"fbox\">Delete subfolder</a>");
					buf.append("</td></tr></table>");
					buf.append("</td>");
				}

				if (cat.getCatType() == 0
					&& (ETSDatabaseManager
						.hasProjectPriv(
							es.gIR_USERN,
							Project.getProjectId(),
							Defines.ADMIN)
						|| isSuperAdmin)) {
					buf.append("<td>");
					buf.append("<table  cellpadding=\"0\" cellspacing=\"0\">");
					buf.append("<tr>");
					buf.append(
						"<td valign=\"top\" align=\"right\"><a href=\"ETSProjectsServlet.wss?action=report&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&linkid="
							+ linkid
							+ "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"delete a folder\" /></a>&nbsp;</td>");
					buf.append(
						"<td valign=\"top\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=report&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&linkid="
							+ linkid
							+ "\" class=\"fbox\">Document access history</a>");
					buf.append("</td></tr></table>");
					buf.append("</td>");
				}

				buf.append("</tr>");
				buf.append("</table>");

				//to add doc line for top cat
				if (cat.getCatType() == 0) {
					buf.append("<table cellpadding=\"0\" cellspacing=\"0\">");
					buf.append(
						"<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"4\" width=\"1\" alt=\"\" /></td></tr>");

					buf.append(
						"<tr><td valign=\"top\" align=\"left\" class=\"small\">");
					buf.append(
						"*To add a document, please select a folder to place it in.");
					buf.append("</td></tr>");
					buf.append("</table>");
				}

			}
		}
		catch (SQLException se) {
			//System.out.println("sql error in docman print cat action buttons= " + se);
		}
		catch (Exception e) {
			//System.out.println("except error in docman print cat action buttons= " + e);
		}

		writer.println(buf.toString());
	}

	private String printDocActionButtons(ETSDoc doc) {
		StringBuffer buf = new StringBuffer();
		boolean print_flag = false;

		buf.append("<br />");
		buf.append(
			"<table cellpadding=\"0\" cellspacing=\"1\" width=\"600\" border=\"0\">");
		buf.append(
			"<tr><td><img src=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" height=\"1\" width=\"600\" alt=\"\" /></td></tr>");
		buf.append(
			"<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"4\" width=\"1\" alt=\"\" /></td></tr>");
		buf.append("</table>");

		try {
			if ((!(userRole.equals(Defines.ETS_EXECUTIVE) || userRole.equals(Defines.WORKSPACE_VISITOR)))|| isSuperAdmin) {
				//update doc properties
				// if user=author or if priv=update
				if (((doc.getUserId().equals(es.gIR_USERN) || ETSDatabaseManager.hasProjectPriv(es.gIR_USERN,Project.getProjectId(),Defines.UPDATE)
					|| isSuperAdmin)) && doc.isLatestVersion()) {
					if (!print_flag) {
						buf.append(
							"<table  cellpadding=\"0\" cellspacing=\"1\" width=\"600\" border=\"0\">");
						buf.append("<tr>");
						print_flag = true;
					}

					buf.append("<td>");
					buf.append(
						"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
					buf.append("<td valign=\"top\" align=\"right\">");
					//update doc properties
					buf.append(
						"<a href=\"ETSProjectsServlet.wss?action=updatedocprop&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&docid="
							+ doc.getId()
							+ "&linkid="
							+ linkid
							+ "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"Update document properties\" /></a>&nbsp;</td>");
					buf.append(
						"<td valign=\"top\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=updatedocprop&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&docid="
							+ doc.getId()
							+ "&linkid="
							+ linkid
							+ "\" class=\"fbox\">Update properties</a>");
					buf.append("</td></tr></table>");
					buf.append("</td>");
				}

				//update doc file
				if (((doc.getUserId().equals(es.gIR_USERN)
					|| ETSDatabaseManager.hasProjectPriv(es.gIR_USERN,Project.getProjectId(),Defines.UPDATE)
					|| isSuperAdmin)) && doc.isLatestVersion()) {
					if (!print_flag) {
						buf.append(
							"<table  cellpadding=\"0\" cellspacing=\"1\" width=\"600\" border=\"0\">");
						buf.append("<tr>");
						print_flag = true;
					}

					buf.append("<td>");
					buf.append(
						"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
					buf.append("<td valign=\"top\" align=\"right\">");
					buf.append(
						"<a href=\"ETSProjectsServlet.wss?action=updatedoc&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&docid="
							+ doc.getId()
							+ "&linkid="
							+ linkid
							+ "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"Upload new version \" /></a>&nbsp;</td>");
					buf.append(
						"<td valign=\"top\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=updatedoc&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&docid="
							+ doc.getId()
							+ "&linkid="
							+ linkid
							+ "\" class=\"fbox\">Upload new version </a>");
					buf.append("</td></tr></table>");
					buf.append("</td>");
				}

				//delete doc
				if (doc.getUserId().equals(es.gIR_USERN)
					|| ETSDatabaseManager.hasProjectPriv(
						es.gIR_USERN,
						Project.getProjectId(),
						Defines.DELETE)
					|| isSuperAdmin) {
					if (!print_flag) {
						buf.append(
							"<table  cellpadding=\"0\" cellspacing=\"1\" width=\"600\" border=\"0\">");
						buf.append("<tr>");
						print_flag = true;
					}

					buf.append("<td>");
					buf.append(
						"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
					buf.append("<td valign=\"top\" align=\"right\">");
					buf.append(
						"<a href=\"ETSProjectsServlet.wss?action=deldoc&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&docid="
							+ doc.getId()
							+ "&linkid="
							+ linkid
							+ "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"Delete document\" /></a>&nbsp;</td>");
					buf.append(
						"<td valign=\"top\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=deldoc&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&docid="
							+ doc.getId()
							+ "&linkid="
							+ linkid
							+ "\" class=\"fbox\">Delete document</a>");
					buf.append("</td></tr></table>");
					buf.append("</td>");
				}

				//move doc
				if (((doc.getUserId().equals(es.gIR_USERN)
					|| ETSDatabaseManager.hasProjectPriv(es.gIR_USERN,Project.getProjectId(),Defines.UPDATE)
					|| isSuperAdmin)) && doc.isLatestVersion()) {
					if (!print_flag) {
						buf.append(
							"<table  cellpadding=\"0\" cellspacing=\"1\" width=\"600\" border=\"0\">");
						buf.append("<tr>");
						print_flag = true;
					}

					buf.append("<td>");
					buf.append(
						"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
					buf.append("<td valign=\"top\" align=\"right\">");
					buf.append(
						"<a href=\"ETSProjectsServlet.wss?action=movedoc&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&docid="
							+ doc.getId()
							+ "&linkid="
							+ linkid
							+ "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"Move document\" /></a>&nbsp;</td>");
					buf.append(
						"<td valign=\"top\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=movedoc&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&docid="
							+ doc.getId()
							+ "&linkid="
							+ linkid
							+ "\" class=\"fbox\">Move document</a>");
					buf.append("</td></tr></table>");
					buf.append("</td>");
				}

			}
		}
		catch (SQLException se) {
			//System.out.println("sql error in docman print doc action buttons= " + se);
		}
		catch (Exception e) {
			//System.out.println("except error in docman print doc action buttons= " + e);
		}

		if (print_flag) {
			buf.append("</tr></table>");
		}

		//writer.println(buf.toString());
		return buf.toString();
	}

	private String printDocCommentButtons(ETSDoc doc,boolean printMore,String currentdocid) {
			StringBuffer buf = new StringBuffer();
			boolean printFlag = false;
			
			buf.append("<br />");
			buf.append("<table cellpadding=\"0\" cellspacing=\"1\" width=\"600\" border=\"0\">");
			buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>");
			buf.append("<tr><td><img src=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" height=\"1\" width=\"600\" alt=\"\" /></td></tr>");
			buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"4\" width=\"1\" alt=\"\" /></td></tr>");
			buf.append("</table>");

			try {
				String curdocid = "";
				if (!doc.isLatestVersion()){
					curdocid = "&currdocid="+currentdocid;
				}

				if ((!(userRole.equals(Defines.ETS_EXECUTIVE) || userRole.equals(Defines.WORKSPACE_VISITOR)))|| isSuperAdmin) {
					printFlag = true;
					buf.append("<table  cellpadding=\"0\" cellspacing=\"1\" width=\"600\" border=\"0\">");
					buf.append("<tr>");
					
					buf.append("<td>");
					buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
					buf.append("<td valign=\"top\" align=\"right\">");
					buf.append("<a href=\"ETSProjectsServlet.wss?action=addcomm&proj="+ Project.getProjectId()
						+ "&tc="+ TopCatId+ "&cc="+ CurrentCatId+ curdocid+"&docid="+ doc.getId()+ "&linkid="+ linkid
						+ "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"Post comment\" /></a>&nbsp;</td>");
					buf.append("<td valign=\"top\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=addcomm&proj="
						+ Project.getProjectId()
						+ "&tc="+ TopCatId+ "&cc="+ CurrentCatId+ curdocid+"&docid="+ doc.getId()+ "&linkid="+ linkid
						+ "\" class=\"fbox\">Post comment</a>");
					buf.append("</td></tr></table>");
					buf.append("</td>");
				}
				
				if (printMore){
					if (!printFlag){
						printFlag = true;
						buf.append("<table  cellpadding=\"0\" cellspacing=\"1\" width=\"600\" border=\"0\">");
						buf.append("<tr>");
					}
					
					buf.append("<td>");
					buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
					buf.append("<td valign=\"top\" align=\"right\">");
					
					buf.append("<a href=\"ETSProjectsServlet.wss?action=allcomm&proj="+ Project.getProjectId()
						+ "&tc="+ TopCatId+ "&cc="+ CurrentCatId+ curdocid+"&docid="+ doc.getId()+ "&linkid="+ linkid
						+ "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"View all comments\" /></a>&nbsp;</td>");
					buf.append("<td valign=\"top\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=allcomm&proj="
						+ Project.getProjectId()+ "&tc="+ TopCatId+ "&cc="+ CurrentCatId+ curdocid+ "&docid="+ doc.getId()+ "&linkid="+ linkid
						+ "\" class=\"fbox\">View all comments</a>");
					buf.append("</td></tr></table>");
					buf.append("</td>");
					
				}
					

				if (printFlag){
					buf.append("</td>");
					buf.append("</tr></table>");
				}
			}
			catch (Exception e) {
				//System.out.println("except error in docman print doc action buttons= " + e);
			}

			return buf.toString();
		}

	private void printDraftDocActionButtons(ETSDoc doc) {
		StringBuffer buf = new StringBuffer();
		boolean print_flag = false;

		buf.append("<br />");
		buf.append(
			"<table cellpadding=\"0\" cellspacing=\"1\" width=\"600\" border=\"0\">");
		buf.append(
			"<tr><td><img src=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" height=\"1\" width=\"600\" alt=\"\" /></td></tr>");
		buf.append(
			"<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"4\" width=\"1\" alt=\"\" /></td></tr>");
		buf.append("</table>");

		try {
			if ((!(userRole.equals(Defines.ETS_EXECUTIVE)
				|| userRole.equals(Defines.WORKSPACE_VISITOR)))
				|| isSuperAdmin) {
				if (doc.getUserId().equals(es.gIR_USERN)
					|| ETSDatabaseManager.hasProjectPriv(
						es.gIR_USERN,
						Project.getProjectId(),
						Defines.UPDATE)
					|| isSuperAdmin) {
					if (!print_flag) {
						buf.append(
							"<table  cellpadding=\"0\" cellspacing=\"1\" width=\"600\" border=\"0\">");
						buf.append("<tr>");
						print_flag = true;
					}

					buf.append("<td>");
					buf.append(
						"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
					buf.append("<td valign=\"top\" align=\"right\">");
					//update doc properties
					buf.append(
						"<a href=\"ETSProjectsServlet.wss?action=updatedocprop&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&docid="
							+ doc.getId()
							+ "&linkid="
							+ linkid
							+ "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"Update document properties\" /></a>&nbsp;</td>");
					buf.append(
						"<td valign=\"top\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=updatedocprop&proj="
							+ Project.getProjectId()
							+ "&tc="+ TopCatId
							+ "&cc="+ CurrentCatId
							+ "&docid="+ doc.getId()
							+ "&linkid="+ linkid
							+ "\" class=\"fbox\">Update properties</a>");
					buf.append("</td></tr></table>");
					buf.append("</td>");
				}

				//repplace doc file
				if (doc.getUserId().equals(es.gIR_USERN)
					|| ETSDatabaseManager.hasProjectPriv(es.gIR_USERN,Project.getProjectId(),Defines.UPDATE)
					|| isSuperAdmin) {
					if (!print_flag) {
						buf.append("<table  cellpadding=\"0\" cellspacing=\"1\" width=\"600\" border=\"0\">");
						buf.append("<tr>");
						print_flag = true;
					}

					buf.append("<td>");
					buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
					buf.append("<td valign=\"top\" align=\"right\">");
					buf.append("<a href=\"ETSProjectsServlet.wss?action=updatedoc&proj="
							+ Project.getProjectId()
							+ "&tc="+ TopCatId
							+ "&cc="+ CurrentCatId
							+ "&docid="+ doc.getId()
							+ "&linkid="+ linkid
							+ "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"Upload new version \" /></a>&nbsp;</td>");
					buf.append("<td valign=\"top\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=replacedoc&proj="
							+ Project.getProjectId()
							+ "&tc="+ TopCatId
							+ "&cc="+ CurrentCatId
							+ "&docid="+ doc.getId()
							+ "&linkid="+ linkid
							+ "\" class=\"fbox\">Replace version </a>");
					buf.append("</td></tr></table>");
					buf.append("</td>");
				}

				//delete doc
				if (doc.getUserId().equals(es.gIR_USERN)
					|| ETSDatabaseManager.hasProjectPriv(
						es.gIR_USERN,
						Project.getProjectId(),
						Defines.DELETE)
					|| isSuperAdmin) {
					if (!print_flag) {
						buf.append("<table  cellpadding=\"0\" cellspacing=\"1\" width=\"600\" border=\"0\">");
						buf.append("<tr>");
						print_flag = true;
					}

					buf.append("<td>");
					buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
					buf.append("<td valign=\"top\" align=\"right\">");
					buf.append("<a href=\"ETSProjectsServlet.wss?action=deldoc&proj="
							+ Project.getProjectId()
							+ "&tc="+ TopCatId
							+ "&cc="+ CurrentCatId
							+ "&docid="+ doc.getId()
							+ "&linkid="+ linkid
							+ "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"Delete document\" /></a>&nbsp;</td>");
					buf.append("<td valign=\"top\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=deldoc&proj="
							+ Project.getProjectId()
							+ "&tc="+ TopCatId
							+ "&cc="+ CurrentCatId
							+ "&docid="+ doc.getId()
							+ "&linkid="+ linkid
							+ "\" class=\"fbox\">Delete document</a>");
					buf.append("</td></tr></table>");
					buf.append("</td>");
				}

				//move doc
				if (doc.getUserId().equals(es.gIR_USERN)
					|| ETSDatabaseManager.hasProjectPriv(es.gIR_USERN,Project.getProjectId(),Defines.UPDATE)
					|| isSuperAdmin) {
					if (!print_flag) {
						buf.append("<table  cellpadding=\"0\" cellspacing=\"1\" width=\"600\" border=\"0\">");
						buf.append("<tr>");
						print_flag = true;
					}

					buf.append("<td>");
					buf.append("<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
					buf.append("<td valign=\"top\" align=\"right\">");
					buf.append("<a href=\"ETSProjectsServlet.wss?action=movedoc&proj="
							+ Project.getProjectId()
							+ "&tc="+ TopCatId
							+ "&cc="+ CurrentCatId
							+ "&docid="+ doc.getId()
							+ "&linkid="+ linkid
							+ "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"Move document\" /></a>&nbsp;</td>");
					buf.append(
						"<td valign=\"top\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=movedoc&proj="
							+ Project.getProjectId()
							+ "&tc="+ TopCatId
							+ "&cc="+ CurrentCatId
							+ "&docid="+ doc.getId()
							+ "&linkid="+ linkid
							+ "\" class=\"fbox\">Move document</a>");
					buf.append("</td></tr></table>");
					buf.append("</td>");
				}

				//publish doc
				if (doc.getUserId().equals(es.gIR_USERN)
					|| ETSDatabaseManager.hasProjectPriv(
						es.gIR_USERN,
						Project.getProjectId(),
						Defines.UPDATE)
					|| isSuperAdmin) {
					if (!print_flag) {
						buf.append(
							"<table  cellpadding=\"0\" cellspacing=\"1\" width=\"600\" border=\"0\">");
						buf.append("<tr>");
						print_flag = true;
					}

					buf.append("<td>");
					buf.append(
						"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
					buf.append("<td valign=\"top\" align=\"right\">");
					buf.append(
						"<a href=\"ETSProjectsServlet.wss?action=publishdoc&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&docid="
							+ doc.getId()
							+ "&linkid="
							+ linkid
							+ "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"Move document\" /></a>&nbsp;</td>");
					buf.append(
						"<td valign=\"top\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=publishdoc&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&docid="
							+ doc.getId()
							+ "&linkid="
							+ linkid
							+ "\" class=\"fbox\">Publish document</a>");
					buf.append("</td></tr></table>");
					buf.append("</td>");
				}

				//send for app doc
				if (doc.getUserId().equals(es.gIR_USERN)
					|| ETSDatabaseManager.hasProjectPriv(
						es.gIR_USERN,
						Project.getProjectId(),
						Defines.UPDATE)
					|| isSuperAdmin) {
					if (!print_flag) {
						buf.append(
							"<table  cellpadding=\"0\" cellspacing=\"1\" width=\"600\" border=\"0\">");
						buf.append("<tr>");
						print_flag = true;
					}

					buf.append("<td>");
					buf.append(
						"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
					buf.append("<td valign=\"top\" align=\"right\">");
					buf.append(
						"<a href=\"ETSProjectsServlet.wss?action=sendappdoc&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&docid="
							+ doc.getId()
							+ "&linkid="
							+ linkid
							+ "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"Move document\" /></a>&nbsp;</td>");
					buf.append(
						"<td valign=\"top\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=sendappdoc&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&docid="
							+ doc.getId()
							+ "&linkid="
							+ linkid
							+ "\" class=\"fbox\">Send for approval</a>");
					buf.append("</td></tr></table>");
					buf.append("</td>");
				}

			}
		}
		catch (SQLException se) {
			//System.out.println("sql error in docman print doc action buttons= " + se);
		}
		catch (Exception e) {
			//System.out.println("except error in docman print doc action buttons= " + e);
		}

		if (print_flag) {
			buf.append("</tr></table>");
		}

		writer.println(buf.toString());
	}

	private void printSubAppDocActionButtons(ETSDoc doc) {
		StringBuffer buf = new StringBuffer();

		buf.append("<br />");
		buf.append(
			"<table cellpadding=\"0\" cellspacing=\"1\" width=\"600\" border=\"0\">");
		buf.append(
			"<tr><td><img src=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" height=\"1\" width=\"600\" alt=\"\" /></td></tr>");
		buf.append(
			"<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"4\" width=\"1\" alt=\"\" /></td></tr>");
		buf.append("</table>");

		try {
			if (doc.getApproverId().equals(es.gIR_USERN)
				|| ETSDatabaseManager.hasProjectPriv(
					es.gIR_USERN,
					Project.getProjectId(),
					Defines.ADMIN)
				|| isSuperAdmin) {
				//update document status

				buf.append(
					"<table  cellpadding=\"0\" cellspacing=\"1\" width=\"600\" border=\"0\">");
				buf.append("<tr>");
				buf.append("<td>");
				buf.append(
					"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
				buf.append("<td valign=\"top\" align=\"right\">");
				//update doc properties
				buf.append(
					"<a href=\"ETSProjectsServlet.wss?action=updatedocstatus&proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ CurrentCatId
						+ "&docid="
						+ doc.getId()
						+ "&linkid="
						+ linkid
						+ "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"Update document status\" /></a>&nbsp;</td>");
				buf.append(
					"<td valign=\"top\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=updatedocstatus&proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ CurrentCatId
						+ "&docid="
						+ doc.getId()
						+ "&linkid="
						+ linkid
						+ "\" class=\"fbox\">Update document status</a>");
				buf.append("</td></tr></table>");
				buf.append("</td>");
				buf.append("</tr></table>");
			}
		}
		catch (SQLException se) {
			//System.out.println("sql error in docman print doc action buttons= " + se);
		}
		catch (Exception e) {
			//System.out.println("except error in docman print doc action buttons= " + e);
		}

		writer.println(buf.toString());
	}

	private String printPrevDocActionButtons(ETSDoc doc, String currdocid) {
		StringBuffer buf = new StringBuffer();

		buf.append("<br />");

		buf.append("<table cellpadding=\"0\" cellspacing=\"1\" width=\"600\" border=\"0\">");
		buf.append("<tr><td><img src=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" height=\"1\" width=\"600\" alt=\"\" /></td></tr>");
		buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"4\" width=\"1\" alt=\"\" /></td></tr>");
		buf.append("</table>");

		try {
			if ((!(userRole.equals(Defines.ETS_EXECUTIVE) || userRole.equals(Defines.WORKSPACE_VISITOR))) || isSuperAdmin) {
				//delete doc
				if (doc.getUserId().equals(es.gIR_USERN) 
					|| ETSDatabaseManager.hasProjectPriv( es.gIR_USERN, Project.getProjectId(), Defines.DELETE)
					|| isSuperAdmin) {
					buf.append( "<table  cellpadding=\"0\" cellspacing=\"1\" width=\"443\" border=\"0\">");
					buf.append("<tr>");
					buf.append("<td>");
					buf.append( "<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
					buf.append("<td valign=\"top\" align=\"right\">");
					buf.append( "<a href=\"ETSProjectsServlet.wss?action=delprevdoc&proj=" + Project.getProjectId()
							+ "&tc=" + TopCatId + "&cc=" + CurrentCatId
							+ "&docid=" + doc.getId() + "&luid=" + currdocid + "&linkid=" + linkid
							+ "\" class=\"fbox\"><img src=\"" + Defines.ICON_ROOT + "fw.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"Delete document\" /></a>&nbsp;</td>");
					buf.append(
						"<td valign=\"top\" align=\"left\"><a href=\"ETSProjectsServlet.wss?action=delprevdoc&proj=" + Project.getProjectId()
							+ "&tc=" + TopCatId + "&cc=" + CurrentCatId + "&docid=" + doc.getId()
							+ "&luid=" + currdocid + "&linkid=" + linkid
							+ "\" class=\"fbox\">Delete document</a>");
					buf.append("</td></tr></table>");
					buf.append("</td>");
					buf.append("</tr></table>");
				}
			}
		}
		catch (SQLException se) {
			//System.out.println( "sql error in docman print doc action buttons= " + se);
		}
		catch (Exception e) {
			//System.out.println( "except error in docman print doc action buttons= " + e);
		}

		//writer.println(buf.toString());
		return buf.toString();
	}

	private void displayDocDetails(ETSDoc doc, Vector resUsers,AccessCntrlFuncs acf) {
		StringBuffer buf = new StringBuffer();
		boolean gray_flag = true;
		/*
		int width_name = 307;
		int width_name_prev = 207;
		int width_size = 70;
		int width_type = 50;
		int width_prev = 100;
		*/
		int width_name = 384;
		int width_name_prev = 234;
		int width_size = 100;
		int width_type = 100;
		int width_prev = 150;

		if (doc.hasExpired() && (!(doc.getUserId().equals(es.gIR_USERN) || userRole.equals(Defines.WORKSPACE_OWNER) || isSuperAdmin))) {
			writer.println("You are not allowed to access this document");
			return;
		}
		
		buf.append(displayDocDetailsPart1(doc,resUsers,acf,false));

		buf.append("<table cellpadding=\"2\" cellspacing=\"0\" width=\"600\" border=\"0\">");

		if (doc.hasPreviousVersion() && doc.isLatestVersion()) {
			buf.append("<tr><td colspan=\"5\"><img src=\"//www.ibm.com/i/c.gif\" height=\"5\" width=\"1\" alt=\"\" /></td></tr>");
			buf.append("<tr><th  id=\"doc_name\" colspan=\"2\" class=\"small\" align=\"left\">Name</th><th  id=\"doc_size\" class=\"small\" align=\"left\">Size</th><th  id=\"doc_type\" class=\"small\" align=\"left\">Type</th><th  id=\"doc_prev\" class=\"small\" align=\"left\">Previous version</th></tr>");
			buf.append("<tr><td colspan=\"5\"><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"1\" alt=\"\" /></td></tr>");

		}
		else {
			buf.append("<tr><td colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"8\" width=\"1\" alt=\"\" /></td></tr>");
			buf.append("<tr><th  id=\"doc_name\" colspan=\"2\" class=\"small\" align=\"left\">Name</th><th id=\"doc_size\" class=\"small\" align=\"left\">Size</th><th id=\"doc_type\" class=\"small\" align=\"left\">Type</th></tr>");
			buf.append("<tr><td colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"1\" alt=\"\" /></td></tr>");

		}

		//back to parent category
		try {
			ETSCat parent = ETSDatabaseManager.getCat(CurrentCatId);

			if (gray_flag) {
				buf.append("<tr style=\"background-color:#eeeeee\">");
				gray_flag = false;
			}
			else {
				buf.append("<tr>");
				gray_flag = true;
			}
			buf.append("<td width=\"16\" height=\"17\" align=\"left\" valign=\"top\"><img src=\""
					+ Defines.SERVLET_PATH
					+ "ETSImageServlet.wss?proj=ETS_BACK_IMG&mod=0\" width=\"12\" height=\"8\" alt=\"back\" /></td>");
			//img
			buf.append(
				"<td headers=\"doc_name\" height=\"17\" align=\"left\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?proj="
					+ Project.getProjectId()+ "&tc="+ TopCatId+ "&cc="+ parent.getId()
					+ "&linkid="+ linkid+ "\" class=\"fbox\">Back to '"+ parent.getName()+ "'</a></td>");
			//filename

			buf.append("<td headers=\"doc_size\" height=\"17\"> &nbsp; </td>");
			//Size
			buf.append("<td headers=\"doc_type\" height=\"17\" align=\"center\" class=\"small\"> &nbsp; </td>");
			//format
			if (doc.hasPreviousVersion() && doc.isLatestVersion()) {
				buf.append("<td headers=\"doc_prev\" height=\"17\"> &nbsp; </td>");
				//prev
			}
			buf.append("</tr>");
		}
		catch (SQLException se) {
			//System.out.println("sql error in getCat in docman for display doc details= " + se);
		}
		catch (Exception e) {
			//System.out.println("ex error in getCat in docman for display doc details= " + e);
		}

		//document
		if (gray_flag) {
			buf.append("<tr style=\"background-color:#eeeeee\">");
			gray_flag = false;
		}
		else {
			buf.append("<tr>");
			gray_flag = true;
		}

		/*
		if(doc.getDocStatus()==Defines.DOC_DRAFT)
			buf.append("<td width=\"16\" height=\"17\" valign=\"top\"><img src=\""+Defines.SERVLET_PATH+"ETSImageServlet.wss?proj=ETS_DOC_DRAFT_IMG&mod=0\" width=\"12\" height=\"12\" alt=\"document\" /></td>"); //img
		else if (doc.getDocStatus()==Defines.DOC_SUB_APP)
			buf.append("<td width=\"16\" height=\"17\" valign=\"top\"><img src=\""+Defines.SERVLET_PATH+"ETSImageServlet.wss?proj=ETS_DOC_SUB_APP_IMG&mod=0\" width=\"12\" height=\"12\" alt=\"document\" /></td>"); //img
		else
		*/
		buf.append("<td width=\"16\" height=\"17\" valign=\"top\"><img src=\""
				+ Defines.SERVLET_PATH
				+ "ETSImageServlet.wss?proj=ETS_DOC_IMG&mod=0\" width=\"12\" height=\"12\" alt=\"document\" /></td>");
		//img

		int docsize = doc.getSize();
		String docsizeStr = docsize + " Bytes";
		if (docsize >= 1024) {
			docsize = docsize / 1024;
			docsize = Math.round(docsize);
			docsizeStr = docsize + " KB";
		}

		int colspan = 4;

		if (doc.hasPreviousVersion() && doc.isLatestVersion()) {
			buf.append("<td headers=\"doc_name\" align=\"left\" valign=\"top\" width=\""
					+ width_name_prev+ "\"><a href=\"ETSContentDeliveryServlet.wss/"
			//+ doc.getFileName()
			+URLEncoder.encode(doc.getFileName())
				+ "?projid="+ Project.getProjectId()
				+ "&docid="+ doc.getId()
				+ "&linkid="+ linkid+ "\" target=\"new\">"
				+ doc.getFileName()
				+ "</a></td>");
			buf.append("<td headers=\"doc_size\" align=\"left\" valign=\"top\" width=\""+ width_size+ "\">"
					+ docsizeStr+ "</td>");
			buf.append("<td headers=\"doc_type\" align=\"left\" valign=\"top\" width=\""+ width_type+ "\">"
					+ (doc.getFileType()).toLowerCase()+ "</td>");
			buf.append("<td headers=\"doc_prev\" align=\"left\" valign=\"top\" width=\""
					+ width_prev
					+ "\"><a href=\"ETSProjectsServlet.wss?action=prev&proj="
					+ Project.getProjectId()
					+ "&tc="
					+ TopCatId
					+ "&cc="
					+ CurrentCatId
					+ "&docid="
					+ doc.getId()
					+ "&linkid="
					+ linkid
					+ "\">View</a></td>");
			colspan = 5;
		}
		else {
			buf.append(
					"<td headers=\"doc_name\" align=\"left\" valign=\"top\" width=\""
					+ width_name
					+ "\"><a href=\"ETSContentDeliveryServlet.wss/"
			//+ doc.getFileName()
			+URLEncoder.encode(doc.getFileName())
				+ "?projid="
				+ Project.getProjectId()
				+ "&docid="
				+ doc.getId()
				+ "&linkid="
				+ linkid
				+ "\" target=\"new\">"
				+ doc.getFileName()
				+ "</a></td>");
			buf.append(
				"<td headers=\"doc_size\" align=\"left\" valign=\"top\" width=\""
					+ width_size
					+ "\">"
					+ docsizeStr
					+ "</td>");
			buf.append(
				"<td headers=\"doc_type\" align=\"left\" valign=\"top\" width=\""
					+ width_type
					+ "\">"
					+ (doc.getFileType()).toLowerCase()
					+ "</td>");
			colspan = 4;
		}

		buf.append("</tr>");
		buf.append("</table>");

		buf.append(printDocActionButtons(doc));
		buf.append(displayRecentCommentsSection(doc,true,""));
		
		
		writer.println(buf.toString());
	}
	
	private String displayDocDetailsPart1(ETSDoc doc, Vector resUsers,AccessCntrlFuncs acf, boolean prevVersion){
		StringBuffer buf = new StringBuffer();
		
		buf.append("<table cellpadding=\"0\" cellspacing=\"1\" width=\"600\" border=\"0\">");
		if (!doc.isLatestVersion() && !prevVersion) {
			buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>");
			buf.append("<tr><td colspan=\"2\" align=\"left\"><span style=\"color:#ff3333\">This is not the most recent version of this document.</span></td></tr>");
		}

		buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>");
		if (doc.isLatestVersion()){
			buf.append("<tr><td colspan=\"2\" align=\"left\"><b>Document details</b></td></tr>");
		}
		else{
			buf.append("<tr><td colspan=\"2\" align=\"left\"><b>Previous version details</b></td></tr>");
		}
		buf.append("<tr><td colspan=\"2\" align=\"left\"><img src=\"//www.ibm.com/i/c.gif\" height=\"7\" width=\"1\" alt=\"\" /></td></tr>");

		buf.append("<tr><td width=\"25%\" valign=\"top\" nowrap=\"nowrap\" class=\"small\">Document name:&nbsp;</td><td align=\"left\" valign=\"top\" width=\"75%\" class=\"small\">"
				+ doc.getName()+ "</td></tr>");
		if (!doc.getDescription().equals("")) {
			buf.append("<tr><td width=\"25%\" valign=\"top\" class=\"small\">Description:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\">"
					+ doc.getDescription()+ "</td></tr>");
		}
		else {
			buf.append("<tr><td width=\"25%\" valign=\"top\" class=\"small\">Description:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\">&nbsp;</td></tr>");
		}

		if (!doc.getKeywords().equals("")) {
			buf.append("<tr><td width=\"25%\" valign=\"top\" class=\"small\">Keywords:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\">"
					+ doc.getKeywords()+ "</td></tr>");
		}
		else {
			buf.append("<tr><td width=\"25%\" valign=\"top\" class=\"small\">Keywords:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\">&nbsp;</td></tr>");
		}
		String author = "";

		try {
			author = ETSUtils.getUsersName(conn, doc.getUserId());
		}
		catch (Exception e) {
			author = doc.getUserId();
		}

		buf.append("<tr><td width=\"25%\" valign=\"top\" class=\"small\">Author:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\">"
				+ author+ "</td></tr>");
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
		java.util.Date date = new java.util.Date(doc.getUploadDate());
		String dateStr = df.format(date);
		buf.append("<tr><td width=\"25%\" valign=\"top\" class=\"small\">File date:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\">"
				+ dateStr+ "</td></tr>");

		if (doc.getExpiryDate() != 0) {
			java.util.Date exDate = new java.util.Date(doc.getExpiryDate());
			String exDateStr = df.format(exDate);
			buf.append("<tr><td width=\"25%\" valign=\"top\" class=\"small\">Expire date:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\">"
					+ exDateStr);
			if (doc.hasExpired())
				buf.append(
					"&nbsp; <span style=\"color:#ff3333\"><b>EXPIRED</b></span>");
			buf.append("</td></tr>");
		}

		//if ((doc.getUpdateDate() != doc.getUploadDate()) || (doc.getPublishDate() != doc.getUploadDate())){
		if (doc.getUpdateDate() != doc.getUploadDate()) {
			String updater = "";
			try {
				updater = ETSUtils.getUsersName(conn, doc.getUpdatedBy());
			}
			catch (Exception e) {
				updater = doc.getUpdatedBy();
			}

			if (doc.getUpdateDate() != doc.getUploadDate()) {
				date = new java.util.Date(doc.getUpdateDate());
				dateStr = df.format(date);
				buf.append("<tr><td width=\"25%\" valign=\"top\" class=\"small\">Properties Updated:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\">"
						+ dateStr+ "</td></tr>");
			}
			/*
			if(doc.getPublishDate() != doc.getUploadDate()){
				date = new java.util.Date(doc.getPublishDate());
				dateStr = df.format(date);
				buf.append("<tr><td width=\"25%\" valign=\"top\" class=\"small\">Published on:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\">"+dateStr+"</td></tr>");	
			}
			*/
			buf.append("<tr><td width=\"25%\" valign=\"top\" class=\"small\">Last updated by:&nbsp;</td><td width=\"75%\" valign=\"top\" align=\"left\" class=\"small\">"
					+ updater+ "</td></tr>");

		}

		/*
		if (doc.getDocStatus()==Defines.DOC_DRAFT){
			buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"600\" alt=\"\" /></td></tr>");
			buf.append("<tr><td width=\"25%\" valign=\"top\" class=\"small\">Status:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\"><span style=\"color:#6699ff\"><b>"+doc.getDocStatusString()+"</span></td></tr>");
		}
		else if(doc.getDocStatus()==Defines.DOC_SUB_APP){
			buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"600\" alt=\"\" /></td></tr>");
			buf.append("<tr><td width=\"25%\" valign=\"top\" class=\"small\">Status:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\"><span style=\"color:#6699ff\"><b>"+doc.getDocStatusString()+"</b></span></td></tr>");
			buf.append("<tr><td width=\"25%\" valign=\"top\" class=\"small\">Approver:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\">"+doc.getApproverId()+"</td></tr>");
		}
		else if(doc.getDocStatus()==Defines.DOC_APPROVED){
			buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"600\" alt=\"\" /></td></tr>");
			buf.append("<tr><td width=\"25%\" valign=\"top\" class=\"small\">Status:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\"><span style=\"color:#6699ff\"><b>"+doc.getDocStatusString()+"</b></span></td></tr>");
			buf.append("<tr><td width=\"25%\" valign=\"top\" class=\"small\">Approved by:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\">"+doc.getApproverId()+"</td></tr>");
		}
		else if(doc.getDocStatus()==Defines.DOC_REJECTED){
			buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"600\" alt=\"\" /></td></tr>");
			buf.append("<tr><td width=\"25%\" valign=\"top\" class=\"small\">Status:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\">"+doc.getDocStatusString()+"</td></tr>");
			buf.append("<tr><td width=\"25%\" valign=\"top\" class=\"small\">Rejected by:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\">"+doc.getApproverId()+"</td></tr>");
		}
		*/

		if (doc.IsDPrivate()){
			buf.append("<tr><td width=\"25%\" valign=\"top\" class=\"small\">Restricted to:&nbsp;</td>");
			buf.append("<td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\">");
			for (int i =0; i<resUsers.size();i++){
				if (i!=0)
					buf.append(", "+((ETSUser)resUsers.elementAt(i)).getUserId());
				else
					buf.append(((ETSUser)resUsers.elementAt(i)).getUserId());
			}
			buf.append("</td></tr>");
		}


		if( (!prevVersion)  && doc.isLatestVersion()){
			try {
				if (doc.getUserId().equals(es.gIR_USERN)|| ETSDatabaseManager.hasProjectPriv(es.gIR_USERN,Project.getProjectId(),Defines.ADMIN)
					|| isSuperAdmin) {
					buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"600\" alt=\"\" /></td></tr>");
					buf.append("<tr><td width=\"25%\" valign=\"top\" class=\"small\"># Hits:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\">"
							+ doc.getDocHits());
					if (doc.getDocHits() > 0) {
						buf.append("&nbsp;&nbsp;<a href=\"ETSProjectsServlet.wss?action=docreport&proj="+ Project.getProjectId()
								+ "&tc="+ TopCatId+ "&cc="+ CurrentCatId+ "&docid="+ doc.getId()+ "&linkid="+ linkid
								+ "\" class=\"fbox\">View access details</a>");
					}
					buf.append("</td></tr>");
				}
			}
			catch (Exception e) {
	
			}
		}



		if (doc.isIbmOnlyOrConf()) {
			buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"600\" alt=\"\" /></td></tr>");
			buf.append("<tr><td colspan=\"2\" class=\"small\">This document has access restricted to IBM team members only.</td></tr>");
		}

		buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>");
		buf.append("<tr><td colspan=\"2\"><img src=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" height=\"1\" width=\"600\" alt=\"\" /></td></tr>");
		buf.append("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>");
		buf.append("</table>");

		return buf.toString(); 
	}
	
	private String displayRecentCommentsSection(ETSDoc doc,boolean preview,String currdocid){
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		StringBuffer buf = new StringBuffer();
		
		String title = "Recent comments";
		if (!preview)
			title = "All document comments";
		
		buf.append("<table cellpadding=\"2\" cellspacing=\"0\" width=\"600\" border=\"0\">");
		buf.append("<tr><td colspan=\"3\"><img src=\"//www.ibm.com/i/c.gif\" height=\"15\" width=\"1\" alt=\"\" /></td></tr>");
		buf.append("<tr><td colspan=\"3\" class=\"tdblue\">&nbsp;"+title+"</td></tr>");
		buf.append("<tr><th id=\"name\" class=\"small\" align=\"left\">User</th><th id=\"date\" class=\"small\" align=\"left\">Date</th><th id=\"comment\" class=\"small\" align=\"left\">Comment</th></tr>");

		boolean printFlag = false;
		boolean printMore = false;
		boolean gray_flag = true;
		
		Vector dc = new Vector();
		try{
			dc = ETSDatabaseManager.getDocComments(doc.getId(),Project.getProjectId());
			for (int ci = 0; ci < dc.size(); ci++){
				ETSDocComment comm = (ETSDocComment)dc.elementAt(ci);
				if (gray_flag) {
					buf.append("<tr style=\"background-color:#eeeeee\">");
					gray_flag = false;
				}
				else {
					buf.append("<tr>");
					gray_flag = true;
				}
				buf.append("<td width=\"150\" valign=\"top\" align=\"left\">"+ETSUtils.getUsersName(conn,comm.getUserId())+"</td>");
				java.util.Date date = new java.util.Date(comm.getCommentDate());
				buf.append("<td width=\"100\" valign=\"top\" align=\"left\">"+df.format(date)+"</td>");
				if(preview)
					buf.append("<td width=\"350\" valign=\"top\" align=\"left\">"+comm.formatComment()+"</td>");
				else
					buf.append("<td width=\"350\" valign=\"top\" align=\"left\">"+comm.getComment()+"</td>");	
				
				buf.append("</tr>");
				if (comm.getComment().length() > 147){
					printMore = true;	
				}
				printFlag = true;				
			}
		}
		catch(Exception e){
			
		}
		
		if (!printFlag){
			buf.append("<tr><td colspan=\"3\">There are no comments.</td></tr>");
		}
		
		if(preview){
			buf.append(printDocCommentButtons(doc,(printMore || dc.size()>5),currdocid));
		}
		else{
			buf.append("<tr><td colspan=\"3\"><table cellpadding=\"0\" cellspacing=\"1\" width=\"600\" border=\"0\">");
			buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>");
			buf.append("<tr><td><img src=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" height=\"1\" width=\"600\" alt=\"\" /></td></tr>");
			buf.append("<tr><td><img src=\"//www.ibm.com/i/c.gif\" height=\"4\" width=\"1\" alt=\"\" /></td></tr>");
			buf.append("</table></td></tr>");
			if(doc.isLatestVersion()){
				buf.append("<tr><td colspan=\"3\"><table cellspacing=\"0\"cellpadding=\"0\"><tr><td valign=\"middle\"><a href=\"ETSProjectsServlet.wss?action=details&proj="+ Project.getProjectId()+ "&tc="+ TopCatId
					+ "&cc="+ CurrentCatId+ "&linkid="+ linkid+"&docid="+doc.getId()
					+ "\"><img src=\""+Defines.ICON_ROOT+ "bk.gif\"\" border=\"0\"  height=\"16\" width=\"16\" alt=\"Back to details\" /></a></td>");
				buf.append("<td valign=\"middle\"><a href=\"ETSProjectsServlet.wss?action=details&proj="+ Project.getProjectId()+ "&tc="+ TopCatId
					+ "&cc="+ CurrentCatId+ "&linkid="+ linkid+"&docid="+doc.getId()+ "\"> Back to '"+doc.getName()+"' details</a></td></tr></table></td></tr>");
			}
			else{
				buf.append("<tr><td colspan=\"3\"><table cellspacing=\"0\"cellpadding=\"0\"><tr><td valign=\"middle\"><a href=\"ETSProjectsServlet.wss?action=prevdetails&proj="+ Project.getProjectId()+ "&tc="+ TopCatId
					+ "&cc="+ CurrentCatId+ "&linkid="+ linkid+"&currdocid="+currdocid+"&docid="+doc.getId()
					+ "\"><img src=\""+Defines.ICON_ROOT+ "bk.gif\"\" border=\"0\"  height=\"16\" width=\"16\" alt=\"Back to details\" /></a></td>");
				buf.append("<td valign=\"middle\"><a href=\"ETSProjectsServlet.wss?action=prevdetails&proj="+ Project.getProjectId()+ "&tc="+ TopCatId
					+ "&cc="+ CurrentCatId+ "&linkid="+ linkid+"&currdocid="+currdocid+"&docid="+doc.getId()+ "\"> Back to '"+doc.getName()+"' details</a></td></tr></table></td></tr>");
			}
		}
		
		buf.append("<tr><td colspan=\"3\"><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"1\" alt=\"\" /></td></tr>");
		buf.append("</table>");

		return buf.toString();		
		
	}

	public void displayPreviousDocs(
		Vector prev_docs,
		ETSDoc currentDoc,
		AccessCntrlFuncs acf) {
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		StringBuffer buf = new StringBuffer();
		boolean gray_flag = true;
		boolean prev_flag = false;
		boolean internal = false;
		boolean ibmonlyFlag = false;
		boolean ibmconfFlag = false;
		boolean bExpDoc = false;
		boolean bResDoc = false;

		int width_name = 219;
		int width_mod = 100;
		int width_type = 50;
		int width_author = 190;
		int width_info = 25;

		try {

			if ((es.gDECAFTYPE.trim()).equalsIgnoreCase("I")) {
				internal = true;
			}

			if (currentDoc.isIbmOnlyOrConf() && !internal) {
				buf.append("You are not authorized to view this information.");
				return;
			}

			if (currentDoc.hasExpired()&& (!(currentDoc.getUserId().equals(es.gIR_USERN)|| userRole.equals(Defines.WORKSPACE_OWNER)|| isSuperAdmin))) {
				writer.println("You are not allowed to access this document");
				return;
			}

			buf.append("<table  cellpadding=\"2\" cellspacing=\"0\" width=\"600\" border=\"0\">");
			buf.append("<tr><td colspan=\"6\" align=\"left\"><img src=\"//www.ibm.com/i/c.gif\" height=\"15\" width=\"1\" alt=\"\" /></td></tr>");
			buf.append("<tr><td colspan=\"6\" align=\"left\" class=\"small\"><b>Previous versions of \""
					+ currentDoc.getName()+ "\"</b></td></tr>");
			buf.append("<tr><td colspan=\"6\" align=\"left\"><img src=\"//www.ibm.com/i/c.gif\" height=\"15\" width=\"1\" alt=\"\" /></td></tr>");

			buf.append("<tr><th id=\"prev_doc_name\" colspan=\"2\" class=\"small\" align=\"left\">Name</th><th id=\"prev_doc_date\" class=\"small\" align=\"left\">Modified</th><th id=\"prev_doc_type\" class=\"small\" align=\"left\">Type</th><th id=\"prev_doc_author\" class=\"small\" align=\"left\">Author</th><th id=\"prev_doc_details\" class=\"small\">&nbsp;</th></tr>");
			buf.append("<tr><td colspan=\"6\"><img src=\"//www.ibm.com/i/c.gif\" height=\"2\" width=\"1\" alt=\"\" /></td></tr>");

			if (gray_flag) {
				buf.append("<tr style=\"background-color:#eeeeee\">");
				gray_flag = false;
			}
			else {
				buf.append("<tr>");
				gray_flag = true;
			}

			buf.append("<td width=\"16\" height=\"17\" align=\"left\" valign=\"top\"><img src=\""
					+ Defines.SERVLET_PATH+ "ETSImageServlet.wss?proj=ETS_BACK_IMG&mod=0\" width=\"12\" height=\"8\" alt=\"back\" /></td>");
			//img
			buf.append("<td headers=\"prev_doc_name\" height=\"17\" align=\"left\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?action=details&proj="
					+ Project.getProjectId()+ "&tc="+ TopCatId+ "&cc="+ CurrentCatId+ "&docid="+ currentDoc.getId()
					+ "&linkid="+ linkid+ "\" class=\"fbox\">Back to '"+ currentDoc.getName()+ "'</a></td>");
			//filename
			buf.append("<td headers=\"prev_doc_date\" height=\"17\" align=\"left\" valign=\"top\"> &nbsp; </td>");
			//date
			buf.append("<td headers=\"prev_doc_type\" height=\"17\" align=\"left\" valign=\"top\" class=\"small\"> &nbsp; </td>");
			//format
			buf.append("<td headers=\"prev_doc_author\" height=\"17\" align=\"left\" valign=\"top\" class=\"small\"> &nbsp; </td>");
			//author
			buf.append("<td headers=\"prev_doc_details\" height=\"17\" align=\"left\" valign=\"top\"> &nbsp; </td>");
			//details

			buf.append("</tr>");

			if (prev_docs != null) {
				if (prev_docs.size() > 0) {
					for (int i = 0; i < prev_docs.size(); i++) {
						String exStr = "";
						String resStr = "";
						boolean isAuth = false;
						ETSDoc prev_doc = (ETSDoc) prev_docs.elementAt(i);
						if (prev_doc.IsDPrivate()){
							isAuth = ETSDocCommon.isAuthorized(prev_doc.getUserId(),prev_doc.getId(),prev_doc.getProjectId(),userRole,isSuperAdmin,isExecutive,true,false,es.gIR_USERN);
						}
						else
							isAuth = true;
							
						if (isAuth && ((!prev_doc.hasExpired())|| (userRole == Defines.WORKSPACE_OWNER)|| prev_doc.getUserId().equals(es.gIR_USERN)|| isSuperAdmin)) {
							if (!prev_doc.isLatestVersion()) {
								prev_flag = true;
								if (gray_flag) {
									buf.append("<tr style=\"background-color:#eeeeee\">");
									gray_flag = false;
								}
								else {
									buf.append("<tr>");
									gray_flag = true;
								}

								if (prev_doc.hasExpired()) {
									exStr ="<span class=\"small\"><span class=\"ast\"><b>&#8224;</b></span>";
									bExpDoc = true;
								}
								if (prev_doc.IsDPrivate()) {
									resStr ="<span class=\"small\"><span class=\"ast\">#</span>";
									bResDoc = true;
								}


								buf.append("<td width=\"16\" height=\"17\" align=\"left\" valign=\"top\"><img src=\""
										+ Defines.SERVLET_PATH+ "ETSImageServlet.wss?proj=ETS_DOC_IMG&mod=0\" width=\"12\" height=\"12\" alt=\"document\" /></td>");
								//img

								if (prev_doc.getIbmOnly()== Defines.ETS_IBM_CONF) {
									ibmconfFlag = true;
									buf.append("<td headers=\"prev_doc_name\" height=\"17\" width=\""
											+ width_name+ "\" align=\"left\" valign=\"top\"><a href=\"ETSContentDeliveryServlet.wss/"
											+URLEncoder.encode(prev_doc.getFileName())
											+ "?projid="+ Project.getProjectId()+ "&docid="+ prev_doc.getId()
											+ "&linkid="+ linkid+ "\" target=\"new\" class=\"fbox\">"
											+ prev_doc.getName()+ "<span class=\"ast\">**</span>"+ exStr+resStr+"</a></td>");
									//filename
								}
								else if (prev_doc.getIbmOnly()== Defines.ETS_IBM_ONLY) {
									ibmonlyFlag = true;
									buf.append("<td headers=\"prev_doc_name\" height=\"17\" width=\""
											+ width_name+ "\" align=\"left\" valign=\"top\"><a href=\"ETSContentDeliveryServlet.wss/"
											+URLEncoder.encode(prev_doc.getFileName())+ "?projid="+ Project.getProjectId()+ "&docid="+ prev_doc.getId()+ "&linkid="+ linkid
											+ "\" target=\"new\" class=\"fbox\">"+ prev_doc.getName()+ "<span class=\"ast\">*</span>"+ exStr+resStr+ "</a></td>");
									//filename
								}
								else {
									buf.append("<td headers=\"prev_doc_name\" height=\"17\" width=\""
											+ width_name+ "\" align=\"left\" valign=\"top\"><a href=\"ETSContentDeliveryServlet.wss/"
											+URLEncoder.encode(prev_doc.getFileName())+ "?projid="+ Project.getProjectId()
											+ "&docid="+ prev_doc.getId()+ "&linkid="+ linkid+ "\" target=\"new\" class=\"fbox\">"
											+ prev_doc.getName()+ exStr+resStr+ "</a></td>");
									//filename
								}
								java.util.Date date =new java.util.Date(prev_doc.getUploadDate());
								buf.append("<td headers=\"prev_doc_date\" height=\"17\" width=\""
										+ width_mod+ "\" align=\"left\" valign=\"top\">"+ df.format(date)+ "</td>");
								//date
								buf.append("<td headers=\"prev_doc_type\" height=\"17\" width=\""
										+ width_type+ "\" align=\"left\" valign=\"top\" class=\"small\">"+ prev_doc.getFileType()+ "</td>");
								//format
								String username =ETSUtils.getUsersName(conn,prev_doc.getUserId());
								buf.append("<td headers=\"prev_doc_author\" height=\"17\" width=\""
										+ width_author+ "\" align=\"left\" valign=\"top\" class=\"small\">"
										+ username+ "</td>");
								//author
								buf.append("<td headers=\"prev_doc_details\" height=\"17\" width=\""
										+ width_info+ "\" align=\"left\" valign=\"top\" class=\"small\"><a href=\"ETSProjectsServlet.wss?action=prevdetails&proj="
										+ Project.getProjectId()+ "&tc="+ TopCatId+ "&cc="+ CurrentCatId
										+ "&currdocid="+ currentDoc.getId()+ "&docid="+ prev_doc.getId()
										+ "&linkid="+ linkid+ "\"> Details </a></td>");
								//details

								buf.append("</tr>");
							} //not latest version
						}  // if not exp || owner...etc
					}
				}

				if (prev_flag == false) {
					buf.append("<td colspan=\"6\" height=\"17\" class=\"small\">There are no previous versions for this document.</td>");
				}

				if (ibmonlyFlag) {
					buf.append("<tr><td colspan=\"6\"><img src=\"//www.ibm.com/i/c.gif\" height=\"2\" width=\"1\" alt=\"\" /></td></tr>");
					buf.append("<tr><td colspan=\"6\" class=\"small\" valign=\"bottom\"><span class=\"ast\">*</span>Denotes IBM Only document</td></tr>");
				}
				if (ibmconfFlag) {
					buf.append("<tr><td colspan=\"6\"><img src=\"//www.ibm.com/i/c.gif\" height=\"2\" width=\"1\" alt=\"\" /></td></tr>");
					buf.append("<tr><td colspan=\"6\" class=\"small\" valign=\"bottom\"><span class=\"ast\">**</span>Denotes permanent IBM Only document</td></tr>");
				}
				if (bExpDoc) {
					buf.append("<tr><td colspan=\"6\"><img src=\"//www.ibm.com/i/c.gif\" height=\"4\" width=\"1\" alt=\"\" /></td></tr>");
					buf.append("<tr><td colspan=\"6\" class=\"small\" valign=\"bottom\"><span class=\"ast\"><b>&#8224;</b></span>Denotes expired document</td></tr>");
				}
				if (bResDoc) {
					buf.append("<tr><td colspan=\"6\"><img src=\"//www.ibm.com/i/c.gif\" height=\"4\" width=\"1\" alt=\"\" /></td></tr>");
					buf.append("<tr><td colspan=\"6\" class=\"small\" valign=\"bottom\"><span class=\"ast\">#</span>Access to this document is restricted to selected team members</td></tr>");
				}

			}
			buf.append("</table>");
		}
		catch (SQLException se) {
			//System.out.println("sql error in docman display prev docs= " + se);
			se.printStackTrace();
		}
		catch (AMTException ae) {
			//System.out.println("amt error in docman display prev docs= " + ae);
			ae.printStackTrace();
		}
		catch (Exception e) {
			//System.out.println("except error in docman display prev docs= " + e);
			e.printStackTrace();
		}

		writer.println(buf.toString());
	}

	private void displayPreviousDocDetails(ETSDoc doc,String currentdocid, Vector resusers,AccessCntrlFuncs acf) {
		StringBuffer buf = new StringBuffer();
		boolean gray_flag = true;

		int width_name = 334;
		int width_size = 150;
		int width_type = 100;

		if (doc.hasExpired()
			&& (!(doc.getUserId().equals(es.gIR_USERN)|| userRole.equals(Defines.WORKSPACE_OWNER)|| isSuperAdmin))) {
			writer.println("You are not allowed to access this document");
			return;
		}
		
		buf.append(displayDocDetailsPart1(doc,resusers,acf,true));
		buf.append("<table cellpadding=\"2\" cellspacing=\"0\" width=\"600\" border=\"0\">");

		buf.append("<tr><td colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"2\" width=\"1\" alt=\"\" /></td></tr>");
		buf.append("<tr><th id=\"prev_doc_details_name\" colspan=\"2\" class=\"small\" align=\"left\">Name</th><th id=\"prev_doc_details_size\" class=\"small\" align=\"left\">Size</th><th id=\"prev_doc_details_type\" class=\"small\" align=\"left\">Type</th></tr>");
		buf.append("<tr><td colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"1\" width=\"1\" alt=\"\" /></td></tr>");

		//back to parent category
		if (gray_flag) {
			buf.append("<tr style=\"background-color:#eeeeee\">");
			gray_flag = false;
		}
		else {
			buf.append("<tr>");
			gray_flag = true;
		}

		buf.append(
			"<td width=\"16\" height=\"17\" algin=\"left\" valign=\"top\"><img src=\""
				+ Defines.SERVLET_PATH
				+ "ETSImageServlet.wss?proj=ETS_BACK_IMG&mod=0\" width=\"12\" height=\"8\" alt=\"back\" /></td>");
		//img
		buf.append(
			"<td headers=\"prev_doc_details_name\" height=\"17\" algin=\"left\" valign=\"top\" width=\""
				+ width_name
				+ "\"><a href=\"ETSProjectsServlet.wss?action=prev&proj="
				+ Project.getProjectId()
				+ "&tc="
				+ TopCatId
				+ "&cc="
				+ CurrentCatId
				+ "&docid="
				+ currentdocid
				+ "&linkid="
				+ linkid
				+ "\" class=\"fbox\">Back to previous version listing</a></td>");
		//filename

		buf.append(
			"<td headers=\"prev_doc_details_size\" height=\"17\" width=\""
				+ width_size
				+ "\" algin=\"left\" valign=\"top\"> &nbsp; </td>");
		//Size
		buf.append(
			"<td headers=\"prev_doc_details_type\" height=\"17\" width=\""
				+ width_type
				+ "\" algin=\"left\" valign=\"top\" class=\"small\"> &nbsp; </td>");
		//format
		buf.append("</tr>");

		buf.append("<tr>");
		buf.append(
			"<td width=\"16\" height=\"17\" algin=\"left\" valign=\"top\"><img src=\""
				+ Defines.SERVLET_PATH
				+ "ETSImageServlet.wss?proj=ETS_DOC_IMG&mod=0\" width=\"12\" height=\"12\" alt=\"document\" /></td>");
		buf
			.append(
				"<td headers=\"prev_doc_details_name\" height=\"17\" width=\""
				+ width_name
				+ "\" algin=\"left\" valign=\"top\"><a href=\"ETSContentDeliveryServlet.wss/"
		//+ doc.getFileName()
		+URLEncoder.encode(doc.getFileName())
			+ "?projid="
			+ Project.getProjectId()
			+ "&docid="
			+ doc.getId()
			+ "&linkid="
			+ linkid
			+ "\" target=\"new\">"
			+ doc.getFileName()
			+ "</a></td>");
		int docsize = doc.getSize();
		String docsizeStr = docsize + " Bytes";
		if (docsize >= 1024) {
			docsize = docsize / 1024;
			docsize = Math.round(docsize);
			docsizeStr = docsize + " KB";
		}
		buf.append("<td headers=\"prev_doc_details_size\" height=\"17\" width=\""+ width_size
				+ "\" algin=\"left\" valign=\"top\">"+ docsizeStr+ "</td>");
		buf.append("<td headers=\"prev_doc_details_type\" height=\"17\" width=\""+ width_type+ "\" algin=\"left\" valign=\"top\">"
				+ (doc.getFileType()).toLowerCase()+ "</td>");
		int colspan = 4;

		buf.append("</tr>");
		buf.append("</table>");

		buf.append(printPrevDocActionButtons(doc, currentdocid));
		buf.append(displayRecentCommentsSection(doc,true,currentdocid));
		writer.println(buf.toString());
	}

	// 88888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888
	// 88888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888
	// 88888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888
	// 88888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888

	private void printTitle(String title) {
		writer.println("<table width=\"443\"><tr><td><b>"+ title+ "</b></td></tr></table>");
	}

	private void doAddCat(String msg) {
		try {
			Vector notEditor = new Vector(); 
			notEditor.addElement(Defines.WORKSPACE_VISITOR);
			notEditor.addElement(Defines.ETS_EXECUTIVE);
			notEditor.addElement(Defines.WORKSPACE_CLIENT);
			
			if (notEditor.contains(userRole)) {
				printHeader("", "Add folder", false);
				writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
				writer.println("You are not authorized to perform this action");
				return;
			}


			boolean internal = false;
			if ((es.gDECAFTYPE.trim()).equalsIgnoreCase("I")) {
				internal = true;
			}
			
			Vector breadcrumb = new Vector();
			
			ETSProj proj = Project;

			ETSCat parent_cat =	ETSDatabaseManager.getCat(CurrentCatId, Project.getProjectId());

			if (parent_cat != null) {
				/*Vector users = ETSDatabaseManager.getProjMembers(Project.getProjectId(),true);
				if (parent_cat.IsCPrivate()) {
					users =  ETSDatabaseManager.getRestrictedProjMembers(Project.getProjectId(),parent_cat.getId(),true,true);
				}
				else if (parent_cat.isIbmOnlyOrConf()) {
					users = getIBMMembers(users, conn);
				}
				
				if (!isAuthorized(parent_cat.getUserId(),users,false)){
					printHeader("", "Add folder", false);
					writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
					writer.println("<tr><td>");
					writer.println("You are not authorized to perform this action");
					return;
				}
			
				Vector ibmusers = getIBMMembers(users,conn);
				*/
				
				breadcrumb = getBreadcrumb(parent_cat);
				String header = getBreadcrumbTrail(breadcrumb);

				printHeader(header, "Add folder", false);
				writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");

				writer.println("<form action=\"ETSContentManagerServlet.wss\" method=\"post\" name=\"addcatForm\">");
				
				//writer.println(getCatJS(users,ibmusers));
				
				if (!((msg.trim()).equals(""))) {
					if (msg.equals("1")) {
						msg = "Folder Name must be 1-128 characters long";
					}

					writer.println("<table><tr><td><span style=\"color:#ff3333\">"+ msg+ "</span></td></tr></table>");
				}

				writer.println("<input type=\"hidden\" name=\"action\" value=\"addcat2\" />");
				writer.println("<input type=\"hidden\" name=\"proj\" value=\""+ proj.getProjectId()+ "\" />");
				writer.println("<input type=\"hidden\" name=\"tc\" value=\""+ TopCatId+ "\" />");
				writer.println("<input type=\"hidden\" name=\"cc\" value=\""+ parent_cat.getId()+ "\" />");
				writer.println("<input type=\"hidden\" name=\"linkid\" value=\""+ linkid+ "\" />");

				writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
				writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td></tr>");
				writer.println("<tr><td><span class=\"ast\"><b>*</b></span></td><td><span class=\"small\"> Denotes required fields</span></td>");
				writer.println("</tr></table>");

				writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\">");
				writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"20\" height=\"10\" alt=\"\" /></td>");
				writer.println("<td>");

				writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
				writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");
				writer.println("<tr>");
				writer.println("<td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
				writer.println("<td align=\"left\" width=\"99%\"><label for=\"catname\">New folder name: </label></td></tr>");
				writer.println("<tr><td colspan=\"2\"><input type=\"text\" id=\"catname\" size=\"30\" style=\"width:300px\" width=\"300px\" name=\"catname\" value=\"\" /></td></tr>");

				writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");

				//ibmonly
				//writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"15\" alt=\"\" /></td></tr>");
				//writer.println("<tr><td colspan=\"2\" class=\"tdblue\" height=\"18\"> Folder access</td></tr>");
				//writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
				
				if (internal) {
					writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");
					writer.println("<tr><td align=\"left\" colspan=\"2\"><label for=\"ibmonly\"><span style=\"color:#ff3333\"><b>Security classification</b></span>"
						+ "<a href=\"ETSContentManagerServlet.wss?OP=Documents&BlurbField=Sec\"  "
						+ "onclick=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Security','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400');return false\"  "
						+ "onkeypress=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Security','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400')\" >"
						+ "<img border=\"0\" name=\"Help\" src=\""
						+ Defines.ICON_ROOT+"/popup.gif\" width=\"16\" height=\"16\"  alt=\"Click for help on this field\" /></a>&nbsp;"
						+"</label></td></tr>");
				}
				else {
					writer.println("<input type=\"hidden\" name=\"ibmonly\" value=\"0\" />");
				}

				if (parent_cat.getIbmOnly() == Defines.ETS_IBM_CONF) {
					writer.println("<tr><td align=\"left\" width=\"1%\">&nbsp;</td>");
					writer.println("<td align=\"left\" width=\"99%\"><b>Access to this folder will be limited to IBM team members and can never be changed.</b><br />");
					writer.println("<span class=\"small\">To make it public, create it under a different folder.</span><input type=\"hidden\" name=\"ibmonly\" value=\"2\" /></td></tr>");
				}
				else if (parent_cat.getIbmOnly() == Defines.ETS_IBM_ONLY) {
					writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
					writer.println("<td align=\"left\" width=\"99%\">Access limited to:</td></tr>");
					writer.println("<tr><td align=\"left\" valign=\"bottom\" colspan=\"2\">");
					writer.println("<select id=\"ibmonly\" name=\"ibmonly\">"); //onchange=\"security_ch(document.addcatForm.res_users,document.addcatForm.ibmonly)\" 
					writer.println("<option value=\"1\">All IBM team members</option>");
					writer.println("<option value=\"2\">All IBM team members permanently</option>");
					writer.println("</select></td></tr>");
					writer.println("<tr><td class=\"small\" colspan=\"2\">To make accessible to all team members, create it under a different folder or make its parent folder public.</td></tr>");

				}
				else {
					if (internal) {
						writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
						writer.println("<td align=\"left\" width=\"99%\">Access limited to:</td></tr>");
						writer.println("<tr><td align=\"left\" valign=\"bottom\" colspan=\"2\">");
						writer.println("<select id=\"ibmonly\" name=\"ibmonly\">"); //onchange=\"security_ch(document.addcatForm.res_users,document.addcatForm.ibmonly)\"
						writer.println("<option value=\"0\">All team members</option>");
						writer.println("<option value=\"1\">All IBM team members</option>");
						writer.println("<option value=\"2\">All IBM team members permanently</option>");
						writer.println("</select></td></tr>");
					}
				}

				writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td></tr>");
				
				//users
				/*
				writer.println("<tr height=\"21\">");
				writer.println("<td align=\"left\" valign=\"top\" colspan=\"2\">");
				if(parent_cat.IsCPrivate()){
					writer.println("<input type=\"hidden\" name=\"chusers\" value=\"yes\" />");
					writer.println("<b>This folder is restricted. To give access, please select from the following users: </label>");
				}
				else{
					writer.println("<input type=\"checkbox\" name=\"chusers\" value=\"yes\" id=\"users\" />");
					writer.println("<label for=\"chusers\"><b>Restrict to users</b></label>");
				}
				writer.println("</td></tr>");
				
				writer.println("<tr><td align=\"left\" colspan=\"2\" class=\"small\">[For selecting multiple values use Ctrl + Mouse click together.]</td></tr>");
				writer.println("<tr><td colspan=\"2\"><table width=\"443\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
				writer.println("<select id=\"users\" name=\"res_users\" multiple=\"multiple\" size=\"10\" style=\"width:320px\" width=\"320px\">");
				for (int u = 0; u < users.size(); u++) {
					ETSUser user = (ETSUser) users.elementAt(u);
					String username = ETSUtils.getUsersName(conn, user.getUserId());
					writer.println("<option value=\""+ user.getUserId()+ "\">"+ username+ " ["+ user.getUserId()+ "]</option>");
				}
				writer.println("</select>");
				
				if (internal && !parent_cat.isIbmOnlyOrConf()) {
					writer.println("<noscript><tr><td colspan=\"2\" class=\"small\">[If access is restricted to IBM members, only IBM employees selected will be notified.]</td></tr></noscript>");
				}
				
				writer.println("</table>");
				writer.println("</td></tr>");
  				//END users
  				*/
  				
				writer.println("</table>");

				writer.println("<br />");
				writer.println("<input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"submit\" />");
				writer.println("&nbsp; &nbsp; <a href=\"ETSProjectsServlet.wss?proj="+ proj.getProjectId()
						+ "&tc="+ TopCatId
						+ "&cc="+ parent_cat.getId()
						+ "&linkid="+ linkid+ "\">"
						+ "<img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" />Cancel</a>");

				writer.println("</td></tr></table>");
				writer.println("</form>");
			}
			else {
				writer.println("error occured: invalid parent cat id for this user.");
				//System.out.println("put bad parent cat id message here");
			}
		}
		catch (Exception e) {
			writer.println("error occurred");
			//System.out.println("error here");
		}
	}

	private void doDelCat(String msg) {
		try {
			if (userRole.equals(Defines.ETS_EXECUTIVE)
				|| userRole.equals(Defines.WORKSPACE_VISITOR)) {
				printHeader("", "Delete folder", false);
				writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
				writer.println("You are not authorized to perform this action");
				return;
			}

			Vector breadcrumb = new Vector();

			ETSCat current_cat = this_current_cat;
			//databaseManager.getCat(CurrentCatId);
			Vector cats = ETSDocCommon.getValidCatTree(current_cat,es.gIR_USERN,Project.getProjectId(),userRole,Defines.DELETE,true);

			if (current_cat != null) {
				breadcrumb = getBreadcrumb(current_cat);
				String header = getBreadcrumbTrail(breadcrumb);

				printHeader(header, "Delete folder", false);
				writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");

				writer.println("<form action=\"ETSContentManagerServlet.wss\" method=\"get\" name=\"delcatForm\">");
				writer.println("<input type=\"hidden\" name=\"action\" value=\"delcat2\" />");
				writer.println("<input type=\"hidden\" name=\"proj\" value=\""+ Project.getProjectId()+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"cc\" value=\""
						+ current_cat.getId()
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"tc\" value=\""
						+ TopCatId
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"linkid\" value=\""
						+ linkid
						+ "\" />");

				if (!((msg.trim()).equals(""))) {
					if (msg.equals("0")) {
						msg = "Invalid top category id for this user.";
					}
					else if (msg.equals("1")) {
						msg = "Invalid project id for this user.";
					}
					else if (msg.equals("2")) {
						msg = "Invalid category choosen to delete";
					}
					else if (msg.equals("3")) {
						msg = "Error occurred while deleting the folder";
					}
					else if (msg.equals("4")) {
						msg = "You must choose a valid folder to delete";
					}

					writer.println(
						"<table><tr><td><span style=\"color:#ff3333\">"
							+ msg
							+ "</span></td></tr></table>");
				}

				writer.println(
					"<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">");
				writer.println(
					"<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td>");
				writer.println(
					"<tr><td class=\"small\">Only subfolders you are authorized to delete are listed.</td></tr>");
				writer.println(
					"<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"15\" alt=\"\" /></td>");
				writer.println(
					"<tr><td class=\"small\">To be authorized to delete a folder, "
						+ "you must also be authorized to delete all folders and documents under it.</td></tr>");
				writer.println(
					"<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td>");
				writer.println("</table>");

				writer.println(
					"<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
				writer.println("<tr>");
				writer.println(
					"<td class=\"small\" valign=\"top\"><label for=\"delcatid\">Delete folder(s):</label></td>");
				writer.println(
					"<td class=\"small\" valign=\"top\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"1\" alt=\"\" /></td>");
				writer.println("<td class=\"small\">");

				if (cats.size() > 0) {
					writer.println(
						"<select id=\"delcatid\" name=\"delcatid\" multiple=\"multiple\" size=\"6\" style=\"width:250px\" width=\"250px\">");
					for (int i = 0; i < cats.size(); i++) {
						ETSCat c = (ETSCat) cats.elementAt(i);
						writer.println(
							"<option value=\""
								+ c.getId()
								+ "\">"
								+ c.getName()
								+ " </option>");
					}
					writer.println("</select>");
				}
				else {
					writer.println("no folders to delete");
				}

				writer.println("</td>");
				writer.println("</tr>");
				writer.println("</table>");

				writer.println(
					"<br /><br /><table border=\"0\" cellspacing=\"5\" cellpadding=\"5\"><tr>");
				if (cats.size() > 0) {
					writer.println(
						"<td><input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"delete folder\" /></td>");
					writer.println(
						"<td><a href=\"ETSProjectsServlet.wss?proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&linkid="
							+ linkid
							+ "\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" />Cancel</a></td>");
				}
				else {
					writer.println(
						"<td><a href=\"ETSProjectsServlet.wss?proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&linkid="
							+ linkid
							+ "\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_lt.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"OK\" />Go back</a></td>");
				}
				writer.println("</tr></table>");
				writer.println("</form>");
			}
			else {
				writer.println("error occurred:invalid cat id for this user.");
				//System.out.print("put bad current cat id message here");
			}
		}
		catch (Exception e) {
			writer.println("error occurred");
			//System.out.println("error here=" + e);
		}
	}

	private void doUpdateCat(String msg) {
		try {
			if (userRole.equals(Defines.ETS_EXECUTIVE)|| userRole.equals(Defines.WORKSPACE_VISITOR)|| userRole.equals(Defines.WORKSPACE_CLIENT)) {
				printHeader("", "Update folder", false);
				writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
				writer.println("You are not authorized to perform this action");
				return;
			}

			Vector breadcrumb = new Vector();

			ETSProj proj = Project;

			ETSCat cat = ETSDatabaseManager.getCat(CurrentCatId);
			
			/*if (cat.IsCPrivate()){
				if (!isAuthorized(cat.getUserId(),cat.getId(),proj.getProjectId(),false,true)){
					printHeader("", "Update folder", false);
					writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
					writer.println("<tr><td>");
					writer.println("You are not authorized to perform this action");
					return;	
				}	
			}*/

			Vector cats = ETSDocCommon.getValidCatTree(cat,es.gIR_USERN,Project.getProjectId(),userRole,Defines.UPDATE,false);

			if (cat != null) {
				breadcrumb = getBreadcrumb(cat);
				String header = getBreadcrumbTrail(breadcrumb);

				printHeader(header, "Update folder", false);
				writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");

				writer.println("<form action=\"ETSProjectsServlet.wss\" method=\"get\" name=\"updatecatForm\">");
				if (!((msg.trim()).equals(""))) {
					if (msg.equals("1")) {
						msg = "New folder name must be 1-128 characters long";
					}
					else if (msg.equals("2")) {
						msg = "Invalid folder chosen";
					}
					else if (msg.equals("3")) {
						msg = "Invalid project for user.";
					}
					else if (msg.equals("4")) {
						msg = "Error occurred while updating category.";
					}
					writer.println("<table><tr><td><span style=\"color:#ff3333\">"+ msg+ "</span></td></tr></table>");
				}

				writer.println("<input type=\"hidden\" name=\"action\" value=\"updatecatA\" />");
				writer.println("<input type=\"hidden\" name=\"proj\" value=\""+ proj.getProjectId()+ "\" />");
				writer.println("<input type=\"hidden\" name=\"tc\" value=\""+ TopCatId+ "\" />");
				writer.println("<input type=\"hidden\" name=\"cc\" value=\""+ CurrentCatId+ "\" />");
				writer.println("<input type=\"hidden\" name=\"linkid\" value=\""+ linkid+ "\" />");

				writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
				writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td>");
				writer.println("<tr><td class=\"small\">Only subfolders you are authorized to update are listed.</td></tr>");
				writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td>");
				writer.println("</table>");

				writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
				writer.println("<tr><td><span class=\"ast\"><b>*</b></span></td><td><span class=\"small\"> Denotes required fields</span></td></tr>");
				writer.println("</table>");

				writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
				writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"20\" height=\"1\" alt=\"\" /></td>");
				writer.println("<td>");

				writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
				writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"20\" alt=\"\" /></td></tr>");
				writer.println("<td class=\"small\"><span class=\"ast\">*</span><label for\"updatecatid\">Folder to update: </label>&nbsp;&nbsp;</td>");
				writer.println("<td class=\"small\">");

				if (cats.size() > 0) {
					writer.println("<select id=\"updatecatid\" name=\"updatecatid\" style=\"width:300px\" width=\"300px\">");
					for (int i = 0; i < cats.size(); i++) {
						ETSCat c = (ETSCat) cats.elementAt(i);
						writer.println("<option value=\""+ c.getId()+ "\">"+ c.getName()+ "</option>");
					}
					writer.println("</select>");
				}
				else {
					writer.println("You are authorized to update any of the sub folders.");
				}

				writer.println("</td></tr>");
				writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"20\" alt=\"\" /></td></tr>");
				writer.println("</table>");

				if (cats.size() > 0) {
					writer.println("<input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "continue.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"continue\" /> &nbsp;&nbsp;&nbsp;");
				}
				writer.println("<a href=\"ETSProjectsServlet.wss?proj="+ Project.getProjectId()
						+ "&tc="+ TopCatId+ "&cc="+ CurrentCatId+ "&linkid="+ linkid
						+ "\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" />Cancel</a>");

				writer.println("</td></tr></table>");
				writer.println("</form>");

			}
			else {
				writer.println("error occurred: invalid cat id for this user.");
				//System.out.print("put bad cat id message here");
			}
		}
		catch (Exception e) {
			writer.println("error occurred");
			//System.out.print("error here");
		}

	}

	private void doUpdateCatA(String upCatId, String msg) {
		try {
			if (userRole.equals(Defines.ETS_EXECUTIVE) || userRole.equals(Defines.WORKSPACE_VISITOR)|| userRole.equals(Defines.WORKSPACE_CLIENT)) {
				printHeader("", "Update folder", false);
				writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
				writer.println("You are not authorized to perform this action");
				return;
			}
			Vector breadcrumb = new Vector();

			ETSProj proj = Project;

			int catid = new Integer(upCatId).intValue();
			ETSCat thisCat = ETSDatabaseManager.getCat(catid);
			ETSCat CurrCat = ETSDatabaseManager.getCat(CurrentCatId);

			if (thisCat != null) {
				breadcrumb = getBreadcrumb(CurrCat);
				String header = getBreadcrumbTrail(breadcrumb);
				
				printHeader(header,"Update subfolder: " + thisCat.getName(),false);
				writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");

				writer.println("<form action=\"ETSContentManagerServlet.wss\" method=\"post\" name=\"updatecatForm\">");
				if (!((msg.trim()).equals(""))) {
					if (msg.equals("1")) {
						msg = "New folder name must be 1-128 characters long";
					}
					else if (msg.equals("2")) {
						msg = "";
					}
					else if (msg.equals("3")) {
						msg = "";
					}
					else if (msg.equals("4")) {
						msg = "";
					}
					writer.println(
						"<table><tr><td><span style=\"color:#ff3333\">"+ msg+ "</span></td></tr></table>");
				}

				writer.println("<input type=\"hidden\" name=\"action\" value=\"updatecat2\" />");
				writer.println(
					"<input type=\"hidden\" name=\"proj\" value=\""
						+ proj.getProjectId()
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"tc\" value=\""
						+ TopCatId
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"cc\" value=\""
						+ CurrentCatId
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"linkid\" value=\""
						+ linkid
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"updatecatid\" value=\""
						+ upCatId
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"oldibm\" value=\""
						+ thisCat.getIbmOnly()
						+ "\" />");


		
				writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
				writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td>");
				writer.println("<tr><td><span class=\"ast\"><b>*</b></span></td><td><span class=\"small\"> Denotes required fields</span></td></tr>");
				writer.println("</table>");

				writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
				writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"20\" height=\"1\" alt=\"\" /></td>");
				writer.println("<td>");

				writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
				writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");
				writer.println("<tr>");
				writer.println("<td align=\"left\" class=\"small\"><label for=\"catname\"><span class=\"ast\"><b>*</b></span>New folder name:</label></td>");
				writer.println("</tr>");
				writer.println("<tr><td><input type=\"text\" id=\"catname\" size=\"25\" style=\"width:230px\" width=\"230px\" name=\"catname\" value=\""
						+ thisCat.getName()
						+ "\" /></td></tr>");
				writer.println(
					"<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"20\" alt=\"\" /></td></tr>");
				writer.println("</table>");

				writer.println(
					"<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");

				//ibmonly
				if ((es.gDECAFTYPE.trim()).equalsIgnoreCase("I")) {
					writer.println("<td align=\"left\" colspan=\"2\"><label for=\"ibmonly\"><span style=\"color:#ff3333\"><b>Security classification</b></span></label>" 
						+ "<a href=\"ETSContentManagerServlet.wss?OP=Documents&BlurbField=Sec\"  "
						+ "onclick=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Security','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400');return false\"  "
						+ "onkeypress=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Security','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400')\" >"
						+ "<img border=\"0\" name=\"Help\" src=\""
						+ Defines.ICON_ROOT+"/popup.gif\" width=\"16\" height=\"16\"  alt=\"Click for help on this field\" /></a>&nbsp;"
						+"</td></tr>");
					//System.out.println("ibmonly=" + thisCat.getIbmOnly());

					if (CurrCat.getIbmOnly() == Defines.ETS_IBM_CONF || thisCat.getIbmOnly() == Defines.ETS_IBM_CONF) {
						writer.println("<tr><td align=\"left\" colspan=\"2\"><b>Access to this folder is limited to IBM team members and can never be changed.</b><br />");
						writer.println("<input type=\"hidden\" name=\"ibmonly\" value=\"2\" /></td></tr>");
					}
					else if (CurrCat.getIbmOnly() == Defines.ETS_IBM_ONLY) {
						writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
						writer.println("<td align=\"left\" width=\"99%\">Access limited to:</td></tr>");

						writer.println("<tr><td align=\"left\" valign=\"bottom\" colspan=\"2\">");
						writer.println("<select id=\"ibmonly\" name=\"ibmonly\" />");
						
						if (thisCat.getIbmOnly() == Defines.ETS_IBM_ONLY)
							writer.println("<option value=\"1\" selected=\"selected=\">All IBM team members</option>");
						else
							writer.println("<option value=\"1\">All IBM team members</option>");

						if (thisCat.getIbmOnly() == Defines.ETS_IBM_CONF)
							writer.println("<option value=\"2\" selected=\"selected=\">All IBM team members permanently</option>");
							else
							writer.println("<option value=\"2\">All IBM team members permanently</option>");

						writer.println("</select></td></tr>");
						if (thisCat.getIbmOnly() == Defines.ETS_IBM_ONLY) {
							writer.println("<tr><td align=\"left\" valign=\"bottom\" colspan=\"2\" class=\"small\">"
									+ "**If changing folder to restrict to all IBM team members permanently, all folders and documents will "
									+ "also be changed.</td></tr>");
						}
					
						writer.println("<tr><td class=\"small\" colspan=\"2\">**To make accessible to all team members, make its parent folder public.</td></tr>");
					}
					else {
						writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
						writer.println("<td align=\"left\" width=\"99%\">Access limited to:</td></tr>");
						writer.println("<tr><td align=\"left\" valign=\"bottom\" colspan=\"2\">");
						writer.println("<select id=\"ibmonly\" name=\"ibmonly\" />");
						if (thisCat.getIbmOnly() == Defines.ETS_PUBLIC)
							writer.println("<option value=\"0\" selected=\"selected=\">All team members</option>");
						else
							writer.println("<option value=\"0\">All team members</option>");

						if (thisCat.getIbmOnly() == Defines.ETS_IBM_ONLY)
							writer.println("<option value=\"1\" selected=\"selected=\">All IBM team members</option>");
						else
							writer.println("<option value=\"1\">All IBM team members</option>");

						if (thisCat.getIbmOnly() == Defines.ETS_IBM_CONF)
							writer.println("<option value=\"2\" selected=\"selected=\">All IBM team members permanently</option>");
						else
							writer.println("<option value=\"2\">All IBM team members permanently</option>");
						writer.println("</select></td></tr>");

						if (thisCat.getIbmOnly()==Defines.ETS_IBM_ONLY) {
							writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td></tr>");
							writer.println("<tr><td colspan=\"2\" class=\"small\" align=\"left\" valign=\"bottom\">The following options only apply when changing" +
								" the security classification of this folder to be accessible to all team members.");
							writer.println(" If changing to permanently restricted to IBM members all subfolders and subdocuments will automatically be changed.</td></tr>");
							writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");

							writer.println("<tr><td colspan=\"2\"><table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
							writer.println("<tr><td rowspan=\"3\"><img src=\"//www.ibm.com/i/c.gif\" width=\"20\" height=\"1\" alt=\"\" /></td>");
							writer.println("<td><table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
							writer.println("<tr><td valign=\"bottom\"><input type=\"radio\" id=\"catonly\" name=\"propOpt\" value=\"1\" checked=\"checked\" /></td>");
							writer.println("<td class=\"small\" align=\"left\" valign=\"bottom\"><label for=\"catonly\">Update only this folder.</label></td></tr>");

							writer.println("<tr><td valign=\"bottom\"><input type=\"radio\" id=\"subdocsonly\" name=\"propOpt\" value=\"2\" /></td>");
							writer.println("<td class=\"small\" align=\"left\" valign=\"bottom\"><label for=\"subdocsonly\">Update this folder and its subdocuments you are authorized to change.</label></td></tr>");

							writer.println("<tr><td valign=\"bottom\"><input type=\"radio\" id=\"propall\" name=\"propOpt\" value=\"3\" /></td>");
							writer.println("<td class=\"small\" align=\"left\" valign=\"bottom\"><label for=\"propall\">Update this folder and all of its subfolders and subdocuments you are authorized to change.</label></td></tr>");

							writer.println("</table></td></tr>");
							writer.println("</table></td></tr>");
						}
						else if (thisCat.getIbmOnly() == Defines.ETS_PUBLIC) {
							writer.println("<tr><td align=\"left\" valign=\"bottom\" colspan=\"2\" class=\"small\">"
									+ "**If changing security classification of folder, all non-permanent IBM accessible subfolders and subdocuments will "
									+ "also be changed.</td></tr>");
						}
					}
					
					writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"30\" alt=\"\" /></td></tr>");
				}
				else {
					writer.println("<input type=\"hidden\" name=\"ibmonly\" value=\"0\" />");
				}

				writer.println("</table>");

				writer.println("<input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"submit\" /> &nbsp;&nbsp;&nbsp;");
				writer.println(
					"<a href=\"ETSProjectsServlet.wss?action=updatecat&proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ linkid
						+ "\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_lt.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Back\" />Back</a> &nbsp;&nbsp;&nbsp;");
				writer.println(
					"<a href=\"ETSProjectsServlet.wss?proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ linkid
						+ "\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" />Cancel</a>");

				writer.println("</td></tr></table>");
				writer.println("</form>");

			}
			else {
				writer.println("error occurred: invalid cat id for this user.");
				//System.out.print("put bad cat id message here");
			}
		}
		catch (Exception e) {
			writer.println("error occurred");
			//System.out.print("error here");
		}

	}

	//updateCatConf(upid,name,oldibmonly,ibmonly,opt);
	private void doUpdateCatC(
		String upCatId,
		String name,
		String ibmonly,
		String opt) {
		try {
			if (userRole.equals(Defines.ETS_EXECUTIVE) || userRole.equals(Defines.WORKSPACE_VISITOR)) {
				printHeader("", "Update folder", false);
				writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
				writer.println("You are not authorized to perform this action");
				return;
			}
			Vector breadcrumb = new Vector();

			ETSProj proj = Project;

			int catid = new Integer(upCatId).intValue();
			ETSCat thisCat = ETSDatabaseManager.getCat(catid);
			ETSCat CurrCat = ETSDatabaseManager.getCat(CurrentCatId);

			if (thisCat != null) {
				breadcrumb = getBreadcrumb(CurrCat);
				String header = getBreadcrumbTrail(breadcrumb);

				printHeader(
					header,
					"Update subfolder: " + thisCat.getName(),
					false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");

				writer.println(
					"<form action=\"ETSContentManagerServlet.wss\" method=\"post\" name=\"updatecatForm\">");
				writer.println(
					"<input type=\"hidden\" name=\"action\" value=\"updatecat2\" />");
				writer.println("<input type=\"hidden\" name=\"proj\" value=\""+ proj.getProjectId()+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"tc\" value=\""+ TopCatId+ "\" />");
				writer.println("<input type=\"hidden\" name=\"cc\" value=\""+ CurrentCatId+ "\" />");
				writer.println("<input type=\"hidden\" name=\"linkid\" value=\""+ linkid+ "\" />");
				writer.println("<input type=\"hidden\" name=\"updatecatid\" value=\""+ upCatId+ "\" />");
				writer.println("<input type=\"hidden\" name=\"oldibm\" value=\""+ ibmonly+ "\" />");
				writer.println("<input type=\"hidden\" name=\"catname\" value=\""+ name+ "\" />");
				writer.println("<input type=\"hidden\" name=\"ibmonly\" value=\""+ ibmonly+ "\" />");
				writer.println("<input type=\"hidden\" name=\"propOpt\" value=\""+ opt+ "\" />");

				writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"443\">");
				writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td></tr>");
				writer.println("<tr><td colspan=\"2\"><span style=\"color:#ff3333\"><b>Warning:</b></span></td></tr>");
				writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");
				writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"20\" height=\"1\" alt=\"\" /></td>");
				writer.println("<td>");

				StringBuffer message =new StringBuffer();
				if (getParameter(request,"msg").equals("S")){
					message.append("You are request to change the security classification for this folder.");
					message.append("There are folders/documents under this folder you do not own that will " +
						"be affected by this change.");
					message.append("By continuting with this action, you may be restricting users from accessing " +
						"folders/documents.");
					Vector v = ETSDocCommon.getCatSubTreeOwners(new Vector(),catid,proj.getProjectId(),es.gIR_USERN,userRole,Defines.UPDATE,true);
					if (v.size()>0){
						message.append("The following is a list of users that own folders/documents under your folder.");
						message.append("You may want to consult with them prior to proceed with this action.<br />");
						
						message.append("<br /><br /><b>Other owners:</b><br />");
						for (int vi = 0; vi < v.size(); vi++){
							message.append(ETSUtils.getUsersName(conn,(String)v.elementAt(vi))+"<br />");	
						}
					}
				}
				else{
					message.append("You are about to change this folder ");
					if (opt.equals("2")) {
						message.append("and its non-permanent IBM restricted subdocuments ");
					}
					else if (opt.equals("3")) {
						message.append("and its non-permanent IBM restricted subfolders and subdocuments ");
					}
					message.append("from restricted to IBM team members to accessible to all team members.");
	
				}
	
				writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
				writer.println("<tr><td>" + message.toString() + "</td></tr>");
				writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td></tr>");
				writer.println("<tr><td> Press continue if you would like to proceed or back to edit this action.</td></tr>");
				writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td></tr>");
				writer.println("</table>");

				writer.println("<input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "continue.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"submit\" /> &nbsp;&nbsp;&nbsp;");
				writer.println(
					"<a href=\"ETSProjectsServlet.wss?action=updatecatA&proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ CurrentCatId
						+ "&updatecatid="
						+ upCatId
						+ "&linkid="
						+ linkid
						+ "\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_lt.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Back\" />Back</a> &nbsp;&nbsp;&nbsp;");
				writer.println(
					"<a href=\"ETSProjectsServlet.wss?proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ linkid
						+ "\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" />Cancel</a>");

				writer.println("</td></tr></table>");
				writer.println("</form>");

			}
			else {
				writer.println("error occurred: invalid cat id for this user.");
				//System.out.print("put bad cat id message here");
			}
		}
		catch (Exception e) {
			writer.println("error occurred");
			//System.out.print("error here");
		}

	}

	private void doAddDoc(
		String action,
		int parentId,
		String meeting_id,
		String repeat_id,
		String self_id,
		String set_id,
		String msg) {
		try {
			if (userRole.equals(Defines.ETS_EXECUTIVE)	|| userRole.equals(Defines.WORKSPACE_VISITOR) || userRole.equals(Defines.WORKSPACE_CLIENT)) {
				printHeader("", "Add document", false);
				writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
				writer.println("You are not authorized to perform this action");
				return;
			}

			Vector breadcrumb = new Vector();
			boolean internal = false;
			if ((es.gDECAFTYPE.trim()).equalsIgnoreCase("I")) {
				internal = true;
			}

			boolean submit = false;
			String s_name = ETSDocCommon.getSessionString("ETSDocName",request);
			if (s_name != null)
				submit = true;
			
			char s_ibmonly = ETSDocCommon.getSessionChar("ETSDocIbmOnly",request);
			String s_desc = ETSDocCommon.getSessionString("ETSDocDesc",request);
			String s_keywords = ETSDocCommon.getSessionString("ETSDocKeywords",request);
			String s_date = ETSDocCommon.getSessionString("ETSDocExDate",request);
			String s_month = ETSDocCommon.getSessionString("ETSDocExMonth",request);
			String s_day = ETSDocCommon.getSessionString("ETSDocExDay",request);
			String s_year = ETSDocCommon.getSessionString("ETSDocExYear",request);
			String s_chResUsers = ETSDocCommon.getSessionString("ETSChUsers",request);
			Vector s_vResUsers = ETSDocCommon.getSessionVector("ETSResUsers",request);
			String s_notifyOpt = ETSDocCommon.getSessionString("ETSNotifyOptions",request);
			String s_notifyAll = ETSDocCommon.getSessionString("ETSNotifyAll",request);
			Vector s_vNotifyUsers = ETSDocCommon.getSessionVector("ETSNotifyUsers",request);

			

			ETSProj proj = Project;
			ETSCat parent_cat = null;
			parent_cat = ETSDatabaseManager.getCat(CurrentCatId);
			if (parent_cat == null) {
				//System.out.print("put bad parent cat id message here");
			}

			Vector users = new Vector();
			Vector ibmusers = new Vector();
			if (action.equals("adddoc")) {
				if (parent_cat != null) {
					/*if (parent_cat.IsCPrivate()) {
						users = ETSDatabaseManager.getRestrictedProjMembers(Project.getProjectId(),parent_cat.getId(),true,true);
					}
					else{*/
						users = ETSDatabaseManager.getProjMembers(Project.getProjectId(),true);
					//}
					
					ibmusers = getIBMMembers(users,conn);
					if (parent_cat.isIbmOnlyOrConf()) {
						users = ibmusers;
					}

					/*
					if(!isAuthorized(parent_cat.getUserId(),users,false)){
						printHeader("", "Add document", false);
						writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
						writer.println("<tr><td>");
						writer.println("You are not authorized to perform this action");
						return;
					}*/

					breadcrumb = getBreadcrumb(parent_cat);
					String header = getBreadcrumbTrail(breadcrumb);

					printHeader(header, "Add document", false);
					writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
					writer.println("<tr><td>");

				}
				else {
					//System.out.print("put bad parent cat id message here");
				}
			}
			else if (action.equals("addprojectplan")) {
				printTitle("Upload new project plan");

				writer.println("<table width=\"443\">");
				writer.println("<tr><td height=\"10\">");
				writer.println("<img src=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" height=\"1\" width=\"100%\" alt=\"\" />");
				writer.println("</td></tr>");
				writer.println("</table>");

			}
			else if (action.equals("addmeetingdoc")) {
				printTitle("Add document");

				writer.println("<table width=\"443\">");
				writer.println("<tr><td height=\"10\">");
				writer.println("<img src=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" height=\"1\" width=\"100%\" alt=\"\" />");
				writer.println("</td></tr>");
				writer.println("</table>");
			}
			else if (action.equals("addtaskdoc")) {
				writer.println("<table>");
				writer.println("<tr><td height=\"10\">");
				writer.println("<img src=\"//www.ibm.com/i/c.gif\" height=\"5\" width=\"443\" alt=\"\" />");
				writer.println("</td></tr>");
				writer.println("</table>");
			}
			else if (action.equals("addactionplan")) {
				printTitle("Add action plan document");

				writer.println("<table width=\"443\">");
				writer.println("<tr><td height=\"10\">");
				writer.println("<img src=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" height=\"1\" width=\"100%\" alt=\"\" />");
				writer.println("</td></tr>");
				writer.println("</table>");
			}


			ResourceBundle pdResources = ResourceBundle.getBundle("oem.edge.ets.fe.ets-itar");
			String strBTV = pdResources.getString("ets.doc.btv.server");
			if (proj.isITAR() && action.equals("addmeetingdoc"))  {
				String strPostAction = strBTV + "addDocFilesITAR.wss";
				writer.println("<form onsubmit=\"return Validate()\" action=\"" +strPostAction +"\" method=\"post\" enctype=\"multipart/form-data\" name=\"adddocForm\" >");
			} else {
				writer.println("<form onsubmit=\"return Validate()\" action=\"ETSContentUploadServlet.wss\" method=\"post\" enctype=\"multipart/form-data\" name=\"adddocForm\" >");
			}
			
			writer.println("<script type=\"text/javascript\" language=\"javascript\">");

			writer.println("function Validate() {");
			writer.println("Message = \"\";");
			writer.println("Message = Message + CheckName();");
			writer.println("if (Message == \"\") {");
			writer.println("return true;");
			writer.println("}");
			writer.println("else {");
			writer.println("alert(Message);");
			writer.println("return false;");
			writer.println("}");
			writer.println("}");
			
			writer.println("function trim(sString){");
			writer.println("while (sString.substring(0,1) == ' '){");
			writer.println("sString = sString.substring(1, sString.length);");
			writer.println("}");
				
			writer.println("while (sString.substring(sString.length-1, sString.length) == ' '){");
			writer.println("sString = sString.substring(0,sString.length-1);");
			writer.println("}");
			writer.println("return sString;");
			writer.println("}");

			writer.println("function CheckName() {");
			writer.println("Message = \"\";");    
			writer.println("id=document.getElementById(\"docname\").value;");
			writer.println("if((trim(id) == \"\") || (trim(id) == \" \")){");
			writer.println("Message = Message + \"Please enter a name for the document to be added.\";");
			writer.println("}");
			writer.println("return Message;");
			writer.println("}");
			 
			writer.println("</script>");
						
			if (action.equals("addmeetingdoc")) {
				writer.println("<input type=\"hidden\" name=\"action\" value=\"addmeetingdoc2\" />");
				writer.println("<input type=\"hidden\" name=\"meetingid\" value=\""+ meeting_id+ "\" />");
				writer.println("<input type=\"hidden\" name=\"repeatid\" value=\""+ repeat_id+ "\" />");
				if (proj.isITAR() && action.equals("addmeetingdoc")) {

				  String strProjId = proj.getProjectId();
				  String strEdgeId = es.gIR_USERN;
				  String strTopCatId = String.valueOf(TopCatId);
				  String strCurCatId = String.valueOf(CurrentCatId);
				  int iDocId = DocumentsHelper.createTmpITARMeetingDoc(strProjId, CurrentCatId, meeting_id, es.gIR_USERN);
				  
				  String strEncodedString = EncodeUtils.encode(String.valueOf(iDocId), strProjId, strEdgeId, strTopCatId, strCurCatId);
				  writer.println("<input type=\"hidden\" name=\"encodedToken\" value=\""+ strEncodedString+ "\" />");
				  writer.println("<input type=\"hidden\" name=\"formContext\" value=\"MEETINGS\" />");

				  String strReturnURL = "ETSProjectsServlet.wss?tc=" + TopCatId + "&proj=" + proj.getProjectId() + "&etsop=viewmeeting&meetid=" + meeting_id + "&linkid=" + linkid;
				  String strErrorURL = "ETSProjectsServlet.wss?tc=" + TopCatId + "&cc=" + strCurCatId + "&proj=" + proj.getProjectId() + "&etsop=addmeetingdoc&action=addmeetingdoc&meetingid=" + meeting_id + "&linkid=" + linkid;
                  writer.println("<input type=\"hidden\" name=\"docAction\" value=\"" + strReturnURL + "\" />");
                  writer.println("<input type=\"hidden\" name=\"errorURL\" value=\"" + strErrorURL + "\" />");
				
				}
			}
			else if (action.equals("addprojectplan")) {
				writer.println("<input type=\"hidden\" name=\"action\" value=\"addprojectplan2\" />");
			}
			else if (action.equals("addtaskdoc")) {
				writer.println("<input type=\"hidden\" name=\"action\" value=\"addtaskdoc2\" />");
				writer.println("<input type=\"hidden\" name=\"meetingid\" value=\""+ meeting_id+ "\" />");
				writer.println("<input type=\"hidden\" name=\"self\" value=\""+ self_id+ "\" />");
				writer.println("<input type=\"hidden\" name=\"set\" value=\""+ set_id+ "\" />");
			}
			else if (action.equals("addactionplan")) {
				writer.println("<input type=\"hidden\" name=\"action\" value=\"addactionplan2\" />");
				writer.println("<input type=\"hidden\" name=\"meetingid\" value=\""+ meeting_id+ "\" />");
			}
			else {
				writer.println("<input type=\"hidden\" name=\"action\" value=\"adddoc2\" />");
			}

			writer.println("<input type=\"hidden\" name=\"proj\" value=\""+ proj.getProjectId()+ "\" />");
			writer.println("<input type=\"hidden\" name=\"tc\" value=\""+ TopCatId+ "\" />");
			writer.println("<input type=\"hidden\" name=\"cc\" value=\""+ parent_cat.getId()+ "\" />");
			writer.println("<input type=\"hidden\" name=\"linkid\" value=\""+ linkid+ "\" />");

			if (!((msg.trim()).equals(""))) {
				if (msg.equals("0")) {
					msg = "Error occurred, please try again.";
				}
				if (msg.equals("1")) {
					msg = "No file name was submitted to be uploaded.";
				}
				else if (msg.equals("2")) {
					msg = "The document name must be 1-128 characters long.";
				}
				else if (msg.equals("3")) {
					msg = "The file is over the 100MB limit.  Please use the DropBox for this file.";
				}
				else if (msg.equals("4")) {
					msg = "Error occurred, please try again";
				}
				else if (msg.equals("5")) {
					msg ="Error occurred while uploading document. Please try again.";
				}
				else if (msg.equals("6")) {
					msg = "Invalid expiration date chosen. Please try again.";
				}
				else if (msg.equals("7")) {
					msg = "Expiration date chosen has past. Please try again.";
				}
				else if (msg.equals("9")) {
					msg = "The document description must be 0-2000 characters long.";
				}
				else if (msg.equals("10")) {
					msg = "The document keywords must be 0-500 characters long.";
				}
				else {
					msg = "Error occurred, document was added";
				}

				writer.println("<table><tr><td><span style=\"color:#ff3333\">"+ msg+ "</span></td></tr></table>");
			}
			else {
			    // Check for Error request param
			    String strError = request.getParameter("error");
			    if (strError != null && !strError.trim().equals("")) {
			        strError = DocMessages.getMessage(strError);
					writer.println("<table><tr><td><span style=\"color:#ff3333\">"+ strError+ "</span></td></tr></table>");
			    }
			}

			writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td></tr>");
			writer.println("<tr><td><span class=\"ast\"><b>*</b></span></td><td><span class=\"small\"> Denotes required fields</span></td>");
			writer.println("</tr></table>");

			writer.println("<table width=\"443\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"20\" height=\"1\" alt=\"\" /></td>");
			writer.println("<td>");

			writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");

			printDocPart1();
			/*
			//name
			writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
			writer.println("<td align=\"left\" width=\"99%\"><label for=\"docname\"><b>Name:</b></label></td></tr>");
			writer.println("<tr><td colspan=\"2\"><input type=\"text\" id=\"docname\" size=\"25\" style=\"width:300px\" width=\"300px\" name=\"docname\" value=\"\" /></td></tr>");
			
			
			//description
			writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"5\" alt=\"\" /></td></tr>");
			//writer.println("<tr><td align=\"left\" width=\"1%\">&nbsp;</td>");
			writer.println("<tr><td align=\"left\" colspan=\"2\"><label for=\"docdesc\"><b>Description</b> (max. 1024 chars.)</label></td></tr>");
			writer.println("<tr><td colspan=\"2\"><textarea id=\"docdesc\" name=\"docdesc\" cols=\"25\" rows=\"3\" style=\"width:300px\" width=\"300px\" wrap=\"soft\" value=\"\" /></textarea></td></tr>");
			
			//keywords
			writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"5\" alt=\"\" /></td></tr>");
			//writer.println("<tr><td align=\"left\" width=\"1%\">&nbsp;</td>");
			writer.println("<tr><td align=\"left\" colspan=\"2\"><label for=\"keywords\"><b>Keywords:</b></label></td></tr>");
			writer.println("<tr><td colspan=\"2\"><input type=\"text\" id=\"keywords\" size=\"25\" style=\"width:300px\" width=\"300px\" name=\"keywords\" value=\"\" /></td></tr>");
			*/

			if (proj.isITAR() && action.equals("addmeetingdoc")) {
				//file
				writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"5\" alt=\"\" /></td></tr>");
				writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
				writer.println("<td align=\"left\" width=\"99%\"><label for=\"uploadedFile[0]\"><b>File:</b></label></td></tr>");
				writer.println("<tr><td colspan=\"2\"><input type=\"file\" id=\"uploadedFile[0]\" size=\"25\" style=\"width:300px\" width=\"300px\" name=\"uploadedFile[0]\" value=\"\" /></td></tr>");
			}else {
				//file
				writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"5\" alt=\"\" /></td></tr>");
				writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
				writer.println("<td align=\"left\" width=\"99%\"><label for=\"docfile\"><b>File:</b></label></td></tr>");
				writer.println("<tr><td colspan=\"2\"><input type=\"file\" id=\"docfile\" size=\"25\" style=\"width:300px\" width=\"300px\" name=\"docfile\" value=\"\" /></td></tr>");
			}

			if (action.equals("adddoc")) {
				if (s_date == null || s_date.equals("")){
					s_date = "";
				}
				else {
					s_date = " checked=\"checked\" ";
				}
				//expiration date
				writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
				writer.println("<tr height=\"21\">");
				writer.println("<td align=\"left\" valign=\"top\" colspan=\"2\">");
				writer.println("<input type=\"checkbox\" name=\"exdate\" value=\"yes\" "+s_date+" id=\"date\" />");
				writer.println("<label for=\"date\"><b>Set expiration date</b></label>");
				writer.println("</td></tr>");

				writer.println("<tr>");
				writer.println("<td align=\"left\" valign=\"top\" colspan=\"2\">\n");

				String sm = "";
				if (s_month != null){
					sm = s_month;
				}
				int im = -1;
				if (!sm.equals("")) {
					im = Integer.parseInt(sm);
				}
				writer.println("<select name=\"exmonth\" id=\"date\" class=\"iform\">");
				writer.println("<option value=\"-1\">&nbsp;</option>");
				for (int m = 0; m < 12; m++) {
					if (im == m) {
						writer.println("<option value=\""+ m+ "\" selected=\"selected\">"+ Defines.months[m]+ "</option>");
					}
					else {
						writer.println("<option value=\""+ m+ "\">"+ Defines.months[m]+ "</option>");
					}
				}
				writer.println("</select>\n");

				String sd = "";
				if (s_day != null){
					sd = s_day;
				}
				int idy = -1;
				if (!sd.equals("")) {
					idy = Integer.parseInt(sd);
				}
				writer.println("&nbsp;&nbsp;");
				writer.println(
					"<select name=\"exday\" id=\"date\" class=\"iform\">");
				writer.println("<option value=\"-1\">&nbsp;</option>");
				for (int d = 1; d <= 31; d++) {
					if (idy == d) {
						writer.println("<option value=\""+ d+ "\" selected=\"selected\">"+ d+ "</option>");
					}
					else {
						writer.println("<option value=\"" + d + "\">" + d + "</option>");
					}
				}
				writer.println("</select>\n");

				String sy = "";
				if (s_year != null){
					sy = s_year;
				}
				int iy = -1;
				if (!sy.equals("")) {
					iy = Integer.parseInt(sy);
				}
				Calendar cal = Calendar.getInstance();
				int year = (cal.get(Calendar.YEAR)) - 1;

				writer.println("&nbsp;&nbsp;");
				writer.println(
					"<select name=\"exyear\" id=\"date\" class=\"iform\">");
				writer.println("<option value=\"-1\">&nbsp;</option>");
				for (int c = year; c <= year + 4; c++) {
					if (iy == c) {
						writer.println("<option value=\""+ c+ "\" selected=\"selected\">"+ c+ "</option>");
					}
					else {
						writer.println("<option value=\"" + c + "\">" + c + "</option>");
					}
				}
				writer.println("</select>\n");
				writer.println("</td>");

				writer.println("</tr>\n");
			}

			//ibm only
			if (action.equals("adddoc")) {
				//blue header
				writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"15\" alt=\"\" /></td></tr>");
				writer.println("<tr><td colspan=\"2\" class=\"tdblue\" height=\"18\"> Document access</td></tr>");
				
				char ibmonly = parent_cat.getIbmOnly();;
				if (s_ibmonly != ' ' && submit){
					ibmonly = s_ibmonly;
				}
				if (internal) {
					writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");
					writer.println("<tr><td align=\"left\" colspan=\"2\"><label for=\"ibmonly\"><span style=\"color:#ff3333\"><b>Security classification</b></span></label>"
						+ "<a href=\"ETSContentManagerServlet.wss?OP=Documents&BlurbField=Sec\"  "
						+ "onclick=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Security','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400');return false\"  "
						+ "onkeypress=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Security','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400')\" >"
						+ "<img border=\"0\" name=\"Help\" src=\""
						+ Defines.ICON_ROOT+"/popup.gif\" width=\"16\" height=\"16\"  alt=\"Click for help on this field\" /></a>&nbsp;"
						+ "</td></tr>");
					//writer.println("<tr><td align=\"left\" colspan=\"2\"><label for=\"ibmonly\"><b>Security classification</b></label></td></tr>");
				}
				else {
					writer.println("<input type=\"hidden\" name=\"ibmonly\" value=\"0\" />");
				}

				writer.println(getDocJS(users,ibmusers,false));
				
				if (parent_cat.getIbmOnly() == Defines.ETS_IBM_CONF) {
					writer.println("<tr><td align=\"left\" width=\"1%\">&nbsp;</td>");
					writer.println("<td align=\"left\" width=\"99%\"><b>Access to this document will be limited to IBM team members and can never be changed.</b><br />");
					writer.println("<span class=\"small\">To make it public, create it under a different folder.</span><input type=\"hidden\" name=\"ibmonly\" value=\"2\" /></td></tr>");
				}
				else if (parent_cat.getIbmOnly() == Defines.ETS_IBM_ONLY) {
					writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
					writer.println("<td align=\"left\" width=\"99%\"><b>Access limited to:</b></td></tr>");
					writer.println("<tr><td align=\"left\" valign=\"bottom\" colspan=\"2\">");
					writer.println("<select id=\"ibmonly\" name=\"ibmonly\" onchange=\"security_ch(document.adddocForm.res_users,document.adddocForm.notify,this)\" />");
					if(ibmonly == Defines.ETS_IBM_ONLY)
						writer.println("<option value=\"1\" selected=\"selected\">All IBM team members</option>");
					else
						writer.println("<option value=\"1\">All IBM team members</option>");
					if (ibmonly == Defines.ETS_IBM_CONF)
						writer.println("<option value=\"2\" selected=\"selected\">All IBM team members permanently</option>");
					else
						writer.println("<option value=\"2\">All IBM team members permanently</option>");
					writer.println("</select></td></tr>");
					writer.println("<tr><td class=\"small\" colspan=\"2\">To make accessible to all team members, create it under a different folder or make its parent folder public.</td></tr>");

				}
				else {
					if (internal) {
						writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
						writer.println("<td align=\"left\" width=\"99%\"><b>Access limited to:</b></td></tr>");
						writer.println("<tr><td align=\"left\" valign=\"bottom\" colspan=\"2\">");
						writer.println("<select id=\"ibmonly\" name=\"ibmonly\"  onchange=\"security_ch(document.adddocForm.res_users,document.adddocForm.notify,this)\" />");
						if(ibmonly == Defines.ETS_PUBLIC)
							writer.println("<option value=\"0\" selected=\"selected\">All team members</option>");
						else
							writer.println("<option value=\"0\">All team members</option>");
						if(ibmonly == Defines.ETS_IBM_ONLY)
							writer.println("<option value=\"1\" selected=\"selected\">All IBM team members</option>");
						else
							writer.println("<option value=\"1\">All IBM team members</option>");
						if (ibmonly == Defines.ETS_IBM_CONF)
							writer.println("<option value=\"2\" selected=\"selected\">All IBM team members permanently</option>");
						else
							writer.println("<option value=\"2\">All IBM team members permanently</option>");
					writer.println("</select></td></tr>");
					}
				}
			
			
				//users
				writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");

				writer.println("<tr height=\"21\">");
				writer.println("<td align=\"left\" valign=\"top\" colspan=\"2\">");
				
				/*if(parent_cat.IsCPrivate()){
					writer.println("<input type=\"hidden\" name=\"chusers\" value=\"yes\" />");
					writer.println("<b>This document is restricted. To give access, please select from the following users: </label>");
				}
				else{*/
					String chresusers = "";
					if (s_chResUsers == null || s_chResUsers.equals("")){
						chresusers = "";
					}
					else{
						chresusers = " checked=\"checked\" ";
					}
					writer.println("<input type=\"checkbox\" name=\"chusers\" value=\"yes\" "+chresusers+" id=\"users\" />");
				
					if(internal){
						writer.println("<label for=\"chusers\"><b>Further restrict access to the following users,<br/ > " +
							"based on security classification above</b></label>");
					}
					else{
						writer.println("<label for=\"chusers\"><b>Restrict to users</b></label>");
					}
				//}
				writer.println("<a href=\"ETSContentManagerServlet.wss?OP=Documents&BlurbField=Restrict%20Users\"  "
						+ "onclick=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Restrict%20Users','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400');return false\"  "
						+ "onkeypress=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Restrict%20Users','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400')\" >"
						+ "<img border=\"0\" name=\"Help\" src=\""
						+ Defines.ICON_ROOT+"/popup.gif\" width=\"16\" height=\"16\"  alt=\"Click for help on this field\" /></a>&nbsp;");
				writer.println("</td></tr>");
				
				
				writer.println("<tr><td align=\"left\" colspan=\"2\" class=\"small\">[For selecting multiple values use Ctrl + Mouse click together.]</td></tr>");
				writer.println("<tr><td colspan=\"2\"><table width=\"443\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
				writer.println("<select id=\"users\" name=\"res_users\" multiple=\"multiple\" size=\"10\" style=\"width:320px\" width=\"320px\">");
				String selectedUser = "";
				if (ibmonly == Defines.ETS_IBM_ONLY || ibmonly == Defines.ETS_IBM_CONF){
					for (int u = 0; u < ibmusers.size(); u++) {
						ETSUser user = (ETSUser) ibmusers.elementAt(u);
						if (s_vResUsers.contains(user.getUserId())){
							selectedUser = " selected=\"selected\" ";	
						}
						String username = ETSUtils.getUsersName(conn, user.getUserId());
						writer.println("<option value=\""+ user.getUserId()+ "\" "+ selectedUser+">"+ username+ " ["+ user.getUserId()+ "]</option>");
						selectedUser = "";
					}
				}
				else{
					for (int u = 0; u < users.size(); u++) {
						ETSUser user = (ETSUser) users.elementAt(u);
						if (s_vResUsers.contains(user.getUserId())){
							selectedUser = " selected=\"selected\" ";	
						}
						String username = ETSUtils.getUsersName(conn, user.getUserId());
						writer.println("<option value=\""+ user.getUserId()+"\" "+ selectedUser+">"+ username+ " ["+ user.getUserId()+ "]</option>");
						selectedUser = "";
					}
				}
				writer.println("</select></td></tr>");
				
				writer.println("<tr><td align=\"left\" colspan=\"2\" class=\"small\">[Author will automatically be added to the restricted list]</td></tr>");
				
				if (internal && !parent_cat.isIbmOnlyOrConf()) {
				 	writer.println("<noscript><tr><td colspan=\"2\" class=\"small\">[If access is restricted to IBM members, only IBM employees selected will be given access.]</td></tr></noscript>");
				}
	
				writer.println("</table>");
				writer.println("</td></tr>");
			  
			
			//notify option
				writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"15\" alt=\"\" /></td></tr>");
				writer.println("<tr><td colspan=\"2\" class=\"tdblue\" height=\"18\"> Notification</td></tr>");
				
				String notopt = "to";
				if (s_notifyOpt != null){
					System.out.println("*********snotopt="+s_notifyOpt);
					notopt = s_notifyOpt;
				}
				writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");
				writer.println("<tr><td colspan=\"2\">");
				writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td><b>E-mail notification option:</b> ");
				writer.println("<a href=\"ETSContentManagerServlet.wss?OP=Documents&BlurbField=Notification%20option\"  "
						+ "onclick=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Notification%20option','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400');return false\"  "
						+ "onkeypress=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Notification%20option','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400')\" >"
						+ "<img border=\"0\" name=\"Help\" src=\""
						+ Defines.ICON_ROOT+"/popup.gif\" width=\"16\" height=\"16\"  alt=\"Click for help on this field\" /></a>&nbsp;"
						+"</td>");
				if (notopt.equals("to"))
					writer.println("<td><input type=\"radio\" id=\"notifyOption\" name=\"notifyOption\" value=\"to\" checked=\"checked\" /><label for=\"notifyOption\">To</label></td>");
				else
					writer.println("<td><input type=\"radio\" id=\"notifyOption\" name=\"notifyOption\" value=\"to\" /><label for=\"notifyOption\">To</label></td>");
				
				if (notopt.equals("bcc"))
					writer.println("<td><input type=\"radio\" id=\"notifyOption\" name=\"notifyOption\" value=\"bcc\" checked=\"checked\" /><label for=\"notifyOption\">Bcc</label></td>");
				else
					writer.println("<td><input type=\"radio\" id=\"notifyOption\" name=\"notifyOption\" value=\"bcc\" /><label for=\"notifyOption\">Bcc</label></td>");
				writer.println("</tr></table></td></tr>");
				
			
				
				writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");
				writer.println("<tr><td align=\"left\" colspan=\"2\"><label for=\"notify\"><b>Notify following team members:</b></label></td></tr>");
				if (s_notifyAll != null && !s_notifyAll.equals(""))
					writer.println("<tr><td align=\"left\" colspan=\"2\"><input type=\"checkbox\" name=\"notifyall\" value=\"yes\" id=\"notifyall\" checked=\"checked\" /><label for=\"notifyall\">Notify all</label></td></tr>");
				else
					writer.println("<tr><td align=\"left\" colspan=\"2\"><input type=\"checkbox\" name=\"notifyall\" value=\"yes\" id=\"notifyall\" /><label for=\"notifyall\">Notify all</label></td></tr>");
				/*
				writer.println("<tr><td align=\"left\" colspan=\"2\"><table width=\"320px\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tr><td><label for=\"notify\"><b>Notify following team members:</b></label></td>");
				writer.println("<td align=\"right\"><input type=\"checkbox\" name=\"notifyall\" value=\"yes\" id=\"notifyall\" /><label for=\"notifyall\">Notify all</label></td></tr></table></td></tr>");
				*/
				writer.println("<tr><td align=\"left\" colspan=\"2\" class=\"small\">[For selecting multiple values use Ctrl + Mouse click together.]</td></tr>");
				writer.println("<tr><td colspan=\"2\"><table width=\"443\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
				writer.println("<select id=\"notify\" name=\"notify\" multiple=\"multiple\" size=\"10\" style=\"width:320px\" width=\"320px\">");
				
				if(ibmonly == Defines.ETS_IBM_ONLY || ibmonly == Defines.ETS_IBM_CONF){
					for (int u = 0; u < ibmusers.size(); u++) {
						ETSUser user = (ETSUser) ibmusers.elementAt(u);
						if (s_vNotifyUsers.contains(user.getUserId())){
							selectedUser = " selected=\"selected\" ";	
						}
						String username = ETSUtils.getUsersName(conn, user.getUserId());
						writer.println("<option value=\""+ user.getUserId()+"\" "+selectedUser+">"+ username+ " ["+ user.getUserId()+ "]</option>");
						selectedUser = "";
					}
				}
				else{
					for (int u = 0; u < users.size(); u++) {
						ETSUser user = (ETSUser) users.elementAt(u);
						if (s_vNotifyUsers.contains(user.getUserId())){
							selectedUser = " selected=\"selected\" ";	
						}
						String username = ETSUtils.getUsersName(conn, user.getUserId());
						writer.println("<option value=\""+ user.getUserId()+ "\" "+selectedUser+">"+ username+ " ["+ user.getUserId()+ "]</option>");
						selectedUser = "";
					}
				}
				writer.println("</select>");

				if (internal && !parent_cat.isIbmOnlyOrConf()) {
					writer.println("<tr><td colspan=\"2\" class=\"small\">[Security classification and additional access restrictions will be applied to the notification list]</td></tr>");
				}
				else if (!internal && !parent_cat.isIbmOnlyOrConf()) {
					writer.println("<tr><td colspan=\"2\" class=\"small\">[User access restrictions will be applied to the notification list]</td></tr>");
				}

				writer.println("</table>");
				writer.println("</td></tr>");
			}

			writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"20\" alt=\"\" /></td></tr>");
			writer.println("</table>");

			if (action.equals("addtaskdoc")) {
				String cvStr = "";
				if (!self_id.equals("")){
					cvStr = "&self="+ self_id+ "&etsop=action";
				}
				else if (!set_id.equals("")){
					cvStr = "&set="+ set_id+ "&etsop=action";
				}
				writer.println(
					"<input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"add document\" /> &nbsp; &nbsp;");
				writer.println(
					"<a class=\"fbox\" href=\"ETSProjectsServlet.wss?action=details&taskid="+ meeting_id
						+ "&proj="+Project.getProjectId()
						+ cvStr
						+ "&tc="+ TopCatId+ "&cc="+ CurrentCatId+ "&linkid="+ linkid
						+ "\"><img src=\"" + Defines.BUTTON_ROOT + "cancel.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"Cancel\" /></a>");
				writer.println("</td></tr></table>");
			}
			else {
				writer.println("<input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"add document\" /> &nbsp; &nbsp;");
				writer.println("<a class=\"fbox\" href=\"ETSProjectsServlet.wss?proj="+ Project.getProjectId()
						+ "&tc="+ TopCatId+ "&cc="+ CurrentCatId+ "&linkid="+ linkid
						+ "\"><img src=\"" + Defines.BUTTON_ROOT + "cancel.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"Cancel\" /></a>");
				writer.println("</td></tr></table>");
			}

			writer.println("<table  border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");
			writer.println("<tr><td><span class=\"small\">Note: If posting material that may be used as training information for other customers, send to your IBM contact before posting.</span></td></tr></table>");
			writer.println("</form>");

		}
		catch (Exception e) {
			writer.println("error occurred");
			e.printStackTrace();
			//System.out.println("error here=" + e);
		}
		//System.out.println("spn " + new java.util.Date(System.currentTimeMillis()));

		ETSDocCommon.removeSessionVar("ETSDocName",request);
		ETSDocCommon.removeSessionVar("ETSDocDesc",request);
		ETSDocCommon.removeSessionVar("ETSDocKeywords",request);
		ETSDocCommon.removeSessionVar("ETSDocIbmOnly",request);;
		ETSDocCommon.removeSessionVar("ETSDocExDate",request);
		ETSDocCommon.removeSessionVar("ETSDocExMonth",request);
		ETSDocCommon.removeSessionVar("ETSDocExDay",request);
		ETSDocCommon.removeSessionVar("ETSDocExYear",request);
		ETSDocCommon.removeSessionVar("ETSChUsers",request);
		ETSDocCommon.removeSessionVar("ETSResUsers",request);
		ETSDocCommon.removeSessionVar("ETSNotifyOptions",request);
		ETSDocCommon.removeSessionVar("ETSNotifyAll",request);
		ETSDocCommon.removeSessionVar("ETSNotifyUsers",request);
	}

	private void doDelDoc(
		int docid,
		String action,
		String latest_uid,
		String msg) {
		//  private void delDoc(int docid, String projectid, int currentid, int topcatid, String action, String latest_uid, EdgeAccessCntrl es,HttpServletResponse response){
		try {
			if (userRole.equals(Defines.ETS_EXECUTIVE) || userRole.equals(Defines.WORKSPACE_VISITOR) || userRole.equals(Defines.WORKSPACE_CLIENT)) {
				printHeader("", "Delete document", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
				writer.println("You are not authorized to perform this action");
				return;
			}

			Vector breadcrumb = new Vector();

			ETSProj proj = Project;

			ETSCat current_cat = this_current_cat;
			//databaseManager.getCat(CurrentCatId);
			ETSDoc doc =ETSDatabaseManager.getDocByIdAndProject(docid,Project.getProjectId());

			if (current_cat != null) {
				breadcrumb = getBreadcrumb(current_cat);
				//printBreadcrumb(breadcrumb,writer);
				//printTitle("Delete document");
				String header = getBreadcrumbTrail(breadcrumb);
				printHeader(header, "Delete document", false);
				
				writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
				
				if (doc == null) {
					writer.println("Invalid document id");
					return;
				}
				if (doc.isIbmOnlyOrConf()&& (!(es.gDECAFTYPE.trim()).equalsIgnoreCase("I"))) {
					writer.println("You are not authorized to edit this document");
					return;
				}
				
				if (!(userRole.equals(Defines.WORKSPACE_OWNER) || userRole.equals(Defines.WORKSPACE_MANAGER)|| isSuperAdmin || doc.getUserId().equals(es.gIR_USERN))){
					writer.println("You are not authorized to edit this document");
					return;
				}
				else if (doc.IsDPrivate() && userRole.equals(Defines.WORKSPACE_MANAGER)){
					Vector resusers = ETSDatabaseManager.getRestrictedProjMemberIds(proj.getProjectId(),doc.getId(),false);
					if (!resusers.contains(es.gIR_USERN)){
						writer.println("You are not authorized to edit this document");
						return;						
					}	
				} 

				
				writer.println("<form action=\"ETSContentManagerServlet.wss\" method=\"get\" name=\"deldocForm\">");
				if (!((msg.trim()).equals(""))) {
					if (msg.equals("1")) {
						msg = "An error occured with the current top category.";
					}
					else if (msg.equals("2")) {
						msg = "An error occured with the current document id.";
					}
					else if (msg.equals("3")) {
						msg = "An error occurred while deleting the document.";
					}
					else if (msg.equals("4")) {
						msg = "error occurred: document was not found.";
					}
					else if (msg.equals("5")) {
						msg = "error occurred: invalid folder for this user.";
					}
					else if (msg.equals("6")) {
						msg = "error occurred: exception caught while deleting";
					}
					else if (msg.equals("7")) {
						msg = "Invalid project for user.";
					}
					else if (msg.equals("8")) {
						msg = "You are not authorized to perform this action.";
					}
					writer.println(
						"<table><tr><td><span style=\"color:#ff3333\">"
							+ msg
							+ "</span></td></tr></table>");
				}

				if (action.equals("deldoc")) {
					writer.println(
						"<input type=\"hidden\" name=\"action\" value=\"deldoc2\" />");
				}
				else {
					writer.println(
						"<input type=\"hidden\" name=\"action\" value=\"delprevdoc2\" />");
					writer.println(
						"<input type=\"hidden\" name=\"luid\" value=\""
							+ latest_uid
							+ "\" />");
				}

				writer.println(
					"<input type=\"hidden\" name=\"proj\" value=\""
						+ proj.getProjectId()
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"cc\" value=\""
						+ current_cat.getId()
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"tc\" value=\""
						+ TopCatId
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"docid\" value=\""
						+ docid
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"linkid\" value=\""
						+ linkid
						+ "\" />");

				writer.println(
					"<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
				writer.println(
					"<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"20\" height=\"1\" alt=\"\" /></td>");
				writer.println("<td>");

				if (doc != null) {
					writer.println(
						"<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
					writer.println(
						"<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td></tr>");
					writer.println(
						"<tr><td>You are about to delete the document <b>"
							+ doc.getName()
							+ "</b>. </td></tr>");

					if (doc.isLatestVersion() && doc.hasPreviousVersion()) {
						writer.println("<tr><td>");
						writer.println(
							"<br /> &nbsp; &nbsp; <input id=\"alldel\" type=\"checkbox\" name=\"alldel\" value=\"yes\" />");
						writer.println(
							"<label for=\"alldel\"> Delete all versions of this document</label>");
						writer.println("</td></tr>");
					}

					writer.println(
						"<tr><td><br /><span style=\"color:#ff3333\">This action can not be undone.</span></td></tr>");
					writer.println("</table>");
					writer.println("<br /><br />");

					writer.println(
						"<table border=\"0\" cellspacing=\"2\" cellpadding=\"0\">");
					writer.println(
						"<tr><td><input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"delete document\" />&nbsp;&nbsp;&nbsp;</td>");
					if (action.equals("delprevdoc")) {
						writer.println(
							"<td><a href=\"ETSProjectsServlet.wss?action=prevdetails&proj="
								+ Project.getProjectId()
								+ "&tc="
								+ TopCatId
								+ "&cc="
								+ CurrentCatId
								+ "&currdocid="
								+ latest_uid
								+ "&docid="
								+ docid
								+ "&linkid="
								+ linkid
								+ "\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" />Cancel</a></td>");
					}
					else {
						writer.println(
							"<td><a href=\"ETSProjectsServlet.wss?action=details&proj="
								+ Project.getProjectId()
								+ "&tc="
								+ TopCatId
								+ "&cc="
								+ CurrentCatId
								+ "&docid="
								+ docid
								+ "&linkid="
								+ linkid
								+ "\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" />Cancel</a></td>");
					}
					writer.println("</td></tr></table>");
				}
				else {
					writer.println(
						"<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
					writer.println(
						"<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td>");
					writer.println(
						"<tr><td>The document you are trying to delete does not exist for the current project or an error has occurred.");
					writer.println("</td></tr></table>");
					writer.println("<br /><br />");

					writer.println(
						"<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr>");
					writer.println(
						"<td><a href=\"ETSProjectsServlet.wss?action=details&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&docid="
							+ docid
							+ "&linkid="
							+ linkid
							+ "\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" />Cancel</a></td>");
					writer.println("</td></tr></table>");

				}

				writer.println("</td></tr></table>");
				writer.println("</form>");
			}
			else {
				writer.println("error occurred: invalid cat id for this user.");
				//System.out.print("put bad current doc id message here");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			//System.out.println("error here");
		}
	}

	private void doUpdateDocProp(int docid, String msg) {
		try {
			if (userRole.equals(Defines.ETS_EXECUTIVE)|| userRole.equals(Defines.WORKSPACE_VISITOR)|| userRole.equals(Defines.WORKSPACE_CLIENT)) {
				printHeader("", "Update document properties", false);
				writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
				writer.println("You are not authorized to perform this action");
				return;
			}

			Vector breadcrumb = new Vector();

			ETSProj proj = Project;
			ETSCat parent_cat = ETSDatabaseManager.getCat(CurrentCatId);

			if (parent_cat != null) {
				breadcrumb = getBreadcrumb(parent_cat);
				//printBreadcrumb(breadcrumb,writer);
				//printTitle("Update document properties");
				String header = getBreadcrumbTrail(breadcrumb);
				printHeader(header, "Update document properties", false);
				writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");

				ETSDoc doc = ETSDatabaseManager.getDocByIdAndProject(docid,Project.getProjectId());
				if (doc == null) {
					writer.println("Invalid document id");
					return;
				}
				if (doc.isIbmOnlyOrConf() && (!(es.gDECAFTYPE.trim()).equalsIgnoreCase("I"))) {
					writer.println("You are not authorized to view this document");
					return;
				}

				//if (!((userRole == Defines.WORKSPACE_OWNER|| doc.getUserId().equals(es.gIR_USERN)|| !isSuperAdmin)|| (!doc.hasExpired() && userRole == Defines.WORKSPACE_MANAGER))) {
				//if (!((userRole == Defines.WORKSPACE_OWNER|| doc.getUserId().equals(es.gIR_USERN)|| isSuperAdmin)|| (!doc.hasExpired() && userRole == Defines.WORKSPACE_MANAGER))) {
				if (doc.hasExpired() && !((userRole == Defines.WORKSPACE_OWNER)|| doc.getUserId().equals(es.gIR_USERN) || isSuperAdmin)){
					writer.println("You are not authorized to edit this document");
					return;
				}

				Vector users = new Vector();
				Vector docuserids = new Vector();
					
				/*if (parent_cat.IsCPrivate()){
					users = ETSDatabaseManager.getRestrictedProjMembers(Project.getProjectId(),parent_cat.getId(),true,true);
				}
				else{*/
					users = ETSDatabaseManager.getProjMembers(Project.getProjectId(),true);
				//}

				Vector ibmusers = getIBMMembers(users,conn);
				if (parent_cat.isIbmOnlyOrConf()) {
					users = ibmusers;
				}
				
				if (doc.IsDPrivate()){
					boolean authorized = false;
					Vector docusers = ETSDatabaseManager.getRestrictedProjMembers(Project.getProjectId(),doc.getId(),true,false);
					for (int u =0;u<docusers.size();u++){
						docuserids.addElement(((ETSUser)docusers.elementAt(u)).getUserId());	
					}
					if (docuserids.contains(es.gIR_USERN) || isSuperAdmin || userRole == Defines.WORKSPACE_OWNER || userRole == Defines.WORKSPACE_MANAGER || doc.getUserId().equals(es.gIR_USERN)){
						authorized = true;	
					}
					
					if(!authorized){
						writer.println("You are not authorized to edit this document");
						return;
					}
				}
				

				if (!doc.isLatestVersion()) {
					writer.println("You are not authorized to update the properties of a document's previous version. <br /><br />");
					writer.println("<a href=\"ETSProjectsServlet.wss?proj="+ Project.getProjectId()
							+ "&tc="+ TopCatId+ "&cc="+ CurrentCatId+ "&linkid="+ linkid
							+ "\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" /> Ok</a>");
					return;
				}

				String decaftype = es.gDECAFTYPE.trim();

				writer.println("<form action=\"ETSContentManagerServlet.wss\" method=\"post\" name=\"updatedocpropForm\">");
				if (!((msg.trim()).equals(""))) {
					if (msg.equals("1")) {
						msg = "An error occured with the current document id.";
					}
					else if (msg.equals("2")) {
						msg = "Document Name must be 1-128 characters long.";
					}
					else if (msg.equals("3")) {
						msg = "An error occurred: invalid projet id for user.";
					}
					else if (msg.equals("4")) {
						msg ="An error occurred while trying to update the properties for the document.";
					}
					else if (msg.equals("5")) {
						msg ="An exception occured while updating the document properties.";
					}
					else if (msg.equals("6")) {
						msg = "Error occurred:  invalid category id.";
					}
					else if (msg.equals("7")) {
						msg ="Error occurred:  Invalid expiration date chosen.";
					}
					else if (msg.equals("8")) {
						msg ="Error occurred:  Expiration date chosen has already past.";
					}
					else if (msg.equals("9")) {
						msg ="Document description must be 0-2000 characters long.";
					}
					else if (msg.equals("10")) {
						msg ="Document keywords must be 0-500 characters long.";
					}

					writer.println("<table><tr><td><span style=\"color:#ff3333\">"+ msg+ "</span></td></tr></table>");
				}

				writer.println("<input type=\"hidden\" name=\"action\" value=\"updatedocprop2\" />");
				writer.println("<input type=\"hidden\" name=\"proj\" value=\""+ proj.getProjectId()+ "\" />");
				writer.println("<input type=\"hidden\" name=\"tc\" value=\""+ TopCatId+ "\" />");
				writer.println("<input type=\"hidden\" name=\"cc\" value=\""+ parent_cat.getId()+ "\" />");
				writer.println("<input type=\"hidden\" name=\"docid\" value=\""+ docid+ "\" />");
				writer.println("<input type=\"hidden\" name=\"linkid\" value=\""+ linkid+ "\" />");
				writer.println("<input type=\"hidden\" name=\"oldibm\" value=\""+ doc.getIbmOnly()+ "\" />");

				writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
				writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td>");
				writer.println("<tr><td><span class=\"ast\"><b>*</b></span></td><td><span class=\"small\"> Denotes required fields</span></td>");
				writer.println("</tr></table>");

				writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
				writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"20\" height=\"1\" alt=\"\" /></td>");
				writer.println("<td>");


				String s_name = ETSDocCommon.getSessionString("ETSDocName",request);
				String s_desc = ETSDocCommon.getSessionString("ETSDocDesc",request);
				String s_keywords = ETSDocCommon.getSessionString("ETSDocKeywords",request);
				char s_ibmonly = ETSDocCommon.getSessionChar("ETSDocIbmOnly",request);
				String s_date = ETSDocCommon.getSessionString("ETSDocExDate",request);
				String s_month = ETSDocCommon.getSessionString("ETSDocExMonth",request);
				String s_day = ETSDocCommon.getSessionString("ETSDocExDay",request);
				String s_year = ETSDocCommon.getSessionString("ETSDocExYear",request);
				String s_chResUsers = ETSDocCommon.getSessionString("ETSChUsers",request);
				Vector s_vResUsers = ETSDocCommon.getSessionVector("ETSResUsers",request);
				
				boolean submit = false;
				if (s_name != null){
					submit = true;	
				}
				if (s_ibmonly == ' '){
					s_ibmonly = doc.getIbmOnly();	
				}
				if (s_name == null){
					s_name = doc.getName().trim();
					s_desc = doc.getDescription().trim();
					s_keywords = doc.getKeywords();
					s_ibmonly = doc.getIbmOnly();
					s_vResUsers = docuserids;
				}
				
				
				if (doc != null) {
					writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
					writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");

					//blue header
					writer.println("<tr><td colspan=\"2\" class=\"tdblue\" height=\"18\"> Document details</td></tr>");
					writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"5\" alt=\"\" /></td></tr>");
		
					//name
					writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
					writer.println("<td align=\"left\" width=\"99%\"><label for=\"docname\"><b>Name:</b></label></td></tr>");
					writer.println("<tr><td colspan=\"2\"><input type=\"text\" id=\"docname\" size=\"30\" style=\"width:300px\" width=\"300px\" name=\"docname\" value=\""
						+ s_name+ "\" /></td></tr>");
						//+ (doc.getName()).trim()+ "\" /></td></tr>");

					//description
					writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");
					//writer.println("<tr><td align=\"left\" width=\"1%\">&nbsp;</td>");
					writer.println("<tr><td align=\"left\" colspan=\"2\"><label for=\"docdesc\"><b>Description</b> (max. 2000 chars.)</label></td></tr>");
					writer.println("<tr><td colspan=\"2\"><textarea id=\"docdesc\" name=\"docdesc\" cols=\"30\" rows=\"3\" wrap=\"soft\" style=\"width:300px\" width=\"300px\" value=\"\">");
					//writer.println((doc.getDescription()).trim());
					writer.println(s_desc);
					writer.println("</textarea></td></tr>");

					//keywords
					writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");
					//writer.println("<tr><td align=\"left\" width=\"1%\">&nbsp;</td>");
					writer.println("<tr><td align=\"left\" colspan=\"2\"><label for=\"keywords\"><b>Keywords:</b></label></td></tr>");
					writer.println("<tr><td colspan=\"2\"><input type=\"text\" id=\"keywords\" size=\"30\" style=\"width:300px\" width=\"300px\" name=\"keywords\" value=\""
							+ s_keywords+ "\" /></td></tr>");
							//+ (doc.getKeywords()).trim()+ "\" /></td></tr>");

					//expiration date
					writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
					writer.println("<tr height=\"21\">");
					writer.println("<td align=\"left\" valign=\"top\" colspan=\"2\">");
					if (submit){
						if (s_date != null && !s_date.equals("")) {
							writer.println("<input type=\"checkbox\" name=\"exdate\" value=\"yes\" id=\"date\" checked=\"checked\">");
						}
						else {
							writer.println("<input type=\"checkbox\" name=\"exdate\" value=\"yes\" id=\"date\" />");
						}
					}
					else{
						if (doc.getExpiryDate() != 0) {
							writer.println("<input type=\"checkbox\" name=\"exdate\" value=\"yes\" id=\"date\" checked=\"checked\">");
						}
						else {
							writer.println("<input type=\"checkbox\" name=\"exdate\" value=\"yes\" id=\"date\" />");
						}
					}
					writer.println("<label for=\"date\"><b>Set expiration date</b></label>");
					writer.println("</td></tr>");

					writer.println("<tr>");
					writer.println("<td align=\"left\" valign=\"top\" colspan=\"2\">\n");

					String sm = "";
					if (submit){
						if(s_month != null){
							sm = s_month;	
						}	
					}
					else if (doc.getExpiryDate() != 0) {
						sm =(new Integer(getExpireDatePart(doc.getExpiryDate(),Calendar.MONTH))).toString();
					}

					int im = -1;
					if (!sm.equals("")) {
						im = Integer.parseInt(sm);
					}
					writer.println("<select name=\"exmonth\" id=\"date\" class=\"iform\">");
					writer.println("<option value=\"-1\">&nbsp;</option>");
					for (int m = 0; m < 12; m++) {
						if (im == m) {
							writer.println("<option value=\""+ m+ "\" selected=\"selected\">"+ Defines.months[m]+ "</option>");
						}
						else {
							writer.println("<option value=\""+ m+ "\">"+ Defines.months[m]+ "</option>");
						}
					}
					writer.println("</select>\n");

					String sd = "";
					if (submit){
						if(s_day != null){
							sd = s_day;	
						}	
					}
					else if (doc.getExpiryDate() != 0) {
						sd =(new Integer(getExpireDatePart(doc.getExpiryDate(),Calendar.DAY_OF_MONTH))).toString();
					}
					int idy = -1;
					if (!sd.equals("")) {
						idy = Integer.parseInt(sd);
					}
					writer.println("&nbsp;&nbsp;");
					writer.println("<select name=\"exday\" id=\"date\" class=\"iform\">");
					writer.println("<option value=\"-1\">&nbsp;</option>");
					for (int d = 1; d <= 31; d++) {
						if (idy == d) {
							writer.println("<option value=\""+ d+ "\" selected=\"selected\">"+ d+ "</option>");
						}
						else {
							writer.println("<option value=\""+ d+ "\">"+ d+ "</option>");
						}
					}
					writer.println("</select>\n");

					String sy = "";
					if (submit){
						if(s_year != null){
							sy = s_year;	
						}	
					}
					else if (doc.getExpiryDate()!= 0) {
						sy =(new Integer(getExpireDatePart(doc.getExpiryDate(),Calendar.YEAR))).toString();
					}
					int iy = -1;
					if (!sy.equals("")) {
						iy = Integer.parseInt(sy);
					}
					Calendar cal = Calendar.getInstance();
					int year = (cal.get(Calendar.YEAR)) - 1;

					writer.println("&nbsp;&nbsp;");
					writer.println("<select name=\"exyear\" id=\"date\" class=\"iform\">");
					writer.println("<option value=\"-1\">&nbsp;</option>");
					for (int c = year; c <= year + 4; c++) {
						if (iy == c) {
							writer.println("<option value=\""+ c+ "\" selected=\"selected\">"+ c+ "</option>");
						}
						else {
							writer.println("<option value=\""+ c+ "\">"+ c+ "</option>");
						}
					}
					writer.println("</select>\n");
					writer.println("</td>");

					writer.println("</tr>\n");


					writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"15\" alt=\"\" /></td></tr>");
					writer.println("<tr><td colspan=\"2\" class=\"tdblue\" height=\"18\"> Document access</td></tr>");

					//ibm only
					if (decaftype.equals("I")) {
						writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");
						writer.println("<tr><td align=\"left\" colspan=\"2\"><label for=\"ibmonly\"><span style=\"color:#ff3333\"><b>Security classification</b></span></label>"
						+ "<a href=\"ETSContentManagerServlet.wss?OP=Documents&BlurbField=Sec\"  "
							+ "onclick=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Security','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400');return false\"  "
							+ "onkeypress=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Security','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400')\" >"
							+ "<img border=\"0\" name=\"Help\" src=\""
							+ Defines.ICON_ROOT+"/popup.gif\" width=\"16\" height=\"16\"  alt=\"Click for help on this field\" /></a>&nbsp;"
							+"</td></tr>");
					}
					else {
						writer.println("<input type=\"hidden\" name=\"ibmonly\" value=\"0\" />");
					}

					if (parent_cat.getIbmOnly() == Defines.ETS_IBM_CONF|| s_ibmonly == '2') {
						writer.println("<tr><td align=\"left\" colspan=\"2\"><b>Access to this document is limited to IBM team members and can never be changed.</b><br />");
						writer.println("<input type=\"hidden\" name=\"ibmonly\" value=\"2\" /></td></tr>");
					}
					else if (parent_cat.getIbmOnly() == Defines.ETS_IBM_ONLY) {
						writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
						writer.println("<td align=\"left\" width=\"99%\"><b>Access limited to:</b></td></tr>");
						writer.println("<tr><td align=\"left\" valign=\"bottom\" colspan=\"2\">");
						writer.println("<select id=\"ibmonly\" name=\"ibmonly\" onchange=\"security_ch(document.updatedocpropForm.res_users,'',this)\" />");

						if (s_ibmonly == Defines.ETS_IBM_ONLY)
							writer.println("<option value=\"1\" selected=\"selected=\">All IBM team members</option>");
						else
							writer.println("<option value=\"1\">All IBM team members</option>");

						if (s_ibmonly == Defines.ETS_IBM_CONF)
							writer.println("<option value=\"2\" selected=\"selected=\">All IBM team members permanently</option>");
						else
							writer.println("<option value=\"2\">All IBM team members permanently</option>");

						writer.println("</select></td></tr>");
						writer.println("<tr><td class=\"small\" colspan=\"2\">To make accessible to all team members, create it under a different folder or make its parent folder public.</td></tr>");

					}
					else {
						if (decaftype.equals("I")) {
							writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
							writer.println("<td align=\"left\" width=\"99%\"><b>Access limited to:</b></td></tr>");
							writer.println("<tr><td align=\"left\" valign=\"bottom\" colspan=\"2\">");
							writer.println("<select id=\"ibmonly\" name=\"ibmonly\" onchange=\"security_ch(document.updatedocpropForm.res_users,'',this)\" />");
							if (s_ibmonly == Defines.ETS_PUBLIC)
								writer.println("<option value=\"0\" selected=\"selected=\">All team members</option>");
							else
								writer.println("<option value=\"0\">All team members</option>");

							if (s_ibmonly == Defines.ETS_IBM_ONLY)
								writer.println("<option value=\"1\" selected=\"selected=\">All IBM team members</option>");
							else
								writer.println("<option value=\"1\">All IBM team members</option>");

							if (s_ibmonly == Defines.ETS_IBM_CONF)
								writer.println("<option value=\"2\" selected=\"selected=\">All IBM team members permanently</option>");
							else
								writer.println("<option value=\"2\">All IBM team members permanently</option>");
							writer.println("</select></td></tr>");
						}
					}

					//users
					writer.println(getDocJS(users,ibmusers,true));

					writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");

					writer.println("<tr height=\"21\">");
					writer.println("<td align=\"left\" valign=\"top\" colspan=\"2\">");
					//writer.println("<input type=\"checkbox\" name=\"chusers\" value=\"yes\" id=\"users\" />");
					//writer.println("<label for=\"chusers\"><b>Restrict to users</b></label>");
					/*if(parent_cat.IsCPrivate()){
						writer.println("<input type=\"hidden\" name=\"chusers\" value=\"yes\" />");
						writer.println("<b>This document is restricted. To give access, please select from the following users: </label>");
					}
					else{*/
					if (submit){
						if(s_chResUsers != null && s_chResUsers.equals("1"))
							writer.println("<input type=\"checkbox\" name=\"chusers\" value=\"yes\" id=\"users\" checked=\"checked\" />");
						else
							writer.println("<input type=\"checkbox\" name=\"chusers\" value=\"yes\" id=\"users\" />");
	
					}
					else{
						if(doc.IsDPrivate())
							writer.println("<input type=\"checkbox\" name=\"chusers\" value=\"yes\" id=\"users\" checked=\"checked\" />");
						else
							writer.println("<input type=\"checkbox\" name=\"chusers\" value=\"yes\" id=\"users\" />");
					}
					if(decaftype.equals("I")){
						writer.println("<label for=\"chusers\"><b>Further restrict access to the following users,<br/ > "
							+"based on security classification above</b></label>");
					}
					else{
						writer.println("<label for=\"chusers\"><b>Restrict to users</b></label>");
					}
					//}
					writer.println("<a href=\"ETSContentManagerServlet.wss?OP=Documents&BlurbField=Restrict%20Users\"  "
						+ "onclick=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Restrict%20Users','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400');return false\"  "
						+ "onkeypress=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Restrict%20Users','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400')\" >"
						+ "<img border=\"0\" name=\"Help\" src=\""
						+ Defines.ICON_ROOT+"/popup.gif\" width=\"16\" height=\"16\"  alt=\"Click for help on this field\" /></a>&nbsp;");
	
					writer.println("</td></tr>");
				
					writer.println("<tr><td align=\"left\" colspan=\"2\" class=\"small\">[For selecting multiple values use Ctrl + Mouse click together.]</td></tr>");
					writer.println("<tr><td colspan=\"2\"><table width=\"443\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
					writer.println("<select id=\"users\" name=\"res_users\" multiple=\"multiple\" size=\"10\" style=\"width:320px\" width=\"320px\">");
					if (s_ibmonly == Defines.ETS_IBM_CONF || s_ibmonly == Defines.ETS_IBM_ONLY){
						for (int u = 0; u < ibmusers.size(); u++) {
							ETSUser user = (ETSUser) ibmusers.elementAt(u);
							String username = ETSUtils.getUsersName(conn, user.getUserId());
							if(s_vResUsers.contains(user.getUserId()))
								writer.println("<option value=\""+ user.getUserId()+ "\" selected=\"selected\">"+ username+ " ["+ user.getUserId()+ "]</option>");
							else
								writer.println("<option value=\""+ user.getUserId()+ "\">"+ username+ " ["+ user.getUserId()+ "]</option>");
						}
					}
					else{
						for (int u = 0; u < users.size(); u++) {
							ETSUser user = (ETSUser) users.elementAt(u);
							String username = ETSUtils.getUsersName(conn, user.getUserId());
							if(s_vResUsers.contains(user.getUserId()))
								writer.println("<option value=\""+ user.getUserId()+ "\" selected=\"selected\">"+ username+ " ["+ user.getUserId()+ "]</option>");
							else
								writer.println("<option value=\""+ user.getUserId()+ "\">"+ username+ " ["+ user.getUserId()+ "]</option>");
						}
					}
					writer.println("</select>");
				    writer.println("<tr><td align=\"left\" colspan=\"2\" class=\"small\">[Author will automatically be added to the restricted list]</td></tr>");
					

					if ((decaftype.equals("I")) && !parent_cat.isIbmOnlyOrConf()) {
						writer.println("<noscript><tr><td colspan=\"2\" class=\"small\">[If access is restricted to IBM members, only IBM employees selected will be given access.]</td></tr></noscript>");
					}
	
					writer.println("</table>");
					writer.println("</td></tr>");
					

					writer.println("</table>");

					writer.println("<br /><br />");
					writer.println("<input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"update document\" /> &nbsp; &nbsp; ");
				}
				else {
					writer.println("error occurred.  document not found.<br />");
				}

				writer.println("<a href=\"ETSProjectsServlet.wss?action=details&proj="+ Project.getProjectId()
						+ "&tc="+ TopCatId+ "&cc="+ CurrentCatId+ "&docid="+ docid+ "&linkid="+ linkid
						+ "\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" /> Cancel</a>");

				writer.println("</td></tr></table>");
				writer.println("</form>");
			}
			else {
				writer.println("error occurred: invalid cat id for this user.");
				//System.out.print("put bad parent cat id message here");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			//System.out.println("error here");
		}
		
		ETSDocCommon.removeSessionVar("ETSDocName",request);
		ETSDocCommon.removeSessionVar("ETSDocDesc",request);
		ETSDocCommon.removeSessionVar("ETSDocKeywords",request);
		ETSDocCommon.removeSessionVar("ETSDocIbmOnly",request);
		ETSDocCommon.removeSessionVar("ETSDocExDate",request);
		ETSDocCommon.removeSessionVar("ETSDocExMonth",request);
		ETSDocCommon.removeSessionVar("ETSDocExDay",request);
		ETSDocCommon.removeSessionVar("ETSDocExYear",request);
		ETSDocCommon.removeSessionVar("ETSChUsers",request);
		ETSDocCommon.removeSessionVar("ETSResUsers",request);
	}

	private void doUpdateDocPropC(
		int docid,
		String name,
		String desc,
		String keywords,
		String ibmonly,
		String exdate,
		String exmonth,
		String exday,
		String exyear,String chusers,Vector resusers) {
		try {
			if (userRole.equals(Defines.ETS_EXECUTIVE)
				|| userRole.equals(Defines.WORKSPACE_VISITOR)) {
				printHeader("", "Update document properties", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
				writer.println("You are not authorized to perform this action");
				return;
			}

			Vector breadcrumb = new Vector();

			ETSProj proj = Project;

			ETSDoc thisDoc =
				ETSDatabaseManager.getDocByIdAndProject(
					docid,
					proj.getProjectId());
			ETSCat CurrCat = ETSDatabaseManager.getCat(CurrentCatId);

			if (thisDoc != null) {
				breadcrumb = getBreadcrumb(CurrCat);
				//printBreadcrumb(breadcrumb, writer);
				//printTitle("Update document properties: "+thisDoc.getName());
				String header = getBreadcrumbTrail(breadcrumb);
				printHeader(header, "Update document properties", false);
				writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");

				writer.println("<form action=\"ETSContentManagerServlet.wss\" method=\"post\" name=\"updatecatForm\">");
				writer.println("<input type=\"hidden\" name=\"action\" value=\"updatedocprop2\" />");
				writer.println("<input type=\"hidden\" name=\"proj\" value=\""+ proj.getProjectId()+ "\" />");
				writer.println("<input type=\"hidden\" name=\"tc\" value=\""+ TopCatId+ "\" />");
				writer.println("<input type=\"hidden\" name=\"cc\" value=\""+ CurrentCatId+ "\" />");
				writer.println("<input type=\"hidden\" name=\"linkid\" value=\""+ linkid+ "\" />");
				writer.println("<input type=\"hidden\" name=\"docid\" value=\""+ docid+ "\" />");
				writer.println("<input type=\"hidden\" name=\"oldibm\" value=\""+ ibmonly+ "\" />");
				writer.println("<input type=\"hidden\" name=\"docname\" value=\""+ name+ "\" />");
				writer.println("<input type=\"hidden\" name=\"docdesc\" value=\""+ desc+ "\" />");
				writer.println("<input type=\"hidden\" name=\"keywords\" value=\""+ keywords+ "\" />");
				writer.println("<input type=\"hidden\" name=\"ibmonly\" value=\""+ ibmonly+ "\" />");
				writer.println("<input type=\"hidden\" name=\"exdate\" value=\""+ exdate+ "\" />");
				writer.println("<input type=\"hidden\" name=\"exmonth\" value=\""+ exmonth+ "\" />");
				writer.println("<input type=\"hidden\" name=\"exday\" value=\""+ exday+ "\" />");
				writer.println("<input type=\"hidden\" name=\"exyear\" value=\""+ exyear+ "\" />");
				writer.println("<input type=\"hidden\" name=\"chusers\" value=\""+ chusers+ "\" />");
				for (int i =0; i < resusers.size();i++){
					writer.println("<input type=\"hidden\" name=\"res_users\" value=\""+ resusers.elementAt(i)+ "\" />");
				}

				writer.println(
					"<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"443\">");
				writer.println(
					"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td></tr>");
				writer.println(
					"<tr><td colspan=\"2\"><span style=\"color:#ff3333\"><b>Warning:</b></span></td></tr>");
				writer.println(
					"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");

				writer.println(
					"<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"20\" height=\"1\" alt=\"\" /></td>");
				writer.println("<td>");

				StringBuffer message =
					new StringBuffer("You are about to change this document ");
				message.append(
					"from restricted to IBM team members to accessible to all team members.");

				writer.println(
					"<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"443\">");
				writer.println("<tr><td>" + message.toString() + "</td></tr>");
				writer.println(
					"<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td></tr>");
				writer.println(
					"<tr><td> Press continue if you would like to proceed or back to edit this action.</td></tr>");
				writer.println(
					"<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td></tr>");
				writer.println("</table>");

				writer.println(
					"<input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "continue.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"submit\" /> &nbsp;&nbsp;&nbsp;");
				writer.println(
					"<a href=\"ETSProjectsServlet.wss?action=updatedocprop&proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ CurrentCatId
						+ "&docid="
						+ docid
						+ "&linkid="
						+ linkid
						+ "\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_lt.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Back\" />Back</a> &nbsp;&nbsp;&nbsp;");
				writer.println(
					"<a href=\"ETSProjectsServlet.wss?proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ linkid
						+ "\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" />Cancel</a>");

				writer.println("</td></tr></table>");
				writer.println("</form>");

			}
			else {
				writer.println("error occurred: invalid cat id for this user.");
				//System.out.print("put bad cat id message here");
			}
		}
		catch (Exception e) {
			writer.println("error occurred");
			e.printStackTrace();
			//System.out.print("error here");
		}

	}

	private void doUpdateDoc(int docid, boolean replace, String msg) {
		try {
			if (userRole.equals(Defines.ETS_EXECUTIVE) || userRole.equals(Defines.WORKSPACE_VISITOR)) {
				if (replace)
					printHeader("", "Replace draft document", false);
				else
					printHeader("", "Update document", false);
				writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
				writer.println("You are not authorized to perform this action");
				return;
			}

			Vector breadcrumb = new Vector();
			ETSProj proj = Project;
			ETSCat parent_cat = ETSDatabaseManager.getCat(CurrentCatId);
			Vector users = new Vector();
			Vector docuserids = new Vector();

			if (parent_cat != null) {
	
				/*if (parent_cat.IsCPrivate()){
					users = ETSDatabaseManager.getRestrictedProjMembers(Project.getProjectId(),parent_cat.getId(),true,true);
				}
				else{*/
					users = ETSDatabaseManager.getProjMembers(Project.getProjectId(),true);
				//}

				Vector ibmusers = getIBMMembers(users,conn);
				if (parent_cat.isIbmOnlyOrConf()) {
					users = ibmusers;
				}
				
				String s_name = ETSDocCommon.getSessionString("ETSDocName",request);
				char s_ibmonly = ETSDocCommon.getSessionChar("ETSDocIbmOnly",request);
				String s_date = ETSDocCommon.getSessionString("ETSDocExDate",request);
				String s_month = ETSDocCommon.getSessionString("ETSDocExMonth",request);
				String s_day = ETSDocCommon.getSessionString("ETSDocExDay",request);
				String s_year = ETSDocCommon.getSessionString("ETSDocExYear",request);
				String s_chResUsers = ETSDocCommon.getSessionString("ETSChUsers",request);
				Vector s_vResUsers = ETSDocCommon.getSessionVector("ETSResUsers",request);
				String s_notOpt = ETSDocCommon.getSessionString("ETSNotifyOptions",request);
				String s_notall = ETSDocCommon.getSessionString("ETSNotifyAll",request);
				Vector s_vNotUsers = ETSDocCommon.getSessionVector("ETSNotifyUsers",request);
	
				boolean submit = false;

				
				ETSDoc doc = ETSDatabaseManager.getDocByIdAndProject(docid,proj.getProjectId());

				
				breadcrumb = getBreadcrumb(parent_cat);
				String header = getBreadcrumbTrail(breadcrumb);

				if (replace)
					printHeader(header, "Replace draft document", false);
				else
					printHeader(header, "Update document", false);
				writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");

				writer.println("<form action=\"ETSContentUploadServlet.wss\" method=\"post\" enctype=\"multipart/form-data\" name=\"updatedocForm\" >");
				if (replace)
					writer.println("<input type=\"hidden\" name=\"action\" value=\"replacedoc2\" />");
				else
					writer.println("<input type=\"hidden\" name=\"action\" value=\"updatedoc2\" />");
				
				writer.println("<input type=\"hidden\" name=\"proj\" value=\""+ proj.getProjectId()+ "\" />");
				writer.println("<input type=\"hidden\" name=\"cc\" value=\""+ parent_cat.getId()+ "\" />");
				writer.println("<input type=\"hidden\" name=\"tc\" value=\""+ TopCatId+ "\" />");
				writer.println("<input type=\"hidden\" name=\"docid\" value=\""+ docid+ "\" />");
				writer.println("<input type=\"hidden\" name=\"linkid\" value=\""+ linkid+ "\" />");

				if (doc != null) {
					writer.println("<input type=\"hidden\" name=\"oldibm\" value=\""+ doc.getIbmOnly()+ "\" />");
					
					if (s_name != null){
						submit = true;	
					}
					if (s_ibmonly == ' '){
						s_ibmonly = doc.getIbmOnly();	
					}
					if (s_name == null){
						s_ibmonly = doc.getIbmOnly();
						s_vResUsers = docuserids;
					}
					
					if (doc.IsDPrivate()){
						boolean authorized = false;
						Vector docusers = ETSDatabaseManager.getRestrictedProjMembers(Project.getProjectId(),doc.getId(),true,false);
						for (int u =0;u<docusers.size();u++){
							docuserids.addElement(((ETSUser)docusers.elementAt(u)).getUserId());	
						}
						if (docuserids.contains(es.gIR_USERN) || isSuperAdmin || userRole == Defines.WORKSPACE_OWNER || doc.getUserId().equals(es.gIR_USERN)){
							authorized = true;	
						}
	
						if(!authorized){
							writer.println("You are not authorized to edit this document");
							return;
						}
					}
					
					//if (!((userRole == Defines.WORKSPACE_OWNER|| doc.getUserId().equals(es.gIR_USERN)|| isSuperAdmin) || (!doc.hasExpired() && userRole == Defines.WORKSPACE_MANAGER))) {
					if (doc.hasExpired() && !((userRole == Defines.WORKSPACE_OWNER)|| doc.getUserId().equals(es.gIR_USERN) || isSuperAdmin)){
						writer.println("You are not authorized to edit this document");
						return;
					}

					if (doc.isIbmOnlyOrConf()&& (!(es.gDECAFTYPE.trim()).equalsIgnoreCase("I"))) {
						writer.println("You are not authorized to edit this document");
						return;
					}
	
					
					if (!((msg.trim()).equals(""))) {
						if (msg.equals("0")) {
							msg = "Error occurred, please try again.";
						}
						if (msg.equals("1")) {
							msg = "No file name was submitted to be uploaded.";
						}
						else if (msg.equals("2")) {
							msg ="The document name must be 1-128 characters long.";
						}
						else if (msg.equals("3")) {
							msg ="The file is over the 100MB limit.  Please use the DropBox for this file.";
						}
						else if (msg.equals("4")) {
							msg = "Error occurred, please try again";
						}
						else if (msg.equals("5")) {
							msg ="Error occurred while uploading document. Please try again.";
						}
						else if (msg.equals("6")) {
							msg ="Invalid expiration date chosen. Please try again.";
						}
						else if (msg.equals("7")) {
							msg ="Expiration date chosen has already past. Please try again.";
						}
						else if (msg.equals("9")) {
							msg ="The document description must be 0-2000 characters long.";
						}
						else if (msg.equals("10")) {
							msg ="The document keywords must be 0-500 characters long.";
						}
						else {
							msg = "Error occurred, document was added";
						}

						writer.println("<table><tr><td><span style=\"color:#ff3333\">"+ msg+ "</span></td></tr></table>");
					}

					writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
					writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td>");
					writer.println("<tr><td><span class=\"ast\"><b>*</b></span></td><td><span class=\"small\"> Denotes required fields</span></td>");
					writer.println("</tr></table>");

					writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
					writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"20\" height=\"1\" alt=\"\" /></td>");
					writer.println("<td>");

					writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
					writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");

					writer.println(getDocJS(users,ibmusers,false));
					
					printDocPart1(doc);
					
					//file
					writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"5\" alt=\"\" /></td></tr>");
					writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
					writer.println("<td align=\"left\" width=\"99%\"><label for=\"docfile\"><b>File:</b></label></td></tr>");
					writer.println("<tr><td colspan=\"2\"><input type=\"file\" id=\"docfile\" size=\"25\" style=\"width:300px\" width=\"300px\" name=\"docfile\" value=\"\" /></td>");

					//expiration date
					writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
					writer.println("<tr height=\"21\">");
					writer.println("<td align=\"left\" valign=\"top\" colspan=\"2\">");
					if (submit){
						if (s_date != null && !s_date.equals("")) {
							writer.println("<input type=\"checkbox\" name=\"exdate\" value=\"yes\" id=\"date\" checked=\"checked\">");
						}
						else {
							writer.println("<input type=\"checkbox\" name=\"exdate\" value=\"yes\" id=\"date\" />");
						}
					}
					else{
						if (doc.getExpiryDate() != 0) {
							writer.println("<input type=\"checkbox\" name=\"exdate\" value=\"yes\" id=\"date\" checked=\"checked\" />");
						}
						else {
							writer.println("<input type=\"checkbox\" name=\"exdate\" value=\"yes\" id=\"date\" />");
						}
					}
					writer.println("<label for=\"date\"><b>Set expiration date</b></label>");
					writer.println("</td></tr>");

					writer.println("<tr>");
					writer.println("<td align=\"left\" valign=\"top\" colspan=\"2\">\n");

					String sm = "";
					if (submit){
						if(s_month != null){
							sm = s_month;	
						}	
					}
					else if (doc.getExpiryDate() != 0) {
						sm =(new Integer(getExpireDatePart(doc.getExpiryDate(),Calendar.MONTH))).toString();
					}
					int im = -1;
					if (!sm.equals("")) {
						im = Integer.parseInt(sm);
					}
					writer.println("<select name=\"exmonth\" id=\"date\" class=\"iform\">");
					writer.println("<option value=\"-1\">&nbsp;</option>");
					for (int m = 0; m < 12; m++) {
						if (im == m) {
							writer.println("<option value=\""+ m+ "\" selected=\"selected\">"+ Defines.months[m]+ "</option>");
						}
						else {
							writer.println("<option value=\""+ m+ "\">"+ Defines.months[m]+ "</option>");
						}
					}
					writer.println("</select>\n");

					String sd = "";
					if (submit){
						if(s_day != null){
							sd = s_day;	
						}	
					}
					else if (doc.getExpiryDate() != 0) {
						sd =(new Integer(getExpireDatePart(doc.getExpiryDate(),Calendar.DAY_OF_MONTH))).toString();
					}
					int idy = -1;
					if (!sd.equals("")) {
						idy = Integer.parseInt(sd);
					}
					writer.println("&nbsp;&nbsp;");
					writer.println("<select name=\"exday\" id=\"date\" class=\"iform\">");
					writer.println("<option value=\"-1\">&nbsp;</option>");
					for (int d = 1; d <= 31; d++) {
						if (idy == d) {
							writer.println("<option value=\""+ d+ "\" selected=\"selected\">"+ d+ "</option>");
						}
						else {
							writer.println("<option value=\""+ d+ "\">"+ d	+ "</option>");
						}
					}
					writer.println("</select>\n");

					String sy = "";
					if (submit){
						if(s_year != null){
							sy = s_year;	
						}	
					}
					else if (doc.getExpiryDate() != 0) {
						sy =(new Integer(getExpireDatePart(doc.getExpiryDate(),Calendar.YEAR))).toString();
					}
					int iy = -1;
					if (!sy.equals("")) {
						iy = Integer.parseInt(sy);
					}
					Calendar cal = Calendar.getInstance();
					int year = (cal.get(Calendar.YEAR)) - 1;

					writer.println("&nbsp;&nbsp;");
					writer.println("<select name=\"exyear\" id=\"date\" class=\"iform\">");
					writer.println("<option value=\"-1\">&nbsp;</option>");
					for (int c = year; c <= year + 4; c++) {
						if (iy == c) {
							writer.println("<option value=\""+ c+ "\" selected=\"selected\">"+ c+ "</option>");
						}
						else {
							writer.println("<option value=\""+ c+ "\">"+ c+ "</option>");
						}
					}
					writer.println("</select>\n");
					writer.println("</td>");

					writer.println("</tr>\n");

					//ibm only
					boolean internal = false;
					if ((es.gDECAFTYPE.trim()).equalsIgnoreCase("I")) {
						internal = true;
					}
				
					writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"15\" alt=\"\" /></td></tr>");
					writer.println("<tr><td colspan=\"2\" class=\"tdblue\" height=\"18\"> Document access</td></tr>");
				
				
					if (internal) {
						writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");
						writer.println("<tr><td align=\"left\" colspan=\"2\"><label for=\"ibmonly\"><span style=\"color:#ff3333\"><b>Security classification</b></span></label>"
							+ "<a href=\"ETSContentManagerServlet.wss?OP=Documents&BlurbField=Sec\"  "
							+ "onclick=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Security','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400');return false\"  "
							+ "onkeypress=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Security','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400')\" >"
							+ "<img border=\"0\" name=\"Help\" src=\""
							+ Defines.ICON_ROOT+"/popup.gif\" width=\"16\" height=\"16\"  alt=\"Click for help on this field\" /></a>&nbsp;"
							+"</td></tr>");
					}
					else {
						writer.println("<input type=\"hidden\" name=\"ibmonly\" value=\"0\" />");
					}

					if (parent_cat.getIbmOnly() == Defines.ETS_IBM_CONF || s_ibmonly == '2') {
						writer.println("<tr><td align=\"left\" colspan=\"2\"><b>Access to this document is limited to IBM team members and can never be changed.</b><br />");
						writer.println("<input type=\"hidden\" name=\"ibmonly\" value=\"2\" /></td></tr>");
					}
					else if (parent_cat.getIbmOnly() == Defines.ETS_IBM_ONLY) {
						writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
						writer.println("<td align=\"left\" width=\"99%\"><b>Access limited to:</b></td></tr>");
						writer.println("<tr><td align=\"left\" valign=\"bottom\" colspan=\"2\">");
						writer.println("<select id=\"ibmonly\" name=\"ibmonly\" onchange=\"security_ch(document.updatedocForm.res_users,document.updatedocForm.notify,this)\" />");

						if (s_ibmonly == Defines.ETS_IBM_ONLY)
							writer.println("<option value=\"1\" selected=\"selected=\">All IBM team members</option>");
						else
							writer.println("<option value=\"1\">All IBM team members</option>");

						if (s_ibmonly == Defines.ETS_IBM_CONF)
							writer.println("<option value=\"2\" selected=\"selected=\">All IBM team members permanently</option>");
						else
							writer.println("<option value=\"2\">All IBM team members permanently</option>");

						writer.println("</select></td></tr>");
						writer.println("<tr><td class=\"small\" colspan=\"2\">To make accessible to all team members, its parent folder must be accessible to all team members.</td></tr>");

					}
					else {
						if (internal) {
							writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
							writer.println("<td align=\"left\" width=\"99%\"><b>Access limited to:</b></td></tr>");
							writer.println("<tr><td align=\"left\" valign=\"bottom\" colspan=\"2\">");
							writer.println("<select id=\"ibmonly\" name=\"ibmonly\" onchange=\"security_ch(document.updatedocForm.res_users,document.updatedocForm.notify,this)\" />");
							if (s_ibmonly == Defines.ETS_PUBLIC)
								writer.println("<option value=\"0\" selected=\"selected=\">All team members</option>");
							else
								writer.println("<option value=\"0\">All team members</option>");

							if (s_ibmonly == Defines.ETS_IBM_ONLY)
								writer.println("<option value=\"1\" selected=\"selected=\">All IBM team members</option>");
							else
								writer.println("<option value=\"1\">All IBM team members</option>");

							if (s_ibmonly == Defines.ETS_IBM_CONF)
								writer.println("<option value=\"2\" selected=\"selected=\">All IBM team members permanently</option>");
							else
								writer.println("<option value=\"2\">All IBM team members permanently</option>");
							writer.println("</select></td></tr>");
						}
					}

					if (internal && parent_cat.getIbmOnly() == Defines.ETS_PUBLIC && doc.getIbmOnly() == Defines.ETS_IBM_ONLY) {
						writer.println("<td align=\"left\" colspan=\"2\" class=\"small\"><span style=\"color:#ff3333\"><b>Warning:</b></span>");
						writer.println(" If you change the security classification to accessible to all team members, all team members will be able to view this document and its previous version(s).<td></tr>");
					}


					//users
					writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");

					writer.println("<tr height=\"21\">");
					writer.println("<td align=\"left\" valign=\"top\" colspan=\"2\">");
					/*if(parent_cat.IsCPrivate()){
						writer.println("<input type=\"hidden\" name=\"chusers\" value=\"yes\" />");
						writer.println("<b>This document is restricted. To give access, please select from the following users: </label>");
					}
					else{*/
						if (submit){
							if(s_chResUsers != null && !s_chResUsers.equals(""))
								writer.println("<input type=\"checkbox\" name=\"chusers\" value=\"yes\" id=\"users\" checked=\"checked\" />");
							else
								writer.println("<input type=\"checkbox\" name=\"chusers\" value=\"yes\" id=\"users\" />");

						}
						else{
							if(doc.IsDPrivate())
								writer.println("<input type=\"checkbox\" name=\"chusers\" value=\"yes\" id=\"users\" checked=\"checked\" />");
							else
								writer.println("<input type=\"checkbox\" name=\"chusers\" value=\"yes\" id=\"users\" />");
						}
						
						if (internal){
							writer.println("<label for=\"chusers\"><b>Further restrict access to the following users,<br/ > "
								+"based on security classification above</b></label>");	
						}
						else{
							writer.println("<label for=\"chusers\"><b>Restrict to users</b></label>");
						}
					//}
					writer.println("<a href=\"ETSContentManagerServlet.wss?OP=Documents&BlurbField=Restrict%20Users\"  "
						+ "onclick=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Restrict%20Users','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400');return false\"  "
						+ "onkeypress=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Restrict%20Users','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400')\" >"
						+ "<img border=\"0\" name=\"Help\" src=\""
						+ Defines.ICON_ROOT+"/popup.gif\" width=\"16\" height=\"16\"  alt=\"Click for help on this field\" /></a>&nbsp;");
	
					writer.println("</td></tr>");
				
					writer.println("<tr><td align=\"left\" colspan=\"2\" class=\"small\">[For selecting multiple values use Ctrl + Mouse click together.]</td></tr>");
					writer.println("<tr><td colspan=\"2\"><table width=\"443\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
					writer.println("<select id=\"users\" name=\"res_users\" multiple=\"multiple\" size=\"10\" style=\"width:320px\" width=\"320px\">");
					if(s_ibmonly == Defines.ETS_IBM_ONLY || s_ibmonly == Defines.ETS_IBM_CONF){
						for (int u = 0; u < ibmusers.size(); u++) {
							ETSUser user = (ETSUser) ibmusers.elementAt(u);
							String username = ETSUtils.getUsersName(conn, user.getUserId());
							if(s_vResUsers.contains(user.getUserId()))
								writer.println("<option value=\""+ user.getUserId()+ "\" selected=\"selected\">"+ username+ " ["+ user.getUserId()+ "]</option>");
							else
								writer.println("<option value=\""+ user.getUserId()+ "\">"+ username+ " ["+ user.getUserId()+ "]</option>");
						}
					}
					else{
						for (int u = 0; u < users.size(); u++) {
							ETSUser user = (ETSUser) users.elementAt(u);
							String username = ETSUtils.getUsersName(conn, user.getUserId());
							if(s_vResUsers.contains(user.getUserId()))
								writer.println("<option value=\""+ user.getUserId()+ "\" selected=\"selected\">"+ username+ " ["+ user.getUserId()+ "]</option>");
							else
								writer.println("<option value=\""+ user.getUserId()+ "\">"+ username+ " ["+ user.getUserId()+ "]</option>");
						}
					}
					writer.println("</select>");
					writer.println("<tr><td align=\"left\" colspan=\"2\" class=\"small\">[Author will automatically be added to the restricted list]</td></tr>");
				

					if (internal && !parent_cat.isIbmOnlyOrConf()) {
						writer.println("<noscript><tr><td colspan=\"2\" class=\"small\">[If access is restricted to IBM members, only IBM employees selected will be given access.]</td></tr></noscript>");
					}
	
					writer.println("</table>");
					writer.println("</td></tr>");
			  
			
					//notify option
					writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"15\" alt=\"\" /></td></tr>");
					writer.println("<tr><td colspan=\"2\" class=\"tdblue\" height=\"18\"> Notification</td></tr>");

					writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");
					writer.println("<tr><td colspan=\"2\">");
					writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td><b>E-mail notification option:</b>");
					writer.println("<a href=\"ETSContentManagerServlet.wss?OP=Documents&BlurbField=Notification%20option\"  "
						+ "onclick=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Notification%20option','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400');return false\"  "
						+ "onkeypress=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Notification%20option','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400')\" >"
						+ "<img border=\"0\" name=\"Help\" src=\""
						+ Defines.ICON_ROOT+"/popup.gif\" width=\"16\" height=\"16\"  alt=\"Click for help on this field\" /></a>&nbsp;"
						+"</td>");
					writer.println("</td>");
					if (!submit || s_notOpt.equals("to")){
						writer.println("<td><input type=\"radio\" id=\"notifyOption\" name=\"notifyOption\" value=\"to\" checked=\"checked\" /><label for=\"notifyOption\">To</label></td>");
						writer.println("<td><input type=\"radio\" id=\"notifyOption\" name=\"notifyOption\" value=\"bcc\" /><label for=\"notifyOption\">Bcc</label></td>");
					}
					else{
						writer.println("<td><input type=\"radio\" id=\"notifyOption\" name=\"notifyOption\" value=\"to\" /><label for=\"notifyOption\">To</label></td>");
						writer.println("<td><input type=\"radio\" id=\"notifyOption\" name=\"notifyOption\" value=\"bcc\" checked=\"checked\" /><label for=\"notifyOption\">Bcc</label></td>");
					}
					writer.println("</tr></table></td></tr>");

					writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");
					writer.println("<tr><td align=\"left\" colspan=\"2\"><label for=\"notify\"><b>Notify the following team members:</b></label></td></tr>");
					
					if(submit && s_notall != null && s_notall!= "")
						writer.println("<tr><td align=\"left\" colspan=\"2\"><input type=\"checkbox\" name=\"notifyall\" value=\"yes\" id=\"notifyall\" checked=\"checked\" /><label for=\"notifyall\">Notify all</label></td></tr>");
					else
						writer.println("<tr><td align=\"left\" colspan=\"2\"><input type=\"checkbox\" name=\"notifyall\" value=\"yes\" id=\"notifyall\" /><label for=\"notifyall\">Notify all</label></td></tr>");
									
					writer.println("<tr><td align=\"left\" colspan=\"2\" class=\"small\">[For selecting multiple values use Ctrl + Mouse click together.]</td></tr>");
					writer.println("<tr><td colspan=\"2\"><table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
					writer.println("<select name=\"notify\" multiple=\"multiple\" size=\"10\" style=\"width:300px\" width=\"300px\" id=\"notify\">");
					if(s_ibmonly == Defines.ETS_IBM_ONLY || s_ibmonly == Defines.ETS_IBM_CONF){
						for (int u = 0; u < ibmusers.size(); u++) {
							ETSUser user = (ETSUser) ibmusers.elementAt(u);
							String username =ETSUtils.getUsersName(conn, user.getUserId());
							if(s_vNotUsers.contains(user.getUserId()))
								writer.println("<option value=\""+ user.getUserId()+ "\" selected=\"selected\">"+ username+ " ["+ user.getUserId()+ "]</option>");
							else
								writer.println("<option value=\""+ user.getUserId()+ "\">"+ username+ " ["+ user.getUserId()+ "]</option>");
						}
					}
					else{
						for (int u = 0; u < users.size(); u++) {
							ETSUser user = (ETSUser) users.elementAt(u);
							String username =ETSUtils.getUsersName(conn, user.getUserId());
							if(s_vNotUsers.contains(user.getUserId()))
								writer.println("<option value=\""+ user.getUserId()+ "\" selected=\"selected\">"+ username+ " ["+ user.getUserId()+ "]</option>");
							else
								writer.println("<option value=\""+ user.getUserId()+ "\">"+ username+ " ["+ user.getUserId()+ "]</option>");
						}
					}
					writer.println("</select>");

					if (internal && !parent_cat.isIbmOnlyOrConf()) {
						writer.println("<tr><td colspan=\"2\" class=\"small\">[Security classification and additional access restrictions will be applied to the notification list]</td></tr>");
					}
					else if (!internal && !parent_cat.isIbmOnlyOrConf()) {
						writer.println("<tr><td colspan=\"2\" class=\"small\">[User access restrictions will be applied to the notification list]</td></tr>");
					}

					writer.println("</table>");
					writer.println("</td></tr>");

					writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"5\" alt=\"\" /></td></tr>");
					writer.println("</table>");

					writer.println("<br />");

					writer.println("<input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"update document\" /> &nbsp; &nbsp; ");
					writer.println("<a href=\"ETSProjectsServlet.wss?action=details&proj="+ Project.getProjectId()
							+ "&tc="+ TopCatId+ "&cc="+ CurrentCatId+ "&docid="+ docid+ "&linkid="+ linkid
							+ "\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" /> Cancel</a>");
					writer.println("</td></tr></table>");
				}
				else {
					writer.println("error occurred.  document not found to update<br />");
					writer.println("<a href=\"ETSProjectsServlet.wss?action=details&proj="+ Project.getProjectId()
							+ "&tc="+ TopCatId+ "&cc="+ CurrentCatId+ "&docid="+ docid+ "&linkid="+ linkid
							+ "\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" />Close</a>");
				}

				writer.println("</form>");
			}
			else {
				writer.println("invalid cat id for this user.");
				//System.out.print("put bad parent cat id message here");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			//System.out.println("error here");
		}
	}

	//doUpdateDoc2(olddocid,name,desc,keywords,options,msg);
	
	private void doUpdateDoc2(
		int olddocid,
		String name,
		String desc,
		String keywords,
		String options,
		boolean replace,
		String msg) {

		try {
			if (userRole.equals(Defines.ETS_EXECUTIVE)
				|| userRole.equals(Defines.WORKSPACE_VISITOR)) {
				if (replace)
					printHeader("", "Replace draft document", false);
				else
					printHeader("", "Update document", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
				writer.println("You are not authorized to perform this action");
				return;
			}

			//System.out.println("spn " + new java.util.Date(System.currentTimeMillis()));
			Vector breadcrumb = new Vector();
			boolean internal = false;
			if ((es.gDECAFTYPE.trim()).equalsIgnoreCase("I")) {
				internal = true;
			}

			ETSProj proj = Project;
			ETSCat parent_cat = null;
			parent_cat = ETSDatabaseManager.getCat(CurrentCatId);
			if (parent_cat == null) {
				//System.out.print("put bad parent cat id message here");
			}

			if (parent_cat != null) {
				breadcrumb = getBreadcrumb(parent_cat);
				String header = getBreadcrumbTrail(breadcrumb);

				if (replace)
					printHeader(header, "Replace draft document", false);
				else
					printHeader(header, "Update document", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");

			}
			else {
				//System.out.print("put bad parent cat id message here");
			}

			writer.println(
				"<form action=\"ETSContentUploadServlet.wss\" method=\"post\" enctype=\"multipart/form-data\" name=\"updatedocForm\" >");

			if (replace)
				writer.println(
					"<input type=\"hidden\" name=\"action\" value=\"replacedoc2\" />");
			else
				writer.println(
					"<input type=\"hidden\" name=\"action\" value=\"updatedoc2\" />");

			writer.println(
				"<input type=\"hidden\" name=\"proj\" value=\""
					+ proj.getProjectId()
					+ "\" />");
			writer.println(
				"<input type=\"hidden\" name=\"tc\" value=\""
					+ TopCatId
					+ "\" />");
			writer.println(
				"<input type=\"hidden\" name=\"cc\" value=\""
					+ parent_cat.getId()
					+ "\" />");
			writer.println(
				"<input type=\"hidden\" name=\"linkid\" value=\""
					+ linkid
					+ "\" />");
			writer.println(
				"<input type=\"hidden\" name=\"docid\" value=\""
					+ olddocid
					+ "\" />");
			writer.println(
				"<input type=\"hidden\" name=\"docname\" value=\""
					+ name
					+ "\" />");
			writer.println(
				"<input type=\"hidden\" name=\"docdesc\" value=\""
					+ desc
					+ "\" />");
			writer.println(
				"<input type=\"hidden\" name=\"keywords\" value=\""
					+ keywords
					+ "\" />");
			writer.println(
				"<input type=\"hidden\" name=\"options\" value=\""
					+ options
					+ "\" />");

			ETSDoc doc =
				ETSDatabaseManager.getDocByIdAndProject(
					olddocid,
					proj.getProjectId());
			if (doc == null) {
				writer.println("ERROR: Document not found.");
				return;
			}
			writer.println(
				"<input type=\"hidden\" name=\"oldibm\" value=\""
					+ doc.getIbmOnly()
					+ "\" />");

			if (!((msg.trim()).equals(""))) {
				if (msg.equals("0")) {
					msg = "Error occurred, please try again.";
				}
				if (msg.equals("1")) {
					msg = "No file name was submitted to be uploaded.";
				}
				else if (msg.equals("2")) {
					msg = "The document name must be 1-128 characters long.";
				}
				else if (msg.equals("3")) {
					msg =
						"The file is over the 100MB limit.  Please use the DropBox for this file.";
				}
				else if (msg.equals("4")) {
					msg = "Error occurred, please try again";
				}
				else if (msg.equals("5")) {
					msg =
						"Error occurred while uploading document. Please try again.";
				}
				else {
					msg = "Error occurred, document was added";
				}

				writer.println(
					"<table><tr><td><span style=\"color:#ff3333\">"
						+ msg
						+ "</span></td></tr></table>");
			}

			writer.println(
				"<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			writer.println(
				"<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td></tr>");
			writer.println(
				"<tr><td><span class=\"ast\"><b>*</b></span></td><td><span class=\"small\"> Denotes required fields</span></td>");
			writer.println("</tr></table>");

			writer.println(
				"<table width=\"443\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			writer.println(
				"<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"20\" height=\"1\" alt=\"\" /></td>");
			writer.println("<td>");

			writer.println(
				"<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			writer.println(
				"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");

			writer.println(
				"<tr><td colspan=\"2\"><table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");

			//name
			writer.println("<tr><td align=\"left\"><b>Name:</b></td>");
			writer.println("<td>" + name + "</td></tr>");

			//description
			writer.println(
				"<tr><td align=\"left\"><b>Description:</b>&nbsp; &nbsp;</td>");
			writer.println("<td>" + desc + "</td></tr>");

			//keywords
			writer.println("<tr><td align=\"left\"><b>Keywords:</b></td>");
			writer.println("<td>" + keywords + "</td></tr>");

			//option
			writer.println("<tr><td align=\"left\"><b>Option:</b></td>");
			if (options.equals(String.valueOf(Defines.DOC_SUB_APP))) {
				writer.println("<td>Save and send to approver</td></tr>");
			}
			else if (options.equals(String.valueOf(Defines.DOC_DRAFT))) {
				writer.println("<td>Save as draft</td></tr>");
			}
			else {
				writer.println("<td>Save and publish</td></tr>");
			}
			writer.println("</table></td></tr>");
			writer.println(
				"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td></tr>");

			//file
			writer.println(
				"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"5\" alt=\"\" /></td></tr>");
			writer.println(
				"<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
			writer.println(
				"<td align=\"left\" width=\"99%\"><label for=\"docfile\"><b>File:</b></label></td></tr>");
			writer.println(
				"<tr><td colspan=\"2\"><input type=\"file\" id=\"docfile\" size=\"25\" style=\"width:300px\" width=\"300px\" name=\"docfile\" value=\"\" /></td>");

			//ibm only
			if ((es.gDECAFTYPE.trim()).equalsIgnoreCase("I")) {
				internal = true;
			}
			if (internal) {
				writer.println(
					"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");
				writer.println(
					"<tr><td align=\"left\" colspan=\"2\"><label for=\"ibmonly\"><span style=\"color:#ff3333\"><b>Security classification</b></span></label>"
					+ "<a href=\"ETSContentManagerServlet.wss?OP=Documents&BlurbField=Sec\"  "
					+ "onclick=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Security','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400');return false\"  "
					+ "onkeypress=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Security','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400')\" >"
					+ "<img border=\"0\" name=\"Help\" src=\""
					+ Defines.ICON_ROOT+"/popup.gif\" width=\"16\" height=\"16\"  alt=\"Click for help on this field\" /></a>&nbsp;"
					+"</td></tr>");
			}
			else {
				writer.println(
					"<input type=\"hidden\" name=\"ibmonly\" value=\"0\" />");
			}

			if (parent_cat.getIbmOnly() == Defines.ETS_IBM_CONF
				|| doc.getIbmOnly() == '2') {
				writer.println(
					"<tr><td align=\"left\" colspan=\"2\"><b>Access to this document is limited to IBM team members and can never be changed.</b><br />");
				writer.println(
					"<input type=\"hidden\" name=\"ibmonly\" value=\"2\" /></td></tr>");
			}
			else if (parent_cat.getIbmOnly() == Defines.ETS_IBM_ONLY) {
				writer.println(
					"<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
				writer.println(
					"<td align=\"left\" width=\"99%\"><b>Access limited to:</b></td></tr>");
				writer.println(
					"<tr><td align=\"left\" valign=\"bottom\" colspan=\"2\">");
				writer.println("<select id=\"ibmonly\" name=\"ibmonly\" />");

				if (doc.getIbmOnly() == Defines.ETS_IBM_ONLY)
					writer.println(
						"<option value=\"1\" selected=\"selected=\">All IBM team members</option>");
				else
					writer.println(
						"<option value=\"1\">All IBM team members</option>");

				if (doc.getIbmOnly() == Defines.ETS_IBM_CONF)
					writer.println(
						"<option value=\"2\" selected=\"selected=\">All IBM team members permanently</option>");
				else
					writer.println(
						"<option value=\"2\">All IBM team members permanently</option>");

				writer.println("</select></td></tr>");
				writer.println(
					"<tr><td class=\"small\" colspan=\"2\">To make accessible to all team members, its parent folder must be accessible to all team members.</td></tr>");

			}
			else {
				if (internal) {
					writer.println(
						"<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
					writer.println(
						"<td align=\"left\" width=\"99%\"><b>Access limited to:</b></td></tr>");
					writer.println(
						"<tr><td align=\"left\" valign=\"bottom\" colspan=\"2\">");
					writer.println(
						"<select id=\"ibmonly\" name=\"ibmonly\" />");
					if (doc.getIbmOnly() == Defines.ETS_PUBLIC)
						writer.println(
							"<option value=\"0\" selected=\"selected=\">All team members</option>");
					else
						writer.println(
							"<option value=\"0\">All team members</option>");

					if (doc.getIbmOnly() == Defines.ETS_IBM_ONLY)
						writer.println(
							"<option value=\"1\" selected=\"selected=\">All IBM team members</option>");
					else
						writer.println(
							"<option value=\"1\">All IBM team members</option>");

					if (doc.getIbmOnly() == Defines.ETS_IBM_CONF)
						writer.println(
							"<option value=\"2\" selected=\"selected=\">All IBM team members permanently</option>");
					else
						writer.println(
							"<option value=\"2\">All IBM team members permanently</option>");
					writer.println("</select></td></tr>");
				}
			}

			if (internal
				&& parent_cat.getIbmOnly() == Defines.ETS_PUBLIC
				&& doc.getIbmOnly() == Defines.ETS_IBM_ONLY) {
				writer.println(
					"<td align=\"left\" colspan=\"2\" class=\"small\"><span style=\"color:#ff3333\"><b>Warning:</b></span>");
				writer.println(
					" If you change the security classification to accessible to all team members, all team members will be able to view this document and its previous version(s).<td></tr>");
			}

			//notify option
			writer.println(
				"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");

			Vector users =
				ETSDatabaseManager.getProjMembers(Project.getProjectId(), true);
			if (parent_cat.isIbmOnlyOrConf()) {
				users = getIBMMembers(users, conn);
			}

			
			writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"15\" alt=\"\" /></td></tr>");
			writer.println("<tr><td class=\"tdblue\" height=\"18\" colspan=\"2\"> Notification</td></tr>");

			writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");
			writer.println("<tr><td colspan=\"2\">");
			writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td><b>E-mail notification option:</b>");
			writer.println("<a href=\"ETSContentManagerServlet.wss?OP=Documents&BlurbField=Notification%20option\"  "
				+ "onclick=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Notification%20option','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400');return false\"  "
				+ "onkeypress=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Notification%20option','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400')\" >"
				+ "<img border=\"0\" name=\"Help\" src=\""
				+ Defines.ICON_ROOT+"/popup.gif\" width=\"16\" height=\"16\"  alt=\"Click for help on this field\" /></a>&nbsp;");
			writer.println("</td>");
			writer.println("<td><input type=\"radio\" id=\"notifyOption\" name=\"notifyOption\" value=\"to\" checked=\"checked\" /><label for=\"notifyOption\">To</label></td>");
			writer.println("<td><input type=\"radio\" id=\"notifyOption\" name=\"notifyOption\" value=\"bcc\" /><label for=\"notifyOption\">Bcc</label></td>");
			writer.println("</tr></table></td></tr>");

			writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");

			writer.println("<tr><td align=\"left\" colspan=\"2\"><label for=\"notify\"><b>Notify following team members:</b></label></td></tr>");
			writer.println("<tr><td align=\"left\"><input type=\"checkbox\" name=\"notifyall\" value=\"yes\" id=\"notifyall\" /><label for=\"notifyall\">Notify all</label></td></tr>");
			
			writer.println("<tr><td align=\"left\" colspan=\"2\" class=\"small\">[For selecting multiple values use Ctrl + Mouse click together.]</td></tr>");
			writer.println("<tr><td colspan=\"2\"><table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			writer.println("<select name=\"notify\" multiple=\"multiple\" size=\"10\" style=\"width:300px\" width=\"300px\" id=\"notify\">");
			for (int u = 0; u < users.size(); u++) {
				ETSUser user = (ETSUser) users.elementAt(u);
				String username = ETSUtils.getUsersName(conn, user.getUserId());
				writer.println(
					"<option value=\""
						+ user.getUserId()
						+ "\">"
						+ username
						+ " ["
						+ user.getUserId()
						+ "]</option>");
			}
			writer.println("</select>");
			
			if (internal && !parent_cat.isIbmOnlyOrConf()) {
				writer.println("<tr><td colspan=\"2\" class=\"small\">[Security classification and additional access restrictions will be applied to the notification list]</td></tr>");
			}
			else if (!internal && !parent_cat.isIbmOnlyOrConf()) {
				writer.println("<tr><td colspan=\"2\" class=\"small\">[User access restrictions will be applied to the notification list]</td></tr>");
			}


			writer.println("</table>");
			writer.println("</td></tr>");

			writer.println(
				"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"5\" alt=\"\" /></td></tr>");
			writer.println("</table>");

			writer.println("<br />");

			if (replace)
				writer.println(
					"Warning: This action will replace the existing document.");

			writer.println(
				"<input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"update document\" /> &nbsp; &nbsp;");
			writer.println(
				"<a href=\"ETSProjectsServlet.wss?proj="
					+ Project.getProjectId()
					+ "&tc="
					+ TopCatId
					+ "&cc="
					+ CurrentCatId
					+ "&linkid="
					+ linkid
					+ "\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" />Cancel</a>");
			writer.println("</td></tr></table>");

			writer.println(
				"<table  border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			writer.println(
				"<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");
			writer.println(
				"<tr><td><span class=\"small\">Note: If posting material that may be used as training information for other customers, send to your IBM contact before posting.</span></td></tr></table>");
			writer.println("</form>");

		}
		catch (Exception e) {
			writer.println("error occurred");
			e.printStackTrace();
			//System.out.println("error here=" + e);
		}
		//System.out.println("spn " + new java.util.Date(System.currentTimeMillis()));

	}

	private void doUpdateDocC(
		int docid,
		String name,
		String desc,
		String keywords,
		String ibmonly,
		String notify,
		String filename) {
		try {
			if (userRole.equals(Defines.ETS_EXECUTIVE)
				|| userRole.equals(Defines.WORKSPACE_VISITOR)) {
				printHeader("", "Update document", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
				writer.println("You are not authorized to perform this action");
				return;
			}

			Vector breadcrumb = new Vector();

			ETSProj proj = Project;

			ETSDoc thisDoc =
				ETSDatabaseManager.getDocByIdAndProject(
					docid,
					proj.getProjectId());
			ETSCat CurrCat = ETSDatabaseManager.getCat(CurrentCatId);

			if (thisDoc != null) {
				breadcrumb = getBreadcrumb(CurrCat);
				//printBreadcrumb(breadcrumb, writer);
				//printTitle("Update document: "+thisDoc.getName());
				String header = getBreadcrumbTrail(breadcrumb);
				printHeader(header, "Update document", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");

				writer.println(
					"<form action=\"ETSContentUploadServlet.wss\" method=\"post\" enctype=\"multipart/form-data\" name=\"updatecatForm\">");
				writer.println(
					"<input type=\"hidden\" name=\"action\" value=\"updatedoc2\" />");
				writer.println(
					"<input type=\"hidden\" name=\"proj\" value=\""
						+ proj.getProjectId()
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"tc\" value=\""
						+ TopCatId
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"cc\" value=\""
						+ CurrentCatId
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"linkid\" value=\""
						+ linkid
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"docid\" value=\""
						+ docid
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"oldibm\" value=\""
						+ ibmonly
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"docname\" value=\""
						+ name
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"docdesc\" value=\""
						+ desc
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"keywords\" value=\""
						+ keywords
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"ibmonly\" value=\""
						+ ibmonly
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"notify\" value=\""
						+ notify
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"docfile\" value=\""
						+ filename
						+ "\" />");

				writer.println(
					"<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
				writer.println(
					"<tr><td colspan=\"2\"><span style=\"color:#ff3333\"><b>Warning:</b></span></td></tr>");
				writer.println(
					"<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");

				writer.println(
					"<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"20\" height=\"1\" alt=\"\" /></td>");
				writer.println("<td>");

				StringBuffer message =
					new StringBuffer("You are about to change this document ");
				message.append(
					"from restricted to IBM team members to accessible to all team members.");

				writer.println(
					"<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
				writer.println("<tr><td>" + message.toString() + "</td></tr>");
				writer.println(
					"<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td></tr>");
				writer.println(
					"<tr><td> Press continue if you would like to proceed or back to edit this action.</td></tr>");
				writer.println(
					"<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td></tr>");
				writer.println("</table>");

				writer.println(
					"<input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "continue.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"submit\" /> &nbsp;&nbsp;&nbsp;");
				writer.println(
					"<a href=\"ETSProjectsServlet.wss?action=updatedoc&proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ CurrentCatId
						+ "&docid="
						+ docid
						+ "&linkid="
						+ linkid
						+ "\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_lt.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Back\" />Back</a> &nbsp;&nbsp;&nbsp;");
				writer.println(
					"<a href=\"ETSProjectsServlet.wss?proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ linkid
						+ "\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" />Cancel</a>");

				writer.println("</td></tr></table>");
				writer.println("</form>");

			}
			else {
				writer.println("error occurred: invalid cat id for this user.");
				//System.out.print("put bad cat id message here");
			}
		}
		catch (Exception e) {
			writer.println("error occurred");
			e.printStackTrace();
			System.out.print("error here");
		}

	}



	private Vector getBreadcrumb(ETSCat parent_cat) throws SQLException {
		Vector breadcrumb = new Vector();
		try {
			if (parent_cat != null) {
				breadcrumb.addElement(parent_cat);
				ETSCat c = parent_cat;
				while (true) {
					c = ETSDatabaseManager.getCat(c.getParentId());
					if (c != null) {
						breadcrumb.addElement(c);
						if (c.getParentId() == 0) {
							break;
						}
					}
					else {
						break;
					}
				}

			}
			return breadcrumb;
		}
		catch (SQLException se) {
			throw se;
		}
	}


	private void printBreadcrumb(Vector breadcrumb, PrintWriter writer) {
		StringBuffer buf = new StringBuffer();

		buf.append(
			"<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td class=\"small\">");
		for (int i = (breadcrumb.size()) - 1; i >= 0; i--) {
			ETSCat bc = (ETSCat) breadcrumb.elementAt(i);
			if (i != (breadcrumb.size()) - 1) {
				buf.append(" &gt; ");
			}

			if (i == 0) {
				buf.append("<b>" + bc.getName() + "</b>");
			}
			else {
				buf.append(bc.getName());
			}
		}
		buf.append("</td></tr>");

		//gray dotted line
		buf.append("<tr><td height=\"21\">");
		buf.append(
			"<img src=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" height=\"1\" width=\"443\" alt=\"\" />");
		buf.append("</td></tr>");
		buf.append("</table>");
		writer.println(buf.toString());

	}
	private String getBreadcrumbTrail(Vector breadcrumb) {
		StringBuffer buf = new StringBuffer();

		for (int i = (breadcrumb.size()) - 1; i >= 0; i--) {
			ETSCat bc = (ETSCat) breadcrumb.elementAt(i);
			if (i != (breadcrumb.size()) - 1) {
				buf.append(" &gt; ");
			}

			if (i == 0) {
				buf.append("<b>" + bc.getName() + "</b>");
			}
			else {
				buf.append(bc.getName());
			}
		}
		return buf.toString();

	}

	// 4.5.1 *************************************
	private void doMoveDoc(String docidStr, String msg) {
		try {
			if (userRole.equals(Defines.ETS_EXECUTIVE) || userRole.equals(Defines.WORKSPACE_VISITOR)) {
				printHeader("", "Move document", false);
				writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
				writer.println("You are not authorized to perform this action");
				return;
			}

			Vector breadcrumb = new Vector();

			ETSProj proj = Project;
			ETSCat parent_cat = ETSDatabaseManager.getCat(CurrentCatId);

			if (parent_cat != null) {
				breadcrumb = getBreadcrumb(parent_cat);
				String header = getBreadcrumbTrail(breadcrumb);
				printHeader(header, "Move document", false);
				writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");

				int docid = new Integer(docidStr).intValue();

				ETSDoc doc =ETSDatabaseManager.getDocByIdAndProject(docid,Project.getProjectId());
				
				if (doc == null) {
					writer.println("Invalid document id");
					return;
				}
				if (doc.isIbmOnlyOrConf() && (!(es.gDECAFTYPE.trim()).equalsIgnoreCase("I"))) {
					writer.println("You are not authorized to access this document");
					return;
				}
				if ((!doc.getUserId().equals(es.gIR_USERN))&& (!ETSDatabaseManager.hasProjectPriv(es.gIR_USERN,Project.getProjectId(),Defines.ADMIN))&& (!isSuperAdmin)) {
					writer.println("You are not authorized to move this document");
					return;
				}
				
				Vector resusers = new Vector();
				if (doc.IsDPrivate()){
					resusers = ETSDatabaseManager.getRestrictedProjMemberIds(Project.getProjectId(),doc.getId(),false);
					if (!ETSDocCommon.isAuthorized(doc.getUserId(),userRole,isSuperAdmin,isExecutive,resusers,false,es.gIR_USERN)){
						writer.println("You are not authorized to move this document");
						writer.println("<a href=\"ETSProjectsServlet.wss?proj="+ Project.getProjectId()
							+ "&tc="+ TopCatId+ "&cc="+ CurrentCatId+ "&linkid="+ linkid
							+ "\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" /> Ok</a>");
					return;
					}
				}

				if (!doc.isLatestVersion()) {
					writer.println("You are not authorized to move this document.  (previous version) <br /><br />");
					writer.println(
						"<a href=\"ETSProjectsServlet.wss?proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&linkid="
							+ linkid
							+ "\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" /> Ok</a>");
					return;
				}

				writer.println("<form action=\"ETSProjectsServlet.wss\" method=\"get\" name=\"movedocForm\">");
				if (!((msg.trim()).equals(""))) {
					if (msg.equals("1")) {
						msg = "An error occured with the current document id.";
					}
					else {
						msg = "Error occurred:  invalid category id.";
					}

					writer.println(
						"<table><tr><td><span style=\"color:#ff3333\">"
							+ msg
							+ "</span></td></tr></table>");
				}

				writer.println(
					"<input type=\"hidden\" name=\"action\" value=\"movedoc2\" />");
				writer.println(
					"<input type=\"hidden\" name=\"proj\" value=\""
						+ proj.getProjectId()
						+ "\" />");
				writer.println("<input type=\"hidden\" name=\"tc\" value=\""
						+ TopCatId
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"cc\" value=\""
						+ parent_cat.getId()
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"docid\" value=\""
						+ docid
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"linkid\" value=\""
						+ linkid
						+ "\" />");

				if (doc != null) {
					displayAllCats(writer, parent_cat.getId(), 0,false);
					writer.println(
						"<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
					writer.println("<tr>");
					writer.println(
						"<td width=\"130\" valign=\"top\"><input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"move document\" /> &nbsp; &nbsp;</td> ");
					writer.println(
						"<td width=\"16\" height=\"21\" valign=\"top\"><a href=\""
							+ Defines.SERVLET_PATH
							+ "ETSProjectsServlet.wss?proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&linkid="
							+ linkid
							+ "\" ><img src=\""
							+ Defines.ICON_ROOT
							+ "bk.gif\" width=\"16\" height=\"16\" alt=\"Back\" border=\"0\" /></a></td>");
					writer.println(
						"<td align=\"left\" valign=\"top\"><a href=\""
							+ Defines.SERVLET_PATH
							+ "ETSProjectsServlet.wss?action=details&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&docid="
							+ doc.getId()
							+ "&linkid="
							+ linkid
							+ "\"  class=\"fbox\">Back to '"
							+ doc.getName()
							+ "'</a></td>");
					writer.println("</tr>");
					writer.println("</table>");
				}
				else {
					writer.println(
						"error occurred.  document not found.<br />");
				}

				writer.println("</td></tr></table>");
				writer.println("</form>");
			}
			else {
				writer.println("error occurred: invalid cat id for this user.");
				//System.out.print("put bad parent cat id message here");
			}
		}
		catch (Exception e) {
			//System.out.println("error here");
			e.printStackTrace();
		}
	}

	private void doMoveDoc2(String docidStr, String catidStr) {
		try {
			if (userRole.equals(Defines.ETS_EXECUTIVE)
				|| userRole.equals(Defines.WORKSPACE_VISITOR)) {
				printHeader("", "Move document", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
				writer.println("You are not authorized to perform this action");
				return;
			}

			Vector breadcrumb = new Vector();

			ETSProj proj = Project;
			ETSCat parent_cat = ETSDatabaseManager.getCat(CurrentCatId);

			if (parent_cat != null) {
				breadcrumb = getBreadcrumb(parent_cat);
				String header = getBreadcrumbTrail(breadcrumb);
				printHeader(header, "Move document", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");

				int docid = new Integer(docidStr).intValue();
				int catid = new Integer(catidStr).intValue();

				ETSDoc doc =
					ETSDatabaseManager.getDocByIdAndProject(
						docid,
						Project.getProjectId());
				ETSCat cat =
					ETSDatabaseManager.getCat(catid, Project.getProjectId());

				if (doc == null) {
					writer.println("Invalid document id");
					return;
				}
				if (cat == null) {
					writer.println("Invalid folder id");
					return;
				}

				if (doc.isIbmOnlyOrConf()
					&& (!(es.gDECAFTYPE.trim()).equalsIgnoreCase("I"))) {
					writer.println(
						"You are not authorized to access this document");
					return;
				}
				if ((!doc.getUserId().equals(es.gIR_USERN))
					&& (!ETSDatabaseManager
						.hasProjectPriv(
							es.gIR_USERN,
							Project.getProjectId(),
							Defines.ADMIN))
					&& (!isSuperAdmin)) {
					writer.println(
						"You are not authorized to move this document");
					return;
				}
				if (cat.isIbmOnlyOrConf()
					&& (!(es.gDECAFTYPE.trim()).equalsIgnoreCase("I"))) {
					writer.println(
						"You are not authorized to move document to folder");
					return;
				}

				Vector resusers = new Vector();
				if (doc.IsDPrivate()){
					resusers = ETSDatabaseManager.getRestrictedProjMemberIds(Project.getProjectId(),doc.getId(),false);
					if (!ETSDocCommon.isAuthorized(doc.getUserId(),userRole,isSuperAdmin,isExecutive,resusers,false,es.gIR_USERN)){
						writer.println("You are not authorized to move this document");
						writer.println("<a href=\"ETSProjectsServlet.wss?proj="+ Project.getProjectId()
							+ "&tc="+ TopCatId+ "&cc="+ CurrentCatId+ "&linkid="+ linkid
							+ "\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" /> Ok</a>");
					return;
					}
				}


				if (!doc.isLatestVersion()) {
					writer.println(
						"You are not authorized to move this document.  (previous version) <br /><br />");
					writer.println(
						"<a href=\"ETSProjectsServlet.wss?proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&linkid="
							+ linkid
							+ "\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" /> Ok</a>");
					return;
				}

				writer.println("<form action=\"ETSContentManagerServlet.wss\" method=\"post\" name=\"movedocForm\">");

				writer.println(
					"<input type=\"hidden\" name=\"action\" value=\"movedoc3\" />");
				writer.println(
					"<input type=\"hidden\" name=\"proj\" value=\""
						+ proj.getProjectId()
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"tc\" value=\""
						+ TopCatId
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"cc\" value=\""
						+ parent_cat.getId()
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"docid\" value=\""
						+ docid
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"catid\" value=\""
						+ catid
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"linkid\" value=\""
						+ linkid
						+ "\" />");

				writer.println(
					"<img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /><br />");

				writer.println("You are about to move the document: <b>"
						+ doc.getName()
						+ "</b><br />");
				writer.println(
					"to the folder: <b>" + cat.getName() + "</b><br />");

				writer.println(
					"<img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /><br />");

				if (((doc.getIbmOnly() == Defines.ETS_PUBLIC) && cat.isIbmOnlyOrConf())
					|| ((doc.getIbmOnly() == Defines.ETS_IBM_ONLY) && (cat.getIbmOnly() == Defines.ETS_IBM_CONF))) {
					writer.println(
						"<span style=\"color:#ff3333\"><b>Warning:</b></span><br />");

					if (cat.getIbmOnly() == Defines.ETS_IBM_ONLY) {
						writer.println(
							"The folder <b>"
								+ cat.getName()
								+ "</b> is restricted to IBM workspace members only.<br /> ");
						writer.println(
							"By moving this document here, it will automatically be changed to be restricted to IBM workspace members only.<br /> ");
					}
					else {
						writer.println(
							"The folder <b>"
								+ cat.getName()
								+ "</b> is permanently restricted to IBM workspace members only.<br /> ");
						writer.println(
							"By moving this document here, will automatically be changed to be permanently restricted to IBM workspace members only.<br /> ");
					}
				}

				writer.println(
					"<img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /><br />");

				writer.println(
					"<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				writer.println("<tr>");
				writer.println(
					"<td width=\"140\" height=\"21\" valign=\"bottom\" align=\"left\"><input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "continue.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"move document\" /> </td> ");
				writer.println(
					"<td width=\"16\"  height=\"21\" valign=\"bottom\" align=\"right\"><a href=\""
						+ Defines.SERVLET_PATH
						+ "ETSProjectsServlet.wss?action=movedoc&proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ parent_cat.getId()
						+ "&docid="
						+ docid
						+ "&linkid="
						+ linkid
						+ "\" ><img src=\""
						+ Defines.BUTTON_ROOT
						+ "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"Back\" border=\"0\" /></a></td>");
				writer.println(
					"<td align=\"left\" valign=\"bottom\">&nbsp;<a href=\""
						+ Defines.SERVLET_PATH
						+ "ETSProjectsServlet.wss?action=movedoc&proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ parent_cat.getId()
						+ "&docid="
						+ docid
						+ "&linkid="
						+ linkid
						+ "\"  class=\"fbox\">Cancel</a></td>");
				writer.println("</tr>");
				writer.println("</table>");

				writer.println("</form>");
			}
			else {
				writer.println("error occurred: invalid cat id for this user.");
				//System.out.print("put bad parent cat id message here");
			}
		}
		catch (Exception e) {
			//System.out.println("error here");
			e.printStackTrace();
		}
	}

	private void doMoveCatConf(
		String movecatidStr,
		String movetocatidStr,
		String ibmonly) {
		try {
			if (userRole.equals(Defines.ETS_EXECUTIVE)
				|| userRole.equals(Defines.WORKSPACE_VISITOR)) {
				printHeader("", "Move folder", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
				writer.println("You are not authorized to perform this action");
				return;
			}

			Vector breadcrumb = new Vector();

			ETSProj proj = Project;
			ETSCat parent_cat = ETSDatabaseManager.getCat(CurrentCatId);

			if (parent_cat != null) {
				breadcrumb = getBreadcrumb(parent_cat);
				String header = getBreadcrumbTrail(breadcrumb);
				printHeader(header, "Move folder", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");

				int movecatid = new Integer(movecatidStr).intValue();

				ETSCat movecat =
					ETSDatabaseManager.getCat(
						movecatid,
						Project.getProjectId());

				if (movecat == null) {
					writer.println("Invalid folder id");
					return;
				}

				if (movecat.isIbmOnlyOrConf()
					&& (!(es.gDECAFTYPE.trim()).equalsIgnoreCase("I"))) {
					writer.println(
						"You are not authorized to access this document");
					return;
				}
				if ((!movecat.getUserId().equals(es.gIR_USERN))
					&& (!ETSDatabaseManager
						.hasProjectPriv(
							es.gIR_USERN,
							Project.getProjectId(),
							Defines.ADMIN))
					&& (!isSuperAdmin)) {
					writer.println(
						"You are not authorized to move this document");
					return;
				}

				if (parent_cat.isIbmOnlyOrConf()
					&& (!(es.gDECAFTYPE.trim()).equalsIgnoreCase("I"))) {
					writer.println(
						"You are not authorized to move document to folder");
					return;
				}

				writer.println(
					"<img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /><br />");

				writer.println(
					"You have succssfully moved the folder: <b>"
						+ movecat.getName()
						+ "</b> <br />");
				writer.println(
					"to the folder: <b>" + parent_cat.getName() + "</b><br />");

				writer.println(
					"<img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /><br />");

				if (!ibmonly.equals("x")) {
					writer.println(
						"<span style=\"color:#ff3333\"><b>Notice:</b></span><br />");

					if (ibmonly.equals(String.valueOf(Defines.ETS_IBM_ONLY))) {
						writer.println(
							"Access to this folder is now restriced to IBM workspace members<br />");
					}
					else if (
						ibmonly.equals(String.valueOf(Defines.ETS_IBM_CONF))) {
						writer.println(
							"Access to this folder is now permanently restriced to IBM workspace members<br />");
					}
				}

				writer.println(
					"<img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /><br />");

				writer.println(
					"<a href=\"ETSProjectsServlet.wss?proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ parent_cat.getId()
						+ "&linkid="
						+ linkid
						+ "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"continue\" /></a>");

			}
			else {
				writer.println("error occurred: invalid cat id for this user.");
				//System.out.print("put bad parent cat id message here");
			}
		}
		catch (Exception e) {
			//System.out.println("error here");
			e.printStackTrace();
		}
	}

	private void doMoveCat(String msg) {
		try {
			if (userRole.equals(Defines.ETS_EXECUTIVE) || userRole.equals(Defines.WORKSPACE_VISITOR)) {
				printHeader("", "Move folder", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
				writer.println("You are not authorized to perform this action");
				return;
			}

			Vector breadcrumb = new Vector();

			ETSCat current_cat = this_current_cat;
			//databaseManager.getCat(CurrentCatId);
			Vector cats = ETSDocCommon.getValidCatTree(current_cat,es.gIR_USERN,Project.getProjectId(),userRole, Defines.UPDATE,false);

			if (current_cat != null) {
				breadcrumb = getBreadcrumb(current_cat);
				String header = getBreadcrumbTrail(breadcrumb);

				printHeader(header, "Move folder", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");

				writer.println(
					"<form action=\"ETSProjectsServlet.wss\" method=\"get\" name=\"movecatForm\">");
				writer.println(
					"<input type=\"hidden\" name=\"action\" value=\"movecat2\" />");
				writer.println(
					"<input type=\"hidden\" name=\"proj\" value=\""
						+ Project.getProjectId()
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"cc\" value=\""
						+ current_cat.getId()
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"tc\" value=\""
						+ TopCatId
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"linkid\" value=\""
						+ linkid
						+ "\" />");

				if (!((msg.trim()).equals(""))) {
					if (msg.equals("0")) {
						msg = "Invalid top category id for this user.";
					}
					else if (msg.equals("1")) {
						msg = "Invalid project id for this user.";
					}
					else if (msg.equals("2")) {
						msg = "Invalid category choosen to move";
					}
					else if (msg.equals("3")) {
						msg = "You must choose a valid folder to move";
					}

					writer.println(
						"<table><tr><td><span style=\"color:#ff3333\">"
							+ msg
							+ "</span></td></tr></table>");
				}

				
				writer.println(
					"<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"600\">");
				writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td>");
				writer.println("<tr><td class=\"small\">Only subfolders you are authorized to move are listed.</td></tr>");
				//writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"15\" alt=\"\" /></td>");
				//writer.println("<tr><td class=\"small\">To be authorized to move a folder, "
				//		+ "you must also be authorized to move all folders and documents under it.</td></tr>");
				writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td>");
				writer.println("</table>");

				writer.println(
					"<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
				writer.println("<tr>");
				writer.println(
					"<td class=\"small\" valign=\"top\"><label for=\"delcatid\">Move folder:</label></td>");
				writer.println(
					"<td class=\"small\" valign=\"top\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"1\" alt=\"\" /></td>");
				writer.println("<td class=\"small\">");

				if (cats.size() > 0) {
					writer.println(
						"<select id=\"movecatid\" name=\"movecatid\" style=\"width:250px\" width=\"250px\">");
					for (int i = 0; i < cats.size(); i++) {
						ETSCat c = (ETSCat) cats.elementAt(i);
						writer.println(
							"<option value=\""
								+ c.getId()
								+ "\">"
								+ c.getName()
								+ " </option>");
					}
					writer.println("</select>");
				}
				else {
					writer.println("no folders to move");
				}

				writer.println("</td>");
				writer.println("</tr>");
				writer.println("</table>");

				writer.println(
					"<br /><br /><table border=\"0\" cellspacing=\"5\" cellpadding=\"5\"><tr>");
				if (cats.size() > 0) {
					writer.println(
						"<td><input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"move folder\" /></td>");
					writer.println(
						"<td><a href=\"ETSProjectsServlet.wss?proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&linkid="
							+ linkid
							+ "\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" />Cancel</a></td>");
				}
				else {
					writer.println(
						"<td><a href=\"ETSProjectsServlet.wss?proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&linkid="
							+ linkid
							+ "\"><img src=\"" + Defines.BUTTON_ROOT + "arrow_lt.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"OK\" />Go back</a></td>");
				}
				writer.println("</tr></table>");
				writer.println("</form>");
			}
			else {
				writer.println("error occurred:invalid cat id for this user.");
				//System.out.print("put bad current cat id message here");
			}
		}
		catch (Exception e) {
			writer.println("error occurred");
			e.printStackTrace();
			//System.out.println("error here=" + e);
		}
	}

	private void doMoveCat2(ETSCat movecat, String msg) {
		try {
			if (userRole.equals(Defines.ETS_EXECUTIVE)
				|| userRole.equals(Defines.WORKSPACE_VISITOR)) {
				printHeader("", "Move folder", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
				writer.println("You are not authorized to perform this action");
				return;
			}

			Vector breadcrumb = new Vector();

			ETSProj proj = Project;
			ETSCat parent_cat = ETSDatabaseManager.getCat(CurrentCatId);

			if (parent_cat != null) {
				breadcrumb = getBreadcrumb(parent_cat);
				String header = getBreadcrumbTrail(breadcrumb);
				printHeader(header, "Move folder", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");

				if (movecat.isIbmOnlyOrConf()
					&& (!(es.gDECAFTYPE.trim()).equalsIgnoreCase("I"))) {
					writer.println(
						"You are not authorized to access this document");
					return;
				}
				if ((!movecat.getUserId().equals(es.gIR_USERN))
					&& (!ETSDatabaseManager
						.hasProjectPriv(
							es.gIR_USERN,
							Project.getProjectId(),
							Defines.ADMIN))
					&& (!isSuperAdmin)) {
					writer.println(
						"You are not authorized to move this document");
					return;
				}

				writer.println(
					"<form action=\"ETSProjectsServlet.wss\" method=\"get\" name=\"movecat2Form\">");
				if (!((msg.trim()).equals(""))) {
					if (msg.equals("1")) {
						msg = "An error occured with the current folder id.";
					}
					else {
						msg = "Error occurred:  invalid category id.";
					}

					writer.println(
						"<table><tr><td><span style=\"color:#ff3333\">"
							+ msg
							+ "</span></td></tr></table>");
				}

				writer.println(
					"<input type=\"hidden\" name=\"action\" value=\"movecat3\" />");
				writer.println(
					"<input type=\"hidden\" name=\"proj\" value=\""
						+ proj.getProjectId()
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"tc\" value=\""
						+ TopCatId
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"cc\" value=\""
						+ parent_cat.getId()
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"linkid\" value=\""
						+ linkid
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"movecatid\" value=\""
						+ movecat.getId()
						+ "\" />");

				displayAllCats(writer, parent_cat.getId(), movecat.getId(),true);
				writer.println(
					"<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				writer.println("<tr>");
				writer.println(
					"<td width=\"130\" valign=\"top\"><input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"move document\" /> &nbsp; &nbsp;</td> ");
				writer.println(
					"<td width=\"16\" height=\"21\" valign=\"top\"><a href=\""
						+ Defines.SERVLET_PATH
						+ "ETSProjectsServlet.wss?proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&linkid="
						+ linkid
						+ "\" ><img src=\""
						+ Defines.ICON_ROOT
						+ "bk.gif\" width=\"16\" height=\"16\" alt=\"Back\" border=\"0\" /></a></td>");
				writer.println(
					"<td align=\"left\" valign=\"top\"><a href=\""
						+ Defines.SERVLET_PATH
						+ "ETSProjectsServlet.wss?proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ linkid
						+ "\"  class=\"fbox\">Back to '"
						+ parent_cat.getName()
						+ "'</a></td>");
				writer.println("</tr>");
				writer.println("</table>");

				writer.println("</td></tr></table>");
				writer.println("</form>");
			}
			else {
				writer.println("error occurred: invalid cat id for this user.");
				//System.out.print("put bad parent cat id message here");
			}
		}
		catch (Exception e) {
			//System.out.println("error here");
			e.printStackTrace();
		}
	}

	private void doMoveCat3(String movecatStr, String movetocatStr) {
		try {
			if (userRole.equals(Defines.ETS_EXECUTIVE)
				|| userRole.equals(Defines.WORKSPACE_VISITOR)) {
				printHeader("", "Move folder", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
				writer.println("You are not authorized to perform this action");
				return;
			}

			Vector breadcrumb = new Vector();

			ETSProj proj = Project;
			ETSCat parent_cat = ETSDatabaseManager.getCat(CurrentCatId);

			if (parent_cat != null) {
				breadcrumb = getBreadcrumb(parent_cat);
				String header = getBreadcrumbTrail(breadcrumb);
				printHeader(header, "Move folder", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");

				int movecatid = new Integer(movecatStr).intValue();
				int movetocatid = new Integer(movetocatStr).intValue();

				ETSCat movecat =ETSDatabaseManager.getCat(movecatid,Project.getProjectId());
				ETSCat movetocat =ETSDatabaseManager.getCat(movetocatid,Project.getProjectId());

				if (movecat == null) {
					writer.println("Invalid folder id to move");
					return;
				}
				if (movetocat == null) {
					writer.println("Invalid folder id to move folder to");
					return;
				}

				if (movecat.isIbmOnlyOrConf()
					&& (!(es.gDECAFTYPE.trim()).equalsIgnoreCase("I"))) {
					writer.println(
						"You are not authorized to access this folder");
					return;
				}
				if ((!movecat.getUserId().equals(es.gIR_USERN))
					&& (!ETSDatabaseManager
						.hasProjectPriv(
							es.gIR_USERN,
							Project.getProjectId(),
							Defines.ADMIN))
					&& (!isSuperAdmin)) {
					writer.println(
						"You are not authorized to move this folder");
					return;
				}
				if (movetocat.isIbmOnlyOrConf()
					&& (!(es.gDECAFTYPE.trim()).equalsIgnoreCase("I"))) {
					writer.println(
						"You are not authorized to move to chosen folder");
					return;
				}

				writer.println(
					"<form action=\"ETSContentManagerServlet.wss\" method=\"post\" name=\"movecat3Form\">");

				writer.println(
					"<input type=\"hidden\" name=\"action\" value=\"movecat4\" />");
				writer.println(
					"<input type=\"hidden\" name=\"proj\" value=\""
						+ proj.getProjectId()
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"tc\" value=\""
						+ TopCatId
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"cc\" value=\""
						+ parent_cat.getId()
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"movecatid\" value=\""
						+ movecat.getId()
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"movetocatid\" value=\""
						+ movetocat.getId()
						+ "\" />");
				writer.println(
					"<input type=\"hidden\" name=\"linkid\" value=\""
						+ linkid
						+ "\" />");

				writer.println(
					"<img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /><br />");

				writer.println(
					"You are about to move the folder: <b>"
						+ movecat.getName()
						+ "</b><br />");
				writer.println(
					"to the folder: <b>" + movetocat.getName() + "</b><br />");

				writer.println(
					"<img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /><br />");

				if (((movecat.getIbmOnly() == Defines.ETS_PUBLIC) && movetocat.isIbmOnlyOrConf())
					|| ((movecat.getIbmOnly() == Defines.ETS_IBM_ONLY) && (movetocat.getIbmOnly() == Defines.ETS_IBM_CONF))) {
					writer.println("<span style=\"color:#ff3333\"><b>Warning:</b></span><br />");

					if (movetocat.getIbmOnly() == Defines.ETS_IBM_ONLY) {
						writer.println(
							"The folder <b>"
								+ movetocat.getName()
								+ "</b> is restricted to IBM workspace members only.<br /> ");
						writer.println(
							"By moving this folder here, it and all of its sub folders and sub documents will automatically be changed to be restricted to IBM workspace members only.<br /> ");
					}
					else {
						writer.println(
							"The folder <b>"
								+ movetocat.getName()
								+ "</b> is permanently restricted to IBM workspace members only.<br /> ");
						writer.println(
							"By moving this folder here, it and all of its sub folders and sub documents will automatically be changed to be permanently restricted to IBM workspace members only.<br /> ");
					}
					
					Vector v = ETSDocCommon.getCatSubTreeOwners(new Vector(),movecat.getId(),proj.getProjectId(),es.gIR_USERN,userRole,Defines.UPDATE,true);
					
					if (v.size()>0){
						writer.println("<br /><br />The following is a list of users that own folders/documents under your folder.");
						writer.println("You may want to consult with them prior to proceeding with this action.<br />");
	
						writer.println("<br /><b>Other owners:</b><br />");
						for (int vi = 0; vi < v.size(); vi++){
							writer.println(ETSUtils.getUsersName(conn,(String)v.elementAt(vi))+"<br />");	
						}
					}
					
				}

				writer.println(
					"<img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /><br />");

				writer.println(
					"<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
				writer.println("<tr>");
				writer.println(
					"<td width=\"140\" valign=\"bottom\" align=\"left\"><input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "continue.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"move folder\" /></td> ");

				writer.println(
					"<td width=\"16\"  valign=\"bottom\" align=\"right\"><a href=\""
						+ Defines.SERVLET_PATH
						+ "ETSProjectsServlet.wss?action=movecat2&proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ parent_cat.getId()
						+ "&movecatid="
						+ movecatStr
						+ "&linkid="
						+ linkid
						+ "\" ><img src=\""
						+ Defines.BUTTON_ROOT
						+ "arrow_lt.gif\" width=\"21\" height=\"21\" alt=\"Back\" border=\"0\" /></a></td>");
				writer.println(
					"<td width=\"50\" align=\"left\" valign=\"bottom\">&nbsp;<a href=\""
						+ Defines.SERVLET_PATH
						+ "ETSProjectsServlet.wss?action=movecat2&proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ parent_cat.getId()
						+ "&movecatid="
						+ movecatStr
						+ "&linkid="
						+ linkid
						+ "\"  class=\"fbox\">Back</a></td>");

				writer.println(
					"<td width=\"16\"  valign=\"bottom\" align=\"left\"><a href=\""
						+ Defines.SERVLET_PATH
						+ "ETSProjectsServlet.wss?proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ parent_cat.getId()
						+ "&linkid="
						+ linkid
						+ "\" ><img src=\""
						+ Defines.BUTTON_ROOT
						+ "cancel_rd.gif\" width=\"21\" height=\"21\" alt=\"Cancel\" border=\"0\" /></a></td>");
				writer.println(
					"<td align=\"left\" valign=\"bottom\">&nbsp;<a href=\""
						+ Defines.SERVLET_PATH
						+ "ETSProjectsServlet.wss?proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ parent_cat.getId()
						+ "&linkid="
						+ linkid
						+ "\"  class=\"fbox\">Cancel</a></td>");

				writer.println("</tr>");
				writer.println("</table>");

				writer.println("</form>");
			}
			else {
				writer.println("error occurred: invalid cat id for this user.");
				//System.out.print("put bad parent cat id message here");
			}
		}
		catch (Exception e) {
			//System.out.println("error here");
			e.printStackTrace();
		}
	}

	private void doMoveConf(String docidStr, String catidStr, String ibmonly) {
		try {
			if (userRole.equals(Defines.ETS_EXECUTIVE)
				|| userRole.equals(Defines.WORKSPACE_VISITOR)) {
				printHeader("", "Move document", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
				writer.println("You are not authorized to perform this action");
				return;
			}

			Vector breadcrumb = new Vector();

			ETSProj proj = Project;
			ETSCat parent_cat = ETSDatabaseManager.getCat(CurrentCatId);

			if (parent_cat != null) {
				breadcrumb = getBreadcrumb(parent_cat);
				String header = getBreadcrumbTrail(breadcrumb);
				printHeader(header, "Move document", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");

				int docid = new Integer(docidStr).intValue();

				ETSDoc doc =
					ETSDatabaseManager.getDocByIdAndProject(
						docid,
						Project.getProjectId());

				if (doc == null) {
					writer.println("Invalid document id");
					return;
				}

				if (doc.isIbmOnlyOrConf()
					&& (!(es.gDECAFTYPE.trim()).equalsIgnoreCase("I"))) {
					writer.println(
						"You are not authorized to access this document");
					return;
				}
				if ((!doc.getUserId().equals(es.gIR_USERN))
					&& (!ETSDatabaseManager
						.hasProjectPriv(
							es.gIR_USERN,
							Project.getProjectId(),
							Defines.ADMIN))
					&& (!isSuperAdmin)) {
					writer.println(
						"You are not authorized to move this document");
					return;
				}
				if (parent_cat.isIbmOnlyOrConf()
					&& (!(es.gDECAFTYPE.trim()).equalsIgnoreCase("I"))) {
					writer.println(
						"You are not authorized to move document to folder");
					return;
				}

				if (!doc.isLatestVersion()) {
					writer.println(
						"You are not authorized to move this document.  (previous version) <br /><br />");
					writer.println(
						"<a href=\"ETSProjectsServlet.wss?proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&linkid="
							+ linkid
							+ "\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" /> Ok</a>");
					return;
				}

				writer.println(
					"<img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /><br />");

				writer.println(
					"You have succssfully moved the document: <b>"
						+ doc.getName()
						+ "</b> <br />");
				writer.println(
					"to the folder: <b>" + parent_cat.getName() + "</b><br />");

				writer.println(
					"<img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /><br />");

				if (!ibmonly.equals("x")) {
					writer.println(
						"<span style=\"color:#ff3333\"><b>Notice:</b></span><br />");

					if (ibmonly.equals(String.valueOf(Defines.ETS_IBM_ONLY))) {
						writer.println(
							"Access to this document is now restriced to IBM workspace members<br />");
					}
					else if (
						ibmonly.equals(String.valueOf(Defines.ETS_IBM_CONF))) {
						writer.println(
							"Access to this document is now permanently restriced to IBM workspace members<br />");
					}
				}

				writer.println(
					"<img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /><br />");

				writer.println(
					"<a href=\"ETSProjectsServlet.wss?action=details&proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ parent_cat.getId()
						+ "&docid="
						+ doc.getId()
						+ "&linkid="
						+ linkid
						+ "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"move document\" /></a>");

			}
			else {
				writer.println("error occurred: invalid cat id for this user.");
				//System.out.print("put bad parent cat id message here");
			}
		}
		catch (Exception e) {
			//System.out.println("error here");
			e.printStackTrace();
		}
	}

	// ******************************************
	//private void printRecursive(PrintWriter out, Vector vDetails, Vector noshows, int ID, int oldParentId,int movecatid,boolean showradio,int iLevel) throws SQLException, Exception {
	private void printRecursive(
		PrintWriter out,
		Vector vDetails,
		Vector noshows,
		int ID,
		int oldParentId,
		int movecatid,
		int iLevel)
		throws SQLException, Exception {
		int iMaxCols = 15;
		int iBlankColSpan = 0;
		int iFillColSpan = 0;

		try {
			iBlankColSpan = (iLevel - 1) * 25;

			/*
				String s = "<span class=\"greytext\">|";	
				for (int i =0; i<iLevel; i++){
					for (int j=0; j<4; j++){
						s = s+"_";
					}
					if (i!=(iLevel-1))
						s = s+"|";
				}
				s = s+"</span>";
				*/

			for (int i = 0; i < vDetails.size(); i++) {
				ETSCat cat = (ETSCat) vDetails.elementAt(i);
				boolean showradio = true;

				if (cat.isIbmOnlyOrConf()
					&& !(es.gDECAFTYPE.trim().equals("I"))) {

				}
				else {
					if (cat.getParentId() == ID) {
						out.println(
							"<tr><td class=\"small\" align=\"left\" valign=\"middle\" height=\"1\" >");
						out.println(
							"<img src=\""
								+ Defines.V11_IMAGE_ROOT
								+ "gray_dotted_line.gif\" alt=\"\" width=\"600\" height=\"1\" />");
						out.println("</td></tr>");

						out.println("<tr>");
						out.println(
							"<td class=\"small\" align=\"left\" valign=\"top\">");

						String catname = cat.getName();
						if (movecatid == cat.getId()) {
							noshows.addElement(new Integer(cat.getId()));
							catname = "<b>" + catname + "</b>";
							showradio = false;
						}
						else if (
							noshows.contains(new Integer(cat.getParentId()))) {
							noshows.addElement(new Integer(cat.getId()));
							showradio = false;
						}

						if (showradio) {
							if (cat.getId() == oldParentId) {
								out.println(
									"<input id=\"move2cat\" type=\"radio\" name=\"movetocat\" value=\""
										+ cat.getId()
										+ "\" checked=\"checked\" />");
							}
							else {
								out.println(
									"<input id=\"move2cat\" type=\"radio\" name=\"movetocat\" value=\""
										+ cat.getId()
										+ "\" />");
							}
						}
						else {
							out.println(
								"<img alt=\"\" src=\""
									+ Defines.TOP_IMAGE_ROOT
									+ "c.gif\" width=\"20\" height=\"20\" />");
						}

						out.println("<img alt=\"\" src=\""+ Defines.TOP_IMAGE_ROOT+ "c.gif\" width=\""+ iBlankColSpan+ "\" height=\"1\" />");
						out.println(
							"<img src=\""
								+ Defines.SERVLET_PATH
								+ "ETSImageServlet.wss?proj=ETS_CAT_IMG&mod=0\" width=\"13\" height=\"9\" alt=\"folder\" />");

						if (cat.getIbmOnly() == Defines.ETS_IBM_ONLY)
							out.println(
								catname + "<span class=\"ast\">*</span>");
						else if (cat.getIbmOnly() == Defines.ETS_IBM_CONF)
							out.println(
								catname + "<span class=\"ast\">**</span>");
						else
							out.println(catname);

						out.println("</td>");
						out.println("</tr>");

						// to make it print as a tree format upto 15 levels...
						//printRecursive(out,vDetails,cat.getId(),oldParentId,movecatid,showradio,iLevel + 1);
						printRecursive(
							out,
							vDetails,
							noshows,
							cat.getId(),
							oldParentId,
							movecatid,
							iLevel + 1);
					}
				}
			}

		}
		catch (SQLException e) {
			throw e;
		}
		catch (Exception e) {
			throw e;
		}

	}

	private void displayAllCats(
		PrintWriter out,
		int oldParentId,
		int movecatid, boolean isCat)
		throws SQLException, Exception {

		PreparedStatement pstmt = null;
		try {
			Vector vDet = ETSDatabaseManager.getAllCats(Project.getProjectId());

			out.println("<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"  width=\"100%\">");

			int catid = 0;
			if (!isCat){
				for (int i = 0; i < vDet.size(); i++) {
	
					ETSCat cat = (ETSCat) vDet.elementAt(i);
					if (cat.getProjectId().equals(Project.getProjectId())) {
						catid = cat.getId();
	
						out.println("<tr>");
						
						out.println("<td class=\"small\" align=\"left\" width=\"200\" colspan=\"1\"><table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>");
						out.println(
							"<td align=\"left\" width=\"143\" ><img src=\""
								+ Defines.SERVLET_PATH
								+ "ETSImageServlet.wss?proj=ETS_CAT_IMG&mod=0\" width=\"13\" height=\"9\" alt=\"folder\" />"
								+ cat.getName()
								+ "</td>");
						out.println("</tr></table></td>");
						out.println("</tr>");
						
						//System.out.println("cat.getname="+cat.getName());
						break;
	
					}
				}
			}
			
			printRecursive(out,vDet,new Vector(),catid,oldParentId,movecatid,1);
			
			out.println("<tr>");
			out.println("<td colspan=\"1\">&nbsp;</td>");
			out.println("</tr>");

			out.println("<tr>");
			out.println("<td colspan=\"1\">");
			out.println("<!-- Gray dotted line -->");
			out.println(
				"<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
			out.println("<tr>");
			out.println(
				"<td width=\"1\"><img src=\""
					+ Defines.TOP_IMAGE_ROOT
					+ "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println(
				"<td background=\""
					+ Defines.V11_IMAGE_ROOT
					+ "gray_dotted_line.gif\" width=\"431\"><img alt=\"\" border=\"0\" height=\"1\" width=\"100%\" src=\"//www.ibm.com/i/c.gif\" /></td>");
			out.println(
				"<td width=\"1\"><img src=\""
					+ Defines.TOP_IMAGE_ROOT
					+ "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("<!-- End Gray dotted line -->");
			out.println("</td>");
			out.println("</tr>");

			out.println("</table>");

			out.println("<br />");

		}
		catch (SQLException e) {
			throw e;
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			ETSDBUtils.close(pstmt);
		}

	}

	// &&&&&&&&&&&&&&&&&&&&&&&

	private void doAddRegDoc(int parentId, String meeting_id, String msg) {
		try {
			if (userRole.equals(Defines.ETS_EXECUTIVE)
				|| userRole.equals(Defines.WORKSPACE_VISITOR)) {
				printHeader("", "Add document", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
				writer.println("You are not authorized to perform this action");
				return;
			}

			//System.out.println("spn " + new java.util.Date(System.currentTimeMillis()));
			Vector breadcrumb = new Vector();
			boolean internal = false;
			if ((es.gDECAFTYPE.trim()).equalsIgnoreCase("I")) {
				internal = true;
			}

			ETSProj proj = Project;
			ETSCat parent_cat = null;
			parent_cat = ETSDatabaseManager.getCat(CurrentCatId);
			if (parent_cat == null) {
				//System.out.print("put bad parent cat id message here");
			}

			if (parent_cat != null) {
				breadcrumb = getBreadcrumb(parent_cat);
				String header = getBreadcrumbTrail(breadcrumb);

				printHeader(header, "Add document", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");

			}
			else {
				//System.out.print("put bad parent cat id message here");
			}

			writer.println(
				"<form action=\"ETSProjectsServlet.wss\" method=\"post\" name=\"adddocForm\" >");

			writer.println(
				"<input type=\"hidden\" name=\"action\" value=\"adddoc2\" />");
			writer.println(
				"<input type=\"hidden\" name=\"proj\" value=\""
					+ proj.getProjectId()
					+ "\" />");
			writer.println(
				"<input type=\"hidden\" name=\"tc\" value=\""
					+ TopCatId
					+ "\" />");
			writer.println(
				"<input type=\"hidden\" name=\"cc\" value=\""
					+ parent_cat.getId()
					+ "\" />");
			writer.println(
				"<input type=\"hidden\" name=\"linkid\" value=\""
					+ linkid
					+ "\" />");

			if (!((msg.trim()).equals(""))) {
				if (msg.equals("0")) {
					msg = "Error occurred, please try again.";
				}
				if (msg.equals("1")) {
					msg = "No file name was submitted to be uploaded.";
				}
				else if (msg.equals("2")) {
					msg = "The document name must be 1-128 characters long.";
				}
				else if (msg.equals("4")) {
					msg = "Error occurred, please try again";
				}

				writer.println(
					"<table><tr><td><span style=\"color:#ff3333\">"
						+ msg
						+ "</span></td></tr></table>");
			}

			writer.println(
				"<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			writer.println(
				"<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td></tr>");
			writer.println(
				"<tr><td><span class=\"ast\"><b>*</b></span></td><td><span class=\"small\"> Denotes required fields</span></td>");
			writer.println("</tr></table>");

			writer.println(
				"<table width=\"443\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			writer.println(
				"<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"20\" height=\"1\" alt=\"\" /></td>");
			writer.println("<td>");

			writer.println(
				"<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			writer.println(
				"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");

			printDocPart1();

			//options
			writer.println(
				"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"5\" alt=\"\" /></td></tr>");
			writer.println(
				"<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td><td align=\"left\"><b>Options:</b></td></tr>");
			writer.println(
				"<tr><td colspan=\"2\"><input type=\"radio\" id=\"options\" name=\"options\" value=\""
					+ Defines.DOC_PUBLISH
					+ "\" /><label for=\"options\">Save and publish</label></td></tr>");
			writer.println(
				"<tr><td colspan=\"2\"><input type=\"radio\" id=\"options\" name=\"options\" value=\""
					+ Defines.DOC_DRAFT
					+ "\" /><label for=\"options\">Save as draft</label></td></tr>");
			writer.println(
				"<tr><td colspan=\"2\"><input type=\"radio\" id=\"options\" name=\"options\" value=\""
					+ Defines.DOC_SUB_APP
					+ "\" /><label for=\"options\">Save and send for approval</label></td></tr>");

			writer.println(
				"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"30\" alt=\"\" /></td></tr>");
			writer.println("</table>");

			writer.println(
				"<input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "continue.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"continue\" /> &nbsp; &nbsp;");
			writer.println(
				"<a href=\"ETSProjectsServlet.wss?proj="
					+ Project.getProjectId()
					+ "&tc="
					+ TopCatId
					+ "&cc="
					+ CurrentCatId
					+ "&linkid="
					+ linkid
					+ "\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" />Cancel</a>");
			writer.println("</td></tr></table>");

			writer.println(
				"<table  border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			writer.println(
				"<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");
			writer.println(
				"<tr><td><span class=\"small\">Note: If posting material that may be used as training information for other customers, send to your IBM contact before posting.</span></td></tr></table>");
			writer.println("</form>");

		}
		catch (Exception e) {
			writer.println("error occurred");
			e.printStackTrace();
			//System.out.println("error here=" + e);
		}
		//System.out.println("spn " + new java.util.Date(System.currentTimeMillis()));

	}

	private void doAddRegDoc2(
		int parentId,
		String meeting_id,
		String name,
		String desc,
		String keywords,
		String options,
		String msg) {

		try {
			if (userRole.equals(Defines.ETS_EXECUTIVE)
				|| userRole.equals(Defines.WORKSPACE_VISITOR)) {
				printHeader("", "Add document", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
				writer.println("You are not authorized to perform this action");
				return;
			}

			//System.out.println("spn " + new java.util.Date(System.currentTimeMillis()));
			Vector breadcrumb = new Vector();
			boolean internal = false;
			if ((es.gDECAFTYPE.trim()).equalsIgnoreCase("I")) {
				internal = true;
			}

			ETSProj proj = Project;
			ETSCat parent_cat = null;
			parent_cat = ETSDatabaseManager.getCat(CurrentCatId);
			if (parent_cat == null) {
				//System.out.print("put bad parent cat id message here");
			}

			if (parent_cat != null) {
				breadcrumb = getBreadcrumb(parent_cat);
				String header = getBreadcrumbTrail(breadcrumb);

				printHeader(header, "Add document", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");

			}
			else {
				//System.out.print("put bad parent cat id message here");
			}

			writer.println(
				"<form action=\"ETSContentUploadServlet.wss\" method=\"post\" enctype=\"multipart/form-data\" name=\"adddocForm\" >");
			writer.println(
				"<input type=\"hidden\" name=\"action\" value=\"adddoc2\" />");

			writer.println(
				"<input type=\"hidden\" name=\"proj\" value=\""
					+ proj.getProjectId()
					+ "\" />");
			writer.println(
				"<input type=\"hidden\" name=\"tc\" value=\""
					+ TopCatId
					+ "\" />");
			writer.println(
				"<input type=\"hidden\" name=\"cc\" value=\""
					+ parent_cat.getId()
					+ "\" />");
			writer.println(
				"<input type=\"hidden\" name=\"linkid\" value=\""
					+ linkid
					+ "\" />");
			writer.println(
				"<input type=\"hidden\" name=\"docname\" value=\""
					+ name
					+ "\" />");
			writer.println(
				"<input type=\"hidden\" name=\"docdesc\" value=\""
					+ desc
					+ "\" />");
			writer.println(
				"<input type=\"hidden\" name=\"keywords\" value=\""
					+ keywords
					+ "\" />");
			writer.println(
				"<input type=\"hidden\" name=\"options\" value=\""
					+ options
					+ "\" />");

			if (!((msg.trim()).equals(""))) {
				if (msg.equals("0")) {
					msg = "Error occurred, please try again.";
				}
				if (msg.equals("1")) {
					msg = "No file name was submitted to be uploaded.";
				}
				else if (msg.equals("2")) {
					msg = "The document name must be 1-128 characters long.";
				}
				else if (msg.equals("3")) {
					msg =
						"The file is over the 100MB limit.  Please use the DropBox for this file.";
				}
				else if (msg.equals("4")) {
					msg = "Error occurred, please try again";
				}
				else if (msg.equals("5")) {
					msg =
						"Error occurred while uploading document. Please try again.";
				}
				else {
					msg = "Error occurred, document was added";
				}

				writer.println(
					"<table><tr><td><span style=\"color:#ff3333\">"
						+ msg
						+ "</span></td></tr></table>");
			}

			writer.println(
				"<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			writer.println(
				"<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td></tr>");
			writer.println(
				"<tr><td><span class=\"ast\"><b>*</b></span></td><td><span class=\"small\"> Denotes required fields</span></td>");
			writer.println("</tr></table>");

			writer.println(
				"<table width=\"443\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			writer.println(
				"<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"20\" height=\"1\" alt=\"\" /></td>");
			writer.println("<td>");

			writer.println(
				"<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			writer.println(
				"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");

			writer.println(
				"<tr><td colspan=\"2\"><table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");

			//name
			writer.println("<tr><td align=\"left\"><b>Name:</b></td>");
			writer.println("<td>" + name + "</td></tr>");

			//description
			writer.println(
				"<tr><td align=\"left\"><b>Description:</b>&nbsp; &nbsp;</td>");
			writer.println("<td>" + desc + "</td></tr>");

			//keywords
			writer.println("<tr><td align=\"left\"><b>Keywords:</b></td>");
			writer.println("<td>" + keywords + "</td></tr>");

			//option
			writer.println("<tr><td align=\"left\"><b>Option:</b></td>");
			if (options.equals(String.valueOf(Defines.DOC_SUB_APP))) {
				writer.println("<td>Save and send to approver</td></tr>");
			}
			else if (options.equals(String.valueOf(Defines.DOC_DRAFT))) {
				writer.println("<td>Save as draft</td></tr>");
			}
			else {
				writer.println("<td>Save and publish</td></tr>");
			}
			writer.println("</table></td></tr>");
			writer.println(
				"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td></tr>");

			printDocPart2("adddoc2", internal, parent_cat, options);

			writer.println(
				"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"20\" alt=\"\" /></td></tr>");
			writer.println("</table>");

			writer.println(
				"<input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"add document\" /> &nbsp; &nbsp;");
			writer.println(
				"<a href=\"ETSProjectsServlet.wss?proj="
					+ Project.getProjectId()
					+ "&tc="
					+ TopCatId
					+ "&cc="
					+ CurrentCatId
					+ "&linkid="
					+ linkid
					+ "\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" />Cancel</a>");
			writer.println("</td></tr></table>");

			writer.println(
				"<table  border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			writer.println(
				"<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");
			writer.println(
				"<tr><td><span class=\"small\">Note: If posting material that may be used as training information for other customers, send to your IBM contact before posting.</span></td></tr></table>");
			writer.println("</form>");

		}
		catch (Exception e) {
			writer.println("error occurred");
			e.printStackTrace();
			//System.out.println("error here=" + e);
		}
		//System.out.println("spn " + new java.util.Date(System.currentTimeMillis()));

	}

	private void doUpdateDocStatus(ETSDoc doc, String msg) {
		try {
			if (userRole.equals(Defines.ETS_EXECUTIVE)
				|| userRole.equals(Defines.WORKSPACE_VISITOR)) {
				printHeader("", "Update document status", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
				writer.println("You are not authorized to perform this action");
				return;
			}

			Vector breadcrumb = new Vector();
			boolean internal = false;
			if ((es.gDECAFTYPE.trim()).equalsIgnoreCase("I")) {
				internal = true;
			}

			ETSProj proj = Project;
			ETSCat parent_cat = null;
			parent_cat = ETSDatabaseManager.getCat(CurrentCatId);
			if (parent_cat == null) {
				//System.out.print("put bad parent cat id message here");
			}

			if (parent_cat != null) {
				breadcrumb = getBreadcrumb(parent_cat);
				String header = getBreadcrumbTrail(breadcrumb);

				printHeader(header, "Update document status", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");

			}
			else {
				//System.out.print("put bad parent cat id message here");
			}

			writer.println(
				"<form action=\"ETSProjectsServlet.wss\" method=\"post\" name=\"updocstForm\" >");
			writer.println(
				"<input type=\"hidden\" name=\"action\" value=\"updatedocstatus2\" />");
			writer.println(
				"<input type=\"hidden\" name=\"proj\" value=\""
					+ proj.getProjectId()
					+ "\" />");
			writer.println(
				"<input type=\"hidden\" name=\"tc\" value=\""
					+ TopCatId
					+ "\" />");
			writer.println(
				"<input type=\"hidden\" name=\"cc\" value=\""
					+ parent_cat.getId()
					+ "\" />");
			writer.println(
				"<input type=\"hidden\" name=\"linkid\" value=\""
					+ linkid
					+ "\" />");
			writer.println(
				"<input type=\"hidden\" name=\"docid\" value=\""
					+ doc.getId()
					+ "\" />");

			if (!((msg.trim()).equals(""))) {
				if (msg.equals("0")) {
					msg = "Error occurred, please try again.";
				}
				writer.println(
					"<table><tr><td><span style=\"color:#ff3333\">"
						+ msg
						+ "</span></td></tr></table>");
			}

			printTopDocPart(doc);

			writer.println(
				"<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			writer.println(
				"<tr><td><span class=\"ast\"><b>*</b></span></td><td><span class=\"small\"> Denotes required fields</span></td>");
			writer.println("</tr></table>");

			if (doc.getDocStatus() != Defines.DOC_SUB_APP) {
				writer.println(
					"<table width=\"443\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
				writer.println(
					"<tr><td rowspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"20\" height=\"1\" alt=\"\" /></td>");
				writer.println(
					"<td>This status for this document has been updated to "
						+ doc.getDocStatusString()
						+ "</td>");
				writer.println("</tr>");
				writer.println(
					"<tr><td><a href=\"ETSProjectsServlet.wss?proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ linkid
						+ "\"><img src=\"" + Defines.ICON_ROOT + "bk.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"back\" />Back to '"
						+ doc.getName()
						+ "'</a></td></tr>");
				writer.println("</table>");
				return;
			}

			writer.println(
				"<table width=\"443\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			writer.println(
				"<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"20\" height=\"1\" alt=\"\" /></td>");
			writer.println("<td>");

			writer.println(
				"<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			writer.println(
				"<tr><td colspan=\"3\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");

			//status
			writer.println(
				"<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
			writer.println(
				"<td align=\"left\" width=\"15%\"><b>Status:</b></td>");
			writer.println(
				"<td align=\"left\"><input type=\"radio\" id=\"status1\" name=\"status\" value=\""
					+ Defines.DOC_APPROVED
					+ "\" checked=\"checked\" /><label for=\"status1\">Approve</label></td></tr>");
			writer.println(
				"<tr><td colspan=\"2\">&nbsp;</td><td align=\"left\"><input type=\"radio\" id=\"status2\" name=\"status\" value=\""
					+ Defines.DOC_REJECTED
					+ "\" /><label for=\"status\"><label for=\"status2\">Reject</label></td></tr>");

			//comments
			writer.println(
				"<tr><td colspan=\"3\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"15\" alt=\"\" /></td></tr>");
			writer.println(
				"<tr><td align=\"left\" colspan=\"3\"><label for=\"comm\"><b>Comments</b> (max. 1024 chars.)</label></td></tr>");
			writer.println(
				"<tr><td colspan=\"3\"><textarea id=\"comm\" name=\"comm\" cols=\"25\" rows=\"3\" style=\"width:300px\" width=\"300px\" wrap=\"soft\" value=\"\" /></textarea></td></tr>");

			writer.println(
				"<tr><td colspan=\"3\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"15\" alt=\"\" /></td></tr>");
			writer.println(
				"<tr><td align=\"left\" colspan=\"3\" class=\"small\"><span style=\"color:#ff3333\"><b>Warning:</b></span> If you approve this document, it will "
					+ "automatically be accessible to all authorized workspace members.</td></tr>");
			writer.println(
				"<tr><td colspan=\"3\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"15\" alt=\"\" /></td></tr>");

			writer.println("</table>");

			writer.println(
				"<input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"add document\" /> &nbsp; &nbsp;");
			writer.println(
				"<a href=\"ETSProjectsServlet.wss?proj="
					+ Project.getProjectId()
					+ "&tc="
					+ TopCatId
					+ "&cc="
					+ CurrentCatId
					+ "&linkid="
					+ linkid
					+ "\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" />Cancel</a>");
			writer.println("</td></tr></table>");

		}
		catch (Exception e) {
			writer.println("error occurred");
			e.printStackTrace();
			//System.out.println("error here=" + e);
		}
	}

	private void doUpdateStatus2(ETSDoc doc, String status, String comm) {
		try {
			if (userRole.equals(Defines.ETS_EXECUTIVE)
				|| userRole.equals(Defines.WORKSPACE_VISITOR)) {
				printHeader("", "Update document status", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
				writer.println("You are not authorized to perform this action");
				return;
			}

			Vector breadcrumb = new Vector();

			ETSProj proj = Project;
			ETSCat parent_cat = ETSDatabaseManager.getCat(CurrentCatId);

			if (parent_cat != null) {

				boolean success =
					ETSDatabaseManager.updateDocStatus(
						doc.getId(),
						Project.getProjectId(),
						status,
						comm,
						es.gIR_USERN);

				breadcrumb = getBreadcrumb(parent_cat);
				String header = getBreadcrumbTrail(breadcrumb);
				printHeader(header, "Update document status", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");

				writer.println(
					"<img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /><br />");

				writer.println(
					"You have succssfully "
						+ (doc.getDocStatusString()).toLowerCase()
						+ " the document: <b>"
						+ doc.getName()
						+ "</b> <br />");
				if (status.equals(String.valueOf(Defines.DOC_APPROVED))) {
					writer.println(
						"<img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /><br />");
					writer.println(
						"This document is now accessible to all authorized workspace members.<br />");
				}

				writer.println(
					"<img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /><br />");
				writer.println(
					"The author of this document will be notified of this update.<br />");
				writer.println(
					"<img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /><br />");

				writer.println(
					"<a href=\"ETSProjectsServlet.wss?action=details&proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ parent_cat.getId()
						+ "&docid="
						+ doc.getId()
						+ "&linkid="
						+ linkid
						+ "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"continue\" /></a>");

			}
			else {
				writer.println("error occurred: invalid cat id for this user.");
				//System.out.print("put bad parent cat id message here");
			}
		}
		catch (Exception e) {
			//System.out.println("error here");
			e.printStackTrace();
		}
	}

	private void doPublishDoc(ETSDoc doc, String msg) {  //not used yet
		try {
			if (!(doc.getUserId().equals(es.gIR_USERN)
				|| userRole == Defines.WORKSPACE_OWNER
				|| userRole == Defines.WORKSPACE_MANAGER)) {
				printHeader("", "Publish document", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
				writer.println("You are not authorized to perform this action");
				return;
			}

			Vector breadcrumb = new Vector();
			boolean internal = false;
			if ((es.gDECAFTYPE.trim()).equalsIgnoreCase("I")) {
				internal = true;
			}

			ETSProj proj = Project;
			ETSCat parent_cat = null;
			parent_cat = ETSDatabaseManager.getCat(CurrentCatId);
			if (parent_cat == null) {
				//System.out.print("put bad parent cat id message here");
			}

			if (parent_cat != null) {
				breadcrumb = getBreadcrumb(parent_cat);
				String header = getBreadcrumbTrail(breadcrumb);

				printHeader(header, "Publish document", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
			}
			else {
				//System.out.print("put bad parent cat id message here");
			}

			writer.println(
				"<form action=\"ETSProjectsServlet.wss\" method=\"post\" name=\"pubdocForm\" >");
			writer.println(
				"<input type=\"hidden\" name=\"action\" value=\"publishdoc2\" />");
			writer.println(
				"<input type=\"hidden\" name=\"proj\" value=\""
					+ proj.getProjectId()
					+ "\" />");
			writer.println(
				"<input type=\"hidden\" name=\"tc\" value=\""
					+ TopCatId
					+ "\" />");
			writer.println(
				"<input type=\"hidden\" name=\"cc\" value=\""
					+ parent_cat.getId()
					+ "\" />");
			writer.println(
				"<input type=\"hidden\" name=\"linkid\" value=\""
					+ linkid
					+ "\" />");
			writer.println(
				"<input type=\"hidden\" name=\"docid\" value=\""
					+ doc.getId()
					+ "\" />");

			if (!((msg.trim()).equals(""))) {
				if (msg.equals("0")) {
					msg = "Error occurred, please try again.";
				}
				writer.println(
					"<table><tr><td><span style=\"color:#ff3333\">"
						+ msg
						+ "</span></td></tr></table>");
			}

			printTopDocPart(doc);

			writer.println(
				"<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			writer.println("<tr><td colspan=\"2\">");
			writer.println(
				"You are about to publish the document <b>'"
					+ doc.getName()
					+ "'</b>.<br />");
			writer.println(
				"By publishing this document,it will be accessible to all authorized workspace members.<br />");
			writer.println("</td></tr>");

			writer.println(
				"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"20\" alt=\"\" /></td></tr>");

			Vector users =
				ETSDatabaseManager.getProjMembers(Project.getProjectId(), true);
			if (parent_cat.isIbmOnlyOrConf()) {
				users = getIBMMembers(users, conn);
			}

			writer.println("<tr><td align=\"left\" colspan=\"2\"><label for=\"notify\"><b>Notify following team members:</b></label></td></tr>");

			writer.println(
				"<tr><td align=\"left\" colspan=\"2\" class=\"small\">[For selecting multiple values use Ctrl + Mouse click together.]</td></tr>");

			writer.println(
				"<tr><td colspan=\"2\"><table width=\"443\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");

			writer.println(
				"<select id=\"notify\" name=\"notify\" multiple=\"multiple\" size=\"10\" style=\"width:320px\" width=\"320px\">");
			for (int u = 0; u < users.size(); u++) {
				ETSUser user = (ETSUser) users.elementAt(u);
				String username = ETSUtils.getUsersName(conn, user.getUserId());
				writer.println(
					"<option value=\""
						+ user.getUserId()
						+ "\">"
						+ username
						+ " ["
						+ user.getUserId()
						+ "]</option>");

			}
			writer.println("</select>");
			writer.println("</td></tr>");

			if (internal && !parent_cat.isIbmOnlyOrConf()) {
				writer.println("<tr><td colspan=\"2\" class=\"small\">[Security classification and additional access restrictions will be applied to the notification list]</td></tr>");
			}
			else if (!internal && !parent_cat.isIbmOnlyOrConf()) {
				writer.println("<tr><td colspan=\"2\" class=\"small\">[User access restrictions will be applied to the notification list]</td></tr>");
			}

			writer.println("</table>");

			writer.println("</td></tr>");
			writer.println("</table>");

			writer.println(
				"<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			writer.println(
				"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"20\" alt=\"\" /></td></tr>");
			writer.println(
				"<tr><td align=\"left\"><input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"publish document\" /> &nbsp; &nbsp;</td>");
			writer.println(
				"<td align=\"left\"><a href=\"ETSProjectsServlet.wss?action=details&proj="
					+ Project.getProjectId()
					+ "&tc="
					+ TopCatId
					+ "&cc="
					+ CurrentCatId
					+ "&docid="
					+ doc.getId()
					+ "&linkid="
					+ linkid
					+ "\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" />Cancel</a></td></tr>");
			writer.println("</table>");

		}
		catch (Exception e) {

		}
	}

	private void doPublishDoc2(ETSDoc doc, String notify) { //not used yet
		try {
			if (!(doc.getUserId().equals(es.gIR_USERN)
				|| userRole == Defines.WORKSPACE_OWNER
				|| userRole == Defines.WORKSPACE_MANAGER)) {
				printHeader("", "Publish document", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
				writer.println("You are not authorized to perform this action");
				return;
			}

			Vector breadcrumb = new Vector();

			ETSProj proj = Project;
			ETSCat parent_cat = ETSDatabaseManager.getCat(CurrentCatId);

			if (parent_cat != null) {
				boolean success =
					ETSDatabaseManager.updateDocPubStatus(
						doc.getId(),
						Project.getProjectId(),
						es.gIR_USERN);

				breadcrumb = getBreadcrumb(parent_cat);
				String header = getBreadcrumbTrail(breadcrumb);
				printHeader(header, "Publish document", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");

				writer.println(
					"<img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /><br />");

				writer.println(
					"You have succssfully published the document: <b>"
						+ doc.getName()
						+ "</b> <br />");

				writer.println(
					"<img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /><br />");

				writer.println(
					"<a href=\"ETSProjectsServlet.wss?action=details&proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ parent_cat.getId()
						+ "&docid="
						+ doc.getId()
						+ "&linkid="
						+ linkid
						+ "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"continue\" /></a>");

			}
			else {
				writer.println("error occurred: invalid cat id for this user.");
				//System.out.print("put bad parent cat id message here");
			}
		}
		catch (Exception e) {
			//System.out.println("error here");
			e.printStackTrace();
		}
	}

	private void doSendAppDoc(ETSDoc doc, String msg) {
		try {
			if (!(doc.getUserId().equals(es.gIR_USERN)
				|| userRole == Defines.WORKSPACE_OWNER
				|| userRole == Defines.WORKSPACE_MANAGER)) {
				printHeader("", "Send document for approval", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
				writer.println("You are not authorized to perform this action");
				return;
			}

			Vector breadcrumb = new Vector();
			boolean internal = false;
			if ((es.gDECAFTYPE.trim()).equalsIgnoreCase("I")) {
				internal = true;
			}

			ETSProj proj = Project;
			ETSCat parent_cat = null;
			parent_cat = ETSDatabaseManager.getCat(CurrentCatId);
			if (parent_cat == null) {
				//System.out.print("put bad parent cat id message here");
			}

			if (parent_cat != null) {
				breadcrumb = getBreadcrumb(parent_cat);
				String header = getBreadcrumbTrail(breadcrumb);

				printHeader(header, "Send document for approval", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
			}
			else {
				//System.out.print("put bad parent cat id message here");
			}

			writer.println(
				"<form action=\"ETSProjectsServlet.wss\" method=\"post\" name=\"pubdocForm\" >");
			writer.println(
				"<input type=\"hidden\" name=\"action\" value=\"sendappdoc2\" />");
			writer.println(
				"<input type=\"hidden\" name=\"proj\" value=\""
					+ proj.getProjectId()
					+ "\" />");
			writer.println(
				"<input type=\"hidden\" name=\"tc\" value=\""
					+ TopCatId
					+ "\" />");
			writer.println(
				"<input type=\"hidden\" name=\"cc\" value=\""
					+ parent_cat.getId()
					+ "\" />");
			writer.println(
				"<input type=\"hidden\" name=\"linkid\" value=\""
					+ linkid
					+ "\" />");
			writer.println(
				"<input type=\"hidden\" name=\"docid\" value=\""
					+ doc.getId()
					+ "\" />");

			if (!((msg.trim()).equals(""))) {
				if (msg.equals("0")) {
					msg = "Error occurred, please try again.";
				}
				writer.println(
					"<table><tr><td><span style=\"color:#ff3333\">"
						+ msg
						+ "</span></td></tr></table>");
			}

			printTopDocPart(doc);

			writer.println(
				"<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			writer.println("<tr><td colspan=\"2\">");
			writer.println(
				"You are about to send the document <b>'"
					+ doc.getName()
					+ "'</b> for approval.<br />");
			writer.println(
				"Once this document is approved,it will be automatically published and made accessible to "
					+ "all authorized workspace members.<br />");
			writer.println("</td></tr>");

			writer.println(
				"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"20\" alt=\"\" /></td></tr>");

			Vector users =
				ETSDatabaseManager.getProjMembers(Project.getProjectId(), true);
			if (parent_cat.isIbmOnlyOrConf()) {
				users = getIBMMembers(users, conn);
			}

			writer.println(
				"<tr><td align=\"left\" colspan=\"2\"><label for=\"notify\"><b>Approver:</b></label></td></tr>");
			writer.println(
				"<tr><td colspan=\"2\"><table width=\"443\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");

			writer.println(
				"<select id=\"notify\" name=\"notify\" style=\"width:320px\" width=\"320px\">");
			for (int u = 0; u < users.size(); u++) {
				ETSUser user = (ETSUser) users.elementAt(u);
				String username = ETSUtils.getUsersName(conn, user.getUserId());
				writer.println(
					"<option value=\""
						+ user.getUserId()
						+ "\">"
						+ username
						+ " ["
						+ user.getUserId()
						+ "]</option>");

			}
			writer.println("</select>");
			writer.println("</td></tr>");
			writer.println("</table>");

			writer.println("</td></tr>");
			writer.println("</table>");

			writer.println(
				"<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			writer.println(
				"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"20\" alt=\"\" /></td></tr>");
			writer.println(
				"<tr><td align=\"left\"><input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"publish document\" /> &nbsp; &nbsp;</td>");
			writer.println(
				"<td align=\"left\"><a href=\"ETSProjectsServlet.wss?action=details&proj="
					+ Project.getProjectId()
					+ "&tc="
					+ TopCatId
					+ "&cc="
					+ CurrentCatId
					+ "&docid="
					+ doc.getId()
					+ "&linkid="
					+ linkid
					+ "\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" />Cancel</a></td></tr>");
			writer.println("</table>");

		}
		catch (Exception e) {

		}
	}

	private void doSendAppDoc2(ETSDoc doc, String notify) {
		try {
			if (!(doc.getUserId().equals(es.gIR_USERN)
				|| userRole == Defines.WORKSPACE_OWNER
				|| userRole == Defines.WORKSPACE_MANAGER)) {
				printHeader("", "Send document for approval", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
				writer.println("You are not authorized to perform this action");
				return;
			}

			Vector breadcrumb = new Vector();

			ETSProj proj = Project;
			ETSCat parent_cat = ETSDatabaseManager.getCat(CurrentCatId);

			if (parent_cat != null) {

				boolean success =
					ETSDatabaseManager.updateDocStatus(
						doc.getId(),
						Project.getProjectId(),
						String.valueOf(Defines.DOC_SUB_APP),
						"",
						es.gIR_USERN);

				breadcrumb = getBreadcrumb(parent_cat);
				String header = getBreadcrumbTrail(breadcrumb);
				printHeader(header, "Send document for approval", false);
				writer.println(
					"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");

				writer.println(
					"<img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /><br />");

				writer.println(
					"You have succssfully submitted the document: <b>"
						+ doc.getName()
						+ "</b> for approval.<br />");

				writer.println(
					"<img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /><br />");

				writer.println(
					"<a href=\"ETSProjectsServlet.wss?action=details&proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ parent_cat.getId()
						+ "&docid="
						+ doc.getId()
						+ "&linkid="
						+ linkid
						+ "\"><img src=\"" + Defines.BUTTON_ROOT + "continue.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"continue\" /></a>");

			}
			else {
				writer.println("error occurred: invalid cat id for this user.");
				//System.out.print("put bad parent cat id message here");
			}
		}
		catch (Exception e) {
			//System.out.println("error here");
			e.printStackTrace();
		}
	}

	private void doProjectReport(String sortby, String ad) {
		try {
			StringBuffer buf = new StringBuffer();
			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

			printHeader("", "Document access report", false);
			writer.println(
				"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
			writer.println("<tr><td>");

			if ((!ETSDatabaseManager
				.isProjectAdmin(es.gIR_USERN, Project.getProjectId()))
				&& !isSuperAdmin) {
				writer.println("You are not allowed to access this function");
				return;
			}

			Vector sorteddocs =
				ETSDatabaseManager.getAllDocMetrics(
					Project.getProjectId(),
					sortby,
					ad);

			boolean gray_flag = true;
			boolean child_flag = false;
			boolean owns_a_cat = false;

			int width_name = 250;
			int width_mod = 150;
			int width_size = 100;
			int width_hits = 100;

			boolean internal = false;
			boolean ibmonlyFlag = false;
			boolean ibmconfFlag = false;
			boolean exDocFlag = false;

			ETSCat curr_cat = this_current_cat;

			if ((es.gDECAFTYPE.trim()).equalsIgnoreCase("I")) {
				internal = true;
			}

			if (curr_cat.isIbmOnlyOrConf() && !internal) {
				buf.append("You are not authorized to view this folder.");
				return;
			}

			buf.append(
				"<table  cellpadding=\"2\" cellspacing=\"0\" width=\"600\" border=\"0\">");
			buf.append(
				"<tr><td colspan=\"5\"><img src=\"//www.ibm.com/i/c.gif\" height=\"16\" width=\"1\" alt=\"\" /></td></tr>");

			buf.append(
				"<tr><td colspan=\"5\" class=\"small\">Click on the column heading to sort</td></tr>\n");
			buf.append(
				"<tr><td colspan=\"5\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");

			buf.append(
				"<tr><th id=\"list_name\" colspan=\"2\" align=\"left\" valign=\"middle\" height=\"16\">");
			//sort by name
			if (sortby.equals(Defines.SORT_BY_NAME_STR)) {
				if (ad.equals(Defines.SORT_ASC_STR)) {
					buf.append(
						"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=report&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&linkid="
							+ linkid
							+ "&sort_by="
							+ Defines.SORT_BY_NAME_STR
							+ "&sort="
							+ Defines.SORT_DES_STR
							+ "\" class=\"fbox\">");
					buf.append(
						"Name</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
					buf.append("</table></th>");
				}
				else {
					buf.append(
						"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=report&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&linkid="
							+ linkid
							+ "&sort_by="
							+ Defines.SORT_BY_NAME_STR
							+ "&sort="
							+ Defines.SORT_ASC_STR
							+ "\">");
					buf.append(
						"Name</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");
					buf.append("</table></th>");
				}
			}
			else {
				buf.append(
					"<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=report&proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ linkid
						+ "&sort_by="
						+ Defines.SORT_BY_NAME_STR
						+ "&sort="
						+ Defines.SORT_ASC_STR
						+ "\">");
				buf.append("Name</a></th>");
			}

			//sort by date
			buf.append(
				"<th id=\"list_date\" align=\"left\" valign=\"middle\">");
			if (sortby.equals(Defines.SORT_BY_DATE_STR)) {
				if (ad.equals(Defines.SORT_ASC_STR)) {
					buf.append(
						"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=report&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&linkid="
							+ linkid
							+ "&sort_by="
							+ Defines.SORT_BY_DATE_STR
							+ "&sort="
							+ Defines.SORT_DES_STR
							+ "\" class=\"fbox\">");
					buf.append(
						"Modified</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
					buf.append("</table></th>");
				}
				else {
					buf.append(
						"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=report&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&linkid="
							+ linkid
							+ "&sort_by="
							+ Defines.SORT_BY_DATE_STR
							+ "&sort="
							+ Defines.SORT_ASC_STR
							+ "\" class=\"fbox\">");
					buf.append(
						"Modified</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");
					buf.append("</table></th>");
				}
			}
			else {
				buf.append(
					"<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=report&proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ linkid
						+ "&sort_by="
						+ Defines.SORT_BY_DATE_STR
						+ "&sort="
						+ Defines.SORT_ASC_STR
						+ "\" class=\"fbox\">");
				buf.append("Modified</a></th>");
			}

			//sort by size
			buf.append(
				"<th id=\"list_type\" align=\"left\" valign=\"middle\">");
			if (sortby.equals(Defines.SORT_BY_SIZE_STR)) {
				if (ad.equals(Defines.SORT_ASC_STR)) {
					buf.append(
						"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=report&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&linkid="
							+ linkid
							+ "&sort_by="
							+ Defines.SORT_BY_SIZE_STR
							+ "&sort="
							+ Defines.SORT_DES_STR
							+ "\" class=\"fbox\">");
					buf.append(
						"Size</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
					buf.append("</table></th>");
				}
				else {
					buf.append(
						"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=report&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&linkid="
							+ linkid
							+ "&sort_by="
							+ Defines.SORT_BY_SIZE_STR
							+ "&sort="
							+ Defines.SORT_ASC_STR
							+ "\" class=\"fbox\">");
					buf.append(
						"Size</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");
					buf.append("</table></th>");
				}
			}
			else {
				buf.append(
					"<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=report&proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ linkid
						+ "&sort_by="
						+ Defines.SORT_BY_SIZE_STR
						+ "&sort="
						+ Defines.SORT_ASC_STR
						+ "\" class=\"fbox\">");
				buf.append("Size</a></th>");
			}

			//sort by hits
			buf.append(
				"<th id=\"list_type\" align=\"left\" valign=\"middle\">");
			if (sortby.equals(Defines.SORT_BY_HITS_STR)) {
				if (ad.equals(Defines.SORT_ASC_STR)) {
					buf.append(
						"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=report&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&linkid="
							+ linkid
							+ "&sort_by="
							+ Defines.SORT_BY_HITS_STR
							+ "&sort="
							+ Defines.SORT_DES_STR
							+ "\" class=\"fbox\">");
					buf.append(
						"# Hits</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
					buf.append("</table></th>");
				}
				else {
					buf.append(
						"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=report&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&linkid="
							+ linkid
							+ "&sort_by="
							+ Defines.SORT_BY_HITS_STR
							+ "&sort="
							+ Defines.SORT_ASC_STR
							+ "\" class=\"fbox\">");
					buf.append(
						"# Hits</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");
					buf.append("</table></th>");
				}
			}
			else {
				buf.append(
					"<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=report&proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ linkid
						+ "&sort_by="
						+ Defines.SORT_BY_HITS_STR
						+ "&sort="
						+ Defines.SORT_ASC_STR
						+ "\" class=\"fbox\">");
				buf.append("# Hits</a></th>");
			}

			buf.append("</tr>");

			if (sorteddocs != null) {
				for (int i = 0; i < sorteddocs.size(); i++) {
					String exStr = "";

					ETSDoc doc = (ETSDoc) sorteddocs.elementAt(i);
					if ((doc.isIbmOnlyOrConf() && internal)
						|| (!doc.isIbmOnlyOrConf())) {
						if ((!doc.hasExpired())
							|| userRole.equals(Defines.WORKSPACE_OWNER)
							|| isSuperAdmin) {
							child_flag = true;

							if (gray_flag) {
								buf.append(
									"<tr style=\"background-color:#eeeeee\">");
								gray_flag = false;
							}
							else {
								buf.append("<tr>");
								gray_flag = true;
							}

							if (doc.hasExpired()) {
								exDocFlag = true;
								exStr =
									"<span class=\"small\"><span class=\"ast\"><b>&#8224;</b></span></span>";
							}

							buf.append(
								"<td width=\"16\" height=\"17\" align=\"left\" valign=\"top\"><img src=\""
									+ Defines.SERVLET_PATH
									+ "ETSImageServlet.wss?proj=ETS_DOC_IMG&mod=0\" width=\"12\" height=\"12\" alt=\"document\" /></td>");
							//img

							if (doc.getIbmOnly() == Defines.ETS_IBM_CONF) {
								ibmconfFlag = true;
								buf.append(
									"<td headers=\"list_name\" height=\"17\" width=\""
										+ width_name
										+ "\" align=\"left\" valign=\"top\">"
										+ doc.getName()
										+ "<span class=\"ast\">**</span>"
										+ exStr
										+ "</td>");
								//filename
							}
							else if (
								doc.getIbmOnly() == Defines.ETS_IBM_ONLY) {
								ibmonlyFlag = true;
								buf.append(
									"<td headers=\"list_name\" height=\"17\" width=\""
										+ width_name
										+ "\" align=\"left\" valign=\"top\">"
										+ doc.getName()
										+ "<span class=\"ast\">*</span>"
										+ exStr
										+ "</td>");
								//filename
							}
							else {
								buf.append(
									"<td headers=\"list_name\" height=\"17\" width=\""
										+ width_name
										+ "\" align=\"left\" valign=\"top\">"
										+ doc.getName()
										+ exStr
										+ "</td>");
								//filename
							}
							java.util.Date date =
								new java.util.Date(doc.getUploadDate());
							buf.append(
								"<td headers=\"list_date\" height=\"17\" width=\""
									+ width_mod
									+ "\" align=\"left\" valign=\"top\">"
									+ df.format(date)
									+ "</td>");
							//date
							buf.append(
								"<td headers=\"list_type\" height=\"17\" width=\""
									+ width_size
									+ "\" align=\"left\" class=\"small\" valign=\"top\">"
									+ doc.getSize()
									+ "</td>");
							//size
							//buf.append("<td headers=\"list_author\" height=\"17\" width=\""+width_author+"\" class=\"small\" align=\"left\" valign=\"top\">"+ETSUtils.getUsersName(conn,doc.getUserId())+"</td>"); //author
							if (doc.getDocHits() > 0)
								buf.append(
									"<td headers=\"list_hits\" height=\"17\" width=\""
										+ width_hits
										+ "\" align=\"left\" class=\"small\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?action=docreport&proj="
										+ Project.getProjectId()
										+ "&tc="
										+ TopCatId
										+ "&cc="
										+ CurrentCatId
										+ "&docid="
										+ doc.getId()
										+ "&linkid="
										+ linkid
										+ "\" class=\"fbox\">"
										+ doc.getDocHits()
										+ "</a></td>");
							//hits
							else
								buf.append(
									"<td headers=\"list_hits\" height=\"17\" width=\""
										+ width_hits
										+ "\" align=\"left\" class=\"small\" valign=\"top\">"
										+ doc.getDocHits()
										+ "</td>");
							//hits
							buf.append("</tr>");
						}
					}
				}
			}
			else {
				buf.append(
					"<tr><td colspan=\"5\">No documents available</td></tr>");
			}

			if (ibmonlyFlag) {
				buf.append(
					"<tr><td colspan=\"5\"><img src=\"//www.ibm.com/i/c.gif\" height=\"4\" width=\"1\" alt=\"\" /></td></tr>");
				buf.append(
					"<tr><td colspan=\"5\" class=\"small\" valign=\"bottom\"><span class=\"ast\">*</span>Denotes IBM Only folder/document</td></tr>");
			}
			if (ibmconfFlag) {
				buf.append(
					"<tr><td colspan=\"5\"><img src=\"//www.ibm.com/i/c.gif\" height=\"4\" width=\"1\" alt=\"\" /></td></tr>");
				buf.append(
					"<tr><td colspan=\"5\" class=\"small\" valign=\"bottom\"><span class=\"ast\">**</span>Denotes permanent IBM Only folder/document</td></tr>");
			}
			if (exDocFlag) {
				buf.append(
					"<tr><td colspan=\"6\"><img src=\"//www.ibm.com/i/c.gif\" height=\"4\" width=\"1\" alt=\"\" /></td></tr>");
				buf.append(
					"<tr><td colspan=\"6\" class=\"small\" valign=\"bottom\"><span class=\"ast\"><b>&#8224;</b></span>Denotes expired document</td></tr>");
			}

			buf.append("</table>");

			buf.append(
				"<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			buf.append(
				"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>");
			buf.append("<tr>");
			buf.append(
				"<td width=\"16\" height=\"21\" valign=\"top\"><a href=\""
					+ Defines.SERVLET_PATH
					+ "ETSProjectsServlet.wss?proj="
					+ Project.getProjectId()
					+ "&tc="
					+ TopCatId
					+ "&cc="
					+ CurrentCatId
					+ "&linkid="
					+ linkid
					+ "\" ><img src=\""
					+ Defines.ICON_ROOT
					+ "bk.gif\" width=\"16\" height=\"16\" alt=\"Back\" border=\"0\" /></a></td>");
			buf.append(
				"<td align=\"left\" valign=\"top\"><a href=\""
					+ Defines.SERVLET_PATH
					+ "ETSProjectsServlet.wss?proj="
					+ Project.getProjectId()
					+ "&tc="
					+ TopCatId
					+ "&cc="
					+ CurrentCatId
					+ "&linkid="
					+ linkid
					+ "\"  class=\"fbox\">Back to '"
					+ curr_cat.getName()
					+ "'</a></td>");
			buf.append("</tr>");
			buf.append("</table>");

			writer.println(buf.toString());
		}
		catch (SQLException se) {
			//System.out.println("sql error in docman print children= " + se);
			se.printStackTrace();
		}
		catch (Exception e) {
			//System.out.println("except error in docman print children= " + e);
			e.printStackTrace();
		}

	}

	private void doDocProjectReport(
		String docidStr,
		String sortby,
		String ad) {
		try {
			StringBuffer buf = new StringBuffer();
			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");

			int docid = (new Integer(docidStr)).intValue();

			printHeader("", "Document access report", false);
			writer.println(
				"<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
			writer.println("<tr><td>");

			ETSDoc doc =
				ETSDatabaseManager.getDocByIdAndProject(
					docid,
					Project.getProjectId());

			Vector users =
				ETSDatabaseManager.getDocMetrics(
					docid,
					Project.getProjectId(),
					sortby,
					ad);
			if (sortby.equals(Defines.SORT_BY_AUTH_STR)) {
				byte sortOrder = ETSComparator.getSortOrder(sortby);
				byte sortAD = ETSComparator.getSortBy(ad);
				Collections.sort(users, new ETSComparator(sortOrder, sortAD));
			}

			if (doc == null) {
				writer.println("invalid document");
				return;
			}

			if (!(ETSDatabaseManager
				.isProjectAdmin(es.gIR_USERN, Project.getProjectId())
				|| doc.getUserId().equals(es.gIR_USERN)
				|| isSuperAdmin)) {
				writer.println("You are not allowed to access this function");
				return;
			}
			if (doc.hasExpired()
				&& (!(doc.getUserId().equals(es.gIR_USERN)
					|| userRole.equals(Defines.WORKSPACE_OWNER)
					|| isSuperAdmin))) {
				writer.println("You are not allowed to access this function");
				return;
			}

			boolean gray_flag = true;

			int width_name = 350;
			int width_date = 250;

			boolean internal = false;

			ETSCat curr_cat = this_current_cat;

			if ((es.gDECAFTYPE.trim()).equalsIgnoreCase("I")) {
				internal = true;
			}

			if (curr_cat.isIbmOnlyOrConf() && !internal) {
				buf.append("You are not authorized to view this folder.");
				return;
			}

			buf.append(
				"<table cellpadding=\"0\" cellspacing=\"1\" width=\"600\" border=\"0\">");
			if (!doc.isLatestVersion()) {
				buf.append(
					"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>");
				buf.append(
					"<tr><td colspan=\"2\" align=\"left\"><span style=\"color:#ff3333\">This is not the most recent version of this document.</span></td></tr>");
			}

			buf.append(
				"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>");
			buf.append(
				"<tr><td colspan=\"2\" align=\"left\"><b>Document details</b></td></tr>");
			buf.append(
				"<tr><td colspan=\"2\" align=\"left\"><img src=\"//www.ibm.com/i/c.gif\" height=\"7\" width=\"1\" alt=\"\" /></td></tr>");

			buf.append(
				"<tr><td width=\"25%\" valign=\"top\" nowrap=\"nowrap\" class=\"small\">Document name:&nbsp;</td><td align=\"left\" valign=\"top\" width=\"75%\" class=\"small\">"
					+ doc.getName()
					+ "</td></tr>");
			if (!doc.getDescription().equals("")) {
				buf.append(
					"<tr><td width=\"25%\" valign=\"top\" class=\"small\">Description:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\">"
						+ doc.getDescription()
						+ "</td></tr>");
			}
			else {
				buf.append(
					"<tr><td width=\"25%\" valign=\"top\" class=\"small\">Description:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\">&nbsp;</td></tr>");
			}

			if (!doc.getKeywords().equals("")) {
				buf.append(
					"<tr><td width=\"25%\" valign=\"top\" class=\"small\">Keywords:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\">"
						+ doc.getKeywords()
						+ "</td></tr>");
			}
			else {
				buf.append(
					"<tr><td width=\"25%\" valign=\"top\" class=\"small\">Keywords:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\">&nbsp;</td></tr>");
			}
			String author = "";

			try {
				author = ETSUtils.getUsersName(conn, doc.getUserId());
			}
			catch (Exception e) {
				author = doc.getUserId();
			}

			buf.append(
				"<tr><td width=\"25%\" valign=\"top\" class=\"small\">Author:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\">"
					+ author
					+ "</td></tr>");
			//DateFormat df1 = DateFormat.getDateInstance(DateFormat.MEDIUM);
			java.util.Date date = new java.util.Date(doc.getUploadDate());
			String dateStr = df.format(date);
			buf.append(
				"<tr><td width=\"25%\" valign=\"top\" class=\"small\">File date:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\">"
					+ dateStr
					+ "</td></tr>");
			if (doc.getUpdateDate() != doc.getUploadDate()) {
				String updater = "";
				try {
					updater = ETSUtils.getUsersName(conn, doc.getUpdatedBy());
				}
				catch (Exception e) {
					updater = doc.getUpdatedBy();
				}

				if (doc.getUpdateDate() != doc.getUploadDate()) {
					date = new java.util.Date(doc.getUpdateDate());
					dateStr = df.format(date);
					buf.append(
						"<tr><td width=\"25%\" valign=\"top\" class=\"small\">Properties Updated:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\">"
							+ dateStr
							+ "</td></tr>");
				}
				buf.append(
					"<tr><td width=\"25%\" valign=\"top\" class=\"small\">Last updated by:&nbsp;</td><td width=\"75%\" valign=\"top\" align=\"left\" class=\"small\">"
						+ updater
						+ "</td></tr>");

			}

			if (doc.getExpiryDate() != 0) {
				java.util.Date exDate = new java.util.Date(doc.getExpiryDate());
				String exDateStr = df.format(exDate);
				buf.append(
					"<tr><td width=\"25%\" valign=\"top\" class=\"small\">Expire date:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\">"
						+ exDateStr);
				if (doc.hasExpired())
					buf.append(
						"&nbsp; <span style=\"color:#ff3333\"><b>EXPIRED</b></span>");
				buf.append("</td></tr>");
			}

			buf.append(
				"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"600\" alt=\"\" /></td></tr>");
			buf.append(
				"<tr><td width=\"25%\" valign=\"top\" class=\"small\"># Hits:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\">"
					+ doc.getDocHits()
					+ "</td></tr>");

			if (doc.isIbmOnlyOrConf()) {
				buf.append(
					"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"600\" alt=\"\" /></td></tr>");
				buf.append(
					"<tr><td colspan=\"2\" class=\"small\">This document has access restricted to IBM team members only.</td></tr>");
			}

			buf.append(
				"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>");
			buf.append(
				"<tr><td colspan=\"2\"><img src=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" height=\"1\" width=\"600\" alt=\"\" /></td></tr>");
			buf.append(
				"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>");
			buf.append("</table>");

			buf.append(
				"<table  cellpadding=\"2\" cellspacing=\"0\" width=\"600\" border=\"0\">");
			buf.append(
				"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"16\" width=\"1\" alt=\"\" /></td></tr>");

			buf.append(
				"<tr><td colspan=\"2\" class=\"small\">Click on the column heading to sort</td></tr>\n");
			buf.append(
				"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");

			buf.append("<tr>");

			//sort by userid
			buf.append(
				"<th id=\"list_author\" align=\"left\" valign=\"middle\">");
			if (sortby.equals(Defines.SORT_BY_AUTH_STR)) {
				if (ad.equals(Defines.SORT_ASC_STR)) {
					buf.append(
						"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=docreport&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&docid="
							+ doc.getId()
							+ "&linkid="
							+ linkid
							+ "&sort_by="
							+ Defines.SORT_BY_AUTH_STR
							+ "&sort="
							+ Defines.SORT_DES_STR
							+ "\" class=\"fbox\">");
					buf.append(
						"User</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
					buf.append("</table></th>");
				}
				else {
					buf.append(
						"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=docreport&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&docid="
							+ doc.getId()
							+ "&linkid="
							+ linkid
							+ "&sort_by="
							+ Defines.SORT_BY_AUTH_STR
							+ "&sort="
							+ Defines.SORT_ASC_STR
							+ "\" class=\"fbox\">");
					buf.append(
						"User</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");
					buf.append("</table></th>");
				}
			}
			else {
				buf.append(
					"<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=docreport&proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ CurrentCatId
						+ "&docid="
						+ doc.getId()
						+ "&linkid="
						+ linkid
						+ "&sort_by="
						+ Defines.SORT_BY_AUTH_STR
						+ "&sort="
						+ Defines.SORT_ASC_STR
						+ "\" class=\"fbox\">");
				buf.append("User</a></th>");
			}

			//sort by date
			buf.append(
				"<th id=\"list_date\" align=\"left\" valign=\"middle\">");
			if (sortby.equals(Defines.SORT_BY_DATE_STR)) {
				if (ad.equals(Defines.SORT_ASC_STR)) {
					buf.append(
						"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=docreport&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&docid="
							+ doc.getId()
							+ "&linkid="
							+ linkid
							+ "&sort_by="
							+ Defines.SORT_BY_DATE_STR
							+ "&sort="
							+ Defines.SORT_DES_STR
							+ "\" class=\"fbox\">");
					buf.append(
						"Date</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
					buf.append("</table></th>");
				}
				else {
					buf.append(
						"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=docreport&proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&cc="
							+ CurrentCatId
							+ "&docid="
							+ doc.getId()
							+ "&linkid="
							+ linkid
							+ "&sort_by="
							+ Defines.SORT_BY_DATE_STR
							+ "&sort="
							+ Defines.SORT_ASC_STR
							+ "\" class=\"fbox\">");
					buf.append(
						"Date</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");
					buf.append("</table></th>");
				}
			}
			else {
				buf.append(
					"<a class=\"parent\" href=\"ETSProjectsServlet.wss?action=docreport&proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ CurrentCatId
						+ "&docid="
						+ doc.getId()
						+ "&linkid="
						+ linkid
						+ "&sort_by="
						+ Defines.SORT_BY_DATE_STR
						+ "&sort="
						+ Defines.SORT_ASC_STR
						+ "\" class=\"fbox\">");
				buf.append("Date</a></th>");
			}

			buf.append("</tr>");

			for (int ucnt = 0; ucnt < users.size(); ucnt++) {
				ETSUser u = (ETSUser) users.elementAt(ucnt);

				if (gray_flag) {
					buf.append("<tr style=\"background-color:#eeeeee\">");
					gray_flag = false;
				}
				else {
					buf.append("<tr>");
					gray_flag = true;
				}

				buf.append(
					"<td headers=\"list_name\" height=\"17\" width=\""
						+ width_name
						+ "\" align=\"left\" valign=\"top\">"
						+ ETSUtils.getUsersName(conn, u.getUserId())
						+ "</td>");
				//filename
				SimpleDateFormat df1 =
					new SimpleDateFormat("MM/dd/yyyy hh:mm a");
				java.util.Date tdate = new java.util.Date(u.getLastTimestamp());
				buf.append(
					"<td headers=\"list_date\" height=\"17\" width=\""
						+ width_date
						+ "\" align=\"left\" valign=\"top\">"
						+ df1.format(tdate)
						+ "</td>");
				//date
				buf.append("</tr>");
			}

			buf.append("</table>");

			buf.append(
				"<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">");
			if (TopCatId == CurrentCatId) {
				buf.append(
					"<tr><td colspan=\"4\"><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>");
				buf.append(
					"<td width=\"16\" height=\"21\" valign=\"top\"><a href=\""
						+ Defines.SERVLET_PATH
						+ "ETSProjectsServlet.wss?action=report&proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ linkid
						+ "\" ><img src=\""
						+ Defines.ICON_ROOT
						+ "bk.gif\" width=\"16\" height=\"16\" alt=\"Back\" border=\"0\" /></a></td>");
				buf.append(
					"<td align=\"left\" valign=\"top\"><a href=\""
						+ Defines.SERVLET_PATH
						+ "ETSProjectsServlet.wss?action=report&proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ linkid
						+ "\"  class=\"fbox\">Back to access listing</a></td>");
				buf.append(
					"<td width=\"16\" height=\"21\" valign=\"top\"><a href=\""
						+ Defines.SERVLET_PATH
						+ "ETSProjectsServlet.wss?proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ linkid
						+ "\" ><img src=\""
						+ Defines.ICON_ROOT
						+ "bk.gif\" width=\"16\" height=\"16\" alt=\"Back\" border=\"0\" /></a></td>");
				buf.append(
					"<td align=\"left\" valign=\"top\"><a href=\""
						+ Defines.SERVLET_PATH
						+ "ETSProjectsServlet.wss?proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ CurrentCatId
						+ "&linkid="
						+ linkid
						+ "\"  class=\"fbox\">Back to '"
						+ curr_cat.getName()
						+ "'</a></td>");

			}
			else {
				buf.append(
					"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"20\" width=\"1\" alt=\"\" /></td></tr>");
				buf.append(
					"<td width=\"16\" height=\"21\" valign=\"top\"><a href=\""
						+ Defines.SERVLET_PATH
						+ "ETSProjectsServlet.wss?action=details&proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ CurrentCatId
						+ "&docid="
						+ docid
						+ "&linkid="
						+ linkid
						+ "\" ><img src=\""
						+ Defines.ICON_ROOT
						+ "bk.gif\" width=\"16\" height=\"16\" alt=\"Back\" border=\"0\" /></a></td>");
				buf.append(
					"<td align=\"left\" valign=\"top\"><a href=\""
						+ Defines.SERVLET_PATH
						+ "ETSProjectsServlet.wss?action=details&proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ CurrentCatId
						+ "&docid="
						+ docid
						+ "&linkid="
						+ linkid
						+ "\"  class=\"fbox\">Back to '"
						+ doc.getName()
						+ "'</a></td>");
			}
			buf.append("</tr>");

			buf.append("</table>");
			writer.println(buf.toString());
		}
		catch (SQLException se) {
			//System.out.println("sql error in docman print children= " + se);
			se.printStackTrace();
		}
		catch (Exception e) {
			//System.out.println("except error in docman print children= " + e);
			e.printStackTrace();
		}

	}

	// 8888888888888888888888888888888888888888888888888888888888888888888888
	
	/*
	private String[] composeEmail(
		ETSDoc doc,
		String notify,
		String action,
		String subject) {
		try {
			ETSCat parent_cat;
			Vector members = new Vector();
			if (!notify.equals("")) {
				notify = notify.substring(0, notify.length() - 1);
				System.out.println("snotify" + notify);
				StringTokenizer st = new StringTokenizer(notify, ",");
				while (st.hasMoreTokens()) {
					String uid = st.nextToken();
					members.addElement(uid);
				}
			}

			if (members.size() > 0) {
				SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
				java.util.Date date = new java.util.Date(doc.getUploadDate());
				String dateStr = df.format(date);

				AccessCntrlFuncs acf = new AccessCntrlFuncs();

				String emailids = "";
				StringBuffer message = new StringBuffer();

				message.append("\n\n");
				if (doc.getDocStatus() == Defines.DOC_SUB_APP)
					message.append(
						"A new document was added to the project for your approval: \n");
				else
					message.append(
						"A new document was added to the project: \n");

				message.append(Project.getName() + " \n\n");
				message.append(
					"The details of the document are as follows: \n\n");
				message.append(
					"==============================================================\n");

				message.append(
					"  Name:           "
						+ ETSUtils.formatEmailStr(doc.getName())
						+ "\n");
				message.append(
					"  Description:    "
						+ ETSUtils.formatEmailStr(doc.getDescription())
						+ "\n");
				message.append(
					"  Keywords:       "
						+ ETSUtils.formatEmailStr(doc.getKeywords())
						+ " \n");
				message.append(
					"  Author:         "
						+ ETSUtils.getUsersName(conn, doc.getUserId())
						+ "\n");
				message.append(
					"  Date:           " + dateStr + " (mm/dd/yyyy)\n\n");

				if (doc.getIbmOnly() == Defines.ETS_IBM_ONLY
					|| doc.getIbmOnly() == Defines.ETS_IBM_CONF) {
					message.append("  This document is marked IBM Only\n\n");
				}

				message.append(
					"To view this document, click on the following  URL:  \n");
				String url =
					Global.getUrl("ets/ETSProjectsServlet.wss")
						+ "?action=details&proj="
						+ doc.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ CurrentCatId
						+ "&docid="
						+ doc.getId()
						+ "&linkid="
						+ linkid;

				message.append(url + "\n\n");

				message.append(
					"==============================================================\n");
				message.append("Delivered by E&TS Connect.\n");
				message.append("This is a system generated email. \n");
				message.append(
					"==============================================================\n\n");

				if (doc.getIbmOnly() == Defines.ETS_IBM_CONF
					|| doc.getIbmOnly() == Defines.ETS_IBM_ONLY) {
					members = getIBMMembers(members, conn);
				}

				if (members.size() > 0) {
					for (int i = 0; i < members.size(); i++) {
						String memb = (String) members.elementAt(i);
						try {
							String userEmail =
								ETSUtils.getUserEmail(conn, memb);
							emailids = emailids + userEmail + ",";
						}
						catch (AMTException ae) {
							//writer.println("amt exception caught. e= "+ae);
						}
					}

					//String subject = "E&TS Connect - New Document: "+doc.getName();
					subject = ETSUtils.formatEmailSubject(subject);

					String toList = "";
					toList = emailids;
					//toList = "sandieps@us.ibm.com";
					boolean bSent = false;

					if (!toList.trim().equals("")) {
						bSent =
							ETSUtils.sendEMail(
								es.gEMAIL,
								toList,
								"","", 
								Global.mailHost,
								message.toString(),
								subject,
								es.gEMAIL);
					}

					if (bSent) {
						ETSDatabaseManager.addEmailLog(
							"Document",
							String.valueOf(doc.getId()),
							"Add document",
							es.gIR_USERN,
							Project.getProjectId(),
							subject,
							toList,
							"");
					}
					else {
						System.out.println(
							"Error occurred while notifying project members.");
					}

				}
				else {
					//writer.println("There are no project members to notify. <br />");
				}
			}
			else {
			}

			return new String[] { "0", "success" };
		}
		catch (Exception e) {
			System.out.println("error here " + e);
			return new String[] { "1", "error" };
		}
	}
*/

	private Vector getIBMMembers(Vector membs, Connection conn) {
		Vector new_members = new Vector();

		for (int i = 0; i < membs.size(); i++) {
			ETSUser mem = (ETSUser) membs.elementAt(i);
			try {
				String edge_userid = AccessCntrlFuncs.getEdgeUserId(conn, mem.getUserId());
				String decaftype = AccessCntrlFuncs.decafType(edge_userid, conn);
				if (decaftype.equals("I")) {
					new_members.addElement(mem);
				}
			}
			catch (AMTException a) {
				a.printStackTrace();
				//System.out.println("amt exception in getibmmembers err= " + a);
			}
			catch (SQLException s) {
				s.printStackTrace();
				//System.out.println("sql exception in getibmmembers err= " + s);
			}
		}

		return new_members;
	}

	private String getParameter(HttpServletRequest req, String key) {
		String value = req.getParameter(key);

		if (value == null) {
			return "";
		}
		else {
			return value;
		}
	}

	private void printDocPart1() {
		ETSDoc d = new ETSDoc();
		printDocPart1(d);
	}
	private void printDocPart1(ETSDoc doc) {
		
		String s_name = ETSDocCommon.getSessionString("ETSDocName",request);
		String s_desc = ETSDocCommon.getSessionString("ETSDocDesc",request);
		String s_keywords = ETSDocCommon.getSessionString("ETSDocKeywords",request);

		if (s_name == null){
			s_name = doc.getName();			
			s_desc = doc.getDescription();
			s_keywords = doc.getKeywords();
		}

			//blue header
			writer.println("<tr><td colspan=\"2\" class=\"tdblue\" height=\"18\"> Document details</td></tr>");
			writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"5\" alt=\"\" /></td></tr>");
			
			String strProjectId = Project.getProjectId();
			String docname = "docname";
			String docdesc = "docdesc";
			String keywords = "keywords";
			
			String action = getParameter(request, "action");
			if (Project.isITAR() && action.equals("addmeetingdoc")) {
				docname = "document.name";
				docdesc = "document.description";
				keywords = "document.keywords";
			}

			//name
			writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
			writer.println("<td align=\"left\" width=\"99%\"><label for=\""+ docname +"\"><b>Name:</b></label>"
					+ "<a href=\"ETSContentManagerServlet.wss?OP=Documents&BlurbField=Name&proj="+strProjectId+"\"  "
					+ "onclick=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Name&proj="+strProjectId+"','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=350,height=450');return false\"  "
					+ "onkeypress=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Name&proj="+strProjectId+"','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=350,height=450')\" >"
					+ "<img border=\"0\" name=\"Help\" src=\""
					+ Defines.ICON_ROOT
					+ "/popup.gif\" width=\"16\" height=\"16\"  alt=\"Click for help on this field\" /></a>&nbsp;"
					+ "</td></tr>");
			
			
			writer.println("<tr><td colspan=\"2\"><input type=\"text\" id=\""+ docname +"\" size=\"25\" style=\"width:300px\" width=\"300px\" name=\""+ docname +"\" value=\""
					+ s_name //doc.getName()
					+ "\" /></td></tr>");
			
			//description
			writer.println(
				"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"5\" alt=\"\" /></td></tr>");
			writer.println(
				"<tr><td align=\"left\" colspan=\"2\"><label for=\""+ docdesc +"\"><b>Description</b> (max. 2000 chars.)</label>"
					+ "<a href=\"ETSContentManagerServlet.wss?OP=Documents&BlurbField=Description&proj="+strProjectId+"\"  "
					+ "onclick=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Description&proj="+strProjectId+"','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=350,height=450');return false\"  "
					+ "onkeypress=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Description&proj="+strProjectId+"','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=350,height=450')\" >"
					+ "<img border=\"0\" name=\"Help\" src=\""
					+ Defines.ICON_ROOT
					+ "/popup.gif\" width=\"16\" height=\"16\"  alt=\"Click for help on this field\" /></a>&nbsp;"
					+ "</td></tr>");
			writer.println(
				"<tr><td colspan=\"2\"><textarea id=\""+ docdesc +"\" name=\""+ docdesc +"\" cols=\"25\" rows=\"3\" style=\"width:300px\" width=\"300px\" wrap=\"soft\" value=\"\" />"
					+ s_desc //doc.getDescription()
					+ "</textarea></td></tr>");

			//keywords
			writer.println(
				"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"5\" alt=\"\" /></td></tr>");
			writer.println(
				"<tr><td align=\"left\" colspan=\"2\"><label for=\""+ keywords +"\"><b>Keywords:</b></label>"
					+ "<a href=\"ETSContentManagerServlet.wss?OP=Documents&BlurbField="+ keywords +"&proj="+strProjectId+"\"  "
					+ "onclick=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Keywords&proj="+strProjectId+"','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=350,height=450');return false\"  "
					+ "onkeypress=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField="+ keywords +"&proj="+strProjectId+"','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=350,height=450')\" >"
					+ "<img border=\"0\" name=\"Help\" src=\""
					+ Defines.ICON_ROOT
					+ "/popup.gif\" width=\"16\" height=\"16\"  alt=\"Click for help on this field\" /></a>&nbsp;"
					+ "</td></tr>");
			writer.println(
				"<tr><td colspan=\"2\"><input type=\"text\" id=\""+ keywords +"\" size=\"25\" style=\"width:300px\" width=\"300px\" name=\""+ keywords +"\" value=\""
					+ s_keywords //doc.getKeywords()
					+ "\" /></td></tr>");
	}

	private void printDocPart2(
		String action,
		boolean internal,
		ETSCat parent_cat,
		String option)
		throws Exception {

		//file
		writer.println(
			"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"5\" alt=\"\" /></td></tr>");
		writer.println(
			"<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
		writer.println(
			"<td align=\"left\" width=\"99%\"><label for=\"docfile\"><b>File:</b></label></td></tr>");
		writer.println(
			"<tr><td colspan=\"2\"><input type=\"file\" id=\"docfile\" size=\"25\" style=\"width:300px\" width=\"300px\" name=\"docfile\" value=\"\" /></td></tr>");

		//ibm only
		if (action.equals("adddoc2")) {
			if (internal) {
				writer.println(
					"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");
				writer.println("<tr><td align=\"left\" colspan=\"2\"><label for=\"ibmonly\"><span style=\"color:#ff3333\"><b>Security classification</b></span></label>"
					+ "<a href=\"ETSContentManagerServlet.wss?OP=Documents&BlurbField=Sec\"  "
					+ "onclick=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Security','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400');return false\"  "
					+ "onkeypress=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Security','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400')\" >"
					+ "<img border=\"0\" name=\"Help\" src=\""
					+ Defines.ICON_ROOT+"/popup.gif\" width=\"16\" height=\"16\"  alt=\"Click for help on this field\" /></a>&nbsp;"
					+"</td></tr>");
			}
			else {
				writer.println(
					"<input type=\"hidden\" name=\"ibmonly\" value=\"0\" />");
			}

			if (parent_cat.getIbmOnly() == Defines.ETS_IBM_CONF) {
				writer.println(
					"<tr><td align=\"left\" width=\"1%\">&nbsp;</td>");
				writer.println(
					"<td align=\"left\" width=\"99%\"><b>Access to this document will be limited to IBM team members and can never be changed.</b><br />");
				writer.println(
					"<span class=\"small\">To make it public, create it under a different folder.</span><input type=\"hidden\" name=\"ibmonly\" value=\"2\" /></td></tr>");
			}
			else if (parent_cat.getIbmOnly() == Defines.ETS_IBM_ONLY) {
				writer.println(
					"<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
				writer.println(
					"<td align=\"left\" width=\"99%\"><b>Access limited to:</b></td></tr>");
				writer.println(
					"<tr><td align=\"left\" valign=\"bottom\" colspan=\"2\">");
				writer.println("<select id=\"ibmonly\" name=\"ibmonly\" />");
				writer.println(
					"<option value=\"1\">All IBM team members</option>");
				writer.println(
					"<option value=\"2\">All IBM team members permanently</option>");
				writer.println("</select></td></tr>");
				writer.println(
					"<tr><td class=\"small\" colspan=\"2\">To make accessible to all team members, create it under a different folder or make its parent folder public.</td></tr>");

			}
			else {
				if (internal) {
					writer.println(
						"<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
					writer.println(
						"<td align=\"left\" width=\"99%\"><b>Access limited to:</b></td></tr>");
					writer.println(
						"<tr><td align=\"left\" valign=\"bottom\" colspan=\"2\">");
					writer.println(
						"<select id=\"ibmonly\" name=\"ibmonly\" />");
					writer.println(
						"<option value=\"0\">All team members</option>");
					writer.println(
						"<option value=\"1\">All IBM team members</option>");
					writer.println(
						"<option value=\"2\">All IBM team members permanently</option>");
					writer.println("</select></td></tr>");
				}
			}
		}

		//notify option
		if (option.equals(String.valueOf(Defines.DOC_DRAFT))
			|| option.equals(String.valueOf(Defines.DOC_PUBLISH))) {
			//draft == can only notice owner,manager,self
			writer.println(
				"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");

			Vector users = new Vector();
			if (option.equals(String.valueOf(Defines.DOC_DRAFT))) {
				users =
					ETSDatabaseManager.getUsersByProjectPriv(
						Project.getProjectId(),
						Defines.ADMIN,
						true);
			}
			else {
				users =
					ETSDatabaseManager.getProjMembers(
						Project.getProjectId(),
						true);
			}

			if (parent_cat.isIbmOnlyOrConf()) {
				users = getIBMMembers(users, conn);
			}


			writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"15\" alt=\"\" /></td></tr>");
			writer.println("<tr><td colspan=\"2\" class=\"tdblue\" height=\"18\"> Notification</td></tr>");
			
			writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");
			writer.println("<tr><td colspan=\"2\">");
			writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td><b>E-mail notification option:</b> ");
			writer.println("<a href=\"ETSContentManagerServlet.wss?OP=Documents&BlurbField=Notification%20option\"  "
					+ "onclick=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Notification%20option','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400');return false\"  "
					+ "onkeypress=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Notification%20option','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400')\" >"
					+ "<img border=\"0\" name=\"Help\" src=\""
					+ Defines.ICON_ROOT+"/popup.gif\" width=\"16\" height=\"16\"  alt=\"Click for help on this field\" /></a>&nbsp;"
					+"</td>");
			writer.println("<td><input type=\"radio\" id=\"notifyOption\" name=\"notifyOption\" value=\"to\" checked=\"checked\" /><label for=\"notifyOption\">To</label></td>");
			writer.println("<td><input type=\"radio\" id=\"notifyOption\" name=\"notifyOption\" value=\"bcc\" /><label for=\"notifyOption\">Bcc</label></td>");
			writer.println("</tr></table></td></tr>");
			
		
			writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");
			writer.println("<tr><td align=\"left\" colspan=\"2\"><label for=\"notify\"><b>Notify following team members:</b></label></td></tr>");
			writer.println("<tr><td align=\"left\" colspan=\"2\"><input type=\"checkbox\" name=\"notifyall\" value=\"yes\" id=\"notifyall\" /><label for=\"notifyall\">Notify all</label></td></tr>");
			
			writer.println("<tr><td align=\"left\" colspan=\"2\" class=\"small\">[For selecting multiple values use Ctrl + Mouse click together.]</td></tr>");

			writer.println("<tr><td colspan=\"2\"><table width=\"443\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
			writer.println("<select id=\"notify\" name=\"notify\" multiple=\"multiple\" size=\"10\" style=\"width:320px\" width=\"320px\">");
			for (int u = 0; u < users.size(); u++) {
				ETSUser user = (ETSUser) users.elementAt(u);
				String username = ETSUtils.getUsersName(conn, user.getUserId());
				writer.println(
					"<option value=\""
						+ user.getUserId()
						+ "\">"
						+ username
						+ " ["
						+ user.getUserId()
						+ "]</option>");

			}
			writer.println("</select>");

			if (internal && !parent_cat.isIbmOnlyOrConf()) {
				writer.println("<tr><td colspan=\"2\" class=\"small\">[Security classification and additional access restrictions will be applied to the notification list]</td></tr>");
			}
			else if (!internal && !parent_cat.isIbmOnlyOrConf()) {
				writer.println("<tr><td colspan=\"2\" class=\"small\">[User access restrictions will be applied to the notification list]</td></tr>");
			}



			writer.println("</table>");
			writer.println("</td></tr>");
		}
		else if (option.equals(String.valueOf(Defines.DOC_SUB_APP))) {
			writer.println(
				"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");

			Vector users =
				ETSDatabaseManager.getProjMembers(Project.getProjectId(), true);

			if (parent_cat.isIbmOnlyOrConf()) {
				users = getIBMMembers(users, conn);
			}

			writer.println(
				"<tr><td align=\"left\" colspan=\"2\"><label for=\"notify\"><b>Send to Approver:</b></label></td></tr>");

			writer.println(
				"<tr><td colspan=\"2\"><table width=\"443\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");

			writer.println(
				"<select id=\"notify\" name=\"notify\" style=\"width:320px\" width=\"320px\">");
			for (int u = 0; u < users.size(); u++) {
				ETSUser user = (ETSUser) users.elementAt(u);
				String username = ETSUtils.getUsersName(conn, user.getUserId());
				writer.println(
					"<option value=\""
						+ user.getUserId()
						+ "\">"
						+ username
						+ " ["
						+ user.getUserId()
						+ "]</option>");

			}
			writer.println("</select>");

			if (internal
				&& !parent_cat.isIbmOnlyOrConf()
				&& !option.equals(String.valueOf(Defines.DOC_SUB_APP))) {
				writer.println("<tr><td colspan=\"2\" class=\"small\">[If access is restricted to IBM members, only IBM employees selected will be notified.]</td></tr>");
			}

			writer.println("</table>");
			writer.println("</td></tr>");
		}
		else {
			//writer.println("<input type=\"hidden\" name=\"notify\" value=\"none\" />");
		}

	}

	private void printDocPart2(
		String action,
		boolean internal,
		ETSDoc doc,
		ETSCat parent_cat,
		String option)
		throws Exception {
		//file
		writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"5\" alt=\"\" /></td></tr>");
		writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
		writer.println("<td align=\"left\" width=\"99%\"><label for=\"docfile\"><b>File:</b></label></td></tr>");
		writer.println("<tr><td colspan=\"2\"><input type=\"file\" id=\"docfile\" size=\"25\" style=\"width:300px\" width=\"300px\" name=\"docfile\" value=\"\" /></td>");

		if ((es.gDECAFTYPE.trim()).equalsIgnoreCase("I")) {
			internal = true;
		}
		if (internal) {
			writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");
			writer.println("<tr><td align=\"left\" colspan=\"2\"><label for=\"ibmonly\"><span style=\"color:#ff3333\"><b>Security classification</b></span></label>"
				+ "<a href=\"ETSContentManagerServlet.wss?OP=Documents&BlurbField=Sec\"  "
				+ "onclick=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Security','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400');return false\"  "
				+ "onkeypress=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Security','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400')\" >"
				+ "<img border=\"0\" name=\"Help\" src=\""
				+ Defines.ICON_ROOT+"/popup.gif\" width=\"16\" height=\"16\"  alt=\"Click for help on this field\" /></a>&nbsp;"
				+"</td></tr>");
		}
		else {
			writer.println("<input type=\"hidden\" name=\"ibmonly\" value=\"0\" />");
		}

		if (parent_cat.getIbmOnly() == Defines.ETS_IBM_CONF
			|| doc.getIbmOnly() == '2') {
			writer.println("<tr><td align=\"left\" colspan=\"2\"><b>Access to this document is limited to IBM team members and can never be changed.</b><br />");
			writer.println("<input type=\"hidden\" name=\"ibmonly\" value=\"2\" /></td></tr>");
		}
		else if (parent_cat.getIbmOnly() == Defines.ETS_IBM_ONLY) {
			writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
			writer.println("<td align=\"left\" width=\"99%\"><b>Access limited to:</b></td></tr>");
			writer.println("<tr><td align=\"left\" valign=\"bottom\" colspan=\"2\">");
			writer.println("<select id=\"ibmonly\" name=\"ibmonly\" />");

			if (doc.getIbmOnly() == Defines.ETS_IBM_ONLY)
				writer.println(
					"<option value=\"1\" selected=\"selected=\">All IBM team members</option>");
			else
				writer.println(
					"<option value=\"1\">All IBM team members</option>");

			if (doc.getIbmOnly() == Defines.ETS_IBM_CONF)
				writer.println(
					"<option value=\"2\" selected=\"selected=\">All IBM team members permanently</option>");
			else
				writer.println(
					"<option value=\"2\">All IBM team members permanently</option>");

			writer.println("</select></td></tr>");
			writer.println(
				"<tr><td class=\"small\" colspan=\"2\">To make accessible to all team members, its parent folder must be accessible to all team members.</td></tr>");

		}
		else {
			if (internal) {
				writer.println("<tr><td align=\"left\" width=\"1%\"><span class=\"ast\"><b>*</b></span></td>");
				writer.println("<td align=\"left\" width=\"99%\"><b>Access limited to:</b></td></tr>");
				writer.println("<tr><td align=\"left\" valign=\"bottom\" colspan=\"2\">");
				writer.println("<select id=\"ibmonly\" name=\"ibmonly\" />");
				if (doc.getIbmOnly() == Defines.ETS_PUBLIC)
					writer.println("<option value=\"0\" selected=\"selected=\">All team members</option>");
				else
					writer.println("<option value=\"0\">All team members</option>");

				if (doc.getIbmOnly() == Defines.ETS_IBM_ONLY)
					writer.println("<option value=\"1\" selected=\"selected=\">All IBM team members</option>");
				else
					writer.println("<option value=\"1\">All IBM team members</option>");

				if (doc.getIbmOnly() == Defines.ETS_IBM_CONF)
					writer.println("<option value=\"2\" selected=\"selected=\">All IBM team members permanently</option>");
				else
					writer.println("<option value=\"2\">All IBM team members permanently</option>");
				writer.println("</select></td></tr>");
			}
		}

		if (internal
			&& parent_cat.getIbmOnly() == Defines.ETS_PUBLIC
			&& doc.getIbmOnly() == Defines.ETS_IBM_ONLY) {
			writer.println("<td align=\"left\" colspan=\"2\" class=\"small\"><span style=\"color:#ff3333\"><b>Warning:</b></span>");
			writer.println(" If you change the security classification to accessible to all team members, all team members will be able to view this document and its previous version(s).<td></tr>");
		}

		//notify option
		writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");

		Vector users =
			ETSDatabaseManager.getProjMembers(Project.getProjectId(), true);
		if (parent_cat.isIbmOnlyOrConf()) {
			users = getIBMMembers(users, conn);
		}


		writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"15\" alt=\"\" /></td></tr>");
		writer.println("<tr><td colspan=\"2\" class=\"tdblue\" height=\"18\"> Notification</td></tr>");

		writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");
		writer.println("<tr><td colspan=\"2\">");
		writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td><b>E-mail notification option:</b> ");
		writer.println("<a href=\"ETSContentManagerServlet.wss?OP=Documents&BlurbField=Notification%20option\"  "
				+ "onclick=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Notification%20option','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400');return false\"  "
				+ "onkeypress=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Notification%20option','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400')\" >"
				+ "<img border=\"0\" name=\"Help\" src=\""
				+ Defines.ICON_ROOT+"/popup.gif\" width=\"16\" height=\"16\"  alt=\"Click for help on this field\" /></a>&nbsp;"
				+"</td>");
		writer.println("<td><input type=\"radio\" id=\"notifyOption\" name=\"notifyOption\" value=\"to\" checked=\"checked\" /><label for=\"notifyOption\">To</label></td>");
		writer.println("<td><input type=\"radio\" id=\"notifyOption\" name=\"notifyOption\" value=\"bcc\" /><label for=\"notifyOption\">Bcc</label></td>");
		writer.println("</tr></table></td></tr>");


		writer.println("<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");
		writer.println("<tr><td align=\"left\" colspan=\"2\"><label for=\"notify\"><b>Notify following team members:</b></label></td></tr>");
		writer.println("<tr><td align=\"left\" colspan=\"2\"><input type=\"checkbox\" name=\"notifyall\" value=\"yes\" id=\"notifyall\" /><label for=\"notifyall\">Notify all</label></td></tr>");

		writer.println("<tr><td align=\"left\" colspan=\"2\" class=\"small\">[For selecting multiple values use Ctrl + Mouse click together.]</td></tr>");
		writer.println("<tr><td colspan=\"2\"><table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		writer.println("<select name=\"notify\" multiple=\"multiple\" size=\"10\" style=\"width:300px\" width=\"300px\" id=\"notify\">");
		for (int u = 0; u < users.size(); u++) {
			ETSUser user = (ETSUser) users.elementAt(u);
			String username = ETSUtils.getUsersName(conn, user.getUserId());
			writer.println(
				"<option value=\""
					+ user.getUserId()
					+ "\">"
					+ username
					+ " ["
					+ user.getUserId()
					+ "]</option>");
		}
		writer.println("</select>");

		if (internal && !parent_cat.isIbmOnlyOrConf()) {
			writer.println("<tr><td colspan=\"2\" class=\"small\">[Security classification and additional access restrictions will be applied to the notification list]</td></tr>");
		}
		else if (!internal && !parent_cat.isIbmOnlyOrConf()) {
			writer.println("<tr><td colspan=\"2\" class=\"small\">[User access restrictions will be applied to the notification list]</td></tr>");
		}


		writer.println("</table>");
		writer.println("</td></tr>");

		writer.println(
			"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"5\" alt=\"\" /></td></tr>");
		writer.println("</table>");

	}

	private void printTopDocPart(ETSDoc doc) {

		StringBuffer buf = new StringBuffer();

		buf.append(
			"<table cellpadding=\"0\" cellspacing=\"1\" width=\"600\" border=\"0\">");
		buf.append(
			"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>");

		buf.append(
			"<tr><td width=\"25%\" valign=\"top\" nowrap=\"nowrap\" class=\"small\">Document name:&nbsp;</td><td align=\"left\" valign=\"top\" width=\"75%\" class=\"small\">"
				+ doc.getName()
				+ "</td></tr>");
		if (!doc.getDescription().equals("")) {
			buf.append(
				"<tr><td width=\"25%\" valign=\"top\" class=\"small\">Description:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\">"
					+ doc.getDescription()
					+ "</td></tr>");
		}
		else {
			buf.append(
				"<tr><td width=\"25%\" valign=\"top\" class=\"small\">Description:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\">&nbsp;</td></tr>");
		}

		if (!doc.getKeywords().equals("")) {
			buf.append(
				"<tr><td width=\"25%\" valign=\"top\" class=\"small\">Keywords:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\">"
					+ doc.getKeywords()
					+ "</td></tr>");
		}
		else {
			buf.append(
				"<tr><td width=\"25%\" valign=\"top\" class=\"small\">Keywords:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\">&nbsp;</td></tr>");
		}
		String author = "";

		try {
			author = ETSUtils.getUsersName(conn, doc.getUserId());
		}
		catch (Exception e) {
			author = doc.getUserId();
		}

		buf.append(
			"<tr><td width=\"25%\" valign=\"top\" class=\"small\">Author:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\">"
				+ author
				+ "</td></tr>");
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
		java.util.Date date = new java.util.Date(doc.getUploadDate());
		String dateStr = df.format(date);
		buf.append(
			"<tr><td width=\"25%\" valign=\"top\" class=\"small\">File date:&nbsp;</td><td width=\"75%\" align=\"left\" valign=\"top\" class=\"small\">"
				+ dateStr
				+ "</td></tr>");

		if (doc.isIbmOnlyOrConf()) {
			buf.append(
				"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"600\" alt=\"\" /></td></tr>");
			buf.append(
				"<tr><td colspan=\"2\" class=\"small\">This document has access restricted to IBM team members only.</td></tr>");
		}

		buf.append(
			"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>");
		buf.append(
			"<tr><td colspan=\"2\"><img src=\"" + Defines.V11_IMAGE_ROOT + "gray_dotted_line.gif\" height=\"1\" width=\"600\" alt=\"\" /></td></tr>");
		buf.append(
			"<tr><td colspan=\"2\"><img src=\"//www.ibm.com/i/c.gif\" height=\"8\" width=\"1\" alt=\"\" /></td></tr>");
		buf.append("</table>");

		writer.println(buf.toString());

	}

	public int getExpireDatePart(long date, int calPart) {
		Calendar c = Calendar.getInstance();
		java.util.Date d = new java.util.Date(date);
		c.setTime(d);
		return c.get(calPart);
	}

	//sandra pmo 
	private void printPMOChildren(
		ETSPMOffice curr_cat,
		Vector pmocats,
		Vector pmodocs,
		ETSPMODao pmodao,
		Vector vDetails,
		String sortby,
		String ad) {
		AccessCntrlFuncs acf = new AccessCntrlFuncs();
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		StringBuffer buf = new StringBuffer();

		Vector sortedcats = pmocats; //sortcats(cats,1);
		Vector sorteddocs = pmodocs; //sortdocs(docs,1);

		if (sortby.equals(Defines.SORT_BY_TYPE_STR)) {
			byte sortOrder = ETSComparator.getSortOrder(sortby);
			byte sortAD = ETSComparator.getSortBy(ad);
			//Collections.sort(sortedcats,new ETSComparator(sortOrder,sortAD)); 
			Collections.sort(sorteddocs, new ETSComparator(sortOrder, sortAD));
		}

		boolean gray_flag = true;
		boolean child_flag = false;

		int width_name = 259;
		int width_mod = 100;
		int width_type = 50;
		int width_author = 175;

		try {
			buf.append(
				"<table  cellpadding=\"2\" cellspacing=\"0\" width=\"600\" border=\"0\">");
			buf.append(
				"<tr><td colspan=\"6\"><img src=\"//www.ibm.com/i/c.gif\" height=\"16\" width=\"1\" alt=\"\" /></td></tr>");

			buf.append(
				"<tr><td colspan=\"6\" class=\"small\">Click on the column heading to sort</td></tr>\n");
			buf.append(
				"<tr><td colspan=\"6\"><img src=\"//www.ibm.com/i/c.gif\" height=\"10\" width=\"1\" alt=\"\" /></td></tr>\n");

			buf.append(
				"<tr><th id=\"list_name\" colspan=\"2\" align=\"left\" valign=\"middle\" height=\"16\">");
			//sort by name
			if (sortby.equals(Defines.SORT_BY_NAME_STR)) {
				if (ad.equals(Defines.SORT_ASC_STR)) {
					buf.append(
						"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&pmocat="
							+ curr_cat.getPMOID()
							+ "&linkid="
							+ linkid
							+ "&sort_by="
							+ Defines.SORT_BY_NAME_STR
							+ "&sort="
							+ Defines.SORT_DES_STR
							+ "\" class=\"fbox\">");
					buf.append(
						"Name</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
					buf.append("</table></th>");
				}
				else {
					buf.append(
						"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&pmocat="
							+ curr_cat.getPMOID()
							+ "&linkid="
							+ linkid
							+ "&sort_by="
							+ Defines.SORT_BY_NAME_STR
							+ "&sort="
							+ Defines.SORT_ASC_STR
							+ "\">");
					buf.append(
						"Name</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");
					buf.append("</table></th>");
				}
			}
			else {
				buf.append(
					"<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&pmocat="
						+ curr_cat.getPMOID()
						+ "&linkid="
						+ linkid
						+ "&sort_by="
						+ Defines.SORT_BY_NAME_STR
						+ "&sort="
						+ Defines.SORT_ASC_STR
						+ "\">");
				buf.append("Name</a></th>");
			}

			//sort by date
			buf.append(
				"<th id=\"list_date\" align=\"left\" valign=\"middle\">");
			if (sortby.equals(Defines.SORT_BY_DATE_STR)) {
				if (ad.equals(Defines.SORT_ASC_STR)) {
					buf.append(
						"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&pmocat="
							+ curr_cat.getPMOID()
							+ "&linkid="
							+ linkid
							+ "&sort_by="
							+ Defines.SORT_BY_DATE_STR
							+ "&sort="
							+ Defines.SORT_DES_STR
							+ "\" class=\"fbox\">");
					buf.append(
						"Modified</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
					buf.append("</table></th>");
				}
				else {
					buf.append(
						"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&pmocat="
							+ curr_cat.getPMOID()
							+ "&linkid="
							+ linkid
							+ "&sort_by="
							+ Defines.SORT_BY_DATE_STR
							+ "&sort="
							+ Defines.SORT_ASC_STR
							+ "\" class=\"fbox\">");
					buf.append(
						"Modified</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");
					buf.append("</table></th>");
				}
			}
			else {
				buf.append(
					"<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&pmocat="
						+ curr_cat.getPMOID()
						+ "&linkid="
						+ linkid
						+ "&sort_by="
						+ Defines.SORT_BY_DATE_STR
						+ "&sort="
						+ Defines.SORT_ASC_STR
						+ "\" class=\"fbox\">");
				buf.append("Modified</a></th>");
			}

			//sort by type
			buf.append(
				"<th id=\"list_type\" align=\"left\" valign=\"middle\">");
			if (sortby.equals(Defines.SORT_BY_TYPE_STR)) {
				if (ad.equals(Defines.SORT_ASC_STR)) {
					buf.append(
						"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&pmocat="
							+ curr_cat.getPMOID()
							+ "&linkid="
							+ linkid
							+ "&sort_by="
							+ Defines.SORT_BY_TYPE_STR
							+ "&sort="
							+ Defines.SORT_DES_STR
							+ "\" class=\"fbox\">");
					buf.append(
						"Type</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
					buf.append("</table></th>");
				}
				else {
					buf.append(
						"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&pmocat="
							+ curr_cat.getPMOID()
							+ "&linkid="
							+ linkid
							+ "&sort_by="
							+ Defines.SORT_BY_TYPE_STR
							+ "&sort="
							+ Defines.SORT_ASC_STR
							+ "\" class=\"fbox\">");
					buf.append(
						"Type</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");
					buf.append("</table></th>");
				}
			}
			else {
				buf.append(
					"<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&pmocat="
						+ curr_cat.getPMOID()
						+ "&linkid="
						+ linkid
						+ "&sort_by="
						+ Defines.SORT_BY_TYPE_STR
						+ "&sort="
						+ Defines.SORT_ASC_STR
						+ "\" class=\"fbox\">");
				buf.append("Type</a></th>");
			}

			//sort by author
			buf.append(
				"<th id=\"list_author\" align=\"left\" valign=\"middle\">");
			if (sortby.equals(Defines.SORT_BY_AUTH_STR)) {
				if (ad.equals(Defines.SORT_ASC_STR)) {
					buf.append(
						"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&pmocat="
							+ curr_cat.getPMOID()
							+ "&linkid="
							+ linkid
							+ "&sort_by="
							+ Defines.SORT_BY_AUTH_STR
							+ "&sort="
							+ Defines.SORT_DES_STR
							+ "\" class=\"fbox\">");
					buf.append(
						"Author</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<img src=\"" + Defines.ICON_ROOT + "u.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"ascending\" /></th>");
					buf.append("</table></th>");
				}
				else {
					buf.append(
						"<table  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="
							+ Project.getProjectId()
							+ "&tc="
							+ TopCatId
							+ "&pmocat="
							+ curr_cat.getPMOID()
							+ "&linkid="
							+ linkid
							+ "&sort_by="
							+ Defines.SORT_BY_AUTH_STR
							+ "&sort="
							+ Defines.SORT_ASC_STR
							+ "\" class=\"fbox\">");
					buf.append(
						"Author</a></th><th align=\"left\" valign=\"middle\" height=\"16\">");
					buf.append(
						"<img src=\"" + Defines.ICON_ROOT + "d.gif\" border=\"0\"  height=\"16\" width=\"16\" alt=\"descending\" /></th>");
					buf.append("</table></th>");
				}
			}
			else {
				buf.append(
					"<a class=\"parent\" href=\"ETSProjectsServlet.wss?proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&pmocat="
						+ curr_cat.getPMOID()
						+ "&linkid="
						+ linkid
						+ "&sort_by="
						+ Defines.SORT_BY_AUTH_STR
						+ "&sort="
						+ Defines.SORT_ASC_STR
						+ "\" class=\"fbox\">");
				buf.append("Author</a></th>");
			}

			buf.append(
				"<th id=\"list_details\" class=\"small\">&nbsp;</th></tr>");
			//	   -----------------------------------------------------------------------------------------------------------	    

			if (gray_flag) {
				buf.append("<tr style=\"background-color:#eeeeee\">");
				gray_flag = false;
			}
			else {
				buf.append("<tr>");
				gray_flag = true;
			}

			buf.append(
				"<td width=\"16\" height=\"17\" align=\"left\" valign=\"top\"><img src=\""
					+ Defines.SERVLET_PATH
					+ "ETSImageServlet.wss?proj=ETS_BACK_IMG&mod=0\" width=\"12\" height=\"8\" alt=\"back\" /></td>");
			//img
			if (curr_cat.getPMOID().equals(curr_cat.getPMO_Project_ID())) {
				ETSCat topcat =
					(ETSCat) ETSDatabaseManager.getCat(
						TopCatId,
						Project.getProjectId());
				buf.append(
					"<td headers=\"list_name\" height=\"17\" width=\""
						+ width_name
						+ "\" align=\"left\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&cc="
						+ TopCatId
						+ "&linkid="
						+ linkid
						+ "\" class=\"fbox\">Back to '"
						+ topcat.getName()
						+ "'</a></td>");
				//filename
			}
			else {
				ETSPMODao pmoDao = new ETSPMODao();
				ETSPMOffice pmoparent =
					(ETSPMOffice) pmoDao.getPMOfficeObjectDetail(
						conn,
						curr_cat.getPMO_Project_ID(),
						curr_cat.getPMO_Parent_ID());
				buf.append(
					"<td headers=\"list_name\" height=\"17\" width=\""
						+ width_name
						+ "\" align=\"left\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?proj="
						+ Project.getProjectId()
						+ "&tc="
						+ TopCatId
						+ "&pmocat="
						+ curr_cat.getPMO_Parent_ID()
						+ "&linkid="
						+ linkid
						+ "\" class=\"fbox\">Back to '"
						+ pmoparent.getName()
						+ "'</a></td>");
				//filename
			}
			buf.append(
				"<td headers=\"list_date\" height=\"17\" width=\""
					+ width_mod
					+ "\" align=\"left\" valign=\"top\"> &nbsp; </td>");
			//date
			buf.append(
				"<td headers=\"list_type\" height=\"17\" align=\"left\" class=\"small\" width=\""
					+ width_type
					+ "\" valign=\"top\"> &nbsp; </td>");
			//format
			buf.append(
				"<td headers=\"list_author\" height=\"17\" align=\"left\" class=\"small\" width=\""
					+ width_author
					+ "\" valign=\"top\"> &nbsp; </td>");
			//author
			buf.append("</tr>");

			if (sortedcats != null) {
				for (int i = 0; i < sortedcats.size(); i++) {
					ETSPMOffice cat = (ETSPMOffice) sortedcats.elementAt(i);
					
					boolean hasSubDocs = pmodao.isDocumentsAvailable(conn,Params,pmodao,vDetails,cat.getPMOID(),false);
					
					Vector vDocs =  pmodao.getPMODocuments(conn,cat.getPMOID(),Project.getPmo_project_id());
					
					boolean isdocsAvailable = false;
					if (vDocs != null && vDocs.size() > 0) {
						isdocsAvailable = true;
					}
					
					//hasSubDocs = true;
					if(hasSubDocs || isdocsAvailable){
						child_flag = true;
	
						if (gray_flag) {
							buf.append("<tr style=\"background-color:#eeeeee\">");
							gray_flag = false;
						}
						else {
							buf.append("<tr>");
							gray_flag = true;
						}
						buf.append(
							"<td width=\"16\" height=\"17\" align=\"left\" valign=\"top\"><img src=\""
								+ Defines.SERVLET_PATH
								+ "ETSImageServlet.wss?proj=ETS_CAT_IMG&mod=0\" width=\"13\" height=\"9\" alt=\"folder\" /></td>");
						//img
						buf.append(
							"<td headers=\"list_name\" height=\"17\" width=\""
								+ width_name
								+ "\" align=\"left\" valign=\"top\"><a href=\"ETSProjectsServlet.wss?proj="
								+ Project.getProjectId()
								+ "&tc="
								+ TopCatId
								+ "&pmocat="
								+ cat.getPMOID()
								+ "&linkid="
								+ linkid
								+ "\" class=\"fbox\">"
								+ cat.getName()
								+ "</a></td>");
						//filename
	
						//java.util.Date date = (java.util.Date)cat.getLastTimestamp();
						//buf.append("<td headers=\"list_date\" height=\"17\" width=\""+width_mod+"\" align=\"left\" valign=\"top\">"+df.format(date)+"</td>"); //date
						buf.append(
							"<td headers=\"list_type\" height=\"17\" width=\""
								+ width_type
								+ "\" align=\"left\" class=\"small\" valign=\"top\"> - </td>");
						//date
						buf.append(
							"<td headers=\"list_type\" height=\"17\" width=\""
								+ width_type
								+ "\" align=\"left\" class=\"small\" valign=\"top\"> - </td>");
						//format
	
						buf.append(
							"<td headers=\"list_author\" height=\"17\" width=\""
								+ width_author
								+ "\" class=\"small\" align=\"left\" valign=\"top\">System</td>");
						//author
						buf.append("</tr>");
					}
				}
			}

			if (sorteddocs != null) {
				for (int i = 0; i < sorteddocs.size(); i++) {
					ETSPMODoc doc = (ETSPMODoc) sorteddocs.elementAt(i);
					child_flag = true;

					if (gray_flag) {
						buf.append("<tr style=\"background-color:#eeeeee\">");
						gray_flag = false;
					}
					else {
						buf.append("<tr>");
						gray_flag = true;
					}

					buf.append(
						"<td width=\"16\" height=\"17\" align=\"left\" valign=\"top\"><img src=\""
							+ Defines.SERVLET_PATH
							+ "ETSImageServlet.wss?proj=ETS_DOC_IMG&mod=0\" width=\"12\" height=\"12\" alt=\"document\" /></td>");
					//img

					buf
						.append(
							"<td headers=\"list_name\" height=\"17\" width=\""
							+ width_name
							+ "\" align=\"left\" valign=\"top\"><a href=\"ETSContentDeliveryServlet.wss/"
					//+ doc.getDocDesc()
					+URLEncoder.encode(doc.getDocDesc())
						+ "?projid="
						+ Project.getProjectId()
						+ "&pmodocid="
						+ doc.getDocId()
						+ "&linkid="
						+ linkid
						+ "\" target=\"new\" class=\"fbox\">"
						+ doc.getDocName()
						+ "</a></td>");
					//filename
					String upDate = "";
					if (doc.getUpdateDate() != null) {
						java.util.Date date =
							(java.util.Date) doc.getUpdateDate();
						upDate = df.format(date);
					}
					buf.append(
						"<td headers=\"list_date\" height=\"17\" width=\""
							+ width_mod
							+ "\" align=\"left\" valign=\"top\">"
							+ upDate
							+ "</td>");
					//date
					buf.append(
						"<td headers=\"list_type\" height=\"17\" width=\""
							+ width_type
							+ "\" align=\"left\" class=\"small\" valign=\"top\">"
							+ doc.getFileType()
							+ "</td>");
					//file type
					buf.append(
						"<td headers=\"list_author\" height=\"17\" width=\""
							+ width_author
							+ "\" class=\"small\" align=\"left\" valign=\"top\">System</td>");
					//author
					buf.append("</tr>");
				}
			}
			else {
				//System.out.println("sorted doc == null");
			}

			if (!child_flag) {
				buf.append("<tr>");
				buf.append(
					"<td colspan=\"6\" height=\"17\" class=\"small\"> This folder is currently empty.</td>");
				buf.append("</tr>");
			}

			buf.append("</table>");
			writer.println(buf.toString());
		}
		catch (Exception e) {
			//System.out.println("except error in docman print children= " + e);
			e.printStackTrace();
		}

	}

	//end sandra pmo


	private String getCatJS(Vector users, Vector ibmusers){
		StringBuffer buf = new StringBuffer();
		
		buf.append("<script  type=\"text/javascript\" language=\"JavaScript\">\n");

		buf.append("function clearOpList(list){");
		buf.append("var cnt = list.options.length;");
		buf.append("for (var c=0;c<cnt; c++){");
		buf.append("list.options[0] = null;");
		buf.append("}");
		buf.append("}\n");

		buf.append("function addOp(list,text,val,selvalue){");
		buf.append("list[list.length] = new Option(text,val,false,selvalue);");
		buf.append("}\n");

		buf.append("function checkSel(list,userid){");
		buf.append("for (var c=0;c<list.length; c++){");
		buf.append("if(list[c] == userid){");
		buf.append("return true;");
		buf.append("}");
		buf.append("}");
		buf.append("return false;");
		buf.append("}\n");


		buf.append("function getSel(list){");
		buf.append("var i = 0;");
		buf.append("var sellist = new Array();");
		buf.append("for (var c=0;c<list.length; c++){");
		buf.append("if(list.options[c].selected == true){");
		buf.append("sellist[i]=list.options[c].value;");
		buf.append("i++;");
		buf.append("}");
		buf.append("}");
		buf.append("return sellist;");
		buf.append("}\n");

		buf.append("function security_ch(userlist,ibmvar){");
		buf.append("var oplist = new Array();");
		buf.append("oplist = getSel(userlist);");
		
		buf.append("if (ibmvar.value==0){");
		buf.append("clearOpList(userlist);");
		for(int ii = 0; ii<users.size(); ii++){ 
			ETSUser uu = (ETSUser)users.elementAt(ii);
			try{
				buf.append("addOp(userlist,'"+ETSUtils.getUsersName(conn,uu.getUserId())+" ["+uu.getUserId()+"]','"+uu.userid+"',checkSel(oplist,'"+uu.userid+"'));\n");
			}
			catch (Exception e){
				buf.append("addOp(userlist,'["+uu.getUserId()+"]','"+uu.userid+"',checkSel(oplist,'"+uu.userid+"'));\n");
			}
		}
		buf.append("}\n");

		buf.append("else if ((ibmvar.value==1) || (ibmvar.value==2)){");
		buf.append("clearOpList(userlist);");
		for(int ii = 0; ii<ibmusers.size(); ii++){ 
			ETSUser uu = (ETSUser)ibmusers.elementAt(ii);
			try{
				buf.append("addOp(userlist,'"+ETSUtils.getUsersName(conn,uu.getUserId())+" ["+uu.getUserId()+"]','"+uu.userid+"',checkSel(oplist,'"+uu.userid+"'));\n");
			}
			catch(Exception e){
				buf.append("addOp(userlist,'["+uu.getUserId()+"]','"+uu.userid+"',checkSel(oplist,'"+uu.userid+"'));\n");
			}
		}
		buf.append("}");
		buf.append("}\n");

		buf.append("</script>\n");	
		return buf.toString();
	}

	private String getDocJS(Vector users, Vector ibmusers, boolean propOnly){
		StringBuffer buf = new StringBuffer();
		
		buf.append("<script  type=\"text/javascript\" language=\"JavaScript\">\n");

		buf.append("function clearOpList(list){");
		buf.append("var cnt = list.options.length;");
		buf.append("for (var c=0;c<cnt; c++){");
		buf.append("list.options[0] = null;");
		buf.append("}");
		buf.append("}\n");

		buf.append("function addOp(list,text,val,selvalue){");
		buf.append("list[list.length] = new Option(text,val,false,selvalue);");
		buf.append("}\n");

		buf.append("function checkSel(list,userid){");
		buf.append("for (var c=0;c<list.length; c++){");
		buf.append("if(list[c] == userid){");
		buf.append("return true;");
		buf.append("}");
		buf.append("}");
		buf.append("return false;");
		buf.append("}\n");


		buf.append("function getSel(list){");
		buf.append("var i = 0;");
		buf.append("var sellist = new Array();");
		buf.append("for (var c=0;c<list.length; c++){");
		buf.append("if(list.options[c].selected == true){");
		buf.append("sellist[i]=list.options[c].value;");
		buf.append("i++;");
		buf.append("}");
		buf.append("}");
		buf.append("return sellist;");
		buf.append("}\n");

		buf.append("function security_ch(userlist,notifylist,ibmvar){");
		buf.append("var ulist = new Array();");
		
		buf.append("ulist = getSel(userlist);");
		
		if(!propOnly){
			buf.append("var nlist = new Array();");
			buf.append("nlist = getSel(notifylist);");
		}
		buf.append("if (ibmvar.value==0){");
		buf.append("clearOpList(userlist);");
		if (!propOnly)
			buf.append("clearOpList(notifylist);");
	
		for(int ii = 0; ii<users.size(); ii++){ 
			ETSUser uu = (ETSUser)users.elementAt(ii);
			try{
				buf.append("addOp(userlist,'"+ETSUtils.getUsersName(conn,uu.getUserId())+" ["+uu.getUserId()+"]','"+uu.userid+"',checkSel(ulist,'"+uu.userid+"'));\n");
				if (!propOnly)
					buf.append("addOp(notifylist,'"+ETSUtils.getUsersName(conn,uu.getUserId())+" ["+uu.getUserId()+"]','"+uu.userid+"',checkSel(nlist,'"+uu.userid+"'));\n");
			}
			catch (Exception e){
				buf.append("addOp(userlist,'["+uu.getUserId()+"]','"+uu.userid+"',checkSel(ulist,'"+uu.userid+"'));\n");
				if (!propOnly)
					buf.append("addOp(notifylist,'["+uu.getUserId()+"]','"+uu.userid+"',checkSel(nlist,'"+uu.userid+"'));\n");
			}
		}
		buf.append("}\n");

		buf.append("else if ((ibmvar.value==1) || (ibmvar.value==2)){");
		buf.append("clearOpList(userlist);");
		if (!propOnly)
			buf.append("clearOpList(notifylist);");
		for(int ii = 0; ii<ibmusers.size(); ii++){ 
			ETSUser uu = (ETSUser)ibmusers.elementAt(ii);
			try{
				buf.append("addOp(userlist,'"+ETSUtils.getUsersName(conn,uu.getUserId())+" ["+uu.getUserId()+"]','"+uu.userid+"',checkSel(ulist,'"+uu.userid+"'));\n");
				if (!propOnly)
					buf.append("addOp(notifylist,'"+ETSUtils.getUsersName(conn,uu.getUserId())+" ["+uu.getUserId()+"]','"+uu.userid+"',checkSel(nlist,'"+uu.userid+"'));\n");
			}
			catch(Exception e){
				buf.append("addOp(userlist,'["+uu.getUserId()+"]','"+uu.userid+"',checkSel(ulist,'"+uu.userid+"'));\n");
				if (!propOnly)
					buf.append("addOp(notifylist,'["+uu.getUserId()+"]','"+uu.userid+"',checkSel(nlist,'"+uu.userid+"'));\n");
			}
		}
		buf.append("}");
		buf.append("}\n");

		buf.append("</script>\n");	
		return buf.toString();
	}



	private void doAddDocComment(int docid, String currdocid,String msg) {
		try {
			if (userRole.equals(Defines.ETS_EXECUTIVE)|| userRole.equals(Defines.WORKSPACE_VISITOR)|| userRole.equals(Defines.WORKSPACE_CLIENT)) {
				printHeader("", "Update document properties", false);
				writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
				writer.println("You are not authorized to perform this action");
				return;
			}


			String s_comment = "";
			String s_opt = "";
			String s_nall = "";
			String[] s_nusers = null;
			
			s_comment = (String) request.getSession(true).getAttribute("ETSDocComment");
			s_opt = (String) request.getSession(true).getAttribute("ETSNotifyOptions");
			s_nall = (String) request.getSession(true).getAttribute("ETSNotifyAll");
			s_nusers = (String[])request.getSession(true).getAttribute("ETSNotifyUsers");
			
			if (s_comment == null){
				s_comment = "";	
			}
			if (s_opt == null){
				s_opt = "";
			}
			if (s_nall == null){
				s_nall = "";
			}
			
			ETSDocCommon.removeSessionVar("ETSDocComment",request);
			ETSDocCommon.removeSessionVar("ETSNotifyOptions",request);
			ETSDocCommon.removeSessionVar("ETSNotifyAll",request);
			ETSDocCommon.removeSessionVar("ETSNotifyUsers",request);

			Vector breadcrumb = new Vector();
	
			ETSProj proj = Project;
			ETSCat parent_cat = ETSDatabaseManager.getCat(CurrentCatId);
	
			if (parent_cat != null) {
				breadcrumb = getBreadcrumb(parent_cat);
				String header = getBreadcrumbTrail(breadcrumb);
				printHeader(header, "Post document comment", false);
				writer.println("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\">");
				writer.println("<tr><td>");
	
				ETSDoc doc = ETSDatabaseManager.getDocByIdAndProject(docid,Project.getProjectId());
				if (doc == null) {
					writer.println("Invalid document id");
					return;
				}
				if (doc.isIbmOnlyOrConf() && (!(es.gDECAFTYPE.trim()).equalsIgnoreCase("I"))) {
					writer.println("You are not authorized to view this document");
					return;
				}
	
				//if (!((userRole == Defines.WORKSPACE_OWNER|| doc.getUserId().equals(es.gIR_USERN)|| isSuperAdmin)|| (!doc.hasExpired() && userRole == Defines.WORKSPACE_MANAGER))) {
				if (doc.hasExpired() && !((userRole == Defines.WORKSPACE_OWNER)|| doc.getUserId().equals(es.gIR_USERN) || isSuperAdmin)){
					writer.println("You are not authorized to edit this document");
					return;
				}
	
				if (doc.IsDPrivate()){
					System.out.println("1");
					if (!ETSDocCommon.isAuthorized(doc.getUserId(),doc.getId(),proj.getProjectId(),userRole,isSuperAdmin,isExecutive,false,false,true,es.gIR_USERN)){
						System.out.println("2");
						writer.println("You are not authorized to view this document");
						return;
					}
					System.out.println("3");
				}
	
				String decaftype = es.gDECAFTYPE.trim();
	
				writer.println("<form action=\"ETSContentManagerServlet.wss\" method=\"post\" name=\"addDocCommForm\">");
				if (!((msg.trim()).equals(""))) {
					if (msg.equals("1")) {
						msg = "An error occured with the current document id.";
					}
					else if (msg.equals("2")) {
						msg = "Comments need to be 1-32K characters long";
					}
					else if (msg.equals("3")) {
						msg = "Invalid project";
					}
					else if (msg.equals("4")) {
						msg = "An error occured with adding your comment. Please try again.";
					}
					else if (msg.equals("5")) {
						msg = "An error occured. Please try again.";
					}
	
					writer.println("<table><tr><td><span style=\"color:#ff3333\">"+ msg+ "</span></td></tr></table>");
				}
	
				writer.println("<input type=\"hidden\" name=\"action\" value=\"addcomm2\" />");
				writer.println("<input type=\"hidden\" name=\"proj\" value=\""+ proj.getProjectId()+ "\" />");
				writer.println("<input type=\"hidden\" name=\"tc\" value=\""+ TopCatId+ "\" />");
				writer.println("<input type=\"hidden\" name=\"cc\" value=\""+ parent_cat.getId()+ "\" />");
				writer.println("<input type=\"hidden\" name=\"docid\" value=\""+ docid+ "\" />");
				writer.println("<input type=\"hidden\" name=\"linkid\" value=\""+ linkid+ "\" />");
				if(!doc.isLatestVersion() && !currdocid.equals("")){
					writer.println("<input type=\"hidden\" name=\"currdocid\" value=\""+ currdocid+ "\" />");
				}
				
				writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
				writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td>");
				writer.println("<tr><td><span class=\"ast\"><b>*</b></span></td><td><span class=\"small\"> Denotes required fields</span></td>");
				writer.println("</tr></table>");
	
	
				writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
				writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"20\" height=\"1\" alt=\"\" /></td>");
				writer.println("<td>");
	
				writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
				writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr>");

				//comment
				writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");
				writer.println("<tr><td align=\"left\"><label for=\"doccomm\"><b>Comment:</b><span class=\"ast\">*</span></label></td></tr>");
				writer.println("<tr><td><textarea id=\"doccomm\" name=\"doccomm\" cols=\"50\" rows=\"7\" wrap=\"soft\" style=\"width:400px\" width=\"400px\" value=\"\">");
				if (s_comment.length()>0){
					writer.println(s_comment);
				}
				writer.println("</textarea></td></tr>");

					
				//notify option
				Vector users = ETSDatabaseManager.getProjMembers(Project.getProjectId(),true);
				if (doc.IsDPrivate()){
					users  = ETSDatabaseManager.getRestrictedProjMembers(proj.getProjectId(),doc.getId(),true,false);	
				}
				if (doc.isIbmOnlyOrConf()) {
					users = getIBMMembers(users,conn);
				}
				
				writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"1\" height=\"15\" alt=\"\" /></td></tr>");
				writer.println("<tr><td class=\"tdblue\" height=\"18\"> Notification</td></tr>");
	
				writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");
				writer.println("<tr><td>");
				writer.println("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td><b>E-mail notification option:</b>");
				writer.println("<a href=\"ETSContentManagerServlet.wss?OP=Documents&BlurbField=Notification%20option\"  "
					+ "onclick=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Notification%20option','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400');return false\"  "
					+ "onkeypress=\"window.open('ETSContentManagerServlet.wss?OP=Documents&BlurbField=Notification%20option','Help','toolbar=0,location=0,resizable=1,scrollbars=1,status=0,menubar=0,left=200,top=100,width=400,height=400')\" >"
					+ "<img border=\"0\" name=\"Help\" src=\""
					+ Defines.ICON_ROOT+"/popup.gif\" width=\"16\" height=\"16\"  alt=\"Click for help on this field\" /></a>&nbsp;");
				writer.println("</td>");
				if(s_opt.equals("") || s_opt.equals("to")){
					writer.println("<td><input type=\"radio\" id=\"notifyOption\" name=\"notifyOption\" value=\"to\" checked=\"checked\" /><label for=\"notifyOption\">To</label></td>");
					writer.println("<td><input type=\"radio\" id=\"notifyOption\" name=\"notifyOption\" value=\"bcc\" /><label for=\"notifyOption\">Bcc</label></td>");
				}
				else{
					writer.println("<td><input type=\"radio\" id=\"notifyOption\" name=\"notifyOption\" value=\"to\" /><label for=\"notifyOption\">To</label></td>");
					writer.println("<td><input type=\"radio\" id=\"notifyOption\" name=\"notifyOption\" value=\"bcc\" checked=\"checked\" /><label for=\"notifyOption\">Bcc</label></td>");
				}
				writer.println("</tr></table></td></tr>");
	
				writer.println("<tr><td><img src=\"//www.ibm.com/i/c.gif\" width=\"10\" height=\"10\" alt=\"\" /></td></tr>");
				
				writer.println("<tr><td align=\"left\"><label for=\"notify\"><b>Notify following team members:</b></label></td></tr>");
				if(!s_nall.equals("")){
					writer.println("<tr><td align=\"left\"><input type=\"checkbox\" name=\"notifyall\" value=\"yes\" id=\"notifyall\" checked=\"checked\" /><label for=\"notifyall\">Notify all</label></td></tr>");
				}
				else{
					writer.println("<tr><td align=\"left\"><input type=\"checkbox\" name=\"notifyall\" value=\"yes\" id=\"notifyall\" /><label for=\"notifyall\">Notify all</label></td></tr>");
				}	
						
				/*writer.println("<tr><td align=\"left\"><table width=\"320px\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tr><td><label for=\"notify\"><b>Notify:</b></label></td>");
				if(!s_nall.equals("")){
					writer.println("<td align=\"right\"><input type=\"checkbox\" name=\"notifyall\" value=\"yes\" id=\"notifyall\" checked=\"checked\" /><label for=\"notifyall\">Notify all</label></td></tr></table></td></tr>");
				}
				else{
					writer.println("<td align=\"right\"><input type=\"checkbox\" name=\"notifyall\" value=\"yes\" id=\"notifyall\" /><label for=\"notifyall\">Notify all</label></td></tr></table></td></tr>");
				}*/
				writer.println("<tr><td align=\"left\" class=\"small\">[For selecting multiple values use Ctrl + Mouse click together.]</td></tr>");
				writer.println("<tr><td><table width=\"443\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
				writer.println("<select id=\"notify\" name=\"notify\" multiple=\"multiple\" size=\"10\" style=\"width:320px\" width=\"320px\">");
				Vector s = new Vector();
				if (s_nusers != null){
					if (s_nusers.length > 0){
						for (int si = 0; si < s_nusers.length; si++){
							s.addElement(s_nusers[si]);	
						}
							
					}	
				}
				
				for (int u = 0; u < users.size(); u++) {
					ETSUser user = (ETSUser) users.elementAt(u);
					String username = ETSUtils.getUsersName(conn, user.getUserId());
					if(s.contains(user.getUserId()))
						writer.println("<option value=\""+ user.getUserId()+ "\" selected=\"selected\">"+ username+ " ["+ user.getUserId()+ "]</option>");
					else
						writer.println("<option value=\""+ user.getUserId()+ "\">"+ username+ " ["+ user.getUserId()+ "]</option>");
				}
				writer.println("</select>");

				writer.println("</table>");
				writer.println("</td></tr>");



				writer.println("</table>");

				writer.println("<br /><br />");
				writer.println("<input type=\"image\" src=\"" + Defines.BUTTON_ROOT + "submit.gif\" border=\"0\"  height=\"21\" width=\"120\" alt=\"update document\" /> &nbsp; &nbsp; ");
	
				if(doc.isLatestVersion()){
					writer.println("<a href=\"ETSProjectsServlet.wss?action=details&proj="+ Project.getProjectId()
							+ "&tc="+ TopCatId+ "&cc="+ CurrentCatId+ "&docid="+ docid+ "&linkid="+ linkid
							+ "\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" /> Cancel</a>");
				}
				else{
					writer.println("<a href=\"ETSProjectsServlet.wss?action=prevdetails&proj="+ Project.getProjectId()
							+ "&tc="+ TopCatId+ "&cc="+ CurrentCatId+ "&currdocid="+currdocid+"&docid="+ docid+ "&linkid="+ linkid
							+ "\"><img src=\"" + Defines.BUTTON_ROOT + "cancel_rd.gif\" border=\"0\"  height=\"21\" width=\"21\" alt=\"Cancel\" /> Cancel</a>");
				}
				writer.println("</td></tr></table>");
				writer.println("</form>");
			}
			else {
				writer.println("error occurred: invalid cat id for this user.");
				//System.out.print("put bad parent cat id message here");
			}
		}
		catch (Exception e) {
			//System.out.println("error here");
			e.printStackTrace();
		}
	}
	
	private void displayAllDocComments(ETSDoc doc, Vector resUsers, String currdocid, AccessCntrlFuncs acf) {
		StringBuffer buf = new StringBuffer();
		
		if (doc.hasExpired() && (!(doc.getUserId().equals(es.gIR_USERN) || userRole.equals(Defines.WORKSPACE_OWNER) || isSuperAdmin))) {
			writer.println("You are not allowed to access this document");
			return;
		}
	
		buf.append(displayDocDetailsPart1(doc,resUsers,acf,!doc.isLatestVersion()));
	
		buf.append(displayRecentCommentsSection(doc,false,currdocid));
			
		writer.println(buf.toString());
	}

} // end of class
