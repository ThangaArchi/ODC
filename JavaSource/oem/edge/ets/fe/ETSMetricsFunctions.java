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
/*                                                                          */
/*   --------------------------------------------------------------------   */
/*     Please do not remove any of these commented lines  20 lines          */
/*   --------------------------------------------------------------------   */
/*                       Copyright Footer Check                             */
package oem.edge.ets.fe;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import oem.edge.common.DatesArithmatic;

public class ETSMetricsFunctions implements ETSMetricsFunctionsInt {

    private ETSParams etsParams;

	public ETSMetricsFunctions(){
	}
	
	public void setETSParams(ETSParams params){
		this.etsParams = params;	
	}
	
	
	//start get report list
	public ETSMetricsResultObj getReportList(){
		ETSMetricsResultObj obj = new ETSMetricsResultObj();
		return obj;
	}
	//end get report list

	public ETSMetricsResultObj getReportPageInfo(){
		ETSMetricsResultObj obj = new ETSMetricsResultObj(etsParams);
		
		boolean showAll = false;
		String reportid = getParameter(etsParams.request,"reportid");
		String option2 = getParameter(etsParams.request,"option2");
		obj.setReportId(reportid);
		obj.setReportName(ETSMetricsList.getTitleByReportId(reportid));
		obj.setOption2(option2);
		
		obj.setFiltersShown(ETSMetricsReports.getFilterSetting(obj, reportid));
		if(!(obj.getFiltersShown().contains("1")) && !option2.equals("search")){
			showAll = true;
			option2 = "search";
			obj.setOption2(option2);
		}
		obj.setColumnsToShow(ETSMetricsReports.getColumnsToShowSetting(obj, reportid));

		obj.setFilterLists(etsParams);

		if(option2.equals("search")){
			String[] users = etsParams.request.getParameterValues("users");
			String[] wkspcs = etsParams.request.getParameterValues("wkspcs");
			String[] comps = etsParams.request.getParameterValues("comp");
			String[] teams = etsParams.request.getParameterValues("delteams");
			String[] wstypes = etsParams.request.getParameterValues("wstypes");
			String[] tabnames = etsParams.request.getParameterValues("tabname");
			String[] issStatus = etsParams.request.getParameterValues("issStatus");
			String[] inds = etsParams.request.getParameterValues("inds");
			String[] geos = etsParams.request.getParameterValues("geos");
			String[] roles = etsParams.request.getParameterValues("roles");
			String intext = etsParams.request.getParameter("intext");
			
			String bAllDates = getParameter(etsParams.request,"alldates");
			String frommonth = getParameter(etsParams.request,"frommonth");
			String fromday = getParameter(etsParams.request,"fromday");
			String fromyear = getParameter(etsParams.request,"fromyear");
			String tomonth = getParameter(etsParams.request,"tomonth");
			String today = getParameter(etsParams.request,"today");
			String toyear = getParameter(etsParams.request,"toyear");
			
			String[] clientDes = etsParams.request.getParameterValues("cdlist");	
			String[] expcats = etsParams.request.getParameterValues("expcat");
			String[] advs = etsParams.request.getParameterValues("advocate");
			String[] wsowners = etsParams.request.getParameterValues("wsowner");
			String[] cvsource = etsParams.request.getParameterValues("cvsource");

			obj.setSelectedUsers(users);
			obj.setSelectedWorkspaces(wkspcs);
			obj.setSelectedComps(comps);
			obj.setSelectedTeams(teams);
			obj.setSelectedWsTypes(wstypes);
			obj.setSelectedTabNames(tabnames);
			obj.setSelectedIssueStatus(issStatus);
			obj.setSelectedInds(inds);
			obj.setSelectedGeos(geos);
			obj.setSelectedRoles(roles);
			obj.setSelectedIntExt(intext);
			obj.setAllDates(bAllDates);
			obj.setFromMonth(frommonth);
			obj.setFromDay(fromday);
			obj.setFromYear(fromyear);
			obj.setToMonth(tomonth);
			obj.setToDay(today);
			obj.setToYear(toyear);
			obj.setSelectedClientDes(clientDes);
			obj.setSelectedExpCats(expcats);
			obj.setSelectedAdvocate(advs);
			obj.setSelectedWSOwner(wsowners);
			obj.setSelectedCVSource(cvsource);

			obj.setSelectedColsToShow(getSelColParameters(etsParams.request,obj,showAll));

		}
		
		if (reportid.equals("PR0001")){
			if (option2.equals("search")){
				obj = validateForm(obj);
				if (!obj.getErrorFlag())
					obj = getAvgSatData(obj);
			}			
		}
		else if (reportid.equals("PR0002")){
			if (option2.equals("search")){
				obj = validateForm(obj);
				if (!obj.getErrorFlag())
					obj = getExpRatingFreqDistribution(obj);
			}			
		}
		else if (reportid.equals("PR0003")){
			if (option2.equals("search")){
				obj = validateForm(obj);
				if (!obj.getErrorFlag())
					obj = getAvgRatByCode(obj);
			}			
		}
		else if (reportid.equals("PR0004")){
			if (option2.equals("search")){
				obj = validateForm(obj);
				if (!obj.getErrorFlag())
					obj = getClientsWithNoInput(obj);
			}			
		}
		else if (reportid.equals("CV0001")){
			if (option2.equals("search")){
				obj = validateForm(obj);
				if (!obj.getErrorFlag())
					obj = getAvgClientSatByClient(obj);
			}			
		}
		else if (reportid.equals("CV0002")){
			if (option2.equals("search")){
				obj = validateForm(obj);
				if (!obj.getErrorFlag())
					obj = getCurrentSetMetRating(obj);
			}			
		}
		else if (reportid.equals("CV0003")){
			if (option2.equals("search")){
				obj = validateForm(obj);
				if (!obj.getErrorFlag())
					obj = getOverAllSatRating(obj);
			}			
		}
		else if (reportid.equals("CV0004")){
			if (option2.equals("search")){
				obj = validateForm(obj);
				if (!obj.getErrorFlag())
					obj = getExpRatingByClient(obj);
			}			
		}
		else if (reportid.equals("WS0001")){
			if (option2.equals("search")){
				obj = validateForm(obj);
				if (!obj.getErrorFlag())
					obj = getActivityOverview(obj);
			}			
		}
		else if (reportid.equals("WS0002")){
			if (option2.equals("search")){
				obj = validateForm(obj);
				if (!obj.getErrorFlag())
					obj = getDocumentActivity(obj);
			}			
		}
		else if (reportid.equals("WS0003")){
			if (option2.equals("search")){
				obj = validateForm(obj);
				if (!obj.getErrorFlag())
					obj = getIssueActivity(obj);
			}			
		}
		else if (reportid.equals("WS0004")){
			if (option2.equals("search")){
				obj = validateForm(obj);
				if (!obj.getErrorFlag())
					obj = getIssueActivityDetails(obj);
			}			
		}
		else if (reportid.equals("WS0005")){
			if (option2.equals("search")){
				obj = validateForm(obj);
				if (!obj.getErrorFlag())
					obj = getUsageSummary(obj);
			}			
		}
		else if (reportid.equals("WS0006")){
			if (option2.equals("search")){
				obj = validateForm(obj);
				if (!obj.getErrorFlag())
					obj = getUsageDetails(obj);
			}			
		}
		else if (reportid.equals("WS0007")){
			if (option2.equals("search")){
				obj = validateForm(obj);
				if (!obj.getErrorFlag())
					obj = getMembershipDistribution(obj);
			}			
		}
		else if (reportid.equals("SA0001")){
			if (option2.equals("search")){
				obj = validateForm(obj);
				if (!obj.getErrorFlag())
					obj = getIssueTabMetrics(obj);
			}			
		}
		else if (reportid.equals("SA0002")){
			if (option2.equals("search")){
				obj = validateForm(obj);
				if (!obj.getErrorFlag())
					obj = getTeamListing(obj);
			}			
		}
		else if (reportid.equals("SA0003")){
			if (option2.equals("search")){
				obj = validateForm(obj);
				if (!obj.getErrorFlag())
					obj = getTeamMembership(obj);
			}			
		}
		else if (reportid.equals("BC0001")){
			if (option2.equals("search")){
				obj = validateForm(obj);
				if (!obj.getErrorFlag())
					obj = getBladeDocuments(obj);
			}			
		}
		else if (reportid.equals("BC0002")){
			if (option2.equals("search")){
				obj = validateForm(obj);
				if (!obj.getErrorFlag())
					obj = getBladeLicenseActivity(obj);
			}			
		}
		else{
			obj = null;	
		}
		
		return obj;
	}




