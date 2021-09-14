package oem.edge.ets.fe;

/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2003-2004                                     */
/*                                                                          */
/*     All Rights Reserved                                                  */
/*     US Government Users Restricted Rights                                */
/*                                                                          */
/*     The source code for this program is not published or otherwise       */
/*     divested of its trade secrets, irrespective of what has been         */
/*     deposited with the US Copyright Office.                              */
/*                                                                          */
/*   --------------------------------------------------------------------   */
/*     Please do not remove any of these commented lines  20 lines          */
/*   --------------------------------------------------------------------   */
/*                       Copyright Footer Check                             */

/**
 * @author  Navneet Gupta (navneet@us.ibm.com)
 * @since   custcont.3.7.1
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import oem.edge.amt.AMTException;
import oem.edge.amt.AmtErrorHandler;
import oem.edge.amt.AmtHeaderFooter;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.DbConnect;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;
import oem.edge.ets.fe.self.ETSSelfConstants;

import org.apache.commons.logging.Log;

import com.ibm.hrl.juru.DocScore;
import com.ibm.hrl.juru.TfIdfQuery;

public class ETSSearchServlet extends HttpServlet {

	public static final String Copyright = "(C) Copyright IBM Corp. 2003, 2004";

	public static final String VERSION_SID = "1.79";
	public static final String LAST_UPDATE = "10/6/06 18:31:53";

	private static final Class thisClass = ETSSearchServlet.class;

	private static final Log log = ETSSearchCommon.getLog(thisClass);

	private static final boolean DEBUG_FLAG = false;

	static final String AMT_DS = "amtds";
	static final String ETS_DS = "etsds";

	private static final int MAX_RESULTS = 100;
	private static final int RESULTS_PER_PAGE = 10;

	private static boolean initialized;
	private static long initializeTime;

	private ETSSearchMonitor indexCache;

	private static final TreeMap LINKIDS = new TreeMap();

	// private static String LANDING_PAGE_INFO_LINK;
	// private static String LANDING_PAGE_IMAGE_ALT_TEXT;

	static final String PROJECTS_SERVLET =
		Defines.SERVLET_PATH + "ETSProjectsServlet.wss";

	static final String CAT_SERVLET =
		Defines.SERVLET_PATH + "displayDocumentList.wss";

	static final String DOC_SERVLET =
		Defines.SERVLET_PATH + "displayDocumentDetails.wss";

	static final String FILE_SERVLET =
		Defines.SERVLET_PATH + "ETSContentDeliveryServlet.wss";

	static final String CALENDAR_SERVLET =
		Defines.SERVLET_PATH + "ETSCalDisplayServlet.wss";

	static final String IMAGE_SERVLET =
		Defines.SERVLET_PATH + "ETSImageServlet.wss";

	static final String IBM_ONLY_FLAG = "<span class=\"ast\">*</span>";
	static final String IBM_CONF_FLAG = "<span class=\"ast\">**</span>";
	static final String PRIVATE_FLAG = "<span class=\"ast\">#</span>";
	static final String EXPIRED_FLAG =
		"<span class=\"ast\"><b>&#8224;</b></span>";

	static final String HTML_SEPARATOR = " &gt; ";

	static final String TABLE_TAG =
		"<table summary=\"\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">";

	static final String CAT_IMAGE =
		"<img border=\"0\" height=\"9\" width=\"13\" alt=\"folder\" title=\"folder\" src=\""
			+ IMAGE_SERVLET
			+ "?proj=ETS_CAT_IMG&mod=0\" />";

	static final String POPUP_GIF =
		"<img src=\"//www.ibm.com/i/v14/icons/popup.gif\" border=\"0\" height=\"16\" width=\"16\" alt=\"This link will open in a pop-up window\" title=\"This link will open in a pop-up window\" />";

	static final String DOTTED_RULE_GIF =
		"<img src=\"//www.ibm.com/i/v14/rules/dotted_rule_443.gif\" border=\"0\" height=\"1\" width=\"443\" alt=\"\" />";

	static final String GRAY_RULE_GIF =
		"<img src=\"//www.ibm.com/i/v14/rules/gray_rule.gif\" border=\"0\" height=\"1\" width=\"443\" alt=\"\" />";

	static final String SECURE_CONTENT_GIF =
		"<img src=\"//www.ibm.com/i/v14/icons/key.gif\" border=\"0\" height=\"16\" width=\"16\" alt=\"\" />";

	static final String SECURE_CONTENT_TAG =
		TABLE_TAG
			+ "<tr height=\"25\" align=\"right\">"
			+ "<td>"
			+ SECURE_CONTENT_GIF
			+ "</td>"
			+ "<td width=\"90\">"
			+ "Secure content"
			+ "</td>"
			+ "</tr>"
			+ "</table>";

	static final String BACK_BUTTON =
		"\n<script type=\"text/javascript\" language=\"javascript\"><!--"
			+ "\n document.write(\"<br />\");"
			+ "\n document.write(\"<table width=\\\"100%\\\" border=\\\"0\\\" cellpadding=\\\"0\\\" cellspacing=\\\"0\\\"><tr>\");"
			+ "\n document.write(\"<td align=\\\"center\\\">\");"
			+ "\n document.write(\"<table border=\\\"0\\\" cellpadding=\\\"0\\\" cellspacing=\\\"0\\\"><tr>\");"
			+ "\n document.write(\"<td>\");"
			+ "\n document.write(\"<a href=\\\"javascript:history.back()\\\">\");"
			+ "\n document.write(\"<img src=\\\"//www.ibm.com/i/v14/buttons/arrow_lt.gif\\\" border=\\\"0\\\" height=\\\"21\\\" width=\\\"21\\\" alt=\\\"\\\" />\");"
			+ "\n document.write(\"</a>\");"
			+ "\n document.write(\"</td>\");"
			+ "\n document.write(\"<td>\");"
			+ "\n document.write(\"&nbsp;<b>\");"
			+ "\n document.write(\"<a class=\\\"fbox\\\" href=\\\"javascript:history.back()\\\">\");"
			+ "\n document.write(\"Back\");"
			+ "\n document.write(\"</a>\");"
			+ "\n document.write(\"</b>\");"
			+ "\n document.write(\"</td>\");"
			+ "\n document.write(\"</tr></table>\");"
			+ "\n document.write(\"</td>\");"
			+ "\n document.write(\"</tr></table>\");"
			+ "\n//--></script>\n";

	public void doPost(
		HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException, IOException {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {

		EdgeAccessCntrl edgeAccessCntrl = new EdgeAccessCntrl();

		try {

			String query = request.getParameter("q");

			String searchScope = request.getParameter("searchScope");
			if (searchScope != null) {
				searchScope = searchScope.trim();
				if (searchScope.length() == 0) {
					searchScope = null;
				}
			}

			if (searchScope != null && searchScope.equals("ibm")) {
				String q = "";
				if (query != null) {
					q = URLEncoder.encode(query, "UTF-8");
				}

				response.sendRedirect(
					request.getScheme()
						+ "://www.ibm.com/Search?v=14&lang=en&cc=us&q="
						+ q);
				return;
			}

			if (!getProfile(request, response, edgeAccessCntrl)) {
				return;
			}

			boolean isAdmin = false;

			if (ETSSearchCommon.isAdmin(edgeAccessCntrl)) {
				isAdmin = true;

				if (!initialized) {
					initialize();
				}

				if (request.getParameter("showstatus") != null) {
					showETSIndexCacheStatus(request, response);
					return;
				} else if (request.getParameter("loadindex") != null) {
					indexCache.refreshIndices();
					displayMessage(
						response,
						"loaded index on " + ETSSearchCommon.getHostName());
					return;
				}
			}

			String adminRequest =
				request.getParameter("ETSIndexCacheAdminRequest");

			if (!initialized) {
				if (!initialize()) {
					throw new RuntimeException("could not initialize");
				}
			}

			startThreads(false);

			String projectType = request.getParameter("projtype");
			if (projectType == null) {
				projectType = Defines.ETS_WORKSPACE_TYPE;
			}
			UnbrandedProperties unBrandedProp =
				PropertyFactory.getProperty(projectType);

			String linkid = request.getParameter("linkid");
			if (linkid == null
				|| linkid.length() == 0
				|| linkid.equalsIgnoreCase("null")) {
				linkid = unBrandedProp.getLinkID();
			}

			StringBuffer searchLinkURL =
				new StringBuffer(ETSSearchCommon.SEARCH_SERVLET);

			searchLinkURL.append("?linkid=" + linkid);

			boolean debug = DEBUG_FLAG;
			boolean applySearchOperators = false;

			if (request.getParameter("dfon") != null) {
				debug = true;
				searchLinkURL.append("&amp;dfon=y");

				if (request.getParameter("aso") != null) {
					applySearchOperators = true;
					searchLinkURL.append("&amp;aso=y");
				}
			}

			boolean showAllResults = false;
			if (isAdmin) {
				if (request.getParameter("oacvbn") != null) {
					showAllResults = true;
					searchLinkURL.append("&amp;oacvbn=y");
				}
			}

			String projtab = request.getParameter("projtab");
			if (projtab != null) {
				searchLinkURL.append("&amp;projtab=" + projtab);
			}
			String proj = request.getParameter("proj");
			if (proj != null) {
				searchLinkURL.append("&amp;proj=" + proj);
			}
			String tabStr = request.getParameter("tab");
			if (tabStr != null) {
				searchLinkURL.append("&amp;tab=" + tabStr);
			}

			boolean restrictProj = false;
			boolean restrictTab = false;
			if (searchScope != null) {
				if (searchScope.equals("projtab") && projtab != null) {
					restrictProj = true;
					restrictTab = true;
				} else if (searchScope.equals("proj") && proj != null) {
					restrictProj = true;
				} else if (searchScope.equals("tab") && tabStr != null) {
					restrictTab = true;
				} else if (!searchScope.equals("ets")) {
					String logDetails =
						"Invalid URL (invalid searchScope: "
							+ searchScope
							+ ")";
					showErrorAndLog(
						request,
						response,
						logDetails,
						edgeAccessCntrl);
					return;
				}
			} else {
				searchScope = "ets";
			}

			/*
			StringBuffer searchLinkURL =
				new StringBuffer(
					replace(searchFormURL.toString(), '&', "&amp;"));
			*/

			searchLinkURL.append("&amp;searchScope=" + searchScope);

			int viewType = -1;
			String tabType = null;
			String tabName = null;
			if (tabStr != null) {
				try {
					viewType = Integer.parseInt(tabStr);
				} catch (Exception e) {
					log.error(e);
				}
				tabType = ETSSearchCommon.getTabType(viewType);
				tabName = ETSSearchCommon.getTabName(viewType);
				if (tabType == null) {
					String logDetails =
						"Invalid URL (invalid tab: " + tabStr + ")";
					showErrorAndLog(
						request,
						response,
						logDetails,
						edgeAccessCntrl);
					return;
				}
			}

			String projName = null;
			String projUserRole = null;
			if (proj != null) {
				projName = getProjectName(proj);

				if (projName == null) {
					String logDetails =
						"Invalid URL (invalid proj: " + proj + ")";
					showErrorAndLog(
						request,
						response,
						logDetails,
						edgeAccessCntrl);
					return;
				} else {
					projUserRole =
						ETSUtils.checkUserRole(edgeAccessCntrl, proj);

					if (projUserRole == null
						|| projUserRole.equals(Defines.INVALID_USER)) {

						String logDetails =
							"gIR_USERN: "
								+ edgeAccessCntrl.gIR_USERN
								+ " not authorized to project ID: "
								+ proj
								+ " projUserRole: "
								+ projUserRole;
						showErrorAndLog(
							request,
							response,
							"Your ID is not authorized to access this workspace",
							logDetails,
							edgeAccessCntrl);
						return;
					}
				}
			}

			boolean projtabIsCat = false;
			String projtabName = null;
			if (projtab != null) {
				if (projtab.equals("tab")) {
					projtab = tabType;
					projtabName = tabName;
				} else {
					DbConnect db = new DbConnect(this);
					Statement stmt = null;
					ResultSet rs = null;
					try {
						db.makeConn(ETS_DS);
						stmt = db.conn.createStatement();
						rs =
							stmt.executeQuery(
								"select CAT_NAME from ETS.ETS_CAT where CAT_ID = "
									+ projtab
									+ " with ur");
						if (rs.next()) {
							projtabName = rs.getString("CAT_NAME");
							projtabIsCat = true;
						}
					} finally {
						close(rs);
						close(stmt);
						db.closeConn();
					}
				}

				if (projtabName == null) {
					String logDetails =
						"Invalid URL (invalid projtab: " + projtab + ")";
					showErrorAndLog(
						request,
						response,
						logDetails,
						edgeAccessCntrl);
					return;
				}
			}

			String displayQuery = "";
			if (query != null) {
				query = query.trim();
				if (query.length() == 0) {
					query = null;
				} else {
					displayQuery = query;
					query = replace(query, "&quot;", "\"");
					displayQuery = replace(displayQuery, '\"', "&quot;");
					searchLinkURL.append(
						"&amp;q=" + URLEncoder.encode(displayQuery, "UTF-8"));
				}
			}

			if (query == null) {
				/*
				String referer = request.getHeader("Referer");
				if (referer != null) {
					response.sendRedirect(referer);
				} else {
					response.sendRedirect(CONNECT_SERVLET);
				}
				*/

				response.sendRedirect(
					Defines.SERVLET_PATH + unBrandedProp.getLandingPageURL());
				return;
			}

			String[] projects = null;
			String[] tabs = null;
			String searchKey = "";
			String SEARCH_REALM = "";

			if (restrictTab) {
				if (restrictProj) {
					tabs = new String[] { projtab };
					searchKey += projtab;
					SEARCH_REALM += projtabName;
				} else {
					tabs = new String[] { tabType };
					searchKey += tabType;
					SEARCH_REALM += tabName;
				}
			} else {
				searchKey += "null";
				SEARCH_REALM += "All content";
			}
			SEARCH_REALM += " in ";
			if (restrictProj) {
				projects = new String[] { proj };
				searchKey += proj;
				SEARCH_REALM += projName;
			} else {
				searchKey += "null";
				SEARCH_REALM += " all workspaces";
			}
			searchKey += query;

			if (!restrictTab && !restrictProj) {
				SEARCH_REALM = "All my workspaces";
			}

			boolean showCachedResults = false;
			String showPage = request.getParameter("showPage");
			if (showPage != null) {
				showCachedResults = true;
			}

			ArrayList cachedResults = null;
			HttpSession session = request.getSession(true);
			String cachedSearchKey =
				(String) session.getAttribute("ETSSearchKey");

			if (cachedSearchKey != null) {
				cachedSearchKey = cachedSearchKey.trim();
				if (cachedSearchKey.length() == 0) {
					cachedSearchKey = null;
				}
			}

			if (searchKey.equals(cachedSearchKey)) {
				cachedResults =
					(ArrayList) session.getAttribute("ETSSearchResults");
				if (cachedResults != null && cachedResults.size() == 0) {
					cachedResults = null;
				}
			}

			if (cachedResults == null) {
				if (showCachedResults) {
					showCachedResults = false;
					log.warn(
						"No cachedResults. searchKey: "
							+ searchKey
							+ " cachedSearchKey: "
							+ cachedSearchKey);
				}
			}

			boolean logUsage = false;

			/*
			Timestamp TIMESTAMP = new Timestamp(System.currentTimeMillis());
			String USERID = edgeAccessCntrl.gIR_USERN;
			String SEARCH_STRING = query;
			
			String SEARCH_TYPE = "DOCUMENT";
			
			short ENT_CAT_RESULTS = 0;
			short ENT_DOC_RESULTS = 0;
			short ENT_FILE_RESULTS = 0;
			
			String REFERRER_LINK = request.getParameter("referer_link");
			if (REFERRER_LINK == null || REFERRER_LINK.length() == 0)
				REFERRER_LINK = request.getHeader("Referer");
			
			String REFERRER_PAGE = request.getParameter("referer");
			if (REFERRER_PAGE != null)
				searchLinkURL.append(
					"&amp;referer=" + URLEncoder.encode(REFERRER_PAGE));
			*/

			boolean usedCache = false;
			short NUM_ALL_RESULTS = 0;

			String numResultsStr = "";
			StringBuffer resultsBuffer = new StringBuffer();

			String q = null;
			String qPhrase = null;

			int currentPage = 1;
			int numEntResults = 0;

			ArrayList entResults = new ArrayList();
			ArrayList allResults = new ArrayList();
			TreeMap projectRoles = new TreeMap();
			TreeMap projectTypes = new TreeMap();

			if (query != null && query.trim().length() > 0) {

				resultsBuffer = new StringBuffer(8000);

				TreeMap resultsMap = new TreeMap();

				// START execute new query
				if (cachedResults == null) {
					q = query.replace(':', ' ');

					if (query.indexOf('"') < 0) {
						if (query.indexOf(' ') < 0) {
							if (q.indexOf('*') < 0)
								q = "\"" + q + "\"";
						} else {
							StringBuffer qBuf = new StringBuffer();
							StringTokenizer st = new StringTokenizer(query);
							while (st.hasMoreTokens()) {
								String token = st.nextToken();

								if (token.indexOf('*') < 0) {
									qBuf.append('"');
									qBuf.append(token);
									qBuf.append('"');
								} else {
									qBuf.append(token);
								}

								qBuf.append(' ');
							}
							qBuf.deleteCharAt(qBuf.length() - 1);

							q = qBuf.toString().replace(':', ' ');
							qPhrase =
								"\""
									+ query.replace(':', ' ').replace('*', ' ')
									+ "\"";
						}
					}

					TfIdfQuery tq = new TfIdfQuery(q, tabs, projects);

					TfIdfQuery tqPhrase = null;
					if (qPhrase != null)
						tqPhrase = new TfIdfQuery(qPhrase, tabs, projects);

					// tq.setMinusOperator('^');
					// tq.setPlusOperator('`');
					// tq.setLAWeight(lexicalAffinityWeight);
					// tq.collectMatches();

					DocScore[] scores = null;
					DocScore[] scoresPhrase = new DocScore[0];

					int expansionType = TfIdfQuery.NO_EXPAND;

					// Juru's query methods are not thread-safe
					// so we need to synchronize calls to them explicitly
					synchronized (this) {
						scores =
							indexCache.queryIndex(
								tq,
								expansionType,
								applySearchOperators);

						if (tqPhrase != null) {
							scoresPhrase =
								indexCache.queryIndex(
									tqPhrase,
									expansionType,
									applySearchOperators);
						}
					}

					StringBuffer errors = new StringBuffer();

					int i = 0;
					int iPhrase = 0;

					int len = scores.length;
					int lenPhrase = scoresPhrase.length;

					DbConnect db = new DbConnect(this);
					try {
						db.makeConn(ETS_DS);

						while (true) {
							DocScore doc;
							boolean phraseHit = false;

							if (iPhrase < lenPhrase) {
								doc = scoresPhrase[iPhrase++];
								phraseHit = true;
							} else if (i < len) {
								doc = scores[i++];
							} else {
								break;
							}

							String docName = null;
							String id = null;

							try {
								docName = doc.getName();

								id = (String) doc.getDocumentProperty("ID");

								byte type =
									ETSSearchResult.parseType(
										(String) doc.getDocumentProperty(
											"TYPE"));

								String docfileId =
									(String) doc.getDocumentProperty(
										"DOCFILE_ID");

								String cqTrkId =
									(String) doc.getDocumentProperty(
										"CQ_TRK_ID");

								String key = null;
								// the below if statement is not really needed
								// it is a reminder that doc results use an extra key field
								if (docfileId != null) {
									key = getKey(type, id, docfileId);
								} else {
									key = getKey(type, id, null);
								}

								String projectId =
									(String) doc.getDocumentProperty(
										"PROJECT_ID");

								String resProjType = null;
								if (projectTypes.containsKey(projectId)) {
									resProjType =
										(String) projectTypes.get(projectId);
								} else {
									ETSProj resultProj =
										ETSUtils.getProjectDetails(
											db.conn,
											projectId);
									resProjType = resultProj.getProjectType();
									projectTypes.put(projectId, resProjType);
								}

								if (resProjType == null) {
									// invalid project
									continue;
								}

								String projLinkid =
									(String) LINKIDS.get(resProjType);
								if (projLinkid == null) {
									UnbrandedProperties props =
										PropertyFactory.getProperty(
											resProjType);
									projLinkid = props.getLinkID();
									LINKIDS.put(resProjType, projLinkid);
								}

								String userRole =
									(String) projectRoles.get(projectId);
								if (userRole == null) {
									userRole =
										ETSUtils.getUserRole(
											edgeAccessCntrl.gIR_USERN,
											projectId,
											db.conn);
									projectRoles.put(projectId, userRole);
								}

								if (userRole.equals(Defines.INVALID_USER)) {
									continue;
								}

								float score = (float) doc.getScore();

								if (phraseHit)
									score *= 10;

								ETSSearchResult existingResult =
									(ETSSearchResult) resultsMap.get(key);

								if (existingResult == null) {
									ETSSearchResult newResult =
										new ETSSearchResult(
											id,
											type,
											projectId,
											resProjType,
											projLinkid,
											score);

									newResult.docfileId = docfileId;
									newResult.cqTrkId = cqTrkId;

									resultsMap.put(key, newResult);
								} else {
									existingResult.score += score;
								}

							} catch (Throwable t) {
								errors.append(
									docName
										+ " - "
										+ id
										+ " - "
										+ ETSSearchCommon.getShortStackTrace(t)
										+ "\n");
							}
						}
					} finally {
						db.closeConn();
					}

					if (errors.length() > 0) {
						showErrorAndLog(
							request,
							response,
							"System index error",
							"thrown processing results",
							"searchKey: "
								+ searchKey
								+ "\nnumResults: "
								+ resultsMap.size()
								+ "\n\n"
								+ errors,
							null,
							edgeAccessCntrl);

						return;
					}

					NUM_ALL_RESULTS = (short) resultsMap.size();

				} // END if (cachedResults == null)
				else {
					allResults = cachedResults;
					NUM_ALL_RESULTS = (short) allResults.size();
				}

				if (cachedResults != null) {
					usedCache = true;
					entResults = allResults;
				} else if (showAllResults) {
					entResults = new ArrayList(resultsMap.values());
				} else if (NUM_ALL_RESULTS > 0) {
					entResults =
						getEntitledResults(
							resultsMap,
							edgeAccessCntrl,
							projectRoles);
				}

				Collections.sort(
					entResults,
					new ETSSearchResultComparator(
						ETSSearchResultComparator.SORT_BY_RANK));

				numEntResults = entResults.size();

				while (numEntResults > MAX_RESULTS) {
					entResults.remove(--numEntResults);
				}

				/*
				boolean sortedByTitle = false;
				if (numEntResults > 0
					&& ((ETSSearchResult) entResults.get(0)).title != null) {
					Collections.sort(
						entResults,
						new ETSSearchResultComparator(
							ETSSearchResultComparator.SORT_BY_TITLE));
				
					sortedByTitle = true;
				
					for (short i = 0; i < numEntResults; i++) {
						ETSSearchResult res =
							(ETSSearchResult) entResults.get(i);
						res.titleRank = i;
						res.title = null;
					}
				}
				
				if (sortBy != ETSSearchResultComparator.SORT_BY_TITLE
					|| !sortedByTitle) {
					Collections.sort(
						entResults,
						new ETSSearchResultComparator(sortBy));
				}
				*/

				if (numEntResults > 0) {

					int index = 0;

					if (showPage != null && showPage.trim().length() > 0) {
						currentPage = Integer.parseInt(showPage);
						index = (currentPage - 1) * RESULTS_PER_PAGE;
					}

					if (index >= numEntResults)
						index = 0;

					int endIndex = index + RESULTS_PER_PAGE;
					if (endIndex > numEntResults)
						endIndex = numEntResults;

					if ((index + 1) == endIndex)
						numResultsStr =
							"Result " + (index + 1) + " of " + numEntResults;
					else
						numResultsStr =
							"Results "
								+ (index + 1)
								+ " - "
								+ endIndex
								+ " of "
								+ numEntResults;

					if (debug)
						numResultsStr += " - "
							+ usedCache
							+ " - "
							+ q
							+ " - "
							+ qPhrase
							+ " - "
							+ allResults.size();

					resultsBuffer.append(
						getFormattedResults(
							entResults.subList(index, endIndex)));
				}

			}
			// END if query != null

			if (numEntResults == 0) {
				numResultsStr = "";

				resultsBuffer.append(TABLE_TAG);
				resultsBuffer.append("<tr> \n");
				resultsBuffer.append("<td> \n");
				resultsBuffer.append(
					"Your query did not match any searchable content.");
				resultsBuffer.append(
					" Please make sure all words are spelled correctly or try using other keywords.");
				if (restrictProj || restrictTab) {
					resultsBuffer.append(
						" Making the search less restrictive might also help.");
				}
				resultsBuffer.append("<br /> \n");

				if (debug) {
					resultsBuffer.append(usedCache);
					resultsBuffer.append(" - ");
					resultsBuffer.append(q);
					resultsBuffer.append(" - ");
					resultsBuffer.append(qPhrase);
					resultsBuffer.append(" - ");
					resultsBuffer.append(allResults.size());
					resultsBuffer.append("<br /> \n");
				}

				resultsBuffer.append("<br /> \n");
				resultsBuffer.append("</td></tr></table> \n");
			}

			StringBuffer servletOutput = new StringBuffer(32000); // 32 KB

			DbConnect db = new DbConnect(this);
			try {
				db.makeConn(AMT_DS);

				Hashtable params = new Hashtable();
				params.put("linkid", linkid);

				AmtHeaderFooter amtHF =
					new AmtHeaderFooter(edgeAccessCntrl, db.conn, params);

				String multiplePageLinks =
					getMultiplePageLinks(
						searchLinkURL.toString(),
						currentPage,
						numEntResults)
						.toString();

				amtHF.setPageTitle(
					unBrandedProp.getAppName() + " - Search results");
				amtHF.setHeader(unBrandedProp.getAppName());
				amtHF.setSubHeader("Search results");

				ETSProj etsProj = null;
				if (proj != null) {
					etsProj =
						ETSDatabaseManager.getProjectDetails(db.conn, proj);
				}

				if (projtabIsCat) {
					servletOutput.append(
						ETSSearchCommon.getMasthead(
							amtHF,
							etsProj,
							viewType,
							linkid,
							projtab,
							projtabName,
							restrictTab,
							restrictProj,
							displayQuery,
							projectType));
				} else {
					servletOutput.append(
						ETSSearchCommon.getMasthead(
							amtHF,
							etsProj,
							viewType,
							linkid,
							null,
							null,
							restrictTab,
							restrictProj,
							displayQuery,
							projectType));
				}

				servletOutput.append(amtHF.printBullsEyeLeftNav());
				servletOutput.append(amtHF.printSubHeader());

				servletOutput.append(TABLE_TAG);
				servletOutput.append("<tr> \n");

				servletOutput.append("<td width=\"443\" valign=\"top\"> \n");
				servletOutput.append(
					getSearchHeader(
						displayQuery,
						SEARCH_REALM,
						numEntResults,
						numResultsStr,
						multiplePageLinks));

				servletOutput.append(resultsBuffer);

				servletOutput.append(getSearchFooter(multiplePageLinks));

				servletOutput.append(BACK_BUTTON);
				servletOutput.append("</td> \n");

				servletOutput.append("<td width=\"7\">&nbsp;</td> \n");

				servletOutput.append("<td width=\"150\" valign=\"top\"> \n");
				if (request.isSecure()) {
					servletOutput.append(SECURE_CONTENT_TAG);
				}
				if (proj != null) {
					servletOutput.append(
						new ETSContact(proj, request).getContactBox());
				} else {
					// servletOutput.append(getGenericRightColumn());
				}
				servletOutput.append("</td> \n");

				servletOutput.append("</tr> \n");
				servletOutput.append("</table> \n");

				servletOutput.append(amtHF.printBullsEyeFooter());

				response.setContentType("text/html");

				PrintWriter out =
					ETSSearchCommon.getGZIPWriter(request, response);

				out.println(servletOutput.toString());
				out.close();

				if (entResults.size() > 0 && cachedResults == null) {
					session.setAttribute("ETSSearchResults", entResults);
					session.setAttribute("ETSSearchKey", searchKey);
				}

			} finally {
				db.closeConn();
			}

			/*
			if (logUsage && !debug) {
				logSearchUsage(
					TIMESTAMP,
					USERID,
					SEARCH_STRING,
					SEARCH_REALM,
					SEARCH_TYPE,
					NUM_ALL_RESULTS,
					ENT_CAT_RESULTS,
					ENT_DOC_RESULTS,
					ENT_FILE_RESULTS,
					REFERRER_LINK,
					REFERRER_PAGE);
			}
			*/

		} catch (Throwable t) {
			showErrorAndLog(
				request,
				response,
				"System error",
				t,
				edgeAccessCntrl);
		}

	}

	private boolean verifyIBMAuth(
		ETSSearchResult result,
		EdgeAccessCntrl edgeAccessCntrl,
		String userRole) {

		if (result.ibmOnly == Defines.ETS_PUBLIC
			|| result.ibmOnly == Defines.NOT_SET_FLAG) {

			return true;
		} else if (
			result.ibmOnly == Defines.ETS_IBM_ONLY
				|| result.ibmOnly == Defines.ETS_IBM_CONF) {

			if (edgeAccessCntrl.gDECAFTYPE.equals("I")
				&& !userRole.equals(Defines.WORKSPACE_CLIENT)) {

				return true;
			}
		} else {
			logError(
				"Warning in verifyIBMAuth",
				"Unknown value: "
					+ result.ibmOnly
					+ " for ETSSearchResult.ibmOnly");
		}

		return false;
	}

	private boolean isAuthorizedUser(int docId, String userId)
		throws SQLException {
		DbConnect db = new DbConnect(this);
		Statement stmt = null;
		ResultSet rs = null;
		try {
			db.makeConn(ETS_DS);
			stmt = db.conn.createStatement();

			rs =
				stmt.executeQuery(
					"select USER_ID from ETS.ETS_PRIVATE_DOC"
						+ " where DOC_ID = "
						+ docId
						+ " with ur");

			while (rs.next()) {
				if (userId.equals(rs.getString(1))) {
					return true;
				}
			}
		} finally {
			close(rs);
			close(stmt);
			db.closeConn();
		}

		return false;
	}

	private boolean isTrue(String str) {
		return str != null && str.charAt(0) == ETSDatabaseManager.TRUE_FLAG;
	}

	private String getKey(byte type, String id1, String id2) {
		if (id2 != null) {
			return type + "-" + id1 + "-" + id2;
		} else {
			return type + "-" + id1 + "-";
		}
	}

	private ArrayList getEntitledResults(
		Map allResults,
		EdgeAccessCntrl edgeAccessCntrl,
		TreeMap projectRoles)
		throws SQLException {

		ArrayList entResults = new ArrayList();

		TreeMap docResults = new TreeMap();
		TreeMap catResults = new TreeMap();
		TreeMap issueResults = new TreeMap();
		TreeMap changeReqResults = new TreeMap();
		TreeMap calendarResults = new TreeMap();
		TreeMap setMetResults = new TreeMap();
		TreeMap selfAssessmentResults = new TreeMap();

		Iterator iter = allResults.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			ETSSearchResult result = (ETSSearchResult) entry.getValue();

			switch (result.type) {
				case ETSSearchResult.TYPE_DOC_NUM :
				case ETSSearchResult.TYPE_PROJECT_PLAN_NUM :
					docResults.put(entry.getKey(), result);
					break;

				case ETSSearchResult.TYPE_CAT_NUM :
					catResults.put(entry.getKey(), result);
					break;

				case ETSSearchResult.TYPE_ISSUE_NUM :
					issueResults.put(entry.getKey(), result);
					break;

				case ETSSearchResult.TYPE_CHANGE_REQ_NUM :
					changeReqResults.put(entry.getKey(), result);
					break;

				case ETSSearchResult.TYPE_ALERT_NUM :
				case ETSSearchResult.TYPE_EVENT_NUM :
				case ETSSearchResult.TYPE_MEETING_NUM :
					calendarResults.put(entry.getKey(), result);
					break;

				case ETSSearchResult.TYPE_SELF_ASSESSMENT_NUM :
					selfAssessmentResults.put(entry.getKey(), result);
					break;

				case ETSSearchResult.TYPE_SETMET_NUM :
					setMetResults.put(entry.getKey(), result);
					break;

				default :
					throw new RuntimeException(
						"Unknown result type: " + result.type);
			}
		}

		if (!docResults.isEmpty()) {
			getEntitledDocResults(
				docResults,
				entResults,
				edgeAccessCntrl,
				projectRoles);
		}

		if (!catResults.isEmpty()) {
			getEntitledCatResults(
				catResults,
				entResults,
				edgeAccessCntrl,
				projectRoles);
		}

		if (!issueResults.isEmpty()) {
			getEntitledIssueResults(
				issueResults,
				entResults,
				edgeAccessCntrl,
				projectRoles);
		}

		if (!changeReqResults.isEmpty()) {
			getEntitledChangeReqResults(
				changeReqResults,
				entResults,
				edgeAccessCntrl,
				projectRoles);
		}

		if (!calendarResults.isEmpty()) {
			getEntitledCalendarResults(
				calendarResults,
				entResults,
				edgeAccessCntrl,
				projectRoles);
		}

		if (!setMetResults.isEmpty()) {
			getEntitledSetMetResults(
				setMetResults,
				entResults,
				edgeAccessCntrl,
				projectRoles);
		}

		if (!selfAssessmentResults.isEmpty()) {
			getEntitledSelfAssessmentResults(
				selfAssessmentResults,
				entResults,
				edgeAccessCntrl,
				projectRoles);
		}

		return entResults;
	}

	private void getEntitledDocResults(
		Map docResults,
		ArrayList entResults,
		EdgeAccessCntrl edgeAccessCntrl,
		TreeMap projectRoles)
		throws SQLException {

		DbConnect db = new DbConnect(this);
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String sql =
				"select USER_ID, DOC_TYPE, IBM_ONLY, EXPIRY_DATE, ISPRIVATE"
					+ " from ETS.ETS_DOC"
					+ " where DOC_ID = ?"
					+ " and DELETE_FLAG != '"
					+ ETSDatabaseManager.TRUE_FLAG
					+ "' and LATEST_VERSION = '"
					+ ETSDatabaseManager.TRUE_FLAG
					+ "' with ur";

			db.makeConn(ETS_DS);
			pstmt = db.conn.prepareStatement(sql);

			long currentTime = System.currentTimeMillis();

			Iterator iter = docResults.values().iterator();
			while (iter.hasNext()) {
				ETSSearchResult result = (ETSSearchResult) iter.next();
				int docId = Integer.parseInt(result.id);

				pstmt.setInt(1, docId);
				rs = pstmt.executeQuery();

				if (!rs.next()) {
					rs.close();
					continue;
				}

				String docOwner = rs.getString("USER_ID");
				int docType = rs.getInt("DOC_TYPE");
				String ibmOnlyStr = rs.getString("IBM_ONLY");
				Timestamp expiryDate = rs.getTimestamp("EXPIRY_DATE");
				boolean isPrivate = isTrue(rs.getString("ISPRIVATE"));

				rs.close();

				/*
				byte type;
				if (docType == Defines.DOC) {
					type = ETSSearchResult.TYPE_DOC_NUM;
				} else if (docType == Defines.PROJECT_PLAN) {
					type = ETSSearchResult.TYPE_PROJECT_PLAN_NUM;
				} else {
					throw new RuntimeException(
						"Unexpected DOC_TYPE: "
							+ docType
							+ " for DOC_ID: "
							+ docId);
				}
				
				String key = getKey(type, String.valueOf(docId));
				ETSSearchResult result = (ETSSearchResult) docResults.get(key);
				
				if (result == null) {
					throw new RuntimeException(
						"No match in docResults for key: " + key);
				}
				*/

				String userRole = (String) projectRoles.get(result.projectId);

				boolean isDocOwner = docOwner.equals(edgeAccessCntrl.gIR_USERN);
				boolean isWorkspaceOwner =
					userRole.equals(Defines.WORKSPACE_OWNER);
				boolean isAdmin = userRole.equals(Defines.ETS_ADMIN);
				boolean isExecutive = userRole.equals(Defines.ETS_EXECUTIVE);

				boolean hasExpired =
					expiryDate != null && expiryDate.getTime() < currentTime;

				result.hasExpired = hasExpired;
				result.isPrivate = isPrivate;
				if (ibmOnlyStr != null) {
					result.ibmOnly = ibmOnlyStr.charAt(0);
				}

				if (!verifyIBMAuth(result, edgeAccessCntrl, userRole)) {
					continue;
				} else if (
					hasExpired
						&& !isDocOwner
						&& !isWorkspaceOwner
						&& !isAdmin) {

					continue;
				} else if (
					!isPrivate
						|| isDocOwner
						|| isWorkspaceOwner
						|| isAdmin
						|| isExecutive
						|| isAuthorizedUser(docId, edgeAccessCntrl.gIR_USERN)) {

					entResults.add(result);
				}
			}
		} finally {
			close(rs);
			close(pstmt);
			db.closeConn();
		}
	}

	private void getEntitledCatResults(
		Map catResults,
		ArrayList entResults,
		EdgeAccessCntrl edgeAccessCntrl,
		TreeMap projectRoles)
		throws SQLException {

		DbConnect db = new DbConnect(this);
		Statement stmt = null;
		ResultSet rs = null;

		try {
			db.makeConn(ETS_DS);
			stmt = db.conn.createStatement();

			rs =
				stmt.executeQuery(
					"select CAT_ID, USER_ID, PRIVS, IBM_ONLY"
						+ " from ETS.ETS_CAT"
						+ " where CAT_ID in "
						+ getIntegerIdList(
							catResults.values(),
							ETSSearchResult.TYPE_CAT_NUM)
						+ " and (PRIVS is null or length(PRIVS) = 0)"
						+ " with ur");

			long currentTime = System.currentTimeMillis();

			while (rs.next()) {
				int id = rs.getInt("CAT_ID");
				String owner = rs.getString("USER_ID");
				String privs = rs.getString("PRIVS");
				String ibmOnlyStr = rs.getString("IBM_ONLY");

				String key =
					getKey(
						ETSSearchResult.TYPE_CAT_NUM,
						String.valueOf(id),
						null);

				ETSSearchResult result = (ETSSearchResult) catResults.get(key);

				if (result == null) {
					throw new RuntimeException(
						"No match in catResults for key: " + key);
				}

				String userRole = (String) projectRoles.get(result.projectId);

				if (ibmOnlyStr != null) {
					result.ibmOnly = ibmOnlyStr.charAt(0);
				}

				if (!verifyIBMAuth(result, edgeAccessCntrl, userRole)) {
					continue;
				} else {
					entResults.add(result);
				}
			}
		} finally {
			close(rs);
			close(stmt);
			db.closeConn();
		}
	}

	private void getEntitledIssueResults(
		Map issueResults,
		ArrayList entResults,
		EdgeAccessCntrl edgeAccessCntrl,
		TreeMap projectRoles)
		throws SQLException {

		DbConnect db = new DbConnect(this);
		Statement stmt = null;
		ResultSet rs = null;

		try {
			db.makeConn(ETS_DS);
			stmt = db.conn.createStatement();

			rs =
				stmt.executeQuery(
					"select EDGE_PROBLEM_ID, ISSUE_ACCESS"
						+ " from ETS.PROBLEM_INFO_USR1"
						+ " where EDGE_PROBLEM_ID in "
						+ getStringIdList(
							issueResults.values(),
							ETSSearchResult.TYPE_ISSUE_NUM)
						+ " with ur");

			while (rs.next()) {
				String edgeProblemId = rs.getString("EDGE_PROBLEM_ID");
				String issueAccess = rs.getString("ISSUE_ACCESS");

				String key =
					getKey(ETSSearchResult.TYPE_ISSUE_NUM, edgeProblemId, null);

				ETSSearchResult result =
					(ETSSearchResult) issueResults.get(key);

				if (result == null) {
					throw new RuntimeException(
						"No match in issueResults for key: " + key);
				}

				String userRole = (String) projectRoles.get(result.projectId);

				if (issueAccess != null && issueAccess.startsWith("IBM")) {
					result.ibmOnly = Defines.ETS_IBM_ONLY;
				}

				if (!verifyIBMAuth(result, edgeAccessCntrl, userRole)) {
					continue;
				} else {
					entResults.add(result);
				}
			}
		} finally {
			close(rs);
			close(stmt);
			db.closeConn();
		}
	}

	private void getEntitledChangeReqResults(
		Map changeReqResults,
		ArrayList entResults,
		EdgeAccessCntrl edgeAccessCntrl,
		TreeMap projectRoles)
		throws SQLException {

		DbConnect db = new DbConnect(this);
		Statement stmt = null;
		ResultSet rs = null;

		try {
			db.makeConn(ETS_DS);
			stmt = db.conn.createStatement();

			rs =
				stmt.executeQuery(
					"select ETS_ID, ISSUE_ACCESS"
						+ " from ETS.PMO_ISSUE_INFO"
						+ " where ETS_ID in "
						+ getStringIdList(
							changeReqResults.values(),
							ETSSearchResult.TYPE_CHANGE_REQ_NUM)
						+ " with ur");

			while (rs.next()) {
				String etsId = rs.getString("ETS_ID");
				String issueAccess = rs.getString("ISSUE_ACCESS");

				String key =
					getKey(ETSSearchResult.TYPE_CHANGE_REQ_NUM, etsId, null);

				ETSSearchResult result =
					(ETSSearchResult) changeReqResults.get(key);

				if (result == null) {
					throw new RuntimeException(
						"No match in changeReqResults for key: " + key);
				}

				String userRole = (String) projectRoles.get(result.projectId);

				if (issueAccess != null && issueAccess.startsWith("IBM")) {
					result.ibmOnly = Defines.ETS_IBM_ONLY;
				}

				if (!verifyIBMAuth(result, edgeAccessCntrl, userRole)) {
					continue;
				} else {
					entResults.add(result);
				}
			}
		} finally {
			close(rs);
			close(stmt);
			db.closeConn();
		}
	}

	private void getEntitledSetMetResults(
		Map setMetResults,
		ArrayList entResults,
		EdgeAccessCntrl edgeAccessCntrl,
		TreeMap projectRoles)
		throws SQLException {

		DbConnect db = new DbConnect(this);
		Statement stmt = null;
		ResultSet rs = null;

		try {
			db.makeConn(ETS_DS);
			stmt = db.conn.createStatement();

			rs =
				stmt.executeQuery(
					"select QBR_ID"
						+ " from ETS.ETS_QBR_MAIN"
						+ " where QBR_ID in "
						+ getStringIdList(
							setMetResults.values(),
							ETSSearchResult.TYPE_SETMET_NUM)
						+ " with ur");

			while (rs.next()) {
				String qbrId = rs.getString("QBR_ID");

				String key =
					getKey(ETSSearchResult.TYPE_SETMET_NUM, qbrId, null);

				ETSSearchResult result =
					(ETSSearchResult) setMetResults.get(key);

				if (result == null) {
					throw new RuntimeException(
						"No match in setMetResults for key: " + key);
				}

				entResults.add(result);
			}
		} finally {
			close(rs);
			close(stmt);
			db.closeConn();
		}
	}

	private void getEntitledSelfAssessmentResults(
		Map selfAssessmentResults,
		ArrayList entResults,
		EdgeAccessCntrl edgeAccessCntrl,
		TreeMap projectRoles)
		throws SQLException {

		DbConnect db = new DbConnect(this);
		Statement stmt = null;
		ResultSet rs = null;

		try {
			db.makeConn(ETS_DS);
			stmt = db.conn.createStatement();

			rs =
				stmt.executeQuery(
					"select distinct SELF_ID"
						+ " from ETS.ETS_SELF_PLAN"
						+ " where SELF_ID in "
						+ getStringIdList(
							selfAssessmentResults.values(),
							ETSSearchResult.TYPE_SELF_ASSESSMENT_NUM)
						+ " and STEP != '"
						+ ETSSelfConstants.SELF_STEP_MEMBER_ASSESSMENT
						+ "' with ur");

			while (rs.next()) {
				String selfId = rs.getString("SELF_ID");

				String key =
					getKey(
						ETSSearchResult.TYPE_SELF_ASSESSMENT_NUM,
						selfId,
						null);

				ETSSearchResult result =
					(ETSSearchResult) selfAssessmentResults.get(key);

				if (result == null) {
					throw new RuntimeException(
						"No match in selfAssessmentResults for key: " + key);
				}

				String userRole = (String) projectRoles.get(result.projectId);
				boolean isClient = userRole.equals(Defines.WORKSPACE_CLIENT);

				if (isClient) {
					continue;
				} else {
					entResults.add(result);
				}
			}
		} finally {
			close(rs);
			close(stmt);
			db.closeConn();
		}
	}

	private void getEntitledCalendarResults(
		Map calendarResults,
		ArrayList entResults,
		EdgeAccessCntrl edgeAccessCntrl,
		TreeMap projectRoles)
		throws SQLException {

		DbConnect db = new DbConnect(this);
		Statement stmt = null;
		ResultSet rs = null;

		try {
			db.makeConn(ETS_DS);
			stmt = db.conn.createStatement();

			rs =
				stmt.executeQuery(
					"select CALENDAR_ID, CALENDAR_TYPE, SCHEDULED_BY, INVITEES_ID, IBM_ONLY"
						+ " from ETS.ETS_CALENDAR"
						+ " where CALENDAR_ID in "
						+ getStringIdList(calendarResults.values(), (byte) - 1)
						+ " with ur");

			String irUserStr = ',' + edgeAccessCntrl.gIR_USERN + ',';

			while (rs.next()) {
				String calendarId = rs.getString("CALENDAR_ID");
				String calendarType = rs.getString("CALENDAR_TYPE");
				String scheduledBy = rs.getString("SCHEDULED_BY");
				String inviteesId = rs.getString("INVITEES_ID");
				String ibmOnlyStr = rs.getString("IBM_ONLY");

				byte type;

				switch (calendarType.charAt(0)) {
					case 'M' :
						type = ETSSearchResult.TYPE_MEETING_NUM;
						break;

					case 'E' :
						type = ETSSearchResult.TYPE_EVENT_NUM;
						break;

					case 'A' :
						type = ETSSearchResult.TYPE_ALERT_NUM;
						break;

					default :
						throw new RuntimeException(
							"Unknown calendar type: "
								+ calendarType
								+ " for id: "
								+ calendarId);
				}

				String key = getKey(type, calendarId, null);

				ETSSearchResult result =
					(ETSSearchResult) calendarResults.get(key);

				if (result == null) {
					throw new RuntimeException(
						"No match in calendarResults for key: " + key);
				}

				String userRole = (String) projectRoles.get(result.projectId);
				boolean isWorkspaceOwner =
					userRole.equals(Defines.WORKSPACE_OWNER);
				boolean isAdmin = userRole.equals(Defines.ETS_ADMIN);
				boolean isExecutive = userRole.equals(Defines.ETS_EXECUTIVE);

				if (inviteesId != null) {
					inviteesId = inviteesId.trim();
					if (inviteesId.length() > 0 && !inviteesId.equals("ALL")) {
						result.isPrivate = true;
						inviteesId = ',' + inviteesId + ',';
					}
				}

				if (ibmOnlyStr != null && ibmOnlyStr.equalsIgnoreCase("Y")) {
					result.ibmOnly = Defines.ETS_IBM_ONLY;
				}

				if (!verifyIBMAuth(result, edgeAccessCntrl, userRole)) {
					continue;
				} else if (
					!result.isPrivate
						|| isWorkspaceOwner
						|| isAdmin
						|| isExecutive
						|| scheduledBy.equals(edgeAccessCntrl.gIR_USERN)
						|| inviteesId.indexOf(irUserStr) >= 0) {

					entResults.add(result);
				}
			}
		} finally {
			close(rs);
			close(stmt);
			db.closeConn();
		}
	}

	private void getDocumentDetails(List entResults) throws SQLException {

		DbConnect db1 = new DbConnect(this);
		DbConnect db2 = new DbConnect(this);
		DbConnect db3 = new DbConnect(this);
		PreparedStatement catParentPstmt = null;
		PreparedStatement docPstmt = null;
		PreparedStatement filePstmt = null;
		ResultSet rs = null;

		try {
			db1.makeConn(ETS_DS);
			db2.makeConn(ETS_DS);
			db3.makeConn(ETS_DS);

			TreeMap ancestorsCache = new TreeMap();

			catParentPstmt =
				db1.conn.prepareStatement(
					"select CAT_NAME, PARENT_ID from ETS.ETS_CAT where CAT_ID = ? with ur");

			docPstmt =
				db2.conn.prepareStatement(
					"select CAT_ID, USER_ID, DOC_NAME, DOC_UPDATE_DATE"
						+ " from ETS.ETS_DOC"
						+ " where DOC_ID = ?"
						+ " with ur");

			filePstmt =
				db3.conn.prepareStatement(
					"select d.CAT_ID, d.USER_ID, d.DOC_NAME,"
						+ " f.DOCFILE_NAME, f.DOCFILE_SIZE, f.DOCFILE_UPDATE_DATE"
						+ " from ETS.ETS_DOC d, ETS.ETS_DOCFILE f"
						+ " where f.DOC_ID = ? and f.DOCFILE_ID = ?"
						+ " and f.DOC_ID = d.DOC_ID"
						+ " with ur");
			
			Iterator iter = entResults.iterator();
			while (iter.hasNext()) {
				ETSSearchResult result = (ETSSearchResult) iter.next();

				if (result.type != ETSSearchResult.TYPE_DOC_NUM
					&& result.type != ETSSearchResult.TYPE_PROJECT_PLAN_NUM) {

					continue;
				}

				int catId = 0;
				String userId = null;
				String docName = null;
				Timestamp updateDate = null;
				String fileLink = null;

				if(result.docfileId != null) {
					filePstmt.setInt(1, Integer.parseInt(result.id));
					filePstmt.setInt(2, Integer.parseInt(result.docfileId));
					rs = filePstmt.executeQuery();
					rs.next();
					
					catId = rs.getInt("CAT_ID");
					userId = rs.getString("USER_ID");
					docName = rs.getString("DOC_NAME");
					String fileName = rs.getString("DOCFILE_NAME");
					int fileSize = rs.getInt("DOCFILE_SIZE");
					updateDate =
						rs.getTimestamp("DOCFILE_UPDATE_DATE");

					rs.close();
					rs = null;

					result.title = fileName;
					result.description =
						docName
							+ " - "
							+ ETSSearchCommon.formatFileSize(fileSize)
							+ " - "
							+ ETSSearchCommon.formatDate(updateDate);
					
					fileLink = 
						FILE_SERVLET
						+ "/"
						+ fileName
						+ "?projid="
						+ result.projectId
						+ "&amp;docid="
						+ result.id
						+ "&amp;docfileid="
						+ result.docfileId
						+ "&amp;linkid="
						+ result.linkid;
				} else {
					docPstmt.setInt(1, Integer.parseInt(result.id));
					rs = docPstmt.executeQuery();
					rs.next();
					
					catId = rs.getInt("CAT_ID");
					userId = rs.getString("USER_ID");
					docName = rs.getString("DOC_NAME");
					updateDate =
						rs.getTimestamp("DOC_UPDATE_DATE");

					rs.close();
					rs = null;

					result.title = docName;
					result.description = ETSSearchCommon.formatDate(updateDate);
				}

				result.irUserId = userId;
				result.userIdRole = "Author";

				if (result.type == ETSSearchResult.TYPE_PROJECT_PLAN_NUM) {
					result.location = "Project plan";
					result.link = fileLink;
				} else {
					ArrayList ancestors =
						getAncestors(
							new Integer(catId),
							ancestorsCache,
							catParentPstmt);
					StringBuffer location = new StringBuffer(32);
					for (int i = ancestors.size() - 1; i >= 0; i--) {
						String[] cat = (String[]) ancestors.get(i);
						String name = cat[1];
						location.append(name);
						location.append(HTML_SEPARATOR);
					}
					if (location.length() >= HTML_SEPARATOR.length()) {
						location.delete(
							location.length() - HTML_SEPARATOR.length(),
							location.length());
					}
					result.location = location.toString();
					
					String docLink =
						DOC_SERVLET
						+ "?proj="
						+ result.projectId
						+ "&amp;docid="
						+ result.id
						+ "&amp;tc="
						+ result.topCatId
						+ "&amp;cc="
						+ catId
						+ "&amp;linkid="
						+ result.linkid;
					
					if(fileLink != null) {
						result.link = fileLink;
						result.altTitle = "Details";
						result.altLink = docLink;
					} else {
						result.link = docLink;
					}
				}
			}
		} finally {
			close(rs);
			close(docPstmt);
			close(filePstmt);
			close(catParentPstmt);
			db1.closeConn();
			db2.closeConn();
			db3.closeConn();
		}
	}

	private void getCategoryDetails(List entResults) throws SQLException {

		DbConnect db1 = new DbConnect(this);
		DbConnect db2 = new DbConnect(this);
		PreparedStatement catParentPstmt = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			db1.makeConn(ETS_DS);
			db2.makeConn(ETS_DS);

			TreeMap ancestorsCache = new TreeMap();

			catParentPstmt =
				db1.conn.prepareStatement(
					"select CAT_NAME, PARENT_ID from ETS.ETS_CAT where CAT_ID = ? with ur");

			pstmt =
				db2.conn.prepareStatement(
					"select USER_ID, CAT_NAME, PARENT_ID, LAST_TIMESTAMP"
						+ " from ETS.ETS_CAT"
						+ " where CAT_ID = ?"
						+ " with ur");

			Iterator iter = entResults.iterator();
			while (iter.hasNext()) {
				ETSSearchResult result = (ETSSearchResult) iter.next();

				if (result.type != ETSSearchResult.TYPE_CAT_NUM) {
					continue;
				}

				pstmt.setInt(1, Integer.parseInt(result.id));
				rs = pstmt.executeQuery();
				rs.next();

				String userId = rs.getString("USER_ID");
				String catName = rs.getString("CAT_NAME");
				int parentId = rs.getInt("PARENT_ID");
				Timestamp lastTimestamp = rs.getTimestamp("LAST_TIMESTAMP");

				rs.close();
				rs = null;

				result.irUserId = userId;
				result.userIdRole = "Author";
				result.description = ETSSearchCommon.formatDate(lastTimestamp);
				result.title = catName;

				result.link =
					CAT_SERVLET
						+ "?proj="
						+ result.projectId
						+ "&amp;tc="
						+ result.topCatId
						+ "&amp;cc="
						+ result.id
						+ "&amp;linkid="
						+ result.linkid;

				ArrayList ancestors =
					getAncestors(
						new Integer(parentId),
						ancestorsCache,
						catParentPstmt);
				StringBuffer location = new StringBuffer(32);
				for (int i = ancestors.size() - 1; i >= 0; i--) {
					String[] cat = (String[]) ancestors.get(i);
					String name = cat[1];
					location.append(name);
					location.append(HTML_SEPARATOR);
				}
				if (location.length() >= HTML_SEPARATOR.length()) {
					location.delete(
						location.length() - HTML_SEPARATOR.length(),
						location.length());
				}
				result.location = location.toString();
			}
		} finally {
			close(rs);
			close(pstmt);
			close(catParentPstmt);
			db1.closeConn();
			db2.closeConn();
		}
	}

	private void getIssueDetails(List entResults) throws SQLException {

		DbConnect db1 = new DbConnect(this);
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			db1.makeConn(ETS_DS);

			pstmt =
				db1.conn.prepareStatement(
					"select cq1.PROBLEM_STATE, u1.PROBLEM_CREATOR, u1.PROBLEM_CLASS, u1.TITLE, u1.SEVERITY, u1.PROBLEM_TYPE"
						+ " from ETS.PROBLEM_INFO_CQ1 cq1, ETS.PROBLEM_INFO_USR1 u1"
						+ " where cq1.EDGE_PROBLEM_ID = ?"
						+ " and cq1.CQ_TRK_ID = ?"
						+ " and cq1.EDGE_PROBLEM_ID = u1.EDGE_PROBLEM_ID"
						+ " with ur");

			Iterator iter = entResults.iterator();
			while (iter.hasNext()) {
				ETSSearchResult result = (ETSSearchResult) iter.next();
				if (result.type != ETSSearchResult.TYPE_ISSUE_NUM) {
					continue;
				}

				pstmt.setString(1, result.id);
				pstmt.setString(2, result.cqTrkId);
				rs = pstmt.executeQuery();
				rs.next();

				String state = rs.getString("PROBLEM_STATE");
				String creatorId = rs.getString("PROBLEM_CREATOR");
				String problemClass = rs.getString("PROBLEM_CLASS");
				String title = rs.getString("TITLE");
				String severity = rs.getString("SEVERITY");
				String problemType = rs.getString("PROBLEM_TYPE");

				rs.close();
				rs = null;

				result.irUserId = creatorId;
				result.userIdRole = "Submitter";
				result.location = "Issues/changes > " + problemClass;
				result.title = title;

				result.link =
					PROJECTS_SERVLET
						+ "?proj="
						+ result.projectId
						+ "&amp;edge_problem_id="
						+ result.id
						+ "&amp;tc="
						+ result.topCatId
						+ "&amp;linkid="
						+ result.linkid;

				if (problemClass != null) {
					problemClass = problemClass.trim();

					if (problemClass.equalsIgnoreCase("Defect")) {
						result.location = "Issues/changes > Issues";

						result.description =
							"Issue type:&nbsp;"
								+ problemType
								+ " &nbsp; Severity:&nbsp;"
								+ severity
								+ " &nbsp; Status:&nbsp;"
								+ state;

						result.link += "&amp;actionType=viewIssue"
							+ "&amp;istyp=iss"
							+ "&amp;op=60"
							+ "&amp;flop=20";
					} else if (problemClass.equalsIgnoreCase("Feedback")) {
						result.location = "Issues/changes > Feedback";

						result.link += "&amp;actionType=feedback"
							+ "&amp;subactionType=viewfeedback"
							+ "&amp;from=allfeedback";
					}
				}
			}
		} finally {
			close(rs);
			close(pstmt);
			db1.closeConn();
		}
	}

	private void getChangeRequestDetails(List entResults) throws SQLException {

		DbConnect db1 = new DbConnect(this);
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			db1.makeConn(ETS_DS);

			pstmt =
				db1.conn.prepareStatement(
					"select STATE_ACTION, SUBMITTER_IR_ID, TITLE, SEVERITY"
						+ " from ETS.PMO_ISSUE_INFO"
						+ " where ETS_ID = ?"
						+ " with ur");

			Iterator iter = entResults.iterator();
			while (iter.hasNext()) {
				ETSSearchResult result = (ETSSearchResult) iter.next();
				if (result.type != ETSSearchResult.TYPE_CHANGE_REQ_NUM) {
					continue;
				}

				pstmt.setString(1, result.id);
				rs = pstmt.executeQuery();
				rs.next();

				String state = rs.getString("STATE_ACTION");
				String creatorId = rs.getString("SUBMITTER_IR_ID");
				String title = rs.getString("TITLE");
				String severity = rs.getString("SEVERITY");

				rs.close();
				rs = null;

				result.irUserId = creatorId;
				result.userIdRole = "Submitter";
				result.location = "Issues/changes > Change requests";
				result.title = title;

				result.link =
					PROJECTS_SERVLET
						+ "?proj="
						+ result.projectId
						+ "&amp;etsId="
						+ result.id
						+ "&amp;actionType=viewChange"
						+ "&amp;istyp=chg"
						+ "&amp;tc="
						+ result.topCatId
						+ "&amp;linkid="
						+ result.linkid
						+ "&amp;op=60"
						+ "&amp;flop=20";

				result.description =
					"Priority:&nbsp;"
						+ severity
						+ " &nbsp; &nbsp; Status:&nbsp;"
						+ state;
			}
		} finally {
			close(rs);
			close(pstmt);
			db1.closeConn();
		}
	}

	private void getCalendarDetails(List entResults) throws SQLException {

		DbConnect db1 = new DbConnect(this);
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			db1.makeConn(ETS_DS);

			pstmt =
				db1.conn.prepareStatement(
					"select SCHEDULED_BY, START_TIME, SUBJECT"
						+ " from ETS.ETS_CALENDAR"
						+ " where CALENDAR_ID = ?"
						+ " with ur");

			Iterator iter = entResults.iterator();
			while (iter.hasNext()) {
				ETSSearchResult result = (ETSSearchResult) iter.next();

				if (result.type == ETSSearchResult.TYPE_ALERT_NUM) {
					result.location = "Messages";
					result.userIdRole = "By";
					result.link =
						CALENDAR_SERVLET
							+ "?caltype=A"
							+ "&amp;proj="
							+ result.projectId
							+ "&amp;calid="
							+ result.id;
					result.isPopup = true;
				} else if (result.type == ETSSearchResult.TYPE_EVENT_NUM) {
					result.location = "Events";
					result.userIdRole = "By";
					result.link =
						CALENDAR_SERVLET
							+ "?caltype=E"
							+ "&amp;proj="
							+ result.projectId
							+ "&amp;calid="
							+ result.id;
					result.isPopup = true;
				} else if (result.type == ETSSearchResult.TYPE_MEETING_NUM) {
					result.location = "Meetings";
					result.userIdRole = "Organized by";
					result.link =
						PROJECTS_SERVLET
							+ "?etsop=viewmeeting"
							+ "&amp;proj="
							+ result.projectId
							+ "&amp;meetid="
							+ result.id
							+ "&amp;tc="
							+ result.topCatId
							+ "&amp;linkid="
							+ result.linkid;
				} else {
					continue;
				}

				pstmt.setString(1, result.id);
				rs = pstmt.executeQuery();
				rs.next();

				String scheduledBy = rs.getString("SCHEDULED_BY");
				Timestamp startTime = rs.getTimestamp("START_TIME");
				String subject = rs.getString("SUBJECT");

				rs.close();
				rs = null;

				result.title = subject;
				result.irUserId = scheduledBy;

				result.description = ETSSearchCommon.formatDate(startTime);
				if (result.type != ETSSearchResult.TYPE_ALERT_NUM) {
					result.description += "&nbsp;&nbsp;&nbsp;"
						+ ETSSearchCommon.formatTime(startTime);
				}
			}
		} finally {
			close(rs);
			close(pstmt);
			db1.closeConn();
		}

	}

	private void getSelfAssessmentDetails(List entResults)
		throws SQLException {

		DbConnect db1 = new DbConnect(this);
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			db1.makeConn(ETS_DS);

			pstmt =
				db1.conn.prepareStatement(
					"select SELF_NAME, SELF_PM, STATE"
						+ " from ETS.ETS_SELF_MAIN"
						+ " where SELF_ID = ?"
						+ " with ur");

			Iterator iter = entResults.iterator();
			while (iter.hasNext()) {
				ETSSearchResult result = (ETSSearchResult) iter.next();
				if (result.type != ETSSearchResult.TYPE_SELF_ASSESSMENT_NUM) {
					continue;
				}

				pstmt.setString(1, result.id);
				rs = pstmt.executeQuery();
				rs.next();

				String name = rs.getString("SELF_NAME");
				String irUserId = rs.getString("SELF_PM");
				String state = rs.getString("STATE");

				rs.close();
				rs = null;

				result.location = "Self Assessment Reviews";
				result.irUserId = irUserId;
				result.userIdRole = "Owner";
				result.description = "Status: " + state;
				result.title = name;

				result.link =
					PROJECTS_SERVLET
						+ "?etsop=compile"
						+ "&amp;proj="
						+ result.projectId
						+ "&amp;self="
						+ result.id
						+ "&amp;tc="
						+ result.topCatId
						+ "&amp;linkid="
						+ result.linkid;
			}
		} finally {
			close(rs);
			close(pstmt);
			db1.closeConn();
		}
	}

	private void getSetMetDetails(List entResults) throws SQLException {
		DbConnect db1 = new DbConnect(this);
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			db1.makeConn(ETS_DS);

			pstmt =
				db1.conn.prepareStatement(
					"select QBR_NAME, STATE, INTERVIEW_BY"
						+ " from ETS.ETS_QBR_MAIN"
						+ " where QBR_ID = ?"
						+ " with ur");

			Iterator iter = entResults.iterator();
			while (iter.hasNext()) {
				ETSSearchResult result = (ETSSearchResult) iter.next();
				if (result.type != ETSSearchResult.TYPE_SETMET_NUM) {
					continue;
				}

				pstmt.setString(1, result.id);
				rs = pstmt.executeQuery();
				rs.next();

				String name = rs.getString("QBR_NAME");
				String state = rs.getString("STATE");
				String irUserId = rs.getString("INTERVIEW_BY");

				rs.close();
				rs = null;

				result.location = "Set/Met Reviews";
				result.irUserId = irUserId;
				result.userIdRole = "Interview by";
				result.description = "Status: " + state;
				result.title = name;

				result.link =
					PROJECTS_SERVLET
						+ "?etsop=inter"
						+ "&amp;proj="
						+ result.projectId
						+ "&amp;set="
						+ result.id
						+ "&amp;tc="
						+ result.topCatId
						+ "&amp;linkid="
						+ result.linkid;
			}
		} finally {
			close(rs);
			close(pstmt);
			db1.closeConn();
		}
	}

	private StringBuffer getFormattedResults(List entResults)
		throws SQLException {

		StringBuffer buffer = new StringBuffer();
		buffer.append(TABLE_TAG);

		boolean hasDoc = false;
		boolean hasCat = false;
		boolean hasIssue = false;
		boolean hasChangeRequest = false;
		boolean hasCalendar = false;
		boolean hasSelfAssessment = false;
		boolean hasSetMet = false;

		DbConnect db1 = new DbConnect(this);
		DbConnect db2 = new DbConnect(this);
		PreparedStatement pstmt = null;
		PreparedStatement projPstmt = null;
		ResultSet rs = null;

		try {
			db1.makeConn(ETS_DS);
			pstmt =
				db1.conn.prepareStatement(
					"select CAT_ID from ETS.ETS_CAT"
						+ " where PARENT_ID = 0 and VIEW_TYPE = ? AND PROJECT_ID = ?"
						+ " with ur");

			db2.makeConn(ETS_DS);
			projPstmt =
				db2.conn.prepareStatement(
					"select PROJECT_NAME from ETS.ETS_PROJECTS"
						+ " where PROJECT_ID = ?"
						+ " with ur");

			Iterator iterator = entResults.iterator();
			while (iterator.hasNext()) {
				ETSSearchResult result = (ETSSearchResult) iterator.next();
				int viewType = -1;

				switch (result.type) {
					case ETSSearchResult.TYPE_DOC_NUM :
					case ETSSearchResult.TYPE_PROJECT_PLAN_NUM :
						hasDoc = true;
						if (result.type == ETSSearchResult.TYPE_DOC_NUM)
							viewType = Defines.DOCUMENTS_VT;
						else
							viewType = Defines.MAIN_VT;
						break;

					case ETSSearchResult.TYPE_CAT_NUM :
						hasCat = true;
						viewType = Defines.DOCUMENTS_VT;
						break;

					case ETSSearchResult.TYPE_ISSUE_NUM :
						hasIssue = true;
						viewType = Defines.ISSUES_CHANGES_VT;
						break;

					case ETSSearchResult.TYPE_CHANGE_REQ_NUM :
						hasChangeRequest = true;
						viewType = Defines.ISSUES_CHANGES_VT;
						break;

					case ETSSearchResult.TYPE_ALERT_NUM :
					case ETSSearchResult.TYPE_EVENT_NUM :
					case ETSSearchResult.TYPE_MEETING_NUM :
						hasCalendar = true;
						if (result.type == ETSSearchResult.TYPE_MEETING_NUM)
							viewType = Defines.MEETINGS_VT;
						else
							viewType = Defines.MAIN_VT;
						break;

					case ETSSearchResult.TYPE_SELF_ASSESSMENT_NUM :
						hasSelfAssessment = true;
						viewType = Defines.SELF_ASSESSMENT_VT;
						break;

					case ETSSearchResult.TYPE_SETMET_NUM :
						hasSetMet = true;
						viewType = Defines.SETMET_VT;
						break;

					default :
						throw new RuntimeException(
							"Unknown result type: " + result.type);
				}

				pstmt.setInt(1, viewType);
				pstmt.setString(2, result.projectId);
				rs = pstmt.executeQuery();
				if (rs.next()) {
					result.topCatId = rs.getInt(1);
				} else {
					result.topCatId = 0;
				}
				rs.close();
				rs = null;

				projPstmt.setString(1, result.projectId);
				rs = projPstmt.executeQuery();
				if (rs.next()) {
					result.projectName = rs.getString(1);
				}
				rs.close();
				rs = null;
			}
			pstmt.close();
			pstmt = null;
			projPstmt.close();
			projPstmt = null;
			db2.closeConn();

			if (hasDoc) {
				getDocumentDetails(entResults);
			}

			if (hasCat) {
				getCategoryDetails(entResults);
			}

			if (hasIssue) {
				getIssueDetails(entResults);
			}

			if (hasChangeRequest) {
				getChangeRequestDetails(entResults);
			}

			if (hasCalendar) {
				getCalendarDetails(entResults);
			}

			if (hasSelfAssessment) {
				getSelfAssessmentDetails(entResults);
			}

			if (hasSetMet) {
				getSetMetDetails(entResults);
			}

			boolean ibmOnlyFlag = false;
			boolean ibmConfFlag = false;
			boolean privateFlag = false;
			boolean expiredFlag = false;

			pstmt = ETSSearchCommon.getUserNamePstmt(db1.conn);
			TreeMap amtCache = new TreeMap();

			iterator = entResults.iterator();
			while (iterator.hasNext()) {
				ETSSearchResult result = (ETSSearchResult) iterator.next();

				boolean isIRuserid = true;
				if (result.type == ETSSearchResult.TYPE_ISSUE_NUM
					|| result.type == ETSSearchResult.TYPE_CHANGE_REQ_NUM) {

					isIRuserid = false;
				}
				String userName =
					ETSSearchCommon.getUserName(
						result.irUserId,
						isIRuserid,
						amtCache,
						pstmt);

				buffer.append("<tr><td>\n");
				buffer.append(TABLE_TAG);
				buffer.append("<tr>\n");
				buffer.append("<td>\n");
				if (result.isPopup) {
					buffer.append(POPUP_GIF);
					buffer.append("&nbsp;");
				} else if (result.type == ETSSearchResult.TYPE_CAT_NUM) {
					buffer.append(CAT_IMAGE);
					buffer.append("&nbsp;");
				}
				buffer.append("<strong>");
				buffer.append("<a class=\"fbox\" href=\"");
				buffer.append(result.link);
				buffer.append("\"");
				if (result.isPopup) {
					String popupJS =
						getPopupJavascript(result.link, "Calendar");
					buffer.append(" target=\"new\" onclick=\"");
					buffer.append(popupJS);
					buffer.append("\" onkeypress=\"");
					buffer.append(popupJS);
					buffer.append("\"");
				}
				buffer.append(">");
				buffer.append(result.title);
				buffer.append("</a>");
				buffer.append("</strong>");
				if (result.ibmOnly == Defines.ETS_IBM_ONLY) {
					buffer.append("&nbsp;");
					buffer.append(IBM_ONLY_FLAG);
					ibmOnlyFlag = true;
				} else if (result.ibmOnly == Defines.ETS_IBM_CONF) {
					buffer.append("&nbsp;");
					buffer.append(IBM_CONF_FLAG);
					ibmConfFlag = true;
				}
				if (result.isPrivate) {
					buffer.append("&nbsp;");
					buffer.append(PRIVATE_FLAG);
					privateFlag = true;
				}
				if (result.hasExpired) {
					buffer.append("&nbsp;");
					buffer.append(EXPIRED_FLAG);
					expiredFlag = true;
				}
				buffer.append("</td>\n");
				if (result.altLink != null) {
					buffer.append("<td align=\"right\">\n");
					buffer.append("<span class=\"small\">");
					buffer.append("<a href=\"");
					buffer.append(result.altLink);
					buffer.append("\">");
					buffer.append(result.altTitle);
					buffer.append("</a>");
					buffer.append("</span>");
					buffer.append("</td>\n");
				}
				buffer.append("</tr>\n");
				buffer.append("</table>\n");
				buffer.append("</td></tr>\n");

				buffer.append("<tr><td>\n");
				buffer.append("<span class=\"small\">");
				buffer.append(result.userIdRole);
				buffer.append(": ");
				buffer.append(userName);
				buffer.append("</span>");
				buffer.append("</td></tr>\n");

				buffer.append("<tr><td>\n");
				buffer.append("<span class=\"small\">");
				buffer.append("<strong>");
				buffer.append(result.projectName);
				buffer.append("</strong>");
				buffer.append(HTML_SEPARATOR);
				buffer.append(result.location);
				if (DEBUG_FLAG) {
					buffer.append(" - ");
					buffer.append(result.score);
					buffer.append(" - ");
					buffer.append(result.id);
					buffer.append(" - ");
					buffer.append(result.type);
				}
				buffer.append("</span>");
				buffer.append("</td></tr>\n");

				if (result.description != null) {
					buffer.append("<tr><td>\n");
					buffer.append("<span class=\"small\">");
					buffer.append(result.description);
					buffer.append("</span>");
					buffer.append("</td></tr>\n");
				}

				buffer.append("<tr><td height=\"12\">");
				buffer.append(DOTTED_RULE_GIF);
				buffer.append("</td></tr>");
			}

			if (expiredFlag) {
				buffer.append("<tr><td class=\"small\" height=\"20\">");
				buffer.append(EXPIRED_FLAG);
				buffer.append(" Denotes expired content");
				buffer.append("</td></tr>");
			}
			if (privateFlag) {
				buffer.append("<tr><td class=\"small\" height=\"20\">");
				buffer.append(PRIVATE_FLAG);
				buffer.append(
					" Denotes content restricted to selected team members");
				buffer.append("</td></tr>");
			}
			if (ibmOnlyFlag) {
				buffer.append("<tr><td class=\"small\" height=\"20\">");
				buffer.append(IBM_ONLY_FLAG);
				buffer.append(" Denotes IBM Only content");
				buffer.append("</td></tr>");
			}
			if (ibmConfFlag) {
				buffer.append("<tr><td class=\"small\" height=\"20\">");
				buffer.append(IBM_CONF_FLAG);
				buffer.append(" Denotes permanent IBM Only content");
				buffer.append("</td></tr>");
			}
			if (expiredFlag || privateFlag || ibmOnlyFlag || ibmConfFlag) {
				buffer.append("<tr> \n");
				buffer.append("<td height=\"15\"> \n");
				buffer.append(GRAY_RULE_GIF);
				buffer.append("</td> \n");
				buffer.append("</tr> \n");
			}

		} finally {
			close(rs);
			close(pstmt);
			close(projPstmt);
			db1.closeConn();
			db2.closeConn();
		}

		buffer.append("</table> \n");
		return buffer;
	}

	private String getPopupJavascript(String url, String name) {
		return "window.open('"
			+ url
			+ "', '"
			+ name
			+ "', 'toolbar=0,scrollbars=1,location=0,statusbar=1,menubar=0,resizable=1,width=550,height=420,left=150,top=120'); return false;";
	}

	//	private StringBuffer getGenericRightColumn() throws SQLException {
	//	
	//		initLandingPageInfoLink();
	//	
	//		StringBuffer buffer = new StringBuffer();
	//	
	//		buffer.append(
	//			"<table summary=\"\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"150\">");
	//		buffer.append("<tr valign=\"middle\">");
	//		buffer.append(
	//			"<td headers=\"\" style=\"background-color: #ffffff;\" align=\"center\">");
	//		buffer.append("<a href=\"");
	//		buffer.append(LANDING_PAGE_INFO_LINK);
	//		buffer.append(
	//			"\"><img border=\"0\" height=\"149\" width=\"150\" alt=\"");
	//		buffer.append(LANDING_PAGE_IMAGE_ALT_TEXT);
	//		buffer.append("\" title=\"");
	//		buffer.append(LANDING_PAGE_IMAGE_ALT_TEXT);
	//		buffer.append("\" src=\"");
	//		buffer.append(IMAGE_SERVLET);
	//		buffer.append("?proj=ETS_LANDING_1&mod=1\" />");
	//		buffer.append("</a>");
	//		buffer.append("</td>");
	//		buffer.append("</tr>");
	//		buffer.append("</table>");
	//	
	//		return buffer;
	//	}
	//	
	//	private void initLandingPageInfoLink() throws SQLException {
	//		if (LANDING_PAGE_INFO_LINK != null) {
	//			return;
	//		}
	//	
	//		DbConnect db = new DbConnect(this);
	//		Statement stmt = null;
	//		ResultSet rs = null;
	//	
	//		try {
	//			db.makeConn(ETS_DS);
	//			stmt = db.conn.createStatement();
	//	
	//			rs =
	//				stmt.executeQuery(
	//					"select IMAGE_ALT_TEXT, INFO_LINK from ETS.ETS_PROJECT_INFO"
	//						+ " where PROJECT_ID = 'ETS_LANDING_1'"
	//						+ " and INFO_MODULE = 1"
	//						+ " with ur");
	//	
	//			if (rs.next()) {
	//				LANDING_PAGE_IMAGE_ALT_TEXT = rs.getString("IMAGE_ALT_TEXT");
	//				LANDING_PAGE_INFO_LINK = rs.getString("INFO_LINK");
	//	
	//				if (LANDING_PAGE_IMAGE_ALT_TEXT != null) {
	//					LANDING_PAGE_IMAGE_ALT_TEXT =
	//						LANDING_PAGE_IMAGE_ALT_TEXT.trim();
	//				} else {
	//					LANDING_PAGE_IMAGE_ALT_TEXT = "";
	//				}
	//	
	//				if (LANDING_PAGE_INFO_LINK != null) {
	//					LANDING_PAGE_INFO_LINK = LANDING_PAGE_INFO_LINK.trim();
	//				}
	//			}
	//		} finally {
	//			close(rs);
	//			close(stmt);
	//			db.closeConn();
	//		}
	//	}

	private StringBuffer getSearchHeader(
		String displayQuery,
		String SEARCH_REALM,
		int numEntResults,
		String numResultsStr,
		String multiplePageLinks) {

		StringBuffer buffer = new StringBuffer();

		buffer.append(TABLE_TAG);

		buffer.append("<tr height=\"20\"><td>");
		buffer.append("Searched for ");
		buffer.append("<strong>");
		buffer.append(displayQuery);
		buffer.append("</strong>");
		buffer.append(" in: ");
		buffer.append("<em>");
		buffer.append(SEARCH_REALM);
		buffer.append("</em>");
		buffer.append("</td></tr>\n");

		buffer.append("<tr height=\"20\"><td>");
		buffer.append("<span class=\"small\">");
		buffer.append(numResultsStr);
		buffer.append("</span>");
		buffer.append("</td></tr>\n");

		if (multiplePageLinks.length() > 0) {
			buffer.append("<tr height=\"15\"><td>");
			buffer.append(GRAY_RULE_GIF);
			buffer.append("</td></tr>\n");

			buffer.append("<tr><td>");
			buffer.append(multiplePageLinks);
			buffer.append("</td></tr>\n");
		}

		if (numEntResults > 0) {
			buffer.append("<tr height=\"15\"><td>");
			buffer.append(GRAY_RULE_GIF);
			buffer.append("</td></tr>\n");
		}

		buffer.append("</table>");

		return buffer;
	}

	private StringBuffer getSearchFooter(String multiplePageLinks) {
		if (multiplePageLinks.length() == 0)
			return new StringBuffer();

		StringBuffer buffer = new StringBuffer(500);
		buffer.append(TABLE_TAG);

		buffer.append("<tr> \n");
		buffer.append("<td>\n");
		buffer.append(multiplePageLinks);
		buffer.append("</td> \n");
		buffer.append("</tr> \n");

		buffer.append("<tr><td height=\"15\">");
		buffer.append(GRAY_RULE_GIF);
		buffer.append("</td></tr>");

		buffer.append("</table> \n");

		return buffer;
	}

	private StringBuffer getMultiplePageLinks(
		String searchLinkURL,
		int currentPage,
		int numEntResults) {

		if (numEntResults <= RESULTS_PER_PAGE)
			return new StringBuffer();

		String urlPrefix = searchLinkURL + "&amp;showPage=";

		int numPages = ((numEntResults - 1) / RESULTS_PER_PAGE) + 1;

		StringBuffer buffer = new StringBuffer(500);

		if (currentPage > 1) {
			buffer.append("<a class=\"fbox\" href=\"");
			buffer.append(urlPrefix);
			buffer.append(currentPage - 1);
			buffer.append("\">");
			buffer.append("&lt;&nbsp;Previous");
			buffer.append("</a>");

			buffer.append("&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp");
		}

		for (int i = 1; i <= numPages; i++) {
			if (i == currentPage) {
				buffer.append("&nbsp;<strong>");
				buffer.append(i);
				buffer.append("</strong>&nbsp; ");
			} else {
				buffer.append("&nbsp;<a class=\"fbox\" href=\"");
				buffer.append(urlPrefix);
				buffer.append(i);
				buffer.append("\">");
				buffer.append(i);
				buffer.append("</a>&nbsp; ");
			}
		}

		if (currentPage < numPages) {
			buffer.append("&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;");

			buffer.append("<a class=\"fbox\" href=\"");
			buffer.append(urlPrefix);
			buffer.append(currentPage + 1);
			buffer.append("\">");
			buffer.append("Next&nbsp;&gt;");
			buffer.append("</a>");
		}

		return buffer;
	}

	/*
	private void logSearchUsage(
		Timestamp TIMESTAMP,
		String USERID,
		String SEARCH_STRING,
		String SEARCH_REALM,
		String SEARCH_TYPE,
		short NUM_ALL_RESULTS,
		short ENT_CAT_RESULTS,
		short ENT_DOC_RESULTS,
		short ENT_FILE_RESULTS,
		String REFERRER_LINK,
		String REFERRER_PAGE) {
	
		if (true) {
			return;
		}
	
		DbConnect db = new DbConnect(this);
	
		try {
	
			db.makeConn(ETS_DS);
	
			StringBuffer sqlbuff = new StringBuffer();
	
			sqlbuff.append("INSERT INTO ETS.SEARCH_USAGE");
			sqlbuff.append(
				" ( TIMESTAMP, IR_USERID, SEARCH_STRING, SEARCH_REALM, SEARCH_TYPE,");
			sqlbuff.append(" ALL_RESULTS,");
			sqlbuff.append(
				" ENT_CAT_RESULTS, ENT_DOC_RESULTS, ENT_FILE_RESULTS,");
			sqlbuff.append(" REFERRER_LINK, REFERRER_PAGE)");
			sqlbuff.append(" VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");
	
			PreparedStatement pstmt =
				db.conn.prepareStatement(sqlbuff.toString());
	
			pstmt.setTimestamp(1, TIMESTAMP);
			pstmt.setString(2, USERID);
			pstmt.setString(3, truncateString(SEARCH_STRING, 128));
			pstmt.setString(4, truncateString(SEARCH_REALM, 50));
			pstmt.setString(5, SEARCH_TYPE);
			pstmt.setShort(6, NUM_ALL_RESULTS);
			pstmt.setShort(7, ENT_CAT_RESULTS);
			pstmt.setShort(8, ENT_DOC_RESULTS);
			pstmt.setShort(9, ENT_FILE_RESULTS);
			pstmt.setString(10, truncateString(REFERRER_LINK, 500));
			pstmt.setString(11, truncateString(REFERRER_PAGE, 128));
	
			pstmt.executeUpdate();
	
			pstmt.close();
	
		} catch (SQLException e) {
			logError(
				"SQLException in logSearchUsage()",
				TIMESTAMP.toString() + ":" + USERID,
				e);
		} finally {
			db.closeConn();
		}
	
	}
	*/

	private synchronized boolean initialize() {
		if (initialized)
			return true;

		if (startThreads(true)) {
			initializeTime = System.currentTimeMillis();
			initialized = true;
		}

		return initialized;
	}

	public void init() {
		try {
			initialize();
		} catch (Throwable t) {
		}
	}

	private boolean startThreads(boolean initialize) {
		try {
			if (!ETSSearchMonitor.hasStarted()) {
				indexCache = ETSSearchMonitor.startThread();
				if (!initialize) {
					logError(
						"starting ETSSearchMonitor outside of init",
						"starting ETSSearchMonitor outside of init");
				}
			}

			return true;
		} catch (MissingResourceException mre) {
			log.error(
				"MissingResourceException in startThreads(" + initialize + ")",
				mre);
			return false;
		} catch (Exception e) {
			logError("Exception in startThreads(" + initialize + ")", "", e);
			return false;
		}
	}

	public void destroy() {
		if (indexCache != null) {
			indexCache.stopThread();
		}
	}

	private void showETSIndexCacheStatus(
		HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException, IOException {

		String str =
			""
				+ "initialized:                  "
				+ initialized
				+ "\n"
				+ "initializeTime:               "
				+ new Date(initializeTime)
				+ "\n"
				+ "hostName:                     "
				+ ETSSearchCommon.getHostName()
				+ "\n"
				+ "mailHost:                     "
				+ ETSSearchCommon.getMailHost()
				+ "\n"
				+ "\n"
				+ indexCache.getIndexInfo()
				+ "\n"
				+ "\n"
				+ "ETSSearchCommon:              "
				+ ETSSearchCommon.VERSION_SID
				+ " - "
				+ ETSSearchCommon.LAST_UPDATE
				+ "\n"
				+ "ETSSearchCreateIndex:         "
				+ ETSSearchCreateIndex.VERSION_SID
				+ " - "
				+ ETSSearchCreateIndex.LAST_UPDATE
				+ "\n"
				+ "ETSSearchMonitor:             "
				+ ETSSearchMonitor.VERSION_SID
				+ " - "
				+ ETSSearchMonitor.LAST_UPDATE
				+ "\n"
				+ "ETSSearchResult:              "
				+ ETSSearchResult.VERSION_SID
				+ " - "
				+ ETSSearchResult.LAST_UPDATE
				+ "\n"
				+ "ETSSearchResultComparator:    "
				+ ETSSearchResultComparator.VERSION_SID
				+ " - "
				+ ETSSearchResultComparator.LAST_UPDATE
				+ "\n"
				+ "ETSSearchServlet:             "
				+ ETSSearchServlet.VERSION_SID
				+ " - "
				+ ETSSearchServlet.LAST_UPDATE
				+ "\n";

		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		out.println(str);
		return;
	}

	private String getIntegerIdList(Collection results, byte type) {
		Iterator iter = results.iterator();
		StringBuffer buffer = new StringBuffer();

		buffer.append('(');
		while (iter.hasNext()) {
			ETSSearchResult result = (ETSSearchResult) iter.next();
			if (type == result.type || type <= 0) {
				buffer.append(result.id);
				buffer.append(',');
			}
		}
		buffer.deleteCharAt(buffer.length() - 1);
		buffer.append(')');

		return buffer.toString();
	}

	private String getStringIdList(Collection results, byte type) {
		Iterator iter = results.iterator();
		StringBuffer buffer = new StringBuffer();

		buffer.append('(');
		while (iter.hasNext()) {
			ETSSearchResult result = (ETSSearchResult) iter.next();
			if (type == result.type || type <= 0) {
				buffer.append('\'');
				buffer.append(result.id);
				buffer.append('\'');
				buffer.append(',');
			}
		}
		buffer.deleteCharAt(buffer.length() - 1);
		buffer.append(')');

		return buffer.toString();
	}

	static String truncateString(String s, int len) {
		if (s == null || s.length() <= len) {
			return s;
		} else {
			if (len < 4) {
				len = 4;
			}
			return s.substring(0, len - 3) + "...";
		}
	}

	private DocScore[] combineDocScores(DocScore[] arr1, DocScore[] arr2) {
		DocScore[] arr0 = new DocScore[arr1.length + arr2.length];
		int index = 0;

		for (int i = 0; i < arr1.length; i++) {
			arr0[index++] = arr1[i];
		}

		for (int i = 0; i < arr2.length; i++) {
			arr0[index++] = arr2[i];
		}

		return arr0;
	}

	private ArrayList getCommonResults(
		TreeMap allResults,
		ArrayList cachedResults) {

		ArrayList commonResults = new ArrayList();

		// if (allResults == null || cachedResults == null)
		// return commonResults;

		Iterator iter = cachedResults.iterator();

		while (iter.hasNext()) {
			ETSSearchResult cachedResult = (ETSSearchResult) iter.next();
			Integer key = new Integer(cachedResult.id);
			ETSSearchResult result = (ETSSearchResult) allResults.get(key);

			if (result != null) {
				commonResults.add(result);
			}
		}

		return commonResults;
	}

	static String replace(String str, char x, String y) {

		if (str == null || str.length() == 0 || y == null)
			return str;

		StringBuffer buffer = new StringBuffer();

		int xPosition = -1;
		int cursorPosition = 0;

		while ((xPosition = str.indexOf(x, cursorPosition)) >= 0) {
			buffer.append(str.substring(cursorPosition, xPosition));
			buffer.append(y);
			cursorPosition = xPosition + 1;
		}

		buffer.append(str.substring(cursorPosition));

		return buffer.toString();

	}

	static String replace(String str, String x, String y) {

		if (str == null
			|| str.length() == 0
			|| x == null
			|| x.length() == 0
			|| y == null)
			return str;

		StringBuffer buffer = new StringBuffer();

		int xPosition = -1;
		int cursorPosition = 0;
		int xLength = x.length();

		while ((xPosition = str.indexOf(x, cursorPosition)) >= 0) {
			buffer.append(str.substring(cursorPosition, xPosition));
			buffer.append(y);
			cursorPosition = xPosition + xLength;
		}

		buffer.append(str.substring(cursorPosition));

		return buffer.toString();

	}

	static void drawErrorPage(
		HttpServletRequest request,
		HttpServletResponse response,
		String details,
		EdgeAccessCntrl edgeAccessCntrl) {

		try {
			if (details == null || details.trim().length() == 0) {
				details = "System error";
			}

			StringBuffer buffer = new StringBuffer(1024);

			buffer.append(
				"<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"443\">");

			buffer.append("<tr>");
			buffer.append("<td>");
			buffer.append("<strong style=\"color:#ff3333\">");
			buffer.append(details);
			buffer.append("</strong>");
			buffer.append("<br />");
			buffer.append("<br />");
			buffer.append("</td>");
			buffer.append("</tr>");

			buffer.append("<tr>");
			buffer.append("<td height=\"18\" class=\"tdblue\">");
			buffer.append("&nbsp;&nbsp;Details");
			buffer.append("</td>");
			buffer.append("</tr>");

			buffer.append("<tr>");
			buffer.append("<td>");
			buffer.append("<br />");
			buffer.append(
				"Servlet: ETSSearchServlet (version: " + VERSION_SID + ")");
			buffer.append("<br />");
			buffer.append("<br />");
			buffer.append("User ID: " + edgeAccessCntrl.gIR_USERN);
			buffer.append("<br />");
			buffer.append("<br />");
			buffer.append("Time: " + new Date().toString());
			buffer.append("</td>");
			buffer.append("</tr>");

			buffer.append("<tr>");
			buffer.append("<td>");
			buffer.append("<br />");
			buffer.append(BACK_BUTTON);
			buffer.append("</td>");
			buffer.append("</tr>");

			buffer.append("</table>");

			response.setHeader("Cache-Control", "no-cache");
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			AmtErrorHandler errorHandler = new AmtErrorHandler();
			errorHandler.setErrorTitle("IBM Customer Connect");
			errorHandler.setErrorHeader("Our apologies...");
			out.println(errorHandler.printErrorScreen(buffer.toString()));
			out.close();
		} catch (Throwable t) {
			logError(
				"Exception in drawErrorPage()",
				" for gIR_USERN: "
					+ edgeAccessCntrl.gIR_USERN
					+ " thrown drawing error page with errorCode: "
					+ details,
				t);
		}
	}

	private static void showErrorAndLog(
		HttpServletRequest request,
		HttpServletResponse response,
		String details,
		EdgeAccessCntrl edgeAccessCntrl) {

		showErrorAndLog(
			request,
			response,
			details,
			details,
			null,
			null,
			edgeAccessCntrl);
	}

	private static void showErrorAndLog(
		HttpServletRequest request,
		HttpServletResponse response,
		String userMessage,
		String shortDesc,
		EdgeAccessCntrl edgeAccessCntrl) {

		showErrorAndLog(
			request,
			response,
			userMessage,
			shortDesc,
			null,
			null,
			edgeAccessCntrl);
	}

	private static void showErrorAndLog(
		HttpServletRequest request,
		HttpServletResponse response,
		String details,
		Throwable t,
		EdgeAccessCntrl edgeAccessCntrl) {

		showErrorAndLog(
			request,
			response,
			details,
			details,
			null,
			t,
			edgeAccessCntrl);
	}

	private static void showErrorAndLog(
		HttpServletRequest request,
		HttpServletResponse response,
		String userMessage,
		String shortDesc,
		String longDesc,
		Throwable t,
		EdgeAccessCntrl edgeAccessCntrl) {

		drawErrorPage(request, response, userMessage, edgeAccessCntrl);

		if (longDesc == null) {
			longDesc = "";
		}

		StringBuffer urlBuf = request.getRequestURL();
		String queryString = request.getQueryString();
		if (queryString != null) {
			urlBuf.append('?');
			urlBuf.append(queryString);
		}

		longDesc =
			edgeAccessCntrl.gIR_USERN + ":" + longDesc + "\nURL: " + urlBuf;

		if (t != null) {
			logError(shortDesc, longDesc, t);
		} else {
			logError(shortDesc, longDesc);
		}
	}

	private boolean getProfile(
		HttpServletRequest request,
		HttpServletResponse response,
		EdgeAccessCntrl edgeAccessCntrl)
		throws SQLException, AMTException {

		DbConnect amtdb = new DbConnect(this);

		try {
			amtdb.makeConn(AMT_DS);
			return edgeAccessCntrl.GetProfile(response, request, amtdb.conn);
		} finally {
			amtdb.closeConn();
		}

	}

	private static ArrayList getAncestors(
		Integer catId,
		TreeMap cache,
		PreparedStatement pstmt)
		throws SQLException {

		ArrayList cachedList = (ArrayList) cache.get(catId);
		if (cachedList != null) {
			return cachedList;
		}

		pstmt.setInt(1, catId.intValue());
		ResultSet rs = pstmt.executeQuery();
		rs.next();

		String catName = rs.getString("CAT_NAME");
		int parentId = rs.getInt("PARENT_ID");

		rs.close();

		ArrayList ancestors = new ArrayList();
		ancestors.add(new String[] { String.valueOf(catId), catName });

		if (parentId > 0) {
			ancestors.addAll(getAncestors(new Integer(parentId), cache, pstmt));
		}

		cache.put(catId, ancestors);

		return ancestors;
	}

	private String getProjectName(String projectId) throws SQLException {
		if (projectId == null || projectId.length() == 0) {
			return null;
		}

		DbConnect db = new DbConnect(this);
		Statement stmt = null;
		ResultSet rs = null;
		try {
			String sql =
				"select PROJECT_NAME from ETS.ETS_PROJECTS"
					+ " where PROJECT_ID = '"
					+ projectId
					+ "' with ur";

			db.makeConn(ETS_DS);
			stmt = db.conn.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				return rs.getString(1);
			}
		} finally {
			close(rs);
			close(stmt);
			db.closeConn();
		}

		return null;
	}

	static void displayMessage(HttpServletResponse response, String message)
		throws IOException {

		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		out.println(message);
		out.close();
	}

	static void close(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (Exception e) {
				log.error(e);
			}
		}
	}

	static void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
				log.error(e);
			}
		}
	}

	private static void logError(String subject, String text) {
		log.error(subject + ":" + text);
		ETSSearchCommon.sendMail(subject, text, thisClass);
	}

	private static void logError(String subject, String text, Throwable t) {
		log.error(subject + ":" + text, t);
		ETSSearchCommon.sendMail(subject, text, t, thisClass);
	}

}
