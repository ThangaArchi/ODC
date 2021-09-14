package oem.edge.ed.odc.cntl;

import java.net.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.*;
import javax.servlet.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

import oem.edge.ed.odc.util.UserRegistryFactory;
import oem.edge.common.cipher.*;
import oem.edge.common.RSA.*;
import com.ibm.as400.webaccess.common.*;
import oem.edge.ed.odc.tunnel.common.*;
import oem.edge.ed.odc.tunnel.servlet.*;
import oem.edge.ed.odc.model.*;
import oem.edge.ed.odc.util.*;
import oem.edge.ed.odc.view.*;
import oem.edge.ed.util.*;
import oem.edge.ed.odc.cntl.metrics.*;

/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2002-2006                                     */
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

public class DesktopConstants {

   static final protected String trouble_shoot_blurb = 
         "<p />If you experience a problem running\n" + 
         "our signed applet, check that you are\n" + 
         "using one of the following supported\n" + 
         "configurations:\n" + 
         "<p /><b>Netscape 4.75 through 4.79</b>:\n" + 
         "Use <i>Edit->Preferences</i> menu\n" + 
         "item. Select the <i>Advanced</i> option\n" + 
         "on the Preferences window. Ensure\n" + 
         "that <i>Enable Java</i> is checked\n" + 
         "and that <i>Enable Java Plug-in</i>\n" + 
         "is not checked (if present). If you\n" + 
         "changed either setting, restart the\n" + 
         "broswer. When the applet executes, a\n" + 
         "series of pop-up windows prompt you\n" + 
         "to grant the applet permission to access\n" + 
         "your computer. Push the <i>Grant</i>\n" + 
         "button to permit access. If you push\n" + 
         "the <i>Deny</i> button or close the\n" + 
         "pop-up, access to your computer is denied.\n" + 
         "<br /><b>Netscape 6</b>: The Sun Java\n" + 
         "Plug-in is required to support applets.\n" + 
         "A single pop-up appears before the applet\n" + 
         "executes. This pop-up asks your\n" + 
         "permission to trust the applet and provide\n" + 
         "the applet access to your computer.\n" + 
         "Push the <i>Grant Always</i> or <i>Grant\n" + 
         "This Session</i> button to allow the\n" + 
         "applet to run. If you push the <i>Deny</i>\n" + 
         "buton or close the pop-up, access to\n" + 
         "your computer is denied and the applet does\n" + 
         "not run. We have successfully tested\n" + 
         "the Sun Java Plug-in versions 1.3.1 and\n" + 
         "1.3.1_01 with Netscape 6. Versions\n" + 
         "1.3.1_01a, 1.3.1_02 and 1.4.0 experienced\n" + 
         "various problems in our testing.\n" + 
         "<br /><b>Internet Explorer (IE) 4.x and 5.x</b>:\n" + 
         "A single pop-up window appears\n" + 
         "before the applet executes. This pop-up asks\n" + 
         "your permission to trust the\n" + 
         "applet and provide the applet access to your\n" + 
         "computer. Push the <i>Yes</i>\n" + 
         "button to allow the applet to run. If you push\n" + 
         "the <i>No</i> buton or close\n" + 
         "the pop-up, access to your computer is denied\n" + 
         "and the applet does not run.\n" + 
         "<br /><b>IE 6 on Windows XP using Microsoft's\n" + 
         "Java VM</b>: You must install\n" + 
         "Microsoft's Java VM to run applets. A single\n" + 
         "pop-up appears before the\n" + 
         "applet executes. This pop-up asks your\n" + 
         "permission to trust the applet and\n" + 
         "provide the applet access to your computer.\n" + 
         "Push the <i>Yes</i> button\n" + 
         "to allow the applet to run. If you push the\n" + 
         "<i>No</i> buton or close the\n" + 
         "pop-up, access to your computer is denied\n" + 
         "and the applet does not run.\n" + 
         "Microsoft's Java VM may be downloaded through\n" + 
         "the Windows Update feature.\n" + 
         "<br /><b>IE 6 on Windows XP using Sun's Java\n" + 
         "Plug-in</b>: You must install\n" + 
         "the Sun Java Plug-in to run applets. A single\n" + 
         "pop-up appears before the\n" + 
         "applet executes. This pop-up asks your permission\n" + 
         "to trust the applet and\n" + 
         "provide the applet access to your computer. Push\n" + 
         "the <i>Grant Always</i> or\n" + 
         "<i>Grant This Session</i> button to allow the\n" + 
         "applet to run. If you push the\n" + 
         "<i>Deny</i> buton or close the pop-up, access to\n" + 
         "your computer is denied and\n" + 
         "the applet does not run. We have successfully\n" + 
         "tested Sun Java Plug-in versions\n" + 
         "1.3.1_01a, 1.3.1_02 and 1.4.0. Sun Java Plug-in\n" + 
         "versions 1.3.1 and 1.3.1_01\n" + 
         "work only if you have not installed Microsoft's Java VM.\n" + 
         "<br /><b>IE 6 on non-XP Windows Platforms</b>: We\n" + 
         "recommend the use of Microsoft's\n" + 
         "Java VM. We experienced problems with most versions\n" + 
         "of the Sun Java Plug-in.\n" + 
         "If you have the Sun Java Plug-in installed, select\n" + 
         "the <i>Tools->Internet\n" + 
         "Options...</i> menu item in IE. In the pop-up,\n" + 
         "select the <i>Advanced</i>\n" + 
         "tab. In the <i>Java (Sun)</i> section, uncheck\n" + 
         "the <i>Use Java 2 ... for&lt;applet&gt;</i> option,\n" + 
         "if present.\n";
               
