package oem.edge.ed.odc.cntl;
import com.ibm.as400.webaccess.common.*;
import oem.edge.ed.odc.util.ReloadingProperty;
import java.util.*;
import javax.servlet.http.*;
import javax.servlet.*;
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

public class FE {

   static String servletcontextpath = "";
   static String replacevalue       = "/servlet/oem/edge/ed/odc";
   static ReloadingProperty deskprops      = null;
   static void setContextPath(String s) { servletcontextpath = s; }
   static void setProperties(ReloadingProperty p) { deskprops = p; }
   
   private static StringBuffer fixContextPath(StringBuffer sb, 
                                javax.servlet.http.HttpServletResponse res) {
                                
     //System.out.println("servletcontextpath = " + servletcontextpath);
      if (servletcontextpath.length() != 0) {
         String s = sb.toString();
         StringBuffer retsb = new StringBuffer();
         int idx;
         while((idx = s.indexOf(replacevalue)) >= 0) {
            String use_servletcontextpath = servletcontextpath;
            int len = replacevalue.length();
            
           // If it already HAS the contextpath added, don't add it again
            int sidx = s.indexOf(servletcontextpath);
            if (sidx >=0 && sidx + servletcontextpath.length() == idx) {
               use_servletcontextpath = "";
               idx = sidx;
               len = replacevalue.length() + servletcontextpath.length();
            }
            
            if (idx > 0) {
               retsb.append(s.substring(0, idx));
            }
            
           // Stop at " or '
            int idx2 = s.indexOf('"',  idx);
            int idx3 = s.indexOf('\'', idx);
            if (idx3 >= 0 && (idx2 < 0 || idx3 < idx2)) idx2=idx3;
            
            if (idx2 > 0) {
               String ls = use_servletcontextpath + s.substring(idx, idx2);
               String rls = res.encodeURL(ls);
               retsb.append(rls);
               
              //System.out.println("Encoding [" + ls + "] = [" + rls + "]");
               
               s = s.substring(idx2);
            } else {
               retsb.append(servletcontextpath);
               retsb.append(replacevalue);
               try {
                  s = s.substring(idx+len);
               } catch(Exception ee) {}
            }
         } 
         retsb.append(s);
         sb = retsb;
      }
      return sb;
   }
   
   static private void injectExpiration(StringBuffer sb, long expires, 
                                        boolean twoline) {
      java.text.DateFormatSymbols dfs = new java.text.DateFormatSymbols();
      Calendar cal = Calendar.getInstance();
      if (expires != 0) {
         cal.setTime(new Date(expires));
      } else {
         Date d = new Date();
         cal.setTime(new Date());
         cal.add(Calendar.DATE, 5);
         cal.set(Calendar.HOUR_OF_DAY, 18);
         cal.set(Calendar.MINUTE, 0);
      }
      
      int day   = cal.get(Calendar.DAY_OF_MONTH);
      int month = cal.get(Calendar.MONTH);
      int year  = cal.get(Calendar.YEAR);
      int hour  = cal.get(Calendar.HOUR_OF_DAY);
      int min   = cal.get(Calendar.MINUTE);
      
      String dfsarr[] = dfs.getShortMonths();
      
      if (twoline) {
         sb.append("<b>Date: </b>");
      }
      
      int i;
      sb.append("<select name=\"Month\">\n");
      for(i=0; i < 12; i++) {
         sb.append("<option value=\"" + i + "\" ");
         if (month == i) sb.append("selected");
         sb.append(">").append(dfsarr[i]).append("\n");
      }
      sb.append("</select>\n<select name=\"Day\">\n");
      for(i=1; i < 31; i++) {
         sb.append("<option value=\"" + i + "\" ");
         if (day == i) sb.append("selected");
         sb.append(">").append("" + i).append("\n");
      }
      sb.append("</select>\n<select name=\"Year\">\n");
      for(i=2000; i < 2008; i++) {
         sb.append("<option value=\"" + i + "\" ");
         if (year == i) sb.append("selected");
         sb.append(">").append("" + i).append("\n");
      }
      sb.append("</select>\n");
      
      if (twoline) {
         sb.append("<br /><b>Time: </b>");
      }
      
      sb.append("<select name=\"Hour\">\n");
      for(i=0; i < 24; i++) {
         sb.append("<option value=\"" + i + "\" ");
         if (hour == i) sb.append("selected");
         sb.append(">");
         if (i < 10) sb.append("0");
         sb.append(""+i).append("\n");
      }
      sb.append("</select>\n<b>:</b><select name=\"Min\">\n");
      for(i=0; i < 60; i++) {
         sb.append("<option value=\"" + i + "\" ");
         if (min == i) sb.append("selected");
         sb.append(">");
         if (i < 10) sb.append("0");
         sb.append("" + i).append("\n");
      }
      sb.append("</select>\n");
   }
   
   public static String addId(String errormsg, 
                              javax.servlet.http.HttpServletResponse res) {
      StringBuffer sb = new StringBuffer();
      sb.append(deskprops.getProperty("header_title_pre", header_title_pre));
      sb.append("Add new Login ID");
      sb.append(deskprops.getProperty("header_title_post", header_title_post));
      sb.append(deskprops.getProperty("masthead", masthead));
      sb.append(deskprops.getProperty("actionpage_nav", actionpage_nav));
      sb.append(deskprops.getProperty("pagetitle_pre", pagetitle_pre));
      sb.append(DesktopServlet.getDomainIPReal());
      sb.append(DesktopServlet.getBuildInfo());
      sb.append("Add new Login ID");
      sb.append(deskprops.getProperty("pagetitle_post", pagetitle_post));
      sb.append(deskprops.getProperty("contentspace_head", contentspace_head));
      
      if (errormsg != null) {
         sb.append(deskprops.getProperty("errorHead", errorHead));
         sb.append(errormsg);
         sb.append(deskprops.getProperty("errorTail", errorTail));
      }
      sb.append(deskprops.getProperty("addid_content", addid_content));
      sb.append(deskprops.getProperty("contentspace_tail", contentspace_tail));
      sb.append(deskprops.getProperty("footer", footer));
      
      sb = fixContextPath(sb, res);
      return sb.toString();
   }
   
   public static String multiCmd(Enumeration ids, String errormsg, 
                               javax.servlet.http.HttpServletResponse res) {
                               
      StringBuffer sb = new StringBuffer();
      sb.append(deskprops.getProperty("header_title_pre", header_title_pre));
      sb.append("Modify Login IDs");
      sb.append(deskprops.getProperty("header_title_post", header_title_post));
      sb.append(deskprops.getProperty("masthead", masthead));
      sb.append(deskprops.getProperty("actionpage_nav", actionpage_nav));
      sb.append(deskprops.getProperty("pagetitle_pre", pagetitle_pre));
      sb.append(DesktopServlet.getDomainIPReal());
      sb.append(DesktopServlet.getBuildInfo());
      sb.append("Modify Login IDs");
      sb.append(deskprops.getProperty("pagetitle_post", pagetitle_post));
      sb.append(deskprops.getProperty("contentspace_head", contentspace_head));
      
      if (errormsg != null) {
         sb.append(deskprops.getProperty("errorHead", errorHead));
         sb.append(errormsg);
         sb.append(deskprops.getProperty("errorTail", errorTail));
      }
      
      sb.append(deskprops.getProperty("multicmd_content_1", multicmd_content_1));
      
      injectExpiration(sb, 0, true);
      
      sb.append(deskprops.getProperty("multicmd_content_2", multicmd_content_2));
      int i = 1;
      while(ids.hasMoreElements()) {
         String v = (String)ids.nextElement();
         sb.append("<option");
         sb.append(" value=\"");
         sb.append(v);
         sb.append("\">");
         sb.append(v);
         sb.append("</option>\n");
      }
      
      sb.append(deskprops.getProperty("multicmd_content_3", multicmd_content_3));
      
      sb.append(deskprops.getProperty("contentspace_tail", contentspace_tail));
      sb.append(deskprops.getProperty("footer", footer));
      
      sb = fixContextPath(sb, res);
      return sb.toString();
   }
   
   public static String editId(Enumeration ids, String errormsg, 
                               javax.servlet.http.HttpServletResponse res) {
      StringBuffer sb = new StringBuffer();
      sb.append(deskprops.getProperty("header_title_pre", header_title_pre));
      sb.append("Edit Login ID");
      sb.append(deskprops.getProperty("header_title_post", header_title_post));
      sb.append(deskprops.getProperty("masthead", masthead));
      sb.append(deskprops.getProperty("actionpage_nav", actionpage_nav));
      sb.append(deskprops.getProperty("pagetitle_pre", pagetitle_pre));
      sb.append(DesktopServlet.getDomainIPReal());
      sb.append(DesktopServlet.getBuildInfo());
      sb.append("Edit Login ID");
      sb.append(deskprops.getProperty("pagetitle_post", pagetitle_post));
      sb.append(deskprops.getProperty("contentspace_head", contentspace_head));
      
      if (errormsg != null) {
         sb.append(deskprops.getProperty("errorHead", errorHead));
         sb.append(errormsg);
         sb.append(deskprops.getProperty("errorTail", errorTail));
      }
      sb.append(editid_content1);
      
      if (ids != null) {
         sb.append(deskprops.getProperty("editid_content_select",
                                         editid_content_select));
         while(ids.hasMoreElements()) {
            String v = (String)ids.nextElement();
            sb.append("<option value=\"");
            sb.append(v);
            sb.append("\">");
            sb.append(v);
         }
         sb.append("</select></td>\n");
      } else {
         sb.append(deskprops.getProperty("editid_content_text",
                                         editid_content_text));
      }
      
      sb.append(deskprops.getProperty("editid_content2", editid_content2));
      
      sb.append(deskprops.getProperty("contentspace_tail", contentspace_tail));
      sb.append(deskprops.getProperty("footer", footer));
      
      sb = fixContextPath(sb, res);
      return sb.toString();
   }
   
