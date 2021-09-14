package oem.edge.ed.odc.cntl;

import com.ibm.as400.webaccess.common.*;
import oem.edge.common.cipher.*;
import javax.servlet.http.*;
import java.net.*;
import java.util.*;
import java.io.*;

import oem.edge.ed.odc.applet.InstallAndLaunchApp;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2000-2006                                     */
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

/**
 * Insert the type's description here.
 * Creation date: (9/14/2001 2:57:28 PM)
 * @author: Mike Zarnick
 */
public class HelperInstall extends javax.servlet.http.HttpServlet {
	static final String headerTitleFront = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
          "<html  xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n<head>\n"+
          "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />\n" +
          "<meta http-equiv=\"PICS-Label\" content='(PICS-1.1 \"http://www.icra.org/ratingsv02.html\" l gen true r (cz 1 lz 1 nz 1 oz 1 vz 1) \"http://www.rsac.org/ratingsv01.html\" l gen true r (n 0 s 0 v 0 l 0) \"http://www.classify.org/safesurf/\" l gen true r (SS~~000 1))' />\n" +
          "<link rel=\"schema.DC\" href=\"http://purl.org/DC/elements/1.0/\"/>\n" +
          "<link rel=\"SHORTCUT ICON\" href=\"http://www.ibm.com/favicon.ico\"/>\n" +
          "<meta name=\"SECURITY\" content=\"Public\" />\n" +
          "<meta name=\"KEYWORDS\" content=\"OEM, IBM Corporation, Technology Group\" />\n" +
          "<meta name=\"SOURCE\" content=\"manual creation\" />\n" +
          "<meta name=\"DC.Rights\" content=\"Copyright (c) 2002 by IBM Corporation\" />\n" +
          "<meta name=\"Robots\" content=\"noindex,nofollow,noarchive\" />\n" +
          "<meta name=\"DC.LANGUAGE\" scheme=\"rfc1766\" content=\"en-US\" />\n" +
          "<meta name=\"CHARSET\" content=\"iso88591\" />\n" +
          "<meta name=\"IBM.COUNTRY\" content=\"us\" />\n" +
          "<meta name=\"DC.DATE\" scheme=\"iso8601\" content=\"2005-07-16\" />\n" +
          "<meta name=\"OWNER\"  content=\"econnect@us.ibm.com\" />\n" +
          "<meta name=\"DESCRIPTION\" content=\"Client Software for Customer Connect installation applet\" />\n" +
          "<meta name=\"ABSTRACT\" content=\"Client Software for Customer Connect installation applet\" />\n" +
          "<meta name=\"DC.Type\" scheme=\"IBM_ContentClassTaxonomy\" content=\"TS100\" />\n" +
          "<meta name=\"DC.Subject\" scheme=\"IBM_SubjectTaxonomy\" content=\"1018529, 56982\" />\n" +
          "<meta name=\"DC.Publisher\" content=\"IBM Corporation\" />\n" +
          "<meta name=\"IBM.Effective\" scheme=\"W3CDTF\" content=\"2004-12-04\" />\n" +
          "<meta name=\"IBM.Industry\" scheme=\"IBM_IndustryTaxonomy\" content=\"K, Y, BA\" />\n<title>IBM ";
	static final String headerTitleBack = "</title>\n<link rel=\"stylesheet\" type=\"text/css\" href=\"//www.ibm.com/common/v14/main.css\" />\n" +
          "<link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"//www.ibm.com/common/v14/screen.css\" />\n" +
          "<link rel=\"stylesheet\" type=\"text/css\" media=\"print\" href=\"//www.ibm.com/common/v14/print.css\" />\n" +
          "<link rel=\"stylesheet\" type=\"text/css\" href=\"//www.ibm.com/common/v14/popup.css\" />\n" +
          "<script src=\"//www.ibm.com/common/v14/detection.js\" language=\"JavaScript\" type=\"text/javascript\">\n" +
          "</script></head>\n" +
          "<body>\n<!-- MASTHEAD_BEGIN -->\n" +
          "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" id=\"v14-pop-mast\" width=\"100%\">\n" +
          "<tr>\n<td class=\"bbg\"><img alt=\"IBM&reg;\" border=\"0\" height=\"30\" src=\"//www.ibm.com/i/v14/t/ibm-logo-small.gif\" width=\"55\"/></td>\n" +
          "<td align=\"right\" class=\"bbg\">&nbsp;</td>\n</tr>\n" +
          "<tr>\n<td class=\"lgray\" colspan=\"2\"><img alt=\"\" height=\"2\" src=\"//www.ibm.com/i/c.gif\" width=\"1\"/></td>\n</tr>\n" +
          "</table>\n<!-- MASTHEAD_END -->\n" +
          "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">" +
          "<tbody>\n<tr>\n<td valign=\"middle\">\n<h3>";
	static final String headerBackEnd = "</h3>\n</td>\n<td align=\"right\" valign=\"top\">\n" +
		"<table>\n<tbody>\n<tr>\n" +
		"<td><a href=\"javascript:history.back();\">" +
		"<img src=\"//www.ibm.com/i/v14/buttons/arrow_lt.gif\" border=\"0\" alt=\"Back\" width=\"21\" height=\"21\" />" +
		"</a></td>\n" +
		"<td><a href=\"javascript:history.back();\">Back</a></td>\n" +
		"</tr>\n</tbody>\n</table>\n</td>\n</tr>\n</tbody>\n</table>";
	static final String headerNoBackEnd = "</h3>\n</td>\n</tr>\n</tbody>\n</table>";
	static final String unpack = "<p><b>Step 2:</b> Unpack the Java Runtime Environment (JRE) in the same directory where you\n" +
		"saved it. A new subdirectory, named <b>jre</b>, will be created. This <b>jre</b> subdirectory\n" +
		"needs to be renamed as specified by the following table:</p><table cellpadding=\"2\" border=\"1\">\n" +
		"<tbody><tr><td>JRE Platform</td><td>File Name</td><td>Rename <b>jre</b> to:</td></tr>\n" +
		"<tr><td>JRE for Windows&reg;</td><td>" + InstallAndLaunchApp.WIN_FILE + "</td><td>WINJRE</td></tr>\n" +
		"<tr><td>JRE for AIX</td><td>" + InstallAndLaunchApp.AIX_GZFILE + "</td><td>AIXJRE</td></tr>\n" +
		"<tr><td>JRE for Linux&reg; x86</td><td>" + InstallAndLaunchApp.LINUX_GZFILE + "</td><td>LINJRE</td></tr>\n" +
		"</tbody></table>\n" +
		"<p>For Windows&reg;, use any zip tool to unpack the odc-win-x86.zip file. On all UNIX&reg; systems, use:</p>\n" +
		"<pre>\n  gunzip -c &lt;file&gt; | tar -xvf -\n</pre>\n" +
		"<p>where &lt;file&gt; is the name of the JRE tar.gz file that you downloaded.</p>";
	static final String inifileBegin = "<p><b>Step 3:</b> Create a file named <b><i>edesign.ini</i></b> in the same directory where\n" +
		"you stored the software. Based on your completed downloads, it would contain the following statements:</p>";
        static final String endMatter = "<!-- FOOTER_BEGIN -->\n<br />\n" +
                "<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "<tr class=\"bbg\">\n<td height=\"19\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n<tr>\n" +
                "<td><span class=\"spacer\">&nbsp;&nbsp;&nbsp;&nbsp;</span><a class=\"ur-link\" href=\"http://www.ibm.com/legal/\" target=\"_blank\">Terms of use</a></td>\n"+
                "<td class=\"footer-divider\" width=\"27\">&nbsp;&nbsp;&nbsp;&nbsp;</td>\n" +
                "<td><a class=\"ur-link\" href=\"http://www.ibm.com/privacy/\" target=\"_blank\">Privacy</a><span class=\"spacer\">&nbsp;&nbsp;&nbsp;&nbsp;</span></td>\n" +
                "</tr>\n</table>\n" +
                "</td>\n<td align=\"right\"><a class=\"mainlink\" href=\"javascript:window.close()\">Close [x]</a><span class=\"spacer\">&nbsp;&nbsp;</span></td>\n" +
                "</tr>\n</table>\n<!-- FOOTER_END -->\n" +
                "<script type=\"text/javascript\" language=\"JavaScript1.2\" src=\"//www.ibm.com/common/stats/stats.js\"></script>\n" +
                "<noscript><img src=\"//stats.www.ibm.com/rc/images/uc.GIF?R=noscript\" width=\"1\" height=\"1\" alt=\"\" border=\"0\"/></noscript>\n" +
                "</body>\n</html>";

/**
 * Present instructions for step 2.
 * 
 * @param request Object that encapsulates the request to the servlet 
 * @param response Object that encapsulates the response from the servlet
 */
public void configure(HttpServletRequest request, HttpServletResponse response) throws Exception {
	// Get the session object (if any).
	HttpSession session = request.getSession(true);

	// set content-type and get writer.
	response.setContentType("text/html");
	PrintWriter out = response.getWriter();

	// Write out header...
	out.print(headerTitleFront);
	out.print((String) session.getAttribute("TOKEN"));
	out.print(headerTitleBack);
	out.print("Client Software for Customer Connect<br />Manual installation");
	out.println(headerBackEnd);

	// Write out the configure content...
	out.println("<p><b>Step 4:</b> Configure your browser to launch the Client Software for");
	out.println("Customer Connect by selecting the launch button below.</p><p>When selected, your");
	out.println("browser will ask you to choose to save or open the file (opening the file");
	out.println("may be presented as picking an application to run). Choose the browser");
	out.println("option to open the file. Select the startds program you downloaded as the");
	out.println("application to use to open the file.</p><p>The browser dialogs used to identify");
	out.println("the application may also include check boxes to indicate that the browser");
	out.println("should always use this application to open the file. Indicate that the");
	out.println("browser should always use this application to have it automatically");
	out.println("launch the Client Software for Customer Connect in the future.</p>");
	out.println("<p><b>Attention</b> Microsoft Internet Explorer users: you must");
	out.println("turn off the <i>Do not save encrypted pages to disk</i> option.");
	out.println("When enabled, this option prevents you from selecting <i>open the file</i>");
	out.println("as described above. To turn off this option, select the");
	out.println("<b>Tools-&gt;Internet Options...</b> menu item, then select the");
	out.println("<b>Advanced</b> tab. In the <b>Security</b> section, make sure that the");
	out.println("<i>Do not save encrypted pages to disk</i> box is <b>NOT</b> checked.</p>");

	// Rebuild the url components and rewrap the token.,,
	String servlet = (String) session.getAttribute("LAUNCHSERVLET");
	Hashtable h = (Hashtable) session.getAttribute("QUERYSTRING");

	Enumeration e = h.keys();
	StringBuffer b = new StringBuffer();
	while (e.hasMoreElements()) {
		String parm = (String) e.nextElement();
		if (b.length() > 0)
			b.append('&');

		String value = (String) h.get(parm);

		if (parm.equals("compname") || parm.equals("token"))
			value = DesktopServlet.rewrapTokenWithTrust(value);

		b.append(parm);
		b.append('=');
		b.append(value);
	}

	String queryString = b.toString();

	// Write out the trailer...
	out.println("<table width=\"100%\" border=\"0\" cellspacing=\"0\"><tbody><tr><td>");
	out.print("<a href=\"");
	out.print(servlet);
	out.print("?");
	out.print(queryString);
	out.println("\"><img src=\"//www.ibm.com/i/v14/buttons/arrow_rd.gif\" border=\"0\" alt=\"continue\" width=\"21\" height=\"21\" /></a></td>");
	out.print("<td><a href=\"");
	out.print(servlet);
	out.print("?");
	out.print(queryString);
	out.println("\">Launch Client Software for Customer Connect</a></td>");
	out.println("</tr></tbody></table>");
	out.println(endMatter);
	out.close();
}
/**
 * Process incoming HTTP GET requests 
 * 
 * @param request Object that encapsulates the request to the servlet 
 * @param response Object that encapsulates the response from the servlet
 */
public void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, java.io.IOException {

	performTask(request, response);

}
/**
 * Process incoming HTTP POST requests 
 * 
 * @param request Object that encapsulates the request to the servlet 
 * @param response Object that encapsulates the response from the servlet
 */
public void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, java.io.IOException {

	performTask(request, response);

}
/**
 * Process download requests.
 * 
 * @param request Object that encapsulates the request to the servlet 
 * @param response Object that encapsulates the response from the servlet
 */
public void download(HttpServletRequest request, HttpServletResponse response) throws Exception {
	// Get the current session.
	HttpSession session = request.getSession(true);

	// Came here from the download page? Mark which are to be downloaded
	// and reset their declined licenses.
	if (request.getParameter("DSC") != null) {
		session.setAttribute("DSC","yes");
		resetLicense(session,"LICDSC");
	}
	if (request.getParameter("SCRIPT") != null) {
		session.setAttribute("SCRIPT","yes");
		resetLicense(session,"LICDSC");
	}
	if (request.getParameter("LDS") != null) {
		session.setAttribute("LDS","yes");
		resetLicense(session,"LICDSC");
	}
	if (request.getParameter("SOD") != null) {
		session.setAttribute("SOD","yes");
		resetLicense(session,"LICDSC");
	}
	if (request.getParameter("XML") != null) {
		session.setAttribute("XML","yes");
		resetLicense(session,"LICDSC");
	}
	if (request.getParameter("IM1") != null) {
		session.setAttribute("IM1","yes");
		resetLicense(session,"LICDSC");
	}
	if (request.getParameter("IM2") != null) {
		session.setAttribute("IM2","yes");
		resetLicense(session,"LICDSC");
	}
	if (request.getParameter("ODC") != null) {
		session.setAttribute("ODC","yes");
		resetLicense(session,"LICDSC");
	}
	if (request.getParameter("WIN1LIB") != null) {
		session.setAttribute("WIN1LIB","yes");
		resetLicense(session,"LICDSC");
	}
	if (request.getParameter("WIN2LIB") != null) {
		session.setAttribute("WIN2LIB","yes");
		resetLicense(session,"LICDSC");
	}
	if (request.getParameter("AIXLIB") != null) {
		session.setAttribute("AIXLIB","yes");
		resetLicense(session,"LICDSC");
	}
	if (request.getParameter("SUN1LIB") != null) {
		session.setAttribute("SUN1LIB","yes");
		resetLicense(session,"LICDSC");
	}
	if (request.getParameter("SUN2LIB") != null) {
		session.setAttribute("SUN2LIB","yes");
		resetLicense(session,"LICDSC");
	}
	if (request.getParameter("LINLIB") != null) {
		session.setAttribute("LINLIB","yes");
		resetLicense(session,"LICDSC");
	}
	if (request.getParameter("HPUX1LIB") != null) {
		session.setAttribute("HPUX1LIB","yes");
		resetLicense(session,"LICDSC");
	}
	if (request.getParameter("HPUX2LIB") != null) {
		session.setAttribute("HPUX2LIB","yes");
		resetLicense(session,"LICDSC");
	}
	if (request.getParameter("ICA1") != null) {
		session.setAttribute("ICA1","yes");
		resetLicense(session,"LICICA");
	}
	if (request.getParameter("ICA2") != null) {
		session.setAttribute("ICA2","yes");
		resetLicense(session,"LICICA");
	}
	if (request.getParameter("ICA3") != null) {
		session.setAttribute("ICA3","yes");
		resetLicense(session,"LICICA");
	}
	if (request.getParameter("JREWIN") != null) {
		session.setAttribute("JREWIN","yes");
		resetLicense(session,"LICDSC");
	}
	if (request.getParameter("JREAIX") != null) {
		session.setAttribute("JREAIX","yes");
		resetLicense(session,"LICDSC");
	}
	if (request.getParameter("JRELINUX") != null) {
		session.setAttribute("JRELINUX","yes");
		resetLicense(session,"LICDSC");
	}
	if (request.getParameter("LAUNCHWIN") != null) {
		session.setAttribute("LAUNCHWIN","yes");
		resetLicense(session,"LICDSC");
	}
	if (request.getParameter("LAUNCHUNIX") != null) {
		session.setAttribute("LAUNCHUNIX","yes");
		resetLicense(session,"LICDSC");
	}

	// Determine if a license response is inbound...
	String LICENSE = request.getParameter("LICENSE");
	if (LICENSE != null) {
//		if (request.getParameter("accept.x") != null)
		String accept = request.getParameter("ACCEPT");
		if (accept != null && accept.equals("YES"))
			session.setAttribute(LICENSE,"accept");
		else
			session.setAttribute(LICENSE,"decline");
	}

	// Get the appropriate attributes from the session:
	String DSC = (String) session.getAttribute("DSC");
	String SCRIPT = (String) session.getAttribute("SCRIPT");
	String LDS = (String) session.getAttribute("LDS");
	String SOD = (String) session.getAttribute("SOD");
	String XML = (String) session.getAttribute("XML");
	String IM1 = (String) session.getAttribute("IM1");
	String IM2 = (String) session.getAttribute("IM2");
	String ODC = (String) session.getAttribute("ODC");
	String WIN1LIB = (String) session.getAttribute("WIN1LIB");
	String WIN2LIB = (String) session.getAttribute("WIN2LIB");
	String AIXLIB = (String) session.getAttribute("AIXLIB");
	String SUN1LIB = (String) session.getAttribute("SUN1LIB");
	String SUN2LIB = (String) session.getAttribute("SUN2LIB");
	String LINLIB = (String) session.getAttribute("LINLIB");
	String HPUX1LIB = (String) session.getAttribute("HPUX1LIB");
	String HPUX2LIB = (String) session.getAttribute("HPUX2LIB");
	String ICA1 = (String) session.getAttribute("ICA1");
	String ICA2 = (String) session.getAttribute("ICA2");
	String ICA3 = (String) session.getAttribute("ICA3");
	String WIN = (String) session.getAttribute("JREWIN");
	String AIX = (String) session.getAttribute("JREAIX");
	String LINUX = (String) session.getAttribute("JRELINUX");
	String LWIN = (String) session.getAttribute("LAUNCHWIN");
	String LUNIX = (String) session.getAttribute("LAUNCHUNIX");
	String LICDSC = (String) session.getAttribute("LICDSC");
	String LICICA = (String) session.getAttribute("LICICA");

	// Determine if licensing needs to be handled.
	String licFile;
	String title;
	String licType;
	if (DSC != null && ! DSC.equals("done") && LICDSC == null) {
		licFile = "internal.lic";
		licType = "LICDSC";
		title = "Customer Connect Client<br />License Agreement";
	}
	else if (SCRIPT != null && ! SCRIPT.equals("done") && LICDSC == null) {
		licFile = "internal.lic";
		licType = "LICDSC";
		title = "Customer Connect Client<br />License Agreement";
	}
	else if (LDS != null && ! LDS.equals("done") && LICDSC == null) {
		licFile = "internal.lic";
		licType = "LICDSC";
		title = "Customer Connect Client<br />License Agreement";
	}
	else if (SOD != null && ! SOD.equals("done") && LICDSC == null) {
		licFile = "internal.lic";
		licType = "LICDSC";
		title = "Customer Connect Client<br />License Agreement";
	}
	else if (XML != null && ! XML.equals("done") && LICDSC == null) {
		licFile = "internal.lic";
		licType = "LICDSC";
		title = "Customer Connect Client<br />License Agreement";
	}
	else if (IM1 != null && ! IM1.equals("done") && LICDSC == null) {
		licFile = "internal.lic";
		licType = "LICDSC";
		title = "Customer Connect Client<br />License Agreement";
	}
	else if (IM2 != null && ! IM2.equals("done") && LICDSC == null) {
		licFile = "internal.lic";
		licType = "LICDSC";
		title = "Customer Connect Client<br />License Agreement";
	}
	else if (ODC != null && ! ODC.equals("done") && LICDSC == null) {
		licFile = "internal.lic";
		licType = "LICDSC";
		title = "Customer Connect Client<br />License Agreement";
	}
	else if (WIN1LIB != null && ! WIN1LIB.equals("done") && LICDSC == null) {
		licFile = "internal.lic";
		licType = "LICDSC";
		title = "Customer Connect Client<br />License Agreement";
	}
	else if (WIN2LIB != null && ! WIN2LIB.equals("done") && LICDSC == null) {
		licFile = "internal.lic";
		licType = "LICDSC";
		title = "Customer Connect Client<br />License Agreement";
	}
	else if (AIXLIB != null && ! AIXLIB.equals("done") && LICDSC == null) {
		licFile = "internal.lic";
		licType = "LICDSC";
		title = "Customer Connect Client<br />License Agreement";
	}
	else if (SUN1LIB != null && ! SUN1LIB.equals("done") && LICDSC == null) {
		licFile = "internal.lic";
		licType = "LICDSC";
		title = "Customer Connect Client<br />License Agreement";
	}
	else if (SUN2LIB != null && ! SUN2LIB.equals("done") && LICDSC == null) {
		licFile = "internal.lic";
		licType = "LICDSC";
		title = "Customer Connect Client<br />License Agreement";
	}
	else if (LINLIB != null && ! LINLIB.equals("done") && LICDSC == null) {
		licFile = "internal.lic";
		licType = "LICDSC";
		title = "Customer Connect Client<br />License Agreement";
	}
	else if (HPUX1LIB != null && ! HPUX1LIB.equals("done") && LICDSC == null) {
		licFile = "internal.lic";
		licType = "LICDSC";
		title = "Customer Connect Client<br />License Agreement";
	}
	else if (HPUX2LIB != null && ! HPUX2LIB.equals("done") && LICDSC == null) {
		licFile = "internal.lic";
		licType = "LICDSC";
		title = "Customer Connect Client<br />License Agreement";
	}
	else if (ICA1 != null && ! ICA1.equals("done") && LICICA == null) {
		licFile = "odc-citrix.lic";
		licType = "LICICA";
		title = "Citrix ICA Client<br />License Agreement";
	}
	else if (ICA2 != null && ! ICA2.equals("done") && LICICA == null) {
		licFile = "odc-citrix.lic";
		licType = "LICICA";
		title = "Citrix ICA Client<br />License Agreement";
	}
	else if (ICA3 != null && ! ICA3.equals("done") && LICICA == null) {
		licFile = "odc-citrix.lic";
		licType = "LICICA";
		title = "Citrix ICA Client<br />License Agreement";
	}
	else if (WIN != null && ! WIN.equals("done") && LICDSC == null) {
		licFile = "internal.lic";
		licType = "LICDSC";
		title = "Customer Connect Client<br />License Agreement";
	}
	else if (AIX != null && ! AIX.equals("done") && LICDSC == null) {
		licFile = "internal.lic";
		licType = "LICDSC";
		title = "Customer Connect Client<br />License Agreement";
	}
	else if (LINUX != null && ! LINUX.equals("done") && LICDSC == null) {
		licFile = "internal.lic";
		licType = "LICDSC";
		title = "Customer Connect Client<br />License Agreement";
	}
	else if (LWIN != null && ! LWIN.equals("done") && LICDSC == null) {
		licFile = "internal.lic";
		licType = "LICDSC";
		title = "Customer Connect Client<br />License Agreement";
	}
	else if (LUNIX != null && ! LUNIX.equals("done") && LICDSC == null) {
		licFile = "internal.lic";
		licType = "LICDSC";
		title = "Customer Connect Client<br />License Agreement";
	}

	// No more licenses to present, ready to construct the download page.
	else {
		// set content-type and get writer.
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		// Write out license header...
		out.print(headerTitleFront);
		out.print((String) session.getAttribute("TOKEN"));
		out.print(headerTitleBack);
		out.print("Client Software for Customer Connect<br />Download page");
		out.println(headerBackEnd);

		// Write out the download content...
		out.println("<p>Please select each of the following links to download the files requested.</p>");
		out.println("<table border=\"0\"><tbody>");

		String servlet = request.getContextPath() + request.getServletPath() + "/getfile/";

		if (DSC != null && ! DSC.equals("done")) downloadComponent(out,"Customer Connect Client",response.encodeURL(servlet + InstallAndLaunchApp.DSC_FILE),LICDSC);
		if (SCRIPT != null && ! SCRIPT.equals("done")) downloadComponent(out,"CSDS dropboxftp",response.encodeURL(servlet + InstallAndLaunchApp.SCRIPT_FILE),LICDSC);
		if (LDS != null && ! LDS.equals("done")) downloadComponent(out,"CSDS cmdline",response.encodeURL(servlet + InstallAndLaunchApp.LDSSCRIPT_FILE),LICDSC);
		if (SOD != null && ! SOD.equals("done")) downloadComponent(out,"CSDS on-Demand",response.encodeURL(servlet + InstallAndLaunchApp.SOD_FILE),LICDSC);
		if (XML != null && ! XML.equals("done")) downloadComponent(out,"CSDS Software Download",response.encodeURL(servlet + InstallAndLaunchApp.XML_FILE),LICDSC);
		if (IM1 != null && ! IM1.equals("done")) downloadComponent(out,"CSDS Instant Messaging Pt 1",response.encodeURL(servlet + InstallAndLaunchApp.IM1_FILE),LICDSC);
		if (IM2 != null && ! IM2.equals("done")) downloadComponent(out,"CSDS Instant Messaging Pt 2",response.encodeURL(servlet + InstallAndLaunchApp.IM2_FILE),LICDSC);
		if (ODC != null && ! ODC.equals("done")) downloadComponent(out,"CSDS Application Sharing",response.encodeURL(servlet + InstallAndLaunchApp.DSMP_FILE),LICDSC);
		if (WIN1LIB != null && ! WIN1LIB.equals("done")) downloadComponent(out,"CSDS Scraper for Windows&reg; 1 of 2",response.encodeURL(servlet + InstallAndLaunchApp.WIN1_LIB),LICDSC);
		if (WIN2LIB != null && ! WIN2LIB.equals("done")) downloadComponent(out,"CSDS Scraper for Windows&reg; 2 of 2",response.encodeURL(servlet + InstallAndLaunchApp.WIN2_LIB),LICDSC);
		if (AIXLIB != null && ! AIXLIB.equals("done")) downloadComponent(out,"CSDS Scraper for AIX",response.encodeURL(servlet + InstallAndLaunchApp.AIX_LIB),LICDSC);
		if (SUN1LIB != null && ! SUN1LIB.equals("done")) downloadComponent(out,"CSDS Scraper for Solaris[tm] SPARC 1 of 2",response.encodeURL(servlet + InstallAndLaunchApp.SUN1_LIB),LICDSC);
		if (SUN2LIB != null && ! SUN2LIB.equals("done")) downloadComponent(out,"CSDS Scraper for Solaris[tm] SPARC 1 of 2",response.encodeURL(servlet + InstallAndLaunchApp.SUN2_LIB),LICDSC);
		if (LINLIB != null && ! LINLIB.equals("done")) downloadComponent(out,"CSDS Scraper for Linux&reg; x86",response.encodeURL(servlet + InstallAndLaunchApp.LIN_LIB),LICDSC);
		if (HPUX1LIB != null && ! HPUX1LIB.equals("done")) downloadComponent(out,"CSDS Scraper for HP-UX 1 of 2",response.encodeURL(servlet + InstallAndLaunchApp.HPUX1_LIB),LICDSC);
		if (HPUX2LIB != null && ! HPUX2LIB.equals("done")) downloadComponent(out,"CSDS Scraper for HP-UX 2 of 2",response.encodeURL(servlet + InstallAndLaunchApp.HPUX2_LIB),LICDSC);
		if (ICA1 != null && ! ICA1.equals("done")) downloadComponent(out,"Citrix ICA Client 1 of 3",response.encodeURL(servlet + InstallAndLaunchApp.ICA1_FILE),LICICA);
		if (ICA2 != null && ! ICA2.equals("done")) downloadComponent(out,"Citrix ICA Client 2 of 3",response.encodeURL(servlet + InstallAndLaunchApp.ICA2_FILE),LICICA);
		if (ICA3 != null && ! ICA3.equals("done")) downloadComponent(out,"Citrix ICA Client 3 of 3",response.encodeURL(servlet + InstallAndLaunchApp.ICA3_FILE),LICICA);
		if (WIN != null && ! WIN.equals("done")) downloadComponent(out,"JRE for Windows&reg;",response.encodeURL(servlet + InstallAndLaunchApp.WIN_FILE),LICDSC);
		if (AIX != null && ! AIX.equals("done")) downloadComponent(out,"JRE for AIX",response.encodeURL(servlet + InstallAndLaunchApp.AIX_GZFILE),LICDSC);
		if (LINUX != null && ! LINUX.equals("done")) downloadComponent(out,"JRE for Linux&reg; x86",response.encodeURL(servlet + InstallAndLaunchApp.LINUX_GZFILE),LICDSC);
		if (LWIN != null && ! LWIN.equals("done")) downloadComponent(out,"Launcher for Windows&reg;",response.encodeURL(servlet + InstallAndLaunchApp.LWIN_FILE),LICDSC);
		if (LUNIX != null && ! LUNIX.equals("done")) downloadComponent(out,"Launcher for all Unix Platforms",response.encodeURL(servlet + InstallAndLaunchApp.LUNIX_FILE),LICDSC);

		// Write out the download trailer...
		out.println("</tbody></table>");
		out.println("<p>When you have finished downloading the files, select Continue.</p>");
		out.print("<form method=\"post\" action=\"");
		out.print(response.encodeURL(request.getContextPath() + request.getServletPath()));
		out.println("\">");
		out.println("<input type=\"image\" src=\"//www.ibm.com/i/v14/buttons/us/en/continue.gif\" alt=\"Continue\" name=\"accept\" value=\"Continue\" />");
		out.println("</form>");
		out.println(endMatter);
		out.close();

		return;
	}

	// Construct the license text page.

	// set content-type and get writer.
	response.setContentType("text/html");
	PrintWriter out = response.getWriter();

	// Write out license header...
	out.print(headerTitleFront);
	out.print((String) session.getAttribute("TOKEN"));
	out.print(headerTitleBack);
	out.print("Client Software for Customer Connect<br />");
	out.print(title);
	out.println(headerBackEnd);
	out.println("<p><b>Please read and indicate your acceptance by selecting I Agree at the end of this License Agreement:</b></p><p>");

	// Get license text here and output to out...
	boolean gotLicenseText = false;
	try {
		String realFile = null;

		try {
			realFile = DesktopServlet.findFileInOurDirectories(licFile);
		}
		catch (Exception e) {
			System.out.println("HelperInstall.download: " + e.getMessage());
			e.printStackTrace();
		}

		FileInputStream lic = new FileInputStream(realFile);
		BufferedReader rdr = new BufferedReader(new InputStreamReader(lic));
		String line = rdr.readLine();
		boolean wasBlank = true;

		while (line != null) {
			line = prepText(line.trim());
			if (line.length() == 0) {
				if (! wasBlank) {
					out.println("</p><p>");
					wasBlank = true;
				}
			}
			else {
				if (! wasBlank) out.println("<br />");
				out.println(line);
				wasBlank = false;
			}
			line = rdr.readLine();
		}
		rdr.close();
		gotLicenseText = true;
	}
	catch (Exception e) {
		out.println("Internal Error: " + e.getMessage());
		e.printStackTrace(out);
	}

	// Write out license buttons...
	out.println("</p><table width=\"100%\" border=\"0\" cellspacing=\"2\"><tbody>");
	out.println("<tr><td align=\"right\"><table border=\"0\"><tbody>");
	if (gotLicenseText) {
		out.println("<tr><td>");
		out.print("<a href=\"");
		out.print(response.encodeURL(request.getContextPath() + request.getServletPath() + "?REQUEST=DOWNLOAD&ACCEPT=YES&LICENSE=" + licType));
		out.println("\"><img src=\"//www.ibm.com/i/v14/buttons/arrow_rd.gif\" border=\"0\" alt=\"accept\" width=\"21\" height=\"21\" /></a></td>");
		out.print("<td><a href=\"");
		out.print(response.encodeURL(request.getContextPath() + request.getServletPath() + "?REQUEST=DOWNLOAD&ACCEPT=YES&LICENSE=" + licType));
		out.println("\">I Agree</a></td></tr>");
	}
	out.println("<tr><td>");
	out.print("<a href=\"");
	out.print(response.encodeURL(request.getContextPath() + request.getServletPath() + "?REQUEST=DOWNLOAD&ACCEPT=NO&LICENSE=" + licType));
	out.println("\"><img src=\"//www.ibm.com/i/v14/buttons/cancel_rd.gif\" border=\"0\" alt=\"decline\" width=\"21\" height=\"21\" /></a></td>");
	out.print("<td><a href=\"");
	out.print(response.encodeURL(request.getContextPath() + request.getServletPath() + "?REQUEST=DOWNLOAD&ACCEPT=NO&LICENSE=" + licType));
	out.println("\">I Do Not Agree</a></td></tr>");
	out.println("</tbody></table></td></tr></tbody></table>");
	out.println(endMatter);

	out.close();
}
public String prepText(String line) {
	int i = 0;

	while ((i = line.indexOf('&',i)) != -1) {
		line = line.substring(0,i) + "&amp;" + line.substring(i+1);
		i++; // Added in our own &.
	}

	i = 0;
	while ((i = line.indexOf('<',i)) != -1) {
		line = line.substring(0,i) + "&lt;" + line.substring(i+1);
	}

	i = 0;
	while ((i = line.indexOf('>',i)) != -1) {
		line = line.substring(0,i) + "&gt;" + line.substring(i+1);
	}

	return line;
}
/**
 * Insert the method's description here.
 * Creation date: (9/18/2001 2:00:18 PM)
 * @param name java.lang.String
 * @param file java.lang.String
 * @param licensed java.lang.String
 * @exception java.lang.Exception The exception description.
 */
public void downloadComponent(PrintWriter out, String name, String file, String licensed) throws Exception {
	if (licensed.equals("accept")) {
		out.print("<tr><td><a href=\"");
		out.print(file);
		out.println("\">");
		out.print("<img src=\"//www.ibm.com/i/v14/buttons/download_now_rd.gif\" border=\"0\" alt=\"");
		out.print(name);
		out.println("\" width=\"21\" height=\"21\" /></a></td>");
		out.print("<td><a href=\"");
		out.print(file);
		out.print("\">");
		out.print(name);
		out.print("</a></td></tr>");
	}
	else {
		out.print("<tr><td></td><td>");
		out.print(name);
		out.print(" (Declined Terms)</td></tr>");
	}
}
/**
 * Return Front Installation Page.
 * 
 * @param request Object that encapsulates the request to the servlet 
 * @param response Object that encapsulates the response from the servlet
 */
public void frontPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
	// Get the session object (if any).
	HttpSession session = request.getSession(true);