   static final protected String signed_applet_blurb1a = 
         "<p /><b>Signed applet technology</b>&nbsp;&nbsp;\n" +
         "<a href=\"";
         
   static final protected String signed_applet_blurb1b = 
         "/servlet/oem/edge/ed/odc/desktop/troubleshootinstall" +
         "?cmddesc=";
         
   static final protected String signed_applet_blurb2 = 
         "\">trouble-shoot installation applet</a>\n" +
         "<p />An applet is a program delivered over the\n" +
         "Internet to your web browser, where it is\n" +
         "executed. To protect your workstation from a malicious\n" +
         "program, an applet is not allowed to access\n" +
         "local system resources on your workstation such as\n" +
         "the disk drives and personal information.\n" +
         "These restrictions, for your safety, limit\n" +
         "the activity and usefulness of applet software.\n" +
         "Signed applets are different; they are digitally\n" +
         "signed using cryptography. The digital signature\n" +
         "enables your web browser to identify the\n" +
         "organization or company that produced the\n" +
         "software and to verify that it is original\n" +
         "and has not been altered. With your permission,\n" +
         "your web browser can be instructed to provide the signed applet\n" +
         "access to local system resources as though the\n" +
         "it were an installed application on your\n" +
         "workstation. Your browser will prompt you to grant\n" +
         "the signed applet access to your workstation.\n";
   
           
        
   static final protected String doctypehead_req = 
          "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
          "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\n<head>\n";
   
