/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2006                                     */
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

package oem.edge.ets.fe.workflow.setmet.prepare;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.ubp.ETSUserDetails;
import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;

import org.apache.commons.logging.Log;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


/**
 * Class       : IssuedetailsAction
 * Package     : oem.edge.ets.fe.workflow.prepare
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class IssuedetailsAction extends Action

{

	private static Log logger = WorkflowLogger.getLogger(IssuedetailsAction.class);
    public ActionForward executeWorkflow(ActionMapping mapping,
            WorkflowForm form, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {

        ActionForward forward = new ActionForward(); 
        System.out.println("In issue details action");
	
        
        
      
        
        if(request.getParameter("tab").equals("1"))
        {
            String issueDesc = null;
            String issue_contact = null;
            String issue_status = null;
            String issue_type = null;
            String target_date =null;
            String submit_date = null;
            String close_date = null;
            String last_modified = null;
            
            /** DB stuff */
            DBAccess db = null;
            try {
            String q = "select * from ets.wf_issue where issue_id='"+request.getParameter("issueid")+"' with ur";
			db = new DBAccess();
			db.prepareDirectQuery(q);
			System.out.println("DB:"+ q);
			int rows = db.execute();
			System.out.println("DB: Returned "+rows+" rows");
	
			
			
			issueDesc = db.getString(0,"ISSUE_DESC")==null?"Unavailable":db.getString(0,"ISSUE_DESC");
			issue_contact = db.getString(0,"ISSUE_CONTACT")==null?"Unavailable":db.getString(0,"ISSUE_CONTACT");
			issue_status = db.getString(0,"status")==null?"Unavailable":db.getString(0,"status");
			issue_type = db.getString(0,"issue_type")==null?"Unavailable":db.getString(0,"issue_type");
			target_date = db.getString(0,"target_date")==null?"Unavailable":db.getString(0,"target_date");
			submit_date = db.getString(0,"submit_date")==null?"Unavailable":db.getString(0,"submit_date");
			close_date = db.getString(0,"close_date")==null?"Unavailable":db.getString(0,"close_date");
			last_modified = db.getString(0,"last_timestamp")==null?"Unavailable":db.getString(0,"last_timestamp");
            } catch (Exception e) {
    			e.printStackTrace();
    		}finally
    		{
    			if(db!=null)
    			{
    				try {
    					db.close();
    				} catch (Exception e) {
    					e.printStackTrace();
    				db=null;
    				}
    			}
    		}
        			
		/* DB Stuff ends */
		
        request.setAttribute("issueDesc",issueDesc);
        request.setAttribute("issue_contact",issue_contact);
        request.setAttribute("issue_status",issue_status);
        request.setAttribute("issue_type",issue_type);
        request.setAttribute("target_date",target_date);
        request.setAttribute("submit_date",submit_date);
        request.setAttribute("close_date",close_date);
        request.setAttribute("last_modified",last_modified);
        request.setAttribute("tabnum","1");
        System.out.println("Showed tab1");
        
        }
        if(request.getParameter("tab").equals("2"))
        {
            ArrayList a = new ArrayList();

            /** DB Stuff */
            DBAccess db = null;
            try {
            db = new DBAccess();
            String q="select owner_id, ownership_state from ets.wf_issue_owner where issue_id='"+request.getParameter("issueid")+"' with ur";
            db.prepareDirectQuery(q);
			System.out.println("DB:"+ q);
			int rows = db.execute();
			System.out.println("DB: Returned "+rows+" rows");
			for(int t=0;t<rows;t++)
			{
			    ETSUserDetails u = new ETSUserDetails();
				u.setWebId(db.getString(t,0));
				u.extractUserDetails(db.getConnection());
				a.add((u.getFirstName()+" "+u.getLastName()));
			}
            }catch (Exception e) {
    			e.printStackTrace();
    		}finally
    		{
    			if(db!=null)
    			{
    				try {
    					db.close();
    				} catch (Exception e) {
    					e.printStackTrace();
    				db=null;
    				}
    			}
    		}
            
            /* DB Stuff ends */
            
            
            request.setAttribute("owners",a);
            request.setAttribute("tabnum","2");
            System.out.println("Showed tab2");
        }
        if(request.getParameter("tab").equals("3"))
        {

			request.setAttribute("workflowName","Unavailable");
            request.setAttribute("tabnum","3");
            System.out.println("Showed tab3");
        }
        request.setAttribute("issue_id",request.getParameter("issueid"));
        
        forward = mapping.findForward("success");
        return forward;
    }

	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute(ActionMapping arg0, ActionForm arg1,
			HttpServletRequest arg2, HttpServletResponse arg3) throws Exception {
		return executeWorkflow(arg0,(WorkflowForm)arg1,arg2,arg3);
	}
}