	// set content-type and get writer.
	response.setContentType("text/html");
	PrintWriter out = response.getWriter();

	// Write out page header and front matter.
	out.print(headerTitleFront);
	out.print((String) session.getAttribute("TOKEN"));
	out.print(headerTitleBack);
	out.print("Client Software for Customer Connect<br />Manual installation");
	out.println(headerNoBackEnd);
	sendDownloadBegin(out,response.encodeURL(request.getContextPath() + request.getServletPath()));

	// Get the appropriate attributes from the session:
	String DSC = (String) session.getAttribute("DSC");
	String SCRIPT = (String) session.getAttribute("SCRIPT");
	String LDS = (String) session.getAttribute("LDS");
	String SOD = (String) session.getAttribute("SOD");
	String XML = (String) session.getAttribute("XML");
	String IM1 = (String) session.getAttribute("IM1");
	String IM2 = (String) session.getAttribute("IM2");
	String ODC = (String) session.getAttribute("ODC");
	String WIN1LIB = (String) session.getAttribute("WIN1LIB");
	String WIN2LIB = (String) session.getAttribute("WIN2LIB");
	String AIXLIB = (String) session.getAttribute("AIXLIB");
	String SUN1LIB = (String) session.getAttribute("SUN1LIB");
	String SUN2LIB = (String) session.getAttribute("SUN2LIB");
	String LINLIB = (String) session.getAttribute("LINLIB");
	String HPUX1LIB = (String) session.getAttribute("HPUX1LIB");
	String HPUX2LIB = (String) session.getAttribute("HPUX2LIB");
	String ICA1 = (String) session.getAttribute("ICA1");
	String ICA2 = (String) session.getAttribute("ICA2");
	String ICA3 = (String) session.getAttribute("ICA3");
	String WIN = (String) session.getAttribute("JREWIN");
	String AIX = (String) session.getAttribute("JREAIX");
	String LINUX = (String) session.getAttribute("JRELINUX");
	String LWIN = (String) session.getAttribute("LAUNCHWIN");
	String LUNIX = (String) session.getAttribute("LAUNCHUNIX");

