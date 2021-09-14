/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2001-2004                                     */
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

import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import oem.edge.ets.fe.ETSParams;
import oem.edge.ets.fe.Defines;

public class AICMetricsResultObj {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.4";


	static protected AICMetricsListValue[] prlist;
    static protected AICMetricsListValue[] cclist;
	static protected AICMetricsListValue[] projactlist;
	static protected AICMetricsListValue[] salist;
	static protected AICMetricsListValue[] bladelist;
	static protected AICMetricsListValue[] clientreportslist;
	static protected AICMetricsListValue[] salesreportslist;//added for workflow
	protected Vector userList;
	protected Vector workspaceList;
	protected Vector companyList;
	protected Vector deliveryTeamList;
	protected Vector expCatList;
	protected Vector tabNameList;
	protected Vector searchResult;
	protected Vector indsList;
	protected Vector geosList;
	protected Vector rolesList;
	protected Vector advocateList;
	protected Vector wsownerList;
	protected Vector brandList;
	protected Vector processList;
	protected Vector sectorList;
	protected Vector sceSectorList;
	protected Vector sortOrderList;
	protected String[] intextList;
	protected String[] clientDesList;
	protected String[] clientDesValList;
	protected String[] wsTypesList;
	protected String[] issueStatusList;
	protected String[] cvSourceList;
	protected String[] wsAccessTypeList;
	protected Hashtable filtersShown;
	protected String reportName;
	protected String reportid;
	protected String option2;
	protected Vector selectedUsers;
	protected Vector selectedWorkspaces;
	protected Vector selectedComps;
	protected Vector selectedTeams;
	protected Vector selectedWsTypes;
	protected Vector selectedWsAccessTypes;
	protected Vector selectedClientDes;
	protected Vector selectedExpCats;
	protected Vector selectedTabNames;
	protected Vector selectedIssueStatus;
	protected Vector selectedColsToShow;
	protected Vector selectedInds;
	protected Vector selectedGeos;
	protected Vector selectedRoles;
	protected String selectedIntExt;
	protected Vector selectedAdvocate;
	protected Vector selectedWSOwner;
	protected Vector selectedCVSource;
	protected Vector selectedBrand;
	protected Vector selectedProcess;
	protected Vector selectedSector;
	protected Vector selectedSceSector;
	protected Vector selectedSortOrder;
	protected boolean bAllDates;
	protected boolean errorFlag;
	protected String errorField;
	protected String errorMsg;
	protected String fromMonth;
	protected String fromDay;
	protected String fromYear;
	protected long fromDate;
	protected String toMonth;
	protected String toDay;
	protected String toYear;
	protected long toDate;
	protected String title;
	// added for Client Reports
	protected Vector selectedCRcompany;
	protected Vector selectedCRcountry;
	protected Vector selectedCRdatasource;
	protected String selectedCRFname;
	protected String selectedCRLname;
	protected String selectedCREmail;
	protected Vector crCompanyList;
	protected Vector crCountryList;
	protected String crFNameList;
	protected String crLNameList;
	protected String crEmailList;
	protected Vector crDatasourceList;
	
	

	protected Vector columnsToShow;

	public AICMetricsResultObj(){
		prlist = AICMetricsList.getPRMetricsList();
		cclist = AICMetricsList.getCCMetricsList();
		projactlist = AICMetricsList.getPAMetricsList();
		salist = AICMetricsList.getSAMetricsList();
		bladelist = AICMetricsList.getBladeMetricsList();
		clientreportslist = AICMetricsList.getCRMetricsList();
		salesreportslist = AICMetricsList.getSMRMetricsList();

	 }



