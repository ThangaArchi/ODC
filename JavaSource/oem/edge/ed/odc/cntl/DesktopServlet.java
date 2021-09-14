package oem.edge.ed.odc.cntl;

import java.net.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.*;
import javax.servlet.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

import org.apache.log4j.*;
import org.apache.log4j.spi.LoggerRepository;

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
/*     (C)Copyright IBM Corp. 2002-2004,2005,2006                           */
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


public class DesktopServlet extends javax.servlet.http.HttpServlet {

  //
  // Timeout inner classes
  //
  
  // Used for tunnel status update snapshots
   class DesktopTO extends Timeout {
      long interval;
      public DesktopTO(long delta) {
         super(delta, "DtSrvStatus", null);
      
         interval = delta;
      }
   
      public void tl_process(Timeout to) {
   
        // Add timeout for specified delta to do status update
         TimeoutManager tmgr = TimeoutManager.getGlobalManager();
         tmgr.removeTimeout("DtSrvStatus");
         tmgr.addTimeout(new DesktopTO(interval));
      
         DesktopServlet.processStatus();
      }
   }

  // Used for ping processing
   static int pingidval = 1;
   class DesktopPingTO extends Timeout {
      long interval;
      public DesktopPingTO(long delta) {
         super(delta, "DtSrvPing", null);
      
         interval = delta;
      }
   
      public void tl_process(Timeout to) {
   
         String v = DesktopServlet.getDesktopProperty("edodc.doRealPings");
         boolean doRealPings = (v != null && v.equalsIgnoreCase("true"));
         Vector realpings = null;
         if (doRealPings) {
            realpings = new Vector();
         }
   
         Enumeration e = ServletSessionManager.getSessions();
         int localid = pingidval++;
         while(e.hasMoreElements()) {
            String did = (String)e.nextElement();
            HttpTunnelSession session =
               ServletSessionManager.getSession(did, false);
            
            if (doRealPings) {
               String edgeid = session.getEdgeId();
               String ipaddr = session.getIPAddress();
               if (edgeid == null) edgeid = session.desktopID();
               if (edgeid != null && ipaddr != null) {
                  realpings.addElement(edgeid);
                  realpings.addElement(ipaddr);
               } else {
                  DebugPrint.println(DebugPrint.ERROR, 
                                     "Can't do realping to session, no edgeid/IP: " +
                                     edgeid + "/" + ipaddr);
               }
            }
            DesktopServlet.sendTunnelPing(session, localid);
         }
      
        // realPingCommand takes pingid, outputfilename, and edgeid,  ip pairs
         if (doRealPings && realpings.size() > 0) {
      
           // Give those pings a chance to fly
            try {
               Thread.sleep(300);
            } catch(Throwable ttt) {}
      
            try {
               String cmd = 
                  DesktopServlet.findFileWhereEver(
                     DesktopServlet.getDesktopProperty("edodc.realPingCommand"));
         
               String arr[] = new String[realpings.size() + 3];
               int idx = 0;
               int i;
               arr[idx++] = cmd;
               arr[idx++] = "" + localid;
               arr[idx++] = 
                  DesktopServlet.getDesktopProperty("edodc.realPingOutputFile");
               DebugPrint.println(DebugPrint.DEBUG2, 
                                  "About to do: " + cmd + " " + 
                                  localid + " " + arr[idx-1]);
                            
               for(i=0; i < realpings.size(); i++) {
                  arr[idx++] = (String)realpings.elementAt(i);
                  DebugPrint.println(DebugPrint.DEBUG2, "\t" + arr[idx-1]);
               }
         
               java.lang.Runtime.getRuntime().exec(arr);
            
            } catch(Throwable tt) {
               DebugPrint.println(DebugPrint.ERROR, "Error doing realping!");
               DebugPrint.println(DebugPrint.ERROR, tt);
            }
         }
      
        // Add timeout for specified delta to do status update
         TimeoutManager tmgr = TimeoutManager.getGlobalManager();
         tmgr.removeTimeout("DtSrvPing");
         tmgr.addTimeout(new DesktopPingTO(interval));
      }
   }

   public static final int REWRAP_LOGIN_TIMEOUT_SECS  = 60*10;
   public static final int EDGE_CIPHER_TIMEOUT_SECS   = 60*60;
   public static final int LOCAL_CIPHER_TIMEOUT_SECS  = 60*10;
   public static final int PUBLISHED_APPS_CIPHER_TIMEOUT_SECS = 60*3;
 
   public static String servletcontextpath = "";
   public static boolean servletcontextpathset = false;
   
   public static String usrPath          = null;
   public static String pwdPath          = null;
   
   private static DesktopManager mgr     = null;
   
   private static long lastInterval      = 0;
   private static long statusInterval    = 0;
   private static long pingInterval      = 0;
   private static String DomainIP        = null;
   private static String DomainIPReal    = null;
   
   final String JSESSIONID_STR = ";jsessionid=";    
   
   private static String buildInfo = "<br/>Built: NA<br/>";
   
  // loginpage handles ALL login page activity
  // 
  // Note: This can be accessed from package
  // 
   protected static DesktopLogin      loginpage    = null;
   
  // Reloading Properties
   private   static ReloadingProperty deskprops    = null;
   
  // Reloading Properties
   private   static MappingFiles      mappingfiles = null;
   
   protected static MappingFiles getMappings() { return mappingfiles; }
   
   public static String getBuildInfo() {
      return buildInfo;
   }
   
   public static String getDomainIPReal() {
      return DomainIPReal;
   }
   public static String getDomainIP() {
      return DomainIP;
   }
   
   public static String getDesktopProperty(String a, String b) {
      return deskprops.getProperty(a,b);
   }
   public static String getDesktopProperty(String a) {
      return deskprops.getProperty(a,null);
   }
   
   private static String servingDirectories  = null;
   
  // following properties are for testing only
   private static int count = 0;
   
   private static final long EXPIRE_DELTA = 1000*15;
   
   private static String counter = new String();
   
   private static java.lang.String odcResName = "edesign_edodc_desktop";
                                        
                
   public static String getCommandDescription(String token) {
   
      String ret = null;
      ODCipherData cd = null;
      try { 
         cd = edgecipher.decode(token);
      } catch(DecodeException de) {
         try {
            cd = cipher.decode(token);
         } catch(DecodeException de2) {
         } catch(Throwable tt2) {
         }
      } catch(Throwable tt) {
      }
      
      try {
         if (cd != null) {
            String thisToken = cd.getString();
            ConfigObject co = new ConfigObject();
            co.fromString(thisToken);
            String tunnelcommand = co.getProperty("COMMAND");
            ret = serviceNameFromCommand(tunnelcommand);
         }
      } catch(Throwable tt) {}
      return ret;
   }
      
   public static Hashtable dataFromToken(ODCipherRSA cipher, String token) {
      return SearchEtc.dataFromToken(cipher, token);
   }
   
   public static String commandFromTunnelCommand(String tunnelcommand) {
      String ret = tunnelcommand;
      
      try {
         int cmd = Integer.parseInt(tunnelcommand);
         switch(cmd) {
            case  1: tunnelcommand = "ODC";    break;
            case  2: tunnelcommand = "DSH";    break;
            case  3: tunnelcommand = "EDU";    break;
            case  4: tunnelcommand = "IMS";    break;
            case  5: tunnelcommand = "NEWODC"; break;
            case  6: tunnelcommand = "STM";    break;
            case  7: tunnelcommand = "XFR";    break;
            case  8: tunnelcommand = "FDR";    break;
            
              // 9 and 3 are the same for now
            case  9: tunnelcommand = "EDU";    break;
            
            case 10: tunnelcommand = "CON";    break;
            case 20: tunnelcommand = "DST";    break;
            case 30: tunnelcommand = "EDT";    break;
            case 60: tunnelcommand = "STT";    break;
            default:                           break;
         }
      } catch(NumberFormatException nfe) {
      }
      if (tunnelcommand.equalsIgnoreCase("DGP"))tunnelcommand = "NEWODC";
      return tunnelcommand;
   }
   
   public static String serviceNameFromCommand(String tunnelcommand) {
      String commanddesc = null;
      tunnelcommand = commandFromTunnelCommand(tunnelcommand);
      try {
         
         if (tunnelcommand.equals("ODC")    || 
             tunnelcommand.equals("NEWODC")) {
            String v = getDesktopProperty("edodc.support_collab");
            if (v != null && v.equalsIgnoreCase("true")) {
               commanddesc = "Conferencing service";
            }
         } else if (tunnelcommand.equals("EDU")) {
            String v = getDesktopProperty("edodc.support_education");
            if (v != null && v.equalsIgnoreCase("true")) {
               commanddesc = "Classroom service";
            }
         } else if (tunnelcommand.equals("DSH")) {
            String v = getDesktopProperty("edodc.support_hosting");
            if (v != null && v.equalsIgnoreCase("true")) {
               commanddesc = "Hosting service";
            }
         } else if (tunnelcommand.equals("FTP")) {
            String v = getDesktopProperty("edodc.support_pftp");
            if (v != null && v.equalsIgnoreCase("true")) {
               commanddesc = "Secure file transfer";
            }
         } else if (tunnelcommand.equals("IMS")) {
            String v = getDesktopProperty("edodc.support_im");
            if (v != null && v.equalsIgnoreCase("true")) {
               commanddesc = "Instant messaging";
            }
         } else if (tunnelcommand.equals("STM")) {
            String v = getDesktopProperty("edodc.support_rm");
            if (v != null && v.equalsIgnoreCase("true")) {
               commanddesc = "Real Media";
            }
         } else if (tunnelcommand.equals("XFR")) {
            String v = getDesktopProperty("edodc.support_xfr");
            if (v != null && v.equalsIgnoreCase("true")) {
               commanddesc = "Dropbox Service";
            }
         } else if (tunnelcommand.equals("FDR")) {
            String v = getDesktopProperty("edodc.support_fdr");
            if (v != null && v.equalsIgnoreCase("true")) {
               commanddesc = "Grid Service";
            }
         } else if (tunnelcommand.equals("DOC")) {
           //subu 04/15/03
            String v = getDesktopProperty("edodc.support_doc");
            if (v != null && v.equalsIgnoreCase("true")) {
               commanddesc = "DesktopOnCall Service";
            }
         } else if (tunnelcommand.equals("CON")) {
            commanddesc = "Connectivity Test";
         }
      } catch(NullPointerException ne) {}
      return commanddesc;
   }
   
  //
  // Sigh. When using sendRedirect, we need to call encodeRedirectURL. If we are
  //  passing a fullyQualified URL, then no prob ... BUT, if we pass in a URL
  //  which does NOT have the scheme://host/ ... then it will PREPEND that YUK.
  //  Ok, thats cool, ... except that it also prepends the ServletContext. If I
  //  already have that on the URL, it will be there twice.
  //
  // Bigger sigh.
  //
  // Biggest sigh of all. This was a 4.x problem, 5.x does away with it, so turn it
  // off unless we ask for it
  //
   protected static String fixForRedirect(String redirect) {
   
      boolean dofix = getDesktopProperty(
         "edodc.do4.xRedirectFix", "false").equalsIgnoreCase("true");
   
      if (dofix && servletcontextpath.length() > 0 &&
          redirect.indexOf(servletcontextpath) == 0) {
         redirect = redirect.substring(servletcontextpath.length());
      }
      return redirect;
   }
   
   
   boolean suppZApplet = false;
   boolean suppZNSApplet = false;
   boolean suppZAnyApplet = false;
   
   static boolean tokenChecking = true;
   private static ODCipherRSA SDcipher = null;
   private static ODCipherRSA edgecipher = null;
   private static ODCipherRSA cipher = null;
   private static ODCipherRSA publishedAppCipher = null;
   private static boolean glueSet = false;
/**
 * DesktopServlet constructor comment.
 */
   public DesktopServlet() {
      super();
   }
   
   static public boolean doTokenChecking() { return tokenChecking; }
   
   
   protected static ConfigSection getUserInfoFromToken(ConfigObject co) {
      String user=co.getProperty("EDGEID");
      String company=co.getProperty("COMPANY");
      String country=co.getProperty("COUNTRY");
      String first=co.getProperty("FIRST");
      String last=co.getProperty("LAST");
      String email=co.getProperty("EMAIL");
      String state=co.getProperty("STATE");
      String projects=null;
      int cnt = 1;
      String ts;
      while((ts = co.getProperty("P"+(cnt++))) != null) {
         if (projects != null) {
            projects += ":";
         } else {
            projects = new String();
         }
         projects += ts;
      }
      
      ConfigSection cs = new ConfigSection(user);
      
      if (user != null)     cs.setProperty("userid", user);
      if (email != null)    cs.setProperty("email", email);
      if (company != null)  cs.setProperty("company", company);
      if (country != null)  cs.setProperty("country", country);
      if (state != null)    cs.setProperty("state", state);
      if (projects != null) cs.setProperty("projects", projects);
      if (last != null)     cs.setProperty("last", last);
      if (first != null)    cs.setProperty("first", first);
      
     // Mark this as an Edge user
      cs.setProperty("EDGEAUTH", "TRUE");
      
      return cs;
   }
      
   static protected String makehash(String s) {
      String ret = null;
      try {
         java.security.MessageDigest d;
         d = java.security.MessageDigest.getInstance("MD5");
         
         d.update(s.getBytes());
         byte arr[] = d.digest();
         StringBuffer ans = new StringBuffer();
         for(int i=0 ; i < arr.length; i++) {
            String v = Integer.toHexString(((int)arr[i]) & 0xff);
            if (v.length() == 1) ans.append("0");
            ans.append(v);
         }
         
         ret = ans.toString();
      } catch(Exception ee) {
         ee.printStackTrace(System.out);
      }
      return ret;
   }
   