	// Check browser's platform and build software choices.
	String ua = request.getHeader("user-agent").toLowerCase();
	boolean isWin = ua.indexOf("win") != -1;
	boolean isAIX = ua.indexOf("aix") != -1;
	boolean isSun = ua.indexOf("sunos") != -1;
	boolean isLinux = ua.indexOf("inux") != -1;
	boolean isHPUX = ua.indexOf("hpux") != -1;

	out.println("<tr><td>Client software:</td></tr>\n<tr><td>");
	out.println("<table cellpadding=\"0\" cellspacing=\"0\"><tbody>");

	// Layout a check box for each of the downloadable items.
	formatDownloadableItem(out,DSC,"DSC","Client Software for Customer Connect",true);
	formatDownloadableItem(out,SCRIPT,"SCRIPT","CSDS dropboxftp (co-req)",isWin);
	formatDownloadableItem(out,LDS,"LDS","CSDS cmdline (co-req)",isWin);
	formatDownloadableItem(out,SOD,"SOD","CSDS on-Demand (co-req)",true);
	formatDownloadableItem(out,XML,"XML","CSDS software download (co-req)",true);
	formatDownloadableItem(out,IM1,"IM1","CSDS instant messaging part 1 (co-req)",true);
	formatDownloadableItem(out,IM2,"IM2","CSDS instant messaging part 2 (co-req)",true);
	formatDownloadableItem(out,ODC,"ODC","CSDS application sharing (co-req)",true);
	formatDownloadableItem(out,ICA1,"ICA1","ICA client 1 of 3 (co-req)",true);
	formatDownloadableItem(out,ICA2,"ICA2","ICA client 2 of 3 (co-req)",true);
	formatDownloadableItem(out,ICA3,"ICA3","ICA client 3 of 3 (co-req)",true);
	formatDownloadableItem(out,WIN1LIB,"WIN1LIB","Scraper for Windows&reg; 1 of 2",isWin);
	formatDownloadableItem(out,WIN2LIB,"WIN2LIB","Scraper for Windows&reg; 2 of 2",isWin);
	formatDownloadableItem(out,AIXLIB,"AIXLIB","Scraper for AIX",isAIX);
	formatDownloadableItem(out,SUN1LIB,"SUN1LIB","Scraper for Solaris[tm] SPARC 1 of 2",isSun);
	formatDownloadableItem(out,SUN2LIB,"SUN2LIB","Scraper for Solaris[tm] SPARC 2 of 2",isSun);
	formatDownloadableItem(out,LINLIB,"LINLIB","Scraper for Linux&reg; x86",isLinux);
	formatDownloadableItem(out,HPUX1LIB,"HPUX1LIB","Scraper for HP-UX&reg; 1 of 2",isHPUX);
	formatDownloadableItem(out,HPUX2LIB,"HPUX2LIB","Scraper for HP-UX&reg; 2 of 2",isHPUX);

