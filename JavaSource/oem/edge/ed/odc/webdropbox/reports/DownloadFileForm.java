package oem.edge.ed.odc.webdropbox.reports;

import org.apache.struts.action.*;
import javax.servlet.http.*;
import javax.servlet.*;

import java.util.*;
import java.text.*;
import java.lang.*;

public class DownloadFileForm extends FileForm {

   private boolean firsttime = true;
   
   public DownloadFileForm() {
   }
   
  // Main
   public static final String OwnerUser        = "FileOwnerUser";
   public static final String OwnerCompany     = "FileOwnerCompany";
   public static final String PackageName      = "PackageName";
   
  // Record
   public static final String DownloadTime     = "DownloadTime";
   
  // include field
   protected boolean owneruser         = true;
   protected boolean ownercompany      = true;
   protected boolean packagename       = true;
   protected boolean downloadtime      = true;
   
  // include filter
   protected boolean owneruserF        = false;
   protected boolean ownercompanyF     = false;
   protected boolean packagenameF      = false;
   protected boolean downloadtimeF     = true;
   
  // Filter Value
   protected String owneruserFV        = "";
   protected String ownercompanyFV     = "";
   protected String packagenameFV      = "";
   
  // Date processing filter values
   protected String downloadstartFV[] = new String[3];
   protected String downloadendFV[]   = new String[3];
   
  // Filter Modifier
   protected String downloadtimeFG    = "between";
   
  //
  // -- Setters and Getters
  //
   
   public boolean getOwnerUser() { return owneruser; }
   public void    setOwnerUser(boolean v) { owneruser = v; }
   
   public boolean getOwnerCompany() { return ownercompany; }
   public void    setOwnerCompany(boolean v) { ownercompany = v; }
   
   public boolean getPackageName() { return packagename; }
   public void    setPackageName(boolean v) { packagename = v; }
   
   public boolean getDownloadTime() { return downloadtime; }
   public void    setDownloadTime(boolean v) { downloadtime = v; }
   
   
  // include filter
  
   public boolean getOwnerUserFilter() { return owneruserF; }
   public void    setOwnerUserFilter(boolean v) { owneruserF = v; }
   
   public boolean getOwnerCompanyFilter() { return ownercompanyF; }
   public void    setOwnerCompanyFilter(boolean v) { ownercompanyF = v; }
   
   public boolean getPackageNameFilter() { return packagenameF; }
   public void    setPackageNameFilter(boolean v) { packagenameF= v; }
   
   public boolean getDownloadTimeFilter() { return downloadtimeF; }
   public void    setDownloadTimeFilter(boolean v) { downloadtimeF = v; }
   
  // include filter Values
   public String  getOwnerUserFilterValue() { return owneruserFV; }
   public void    setOwnerUserFilterValue(String v) { owneruserFV = v; }
   
   public String  getOwnerCompanyFilterValue() { return ownercompanyFV; }
   public void    setOwnerCompanyFilterValue(String v) { ownercompanyFV = v; }
   
   public String  getPackageNameFilterValue() { return packagenameFV; }
   public void    setPackageNameFilterValue(String  v) { packagenameFV = v; }
   
   public String  getDownloadFilterStartYear()        { return downloadstartFV[YEAR];}
   public void    setDownloadFilterStartYear(String v){ downloadstartFV[YEAR] = v;}
   public String  getDownloadFilterStartMonth()       { return downloadstartFV[MONTH];}
   public void    setDownloadFilterStartMonth(String v){ downloadstartFV[MONTH] = v;}
   public String  getDownloadFilterStartDay()          { return downloadstartFV[DAY];}
   public void    setDownloadFilterStartDay(String v)  { downloadstartFV[DAY] = v;}
   
   public String  getDownloadFilterEndYear()            { return downloadendFV[YEAR]; }
   public void    setDownloadFilterEndYear(String v)    { downloadendFV[YEAR] = v;    }
   public String  getDownloadFilterEndMonth()           { return downloadendFV[MONTH];}
   public void    setDownloadFilterEndMonth(String v  ) { downloadendFV[MONTH] = v;   }
   public String  getDownloadFilterEndDay()             { return downloadendFV[DAY];  }
   public void    setDownloadFilterEndDay(String v)     { downloadendFV[DAY] = v;     }
 
  // include filter modifiers
  
   public String  getDownloadTimeFilterModifier() { return downloadtimeFG; }
   public void    setDownloadTimeFilterModifier(String v) { downloadtimeFG = v; }
  
   public Date getDownloadTimeStartDate() {
      return getStartDate(downloadstartFV);
   }
   public Date getDownloadTimeEndDate() {
      return getEndDate(downloadendFV);
   }
  
  
   public boolean modifyField(String fieldname, String value) {
   
      boolean ret = super.modifyField(fieldname, value);
      if (ret) return ret;
      
      ret = true;
       
      if        (fieldname.equalsIgnoreCase(PackageName)) {
         setPackageNameFilter(true);
         setPackageNameFilterValue("\"" + value + "\"");
      } else if (fieldname.equalsIgnoreCase(OwnerUser)) {
         setOwnerUserFilter(true);
         setOwnerUserFilterValue("\"" + value + "\"");
      } else if (fieldname.equalsIgnoreCase(OwnerCompany)) {
         setOwnerCompanyFilter(true);
         setOwnerCompanyFilterValue("\"" + value + "\"");
      } else {
         ret = false;
      }
      return ret;
   }
   
   public void reset(boolean toDefault) {
   
      if (toDefault) firsttime = true;
      
      super.reset(toDefault);
      
      setOwnerUser(false);
      setOwnerCompany(false);
      setPackageName(false);
      setOwnerUserFilter(false);
      setOwnerCompanyFilter(false);
      setPackageNameFilter(false);
      setDownloadTime(false);
      setDownloadTimeFilter(false);
            
     // Set our starting values
      if (firsttime) {
         firsttime = false;
         
         setOwnerCompany(true);
         setOwnerUser(true);
         
         setDownloadTimeFilterModifier("between");
         
         setDownloadFilterStartYear(getStartYear());
         setDownloadFilterStartMonth(getStartMonth());
         setDownloadFilterStartDay(getStartDay());
         setDownloadFilterEndYear(getEndYear());
         setDownloadFilterEndMonth(getEndMonth());
         setDownloadFilterEndDay(getEndDay());
      }
   }
   
   public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
   
      ActionErrors ret = super.validate(mapping, request);
      
      String s = getOwnerUserFilterValue();
      if (getOwnerUserFilter() && (s == null || s.trim().length() == 0)) {
         ret.add("ownerUser", new ActionError("error.ownerUserfilter"));
      }
      
      s = getOwnerCompanyFilterValue();
      if (getOwnerCompanyFilter() && (s == null || s.trim().length() == 0)) {
         ret.add("ownerCompany", new ActionError("error.ownerCompanyfilter"));
      }
      
      s = getPackageNameFilterValue();
      if (getPackageNameFilter() && (s == null || s.trim().length() == 0)) {
         ret.add("packageName", new ActionError("error.dffpackageNamefilter"));
      }
      
      
      return ret;
   }
   
  // If a filter name has a text field set but NO gval, its considered simple
   public static boolean fieldHasSimpleFilter(String name) {
      boolean ret = FileForm.fieldHasSimpleFilter(name);
      if (!ret && (name.equals(PackageName)  || 
                   name.equals(OwnerUser)    ||
                   name.equals(OwnerCompany))) {
         ret = true;
      }
      return ret;
   }
}