	private ETSMetricsResultObj getAvgSatData(ETSMetricsResultObj resobj){
		
		Vector result = ETSMetricsDAO.filterAvgSatData(resobj,etsParams);
		resobj.setSearchResult(result);
	
		return resobj;
	}

	private ETSMetricsResultObj getOverAllSatRating(ETSMetricsResultObj resobj){
		
		Vector result = ETSMetricsDAO.filterOverallSat(resobj,etsParams);
		resobj.setSearchResult(result);
		
		return resobj;
	}
	
	private ETSMetricsResultObj getAvgRatByCode(ETSMetricsResultObj resobj){
		
		Vector result = ETSMetricsDAO.filterAvgRatByCode(resobj,etsParams);
		resobj.setSearchResult(result);
		
		return resobj;
	}
	
	private ETSMetricsResultObj getClientsWithNoInput(ETSMetricsResultObj resobj){
		
		Vector result = ETSMetricsDAO.filterClientsWithNoInput(resobj,etsParams);
		resobj.setSearchResult(result);
	
		return resobj;
	}
	
	private ETSMetricsResultObj getAvgClientSatByClient(ETSMetricsResultObj resobj){
		
		Vector result = ETSMetricsDAO.filterAvgClientSatByClient(resobj,etsParams);
		resobj.setSearchResult(result);

		return resobj;
	}
	
