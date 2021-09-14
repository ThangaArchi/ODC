package oem.edge.ed.odc.webdropbox.reports;

import org.apache.struts.util.*;
import org.apache.struts.action.*;
import org.apache.struts.config.*;
import org.apache.struts.actions.*;
import org.apache.commons.beanutils.BeanUtils;

import oem.edge.ed.odc.tunnel.common.DebugPrint;
import oem.edge.ed.odc.util.*;
import oem.edge.ed.odc.dsmp.server.UserInfo;
import com.ibm.as400.webaccess.common.*;
import oem.edge.ed.odc.dropbox.common.DropboxGenerator;
import oem.edge.ed.odc.dropbox.common.PackageInfo;

import javax.servlet.http.*;
import javax.servlet.*;

import  java.io.*;
import  java.util.*;
import  java.lang.*;
import  java.lang.reflect.*;
import  java.sql.*;

/*  ------------------------------------------------------------------   */ 
/*                       Copyright Header Check                          */
/*  ------------------------------------------------------------------   */ 
/*                                                                       */ 
/*   OCO Source Materials                                                */ 
/*                                                                       */ 
/*   Product(s): PROFIT                        		                 */ 
/*                                                                       */ 
/*   (C)Copyright IBM Corp. 2005-2006                                    */ 
/*                                                                       */ 
/*     All Rights Reserved                                               */ 
/*     US Government Users Restricted Rights                             */ 
/*                                                                       */ 
/*   The source code for this program is not published or otherwise      */ 
/*   divested of its trade secrets, irrespective of what has been        */ 
/*   deposited with the US Copyright Office.                             */ 
/*   ------------------------------------------------------------------  */
/*     Please do not remove any of these commented lines  21 lines       */
/*   ------------------------------------------------------------------  */
/*                       Copyright Footer Check                          */
/*   ------------------------------------------------------------------  */ 

public class ReportAction extends DispatchAction {

   protected static final String p_pkgstate =
   "case when p.pkgstat = " + DropboxGenerator.STATUS_COMPLETE +
   " then 'Completed' else 'Ready' end ";
            
  // Check the ITAR bit in package flags 
   protected static final String p_itarstatus =
   "case when MOD((p.flags / " + PackageInfo.ITAR + "), 2) = 1" +
   " then 'True' else 'False' end ";
            
   protected static final String fa_state = 
   " case when fa.dloadstat = " + DropboxGenerator.STATUS_COMPLETE +
   " then 'Completed' else 'Failed' end ";

   public static final String f_state = 
   "case when f.filestat = " +
   DropboxGenerator.STATUS_COMPLETE + " then 'Completed' when f.filestat = " +
   DropboxGenerator.STATUS_INCOMPLETE + " then 'Failed' else 'NotStarted' end ";
   
  // --------------------------------------------
  // ----- Filter related code
  // --------------------------------------------

   public static final int BETWEEN = 1;
   public static final int      GT = 2;
   public static final int      LT = 3;
   public static final int      EQ = 4;
   public static final int      GE = 5;
   public static final int      LE = 6;
   public String getSQLModifier(String s) {
      int v = getSQLModifierAsNumber(s);
      String ret = "GE";
      switch(v) {
         case GT:      ret = ">";       break;
         case EQ:      ret = "=";       break;
         case LT:      ret = "<";       break;
         case LE:      ret = "<=";      break;
         case BETWEEN: ret = "between"; break;
         case GE:
         default:      ret = ">=";      break;
      }
      return ret;
   }
   
   public boolean applyFilter(int op, long v, long vv) {
      boolean ret = true;
      switch(op) {
         case GE: if (v <  vv) ret=false; break;
         case LE: if (v >  vv) ret=false; break;
         case LT: if (v >= vv) ret=false; break; 
         case GT: if (v <= vv) ret=false; break;
         case EQ: if (v != vv) ret=false; break;
         default:
      }
      return ret;
   }
   
   public int getSQLModifierAsNumber(String s) {
      if (s == null ) return GT;
      s = s.trim();
      if (s.equalsIgnoreCase("ge") || s.equals(">="))      return GE;
      if (s.equalsIgnoreCase("gt") || s.equals(">"))       return GT;
      if (s.equalsIgnoreCase("eq") || s.equals("="))       return EQ;
      if (s.equalsIgnoreCase("lt") || s.equals("<"))       return LT;
      if (s.equalsIgnoreCase("le") || s.equals("<="))      return LE;
      if (s.equalsIgnoreCase("between") || s.equals("><")) return BETWEEN;
      return GE;
   }

   
   
  // Generates the appropriate SQL for a WHERE clause including/excluding the
  //  items in 's' which are values of variable 'v'
  //
  // For now, if the first char in the field is *, then its considered a WILD
  //  field. Change this to pass wild in or somesuch
  //
   protected String generateFilter(String s, String v) {
      StringBuffer ret = new StringBuffer(" ");
      
      Vector positive = new Vector();
      Vector negative = new Vector();
      
      if (s != null && v != null) {
      
         s = s.trim();
            
        // Cheapo way of stating its wildcard
         boolean wild = false;
         if (s.length() >= 1 && s.charAt(0) == '*') {
            wild = true;
            s = s.substring(1);
         }
         
        // Parse the string into tokens. All consecutive chars are a token,
        //  as well, entire strings enclosed as qouted strings.
        //  Handle the Wilds conversion from *. to %_ ... also escape 
        //  things appropriately
         String maindelims = " \n\"'";
         String curdelims  = maindelims;
         boolean qstring = false;
         StringTokenizer st = new StringTokenizer(s, maindelims, true);
         try {
            while(st.hasMoreTokens()) {
            
               String token = st.nextToken(curdelims);
                
              // If we have a delim
               if (token.length() == 1 && curdelims.indexOf(token) >= 0) {
                  if (qstring) {
                     qstring = false;
                     curdelims = maindelims;
                  } else if (token.equals("'") || token.equals("\"")) {
                     qstring = true;
                     curdelims = token;
                  }
                  continue;
               }
               
               boolean hasWild = false;
               
              // Remove all '\' (escape) chars other than \\, \* and \.
               int idx=0;
               String t = token;
               while((idx=t.indexOf('\\', idx)) >=0) {
                  if (t.length() == idx+1     || 
                      (t.charAt(idx+1) != '\\' &&
                       t.charAt(idx+1) != '*'  &&
                       t.charAt(idx+1) != '.')) {
                     t = t.substring(0,idx) + t.substring(idx+1);
                  } else {
                     idx+=2;
                  }
               }
            
              // Escape all '
               idx=0;
               while((idx=t.indexOf('\'', idx)) >=0) {
                  if (!isEscaped(t, idx)) {
                     t = t.substring(0, idx) + '\\' + t.substring(idx);
                     idx += 2;
                  } else {
                     idx++;
                  }
               }
            
              // Escape all % and _ chars  if wild
               if (wild) {
                  idx=0;
                  while((idx=t.indexOf('%', idx)) >=0) {
                     if (!isEscaped(t, idx)) {
                        t = t.substring(0, idx) + '\\' + t.substring(idx);
                        idx += 2;
                     } else {
                        idx++;
                     }
                  }
               
                  idx=0;
                  while((idx=t.indexOf('_', idx)) >=0) {
                     if (!isEscaped(t, idx)) {
                        t = t.substring(0, idx) + '\\' + t.substring(idx);
                        idx += 2;
                     } else {
                        idx++;
                     }
                  }
               
               
                 // Convert all * -> % and . -> _  unless its escaped
                  idx=0;
                  while((idx=t.indexOf('*', idx)) >=0) {
                     if (!isEscaped(t, idx)) {
                        t = t.substring(0, idx) + '%' + t.substring(idx+1);
                        hasWild = true;
                     }
                     idx++;
                  }
               
                  idx=0;
                  while((idx=t.indexOf('.', idx)) >=0) {
                     if (!isEscaped(t, idx)) {
                        t = t.substring(0, idx) + '_' + t.substring(idx+1);
                        hasWild = true;
                     }
                     idx++;
                  }
               }
            
               if (t.length() > 0) {
                  boolean neg = t.charAt(0) == '!';
                  if (neg) t = t.substring(1);
               
                  if (neg) negative.addElement(t);
                  else     positive.addElement(t);
               }
            }
         } catch(NoSuchElementException nsee) {
            positive.clear();
            negative.clear();
            ret = new StringBuffer(" ");
            DebugPrint.printlnd(DebugPrint.WARN, 
                               "Error parsing filter data");
            DebugPrint.println(DebugPrint.WARN, nsee);               
         }
      
         if (positive.size() > 0) {
            
            Enumeration enum = positive.elements();
            if (wild) {
               ret.append(" AND (");
            } else {
               ret.append(" AND ").append(v).append(" IN (");
            }
            
            int i = 0;
            while(enum.hasMoreElements()) {
               String t = (String)enum.nextElement();
               
               if (wild) {
                  if (i++ > 0) ret.append(" OR ");
                  ret.append(v).append(" LIKE '").append(t).append("' ");
                  ret.append(" ESCAPE '\\' ");
               } else {
                  if (i++ > 0) ret.append(",");
                  ret.append("'").append(t).append("'");
               }
               
            }
            ret.append(") ");
         }
         
         if (negative.size() > 0) {
            
            Enumeration enum = negative.elements();
            if (wild) {
               ret.append(" AND (");
            } else {
               ret.append(" AND ").append(v).append(" NOT IN (");
            }
            
            int i = 0;
            while(enum.hasMoreElements()) {
               String t = (String)enum.nextElement();
               
               if (wild) {
                  if (i++ > 0) ret.append(" OR ");
                  ret.append(v).append(" NOT LIKE '").append(t).append("' ");
                  ret.append(" ESCAPE '\\' ");
               } else {
                  if (i++ > 0) ret.append(",");
                  ret.append("'").append(t).append("'");
               }
               
            }
            ret.append(") ");
         }
         
      }
      return ret.toString();
   }
       
  // Returns true of the char at idx is escaped, false otherwise 
   public boolean isEscaped(String t, int idx) {
      if (idx <= 0 || t.charAt(idx-1) != '\\') return false;
      
      int i=1;
      idx -= 2;
      while(idx >= 0 && t.charAt(idx) == '\\') {
         i++;
         idx--;
      }
      
     // If number of found slashes is odd, then its escaped
      return (i & 1) != 0;
   }
   
  // --------------------------------------------
  // ----- Other (misc) code  
  // --------------------------------------------
  
   
  // Returns a 'where' clause which will limit the packages for a user. It should
  //  be used like so: 
  //
  //     append(" where <my normal where stuff> ").append(getVisiblePackageList());
  //
  // Uses pacl, a and lp as table ids, assumes you want to check if p.pkgid is an 
  //  accessible package. Use sub-select with in to search ... switched from table 
  //  join, as that messes up our counts
  //
   String getVisiblePackageList(Credentials credentials) {
      String ret = " ";
      
      if (!credentials.isSuper()) {               
         StringBuffer where = new StringBuffer();
         
         
        // He is owner
         where.append(" AND (p.OWNERID='");
         where.append(credentials.getName()).append("'");
         
         where.append(" OR (p.PKGSTAT=");
         where.append(DropboxGenerator.STATUS_COMPLETE);
         
         where.append(" AND 0 < ");
         where.append("(select count(*) from EDESIGN.PKGACL pacl,EDESIGN.ALLOWSEND a where ");

         
        // OR, the package is complete, AND he is allowed to see it AND he 
        // has access via group or direct acl (not supporting by project 
        // at this time
         where.append(" p.PKGID=pacl.PKGID ");
         
         where.append(" AND ('IBM' in (p.company, '");
         where.append(credentials.getCompany()).append("') ");
         
         where.append("OR (((a.FROMTYPE='U' AND a.FROMNAME in (p.OWNERID,'*')) OR ");
         where.append("(a.FROMTYPE='C' AND a.FROMNAME in (p.COMPANY,'*')) ) AND");
         where.append("((a.TOTYPE ='U' AND a.TONAME in ('");
         where.append(credentials.getName()).append("', '*')) OR ");
         where.append(" (a.TOTYPE ='C' AND a.TONAME in ('");
         where.append(credentials.getCompany()).append("', '*'))))) ");
         
         where.append(" AND (");
         where.append("(USERTYPE=").append(DropboxGenerator.STATUS_NONE);
         where.append(" AND pacl.USERID in ('");
         where.append(credentials.getName()).append("', '*'))  OR ");
         
         where.append("(USERTYPE=").append(DropboxGenerator.STATUS_GROUP);
         where.append(" AND pacl.USERID in ");
         where.append("(select gm.GROUPNAME from edesign.GROUPMEMBERS gm ");
         where.append("where gm.USERID='"); 
         where.append(credentials.getName()).append("')))))) ");
         ret = where.toString();
      }
      return ret;
   }
  
  
  // Return a string of either left, right, or center as appropriate. Default is left
   public String getAlignment(String key) {
      String ret = "left";
      
      if (key.equalsIgnoreCase(PackageForm.PackageSize)    ||
          key.equalsIgnoreCase(PackageForm.NumFiles)       ||
          key.equalsIgnoreCase(PackageForm.NumPackages)    ||
          key.equalsIgnoreCase(PackageForm.AvgNumFiles)    ||
          key.equalsIgnoreCase(PackageForm.AvgPackageSize) ||
          
          key.equalsIgnoreCase(FileForm.FileSize)          ||
          key.equalsIgnoreCase(FileForm.NumComponents)     ||
          key.equalsIgnoreCase(FileForm.XferRate)          ||
          key.equalsIgnoreCase(FileForm.NumFiles)          ||
          key.equalsIgnoreCase(FileForm.AvgNumComponents)  ||
          key.equalsIgnoreCase(FileForm.AvgFileSize)       ||
          key.equalsIgnoreCase(FileForm.AvgXferRate)       ||
          
          key.equalsIgnoreCase(SessionForm.BytesUp)        ||
          key.equalsIgnoreCase(SessionForm.BytesDown)      ||
          key.equalsIgnoreCase(SessionForm.Bytes)          ||
          key.equalsIgnoreCase(SessionForm.AvgBytesUp)     ||
          key.equalsIgnoreCase(SessionForm.AvgBytesDown)   ||
          key.equalsIgnoreCase(SessionForm.AvgBytes)       ||
          key.equalsIgnoreCase(SessionForm.NumSessions)) {
         ret = "right";
      }
          
      return ret;
   }
   
   public String formatAsString(String key, Object obj) {
      if (obj instanceof java.util.Date) {
         return dateAsString((java.util.Date)obj);
      } else if (key.equalsIgnoreCase(SessionForm.Duration) ||
                 key.equalsIgnoreCase(SessionForm.AvgDuration)) {
        // Ok, we have Seconds, lets make a nice day:hr:min:sec string out of it
         try {
            long v = Long.parseLong(obj.toString());
            return SessionForm.getDuration(v);
         } catch(Exception e) {
            return obj.toString();
         }
      } else {
         return obj.toString();
      }
   }
   
   public String dateAsSqlDateString(java.util.Date d) {
      return "'" + dateAsString(d) + "'";
   }
   
   public String dateAsString(java.util.Date d) {
      Calendar sc = Calendar.getInstance();
      sc.setTime(d);
      int yr  = sc.get(Calendar.YEAR);
      int mon = sc.get(Calendar.MONTH)+1;
      int day = sc.get(Calendar.DAY_OF_MONTH);
      int hr  = sc.get(Calendar.HOUR_OF_DAY);
      int min = sc.get(Calendar.MINUTE);
      int sec = sc.get(Calendar.SECOND);
      
      String scS = 
         yr  + ((mon < 10)?"-0":"-") +
         mon + ((day < 10)?"-0":"-") +
         day + ((hr  < 10)?"-0":"-") +
         hr  + ((min < 10)?".0":".") +
               min + ((sec < 10)?".0":".") +
         sec;
      return scS;
   }
   
  // --------------------------------------------
  // ----- Dropbox/AMT Connection
  // --------------------------------------------
   
   ReloadingProperty getDesktopProperties() {
     // Setup for Dropbox Reports
      ReloadingProperty prop = new ReloadingProperty();
      
     // Get ODC properties
      try {
         PropertyResourceBundle AppProp = (PropertyResourceBundle)
            PropertyResourceBundle.getBundle("edesign_edodc_desktop");
         
         ConfigObject cfgobj = new ConfigObject();
         Enumeration keys = AppProp.getKeys();
         while(keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            cfgobj.setProperty(key, AppProp.getString(key));
         }
         prop.bulkLoad(cfgobj);
      } catch ( Exception e ) {
         DebugPrint.println(DebugPrint.ERROR, 
                            "Property file edesign_edodc_desktop not found");
      }
      return prop;
   }
   
   DBConnection getDropboxConnection() {
      DBConnection conn = DBSource.getDBConnection("dropbox");
      if (conn == null) {
        // This guy will get the AMT and EDODC datasources set up
         oem.edge.ed.odc.dropbox.server.DropboxAccessSrv.getSingleton();
      
        /*
         ReloadingProperty prop = getDesktopProperties();
         conn = new DBConnDataSource();
         conn.setPasswordDir(prop.getProperty("edodc.dbPwdPath"));
         conn.setURL("jdbc/dropbox");
         DBSource.addDBConnection("dropbox", conn, false);
        */
      }
      return conn;
   }
   
   DBConnection getAMTConnection() {
      DBConnection conn = DBSource.getDBConnection("AMT");
      if (conn == null) {
        // This guy will get the AMT and EDODC datasources set up
         oem.edge.ed.odc.dropbox.server.DropboxAccessSrv.getSingleton();
        /*
         ReloadingProperty prop = getDesktopProperties();
         conn = new DBConnDataSource();
         conn.setPasswordDir(prop.getProperty("edodc.dbPwdPath"));
         conn.setURL("jdbc/edodc");
         DBSource.addDBConnection("AMT", conn, false);
        */
      }
      return conn;
   }
   
