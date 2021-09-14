package oem.edge.ed.odc.webdropbox.reports;

import org.apache.struts.action.*;
import javax.servlet.http.*;
import javax.servlet.*;

import java.util.*;
import java.text.*;
import java.lang.*;

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

public class PackageForm extends BaseForm {

   private boolean firsttime = true;
   
   public PackageForm() {
      createstartFV[YEAR] = "";
      createstartFV[MONTH] = "";
      createstartFV[DAY] = "";
      commitstartFV[YEAR] = "";
      commitstartFV[MONTH] = "";
      commitstartFV[DAY] = "";
      deletestartFV[YEAR] = "";
      deletestartFV[MONTH] = "";
      deletestartFV[DAY] = "";
      
      createendFV  [YEAR] = "";
      createendFV  [MONTH] = "";
      createendFV  [DAY] = "";
      commitendFV  [YEAR] = "";
      commitendFV  [MONTH] = "";
      commitendFV  [DAY] = "";
      deleteendFV  [YEAR] = "";
      deleteendFV  [MONTH] = "";
      deleteendFV  [DAY] = "";
   }
   
  // Access summary filter value fields as longs
   public long getAvgPackageSizeAsLong() {
      return getFieldValueAsLong(AvgPackageSize, getAvgPackageSizeFilterValue());
   }
   public long getAvgNumFilesAsLong() {
      return getFieldValueAsLong(AvgNumFiles, getAvgNumFilesFilterValue());
   }
   public long getNumPackagesAsLong() {
      return getFieldValueAsLong(NumPackages, getNumPackagesFilterValue());
   }
   
   
   public static final String PKGID        = "PKGID";
   
   
  // Main
   public static final String PackageState   = "PackageState";
   public static final String PackageSize    = "PackageSize";
   public static final String NumFiles       = "NumFiles";
   public static final String ItarStatus     = "ItarStatus";
   
  // Summary
   public static final String NumPackages    = "NumPackages";
   public static final String AvgNumFiles    = "AvgNumFiles";
   public static final String AvgPackageSize = "AvgPackageSize";
   
  // Record
   public static final String PackageName    = "PackageName";
   public static final String CreateTime     = "CreateTime";
   public static final String CommitTime     = "CommitTime";
   public static final String DeleteTime     = "DeleteTime";
   
  // include field
   protected boolean packagestate    = true;
   protected boolean packagesize     = true;
   protected boolean numfiles        = true;
   
   protected boolean numpackages     = true;
   protected boolean avgnumfiles     = true;
   protected boolean avgpackagesize  = false;
   
   protected boolean packagename     = true;
   protected boolean itarstatus      = true;
   protected boolean createtime      = false;
   protected boolean committime      = true;
   protected boolean deletetime      = false;
   
  // include filter
   protected boolean packagestateF   = false;
   protected boolean packagesizeF    = false;
   protected boolean numfilesF       = false;
   
   protected boolean numpackagesF    = false;
   protected boolean avgnumfilesF    = false;
   protected boolean avgpackagesizeF = false;
   
   protected boolean packagenameF    = false;
   protected boolean createtimeF     = false;
   protected boolean committimeF     = false;
   protected boolean deletetimeF     = false;
   
  // Filter Value
   protected String packagestateFV   = "";
   protected String packagesizeFV    = "";
   protected String numfilesFV       = "";
   
   protected String numpackagesFV    = "";
   protected String avgnumfilesFV    = "";
   protected String avgpackagesizeFV = "";
   
   protected String packagenameFV    = "";
   
  // Date processing filter values
   protected String createstartFV[] = new String[3];
   protected String createendFV[]   = new String[3];
   protected String commitstartFV[] = new String[3];
   protected String commitendFV[]   = new String[3];
   protected String deletestartFV[] = new String[3];
   protected String deleteendFV[]   = new String[3];
   
  
  // Filter Modifier
   protected String packagestateFG   = "Ready";
   protected String packagesizeFG    = "ge";
   protected String numfilesFG       = "ge";
   protected String avgpackagesizeFG = "ge";
   protected String numpackagesFG    = "ge";
   protected String avgnumfilesFG    = "ge";
   protected String createtimeFG     = "between";
   protected String committimeFG     = "between";
   protected String deletetimeFG     = "between";
   
