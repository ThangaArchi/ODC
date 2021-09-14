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

import oem.edge.ets.fe.ubp.ETSUserDetails;
import oem.edge.ets.fe.workflow.core.*;
import oem.edge.ets.fe.workflow.dao.DBAccess;

import java.util.ArrayList;
import java.util.Collections;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;
import org.apache.commons.logging.Log;



/**
 * Class       : ListOfExistingIssuesPreload
 * Package     : oem.edge.ets.fe.workflow.prepare
 * Description : 
 *
 * @author      Pradyumna Achar
 */
public class ListOfExistingIssuesPreload extends WorkflowObject {

	ArrayList rows = new ArrayList();
	private static Log logger = WorkflowLogger.getLogger(ListOfExistingIssuesPreload.class);
public ListOfExistingIssuesPreload(String pid,String wid, WorkflowForm workflowForm) {
	
	//Note: I'm assuming that the last stage of the workflow is called 'completed'.
	
		Row r = null;
		ListOfExistingIssuesVO voList = null;
		ListOfExistingIssuesFormBean formBean  = null;
		if(workflowForm==null)
		{
			System.out.println(".................Form bean was null");
		}
		else{
		 formBean = (ListOfExistingIssuesFormBean)workflowForm;
		 voList = (ListOfExistingIssuesVO)formBean.getWorkflowObject();
		 System.out.println("........Form bean is not null");
		}
		String project_id = pid;
		String cur_wf_id = wid;
		
		/** ********************* */
		DBAccess db = null, db2=null, db3=null;
		try {
			
			
			String q1=null, q2=null, q3=null, q4=null , quarterYear=null;
			
			db=new DBAccess();
			
			String q = "select quarter, year, creation_date, wf_type from ets.wf_def where project_id='"+project_id+"' and wf_id='"+cur_wf_id+"' with ur";
			db.prepareDirectQuery(q);
			System.out.println("DB:"+ q);
			int nrows=db.execute();
			System.out.println("DB: Returned "+nrows+" rows");
			String curQuarter = "SELF ASSESSMENT".equals(db.getString(0,3))?"" + (1+(Integer.parseInt(db.getString(0,0)))/4) : db.getString(0,0) ;
			String curYear = db.getString(0, 1);
			System.out.println(curYear);
			String prevYear = ""+(Integer.parseInt(curYear)-1);
			String d = db.getString(0,2);
			String curCreatYear = d.substring(0,4);
			String curCreatMonth = d.substring(5,7);
			String curCreatDay = d.substring(8,10);
			q1 = "select wf_id,quarter,year from ets.wf_def where project_id='"+project_id+"' and (cast(year as int)<"+curYear+" or (year='"+curYear+"' and quarter<"+curQuarter+")) with ur";
			
			q1 = "select wf_id,quarter,year,wf_type from ets.wf_def where project_id='"+project_id+"' and ((wf_type not in('SELF ASSESSMENT') and (cast(year as int)="+prevYear+" and quarter>="+curQuarter+") or (cast(year as int)="+curYear+" and quarter<"+curQuarter+") or ((cast(year as int)="+curYear+" and quarter="+curQuarter+") and creation_date < '"+curCreatYear+"-"+curCreatMonth+"-"+curCreatDay+"'))) or (wf_type in('SELF ASSESSMENT') and  and (cast(year as int)="+prevYear+" and quarter>="+curQuarter+") or (cast(year as int)="+curYear+" and quarter<"+curQuarter+") or ((cast(year as int)="+curYear+" and quarter="+curQuarter+") and creation_date < '"+curCreatYear+"-"+curCreatMonth+"-"+curCreatDay+"')))) with ur";
			
			q1=" "+
			"select wf_id,quarter,year,wf_type "+
			" "+
			"from "+ 
			"	ets.wf_def "+
			"where "+
			"	project_id='"+project_id+"' "+
			"	and ( "+
			"		( "+
			"			wf_type not in('SELF ASSESSMENT') "+
			"			and ( "+
			"				(cast(year as int)="+prevYear+" and quarter>="+curQuarter+") "+
			"				or (cast(year as int)="+curYear+" and quarter<"+curQuarter+") "+
			"				or ( "+
			"					(cast(year as int)="+curYear+" and quarter="+curQuarter+") "+
			"					and creation_date < '"+curCreatYear+"-"+curCreatMonth+"-"+curCreatDay+"'  "+
			"				 ) "+
			"			) "+
			"		) "+
			"		or ( "+
			"			wf_type in('SELF ASSESSMENT') "+
			"			and ( "+
			"				(cast(year as int)="+prevYear+" and (1+quarter/4)>="+curQuarter+") "+
			"				or (cast(year as int)="+curYear+" and (1+quarter/4)<"+curQuarter+") "+
			"				or ( "+
			"					(cast(year as int)="+curYear+" and (1+quarter/4)="+curQuarter+") "+
			"					and creation_date < '"+curCreatYear+"-"+curCreatMonth+"-"+curCreatDay+"' "+ 
			"				 ) "+
			"			) "+
			"		) "+
			"	) "+
			" with ur ";
			
			
			System.out.println("*** UPDATED QUERY FOR PREV QTRS : \n\n"+q1+"\n\n");
			
			db.prepareDirectQuery(q1);
			System.out.println("DB:"+ q1);
			nrows=db.execute();
			System.out.println("DB: Returned "+nrows+" rows");
			
			for(int i= 0 ; i < nrows ; i++)
			{
				
				String wf_id = db.getString(i,"wf_id");
				System.out.println("............Fetch issues for workflow "+wf_id);
				quarterYear=db.getString(i,1)+"Q"+db.getString(i,2);
				if("SELF ASSESSMENT".equals(db.getString(i,"wf_type")))
					quarterYear=db.getString(i,1)+"/"+db.getString(i,2);
				db2 = new DBAccess();
				
				q4 = "select wf_type,wf_curr_stage_name from ets.wf_def where wf_id='"+wf_id +"' and project_id='"+project_id+"' with ur";
				 
				    
				db2.prepareDirectQuery(q4);
				String wf_type=null;
				String wf_status = null;
				
				System.out.println("DB:"+ q4);
				int mrows = db2.execute();
				System.out.println("DB: Returned "+mrows+" rows");
				
				if(mrows==1)
				{
					wf_type=db2.getString(0,0);
					wf_status=db2.getString(0,1).equalsIgnoreCase("complete")?"Complete":"Incomplete";
				}
			
					
				q2 = "select issue_id from ets.wf_issue_wf_map where wf_id='"+wf_id+"' and project_id='"+project_id+"' with ur";
				db2.prepareDirectQuery(q2);
				System.out.println("DB:"+ q2);
				mrows = db2.execute();
				System.out.println("DB: Returned "+mrows+" rows");
				
				for(int j= 0 ; j < mrows ; j++)
				{	
					r = new Row();
					String issue_id = db2.getString(j,"issue_id");
					//q3 = "select issue_desc,issue_contact,status from ets.wf_issue where issue_id='"+issue_id+"' with ur";
					q3 = "select issue_title,issue_contact,status from ets.wf_issue where issue_id='"+issue_id+"' with ur";
					db3 = new DBAccess();
					db3.prepareDirectQuery(q3);
					System.out.println("DB:"+ q3);
					int prows = db3.execute();
					System.out.println("DB: Returned "+prows+" rows");
					if(prows==1)
					{
						r.setIssueTitle(db3.getString(0,0));
						
						/** Issue contact */
						ETSUserDetails u = new ETSUserDetails();
						u.setWebId(db3.getString(0,1));
						u.extractUserDetails(db.getConnection());
						r.setIssueOwner(u.getFirstName()+" "+u.getLastName());

						r.setIssueStatus(db3.getString(0,2));
					}
					
					/*q3="select owner_id, ownership_state from ets.wf_issue_owner where issue_id='"+issue_id+"' with ur";
					db3.prepareDirectQuery(q3);
					System.out.println("DB:"+ q3);
					prows = db3.execute();
					System.out.println("DB: Returned "+prows+" rows");
					for(int t=0;t<prows;t++)
					{
					    ETSUserDetails u = new ETSUserDetails();
						u.setWebId(db3.getString(t,0));
						u.extractUserDetails(db.getConnection());
						r.getIssueOwner().add((u.getFirstName()+" "+u.getLastName()));
					}*/
					
					
					q3="select issue_id from ETS.WF_PREPARE_PREVIOUS_ISSUES where project_id='"+project_id +"' and WF_ID='"+cur_wf_id+"' and issue_id='"+issue_id+"' with ur";
					db3.prepareDirectQuery(q3);
					System.out.println("DB:"+ q3);
					prows = db3.execute();
					System.out.println("DB: Returned "+prows+" rows");
					if(prows==0)
					{
					    r.setIsAlreadyPresent("N");
					}
					else
					{
					    r.setIsAlreadyPresent("Y");
					}
					
					r.setIssueID(issue_id);
					r.setDateOpened(quarterYear);
					r.setWorkflowType(wf_type);
					r.setWorkflowStatus(wf_status);
					
					System.out.println(r.getDateOpened());
					
					db3.close();
					db3=null;
					
					rows.add(r);
				}
				db2.close();
				db2=null;
				System.out.println("...........Done processing for worfklow "+wf_id);
			}
			
			db.close();
			db=null;
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
			if(db2!=null)
			{
				try {
					db2.close();
				} catch (Exception e) {
					e.printStackTrace();
				db2=null;
				}
			}
			if(db3!=null)
			{
				try {
					db3.close();
				} catch (Exception e) {
					e.printStackTrace();
				db3=null;
				}
			}
		}
		
		/** ********************* */
		if(voList!=null)
		{
			
			OneIssueVO vo = null;
			voList.setIssues(new ArrayList(rows.size()));
			/*
			System.out.println("rows.size() is "+ rows.size());
			System.out.println(".....Size of (new ArrayList(rows.size())) is "+ (new ArrayList(rows.size())).size());
			System.out.println("....size of voList is "+voList.getIssues().size());
			*/
			for (int i =0; i<rows.size(); i++)
			{
				voList.getIssues().add(new Object());
			}
			for (int i = 0; i < rows.size(); i++) {
				vo = new OneIssueVO();
				vo.setIssueID(((Row)rows.get(i)).getIssueID());
				vo.setSelectedIssue("");

				if(((Row) rows.get(i)).getIsAlreadyPresent().equals("Y"))
				{
					//System.out.println("this issue was present already.");
					vo.setSelectedIssue(((Row) rows.get(i)).getIssueID());
					//System.out.println("vo.getSelectedIssue() is " +vo.getSelectedIssue());
				}
				else
					vo.setSelectedIssue(null);
				
				voList.getIssues().set(i,vo);
			}
		//	System.out.println(((ListOfExistingIssuesVO)(workflowForm.getWorkflowObject())).getIssuesIndexed(1).getSelectedIssue());
		}
		IssueComparator ic = new IssueComparator();
		ic.setCurrentType("SETMET");
		Collections.sort(rows,ic);
		for(int i=0; i<rows.size(); i++)
		{
			r = (Row)rows.get(i);
			if(i%2==0)r.setStyleID("1"); else r.setStyleID("2");
		}
		
	}

		/**
		  * @return Returns the rows.
		  */
	public ArrayList getRows() {
		return rows;
	}

	/**
	 * @param rows
	 *            The rows to set.
	 */
	public void setRows(ArrayList rows) {
		this.rows = rows;
	}
}
