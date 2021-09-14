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


package oem.edge.ets.fe;

import java.util.Hashtable;
import java.util.Vector;

import oem.edge.ets.fe.common.EtsLogger;

import org.apache.commons.logging.Log;

public class ETSMetricsReports {

	public final static String Copyright = "(C)Copyright IBM Corp.  2001 - 2004";
	private static final String CLASS_VERSION = "1.3";
	
	private static Log logger = EtsLogger.getLogger(ETSMetricsReports.class);


	static public Hashtable getFilterSetting(ETSMetricsResultObj o, String s){
		Hashtable h = o.getFiltersShown();
		
		if (s.equals("PR0001")){
			h.put("inds","1");
			h.put("geos","1");
			h.put("deliveryTeams","1");
			h.put("advocate","1");
			h.put("wsowner","1");
		}
		else if (s.equals("PR0002")){
			h.put("companyList","1");
			h.put("clientDesList","1");
			h.put("exp_cat","1");
			h.put("date","1");
			h.put("inds","1");
			h.put("geos","1");
		}
		else if (s.equals("PR0003")){
			h.put("inds","1");
			h.put("geos","1");
			h.put("deliveryTeams","1");
			h.put("advocate","1");
			h.put("wsowner","1");
		}
		else if (s.equals("PR0004")){
			h.put("datefrom","1");
			h.put("source","1");
		}
		else if (s.equals("CV0001")){
			h.put("companyList","1");
			h.put("inds","1");
			h.put("geos","1");
			h.put("deliveryTeams","1");
			h.put("advocate","1");
			h.put("wsowner","1");
		}
		else if (s.equals("CV0002")){
		
		}
		else if (s.equals("CV0003")){
			h.put("companyList","1");
			h.put("clientDesList","1");
			h.put("date","1");
			h.put("inds","1");
			h.put("geos","1");
		}
		else if (s.equals("CV0004")){
			h.put("companyList","1");
			h.put("clientDesList","1");
			h.put("exp_cat","1");
			h.put("date","1");
			h.put("inds","1");
			h.put("geos","1");
		}
		else if (s.equals("WS0001")){
			h.put("companyList","1");
			h.put("deliveryTeams","1");
			h.put("wsTypes","1");
			h.put("date","1");
			h.put("inds","1");
			h.put("geos","1");
		}
		else if (s.equals("WS0002")){
			h.put("companyList","1");
			h.put("deliveryTeams","1");
			h.put("wsTypes","1");
			h.put("date","1");
			h.put("inds","1");
			h.put("geos","1");
		}
		else if (s.equals("WS0003")){
			h.put("companyList","1");
			h.put("clientDesList","1");
			h.put("inds","1");
			h.put("geos","1");
		}
		else if (s.equals("WS0004")){
			h.put("companyList","1");
			h.put("clientDesList","1");
			h.put("issueStatus","1");
			h.put("date","1");
			h.put("inds","1");
			h.put("geos","1");
			h.put("intext","1");
		}
		else if (s.equals("WS0005")){
			h.put("companyList","1");
			h.put("deliveryTeams","1");
			h.put("wsTypes","1");
			h.put("date","1");
			h.put("inds","1");
			h.put("geos","1");
		}
		else if (s.equals("WS0006")){
			h.put("companyList","1");
			h.put("deliveryTeams","1");
			h.put("wsTypes","1");
			h.put("tabNames","1");
			h.put("date","1");
			h.put("inds","1");
			h.put("geos","1");
		}
		else if (s.equals("WS0007")){
			h.put("companyList","1");
			h.put("deliveryTeams","1");
			h.put("wsTypes","1");
			h.put("inds","1");
			h.put("geos","1");
		}
		else if (s.equals("SA0001")){
			h.put("wkspaces","1");
			h.put("date","1");
		}
		else if (s.equals("SA0002")){
			h.put("wkspaces","1");
			//h.put("date","1");
		}
		else if (s.equals("SA0003")){
			h.put("users","1");
			h.put("roles","1");
			//h.put("date","1");
		}
		else if (s.equals("BC0001")){
			h.put("date","1");
		}
		else if (s.equals("BC0002")){
			h.put("date","1");
		}
		
		return h;	
	}
	
	
	