  //
  // -- Setters and Getters
  //
   
   public boolean getPackageState() { return packagestate; }
   public void    setPackageState(boolean v) { packagestate = v; }
   
   public boolean getPackageItarStatus()          { return itarstatus; }
   public void    setPackageItarStatus(boolean v) { itarstatus = v;    }
   
   public boolean getPackageSize() { return packagesize; }
   public void    setPackageSize(boolean v) { packagesize = v; }
   
   public boolean getNumFiles() { return numfiles; }
   public void    setNumFiles(boolean v) { numfiles = v; }
   
   public boolean getAvgNumFiles() { return avgnumfiles; }
   public void    setAvgNumFiles(boolean v) { avgnumfiles = v; }
   
   public boolean getNumPackages() { return numpackages; }
   public void    setNumPackages(boolean v) { numpackages = v; }
   
   public boolean getAvgPackageSize() { return avgpackagesize; }
   public void    setAvgPackageSize(boolean v) { avgpackagesize = v; }
   
   public boolean getPackageName() { return packagename; }
   public void    setPackageName(boolean v) { packagename = v; }
   
   public boolean getCreateTime() { return createtime; }
   public void    setCreateTime(boolean v) { createtime = v; }
   
   public boolean getCommitTime() { return committime; }
   public void    setCommitTime(boolean v) { committime = v; }
   
   public boolean getDeleteTime() { return deletetime; }
   public void    setDeleteTime(boolean v) { deletetime = v; }
   
  // include filter
   public boolean getPackageStateFilter() { return packagestateF; }
   public void    setPackageStateFilter(boolean v) { packagestateF = v; }
   
   public boolean getPackageSizeFilter() { return packagesizeF; }
   public void    setPackageSizeFilter(boolean v) { packagesizeF = v; }
   
   public boolean getNumFilesFilter() { return numfilesF; }
   public void    setNumFilesFilter(boolean v) { numfilesF = v; }
   
   public boolean getAvgNumFilesFilter() { return avgnumfilesF; }
   public void    setAvgNumFilesFilter(boolean v) { avgnumfilesF = v; }
   
   public boolean getNumPackagesFilter() { return numpackagesF; }
   public void    setNumPackagesFilter(boolean v) { numpackagesF = v; }
   
   public boolean getAvgPackageSizeFilter() { return avgpackagesizeF; }
   public void    setAvgPackageSizeFilter(boolean v) { avgpackagesizeF = v; }
   
   public boolean getPackageNameFilter() { return packagenameF; }
   public void    setPackageNameFilter(boolean v) { packagenameF = v; }
   
   public boolean getCreateTimeFilter() { return createtimeF; }
   public void    setCreateTimeFilter(boolean v) { createtimeF = v; }
   
   public boolean getCommitTimeFilter() { return committimeF; }
   public void    setCommitTimeFilter(boolean v) { committimeF = v; }
   
   public boolean getDeleteTimeFilter() { return deletetimeF; }
   public void    setDeleteTimeFilter(boolean v) { deletetimeF = v; }
   
  // include filter Values
   public String  getPackageStateFilterValue() { return packagestateFV; }
   public void    setPackageStateFilterValue(String v) { packagestateFV = v; }
   
   public String  getPackageSizeFilterValue() { return packagesizeFV; }
   public void    setPackageSizeFilterValue(String v) { packagesizeFV = v; }
   
   public String  getNumFilesFilterValue() { return numfilesFV; }
   public void    setNumFilesFilterValue(String v) { numfilesFV = v; }
   