   public static String removeId(String errormsg, 
                                 javax.servlet.http.HttpServletResponse res) {
      StringBuffer sb = new StringBuffer();
      sb.append(deskprops.getProperty("header_title_pre", header_title_pre));
      sb.append("Remove Existing Login ID");
      sb.append(deskprops.getProperty("header_title_post", header_title_post));
      sb.append(deskprops.getProperty("masthead", masthead));
      sb.append(deskprops.getProperty("actionpage_nav", actionpage_nav));
      sb.append(deskprops.getProperty("pagetitle_pre", pagetitle_pre));
      sb.append(DesktopServlet.getDomainIPReal());
      sb.append(DesktopServlet.getBuildInfo());
      sb.append("Remove Existing Login ID");
      sb.append(deskprops.getProperty("pagetitle_post", pagetitle_post));
      sb.append(deskprops.getProperty("contentspace_head", contentspace_head));
      
      if (errormsg != null) {
         sb.append(deskprops.getProperty("errorHead", errorHead));
         sb.append(errormsg);
         sb.append(deskprops.getProperty("errorTail", errorTail));
      }
      sb.append(removeid_content);
      sb.append(contentspace_tail);
      sb.append(footer);
      
      sb = fixContextPath(sb, res);
      return sb.toString();
   }
   
   public static String changePassword(String errormsg, 
                                 javax.servlet.http.HttpServletResponse res) {
      StringBuffer sb = new StringBuffer();
      sb.append(deskprops.getProperty("header_title_pre", header_title_pre));
      sb.append("Change Login Password");
      sb.append(deskprops.getProperty("header_title_post", header_title_post));
      sb.append(deskprops.getProperty("masthead", masthead));
      sb.append(deskprops.getProperty("actionpage_nav", actionpage_nav));
      sb.append(deskprops.getProperty("pagetitle_pre", pagetitle_pre));
      sb.append(DesktopServlet.getDomainIPReal());
      sb.append(DesktopServlet.getBuildInfo());
      sb.append("Change Login Password");
      sb.append(deskprops.getProperty("pagetitle_post", pagetitle_post));
      sb.append(deskprops.getProperty("contentspace_head", contentspace_head));
      
      if (errormsg != null) {
         sb.append(deskprops.getProperty("errorHead", errorHead));
         sb.append(errormsg);
         sb.append(deskprops.getProperty("errorTail", errorTail));
      }
      sb.append(deskprops.getProperty("changepw_content", changepw_content));
      sb.append(deskprops.getProperty("contentspace_tail", contentspace_tail));
      sb.append(deskprops.getProperty("footer", footer));
      
      sb = fixContextPath(sb, res);
      return sb.toString();
   }
   
   public static String loginInfo(String errormsg, String redirect, 
                                  javax.servlet.http.HttpServletResponse res) {
      StringBuffer sb = new StringBuffer();
      sb.append(deskprops.getProperty("header_title_pre", header_title_pre));
      sb.append(deskprops.getProperty("signin_title", signin_title));
      sb.append(deskprops.getProperty("header_title_post", header_title_post));
      sb.append(deskprops.getProperty("masthead_login", masthead_login));
      sb.append(deskprops.getProperty("signin_nav", signin_nav));
      sb.append(deskprops.getProperty("pagetitle_pre", pagetitle_pre));
      sb.append(DesktopServlet.getDomainIPReal());
      sb.append(DesktopServlet.getBuildInfo());
      sb.append(deskprops.getProperty("signin_pagetitle", signin_pagetitle));
      sb.append(deskprops.getProperty("pagetitle_post", pagetitle_post));
      sb.append(deskprops.getProperty("contentspace_head", contentspace_head));
      
      if (errormsg != null) {
         sb.append(deskprops.getProperty("errorHead", errorHead));
         sb.append(errormsg);
         sb.append(deskprops.getProperty("errorTail", errorTail));
      }
      sb.append(deskprops.getProperty("signin_content1", signin_content1));
      sb.append(deskprops.getProperty("use_pilot_id", use_pilot_id));
      sb.append(deskprops.getProperty("signin_content2", signin_content2));
      if (redirect != null) {
         sb.append("<input type=\"hidden\" name=\"redirect\" value=\"");
         sb.append(redirect);
         sb.append("\" />\n");
      }
      sb.append(deskprops.getProperty("signin_content3", signin_content3));
      sb.append(deskprops.getProperty("contentspace_tail", contentspace_tail));
      sb.append(deskprops.getProperty("footer", footer));
      
      sb = fixContextPath(sb, res);
      return sb.toString();
   }
   
   public static String actionPage(ConfigObject co, boolean edgeLogin,
                                   String errormsg, 
                                   javax.servlet.http.HttpServletResponse res) {
      StringBuffer sb = new StringBuffer();
      sb.append(deskprops.getProperty("header_title_pre", header_title_pre));
      sb.append(deskprops.getProperty("actionpage_title", actionpage_title));
      sb.append(deskprops.getProperty("header_title_post", header_title_post));
      sb.append(deskprops.getProperty("masthead", masthead));
      sb.append(deskprops.getProperty("actionpage_nav", actionpage_nav));
      sb.append(deskprops.getProperty("pagetitle_pre", pagetitle_pre));
      sb.append(DesktopServlet.getDomainIPReal());
      sb.append(DesktopServlet.getBuildInfo());
      sb.append(deskprops.getProperty("actionpage_pagetitle", 
                                      actionpage_pagetitle));
      sb.append(deskprops.getProperty("pagetitle_post", pagetitle_post));
      sb.append(deskprops.getProperty("contentspace_head", contentspace_head));
      
      if (errormsg != null) {
         sb.append(deskprops.getProperty("errorHead", errorHead));
         sb.append(errormsg);
         sb.append(deskprops.getProperty("errorTail", errorTail));
      }
      
      sb.append(deskprops.getProperty("content_bar_pre", content_bar_pre));
      sb.append(deskprops.getProperty("content_bar", "Design Services"));
      sb.append(deskprops.getProperty("content_bar_post", content_bar_post));
            
      String last  = co.getProperty("last");
      String first = co.getProperty("first");
      String whatAreYouCalled = null;
      if (first != null && last != null) {
         whatAreYouCalled = first + " " + last;
      } else if (first != null) {
         whatAreYouCalled = first;
      }
      
      sb.append(deskprops.getProperty("actionpage_main1", actionpage_main1));
      sb.append(deskprops.getProperty("actionpage_main2_hosting",
                                      actionpage_main2_hosting));
      sb.append(deskprops.getProperty("actionpage_main2_collab", 
                                      actionpage_main2_collab));
      sb.append(deskprops.getProperty("actionpage_main2_meetings", 
                          actionpage_main2_meetings));
      sb.append(deskprops.getProperty("actionpage_main2_dropbox", 
                                      actionpage_main2_dropbox));
      sb.append(deskprops.getProperty("actionpage_main2_grid", 
                                      actionpage_main2_grid));
      
      boolean isadmin   = 
         co.getProperty("admin", "").equalsIgnoreCase("true");
      boolean isstatus  = 
         co.getProperty("status", "").equalsIgnoreCase("true");
      boolean isreport  = 
         co.getProperty("report", ""). equalsIgnoreCase("true");
      boolean isrepdbox = 
         co.getProperty("reportdbox", "").equalsIgnoreCase("true");
         
      if (co.getProperty("userid", "").toUpperCase().indexOf("DEMO") < 0 &&
          (!edgeLogin || isadmin || isstatus || isreport || isrepdbox)) {
         
         sb.append(deskprops.getProperty("actionpage_admin1",
                                         actionpage_admin1));
                                                     
         if (!edgeLogin) {
            sb.append(deskprops.getProperty("actionpage_admin_pw",
                                            actionpage_admin_pw));
         }
      
         if (isadmin) {
            sb.append(deskprops.getProperty("actionpage_admin_usermanip",
                                            actionpage_admin_usermanip));
         }
         
         if (isadmin || isstatus) {
            sb.append(deskprops.getProperty("actionpage_admin_status",
                                            actionpage_admin_status));
         }
         
         if (isadmin || isreport) {
            sb.append(deskprops.getProperty("actionpage_admin_report",
                                            actionpage_admin_report));
         }
         
         if (isadmin || isrepdbox) {
            sb.append(
              deskprops.getProperty("actionpage_admin_reportdbox", 
                        actionpage_admin_reportdbox));
         }
      }
         
      sb.append(deskprops.getProperty("actionpage_main3", actionpage_main3));
      
      sb.append(deskprops.getProperty("contentspace_tail", contentspace_tail));
      sb.append(deskprops.getProperty("footer", footer));
      
      sb = fixContextPath(sb, res);
      return sb.toString();
   }
   
