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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.ets.fe.workflow.core.WorkflowAction;
import oem.edge.ets.fe.workflow.core.WorkflowForm;
import oem.edge.ets.fe.workflow.setmet.validate.ValidateDocumentStageDAO;
import oem.edge.ets.fe.workflow.util.DocUtils;
import oem.edge.ets.fe.workflow.util.pdfutils.PDFReportMaker;
import oem.edge.ets.fe.workflow.util.pdfutils.QBRCommonData;
import oem.edge.ets.fe.workflow.util.pdfutils.QBRScore;
import oem.edge.ets.fe.workflow.util.pdfutils.QBRScorecardSummaryPDF;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


/**
 * Class       : ScoringSummaryAction
 * Package     : oem.edge.ets.fe.workflow.qbr.summary
 * Description : 
 * Date		   : Mar 5, 2007
 * 
 * @author     : Pradyumna Achar
 */
public class ScoringSummaryAction extends WorkflowAction {

	public ActionForward executeWorkflow(ActionMapping mapping,
			WorkflowForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {

		String wf_id = request.getParameter("workflowID");
		String mode = request.getParameter("mode");
		if("pdf".equals(mode) || "pdfAtt".equals(mode))
		{
			QBRScorecardSummaryPDF pdf = new QBRScorecardSummaryPDF();
			QBRCommonData commData = (new ScoringSummaryBL()).getCommonData(projectID,wf_id);
			ArrayList score = (new ScoringSummaryBL()).getScore(projectID, wf_id);
			
			String fileName = commData.getQbrName().replace(' ','_')+"-scoring.pdf";
			
			/*
			response.setHeader("Content-Type", "application/pdf");
			response.setContentLength(file.length());
			PrintWriter out = response.getWriter();
			out.println(file);
			out.close();
			out.flush();*/
			HashMap m = new HashMap();
			
			m.put("repTitle",commData.getReportTitle());
			m.put("client",commData.getClient());
			m.put("segment",commData.getSegment());
			m.put("areaRated",commData.getAreaRated());
			m.put("newScore",commData.getCurrentScore());
			m.put("oldScore",commData.getOldScore());
			m.put("change",commData.getChange());
			m.put("rank",commData.getCurrentScore());
			m.put("ratingPeriod",commData.getRatingPeriod());
			m.put("clientAttendees",commData.getClient_attendees());
			m.put("overallComments",commData.getOverall_comments());
			m.put("currentMonth",commData.getQuarter()+"Q"+commData.getYear());
			m.put("qbrName",commData.getQbrName());
			
			ArrayList rows = new ArrayList();
			
			ArrayList row = null;
			for(int i=0; i<score.size(); i++)
			{
				QBRScore scr = (QBRScore)score.get(i);
				row = new ArrayList();
				row.add(makeWrappable(scr.getClientAttribute()));row.add(scr.getOldScorePercent()+" -> "+scr.getNewScorePercent()+"");row.add(scr.getCommentsProvided());
				rows.add(row);
			}
			
			m.put("issueDesc",rows);
			
			PDFReportMaker pdf2 = new PDFReportMaker();
			System.out.println("Call PDFReportMaker");
			if("pdf".equals(mode))
			{
				response.setHeader("Content-disposition","attachment; filename="+fileName);
				pdf2.execute(m,PDFReportMaker.TYPE_QBR_SCORING,request,response,true);
				
			}
			else
			{
				InputStream is = null;
				pdf2.execute(m,PDFReportMaker.TYPE_QBR_SCORING,request,response,false);
				is = (InputStream)request.getAttribute("is");
				if(is==null)
					System.out.println("InputStream is null in Action");
				int size = ((Integer)request.getAttribute("size")).intValue();
				DocUtils docUtils = new DocUtils();
				docUtils.addDoc(projectID,wf_id,commData.getReportTitle(),fileName, size,is,loggedUser);
				request.removeAttribute("is");
				request.removeAttribute("size");
				return mapping.findForward("attachConfirm");
			}
			return null;
		}
		else
		{
			request.setAttribute("COMMON_DATA",(new ScoringSummaryBL()).getCommonData(projectID,wf_id));
			request.setAttribute("SCORE_DATA",(new ScoringSummaryBL()).getScore(projectID, wf_id));
			request.setAttribute("projectID",projectID);
			request.setAttribute("workflowID",wf_id);
			return mapping.findForward("scoringSummary");
		}
	}
	/**
	 * Make it 80 char long
	 */
	private String m80(String s)
	{
		return s+"          ";
		/*StringBuffer a = new StringBuffer(s);
		for(int k=0;k<80-s.length(); k++)
		a.append(" ");
		return a.toString();*/
	}
	private String makeWrappable(String s)
	{
		StringBuffer b = new StringBuffer(s.length()*2);
		for(int i=0; i<s.length(); i++)
		{
			b.append(s.substring(i,i+1));
			b.append("\u200b");
		}
		return b.toString();
	}
}