	out.println("</tbody></table>\n</td></tr>\n<tr><td>Java Runtime Environment - JRE: (pre-req)</td></tr>");
	out.println("<tr><td>\n<table cellpadding=\"0\" cellspacing=\"0\"><tbody>");

	formatDownloadableItem(out,WIN,"JREWIN","JRE for Windows&reg;",isWin);
	formatDownloadableItem(out,AIX,"JREAIX","JRE for AIX",isAIX);
	formatDownloadableItem(out,LINUX,"JRELINUX","JRE for Linux&reg; x86",isLinux);

	out.println("</tbody></table>\n</td></tr>\n<tr><td>Launch software: (pre-req)</td></tr>");
	out.println("<tr><td>\n<table cellpadding=\"0\" cellspacing=\"0\"><tbody>");

	formatDownloadableItem(out,LWIN,"LAUNCHWIN","Launcher for Windows&reg;",isWin);
	formatDownloadableItem(out,LUNIX,"LAUNCHUNIX","Launcher for all UNIX&reg; platforms",! isWin);

	// Write out page trailer.
	sendDownloadEnd(out,response.encodeURL(request.getContextPath() + request.getServletPath() + "?REQUEST=UNPACK"));
	out.close();
}
public void formatDownloadableItem(PrintWriter out, String status, String name, String label, boolean needed) {
	out.print("<tr><td><label for=\"" + name + "\"><input type=\"checkbox\" name=\"" + name + "\" value=\"yes\"");

	boolean done = status != null && status.equals("done");

	if (! done && needed) {
		out.print(" checked=\"checked\"");
	}

	out.print(" />" + label);

	if (done) {
		out.print(" - <b>Downloaded</b>");
	}

	out.println("</label></td></tr>");
}
/**
 * Process download requests.
 * 
 * @param request Object that encapsulates the request to the servlet 
 * @param response Object that encapsulates the response from the servlet
 */