   static final protected String metamatter_req = 
          "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />\n" +
          "<meta http-equiv=\"PICS-Label\" content='(PICS-1.1 \"http://www.icra.org/ratingsv02.html\" l gen true r (cz 1 lz 1 nz 1 oz 1 vz 1) \"http://www.rsac.org/ratingsv01.html\" l gen true r (n 0 s 0 v 0 l 0) \"http://www.classify.org/safesurf/\" l gen true r (SS~~000 1))' />\n" +
          "<link rel=\"schema.DC\" href=\"http://purl.org/DC/elements/1.0/\" />\n" +
          "<link rel=\"SHORTCUT ICON\" href=\"http://www.ibm.com/favicon.ico\" />\n" +
          "<meta name=\"SECURITY\" content=\"Public\" />\n" +
          "<meta name=\"KEYWORDS\" content=\"OEM, IBM Corporation, Technology Group\" />\n" +
          "<meta name=\"SOURCE\" content=\"v11 Template Generator, Template 11.1.3\" />\n" +
          "<meta name=\"DC.Rights\" content=\"Copyright (c) 2002 by IBM Corporation\" />\n" +
          "<meta name=\"Robots\" content=\"noindex,nofollow,noarchive\" />\n" +
          "<meta name=\"DC.LANGUAGE\" scheme=\"rfc1766\" content=\"en-US\" />\n" +
          "<meta name=\"CHARSET\" content=\"iso88591\" />\n" +
          "<meta name=\"IBM.COUNTRY\" content=\"us\" />\n" +
          "<meta name=\"DC.DATE\" scheme=\"iso8601\" content=\"2005-07-16\" />\n" +
          "<meta name=\"OWNER\"  content=\"econnect@us.ibm.com\" />\n" +
          "<meta name=\"DESCRIPTION\" content=\"The IBM Customer Connect BTV\" />\n" +
          "<meta name=\"ABSTRACT\" content=\"The IBM Customer Connect BTV\" />\n" +
          "<meta name=\"DC.Type\" scheme=\"IBM_ContentClassTaxonomy\" content=\"TS100\" />\n" +
          "<meta name=\"DC.Subject\" scheme=\"IBM_SubjectTaxonomy\" content=\"1018529, 56982\" />\n" +
          "<meta name=\"DC.Publisher\" content=\"IBM Corporation\" />\n" +
          "<meta name=\"IBM.Effective\" scheme=\"W3CDTF\" content=\"2004-12-04\" />\n" +
          "<meta name=\"IBM.Industry\" scheme=\"IBM_IndustryTaxonomy\" content=\"K, Y, BA\" />\n";
   
   
   static final protected String StandardHead = doctypehead_req + metamatter_req; 
         
   static final protected String metapragma = 
         "<meta http-equiv=\"Pragma\" content=\"no-cache\" />\n" +
         "<meta http-equiv=\"Cache-Control\" content=\"no-cache\" />\n" +
         "<meta http-equiv=\"Expires\" content=\"0\" />\n";
   
   static final protected String head_matter2 =
          "<link rel=\"stylesheet\" type=\"text/css\" href=\"//www.ibm.com/common/v14/main.css\" />\n" +
          "<link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"//www.ibm.com/common/v14/screen.css\" />\n" +
          "<link rel=\"stylesheet\" type=\"text/css\" media=\"print\" href=\"//www.ibm.com/common/v14/print.css\" />\n" +
          "<link rel=\"stylesheet\" type=\"text/css\" href=\"//www.ibm.com/common/v14/popup.css\" />\n" +
          "<script src=\"//www.ibm.com/common/v14/detection.js\" language=\"JavaScript\" type=\"text/javascript\">\n" +
          "</script></head><body>";
/*        "<style type=\"text/css\">\n" +
          "a.close:link { text-decoration: underline; color: #ffffff; font-family: Arial, sans-serif; font-size: 10px; }\n"+
          "a.close:visited { text-decoration: underline; color: #ffffff; font-family: Arial, sans-serif; font-size: 10px; }\n" +
          "a.close:hover { text-decoration: underline; color: #ffffff; font-family: Arial, sans-serif; font-size: 10px; }\n" +
          "</style></head><body bgcolor=\"\" ; marginheight=\"\" ;marginwidth=\"\">";
*/          
   static final protected String top_matter =
          "<!-- MASTHEAD_BEGIN -->\n<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" id=\"v14-pop-mast\" width=\"100%\">\n<tr>\n" +
          "<td class=\"bbg\"><img alt=\"IBM&reg;\" border=\"0\" height=\"30\" src=\"//www.ibm.com/i/v14/t/ibm-logo-small.gif\" width=\"55\" /></td>\n" +
          "<td align=\"right\" class=\"bbg\">&nbsp;</td>\n</tr>\n<tr>\n" +
          "<td class=\"lgray\" colspan=\"2\"><img alt=\"\" height=\"2\" src=\"//www.ibm.com/i/c.gif\" width=\"1\"/></td>\n" +
          "</tr>\n</table>\n<!-- MASTHEAD_END -->\n";
/*       "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\">\n" +
         "<tr bgcolor=\"#006699\"><td class=\"hbg\" height=\"35\" valign=\"middle\">\n" +
         "<img src=\"//www.ibm.com/i/v11/ibmlogo_small.gif\" width=\"56\" height=\"24\" border=\"0\" alt=\"IBM\" /></td>\n" +
         "<td class=\"hbg\" align=\"right\" valign=\"middle\"><a href=\"javascript:self.close()\" class=\"close\">close</a>\n" +
         "<img src=\"//www.ibm.com/i/v11/c.gif\" width=\"12\" height=\"1\" border=\"0\" alt=\"\" /></td></tr></table>\n";
*/   
   static final protected String top_matterback1 =
         "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">" +
         "<tbody>\n<tr>\n<td valign=\"middle\">\n<h3>";
         
