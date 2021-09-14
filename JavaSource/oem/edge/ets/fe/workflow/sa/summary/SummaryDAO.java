/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     © Copyright IBM Corp. 2001-2006                                     */
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


package oem.edge.ets.fe.workflow.sa.summary;

import java.util.ArrayList;

import oem.edge.ets.fe.ubp.ETSUserDetails;
import oem.edge.ets.fe.workflow.dao.DBAccess;

/**
 * Class       : SummaryDAO
 * Package     : oem.edge.ets.fe.workflow.sa.summary
 * Description : 
 * Date		   : Mar 24, 2007
 * 
 * @author     : Pradyumna Achar
 */
public class SummaryDAO {
	
	public String getComments(String issue_id)
	{
		String retVal = "NA";
		String query="select h.comment, i.status from ets.wf_issue i, ets.wf_history h where h.wf_resource_id = i.issue_id and i.issue_id='"+issue_id+"' with ur";
		
		DBAccess db = null;
		try{
			db = new DBAccess();
			db.prepareDirectQuery(query);
			int rows = db.execute();
			if(rows>0)retVal = "";
			for(int i=0; i<rows; i++)
				retVal+=db.getString(i,0)+" >> ";
			if(rows>0)
				retVal+=" << Issue Status is "+db.getString(0,1)+">> ";
			db.doCommit();
			db.close();
			db=null;
		}catch(Exception e)
		{
			db.doRollback();
			e.printStackTrace();
			try{db.close();}catch(Exception ex){}
			db = null;
		}
		return retVal;
	}
	public ArrayList getMetricData(String prev0, String prev1, String prev2)
	{
		
		String query="select distinct a.question_id, a.ques_desc ques, b.rating prev0, c.rating prev1, " +
		"d.rating prev2, e.bypass_score, f.bypass_score, g.bypass_score " +
		"from " +
		"ets.wf_score_question_template a, " +
		"ets.wf_score b, " +
		"ets.wf_score c, " +
		"ets.wf_score d, " +
		"ets.wf_stage_document_setmet e, " +
		"ets.wf_stage_document_setmet f, " +
		"ets.wf_stage_document_setmet g " +
		"where " +
		"b.question_id=a.question_id " +
		"and c.question_id=a.question_id " +
		"and d.question_id=a.question_id " +
		"and e.wf_id=b.wf_id " +
		"and f.wf_id=c.wf_id " +
		"and g.wf_id=d.wf_id " +
		"and b.wf_id='"+prev0+"' " +
		"and c.wf_id='"+prev1+"' " +
		"and d.wf_id='"+prev2+"' order by a.question_id with ur";
		
		ArrayList retVal = new ArrayList();
		DBAccess db = null;
		try{
			db = new DBAccess();
			db.prepareDirectQuery(query);
			int rows = db.execute();
			for(int i=0; i<rows; i++)
			{
				String[] resultant = new String[]{db.getString(i,0),	db.getString(i,1),
						(null==db.getString(i,2) || "0".equals(db.getString(i,2)) || "Y".equals(db.getString(i,5))) ? "-": db.getString(i,2),
						(null==db.getString(i,3) || "0".equals(db.getString(i,3)) || "Y".equals(db.getString(i,6))) ? "-":db.getString(i,3),
						(null==db.getString(i,4) || "0".equals(db.getString(i,4)) || "Y".equals(db.getString(i,7))) ? "-":db.getString(i,4)};
				
				if(!(
						"-".equals(resultant[2]) ||
						"-".equals(resultant[3]) ||
						"-".equals(resultant[4])
					))
				{
					retVal.add(resultant);
				}
			}
			db.doCommit();
			db.close();
			db=null;
		}catch(Exception e)
		{
			db.doRollback();
			e.printStackTrace();
			try{db.close();}catch(Exception ex){}
			db = null;
		}
		return retVal;
	}
	public String[] getCommData(String oldWF, String newWF)
	{
		String query ="select " + 
		"	a.company client, a.sector sector, b.rating newScore, c.rating oldScore, z.quarter, z.year, " + 
		"	 " +
		" d.rating status " +
		"from  " +
		"	ets.ets_projects a, " +
		"	ets.wf_score b, " +
		"	ets.wf_score c, " +
		"	ets.wf_score d, " +
		"	ets.wf_stage_document_setmet e, " +
		"	ets.wf_stage_document_setmet f, " +
		"	ets.wf_score_question_template g, " +
		"	ets.wf_score_question_template h, " +
		"	ets.wf_score_question_template i, " +
		"	ets.wf_def z " +
		"where " +
		"	a.project_id=e.project_id " +
		"	and f.project_id=e.project_id " +
		"	and z.wf_id=e.wf_id " +
		"	and b.wf_id=e.wf_id " +
		"	and c.wf_id=f.wf_id " +
		"	and d.wf_id=e.wf_id " +
		"	and b.question_id=g.question_id " +
		"	and c.question_id=h.question_id " +
		"	and d.question_id=i.question_id " +
		"	and g.ques_desc='Client Input Rating' and g.question_type='B' and " + 
		"	 " +
		"g.wf_type='Self Assessment' " +
		"	and h.ques_desc='Client Input Rating' and h.question_type='B' and " + 
		"	 " +
		"h.wf_type='Self Assessment' " +
		"	and i.ques_desc='Stoplight Color' and i.question_type='B' and " + 
		"	 " +
		"i.wf_type='Self Assessment' " +
		"and e.wf_id='"+newWF+"' " +
		"and f.wf_id='"+oldWF+"' with ur";
		String[] retVal = new String[]{"NA","NA","NA","NA","NA","NA"};
		DBAccess db = null;
		try{
			db = new DBAccess();
			db.prepareDirectQuery(query);
			int rows = db.execute();
			if(rows>0)
				retVal = new String[]{db.getString(0,"client"),
					db.getString(0,"sector"),
					db.getString(0,"newScore"),
					db.getString(0,"oldScore"),
					db.getString(0,"status"),
					db.getString(0,"quarter"),
					db.getString(0,"year")
				};
			
			db.doCommit();
			db.close();
			db=null;
		}catch(Exception e)
		{
			db.doRollback();
			e.printStackTrace();
			try{db.close();}catch(Exception ex){}
			db = null;
		}
		return retVal;
	}
	public String getClientAttendees(String workflowID)
	{
		String q= "NA";
		String query =
			"select concat(concat(b.fname,' '),b.lname) from ets.wf_setmet_attendees_client a, ets.wf_client b where wf_id='"+workflowID+"' and a.userid = b.client_id with ur";
		DBAccess db = null;
		try{
			db = new DBAccess();
			db.prepareDirectQuery(query);
			int nrows = db.execute();
			String s = "";
			if(nrows > 0)
			{
				for(int i=0; i<nrows-1; i++)
				{
					s += db.getString(i,0)+", ";
				}
				s += db.getString(nrows-1, 0);
				q = s;
			}
			db.doCommit();
			db.close();
			db=null;
		}catch(Exception e)
		{
			db.doRollback();
			e.printStackTrace();
			try{db.close();}catch(Exception ex){}
			db = null;
		}
		return q;
	}
	public ArrayList getIssues(String workflowID)
	{
		String query="select i.issue_type, i.issue_category, i.issue_title, i.issue_id from ets.wf_issue i, ets.wf_issue_wf_map m where i.issue_id = m.issue_id and m.wf_id='"+workflowID+"'with ur";
		DBAccess db = null;
		ArrayList retVal = new ArrayList();
		try{
			db = new DBAccess();
			db.prepareDirectQuery(query);
			int nrows = db.execute();
			for(int i=0; i<nrows; i++)
			{
				retVal.add(new String[]{db.getString(i,0), db.getString(i,1), db.getString(i,2), db.getString(i,3)});
			}
			db.doCommit();
			db.close();
			db=null;
		}catch(Exception e)
		{
			db.doRollback();
			e.printStackTrace();
			try{db.close();}catch(Exception ex){}
			db = null;
		}
		return retVal;
	}
	public String getOwners(String issue_id)
	{
		String query = "select owner_id from ets.wf_issue_owner where issue_id='"+issue_id+"' with ur";
		String retVal = "NA";
		ArrayList owners = new ArrayList();
		DBAccess db = null;
		try{
			db = new DBAccess();
			db.prepareDirectQuery(query);
			int nrows = db.execute();
			if(nrows>0)retVal ="";
			
			for(int i=0; i<nrows; i++)
			{
				owners.add(db.getString(i,0));
			}
			for(int i=0; i<owners.size(); i++)
			{
				ETSUserDetails u = new ETSUserDetails();
				u.setWebId((String)owners.get(i));
				u.extractUserDetails(db.getConnection());
				if(i==owners.size()-1)
					retVal+=u.getFirstName()+" "+u.getLastName();
				else
					retVal+=u.getFirstName()+" "+u.getLastName()+", ";
				
			}
			db.doCommit();
			db.close();
			db=null;
		}catch(Exception e)
		{
			db.doRollback();
			e.printStackTrace();
			try{db.close();}catch(Exception ex){}
			db = null;
		}
		return retVal;
	}
	public String getMonthOpened(String issue_id)
	{
		String query = "select action_date from ets.wf_history where wf_resource_id='"+issue_id+"' order by action_date asc with ur";
		String retVal = "NA";
		DBAccess db = null;
		try{
			db = new DBAccess();
			db.prepareDirectQuery(query);
			int nrows = db.execute();
			if(nrows>0)
			{
				String d= db.getString(0,0);
				try{
					String[] temp = d.split("-");
					if(temp.length==3)
						retVal =  temp[1]+"/"+temp[0];
				}catch(Exception x)
				{
					retVal = "NA";
				}
			}
			db.doCommit();
			db.close();
			db=null;
		}catch(Exception e)
		{
			db.doRollback();
			e.printStackTrace();
			try{db.close();}catch(Exception ex){}
			db = null;
		}
		return retVal;
	}
	public String getPrevWF(String workflowID)
	{
		String prevYear = null;
		String currMonth = null;
		String query = "select year, quarter from ets.wf_def where wf_id='"+workflowID+"' with ur";
		DBAccess db = null;
		try{
			db = new DBAccess();
			db.prepareDirectQuery(query);
			int nrows = db.execute();
			if(nrows==1)
			{
			if(db.getInt(0,"quarter")==1)
				prevYear = ""+(Integer.parseInt(db.getString(0,"year"))-1);
			else
				prevYear = db.getString(0,"year");
			currMonth = ""+db.getString(0,"quarter");
			}
			db.doCommit();
			db.close();
			db=null;
		}catch(Exception e)
		{
			db.doRollback();
			e.printStackTrace();
			try{db.close();}catch(Exception ex){}
			db = null;
		}
		return getPrevWF(workflowID, prevYear, getPrevMonth(currMonth));
	}
	public String getPrevWF(String workflowID, String year, String month)
	{
		String query = "select b.wf_id, b.creation_date from ets.wf_def a, ets.wf_def b "+
		"where b.wf_type='SELF ASSESSMENT' and b.project_id=a.project_id "+
		"and b.quarter="+month+" and b.year = '"+year+"' "+
		"and a.wf_id ='"+workflowID+"' order by b.creation_date desc with ur";
		String retVal = null;
		DBAccess db = null;
		try{
			db = new DBAccess();
			db.prepareDirectQuery(query);
			int nrows = db.execute();
			if(nrows > 0)
			{
				retVal = db.getString(0,0);
			}
			db.doCommit();
			db.close();
			db=null;
		}catch(Exception e)
		{
			db.doRollback();
			e.printStackTrace();
			try{db.close();}catch(Exception ex){}
			db = null;
		}
		return retVal;
	}
	public String getPrevMonth(String currentMonth)
	{
		try{
		int i = Integer.parseInt(currentMonth);
		return i==1 ? ""+12 : ""+(i-1);
		}catch(Exception e)
		{
			return "13";
		}
	}
	public String getCurrentMonth(String currentMonth)
	{
		try{
		switch(Integer.parseInt(currentMonth))
		{
		case 1:	return "January";
		case 2:	return "February";
		case 3:	return "March";
		case 4:	return "April";
		case 5:	return "May";
		case 6:	return "June";
		case 7:	return "July";
		case 8:	return "August";
		case 9:	return "September";
		case 10:return "October";
		case 11:return "November";
		case 12:return "December";
		}
		}catch(Exception x)
		{
			return null;
		}
		return null;
	}
	public String[] getNameAndTitle(String workflowID)
	{
		String query="select a.wf_name, b.report_title from ets.wf_def a , ets.wf_stage_document_setmet b where b.wf_id=a.wf_id and a.wf_id='"+workflowID+"' with ur";
		String[] retVal = new String[]{"NA","GES Client Sat Summary Report"};
		DBAccess db = null;
		try{
			db = new DBAccess();
			db.prepareDirectQuery(query);
			int nrows = db.execute();
			if(nrows > 0)
				retVal = new String[] {db.getString(0,0), db.getString(0,1)};
			db.doCommit();
			db.close();
			db=null;
		}catch(Exception e)
		{
			db.doRollback();
			e.printStackTrace();
			try{db.close();}catch(Exception ex){}
			db = null;
		}
		return retVal;
	}
}