package oem.edge.ed.sd;
import java.util.Hashtable;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
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

public class SdOptions {

 public final static String Copyright = "(C) Copyright IBM Corp. 2002, 2003";
    final static String version = "v041103.1: ";


    static final String ibmDocType =
        "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
        "<?xml-stylesheet href=\"//www.ibm.com/data/css/v11/ns1.css\" type=\"text/css\"?>\n" +
        "<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">\n";



    static final String ibmHeader =

        "<link rel=\"stylesheet\" href=\"//www.ibm.com/data/css/v11/ns1.css\" type=\"text/css\" />\n" +

        "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />\n" +
        "<meta http-equiv=\"PICS-Label\" content=\'(PICS-1.1 \"http://www.icra.org/ratingsv02.html\" l gen true for \"http://www.ibm.com\" r (cz 1 lz 1 nz 1 oz 1 vz 1) \"http://www.rsac.org/ratingsv01.html\" l gen true for \"http://www.ibm.com\" r (n 0 s 0 v 0 l 0))\' />\n" +
        "<meta name=\"owner\" content=\"fyuan@us.ibm.com\" />\n" +
        "<meta name=\"abstract\" content=\"ASIC Connect\" />\n" +
        "<meta name=\"description\" content=\"ASIC Connect\" />\n" +
        "<meta name=\"keywords\" content=\"ASIC Connect\" />\n" +
        "<meta name=\"dc.language\" scheme=\"rfc1766\" content=\"en-us\" />\n" +
        "<meta name=\"dc.date\" scheme=\"iso8601\" content=\"2001-10-10\" />\n" +
        "<meta name=\"security\" content=\"public\" />\n" +
        "<meta name=\"copyright\" content=\"copyright (c) 2001 by IBM corporation\" />\n" +
        "<meta name=\"source\" content=\"v11 template generator, template 11.2.4\" />\n" +
        "<meta name=\"robots\" content=\"index,follow\" />\n" +
        "<meta name=\"ibm.country\" content=\"us\" />\n" +

        "<meta http-equiv=\"Pragma\" content=\"no-cache\" />\n" +
        "<meta http-equiv=\"Cache-Control\" content=\"no-cache\" />\n" +
        "<meta http-equiv=\"Expires\" content=\"0\" />\n" +

        "<style type=\"text/css\">\n" +
        "a.close:link    { text-decoration: underline; color: #ffffff; font-family: Arial, sans-serif; font-size: 10px; }\n" +
        "a.close:visited { text-decoration: underline; color: #ffffff; font-family: Arial, sans-serif; font-size: 10px; }\n" +
        "a.close:hover   { text-decoration: underline; color: #ffffff; font-family: Arial, sans-serif; font-size: 10px; }\n" +
        "</style>\n";



    static final String ibmTopBar =

        "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\">\n" +
        "<tr bgcolor=\"#006699\">\n" +
        "<td class=\"hbg\" height=\"35\" valign=\"middle\">\n" +
        "<img src=\"//www.ibm.com/i/v11/ibmlogo_small.gif\" width=\"56\" height=\"24\" border=\"0\" alt=\"IBM\" />\n" +
        "</td>\n" +
        "<td class=\"hbg\" align=\"right\" valign=\"middle\">\n" +
        "<a href=\"javascript:self.close()\" class=\"close\">close</a>\n" +
        "<img src=\"//www.ibm.com/i/v11/c.gif\" width=\"12\" height=\"1\" border=\"0\" alt=\"\" />\n" +
        "</td>\n" +
        "</tr>\n" +
        "</table>\n";


    private static final String bodyTop =
        "<br /> <br /> <strong><font size=\"+2\">Client Software for ASIC Connect</font></strong><br /> <br />\n";


    private static final String appletBlurbA =
        "<br /> <br /><table width=\"400\"><tr><td align=\"right\"><span class=\"fnt\"><a"; 

    private static final String appletBlurbB =
        ">signed applet technology</a></span></td></tr></table>\n";


    private static final String appName = "Client Software for ASIC Connect";

    private static String faq = null;

    private static final String[][] faqArr = {

        {"What is the " + appName + "?",
         "The " + appName + " is an application that lets you download your ASIC Connect orders blah, blah, blah, blah, blah, blah,blah, blah, blah, blah, blah, blah, blah, blah, blah,"},

        {"Why should I use the " + appName + "?",
         "Because blah, blah, blah, blah, blah, blah,blah, blah, blah, blah, blah, blah, blah, blah, blah, blah, blah,blah, blah, blah, blah, blah, blah, blah, blah, blah, blah, blah,blah, blah, blah, blah, blah"}

    };