   public String  getAvgNumFilesFilterValue() { return avgnumfilesFV; }
   public void    setAvgNumFilesFilterValue(String v) { avgnumfilesFV = v; }
   
   public String  getNumPackagesFilterValue() { return numpackagesFV; }
   public void    setNumPackagesFilterValue(String v) { numpackagesFV = v; }
   
   public String  getAvgPackageSizeFilterValue() { return avgpackagesizeFV; }
   public void    setAvgPackageSizeFilterValue(String v) { avgpackagesizeFV = v; }
   
   public String  getPackageNameFilterValue() { return packagenameFV; }
   public void    setPackageNameFilterValue(String v) { packagenameFV = v; }
   
   public String  getCreateFilterStartYear()          { return createstartFV[YEAR];  }
   public void    setCreateFilterStartYear(String v)  { createstartFV[YEAR] = v;     }
   public String  getCreateFilterStartMonth()         { return createstartFV[MONTH]; }
   public void    setCreateFilterStartMonth(String v) { createstartFV[MONTH] = v;    }
   public String  getCreateFilterStartDay()           { return createstartFV[DAY];   }
   public void    setCreateFilterStartDay(String v)   { createstartFV[DAY] = v;      }
   
   public String  getCreateFilterEndYear()            { return createendFV[YEAR];    }
   public void    setCreateFilterEndYear(String v)    { createendFV[YEAR] = v;       }
   public String  getCreateFilterEndMonth()           { return createendFV[MONTH];   }
   public void    setCreateFilterEndMonth(String v  ) { createendFV[MONTH] = v;      }
   public String  getCreateFilterEndDay()             { return createendFV[DAY];     }
   public void    setCreateFilterEndDay(String v)     { createendFV[DAY] = v;        }
   
   public String  getCommitFilterStartYear()          { return commitstartFV[YEAR];  }
   public void    setCommitFilterStartYear(String v)  { commitstartFV[YEAR] = v;     }
   public String  getCommitFilterStartMonth()         { return commitstartFV[MONTH]; }
   public void    setCommitFilterStartMonth(String v) { commitstartFV[MONTH] = v;    }
   public String  getCommitFilterStartDay()           { return commitstartFV[DAY];   }
   public void    setCommitFilterStartDay(String v)   { commitstartFV[DAY] = v;      }
   
   public String  getCommitFilterEndYear()            { return commitendFV[YEAR];    }
   public void    setCommitFilterEndYear(String v)    { commitendFV[YEAR] = v;       }
   public String  getCommitFilterEndMonth()           { return commitendFV[MONTH];   }
   public void    setCommitFilterEndMonth(String v  ) { commitendFV[MONTH] = v;      }
   public String  getCommitFilterEndDay()             { return commitendFV[DAY];     }
   public void    setCommitFilterEndDay(String v)     { commitendFV[DAY] = v;        }
   
   public String  getDeleteFilterStartYear()          { return deletestartFV[YEAR];  }
   public void    setDeleteFilterStartYear(String v)  { deletestartFV[YEAR] = v;     }
   public String  getDeleteFilterStartMonth()         { return deletestartFV[MONTH]; }
   public void    setDeleteFilterStartMonth(String v) { deletestartFV[MONTH] = v;    }
   public String  getDeleteFilterStartDay()           { return deletestartFV[DAY];   }
   public void    setDeleteFilterStartDay(String v)   { deletestartFV[DAY] = v;      }
   
   public String  getDeleteFilterEndYear()            { return deleteendFV[YEAR];    }
   public void    setDeleteFilterEndYear(String v)    { deleteendFV[YEAR] = v;       }
   public String  getDeleteFilterEndMonth()           { return deleteendFV[MONTH];   }
   public void    setDeleteFilterEndMonth(String v  ) { deleteendFV[MONTH] = v;      }
   public String  getDeleteFilterEndDay()             { return deleteendFV[DAY];     }
   public void    setDeleteFilterEndDay(String v)     { deleteendFV[DAY] = v;        }
   
