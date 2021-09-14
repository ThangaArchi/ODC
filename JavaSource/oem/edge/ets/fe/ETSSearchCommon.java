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
 * @since   custcont.4.2.1
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.zip.GZIPOutputStream;

import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.AmtHeaderFooter;
import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.ets.fe.brand.PropertyFactory;
import oem.edge.ets.fe.brand.UnbrandedProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.Log4JLogger;

public class ETSSearchCommon extends Thread {

	public static final String Copyright = "(C) Copyright IBM Corp. 2003, 2004";

	public static final String VERSION_SID = "1.26";
	public static final String LAST_UPDATE = "11/10/05 14:32:23";

	private static final Class thisClass = ETSSearchCommon.class;

	private static String hostName = "NA";
	private static String shortHostName = hostName;

	private static Log log =
		new Log4JLogger(thisClass.getName() + "." + shortHostName);

	final static TimeZone zoneEastern = TimeZone.getTimeZone("US/Eastern");

	private static final SimpleDateFormat dateFormatter =
		new SimpleDateFormat("MM/dd/yyyy", Locale.US);
	private static final SimpleDateFormat timeFormatter =
		new SimpleDateFormat("h:mm a", Locale.US);

	private static final DecimalFormat sizeFormatter = new DecimalFormat();
	private static final int CUTOFF_MB = 900 * 1024;
	private static final int ONE_KB = 1024;
	private static final int ONE_MB = 1024 * 1024;

	private static final String[] ADMINS = { "navneet@us.ibm.com" };

	static final String SEARCH_SERVLET =
		Defines.SERVLET_PATH + "ETSSearchServlet.wss";

	private static Map viewTypes = new HashMap();

	private static ResourceBundle gwaProps = null;

	private static String defaultMailHost = "us.ibm.com";
	private static String mailHost;

	private static InternetAddress mailFrom;
	private static InternetAddress[] defaultMailTo;

	private InternetAddress[] mailTo;
	private String subject;
	private String text;

	static {
		// dateFormatter.setTimeZone(zoneEastern);
		// timeFormatter.setTimeZone(zoneEastern);
		sizeFormatter.setMaximumFractionDigits(2);

		try {
			defaultMailTo = InternetAddress.parse("navneet@us.ibm.com", false);
			mailFrom =
				new InternetAddress("eConnect@us.ibm.com", "ETSSearchMailer");
		} catch (Exception e) {
			log.error("Exception in ETSSearchCommon static initializer", e);
		}

		try {
			hostName = InetAddress.getLocalHost().getHostName();
			int index = hostName.indexOf('.');
			if (index > 0 && index < hostName.length()) {
				shortHostName = hostName.substring(0, index);
			} else {
				shortHostName = hostName;
			}
			log = new Log4JLogger(thisClass.getName() + "." + shortHostName);
		} catch (UnknownHostException e) {
			log.error("Exception in ETSSearchCommon static initializer", e);
		}

		viewTypes.put(
			new Integer(Defines.DOCUMENTS_VT),
			new String[] { ETSSearchResult.TYPE_DOC_STR, "Documents" });
		viewTypes.put(
			new Integer(Defines.ISSUES_CHANGES_VT),
			new String[] { ETSSearchResult.TYPE_ISSUE_STR, "Issues/Changes" });
		viewTypes.put(
			new Integer(Defines.MEETINGS_VT),
			new String[] { ETSSearchResult.TYPE_MEETING_STR, "Meetings" });
		viewTypes.put(
			new Integer(Defines.MAIN_VT),
			new String[] { ETSSearchResult.TYPE_MAIN_STR, "Messages/Events" });
		viewTypes.put(
			new Integer(Defines.SETMET_VT),
			new String[] {
				ETSSearchResult.TYPE_SETMET_STR,
				"Set/Met Reviews" });
		viewTypes.put(
			new Integer(Defines.SELF_ASSESSMENT_VT),
			new String[] {
				ETSSearchResult.TYPE_SELF_ASSESSMENT_STR,
				"Self Assessment" });
	}

	static Log getLog(Class clazz) {
		return new Log4JLogger(clazz.getName() + "." + shortHostName);
	}

	static String getHostName() {
		return hostName;
	}

	static boolean isSearchableViewType(int vt) {
		return getViewTypeArr(vt) != null;
	}

	static String getTabType(int vt) {
		String[] arr = getViewTypeArr(vt);
		if (arr != null) {
			return arr[0];
		} else {
			return null;
		}
	}

	static String getTabName(int vt) {
		String[] arr = getViewTypeArr(vt);
		if (arr != null) {
			return arr[1];
		} else {
			return null;
		}
	}

	private static String[] getViewTypeArr(int vt) {
		return (String[]) viewTypes.get(new Integer(vt));
	}

	static boolean isAdmin(EdgeAccessCntrl accessCntrl) {
		for (int i = 0; i < ADMINS.length; i++) {
			if (ADMINS[i].equals(accessCntrl.gIR_USERN)) {
				return true;
			}
		}
		return false;
	}

	public static String getMasthead(AmtHeaderFooter amtHF) {
		return getMasthead(amtHF, null, null, Defines.LINKID, null, null, null);
	}

