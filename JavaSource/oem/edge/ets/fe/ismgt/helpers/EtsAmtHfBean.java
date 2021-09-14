/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004                                          */
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

package oem.edge.ets.fe.ismgt.helpers;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import oem.edge.amt.AMTException;
import oem.edge.amt.AmtCommonUtils;
import oem.edge.amt.AmtHeaderFooter;
import oem.edge.amt.AmtHfConstants;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.amt.LeftNavExceptions;
import oem.edge.common.DbConnect;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.ets.fe.Defines;
import oem.edge.ets.fe.ETSCat;
import oem.edge.ets.fe.ETSDBUtils;
import oem.edge.ets.fe.ETSDatabaseManager;
import oem.edge.ets.fe.ETSHeaderFooter;
import oem.edge.ets.fe.ETSProj;
import oem.edge.ets.fe.ETSProjectInfoBean;
import oem.edge.ets.fe.ETSProperties;
import oem.edge.ets.fe.ETSSearchCommon;
import oem.edge.ets.fe.ETSUtils;
import oem.edge.ets.fe.ismgt.model.EtsIssFilterObjectKey;
import oem.edge.ets.fe.ismgt.model.EtsPrimaryContactInfo;
import oem.edge.ets.fe.ismgt.resources.EtsIssFilterConstants;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;

/**
 * @author v2phani
 * The AMT Bean mainly to give the Header,Footer,Leftnav and subheader
 * mainly intended for use in JSPs
 *
 */

public class EtsAmtHfBean implements Serializable, EtsIssFilterConstants, AmtHfConstants {

	public static final String VERSION = "1.54";

	private AmtHeaderFooter amtHf;
	private String helppage = "";

	//params
	private String linkid = "";
	private String issueProjId = "";
	private String topCatStr = "";
	private String curCatStr = "";
	private String sCatName = "";

	///
	//get the project details
	private ETSProj etsProj;
	private ETSCat topCat;
	private ETSCat currentCat;
	private EtsPrimaryContactInfo etsContact;

	//
	private String sTabStr = "";
	private String contactModStr = "";
	///common leftnav params//
	private EtsIssFilterObjectKey newIssobjkey;

	///
	private String globalWebRoot = "";
	private boolean isITAR=false;

	////for 521 with EtsHeaderFooter
	private String etsHeader = "";
	private String etsTabStr = "";
	private String etsFooter = "";

	/**
	 * Constructor for EtsAmtBean.
	 */

	public EtsAmtHfBean() {
		super();

	}

	/**
	 * Constructor for EtsAmtBean.
	 */
	public EtsAmtHfBean(HttpServletRequest _request, HttpServletResponse _response, EtsIssFilterObjectKey issobjkey) {
		super();
		init(_request, _response, issobjkey);
	}

	/**
	 * Returns the Footer and at the end of footer,releases the connection, given
	 * to amtheaderfooter
	 * @return java.lang.String
	 */

	public String getFooter() {

		String footer = "";
		Connection conn = null;
		try {

			conn = amtHf.getConnection();

			if (amtHf != null) {

				footer = amtHf.printBullsEyeFooter();

			}

		} finally {

			try {

				if (conn != null) {

					conn.close();
				}
			} catch (SQLException ex) {

				ex.printStackTrace();
			}

		}

		EtsIssCommonGuiUtils comGuiUtils = new EtsIssCommonGuiUtils();

		StringBuffer sb = new StringBuffer();
		sb.append("<br />");
		sb.append(comGuiUtils.printSecureContentFooter(isITAR));

		return sb.toString() + footer;
	}

	/**
	 * returns the Header
	 * @return java.lang.String
	 */

	public String getHeader() {

		

		if (amtHf != null) {
				return ETSSearchCommon.getMasthead(amtHf, etsProj, topCat, linkid);
			} else {
				return "";
			}

	}