  // Had a dropping leading 0 bug ...
   static protected String makehash_old(String s) {
      String ret = null;
      try {
         java.security.MessageDigest d;
         d = java.security.MessageDigest.getInstance("MD5");
         
         d.update(s.getBytes());
         byte arr[] = d.digest();
         StringBuffer ans = new StringBuffer();
         for(int i=0 ; i < arr.length; i++) {
            String v = Integer.toHexString(((int)arr[i]) & 0xff);
            ans.append(v);
         }
         
         ret = ans.toString();
      } catch(Exception ee) {
         ee.printStackTrace(System.out);
      }
      return ret;
   }
   
   
  // Routine to authenticate and generate a token using the CustomerConnect
  //  method. If 
   public static
   String authenticateAndMakeToken(String op, String scope,
                                   String userid, String inpw, String sftp, 
                                   javax.servlet.http.HttpServletResponse res){
         
      String compname = null;
      
     // We only work if loginpageDoCustomerConnect is set to true
      boolean doCC       = getDesktopProperty(
         "edodc.loginpageDoCustomerConnect", "false").equalsIgnoreCase("true");
         
     // If rollover is TRUE, then if probs with authenticator, fallback to 
     //  HTTPS method
      boolean doHTTPSRollover  = getDesktopProperty(
         "edodc.doHTTPSRollover", "true").equalsIgnoreCase("true");
         
     // Force LDAP property reload
      boolean forceLDAPReload  = getDesktopProperty(
         "edodc.forceLDAPReload", "false").equalsIgnoreCase("true");
         
     // If above is True, we stay local if doLDAPAuthentication is true
      boolean doLDAPAuth = getDesktopProperty(
         "edodc.doLDAPAuthentication", "false").equalsIgnoreCase("true");
         
      boolean doLDAPAuthTest = getDesktopProperty(
         "edodc.LDAPAuthenticationTest", "false").equalsIgnoreCase("true");
         
     // Fold the ID to lower case
      if (userid != null) userid = userid.toLowerCase();
      
      if (doCC              && 
          op     != null    && 
          userid != null    && 
          inpw   != null    &&
          
         // JMC 1/31/07 - CSR11095 workaround for LDAP bug
          inpw.length() > 0 && 
          inpw.trim().length() > 0) {
          
          
         boolean authenticated     = false;
         boolean authworked        = false;
         boolean amtlookupWorked   = false;
             
        // If doing LDAP auth, then also have to build token locally
         if (doLDAPAuth) {
            DebugPrint.printlnd(DebugPrint.INFO, "LDap Authenticate: " + 
                                userid);
               
            try {
               Class authclass = Class.forName(
                  "oem.edge.authentication.MultisiteAuthenticator");
               
               Class classparms[] = new Class[2];
               
              // See if we should force a reload
               if (forceLDAPReload) {
                  DebugPrint.printlnd(DebugPrint.WARN,
                                      "FORCING LDAP Config reread!");
                  try {
                     java.lang.reflect.Method reconfigmeth = 
                        authclass.getMethod("reconfigure", new Class[0]);
                     reconfigmeth.invoke(null, new Object[0]);
                  } catch(Exception mex) {
                     DebugPrint.printlnd(DebugPrint.ERROR,
                                         "Error with Multisite reconfigure!");
                     DebugPrint.printlnd(DebugPrint.ERROR, mex);
                  }
               }
               
               classparms[0] = String.class;
               classparms[1] = String.class;
               java.lang.reflect.Method authmeth = 
                  authclass.getMethod("authenticate", classparms);
                  
               String parmarr[] = new String[2];
               parmarr[0] = userid;
               parmarr[1] = inpw;
               Boolean lauth = (Boolean)authmeth.invoke(null, parmarr);
               authenticated = lauth.booleanValue();
               authworked = true;
            } catch(Exception ee) {
               DebugPrint.printlnd(DebugPrint.ERROR,
                                   "Authentication process Failed: " + 
                                   ee.toString());
               DebugPrint.printlnd(DebugPrint.ERROR, ee);
            }               
            
            if (!authenticated && doLDAPAuthTest) {
               authenticated = true;
               DebugPrint.printlnd(DebugPrint.ERROR,
                                   "Auth TESTING: force to Authenticated for: "
                                   + userid);
            }
            
           // If authenticated, look up AMT data
            if (authenticated) {
               
               DebugPrint.printlnd(DebugPrint.INFO, 
                                   "LDap worked. Lookup user");
                                      
               if (res != null) res.setHeader("authenticated", "true");
                  
               int intop = Misc.opFromCommand(op);
                  
              // We want projects in token if ODC or DSH
               String projapp = null;
               if (intop == 1 || intop == 5) projapp = "O";
               else if (intop == 2)          projapp = "D";
                  
               Vector amtv = null;
               try {
                 // moved to URF, ditched the projapp. We don't use it anyway
                  amtv = UserRegistryFactory.getInstance().lookup(userid, false, 
                                                                  true, false);
                 //amtv = AMTQuery.getAMTByUser(userid, true, false, projapp);
               } catch(Exception ee) {
                  DebugPrint.printlnd(DebugPrint.ERROR, 
                                      "Error getAMTByUser!");
                  DebugPrint.printlnd(DebugPrint.ERROR, ee); 
               }
                  
               if (amtv != null && amtv.size() > 0) {
                  
                  AMTUser amtuser = (AMTUser)amtv.elementAt(0);
                  
                  DebugPrint.printlnd(DebugPrint.INFO, 
                                      "User successfully found:\n" + 
                                      amtuser.toString());
                  
                  if (amtv.size() > 1) {
                     DebugPrint.printlnd(DebugPrint.ERROR, 
                                         "User has multiple AMT entries!: \n" +
                                         amtuser.toString());
                  }
                  
                  amtlookupWorked = true;
                  
                  String company   = amtuser.getCompany();
                  String user      = amtuser.getUser();
                  String firstname = amtuser.getFirstName();
                  String lastname  = amtuser.getLastName();
                  String emailaddr = amtuser.getEmail();
                  String country   = amtuser.getCountry();
                  String state     = amtuser.getState();
                     
                  if (company == null) company = "";
                  if (country == null) country = "";
                     
                  boolean isIBM = company.equalsIgnoreCase("IBM");
                  boolean entitled = true;
                  entitled = false;
                  switch(intop) {
                     case  1: // OLDODC 
                        entitled=amtuser.isEntitled("DSGN_COLLAB");
                        break;
                     case  2: // DSH
                        entitled=amtuser.isEntitled("DSGN_HOST");
                        break;
                     case  4: // IMS
                        entitled=amtuser.isEntitled("DSGN_IM");
                        break;
                     case  5: // NEWODC
                        entitled = isIBM || amtuser.isEntitled("DSGN_CONF");
                        break;
                           
                     case  7: // XFR
                        entitled = isIBM || amtuser.isEntitled("DSGN_CONF");
                        if (!entitled && (sftp != null &&
                                          sftp.equalsIgnoreCase("true"))) {
                           entitled=amtuser.isEntitled("DSGN_SFTP");
                        }
                        break;
                           
                     case  3: // EDU No entitlement needed
                     case  9: // EDU
                     case  6: // STM
                     case  8: // FDR
                     case 10: // CON
                     case 20: // DST
                     case 30: // EDT
                     case 60: // STT
                     default:
                        entitled = true;
                        break;
                  }
                     
                  if (entitled) {
                     DebugPrint.printlnd(DebugPrint.INFO, 
                                         "User " + userid + 
                                         " entitled for op = " + op);
                     ConfigObject nco = new ConfigObject();
                     nco.setProperty("COMMAND", op);
                     nco.setProperty("EDGEID",  user);
                     nco.setProperty("COMPANY", company);
                     nco.setProperty("COUNTRY", country);
                     if (firstname != null) 
                        nco.setProperty("FIRST", firstname);
                     if (lastname  != null) 
                        nco.setProperty("LAST", lastname);
                     if (emailaddr != null) 
                        nco.setProperty("EMAIL", emailaddr);
                     if (state     != null) 
                        nco.setProperty("STATE", state);
                        
                     Vector pv = amtuser.getProjects();
                     Enumeration enum = (pv != null)?pv.elements():null; 
                     int cnt   = 0;
                     while(enum != null && enum.hasMoreElements()) {
                        nco.setProperty("P" + (++cnt),
                                        (String)enum.nextElement());
                     }
                     if (cnt > 0) {
                        nco.setProperty("PN" , ""+cnt);
                     }
                        
                     compname = generateCipher(nco.toString());
                     if (res != null) res.setHeader("compname", compname);
                  } else {
                     DebugPrint.printlnd(DebugPrint.INFO, 
                                         "User " + userid + 
                                         " NOT entitled for op = " + op);
                  }
               } else {
                  if (res != null) res.setHeader("authenticated", "false");
                  DebugPrint.printlnd(DebugPrint.INFO4, 
                                      "User " + userid + 
                                      " NOT found in local amt.users DB");
               }
            } else {
               if (res != null) res.setHeader("authenticated", "false");
               if (res != null) res.setHeader("authenticationproblem", "true");
            }
         }
         
        // If we are NOT doing LDAP OR, we are, and there was some problem
        //  AND rolling over to HTTPS is enabled
         if (!doLDAPAuth   ||
             ((!authworked ||
               (authenticated && !amtlookupWorked))
              && doHTTPSRollover)) {
             
            if (doLDAPAuth) {
               DebugPrint.printlnd(DebugPrint.ERROR,
                                   "Rolling over to authenticate " + 
                                   userid + " via HTTPS!");
            }
             
           // Get any overrides to FE token stuff
            String feURL       = getDesktopProperty("edodc.feURL");
            String feIRServlet = getDesktopProperty("edodc.feIRServlet");
            String feEDServlet = getDesktopProperty("edodc.feEDServlet");
            
            String connectinfo[] = 
               oem.edge.ed.odc.tunnel.common.Misc.getConnectInfoBTV(
                  userid, inpw, op, scope,
                  feURL, feIRServlet, feEDServlet);
               
            if (connectinfo != null) {
               
               if (res != null) res.setHeader("authenticated", "true");
                  
               DebugPrint.printlnd(DebugPrint.INFO2, 
                                   "Authenticated to Customer Connect as" +
                                   userid);
                  
               try { 
                  ODCipherData cd = edgecipher.decode(connectinfo[1]);
                  if (cd.isCurrent()) {
                        
                    // sftp COULD be used to access dropbox directly.
                    //  Enforce checking here.
                     boolean enabled = true;
                     if (op.equalsIgnoreCase("XFR") || 
                         op.equalsIgnoreCase("7")) {
                           
                        ConfigObject co = new ConfigObject();
                        co.fromString(cd.getString());
                           
                        String lu = co.getProperty("edgeid");
                        String lc = co.getProperty("company", "");
                        if (lu != null) {
                           enabled = mappingfiles.enabledForDropbox(lu, lc);
                        } else {
                           enabled = false;
                        } 
                     }
                        
                     if (enabled) {
                        compname = 
                           cipher.encode(REWRAP_LOGIN_TIMEOUT_SECS, 
                                         cd.getString()).getExportString();
                        if (compname != null) {
                           if (res != null) res.setHeader("compname", 
                                                          compname);
                        }
                     }
                  }
               } catch(DecodeException de) {
                  DebugPrint.println(DebugPrint.INFO2, 
                                     "Token decode problem");
                  if (res != null) res.setHeader("authenticated", "false");
               } catch(Throwable tt) {
                  DebugPrint.println(DebugPrint.DEBUG, tt);
                  if (res != null) res.setHeader("authenticated", "false");
               }
            } else {
               DebugPrint.println(DebugPrint.INFO2, 
                                  "null back from authenticate");
               if (res != null) res.setHeader("authenticated", "false");
            }
         }
      } else {   
         if (inpw == null || inpw.trim().length() == 0) { 
            DebugPrint.println(DebugPrint.WARN, "Login attempted for user[" + userid + 
                               "] with a null or empty password. Ldap relief");
         }
      }
      return compname;
   }
   
   
/**
 * Insert the method's description here.
 * Creation date: (7/30/00 12:55:46 PM)
 * @param req javax.servlet.http.HttpServletRequest
 * @param res javax.servlet.http.HttpServletResponse
 * @exception javax.servlet.ServletException The exception description.
 * @exception java.io.IOException The exception description.
 */
   public void doGet(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse res) throws javax.servlet.ServletException, java.io.IOException {

      res.setContentType("text/html");
   
      if (!servletcontextpathset) {
         servletcontextpathset = true;
         servletcontextpath = req.getContextPath();
         System.out.println("Servlet Context Path in DesktopServlet = " + servletcontextpath);
         FE.setContextPath(servletcontextpath);
      }
      
      if (DebugPrint.getLevel() >= DebugPrint.DEBUG) {
         DebugPrint.println(DebugPrint.DEBUG, 
                            "\n-----------  Request Parms ----------");
         Enumeration enuml = req.getParameterNames();
         while(enuml.hasMoreElements()) {
            String arr[];
            String parm = (String)enuml.nextElement();
            arr=req.getParameterValues(parm);
            DebugPrint.println(DebugPrint.DEBUG, 
                               "Parm = " + parm);
            if (!parm.equalsIgnoreCase("pw")       &&
                !parm.equalsIgnoreCase("curpass")  && 
                !parm.equalsIgnoreCase("npass1")   && 
                !parm.equalsIgnoreCase("npass2")   && 
                !parm.equalsIgnoreCase("pass")     && 
                !parm.equalsIgnoreCase("password")) {
               for(int i=0; arr != null && i < arr.length; i++) {
                  DebugPrint.println(DebugPrint.DEBUG, 
                                     "\t" + arr[i]);
               }
            }
         }
      }
      
      if (false && DebugPrint.doDebug()) {
         HttpSession sest = req.getSession(true);
         
         String turl = req.getRequestURI();
         
         String qs = req.getQueryString();
         if (qs != null) {
            turl = turl + "?" + qs;
         }
                        
         String didv = (String)sest.getAttribute("did");
         DebugPrint.println(DebugPrint.DEBUG, 
                            "In URL: [" + turl + "]\n" +
                            "DID   : [" + didv + "]\n" +
                            "encode: [" + res.encodeURL(turl) + "]\n" +
                            "redir : [" + res.encodeRedirectURL(turl) + "]\n");
                            
         DebugPrint.println(DebugPrint.DEBUG, 
                            "Adding Affinity: " + 
                            res.encodeRedirectURL(servletcontextpath 
                                  +"/servlet/oem/edge/edodc/tunnel/ReceiveFromClient"));
         DebugPrint.println(DebugPrint.DEBUG, 
                            "Adding Affinity: " + 
                            res.encodeRedirectURL(servletcontextpath  
                                       +"/servlet/oem/edge/edodc/tunnel/SendToClient"));
      }
      
     // If redirecting is ON, come here specifically
     // Otherwise, we have to assume that Session Affinity is ON
     //
     // Other oddities. encodeURL just does URLRewriting while encodeRedirectURL
     //  does both rewriting AND prepending the scheme, servername AND CONTEXT!
     //  This last bit about appending the context makes things difficult, and
     //  we need to be aware that the context cannot be prepended! If the URL
     //  already has the scheme et al, then no adjustments other than Rewriting
     //  occur.
     // 
      String phead = "";
      String doRedir = getDesktopProperty("useIPRedirect", "false");
      if (doRedir != null && doRedir.equalsIgnoreCase("true")) {
         phead = req.getScheme() + "://" + DomainIP;
      }
      String head = phead + servletcontextpath;
      
      String command = null;
      String p1 = null;
      String p2 = null;
      String action = req.getPathInfo();
      if ( action != null ) {
         StringTokenizer st = new StringTokenizer(action, "/");
         if (st.hasMoreTokens()) command = st.nextToken();
         if (st.hasMoreTokens()) p1 = st.nextToken();
         if (st.hasMoreTokens()) p2 = st.nextToken();
         
        // command = action.replace('/', ' ').trim();
      }
      
      if ( command == null ) {
        // someone bookmarked this servlet!
         DebugPrint.println(DebugPrint.WARN, "bookmarking is not allowed!");
         return;
      }
      
     // DebugPrint.println(DebugPrint.INFO, "DS: url [" + action + "]");
      DebugPrint.println(DebugPrint.DEBUG, "DTS: Command=" + command); 
      boolean rc = false;
      Properties prop = getRequestParameters(req);
      String thisToken = "-invalid-";
      
      String compname = prop.getProperty("compname");
      
     /* boo 10/05 start */
      String handleSD	= null;
      String base_url = null;
      
      if(compname == null && command.equalsIgnoreCase("LoginSD")) {
         handleSD = prop.getProperty("handleSD");
         base_url = prop.getProperty("base_url");
         System.out.println("boo : compname is null");
         System.out.println("boo : handleSD from URL : " + handleSD);
         System.out.println("boo : base_url from URL :" + base_url);
         if(handleSD != null){
            if(	base_url == null && handleSD.indexOf("?base_url=") != -1){
               base_url = 
                  handleSD.substring(handleSD.indexOf("?base_url=") + 10);
            }
            if(handleSD.indexOf("?base_url=") != -1){
               handleSD = handleSD.substring(0, handleSD.indexOf("?base_url="));
            }
            System.out.println("handleSD is  : " + handleSD + 
                               " & base_url : " + base_url);
         }
         System.out.println("handleSD is  : " + handleSD + 
                            " & base_url : " + base_url);
      }
     /* boo 10/05 end */
     
     // Handle front page login entrypoints
      if (loginpage.handleFrontPage(req, res, command, prop, head)) return;
      
      String feURL       = getDesktopProperty("edodc.feURL",
                                              "https://www-306.ibm.com");
      
     // Tokengen just tries to generate CustConnect token
     // getConnectInfoGeneric tried the NJ path, then this path
      if (command.equalsIgnoreCase("tokengen")) {            
         String op     = req.getParameter("op");
         String scope  = req.getParameter("scope");
         String userid = req.getParameter("userid");
         String inpw   = req.getParameter("password");
         String sftp   = req.getParameter("sftp");
         PrintWriter out = res.getWriter();
         
        // since we pass in res, he does all the associated setting
        //  of response fields
         compname = authenticateAndMakeToken(op, scope, userid, inpw, 
                                             sftp, res);
                                             
         if (compname == null && op.indexOf("@") < 0) {
            String tryIBMEmail = getDesktopProperty("edodc.tryIBMEmail", 
                                                    "false");
                                                    
            if (tryIBMEmail.equalsIgnoreCase("true")) {
            
               userid = userid + "@us.ibm.com";
               compname = authenticateAndMakeToken(op, scope, userid, inpw, 
                                                   sftp, res);
            }
            
         }
            
         res.setHeader("complete", "true");
         if (compname == null) {
            res.setStatus(401);
         }
         out.println((compname == null?"Login Failure":"Login Success"));
         out.close();
         return;
      } else if (command.equalsIgnoreCase("FEForward")) { 
         String link;
         link=getDesktopProperty("edodc.FETopPage",
                                 "/technologyconnect/AmtMenuServlet.wss?linkid=100000");
         String u = feURL + link;
         res.sendRedirect(res.encodeRedirectURL(u));
         return;
         
      } else if (command.equalsIgnoreCase("FEDboxRep")) {
         String link;
         link=getDesktopProperty("edodc.FEDboxReportPage",
                                 "/technologyconnect/EdesignServicesServlet.wss?op=7&sc=webox:op:r");
         String u = feURL + link;
         res.sendRedirect(res.encodeRedirectURL(u));
         return;
      } else if (command.equalsIgnoreCase("FEHosting")) {
         String link;
         link=getDesktopProperty("edodc.FEHosting",
                                 "/technologyconnect/EdesignServicesServlet.wss?op=2");
         String u = feURL + link;
         res.sendRedirect(res.encodeRedirectURL(u));
         return;
      } else if (command.equalsIgnoreCase("FEDbox")) {
         String link;
         link=getDesktopProperty("edodc.FEDboxPage",
                                 "/technologyconnect/EdesignServicesServlet.wss?op=7");
         String u = feURL + link;
         res.sendRedirect(res.encodeRedirectURL(u));
         return;
      } else if (command.equalsIgnoreCase("FEDboxWeb")) {
         String link;
         link=getDesktopProperty("edodc.FEDboxWebPage",
                                 "/technologyconnect/EdesignServicesServlet.wss?op=7&sc=webox:op:i");
         String u = feURL + link;
         res.sendRedirect(res.encodeRedirectURL(u));
         return;
      }
      
      String allowTestEdge = getDesktopProperty("edodc.allowTestEdge");
      if (allowTestEdge != null && allowTestEdge.equalsIgnoreCase("true")) {
         
         String cmd = command.toLowerCase();
         if (cmd.length() > 7) {
            cmd = cmd.substring(0, 7);
         }
                  
         String collabtype = null;
         
         if      (cmd.equals("testdsh"))  collabtype = "DSH";
         else if (cmd.equals("testodc"))  collabtype = "ODC";
         else if (cmd.equals("testndc"))  collabtype = "NEWODC";
         else if (cmd.equals("testftp"))  collabtype = "FTP";
         else if (cmd.equals("testedu"))  collabtype = "EDU";
         else if (cmd.equals("testims"))  collabtype = "IMS" ;
         else if (cmd.equals("testxfr"))  collabtype = "XFR" ;
         else if (cmd.equals("testfdr"))  collabtype = "FDR" ;
         else if (cmd.equals("testdoc"))  collabtype = "DOC" ;
         else if (cmd.equals("testrep"))  collabtype = "REP" ;
         else if (cmd.equals("testdbx"))  collabtype = "DBX" ;
         else if (cmd.equals("testrms"))  collabtype = "STM" ;
         
         if (collabtype != null) {
         
           // Get a session so affinity will work here
            HttpSession session = req.getSession(true);
             
            int testnum = -1;
            try {
               String testnumS = command.substring(7);
               if (testnumS == null || testnumS.length() == 0) testnum = 0;
               else {
                  testnum = Integer.parseInt(testnumS);
               }
            } catch(NumberFormatException nfe) {}
            
            switch(testnum) {
               case 0: {
                  String b = DesktopConstants.StandardHead +
                     "<title>Login Generator</title></head><body bgcolor=\"\" ; marginheight=\"\" ;marginwidth=\"\"><p />\n" +
                     "Enter password to access Login generator:\n<p />\n" +
                     "<form method=\"POST\" " +
                     "action=\"" + 
                     res.encodeURL(head +
                                   "/servlet/oem/edge/ed/odc/desktop/" + 
                                   command + "2") +
                     "\">\n" +
                     "<p /><label id=\"a\"><input id=\"a\" name=\"password\" type=\"password\" maxlength = 25 /></label>\n" +
                     "<p /><input name=\"submit\" type=\"submit\" value=\"Login\" />\n" +
                     "</form></body></html>\n";
                  
                  PrintWriter out = res.getWriter();
                  out.println(b);
                  out.close();
                  return;
               }
               case 2: {
            
                  command = command.substring(0, 7);
            
                  PrintWriter out = res.getWriter();
                  
                  String errstr = null;
                  String inpw  = (String)prop.get("password");
                  String setpw = null;
                  
                  setpw = getDesktopProperty("edodc.TestPassword");
                  
                  
                  if (setpw == null) {
                     errstr = "Login not available!";
                  } else if (inpw == null) {
                     errstr = "No password provided!";
                  } else if (inpw.trim().equals("")) {
                     errstr = "Empty password provided!";
                  } else {
                     try {
                        String inlinepw = 
                           getDesktopProperty("edodc.InlineTestPassword", 
                                              "false");
                        if (inlinepw.equalsIgnoreCase("false")) {
                           setpw = 
                              PasswordUtils.getPassword(setpw+"/._afsde7e");
                        }
                        setpw = " " + setpw + " ";
                        if (setpw.indexOf(" " + inpw + " ") < 0) {
                           errstr = "Invalid password!";
                        }
                     } catch(Exception eee) {
                           errstr = "Login not available - elpwf";
                     }
                  }
                  
                  if (errstr != null) {
                     out.println(DesktopConstants.StandardHead +
                                 "<title>Error</title></head><body bgcolor=\"\" ; marginheight=\"\" ;marginwidth=\"\">" + errstr);
                     out.println("</body></html>\n");
                     out.close();
                     return;      
                  }
                  
                  if (command.equalsIgnoreCase("testrep") ||
                      command.equalsIgnoreCase("testdbx")) {
                      
                     if (command.equalsIgnoreCase("testdbx")) {
                        prop.put("DROPBOX", "1");
                     }
                     
                     UsageReport.handleReport(prop, req, res);
                     
                     return;
                  }
                  
                  String b = DesktopConstants.StandardHead +
                     "<title>Login Generator</title></head><body bgcolor=\"\" ; marginheight=\"\" ;marginwidth=\"\"><p>\n" +
                     "<b>Welcome to the Wild Wacky World of Collaboration</b><p />\n" +
                     "This is a test page which is a standin for Edge Frontend \n" +
                     "functionality. Enter the name of the 'company' and Edge \n" +
                     "user that you are representing in this collaborative \n" +
                     "session below.<p />\n" +
                     "Note: In a production Edge environment this information \n" +
                     "is implied, and will not be collected in this way. The \n" +
                     "goal of this information is multi-fold. For DSH/EDU, it is used \n" +
                     "to direct you to an appropriate machine on the backend. For \n" +
                     "ODC, using the token IBM will ID you as an IBMer, and allow \n" +
                     "modified access rights. The Edge userid specified is used \n" +
                     "to send/receive invitations.<p />" +
                     "<p /><p /><font color=\"red\">Note</font>: If you check the box to the right of the Emailaddr\n" +
                     "and their is a valid address, email will be sent!<p /><p />\n" +
                     "Enter Information for " + collabtype + 
                     " Session: (<font color=\"red\">required fields</font>)\n<p />\n" +
                     "<form method=\"POST\" " +
                     "action=\"" + 
                     res.encodeURL(head +
                                   "/servlet/oem/edge/ed/odc/desktop/" + command + "3") + "\">\n" +
                     "<input type=\"HIDDEN\" name=\"password\" value=\"" + inpw + "\" />\n"+
                     "<table border = 1 rules=\"none\" frame=\"border\">\n" +
                     "<tr>" +
                     "<th align=\"right\"><font color=\"red\">Edge ID</font>:</th><td><label id=\"a\"><input id=\"a\" name=\"Edge Userid\" type=\"text\" maxlength = 40 /></label></td>\n" +
                     (collabtype.equals("EDU") 
                      ? "<th align=\"right\"><font color=\"red\">Classname</font>:</th><td><label id=\"a\"><input id=\"a\" name=\"Classname\" type=\"text\" value=\"Fill in\" maxlength = 40 /></label></td>\n" 
                      : (collabtype.equals("STM")?"<th align=\"right\"><font color=\"red\">STM Class</font>:</th><td><label id=\"a\"><input id=\"a\" name=\"Classname\" type=\"text\" value=\"class1\" maxlength = 40 /></label></td>\n":"<th align=\"right\">Scope:</th><td><label id=\"a\"><input id=\"a\" name=\"Classname\" type=\"text\" maxlength = 120 /></label></td>\n")) +
                     "</tr>" +
                     "<tr>" +
                     "<th align=\"right\">First name:</th><td><label id=\"a\"><input id=\"a\" name=\"FirstName\" type=\"text\" maxlength = 40 /></label></td>\n" +
                     "<th align=\"right\">Last name:</th><td><label id=\"a\"><input id=\"a\" name=\"LastName\" type=\"text\" maxlength = 40 /></label></td>\n" +
                     "</tr>" +
                     "<tr>" +
                     "<th align=\"right\"><font color=\"red\">Company Name</font>:</th><td><label id=\"a\"><input id=\"a\" name=\"Company\" type=\"text\" maxlength = 40 value=\"IBM\" /></label></td>\n" +
                     "<th align=\"right\">EMail address:</th><td><label id=\"a\"><input id=\"a\" name=\"EmailAddr\" type = text maxlength = 80 /></label></td>\n" +
                     "<td><label id=\"a\"><input id=\"a\" name=\"mailto\" type=\"checkbox\" /></label></td>" +
                     "</tr>" +
                     "<tr>" +
                     "<th align=\"right\">State:</th><td><label id=\"a\"><input id=\"a\" name=\"State\" type=\"text\" value=\"New York\" maxlength = 40 /></label></td>\n" +
                     "<th align=\"right\"><font color=\"red\">Country</font>:</th><td><label id=\"a\"><input id=\"a\" name=\"Country\" type=\"text\" value=\"US\" maxlength = 40 /></label></td>\n" +
                     "</tr>" +
                     "<tr>" +
                     "<th align=\"right\">Projects:</th><td><label id=\"a\"><input id=\"a\" name=\"Projects\" type = text maxlength = 80 /></label></td>\n" +
                     "</tr>" +
                     "<tr>" +
                     "<th align=\"right\">Top URL:</th><td colspan = 3 align=\"left\"><label id=\"a\"><input id=\"a\" name=\"hostinfo\" size = 40 type = text value=\"" +  head + "\" maxlength = 80 /></label></th>\n" +
                     "</tr>" +
                     "<tr></tr>" +
                     "<tr>" +
                     "<td colspan = 4 align=\"center\"><input name=\"submit\" type=\"submit\" value=\"Submit\" align=\"center\" /></td>\n" +
                     "</tr>" +
                     "</table>" +
                     "</form>" +
                     "<p /><p /><a href=\"" + 
                     res.encodeURL(head +
                              "/servlet/oem/edge/ed/odc/desktop/getstatus"   + 
                              "?compname=" + generateCipher("getstatus", 
                                                            60*60))+
                     "\">" +
                     "Click here for Tunnel status</a>\n"+
                     "</body></html>\n";
            
                  out.println(b);
                  out.close();
                  return;
               }
               case 3: {
                  PrintWriter out = res.getWriter();
            
                  command = command.substring(0, 7);
                  
                  String errstr = null;
                  String inpw  = (String)prop.get("password");
                  
                 // Check token
                  String setpw = getDesktopProperty("edodc.TestPassword");
                  
                  
                  if (setpw == null) {
                     errstr = "Login not available!";
                  } else if (inpw == null) {
                     errstr = "No password provided!";
                  } else if (inpw.trim().equals("")) {
                     errstr = "Empty password provided!";
                  } else {
                     try {
                        String inlinepw = 
                           getDesktopProperty("edodc.InlineTestPassword", 
                                              "false");
                        if (inlinepw.equalsIgnoreCase("false")) {
                           setpw = 
                              PasswordUtils.getPassword(setpw+"/._afsde7e");
                        }
                     
                        setpw = " " + setpw + " ";
                        if (setpw.indexOf(" " + inpw + " ") < 0) {
                           errstr = "Invalid password!";
                        }
                     } catch(Exception eee) {
                           errstr = "Login not available - elpwf";
                     }
                  }
                  
                  if (errstr != null) {
                     out.println(DesktopConstants.StandardHead +
                                 "<title>Login Generator</title></head><body bgcolor=\"\" ; marginheight=\"\" ;marginwidth=\"\">" + 
                                 errstr);
                     out.println("</body></html>\n");
                     out.close();
                     return;      
                  }
                  
                  if (command.equalsIgnoreCase("testrep") ||
                      command.equalsIgnoreCase("testdbx")) {
                     out.println("testrep/dbx3 is not supported!");
                     out.close();
                     return;
                  }
                  
                  
                  String company   = prop.getProperty("Company");
                  String user      = prop.getProperty("Edge Userid");
                  String firstname = prop.getProperty("FirstName");
                  String lastname  = prop.getProperty("LastName");
                  String emailaddr = prop.getProperty("EmailAddr");
                  String projects  = prop.getProperty("Projects");
                  String country   = prop.getProperty("Country");
                  String urlstart  = prop.getProperty("hostinfo");
                  String state     = prop.getProperty("State");
                  String classname = prop.getProperty("Classname");
                  String mailto    = prop.getProperty("mailto");
                  if (company == null || company.equals("")) company = "IBM";
                  if (user    == null || user.equals(""))    user    = "joeuser";
                  if (country == null || country.equals("")) country = "US";
                  
                  if (urlstart == null) urlstart = head;
                  
                  ConfigObject co = new ConfigObject();
                  co.setProperty("COMMAND", collabtype);
                  co.setProperty("EDGEID",  user);
                  co.setProperty("COMPANY", company);
                  co.setProperty("COUNTRY", country);
                  if (firstname != null) co.setProperty("FIRST",    firstname);
                  if (lastname  != null) co.setProperty("LAST",     lastname);
                  if (projects  != null) {
                     StringTokenizer st = new StringTokenizer(projects, ":", false);
                     int cnt = 0;
                     while(st.hasMoreElements()) {
                        co.setProperty("P" + (++cnt), st.nextToken());
                     }
                     if (cnt > 0) {
                        co.setProperty("PN" , ""+cnt);
                     }
                  }
                  if (emailaddr != null) co.setProperty("EMAIL",    emailaddr);
                  if (state     != null) co.setProperty("STATE",    state);
                  if (classname != null) co.setProperty("SCOPE",    classname);
                  
                  String testminS = getDesktopProperty("tokenlifeForTest", 
                                                       "60");
                  int testmin = 60;
                  DebugPrint.println(DebugPrint.INFO, 
                                     "tokenlifeForTest = " + testminS);
                  try { 
                     testmin = Integer.parseInt(testminS);
                  } catch(Exception ee) {
                     DebugPrint.printlnd(DebugPrint.ERROR, ee);
                  }
                  
                  
                  String lkey      = generateEdgeCipher(co.toString(), 
                                                        testmin*60);
                  String rewraptok = generateCipher(co.toString(), 
                                                    testmin*60); 
                  
                  String urlyuk = urlstart+"/servlet/oem/edge/ed/odc/desktop/Login";
                  String urlplain= urlyuk + "?compname=" + lkey;
                  String urlv = res.encodeURL(urlyuk) + "?compname=" + lkey;
                  
                  
                  String b = DesktopConstants.StandardHead +
                     "<title>Login Generator</title></head><body bgcolor=\"\" ; marginheight=\"\" ;marginwidth=\"\">" +
                     DesktopConstants.javascript_launch +
                     "Prepped to begin session as User [" + user + 
                     "] company [" + company + "] Country[" + country + "] <p />" +
                     "This startup URL will expire in " + testmin + 
                     " minutes or at " +
                     (new Date((60L*testmin*1000) + System.currentTimeMillis())).toString() +
                     "<p /><a href=\"javascript:;\" onclick=\"launchr('"+urlv+"','launchwin1','436','425')\" onkeypress=\"launchr('"+urlv+"','launchwin1','436','425')\">" +
                     "Click here to start " + collabtype + "Session</a>"  +
                     "<p /><br /><br />" + urlv + 
                     "<p /><br /><br />Rewraptoken: " + rewraptok;
                     
                  if (mailto != null && mailto.equalsIgnoreCase("on")) {
                     if (emailaddr != null && !emailaddr.equals("")) {
                        b += "<p />Mailing URL to : [" + emailaddr + "]";
                        
                        String replyAddr = getDesktopProperty("edodc.smtpReplyAddr");
                        
                        String smtpserver = 
                           getDesktopProperty("edodc.smtpRelay");
                        if (smtpserver == null) {
                           smtpserver = "mailrelay.btv.ibm.com";
                        }
                        String body 
                           = "\n\n" + 
                           "This is a generated login URL to participate\n" +
                           "in an Customer Connect based Conference, Hosting, or\n" +
                           "Education session. Simply click on the link\n"  +
                           "below to begin the login process\n\n"           +
                           
                           "Prepped as User [" + user + 
                           "] company [" + company + "] Country[" + country + "]" +
                           "\n\nThis startup URL will expire in " + testmin +
                           " minutes or at " +
                           (new Date((60*testmin*1000) + 
                                     System.currentTimeMillis())).toString() +
                           
                           "\n URL: " + urlplain + 
                           "\n\n    Thankyou.\n";
                        
                        try {
                           oem.edge.ed.sd.ordproc.Mailer.sendMail
                              (smtpserver,
                               replyAddr, 
                               emailaddr, null,
                               null, null,
                               "Conferencing/Hosting/Education Login",
                               body);
                        } catch(Throwable tt) {
                           b += "<p /><font color=\"red\" size=\"+1\">" + 
                              "Asked to MAILTO, but ERROR occured!</font>";
                           DebugPrint.println(DebugPrint.ERROR, tt);
                        }
                     } else {
                        b += "<p /><font color=\"red\" size=\"+1\">" + 
                           "Asked to MAILTO, but no address!</font>";
                     }
                  }
                  
                  b += "</body></html>\n";
                  
                  out.println(b);
                  out.close();
                  return;
               }        
               case 4: {
               }
               default: {
                  PrintWriter out = res.getWriter();
                  out.println("Invalid testnum = " + testnum);
                  out.close();
                  return;
               }
            }
         }
      } // End testedge
      
      if (command.equalsIgnoreCase("refreshlog")) {
         try {Thread.sleep(5000);} catch (Throwable tt) {}
         DebugPrint.refresh();
         PrintWriter out = res.getWriter();
         out.println(DesktopConstants.StandardHead +
                     "<title>refreshlog</title></head><body bgcolor=\"\" ; marginheight=\"\" ;marginwidth=\"\">" +
                     "<p />Logfile Refresh accepted</body></html>");
         out.close();
         return;
      } else if (command.equalsIgnoreCase("ding")) {
         try {Thread.sleep(5000);} catch (Throwable tt) {}
         PrintWriter out = res.getWriter();
         out.println("DONG");
         out.close();
         return;
      } else if (command.equalsIgnoreCase("status") ) {
        // JMC 5/22/01 - The Status commands should have 
        //               but is here for completeness. No requirement for
        //               a specific token is needed here, just that it is
        //               not expired and decodable (already checked above)
        
        // Deliver the Status password prompt
         String b = 
            DesktopConstants.StandardHead +
            "<title>Status Password</title></head><body bgcolor=\"\" ; marginheight=\"\" ;marginwidth=\"\"><p />" +
            "Enter password to access Status information:\n<p />\n" +
            "<form method=\"POST\" " +
            "action=\"" + 
            res.encodeURL(head + "/servlet/oem/edge/ed/odc/desktop/status2") +
            "\">\n" +
            "<p /><label id=\"a\"><input id=\"a\" name=\"password\" type=\"password\" maxlength = 25 /></label>\n" +
            "<p /><p />Select refresh rate<p /><p />" +
            "<label id=\"a\"><input id=\"a\" name=\"refrt\" type=\"radio\" checked value=\"None\" /></label>None\n" +
            "<label id=\"a\"><input id=\"a\" name=\"refrt\" type=\"radio\" value=\"30\" /></label>30 secs\n" +
            "<label id=\"a\"><input id=\"a\" name=\"refrt\" type=\"radio\" value=\"60\" /></label>60 secs\n" +
            "<label id=\"a\"><input id=\"a\" name=\"refrt\" type=\"radio\" value=\"90\" /></label>90 secs\n" +
            "<label id=\"a\"><input id=\"a\" name=\"refrt\" type=\"radio\" value=\"120\" /></label>2 min" +
            "<label id=\"a\"><input id=\"a\" name=\"refrt\" type=\"radio\" value=\"300\" /></label>5 min" +
            "<p /><input name=\"submit\" type=\"submit\" value=\"Get Status\" />\n" +
            "</form></body></html>\n";
         
         PrintWriter out = res.getWriter();
         out.println(b);
         out.close();
         return;
      } else if (command.equalsIgnoreCase("status2") ) {
         PrintWriter out = res.getWriter();
         
         String headValue = "";
         String refrt  = (String)prop.get("refrt");
         
         if (refrt != null     && 
             !refrt.equals("") && 
             !refrt.equals("None")) {
            headValue = 
               "<meta http-equiv=\"refresh\" content=\"" + refrt + "\" />";
         }
         
         out.println(DesktopConstants.StandardHead + headValue +
            "<title>Status Password</title></head><body bgcolor=\"\" ; marginheight=\"\" ;marginwidth=\"\"><p />\n");
         
         String inpw   = (String)prop.get("password");
         String setpw  = null;
         String errstr = null;
         setpw = getDesktopProperty("edodc.StatusPassword");
         
         if (setpw == null) {
            errstr = "Login not available!";
         } else if (inpw == null) {
            errstr = "No password provided!";
         } else if (inpw.trim().equals("")) {
            errstr = "Empty password provided!";
         } else {
            try {
               setpw = PasswordUtils.getPassword(setpw+"/._afsde7e");
               setpw = " " + setpw + " ";
               if (setpw.indexOf(" " + inpw + " ") < 0) {
                  errstr = "Invalid password!";
               }
            } catch(Exception eee) {
               errstr = "Status not available - elpwf";
            }
         }
         
         if (errstr != null) {
            out.println(errstr);
         } else {
            displayStatus(out, prop, req, res);
         }
         out.println("</body></html>\n");
         
         out.close();
         return;
      } else if (command.equalsIgnoreCase("signedappletblurb")) {
         PrintWriter out  = res.getWriter();
         String manualurl = (String)prop.get("manualurl");
         String cmddesc   = (String)prop.get("cmddesc");
         if (cmddesc == null) {
            cmddesc = "Signed applet technology";
         }
         out.println(DesktopConstants.getHeadMatter(cmddesc));
         out.println(DesktopConstants.top_matter);
            
         if (manualurl != null) {
            out.println(DesktopConstants.javascript_launch);
         }
         
         out.print(DesktopConstants.signed_applet_blurb1a);
         out.print(servletcontextpath);
         out.print(DesktopConstants.signed_applet_blurb1b);
         out.print(URLEncoder.encode(cmddesc));
         out.println(DesktopConstants.signed_applet_blurb2);
         
         if (manualurl != null) {
            out.println(DesktopConstants.getManualInstallMatter(manualurl,
                                                                servletcontextpath));
         }
         out.println(DesktopConstants.bot_matter);
         out.close();
         return;
      } else if (command.equalsIgnoreCase("troubleshootinstall")) {
         PrintWriter out  = res.getWriter();
         String cmddesc   = (String)prop.get("cmddesc");
         String title = "Trouble-Shoot Installation";
         if (cmddesc == null) {
            cmddesc = title;
         }
         out.println(DesktopConstants.getHeadMatter(cmddesc));
         out.println(DesktopConstants.top_matter);
         out.print(DesktopConstants.top_matterback1);
         out.print(title);
         out.println(DesktopConstants.top_matterback2);
            
         out.println(DesktopConstants.trouble_shoot_blurb);
         out.println(DesktopConstants.bot_matter);
         
         out.close();
         return;
      } else if (command.equalsIgnoreCase(DesktopCommon.MAKEXCHANNEL_CMD)) {
         
        // JMC 5/24/01 - Whiteboard request cometh in
         
         String owner      = prop.getProperty(DesktopCommon.OWNER);
         String passphrase = prop.getProperty(DesktopCommon.PASSPHRASE);
         String display    = prop.getProperty(DesktopCommon.DISPLAY);
         String cookie     = prop.getProperty(DesktopCommon.COOKIE);
         String host       = null;
         
         
         String error = "NULL owner/display/passphrase";
         DesktopView dt = null;
         
         if (passphrase != null && display != null && owner != null) {
            
            passphrase = makehash(passphrase);
            Vector v = mgr.getDesktopByUser(owner);
            if (v != null) {
               Enumeration enum = v.elements();
               while(enum.hasMoreElements()) {
                  DesktopView ldt = (DesktopView)enum.nextElement();
                  String alias = ldt.getXAlias();
                  if (alias != null && 
                      alias.trim().length() > 0 && alias.equals(passphrase)) {
                     dt = ldt;
                     break;
                  }
               }
            }
            
            error = "No matching owner with matching passphrase: " + owner;
            
            if (dt != null) {
            
               if (!dt.getLocalHost().equals(DomainIPReal)) {
               
                  String phead2 = req.getScheme() + "://" + dt.getLocalHost();
                  String jsessionid = null;
                  
                  if (!doRedir.equalsIgnoreCase("true")) {
                    // If we are NOT doing Redirect, then MUST use affinity
                    // (if in clustered env)
                     jsessionid = dt.getBumpHost();
                     if (jsessionid == null ||
                         jsessionid.trim().length() == 0) {
                        DebugPrint.printlnd(DebugPrint.ERROR,
                                            "MakeXChannel: " + 
                                            "No Redirect && jsessid == NULL!");
                        jsessionid = null;
                     } else {
                        phead2=req.getScheme() + "://" + req.getServerName();
                        jsessionid = JSESSIONID_STR + jsessionid; 
                     }
                  }
               
                  String head2 = phead2 + servletcontextpath;
                  String thisurl = head2 + req.getServletPath();
                  
                  String pinfo = req.getPathInfo();
                  if (pinfo != null) {
                     thisurl += pinfo;
                  }
                  
                  if (jsessionid != null) {
                     thisurl += jsessionid;
                  }
                  
                  String qs = req.getQueryString();
                  if (qs != null && qs.length() > 0) {
                     thisurl += "?" + qs;
                  }
                  
                  DebugPrint.printlnd(DebugPrint.INFO4, 
                                      "Forwarding XCHANNEL request for " + 
                                      dt.toString() + "\n to " + thisurl);
                  res.setHeader("success", "false");
                  
                 // No encodeRedirectURL here case no session cross over
                 // Perhaps fix XChannel code to use affinity as well
                  res.sendRedirect(thisurl);
                  return;
               }
            
               HttpTunnelSession sessionMgr = 
                  ServletSessionManager.getSession(dt.getKey(), false);
                  
               Desktopdata dd = null;
               if (sessionMgr != null) {
                  dd = (Desktopdata)sessionMgr.getUserData();
               }
               
               if (dd != null) {    
                  Educationdata edudata = dd.getLastEDUSession();
                  error = "No EDU Service or Not Enabled for XWindows Channel";
                  if (edudata != null && 
                      mappingfiles.getEducationMachine("AllowXWindows", 
                                                       dt.getEdgeId(), 
                                                       dt.getCompany()) 
                      != null) {
                     
                     error = "Bad display format: " + display;
                     int idx = display.indexOf(":");
                     if (idx >= 0) {
                        host = display.substring(0, idx);
                        if (host == null || host.length() == 0 || 
                            host.equals("unix")) {
                           host = edudata.getHostname();
                        }
                        
                        error = "Hostnames must MATCH: " + host + 
                           " != " + edudata.getHostname();
                        if (host.equals(edudata.getHostname())) {
                           error = null;
                        } else { 
                           try {
                              InetAddress dhostip = InetAddress.getByName(host);
                              InetAddress eduip   = InetAddress.getByName(
                                 edudata.getHostname());
                              if (dhostip.equals(eduip)) {
                                 error = null;
                              }
                           } catch(Throwable tt) {
                           }
                        }
                     }
                  }
               } else {
                  error = "No session or no Desktopdata found for tunnel";
               }
            }
         }
         
         if (error != null) {
           // error
            DebugPrint.printlnd(DebugPrint.INFO4, 
                                "Error handling XCHANNEL request was "
                                + error);
            try {
              // Hackers wait
               Thread.sleep(10000);
            } catch(Throwable tt) {}
            res.setHeader("success", "false");
            PrintWriter out = res.getWriter();
            out.println("XCHANNEL creation failed");
            out.println(error);
            return;
         }
                  
         registerXChannel(dt.getKey(), host, display, cookie, passphrase);
         
         res.setHeader("success", "true");
         PrintWriter out = res.getWriter();
         out.println("XCHANNEL created");
         out.flush();
         out.close();
         return;
      }
      
      
      boolean tunnelStart = false;
      String tunnelcommand = null;
      String commanddesc = serviceNameFromCommand(command);
      if (commanddesc == null) commanddesc = command;
         
     // Check that token is current and decodable
      String err = null;
      
     /* boo 10/05 starts * */
      String cname = compname;
      if(handleSD != null){
         compname = handleSD;
         commanddesc = "Software Delivery";
      }
     /* boo 10/05 ends * */
     
      if (compname == null) {
         err = "Token not supplied";
      } else {
      
        /* JMC 2/8/02 - Have it loop and check both Edge cipher as well
                        as local cipher. This will allow us to restart
                        the client after a short failure. We will re-wrap
                        the login token
        */
         thisToken = null;
         ODCipher lcipher = edgecipher;
         
        //		boo 10/05 starts
         if(handleSD != null){ 
            lcipher = SDcipher ;
         }
        //		boo 10/05 ends
         
         for(int cnt=2; cnt > 0; cnt--) {
            try {
               
               ODCipherData cd = lcipher.decode(compname);
               
               if (!cd.isCurrent()) {
                  err = DesktopConstants.StandardHead +
                     "<title>Failure</title></head><body bgcolor=\"\" ; marginheight=\"\" ;marginwidth=\"\">\n" +
                     "<!--\nERRORMSG=Token expired!\n<p />" +
                     "\nSTATUS=FAILURE\n-->" +
                     "ERROR: Launch of " + commanddesc + " failed<br />" +
                     "CAUSE: Customer Connect launch token expired.<br />" +
                     "ACTION: Click on the " + commanddesc +
                     " link to launch again.</body></html>";
                  res.getOutputStream().write(err.getBytes());
                  return;
               } else {
                  thisToken = cd.getString();
               }
            } catch(DecodeException de) {
               lcipher = cipher;
            }
         }
         if (thisToken == null) {
            err = "Token invalid!";
         }
      }
      
     //	boo 10/05 starts  Handle SD Applet Download
      if(handleSD != null && thisToken != null){
         compname = cname ;// get the compname back from the cname field.
         System.out.println("lets handle the SD token here");
         if (DebugPrint.doDebug()) {
            DebugPrint.printlnd(
               "boo : Contents of Token\n-------------\n"
               + thisToken);
            DebugPrint.printlnd("Sending the above token and base_url via the applet to the client");
         }
         System.out.println("Contents of the Token\n---------\n" + thisToken);
         System.out.println("Sending the above token and base_url via the applet to the client");
         System.out.println("The base url at this stage is : " + base_url);
         prop.put("SD_TOKEN", handleSD);
         prop.put("SD_BASE_URL",base_url);		
         
         handleDownloadApplet(req, res, "SD", "Software Delivery",
                              null, null, prop, null);
         return;
      }
     // boo 10/05 ends
      
      if (err != null) {
         DebugPrint.println(DebugPrint.ERROR, 
                          "DesktopServer URL failed: Error tokenchecking was " 
                           + err + "\n URL = " + action);
         
        // Make the hackers wait
         try {
            Thread.sleep(5000);
         } catch(Throwable t) {}
         
         writeStatus(res, false, err);
         return;
      }
                        
     // JMC 5/22/01 - Tunnel Starts do not have any Preconceived
     //               notion of a valid token, just that it has been
     //               properly encoded and is not expired, so if
     //               tunnelStart is true, we have already passed
     //               all checks to this point.
     
     /* --------------------------- Tunnel Start --------------------------- */
      
     
      if (command.equalsIgnoreCase(DesktopCommon.START_CMD)) {
      
         DesktopView dt = new DesktopView();
         
         String user      = null;
         String company   = null;
         String country   = null;
         String firstname = null;
         String lastname  = null;
         String emailaddr = null;
         String projects  = null;
         Vector projectsv = null;
         String state     = null;
         String classname = null;
         String lcmd = null;
         ConfigObject co = new ConfigObject();
         
         if (thisToken != null) {
            
            try { 
               co.fromString(thisToken);
               
               if (DebugPrint.doDebug()) {
                  DebugPrint.printlnd("Contents of Token\n-------------\n" + 
                                      co.toString());
               }
               
               dt.setEdgeId(user=co.getProperty("EDGEID"));
               dt.setCompany(company=co.getProperty("COMPANY"));
               dt.setCountry(country=co.getProperty("COUNTRY"));
               dt.setFirstName(firstname=co.getProperty("FIRST"));
               dt.setLastName(lastname=co.getProperty("LAST"));
               dt.setEmailAddr(emailaddr=co.getProperty("EMAIL"));
               dt.setState(state=co.getProperty("STATE"));
               dt.setClassroom(classname=co.getProperty("SCOPE"));
               tunnelcommand = co.getProperty("COMMAND");
               
               int cnt = 1;
               String ts;
               while((ts = co.getProperty("P"+(cnt++))) != null) {
                  if (projects != null) {
                     projects += ":";
                  } else {
                     projects = new String();
                     projectsv = new Vector();
                  }
                  projectsv.addElement(ts);
                  projects += ts;
               }
               dt.setProjects(projects);
               
            } catch(Exception e) {
            }
            
           // FE thinks DGP means NEWODC
            if (tunnelcommand.equalsIgnoreCase("DGP")) {
               tunnelcommand = "NEWODC";
            }
            
            
            tunnelcommand = commandFromTunnelCommand(tunnelcommand);
            commanddesc   = serviceNameFromCommand(tunnelcommand);
            
           // command dispatch
            if (user        == null || company       == null || 
                country     == null || tunnelcommand == null ||
                commanddesc == null) {
                
               DebugPrint.printlnd(DebugPrint.ERROR, 
                                  "DS: Bad token format for start" + 
                                  thisToken);
               DebugPrint.println("Token contents:\n" + thisToken);
              // Make the hackers wait
               try {
                  Thread.sleep(10000);
               } catch(Throwable t) {}
               
               writeStatus(res, false, "Bad token format");
               return;
            }
            
           // 'Special processing' (hack) to get WebDropbox download going
           // First pass generate html like the Launcher applet html, but is
           // just informational regarding downloading of file. Has javascript
           // to auto start the download, also includes link to 2nd pass, which
           // is actual file download.
 
            if (tunnelcommand.equals("XFR") && classname != null &&
                (classname.toLowerCase().indexOf("webdropbox:op:i") == 0 ||
                 classname.toLowerCase().indexOf("webox:op:i") == 0)) {
                 
              // If we are using the BROWSING dropbox, forward the req to it
               String webdropboxURI = "/WebDboxLogin.do";
               String urlstart = head;
               String newtoken = rewrapTokenWithTrust(compname); 
               
               String urlyuk  = urlstart+ webdropboxURI;
               urlyuk = fixForRedirect(urlyuk);
               
               String urlv = res.encodeRedirectURL(urlyuk) + 
                             "?address=login&compname=" + newtoken;
                             
              // If a package was specified, supply that
               int idx = classname.toLowerCase().indexOf(":op:i"); 
               if (classname.toLowerCase().indexOf(":p:", idx+5) == idx+5) {
                  int idx2 = classname.indexOf(":", idx+5+3);
                  String adjS = "";
                  if (idx2 > 0) adjS = classname.substring(idx+5+3, idx2);
                  else          adjS = classname.substring(idx+5+3);
                  urlv += "&pkgid=" + URLEncoder.encode(adjS);
               }
               
               res.sendRedirect(urlv);
               return;
               
              /* Was first attempt at new reporting ... moved to struts
            } else if (tunnelcommand.equals("XFR") && classname != null &&
                       (classname.toLowerCase().indexOf("webdropbox:op:r") == 0 ||
                        classname.toLowerCase().indexOf("webox:op:r") == 0)) {
                 
              // If we are using DropboxReports
               String webdropboxURI = "/servlet/oem/edge/ed/odc/DropboxReports";
               String urlstart = head;
               String newtoken = rewrapTokenWithTrust(compname); 
               
               String urlyuk  = urlstart+ webdropboxURI;
               urlyuk = fixForRedirect(urlyuk);
               
               String urlv = res.encodeRedirectURL(urlyuk) + 
                             "?address=login&compname=" + newtoken;
                             
               res.sendRedirect(urlv);
               return;
              */
              
            } else if (tunnelcommand.equals("XFR") && classname != null &&
                       (classname.toLowerCase().indexOf("webdropbox:op:r") == 0 ||
                        classname.toLowerCase().indexOf("webox:op:r") == 0)) {
                 
              // If we are using DropboxReports
               String reportURI = "/dboxrep/mainPage.do";
               String urlstart = head;
               
               String extra="";
               if (classname.toLowerCase().indexOf("box:op:r:q") > 0) {
                  reportURI = "/dboxrep/PackageResetSubmit.do";
                  extra="#headers";
               }
               
               String urlyuk  = urlstart+ reportURI;
               urlyuk = fixForRedirect(urlyuk);
               
              // Get session set up
               HttpSession ses = req.getSession(true);
               
               oem.edge.ed.odc.dsmp.server.UserInfo userinfo = new 
                  oem.edge.ed.odc.dsmp.server.UserInfo(user);
               userinfo.setCompany(company);
               userinfo.setCountry(country);
               if (projectsv != null) userinfo.setProjects(projectsv);
               userinfo.setEmail(emailaddr);
               
              // Set the credentials into the session
               ses.setAttribute("dboxreport_credentials", userinfo);
               
              // Start reporting
               String urlv = res.encodeRedirectURL(urlyuk);
               res.sendRedirect(urlv+extra);
               return;
               
            } else if (tunnelcommand.equals("XFR") && classname != null &&
                       (classname.toLowerCase().indexOf("webdropbox:") == 0 ||
                        classname.toLowerCase().indexOf("webox:") == 0)) {
                
               boolean compactLogin =
                  prop.getProperty(DesktopCommon.COMPACT_LOGIN) != null;
                  
               if (!compactLogin) {
                  PrintWriter out  = res.getWriter();
                  
                  String here = servletcontextpath + 
                     "/servlet/oem/edge/ed/odc/desktop/Login?" + 
                     DesktopCommon.COMPACT_LOGIN + "=true" + "&compname=" + 
                     compname;
                     
                  out.println(DesktopConstants.getHeadMatterXferDownload(
                     "Web download of ICC Dropbox file", here));
                     
                  out.println(DesktopConstants.top_matter);
                  
                  String stuff =
                     "<p align=\"left\"><b><font size=\"+1\">" +
                     "Web-based File Download </font></b><br />\n";
                  
                  stuff += 
                     "<p />Your file download should begin automatically.\n "  +
                     "If the download does not start within 30 seconds, click "+
                     "<a href=" + here + ">here</a>";
               
                  out.println(stuff);
                  out.println(DesktopConstants.bot_matter);
                  out.close();
                  return;
               }
            }                   
         }
         
         boolean mimeLaunch = prop.getProperty("mime-launch") != null;
         String  hostmach   = prop.getProperty("HOSTINGMACHINE");
         String  selproject = prop.getProperty("PROJECT");
         String  protoverS  = prop.getProperty("tunnelprotover");
         int     protover   = 1; 
         
         try {
            protover = Integer.parseInt(protoverS);
         } catch(Throwable nee) {
         }
            
         boolean compactLogin =
            prop.getProperty(DesktopCommon.COMPACT_LOGIN) != null;
            
         DebugPrint.printlnd(DebugPrint.INFO, 
                             "Login Request: " + commanddesc + "[" 
                             + tunnelcommand + "]\n\t\t"
                             + "EdgeID["   + user + "] "
                             + "First["    + firstname + "] "
                             + "Last["     + lastname  + "]\n\t\t"
                             + "Company["  + company   + "] "
                             + "Country["  + country   + "]\n\t\t"
                             + "State["    + state     + "]\n\t\t"
                             + "Classname["+ classname + "]\n\t\t"
                             + "Email["    + emailaddr + "] "
                             + "Projects[" + projects  + "]\n\t\t"
                             + "Hostmach[" + hostmach  + "]\n\t\t"
                             + "Selected project[" + selproject + "]\n\t\t"
                             + "Mimelaunch [" + mimeLaunch + "] "
                             + "compactLogin [" + compactLogin + "]\n");
            
            
        // Handle all startup user interaction
        //
        //   eg. - if multiple projects, have him/her select
        //       - if dsh/classroom startup, check that they are allowed,   
        //       - if dsh/classroom startup, if multiple machines, allow select
        //
         if (!compactLogin && 
             !handleStartupChecks(req, res, dt, tunnelcommand, prop)) {
            return;
         }
            
        // Set additionalParms to go to applet
         Hashtable addParms = null;
         if (!compactLogin) {
            
            addParms = new Hashtable();
            
           // For NEW odc, do the right thing with extra parms. Essentially, 
           //  we are taking scope values and turning them into parms for
           //  MIME or applet launch
            if ((tunnelcommand.equals("NEWODC") && classname != null &&
                 (classname.toLowerCase().indexOf("launch:") == 0))) {
               
              // End it in :
               classname += ":";
               
               String cnl  = classname.toLowerCase();
               
               try {
                  StringTokenizer stok = new StringTokenizer(classname, ":", 
                                                             false);
                  stok.nextToken();  // peel off launch:
                  while(stok.hasMoreTokens()) {
                     String s4 = stok.nextToken().toLowerCase();
                     String val = stok.nextToken();
                     
                     if        (s4.equals("pw")) {
                        addParms.put("CALL_PASSWORD", val);
                     } else if (s4.equals("user")) {
                        addParms.put("CALL_USERID", val);
                     } else if (s4.equals("mid")) {
                        addParms.put("CALL_MEETINGID", val);
                     }
                  }
               } catch(Exception hh) {
                  hh.printStackTrace(System.out);
               }
            }
         }
            
         boolean MSIE = false;
         String userAgent =  req.getHeader("user-agent");
         if (userAgent != null && userAgent.indexOf("MSIE") >= 0) {
            MSIE = true;
         }
         
         
        // Get these again, as handleStartupChecks might add them
         hostmach   = prop.getProperty("HOSTINGMACHINE");
         selproject = prop.getProperty("PROJECT");
         
         
        // If remoteview_admin, redirect here
         if (!compactLogin && 
             tunnelcommand.equals("DSH") && 
             (classname != null &&
              classname.equalsIgnoreCase("remoteviewer_admin")) ||
             (hostmach != null &&
               hostmach.startsWith("remoteviewer_admin@"))) {
            
           // RemoteViewer admin
            String reportURI = "/remoteviewer/RemoteAdministrator.do";
            String urlstart = head;
            
            String urlyuk  = urlstart+ reportURI;
            urlyuk = fixForRedirect(urlyuk);
            
           // Get session set up
            HttpSession ses = req.getSession(true);
            
            oem.edge.ed.odc.dsmp.server.UserInfo userinfo = new 
               oem.edge.ed.odc.dsmp.server.UserInfo(user);
            
            userinfo.setCompany(company);
            userinfo.setCountry(country);
            if (projectsv != null) userinfo.setProjects(projectsv);
            userinfo.setEmail(emailaddr);
            
           // Set the credentials into the session
            ses.setAttribute("rvadmin_credentials", userinfo);
            
           // Start grid action
            String urlv = res.encodeRedirectURL(urlyuk);
            DebugPrint.printlnd(DebugPrint.DEBUG, "Remoteview admin redirect = " + 
                                urlv);
            res.sendRedirect(urlv);
            return;
         }
         
         
        // If this is an SOA Dropbox applet launch send it to jsp
         if (tunnelcommand.equals("XFR") && classname != null &&
             (classname.toLowerCase().indexOf("soa:applet") == 0)) {
             
            String newtoken = rewrapTokenWithTrust(compname); 
            
            req.setAttribute("context", req.getContextPath());
            req.setAttribute("token", newtoken);
            
            RequestDispatcher disp = req.getRequestDispatcher("/jsp/DropboxApplet.jsp");
            disp.forward(req, res);
            return;
         }
               
        /*
        // If this is an SOA Dropbox launch ... prime it
        // For SOA launch, we just want to deliver the applet
         if (tunnelcommand.equals("XFR") && classname != null &&
             (classname.toLowerCase().indexOf("soa:application") == 0)) {
            tunnelcommand = "XFRSVC";
         }
        */
               
        // If Not MS (netscape?), do download application applet
        //  if so setup.
         if (suppZNSApplet && !MSIE && !compactLogin) {
         
            if (mimeLaunch) {
               handleMimeLaunch(req, res, tunnelcommand, hostmach, 
                                selproject, prop, addParms);
            } else {
               handleDownloadApplet(req, res, tunnelcommand, commanddesc, 
                                    hostmach, selproject, prop, addParms);
            }
            return;
         }
         
        // If Not compact login, and only compact allowed, return applic
        //  if so setup.
         if (suppZAnyApplet && !compactLogin) {
         
            if (mimeLaunch) {
               handleMimeLaunch(req, res, tunnelcommand, hostmach, 
                                selproject, prop, addParms);
            } else {
               handleDownloadApplet(req, res, tunnelcommand, commanddesc,
                                    hostmach, selproject, prop, addParms);
            }
            return;
         }
         
         HttpSession ses = req.getSession(true);
         if ( ses.isNew() == false ) {
            ses.invalidate();
            ses = req.getSession(true); // jsdk2.0
         }
         
        // JMC 1/17/00 - Otherside can set COMPACT_LOGIN to show NO applets
        //               required (at least not the first), 2nd is always
        //               optional
         String sid = ses.getId();
         
        // URLRewriting - get fixed up session id so client can gen correctly
         String testsid = res.encodeURL(servletcontextpath + 
                                        "/servlet/oem/edge/ed/odc/desktop");
         int testidx = testsid.indexOf(JSESSIONID_STR);
         String tsid = null;
         if (testidx >= 0) {
            tsid = testsid.substring(testidx+JSESSIONID_STR.length());
            DebugPrint.println(DebugPrint.DEBUG, "SessionId[" + sid + "]" +
                               " rewrite Desktopid[" + tsid + "]");
                               
           // Use the BumpHost field to hold jsessionid for use by XChannel
            dt.setBumpHost(tsid);
                               
         } else {
            tsid = sid;
         }
         
         System.out.println("Generating START desktopid[" + sid + "]");
         
        //
        // JMC 7/2/01 - Have to store something into the session for 
        //              session affinity to work ... didn't work ...
        //
        //              But, this is where the Clone ID version of the 
        //              desktopid is stored.
        //
         ses.setAttribute("did", tsid);
         dt.setKey(sid);
                           
        // If remote-host is set, keep its value, otherwise, take from req
         String rhost = prop.getProperty(DesktopCommon.R_HOST);
         if (rhost != null) {
            dt.setRemoteHost(rhost);
         } else {
           // getRemoteHost is potentially the socks server address
           //  need get this from the tunnel instead!
           //dt.setRemoteHost(req.getRemoteAddr());
            dt.setRemoteHost("localhost");
         } 
         dt.setLocalHost(DomainIPReal);
         dt.setLocalPort(req.getServerPort());
         
         if (mgr.createDesktop(dt)) {
            
            byte earid = 0;
            boolean loadapplet = true;
            String errormsg = null;
            boolean isHosting = false;
            String forceInitPgm = null;
            
            Desktopdata desktopdata = new Desktopdata();
            ses.setAttribute("desktopdata", desktopdata);
            desktopdata.setDesktop(dt);
            
            if (tunnelcommand.equals("NEWODC")) {
               
               SendToClientServlet.addTunnelTimeout(sid);
               earid = registerNewODCTunnel(sid, user, selproject, 
                                            desktopdata, compname);
               DebugPrint.printlnd(DebugPrint.INFO4, 
                                  "DesktopServlet: New ODC session req: " 
                                  + sid);
            } else if (tunnelcommand.equals("FTP")) {
               DebugPrint.printlnd("StartFTP");
               SendToClientServlet.addTunnelTimeout(sid);
               registerFtpTunnel(sid, user, desktopdata);
            } else if (tunnelcommand.equals("XFR")) {
               DebugPrint.printlnd("StartXFR");
               SendToClientServlet.addTunnelTimeout(sid);
               registerXFRTunnel(sid, user, desktopdata, compname);
            } else if (tunnelcommand.equals("FDR")) {
               DebugPrint.printlnd("StartFDR");
               SendToClientServlet.addTunnelTimeout(sid);
               registerFDRTunnel(sid, user, desktopdata, compname);
	    } else if (tunnelcommand.equals("IMS")) {
               DebugPrint.printlnd("StartIM");
               SendToClientServlet.addTunnelTimeout(sid);
               registerIMTunnel(sid, user, desktopdata);
	    } else if (tunnelcommand.equals("CON")) {
               DebugPrint.printlnd("StartCON");
               SendToClientServlet.addTunnelTimeout(sid);
               registerCONTunnel(sid, user, desktopdata);
            } else if (tunnelcommand.equals("DOC")) {
               DebugPrint.printlnd("StartDesktopOnCall");
               SendToClientServlet.addTunnelTimeout(sid);
               registerDesktopOnCallTunnel(sid, user, desktopdata);
            } else if (tunnelcommand.equals("STM")) {
               DebugPrint.printlnd("StartRM");
               SendToClientServlet.addTunnelTimeout(sid);
               loadapplet = registerRMTunnel(sid, classname, user, 
                                             company, desktopdata);
               DebugPrint.println(loadapplet?DebugPrint.INFO4:DebugPrint.ERROR,
                                  "STMClass req [" + classname + "] for [" +
                                  user + "] [" 
                                  + company + "] was " 
                                  + (loadapplet?"Good":"No Good!"));
               if (!loadapplet) {
                  errormsg = "User[" + user + "] Company[" + company 
                             + "] not setup to attend STMClass [" 
                             + classname + "]";
               }
            } else if (tunnelcommand.equals("DSH")) {
               isHosting = true;
               SendToClientServlet.addTunnelTimeout(sid);
               loadapplet = registerHostingTunnel(sid, user, company, 
                                                  hostmach, selproject,
                                                  desktopdata);
               DebugPrint.println(loadapplet?DebugPrint.INFO4:DebugPrint.ERROR, 
                                  "Hosting req for [" + user + "] [" 
                                  + company + "] was " 
                                  + (loadapplet?"Good":"No Good!"));
               if (!loadapplet) {
                  errormsg = "User[" + user + "] Company[" + company 
                             + "] not setup for Hosting";
               } else {
            
                 /*
                 ** 4/10/03 - For JV and his PublishApps hack
                 */
                  int atidx = hostmach.indexOf('@');
                  if (atidx > 0 && publishedAppCipher != null) {
                     String initpgmvalNON = 
                        hostmach.substring(0, atidx+1) + user;
                     
                     String ltok = "Error Creating Token";
                     try {
                        ltok = 
                           publishedAppCipher.encode(PUBLISHED_APPS_CIPHER_TIMEOUT_SECS, 
                                                     initpgmvalNON).getExportString(); 
                     } catch(CipherException ce) {}
                     
                     forceInitPgm = "#\"" + 
                        getDesktopProperty("edodc.PUBAPPSappname", "test") +
                        "\" " + ltok;
                        
                  }
               }
            } else if (tunnelcommand.equals("EDU")) {
               isHosting = true;
               SendToClientServlet.addTunnelTimeout(sid);
               // MPZ Need hostname to hack apart a published app, if present (for connectivity test).
               // MPZ boolean used to be returned as loadapplet.
               String hname = registerEducationTunnel(sid, classname, user, 
                                                    company, desktopdata);
               loadapplet = hname != null;
               DebugPrint.println(loadapplet?DebugPrint.INFO4:DebugPrint.ERROR,
                                  "Class req [" + classname + "] for [" +
                                  user + "] [" 
                                  + company + "] was " 
                                  + (loadapplet?"Good":"No Good!"));
               if (!loadapplet) {
                  errormsg = "User[" + user + "] Company[" + company 
                             + "] not setup to attend Class [" 
                             + classname + "]";
               }
               else {
                  // MPZ Need to hack apart a published app, if present (for connectivity test).
                  int atidx = hname.indexOf('@');
                  if (atidx > 0) {
                     String pubApp = hname.substring(0,atidx);
                     forceInitPgm = "#" + pubApp;
                     DebugPrint.println(DebugPrint.INFO4,
                                  "[" + user + "] [" + company + "] using published app [" + pubApp + "]");
                  }
               }
            }
            
           // return the HTTP tunnel applet to browser
           // pass in the earid in case it's the mutated ICA applet
            if (loadapplet) {
               HttpTunnelSession sessionMgr = (HttpTunnelSession) 
                  ServletSessionManager.getSession(sid, false);
                  
               sessionMgr.setIPAddress(req.getRemoteAddr());

               if (protover > sessionMgr.getProtocolVersion()) {
                  DebugPrint.printlnd("DS: Client proto = " + protover + 
                                      " ServerProto = " + 
                                      sessionMgr.getProtocolVersion());
                  protover = sessionMgr.getProtocolVersion();
               } else {
                  DebugPrint.printlnd("DS: Client proto = " + protover + 
                                      " ServerProto = " + 
                                      sessionMgr.getProtocolVersion());
                  sessionMgr.setProtocolVersion(protover);
               }
               getTunnelApplet(req, res, sid, tsid, earid, compactLogin, 
                               earid != 0, isHosting, company, protover,
                               forceInitPgm, null);
            } else {
               mgr.destroyDesktop(sid, null);
               writeStatus(res, false, errormsg);
            }
         } else {
            DebugPrint.println(DebugPrint.ERROR, "Cannot create desktop???");
            writeStatus(res, false, "Error creating Desktop");
         }
         
         
         
         
         
      } else {
      
      
      
      
      
     /* --------------------------- OTHER things --------------------------- */
      
         DebugPrint.println(DebugPrint.DEBUG, "DS: Processing " + command);
        // Support getstatus as a Token enabled status
        //String id = prop.getProperty(DesktopCommon.DESKTOP_ID);
         HttpSession ses = req.getSession(true);
         String id = null;
         if (ses != null) {
            id = ses.getId();
         }
         
         if (thisToken.equals("getstatus") && 
             command.equalsIgnoreCase("getstatus")) {
            PrintWriter out = res.getWriter();
            out.println(DesktopConstants.StandardHead + 
                        "<title>GetStatus</title></head><body bgcolor=\"\" ; marginheight=\"\" ;marginwidth=\"\"><p />");
            displayStatus(out, prop, req, res);
            out.println("</body></html>\n");
            out.close();
            return;
         }
         
         if (thisToken.equals("report") && 
             command.equalsIgnoreCase("report")) {
            UsageReport.handleMainReport(prop, req, res);
            return;
         }
         
         if (thisToken.indexOf("setlevel") == 0 && 
             command.equalsIgnoreCase("setlevel")) {
            try {
               DebugPrint.setLevel(Integer.parseInt(thisToken.substring(8)));
            } catch(Throwable t) {}
            PrintWriter out = res.getWriter();
            out.println(DesktopConstants.StandardHead +
                        "<title>Set Level</title></head><body bgcolor=\"\" ; marginheight=\"\" ;marginwidth=\"\"><p />");
            out.println("New level set to " + DebugPrint.getLevel());
            out.println("</body></html>\n");
            out.close();
            return;
         }
         
         
         if (thisToken.indexOf("setlog4j") == 0 && 
             command.equalsIgnoreCase("setlog4j")) {
             
           // setlog4j:intlev:name
            try {
               int idx1 = thisToken.indexOf(":");
               int idx2 = thisToken.indexOf(":", idx1+1);
               int lev = Integer.parseInt(thisToken.substring(idx1+1, idx2));
               String name = thisToken.substring(idx2+1);
               LoggerRepository repos = LogManager.getLoggerRepository();
               Logger logger = repos.getLogger(name);
               if (name.equals("root")) {
                  logger = repos.getRootLogger();
               }
               if (logger != null) {
                  Level newlev = Level.toLevel(lev, Level.INFO);
                  logger.setLevel(newlev);
               }
               
            } catch(Throwable t) {}
            
            PrintWriter out = res.getWriter();
            
            out.println(DesktopConstants.StandardHead + 
                        "<title>GetStatus - Modified log4j</title></head><body bgcolor=\"\" ; marginheight=\"\" ;marginwidth=\"\"><p />");
            displayStatus(out, prop, req, res);
            out.println("</body></html>\n");
            out.close();
            
            return;
         }
         
         if (thisToken.indexOf("logging") == 0 && 
             command.equalsIgnoreCase("logging")) {
            String op = thisToken.substring(7, 8);
            String ldid = thisToken.substring(8);
            HttpTunnelSession session = 
               ServletSessionManager.getSession(ldid, false);
               
            PrintWriter out = res.getWriter();
            out.println(DesktopConstants.StandardHead +
                        "<title>Logging</title></head><body bgcolor=\"\" ; marginheight=\"\" ;marginwidth=\"\"><p />");
            if (session != null) {
               if (op.equalsIgnoreCase("T")) {
                  session.setDoLogging(!session.getDoLogging()); 
                  out.println(session.getEdgeId()  + 
                              ": Logging set to "  + 
                              session.getDoLogging());
               } else if (op.equalsIgnoreCase("F")) {
                  out.println(session.getEdgeId()  + 
                              ": Log Contents<p /><pre>"  + 
                              session.logAsString() + "</pre>");
               } else if (op.equalsIgnoreCase("C")) {
                  String msg = "Test Message: " + System.currentTimeMillis();
                  ConfigObject co = new ConfigObject();
                  co.setProperty("COMMAND", "showclient");
                  co.setProperty("MESSAGE", "-------: " + 
                                 (new Date()).toString() +
                                 " :-------\n" + 
                                 "Server side forcing shutdown of tunnel");
                  session.writeControlCommand(co);
                  try {
                     Thread.sleep(5000);
                  } catch(InterruptedException ie) {}
                  session.shutdown();
                  out.println("Shutdown sent to user " + session.getEdgeId());
               }
            } else if (op.equalsIgnoreCase("S")) {
              // WALL message
               String msg = (String)prop.get("WALL");
               msg = "-------: " + (new Date().toString()) + 
                  " :------- \n" + msg;
               ConfigObject co = new ConfigObject();
               co.setProperty("COMMAND", "showclient");
               co.setProperty("MESSAGE", msg); 
               
               out.println("Sending WALL message: <br />"+ msg);
               Enumeration e = ServletSessionManager.getSessions();
               while(e.hasMoreElements()) {
                  String did = (String)e.nextElement();
                  if (prop.get("wallallchk") != null ||
                      prop.get("wallchk_" + did) != null) {
                     DesktopView desktop = mgr.getDesktop(did);
                     String owner = desktop.getEdgeId();
                     HttpTunnelSession lsession = 
                        ServletSessionManager.getSession(desktop.getKey(), 
                                                         false);
                     lsession.writeControlCommand(co);
                     out.println("<br /> ... to : " + owner);
                  }
               }
               
            } else {
               out.println("Logging op " + op + " for unknown ID "  + ldid);
            }
            out.println("</body></html>\n");
            out.close();
            return;
         }
         
         if (thisToken.indexOf("setping") == 0 && 
             command.equalsIgnoreCase("setping")) {
            try {
               pingInterval = (Integer.parseInt(thisToken.substring(7)))*1000;
              // Add timeout for specified delta to do status update
               TimeoutManager tmgr = TimeoutManager.getGlobalManager();
               tmgr.removeTimeout("DtSrvPing");
               tmgr.addTimeout(new DesktopPingTO(DesktopServlet.pingInterval));
            } catch(Throwable t) {}
            PrintWriter out = res.getWriter();
            out.println(DesktopConstants.StandardHead +
                        "<title>Ping Timeout</title></head><body bgcolor=\"\" ; marginheight=\"\" ;marginwidth=\"\"><p />");
            out.println("New Ping Timeout set to " + pingInterval);
            out.println("</body></html>\n");
            out.close();
            return;
         }
         
        // JMC 5/22/01 - All the remaining (non-start) commands MUST have
        //               the DesktopID encoded as thisToken             
         if (id == null || !thisToken.equals(id)) {
           // Make the hackers wait
            try {
               Thread.sleep(5000);
            } catch(Throwable t) {}
            writeStatus(res, false, "Invalid Token!");
            DebugPrint.printlnd("DS: Invalid token specified for cmd=" + command);
            DebugPrint.println("id = " + id + " tokenval = " + thisToken);
            return;
         }
         
         if (command.equalsIgnoreCase(DesktopCommon.GET_VIEWER_CMD) ) {
            
            String earid = prop.getProperty("earid");
            
            DesktopView dv = mgr.getDesktop(id);
            if ( dv != null) {
               getICAApplet(req, res, prop.getProperty("host"), 
                            prop.getProperty("port"), id, 
                            (String)ses.getAttribute("did"),
                            dv.getCompany());
               
            } else {
               
               writeStatus(res, false);
            }
            
         } else if (command.equalsIgnoreCase("speedtest")) {
         
            int bytecnt = -1;
            boolean upload = true;
            String bc = prop.getProperty("UPLOAD");
            String error = null;
            if (bc == null) bc = prop.getProperty("upload");
            
            if (bc == null) {
               bc = prop.getProperty("DOWNLOAD");
               if (bc == null) bc = prop.getProperty("download");
               if (bc != null) {
                  upload = false;
               } else {
                  error = "Need UPLOAD or DOWNLOAD for speedtest";
                  DebugPrint.printlnd(DebugPrint.ERROR, 
                                      "SPEEDTEST sent w/o up/down id=" + id);
               }
            }
            
            if (bc != null) {
               try {
                  bytecnt = Integer.parseInt(bc);
               } catch(Exception ne) {
                  error = "UP/DOWNLOAD value for speedtest must be numeric!";
                  DebugPrint.printlnd(DebugPrint.ERROR, 
                                      "SPEEDTEST sent w/o up/down val=" + bc +
                                      " id=" + id);
                  bc=null;
               }
            }
            
            OutputStream out = null;
            InputStream  in  = null;
            
            if (bytecnt > 0) {
            
               byte buf[] = new byte[16384];
               int iter = 0;
               int tot  = 0;
               
               if (upload) {
                  
                  try {
                     in = req.getInputStream();
                     while(tot < bytecnt) {
                        iter = bytecnt-tot;
                        if (iter > buf.length) iter = buf.length;
                        iter = in.read(buf, 0, iter);
                        if (iter < 0) {
                           break;
                        }
                        tot += iter;
                     }
                  } catch(Exception ee) {
                     ee.printStackTrace(System.out);
                  }
                  
                  if (tot != bytecnt) {
                     DebugPrint.printlnd(DebugPrint.ERROR, 
                                         "SPEEDTEST: UP asked=" + bytecnt + 
                                         " actual=" + tot +
                                         " id=" + id);
                     res.setStatus(403);
                     error="Only uploaded " + tot + " bytes out of " + bc;
                  } else {
                     res.addHeader("bytecount", bc);
                     res.setStatus(200);
                     DebugPrint.printlnd(DebugPrint.INFO4, 
                                         "SPEEDTEST: UP asked=" + bytecnt + 
                                         " actual=" + tot +
                                         " id=" + id);
                  }
                  
                  res.setContentType("application/octet-stream");
                  
                 // Just get this so it will be closed below.
                  out = res.getOutputStream();
                  
               } else {
               
                  res.setContentType("application/octet-stream");
                  res.setContentLength(bytecnt);
                  res.addHeader("bytecount", bc);
                  res.setStatus(200);
                  out = res.getOutputStream();
                  try {
                     while(tot < bytecnt) {
                        iter = bytecnt-tot;
                        if (iter > buf.length) iter = buf.length;
                        out.write(buf, 0, iter);
                        tot += iter;
                     }
                  } catch(Exception ee) {
                  }
                  
                  if (tot != bytecnt) {
                     DebugPrint.printlnd(DebugPrint.ERROR, 
                                         "SPEEDTEST: DOWN asked=" + bytecnt + 
                                         " actual=" + tot +
                                         " id=" + id);
                  } else {
                     DebugPrint.printlnd(DebugPrint.INFO4, 
                                         "SPEEDTEST: DOWN asked=" + bytecnt + 
                                         " actual=" + tot +
                                         " id=" + id);
                  }
               }
            }
            
            if (error != null) {
               res.addHeader("errorstring", error);
               res.setStatus(403);
            }
         
            if (in != null) {
               try {in.close();} catch(Exception ee) {}
            }
            if (out != null) {
               try {
                  out.flush();
                  out.close();
               } catch(Exception ee) {}
            }
         } else {
            writeStatus(res, false);
         }
      }
   }
   

