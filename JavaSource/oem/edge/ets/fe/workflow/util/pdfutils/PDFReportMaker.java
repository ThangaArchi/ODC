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


package oem.edge.ets.fe.workflow.util.pdfutils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.struts.action.ActionForward;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class       : PDFReportMaker
 * Package     : oem.edge.ets.fe.workflow.util.pdfutils
 * Description : 
 * Date		   : Mar 20, 2007
 * 
 * @author     : Pradyumna Achar
 */
public class PDFReportMaker implements ReportConstants {
	

	
	public ActionForward execute(HashMap map, int reportType, HttpServletRequest request, HttpServletResponse response, boolean sendToResponse){
		
		InputStream template = null;
		InputStream transformation = null;
		System.out.println("Servlet Path = " + request.getServletPath());
		try{
			transformation = new FileInputStream(ReportConfig.xslFile);
			if(reportType==TYPE_QBR_SCORING)	
				template = new FileInputStream(ReportConfig.qbrReportFile);
			if(reportType==TYPE_MSA_OVERALL)	
				template = new FileInputStream(ReportConfig.msaReportFile);
			if(reportType==TYPE_MSA_OVERALL_TEMPLATE)	
				template = new FileInputStream(ReportConfig.msaOverallTemplateFile);
			if(reportType==TYPE_MSA_SCORING_TEMPLATE)	
				template = new FileInputStream(ReportConfig.msaScoringTemplateFile);
		}catch(Exception e)
		{
			System.err.println(e);
		}
		System.out.println("Template = "+template+", Transformation="+transformation);
		if(template==null || transformation == null)
		{
			
			return null;
		}
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder parser = null;
		try{
			 parser = factory.newDocumentBuilder();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		Document doc = null;
		try{
			doc = parser.parse(template);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		if(map!=null)
		{
			ArrayList keys = new ArrayList();
			keys.addAll(map.keySet());
			for(int i=0; i<keys.size(); i++)
			{
				if(doc.getElementById((String)keys.get(i))==null)continue;
				
				System.out.println("Process "+keys.get(i));
				Object obj = map.get(keys.get(i));
				if(obj instanceof String)
				{
					if("repTitle".equals(keys.get(i)))
					{
						doc.getElementsByTagName("title").item(0).appendChild(doc.createTextNode((String)obj));
					}
					Element e = doc.getElementById((String)keys.get(i));
					if("img".equalsIgnoreCase(e.getTagName()))
						e.setAttribute("src",(String)obj);
					else
					{
						for(int j=0; j<e.getChildNodes().getLength(); j++)
						{
							e.removeChild(e.getChildNodes().item(j));
						}
						e.appendChild(doc.createTextNode((String)obj));
					}
					System.out.println("Change String");
				}
				
				if(obj instanceof ArrayList)
				{
					Element table = doc.getElementById((String)keys.get(i));
					
					boolean propagate = false;
					if("propagate".equals(table.getAttribute("style")))
						propagate = true;
					
					/*for(int l=1; l<table.getChildNodes().getLength(); l++)
					{
						table.removeChild(table.getChildNodes().item(l));
					}
					*/
					ArrayList rows = (ArrayList)obj;
					for(int j=0; j<rows.size(); j++)
					{
						Element newRow = doc.createElement("tr");
						System.out.println("Add Row");
						ArrayList row = (ArrayList)rows.get(j);
						for(int k=0; k<row.size(); k++)
						{
							Element newCol = doc.createElement("td");
							/*System.out.println("Add col");
							System.out.println("TRs="+table.getElementsByTagName("tr").getLength());
							System.out.println("TDs="+((Element)(table.getElementsByTagName("tr").item(0))).getElementsByTagName("td").getLength());
							System.out.println("K="+k);*/
							String headerBG = ((Element)(((Element)(table.getElementsByTagName("tr").item(0))).getElementsByTagName("td").item(k))).getAttribute("bgcolor");
							if(propagate)
								newCol.setAttribute("bgcolor",headerBG);
							else
								newCol.setAttribute("bgcolor","#e1e2e2");
							if("embolden".equals(((Element)(((Element)(table.getElementsByTagName("tr").item(0))).getElementsByTagName("td").item(k))).getAttribute("style")))
							{
								Element b = doc.createElement("b");
								b.appendChild(doc.createTextNode((String)row.get(k)));
								newCol.appendChild(b);
							}
							else
								newCol.appendChild(doc.createTextNode((String)row.get(k)));
							newRow.appendChild(newCol);
						}
						table.appendChild(newRow);
					}
				}
			}
		}
		
		try {
			
			PdfGenerator pdfGenerator = new PdfGenerator();
			if (doc != null) {
				if(sendToResponse)
				{
					response.setContentType("application/pdf");
					pdfGenerator.generatePdf(doc, transformation, response.getOutputStream());
				}
				else
				{
					ByteArrayOutputStream tempOutStream = new ByteArrayOutputStream();
					pdfGenerator.generatePdf(doc, transformation, tempOutStream);
					byte[] ba = tempOutStream.toByteArray();
					request.setAttribute("is",new ByteArrayInputStream(ba));
					request.setAttribute("size",new Integer(ba.length));
				}
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
		return null;
	}
}