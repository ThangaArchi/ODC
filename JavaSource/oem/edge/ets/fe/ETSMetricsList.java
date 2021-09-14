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
package oem.edge.ets.fe;
public class ETSMetricsList {
	static private final ETSMetricsListValue[] prlist =
		{
			new ETSMetricsListValue("Average client satisfaction data", "Average by quarter of all closed set/met, self assessments and survey data for all clients or by geography, industry, advocate, or delivery team", "ETSMetricsServlet.wss?option=report&reportid=PR0001", "PR0001", 1),
			new ETSMetricsListValue("Category codes rating frequency distribution", "Lists each closed set/met category with the distribution of ratings", "ETSMetricsServlet.wss?option=report&reportid=PR0002", "PR0002", 1),
			new ETSMetricsListValue("Category code and average ratings", "Average rating for each closed set/met category by quarter for all clients or by geography, industry, advocate or delivery team", "ETSMetricsServlet.wss?option=report&reportid=PR0003", "PR0003", 1),		//new ETSMetricsListValue("Clients with no satisfaction input", "Lists clients with no data in the specified date range","ETSMetricsServlet.wss?option=report&reportid=PR0004","PR0004",1),
	};
	static private final ETSMetricsListValue[] cclist =
		{
			new ETSMetricsListValue("Average client satisfaction data by client", "Average closed set/met ratings by quarter by selected clients", "ETSMetricsServlet.wss?option=report&reportid=CV0001", "CV0001", 0),
			new ETSMetricsListValue("Current set/met assessment ratings", "List of clients and their current set/met ratings", "ETSMetricsServlet.wss?option=report&reportid=CV0002", "CV0002", 0),
			new ETSMetricsListValue("Overall satisfaction rating - history", "All set/met, self assessment and survey data by client (open and closed)", "ETSMetricsServlet.wss?option=report&reportid=CV0003", "CV0003", 0),
			new ETSMetricsListValue("Expectation rating by client", "Includes expectation category and rating by client for set/met and self assessments", "ETSMetricsServlet.wss?option=report&reportid=CV0004", "CV0004", 0),
			};
	/*
	static private final ETSMetricsListValue[] cclist = {
		new ETSMetricsListValue("Overall satisfaction rating - history", "All set/met rating by client (open and closed)","ETSMetricsServlet.wss?option=report&reportid=CV0001","CV0001",false),
		new ETSMetricsListValue("Expectation rating by client", "Includes expectation category and rating by client","ETSMetricsServlet.wss?option=report&reportid=CV0002","CV0002",false),
		//moved to pr new ETSMetricsListValue("Expectation rating frequency distribution", "Each expectation category and distribution rating","ETSMetricsServlet.wss?option=report&reportid=CV0003","CV0003",false),
		//new ETSMetricsListValue("Self Assessment sat rating - history", "All self assessment rating by client (open and closed)","ETSMetricsServlet.wss?option=report&reportid=CV0004","CV0004",false),
		//new ETSMetricsListValue("Self Assessment Exp rating by client", "Includes expectation category and rating by client","ETSMetricsServlet.wss?option=report&reportid=CV0005","CV0005",false)
	};*/
	static private final ETSMetricsListValue[] palist =
		{
			new ETSMetricsListValue("Activity overview", "Membership, documents, issues & hits by workspace", "ETSMetricsServlet.wss?option=report&reportid=WS0001", "WS0001", 0),
			new ETSMetricsListValue("Document activity", "Posting totals and size including internal/external breakout", "ETSMetricsServlet.wss?option=report&reportid=WS0002", "WS0002", 0),
			new ETSMetricsListValue("Issue activity summary", "Totals by Serverity with internal/external breakout", "ETSMetricsServlet.wss?option=report&reportid=WS0003", "WS0003", 0),
			new ETSMetricsListValue("Issue activity details", "Details by issue including date opened, date of last activity", "ETSMetricsServlet.wss?option=report&reportid=WS0004", "WS0004", 0),
			new ETSMetricsListValue("Usage summary (in hits)", "Total hits by workspace with internal/external breakout", "ETSMetricsServlet.wss?option=report&reportid=WS0005", "WS0005", 0),
			new ETSMetricsListValue("Usage details (in hits)", "Hits by tab for each workspace with internal/external breakout", "ETSMetricsServlet.wss?option=report&reportid=WS0006", "WS0006", 0),
			new ETSMetricsListValue("Membership summary", "Totals by workspace including internal/external breakout", "ETSMetricsServlet.wss?option=report&reportid=WS0007", "WS0007", 0),
			};
	static private final ETSMetricsListValue[] superadminlist =
		{
			new ETSMetricsListValue("Issue tab metrics", "Total number of issues, changes and feedbacks by workspace", "ETSMetricsServlet.wss?option=report&reportid=SA0001", "SA0001", 2),
			new ETSMetricsListValue("Team listing", "List of users by workspace which includes logins and roles", "ETSMetricsServlet.wss?option=report&reportid=SA0002", "SA0002", 2),
			new ETSMetricsListValue("Team membership", "Membership status by user name", "ETSMetricsServlet.wss?option=report&reportid=SA0003", "SA0003", 2)};
	static private final ETSMetricsListValue[] bladelist = { new ETSMetricsListValue("Blade document activity", "Document activity", "ETSMetricsServlet.wss?option=report&reportid=BC0001", "BC0001", 2, true), new ETSMetricsListValue("Blade user activity", "User License activity", "ETSMetricsServlet.wss?option=report&reportid=BC0002", "BC0002", 2, true)};
	static public String getDescription(int index, ETSMetricsListValue[] list) {
		return list[index].getDescription();
	}
	static public String getTitle(int index, ETSMetricsListValue[] list) {
		return list[index].getTitle();
	}
	static public String getLink(int index, ETSMetricsListValue[] list) {
		return list[index].getLink();
	}
	static public String getReportId(int index, ETSMetricsListValue[] list) {
		return list[index].getReportId();
	}
	/*static public boolean isAdminRestricted(int index,ETSMetricsListValue[] list){
		return list[index].isAdminRestricted();
	}*/
	static public int getRestricted(int index, ETSMetricsListValue[] list) {
		return list[index].getRestricted();
	}
	static public boolean isOwnerOnly(int index, ETSMetricsListValue[] list) {
		return list[index].isOwnerOnly();
	}
	static public int getCount(ETSMetricsListValue[] list) {
		return list.length;
	}
	static public ETSMetricsListValue[] getPRMetricsList() {
		return prlist;
	}
	static public ETSMetricsListValue[] getCCMetricsList() {
		return cclist;
	}
	static public ETSMetricsListValue[] getPAMetricsList() {
		return palist;
	}
	static public ETSMetricsListValue[] getSAMetricsList() {
		return superadminlist;
	}
	static public ETSMetricsListValue[] getBladeMetricsList() {
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
		for (int i = 0; i < bladelist.length; i++) {
			if ((bladelist[i].getReportId()).equals(id)) {
				return bladelist[i].getTitle();
			}
		}
		return "Metrics";
	}
}
