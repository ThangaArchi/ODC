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

import java.util.Hashtable;
import java.util.HashSet;
import java.util.Date;

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
/*     (C)Copyright IBM Corp. 2002-2004                                     */
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


public class SdQueryServlet extends HttpServlet {

    public final static String Copyright = "(C) Copyright IBM Corp. 2002, 2003";
    final static String version = "v091903.1: ";

    private static final String sourceServlet = "SdQueryServlet";
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

        String admin = request.getParameter("admin");
	String id = request.getParameter("id");
        String token = request.getParameter("token");
        String remoteAddr = request.getRemoteAddr();

        try {

            if( ! SdUtils.initialize(this.propsFile) ) {

                if(admin != null && admin.equals("yes")) {
                    SdUtils.handleAdminRequest(request, response);
                    return;
                }
		else if(id != null && id.equals("whoami")) {
		    SdUtils.returnRemoteHost(request, response);
                    return;
		}
                else {
                    SdUtils.sendServiceUnavailable(request, response, SdUtils.INIT_FAILED);
                    SdUtils.sendImpAlert("SdUtils failed to initialize");
                    return;
                }
            }


            if(admin != null && admin.equals("yes")) {
                SdUtils.handleAdminRequest(request, response);
                return;
            }

	    if(id != null && id.equals("whoami")) {
	        SdUtils.returnRemoteHost(request, response);
		return;
	    }

           /* if( ! SdUtils.authenticate() ) {
                SdUtils.sendServiceUnavailable(request, response, SdUtils.INTERNAL_AUTH_FAILURE);
                SdUtils.sendImpAlert("SdUtils failed to authenticate");
                return;
                }*/


	    String order = null;
	    String orderPrefix = null;

            boolean hasValidToken = false;


            if(token != null) {

		String[] userInfo = SdUtils.checkToken(token, sourceServlet);
                user = userInfo[0];
		String ent = userInfo[1];
		order = userInfo[2];
		orderPrefix = user;

		if(order.length() != 0 && ! ent.equals(SdUtils.OWNER_ENT)) {
		    int index = order.lastIndexOf('-');
		    if(index > 0)
		        orderPrefix = order.substring(0, index);
		    else
			print("Invalid order number: " + order + " in token for user: " + user + " and ent: " + ent);
		}

        // MPZ Change for 4.3.1
        // Due to longer user ids, the user ID and the order prefix may
	// no longer be an exact match. For users accessing orders with
	// advanced entitlements, the owning user ID was gleaned from
	// the order prefix. This may no longer be reliable. As a
	// result, if user is not found, then the order will be
        // looked up and its user ID will be used.

	// This must be done before the order is blanked out!!!

        HashSet userSet = SdUtils.getUserEntry(orderPrefix);

	// If orderPrefix did not map to a user, then we will need
	// to use the order number to find its owner and use that
	// owner as the orderPrefix.
        if(userSet == null) {
                Hashtable orderHash = SdUtils.getOrderEntry(order);
                if (orderHash != null) {
                        String orderUser = (String) orderHash.get(SdUtils.USER_ID);
                        if (orderUser != null) {
                                userSet = SdUtils.getUserEntry(orderUser);
				if (userSet != null) {
					orderPrefix = orderUser;
				}
                        }
                }
        }
        // End of MPZ Change 4.3.1

		if(userInfo[3].equals("su") || ent.equals(SdUtils.OWNER_ENT))
		    order = null;

                if(user != null) {
                    if(user.length() == 0) {
                        SdUtils.sendForbidden(request, response, SdUtils.TOKEN_EXPIRED);
			print("TOKEN_EXPIRED: remoteAddr: " + remoteAddr);
                        return;
                    }
                    else
                        hasValidToken = true;
                }

            }
            else {
                SdUtils.sendForbidden(request, response, SdUtils.AUTH_NULL);
		print("AUTH_NULL: remoteAddr: " + remoteAddr);
                return;
            }


            if( ! hasValidToken ) {
                SdUtils.sendForbidden(request, response, SdUtils.AUTH_FAILED);
                SdUtils.sendAlert(sourceServlet + ": WARNING! user authorization failed");
                return;
            }

            String output = null;


            if(request.getParameter("getSLA") != null)
                output = SdUtils.getSLA();
            else if(request.getParameter("completed") != null)
                output = SdUtils.updateCompleted(user, request.getParameter("completed"));
            else if(request.getParameter("id1") != null) {
                boolean result = SdUtils.compareFiles(request.getParameter("id1"), request.getParameter("id2"));
                output = String.valueOf(result);
            }
            else
                output = SdUtils.getUserInfo(orderPrefix, order);


            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            out.println(output);

            
        }
        catch (oem.edge.common.cipher.DecodeException de) {
            try {
                SdUtils.sendForbidden(request, response, SdUtils.AUTH_FAILED);
            }
            catch(Throwable t1) { }
            print(de.toString() + ". remoteAddr: " + remoteAddr);
        }
        catch (Throwable t) {

            System.err.println("\nException at " + SdUtils.formatter.format(new Date()) + "\n");

            print("ERROR! The following exception was thrown for user: " + user + "\nStackTrace:\n" + SdUtils.getStackTrace(t));

            try {
                SdUtils.sendServiceUnavailable(request, response, SdUtils.EXCEPTION);
            }
            catch(Throwable t1) {
                print("ERROR! The following exception was thrown for user: " + user + "\nStackTrace:\n" + SdUtils.getStackTrace(t1));
                throw new ServletException("503 Service Unavailable");
            }
            
        }
    }

    
    private void print(String s) {

        SdUtils.print(s, sourceServlet);

    }


}