   public static boolean isTokenCurrent(String token, boolean useEdgeCipher) {
      boolean ret = false;
      ODCipherRSA lcipher = null;
      ODCipherData cd = null;
      
      try {
         if (useEdgeCipher) lcipher = edgecipher;
         else               lcipher = SDcipher;
         cd = lcipher.decode(token);
         ret = cd.isCurrent();
      } catch(Exception e) {
         if (useEdgeCipher) {
            try {
               lcipher = cipher;
               cd = lcipher.decode(token);
               ret = cd.isCurrent();
            } catch(Exception ie) {}
         }
      }
      return ret;
   }
   
  // JMC 2/10/02 - Rewrap the provided token, assuming that the
  //               edgeid within matches the edgeid for the provided 'id'
   public static String rewrapToken(String token, String id) {
      try {
         ODCipherData cd = null;
         DebugPrint.println(DebugPrint.DEBUG2, 
                            "Rewrap_Token: id="+id+" token=>");
         DebugPrint.println(DebugPrint.DEBUG2, token); 
         try {
            cd = edgecipher.decode(token); 
         } catch(DecodeException de1) {
            cd = cipher.decode(token); 
         }
         if (cd.isCurrent()) {
            DesktopView dtv = mgr.getDesktop(id);
            String edgeid = dtv.getEdgeId();
            ConfigObject co = new ConfigObject();
            String oldstr = cd.getString();
            co.fromString(oldstr);
            if (co.getProperty("EDGEID").equalsIgnoreCase(edgeid)) {
               co.setProperty("desktopid", id);
               String newstr = co.toString();
               return cipher.encode(REWRAP_LOGIN_TIMEOUT_SECS,
                                    newstr).getExportString();
            } else {
               DebugPrint.println(DebugPrint.ERROR, 
                                  "RewrapToken ... edgeIDs differ!!: " +
                                  co.getProperty("EDGEID") + " " + edgeid);
            }
         } else {
            DebugPrint.println(DebugPrint.WARN, "RewrapToken ... not current");
            DebugPrint.println(DebugPrint.WARN, token);
         }
      } catch(Throwable tt) {
         DebugPrint.println(DebugPrint.WARN, 
                            "Error decoding rewrapToken");
         DebugPrint.println(DebugPrint.WARN, tt);
         DebugPrint.println(DebugPrint.WARN, "token is " + token);
      }
      return null;
   }
   