    public AICMetricsResultObj(ETSParams etsparams){
		prlist = AICMetricsList.getPRMetricsList();
		cclist = AICMetricsList.getCCMetricsList();
		projactlist = AICMetricsList.getPAMetricsList();
		salist = AICMetricsList.getSAMetricsList();
		bladelist = AICMetricsList.getBladeMetricsList();
		clientreportslist = AICMetricsList.getCRMetricsList();
		salesreportslist = AICMetricsList.getSMRMetricsList();
		/*companyList = AICMetricsDAO.getAllCompanyList(etsparams);
		userList = AICMetricsDAO.getAllUsersList();
		workspaceList = AICMetricsDAO.getAllWorkspaceList();
		deliveryTeamList = AICMetricsDAO.getDeliveryTeamList();
		expCatList = AICMetricsDAO.getExpCatList();
		tabNameList = AICMetricsDAO.getTabNameList();*/
		this.clientDesList = new String[]{"All values",Defines.METRICS_CLIENTCARE_STR,Defines.METRICS_FOCUSACCT_STR};
		this.clientDesValList = new String[]{"All values",Defines.METRICS_CLIENTCARE,Defines.METRICS_FOCUSACCT};
		this.wsTypesList = new String[]{"All values","Project","Proposal","Client voice"};
		this.issueStatusList = new String[]{"All values","Submitted","Assigned","Rejected","Resolved","Closed","Withdrawn"};
		this.intextList = new String[]{"Both","Internal","External"};
		this.cvSourceList = new String[]{"All values","Set/Met","Self Assessment"};
		this.wsAccessTypeList = new String[]{"All values","Public Workspace","Restricted Workspace","Private Workspace"};

		filtersShown = new Hashtable();
		setFiltersShown();
		searchResult = null;
		reportid = "";
		option2 = "";
		selectedUsers = new Vector();
		selectedWorkspaces = new Vector();
		selectedComps = new Vector();
		selectedTeams = new Vector();
		selectedWsTypes = new Vector();
		selectedWsAccessTypes = new Vector();
		selectedClientDes = new Vector();
		selectedExpCats = new Vector();
		selectedTabNames = new Vector();
		selectedIssueStatus = new Vector();
	    selectedColsToShow = new Vector();
		selectedInds = new Vector();
		selectedGeos = new Vector();
		selectedRoles = new Vector();
		selectedIntExt = "0";
		selectedAdvocate = new Vector();
		selectedWSOwner = new Vector();
		selectedCVSource = new Vector();
		selectedBrand = new Vector();
		selectedProcess = new Vector();
		selectedSector = new Vector();
		selectedSceSector = new Vector();
		selectedSortOrder = new Vector();
		// added for client Reports
		selectedCRFname = new String();
		selectedCRLname = new String();
		selectedCREmail = new String();
		selectedCRcountry = new Vector();
		selectedCRcompany = new Vector();
		selectedCRdatasource = new Vector();

		Map m = Collections.synchronizedMap(new HashMap());

		bAllDates = false;
		errorFlag = false;
		errorMsg = "";
		errorField = "";
		Calendar c = Calendar.getInstance();
		fromMonth = String.valueOf(c.get(Calendar.MONTH));
		fromDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
		fromYear = String.valueOf(c.get(Calendar.YEAR));
		fromDate = 0;
		toMonth = String.valueOf(c.get(Calendar.MONTH));
		toDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
		toYear = String.valueOf(c.get(Calendar.YEAR));
		toDate = 0;
		title = "";
    }


	public AICMetricsListValue[] getPRList(){
		return prlist;
	}

	public AICMetricsListValue[] getCCList(){
		return cclist;
	}

	public AICMetricsListValue[]  getProjectActivityList(){
		return projactlist;
	}

	public AICMetricsListValue[] getSAList(){
		return salist;
	}

	public AICMetricsListValue[] getClientReportsList(){
		return clientreportslist;
	}

	public AICMetricsListValue[]  getBladeActiviyList(){
		return bladelist;
	}

	public String[] getClientDesList(){
		return clientDesList;
	}
	public String[] getClientDesValList(){
		return clientDesValList;
	}

	public String[] getWSTypesList(){
		return wsTypesList;
	}

	public String[] getWSAccessTypesList(){
		return wsAccessTypeList;
	}

	public String[] getIssuesStatusList(){
		return issueStatusList;
	}

	public Vector getCompanyList(){
		return companyList;
	}

	public Vector getTabNameList(){
		return tabNameList;
	}

	public Vector getUserList(){
		return userList;
	}

	public Vector getWorkspaceList(){
		return workspaceList;
	}


	public Vector getDeliveryTeamList(){
		return deliveryTeamList;
	}

	public Vector getExpCatList(){
		return expCatList;
	}

	public void setReportId(String s){
		reportid = s;
	}
	public String getReportId(){
		return reportid;
	}

	public void setReportName(String s){
		reportName = s;
	}
	public String getReportName(){
		return reportName;
	}
	public String getEncReportName(){
		String s = reportName.replace('/',' ');
		return s;
	}
	public void setOption2(String s){
		option2 = s;
	}
	public String getOption2(){
		return option2;
	}

	public Vector getSearchResult(){
		return searchResult;
	}
	public void setSearchResult(Vector v){
		searchResult = v;
	}


	public Vector getIndsList(){
		return indsList;
	}
	public Vector getGeosList(){
		return geosList;
	}
	public Vector getRolesList(){
		return rolesList;
	}
	public String[] getIntExtList(){
		return intextList;
	}

