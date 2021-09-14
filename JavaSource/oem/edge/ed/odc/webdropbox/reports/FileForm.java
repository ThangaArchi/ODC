package oem.edge.ed.odc.webdropbox.reports;

import org.apache.struts.action.*;
import javax.servlet.http.*;
import javax.servlet.*;

import java.util.*;
import java.text.*;
import java.lang.*;

public class FileForm extends BaseForm {

   private boolean firsttime = true;
   
   public FileForm() {
      createstartFV[YEAR] = "";
      createstartFV[MONTH] = "";
      createstartFV[DAY] = "";
      deletestartFV[YEAR] = "";
      deletestartFV[MONTH] = "";
      deletestartFV[DAY] = "";
      
      createendFV  [YEAR] = "";
      createendFV  [MONTH] = "";
      createendFV  [DAY] = "";
      deleteendFV  [YEAR] = "";
      deleteendFV  [MONTH] = "";
      deleteendFV  [DAY] = "";
   }
   
  // Access summary filter value fields as longs
   public long getAvgFileSizeAsLong() {
      return getFieldValueAsLong(AvgFileSize, getAvgFileSizeFilterValue());
   }
   public long getAvgNumComponentsAsLong() {
      return getFieldValueAsLong(AvgNumComponents, getAvgNumComponentsFilterValue());
   }
   public long getAvgXferRateAsLong() {
      return getFieldValueAsLong(AvgXferRate, getAvgXferRateFilterValue());
   }
   public long getXferRateAsLong() {
      return getFieldValueAsLong(XferRate, getXferRateFilterValue());
   }
   public long getNumFilesAsLong() {
      return getFieldValueAsLong(NumFiles, getNumFilesFilterValue());
   }
   
   public static final String FILEID        = "FILEID";
   
  // Main
   public static final String FileState        = "FileState";
   public static final String FileSize         = "FileSize";
   public static final String NumComponents    = "NumComponents";
   public static final String XferRate         = "XferRate";
   
  // Summary
   public static final String NumFiles         = "NumFiles";
   public static final String AvgNumComponents = "AvgNumComponents";
   public static final String AvgFileSize      = "AvgFileSize";
   public static final String AvgXferRate      = "AvgXferRate";
   
  // Record
   public static final String FileName         = "FileName";
   public static final String CreateTime       = "CreateTime";
   public static final String DeleteTime       = "DeleteTime";
   
  // include field
   protected boolean filestate         = true;
   protected boolean filesize          = true;
   protected boolean numcomponents     = true;
   protected boolean xferrate          = true;

   protected boolean numfiles          = true;
   protected boolean avgnumcomponents  = true;
   protected boolean avgfilesize       = false;
   protected boolean avgxferrate       = true;

   protected boolean filename          = true;
   protected boolean createtime        = false;
   protected boolean deletetime        = false;
   
  // include filter
   protected boolean filestateF        = false;
   protected boolean filesizeF         = false;
   protected boolean numcomponentsF    = false;
   protected boolean xferrateF         = true;
   
   protected boolean numfilesF         = false;
   protected boolean avgnumcomponentsF = false;
   protected boolean avgfilesizeF      = false;
   protected boolean avgxferrateF      = true;
   
   protected boolean filenameF         = false;
   protected boolean createtimeF       = false;
   protected boolean deletetimeF       = false;
   
  // Filter Value
   protected String filestateFV        = "";
   protected String filesizeFV         = "";
   protected String numcomponentsFV    = "";
   protected String xferrateFV         = "";
   
   protected String numfilesFV         = "";
   protected String avgnumcomponentsFV = "";
   protected String avgfilesizeFV      = "";
   protected String avgxferrateFV      = "";
   
   protected String filenameFV         = "";
   
  // Date processing filter values
   protected String createstartFV[] = new String[3];
   protected String createendFV[]   = new String[3];
   protected String deletestartFV[] = new String[3];
   protected String deleteendFV[]   = new String[3];
   