  // include filter modifiers
   
   public String  getPackageStateFilterModifier() { return packagestateFG; }
   public void    setPackageStateFilterModifier(String v) { packagestateFG = v; }
   
   public String  getPackageSizeFilterModifier() { return packagesizeFG; }
   public void    setPackageSizeFilterModifier(String v) { packagesizeFG = v; }
   
   public String  getNumFilesFilterModifier() { return numfilesFG; }
   public void    setNumFilesFilterModifier(String v) { numfilesFG = v; }
         
   public String  getAvgPackageSizeFilterModifier() { return avgpackagesizeFG; }
   public void    setAvgPackageSizeFilterModifier(String v) { avgpackagesizeFG = v; }
   
   public String  getAvgNumFilesFilterModifier() { return avgnumfilesFG; }
   public void    setAvgNumFilesFilterModifier(String v) { avgnumfilesFG = v; }
         
   public String  getNumPackagesFilterModifier() { return numpackagesFG; }
   public void    setNumPackagesFilterModifier(String v) { numpackagesFG = v; }
         
   public String  getCreateTimeFilterModifier() { return createtimeFG; }
   public void    setCreateTimeFilterModifier(String v) { createtimeFG = v; }
         
   public String  getCommitTimeFilterModifier() { return committimeFG; }
   public void    setCommitTimeFilterModifier(String v) { committimeFG = v; }

   public String  getDeleteTimeFilterModifier() { return deletetimeFG; }
   public void    setDeleteTimeFilterModifier(String v) { deletetimeFG = v; }
   
   public Date getCreateTimeStartDate() {
      return getStartDate(createstartFV);
   }
   public Date getCreateTimeEndDate() {
      return getEndDate(createendFV);
   }
   
   public Date getCommitTimeStartDate() {
      return getStartDate(commitstartFV);
   }
   public Date getCommitTimeEndDate() {
      return getEndDate(commitendFV);
   }
   
   public Date getDeleteTimeStartDate() {
      return getStartDate(deletestartFV);
   }
   public Date getDeleteTimeEndDate() {
      return getEndDate(deleteendFV);
   }
   
   public boolean modifyField(String fieldname, String value) {
   
      boolean ret = super.modifyField(fieldname, value);
      if (ret) return ret;
      
      ret = true;
      if        (fieldname.equalsIgnoreCase(PackageState)) {
         setPackageStateFilter(true);
         setPackageStateFilterModifier(value);
      } else if (fieldname.equalsIgnoreCase(PackageName)) {
         setPackageNameFilter(true);
         setPackageNameFilterValue("\"" + value + "\"");
      } else {
         ret = false;
      }
      return ret;
   }
   
