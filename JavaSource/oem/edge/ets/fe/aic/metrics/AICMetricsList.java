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

public class AICMetricsList {

	static private final AICMetricsListValue[] prlist =
		{
			//new AICMetricsListValue("Average client satisfaction data", "Average closed set/met and self assessments by quarter for all clients or by geography, industry, advocate, or delivery team", "AICMetricsServlet.wss?option=report&reportid=PR0001", "PR0001", 1),
			//new AICMetricsListValue("Category codes rating frequency distribution", "Lists each closed set/met category with the distribution of ratings", "AICMetricsServlet.wss?option=report&reportid=PR0002", "PR0002", 1),
			//new AICMetricsListValue("Category code and average ratings", "Average rating for each closed set/met category by quarter for all clients or by geography, industry, advocate or delivery team", "AICMetricsServlet.wss?option=report&reportid=PR0003", "PR0003", 1),
		//new AICMetricsListValue("Clients with no satisfaction input", "Lists clients with no data in the specified date range","AICMetricsServlet.wss?option=report&reportid=PR0004","PR0004",1),
	};

	static private final AICMetricsListValue[] cclist =
		{
			//new AICMetricsListValue("Average client satisfaction data by client", "Average closed set/met ratings by quarter by selected clients", "AICMetricsServlet.wss?option=report&reportid=CV0001", "CV0001", 0),
			//new AICMetricsListValue("Current set/met assessment ratings", "List of clients and their current set/met ratings", "AICMetricsServlet.wss?option=report&reportid=CV0002", "CV0002", 0),
			//new AICMetricsListValue("Overall satisfaction rating - history", "All set/met and self assessment ratings by client (open and closed)", "AICMetricsServlet.wss?option=report&reportid=CV0003", "CV0003", 0),
			//new AICMetricsListValue("Expectation rating by client", "Includes expectation category and rating by client for set/met and self assessments", "AICMetricsServlet.wss?option=report&reportid=CV0004", "CV0004", 0),
			};

	/*
	static private final AICMetricsListValue[] cclist = {
		new AICMetricsListValue("Overall satisfaction rating - history", "All set/met rating by client (open and closed)","AICMetricsServlet.wss?option=report&reportid=CV0001","CV0001",false),
		new AICMetricsListValue("Expectation rating by client", "Includes expectation category and rating by client","AICMetricsServlet.wss?option=report&reportid=CV0002","CV0002",false),
		//moved to pr new AICMetricsListValue("Expectation rating frequency distribution", "Each expectation category and distribution rating","AICMetricsServlet.wss?option=report&reportid=CV0003","CV0003",false),
		//new AICMetricsListValue("Self Assessment sat rating - history", "All self assessment rating by client (open and closed)","AICMetricsServlet.wss?option=report&reportid=CV0004","CV0004",false),
		//new AICMetricsListValue("Self Assessment Exp rating by client", "Includes expectation category and rating by client","AICMetricsServlet.wss?option=report&reportid=CV0005","CV0005",false)
	};*/

	static private final AICMetricsListValue[] palist =
		{
			new AICMetricsListValue("Activity overview", "Membership, documents, issues & hits by workspace", "AICMetricsServlet.wss?option=report&reportid=WS0001", "WS0001", 0),
			new AICMetricsListValue("Document activity", "Posting totals and size ", "AICMetricsServlet.wss?option=report&reportid=WS0002", "WS0002", 0),
			//new AICMetricsListValue("Issue activity summary", "Totals by Serverity with internal/external breakout", "AICMetricsServlet.wss?option=report&reportid=WS0003", "WS0003", 0),
			//new AICMetricsListValue("Issue activity details", "Details by issue including date opened, date of last activity", "AICMetricsServlet.wss?option=report&reportid=WS0004", "WS0004", 0),
			new AICMetricsListValue("Usage summary (in hits)", "Total hits by workspace ", "AICMetricsServlet.wss?option=report&reportid=WS0005", "WS0005", 0),
			new AICMetricsListValue("Usage details (in hits)", "Hits by tab for each workspace ", "AICMetricsServlet.wss?option=report&reportid=WS0006", "WS0006", 0),
			new AICMetricsListValue("Membership summary", "Totals by workspace ", "AICMetricsServlet.wss?option=report&reportid=WS0007", "WS0007", 0),
			};

	static private final AICMetricsListValue[] superadminlist =
		{
			new AICMetricsListValue("Team listing", "List of users by workspace which includes logins and roles", "AICMetricsServlet.wss?option=report&reportid=SA0002", "SA0002", 2),
			new AICMetricsListValue("Team membership", "Membership status by user name", "AICMetricsServlet.wss?option=report&reportid=SA0003", "SA0003", 2)};

