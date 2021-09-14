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

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AMTException;
import oem.edge.amt.AccessCntrlFuncs;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.Metrics;
import oem.edge.common.Global;
import oem.edge.ets.fe.dealtracker.ETSDealTrackerDAO;
import oem.edge.ets.fe.dealtracker.ETSTask;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.common.StringUtil;
import oem.edge.ets.fe.documents.data.DocumentDAO;


public class ETSContentUploadServlet extends HttpServlet {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.13";


    public  ETSContentUploadServlet(){
    }

    
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        performGetPost(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        performGetPost(req, resp);
    }
    

    // add a new document
    /*protected String[] performGetPost(ETSDoc doc, HttpServletRequest req, HttpServletResponse resp)
        throws IOException
    {
	return performGetPost(doc, -1, req, resp);
    }*/

    // update an existing document keeping the old version archived
    //protected String[] performGetPost(ETSDoc doc, int existingDocID, HttpServletRequest req, HttpServletResponse resp)
	protected void performGetPost(HttpServletRequest req, HttpServletResponse resp)
        throws IOException
    {
  
	    
	    String formCharset = "ISO_8859-1";
	    String parm = null;
	    InputStream inStream = null;
	    InputStream inStream2 = null;
	    String fileName = null;
		String filePath = null;
	    String docname = null;
	    String docdesc = null;
	    String keywords = null;
	    String notify = null;
	    String sNotify = "";
	    String sIbmOnly = null;
		char ibmonly = Defines.ETS_PUBLIC;
		
		String projectid = null;
		String currentcatid = null;
		String topcatid = null;
		String linkid = "251000";
		String meetingid = "0";
		String selfid = "";
		String setid = "";
		
		String action = "";
		int parentId =  0;
		int doctype = Defines.DOC;
		String cv_id = "";
		String meeting_id = "";
		String repeatid="";
		String repeat_id="";
		String action1 = "";
		String action_meeting = "";
		int olddocid = -1;
		boolean errorFlag = false;
		String[] err = new String[2];
		int inStreamAvail = -1;
		ETSDoc doc;
		String olddocIbmOnly="";
		
		String options = "";
		String exDateStr = "";
		String exMonthStr = "";
		String exDayStr = "";
		String exYearStr = "";
		
		
		String sIsResUser = "";
		Vector vResUsers = new Vector();
		String resUsers = "";
		String sNotifyOptions = "";
		String olddocDPrivate= "";
		String sNotifyAll = "";
		Vector vNotify = new Vector();
		
	    EdgeAccessCntrl es = new EdgeAccessCntrl();
		Connection conn = null;
		
		try
		  {
			conn = ETSDBUtils.getConnection();
 			if (!es.GetProfile(resp,req,conn)) {
				return;
			}
	
			try{
				  System.out.println("w "+new java.util.Date());
				  Vector mult = MimeMultipartParser.getBodyParts(req.getInputStream(),req.getIntHeader("Content-Length"));
				  System.out.println("e "+new java.util.Date());
				  int count = mult.size();
		
		        
		        
		            // Since the parts can be in any order, we loop through once to find our
		            // form charset, so we know how to convert the rest of the data in the other parts.
		            for (int i=0; i<count; ++i)
		            {
		              WebAccessBodyPart part = (WebAccessBodyPart)mult.elementAt(i);
		              if (part.getDisposition("name", "ISO_8859-1").equalsIgnoreCase("form-charset"))
		              {
		                formCharset = part.getContentAsString("ISO_8859-1");
		              }
		            }
		
			
					
		            for (int i=0; i < count; ++i)
		            {
		              WebAccessBodyPart part = (WebAccessBodyPart)mult.elementAt(i);
		
		              parm = part.getDisposition("name", formCharset);
		              String value = (part.getContentAsString(formCharset)).trim();
		
		
		              if (parm.equalsIgnoreCase(Defines.FILE_FORM_NAME_CLIENT_FILE_NAME))
		              {
		                // Set our input stream
		                inStream = part.getContentInputStream();
						//inStream2 = part.getContentInputStream();
		
						fileName = part.getDisposition(Defines.FILE_MULTIPART_DISPOSITION_FILENAME, formCharset);
						filePath = fileName;
						
						if (fileName.length() > 0) {
						    int lastBackSlash = fileName.lastIndexOf("\\");      // Windows based
						    int lastForwardSlash = fileName.lastIndexOf(Defines.SLASH);    // Unix based
		
						    if (lastBackSlash > 0) {
								fileName = fileName.substring(lastBackSlash + 1, fileName.length());
						    }
						    else if (lastForwardSlash > 0) {
								fileName = fileName.substring(lastForwardSlash + 1, fileName.length());
						    }
		
						    fileName = fileName.replace(' ','_');
						    System.out.println("*****filename="+fileName);
						}
						else {
						    System.out.println("No file name was submitted to be uploaded.");
						    //String[] err = {"0","No file name was submitted to be upload"};
						    errorFlag = true;
						    err = new String[]{"1","1"};
							//break;
							//return err;
						}
		              }
		              else
		              {
						  System.out.println(parm + "=" + value);
						  if (parm.equalsIgnoreCase(Defines.FILE_FORM_NAME_DOC_NAME)) {
						      docname = value;
						      if (docname == null || docname.length()<=0 || docname.length()>128){
						      	
							  //String[] err = {"0","The document name must be 1-128 characters long."};
							  errorFlag = true;
							  err = new String[]{"1","2"};
							  //break;
							  //return err;
						      }
						  }
						  else if (parm.equalsIgnoreCase(Defines.FILE_FORM_NAME_DOC_DESC)) {
						      docdesc = value;
						      if (docdesc == null || docdesc.length()<=0){
							 	 docdesc="";
							  }
							  else if (docdesc.length()>2000){
								errorFlag = true;
								err = new String[]{"1","9"};
							  }
						  }
						  else if (parm.equalsIgnoreCase(Defines.FILE_FORM_NAME_DOC_KEYWORDS)) {
						      keywords = value;
						      if (keywords == null || keywords.length()<=0){
								keywords="";
							  }
						      if(keywords.length()>500){
								errorFlag = true;
								err = new String[]{"1","10"};
						      }
						  }
						  else if (parm.equalsIgnoreCase(Defines.FILE_FORM_NAME_NOTIFY_OPTION)) {
							System.out.println("notify VALUE= "+value);
						     notify = value;
						     sNotify = sNotify + notify + ",";
						     vNotify.addElement(notify);
						  }
						  else if (parm.equalsIgnoreCase("ibmonly")) {
							sIbmOnly = value;
							if (sIbmOnly != null){
							  ibmonly = sIbmOnly.trim().charAt(0);
							}
							else{
							  ibmonly = Defines.ETS_PUBLIC;	
							}
						  }
						 else if (parm.equalsIgnoreCase("action")) {
							action = value;
						  }
						 else if (parm.equalsIgnoreCase("proj")) {
							projectid = value;
					     }
			  			 else if (parm.equalsIgnoreCase("cc")) {
							currentcatid = value;
						 }
						 else if (parm.equalsIgnoreCase("docid")) {
							olddocid = (new Integer(value)).intValue();
						 }
						 else if (parm.equalsIgnoreCase("tc")) {
							topcatid = value;
						 }
						 else if (parm.equalsIgnoreCase("linkid")) {
						   linkid = value;
						 }
						 else if (parm.equalsIgnoreCase("meetingid")) {
						   meetingid = value;
						 }
						else if (parm.equalsIgnoreCase("repeatid")) {
						   repeatid = value;
						 }
						 else if (parm.equalsIgnoreCase("options")) {
						   options = value;
						 }
						else if (parm.equalsIgnoreCase("exdate")) {
						  exDateStr = value;
						}
						else if (parm.equalsIgnoreCase("exmonth")) {
						  exMonthStr = value;
						}
						else if (parm.equalsIgnoreCase("exday")) {
							exDayStr = value;
						}
						else if (parm.equalsIgnoreCase("exyear")) {
							exYearStr = value;
						}
						else if (parm.equalsIgnoreCase("self")) {
							System.out.println("SELF1="+value);
							selfid = value;
						 }
						else if (parm.equalsIgnoreCase("set")) {
							System.out.println("SET1="+value);
							setid = value;
						 }
						else if (parm.equalsIgnoreCase("chusers")) {
						  	sIsResUser = value;
						}
						else if (parm.equalsIgnoreCase("res_users")) {
							resUsers = value;
							vResUsers.addElement(resUsers);
						}
						else if (parm.equalsIgnoreCase("notifyOption")) {
							System.out.println("hereerhehehr="+value);
							sNotifyOptions = value;
						}
						else if (parm.equalsIgnoreCase("notifyall")) {
							sNotifyAll = value;
						}
												 
		              }
		            }

				if(!sIsResUser.equals("") && !vResUsers.contains(es.gIR_USERN)){
					String userRole = ETSUtils.checkUserRole(es,projectid);
					if(!userRole.equals(Defines.ETS_ADMIN) && !userRole.equals(Defines.ETS_EXECUTIVE))
						vResUsers.addElement(es.gIR_USERN);
				}

				if ((!sIsResUser.equals("")) && (ibmonly != Defines.ETS_PUBLIC)){
					vResUsers =	getIBMMembers(vResUsers,conn);
				}
				
		
				if (!exDateStr.equals("")){
					int vDate = verifyDate(exMonthStr,exDayStr,exYearStr);
					if (vDate == 1){
						errorFlag = true;
						err  = new String[]{"1","6"};
					}
					else if (vDate == 2){
						errorFlag = true;
						err  = new String[]{"1","7"};
					} 
				}
		
		
		
				if (!errorFlag){
				    if (inStream == null){
						System.out.println("writeDocument -- Input stream not found for file.");
						errorFlag = true;
						err = new String[]{"1","Input connection for file not found, please try again"};
						//return err;
				    }
				    
				   
				    try {
						inStreamAvail = inStream.available();
						if (inStreamAvail > (100000000)){
					    	System.out.println("writeDocument -- File over 100 Meg limit.");
					    	//String[] err = {"0","The File is over the 100MB limit.  Please use the DropBox for this file."};
							errorFlag = true;
							err = new String[]{"1","3"};
					    	//return err;
						}
				    }
				    catch (IOException ioe) {
						System.out.println("ioe ex for instreamavail(). e="+ioe);
						//String[] err = {"1","Error occurred, please try again"};
						errorFlag = true;
						err = new String[]{"1","4"};
						//return err;
				    }
				    System.out.println("writeDocument -- Input stream reports " + inStreamAvail + " bytes available.");
				}
				
			
		
				if (action.equals("updatedoc2")){
					action_meeting = "action=updatedoc";
					doctype = Defines.DOC;
					parentId =  (new Integer(currentcatid)).intValue();
				}
				else if (action.equals("replacedoc2")){
					action_meeting = "action=replacedoc";
					doctype = Defines.DOC;
					parentId =  (new Integer(currentcatid)).intValue();
				}
				else if (action.equals("addmeetingdoc2")){
				  olddocid = -1;
				  action1 = "addmeetingdoc";
				  doctype = Defines.MEETING;
				  meeting_id = meetingid;
				  repeat_id = repeatid;
				  action_meeting="action="+action1+"&meetingid="+meeting_id;
				  
				  // 5.4.1 - For Meetings Doc.
				  if (!StringUtil.isNullorEmpty(currentcatid)) {
					parentId =  (new Integer(currentcatid)).intValue();
				  }
				  else {
					parentId = -2;
					// Means the Meeting Doc may have been submitted for a
					// Meeting which existed before 5.4 cutover. All such
					// Documents will be kept in the Previous Meeting Docs 
					// folder
					DocumentDAO udDAO = new DocumentDAO();
					ETSCat udCat = null;
					try {
						udDAO.prepare();
						udCat = 
							udDAO.getMeetingsCatByName(
								DocConstants.PREV_MEETINGS_DOC_FOLDER, 
								projectid);
						if (udCat == null) {
							// We have to create this folder as well
							udCat = udDAO.getCatByName("Documents", projectid);
							if (udCat == null) {
								udCat = new ETSCat();
							}
							udCat.setName(DocConstants.PREV_MEETINGS_DOC_FOLDER);
							udCat.setIbmOnly(DocConstants.ETS_PUBLIC);
							udCat.setUserId(es.gIR_USERN);
							udCat.setParentId(udCat.getId());
							udCat.setDisplayFlag(DocConstants.IND_NO);

							String strResult[] = udDAO.addCat(udCat);
							if (strResult != null
								&& strResult.length == 2
								&& !StringUtil.isNullorEmpty(strResult[1])) {
								udCat.setId(Integer.parseInt(strResult[1]));
							}
						}
					}
					catch(Exception e) {
						e.printStackTrace(System.err);
					}
					finally {
						udDAO.cleanup();
					}
					parentId = udCat.getId();
				  }
				  Vector vtCalendar = 
				  	ETSCalendar.getMeetingsDetails(conn, projectid, meeting_id);
				  if (vtCalendar != null && vtCalendar.size() > 0) {
				  	ETSCal udCalendar = (ETSCal) vtCalendar.get(0);
				  	if (udCalendar != null) {
				  		String strInvitees = udCalendar.getSInviteesID();
				  		if (strInvitees != null && strInvitees.length() > 0) {
				  			StringTokenizer strTokens = new StringTokenizer(strInvitees, ",");
							if (strTokens.countTokens() > 0) {
								sIsResUser = "Y";
								vResUsers = new Vector();
								while (strTokens.hasMoreTokens()) {
									vResUsers.add(strTokens.nextToken());
								}
								vResUsers.add(udCalendar.getSScheduleBy());
							}
				  		}
				  	}
				  }
				  
				  
		  	   	}
			   	else if (action.equals("addprojectplan2")){
				  olddocid = -1;
				  action1 = "addprojectplan";
				  parentId =  -1;
				  doctype = Defines.PROJECT_PLAN;
				  meeting_id="";
				  action_meeting="action="+action1+"&etsop=addprojectplan";
			   	}
				else if (action.equals("addtaskdoc2")){
					action1 = "addtaskdoc";
					parentId =  -3;
					doctype = Defines.TASK;
					meeting_id= meetingid;
					cv_id=selfid;
					String cvStr = "";
					if (!selfid.equals(""))
						cvStr = "&self="+selfid+"&etsop=action";
					else if (!setid.equals("")){
						cv_id=setid;
						cvStr = "&set="+setid+"&etsop=action";
					}
					
					action_meeting="action="+action1+"&taskid="+meeting_id+cvStr;
				}
				else if (action.equals("addactionplan2")){
				 olddocid = -1;
				 action1 = "addactionplan";
				 parentId = -4;
				 doctype = Defines.SETMET_PLAN;
				 meeting_id=meetingid;
				 action_meeting="action="+action1+"&setmet="+meeting_id;
				}
			  	else{  //doc
				  olddocid = -1;
				  action1 = "adddoc";
				  parentId =  (new Integer(currentcatid)).intValue();
				  doctype = Defines.DOC;
				  meeting_id="";
				  action_meeting="action="+action1;
			  	}
		        
		    }
		    catch(Exception e){
				//err = new String[]{"1","Error occurred, please try again"};
				err = new String[]{"1","0"};
				System.out.println("error in etsconup:: e="+e);
				e.printStackTrace();
				errorFlag = true;
				//return err;
		    }               	


			if(errorFlag){
				if (action.equals("adddoc2") || action.equals("updatedoc2")){
					req.getSession(true).setAttribute("ETSDocName",docname);
					req.getSession(true).setAttribute("ETSDocDesc",docdesc);
					req.getSession(true).setAttribute("ETSDocKeywords",keywords);
					req.getSession(true).setAttribute("ETSDocIbmOnly",String.valueOf(ibmonly));
					req.getSession(true).setAttribute("ETSDocExDate",exDateStr);
					req.getSession(true).setAttribute("ETSDocExMonth",exMonthStr);
					req.getSession(true).setAttribute("ETSDocExDay",exDayStr);
					req.getSession(true).setAttribute("ETSDocExYear",exYearStr);
					req.getSession(true).setAttribute("ETSChUsers",sIsResUser);
					req.getSession(true).setAttribute("ETSResUsers",vResUsers);
					req.getSession(true).setAttribute("ETSNotifyOptions",sNotifyOptions);
					req.getSession(true).setAttribute("ETSNotifyAll",sNotifyAll);
					req.getSession(true).setAttribute("ETSNotifyUsers",vNotify);
				}					
				//resp.sendRedirect(Defines.SERVLET_PATH+"ETSProjectsServlet.wss?"+action_meeting+"&proj="+projectid+"&tc="+topcatid+"&cc="+currentcatid+"&msg="+err[1]+"&linkid="+linkid);
				String strError = "ETSProjectsServlet.wss?"+action_meeting+"&proj="+projectid+"&tc="+topcatid+"&cc="+currentcatid+"&docid="+olddocid+"&msg="+err[1]+"&linkid="+linkid;
				resp.sendRedirect(strError);
			}
			else{
				boolean bIsUpdateDoc = false;
				try{
					if (action.equals("updatedoc2") || action.equals("replacedoc2")){
						bIsUpdateDoc = true;
						doc = ETSDatabaseManager.getDocByIdAndProject(olddocid,projectid);
						olddocIbmOnly = String.valueOf(doc.getIbmOnly());
						olddocDPrivate = doc.getDPrivate();
					}
					else{ 
						doc = new ETSDoc();
						System.out.println("id = "+doc.getId());
						doc.setProjectId(projectid);
						doc.setCatId(parentId);
						doc.setDocType(doctype);
						doc.setSelfId(cv_id);
						
						
						if (repeat_id.trim().equals("")) {
							doc.setMeetingId(meeting_id);   
						} else {
							doc.setMeetingId(repeat_id);
						}
						if (options.equals(String.valueOf(Defines.DOC_DRAFT)))
							doc.setDocStatus(Defines.DOC_DRAFT);
						else if (options.equals(String.valueOf(Defines.DOC_SUB_APP)))
							doc.setDocStatus(Defines.DOC_SUB_APP);
						else
							doc.setDocStatus("");
						
					}
			
					if(!exDateStr.equals("")){
						doc.setExpiryDate(exMonthStr,exDayStr,exYearStr);	
					}
					else{
						doc.setExpiryDate(new Timestamp(0));	
					}
					
					doc.setUserId(es.gIR_USERN);
				    doc.setName(docname);
				    doc.setDescription(docdesc);
				    doc.setKeywords(keywords);
				    doc.setIbmOnly(ibmonly);
				    doc.setSize(inStreamAvail);
				    doc.setFileName(fileName);
					if(!sIsResUser.equals("")){
						doc.setDPrivate(true);
					}
					else{
						doc.setDPrivate(false);	
					}
					
				    boolean success = false;
				    if (action.equals("addprojectplan2")){
						//need to delete old project plan frist then add this one
						success = ETSDatabaseManager.deleteProjectPlan(doc.getProjectId(),es.gIR_USERN);
					
						if (success){
					    	success = ETSDatabaseManager.addDocMethod(doc,inStream, olddocid);
						}
						else{
						    System.out.println("project plan was not deleted");
						}
				    }
					else if (action.equals("addactionplan2")){
							//need to delete old action plan frist then add this one
							success = ETSDatabaseManager.deleteActionPlan(doc.getProjectId(),es.gIR_USERN);
		    				
							if (success){
									success = ETSDatabaseManager.addDocMethod(doc,inStream, olddocid);
							}
							else{
									System.out.println("action plan was not deleted");
							}
					}
					else if (action.equals("replace2")){
						//success = ETSDatabaseManager.replaceDocMethod(doc,inStream,olddocid);		
					}
				    else{
						success = ETSDatabaseManager.addDocMethod(doc,inStream, olddocid);
						
				    }
				
				    System.out.println("success = "+ String.valueOf(success));
			
			
			
				    if (success){
						if (action.equals("updatedoc2")){
							int id_temp = doc.getId();
							String sibmonly = String.valueOf(ibmonly);
							//if ((!olddocIbmOnly.equals(sibmonly)) || (!olddocDPrivate.equals(doc.getDPrivate()))){
							//	ETSDatabaseManager.updateDocProp(id_temp,ibmonly,doc.getDPrivate(),conn);
							//}
							
							// sandra
							String add = "";
							String remove = "";
							Vector vAdd = new Vector();
							boolean changeRes = !olddocDPrivate.equals(doc.getDPrivate());
							
							if (changeRes || doc.IsDPrivate()){
								if(changeRes && !doc.IsDPrivate()){ //remove all
									System.out.println("IN REMOVE ALL");
									Vector resu = ETSDatabaseManager.getRestrictedProjMemberIds(projectid,olddocid,false);
									for (int i=0;i<resu.size();i++){
										if (i!=0)
											remove = remove+",'"+resu.elementAt(i)+"'";
										else
											remove = "'"+resu.elementAt(i)+"'";
									}		
								}
								else if (changeRes && doc.IsDPrivate()){ //add all
									System.out.println("IN ADD ALL");
									for (int i=0;i<vResUsers.size();i++){
										if (i!=0)
											add = add+",("+doc.getId()+",'"+vResUsers.elementAt(i)+"','"+projectid+"')";	
										else
											add = add+"("+doc.getId()+",'"+vResUsers.elementAt(i)+"','"+projectid+"')";
										vAdd.addElement(vResUsers.elementAt(i));
									}					
								}
								else if (!changeRes && doc.IsDPrivate()){ //add and remove
									System.out.println("IN ADD/REMOVE");
									Vector allu = ETSDatabaseManager.getProjMembers(projectid);
									Vector resu = ETSDatabaseManager.getRestrictedProjMemberIds(projectid,olddocid,false);
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

							}
							
							if (!doc.IsDPrivate())
								vResUsers = new Vector();
							
							ETSDatabaseManager.updateDocProp(doc, !olddocIbmOnly.equals(sibmonly),!olddocDPrivate.equals(doc.getDPrivate()),vResUsers,add,remove, vAdd,conn, bIsUpdateDoc);
							
							String[] res_done = updateDoc2(projectid,parentId,(new Integer(id_temp)).toString(),sNotify,sNotifyOptions,sNotifyAll,ibmonly,vResUsers, sIsResUser, topcatid,currentcatid,linkid,es,resp,conn);
							String res_s = (String)res_done[0];
							String msg_s = (String)res_done[1];
							if(res_s.equals("1")){ //error
								resp.sendRedirect("ETSProjectsServlet.wss?action=updatedoc&proj="+projectid+"&tc="+topcatid+"&cc="+currentcatid+"&msg="+msg_s+"&linkid="+linkid);
							}
							else{
								if(msg_s.equals("success")){
									resp.sendRedirect("ETSProjectsServlet.wss?action=details&proj="+projectid+"&tc="+topcatid+"&cc="+currentcatid+"&docid="+id_temp+"&linkid="+linkid);
								}
								else{
									resp.sendRedirect("ETSProjectsServlet.wss?action=updatedocerror&proj="+projectid+"&tc="+topcatid+"&cc="+currentcatid+"&docid="+id_temp+"&msg="+msg_s+"&linkid="+linkid);
								}
							}
						
						}
				    	else{  //add
							String id_temp = (new Integer(doc.getId())).toString();
							
							if (!sIsResUser.equals("")){
								ETSDatabaseManager.addDocResUsers(vResUsers,id_temp,projectid);
							}
							
							
							String[] res_done =addDoc2(projectid,parentId,id_temp,sNotify,sNotifyOptions,sNotifyAll,action,meeting_id,cv_id,ibmonly, vResUsers, sIsResUser,topcatid,currentcatid,linkid,es,conn);
						   	String res_s = (String)res_done[0];
						   	String msg_s = (String)res_done[1];
						   	if(res_s.equals("1")){
							   resp.sendRedirect("ETSProjectsServlet.wss?"+action_meeting+"&proj="+projectid+"&tc="+topcatid+"&cc="+currentcatid+"&msg="+msg_s+"&linkid="+linkid);
							}
						   	else{
							   if (action.equals("addmeetingdoc2")){
								   resp.sendRedirect("ETSProjectsServlet.wss?proj="+projectid+"&tc="+topcatid+"&cc="+currentcatid+"&etsop=viewmeeting&meetid="+meeting_id+"&linkid="+linkid);
							   }
							   else if (action.equals("addtaskdoc2")){
							   		String cvStr = "";
							   		if(!selfid.equals(""))
							   			cvStr = "&self="+selfid+"&etsop=action";
									else if(!setid.equals(""))
										cvStr = "&set="+setid+"&etsop=action";
							   		
							   		System.out.println("SELF3="+cvStr);
								   resp.sendRedirect(Defines.SERVLET_PATH+"ETSProjectsServlet.wss?action=details&taskid="+meeting_id+"&proj="+projectid+cvStr+"&tc="+topcatid+"&cc="+currentcatid+"&linkid="+linkid);
							   }
							   else if (action.equals("addactionplan2")){
									resp.sendRedirect("ETSProjectsServlet.wss?proj="+projectid+"&tc="+topcatid+"&cc="+currentcatid+"&etsop=action&set="+meeting_id+"&linkid="+linkid);
							   }
							   else{
								   //System.out.println("ETSProjectsServlet.wss?proj="+projectid+"&tc="+topcatid+"&cc="+currentcatid+"&linkid="+linkid);
								   resp.sendRedirect("ETSProjectsServlet.wss?proj="+projectid+"&tc="+topcatid+"&cc="+currentcatid+"&linkid="+linkid);
							   }
						   	}
				    	}
						
				    }
				    else{
				    	System.out.println("there was an error");
						err = new String[]{"1","5"};
						//resp.sendRedirect(Defines.SERVLET_PATH+"ETSProjectsServlet.wss?"+action_meeting+"proj="+projectid+"&tc="+topcatid+"&cc="+currentcatid+"&msg="+err[1]+"&linkid="+linkid);
						resp.sendRedirect("ETSProjectsServlet.wss?"+action_meeting+"&proj="+projectid+"&tc="+topcatid+"&cc="+currentcatid+"&msg="+err[1]+"&linkid="+linkid);
						//return err;
				    }
				
				  }
				catch(Exception e){
					//resp.sendRedirect(Defines.SERVLET_PATH+"ETSProjectsServlet.wss?"+action_meeting+"proj="+projectid+"&tc="+topcatid+"&cc="+currentcatid+"&msg=0&linkid="+linkid);
					resp.sendRedirect("ETSProjectsServlet.wss?"+action_meeting+"&proj="+projectid+"&tc="+topcatid+"&cc="+currentcatid+"&msg=0&linkid="+linkid);
				}
		
		   	}
		}
		catch(Exception e){
			System.out.println("Error in uploadservlet ="+e);
			e.printStackTrace();
			err = new String[]{"1","0"};
			resp.sendRedirect("ETSProjectsServlet.wss?"+action_meeting+"&proj="+projectid+"&tc="+topcatid+"&cc="+currentcatid+"&msg="+err[1]+"&linkid="+linkid);
		}             		  
   		finally{
	   		ETSDBUtils.close(conn);
		}
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




	public String[] addDoc2(String projectid, int parentid, String docidStr, String notify, String notifyOpt, String notifyAll,String action, String meeting_id, String self_id,char ibmonly,  Vector resUsers, String sIsResUser,String topcat, String current, String linkid, EdgeAccessCntrl es, Connection conn){

	try{
		Vector p = new Vector();
		if ((ETSUtils.checkUserRole(es,projectid)).equals(Defines.ETS_ADMIN)){
			p = ETSDatabaseManager.getProject(projectid);
		}
		else{
			p = ETSDatabaseManager.getProjects(es.gIR_USERN,projectid);
		}
		
		int docid = (new Integer(docidStr)).intValue();

		if (p.size() <= 0){
			//writer.println("error occurred: invalid project id for this user.");
			//System.out.println("put bad projet id message here");
		}
		else{
			ETSProj proj  = (ETSProj)p.elementAt(0);
			ETSDoc doc = ETSDatabaseManager.getDocByIdAndProject(docid,proj.getProjectId());
			ETSCat parent_cat;
			/*
			if (action.equals("adddoc2")){
				parent_cat = databaseManager.getCat(parentid);
			}
			*/

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
			
			if (!notifyAll.equals("")){
				Vector memb_temp = ETSDatabaseManager.getProjMembers(proj.getProjectId(),true);
				for (int i =0; i<memb_temp.size();i++){
					System.out.println("1:"+((ETSUser)memb_temp.elementAt(i)).getUserId());
					members.addElement(((ETSUser)memb_temp.elementAt(i)).getUserId());
				}
			}
			else if (!notify.equals("")){
				notify = notify.substring(0,notify.length()-1);
				System.out.println("snotify= "+notify);
				StringTokenizer st = new StringTokenizer(notify, ",");
				while (st.hasMoreTokens()){
					String uid = st.nextToken();
					members.addElement(uid);
				}
			}
			
			
			if (members.size() >0){
				members = getAuthorizedMembers(members,resUsers,ibmonly,sIsResUser,conn);

				SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
				java.util.Date date = new java.util.Date(doc.getUploadDate());
				String dateStr=df.format(date);

				AccessCntrlFuncs acf = new AccessCntrlFuncs();
				//Vector members =databaseManager.getProjMembers(proj.getProjectId());

				String emailids = "";
				StringBuffer message = new StringBuffer();

				message.append("\n\n");
				if (doc.getDocStatus()==Defines.DOC_SUB_APP)
					message.append("A new document was added to the project for your approval: \n");
				else
					message.append("A new document was added to the project: \n");
				message.append(proj.getName()+" \n\n");
				message.append("The details of the document are as follows: \n\n");
				message.append("==============================================================\n");

				message.append("  Name:           " + ETSUtils.formatEmailStr(doc.getName()) + "\n");
				message.append("  Description:    " + ETSUtils.formatEmailStr(doc.getDescription()) + "\n");
				message.append("  Keywords:       " + ETSUtils.formatEmailStr(doc.getKeywords()) + " \n");
				message.append("  Author:         " + ETSUtils.getUsersName(conn, doc.getUserId()) + "\n");
				message.append("  Date:           " + dateStr + " (mm/dd/yyyy)\n\n");
				
				if (doc.getExpiryDate()!=0){
					java.util.Date exdate = new java.util.Date(doc.getExpiryDate());
					String exdateStr=df.format(exdate);
					message.append("  Exp Date:       " + exdateStr + " (mm/dd/yyyy)\n\n");
				}
				
				if (ibmonly==Defines.ETS_IBM_ONLY || ibmonly==Defines.ETS_IBM_CONF){
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

				
				/*
				if (ibmonly==Defines.ETS_IBM_CONF || ibmonly==Defines.ETS_IBM_ONLY){
					members = getIBMMembers(members,conn);
				}
				*/
				
				if (members.size() >0){
					for (int i = 0; i<members.size();i++){
						//get amt information
						String memb = (String)members.elementAt(i);
						try{
							String userEmail = ETSUtils.getUserEmail(conn,memb);
							emailids = emailids + userEmail +",";
						}
						catch(AMTException ae){
							//writer.println("amt exception caught. e= "+ae);
						}
					}

					System.out.println("******emailids ="+emailids);
					String subject = "E&TS Connect - New Document: "+doc.getName();
					subject = ETSUtils.formatEmailSubject(subject);

					String toList = "";
					String bccList = "";
					//emailids = "sandieps@us.ibm.com";
					
					if(notifyOpt.equals("bcc")){
						bccList = emailids;	
					}
					else{
						toList = emailids;
					}
					
					boolean bSent = false;

					if (!toList.trim().equals("") || !bccList.trim().equals("")) {
						
						bSent = ETSUtils.sendEMail(es.gEMAIL,toList,"",bccList,Global.mailHost,message.toString(),subject,es.gEMAIL);
					}

					if (bSent){
						ETSDatabaseManager.addEmailLog("Document",String.valueOf(doc.getId()),"Add document",es.gIR_USERN,proj.getProjectId(),subject,toList,"");
					}
					else{
						System.out.println("Error occurred while notifying project members.");
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
		
			if (action.equals("addtaskdoc2")){
				addTaskDocEmail(meeting_id,self_id,proj,doc,topcat,current,linkid,es,conn);	
			}	
		}
		return new String[]{"0","success"};
	}
	catch(Exception e) {
		//writer.println("error occurred");
		System.out.println("error here "+e);
		return new String[]{"1","8"};
	}
	}
	
	private Vector getIBMMembers(Vector membs, Connection conn){
		Vector new_members = new Vector();

		for (int i = 0; i<membs.size();i++){
			String mem = (String)membs.elementAt(i);
			try{
				String edge_userid = AccessCntrlFuncs.getEdgeUserId(conn,mem);
				String decaftype = AccessCntrlFuncs.decafType(edge_userid,conn);
				if (decaftype.equals("I")){
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
	
	private Vector getAuthorizedMembers(Vector membs, Vector resusers, char ibmonly, String sIsRestricted, Connection conn){
		Vector notify_members = new Vector();
		
		boolean internal = false;
		
		System.out.println("sir="+sIsRestricted);
		
		if ((!sIsRestricted.equals("")) || (ibmonly != Defines.ETS_PUBLIC)){ 
			for (int i = 0; i<membs.size();i++){
				String mem = (String)membs.elementAt(i);
				try{
					internal = false;
					String edge_userid = AccessCntrlFuncs.getEdgeUserId(conn,mem);
					String decaftype = AccessCntrlFuncs.decafType(edge_userid,conn);
					if (decaftype.equals("I")){
						internal = true;				
					}
					
					if ((sIsRestricted.equals("")||resusers.contains(mem)) && (ibmonly==Defines.ETS_PUBLIC || internal)){
						System.out.println("2:"+mem);
						notify_members.addElement(mem);	
					} 
					
				}
				catch(AMTException a){
					System.out.println("amt exception in getibmmembers err= "+a);
				}
				catch(SQLException s){
					System.out.println("sql exception in getibmmembers err= "+s);
				}
			}
		}
		else{
			notify_members = membs;
		}
		
		System.out.println("nmsize="+notify_members.size());
		
		return notify_members;
	}

	private void addTaskDocEmail(String taskid,String selfId,ETSProj p,ETSDoc doc,String tcid, String ccid, String linkid,EdgeAccessCntrl es, Connection conn){
				
		
		try{
			ETSTask t = ETSDealTrackerDAO.getTask(taskid,p.getProjectId(),selfId);
			
			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
			java.util.Date duedate = new java.util.Date(t.getDueDate());
			java.util.Date cdate = new java.util.Date(t.getCreatedDate());
			java.util.Date now =  new java.util.Date();
			String duedateStr=df.format(duedate);
			String cdateStr=df.format(cdate);
			String nowStr=df.format(now);
	   
			StringBuffer message = new StringBuffer();

			message.append("\n\n");
			message.append("A document has been added to a task in the project: \n");
			message.append(p.getName()+" \n\n");
			message.append("The details of task are now as follows: \n\n");
			message.append("==============================================================\n");
		
			if (t.getTrackerType().equals("A"))
				message.append(" Self Assessment: " + ETSUtils.formatEmailStr(ETSDealTrackerDAO.getSelfAssessTitle(t.getProjectId(),t.getSelfId())) + "\n");
			else if (t.getTrackerType().equals("M"))
				message.append(" Set/Met:         " + ETSUtils.formatEmailStr(ETSDealTrackerDAO.getSetMetTitle(t.getProjectId(),t.getSelfId())) + "\n");
			else 
				message.append(" Task Id:         " + ETSUtils.formatEmailStr(taskid) + "\n");
			
			message.append(" Title:           " + ETSUtils.formatEmailStr(t.getTitle()) + "\n");
			if(t.getTrackerType().equals("D"))
				message.append(" Section:         " + ETSUtils.formatEmailStr(t.getSection()) + " \n");
			else
				message.append(" Attribute:       " + ETSUtils.formatEmailStr(t.getSection()) + " \n");
			message.append(" Due Date:        " + duedateStr + " (mm/dd/yyyy)\n\n");
			
			message.append(" Created By:      " + ETSUtils.getUsersName(conn, t.getCreatorId()) + "\n");
			message.append(" Created Date:    " + cdateStr + " (mm/dd/yyyy)\n\n");
			
			message.append(" Owner:           " + ETSUtils.getUsersName(conn, t.getOwnerId()) + "\n");
			if(t.getTrackerType().equals("D"))
				message.append(" Work Required:   " + ETSUtils.formatEmailStr(t.getWorkRequired()) + " \n");
			message.append(" Action Required: " + ETSUtils.formatEmailStr(t.getWorkRequired()) + "\n\n");
			
			if (t.isIbmOnly()){
				message.append("  This task is marked IBM Only\n\n");
			}

			message.append(" Document Name:   " + ETSUtils.formatEmailStr(doc.getName())  + "\n");
			message.append(" Uploaded By:     " + ETSUtils.getUsersName(conn, es.gIR_USERN) + "\n\n");

			String cvStr = "";
			if(t.getTrackerType().equals("A"))
				cvStr = "&self="+selfId+"&etsop=action";
			else
				cvStr = "&set="+selfId+"&etsop=action";
			
			message.append("To view this document , click on the following  URL:  \n");
			String url = Global.getUrl("ets/ETSProjectsServlet.wss")+"?action=details&taskid="+taskid+cvStr+"&proj="+p.getProjectId()+"&tc="+tcid+"&cc="+ccid+"&linkid="+linkid;
			message.append(url+"\n\n");
		
			message.append("==============================================================\n");
			message.append("Delivered by E&TS Connect.\n");
			message.append("This is a system generated email. \n");
			message.append("==============================================================\n\n");
		
		
			String emailids = "";	
			try{
				String cUserEmail = ETSUtils.getUserEmail(conn,t.getCreatorId());
				String oUserEmail = ETSUtils.getUserEmail(conn,t.getOwnerId());
				String mUserEmail = ETSUtils.getUserEmail(conn,es.gIR_USERN);
				emailids = cUserEmail;
				if (!t.getCreatorId().equals(t.getOwnerId())){
					emailids = emailids+","+oUserEmail;
				}
				if (!t.getCreatorId().equals(es.gIR_USERN) && !t.getOwnerId().equals(es.gIR_USERN)){
					emailids = emailids+","+mUserEmail;
				}
							
			}
			catch(AMTException ae){
				//writer.println("amt exception caught. e= "+ae);
			}
		
			String subject = "E&TS Connect - Task document added to: "+t.getTitle();
			subject = ETSUtils.formatEmailSubject(subject);
		
			String toList = "";
			toList = emailids;
			//toList = "sandieps@us.ibm.com";
			boolean bSent = false;
		
			if (!toList.trim().equals("")) {
				bSent = ETSUtils.sendEMail(es.gEMAIL,toList,"","", Global.mailHost,message.toString(),subject,es.gEMAIL);
			}
		
			if (bSent){
				ETSDatabaseManager.addEmailLog("Task",String.valueOf(taskid),"Add task document",es.gIR_USERN,p.getProjectId(),subject,toList,"");
			}
			else{
				System.out.println("Error occurred while notifying owner,submitter of new task comment.");
			}
		}
		catch(Exception e){
			
		}
    }



	public String[] updateDoc2(String projectid, int parentid, String docidStr, String notify, String notifyOpt, String notifyAll,char ibmonly, Vector vResUsers, String sIsResUser, String topcat, String current, String linkid, EdgeAccessCntrl es, HttpServletResponse response, Connection conn){
	String res_1 = "";
	String res_2 = "";

	try{
		//Vector p = ETSDatabaseManager.getProjects(es.gIR_USERN,projectid);
		Vector p = new Vector();
		if ((ETSUtils.checkUserRole(es,projectid)).equals(Defines.ETS_ADMIN)){
			p = ETSDatabaseManager.getProject(projectid);
		}
		else{
			p = ETSDatabaseManager.getProjects(es.gIR_USERN,projectid);
		}

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
				if (!notifyAll.equals("")){
					Vector memb_temp = ETSDatabaseManager.getProjMembers(proj.getProjectId(),true);
					for (int i =0; i<memb_temp.size();i++){
						members.addElement(((ETSUser)memb_temp.elementAt(i)).getUserId());
					}
				}
				else if (!notify.equals("")){
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
					//Vector members = databaseManager.getProjMembers(proj.getProjectId());
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
					
					if (doc.getExpiryDate()!=0){
						java.util.Date exdate = new java.util.Date(doc.getExpiryDate());
						String exdateStr=df.format(exdate);
						message.append("  Exp Date:       " + exdateStr + " (mm/dd/yyyy)\n\n");
					}
					//if (sIbmOnly.equals(String.valueOf(ETSDatabaseManager.TRUE_FLAG))){
					if (ibmonly==Defines.ETS_IBM_CONF || ibmonly==Defines.ETS_IBM_ONLY){
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

					//if (sIbmOnly.equals(String.valueOf(ETSDatabaseManager.TRUE_FLAG))){
					/*if (ibmonly==Defines.ETS_IBM_CONF || ibmonly==Defines.ETS_IBM_ONLY){
						members = getIBMMembers(members,conn);
					}*/

					members = getAuthorizedMembers(members,vResUsers,ibmonly,sIsResUser,conn);

					if (members.size() >0){
						for (int i = 0; i<members.size();i++){
							String memb = (String)members.elementAt(i);
							try{
								String userEmail = ETSUtils.getUserEmail(conn,memb);
								emailids = emailids + userEmail +",";
							}
							catch(AMTException ae){
								//writer.println("amt exception caught. e= "+ae);
							}
						}

						String subject = "E&TS Connect - Document Updated: "+doc.getName();
						subject = ETSUtils.formatEmailSubject(subject);

						System.out.println("******** upemailids="+emailids);
						String toList = "";
						String bccList = "";
						//emailids = "sandieps@us.ibm.com";
						
						if(notifyOpt.equals("bcc")){
							bccList = emailids;	
						}
						else{
							toList = emailids;
						}
					
						boolean bSent = false;

						if (!toList.trim().equals("")|| !bccList.trim().equals("")) {
							//sendEMail(String from, String to, String sCC,String bcc String host, String sMessage, String Subject, String reply)
							bSent = ETSUtils.sendEMail(es.gEMAIL,toList,"",bccList, Global.mailHost,message.toString(),subject,es.gEMAIL);
						}

						if (bSent){
							 //addEmailLog(String mail_type, String key1, String key2, String key3, String project_id, String subject, String to, String cc)
							ETSDatabaseManager.addEmailLog("Document",String.valueOf(doc.getId()),"Update document",es.gIR_USERN,proj.getProjectId(),subject,toList,"");
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




	private int verifyDate(String smonth,String sday,String syear){
		String[] s = new String[]{"0","fine"};
		
		//date
		System.out.println("month="+smonth);
		System.out.println("day="+sday);
		System.out.println("year="+syear);
		
		if (smonth.equals("") || smonth.equals("-1")){
			s[0]="1";
			s[1]="Input Error: \"A valid month for expriation date must be chosen.\"";
			return 1;
		}
		else if (sday.equals("") || sday.equals("-1")){
			s[0]="1";
			s[1]="Input Error: \"A valid day for expriation date must be chosen.\"";
			return 1;
		}
		else if (syear.equals("") || syear.equals("-1")){
			s[0]="1";
			s[1]="Input Error: \"A valid year for expriation date must be chosen.\"";
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
				s[0]="0";
				s[1]="Input Error: \"Invalid expriation date specified.  Please review date entered.\"";
				return 1;
			}
			
			if (cal.before(today)){
				s[0]="0";
				s[1]="Input Error: \"Invalid expriation date specified.  Please review date entered.\"";
				return 2;
			}			
		}
	
		return 0;
	}


}


//write to file
/*
int bytesRead = 0;
final byte[] buf = new byte[32768];
FileOutputStream out = null;
try{
out = new FileOutputStream("/afs/eda/u/sandieps/public/"+doc.getName());
}
catch (IOException ioe) {
System.out.println("ioe 0 ioe= "+ioe);
String[] err = {"0","ioe 0 ioe= "+ioe};
return err;
}
try {
System.out.println("in try inStream before while");
while ((bytesRead = inStream2.read(buf, 0, buf.length)) >= 0) {
	out.write(buf, 0, bytesRead);
	System.out.println("bytesRead="+bytesRead);
}
}
catch (IOException ioe) {
System.out.println("ioe 1 ioe="+ioe);
String[] err = {"0","ioe 1 ioe= "+ioe};
return err;
}
finally{
if (out != null) {
	try {
	out.flush();
	out.close();
	}
	catch (IOException ioe) {
	System.out.println("ioe 2 ioe="+ioe);
	}
}
else{
	System.out.println("out == null");
}
}
*/
// end of writting to file





/* moved to ETSDatabaseManager.java

    synchronized private boolean addDocMethod(ETSDoc doc, InputStream inStream){
	//DbConnect dbConnect = new DbConnect();
	boolean success = false;
	try{
	    //dbConnect.makeConn();
	    //Connection conn = dbConnect.conn;
	    int maxid = getMaxId(conn);
	    if (maxid == -1){
		System.out.println("error in maxid");
		return false;
	    }
	    doc.setId(maxid);
	    System.out.println("docid="+doc.getId());
	    success = addDoc(doc,conn);
	    if (!success){
		System.out.println("error in add doc");
		return false;
	    }
	    success = addDocFile(doc,inStream,conn);
	    if (!success){
		System.out.println("error in add doc file");
		return false;
	    }
	}
	catch(SQLException e) {
	    success = false;
	    System.err.println("error= "+e);
	    //dbConnect.removeConn(e);
	    //dbConnect = null;
	    throw e;
	}
	finally{
	    //if (dbConnect != null)
		//dbConnect.closeConn();
	    return success;
	}

    }

    private int getMaxId(Connection conn){
	ResultSet rs;
	int maxid = -1;
	try{
	    PreparedStatement statement = conn.prepareStatement("select max(doc_id) as id from ets.ets_doc");
	    rs = statement.executeQuery();
	    while(rs.next()){
		maxid = rs.getInt("id") + 1;
	    }
	    statement.close();
	}
	catch(SQLException e) {
	    maxid = -1;
	    System.err.println("error= "+e);
	    throw e;
	}
	finally{
	    return maxid;
	}
    }

    private boolean addDoc(ETSDoc doc, Connection conn){
	//DbConnect dbConnect = new DbConnect();
	boolean success = false;
	try{
	    //dbConnect.makeConn();
	    //Connection conn = dbConnect.conn;

	    PreparedStatement statement = conn.prepareStatement("insert into ets.ets_doc(doc_id,project_id,cat_id,user_id,doc_name,doc_description,doc_keywords,doc_size,doc_upload_date,doc_update_date,doc_publish_date) values(?,?,?,?,?,?,?,?,current timestamp,current timestamp,current timestamp)");

	    statement.setInt(1,doc.getId());
	    statement.setInt(2,doc.getProjectId());
	    statement.setInt(3,doc.getCatId());
	    statement.setString(4,doc.getUserId());
	    statement.setString(5,doc.getName());
	    statement.setString(6,doc.getDescription());
	    statement.setString(7,doc.getKeywords());
	    statement.setInt(8,doc.getSize());

	    statement.executeUpdate();
	    statement.close();
	    success = true;
	}
	catch(SQLException e) {
	    success = false;
	    System.err.println("sql error in add doc= "+e);
	    throw e;
	}
	finally{
	    return success;
	}
    }

    private boolean addDocFile(ETSDoc doc,InputStream inStream, Connection conn){
	boolean success = false;

	try{
	    //dbConnect.makeConn();
	    //Connection conn = dbConnect.conn;

	    PreparedStatement statement = conn.prepareStatement("insert into ets.ets_docfile(doc_id,docfile_name,docfile,docfile_size,docfile_update_date) values(?,?,?,?,current timestamp)");

	    statement.setInt(1,doc.getId());
	    statement.setString(2,doc.getFileName());
	    statement.setBinaryStream(3,inStream,doc.getSize());
	    statement.setInt(4,doc.getSize());
	    statement.executeUpdate();
	    statement.close();
	    success = true;
	}
	catch(SQLException e) {
	    success = false;
	    System.err.println("sql error in add doc file= "+e);
	    //dbConnect.removeConn(e);
	    //dbConnect = null;
	    throw e;
	}
	finally{
	    //if (dbConnect != null)
	    //	dbConnect.closeConn();
	    return success;
	}
    }

*/