	public Vector getAdvocateList(){
		return advocateList;
	}
	public Vector getWSOwnerList(){
		return wsownerList;
	}
	public Vector getBrandList(){
		return brandList;
	}
	public Vector getProcessList(){
		return processList;
	}
	public Vector getSectorList(){
		return sectorList;
	}
	public Vector getSceSectorList(){
		return sceSectorList;
	}
	public Vector getSortOrderList(){
		return sortOrderList;
	}
	// Added for Client reports
	public Vector getCRcountryList(){
		return crCountryList;
	}
	public Vector getCRcompanyList(){
		return crCompanyList;
	}
	public Vector getCRdatasourceList(){
		return crDatasourceList;
	}
	public String getCRFnameList(){
		return crFNameList;
	}
	public String getCRLnameList(){
		return crLNameList;
	}
	public String getCREmailList(){
		return crEmailList;
	}
		

	public String[] getCVSourceList(){
		return cvSourceList;
	}


	public void setFilterLists(ETSParams etsparams, Vector allCols){
		if (filtersShown.get("companyList").equals("1"))
			companyList = AICMetricsDAO.getAllCompanyList(etsparams);
		if (filtersShown.get("users").equals("1"))
			userList = AICMetricsDAO.getAllUsersList(etsparams);
		//if (filtersShown.get("wsAccessType").equals("1"))
		//	wsAccessTypeList = this.wsTypesList;
		if (filtersShown.get("wkspaces").equals("1"))
			workspaceList = AICMetricsDAO.getAllWorkspaceList(etsparams);
		if (filtersShown.get("deliveryTeams").equals("1"))
			deliveryTeamList = AICMetricsDAO.getDeliveryTeamList(etsparams);
		if (filtersShown.get("exp_cat").equals("1"))
			expCatList = AICMetricsDAO.getExpCatList(etsparams);
		//if (filtersShown.get("exp_catsa").equals("1"))
		//	expCatList = AICMetricsDAO.getSAExpCatList(etsparams);
		if (filtersShown.get("tabNames").equals("1"))
			tabNameList = AICMetricsDAO.getTabNameList(etsparams);
		if (filtersShown.get("inds").equals("1"))
			indsList = AICMetricsDAO.getIndsList(etsparams);
		if (filtersShown.get("geos").equals("1"))
			geosList = AICMetricsDAO.getGeosList(etsparams);
		if (filtersShown.get("roles").equals("1"))
			rolesList = AICMetricsDAO.getRolesList(etsparams);
		if (filtersShown.get("advocate").equals("1"))
			advocateList = AICMetricsDAO.getAdvocateList(etsparams);
		if (filtersShown.get("wsowner").equals("1"))
			wsownerList = AICMetricsDAO.getCVWSOwnerList(etsparams);
		if (filtersShown.get("brand").equals("1"))
			brandList = AICMetricsDAO.getBrandList(etsparams);
		if (filtersShown.get("process").equals("1"))
			processList = AICMetricsDAO.getProcessList(etsparams);
		if (filtersShown.get("sector").equals("1"))
			sectorList = AICMetricsDAO.getSectorList(etsparams);
		if (filtersShown.get("sceSector").equals("1"))
			sceSectorList = AICMetricsDAO.getSceSectorList(etsparams);
		if (filtersShown.get("sortBy").equals("1"))
			sortOrderList = AICMetricsDAO.getSortOrderList(etsparams,allCols);
		// Added for Client Reports
		if (filtersShown.get("crCompany").equals("1"))
			crCompanyList = AICMetricsDAO.getCRcompanyList(etsparams);
		if (filtersShown.get("crCountry").equals("1"))
			crCountryList = AICMetricsDAO.getCRcountryList(etsparams);
		if (filtersShown.get("crFname").equals("1"))
			crFNameList = AICMetricsDAO.getCRFnameList(etsparams);
		if (filtersShown.get("crLname").equals("1"))
			crLNameList = AICMetricsDAO.getCRLnameList(etsparams);
		if (filtersShown.get("crEmail").equals("1"))
			crEmailList = AICMetricsDAO.getCREmailList(etsparams);

		if (filtersShown.get("crDatasource").equals("1"))
			crDatasourceList = AICMetricsDAO.getCRdatasourceList(etsparams);
	}

