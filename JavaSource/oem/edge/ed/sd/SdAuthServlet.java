package oem.edge.ed.sd;

/************************** BOF : HEADER *************************************/
/* IBM Confidential                                                          */
/* OCO Source Materials                                                      */
/* Identifier: ICC/PROFIT                                                    */
/* (C) Copyright IBM Corp. 2002, 2003                                        */
/* The source code for this program is not published or otherwise divested   */
/* of its trade secrets, irrespective of what has been deposited with the    */
/* U.S. Copyright Office                                                     */
/************************** RCS & COPYRT *************************************/
/************************** EOF : HEADER *************************************/
/////////////////////////////////////////////////////////////////////////////
//
//                            Edge 2.10
//                      Dev: Navneet Gupta
//
//////////////////////////////////////////////////////////////////////////////


import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.net.URLEncoder;// boo 10/12


public class SdAuthServlet extends HttpServlet {
   public final static String Copyright = "(C) Copyright IBM Corp. 2002, 2003";
    final static String version = "v111504.1: ";

    private static final String sourceServlet = "SdAuthServlet";
    private String propsFile;

    public void init(ServletConfig config) throws ServletException {

        super.init(config);
        this.propsFile = config.getInitParameter(SdUtils.PROPS_FILE_TAG);

    }


    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }



    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String user = null;
	String order = null;
	String[] userInfo = null;

        try {

            if( ! SdUtils.initialize(this.propsFile) ) {
                SdUtils.sendServiceUnavailable(request, response, SdUtils.INIT_FAILED);
                SdUtils.sendImpAlert("SdUtils failed to initialize");
                return;
            }


            boolean hasValidToken = false;
            boolean hasValidSsoCookie = false;
            boolean launchDSC = false;
            boolean smartDL = false;

            long fileID = 0;
            String fileOwner = null;
            String fileOrder = null;

            String token = request.getParameter("token");

            if(request.getParameter("launchDSC") != null)
                launchDSC = true;


            if(token != null) {

                userInfo = SdUtils.checkToken(token, sourceServlet);

		user = userInfo[0];
		order = userInfo[2];

                if(user != null) {
                    if(user.length() == 0) {
                        SdUtils.sendForbidden(request, response, SdUtils.TOKEN_EXPIRED);
			SdUtils.sendAlert(sourceServlet + ": checkToken returned false. QueryString: " + request.getQueryString());
                        return;
                    }
                    else
                        hasValidToken = true;
                }

            }


            if( ! hasValidToken ) {

                try {
                    fileID = Long.parseLong(request.getParameter("id"));
                }
                catch(NumberFormatException nfe) {
                    SdUtils.sendForbidden(request, response, SdUtils.ID_NOT_PARSABLE);
                    SdUtils.sendAlert(sourceServlet + ": ERROR! NumberFormatException thrown while parsing fileID: " + request.getParameter("id"));
                    return;
                }


                if (fileID > 0) {
                    fileOwner = SdUtils.getFileOwner(fileID);
                    fileOrder = SdUtils.getFileOrder(fileID);
                }


                user = request.getParameter("edsdTester");
		userInfo = new String[] {user, "EDSGN_ADMIN", "", "su", "", "fyuan@us.ibm.com"};

                if(user != null) {
                    if( ! SdUtils.checkAdmin(request, response) )
                        return;
                }
                else {
                    userInfo = SdUtils.checkSsoCookie(request, fileID);
		    user = userInfo[0];
		    order = userInfo[2];
                }


                if(user != null) {
                    if(user.length() == 0) {
                        SdUtils.sendForbidden(request, response, SdUtils.SSO_EXPIRED);
                        return;
                    }
                    else
                        hasValidSsoCookie = true;
                }

                String smart = request.getParameter("smart");
                if(smart != null && smart.equals("yes"))
                    smartDL = true;

            }




            if(hasValidSsoCookie) {
                if(userInfo[3].equals("su") || order.equals(fileOrder) || user.equals(fileOwner)) {
                    if(smartDL) {
						/* boo 10/12 */
						String baseURL = SdUtils.getBaseURL(request);
						String newToken = SdUtils.encode(userInfo);
						// redirect to ODC servlet DesktopServlet with ?handleSD=token?base_url=baseURL
						
						response.sendRedirect(SdUtils.getODCURL(request) + "?handleSD=" + newToken + "?base_url=" +  URLEncoder.encode(baseURL));
                        //SdOptions.startSmartDL(request, response, userInfo, fileID);
                    }
                    else {
			SdDownloadServlet.downloadFile(request, response, userInfo);
/*
String newToken = SdUtils.encode(userInfo);
String redirectURL =  SdUtils.getBaseURL(request) + "/servlet/SdDownloadServlet";
redirectURL += "/" + SdUtils.getFileName(fileID);
redirectURL += "?" + request.getQueryString();
redirectURL += "&token=" + newToken;
response.sendRedirect(redirectURL);
*/
                    }
                }
                else if(fileOwner == null) {
                    SdUtils.sendForbidden(request, response, SdUtils.ID_NOT_FOUND);
                    SdUtils.sendImpAlert("ID not found in table:" + fileID + " SsoCookieUser: " + user);
                }
                else {
                    SdUtils.sendForbidden(request, response, SdUtils.USER_MISMATCH);
                    SdUtils.sendImpAlert("User mismatch for fileID:" + fileID + " SsoCookieUser: " + user + " fileOwner: " + fileOwner);
                }

            }
            else if(hasValidToken) {
                if(launchDSC)
                    launchDesignSolutionsClient(request, response, token);
                else
                    sendNewToken(response, userInfo);
            }
            else {
                SdUtils.sendForbidden(request, response, SdUtils.AUTH_NULL);
                SdUtils.sendAlert(sourceServlet + ": No valid token or SsoCookie for fileID: " + fileID);
            }

        }
        catch (IllegalArgumentException iae) {

            try {
                SdUtils.sendForbidden(request, response, SdUtils.ID_NOT_FOUND);
            }
            catch(Throwable t1) {
                print("ERROR! The following exception was thrown for user: " + user + "\nStackTrace:\n" + SdUtils.getStackTrace(t1));
                throw new ServletException("503 Service Unavailable");
            }

            SdUtils.sendImpAlert("The following exception was thrown for queryString: " + request.getQueryString() + " The stacktrace is as follows:\n" + SdUtils.getStackTrace(iae));

        }
        catch (Throwable t) {

            try {
                SdUtils.sendServiceUnavailable(request, response, SdUtils.EXCEPTION);
            }
            catch(Throwable t1) {
                print("ERROR! The following exception was thrown for user: " + user + "\nStackTrace:\n" + SdUtils.getStackTrace(t1));
                throw new ServletException("503 Service Unavailable");
            }

            SdUtils.sendImpAlert("The following exception was thrown for user: " + user + "\nStackTrace:\n" + SdUtils.getStackTrace(t));

        }
    }




    private void sendNewToken(HttpServletResponse response, String[] userInfo) throws IOException {

        String newToken = SdUtils.encode(userInfo);
        String output = String.valueOf(SdUtils.TOKEN_DURATION) + ":" + newToken;

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.println(output);
        out.close();

    }




    private void launchDesignSolutionsClient(HttpServletRequest request, HttpServletResponse response, String token) throws IOException {

        String url = SdUtils.getBaseURL(request);

        String str = "SD\n"
                   + "-SD_TOKEN " + token + "\n"
                   + "-URL " + url + "\n";

        // do not use cache-control
        response.setContentType("application/x-ibm-edge-dsc");

        PrintWriter out = response.getWriter();
        out.println(str);
        out.close();

    }



    private void print(String s) {

        SdUtils.print(s, sourceServlet);

    }



}