   void addSavedReports(HttpServletRequest request, 
                        Credentials credentials) throws Exception {
                        
     // Can decide who gets saved reports here
      if (true || credentials.isSuper()) {
         request.setAttribute("savedreports", getSavedReports(credentials.getName()));
      }
      
   }
   
   
  // The UserInfo object was put in the session by the entity that started us
  //  in the first place. That is when its determined WHAT powers we have
   Credentials getDropboxCredentials(HttpSession session) {
      if (session == null) return null;
      UserInfo uinfo = (UserInfo)session.getAttribute("dboxreport_credentials");
      if (uinfo != null && !(uinfo instanceof Credentials)) {
         uinfo = new Credentials(uinfo);
         
         try {
            DBConnection conn = getAMTConnection();
           //Vector amtvec = AMTQuery.getAMTByUser(uinfo.getName());
            Vector amtvec = UserRegistryFactory.getInstance().lookup(
               uinfo.getName(), false, true, false);
               
            if (amtvec != null && amtvec.size() == 1) {
               AMTUser amtuser = (AMTUser)amtvec.elementAt(0);
               Vector projects = amtuser.getProjects();
               if (projects != null) uinfo.addProjects(projects);
               uinfo.setIBMDept(amtuser.getIBMDept());
               uinfo.setIBMDiv(amtuser.getIBMDiv());
               ((Credentials)uinfo).setIsSuper(amtuser.isEntitled("DSGN_DBOX_SUPER"));
               ((Credentials)uinfo).setIsFSESuper(amtuser.isEntitled("DSGN_VIEW_PACKAGE"));
              // JMC 5/10/05 - Req changed. The entitlement is enough to spy. Forget
              //                having to check FSE projects and the like.
              // ((Credentials)uinfo).setFSEProjects(AMTQuery.getFSEProjects(uinfo.getName()));
               if (((Credentials)uinfo).isSuper()) {
                  DebugPrint.printlnd(DebugPrint.INFO2, 
                                      "DropboxReports: Super has arrived: \n" + 
                                      uinfo.toString());
               }
               if (((Credentials)uinfo).isFSESuper()) {
                  DebugPrint.printlnd(DebugPrint.INFO2, 
                                      "DropboxReports: FSESuper has arrived: \n" + 
                                      uinfo.toString());
               }
            }
         } catch(DBException dbe) {
            DebugPrint.printlnd(DebugPrint.WARN, 
                                "Error getting AMT credentials for DboxReporting");
                                
            DebugPrint.printlnd(DebugPrint.WARN, dbe);
         }
         
        // Hack to get isSuper for test
         if (!((Credentials)uinfo).isSuper()) {
            boolean issuper = uinfo.getProjects().contains("DSGN_DBOX_SUPER");
            ((Credentials)uinfo).setIsSuper(issuper);
            if (((Credentials)uinfo).isSuper()) {
               DebugPrint.printlnd(DebugPrint.INFO2, 
                                   "DropboxReports: Super (hack) has arrived: \n" + 
                                   uinfo.toString());
            }
         }
         if (!((Credentials)uinfo).isFSESuper()) {
            boolean issuper = uinfo.getProjects().contains("DSGN_VIEW_PACKAGE");
            ((Credentials)uinfo).setIsFSESuper(issuper);
            if (((Credentials)uinfo).isFSESuper()) {
               DebugPrint.printlnd(DebugPrint.INFO2, 
                                   "DropboxReports: FSESuper (hack) has arrived: \n" + 
                                   uinfo.toString());
            }
         }
         session.setAttribute("dboxreport_credentials", uinfo);
      }
      return (Credentials)uinfo;
   }

  // --------------------------------------------
  // ----- DispatchAction specific
  // --------------------------------------------
   
  /*
  ** DispatchAction is a special type of action which looks up which method to call
  **  with the action itself. This is nice, as it cuts down on the number of Action
  **  classes needed to implement your web app. 
  **
  **
  ** The DispatchAction getMethodName method is used to cull the method to call from
  **  the parameter and the request. The default impl uses the parmameter parm as a
  **  key into the request.getParameter space ... I use the incoming parameter variable
  **  itself, and chop off the name at the first : I see
  **
  **  This last part allows be to set arbitrary switches that can be used elsewhere
  **   that the mapping is passed (like into the ActionForm's reset method)
  **
  ** This trick only works for Struts 1.2 or higher ... we are using 1.1 right now,
  **  so this method is NOT an override of the super class, and is never called. When
  **  we move to 1.2, this will be used. For now (1.1), I fix this by actually calling
  **  out the mapping name using the :RESET: and the like. Will drop that when we go 
  **  to 1.2
  **
  ** Flash, I added an execute method to do what I need. ;-)
  */
   protected String getMethodName(ActionMapping mapping,
                                  ActionForm form,
                                  HttpServletRequest request,
                                  HttpServletResponse response,
                                  String parameter)
     throws Exception {
     
      DebugPrint.printlnd(DebugPrint.INFO5, "In getmethodname: " + parameter);
     
      int idx = parameter.indexOf(":");
      if (idx >= 0) parameter = parameter.substring(0, idx);
      
      DebugPrint.printlnd(DebugPrint.INFO5, "Returning: " + parameter);
      
      return parameter;
   }
   
