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


package oem.edge.ets.fe.workflow.qbr.summary;

import java.util.ArrayList;

import oem.edge.ets.fe.workflow.dao.DBAccess;
import oem.edge.ets.fe.workflow.util.pdfutils.QBRCommonData;
import oem.edge.ets.fe.workflow.util.pdfutils.QBRScore;

/**
 * Class       : ScoringSummaryDAO
 * Package     : oem.edge.ets.fe.workflow.qbr.summary
 * Description : 
 * Date		   : Mar 5, 2007
 * 
 * @author     : Pradyumna Achar
 */
public class ScoringSummaryDAO {
	public QBRCommonData getCommonData(String workflowID, String prevWFID)
	{
		QBRCommonData q = new QBRCommonData();
		
		if(prevWFID!=null)
		{
			String query = 
				"select distinct b.wf_name wfname, b.quarter qtr, b.year yr, a.company cmp, a.brand brnd, a.sector sctr, c.rating_period_from ratFrom, c.rating_period_to ratTo, d.overal_score newscore, e.overal_score oldscore, d.overal_comments comms, d.report_title rt "+  
				"from ets.ets_projects a, "+
				"ets.wf_def b, "+
				"ets.wf_stage_identify_setmet c, "+
				"ets.wf_stage_document_setmet d, "+
				"ets.wf_stage_document_setmet e "+
				" "+
				"where a.project_id=b.project_id and b.wf_id = c.wf_id and b.wf_id = d.wf_id and "+
				"c.wf_id='"+workflowID+"' "+
				"and e.wf_id='"+prevWFID+"' with ur";
			
			DBAccess db = null;
			try{
				db = new DBAccess();
				db.prepareDirectQuery(query);
				if(db.execute()==1)
				{
					q.setQbrName(db.getString(0,0));
					q.setQuarter(db.getString(0,1));
					q.setYear(db.getString(0,2));
					q.setClient(db.getString(0,3));
					q.setAreaRated(db.getString(0,4));
					q.setSegment(db.getString(0,5));
					q.setRatingPeriod(toUSDate(db.getString(0,6))+"-"+toUSDate(db.getString(0,7)));
					String[] temp1 = getOveralls(workflowID);
					String[] temp2 = getOveralls(prevWFID);
					q.setReportTitle(db.getString(0,"rt")==null?"QBR Scoring Summary":db.getString(0,"rt"));
					q.setCurrentScore(temp1[0]);
					q.setOldScore(temp2[0]);
					q.setOverall_comments(temp1[1]);
					System.out.println("Current Score = "+q.getCurrentScore());
					System.out.println("Old score = "+q.getOldScore());
					System.out.println("Overall comments = "+q.getOverall_comments());
					try{
						double old =Double.parseDouble(q.getOldScore());
						double neu =Double.parseDouble(q.getCurrentScore());
						q.setChange(""+(neu-old));
					}catch(Exception e)
					{
						q.setCurrentScore("NA");
						q.setChange("NA");
						q.setOldScore("NA");
						System.out.println(e);
					}
					q.setRank(q.getCurrentScore());
					
				}
				db.close();
				db=null;
			}catch(Exception e)
			{
				db.doRollback();
				e.printStackTrace();
				try{db.close();}catch(Exception ex){}
				db = null;
			}
		}else
		{
			String query = 
				"select distinct b.wf_name, b.quarter, b.year, a.company, a.brand, a.sector, c.rating_period_from, c.rating_period_to, d.overal_score, d.overal_comments, d.report_title rt "+  
				"from ets.ets_projects a, "+
				"ets.wf_def b, "+
				"ets.wf_stage_identify_setmet c, "+
				"ets.wf_stage_document_setmet d "+
				" "+
				"where a.project_id=b.project_id and b.wf_id = c.wf_id and b.wf_id = d.wf_id and "+
				"c.wf_id='"+workflowID+"' with ur";
			
			DBAccess db = null;
			try{
				db = new DBAccess();
				db.prepareDirectQuery(query);
				if(db.execute()==1)
				{
					q.setQbrName(db.getString(0,0));
					q.setQuarter(db.getString(0,1));
					q.setYear(db.getString(0,2));
					q.setClient(db.getString(0,3));
					q.setAreaRated(db.getString(0,4));
					q.setSegment(db.getString(0,5));
					q.setRatingPeriod(toUSDate(db.getString(0,6))+"-"+toUSDate(db.getString(0,7)));
					String[] temp1 = getOveralls(workflowID);
					q.setCurrentScore(temp1[0]);
					q.setOverall_comments(temp1[1]);
					q.setChange("NA");
					q.setOldScore("NA");
					q.setReportTitle(db.getString(0,"rt")==null?"QBR Scoring Summary":db.getString(0,"rt"));
				}
				q.setRank(q.getCurrentScore());
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
		}
		
		String query =
			"select concat(concat(b.fname,' '),b.lname) from ets.wf_setmet_attendees_client a, ets.wf_client b where wf_id='"+workflowID+"' and a.userid = b.client_id";
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
			}
			q.setClient_attendees(s);
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
	
	public ArrayList getScore(String workflowID, String prevWFID)
	{
		ArrayList returnValue = new ArrayList();
		QBRScore q = null;
		if(prevWFID!=null)
		{
			String query = 
				"select distinct a.question_id qid, a.ques_desc desc, b.rating newrat, b.rating_comments comms, c.rating oldrat, d.bypass_score bpsNew, e.bypass_score bpsOld from ets.wf_score_question_template a, ets.wf_score b, "+ 
				"ets.wf_score c, ets.wf_stage_document_setmet d,ets.wf_stage_document_setmet e where e.wf_id = c.wf_id and d.wf_id=b.wf_id and b.wf_id='"+workflowID+"' and b.question_id = a.question_id and c.question_id=b.question_id and "+
				"c.wf_id='"+prevWFID+"' order by a.question_id with ur";
			DBAccess db = null;
			try{
				db = new DBAccess();
				db.prepareDirectQuery(query);
				int nrows = db.execute();
				if(nrows > 0)
				{
					for(int i=0;i<nrows; i+=2)
					{
						q = new QBRScore();
						q.setClientAttribute(db.getString(i,"desc"));
						q.setNewScorePercent((null==db.getString(i,"newrat") || "0".equals(db.getString(i,"newrat")))?"-":db.getString(i,"newrat"));
						q.setCommentsProvided(db.getString(i,"comms"));
						q.setOldScorePercent((null==db.getString(i,"oldrat") || "0".equals(db.getString(i,"oldrat")))?"-":db.getString(i,"oldrat"));
						if(q.getOldScorePercent()==null|| !"N".equals(db.getString(i,"bpsOld")))q.setOldScorePercent("NA");
						if(q.getNewScorePercent()==null|| !"N".equals(db.getString(i,"bpsNew")))q.setNewScorePercent("NA");
						if(q.getCommentsProvided()==null|| !"N".equals(db.getString(i,"bpsNew")))q.setCommentsProvided("NA");
						if(!(
								("NA".equals(q.getNewScorePercent()) || "-".equals(q.getNewScorePercent()))
							  &&("NA".equals(q.getOldScorePercent()) || "-".equals(q.getOldScorePercent()))
							 )
						){
							
							returnValue.add(q);
						}
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
		}
		else
		{
			String query = 
				"select distinct a.question_id, a.ques_desc, b.rating, b.rating_comments, c.bypass_score bps from ets.wf_score_question_template a, ets.wf_score b, ets.wf_stage_document_setmet c "+ 
				"where c.wf_id=b.wf_id and b.wf_id='"+workflowID+"' and b.question_id = a.question_id "+
				"order by a.question_id with ur";
			DBAccess db = null;
			try{
				db = new DBAccess();
				db.prepareDirectQuery(query);
				int nrows = db.execute();
				if(nrows > 0)
				{
					for(int i=0;i<nrows; i+=2)
					{
						q = new QBRScore();
						q.setClientAttribute(db.getString(i,1));
						q.setNewScorePercent(db.getString(i,2));
						q.setCommentsProvided(db.getString(i,3));
						q.setOldScorePercent("NA");
						if("0".equals(q.getNewScorePercent()))q.setNewScorePercent("-");
						if(!"N".equals(db.getString(i,"bps")))q.setNewScorePercent("NA");
						if(!"N".equals(db.getString(i,"bps")))q.setCommentsProvided("NA");
						if(!("NA".equals(q.getNewScorePercent()) || "-".equals(q.getNewScorePercent())))
							returnValue.add(q);
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
		}
		return returnValue;
	}
	public String getPreviousWF(String workflowID)
	{
		String query = "select b.wf_id from ets.wf_def a, ets.wf_def b where " +
				" ((a.year = b.year and a.quarter=b.quarter+1) or" +
				" (cast(a.year as int) = cast(b.year as int)+1 and a.quarter = b.quarter-3))" +
				" and a.project_id=b.project_id and b.wf_type='QBR' and b.wf_curr_stage_name='Complete' and a.wf_id='"+workflowID +"'" +
				" order by b.wf_id desc with ur";
		String result = null;
		DBAccess db = null;
		try{
			db = new DBAccess();
			db.prepareDirectQuery(query);
			if(db.execute()>0)
				result = db.getString(0,0);
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
		return result;
	}
	public String toUSDate(String db2date)
	{
		if(db2date==null) return "NA";
		try{
			return db2date.split("-")[1] +"/"+ db2date.split("-")[2]+"/"+ db2date.split("-")[0];
		}catch(Exception e)
		{
			return "NA";
		}
	}
	public String[] getOveralls(String workflowID)
	{
		String[] retVal = new String[]{"",""};
		String query = "select b.rating, b.rating_comments, c.overal_comments from ets.wf_score_question_template a, ets.wf_score b, ets.wf_stage_document_setmet c where a.wf_type='QBR' and ques_desc='Client Input Rating' and b.question_id=a.question_id and b.wf_id='"+workflowID+"' and c.wf_id=b.wf_id with ur";
		DBAccess db = null;
		try{
			db = new DBAccess();
			db.prepareDirectQuery(query);
			if(db.execute()>0)
				retVal = new String[]{ db.getString(0,0)==null?"":db.getString(0,0),db.getString(0,2)==null?"":db.getString(0,2)}; 
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