public void getFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
	// Get the current session.
	HttpSession session = request.getSession(true);

	// Get the file requested.
	String path = request.getPathInfo();
	String file = path.substring(9); // chop off "/getfile/"

	// Get the file and license type.
	String licType;
	String fileType;
	if (file.equals(InstallAndLaunchApp.DSC_FILE)) {
		licType = "LICDSC";
		fileType = "DSC";
	}
	else if (file.equals(InstallAndLaunchApp.SCRIPT_FILE)) {
		licType = "LICDSC";
		fileType = "SCRIPT";
	}
	else if (file.equals(InstallAndLaunchApp.LDSSCRIPT_FILE)) {
		licType = "LICDSC";
		fileType = "LDS";
	}
	else if (file.equals(InstallAndLaunchApp.SOD_FILE)) {
		licType = "LICDSC";
		fileType = "SOD";
	}
	else if (file.equals(InstallAndLaunchApp.XML_FILE)) {
		licType = "LICDSC";
		fileType = "XML";
	}
	else if (file.equals(InstallAndLaunchApp.IM1_FILE)) {
		licType = "LICDSC";
		fileType = "IM1";
	}
	else if (file.equals(InstallAndLaunchApp.IM2_FILE)) {
		licType = "LICDSC";
		fileType = "IM2";
	}
	else if (file.equals(InstallAndLaunchApp.DSMP_FILE)) {
		licType = "LICDSC";
		fileType = "ODC";
	}
	else if (file.equals(InstallAndLaunchApp.WIN1_LIB)) {
		licType = "LICDSC";
		fileType = "WIN1LIB";
	}
	else if (file.equals(InstallAndLaunchApp.WIN2_LIB)) {
		licType = "LICDSC";
		fileType = "WIN2LIB";
	}
	else if (file.equals(InstallAndLaunchApp.AIX_LIB)) {
		licType = "LICDSC";
		fileType = "AIXLIB";
	}
	else if (file.equals(InstallAndLaunchApp.SUN1_LIB)) {
		licType = "LICDSC";
		fileType = "SUN1LIB";
	}
	else if (file.equals(InstallAndLaunchApp.SUN2_LIB)) {
		licType = "LICDSC";
		fileType = "SUN2LIB";
	}
	else if (file.equals(InstallAndLaunchApp.LIN_LIB)) {
		licType = "LICDSC";
		fileType = "LINLIB";
	}
	else if (file.equals(InstallAndLaunchApp.HPUX1_LIB)) {
		licType = "LICDSC";
		fileType = "HPUX1LIB";
	}
	else if (file.equals(InstallAndLaunchApp.HPUX2_LIB)) {
		licType = "LICDSC";
		fileType = "HPUX2LIB";
	}
	else if (file.equals(InstallAndLaunchApp.ICA1_FILE)) {
		licType = "LICICA";
		fileType = "ICA1";
	}
	else if (file.equals(InstallAndLaunchApp.ICA2_FILE)) {
		licType = "LICICA";
		fileType = "ICA2";
	}
	else if (file.equals(InstallAndLaunchApp.ICA3_FILE)) {
		licType = "LICICA";
		fileType = "ICA3";
	}
	else if (file.equals(InstallAndLaunchApp.WIN_FILE)) {
		licType = "LICDSC";
		fileType = "JREWIN";
	}
	else if (file.equals(InstallAndLaunchApp.AIX_GZFILE)) {
		licType = "LICDSC";
		fileType = "JREAIX";
	}
	else if (file.equals(InstallAndLaunchApp.LINUX_GZFILE)) {
		licType = "LICDSC";
		fileType = "JRELINUX";
	}
	else if (file.equals(InstallAndLaunchApp.LWIN_FILE)) {
		licType = "LICDSC";
		fileType = "LAUNCHWIN";
	}
	else if (file.equals(InstallAndLaunchApp.LUNIX_FILE)) {
		licType = "LICDSC";
		fileType = "LAUNCHUNIX";
	}

	// File is not supported, return a failure page.
	else {
		// set content-type and get writer.
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		// Write out the header...
		out.print(headerTitleFront);
		out.print((String) session.getAttribute("TOKEN"));
		out.print(headerTitleBack);
		out.print("Client Software for Customer Connect<br />Internal error!");
		out.println(headerBackEnd);

		// Write out the message...
		out.println("<p>The file requested has not been defined.</p>");

		// Write out the trailer...
		out.print("<form method=\"post\" action=\"");
		out.print(response.encodeURL(request.getContextPath() + request.getServletPath()));
		out.println("\">");
		out.println("<input type=\"image\" src=\"//www.ibm.com/i/v14/buttons/us/en/continue.gif\" alt=\"Continue\" name=\"accept\" value=\"Continue\" />");
		out.println("<input type=\"hidden\" name=\"REQUEST\" value=\"DOWNLOAD\">");
		out.println("</form>");
		out.println(endMatter);
		out.close();

		return;
	}

	// Confirm that the license has been agreed to...
	String license = (String) session.getAttribute(licType);

	if (license != null && license.equals("accept")) {
		// Get a URL to the file.
		File f = null;

		try {
			String realFile = DesktopServlet.findFileInOurDirectories(file);
			f = new File(realFile);
		}
		catch (Exception e) {
			System.out.println("HelperInstall.getFile: " + e.getMessage());
			e.printStackTrace();
		}

		if (f.exists()) {
			// set content-type, content-length and get output stream.
			response.setContentType("application/octet-stream");
			long fl = f.length();
			if (fl >= 0 && fl <= Integer.MAX_VALUE) {
				int x = (int) fl;
				response.setContentLength(x);
			}

			FileInputStream in = new FileInputStream(f);
			OutputStream out = response.getOutputStream();

			byte[] b = new byte[1024];
			int l;
			while ((l = in.read(b,0,1024)) != -1)
				out.write(b,0,l);

			in.close();
			out.close();

			// This file is donwloaded.
			session.setAttribute(fileType,"done");
		}
		else {
			// set content-type and get writer.
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();

			// Write out the header...
			out.print(headerTitleFront);
			out.print((String) session.getAttribute("TOKEN"));
			out.print(headerTitleBack);
			out.print("Client Software for Customer Connect<br />File not found!");
			out.println(headerBackEnd);

			// Write out the message...
			out.println("<p>The file requested could not be found on this server.</p>");

			// Write out the trailer...
			out.print("<form method=\"post\" action=\"");
			out.print(response.encodeURL(request.getContextPath() + request.getServletPath()));
			out.println("\">");
			out.println("<input type=\"image\" src=\"//www.ibm.com/i/v14/buttons/us/en/continue.gif\" alt=\"Continue\" name=\"accept\" value=\"Continue\" />");
			out.println("<input type=\"hidden\" name=\"REQUEST\" value=\"DOWNLOAD\">");
			out.println("</form>");
			out.println(endMatter);
			out.close();
		}
	}

	// License was not agreed to...
	else {
		if (license != null) session.removeAttribute(licType);

		// set content-type and get writer.
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		// Write out the header...
		out.print(headerTitleFront);
		out.print((String) session.getAttribute("TOKEN"));
		out.print(headerTitleBack);
		out.print("Client Software for Customer Connect<br />Agreement declined!");
		out.println(headerBackEnd);

		// Write out the message...
		out.println("<p>You declined the License Agreement for the requested file. Select the Continue button to view the License Agreement for the file.</p>");

		// Write out the trailer...
		out.print("<form method=\"post\" action=\"");
		out.print(response.encodeURL(request.getContextPath() + request.getServletPath()));
		out.println("\">");
		out.println("<input type=\"image\" src=\"//www.ibm.com/i/v14/buttons/us/en/continue.gif\" alt=\"Continue\" name=\"accept\" value=\"Continue\" />");
		out.println("<input type=\"hidden\" name=\"REQUEST\" value=\"DOWNLOAD\">");
		out.println("</form>");
		out.println(endMatter);
		out.close();
	}
}
/**
 * Process unauthenticated download requests.
 * 
 * @param request Object that encapsulates the request to the servlet 
 * @param response Object that encapsulates the response from the servlet
 */