	private ETSMetricsResultObj getCurrentSetMetRating(ETSMetricsResultObj resobj){
		
		Vector result = ETSMetricsDAO.filterCurrentSetMetRating(resobj,etsParams);
		resobj.setSearchResult(result);

		return resobj;
	}
	
	private ETSMetricsResultObj getExpRatingByClient(ETSMetricsResultObj resobj){
		
		Vector result = ETSMetricsDAO.filterExpRatingByClient(resobj,etsParams);
		resobj.setSearchResult(result);
		
		return resobj;
	}
	
	private ETSMetricsResultObj getExpRatingFreqDistribution(ETSMetricsResultObj resobj){
		
		Vector result = ETSMetricsDAO.filterExpRatingFreqDistribution(resobj,etsParams);
		resobj.setSearchResult(result);
		
		return resobj;
	}
	
	/*
	private ETSMetricsResultObj getOverAllSelfAssessSatRating(ETSMetricsResultObj resobj){
		
		Vector result = ETSMetricsDAO.filterOverallSASat(resobj,etsParams);
		resobj.setSearchResult(result);
	
		return resobj;
	}*/
	/*
	private ETSMetricsResultObj getSelfAssessExpRatingByClient(ETSMetricsResultObj resobj){
		
		Vector result = ETSMetricsDAO.filterSAExpRatingByClient(resobj,etsParams);
		resobj.setSearchResult(result);
	
		return resobj;
	}*/
	