  // Filter Modifier
   protected String filestateFG        = "Ready";
   protected String filesizeFG         = "ge";
   protected String numfilesFG         = "ge";
   protected String numcomponentsFG    = "ge";
   protected String xferrateFG         = "ge";
   protected String avgfilesizeFG      = "ge";
   protected String avgnumcomponentsFG = "ge";
   protected String avgxferrateFG      = "ge";
   protected String createtimeFG       = "between";
   protected String deletetimeFG       = "between";
   
  //
  // -- Setters and Getters
  //
   
   public boolean getFileState() { return filestate; }
   public void    setFileState(boolean v) { filestate = v; }
   
   public boolean getFileSize() { return filesize; }
   public void    setFileSize(boolean v) { filesize = v; }
   
   public boolean getNumFiles() { return numfiles; }
   public void    setNumFiles(boolean v) { numfiles = v; }
   
   public boolean getNumComponents() { return numcomponents; }
   public void    setNumComponents(boolean v) { numcomponents = v; }
   
   public boolean getXferRate() { return xferrate; }
   public void    setXferRate(boolean v) { xferrate = v; }
   
   public boolean getAvgNumComponents() { return avgnumcomponents; }
   public void    setAvgNumComponents(boolean v) { avgnumcomponents = v; }
   
   public boolean getAvgFileSize() { return avgfilesize; }
   public void    setAvgFileSize(boolean v) { avgfilesize = v; }
   
   public boolean getAvgXferRate() { return avgxferrate; }
   public void    setAvgXferRate(boolean v) { avgxferrate = v; }
   
   public boolean getFileName() { return filename; }
   public void    setFileName(boolean v) { filename = v; }
   
   public boolean getCreateTime() { return createtime; }
   public void    setCreateTime(boolean v) { createtime = v; }
   
   public boolean getDeleteTime() { return deletetime; }
   public void    setDeleteTime(boolean v) { deletetime = v; }
   
  // include filter
   public boolean getFileStateFilter() { return filestateF; }
   public void    setFileStateFilter(boolean v) { filestateF = v; }
   
   public boolean getFileSizeFilter() { return filesizeF; }
   public void    setFileSizeFilter(boolean v) { filesizeF = v; }
   
   public boolean getNumFilesFilter() { return numfilesF; }
   public void    setNumFilesFilter(boolean v) { numfilesF = v; }
   
   public boolean getXferRateFilter() { return xferrateF; }
   public void    setXferRateFilter(boolean v) { xferrateF= v; }
   
   public boolean getNumComponentsFilter() { return numcomponentsF; }
   public void    setNumComponentsFilter(boolean v) { numcomponentsF = v; }
   
   public boolean getAvgNumComponentsFilter() { return avgnumcomponentsF; }
   public void    setAvgNumComponentsFilter(boolean v) { avgnumcomponentsF = v; }
   
   public boolean getAvgFileSizeFilter() { return avgfilesizeF; }
   public void    setAvgFileSizeFilter(boolean v) { avgfilesizeF = v; }
   
   public boolean getAvgXferRateFilter() { return avgxferrateF; }
   public void    setAvgXferRateFilter(boolean v) { avgxferrateF= v; }
   
   public boolean getFileNameFilter() { return filenameF; }
   public void    setFileNameFilter(boolean v) { filenameF = v; }
   
   public boolean getCreateTimeFilter() { return createtimeF; }
   public void    setCreateTimeFilter(boolean v) { createtimeF = v; }
   
   public boolean getDeleteTimeFilter() { return deletetimeF; }
   public void    setDeleteTimeFilter(boolean v) { deletetimeF = v; }
   
  // include filter Values
   public String  getFileStateFilterValue() { return filestateFV; }
   public void    setFileStateFilterValue(String v) { filestateFV = v; }
   
   public String  getFileSizeFilterValue() { return filesizeFV; }
   public void    setFileSizeFilterValue(String v) { filesizeFV = v; }
   