	public void setFiltersShown(){
		filtersShown.put("companyList","0");
		filtersShown.put("clientDesList","0");
		filtersShown.put("date","0");
		filtersShown.put("exp_cat","0");
		//filtersShown.put("exp_catsa","0");
		filtersShown.put("deliveryTeams","0");
		filtersShown.put("wsTypes","0");
		filtersShown.put("tabNames","0");
		filtersShown.put("issueStatus","0");
		filtersShown.put("users","0");
		filtersShown.put("wkspaces","0");
		filtersShown.put("brand","0");
		filtersShown.put("process","0");
		filtersShown.put("sector","0");
		filtersShown.put("sceSector","0");
		filtersShown.put("inds","0");
		filtersShown.put("geos","0");
		filtersShown.put("roles","0");
		filtersShown.put("intext","0");
		filtersShown.put("advocate","0");
		filtersShown.put("wsowner","0");
		filtersShown.put("source","0");
		filtersShown.put("datefrom","0");
		filtersShown.put("sortBy","0");
		// added for Client Contact Reports
		filtersShown.put("crCompany","0");
		filtersShown.put("crFname","0");
		filtersShown.put("crLname","0");
		filtersShown.put("crEmail","0");
		filtersShown.put("crCountry","0");
		filtersShown.put("crDatasource","0");
		
	}
	public void setFiltersShown(Hashtable h){
		filtersShown = h;
	}
	public Hashtable getFiltersShown(){
		return filtersShown;
	}

	public void setColumnsToShow(Vector v){
		columnsToShow = v;
	}
	public Vector getColumnsToShow(){
		return columnsToShow;
	}



	public void setSelectedUsers(String[] s){
		selectedUsers = new Vector();
		if (s!=null)
			for (int i = 0; i<s.length;i++){
				selectedUsers.addElement(s[i]);
			}
	}
	public Vector getSelectedUsers(){
		return selectedUsers;
	}
	public String getSelectedUsersDBStr(){
		String s = new String();

		for (int i = 0; i<selectedUsers.size();i++){
			if (i == 0){
				s = "'"+(String)selectedUsers.elementAt(i)+"'";
			}
			else{
				s = s + ",'"+(String)selectedUsers.elementAt(i)+"'";
			}
		}
		return s;
	}



	public void setSelectedWorkspaces(String[] s){
		selectedWorkspaces = new Vector();
		if (s!=null)
			for (int i = 0; i<s.length;i++){
				selectedWorkspaces.addElement(s[i]);
			}
	}
	public Vector getSelectedWorkspaces(){
		return selectedWorkspaces;
	}
	public String getSelectedWorkspacesDBStr(){
		String s = new String();

		for (int i = 0; i<selectedWorkspaces.size();i++){
			if (i == 0){
				s = "'"+(String)selectedWorkspaces.elementAt(i)+"'";
			}
			else{
				s = s + ",'"+(String)selectedWorkspaces.elementAt(i)+"'";
			}
		}
		return s;
	}


	public void setSelectedComps(String[] comps){
		selectedComps = new Vector();
		if (comps!=null)
			for (int i = 0; i<comps.length;i++){
				selectedComps.addElement(comps[i]);
			}
	}
	public Vector getSelectedComps(){
		return selectedComps;
	}
	public String getSelectedCompsDBStr(){
		String s = new String();

		for (int i = 0; i<selectedComps.size();i++){
			if (i == 0){
				s = "'"+(String)selectedComps.elementAt(i)+"'";
			}
			else{
				s = s + ",'"+(String)selectedComps.elementAt(i)+"'";
			}
		}
		return s;
	}


	public void setSelectedTeams(String[] s){
		selectedTeams = new Vector();
		if (s!= null)
			for (int i = 0; i<s.length;i++){
				selectedTeams.addElement(s[i]);
			}
	}
	public Vector getSelectedTeams(){
		return selectedTeams;
	}
	public String getSelectedTeamsDBStr(){
		String s = new String();

		for (int i = 0; i<selectedTeams.size();i++){
			if (i == 0){
				s = "'"+(String)selectedTeams.elementAt(i)+"'";
			}
			else{
				s = s + ",'"+(String)selectedTeams.elementAt(i)+"'";
			}
		}
		return s;
	}


	public void setSelectedWsTypes(String[] s){
		selectedWsTypes = new Vector();
		if (s != null)
			for (int i = 0; i<s.length;i++){
				selectedWsTypes.addElement(s[i]);
			}
	}
	public Vector getSelectedWsTypes(){
		return selectedWsTypes;
	}
	public String getSelectedWsTypesDBStr(){
		String s = new String();

		for (int i = 0; i<selectedWsTypes.size();i++){
			String tmp = (String)selectedWsTypes.elementAt(i);

			if (i == 0){
				s = "'"+getWsType(tmp)+"'";
			}
			else{
				s = s + ",'"+getWsType(tmp)+"'";
			}
		}
		return s;
	}
	private String getWsType(String s){
		if (s.equals("Project")){
			s="P";
		}
		else if(s.equals("Proposal")){
			s="O";
		}
		else if(s.equals("Client voice")){
			s="C";
		}
		return s;
	}