	private ETSMetricsResultObj getActivityOverview(ETSMetricsResultObj resobj){
					
		Vector result = ETSMetricsDAO.filterProjectActivity(resobj,etsParams);
		resobj.setSearchResult(result);
		
		return resobj;
	}
	private ETSMetricsResultObj getDocumentActivity(ETSMetricsResultObj resobj){
					
		Vector result = ETSMetricsDAO.filterDocumentActivity(resobj,etsParams);
		resobj.setSearchResult(result);
		
		return resobj;
	}
	private ETSMetricsResultObj getIssueActivity(ETSMetricsResultObj resobj){
					
		Vector result = ETSMetricsDAO.filterIssueActivity(resobj,etsParams);
		resobj.setSearchResult(result);
		
		return resobj;
	}
	private ETSMetricsResultObj getIssueActivityDetails(ETSMetricsResultObj resobj){
				
		Vector result = ETSMetricsDAO.filterIssueActivityDetails(resobj,etsParams);
		resobj.setSearchResult(result);
	
		return resobj;
	}
	private ETSMetricsResultObj getUsageSummary(ETSMetricsResultObj resobj){
					
		Vector result = ETSMetricsDAO.filterUsageSummary(resobj,etsParams);
		resobj.setSearchResult(result);
		
		return resobj;
	}
	private ETSMetricsResultObj getUsageDetails(ETSMetricsResultObj resobj){
					
		Vector result = ETSMetricsDAO.filterUsageDetails(resobj,etsParams);
		resobj.setSearchResult(result);
		
		return resobj;
	}
	private ETSMetricsResultObj getMembershipDistribution(ETSMetricsResultObj resobj){
					
		Vector result = ETSMetricsDAO.filterMembershipDistribution(resobj,etsParams);
		resobj.setSearchResult(result);
		
		return resobj;
	}
	private ETSMetricsResultObj getIssueTabMetrics(ETSMetricsResultObj resobj){
					
		Vector result = ETSMetricsDAO.filterIssueTabMetrics(resobj,etsParams);
		resobj.setSearchResult(result);
		
		return resobj;
	}
	private ETSMetricsResultObj getTeamListing(ETSMetricsResultObj resobj){
				
		Vector result = ETSMetricsDAO.filterTeamListing(resobj,etsParams);
		resobj.setSearchResult(result);
	
		return resobj;
	}
	private ETSMetricsResultObj getTeamMembership(ETSMetricsResultObj resobj){
				
		Vector result = ETSMetricsDAO.filterTeamMembership(resobj,etsParams);
		resobj.setSearchResult(result);

		return resobj;
	}
	private ETSMetricsResultObj getBladeDocuments(ETSMetricsResultObj resobj){
				
		Vector result = ETSMetricsDAO.filterBladeDocHits(resobj,etsParams);
		resobj.setSearchResult(result);

		return resobj;
	}
	
	private ETSMetricsResultObj getBladeLicenseActivity(ETSMetricsResultObj resobj){
		Vector result = ETSMetricsDAO.filterBladeLicenseActivity(resobj,etsParams);
		resobj.setSearchResult(result);

		return resobj;
	}


	private String getParameter(HttpServletRequest req, String key) {
		  String value = req.getParameter(key);

		  if (value == null) {
			System.out.println(key+" = null");
			  return "";
		  } else {
			  return value;
		  }
	  }