   public String  getNumFilesFilterValue() { return numfilesFV; }
   public void    setNumFilesFilterValue(String v) { numfilesFV = v; }
   
   public String  getNumComponentsFilterValue() { return numcomponentsFV; }
   public void    setNumComponentsFilterValue(String v) { numcomponentsFV = v; }
   
   public String  getXferRateFilterValue() { return xferrateFV; }
   public void    setXferRateFilterValue(String  v) { xferrateFV = v; }
   
   public String  getAvgNumComponentsFilterValue() { return avgnumcomponentsFV; }
   public void    setAvgNumComponentsFilterValue(String v) { avgnumcomponentsFV = v; }
   
   public String  getAvgFileSizeFilterValue() { return avgfilesizeFV; }
   public void    setAvgFileSizeFilterValue(String v) { avgfilesizeFV = v; }
   
   public String  getAvgXferRateFilterValue() { return avgxferrateFV; }
   public void    setAvgXferRateFilterValue(String  v) { avgxferrateFV = v; }
   
   public String  getFileNameFilterValue() { return filenameFV; }
   public void    setFileNameFilterValue(String v) { filenameFV = v; }
   
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
   
   public String  getFileStateFilterModifier() { return filestateFG; }
   public void    setFileStateFilterModifier(String v) { filestateFG = v; }
   
   public String  getFileSizeFilterModifier() { return filesizeFG; }
   public void    setFileSizeFilterModifier(String v) { filesizeFG = v; }
   
   public String  getNumFilesFilterModifier() { return numfilesFG; }
   public void    setNumFilesFilterModifier(String v) { numfilesFG = v; }
         
   public String  getXferRateFilterModifier() { return xferrateFG; }
   public void    setXferRateFilterModifier(String v) { xferrateFG = v; }
         
   public String  getAvgFileSizeFilterModifier() { return avgfilesizeFG; }
   public void    setAvgFileSizeFilterModifier(String v) { avgfilesizeFG = v; }
   
   public String  getNumComponentsFilterModifier() { return numcomponentsFG; }
   public void    setNumComponentsFilterModifier(String v) { numcomponentsFG = v; }
   
   public String  getAvgNumComponentsFilterModifier() { return avgnumcomponentsFG; }
   public void    setAvgNumComponentsFilterModifier(String v) { avgnumcomponentsFG = v; }
         
   public String  getAvgXferRateFilterModifier() { return avgxferrateFG; }
   public void    setAvgXferRateFilterModifier(String v) { avgxferrateFG = v; }
   
   public String  getCreateTimeFilterModifier() { return createtimeFG; }
   public void    setCreateTimeFilterModifier(String v) { createtimeFG = v; }
         
   public String  getDeleteTimeFilterModifier() { return deletetimeFG; }
   public void    setDeleteTimeFilterModifier(String v) { deletetimeFG = v; }
   