   public static String editIdInfo(ConfigSection co, String errormsg, 
                                 javax.servlet.http.HttpServletResponse res) {
      StringBuffer sb = new StringBuffer();
      
      String user, email, password, company, country, state, projects, creator;
      String first, last, canloginchecked, expireschecked;
      long expires;
      
      user     = co.getProperty("userid", "");
      email    = co.getProperty("email", "");
      company  = co.getProperty("company", "");
      country  = co.getProperty("country", "");
      state    = co.getProperty("state", "");
      projects = co.getProperty("projects", "");
      last     = co.getProperty("last", "");
      first    = co.getProperty("first", "");
      expires  = co.getLongProperty("expires", 0);
      creator  = co.getProperty("creator", "unspecified");
      
      String tmp;
      tmp = co.getProperty("loginallowed", "true");
      if (tmp != null && !tmp.equalsIgnoreCase("false")) {
         canloginchecked = "checked";
      } else {
         canloginchecked = "";
      }
      
      boolean isadmin   = 
         co.getProperty("admin", "").equalsIgnoreCase("true");
      boolean isstatus  = 
         co.getProperty("status", "").equalsIgnoreCase("true");
      boolean isreport  = 
         co.getProperty("report", ""). equalsIgnoreCase("true");
      boolean isrepdbox = 
         co.getProperty("reportdbox", "").equalsIgnoreCase("true");
      
      if (expires != 0) {
         expireschecked = "checked";
      } else {
         expireschecked = "";
      }
      
      sb.append(deskprops.getProperty("header_title_pre", header_title_pre));
      sb.append("Edit user information");
      sb.append(deskprops.getProperty("header_title_post", header_title_post));
      sb.append(deskprops.getProperty("masthead", masthead));
      sb.append(deskprops.getProperty("actionpage_nav", actionpage_nav));
      sb.append(deskprops.getProperty("pagetitle_pre", pagetitle_pre));
      sb.append(DesktopServlet.getDomainIPReal());
      sb.append(DesktopServlet.getBuildInfo());
      sb.append("Edit user information");
      sb.append(deskprops.getProperty("pagetitle_post", pagetitle_post));
      sb.append(deskprops.getProperty("contentspace_head", contentspace_head));
      
      if (errormsg != null) {
         sb.append(deskprops.getProperty("errorHead", errorHead));
         sb.append(errormsg);
         sb.append(deskprops.getProperty("errorTail", errorTail));
      }
      
      sb.append(deskprops.getProperty("content_bar_pre", content_bar_pre));
      sb.append(deskprops.getProperty("content_bar", "Design Services"));
      sb.append(deskprops.getProperty("content_bar_post", content_bar_post));
            
      sb.append(deskprops.getProperty("editinfo_main_head", editinfo_main_head));
      
      sb.append("<p>Modify Information for Login ID <b>" + user + "</b>\n" +
                " <font color=\"red\"> (required fields</font>)\n<p />\n");
                
      sb.append("<form method=\"POST\" \n" +
                "action=\"/servlet/oem/edge/ed/odc/desktop/" +
                "frontpageadmin?op=editid3\">\n" +
                "<table border = 1 rules=\"none\" frame=\"border\">\n");
                
     // User
      sb.append("<tr>");
      sb.append("<th align=\"right\"><font color=\"red\">Login ID</font>:</th><td><label id=\"a\"><input id=\"a\" name=\"userid\" type=\"hidden\" value=\"");
      sb.append(user + "\" />" + user + "</label></td>\n");
      
     // Password
      sb.append("<th align=\"right\">Password:</th><td><label id=\"a\"><input id=\"a\" name=\"password\" type=\"password\" maxlength=40 value=\"");
      sb.append("[enter password here]");
      sb.append("\" /></label></td>");
      sb.append("</tr>");
   
     // First
      sb.append("<tr>");
      sb.append("<th align=\"right\">First name:</th><td><label id=\"a\"><input id=\"a\" name=\"first\" type=\"text\" maxlength=40 value=\"");
      sb.append(first);
      sb.append("\"/></label></td>\n");
      
     // Last
      sb.append("<th align=\"right\">Last name:</th><td><label id=\"a\"><input id=\"a\" name=\"last\" type=\"text\" maxlength=40 value=\"");
      sb.append(last);
      sb.append("\"/></label></td>\n");
      sb.append("</tr>");
                
     // Admin & Login checkboxes
      sb.append("<tr>");
      sb.append("<th align=\"right\">Admin Authority:</th><td><input id=\"a\" name=\"admin\" type=\"checkbox\" " + (isadmin?"checked":"") + "/></td>");
      sb.append("<th align=\"right\">Can Login:</th><td><input id=\"a\" name=\"canlogin\" type=\"checkbox\" " + canloginchecked + "/></td>");
      sb.append("</tr>");
      
     // Report, DboxReport
      sb.append("<tr>");
      sb.append("<th align=\"right\">TunnelReports:</th><td><input id=\"a\" name=\"report\" type=\"checkbox\" " + (isreport?"checked":"") + "/></td>");
      sb.append("<th align=\"right\">DropboxReports:</th><td><input id=\"a\" name=\"reportdbox\" type=\"checkbox\" " +(isrepdbox?"checked":"")+ "/></td>");
      sb.append("</tr>");
      
     // TunnelStatus
      sb.append("<tr>");
      sb.append("<th align=\"right\">TunnelStatus:</th><td><input id=\"a\" name=\"status\" type=\"checkbox\" " + (isstatus?"checked":"") + "/></td>");
      sb.append("</tr>");
      
     // Company
      sb.append("<tr>");
      sb.append("<th align=\"right\"><font color=\"red\">Company Name</font>:</th><td><label id=\"a\"><input id=\"a\" name=\"company\" type=\"text\" maxlength=40 value=\"");
      sb.append(company);
      sb.append("\" /></label></td>");
                
     // Email
      sb.append("<th align=\"right\">E-Mail Address:</th><td><label id=\"a\"><input id=\"a\" name=\"email\" type=\"text\" maxlength=80 value=\"");
      sb.append(email);
      sb.append("\" /></label></td>");
      sb.append("</tr>");
      
     // State
      sb.append("<tr>");
      sb.append("<th align=\"right\">State:</th><td><label id=\"a\"><input id=\"a\" name=\"state\" type=\"text\" maxlength=40 value=\"");
      sb.append(state);
      sb.append("\" /></label></td>");
      
     // Country
      sb.append("<th align=\"right\"><font color=\"red\">Country</font>:</th><td><label id=\"a\"><input id=\"a\" name=\"country\" type=\"text\" maxlength=40 value=\"");
      sb.append(country);
      sb.append("\" /></label></td>");
      sb.append("</tr>");
      
     // Projects
      sb.append("<tr>");
      sb.append("<th align=\"right\">Projects:</th><td><label id=\"a\"><input id=\"a\" name=\"projects\" type=\"text\" maxlength=80 value=\"");
      sb.append(projects);
      sb.append("\" /></label></td>");
      
     // Creator
      sb.append("<th align=\"right\">Creator:</th><td><i>");
      sb.append(creator);
      sb.append("</i></td>");
      sb.append("</tr>");

     // Expiration
      sb.append("<tr>");
      sb.append("<th align=\"right\"><input id=\"a\" name=\"expires\" type=\"checkbox\" " + expireschecked + "/>Expires:</th><td colspan=\"3\">");
      
      injectExpiration(sb, expires, false);
      sb.append("</td>\n");
      
      sb.append("<tr><td colspan=\"4\" BGCOLOR=\"#666666\"></td></tr>");
      sb.append("<tr>");
      sb.append("<td headers=\"\" colspan=\"4\" valign=\"middle\" align=\"middle\"><input id=\"edit\" type=\"image\" name=\"edit\" border=\"0\" src=\"//www.ibm.com/i/v11/buttons/continue.gif\" width=\"120\" height=\"21\" alt=\"Continue\"></td>\n");
      sb.append("</tr>");
      sb.append("</table></form>");
      
      sb.append(editinfo_main_tail);
      
      sb.append(contentspace_tail);
      sb.append(footer);
      
      sb = fixContextPath(sb, res);
      return sb.toString();
}   
   
   static final String actionpage_pagetitle = 
                       "IBM Engineering &amp; Technology Services\n";
   
   static final String signin_pagetitle = "*PILOT* signin\n";
   
   static final String actionpage_title = 
                       "IBM Engineering &amp; Technology Services | Home page";

   static final String signin_title = 
                       "Sign In: IBM Engineering &amp; Technology Services";

                       
   static final String editinfo_main_head = 
"                <tr valign=\"top\">\n" +
"                  <td width=\"10\"> </td>\n" +
"                  <td width=\"550\">\n";

   static final String editinfo_main_tail = 
"                </tr>\n";
                       
   
   static final String actionpage_main1 = 
"                <tr valign=\"top\">\n" +
"                  <td width=\"10\"> </td>\n" +
"                  <td width=\"550\">\n" +
"                  <p><font size=\"-1\"><br />\n" +
"                  </font></p>\n";

   static final String actionpage_main1_intro = 
"                  <p><font size=\"-1\">" + 
"                  These services are designed to allow remote\n" +
"                  design resources in real\n" +
"                  time.  They will\n" +
"help you decrease design cycle time, reduce\n" +
"                  travel expenses and guesswork,\n" +
"                  and \n" +
"reach your colleagues and design environment\n" +
"                  more effectively. </font></p>\n";

