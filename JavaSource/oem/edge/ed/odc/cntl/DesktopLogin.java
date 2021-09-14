package oem.edge.ed.odc.cntl;

import java.util.*;
import java.io.*;
import java.lang.*;
import javax.servlet.http.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

import oem.edge.common.cipher.*;
import oem.edge.common.RSA.*;

import com.ibm.as400.webaccess.common.*;
import oem.edge.ed.odc.tunnel.common.*;
import oem.edge.ed.util.*;
import oem.edge.ed.odc.util.*;

/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2002-2005,2006                                */
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

/*
** This file provide Loginpage support to DesktopServlet Split out to keep
** DesktopServlet less bulky. Still references DesktopServlet static meths
** and data all over the place. In most cases these refs are for other 
** routines that should be refactored. TODO
*/
public class DesktopLogin {

   private ConfigObject frontPageCO = null;
   private Byte frontPageSync       = new Byte((byte)1);
   private String lastFrontPageFile = null;
   private long   frontPageFileTime = 0;
   
   private ReloadingProperty props = null;
   
   public DesktopLogin(ReloadingProperty p) {
      props = p;
   }
   
   public String getDesktopProperty(String a, String b) {
      return props.getProperty(a, b);
   }
   public String getDesktopProperty(String a) {
      return props.getProperty(a, null);
   }
   
  //
  // Support User per Section
  //   Keys are: userid, pw, state, country, projects, 
  //             first, last, company, email
  //
  // caller should call saveUserInfo
  //
  // 11/10/04 - Add DB support.
  
   Enumeration getUserNames() {
      Enumeration ret = null;
      
      String useDB = getDesktopProperty("edodc.usefrontPageDB", "true");
      if (useDB.equalsIgnoreCase("true")) {
         DBConnection dbconn = null;
         Connection conn = null;
         PreparedStatement pstmt=null;
         ResultSet rs=null;	
         StringBuffer sql = null;
         
         try {
            dbconn = DBSource.getDBConnection("AMT");
            conn = dbconn.getConnection();
            sql = new StringBuffer("select userid from edesign.frontpage");
            pstmt=conn.prepareStatement(sql.toString());
            rs = dbconn.executeQuery(pstmt);
            Vector v = new Vector();
            while(rs.next()) {
               v.addElement(rs.getString(1));
            }
            ret = v.elements();
         } catch (Exception ee) {
            DebugPrint.printlnd(DebugPrint.ERROR, "Error with DB Processing");
            DebugPrint.printlnd(DebugPrint.ERROR, ee);
            dbconn.destroyConnection(conn);
            conn=null;
            pstmt=null;
         } finally {
            dbconn.returnConnection(conn);
            if (pstmt != null) try {
               pstmt.close();
            } catch(Exception ee) {}
         }
         
      } else {
         synchronized(frontPageSync) {
            if (frontPageCO != null) {
               ret = frontPageCO.getSectionNames();
            }
         }
      }
      return ret;
   }
   
   void addUserInfo(ConfigSection co) {
      addUserInfo(co, false);
   }
   
