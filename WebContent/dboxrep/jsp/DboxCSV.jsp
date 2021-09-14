<%@ page session="true" language="java" import="oem.edge.ed.odc.webdropbox.reports.*,java.util.*,java.io.*,java.text.*" %>
<%
  /*
  ** Generic JSP to download Results as CSV. Put a method on Results to stoke
  **  sorted order. Not really a JSP, more a servlet. 
  **
  **  This is DEFINITELY the wrong way to do this. Each \n in this file is seen in the
  **   output stream (each < % % > section counts as one \n). Leave it for now.
  */
   
   response.setContentType("application/download");
   response.setHeader("Content-Disposition", 
                      "attachment; filename=\"report.csv\";");
                      
   Results results = (Results)request.getAttribute("results");
   if (results != null) {
   
      Enumeration enum = results.getOrderedHeaders();
      int i=0;
      while(enum.hasMoreElements()) {
         String h = (String)enum.nextElement();
         if (i++ > 0) {
            out.print(",");
         }
         out.print(results.getHeader(h));
      }
      if (i > 0) {
         out.println();
         
         while(results.hasMoreElements()) {
            results.nextRow();
         
            enum = results.getOrderedHeaders();
            i=0;
            while(enum.hasMoreElements()) {
               String h = (String)enum.nextElement();
               if (i++ > 0) {
                  out.print(",");
               }
               String v = results.getValue(h);
               
              // Escape backslashes and quotes
               int idx = 0;
               while((idx = v.indexOf("\\", idx)) >= 0) {
                  idx++;
                  v = v.substring(0,idx) + "\\" + v.substring(idx);
               }
               idx = 0;
               while((idx = v.indexOf("\"", idx)) >= 0) {
                  idx++;
                  v = v.substring(0,idx) + "\"" + v.substring(idx);
               }
               out.print("\"");
               out.print(v);
               out.print("\"");
            }
            out.println();
         }
      }
   }
%>