   public static String rewrapTokenWithTrust(String token) {
      String ret = null;
      try {
         ODCipherRSA cipherToUse = cipher;
         ODCipherData cd = null;
         DebugPrint.println(DebugPrint.DEBUG2, 
                            "Rewrap_Token_withTrust: token=>");
         DebugPrint.println(DebugPrint.DEBUG2, token); 
         try {
            cd = edgecipher.decode(token); 
         } catch(DecodeException de1) {
            try {
               cd = cipher.decode(token); 
            } catch(DecodeException de2) {
               cd = SDcipher.decode(token); 
               cipherToUse = SDcipher;
            }
         }
         ret=cipherToUse.encode(REWRAP_LOGIN_TIMEOUT_SECS, 
                                cd.getString()).getExportString();
      } catch(Throwable tt) {
         DebugPrint.println(DebugPrint.WARN, 
                            "Error decoding rewrapTokenWithTrust");
         DebugPrint.println(DebugPrint.WARN, tt);
         DebugPrint.println(DebugPrint.WARN, "token is " + token);
      }
      return ret;
   }
   
   protected static ODCipherRSA getLocalCipher() { return cipher; }
   
   public static String generateCipher(String id) {
      return generateCipher(id, LOCAL_CIPHER_TIMEOUT_SECS);
   }
   public static String generateCipher(String id, int secs) {
      try {
         return cipher.encode(secs, id).getExportString();
      } catch(CipherException ce) {
         DebugPrint.printlnd(DebugPrint.ERROR, "Error encoding string Local Cipher");
         DebugPrint.printlnd(DebugPrint.ERROR, ce);
      }
      return null;
   }
   public static String generateEdgeCipher(String id) {
      return generateEdgeCipher(id, EDGE_CIPHER_TIMEOUT_SECS);
   }
   public static String generateEdgeCipher(String id, int secs) {
      try {
         return edgecipher.encode(secs, id).getExportString();
      } catch(CipherException ce) {
         DebugPrint.printlnd(DebugPrint.ERROR, "Error encoding string EdgeCipher");
         DebugPrint.printlnd(DebugPrint.ERROR, ce);
      }
      return null;
   }
      
