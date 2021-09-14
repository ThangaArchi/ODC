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

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 1998,2004,2005,2006		                 */ 
/*                                                                       */ 
/*     All Rights Reserved					         */ 
/*     US Government Users Restricted Rights			         */ 
/*                                                                       */ 
/*   The source code for this program is not published or otherwise      */ 
/*   divested of its trade secrets, irrespective of what has been        */ 
/*   deposited with the US Copyright Office.                             */ 
/*   ------------------------------------------------------------------  */
/*     Please do not remove any of these commented lines  21 lines       */
/*   ------------------------------------------------------------------  */
/*                       Copyright Footer Check                          */
/*   ------------------------------------------------------------------  */ 

public class UsageReport {

   static String repgen_matter_head =  
   DesktopConstants.StandardHead + DesktopConstants.metapragma + 
   "<title>E-Design Report Facility</title></head>\n";
      
   static String repgen_matter_1a =  
      "<h1>Generate a Usage report</h1>\n" +
      "<form method=\"POST\" action=\"";
      
   static String repgen_matter_1b =  
      "/servlet/oem/edge/ed/odc/desktop/report\">\n" +
      "<input type=\"HIDDEN\" name=\"compname\" value=\"";
      
   static String repgen_matter_2 =  

      "\" />\n" +
      "<table border = 1 rules=\"none\" frame=\"border\">\n" +
      "<tr>\n" +
      "<th align=\"right\"><font color=\"red\">Start Date</font>:</th>\n" +
      "<td>\n";
      
   static String repgen_matter_3 =  
      "</td>\n" +
      "<th align=\"right\"><font color=\"red\">End Date</font>:</th>\n" +
      "<td>\n";
      
   static String repgen_matter_4 =  
      "</td>\n" +
      "</tr>\n" +
      "<tr><td colspan = 4><hr /></td></tr>\n" +
      "<tr>\n" +
      "<th align=\"right\"><font color=\"red\">Report Attributes</font>:</th>\n" +
      "</td><td colspan = 3>" +
      "<table border = 0 rules=\"none\">";
      
   static String repgen_matter_4drop =  
      "</td>\n" +
      "</tr>\n" +
      "<tr><td colspan = 4><hr /></td></tr>\n" +
      "<tr>\n" +
      "<td colspan = 1>";
      
   static String repgen_matter_4adrop =  
      "</td>\n" +
      "</tr>\n" +
      "<tr><td colspan = 4><hr /></td></tr>\n" +
      "<tr>\n" +
      "<th colspan = 4 align=\"center\"><font color=\"red\">Dropbox Reports</font></th>\n"+
      "</td><td colspan = 4>" +
      "<input type=\"HIDDEN\" name=\"DROPBOX\" value=\"1\"";
      
      
   static String repgen_matter_5 =  
      "</tr></table>\n" +
      "</td></tr>\n" +
      "<tr><td colspan = 4><hr /></td></tr>\n";
      
   static String repgen_matter_6 =  
      "</td></tr>\n" +
      "<tr><td colspan = 4><hr /></td></tr>\n" +
      "<tr>\n" +
      "<td colspan = 4 align=\"center\"><input name=\"submit\" type=\"submit\" value=\"Submit\" align=\"center\" /></td>\n" +
      "</tr>\n" +
      "</table>\n" +
      "</form></center>\n";
      
   static String repgen_matter_6drop =  
      "</td></tr>\n" +
      "</table>\n" +
      "</form></center>\n";
      
   
   static String repgen_matter_tail = 
      "</body></html>\n"; 