	/**
	 * Returns the leftnavg, catches leftnav exception
	 * and closes the connection on exception
	 * @return java.lang.String
	 */

	public String getLeftNavigation() {

		String leftnavigation = "";

		if (amtHf != null) {

			try {

				leftnavigation = amtHf.printBullsEyeLeftNav();

			} catch (LeftNavExceptions le) {

				Exception leftEx = le.getException();

				if (leftEx != null && (leftEx instanceof SQLException)) {

					Connection conn = amtHf.getConnection();

					try {

						if (conn != null) {

							conn.close();

						}

					} catch (SQLException ex) {

						ex.printStackTrace();
					}

				}

				if (leftEx != null) {

					SysLog.log(SysLog.ERR, this, leftEx);
					leftEx.printStackTrace();

				}

			}

		} else {

			leftnavigation = "";

		}

		return leftnavigation;

	}

	/**
	 * get the current leftnav state
	 * @return java.lang.String
	 */

	public String getLinkId() {
		return amtHf.getLinkId();

	}

	/**
	 * returns the leftnav subheader
	 * @return java.lang.String
	 */

	public String getSubHeader() {

		String subheader = "";

		if (amtHf != null) {

			try {

				subheader = amtHf.printSubHeader();

			} catch (LeftNavExceptions le) {

				Exception leftEx = le.getException();

				if (leftEx != null && (leftEx instanceof SQLException)) {

					Connection conn = amtHf.getConnection();

					try {

						if (conn != null) {

							conn.close();

						}

					} catch (SQLException ex) {

						ex.printStackTrace();
					}

				}

				if (leftEx != null) {

					SysLog.log(SysLog.ERR, this, leftEx);
					leftEx.printStackTrace();

				}

			}

		} else {

			subheader = "";

		}

		return subheader;
	}

	/**
	 * This is the default init method, in which every jsp should initialize
	 * before start using bean left nav methods
	 * @return java.lang.String
	 */

