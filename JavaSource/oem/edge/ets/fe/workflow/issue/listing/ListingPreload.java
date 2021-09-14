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


package oem.edge.ets.fe.workflow.issue.listing;

import java.util.ArrayList;

import oem.edge.ets.fe.workflow.core.WorkflowObject;
import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.log.WorkflowLogger;

import org.apache.commons.logging.Log;



/**
 * Class       : ListingPreload
 * Package     : oem.edge.ets.fe.workflow.issue.listing
 * Description : 
 * Date		   : Oct 9, 2006
 * 
 * @author     : Pradyumna Achar
 */
public class ListingPreload extends WorkflowObject {

	private static Log logger = WorkflowLogger.getLogger(ListingPreload.class);
	
	private ArrayList issues=new ArrayList();
	private ArrayList p_issues=new ArrayList(); //Added for 7.1.1 by Konda Reddy
	/**
	 * @param project_id
	 * @param wf_id
	 */
	public ListingPreload(String project_id, String cur_wf_id) {
		
		IssueObj issue = null;
		IssueObj p_issue = null;
		String q1 = null, q2=null,q3=null,q4=null;

		
		/* GET ISSUES CREATED IN THIS WORKFLOW */
		
		q1 = "select issue_id from ets.wf_issue_wf_map where project_id='"+project_id+"' and wf_id='"+cur_wf_id+"' with ur";
		
		DBAccess db = null;
		DBAccess db2 = null;
		try {
			db = new DBAccess();
			db.prepareDirectQuery(q1);
			System.out.println("DB:"+q1);
			int nrows = db.execute();
			System.out.println("DB: Returned "+nrows+" rows");
			for(int i =0; i<nrows; i++)
			{
				String issue_id = db.getString(i,0);
				q2 = "select issue_title, issue_type, status, issue_id_display from ets.wf_issue where issue_id='"+issue_id+"' with ur";
				db2 = new DBAccess();
				db2.prepareDirectQuery(q2);
				System.out.println("DB:"+q2);
				db2.execute();
				System.out.println("DB:done");
				issue = new IssueObj();
				issue.setId(issue_id);
				issue.setTitle(db2.getString(0,0));
				System.out.println(issue.getTitle());
				issue.setType(db2.getString(0,1));
				issue.setStatus(db2.getString(0,2));
				String issue_id_display=db2.getString(0,3);
				
				if(issue_id_display!=null)
					issue.setId_display(issue_id_display);
				else
					issue.setId_display(issue_id);
				
				issues.add(issue);
				db2.doCommit();
				db2.close();
				db2 = null;
			}
			db.doCommit();
			db.close();
			db = null;
		} catch (Exception e) {

			e.printStackTrace();
		}finally{
			if(db2!=null)
			{
				try {
					db2.doCommit();
					db2.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				db2=null;
			}
			if(db!=null)
			{
				try {
					db.doCommit();
					db.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				db=null;
			}
		}

		/* GET ISSUES IMPORTED FROM PREVIOUS WORKFLOWS */
		/*
		q1 = "select issue_id from ets.wf_prepare_previous_issues where project_id='"+project_id+"' and wf_id='"+wf_id+"' with ur";
		
		db = null;
		db2 = null;
		try {
			db = new DBAccess();
			db.prepareDirectQuery(q1);
			System.out.println("DB:"+q1);
			int nrows = db.execute();
			System.out.println("DB: Returned "+nrows+" rows");
			for(int i =0; i<nrows; i++)
			{
				String issue_id = db.getString(i,0);
				q2 = "select issue_title, issue_type, status from ets.wf_issue where issue_id='"+issue_id+"' with ur";
				db2 = new DBAccess();
				db2.prepareDirectQuery(q2);
				System.out.println("DB:"+q2);
				System.out.println("DB: returned "+db2.execute());
				System.out.println("DB:done");
				issue = new IssueObj();
				issue.setId(issue_id);
				issue.setTitle(db2.getString(0,0));
				issue.setType(db2.getString(0,1));
				issue.setStatus(db2.getString(0,2));
				System.out.println("Issue title is "+ issue.getTitle());
				issues.add(issue);
				db2.close();
				db2 = null;
			}
			db.close();
			db = null;
		} catch (Exception e) {

			e.printStackTrace();
		}finally{
			if(db2!=null)
			{
				try {
					db2.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				db2=null;
			}
			if(db!=null)
			{
				try {
					db.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				db=null;
			}
		}
		*/
		
		/* Added for 7.1.1 by Konda Reddy */
		DBAccess db3 = null;
		DBAccess db4 = null;
		try {
		db=new DBAccess();
		
		String q = "select quarter, year, creation_date, wf_type from ets.wf_def where project_id='"+project_id+"' and wf_id='"+cur_wf_id+"' with ur";
		db.prepareDirectQuery(q);
		System.out.println("DB:"+ q);
		int nrows=db.execute();
		System.out.println("DB: Returned "+nrows+" rows");
		String curQuarter = "SELF ASSESSMENT".equals(db.getString(0,3))?"" + (1+(Integer.parseInt(db.getString(0,0)))/4) : db.getString(0,0) ;
		String curYear = db.getString(0, 1);
		String prevYear = ""+(Integer.parseInt(curYear)-1);
		String d = db.getString(0,2);
		String curCreatYear = d.substring(0,4);
		String curCreatMonth = d.substring(5,7);
		String curCreatDay = d.substring(8,10);
	
		// Query modified by KP, to keep in sync with the prepare stage preload query
		q3 = "select distinct a.issue_id,a.wf_id from ets.wf_issue_wf_map a, ets.wf_issue b where a.wf_id in "+ 
		"( "+
		"select distinct wf_id "+
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
		") and a.issue_id=b.issue_id and b.status!='COMPLETED' and b.status!='CANCELLED'  with ur ";
		
	    db3 = new DBAccess();
		db3.prepareDirectQuery(q3);
		System.out.println("DB:"+q3);
		int mrows = db3.execute();
		System.out.println("DB: Returned "+mrows+" rows");
		for(int j =0; j<mrows; j++)
		{
			String issue_id = db3.getString(j,0);
			System.out.println("Issue id is:"+issue_id);
			String wf_id = db3.getString(j,1);
			System.out.println("Workflow id is:"+wf_id);
			q4 = "select issue_title, issue_type, status,issue_id_display from ets.wf_issue where issue_id='"+issue_id+"'and status!='COMPLETED' and status!='CANCELLED' with ur";
			db4 = new DBAccess();
			db4.prepareDirectQuery(q4);
			System.out.println("DB:"+q4);
			db4.execute();
			System.out.println("DB:done");
			System.out.println("issue id is:"+issue_id);
			p_issue = new IssueObj();
			p_issue.setId(issue_id);
			p_issue.setTitle(db4.getString(0,0));
			System.out.println(p_issue.getTitle());
			p_issue.setType(db4.getString(0,1));
			p_issue.setStatus(db4.getString(0,2));
			
			String issue_id_display=db4.getString(0,3);
			if(issue_id_display!=null)
				p_issue.setId_display(issue_id_display);
			else
				p_issue.setId_display(issue_id);
			
			p_issues.add(p_issue);
			db4.doCommit();
			db4.close();
			db4 = null;
		}
		db3.doCommit();
		db3.close();
		db3 = null;
	
	} catch (Exception e) {

		e.printStackTrace();
	}finally{
		if(db4!=null)
		{
			try {
				db4.doCommit();
				db4.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			db4=null;
		}
		if(db3!=null)
		{
			try {
				db3.doCommit();
				db3.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			db3=null;
		}
	
	}
	}/**
	 * @return Returns the issues.
	 */
	public ArrayList getIssues() {
		return issues;
	}
	/**
	 * @param issues The issues to set.
	 */
	public void setIssues(ArrayList issues) {
		this.issues = issues;
	}
	/**
	 * @return Returns the p_issues.
	 */
	public ArrayList getP_issues() {
		return p_issues;
	}
	/**
	 * @param p_issues The p_issues to set.
	 */
	public void setP_issues(ArrayList p_issues) {
		this.p_issues = p_issues;
	}
}