	static private final AICMetricsListValue[] clientReportsList =
		{
			new AICMetricsListValue("Client Report", "List of clients with their contact information", "AICMetricsServlet.wss?option=report&reportid=CR0001", "CR0001", 2),
		};

	static private final AICMetricsListValue[] salesReportsList =
	{
		new AICMetricsListValue("Preliminary Prep View Report", "Provides overview of Set/Mets, QBRs and Self Assessments by preparation state", "SalesReport.wss?action=report&proj=metrics&tc=1691&reportid=WF001", "WF001", 2),
		new AICMetricsListValue("Preliminary Issue View Report", "Provides details associated with all Set/Met, QBR and Self Assessment issues", "SalesReport.wss?action=report&proj=metrics&tc=1691&reportid=WF002", "WF002", 2),
		new AICMetricsListValue("Preliminary Exec View Report", "Provides high-level status of all Set/Mets, QBRs and Self Assessments", "SalesReport.wss?action=report&proj=metrics&tc=1691&reportid=WF003", "WF003", 2),
	};
	static private final AICMetricsListValue[] bladelist = 
		{ 
			//new AICMetricsListValue("Blade document activity", "Document activity", "AICMetricsServlet.wss?option=report&reportid=BC0001", "BC0001", 2, true),
		 	//new AICMetricsListValue("Blade user activity", "User License activity", "AICMetricsServlet.wss?option=report&reportid=BC0002", "BC0002", 2, true)
		};

	static public String getDescription(int index, AICMetricsListValue[] list) {
		return list[index].getDescription();
	}

	static public String getTitle(int index, AICMetricsListValue[] list) {
		return list[index].getTitle();
	}

	static public String getLink(int index, AICMetricsListValue[] list) {
		return list[index].getLink();
	}

	static public String getReportId(int index, AICMetricsListValue[] list) {
		return list[index].getReportId();
	}

	/*static public boolean isAdminRestricted(int index,AICMetricsListValue[] list){
		return list[index].isAdminRestricted();
	}*/

	static public int getRestricted(int index, AICMetricsListValue[] list) {
		return list[index].getRestricted();
	}

	static public boolean isOwnerOnly(int index, AICMetricsListValue[] list) {
		return list[index].isOwnerOnly();
	}

	static public int getCount(AICMetricsListValue[] list) {
		return list.length;
	}

	static public AICMetricsListValue[] getPRMetricsList() {
		return prlist;
	}

	static public AICMetricsListValue[] getCCMetricsList() {
		return cclist;
	}

	static public AICMetricsListValue[] getPAMetricsList() {
		return palist;
	}

	static public AICMetricsListValue[] getSAMetricsList() {
		return superadminlist;
	}

	static public AICMetricsListValue[] getCRMetricsList() {
		return clientReportsList;
	}

	static public AICMetricsListValue[] getSMRMetricsList() {
		return salesReportsList;
	}//addded for workflow
	
	static public AICMetricsListValue[] getBladeMetricsList() {
		return bladelist;
	}

	static public String getTitleByReportId(String id) {
		for (int i = 0; i < prlist.length; i++) {
			if ((prlist[i].getReportId()).equals(id)) { 
				return prlist[i].getTitle();
			}
		}
		for (int i = 0; i < palist.length; i++) {
			if ((palist[i].getReportId()).equals(id)) {
				return palist[i].getTitle();
			}
		}
		for (int i = 0; i < cclist.length; i++) {
			if ((cclist[i].getReportId()).equals(id)) {
				return cclist[i].getTitle();
			}
		}
		for (int i = 0; i < superadminlist.length; i++) {
			if ((superadminlist[i].getReportId()).equals(id)) {
				return superadminlist[i].getTitle();
			}
		}
		for (int i = 0; i < clientReportsList.length; i++) {
			if ((clientReportsList[i].getReportId()).equals(id)) {
				return clientReportsList[i].getTitle();
			}
		}
		for (int i = 0; i < salesReportsList.length; i++) {
			if ((salesReportsList[i].getReportId()).equals(id)) {
				return salesReportsList[i].getTitle();
			}
		}//added for workflow
		
		for (int i = 0; i < bladelist.length; i++) {
			if ((bladelist[i].getReportId()).equals(id)) {
				return bladelist[i].getTitle();
			}
		}
		return "Metrics";
	}

}