   static final protected String top_matterback2 = 
         "</h3>\n</td>\n<td align=\"right\" valign=\"top\">\n" +
	 "<table>\n<tbody>\n<tr>\n" +
	 "<td><a href=\"javascript:history.back();\">" +
	 "<img src=\"//www.ibm.com/i/v14/buttons/arrow_lt.gif\" border=\"0\" alt=\"Back\" width=\"21\" height=\"21\" />" +
	 "</a></td>\n" +
	 "<td><a href=\"javascript:history.back();\">Back</a></td>\n" +
	 "</tr>\n</tbody>\n</table>\n</td>\n</tr>\n</tbody>\n</table>";
   
   static final protected String bot_matter = /* "</body></html>"; */
         "<!-- FOOTER_BEGIN -->\n<br />\n" +
         "<table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
         "<tr class=\"bbg\">\n<td height=\"19\">\n" +
         "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n<tr>\n" +
         "<td><span class=\"spacer\">&nbsp;&nbsp;&nbsp;&nbsp;</span><a class=\"ur-link\" href=\"http://www.ibm.com/legal/\" target=\"_blank\">Terms of use</a></td>\n" +
         "<td class=\"footer-divider\" width=\"27\">&nbsp;&nbsp;&nbsp;&nbsp;</td>\n" +
         "<td><a class=\"ur-link\" href=\"http://www.ibm.com/privacy/\" target=\"_blank\">Privacy</a><span class=\"spacer\">&nbsp;&nbsp;&nbsp;&nbsp;</span></td>\n" +
         "</tr>\n</table>\n</td>\n" +
         "<td align=\"right\"><a class=\"mainlink\" href=\"javascript:window.close()\">Close [x]</a><span class=\"spacer\">&nbsp;&nbsp;</span></td>\n" +
         "</tr>\n</table>\n<!-- FOOTER_END -->\n" +
         "<script type=\"text/javascript\" language=\"JavaScript1.2\" src=\"//www.ibm.com/common/stats/stats.js\"></script>\n" +
         "<noscript><img src=\"//stats.www.ibm.com/rc/images/uc.GIF?R=noscript\" width=\"1\" height=\"1\" alt=\"\" border=\"0\"/></noscript>\n" +
         "</body>\n</html>\n";
         
   static final protected String javascript_launch = 
         "<script type=\"text/javascript\" language=\"javascript1.2\">\n" +
         "<!--\n" +
      /* "function launch(urlv, nm, inw, inh) {\n" +
         "   h=\"\";\n" +   
         "   w=\"\";\n" +   
         "   if (inh > 0) { h=\"height=\" + inh + \",\"; }\n" +
         "   if (inw > 0) { w=\"width=\"  + inw + \",\"; }\n" +
         "   opts=h+w+\"menubar=no,resizable=no,status=no,toolbar=no,scrollbars=yes\";\n" +
         "   open(urlv, nm , opts);\n" +
         "}\n" +
      */ "function launchr(urlv, nm, inw, inh) {\n" +
         "   h=\"\";\n" +   
         "   w=\"\";\n" +   
         "   if (inh > 0) { h=\"height=\" + inh + \",\"; }\n" +
         "   if (inw > 0) { w=\"width=\"  + inw + \",\"; }\n" +
         "   opts=h+w+\"menubar=no,resizable=yes,status=no,toolbar=no,scrollbars=yes\";\n" +
         "   open(urlv, nm , opts);\n" +
         "}\n" +
      /* "function resizeWindowN(inw, inh) {}\n" +
         "function resizeWindow(inw, inh) {\n" +
         "   var v=0;\n" +
         "   var w=window.outerWidth;\n"  +
         "   var h=window.outerHeight;\n" +
         "   if (inh > 0) {v++; h=inh;}\n" +
         "   if (inw > 0) {v++; w=inw;}\n" +
         "   if (v > 0)   {window.resizeTo(w, h);}\n" +
         "}\n" +
      */ "//-->\n" +
         "</script>\n";
         
         
   static final protected String manual_install_matter1a = 
         "\n" +
         "<p /><b>Alternate approach:</b> manual installation and launch<br />\n" +
         "<br />\n" +
         "For customers who prefer to not use signed\n" +
         "applet technology, you may download, install\n" +
         "and setup the Client Software for Customer Connect manually.\n" +
         "Once the client is installed, select the\n" +
         "launch\n" +
         "button to start the client.<table><tbody>\n" +
         "<tr><td>" +
         "<a href=\"";
         