	private ETSMetricsResultObj validateForm(ETSMetricsResultObj obj){
		Hashtable h = obj.getFiltersShown();
		
		if (h.get("companyList").equals("1")){
			if (obj.getSelectedComps().size()<=0){
				if (!obj.getErrorFlag()){
					obj.setErrorFlag(true);
					obj.setErrorMsg("You must specify a company from list");
					obj.setErrorField("companyList");
				}
			}
		}
		if (h.get("clientDesList").equals("1")){
			if (obj.getSelectedClientDes().size()<=0){
				if (!obj.getErrorFlag()){
					obj.setErrorFlag(true);
					obj.setErrorMsg("You must specify a client designation from list");
					obj.setErrorField("clientDesList");
				}
			}
		}

		if (h.get("deliveryTeams").equals("1")){
			if (obj.getSelectedTeams().size()<=0){
				if (!obj.getErrorFlag()){
					obj.setErrorFlag(true);
					obj.setErrorMsg("You must specify a delivery team from list");
					obj.setErrorField("deliveryTeams");
				}
			}
		}
		
		if (h.get("exp_cat").equals("1")){
			if (obj.getSelectedExpCats().size()<=0){
				if (!obj.getErrorFlag()){
					obj.setErrorFlag(true);
					obj.setErrorMsg("You must specify an expectation category from list");
					obj.setErrorField("exp_cat");
				}
			}
		}
		
		/*if (h.get("exp_catsa").equals("1")){
			if (obj.getSelectedExpCats().size()<=0){
				if (!obj.getErrorFlag()){
					obj.setErrorFlag(true);
					obj.setErrorMsg("You must specify an expectation category from list");
					obj.setErrorField("exp_catsa");
				}
			}
		}*/
		
		if (h.get("wsTypes").equals("1")){
			if (obj.getSelectedWsTypes().size()<=0){
				if (!obj.getErrorFlag()){
					obj.setErrorFlag(true);
					obj.setErrorMsg("You must specify a workspace type from list");
					obj.setErrorField("wsTypes");
				}
			}
		}
		
		

		if (h.get("tabNames").equals("1")){
			if (obj.getSelectedTabNames().size()<=0){
				if (!obj.getErrorFlag()){
					obj.setErrorFlag(true);
					obj.setErrorMsg("You must specify a tab name from list");
					obj.setErrorField("tabNames");
				}
			}
		}
		
		if (h.get("inds").equals("1")){
			if (obj.getSelectedInds().size()<=0){
				if (!obj.getErrorFlag()){
					obj.setErrorFlag(true);
					obj.setErrorMsg("You must specify an industry from list");
					obj.setErrorField("inds");
				}
			}
		}
		
		if (h.get("geos").equals("1")){
			if (obj.getSelectedGeos().size()<=0){
				if (!obj.getErrorFlag()){
					obj.setErrorFlag(true);
					obj.setErrorMsg("You must specify a geography from list");
					obj.setErrorField("geos");
				}
			}
		}
		
		if (h.get("roles").equals("1")){
			if (obj.getSelectedRoles().size()<=0){
				if (!obj.getErrorFlag()){
					obj.setErrorFlag(true);
					obj.setErrorMsg("You must specify a role from list");
					obj.setErrorField("roles");
				}
			}
		}

		if (h.get("advocate").equals("1")){
			if (obj.getSelectedAdvocate().size()<=0){
				if (!obj.getErrorFlag()){
					obj.setErrorFlag(true);
					obj.setErrorMsg("You must specify an advocate from list");
					obj.setErrorField("advocate");
				}
			}
		}
		
		if (h.get("wsowner").equals("1")){
			if (obj.getSelectedWSOwner().size()<=0){
				if (!obj.getErrorFlag()){
					obj.setErrorFlag(true);
					obj.setErrorMsg("You must specify a workspace owner from list");
					obj.setErrorField("wsowner");
				}
			}
		}
		

		if (h.get("source").equals("1")){
			if (obj.getSelectedCVSource().size()<=0){
				if (!obj.getErrorFlag()){
					obj.setErrorFlag(true);
					obj.setErrorMsg("You must specify a source from list");
					obj.setErrorField("source");
				}
			}
		}
		
		if (h.get("date").equals("1")){
			if (!obj.getAllDates()){
				if (!obj.getErrorFlag()){
					String[] s = validateDates(obj,true);
					if (s[0].equals("0")){
						obj.setErrorFlag(true);
						obj.setErrorMsg(s[1]);
						//[2]  from=fromdate  to=todate  both=bothdate
						obj.setErrorField(s[2]);
					}
					else{
						obj.setFromDate();
						obj.setToDate();	
					}
				}
			}
		}
		
		if (h.get("datefrom").equals("1")){
			if (!obj.getErrorFlag()){
				String[] s = validateDates(obj,false);
				if (s[0].equals("0")){
					obj.setErrorFlag(true);
					obj.setErrorMsg(s[1]);
					obj.setErrorField(s[2]);
				}
				else{
					obj.setFromDate();
				}
			}
		}
		
		if (!obj.getErrorFlag()){
			obj = validateResultOptions(obj);
		}
		return obj;	
	}
	
