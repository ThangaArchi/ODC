/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2000-2004                                     */
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
package oem.edge.ets.fe.aic.metrics;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import oem.edge.ets.fe.Defines;


public class AICMetricsPrintResults  {

	static public StringBuffer printColumns(AICMetricsResultObj resobj){
		StringBuffer buf = new StringBuffer();
		Vector cols = resobj.getColumnsToShow();

		buf.append("<tr><td headers=\"col1\" colspan=\"2\" class=\"tdblue\" height=\"18\">&nbsp;Select column(s) to display</td></tr>");
		if(resobj.getErrorFlag() && resobj.getErrorField().equals("columnSelOptions")){
			buf.append("<tr><td headers=\"col2\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
			buf.append("<tr><td headers=\"col3\" colspan=\"2\"><a name=\"res\" id=\"res\" href=\"#res\"></a><span style=\"color:#ff3333;\"><b>Input error: \""+resobj.getErrorMsg()+"\"</b></span></td></tr>");
			buf.append("<tr><td headers=\"col4\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");
		}
		buf.append("<tr><td headers=\"col5\" colspan=\"2\"><table border=\"0\" width=\"600\">");
		int iCol = 1;

		for(int i=0;i<cols.size();i++){
			String[] s = (String[])cols.elementAt(i);
			if (iCol>4){
				buf.append("</tr><tr>");
				iCol=1;
			}

			String sel_str = "";
			if (resobj.getSelectedColsToShow().contains(s[0]) || resobj.getOption2().equals(""))
				sel_str = "checked=\"checked\"";
			buf.append("<td><input type=\"checkbox\" name=\"colsel_"+s[0]+"\" value=\""+s[0]+"\" id=\"colsel_"+s[0]+"\" "+sel_str+">");
			buf.append("<label for=\"colsel_"+s[0]+"\">"+s[1]+"</label>");
			buf.append("<img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"5\" height=\"1\" alt=\"\" /></td>");

			iCol++;
		}
		if (iCol<4){
			while(iCol<=4){
				buf.append("<td>&nbsp;</td>");
				iCol++;
			}
			buf.append("</tr>");
		}

		buf.append("</table></td></tr>");
		buf.append("<tr><td headers=\"col6\" colspan=\"2\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"5\" alt=\"\" /></td></tr>");


		return buf;
	}




	static public StringBuffer printSearchResults(AICMetricsResultObj resobj, boolean download){
		StringBuffer buf = new StringBuffer();

		if (download){
			buf.append(getCSVResults(resobj,buf));
		}
		else{
			buf.append("<a name=\"res\" id=\"res\" href=\"#res\"></a>");
			buf.append(printResults(resobj));
		}
		return buf;
	}





	static public StringBuffer printResults(AICMetricsResultObj obj){
		StringBuffer buf = new StringBuffer();
		boolean gray_flag = true;

		if(obj.getSearchResult()!=null){
			Vector v = obj.getSearchResult();
			Vector cols = obj.getSelectedColsToShow();
			Vector allcols = obj.getColumnsToShow();

			buf.append("<table cellpadding=\"1\" cellspacing=\"2\"  width=\"600\" border=\"0\">");
			if (v.size()>0){
				buf.append("<tr><td headers=\"col7\" colspan=\""+cols.size()+"\" class=\"tdblue\" height=\"18\">&nbsp;Report result(s)</td></tr>");
				//buf.append("<tr bgcolor=\"#cccc99\">");
				buf.append("<tr>");

				for (int icols = 0; icols<allcols.size();icols++){
					String[] ss = (String[])allcols.elementAt(icols);
					if (cols.contains(ss[0])){
						buf.append("<th id=\"rep_result_cols\" width=\""+ss[4]+"\" valign=\"top\">"+ss[2]+"</th>");
					}
				}

				buf.append("</tr>");
				for(int i=0;i<v.size();i++){

					AICMetricsObj m = (AICMetricsObj)v.elementAt(i);

					if (gray_flag) {
						buf.append("<tr style=\"background-color:#eeeeee\">");
						gray_flag = false;
					}
					else {
						buf.append("<tr>");
						gray_flag = true;
					}

					for (int icols = 0; icols<allcols.size();icols++){
						String[] ss = (String[])allcols.elementAt(icols);
						if (cols.contains(ss[0])){
							buf.append("<td>"+m.getFieldByName(ss[3])+"</td>");
						}
					}
					buf.append("</tr>");
				}
			}
			else{
				buf.append("<tr><td headers=\"col8\" class=\"tdblue\" width=\"600\" height=\"18\">&nbsp;Report result(s)</td></tr>");
				buf.append("<tr><td>No results matched</td></tr>");
			}
			buf.append("</table>");
		}


		return buf;
	}


	static public StringBuffer getCSVResults(AICMetricsResultObj obj, StringBuffer b){
		StringBuffer buf = new StringBuffer();

		if(obj.getSearchResult()!=null){
			Vector v = obj.getSearchResult();
			Vector cols = obj.getSelectedColsToShow();
			Vector allcols = obj.getColumnsToShow();

			if (v.size()>0){
				for (int icols = 0; icols<allcols.size();icols++){
					String[] ss = (String[])allcols.elementAt(icols);
					
					if(ss.length >5){
					if (cols.contains(ss[0])){
						buf.append(ss[2]+",");
						if (ss[5].equals("1")){
							buf.append(",");
						}
					}
					}else{
					
						if (ss.length > 0 && cols.contains(ss[0])){
							if(ss.length >2)
							buf.append(ss[2]+",");
						}
					}
				}
				buf.append("\n");

				for(int i=0;i<v.size();i++){
					AICMetricsObj m = (AICMetricsObj)v.elementAt(i);
					for (int icols = 0; icols<allcols.size();icols++){
						String[] ss = (String[])allcols.elementAt(icols);
						if (cols.contains(ss[0])){
							buf.append(m.getFieldByName(ss[3])+",");
						}
					}
					buf.append("\n");
				}
			}
			else{
				buf.append("No results matched\n");
			}
		}


		return buf;
	}




}