   static final protected String manual_install_matter1b = 
         "/servlet/oem/edge/ed/odc/HelperInstall";
         
   static final protected String manual_install_matter2a = 
         //"\" onclick=\"resizeWindowN(400, 650);\" onkeypress=\"resizeWindowN(400, 650);\">\n" +
         "\">\n" +
         "<img src=\"//www.ibm.com/i/v14/buttons/arrow_rd.gif\" border=\"0\" alt=\"Install\" width=\"21\" height=\"21\" /></a></td>\n<td>" +
         "<a href=\"";
         
   static final protected String manual_install_matter2b = 
         "/servlet/oem/edge/ed/odc/HelperInstall";
         
   static final protected String manual_install_matter3 = 
         //"\" onclick=\"resizeWindowN(400, 650);\" onkeypress=\"resizeWindowN(400, 650);\">\n" +
         "\">\n" +
         "Manual install of Client Software for Customer Connect</a>" +
         "</td>\n</tr><tr>\n<td>" +
         "<a href=\"";
         
   static final protected String manual_install_matter4 = 
         "\">\n" +
         "<img src=\"//www.ibm.com/i/v14/buttons/arrow_rd.gif\" border=\"0\" alt=\"Launch\" width=\"21\" height=\"21\" /></a>\n" +
         "</td>\n<td>" +
         "<a href=\"";
         
   static final protected String manual_install_matter5 = 
         "\">\n" +
         "Launch Client Software for Customer Connect</a>\n" +
         "</td>\n</tr></tbody></table>\n";
                                        
                                        
   public static String getHeadMatter(String title) {
      return doctypehead_req + metamatter_req + metapragma + 
         "<title>IBM " + title + "</title>\n" + head_matter2;
   }
   
   public static String getHeadMatterXferDownload(String title, String redir) {
      return doctypehead_req + metamatter_req + metapragma + 
         "<meta http-equiv=\"Refresh\" content=\"1; url=" + redir + "\" />\n" +
         "<title>IBM " + title + "</title>\n" + head_matter2;
   }
   
   public static String getSignedMatter(String urlv, String desc, String contextpath) {
             
      String lval = 
         "launchr('" + contextpath + 
         "/servlet/oem/edge/ed/odc/desktop/signedappletblurb?manualurl=" + 
         URLEncoder.encode(urlv) + "&cmddesc=" + URLEncoder.encode(desc) +
         "', 'signedappletblurb', '450', '640');";
             
      return "<p /><table width=\"100%\"><tbody><tr><td align=\"right\"><span class=\"fnt\">\n" +
             "<a href=\"javascript:;\" onkeypress=\"" + lval + "\" onclick=\"" + lval + "\">signed applet technology</a>" +
             "</span></td></tr></tbody></table>";
   }                                    
   
   public static String getManualInstallMatter(String urlv, String contextpath) {
     // urlv already HAS servletcontext in front
      return manual_install_matter1a + contextpath + manual_install_matter1b + 
             urlv +
             manual_install_matter2a + contextpath + manual_install_matter2b + 
             urlv +             
             manual_install_matter3  + urlv +
             manual_install_matter4  + urlv +
             manual_install_matter5;
   }
   public static String getAppletMatter(String desc) {
      return "<p align=\"left\"><b><font size=\"+1\">" + desc + 
             "</font></b></p>\n";
   }
}