	public static String getMasthead(
		AmtHeaderFooter amtHF,
		String projectType) {

		return getMasthead(amtHF, null, null, null, null, null, projectType);
	}

	public static String getMasthead(
		AmtHeaderFooter amtHF,
		ETSProj proj,
		ETSCat topCat,
		String linkid) {

		return getMasthead(amtHF, proj, topCat, linkid, null, null, null);
	}

	static String getMasthead(
		AmtHeaderFooter amtHF,
		ETSProj proj,
		ETSCat topCat,
		String linkid,
		String subCatId,
		String subCatName) {

		return getMasthead(
			amtHF,
			proj,
			topCat,
			linkid,
			subCatId,
			subCatName,
			null);
	}

	static String getMasthead(
		AmtHeaderFooter amtHF,
		ETSProj proj,
		ETSCat topCat,
		String linkid,
		String subCatId,
		String subCatName,
		String projectType) {

		int viewType = -1;
		if (topCat != null) {
			viewType = topCat.getViewType();
		}
		return getMasthead(
			amtHF,
			proj,
			viewType,
			linkid,
			subCatId,
			subCatName,
			true,
			true,
			null,
			projectType);
	}

	static String getMasthead(
		AmtHeaderFooter amtHF,
		ETSProj proj,
		int viewType,
		String linkid,
		String subCatId,
		String subCatName,
		boolean restrictTab,
		boolean restrictProj,
		String query,
		String projectType) {

		if (projectType == null && proj != null) {
			projectType = proj.getProjectType();
		}
		if (linkid == null && projectType != null) {
			UnbrandedProperties unBrandedProp =
				PropertyFactory.getProperty(projectType);
			linkid = unBrandedProp.getLinkID();
		}

		String tabName = getTabName(viewType);
		if (subCatId == null && tabName != null) {
			subCatId = "tab";
			subCatName = tabName + " in this workspace";
		}

		StringBuffer searchFormURL = new StringBuffer(SEARCH_SERVLET);
		searchFormURL.append("?linkid=");
		searchFormURL.append(linkid);

		if (projectType != null) {
			searchFormURL.append("&projtype=");
			searchFormURL.append(projectType);
		}

		ArrayList searchScopeOptions = new ArrayList();
		int defaultScope = 0;
		boolean scopeSelected = false;

		if (subCatId != null) {
			searchFormURL.append("&projtab=");
			searchFormURL.append(subCatId);
			searchScopeOptions.add(new String[] { subCatName, "projtab" });
			if (restrictTab && restrictProj) {
				scopeSelected = true;
			} else if (!scopeSelected) {
				defaultScope++;
			}
		}
		if (tabName != null) {
			searchFormURL.append("&tab=");
			searchFormURL.append(viewType);
			searchScopeOptions.add(
				new String[] { tabName + " in all workspaces", "tab" });
			if (restrictTab) {
				scopeSelected = true;
			} else if (!scopeSelected) {
				defaultScope++;
			}
		}
		if (proj != null) {
			searchFormURL.append("&proj=");
			searchFormURL.append(proj.getProjectId());
			searchScopeOptions.add(
				new String[] { "All content in this workspace", "proj" });
			if (restrictProj) {
				scopeSelected = true;
			} else if (!scopeSelected) {
				defaultScope++;
			}
		}
		searchScopeOptions.add(new String[] { "All my workspaces", "ets" });
		searchScopeOptions.add(new String[] { "All of IBM", "ibm" });

		List hiddenFormFields = new ArrayList();
		hiddenFormFields.add(
			new String[] {
				"referer",
				amtHF.getPageHeader() + "-" + amtHF.getSubHeader()});

		return amtHF.printBullsEyeHeader(
			searchFormURL.toString(),
			searchScopeOptions,
			defaultScope,
			query,
			hiddenFormFields);
	}

	static String getGwaProperty(String key, String defaultValue) {
		if (gwaProps == null) {
			try {
				gwaProps = ResourceBundle.getBundle("oem.edge.common.gwa");
			} catch (MissingResourceException e) {
				log.error(
					"MissingResourceException getting oem.edge.common.gwa.properties",
					e);
				return defaultValue;
			}
		}

		String value = null;

		try {
			value = gwaProps.getString(key);
		} catch (MissingResourceException mre) {
			log.warn("MissingResourceException getting key: " + key);
		}

		if (value != null) {
			value = value.trim();
			if (value.length() == 0)
				value = null;
		}

		return (value != null ? value : defaultValue);
	}

	static PreparedStatement getUserNamePstmt(Connection conn)
		throws SQLException {

		return conn.prepareStatement(
			"select USER_FULLNAME from AMT.USERS"
				+ " where IR_USERID = ? or EDGE_USERID = ?"
				+ " with ur");
	}