public void getFileAnyway(HttpServletRequest request, HttpServletResponse response) throws Exception {
	StringBuffer debugInfo = new StringBuffer();

	try {
		// Get the current session.
		HttpSession session = request.getSession(true);

		// Get the file requested.
		String path = request.getPathInfo();
		String file = path.substring(15); // chop off "/getFileAnyway/"

		debugInfo.append("Path is: " + path + "\n");
		debugInfo.append("File is: " + file + "\n");

		// Get the file and license type.
		if (! file.equals(InstallAndLaunchApp.DSC_FILE) &&
			! file.equals(InstallAndLaunchApp.SCRIPT_FILE) &&
			! file.equals(InstallAndLaunchApp.LDSSCRIPT_FILE) &&
			! file.equals(InstallAndLaunchApp.SOD_FILE) &&
			! file.equals(InstallAndLaunchApp.DSMP_FILE) &&
			! file.equals(InstallAndLaunchApp.IM1_FILE) &&
			! file.equals(InstallAndLaunchApp.IM2_FILE) &&
			! file.equals(InstallAndLaunchApp.XML_FILE) &&
			! file.equals(InstallAndLaunchApp.WIN1_LIB) &&
			! file.equals(InstallAndLaunchApp.WIN2_LIB) &&
			! file.equals(InstallAndLaunchApp.AIX_LIB) &&
			! file.equals(InstallAndLaunchApp.LIN_LIB) &&
			! file.equals(InstallAndLaunchApp.SUN1_LIB) &&
			! file.equals(InstallAndLaunchApp.SUN2_LIB) &&
			! file.equals(InstallAndLaunchApp.HPUX1_LIB) &&
			! file.equals(InstallAndLaunchApp.HPUX2_LIB) &&
			! file.equals(InstallAndLaunchApp.ICA1_FILE) &&
			! file.equals(InstallAndLaunchApp.ICA2_FILE) &&
			! file.equals(InstallAndLaunchApp.ICA3_FILE) &&
			! file.equals(InstallAndLaunchApp.WIN_FILE) &&
			! file.equals(InstallAndLaunchApp.AIX_GZFILE) &&
			! file.equals(InstallAndLaunchApp.LINUX_GZFILE) &&
			! file.equals(InstallAndLaunchApp.LWIN_FILE) &&
			! file.equals(InstallAndLaunchApp.LUNIX_FILE) &&
			! file.equals("DropboxServiceApplet.jar") &&
			! file.equals("EDODCTunnelApplet.jar") &&
			! file.equals("EDODCTunnelApplet.cab") &&
			! file.equals("DSCVersionStamps") &&
			! file.equals("dropbox.BANNER") &&
			! file.endsWith(".lic")) {
			debugInfo.append("File is not defined.\n");

			// set content-type and get writer.
			// Our mechanism doesn't work w/ 131 plugin, so need an alternative method.
			// response.setHeader("CrcCheck","file not defined");
			response.setHeader("Cache-Control", "no-cache");
			response.setHeader("Pragma", "no-cache");
			response.setContentType("text/html");

			debugInfo.append("Header defined.\n");

			PrintWriter out = response.getWriter();

			debugInfo.append("Got writer, writing 'NO'.\n");

			// Write out the header...
			out.print("NO");

			debugInfo.append("Writing 'The file requested has not been defined.'\n");

			// Write out the message...
			out.println("The file requested has not been defined.");

			debugInfo.append("Closing writer.\n");

			out.close();

			System.out.println("HelperInstall.getFileAny: Error processing request.");
			System.out.println(debugInfo.toString());
			return;
		}

		// Get a URL to the file.
		File f = null;

		debugInfo.append("Searching for file.\n");

		try {
			String realFile = DesktopServlet.findFileInOurDirectories(file);
			debugInfo.append("File found at: " + realFile + "\n");
			f = new File(realFile);
			debugInfo.append("File object created.\n");
		}
		catch (Exception e) {
			debugInfo.append("Exception while searching for file: " + e.getMessage() + "\n");
			System.out.println("HelperInstall.getFileAnyWay: " + e.getMessage());
			e.printStackTrace();
		}

		if (f != null && f.exists()) {
			debugInfo.append("File found.\n");

			String crcStr = request.getParameter("crc");

			debugInfo.append("crc request parameter is " + (crcStr == null ? "null" : crcStr) + "\n");

			FileInputStream in = new FileInputStream(f);
			byte[] b = new byte[4096];
			long fs = 0;

			debugInfo.append("Got file inputstream\n");

			// Trying to restart an old download?
			if (crcStr != null) {
				debugInfo.append("Trying to confirm CRC value.\n");

				try {
					long size = Long.parseLong(request.getParameter("size"));

					int crc = 0;
					ODCrc checker = new ODCrc();
					checker.resetCRC();

					int amt = (int) Math.min(b.length,size);
					int len = 0;
					while (amt > 0 && (len = in.read(b,0,amt)) != -1) {
						checker.generateCRC(b,0,len);
						fs += len;
						amt = (int) Math.min(b.length,size - fs);
					}

					crc = checker.getCRC();
					if (fs != size || crc != Integer.parseInt(crcStr)) {
						throw new NumberFormatException();
					}
				}
				catch (Exception e) {
					debugInfo.append("Failed to confirm CRC, exception is: " + e.getMessage() + "\n");

					in.close();

					// set content-type and get writer.
					// Our mechanism doesn't work w/ 131 plugin, so need an alternative method.
					//response.setHeader("CrcCheck","crc mismatch");
					response.setHeader("Cache-Control", "no-cache");
					response.setHeader("Pragma", "no-cache");
					response.setContentType("text/html");

					debugInfo.append("Header defined.\n");

					PrintWriter out = response.getWriter();

					debugInfo.append("Got writer, writing 'NO'.\n");

					// Write out the header...
					out.print("NO");

					debugInfo.append("Writing 'The file download requested could not be resumed.'\n");

					// Write out the message...
					out.println("The file download requested could not be resumed.");

					debugInfo.append("Closing writer.\n");

					out.close();
					System.out.println("HelperInstall.getFileAny: Error processing request.");
					System.out.println(debugInfo.toString());
					return;
				}

				debugInfo.append("Confirmed CRC value, download will resume.\n");
			}

			// set content-type, content-length and get output stream.
			// Our mechanism doesn't work w/ 131 plugin, so need an alternative method.
			//response.setHeader("CrcCheck","Ok");
			if (! file.equals("DropboxServiceApplet.jar")) {
				response.setHeader("Cache-Control", "no-cache");
				response.setHeader("Pragma", "no-cache");
			}
			response.setDateHeader("Last-Modified",f.lastModified());
			response.setContentType("application/octet-stream");

			debugInfo.append("Header started.\n");

			// We'll be sending 2 bytes as a response status (OK).
			int responseSize = 2;

			// Our applet won't be getting these files, the browse will, drop the response status.
			if (file.equals("EDODCTunnelApplet.jar") ||
                            file.equals("DropboxServiceApplet.jar") ||
                            file.equals("EDODCTunnelApplet.cab"))
                           responseSize = 0;

			long fl = f.length() - fs;
			if (fl >= 0 && fl <= Integer.MAX_VALUE - responseSize) {
				int x = (int) fl;
				response.setContentLength(x + responseSize);
				debugInfo.append("Header added content length.\n");
			}

			debugInfo.append("Header completed.\n");

			OutputStream out = response.getOutputStream();

			debugInfo.append("Got outputstream.\n");

			// Need to send our OK response?
			if (responseSize == 2) {
				debugInfo.append("Writing 'OK'.\n");

				out.write('O');
				out.write('K');

				debugInfo.append("Wrote 'OK'.\n");
			}

			debugInfo.append("Writing file content.\n");

			int l;
			while ((l = in.read(b,0,b.length)) != -1)
				out.write(b,0,l);

			debugInfo.append("Wrote file content, closing input stream.\n");

			in.close();

			debugInfo.append("Closed, closing response stream.\n");

			out.close();
		}
		else {
			debugInfo.append("File not found on this server.\n");
			// set content-type and get writer.
			// Our mechanism doesn't work w/ 131 plugin, so need an alternative method.
			//response.setHeader("CrcCheck","file not found");
			response.setHeader("Cache-Control", "no-cache");
			response.setHeader("Pragma", "no-cache");
			response.setContentType("text/html");

			debugInfo.append("Header defined.\n");

			PrintWriter out = response.getWriter();

			debugInfo.append("Got writer, writing 'NO'.\n");

			// Write out the header...
			out.print("NO");

			debugInfo.append("Writing 'The file requested could not be found on this server.'\n");

			// Write out the message...
			out.println("The file requested could not be found on this server.");

			debugInfo.append("Closing writer.\n");

			out.close();
			System.out.println("HelperInstall.getFileAny: Error processing request.");
			System.out.println(debugInfo.toString());
		}
	}
	catch (Exception e) {
		System.out.println("HelperInstall.getFileAnyway: Exception while processing request.");
		System.out.println(debugInfo.toString());
		throw e;
	}
}
/**
 * Returns the servlet info string.
 */
public String getServletInfo() {

	return super.getServletInfo();

}
/**
 * Present instructions for step 2.
 * 
 * @param request Object that encapsulates the request to the servlet 
 * @param response Object that encapsulates the response from the servlet
 */
