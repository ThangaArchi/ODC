package oem.edge.ed.odc.util;

import  oem.edge.ed.odc.tunnel.common.*;
import  oem.edge.ed.util.*;
import  oem.edge.ed.odc.util.*;
import  java.io.*;
import  java.util.*;
import  java.lang.*;
import  java.sql.*;
/*                       Copyright Header Check                             */
/*   --------------------------------------------------------------------   */
/*                                                                          */
/*     OCO Source Materials                                                 */
/*                                                                          */
/*     Product(s): PROFIT                                                   */
/*                                                                          */
/*     (C)Copyright IBM Corp. 2004-2004,2006                                */
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

public class ReportGenerator {

   static final String rowstart="<tr>";
   static final String headstart="<th style=\"vertical-align: top;\">";
   static final String headend="</th>";
   static final String cellstart="<td style=\"vertical-align: top;\">";
   static final String cellend="</td>";
   static final String rowend="</tr>";
   
   static boolean inServlet = false;
   static String outputdir = ".";
   static String inputfile = null;
   static long   inputfileLastModified = 0;
   static Vector files = null;

   public static void main(String args[]) {
      
      inputfile = "sqlinput";
      for(int i=0; i < args.length; i++) {
         if        (args[i].equals("-inputfile")) {
            inputfile = args[++i];
         } else if (args[i].equals("-outputdir")) {
            outputdir = args[++i];
         } else if (args[i].equalsIgnoreCase("-testdb2")) {
            DBConnection conn = null;
            conn = new DBConnLocalPool();
            conn.setDriver     ("COM.ibm.db2.jdbc.app.DB2Driver");
            conn.setURL        ("jdbc:db2:dropbox");
            conn.setInstance   ("edesign");
            conn.setPasswordDir("");
            DBSource.addDBConnection("dropbox", conn, false);
         } else if (args[i].equalsIgnoreCase("-db2")) {
            DBConnection conn = null;
            conn = new DBConnLocalPool();
            conn.setDriver     (args[++i]);
            conn.setURL        (args[++i]);
            conn.setInstance   (args[++i]);
            conn.setPasswordDir(args[++i]);
            DBSource.addDBConnection("dropbox", conn, false);
         }
      }
      
      try {
         files = parseInputFile(inputfile);
         File reloadFile = new File(inputfile);
         inputfileLastModified = reloadFile.lastModified();
         
         int i=files.size()/4;
         for(int j = -1; j < i; j++) {
            generateReport(null, j, null, true, null, null);
         }
      } catch(Throwable tt) {
      }
   }
      
   public static void setForServlet(String infile) {
      inServlet = true;
      inputfile = infile;
      try {
         files = parseInputFile(infile);
         File reloadFile = new File(inputfile);
         inputfileLastModified = reloadFile.lastModified();
      } catch(Throwable tt) {
         DebugPrint.printlnd(DebugPrint.ERROR, "Error setting up report generator");
         DebugPrint.println(DebugPrint.ERROR, tt);
      }
   }
   
   public static void refreshInputFile() {
      if (inputfile != null) {
         Vector oldfiles = files;
         try {
            File reloadFile = new File(inputfile);
            if (reloadFile.exists() && 
                reloadFile.lastModified() > inputfileLastModified) {
               files=parseInputFile(inputfile);
               inputfileLastModified = reloadFile.lastModified();
            }
         } catch(Throwable tt) {
            files = oldfiles;
            DebugPrint.printlnd(DebugPrint.WARN,
                                "ReportGenerator: refreshInputFile: " +
                                "Error refreshing inputfile " + inputfile);
            DebugPrint.printlnd(DebugPrint.WARN, tt);
         }
      }
   }
   