	static String getUserName(
		String userid,
		boolean isIRuserid,
		Map cache,
		PreparedStatement pstmt)
		throws SQLException {

		if (userid == null) {
			return "N.A.";
		}

		String userName = (String) cache.get(isIRuserid + userid);
		if (userName != null) {
			return userName;
		}

		ResultSet rs = null;
		try {
			if (isIRuserid) {
				pstmt.setString(1, userid);
				pstmt.setString(2, "<<DuMmY>>");
			} else {
				pstmt.setString(1, "<<DuMmY>>");
				pstmt.setString(2, userid);
			}

			rs = pstmt.executeQuery();
			if (rs.next()) {
				userName = rs.getString(1);
			}
		} finally {
			rs.close();
		}

		if (userName != null) {
			userName = userName.trim();
		} else {
			userName = userid;
		}

		cache.put(isIRuserid + userid, userName);
		return userName;
	}

	static PrintWriter getGZIPWriter(
		HttpServletRequest request,
		HttpServletResponse response)
		throws IOException {

		String encodings = request.getHeader("Accept-Encoding");

		if ((encodings != null) && (encodings.indexOf("gzip") != -1)) {

			if (encodings.indexOf("x-gzip") != -1)
				response.setHeader("Content-Encoding", "x-gzip");
			else
				response.setHeader("Content-Encoding", "gzip");

			return new PrintWriter(
				new GZIPOutputStream(response.getOutputStream()),
				false);

		} else {
			return response.getWriter();
		}

	}

	static OutputStream getGZIPOutputStream(
		HttpServletRequest request,
		HttpServletResponse response)
		throws IOException {

		String encodings = request.getHeader("Accept-Encoding");

		if ((encodings != null) && (encodings.indexOf("gzip") != -1)) {

			if (encodings.indexOf("x-gzip") != -1)
				response.setHeader("Content-Encoding", "x-gzip");
			else
				response.setHeader("Content-Encoding", "gzip");

			return new GZIPOutputStream(response.getOutputStream());

		} else {
			return response.getOutputStream();
		}

	}

	static String formatTime(Date date) {
		return timeFormatter.format(date) + "&nbsp;US&nbsp;Eastern";
	}

	static String formatDate(Date date) {
		return dateFormatter.format(date);
	}

	static String formatFileSize(int size) {
		if (size < CUTOFF_MB) {
			float f = (float) size / ONE_KB;
			return sizeFormatter.format(f) + " KB";
		} else {
			float f = (float) size / ONE_MB;
			return sizeFormatter.format(f) + " MB";
		}
	}

	static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		String stackTrace = sw.toString();
		pw.close();
		return stackTrace;
	}

	static String getShortStackTrace(Throwable t) {
		BufferedReader reader =
			new BufferedReader(new StringReader(getStackTrace(t)));

		StringBuffer shortStackTrace = new StringBuffer();
		int numLines = 5;
		String line = null;

		try {
			while ((line = reader.readLine()) != null && numLines-- > 0) {
				shortStackTrace.append(line);
				shortStackTrace.append('\n');
			}
			if (shortStackTrace.length() > 0) {
				shortStackTrace.deleteCharAt(shortStackTrace.length() - 1);
			}
		} catch (IOException ioe) {
			// dummy catch - should never happen
		}

		return shortStackTrace.toString();
	}

	static String getMailHost() {
		return mailHost;
	}

	private ETSSearchCommon(
		String subject,
		String text,
		InternetAddress[] mailTo,
		Class caller) {

		this.subject = caller.getName() + ":" + hostName + ":" + subject;
		this.text = text;
		this.mailTo = (mailTo != null ? mailTo : defaultMailTo);
	}

	static void sendMail(String subject, String text, Class caller) {
		sendMail(subject, text, defaultMailTo, caller);
	}

	static void sendMail(
		String subject,
		String text,
		Throwable t,
		Class caller) {

		sendMail(subject, text, t, defaultMailTo, caller);
	}

	static void sendMail(
		String subject,
		String text,
		Throwable t,
		InternetAddress[] mailTo,
		Class caller) {

		sendMail(
			subject,
			subject + ":" + text + "\n\n" + getStackTrace(t),
			mailTo,
			caller);
	}

	static void sendMail(
		String subject,
		String text,
		String mailToStr,
		Class caller)
		throws AddressException {

		InternetAddress[] mailTo = InternetAddress.parse(mailToStr, false);
		sendMail(subject, text, mailTo, caller);
	}

	static void sendMail(
		String subject,
		String text,
		InternetAddress[] mailTo,
		Class caller) {

		new ETSSearchCommon(subject, text, mailTo, caller).start();
	}

	public void run() {

		try {

			if (mailHost == null) {
				mailHost = getGwaProperty("gwa.mailHost", null);
			}

			Properties props = new Properties();
			props.put(
				"mail.smtp.host",
				(mailHost != null ? mailHost : defaultMailHost));

			javax.mail.Session session =
				javax.mail.Session.getDefaultInstance(props, null);

			MimeMessage msg = new MimeMessage(session);

			msg.setFrom(mailFrom);
			msg.setRecipients(Message.RecipientType.TO, mailTo);
			msg.setSubject(subject);
			msg.setText(text);

			Transport transport = session.getTransport("smtp");
			transport.connect();
			Transport.send(msg);
			transport.close();

		} catch (Throwable t) {
			log.error("Exception sending mail with subject: " + subject, t);
		}

	}
}