public void inifile(HttpServletRequest request, HttpServletResponse response) throws Exception {
	// Get the current session.
	HttpSession session = request.getSession(true);

	// Get the appropriate attributes from the session:
	String DSC = (String) session.getAttribute("DSC");
	String SCRIPT = (String) session.getAttribute("SCRIPT");
	String LDS = (String) session.getAttribute("LDS");
	String SOD = (String) session.getAttribute("SOD");
	String XML = (String) session.getAttribute("XML");
	String IM1 = (String) session.getAttribute("IM1");
	String IM2 = (String) session.getAttribute("IM2");
	String ODC = (String) session.getAttribute("ODC");
	String WIN1LIB = (String) session.getAttribute("WIN1LIB");
	String WIN2LIB = (String) session.getAttribute("WIN2LIB");
	String AIXLIB = (String) session.getAttribute("AIXLIB");
	String SUN1LIB = (String) session.getAttribute("SUN1LIB");
	String SUN2LIB = (String) session.getAttribute("SUN2LIB");
	String LINLIB = (String) session.getAttribute("LINLIB");
	String HPUX1LIB = (String) session.getAttribute("HPUX1LIB");
	String HPUX2LIB = (String) session.getAttribute("HPUX2LIB");
	String ICA1 = (String) session.getAttribute("ICA1");
	String ICA2 = (String) session.getAttribute("ICA2");
	String ICA3 = (String) session.getAttribute("ICA3");
	String WIN = (String) session.getAttribute("JREWIN");
	String AIX = (String) session.getAttribute("JREAIX");
	String LINUX = (String) session.getAttribute("JRELINUX");
	String LWIN = (String) session.getAttribute("LAUNCHWIN");
	String LUNIX = (String) session.getAttribute("LAUNCHUNIX");

	// Load the version stamps
	ConfigFile versionStamps = new ConfigFile();
	String realFile = DesktopServlet.findFileInOurDirectories("DSCVersionStamps");
	if (realFile != null) versionStamps.load(realFile);

	// User wants to download ini file content.
	if (request.getParameter("download.x") != null) {
		// set content-type and get writer.
		response.setContentType("application/octet-stream");
		PrintWriter out = response.getWriter();

		// Write out ini file content.
		if (DSC != null && DSC.equals("done")) out.println("DSCVER=" + versionStamps.getProperty("DSCVER",null));
		if (SCRIPT != null && SCRIPT.equals("done")) out.println("SCRIPTVER=" + versionStamps.getProperty("SCRIPTVER",null));
		if (LDS != null && LDS.equals("done")) out.println("LDSVER=" + versionStamps.getProperty("LDSVER",null));
		if (SOD != null && SOD.equals("done")) out.println("SODVER=" + versionStamps.getProperty("SODVER",null));
		if (XML != null && XML.equals("done")) out.println("XMLVER=" + versionStamps.getProperty("XMLVER",null));
		if ((IM1 != null && IM1.equals("done")) ||
			(IM2 != null && IM2.equals("done"))) out.println("IMVER=" + versionStamps.getProperty("IMVER",null));
		if (ODC != null && ODC.equals("done")) out.println("DSMPVER=" + versionStamps.getProperty("DSMPVER",null));
		if ((WIN1LIB != null && WIN1LIB.equals("done")) ||
			(WIN2LIB != null && WIN2LIB.equals("done"))) out.println("LIBWINVER=" + versionStamps.getProperty("LIBWINVER",null));
		if (AIXLIB != null && AIXLIB.equals("done")) out.println("LIBAIXVER=" + versionStamps.getProperty("LIBAIXVER",null));
		if (LINLIB != null && LINLIB.equals("done")) out.println("LIBLINVER=" + versionStamps.getProperty("LIBLINVER",null));
		if ((SUN1LIB != null && SUN1LIB.equals("done")) ||
			(SUN2LIB != null && SUN2LIB.equals("done"))) out.println("LIBSUNSPVER=" + versionStamps.getProperty("LIBSUNSPVER",null));
		if ((HPUX1LIB != null && HPUX1LIB.equals("done")) ||
			(HPUX2LIB != null && HPUX2LIB.equals("done"))) out.println("LIBHPUXVER=" + versionStamps.getProperty("LIBHPUXVER",null));
		if ((ICA1 != null && ICA1.equals("done")) || (ICA2 != null && ICA2.equals("done")) ||
			(ICA3 != null && ICA3.equals("done"))) out.println("ICAVER=" + versionStamps.getProperty("ICAVER",null));
		if (WIN != null && WIN.equals("done")) {
			out.println("WINJREVERSION=" + versionStamps.getProperty("JREWINVER",null));
			out.println("WINJREPATH=WINJRE\\jre\\bin\\java.exe");
		}
		if (AIX != null && AIX.equals("done")) {
			out.println("AIXJREVERSION=" + versionStamps.getProperty("JREAIXVER",null));
			out.println("AIXJREPATH=AIXJRE/jre/bin/java");
		}
		if (LINUX != null && LINUX.equals("done")) {
			out.println("LINJREVERSION=" + versionStamps.getProperty("JRELINVER",null));
			out.println("LINJREPATH=LINJRE/jre/bin/java");
		}
		if ((LWIN != null && LWIN.equals("done")) ||
			(LUNIX != null && LUNIX.equals("done"))) out.println("LAUNCHVER=" + versionStamps.getProperty("LAUNCHVER",null));
		out.println("NEWODCCLASS=oem.edge.ed.odc.meeting.client.MeetingViewer");
		out.println("NEWODCCLASSPATH=" + InstallAndLaunchApp.DSMP_FILE);
		out.println("TUNNELCLASS=oem.edge.ed.odc.applet.LaunchApp");
		out.println("TUNNELCLASSPATH=" + InstallAndLaunchApp.SOD_FILE + ";" + InstallAndLaunchApp.DSC_FILE);
		out.println("IMCLASS=oem.edge.ed.odc.applet.SametimeClient");
		out.println("IMCLASSPATH=" + InstallAndLaunchApp.IM1_FILE + ";" +
						InstallAndLaunchApp.IM2_FILE + ";" + InstallAndLaunchApp.DSC_FILE);
		out.println("SDCLASS=oem.edge.ed.sd.SDHostingApp1");
		out.println("SDCLASSPATH=" + InstallAndLaunchApp.XML_FILE + ";" + InstallAndLaunchApp.DSC_FILE);
		out.println("DROPCMDLINECLASSPATH=" + InstallAndLaunchApp.DSC_FILE);
		out.println("DROPCMDLINECLASS=oem.edge.ed.odc.dropbox.client.DropboxCmdline");
		out.println("ICA1CLASS=com.citrix.JICA");
		out.println("ICA1CLASSPATH=" + InstallAndLaunchApp.ICA3_FILE);
		out.println("ICA2CLASS=com.citrix.JICA");
		out.println("ICA2CLASSPATH=" + InstallAndLaunchApp.ICA1_FILE + ";" + InstallAndLaunchApp.ICA2_FILE);
		out.println("XFRCLASS=oem.edge.ed.odc.dropbox.client.DropBox");
		out.println("XFRCLASSPATH=" + InstallAndLaunchApp.DSC_FILE);
		out.println("INIVERSION=" + InstallAndLaunchApp.INIVERSION);

		out.close();
		return;
	}

	// User wants to see ini file content...

	// set content-type and get writer.
	response.setContentType("text/html");
	PrintWriter out = response.getWriter();

	// Write out header...
	out.print(headerTitleFront);
	out.print((String) session.getAttribute("TOKEN"));
	out.print(headerTitleBack);
	out.print("Client Software for Customer Connect<br />Manual installation");
	out.println(headerBackEnd);

	// Write out the inifile content...
	out.println(inifileBegin);

	out.println("<table border=\"0\" cellpadding=\"0\"><tbody>");

	if (DSC != null && DSC.equals("done")) out.println("<tr><td>DSCVER=" + versionStamps.getProperty("DSCVER",null) + "</td></tr>");
	if (SCRIPT != null && SCRIPT.equals("done")) out.println("<tr><td>SCRIPTVER=" + versionStamps.getProperty("SCRIPTVER",null) + "</td></tr>");
	if (LDS != null && LDS.equals("done")) out.println("<tr><td>LDSVER=" + versionStamps.getProperty("LDSVER",null) + "</td></tr>");
	if (SOD != null && SOD.equals("done")) out.println("<tr><td>SODVER=" + versionStamps.getProperty("SODVER",null) + "</td></tr>");
	if (XML != null && XML.equals("done")) out.println("<tr><td>XMLVER=" + versionStamps.getProperty("XMLVER",null) + "</td></tr>");
	if ((IM1 != null && IM1.equals("done")) ||
		(IM2 != null && IM2.equals("done"))) out.println("<tr><td>IMVER=" + versionStamps.getProperty("IMVER",null) + "</td></tr>");
	if (ODC != null && ODC.equals("done")) out.println("<tr><td>DSMPVER=" + versionStamps.getProperty("DSMPVER",null) + "</td></tr>");
	if ((WIN1LIB != null && WIN1LIB.equals("done")) ||
		(WIN2LIB != null && WIN2LIB.equals("done"))) out.println("<tr><td>LIBWINVER=" + versionStamps.getProperty("LIBWINVER",null) + "</td></tr>");
	if (AIXLIB != null && AIXLIB.equals("done")) out.println("<tr><td>LIBAIXVER=" + versionStamps.getProperty("LIBAIXVER",null) + "</td></tr>");
	if (LINLIB != null && LINLIB.equals("done")) out.println("<tr><td>LIBLINVER=" + versionStamps.getProperty("LIBLINVER",null) + "</td></tr>");
	if ((SUN1LIB != null && SUN1LIB.equals("done")) ||
		(SUN2LIB != null && SUN2LIB.equals("done"))) out.println("<tr><td>LIBSUNSPVER=" + versionStamps.getProperty("LIBSUNSPVER",null) + "</td></tr>");
	if ((HPUX1LIB != null && HPUX1LIB.equals("done")) ||
		(HPUX2LIB != null && HPUX2LIB.equals("done"))) out.println("<tr><td>LIBHPUXVER=" + versionStamps.getProperty("LIBHPUXVER",null) + "</td></tr>");
	if ((ICA1 != null && ICA1.equals("done")) || (ICA2 != null && ICA2.equals("done")) ||
		(ICA3 != null && ICA3.equals("done"))) out.println("<tr><td>ICAVER=" + versionStamps.getProperty("ICAVER",null) + "</td></tr>");
	if (WIN != null && WIN.equals("done")) {
		out.println("<tr><td>WINJREVERSION=" + versionStamps.getProperty("JREWINVER",null) + "</td></tr>");
		out.println("<tr><td>WINJREPATH=WINJRE\\jre\\bin\\java.exe</td></tr>");
	}
	if (AIX != null && AIX.equals("done")) {
		out.println("<tr><td>AIXJREVERSION=" + versionStamps.getProperty("JREAIXVER",null) + "</td></tr>");
		out.println("<tr><td>AIXJREPATH=AIXJRE/jre/bin/java</td></tr>");
	}
	if (LINUX != null && LINUX.equals("done")) {
		out.println("<tr><td>LINJREVERSION=" + versionStamps.getProperty("JRELINVER",null) + "</td></tr>");
		out.println("<tr><td>LINJREPATH=LINJRE/jre/bin/java</td></tr>");
	}
	if ((LWIN != null && LWIN.equals("done")) ||
		(LUNIX != null && LUNIX.equals("done"))) out.println("<tr><td>LAUNCHVER=" + versionStamps.getProperty("LAUNCHVER",null) + "</td></tr>");
	out.println("<tr><td>ODCCLASS=oem.edge.ed.odc.meeting.client.MeetingViewer</td></tr>");
	out.println("<tr><td>ODCCLASSPATH=" + InstallAndLaunchApp.DSMP_FILE + "</td></tr>");
	out.println("<tr><td>TUNNELCLASS=oem.edge.ed.odc.applet.LaunchApp</td></tr>");
	out.println("<tr><td>TUNNELCLASSPATH=" + InstallAndLaunchApp.SOD_FILE + ";" + InstallAndLaunchApp.DSC_FILE + "</td></tr>");
	out.println("<tr><td>IMCLASS=oem.edge.ed.odc.applet.SametimeClient</td></tr>");
	out.println("<tr><td>IMCLASSPATH=" + InstallAndLaunchApp.IM1_FILE + ";" +
						InstallAndLaunchApp.IM2_FILE + ";" + InstallAndLaunchApp.DSC_FILE + "</td></tr>");
	out.println("<tr><td>SDCLASS=oem.edge.ed.sd.SDHostingApp1</td></tr>");
	out.println("<tr><td>SDCLASSPATH=" + InstallAndLaunchApp.XML_FILE + ";" + InstallAndLaunchApp.DSC_FILE + "</td></tr>");
	out.println("<tr><td>DROPCMDLINECLASSPATH=" + InstallAndLaunchApp.DSC_FILE + "</td></tr>");
	out.println("<tr><td>DROPCMDLINECLASS=oem.edge.ed.odc.dropbox.client.DropboxCmdline</td></tr>");
	out.println("<tr><td>ICA1CLASS=com.citrix.JICA</td></tr>");
	out.println("<tr><td>ICA1CLASSPATH=" + InstallAndLaunchApp.ICA3_FILE + "</td></tr>");
	out.println("<tr><td>ICA2CLASS=com.citrix.JICA</td></tr>");
	out.println("<tr><td>ICA2CLASSPATH=" + InstallAndLaunchApp.ICA1_FILE + ";" + InstallAndLaunchApp.ICA2_FILE + "</td></tr>");
	out.println("<tr><td>XFRCLASS=oem.edge.ed.odc.dropbox.client.DropBox</td></tr>");
	out.println("<tr><td>XFRCLASSPATH=" + InstallAndLaunchApp.DSC_FILE + "</td></tr>");
	out.println("<tr><td>INIVERSION=" + InstallAndLaunchApp.INIVERSION + "</td></tr>");

	out.println("</tbody></table>");

	out.println("<p>Select the Download now button to download this edesign.ini file. If you already have an edesign.ini");
	out.println("file, you should mark, copy and paste these additional statements into the existing edesign.ini file.</p>");

	// Write out the trailer...
	out.print("<form method=\"post\" action=\"");
	out.print(response.encodeURL(request.getContextPath() + request.getServletPath() + "/edesign.ini"));
	out.println("\">");
	out.println("<input type=\"hidden\" name=\"REQUEST\" value=\"INIFILE\" />");
	out.println("<table width=\"100%\" border=\"0\" cellspacing=\"0\"><tbody><tr><td>");
	out.println("<input type=\"image\" src=\"//www.ibm.com/i/v14/buttons/us/en/download_now.gif\" alt=\"Download Now\" name=\"download\" value=\"Download now\" />");
	out.println("</td><td align=\"right\"><table border=\"0\"><tbody>");
	out.println("<tr><td>");
	out.print("<a href=\"");
	out.print(response.encodeURL(request.getContextPath() + request.getServletPath() + "?REQUEST=CONFIG"));
	out.println("\"><img src=\"//www.ibm.com/i/v14/buttons/arrow_rd.gif\" border=\"0\" alt=\"continue\" width=\"21\" height=\"21\" /></a></td>");
	out.print("<td><a href=\"");
	out.print(response.encodeURL(request.getContextPath() + request.getServletPath() + "?REQUEST=CONFIG"));
	out.println("\">Continue with Step 4</a></td></tr>");
	out.println("</tbody></table>");
	out.println("</td></tr></tbody></table>\n</form>");
	out.println(endMatter);
	out.close();
}
/**
 * Initializes the servlet.
 */
