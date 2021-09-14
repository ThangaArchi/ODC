package oem.edge.ed.odc.webdropbox.reports;

import org.apache.struts.action.*;
import javax.servlet.http.*;
import javax.servlet.*;

import java.util.*;
import java.text.*;
import java.lang.*;

public class SavedReportForm extends ActionForm {

   static public final int ABSOLUTE       = 1;
   static public final int LASTMONTH      = 2;
   static public final int THISMONTH      = 3;
   static public final int ELAPSEDMONTH   = 4;
   static public final int LASTQUARTER    = 5;
   static public final int THISQUARTER    = 6;
   static public final int ELAPSEDQUARTER = 7;
   static public final int LASTYEAR       = 8;
   static public final int THISYEAR       = 9;
   static public final int ELAPSEDYEAR    = 10;
   static public final int RELATIVE       = 11;

   public SavedReportForm() {
   }
   
   protected Map supportedReportTypes;
   public void initializeSupportedReportTypes() {
      if (supportedReportTypes == null) {
         supportedReportTypes = new HashMap();
         supportedReportTypes.put("SessionForm",      "Session");
         supportedReportTypes.put("PackageForm",      "Package");
         supportedReportTypes.put("FileForm",         "File");
         supportedReportTypes.put("DownloadFileForm", "DownloadFile");
      }
   }
   public Collection getSupportedReportTypes() {
      initializeSupportedReportTypes();
      return supportedReportTypes.keySet();
   }
   
   
   public String getReportTypeLabel(String s) {
      initializeSupportedReportTypes();
      return (String)supportedReportTypes.get(s);
   }
   
   
   protected boolean firsttime = true;
   
   protected String reportName = null;
   protected String reportType = null;
   protected String reportId   = null;
   protected String datemgmt   = null;
   
  //
  // -- Setters and Getters
  //
  
   public String  getReportName()     { return reportName; }
   public void    setReportName(String v) { reportName = v;    }
    
   public String  getReportType()     { return reportType; }
   public void    setReportType(String v) { reportType = v;    }
    
   public String  getReportTypeLabel() {
      initializeSupportedReportTypes();
      return (String)supportedReportTypes.get(reportType);
   }
    
   public String  getReportId()     { return reportId; }
   public void    setReportId(String v) { reportId = v;    }
   
   public String  getDateManagement()     { return datemgmt; }
   public void    setDateManagement(String v) { datemgmt = v;    }
   
   public void reset(ActionMapping mapping, javax.servlet.ServletRequest req) {
      if (mapping != null) {
         String s = mapping.getParameter();
         if (s != null && s.indexOf(":NORESET:") >= 0) {
            return;
         }
      }
      reset(false);
   }
   
   public void reset(ActionMapping mapping, 
                     javax.servlet.http.HttpServletRequest req) {
      if (mapping != null) {
         String s = mapping.getParameter();
         if (s != null && s.indexOf(":NORESET:") >= 0) {
            return;
         }
      }
      reset(false);
   }
   
   
   public void reset(boolean toDefault) {
         
      if (toDefault) firsttime = true;
         
      setReportName("");
      setReportType("");
      setReportId("");
      setDateManagement("");
      
      if (firsttime) {
         firsttime = false;
      }
   }
   
   public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
      
      ActionErrors errors = super.validate(mapping, request);
      if (errors == null) errors = new ActionErrors();
      
      return errors;
   }
   
}