	public void setSelectedWsAccessTypes(String[] s){
		selectedWsAccessTypes = new Vector();
		if (s != null)
			for (int i = 0; i<s.length;i++){
				selectedWsAccessTypes.addElement(s[i]);
			}
	}
	public Vector getSelectedWsAccessTypes(){
		return selectedWsAccessTypes;
	}
	public String getSelectedWsAccessTypesDBStr(){
		String s = new String();

		for (int i = 0; i<selectedWsAccessTypes.size();i++){
			String tmp = (String)selectedWsAccessTypes.elementAt(i);

			if (i == 0){
				s = "'"+getWsAccessType(tmp)+"'";
			}
			else{
				s = s + ",'"+getWsAccessType(tmp)+"'";
			}
		}
		return s;
	}
	private String getWsAccessType(String s){
		if (s.equals("Public Workspace")){
			s="A";
		}
		else if(s.equals("Restricted Workspace")){
			s="O";
		}
		else if(s.equals("Private Workspace")){
			s="C";
		}
		return s;
	}


	public void setSelectedClientDes(String[] s){
		selectedClientDes = new Vector();
		if (s!=null)
			for (int i = 0; i<s.length;i++){
				selectedClientDes.addElement(s[i]);
			}
	}
	public Vector getSelectedClientDes(){
		return selectedClientDes;
	}
	public String getSelectedClientDesDBStr(){
		String s = new String();

		for (int i = 0; i<selectedClientDes.size();i++){
			if (i == 0){
				s = "'"+(String)selectedClientDes.elementAt(i)+"'";
			}
			else{
				s = s + ",'"+(String)selectedClientDes.elementAt(i)+"'";
			}

			if (((String)selectedClientDes.elementAt(i)).equals(Defines.METRICS_CLIENTCARE))
				s = s + ",'"+Defines.METRICS_FOCUSACCT+"'";

		}
		return s;
	}


	public void setSelectedExpCats(String[] c){
		selectedExpCats = new Vector();
		if (c!=null)
			for (int i = 0; i<c.length;i++){
				selectedExpCats.addElement(c[i]);
			}
	}
	public Vector getSelectedExpCats(){
		return selectedExpCats;
	}
	public String getSelectedExpCatsDBStr(){
		String s = new String();

		for (int i = 0; i<selectedExpCats.size();i++){
			if (i == 0){
				s = "'"+(String)selectedExpCats.elementAt(i)+"'";
			}
			else{
				s = s + ",'"+(String)selectedExpCats.elementAt(i)+"'";
			}
		}
		return s;
	}


	public void setSelectedTabNames(String[] c){
		selectedTabNames = new Vector();
		if (c!=null)
			for (int i = 0; i<c.length;i++){
				selectedTabNames.addElement(c[i]);
			}
	}
	public Vector getSelectedTabNames(){
		return selectedTabNames;
	}
	public String getSelectedTabNamesDBStr(){
		String s = new String();

		for (int i = 0; i<selectedTabNames.size();i++){
			if (i == 0){
				s = "'"+(String)selectedTabNames.elementAt(i)+"'";
			}
			else{
				s = s + ",'"+(String)selectedTabNames.elementAt(i)+"'";
			}
		}
		return s;
	}



	public void setSelectedAdvocate(String[] s){
		selectedAdvocate = new Vector();
		if (s!=null)
			for (int i = 0; i<s.length;i++){
				selectedAdvocate.addElement(s[i]);
			}
	}
	public Vector getSelectedAdvocate(){
		return selectedAdvocate;
	}
	public String getSelectedAdvocateDBStr(){
		String s = new String();

		for (int i = 0; i<selectedAdvocate.size();i++){
			if (i == 0){
				s = "'"+(String)selectedAdvocate.elementAt(i)+"'";
			}
			else{
				s = s + ",'"+(String)selectedAdvocate.elementAt(i)+"'";
			}
		}
		return s;
	}

	public void setSelectedWSOwner(String[] s){
		selectedWSOwner = new Vector();
		if (s!=null)
			for (int i = 0; i<s.length;i++){
				selectedWSOwner.addElement(s[i]);
			}
	}
	public Vector getSelectedWSOwner(){
		return selectedWSOwner;
	}
	public String getSelectedWSOwnerDBStr(){
		String s = new String();

		for (int i = 0; i<selectedWSOwner.size();i++){
			if (i == 0){
				s = "'"+(String)selectedWSOwner.elementAt(i)+"'";
			}
			else{
				s = s + ",'"+(String)selectedWSOwner.elementAt(i)+"'";
			}
		}
		return s;
	}



