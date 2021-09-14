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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import oem.edge.ets.fe.workflow.core.WorkflowAction;
import oem.edge.ets.fe.workflow.core.WorkflowForm;

import oem.edge.ets.fe.workflow.util.DocUtils;
import oem.edge.ets.fe.workflow.util.MiscUtils;
import oem.edge.ets.fe.workflow.util.pdfutils.PDFReportMaker;

/**
 * Class       : ScoringTemplateAction
 * Package     : oem.edge.ets.fe.workflow.sa.summary
 * Description : 
 * Date		   : Mar 24, 2007
 * 
 * @author     : Pradyumna Achar
 */
public class ScoringTempalteAction extends WorkflowAction{
	public ActionForward executeWorkflow(ActionMapping mapping, WorkflowForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String wf_id = request.getParameter("workflowID");
		String mode = request.getParameter("mode");
		SummaryBL bl = new SummaryBL();
		String[] nameAndTitle = bl.getNameAndTitle(wf_id);
		String msaName = nameAndTitle[0]; 
		CommonData c = bl.getCommonData(wf_id);
		
		ArrayList rows = bl.getMetrics(wf_id);
		ArrayList roz = new ArrayList();
		ArrayList row = null;
		for(int i=0; i<rows.size(); i++)
		{
			row = new ArrayList();
			String[] t = (String[])(rows.get(i));
			row.add(mw(t[0]));
			row.add(mw(t[1]));
			row.add(mw(t[2]));
			row.add(mw(t[3]));
			roz.add(row);
		}
		
		if("pdf".equals(mode)||"pdfAtt".equals(mode))
		{
			String fileName = msaName.replace(' ','_')+"-scoringTemplate.pdf";

			HashMap m = new HashMap();
			
			m.put("repTitle",mw("Scoring Template"));
			m.put("client",mw(c.getClient()));
			m.put("segment",mw(c.getSegment()));
			m.put("newScore",mw(c.getNewScore()));
			m.put("oldScore",mw(c.getOldScore()));
			m.put("newMonth",mw(c.getNewMonth() +" overall"));
			if("-".equals(c.getOldMonth()))
				m.put("oldMonth",mw(c.getOldMonth()));
			else
				m.put("oldMonth",mw(c.getOldMonth() + " overall"));
			m.put("prev0",mw(bl.getPrev0()));
			m.put("prev1",mw(bl.getPrev1()));
			m.put("prev2",mw(bl.getPrev2()));
			
			m.put("change",mw(c.getChange()));
			m.put("status",MiscUtils.imageURI("etsWorkflowImg"+c.getStatusBit())+".gif");
			//m.put("status","http://localhost:9080/technologyconnect/ets/ETSImageServlet.wss?proj=workflow1&mod=100"+c.getStatusBit());
			//m.put("status","c:\\yellow.gif");
			m.put("clientAttendees",mw(c.getClientAttendees()));
			m.put("currentMonth",mw(bl.getPrev0()+" "+bl.getCurrentYear()));
			m.put("msaName",mw(nameAndTitle[0]));
			
			m.put("metrics",roz);

			/*rows = bl.getIssueData(wf_id);
			row = null;
			for(int i=0; i<3; i++)
			{
				row = new ArrayList();
				row.add(mw("Thermal diode issue encountered at module test on one tester at Amkor facility"));
				row.add(mw("Key Issue(Quality)"));
				row.add(mw("Adolf Hitler, Leonardo da Vinci, Giovanni de Michelli"));
				row.add(mw("3/06"));
				row.add(mw("> Issue Description.\n> Comments added come here\n> Modified issue descriptions come here\n> Issue owner rejection, acceptance events\n> Issue status changes"));
				rows.add(row);
			}*/
			
			m.put("issueDesc",bl.getIssueData(wf_id));
			
			PDFReportMaker pdf2 = new PDFReportMaker();
			System.out.println("Call PDFReportMaker");
			if("pdf".equals(mode))
			{
				response.setHeader("Content-disposition","attachment; filename="+fileName);
				pdf2.execute(m,PDFReportMaker.TYPE_MSA_SCORING_TEMPLATE,request,response,true);
			}
			else
			{
				InputStream is = null;
				pdf2.execute(m,PDFReportMaker.TYPE_MSA_SCORING_TEMPLATE,request,response,false);
				is = (InputStream)request.getAttribute("is");
				if(is==null)
					System.out.println("InputStream is null in Action");
				int size = ((Integer)request.getAttribute("size")).intValue();
				DocUtils docUtils = new DocUtils();
				docUtils.addDoc(projectID,wf_id,"Scoring Template",fileName, size,is,loggedUser);
				request.removeAttribute("is");
				request.removeAttribute("size");
				return mapping.findForward("attachConfirm");
			}
			
			return null;
		}
		else
		{
			request.setAttribute("repTitle","Scoring Template");
			request.setAttribute("msaName",nameAndTitle[0]);
			request.setAttribute("COMMON_DATA",c);
			request.setAttribute("BL",bl);
			request.setAttribute("ISSUE_DATA",bl.getIssueData(wf_id));
			request.setAttribute("SCORE_DATA",rows);
			request.setAttribute("projectID",projectID);
			request.setAttribute("workflowID",wf_id);
			return mapping.findForward("overallSummary");
		}
	
	}

	/**
	 * mw{String} is same as qbr/summary/ScoringSummaryAction/makeWrappable{String}
	 */
	private String mw(String s)
	{
		if(s==null)
			return "NA";
		StringBuffer b = new StringBuffer(s.length()*2);
		for(int i=0; i<s.length(); i++)
		{
			b.append(s.substring(i,i+1));
			b.append("\u200b");
		}
		return b.toString();
	}

}