  /* boo 10/1 */
   public static String generateSDCipher(String id){
      return generateSDCipher(id, LOCAL_CIPHER_TIMEOUT_SECS);
   }
  /* boo 10/1 */
   public static String generateSDCipher(String id, int secs) {
      try {
         return SDcipher.encode(secs, id).getExportString();
      } catch(CipherException ce) {
         DebugPrint.printlnd(DebugPrint.ERROR, "Error encoding string SDCipher");
         DebugPrint.printlnd(DebugPrint.ERROR, ce);
      }
      return null;
   } 
   
/**
 * Insert the method's description here.
 * Creation date: (7/30/00 12:55:46 PM)
 * @param req javax.servlet.http.HttpServletRequest
 * @param res javax.servlet.http.HttpServletResponse
 * @exception javax.servlet.ServletException The exception description.
 * @exception java.io.IOException The exception description.
 */
   public void doPost(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse res) throws javax.servlet.ServletException, java.io.IOException {
      doGet(req, res);
   }
/**
 * Insert the method's description here.
 * Creation date: (7/30/00 12:47:29 PM)
 */
   public void finalize() throws Throwable {
      super.finalize();
      mgr = null;
   }
   
   public String generateInitPgmString(String desktopId, String jsessionid, 
                                       HttpServletRequest req, String company) {
                                       
     // URL we send to user is based on if session affinity is enabled
      String phead = req.getScheme() + "://";
      String doRedir = getDesktopProperty("useIPRedirect");
      if (doRedir != null && doRedir.equalsIgnoreCase("true")) {
         phead += DomainIP;
      } else {
         phead += req.getServerName();
      }
      phead += servletcontextpath;
      String head = phead + servletcontextpath;
      String str = 
         jsessionid + 
         "!" + generateCipher(desktopId) + 
         "!" + phead +
         "!" + company;
      return str;
   }
   
   private boolean handleStartupChecks(HttpServletRequest req, 
                                       HttpServletResponse res, 
                                       DesktopView dv, 
                                       String tunnelcommand,
                                       Properties prop) {
      boolean ret  = false;
      String stuff = null;
      
      String turl = req.getRequestURI();
      String qs = req.getQueryString();
      String other = "Other";
      String qchar = "?";
      if (qs != null) {
         turl  = turl + "?" + qs;
         qchar = "&";
      }
      
     // Get projects vector
      Vector projects = new Vector();
      String pstr = dv.getProjects();
      if (pstr != null) {
         StringTokenizer st = new StringTokenizer(pstr, ":", false);
         while(st.hasMoreTokens()) {
            String tok = st.nextToken();
            if (tok.length() > 0) {
               projects.addElement(tok);
            }
         }
      }
      projects.addElement(other);
      
      if        (tunnelcommand.equals("EDU")) {
         String classroom = dv.getClassroom();
         if (classroom == null) {
            
           // Here is the contents of the page
            stuff = 
               "<p align=\"left\"><b><font size=\"+1\">Attempt to attend class failed!</font></b><br />\n" +
               "<p />The class to attend was not specified.\nPlease contact the IBM Customer Connect help desk to report the problem";
            
         } else {
            String mach = mappingfiles.getEducationMachine(dv.getClassroom(), 
                                                           dv.getEdgeId(), 
                                                           dv.getCompany());
            if (mach == null) {
               stuff = 
                  "<p align=\"left\"><b><font size=\"+1\">Attempt to attend class failed!</font></b><br />\n" +
                  "<p />Your ID [" + dv.getEdgeId() + 
                  "] was not enabled for the class [" + dv.getClassroom() + 
                  "].\nPlease verify that you have enrolled for this class in this time slot";
               
            } else {
               ret = true;
            }
         }
      } else if (tunnelcommand.equals("DSH")) {
      
         if (prop.getProperty("HOSTINGMACHINE") != null) return true;
         
         Vector machs = mappingfiles.getHostingMachines(dv.getEdgeId(), 
                                                        dv.getCompany());
         if (machs == null || machs.size() == 0) {
            stuff = 
               "<p align=\"left\"><b><font size=\"+1\">Hosting session startup failed!</font></b><br />\n" +
               "<p />Your ID [" + dv.getEdgeId() + 
               "] is <font color=\"red\">not</font> enabled for hosting.\nIf you believe you are entitled to access the hosting service,\nplease contact the IBM Customer Connect help desk to report the problem";
            
         } else if (machs.size() > 1) {  // || projects.size() > 1) {
               
            if (machs.size() > 1) {
            
               stuff = 
                  "<p align=\"left\"><b><font size=\"+1\">Select a server" +
                  
                 // Disable project selection
                 // (projects.size() > 1?" and project":"") 
                  
                  "\nfor this hosting session</font></b><br />\n";
              // Disable multiple project selection
               if (false && projects.size() > 1) {
                  stuff += "<p />You are entitled to access multiple hosting servers.\nPlease select the appropriate project listed under the desired server to launch your hosting session<p />";
               } else {
                  stuff += "<p />You are entitled to access multiple hosting servers.\nPlease select the desired server to launch your hosting session<p />Servers accessible by " + dv.getEdgeId() + ":<p />";
               }
            } else {
               stuff = 
                  "<p align=\"left\"><b><font size=\"+1\">Select a project\nfor this hosting session</font></b><br />\n" +
                  "<p />You have multiple projects for which you are entitled to access the hosting server '" + (String)machs.elementAt(0) + 
                  "'.\nPlease select the appropriate project listed below to start your hosting session<p />Projects for " + dv.getEdgeId() + " :<p />";
               
            }
            
            if (machs.size() > 1) {
               stuff += "<table border = 0 cellpadding = 10>";
            }
            
            Enumeration e = machs.elements();
            int i = 0;
            
            while(e.hasMoreElements()) {
               String ts = (String)e.nextElement();
               
               String url = turl + qchar + "HOSTINGMACHINE=" + 
                            URLEncoder.encode(ts) + "&PROJECT=";
               
               if (machs.size() > 1) {
                  stuff += "<td align=\"left\">";
                 // Disable project support
                  if (true || projects.size() == 1) {
                     stuff += "<a href=\"" + url + other + "\">" + ts + "</a>";
                  } else {
                     stuff += "<b>" + ts + "</b><blockquote><ul>";
                  }
               }
               
              // Disable project support
               if (false && projects.size() > 1) {
                  for(int j=0; j < projects.size(); j++) {
                     String proj = (String)projects.elementAt(j);
                     stuff += "<li><a href=\"" + url + URLEncoder.encode(proj) + "\">" + proj + "</a></li>\n";
                  }
                  stuff += "</ul></blockquote>";
                  
               }
               
               if (machs.size() > 1) {
                  stuff += "</td>";
                  i++;
                  if (i % 2 == 0 && i > 0) {
                     stuff +="<tr>\n";
                  }
               }
            }
            if (machs.size() > 1) {
               stuff += "</table>";
            }
         } else {
            prop.put("HOSTINGMACHINE", machs.elementAt(0));
            ret = true;
         }
      } else if (tunnelcommand.equals("ODC")) {
         boolean enabled = mappingfiles.enabledForODC(dv.getEdgeId(),
                                                      dv.getCompany());
         if (!enabled) {
            stuff = 
               "<p align=\"left\"><b><font size=\"+1\">Conferencing session startup failed!</font></b><br />\n" +
               "<p />Your ID [" + dv.getEdgeId() + 
               "] is <font color=\"red\">not</font> entitled to access the conferencing service.\nIf you believe this to be in error,\nplease contact the IBM Customer Connect help desk to report the problem";
         } else {
            if (prop.getProperty("PROJECT") != null) return true;
            
           // Disable Project support 
            if (false && projects.size() > 1) {
               stuff = "<p align=\"left\"><b><font size=\"+1\">Select a project for this conferencing session</font></b><br />\n<p />You have multiple projects for which you are entitled to access the conferencing service.\nPlease select the appropriate project listed below to start your conferencing session<p />Projects for " + dv.getEdgeId() + ":<p /><blockquote><ul>";
               for(int i=0; i < projects.size(); i++) {
                  String proj = (String)projects.elementAt(i);
                  String url = turl + qchar + "PROJECT=" + 
                     URLEncoder.encode(proj);
                  stuff += "<li><a href=\"" + url + "\">" + proj + "</a></li>\n";
               }
               stuff += "</ul></blockquote>\n";
               
            } else {
               ret = true;
            }
         }
      } else if (tunnelcommand.equals("XFR")) {
         boolean enabled = mappingfiles.enabledForDropbox(dv.getEdgeId(), 
                                                          dv.getCompany());
         if (!enabled) {
            stuff = 
               "<p align=\"left\"><b><font size=\"+1\">Dropbox session startup failed!</font></b><br />\n" +
               "<p />Your ID [" + dv.getEdgeId() + 
               "] is <font color=\"red\">not</font> entitled to access the Dropbox service.\nIf you believe this to be in error,\nplease contact the IBM Customer Connect help desk to report the problem";
         } else {
            ret = true;
         }
      } else if (tunnelcommand.equals("FDR")) {
         boolean enabled = mappingfiles.enabledForGrid(dv.getEdgeId(), 
                                                       dv.getCompany());
         if (!enabled) {
            stuff = 
               "<p align=\"left\"><b><font size=\"+1\">Grid session startup failed!</font></b><br />\n" +
               "<p />Your ID [" + dv.getEdgeId() + 
               "] is <font color=\"red\">not</font> entitled to access the Grid service.\nIf you believe this to be in error,\nplease contact the IBM Customer Connect help desk to report the problem";
         } else {
            ret = true;
         }
      } else {
         ret = true;
      }
      
      if (stuff != null) {
         try {
            PrintWriter out = res.getWriter();
            out.println(DesktopConstants.getHeadMatter(serviceNameFromCommand(tunnelcommand)));
            out.println(DesktopConstants.top_matter);
            out.println(stuff);
            out.println(DesktopConstants.bot_matter);
            out.close();
           // System.out.println("STUFF = " + stuff);
           // System.out.println("ret = " + ret);
         } catch(Throwable t) {
            DebugPrint.printlnd(DebugPrint.ERROR, 
                                "DS: handleStartupChecks: Have stuff to show user, but got exception!\n" + stuff); 
            DebugPrint.println(DebugPrint.ERROR, t);
         }
      }
      
      return ret;
   }
   
   
/**
 * Return ICA applet to browser
 * Creation date: (08/30/00 16:19:19)
 * Modified by David Y. Yang on his last day (10/06/2000) of employment with IBM
 * @return java.lang.String
 */
   private void getICAApplet(HttpServletRequest req, 
                             HttpServletResponse res, String host, 
                             String port, String desktopId, 
                             String jsessionid, String company){
      StringBuffer buf = new StringBuffer();	
      String ctxJar, ctxCab;

     // Mutated ICA?
      boolean doCombo = false;
      int idxMSIE = req.getHeader("user-agent").indexOf("MSIE");
      String mode = getDesktopProperty("edodc.ctxMode");
      if ( mode != null && (mode.equalsIgnoreCase("combo")          || 
                            (mode.equalsIgnoreCase("combonetscape") && idxMSIE  < 0) ||
                            (mode.equalsIgnoreCase("comboIE")       && idxMSIE >= 0))) {
         doCombo = true;
      }
      
      if ( doCombo ) {
         ctxCab = getDesktopProperty("edodc.ctxComboCab");
         ctxJar = getDesktopProperty("edodc.ctxComboJar");
      } else {
         ctxCab = getDesktopProperty("edodc.ctxCab");
         ctxJar = getDesktopProperty("edodc.ctxJar");
      }
	
      buf.append("<applet ALT=\"none\" CODE=\"");
      buf.append(getDesktopProperty("edodc.ctxApplet")).append("\"");
	
      String codepath = servletcontextpath + 
                        "/servlet/oem/edge/ed/odc/HelperInstall/getFileAnyway";
                        
     // Different tag for jar and cab
      if ( req.getHeader("user-agent").indexOf("MSIE") >= 0 ) {
        // ms internet explorer
         buf.append(" CODEBASE=\"" + codepath + "\"");
      } else {
        // netscape and others
         buf.append(" CODEBASE=\"").append(codepath).append("\" ARCHIVE=\"");
         buf.append(ctxJar).append("\"");
      }
	
      buf.append(" width=\"").append(getDesktopProperty("edodc.ctxWidth"));
      buf.append("\" height=\"").append(getDesktopProperty("edodc.ctxHeight"));
      buf.append("\">");

     // now the parameters
      if ( req.getHeader("user-agent").indexOf("MSIE") >= 0 ) {
         buf.append("<param name=\"cabbase\" value=\"");
         buf.append(ctxCab).append("\">");
      }
	
      buf.append("<param name=\"address\" value=\"").append(host).append("\">");
      buf.append("<param name=\"ICAPortNumber\" value=\"").append(port).append("\">");
      buf.append("<param name=\"Border\" value=\"off\">");
      buf.append("<param name=\"Start\" value=\"auto\">");
      buf.append("<param name=\"End\" value=\"terminate\">");
      buf.append("<param name=\"desiredColor\" value=\"2\">");
      
      HttpTunnelSession session = 
         ServletSessionManager.getSession(desktopId, false);
      
      if (session != null) {
         buf.append("<param name=\"InitialProgram\" value=\"");
        /* ... We use the same initpgm for hosting and ODC
         if (session.getHosting()) {
            buf.append(company);
            } else*/ {
            buf.append(generateInitPgmString(desktopId, jsessionid, 
                                             req, company));
         }
         buf.append("\">");
      }
      buf.append("<param name=\"user.wfclient.keyboardlayout\" VALUE=\"US\">");
      buf.append("<param name=\"EndSessionTimeout\" VALUE=\"0\">");

     // Need streaming option here only if the tunnels are embeded in the ICA applet
      if ( doCombo ) {
         String doStream = getDesktopProperty("edodc.streaming", 
                                              "false");
         String doUploadStream = getDesktopProperty("edodc.uploadstreaming", 
                                                    "true");
      
         buf.append("<param NAME=\"streaming\" VALUE=\"").append(doStream).append("\">");
         buf.append("<param NAME=\"desktopid\" VALUE=\"").append(jsessionid).append("\">");
         buf.append("<param NAME=\"uploadstreaming\" VALUE=\"").append(doUploadStream).append("\">");
         
         String upflushSz = getDesktopProperty("edodc.upflushsize");
         if (upflushSz != null) {
            buf.append("<param NAME=\"upflushsize\" VALUE=\"").append(upflushSz).append("\">");
         }
      }
	
     // close the applet tag
      buf.append("</applet>");
      res.setContentType("text/html");

     // no cache
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      res.setDateHeader("Expires", 0);
	
      try {
         PrintWriter out = res.getWriter();
         out.println(DesktopConstants.StandardHead +
                     DesktopConstants.metapragma +
                     "<title>Load applet</title></head><body bgcolor=\"\" ; marginheight=\"\" ;marginwidth=\"\"><p />");
         out.println(buf.toString());
         out.println("</body></html>");
         out.close();
      } catch (IOException e) {
         DebugPrint.println(DebugPrint.ERROR, "Error writing ICAApplet info");
         DebugPrint.println(DebugPrint.ERROR, e);
         res.setContentLength(0);
      }
   }
/**
 * This method was created by a SmartGuide.
 * @return java.util.Hashtable
 * @param req javax.servlet.http.HttpServletRequest
 */
   protected Properties getRequestParameters(HttpServletRequest req)
      {
            Properties prop = new Properties();
         try { 
            Enumeration fieldNames = req.getParameterNames();
            while (fieldNames.hasMoreElements())
            {
               String paramName = (String) fieldNames.nextElement();
               String paramValue = (String) req.getParameterValues(paramName)[0];
               
              // check for missing parameters (code to be added)
               if (paramValue.length() == 0)
                  DebugPrint.println(DebugPrint.ERROR, 
                                     paramName + " is not specified");
               
               else {
                  if (paramName.equalsIgnoreCase(DesktopCommon.DESKTOP_ID)) {
                     paramValue = req.getSession(false).getId();
                  }
                  prop.put(paramName, paramValue);
               }
            }
         } catch(Exception e) {
            DebugPrint.printlnd("Error in getReqParms: " + e.getMessage());
            DebugPrint.printlnd("    " + e.toString());
         }
         return prop;
      }
      
  // JMC 1/25/01 - to handle download/application applet
   private void handleDownloadApplet(HttpServletRequest req, 
                                     HttpServletResponse res,
                                     String tunnelcommand, 
                                     String desc, 
                                     String hostingmachine,
                                     String selproj,
                                     Properties prop,
                                     Hashtable additionalParms) {
      
      boolean MSIE    = false;
      boolean MSIECAB = false;
      String userAgent =  req.getHeader("user-agent");
      if (userAgent != null && userAgent.indexOf("MSIE") >= 0) {
         MSIE = true;
         
        // If prop is set, we think its JAVA 1.2 or above ... use JAR
         if (prop.getProperty("CALLMEACAB") != null) {
            MSIECAB = true;
         }
      }
      
      StringBuffer buf = new StringBuffer();
      buf.append("<applet alt=\"none\" MAYSCRIPT code=\"");
      buf.append(getDesktopProperty("edodc.downloadApplet")).append("\"");
         
      String codepath = servletcontextpath + 
                        "/servlet/oem/edge/ed/odc/HelperInstall/getFileAnyway";
      if (MSIECAB) {
         buf.append(" codebase=\"").append(codepath).append("\"");
         buf.append(" width=\"400\" height=\"85\">");
         buf.append("<param name=\"CABBASE\"  value=\"");
         buf.append(getDesktopProperty("edodc.downloadCab")).append("\">");
         
      } else {
        // netscape and others
         buf.append(" codebase=\"").append(codepath).append("\" archive=\"");
         buf.append(getDesktopProperty("edodc.downloadJar")).append("\"");
            
         buf.append(" width=\"400\" height=\"85\">");
         if (MSIE) {
            buf.append("<param name=\"ISIE\" value=\"YES\" />");
            String servpath = servletcontextpath + req.getServletPath();
            String pathinfo = req.getPathInfo();
            String qstring  = req.getQueryString();
            if (qstring != null) {
               qstring = "?" + qstring + "&CALLMEACAB=YES";
            } else {
               qstring = "?CALLMEACAB=YES";
            }
            
           /* 
           ** Switch from DomainIP to getServerName. This will allow
           **  NetworkDispatch to continue to multiplex, and prevent
           **  users from seeing the Security warning about Certificate
           **  mismatch.
           */
            String cabURL = 
               req.getScheme() + "://" + req.getServerName() + 
               servpath + ((pathinfo==null)?"":pathinfo) + qstring;
            
            buf.append("<param name=\"TOCAB\" value=\"");
            buf.append(cabURL).append("\" />");
         }
      }
      
     // If injecting parms ... inject away
      if (additionalParms != null) {
         Enumeration enum = additionalParms.keys();
         while(enum.hasMoreElements()) {
            String key = (String)enum.nextElement();
            String val = (String)additionalParms.get(key);
            
            buf.append("<param name=\"-CH_").append(key.toUpperCase());
            buf.append("\" value=\"").append(val).append("\" />");
         }
      }
      
     /* boo : 9/24 10/5 */
      if(tunnelcommand.equalsIgnoreCase("SD")){
         buf.append("<param name=\"COMMAND\" value=\"SD\" />\n");
         buf.append("<param name=\"DEBUG\" value=\"yes\" />\n");
         buf.append("<param name=\"-SD_TOKEN\" value=\"" + prop.getProperty("SD_TOKEN") + "\" />\n");
         buf.append("<param name=\"-URL\" value=\"" + prop.getProperty("SD_BASE_URL") + "\" />\n");
         buf.append("<param name=\"-CH_SERVLETCONTEXT\" value=\"");
         buf.append(req.getContextPath()).append("\" />");
      } else {  // boo 10/05 end


         buf.append("<param name=\"DEBUG\" value=\"yes\" />");
         buf.append("<param name=\"COMMAND\" value=\"HOSTING\" />");
         
         String debugpanel = getDesktopProperty("edodc.debugpanel");
         if (debugpanel != null && debugpanel.equalsIgnoreCase("true")) {
            buf.append("<param name=\"-CH_DEBUG\" value=\"true\" />");
         }
         
         String compname = (String)prop.getProperty("compname");
         if (compname != null) {
            buf.append("<param name=\"-CH_TOKEN\" value=\"").append(compname);
            buf.append("\" />");
         }
         
         buf.append("<param name=\"-CH_SERVLETCONTEXT\" value=\"");
         buf.append(req.getContextPath()).append("\" />");
         
         if (tunnelcommand != null) {
            buf.append("<param name=\"-CH_TUNNELCOMMAND\" value=\"");
            
           // If we are doing a connection test, make it look like a newodc launch
            if (tunnelcommand.equalsIgnoreCase("CON")) tunnelcommand = "NEWODC"; 
            
            buf.append(tunnelcommand).append("\" />");
         }
         
         if (hostingmachine != null) {
            buf.append("<param name=\"-CH_HOSTINGMACHINE\" value=\"");
            buf.append(hostingmachine).append("\" />");
         }
         
         if (selproj != null) {
            buf.append("<param name=\"-CH_PROJECT\" value=\"");
            buf.append(URLEncoder.encode(selproj)).append("\" />");
         }
         
         String doStream = getDesktopProperty("edodc.streaming", 
                                              "false");
         if (doStream.equalsIgnoreCase("false")) {
            buf.append("<param name=\"-CH_NOSTREAM\" value=\"true\" />");
         }         
      } // boo 10/05 insert
      
     // close the applet tag
      buf.append("</applet>");

      res.setContentType("text/html");

     // no cache
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      res.setDateHeader("Expires", 0);
                       
      try {
        // Get appropriate URL info for manual/Mime launch
         String servpath = servletcontextpath + req.getServletPath();
         String pathinfo = req.getPathInfo();
         String qstring  = req.getQueryString();
         if (qstring != null) {
            qstring = "?" + qstring + "&mime-launch=true";
         } else {
            qstring = "?mime-launch=true";
         }
         
        // IE won't work correctly without ending in .dsc ... boohoo
         qstring += "&ietrick=file.dsc";
         String myurl = servpath + ((pathinfo==null)?"":pathinfo) + qstring;
         
         PrintWriter out = res.getWriter();
         out.println(DesktopConstants.getHeadMatter(desc));
         out.println(DesktopConstants.top_matter);
         out.println(DesktopConstants.javascript_launch);
         out.println(DesktopConstants.getAppletMatter("Client Software for Customer Connect"));
         out.println(buf.toString());
         out.println(DesktopConstants.getSignedMatter(myurl, desc, servletcontextpath));
         out.println(DesktopConstants.bot_matter);
         out.close();
      } catch (IOException e) {
         DebugPrint.println(DebugPrint.ERROR, 
                            "Error writing handleDownload Info");
         DebugPrint.println(DebugPrint.ERROR, e);
         res.setContentLength(0);
      }
   }
      
