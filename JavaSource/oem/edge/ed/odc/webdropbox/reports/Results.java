package oem.edge.ed.odc.webdropbox.reports;

import java.util.*;
import java.lang.*;

public class Results {
   
   
   Hashtable headers     = new Hashtable();
   Hashtable headeralign = new Hashtable();
   Hashtable currow  = null;
   int       cursor  = 0;
   Vector    rows    = new Vector(); 
   
   boolean   orderedheadersComplete = false;
   Vector    orderedheaders = null;
   
   Vector    userheaderorder = null;
   
   public Results() {}
   
   public void addHeader(String k, String v) {
      headers.put(k, v);
      orderedheadersComplete = false;
   }
   
   public void setHeaderAlignment(String k, String v) {
      headeralign.put(k.toUpperCase(), v);
   }
   
   public String getHeaderAlignment(String k) {
      String ret = (String)headeralign.get(k.toUpperCase());
      if (ret == null) ret = "left";
      return ret;
   }
   
   public Enumeration getOrderedHeaders() {
      if (!orderedheadersComplete) {
         orderedheaders = new Vector();
         if (userheaderorder != null) {
            Enumeration enum = userheaderorder.elements();
            while(enum.hasMoreElements()) {
               Object s = enum.nextElement();
               if (headers.containsKey(s) && !orderedheaders.contains(s)) {
                  orderedheaders.addElement(s.toString());
               }
            }
            if (orderedheaders.size() != headers.size()) {
               System.out.println("Results.getHeaderOrd: Specified header(s) NOT found");
               enum = headers.keys();
               while(enum.hasMoreElements()) {
                  String s = (String)enum.nextElement();
                  if (!orderedheaders.contains(s)) {
                     System.out.println("    " + s);
                     orderedheaders.addElement(s);
                  }
               }
            }
         } else {
            Enumeration enum = headers.keys();
            while(enum.hasMoreElements()) {
               String s = (String)enum.nextElement();
               if (!orderedheaders.contains(s)) {
                  System.out.println("    " + s);
                  orderedheaders.addElement(s);
               }
            }
         }
         orderedheadersComplete = true;
      }
      return orderedheaders.elements();
   }
   
   public void setOrderedHeaders(Vector v) {
      userheaderorder = (Vector)v.clone();
      orderedheadersComplete = false;
   }
   public void addOrderedHeader(String h) {
      if (userheaderorder == null) userheaderorder = new Vector();
      userheaderorder.addElement(h);
      orderedheadersComplete = false;
   }
   
   public void reset() { cursor = 0; }
   
   public boolean hasMoreElements() {
      return rows.size() > cursor;
   }
   
   public void nextRow() {
      synchronized(rows) {
         currow = (Hashtable)rows.elementAt(cursor++);
      }
   }
   
   public String getValue(String key) { 
      if (currow == null) return null;
      return (String)currow.get(key.toUpperCase());
   }
   public String getHeader(String key) { 
      return (String)headers.get(key);
   }
   
   public Enumeration getHeaders() {
      return headers.keys();
   }
   
   public void addRow(Hashtable hash) {
      rows.addElement(hash);
   }
   
   static final String rowstart ="      <tr>\n";
   static final String headstart="        <th style=\"vertical-align: top;\">";
   static final String headend  ="</th>\n";
   static final String cellstart="        <td style=\"vertical-align: top;\">";
   static final String cellend  ="</td>\n";
   static final String rowend   ="     </tr>\n";
   
   public String toString() {
      StringBuffer out = new StringBuffer();
      out.append("    <br />").append("\n"); 
      out.append("    <h1>Result Dump<br />").append("\n"); 
      out.append("    </h1>").append("\n"); 
      
      out.append("    <table cellpadding=\"2\" cellspacing=\"2\" border=\"1\"").append("\n"); 
      out.append("    style=\"text-align: left;\">").append("\n"); 
      out.append("    <tbody>").append("\n"); 
      
     // Print column names
      out.append(rowstart);
      Enumeration henum = headers.keys();
      while(henum.hasMoreElements()) {
         String hkey = (String)henum.nextElement();
         out.append(headstart);
         out.append(getHeader(hkey)); 
         out.append(headend);
      }
      out.append(rowend);
      
      while(hasMoreElements()) {
         nextRow();
         out.append(rowstart);
         henum = headers.keys();
         while(henum.hasMoreElements()) {
            String hkey = (String)henum.nextElement();
            String val  = getValue(hkey);
            out.append(cellstart);
            if (val != null) {
               out.append(val);
            } else {
               out.append("-");
            }
            out.append(cellend);
         }
         out.append(rowend);
      } 
      
      out.append("    </tbody>").append("\n"); 
      out.append("</table>").append("\n"); 
      return out.toString();
   }
}