	public void init(HttpServletRequest _request, HttpServletResponse _response, EtsIssFilterObjectKey issobjkey) {

		HttpSession ses = _request.getSession(true);
		EdgeAccessCntrl es = new EdgeAccessCntrl();
		DbConnect db = null;
		Hashtable params = new Hashtable();

		try {

			if (!Global.loaded) {

				Global.Init();
			}

			globalWebRoot = Global.WebRoot;

			db = new DbConnect();
			db.makeConn(ETSDATASRC);

			ETSHeaderFooter headerfooter = new ETSHeaderFooter();
			headerfooter.init(_request, _response);
			etsHeader = headerfooter.getOnlyHeader().toString();
			etsFooter = headerfooter.getFooter().toString();
			etsTabStr = headerfooter.getTabStr().toString();

			newIssobjkey = issobjkey;
			///set common params///
			es = issobjkey.getEs();
			linkid = issobjkey.getLinkid();
			issueProjId = issobjkey.getProjectId();
			topCatStr = issobjkey.getTc();
			curCatStr = issobjkey.getCc();
			isITAR=issobjkey.getProj().isITAR();

			//get the project details//
			String reqProjectId = issobjkey.getProjectId();

			UnbrandedProperties prop = PropertyFactory.getProperty(db.conn,reqProjectId);

			if (!AmtCommonUtils.isResourceDefined(linkid)) {

				linkid = prop.getLinkID();

			}

			params = getServletParameters(_request);

			amtHf = new AmtHeaderFooter(es, db.conn, params);

			//get the project details//
			ETSProj proj = ETSUtils.getProjectDetails(db.conn,reqProjectId); 
			String sLink = issobjkey.getLinkid();

			//set common params
			EtsIssCommonSessnParams etsCommonSessn = new EtsIssCommonSessnParams(ses, reqProjectId);
			etsProj = etsCommonSessn.getEtsProj();
			etsContact = etsCommonSessn.getEtsContInfo();

			//set the pop-up
			amtHf.setPopUp("J");

			//			for help...
			ETSProperties properties = new ETSProperties();

			if (properties.displayHelp() == true) {
				amtHf.setPageHelp(Defines.SERVLET_PATH + "ETSHelpServlet.wss?proj=" + etsProj.getProjectId());
			}

			boolean bInternal = false;
			boolean bClientRole = false;

			if (es.gDECAFTYPE.trim().equalsIgnoreCase("I")) {
				bInternal = true;
			}

			if (ETSUtils.checkUserRole(es, etsProj.getProjectId()).equalsIgnoreCase(Defines.WORKSPACE_CLIENT)) {
				bClientRole = true;
			}

			// FOR BLADE CENTER
			Vector graphTabs = new Vector();
			Vector graphTabs1 = ETSDatabaseManager.getTopCats(etsProj.getProjectId());
			Vector userprivs = ETSDatabaseManager.getUserPrivs(etsProj.getProjectId(), es.gIR_USERN);
			if (ETSUtils.checkUserRole(es, etsProj.getProjectId()).equals(Defines.ETS_ADMIN)) {
				graphTabs = graphTabs1;
			} else {
				for (int igt = 0; igt < graphTabs1.size(); igt++) {
					boolean bAdd = false;
					Vector privs = ((ETSCat) (graphTabs1.elementAt(igt))).getPrivsInts();
					if (privs == null) {
						bAdd = true;
					} else if (privs.size() <= 0) {
						bAdd = true;
					} else {
						for (int p = 0; p < privs.size(); p++) {
							if (privs.elementAt(p).toString().equalsIgnoreCase(String.valueOf(Defines.IBM_ONLY))) {
								if (bInternal && !bClientRole) {
									bAdd = true;
									break;
								}
							} else if (userprivs.contains(privs.elementAt(p))) {
								bAdd = true;
								break;
							}
						}
					}
					if (bAdd)
						graphTabs.addElement(graphTabs1.elementAt(igt));
				}
			}
			// END OF BLADE

			//get the tabs//

			int topCatId = 0;

			if (graphTabs.size() == 0) {

				Global.println("There are no folders associated with this project.");

			} else {

				if (!topCatStr.equals("")) {

					topCatId = (new Integer(topCatStr)).intValue();

				} else {

					topCatId = ((ETSCat) graphTabs.elementAt(0)).getId();
					topCatStr = String.valueOf(topCatId);

				}

				topCat = (ETSCat) ETSDatabaseManager.getCat(topCatId);

				currentCat = topCat;

				if (AmtCommonUtils.isResourceDefined(curCatStr)) {

					sCatName = getCatName(db.conn, curCatStr); // get the cat nam

				}

				//commented for more clarity in closing connections
				//Connection con = db.conn;

				///new521

				String topCatStr = ETSUtils.checkNull(_request.getParameter("tc"));
				if (!topCatStr.equals("")) {
					topCatId = (new Integer(topCatStr)).intValue();
				} else {
					topCatId = ((ETSCat) graphTabs.elementAt(0)).getId();
					topCatStr = String.valueOf(topCatId);
				}

				ETSCat topCat = (ETSCat) ETSDatabaseManager.getCat(topCatId);

				setTopCat(topCat);

				String sPrinterFriendly = _request.getParameter("skip");
				if (sPrinterFriendly == null) {
					sPrinterFriendly = "N";
				} else {
					sPrinterFriendly = sPrinterFriendly.trim();
				}

				System.out.println("SKIP********" + sPrinterFriendly);

				String sArchive = "";
				if (etsProj.getProject_status().equalsIgnoreCase("A")) {
					// archieved workspace.
					sArchive = " [ <span style=\"color:#ff0000\">Archived</span> ]";
				}

				boolean bAdmin = es.Qualify(Defines.ETS_ADMIN_ENTITLEMENT, "tg_member=MD");
				boolean bExecutive = es.Qualify(Defines.ETS_EXECUTIVE_ENTITLEMENT, "tg_member=MD");

				int iCount = 0;

				if (bAdmin || bExecutive) {
					iCount = 2;
				} else {
					iCount = getWorkspaceCount(db.conn, es.gIR_USERN);
				}

				String sSwitch = "";
				String sBegin = "";

				///set the page title,page header, page subheader st///

				if (iCount > 1) {
					sSwitch = "<span style=\"fnt\">&nbsp;[<a href=\"" + Defines.SERVLET_PATH +  prop.getLandingPageURL() + "?linkid=" + prop.getLinkID() + "\" >Switch workspace</a>]&nbsp;</span>";
				}
				String strParentWS = ""; 
				if (!etsProj.getParent_id().equalsIgnoreCase("0")) {
					 ETSProj parentProj = ETSUtils.getProjectDetails(db.conn, proj.getParent_id());
				     strParentWS = "<br/>(Parent workspace is <a href=\"" +Defines.SERVLET_PATH + "ETSProjectsServlet.wss?proj=" + parentProj.getProjectId()+ "&linkid=" + sLink + "\">" + parentProj.getName() + "</a>)";
								sBegin = "Subworkspace for ";
							} else {
								sBegin = "Workspace for ";
							}

				String strITARWarning = "";
				
				if (proj.isITAR()) {
				    strITARWarning = "<br /><span style=\"color:#ff3333\">ITAR information contained in this workspace is export restricted and must only be accessed when physically located in the U.S.</span>";
				}
				if(etsProj.getProjectType().trim().equals(Defines.AIC_WORKSPACE_TYPE)){
									amtHf.setPageTitle("Collaboration Center - " + etsProj.getName() + " - " + topCat.getName());
									amtHf.setHeader("Collaboration Center");
									amtHf.setSubHeader(sBegin + etsProj.getName() + sArchive + strParentWS + strITARWarning);
									
				} else if (etsProj.getProjectOrProposal().trim().equalsIgnoreCase("P")) {
					amtHf.setPageTitle("E&TS Connect - " + etsProj.getName() + " - " + topCat.getName());
					amtHf.setHeader("My E&TS projects");
					amtHf.setSubHeader(sBegin + etsProj.getName() + sArchive + strParentWS + strITARWarning);
					
				} else if (etsProj.getProjectOrProposal().trim().equalsIgnoreCase("O")) {
					amtHf.setPageTitle("E&TS Connect - " + etsProj.getName() + " - " + topCat.getName());
					amtHf.setHeader("My E&TS proposals");
					amtHf.setSubHeader(sBegin + etsProj.getName() + sArchive + strParentWS + strITARWarning);
					
				} else if (etsProj.getProjectOrProposal().trim().equalsIgnoreCase("M")) {
					amtHf.setPageTitle("E&TS Connect - " + etsProj.getName() + " - " + topCat.getName());
					amtHf.setHeader("Metrics");
					amtHf.setSubHeader("Workspace for metrics");
					
				} else if (etsProj.getProjectOrProposal().trim().equalsIgnoreCase("C")) {
					amtHf.setPageTitle("E&TS Client Voice - " + etsProj.getCompany() + " - " + topCat.getName());
					amtHf.setHeader("E&TS Client Voice");
					amtHf.setSubHeader(sBegin + etsProj.getName() + sArchive+ strParentWS + strITARWarning);
					
				}

				///start521

				///set the page title,page header, page subheader st///

				///////////////////

				ETSProjectInfoBean projBean = (ETSProjectInfoBean) _request.getSession(false).getAttribute("ETSProjInfo");

				if (projBean == null || !projBean.isLoaded()) {

					projBean = ETSUtils.getProjInfoBean(db.conn);
					_request.getSession(false).setAttribute("ETSProjInfo", projBean);
				}

				sTabStr = createGraphTabs(graphTabs, topCatId, linkid, db.conn);

				///contact info str//
				EtsIssFilterGuiUtils etsGuiUtil = new EtsIssFilterGuiUtils();
				contactModStr = etsGuiUtil.getPrimaryContactModule(etsContact, reqProjectId);

			} //end of graph tabs

		} catch (LeftNavExceptions le) {

			Exception leftEx = le.getException();

			if (leftEx != null && (leftEx instanceof java.sql.SQLException)) {

				AmtCommonUtils.LogSqlExpMsg((SQLException) leftEx, "SQL/LHN Exception  in EtsAmtHfBean", ETSLSTUSR);
				db.removeConn((SQLException) leftEx);
			}

			AmtCommonUtils.LogGenExpMsg(leftEx, "Gen/LHN Exception  in EtsAmtHfBean", ETSLSTUSR);

			if (leftEx != null) {
				SysLog.log(SysLog.ERR, this, leftEx);
				leftEx.printStackTrace();

			}

		} catch (AMTException amtException) {

			Exception innerException = amtException.getException();

			if (innerException != null && (innerException instanceof java.sql.SQLException)) {

				AmtCommonUtils.LogSqlExpMsg((SQLException) innerException, "SQL/LHN Exception  in EtsAmtHfBean", ETSLSTUSR);
				db.removeConn((SQLException) innerException);

			}

			AmtCommonUtils.LogGenExpMsg(amtException, "AMTException", ETSLSTUSR);

			if (innerException != null) {
				SysLog.log(SysLog.ERR, this, innerException);
				innerException.printStackTrace();

			}

		} catch (SQLException se) {

			AmtCommonUtils.LogSqlExpMsg(se, "SQL Exception in EtsAmtHfBean & in making db.conn", ETSLSTUSR);

			if (db != null) {
				db.removeConn(se);
			}
			if (se != null) {
				SysLog.log(SysLog.ERR, this, se);
				se.printStackTrace();

			}

		} catch (Exception ex) {

			AmtCommonUtils.LogGenExpMsg(ex, "General Exception in building EtsAmtHfBean", ETSLSTUSR);

			if (ex != null) {
				SysLog.log(SysLog.ERR, this, ex);
				ex.printStackTrace();

			}

		} finally {

			if (db != null)
				db.closeConn();
			db = null;
		}

	} //end of init method