  // JMC 1/25/01 - to handle download/application applet
  // additionalParms are prepended with -CH_ 
   private void handleMimeLaunch(HttpServletRequest req, 
                                 HttpServletResponse res,
                                 String tunnelcommand, 
                                 String hostingmachine,
                                 String selproject,
                                 Properties prop,
                                 Hashtable additionalParms) {
      
      StringBuffer buf = new StringBuffer();
     //  buf.append(tunnelcommand).append("\n");
      buf.append("TUNNEL\n");
      buf.append("-URL ").append(req.getScheme());
      buf.append("://").append(req.getServerName());
      buf.append(servletcontextpath).append("\n");
      
      String compname = (String)prop.getProperty("compname");
      if (compname != null) {
         buf.append("-CH_TOKEN ").append(compname).append("\n");
      }
      
      if (tunnelcommand != null) {
      
        // If we are doing a connection test, make it look like a newodc launch
         if (tunnelcommand.equalsIgnoreCase("CON")) tunnelcommand = "NEWODC"; 
         
         buf.append("-CH_TUNNELCOMMAND ").append(tunnelcommand).append("\n");
      }
      if (hostingmachine != null) {
         buf.append("-CH_HOSTINGMACHINE ").append(hostingmachine).append("\n");
      }
      
      if (selproject != null) {
        // Don't URLEncode this, as the guy on the other end will
        //  thus, it would be done twice
         buf.append("-CH_PROJECT ").append(selproject).append("\n");
      }
      
      String debugpanel = getDesktopProperty("edodc.debugpanel");
      if (debugpanel != null && debugpanel.equalsIgnoreCase("true")) {
         buf.append("-CH_DEBUG\n");
      }
      
     // If injecting parms ... inject away
      if (additionalParms != null) {
         Enumeration enum = additionalParms.keys();
         while(enum.hasMoreElements()) {
            String key = (String)enum.nextElement();
            String val = (String)additionalParms.get(key);
            buf.append("-CH_" + key.toUpperCase() + val + "\n");
         }
      }
      
      String doStream = getDesktopProperty("edodc.streaming", "false");
      if (doStream.equalsIgnoreCase("false")) {
         buf.append("-CH_NOSTREAM\n");
      }
      

     //res.setContentType("application/octet-stream");
     res.setContentType("application/x-ibm-edge-dsc");

     // no cache  ... THIS BROKE IE!!
     //res.setHeader("Pragma", "no-cache");
     //res.setHeader("Cache-Control", "no-cache");
     //res.setDateHeader("Expires", 0);
                       
      try {
         PrintWriter out = res.getWriter();
         out.print(buf.toString());
         out.close();
      } catch (IOException e) {
         DebugPrint.println(DebugPrint.ERROR, 
                            "Error writing handleMimeLaunch Info");
         DebugPrint.println(DebugPrint.ERROR, e);
         res.setContentLength(0);
      }
      
      return;
   }
      
      
   private void send_header_info(PrintWriter out) throws IOException {
      out.println(DesktopConstants.StandardHead + DesktopConstants.metapragma + 
                  "</head><body bgcolor=\"\" ; marginheight=\"\" ;marginwidth=\"\">");
   }
   
/**
 * Return the tunnel applet or the mutated ICA applet to browser
 * Creation date: (08/30/00 16:06:08)
 * Modified by David Y. Yang on his last day (10/06/2000) of employment with IBM
 * @return void
 */
  // additionalParms are sent directly
   private void getTunnelApplet(HttpServletRequest req, 
                                HttpServletResponse res, 
                                String sessionid,
                                String jsessionid,
                                byte earId, 
                                boolean isApplication,
                                boolean icaTunnel, 
                                boolean isHosting, 
                                String company, 
                                int protover,
                                String forceInitPgm,
                                Hashtable additionalParms) {
      
     // If IPRedirection is TRUE, set the redirectHost property to be
     //  this hosts Hostname, or IP, if hostname is unavailable.
      String doRedir = getDesktopProperty("useIPRedirect");
      String redirectHost = null;
      String compname = generateCipher(sessionid);
      if (doRedir != null && doRedir.equalsIgnoreCase("true")) {
         redirectHost = DomainIP;
      }
         
      String doStream = getDesktopProperty("edodc.streaming", 
                                           "false");
      String doUploadStream = getDesktopProperty("edodc.uploadstreaming", 
                                                 "true");
      String upflushSz = getDesktopProperty("edodc.upflushsize");
      
     // Special case, if application, just return success with appropriate info
      if (isApplication) {
         res.setContentType("text/html");
         res.setHeader("Pragma", "no-cache");
         res.setHeader("Cache-Control", "no-cache");
         res.setDateHeader("Expires", 0);
         res.setHeader("earId", "" + earId);
         res.setHeader("desktopid", jsessionid);
         res.setHeader("compname", compname);
         res.setHeader("tunnelprotover", ""+protover);
         res.setHeader("uploadstreaming", doUploadStream);
         if (upflushSz != null) {
            res.setHeader("upflushsize", upflushSz);
         }
         res.setHeader("streaming", doStream);
         
        // Don't send initial pgm if hosting. 
        // Breaks disconnect/reconnect with FR1
        
         if (forceInitPgm != null) {
            res.setHeader("InitialProgram", forceInitPgm);
         } else if (!isHosting) {
            res.setHeader("InitialProgram", 
                          generateInitPgmString(sessionid, jsessionid,
                                                req, company));
         }
      
         if (redirectHost != null) {
            res.setHeader("redirectHost", redirectHost);
         }
         
         try {
            PrintWriter out = res.getWriter();
            send_header_info(out);
            out.println("Application startup OK");
            out.println("</html></body>");
            out.close();
         } catch (IOException e) {
            DebugPrint.println(DebugPrint.ERROR, 
                               "Error writing getTunnelAppletInfo1");
            DebugPrint.println(DebugPrint.ERROR, e);
         }
         return;
      } 
      
      boolean MSIE = false;
      String userAgent =  req.getHeader("user-agent");
      if (userAgent != null && userAgent.indexOf("MSIE") >= 0) {
         MSIE = true;
      }
      
     // Return the mutated ICA applet if we are told so
     // This is OLD now ... needs updating TODOTODOTODO
      String mode = getDesktopProperty("edodc.ctxMode");
      if ( mode != null && icaTunnel &&
           (mode.equalsIgnoreCase("combo")          || 
            (mode.equalsIgnoreCase("combonetscape") && !MSIE) ||
            (mode.equalsIgnoreCase("comboIE")       && MSIE))) {
      
         getICAApplet(req, res, "TUNNELCOMBODIRECT", 
                      Byte.toString(earId), sessionid, 
                      jsessionid, company);
         return;
      }
	
      StringBuffer buf = new StringBuffer();
      buf.append("<applet ALT=\"none\" CODE=\"");
      buf.append(getDesktopProperty("edodc.tunnelApplet")).append("\"");
         
      String codepath = servletcontextpath + 
                        "/servlet/oem/edge/ed/odc/HelperInstall/getFileAnyway";
      if (MSIE) {
         buf.append(" CODEBASE=\"").append(codepath).append("\"");
      } else {
        // netscape and others
         buf.append(" CODEBASE=\"").append(codepath).append("\" ARCHIVE=\"");
         buf.append(getDesktopProperty("edodc.tunnelJar")).append("\"");
      }
      buf.append(">");

     // now the parameters
      if (MSIE) {
         buf.append("<param NAME=\"cabbase\" VALUE=\"");
         buf.append(getDesktopProperty("edodc.tunnelCab")).append("\">");
      }
      
      buf.append("<param NAME=\"desktopid\" VALUE=\"").append(
         jsessionid).append("\">");
         
      buf.append("<param NAME=\"eartype\" VALUE=\"ica\">");
      
     // If injecting parms ... inject away
      if (additionalParms != null) {
         Enumeration enum = additionalParms.keys();
         while(enum.hasMoreElements()) {
            String key = (String)enum.nextElement();
            String val = (String)additionalParms.get(key);
            buf.append("<param NAME=\"").append(key);
            buf.append("\" VALUE=\"").append(val).append("\">");
         }
      }
      
      if (redirectHost != null) {
         buf.append("<param NAME=\"redirectHost\" VALUE=\"" +redirectHost+ "\">");
      }
      
      String debugpanel = getDesktopProperty("edodc.debugpanel");
      if (debugpanel != null && debugpanel.equalsIgnoreCase("true")) {
         buf.append("<param NAME=\"debugpanel\" VALUE=\"true\">");
      }

      buf.append("<param NAME=\"streaming\" VALUE=\"").append(doStream).append("\">");
      buf.append("<param NAME=\"compname\" VALUE=\"").append(compname).append("\">");      
      buf.append("<param NAME=\"uploadstreaming\" VALUE=\"").append(doUploadStream).append("\">");
      if (upflushSz != null) {
         buf.append("<param NAME=\"upflushsize\" VALUE=\"").append(upflushSz).append("\">");
      }
      
      String requestURL = req.getRequestURI();
      String pathinfo = req.getPathInfo();
      String servletPath = null;
      if (pathinfo != null) {
         servletPath = requestURL.substring(0, requestURL.lastIndexOf(pathinfo));
      } else {
         servletPath = requestURL;
      }
      buf.append("<param NAME=\"viewerpath\" VALUE=\"").append(servletPath).append("/").append(DesktopCommon.GET_VIEWER_CMD).append("\">");
	
     // close the applet tag
      buf.append("</applet>");

      res.setContentType("text/html");
      res.setHeader("Pragma", "no-cache");
      res.setHeader("Cache-Control", "no-cache");
      res.setDateHeader("Expires", 0);
	
      try {
         PrintWriter out = res.getWriter();
         send_header_info(out);
         out.println(buf.toString());
         out.println("</html></body>");
         out.close();
      } catch (IOException e) {
         DebugPrint.println(DebugPrint.ERROR, 
                            "Error writing getTunnelAppletInfo2");
         DebugPrint.println(DebugPrint.ERROR, e);
         res.setContentLength(0);
      }
   }
   
   static public ODCipher copyCipher() {
      return cipher;
   }
   
/**
 * Insert the method's description here.
 * Creation date: (7/30/00 12:43:46 PM)
 * @param param javax.servlet.ServletConfig
 */
   public void init(javax.servlet.ServletConfig config) 
                                                throws ServletException {
      super.init(config);
      
     // Get BuildInfo details
      try {
         PropertyResourceBundle prop = (PropertyResourceBundle)
            PropertyResourceBundle.getBundle("BuildInfo");
            
         buildInfo = prop.getString("build.timestamp");
         buildInfo = "<br/>Built: " + buildInfo + "<br/>";
      } catch ( Exception e ) {
         DebugPrint.println(DebugPrint.WARN, 
                            "ODC: Error loading BuildInfo build.timestamp property");
         DebugPrint.println(DebugPrint.WARN, e);
      }
      
      
     // Get ODC properties
      try {
         deskprops = new ReloadingProperty();
         try {
            PropertyResourceBundle AppProp = (PropertyResourceBundle)
               PropertyResourceBundle.getBundle(odcResName);
            
            ConfigObject cfgobj = new ConfigObject();
            Enumeration keys = AppProp.getKeys();
            while(keys.hasMoreElements()) {
               String key = (String)keys.nextElement();
               cfgobj.setProperty(key, AppProp.getString(key));
            }
            deskprops.bulkLoad(cfgobj);
         } catch ( Exception e ) {
            DebugPrint.println(DebugPrint.ERROR, 
                               "ODC: ODC Property file '" + odcResName +
                               " not found");
         }
         
      } catch(Exception ioe) {
         DebugPrint.printlnd(DebugPrint.ERROR, 
                             "Error getting DesktopServlet property file");
         DebugPrint.printlnd(DebugPrint.ERROR, ioe);
      }
      
      servingDirectories = config.getInitParameter("edodc.servingDirectories");
      if (servingDirectories == null) {
         servingDirectories = getDesktopProperty("edodc.servingDirectories");
      }
      
      deskprops.setServingDirectories(servingDirectories);
      
      FE.setProperties(deskprops);
      
      loginpage = new DesktopLogin(deskprops);
      
      lastInterval = (new Date()).getTime();
      
     // If we have AFS/GSA authentication needs, get that thing running
      String wasauthcell  = getDesktopProperty("edodc.wasProcessAuth_Cell");
      String wasauthpwdir = getDesktopProperty("edodc.wasProcessAuth_PWDir");
      String wasauthTO    = getDesktopProperty("edodc.wasProcessAuth_Timeout");
      String klogpath     = getDesktopProperty("edodc.klogPath");
      String gsapath      = getDesktopProperty("edodc.gsaloginPath");
      
      DebugPrint.printlnd(DebugPrint.WARN,
                          "Checking for AppServer based File System Authentication");
      if (wasauthcell != null && wasauthcell.length() > 0) {
         if (wasauthpwdir != null && wasauthpwdir.length() > 0) {
            File pwdir = new File(wasauthpwdir);
            if (!pwdir.exists() || !pwdir.isDirectory() || !pwdir.canRead()) {
               DebugPrint.println(DebugPrint.WARN,
                                  "wasProcessAuth: Error during setup for cell[" + 
                                  wasauthcell + "]. PWDir does not exist/dir/readable [" + 
                                  wasauthpwdir + "]");
            } else {
            
               FSAuthentication auth = new FSAuthentication(wasauthcell, wasauthpwdir);
               
               if (klogpath != null && klogpath.length() > 0) auth.setKlog(klogpath);
               if (gsapath  != null &&  gsapath.length() > 0) auth.setGSA(gsapath);
            
               auth.setLoud(true);
            
              // Set timeout time if valid number and > 1 minute
               try {
                  long wasinterval = Long.parseLong(wasauthTO) * 60 * 1000;
                  if (wasinterval > 60000) {
                     auth.setTimeoutMS(wasinterval);
                     DebugPrint.printlnd(DebugPrint.INFO, 
                                         "FSAuthenticator timeout set to " + 
                                         (wasinterval/60000) + " minutes");
                  }
               } catch(Throwable t) {
               }
               
              // Finally, authenticate, and schedule it to retry. If there is an error,
              //  it will be verbose (generate alert), and will retry 1/10 of specified
              //  interval
               auth.schedule(!auth.reauthenticate());
               
               DebugPrint.printlnd(DebugPrint.WARN,
                                   "AppServer based File System Authentication enabled: "
                                   + wasauthcell);
            }
            
         } else {
            DebugPrint.printlnd(DebugPrint.WARN,
                                "wasProcessAuth_Cell was set, but PWDir was NOT!");
         }
      } else {
         DebugPrint.printlnd(DebugPrint.WARN,
                             "No File System Authentication as no CELL provided");
      }
      
      
     // get initParameters
      usrPath = config.getInitParameter("edodc.dbUserPath");
      pwdPath = config.getInitParameter("edodc.dbPwdPath");
      
     // Setup all out mapping files. 
      mappingfiles = new DBMappingFiles(deskprops);
      
     // Override mapping with any specified directly as config parm
      String mapparm;
      mapparm = "eddsh.hmappingFile";
      mappingfiles.overrideMapping(mapparm, config.getInitParameter(mapparm));
      mapparm = "ededu.emappingFile";
      mappingfiles.overrideMapping(mapparm, config.getInitParameter(mapparm));
      mapparm = "edodc.omappingFile";
      mappingfiles.overrideMapping(mapparm, config.getInitParameter(mapparm));
      mapparm = "edodc.dmappingFile";
      mappingfiles.overrideMapping(mapparm, config.getInitParameter(mapparm));
      mapparm = "edodc.pmappingFile";
      mappingfiles.overrideMapping(mapparm, config.getInitParameter(mapparm));
      mapparm = "edodc.imappingFile";
      mappingfiles.overrideMapping(mapparm, config.getInitParameter(mapparm));
      mapparm = "edodc.gmappingFile";
      mappingfiles.overrideMapping(mapparm, config.getInitParameter(mapparm));
      
      String cipherKey = config.getInitParameter("edodc.cipherFile");
      if (cipherKey == null) {
         cipherKey = getDesktopProperty("edodc.ODCcipherFile");
      }
      
      ODCipherRSAFactory fac = ODCipherRSAFactory.newFactoryInstance();
      try {
         edgecipher = fac.newInstance(DesktopServlet.findFileWhereEver(cipherKey)); 
      } catch(Throwable t) {
         DebugPrint.println(DebugPrint.ERROR, 
                            "Error loading CipherFile! [" + cipherKey + "]");
         try {
            edgecipher = fac.newInstance(1024);
         } catch(Exception e) {}
      }
      
      cipherKey = config.getInitParameter("edodc.ODClocalCipherFile");
      if (cipherKey == null) {
         cipherKey = getDesktopProperty("edodc.ODClocalCipherFile");
      }
      
      try {
         cipher = fac.newInstance(DesktopServlet.findFileWhereEver(cipherKey)); 
      } catch(Throwable t) {
         DebugPrint.println(DebugPrint.ERROR, 
                            "Error loading Local ODC CipherFile! [" + 
                            cipherKey + "]");
         try {
            cipher = fac.newInstance(1024);
         } catch(Exception e) {}
      }
      
       
      cipherKey = config.getInitParameter("edodc.SDcipherFile");
      if (cipherKey == null) {
         cipherKey = getDesktopProperty("edodc.SDcipherFile");
      }
      
      try {
         SDcipher = fac.newInstance(DesktopServlet.findFileWhereEver(cipherKey)); 
      } catch(Throwable t) {
         DebugPrint.println(DebugPrint.ERROR, 
                            "Error loading SD CipherFile! [" + 
                            cipherKey + "]");
         try {
            SDcipher = fac.newInstance(1024);
         } catch(Exception e) {}
      }
      
      cipherKey = config.getInitParameter("edodc.PUBAPPScipherFile");
      if (cipherKey == null) {
         cipherKey = getDesktopProperty("edodc.PUBAPPScipherFile");
      }
      
      try {
         publishedAppCipher = new ODCipherRSASimple(DesktopServlet.findFileWhereEver(cipherKey)); 
      } catch(Throwable t) {
         DebugPrint.println(DebugPrint.ERROR, 
                            "Error loading Published Apps CipherFile! [" + 
                            cipherKey + "]");
      }
      
      
      servingDirectories = config.getInitParameter("edodc.servingDirectories");
      if (servingDirectories == null) {
         servingDirectories = getDesktopProperty("edodc.servingDirectories");
      }
      
     // Get domain
      try {
      
         DomainIP = getDesktopProperty("edodc.DomainIP");
         
        // JMC 10/22/04 - ALWAYS get this, and save as DomainIPReal
         String ip = java.net.InetAddress.getLocalHost().toString();
         StringTokenizer parser = new StringTokenizer(ip, "/");
        // DebugPrint.println(DebugPrint.INFO, 
        //                    "host name: " + parser.nextToken());
         DomainIPReal = parser.nextToken();
         
        // If we don't have a specific domainip from prop file, use REAL
         if (DomainIP == null) {
            DomainIP = DomainIPReal;
         }
         DebugPrint.println(DebugPrint.INFO, 
                            "DesktopServlet: Init: host ip: " + DomainIP);
         DebugPrint.println(DebugPrint.INFO, 
                            "DesktopServlet: Init: host ip Real: "+DomainIPReal);
                            
      } catch ( Exception e ) {
         DebugPrint.println(DebugPrint.ERROR, 
                            "Error getting/parsing IP address");
         DebugPrint.println(DebugPrint.ERROR, e);
      }

            
     // Couple more props
      String s = getDesktopProperty("edodc.supportDownloadApplet");
      if (s != null && s.equalsIgnoreCase("true")) {
         suppZApplet = true;
      }
      
      s = getDesktopProperty("edodc.supportAutoNSDownloadApplet");
      if (s != null && s.equalsIgnoreCase("true")) {
         suppZNSApplet = true;
      }
      
      s = getDesktopProperty("edodc.supportAutoAnyDownloadApplet");
      if (s != null && s.equalsIgnoreCase("true")) {
         suppZAnyApplet = true;
      }
      
      try {
         int lev = Integer.parseInt(getDesktopProperty("edodc.debuglevel"));
         DebugPrint.setLevel(lev);
      } catch(Throwable t) {
         DebugPrint.setLevel(DebugPrint.INFO);
      }
      
      try {
         String tstatusInterval = getDesktopProperty("edodc.StatusInterval");
         statusInterval = Long.parseLong(tstatusInterval) * 1000;
      } catch(Throwable t) {
         statusInterval = 5*60*1000;
      }
      
      try {
         String tpingInterval = getDesktopProperty("edodc.PingInterval");
         pingInterval = Long.parseLong(tpingInterval) * 1000;
      } catch(Throwable t) {
         pingInterval = 1*60*1000;
      }
      
     // Add timeout for specified delta to do status update
      TimeoutManager tmgr = TimeoutManager.getGlobalManager();
      tmgr.addTimeout(new DesktopTO(statusInterval));
      tmgr.addTimeout(new DesktopPingTO(pingInterval));
     
     // This guy will get the AMT and EDODC datasources set up
      oem.edge.ed.odc.dropbox.server.DropboxAccessSrv.getSingleton();
      
      String dboxrep = getDesktopProperty("edodc.dropboxReportFile");
      if (dboxrep != null) {
         try {
            ReportGenerator.setForServlet(
               DesktopServlet.findFileWhereEver(dboxrep));
         } catch(Throwable tt) {
            DebugPrint.printlnd(DebugPrint.WARN, 
                                "Error finding dropboxReportFile: " + dboxrep);
            
            DebugPrint.println(DebugPrint.WARN, tt);
         }
      } else {
         DebugPrint.printlnd(DebugPrint.WARN, 
                             "No edodc.dropboxReportFile in desktop propfile");
      }
        
     // Get desktop manager and initialize it with the properties
      mgr = DesktopManager.getDesktopManager();
      mgr.init(deskprops, DomainIPReal);
   }
   
   static public void tunnelPing(SessionManager smIn, ConfigObject cf) {
      
      HttpTunnelSession sm = (HttpTunnelSession) smIn;
     // Hooked in here to hack the Connectivity test timeout
      try {
         Desktopdata dd = (Desktopdata)sm.getUserData();
         GenericService gendata = dd.getLastGenericSession();
         if (gendata.getServiceType().equalsIgnoreCase("CON")) {
            DebugPrint.printlnd("Connectivity test concluded for user " + 
                                sm.getEdgeId());
            sm.shutdown();
            return;
         } 
      } catch(Throwable tt) {}
      
     /*
       DebugPrint.println(DebugPrint.INFO2, "TP : " + mil);
     */
      try {
         long mil           = System.currentTimeMillis();
         int  id         = cf.getIntProperty("PINGID", 0);
         long sendmil    = cf.getLongProperty("SENDTIME", 0);
         long sendtotin  = cf.getLongProperty("TOTIN", 0);
         long sendtotout = cf.getLongProperty("TOTOUT", 0);
         long timediff   = (mil-sendmil);
         long totinDiff  = sm.getTotIn()  - sendtotin;
         long totoutDiff = sm.getTotOut() - sendtotout;
         String edgeid   = ((HttpTunnelSession)sm).getEdgeId();
         sm.addPingInfo(id, timediff, totinDiff, totoutDiff);
         DebugPrint.printlnd(DebugPrint.INFO, 
                             "TunnelPing: Edgeid[" + edgeid     +
                             "] dtid[" + sm.desktopID()         + 
                             "] Pingid["     + id               + 
                             "] time(ms) ["  + timediff   + "]" +
                             "] totin ["     + totinDiff  + "]" +
                             "] totout ["    + totoutDiff + "]" +
                             "] total  ["    + (totoutDiff+totinDiff) + "]");
      } catch(Throwable t) {
         DebugPrint.printlnd(DebugPrint.ERROR, 
                             "Error handling tunnelPing for " + 
                             sm.desktopID());
         DebugPrint.println(DebugPrint.ERROR, t);
      }
   }
   
   static public void sendTunnelPing(SessionManager sm, int pid) {
     /*
       DebugPrint.println(DebugPrint.INFO2, "STP : " +
                          System.currentTimeMillis());
     */
      try {
         ConfigObject cf = new ConfigObject();
         cf.setProperty    ("COMMAND",  "serverping");
         cf.setIntProperty ("PINGID",   pid);
         cf.setLongProperty("TOTIN",    sm.getTotIn());
         cf.setLongProperty("TOTOUT",   sm.getTotOut());
         cf.setLongProperty("SENDTIME", System.currentTimeMillis());
         sm.writeControlCommand(cf);
      } catch(Throwable t) {
         DebugPrint.printlnd(DebugPrint.ERROR, 
                             "Error Sending tunnelPing for " + 
                             sm.desktopID());
         DebugPrint.println(DebugPrint.ERROR, t);
      }
   }
   
   
/**
 * Insert the method's description here.
 * Creation date: (9/15/00 2:42:41 PM)
 * @param req javax.servlet.http.HttpServletRequest
 * @param remoteHosts java.util.Vector
 */
   private void notify(HttpServletRequest req, Vector remoteHosts) {

      StringBuffer buf;
      String scheme = req.getScheme();
      String host = req.getServerName();
      int port = req.getServerPort();

      String requestURL = req.getRequestURI();
      String pathinfo = req.getPathInfo();
      String servletPath = requestURL;
      if ( pathinfo != null ) {
         servletPath = requestURL.substring(0, requestURL.lastIndexOf(pathinfo));
      }

     // Bug in SecureGlue within Websphere ... doesn't seem to setup https
     //   as a valid proto on 3.0x and doesn't return on 2.03!!. So, use the
     //   URLConnection2 approach, and set URL using http as the proto, so 
     //   we don't get unknown protocol, and use the connect method specifying
     //   the actual scheme
     
      String myURL = scheme + "://" + host;
      if ( port != 80 ) {
         myURL += ":" + port;
      }
      DebugPrint.println(DebugPrint.INFO4, 
                         "Notification source = " + myURL + servletPath );

      synchronized ( remoteHosts ) {
	
         for ( int index=0; index<remoteHosts.size(); index++ ) {

           // construct the destination url
            String rhost = (String) remoteHosts.elementAt(index);
            buf = new StringBuffer("http");
            buf.append("://").append(rhost);
            if ( port != 80 ) {
               buf.append(":").append(port);
            }
            buf.append(servletPath).append("/").append(DesktopCommon.NOTIFY_CMD);
			
            DebugPrint.println(DebugPrint.DEBUG, "This is NEW code!");
            DebugPrint.println(DebugPrint.DEBUG, 
                               "Notification target = " + buf.toString());

            try {
            
               DebugPrint.println(DebugPrint.DEBUG, 
                                  "About to create Notify URL " + 
                                  buf.toString());
                                  
               URL url = new URL(buf.toString());
               
               DebugPrint.println(DebugPrint.DEBUG, 
                                  "About to Connect to other in Notify");
               
               URLConnection2 conn = new URLConnection2(url, false);
               conn.setProtocol(scheme);
               conn.setKeepAlive(false);
              
              //    URLConnection conn = url.openConnection();
              //DebugPrint.println("Just after to Connect to other in Notify");
               
               InputStream in = conn.getInputStream();
               DebugPrint.println(DebugPrint.DEBUG, "Got inputstream");
               ConfigFile ini = new ConfigFile(in);
               DebugPrint.println(DebugPrint.DEBUG, "read ini file");
               String status = ini.getProperty(DesktopCommon.STATUS);
               in.close();
               DebugPrint.println(DebugPrint.DEBUG, 
                         "Just closed inputstream for Notify ... checking RC");

               if ( !status.equalsIgnoreCase("ok") ) {
                  DebugPrint.println(DebugPrint.ERROR, "Notify failure");
               } else
                  DebugPrint.println(DebugPrint.DEBUG, "Got back OK!");

            } catch ( Exception e ) {
               DebugPrint.println(DebugPrint.ERROR, 
                                  "Notify failed with exception:");
               DebugPrint.println(DebugPrint.ERROR, e);
            }
         }

        // clean up
         remoteHosts.removeAllElements();
		
      }
   }
   