   static final String actionpage_main1_firsttimeusers = 
"                  <p><font size=\"-1\">First time users!\n" +
"<a onclick=\"window.open('/servlet/oem/edge/ed/odc/desktop/frontpagelink?op=CON','','scrollbars=1,statusbar=1,width=436,height=425,left=387,top=207'); return false;\" target=\"new\" href=\"/servlet/oem/edge/ed/odc/desktop/frontpagelink?op=CON\">Prepare your PC using the Web conferences Connectivity test</a></font></p>\n" +
"                  <font size=\"-1\">This will prepare your machine to participate in web conferences\n" +
"                  You should see a confirmation popup.</font>\n";

static final String actionpage_admin1 =
"                  <p><font size=\"-1\"><b>User ID Administration</b>\n" +
"                  </font></p>\n";

static final String actionpage_admin_pw =
"                  <font size=\"-1\"><b><!-- changepw --></b><img border=\"0\" width=\"18\" height=\"18\" src=\"//www.ibm.com/i/v11/icons/popup.gif\" alt=\"IBM\" />\n" +
"<a href=\"/servlet/oem/edge/ed/odc/desktop/frontpageadmin?op=pw1\">Change Password</a></font><br />\n";

static final String actionpage_admin_usermanip = 
"                  <font size=\"-1\"><b><!-- Adminfunctions --></b><img border=\"0\" width=\"18\" height=\"18\" src=\"//www.ibm.com/i/v11/icons/popup.gif\" align=\"top\" alt=\"IBM\" />\n" +
"<a href=\"/servlet/oem/edge/ed/odc/desktop/frontpageadmin?op=addid1\">Add ID</a></font><br />\n" +
"                  <font size=\"-1\"><b><!-- Adminfunctions --></b><img border=\"0\" width=\"18\" height=\"18\" src=\"//www.ibm.com/i/v11/icons/popup.gif\" align=\"top\" alt=\"IBM\" />\n" +
"<a href=\"/servlet/oem/edge/ed/odc/desktop/frontpageadmin?op=editid1\">Edit ID</a></font><br />\n" +
"                  <font size=\"-1\"><b><!-- Adminfunctions --></b><img border=\"0\" width=\"18\" height=\"18\" src=\"//www.ibm.com/i/v11/icons/popup.gif\" align=\"top\" alt=\"IBM\" />\n" +
"<a href=\"/servlet/oem/edge/ed/odc/desktop/frontpageadmin?op=removeid1\">Remove ID</a></font><br />\n" +
"                  <font size=\"-1\"><b><!-- Adminfunctions --></b><img border=\"0\" width=\"18\" height=\"18\" src=\"//www.ibm.com/i/v11/icons/popup.gif\" align=\"top\" alt=\"IBM\" />\n" +
"<a href=\"/servlet/oem/edge/ed/odc/desktop/frontpageadmin?op=multicmd1\">Modify Multiple IDs</a></font><br />\n";

static final String actionpage_admin_status = 
"                  <font size=\"-1\"><b><!-- Adminfunctions --></b><img border=\"0\" width=\"18\" height=\"18\" src=\"//www.ibm.com/i/v11/icons/popup.gif\" align=\"top\" alt=\"IBM\" />\n" +
"<a href=\"/servlet/oem/edge/ed/odc/desktop/frontpageadmin?op=status1\">Tunnel Status</a></font><br />\n";

static final String actionpage_admin_reportdbox = 
"                  <font size=\"-1\"><b><!-- Adminfunctions --></b><img border=\"0\" width=\"18\" height=\"18\" src=\"//www.ibm.com/i/v11/icons/popup.gif\" align=\"top\" alt=\"IBM\" />\n" +
"<a href=\"/servlet/oem/edge/ed/odc/desktop/frontpageadmin?op=reportdbox1\">Dropbox Metrics</a></font><br />\n" +
"                  <font size=\"-1\"><b><!-- Adminfunctions --></b><img border=\"0\" width=\"18\" height=\"18\" src=\"//www.ibm.com/i/v11/icons/popup.gif\" align=\"top\" alt=\"IBM\" />\n" +
"<a href=\"/servlet/oem/edge/ed/odc/desktop/frontpageadmin?op=reportdboxnew1\">(New) Dropbox Reports</a></font><br />\n";


static final String actionpage_admin_report = 
"                  <font size=\"-1\"><b><!-- Adminfunctions --></b><img border=\"0\" width=\"18\" height=\"18\" src=\"//www.ibm.com/i/v11/icons/popup.gif\" align=\"top\" alt=\"IBM\" />\n" +
"<a href=\"/servlet/oem/edge/ed/odc/desktop/frontpageadmin?op=report1\">Tunnel Metrics</a></font><br />\n";

static final String actionpage_main2_hosting = 
"                  <p><font size=\"-1\"><b>Hosting services</b>\n" +
"                  </font></p>\n" +
"                  <font size=\"-1\"><b><!-- Design environment hosting --></b><img border=\"0\" width=\"18\" height=\"18\" src=\"//www.ibm.com/i/v11/icons/popup.gif\" alt=\"IBM\" />\n" +
"<a onclick=\"window.open('/servlet/oem/edge/ed/odc/desktop/frontpagelink?op=DSH','','scrollbars=1,statusbar=1,width=436,height=425,left=387,top=207'); return false;\" target=\"new\" href=\"/servlet/oem/edge/ed/odc/desktop/frontpagelink?op=DSH\">Design environment hosting</a></font><br />\n" +
"                  <font size=\"-1\"><b><!-- hosting test drive --></b><img border=\"0\" width=\"18\" height=\"18\" src=\"//www.ibm.com/i/v11/icons/popup.gif\" align=\"top\" alt=\"IBM\" />\n" +
"<a onclick=\"window.open('/servlet/oem/edge/ed/odc/desktop/frontpagelink?op=DSH&amp;host=DSHDEMOS-0001','','scrollbars=1,statusbar=1,width=436,height=425,left=387,top=207'); return false;\" target=\"new\" href=\"/servlet/oem/edge/ed/odc/desktop/frontpagelink?op=DSH&amp;host=DSHDEMOS-0001\">Hosting test-drive</a></font><br />\n"; 
   
static final String actionpage_main2_collab = 
"                  <p><font size=\"-1\"><b>Design collaboration services</b>\n" +
"                  </font></p>\n";

static final String actionpage_main2_meetings = 
"                  <font size=\"-1\"><b><!-- web conferences link --></b><img border=\"0\" width=\"18\" height=\"18\" src=\"//www.ibm.com/i/v11/icons/popup.gif\" alt=\"IBM\" />\n" +
"<a onclick=\"window.open('/servlet/oem/edge/ed/odc/desktop/frontpagelink?op=NEWODC','','scrollbars=1,statusbar=1,width=436,height=425,left=387,top=207'); return false;\" target=\"new\" href=\"/servlet/oem/edge/ed/odc/desktop/frontpagelink?op=NEWODC\">Web conferences</a></font><br />\n" +
"                  <font size=\"-1\"><b><!-- teamroom link --></b><img border=\"0\" width=\"18\" height=\"18\" src=\"//www.ibm.com/i/v11/icons/popup.gif\" align=\"top\" alt=\"IBM\" />\n" +
"<a href=\"https://oem.partner.boulder.ibm.com/data/PremierAllianceMenu.nsf/Home?OpenPage\">Teamroom</a></font><br />\n";

static final String actionpage_main2_dropbox = 
"                  <font size=\"-1\"><b><!-- web dropbox link --></b><img border=\"0\" width=\"18\" height=\"18\" src=\"//www.ibm.com/i/v11/icons/popup.gif\" alt=\"IBM\" />\n" +
"<a onclick=\"window.open('/servlet/oem/edge/ed/odc/desktop/frontpagelink?op=WXR','','resizable=1,scrollbars=1,statusbar=1,width=700,height=600,left=287,top=107'); return false;\" target=\"webdbox\" href=\"/servlet/oem/edge/ed/odc/desktop/frontpagelink?op=WXR\">Web Dropbox</a></font><br />\n" +
"                  <font size=\"-1\"><b><!-- dropbox link --></b><img border=\"0\" width=\"18\" height=\"18\" src=\"//www.ibm.com/i/v11/icons/popup.gif\" alt=\"IBM\" />\n" +
"<a onclick=\"window.open('/servlet/oem/edge/ed/odc/desktop/frontpagelink?op=XFR','','scrollbars=1,statusbar=1,width=436,height=425,left=387,top=207'); return false;\" target=\"new\" href=\"/servlet/oem/edge/ed/odc/desktop/frontpagelink?op=XFR\">Dropbox Application</a></font><br />\n" +
"                  <font size=\"-1\"><b><!-- GUI applet link --></b><img border=\"0\" width=\"18\" height=\"18\" src=\"//www.ibm.com/i/v11/icons/popup.gif\" alt=\"IBM\" />\n" +
"<a onclick=\"window.open('/servlet/oem/edge/ed/odc/desktop/frontpagelink?op=XFRSVC','','resizable=1,scrollbars=0,statusbar=1,width=768,height=480,left=287,top=107'); return false;\" target=\"dboxsoaapplet\" href=\"/servlet/oem/edge/ed/odc/desktop/frontpagelink?op=XFRSVCA\">Dropbox Applet</a></font><br />\n";

static final String actionpage_main2_grid = 
"                  <font size=\"-1\"><b><!-- grid link --></b><img border=\"0\" width=\"18\" height=\"18\" src=\"//www.ibm.com/i/v11/icons/popup.gif\" alt=\"IBM\" />\n" +
"<a onclick=\"window.open('/servlet/oem/edge/ed/odc/desktop/frontpagelink?op=FDR','','scrollbars=1,statusbar=1,width=436,height=425,left=387,top=207'); return false;\" target=\"new\" href=\"/servlet/oem/edge/ed/odc/desktop/frontpagelink?op=FDR\">Grid</a></font><br />\n";
;


static final String actionpage_main3 =
"                  <br /><br /></td>\n" +
"                </tr>\n";