	public void setSelectedColsToShow(Vector v){
		selectedColsToShow = v;
	}
	public Vector getSelectedColsToShow(){
		return selectedColsToShow;
	}




	public void setAllDates(String s){
		if (!s.equals("")){
			bAllDates = true;
		}
		else{
			bAllDates = false;
		}
	}
	public void setAllDates(boolean b){
		bAllDates = b;
	}
	public boolean getAllDates(){
		return bAllDates;
	}




	public void setFromMonth(String s){
		System.out.println("f month="+s);
		fromMonth = s;
	}
	public String getFromMonth(){
		return fromMonth;
	}
	public void setFromDay(String s){
		fromDay = s;
	}
	public String getFromDay(){
		return fromDay;
	}
	public void setFromYear(String s){
		fromYear = s;
	}
	public String getFromYear(){
		return fromYear;
	}




	long getFromDate(){
		return fromDate;
	}
	Timestamp getFromDateTS(){
		return new Timestamp(fromDate);
	}
	String[] getFromDateStrs(){
		String[] s = new String[3];
		s[0] = fromMonth;
		s[1] = fromDay;
		s[2] = fromYear;
		return s;
	}
	void setFromDate(){
		int month = Integer.parseInt(fromMonth.trim());
		int day = Integer.parseInt(fromDay.trim());
		int year = Integer.parseInt(fromYear.trim());

		Calendar c = Calendar.getInstance();
		c.set(year,month,day);
		Date d = c.getTime();
		this.fromDate = d.getTime();

	}
	void setFromDate(java.sql.Timestamp d){
		this.fromDate = d.getTime();
	}



	public void setToMonth(String s){
		toMonth = s;
	}
	public String getToMonth(){
		return toMonth;
	}
	public void setToDay(String s){
		toDay = s;
	}
	public String getToDay(){
		return toDay;
	}
	public void setToYear(String s){
		toYear = s;
	}
	public String getToYear(){
		return toYear;
	}

	long getToDate(){
		return toDate;
	}
	Timestamp getToDateTS(){
		return new Timestamp(toDate);
	}
	String[] getToDateStrs(){
		String[] s = new String[3];
		s[0] = toMonth;
		s[1] = toDay;
		s[2] = toYear;
		return s;
	}
	void setToDate(){
		int month = Integer.parseInt(toMonth.trim());
		int day = Integer.parseInt(toDay.trim());
		int year = Integer.parseInt(toYear.trim());
		Calendar c = Calendar.getInstance();
		c.set(year,month,day);
		Date d = c.getTime();
		this.toDate = d.getTime();

	}
	void setToDate(java.sql.Timestamp d){
		this.toDate = d.getTime();
	}



	public void setSelectedIssueStatus(String[] s){
		selectedIssueStatus = new Vector();
		if (s != null)
			for (int i = 0; i<s.length;i++){
				selectedIssueStatus.addElement(s[i]);
			}
	}
	public Vector getSelectedIssueStatus(){
		return selectedIssueStatus;
	}
	public String getSelectedIssueStatusDBStr(){
		String s = new String();

		for (int i = 0; i<selectedIssueStatus.size();i++){
			String tmp = (String)selectedIssueStatus.elementAt(i);

			if (i == 0){
				s = "'"+tmp+"'";
			}
			else{
				s = s + ",'"+tmp+"'";
			}
		}
		return s;
	}

	public void setSelectedInds(String[] c){
		selectedInds = new Vector();
		if (c!=null)
			for (int i = 0; i<c.length;i++){
				selectedInds.addElement(c[i]);
			}
	}
	public Vector getSelectedInds(){
		return selectedInds;
	}
	public String getSelectedIndsDBStr(){
		String s = new String();

		for (int i = 0; i<selectedInds.size();i++){
			if (i == 0){
				s = "'"+(String)selectedInds.elementAt(i)+"'";
			}
			else{
				s = s + ",'"+(String)selectedInds.elementAt(i)+"'";
			}
		}
		return s;
	}

	public void setSelectedGeos(String[] c){
		selectedGeos = new Vector();
		if (c!=null)
			for (int i = 0; i<c.length;i++){
				selectedGeos.addElement(c[i]);
			}
	}
	public Vector getSelectedGeos(){
		return selectedGeos;
	}
	public String getSelectedGeosDBStr(){
		String s = new String();

		for (int i = 0; i<selectedGeos.size();i++){
			if (i == 0){
				s = "'"+(String)selectedGeos.elementAt(i)+"'";
			}
			else{
				s = s + ",'"+(String)selectedGeos.elementAt(i)+"'";
			}
		}
		return s;
	}