   static public String findFileInOurDirectories(String fname) 
       throws IOException {

      return SearchEtc.findFileInDirectories(fname, 
                                             DesktopServlet.servingDirectories);
   }
   
   static public String findFileWhereEver(String fname) 
      throws IOException {
      if (DesktopServlet.servingDirectories != null) {
         String ret = 
            SearchEtc.findFileInDirectories(fname,
                                            DesktopServlet.servingDirectories);
         if (ret != null) return ret;
      }
      
      return SearchEtc.findFileInClasspath(fname);
   }
   
   static
   private HttpTunnelSession getNewTunnelSession(String desktopId) {
      HttpTunnelSession ret = ServletSessionManager.getSession(desktopId, 
                                                               true);
      String v = getDesktopProperty("edodc.doGovenor");
      if (v != null) {
         boolean doGovenor = v.equalsIgnoreCase("true");
         ret.setDoConsumption(doGovenor);
         ret.sendDoConsumption(doGovenor);
      }
      return ret;
   }
   
   static
   private boolean registerHostingTunnel(String desktopId, 
                                         String uname, 
                                         String company,
                                         String mach, 
                                         String selproj,
                                         Desktopdata desktopdata) {
                                         
      HttpTunnelSession session = getNewTunnelSession(desktopId);

     // register the ICA ear
      if (session.getUserData() == null) {
         session.setUserData(desktopdata);
         session.addActionListener(desktopdata);
      }
      
      session.setEdgeId(uname);
      session.refreshToken();
      
      session.setHosting(true);
      
      Vector v = mappingfiles.getHostingMachines(uname, company);
      String hname = null;
      if (v != null) {      
         if (mach != null) {
            Enumeration e = v.elements();
            while(e.hasMoreElements()) {
               hname = (String)e.nextElement();
               if (hname.equals(mach)) {
                  DebugPrint.printlnd(DebugPrint.INFO, 
                                      "Hosting: User selection user=" + 
                                      uname + " company=" + company + 
                                      " hname=" + hname);
                  break;
               } 
               hname = null;
            }
         } else {
            hname = (String)v.firstElement();
            DebugPrint.printlnd(DebugPrint.ERROR, 
                                "Hosting registration using default (NO MACH set!) user=" + 
                                uname + " company=" + company + 
                                " hname=" + hname);
         }
      }
      if (hname != null) {
         int port = Integer.parseInt(getDesktopProperty("edodc.ctxPort"));
         
         int atidx = hname.indexOf('@');
         if (atidx > 0) {
         
           // Ignore any selected project ... store the PubApp name
            selproj = hname.substring(0, atidx);
            hname = hname.substring(atidx+1);
            DebugPrint.println(DebugPrint.INFO2, 
                               "Hosting session using PubApp");
         }
         
         TunnelEarInfo info = session.createEar (true, hname, port, null);
         
         Hostingdata edata = new Hostingdata();
         edata.setHostname(hname);
         edata.setProject(selproj);
         desktopdata.addDSHSession(edata);
         info.addListener(edata);
      }
      return hname != null;
   }
   
   // MPZ Need to return hostname (and potential pubApp here).
   static private String registerEducationTunnel(String desktopId, 
                                                  String classname,
                                           String uname, String company,
                                           Desktopdata desktopdata) {
     // register the ICA ear
      HttpTunnelSession session = getNewTunnelSession(desktopId);
      
      if (session.getUserData() == null) {
         session.setUserData(desktopdata);
         session.addActionListener(desktopdata);
      }
      
      session.setEdgeId(uname);
      session.refreshToken();
      
      session.setHosting(true);
      String hname = mappingfiles.getEducationMachine(classname, uname, company);
      String ret = hname;
      
      if (hname != null) {
         // MPZ Need to hack apart a published app, if present (for connectivity test).
         int atidx = hname.indexOf('@');
         if (atidx > 0) {
            hname = hname.substring(atidx+1);
         }

         int port = Integer.parseInt(getDesktopProperty("edodc.ctxPort"));
         TunnelEarInfo info = session.createEar (true, hname, port, null);
         
         Educationdata edata = new Educationdata();
         edata.setClassname(classname);
         edata.setHostname(hname);
         info.addListener(edata);
         desktopdata.addEDUSession(edata);
      }
      return ret;
   }
   
   static private byte registerODCTunnel(String desktopId, 
                                         String uname, 
                                         String selproj,
                                         Desktopdata desktopdata) {
     // register the ICA ear
      HttpTunnelSession session = getNewTunnelSession(desktopId);
         
      if (session.getUserData() == null) {
         session.setUserData(desktopdata);
         session.addActionListener(desktopdata);
      }
      
      session.setEdgeId(uname);
      session.refreshToken();
      
      int port = Integer.parseInt(getDesktopProperty("edodc.ctxPort"));
      TunnelEarInfo info = 
         session.createEar(true, 
                           getDesktopProperty("edodc.ctxServer"), port,
                           null);
      
     // Hmmm, lets use a static key for now ... 
      ODCdata odcdata = new ODCdata("ODC");
      odcdata.setProject(selproj);
      desktopdata.addODCSession(odcdata);
      info.addListener(odcdata);
      
      return info.getEarId();
   }
   
   static private byte registerNewODCTunnel(String desktopId, 
                                     String uname, 
                                     String selproj,
                                     Desktopdata desktopdata,
                                     String logintoken) {
                                     
      HttpTunnelSession session = getNewTunnelSession(desktopId);
         
      if (session.getUserData() == null) {
         session.setUserData(desktopdata);
         session.addActionListener(desktopdata);
      }
      
      session.setEdgeId(uname);
      session.refreshToken();
      
     // Force a rewrap token response of newodc ... we need the desktopID in
     //  the login token
      String ret=DesktopServlet.rewrapToken(logintoken, desktopId);
      if (ret != null) {
         ConfigObject co = new ConfigObject();
         co.setProperty("COMMAND", "rewrap_token_resp");
         co.setProperty("TOKEN", ret);
         session.writeControlCommand(co);
      }
      
      int port = Integer.parseInt(getDesktopProperty("edodc.newodcPort"));
      TunnelEarInfo info = 
         session.createEar("NEWODC", 
                           getDesktopProperty("edodc.newodcServer"), port,
                           null);
      
      ODCdata odcdata = new ODCdata("NEWODC");
      odcdata.setProject(selproj);
      desktopdata.addODCSession(odcdata);
      info.addListener(odcdata);
      
      return info.getEarId();
   }
   
   static public  boolean createFTPServiceEar(String desktopId, 
                                              String targethost) {
      boolean ret = false;
      
      HttpTunnelSession sessionMgr = 
         ServletSessionManager.getSession(desktopId, false);
                                                               
      Desktopdata desktopdata = null;
      String v = getDesktopProperty("edodc.support_hftp");
      if (targethost != null && sessionMgr != null && 
          v != null && v.equalsIgnoreCase("true")  &&
          (desktopdata=(Desktopdata)sessionMgr.getUserData()) != null) {
         
         
         Hostingdata dshdata = desktopdata.getLastDSHSession();
         if (dshdata != null && dshdata.getHostname().equals(targethost)) {
            ret = true;
            
            int port = 5555;
            
            v = getDesktopProperty("edodc.ftpHostingPort");
            if (v != null) {
               port = Integer.parseInt(v);
            } else {
               v = getDesktopProperty("edodc.ftpPort");
               if (v != null) {
                  port = Integer.parseInt(v);
               }
            }
            TunnelEarInfo info = sessionMgr.createEar ("ftp", targethost, 
                                                       port, null);
            GenericService data = new GenericService("FTP");
            desktopdata.addGenericSession(data);
            info.addListener(data);
         }
      }
      return ret;
   }
   
   static
   private boolean registerFtpTunnel(String desktopId, String edgeuser,
                                     Desktopdata desktopdata) {
                                     
      HttpTunnelSession session = getNewTunnelSession(desktopId);
      
      if (session.getUserData() == null) {
         session.setUserData(desktopdata);
         session.addActionListener(desktopdata);
      }
      
      session.setEdgeId(edgeuser);
      session.refreshToken();
      
      
      String internal = getDesktopProperty("edodc.ftpInternal");
      String ports    = getDesktopProperty("edodc.ftpPort");
      String host     = getDesktopProperty("edodc.ftpHost");
      
      int port = -1;
      
      try {
         port = Integer.parseInt(ports);
      } catch (Exception ee) {}
      
      if (internal != null && internal.equalsIgnoreCase("true")) {
         port = -1;
         host = null;
      }
 
      TunnelEarInfo info = session.createEar ("ftp", host, port, null);
      GenericService data = new GenericService("FTP");
      desktopdata.addGenericSession(data);
      info.addListener(data);
      
      return true;
   }
   
   static
   private boolean registerXFRTunnel(String desktopId, String edgeuser,
                                     Desktopdata desktopdata,
                                     String logintoken) {
                                     
      HttpTunnelSession session = getNewTunnelSession(desktopId);
      
      if (session.getUserData() == null) {
         session.setUserData(desktopdata);
         session.addActionListener(desktopdata);
      }
      
      session.setEdgeId(edgeuser);
      session.refreshToken();
      
     // Force a rewrap token response of newodc ... we want the local cipher
     //  to be used for XFR login tokens
      String ret=DesktopServlet.rewrapToken(logintoken, desktopId);
      if (ret != null) {
         ConfigObject co = new ConfigObject();
         co.setProperty("COMMAND", "rewrap_token_resp");
         co.setProperty("TOKEN", ret);
         session.writeControlCommand(co);
      }
      
      String ports    = getDesktopProperty("edodc.xfrPort");
      String host     = getDesktopProperty("edodc.xfrHost");
      
      int port = -1;
      
      try {
         port = Integer.parseInt(ports);
      } catch (Exception ee) {}
      
 
      TunnelEarInfo info = session.createEar ("xfr", host, port, null);
      GenericService data = new GenericService("XFR");
      desktopdata.addGenericSession(data);
      info.addListener(data);
      
      return true;
   }
   
   static
   private boolean registerFDRTunnel(String desktopId, String edgeuser,
                                     Desktopdata desktopdata,
                                     String logintoken) {
                                     
      HttpTunnelSession session = getNewTunnelSession(desktopId);
      
      if (session.getUserData() == null) {
         session.setUserData(desktopdata);
         session.addActionListener(desktopdata);
      }
      
      session.setEdgeId(edgeuser);
      session.refreshToken();
      
     // Force a rewrap token response of newodc ... we want the local cipher
     //  to be used for FDR login tokens
      String ret=DesktopServlet.rewrapToken(logintoken, desktopId);
      if (ret != null) {
         ConfigObject co = new ConfigObject();
         co.setProperty("COMMAND", "rewrap_token_resp");
         co.setProperty("TOKEN", ret);
         session.writeControlCommand(co);
      }
      
      String ports    = getDesktopProperty("edodc.fdrPort");
      String host     = getDesktopProperty("edodc.fdrHost");
      
      int port = -1;
      
      try {
         port = Integer.parseInt(ports);
      } catch (Exception ee) {}
      
 
      TunnelEarInfo info = session.createEar ("fdr", host, port, null);
      GenericService data = new GenericService("FDR");
      desktopdata.addGenericSession(data);
      info.addListener(data);
      
      return true;
   }
   
   static
   private boolean registerCONTunnel(String desktopId, String edgeuser,
                                     Desktopdata desktopdata) {
                                     
      HttpTunnelSession session = getNewTunnelSession(desktopId);
      
      if (session.getUserData() == null) {
         session.setUserData(desktopdata);
         session.addActionListener(desktopdata);
      }
      
      session.setEdgeId(edgeuser);
      session.refreshToken();
      
      
      GenericService data = new GenericService("CON");
      desktopdata.addGenericSession(data);
      
     // This message will warn the user that an auto shutdown will occur
     //  We accomlish this by hooking into the next serverping
      ConfigObject co = new ConfigObject();
      co.setProperty("COMMAND", "showclient");
      co.setProperty("MESSAGE", 
                     "Connection will shutdown within a minute ...!"); 
      session.writeControlCommand(co);
      
     // This message will cause Client to popup a nice Connectivity works msg
      co = new ConfigObject();
      co.setProperty("COMMAND", "showclient");
      co.setProperty("MESSAGE", "... but your connectivity tested fine!"); 
      session.writeControlCommand(co);
      
      
      
      return true;
   }
   
  /* subu 05/20/02 */
   static
   private boolean registerIMTunnel(String desktopId, String edgeuser,
                                    Desktopdata desktopdata) {
      HttpTunnelSession session =
         ServletSessionManager.getSession(desktopId, true);
      
      if (session.getUserData() == null) {
         session.setUserData(desktopdata);
         session.addActionListener(desktopdata);
      }
      
      session.setEdgeId(edgeuser);
      session.refreshToken();
      
      int port = 1533;
      String imHost = getDesktopProperty("edodc.imHost");
      String imPort = getDesktopProperty("edodc.imPort");
      try {
         port = Integer.parseInt(imPort);
      } catch(Throwable ttt) {}
      TunnelEarInfo info = session.createEar ("IMS", imHost, port, null);
      return true;
   }
   static
   private boolean registerRMTunnel(String desktopId, String classname, 
                                    String edgeuser, String company, 
                                    Desktopdata desktopdata) {
                                    
      boolean ret = false;
      
      HttpTunnelSession session =
         ServletSessionManager.getSession(desktopId, true);
      
      if (session.getUserData() == null) {
         session.setUserData(desktopdata);
         session.addActionListener(desktopdata);
      }
      
      session.setEdgeId(edgeuser);
      session.refreshToken();
      
      String hname = mappingfiles.getEducationMachine(classname, edgeuser,
                                                      company);
      
      if (hname != null) {
         int port = 554;
         String rmHost = getDesktopProperty("edodc.rmHost");
         String rmPort = getDesktopProperty("edodc.rmPort");
         try {
            port = Integer.parseInt(rmPort);
         } catch(Throwable ttt) {}
         
         TunnelEarInfo info = session.createEar ("STM", rmHost, port, 
                                                 hname + "?" + edgeuser);
         ret = true;
      }
      
      return ret;
   }
   
  /* subu 04/14/03*/
   static
   private boolean registerDesktopOnCallTunnel(String desktopId, 
                                               String edgeuser,
                                               Desktopdata desktopdata) {
       HttpTunnelSession session =
         ServletSessionManager.getSession(desktopId, true);
      
      if (session.getUserData() == null) {
         session.setUserData(desktopdata);
         session.addActionListener(desktopdata);
      }
      
      session.setEdgeId(edgeuser);
      session.refreshToken();
      
      int port1 = 8089;
      int port2 = 25345;
      int port3 = 25346;
      int port4 = 25347;
      int port5 = 25348;
      int port6 = 25349;
      int port7 = 25350;
      int port8 = 25351;
      
      String DesktopOnCallHost  = getDesktopProperty("eddoc.host");
      if (DesktopOnCallHost == null) return false;
      
      try {
         String DesktopOnCallPort1 = getDesktopProperty("eddoc.port1");
         port1 = Integer.parseInt(DesktopOnCallPort1);
         String DesktopOnCallPort2 = getDesktopProperty("eddoc.startingPort2");
         port2 = Integer.parseInt(DesktopOnCallPort2);
         port3 = port2+1;
         port4 = port3+1;
         port5 = port4+1;
         port6 = port5+1;
         port7 = port6+1;
         port8 = port7+1;
      } catch(Throwable ttt) {
         return false;
      }
         
      session.createEar ("DesktopOnCallService1", DesktopOnCallHost, port1, 
                         null);
      session.createEar ("DesktopOnCallService2", DesktopOnCallHost, port2,
                         null);
      session.createEar ("DesktopOnCallService3", DesktopOnCallHost, port3,
                         null);
      session.createEar ("DesktopOnCallService4", DesktopOnCallHost, port4,
                         null);
      session.createEar ("DesktopOnCallService5", DesktopOnCallHost, port5,
                         null);
      session.createEar ("DesktopOnCallService6", DesktopOnCallHost, port6,
                         null);
      session.createEar ("DesktopOnCallService7", DesktopOnCallHost, port7,
                         null);
      session.createEar ("DesktopOnCallService8", DesktopOnCallHost, port8,
                         null);
      return true;
      
   }
   
   static
   private void deregisterXTunnel(String id) {
      DesktopView dv = mgr.getDesktop(id);
      if (dv != null) {
         String display = dv.getXDisplay();
         StringTokenizer tokens = new StringTokenizer(display, ":");
         String host = tokens.nextToken();
         String portStr = tokens.nextToken();
         int port = -1;
         try {
            port = Integer.parseInt(portStr);
         } catch ( Exception e ) {
            DebugPrint.println(DebugPrint.DEBUG, 
                               "Error deregisteringXTunnel info");
            DebugPrint.println(DebugPrint.ERROR, e);
         }
         HttpTunnelSession session = 
            ServletSessionManager.getSession(id, false);
         if (port >= 0) {
            session.cleanupConnectionEarByPort(host, port+6000);
         }
         
      }
   }
   
   static
   private void registerXTunnel(String id, Properties prop, 
                                Desktopdata desktopdata) {

      String display = prop.getProperty(DesktopCommon.DISPLAY);
      String cookie = prop.getProperty(DesktopCommon.COOKIE);
      StringTokenizer tokens = new StringTokenizer(display, ":");
      String host = tokens.nextToken();
      String portStr = tokens.nextToken();
      int port = -1;
      try {
         port = Integer.parseInt(portStr);
      } catch ( Exception e ) {
         DebugPrint.println(DebugPrint.DEBUG, 
                            "Error registeringXTunnel info");
         DebugPrint.println(DebugPrint.ERROR, e);
      }

     // now register with the X tunnel
      HttpTunnelSession session = ServletSessionManager.getSession(id, false);
      TunnelEarInfo tei = session.createEar(false, host, 6000+port, cookie);
      
      ODCdata odcdata = desktopdata.getLastODCSession();
      if (odcdata != null) {
         Meetingdata meetingdata = new Meetingdata();
         odcdata.addMeeting(meetingdata);
         tei.addListener(meetingdata);
      } else {
         DebugPrint.printlnd("Gak!, no ODC entry while registering X tunnel!");
      }
   }
   
   static public void handleXChannelStartup(TunnelEarInfo tei, 
                                            Socket socket) throws IOException {
      String pphrase = (String)tei.getUserData();
      String cookie  = tei.getOtherInfo();
      
      byte cookieBytes[] = cookie.getBytes();
      int  cookielen = cookieBytes.length;
      
      byte buf[] = new byte[1024];
      
      OutputStream out = socket.getOutputStream();
      InputStream   in = socket.getInputStream();
      buf[0] = (byte)((cookielen >> 8) & 0xff);
      buf[1] = (byte)((cookielen     ) & 0xff);
      out.write(buf, 0, 2);
      out.write(cookieBytes);
      out.flush();
      
      int bytesread = 0;
      int passlen = 0;
      while(bytesread != 2) {
         int l = in.read(buf, bytesread, 2-bytesread);
         if (l == -1) throw new IOException("Passphrase len not read");
         bytesread += l;
      }
      
      passlen = ((((int)buf[0]) & 0xff) << 8) | (((int)buf[1]) & 0xff);
      if (passlen <= 0 || passlen > 1024) { 
         throw new IOException("passlength is too long");
      }
      
      bytesread = 0;
      while(bytesread != passlen) {
         int l = in.read(buf, bytesread, passlen-bytesread);
         if (l == -1) throw new IOException("Passphrase not read");
         bytesread += l;
      }
      
      String rcvphrase = new String(buf, 0, passlen);
      rcvphrase = makehash(rcvphrase);
      if (!rcvphrase.equals(pphrase)) {
         throw new IOException("Pass Phrase does not match");
      }
   }
   
   static
   private void registerXChannel(String id, String host, String display, 
                                 String cookie, String passphrase) {

      StringTokenizer tokens = new StringTokenizer(display, ":");
      String hostS = tokens.nextToken();
      String portStr = tokens.nextToken();
      int port = -1;
      try {
         int idx = portStr.indexOf(".");
         if (idx >= 0) portStr = portStr.substring(0,idx);
         port = Integer.parseInt(portStr);
      } catch ( Exception e ) {
         DebugPrint.println(DebugPrint.DEBUG, 
                            "Error registeringXChannel info");
         DebugPrint.println(DebugPrint.ERROR, e);
      }

     // now register with the X tunnel
      HttpTunnelSession session = ServletSessionManager.getSession(id, false);
      TunnelEarInfo tei = session.createEar(false, host, port, cookie);
      tei.setUserData(passphrase);
      
      Desktopdata ddata = (Desktopdata)session.getUserData();
         
      GenericService gs = new GenericService("WHB");
      gs.setHostname(host);
      ddata.addGenericSession(gs);
      tei.addListener(gs);
   }
   
  // Used to set the passphrase which will allow hookup of an XChannel
  // Session has to have active EDU session, and be in Education table 
  //  to attend class named "AllowXWindows"
   static public boolean setXChannelPassphrase(String id, String passphrase) {
      
      boolean isallowed = false;
      
      HttpTunnelSession session = ServletSessionManager.getSession(id, false);
      if (id == null) return false;
      
      try {
         Desktopdata dd = (Desktopdata)session.getUserData();
         DesktopView desktop = dd.getDesktop();
         Educationdata edudata = dd.getLastEDUSession();
         if (edudata != null &&
             mappingfiles.getEducationMachine("AllowXWindows", 
                                              desktop.getEdgeId(), 
                                              desktop.getCompany()) != null) {
            isallowed = true;
         }
      } catch(Throwable tt) {}
      
      if (isallowed) {
         Properties props = new Properties();
         props.setProperty(DesktopCommon.DESKTOP_ID, id);
         if (passphrase != null) passphrase = makehash(passphrase);
         props.setProperty(DesktopCommon.ALIAS, passphrase);
         if (mgr.bindDesktop(props) == null) {
            isallowed = false;
            DebugPrint.printlnd(DebugPrint.ERROR, 
                                "Error binding passphrase for desktop " + id);
         }
      } else {
         DebugPrint.printlnd(DebugPrint.WARN, 
                             "Error binding passphrase for desktop " + id);
      }
      
      return isallowed;
   }
   
   static
   public void showRequestInfo(PrintStream out, javax.servlet.http.HttpServletRequest req) {
      out.println("==== Request Information ====");
      out.println(" Request method: " + req.getMethod());
      out.println(" Request URI: " + req.getRequestURI());
      out.println(" Request protocol: " + req.getProtocol());
      out.println(" Servlet path: " + req.getServletPath());
      out.println(" Path info: " + req.getPathInfo());
      out.println(" Path translated: " + req.getPathTranslated());
      out.println(" Character encoding: " + req.getCharacterEncoding());
      out.println(" Query string: " + req.getQueryString());
      out.println(" Content length: " + req.getContentLength());
      out.println(" Content type: " + req.getContentType());
      out.println(" Server name: " + req.getServerName());
      out.println(" Server port: " + req.getServerPort());
      out.println(" Remote user: " + req.getRemoteUser());
      out.println(" Remote address: " + req.getRemoteAddr());
      out.println(" Remote host: " + req.getRemoteHost());
      out.println(" Authorization scheme: " + req.getAuthType());

      Enumeration e = req.getHeaderNames();
      if(e.hasMoreElements()) {
         out.println("==== Request Headers ====");
         while(e.hasMoreElements()) {
            String name = (String)e.nextElement();
            out.println("  " + name + ": " + req.getHeader(name));
         }
      }

      e = req.getParameterNames();
      if(e.hasMoreElements()) {
         out.println("==== Servlet Parameters (Single Value style) ====");
         while(e.hasMoreElements()) {
            String name = (String)e.nextElement();
            out.println("  " + name + " = " + req.getParameter(name));
         }
      }

      e = req.getParameterNames();
      if(e.hasMoreElements()) {
         out.println("==== Servlet parameters (Multiple Value style) ====");
         while(e.hasMoreElements()) {
            String name = (String)e.nextElement();
            String vals[] = (String []) req.getParameterValues(name);
            if(vals != null) {
               out.print(" " + name + " = "); 
               out.println(vals[0]);
               for(int i = 1; i<vals.length; i++)
                  out.println("            " + vals[i]);
            }
         }
      }
   }
   
  // This is called to during a tunnel shutdown. ActionEvent triggers it
   public static void tunnelShuttingDown(HttpTunnelSession session, 
                                         String reason) {
      String desktopId = session.desktopID();
      DebugPrint.printlnd(DebugPrint.INFO, 
                          "DTServlet: shuttingDownTunnel: " + reason + ": " +
                          desktopId);
      mgr.destroyDesktop(desktopId, session);
   }
   