   public Date getCreateTimeStartDate() {
      return getStartDate(createstartFV);
   }
   public Date getCreateTimeEndDate() {
      return getEndDate(createendFV);
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
      if        (fieldname.equalsIgnoreCase(FileState)) {
         setFileStateFilter(true);
         setFileStateFilterModifier(value);
      } else if (fieldname.equalsIgnoreCase(FileName)) {
         setFileNameFilter(true);
         setFileNameFilterValue("\"" + value + "\"");
      } else {
         ret = false;
      }
      return ret;
   }
   
   
   public void reset(boolean toDefault) {
   
      if (toDefault) firsttime = true;
   
      super.reset(toDefault);
      
      setFileState(false);
      setFileSize(false);
      setNumFiles(false);
      setNumComponents(false);
      setXferRate(false);
      setAvgNumComponents(false);
      setAvgFileSize(false);
      setAvgXferRate(false);
      setFileName(false);
      setCreateTime(false);
      setDeleteTime(false);
      setFileStateFilter(false);
      setFileSizeFilter(false);
      setNumFilesFilter(false);
      setNumComponentsFilter(false);
      setXferRateFilter(false);
      setAvgNumComponentsFilter(false);
      setAvgFileSizeFilter(false);
      setAvgXferRateFilter(false);
      setFileNameFilter(false);
      setCreateTimeFilter(false);
      setDeleteTimeFilter(false);
            
     // Set our starting values
      if (firsttime) {
         firsttime = false;
         setFileState(true);
         setFileSize(true);
         setNumFiles(true);
         setXferRate(true);
         setAvgXferRate(true);
         setAvgFileSize(true);
         setFileName(true);
         
         
         setFileSizeFilterValue("1000");
         setNumFilesFilterValue("2");
         setNumComponentsFilterValue("2");
         setXferRateFilterValue("100");
         setAvgNumComponentsFilterValue("2");
         setAvgFileSizeFilterValue("1000");
         setAvgXferRateFilterValue("100");
         setFileNameFilterValue("");
         setFileStateFilterValue("Completed");
         
         setFileStateFilterModifier("Completed");
         setFileSizeFilterModifier("ge");
         setNumFilesFilterModifier("ge");
         setAvgFileSizeFilterModifier("ge");
         setNumFilesFilterModifier("ge");
         setAvgXferRateFilterModifier("ge");
         setCreateTimeFilterModifier("between");
         setDeleteTimeFilterModifier("between");
         
         setCreateFilterStartYear(getStartYear());
         setCreateFilterStartMonth(getStartMonth());
         setCreateFilterStartDay(getStartDay());
         setCreateFilterEndYear(getEndYear());
         setCreateFilterEndMonth(getEndMonth());
         setCreateFilterEndDay(getEndDay());
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
      
      String s = getFileStateFilterValue();
      if (getFileStateFilter() && (s == null || s.trim().length() == 0)) {
         ret.add("fileState", new ActionError("error.filestatefilter"));
      }
      
      s = getFileSizeFilterValue();
      if (getFileSizeFilter()) {
         try { 
            Long.parseLong(s);
         } catch(Exception e) {
            ret.add("fileSize", new ActionError("error.filesizefilter"));
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
      
      s = getAvgFileSizeFilterValue();
      if (getAvgFileSizeFilter()) {
         try {
            Long.parseLong(s);
         } catch(Exception e) {
            ret.add("avgFileSize", new ActionError("error.avgfilesizefilter"));
         }
      }
      
      s = getFileNameFilterValue();
      if (getFileNameFilter() && (s == null || s.trim().length() == 0)) {
         ret.add("fileName", new ActionError("error.filenamefilter"));
      }
      
      s = getNumComponentsFilterValue();
      if (getNumComponentsFilter()) {
         try {
            Long.parseLong(s);
         } catch(Exception e) {
            ret.add("numComponents", new ActionError("error.numcomponentsfilter"));
         }
      }
      
      s = getAvgNumComponentsFilterValue();
      if (getAvgNumComponentsFilter()) {
         try {
            Long.parseLong(s);
         } catch(Exception e) {
            ret.add("avgNumComponents", new ActionError("error.avgnumcomponentsfilter"));
         }
      }
      
      s = getXferRateFilterValue();
      if (getXferRateFilter()) {
         try {
            Long.parseLong(s);
         } catch(Exception e) {
            ret.add("xferRate", new ActionError("error.xferRatefilter"));
         }
      }
      
      s = getAvgXferRateFilterValue();
      if (getAvgXferRateFilter()) {
         try {
            Long.parseLong(s);
         } catch(Exception e) {
            ret.add("avgXferRate", new ActionError("error.avgXferRatefilter"));
         }
      }
      
      return ret;
   }
   
  // If a filter name has a text field set but NO gval, its considered simple
   public static boolean fieldHasSimpleFilter(String name) {
      boolean ret = BaseForm.fieldHasSimpleFilter(name);
      if (!ret && (name.equals(FileName)     || 
                   name.equals(FileState))) {
         ret = true;
      }
      return ret;
   }
}