   void addUserInfo(ConfigSection co, boolean forceDB) {
   
      String userid = co.getProperty("userid");
      if (userid == null) return;
      
      String useDB = getDesktopProperty("edodc.usefrontPageDB", "true");
      if (forceDB || useDB.equalsIgnoreCase("true")) {
         DBConnection dbconn = null;
         Connection conn = null;
         PreparedStatement pstmt=null;
         ResultSet rs=null;	
         StringBuffer sql = null;
         
         try {
            dbconn = DBSource.getDBConnection("AMT");
            conn = dbconn.getConnection();
            sql = new StringBuffer("delete from edesign.frontpage ");
            sql.append("where ucase(userid)=?");
            pstmt=conn.prepareStatement(sql.toString());
            pstmt.setString(1, userid.toUpperCase());
            dbconn.executeUpdate(pstmt);
            pstmt.close();
            
            String  pw, email, password, company, country, state,
                   projects, creator, first, last;
            long expires;
      
            pw       = co.getProperty("pw", "");
            email    = co.getProperty("email", "");
            company  = co.getProperty("company", "");
            country  = co.getProperty("country", "");
            state    = co.getProperty("state", "");
            projects = co.getProperty("projects", "");
            last     = co.getProperty("last", "");
            first    = co.getProperty("first", "");
            expires  = co.getLongProperty("expires", 0);
            creator  = co.getProperty("creator", "unspecified");
      
            String canlogin   = co.getProperty("loginallowed", "true");
            String isadmin   = co.getProperty("admin", "false");
            String isstatus  = co.getProperty("status", "false");
            String isreport  = co.getProperty("report", "false");
            String isrepdbox = co.getProperty("reportdbox", "false");
            
            sql = new StringBuffer("insert into edesign.frontpage ");
            sql.append("(userid,pw,email,company,country,state,projects,last,");
            sql.append("first,expires,creator,canlogin,admin,status,report,");
            sql.append("reportdbox) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            
            pstmt=conn.prepareStatement(sql.toString());
            
            int i=1;
            pstmt.setString(i++, userid);
            pstmt.setString(i++, pw);
            pstmt.setString(i++, email);
            pstmt.setString(i++, company);
            pstmt.setString(i++, country);
            pstmt.setString(i++, state);
            pstmt.setString(i++, projects);
            pstmt.setString(i++, last);
            pstmt.setString(i++, first);
            pstmt.setLong  (i++, expires);
            pstmt.setString(i++, creator);
            pstmt.setString(i++, canlogin);
            pstmt.setString(i++, isadmin);
            pstmt.setString(i++, isstatus);
            pstmt.setString(i++, isreport);
            pstmt.setString(i++, isrepdbox);
            
            dbconn.executeUpdate(pstmt);
            
         } catch (Exception ee) {
            DebugPrint.printlnd(DebugPrint.ERROR, "Error with DB Processing");
            DebugPrint.printlnd(DebugPrint.ERROR, ee);
            dbconn.destroyConnection(conn);
            conn=null;
            pstmt=null;
         } finally {
            dbconn.returnConnection(conn);
            if (pstmt != null) try {
               pstmt.close();
            } catch(Exception ee) {}
         }
      } else {
         synchronized(frontPageSync) {
            if (frontPageCO != null) {
               frontPageCO.removeSection(co);
               frontPageCO.addSection(co);
            }
         }
      }
   }
   
  // Pass in "*" and get them all
   ConfigSection getUserInfo(String u) {
      ConfigSection ret = null;
      
      String useDB = getDesktopProperty("edodc.usefrontPageDB", "true");
      if (useDB.equalsIgnoreCase("true")) {
         DBConnection dbconn = null;
         Connection conn = null;
         PreparedStatement pstmt=null;
         ResultSet rs=null;	
         StringBuffer sql = null;
         
         try {
            dbconn = DBSource.getDBConnection("AMT");
            conn = dbconn.getConnection();
            
            sql = new StringBuffer("select ");
            sql.append("userid,pw,email,company,country,state,projects,last,");
            sql.append("first,expires,creator,canlogin,admin,status,report,");
            sql.append("reportdbox from edesign.frontpage ");
            
            if (!u.equals("*")) sql.append("where ucase(userid) = ?");
            
            pstmt=conn.prepareStatement(sql.toString());
            
            if (!u.equals("*")) pstmt.setString(1, u.toUpperCase());
            
            rs = dbconn.executeQuery(pstmt);
            
            while (rs.next()) {
            
               int i=1;
               String userid = rs.getString(i++);
               
               ConfigSection rets = new ConfigSection(userid);
               if (u.equals("*")) {
                  if (ret == null) ret = new ConfigSection("frontpage");
                  ret.addSection(rets);
               } else {
                  ret = rets;
               }
               
               rets.setProperty("userid",       userid);
               rets.setProperty("pw",           rs.getString(i++));
               rets.setProperty("email",        rs.getString(i++));
               rets.setProperty("company",      rs.getString(i++));
               rets.setProperty("country",      rs.getString(i++));
               rets.setProperty("state",        rs.getString(i++));
               rets.setProperty("projects",     rs.getString(i++));
               rets.setProperty("last",         rs.getString(i++));
               rets.setProperty("first",        rs.getString(i++));
               rets.setProperty("expires",   ""+rs.getLong(i++));
               rets.setProperty("creator",      rs.getString(i++));
               rets.setProperty("loginallowed", rs.getString(i++));
               rets.setProperty("admin",        rs.getString(i++));
               rets.setProperty("status",       rs.getString(i++));
               rets.setProperty("report",       rs.getString(i++));
               rets.setProperty("reportdbox",   rs.getString(i++));
            }
            
         } catch (Exception ee) {
            DebugPrint.printlnd(DebugPrint.ERROR, "Error with DB Processing");
            DebugPrint.printlnd(DebugPrint.ERROR, ee);
            dbconn.destroyConnection(conn);
            conn=null;
            pstmt=null;
         } finally {
            dbconn.returnConnection(conn);
            if (pstmt != null) try {
               pstmt.close();
            } catch(Exception ee) {}
         }
         
      } else {
      
         if (u != null) {      
            synchronized(frontPageSync) {
               reloadUserInfo();
               if (frontPageCO != null) {
                  if (u.equals("*")) {
                    // TODO: fill in logic to create ConfigSection out of fpCO
                     return null;
                  }
                  
                  try {
                     
                     Vector v = frontPageCO.getSection(u);
                     ret = (ConfigSection)v.elementAt(0);
                     if (ret == null) {
                        DebugPrint.printlnd(DebugPrint.INFO4, 
                                            "User[" + u + 
                                            "] not in frontPageUserPW: " + 
                                            lastFrontPageFile);
                     } else {
                        DebugPrint.printlnd(DebugPrint.INFO4, 
                                            "User[" + u + "] (" + v.size() + 
                                            ") found in frontPageUserPW: " + 
                                            lastFrontPageFile);
                     }
                  } catch(Exception ee) {
                     DebugPrint.printlnd(DebugPrint.INFO4, 
                                         "User[" + u + 
                                         "] not in frontPageUserPW: " + 
                                         lastFrontPageFile);
                  }
               }
            }
         }
      }
      return ret;
   }
   
   
  // this is for the benefit of DB ... when a co changes we have to know it
  //   changed (we either call addUserInfo or this meth). For flatfile, the
  //   saveUserInfo method would handle things ... we skip that for DB
   void userInfoChanged(ConfigSection co) {
      addUserInfo(co);
   }
   
  // caller should call saveUserInfo
   boolean removeUserInfo(String user) {
      boolean ret = false;
      
      String useDB = getDesktopProperty("edodc.usefrontPageDB", "true");
      if (useDB.equalsIgnoreCase("true")) {
         DBConnection dbconn = null;
         Connection conn = null;
         PreparedStatement pstmt=null;
         ResultSet rs=null;	
         StringBuffer sql = null;
         
         try {
            dbconn = DBSource.getDBConnection("AMT");
            conn = dbconn.getConnection();
            sql = new StringBuffer("delete from edesign.frontpage ");
            sql.append("where ucase(userid)=?");
            pstmt=conn.prepareStatement(sql.toString());
            pstmt.setString(1, user.toUpperCase());
            ret = dbconn.executeUpdate(pstmt) >= 1;
            
         } catch (Exception ee) {
            DebugPrint.printlnd(DebugPrint.ERROR, "Error with DB Processing");
            DebugPrint.printlnd(DebugPrint.ERROR, ee);
            dbconn.destroyConnection(conn);
            conn=null;
            pstmt=null;
         } finally {
            dbconn.returnConnection(conn);
            if (pstmt != null) try {
               pstmt.close();
            } catch(Exception ee) {}
         }
         
      } else {
         synchronized(frontPageSync) {
            if (frontPageCO != null) {
               ret = frontPageCO.removeSection(user);
            }
         }
      }
      return ret;
   }
   
   void reloadUserInfo() {
   
      String useDB = getDesktopProperty("edodc.usefrontPageDB", "true");
      if (useDB.equalsIgnoreCase("true")) {
         return;
      }

      String fname = getDesktopProperty("edodc.frontPageUserPW");
      synchronized(frontPageSync) {
         if (fname != null) {
            try {
               File f = new File(fname);
               if (f.exists() && 
                   ((fname != lastFrontPageFile && 
                     !fname.equals(lastFrontPageFile))) ||
                   f.lastModified() > frontPageFileTime) {
                  
                  frontPageCO = new ConfigFile(fname);
                  lastFrontPageFile = fname;
                  frontPageFileTime = f.lastModified();
                  
                  DebugPrint.printlnd(DebugPrint.INFO, 
                                      "(Re)loading frontpage file " + fname);
            
                 // Convert all passwords to MD5 hashes
                 // Turn this off
                 /*
                  if (!frontPageCO.getBoolProperty("MD5", false)) {
                     frontPageCO.setBoolProperty("MD5", true);
                     
                     Enumeration enum = frontPageCO.getSectionNames();
                     while(enum.hasMoreElements()) {
                        String sect = (String)enum.nextElement();
                        Vector v = frontPageCO.getSection(sect);
                        ConfigSection cs = (ConfigSection)v.elementAt(0);
                        String pw = cs.getProperty("PW");
                        if (pw != null && pw.trim().length() != 0) {
                           cs.setProperty("PW", makehash(pw));
                           addUserInfo(cs);
                        }
                     }
                     saveUserInfo();
                  }
                 */
               }
            } catch(Exception ee) {
               DebugPrint.printlnd(DebugPrint.ERROR, 
                                   "Error getting frontPageUserPW = " + fname);
            }
         }
      }
   }
   
   void saveUserInfo() {
      
      String useDB = getDesktopProperty("edodc.usefrontPageDB", "true");
      if (useDB.equalsIgnoreCase("true")) {
      
        // HACK!! to generate flat file from DB2
         try {
            if (getDesktopProperty("edodc.frontpageToFlat", 
                                   "false").equalsIgnoreCase("true")) {
                                   
               String outfile = getDesktopProperty("edodc.frontpageFileSave", 
                                                   "/tmp/frontpage.flat");
               DebugPrint.printlnd(DebugPrint.INFO,
                                   "Converting frontpage DB2 to file: " +
                                   outfile);
               
              // get all entries from DB
               ConfigSection co = getUserInfo("*");
               
               File f = new File(outfile);
               FileOutputStream os = new FileOutputStream(f);
               co.save(os);
               os.close();
            }
         } catch(Exception ee) {
            DebugPrint.printlnd(DebugPrint.ERROR, 
                                "Error making frontpage flatfile");
            DebugPrint.printlnd(DebugPrint.ERROR, ee);
         }
      
         return;
      } 
      
      synchronized(frontPageSync) {
         if (frontPageCO != null) {
            try {
               File f = new File(lastFrontPageFile);
               FileOutputStream os = new FileOutputStream(f);
               frontPageCO.save(os);
               os.close();
               frontPageFileTime = f.lastModified();
               
              // HACK!! to get flat file into DB2
               if (getDesktopProperty("edodc.frontpageToDB2", 
                                      "false").equalsIgnoreCase("true")) {
                  DebugPrint.printlnd(DebugPrint.INFO,
                                      "Converting frontpage to DB2");
                  Enumeration enum = getUserNames();
                  while(enum != null && enum.hasMoreElements()) {
                     String userid = (String)enum.nextElement();
                     ConfigSection co = getUserInfo(userid);
                     System.out.println("  " + userid);
                     
                    // true means force to save with DB2
                     addUserInfo(co, true);
                  }
               }
               
            } catch(Exception ee) {
               DebugPrint.printlnd(DebugPrint.ERROR, 
                                   "Error saving frontpageFile " + 
                                   lastFrontPageFile);
               DebugPrint.println(DebugPrint.ERROR, ee);
            }
         }
      }
   }
   
   public boolean handleFrontPage(HttpServletRequest req,
                                  HttpServletResponse res,
                                  String command, Properties prop,
                                  String head) throws IOException {
                                  
      String allowFrontPage = getDesktopProperty("edodc.allowFrontPage");
      if (allowFrontPage != null && allowFrontPage.equalsIgnoreCase("true")) {
      
         String thisToken = "-invalid-";
      
      
        // thisurl will already HAVE the servletcontextpath on it
         
         String thisurl = req.getRequestURI();
         
        // 10/13/04
        // When I pulled phead/head to top of doGet ... decided this was wrong
        //
        //if (thisurl.indexOf("://") < 0) {
        //    thisurl = phead + thisurl;
        // }
         
         String qs = req.getQueryString();
         if (qs != null && qs.length() > 0) {
            thisurl += "?" + qs;
         }
         
         if        (command.equalsIgnoreCase("loginpage")) {
         
            HttpSession ses = req.getSession(true);
            ses.invalidate();
            
            String errormsg = prop.getProperty("errormsg");
            String b = FE.loginInfo(errormsg, null, res);
            PrintWriter out = res.getWriter();
            out.println(b);
            out.close();
            return true;
            
         } else if (command.equalsIgnoreCase("loginpage2")) {
            
            String errstr   = null;
            String userid   = (String)prop.get("userid");
            String inpw     = (String)prop.get("password");
            String sftp     = (String)prop.get("sftp");
            String hashpw     = inpw != null?DesktopServlet.makehash(inpw):null;
            String hashpw_old = inpw != null?DesktopServlet.makehash_old(inpw):null;
            String redirect = (String)prop.get("redirect");
            String setpw = null;
            ConfigSection tokenco = null;
            ConfigSection co = null;
            
            if (inpw != null && hashpw != null && 
                hashpw.trim().length() != 0) {
               
              // Strip off leading !. This is leftover from when we 
              //  tried authenticating with CC or UP first
               if (userid.startsWith("!")) {
                  userid = userid.substring(1);
               }
               
               co = getUserInfo(userid);
               
               if (co != null) {
                  setpw = co.getProperty("pw");
                  if (setpw != null && setpw.trim().length() == 0) {
                     setpw = null;
                  }
                  if (hashpw == null || hashpw.trim().length() == 0  || 
                  
                      (setpw != null && 
                       (!hashpw.equalsIgnoreCase(setpw) && 
                        !hashpw_old.equalsIgnoreCase(setpw)))) {
                        
                     co = null;
                  }
               }
            }
            
           // Try to validate this guy via CCIR/IIP. The User has to be 
           //  defined locally, but does not have a PW set for this path 
           //  to be taken
            if (co != null && setpw == null) {
                                  
               String doIIP = getDesktopProperty("edodc.loginpageDoIIP", "false");
               String doCC  = getDesktopProperty(
                                     "edodc.loginpageDoCustomerConnect", "false");
                                  
               if (doIIP.equalsIgnoreCase("true") && 
                   userid.lastIndexOf(".ibm.com") == (userid.length()-8)) {
                   
                  if (oem.edge.ed.odc.tunnel.common.Misc.getConnectInfoQP(userid, 
                                                                          inpw)) {
                     DebugPrint.printlnd(DebugPrint.INFO2,
                                         "Authenticated to IIP as " + userid);
                     tokenco = getUserInfo(userid);
                  }
               }
                                                                       
               if (tokenco == null && doCC.equalsIgnoreCase("true")) {
               
                 // pass null for res so its ignored
                 // We get back a token which is wrapped with out local cipher
                  String connectinfo = 
                     DesktopServlet.authenticateAndMakeToken("60", null,
                                                             userid, inpw, 
                                                             sftp, null);
                        
                  if (connectinfo != null) {
                     DebugPrint.printlnd(DebugPrint.INFO2, 
                                         "Authenticated to Customer Connect as" +
                                         userid);
                     
                     try { 
                        ODCipherData cd = 
                           DesktopServlet.getLocalCipher().decode(connectinfo);
                        thisToken = cd.getString();
                        ConfigObject cot = new ConfigObject();
                        cot.fromString(thisToken);
                        if (cd.isCurrent()) {
                           tokenco = DesktopServlet.getUserInfoFromToken(cot);
                        }
                     } catch(DecodeException de) {
                        DebugPrint.println(DebugPrint.INFO2, 
                                           "Token decode problem");
                     } catch(Throwable tt) {
                        DebugPrint.println(DebugPrint.DEBUG, tt);
                     }
                  } else {
                     DebugPrint.println(DebugPrint.INFO2, 
                                        "null back from authenticate");
                  }
               }
            } 
            
            String loginerr = "Invalid Userid/PW!";
            if (co != null) {
               if ((setpw == null && tokenco == null) ) {
                  loginerr = "External Authentication Failed for ID " + userid;
                  co = null;
               }
            }
               
            if (co == null) {
            
               HttpSession ses = req.getSession(true);
               ses.invalidate();
               
               String b = FE.loginInfo(loginerr, redirect, res);
               PrintWriter out = res.getWriter();
               out.println(b);
               out.close();
               
               return true;      
            }
            
            String loginallowed = co.getProperty("loginallowed");
            if (loginallowed != null && 
                loginallowed.equalsIgnoreCase("false")) {
               HttpSession ses = req.getSession(true);
               ses.invalidate();
               String b = FE.loginInfo("Login disabled! Call your FSE",
                                       null, res);
               PrintWriter out = res.getWriter();
               out.println(b);
               out.close();
               return true;
            }
            
            long expires = co.getLongProperty("expires", 0);
            if (expires != 0) {
               long now = System.currentTimeMillis();
               if (expires < now) {
                  HttpSession ses = req.getSession(true);
                  ses.invalidate();
                  String b = FE.loginInfo("Login ID expired! Call your FSE", 
                                          null, res);
                  PrintWriter out = res.getWriter();
                  out.println(b);
                  out.close();
                  return true;
               }
            }
            
            HttpSession ses = req.getSession(true);
            if ( ses.isNew() == false ) {
               ses.invalidate();
               ses = req.getSession(true); // jsdk2.0
            }
            
            String sid = ses.getId();
            
            ses.setAttribute("did", sid);
            ses.setAttribute("userinfo", userid);
            if (tokenco != null) {
               ses.setAttribute("tokenco", tokenco);
            }
            
           // Control this via the session-timeout value in web.xml
           //ses.setMaxInactiveInterval(15 * 60);
           //
            
            if (redirect != null && redirect.length() > 0) {
               redirect = DesktopServlet.fixForRedirect(redirect);
               String encoderedirect = res.encodeRedirectURL(redirect);
               res.sendRedirect(encoderedirect);
            } else {
               String b = FE.actionPage(co, tokenco != null, null, res);
               PrintWriter out = res.getWriter();
               out.println(b);
               out.close();
            }
            return true;      
            
         } else if (command.equalsIgnoreCase("frontpage")) {
            HttpSession ses = req.getSession(true);
            if ( ses.isNew() == true) {
            
               ses.invalidate();
               
               String b = FE.loginInfo("No active session, or session expired. Please sign in.", thisurl, res);
               PrintWriter out = res.getWriter();
               out.println(b);
               out.close();
               return true;
            }
            
            
            String sid = ses.getId();
            String did       = (String)ses.getAttribute("did");
            String use       = (String)ses.getAttribute("userinfo");
            
           // Don't use tokenco info to drive access issues
            ConfigSection tokenco = (ConfigSection)ses.getAttribute("tokenco");
            ConfigSection co = null;
            
            co = getUserInfo(use);
            
            if (did == null || sid == null || !did.equals(sid) || 
                co == null) {
                
               String b = FE.loginInfo("Session Broken!", thisurl, res);
               
               PrintWriter out = res.getWriter();
               out.println(b);
               out.close();
               return true;
            }
            
            String b = FE.actionPage(co, tokenco != null, null, res);
            PrintWriter out = res.getWriter();
            out.println(b);
            out.close();
            
            return true;
            
         } else if (command.equalsIgnoreCase("frontpageadmin")) {
            HttpSession ses = req.getSession(true);
            if ( ses.isNew() == true) {
               ses.invalidate();
               
               String b = FE.loginInfo("No active session, or session expired. Please sign in.", thisurl, res);
               PrintWriter out = res.getWriter();
               out.println(b);
               out.close();
               return true;
            }
            
            
            String sid = ses.getId();
            String did       = (String)ses.getAttribute("did");
            String use       = (String)ses.getAttribute("userinfo");
            
           // Don't use tokenco info to drive access issues
            ConfigSection tokenco = (ConfigSection)ses.getAttribute("tokenco");
            ConfigSection co = null;
            
            co = getUserInfo(use);
            
            if (did == null || sid == null || !did.equals(sid) || 
                co == null) {
            
               String b = FE.loginInfo("Session Broken!", thisurl, res);
               PrintWriter out = res.getWriter();
               out.println(b);
               out.close();
               return true;
            }
                        
            String op     = req.getParameter("op");
            String baseop = null;
            int    opv    = -1;
            if (op != null && op.length() > 1) {
               baseop = op.substring(0, op.length()-1);
               try {
                  String tmp = op.substring(op.length()-1);
                  opv = Integer.parseInt(tmp); 
               } catch(Exception ee) {}
            }
            if (opv == -1 || !(baseop.equals("pw")         || 
                               baseop.equals("addid")      ||
                               baseop.equals("editid")     ||
                               baseop.equals("multicmd")   ||
                               baseop.equals("status")     ||
                               baseop.equals("report")     ||
                               baseop.equals("reportdbox") ||
                               baseop.equals("reportdboxnew") ||
                               baseop.equals("removeid"))) {
            
               String b = FE.loginInfo("Invalid operation op=" + op + "!", 
                                       thisurl, res);
               PrintWriter out = res.getWriter();
               out.println(b);
               out.close();
               
               return true;
            }
            
            boolean allowed   = false;
            boolean isadmin   = co.getProperty("admin", 
                                             "false").equalsIgnoreCase("true");
            boolean isstatus  = co.getProperty("status", 
                                             "false").equalsIgnoreCase("true");
            boolean isreport  = co.getProperty("report", 
                                             "false").equalsIgnoreCase("true");
            boolean isrepdbox = co.getProperty("reportdbox", 
                                             "false").equalsIgnoreCase("true");
            if (baseop.equals("pw")) { 
               allowed = true;
            } else if (((baseop.equals("addid")      ||
                         baseop.equals("editid")     ||
                         baseop.equals("multicmd")   ||
                         baseop.equals("removeid"))  &&
                        isadmin)) {
               allowed = true;
            } else if (baseop.equals("status") && (isadmin || isstatus)) {
               allowed = true;
            } else if (baseop.equals("report") && (isadmin || isreport)) {
               allowed = true;
            } else if (baseop.equals("reportdbox") && (isadmin || isrepdbox)) {
               allowed = true;
            } else if (baseop.equals("reportdboxnew") && (isadmin || isrepdbox)){
               allowed = true;
            }                
            
            if (!allowed) {
               String b = FE.loginInfo("You are not ALLOWED!", thisurl, res);
               PrintWriter out = res.getWriter();
               out.println(b);
               out.close();
               
               return true;
            }
                        

            String b= DesktopConstants.doctypehead_req + 
               DesktopConstants.metamatter_req +
               "<title>Decode</title></head><body><a href=\""  + 
               
               res.encodeURL(DesktopServlet.servletcontextpath +
                             "/servlet/oem/edge/ed/odc/desktop/frontpage") + 
               "\">Not Yet Implemented!</a></body></html>";
               
            if (baseop.equals("pw")) {
            
               if (tokenco != null || use.toUpperCase().indexOf("DEMO") >= 0) {
                  b = FE.loginInfo("You are not ALLOWED!", null, res);
                  PrintWriter out = res.getWriter();
                  out.println(b);
                  out.close();
                  return true;
               }
               
               switch(opv) {
                  case 1:
                     b=FE.changePassword(null, res);
                     break;
                  case 2:
                     String curpass  = req.getParameter("curpass"); 
                     String pass1    = req.getParameter("npass1"); 
                     String pass2    = req.getParameter("npass2"); 
                     
                     if (curpass == null || pass1 == null || pass2 == null) {
                        b=FE.changePassword("Fill in current and new password fields!", res);
                     } else {
                        curpass = curpass.trim();
                        pass1   = pass1.trim();
                        pass2   = pass2.trim();
                        if (curpass.length() == 0 || 
                            pass1.length()   == 0 ||
                            pass2.length()   == 0 ) {
                           b=FE.changePassword(
                              "Fill in current and new password fields!", res);
                        } else {
                           String lpw = co.getProperty("pw", "");
                           if (DesktopServlet.makehash(curpass).equalsIgnoreCase(lpw) ||
                               DesktopServlet.makehash_old(curpass).equalsIgnoreCase(lpw)) {
                              if (!pass1.equals(pass2)) {
                                 b=FE.changePassword(
                                    "Both new passwords must match!", res);
                              } else if (pass1.length() < 6) {
                                 b=FE.changePassword(
                                    "New password must be >= 6 characters!", 
                                    res);
                              } else {
                                 co.setProperty("pw", 
                                                DesktopServlet.makehash(pass1));
                                 userInfoChanged(co);
                                 saveUserInfo();
                                 b=FE.actionPage(co, tokenco != null, 
                                                 "Password change complete", 
                                                 res);
                              }
                           } else {
                              b=FE.changePassword(
                                 "Need your *CORRECT* current pw!", res);
                              
                           }
                        }
                     }
                     break;
                  default:
                     b = FE.loginInfo("Invalid operation op="+op+"!", null, 
                                      res);
                     break;
               }
            } else if (baseop.equals("addid")) {
               switch(opv) {
                  case 1:
                     b=FE.addId(null, res);
                     break;
                  case 2:
                     String userid   = req.getParameter("userid"); 
                     String copyid   = req.getParameter("copyid"); 
                     
                     if (userid == null) {
                        b=FE.addId("Enter a UserID!", res);
                     } else {
                        userid = userid.trim();
                        if (userid.length() == 0) {
                           b=FE.addId("Enter a non-null Userid!", res);
                        } else {
                           co = getUserInfo(userid);
                           if (co != null) {
                              b=FE.addId("User <b>" + userid + 
                                         "</b> already exists", res);
                           } else {
                              String msg = null;
                              ConfigSection csn = new ConfigSection(userid);
                              if (copyid != null && 
                                  copyid.trim().length() != 0) {
                                  
                                 copyid = copyid.trim();
                                 ConfigSection cs = 
                                    getUserInfo(copyid);
                                 if (cs == null ) {
                                    msg = "Id to copy <b>" + copyid + 
                                       "</b> not found!";
                                 } else {
                                    Enumeration e = cs.getPropertyNames();
                                    while(e.hasMoreElements()) {
                                       String str = (String)e.nextElement();
                                       String v   = cs.getProperty(str);
                                       if (v != null && 
                                           !str.equalsIgnoreCase("pw")) {
                                          csn.setProperty(str, v);
                                       }
                                    }
                                 }
                              }
                              csn.setProperty("userid",  userid);
                              csn.setProperty("creator", use);
                              addUserInfo(csn);
                              b=FE.editIdInfo(csn, msg, res);
                              saveUserInfo();
                           }                              
                        }
                     }
                     break;
                  default:
                     b = FE.loginInfo("Invalid operation op="+op+"!", 
                                      null, res);
                     break;
               }
            } else if (baseop.equals("multicmd")) {
               switch(opv) {
                  case 1:
                     b=FE.multiCmd(SearchEtc.sortVector(getUserNames(), true),
                                   null, res);
                     break;
                  case 2: {
                  
                     String selcmd   = req.getParameter("selcmd"); 
                     b = null;
                     if (selcmd == null) {
                        b=FE.multiCmd(SearchEtc.sortVector(getUserNames(), true),
                                      "Select a command!", res);
                     } else {
                        Vector userlist = new Vector();
                        String ss[] = req.getParameterValues("userids");
                        if (ss != null) {
                           for (int i=0; i < ss.length; i++) {
                              userlist.addElement(ss[i]);
                           }
                        }
                        
                        if (userlist.size() == 0) {
                           b=FE.multiCmd(
                              SearchEtc.sortVector(getUserNames(), true),
                              "Select at least 1 user!", res);
                        } else {
                        
                           selcmd = selcmd.toUpperCase();
                           Enumeration enum = userlist.elements();
                           StringBuffer telluser = new StringBuffer();
                           
                           if        (selcmd.equals("3")) {
                              while(enum.hasMoreElements()) {
                                 String s = (String)enum.nextElement();
                                 telluser.append("<br />User <b>" + s + "</b> ");
                                 if (!removeUserInfo(s)) {
                                    telluser.append("non-existant!\n");
                                 } else {
                                    saveUserInfo();
                                    telluser.append("removed\n");
                                 }
                              }
                           } else if (selcmd.equals("4")) {
                           
                              while(enum.hasMoreElements()) {
                                 String s = (String)enum.nextElement();
                                 telluser.append("<br />User <b>" + s + "</b> ");
                                 ConfigSection lcs = getUserInfo(s);
                                 if (lcs == null) {
                                    telluser.append("non-existant!\n");
                                    continue;
                                 }
                                 
                                 try {
                                    String Month, Day, Year, Hour, Min;
                                    long expiresTime;
                                    Month    = req.getParameter("Month");
                                    Day      = req.getParameter("Day");
                                    Year     = req.getParameter("Year");
                                    Hour     = req.getParameter("Hour");
                                    Min      = req.getParameter("Min");
                                    int y   = Integer.parseInt(Year);
                                    int d   = Integer.parseInt(Day);
                                    int mon = Integer.parseInt(Month);
                                    int h   = Integer.parseInt(Hour);
                                    int min = Integer.parseInt(Min);
                                    
                                    Calendar sc = Calendar.getInstance();
                                    sc.set(Calendar.YEAR,         y);
                                    sc.set(Calendar.MONTH,        mon);
                                    sc.set(Calendar.DAY_OF_MONTH, d);
                                    sc.set(Calendar.HOUR_OF_DAY,  h);
                                    sc.set(Calendar.MINUTE,       min);
                                    sc.set(Calendar.SECOND,       0);
                                    
                                    expiresTime = sc.getTime().getTime();
                                    lcs.setLongProperty("expires", expiresTime);
                                    userInfoChanged(lcs);
                                    telluser.append("expiration updated\n");
                                 } catch(Exception ee) {
                                    telluser.append("error mod'ing expiration\n");
                                 }
                              }
                              
                           } else if (selcmd.equals("1")) {
                              while(enum.hasMoreElements()) {
                                 String s = (String)enum.nextElement();
                                 telluser.append("<br />User <b>" + s + "</b> ");
                                 ConfigSection lcs = getUserInfo(s);
                                 if (lcs == null) {
                                    telluser.append("non-existant!\n");
                                    continue;
                                 }
                                 
                                 lcs.setProperty("loginallowed", "true");
                                 userInfoChanged(lcs);

                                 telluser.append("login enabled\n");
                              }
                           } else if (selcmd.equals("2")) {
                              while(enum.hasMoreElements()) {
                                 String s = (String)enum.nextElement();
                                 telluser.append("<br />User <b>" + s + "</b> ");
                                 ConfigSection lcs = getUserInfo(s);
                                 if (lcs == null) {
                                    telluser.append("non-existant!\n");
                                    continue;
                                 }
                                 
                                 lcs.setProperty("loginallowed", "false");
                                 userInfoChanged(lcs); 
                                 telluser.append("login disabled\n");
                              }
                           } else {
                              b=FE.multiCmd(
                                 SearchEtc.sortVector(getUserNames(), true),
                                 "Invalid command!", res);
                           }
                           if (b == null) {
                              b=FE.actionPage(co, tokenco != null, 
                                              telluser.toString(), 
                                              res);
                           }
                           saveUserInfo();
                        }
                     }
                     break;
                  }
                  default:
                     b = FE.loginInfo("Invalid operation op="+op+"!", null, 
                                      res);
                     break;
               }
            } else if (baseop.equals("editid")) {
               switch(opv) {
                  case 1:
                     b=FE.editId(SearchEtc.sortVector(getUserNames(), true),
                                 null, res);
                     break;
                  case 2: {
                     String userid   = req.getParameter("userid"); 
                     
                     if (userid == null) {
                        b=FE.editId(SearchEtc.sortVector(getUserNames(), true),
                                    "Enter a UserID!", res);
                     } else {
                        userid = userid.trim();
                        if (userid.length() == 0) {
                           b=FE.editId(SearchEtc.sortVector(getUserNames(),
                                                            true),
                                       "Enter a non-null Userid!", res);
                        } else {
                           co = getUserInfo(userid);
                           if (co == null) {
                              b=FE.editId(
                                 SearchEtc.sortVector(getUserNames(), true),
                                          "User <b>" + userid + 
                                         "</b> does NOT exist", res);
                           } else {
                              b=FE.editIdInfo(co, null, res);
                           }                              
                        }
                     }
                     break;
                  }
                     
                  case 3: {
                     String user, email, password, company, country, state, 
                        projects, first, last, admin, canlogin, expires,
                        report, reportdbox, status;
                     
                     String Month, Day, Year, Hour, Min;
                     ConfigSection cs = null;
                     int reqnum = 0;
                     
                     user     = req.getParameter("userid");
                     if (user == null || (user=user.trim()).length() == 0) {
                        b=FE.editId(SearchEtc.sortVector(getUserNames(), true),
                           "Huh? Your Userid to edit not found. Select again",
                                    res);
                     } else {
                        cs = getUserInfo(user);
                        if (cs == null) {
                           b=FE.editId(SearchEtc.sortVector(getUserNames(),
                                                            true),
                              "Userid <b>" + user + "</b> is not found?", res);
                        } else {
                           reqnum++;
                           email      = req.getParameter("email");
                           password   = req.getParameter("password");
                           company    = req.getParameter("company");
                           country    = req.getParameter("country");
                           state      = req.getParameter("state");
                           projects   = req.getParameter("projects");
                           last       = req.getParameter("last");
                           first      = req.getParameter("first");
                           admin      = req.getParameter("admin");
                           canlogin   = req.getParameter("canlogin");
                           report     = req.getParameter("report");
                           reportdbox = req.getParameter("reportdbox");
                           status     = req.getParameter("status");
                           
                           expires  = req.getParameter("expires");
                           Month    = req.getParameter("Month");
                           Day      = req.getParameter("Day");
                           Year     = req.getParameter("Year");
                           Hour     = req.getParameter("Hour");
                           Min      = req.getParameter("Min");
                           
                           if (email == null ||
                               (email = email.trim()).length() == 0) {
                              email = null;
                           }
                           
                           boolean dopassword = true;
                           if (password == null ||
                               (password = password.trim()).length() == 0) {
                              password = null;
                           } else if (password.equals("[enter password here]")){
                              dopassword = false;
                           } else {
                              password = DesktopServlet.makehash(password);
                           }
                           if (company == null ||
                               (company = company.trim()).length() == 0) {
                              company = null;
                           } else {
                              reqnum++;
                           }
                           if (country == null ||
                               (country = country.trim()).length() == 0) {
                              country = null;
                           } else {
                              reqnum++;
                           }
                           if (state == null ||
                               (state = state.trim()).length() == 0) {
                              state = null;
                           }
                           if (projects == null ||
                               (projects = projects.trim()).length() == 0) {
                              projects = null;
                           }
                           if (last == null ||
                               (last = last.trim()).length() == 0) {
                              last = null;
                           }
                           if (first == null ||
                               (first = first.trim()).length() == 0) {
                              first = null;
                           }
                           
                           if (reqnum != 3) {
                              b=FE.editIdInfo(cs, 
                                        "Please fill in ALL required fields", 
                                              res);

                           } else {
                           
                              long expiresTime = 0;
                              if (expires != null && 
                                  expires.equalsIgnoreCase("on")) {
                                 
                                 try {
                                    int y   = Integer.parseInt(Year);
                                    int d   = Integer.parseInt(Day);
                                    int mon = Integer.parseInt(Month);
                                    int h   = Integer.parseInt(Hour);
                                    int min = Integer.parseInt(Min);
                                    
                                    Calendar sc = Calendar.getInstance();
                                    sc.set(Calendar.YEAR,         y);
                                    sc.set(Calendar.MONTH,        mon);
                                    sc.set(Calendar.DAY_OF_MONTH, d);
                                    sc.set(Calendar.HOUR_OF_DAY,  h);
                                    sc.set(Calendar.MINUTE,       min);
                                    sc.set(Calendar.SECOND,       0);
                                    
                                    expiresTime = sc.getTime().getTime();
                                 } catch(Exception ee) {
                                    DebugPrint.println(DebugPrint.ERROR, ee);
                                 }
                              }
                              cs.setLongProperty("expires", expiresTime);
                           
                              if (user != null) {
                                 cs.setProperty("userid", user);
                              } else {
                                 cs.removeProperty("USERID");
                              }
                              if (email != null) {
                                 cs.setProperty("email", email);
                              } else {
                                 cs.removeProperty("EMAIL");
                              }
                              if (dopassword) {
                                 if (password != null) {
                                    cs.setProperty("pw", password);
                                 } else {
                                    cs.removeProperty("PW");
                                 }
                              }
                              if (company != null) {
                                 cs.setProperty("company", company);
                              } else {
                                 cs.removeProperty("COMPANY");
                              }
                              if (country != null) {
                                 cs.setProperty("country", country);
                              } else {
                                 cs.removeProperty("COUNTRY");
                              }
                              if (state != null) {
                                 cs.setProperty("state", state);
                              } else {
                                 cs.removeProperty("STATE");
                              }
                              if (projects != null) {
                                 cs.setProperty("projects", projects);
                              } else {
                                 cs.removeProperty("PROJECTS");
                              }
                              if (last != null) {
                                 cs.setProperty("last", last);
                              } else {
                                 cs.removeProperty("LAST");
                              }
                              if (first != null) {
                                 cs.setProperty("first", first);
                              } else {
                                 cs.removeProperty("FIRST");
                              }
                              
                              if (admin != null && 
                                  admin.equalsIgnoreCase("on")) {
                                 cs.setProperty("admin", "true");
                              } else {
                                 cs.setProperty("admin", "false");
                              }
                              
                              if (canlogin != null && 
                                  canlogin.equalsIgnoreCase("on")) {
                                 cs.setProperty("loginallowed", "true");
                              } else {
                                 cs.setProperty("loginallowed", "false");
                              }
                              
                              if (report != null && 
                                  canlogin.equalsIgnoreCase("on")) {
                                 cs.setProperty("report", "true");
                              } else {
                                 cs.setProperty("report", "false");
                              }
                              
                              if (reportdbox != null && 
                                  canlogin.equalsIgnoreCase("on")) {
                                 cs.setProperty("reportdbox", "true");
                              } else {
                                 cs.setProperty("reportdbox", "false");
                              }
                              
                              if (status != null && 
                                  canlogin.equalsIgnoreCase("on")) {
                                 cs.setProperty("status", "true");
                              } else {
                                 cs.setProperty("status", "false");
                              }
                              
                              userInfoChanged(cs); 
                              saveUserInfo();
                              b=FE.actionPage(co, tokenco != null, 
                                              "ID <b>" + user + 
                                              "</b> Updated", res);
                           }
                        }
                     }
                     break;
                  }
                  default:
                     b = FE.loginInfo("Invalid operation op="+op+"!", null, 
                                      res);
                     break;
               }
            } else if (baseop.equals("removeid")) {
               switch(opv) {
                  case 1:
                     b=FE.removeId(null, res);
                     break;
                  case 2:
                     String userid   = req.getParameter("userid"); 
                     
                     if (userid == null) {
                        b=FE.removeId("Enter a UserID!", res);
                     } else {
                        userid = userid.trim();
                        if (userid.length() == 0) {
                           b=FE.removeId("Enter a non-null Userid!", res);
                        } else {
                           if (!removeUserInfo(userid)) {
                              b=FE.removeId("User <b>" + userid + 
                                         "</b> does not exist!", res);
                           } else {
                              saveUserInfo();
                              b=FE.actionPage(co, tokenco != null,
                                              "ID <b>" + userid + 
                                              "</b> Removed", res);
                           }                              
                        }
                     }
                     break;
                  default:
                     b = FE.loginInfo("Invalid operation op="+op+"!", null, 
                                      res);
                     break;
               }
            } else if (baseop.equals("status")) {
            
               String urlstart = head;
               String lkey = DesktopServlet.generateCipher("getstatus", 60*60);
               String urlyuk  = urlstart + 
                                "/servlet/oem/edge/ed/odc/desktop/getstatus";
                                
               urlyuk = DesktopServlet.fixForRedirect(urlyuk);
               String urlv = res.encodeRedirectURL(urlyuk) + "?compname=" + lkey;
               
               res.sendRedirect(urlv);
               return true;
            } else if (baseop.equals("report")) {
            
               String urlstart = head;
               String lkey = DesktopServlet.generateCipher("report", 60*60);
               String urlyuk  = urlstart + 
                                "/servlet/oem/edge/ed/odc/desktop/report";
                                
               urlyuk = DesktopServlet.fixForRedirect(urlyuk);
               String urlv = res.encodeRedirectURL(urlyuk) + "?compname=" + lkey;
               
               res.sendRedirect(urlv);
               return true;
            } else if (baseop.equals("reportdbox")) {
            
               String urlstart = head;
               String lkey = DesktopServlet.generateCipher("report", 60*60);
               
               String urlyuk  = urlstart + 
                                "/servlet/oem/edge/ed/odc/desktop/report";
               urlyuk = DesktopServlet.fixForRedirect(urlyuk);
               
               String urlv = res.encodeRedirectURL(urlyuk) + "?DROPBOX=1&compname=" + lkey;
               res.sendRedirect(urlv);
               return true;
            } else if (baseop.equals("reportdboxnew")) {
            
               ConfigSection localco = co;
               
               co = tokenco;
               
               if (co == null) {
                  co = localco;
               }            
               
               String company   = (String)co.getProperty("company");
               String user      = (String)co.getProperty("userid");
               String firstname = (String)co.getProperty("first");
               String lastname  = (String)co.getProperty("last");
               String emailaddr = (String)co.getProperty("email");
               String projects  = (String)co.getProperty("projects");
               String country   = (String)co.getProperty("country");
               String state     = (String)co.getProperty("state");
               
               if (tokenco != null) {
                  String projectslocal = (String)localco.getProperty("projects");
                  if (projectslocal != null && projectslocal.length() > 0) {
                     if (projects == null || 
                         projects.length() == 0) {
                        projects = projectslocal;
                     } else {
                        projects = projects + ":" + projectslocal;
                     }
                  }
               }
               
              // Ensure project shows up only once
               if (projects != null && projects.length() > 0) {
                  StringTokenizer stok = new StringTokenizer(projects, ":");
                  projects=null;
                  String trick = ":";
                  while(stok.hasMoreTokens()) {
                     String maybe = stok.nextToken();
                     if (maybe != null && trick.indexOf(":" + maybe + ":") < 0) {
                        if (projects == null) projects  = maybe;
                        else                  projects += ":" + maybe;
                        trick += maybe + ":";
                     }
                  }
               }
               
               if (company == null) company = "";
               if (country == null) country = "";
               
               String collabtype = null;
               String cmd = null;
               
               ConfigObject nco = new ConfigObject();
               
               nco.setProperty("SCOPE", "webox:op:r");
               op="XFR";
               nco.setProperty("COMMAND", op);
               
               nco.setProperty("EDGEID",  user);
               nco.setProperty("COMPANY", company);
               nco.setProperty("COUNTRY", country);
               if (firstname != null) nco.setProperty("FIRST",    firstname);
               if (lastname  != null) nco.setProperty("LAST",     lastname);
               if (projects  != null) {
                  StringTokenizer st = new StringTokenizer(projects, ":", false);
                  int cnt = 0;
                  while(st.hasMoreElements()) {
                     nco.setProperty("P" + (++cnt), st.nextToken());
                  }
                  if (cnt > 0) {
                     nco.setProperty("PN" , ""+cnt);
                  }
               }
               if (emailaddr != null) nco.setProperty("EMAIL",    emailaddr);
               if (state     != null) nco.setProperty("STATE",    state);
               
               String urlstart = head;
               String lkey = DesktopServlet.generateCipher(nco.toString());
               
               String urlyuk  = urlstart+"/servlet/oem/edge/ed/odc/desktop/Login";
               urlyuk = DesktopServlet.fixForRedirect(urlyuk);
               
               String urlv = res.encodeRedirectURL(urlyuk) + 
                  "?compname=" + lkey;
               
               res.sendRedirect(urlv);
               return true;
            }
            
            PrintWriter out = res.getWriter();
            out.println(b);
            out.close();
               
            return true;
         } else if (command.equalsIgnoreCase("frontpagelink")) {
            HttpSession ses = req.getSession(true);
            if ( ses.isNew() == true) {
               ses.invalidate();
               
               String b = FE.loginInfo("No active session, or session expired. Please sign in.", thisurl, res);
               PrintWriter out = res.getWriter();
               out.println(b);
               out.close();
               return true;
            }
            
            
            String sid = ses.getId();
            String did    = (String)ses.getAttribute("did");
            String use       = (String)ses.getAttribute("userinfo");
            
           // We want tokenco here as its those contents that get used below
           //  No admin/expires/etc checks needed
            ConfigSection tokenco = (ConfigSection)ses.getAttribute("tokenco");
            ConfigSection localco = getUserInfo(use);
            
            ConfigSection co = tokenco;
            
            if (co == null) {
               co = localco;
            }
            
            if (did == null || sid == null || !did.equals(sid) || 
                co == null) {
            
               String b = FE.loginInfo("Session Broken!", thisurl, res);
               PrintWriter out = res.getWriter();
               out.println(b);
               out.close();
               return true;
            }
            
            String op   = req.getParameter("op");
            String host = req.getParameter("host");
            
            if (op != null && op.equals("SIGNOFF")) {
               ses.invalidate();
               
               String b = FE.loginInfo(null, null, res);
               PrintWriter out = res.getWriter();
               out.println(b);
               out.close();
               return true;
            }

            if (op == null || !(op.equals("DSH")     || 
                                op.equals("ODC")     ||
                                op.equals("CON")     ||
                                op.equals("XFR")     ||
                                op.equals("XFRSVC")  ||
                                op.equals("WXR")     ||
                                op.equals("FDR")     ||
                                op.equals("NEWODC"))) {
            
               String b = FE.loginInfo("Invalid operation op=" + op + "!",
                                       null, res);
               PrintWriter out = res.getWriter();
               out.println(b);
               out.close();
               
               return true;
            }
            
            String company   = (String)co.getProperty("company");
            String user      = (String)co.getProperty("userid");
            String firstname = (String)co.getProperty("first");
            String lastname  = (String)co.getProperty("last");
            String emailaddr = (String)co.getProperty("email");
            String projects  = (String)co.getProperty("projects");
            String country   = (String)co.getProperty("country");
            String state     = (String)co.getProperty("state");
            
            
           // sftp COULD be used to access dropbox directly.
           //  Enforce checking here.
            if (op.equalsIgnoreCase("XFR")     || 
                op.equalsIgnoreCase("XFRSVC")  ||
                op.equalsIgnoreCase("WXR")) {
               
               boolean enabled = true;
               if (company == null) company = "";
               if (user != null) {
                  enabled = 
                     DesktopServlet.getMappings().enabledForDropbox(user,
                                                                    company);
               } else {
                  enabled = false;
               } 
               if (!enabled) {
                  String b = FE.loginInfo("XFR function not available to you",
                                          null, res);
                  PrintWriter out = res.getWriter();
                  out.println(b);
                  out.close();
                  return true;
               }
            }
            
            if (tokenco != null) {
               String projectslocal = (String)localco.getProperty("projects");
               if (projectslocal != null && projectslocal.length() > 0) {
                  if (projects == null || 
                      projects.length() == 0) {
                     projects = projectslocal;
                  } else {
                     projects = projects + ":" + projectslocal;
                  }
               }
            }
            
           // Ensure project shows up only once
            if (projects != null && projects.length() > 0) {
               StringTokenizer stok = new StringTokenizer(projects, ":");
               projects=null;
               String trick = ":";
               while(stok.hasMoreTokens()) {
                  String maybe = stok.nextToken();
                  if (maybe != null && trick.indexOf(":" + maybe + ":") < 0) {
                     if (projects == null) projects  = maybe;
                     else                  projects += ":" + maybe;
                     trick += maybe + ":";
                  }
               }
            }
            
            if (company == null) company = "";
            if (country == null) country = "";
            
            String collabtype = null;
            String cmd = null;
            
            ConfigObject nco = new ConfigObject();
            
           // If Web dropbox, change op and set scope accordingly 
            if (op.equalsIgnoreCase("WXR")) {
               nco.setProperty("SCOPE", "webox:op:i");
               op="XFR";
            }
            
           /*
           // If SOA dropbox, change op and set scope accordingly 
            if (op.equalsIgnoreCase("XFRSVC")) {
               nco.setProperty("SCOPE", "soa:application");
               op="XFR";
            } else 
           */
            if (op.equalsIgnoreCase("XFRSVC")) {
               nco.setProperty("SCOPE", "soa:applet");
               op="XFR";
            }
            
            nco.setProperty("COMMAND", op);
            
            nco.setProperty("EDGEID",  user);
            nco.setProperty("COMPANY", company);
            nco.setProperty("COUNTRY", country);
            if (firstname != null) nco.setProperty("FIRST",    firstname);
            if (lastname  != null) nco.setProperty("LAST",     lastname);
            if (projects  != null) {
               StringTokenizer st = new StringTokenizer(projects, ":", false);
               int cnt = 0;
               while(st.hasMoreElements()) {
                  nco.setProperty("P" + (++cnt), st.nextToken());
               }
               if (cnt > 0) {
                  nco.setProperty("PN" , ""+cnt);
               }
            }
            if (emailaddr != null) nco.setProperty("EMAIL",    emailaddr);
            if (state     != null) nco.setProperty("STATE",    state);
               
            String urlstart = head;
            String lkey = DesktopServlet.generateCipher(nco.toString());
            
            String urlyuk  = urlstart+"/servlet/oem/edge/ed/odc/desktop/Login";
            urlyuk = DesktopServlet.fixForRedirect(urlyuk);
            
            String urlv = res.encodeRedirectURL(urlyuk) + "?compname=" + lkey;
            
            res.sendRedirect(urlv);
            return true;
         }            
      }
      return false;
   }
   
}
