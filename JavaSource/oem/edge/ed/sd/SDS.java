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
//                            Edge 2.9
//                      Author: Navneet Gupta
//
//////////////////////////////////////////////////////////////////////////////

// import sun.misc.BASE64Decoder;

// import oem.edge.entitlement.engine.EdgeLogonContext;
// import oem.edge.ed.util.EDCMafsFile;
// import oem.edge.ed.util.PasswordUtils;
// import oem.edge.ed.sd.ordproc.Mailer;

import java.util.Hashtable;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.StringTokenizer;

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

public class SDS extends HttpServlet {

   public final static String Copyright = "(C) Copyright IBM Corp. 2002, 2003";
    private boolean initialized = false;
    private boolean authOK      = false;
    private boolean mailOK      = false;

    private String tablePath, configPath;
    private Hashtable table;
    private long lastModified;

    private String sla; //software license agreement

    private String afsCell, uid, pwd;

    private long nextAuthenticate;
    
// private EDCMafsFile authenticator = new EDCMafsFile();

    private int blockSizeInKB;

    private String adminStr;

    private String[] admins;

    private String outFile, errFile, stdOutFile, stdErrFile;

    private String mailHost1, mailHost2, mailTo, mailCc;

    private String dce_monitor, hostname;

    private static final SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy-HH:mm:ss");
    private static final SimpleDateFormat customerFormatter = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm z");    // to show customer expiration date

    private static final long AUTH_DURATION = 6 * 60 * 60 * 1000; // 6 hours

    // 401 UNAUTHORIZED
    private static final int AUTH_NULL               = 0;
    private static final int AUTH_FAILED             = 1;
    private static final int NOT_ADMIN               = 2;
    

    // 403 FORBIDDEN
    private static final int ID_NULL                 = 3;
    private static final int ID_NOT_PARSABLE         = 4;
    private static final int ID_NOT_FOUND            = 5;
    private static final int ORDER_EXPIRED           = 6;
    private static final int MUST_ACCEPT_SLA         = 7;


    // 503 SERVICE UNAVAILABLE
    private static final int REQUIRED_FIELD_MISSING  = 8;
    private static final int INTERNAL_AUTH_FAILURE   = 9;
    private static final int FILE_NOT_FOUND          = 10;
    private static final int NULL_INPUT_STREAM       = 11;
    private static final int NULL_OUTPUT_STREAM      = 12;
    private static final int EXCEPTION               = 13;
    private static final int INIT_FAILED             = 14;
    private static final int NULL_LOGON_CONTEXT      = 15;
    private static final int INVALID_LOGON_CONTEXT    = 16;
    private static final int INVALID_CODE_FOR_METHOD = 17;



    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }



    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

}