   static final String signin_nav = 
"<!-- LEFT NAV BEGINS -->\n" +
"<table summary=\"\" width=\"760\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" +
"  <tbody>\n" +
"    <tr valign=\"top\">\n" +
"      <td headers=\"\" width=\"150\" class=\"dbg\">\n" +
"      <table summary=\"\" width=\"150\" border=\"0\" cellspacing=\"0\" cellpadding=\"4\">\n" +
"        <tbody>\n" +
"          <tr class=\"bbg\">\n" +
"            <td headers=\"\" colspan=\"2\"><img src=\"//www.ibm.com/i/v11/icons/rt_arrow.gif\" border=\"0\" width=\"7\" height=\"5\" alt=\"\" />\n" +
"							<span class=\"divider\"> </span><a class=\"mainlink\" href=\"https://www.ibm.com/planetwide/select/\">Select a country</a></td>\n" +
"          </tr>\n" +
"          <tr class=\"dbg\">\n" +
"            <td headers=\"\" colspan=\"2\"><span class=\"related\"></span></td>\n" +
"          </tr>\n" +
"          <tr class=\"dbg\">\n" +
"            <td headers=\"\" colspan=\"2\"> </td>\n" +
"          </tr>\n" +
"          <tr class=\"dbg\">\n" +
"            <td headers=\"\" colspan=\"2\"> </td>\n" +
"          </tr>\n" +
"          <tr class=\"dbg\">\n" +
"            <td headers=\"\" colspan=\"2\"> </td>\n" +
"          </tr>\n" +
"          <tr class=\"dbg\">\n" +
"            <td headers=\"\" colspan=\"2\"> </td>\n" +
"          </tr>\n" +
"          <tr class=\"dbg\">\n" +
"            <td headers=\"\" colspan=\"2\"> </td>\n" +
"          </tr>\n" +
"          <tr class=\"dbg\">\n" +
"            <td headers=\"\" colspan=\"2\"> </td>\n" +
"          </tr>\n" +
"          <tr class=\"dbg\">\n" +
"            <td headers=\"\" colspan=\"2\"> </td>\n" +
"          </tr>\n" +
"          <tr class=\"dbg\">\n" +
"            <td headers=\"\" colspan=\"2\"> </td>\n" +
"          </tr>\n" +
"          <tr class=\"dbg\">\n" +
"            <td headers=\"\" colspan=\"2\"> </td>\n" +
"          </tr>\n" +
"          <tr class=\"dbg\">\n" +
"            <td headers=\"\" colspan=\"2\"> </td>\n" +
"          </tr>\n" +
"          <tr class=\"dbg\">\n" +
"            <td headers=\"\" colspan=\"2\"> </td>\n" +
"          </tr>\n" +
"        </tbody>\n" +
"      </table>\n" +
"      </td>\n" +
"    <!-- LEFT NAV ENDS -->\n";

   static final String actionpage_nav =
"<table width=\"760\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" +
"  <tbody>\n" +
"    <tr valign=\"top\">\n" +
"      <td width=\"150\" class=\"dbg\">\n" +
"      <table width=\"150\" border=\"0\" cellspacing=\"0\" cellpadding=\"4\">\n" +
"        <tbody>\n" +
"          <tr class=\"bbg\">\n" +
"            <td colspan=\"2\"><img src=\"//www.ibm.com/i/v11/icons/rt_arrow.gif\" border=\"0\" width=\"7\" height=\"5\" alt=\"\" /><span class=\"divider\"> </span><a class=\"mainlink\" href=\"http://www.ibm.com/planetwide/select/\">Select a country</a></td>\n" +
"          </tr>\n" +
"          <tr class=\"mbg\">\n" +
"            <td colspan=\"2\"><span class=\"divider\"> </span></td>\n" +
"          </tr>\n" +
"          <tr>\n" +
"            <th class=\"hil\" colspan=\"2\" align=\"left\"><a href=\"/servlet/oem/edge/ed/odc/desktop/frontpage\" class=\"nav\">IBM Engineering &amp; Technology Services</a></th>\n" +
"          </tr>\n" +
"          <tr>\n" +
"            <td colspan=\"2\"><img src=\"//www.ibm.com/i/v11/c.gif\" width=\"1\" height=\"8\" alt=\"\" /></td>\n" +
"          </tr>\n" +
"          <tr>\n" +
"            <td colspan=\"2\" class=\"related\">Related links:</td>\n" +
"          </tr>\n" +
"          <tr>\n" +
"            <td width=\"5\"></td>\n" +
"            <td width=\"145\"><a class=\"rlinks\" href=\"http://www.ibm.com/research\">IBM Reseach</a></td>\n" +
"          </tr>\n" +
"          <tr>\n" +
"            <td width=\"5\"></td>\n" +
"            <td width=\"145\"><a class=\"rlinks\" href=\"http://www.ibm.com/research/bluegene/index.html\">IBM Blue Gene Project</a></td>\n" +
"          </tr>\n" +
"          <tr>\n" +
"            <td width=\"5\"></td>\n" +
"            <td width=\"145\"><a class=\"rlinks\" href=\"http://www.ibm.com/alphaworks\">alphaWorks</a></td>\n" +
"          </tr>\n" +
"          <tr>\n" +
"            <td width=\"5\"></td>\n" +
"            <td width=\"145\"><a class=\"rlinks\" href=\"http://www.ibm.com/financing\">IBM Global Financing</a></td>\n" +
"          </tr>\n" +
"          <tr>\n" +
"            <td width=\"5\"></td>\n" +
"            <td width=\"145\"><a class=\"rlinks\" href=\"http://www.ibm.com/chips\">IBM Microelectronics</a></td>\n" +
"          </tr>\n" +
"          <tr>\n" +
"            <td width=\"5\"></td>\n" +
"            <td width=\"145\"><a class=\"rlinks\" href=\"http://www.ibm.com/services\">IBM Global Services</a></td>\n" +
"          </tr>\n" +
"        </tbody>\n" +
"      </table>\n" +
"      <br />\n" +
"       </td>\n";
   
   static final String header_title_pre = 
"<!DOCTYPE html SYSTEM \"http://www.ibm.com/data/dtd/v11/ibmxhtml1-transitional.dtd\"><html lang=\"en-us\"><head>\n" +
"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />" +
"<meta name=\"SECURITY\" content=\"Public\" />" +
"<meta name=\"KEYWORDS\" content=\"OEM, IBM Corporation, Technology Group\" />" +
"<meta name=\"SOURCE\" content=\"v11 Template Generator, Template 11.1.3\" />" +
"<meta name=\"COPYRIGHT\" content=\"Copyright (c) 2001 by IBM Corporation\" />" +
"<meta name=\"ROBOTS\" content=\"index,follow\" />" +
"<meta name=\"DC.LANGUAGE\" scheme=\"rfc1766\" content=\"en-us\" />" +
"<meta name=\"CHARSET\" content=\"iso88591\" />" +
"<meta name=\"IBM.COUNTRY\" content=\"us\" />" +
"<meta name=\"DC.DATE\" scheme=\"iso8601\" content=\"2001-09-10\" />" +
"<meta name=\"OWNER\"  content=\"econnect@us.ibm.com\" />" +
"<meta name=\"DESCRIPTION\" content=\"Backend Collaboration Central\" />" +
"<meta name=\"ABSTRACT\" content=\"Backend Collaboration Central\" />" +
"<meta http-equiv=\"PICS-Label\" content='(pics-1.1 \"http://www.icra.org/ratingsv02.html\" l gen true for \"http://www.ibm.com\" r (cz 1 lz 1 nz 1 oz 1 vz 1) \"http://www.rsac.org/ratingsv01.html\" l gen true for \"http://www.ibm.com\" r (n 0 s 0 v 0 l 0))' />" +
"<meta http-equiv=\"Pragma\" content=\"no-cache\" />\n" +
"<meta http-equiv=\"Cache-Control\" content=\"no-cache\" />\n" +
"<meta http-equiv=\"Expires\" content=\"0\" />\n" +

"<title>";

