package oem.edge.ets.fe;

/************************** BOF : HEADER *************************************/
/* IBM Confidential                                                          */
/* OCO Source Materials                                                      */
/* Identifier: IBM Customer Connect                                          */
/* (C) Copyright IBM Corp. 2002,2003                                       */
/* The source code for this program is not published or otherwise divested   */
/* of its trade secrets, irrespective of what has been deposited with the    */
/* U.S. Copyright Office                                                     */
/************************** EOF : HEADER *************************************/
/*																			 */
/*	File Name 	: 	EdesignServicesServlet.java	     */
/*	Release		:	3.7.1				 */
/*	Description	:	Displays Collab and Hosting Services */
/*	Created By	: 	Sathish, Sandra				 */
/*	Date		:	6/11/2003	 */
/*****************************************************************************/
/*  Change Log 	: 	Please Enter Changed on, Changed by and Desc  */
/*****************************************************************************/

/**
 * @author: Sathish,Sandra
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oem.edge.amt.EdgeAccessCntrl;
import oem.edge.common.DbConnect;
import oem.edge.common.Global;
import oem.edge.common.SysLog;
import oem.edge.common.RSA.RSAKeyPair;
import oem.edge.common.cipher.ODCipherData;
import oem.edge.common.cipher.ODCipherRSA;
import oem.edge.common.cipher.ODCipherRSAFactory;

import com.ibm.as400.webaccess.common.ConfigObject;
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


public class ETSServicesServlet extends javax.servlet.http.HttpServlet {

  public final static String Copyright = "(C) Copyright IBM Corp.  2002, 2003";

    private static String CLASS_VERSION = "1.11";


    public ETSServicesServlet() {
	super();
    }

    public String encode(Hashtable htDetails,String sOption) throws Exception {


	try {


		ODCipherRSA edgecipher = null;

		ODCipherRSAFactory fac = ODCipherRSAFactory.newFactoryInstance();
		try {
			edgecipher = fac.newInstance(Global.encode_keypath + "boulder.key");
		} catch(Throwable t) {
			System.out.println("Error loading CipherFile! [" + Global.encode_keypath + "boulder.key" + "]");
		}

//	    RSAKeyPair keypair = new RSAKeyPair();
//	    keypair = RSAKeyPair.load(Global.encode_keypath + "boulder.key");

	    //RSAKeyPair keypair = new RSAKeyPair(Global.encode_keypath + "boulder.key");


//	    ODCipherRSA edgecipher = new ODCipherRSA(keypair);


	    ConfigObject co = new ConfigObject();
	    co.setProperty("EDGEID",htDetails.get("USERID").toString());
	    co.setProperty("LAST",htDetails.get("LASTNAME").toString());
	    co.setProperty("FIRST",htDetails.get("FIRSTNAME").toString());
	    co.setProperty("EMAIL",htDetails.get("EMAIL").toString());
	    co.setProperty("COMPANY",htDetails.get("COMPANY").toString());
	    co.setProperty("STATE",htDetails.get("STATE").toString());
	    co.setProperty("COUNTRY",htDetails.get("COUNTRY").toString());
	    co.setProperty("COMMAND",htDetails.get("COMMAND").toString());
	    if (sOption.equals("COLLAB")) {
		co.setProperty("PN",htDetails.get("PROJECTCOUNT").toString());
		StringTokenizer st = new StringTokenizer(htDetails.get("PROJECTS").toString(),"'");
		int i = 1;
		while (st.hasMoreTokens()) {
		    co.setProperty("P" + i + "",st.nextToken());
		    i = i + 1;
		}
	    }
	    co.setProperty("SCOPE",htDetails.get("SCOPE").toString());

	    String sToEncode = co.toString();

	    ODCipherData cipherdata = edgecipher.encode(60 * 10,sToEncode);
	    String sEncodedString = cipherdata.getExportString();


	    return sEncodedString;

	} catch (Exception e ) {
	    throw e;
	}
    }


    public static String getClassVersion() {
	return CLASS_VERSION;
    }


    public String getCollabOptionString(Connection con,HttpServletRequest request,EdgeAccessCntrl es,String sScope, String sOp) throws Exception {


	String sEncodedString = "";

	try {

	    //Global.Init();

	    String sProjects = "";

	    Vector vDetails = getCollabProjects(con,es.gUSERN);
	    int iProjectCount = 0;

	    for (int i=0; i < vDetails.size(); i++) {
		if (i == 0) {
		    sProjects = (String) vDetails.elementAt(i);
		} else {
		    sProjects = sProjects + "'" + (String) vDetails.elementAt(i);
		}
		iProjectCount = iProjectCount + 1;
	    }

	    String sCompanyName = "";
	    sCompanyName = getUsersCompany(con,es);

	    /*
	    if (es.gDECAFTYPE.trim().toUpperCase().equals("I")) {
		sCompanyName = "IBM";
	    } else if (es.gDECAFTYPE.trim().toUpperCase().equals("E")) {
		sCompanyName = getDecafCompany(con,es.gUSERN);
	    } else {
		sCompanyName = "";
	    }
	    */

	    Hashtable hsDetails = new Hashtable();

	    hsDetails.put("USERID",es.gIR_USERN);
	    hsDetails.put("LASTNAME", es.gLAST_NAME);
	    hsDetails.put("FIRSTNAME",es.gFIRST_NAME);
	    hsDetails.put("EMAIL",es.gEMAIL);
	    hsDetails.put("COMPANY",sCompanyName);
	    hsDetails.put("STATE", es.gSTATE);
	    hsDetails.put("COUNTRY",es.gCOUNTRY);
	    hsDetails.put("COMMAND",sOp);
	    hsDetails.put("PROJECTCOUNT",iProjectCount + "");
	    hsDetails.put("PROJECTS",sProjects);
	    hsDetails.put("SCOPE",sScope);

	    sEncodedString = encode(hsDetails,"COLLAB");

	} catch (Exception e) {
	    throw e;
	}

	return sEncodedString;
    }

    /*
      public Vector getCollabProjects(Connection con, String sUserId,String sApplication) throws SQLException, Exception {

      StringBuffer sb = new StringBuffer();
      PreparedStatement pstmt = null;
      ResultSet rs = null;
      Vector vDetails = new Vector();

      try {
      sb.append("SELECT CUSTOMER_PROJNAME FROM EDESIGN.SERVICES_PROJECTS WHERE EDGE_USERID = ? AND APPLICATION=?");

      pstmt = con.prepareStatement(sb.toString());
      pstmt.setString(1,sUserId.trim());
      pstmt.setString(2,sApplication);
      rs = pstmt.executeQuery();

      while (rs.next()) {
      vDetails.addElement(rs.getString("CUSTOMER_PROJNAME"));
      }
      } catch (SQLException e) {
      throw e;
      } catch (Exception e) {
      throw e;
      } finally {
      if (pstmt != null)
      pstmt = null;
      if (rs != null)
      rs = null;
      }
      return vDetails;
      }
    */

    public Vector getCollabProjects(Connection con, String sUserId) throws SQLException, Exception {

	StringBuffer sb = new StringBuffer();
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	Vector vDetails = new Vector();

	try {

	    //vDetails = AccessCntrlFuncs.getUserDataTypeValuesEdgeId(sUserId,"ETS_PROJECTS","PROJECT_NAME");

	      sb.append("SELECT PROJECT_NAME FROM ETS.ETS_PROJECTS WHERE PROJECT_ID IN (SELECT USER_PROJECT_ID FROM ETS.ETS_USERS WHERE USER_ID = ?) for READ ONLY");

	      pstmt = con.prepareStatement(sb.toString());
	      pstmt.setString(1,sUserId.trim());

	      rs = pstmt.executeQuery();

	      while (rs.next()) {
	    	  vDetails.addElement(rs.getString("PROJECT_NAME"));
	      }

	} catch (SQLException e) {
	    throw e;
	} catch (Exception e) {
	    throw e;
	} finally {
		ETSDBUtils.close(rs);
		ETSDBUtils.close(pstmt);
	}

	return vDetails;
    }



    public String getConnectivityTestString(Connection con,HttpServletRequest request,EdgeAccessCntrl es,String sScope, String sOp) throws Exception {


	String sEncodedString = "";

	try {

	    String sCompanyName = "";

	    sCompanyName = getUsersCompany(con,es);

	    /*
	    if (es.gDECAFTYPE.trim().toUpperCase().equals("I")) {
		sCompanyName = "IBM";
	    } else if (es.gDECAFTYPE.trim().toUpperCase().equals("E")) {
		sCompanyName = EdesignMyAccount.getUsersCompany(con,es);
	    } else {
		sCompanyName = "";
	    }
	    */

	    Hashtable hsDetails = new Hashtable();

	    hsDetails.put("USERID",es.gIR_USERN);
	    hsDetails.put("LASTNAME", es.gLAST_NAME);
	    hsDetails.put("FIRSTNAME",es.gFIRST_NAME);
	    hsDetails.put("EMAIL",es.gEMAIL);
	    hsDetails.put("COMPANY",sCompanyName);
	    hsDetails.put("STATE", es.gSTATE);
	    hsDetails.put("COUNTRY",es.gCOUNTRY);
	    hsDetails.put("COMMAND",sOp);
	    hsDetails.put("SCOPE",sScope);

	    sEncodedString = encode(hsDetails,"CON");

	} catch (Exception e) {
	    throw e;
	}

	return sEncodedString;
    }


    public String getConferenceMultiPlatform(Connection con,HttpServletRequest request,EdgeAccessCntrl es,String sScope, String sOp) throws Exception {


	String sEncodedString = "";

	try {

	    String sProjects = "";

	    Vector vDetails = getCollabProjects(con,es.gUSERN);
	    int iProjectCount = 0;

	    for (int i=0; i < vDetails.size(); i++) {
		if (i == 0) {
		    sProjects = (String) vDetails.elementAt(i);
		} else {
		    sProjects = sProjects + "'" + (String) vDetails.elementAt(i);
		}
		iProjectCount = iProjectCount + 1;
	    }


	    String sCompanyName = "";
	    sCompanyName = getUsersCompany(con,es);

	    /*
	    if (es.gDECAFTYPE.trim().toUpperCase().equals("I")) {
		sCompanyName = "IBM";
	    } else if (es.gDECAFTYPE.trim().toUpperCase().equals("E")) {
		sCompanyName = EdesignMyAccount.getUsersCompany(con,es.gUSERN);
	    } else {
		sCompanyName = "";
	    }
	    */

	    Hashtable hsDetails = new Hashtable();

	    hsDetails.put("USERID",es.gIR_USERN);
	    hsDetails.put("LASTNAME", es.gLAST_NAME);
	    hsDetails.put("FIRSTNAME",es.gFIRST_NAME);
	    hsDetails.put("EMAIL",es.gEMAIL);
	    hsDetails.put("COMPANY",sCompanyName);
	    hsDetails.put("STATE", es.gSTATE);
	    hsDetails.put("COUNTRY",es.gCOUNTRY);
	    hsDetails.put("COMMAND",sOp);
	    hsDetails.put("SCOPE",sScope);
	    hsDetails.put("PROJECTCOUNT",iProjectCount + "");
	    hsDetails.put("PROJECTS",sProjects);

	    sEncodedString = encode(hsDetails,"COLLAB");

	} catch (Exception e) {
	    throw e;
	}

	return sEncodedString;
    }


    public String getDistributionString(Connection con, HttpServletRequest request,EdgeAccessCntrl es, String sScope, String sOp) throws Exception {


	String sEncodedString = "";

	try {

	    String sCompanyName = "";
	    sCompanyName = getUsersCompany(con,es);

	    /*
	    if (es.gDECAFTYPE.trim().toUpperCase().equals("I")) {
		sCompanyName = "IBM";
	    } else if (es.gDECAFTYPE.trim().toUpperCase().equals("E")) {
		sCompanyName = EdesignMyAccount.getUsersCompany(con,es.gUSERN);
	    } else {
		sCompanyName = "";
	    }
	    */

	    Hashtable hsDetails = new Hashtable();

	    hsDetails.put("USERID",es.gIR_USERN);
	    hsDetails.put("LASTNAME", es.gLAST_NAME);
	    hsDetails.put("FIRSTNAME",es.gFIRST_NAME);
	    hsDetails.put("EMAIL",es.gEMAIL);
	    hsDetails.put("COMPANY",sCompanyName);
	    hsDetails.put("STATE", es.gSTATE);
	    hsDetails.put("COUNTRY",es.gCOUNTRY);
	    hsDetails.put("COMMAND",sOp);
	    hsDetails.put("SCOPE",sScope);

	    sEncodedString = encode(hsDetails,"EDU");


	} catch (Exception e) {
	    throw e;
	}

	return sEncodedString;

    }


    public String getDistributionTestString(Connection con, HttpServletRequest request,EdgeAccessCntrl es, String sScope, String sOp) throws Exception {


	String sEncodedString = "";

	try {

	    String sCompanyName = "";
	    sCompanyName = getUsersCompany(con,es);

	    /*
	      if (es.gDECAFTYPE.trim().toUpperCase().equals("I")) {
		sCompanyName = "IBM";
	    } else if (es.gDECAFTYPE.trim().toUpperCase().equals("E")) {
		sCompanyName = EdesignMyAccount.getUsersCompany(con,es.gUSERN);
	    } else {
		sCompanyName = "";
	    }
	    */

	    Hashtable hsDetails = new Hashtable();

	    hsDetails.put("USERID",es.gIR_USERN);
	    hsDetails.put("LASTNAME", es.gLAST_NAME);
	    hsDetails.put("FIRSTNAME",es.gFIRST_NAME);
	    hsDetails.put("EMAIL",es.gEMAIL);
	    hsDetails.put("COMPANY",sCompanyName);
	    hsDetails.put("STATE", es.gSTATE);
	    hsDetails.put("COUNTRY",es.gCOUNTRY);
	    hsDetails.put("COMMAND",sOp);
	    hsDetails.put("SCOPE",sScope);

	    sEncodedString = encode(hsDetails,"Eastern US");


	} catch (Exception e) {
	    throw e;
	}

	return sEncodedString;

    }


    public String getHostingOptionString(Connection con, HttpServletRequest request,EdgeAccessCntrl es,String sScope, String sOp) throws Exception {


	String sEncodedString = "";

	try {

	    String sProjects = "";

	    Vector vDetails = getCollabProjects(con,es.gUSERN);
	    int iProjectCount = 0;

	    for (int i=0; i < vDetails.size(); i++) {
		if (i == 0) {
		    sProjects = (String) vDetails.elementAt(i);
		} else {
		    sProjects = sProjects + "'" + (String) vDetails.elementAt(i);
		}
		iProjectCount = iProjectCount + 1;
	    }

	    String sCompanyName = "";
	    sCompanyName = getUsersCompany(con, es);

	    /*
	    if (es.gDECAFTYPE.trim().toUpperCase().equals("I")) {
		sCompanyName = "IBM";
	    } else if (es.gDECAFTYPE.trim().toUpperCase().equals("E")) {
		sCompanyName = EdesignMyAccount.getUsersCompany(con,es.gUSERN);
	    } else {
		sCompanyName = "";
	    }
	    */

	    Hashtable hsDetails = new Hashtable();

	    hsDetails.put("USERID",es.gIR_USERN);
	    hsDetails.put("LASTNAME", es.gLAST_NAME);
	    hsDetails.put("FIRSTNAME",es.gFIRST_NAME);
	    hsDetails.put("EMAIL",es.gEMAIL);
	    hsDetails.put("COMPANY",sCompanyName);
	    hsDetails.put("STATE", es.gSTATE);
	    hsDetails.put("COUNTRY",es.gCOUNTRY);
	    hsDetails.put("COMMAND",sOp);
	    hsDetails.put("PROJECTCOUNT",iProjectCount + "");
	    hsDetails.put("PROJECTS",sProjects);
	    hsDetails.put("SCOPE",sScope);

	    sEncodedString = encode(hsDetails,"HOST");


	} catch (Exception e) {
	    throw e;
	}

	return sEncodedString;

    }


    public String getHostingTestString(Connection con,HttpServletRequest request,EdgeAccessCntrl es,String sScope, String sOp) throws Exception {


	String sEncodedString = "";

	try {

	    String sCompanyName = "";
	    sCompanyName = getUsersCompany(con,es);

	    /*
	    if (es.gDECAFTYPE.trim().toUpperCase().equals("I")) {
		sCompanyName = "IBM";
	    } else if (es.gDECAFTYPE.trim().toUpperCase().equals("E")) {
		sCompanyName = EdesignMyAccount.getUsersCompany(con,es.gUSERN);
	    } else {
		sCompanyName = "";
	    }
	    */

	    Hashtable hsDetails = new Hashtable();

	    hsDetails.put("USERID",es.gIR_USERN);
	    hsDetails.put("LASTNAME", es.gLAST_NAME);
	    hsDetails.put("FIRSTNAME",es.gFIRST_NAME);
	    hsDetails.put("EMAIL",es.gEMAIL);
	    hsDetails.put("COMPANY",sCompanyName);
	    hsDetails.put("STATE", es.gSTATE);
	    hsDetails.put("COUNTRY",es.gCOUNTRY);
	    hsDetails.put("COMMAND",sOp);
	    hsDetails.put("SCOPE",sScope);

	    sEncodedString = encode(hsDetails,"DST");

	} catch (Exception e) {
	    throw e;
	}

	return sEncodedString;
    }


    public String getInstantMessagingString(Connection con,HttpServletRequest request,EdgeAccessCntrl es,String sScope, String sOp) throws Exception {


	String sEncodedString = "";

	try {

	    String sCompanyName = "";
	    sCompanyName = getUsersCompany(con,es);

	    /*
	    if (es.gDECAFTYPE.trim().toUpperCase().equals("I")) {
		sCompanyName = "IBM";
	    } else if (es.gDECAFTYPE.trim().toUpperCase().equals("E")) {
		sCompanyName = EdesignMyAccount.getUsersCompany(con,es.gUSERN);
	    } else {
		sCompanyName = "";
	    }
	    */

	    Hashtable hsDetails = new Hashtable();

	    hsDetails.put("USERID",es.gIR_USERN);
	    hsDetails.put("LASTNAME", es.gLAST_NAME);
	    hsDetails.put("FIRSTNAME",es.gFIRST_NAME);
	    hsDetails.put("EMAIL",es.gEMAIL);
	    hsDetails.put("COMPANY",sCompanyName);
	    hsDetails.put("STATE", es.gSTATE);
	    hsDetails.put("COUNTRY",es.gCOUNTRY);
	    hsDetails.put("COMMAND",sOp);
	    hsDetails.put("SCOPE",sScope);

	    sEncodedString = encode(hsDetails,"IMS");

	} catch (Exception e) {
	    throw e;
	}

	return sEncodedString;
    }


    public String getStreamingMediaString(Connection con,HttpServletRequest request,EdgeAccessCntrl es,String sScope, String sOp) throws Exception {


	String sEncodedString = "";

	try {

	    String sCompanyName = "";
	    sCompanyName = getUsersCompany(con,es);

	    /*
	    if (es.gDECAFTYPE.trim().toUpperCase().equals("I")) {
		sCompanyName = "IBM";
	    } else if (es.gDECAFTYPE.trim().toUpperCase().equals("E")) {
		sCompanyName = EdesignMyAccount.getUsersCompany(con,es.gUSERN);
	    } else {
		sCompanyName = "";
	    }
	    */

	    Hashtable hsDetails = new Hashtable();

	    hsDetails.put("USERID",es.gIR_USERN);
	    hsDetails.put("LASTNAME", es.gLAST_NAME);
	    hsDetails.put("FIRSTNAME",es.gFIRST_NAME);
	    hsDetails.put("EMAIL",es.gEMAIL);
	    hsDetails.put("COMPANY",sCompanyName);
	    hsDetails.put("STATE", es.gSTATE);
	    hsDetails.put("COUNTRY",es.gCOUNTRY);
	    hsDetails.put("COMMAND",sOp);
	    hsDetails.put("SCOPE",sScope);

	    sEncodedString = encode(hsDetails,"STM");

	} catch (Exception e) {
	    throw e;
	}

	return sEncodedString;
    }


    public String getStreamingMediaTestString(Connection con,HttpServletRequest request,EdgeAccessCntrl es,String sScope, String sOp) throws Exception {


	String sEncodedString = "";

	try {

	    String sCompanyName = "";
	    sCompanyName = getUsersCompany(con,es);

	    /*
	    if (es.gDECAFTYPE.trim().toUpperCase().equals("I")) {
		sCompanyName = "IBM";
	    } else if (es.gDECAFTYPE.trim().toUpperCase().equals("E")) {
		sCompanyName = EdesignMyAccount.getUsersCompany(con,es.gUSERN);
	    } else {
		sCompanyName = "";
	    }
	    */

	    Hashtable hsDetails = new Hashtable();

	    hsDetails.put("USERID",es.gIR_USERN);
	    hsDetails.put("LASTNAME", es.gLAST_NAME);
	    hsDetails.put("FIRSTNAME",es.gFIRST_NAME);
	    hsDetails.put("EMAIL",es.gEMAIL);
	    hsDetails.put("COMPANY",sCompanyName);
	    hsDetails.put("STATE", es.gSTATE);
	    hsDetails.put("COUNTRY",es.gCOUNTRY);
	    hsDetails.put("COMMAND",sOp);
	    hsDetails.put("SCOPE",sScope);

	    sEncodedString = encode(hsDetails,"STT");

	} catch (Exception e) {
	    throw e;
	}

	return sEncodedString;
    }


    public void service(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.io.IOException {

	response.setContentType("text/html");
	PrintWriter out = response.getWriter();

	if (!Global.loaded) {
	    Global.Init();
	}

	Connection con = null;
	PreparedStatement stmt = null;

	try {

	    boolean bAvailable = false;

	    con = ETSDBUtils.getConnection();

	    EdgeAccessCntrl es = new EdgeAccessCntrl();
	    if (!es.GetProfile(response,request,con)) {
		return;
	    }


	    String sOp = request.getParameter("servicesop");
	    if (sOp == null) {
		sOp = "";
	    }

	    if (sOp.trim().equals("")) {
		sOp = request.getParameter("op");
		if (sOp == null) {
		    sOp = "";
		}
	    }

	    String sScope = request.getParameter("servicesscope");
	    if (sScope == null) {
		sScope = "";
	    }

	    if (sScope.trim().equals("")) {
		sScope = request.getParameter("sc");
		if (sScope == null) {
		    sScope = "";
		}
	    }



	    boolean bConfEntitled = true;    //false;
	    boolean bDropBoxEntitled = true; //false;

	    /*
	    if (es.gDECAFTYPE.trim().toUpperCase().equals("I")) {
		bConfEntitled = true;
		bDropBoxEntitled = true;
	    } else if (es.gDECAFTYPE.trim().toUpperCase().equals("E")) {
		bConfEntitled = es.Qualify("DSGN_CONF", "tg_member=MD");
		bDropBoxEntitled = es.Qualify("DSGN_DROPBOX", "tg_member=MD");
	    } else {
		bConfEntitled = es.Qualify("DSGN_CONF", "tg_member=MD");
		bDropBoxEntitled = es.Qualify("DSGN_DROPBOX", "tg_member=MD");
	    }
	    */

	    /*
	    ResourceBundle rb=ResourceBundle.getBundle("oem.edge.common.gwa");

	    String sInstMsgFlag = rb.getString("gwa.edfeInstMsg").trim();
	    if (sInstMsgFlag == null || sInstMsgFlag.trim().equals("")) {
		sInstMsgFlag = "N";
	    } else {
		sInstMsgFlag = sInstMsgFlag.trim();
	    }

	    String sHostTest = rb.getString("gwa.edfeHostTest").trim();
	    if (sHostTest == null || sHostTest.trim().equals("")) {
		sHostTest = "N";
	    } else {
		sHostTest = sHostTest.trim();
	    }

	    String sDeduTest = rb.getString("gwa.edfeDeduTest").trim();
	    if (sDeduTest == null || sDeduTest.trim().equals("")) {
		sDeduTest = "N";
	    } else {
		sDeduTest = sDeduTest.trim();
	    }

	    String sStrmMedia = rb.getString("gwa.edfeStrmMedt").trim();
	    if (sStrmMedia == null || sStrmMedia.trim().equals("")) {
		sStrmMedia = "N";
	    } else {
		sStrmMedia = sStrmMedia.trim();
	    }

	    String sStrmTest = rb.getString("gwa.edfeStrmTest").trim();
	    if (sStrmTest == null || sStrmTest.trim().equals("")) {
		sStrmTest = "N";
	    } else {
		sStrmTest = sStrmTest.trim();
	    }
	    */

	    String sInstMsgFlag = "N";
	    String sHostTest = "N";
	    String sDeduTest = "N";
	    String sStrmMedia  = "N";
	    String sStrmTest = "N";

	    String redirectLoc = "";
	    String sEncodedString = "";

	    if (sOp.equals("1")) {
		// collaboration
		if (es.Qualify("DSGN_COLLAB", "tg_member=MD")) {
		    sEncodedString = getCollabOptionString(con,request,es,sScope,sOp);
		    redirectLoc = Global.design_host_url + "?compname=" + sEncodedString;
		    bAvailable = true;
		} else {
		    bAvailable = false;
		}
	    } else if (sOp.equals("2")) {
		// hosting
		if (es.Qualify("DSGN_HOST", "tg_member=MD")) {
		    sEncodedString = getHostingOptionString(con,request,es,sScope,sOp);
		    redirectLoc = Global.design_collab_url + "?compname=" + sEncodedString;
		    bAvailable = true;
		} else {
		    bAvailable = false;
		}
	    } else if (sOp.equals("4")) {
		// instant messaging
		if (sInstMsgFlag.equalsIgnoreCase("Y") && es.Qualify("DSGN_IM", "tg_member=MD")) {
		    sEncodedString = getInstantMessagingString(con,request,es,sScope,sOp);
		    redirectLoc = Global.design_collab_url + "?compname=" + sEncodedString;
		    bAvailable = true;
		} else {
		    bAvailable = false;
		}
	    } else if (sOp.equals("5")) {
		// web conference multi platform

		if (bConfEntitled) {
		    sEncodedString = getConferenceMultiPlatform(con,request,es,sScope,sOp);
		    //redirectLoc = Global.design_collab_url + "?compname=" + sEncodedString;
			redirectLoc = Global.design_newodc_url + "?compname=" + sEncodedString;
		    bAvailable = true;
		} else {
		    bAvailable = false;
		}
	    } else if (sOp.equals("10")) {
		// connectivity
		sEncodedString = getConnectivityTestString(con,request,es,sScope,sOp);
		redirectLoc = Global.design_collab_url + "?compname=" + sEncodedString;
		bAvailable = true;
	    } else if (sOp.equals("20")) {
		// hosting test drive
		if (sHostTest.equalsIgnoreCase("Y")) {
		    sEncodedString = getHostingTestString(con,request,es,sScope,sOp);
		    redirectLoc = Global.design_collab_url + "?compname=" + sEncodedString;
		    bAvailable = true;
		} else {
		    bAvailable = false;
		}
	    } else if (sOp.equals("3")) {
		// distributed education
		sEncodedString = getDistributionString(con,request,es,sScope,sOp);
		redirectLoc = Global.design_collab_url + "?compname=" + sEncodedString;
		bAvailable = true;
	    } else if (sOp.equals("30")) {
		// distributed education test drive
		sEncodedString = getDistributionTestString(con,request,es,sScope,sOp);
		redirectLoc = Global.design_collab_url + "?compname=" + sEncodedString;
		bAvailable = true;
	    } else if (sOp.equals("6")) {
		// Streaming media
		if (sStrmMedia.equalsIgnoreCase("Y")) {
		    sEncodedString = getStreamingMediaString(con,request,es,sScope,sOp);
		    redirectLoc = Global.design_collab_url + "?compname=" + sEncodedString;
		    bAvailable = true;
		} else {
		    bAvailable = false;
		}
	    } else if (sOp.equals("60")) {
		// Streaming media test drive
		if (sStrmTest.equalsIgnoreCase("Y")) {
		    sEncodedString = getStreamingMediaTestString(con,request,es,sScope,sOp);
		    redirectLoc = Global.design_collab_url + "?compname=" + sEncodedString;
		    bAvailable = true;
		} else {
		    bAvailable = false;
		}
	    } else if (sOp.equals("7")) {

		// Dropbox

		if (bDropBoxEntitled) {
		    sEncodedString = getFileTransfer(con,request,es,sScope,sOp);
		    //redirectLoc = Global.design_collab_url + "?compname=" + sEncodedString;
			redirectLoc = Global.design_dropbox_url + "?compname=" + sEncodedString;
		    bAvailable = true;
		} else {
		    bAvailable = false;
		}
	    } else if (sOp.equals("8")) {
		// web folder option
		sEncodedString = getWebFolder(con,request,es,sScope,sOp);
		redirectLoc = Global.design_collab_url + "?compname=" + sEncodedString;
		bAvailable = true;
	    }

	    if (bAvailable) {
		response.sendRedirect(redirectLoc);
		return;
	    } else {
		// the service is not available or the user is not entitled..
	    }

	} catch (SQLException e) {
	    SysLog.log(SysLog.ERR, this, e);
	} catch (Exception e) {
	    SysLog.log(SysLog.ERR, this, e);
	} finally {
	    ETSDBUtils.close(con);
	    out.flush();
	    out.close();
	}
    }


    public String getFileTransfer(Connection con,HttpServletRequest request,EdgeAccessCntrl es,String sScope, String sOp) throws Exception {


	String sEncodedString = "";

	try {

	    String sCompanyName = "";
	    sCompanyName = getUsersCompany(con,es);

	    /*
	    if (es.gDECAFTYPE.trim().toUpperCase().equals("I")) {
		sCompanyName = "IBM";
	    } else if (es.gDECAFTYPE.trim().toUpperCase().equals("E")) {
		sCompanyName = EdesignMyAccount.getUsersCompany(con,es.gUSERN);
	    } else {
		sCompanyName = "";
	    }
	    */

	    Hashtable hsDetails = new Hashtable();

	    hsDetails.put("USERID",es.gIR_USERN);
	    hsDetails.put("LASTNAME", es.gLAST_NAME);
	    hsDetails.put("FIRSTNAME",es.gFIRST_NAME);
	    hsDetails.put("EMAIL",es.gEMAIL);
	    hsDetails.put("COMPANY",sCompanyName);
	    hsDetails.put("STATE", es.gSTATE);
	    hsDetails.put("COUNTRY",es.gCOUNTRY);
	    hsDetails.put("COMMAND",sOp);
	    hsDetails.put("SCOPE",sScope);

	    sEncodedString = encode(hsDetails,"XFR");

	} catch (Exception e) {
	    throw e;
	}

	return sEncodedString;
    }

    public String getWebFolder(Connection con,HttpServletRequest request,EdgeAccessCntrl es,String sScope, String sOp) throws Exception {


	String sEncodedString = "";

	try {

	    String sCompanyName = "";
	    sCompanyName = getUsersCompany(con,es);

	    /*
	    if (es.gDECAFTYPE.trim().toUpperCase().equals("I")) {
		sCompanyName = "IBM";
	    } else if (es.gDECAFTYPE.trim().toUpperCase().equals("E")) {
		sCompanyName = EdesignMyAccount.getUsersCompany(con,es.gUSERN);
	    } else {
		sCompanyName = "";
	    }
	    */

	    Hashtable hsDetails = new Hashtable();

	    hsDetails.put("USERID",es.gIR_USERN);
	    hsDetails.put("LASTNAME", es.gLAST_NAME);
	    hsDetails.put("FIRSTNAME",es.gFIRST_NAME);
	    hsDetails.put("EMAIL",es.gEMAIL);
	    hsDetails.put("COMPANY",sCompanyName);
	    hsDetails.put("STATE", es.gSTATE);
	    hsDetails.put("COUNTRY",es.gCOUNTRY);
	    hsDetails.put("COMMAND",sOp);
	    hsDetails.put("SCOPE",sScope);

	    sEncodedString = encode(hsDetails,"FDR");

	} catch (Exception e) {
	    throw e;
	}

	return sEncodedString;
    }


    private static String getDecafCompanyName(Connection con, String sUserId) throws SQLException, Exception {

	String sCompany = "";
	String sQuery = "";
	Statement stmt = null;
	ResultSet rs = null;

	try {

	    StringBuffer sb = new StringBuffer();

	    sb.append("SELECT ASSOC_COMPANY FROM DECAF.USERS WHERE USERID = '" + sUserId + "' for READ ONLY");

	    SysLog.log(SysLog.DEBUG,"ETSServicesServlet:getDecafCompanyName()","Query : " + sb.toString());

	    stmt = con.createStatement();
	    rs = stmt.executeQuery(sb.toString());

	    while (rs.next()) {
		sCompany = rs.getString("ASSOC_COMPANY");
		if (sCompany == null) {
		    sCompany = "";
		} else {
		    sCompany = sCompany.trim();
		}
	    }

	} catch (SQLException e) {
	    SysLog.log(SysLog.ERR,"ETSServicesServlet:getDecafCompanyName()","Error : " + e.toString());
	    throw e;
	} catch (Exception ex) {
	    SysLog.log(SysLog.ERR,"ETSServicesServlet:getDecafCompanyName()","Error : " + ex.toString());
	    throw ex;
	} finally {
	    //FoundrySQLUtils.close(rs);
	    if (rs != null) {
	        try {
	            rs.close();
	            rs = null;
	        } catch (SQLException x) {
	        	SysLog.log(SysLog.ERR,"FoundrySQLUtils:close()",x.toString());
	        }
	    }

	    //FoundrySQLUtils.close(stmt);
	    if (stmt != null) {
	        try {
	            stmt.close();
	            stmt = null;
	        } catch (SQLException x) {
		    SysLog.log(SysLog.ERR,"FoundrySQLUtils:close()",x.toString());
	        }
	    }
	}

	return sCompany;

    }

    private String getUsersCompany(Connection con, EdgeAccessCntrl es) throws SQLException, Exception {

	String sCompany = "";

	try {

	    if (es.gDECAFTYPE.trim().toUpperCase().equals("I")) {
		sCompany = "IBM";
	    } else if (es.gDECAFTYPE.trim().toUpperCase().equals("E")) {
		sCompany = getDecafCompanyName(con,es.gUSERN);
	    } else {
		sCompany = "";
	    }

	}catch (Exception e) {
	    throw e;
	}

	return sCompany;

    }
}