    static {

        // FAQ title
        faq = "\n<div><br /><br /><strong><font size=\"+2\">Frequently Asked Questions about the " + appName + "</font></strong><br /> <br />\n";


        // FAQ questions
        faq += "<ol>\n";
        for(int i = 0; i < faqArr.length; i++) {
            faq += "<li>"
                 + "<a href=\"#" + (i+1) + "\">"
                 + faqArr[i][0]
                 + "</a>"
                 + "</li>\n";
        }
        faq += "</ol><br />\n";



        // and now the answers
        faq += "<ol>\n";
        for(int i = 0; i < faqArr.length; i++) {
            String str =
                   "<a name=\"" + (i+1) + "\">"
                 + "<strong>" + faqArr[i][0] + "</strong>"
                 + "</a><br /><br />"
                 + faqArr[i][1] + "<br /><br />";

	    faq += "<li>" + str + "</li>" + "\n";
        }
        faq += "</ol>\n";

        faq += "\n</div>\n";

//      temporary nullify faq
        faq = "";

    }





/*
    private static String option1a =
        "<b>Option 1:</b> Automated using <a href=\"";


    private static String option1b = 
        "\">signed applet technology</a><br /><br />\n";


    private static String option2a = 
         "<b>Option 2:</b> Manual installation and launch<br />\n" +
         "<br />\n" +
         "For customers who prefer to not used signed\n" +
         "applet technology, you may download, install\n" +
         "and setup the Client Software for ASIC Connect manually.\n" +
         "Once the client is installed, select the\n" +
         "launch\n" +
         "button to start the client.<table>\n" +
         "<tr><td><a href=\"";

    private static String option2b = 
         "\">" + SdUtils.getSecondaryButton("arrow_rd", "Install") + "</a></td>\n<td><a href=\"";

    private static String option2c = 
         "\">Manual Install for Client Software for ASIC Connect</a></td>\n" +
         "</tr><tr>\n" +
         "<td><a href=\"";

    private static String option2d = 
         "\">" + SdUtils.getSecondaryButton("arrow_rd", "Launch") + "</a></td>\n<td><a href=\"";


    private static String option2e = 
         "\">Launch Client Software for ASIC Connect</a></td>\n</tr></table>\n";




    private static String option3a =
         "<b>Option 3:</b> Do not install Client Software for ASIC Connect<br />\n" +
         "<br />For customers who prefer to not download the Client Software for ASIC Connect at all.\n" +
         "<table>\n" +
         "<tr><td><a href=\"";

    private static String option3b = 
         "\">" + SdUtils.getSecondaryButton("arrow_rd", "Launch") + "</a></td>\n<td><a href=\"";

    private static String option3c =
         "\">Just give me my order</a></td></tr></table>\n";


*/



    public static String getFAQ() {
        return faq;
    }





    static void startSmartDL(HttpServletRequest request, HttpServletResponse response, String[] userInfo, long fileID) throws IOException {




/*
         boolean isPreviewKit = false;

         Hashtable fileEntry = SdUtils.getFileEntry(fileID);

         if(fileEntry != null) {
            String filePath = (String)fileEntry.get(SdUtils.FILE_PATH);
            if(filePath != null && filePath.indexOf("PreviewKit") != -1)
                isPreviewKit = true;
         }
*/



         String newToken = SdUtils.encode(userInfo);
         String baseURL = SdUtils.getBaseURL(request);

         boolean MSIE = false;
         String userAgent =  request.getHeader("user-agent");
         if (userAgent != null && userAgent.indexOf("MSIE") >= 0) {
            MSIE = true;
         }

         StringBuffer appletStr = new StringBuffer();

         appletStr.append("<applet alt=\"\"");

         appletStr.append(" code=\"");
         appletStr.append(SdUtils.appletCode);
         appletStr.append("\"");

         appletStr.append(" codebase=\"");
         appletStr.append(SdUtils.appletCodebase);
         appletStr.append("\"");

         if ( ! MSIE) {
            appletStr.append(" archive=\"");
            appletStr.append(SdUtils.appletArchive);
            appletStr.append("\"");
         }

         appletStr.append(" width=\"400\" height=\"85\">\n");


         if (MSIE) {
            appletStr.append("<param name=\"cabbase\" value=\"");
            appletStr.append(SdUtils.appletCabbase);
            appletStr.append("\" />\n");
         }


         appletStr.append("<param name=\"COMMAND\" value=\"SD\" />\n");
         appletStr.append("<param name=\"DEBUG\" value=\"yes\" />\n");
         appletStr.append("<param name=\"-SD_TOKEN\" value=\"" + newToken + "\" />\n");
         appletStr.append("<param name=\"-URL\" value=\"" + baseURL + "\" />\n");


         appletStr.append("</applet>\n");


         String launchURL = request.getContextPath() + "/servlet/SdAuthServlet?launchDSC=true&token=" + newToken + "&app=SD";
         launchURL = java.net.URLEncoder.encode(launchURL, "UTF-8");
//         String installURL = SdUtils.helperInstall + launchURL + "&app=SD";


         StringBuffer outputStr = new StringBuffer();

         outputStr.append(ibmDocType);
         outputStr.append("\n<head>\n<title>Software download</title>\n");
         outputStr.append(ibmHeader);
         outputStr.append("\n</head>\n\n<body>\n");
         outputStr.append(SdUtils.getJavascriptLaunchCode());
         outputStr.append(ibmTopBar);
         outputStr.append("\n<div align=\"center\">\n");
         outputStr.append(bodyTop);
         outputStr.append(appletStr.toString());

         outputStr.append(appletBlurbA);
         outputStr.append(SdUtils.getJavascriptLaunchURL("/cc/servlet/oem/edge/ed/odc/desktop/signedappletblurb?manualurl=" + launchURL +"&cmddesc=SoftwareDownload", "Signed Applet Blurb", 450, 550));
         outputStr.append(appletBlurbB);

         outputStr.append("\n</div>\n");
         outputStr.append(faq);
         outputStr.append("\n</body>\n</html>\n");


/*
         outputStr.append(option1a + appletBlurbURL + option1b + appletStr.toString());
         outputStr.append(option2a + installURL + option2b + installURL + option2c + launchURL + option2d + launchURL +  option2e);
         outputStr.append(option3a + manualURL + option3b + manualURL + option3c);
*/



         response.setContentType("text/html");
         response.setHeader("Pragma", "no-cache");
         response.setHeader("Cache-Control", "no-cache");
         response.setDateHeader("Expires", 0);

         PrintWriter out = response.getWriter();

         out.println(outputStr.toString());

    }

}