   public ActionForward execute(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
      throws Exception {
      
     // Get the parameter setting from mapping ... if none, let parent handle
      String parameter = mapping.getParameter();
      if (parameter == null) {
         return super.execute(mapping, form, request, response);
      }
      
      String name = getMethodName(mapping, form, request, response, parameter);
      
     // Invoke the named method, and return the result
      return dispatchMethod(mapping,form,request,response,name);
   }
   

  // --------------------------------------------
  // ----- Common modifyField action 
  // --------------------------------------------
   
   
   public ActionForward modifyField(ActionMapping       mapping,
                                    ActionForm          form,
                                    HttpServletRequest  request,
                                    HttpServletResponse response)
      throws Exception {
      
      DebugPrint.printlnd(DebugPrint.INFO5, "In modifyField");
      
      HttpSession session = request.getSession(false);
         
      Credentials credentials = getDropboxCredentials(session);
      if (credentials == null) {
         return mapping.findForward("sessionExpired");
      }
         
      BaseForm model = (BaseForm)form;
      
      String fieldname  = model.getModifyFieldName();
      String fieldvalue = model.getModifyFieldValue();
      
      if (fieldname != null && fieldvalue != null) {
         model.modifyField(fieldname, fieldvalue);         
      } else {
         DebugPrint.printlnd(DebugPrint.WARN,
                             "modifyField called, fieldname=" + fieldname + 
                             " fieldvalue=" + fieldvalue);
      }
      
      return mapping.findForward("success");
   }   
   
  // --------------------------------------------
  // ----- Common validateSession action
  // --------------------------------------------
      
   public ActionForward validateSession(ActionMapping       mapping,
                                        ActionForm          form,
                                        HttpServletRequest  request,
                                        HttpServletResponse response)
      throws Exception {
      
      DebugPrint.printlnd(DebugPrint.INFO5, "In validateSession");
      
      HttpSession session = request.getSession(false);
         
      Credentials credentials = getDropboxCredentials(session);
      if (credentials == null) {
         return mapping.findForward("sessionExpired");
      }
         
      addSavedReports(request, credentials);
      return mapping.findForward("success");
   }   
   
  // --------------------------------------------
  // ----- Common resetForm action
  // --------------------------------------------
   
   public ActionForward resetForm(ActionMapping       mapping,
                                  ActionForm          form,
                                  HttpServletRequest  request,
                                  HttpServletResponse response)
      throws Exception {
      
      DebugPrint.printlnd(DebugPrint.INFO5, "In resetForm");
      
      HttpSession session = request.getSession(false);
         
      Credentials credentials = getDropboxCredentials(session);
      if (credentials == null) {
         return mapping.findForward("sessionExpired");
      }
         
      if (form instanceof BaseForm) {
         ((BaseForm)form).reset(true);
      }
         
      return mapping.findForward("success");
   }   
   
   
  // --------------------------------------------
  // ----- Stored Reports
  // --------------------------------------------
      
   public SavedReportForm getSavedReport(String username, long id) throws Exception {
      try {
         
         DebugPrint.printlnd(DebugPrint.INFO5, "In getSavedReport: " + id);
         
         DBConnection conn = getDropboxConnection();
         
         Connection connection=null;
         PreparedStatement pstmt=null;
         ResultSet rs=null;	
         int col = 1;
         StringBuffer sql = new StringBuffer("select reportid, reportname, reporttype, datemgmt  from edesign.dboxreports where owner=? and reportid=? ");
         try {	
            
            connection=conn.getConnection();
            
            sql.append(" for read only with UR");
            
            String sqlS = sql.toString();
         
            DebugPrint.println(DebugPrint.INFO5, "Dropbox ReportQ: " + 
                               sqlS);
                               
            pstmt=connection.prepareStatement(sqlS);
            
            pstmt.setString(1, username);
            pstmt.setLong  (2, id);
            
           // Get list of reports
            rs=conn.executeQuery(pstmt);
            
            if (rs.next()) {
               SavedReportForm srf = new SavedReportForm();
               srf.setReportId(""+rs.getLong(1));
               srf.setReportName(rs.getString(2));
               srf.setReportType(rs.getString(3));
               srf.setDateManagement(rs.getString(4));
               return srf;
            }
            
            return null;
            
         } catch (SQLException e){
            conn.destroyConnection(connection);
            connection=null;
            e.printStackTrace(System.out);
            throw e;
         } finally {
            
            if (pstmt!=null){
               try {
                  pstmt.close();
               } catch(SQLException e) {}
            }
            if (conn != null) conn.returnConnection(connection);
         }               
      } catch(Exception ee) {
         System.out.println("Exception processing getSavedReports request:"
                            + ee);
         throw ee;
      }
   }
      
   public Collection getSavedReports(String username) throws Exception {
      try {
         
         DebugPrint.printlnd(DebugPrint.INFO5, "In getSavedReports: " + username);
         
         DBConnection conn = getDropboxConnection();
         
         Connection connection=null;
         PreparedStatement pstmt=null;
         ResultSet rs=null;	
         int col = 1;
         StringBuffer sql = new StringBuffer("select reportid, reportname, reporttype, datemgmt  from edesign.dboxreports where owner=? order by reporttype,reportname ");
         try {	
            
            connection=conn.getConnection();
            
            sql.append(" for read only with UR");
            
            String sqlS = sql.toString();
         
            DebugPrint.println(DebugPrint.INFO5, "Dropbox ReportQ: " + 
                               sqlS);
                               
            pstmt=connection.prepareStatement(sqlS);
            
            pstmt.setString(1, username);
            
           // Get list of reports
            rs=conn.executeQuery(pstmt);
            
           // For some ODD @#&^*& Reason, rs is null when called via /mainPage.do
            DebugPrint.println(DebugPrint.INFO5, "rs == null: " + (rs == null));
            
            Collection ret = new Vector();
            while(rs != null && rs.next()) {
               SavedReportForm srf = new SavedReportForm();
               srf.setReportId(""+rs.getLong(1));
               srf.setReportName(rs.getString(2));
               srf.setReportType(rs.getString(3));
               srf.setDateManagement(rs.getString(4));
               ret.add(srf);
            }
            
            return ret;
            
         } catch (SQLException e){
            conn.destroyConnection(connection);
            connection=null;
            e.printStackTrace(System.out);
            throw e;
         } finally {
            
            if (pstmt!=null){
               try {
                  pstmt.close();
               } catch(SQLException e) {}
            }
            if (conn != null) conn.returnConnection(connection);
         }               
      } catch(Exception ee) {
         System.out.println("Exception processing getSavedReports request:"
                            + ee);
         throw ee;
      }
   }
      
  /*
  ** This action creates a new (potentially) unnamed report in the DB
  **
  ** Essentially, we look up the Session Scoped form object using the reportType
  **  and save its bean-ness to a map/blob in the DB along with other attribs.
  **
  */
   public ActionForward saveReport(ActionMapping       mapping,
                                   ActionForm          form,
                                   HttpServletRequest  request,
                                   HttpServletResponse response)
      throws Exception {
      
      Results results = new Results();
      
      try {
         
         HttpSession session = request.getSession(false);
         
         Credentials credentials = getDropboxCredentials(session);
         if (credentials == null) {
            return mapping.findForward("sessionExpired");
         }
         
         SavedReportForm model = (SavedReportForm)form;
         
         DebugPrint.printlnd(DebugPrint.INFO5, "In saveReport");
         
         DBConnection conn = getDropboxConnection();
         
         Connection connection=null;
         PreparedStatement pstmt=null;
         ResultSet rs=null;	
         int col = 1;
         StringBuffer sql = new StringBuffer("select max(reportid) from edesign.dboxreports where owner=?");
         try {	
            
            connection=conn.getConnection();
            
            sql.append(" for read only with UR");
            
            String sqlS = sql.toString();
         
            DebugPrint.println(DebugPrint.INFO5, "Dropbox ReportQ: " + 
                               sqlS);
                               
            pstmt=connection.prepareStatement(sqlS);
            
            pstmt.setString(1, credentials.getName());
            
           // Find next ID for this user
            rs=conn.executeQuery(pstmt);
            
            long id = 1;
            
            if (rs.next()) {
               id = rs.getLong(1)+1;
            }
            
            pstmt.close();
            
           // Find instance of form attrs we are saving
            
           // Validate that the report type is good
            String reportType = model.getReportType();
            if (!model.getSupportedReportTypes().contains(reportType)) {
               throw new Exception("Invalid report type: " + reportType);
            }
                         
           // Insert info into table, set up for blob update
            sql = new StringBuffer("insert into edesign.dboxreports (owner, serialform, reportname, reporttype, datemgmt, reportid) values(?,?,?,?,?,?)");
            
            sqlS = sql.toString();
            
            DebugPrint.println(DebugPrint.INFO5, "Dropbox ReportQ2: " + 
                               sqlS);
            
            pstmt=connection.prepareStatement(sqlS);
            
           // Get the form type in question from session
            BaseForm bf = (BaseForm)session.getAttribute(reportType);
            
            
           // Create a map that has all bean attrs
           // Map map = BeanUtils.describe(bf);
           
           // The above is just too simplistic. It takes ALL attrs that look like
           //  getXXX ... regardless of whether there is a matching setXXX. Build the
           //  map using beanutils for the property access, but get the property names
           //  myself
           //
           // For each public set method found, look for matching get w/ no parms. 
           //  If found, take that property.
           //
            Class bfclass = bf.getClass();
            Method meths[] = bfclass.getMethods();
            Map map = new HashMap();
            for(int i=0; i < meths.length; i++) {
               Method m = meths[i];
               String n = m.getName();
               Class carr[] = new Class[0];
               if (Modifier.isPublic(m.getModifiers()) &&
                  n.startsWith("set")) {
                  String prop = n.substring(3);
                  if (prop.length() > 0) {
                     
                    // lowcase the first letter
                     String propl=prop.substring(0,1).toLowerCase() + prop.substring(1);
                  
                     try {
                       // Find a getter method with no parms of same prop name
                        if (bfclass.getMethod("get" + prop, carr) != null) {
                          // Got it ... get this bean value into map
                           String val = BeanUtils.getSimpleProperty(bf, propl);
                           map.put(propl, val);
                           DebugPrint.println(DebugPrint.INFO5, 
                                              "   " + propl + " = " + val);
                        }
                     } catch(NoSuchMethodException nsme) {
                     }
                  }
               }
            }
            
           // Save Map to blob
           
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(map);
            oos.close();
            ByteArrayInputStream byteis = new ByteArrayInputStream(baos.toByteArray());
            
            pstmt.setString(1, credentials.getName());
            pstmt.setBinaryStream(2, byteis, byteis.available());
            pstmt.setString(3, model.getReportName().trim());
            pstmt.setString(4, model.getReportType().trim());
            pstmt.setString(5, model.getDateManagement().trim());
            pstmt.setLong  (6, id);
            
            if (conn.executeUpdate(pstmt) != 1) {
               throw new Exception("Insert failed");
            }
            
            addSavedReports(request, credentials);
            
         } catch (SQLException e){
            conn.destroyConnection(connection);
            connection=null;
            e.printStackTrace(System.out);
            throw e;
         } finally {
            
            if (pstmt!=null){
               try {
                  pstmt.close();
               } catch(SQLException e) {}
            }
            if (conn != null) conn.returnConnection(connection);
         }               
         
      } catch(Exception ee) {
         System.out.println("Exception processing saveReport request:"
                            + ee);
         throw ee;
      }
      
      return mapping.findForward("success");
      
   }
   
  /*
  ** This action preps a report save. Takes the incoming report form, and preps the 
  **  request based SavedReportForm with the report type
  */
   public ActionForward updateReportPrep(ActionMapping       mapping,
                                         ActionForm          form,
                                         HttpServletRequest  request,
                                         HttpServletResponse response)
      throws Exception {
      
      try {
         
         HttpSession session = request.getSession(false);
         
         Credentials credentials = getDropboxCredentials(session);
         if (credentials == null) {
            return mapping.findForward("sessionExpired");
         }
         
         DebugPrint.printlnd(DebugPrint.INFO5, "In updateReportPrep");
         
         SavedReportForm srf = (SavedReportForm)form;
         
         String reportIdS = srf.getReportId();
         if (reportIdS == null) reportIdS = "";
         else                   reportIdS = reportIdS.trim();
         
         if (reportIdS.length() == 0) {
            reportIdS = request.getParameter("requestReportId");
            if (reportIdS == null) reportIdS = "";
            else                   reportIdS = reportIdS.trim();
         }
         
         srf.reset(true);
         
         long reportid = Long.parseLong(reportIdS);
                  
         DebugPrint.printlnd(DebugPrint.INFO5, "In UpdateReport");
         
         DBConnection conn = getDropboxConnection();
         
         Connection connection=null;
         PreparedStatement pstmt=null;
         ResultSet rs=null;	
         int col = 1;
         StringBuffer sql = new StringBuffer("select reportname, reporttype, datemgmt from edesign.dboxreports where owner = ? and reportid = ?");
         try {	
            
            connection=conn.getConnection();
            
            sql.append(" for read only with UR");
            
            String sqlS = sql.toString();
         
            DebugPrint.println(DebugPrint.INFO5, "Dropbox ReportQ: " + 
                               sqlS);
                               
            pstmt=connection.prepareStatement(sqlS);
            
            pstmt.setString(1, credentials.getName());
            pstmt.setLong  (2, reportid);
            
           // Find next ID for this user
            rs=conn.executeQuery(pstmt);
            
            long id = 1;
            
            if (!rs.next()) {
               throw new Exception("Report not found");
            }
            
           // Find instance of form 
            String reportname       = rs.getString(1);
            String reporttype       = rs.getString(2);
            String datemgmt         = rs.getString(3);
            
            srf.setReportName(reportname);
            srf.setReportType(reporttype);
            srf.setDateManagement(datemgmt);
            srf.setReportId(reportIdS);
            
            request.setAttribute("SavedReportForm", srf);
            
         } catch (SQLException e){
            conn.destroyConnection(connection);
            connection=null;
            e.printStackTrace(System.out);
            throw e;
         } finally {
            
            if (pstmt!=null){
               try {
                  pstmt.close();
               } catch(SQLException e) {}
            }
            if (conn != null) conn.returnConnection(connection);
         }               
         
      } catch(Exception ee) {
         System.out.println("Exception processing updateReportPrep request:"
                            + ee);
         throw ee;
      }
      
      return mapping.findForward("success");
   }
   
  /*
  ** This action sets up to update an existing report. If req parm called
  */
   public ActionForward updateReport(ActionMapping       mapping,
                                     ActionForm          form,
                                     HttpServletRequest  request,
                                     HttpServletResponse response)
      throws Exception {
      
      Results results = new Results();
      
      try {
         
         HttpSession session = request.getSession(false);
         
         Credentials credentials = getDropboxCredentials(session);
         if (credentials == null) {
            return mapping.findForward("sessionExpired");
         }
         
         SavedReportForm model = (SavedReportForm)form;
         
        // get reportid 
         String reportid = model.getReportId();
         long         id = Long.parseLong(reportid);
         
         DebugPrint.printlnd(DebugPrint.INFO5, "In saveReport");
         
         DBConnection conn = getDropboxConnection();
         
         Connection connection=null;
         PreparedStatement pstmt=null;
         ResultSet rs=null;	
         int col = 1;
         StringBuffer sql = new StringBuffer("update edesign.dboxreports set (datemgmt, reportname) = (?, ?) where owner=? AND reportid = ?");
         try {	
            
            connection=conn.getConnection();
            
            String sqlS = sql.toString();
         
            DebugPrint.println(DebugPrint.INFO5, "Dropbox ReportQ: " + 
                               sqlS);
                               
            pstmt=connection.prepareStatement(sqlS);
            
            pstmt.setString(1, model.getDateManagement());
            pstmt.setString(2, model.getReportName());
            pstmt.setString(3, credentials.getName());
            pstmt.setLong  (4, id);
            
            if (conn.executeUpdate(pstmt) <= 0) {
               throw new Exception("No record found to update");
            }
                         
         } catch (SQLException e){
            conn.destroyConnection(connection);
            connection=null;
            e.printStackTrace(System.out);
            throw e;
         } finally {
            
            if (pstmt!=null){
               try {
                  pstmt.close();
               } catch(SQLException e) {}
            }
            if (conn != null) conn.returnConnection(connection);
         }               
         
      } catch(Exception ee) {
         System.out.println("Exception processing saveReport request:"
                            + ee);
         throw ee;
      }
      
      return mapping.findForward("success");
      
   }
   
   
  /*
  ** This action preps a report save. Takes the incoming report form, and preps the 
  **  request based SavedReportForm with the report type
  */
   public ActionForward saveReportPrep(ActionMapping       mapping,
                                       ActionForm          form,
                                       HttpServletRequest  request,
                                       HttpServletResponse response)
      throws Exception {
      
      try {
         
         HttpSession session = request.getSession(false);
         
         Credentials credentials = getDropboxCredentials(session);
         if (credentials == null) {
            return mapping.findForward("sessionExpired");
         }
         
        // Get the formbean name
         String formname = mapping.getName();
         
         DebugPrint.printlnd(DebugPrint.INFO5, "In saveReportPrep");
         
         SavedReportForm srf = new SavedReportForm();
         srf.reset(true);
         srf.setReportType(formname);
         srf.setReportName("Untitled");
         
         request.setAttribute("SavedReportForm", srf);
         
      } catch(Exception ee) {
         System.out.println("Exception processing savedReportPrep request:"
                            + ee);
         throw ee;
      }
      
      return mapping.findForward("success");
   }
   
  /*
  ** This action launches the specified report
  **
  ** If the form is specified, it should be a SavedReportForm. We use the value of the
  **  reportId. If no form OR if blank/null id, then we look for a request parm named
  **  requestReportId/ The reportid is used to find a report rec, and to acquire the 
  **  report type, which is in turn, used to do a session lookup for the correct form 
  **  type using the reportype. The Blob in the DB is reconstituted (its
  **  a hashmap), and the beanutils function is used to populate the form in question.
  **
  ** The parameter requestreportid describes the report being launched
  **
  ** The saved dateManagement scheme can be overidden using by form or prop
  */
   public ActionForward launchReport(ActionMapping       mapping,
                                     ActionForm          form,
                                     HttpServletRequest  request,
                                     HttpServletResponse response)
      throws Exception {
      
      Results results = new Results();
      
      try {
         
         HttpSession session = request.getSession(false);
         
         Credentials credentials = getDropboxCredentials(session);
         if (credentials == null) {
            return mapping.findForward("sessionExpired");
         }
         
         SavedReportForm model = (SavedReportForm)form;
         
         String reportIdS = model.getReportId();
         if (reportIdS == null) reportIdS = "";
         else                   reportIdS = reportIdS.trim();
         
         if (reportIdS.length() == 0) {
            reportIdS = request.getParameter("requestReportId");
            if (reportIdS == null) reportIdS = "";
            else                   reportIdS = reportIdS.trim();
         }
         
         String dateManagementS = model.getDateManagement();
         if (dateManagementS.length() == 0) {
            dateManagementS = request.getParameter("dateManagement");
            if (dateManagementS == null) dateManagementS = "";
            else                   dateManagementS = dateManagementS.trim();
         }
         
         long reportid = Long.parseLong(reportIdS);         
         
         DebugPrint.printlnd(DebugPrint.INFO5, "In launchReport");
         
         DBConnection conn = getDropboxConnection();
         
         Connection connection=null;
         PreparedStatement pstmt=null;
         ResultSet rs=null;	
         int col = 1;
         StringBuffer sql = new StringBuffer("select serialform, reporttype, datemgmt, reportid, created from edesign.dboxreports where owner = ? and reportid = ?");
         try {	
            
            connection=conn.getConnection();
            
            sql.append(" for read only with UR");
            
            String sqlS = sql.toString();
         
            DebugPrint.println(DebugPrint.INFO5, "Dropbox ReportQ: " + 
                               sqlS);
                               
            pstmt=connection.prepareStatement(sqlS);
            
            pstmt.setString(1, credentials.getName());
            pstmt.setLong  (2, reportid);
            
           // Find next ID for this user
            rs=conn.executeQuery(pstmt);
            
            long id = 1;
            
            if (!rs.next()) {
               throw new Exception("Report not found");
            }
            
           // Find instance of form 
            Blob   blob             = rs.getBlob(1);
            String reporttype       = rs.getString(2);
            String datemgmt         = rs.getString(3);
                   id               = rs.getLong(4);
            java.sql.Timestamp ts   = rs.getTimestamp(5);
            
           // Override the saved datemgmt value with any passed in for the launch
            if (dateManagementS != null && dateManagementS.length() > 0) {
               datemgmt = dateManagementS;
            }
            
            BaseForm bf = (BaseForm)session.getAttribute(reporttype);
            
           // If we don't have our session scoped form created yet, then 
           //  create it ourselves
            if (bf == null) {
               FormBeanConfig fbc = 
                  mapping.getModuleConfig().findFormBeanConfig(reporttype);
                  
              // Both of these are struts 1.2 ... MAN this is frustrating
              //bf = (BaseForm)fbc.createActionForm(getServlet());
              //bf = (BaseForm)RequestUtils.createActionForm(fbc, getServlet());
               bf = (BaseForm)Class.forName(fbc.getType()).newInstance();
               
               
               session.setAttribute(reporttype, bf);
            }
            
            ObjectInputStream ois = new ObjectInputStream(blob.getBinaryStream());
            Map map = (Map)ois.readObject();
            ois.close();
            
           // Populate Form using reconstituted map
            bf.reset(true);
            
           // If Date management scheme is different than ABSOLUTE ... do it
            int val = Integer.parseInt(datemgmt);
            if (val != SavedReportForm.ABSOLUTE) {
            
              // All of the date fields use the same convention. 
              //  Read Day, Month and Year from map, update, and stuff it back
              //  Except for Relative, all other schemes work off of todays
              //  date.
              
               Calendar cal = Calendar.getInstance();
               cal.setTime(new java.util.Date());
               cal.setLenient(true);
               long ntime = cal.getTime().getTime();
                  
               
               String arr[] = new String[] { "",
                                             "downloadFilter",
                                             "createFilter",
                                             "deleteFilter",
                                             "commitFilter" };
               
               if (val != SavedReportForm.RELATIVE) {
                  int sm, sd, sy, em, ed, ey;
               
                 // When adjusting the date, cal.add recomputes right away, set does 
                 //  so order matters.
                  switch(val) {
                     case SavedReportForm.LASTMONTH:
                     
                        cal.set(Calendar.DAY_OF_MONTH, 1);
                        cal.add(Calendar.MONTH, -1);
                        
                        sm = cal.get(Calendar.MONTH)+1;
                        sd = cal.get(Calendar.DAY_OF_MONTH);
                        sy = cal.get(Calendar.YEAR);
                        
                        cal.set(Calendar.DAY_OF_MONTH, 
                                cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                        cal.getTime();                        
                        em = cal.get(Calendar.MONTH)+1;
                        ed = cal.get(Calendar.DAY_OF_MONTH);
                        ey = cal.get(Calendar.YEAR);
                        
                        break;
                     case SavedReportForm.THISMONTH:
                        
                        em = cal.get(Calendar.MONTH)+1;
                        ed = cal.get(Calendar.DAY_OF_MONTH);
                        ey = cal.get(Calendar.YEAR);
                        
                        cal.set(Calendar.DAY_OF_MONTH, 1);
                        
                        sm = cal.get(Calendar.MONTH)+1;
                        sd = cal.get(Calendar.DAY_OF_MONTH);
                        sy = cal.get(Calendar.YEAR);
                        
                        break;
                     case SavedReportForm.ELAPSEDMONTH:
                     
                        em = cal.get(Calendar.MONTH)+1;
                        ed = cal.get(Calendar.DAY_OF_MONTH);
                        ey = cal.get(Calendar.YEAR);
                        
                        cal.add(Calendar.MONTH, -1);
                        cal.add(Calendar.DAY_OF_MONTH, 1);
                        
                        sm = cal.get(Calendar.MONTH)+1;
                        sd = cal.get(Calendar.DAY_OF_MONTH);
                        sy = cal.get(Calendar.YEAR);
                        
                        break;
                     case SavedReportForm.LASTQUARTER:
                     
                       // Back up to previous quarter
                        cal.set(Calendar.DAY_OF_MONTH, 1);
                        cal.getTime();
                        cal.add(Calendar.MONTH, -(3+(cal.get(Calendar.MONTH)%3)));
                        
                        sm = cal.get(Calendar.MONTH)+1;
                        sd = cal.get(Calendar.DAY_OF_MONTH);
                        sy = cal.get(Calendar.YEAR);
                        
                        cal.add(Calendar.MONTH, 2);
                        cal.set(Calendar.DAY_OF_MONTH, 
                                cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                        cal.getTime();
                     
                        em = cal.get(Calendar.MONTH)+1;
                        ed = cal.get(Calendar.DAY_OF_MONTH);
                        ey = cal.get(Calendar.YEAR);
                     
                        break;
                     case SavedReportForm.THISQUARTER:
                     
                        em = cal.get(Calendar.MONTH)+1;
                        ed = cal.get(Calendar.DAY_OF_MONTH);
                        ey = cal.get(Calendar.YEAR);
                        
                       // Back up to front of this quarter
                        cal.set(Calendar.DAY_OF_MONTH, 1);
                        cal.getTime();
                        cal.add(Calendar.MONTH, -((cal.get(Calendar.MONTH)%3)));
                        
                        sm = cal.get(Calendar.MONTH)+1;
                        sd = cal.get(Calendar.DAY_OF_MONTH);
                        sy = cal.get(Calendar.YEAR);
                     
                        break;
                     case SavedReportForm.ELAPSEDQUARTER:
                     
                        em = cal.get(Calendar.MONTH)+1;
                        ed = cal.get(Calendar.DAY_OF_MONTH);
                        ey = cal.get(Calendar.YEAR);
                        
                       // Back up to front of this quarter
                        cal.getTime();
                        cal.add(Calendar.MONTH, -3);
                        cal.add(Calendar.DAY_OF_MONTH, 1);
                        
                        sm = cal.get(Calendar.MONTH)+1;
                        sd = cal.get(Calendar.DAY_OF_MONTH);
                        sy = cal.get(Calendar.YEAR);
                     
                        break;
                     case SavedReportForm.LASTYEAR:
                     
                        cal.set(Calendar.MONTH, 0);
                        cal.set(Calendar.DAY_OF_MONTH, 1);
                        cal.add(Calendar.YEAR , -1);
                     
                        sm = cal.get(Calendar.MONTH)+1;
                        sd = cal.get(Calendar.DAY_OF_MONTH);
                        sy = cal.get(Calendar.YEAR);
                        
                        cal.set(Calendar.MONTH, 11);
                        cal.set(Calendar.DAY_OF_MONTH, 31);
                        cal.getTime();
                        
                        em = cal.get(Calendar.MONTH)+1;
                        ed = cal.get(Calendar.DAY_OF_MONTH);
                        ey = cal.get(Calendar.YEAR);
                        
                        break;
                     case SavedReportForm.THISYEAR:
                     
                        em = cal.get(Calendar.MONTH)+1;
                        ed = cal.get(Calendar.DAY_OF_MONTH);
                        ey = cal.get(Calendar.YEAR);
                        
                        cal.set(Calendar.MONTH, 0);
                        cal.set(Calendar.DAY_OF_MONTH, 1);
                        cal.getTime();
                        
                        sm = cal.get(Calendar.MONTH)+1;
                        sd = cal.get(Calendar.DAY_OF_MONTH);
                        sy = cal.get(Calendar.YEAR);
                        
                        break;
                     case SavedReportForm.ELAPSEDYEAR:
                     
                        em = cal.get(Calendar.MONTH)+1;
                        ed = cal.get(Calendar.DAY_OF_MONTH);
                        ey = cal.get(Calendar.YEAR);
                        
                        cal.add(Calendar.YEAR, -1);
                        cal.add(Calendar.DAY_OF_MONTH, 1);
                        
                        sm = cal.get(Calendar.MONTH)+1;
                        sd = cal.get(Calendar.DAY_OF_MONTH);
                        sy = cal.get(Calendar.YEAR);
                     
                        break;
                     default:
                        em=ed=ey=sm=sd=sy = 0;
                        break;  // Hmmm
                  }
                  
                                                
                 // We add ALL of them to the map regardless. No harm
                  for (int i = 0; i < arr.length; i++) {
                     String endS   = arr[i] + "End";
                     String startS = arr[i] + "Start";
                     if (arr[i].length() == 0) {
                        endS = "end"; startS = "start";
                     }
                     map.put(startS + "Year",  "" + sy);
                     map.put(startS + "Month", "" + sm);
                     map.put(startS + "Day",   "" + sd);
                     map.put(endS   + "Year",  "" + ey);
                     map.put(endS   + "Month", "" + em);
                     map.put(endS   + "Day",   "" + ed);
                     
                  }
                  System.out.println("Map = \n\n" + map.toString());
                  
               } else {
                  
                 // Get cal for Report creation date
                  Calendar ccal = Calendar.getInstance();
                  ccal.setTime(ts);
                  ccal.setLenient(true);
                  
                  
                 // Loop over all possible targets. If we get a NullPtr or any 
                 //  exception, we just skip it
                  for (int i = 0; i < arr.length; i++) {
                     String endS   = arr[i] + "End";
                     String startS = arr[i] + "Start";
                     if (arr[i].length() == 0) {
                        endS = "end"; startS = "start";
                     }
                     
                     try {
                     
                       // Get all of the start/end day/month/year vals.
                        String ssyS = startS+"Year";
                        String syS = (String)map.get(ssyS);
                        int sy = Integer.parseInt(syS);
                        String ssmS = startS+"Month";
                        String smS = (String)map.get(ssmS);
                        int sm = Integer.parseInt(smS);
                        String ssdS = startS+"Day";
                        String sdS = (String)map.get(ssdS);
                        int sd = Integer.parseInt(sdS);
                        String seyS = endS+"Year";
                        String eyS = (String)map.get(seyS);
                        int ey = Integer.parseInt(eyS);
                        String semS = endS+"Month";
                        String emS = (String)map.get(semS);
                        int em = Integer.parseInt(emS);
                        String sedS = endS+"Day";
                        String edS = (String)map.get(sedS);
                        int ed = Integer.parseInt(edS);
                        
                        Calendar scal = Calendar.getInstance();
                        scal.setLenient(true);
                        scal.set(Calendar.MONTH, sm-1);
                        scal.set(Calendar.YEAR, sy);
                        scal.set(Calendar.DAY_OF_MONTH, sd);
                        scal.set(Calendar.HOUR_OF_DAY, 0);
                        scal.set(Calendar.MINUTE, 0);
                        scal.set(Calendar.SECOND, 0);
                        scal.set(Calendar.MILLISECOND, 0);
                        scal.getTime();
                        
                        Calendar ecal = Calendar.getInstance();
                        ecal.setLenient(true);
                        ecal.set(Calendar.MONTH, em-1);
                        ecal.set(Calendar.YEAR, ey);
                        ecal.set(Calendar.DAY_OF_MONTH, ed);
                        ecal.set(Calendar.HOUR_OF_DAY, 23);
                        ecal.set(Calendar.MINUTE, 59);
                        ecal.set(Calendar.SECOND, 59);
                        ecal.set(Calendar.MILLISECOND, 999);
                        ecal.getTime();
                        
                        long ctime = ccal.getTime().getTime();
                        long stime = scal.getTime().getTime();
                        long etime = ecal.getTime().getTime();
                        
                        scal.setTime(new java.util.Date(ntime-(ctime-stime)));
                        scal.getTime();
                        
                        ecal.setTime(new java.util.Date(ntime-(ctime-etime)));
                        ecal.getTime();
                        
                        sm = scal.get(Calendar.MONTH)+1;
                        sd = scal.get(Calendar.DAY_OF_MONTH);
                        sy = scal.get(Calendar.YEAR);
                        em = ecal.get(Calendar.MONTH)+1;
                        ed = ecal.get(Calendar.DAY_OF_MONTH);
                        ey = ecal.get(Calendar.YEAR);
                        
                       // Update map
                        map.put(ssyS, "" + sy);
                        map.put(ssmS, "" + sm);
                        map.put(ssdS, "" + sd);
                        map.put(seyS, "" + ey);
                        map.put(semS, "" + em);
                        map.put(sedS, "" + ed);
                        
                     } catch(Exception ee) {
                     }
                  }
               }
            }
            
            BeanUtils.populate(bf, map);
            
            return mapping.findForward(reporttype);
            
         } catch (SQLException e){
            conn.destroyConnection(connection);
            connection=null;
            e.printStackTrace(System.out);
            throw e;
         } finally {
            
            if (pstmt!=null){
               try {
                  pstmt.close();
               } catch(SQLException e) {}
            }
            if (conn != null) conn.returnConnection(connection);
         }               
         
      } catch(Exception ee) {
         System.out.println("Exception processing launchReport request:"
                            + ee);
         throw ee;
      }
      
   }
   
  /*
  ** This action sets up for the delete of a saved report from the DB (we fwd to
  **  the question 'are you sure'
  */
   public ActionForward deleteReportQ(ActionMapping       mapping,
                                      ActionForm          form,
                                      HttpServletRequest  request,
                                      HttpServletResponse response)
      throws Exception {
      
      try {
         
         HttpSession session = request.getSession(false);
         
         Credentials credentials = getDropboxCredentials(session);
         if (credentials == null) {
            return mapping.findForward("sessionExpired");
         }
         
         SavedReportForm model = (SavedReportForm)form;
         
         DebugPrint.printlnd(DebugPrint.INFO5, "In deleteReportQ");
         
         DBConnection conn = getDropboxConnection();
         
         Connection connection=null;
         PreparedStatement pstmt=null;
         ResultSet rs=null;	
         int col = 1;
         StringBuffer sql = new StringBuffer("select from edesign.dboxreports where owner=? AND reportid = ?");
         try {	
            
            String reportIdS = model.getReportId();
            if (reportIdS == null) reportIdS = "";
            else                   reportIdS = reportIdS.trim();
            
            if (reportIdS.length() == 0) {
               reportIdS = request.getParameter("requestReportId");
               if (reportIdS == null) reportIdS = "";
               else                   reportIdS = reportIdS.trim();
            }
            
            if (reportIdS.length() == 0) {
               throw new Exception("No report ID specified");
            }
            
            long reportid = Long.parseLong(reportIdS);
            
            SavedReportForm srf = getSavedReport(credentials.getName(), reportid);
            if (srf == null) {
               throw new Exception("Invalid report id: " + reportIdS);
            }
            
            request.setAttribute("SavedReportForm", srf);
            
         } catch (SQLException e){
            conn.destroyConnection(connection);
            connection=null;
            e.printStackTrace(System.out);
            throw e;
         } finally {
            
            if (pstmt!=null){
               try {
                  pstmt.close();
               } catch(SQLException e) {}
            }
            if (conn != null) conn.returnConnection(connection);
         }               
         
      } catch(Exception ee) {
         System.out.println("Exception processing deleteReport request:"
                            + ee);
         throw ee;
      }
      
      return mapping.findForward("success");
      
   }
   
   
  /*
  ** This action deletes the selected reportId from the DB. If the incoming FORM
  **  reportId is empty, we look to the requestReportId request var.
  */
   public ActionForward deleteReport(ActionMapping       mapping,
                                     ActionForm          form,
                                     HttpServletRequest  request,
                                     HttpServletResponse response)
      throws Exception {
      
      try {
         
         HttpSession session = request.getSession(false);
         
         Credentials credentials = getDropboxCredentials(session);
         if (credentials == null) {
            return mapping.findForward("sessionExpired");
         }
         
         SavedReportForm model = (SavedReportForm)form;
         
         DebugPrint.printlnd(DebugPrint.INFO5, "In deleteReport");
         
         DBConnection conn = getDropboxConnection();
         
         Connection connection=null;
         PreparedStatement pstmt=null;
         ResultSet rs=null;	
         int col = 1;
         StringBuffer sql = new StringBuffer("delete from edesign.dboxreports where owner=? AND reportid = ?");
         try {	
            
            String reportIdS = model.getReportId();
            if (reportIdS == null) reportIdS = "";
            else                   reportIdS = reportIdS.trim();
            
            if (reportIdS.length() == 0) {
               reportIdS = request.getParameter("requestReportId");
               if (reportIdS == null) reportIdS = "";
               else                   reportIdS = reportIdS.trim();
            }
            
            if (reportIdS.length() == 0) {
               throw new Exception("No report ID specified");
            }
            
            long reportid = Long.parseLong(reportIdS);
            
            connection=conn.getConnection();
            
            String sqlS = sql.toString();
         
            DebugPrint.println(DebugPrint.INFO5, "Dropbox ReportQ: " + 
                               sqlS);
                               
            pstmt=connection.prepareStatement(sqlS);
            
            pstmt.setString(1, credentials.getName());
            pstmt.setLong  (2, reportid);
            
            if (conn.executeUpdate(pstmt) != 1) {
               throw new Exception("Delete failed");
            }
         } catch (SQLException e){
            conn.destroyConnection(connection);
            connection=null;
            e.printStackTrace(System.out);
            throw e;
         } finally {
            
            if (pstmt!=null){
               try {
                  pstmt.close();
               } catch(SQLException e) {}
            }
            if (conn != null) conn.returnConnection(connection);
         }               
         
         addSavedReports(request, credentials);
         
      } catch(Exception ee) {
         System.out.println("Exception processing deleteReport request:"
                            + ee);
         throw ee;
      }
      
      return mapping.findForward("success");
      
   }
   
   
  // --------------------------------------------
  // ----- Session Report Impl
  // --------------------------------------------
      
  //
  // All session queries come thru here
  //
   public ActionForward sessionQuery(ActionMapping       mapping,
                                     ActionForm          form,
                                     HttpServletRequest  request,
                                     HttpServletResponse response)
      throws Exception {
      
      Results results = new Results();
      
      try {
         
         HttpSession session = request.getSession(false);
         
         Credentials credentials = getDropboxCredentials(session);
         if (credentials == null) {
            return mapping.findForward("sessionExpired");
         }
         
         SessionForm model = (SessionForm)form;
         
         DebugPrint.printlnd(DebugPrint.INFO5, "In sessionQuery");
         
         DBConnection conn = getDropboxConnection();
         
         Connection connection=null;
         PreparedStatement pstmt=null;
         ResultSet rs=null;	
         int col = 1;
         StringBuffer sql = new StringBuffer("select '1' as BOGUS");
         try {	
            
           // HACK ... fix this
            String sortfield = model.getSortField();
            String sortdir   = model.getSortDirection();
            
            DebugPrint.printlnd(DebugPrint.INFO5, "Sortfield=" + sortfield);
            DebugPrint.printlnd(DebugPrint.INFO5, "Sortdir=" + sortdir);
            
            if (sortdir == null ||
                (!sortdir.equalsIgnoreCase("ASC") && 
                 !sortdir.equalsIgnoreCase("DESC"))) {
               sortdir = "ASC";
            }
            
            if (sortfield == null) sortfield = "";
            
            connection=conn.getConnection();
            
            Vector groupby = new Vector();
            Vector orderby = new Vector();
            
            StringBuffer where = new StringBuffer();
            
           // If this guy is NOT a Super User, then scope what he can see to himself
            if (!credentials.isSuper()) {
               where.append(generateFilter("\"" + credentials.getName() + "\"",    
                                           "OWNER"));
               
              // for good measure
               where.append(generateFilter("\"" + credentials.getCompany() + "\"", 
                                           "COMPANY"));
            }
            
            boolean recordlev = model.getRecordLevel().equalsIgnoreCase("true");
            
            String s = model.User;
            if (model.getUser()) {
               results.addHeader(s, "User");
               sql.append(",owner as ").append(s);
               col++;
               if (sortfield.equals(s)) orderby.addElement(""+col);
               if (!recordlev) {
                  groupby.add("owner");
               }
            }
            
           // Apply filter
            if (model.getUserFilter()) {
               where.append(generateFilter(model.getUserFilterValue(),
                                           "OWNER"));
            }
            
            s = model.Company;
            if (model.getCompany()) {
               results.addHeader(s, "Company");
               sql.append(",company as ").append(s);
               col++;
               if (sortfield.equals(s)) orderby.addElement(""+col);
               if (!recordlev) {
                  groupby.add("company");
               }
            }
            
           // Apply filter
            if (model.getCompanyFilter()) {
               where.append(generateFilter(model.getCompanyFilterValue(),
                                           "COMPANY"));
            }
            
            s = model.ClientType;
            if (model.getClientType()) {
               results.addHeader(s, "Client Type");
               sql.append(",clienttype as ").append(s);
               col++;
               if (sortfield.equals(s)) orderby.addElement(""+col);
               if (!recordlev) {
                  groupby.add("clienttype");
               }
            }
            
           // Apply filter
            if (model.getClientTypeFilter()) {
               where.append(generateFilter(model.getClientTypeFilterValue(),
                                           "clienttype"));
            }
            
            s = model.OS;
            if (model.getOs()) {
               results.addHeader(s, "Op Sys");
               sql.append(",os as ").append(s);
               col++;
               if (sortfield.equals(s)) orderby.addElement(""+col);
               if (!recordlev) {
                  groupby.add("os");
               }
            }
            
           // Apply filter
            if (model.getOsFilter()) {
               where.append(generateFilter(model.getOsFilterValue(),
                                           "os"));
            }
            
            s = model.SessionState;
            if (model.getSessionState()) {
               results.addHeader(s, "Session State");
               sql.append(",sestype as ").append(s);
               col++;
               if (sortfield.equals(s)) orderby.addElement(""+col);
               if (!recordlev) {
                  groupby.add("sestype");
               }
            }
            
           // Apply filter
            if (model.getSessionStateFilter()) {
               where.append(generateFilter(model.getSessionStateFilterValue(),
                                           "SESTYPE"));
            }
            
            s = model.Duration;
            if (model.getDuration()) {
               col++;
               if (sortfield.equals(s)) orderby.addElement(""+col);
               if (!recordlev) {
                  results.addHeader(s, "Total Duration");
                  sql.append(",SUM(BIGINT(endi-starti)) as ").append(s);
               } else {
                  results.addHeader(s, "Duration");
                  sql.append(",(endi-starti) as ").append(s);
                  groupby.add(s);
               }
            }
            
           // Apply filter
            if (model.getDurationFilter()) {
               where.append(" AND (endi-starti) ");
               where.append(getSQLModifier(model.getDurationFilterModifier()));
               where.append(" (");
               where.append(SessionForm.parseDuration(model.getDurationFilterValue()));
               where.append(") ");
            }
            
            s = model.BytesUp;
            if (model.getBytesUp()) {
               col++;
               if (sortfield.equals(s)) orderby.addElement(""+col);
               if (!recordlev) {
                  results.addHeader(s, "Tot Bytes Up");
                  sql.append(",SUM(BYTESREAD) as ").append(s);
               } else {
                  results.addHeader(model.BytesUp, "Bytes Up");
                  sql.append(",BYTESREAD as ").append(model.BytesUp);
               }
            }
            
           // Apply filter
            if (model.getBytesUpFilter()) {
               where.append(" AND BYTESREAD ");
               where.append(getSQLModifier(model.getBytesUpFilterModifier()));
               where.append(" (");
               where.append(model.getBytesUpFilterValue()).append("*1024) ");
            }
                        
            s = model.BytesDown;
            if (model.getBytesDown()) {
               col++;
               if (sortfield.equals(s)) orderby.addElement(""+col);
               if (!recordlev) {
                  results.addHeader(s, "Tot Bytes Down");
                  sql.append(",SUM(BYTESWRITTEN) as ").append(s);
               } else {
                  results.addHeader(s, "Bytes Down");
                  sql.append(",BYTESWRITTEN as ").append(s);
               }
            }
            
           // Apply filter
            if (model.getBytesDownFilter()) {
               where.append(" AND BYTESWRITTEN ");
               where.append(getSQLModifier(model.getBytesDownFilterModifier()));
               where.append(" (");
               where.append(model.getBytesDownFilterValue()).append("*1024) ");
            }
            
            s = model.Bytes;
            if (model.getBytes()) {
               col++;
               if (sortfield.equals(s)) orderby.addElement(""+col);
               if (!recordlev) {
                  results.addHeader(s, "Tot Bytes");
                  sql.append(",SUM(BYTESREAD+BYTESWRITTEN) as ").append(s);
               } else {
                  results.addHeader(s, "Bytes");
                  sql.append(",BYTESREAD+BYTESWRITTEN as ").append(s);
               }
            }
            
           // Apply filter
            if (model.getBytesFilter()) {
               where.append(" AND (BYTESREAD+BYTESWRITTEN) ");
               where.append(getSQLModifier(model.getBytesFilterModifier()));
               where.append(" (");
               where.append(model.getBytesFilterValue()).append("*1024) ");
            }
            
            if (!recordlev) {
               if (model.getAvgBytesUp() || model.getAvgBytesUpFilter()) {
                  s = model.AvgBytesUp;
                  col++;
                  if (sortfield.equals(s)) orderby.addElement(""+col);
                  if (model.getAvgBytesUp()) {
                     results.addHeader(model.AvgBytesUp, "Avg Bytes Up");
                  }
                  sql.append(",AVG(BYTESREAD) as ").append(model.AvgBytesUp);
               }
                     
               if (model.getAvgBytesDown() || model.getAvgBytesDownFilter()) {
                  s = model.AvgBytesDown;
                  col++;
                  if (sortfield.equals(s)) orderby.addElement(""+col);
                  if (model.getAvgBytesDown()) {
                     results.addHeader(model.AvgBytesDown, "Avg Bytes Down");
                  }
                  sql.append(",AVG(BYTESWRITTEN) as ").append(model.AvgBytesDown);
               }
                     
               if (model.getAvgBytes() || model.getAvgBytesFilter()) {
                  s = model.AvgBytes;
                  col++;
                  if (sortfield.equals(s)) orderby.addElement(""+col);
                  if (model.getAvgBytes()) {
                     results.addHeader(model.AvgBytes, "Avg Bytes");
                  }
                  sql.append(",AVG(BYTESREAD+BYTESWRITTEN) as ").append(model.AvgBytes);
               }
                     
               if (model.getAvgDuration() || model.getAvgDurationFilter()) {
                  s = model.AvgDuration;
                  col++;
                  if (sortfield.equals(s)) orderby.addElement(""+col);
                  if (model.getAvgDuration()) {
                     results.addHeader(model.AvgDuration, "Avg Duration");
                  }
                  sql.append(",AVG(BIGINT(endi-starti)) as ").append(model.AvgDuration);
               }
                     
               if (model.getNumSessions() || model.getNumSessionsFilter()) {
                  s = model.NumSessions;
                  col++;
                  if (sortfield.equals(s)) orderby.addElement(""+col);
                  if (model.getNumSessions()) {
                     results.addHeader(model.NumSessions, "Num Sessions");
                  }
                  sql.append(",count(distinct sessid) as ").append(model.NumSessions);
               }
                     
            } else {
               if (model.getStartTime()) {
                  s = model.StartTime;
                  col++;
                  if (sortfield.equals(s)) orderby.addElement(""+col);
                  results.addHeader(model.StartTime, "Start Time");
                  sql.append(",start as ").append(model.StartTime);
               }
               
               if (model.getEndTime()) {
                  s = model.EndTime;
                  col++;
                  if (sortfield.equals(s)) orderby.addElement(""+col);
                  results.addHeader(model.EndTime, "End Time");
                  sql.append(",end as ").append(model.EndTime);
               }
            }
            
           // Breaking away from table here would be a bear. Stick with it for now
            sql.append(" from TABLE(SELECT sessid,owner,company,byteswritten,bytesread,start,end,(((days(start)-725000)*24*3600)+(hour(start)*3600)+(minute(start)*60)+(second(start))) as starti, case when END is NULL AND REPORT_END > CURRENT TIMESTAMP then (((days(CURRENT TIMESTAMP)-725000)*24*3600)+(hour(CURRENT TIMESTAMP)*3600)+(minute(CURRENT TIMESTAMP)*60)+(second(CURRENT TIMESTAMP))) when END is NULL then (((days(REPORT_END)-725000)*24*3600)+(hour(REPORT_END)*3600)+(minute(REPORT_END)*60)+(second(REPORT_END))) else (((days(END)-725000)*24*3600)+(hour(END)*3600)+(minute(END)*60)+(second(END))) end as endi, case when END is NULL AND days(current timestamp)-days(start) < 3 then 'Running' when END is NULL then 'Zombie' else 'Completed' end as sestype,os,clienttype from EDESIGN.DBOXSESS) db   where start between REPORT_START and REPORT_END ");
                        
            sql.append(where.toString());
            
            if (!recordlev) {
               if (groupby.size() > 0) {
                  sql.append(" group by ");
                  Enumeration enum = groupby.elements();
                  boolean first = true;
                  while(enum.hasMoreElements()) {
                     if (!first) sql.append(",");
                     first = false;
                     sql.append((String)enum.nextElement());
                  }
               }
            }
            
            if (orderby.size() > 0) {
               sql.append(" order by ");
               Enumeration enum = orderby.elements();
               boolean first = true;
               while(enum.hasMoreElements()) {
                  if (!first) sql.append(",");
                  first = false;
                  sql.append((String)enum.nextElement());
               }
               
               sql.append(" ").append(sortdir).append(" ");
            }
            
            sql.append(" for read only with UR");
         
            int idx;
            String sqlS = sql.toString();
            String scS = dateAsSqlDateString(model.getStartDate());
            while ((idx=sqlS.indexOf("REPORT_START")) >= 0) {
               sqlS = sqlS.substring(0,idx) + scS + sqlS.substring(idx+12);
            }
               
            String ecS = dateAsSqlDateString(model.getEndDate());
            while ((idx=sqlS.indexOf("REPORT_END")) >= 0) {
               sqlS = sqlS.substring(0,idx) + ecS + sqlS.substring(idx+10);
            }

            DebugPrint.println(DebugPrint.INFO5, "Dropbox ReportQ: " + 
                               sqlS);
            pstmt=connection.prepareStatement(sqlS);
         
            rs=conn.executeQuery(pstmt);
         
           // Get meta info 
            ResultSetMetaData rsm = rs.getMetaData();
            int numcols = rsm.getColumnCount();
            
           // get local vars for this
            boolean avgbytesupfilter   = model.getAvgBytesUpFilter();
            boolean avgbytesdownfilter = model.getAvgBytesDownFilter();
            boolean avgbytesfilter     = model.getAvgBytesFilter();
            boolean avgdurationfilter  = model.getAvgDurationFilter();
            boolean numsessionsfilter  = model.getNumSessionsFilter();
            boolean dofilt = (!recordlev          && 
                              (avgbytesupfilter   ||
                               avgbytesdownfilter ||
                               avgbytesfilter     ||
                               avgdurationfilter  ||
                               numsessionsfilter));
                               
            
            int avgbytesupfiltop = 
               getSQLModifierAsNumber(model.getAvgBytesUpFilterModifier());
            int avgbytesdownfiltop = 
               getSQLModifierAsNumber(model.getAvgBytesDownFilterModifier());
            int avgbytesfiltop = 
               getSQLModifierAsNumber(model.getAvgBytesFilterModifier());
            int avgdurationfiltop = 
               getSQLModifierAsNumber(model.getAvgDurationFilterModifier());
            int numsessionsfiltop = 
               getSQLModifierAsNumber(model.getNumSessionsFilterModifier());
            
            
           // Set the suggested alignment for each column
            for(int j=1; j <= numcols; j++) {
               String key = rsm.getColumnLabel(j) ;
               results.setHeaderAlignment(key, getAlignment(key));
            }
            
            while(rs.next()) {
            
              // Apply Summary filters
               if (dofilt) {
                  if (avgbytesupfilter) {
                     long v  = rs.getLong(model.AvgBytesUp);
                     long vv = model.getAvgBytesUpAsLong();
                     vv *= 1024;
                     if (!applyFilter(avgbytesupfiltop, v, vv)) continue; 
                  }
                  if (avgbytesdownfilter) {
                     long v  = rs.getLong(model.AvgBytesDown);
                     long vv = model.getAvgBytesDownAsLong();
                     vv *= 1024;
                     if (!applyFilter(avgbytesdownfiltop, v, vv)) continue; 
                  }
                  if (avgbytesfilter) {
                     long v  = rs.getLong(model.AvgBytes);
                     long vv = model.getAvgBytesAsLong();
                     vv *= 1024;
                     if (!applyFilter(avgbytesfiltop, v, vv)) continue; 
                  }
                  if (avgdurationfilter) {
                     long v  = rs.getLong(model.AvgDuration);
                     long vv = SessionForm.parseDuration(model.getAvgDurationFilterValue());
                     if (!applyFilter(avgdurationfiltop, v, vv)) continue; 
                  }
                  if (numsessionsfilter) {
                     long v  = rs.getLong(model.NumSessions);
                     long vv = model.getNumSessionsAsLong();
                     if (!applyFilter(numsessionsfiltop, v, vv)) continue; 
                  }
               }
            
            
               Hashtable row = new Hashtable();
               for(int j=1; j <= numcols; j++) {
                  String key = rsm.getColumnLabel(j) ;
                  Object obj = rs.getObject(j);
                  
                  if (obj == null || rs.wasNull()) {
                     row.put(key.toUpperCase(), "-");
                  } else {
                     row.put(key.toUpperCase(), formatAsString(key, obj));
                  }
               }
               results.addRow(row);
            } 
         
         } catch (SQLException e){
            conn.destroyConnection(connection);
            connection=null;
            e.printStackTrace(System.out);
            throw e;
         } finally {
            
            if (pstmt!=null){
               try {
                  pstmt.close();
               } catch(SQLException e) {}
            }
            if (conn != null) conn.returnConnection(connection);
         }               
         
        // Tell JSP our notion of a good order
         results.addOrderedHeader(SessionForm.User);
         results.addOrderedHeader(SessionForm.Company);
         results.addOrderedHeader(SessionForm.NumSessions);
         results.addOrderedHeader(SessionForm.ClientType);
         results.addOrderedHeader(SessionForm.OS);
         results.addOrderedHeader(SessionForm.SessionState);
         results.addOrderedHeader(SessionForm.StartTime);
         results.addOrderedHeader(SessionForm.EndTime);
         results.addOrderedHeader(SessionForm.Duration);
         results.addOrderedHeader(SessionForm.AvgDuration);
         results.addOrderedHeader(SessionForm.Bytes);
         results.addOrderedHeader(SessionForm.AvgBytes);
         results.addOrderedHeader(SessionForm.BytesUp);
         results.addOrderedHeader(SessionForm.AvgBytesUp);
         results.addOrderedHeader(SessionForm.BytesDown);
         results.addOrderedHeader(SessionForm.AvgBytesDown);
         
         request.setAttribute("results", results);
         addSavedReports(request, credentials);      
         
      } catch(Exception ee) {
         System.out.println("Exception processing Session Dropbox report request:"
                            + ee);
         throw ee;
      }
      
      return mapping.findForward("success");
   }
   
  // --------------------------------------------
  // ----- Package Report Impl
  // --------------------------------------------
      
  //
  // All package queries come thru here
  //
   public ActionForward packageQuery(ActionMapping       mapping,
                                     ActionForm          form,
                                     HttpServletRequest  request,
                                     HttpServletResponse response)
      throws Exception {
      
      Results results = new Results();
      
      try {
         
         HttpSession session = request.getSession(false);
         
         Credentials credentials = getDropboxCredentials(session);
         if (credentials == null) {
            return mapping.findForward("sessionExpired");
         }
         
         PackageForm model = (PackageForm)form;
         
         DebugPrint.printlnd(DebugPrint.INFO5, "In packageQuery");
         
         DBConnection conn = getDropboxConnection();
         
         Connection connection=null;
         PreparedStatement pstmt=null;
         ResultSet rs=null;	
         int col = 1;
         
         boolean recordlev = model.getRecordLevel().equalsIgnoreCase("true");
         
        // If its record level, put in a header to let JSP know
         if (recordlev) {
            results.addHeader(model.PKGID, "Package Id");
         }
            
            
        // Get distinct p.pkgid in there regardless, as we want to make sure
        //  that we don't have multiple matches
         StringBuffer sql = recordlev 
            ? new StringBuffer("select distinct p.pkgid as ").append(model.PKGID).append(" ")
            : new StringBuffer("select count(distinct p.pkgid) as countpkgid ");
         
         try {	
            
           // HACK ... fix this
            String sortfield = model.getSortField();
            String sortdir   = model.getSortDirection();
            
            DebugPrint.printlnd(DebugPrint.INFO5, "Sortfield=" + sortfield);
            DebugPrint.printlnd(DebugPrint.INFO5, "Sortdir=" + sortdir);
            
            if (sortdir == null ||
                (!sortdir.equalsIgnoreCase("ASC") && 
                 !sortdir.equalsIgnoreCase("DESC"))) {
               sortdir = "ASC";
            }
            
            if (sortfield == null) sortfield = "";
            
            connection=conn.getConnection();
            
            Vector groupby = new Vector();
            Vector orderby = new Vector();
            
            StringBuffer where = new StringBuffer();
            
           // If this guy is NOT a Super User, then scope what he can see to himself
           // This means things he owns, and things that have been sent to him
            where.append(getVisiblePackageList(credentials));
            
            String s = model.User;
            if (model.getUser()) {
               results.addHeader(s, "Owner");
               sql.append(",p.ownerid as ").append(s);
               col++;
               if (sortfield.equals(s)) orderby.addElement(""+col);
               if (!recordlev) {
                  groupby.add("p.ownerid");
               }
            }
            
           // Apply filter
            if (model.getUserFilter()) {
               where.append(generateFilter(model.getUserFilterValue(),
                                           "p.OWNERID"));
            }
            
            s = model.Company;
            if (model.getCompany()) {
               results.addHeader(s, "Company");
               sql.append(",p.company as ").append(s);
               col++;
               if (sortfield.equals(s)) orderby.addElement(""+col);
               if (!recordlev) {
                  groupby.add("p.company");
               }
            }
            
           // Apply filter
            if (model.getCompanyFilter()) {
               where.append(generateFilter(model.getCompanyFilterValue(),
                                           "p.COMPANY"));
            }
            
           // Have to fix this TODO
            s = model.PackageState;
            if (model.getPackageState()) {
               results.addHeader(s, "Package State");
               sql.append(",").append(p_pkgstate).append(" as ").append(s);
               col++;
               if (sortfield.equals(s)) orderby.addElement(""+col);
               if (!recordlev) {
                  groupby.add(p_pkgstate);
               }
            }
                        
           // Apply filter
            if (model.getPackageStateFilter()) {
               where.append(generateFilter(model.getPackageStateFilterModifier(),
                                           p_pkgstate));
            }
                        
            s = model.PackageSize;
            if (model.getPackageSize()) {
               col++;
               if (sortfield.equals(s)) orderby.addElement(""+col);
               if (!recordlev) {
                  results.addHeader(s, "Total Size");
                  sql.append(",SUM(p.pkgsize) as ").append(s);
               } else {
                  results.addHeader(s, "Package Size");
                  sql.append(",p.pkgsize as ").append(s);
                  groupby.add(s);
               }
            }
            
           // Apply filter
            if (model.getPackageSizeFilter()) {
               where.append(" AND p.PKGSIZE ");
               where.append(getSQLModifier(model.getPackageSizeFilterModifier()));
               where.append(" (");
               where.append(model.getPackageSizeFilterValue()).append("*1024) ");
            }
            
            s = model.NumFiles;
            if (model.getNumFiles()) {
               col++;
               if (sortfield.equals(s)) orderby.addElement(""+col);
               if (!recordlev) {
                  results.addHeader(s, "Total Files");
                  sql.append(",SUM(BIGINT(p.fileentry)) as ").append(s);
               } else {
                  results.addHeader(s, "Number of Files");
                  sql.append(",p.fileentry as ").append(s);
               }
            }
            
           // Apply filter
            if (model.getNumFilesFilter()) {
               where.append(" AND p.fileentry ");
               where.append(getSQLModifier(model.getNumFilesFilterModifier()));
               where.append(" ").append(model.getNumFilesFilterValue()).append(" ");
            }
            
            
            s = model.ItarStatus;
            if (model.getPackageItarStatus()) {
               results.addHeader(s, "Itar Status");
               sql.append(",").append(p_itarstatus).append(" as ").append(s);
               col++;
               if (sortfield.equals(s)) orderby.addElement(""+col);
               if (!recordlev) {
                  groupby.add(p_itarstatus);
               }
            }
            
            if (!recordlev) {
            
              // For Summary attributes, we have to query it if the user wants it 
              //  OR if the user wishes to filter by it
               if (model.getAvgPackageSize() || model.getAvgPackageSizeFilter()) {
                  s = model.AvgPackageSize;
                  col++;
                  if (sortfield.equals(s)) orderby.addElement(""+col);
                  if (model.getAvgPackageSize()) {
                     results.addHeader(s, "Avg Package Size");
                  }
                  sql.append(",AVG(p.pkgsize) as ").append(s);
               }
               
               if (model.getNumPackages() || model.getNumPackagesFilter()) {
                  s = model.NumPackages;
                  col++;
                  if (sortfield.equals(s)) orderby.addElement(""+col);
                  if (model.getNumPackages()) {
                     results.addHeader(s, "Num Packages");
                  }
                  sql.append(",COUNT(distinct p.pkgid) as ").append(s);
               }
               
               if (model.getAvgNumFiles() || model.getAvgNumFilesFilter()) {
                  s = model.AvgNumFiles;
                  col++;
                  if (sortfield.equals(s)) orderby.addElement(""+col);
                  if (model.getAvgNumFiles()) {
                     results.addHeader(s, "Avg Number Files");
                  }
                  sql.append(",AVG(BIGINT(p.fileentry)) as ").append(s);
               }
                     
            } else {
            
               s = model.PackageName;
               if (model.getPackageName()) {
                  results.addHeader(s, "Package Name");
                  sql.append(",p.pkgname as ").append(s);
                  col++;
                  if (sortfield.equals(s)) orderby.addElement(""+col);
                  if (!recordlev) {
                     groupby.add("p.pkgname");
                  }
               }
                           
               if (model.getCreateTime()) {
                  s = model.CreateTime;
                  col++;
                  if (sortfield.equals(s)) orderby.addElement(""+col);
                  results.addHeader(s, "Create Time");
                  sql.append(",p.created as ").append(s);
               }
               
                              
               if (model.getDeleteTime()) {
                  s = model.DeleteTime;
                  col++;
                  if (sortfield.equals(s)) orderby.addElement(""+col);
                  results.addHeader(s, "Delete Time");
                  sql.append(",p.deleted as ").append(s);
               }
               
               if (model.getCommitTime()) {
                  s = model.CommitTime;
                  col++;
                  if (sortfield.equals(s)) orderby.addElement(""+col);
                  results.addHeader(s, "Commit Time");
                  sql.append(",p.committed as ").append(s);
               }
               
            }
            
           // Apply filter
            if (model.getPackageNameFilter()) {
               where.append(generateFilter(model.getPackageNameFilterValue(),
                                           "p.pkgname"));
            }
            
           // Apply filter
            if (model.getCreateTimeFilter()) {
               String sqlop = getSQLModifier(model.getCreateTimeFilterModifier());
               where.append(" AND p.created ").append(sqlop).append(" ");
               where.append(dateAsSqlDateString(model.getCreateTimeStartDate()));
               where.append(" ");
               if (sqlop.equalsIgnoreCase("between")) {
                  where.append(" AND ");
                  where.append(dateAsSqlDateString(model.getCreateTimeEndDate()));
                  where.append(" ");
               }
            }
           // Apply filter
            if (model.getDeleteTimeFilter()) {
               String sqlop = getSQLModifier(model.getDeleteTimeFilterModifier());
               where.append(" AND p.deleted ").append(sqlop).append(" ");
               where.append(dateAsSqlDateString(model.getDeleteTimeStartDate()));
               where.append(" ");
               if (sqlop.equalsIgnoreCase("between")) {
                  where.append(" AND ");
                  where.append(dateAsSqlDateString(model.getDeleteTimeEndDate()));
                  where.append(" ");
               }
            }
               
           // Apply filter
            if (model.getCommitTimeFilter()) {
               String sqlop = getSQLModifier(model.getCommitTimeFilterModifier());
               where.append(" AND p.committed ").append(sqlop).append(" ");
               where.append(dateAsSqlDateString(model.getCommitTimeStartDate()));
               where.append(" ");
               if (sqlop.equalsIgnoreCase("between")) {
                  where.append(" AND ");
                  where.append(dateAsSqlDateString(model.getCommitTimeEndDate()));
                  where.append(" ");
               }
            }
            
            sql.append(" from edesign.package p WHERE p.created <= REPORT_END AND (p.deleted is null OR p.deleted >= REPORT_START) ");
                         
            if (where.length() > 0) {
               sql.append(where.toString());
            }
            
            if (!recordlev) {
               if (groupby.size() > 0) {
                  sql.append(" group by ");
                  Enumeration enum = groupby.elements();
                  boolean first = true;
                  while(enum.hasMoreElements()) {
                     if (!first) sql.append(",");
                     first = false;
                     sql.append((String)enum.nextElement());
                  }
               }
            }
            
            if (orderby.size() > 0) {
               sql.append(" order by ");
               Enumeration enum = orderby.elements();
               boolean first = true;
               while(enum.hasMoreElements()) {
                  if (!first) sql.append(",");
                  first = false;
                  sql.append((String)enum.nextElement());
               }
               
               sql.append(" ").append(sortdir).append(" ");
            }
            
            sql.append(" for read only with UR");
         
            int idx;
            String sqlS = sql.toString();
            String scS = dateAsSqlDateString(model.getStartDate());
            while ((idx=sqlS.indexOf("REPORT_START")) >= 0) {
               sqlS = sqlS.substring(0,idx) + scS + sqlS.substring(idx+12);
            }
               
            String ecS = dateAsSqlDateString(model.getEndDate());
            while ((idx=sqlS.indexOf("REPORT_END")) >= 0) {
               sqlS = sqlS.substring(0,idx) + ecS + sqlS.substring(idx+10);
            }

            DebugPrint.println(DebugPrint.INFO5, "Dropbox ReportQ: " + 
                               sqlS);
            pstmt=connection.prepareStatement(sqlS);
         
            rs=conn.executeQuery(pstmt);
         
           // Get meta info 
            ResultSetMetaData rsm = rs.getMetaData();
            int numcols = rsm.getColumnCount();
            
           // get local vars for this
            boolean avgpacksizefilter = model.getAvgPackageSizeFilter();
            boolean avgnumfilesfilter = model.getAvgNumFilesFilter();
            boolean numpacksfilter    = model.getNumPackagesFilter();
            boolean dofilt = (!recordlev && 
                              (avgpacksizefilter || avgnumfilesfilter || 
                               numpacksfilter));
            
            int avgpacksizefiltop = 
               getSQLModifierAsNumber(model.getAvgPackageSizeFilterModifier());
            int avgnumfilesfiltop = 
               getSQLModifierAsNumber(model.getAvgNumFilesFilterModifier());
            int numpacksfiltop = 
               getSQLModifierAsNumber(model.getNumPackagesFilterModifier());
            
           // Set the suggested alignment for each column
            for(int j=1; j <= numcols; j++) {
               String key = rsm.getColumnLabel(j) ;
               results.setHeaderAlignment(key, getAlignment(key));
            }
            
           while(rs.next()) {
           
             // Apply Summary filters
               if (dofilt) {
                  if (avgpacksizefilter) {
                     long v  = rs.getLong(model.AvgPackageSize);
                     long vv = model.getAvgPackageSizeAsLong();
                     vv *= 1024;
                     if (!applyFilter(avgpacksizefiltop, v, vv)) continue; 
                  }
                  if (avgnumfilesfilter) {
                     long v  = rs.getLong(model.AvgNumFiles);
                     long vv = model.getAvgNumFilesAsLong();
                     if (!applyFilter(avgnumfilesfiltop, v, vv)) continue; 
                  }
                  if (numpacksfilter) {
                     long v  = rs.getLong(model.NumPackages);
                     long vv = model.getNumPackagesAsLong();
                     if (!applyFilter(numpacksfiltop, v, vv)) continue; 
                  }
               }
               
               Hashtable row = new Hashtable();
               for(int j=1; j <= numcols; j++) {
               
                  String key = rsm.getColumnLabel(j) ;
                  Object obj = rs.getObject(j);
                  
                  if (obj == null || rs.wasNull()) {
                     row.put(key.toUpperCase(), "-");
                  } else {
                     row.put(key.toUpperCase(), formatAsString(key, obj));
                  }
               }
               results.addRow(row);
            } 
         
         } catch (SQLException e){
            conn.destroyConnection(connection);
            connection=null;
            e.printStackTrace(System.out);
            throw e;
         } finally {
            
            if (pstmt!=null){
               try {
                  pstmt.close();
               } catch(SQLException e) {}
            }
            if (conn != null) conn.returnConnection(connection);
         }               
         
        // Give the JSP our notion of good order of cols
         results.addOrderedHeader(PackageForm.PKGID);
         results.addOrderedHeader(PackageForm.User);
         results.addOrderedHeader(PackageForm.Company);
         results.addOrderedHeader(PackageForm.PackageName);
         results.addOrderedHeader(PackageForm.NumPackages);
         results.addOrderedHeader(PackageForm.PackageState);
         results.addOrderedHeader(PackageForm.PackageSize);
         results.addOrderedHeader(PackageForm.ItarStatus);
         results.addOrderedHeader(PackageForm.NumFiles);
         results.addOrderedHeader(PackageForm.AvgPackageSize);
         results.addOrderedHeader(PackageForm.AvgNumFiles);
         results.addOrderedHeader(PackageForm.CreateTime);
         results.addOrderedHeader(PackageForm.CommitTime);
         results.addOrderedHeader(PackageForm.DeleteTime);
         
         request.setAttribute("results", results);
         addSavedReports(request, credentials);               
         
      } catch(Exception ee) {
         System.out.println("Exception processing Package Dropbox report request:"
                            + ee);
         throw ee;
      }
      
      return mapping.findForward("success");
   } 
   
   public ActionForward packageInfo(ActionMapping       mapping,
                                    ActionForm          form,
                                    HttpServletRequest  request,
                                    HttpServletResponse response)
      throws Exception {
      
      Results results = new Results();
      
      try {
         
         HttpSession session = request.getSession(false);
         
         Credentials credentials = getDropboxCredentials(session);
         if (credentials == null) {
            return mapping.findForward("sessionExpired");
         }
         
         String pkgid = request.getParameter(PackageForm.PKGID);
         
         DboxReportPackageInfo pinfo = new DboxReportPackageInfo();
         
         DebugPrint.printlnd(DebugPrint.INFO5, "In packageInfo");
         
         DBConnection conn = getDropboxConnection();
         
         Connection connection=null;
         PreparedStatement pstmt=null;
         ResultSet rs=null;	
         int col = 1;
         
        // Get distinct p.pkgid in there regardless, as we want to make sure
        //  that we don't have multiple matches
         StringBuffer sql = new StringBuffer("select distinct p.pkgid,p.ownerid");
         sql.append(",p.company,");
         sql.append(p_pkgstate).append(" as pkgstate,").append(p_itarstatus);
         sql.append(" as itarstatus, p.desc");
         sql.append(",p.pkgsize,p.fileentry, p.pkgname");
         sql.append(",p.created as pcreated,p.committed as pcommitted,p.deleted as pdeleted" );
         sql.append(" from edesign.package p ");
         
         try {	
            
            connection=conn.getConnection();
            
           // TODO Check pkgid is a number
            StringBuffer where = new StringBuffer(" p.pkgid = ").append(pkgid);
            
            String common;
            
           // If this guy is NOT a Super User, then scope what he can see to himself
           // This means things he owns, and things that have been sent to him
            where.append(getVisiblePackageList(credentials));
            
            sql.append(" where ").append(where.toString());
            
            sql.append(" for read only with UR");
         
            int idx;
            String sqlS = sql.toString();
            
            DebugPrint.println(DebugPrint.INFO5, "Dropbox ReportQ: " + 
                               sqlS);
            pstmt=connection.prepareStatement(sqlS);
         
            rs=conn.executeQuery(pstmt);
         
            
            if (!rs.next()) {
               return mapping.findForward("nopkg");
            }
           
            Object obj;
            
            pinfo.setPackageId(pkgid);
            
            obj = rs.getObject("pkgname");  if (obj == null) obj = "";
            pinfo.setPackageName(obj.toString());
            
            obj = rs.getObject("ownerid");  if (obj == null) obj = "";
            pinfo.setPackageOwner(obj.toString());
            
            obj = rs.getObject("company");  if (obj == null) obj = "";
            pinfo.setPackageCompany(obj.toString());
            
            obj = rs.getObject("pkgstate");  if (obj == null) obj = "";
            pinfo.setPackageState(obj.toString());
            
            obj = rs.getObject("itarstatus");  if (obj == null) obj = "";
            pinfo.setPackageItarStatus(obj.toString());
            
            obj = rs.getObject("desc");  if (obj == null) obj = "";
            pinfo.setPackageDescription(obj.toString());
            
            obj = rs.getObject("pkgsize");  if (obj == null) obj = "";
            pinfo.setPackageSize(obj.toString());
            
            obj = rs.getObject("fileentry");  if (obj == null) obj = "";
            pinfo.setPackageNumFiles(obj.toString());
            
            obj = rs.getObject("pcreated");  if (obj == null) obj = "";
            pinfo.setPackageCreated(formatAsString("pcreated", obj));
            
            obj = rs.getObject("pcommitted");  if (obj == null) obj = "";
            pinfo.setPackageCommitted(formatAsString("pcommitted", obj));
            
            obj = rs.getObject("pdeleted");  if (obj == null) obj = "";
            pinfo.setPackageDeleted(formatAsString("pdeleted", obj));
            
            
            boolean isowner = pinfo.getPackageOwner().equals(credentials.getName());
            
           // JMC 5/9/05 - isfse will be TRUE iif the reportuser is an FSE for a
           //              project which the package owner is in AND the reportuser 
           //              has the DSGN_VIEW_PACKAGE entitlement
            boolean isfse = credentials.isFSESuper() ;
              /* && credentials.isFSEFor(pinfo.getPackageOwner()); */
            
            pinfo.setLimited(!(isfse || isowner || credentials.isSuper()));
            
           //
           // Get Files in package
           //
            sql = new StringBuffer("SELECT f.fileid,f.filename,");
            sql.append(f_state); 
            sql.append("as state,f.md5,f.created,f.deleted,f.filesize,f.componententry,f.xferspeed from edesign.filetopkg fp, edesign.file f where fp.fileid = f.fileid AND fp.pkgid = PKGID_REPLACE ");
            
            pstmt.close();
            
            sql.append(" for read only with UR");
         
            sqlS = sql.toString();
            while ((idx=sqlS.indexOf("PKGID_REPLACE")) >= 0) {
               sqlS = sqlS.substring(0,idx) + pkgid + sqlS.substring(idx+13);
            }
            
            DebugPrint.println(DebugPrint.INFO5, "Dropbox ReportQ: " + 
                               sqlS);
            pstmt=connection.prepareStatement(sqlS);
         
            rs=conn.executeQuery(pstmt);
         
            long pkgtotxferbytes = 0;
            long pkgtotxferms    = 0;
            while(rs.next()) {
               DboxReportFileInfo finfo = new DboxReportFileInfo();
               
               obj = rs.getObject("filename");  if (obj == null) obj = "";
               finfo.setFileName(obj.toString());
               
               obj = rs.getObject("fileid");  if (obj == null) obj = "";
               finfo.setFileId(obj.toString());
               
               obj = rs.getObject("filesize");  if (obj == null) obj = "";
               finfo.setFileSize(obj.toString());
               
               obj = rs.getObject("state");  if (obj == null) obj = "";
               finfo.setFileState(obj.toString());
               
               obj = rs.getObject("created");  if (obj == null) obj = "";
               finfo.setFileCreated(formatAsString("created", obj));
               
               obj = rs.getObject("deleted");  if (obj == null) obj = "";
               finfo.setFileDeleted(formatAsString("deleted", obj));
               
               obj = rs.getObject("md5");  if (obj == null) obj = "";
               finfo.setFileMD5(obj.toString());
               
               obj = rs.getObject("componententry");  if (obj == null) obj = "";
               finfo.setFileNumComponents(obj.toString());
               
               obj = rs.getObject("xferspeed");  
               
               long filespeed        = 0;
               long filetotxferbytes = 0;
               long filetotxferms    = 0;
               if (obj == null) {
                  finfo.setFileTransferRate("");
               } else {
                 // JMC 8/7/06 - Changed from xferspeed on fc to user supplied
                 //              xferspeed set on File
                  filespeed        = Long.parseLong(obj.toString());
                  if (filespeed > 0) {
                     filetotxferbytes = Long.parseLong(finfo.getFileSize());
                     filetotxferms    = (filetotxferbytes*1000)/filespeed;
                  }
                  
                  if (filetotxferbytes > 0 && filetotxferms > 0) {
                     pkgtotxferbytes  += filetotxferbytes;
                     pkgtotxferms     += filetotxferms;
                     
                     finfo.setFileTransferRate(""+filespeed);
                  }
               }
               
               pinfo.addFile(finfo);
            }
            
            if (pkgtotxferbytes > 0) {
               long pkgxferrate = (pkgtotxferbytes*1000) / pkgtotxferms;
               pinfo.setPackageTransferRate(""+pkgxferrate);
            }
            
           // If the user is NOT allowed to see ACLS, then don't show him
            if (!(credentials.isSuper() || isowner || isfse)) {
               DboxReportAclInfo ainfo = new DboxReportAclInfo();
               ainfo.setAclName("-");
               ainfo.setAclType("-");
               ainfo.setAclCreated("-");
               pinfo.addAclInfo(ainfo);
            } else {
              //
              // Get package access list (actual acls)
              //
               sql = new StringBuffer("SELECT userid, case when usertype = ");
               sql.append(DropboxGenerator.STATUS_NONE);
               sql.append(" then 'User' when usertype = ");
               sql.append(DropboxGenerator.STATUS_GROUP);
               sql.append(" then 'Group' else 'Project' end as type,created from ");
               
               
               sql.append("edesign.pkgacl where pkgid = ").append(pkgid);
               sql.append(" order by 2,1 ");
               
               pstmt.close();
               
               sql.append(" for read only with UR");
               
               sqlS = sql.toString();
               
               DebugPrint.println(DebugPrint.INFO5, "Dropbox ReportQ: " + 
                                  sqlS);
               pstmt=connection.prepareStatement(sqlS);
               
               rs=conn.executeQuery(pstmt);
               
               while(rs.next()) {
                  DboxReportAclInfo ainfo = new DboxReportAclInfo();
                  
                  obj = rs.getObject("userid");  if (obj == null) obj = "";
                  ainfo.setAclName(obj.toString());
                  
                  obj = rs.getObject("type");  if (obj == null) obj = "";
                  ainfo.setAclType(obj.toString());
                  
                  obj = rs.getObject("created");  if (obj == null) obj = "";
                  ainfo.setAclCreated(formatAsString("created", obj));
                  
                  pinfo.addAclInfo(ainfo);
               }
            }
            
           // Limit access to himself if required
            String limitit = "";
            if (!credentials.isSuper() && !isowner && !isfse) {
               limitit = " AND userid = '" + credentials.getName() + "' ";
            }
            
           //
           // Get package Accessed by
           //
            sql = new StringBuffer("SELECT userid, sum(BIGINT(case when dloadstat = ");
            sql.append(DropboxGenerator.STATUS_COMPLETE);
            sql.append(" then 1 else 0 end)) as complete, ");
            sql.append("sum(BIGINT(case when dloadstat != ");
            sql.append(DropboxGenerator.STATUS_COMPLETE);
            sql.append(" then 1 else 0 end)) as failed,");
            sql.append("avg(BIGINT(xferspeed)) as avgxferrate,");
            sql.append("max(created) as acreated");
            sql.append(" from ");
            
            sql.append("edesign.fileacl where pkgid = ").append(pkgid);
            sql.append(limitit);
            sql.append(" and fileid != 0 group by userid ");
            
            pstmt.close();
            
            sql.append(" for read only with UR");
            
            sqlS = sql.toString();
            DebugPrint.println(DebugPrint.INFO5, "Dropbox ReportQ: " + 
                               sqlS);
            pstmt=connection.prepareStatement(sqlS);
            
            rs=conn.executeQuery(pstmt);
            
            while(rs.next()) {
               DboxReportPackageAccess ainfo = new DboxReportPackageAccess();
               
               obj = rs.getObject("userid");  if (obj == null) obj = "";
               ainfo.setAccessName(obj.toString());
               
               int  complete = rs.getInt("complete");
               int  failed   = rs.getInt("failed");
               
               if     (pinfo.getPackageNumFiles().equals(""+complete)) {
                  ainfo.setAccessState("Complete");
               } else if (failed   > 0) {
                  ainfo.setAccessState("Failure");
               } else {
                  ainfo.setAccessState("Partial");
               }
               
               obj = rs.getObject("acreated");  if (obj == null) obj = "";
               ainfo.setAccessLast(formatAsString("acreated", obj));
               
               obj = rs.getObject("avgxferrate");  if (obj == null) obj = "";
               ainfo.setAccessTransferRate(formatAsString("avgxferrate", obj));
               
               pinfo.addAccessInfo(ainfo);
            }
           
           //
           // Get file access status
           //
            sql = new StringBuffer("SELECT userid, fileid, case when dloadstat = ");
            sql.append(DropboxGenerator.STATUS_COMPLETE);
            sql.append(" then 'Complete' else 'Fail' end as state, created,xferspeed");
            sql.append(" from ");
            
            sql.append("edesign.fileacl where pkgid = ").append(pkgid);
            sql.append(limitit);
            sql.append(" AND fileid != 0 ");
            sql.append(" order by fileid,userid ASC ");
            
            pstmt.close();
            
            sql.append(" for read only with UR");
            
            sqlS = sql.toString();
            DebugPrint.println(DebugPrint.INFO5, "Dropbox ReportQ: " + 
                               sqlS);
            pstmt=connection.prepareStatement(sqlS);
            
            rs=conn.executeQuery(pstmt);
            
            while(rs.next()) {
               DboxReportFileAccess ainfo = new DboxReportFileAccess();
               
               long fid = rs.getLong("fileid");
               DboxReportFileInfo finfo = pinfo.getFileById(""+fid);
               
               if (finfo == null) {
                  DebugPrint.printlnd(DebugPrint.ERROR,
                                      "ReportAction: FInfo missing for accessinfo! "
                                      + fid);
                  continue;
               }
               
               ainfo.setAccessFileName(finfo.getFileName());
               
               obj = rs.getObject("userid");  if (obj == null) obj = "";
               ainfo.setAccessName(obj.toString());
               
               obj = rs.getObject("state");  if (obj == null) obj = "";
               ainfo.setAccessState(obj.toString());
               
               obj = rs.getObject("created");  if (obj == null) obj = "";
               ainfo.setAccessCreated(formatAsString("created", obj));
               
               obj = rs.getObject("xferspeed");  if (obj == null) obj = "";
               ainfo.setAccessTransferRate(obj.toString());
               
               finfo.addAccessInfo(ainfo);
            }
           
           // Let JSP know about packageinfo
            request.setAttribute("packageinfo", pinfo);
            addSavedReports(request, credentials);      
            
         } catch (SQLException e){
            conn.destroyConnection(connection);
            connection=null;
            e.printStackTrace(System.out);
            throw e;
         } finally {
            
            if (pstmt!=null){
               try {
                  pstmt.close();
               } catch(SQLException e) {}
            }
            if (conn != null) conn.returnConnection(connection);
         }               
                  
      } catch(Exception ee) {
         System.out.println("Exception processing Package Dropbox report request:"
                            + ee);
         throw ee;
      }
      
      return mapping.findForward("success");
   }   
   
   
  // --------------------------------------------
  // ----- File Report Impl
  // --------------------------------------------
         
  //
  // Reports for Uploaded/existing files
  //
   public ActionForward fileQuery(ActionMapping       mapping,
                                  ActionForm          form,
                                  HttpServletRequest  request,
                                  HttpServletResponse response)
      throws Exception {
      
      Results results = new Results();
      
      try {
         
         HttpSession session = request.getSession(false);
         
         Credentials credentials = getDropboxCredentials(session);
         if (credentials == null) {
            return mapping.findForward("sessionExpired");
         }
         
         FileForm model = (FileForm)form;
         
         DebugPrint.printlnd(DebugPrint.INFO5, "In fileQuery");
         
         DBConnection conn = getDropboxConnection();
         
         Connection connection=null;
         PreparedStatement pstmt=null;
         ResultSet rs=null;	
         int col = 1;
         
         boolean recordlev = model.getRecordLevel().equalsIgnoreCase("true");
         
        // If its record level, put in a header to let JSP know
         if (recordlev) {
            results.addHeader(model.FILEID, "File Id");
         }
            
         StringBuffer sql = recordlev 
            ? new StringBuffer("select f.fileid as ").append(model.FILEID).append(" ")
            : new StringBuffer("select count(f.fileid) as ").append(model.NumFiles).append(" ");
         
         try {	
            
           // HACK ... fix this
            String sortfield = model.getSortField();
            String sortdir   = model.getSortDirection();
            
            DebugPrint.printlnd(DebugPrint.INFO5, "Sortfield=" + sortfield);
            DebugPrint.printlnd(DebugPrint.INFO5, "Sortdir=" + sortdir);
            
            if (sortdir == null ||
                (!sortdir.equalsIgnoreCase("ASC") && 
                 !sortdir.equalsIgnoreCase("DESC"))) {
               sortdir = "ASC";
            }
            
            if (sortfield == null) sortfield = "";
            
            connection=conn.getConnection();
            
            Vector groupby = new Vector();
            Vector orderby = new Vector();
            
            StringBuffer where = new StringBuffer();
                        
           // If this guy is NOT a Super User, then scope what he can see to himself
           // This means things he owns, and things that have been sent to him
            where.append(getVisiblePackageList(credentials));
                        
            String s = model.User;
            if (model.getUser()) {
               results.addHeader(s, "User");
               sql.append(",p.ownerid as ").append(s);
               col++;
               if (sortfield.equals(s)) orderby.addElement(""+col);
               if (!recordlev) {
                  groupby.add("p.ownerid");
               }
            }
            
           // Apply filter
            if (model.getUserFilter()) {
               where.append(generateFilter(model.getUserFilterValue(),
                                           "p.OWNERID"));
            }
            
            s = model.Company;
            if (model.getCompany()) {
               results.addHeader(s, "Company");
               sql.append(",p.company as ").append(s);
               col++;
               if (sortfield.equals(s)) orderby.addElement(""+col);
               if (!recordlev) {
                  groupby.add("p.company");
               }
            }
            
           // Apply filter
            if (model.getCompanyFilter()) {
               where.append(generateFilter(model.getCompanyFilterValue(),
                                           "p.COMPANY"));
            }
            
           // Have to fix this TODO
            s = model.FileState;
            if (model.getFileState()) {
               results.addHeader(s, "File State");
               
               sql.append(",").append(f_state).append(" as ").append(s);
               
               col++;
               if (sortfield.equals(s)) orderby.addElement(""+col);
               if (!recordlev) {
                  groupby.add(f_state);
               }
            }
            
           // Apply filter
            if (model.getFileStateFilter()) {
               where.append(generateFilter(model.getFileStateFilterModifier(),
                                           f_state));
            }
                        
            s = model.FileSize;
            if (model.getFileSize()) {
               col++;
               if (sortfield.equals(s)) orderby.addElement(""+col);
               if (!recordlev) {
                  results.addHeader(s, "Total Size");
                  sql.append(",SUM(f.filesize) as ").append(s);
               } else {
                  results.addHeader(s, "File Size");
                  sql.append(",f.filesize as ").append(s);
                  groupby.add(s);
               }
            }
            
           // Apply filter
            if (model.getFileSizeFilter()) {
               where.append(" AND f.FILESIZE ");
               where.append(getSQLModifier(model.getFileSizeFilterModifier()));
               where.append(" (");
               where.append(model.getFileSizeFilterValue()).append("*1024) ");
            }
            
            s = model.NumComponents;
            if (model.getNumComponents()) {
               col++;
               if (sortfield.equals(s)) orderby.addElement(""+col);
               if (!recordlev) {
                  results.addHeader(s, "Total Components");
                  sql.append(",SUM(BIGINT(f.componententry)) as ").append(s);
               } else {
                  results.addHeader(s, "Number of Components");
                  sql.append(",f.componententry as ").append(s);
               }
            }
            
            
           // Apply filter
            if (model.getNumComponentsFilter()) {
               where.append(" AND f.componententry ");
               where.append(getSQLModifier(model.getNumComponentsFilterModifier()));
               where.append(" ").append(model.getNumComponentsFilterValue()).append(" ");
            }
            
            if (!recordlev) {
            
              // For Summary attributes, we have to query it if the user wants it 
              //  OR if the user wishes to filter by it
               if (model.getAvgFileSize() || model.getAvgFileSizeFilter()) {
                  s = model.AvgFileSize;
                  col++;
                  if (sortfield.equals(s)) orderby.addElement(""+col);
                  if (model.getAvgFileSize()) {
                     results.addHeader(s, "Avg File Size");
                  }
                  sql.append(",AVG(f.filesize) as ").append(s);
               }
               
               if (model.getNumFiles() || model.getNumFilesFilter()) {
                  s = model.NumFiles;
                  col++;
                  if (sortfield.equals(s)) orderby.addElement(""+col);
                  if (model.getNumFiles()) {
                     results.addHeader(s, "Num Files");
                  }
                 // We already added this up top
                 // sql.append(",COUNT(f.fileid) as ").append(s);
               }
               
               if (model.getAvgNumComponents() || model.getAvgNumComponentsFilter()) {
                  s = model.AvgNumComponents;
                  col++;
                  if (sortfield.equals(s)) orderby.addElement(""+col);
                  if (model.getAvgNumComponents()) {
                     results.addHeader(s, "Avg Number Components");
                  }
                  sql.append(",AVG(BIGINT(f.componententry)) as ").append(s);
               }
                     
            } else {
            
               s = model.FileName;
               if (model.getFileName()) {
                  results.addHeader(s, "File Name");
                  sql.append(",f.filename as ").append(s);
                  col++;
                  if (sortfield.equals(s)) orderby.addElement(""+col);
                  if (!recordlev) {
                     groupby.add("f.filename");
                  }
               }
                           
               if (model.getCreateTime()) {
                  s = model.CreateTime;
                  col++;
                  if (sortfield.equals(s)) orderby.addElement(""+col);
                  results.addHeader(s, "Create Time");
                  sql.append(",f.created as ").append(s);
               }
               
                              
               if (model.getDeleteTime()) {
                  s = model.DeleteTime;
                  col++;
                  if (sortfield.equals(s)) orderby.addElement(""+col);
                  results.addHeader(s, "Delete Time");
                  sql.append(",f.deleted as ").append(s);
               }
            }
            
           // Apply filter
            if (model.getFileNameFilter()) {
               where.append(generateFilter(model.getFileNameFilterValue(),
                                           "f.filename"));
            }
            
           // Apply filter
            if (model.getCreateTimeFilter()) {
               String sqlop = getSQLModifier(model.getCreateTimeFilterModifier());
               where.append(" AND f.created ").append(sqlop).append(" ");
               where.append(dateAsSqlDateString(model.getCreateTimeStartDate()));
               where.append(" ");
               if (sqlop.equalsIgnoreCase("between")) {
                  where.append(" AND ");
                  where.append(dateAsSqlDateString(model.getCreateTimeEndDate()));
                  where.append(" ");
               }
            }
           // Apply filter
            if (model.getDeleteTimeFilter()) {
               String sqlop = getSQLModifier(model.getDeleteTimeFilterModifier());
               where.append(" AND f.deleted ").append(sqlop).append(" ");
               where.append(dateAsSqlDateString(model.getDeleteTimeStartDate()));
               where.append(" ");
               if (sqlop.equalsIgnoreCase("between")) {
                  where.append(" AND ");
                  where.append(dateAsSqlDateString(model.getDeleteTimeEndDate()));
                  where.append(" ");
               }
            }
               
            sql.append(" from edesign.package p,edesign.filetopkg fp,edesign.file f");
            sql.append(" where p.pkgid = fp.pkgid AND fp.fileid = f.fileid AND p.created <= REPORT_END AND (p.DELETED is null OR p.deleted >= REPORT_START) ");
 
            sql.append(where.toString());
            
            if (!recordlev) {
               if (groupby.size() > 0) {
                  sql.append(" group by ");
                  Enumeration enum = groupby.elements();
                  boolean first = true;
                  while(enum.hasMoreElements()) {
                     if (!first) sql.append(",");
                     first = false;
                     sql.append((String)enum.nextElement());
                  }
               }
            }
            
            if (orderby.size() > 0) {
               sql.append(" order by ");
               Enumeration enum = orderby.elements();
               boolean first = true;
               while(enum.hasMoreElements()) {
                  if (!first) sql.append(",");
                  first = false;
                  sql.append((String)enum.nextElement());
               }
               
               sql.append(" ").append(sortdir).append(" ");
            }
            
            sql.append(" for read only with UR");
         
            int idx;
            String sqlS = sql.toString();
            String scS = dateAsSqlDateString(model.getStartDate());
            while ((idx=sqlS.indexOf("REPORT_START")) >= 0) {
               sqlS = sqlS.substring(0,idx) + scS + sqlS.substring(idx+12);
            }
               
            String ecS = dateAsSqlDateString(model.getEndDate());
            while ((idx=sqlS.indexOf("REPORT_END")) >= 0) {
               sqlS = sqlS.substring(0,idx) + ecS + sqlS.substring(idx+10);
            }

            DebugPrint.println(DebugPrint.INFO5, "Dropbox ReportQ: " + 
                               sqlS);
            pstmt=connection.prepareStatement(sqlS);
         
            rs=conn.executeQuery(pstmt);
         
           // Get meta info 
            ResultSetMetaData rsm = rs.getMetaData();
            int numcols = rsm.getColumnCount();
            
           // get local vars for this
            boolean avgfilesizefilter = model.getAvgFileSizeFilter();
            boolean avgnumcomponentsfilter = model.getAvgNumComponentsFilter();
            boolean numfilesfilter    = model.getNumFilesFilter();
            boolean dofilt = (!recordlev && 
                              (avgfilesizefilter || avgnumcomponentsfilter || 
                               numfilesfilter));
            
            int avgfilesizefiltop = 
               getSQLModifierAsNumber(model.getAvgFileSizeFilterModifier());
            int avgnumcomponentsfiltop = 
               getSQLModifierAsNumber(model.getAvgNumComponentsFilterModifier());
            int numfilesfiltop = 
               getSQLModifierAsNumber(model.getNumFilesFilterModifier());
            
           // Set the suggested alignment for each column
            for(int j=1; j <= numcols; j++) {
               String key = rsm.getColumnLabel(j) ;
               results.setHeaderAlignment(key, getAlignment(key));
            }
            
           while(rs.next()) {
           
             // Apply Summary filters
               if (dofilt) {
                  if (avgfilesizefilter) {
                     long v  = rs.getLong(model.AvgFileSize);
                     long vv = model.getAvgFileSizeAsLong();
                     vv *= 1024;
                     if (!applyFilter(avgfilesizefiltop, v, vv)) continue; 
                  }
                  if (avgnumcomponentsfilter) {
                     long v  = rs.getLong(model.AvgNumComponents);
                     long vv = model.getAvgNumComponentsAsLong();
                     if (!applyFilter(avgnumcomponentsfiltop, v, vv)) continue; 
                  }
                  if (numfilesfilter) {
                     long v  = rs.getLong(model.NumFiles);
                     long vv = model.getNumFilesAsLong();
                     if (!applyFilter(numfilesfiltop, v, vv)) continue; 
                  }
               }
               
               Hashtable row = new Hashtable();
               for(int j=1; j <= numcols; j++) {
               
                  String key = rsm.getColumnLabel(j) ;
                  Object obj = rs.getObject(j);
                  
                  if (obj == null || rs.wasNull()) {
                     row.put(key.toUpperCase(), "-");
                  } else {
                     row.put(key.toUpperCase(), formatAsString(key, obj));
                  }
               }
               results.addRow(row);
            } 
         
         } catch (SQLException e){
            conn.destroyConnection(connection);
            connection=null;
            e.printStackTrace(System.out);
            throw e;
         } finally {
            
            if (pstmt!=null){
               try {
                  pstmt.close();
               } catch(SQLException e) {}
            }
            if (conn != null) conn.returnConnection(connection);
         }               
         
        // Give the JSP our notion of good order of cols
         results.addOrderedHeader(FileForm.FILEID);
         results.addOrderedHeader(FileForm.User);
         results.addOrderedHeader(FileForm.Company);
         results.addOrderedHeader(FileForm.FileName);
         results.addOrderedHeader(FileForm.NumFiles);
         results.addOrderedHeader(FileForm.FileState);
         results.addOrderedHeader(FileForm.FileSize);
         results.addOrderedHeader(FileForm.XferRate);
         results.addOrderedHeader(FileForm.NumComponents);
         results.addOrderedHeader(FileForm.AvgFileSize);
         results.addOrderedHeader(FileForm.AvgNumComponents);
         results.addOrderedHeader(FileForm.CreateTime);
         results.addOrderedHeader(FileForm.DeleteTime);
         
         request.setAttribute("results", results);
         addSavedReports(request, credentials);      
         
      } catch(Exception ee) {
         System.out.println("Exception processing File Dropbox report request:"
                            + ee);
         throw ee;
      }
      
      return mapping.findForward("success");
   }    
   
  //
  // Reports for Downloaded files
  //
   public ActionForward fileDownloadQuery(ActionMapping       mapping,
                                          ActionForm          form,
                                          HttpServletRequest  request,
                                          HttpServletResponse response)
      throws Exception {
      
      Results results = new Results();
      
      
      try {
         
         HttpSession session = request.getSession(false);
         
         Credentials credentials = getDropboxCredentials(session);
         if (credentials == null) {
            return mapping.findForward("sessionExpired");
         }
         
        // issuper is set if we should be able to see all entries for file
        //  downloads (not just for packages owned by the user)
         boolean isfdsuper = credentials.isFSESuper() || credentials.isSuper();
         
         DownloadFileForm model = (DownloadFileForm)form;
         
         DebugPrint.printlnd(DebugPrint.INFO5, "In fileDownloadQuery");
         
         DBConnection conn = getDropboxConnection();
         
         Connection connection=null;
         PreparedStatement pstmt=null;
         ResultSet rs=null;	
         int col = 1;
         
         boolean recordlev = model.getRecordLevel().equalsIgnoreCase("true");
         
        // If its record level, put in a header to let JSP know
         if (recordlev) {
            results.addHeader(model.FILEID, "File Id");
         }
            
         StringBuffer sql = recordlev 
            ? new StringBuffer("select f.fileid as ").append(model.FILEID).append(" ")
            : new StringBuffer("select count(f.fileid) as ").append(model.NumFiles).append(" ");
         
         try {	
            
           // HACK ... fix this
            String sortfield = model.getSortField();
            String sortdir   = model.getSortDirection();
            
            DebugPrint.printlnd(DebugPrint.INFO5, "Sortfield=" + sortfield);
            DebugPrint.printlnd(DebugPrint.INFO5, "Sortdir=" + sortdir);
            
            if (sortdir == null ||
                (!sortdir.equalsIgnoreCase("ASC") && 
                 !sortdir.equalsIgnoreCase("DESC"))) {
               sortdir = "ASC";
            }
            
            if (sortfield == null) sortfield = "";
            
            connection=conn.getConnection();
            
            Vector groupby = new Vector();
            Vector orderby = new Vector();
            
            StringBuffer where = new StringBuffer();
                        
           // If this guy is NOT a Super User, then scope what he can see to himself
           // This means things he owns, and things that have been sent to him
            where.append(getVisiblePackageList(credentials));
            
           // If this guy should NOT see everything for packages he has 
           //  visibility to ... then limit it
            if (!isfdsuper) {
               where.append(" AND (p.ownerid = '");
               where.append(credentials.getName());
               where.append("' OR fa.userid = '");
               where.append(credentials.getName());
               where.append("') ");
            }
                        
            String s = model.User;
            if (model.getUser()) {
               results.addHeader(s, "User");
               sql.append(",fa.userid as ").append(s);
               col++;
               if (sortfield.equals(s)) orderby.addElement(""+col);
               if (!recordlev) {
                  groupby.add("fa.userid");
               }
            }
            
           // Apply filter
            if (model.getUserFilter()) {
               where.append(generateFilter(model.getUserFilterValue(),
                                           "fa.userid"));
            }
            
            s = model.OwnerUser;
            if (model.getOwnerUser()) {
               results.addHeader(s, "Owning User");
               sql.append(",p.ownerid as ").append(s);
               col++;
               if (sortfield.equals(s)) orderby.addElement(""+col);
               if (!recordlev) {
                  groupby.add("p.ownerid");
               }
            }
            
           // Apply filter
            if (model.getOwnerUserFilter()) {
               where.append(generateFilter(model.getOwnerUserFilterValue(),
                                           "p.OWNERID"));
            }
            
            s = model.Company;
            if (model.getCompany()) {
               results.addHeader(s, "Company");
               sql.append(",fa.company as ").append(s);
               col++;
               if (sortfield.equals(s)) orderby.addElement(""+col);
               if (!recordlev) {
                  groupby.add("fa.company");
               }
            }
            
           // Apply filter
            if (model.getCompanyFilter()) {
               where.append(generateFilter(model.getCompanyFilterValue(),
                                           "fa.COMPANY"));
            }
            
            s = model.OwnerCompany;
            if (model.getOwnerCompany()) {
               results.addHeader(s, "Owning Company");
               sql.append(",p.company as ").append(s);
               col++;
               if (sortfield.equals(s)) orderby.addElement(""+col);
               if (!recordlev) {
                  groupby.add("p.company");
               }
            }
            
           // Apply filter
            if (model.getOwnerCompanyFilter()) {
               where.append(generateFilter(model.getOwnerCompanyFilterValue(),
                                           "p.COMPANY"));
            }
            
           // Have to fix this TODO
            s = model.FileState;
            if (model.getFileState()) {
               results.addHeader(s, "Download Status");
               
//               sql.append(",fa.state as ").append(s);
               sql.append(",").append(fa_state).append(" as ").append(s);
               
               col++;
               if (sortfield.equals(s)) orderby.addElement(""+col);
               if (!recordlev) {
//                  groupby.add("fa.state");
                  groupby.add(fa_state);
               }
            }
            
           // Apply filter
            if (model.getFileStateFilter()) {
               where.append(generateFilter(model.getFileStateFilterModifier(),
                                           fa_state));
            }
                        
           // Have to fix this TODO
            s = model.PackageName;
            if (model.getPackageName()) {
               results.addHeader(s, "Package Name");
               
               sql.append(",p.pkgname as ").append(s);
               
               col++;
               if (sortfield.equals(s)) orderby.addElement(""+col);
               if (!recordlev) {
                  groupby.add("p.pkgname");
               }
            }
            
           // Apply filter
            if (model.getPackageNameFilter()) {
               where.append(generateFilter(model.getPackageNameFilterValue(),
                                           "p.pkgname"));
            }
                        
            s = model.FileSize;
            if (model.getFileSize()) {
               col++;
               if (sortfield.equals(s)) orderby.addElement(""+col);
               if (!recordlev) {
                  results.addHeader(s, "Total Size");
                  sql.append(",SUM(f.filesize) as ").append(s);
               } else {
                  results.addHeader(s, "File Size");
                  sql.append(",f.filesize as ").append(s);
                  groupby.add(s);
               }
            }
            
           // Apply filter
            if (model.getFileSizeFilter()) {
               where.append(" AND f.FILESIZE ");
               where.append(getSQLModifier(model.getFileSizeFilterModifier()));
               where.append(" (");
               where.append(model.getFileSizeFilterValue()).append("*1024) ");
            }
            
            s = model.NumComponents;
            if (model.getNumComponents()) {
               col++;
               if (sortfield.equals(s)) orderby.addElement(""+col);
               if (!recordlev) {
                  results.addHeader(s, "Total Components");
                  sql.append(",SUM(BIGINT(f.componententry)) as ").append(s);
               } else {
                  results.addHeader(s, "Number of Components");
                  sql.append(",f.componententry as ").append(s);
               }
            }
            
            
           // Apply filter
            if (model.getNumComponentsFilter()) {
               where.append(" AND f.componententry ");
               where.append(getSQLModifier(model.getNumComponentsFilterModifier()));
               where.append(" ").append(model.getNumComponentsFilterValue()).append(" ");
            }
            
            
            if (!recordlev) {
            
              // For Summary attributes, we have to query it if the user wants it 
              //  OR if the user wishes to filter by it
               if (model.getAvgFileSize() || model.getAvgFileSizeFilter()) {
                  s = model.AvgFileSize;
                  col++;
                  if (sortfield.equals(s)) orderby.addElement(""+col);
                  if (model.getAvgFileSize()) {
                     results.addHeader(s, "Avg File Size");
                  }
                  sql.append(",AVG(f.filesize) as ").append(s);
               }
               
               if (model.getAvgXferRate() || model.getAvgXferRateFilter()) {
                  s = model.AvgXferRate;
                  col++;
                  if (sortfield.equals(s)) orderby.addElement(""+col);
                  if (model.getAvgXferRate()) {
                     results.addHeader(s, "Avg Transfer Rate");
                  }
                  sql.append(",AVG(BIGINT(fa.xferspeed)) as ").append(s);
               }
               
               if (model.getNumFiles() || model.getNumFilesFilter()) {
                  s = model.NumFiles;
                  if (sortfield.equals(s)) orderby.addElement(""+1);
                  if (model.getNumFiles()) {
                     results.addHeader(s, "Num Files");
                  }
                 // We already added this up top
                 // sql.append(",COUNT(f.fileid) as ").append(s);
               }
               
               if (model.getAvgNumComponents() || model.getAvgNumComponentsFilter()) {
                  s = model.AvgNumComponents;
                  col++;
                  if (sortfield.equals(s)) orderby.addElement(""+col);
                  if (model.getAvgNumComponents()) {
                     results.addHeader(s, "Avg Number Components");
                  }
                  sql.append(",AVG(BIGINT(f.componententry)) as ").append(s);
               }
                     
            } else {
            
               s = model.FileName;
               if (model.getFileName()) {
                  results.addHeader(s, "File Name");
                  sql.append(",f.filename as ").append(s);
                  col++;
                  if (sortfield.equals(s)) orderby.addElement(""+col);
                  if (!recordlev) {
                     groupby.add("f.filename");
                  }
               }
                           
                           
               s = model.XferRate;
               if (model.getXferRate()) {
                  results.addHeader(s, "Transfer Rate");
                  sql.append(",fa.xferspeed as ").append(s);
                  col++;
                  if (sortfield.equals(s)) orderby.addElement(""+col);
                  if (!recordlev) {
                     groupby.add("fa.xferspeed");
                  }
               }                              
                           
               if (model.getDownloadTime()) {
                  s = model.DownloadTime;
                  col++;
                  if (sortfield.equals(s)) orderby.addElement(""+col);
                  results.addHeader(s, "Download Time");
                  sql.append(",fa.created as ").append(s);
               }
                           
               if (model.getCreateTime()) {
                  s = model.CreateTime;
                  col++;
                  if (sortfield.equals(s)) orderby.addElement(""+col);
                  results.addHeader(s, "Create Time");
                  sql.append(",f.created as ").append(s);
               }               
                              
               if (model.getDeleteTime()) {
                  s = model.DeleteTime;
                  col++;
                  if (sortfield.equals(s)) orderby.addElement(""+col);
                  results.addHeader(s, "Delete Time");
                  sql.append(",f.deleted as ").append(s);
               }
            }
            
           // Apply filter
            if (model.getFileNameFilter()) {
               where.append(generateFilter(model.getFileNameFilterValue(),
                                           "f.filename"));
            }
            
           // Apply filter
            if (model.getXferRateFilter()) {
               where.append(" AND fa.xferspeed ");
               where.append(getSQLModifier(model.getXferRateFilterModifier()));
               where.append(" (");
               where.append(model.getXferRateFilterValue()).append("*1024) ");
            }
            
           // Apply filter
            if (model.getCreateTimeFilter()) {
               String sqlop = getSQLModifier(model.getCreateTimeFilterModifier());
               where.append(" AND f.created ").append(sqlop).append(" ");
               where.append(dateAsSqlDateString(model.getCreateTimeStartDate()));
               where.append(" ");
               if (sqlop.equalsIgnoreCase("between")) {
                  where.append(" AND ");
                  where.append(dateAsSqlDateString(model.getCreateTimeEndDate()));
                  where.append(" ");
               }
            }
           // Apply filter
            if (model.getDeleteTimeFilter()) {
               String sqlop = getSQLModifier(model.getDeleteTimeFilterModifier());
               where.append(" AND f.deleted ").append(sqlop).append(" ");
               where.append(dateAsSqlDateString(model.getDeleteTimeStartDate()));
               where.append(" ");
               if (sqlop.equalsIgnoreCase("between")) {
                  where.append(" AND ");
                  where.append(dateAsSqlDateString(model.getDeleteTimeEndDate()));
                  where.append(" ");
               }
            }
            
           // Apply filter
           /* Download time filter is actually the main selection
            if (model.getDownloadTimeFilter()) {
               String sqlop = getSQLModifier(model.getDownloadTimeFilterModifier());
               where.append(" AND fa.created ").append(sqlop).append(" ");
               where.append(dateAsSqlDateString(model.getDownloadTimeStartDate()));
               where.append(" ");
               if (sqlop.equalsIgnoreCase("between")) {
                  where.append(" AND ");
                  where.append(dateAsSqlDateString(model.getDownloadTimeEndDate()));
                  where.append(" ");
               }
            }
           */
               
            sql.append(" from edesign.package p,edesign.filetopkg fp,");
            sql.append("edesign.file f, edesign.fileacl fa ");                       
            sql.append(" where p.pkgid = fp.pkgid AND fp.fileid = f.fileid AND fa.pkgid = p.pkgid AND fa.fileid = f.fileid AND p.created <= REPORT_END AND (p.deleted is null OR p.deleted >= REPORT_START) AND fa.created between REPORT_START and REPORT_END ");
 
            sql.append(where.toString());
            
            if (!recordlev) {
               if (groupby.size() > 0) {
                  sql.append(" group by ");
                  Enumeration enum = groupby.elements();
                  boolean first = true;
                  while(enum.hasMoreElements()) {
                     if (!first) sql.append(",");
                     first = false;
                     sql.append((String)enum.nextElement());
                  }
               }
            }
            
            if (orderby.size() > 0) {
               sql.append(" order by ");
               Enumeration enum = orderby.elements();
               boolean first = true;
               while(enum.hasMoreElements()) {
                  if (!first) sql.append(",");
                  first = false;
                  sql.append((String)enum.nextElement());
               }
               
               sql.append(" ").append(sortdir).append(" ");
            }
            
            sql.append(" for read only with UR");
         
            int idx;
            String sqlS = sql.toString();
            String scS = dateAsSqlDateString(model.getStartDate());
            while ((idx=sqlS.indexOf("REPORT_START")) >= 0) {
               sqlS = sqlS.substring(0,idx) + scS + sqlS.substring(idx+12);
            }
               
            String ecS = dateAsSqlDateString(model.getEndDate());
            while ((idx=sqlS.indexOf("REPORT_END")) >= 0) {
               sqlS = sqlS.substring(0,idx) + ecS + sqlS.substring(idx+10);
            }

            DebugPrint.println(DebugPrint.INFO5, "Dropbox ReportQ: " + 
                               sqlS);
            pstmt=connection.prepareStatement(sqlS);
         
            rs=conn.executeQuery(pstmt);
         
           // Get meta info 
            ResultSetMetaData rsm = rs.getMetaData();
            int numcols = rsm.getColumnCount();
            
           // get local vars for this
            boolean avgfilesizefilter = model.getAvgFileSizeFilter();
            boolean avgnumcomponentsfilter = model.getAvgNumComponentsFilter();
            boolean numfilesfilter    = model.getNumFilesFilter();
            boolean avgxferratefilter    = model.getAvgXferRateFilter();
            boolean dofilt = (!recordlev && 
                              (avgfilesizefilter || avgnumcomponentsfilter || 
                               numfilesfilter    || avgxferratefilter));
            
            int avgfilesizefiltop = 
               getSQLModifierAsNumber(model.getAvgFileSizeFilterModifier());
            int avgnumcomponentsfiltop = 
               getSQLModifierAsNumber(model.getAvgNumComponentsFilterModifier());
            int avgxferratefiltop = 
               getSQLModifierAsNumber(model.getAvgXferRateFilterModifier());
            int numfilesfiltop = 
               getSQLModifierAsNumber(model.getNumFilesFilterModifier());
            
            
           // Set the suggested alignment for each column
            for(int j=1; j <= numcols; j++) {
               String key = rsm.getColumnLabel(j) ;
               results.setHeaderAlignment(key, getAlignment(key));
            }
            
           while(rs.next()) {
           
             // Apply Summary filters
               if (dofilt) {
                  if (avgfilesizefilter) {
                     long v  = rs.getLong(model.AvgFileSize);
                     long vv = model.getAvgFileSizeAsLong();
                     vv *= 1024;
                     if (!applyFilter(avgfilesizefiltop, v, vv)) continue; 
                  }
                  if (avgnumcomponentsfilter) {
                     long v  = rs.getLong(model.AvgNumComponents);
                     long vv = model.getAvgNumComponentsAsLong();
                     if (!applyFilter(avgnumcomponentsfiltop, v, vv)) continue; 
                  }
                  if (numfilesfilter) {
                     long v  = rs.getLong(model.NumFiles);
                     long vv = model.getNumFilesAsLong();
                     if (!applyFilter(numfilesfiltop, v, vv)) continue; 
                  }
                  if (avgxferratefilter) {
                     long v  = rs.getLong(model.AvgXferRate);
                     long vv = model.getAvgXferRateAsLong();
                     vv *= 1024;
                     if (!applyFilter(avgxferratefiltop, v, vv)) continue; 
                  }
               }
               
               Hashtable row = new Hashtable();
               for(int j=1; j <= numcols; j++) {
               
                  String key = rsm.getColumnLabel(j) ;
                  Object obj = rs.getObject(j);
                  
                  if (obj == null || rs.wasNull()) {
                     row.put(key.toUpperCase(), "-");
                  } else {
                     row.put(key.toUpperCase(), formatAsString(key, obj));
                  }
               }
               results.addRow(row);
            } 
         
         } catch (SQLException e){
            conn.destroyConnection(connection);
            connection=null;
            e.printStackTrace(System.out);
            throw e;
         } finally {
            
            if (pstmt!=null){
               try {
                  pstmt.close();
               } catch(SQLException e) {}
            }
            if (conn != null) conn.returnConnection(connection);
         }               
         
        // Give the JSP our notion of good order of cols
         results.addOrderedHeader(FileForm.FILEID);
         results.addOrderedHeader(FileForm.User);
         results.addOrderedHeader(FileForm.Company);
         results.addOrderedHeader(DownloadFileForm.OwnerUser);
         results.addOrderedHeader(DownloadFileForm.OwnerCompany);
         results.addOrderedHeader(DownloadFileForm.PackageName);
         results.addOrderedHeader(FileForm.FileName);
         results.addOrderedHeader(FileForm.NumFiles);
         results.addOrderedHeader(FileForm.FileState);
         results.addOrderedHeader(FileForm.FileSize);
         results.addOrderedHeader(FileForm.XferRate);
         results.addOrderedHeader(FileForm.NumComponents);
         results.addOrderedHeader(FileForm.AvgFileSize);
         results.addOrderedHeader(FileForm.AvgXferRate);
         results.addOrderedHeader(FileForm.AvgNumComponents);
         results.addOrderedHeader(DownloadFileForm.DownloadTime);
         results.addOrderedHeader(FileForm.CreateTime);
         results.addOrderedHeader(FileForm.DeleteTime);
         request.setAttribute("results", results);
         addSavedReports(request, credentials);      
         
      } catch(Exception ee) {
         System.out.println("Exception processing File Dropbox report request:"
                            + ee);
         throw ee;
      }
      
      return mapping.findForward("success");
   }    
   
   public ActionForward fileInfo(ActionMapping       mapping,
                                 ActionForm          form,
                                 HttpServletRequest  request,
                                 HttpServletResponse response)
      throws Exception {
      
      Results results = new Results();
      
      try {
         
         HttpSession session = request.getSession(false);
         
         Credentials credentials = getDropboxCredentials(session);
         if (credentials == null) {
            return mapping.findForward("sessionExpired");
         }
         
         String fileid = request.getParameter(FileForm.FILEID);
         
         DboxReportFileInfo finfo = new DboxReportFileInfo();
         
         DebugPrint.printlnd(DebugPrint.INFO5, "In fileInfo");
         
         DBConnection conn = getDropboxConnection();
         
         Connection connection=null;
         PreparedStatement pstmt=null;
         ResultSet rs=null;	
         int col = 1;
         
        // Look up file attributes
         StringBuffer sql = new StringBuffer("SELECT fileid,filename,");
         sql.append(f_state);
         sql.append(" as state,md5,created,deleted,filesize,componententry from edesign.file f ");
         
         try {	
            
            connection=conn.getConnection();
            
           // TODO Check fileid is a number
            sql.append(" where f.fileid = ").append(fileid);
            
            sql.append(" for read only with UR");
         
            String sqlS = sql.toString();
            DebugPrint.println(DebugPrint.INFO5, "Dropbox ReportQ: " + sqlS);
            pstmt=connection.prepareStatement(sqlS);
         
            rs=conn.executeQuery(pstmt);
         
            if (!rs.next()) {
               return mapping.findForward("nofile");
            }
           
            Object obj;
            
            finfo.setFileId(fileid);
            
            obj = rs.getObject("filename");  if (obj == null) obj = "";
            finfo.setFileName(obj.toString());
            
            obj = rs.getObject("state");  if (obj == null) obj = "";
            finfo.setFileState(obj.toString());
            
            obj = rs.getObject("filesize");  if (obj == null) obj = "";
            finfo.setFileSize(obj.toString());
            
            obj = rs.getObject("md5");  if (obj == null) obj = "";
            finfo.setFileMD5(obj.toString());
            
            obj = rs.getObject("componententry");  if (obj == null) obj = "";
            finfo.setFileNumComponents(obj.toString());
            
            obj = rs.getObject("created");  if (obj == null) obj = "";
            finfo.setFileCreated(formatAsString("created", obj));
            
            obj = rs.getObject("deleted");  if (obj == null) obj = "";
            finfo.setFileDeleted(formatAsString("deleted", obj));
            
            pstmt.close();
            
           //
           // Now get all packages containing file
           //
            sql = new StringBuffer("select distinct p.pkgid,p.ownerid");
            sql.append(",p.company,").append(p_pkgstate).append(" as pkgstate,");
            sql.append("p.pkgsize,p.fileentry, p.pkgname");
            sql.append(",p.created as pcreated,p.committed as pcommitted,p.deleted as pdeleted" );
            sql.append(" from edesign.package p, edesign.filetopkg fp ");
            
            StringBuffer where = new StringBuffer(" fp.fileid = FILEID_REPLACE AND fp.pkgid = p.pkgid ");
            
           // If this guy is NOT a Super User, then scope what he can see to himself
           // This means things he owns, and things that have been sent to him
            where.append(getVisiblePackageList(credentials));
            
            sql.append(" where ").append(where.toString());
            
            sql.append(" for read only with UR");
         
            int idx;
            sqlS = sql.toString();
            while ((idx=sqlS.indexOf("FILEID_REPLACE")) >= 0) {
               sqlS = sqlS.substring(0,idx) + fileid + sqlS.substring(idx+14);
            }
            
            DebugPrint.println(DebugPrint.INFO5, "Dropbox ReportQ: " + 
                               sqlS);
            pstmt=connection.prepareStatement(sqlS);
         
            rs=conn.executeQuery(pstmt);
         
            
            int num = 0;
            while (rs.next()) {
            
               DboxReportPackageInfo pinfo = new DboxReportPackageInfo();
               
               obj = rs.getObject("pkgid");  if (obj == null) obj = "";
               pinfo.setPackageId(obj.toString());
               
               obj = rs.getObject("pkgname");  if (obj == null) obj = "";
               pinfo.setPackageName(obj.toString());
               
               obj = rs.getObject("ownerid");  if (obj == null) obj = "";
               pinfo.setPackageOwner(obj.toString());
               
               obj = rs.getObject("company");  if (obj == null) obj = "";
               pinfo.setPackageCompany(obj.toString());
               
               obj = rs.getObject("pkgstate");  if (obj == null) obj = "";
               pinfo.setPackageState(obj.toString());
               
               obj = rs.getObject("pkgsize");  if (obj == null) obj = "";
               pinfo.setPackageSize(obj.toString());
               
               obj = rs.getObject("fileentry");  if (obj == null) obj = "";
               pinfo.setPackageNumFiles(obj.toString());
               
               obj = rs.getObject("pcreated");  if (obj == null) obj = "";
               pinfo.setPackageCreated(formatAsString("pcreated", obj));
               
               obj = rs.getObject("pcommitted");  if (obj == null) obj = "";
               pinfo.setPackageCommitted(formatAsString("pcommitted", obj));
               
               obj = rs.getObject("pdeleted");  if (obj == null) obj = "";
               pinfo.setPackageDeleted(formatAsString("pdeleted", obj));
               
               boolean isowner = pinfo.getPackageOwner().equals(credentials.getName());
               
               pinfo.setLimited(!(isowner || credentials.isSuper()));
               
               pinfo.addFile(finfo);
               
               finfo.addPackage(pinfo);
               
               num++;
            }
            
            if (num == 0) {
               return mapping.findForward("nofileaccess");
            }
            
           // Let JSP know about packageinfo
            request.setAttribute("fileinfo", finfo);
            addSavedReports(request, credentials);      
            
         } catch (SQLException e){
            conn.destroyConnection(connection);
            connection=null;
            e.printStackTrace(System.out);
            throw e;
         } finally {
            
            if (pstmt!=null){
               try {
                  pstmt.close();
               } catch(SQLException e) {}
            }
            if (conn != null) conn.returnConnection(connection);
         }               
                  
      } catch(Exception ee) {
         System.out.println("Exception processing Package Dropbox report request:"
                            + ee);
         throw ee;
      }
      
      return mapping.findForward("success");
   }   
   
}