	/**
	 * Insert the method's description here.
	 * Creation date: (09/28/01 3:31:04 PM)
	 * @return java.lang.String
	 */

	public void setPageHeader(String _pageheader) {
		amtHf.setHeader(_pageheader);
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (07/18/01 10:25:22 AM)
	 * @param newFcstVersion int
	 */

	public void setPageHelp(String _helpfile) {
		helppage = _helpfile;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (07/18/01 10:25:22 AM)
	 * @param newFcstVersion int
	 */
	public void setPageTitle(String _pagetitle) {
		amtHf.setPageTitle(_pagetitle);
	}

	public Hashtable getServletParameters(HttpServletRequest req) throws java.lang.Exception {
		Hashtable hs = new Hashtable();
		Enumeration e = req.getParameterNames();

		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			//hs.put(name, req.getParameter(name));
			String value = null;
			String values[] = req.getParameterValues(name);

			if (values != null) {
				if (values.length == 1)
					value = values[0];
				else {
					StringBuffer sb = new StringBuffer();

					for (int i = 0; i < values.length; i++) {
						if (i > 0)
							sb.append(",");
						sb.append(values[i]);
					}

					value = sb.toString();
				}
			}

			hs.put(name, value);
		}

		return hs;
	}

	/**
	 * This method will return the HTML INDEX ROOT
	 * Creation date: (09/07/01 5:12:56 PM)
	 * @return String
	 * @param sResource java.lang.String
	 */

	public String getWebHtmlRoot() {

		return amtHf.getWebHtmlRoot();

	}

	/**
	 * This method will return the HTML JS ROOT
	 * Creation date: (09/07/01 5:12:56 PM)
	 * @return String
	 * @param sResource java.lang.String
	 */

	public String getWebJsRoot() {

		return amtHf.getWebJsRoot();

	}

	/**
	 * This method will return the HTML CSS ROOT
	 * Creation date: (09/07/01 5:12:56 PM)
	 * @return String
	 * @param sResource java.lang.String
	 */

	public String getWebCssRoot() {

		return amtHf.getWebCssRoot();

	}

	/**
	 * This method will return the HTML IMG ROOT
	 * Creation date: (09/07/01 5:12:56 PM)
	 * @return String
	 * @param sResource java.lang.String
	 */

	public String getWebImgRoot() {

		return amtHf.getWebImgRoot();

	}

	/**
	 * Method getCatName.
	 * @param conn
	 * @param projectidStr
	 * @param curCatStr
	 * @return String
	 */
	private String getCatName(Connection conn, String curCatStr) throws SQLException, Exception {

		Statement stmt = null;
		ResultSet rs = null;
		StringBuffer sQuery = new StringBuffer("");
		String sCatName = "";

		try {

			sQuery.append("SELECT CAT_NAME FROM ETS.ETS_CAT WHERE CAT_ID=" + curCatStr + " for READ ONLY");

			SysLog.log(SysLog.DEBUG, "EtsAmtHfBean::getCatName()", "QUERY : " + sQuery.toString());

			stmt = conn.createStatement();
			rs = stmt.executeQuery(sQuery.toString());

			if (rs.next()) {
				sCatName = ETSUtils.checkNull(rs.getString("CAT_NAME"));
			}

		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			ETSDBUtils.close(rs);
			ETSDBUtils.close(stmt);
		}

		return sCatName;

	}

	/**
	 * This method will print the graphic tabs
	 *
	 */

	private String createGraphTabs(Vector graphTabs, int topCatId, String sLink, Connection conn) {

	    return ETSHeaderFooter.createGraphTabs(graphTabs, topCatId, sLink, conn).toString();
/*
		StringBuffer buf = new StringBuffer();

		int size = graphTabs.size();
		int indexCat = 0;
		ETSCat tCat = (ETSCat) graphTabs.elementAt(0);
		boolean foundflag = false;

		if (topCatId != 0) {
			try {
				//System.err.println("inside try");
				tCat = (ETSCat) ETSDatabaseManager.getCat(topCatId);
				if (tCat != null) {
					for (int i = 0; i < size; i++) {
						ETSCat c = (ETSCat) graphTabs.elementAt(i);
						if (c.getId() == topCatId) {
							foundflag = true;
							indexCat = i;
							break;
						}
					}
				}

				//indexCat = graphTabs.indexOf(tCat);
				if (!foundflag) {
					System.err.println("******************* in !foundflag");
					indexCat = 0;
					tCat = (ETSCat) graphTabs.elementAt(0);
				}
			} catch (SQLException se) {
				System.out.println("sqlex in createGrTabs. e=" + se);
				indexCat = 0;
				tCat = (ETSCat) graphTabs.elementAt(0);
			}
		}

		buf.append("<table cellpadding=\"0\" cellspacing=\"0\" width=\"443\" border=\"0\">");
		buf.append("<tr><td class=\"tbimage1\">");
		buf.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" >");
		buf.append("<tr>");

		//for top of graph tabs
		if (indexCat != 0) {
			buf.append("<td><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
		}
		buf.append("<td><img src=\"" + Defines.V11_IMAGE_ROOT + "tabs/tab_left_t.gif\" width=\"4\" height=\"1\" alt=\"\" /></td>");
		buf.append("<td class=\"tbdark\"><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
		buf.append("<td><img src=\"" + Defines.V11_IMAGE_ROOT + "tabs/tab_right_t.gif\" width=\"4\" height=\"1\" alt=\"\" /></td>");

		if (indexCat != size - 1) {
			buf.append("<td><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>");
		}

		buf.append("</tr><tr>");

		//for tabs left of topcat
		boolean leftFlag = false;
		for (int i = 0; i < indexCat; i++) {
			leftFlag = true;
			if (i != 0) {
				buf.append("&nbsp;&nbsp;<span class=\"divider\">|</span>&nbsp;&nbsp;");
			} else if (i == 0) {
				buf.append("<td class=\"tbimage2\">&nbsp;&nbsp;");
			}
			ETSCat cat = (ETSCat) graphTabs.elementAt(i);
			if (cat.getName().equals(ETSISSUECATHEADER)) {
				buf.append("<a class=\"tablink\" href=\"EtsIssFilterCntrlServlet.wss?proj=" + cat.getProjectId() + "&tc=" + cat.getId() + "&linkid=" + sLink + "&istyp=iss&opn=10\">" + cat.getName() + "</a>");
			} else {
				buf.append("<a class=\"tablink\" href=\"ETSProjectsServlet.wss?proj=" + cat.getProjectId() + "&tc=" + cat.getId() + "&linkid=" + sLink + "\">" + cat.getName() + "</a>");
			}

		}
		if (leftFlag) {
			buf.append("</td>");
		}

		buf.append("<td><img src=\"" + Defines.V11_IMAGE_ROOT + "tabs/tab_left_b.gif\" width=\"4\" height=\"21\" alt=\"\" /></td>");
		ETSCat tCat2 = (ETSCat) graphTabs.elementAt(indexCat);
		if (tCat2.getName().equals(ETSISSUECATHEADER)) {
			buf.append("<td class=\"tbwhite\">&nbsp;&nbsp;<a class=\"tbmainlink\" href=\"EtsIssFilterCntrlServlet.wss?proj=" + tCat2.getProjectId() + "&tc=" + tCat2.getId() + "&linkid=" + sLink + "&istyp=iss&opn=10\">" + tCat2.getName() + "</a>&nbsp;&nbsp;</td>");
		} else {
			buf.append("<td class=\"tbwhite\">&nbsp;&nbsp;<a class=\"tbmainlink\" href=\"ETSProjectsServlet.wss?proj=" + tCat2.getProjectId() + "&tc=" + tCat2.getId() + "&linkid=" + sLink + "\">" + tCat2.getName() + "</a>&nbsp;&nbsp;</td>");
		}
		buf.append("<td><img src=\"" + Defines.V11_IMAGE_ROOT + "tabs/tab_right_b.gif\" width=\"4\" height=\"21\" alt=\"\" /></td>");

		//for tabs right of topcat
		boolean rightFlag = false;
		for (int i = indexCat + 1; i < size; i++) {
			rightFlag = true;
			if (i != indexCat + 1) {
				buf.append("&nbsp;&nbsp;<span class=\"divider\">|</span>&nbsp;&nbsp;");
			} else if (i == indexCat + 1) {
				buf.append("<td class=\"tbimage2\">&nbsp;&nbsp;");
			}
			ETSCat cat = (ETSCat) graphTabs.elementAt(i);
			if (cat.getName().equals(ETSISSUECATHEADER)) {
				buf.append("<a class=\"tablink\" href=\"EtsIssFilterCntrlServlet.wss?proj=" + cat.getProjectId() + "&tc=" + cat.getId() + "&linkid=" + sLink + "&istyp=iss&opn=10\">" + cat.getName() + "</a>");
			} else {

				buf.append("<a class=\"tablink\" href=\"ETSProjectsServlet.wss?proj=" + cat.getProjectId() + "&tc=" + cat.getId() + "&linkid=" + sLink + "\">" + cat.getName() + "</a>");
			}

		}
		if (rightFlag) {
			buf.append("</td>");
		}

		buf.append("</tr>");
		buf.append("</table>");
		buf.append("</td></tr>");
		buf.append("<tr><td><img src=\"" + Defines.TOP_IMAGE_ROOT + "c.gif\" width=\"1\" height=\"10\" alt=\"\" /></td></tr></table>");

		return buf.toString();
*/
	}

	/**
	 * This method will return the HTML JS ROOT
	 * Creation date: (09/07/01 5:12:56 PM)
	 * @return String
	 * @param sResource java.lang.String
	 */

	public String getTabIndex() {

		return sTabStr;

	}

	/***
	 * get primary contact module
	 * getPrimaryContactModule()
	 */

	public String getPrimaryContactModule() {

		return contactModStr;

	}

	/**
	 * get customized bread-crumb trail
	 */

	public String getIssueBreadCrumb(EtsIssFilterObjectKey issobjkey) {

		StringBuffer sb = new StringBuffer();

		sb.append("<table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" width=\"443\" border=\"0\">\n");
		sb.append("<tbody>\n");
		sb.append("<tr>\n");
		sb.append("<td class=\"small\">\n");
		sb.append(" <a href=\"EtsIssFilterCntrlServlet.wss?proj=" + issobjkey.getProjectId() + "&linkid=" + issobjkey.getLinkid() + "&istyp=" + issobjkey.getProblemType() + "&opn=10&tc=" + issobjkey.getTc() + "\">Issues/changes</a> &gt; <b>" + issobjkey.getStateDesc() + "</b></td>\n");
		sb.append("</tr>\n");
		sb.append("</tbody>\n");
		sb.append("</table>\n");

		return sb.toString();

	}

	/***
		 * get primary contact module
		 * getPrimaryContactModule()
		 */

	public String getGlobalWebRoot() {

		return globalWebRoot;

	}

	public String getEtsHeader() {

		return etsHeader;
	}

	public String getEtsFooter() {

		return etsFooter;
	}

	public String getEtsTabStr() {

		return etsTabStr;
	}

	/**
	 * @param cat
	 */
	public void setTopCat(ETSCat cat) {
		this.topCat = cat;
	}
	
	public  int getWorkspaceCount(Connection con, String sUserId) throws SQLException, Exception {

			Statement stmt = null;
			ResultSet rs = null;
			StringBuffer sQuery = new StringBuffer("");
			int iCount = 0;

			try {

				sQuery.append("SELECT COUNT(PROJECT_ID) FROM ETS.ETS_PROJECTS WHERE PROJECT_ID IN (SELECT USER_PROJECT_ID FROM ETS.ETS_USERS WHERE USER_ID = '" + sUserId + "' AND ACTIVE_FLAG = '" + Defines.USER_ENTITLED + "') AND PROJECT_STATUS != '" + Defines.WORKSPACE_DELETE + "' for READ ONLY");

				SysLog.log(SysLog.DEBUG, "ETSConnectServlet::getMyProposalsCount", "Query : " + sQuery.toString());

				stmt = con.createStatement();
				rs = stmt.executeQuery(sQuery.toString());

				Vector vDetails = new Vector();

				if (rs.next()) {

					iCount = rs.getInt(1);
				}

			} catch (SQLException e) {
				throw e;
			} catch (Exception e) {
				throw e;
			} finally {

				ETSDBUtils.close(rs);
				ETSDBUtils.close(stmt);

			}

			return iCount;
		}
		
	/**
		 * Returns the Footer and at the end of footer,releases the connection, given
		 * to amtheaderfooter
		 * @return java.lang.String
		 */

		public String getFooterWithNoKeyMsg() {

			String footer = "";
			Connection conn = null;
			try {

				conn = amtHf.getConnection();

				if (amtHf != null) {

					footer = amtHf.printBullsEyeFooter();

				}

			} finally {

				try {

					if (conn != null) {

						conn.close();
					}
				} catch (SQLException ex) {

					ex.printStackTrace();
				}

			}

			

			return  footer;
		}




} //end of class