   static private String genUsageReportHtml(String token, 
                                            Hashtable props,
                                            Calendar sc, 
                                            Calendar ec) {
   
      StringBuffer ret = new StringBuffer();
      
      ret.append(repgen_matter_1a);
      ret.append(DesktopServlet.servletcontextpath);
      ret.append(repgen_matter_1b);
      ret.append(token);
      ret.append(repgen_matter_2);
      
      int sm =  0, sd =  1, sy=2003;
      int em = 11, ed = 31, ey=2004;
      
      if (sc != null) {
         sm = sc.get(Calendar.MONTH);
         sd = sc.get(Calendar.DAY_OF_MONTH);
         sy = sc.get(Calendar.YEAR);
      } else {
      
        // Fill in this month 1-31ish as default
         Calendar t = Calendar.getInstance();
         sm = t.get(Calendar.MONTH);
         sd = 1;
         sy = t.get(Calendar.YEAR);
         em = t.get(Calendar.MONTH);
         ed = t.getActualMaximum(Calendar.DAY_OF_MONTH);
         ey = t.get(Calendar.YEAR);
      }
      if (ec != null) {
         em = ec.get(Calendar.MONTH);
         ed = ec.get(Calendar.DAY_OF_MONTH);
         ey = ec.get(Calendar.YEAR);
      }
      
      java.text.DateFormatSymbols dfs = new java.text.DateFormatSymbols();
      String dfsarr[] = dfs.getShortMonths();
      int i;
      ret.append("<select name=\"StartMonth\">\n");
      for(i=0; i < 12; i++) {
         ret.append("<option value=\"" + i + "\" ");
         if (sm == i) ret.append("selected");
         ret.append(">").append(dfsarr[i]).append("\n");
      }
      ret.append("</select>\n<select name=\"StartDay\">\n");
      for(i=1; i < 32; i++) {
         ret.append("<option value=\"" + i + "\" ");
         if (sd == i) ret.append("selected");
         ret.append(">").append("" + i).append("\n");
      }
      ret.append("</select>\n<select name=\"StartYear\">\n");
      for(i=2000; i < 2014; i++) {
         ret.append("<option value=\"" + i + "\" ");
         if (sy == i) ret.append("selected");
         ret.append(">").append("" + i).append("\n");
      }
      ret.append("</select>\n");
      
      ret.append(repgen_matter_3);
      ret.append("<select name=\"EndMonth\">\n");
      for(i=0; i < 12; i++) {
         ret.append("<option value=\"" + i + "\" ");
         if (em == i) ret.append("selected");
         ret.append(">").append(dfsarr[i]).append("\n");
      }
      ret.append("</select>\n<select name=\"EndDay\">\n");
      for(i=1; i < 32; i++) {
         ret.append("<option value=\"" + i + "\" ");
         if (ed == i) ret.append("selected");
         ret.append(">").append("" + i).append("\n");
      }
      ret.append("</select>\n<select name=\"EndYear\">\n");
      for(i=2000; i < 2014; i++) {
         ret.append("<option value=\"" + i + "\" ");
         if (ey == i) ret.append("selected");
         ret.append(">").append("" + i).append("\n");
      }
      ret.append("</select>\n");
      
      if (props.get("DROPBOX") != null || props.get("dropbox") != null) {
         ret.append(repgen_matter_4drop);
         
         ret.append("<input name=\"S_SCOPECHK\" align=\"left\" ");
         if (props.get("S_SCOPECHK") != null || props.get("s_scopechk") != null) 
            ret.append(" checked ");
         ret.append("type=\"checkbox\">Scope ID/Company\n");
         
         ret.append("<br />\n");
         ret.append("<input name=\"S_INCEXCHK\" align=\"left\" ");
         if (props.get("S_INCEXCHK") != null || props.get("s_incexchk") != null) 
            ret.append(" checked ");
         ret.append("type=\"checkbox\">Include or Exclude\n");
         
         ret.append("</td><td colspan = 3>\n");
         ret.append("<textarea name=\"S_INCEXTXT\" rows = 4 cols = 40>");
         String val = (String)props.get("S_INCEXTXT");
         if (val == null) val = (String)props.get("s_incextxt");
         if (val != null) {
            ret.append(val);
         }
         ret.append("</textarea>\n");
         
         ret.append(repgen_matter_4adrop);
         
         ret.append(ReportGenerator.generateReportIndex());
         ret.append(repgen_matter_6drop);
      } else {
         ret.append(repgen_matter_4);
         
         ret.append("<td>\n");
         ret.append("<input name=\"S_COMPANY\" align=\"left\" ");
      
         if (props.get("S_COMPANY") != null || props.get("s_company") != null)
            ret.append(" checked ");
          
         ret.append("type=\"checkbox\">Company\n");
         ret.append("</td><td>\n");
         ret.append("<input name=\"S_USER\" align=\"left\" ");
         
         if (props.get("S_USER") != null || props.get("s_user") != null) 
            ret.append(" checked ");
         
         ret.append("type=\"checkbox\">Userid\n");
         ret.append("</td><td>\n");
         ret.append("<input name=\"S_COUNTRY\" align=\"left\" ");
         
         if (props.get("S_COUNTRY") != null || props.get("s_country") != null) 
            ret.append(" checked ");
         
         ret.append("type=\"checkbox\">Country\n");
         ret.append("</td></tr><tr><td>\n");
         ret.append("<input name=\"S_STATE\" align=\"left\" ");
         
         if (props.get("S_STATE") != null || props.get("s_state") != null) 
            ret.append(" checked ");
         
         ret.append("type=\"checkbox\">State\n");
         ret.append("</td><td>\n");
         ret.append("<input name=\"S_EMAIL\" align=\"left\" ");
         
         if (props.get("S_EMAIL") != null || props.get("s_email") != null) 
            ret.append(" checked ");
         
         ret.append("type=\"checkbox\">Email\n");
         ret.append("</td><td>\n");
         ret.append("<input name=\"S_PROJECT\" align=\"left\" ");
         
         if (props.get("S_PROJECT") != null || props.get("s_project") != null) 
            ret.append(" checked ");
         
         ret.append("type=\"checkbox\">Project\n");
         ret.append("</td></tr><tr><td>\n");
         ret.append("<input name=\"S_FIRST\" align=\"left\" ");
         
         if (props.get("S_FIRST") != null || props.get("s_first") != null) 
            ret.append(" checked ");
         
         ret.append("type=\"checkbox\">First Name\n");
         ret.append("</td><td>\n");
         ret.append("<input name=\"S_LAST\" align=\"left\" ");
         
         if (props.get("S_LAST") != null || props.get("s_last") != null) 
            ret.append(" checked ");
         
         ret.append("type=\"checkbox\">Last Name\n");
         ret.append("</td><td>\n");
         ret.append("<input name=\"S_RHOST\" align=\"left\" ");
         
         if (props.get("S_RHOST") != null || props.get("s_rhost") != null) 
            ret.append(" checked ");
         
         ret.append("type=\"checkbox\">RHost\n");
         ret.append("</td></tr><tr><td>\n");
         ret.append("<input name=\"S_TOFROMCLIENT\" align=\"left\" ");
         
         if (props.get("S_TOFROMCLIENT") != null || 
             props.get("s_tofromclient") != null) 
            ret.append(" checked ");
         
         ret.append("type=\"checkbox\">ToFrom\n");
         ret.append("</td></tr><tr><td>\n");
         
         ret.append("<input name=\"S_SERVICE\" value=\"S_NADA\" align=\"left\" ");
         
         String svc = (String)props.get("S_SERVICE");
         if (svc == null) {
            svc = (String)props.get("S_SERVICE");
         }
         
         if (svc == null || svc.equalsIgnoreCase("S_NADA")) 
            ret.append(" checked ");
         
         ret.append("type=\"radio\">None\n");
         ret.append("</td><td>\n");
         ret.append("<input name=\"S_SERVICE\" value=\"S_SERVICERECAP\" align=\"left\" ");
         
         if (svc != null && svc.equalsIgnoreCase("S_SERVICERECAP")) 
            ret.append(" checked ");
         
         ret.append("type=\"radio\">ServiceRecap\n");
         ret.append("</td><td>\n");
         
         ret.append("<input name=\"S_SERVICE\" value=\"S_SERVICE\" align=\"left\" ");
         
         if (svc != null && svc.equalsIgnoreCase("S_SERVICE")) 
            ret.append(" checked ");
         
         ret.append("type=\"radio\">Service\n");
         
         ret.append("</td></tr><tr><td>\n");
         
         ret.append("<input name=\"S_DRILL\" align=\"left\" ");
         if (props.get("S_DRILL") != null || props.get("s_drill") != null) 
            ret.append(" checked ");
         ret.append("type=\"checkbox\">Record Level\n");
         ret.append("</td><td>\n");
         ret.append("<input name=\"S_TIME\" align=\"left\" ");
         if (props.get("S_TIME") != null || props.get("s_time") != null) 
            ret.append(" checked ");
         ret.append("type=\"checkbox\">Time\n");
         ret.append("<input name=\"S_KEY\" align=\"left\" ");
         if (props.get("S_KEY") != null || 
             props.get("s_key") != null) 
            ret.append(" checked ");
         ret.append("type=\"checkbox\">Key\n");
         ret.append("</td>\n");
      
         ret.append(repgen_matter_5);
         
         ret.append("<tr>\n");
         ret.append("<td>\n");
         ret.append("<input name=\"S_SCOPECHK\" align=\"left\" ");
         if (props.get("S_SCOPECHK") != null || props.get("s_scopechk") != null) 
            ret.append(" checked ");
         ret.append("type=\"checkbox\">Scope ID/Company/SVC\n");
         
         ret.append("<br />\n");
         ret.append("<input name=\"S_INCEXCHK\" align=\"left\" ");
         if (props.get("S_INCEXCHK") != null || props.get("s_incexchk") != null) 
            ret.append(" checked ");
         ret.append("type=\"checkbox\">Include or Exclude\n");
         
         ret.append("</td><td colspan = 3>\n");
         ret.append("<textarea name=\"S_INCEXTXT\" rows = 8 cols = 40>");
         String val = (String)props.get("S_INCEXTXT");
         if (val == null) val = (String)props.get("s_incextxt");
         if (val != null) {
            ret.append(val);
         }
         ret.append("</textarea>\n");
         ret.append(repgen_matter_6);
      }
      
      return ret.toString();
   }
   
   static protected void handleMainReport(
      Properties prop, 
      javax.servlet.http.HttpServletRequest req, 
      javax.servlet.http.HttpServletResponse res) throws IOException {
   
      PrintWriter out = res.getWriter();
            
      Calendar sc = null;
      Calendar ec = null;
      try {
         String sys  = prop.getProperty("StartYear");
         String sds  = prop.getProperty("StartDay");
         String sms  = prop.getProperty("StartMonth");
         String eys  = prop.getProperty("EndYear");
         String eds  = prop.getProperty("EndDay");
         String ems  = prop.getProperty("EndMonth");
//             String reps = prop.getProperty("Report");
         int sy  = Integer.parseInt(sys);
         int sd  = Integer.parseInt(sds);
         int sm  = Integer.parseInt(sms);
         int ey  = Integer.parseInt(eys);
         int ed  = Integer.parseInt(eds);
         int em  = Integer.parseInt(ems);
//             int rep = Integer.parseInt(reps);
                        
         sc = Calendar.getInstance();
         sc.set(Calendar.YEAR,         sy);
         sc.set(Calendar.MONTH,        sm);
         sc.set(Calendar.DAY_OF_MONTH, sd);
         sc.set(Calendar.HOUR_OF_DAY,  0);
         sc.set(Calendar.MINUTE,       0);
         sc.set(Calendar.SECOND,       0);
//             sc.complete();
                        
         ec = Calendar.getInstance();
         ec.set(Calendar.YEAR,         ey);
         ec.set(Calendar.MONTH,        em);
         ec.set(Calendar.DAY_OF_MONTH, ed);
         ec.set(Calendar.HOUR_OF_DAY,  23);
         ec.set(Calendar.MINUTE,       59);
         ec.set(Calendar.SECOND,       59);
//             ec.complete();
      } catch (Exception eee) {}
                        
      try {
         String repostURL = req.getContextPath() + 
            "/servlet/oem/edge/ed/odc/desktop/report";
                        
         out.println(DesktopConstants.StandardHead +
                     "<title>Report Generator</title></head><body>");
         out.println(genUsageReportHtml(DesktopServlet.generateCipher("report", 3600), 
                                        prop, sc, ec));
                                              
         if (prop.get("DROPBOX") != null || 
             prop.get("dropbox") != null) {
            String idxS = (String)prop.get("dropboxidx");
            if (idxS == null)idxS = (String)prop.get("DROPBOXIDX");
                  
            String s_scope   = (String)prop.getProperty("S_SCOPECHK");
            String s_incEx   = (String)prop.getProperty("S_INCEXCHK");
            String s_incExTxt= (String)prop.getProperty("S_INCEXTXT");
                  
            if (idxS != null && sc != null && ec != null) {
               out.println("<p /><p /><hr /><p />");
               try {
                  ReportGenerator.generateReport(out, 
                                                 Integer.parseInt(idxS)-1,
                                                 s_scope != null ? s_incExTxt 
                                                 : null,
                                                 s_incEx != null,
                                                 sc, ec);
                                                    
               } catch(Exception ee) {
                  DebugPrint.printlnd("Error doing DropboxReport!");
                  DebugPrint.println(DebugPrint.ERROR, ee);
                  out.println("Error generating report #" + idxS);
               }
            }
         } else {
            if (sc != null && ec != null) {
               out.println("<p /><p /><hr /><p />");
                     
               Date sdd=null, edd=null;
                     
               sdd=sc.getTime(); 
               edd=ec.getTime();
                     
               generateMetricsReport (out, repostURL, prop, 
                                      sdd, edd);
            }
         }
         out.println("</body></html>");
                        
      } catch(Throwable tt) {
         out.println("Error generating report!");
         tt.printStackTrace(out);
      }
                     
      out.close();
      return;
   }
   
   
   static private String generateInExList(Vector v) {
      if (v == null || v.size() == 0) return null;
      
      String ret = "";
      Enumeration enum = v.elements();
      while(enum.hasMoreElements()) {
         String s = (String)enum.nextElement();
         if (ret.length() > 0) ret += ",";
         ret += "'" + s + "'";
      }
      return "(" + ret + ")";
   }
   