   static final String header_title_post = 
"</title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />\n" +
"<meta name=\"SECURITY\" content=\"Public\" />\n" +
"<meta name=\"SOURCE\" content=\"v11 Template Generator, Template 11.3.0\" />\n" +
"<meta name=\"COPYRIGHT\" content=\"Copyright (c) 2002 by IBM Corporation\" />\n" +
"<meta name=\"CHARSET\" content=\"iso88591\" />\n" +
"<meta name=\"IBM.COUNTRY\" content=\"us\" />\n" +
"<meta name=\"GENERATOR\" content=\"IBM WebSphere Homepage Builder V5.0.1 for Windows\" />\n" +
"\n" +
"\n" +
"<script type=\"text/javascript\" language=\"JavaScript\" src=\"//www.ibm.com/data/js/v11/ibmcss.js\">\n" +
"</script><!--doc created by template generator-->\n" +
"<link rel=\"stylesheet\" href=\"//www.ibm.com/data/css/v11/r1.css\" type=\"text/css\"/>\n" +
"<script type=\"text/javascript\" language=\"javascript\" src=\"//www.ibm.com/data/js/v11/ibmcss.js\"></script>\n" +
"<link rel=\"stylesheet\" href=\"//www.ibm.com/data/css/v11/r1.css\" type=\"text/css\"/>\n" +
"</head>\n" +
"<body bgcolor=\"#ffffff\" marginheight=\"2\" marginwidth=\"2\" topmargin=\"2\" leftmargin=\"2\" alink=\"#006699\">\n" +
"<noscript></noscript>\n" +
"<noscript><br />\n" +
"<br />\n" +
"<form>\n" +
"<table summary=\"\" border=\"0\">\n" +
"  \n" +
"    <tr>\n" +
"      <td headers=\"\" valign=\"top\" align=\"center\"></td>\n" +
"      <td headers=\"\" valign=\"top\" align=\"left\">\n" +
"      <center><br />\n" +
"      <b>JAVASCRIPT IS CURRENLY  OFF!<br />\n" +
"      The functionality will not work.<br />\n" +
"      <br />\n" +
"      Set the javascript property of the browser\n" +
"      on.<br />\n" +
"      <br />\n" +
"      <br />\n" +
"      For 24 hour support - please call (US) 1-888-220-EDGE\n" +
"      or Email: ibmedge@us.ibm.com</B></CENTER>\n" +
"      </td>\n" +
"    </tr>\n" +
"    <tr>\n" +
"      <td headers=\"\"><a href=\"javascript:history.back()\"><img src=\"//www.ibm.com/i/v11/buttons/arrow_lt.gif\" border=\"0\" height=\"21\" width=\"21\" alt=\"Back\" /></a>&nbsp;</td>\n" +
"      <td headers=\"\"><a href=\"javascript:history.back()\">Back</a></td>\n" +
"    </tr>\n" +
"  \n" +
"</table>\n" +
"</form>\n" +
"<br />\n" +
"<br />\n" +
"</noscript>\n" +
"<noscript></noscript>\n";


   static final String pagetitle_pre =
"      <td width=\"610\"><a name=\"main\"></a><!-- Page title table -->\n" +
"      <table width=\"453\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" +
"        <tbody>\n" +
"          <tr>\n" +
"            <td width=\"10\"><img src=\"//www.ibm.com/i/v11//c.gif\" width=\"10\" height=\"1\" alt=\"\" /></td>\n" +
"            <td width=\"443\">\n" +
"            <p><img src=\"//www.ibm.com/i/v11/c.gif\" width=\"1\" height=\"8\" alt=\"\" /><br />\n" +
"            <span class=\"boldtitle\">\n";

   static final String pagetitle_post =
"            </span></p></td>\n" +
"          </tr>\n" +
"          <tr>\n" +
"            <td colspan=\"2\"><img src=\"//www.ibm.com/i/v11/c.gif\" width=\"1\" height=\"20\" alt=\"\" /></td>\n" +
"          </tr>\n" +
"        </tbody>\n" +
"      </table>\n" +
"      <!-- end page title table --><!-- =========================================================================================== -->\n";

   static final String content_bar_pre =
"          <tr>\n" +
"            <td headers=\"\" valign=\"top\" width=\"600\">\n" +
"            <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" +
"              <tbody>\n" +
"                <tr valign=\"top\">\n" +
"                  <td width=\"10\"> </td>\n" +
"                  <td colspan=\"3\">\n" +
"                  <table width=\"550\" height=\"20\" bgcolor=\"#6699cc\">\n" +
"                    <tbody>\n" +
"                      <tr>\n" +
"                        <td>\n" +
"                        <p><font color=\"white\" size=\"2\"><b>\n";

   static final String content_bar_post =
"                            </b></font></p>\n" +
"                        </td>\n" +
"                      </tr>\n" +
"                    </tbody>\n" +
"                  </table>\n" +
"                  </td>\n" +
"                </tr>\n" +
"                <tr valign=\"top\"></tr>\n";


   static final String masthead_login = 
"<!-- MASTHEAD_BEGIN -->\n" +
"<table summary=\"\" width=\"760\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" +
"  <tbody>\n" +
"    <tr bgcolor=\"#006699\">\n" +
"      <td headers=\"\" width=\"150\" class=\"tbgc\"><a href=\"http://www.ibm.com/\"><img src=\"//www.ibm.com/i/v11/m/en/mast_logo.gif\" border=\"0\" alt=\"IBM\" width=\"150\" height=\"47\" /></a></td>\n" +
"      <td headers=\"\" width=\"310\" class=\"tbg\"><a href=\"#main\"><img src=\"//www.ibm.com/i/v11/c.gif\" border=\"0\" width=\"1\" height=\"1\" alt=\"Skip to main content\" /></a></td>\n" +
"      <td headers=\"\" width=\"300\" class=\"tbgc\" align=\"right\" valign=\"top\"><input type=\"hidden\" name=\"v\" value=\"11\"><input value=\"en\" name=\"lang\" type=\"hidden\"><input value=\"us\" name=\"cc\" type=\"hidden\">\n" +
"      <form method=\"get\" action=\"http://www.ibm.com/Search\" title=\"Search form\"></form>\n" +
"      <table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n" +
"        <tbody>\n" +
"          <tr>\n" +
"            <td headers=\"\" colspan=\"4\"><img src=\"//www.ibm.com/i/v11/c.gif\" border=\"0\" width=\"1\" height=\"12\" alt=\"\" /></td>\n" +
"          </tr>\n" +
"          <tr valign=\"middle\">\n" +
"            <td headers=\"\"><input type=\"text\" value=\"\" name=\"q\" size=\"15\" class=\"input\" maxlength=\"100\" id=\"searchformq\"></td>\n" +
"            <td headers=\"\"><img src=\"//www.ibm.com/i/v11/c.gif\" border=\"0\" width=\"5\" height=\"1\" alt=\"\" /></td>\n" +
"            <td headers=\"\" width=\"64\"><label for=\"searchformq\"><input type=\"image\" src=\"//www.ibm.com/i/v11/m/en/search.gif\" width=\"64\" height=\"23\" border=\"0\" value=\"Search button\" name=\"Search\" alt=\"Search\"></label></td>\n" +
"            <td headers=\"\" valign=\"top\"><img src=\"//www.ibm.com/i/v11/c.gif\" border=\"0\" width=\"40\" height=\"1\" alt=\"\" /></td>\n" +
"          </tr>\n" +
"        </tbody>\n" +
"      </table>\n" +
"      </td>\n" +
"    </tr>\n" +
"    <tr>\n" +
"      <td headers=\"\" width=\"150\" height=\"21\" class=\"hbg\" bgcolor=\"#006699\"> </td>\n" +
"      <td headers=\"\" colspan=\"2\" height=\"21\" valign=\"top\" class=\"bbg\">   \n" +
"			<a class=\"mainlink\" href=\"http://www.ibm.com/\">Home</a><span class=\"divider\">  |  </span>\n" +
"			<a class=\"mainlink\" href=\"http://www.ibm.com/products/\">Products &amp; services</a><span class=\"divider\">  |  </span>\n" +
"			<a class=\"mainlink\" href=\"http://www.ibm.com/support/\">Support &amp; downloads</a><span class=\"divider\">  |  </span>\n" +
"			<a class=\"mainlink\" href=\"http://www.ibm.com/account/\">My account</a></td>\n" +
"    </tr>\n" +
"  </tbody>\n" +
"</table>\n" +
"<!-- MASTHEAD_END -->\n";

   static final String masthead = 
"<!-- MASTHEAD_BEGIN -->\n" +
"<table summary=\"\" width=\"760\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" +
"  <tbody>\n" +
"    <tr bgcolor=\"#006699\">\n" +
"      <td headers=\"\" width=\"150\" class=\"tbgc\"><a href=\"http://www.ibm.com/\"><img src=\"//www.ibm.com/i/v11/m/en/mast_logo.gif\" border=\"0\" alt=\"IBM\" width=\"150\" height=\"47\" /></a></td>\n" +
"      <td headers=\"\" width=\"310\" class=\"tbg\"><a href=\"#main\"><img src=\"//www.ibm.com/i/v11/c.gif\" border=\"0\" width=\"1\" height=\"1\" alt=\"Skip to main content\" /></a></td>\n" +
"      <td headers=\"\" width=\"300\" class=\"tbgc\" align=\"right\" valign=\"top\"><input type=\"hidden\" name=\"v\" value=\"11\"><input value=\"en\" name=\"lang\" type=\"hidden\"><input value=\"us\" name=\"cc\" type=\"hidden\">\n" +
"      <form method=\"get\" action=\"http://www.ibm.com/Search\" title=\"Search form\"></form>\n" +
"      <table summary=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n" +
"        <tbody>\n" +
"          <tr>\n" +
"            <td headers=\"\" colspan=\"4\"><img src=\"//www.ibm.com/i/v11/c.gif\" border=\"0\" width=\"1\" height=\"12\" alt=\"\" /></td>\n" +
"          </tr>\n" +
"          <tr valign=\"middle\">\n" +
"            <td headers=\"\"><input type=\"text\" value=\"\" name=\"q\" size=\"15\" class=\"input\" maxlength=\"100\" id=\"searchformq\"></td>\n" +
"            <td headers=\"\"><img src=\"//www.ibm.com/i/v11/c.gif\" border=\"0\" width=\"5\" height=\"1\" alt=\"\" /></td>\n" +
"            <td headers=\"\" width=\"64\"><label for=\"searchformq\"><input type=\"image\" src=\"//www.ibm.com/i/v11/m/en/search.gif\" width=\"64\" height=\"23\" border=\"0\" value=\"Search button\" name=\"Search\" alt=\"Search\"></label></td>\n" +
"            <td headers=\"\" valign=\"top\"><img src=\"//www.ibm.com/i/v11/c.gif\" border=\"0\" width=\"40\" height=\"1\" alt=\"\" /></td>\n" +
"          </tr>\n" +
"        </tbody>\n" +
"      </table>\n" +
"      </td>\n" +
"    </tr>\n" +
"    <tr>\n" +
"      <td headers=\"\" width=\"150\" height=\"21\" class=\"hbg\" bgcolor=\"#006699\"> </td>\n" +
"      <td headers=\"\" colspan=\"2\" height=\"21\" valign=\"top\" class=\"bbg\">   \n" +
"			<a class=\"mainlink\" href=\"http://www.ibm.com/\">Home</a><span class=\"divider\">  |  </span>\n" +
"			<a class=\"mainlink\" href=\"http://www.ibm.com/products/\">Products &amp; services</a><span class=\"divider\">  |  </span>\n" +
"			<a class=\"mainlink\" href=\"http://www.ibm.com/support/\">Support &amp; downloads</a><span class=\"divider\">  |  </span>\n" +
"			<a class=\"mainlink\" href=\"http://www.ibm.com/account/\">My account</a><span class=\"divider\">  |  </span>\n" +
"			<a class=\"mainlink\" href=\"/servlet/oem/edge/ed/odc/desktop/frontpagelink?op=SIGNOFF\">Signoff</a></td>\n" +
"    </tr>\n" +
"  </tbody>\n" +
"</table>\n" +
"<!-- MASTHEAD_END -->\n";

