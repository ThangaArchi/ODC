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
 * Created on Nov 22, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package oem.edge.ets.fe.workflow.setmet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.workflow.core.WorkflowAction;
import oem.edge.ets.fe.workflow.core.WorkflowForm;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import java.util.HashMap;


/**
 * @author ryazuddin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WorkflowMatrixAction extends WorkflowAction {

	/* (non-Javadoc)
	 * @see oem.edge.ets.fe.workflow.core.WorkflowAction#executeWorkflow(org.apache.struts.action.ActionMapping, oem.edge.ets.fe.workflow.core.WorkflowForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward executeWorkflow(ActionMapping mapping, WorkflowForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		
		WorkflowMatrixForm rForm = (WorkflowMatrixForm) form;
		
		HashMap hmap = new HashMap();
		hmap.put("WF001","Preliminary Prep View Report");
		hmap.put("WF002","Preliminary Issue View Report");
		hmap.put("WF003","Preliminary Exec View Report");
		
		if(action.equalsIgnoreCase("report")){
			System.out.println("Inside Report ==============>");
			WorkflowMatrixDAO mdao = new WorkflowMatrixDAO();
			WorkflowMatrixObject object = new WorkflowMatrixObject();
			
			rForm.reset();
			rForm.setWorkspaceslist(mdao.getAllWorkflowList());
			rForm.setBrandlist(mdao.getAllBrand());
			rForm.setProcesslist(mdao.getAllProcess());
			rForm.setScesectorlist(mdao.getAllSCESectorList());
			rForm.setBusinesssectorlist(mdao.getAllBusinessSectorList());
			rForm.setWorkflowObject(object);
			 
			//rForm.setChkboxcolumn("WF_STAGE_IDENTIFY_SETMET.NSI_RATING");
 			
			String reportid = (String)request.getParameter("reportid");
			request.setAttribute("reportid",reportid);
			request.setAttribute("reportName",hmap.get(reportid.trim()));
			request.setAttribute("action",action);
			
	        return  mapping.findForward("SalesReport");
	
		}else if(action.equalsIgnoreCase("generateReport")){
			
			System.out.println("Inside GenertrateReport ==============>");
			WorkflowMatrixDAO mdao = new WorkflowMatrixDAO();
			//WorkflowMatrixObject object = new WorkflowMatrixObject();
			String reportid = (String)request.getParameter("reportid");
			String[] values = request.getParameterValues("chkboxcolumn");
			WorkflowMatrixObject matrix = (WorkflowMatrixObject) rForm.getWorkflowObject();
			ArrayList selectedfields = new ArrayList();
			
			if(values!=null && values.length > 0){
				for(int i=0;i< values.length; i++){
					selectedfields.add(values[i].substring(values[i].indexOf(".")+1).trim());
				}
			}
			String qryString = mdao.getQueryResult(values,matrix,reportid);
			
			System.out.println(qryString + "  Selected fields ------"+ selectedfields);
			//--Ends here
			ArrayList reportlist = mdao.GenerateFinalReport(qryString,selectedfields);
			
			//Testing of reports-----------------
			Iterator it = reportlist.iterator();
			while(it.hasNext()){
				WorkflowMatrixReportObject mobj = (WorkflowMatrixReportObject)it.next();
				System.out.println("\n Report Starts here--------------------------");
				System.out.println("\n" + mobj.getWorkspace()+"---"+mobj.getProcess()+"---"+mobj.getScesector()
										+"---"+mobj.getBrand()+"---"+mobj.getBusinesssector()+"---"+mobj.getClientname()
										+"---"+mobj.getAccountcontact()+"---"+mobj.getNsirating());
 			}
			//Testing Ends here ------------------------
			System.out.println(qryString + "  Selected fields ------"+ selectedfields);
			
			rForm.setWorkspaceslist(mdao.getAllWorkflowList());
			rForm.setBrandlist(mdao.getAllBrand());
			rForm.setProcesslist(mdao.getAllProcess());
			rForm.setScesectorlist(mdao.getAllSCESectorList()); 
			rForm.setBusinesssectorlist(mdao.getAllBusinessSectorList());
			//rForm.setWorkflowObject(object);
			
			request.setAttribute("reportid",reportid);
			request.setAttribute("reportName",hmap.get(reportid.trim()));
			request.setAttribute("action",action);
			request.setAttribute("reportList",reportlist);
			request.setAttribute("selectedfields",selectedfields);
			
			return  mapping.findForward("SalesReport");
			
		}else if(action.equalsIgnoreCase("downloadcsvReports")){
			
			System.out.println("Inside downloadcsvReports  ==============>");
			
			WorkflowMatrixDAO mdao = new WorkflowMatrixDAO();
			WorkflowMatrixObject object = new WorkflowMatrixObject();
			String reportid = (String)request.getParameter("reportid").trim();
			String[] values = request.getParameterValues("chkboxcolumn");
									
			WorkflowMatrixObject matrix = (WorkflowMatrixObject) rForm.getWorkflowObject();
			ArrayList selectedfields = new ArrayList();
			ArrayList allFields = new ArrayList();
			
			allFields.add("Workspace");allFields.add("Process");
			allFields.add("SCE Sector");allFields.add("Brand");allFields.add("Business Sector");
			
			if(values!=null && values.length > 0){
				for(int i=0;i< values.length; i++){
					selectedfields.add(values[i].substring(values[i].indexOf(".")+1).trim());
				}
				allFields.addAll(selectedfields);
			}
			String qryString = mdao.getQueryResult(values,matrix,reportid);
			
			System.out.println(qryString + "  Selected fields ------"+ selectedfields);
			//--Ends here
			ArrayList reportlist = mdao.GenerateFinalReport(qryString,selectedfields);
			StringBuffer sb_csv = mdao.downloadReportPage(allFields,reportlist,reportid);
			
			response.setHeader("Content-disposition","attachment; filename="+hmap.get(reportid)+".csv");
			response.setHeader("Content-Type", "application/octet-stream");
			response.setContentLength(sb_csv.length());
			PrintWriter out = response.getWriter();
			out.println(sb_csv.toString());
			out.close();
			out.flush();
			
			request.setAttribute("reportid",reportid);
			request.setAttribute("reportName",hmap.get(reportid.trim()));
			request.setAttribute("action",action);
		}
		return  mapping.findForward("SalesReport");
	}
	
}