  // This is called to affect a shutdown of the tunnel
   public static boolean shutdownTunnel(String desktopId, String reason) {
      boolean ret = false;
      DebugPrint.printlnd(DebugPrint.INFO, 
                          "DTServlet: shutdownTunnel: " + reason + ": " +
                          desktopId);
                         
      HttpTunnelSession session = ServletSessionManager.getSession(desktopId, false);
      if ( session != null ) {
         ret = true;
         session.shutdown();
      } else {
         DebugPrint.println(DebugPrint.WARN, 
                            "At shutdown tunnel, invalid desktop Id: " + 
                            desktopId);
         ret = mgr.destroyDesktop(desktopId, session);
      }
      return ret;
   }
   
   static
   private void writeStatus(HttpServletResponse res, boolean rc) {
      writeStatus(res, rc, null);
   }
   static
   private void writeStatus(HttpServletResponse res, boolean rc, String s) {
      try {
         ConfigObject ini = new ConfigObject();
         if (s != null) {
            ini.setProperty("ERRORMSG", s);
         }
         ini.setProperty(DesktopCommon.STATUS, rc?"OK":"FAILURE");
         ini.save(res.getOutputStream());
      } catch ( Exception e ) {
         DebugPrint.println(DebugPrint.DEBUG, 
                            "Error in write status: " + s);
         DebugPrint.println(DebugPrint.ERROR, e);
      }
   }
   
   static
   private String buildFrontMatter(DesktopView desktop, String wall) {
      String owner = desktop.getEdgeId();
      if (owner == null)   owner = "NotSet";
      String first = desktop.getFirstName();
      if (first == null)   first = "NotSet";
      String last = desktop.getLastName();
      if (last == null)    last = "NotSet";
      String email = desktop.getEmailAddr();
      if (email == null)   email = "NotSet";
      String company = desktop.getCompany();
      if (company == null) company = "NotSet";
      
      String frontmatter = "<tr>";
      if (wall != null) frontmatter += "<td>" + wall + "</td>";
      frontmatter += "<td>" + owner   + "</td>" +
         "<td>" + first   + "</td>" +
         "<td>" + last    + "</td>" +
         "<td>" + email   + "</td>" +
         "<td>" + company + "</td>";
      return frontmatter;
   }
   
  // JMC 1/5/01 - Display some stats
   static
   private void displayStatus(PrintWriter out, Properties prop, 
                              javax.servlet.http.HttpServletRequest req, 
                              javax.servlet.http.HttpServletResponse res) {
     //out.println("Properties for Status were:<p />");
     // prop.list(out);
     
     // If redirecting is ON, come here specifically
     // Otherwise, we have to assume that Session Affinity is ON
      String phead = "";
      String doRedir = getDesktopProperty("useIPRedirect");
      if (doRedir != null && doRedir.equalsIgnoreCase("true")) {
         phead = req.getScheme() + "://" + DomainIP;
      }
      String head = phead + servletcontextpath;
      
      String urlstart = head;
     
      String urlS = res.encodeURL(servletcontextpath + 
                                  "/servlet/oem/edge/ed/odc/desktop/logging") + 
         "?compname=" + generateCipher("loggingS.", 60*60);
         
      Enumeration e = ServletSessionManager.getSessions();
      out.println("<p />ODC STATUS: " + (new Date()).toString());
      out.println("<p />Set PingInterval " + 
                     
                  " <a href=\"" +
                  res.encodeURL(urlstart +
                                "/servlet/oem/edge/ed/odc/desktop/setping"   + 
                                "?compname=" + generateCipher("setping1", 
                                                              60*60)) +
                  "\">\n" +
                  " 1sec</a>"+
                  " <a href=\"" +
                  res.encodeURL(urlstart +
                                "/servlet/oem/edge/ed/odc/desktop/setping"   + 
                                "?compname=" + generateCipher("setping5", 
                                                              60*60)) + 
                  "\">\n" +
                  " 5sec</a>"+
                  " <a href=\"" +
                  res.encodeURL(urlstart +
                                "/servlet/oem/edge/ed/odc/desktop/setping"   + 
                                "?compname=" + generateCipher("setping10", 
                                                              60*60))+
                  "\">\n" +
                  " 10sec</a>"+
                  " <a href=\"" +
                  res.encodeURL(urlstart +
                                "/servlet/oem/edge/ed/odc/desktop/setping"   + 
                                "?compname=" + generateCipher("setping30",
                                                              60*60))+
                  "\">\n" +
                  " 30sec</a>"+
                  " <a href=\"" +
                  res.encodeURL(urlstart +
                                "/servlet/oem/edge/ed/odc/desktop/setping"   + 
                                "?compname=" + generateCipher("setping60",
                                                              60*60))+
                  "\">\n" +
                  " 60sec</a>"+
               
                  "<p />Set DebugLevel " + 
                  " <a href=\"" +
                  res.encodeURL(urlstart +
                                "/servlet/oem/edge/ed/odc/desktop/setlevel"  + 
                                "?compname=" + generateCipher("setlevel1",
                                                              60*60))+
                  "\">\n" +
                  " E1</a>"+
                  " <a href=\"" +
                  res.encodeURL(urlstart +
                                "/servlet/oem/edge/ed/odc/desktop/setlevel"  + 
                                "?compname=" + generateCipher("setlevel2",
                                                              60*60))+
                  "\">\n" +
                  " E2</a>"+
                  " <a href=\"" +
                  res.encodeURL(urlstart +
                                "/servlet/oem/edge/ed/odc/desktop/setlevel"  + 
                                "?compname=" + generateCipher("setlevel3",
                                                              60*60))+
                  "\">\n" +
                  " E3</a>"+
                  " <a href=\"" +
                  res.encodeURL(urlstart +
                                "/servlet/oem/edge/ed/odc/desktop/setlevel"  + 
                                "?compname=" + generateCipher("setlevel4",
                                                              60*60))+
                  "\">\n" +
                  " E4</a>"+
                  " <a href=\"" +
                  res.encodeURL(urlstart +
                                "/servlet/oem/edge/ed/odc/desktop/setlevel"  + 
                                "?compname=" + generateCipher("setlevel5",
                                                              60*60))+
                  "\">\n" +
                  " E5</a>"+
                  " <a href=\"" +
                  res.encodeURL(urlstart +
                                "/servlet/oem/edge/ed/odc/desktop/setlevel"  + 
                                "?compname=" + generateCipher("setlevel6",
                                                              60*60))+
                  "\">\n" +
                  " W1</a>"+
                  " <a href=\"" +
                  res.encodeURL(urlstart +
                                "/servlet/oem/edge/ed/odc/desktop/setlevel"  + 
                                "?compname=" + generateCipher("setlevel7",
                                                              60*60))+
                  "\">\n" +
                  " W2</a>"+
                  " <a href=\"" +
                  res.encodeURL(urlstart +
                                "/servlet/oem/edge/ed/odc/desktop/setlevel"  + 
                                "?compname=" + generateCipher("setlevel8",
                                                              60*60))+
                  "\">\n" +
                  " W3</a>"+
                  " <a href=\"" +
                  res.encodeURL(urlstart +
                                "/servlet/oem/edge/ed/odc/desktop/setlevel"  + 
                                "?compname=" + generateCipher("setlevel9",
                                                              60*60))+
                  "\">\n" +
                  " W4</a>"+
                  " <a href=\"" +
                  res.encodeURL(urlstart +
                                "/servlet/oem/edge/ed/odc/desktop/setlevel"  + 
                                "?compname=" + generateCipher("setlevel10",
                                                              60*60))+
                  "\">\n" +
                  " W5</a>"+
                  " <a href=\"" +               
                  res.encodeURL(urlstart +
                                "/servlet/oem/edge/ed/odc/desktop/setlevel"  + 
                                "?compname=" + generateCipher("setlevel11",
                                                              60*60))+
                  "\">\n" +
                  " I1</a>"+
                  " <a href=\"" +
                  res.encodeURL(urlstart +
                                "/servlet/oem/edge/ed/odc/desktop/setlevel"  + 
                                "?compname=" + generateCipher("setlevel12",
                                                              60*60))+
                  "\">\n" +
                  " I2</a>"+
                  " <a href=\"" +
                  res.encodeURL(urlstart +
                                "/servlet/oem/edge/ed/odc/desktop/setlevel"  + 
                                "?compname=" + generateCipher("setlevel13",
                                                              60*60))+
                  "\">\n" +
                  " I3</a>"+
                  " <a href=\"" +
                  res.encodeURL(urlstart +
                                "/servlet/oem/edge/ed/odc/desktop/setlevel"  + 
                                "?compname=" + generateCipher("setlevel14",
                                                              60*60))+
                  "\">\n" +
                  " I4</a>"+
                  " <a href=\"" +
                  res.encodeURL(urlstart +
                                "/servlet/oem/edge/ed/odc/desktop/setlevel"  + 
                                "?compname=" + generateCipher("setlevel15",
                                                              60*60))+
                  "\">\n" +
                  " I5</a>"+
                  " <a href=\"" +
                  res.encodeURL(urlstart +
                                "/servlet/oem/edge/ed/odc/desktop/setlevel"  + 
                                "?compname=" + generateCipher("setlevel16",
                                                              60*60))+
                  "\">\n" +
                  " D1</a>"+
                  " <a href=\"" +
                  res.encodeURL(urlstart +
                                "/servlet/oem/edge/ed/odc/desktop/setlevel"  + 
                                "?compname=" + generateCipher("setlevel17",
                                                              60*60))+
                  "\">\n" +
                  " D2</a>"+
                  " <a href=\"" +
                  res.encodeURL(urlstart +
                                "/servlet/oem/edge/ed/odc/desktop/setlevel"  + 
                                "?compname=" + generateCipher("setlevel18",
                                                              60*60))+
                  "\">\n" +
                  " D3</a>"+
                  " <a href=\"" +
                  res.encodeURL(urlstart +
                                "/servlet/oem/edge/ed/odc/desktop/setlevel"  + 
                                "?compname=" + generateCipher("setlevel19",
                                                              60*60))+
                  "\">\n" +
                  " D4</a>"+
                  " <a href=\"" +
                  res.encodeURL(urlstart +
                                "/servlet/oem/edge/ed/odc/desktop/setlevel"  + 
                                "?compname=" + generateCipher("setlevel20",
                                                              60*60))+
                  "\">\n" +
                  " D5</a>"+
                  " <a href=\"" +
                  res.encodeURL(urlstart +
                                "/servlet/oem/edge/ed/odc/desktop/setlevel"  + 
                                "?compname=" + generateCipher("setlevel255",
                                                              60*60))+
                  "\">\n" +
                  " 255</a>"+
                  " <a href=\"" +
                  res.encodeURL(urlstart +
                                "/servlet/oem/edge/ed/odc/desktop/setlevel"  + 
                                "?compname=" + generateCipher("setlevel1024",
                                                              60*60))+
                  "\">\n" +
                  " 1024</a><p />");
      
      out.println("<form method=\"POST\" action=\"" + urlS + "\">"); 
      
      out.println("<table border = 0 ><tr>");
      out.println("<td align=\"center\"><font color=\"red\">WALL Message(to chked)</font>:</td>");
      out.println("<td><label id=\"a\"><input id=\"a\" name=\"WALL\" type=\"text\" maxlength = 1000 size = 30/></label><input name=\"submit\" type=\"submit\" value=\"Send\" align=\"center\" /></td>");
      out.println("<td><label id=\"a\"><input id=\"a\" name=\"wallallchk\" type=\"checkbox\" />Send To All</label></td></tr></table>" );
      
     // Get Desktops in Owner sorted order
      Vector sortv = new Vector();
      while(e.hasMoreElements()) {
         String did = (String)e.nextElement();
         DesktopView desktop = mgr.getDesktop(did);
         String owner = desktop.getEdgeId();
         if (owner == null) owner = "";
         
         boolean added = false;
         int len = sortv.size();
         int i;
         for(i=0; i < len; i++) {
            DesktopView td = (DesktopView)sortv.elementAt(i);
            String to = td.getEdgeId();
            if (to == null) to = "";
            
            int cmp = owner.compareTo(to);
            
            if (cmp <= 0) {
               sortv.insertElementAt(desktop, i);
               break;
            }
         }
         
         if (i == len) {
            sortv.addElement(desktop);
         }
      }
      
     // Loop across each Desktop, and calc total values
      String startTable1 = 
         "<p /><table border><tr><th>wall</th><th>EdgeId</th><th>First</th><th>Last</th><th>Email</th><th>Company</th>" +
         SMStats.getHTMLTableHeader() +
         "<th>Id</th><th>Magic</th></tr>";
         
      String startTable = 
         "<p /><table border><tr><th>EdgeId</th><th>First</th><th>Last</th><th>Email</th><th>Company</th>" +
         SMStats.getHTMLTableHeader() +
         "<th>Id</th><th>Magic</th></tr>";
         
                                        
      SMStats stats;
      Date tdate = new Date();
      
     /* 
     ** ----- Do Totals since session inception
     */
      stats = new SMStats();
      out.print(startTable1);
      e = sortv.elements();
      while(e.hasMoreElements()) {
         
         DesktopView desktop=(DesktopView)e.nextElement();
      
         HttpTunnelSession session = 
            ServletSessionManager.getSession(desktop.getKey(), false);
            
        // SMStats sinceStats = session.getLastIntervalStats();
         SMStats lstats   = session.getTotalStats();
            
         stats.addMerge(lstats);
         
         String wall = "<label id=\"a\"><input id=\"a\" name=\"wallchk_" + 
            session.desktopID() + 
            "\" type=\"checkbox\" \"checked\" /></label>"; 
         out.println(buildFrontMatter(desktop, wall));
         
         out.println(lstats.toHTMLString(tdate));
         
         String magic = session.getToken();
         if (magic.length() > 5) {
            magic = magic.substring(0, 5);
         }
         out.println("<td>" + desktop.getKey() + "</td><td>" + magic +  
                     "</td></tr>");
         out.flush();
      }
      
      out.println("<tr><td></td><td>TOTALS</td><td></td><td></td><td></td><td></td>" + stats.toHTMLString(tdate) + "</tr>");
      out.println("<caption>Total Session Values : " +
                  stats.getResetDate().toString()+ "</table>");

      out.println("</form>");
      
      
     /* 
     ** ----- Do Totals since last Query
     */
      stats = new SMStats();
      out.print(startTable);
      e = sortv.elements();
      while(e.hasMoreElements()) {
         
         DesktopView desktop=(DesktopView)e.nextElement();
      
         HttpTunnelSession session = 
            ServletSessionManager.getSession(desktop.getKey(), false);
            
         SMStats lstats = session.getSinceLastStats();
         
         stats.addMerge(lstats);
         
         out.println(buildFrontMatter(desktop, null));
         out.println(lstats.toHTMLString(tdate));
         out.println("<td>" + desktop.getKey() + "</td></tr>");
         
         session.resetSinceLastStats();
      }
      out.println("<tr><td>TOTALS</td><td></td><td></td><td></td><td></td>" + stats.toHTMLString(tdate) + "</tr>");
      out.println("<caption>Since Last Query Values : " +
                  stats.getResetDate().toString()+ "</table>");
      
     /* 
     ** ----- Do Totals over current interval
     */
      stats = new SMStats();
      out.print(startTable);
      e = sortv.elements();
      while(e.hasMoreElements()) {
         
         DesktopView desktop=(DesktopView)e.nextElement();
      
         HttpTunnelSession session = 
            ServletSessionManager.getSession(desktop.getKey(), false);
            
         SMStats lstats = session.getCurrentIntervalStats();
         
         stats.addMerge(lstats);
         
         out.println(buildFrontMatter(desktop, null));
         out.println(lstats.toHTMLString(tdate));
         out.println("<td>" + desktop.getKey() + "</td></tr>");
      }
      
      out.println("<tr><td>TOTALS</td><td></td><td></td><td></td><td></td>" + stats.toHTMLString(tdate) + "</tr>");
      out.println("<caption>Current Interval Values : " +
                  stats.getResetDate().toString()+ "</table>");
      
     /* 
     ** ----- Do Totals since over Last interval
     */
      stats = new SMStats();
      out.print(startTable);
      e = sortv.elements();
      while(e.hasMoreElements()) {
         
         DesktopView desktop=(DesktopView)e.nextElement();
      
         HttpTunnelSession session = 
            ServletSessionManager.getSession(desktop.getKey(), false);
            
         SMStats lstats = session.getLastIntervalStats();
         
         stats.addMerge(lstats);
         
         out.println(buildFrontMatter(desktop, null));
         out.println(lstats.toHTMLString(tdate));
         out.println("<td>" + desktop.getKey() + "</td></tr>");
      }
      
      out.println("<tr><td>TOTALS</td><td></td><td></td><td></td><td></td>" + stats.toHTMLString(tdate) + "</tr>");
      out.println("<caption>Last Interval Values : " + 
                  stats.getResetDate().toString()+ "</table>");
      
      
      int np = SMStats.MAX_PINGS;
     // Last n pings
      out.println("<p /><table border><tr><th>EdgeId</th><th>Dbg/SVC</th><th>Id/IP</th>");
      for(int i=0; i < 5; i++) {
         out.println("<th>Pid</th><th>Time</th><th>ms</th>");
      }
      Calendar cal = Calendar.getInstance();
      e = sortv.elements();
      while(e.hasMoreElements()) {
         
         DesktopView desktop=(DesktopView)e.nextElement();
      
         HttpTunnelSession session = 
            ServletSessionManager.getSession(desktop.getKey(), false);
            
         Desktopdata dd = (Desktopdata)session.getUserData();
         String svcinfo = "";
         if (dd != null) {
            if (dd.getLastODCSession() != null) {
               if (svcinfo.length() != 0) svcinfo += ",";
               svcinfo += "NEWODC";
            }
            if (dd.getLastDSHSession() != null) {
               if (svcinfo.length() != 0) svcinfo += ",";
               svcinfo += "DSH";
            }
            if (dd.getLastEDUSession() != null) {
               if (svcinfo.length() != 0) svcinfo += ",";
               svcinfo += "EDU";
            }
            Hashtable ghash = dd.getGenericSessions();
            if (ghash != null) {
               Enumeration genum = ghash.elements();
               while(genum.hasMoreElements()) {
                  GenericService gsvc = (GenericService)genum.nextElement();
                  if (svcinfo.length() != 0) svcinfo += ",";
                  svcinfo += gsvc.getServiceType();
               }
            }
         }
            
         SMStats lstats   = session.getTotalStats();
         
         out.println("<tr><td>" + session.getEdgeId() + "</td>");
         
         String urlT =  res.encodeURL(urlstart + "/servlet/oem/edge/ed/odc/desktop/logging") + 
                       "?compname=" + 
                       generateCipher("loggingT"+session.desktopID(), 60*60);
         String urlF = res.encodeURL(urlstart + "/servlet/oem/edge/ed/odc/desktop/logging") + 
                       "?compname=" + 
                       generateCipher("loggingF"+session.desktopID(), 60*60);
         String urlC = res.encodeURL(urlstart + "/servlet/oem/edge/ed/odc/desktop/logging") + 
                       "?compname=" + 
                       generateCipher("loggingC"+session.desktopID(), 60*60);
            
         out.println("<td>" + 
                     "<a href=\"" + urlT + "\">" + session.getDoLogging()+ "</a>"
                     + " <a href=\"" + urlF + "\">Flush</a>"
                     + " <a href=\"" + urlC + "\">Shutdown</a></td>");
         out.println("<td>" + session.desktopID() + "</td>");
         for(int i=lstats.getNumPings(), j=0; --i >= 0; j++) {
            cal.setTime(new Date(lstats.getPingTime(i)));
            String tm =  
               ""  + cal.get(cal.HOUR_OF_DAY) + 
               ":" + cal.get(cal.MINUTE) + 
               ":" + cal.get(cal.SECOND);
            
            if (j > 0 && (j % 5) == 0) {
               if (j == 5) {
                  out.println("</tr><tr><td></td><td>" + svcinfo + 
                              "</td><td>" + session.getIPAddress() + "</td>");
               } else {
                  out.println("</tr><tr><td></td><td></td><td></td>");
               }
               
            }
            out.println("<td>" + lstats.getPingId(i) + "</td>" +
                        "<td>" + tm                  + "</td>" + 
                        "<td><b>" + lstats.getPingMS(i) + "</b></td>");
         }
         if (lstats.getNumPings() < 6) {
            out.println("</tr><tr><td></td><td>" + svcinfo + 
                        "</td><td>" + session.getIPAddress() + "</td>");
         }
         out.println("</tr>");
      }
      
      out.println("<caption>Last " + SMStats.MAX_PINGS + 
                  " Ping values (ms)</table>");
      out.flush();
      
     /*
     ** ----- OLD info!
     */
      out.println("<p /><p /><p />-- OLD WAY --<p /><p /><p />");
     // Loop across each Desktop, and calc total values
      e = ServletSessionManager.getSessions();
      while(e.hasMoreElements()) {
         String did = (String)e.nextElement();
         out.println("<p /><p />ID = [" + did + "]<p /><pre>");
         
         
         
         HttpTunnelSession session = 
            ServletSessionManager.getSession(did, false);
         out.println(session.toString());
         out.println("</pre>");
      }
      
     /*
     ** Log4j info
     */
      out.println("<p /><p />-- Log4j debug --<p />");
      out.println("<table><tr><th></th><th>Inherited</th><th>Name</th></tr>");
      LoggerRepository repos = LogManager.getLoggerRepository();
      Enumeration loggers = repos.getCurrentLoggers();
      
      Vector allogers = new Vector();
      allogers.add(repos.getRootLogger());
      while(loggers.hasMoreElements()) {
         allogers.add(loggers.nextElement());
      }
      loggers = allogers.elements();
      
      Level levarr[] = new Level[] {
         Level.OFF,  Level.FATAL, Level.ERROR, Level.WARN, 
         Level.INFO, Level.DEBUG, Level.ALL
      } ;
      
      int linenum=0;
      while(loggers.hasMoreElements()) {
         Logger logger = (Logger)loggers.nextElement();
         Level lev    = logger.getEffectiveLevel();
         Level actlev = logger.getLevel();
         String name = logger.getName();
         String anchorname = "log4janchor_" + (++linenum);
         
         out.println("<tr><td>&nbsp;&nbsp;&nbsp;</td><td>");
         
        // Show if its inherited or not
         out.print("<a name=\"" + anchorname + "\"></a>");
         if (actlev == null) out.print("X");
         
         out.println("</td><td>");
         out.print(name);
         out.println("</td>");
      
         for(int ii = 0; ii < levarr.length; ii++) {
            Level thislev = levarr[ii];
            out.print("<td>");
            if (lev.toInt() != thislev.toInt()) {
               String urlLev = res.encodeURL(urlstart +
                                             "/servlet/oem/edge/ed/odc/desktop/setlog4j" + 
                                             "?compname=" + 
                                             generateCipher("setlog4j:"+
                                                            thislev.toInt()+":"+name,
                                                            60*60) + 
                                             "#" + anchorname);

               out.print("<a href=\"");
               out.print(urlLev);
               out.print("\">");
               out.print(thislev.toString());
               out.print("</a>");
            } else {
               out.print(thislev.toString());
            }
            out.println("</td>");
         }
         out.println("</tr>");
      }
      out.println("</table>");
      
     /*
     ** ----- Timeouts
     */
      out.println("<p /><p />-- Timeouts --<p />");
      out.println("<p /><pre>");
      TimeoutManager tmgr = TimeoutManager.getGlobalManager();
      
      out.println(tmgr.toString());
      
      out.println("</pre>");
      
      out.println("<p /><p />Status Done: " + (new Date()).toString());
      
      
      out.println("<p />Current Java Heap use statistics:");
      Runtime rt = Runtime.getRuntime();
      long tm = rt.totalMemory();
      long fm = rt.freeMemory();
      out.println("preGC  MemoryUsage Total: " + tm + 
                  " Free: "  + fm + 
                  " Used: "  + (tm-fm));
      rt.gc();
      long ptm = rt.totalMemory();
      long pfm = rt.freeMemory();
      out.println("<p />postGC  MemoryUsage Total: " + ptm + 
                  " Free: "  + pfm + 
                  " Used: "  + (ptm-pfm));
      
      out.println("<p />delta   MemoryUsage Total: " + (ptm-tm) + 
                  " Free: "  + (pfm-fm) + 
                  " Used: "  + ((ptm-pfm)-(tm-fm)));
      
   }
   
   public static void processStatus() {
     
     
      Date newDate = new Date();
      DebugPrint.println(DebugPrint.INFO4, 
                         "ODC: Updating status interval: " + 
                         newDate.toString());
                         
     /*
      Thread l[] = new Thread[500];
      
      ThreadGroup cur = Thread.currentThread().getThreadGroup();
      ThreadGroup par = null;
      int cnt = 0;
      while((par=cur.getParent()) != null && cnt++ < 200) {
         cur = par;
      }
      
      int num = cur.enumerate(l, true);
      DebugPrint.println("ThrdGrpParentage: " + cnt + " numthrds: " + num);
      for(int i=0; i < num; i++) {
         DebugPrint.println("---------- [" + i + "] -------: " +
                            l[i].toString());
        // l[i].printStackTrace(System.out);
      }
     */  
     
      Enumeration e = ServletSessionManager.getSessions();
      
      while(e.hasMoreElements()) {
         String did = (String)e.nextElement();
         HttpTunnelSession session = 
            ServletSessionManager.getSession(did, false);
         session.copyCurrentToLastAndResetStats(newDate);
      }
   }
}