   static private String genReportHeader(String h, String orderby, 
                                         String repostURL,
                                         String qs) {
      return "<th><a href=\"" + repostURL + "?" + qs  + 
         "&C_ORDER=" + URLEncoder.encode(orderby) +
         "\">" + h + "</a></th>\n";
   }

      
   static public void generateMetricsReport(PrintWriter out, String repostURL,
                                            Hashtable props, java.util.Date start, 
                                            java.util.Date end) {
   
     // Upcase elements, build repost qs
      Hashtable tmphash = new Hashtable();
      Enumeration penum = props.keys();
      String repostQS = "";
      while(penum.hasMoreElements()) {
         String key = (String)penum.nextElement();
         String val = (String)props.get(key);
         if (key.length() > 2 && key.charAt(1) == '_') {
            key = key.toUpperCase();
         }
         if (!key.equals("C_ORDER")) {
            if (repostQS.length() > 0) {
               repostQS += "&";
            }
            repostQS += URLEncoder.encode(key) + "=" + URLEncoder.encode(val);
         }
         tmphash.put(key, val);
      }
      props = tmphash;
      
      StringBuffer presql = new StringBuffer();
      
      String s_drill   = (String)props.get("S_DRILL");
      String s_company = (String)props.get("S_COMPANY");
      String s_user    = (String)props.get("S_USER");
      String s_first   = (String)props.get("S_FIRST");
      String s_last    = (String)props.get("S_LAST");
      String s_users   = (String)props.get("S_USERS");
      String s_service = (String)props.get("S_SERVICE");
      String s_country = (String)props.get("S_COUNTRY");
      String s_state   = (String)props.get("S_STATE");
      String s_rhost   = (String)props.get("S_RHOST");
      String s_email   = (String)props.get("S_EMAIL");
      String s_tofrom  = (String)props.get("S_TOFROMCLIENT");
      String s_project = (String)props.get("S_PROJECT");
      String s_time    = (String)props.get("S_TIME");
      String s_key     = (String)props.get("S_KEY");
      
      String s_scope   = (String)props.get("S_SCOPECHK");
      String s_incEx   = (String)props.get("S_INCEXCHK");
      String s_incExTxt= (String)props.get("S_INCEXTXT");
      
      String m_meeting = (String)props.get("M_MEETING");
      String m_invite  = (String)props.get("M_INVITE");
      String c_order   = (String)props.get("C_ORDER");
      

      String dbInstance = "edesign";
      
      String metricdt      = dbInstance + ".metricdt";
      String metricservice = dbInstance + ".metricservice";
      
      Vector listofservices = new Vector();
                       
      String groupby  = "";
      String orderby  = c_order;
      
      String join = " left outer join " + metricservice + 
                    " ms on mdt.key=ms.key ";
      boolean dojoin = false;
      StringBuffer subselect = new StringBuffer(" ");
      
      StringBuffer functpreface = new StringBuffer();
      StringBuffer fromclause = new StringBuffer(" from ");
      
      fromclause.append(metricdt).append(" mdt ");
      
      Connection c = null;
      DBConnection dbconn = DBSource.getDBConnection("EDODC");
      
      try {
         c = dbconn.getConnection();
            
         StringBuffer whereclause = new StringBuffer(" where ");
         whereclause.append(" ms.starttime >= '")
            .append((new java.sql.Timestamp(start.getTime())).toString());
         whereclause.append("' and  ms.starttime <= '")
            .append((new java.sql.Timestamp(end.getTime())).toString())
            .append("' ");
         String q = "select distinct ms.servicetype from " + 
            metricservice + " ms " + whereclause;
            
         System.out.println("Issuing: " + q);
         java.sql.Statement stmt    = c.createStatement();
         java.sql.ResultSet results = stmt.executeQuery(q);
         while(results.next()) {
            String ts = results.getString(1);
            System.out.println("Service in play: " + ts);
            listofservices.addElement(ts);
         }
         
         results.close();
         stmt.close();
         
      } catch(Exception e) {
         System.out.println("Error doing our business!");
         e.printStackTrace(System.out);
         out.println("<p />Error doing our business!<p />");
         return;
      } finally {
         if (dbconn != null && c != null) dbconn.returnConnection(c);
      }
                 
      StringBuffer whereclause = new StringBuffer(" where ");
      whereclause.append(" mdt.starttime >= '")
                 .append((new java.sql.Timestamp(start.getTime())).toString());
      whereclause.append("' and  mdt.starttime <= '")
                 .append((new java.sql.Timestamp(end.getTime())).toString())
                 .append("' ");
                 
                 
     // This is used to modify the search by user, company, and service
      StringBuffer wheremodifier = new StringBuffer(" ");
      if (s_scope != null) {
         boolean incEx = s_incEx != null;
        /*
        ** Text is in form:
        **
        **   userid
        **   U:userid
        **   C:company
        **   S:service
        **   
        **   If X:y form is used, then + and - can precede X to override 
        **    default include/exclude setting
        */
         if (s_incExTxt != null) {
            String s = s_incExTxt;
            
           // Remove all CR codes
            int len = s.length();
            StringBuffer tsb = new StringBuffer();
            for(int i=0; i < len; i++) {
               char ch = s.charAt(i);
               if (ch != '\r') tsb.append(ch);
            }
            
            s = tsb.toString();
            
            Vector inclUser = new Vector();
            Vector inclComp = new Vector();
            Vector inclSvc  = new Vector();
            Vector exclUser = new Vector();
            Vector exclComp = new Vector();
            Vector exclSvc  = new Vector();
            while(s != null) {
               String ws = s;
               int idx = s.indexOf('\n');
               if (idx >= 0) {
                  ws = s.substring(0,idx);
                  if (s.length() > idx+1) s = s.substring(idx+1);
                  else                    s = null;
               } else {
                  s = null;
               }
               
              // ws is our current token
               boolean incExInner = incEx;
               int userCompanyService = 0;
               idx = ws.indexOf(':');
               if (idx >= 0) {
                  String inners = ws.substring(0, idx);
                  ws = ws.substring(idx+1);
                  if (inners.indexOf("+") == 0) incExInner = true;
                  if (inners.indexOf("-") == 0) incExInner = false;
                  if (inners.indexOf("U") >= 0) userCompanyService = 0;
                  if (inners.indexOf("C") >= 0) userCompanyService = 1;
                  if (inners.indexOf("S") >= 0) userCompanyService = 2;
               }
               
              // Now, incExInner is true/false to include/exclude, ws is the
              //  value, and userCompanyServer is 0-2 indicating what variable
              //  we are affecting
               if (ws.indexOf("'") >= 0) {
                  DebugPrint.printlnd(DebugPrint.WARN, 
                                      "Bzzz. Report generator given name with emdedded quote!");
               } else {
                  
                  if (incExInner) {
                     switch(userCompanyService) {
                        case 0: inclUser.add(ws); break;
                        case 1: inclComp.add(ws); break;
                        case 2: inclSvc.add(ws);  break;
                     }
                  } else {
                     switch(userCompanyService) {
                        case 0: exclUser.add(ws); break;
                        case 1: exclComp.add(ws); break;
                        case 2: exclSvc.add(ws);  break;
                     }
                  }
               }
            }
            
            s = generateInExList(inclUser);
            if (s != null) {
               wheremodifier.append(" and mdt.edgeid in ").append(s);
               wheremodifier.append(" ");
            }
            s = generateInExList(inclComp);
            if (s != null) {
               wheremodifier.append(" and mdt.company in ").append(s);
               wheremodifier.append(" ");
            }
            
           //s = generateInExList(inclSvc);
           
            s = generateInExList(exclUser);
            if (s != null) {
               wheremodifier.append(" and mdt.edgeid not in ").append(s);
               wheremodifier.append(" ");
            }
            s = generateInExList(exclComp);
            if (s != null) {
               wheremodifier.append(" and mdt.company not in ").append(s);
               wheremodifier.append(" ");
            }
            
           //s = generateInExList(inclSvc);
           
            s = wheremodifier.toString();
         }
      }
                 
      StringBuffer headers = new StringBuffer();
            
     // If this is still a summary
      if (s_drill == null) {
         
         if (orderby == null || orderby.length() == 0) 
            orderby = " numrec desc ";
            
         functpreface.append("select count(*) as numrec,");
         functpreface.append("sum(mdt.deltatime/1000)  as totdelt,");
         functpreface.append("avg(mdt.deltatime/1000)  as avgdelt ");
         
         headers.append(genReportHeader("#Recs", 
                                        " numrec desc", 
                                        repostURL, repostQS));
         headers.append(genReportHeader("TotElap",     
                                        " totdelt desc", 
                                        repostURL, repostQS));
         headers.append(genReportHeader("AvgElap",     
                                        " avgdelt desc", 
                                        repostURL, repostQS));
                  
         if (s_user != null) {
            functpreface.append(",mdt.edgeid ");
            if (groupby.length() > 0) groupby += ",";
            groupby += " mdt.edgeid ";
            headers.append(genReportHeader("ID",   
                                           " mdt.edgeid desc", 
                                           repostURL, repostQS));
         }
         if (s_company != null) {
            functpreface.append(",mdt.company ");
            if (groupby.length() > 0) groupby += ",";
            groupby += " mdt.company ";
            headers.append(genReportHeader("Company",   
                                           " mdt.company desc", 
                                           repostURL, repostQS));
         }
         if (s_service != null && s_service.equalsIgnoreCase("S_SERVICE")) {
            functpreface.append(",servicetype ");
            if (groupby.length() > 0) groupby += ",";
            groupby += " ms.servicetype ";
            dojoin = true;
            headers.append(genReportHeader("SVC",   
                                           " ms.servicetype desc", 
                                           repostURL, repostQS));
         } 
         if (s_tofrom != null) {
            functpreface.append(",avg(mdt.toclient)/1024   as avgtoclient");
            functpreface.append(",avg(mdt.fromclient)/1024 as avgfromclient ");
            
            headers.append(genReportHeader("AvgSent(K)",   
                                           " mdt.toclient desc", 
                                           repostURL, repostQS));
            headers.append(genReportHeader("AvgRecv(K)", 
                                           " mdt.fromclient desc", 
                                           repostURL, repostQS));
         }
         
         if (s_email != null) {
            functpreface.append(",mdt.email ");
            if (groupby.length() > 0) groupby += ",";
            groupby += " mdt.email ";
            headers.append(genReportHeader("Email",   
                                           " mdt.email desc", 
                                           repostURL, repostQS));
         }
         if (s_rhost != null) {
            functpreface.append(",mdt.rhost ");
            if (groupby.length() > 0) groupby += ",";
            groupby += " mdt.rhost ";
            headers.append(genReportHeader("rhost",   
                                           " mdt.rhost desc", 
                                           repostURL, repostQS));
         }
         if (s_first != null) {
            functpreface.append(",mdt.fname ");
            if (groupby.length() > 0) groupby += ",";
            groupby += " mdt.fname ";
            headers.append(genReportHeader("First",   
                                           " mdt.fname desc", 
                                           repostURL, repostQS));
         }
         if (s_last != null) {
            functpreface.append(",mdt.lname ");
            if (groupby.length() > 0) groupby += ",";
            groupby += " mdt.lname ";
            headers.append(genReportHeader("Last",   
                                           " mdt.lname desc", 
                                           repostURL, repostQS));
         }
         if (s_country != null) {
            functpreface.append(",mdt.country ");
            if (groupby.length() > 0) groupby += ",";
            groupby += " mdt.country ";
            headers.append(genReportHeader("Country",   
                                           " mdt.country desc", 
                                           repostURL, repostQS));
         }
         if (s_state != null) {
            functpreface.append(",mdt.state ");
            if (groupby.length() > 0) groupby += ",";
            groupby += " mdt.state ";
            headers.append(genReportHeader("State",   
                                           " mdt.state desc", 
                                           repostURL, repostQS));
         }
         if (s_project != null) {
            functpreface.append(",ms.project ");
            if (groupby.length() > 0) groupby += ",";
            groupby += " ms.project ";
            dojoin = true;
            headers.append(genReportHeader("Proj",   
                                           " ms.project desc", 
                                           repostURL, repostQS));
         }
         
        /* this does not work well, as records get counted multiple times
         if (s_service != null && 
             s_service.equalsIgnoreCase("S_SERVICERECAP")) {
            Enumeration enum = listofservices.elements();
            while(enum.hasMoreElements()) {
               String s = (String)enum.nextElement();
               String lsvc = " SVCT_" + s;
               
              // Can do a simple subselect here as 
               subselect.append(" left outer join ")
                        .append(metricservice)
                        .append(lsvc)
                        .append(" on mdt.key=")
                        .append(lsvc)
                        .append(".key AND ")
                        .append(lsvc)
                        .append(".servicetype = '")
                        .append(s)
                        .append("' ");
               functpreface.append(",count(").append(lsvc)
                           .append(".servicetype) as svc_").append(s)
                           .append(" ");
               headers.append(genReportHeader(s,   
                                              " svc_" + s + " desc ", 
                                              repostURL, repostQS));
            }
         }
        */
        
        /*
        ** !! MAN but this is ugly !! 
        **
        ** there is only 1 record in metricdt per key, there can be multiple
        ** servicetype records per tunnel. When we create the service recap,
        ** we want to (quickly) count up the number of each service per record
        ** being shown. No good way that I could see. Settled on this mess.
        **
        ** Essentially, I create a single table containing all of the services
        ** (one column each) and sum'd. Then I join that table to the mdt
        ** works, but what obfuscation!
        **
        ** Here is a sample query to help you visualize it
        
           select count(*) as numrec,sum(mdt.deltatime/1000)  as 
           totdelt,avg(mdt.deltatime/1000)  as avgdelt ,mdt.edgeid ,
           mdt.company ,sum(whew.SVCDSH) as svc_DSH ,
           sum(whew.SVCEDU) as svc_EDU ,sum(whew.SVCNEWODC) as svc_NEWODC ,
           sum(whew.SVCWHB) as svc_WHB ,sum(whew.SVCXFR) as svc_XFR  
           from edesign.metricdt mdt   
           left outer join (select ll.svckey,sum(ll.SVCDSH) as SVCDSH,
           sum(ll.SVCEDU) as SVCEDU,sum(ll.SVCNEWODC) as SVCNEWODC,
           sum(ll.SVCWHB) as SVCWHB,sum(ll.SVCXFR) as SVCXFR from 
           table( select key as svckey,count(servicetype) as SVCDSH,
           count(CAST(NULL as VARCHAR(16))) as SVCEDU,
           count(CAST(NULL as VARCHAR(16))) as SVCNEWODC,
           count(CAST(NULL as VARCHAR(16))) as SVCWHB,
           count(CAST(NULL as VARCHAR(16))) as SVCXFR 
           from edesign.metricservice where servicetype='DSH' 
           group by key union all  select key as svckey,
           count(CAST(NULL as VARCHAR(16))) as SVCDSH,
           count(servicetype) as SVCEDU,
           count(CAST(NULL as VARCHAR(16))) as SVCNEWODC,
           count(CAST(NULL as VARCHAR(16))) as SVCWHB,
           count(CAST(NULL as VARCHAR(16))) as SVCXFR 
           from edesign.metricservice where servicetype='EDU' 
           group by key union all  select key as svckey,
           count(CAST(NULL as VARCHAR(16))) as SVCDSH,
           count(CAST(NULL as VARCHAR(16))) as SVCEDU,
           count(servicetype) as SVCNEWODC,
           count(CAST(NULL as VARCHAR(16))) as SVCWHB,
           count(CAST(NULL as VARCHAR(16))) as SVCXFR 
           from edesign.metricservice where servicetype='NEWODC' 
           group by key union all  select key as svckey,
           count(CAST(NULL as VARCHAR(16))) as SVCDSH,
           count(CAST(NULL as VARCHAR(16))) as SVCEDU,
           count(CAST(NULL as VARCHAR(16))) as SVCNEWODC,
           count(servicetype) as SVCWHB,
           count(CAST(NULL as VARCHAR(16))) as SVCXFR 
           from edesign.metricservice where servicetype='WHB' 
           group by key union all  select key as svckey,
           count(CAST(NULL as VARCHAR(16))) as SVCDSH,
           count(CAST(NULL as VARCHAR(16))) as SVCEDU,
           count(CAST(NULL as VARCHAR(16))) as SVCNEWODC,
           count(CAST(NULL as VARCHAR(16))) as SVCWHB,
           count(servicetype) as SVCXFR 
           from edesign.metricservice 
           where servicetype='XFR' group by key) as ll group by ll.svckey 
           order by ll.svckey) as whew on whew.svckey = mdt.key
           where  mdt.starttime >= '2003-10-01 00:00:00.386' and 
           mdt.starttime <= '2003-11-30 23:59:59.386'
           group by  mdt.edgeid , mdt.company  order by  svc_EDU desc 
  
        
        **
        */
         if (s_service != null && 
             s_service.equalsIgnoreCase("S_SERVICERECAP")) {
            
            StringBuffer sb = new StringBuffer();
            
            Enumeration enum = listofservices.elements();
            String Scast = "count(CAST(NULL as VARCHAR(16))) as SVC";
            String Scount = "count(servicetype) as SVC";
            
            StringBuffer where = new StringBuffer(" where ");
            where.append(" ms.starttime >= '")
               .append((new java.sql.Timestamp(start.getTime())).toString());
            where.append("' and  ms.starttime <= '")
               .append((new java.sql.Timestamp(end.getTime())).toString())
               .append("' ");
            
            subselect.append(" left outer join (select ll.svckey");
            int j = 0;
            while(enum.hasMoreElements()) {
               String s = (String)enum.nextElement();
               
               subselect.append(",sum(ll.SVC").append(s)
                        .append(") as SVC").append(s);
               
               if (++j > 1) {
                  sb.append(" union all ");
               } else {
                  sb.append(" from table(");
               }
               sb.append(" select key as svckey");
               
               int i=0;
               Enumeration inenum = listofservices.elements();
               while(inenum.hasMoreElements()) {
                  String ins = (String)inenum.nextElement();
                  
                  sb.append(",");
                  
                  if (ins.equals(s)) {
                     sb.append(Scount).append(ins);
                  } else {
                     sb.append(Scast).append(ins);
                  }
               }
               
               sb.append(" from ").append(metricservice)
                  .append(" where servicetype='").append(s)
                  .append("' group by key");
                  
               functpreface.append(",sum(").append("whew.SVC")
                           .append(s).append(") as svc_").append(s)
                           .append(" ");
               headers.append(genReportHeader(s,   
                                              " svc_" + s + " desc ", 
                                              repostURL, repostQS));
            }
            
            subselect.append(sb.toString())
               .append(") as ll group by ll.svckey order by ll.svckey)")
               .append(" as whew on whew.svckey = mdt.key ");
         }
         
      } else {
      
         functpreface.append("select ");
         
         if (s_key != null) {
            functpreface.append(" mdt.key, ");
            if (groupby.length() > 0) groupby += ",";
            groupby = " mdt.key ";
            headers.append(genReportHeader("Key",   
                                           " mdt.key desc", 
                                           repostURL, repostQS));
         } else {
           // If they don't WANT skey, then add it anyway, cause we have
           //  to always do groupby now, for servicerecap (using count)
            functpreface.append(" mdt.key, ");
            if (groupby.length() > 0) groupby += ",";
            groupby = " mdt.key ";
         }
      
         if (orderby == null || orderby.length() == 0) 
            orderby = " mdt.deltatime desc ";
         
         functpreface.append(" (mdt.deltatime/1000)  as delt ");
         if (groupby.length() > 0) groupby += ",";
         groupby += " mdt.deltatime ";
         headers.append(genReportHeader("Delta",   
                                        " delt desc", 
                                        repostURL, repostQS));
         
         if (s_time != null) {
            if (s_service != null && 
                s_service.equalsIgnoreCase("S_SERVICE")) {
               
               functpreface.append(",ms.starttime ");
               functpreface.append(",ms.endtime ");
               headers.append(genReportHeader("Start",   
                                              " ms.starttime desc", 
                                              repostURL, repostQS));
               headers.append(genReportHeader("End",   
                                              " ms.endtime desc", 
                                              repostURL, repostQS));
               if (groupby.length() > 0) groupby += ",";
               groupby += " ms.starttime, ms.endtime ";
            } else {
               functpreface.append(",mdt.starttime ");
               functpreface.append(",mdt.endtime ");
               headers.append(genReportHeader("Start",   
                                              " mdt.starttime desc", 
                                              repostURL, repostQS));
               headers.append(genReportHeader("End",   
                                              " mdt.endtime desc", 
                                              repostURL, repostQS));
               if (groupby.length() > 0) groupby += ",";
               groupby += " mdt.starttime, mdt.endtime ";
            }
         }
         
         if (s_company != null) {
            functpreface.append(",mdt.company ");
            if (groupby.length() > 0) groupby += ",";
            groupby += " mdt.company ";
            headers.append(genReportHeader("Company",   
                                           " company desc", 
                                           repostURL, repostQS));
         }
         if (s_user != null) {
            functpreface.append(",mdt.edgeid ");
            if (groupby.length() > 0) groupby += ",";
            groupby += " mdt.edgeid ";
            headers.append(genReportHeader("ID",   
                                           " mdt.edgeid desc", 
                                           repostURL, repostQS));
         }
         if (s_email != null) {
            functpreface.append(",mdt.email ");
            if (groupby.length() > 0) groupby += ",";
            groupby += " mdt.email ";
            headers.append(genReportHeader("Email",   
                                           " mdt.email desc", 
                                           repostURL, repostQS));
         }
         if (s_rhost != null) {
            functpreface.append(",mdt.rhost ");
            if (groupby.length() > 0) groupby += ",";
            groupby += " mdt.rhost ";
            headers.append(genReportHeader("Rhost",   
                                           " mdt.rhost desc", 
                                           repostURL, repostQS));
         }
         if (s_first != null) {
            functpreface.append(",mdt.fname ");
            if (groupby.length() > 0) groupby += ",";
            groupby += " mdt.fname ";
            headers.append(genReportHeader("First",   
                                           " mdt.fname desc", 
                                           repostURL, repostQS));            
         }
         if (s_last != null) {
            functpreface.append(",mdt.lname ");
            if (groupby.length() > 0) groupby += ",";
            groupby += " mdt.lname ";
            headers.append(genReportHeader("Last",   
                                           " mdt.lname desc", 
                                           repostURL, repostQS));
         }
         
         if (s_tofrom != null) {
            functpreface.append(",(mdt.toclient/1024)   as ttoclient ");
            functpreface.append(",(mdt.fromclient/1024) as ffromclient ");
            headers.append(genReportHeader("Sent(K)",   
                                           " mdt.toclient desc", 
                                           repostURL, repostQS));
            headers.append(genReportHeader("Recv(K)", 
                                           " mdt.fromclient desc", 
                                           repostURL, repostQS));
            if (groupby.length() > 0) groupby += ",";
            groupby += " ttoclient,ffromclient ";
         }
         
         if (s_service != null && s_service.equalsIgnoreCase("S_SERVICE")) {
            whereclause = new StringBuffer(" where ");
            whereclause.append(" ms.starttime >= '")
               .append((new java.sql.Timestamp(start.getTime())).toString());
            whereclause.append("' and  ms.starttime <= '")
               .append((new java.sql.Timestamp(end.getTime())).toString())
               .append("' ");
            functpreface.append(",servicetype ");
            if (groupby.length() > 0) groupby += ",";
            groupby += " ms.servicetype ";
            dojoin = true;
            headers.append(genReportHeader("SVC",   
                                           " ms.servicetype desc", 
                                           repostURL, repostQS));            
         }
         
         if (s_country != null) {
            functpreface.append(",mdt.country ");
            if (groupby.length() > 0) groupby += ",";
            groupby += " mdt.country ";
            headers.append(genReportHeader("Country",   
                                           " mdt.country desc", 
                                           repostURL, repostQS));
         }
         if (s_state != null) {
            functpreface.append(",mdt.state ");
            if (groupby.length() > 0) groupby += ",";
            groupby += " mdt.state ";
            headers.append(genReportHeader("State",   
                                           " mdt.state desc", 
                                           repostURL, repostQS));            
         }
         if (s_project != null) {
            functpreface.append(",ms.project ");
            if (groupby.length() > 0) groupby += ",";
            groupby += " ms.project ";
            dojoin = true;
            headers.append(genReportHeader("Project",   
                                           " ms.project desc", 
                                           repostURL, repostQS));            
         }
         
        /* No worky
         if (s_service != null && 
             s_service.equalsIgnoreCase("S_SERVICERECAP")) {
            
            Enumeration enum = listofservices.elements();
            while(enum.hasMoreElements()) {
            
               String s = (String)enum.nextElement();
               
               String lsvc = " SVCT_" + s;
               subselect.append(" left outer join ")
                        .append(metricservice)
                        .append(lsvc)
                        .append(" on mdt.key=")
                        .append(lsvc) 
                        .append(".key AND ")
                        .append(lsvc)
                        .append(".servicetype = '")
                        .append(s)
                        .append("' ");
               
               functpreface.append(",count(").append(lsvc)
                           .append(".servicetype) as svc_")
                           .append(s).append(" ");
               
               headers.append(genReportHeader(s,   
                                              " svc_" + s + " desc", 
                                              repostURL, repostQS));
            }
         }
        */
         
         if (s_service != null && 
             s_service.equalsIgnoreCase("S_SERVICERECAP")) {
            
            StringBuffer sb = new StringBuffer();
            
            Enumeration enum = listofservices.elements();
            String Scast = "count(CAST(NULL as VARCHAR(16))) as SVC";
            String Scount = "count(servicetype) as SVC";
            
            StringBuffer timeframe = new StringBuffer(" AND ");
            timeframe.append(" starttime >= '")
                .append((new java.sql.Timestamp(start.getTime())).toString());
            timeframe.append("' and  starttime <= '")
                .append((new java.sql.Timestamp(end.getTime())).toString())
                .append("' ");
            
            subselect.append(" left outer join (select ll.svckey");
            int j = 0;
            while(enum.hasMoreElements()) {
               String s = (String)enum.nextElement();
               
               subselect.append(",sum(ll.SVC").append(s)
                        .append(") as SVC").append(s);
               
               if (++j > 1) {
                  sb.append(" union all ");
               } else {
                  sb.append(" from table(");
               }
               sb.append(" select key as svckey");
               
               int i=0;
               Enumeration inenum = listofservices.elements();
               while(inenum.hasMoreElements()) {
                  String ins = (String)inenum.nextElement();
                  
                  sb.append(",");
                  
                  if (ins.equals(s)) {
                     sb.append(Scount).append(ins);
                  } else {
                     sb.append(Scast).append(ins);
                  }
               }
               
               sb.append(" from ").append(metricservice)
                  .append(" where servicetype='").append(s)
                  .append("' group by key");
                  
               functpreface.append(",").append("whew.SVC")
                           .append(s).append(" as svc_").append(s)
                           .append(" ");
               headers.append(genReportHeader(s,   
                                              " svc_" + s + " desc ", 
                                              repostURL, repostQS));
            }
            
            subselect.append(sb.toString())
               .append(") as ll group by ll.svckey order by ll.svckey)")
               .append(" as whew on whew.svckey = mdt.key ");
         }
         
         
         groupby = "";
      }
   
      if (groupby.length() > 0) groupby = " group by " + groupby;
      if (orderby.length() > 0) orderby = " order by " + orderby;
   
      String sql = functpreface.toString() + 
         fromclause.toString() +  (dojoin?join:"") + subselect.toString() +
         whereclause.toString() + wheremodifier.toString() +
         groupby.toString() + orderby.toString();
         
      c = null;
      try {
         c = dbconn.getConnection();
            
         System.out.println("About to: " + sql);
            
         java.sql.Statement stmt    = c.createStatement();
         java.sql.ResultSet results = stmt.executeQuery(sql);
         String header = 
            s_drill != null?"Tunnel/Service Report":"Summary Report";
         
         out.println("<p /><h1>" + header + "</h1><p />");
         out.println("<table border><tr>");
         out.println(headers.toString());
         out.println("</tr>");
         
         
         int gg = 0;
         while(results.next()) {
         
            int i = 1;
            
            out.println("<tr>");
            if (s_drill == null) {
              /* numrec, totdelt, avgdelt */
               out.println("<td>" + results.getInt(i++) + "</td>");
               
               int totdelt = results.getInt(i++);
               int tdays = totdelt/86400;
               totdelt  -= tdays *86400;
               int thr   = totdelt/3600;
               totdelt  -= thr * 3600;
               int tmin  = totdelt/60;
               totdelt  -= tmin * 60;
               int tsec  = totdelt;
               String tdelt = "" + tdays + ":" + thr + ":" + tmin + ":" + tsec;
               
               int avgdelt = results.getInt(i++);
               tdays = avgdelt/86400;
               avgdelt  -= tdays *86400;
               thr   = avgdelt/3600;
               avgdelt  -= thr * 3600;
               tmin  = avgdelt/60;
               avgdelt  -= tmin * 60;
               tsec  = avgdelt;
               String adelt = "" + tdays + ":" + thr + ":" + tmin + ":" + tsec;
               
               out.println("<td>" + tdelt   + "</td>");
               out.println("<td>" + adelt   + "</td>");
               
               if (s_user != null) {
                  out.println("<td>" + results.getString(i++) + "</td>");
               }
               if (s_company != null) {
                  out.println("<td>" + results.getString(i++) + "</td>");
               }
               if (s_service != null && 
                   s_service.equalsIgnoreCase("S_SERVICE")) {
                  out.println("<td>" + results.getString(i++) + "</td>");
               } 
               if (s_tofrom != null) {
                  out.println("<td>" + results.getLong(i++) + "</td>");
                  out.println("<td>" + results.getLong(i++) + "</td>");
               }
               
               if (s_email != null) {
                  out.println("<td>" + results.getString(i++) + "</td>");
               }
               if (s_rhost != null) {
                  out.println("<td>" + results.getString(i++) + "</td>");
               }
               if (s_first != null) {
                  out.println("<td>" + results.getString(i++) + "</td>");
               }
               if (s_last != null) {
                  out.println("<td>" + results.getString(i++) + "</td>");
               }
               if (s_country != null) {
                  out.println("<td>" + results.getString(i++) + "</td>");
               }
               if (s_state != null) {
                  out.println("<td>" + results.getString(i++) + "</td>");
               }
               if (s_project != null) {
                  out.println("<td>" + results.getString(i++) + "</td>");
               }
               if (s_service != null && 
                   s_service.equalsIgnoreCase("S_SERVICERECAP")) {
                  Enumeration enum = listofservices.elements();
                  while(enum.hasMoreElements()) {
                     enum.nextElement();
                     out.println("<td>" + results.getInt(i++) + "</td>");
                  }
               }
            } else {
            
               if (s_key != null) {
                  out.println("<td>" + results.getString(i++) + "</td>");
               } else {
                 // Just throw the key away if not wanted.
                  results.getString(i++);
               }
               
               int totdelt = results.getInt(i++);
               int tdays = totdelt/86400;
               totdelt  -= tdays *86400;
               int thr   = totdelt/3600;
               totdelt  -= thr * 3600;
               int tmin  = totdelt/60;
               totdelt  -= tmin * 60;
               int tsec  = totdelt;
               String tdelt = "" + tdays + ":" + thr + ":" + tmin + ":" + tsec;
               
              // totdelt
               out.println("<td>" + tdelt + "</td>");
         
               if (s_time != null) {
                  java.sql.Timestamp starttimev = results.getTimestamp(i++);
                  java.sql.Timestamp endtimev   = results.getTimestamp(i++);
                  out.println("<td>" + starttimev.toString() + "</td>");
                  out.println("<td>" + endtimev.toString() + "</td>");
               }
               if (s_company != null) {
                  out.println("<td>" + results.getString(i++) + "</td>");
               }
               if (s_user != null) {
                  out.println("<td>" + results.getString(i++) + "</td>");
               }
               if (s_email != null) {
                  out.println("<td>" + results.getString(i++) + "</td>");
               }
               if (s_rhost != null) {
                  out.println("<td>" + results.getString(i++) + "</td>");
               }
               if (s_first != null) {
                  out.println("<td>" + results.getString(i++) + "</td>");
               }
               if (s_last != null) {
                  out.println("<td>" + results.getString(i++) + "</td>");
               }
               if (s_tofrom != null) {
                  out.println("<td>" + results.getLong(i++) + "</td>");
                  out.println("<td>" + results.getLong(i++) + "</td>");
               }
         
               if (s_service != null && 
                   s_service.equalsIgnoreCase("S_SERVICE")) {
                  out.println("<td>" + results.getString(i++) + "</td>");
               }
         
               if (s_country != null) {
                  out.println("<td>" + results.getString(i++) + "</td>");
               }
               if (s_state != null) {
                  out.println("<td>" + results.getString(i++) + "</td>");
               }
               if (s_project != null) {
                  out.println("<td>" + results.getString(i++) + "</td>");
               }
               if (s_service != null && 
                   s_service.equalsIgnoreCase("S_SERVICERECAP")) {
                  Enumeration enum = listofservices.elements();
                  while(enum.hasMoreElements()) {
                     enum.nextElement();
                     out.println("<td>" + results.getInt(i++) + "</td>");
                  }
               }
            }
            
            out.println("</tr>");
            gg++;
         }
         System.out.println("Got " + gg + " records");
         
         results.close();
         stmt.close();
         
      } catch(Exception e) {
         out.println("<p />Error doing our business!<p />");
         e.printStackTrace(System.out);
         return;
      } finally {
         if (dbconn != null && c != null) dbconn.returnConnection(c);
      }
         
   }
   
   
   static private String genReportHeader1(String h, String ord, String repostURL) {
      return "<th><a href=\"" + repostURL + 
             "&orderby=" + URLEncoder.encode(ord) +
             "\">" + h + "</a></th>";
   }
   