	public void setSelectedRoles(String[] c){
		selectedRoles = new Vector();
		if (c!=null)
			for (int i = 0; i<c.length;i++){
				selectedRoles.addElement(c[i]);
			}
	}
	public Vector getSelectedRoles(){
		return selectedRoles;
	}
	public String getSelectedRolesDBStr(){
		String s = new String();

		for (int i = 0; i<selectedRoles.size();i++){
			if (i == 0){
				s = "'"+(String)selectedRoles.elementAt(i)+"'";
			}
			else{
				s = s + ",'"+(String)selectedRoles.elementAt(i)+"'";
			}
		}
		return s;
	}


	public void setSelectedIntExt(String s){
		selectedIntExt = new String();
		if (s != null)
			selectedIntExt = s;
	}
	public String getSelectedIntExt(){
		return selectedIntExt;
	}


	public void setTitle(String s){
		title = s;
	}
	public String getTitle(){
		return title;
	}


	public void setSelectedCVSource(String[] s){
		selectedCVSource = new Vector();
		if (s != null)
			for (int i = 0; i<s.length;i++){
				selectedCVSource.addElement(s[i]);
			}
	}
	public Vector getSelectedCVSource(){
		return selectedCVSource;
	}
	public String getSelectedCVSourceDBStr(){
		String s = new String();

		for (int i = 0; i<selectedCVSource.size();i++){
			String tmp = (String)selectedCVSource.elementAt(i);

			if (i == 0){
				s = "'"+getCVSourceType(tmp)+"'";
			}
			else{
				s = s + ",'"+getCVSourceType(tmp)+"'";
			}
		}
		return s;
	}
	private String getCVSourceType(String s){
		if (s.equals("Set/Met")){
			s="M";
		}
		else if(s.equals("Self Assessment")){
			s="A";
		}
		return s;
	}


	public void setErrorFlag(boolean f){
		errorFlag = f;
	}
	public boolean getErrorFlag(){
		return errorFlag;
	}

	public void setErrorMsg(String s){
		errorMsg = s;
	}
	public String getErrorMsg(){
		return errorMsg;
	}


	public void setErrorField(String s){
		errorField = s;
	}
	public String getErrorField(){
		return errorField;
	}

	public void setSelectedBrand(String[] c){
		selectedBrand = new Vector();
		if (c!=null)
			for (int i = 0; i<c.length;i++){
				selectedBrand.addElement(c[i]);
			}
	}
	public Vector getSelectedBrand(){
		return selectedBrand;
	}
	public String getSelectedBrandDBStr(){
		String s = new String();

		for (int i = 0; i<selectedBrand.size();i++){
			if (i == 0){
				s = "'"+(String)selectedBrand.elementAt(i)+"'";
			}
			else{
				s = s + ",'"+(String)selectedBrand.elementAt(i)+"'";
			}
		}
		return s;
	}


	public void setSelectedProcess(String[] c){
		selectedProcess = new Vector();
		if (c!=null)
			for (int i = 0; i<c.length;i++){
				selectedProcess.addElement(c[i]);
			}
	}
	public Vector getSelectedProcess(){
		return selectedProcess;
	}
	public String getSelectedProcessDBStr(){
		String s = new String();

		for (int i = 0; i<selectedProcess.size();i++){
			if (i == 0){
				s = "'"+(String)selectedProcess.elementAt(i)+"'";
			}
			else{
				s = s + ",'"+(String)selectedProcess.elementAt(i)+"'";
			}
		}
		return s;
	}

	public void setSelectedSector(String[] c){
		selectedSector = new Vector();
		if (c!=null)
			for (int i = 0; i<c.length;i++){
				selectedSector.addElement(c[i]);
			}
	}
	public Vector getSelectedSector(){
		return selectedSector;
	}
	public String getSelectedSectorDBStr(){
		String s = new String();

		for (int i = 0; i<selectedSector.size();i++){
			if (i == 0){
				s = "'"+(String)selectedSector.elementAt(i)+"'";
			}
			else{
				s = s + ",'"+(String)selectedSector.elementAt(i)+"'";
			}
		}
		return s;
	}

	public void setSelectedSceSector(String[] c){
		selectedSceSector = new Vector();
		if (c!=null)
			for (int i = 0; i<c.length;i++){
				selectedSceSector.addElement(c[i]);
			}
	}
	public Vector getSelectedSceSector(){
		return selectedSceSector;
	}
	public String getSelectedSceSectorDBStr(){
		String s = new String();

		for (int i = 0; i<selectedSceSector.size();i++){
			if (i == 0){
				s = "'"+(String)selectedSceSector.elementAt(i)+"'";
			}
			else{
				s = s + ",'"+(String)selectedSceSector.elementAt(i)+"'";
			}
		}
		return s;
	}