public void init() {
	// insert code to initialize the servlet here

}
/**
 * Process incoming requests for information
 * 
 * @param request Object that encapsulates the request to the servlet 
 * @param response Object that encapsulates the response from the servlet
 */
public void performTask(HttpServletRequest request, HttpServletResponse response) {
	try	{
		// Get the session object (if any).
		HttpSession session = request.getSession(true);

		// Check if request is getFileAnyway and process without token (for now).
		String requestId = request.getParameter("REQUEST");

		if (requestId == null) {
			String path = request.getPathInfo();
			if (path != null && path.startsWith("/getFileAnyway/")) {
				getFileAnyway(request,response);
				return;
			}
		}

		// Check the session object for the token.
		String token = (String) session.getAttribute("TOKEN");

		// No token? Demand one.
		if (token == null) {
			System.out.println("No token found for request " + requestId);
			// Check for compname (ODC & DSH) or token (SWD).
			token = request.getParameter("compname");
			System.out.println("compname is " + (token == null ? "null" : "not null"));
			if (token == null) token = request.getParameter("token");
			System.out.println("token is " + (token == null ? "null" : "not null"));
			String app = request.getParameter("app");
			System.out.println("app is " + (app == null ? "null" : app));
			boolean isEdge = (app == null || ! app.equals("SD"));
			System.out.println("isEdge is " + isEdge);

			// No current token provided, don't show em anything.
			if (token == null || ! DesktopServlet.isTokenCurrent(token,isEdge)) {
				// set content-type and get writer.
				response.setContentType("text/html");
				PrintWriter out = response.getWriter();

				// Write out page header and front matter.
				out.print(headerTitleFront);
				out.print("Customer Connect service");
				out.print(headerTitleBack);
				out.print("Client Software for Customer Connect<br />Manual installation");
				out.println(headerNoBackEnd);
				out.println("<p>ERROR: Manual Installation failed.<br />");
				out.println("CAUSE: Your authentication is expired.<br />");
				if (isEdge)
					out.println("ACTION: Reselect the link on the IBM Edge Design Services web page.");
				else
					out.println("ACTION: Reselect the software order link again.");
				out.println(endMatter);
				out.close();
				return;
			}

			// A valid token is provided. Get launchServlet path and query string parameters.
			String launchServlet = request.getPathInfo();

			Enumeration e = request.getParameterNames();
			Hashtable h = new Hashtable();

			while (e.hasMoreElements()) {
				String parm = (String) e.nextElement();
				h.put(parm,request.getParameter(parm));
			}

			session.setAttribute("LAUNCHSERVLET",launchServlet);
			session.setAttribute("QUERYSTRING",h);

			if (isEdge)
				session.setAttribute("TOKEN",DesktopServlet.getCommandDescription(token));
			else
				session.setAttribute("TOKEN","Software download");
		}

		if (requestId == null) {
			String path = request.getPathInfo();
			if (path != null && path.startsWith("/getfile/"))
				getFile(request,response);
			else
				frontPage(request,response);
		}

		else if (requestId.equals("DOWNLOAD"))
			download(request,response);

		else if (requestId.equals("INIFILE"))
			inifile(request,response);

		else if (requestId.equals("UNPACK"))
			unpack(request,response);

		else if (requestId.equals("CONFIG"))
			configure(request,response);

		else
			frontPage(request,response);
	}
	catch(Throwable theException)
	{
		// uncomment the following line when unexpected exceptions
		// are occuring to aid in debugging the problem.
		theException.printStackTrace();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/19/2001 10:03:23 AM)
 * @param session javax.servlet.http.HttpSession
 * @param license java.lang.String
 */
public void resetLicense(HttpSession session, String license) {
	String LICENSE = (String) session.getAttribute(license);
	if (LICENSE != null && LICENSE.equals("decline"))
		session.removeAttribute(license);
}
/**
 * Insert the method's description here.
 * Creation date: (9/19/2001 9:03:06 AM)
 * @param out java.io.PrintWriter
 * @exception java.lang.Exception The exception description.
 */
public void sendDownloadBegin(PrintWriter out,String servlet) throws Exception {
	out.println("<p><b>Step 1:</b> Download the software to a directory of your choosing. The");
	out.println("appropriate files for your platform are selected.</p>");
	out.println("<script language=\"JavaScript\" type=\"text/javascript\">\n<!--\n");
	out.println("function checkFields(formObj) {");
	out.println("  if (formObj.DSC.checked || formObj.XML.checked ||");
	out.println("      formObj.SOD.checked || formObj.SCRIPT.checked || formObj.LDS.checked ||");
	out.println("      formObj.IM1.checked || formObj.IM2.checked || formObj.ODC.checked ||");
	out.println("      formObj.WIN1LIB.checked || formObj.WIN2LIB.checked || formObj.AIXLIB.checked ||");
	out.println("      formObj.SUN1LIB.checked || formObj.SUN2LIB.checked || formObj.LINLIB.checked ||");
	out.println("      formObj.HPUX1LIB.checked || formObj.HPUX2LIB.checked || formObj.ICA1.checked || formObj.ICA2.checked ||");
	out.println("      formObj.ICA3.checked || formObj.JREWIN.checked || formObj.JREAIX.checked ||");
	out.println("      formObj.JRELINUX.checked || formObj.LAUNCHWIN.checked || formObj.LAUNCHUNIX.checked) {");
	out.println("    return true;\n  }\n\n  window.alert(\"Please select an item to download!\");\n\n  return false;\n}\n//-->\n</script>");
	out.print("<form method=\"post\" action=\"");
	out.print(servlet);
	out.println("\" name=\"DownloadForm\" onsubmit=\"return checkFields(this)\">");
	out.println("<table cellpadding=\"0\" cellspacing=\"0\">\n<tbody>");
}
/**
 * Insert the method's description here.
 * Creation date: (9/19/2001 9:03:06 AM)
 * @param out java.io.PrintWriter
 * @exception java.lang.Exception The exception description.
 */
public void sendDownloadEnd(PrintWriter out,String servlet) throws Exception {
	out.println("</tbody></table>");
	out.println("</td></tr>");
	out.println("</tbody></table>");
	out.println("<input type=\"hidden\" name=\"REQUEST\" value=\"DOWNLOAD\" />");
	out.println("<table width=\"100%\" border=\"0\" cellspacing=\"0\"><tbody><tr><td>");
	out.println("<input type=\"image\" src=\"//www.ibm.com/i/v14/buttons/us/en/download_now.gif\" alt=\"Download Now\" name=\"download\" value=\"Download now\" />");
	out.println("</td><td align=\"right\"><table border=\"0\"><tbody>");
	out.println("<tr><td>");
	out.print("<a href=\"");
	out.print(servlet);
	out.println("\"><img src=\"//www.ibm.com/i/v14/buttons/arrow_rd.gif\" border=\"0\" alt=\"continue\" width=\"21\" height=\"21\" /></a></td>");
	out.print("<td><a href=\"");
	out.print(servlet);
	out.println("\">Continue with Step 2</a></td></tr>");
	out.println("</tbody></table></td></tr></tbody></table>");
	out.println("</form>");
	out.println(endMatter);
}
/**
 * Present instructions for step 2.
 * 
 * @param request Object that encapsulates the request to the servlet 
 * @param response Object that encapsulates the response from the servlet
 */
public void unpack(HttpServletRequest request, HttpServletResponse response) throws Exception {
	// Get the current session.
	HttpSession session = request.getSession(true);

	// set content-type and get writer.
	response.setContentType("text/html");
	PrintWriter out = response.getWriter();

	// Write out header...
	out.print(headerTitleFront);
	out.print((String) session.getAttribute("TOKEN"));
	out.print(headerTitleBack);
	out.print("Client Software for Customer Connect<br />Manual installation");
	out.println(headerBackEnd);

	// Write out the unpack content...
	out.println(unpack);

	// Write out the trailer...
	out.println("<table width=\"100%\" border=\"0\" cellspacing=\"0\"><tbody>");
	out.println("<tr><td align=\"right\"><table border=\"0\"><tbody>");
	out.println("<tr><td>");
	out.print("<a href=\"");
	out.print(response.encodeURL(request.getContextPath() + request.getServletPath() + "?REQUEST=INIFILE"));
	out.println("\"><img src=\"//www.ibm.com/i/v14/buttons/arrow_rd.gif\" border=\"0\" alt=\"continue\" width=\"21\" height=\"21\" /></a></td>");
	out.print("<td><a href=\"");
	out.print(response.encodeURL(request.getContextPath() + request.getServletPath() + "?REQUEST=INIFILE"));
	out.println("\">Continue with Step 3</a></td></tr>");
	out.println("</tbody></table></td></tr></tbody></table>");
	out.println(endMatter);
	out.close();
}
}