	static public Vector getColumnsToShowSetting(ETSMetricsResultObj o, String s){
		//variable, select name (checkbox),result header,method name, width, name field
				
		Vector v = new Vector();
		if (s.equals("PR0001")){
			String q[] = ETSMetricsDAO.getQuarterAbrString(9);
			v.addElement(new String[]{"desc","Description","Description","RowName","",""});
			v.addElement(new String[]{"currQtr",q[0],q[0],"CurrRatStr","",""});
			v.addElement(new String[]{"prevQtr",q[1],q[1],"PrevRatStr","",""});
			v.addElement(new String[]{"2QtrsAgo",q[2],q[2],"2RatStr","",""});
			v.addElement(new String[]{"3QtrsAgo",q[3],q[3],"3RatStr","",""});
			v.addElement(new String[]{"4QtrsAgo",q[4],q[4],"4RatStr","",""});
			v.addElement(new String[]{"5QtrsAgo",q[5],q[5],"5RatStr","",""});
			v.addElement(new String[]{"6QtrsAgo",q[6],q[6],"6RatStr","",""});
			v.addElement(new String[]{"7QtrsAgo",q[7],q[7],"7RatStr","",""});
			v.addElement(new String[]{"8QtrsAgo",q[8],q[8],"8RatStr","",""});
		}
		else if (s.equals("PR0002")){
			v.addElement(new String[]{"expCat","Expectation category","Expectation category","ExpCat","200",""});
			v.addElement(new String[]{"exceedRating","Exceed ratings","# of \"exceed\" ratings","ExceedRating","150",""});
			v.addElement(new String[]{"metRating","Met ratings","# of \"met\" ratings","MetRating","150",""});
			v.addElement(new String[]{"metSomeRating","Met some ratings","# of \"met some\" ratings","SomeRating","150",""});
			v.addElement(new String[]{"shortRating","Fallen short rating","# of \"fallen short\" ratings","LowRating","150",""});
		}
		else if (s.equals("PR0003")){
			String q[] = ETSMetricsDAO.getQuarterAbrString(2);
			v.addElement(new String[]{"desc","Expectation codes","Expectation codes","RowName","",""});
			v.addElement(new String[]{"currQtr",q[0],q[0],"CurrRatStr","",""});
			v.addElement(new String[]{"prevQtr",q[1],q[1],"PrevRatStr","",""});
			
		}
		else if (s.equals("PR0004")){  //not done yet
			v.addElement(new String[]{"company","Client company","Company","ProjectCompany","",""});
			v.addElement(new String[]{"advocate","Client advocate","Advocate","Advocate","","1"});
			v.addElement(new String[]{"lastRatingDate","Date of rating","Date of rating","RatingDateStr","150",""});
			v.addElement(new String[]{"source","Source","Source","Source","",""});
			v.addElement(new String[]{"status","Status","Status","Status","",""});
		}
		else if (s.equals("CV0001")){
			String q[] = ETSMetricsDAO.getQuarterAbrString(9);
			v.addElement(new String[]{"company","Client company","Company","ProjectCompany","",""});
			v.addElement(new String[]{"currQtr",q[0],q[0],"CurrRatStr","",""});
			v.addElement(new String[]{"prevQtr",q[1],q[1],"PrevRatStr","",""});
			v.addElement(new String[]{"2QtrsAgo",q[2],q[2],"2RatStr","",""});
			v.addElement(new String[]{"3QtrsAgo",q[3],q[3],"3RatStr","",""});
			v.addElement(new String[]{"4QtrsAgo",q[4],q[4],"4RatStr","",""});
			v.addElement(new String[]{"5QtrsAgo",q[5],q[5],"5RatStr","",""});
			v.addElement(new String[]{"6QtrsAgo",q[6],q[6],"6RatStr","",""});
			v.addElement(new String[]{"7QtrsAgo",q[7],q[7],"7RatStr","",""});
			v.addElement(new String[]{"8QtrsAgo",q[8],q[8],"8RatStr","",""});
		}
		else if (s.equals("CV0002")){
			v.addElement(new String[]{"company","Client company","Company","ProjectCompany","",""});
			v.addElement(new String[]{"rating","Final overall rating","Final overall sat rating","CurrRatStr","",""});
			v.addElement(new String[]{"lastRatingDate","Date of rating","Date of rating","RatingDateStr","150",""});
		}
		else if (s.equals("CV0003")){
			v.addElement(new String[]{"clientDes","Client Designation","Client designation","ClientDesignation","200",""});
			v.addElement(new String[]{"company","Client company","Company","ProjectCompany","250",""});
			v.addElement(new String[]{"wstype","Source","Source","WorkspaceType","250",""});
			v.addElement(new String[]{"inds","Industry","Industry","Inds","175",""});
			v.addElement(new String[]{"geos","Geography","Geography","Geos","175",""});
			v.addElement(new String[]{"ratingDate","Date","Date","RatingDateStr","175",""});
			v.addElement(new String[]{"rating","Rating","Satisfaction rating","RatingStr","175",""});
			
		}
		else if (s.equals("CV0004")){
			v.addElement(new String[]{"clientDes","Client designation","Client designation","ClientDesignation","150",""});
			v.addElement(new String[]{"company","Client company","Company","ProjectCompany","150",""});
			v.addElement(new String[]{"wstype","Source","Source","WorkspaceType","250",""});
			v.addElement(new String[]{"inds","Industry","Industry","Inds","175",""});
			v.addElement(new String[]{"geos","Geography","Geography","Geos","175",""});
			v.addElement(new String[]{"ratingDate","Date","Date","RatingDateStr","150",""});
			v.addElement(new String[]{"rating","Rating","Rating","RatingStr","50",""});
			v.addElement(new String[]{"expCat","Expectation category","Expectation category","ExpCat","250",""});
			
		}
		else if (s.equals("WS0001")){
			v.addElement(new String[]{"wstype","Workspace type","Workspace type","WorkspaceType","50",""});
			v.addElement(new String[]{"delteam","Delivery team","Delivery team","DeliveryTeam","50",""});
			v.addElement(new String[]{"company","Company","Company","ProjectCompany","50",""});
			v.addElement(new String[]{"inds","Industry","Industry","Inds","175",""});
			v.addElement(new String[]{"geos","Geography","Geography","Geos","175",""});
			v.addElement(new String[]{"wsname","Workspace name","Workspace name","ProjectName","150",""});
			v.addElement(new String[]{"wsowner","Workspace owner","Workspace owner","ProjectOwnerName","150","1"});
			v.addElement(new String[]{"createdate","WS creation date","WS creation date","CreateDateStr","150",""});
			v.addElement(new String[]{"members","Membership","Membership","MemberCount","50",""});
			v.addElement(new String[]{"docup","Docs updated","Doc updated","NewDocCount","30",""});
			v.addElement(new String[]{"opiss","Open issues","Open issues","OpenIssuesCount","30",""});
			v.addElement(new String[]{"usage","Usage (hits)","Usage (hits)","HitCount","30",""});
		}
		else if (s.equals("WS0002")){
			v.addElement(new String[]{"wstype","Workspace type","Workspace type","WorkspaceType","50",""});
			v.addElement(new String[]{"delteam","Delivery team","Delivery team","DeliveryTeam","50",""});
			v.addElement(new String[]{"company","Company","Company","ProjectCompany","50",""});
			v.addElement(new String[]{"inds","Industry","Industry","Inds","175",""});
			v.addElement(new String[]{"geos","Geography","Geography","Geos","175",""});
			v.addElement(new String[]{"wsname","Workspace name","Workspace name","ProjectName","150",""});
			v.addElement(new String[]{"wsowner","Workspace owner","Workspace owner","ProjectOwnerName","150","1"});
			v.addElement(new String[]{"docup","Docs updated","Docs updated","NewDocCount","30",""});
			v.addElement(new String[]{"size","Size","Size(KB)","DocSizeSum","30",""});
			v.addElement(new String[]{"intpost","Posted by IBM","Posted by IBM","IntPostCount","30",""});
			v.addElement(new String[]{"extpost","Posted by external","Posted by external","ExtPostCount","30",""});
			v.addElement(new String[]{"ibmpost","IBM only docs","IBM only docs","IBMOnlyPostCount","30",""});
		}
		else if (s.equals("WS0003")){
			v.addElement(new String[]{"delteam","Delivery team","Delivery team","DeliveryTeam","50",""});
			v.addElement(new String[]{"company","Company","Company","ProjectCompany","50",""});
			v.addElement(new String[]{"inds","Industry","Industry","Inds","175",""});
			v.addElement(new String[]{"geos","Geography","Geography","Geos","175",""});
			v.addElement(new String[]{"wsname","Workspace name","Workspace name","ProjectName","50",""});
			v.addElement(new String[]{"wsowner","Workspace owner","Workspace owner","ProjectOwnerName","50","1"});
			v.addElement(new String[]{"openiss","Open issues","Open issues","OpenIssuesCount","50",""});
			v.addElement(new String[]{"totiss","Total issues","Total issues","IssuesCount","50",""});
			v.addElement(new String[]{"issbyint","Issues opened by IBM","Total opened by IBM","IntPostCount","50",""});
			v.addElement(new String[]{"issbyext","Issues opened by external","Total opened by external","ExtPostCount","50",""});
			v.addElement(new String[]{"opensev1","Open sev1","Open sev1","OpenSev1IssuesCount","50",""});
			v.addElement(new String[]{"totsev1","Total sev1","Total sev1","Sev1IssuesCount","50",""});
			v.addElement(new String[]{"opensev2","Open sev2","Open sev2","OpenSev2IssuesCount","50",""});
			v.addElement(new String[]{"totsev2","Total sev2","Total sev2","Sev2IssuesCount","50",""});
			v.addElement(new String[]{"opensev35","Open sev3-5","Open sev3-5","OpenSev35IssuesCount","50",""});
			v.addElement(new String[]{"totsev35","Total sev3-5","Total sev3-5","Sev35IssuesCount","50",""});
		}
		else if (s.equals("WS0004")){
			//variable, select name (checkbox),result header,method name, width, namecomma
			v.addElement(new String[]{"company","Company","Company","ProjectCompany","50",""});
			v.addElement(new String[]{"inds","Industry","Industry","Inds","175",""});
			v.addElement(new String[]{"geos","Geography","Geography","Geos","175",""});
			v.addElement(new String[]{"wsname","Workspace name","Workspace name","ProjectName","50",""});
			v.addElement(new String[]{"wsowner","Workspace owner","Workspace owner","ProjectOwnerName","50","1"});
			v.addElement(new String[]{"issueno","Issue Number","Issue Number","IssueNumber","50",""});
			v.addElement(new String[]{"status","Status","Status","IssueStatus","100",""});
			v.addElement(new String[]{"isssev","Severity","Severity","IssueSeverity","50",""});
			v.addElement(new String[]{"usertype","Submitted by","Submitted by (int/ext)","UserType","50",""});
			v.addElement(new String[]{"age","Age","Age","IssueAge","50",""});
			v.addElement(new String[]{"createdate","Date opened","Date opened","IssueCreateDateStr","150",""});
			v.addElement(new String[]{"lastact","Date of last activity","Date of last activity","LastActivityStr","150",""});
			v.addElement(new String[]{"issuetype","Issue type","Issue type","IssueProblemType","150",""});
			v.addElement(new String[]{"issueowner","Issue owner","Issue owner","OwnerName","150","1"});
			v.addElement(new String[]{"issuetitle","Issue title","Issue title","IssueTitle","150",""});
		}
		else if (s.equals("WS0005")){
			v.addElement(new String[]{"wstype","Workspace type","Workspace type","WorkspaceType","50",""});
			v.addElement(new String[]{"delteam","Delivery team","Delivery team","DeliveryTeam","50",""});
			v.addElement(new String[]{"company","Company","Company","ProjectCompany","50",""});
			v.addElement(new String[]{"inds","Industry","Industry","Inds","175",""});
			v.addElement(new String[]{"geos","Geography","Geography","Geos","175",""});
			v.addElement(new String[]{"wsname","Workspace name","Workspace name","ProjectName","150",""});
			v.addElement(new String[]{"wsowner","Workspace owner","Workspace owner","ProjectOwnerName","150","1"});
			v.addElement(new String[]{"usage","Usage (hits)","Usage (hits)","HitCount","30",""});
			v.addElement(new String[]{"intusage","Internal usage","Internal usage","IntHitCount","30",""});
			v.addElement(new String[]{"extusage","External usage","External usage","ExtHitCount","30",""});
		}
		else if (s.equals("WS0006")){
			v.addElement(new String[]{"wstype","Workspace type","Workspace type","WorkspaceType","50",""});
			v.addElement(new String[]{"delteam","Delivery team","Delivery team","DeliveryTeam","50",""});
			v.addElement(new String[]{"company","Company","Company","ProjectCompany","50",""});
			v.addElement(new String[]{"inds","Industry","Industry","Inds","175",""});
			v.addElement(new String[]{"geos","Geography","Geography","Geos","175",""});
			v.addElement(new String[]{"wsname","Workspace name","Workspace name","ProjectName","150",""});
			v.addElement(new String[]{"wsowner","Workspace owner","Workspace owner","ProjectOwnerName","150","1"});
			v.addElement(new String[]{"tabname","Tab name","Tab name","TabName","150",""});
			v.addElement(new String[]{"usage","Usage (hits)","Usage (hits)","HitCount","30"});
			v.addElement(new String[]{"intusage","Internal usage","Internal usage","IntHitCount","30",""});
			v.addElement(new String[]{"extusage","External usage","External usage","ExtHitCount","30",""});
		}
		else if (s.equals("WS0007")){
			v.addElement(new String[]{"wstype","Workspace type","Workspace type","WorkspaceType","50",""});
			v.addElement(new String[]{"delteam","Delivery team","Delivery team","DeliveryTeam","50",""});
			v.addElement(new String[]{"company","Company","Company","ProjectCompany","50",""});
			v.addElement(new String[]{"inds","Industry","Industry","Inds","175",""});
			v.addElement(new String[]{"geos","Geography","Geography","Geos","175",""});
			v.addElement(new String[]{"wsname","Workspace name","Workspace name","ProjectName","150",""});
			v.addElement(new String[]{"wsowner","Workspace owner","Workspace owner","ProjectOwnerName","150","1"});
			v.addElement(new String[]{"members","Members","Members","MemberCount","30",""});
			v.addElement(new String[]{"intmemb","Internal members","Internal members","IntMemberCount","30",""});
			v.addElement(new String[]{"extmemb","External members","External members","ExtMemberCount","30",""});
		}
		else if (s.equals("SA0001")){
			v.addElement(new String[]{"wsname","Workspace name","Workspace name","ProjectName","200",""});
			v.addElement(new String[]{"totiss","Total issues","Total issues","IssuesCount","100",""});
			v.addElement(new String[]{"totchanges","Total changes","Total changes","ChangesCount","100",""});
			v.addElement(new String[]{"totfeedbacks","Total feedbacks","Total feedbacks","FeedbacksCount","100",""});
			
		}
		else if (s.equals("SA0002")){
			v.addElement(new String[]{"wsname","Workspace name","Workspace name","ProjectName","150",""});
			v.addElement(new String[]{"username","User name","User name","UserName","100","1"});
			v.addElement(new String[]{"userid","User id","User id","UserId","100",""});
			v.addElement(new String[]{"useremail","User email","User email","UserEmail","100",""});	
			v.addElement(new String[]{"lastlogin","Last logon","Last logon","LastLogonStr","100",""});
			v.addElement(new String[]{"logincount","Login count","Login Count","LogonCount","100",""});		
			v.addElement(new String[]{"userrole","User role","User role","RoleName","100",""});
		}
		else if (s.equals("SA0003")){
			v.addElement(new String[]{"username","User name","User name","UserName","100","1"});
			v.addElement(new String[]{"userid","User id","User id","UserId","100",""});
			v.addElement(new String[]{"useremail","User email","User email","UserEmail","100",""});	
			v.addElement(new String[]{"wsname","Workspace name","Workspace name","ProjectName","150",""});
			v.addElement(new String[]{"userrole","User role","User role","RoleName","100",""});
		}
		else if (s.equals("BC0001")){
			v.addElement(new String[]{"title","Title","Title","Title","500",""});
			v.addElement(new String[]{"hits","Hits","Hits","HitCount","100",""});
			v.addElement(new String[]{"intusage","Internal usage","Internal usage","IntHitCount","30",""});
			v.addElement(new String[]{"extusage","External usage","External usage","ExtHitCount","30",""});
		}
		else if (s.equals("BC0002")){
			//variable, select name (checkbox),result header,method name, width
			v.addElement(new String[]{"sal","Salutation","Sal","Sal","20",""});
			v.addElement(new String[]{"fname","First name","FName","Fname","60",""});
			v.addElement(new String[]{"lname","Last name","LName","Lname","60",""});
			v.addElement(new String[]{"suffix","Suffix","Suffix","Suffix","20",""});
			v.addElement(new String[]{"email","Email","Email","Email","60",""});
			v.addElement(new String[]{"dphone","Day Phone","Day phone","DayPhone","100",""});
			v.addElement(new String[]{"fax","Fax","Fax","Fax","100",""});
			v.addElement(new String[]{"evphone","Ev Phone","Ev phone","EvenPhone","100",""});
			v.addElement(new String[]{"pmphone","Pag/Mob Phone","Pag.Mob Phone","PagmobPhone","100",""});
			v.addElement(new String[]{"job","Job Title","Job Title","JobTitle","100",""});
			v.addElement(new String[]{"add1","Addr 1","Addr 1","StAddr1","100",""});
			v.addElement(new String[]{"add2","Addr 2","Addr 2","StAddr2","100",""});
			v.addElement(new String[]{"city","City","City","City","60",""});
			v.addElement(new String[]{"state","State/Prov","State/Prov","Stprov","30",""});
			v.addElement(new String[]{"zip","Post code","Post code","Postcode","50",""});
			v.addElement(new String[]{"country","Country","Country","Country","50",""});
			v.addElement(new String[]{"comp","Company","Company","Company","100",""});
			v.addElement(new String[]{"uid","UserId","UserId","Userid","100",""});
			v.addElement(new String[]{"licdate","License Date","License Date","LicenseDateStr","100",""});
			v.addElement(new String[]{"revoked","Revoked","Revoked","Revoked","30",""});

		}
		
		return v;	
	}




}


