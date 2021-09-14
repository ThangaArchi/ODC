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

/**
 * Class       : SummaryBL
 * Package     : oem.edge.ets.fe.workflow.sa.summary
 * Description : 
 * Date		   : Mar 24, 2007
 * 
 * @author     : Pradyumna Achar
 */
public class SummaryBL {
	
	private String currentMonth = null;
	private String currentYear = null;
	private String prev1 = null;
	private String prev2 = null;
	
	public CommonData getCommonData(String workflowID)
	{
		CommonData r = new CommonData();
		SummaryDAO dao = new SummaryDAO();
		r.setClientAttendees(dao.getClientAttendees(workflowID));
		
		boolean isPrevDummy = false;
		String prevWF = dao.getPrevWF(workflowID);
		
		if(prevWF ==null){prevWF=workflowID; isPrevDummy = true;}
		
		String[] temp = dao.getCommData(prevWF,workflowID);
		r.setClient(temp[0]);
		r.setSegment(temp[1]);
		r.setNewScore(temp[2]);
		r.setOldScore(temp[3]);
		r.setStatusBit(convert(temp[4]));
		r.setNewMonth(dao.getCurrentMonth(temp[5]));
		this.currentMonth = temp[5];
		this.currentYear = temp[6];
		/*prev1 = dao.getCurrentMonth(dao.getPrevWF(workflowID));
		prev2 = dao.getCurrentMonth(dao.getPrevWF(prev1));
		*/
		prev1 = dao.getCurrentMonth(dao.getPrevMonth(temp[5]));
		prev2 = dao.getCurrentMonth(dao.getPrevMonth(dao.getPrevMonth(temp[5])));
		
		r.setOldMonth(prev1);
		try{
			double old =Double.parseDouble(r.getOldScore());
			double neu =Double.parseDouble(r.getNewScore());
			r.setChange(""+(neu-old));
		}catch(Exception e)
		{
			r.setNewScore("-");
			r.setChange("-");
			r.setOldScore("-");
		}
		if(isPrevDummy)
		{
			r.setOldScore("-");
			r.setChange("-");
			r.setOldMonth("-");
		}	
		return r;
	}
	public ArrayList getMetrics(String workflowID)
	{
		SummaryDAO dao = new SummaryDAO();
		
		String prev0 = workflowID;
		String prev1 = dao.getPrevWF(prev0);
		String prev2year = null;
		if(Integer.parseInt(currentMonth)<3)
			prev2year = ""+(Integer.parseInt(currentYear)-1);
		else
			prev2year = currentYear;
		String prev2 = dao.getPrevWF(prev0,prev2year,dao.getPrevMonth(dao.getPrevMonth(currentMonth)));
		boolean prev1dummy = false;
		boolean prev2dummy = false;
		if(prev1==null)
		{
			prev1dummy = true;
			prev1 = workflowID;
		}
		if(prev2==null)
		{
			prev2dummy = true;
			prev2 = workflowID;
		}
		ArrayList m =  dao.getMetricData(prev0, prev1, prev2);

		ArrayList n = new ArrayList();
		for(int i=0; i<m.size(); i++)
		{
			String[] t=(String[])m.get(i);
			n.add(new String[]{t[1], prev2dummy?"-":t[2],prev1dummy?"-":t[3],t[4]});
		}
		return n;
		
	}
	public ArrayList getIssueData(String workflowID)
	{
		ArrayList r = new ArrayList();
		SummaryDAO dao = new SummaryDAO();
		ArrayList t = dao.getIssues(workflowID);
		ArrayList n = null;
		for(int i=0; i<t.size(); i++)
		{
			n= new ArrayList();
			String[] p = (String[])t.get(i);
			String issue_id = p[3];
			n.add(p[2]);
			n.add(p[0]+" ( "+p[1]+" ) ");
			n.add(dao.getOwners(issue_id));
			n.add(dao.getMonthOpened(issue_id));
			n.add(dao.getComments(issue_id));
			r.add(n);
		}
		return r;
	}
	public String[] getNameAndTitle(String workflowID)
	{
		return (new SummaryDAO()).getNameAndTitle(workflowID);
	}
	
	public String getPrev0() {
		return (new SummaryDAO()).getCurrentMonth(currentMonth);
	}
	public String getPrev1() {
		return prev1;
	}
	public String getPrev2() {
		return prev2;
	}
	public String getCurrentYear() {
		return currentYear;
	}
	private String convert(String s)
	{
		if("1".equals(s))return "3";
		if("2".equals(s))return "1";
		if("3".equals(s))return "2";
		return "";
	}
}