   static final String contentspace_head = 
"    <!-- CONTENT SPACE STARTS -->\n" +
"      <table summary=\"\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"600\">\n" +
"        <tbody>\n";

/*   
   static final String errorHead = 
"          <!-- ADD THESE TAGS TO INCORPORATE A MESSAGE -->\n" +
"          <tr><td>" +
"            <table summary=\"\" cellpadding=\"20\" cellspacing=\"0\" border=\"0\" width=\"450\">\n" +
"              <tbody>\n" +
"                <tr valign=\"top\">\n" +
"                  <td headers=\"\">\n" +
"                  <p><font size=\"+1\" color=\"#ff3333\">\n\n";
   static final String errorTail = 
"                  </font></p>\n" +
"                  </td>\n" +
"                </tr>\n" +
"              </tbody>\n" +
"            </table>\n" +
"          </td></tr>" +
"          <!-- END OF TAGS TO INCORPORATE A MESSAGE -->\n";
*/
   static final String errorHead = 
"                  <p><font size=\"+1\" color=\"#ff3333\">\n\n";


   static final String errorTail = 
"                  </font></p>\n";

   static final String changepw_content = 
"            <table width=\"433\" summary=\"\">\n" +
"              <tbody>\n" +
"                <tr>\n" +
"                  <td headers=\"\">  Change your ETS *PILOT* Password</td>\n" +
"                </tr>\n" +
"              </tbody>\n" +
"            </table>\n" +
"            <form name=\"ChangePW\" method=\"post\" action=\"/servlet/oem/edge/ed/odc/desktop/frontpageadmin?op=pw2\">\n" +
"            <table summary=\"\" cellpadding=\"0\" cellspacing=\"2\" border=\"0\" width=\"350\">\n" +
"              <tbody>\n" +
"                <tr>\n" +
"                  <td headers=\"\" align=\"right\">\n" +
"                  <table summary=\"\" cellpadding=\"2\" cellspacing=\"2\" border=\"0\">\n" +
"                    <tbody>\n" +
"                      <tr>\n" +
"                        <td headers=\"\" valign=\"middle\"><label for=\"curpw\"><b>Current Password</b></label></td>\n" +
"                        <td headers=\"\" valign=\"middle\"><input id=\"curpw\" maxlength=\"100\" class=\"iform\" size=\"20\" name=\"curpass\" type=\"password\" value=\"\"></td>\n" +
"                      </tr>\n" +
"                      <tr>\n" +
"                        <td headers=\"\" valign=\"middle\"><label for=\"npass1\"><b>New Password</b></label></td>\n" +
"                        <td headers=\"\" valign=\"middle\"><input id=\"npass1\" maxlength=\"100\" class=\"iform\" size=\"20\" name=\"npass1\" type=\"password\"></td>\n" +
"                      </tr>\n" +
"                      <tr>\n" +
"                        <td headers=\"\" valign=\"middle\"><label for=\"npass2\"><b>Confirm Password</b></label></td>\n" +
"                        <td headers=\"\" valign=\"middle\"><input id=\"npass2\" maxlength=\"100\" class=\"iform\" size=\"20\" name=\"npass2\" type=\"password\"></td>\n" +
"                      </tr>\n" +
"                      <tr>\n" +
"                        <td headers=\"\" colspan=\"2\" valign=\"middle\" align=\"middle\"><input id=\"signin\" type=\"image\" name=\"Sign in\" border=\"0\" src=\"//www.ibm.com/i/v11/buttons/sign_in.gif\" width=\"120\" height=\"21\" alt=\"Sign in\"></td>\n" +
"                      </tr>\n" +
"                    </tbody>\n" +
"                  </table>\n" +
"                  </td>\n" +
"                </tr>\n" +
"                <tr>\n" +
"                  <td headers=\"\" align=\"right\"></td>\n" +
"                </tr>\n" +
"              </tbody>\n" +
"            </table>\n" +
"            </form>\n";

   static final String signin_content1 = 
"            <table width=\"433\" summary=\"\">\n" +
"              <tbody>\n" +
"                <tr>\n";

   static final String use_pilot_id = 
"                  <td headers=\"\">  Use your Pilot ID to access prototypes</td>\n";
   static final String signin_content2 = 
"                </tr>\n" +
"              </tbody>\n" +
"            </table>\n" +
"            <form name=\"LoginForm\" method=\"post\" action=\"/servlet/oem/edge/ed/odc/desktop/loginpage2\">\n";

   static final String signin_content3 =
"            <table summary=\"\" cellpadding=\"0\" cellspacing=\"2\" border=\"0\" width=\"350\">\n" +
"              <tbody>\n" +
"                <tr>\n" +
"                  <td headers=\"\" align=\"right\">\n" +
"                  <table summary=\"\" cellpadding=\"2\" cellspacing=\"2\" border=\"0\">\n" +
"                    <tbody>\n" +
"                      <tr>\n" +
"                        <td headers=\"\" valign=\"middle\"><label for=\"ibmid\"><b>Pilot ID</b></label></td>\n" +
"                        <td headers=\"\" valign=\"middle\"><input id=\"ibmid\" maxlength=\"100\" class=\"iform\" size=\"20\" name=\"userid\" type=\"text\" value=\"\"></td>\n" +
"                      </tr>\n" +
"                      <tr>\n" +
"                        <td headers=\"\" valign=\"middle\"><label for=\"password\"><b>Password</b></label></td>\n" +
"                        <td headers=\"\" valign=\"middle\"><input id=\"password\" maxlength=\"100\" class=\"iform\" size=\"20\" name=\"password\" type=\"password\"></td>\n" +
"                      </tr>\n" +
"                      <tr>\n" +
"                        <td headers=\"\" colspan=\"2\" valign=\"middle\" align=\"middle\"><input id=\"signin\" type=\"image\" name=\"Sign in\" border=\"0\" src=\"//www.ibm.com/i/v11/buttons/sign_in.gif\" width=\"120\" height=\"21\" alt=\"Sign in\"></td>\n" +
"                      </tr>\n" +
"                    </tbody>\n" +
"                  </table>\n" +
"                  </td>\n" +
"                </tr>\n" +
"                <tr>\n" +
"                  <td headers=\"\" align=\"right\"></td>\n" +
"                </tr>\n" +
"              </tbody>\n" +
"            </table>\n" +
"            </form>\n";


   static final String addid_content = 
"            <table width=\"433\" summary=\"\">\n" +
"              <tbody>\n" +
"                <tr>\n" +
"                  <td headers=\"\">  Enter new Login ID name</td>\n" +
"                </tr>\n" +
"              </tbody>\n" +
"            </table>\n" +
"            <form name=\"LoginForm\" method=\"post\" action=\"/servlet/oem/edge/ed/odc/desktop/frontpageadmin?op=addid2\">\n" +
"            <table summary=\"\" cellpadding=\"0\" cellspacing=\"2\" border=\"0\" width=\"350\">\n" +
"              <tbody>\n" +
"                <tr>\n" +
"                  <td headers=\"\" align=\"right\">\n" +
"                  <table summary=\"\" cellpadding=\"2\" cellspacing=\"2\" border=\"0\">\n" +
"                    <tbody>\n" +
"                      <tr>\n" +
"                        <td headers=\"\" valign=\"middle\"><label for=\"ibmid\"><b>New Pilot ID</b></label></td>\n" +
"                        <td headers=\"\" valign=\"middle\"><input id=\"ibmid\" maxlength=\"100\" class=\"iform\" size=\"20\" name=\"userid\" type=\"text\" value=\"\"></td>\n" +
"                      </tr>\n" +
"                      <tr>\n" +
"                        <td headers=\"\" valign=\"middle\"><label for=\"ibmid\"><b>Copy from ID</b></label></td>\n" +
"                        <td headers=\"\" valign=\"middle\"><input id=\"ibmid\" maxlength=\"100\" class=\"iform\" size=\"20\" name=\"copyid\" type=\"text\" value=\"\"></td>\n" +
"                      </tr>\n" +
"                      <tr>\n" +
"                        <td headers=\"\" colspan=\"2\" valign=\"middle\" align=\"middle\"><input id=\"continue\" type=\"image\" name=\"continue\" border=\"0\" src=\"//www.ibm.com/i/v11/buttons/continue.gif\" width=\"120\" height=\"21\" alt=\"Continue\"></td>\n" +
"                      </tr>\n" +
"                    </tbody>\n" +
"                  </table>\n" +
"                  </td>\n" +
"                </tr>\n" +
"                <tr>\n" +
"                  <td headers=\"\" align=\"right\"></td>\n" +
"                </tr>\n" +
"              </tbody>\n" +
"            </table>\n" +
"            </form>\n";

