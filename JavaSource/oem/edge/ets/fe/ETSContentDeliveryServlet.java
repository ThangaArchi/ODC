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
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.documents.DocumentsHelper;
import oem.edge.ets.fe.documents.common.DocConstants;
import oem.edge.ets.fe.documents.data.DocumentDAO;
import oem.edge.ets.fe.pmo.ETSPMODao;
import oem.edge.ets.fe.pmo.ETSPMODoc;

public class ETSContentDeliveryServlet extends HttpServlet {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.10";


    protected ETSDatabaseManager databaseManager;


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
	handleRequest(request,response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	handleRequest(request,response);
    }


    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	EdgeAccessCntrl es = new EdgeAccessCntrl();
	String Msg = null;
	ETSDoc document = null;
	ETSPMODoc pmoDocument = null;
	Connection conn = null;
	boolean pmoFlag = false;
	String userRole = "";

	String idDocFile = req.getParameter("docfileid");
	
	DocumentDAO udDAO = new DocumentDAO();
	try{
	    conn = ETSDBUtils.getConnection();
		udDAO.setConnection(conn);

	    if (!es.GetProfile(resp, req, conn)){
			return;
	    }

		
		if (idDocFile == null || idDocFile.equals("")) {
			idDocFile = "1";
		}
		
		String idStr = req.getParameter("docid");
	   	if (idStr == null){
	   		idStr = req.getParameter("pmodocid");
			if (idStr != null)
				pmoFlag = true;
	   	}
	   	String projStr = req.getParameter("projid");

		// Check if this is project status request. If so, process and return
		String strProjStatus = req.getParameter("projstatus");
		if (strProjStatus != null && !strProjStatus.equals("")) {
			ETSFileContentHandler handler = null;
			String strSourceId = req.getParameter("source");
			String strDestId = req.getParameter("dest");
			// start delivery
			try{
				handler = new ETSFileContentHandler();
				if (handler != null){
					handler.deliverProjectStatus(req, resp, projStr, strSourceId, strDestId);
				}
			}
			catch (Exception ex){
				ex.printStackTrace(System.err);
				System.out.println("error in cdels 2:"+ex);
				return;
			}
			finally{
				handler = null;
			}
			return;
		}
		
		if (idStr != null && projStr != null){
			if (!pmoFlag){

				int docid = (new Integer(idStr)).intValue();
				String projid = projStr;

				document = udDAO.getDocByIdAndProject(docid,projid);
				
				if (document != null) { 
				System.out.println("document.getName="+document.getName());
				}

				// Check if project is ITAR Workspace. If it is then
				// just log the hit and re-direct to BTV server.
				ETSProj udProj = udDAO.getProjectDetails(projStr);
				if (udProj.isITAR()) {
					if (document == null) {
						document = udDAO.getITARDocByIdAndProject(docid, projid);
					}
					/*
					logHit(document.getId(),document.getProjectId(),es);
					*/
						String strEncode =
							DocumentsHelper.encode(idStr, idDocFile, projStr);

					ResourceBundle pdResources =
						ResourceBundle.getBundle("oem.edge.ets.fe.ets-itar");
					String strBTV =
						pdResources.getString("ets.doc.btv.server");

					resp.sendRedirect(
						strBTV 
								+ "ITARContentDeliveryServlet.wss?encodedToken="
								+ strEncode);
					return;
				}

	    	}
	    	else{
				String projid = projStr;

				ETSPMODao pmoDao = new ETSPMODao();
				pmoDocument = pmoDao.getPMODocument(conn,idStr,projid,false,"","");
				System.out.println("document.getName="+pmoDocument.getDocName());
	    		
	    	}
	    	if (document == null && pmoDocument == null){
				System.out.println("The document does not exist");
		   		return;
	    	}
		}
	   	else{
	    	System.out.println("idstr for projstr == null");
	       	return;
	   	}

	   if (document == null && pmoDocument == null){
	       System.out.println("error no document found");
	       return;
	   }


		if (!pmoFlag){
			userRole = ETSUtils.checkUserRole(es,document.getProjectId());

		   	if ((!(es.gDECAFTYPE.trim()).equalsIgnoreCase("I")) && (document.isIbmOnlyOrConf())){
				PrintWriter writer = resp.getWriter();
				writer.println("You are not authorized to view this document.");
				return;
			}
			if (userRole.equals(Defines.WORKSPACE_CLIENT) && document.isIbmOnlyOrConf()){
				PrintWriter writer = resp.getWriter();
				writer.println("You are not authorized to view this document.");
				return;
			}
			if(document.hasExpired()){
				if (!(userRole.equals(Defines.WORKSPACE_OWNER) || userRole.equals(Defines.ETS_ADMIN) || (document.getUserId()).equals(es.gIR_USERN))){
					PrintWriter writer = resp.getWriter();
					writer.println("You are not authorized to view this document.");
					return;
				}
			}
			if(document.IsDPrivate()){
				if (!isAuthorized(document.getUserId(),document.getId(),document.getProjectId(),userRole,es)){
					PrintWriter writer = resp.getWriter();
					writer.println("You are not authorized to view this document.");
					return;
				}
			}

			
		}
	}
	catch (Exception ex) {
		ex.printStackTrace(System.err);
	    System.out.println("error in cdels 1:"+ex);
	    String msg = "ERROR: Exception "+this.getClass().getName()+":getDoc:: "+ ex;
	    SysLog.log(SysLog.ERR, this, msg);
	    return;
	}
	finally{
	    System.out.println("finally");
		ETSDBUtils.close(conn);
	}






	ETSFileContentHandler handler = null;
	// start delivery
	try{
		handler = new ETSFileContentHandler();
		if (!pmoFlag){
			if (handler != null && document != null){
				//handler.deliverContent(req, resp, document);
				/*
				logHit(document.getId(),document.getProjectId(),es);
				*/
				handler.deliverContent(req, resp, document, idDocFile);
			}
		}
		else{
			if (handler != null && pmoDocument != null){
				handler.deliverContent(req, resp, pmoDocument);
				//logHit(pmoDocument.getDocId(),pmoDocument.getPMOProjectId(),es);
			}	
		}
	}
	catch (Exception ex){
		ex.printStackTrace(System.err);
		System.out.println("error in cdels 2:"+ex);
		return;
	}
	finally{
		handler = null;
	}
}


    public void init(ServletConfig config)
	throws ServletException
    {
	try{
	    super.init(config);
	    if (!Global.loaded)
		Global.Init();
	    databaseManager = new ETSDatabaseManager();

	}
	catch (Exception e)
	    {
		e.printStackTrace(System.err);
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

	private boolean isAuthorized(String ownerid,int docid,String projectid,String userRole,EdgeAccessCntrl es){
		
		if (userRole.equals(Defines.WORKSPACE_OWNER) ||  userRole.equals(Defines.ETS_ADMIN)|| userRole.equals(Defines.ETS_EXECUTIVE) || ownerid.equals(es.gIR_USERN))
			return true;
		else{
			try{
				Vector users = ETSDatabaseManager.getRestrictedProjMemberIds(projectid,docid,false);
				for (int u=0;u<users.size();u++){
					if (es.gIR_USERN.equals(users.elementAt(u))){
						return true;
					}	
				}
			}
			catch(Exception e){
				e.printStackTrace(System.err);
				return false;	
			}
		}
		return false;
	}


}