   public static final int REPORT_OVERALL  = 1;
   public static final int REPORT_USER     = 2;
   public static final int REPORT_COMPANY  = 3;
   public static final int REPORT_PROJECT  = 4;
   public static final int REPORT_USERRECS = 5;
   public static final int REPORT_EDGEID   = 6;
   public static final int REPORT_SERVICE  = 7;
   public static final int REPORT_SERVICES = 8;
   static public void generateMetricsReport1(PrintWriter out, int rep, String orderby,
                                             String edgeid,
                                             String repostURL, Date start, Date end) {
   
      if (edgeid != null) rep = REPORT_EDGEID;
      
      if (true) {
         Connection c = null;
         DBConnection dbconn = DBSource.getDBConnection("EDODC");
         String dbInstance = "edesign";
         
         StringBuffer presql = new StringBuffer();
         
//         String whereclause = "starttime >= ? and endtime <= ?";
         String whereclause = 
            dbInstance + ".metricdt.starttime >= '" + 
            (new java.sql.Timestamp(start.getTime())).toString() + 
            "' and " + dbInstance + ".metricdt.starttime <= '" +
            (new java.sql.Timestamp(end.getTime())).toString() + "'";
            
         String q = null;
         
         String functpreface1 = "select count(*) as numrec,sum(" + dbInstance + ".metricdt.deltatime/1000) as totdelt,avg(" + dbInstance + ".metricdt.deltatime/1000) as avgdelt,avg(" + dbInstance + ".metricdt.toclient)/1024 as avgtoclient,avg(" + dbInstance + ".metricdt.fromclient)/1024 as avgfromclient";
         String functpreface2 = "sum(numodcsess) as totodc,sum(numodcmeet) as totmeet,sum(numdshsess) as totdsh,sum(numedusess) as totedu";
         String functpreface = functpreface1 + "," + functpreface2;
         
        /* Overall Summary */
        /* --------------- */
         String overall = 
            functpreface + 
            " from " + dbInstance + ".metricdt where " + 
            whereclause;
            
          String overallDefOrder = "";
          
        /* Summary by User/Company */
        /* ----------------------- */
         String user = 
            functpreface + 
            ",edgeid,company,country from " + dbInstance + ".metricdt where " + 
            whereclause + " group by edgeid,company,country";
            
          String userDefOrder = " order by company,edgeid,country";

          
        /* Summary by ServiceType  */
        /* ----------------------- */
          String stwhereclause = "starttime >= '" + 
             (new java.sql.Timestamp(start.getTime())).toString() + 
             "' and starttime <= '" +
             (new java.sql.Timestamp(end.getTime())).toString() + "'";
             
         String lfunctpreface1 = "select count(*) as numrec,sum(" + dbInstance + ".metricservice.deltatime/1000) as totdelt,avg(" + dbInstance + ".metricservice.deltatime/1000) as avgdelt,avg(" + dbInstance + ".metricservice.toclient)/1024 as avgtoclient,avg(" + dbInstance + ".metricservice.fromclient)/1024 as avgfromclient";
         
         String servicetype = lfunctpreface1 + 
            ",servicetype from " + dbInstance + ".metricservice where "+ 
            stwhereclause + " group by servicetype";
            
         String servicetypeDefOrder = " order by servicetype";
          
        /* Summary by Company */
        /* ------------------ */ 
         String company = 
            functpreface + 
            ",company,country from " + dbInstance + ".metricdt where " + 
            whereclause + " group by company, country";
            
         String companyDefOrder = " order by company, country";

        /* Summary by Project */
        /* ------------------ */ 
         String project = 
            functpreface + 
            ",project,company,country from " + dbInstance + ".metricdt left join " + dbInstance + ".metricservice on " + dbInstance + ".metricdt.key=" + dbInstance + ".metricservice.key where " + whereclause + " group by project,company,country";

         String projectDefOrder = " order by project";

        /* User Records */
        /* ------------ */ 
         String userrecs = 
            "select " + dbInstance + ".metricdt.deltatime/1000,(" + dbInstance + ".metricdt.toclient/1024),(" + dbInstance + ".metricdt.fromclient/1024),numodcsess,numodcmeet,numdshsess,numedusess,edgeid,project,company,country," + dbInstance + ".metricdt.starttime," + dbInstance + ".metricdt.endtime from " + dbInstance + ".metricdt left join " + dbInstance + ".metricservice on " + dbInstance + ".metricdt.key=" + dbInstance + ".metricservice.key where " + (edgeid!=null?"edgeid='" + edgeid + "' and ":"") + whereclause;

            
         String edgeidDefOrder = " order by " + dbInstance + ".metricdt.starttime asc";
         
         String extrah = "";
         String header = null;
         
         
         switch(rep) {
            case REPORT_OVERALL: // n/a
               header = "Overall Statistics";
               q       = overall;
               if (orderby == null) orderby = overallDefOrder;
               break;
            case REPORT_USER:    // edgeid, company, country
               header = "Report by Edgeid";
               q       = user;
               if (orderby == null) orderby = userDefOrder;
               extrah = genReportHeader1("Company", userDefOrder, repostURL) + 
                        genReportHeader1("Edge ID", 
                                        " order by edgeid,company,country", 
                                        repostURL) +
                        genReportHeader1("Country", 
                                        " order by country,edgeid,company", 
                                        repostURL);
               break;
            case REPORT_COMPANY: // company, country
               header = "Report by Company";
               q       = company;
               extrah  = 
                  genReportHeader1("Company", companyDefOrder, repostURL) +
                  genReportHeader1("Country", "order by country,company", 
                                  repostURL);
               if (orderby == null) orderby = companyDefOrder;
               break;
            case REPORT_PROJECT: // project, company, country
               header = "Report by Project";
               q = project;
               extrah = genReportHeader1("Project", 
                                        projectDefOrder, 
                                        repostURL) + 
                        genReportHeader1("Company", 
                                        "order by company,project", 
                                        repostURL) +
                        genReportHeader1("Country", 
                                        "order by country,company,project", 
                                        repostURL);
               if (orderby == null) orderby = projectDefOrder;
               break;
               
            case REPORT_SERVICE: // servicetype
               header = "Report Summary by ServiceType";
               q = servicetype;
               extrah = genReportHeader1("Service Type", 
                                        "orderby servicetype",
                                        repostURL);
               if (orderby == null) orderby = servicetypeDefOrder;
               break;
               
            case REPORT_USERRECS: // project, company, edgeid
               header = "Report of User Records";
               extrah = genReportHeader1("ID", 
                                        "orderby edgeid" + 
                                        dbInstance + ".metricdt.starttime asc",
                                        repostURL);
                                        
            case REPORT_EDGEID: // project, company, edgeid
               if (header == null) {
                  header = "Report for EdgeID '" + edgeid + "'";
               }
               q = userrecs;
               extrah += genReportHeader1("Start", 
                                        edgeidDefOrder, 
                                        repostURL) + 
                        genReportHeader1("End", 
                                        " order by " + 
                                        dbInstance + ".metricdt.endtime asc",
                                        repostURL) + 
                        genReportHeader1("Project", 
                                        projectDefOrder, 
                                        repostURL) + 
                        genReportHeader1("Company", 
                                        "order by company,project,country", 
                                        repostURL) +
                        genReportHeader1("Country", 
                                        "order by country,company,project", 
                                        repostURL);
               if (orderby == null) orderby = edgeidDefOrder;
               break;
            default:
               out.println("Unknown report value " + rep);
               return;
         }
         
         try {
            c = dbconn.getConnection();
            
            q = q + " " + orderby;
            
            java.sql.Statement stmt    = c.createStatement();
            
           //stmt.setTimestamp(1, new java.sql.Timestamp(start.getTime()));
           // stmt.setTimestamp(2, new java.sql.Timestamp(end.getTime()));
           

            if (DebugPrint.getLevel() > DebugPrint.INFO4) {
               out.println("Executing:<p />");
               out.println("<pre>\n" + q + "\n</pre>");
               out.println("<p />Rep = " + rep + "<p />");
            }
            
            java.sql.ResultSet results = stmt.executeQuery(q);
            
            out.println("<p /><h1>" + header + "</h1><p />");
            out.println("<table border><tr>");
            out.println(extrah);
            
            boolean urep = false;
            if (rep == REPORT_EDGEID || rep == REPORT_USERRECS) {
               urep = true;
               
               out.println(genReportHeader1("Elapsed",     
                                          " order by " + dbInstance + ".metricdt.deltaTime desc", 
                                           repostURL));
               out.println(genReportHeader1("ToClient (K)",   
                                           " order by " + dbInstance + ".metricdt.toclient desc", 
                                           repostURL));
               out.println(genReportHeader1("FromClient (K)", 
                                         " order by " + dbInstance + ".metricdt.fromclient desc", 
                                           repostURL));
               out.println(genReportHeader1("# ODC",          
                                           " order by numodcsess desc", 
                                           repostURL));
               out.println(genReportHeader1("# Meet",         
                                           " order by numodcmeet desc", 
                                           repostURL));
               out.println(genReportHeader1("# DSH",          
                                           " order by numdshsess desc", 
                                           repostURL));
               out.println(genReportHeader1("# EDU",          
                                           " order by numedusess desc", 
                                           repostURL));
            } else {
               out.println(genReportHeader1("# Recs",         
                                           " order by numrec desc", 
                                           repostURL));
               out.println(genReportHeader1("Total Elap",     
                                           " order by totdelt desc", 
                                           repostURL));
               out.println(genReportHeader1("Avg Elap",       
                                           " order by avgdelt desc", 
                                           repostURL));
               out.println(genReportHeader1("Avg ToClient (K)",   
                                           " order by avgtoclient desc", 
                                           repostURL));
               out.println(genReportHeader1("Avg FromClient (K)", 
                                           " order by avgfromclient desc", 
                                           repostURL));
               if (rep != REPORT_SERVICE) {
                  out.println(genReportHeader1("# ODC",          
                                              " order by totodc desc", 
                                              repostURL));
                  out.println(genReportHeader1("# Meet",         
                                              " order by totmeet desc", 
                                              repostURL));
                  out.println(genReportHeader1("# DSH",          
                                              " order by totdsh desc", 
                                              repostURL));
                  out.println(genReportHeader1("# EDU",          
                                              " order by totedu desc", 
                                              repostURL));
               }
            }
            out.println("</tr>");
            
            while(results.next()) {
            
               int i = 1;
               
               int nrec=0, avgdelt=0;
               if (!urep) nrec      = results.getInt(i++);
               int totdelt          = results.getInt(i++);
               if (!urep) avgdelt   = results.getInt(i++);
               
               long toclient = results.getLong(i++);
               long frclient = results.getLong(i++);
               
               int numodc=0;
               int nummeet=0;
               int numdsh=0;
               int numedu=0;
               
               if (rep != REPORT_SERVICE) {
                  numodc    = results.getInt(i++);
                  nummeet   = results.getInt(i++);
                  numdsh    = results.getInt(i++);
                  numedu    = results.getInt(i++);
               }
               
               String servicetypev  = null;
               String edgeidv  = null;
               String projectv = null;
               String companyv = null;
               String countryv = null;
               java.sql.Timestamp starttimev = null;
               java.sql.Timestamp endtimev   = null;
               
               out.println("<tr>");
               switch(rep) {
                  case REPORT_OVERALL: // n/a
                     break;
                  case REPORT_USER:    // edgeid, company
                     edgeidv  = results.getString(i++);
                     companyv = results.getString(i++);
                     countryv = results.getString(i++);
                     out.println("<td>" + companyv + "</td>");

                     out.println("<td><a href=\"" + repostURL + 
                               "&orderby="+URLEncoder.encode(edgeidDefOrder) +
                               "&edgeid=" + URLEncoder.encode(edgeidv) +
                               "&Report=" + REPORT_EDGEID +
                                 "\">" + edgeidv + "</a></td>");
                     out.println("<td>" + countryv + "</td>");
                     break;
                  case REPORT_COMPANY: // company
                     companyv = results.getString(i++);
                     countryv = results.getString(i++);
                     out.println("<td>" + companyv + "</td>");
                     out.println("<td>" + countryv + "</td>");
                     break;
                  case REPORT_PROJECT: // project, company
                     projectv = results.getString(i++);
                     companyv = results.getString(i++);
                     countryv = results.getString(i++);
                     out.println("<td>" + projectv + "</td>");
                     out.println("<td>" + companyv + "</td>");
                     out.println("<td>" + countryv + "</td>");
                     break;
                  case REPORT_SERVICE: // servicetype
                     servicetype = results.getString(i++);
                     out.println("<td>" + servicetype + "</td>");
                     break;
                  case REPORT_USERRECS: // edgeid
                  case REPORT_EDGEID:   // start, end, project, company
                     edgeidv = results.getString(i++);
                     projectv = results.getString(i++);
                     companyv = results.getString(i++);
                     countryv = results.getString(i++);
                     starttimev = results.getTimestamp(i++);
                     endtimev = results.getTimestamp(i++);
                     if (rep == REPORT_USERRECS) {
                        out.println("<td>" + edgeidv + "</td>");
                     }
                     out.println("<td>" + starttimev.toString() + "</td>");
                     out.println("<td>" + endtimev.toString() + "</td>");
                     out.println("<td>" + projectv + "</td>");
                     out.println("<td>" + companyv + "</td>");
                     out.println("<td>" + countryv + "</td>");
                     break;
               }
               
               int tdays = totdelt/86400;
               totdelt  -= tdays *86400;
               int thr   = totdelt/3600;
               totdelt  -= thr * 3600;
               int tmin  = totdelt/60;
               totdelt  -= tmin * 60;
               int tsec  = totdelt;
               String tdelt = "" + tdays + ":" + thr + ":" + tmin + ":" + tsec;
                              
               if (!urep) out.println("<td>" + nrec     + "</td>");
               
               out.println("<td>" + tdelt    + "</td>");
               
               if (!urep) {
                  int adays = avgdelt/86400;
                  avgdelt  -= adays *86400;
                  int ahr   = avgdelt/3600;
                  avgdelt  -= ahr * 3600;
                  int amin  = avgdelt/60;
                  avgdelt  -= amin * 60;
                  int asec  = avgdelt;
                  String adelt = "" + adays + ":" + ahr + ":" + amin + 
                                                          ":" + asec;
                  
                  out.println("<td>" + adelt    + "</td>");
               }
               
               out.println("<td>" + toclient + "</td>");
               out.println("<td>" + frclient + "</td>");
               
               if (rep != REPORT_SERVICE) {
                  out.println("<td>" + numodc   + "</td>");
                  out.println("<td>" + nummeet  + "</td>");
                  out.println("<td>" + numdsh   + "</td>");
                  out.println("<td>" + numedu   + "</td>");
               }
               
               out.println("</tr>");
            }
            out.println("</table>");

            stmt.close();
            results.close();
         } catch(Throwable t) {
            out.println("Error generating Metrics report!");
            t.printStackTrace(out);
         } finally {
            if (dbconn != null && c != null) dbconn.returnConnection(c);
         }
      } else {
         out.println("Can't generate report. Not setup for DB2 metrics");
      }
   }
   
   static protected void handleReport(Properties prop, 
                                      javax.servlet.http.HttpServletRequest req, 
                                      javax.servlet.http.HttpServletResponse res)
      throws IOException {
                     
      PrintWriter out = res.getWriter();
      out.print(repgen_matter_head);
      
      out.println(genUsageReportHtml(DesktopServlet.generateCipher("report", 
                                                                   60*60), 
                                     prop, null, null));
      
      out.print(repgen_matter_tail);
      out.close();
   }
   
}   