  // This code assumes that the tables we are interested in for
  //  inclusion/exclusion are:
  //     edesign.package, edesign.fileacl, and edesign.dboxsess 
  //  as either all lower or all UPPER. If found, we want to add incl/exl
  //  logic after final WHERE clause for:
  //
  //    edesign.package:   p.company,  p.ownerid
  //    edesign.fileacl:   fa.company, fa.userid
  //    edesign.dboxsess:  db.company, db.owner
  //
   protected static String manageInclExclModifiers(String sql, String s, 
                                                   boolean includeExclude) {
      if (s != null) {
            
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
         Vector exclUser = new Vector();
         Vector exclComp = new Vector();
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
            boolean incExInner = includeExclude;
            int userCompany = 0;
            idx = ws.indexOf(':');
            if (idx >= 0) {
               String inners = ws.substring(0, idx);
               ws = ws.substring(idx+1);
               if (inners.indexOf("+") == 0) incExInner = true;
               if (inners.indexOf("-") == 0) incExInner = false;
               if (inners.indexOf("U") >= 0) userCompany = 0;
               if (inners.indexOf("C") >= 0) userCompany = 1;
            }
            
           // Now, incExInner is true/false to include/exclude, ws is the
           //  value, and userCompanyServer is 0-2 indicating what variable
           //  we are affecting
            if (ws.indexOf("'") >= 0) {
               DebugPrint.printlnd(DebugPrint.WARN, 
                                   "Bzzz. Report generator given name with emdedded quote!");
            } else {
               
               if (incExInner) {
                  switch(userCompany) {
                     case 0: inclUser.add(ws); break;
                     case 1: inclComp.add(ws); break;
                  }
               } else {
                  switch(userCompany) {
                     case 0: exclUser.add(ws); break;
                     case 1: exclComp.add(ws); break;
                  }
               }
            }
         }
         
         boolean hasPackage = sql.indexOf("edesign.package")  >= 0 || 
                              sql.indexOf("EDESIGN.PACKAGE")  >= 0;
         boolean hasFileAcl = sql.indexOf("edesign.fileacl")  >= 0 || 
                              sql.indexOf("EDESIGN.FILEACL")  >= 0;
         boolean hasSession = sql.indexOf("edesign.dboxsess") >= 0 || 
                              sql.indexOf("EDESIGN.DBOXSESS") >= 0;
                              
         int lastwhereidxL = sql.lastIndexOf(" where ");
         int lastwhereidxU = sql.lastIndexOf(" WHERE ");
         int lastwhereidx  = (lastwhereidxL > lastwhereidxU) ? lastwhereidxL
                                                             : lastwhereidxU;
         
         StringBuffer wheremodifier = new StringBuffer();
         
         if (lastwhereidx > -1) {
            
            s = generateInExList(inclUser);
            if (s != null) {
               if (hasPackage) {
                  if (wheremodifier.length()!=0) wheremodifier.append(" and ");
                  wheremodifier.append(" p.ownerid in ").append(s);
                  wheremodifier.append(" ");
               }
               if (hasFileAcl) {
                  if (wheremodifier.length()!=0) wheremodifier.append(" and ");
                  wheremodifier.append(" fa.userid in ").append(s);
                  wheremodifier.append(" ");
               }
               if (hasSession) {
                  if (wheremodifier.length()!=0) wheremodifier.append(" and ");
                  wheremodifier.append(" db.owner in ").append(s);
                  wheremodifier.append(" ");
               }
            }
            s = generateInExList(inclComp);
            if (s != null) {
               if (hasPackage) {
                  if (wheremodifier.length()!=0) wheremodifier.append(" and ");
                  wheremodifier.append(" p.company in ").append(s);
                  wheremodifier.append(" ");
               }
               if (hasFileAcl) {
                  if (wheremodifier.length()!=0) wheremodifier.append(" and ");
                  wheremodifier.append(" fa.company in ").append(s);
                  wheremodifier.append(" ");
               }
               if (hasSession) {
                  if (wheremodifier.length()!=0) wheremodifier.append(" and ");
                  wheremodifier.append(" db.company in ").append(s);
                  wheremodifier.append(" ");
               }
            }
            s = generateInExList(exclUser);
            if (s != null) {
               if (hasPackage) {
                  if (wheremodifier.length()!=0) wheremodifier.append(" and ");
                  wheremodifier.append(" p.ownerid not in ").append(s);
                  wheremodifier.append(" ");
               }
               if (hasFileAcl) {
                  if (wheremodifier.length()!=0) wheremodifier.append(" and ");
                  wheremodifier.append(" fa.userid not in ").append(s);
                  wheremodifier.append(" ");
               }
               if (hasSession) {
                  if (wheremodifier.length()!=0) wheremodifier.append(" and ");
                  wheremodifier.append(" db.owner not in ").append(s);
                  wheremodifier.append(" ");
               }
            }
            s = generateInExList(exclComp);
            if (s != null) {
               if (hasPackage) {
                  if (wheremodifier.length()!=0) wheremodifier.append(" and ");
                  wheremodifier.append(" p.company not in ").append(s);
                  wheremodifier.append(" ");
               }
               if (hasFileAcl) {
                  if (wheremodifier.length()!=0) wheremodifier.append(" and ");
                  wheremodifier.append(" fa.company not in ").append(s);
                  wheremodifier.append(" ");
               }
               if (hasSession) {
                  if (wheremodifier.length()!=0) wheremodifier.append(" and ");
                  wheremodifier.append(" db.company not in ").append(s);
                  wheremodifier.append(" ");
               }
            }
            
            if (wheremodifier.length() > 0) {
               sql = sql.substring(0, lastwhereidx+7) + 
                  wheremodifier.toString() + " and " +
                  sql.substring(lastwhereidx+7);
            }
         }
      }
      return sql;
   }   
   
  // Generate the report index represented in files as a string.
  // If inServlet is true, assume we are in a table already
   public static String generateReportIndex() {
      StringBuffer out = new StringBuffer();
      
      refreshInputFile();
      
      if (!inServlet) {
         out.append("<html>").append("\n");
         out.append("    <head>").append("\n");
         out.append("    <meta http-equiv=\"content-type\"").append("\n");
         out.append("    content=\"text/html; charset=ISO-8859-1\">").append("\n");
         out.append("    <title>Available Reports</title>").append("\n");
         out.append("    </head>").append("\n");
         out.append("    <body>").append("\n");
         out.append("    <br>").append("\n");
         out.append("    <h1>Available Reports <br></h1>").append("\n");
      }
      
      out.append("    <table cellpadding=\"2\" cellspacing=\"2\" border=\"1\"").append("\n");
      out.append("    style=\"text-align: left;\">").append("\n");
      out.append("    <tbody>").append("\n"); 
         
      out.append(rowstart).append("\n");
      
      out.append(headstart).append("\n");
      if (!inServlet) {
         out.append("File").append("\n");
      } else {
         out.append("Rep#").append("\n");
      }
      out.append(headend).append("\n");
      out.append(headstart).append("\n");
      out.append("Description").append("\n");
      out.append(headend).append("\n");
      
      out.append(headstart).append("\n");
      if (!inServlet) {
         out.append("File").append("\n");
      } else {
         out.append("Rep#").append("\n");
      }
      out.append(headend).append("\n");
      out.append(headstart).append("\n");
      out.append("Description").append("\n");
      out.append(headend).append("\n");
      
      out.append(rowend).append("\n");
      Enumeration enum = files.elements();
      int i=0;
      
      while(enum.hasMoreElements()) {
         String fname   = (String)enum.nextElement();
         String desc    = (String)enum.nextElement();
         Integer lineno = (Integer)enum.nextElement();
         String sql     = (String)enum.nextElement();
         
         if ((i % 2) == 0) {
            if (i != 0) {
               out.append(rowend).append("\n");
            }
            out.append(rowstart).append("\n");
         }
         
         i++;
         out.append(cellstart).append("\n");
         if (inServlet) {
            out.append("<input type=submit name=\"dropboxidx\" value=\"" + i + "\">").append("\n");
         } else {
            out.append("<A href=\"" + fname + "\">" + 
                       fname + "</A>").append("\n");
         }
         out.append(cellend).append("\n");
         out.append(cellstart).append("\n");
         out.append(desc).append("\n");
         out.append(cellend).append("\n");
      }
      
      if (i > 0) out.append(rowend).append("\n");
      
      out.append("    </tbody>").append("\n");
      out.append("</table>").append("\n");
      
      if (!inServlet) {
         out.append("</body>").append("\n");
         out.append("</html>").append("\n");
      }
      return out.toString();
   }
   
   private static String generateInExList(Vector v) {
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
  
  
  // Generate the report specified by repidx. 
   public static void generateReport(PrintWriter out,
                                     int repidx, 
                                     String inclExcl,
                                     boolean includeExclude,
                                     Calendar sc, Calendar ec) 
      throws Exception {
      
      refreshInputFile();
      
      boolean openedOut = false;
      
      if (files == null || files.size()/4 <= repidx) {
         throw new Exception("Invalid report index: " + repidx);
      }
      
      if (out == null) {
         if (inServlet) {
            throw new Exception("No outstrm for report/write && servlet code");
         }
      }
      
      try {
         if (repidx == -1) {
            if (out == null) {
               out = new PrintWriter(new FileWriter(outputdir + 
                                                    File.separator + 
                                                    "index.html"), true);
               openedOut = true;
            }
            out.println(generateReportIndex());
            return;
         }
      
         int realidx = repidx*4;
         String  fname  = (String)files.elementAt(realidx+0);
         String  title  = (String)files.elementAt(realidx+1);
         int     lineno = ((Integer)files.elementAt(realidx+2)).intValue();
         String  sql    = (String)files.elementAt(realidx+3);
         
         if (out == null) {
            out = new PrintWriter(new FileWriter(outputdir + 
                                                 File.separator + 
                                                 fname), true);
            openedOut = true;
         }   
         
         
         
        // If we have start/end times. Replace "REPORT_START" and 
        //  "REPORT_END" respectively
         if (sc != null && ec != null) {
            int yr  = sc.get(Calendar.YEAR);
            int mon = sc.get(Calendar.MONTH)+1;
            int day = sc.get(Calendar.DAY_OF_MONTH);
            int hr  = sc.get(Calendar.HOUR_OF_DAY);
            int min = sc.get(Calendar.MINUTE);
            int sec = sc.get(Calendar.SECOND);
            
            String scS = 
               "'" + 
               yr  + ((mon < 10)?"-0":"-") +
               mon + ((day < 10)?"-0":"-") +
               day + ((hr  < 10)?"-0":"-") +
               hr  + ((min < 10)?".0":".") +
               min + ((sec < 10)?".0":".") +
               sec + "'";
               
            yr  = ec.get(Calendar.YEAR);
            mon = ec.get(Calendar.MONTH)+1;
            day = ec.get(Calendar.DAY_OF_MONTH);
            hr  = ec.get(Calendar.HOUR_OF_DAY);
            min = ec.get(Calendar.MINUTE);
            sec = ec.get(Calendar.SECOND);
            
            String ecS = 
               "'" + 
               yr  + ((mon < 10)?"-0":"-") +
               mon + ((day < 10)?"-0":"-") +
               day + ((hr  < 10)?"-0":"-") +
               hr  + ((min < 10)?".0":".") +
               min + ((sec < 10)?".0":".") +
               sec + "'";
               
            int idx;
            while ((idx=sql.indexOf("REPORT_START")) >= 0) {
               sql = sql.substring(0,idx) + scS + sql.substring(idx+12);
            }
               
            while ((idx=sql.indexOf("REPORT_END")) >= 0) {
               sql = sql.substring(0,idx) + ecS + sql.substring(idx+10);
            }
            
            sql = manageInclExclModifiers(sql, inclExcl, includeExclude);
         }
         
         generateReportSQL(out, title, lineno, sql);
         
      } finally {
         if (openedOut && out != null) {
            try {
               out.close();
            } catch(Exception fce) {}
         }
      }
   }
      
  // Parse the input file and return a vector filled with
  // filename, description, lineno, and SQL Quads
   public static Vector parseInputFile(String inputfile) throws Exception {
      int lineno=0;
      int sqlno=0;
      String lastsql = "";
      Vector files = new Vector();
      
      BufferedReader in = new BufferedReader(new FileReader(inputfile));
      String line;
      String dFileName = "Default.html";
      String dTitle    = "No Title Set";
      String oFileName = dFileName;
      String oTitle    = dTitle;
      
      try { 
      
         while((line=in.readLine()) != null) {
            lineno++;
            line = line.trim();
            if        (line.startsWith("-- TITLE: ")) {
               oTitle    = line.substring(10).trim();
            } else if (line.startsWith("-- FNAME: ")) {
               oFileName = line.substring(10).trim();
            } else if (line.startsWith("--")) {
               ;  // Skip all other comment lines
            } else if (line.length() == 0) {
               ;  // Skip blank lines
            } else if (line.toUpperCase().startsWith("SELECT") ||
                       line.toUpperCase().startsWith("CALL")) { 
               sqlno++;
               
               if (line.endsWith(";")) {
                  line = line.substring(0,line.length()-1);
               }
               
               lastsql = line;
               
               files.addElement(oFileName);
               files.addElement(oTitle);
               files.addElement(new Integer(lineno));
               files.addElement(lastsql);
               
              // This must be an SQL line ... do the deed
               if (DebugPrint.doDebug()) {
                  DebugPrint.println(DebugPrint.DEBUG,
                                     "====================================\n" +
                                     "Report #: " + sqlno              + "\n" +
                                     "Title   : " + oTitle             + "\n" +
                                     "OutFile : " + oFileName          + "\n" +
                                     "SQL    =>\n " + lastsql);
               }
            } else {
               DebugPrint.println(DebugPrint.WARN, 
                                  "Generating Report: Skipping Illegal line " +
                                  lineno + ": " + line);
            }
         }
      } catch(Exception ee) {
         System.out.println("Error generating reports: Line=" + lineno +
                            " sqlstatmentNo = " + sqlno + " Last SQL:\n" +
                            lastsql);
         ee.printStackTrace(System.out);
         throw ee;
      }
      
      return files;
   }
   
   public static void generateReportSQL(PrintWriter out, 
                                        String oTitle, 
                                        int lineno, 
                                        String sql) throws Exception {
      DBConnection conn = DBSource.getDBConnection("dropbox");
      Connection connection=null;
      PreparedStatement pstmt=null;
      ResultSet rs=null;	
      try {	
         
         if (!inServlet) {
            out.println("<html>");
            out.println("    <head>");
            out.println("    <meta http-equiv=\"content-type\"");
            out.println("    content=\"text/html; charset=ISO-8859-1\">");
            out.println("    <title>" + oTitle + "</title>");
            out.println("    </head>");
            out.println("    <body>");
            out.println("    <br>");
         }
         out.println("    <h1>" + oTitle + "<br>");
         out.println("    </h1>");
         
         out.println("    <table cellpadding=\"2\" cellspacing=\"2\" border=\"1\"");
         out.println("    style=\"text-align: left;\">");
         out.println("    <tbody>");               
         
         connection=conn.getConnection();
         
         pstmt=connection.prepareStatement(sql);
         
         rs=conn.executeQuery(pstmt);
         
        // Get meta info 
         ResultSetMetaData rsm = rs.getMetaData();
         int numcols = rsm.getColumnCount();
         
        // Print column names
         out.println(rowstart);
         for(int j=1; j <= numcols; j++) {
            out.println(headstart);
            out.println(rsm.getColumnLabel(j));
            out.println(headend);
         }
         out.println(rowend);
         
         while(rs.next()) {
            out.println(rowstart);
            for(int j=1; j <= numcols; j++) {
               out.println(cellstart);
               Object obj = rs.getObject(j);
               if (obj == null || rs.wasNull()) {
                  out.println("      -");
               } else {
                  out.println("      " + (obj.toString()));
               }
               out.println(cellend);
            }
            out.println(rowend);
         } 
         
         out.println("    </tbody>");
         out.println("</table>");
         if (!inServlet) {
            out.println("</body>");
            out.println("</html>");
         }
         
      } catch (SQLException e){
         out.println("    </tbody>");
         out.println("</table>");
         
         out.println("\n\nError processing SQL!\n\n" + sql);
         
         if (!inServlet) {
            out.println("</body>");
            out.println("</html>");
         }
         
         conn.destroyConnection(connection);
         connection=null;
         e.printStackTrace(out);
         e.printStackTrace(System.out);
      } finally {
         
         if (pstmt!=null){
            try {
               pstmt.close();
            } catch(SQLException e) {}
         }
         conn.returnConnection(connection);
      }      
   }
}
