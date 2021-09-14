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
package oem.edge.ets.fe.aic.metrics;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import oem.edge.common.DatesArithmatic;
import oem.edge.ets.fe.ETSParams;

public class AICMetricsFunctions implements AICMetricsFunctionsInt {

    private ETSParams etsParams;

	public AICMetricsFunctions(){
	}

	public void setETSParams(ETSParams params){
		this.etsParams = params;
	}


	//start get report list
	public AICMetricsResultObj getReportList(){
		AICMetricsResultObj obj = new AICMetricsResultObj();
		return obj;
	}
	//end get report list

	public AICMetricsResultObj getReportPageInfo(){
		AICMetricsResultObj obj = new AICMetricsResultObj(etsParams);

		boolean showAll = false;
		String reportid = getParameter(etsParams.getRequest(),"reportid");
		String option2 = getParameter(etsParams.getRequest(),"option2");
		obj.setReportId(reportid);
		obj.setReportName(AICMetricsList.getTitleByReportId(reportid));
		obj.setOption2(option2);

		obj.setFiltersShown(AICMetricsReports.getFilterSetting(obj, reportid));
		if(!(obj.getFiltersShown().contains("1")) && !option2.equals("search")){
			showAll = true;
			option2 = "search";
			obj.setOption2(option2);
		}
		obj.setColumnsToShow(AICMetricsReports.getColumnsToShowSetting(obj, reportid));
		
		obj.setFilterLists(etsParams,obj.getColumnsToShow());

		if(option2.equals("search")){
			String[] users = etsParams.getRequest().getParameterValues("users");
			String[] wkspcs = etsParams.getRequest().getParameterValues("wkspcs");
			String[] wsaccesstype = etsParams.getRequest().getParameterValues("wsAccessType");
			String[] comps = etsParams.getRequest().getParameterValues("comp");
			String[] teams = etsParams.getRequest().getParameterValues("delteams");
			String[] wstypes = etsParams.getRequest().getParameterValues("wstypes");
			String[] tabnames = etsParams.getRequest().getParameterValues("tabname");
			String[] issStatus = etsParams.getRequest().getParameterValues("issStatus");
			String[] inds = etsParams.getRequest().getParameterValues("inds");
			String[] geos = etsParams.getRequest().getParameterValues("geos");
			String[] roles = etsParams.getRequest().getParameterValues("roles");
			String intext = etsParams.getRequest().getParameter("intext");
			String[] brand = etsParams.getRequest().getParameterValues("brand");
			String[] process = etsParams.getRequest().getParameterValues("process");
			String[] sector = etsParams.getRequest().getParameterValues("sector");
			String[] sceSector = etsParams.getRequest().getParameterValues("sceSector");
			String[] sortOrder = etsParams.getRequest().getParameterValues("sortBy");
			
			//Added for Client Contact Reports
			String[] company = etsParams.getRequest().getParameterValues("crCompany");
			String[] country = etsParams.getRequest().getParameterValues("crCountry");
			String[] datasource = etsParams.getRequest().getParameterValues("crDatasource");
			String crFName = getParameter(etsParams.getRequest(),"crFname");
			String crLName = getParameter(etsParams.getRequest(),"crLname");
			String crEmailId = getParameter(etsParams.getRequest(),"crEmail");

			String bAllDates = getParameter(etsParams.getRequest(),"alldates");
			String frommonth = getParameter(etsParams.getRequest(),"frommonth");
			String fromday = getParameter(etsParams.getRequest(),"fromday");
			String fromyear = getParameter(etsParams.getRequest(),"fromyear");
			String tomonth = getParameter(etsParams.getRequest(),"tomonth");
			String today = getParameter(etsParams.getRequest(),"today");
			String toyear = getParameter(etsParams.getRequest(),"toyear");

			String[] clientDes = etsParams.getRequest().getParameterValues("cdlist");
			String[] expcats = etsParams.getRequest().getParameterValues("expcat");
			String[] advs = etsParams.getRequest().getParameterValues("advocate");
			String[] wsowners = etsParams.getRequest().getParameterValues("wsowner");
			String[] cvsource = etsParams.getRequest().getParameterValues("cvsource");

			obj.setSelectedUsers(users);
			obj.setSelectedWorkspaces(wkspcs);
			obj.setSelectedWsAccessTypes(wsaccesstype);
			obj.setSelectedComps(comps);
			obj.setSelectedTeams(teams);
			obj.setSelectedWsTypes(wstypes);
			obj.setSelectedTabNames(tabnames);
			obj.setSelectedIssueStatus(issStatus);
			obj.setSelectedInds(inds);
			obj.setSelectedGeos(geos);
			obj.setSelectedRoles(roles);
			obj.setSelectedBrand(brand);
			obj.setSelectedProcess(process);
			obj.setSelectedSector(sector);
			obj.setSelectedSceSector(sceSector);
			obj.setSelectedSortOrder(sortOrder);
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
			// added for client contacts
			obj.setSelectedCRcompany(company);
			obj.setSelectedCRcountry(country);
			obj.setSelectedCRdatasource(datasource);
			obj.setSelectedCRFname(crFName); 
			obj.setSelectedCRLname(crLName);
			obj.setSelectedCREmail(crEmailId);
			obj.setSelectedColsToShow(getSelColParameters(etsParams.getRequest(),obj,showAll));

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
		/*
		else if (reportid.equals("BC0002")){
			if (option2.equals("search")){
				obj = validateForm(obj);
				if (!obj.getErrorFlag())
					obj = getBladeLicenseActivity(obj);
			}
		}
		*/
		else if (reportid.equals("CR0001")){
			if (option2.equals("search")){
				obj = validateForm(obj);
				if (!obj.getErrorFlag())
					obj = getClientContacts(obj);
			}
		}
		else{
			obj = null;
		}

		return obj;
	}




	private AICMetricsResultObj getAvgSatData(AICMetricsResultObj resobj){

		Vector result = AICMetricsDAO.filterAvgSatData(resobj,etsParams);
		resobj.setSearchResult(result);

		return resobj;
	}

	private AICMetricsResultObj getOverAllSatRating(AICMetricsResultObj resobj){

		Vector result = AICMetricsDAO.filterOverallSat(resobj,etsParams);
		resobj.setSearchResult(result);

		return resobj;
	}

	private AICMetricsResultObj getAvgRatByCode(AICMetricsResultObj resobj){

		Vector result = AICMetricsDAO.filterAvgRatByCode(resobj,etsParams);
		resobj.setSearchResult(result);

		return resobj;
	}

	private AICMetricsResultObj getClientsWithNoInput(AICMetricsResultObj resobj){

		Vector result = AICMetricsDAO.filterClientsWithNoInput(resobj,etsParams);
		resobj.setSearchResult(result);

		return resobj;
	}

	private AICMetricsResultObj getAvgClientSatByClient(AICMetricsResultObj resobj){

		Vector result = AICMetricsDAO.filterAvgClientSatByClient(resobj,etsParams);
		resobj.setSearchResult(result);

		return resobj;
	}

	private AICMetricsResultObj getCurrentSetMetRating(AICMetricsResultObj resobj){

		Vector result = AICMetricsDAO.filterCurrentSetMetRating(resobj,etsParams);
		resobj.setSearchResult(result);

		return resobj;
	}

	private AICMetricsResultObj getExpRatingByClient(AICMetricsResultObj resobj){

		Vector result = AICMetricsDAO.filterExpRatingByClient(resobj,etsParams);
		resobj.setSearchResult(result);

		return resobj;
	}

	private AICMetricsResultObj getExpRatingFreqDistribution(AICMetricsResultObj resobj){

		Vector result = AICMetricsDAO.filterExpRatingFreqDistribution(resobj,etsParams);
		resobj.setSearchResult(result);

		return resobj;
	}

	/*
	private AICMetricsResultObj getOverAllSelfAssessSatRating(AICMetricsResultObj resobj){

		Vector result = AICMetricsDAO.filterOverallSASat(resobj,etsParams);
		resobj.setSearchResult(result);

		return resobj;
	}*/
	/*
	private AICMetricsResultObj getSelfAssessExpRatingByClient(AICMetricsResultObj resobj){

		Vector result = AICMetricsDAO.filterSAExpRatingByClient(resobj,etsParams);
		resobj.setSearchResult(result);

		return resobj;
	}*/

	private AICMetricsResultObj getActivityOverview(AICMetricsResultObj resobj){

		Vector result = AICMetricsDAO.filterProjectActivity(resobj,etsParams);
		resobj.setSearchResult(result);

		return resobj;
	}
	private AICMetricsResultObj getDocumentActivity(AICMetricsResultObj resobj){

		Vector result = AICMetricsDAO.filterDocumentActivity(resobj,etsParams);
		resobj.setSearchResult(result);

		return resobj;
	}
	private AICMetricsResultObj getIssueActivity(AICMetricsResultObj resobj){

		Vector result = AICMetricsDAO.filterIssueActivity(resobj,etsParams);
		resobj.setSearchResult(result);

		return resobj;
	}
	private AICMetricsResultObj getIssueActivityDetails(AICMetricsResultObj resobj){

		Vector result = AICMetricsDAO.filterIssueActivityDetails(resobj,etsParams);
		resobj.setSearchResult(result);

		return resobj;
	}
	private AICMetricsResultObj getUsageSummary(AICMetricsResultObj resobj){

		Vector result = AICMetricsDAO.filterUsageSummary(resobj,etsParams);
		resobj.setSearchResult(result);

		return resobj;
	}
	private AICMetricsResultObj getUsageDetails(AICMetricsResultObj resobj){

		Vector result = AICMetricsDAO.filterUsageDetails(resobj,etsParams);
		resobj.setSearchResult(result);

		return resobj;
	}
	private AICMetricsResultObj getMembershipDistribution(AICMetricsResultObj resobj){

		Vector result = AICMetricsDAO.filterMembershipDistribution(resobj,etsParams);
		resobj.setSearchResult(result);

		return resobj;
	}
	private AICMetricsResultObj getIssueTabMetrics(AICMetricsResultObj resobj){

		Vector result = AICMetricsDAO.filterIssueTabMetrics(resobj,etsParams);
		resobj.setSearchResult(result);

		return resobj;
	}
	private AICMetricsResultObj getTeamListing(AICMetricsResultObj resobj){

		Vector result = AICMetricsDAO.filterTeamListing(resobj,etsParams);
		resobj.setSearchResult(result);

		return resobj;
	}
	private AICMetricsResultObj getTeamMembership(AICMetricsResultObj resobj){

		Vector result = AICMetricsDAO.filterTeamMembership(resobj,etsParams);
		resobj.setSearchResult(result);

		return resobj;
	}
	private AICMetricsResultObj getBladeDocuments(AICMetricsResultObj resobj){

		Vector result = AICMetricsDAO.filterBladeDocHits(resobj,etsParams);
		resobj.setSearchResult(result);

		return resobj;
	}
/*
	private AICMetricsResultObj getBladeLicenseActivity(AICMetricsResultObj resobj){
		Vector result = AICMetricsDAO.filterBladeLicenseActivity(resobj,etsParams);
		resobj.setSearchResult(result);

		return resobj;
	}
*/
	private AICMetricsResultObj getClientContacts(AICMetricsResultObj resobj){
		Vector result = AICMetricsDAO.filterClientContactsInfo(resobj,etsParams);
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


	private AICMetricsResultObj validateForm(AICMetricsResultObj obj){
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

		if (h.get("brand").equals("1")){
			if (obj.getSelectedBrand().size()<=0){
				if (!obj.getErrorFlag()){
					obj.setErrorFlag(true);
					obj.setErrorMsg("You must specify a Brand from list");
					obj.setErrorField("brand");
				}
			}
		}

		if (h.get("process").equals("1")){
			if (obj.getSelectedProcess().size()<=0){
				if (!obj.getErrorFlag()){
					obj.setErrorFlag(true);
					obj.setErrorMsg("You must specify a Process from list");
					obj.setErrorField("process");
				}
			}
		}

		if (h.get("sector").equals("1")){
			if (obj.getSelectedSector().size()<=0){
				if (!obj.getErrorFlag()){
					obj.setErrorFlag(true);
					obj.setErrorMsg("You must specify a business Sector from list");
					obj.setErrorField("sector");
				}
			}
		}

		if (h.get("sceSector").equals("1")){
			if (obj.getSelectedSceSector().size()<=0){
				if (!obj.getErrorFlag()){
					obj.setErrorFlag(true);
					obj.setErrorMsg("You must specify a Sce Sector from list");
					obj.setErrorField("sceSector");
				}
			}
		}

		if (h.get("sortBy").equals("1")){
			if (obj.getSelectedSortOrder().size()<=0){
				if (!obj.getErrorFlag()){
					obj.setErrorFlag(true);
					obj.setErrorMsg("You must specify a Sort Order from list");
					obj.setErrorField("sortBy");
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

		if (h.get("crCountry").equals("1")){
			if (obj.getSelectedCRcountry().size()<=0){
				if (!obj.getErrorFlag()){
					obj.setErrorFlag(true);
					obj.setErrorMsg("You must specify a client country from list");
					obj.setErrorField("crCountry");
				}
			}
		}

		if (h.get("crCompany").equals("1")){
			if (obj.getSelectedCRcompany().size()<=0){
				if (!obj.getErrorFlag()){
					obj.setErrorFlag(true);
					obj.setErrorMsg("You must specify a client company from list");
					obj.setErrorField("crCompany");
				}
			}
		}
		if (h.get("crDatasource").equals("1")){
			if (obj.getSelectedCRdatasource().size()<=0){
				if (!obj.getErrorFlag()){
					obj.setErrorFlag(true);
					obj.setErrorMsg("You must specify a datasource from list");
					obj.setErrorField("crDatasource");
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

	private AICMetricsResultObj validateResultOptions(AICMetricsResultObj obj){
		if (obj.getSelectedColsToShow().size()<=0){
			obj.setErrorFlag(true);
			obj.setErrorMsg("You must select at least one column to display");
			obj.setErrorField("columnSelOptions");
		}

		return obj;
	}


	private String[] validateDates(AICMetricsResultObj obj,boolean allDates){
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

	private Vector getSelColParameters(HttpServletRequest req,AICMetricsResultObj obj,boolean showAll){
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