	private ETSMetricsResultObj validateResultOptions(ETSMetricsResultObj obj){
		if (obj.getSelectedColsToShow().size()<=0){
			obj.setErrorFlag(true);
			obj.setErrorMsg("You must select at least one column to display");
			obj.setErrorField("columnSelOptions");
		}
			
		return obj;
	}
	
	
	private String[] validateDates(ETSMetricsResultObj obj,boolean allDates){
		String[] s = new String[]{"1","",""};
		String[] fromDate = obj.getFromDateStrs();
		String fmonth = fromDate[0];
		System.out.println("vf month="+fmonth);
		String fday = fromDate[1];
		String fyear = fromDate[2];
		String[] toDate = obj.getToDateStrs();
		String tmonth = toDate[0];
		String tday = toDate[1];
		String tyear = toDate[2];
			
		Calendar fcal = Calendar.getInstance();
		fcal.set(Calendar.DAY_OF_MONTH,1);
		
		if (fmonth.equals("-1")){
			s[0]="0";
			s[1]="A valid month for from date must be specified";
			s[2]="fromdate";
			return s;
		}
		else if (fday.equals("0")){
			s[0]="0";
			s[1]="A valid day for from date must be specified";
			s[2]="fromdate";
			return s;
		}
		else if (fyear.equals("0")){
			s[0]="0";
			s[1]="A valid year for from date must be specified";
			s[2]="fromdate";
			return s;
		}
		else{
			int month = Integer.parseInt(fmonth.trim());
			System.out.println("month="+month);
			int day = Integer.parseInt(fday.trim());
			int year = Integer.parseInt(fyear.trim());
				
			fcal.set(Calendar.YEAR,year);
			fcal.set(Calendar.MONTH,month);
			System.out.println(fcal.get(Calendar.MONTH));
			int iMaxDaysInMonth =  fcal.getActualMaximum(Calendar.DAY_OF_MONTH);
			int iMinDaysInMonth = fcal.getActualMinimum(Calendar.DAY_OF_MONTH);
			System.out.println("max  for '"+fcal.get(Calendar.MONTH)+"'= "+iMaxDaysInMonth);
			System.out.println("min  for '"+fcal.get(Calendar.MONTH)+"'= "+iMinDaysInMonth);
				
			if(iMinDaysInMonth<=day && day<=iMaxDaysInMonth){
				fcal.set(Calendar.DAY_OF_MONTH,day);				
			}
			else{
				s[0]="0";
				s[1]="Invalid from date specified.  Please review date entered";
				s[2]="fromdate";
				return s;
			}
		}
		
		
		if(allDates){
			Calendar tcal = Calendar.getInstance();
			if (tmonth.equals("-1")){
				s[0]="0";
				s[1]="A valid month for to date must be specified";
				s[2]="todate";
				return s;
			}
			else if (tday.equals("0")){
				s[0]="0";
				s[1]="A valid day for to date must be specified";
				s[2]="todate";
				return s;
			}
			else if (tyear.equals("0")){
				s[0]="0";
				s[1]="A valid year for to date must be specified";
				s[2]="todate";
				return s;
			}
			else{
				int month = Integer.parseInt(tmonth.trim());
				int day = Integer.parseInt(tday.trim());
				int year = Integer.parseInt(tyear.trim());
			
				tcal.set(Calendar.YEAR,year);
				tcal.set(Calendar.MONTH,month);
				int iMaxDaysInMonth =  tcal.getActualMaximum(Calendar.DAY_OF_MONTH);
				int iMinDaysInMonth = tcal.getActualMinimum(Calendar.DAY_OF_MONTH);
				System.out.println("max  for '"+tcal.get(Calendar.MONTH)+"'= "+iMaxDaysInMonth);
				System.out.println("min  for '"+tcal.get(Calendar.MONTH)+"'= "+iMinDaysInMonth);
			
				if(iMinDaysInMonth<=day && day<=iMaxDaysInMonth){
					tcal.set(Calendar.DAY_OF_MONTH,day);				
				}
				else{
					s[0]="0";
					s[1]="Invalid to date specified.  Please review date entered";
					s[2]="todate";
					return s;
				}
			}
			
			if (tcal.before(fcal)){
				s[0]="0";
				s[1]="From date must be before to date";
				s[2]="bothdate";
				return s;
			}
		}
		
		
		
		return s;
	}

	private Vector getSelColParameters(HttpServletRequest req,ETSMetricsResultObj obj,boolean showAll){
		Vector v = new Vector();
		
		Vector cols = obj.getColumnsToShow();
		for (int i=0;i<cols.size();i++){
			String[] s = (String[])cols.elementAt(i);
			if ((getParameter(req,"colsel_"+s[0])!="") || showAll){
				v.addElement(s[0]);
			}
		}
		
		return v;
	}

}