	// for sorting of data in Reports
	public void setSelectedSortOrder(String[] c){
		selectedSortOrder = new Vector();
		if (c!=null)
			for (int i = 0; i<c.length;i++){
				selectedSortOrder.addElement(c[i]);
			}
	}
	public Vector getSelectedSortOrder(){
		return selectedSortOrder;
	}
	public String getSelectedSortOrderDBStr(){
		String s = new String();

		for (int i = 0; i<selectedSortOrder.size();i++){
			if (i == 0){
				s = "'"+(String)selectedSortOrder.elementAt(i)+"'";
			}
			else{
				s = s + ",'"+(String)selectedSortOrder.elementAt(i)+"'";
			}
		}
		return s;
	}


	// Added for Client Contact Reports
	public void setSelectedCRcompany(String[] c){
		selectedCRcompany = new Vector();
		if (c!=null)
			for (int i = 0; i<c.length;i++){
				selectedCRcompany.addElement(c[i]);
			}
	}
	public Vector getSelectedCRcompany(){
		return selectedCRcompany;
	}
	public String getSelectedCRcompanyDBStr(){
		String s = new String();

		for (int i = 0; i<selectedCRcompany.size();i++){
			if (i == 0){
				s = "'"+(String)selectedCRcompany.elementAt(i)+"'";
			}
			else{
				s = s + ",'"+(String)selectedCRcompany.elementAt(i)+"'";
			}
		}
		return s;
	}

	public void setSelectedCRcountry(String[] c){
		selectedCRcountry = new Vector();
		if (c!=null)
			for (int i = 0; i<c.length;i++){
				selectedCRcountry.addElement(c[i]);
			}
	}
	public Vector getSelectedCRcountry(){
		return selectedCRcountry;
	}
	public String getSelectedCRcountryDBStr(){
		String s = new String();

		for (int i = 0; i<selectedCRcountry.size();i++){
			if (i == 0){
				s = "'"+(String)selectedCRcountry.elementAt(i)+"'";
			}
			else{
				s = s + ",'"+(String)selectedCRcountry.elementAt(i)+"'";
			}
		}
		return s;
	}

	public void setSelectedCRdatasource(String[] c){
		selectedCRdatasource = new Vector();
		if (c!=null)
			for (int i = 0; i<c.length;i++){
				selectedCRdatasource.addElement(c[i]);
			}
	}
	public Vector getSelectedCRdatasource(){
		return selectedCRdatasource;
	}
	public String getSelectedCRdatasourceDBStr(){
		String s = new String();

		for (int i = 0; i<selectedCRdatasource.size();i++){
			if (i == 0){
				s = "'"+(String)selectedCRdatasource.elementAt(i)+"'";
			}
			else{
				s = s + ",'"+(String)selectedCRdatasource.elementAt(i)+"'";
			}
		}
		return s;
	}
	
	public void setSelectedCRFname(String c){
		selectedCRFname = new String();
		if (c!=null)
			selectedCRFname = c;
			
	}
	public String getSelectedCRFname(){
		return selectedCRFname;
	}
	public String getSelectedCRFnameDBStr(){
		String s = new String();

		if (selectedCRFname.trim().length() > 0){
			s = selectedCRFname.trim();
		}
		return s;
	}	

	public void setSelectedCRLname(String c){
		selectedCRLname = new String();
		if (c!=null)
			selectedCRLname = c;
			
	}
	public String getSelectedCRLname(){
		return selectedCRLname;
	}
	public String getSelectedCRLnameDBStr(){
		String s = new String();

		if (selectedCRLname.trim().length() > 0){
			s = selectedCRLname.trim();
		}
		return s;
	}
	
	public void setSelectedCREmail(String c){
		selectedCREmail = new String();
		if (c!=null)
			selectedCREmail = c;
			
	}
	public String getSelectedCREmail(){
		return selectedCREmail;
	}
	public String getSelectedCREmailDBStr(){
		String s = new String();

		if (selectedCREmail.trim().length() > 0){
			s = selectedCREmail.trim();
		}
		return s;
	}	
	/**
	 * @return Returns the salesreportslist.
	 */
	public AICMetricsListValue[] getSalesreportslist() {
		return salesreportslist;
	}//added for workflow
	/**
	 * @param salesreportslist The salesreportslist to set.
	 */
	public void setSalesreportslist(
			AICMetricsListValue[] salesreportslist) {
		AICMetricsResultObj.salesreportslist = salesreportslist;
	}//added for workflow
}