   public void reset(boolean toDefault) {
   
      if (toDefault) firsttime = true;
   
      super.reset(toDefault);
      
      setPackageState(false);
      setPackageSize(false);
      setPackageItarStatus(false);
      setNumFiles(false);
      setAvgNumFiles(false);
      setNumPackages(false);
      setAvgPackageSize(false);
      setPackageName(false);
      setCreateTime(false);
      setCommitTime(false);
      setDeleteTime(false);
      setPackageStateFilter(false);
      setPackageSizeFilter(false);
      setNumFilesFilter(false);
      setAvgNumFilesFilter(false);
      setNumPackagesFilter(false);
      setAvgPackageSizeFilter(false);
      setPackageNameFilter(false);
      setCreateTimeFilter(false);
      setCommitTimeFilter(false);
      setDeleteTimeFilter(false);
            
     // Set our starting values
      if (firsttime) {
         firsttime = false;
         setPackageState(true);
         setPackageItarStatus(true);
         setPackageSize(true);
         setNumFiles(true);
         setAvgNumFiles(true);
         setNumPackages(true);
         setAvgPackageSize(true);
         setPackageName(true);
         setCommitTime(true);
         
         setPackageSizeFilterValue("1000");
         setNumFilesFilterValue("2");
         setAvgNumFilesFilterValue("2");
         setNumPackagesFilterValue("10");
         setAvgPackageSizeFilterValue("1000");
         setPackageNameFilterValue("");
         setPackageStateFilterValue("Completed");
         
         setPackageStateFilterModifier("Completed");
         setPackageSizeFilterModifier("ge");
         setNumFilesFilterModifier("ge");
         setAvgPackageSizeFilterModifier("ge");
         setAvgNumFilesFilterModifier("ge");
         setNumPackagesFilterModifier("ge");
         setCreateTimeFilterModifier("between");
         setCommitTimeFilterModifier("between");
         setDeleteTimeFilterModifier("between");
         
         setCreateFilterStartYear(getStartYear());
         setCreateFilterStartMonth(getStartMonth());
         setCreateFilterStartDay(getStartDay());
         setCreateFilterEndYear(getEndYear());
         setCreateFilterEndMonth(getEndMonth());
         setCreateFilterEndDay(getEndDay());
         setCommitFilterStartYear(getStartYear());
         setCommitFilterStartMonth(getStartMonth());
         setCommitFilterStartDay(getStartDay());
         setCommitFilterEndYear(getEndYear());
         setCommitFilterEndMonth(getEndMonth());
         setCommitFilterEndDay(getEndDay());
         setDeleteFilterStartYear(getStartYear());
         setDeleteFilterStartMonth(getStartMonth());
         setDeleteFilterStartDay(getStartDay());
         setDeleteFilterEndYear(getEndYear());
         setDeleteFilterEndMonth(getEndMonth());
         setDeleteFilterEndDay(getEndDay());
      }
   }
   
   public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
   
      ActionErrors ret = super.validate(mapping, request);
      
      String s = getPackageStateFilterValue();
      if (getPackageStateFilter() && (s == null || s.trim().length() == 0)) {
         ret.add("packageState", new ActionError("error.packagestatefilter"));
      }
      s = getPackageNameFilterValue();
      if (getPackageNameFilter() && (s == null || s.trim().length() == 0)) {
         ret.add("packageName", new ActionError("error.packagenamefilter"));
      }
      
      s = getPackageSizeFilterValue();
      if (getPackageSizeFilter()) {
         try {
            Long.parseLong(s);
         } catch(Exception e) {
            ret.add("packageSize", new ActionError("error.packagesizefilter"));
         }
      }
      
      s = getNumFilesFilterValue();
      if (getNumFilesFilter()) {
         try {
            Long.parseLong(s);
         } catch(Exception e) {
            ret.add("numFiles", new ActionError("error.numfilesfilter"));
         }
      }
      
      s = getAvgNumFilesFilterValue();
      if (getAvgNumFilesFilter()) {
         try {
            Long.parseLong(s);
         } catch(Exception e) {
            ret.add("avgNumFiles", new ActionError("error.avgnumfilesfilter"));
         }
      }
      
      s = getNumPackagesFilterValue();
      if (getNumPackagesFilter()) {
         try {
            Long.parseLong(s);
         } catch(Exception e) {
            ret.add("numPackages", new ActionError("error.numpackagesfilter"));
         }
      }
      
      s = getAvgPackageSizeFilterValue();
      if (getAvgPackageSizeFilter()) {
         try {
            Long.parseLong(s);
         } catch(Exception e) {
            ret.add("avgPackageSize", new ActionError("error.avgpackagesizefilter"));
         }
      }
      
      return ret;
   }
   
  // If a filter name has a text field set but NO gval, its considered simple
   public static boolean fieldHasSimpleFilter(String name) {
      boolean ret = BaseForm.fieldHasSimpleFilter(name);
      if (!ret && (name.equals(PackageName) || name.equals(PackageState))) {
         ret = true;
      }
      return ret;
   }
}