   static final String removeid_content = 
"            <table width=\"433\" summary=\"\">\n" +
"              <tbody>\n" +
"                <tr>\n" +
"                  <td headers=\"\">  Enter Login ID to Remove</td>\n" +
"                </tr>\n" +
"              </tbody>\n" +
"            </table>\n" +
"            <form name=\"LoginForm\" method=\"post\" action=\"/servlet/oem/edge/ed/odc/desktop/frontpageadmin?op=removeid2\">\n" +
"            <table summary=\"\" cellpadding=\"0\" cellspacing=\"2\" border=\"0\" width=\"350\">\n" +
"              <tbody>\n" +
"                <tr>\n" +
"                  <td headers=\"\" align=\"right\">\n" +
"                  <table summary=\"\" cellpadding=\"2\" cellspacing=\"2\" border=\"0\">\n" +
"                    <tbody>\n" +
"                      <tr>\n" +
"                        <td headers=\"\" valign=\"middle\"><label for=\"ibmid\"><b>Pilot ID to Remove</b></label></td>\n" +
"                        <td headers=\"\" valign=\"middle\"><input id=\"ibmid\" maxlength=\"100\" class=\"iform\" size=\"20\" name=\"userid\" type=\"text\" value=\"\"></td>\n" +
"                      </tr>\n" +
"                      <tr>\n" +
"                        <td headers=\"\" colspan=\"2\" valign=\"middle\" align=\"middle\"><input id=\"continue\" type=\"image\" name=\"continue\" border=\"0\" src=\"//www.ibm.com/i/v11/buttons/continue.gif\" width=\"120\" height=\"21\" alt=\"Continue\"></td>\n" +
"                      </tr>\n" +
"                    </tbody>\n" +
"                  </table>\n" +
"                  </td>\n" +
"                </tr>\n" +
"                <tr>\n" +
"                  <td headers=\"\" align=\"right\"></td>\n" +
"                </tr>\n" +
"              </tbody>\n" +
"            </table>\n" +
"            </form>\n";

   static final String editid_content1 = 
"            <table width=\"433\" summary=\"\">\n" +
"              <tbody>\n" +
"                <tr>\n" +
"                  <td headers=\"\">  Enter Login ID to Edit</td>\n" +
"                </tr>\n" +
"              </tbody>\n" +
"            </table>\n" +
"            <form name=\"LoginForm\" method=\"post\" action=\"/servlet/oem/edge/ed/odc/desktop/frontpageadmin?op=editid2\">\n" +
"            <table summary=\"\" cellpadding=\"0\" cellspacing=\"2\" border=\"0\" width=\"350\">\n" +
"              <tbody>\n" +
"                <tr>\n" +
"                  <td headers=\"\" align=\"right\">\n" +
"                  <table summary=\"\" cellpadding=\"2\" cellspacing=\"2\" border=\"0\">\n" +
"                    <tbody>\n" +
"                      <tr>\n" +
"                        <td headers=\"\" valign=\"middle\"><label for=\"ibmid\"><b>Pilot ID to Edit</b></label></td>\n";

   
   static final String editid_content_text = 
"                        <td headers=\"\" valign=\"middle\"><input id=\"ibmid\" maxlength=\"100\" class=\"iform\" size=\"20\" name=\"userid\" type=\"text\" value=\"\"></td>\n";

   static final String editid_content_select = 
"                        <td headers=\"\" valign=\"middle\"><select name=\"userid\">";
  
   static final String editid_content2 = 
   "                   </tr>\n" +
"                      <tr>\n" +
"                        <td headers=\"\" colspan=\"2\" valign=\"middle\" align=\"middle\"><input id=\"continue\" type=\"image\" name=\"continue\" border=\"0\" src=\"//www.ibm.com/i/v11/buttons/continue.gif\" width=\"120\" height=\"21\" alt=\"Continue\"></td>\n" +
"                      </tr>\n" +
"                    </tbody>\n" +
"                  </table>\n" +
"                  </td>\n" +
"                </tr>\n" +
"                <tr>\n" +
"                  <td headers=\"\" align=\"right\"></td>\n" +
"                </tr>\n" +
"              </tbody>\n" +
"            </table>\n" +
"            </form>\n";

   static final String multicmd_content_1 = 
"            <table width=\"433\" summary=\"\">\n" +
"              <tbody>\n" +
"                <tr>\n" +
"                  <td headers=\"\">Apply a multiuser command</td>\n" +
"                </tr>\n" +
"              </tbody>\n" +
"            </table>\n" +
"            <form name=\"LoginForm\" method=\"post\" action=\"/servlet/oem/edge/ed/odc/desktop/frontpageadmin?op=multicmd2\">\n" +
"            <table summary=\"\" cellpadding=\"0\" cellspacing=\"2\" border=\"0\" width=\"550\">\n" +
"              <tbody>\n" +
"                <tr>\n" +
"                  <td headers=\"\" align=\"right\">\n" +
"                  <table summary=\"\" cellpadding=\"2\" cellspacing=\"2\" border=\"2\">\n" +
"                    <tbody>\n" +
"                      <tr>\n" +
"                        <th headers=\"\" align=\"middle\" valign=\"middle\"><label for=\"ibmid\">Operation</label></th>\n" +
"                        <th headers=\"\" align=\"middle\" valign=\"middle\"><label for=\"ibmid\">UserIds</label></td>\n" +
"                      </tr>\n" + 
"                      <tr>\n" +
"                        <td headers=\"\" align=\"left\" valign=\"middle\">\n" +
"                          <br /><input type=\"radio\" checked nowrap name=\"selcmd\" value=\"1\">Enable Id\n" +
"                          <br /><input type=\"radio\" nowrap name=\"selcmd\" value=\"2\">Disable Id\n" +
"                          <br /><input type=\"radio\" nowrap name=\"selcmd\" value=\"3\">Remove Id\n" +
"                          <br /><input type=\"radio\" nowrap name=\"selcmd\" value=\"4\">Expiration Date\n" +
"                          <blockquote>\n";


   static final String multicmd_content_2 = 
"                          </blockquote>\n" +
"                        </td>\n" +

"                        <td headers=\"\" valign=\"middle\">\n" +
"                          <select size=10 multiple name=\"userids\">\n";
  
   static final String multicmd_content_3 = 
"                          </select>\n" +
"                        </td>\n" +
"                      </tr>\n" +
"                      <tr>\n" +
"                        <td headers=\"\" colspan=\"2\" valign=\"middle\" align=\"middle\"><input id=\"continue\" type=\"image\" name=\"continue\" border=\"0\" src=\"//www.ibm.com/i/v11/buttons/continue.gif\" width=\"120\" height=\"21\" alt=\"Continue\"></td>\n" +
"                      </tr>\n" +
"                    </tbody>\n" +
"                  </table>\n" +
"                  </td>\n" +
"                </tr>\n" +
"                <tr>\n" +
"                  <td headers=\"\" align=\"right\"></td>\n" +
"                </tr>\n" +
"              </tbody>\n" +
"            </table>\n" +
"            </form>\n";


   static final String contentspace_tail =
"        </tbody>\n" +
"      </table>\n" +
"      </td>\n" +
"    <!-- APPLICATION CONTENT SPACE ENDS -->" + 
"    </tr>\n" +
"  </tbody>\n" +
"</table>\n</tr></tbody></table>\n";

   static final String footer =
"<!-- FOOTER_BEGIN -->\n" +
"<table width=\"760\" summary=\"\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" +
"  <tbody>\n" +
"    <tr>\n" +
"      <td headers=\"\"><img src=\"//www.ibm.com/i/v11/c.gif\" width=\"1\" height=\"1\" alt=\"\" /></td>\n" +
"    </tr>\n" +
"    <tr valign=\"top\">\n" +
"      <td headers=\"\" height=\"21\" class=\"bbg\">  <a href=\"http://www.ibm.com/ibm/\" class=\"mainlink\">About IBM</a><span class=\"divider\">  |  </span>\n" +
"		<a href=\"http://www.ibm.com/privacy/\" class=\"mainlink\">Privacy</a><span class=\"divider\">  |  </span>\n" +
"		<a href=\"http://www.ibm.com/legal/\" class=\"mainlink\">Legal</a><span class=\"divider\">  |  </span>\n" +
"		<a href=\"http://www.ibm.com/contact/\" class=\"mainlink\">Contact</a></td>\n" +
"    </tr>\n" +
"  </tbody>\n" +
"</table>\n" +
"<!-- FOOTER_END -->\n" +
"<noscript><img src=\"//www.ibm.com/i/v11/c.gif\" width=\"1\" height=\"1\" alt=\"\" border=\"0\" /></noscript>\n" +
"</body>\n" +
   "</html>\n";

}
